package cmpe277.sadp.certpinbasic;

import java.io.UnsupportedEncodingException;

import android.content.SharedPreferences;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.res.Resources;

// WebRequest Helper Class
public class WebRequest {

	private final String CONF_PW = "ConfirmPassword";
	private final String PW = "Password";
	private final String EMAIL = "Email";
	
	// Match SadpWebRest/Models/Members.cs
	// Must match for Odata translation and entity framework to function properly
	public  static final String MEM_ID = "MemberID";
	public  static final String NAME = "Name";
	public  static final String LAT = "LocLatCoord";
	public  static final String LONG = "LocLongCoord";
	public  static final String STATUS = "Status";
	
	private String token;

	private String userID;
	private String userPW;
	private double locLatCoord;
	private double locLongCoord;
	private String status;
	private String name;
	private int memberId;
	private SadpWebRestful.REQUEST_TYPE request;
	private Context context;

	// Blank Constructor
	public WebRequest() {

	}

	public WebRequest(LocalData data, SadpWebRestful.REQUEST_TYPE requestType,
			Context context) {
		this(data);
		this.request = requestType;
		this.context = context;

	}

	public WebRequest(LocalData data) {
		this.memberId = data.getMemberId();
		this.name = data.getName();
		this.status = data.getStatus();
		this.token = data.getToken();
		this.userID = data.getUserID();
		this.userPW = data.getUserPW();
	}

	public WebRequest(SharedPreferences dataStore) {
		this.userID = dataStore.getString(LocalStorage.UID, null);
		this.userPW = dataStore.getString(LocalStorage.UPW, null);
		this.status = dataStore.getString(LocalStorage.STATUS, null);
		this.name = dataStore.getString(LocalStorage.NAME, null);
		this.memberId = dataStore.getInt(LocalStorage.ID, -1);
		this.token = dataStore.getString(LocalStorage.TOKEN, null);
	}

	// Create UserID and PW based off of deviceID
	public WebRequest(String deviceId, SadpWebRestful.REQUEST_TYPE requestType,
			Context context) {
		this.userID = deviceId.substring(0, deviceId.length() / 2);
		this.userID += LocalData.ALPHA_UID + "@sadp.com";
		this.userPW = deviceId.substring(deviceId.length() / 2 + 1,
				deviceId.length() - 1);
		this.userPW += "aA!";
		this.request = requestType;
		this.context = context;
	}
	
	public WebRequest(LocalData data, SadpWebRestful.REQUEST_TYPE requestType, Context context, Double userLongCoord, Double userLatCoord) {
		this(data,requestType, context);
		this.locLatCoord = userLatCoord;
		this.locLongCoord = userLongCoord;
	}

	public StringEntity getJsonCreateUser() {

		JSONObject jsonObj = new JSONObject();
		try {
			jsonObj.put(EMAIL, userID);
			jsonObj.put(PW, userPW);
			jsonObj.put(CONF_PW, userPW);
			return new StringEntity(jsonObj.toString(), HTTP.UTF_8);
		} catch (JSONException e) {
		} catch (UnsupportedEncodingException e) {
		}
		return null;
	}

	public StringEntity getJsonMember(SadpWebRestful.REQUEST_TYPE type) {
		JSONObject jsonObj = new JSONObject();
		try {
			jsonObj.put(EMAIL, userID);
			jsonObj.put(NAME, name);
			jsonObj.put(LAT, locLatCoord);
			jsonObj.put(LONG, locLongCoord);
			jsonObj.put(STATUS, status);
			// memberId will only be valid after a member has been created
			if(type == SadpWebRestful.REQUEST_TYPE.UPDATE) {
				jsonObj.put(MEM_ID, memberId);
			}
			return new StringEntity(jsonObj.toString(), HTTP.UTF_8);
		} catch (JSONException e) {
		} catch (UnsupportedEncodingException e) {
		}
		return null;
	}
	public StringEntity getTokenStrEntity() {
		// grant_type=password&username=Alice@test.com&password=Password123$
		try {
			String returnStr = context.getResources().getString(
					R.string.API_Web_Token_Grant_Type);
			returnStr += userID;
			returnStr += context.getResources().getString(
					R.string.API_Web_Token_And_Password);
			returnStr += userPW;
			return new StringEntity(returnStr, HTTP.UTF_8);
		} catch (UnsupportedEncodingException e) {
		}
		return null;
	}

	public String getToken() {
		return token;
	}

	public String getUserID() {
		return userID;
	}

	public String getUserPW() {
		return userPW;
	}

	public double getLocLatCoord() {
		return locLatCoord;
	}

	public double getLocLongCoord() {
		return locLongCoord;
	}

	public String getStatus() {
		return status;
	}

	public String getName() {
		return name;
	}

	public int getMemberId() {
		return memberId;
	}

	public SadpWebRestful.REQUEST_TYPE getRequest() {
		return request;
	}

	public Context getContext() {
		return context;
	}

}
