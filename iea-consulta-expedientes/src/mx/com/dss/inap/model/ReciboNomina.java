package mx.com.dss.inap.model;

import java.util.List;

public class ReciboNomina {

	private String link;
	private String name;
	private String tipoIngreso;
	
	private Integer anyo;
	private List<String> centrosTrabajo;
	private List<String> quincenas;

	
	public ReciboNomina() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getAnyo() {
		return anyo;
	}

	public void setAnyo(Integer anyo) {
		this.anyo = anyo;
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

	public List<String> getCentrosTrabajo() {
		return centrosTrabajo;
	}

	public void setCentrosTrabajo(List<String> centrosTrabajo) {
		this.centrosTrabajo = centrosTrabajo;
	}

	public List<String> getQuincenas() {
		return quincenas;
	}

	public void setQuincenas(List<String> quincenas) {
		this.quincenas = quincenas;
	}
}
