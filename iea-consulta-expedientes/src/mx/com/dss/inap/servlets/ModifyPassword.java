package mx.com.dss.inap.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mx.com.dss.inap.model.Credencial;
import mx.com.dss.inap.services.CredencialService;
import mx.com.dss.inap.util.INAPUtil;

public class ModifyPassword extends HttpServlet {

	private static final long serialVersionUID = 2799155539011630321L;
	private static Credencial credencial = new Credencial();
	private static CredencialService service = null;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");		

		String usuario = request.getParameter("usuario");
		String oldPassword = request.getParameter("password");
		String newPassword = request.getParameter("new_password");

		@SuppressWarnings("deprecation")
		String pathContext = request.getRealPath("");
		
		service = new CredencialService();
		credencial = service.obtenerCredencial(usuario, pathContext);
		
		String servletResponse;
		response.setContentType("application/json, text/javascript; charset=UTF-8");
		PrintWriter out = response.getWriter();

		if (credencial != null && isCorrectPassword(credencial, oldPassword)) {
			if (service.cambiarPassword(usuario, newPassword, pathContext)){
				servletResponse = "{\"message\":[] , \"exito\": \"1\"}";
			}else{
				servletResponse = "{\"message\": Usuario y/o Contraseña no v&aacute;lidos, \"exito\": \"0\"}";
			}
		}else{
			servletResponse = "{\"message\": Usuario y/o Contraseña no v&aacute;lidos, \"exito\": \"0\"}";
		}
		
		out.print(servletResponse);
	}

	private Boolean isCorrectPassword(Credencial credencial, String password) {
		if (credencial.getContrasena().equals(password)
				|| credencial.getContrasena().equals(
						INAPUtil.encryptedMessage(password)))
			return true;
		else
			return false;
	}

}
