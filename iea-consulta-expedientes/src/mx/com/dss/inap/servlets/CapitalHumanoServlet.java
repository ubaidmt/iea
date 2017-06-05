package mx.com.dss.inap.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mx.com.dss.inap.model.CpitalHumano;
import mx.com.dss.inap.services.CapitalHumanoServices;

import com.google.gson.*;

public class CapitalHumanoServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	List<CpitalHumano> listDocs = new ArrayList<CpitalHumano>();
       
    public CapitalHumanoServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");		
		response.setContentType("application/json, text/javascript; charset=UTF-8");
		
		String json = null;
		PrintWriter out = response.getWriter();

		String rfc = request.getParameter("rfc");
		String cmd = request.getParameter("cmd");
		
		Boolean isVIP = (Boolean) request.getSession().getAttribute("isVIP");
		
		String pathContext = request.getParameter("pathContext") == null ? "" : request.getParameter("pathContext");

		CapitalHumanoServices services = new CapitalHumanoServices();
		listDocs = services.searchDocuent(cmd, rfc, pathContext, isVIP);
		
		if(listDocs != null && listDocs.size() > 0){
			Gson gson = new Gson();
			String jsonRespose = gson.toJson(listDocs);
			json = "{\"listDocs\": " + jsonRespose + ", \"exito\": \"1\"}";
			
			out.print(json);
		}else{
			json = "{\"listDocs\": [], \"exito\": \"0\"}";
			out.print(json);
		}
		
		out.close();
	}

}
