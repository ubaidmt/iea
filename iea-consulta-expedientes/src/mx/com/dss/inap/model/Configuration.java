package mx.com.dss.inap.model;

public class Configuration {
	
	private String usuario;
	private String password;
	private String uri;
	private String stanza;
	private String objectStore;
	private String workplaceContext;
	private String userToken;
	
	public String getUsuario() {
		return usuario;
	}
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public String getStanza() {
		return stanza;
	}
	public void setStanza(String stanza) {
		this.stanza = stanza;
	}
	public String getObjectStore() {
		return objectStore;
	}
	public void setObjectStore(String objectStore) {
		this.objectStore = objectStore;
	}
	public String getWorkplaceContext() {
		return workplaceContext;
	}
	public void setWorkplaceContext(String workplaceContext) {
		this.workplaceContext = workplaceContext;
	}
	public String getUserToken() {
		return userToken;
	}
	public void setUserToken(String userToken) {
		this.userToken = userToken;
	}

}
