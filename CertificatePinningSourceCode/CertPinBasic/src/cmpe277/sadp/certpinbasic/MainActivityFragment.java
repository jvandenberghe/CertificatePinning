package cmpe277.sadp.certpinbasic;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import cmpe277.sun.security.tools.borrowed.Encoding;
import android.app.Fragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.BounceInterpolator;
import android.util.Base64;
import android.util.Log;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;

import 	android.util.Base64;
import android.webkit.URLUtil;
import android.os.AsyncTask;
import android.provider.CalendarContract.Colors;
import co.infinum.https.*;
import de.greenrobot.event.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.lang.InterruptedException;
import java.net.URI;
import java.net.URISyntaxException;










import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

	static final String BOUNCY_CASTLE = "BKS";
	private static final char[] STORE_PASS = new char[] { 's', 'a', 'd', 'p',
			's', 'e', 'c', 'r', 'e', 't' };
	@Override
	public void onStart() {
		super.onStart();
		EventBus.getDefault().register(this);
	}

	@Override
	public void onStop() {
		EventBus.getDefault().unregister(this);
		super.onStop();
	}

	private View rootView;
	private static final String MAIN_FRAG_TAG = "MainFragment";

	private Button testButton;

	private RadioGroup certGroup;
	private TextView appCert;
	private TextView rxCert;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_cert_pin_main, container,
				false);

		// Set on Click Listener
		testButton = (Button) rootView.findViewById(R.id.btn_test_pin);
		testButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(MAIN_FRAG_TAG, "Button Clicked, test pin v.getId() = "
						+ v.getId());
				CertPinTestRequest request = formRequest();
				if (request != null) {
					EventBus.getDefault().post(request);
				}
				
				Resources resources = getActivity().getResources();
				
				int rawId = -1;
				int checkedButtonId = certGroup.getCheckedRadioButtonId();
				
				if(checkedButtonId == R.id.opt_use_valid) {
					rawId = R.raw.sadp_keystore;
				} else if(checkedButtonId == R.id.opt_use_invalid) {
					rawId = R.raw.old_sadp_keystore;
				}
				InputStream rawStream = resources.openRawResource(rawId);
				
				try {
					KeyStore keystore = KeyStore.getInstance(BOUNCY_CASTLE);
					keystore.load(rawStream, STORE_PASS);
					List<String> aliasList = Collections.list(keystore.aliases());
					String certStr = "";
					for (String key : aliasList) {
						certStr += Encoding.getCertFingerPrint("SHA1", keystore.getCertificate(key));
					}
					appCert.setText(certStr);
				} catch (Exception e) {
					EventBus.getDefault().post(e);
				}

			}
		});

		// Get Certificates View Handlers
		appCert = (TextView) rootView.findViewById(R.id.main_current_cert);
		rxCert = (TextView) rootView.findViewById(R.id.main_received_cert);

		certGroup = (RadioGroup) rootView.findViewById(R.id.opt_key_valid);

		return rootView;
	}

	public void onEventMainThread(HttpResponse httpResponse) {
		StatusLine status = httpResponse.getStatusLine();

		if (status.getStatusCode() == HttpStatus.SC_OK) {
			Toast.makeText(getActivity(), "Success: " + status.toString(),
					Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(getActivity(), "Error: " + status.toString(),
					Toast.LENGTH_LONG).show();
		}

	}

	


	public void onEventMainThread(Exception e) {
		Toast.makeText(getActivity(), "Error: " + e.getMessage(),
				Toast.LENGTH_LONG).show();
	}

	public void onEventAsync(CertPinTestRequest testRequest) {

		try {

			int rawResId = testRequest.isUseOldCert() ? R.raw.old_sadp_keystore
					: R.raw.sadp_keystore;
			DefaultHttpClient httpClient = new HttpClientBuilder()
					.setConnectionTimeout(10000).setSocketTimeout(60000)
					.setHttpPort(80).setHttpsPort(443)
					.setCookieStore(new BasicCookieStore())
					.pinCertificates(getResources(), rawResId, STORE_PASS)
					.build();

			HttpGet request = new HttpGet(testRequest.getWebRequestUri());
			HttpResponse response = httpClient.execute(request);
			EventBus.getDefault().post(response);
		} catch (Exception e) {
			EventBus.getDefault().post(e);
		} 
		
		String value = retrieveSSLCert(getActivity().getResources().getString(R.string.Man_Cert_Host_Plain));
		EventBus.getDefault().post(value);
	}
	
	public void onEventMainThread(String value) {
		rxCert.setText(value);
		String appCertStr = appCert.getText().toString();
		if(appCertStr.equals(value)) {
			rxCert.setBackgroundColor(getActivity().getResources().getColor(R.color.green));
		} else {
			rxCert.setBackgroundColor(getActivity().getResources().getColor(R.color.red));
		}
	}

	private CertPinTestRequest formRequest() {
		CertPinTestRequest request = null;

		try {
			URI requestUri = null;
			requestUri = new URI(getResources().getString(R.string.API_Web_Cert));

			boolean useOld = false;
			if (certGroup.getCheckedRadioButtonId() == R.id.opt_use_invalid) {
				useOld = true;
			}

			return new CertPinTestRequest(useOld, requestUri);

		} catch (URISyntaxException e) {
		}
		return request;
	}

	private class CertPinTestRequest {

		private boolean useOldCert;
		private URI webRequestUri;

		public CertPinTestRequest() {

		}

		public CertPinTestRequest(boolean useOld, URI uriRequest) {
			this.useOldCert = useOld;
			this.webRequestUri = uriRequest;
		}

		public boolean isUseOldCert() {
			return useOldCert;
		}

		public URI getWebRequestUri() {
			return webRequestUri;
		}

	}
	


	// Based off code found here!
	// http://www.xinotes.net/notes/note/1088/
	private String retrieveSSLCert(String host) {
	
		TrustManager trm = new X509TrustManager() {
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
			
			public void checkClientTrusted(X509Certificate[] certs, String authType) {
			}
			
			public void checkServerTrusted(X509Certificate[] cets, String authType) {
			}
		};
		try {
		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, new TrustManager[] { trm }, null);
		SSLSocketFactory factory = sc.getSocketFactory();
		SSLSocket socket = (SSLSocket) factory.createSocket(host, 443);
		socket.startHandshake();
		SSLSession session = socket.getSession();
		java.security.cert.Certificate[] servercerts = session.getPeerCertificates();
		ArrayList<String> certStrs = new ArrayList<String>();
		for (int i = 0; i < servercerts.length; i++) {
			
			
			certStrs.add(Encoding.getCertFingerPrint("SHA1", servercerts[i]));
			
			
		}
		
		socket.close();
		return certStrs.get(0);
		} catch(Exception e) {
			EventBus.getDefault().post(e);
		}
		
		
		return null;
	}
}
