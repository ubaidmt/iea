package mx.com.dss.inap.servlets;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import mx.com.dss.inap.model.Credencial;
import mx.com.dss.inap.services.CredencialService;
import mx.com.dss.inap.util.INAPUtil;

public class Authentication extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	private static Credencial credencial = new Credencial();
	private static CredencialService service = null;
	
	public Authentication() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		String redirect;
		String modulo = request.getParameter("modulo");
		String usuario = request.getParameter("usuario");
		String password = request.getParameter("password");
		
		System.out.println("[Modulo = " + modulo + " ]");
		System.out.println("[Usuario = " + usuario + " ]");
		System.out.println("[Password = " + password + " ]");

		@SuppressWarnings("deprecation")
		String pathContext = request.getRealPath("");
		
		service = new CredencialService();
		credencial = service.obtenerCredencial(usuario, pathContext);
		
		if(credencial != null && acceso(credencial, password)){
			
			redirect = obtenerAcceso(modulo, credencial);
			
			if(redirect != null){
				HttpSession session = request.getSession(true);
				session.setAttribute("usuario", credencial.getUsuario());
				session.setAttribute("isVIP", credencial.isVip());
				session.setAttribute("modulo", modulo);
				response.sendRedirect(redirect);
			}else {
				request.getSession().setAttribute("error-login", "* Usted no tiene acceso al m&oacute;dulo: " + modulo); 
				response.sendRedirect("index.jsp");
			}
		}else{
			request.getSession().setAttribute("error-login", "* Usuario y/o Contraseña no v&aacute;lidos"); 
			response.sendRedirect("index.jsp");
		}
		
	}
	
	private Boolean acceso(Credencial credencial, String password){
		return credencial.getContrasena().equals(INAPUtil.encryptedMessage(password));
	}
	
	
	private String obtenerAcceso(String modulo, Credencial credencial){

		List<String> tipoExpedientes = credencial.getTipoExpedientes();
		
		for (String expediente : tipoExpedientes) {
			if(expediente.equals(modulo) && modulo.equals("Capital Humano")){
				return "paginas/capital-humano.jsp";
			}else if (expediente.equals(modulo) && modulo.equals("Recibo Nomina")) {
				return "paginas/nomina.jsp";
			}
		}
		return null;
	}
	

}
