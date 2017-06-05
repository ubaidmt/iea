package mx.com.dss.inap.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import mx.com.dss.inap.model.CpitalHumano;
import mx.com.dss.inap.util.INAPConstant;
import mx.com.dss.inap.util.INAPUtil;

public class CapitalHumanoServices {

	ContentEngineServices ceServices = null;
	List<?> listDocs;

	public List<CpitalHumano> searchDocuent(String cmd, String rfc,
			String contextPath, Boolean isVIP) {
		List<CpitalHumano> listaDocumentos = null;
		try {
			ceServices = new ContentEngineServices(contextPath);
			if (ceServices.isConected()) {
				if (cmd == null || cmd.equals("")) {
					listaDocumentos = searchFolder(rfc, isVIP);
				} else {
					listaDocumentos = searchDocument(rfc, cmd, isVIP);
				}
				ceServices.doDisconnect();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listaDocumentos;
	}

	private List<CpitalHumano> searchFolder(String rfc, Boolean isVIP) {

		String sqlQuery, pathName = "";
		List<CpitalHumano> listCapitalHumano = null;

		sqlQuery = "SELECT" + " This, FechaNacimiento, Nombre, PATHNAME "
				+ "FROM" + " FolderDocenteRH WHERE (FolderName = '" + rfc
				+ "' )";

		try {
			listDocs = ceServices.ecmQueryExecute(sqlQuery,	new String[] { "PATHNAME" }, true);

			pathName = INAPUtil.getMappPathName(listDocs);

			if (pathName != null && pathName != "") {
				sqlQuery = "SELECT"
						+ " This, ID, DateCreated, Name, Consecutivo, RFC,"
						+ " Nombre, ReferenciaTopografica, FechaNacimiento, TipodeIngreso, VersionSeries "
						+ "FROM" + " DocumentoDocenteRH WHERE This infolder '"
						+ pathName + "' AND IsCurrentVersion = TRUE ";

				sqlQuery += INAPConstant.FILTROADICIONAL;
				sqlQuery += (!isVIP ? " AND (VIP <> TRUE OR VIP is null)" : "");
				sqlQuery += " ORDER BY DateCreated";

				listDocs = ceServices.ecmQueryExecute(sqlQuery, propiedades(),false);
				listCapitalHumano = generateResult(listDocs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listCapitalHumano;
	}

	private List<CpitalHumano> searchDocument(String rfc, String cmd, Boolean isVip) {

		listDocs = new ArrayList<Object>();
		List<CpitalHumano> listCapitalHumano = null;

		cmd = "SELECT"
				+ " d.This, d.Id, d.DateCreated, d.Name, d.Consecutivo, d.FechaNacimiento,"
				+ " d.Nombre, d.ReferenciaTopografica, d.RFC, d.TipodeIngreso, d.VersionSeries "
				+ "FROM DocumentoDocenteRH d"
				+ " INNER JOIN ContentSearch c on d.This = c.QueriedObject"
				+ " WHERE CONTAINS(d.*, '" + cmd + "') AND d.RFC = '" + rfc
				+ "' AND d.IsCurrentVersion = TRUE";

		cmd += INAPConstant.FILTROADICIONAL;
		cmd += (!isVip ? " AND (VIP <> TRUE OR VIP is null)" : "");
		cmd += " ORDER BY d.DateCreated";

		try {
			listDocs = ceServices.ecmQueryExecute(cmd, propiedades(), false);
			listCapitalHumano = generateResult(listDocs);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listCapitalHumano;
	}

	private List<CpitalHumano> generateResult(List<?> documentos) {
	
		List<CpitalHumano> result = null;

		if (!documentos.isEmpty()) {
		
			result = new ArrayList<CpitalHumano>();

			for (int i = 0; i < documentos.size(); i++) {
				
				CpitalHumano capitalHumano = new CpitalHumano();
				Properties docProperties = (Properties) documentos.get(i);
				
				capitalHumano.setRfc(docProperties.getProperty("RFC", ""));
				capitalHumano.setId(docProperties.getProperty("Id", ""));
				capitalHumano.setName(docProperties.getProperty("Name", ""));
				capitalHumano.setLink(docProperties.getProperty("Link", ""));
				capitalHumano.setNombre(docProperties.getProperty("Nombre",	""));
				capitalHumano.setFechaNac(docProperties.getProperty("FechaNacimiento", ""));
				capitalHumano.setTipoIngreso(docProperties.getProperty("TipodeIngreso", ""));
				capitalHumano.setRefTopografica(docProperties.getProperty("ReferenciaTopografica", ""));
				capitalHumano.setConsecutivo(Integer.parseInt(docProperties.getProperty("Consecutivo", "0")));
				
				result.add(capitalHumano);
			}
		}
		return result;
	}

	private String[] propiedades() {
		return new String[] { "Id", "Name", "Consecutivo", "RFC", "Nombre",
				"ReferenciaTopografica", "FechaNacimiento", "TipodeIngreso" };
	}

}
