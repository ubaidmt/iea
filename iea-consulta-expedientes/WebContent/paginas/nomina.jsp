<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" >

<%@page import="java.util.Date"%>
<%@page import="java.util.Iterator, java.util.Map"%>
<%@page import="java.util.List, java.util.ArrayList"%>
<%@page import="mx.com.dss.inap.model.ReciboNomina"%>

<%
	String usuario, modulo, error;
	
	if(request.getSession(false) == null){
		response.sendRedirect("../");
	}
	
	if(session.getAttribute("usuario") == null){
		response.sendRedirect("../index.jsp");
		usuario = "";
	}else{
		if(session.getAttribute("usuario") == "" || !session.getAttribute("modulo").toString().equals("Recibo Nomina") ){
			request.getSession().setAttribute("error-login", "* Usted no tiene acceso al m&oacute;dulo: Recibos de N&oacute;mina"); 
			response.sendRedirect("../index.jsp");
			usuario = "";
		}else{
			usuario = session.getAttribute("usuario").toString();
		}
	}
	
    ReciboNomina reciboNomina = new ReciboNomina();
    List<?> listDocs = new ArrayList<Object>();
    
    String json = "";
	
%>

<html>
    <head>
        <title>Recibos de Nómina</title>
		<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
		<!-- JavaScript -->
		<script type="text/javascript" language="javascript" src="../js/jquery.js"></script>
        <script type="text/javascript" language="javascript" src="../js/plugins/corners.js"></script>
        <script type="text/javascript" src="../js/plugins/tablesort.js"></script>
		<script type="text/javascript" src="../js/plugins/jquery-ui.js"></script>
		<script type="text/javascript" language="javascript" src="../js/iea-nomina.js"></script>
		<!-- Hojas de Estilo -->
		<link type="text/css" rel="stylesheet" href="../css/iea-style.css"/>
		<link type="text/css" rel="stylesheet" href="../css/custom-theme/jquery-ui.css" type="text/css" />
    </head>

    <body onload="body()">
        <div id="json" ><%= json%></div>
        
        <div class="overlay" id="img" style="display: none;">
            <img class="cargando" align="middle"  src="../imagenes/load.gif" alt="cargando" />
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
                    <form id="frmBusNominas" accept-charset=?utf-8?>
                    <table class="tbForm" cellpadding="0" cellspacing="0">
                        <tr>
                            <td colspan="2">
                                <div class="pfondo">B&uacute;squeda de Recibos de N&oacute;mina</div>
                            </td>
                        </tr>
                        <!-- Columna de Formulario de Busqueda -->
                        <tr>
                            <td>
                                <div class="form" id="form">
                                    <table class="subtabla" cellpadding="5">
                                    <tr>
                                    	<td colspan="2">
                                    	<label class="label3">Año&nbsp;:</label>&nbsp;
                                    	<select id="anyo" name="anyo" />
                                    	</td>
                                    	<td colspan="2">
                                    	<label class="label3">Quincena&nbsp;:</label>&nbsp;
                                    	<input type="text" name="quincena" id="quincena" size="37"/>
										</td>
										<td><img id="searchQuincena" border="0" src="../imagenes/search_green.png" title="Buscar Quincena"/></td>
									</tr>
									<tr>
                                    	<td colspan="4">
                                    	<label class="label3">Zona &oacute; Coordinaci&oacute;n&nbsp;:&nbsp;</label>
                                    		<input type="text" name="centroTrabajo" id="centroTrabajo" size="50"/>
										</td>
                                        <td><img id="searchCentroTrabajo" border="0" src="../imagenes/search_green.png" title="Buscar Zona ó Coordinación" /></td>
									</tr>
									<tr>
                                         <td colspan="4">
                                             <table width="100%" cellpadding="0" cellspacing="0">
                                                  <tr>
                                                  <td class="label3">
                                                       Palabra(s) Clave
                                                  </td>
                                                  <td align="center">
                                                  <!-- CheckBox para buscar por palabras exactas -->
                                                  <input id="radio_todas" name="pexact" type="radio" class="" checked="checked"/> <label class="label2">Todas las palabras</label>
                                                  <input id="radio_cualquiera" name="pexact" type="radio" class="" /> <label class="label2">Cualquiera de las palabras</label>
                                                  </td>
                                                  </tr>
                                              </table>
                                          </td>
                                     </tr>
                                        <tr>
											<td colspan="4">
												<table width="100%" cellpadding="0" cellspacing="0">
													<tr>
													<td>
														<input type="text" id="cad" name="cad" style="width: 98%"
														 value="Todos Los Registros" onblur="b(this);" onfocus="f(this);" class="gr" />
													</td>
													</tr>
													<tr><td>&nbsp;</td></tr>
													<tr>
														<td align="center"><input id="btnBuscar" type="button" value="Buscar" style="width: 120px;" class="boton" onclick="buscarDocumentos();"/></td>
													</tr>
												</table>
											</td>
                                        </tr>
                                        <tr>
                                            <td colspan="4">
                                                <span class="" id="load"> </span>
                                                <label class="error" id="error"></label>
                                                <label class="error" id="msg"></label>
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
                                    <img alt="" src="../imagenes/info.png" height="32" width="32" />
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
                    </form>
                    <!-- Fin Tabla contenido -->
				</td>
			</tr>
            <!-- Columna del Grid de resultados -->
            <tr valign="top">
                <td valign="top">
                    <div>
                        <label id="resReg" class="subtitulo1"><%=listDocs.size()%> registro(s) encontrado(s)</label>
                        <div class="scroll">
                        <table id="grid" >
                            <thead>
                                <tr>
                                    <th class="sortable">Año</th>
                                    <th class="sortable">Quincena</th>
                                    <th class="sortable">Centro de trabajo</th>
                                    <th class="sortable">Tipo de Ingreso</th>
                                    <th class="sortable">Documento</th>
                                </tr>
                            </thead>
                            <tbody id="tbBodyl">

                                <%
                                    if (listDocs == null || listDocs.size() < 1) {
                                %>
                                <tr>
                                    <td colspan="5" class="even" align="center" >
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
                <td colspan="2" align="center" width="200px">
                    <input id="btnSalir" type="button" class="boton" value="Salir" style="width: 120px;" onclick="exit();"/>
                </td>
            </tr>
            <!-- Pie de Página -->
            <tr>
                <td colspan="2" height="58px">
                    <img src="../imagenes/pie_pag.png" align="left" alt=""/>
                </td>
            </tr>
        </table>
        
        <!-- WINDOWS MODAL -->
        <!-- QUINCENA WINDOW MODAL -->
		<div id="quincena-modal" title="Buscador de Quincenas">
		<div class="overlay" id="loadQ" style="display: none;" >
			<img class="cargando" border="0" src="../imagenes/load.gif" alt="cargando" />
		</div>
				<div id="buscadorQuincena" class="form">
				<table>
					<tr>
						<td><input type="text" id="indexQuincena" size="35" /></td>
						<td align="center">&nbsp;&nbsp;<a id="btnBuscarQuincena" onclick="buscarQuincenas()">Buscar</a></td>
					</tr>
				</table>
				</div>
				<br/>
				<label id="reponseLabel" class="subtitulo1" style="font-size: 9px;">0 registro(s) encontrado(s)</label><br/>
				<div class="scroll">
					<table class="grid" id="tbQuincena" >
                            <thead>
                                <tr>
                                    <th class="sortable" colspan="2">Quincenas</th>
                                </tr>

                            </thead>
                            <tbody id="tbBodyQuincena">

                                <%
                                    if (listDocs == null || listDocs.size() < 1) {
                                %>
                                <tr>
                                    <td colspan="2" class="even" align="center" >
                                        Sin registros
                                    </td>
                                </tr>
                                <% } %>

                            </tbody>
                        </table>
                 </div><br/>
                 <center>
                 <a href="#" id="btnAceptarQuincena" onclick="aceptarQuincena()">&nbsp;&nbsp;Aceptar&nbsp;&nbsp;</a>
                 </center>
		</div>
	<!--  END QUINCENA WINDOW MODAL -->

	<!-- CENTRO DE TRABAJO WINDOW MODAL -->
		<div id="modal-centro" title="Buscador de zona ó coordinación">
		<div class="overlay" id="loadC" style="display: none;" >
			<img class="cargando" border="0" src="../imagenes/load.gif" alt="cargando" />
		</div>
		<div id="buscadorCentro" class="form">
				<table>
					<tr>
						<td><input type="text" id="indexCentroTrabajo" size="47" /></td>
						<td>&nbsp;&nbsp;<a id="btnBuscarCentro" onclick="buscarCentroTrabajo()">Buscar</a></td>
					</tr>
				</table>
		</div>
				<label id="reponseCentroLabel" class="subtitulo1" style="font-size: 9px;">0 registro(s) encontrado(s)</label><br/>
					<div class="scroll">
					<table class="grid" id="tbCentroTrabajo" >
                            <thead>
                                <tr>
                                    <th class="sortable" colspan="2">Quincenas</th>
                                </tr>

                            </thead>
                            <tbody id="tbBodyCentroTrabajo">

                                <%
                                    if (listDocs == null || listDocs.size() < 1) {
                                %>
                                <tr>
                                    <td colspan="2" class="even" align="center" >
                                        Sin registros
                                    </td>
                                </tr>
                                <% } %>

                            </tbody>
                        </table>
                      </div><br/>
                      <center><a href="#" id="btnAceptarCentro" onclick="aceptarCentroTrabajo()">&nbsp;&nbsp;Aceptar&nbsp;&nbsp;</a></center>
		</div>        
        
    </body>
</html>
