<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" >

<%@page import="java.util.Iterator, java.util.Map"%>
<%@page import="java.util.List, java.util.ArrayList"%>
<%@page contentType="text/html; charset=utf-8"	pageEncoding="UTF-8"%>

<%
	String usuario, error;
    List<?> listDocs = new ArrayList<Object>();
    String json = "";
    
    if(request.getSession(false) == null){
		response.sendRedirect("../");
	}
    
	if(session.getAttribute("usuario") == null){
		response.sendRedirect("../index.jsp");
		usuario = "";
	}else{
		usuario = session.getAttribute("usuario").toString();
		if(!session.getAttribute("modulo").toString().equals("Capital Humano")){
			request.getSession().setAttribute("error-login", "* Usted no tiene acceso al m&oacute;dulo: Capital Humano"); 
			response.sendRedirect("../index.jsp");
		}
	}
	
	
	if(session.getAttribute("error") != null){
		error = session.getAttribute("error").toString();
	}else{
		error = "";
	}
	
%>
<html>
    <head>
    <title>Búsqueda de Expediente Virtual</title>
	<link rel="stylesheet" href="../css/iea-style.css" type="text/css" />
	<link rel="stylesheet"	href="../css/custom-theme/jquery-ui.css" type="text/css" />
	
	<script language="JavaScript" src="../js/jquery.js"></script>
	<script language="JavaScript" src="../js/iea-rh.js"></script>
	
	<script language="JavaScript" src="../js/plugins/corners.js"></script>
	<script language="JavaScript" src="../js/plugins/jquery-ui.js"></script>
	<script language="JavaScript" src="../js/plugins/tablesort.js"></script>
    </head>

    <body onload="body()">
        <div id="json"><%= json%></div>
        <div class="overlay" id="img" style="display: none;">
            <img class="cargando" align="middle"  src="<%=request.getContextPath()%>/imagenes/load.gif" alt="cargando" />
        </div>
        <table class="tabla" id="tbBody">
            <!--Encabezado-->
            <tr valign="top">
                <td rowspan="4" class="grennIzq" width="20">
                    <img src="../imagenes/ezqSup.png" width="62" height="75" align="top" alt="" />
                </td>
            </tr>
            <tr valign="top" align="justify">
                <td height="58px">
                    <table class="encabezado" cellspacing="0">
                        <tr>
                            <td class="divGreen">&nbsp;</td>
                            <td class="divGreen">&nbsp;</td>
                            <td>
                                <img src="../imagenes/logoIEA.jpg" class="imgEnc" height="58" alt=""/>
                            </td>
                        </tr>
                        <tr>
                        <td colspan="2" align="right" class="load"><b>Bienvenido</b> &nbsp;<%= usuario.toUpperCase() %></td>
                        <td></td>
                        </tr>
                    </table>
                </td>
            </tr>
            <!-- Fin Encabezado -->
            <tr valign="top">
                <td height="100px">
                    <!-- Tabla Formulario -->
                    <table class="tbForm" cellpadding="0" cellspacing="0">
                        <tr>
                            <td colspan="2">
                                <div class="pfondo">Búsqueda de Expediente Virtual</div>
                            </td>
                        </tr>
                        <!-- Columna de Formulario de Busqueda -->
                        <tr>
                            <td>
                                <div id="form" class="form">
                                    <table class="subtabla" cellpadding="5">
                                    	<tr>
                                    		<td class="4">
                                    			<label class="label3">RFC del Docente :</label>
                                    			&nbsp;&nbsp;
                                    			<input type="text" id="rfc" style="text-transform: uppercase;" maxlength="13"/>
                                    			<input type="hidden" id="pathContext" value="<%=request.getRealPath("")%>"/>
                                    		</td>
                                    	</tr>
                                        <tr>
                                            <td colspan="2">
                                                <table width="100%" cellpadding="0" cellspacing="0">
                                                    <tr>
                                                    <td>
                                                         <label class="label3">Palabra(s) Clave</label>
                                                    </td>
                                                    <td align="center">
                                                    <!-- CheckBox para buscar por palabras exactas -->
                                                    <input id="radio_todas" name="pexact" type="radio" class="" checked="checked"/>
                                                    	<label class="label2">Todas las palabras</label>
                                                    <input id="radio_cualquiera" name="pexact" type="radio" class="" />
                                                    	<label class="label2">Cualquiera de las palabras</label>
                                                    </td>
                                                    </tr>
                                                </table>
                                            </td>
                                        </tr>
                                        <tr>
											<td colspan="2">
												<table width="100%" cellpadding="0" cellspacing="0">
													<tr>
													<td>
														<input type="text" id="cad" name="cad" style="width: 100%" value="Todos Los Registros" onblur="b(this);" onfocus="f(this);" class="gr" />
													</td>

													</tr>
													<tr><td>	&nbsp;</td></tr>
													<tr>
														<td align="center"><input id="btnBuscar" type="submit" value="Buscar" onclick="validarFrm()" style="width: 120px;"/></td>
													</tr>
												</table>
											</td>
                                        </tr>
                                        <tr>
                                            <td>
                                                <span class="" id="load"> </span>
                                                <label class="error" id="error"></label>
                                                <label class="error" id="msg"><%=error%></label>
                                            </td>
                                        </tr>
                                    </table>
                                </div>
                            </td>
							<td></td>
                            <td>
                                <table class="borde" cellspacing="0" cellpadding="0">
                                  <tr>
                                    <td rowspan="2" valign="top">
                                    <img alt="" src="<%=request.getContextPath()%>/imagenes/info.png" height="32" width="32" />
                                    </td>
                                    <td>
                                      <label class="load">El filtro por contenido considera cualquiera de las palabras introducidas. </label>
                                    </td>
                                  </tr>

                                  <tr>
                                    <td colspan="2">
                                    <hr width="100%" />
                                       <label class="load">Para filtrar por frase, defínala entre comillas dobles (" "). Utilice el símbolo de asterisco (*) como comodín. </label>
                                    </td>
                                  </tr>
                                </table>
                            </td>
                        </tr>
                        <!-- Fin Columna de Formulario de Busqueda -->
                    </table>
                    <!-- Fin Tabla contenido -->
                </td>
            </tr>
            <!-- Columna del Grid de resultados -->
            <tr valign="top">
                <td valign="top">
                    <div>
                        <label id="resReg" class="subtitulo1"><%=listDocs.size()%> registro(s) encontrado(s)</label>
                        <div class="scroll">
                        <table id="grid">
                            <thead>
                                <tr>
                                    <th class="sortable">RFC</th>
                                    <th class="sortable">Nombre</th>
                                    <th class="sortable-sortAlphaNumeric">Referencia<br/> Topogr&aacute;fica</th>
                                    <th class="sortable-date-dmy">Fecha de Nacimiento</th>
                                    <th class="sortable">Tipo de Ingreso</th>
                                    <th class="sortable">Documento</th>
                                </tr>
                            </thead>
                            <tbody id="tbBodyl">

                                <%
                                    if (listDocs == null || listDocs.size() < 1) {
                                %>
                                <tr>
                                    <td colspan="6" class="even" align="center" >
                                        Sin registros
                                    </td>
                                </tr>
                                <% } %>
                            </tbody>
                        </table>
                        </div>
                    </div>
                </td>
            </tr>
            <!-- Columna del Grid de resultados -->
            <tr>
                <td colspan="2" height="58px" align="center">
                    <input id="btnSalir" type="button" class="boton" value="Salir" style="width: 150px;" onclick="exit();"/>
                </td>
            </tr>
            <!-- Pie de Página -->
            <tr>
                <td colspan="2" height="58px">
                    <img src="../imagenes/pie_pag.png" align="left" alt=""/>
                </td>
            </tr>
        </table>
        
        <table id="list2"></table>
        <div id="pager2"></div>
         
    </body>
</html>
