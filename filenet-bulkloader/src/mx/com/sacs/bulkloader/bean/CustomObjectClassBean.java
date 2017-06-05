package mx.com.sacs.bulkloader.bean;

import java.util.List;

public class CustomObjectClassBean {
	
	private String classCode;
	private String folder;
	private List<PropertyBean> properties;
	
	public String getClassCode() {
		return classCode;
	}
	public void setClassCode(String classCode) {
		this.classCode = classCode;
	}
	public List<PropertyBean> getProperties() {
		return properties;
	}
	public void setProperties(List<PropertyBean> properties) {
		this.properties = properties;
	}
	public String getFolder() {
		return folder;
	}
	public void setFolder(String folder) {
		this.folder = folder;
	}
	
}
