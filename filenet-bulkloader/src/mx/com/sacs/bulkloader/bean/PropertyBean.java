package mx.com.sacs.bulkloader.bean;

public class PropertyBean {

	private String name;
	private String type;
	private String pos;
	private String defaultValue;
	private Boolean required, isMultivalue, createIndex;
	
	private CustomObjectClassBean customObject;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getPos() {
		return pos;
	}
	public void setPos(String pos) {
		this.pos = pos;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	public Boolean isMultivalue() {
		return isMultivalue;
	}
	public void setMultivalue(Boolean isMultivalue) {
		this.isMultivalue = isMultivalue;
	}
	public Boolean createIndex() {
		return createIndex;
	}
	public void setCreateIndex(Boolean createIndex) {
		this.createIndex = createIndex;
	}
	public CustomObjectClassBean getCustomObject() {
		return customObject;
	}
	public void setCustomObject(CustomObjectClassBean customObject) {
		this.customObject = customObject;
	}
	public Boolean isRequired() {
		return required;
	}
	public void setRequired(Boolean required) {
		this.required = required;
	}
}
