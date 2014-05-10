package cmpe277.sun.security.tools.borrowed;

import java.security.MessageDigest;
import java.security.cert.Certificate;


/*
 * This class takes the essential methods for creating pretty fingerprints for certificates
 * The code is found here:
 * http://www.docjar.com/html/api/sun/security/tools/KeyTool.java.html
 */
public class Encoding {

	public static String getCertFingerPrint(String mdAlg, Certificate cert)
			throws Exception {
		byte[] encCertInfo = cert.getEncoded();
		MessageDigest md = MessageDigest.getInstance(mdAlg);
		byte[] digest = md.digest(encCertInfo);
		return toHexString(digest);
	}

	private static String toHexString(byte[] block) {
		StringBuffer buf = new StringBuffer();
		int len = block.length;
		for (int i = 0; i < len; i++) {
			byte2hex(block[i], buf);
			if (i < len - 1) {
				buf.append(":");
			}
		}
		return buf.toString();
	}

	private static void byte2hex(byte b, StringBuffer buf) {
		char[] hexChars = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'A', 'B', 'C', 'D', 'E', 'F' };
		int high = ((b & 0xf0) >> 4);
		int low = (b & 0x0f);
		buf.append(hexChars[high]);
		buf.append(hexChars[low]);
	}

}
