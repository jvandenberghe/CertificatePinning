package cmpe277.sadp.certpinbasic.test;

import cmpe277.sadp.certpinbasic.CertPinMainActivity;

import com.robotium.solo.*;

import android.test.ActivityInstrumentationTestCase2;
public class TestCreate extends ActivityInstrumentationTestCase2<CertPinMainActivity>{
	private Solo solo;
	
	public TestCreate() {
		super(CertPinMainActivity.class);
	}
	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
	}
	
	public void testCreateUser() throws Exception {
		// Click on the Register User button
		assertTrue(solo.waitForActivity(cmpe277.sadp.certpinbasic.CertPinMainActivity.class, 10000));
		solo.assertCurrentActivity("Wrong Activity", CertPinMainActivity.class);
		solo.clickOnMenuItem(solo.getString( cmpe277.sadp.certpinbasic.R.string.action_register));
		solo.clickOnButton(solo.getString(cmpe277.sadp.certpinbasic.R.string.btn_create_user_str));
		solo.sleep(2000);
		solo.clickOnButton(solo.getString(cmpe277.sadp.certpinbasic.R.string.btn_get_tkn_str));
		solo.sleep(10000);
		
	}
	@Override
	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
		super.tearDown();
	}

}
