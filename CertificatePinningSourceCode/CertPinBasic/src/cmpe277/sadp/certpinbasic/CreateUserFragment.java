package cmpe277.sadp.certpinbasic;


import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.util.Log;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.LinearLayout;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import co.infinum.https.*;
import de.greenrobot.event.EventBus;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import 	java.lang.InterruptedException;










import android.provider.Settings.Secure;

public class CreateUserFragment extends Fragment {
	private static final String TAG = "Create User Fragment";
	private View rootView;
	private TextView userIdView;
	private TextView userPwView;
	private TextView userTokenView;
	private LinearLayout userIdLinear;
	private LinearLayout userPwLinear;
	private LinearLayout userTokenLinear;
	private Button tokenButton;
	private Button userCreateButton;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_create_user,
				container, false);
		
		
		userIdView = (TextView)rootView.findViewById(R.id.txt_user_id_created);
		userPwView = (TextView)rootView.findViewById(R.id.txt_user_pw_created);
		userTokenView = (TextView)rootView.findViewById(R.id.txt_user_token_created);
		
		userIdLinear = (LinearLayout)rootView.findViewById(R.id.linear_user_id);
		userPwLinear = (LinearLayout)rootView.findViewById(R.id.linear_user_pw);
		userTokenLinear = (LinearLayout)rootView.findViewById(R.id.linear_user_token);
		
		tokenButton = (Button)rootView.findViewById(R.id.btn_get_token);
		userCreateButton = (Button)rootView.findViewById(R.id.btn_create_user);
		
		if(LocalStorage.userIsInitialized() == true) {
			userCreateButton.setVisibility(Button.INVISIBLE);
			LocalData localData = LocalStorage.getMemberLocalData();
			userIdView.setText((localData.getUserID()));
			userPwView.setText(localData.getUserPW());
			userPwLinear.setBackgroundColor(Color.GREEN);
			userIdLinear.setBackgroundColor(Color.GREEN);
			
			if(localData.getToken() != null) {
				userTokenLinear.setBackgroundColor(Color.GREEN);
				userTokenView.setText(localData.getToken());
			}
		} else {
			userCreateButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					createUserAction();
				}
			});
		}
		
		tokenButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				getUserToken();
			}
		});
		
		
		
		
		return rootView;
	}
	
	private void createUserAction() {
		
		String androidId = Secure.getString(getActivity().getContentResolver(), Secure.ANDROID_ID);
		WebRequest webRequest = new WebRequest(androidId, SadpWebRestful.REQUEST_TYPE.CREATE, getActivity());
		EventBus.getDefault().post(webRequest);
		EventBus.getDefault().post(new LocalData(webRequest));
		
	}
	
	private void getUserToken() {
		LocalData data = LocalStorage.getMemberLocalData();
		WebRequest webRequest = new WebRequest(data, SadpWebRestful.REQUEST_TYPE.TOKEN, getActivity());
		EventBus.getDefault().post(webRequest);
	}
	
	public void onEventMainThread(HttpResponse response) {
		
		StatusLine status = response.getStatusLine();
		try{
			if(status != null) {
				if(HttpStatus.SC_OK == status.getStatusCode()) {
					String retStr = EntityUtils.toString(response.getEntity());
					
					if( retStr.length() == 0) {
					// Success, user registration
						Toast.makeText(getActivity(), "Success: " + status.toString(), Toast.LENGTH_LONG).show();
						userIdLinear.setBackgroundColor(Color.GREEN);
						userPwLinear.setBackgroundColor(Color.GREEN);
						userCreateButton.setVisibility(Button.INVISIBLE);

						LocalData member = LocalStorage.getMemberLocalData();
						userIdView.setText(member.getUserID());
						userPwView.setText(member.getUserPW());
						LocalData memberUpdate = new LocalData(member, true);
						EventBus.getDefault().post(memberUpdate);
					}
					else {
						userIdLinear.setBackgroundColor(Color.GREEN);
						userPwLinear.setBackgroundColor(Color.GREEN);
						userCreateButton.setVisibility(Button.INVISIBLE);
						JSONObject result = new JSONObject(retStr);
						String token = result.getString("access_token");
						userTokenView.setText(token);
						userTokenLinear.setBackgroundColor(Color.GREEN);
						LocalData dataWithToken = new LocalData(LocalStorage.getMemberLocalData(), token);
						EventBus.getDefault().post(dataWithToken);
					}
				}
				else {
					String retStr = null;
					if(response.getEntity() != null) {
						retStr = EntityUtils.toString(response.getEntity());
						JSONObject result = new JSONObject(retStr);
						// HACK
						LocalData member = LocalStorage.getMemberLocalData();
						userIdView.setText(member.getUserID());
						userPwView.setText(member.getUserPW());
						LocalData memberUpdate = new LocalData(member, true);
						EventBus.getDefault().post(memberUpdate);
						
						
					}
					Log.e(TAG, "Response: " + status.toString());
					if(retStr != null) {
						Log.e(TAG, "Response Data: " + retStr);
					}
				}
			}
			else {
				Log.e(TAG, "Null Returned for statusLine, not good");
			}
		}
		catch(IOException e) { }
		catch(JSONException e) { }
	}
	
	public void unregisterEventBus() {
		EventBus.getDefault().unregister(this);
	}
	
	
	@Override
	public void onResume() {

		super.onResume();
		EventBus.getDefault().register(this);
	}
	@Override
	public void onPause() {

		unregisterEventBus();
		super.onPause();
	}

	
}