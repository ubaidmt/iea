package mx.com.sacs.bulkloader.bean;

import java.util.List;

public class BulkLoaderBean {
	
	private List<BatchConfigBean> batchConfigBeans;
	private CEConnectionBean ceConnBean;
	private DocClassesBean docClassesBean;
	private FolderClassesBean folderClassesBean;
	
	public CEConnectionBean getCeConnBean() {
		return ceConnBean;
	}
	public void setCeConnBean(CEConnectionBean ceConnBean) {
		this.ceConnBean = ceConnBean;
	}
	public List<BatchConfigBean> getBatchConfigBeans() {
		return batchConfigBeans;
	}
	public void setBatchConfigBeans(List<BatchConfigBean> batchConfigBeans) {
		this.batchConfigBeans = batchConfigBeans;
	}
	public DocClassesBean getDocClassesBean() {
		return docClassesBean;
	}
	public void setDocClassesBean(DocClassesBean docClassesBean) {
		this.docClassesBean = docClassesBean;
	}
	public FolderClassesBean getFolderClassesBean() {
		return folderClassesBean;
	}
	public void setFolderClassesBean(FolderClassesBean folderClassesBean) {
		this.folderClassesBean = folderClassesBean;
	}

}
