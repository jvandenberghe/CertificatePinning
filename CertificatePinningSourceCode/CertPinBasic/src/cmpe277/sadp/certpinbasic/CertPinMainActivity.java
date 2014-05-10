package cmpe277.sadp.certpinbasic;

import java.io.IOException;

import cmpe277.sadp.memberlists.MemberListFragment;
import de.greenrobot.event.EventBus;
import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

public class CertPinMainActivity extends Activity {
	private SadpWebRestful sadpService;
	private LocalStorage storage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cert_pin_main);

		EventBus.getDefault().register(this);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new MainActivityFragment(), CERT_FRAG)
					.commit();

		}
		storage = new LocalStorage(CertPinMainActivity.this);
		sadpService = new SadpWebRestful();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.cert_pin_main, menu);
		return true;
	}

	private static final String CERT_FRAG = "CERT_PIN_MAIN_FRAG";
	private static final String CREATE_FRAG = "CREATE_USER_FRAG";
	private static final String UPDATE_FRAG = "UPDATE_USER_FRAG";
	private static final String VIEW_FRAG = "VIEW_LIST_FRAG";

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		FragmentManager fragMan = getFragmentManager();
		FragmentTransaction fragTran = getFragmentManager().beginTransaction();

		Fragment fragment = null;
		switch (item.getItemId()) {
		case (R.id.action_register):
			fragment = fragMan.findFragmentByTag(CREATE_FRAG);
			if (fragment == null) {
				fragment = new CreateUserFragment();
			}
			fragTran.replace(R.id.container, fragment, CREATE_FRAG);
			break;
		case (R.id.action_cert_pin):
			fragment = fragMan.findFragmentByTag(CERT_FRAG);
			if (fragment == null) {
				fragment = new MainActivityFragment();
			}
			fragTran.replace(R.id.container, fragment, CERT_FRAG);
			break;
		case (R.id.action_update):
			fragment = fragMan.findFragmentByTag(UPDATE_FRAG);
			if (fragment == null) {
				fragment = new UpdateMemberFragment();

			}
			fragTran.replace(R.id.container, fragment, UPDATE_FRAG);
			break;
		case (R.id.action_view_list):
			fragment = fragMan.findFragmentByTag(VIEW_FRAG);
			if (fragment == null) {
				fragment = new MemberListFragment();
			}
			fragTran.replace(R.id.container, fragment, VIEW_FRAG);
			break;
		default:
			fragment = new MainActivityFragment();
		}

		fragTran.addToBackStack(null);
		fragTran.commit();
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
		storage.unregisterEventBus();
		storage = null;
		sadpService.unregisterEventBus();
		sadpService = null;
		EventBus.getDefault().unregister(this);
	}

	public void onEventMainThread(IOException e) {
		e.printStackTrace();
	}

}
