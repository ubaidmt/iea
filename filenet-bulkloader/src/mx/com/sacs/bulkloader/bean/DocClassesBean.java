package mx.com.sacs.bulkloader.bean;

import java.util.List;

public class DocClassesBean {

	private int pos;
	private String defaultClassCode;
	private List<DocClassBean> docClasses;
	
	public int getPos() {
		return pos;
	}
	public void setPos(int pos) {
		this.pos = pos;
	}
	public String getDefaultClassCode() {
		return defaultClassCode;
	}
	public void setDefaultClassCode(String defaultClassCode) {
		this.defaultClassCode = defaultClassCode;
	}
	public List<DocClassBean> getDocClasses() {
		return docClasses;
	}
	public void setDocClasses(List<DocClassBean> docClasses) {
		this.docClasses = docClasses;
	}
	
	
}
