package mx.com.sacs.bulkloader.util;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.text.SimpleDateFormat;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;

import org.apache.log4j.Logger;

import com.filenet.api.collection.ContentElementList;
import com.filenet.api.constants.AutoClassify;
import com.filenet.api.constants.AutoUniqueName;
import com.filenet.api.constants.CheckinType;
import com.filenet.api.constants.DefineSecurityParentage;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.ContentTransfer;
import com.filenet.api.core.CustomObject;
import com.filenet.api.core.Document;
import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.Factory.BooleanList;
import com.filenet.api.core.Factory.DateTimeList;
import com.filenet.api.core.Factory.Float64List;
import com.filenet.api.core.Factory.StringList;
import com.filenet.api.core.Folder;
import com.filenet.api.core.IndependentlyPersistableObject;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.core.ReferentialContainmentRelationship;
import com.filenet.api.core.UpdatingBatch;
import com.filenet.api.exception.EngineRuntimeException;
import com.filenet.api.util.Id;

import mx.com.sacs.bulkloader.bean.*;
import mx.com.sacs.bulkloader.constants.BulkLoaderConstants;

/**
 * This class provides static methods to make API calls to Content Engine and
 * also some utility methods.
 */
@SuppressWarnings("unchecked")
public class BulkLoaderUtil {
	private static ObjectStore os;
	private static int batchSize;
	private static int batchCount;
	private static SimpleDateFormat sdf;
	
	private static String folDelimiterValue;
	private static String docDelimiterValue;
	
	private static Logger log = Logger.getLogger(BulkLoaderUtil.class);


	/*
	 * Sets the user-selected object store from which folders will be retrieved.
	 */
	public static void setOs(ObjectStore os) {
		BulkLoaderUtil.os = os;
	}

	/*
	 * Returns the current Object Store.
	 */
	public static ObjectStore getOs() {
		return os;
	}

	/*
	 * API call to Content Engine to fetch Folder instance from given Object
	 * Store at given folder path.
	 */
	public static Folder fetchFolder(ObjectStore os, String folderPath) {
		try {
			Folder f = Factory.Folder.fetchInstance(os, folderPath, null);
			return f;
		} catch (EngineRuntimeException ere) {
			return null;
		}
	}

	/*
	 * Creates the Folder object at specified path using specified name.
	 */
	public static Folder createFolder(ObjectStore os, String fPath, String fName) {
		Folder f = Factory.Folder.fetchInstance(os, fPath, null);
		Folder nf = f.createSubFolder(fName);
		return nf;
	}

	/**
	 * Obtener los elementos para generar los Folders
	 * @throws Exception 
	 */
	private static List<String> getFolderElements(FolderClassBean folderBean, List<String> subFolderElements, String[] rowValues) throws Exception{
		List<String> folderElements = new ArrayList<String>();
		
		// Es necesario crear folders personalizados
		if(folderBean.getCreatePersonalFolder()){
			StringTokenizer tokenizer = new StringTokenizer(folderBean.getPathPositions(), "/");
			while (tokenizer.hasMoreElements()) {
				String position = tokenizer.nextToken();
				if(position != null && !position.isEmpty()){
					int i = Integer.parseInt(position);
					if (rowValues.length >= i) {
						String folderName = rowValues[i];
						folderElements.add(folderName.replace(folDelimiterValue, ","));
					}else
						throw new Exception("Ocurrió un error al intentar obtener el 'FolderPath', la posicion " + i + " no existe en el CSV.");
				}
			}
		}else
			folderElements = subFolderElements;
		
		return folderElements;
	}
	
	/*
	 * Creates the Folder object at specified path using specified name.
	 */
	public static Folder createFolderStructureBySegments(ObjectStore os,File docFile, String currentOSFolderPath,List<String> subFolderElements,
			FolderClassesBean folderClassesBean, String[] rowValues)
			throws Exception {
		
		Folder nf = null;
		Folder f = null;
		
		FolderClassBean folderBean = folderClassesBean.getfolderClasses().get(0);
		folDelimiterValue = folderBean.getDelimiterValues();
		
		List<String> elements = getFolderElements(folderBean, subFolderElements, rowValues);
		
			for (int i = 0; i < elements.size(); i++) {
				f = fetchFolder(os,currentOSFolderPath + "/" + elements.get(i));
				if (f == null) {
					// Set Folder Class and Properties
					String currentSegment = getCurrentSegment(elements,elements.get(i));
					FolderClassBean folderClassBean = getFolderClassBeanBySegment(currentSegment, folderClassesBean);
					f = Factory.Folder.fetchInstance(os, currentOSFolderPath, null);

					if (folderClassBean == null) {
						nf = f.createSubFolder(elements.get(i));
					} else {
						nf = Factory.Folder.createInstance(os, folderClassBean.getName());
						BulkLoaderUtil.setProperties(docFile, nf.getProperties(), folderClassBean.getProperties(), rowValues);
						nf.set_Parent(f);
						nf.set_FolderName(elements.get(i));
					}
					nf.save(RefreshMode.REFRESH);
				} else {
					nf = f;
				}

				if (currentOSFolderPath.equals("/"))
					currentOSFolderPath = currentOSFolderPath
							+ elements.get(i);
				else
					currentOSFolderPath = currentOSFolderPath + "/"
							+ elements.get(i);
			}
		return nf;
	}

	public static Document getDocument(ObjectStore os, String documentId) throws Exception {
		Document doc = null;
		if (documentId == null)
			return null;
		try {
			if (Id.isId(documentId))
				doc = Factory.Document.fetchInstance(os, new Id(documentId),null);
			else
				doc = Factory.Document.fetchInstance(os, documentId, null);
		} catch (Exception e) {
			return null;
		}
		return doc;
	}

	@SuppressWarnings("unused")
	public static Document createDocument(File f, ObjectStore os, DocClassesBean docClassesBean, String[] rowValues) throws Exception {
		
		DocClassBean docClassBean = getDocumentClassBean(docClassesBean, rowValues);
		docDelimiterValue = docClassBean.getDelimiterValues();
		
		if (docClassBean == null)
			return null;

		Document doc = Factory.Document.createInstance(os, docClassBean.getName());
		BulkLoaderUtil.setProperties(f, doc.getProperties(), docClassBean.getProperties(), rowValues);
		ContentElementList cel = BulkLoaderUtil.createContentElements(f);
		if (cel != null)
			doc.set_ContentElements(cel);
		doc.checkin(AutoClassify.DO_NOT_AUTO_CLASSIFY, CheckinType.MAJOR_VERSION);
		return doc;
	}

	/*
	 * Files the Containable object (i.e. Document,CustomObject) in specified
	 * folder.
	 */
	public static ReferentialContainmentRelationship fileDocument(Document d,
			String docName, Folder fo) {
		ReferentialContainmentRelationship rcr;
		rcr = fo.file(d, AutoUniqueName.AUTO_UNIQUE, docName,
				DefineSecurityParentage.DO_NOT_DEFINE_SECURITY_PARENTAGE);
		return rcr;
	}

	/*
	 * Creates the UpdatingBatch object.
	 */
	public static UpdatingBatch createBatch(Domain dom) {
		UpdatingBatch ub = UpdatingBatch.createUpdatingBatchInstance(dom,
				RefreshMode.REFRESH);
		return ub;
	}

	/*
	 * Populates the UpdatingBatch object with the supplied
	 * IndependentlyPersistableObject instance.
	 */
	public static void populateBatch(UpdatingBatch ub, IndependentlyPersistableObject value) {
		ub.add(value, null);
	}

	/*
	 * Returns the batch size.
	 */
	public static int getBatchSize() {
		return batchSize;
	}

	/*
	 * Sets the batch size.
	 */
	public static void setBatchSize(int batchSize) {
		BulkLoaderUtil.batchSize = batchSize;
	}

	/*
	 * Returns the batch count.
	 */
	public static int getBatchCount() {
		return batchCount;
	}

	/*
	 * Sets the batch count.
	 */
	public static void setBatchCount(int batchCount) {
		BulkLoaderUtil.batchCount = batchCount;
	}

	public static String normalizePathSeparators(String filePath) {
		filePath = filePath.replace("\\", File.separator);
		filePath = filePath.replace("/", File.separator);
		filePath = filePath.replace("..", "");
		return filePath;
	}

	public static List<String> getSubFolderElements(String filePath) throws Exception {
		List<String> subFolderElements = new ArrayList<String>();
		java.util.StringTokenizer st = new java.util.StringTokenizer(filePath,File.separator);
		while (st.hasMoreElements()) {
			String element = (String) st.nextElement();
			// Since last element should be the filename, this is not added
			if (st.hasMoreElements())
				subFolderElements.add(element);
		}
		return subFolderElements;
	}

	public static int getDocumentFilePosition(String[] rowValues, DocClassesBean docClassesBean) throws Exception {
		DocClassBean docClassBean;
		int classCodePos = docClassesBean.getPos();
		int defaultDocFilePos = 0;
		List<DocClassBean> docClassBeanList = docClassesBean.getDocClasses();
		for (int i = 0; i < docClassBeanList.size(); i++) {
			docClassBean = docClassBeanList.get(i);
			if (docClassBean.getClassCode().equals(
					docClassesBean.getDefaultClassCode()))
				defaultDocFilePos = docClassBean.getFilePos();
			if (docClassBean.getClassCode().equals(rowValues[classCodePos]))
				return docClassBean.getFilePos();
		}
		return defaultDocFilePos;
	}

	public static int getNumberPages(String[] rowValues, DocClassesBean docClassesBean) throws Exception {
		List<DocClassBean> docClassBeanList = docClassesBean.getDocClasses();
		for (DocClassBean docClassBean : docClassBeanList) {
			if (docClassBean.getPagesPos() != BulkLoaderConstants.PAGES_POSITION)
				return docClassBean.getPagesPos();
		}
		return BulkLoaderConstants.PAGES_POSITION;
	}

	public static PropertyDefinitionBean getPropertyDefinition(File f, String[] rowValues, DocClassesBean docClassesBean, String propDefinitionName) throws Exception {
	
		DocClassBean docClassBean = getDocumentClassBean(docClassesBean, rowValues);
		if (docClassBean == null)
			return null;

		List<PropertyBean> propList = docClassBean.getProperties();
		for (int i = 0; i < propList.size(); i++) {
			PropertyBean prop = propList.get(i);
			if (prop.getName().equalsIgnoreCase(propDefinitionName)) {
				return BulkLoaderUtil.getPropertyDefinition(f, prop, rowValues);
			}

		}
		return null;
	}

	public static DocClassBean getDocumentClassBean(DocClassesBean docClassesBean, String[] rowValues) {
		DocClassBean docClassBean = null;
		int classCodePos = docClassesBean.getPos();
		DocClassBean defaultDocClass = null;
		List<DocClassBean> docClassBeanList = docClassesBean.getDocClasses();
		for (int i = 0; i < docClassBeanList.size(); i++) {
			docClassBean = docClassBeanList.get(i);
			if (docClassBean.getClassCode().equals(docClassesBean.getDefaultClassCode()))
				defaultDocClass = docClassBean;
			if (docClassBean.getClassCode().equals(rowValues[classCodePos]))
				return docClassBean;
		}
		return defaultDocClass;
	}

	public static FolderClassBean getFolderClassBeanBySegment(String currentSegment, FolderClassesBean folderClassesBean) {
		FolderClassBean toReturn = null;
		FolderClassBean folderClassBean;
		List<FolderClassBean> folClassBeanList = folderClassesBean.getfolderClasses();
		for (int i = 0; i < folClassBeanList.size(); i++) {
			folderClassBean = folClassBeanList.get(i);
			if (folderClassBean.getSegment().equalsIgnoreCase(currentSegment))
				toReturn = folderClassBean;
		}
		return toReturn;
	}

	public static SimpleDateFormat getSdf() {
		return sdf;
	}

	public static void setSdf(String mask) {
		sdf = new SimpleDateFormat(mask);
	}
	
	private static PropertyDefinitionBean getPropertyDefinition(File f,	PropertyBean propBean, String[] rowValues) throws Exception {
		
		PropertyDefinitionBean propDefinitionBean = null;
		// Get values by positionsw
		String propertyValue = "";
		java.util.StringTokenizer stPos = new java.util.StringTokenizer(propBean.getPos(), "+");
		
		while (stPos.hasMoreElements()) {
		
			String posStr = (String) stPos.nextElement();
			if (posStr != null && !posStr.trim().equals("") && !posStr.contains("[")) {
				int currentPos = Integer.parseInt(posStr);
				if (rowValues.length >= currentPos) {
					String currentVal = rowValues[currentPos];
					if (propertyValue.length() > 0){
						propertyValue += " " + currentVal;
					} else
						propertyValue = currentVal;
				}
			} else {
				int inicio, fin;
				inicio = posStr.indexOf("[");
				fin = posStr.lastIndexOf("]");
				
				String value = posStr.substring(++inicio, fin);
				propertyValue += value;
			}
		}
		
		// Set default value
		propDefinitionBean = new PropertyDefinitionBean();
		propDefinitionBean.setSymbolicName(propBean.getName());
		
		if (propertyValue.equals("")) {
			propertyValue = propBean.getDefaultValue();
			// Check for valid Macros
			if (propertyValue.equalsIgnoreCase(BulkLoaderConstants.MACRO_FILENAME))
				propertyValue = f.getName();
			else if (propertyValue.equalsIgnoreCase(BulkLoaderConstants.MACRO_FILEPATH))
				propertyValue = f.getPath();
		}

		List<String> propValues;
		// Set property object value
		
		if(propBean.isRequired() && propertyValue.equals("")){
			throw new Exception("Ocurrió un error, el valor de " + propDefinitionBean.getSymbolicName() + " es requerido.");
		}
		
		if (propBean.getType().equalsIgnoreCase(BulkLoaderConstants.TYPE_INTEGER)) {
			propDefinitionBean.setDataType(BulkLoaderConstants.TYPE_INTEGER);
			
			if (propBean.isMultivalue()) {
				propValues = getListValue(propertyValue);
				List<Integer> defitionValues = com.filenet.api.core.Factory.Integer32List.createList();
				for (String value : propValues){
					defitionValues.add(Integer.parseInt(value));
				}
				propDefinitionBean.setValue(defitionValues);
			} else {
				propDefinitionBean.setValue(Integer.parseInt(propertyValue));
			}
		} else if (propBean.getType().equalsIgnoreCase(
				BulkLoaderConstants.TYPE_FLOAT)) {
			propDefinitionBean.setDataType(BulkLoaderConstants.TYPE_FLOAT);
			if (propBean.isMultivalue()) {
				propValues = getListValue(propertyValue);
				List<Float> defitionValues = Float64List.createList();
				for (String value : propValues) {
						defitionValues.add(Float.parseFloat(value));
					}
				propDefinitionBean.setValue(defitionValues);
			} else {
				propDefinitionBean.setValue(Float.parseFloat(propertyValue));
			}
		} else if (propBean.getType().equalsIgnoreCase(BulkLoaderConstants.TYPE_BOOLEAN)) {
			propDefinitionBean.setDataType(BulkLoaderConstants.TYPE_BOOLEAN);
			if (propBean.isMultivalue()) {
				propValues = getListValue(propertyValue);
				List<Boolean> defitionValues = BooleanList.createList();
					for (String value : propValues) {
						defitionValues.add(Boolean.parseBoolean(value));
					}
					propDefinitionBean.setValue(defitionValues);
			} else {
				propDefinitionBean.setValue(Boolean.parseBoolean(propertyValue));
			}
		} else if (propBean.getType().equalsIgnoreCase(BulkLoaderConstants.TYPE_DATETIME)) {
			propDefinitionBean.setDataType(BulkLoaderConstants.TYPE_DATETIME);
			if (propBean.isMultivalue()) {
				propValues = getListValue(propertyValue);
				List<Date> defitionValues = DateTimeList.createList();
					for (String value : propValues) {
					defitionValues.add(BulkLoaderUtil.getSdf().parse(value));
					}
					propDefinitionBean.setValue(defitionValues);
			} else {
				propDefinitionBean.setValue(BulkLoaderUtil.getSdf().parse(propertyValue));
			}
		} else {
			propDefinitionBean.setDataType(BulkLoaderConstants.TYPE_STRING);
			if (propBean.isMultivalue()) {
				propValues = getListValue(propertyValue);
				List<String> defitionValues = StringList.createList();
					for (String value : propValues) {
							defitionValues.add(value.trim());						
					}
					propDefinitionBean.setValue(defitionValues);
			} else {
					propDefinitionBean.setValue(propertyValue);
			}
		}
		
		/**********************************************************************
		 *      GENERANDO LOS CUSTOM OBJECTS PARA LAS BUSQUEDAS INDEXADAS
		 **********************************************************************/
		if(propBean.createIndex()){
			CustomObjectClassBean customObject = propBean.getCustomObject();
			createIndexObjects(customObject, rowValues);
		}
		
		return propDefinitionBean;
	}
	
	private static void createIndexObjects(CustomObjectClassBean customObjectBean, String[] rowValues) throws Exception{
	
		PropertyDefinitionBean propDefinitionBean = null;
		List<Object> finalValue = new ArrayList<Object>();

		for (PropertyBean property : customObjectBean.getProperties()) {
			
			String propertyValue = "";
			java.util.StringTokenizer stPos = new java.util.StringTokenizer(property.getPos(), "+");
			while (stPos.hasMoreElements()) {
				String posStr = (String) stPos.nextElement();
				if (posStr != null && !posStr.trim().equals("")) {
					int currentPos = Integer.parseInt(posStr);
					if (rowValues.length >= currentPos) {
						String currentVal = rowValues[currentPos];
						if (propertyValue.length() > 0)
							propertyValue += " " + currentVal;
						else
							propertyValue = currentVal;
					}
				}
			}
			
			
			propDefinitionBean = new PropertyDefinitionBean();
			propDefinitionBean.setSymbolicName(property.getName());
			
			List<String> propValues;

			// Set property object value
			if (property.getType().equalsIgnoreCase(BulkLoaderConstants.TYPE_INTEGER)) {
				propDefinitionBean.setDataType(BulkLoaderConstants.TYPE_INTEGER);
				if (property.isMultivalue()) {
					propValues = getListValue(propertyValue);
					if(propValues != null){
						for (String value : propValues) {
							finalValue.add(Integer.parseInt(value));
						}
					}else
						finalValue = null;
				} else
					finalValue.add(Integer.parseInt(propertyValue));			
			} else if (property.getType().equalsIgnoreCase(
					BulkLoaderConstants.TYPE_FLOAT)) {
				propDefinitionBean.setDataType(BulkLoaderConstants.TYPE_FLOAT);
				if (property.isMultivalue()) {
					propValues = getListValue(propertyValue);
					if(propValues != null){
						for (String value : propValues) {
							finalValue.add(Float.parseFloat(value));
						}
					}else
						finalValue = null;
				} else
					finalValue.add(Float.parseFloat(propertyValue));			
			} else if (property.getType().equalsIgnoreCase(BulkLoaderConstants.TYPE_BOOLEAN)) {
				propDefinitionBean.setDataType(BulkLoaderConstants.TYPE_BOOLEAN);
				if (property.isMultivalue()) {
					propValues = getListValue(propertyValue);
					if(propValues!=null){
						for (String value : propValues) {
							finalValue.add(Boolean.parseBoolean(value));
						}
					}else
						finalValue = null;
				} else
					finalValue.add(Boolean.parseBoolean(propertyValue));			
			} else if (property.getType().equalsIgnoreCase(BulkLoaderConstants.TYPE_DATETIME)) {
				propDefinitionBean.setDataType(BulkLoaderConstants.TYPE_DATETIME);
				if (property.isMultivalue()) {
					propValues = getListValue(propertyValue);
					if(propValues != null){
						for (String value : propValues) {
							finalValue.add(BulkLoaderUtil.getSdf().parse(value));
						}
					}else
						finalValue = null;
				}else
					finalValue.add(BulkLoaderUtil.getSdf().parse(propertyValue));
			} else {
				propDefinitionBean.setDataType(BulkLoaderConstants.TYPE_STRING);
				if (property.isMultivalue()) {
					propValues = getListValue(propertyValue);
					if(propValues != null){
						for (String value : propValues) {
							finalValue.add(value.trim());
						}
					}else
						finalValue = null;
				}else
					finalValue.add(propertyValue.trim());
			}
		}
		
		if(finalValue != null){
			for (Object object : finalValue) {
				
				Folder folder= Factory.Folder.fetchInstance(os,customObjectBean.getFolder(), null);
				String path = customObjectBean.getFolder() + "/" + object.toString();
				
				CustomObject isDuplicate = getCustomObject(path);
				
				if(isDuplicate == null){
					CustomObject customObject = Factory.CustomObject.createInstance(os, customObjectBean.getClassCode());
					customObject.getProperties().putObjectValue(propDefinitionBean.getSymbolicName(), object);
					
					customObject.save(RefreshMode.REFRESH);	
					ReferentialContainmentRelationship rel =  folder.file(customObject, AutoUniqueName.AUTO_UNIQUE, object.toString(), 
							DefineSecurityParentage.DO_NOT_DEFINE_SECURITY_PARENTAGE);
					rel.save(RefreshMode.NO_REFRESH);
					
					log.debug("Indice (" + customObject.getClassName() + "): " + object.toString() + " creado.");
				}
			}
		}
	}
	
	private static CustomObject getCustomObject(String folderPath){
		CustomObject customObject = null;
		try{
			if (Id.isId(folderPath)){
				customObject = Factory.CustomObject.fetchInstance(os, new Id(folderPath),null);
			}else{
				customObject = Factory.CustomObject.fetchInstance(os, folderPath, null);
			}
		} catch (Exception e) {
			return null;
		}
		
		return customObject;
	}
	
	private static List<String> getListValue(String value) {
		List<String> lista = new ArrayList<String>();
		java.util.StringTokenizer tokenizer = new StringTokenizer(value, docDelimiterValue);

		while (tokenizer.hasMoreElements()) {
			lista.add(tokenizer.nextToken());
		}
		
		return lista;
	}

	private static String getCurrentSegment(List<String> subFolderElements,
			String currentElement) {
		String currentSegment = null;
		for (int i = 0; i < subFolderElements.size(); i++) {
			if (subFolderElements.get(i).equals(currentElement)) {
				if (i == 0) {
					currentSegment = BulkLoaderConstants.SEGMENT_FIRST;
					break;
				} else if (i == (subFolderElements.size() - 1)) {
					currentSegment = BulkLoaderConstants.SEGMENT_LAST;
					break;
				} else {
					currentSegment = BulkLoaderConstants.SEGMENT_BETWEEN;
					break;
				}
			}
		}
		return currentSegment;
	}

	private static void setProperties(File f, com.filenet.api.property.Properties properties,
			List<PropertyBean> propertyBeanList, String[] rowValues)
			throws Exception {
		for (int j = 0; j < propertyBeanList.size(); j++) {
			PropertyBean propBean = propertyBeanList.get(j);
			PropertyDefinitionBean propDefinitionBean = getPropertyDefinition(f, propBean, rowValues);
			if (propDefinitionBean != null)
				properties.putObjectValue(propDefinitionBean.getSymbolicName(),	propDefinitionBean.getValue());
		}
	}

	/*
	 * Reads the content from a file and stores it in byte array. The byte array
	 * will later be used to create ContentTransfer object.
	 */
	private static byte[] readDocContentFromFile(File f) throws Exception {
		FileInputStream is;
		byte[] b = null;
		int fileLength = (int) f.length();
		if (fileLength != 0) {
			is = new FileInputStream(f);
			b = new byte[fileLength];
			is.read(b);
			is.close();
		}
		return b;
	}

	/*
	 * Creates the ContentTransfer object from supplied file's content.
	 */
	private static ContentTransfer createContentTransfer(File f)
			throws Exception {
		ContentTransfer ctNew = null;
		if (readDocContentFromFile(f) != null) {
			ctNew = Factory.ContentTransfer.createInstance();
			ByteArrayInputStream is = new ByteArrayInputStream(
					readDocContentFromFile(f));
			ctNew.setCaptureSource(is);
			ctNew.set_RetrievalName(f.getName());
		}
		return ctNew;
	}

	/*
	 * Creates the ContentElementList from ContentTransfer object.
	 */
	private static ContentElementList createContentElements(File f)
			throws Exception {
		ContentElementList cel = null;
		if (createContentTransfer(f) != null) {
			cel = Factory.ContentElement.createList();
			ContentTransfer ctNew = createContentTransfer(f);
			cel.add(ctNew);
		}
		return cel;
	}
	
	

}
