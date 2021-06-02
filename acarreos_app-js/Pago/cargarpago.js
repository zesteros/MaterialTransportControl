
j(document).ready(function(){ 
	inicioLoad('#dialog2');
	setTimeout(()=>{
		j("#dialog2").hide();
		j("#mask").hide();
	},10000);
	
	var dataSet=new FormData();
	dataSet.append( "cmd", "getMenuObra" );
	dataSet.append( "fk_id_menu_sistema", 3 );
	fetch(URLFUNCTIONSGENERAL,{method: 'POST',body:dataSet})
	.then(response => { 	return response.json(); })
	.then(json => { /// CREAR PANEL DE BANCO
		if(!json.mensaje){
			json.ListMenuObra.forEach((obj)=>{
				if(obj.fk_id_tipo_menu == 1){
					if(obj.clase){
						let ele=document.getElementById(obj.divcontenedor);
						var fn = window[obj.clase];
						if (typeof fn === "function") fn.init(ele);
						ele.style.display = 'none';
					}
				}else if(obj.fk_id_tipo_menu == 2) {
					obj.ListOpciones = (obj.ListOpciones == null) ? []:obj.ListOpciones;
					obj.ListOpciones.forEach((s)=>{
						let ele=document.getElementById(s.divcontenedor);
						var fn = window[s.clase];
						if (typeof fn === "function") fn.init(ele);
						ele.style.display = 'none';
					});
				}
			});
		}
		return json;
	})
	.catch(error => {
		console.error('validateDomain: error', error);
		return error;
	});

});