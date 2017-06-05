package mx.com.sacs.bulkloader.bean;

import java.util.List;

public class DocClassBean {

	private String name;
	private String classCode;
	private int filePos;
	private int pagesPos;
	private String delimiterValues;
	private List<PropertyBean> properties;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getClassCode() {
		return classCode;
	}
	public void setClassCode(String classCode) {
		this.classCode = classCode;
	}
	public int getFilePos() {
		return filePos;
	}
	public void setFilePos(int filePos) {
		this.filePos = filePos;
	}
	public List<PropertyBean> getProperties() {
		return properties;
	}
	public void setProperties(List<PropertyBean> properties) {
		this.properties = properties;
	}
	public int getPagesPos() {
		return pagesPos;
	}
	public void setPagesPos(int pagesPos) {
		this.pagesPos = pagesPos;
	}
	public String getDelimiterValues() {
		return delimiterValues;
	}
	public void setDelimiterValues(String delimiterValues) {
		this.delimiterValues = delimiterValues;
	}
}
