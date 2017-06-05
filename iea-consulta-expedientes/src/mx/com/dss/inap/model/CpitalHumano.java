package mx.com.dss.inap.model;

public class CpitalHumano {
	
	 private String rfc;
	 private String id;
	 private String name;
	 private String link;
	 private String nombre;
	 private String fechaNac;
	 private String tipoIngreso;
	 private String refTopografica;

	 private int consecutivo;

	public CpitalHumano() {
		super();
	}

	public String getRfc() {
		return rfc;
	}

	public void setRfc(String rfc) {
		this.rfc = rfc;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getConsecutivo() {
		return consecutivo;
	}

	public void setConsecutivo(int consecutivo) {
		this.consecutivo = consecutivo;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getRefTopografica() {
		return refTopografica;
	}

	public void setRefTopografica(String refTopografica) {
		this.refTopografica = refTopografica;
	}

	public String getFechaNac() {
		return fechaNac;
	}

	public void setFechaNac(String fechaNac) {
		this.fechaNac = fechaNac;
	}

	public String getTipoIngreso() {
		return tipoIngreso;
	}

	public void setTipoIngreso(String tipoIngreso) {
		this.tipoIngreso = tipoIngreso;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

}
