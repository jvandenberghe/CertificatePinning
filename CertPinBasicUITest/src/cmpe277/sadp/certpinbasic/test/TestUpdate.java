package cmpe277.sadp.certpinbasic.test;

import cmpe277.sadp.certpinbasic.CertPinMainActivity;

import com.robotium.solo.*;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;
public class TestUpdate extends ActivityInstrumentationTestCase2<CertPinMainActivity>{
	private Solo solo;
	
	public TestUpdate() {
		super(CertPinMainActivity.class);
	}
	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
	}
	
	public void testCreateUser() throws Exception {
		// Click on the Register User button
		assertTrue(solo.waitForActivity(cmpe277.sadp.certpinbasic.CertPinMainActivity.class, 10000));
		solo.assertCurrentActivity("Wrong Activity", CertPinMainActivity.class);
		solo.clickOnMenuItem(solo.getString( cmpe277.sadp.certpinbasic.R.string.action_update));
		solo.clickOnButton(solo.getString(cmpe277.sadp.certpinbasic.R.string.Up_Frag_Current_Loc_txt));

		EditText nameBox = solo.getEditText(0);
		if(nameBox.isEnabled()) {
			nameBox.setText("Robotium");
		}
		solo.enterText(1,"Robot Hell" + System.currentTimeMillis());
		solo.clickOnButton(solo.getString(cmpe277.sadp.certpinbasic.R.string.Up_Frag_Update_Member_txt));
		
		solo.sleep(10000);
	}
	@Override
	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
		super.tearDown();
	}

}
