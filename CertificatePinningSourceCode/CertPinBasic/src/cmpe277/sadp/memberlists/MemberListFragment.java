package cmpe277.sadp.memberlists;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cmpe277.sadp.certpinbasic.LocalStorage;
import cmpe277.sadp.certpinbasic.MemberData;
import cmpe277.sadp.certpinbasic.R;
import cmpe277.sadp.certpinbasic.SadpWebRestful;
import cmpe277.sadp.certpinbasic.WebRequest;
import de.greenrobot.event.EventBus;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class MemberListFragment extends ListFragment {

	private View rootView;
	private MemberListAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		adapter = new MemberListAdapter(getActivity(),
				new ArrayList<MemberData>());

		this.setListAdapter(adapter);

		WebRequest request = new WebRequest(LocalStorage.getMemberLocalData(),
				SadpWebRestful.REQUEST_TYPE.LIST, getActivity());
		EventBus.getDefault().post(request);

		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
		EventBus.getDefault().register(this);
	}

	@Override
	public void onStop() {
		EventBus.getDefault().unregister(this);
		super.onStop();
	}

	public void onEventMainThread(HttpResponse response) {
		StatusLine status = response.getStatusLine();
		if (status.getStatusCode() == HttpStatus.SC_OK) {
			HttpEntity entity = response.getEntity();
			try {
				String retStr = EntityUtils.toString(entity);
				JSONObject jsonObj = new JSONObject(retStr);
				JSONArray jsonArray = jsonObj.getJSONArray("value");
				adapter.setContentWithJson(jsonArray);
			} catch (IOException e) {
			} catch (JSONException e) {
			}
		} else {
			Toast.makeText(getActivity(), status.toString() + "\nError Retrieving Member Data", Toast.LENGTH_LONG).show();
		}
	}

}
