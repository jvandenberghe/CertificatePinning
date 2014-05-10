package cmpe277.sadp.certpinbasic;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import android.app.Fragment;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Location;
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
import java.lang.InterruptedException;

import android.provider.Settings.Secure;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;


public class UpdateMemberFragment extends Fragment implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener {
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
	private LocationClient locationClient;

	// Fragment Views
	private EditText userName;
	private EditText userStatus;
	private EditText userLongCoord;
	private EditText userLatCoord;
	private Button userLocationButton;
	private Button updateUserMemberButton;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_update_member, container,
				false);

		// Initialize views for easy access
		userName = (EditText) rootView.findViewById(R.id.Up_Frag_ET_Name);
		userStatus = (EditText) rootView.findViewById(R.id.Up_Frag_ET_Status);
		userLongCoord = (EditText) rootView.findViewById(R.id.Up_Frag_TV_Long);
		userLatCoord = (EditText) rootView.findViewById(R.id.Up_Frag_TV_Lat);
		userLocationButton = (Button) rootView
				.findViewById(R.id.Up_Frag_Get_Loc);
		updateUserMemberButton = (Button) rootView
				.findViewById(R.id.Up_Frag_Up_To_Web);

		// Check to see if name and memberId have been defined, if not this is
		// the first time running!
		LocalData localData = LocalStorage.getMemberLocalData();
		if (localData.getName() != null && localData.getMemberId() >= 1) {
			// Disable EditText for Name, I don't want users changing
			userName.setText(localData.getName());
			userName.setEnabled(false);

		}

		// Create onClick Listeners for buttons

		userLocationButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				updateLocation();

			}

			
		});

		updateUserMemberButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				uploadUserMemberData();

			}
		});
		
		locationClient = new LocationClient(getActivity(), this, this);

		return rootView;
	}
	
	private void updateLocation() {
		Location currentLocation = locationClient.getLastLocation();
		if(currentLocation != null) {
			userLatCoord.setText(Double.toString(currentLocation.getLatitude()));
			userLongCoord.setText(Double.toString(currentLocation.getLongitude()));
		}
		else {
			userLatCoord.setText(getActivity().getResources().getString(R.string.Up_Frag_Lat_txt));
			userLongCoord.setText(getActivity().getResources().getString(R.string.Up_Frag_Long_txt));
		}
	}

	private void uploadUserMemberData() {
		LocalData localData = LocalStorage.getMemberLocalData();

		// User Name is initialized after first time running update
		// Afterwards, all subsequent web requests are of the Update variety
		if (localData.getName() != null && localData.getMemberId() >= 1) {
			
			// Update Case
			LocalData localDataUpdate = new LocalData(localData, userName
					.getText().toString(), userStatus.getText().toString());
			try {
				// Test Input before passing on
				double longCoord = Double.parseDouble(userLongCoord
						.getText().toString());
				double latCoord = Double.parseDouble(userLatCoord.getText()
						.toString());

				WebRequest webRequest = new WebRequest(localDataUpdate,
						SadpWebRestful.REQUEST_TYPE.UPDATE, getActivity(),
						longCoord, latCoord);
				EventBus.getDefault().post(webRequest);
			} catch (NumberFormatException e) {
				Toast.makeText(getActivity(),
						"Invalid Location Coordinates", Toast.LENGTH_LONG)
						.show();
			}
			
			
			

		} else { // First time Update, Creating new entry in database
			// MemberId will be created in cloud SQL for new entry, must
			// remember this Id for all future transactions

			if (userName.getText().toString().length() == 0) { // Empty
				Toast.makeText(getActivity(), "Enter a User Name",
						Toast.LENGTH_LONG).show();
			} else {
				// Create New LocalData with updated name
				LocalData localDataUpdate = new LocalData(localData, userName
						.getText().toString(), userStatus.getText().toString());
				
				
				
				try {
					// Test Input before passing on
					double longCoord = Double.parseDouble(userLongCoord
							.getText().toString());
					double latCoord = Double.parseDouble(userLatCoord.getText()
							.toString());

					WebRequest webRequest = new WebRequest(localDataUpdate,
							SadpWebRestful.REQUEST_TYPE.ADD, getActivity(),
							longCoord, latCoord);
					EventBus.getDefault().post(webRequest);
				} catch (NumberFormatException e) {
					Toast.makeText(getActivity(),
							"Invalid Location Coordinates", Toast.LENGTH_LONG)
							.show();
				}
			}

		}
	}

	public void onEventMainThread(HttpResponse response) {

		StatusLine status = response.getStatusLine();
		try {
			// See if there is a response
			String retStr = null;
			if (response.getEntity() != null) {
				retStr = EntityUtils.toString(response.getEntity());
			} else {
				retStr = "";
			}
			if (status != null) {
				if (HttpStatus.SC_CREATED == status.getStatusCode()) {
					// Succesful Creation of Member in database, disable name changing
					userName.setEnabled(false);
					
					// Create New LocalData from the returned JSon
					JSONObject result = new JSONObject(retStr);
					String name = result.getString(WebRequest.NAME);
					int dbMemberId = result.getInt(WebRequest.MEM_ID);
					String statusOfMember = result.getString(WebRequest.STATUS);
					LocalData localDataUpdate = new LocalData(LocalStorage.getMemberLocalData(), dbMemberId, name, statusOfMember);
					EventBus.getDefault().post(localDataUpdate);
				} else if (HttpStatus.SC_NO_CONTENT == status.getStatusCode()){
					Toast.makeText(getActivity(), "Update Succesful", Toast.LENGTH_LONG).show();
				} else {
					Log.e(TAG, "Response: " + status.toString());
				}
			} else {
				Log.e(TAG, "Null Returned for statusLine, not good");
			}
		} catch (IOException e) {
		} catch (JSONException e) {
		}
	}
	
	

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		/*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        getActivity(),
                        9000);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            Toast.makeText(getActivity(), "Google Play Services Error Code: " + connectionResult.getErrorCode(), Toast.LENGTH_LONG).show();
        }
		
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		Toast.makeText(getActivity(), "Google Services Connected", Toast.LENGTH_SHORT).show();
		
	}

	@Override
	public void onDisconnected() {
		Toast.makeText(getActivity(), "Google Services have Disconnected: Please Try Again", Toast.LENGTH_SHORT).show();
		
	}
	
	
	private boolean checkOnGooglePlayServices() {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
		if(ConnectionResult.SUCCESS == resultCode) {
			return true;
		} else {
			Toast.makeText(getActivity(), "Google Play Services Are not Available", Toast.LENGTH_SHORT).show();
			return false;
		}
	}

	@Override
	public void onStart() {

		super.onStart();
		locationClient.connect();
	}
	
	@Override
	public void onStop() {
		locationClient.disconnect();
		super.onStop();
	}
	@Override
	public void onResume() {
		super.onResume();
		EventBus.getDefault().register(this);
	}

	@Override
	public void onPause() {
		EventBus.getDefault().unregister(this);
		super.onPause();
	}

}