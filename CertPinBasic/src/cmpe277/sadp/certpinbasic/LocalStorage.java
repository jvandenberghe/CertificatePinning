package cmpe277.sadp.certpinbasic;

import android.content.Context;
import de.greenrobot.event.EventBus;
import android.content.SharedPreferences;

public class LocalStorage {
	
	public static final String UID = "userID";
	public static final String UPW = "userPW";
	public static final String STATUS = "status";
	public static final String NAME = "name";
	public static final String ID = "memberId";
	public static final String INIT = "initialized";
	public static final String TOKEN = "token";
	
	
	private static Context context;
	private static SharedPreferences localStoragePref;
	private static final String DATA_SADP = "PrefsFileSadpApp";
	
	public LocalStorage(Context contextAct) {
		context = contextAct;
		localStoragePref = context.getSharedPreferences(DATA_SADP, Context.MODE_PRIVATE);
		EventBus.getDefault().register(this);
	}
	
	public void unregisterEventBus() {
		EventBus.getDefault().unregister(this);
	}
	
	public void onEventBackgroundThread(LocalData localMemberData) {

		SharedPreferences.Editor editor = localStoragePref.edit();
		
		String dataStr = localMemberData.getUserID();
		if(dataStr != null) {
			editor.putString(UID, dataStr);
		}
		dataStr = localMemberData.getUserPW();
		if(dataStr != null) {
			editor.putString(UPW, dataStr);
		}
		dataStr = localMemberData.getStatus();
		if(dataStr != null) {
			editor.putString(STATUS, dataStr);
		}
		dataStr = localMemberData.getName();
		if(dataStr != null) {
			editor.putString(NAME, dataStr);
		}
		dataStr = localMemberData.getToken();
		if(dataStr != null) {
			editor.putString(TOKEN, dataStr);
		}
		int dataNum = localMemberData.getMemberId();
		if(dataStr != null) {
			editor.putInt(ID, dataNum);
		}
		boolean init = localMemberData.isInitialized();
		if(init == true) {
			editor.putBoolean(INIT, init);
		}
		editor.commit();
	}
	
	public static boolean userIsInitialized() {
		
		return localStoragePref.getBoolean(INIT, false);
	}
	
	public static LocalData getMemberLocalData() {
		
		return new LocalData(localStoragePref);
	}
}
