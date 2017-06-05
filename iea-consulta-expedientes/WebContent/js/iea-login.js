$(document).ready(function() {
	
	$(document).keypress(function(event) {
		  if ( event.which == 13 ){
				if($('#change-password').dialog('isOpen'))
					return validaModify();
				else
					return validaLoginFrm();
		  }
	});
	
	setCorner('form');
	setCorner('frmModify');

	$('#btnAceptar').button();
	$('#btnModificar').button();
	$('#btnCancel').button();
	
	$('#change-password').dialog({
		autoOpen : false,
		modal: true,
		width : 380,
		resizable: false
	});
	
	
	$('#imgChange').click(function() {
		cleanModify();
		$('#change-password').dialog('open');
	});
	

});

function setCorner(id){
	$('#' + id).corner();
}

function cleanModify(){
	$('#mod-user').val('');
	$('#oldPass').val('');
	$('#newPass').val('');
	$('#error-modify').html('');
}

function validaLoginFrm(){
	usuario = $('#usuario').val();
	password =$('#password').val();
	modulo =$('#modulo').val();
	
	if(usuario.length == 0 || password.length == 0){
		$('#error').html('* Los campos Usuario y Contraseña son requeridos.');
			if(usuario.length == 0)
				$('#usuario').focus();
			else
				$('#password').focus();
		return;
	}else if(modulo == ''){
		$('#error').html('* Debes elegir un m&oacute;dulo de acceso.');
		$('#modulo').focus();
		return;
	}else
		$('#form').submit();
	
}

function validaModify(){
	
	var usuario = $("#mod-user").val();
	var password = $("#oldPass").val();
	var new_pass = $("#newPass").val();

	if(usuario.length == 0 || usuario == ""){
		$('#error-modify').html("* Debes ingresar un nombre de usuario.");
		$("#mod-user").focus();
		return;
	}else if(password.length == 0 || password == ""){
		$('#error-modify').html("* Debes ingresar tu contraseña actual.");
		$("#oldPass").focus();
		return;
	}else if(new_pass.length == 0 || new_pass == ""){
		$('#error-modify').html("* Debes ingresar una nueva contraseña para hacer el cambio.");
		$("#newPass").focus();
		return;
	}else{
		$('#img').show(0, function() {
			if (jQuery.browser.msie)
				this.style.removeAttribute('filter');
		});
		
		$('#img').removeClass('oculto').addClass('cargando');
		
		$.ajax({
			url : 'modify',
			data : {
					'usuario' : usuario,
					'password': password,
					'new_password' : new_pass
					},
			type : 'POST',
			dataType : 'json',
			success : function(response, tipo, settings) {
				if(response.exito == 1){
					$('#change-password').dialog('close');
				}else{
					$('#error-modify').html("" + data.message);
				}
				$('#img').hide();
			},
			error: function(){
				$('#error-modify').html("* Usuario y/o Contraseña no v&aacute;lidos.");
				$('#img').hide();
			},

		});
	}
}




