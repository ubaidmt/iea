package mx.com.dss.inap.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import mx.com.dss.inap.model.Indice;
import mx.com.dss.inap.services.ReciboNominaService;

/**
 * Servlet implementation class Indices
 */
public class IndiceServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	ReciboNominaService service;
	       
    public IndiceServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");		
		response.setContentType("application/json, text/javascript; charset=UTF-8");
		
		String type;
		String criterio;
		PrintWriter out = response.getWriter();
		@SuppressWarnings("deprecation")
		String pathContext = request.getRealPath("");
		
		type = request.getParameter("type");
		criterio = request.getParameter("index");
		
		service = new ReciboNominaService();
		List<Indice> indexList = service.obtenerIndices(pathContext, criterio, type);
		String servletResponse;
		System.out.println(criterio);
		if(indexList != null){
			Gson json = new Gson();
			String respuesta = json.toJson(indexList);
			servletResponse = "{\"indices\":" + respuesta + ", \"exito\": \"1\"}";
		}else{
			servletResponse = "{\"indices\": [], \"exito\": \"0\"}";
		}
		out.print(servletResponse);
	}

}
