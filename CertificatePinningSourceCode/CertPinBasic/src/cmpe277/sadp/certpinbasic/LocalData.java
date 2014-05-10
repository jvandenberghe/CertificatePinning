package cmpe277.sadp.certpinbasic;

import android.content.SharedPreferences;
// Need to remember the name, status, UiD UPW, and Token between sessions
public class LocalData {
	private String userID;
	private String userPW;
	private String status;
	private String name;
	private int memberId;
	private String token;
	private boolean initialized;
	
	public static final String ALPHA_UID = "E";
	
	public LocalData(SharedPreferences localStoragePref) {
		this.userID = localStoragePref.getString(LocalStorage.UID, null);
		this.userPW = localStoragePref.getString(LocalStorage.UPW, null);
		this.status = localStoragePref.getString(LocalStorage.STATUS, null);
		this.name = localStoragePref.getString(LocalStorage.NAME, null);
		this.memberId = localStoragePref.getInt(LocalStorage.ID, -1);
		this.token = localStoragePref.getString(LocalStorage.TOKEN, null);
		this.initialized = localStoragePref.getBoolean(LocalStorage.INIT, false);
	}
	public LocalData(LocalData prevData) {
		this.userID = prevData.userID;
		this.userPW = prevData.userPW;
		this.initialized = prevData.initialized;
		this.token = prevData.token;
		this.memberId = prevData.memberId;
		this.name = prevData.name;
		this.status = prevData.status;
	}
	public LocalData(WebRequest webRequest) {
		this.userID = webRequest.getUserID();
		this.userPW = webRequest.getUserPW();
	}
	public LocalData(String deviceId) {
		this.userID = deviceId.substring(0, deviceId.length()/2);
		this.userID += ALPHA_UID + "@sadp.com";
		this.userPW = deviceId.substring(deviceId.length() / 2 + 1, deviceId.length() - 1);
		this.userPW += "aA!";
	}
	// From a previous LocalData, for example between CreateUserFragment operations
	public LocalData(LocalData prevData, boolean init) {
		this(prevData);
		this.initialized = init;
		
	}
	
	public LocalData(LocalData prevData, String token) {
		this(prevData);
		this.token = token;
	}
	
	public LocalData(LocalData prevData, String name, String status) {
		this(prevData);
		this.name = name;
		this.status = status;
	}
	
	public LocalData(LocalData prevData, int dbMemberId, String name, String status) {
		this(prevData);
		this.memberId = dbMemberId;
		this.name = name;
		this.status = status;
	}

	public String getUserID() {
		return userID;
	}

	public String getUserPW() {
		return userPW;
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

	public String getToken() {
		return token;
	}

	public boolean isInitialized() {
		return initialized;
	}
	
}
