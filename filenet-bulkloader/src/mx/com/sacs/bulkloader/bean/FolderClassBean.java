package mx.com.sacs.bulkloader.bean;

import java.util.List;

public class FolderClassBean {

	private String name;
	private String segment;
	private String folderStructure;
	private Boolean createPersonalFolder;
	private List<PropertyBean> properties;
	private String delimiterValues;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSegment() {
		return segment;
	}
	public void setSegment(String segment) {
		this.segment = segment;
	}	
	public List<PropertyBean> getProperties() {
		return properties;
	}
	public void setProperties(List<PropertyBean> properties) {
		this.properties = properties;
	}
	public Boolean getCreatePersonalFolder() {
		return createPersonalFolder;
	}
	public void setCreatePersonalFolder(Boolean createPersonalFolder) {
		this.createPersonalFolder = createPersonalFolder;
	}
	public String getPathPositions() {
		return folderStructure;
	}
	public void setPathPositions(String pathPositions) {
		this.folderStructure = pathPositions;
	}	
	public String getDelimiterValues(){
		return delimiterValues;
	}
	public void setDelimiterValues(String delimiterValues){
		this.delimiterValues = delimiterValues;
	}
}
