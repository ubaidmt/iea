$(document).ready(function(){
	callback();
});

function body() {
	// Asigna altura a tabla principal
	var h = window.innerHeight;
	document.getElementById('tbBody').style.height = h > 0 ? (h - 10) + 'px'
			: (document.documentElement.clientHeight - 65);
}

function exit(){
	location.href = '../?exit=true';
}

function clear() {
	$('#error').innerHTML = '';
	$('#msg').innerHTML = '';
}

function callback(){
	$('#form').corner();
	$('#btnSalir').button();
	$('#btnBuscar').button();
}

function validarFrm() {
	var pexact = $('#radio_todas').checked;
	str = $('#cad').val();
	
	if(!validarRFC()){
		ordenarResultado('');
		return;
	}
	if (str.length > 0) {
		if (str == 'Todos Los Registros') {
			if (!pexact) {
				str = "rfc=" + $('#rfc').val() + '&pathContext=' + $('#pathContext').val();
			} else {
				str = "rfc=" + $('#rfc').val() + '&pathContext=' + $('#pathContext').val();
			}
			buscarDocumentos(str);
		} else {
			if (!pexact) {
				str = "cmd=" + getFiltro(str) + "&rfc=" + $('#rfc').val() + '&pathContext=' + $('#pathContext').val();
			} else {
				str = "cmd=" + str + "&rfc=" + $('#rfc').val() + '&pathContext=' + $('#pathContext').val();
			}
			buscarDocumentos(str);
		}
	}
}

function validarRFC(rfc) {
	clear();
	var rfc = $('#rfc').val();
	var rx = /^([a-zA-Z]{4})(\d{6})/;
	if(rfc == "" || rfc == null){
		$('#error').html('* El valor del RFC es obligatorio.');
	}else if (rfc.length < 10) {
		$('#error').html('* El valor del RFC est&aacute; incompleto.');
		return false;
	} else if (!rx.test(rfc)) {
		$('#error').html('* El valor del RFC es inv&aacute;lido.');
		return false;
	} else {
		return true;
	}
}

function ordenarResultado(data) {
	var html = '';
	var lst = data.listDocs;
	
	if (lst == null || lst.length < 1 || lst == 'undefined') {
		$('#resReg').html("0 registro(s) encontrado(s)");
		html += '<tr><td colspan="6" class="even" align="center">Sin registros</td></tr>';
	} else {
		$.each(lst, function(i, field) {
			html += '<tr>';
			html += '<td>' + field.rfc + '</td>';
			html += '<td>' + field.nombre + '</td>';
			html += '<td align="center">' + field.refTopografica + '</td>';
			html += '<td align="center">' + field.fechaNac + '</td>';
			html += '<td>' + field.tipoIngreso + '</td>';
			html += '<td class="link"><a href="' + field.link + '" target="_blank">' + field.name + '</a></td>';
			html += '</tr>';
		});
		$('#resReg').html(lst.length + " registro(s) encontrado(s)");
	}
	$('#tbBodyl').html(html);
}

function buscarDocumentos(params) {
	
	$('#img').show(0, function() {
		if (jQuery.browser.msie)
			this.style.removeAttribute('filter');
	});
	
	$('#img').removeClass('oculto').addClass('cargando');
	document.getElementById('img').style.display = 'block';
	
	$('#load').addClass('load').text('Buscando...');
	$
			.ajax({
				type : 'POST',
				url : '../searchCH',
				data : params,
				dataType : 'json',
				success : function(data, res, settings) {
					if (res == 'success') {
						if (data.exito == 1) {
							ordenarResultado(data);
							$('#load').html("");
						} else if (data.exito == 0) {
							ordenarResultado('');
							$('#load')
									.html('Ocurrió un error al realizar la cosulta, favor de remover símbolos especiales del ' +
										  'filtro <br/>Por ejemplo: +, -, <, >, etc.')
									.removeClass('load').addClass('load');
						}
					} else {
						$('#load')
								.html(
										'Error al procesar su solicitud, intente de nuevo')
								.removeClass('load').addClass('error');
					}
					$('#img').hide();
				},
				error : function(request, tipo, settings) {
					$('#load').html(
							'Error t: ' + tipo + ', request: ' + request + ' ('
									+ settings + ')').removeClass('load')
							.addClass('error');
					$('#img').hide();
				}
			});
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

function changeToUpperCase() {
	key = window.event.keyCode;
	if ((key > 0x60) && (key < 0x7B))
		window.event.keyCode = key-0x20;
}

