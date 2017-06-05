package mx.com.sacs.bulkloader.bean;

public class BatchConfigBean {
	
	private String baseFolder;
	private String fileReference;
	private String outputFile;
	private char delimeter;
	private boolean validateDuplicates;
	private boolean containHeader;
	private String dateFormat;
	private String osFolder;		
	private int batchSize;
	private boolean validatePDF;
	
	public Boolean isValidatePDF() {
		return validatePDF;
	}
	public void setValidatePDF(Boolean validate) {
		this.validatePDF = validate;
	}
	public String getBaseFolder() {
		return baseFolder;
	}
	public void setBaseFolder(String baseFolder) {
		this.baseFolder = baseFolder;
	}
	public String getFileReference() {
		return fileReference;
	}
	public void setFileReference(String fileReference) {
		this.fileReference = fileReference;
	}
	public String getOutputFile() {
		return outputFile;
	}
	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}
	public char getDelimeter() {
		return delimeter;
	}
	public void setDelimeter(char delimeter) {
		this.delimeter = delimeter;
	}
	public boolean isValidateDuplicates() {
		return validateDuplicates;
	}
	public void setValidateDuplicates(boolean validateDuplicates) {
		this.validateDuplicates = validateDuplicates;
	}	
	public boolean isContainHeader() {
		return containHeader;
	}
	public void setContainHeader(boolean containHeader) {
		this.containHeader = containHeader;
	}
	public String getDateFormat() {
		return dateFormat;
	}
	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}
	public String getOsFolder() {
		return osFolder;
	}
	public void setOsFolder(String osFolder) {
		this.osFolder = osFolder;
	}
	public int getBatchSize() {
		return batchSize;
	}
	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}
}
