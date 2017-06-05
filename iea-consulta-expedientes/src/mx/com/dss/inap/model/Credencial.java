package mx.com.dss.inap.model;

import java.util.List;

public class Credencial {

	private boolean vip;

	private String usuario;
	private String contrasena;
	
	private List<String> tipoExpedientes;	
	
	public String getUsuario() {
		return usuario;
	}
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	public String getContrasena() {
		return contrasena;
	}
	public void setContrasena(String contrasena) {
		this.contrasena = contrasena;
	}
	public boolean isVip() {
		return vip;
	}
	public void setVip(boolean vip) {
		this.vip = vip;
	}
	public List<String> getTipoExpedientes() {
		return tipoExpedientes;
	}
	public void setTipoExpedientes(List<String> tipoExpedientes) {
		this.tipoExpedientes = tipoExpedientes;
	}
	
}
