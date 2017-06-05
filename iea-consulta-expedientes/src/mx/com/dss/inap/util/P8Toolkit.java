package mx.com.dss.inap.util;

import java.io.FileInputStream;

import com.filenet.wcm.api.ObjectFactory;
import com.filenet.wcm.api.Session;
import com.filenet.wcm.toolkit.util.WcmEncodingUtil;

public class P8Toolkit {
	
	public static String getUserToken(String user, String password, String context) throws Exception {
		
		Session session = ObjectFactory.getSession("UserToken", null, user, password);
		session.setConfiguration(new FileInputStream (context + INAPConstant.PROPERTIES_WCM));
		session.verify();
		String token = session.getToken(false);
		

		
		return encodeURL(token);		
		
	}
	
	public static String encodeURL(String str) throws Exception {
		return WcmEncodingUtil.encodeURL(str);
	}
	
}
