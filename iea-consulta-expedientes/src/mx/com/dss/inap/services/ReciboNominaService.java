package mx.com.dss.inap.services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import com.filenet.api.core.CustomObject;

import mx.com.dss.inap.model.Indice;
import mx.com.dss.inap.model.ReciboNomina;
import mx.com.dss.inap.util.INAPConstant;

public class ReciboNominaService {
	
	private StringBuffer whereClause = new StringBuffer();
	private StringBuffer sqlQuery = new StringBuffer();
	
	private String selectClause, fromClause;
	
	ContentEngineServices services = null;
	List<?> listDocumentos = null;
	
	ReciboNomina nomina = new ReciboNomina();
	
	public List<ReciboNomina> searchReciboNomina(ReciboNomina nomina, String pathName, String cmd, String contextPath, Boolean isVIP){
		List<ReciboNomina> recibosNomina = null; 
		try {
			services = new ContentEngineServices(contextPath);
			if(services.isConected()){
				recibosNomina = searchDocuments(nomina, pathName, cmd, isVIP);
				services.doDisconnect();
			}else{
				throw new Exception("Ocurrió un error al conectarse a Content Engine.");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return recibosNomina;
	}
	
	private List<ReciboNomina> searchDocuments(ReciboNomina nomina, String pathName, String cmd, Boolean isVIP){
		
		listDocumentos = new ArrayList<Object>();
		List<ReciboNomina> recibosNomina = null;

		try {
			listDocumentos = execute(nomina, pathName, cmd, isVIP);
			recibosNomina = generateResult(listDocumentos);

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return recibosNomina;
	}
	
	private List<ReciboNomina> generateResult(List<?> listDocumentos) {
		List<ReciboNomina> result = null;
		if(listDocumentos != null){
			result =  new ArrayList<ReciboNomina>();
			for (Iterator<?> iterator = listDocumentos.iterator(); iterator.hasNext();) {
				Properties nomina = (Properties) iterator.next();
				ReciboNomina reciboNomina = new ReciboNomina();
				reciboNomina.setAnyo(Integer.parseInt(nomina.getProperty("Anyo", "0")));
				reciboNomina.setQuincenas(obtenerElementos((nomina.getProperty("Quincena", "0"))));
				reciboNomina.setName(nomina.getProperty("Name", ""));
				reciboNomina.setLink(nomina.getProperty("Link", ""));
				reciboNomina.setCentrosTrabajo(obtenerElementos(nomina.getProperty("CentroTrabajo", "")));
				reciboNomina.setTipoIngreso(nomina.getProperty("TipodeIngreso", ""));
				result.add(reciboNomina);
			}
		}
		return result;
	}
	
	private String[] propiedades() {
		return new String[] { "Id", "Name", "Anyo", "Quincena", 
				"CentroTrabajo", "TipodeIngreso" };
	}
	
	private List<?> execute(ReciboNomina nomina, String pathName, String cmd, Boolean isVIP) {
		
		
		try {
				generateQuery(nomina, cmd, isVIP);
				listDocumentos = services.ecmQueryExecute(sqlQuery.toString(), propiedades(), false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listDocumentos;
		
	}
	
	private void generateQuery(ReciboNomina nomina, String cmd, Boolean isVIP){
	
		selectClause = 
			"SELECT" +
				" d.This, d.Id, d.DateCreated, d.Name, d.Anyo, d.Quincena," +
				" d.CentroTrabajo, d.TipodeIngreso, d.VersionSeries ";
		
		fromClause = 
			"FROM" +
				" DocumentoReciboNomina d ";
		
		if(cmd == null || cmd.isEmpty()){
			whereClause.append("WHERE d.Anyo = " + nomina.getAnyo().toString() + " ");
		}else{
			whereClause.append("INNER JOIN ContentSearch c on This = c.QueriedObject ");
			whereClause.append("WHERE CONTAINS(d.*, '" + cmd + "') ");
			whereClause.append("AND d.Anyo = " + nomina.getAnyo().toString() + " ");
		}
		
		if(nomina.getQuincenas() != null){
			for (String quincena : nomina.getQuincenas()) {
				whereClause.append("AND '" + quincena + "' in d.Quincena ");
			}
		}
		
		if(nomina.getCentrosTrabajo() != null){
			for (String centroTrabajo : nomina.getCentrosTrabajo()) {
				whereClause.append("AND '" + centroTrabajo + "' in d.CentroTrabajo ");
			}
		}
		
		whereClause.append("AND d.IsCurrentVersion = TRUE"); //+ INAPConstant.FILTROADICIONAL);
		
		if(!isVIP)
			whereClause.append(" AND (VIP <> TRUE OR VIP is null)");
		
		whereClause.append(" ORDER BY d.DateCreated");
		
		sqlQuery.append(selectClause);
		sqlQuery.append(fromClause);
		sqlQuery.append(whereClause.toString());
		
	}

	public static List<String> obtenerElementos(String contenido){
		
		List<String> listaElementos = new ArrayList<String>();
		StringTokenizer tokenizer = new StringTokenizer(contenido, ",");
		
		if(tokenizer.countTokens()>0){
			while (tokenizer.hasMoreElements()) {
				listaElementos.add(tokenizer.nextToken());
			}
		}else{
			listaElementos.add(contenido);
		}
		
		return listaElementos;
	}
	
	public List<Indice> obtenerIndices(String contextPath, String criterio, String indexType){
		
		List<Indice> indices = null;
		
		try {
			services = new ContentEngineServices(contextPath);
			
			if(services.isConected())
				indices = obtenerIndicces(indexType, criterio);
			else
				new Exception("Ocurrió un error al intentar conectarse al repositorio.");
			
		} catch (Exception e) {
			new Exception("Ocurrió un error al intentar la busqueda de indices. [" + indexType + "]");
		}
		
		return indices;
	}
	
	private List<Indice> obtenerIndicces(String indexType, String criterio){
		
		Indice indice = null;
		List<Indice> indexList = null;
		StringBuffer query = new StringBuffer();
		
		if(indexType.equals("Quincena")){
			query.append("SELECT This, IndexStr FROM " + indexType + " WHERE IndexStr like '%");
			query.append(criterio);
			query.append( "%' ORDER BY IndexStr");
			
		} else if(indexType.equals("CentroTrabajo")){
			query.append("SELECT [This], [IndexStr] FROM [" + indexType + "] WHERE ([IndexStr] like '%");
			query.append(criterio);
			query.append( "%') ORDER BY IndexStr");
		} else
			return null;
		
	List<CustomObject> customObjects = services.ecmSelectCustomObject(query.toString());
		if(customObjects != null){
			indexList = new ArrayList<Indice>();
			for (CustomObject customObject : customObjects) {
				indice = new Indice();
				indice.setIndex(customObject.getProperties().getStringValue("IndexStr"));
				indexList.add(indice);
			}
		}
		
		return indexList;
	}
	
}
