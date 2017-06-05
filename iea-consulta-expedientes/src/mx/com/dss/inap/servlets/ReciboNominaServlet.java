package mx.com.dss.inap.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import mx.com.dss.inap.model.ReciboNomina;
import mx.com.dss.inap.services.ReciboNominaService;

public class ReciboNominaServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	private static ReciboNominaService services;
	
	ReciboNomina reciboNomina = new ReciboNomina();
	String cmd, json;
	
	List<ReciboNomina> recibosNomina = new ArrayList<ReciboNomina>();
   
    public ReciboNominaServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		response.setContentType("application/json, text/javascript; charset=UTF-8");
		
		Boolean isVIP = (Boolean) request.getSession().getAttribute("isVIP");
		reciboNomina.setAnyo(Integer.parseInt(request.getParameter("anyo")));
		
		String quincena = new String(request.getParameter("quincena").getBytes("ISO-8859-15"),"UTF-8");
		String centrosTrabajo = new String(request.getParameter("centro-trabajo").getBytes("ISO-8859-15"),"UTF-8");
		
		if(!quincena.equals("null") && !quincena.isEmpty())
			reciboNomina.setQuincenas(ReciboNominaService.obtenerElementos(quincena));
		else
			reciboNomina.setQuincenas(null);
		
		if(!centrosTrabajo.equals("null") && !centrosTrabajo.isEmpty())
			reciboNomina.setCentrosTrabajo(ReciboNominaService.obtenerElementos(centrosTrabajo));
		else{
			reciboNomina.setCentrosTrabajo(null);
			centrosTrabajo = null;
			}
		
		System.out.println("Centro de trabajo = " + centrosTrabajo);
		System.out.println("QUINCENA = " + reciboNomina.getQuincenas().get(0) + " AÑO = " + reciboNomina.getAnyo());
		
		//cmd = request.getParameter("cmd");
		
		System.out.println(request.getParameter("cmd"));
		
		cmd = "";
		
		System.out.println("CMD = " + cmd);

		@SuppressWarnings("deprecation")
		String pathContext = request.getRealPath("");
		
		services = new ReciboNominaService();
		recibosNomina = services.searchReciboNomina(reciboNomina, centrosTrabajo, cmd, pathContext, isVIP);
		
		 Gson gson = new Gson();

		if(reciboNomina != null && recibosNomina.size() == 0){
			json = "{\"listDocs\": [], \"exito\": \"none\"}";
			out.print(json);
		}else if(recibosNomina != null && recibosNomina.size() > 0){
			String jsonRespose = gson.toJson(recibosNomina);
			json = "{\"listDocs\": " + jsonRespose + ", \"exito\": \"1\"}";
			out.print(json);
		}else{
			json = "{\"listDocs\": [], \"exito\": \"0\"}";
			out.print(json);
		}
		
		out.close();
	}
	
}
