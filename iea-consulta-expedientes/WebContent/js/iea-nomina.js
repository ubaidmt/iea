$(document).ready(function() {
	loadSelectValues();
	createWindoModal();
	addFrmTemplate();
	$(document).keypress(function(event) {
		  if ( event.which == 13 ) {
			  	if($('#quincena-modal').dialog('isOpen'))
			     	return buscarQuincenas();
			  	else if($('#modal-centro').dialog('isOpen'))
			  		return buscarCentroTrabajo();
			  		else
			  			return buscarDocumentos();
		   }});
	
	$('#searchQuincena').click(function() {
		$('#quincena-modal').dialog('open');
		return;
	});
	
	$('#searchCentroTrabajo').click(function() {
		$('#modal-centro').dialog('open');
		return;
	});
	
});

function body() {
	var h = window.innerHeight;
	document.getElementById('tbBody').style.height = h > 0 ? (h - 10) + 'px'
			: (document.documentElement.clientHeight - 65);
}

function clear() {
	$('#error').html('');
	$('#msg').html('');
}

function exit(){
	location.href = '../?exit=true';
}

function loadSelectValues(){
	select = $('#anyo');
	
	 today = new Date();
	limInf = 2003;
	limSup = today.getFullYear();
	
	select.append("<option value=''>Seleccionar</oprion>");
	
	for( var i = limInf; i <= limSup; i++ ){
		select.append('<option value=' + i + '>' + i + '</option>');
	}
}

function createWindoModal(){
	$('#quincena-modal').dialog({
		autoOpen : false,
		modal: true,
		width : 300,
		height: 430,
		resizable: false
	});
	
	$('#modal-centro').dialog({
		autoOpen : false,
		modal: true,
		width : 360,
		height: 430,
		resizable: false
	});
}

function addFrmTemplate(){
	$('#form').corner();
	$('#buscadorQuincena').corner();
	$('#buscadorCentro').corner();
	
	$('#btnBuscar').button();
	$('#btnSalir').button();
	
	$('#btnBuscarCentro').button();
	$('#btnAceptarCentro').button();
	
	$('#btnBuscarQuincena').button();
	$('#btnAceptarQuincena').button();
}

function buscarDocumentos(){
	
	var anyo = $('#anyo').val();
	var centro = $('#centroTrabajo').val();
	var quincena = $('#quincena').val();	
	var palabrasFiltro = $('#cad').val();;

	clear();

	if(!validarBusquedaDocumentos()){
		return false;
	}
	
	if(palabrasFiltro.length > 0 && palabrasFiltro != 'Todos Los Registros'){
		if($('#radio_todas').attr('checked')){
			data = "cmd=" + palabrasFiltro
					+ "&anyo=" + anyo + "&quincena=" + quincena + "&centro-trabajo=" + centro;
		}else{
			data = "cmd=" + getFiltro(palabrasFiltro) 
						  + "&anyo=" + anyo + "&quincena=" + quincena + "&centro-trabajo=" +centro;
		}
	} else {
		data = "anyo=" + anyo + "&quincena=" + quincena + "&centro-trabajo=" + centro;
	}
	ordenaResultados(data);
}

/**
 * VALIDAMOS EL FORMULARIO DE BUSQUEDA DE DOCUMENTOS
 * @returns {Boolean}
 */
function validarBusquedaDocumentos(){
	var anyo = $('#anyo');
	var quincena = $('#quincena');
	
	if(anyo.val().length == 0){
		$('#error').html('* No seleccion&oacute; valor para año.');
		anyo.focus();
		return false;
	} else if(quincena.val().length == 0){
		$('#error').html('* Quincena es un campo obligatorio.');
		quincena.focus();
		return false;
	}else
		return true;
}

/**
 * CREAMOS EL FILTRO POR C/U DE LAS PALABRAS
 * @param filtro
 * @returns {String}
 */
function getFiltro(filtro) {
	
	var iswithinphrase = false;
	var newchar;
	var query = '';

	filtro = filtro.replace(/^\s+|\s+$/g, '');
	filtro = filtro.replace(/\s+/g, '|');

	for ( var i = 0; i < filtro.length; i++) {
		newchar = filtro.charAt(i);
		if (filtro.charAt(i) == '"') {
			if (!iswithinphrase) {
				iswithinphrase = true;
			} else {
				iswithinphrase = false;
			}
		} else if (filtro.charAt(i) == '|') {
			newchar = ' OR ';
			if (iswithinphrase) {
				newchar = ' ';
			}
		}
		query += newchar;
	}
	return query;
}

/**
 * ORDENAMOS LOS RESULTADOS EN LA TABLA
 * @param params
 */
function ordenaResultados(params) {
	$('#img').show(0, function() {
		if (jQuery.browser.msie)
			this.style.removeAttribute('filter');
	});
	
	$('#img').removeClass('oculto').addClass('cargando');
	document.getElementById('img').style.display = 'block';
	
	$('#load').addClass('load').text('Buscando...');
		$.ajax({
				type : 'POST',
				url : '../searchRN',
				data : params,
				dataType : 'json',
				scriptCharset: "ISO-8859-1",
		        contentType: "application/x-www-form-urlencoded;charset=ISO-8859-1",
				success : function(data, res, settings) {

					if (res == 'success') {
						if (data.exito == 1) {
							loadTable(data);
							$('#load').html("");
						} else if (data.exito == 0) {
							loadTable('');
							$('#load')
									.html('Ocurrió un error al realizar la cosulta, favor de remover símbolos especiales del filtro <br/>Por ejemplo: +, -, <, >, etc.')
									.removeClass('load').addClass('load');
						} else{
							loadTable('');
							$('#load').html('No se han encontrado resultados.').removeClass('load').addClass('load');
						}
					} else {
						$('#load')
								.html('Error al procesar su solicitud, intente de nuevo')
								.removeClass('load').addClass('error');
					}

					$('#img').hide();
				},
				error : function(request, tipo, settings) {
					$('#load').html(
							'Error t: ' + tipo + ', request: ' + request + ' (' + settings + ')').removeClass('load')
							.addClass('error');
					$('#img').hide();
				}
			});
}

function loadTable(data) {
	var html = '';
	var lst = data.listDocs;
	if (lst == null || lst.length < 1 || lst == 'undefined') {
		$('#resReg').html("0 registro(s) encontrado(s)");
		html += '<tr><td colspan="5" class="even" align="center">Sin registros</td></tr>';
	} else {
		$.each(lst, function(i, field) {
			html += '<tr>';
			html += '<td align="center">' + field.anyo + '</td>';
			listQuincena = field.quincenas;
			if(listQuincena == null || listQuincena.length < 1 || listQuincena == 'undefined'){
				html += '<td> </td>';
			}else{
				html += '<td align="center">' 
					+ 
					$.each(listQuincena, function(j, quincena){
						html += quincena + ', ';
					})
					+ '</td>';
			}
			listCentrosTrabajo = field.centrosTrabajo;
			if(listCentrosTrabajo == null || listCentrosTrabajo.length < 1 || listCentrosTrabajo == 'undefined'){
				html += '<td> </td>';
			}else{
				html += '<td align="center">' 
					+ 
					$.each(listCentrosTrabajo, function(j, centroTrabajo){
						html += centroTrabajo + ', ';
					})
					+ '</td>';
			}
			html += '<td align="center">' + field.tipoIngreso + '</td>';
			html += '<td class="link" align="center"><a href="' + field.link + '" target="_blank">' + field.name + '</a></td>';
			html += '</tr>';
		});
		$('#resReg').html(lst.length + " registro(s) encontrado(s)");
	}
	$('#tbBodyl').html(html);
}

/**
 * BUSCADOR DE QUINCENAS (Indices)
 */
function buscarQuincenas(){
	var quincena = $('#indexQuincena').val();
	var params = 'index=' + quincena + '&type=Quincena';
	
	$('#loadQ').show(0, function() {
		if (jQuery.browser.msie)
			this.style.removeAttribute('filter');
	});
	
	$('#loadQ').removeClass('oculto').addClass('cargando');
	$.ajax({
		type : 'POST',
		url : '../search-index',
		data : params,
		dataType : 'json',
		scriptCharset: "ISO-8859-1",
		contentType: "application/x-www-form-urlencoded; charset=ISO-8859-1",
		success : function(data, res, settings) {
			if (res == 'success') {
				
				if (data.exito == 1) {
					resposeQuincena(data);
				} else {
					resposeQuincena('');
					$('#reponseLabel').html('Ocurrió un error al realizar la cosulta, favor de remover símbolos especiales del filtro <br/>Por ejemplo: +, -, <, >, etc.')
							.removeClass('load').addClass('load');
				}
			} else {
				$('#reponseLabel').html('Error al procesar su solicitud, intente de nuevo')
								  .removeClass('load').addClass('error');
			}
			$('#loadQ').hide();
		},
		error : function(request, tipo, settings) {
			$('#reponseLabel').html(
					'Error t: ' + tipo + ', request: ' + request + ' ('
							+ settings + ')').removeClass('load')
					.addClass('error');
			$('#loadQ').hide();
		}
	});
}

function resposeQuincena(data) {
	var html = '';
	var lst = data.indices;
	
	if (lst == null || lst.length == 0 ) {
		$('#reponseLabel').html("0 registro(s) encontrado(s)");
		html += '<tr><td colspan="6" class="even" align="center">Sin registros</td></tr>';
	} else {
		$('#reponseLabel').html(lst.length + " registro(s) encontrado(s)");
		$.each(lst, function(i, field) {
			html += '<tr>';
			html += '<td align="center" class="even" width="20%"><input name="rbQuincena" id="rbQuincena" type="radio" value="' + field.index + '" /></td>';
			html += '<td align="left" class="even">' + field.index + '</td>';
			html += '</tr>';
		});
		$('#reponseLabel').html(lst.length + " registro(s) encontrado(s)");
	}
	$('#tbBodyQuincena').html(html);
}

function aceptarQuincena(){
	var quincena = $("input[name='rbQuincena']:checked").val(); 
	$('#quincena').val(quincena);
	$('#indexQuincena').val('');
	$('#quincena-modal	').dialog('close');
}


/**
 * BUSCADOR DE CENTROS DE TRABAJO
 */
function buscarCentroTrabajo(){
	
	var params;
	var centro = $('#indexCentroTrabajo').val();
	
	if(centro.length > 0 && centro != 'Todos los registros'){
		params = 'index=' + centro + '&type=CentroTrabajo';
	}else{
		params = 'index=&type=CentroTrabajo';
	}
	
	$('#loadC').show(0, function() {
		if (jQuery.browser.msie)
			this.style.removeAttribute('filter');
	});
	
	$('#loadC').removeClass('oculto').addClass('cargando');
	
	$.ajax({
		type : 'POST',
		url : '../search-index',
		data : params,
		dataType : 'json',
		scriptCharset: "ISO-8859-1",
		contentType: "application/x-www-form-urlencoded; charset=ISO-8859-1",
		success : function(data, res, settings) {
			
			if (res == 'success') {
				
				if (data.exito == 1) {
					responseCentro(data);
				} else if (data.exito == 0) {
					responseCentro('');
					$('#reponseCentroLabel').html('Ocurrió un error al realizar la cosulta, favor de remover símbolos especiales del filtro <br/>Por ejemplo: +, -, <, >, etc.')
							.removeClass('load').addClass('load');
				}

			} else {
				$('#reponseCentroLabel')
						.html(
								'Error al procesar su solicitud, intente de nuevo')
						.removeClass('load').addClass('error');
			}
			$('#loadC').hide();
		},
		error : function(request, tipo, settings) {
			$('#reponseCentroLabel').html(
					'Error t: ' + tipo + ', request: ' + request + ' ('
							+ settings + ')').removeClass('load')
					.addClass('error');
			$('#loadC').hide();
		}
	});
}


function responseCentro(data) {
	var html = '';
	var lst = data.indices;
	
	if (lst == null || lst.length == 0 ) {
		$('#reponseCentroLabel').html("0 registro(s) encontrado(s)");
		html += '<tr><td colspan="6" class="even" align="center">Sin registros</td></tr>';
	} else {
		$('#reponseCentroLabel').html(lst.length + " registro(s) encontrado(s)");
		$.each(lst, function(i, field) {
			html += '<tr>';
			html += '<td align="center" class="even" width="20%"><input name="rbCentroTrabajo" id="rbCentroTrabajo" type="radio" value="' + field.index + '" /></td>';
			html += '<td align="left" class="even">' + field.index + '</td>';
			html += '</tr>';
		});
		
		$('#reponseCentroLabel').html(lst.length + " registro(s) encontrado(s)");
	}
	$('#tbBodyCentroTrabajo').html(html);
}

function aceptarCentroTrabajo(){
	var quincena = $("input[name='rbCentroTrabajo']:checked").val(); 
	$('#centroTrabajo').val(quincena);
	$('#modal-centro').dialog('close');
	$('#indexCentroTrabajo').val('');
	return false;
}

var t = 'Todos Los Registros';

function b(elem) {
	var ly = $(elem).val();
	if (ly.length <= 0) {
		$(elem).val(t).addClass("gr");
	} else {
		if (ly == t) {
			$(elem).val('').addClass("gr");
		} else {
			$(elem).removeClass("gr");
		}
	}
}

function f(e) {
	var ly = $(e).val();
	if (ly.length > 0) {
		if (ly == t) {
			$(e).val('').addClass("gr");
		} else {
			$(e).removeClass("gr");
		}
	}
}

//Introduce la clausula AND entre palabras de una cadena
function getFiltro(filtro) {
	
	var iswithinphrase = false;
	var newchar;
	var query = '';

	// Reemplazar espacios a los extremos
	filtro = filtro.replace(/^\s+|\s+$/g, '');
	// Reemplazar espacios restantes por "|"
	filtro = filtro.replace(/\s+/g, '|');

	for ( var i = 0; i < filtro.length; i++) {
		newchar = filtro.charAt(i);
		// Identificar si estamos dentro de frase o no
		if (filtro.charAt(i) == '"') {
			if (!iswithinphrase) {
				iswithinphrase = true;
			} else {
				iswithinphrase = false;
			}
		} else if (filtro.charAt(i) == '|') {
			newchar = ' OR ';
			if (iswithinphrase) {
				newchar = ' ';
			}
		}
		query += newchar;
	}

	return query;
}

function comodin(strg) {
	var co = /\b([^\w\*])/g;
	var cin = /([^\w\@\+\-\.\,\:\;])/g;
	cin.compile(cin);
	var b = false;
	var i = strg.indexOf('*');
	var f = strg.lastIndexOf('*');
	if (i == 0 && f == (strg.length - 1)) {
		if (strg.length > 4) {
			b = true;
		} else {
			$('#load')
					.html(
							'Introduzca 3 letras como minimo y los comodines \'*\' para realizar la busqueda')
					.addClass('load').removeClass('error');
		}
	} else if (strg == '*' || i == 0 || f == (strg.length - 1)) {
		if (strg.length > 2) {
			b = true;
		} else {
			$('#load')
					.html(
							'Introduzca 2 letras como minimo y \'*\' para realizar la busqueda')
					.addClass('load').removeClass('error');
		}
	} else {
		strg = strg.replace(co, '');
		strg = strg.replace(cin, '');
		str = strg;
		$('#cad').val(str);
		b = true;
	}
	return b;
}

function changetoUpperCase() {
	key = window.event.keyCode;
	if ((key > 0x60) && (key < 0x7B))
		window.event.keyCode = key-0x20;
}