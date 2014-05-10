package cmpe277.sadp.certpinbasic;

import org.json.JSONException;
import org.json.JSONObject;

// used for MemberData pulled from Web service
public class MemberData {
	private double locLatCoord;
	private double locLongCoord;
	private String status;
	private String name;
	private int memberId;
	
	public MemberData(JSONObject obj) {
		try{
			this.memberId = obj.getInt("MemberID");
			this.name = obj.getString("Name");
			this.locLongCoord = obj.getDouble("LocLongCoord");
			this.locLatCoord = obj.getDouble("LocLatCoord");
			this.status = obj.getString("Status");
		} catch(JSONException e) {
		}
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

	public String getLocationCoordString() {
		String coordStr = "( ";
		coordStr += Double.toString(locLatCoord);
		coordStr += ", ";
		coordStr += Double.toString(locLongCoord);
		coordStr += " )";
		return coordStr;
	}

}
