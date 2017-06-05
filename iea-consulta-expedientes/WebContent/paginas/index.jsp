<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page contentType="text/html; charset=utf-8"	pageEncoding="UTF-8"%>
<%@page import="java.util.*" session="true"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<title></title>
</head>

<%

	if(request.getSession(false) == null){
		response.sendRedirect("../");
	}
		
		
	if(session.getAttribute("usuario") == null){
		response.sendRedirect("../");
	}else{
		if(session.getAttribute("usuario").toString() != null){
			if(session.getAttribute("modulo").toString().equals("Capital Humano")){
				response.sendRedirect("capital-humano.jsp");
			}else if(session.getAttribute("modulo").toString().equals("Recibo Nomina")){
				response.sendRedirect("capital-humano.jsp");
			}else
				response.sendRedirect("../");
		}else{
			response.sendRedirect("../");
		}
	}
	 
%>
<body/>
</html>