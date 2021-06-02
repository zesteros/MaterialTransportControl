let URLFUNCTIONSGENERAL =  "/cgi-bin/util/general/functions_general.cgi";
let URLFUNCTIONSOBRA = "/cgi-bin/util/obra/functions_obra.cgi";
let URLFUNCTIONSOBRAPROVEEDOR = "/cgi-bin/util/obra/functions_obra_proveedor.cgi";
let URLFUNCTIONSPERSONA = "/cgi-bin/util/general/functions_general_persona.cgi";
let PROYECTO = 82;// SE PONE COMO 1 GESTION DE OBRA
let CONFIGURACION;
let SISTEMA = 3; //PAGO ACARREO
let MODULOOBRAS = 453;

let URLFUNCTIONSACARREOS =  "/cgi-bin/acarreos_app/functions.cgi";
let URLFUNCTIONSPAGO =  "/cgi-bin/acarreos_app/help/functionpago.cgi";

class TableLigass{
	constructor(ele,params) {
		this.config = {
			opt:0,
		};//FIN del config
		this.local={
		};//FIN del local config
		this.ele=ele;
		this.config = {...this.config, ...params};
			this.generate();
	}
	generate(){
		//console.log("entro al table ligas");
		let css2 = `
		#rightmenu {
			width:110px;
            text-align:left;
            top:0;
            position:absolute;
            right:0;
		}`;
		addcss(css2);
		let html=`
		<div id="rightmenu" class="list-group listUlDir">
		<input type="text" class="inputTextFilter" id="inputFindAplicaciones" placeholder="Buscar Módulo" autocomplete="off">
		</div>`;
		
		let body = document.getElementsByTagName('body')[0];
    	this.ele = document.createElement('div');
    	this.ele.className="PopupPanelTable";
    	body.appendChild(this.ele);

		if(vee(this.ele)){
			this.ele.innerHTML=html;
			this.my_table_general_dir = this.ele.getElementsByClassName('listUlDir')[0];
		}
	} 
	//Add class liga
	addLiga(classLiga){
		let ele=j(classLiga.ligaHtml);
		j(this.my_table_general_dir).append(ele);
		ele.click(function(event){
			cerrar();
			scrollup();
			if(event.currentTarget.innerHTML == "Ubicaciones")
				j('#contenedorecursos').fadeIn(1000);
			if(event.currentTarget.innerHTML == "Bancos Material")
				j('#contenedorbancosmaterial').fadeIn(1000);
			if(event.currentTarget.innerHTML == "Comedores")
				j('#contenedorcomedor').fadeIn(1000);
			if(event.currentTarget.innerHTML == "Renta camion")
				j('#contenedorentacamion').fadeIn(1000);
			if(event.currentTarget.innerHTML == "Hospedaje")
				j('#contenedorhospedaje').fadeIn(1000);
		});
		function cerrar(){
			j('#contenedorbancosmaterial').fadeOut(200);
		}
		function scrollup(){
			document.documentElement.scrollTop = 0;
			j(window).scrollTop(0);
		}
	}
}
class Liga{
	constructor(ele,params) {
		//ele elemento a donde queremos digigur la liga
		this.config = {
			nombre:"",
		};//FIN del config
		this.local={
		};//FIN del local config
		this.ele=ele;
		this.config = {...this.config, ...params};
		this.ligaHtml=`<a class="list-group-item findModulos" style="padding: 5px 5px;" onmouseover="style.background ='grey';style.color='white'" onmouseout="style.background ='white';style.color='#555555'">${this.config.nombre}</a>`;
	}//...
}

class TableMenu{
	constructor(ele,params) {
		this.config = {
			opt:0,
		};//FIN del config
		this.local={
		};//FIN del local config
		this.ele=ele;
		this.config = {...this.config, ...params};
		this.generate();
	}
	generate(){
	 let html=`
		<!--Navbar-->
		<div>
		    <nav id="myNavbar" class="navbar navbar-light" style="background-color: #F7DC6F;  role="navigation">
		        <!-- Brand and toggle get grouped for better mobile display -->
		        <div class="container">
		            <!-- Collect the nav links, forms, and other content for toggling -->
		            <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
		                <ul class="nav navbar-nav" id="ulistaopciones">
		                	
		                	<!--<li class="dropdown">
						        <a href="#" data-toggle="dropdown" class="dropdown-toggle">Unidades<b class="caret"></b></a>
						         <ul class="dropdown-menu">
						            <li><a href="$homeconfiguracion?cmd=listUnidades">Unidades</a></li>
						        </ul>
						    </li>-->
		                </ul>
		            </div>  <!-- /.navbar-collapse -->
		        </div> <!-- /.container -->
		    </nav>
		</div>	`;
		this.menu = document.getElementById("divmenu");
		this.menu.innerHTML=html;
		this.opciones = document.getElementById("ulistaopciones");
	} 
	addmenu(classLiga){
		let sub=j(classLiga.ligaHtml);
		j(this.opciones).append(sub);
	}
	addLiga(classLiga,listmenu){
		let sub=j(classLiga.ligaHtml);
		j(this.opciones).append(sub);
		sub.click(function(event){
			cerrar();
			scrollup();
			listmenu.forEach((obj)=>{
				if(event.currentTarget.innerText == obj.nombre)
					j('#'+ obj.divcontenedor).fadeIn(1000);
			});
		});
		function cerrar(){
			listmenu.forEach((obj)=>{
				j('#'+ obj.divcontenedor).fadeOut(100);
			});
		}
		function scrollup(){
			document.documentElement.scrollTop = 0;
			j(window).scrollTop(0);
		}
	}
}
class menu{
	constructor(ele,params) {
		//ele elemento a donde queremos digigur la liga
		this.config = {
			nombre:"",
		};//FIN del config
		this.local={
		};//FIN del local config
		this.ele=ele;
		this.config = {...this.config, ...params};
		//this.ligaHtml=`<a class="list-group-item findModulos" style="padding: 5px 5px;" onmouseover="style.background ='grey';style.color='white'" onmouseout="style.background ='white';style.color='#555555'">${this.config.nombre}</a>`;
		this.ligaHtml=`<li><a class="findModulos" onmouseover="style.background ='grey';style.color='white'" onmouseout="style.background ='#F7DC6F';style.color='#555555'">${this.config.nombre}</a></li>`;
	}//...
}

class menulist{
	constructor(idList,params) {
		this.config = { nombre:"", };//FIN del config
		this.local={};//FIN del local config
		this.config = {...this.config, ...params};
		this.ligaHtml=`<li class="dropdown">
			<a data-toggle="dropdown" class="dropdown-toggle" onmouseover="style.background ='grey';style.color='white'" onmouseout="style.background ='#F7DC6F';style.color='#555555'">${this.config.nombre}<b class="caret"></b></a>
			<ul class="dropdown-menu" id="${idList}"> </ul>
		</li>`;
	}//...
} 

class submenu{
	constructor(ele,params) {
		//ele elemento a donde queremos digigur la liga
		this.config = { nombre:"",idlist:"" };//FIN del config
		this.local={};//FIN del local config
		this.ele=ele;
		this.config = {...this.config, ...params};
		this.ligaHtml=`<li><a class="findModulos" onmouseover="style.background ='grey';style.color='white'" onmouseout="style.background ='#FFFFFF';style.color='#555555'">${this.config.nombre}</a></li>`;
	}//...
} 

class TableSubMenu{
	constructor(ele,params) {
		this.config = { opt:0, };//FIN del config
		this.local={ };//FIN del local config
		this.ele=ele;
		this.config = {...this.config, ...params};
	}
	//Add class liga
	addLiga(classLiga,listmenu){
		let ele=j(classLiga.ligaHtml);
		this.opciones = document.getElementById(classLiga.config.idlist);
		j(this.opciones).append(ele);
		ele.click(function(event){
			cerrar();
			scrollup();
			listmenu.forEach((obj)=>{
				if(event.currentTarget.innerText == obj.nombre)
					j('#'+ obj.divcontenedor).fadeIn(1000);
			});
		});
		function cerrar(){
			listmenu.forEach((obj)=>{ j('#'+ obj.divcontenedor).fadeOut(100); });
		}
		function scrollup(){
			document.documentElement.scrollTop = 0;
			j(window).scrollTop(0);
		}
	}
}

class getInfoPago{
		constructor(ele,params){
			this.config = {
				fk_user:0,
			};//FIN del config
			this.local={
				cDivBoletosId:"input-text-Id",
				cTableBoletosId:"table-boletos-Id",
			};//FIN del local config
			this.formatNotify={type: 'info',animate: {
					enter: 'animated fadeInUp',
					exit: 'animated fadeOutRight'
				},offset: 20,spacing: 10,z_index: 1031,};
			this.ele=ele;
			this.config = {...this.config, ...params};
			this.getmenu();
		}
		getmenu(){
			var dataSet=new FormData();
			dataSet.append( "cmd", "getMenuObra" );
			dataSet.append( "fk_id_menu_sistema", SISTEMA );
			fetch(URLFUNCTIONSGENERAL,{method: 'POST',body:dataSet})
			.then(response => {
				return response.json();
			})
			.then(json => { /// CREAR PANEL DE BANCO
				if(!json.mensaje){
					let tabla = new TableMenu(json.ListMenuObra);
					let tablasub = new TableSubMenu(json.ListMenuObra);
					json.ListMenuObra.forEach((obj)=>{
						if(obj.fk_id_tipo_menu == 1){
							let contenedor = document.getElementById(obj.divcontenedor);
							let liga = new menu(contenedor,{nombre:obj.nombre});
							tabla.addLiga(liga,json.ListMenuObra);
						}else if(obj.fk_id_tipo_menu == 2) {
							let liga = new menulist(obj.divlist,{nombre:obj.nombre});
							tabla.addmenu(liga);
							obj.ListOpciones = (obj.ListOpciones == null) ? []:obj.ListOpciones;
							obj.ListOpciones.forEach((s)=>{
								let contenedor = document.getElementById(s.divcontenedor);
								let liga = new submenu(contenedor,{nombre:s.nombre,idlist:obj.divlist});
								tablasub.addLiga(liga,obj.ListOpciones);
							});
						}
					});
				}
				return json;
			}).
			then(json => { 
				this.perfiluser();
			})
			.then(json => { 
				this.generate();
			})
			.catch(error => {
				console.error('validateDomain: error', error);
				return error;
			});
		}
		perfiluser(){
			var dataSet=new FormData();
			dataSet.append( "cmd", "getPerfilUsuario" );
			dataSet.append( "fk_user", this.config.fk_user );
			dataSet.append( "fk_id_proyecto", PROYECTO);
			dataSet.append( "obras", MODULOOBRAS);
			fetch(URLFUNCTIONSGENERAL,{method: 'POST',body:dataSet})
			.then(response => {
				return response.json();
			})
			.then(json => { /// CREAR PANEL DE BANCO
				if(!json.mensaje){
					let tabla = new TableMenu(json.ListMenuObra);
					let tablasub = new TableSubMenu(json.ListMenuObra);
					json.ListMenuObra.forEach((obj)=>{
						if(obj.fk_id_tipo_menu == 1){
							let contenedor = document.getElementById(obj.divcontenedor);
							let liga = new menu(contenedor,{nombre:obj.nombre});
							tabla.addLiga(liga,json.ListMenuObra);
						}else if(obj.fk_id_tipo_menu == 2) {
							let liga = new menulist(obj.divlist,{nombre:obj.nombre});
							tabla.addmenu(liga);
							obj.ListOpciones = (obj.ListOpciones == null) ? []:obj.ListOpciones;
							obj.ListOpciones.forEach((s)=>{
								let contenedor = document.getElementById(s.divcontenedor);
								let liga = new submenu(contenedor,{nombre:s.nombre,idlist:obj.divlist});
								tablasub.addLiga(liga,obj.ListOpciones);
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
		}
		generate(){
			let contenidoHtml=`
				<div id="divmensajes" class="divmensajes"></div>
				<div class="row">
					<div class="panel panel-info">
						<div class="panel-heading clearfix">
							<a data-toggle="collapse" href="#accordionPanelPago"><h3 class="panel-title pull-left" style="padding-top: 7.5px;"><b>Boletos</b></h3></a>
						</div>
						<div id="accordionPanelPago" class="panel-collapse collapse in">
							<div id="accordionPanelPagoBody" class="panel-body">
								<center>
									<div class="form-group row">
										<label for="tipo_pago" class="col-sm-2 col-form-label">Tipo pago</label>
										<div class="col-sm-10">
											<select class="form-control" id="tipo_pago" class="col-sm-4">
												<option value="0"> Seleccionar </option>
												<option value="1"> Acarreo </option>
												<option value="2"> Regalía - Renta camión </option>
												<option value="3"> Regalía - Compra de material </option>
											</select>
										</div>
									</div>
									<div class="form-group row" id="${this.local.cDivBoletosId}" style="display:none">
										<label for="scan_tickets" class="col-sm-2 col-form-label">Boletos a pagar</label>
										<div class="col-sm-10">
										    <input type="text" style="font-size:15px;" name="scan_tickets" id="scan_tickets" placeholder="Escanea boletos">
										</div>
										<table  id="${this.local.cTableBoletosId}" class="cell-border" style="font-size:10px;">
		        							<thead>
									            <tr>
									            	<th></th>
									            	<th>Fecha salida</th>
									            	<th>Folio</th>
													<th>Placa trasera</th>
													<th>Origen</th>
													<th>Destino</th>
													<th>Material</th>
													<th>M3</th>
													<th>Elaboró</th>
													<th>Fecha entrega</th>
													<th>Recibió</th>
													<th>Tiempo</th>
													<th>Distancia</th>
													<th>Importe acarreo</td>
													<th>Importe material</td>
													<th>Subtotal</th>
												</tr>
									        </thead>
									        <tbody id="content"> </tbody>
									        <tfoot>
								  				<tr><th></th>
									            	<th></th>
									            	<th></th>
													<th></th>
													<th></th>
													<th></th>
													<th></th>
													<th></th>
													<th></th>
													<th></th>
													<th></th>
													<th></th>
													<th></th>
													<th></th>
													<th></th>
													<th></th>
												</tr>
									        </tfoot>
							   				</table>
									</div>
								</center>
							</div>
					      	<div class="panel-footer clearfix">
								<div class="btn-group pull-right">
									<button type="button" class="btn btn-success"><span class="glyphicon glyphicon-cog"></span> Generar pago / regalía</button>
								</div>
							</div>
						</div>
					</div>
				</div> `;

			this.ele.innerHTML=contenidoHtml;
			this.ele.style.display = 'inline';
			this.divmensajes=this.ele.getElementsByClassName("divmensajes")[0];
			this.tablaboletos = document.getElementById(this.local.cTableBoletosId);
			document.getElementById(`tipo_pago`).addEventListener('change', (event) => {this.getBuscarBoletos(event);});
			this.inputboletos=document.getElementById(`scan_tickets`);
			
			//j('#scan_tickets').tagsinput();
		}
		getBuscarBoletos(event){
			document.getElementById(this.local.cDivBoletosId).style.display = 'none';
			if(event.target.value == 0){
				sweetAlert("Oops...", "Es necesario elegir el tipo de pago a procesar", "warning");
			}
			else{
				switch (parseInt(event.target.value)) {
					case 1:case 2: 
						sweetAlert("Oops...", "No se tiene la opción diseñada", "warning");
					break;
					case 3:
						document.getElementById(this.local.cDivBoletosId).style.display = 'inline';
						this.tabladatatable = j(this.tablaboletos).DataTable({
							"language": {"url": "/lib/js/datatables.spanish.js"},
							"bPaginate": false, scrollX: true
						});
						//this.inputboletos.onpaste = this.getboletosInput(parseInt(event.target.value));
						this.inputboletos.addEventListener('paste', (e) => {this.getboletosInput(event.target.value,e);});
					break;
					default: sweetAlert("Oops...", "No se tiene la opción diseñada", "warning"); break;
				}
				
			}
		}
		getboletosInput(tipopago,e){
			let existInTable = false;
			let sheetNumber = (e.clipboardData || window.clipboardData).getData('text');
			console.log(this.tabladatatable);
			if(existInTable == false){
				this.addTicketToTable(tipopago,sheetNumber)
			}
		}
		addTicketToTable(tipopago,sheetNumber){
			let existInDatabase = false;
			var dataSet=new FormData();
			dataSet.append( "cmd", "getValidarBoleto" );
			dataSet.append( "sheet_number",sheetNumber);
			dataSet.append( "tipopago",tipopago);
			fetch(URLFUNCTIONSPAGO,{method: 'POST',body:dataSet})
			.then(response => {
				return response.json();
			})
			.then(json => {
				if(json.mensaje){
					sweetAlert("Oops...", "El boleto con no. de folio "+sheetNumber+" / "+json.mensaje, "warning");
					this.inputboletos.value="";
				}else{
					let ticket = json.ticket;
					let rowNode = this.tabladatatable.row.add([
		            "",
		            ticket.fecha_salida +" "+ticket.hora_salida,
		            ticket.folio,
		            ticket.plates,
		            ticket.origen,
		            ticket.destino,
		            ticket.material,
		            parseFloat(ticket.m3).toFixed(2) +" M3",
		            ticket.elaboro,
		            ticket.fecha_entrega +" " +ticket.hora_entrega,
		            ticket.recibio,
		            ticket.tiempo,
		            ticket.distancia,
		            ticket.importe_acarreo,ticket.importe_material,ticket.importe_acarreo
		            //formatNumber(ticket.importe_acarreo?ticket.importe_acarreo:"0"),
		            //formatNumber(ticket.importe_material?ticket.importe_material:"0"),
		            //formatNumber(parseFloat(ticket.importe_acarreo?ticket.importe_acarreo:"0")+parseFloat(ticket.importe_material?ticket.importe_material:"0"))
		          ])
		          .draw()
		          .node();
		          this.inputboletos.value="";
				}
				return json;
			})
			.then(json => { })
			.catch(error => {
				console.error('validateDomain: error', error);
				return error;
			});
		}

}//fin de la calse
function getboletosInput(tipopago,tabladatatable){
			let scanTicketsInternal = j("#scan_tickets").tagsinput('input');
 			scanTicketsInternal.focus();
  			scanTicketsInternal.on('change paste input', function(){
  			console.log(scanTicketsInternal);
  			let sheetNumber =  scanTicketsInternal.val();
  			let existInTable = false;
  			console.log(sheetNumber);
  				tabladatatable.column(2).data().each( function ( value, index ) {
  					console.log(value);
			          if(value==sheetNumber){
			            existInTable = true;

			            swal({
			              title: "Boleto repetido",
			              text:"El boleto con no. de folio: "+sheetNumber+" ya se capturó.",
			              type: "warning",
			              showCancelButton: false,
			              confirmButtonClass: "btn-danger",
			              confirmButtonText: "Aceptar",
			              closeOnConfirm: true
			            },
			            function(){
			              scanTicketsInternal.focus();
			            });
			          }
			    });
			    if(!existInTable){
			        if(addTicketToTable(sheetNumber)){
			          j('#scan_tickets').tagsinput('add', sheetNumber);
			          scanTicketsInternal.val("");
			        } else {
			           swal({
			              title: "Boleto inexistente",
			              text:"El ticket con no. de folio "+sheetNumber+" no existe o fue dado de baja.",
			              type: "warning",
			              showCancelButton: false,
			              confirmButtonClass: "btn-danger",
			              confirmButtonText: "Aceptar",
			              closeOnConfirm: true
			            },
			            function(){
			              scanTicketsInternal.focus();
			            });
			          j('#scan_tickets').tagsinput('remove', sheetNumber);
			        }
		      	}
  			});
		}

function addTicketToTable(sheetNumber){
		  	var existInDatabase = false;
			j.ajax({
			    url: "/cgi-bin/acarreos_app/functions.cgi",
			    dataType: "json",
			    type: "POST",
			    async: false,
			    data: {
			      cmd:"get_carry_tickets_by_sheet_number",
			      sheet_number: sheetNumber
			    },
			    success: function( data ) {

			      /*if(data.tickets != false){
			        if(data.tickets.length > 0){

			          var ticket = data.tickets[0];
			          var rowNode = ticketsToPayTable.row.add([
			            "",
			            ticket.fecha_salida +" "+ticket.hora_salida,
			            ticket.folio,
			            ticket.plates,
			            ticket.origen,
			            ticket.destino,
			            ticket.material,
			            parseFloat(ticket.m3).toFixed(2) +" M3",
			            ticket.elaboro,
			            ticket.fecha_entrega +" " +ticket.hora_entrega,
			            ticket.recibio,
			            ticket.tiempo,
			            ticket.distancia,
			            formatNumber(ticket.importe_acarreo?ticket.importe_acarreo:"0"),
			            formatNumber(ticket.importe_material?ticket.importe_material:"0"),
			            formatNumber(parseFloat(ticket.importe_acarreo?ticket.importe_acarreo:"0")+parseFloat(ticket.importe_material?ticket.importe_material:"0"))
			          ])
			          .draw()
			          .node();
			          existInDatabase = true;
			          }
			        } else {
			          existInDatabase = false;
			        }*/
			       
			      },
			      error: function(e){
			        console.log(e);
			        existInDatabase = false;
			      }
			  });
		    
		    return existInDatabase;
		}

(function(window){
  let _init = (ele,params)=> {
    let mi_ref_aux = new getInfoPago(ele,params);
    return mi_ref_aux;
  };
  window.getListInfoPago = {
    init:_init
  };
})(window);

j(document).ready(function(){
	try{
		let ele = document.getElementById("contenedorinfogeneral");
		var fk_user = document.getElementById("fk_user").value;
		getListInfoPago.init(ele,{idProyecto:PROYECTO,fk_user:fk_user});
	}catch(e){
		console.error("ocurrio un error al generar la vista de layaut instalaciones",e);
	}
	j("#inputFindAplicaciones").keyup(function(e){
	    if(j(this).val()==''){
	        j(".findModulos").show();
	    }else{
	        j(".findModulos").hide();
	        j(".findModulos").filter(function( index ) {
		        var string = j(this).text().toLowerCase(),
		        substring = j("#inputFindAplicaciones").val().toLowerCase();
		        var valCons = string.includes(substring);
		        return  string==substring ||  valCons;
	        }).show();
	    }
    });

});