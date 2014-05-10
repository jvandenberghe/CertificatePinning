package cmpe277.sadp.memberlists;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import cmpe277.sadp.certpinbasic.MemberData;
import cmpe277.sadp.certpinbasic.R;
import android.view.LayoutInflater;

public class MemberListAdapter extends ArrayAdapter<MemberData> {

	private final Context context;
	private final List<MemberData> memberList;

	public MemberListAdapter(Context context, List<MemberData> list) {
		super(context, R.layout.row_member_frag_member_list, list);
		this.context = context;
		this.memberList = list;
	}

	// Used as a convenience
	static class ViewHolder {
		public TextView userName;
		public TextView userStatus;
		public TextView userLocation;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		if (rowView == null) {
			LayoutInflater inflator = LayoutInflater.from(context);
			rowView = inflator.inflate(R.layout.row_member_frag_member_list,
					null);
			// Easy Way to access sub view later
			ViewHolder holder = new ViewHolder();
			holder.userName = (TextView) rowView
					.findViewById(R.id.row_user_name);
			holder.userStatus = (TextView) rowView
					.findViewById(R.id.row_user_status);
			holder.userLocation = (TextView) rowView
					.findViewById(R.id.row_user_location_coord);

			// Convenient way of accessing sub views at a later time
			rowView.setTag(holder);
		}

		ViewHolder holder = (ViewHolder) rowView.getTag();
		MemberData member = memberList.get(position);
		holder.userName.setText(member.getName());
		holder.userStatus.setText(member.getStatus());
		holder.userLocation.setText(member.getLocationCoordString());
		return rowView;
	}

	public void setContentWithJson(JSONArray jsonArray) {
		try {
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObj = jsonArray.getJSONObject(i);
				memberList.add(new MemberData(jsonObj));
			}
			notifyDataSetChanged(); 
		} catch (JSONException e) {
		}
	}
}
