package mx.com.sacs.bulkloader.util;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import java.util.List;
import java.util.ArrayList;

// Beans
import mx.com.sacs.bulkloader.bean.BulkLoaderBean;
import mx.com.sacs.bulkloader.bean.CEConnectionBean;
import mx.com.sacs.bulkloader.bean.BatchConfigBean;
import mx.com.sacs.bulkloader.bean.CustomObjectClassBean;
import mx.com.sacs.bulkloader.bean.DocClassesBean;
import mx.com.sacs.bulkloader.bean.DocClassBean;
import mx.com.sacs.bulkloader.bean.PropertyBean;
import mx.com.sacs.bulkloader.bean.FolderClassesBean;
import mx.com.sacs.bulkloader.bean.FolderClassBean;

public class XMLConfigUtil {
	
	
	private final static String configFileName = "config/sacs-bulkloader-config.xml";
	
	public static BulkLoaderBean loadXMLConfig() throws Exception {
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		org.w3c.dom.Document doc = dBuilder.parse(new File(configFileName));
		doc.getDocumentElement().normalize();
				
		// Load Batch Configuration Beans
		List<BatchConfigBean> batchConfigList = new ArrayList<BatchConfigBean>();
		Node bulkLoadersNode = doc.getElementsByTagName("bulkloaders").item(0);
		Element bulkLoadersElement = (Element) bulkLoadersNode;		
		NodeList batchConfigNodeList = bulkLoadersElement.getElementsByTagName("batchconfig");
		for (int i = 0;i < batchConfigNodeList.getLength(); i++) {
			BatchConfigBean batchConfigBean = new BatchConfigBean();
			Node nNode = batchConfigNodeList.item(i);
			Element batchConfigElement = (Element) nNode;	
			NodeList batchsettingsNodeList = batchConfigElement.getElementsByTagName("setting");
			for (int j = 0; j < batchsettingsNodeList.getLength(); j++) {
				Node sNode = batchsettingsNodeList.item(j);
				Element settingElement = (Element) sNode;
				if (settingElement.getAttribute("name").equals("baseFolder"))
					batchConfigBean.setBaseFolder(settingElement.getAttribute("value"));
				else if (settingElement.getAttribute("name").equals("delimeter"))
					batchConfigBean.setDelimeter(settingElement.getAttribute("value").charAt(0));				
				else if (settingElement.getAttribute("name").equals("fileReference"))
					batchConfigBean.setFileReference(settingElement.getAttribute("value"));
				else if (settingElement.getAttribute("name").equals("validateDuplicates"))
					batchConfigBean.setValidateDuplicates(Boolean.parseBoolean(settingElement.getAttribute("value")));				
				else if (settingElement.getAttribute("name").equals("containHeader"))
					batchConfigBean.setContainHeader(Boolean.parseBoolean(settingElement.getAttribute("value")));
				else if (settingElement.getAttribute("name").equals("outputFile"))
					batchConfigBean.setOutputFile(settingElement.getAttribute("value"));				
				else if (settingElement.getAttribute("name").equals("osFolder"))
					batchConfigBean.setOsFolder(settingElement.getAttribute("value"));
				else if (settingElement.getAttribute("name").equals("batchSize"))
					batchConfigBean.setBatchSize(Integer.parseInt(settingElement.getAttribute("value")));
				else if (settingElement.getAttribute("name").equals("dateFormat"))
					batchConfigBean.setDateFormat(settingElement.getAttribute("value"));
				else if (settingElement.getAttribute("name").equals("validatePDF"))
					batchConfigBean.setValidatePDF(Boolean.parseBoolean(settingElement.getAttribute("value")));
			}	
			batchConfigList.add(batchConfigBean);
		}

		// Load CE Connection Bean
		CEConnectionBean ceConnBean = new CEConnectionBean();
		Node ceConnNode = doc.getElementsByTagName("ceconnection").item(0);
		Element ceConnElement = (Element) ceConnNode;
		NodeList cesettingsNodeList = ceConnElement.getElementsByTagName("setting");
		for (int i = 0;i < cesettingsNodeList.getLength(); i++) {
			Node nNode = cesettingsNodeList.item(i);
			Element settingElement = (Element) nNode;
			if (settingElement.getAttribute("name").equals("uri"))
				ceConnBean.setUri(settingElement.getAttribute("value"));
			else if (settingElement.getAttribute("name").equals("stanza"))
				ceConnBean.setStanza(settingElement.getAttribute("value"));
			else if (settingElement.getAttribute("name").equals("user"))
				ceConnBean.setUser(settingElement.getAttribute("value"));
			else if (settingElement.getAttribute("name").equals("pass"))
				ceConnBean.setPass(settingElement.getAttribute("value"));
			else if (settingElement.getAttribute("name").equals("os"))
				ceConnBean.setOs(settingElement.getAttribute("value"));	
		}			
		
		// Load Document Classes Bean
		DocClassesBean docClassesBean = new DocClassesBean();
		Node docClassesNode = doc.getElementsByTagName("docclasses").item(0);
		Element docClasessElement = (Element) docClassesNode;
		docClassesBean.setPos(Integer.parseInt(docClasessElement.getAttribute("pos")));
		docClassesBean.setDefaultClassCode(docClasessElement.getAttribute("default"));
		NodeList docClassesNodeList = docClasessElement.getElementsByTagName("docclass");
		List<DocClassBean> docClasses = new ArrayList<DocClassBean>();
		for (int i = 0;i < docClassesNodeList.getLength(); i++) {
			DocClassBean docClassBean = new DocClassBean();
			Node nNode = docClassesNodeList.item(i);
			Element docClassElement = (Element) nNode;
			docClassBean.setName(docClassElement.getAttribute("name"));
			docClassBean.setClassCode(docClassElement.getAttribute("value"));
			docClassBean.setFilePos(Integer.parseInt(docClassElement.getAttribute("filepos")));
			docClassBean.setPagesPos(Integer.parseInt(docClassElement.getAttribute("pagespos")));
			docClassBean.setDelimiterValues(docClassElement.getAttribute("delimiter-values"));
			NodeList propsNodeList = docClassElement.getElementsByTagName("property");
			List<PropertyBean> properties = new ArrayList<PropertyBean>();
			for (int j = 0;j < propsNodeList.getLength(); j++) {
				PropertyBean propBean = new PropertyBean();
				Node pNode = propsNodeList.item(j);
				Element propElement = (Element) pNode;
				propBean.setName(propElement.getAttribute("name"));
				propBean.setType(propElement.getAttribute("type"));
				propBean.setPos(propElement.getAttribute("pos"));
				propBean.setDefaultValue(propElement.getAttribute("default"));
				
				/* VERIFICAMOS SI LA PROPIEDAD ES MULTI-VALOR */
				if(propElement.getAttribute("multi-value").equals("T"))
					propBean.setMultivalue(true);
				else
					propBean.setMultivalue(false);
				
				if(propElement.getAttribute("required").equals("true"))
					propBean.setRequired(true);
				else
					propBean.setRequired(false);
				
				/*VERIFICAMOS SI SE ES NECESARIO LA CREACION DE INDEX PARA BUSQUEDAS */
				if(!propElement.getAttribute("index").isEmpty()){
					propBean.setCreateIndex(true);
					propBean.setCustomObject(getCustomObjectBean(Integer.parseInt(propElement.getAttribute("index")), doc.getElementsByTagName("customobjectclasses").item(0)));
				}else{
					propBean.setCreateIndex(false);
					propBean.setCustomObject(null);
				}
				
				properties.add(propBean);
			}
			docClassBean.setProperties(properties);
			docClasses.add(docClassBean);
		}
		docClassesBean.setDocClasses(docClasses);
		
		// Load Folder Classes Bean
		FolderClassesBean folderClassesBean = new FolderClassesBean();
		Node folderClassesNode = doc.getElementsByTagName("folderclasses").item(0);
		Element folderClasessElement = (Element) folderClassesNode;
		NodeList folderClassesNodeList = folderClasessElement.getElementsByTagName("folderclass");
		
		List<FolderClassBean> folderClasses = new ArrayList<FolderClassBean>();
		
		for (int i = 0;i < folderClassesNodeList.getLength(); i++) {
		
			FolderClassBean folderClassBean = new FolderClassBean();
			Node nNode = folderClassesNodeList.item(i);
			
			Element folderClassElement = (Element) nNode;
			folderClassBean.setName(folderClassElement.getAttribute("name"));
			folderClassBean.setSegment(folderClassElement.getAttribute("segment"));
			folderClassBean.setDelimiterValues(folderClassElement.getAttribute("delimiter-values"));
		
			if(folderClassElement.getAttribute("createPath").equals("true")){
				folderClassBean.setCreatePersonalFolder(true);
				folderClassBean.setPathPositions(folderClassElement.getAttribute("pathPos"));
			}else{
				folderClassBean.setCreatePersonalFolder(false);
				folderClassBean.setPathPositions(null);
			}
			
			NodeList propsNodeList = folderClassElement.getElementsByTagName("property");
			List<PropertyBean> properties = new ArrayList<PropertyBean>();
			for (int j = 0;j < propsNodeList.getLength(); j++) {
				PropertyBean propBean = new PropertyBean();
				Node pNode = propsNodeList.item(j);
				Element propElement = (Element) pNode;
				propBean.setName(propElement.getAttribute("name"));
				propBean.setType(propElement.getAttribute("type"));
				propBean.setPos(propElement.getAttribute("pos"));
				propBean.setDefaultValue(propElement.getAttribute("default"));
				
				/* VERIFICAMOS SI LA PROPIEDAD ES MULTI-VALOR */
				if(propElement.getAttribute("multi-value").equals("T"))
					propBean.setMultivalue(true);
				else
					propBean.setMultivalue(false);
				
				if(propElement.getAttribute("required").equals("true"))
					propBean.setRequired(true);
				else
					propBean.setRequired(false);
				
				/*VERIFICAMOS SI SE ES NECESARIO LA CREACION DE INDEX PARA BUSQUEDAS */
				if(!propElement.getAttribute("index").isEmpty()){
					propBean.setCreateIndex(true);
					propBean.setCustomObject(getCustomObjectBean(Integer.parseInt(propElement.getAttribute("index")), doc.getElementsByTagName("customobjectclasses").item(0)));
				}else{
					propBean.setCreateIndex(false);
					propBean.setCustomObject(null);
				}
				
				properties.add(propBean);
			}

			folderClassBean.setProperties(properties);
			folderClasses.add(folderClassBean);
		}
		folderClassesBean.setfolderClasses(folderClasses);
		
		// Set BulkLoader Bean
		BulkLoaderBean bulkloaderBean = new BulkLoaderBean();
		bulkloaderBean.setCeConnBean(ceConnBean);
		bulkloaderBean.setBatchConfigBeans(batchConfigList);
		bulkloaderBean.setDocClassesBean(docClassesBean);
		bulkloaderBean.setFolderClassesBean(folderClassesBean);
		
		return bulkloaderBean;
				
	}
	
	private static CustomObjectClassBean getCustomObjectBean(int position, Node customObjectsNode){
		
		CustomObjectClassBean customObjectClass = new CustomObjectClassBean();
		Element customClasessElement = (Element) customObjectsNode;
		NodeList customClassesNodeList = customClasessElement.getElementsByTagName("customobjectclass");
		for (int i = 0;i < customClassesNodeList.getLength(); i++) {
			Node nNode = customClassesNodeList.item(i);
			Element docClassElement = (Element) nNode;
			if(Integer.parseInt(docClassElement.getAttribute("value")) == position){
				customObjectClass.setClassCode(docClassElement.getAttribute("name"));
				customObjectClass.setFolder(docClassElement.getAttribute("path"));
				NodeList propsNodeList = docClassElement.getElementsByTagName("property");
				List<PropertyBean> properties = new ArrayList<PropertyBean>();
				for (int j = 0;j < propsNodeList.getLength(); j++) {
					PropertyBean propBean = new PropertyBean();
					Node pNode = propsNodeList.item(j);
					Element propElement = (Element) pNode;
					propBean.setName(propElement.getAttribute("name"));
					propBean.setType(propElement.getAttribute("type"));
					propBean.setPos(propElement.getAttribute("pos"));
				
					/* VERIFICAMOS SI LA PROPIEDAD ES MULTI-VALOR */
					if(propElement.getAttribute("multi-value").equals("T"))
						propBean.setMultivalue(true);
					else
						propBean.setMultivalue(false);
					
					if(propElement.getAttribute("required").equals("true"))
						propBean.setRequired(true);
					else
						propBean.setRequired(false);
					
					properties.add(propBean);
				}
				customObjectClass.setProperties(properties);
			}
		}
		return customObjectClass;
	}
	
}



