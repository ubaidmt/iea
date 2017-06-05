package mx.com.dss.inap.services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import mx.com.dss.inap.model.Credencial;
import mx.com.dss.inap.util.INAPUtil;

import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.CustomObject;

public class CredencialService {
	
	static ContentEngineServices ceServices = null;
	static Credencial credencial = null;
	static Properties propiedades = null;

	public Credencial obtenerCredencial(String usuario, String context) {
		
		String query = "SELECT [This], [Usuario], [Contrasena], [VIP], [TipoExpediente] FROM [Credencial] WHERE ([Usuario] = '" + usuario + "')";
		
		try {
			ceServices = new ContentEngineServices(context);
			
			if(ceServices.isConected()){
				
				List<?> credenciales = ceServices.ecmSelectCustomObject(query);
				
				if(credenciales != null && credenciales.size() == 1){
					CustomObject customObject = (CustomObject) credenciales.get(0);
					credencial = new Credencial();
					credencial.setUsuario(customObject.getProperties().getStringValue("Usuario"));
					credencial.setContrasena(customObject.getProperties().getStringValue("Contrasena"));
					credencial.setVip(customObject.getProperties().getBooleanValue("VIP"));
					
					List<String> tipoExpLista = new ArrayList<String>();;
					Iterator<?> it = customObject.getProperties().getStringListValue("TipoExpediente").iterator();

					while (it.hasNext() == true){
						tipoExpLista.add(it.next().toString());
					}
					credencial.setTipoExpedientes(tipoExpLista);
				}
				
				ceServices.doDisconnect();
				
			}else{
				throw new Exception("Ocurrió un error al conectarse a Content Engine.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return credencial;
	}
	
	public Boolean cambiarPassword(String usuario, String newPassword, String context){
		
		String query = "SELECT * FROM Credencial WHERE Usuario = '" + usuario + "'";
		Boolean result = false;
	
		try {
			ceServices = new ContentEngineServices(context);
			if(ceServices.isConected()){
				List<?> credenciales = ceServices.ecmSelectCustomObject(query);
				if(credenciales != null && credenciales.size() == 1){
					CustomObject customObject = (CustomObject) credenciales.get(0);
		             com.filenet.api.property.Properties props = customObject.getProperties();
		             props.putValue("Contrasena", INAPUtil.encryptedMessage(newPassword));
		             customObject.save(RefreshMode.REFRESH);
		             result = true;
				}
				ceServices.doDisconnect();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

}
