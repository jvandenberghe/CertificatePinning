package cmpe277.sadp.certpinbasic;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.params.BasicHttpParams;

import android.content.res.Resources;
import co.infinum.https.HttpClientBuilder;
import android.os.AsyncTask;

import org.apache.http.client.ClientProtocolException;
// HTTP
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.HttpResponse;

import android.net.TrafficStats;
import android.net.Uri;
import de.greenrobot.event.EventBus;

// Passed from Fragment
import android.content.res.Resources;
import android.widget.Toast;


// Exceptions
import java.io.IOException;

import android.util.Log;

public class SadpWebRestful {

	private final static String TAG = "RESTful Class";

	public enum REQUEST_TYPE {
		CREATE, TOKEN, ADD, UPDATE, LIST, PIN
	}

	// Password used for bks file
	private static final char[] STORE_PASS = new char[] { 's', 'a', 'd', 'p',
			's', 'e', 'c', 'r', 'e', 't' };

	public SadpWebRestful() {
		EventBus.getDefault().register(this);
	}

	public void unregisterEventBus() {
		EventBus.getDefault().unregister(this);
	}

	public void onEventAsync(WebRequest webRequest) {
		TrafficStats.setThreadStatsTag(0xF00D);

		switch (webRequest.getRequest()) {
		case CREATE:
			createUser(webRequest);
			break;
		case TOKEN:
			getToken(webRequest);
			break;
		case ADD:
			addMember(webRequest);
			break;
		case UPDATE:
			updateMember(webRequest);
			break;
		case LIST:
			getMemberList(webRequest);
		case PIN:
			testPinnedCert(webRequest);
			break;

		}
		// return response;

	}

	private void testPinnedCert(WebRequest webRequest) {
		Resources resources = webRequest.getContext().getResources();
		DefaultHttpClient httpClient = makeHttpClient(resources);
		HttpGet httpRequest = new HttpGet();
		try {
			URI testPin = new URI(resources.getString(R.string.API_Web_Cert));
			
			HttpResponse httpResponse = httpClient.execute(httpRequest);
			
			EventBus.getDefault().post(httpResponse);
			
			
		} catch (URISyntaxException e) {
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
			EventBus.getDefault().post(e);
		}
		
	}

	private void getMemberList(WebRequest webRequest) {
		Resources resources = webRequest.getContext().getResources();
		DefaultHttpClient httpClient = makeHttpClient(resources);
		HttpGet httpRequest = new HttpGet();
		
		try {
			URI requestAllUri = new URI(resources.getString(R.string.API_Web_Members));
			httpRequest.setURI(requestAllUri);
			
			httpRequest.setHeader(
					resources.getString(R.string.API_Web_Aut_Header),
					resources.getString(R.string.ApI_Web_Auth_Bearer)
							+ " " + webRequest.getToken());
			
			HttpResponse response = httpClient.execute(httpRequest);
			EventBus.getDefault().post(response);
			
		} catch (URISyntaxException e) {
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
			EventBus.getDefault().post(e);
		}
		
	}

	private void updateMember(WebRequest webRequest) {
		// Create Reference to Resources
				Resources resources = webRequest.getContext().getResources();
				// HttpClient with Pinning
				DefaultHttpClient httpClient = makeHttpClient(resources);
				// Create HttpPut for updating a Member in the Database
				HttpPut httpRequest = new HttpPut();

				try {
					
					// Update Member Method requires a valid MemberId in the URI
					URI updateUri = new URI(resources
							.getString(R.string.API_Web_Members) + "(" + webRequest.getMemberId() + ")");
					
					// Set URI
					httpRequest.setURI(updateUri);
					// Set Authorization
					// HACK Added space between the header
					httpRequest.setHeader(
							resources.getString(R.string.API_Web_Aut_Header),
							resources.getString(R.string.ApI_Web_Auth_Bearer)
									+ " " + webRequest.getToken());
					// Set Content-Type
					httpRequest.setHeader("Content-type",
							resources.getString(R.string.API_WebContent_Type_Json));
					// Set charset
					httpRequest.setHeader("charset",
							resources.getString(R.string.API_Web_Charset));
					// Set Body for Request
					httpRequest.setEntity(webRequest.getJsonMember(REQUEST_TYPE.UPDATE));

					// Send Request
					HttpResponse httpResponse = httpClient.execute(httpRequest);
					EventBus.getDefault().post(httpResponse);

				} catch (URISyntaxException e) {
				} catch (ClientProtocolException e) {
				} catch (IOException e) {
					EventBus.getDefault().post(e);
				}

		
	}

	private void addMember(WebRequest webRequest) {
		// Create Reference to Resources
		Resources resources = webRequest.getContext().getResources();
		// HttpClient with Pinning
		DefaultHttpClient httpClient = makeHttpClient(resources);
		// Create HttpPost for Adding Member to Database
		HttpPost httpRequest = new HttpPost();

		try {
			// Set URI
			httpRequest.setURI(new URI(resources
					.getString(R.string.API_Web_Members)));
			// Set Authorization
			// HACK Added space between the header
			httpRequest.setHeader(
					resources.getString(R.string.API_Web_Aut_Header),
					resources.getString(R.string.ApI_Web_Auth_Bearer)
							+ " " + webRequest.getToken());
			// Set Content-Type
			httpRequest.setHeader("Content-type",
					resources.getString(R.string.API_WebContent_Type_Json));
			// Set charset
			httpRequest.setHeader("charset",
					resources.getString(R.string.API_Web_Charset));
			// Set Body for Request
			httpRequest.setEntity(webRequest.getJsonMember(REQUEST_TYPE.ADD));

			// Send Request
			HttpResponse httpResponse = httpClient.execute(httpRequest);
			EventBus.getDefault().post(httpResponse);

		} catch (URISyntaxException e) {
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		}

	}

	private void getToken(WebRequest webRequest) {
		// HttpClient with Pinning
		Resources resources = webRequest.getContext().getResources();
		DefaultHttpClient httpClient = makeHttpClient(resources);
		// Create HttpPost Getting Token
		HttpPost httpRequest = new HttpPost();

		try {
			// Set URI
			httpRequest.setURI(new URI(resources
					.getString(R.string.API_Web_Token)));
			// Set Content-Type
			httpRequest.setHeader("Content-type",
					resources.getString(R.string.API_Web_Content_Type_Form));
			// Set charset
			httpRequest.setHeader("charset",
					resources.getString(R.string.API_Web_Charset));
			// Set Body for Request
			httpRequest.setEntity(webRequest.getTokenStrEntity());

			// Send Request
			HttpResponse httpResponse = httpClient.execute(httpRequest);
			EventBus.getDefault().post(httpResponse);

		} catch (URISyntaxException e) {
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		}

	}

	private void createUser(WebRequest webRequest) {
		Resources resources = webRequest.getContext().getResources();
		// HttpClient with Pinning
		DefaultHttpClient httpClient = makeHttpClient(resources);
		// Create HttpPost for Creating User
		HttpPost httpRequest = new HttpPost();

		try {
			// Set URI
			httpRequest.setURI(new URI(resources
					.getString(R.string.API_Web_User)));
			// Set Content-Type
			httpRequest.setHeader("Content-type",
					resources.getString(R.string.API_WebContent_Type_Json));
			// Set charset
			httpRequest.setHeader("charset",
					resources.getString(R.string.API_Web_Charset));
			// Set Body for Request
			httpRequest.setEntity(webRequest.getJsonCreateUser());

			// Send Request
			HttpResponse httpResponse = httpClient.execute(httpRequest);
			EventBus.getDefault().post(httpResponse);

		} catch (URISyntaxException e) {
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		}

	}

	// Create a HttpClient for use with the other functions in this class
	private DefaultHttpClient makeHttpClient(Resources resources) {
		// Create a HttpClient with keystore
		DefaultHttpClient httpClient = null;
		try {
			httpClient = new HttpClientBuilder()
					.setConnectionTimeout(10000)
					.setSocketTimeout(60000)
					.setHttpPort(80)
					.setHttpsPort(443)
					.setCookieStore(new BasicCookieStore())
					.pinCertificates(resources, R.raw.sadp_keystore, STORE_PASS)
					.build();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (CancellationException e) {
		}

		return httpClient;
	}

	protected HttpRequestBase makeHttpRequest() {
		HttpRequestBase httpRequest = null;

		return httpRequest;
	}

}
