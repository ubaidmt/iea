<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" >
<%@page import="java.io.PrintWriter"%>
<%@page import="java.util.*"%>

<%
	String usuario = "";
	String error = null;
	
	if(request.getParameter("exit") != null){
		error = "Log Out...";
		session.removeAttribute("usuario");
		session.invalidate();
		error = "";
	}else{
		if(session.getAttribute("error-login") != null)
		{
			error = session.getAttribute("error-login").toString();
			session.removeAttribute("usuario");
			session.invalidate();
		} else if(session.getAttribute("error-modulo") != null){
			error = session.getAttribute("error-modulo").toString();
			session.removeAttribute("usuario");
			session.invalidate();
		} else 
			error = "";
	}
	
%>

<html>
<head>
<title>IEA</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<!-- Java Script -->
<script type="text/javascript" language="javascript" src="js/jquery.js"></script>
<script type="text/javascript" language="javascript" src="js/plugins/jquery-ui.js"></script>
<script type="text/javascript" language="javascript" src="js/plugins/corners.js"></script>
<script type="text/javascript" language="javascript" src="js/iea-login.js"></script>
<!-- Hojas de estilo (CSS) -->
<link type="text/css" rel="stylesheet" href="css/iea-style.css" />
<link type="text/css" rel="stylesheet" href="css/custom-theme/jquery-ui.css" />
</head>
<body>
	<table class="tabla" id="tbBody" cellpadding="0" cellspacing="0">
		<tr valign="top">
			<td rowspan="3" class="grennIzq" width="20"><img
				src="imagenes/ezqSup.png" width="62" height="75" align="top" alt="" />
			</td>
		</tr>
		<tr valign="top" align="justify">
			<td>
				<table class="encabezado" cellspacing="0">
					<tr valign="top">
						<td class="divGreen">&nbsp;</td>
						<td class="divGreen">&nbsp;</td>
						<td><img src="imagenes/logoIEA.jpg" class="imgEnc"
							height="58" alt="" />
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td valign="top">
				<table class="tbForm" width="40%">
					<tr>
						<td><p class="pfondo">Bienvenido</p>
						</td>
					</tr>
					<tr>
						<td>
							<form id="form" class="form" method="post" action="login">
								<table cellpadding="3" align="center" width="80%">
									<tr>
										<td colspan="3"><label class="label">Iniciar Sesi&oacute;n</label>
										</td>
									</tr>
									<tr>
										<td align="right" class="label2">Nombre de usuario:</td>
										<td colspan="2">
										<input type="text" name="usuario" id="usuario" style="width: 140px;"
											 />
										</td>
									</tr>
									<tr>
										<td align="right" class="label2" >Contraseña:</td>
										<td colspan="2">
										<input type="password" id="password" name="password" style="width: 140px;"
											 />
										</td>
									</tr>
									<tr>
										<td align="right" class="label2">Módulo:</td>
										<td>
										<select name="modulo" id="modulo">
												<option value="" selected="selected">Seleccionar</option>
												<option value="Capital Humano">Capital Humano</option>
												<option value="Recibo Nomina">Recibos de Nomina</option>
										</select>
										</td>
										<td>
											<img id="imgChange" src="imagenes/secrecy-icon.png" title="Cambiar contraseña" border="0" />
										</td>
									</tr>
									<tr>
										<td>&nbsp;</td>
										<td colspan="2">
											<center><a id="btnAceptar" onclick="validaLoginFrm()" >Iniciar Sessi&oacute;n</a></center>
										</td>
									</tr>
									<tr>
										<td colspan="3"><div class="error" id="error"><%= error %></div>
										</td>
									</tr>
								</table>
							</form>
						</td>
					</tr>
					<tr>
						<td><span class="load"> El Instituto de
								Educaci&oacute;n de Aguascalientes no se hace responsable por el
								uso indebido de consultas a documentos. </span>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td id="piePag" colspan="2"><img src="imagenes/pie_pag.png"
				align="left" alt="" />
			</td>
		</tr>
	</table>

	<!-- DIV PARA CAMBIAR LOS DATOS -->
	<div id="change-password" title="CAMBIAR CONTRASEÑA">
	<div class="overlay" id="img" style="display: none;" >
		<img class="cargando" border="0" src="imagenes/load.gif" alt="cargando" />
	</div>
	<form class="form" method="post" id="frmModify">
				<table class="subtabla" cellpadding="5">
					<tr>
						<td align="right" class="label2">Nombre de usuario:</td>

						<td><input type="text" id="mod-user" name="mod-user" style="width: 140px;" /></td>
					</tr>
					<tr>
						<td align="right" class="label2">Contraseña:</td>

						<td><input type="password" id="oldPass" name="oldPass"  style="width: 140px;;" /></td>

					</tr>
					<tr>
						<td align="right" class="label2">Contraseña nueva:</td>
						<td><input type="password" id="newPass" name="newPass" style="width: 140px;" /></td>
					</tr>

					<tr>
						<td colspan="2">
						<label class="error" id="error-modify"><%=error %></label>
						</td>
					</tr>
					<tr>
						<td align="center" colspan="2">
						<a id="btnModificar" onclick="validaModify();" >Modificar Datos</a>&nbsp;&nbsp;<a id="btnCancel" onclick="$('#change-password').dialog('close')" >Cancelar</a>
						</td>
					</tr>
				</table>
		</form>
	</div>

</body>
</html>
