package mx.com.dss.inap.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;

import com.filenet.api.collection.IndependentObjectSet;
import com.filenet.api.core.Connection;
import com.filenet.api.core.CustomObject;
import com.filenet.api.core.Document;
import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.Folder;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.query.SearchSQL;
import com.filenet.api.query.SearchScope;
import com.filenet.api.util.Id;
import com.filenet.api.util.UserContext;
import com.filenet.apiimpl.collection.StringListImpl;

import javax.security.auth.Subject;

import mx.com.dss.inap.model.Configuration;
import mx.com.dss.inap.util.INAPConstant;
import mx.com.dss.inap.util.INAPUtil;
import mx.com.dss.inap.util.P8Toolkit;

public class ContentEngineServices {

	static UserContext uc;
	static Domain dom;
	static Connection con = null;
	static Subject sub = null;
	static ObjectStore objectStore = null;
	String workplaceContext, userToken;

	static Properties propiedades;

	public static ObjectStore getObjectStore() {
		return objectStore;
	}

	public static void setObjectStore(ObjectStore objectStore) {
		ContentEngineServices.objectStore = objectStore;
	}

	public String getWorkplaceContext() {
		return workplaceContext;
	}

	public void setWorkplaceContext(String workplaceContext) {
		this.workplaceContext = workplaceContext;
	}

	public String getUserToken() {
		return userToken;
	}

	public void setUserToken(String userToken) {
		this.userToken = userToken;
	}

	public ContentEngineServices(String contextPath) throws Exception{

		Configuration appContext;
		
			appContext = ContentEngineServices.loadProperties(contextPath);
			if (appContext != null) {
				setUserToken(appContext.getUserToken());
				setWorkplaceContext(appContext.getWorkplaceContext());
				login(appContext);
			}

	}

	/**
	 * Metodo que establece la conecion con Content Engine
	 * 
	 * @param context
	 *            {@link Configuration}
	 * @return {@link Boolean}
	 */
	private Boolean login(Configuration context) {
		return loginContentEngine(context);
	}

	/**
	 * Método que cierra la conexion con Content Engine
	 * 
	 * @return boolean
	 * @throws Exception
	 */
	public boolean doDisconnect() {
		try {
			logOutContentEngine();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Método para ejecutar busqueda de Custom Objects
	 * 
	 * @param query
	 *            {@link String}
	 * @return {@link CustomObject}
	 * @throws Exception
	 */
	public List<CustomObject> ecmSelectCustomObject(String query) {
		CustomObject object = null;
		List<CustomObject> customObject = null;
		try {
			ObjectStore store = objectStore;
			SearchSQL sql = new SearchSQL(query);
			SearchScope scope = new SearchScope(store);
			IndependentObjectSet iSet = scope.fetchObjects(sql, null, null, true);
			customObject = new ArrayList<CustomObject>();
			Iterator<?> i = iSet.iterator();
			while (i.hasNext()) {
				object = (CustomObject) i.next();
				customObject.add(object);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return customObject;
	}

	/**
	 * Método que verifica si la conexión sigue activa
	 * 
	 * @return
	 */
	public Boolean isConected() {
		return objectStore.isCurrent();
	}

	/**
	 * Método que expuesto que ejecuta la consulta a Content Engine
	 * 
	 * @param cmd
	 * @param attr
	 *            String[]
	 * @return ArrayList
	 * @throws Exception
	 */
	public List<?> ecmQueryExecute(String cmd, String[] attr, boolean isFolder)
			throws Exception {
		System.out.println("QUERY [" + cmd + "]");
		return (List<?>) (isFolder ? ecmSelectFolder(cmd, attr) : ecmSelect(
				cmd, attr));
	}

	/**
	 * Método que realiza busqueda de documentos tipo "Folder" en CE
	 * 
	 * @param query
	 * @param attr
	 * @return
	 * @throws Exception
	 */
	private ArrayList<?> ecmSelectFolder(String query, String[] attr)
			throws Exception {

		ObjectStore os = objectStore;
		SearchSQL sql = new SearchSQL(query);
		SearchScope scope = new SearchScope(os);
		ArrayList<Properties> ar = new ArrayList<Properties>();

		IndependentObjectSet s = scope.fetchObjects(sql, null, null, true);

		Iterator<?> i = s.iterator();

		while (i.hasNext()) {

			Folder folder = (Folder) i.next();
			Properties p = null;

			for (int x = 0; x < attr.length; x++) {
				p = new Properties();
				if (folder.getProperties().getObjectValue(attr[x]) instanceof Date) {
					Date d = (Date) folder.getProperties().getObjectValue(attr[x]);
					SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
					formato.setTimeZone(TimeZone.getTimeZone("GTM-6"));
					String cadenaFecha = formato.format(d);
					p.setProperty(attr[x], cadenaFecha);
				} else {
					p.setProperty(attr[x], (String) folder.getProperties()
							.getObjectValue(attr[x]));
				}
				ar.add(p);
			}
		}
		return ar;
	}

	/**
	 * Método que realiza busqueda de documentos en CE
	 * 
	 * @param cmd
	 * @param attr
	 * @return
	 * @throws Exception
	 */
	private ArrayList<?> ecmSelect(String query, String[] attr)
			throws Exception {

		ObjectStore os = objectStore;
		SearchSQL sql = new SearchSQL(query);
		SearchScope scope = new SearchScope(os);
		ArrayList<Properties> ar = new ArrayList<Properties>();

		IndependentObjectSet s = scope.fetchObjects(sql, null, null, true);
		Iterator<?> i = s.iterator();
		
		while (i.hasNext()) {
			Properties p = new Properties();
			Document document = (Document) i.next();

			for (int x = 0; x < attr.length; x++) {
				if (document.getProperties().getObjectValue(attr[x]) instanceof StringListImpl) {

					String valor = "";
					String separator = "";

					StringListImpl cl = (StringListImpl) document
							.getProperties().getObjectValue(attr[x]);

					if (cl.size() > 1)
						separator = ",";

					for (int a = 0; a < cl.size(); a++) {
						valor += cl.get(a).toString() + separator;
					}

					p.setProperty(attr[x], valor);

				} else if (document.getProperties().getObjectValue(attr[x]) instanceof Date) {

					Date d = (Date) document.getProperties().getObjectValue(attr[x]);
					SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
					formato.setTimeZone(TimeZone.getTimeZone("GTM-6"));
					String cadenaFecha = formato.format(d);
					p.setProperty(attr[x], cadenaFecha);

				} else
					p.setProperty(attr[x], validanull(document.getProperties()
							.getObjectValue(attr[x]) + ""));
			}
			
			// Generar link hacia el documento
			p.setProperty(
					"Link",
					getWorkplaceContext()
							+ "getContent?id=release&vsId="
							+ P8Toolkit.encodeURL(document
									.get_VersionSeries().get_Id()
									.toString()) + "&objectStoreName="
							+ os.get_Name() + "&objectType=document&ut="
							+ getUserToken());
			
			ar.add(p);
		}
		return ar;
	}

	/**
	 * Valida si una expreción contiene null
	 * 
	 * @param valor
	 *            {@link String}
	 * @return {@link String}
	 */
	private String validanull(String expresion) {
		return "null".equals(expresion) ? "" : expresion;
	}

	/**
	 * Obtiene el contenido de un documento.
	 * 
	 * @param id
	 *            {@link String}
	 * @param filename
	 *            {@link String}
	 * @return {@link String}
	 */
	public String getContent(String id, String filename) throws Exception {
		if (isConected()) {
			Document document = fetchDocById(objectStore, id);
			writeDocContentToFile(document, filename);
		} else
			throw new Exception("Error al obtener conexión FILENET.");
		return filename;
	}

	/**
	 * Método que recupera un Documento del Content Engine de Filenet en base al
	 * ID de dicho documento
	 * 
	 * @param os
	 * @param id
	 * @return
	 */
	private Document fetchDocById(ObjectStore os, String id) {
		Id id1 = new Id(id);
		Document doc = Factory.Document.fetchInstance(os, id1, null);
		return doc;
	}

	/**
	 * Método que guarda un Documento recuperado del Content Engine en la ruta
	 * especificada.
	 * 
	 * @param doc
	 * @param pathfilename
	 */
	private void writeDocContentToFile(Document doc, String pathfilename) {
		File f = new File(pathfilename);
		InputStream is = doc.accessContentStream(0);
		int c = 0;
		try {
			FileOutputStream out = new FileOutputStream(f);
			c = is.read();
			while (c != -1) {
				out.write(c);
				c = is.read();
			}
			out.flush();
			is.close();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Método para cerrar la conexión con FileNet
	 */
	private void logOutContentEngine() {
		try {
			uc.popSubject();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Método para conectarse a Content Engine de FileNet.
	 * 
	 * @param contex
	 * @return
	 */
	private Boolean loginContentEngine(Configuration contex) {
		Boolean isConnected = false;
		try {
			
			con = Factory.Connection.getConnection(contex.getUri());
			sub = UserContext.createSubject(con, contex.getUsuario(),contex.getPassword(), contex.getStanza());
			uc = UserContext.get();
			uc.pushSubject(sub);
			dom = Factory.Domain.fetchInstance(con, null, null);
			objectStore = Factory.ObjectStore.fetchInstance(dom,contex.getObjectStore(), null);
			isConnected = true;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isConnected;
	}

	public static Configuration loadProperties(String context) throws Exception {
		Configuration apContext = null;
			
			propiedades = INAPUtil.loadProperties(context + INAPConstant.PROPERTIES);

			if (propiedades.isEmpty()){
				throw new Exception("Error al realizar la carga del archivo de configuracion. [" 
									+ context + INAPConstant.PROPERTIES + "]");
				}else{
					apContext = new Configuration();
					
					apContext.setObjectStore(propiedades.getProperty("OBJECSTORE", ""));
					apContext.setStanza(propiedades.getProperty("STANZA", ""));
					apContext.setPassword(propiedades.getProperty("PWD", ""));
					apContext.setUsuario(propiedades.getProperty("USR", ""));
					apContext.setUri(propiedades.getProperty("URI", ""));
					apContext.setWorkplaceContext(propiedades.getProperty("WORKPLACECONTEXT", ""));
					apContext.setUserToken(P8Toolkit.getUserToken(propiedades.getProperty("USR", ""), propiedades.getProperty("PWD", ""), context));
				}
			
		return apContext;
	}

}
