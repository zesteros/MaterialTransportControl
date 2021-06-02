#!/usr/bin/perl

require 'cgi-lib.pl';
use Eflow::user_check;
use Eflow::Libs;
use MSQL_VB;
use CGI; 
use Encode;
use MIME::Lite;
use Eflow::GH;
use Eflow::jstl;
use utf8;
binmode STDOUT,':utf8';
use URI::Escape;
use CGI::Carp qw(fatalsToBrowser);
use Eflow::Utils;

print PrintHeader();
$|=1;
$qt = new CGI;
%in = $qt->Vars;
conectadb();
$lib->hash_decode(\%in);
#post
$user = user_check(1);##ID DEL EMPLEADO LOGEADO
$home ="/cgi-bin/acarreos_app";
$pDir = "$home/points.cgi";

require 'menu.pl';
($sec,$min,$hour,$mday,$mon,$year,$wday,$yday,$isdst) = localtime(time);

$CMD=$in{'cmd'}; 

my $gh=Eflow::GH->new();

my @js =("/lib/validate.js","/lib/navigate.js"); 
my @js_post =(
		"/lib/proj4js-2.5.0/proj4.js",
		"/lib/jquery/jquery-ui-1.11.1/jquery-ui.min.js",
		"/js/classie.js","/js/borderMenu.js?v=1.1",
		"/lib/js/jquery.dataTables.min.js",
		"/lib/js/datepicker.spanish.js",
		"/lib/js/multiselect/src/jquery.multiselect.js",
		"/lib/js/multiselect/src/jquery.multiselect.filter.js",
		"/js/SUtil/ajaxUtil.js?val=0.0.1",
		"/js/SUtil/utilNumbers.js?val=0.0.1",
		"/lib/js/select2.js",
		"/lib/js/jquery.dataTables.yadcf.js",
		"/js/SUtil/utilErrors.js",
		"/lib/jquery.multi-select.js",
		"/lib/sweetalert/sweetalert.min.js",
		"/lib/jquery/jquery.quicksearch.js",
		"/lib/excel-import/infragistics.core.js",
		"/lib/excel-import/infragistics.lob.js",
		"/lib/excel-import/infragistics.ext_core.js",
		"/lib/excel-import/infragistics.ext_collections.js",
		"/lib/excel-import/infragistics.ext_text.js",
		"/lib/excel-import/infragistics.ext_io.js",
		"/lib/excel-import/infragistics.ext_ui.js",
		"/lib/excel-import/infragistics.documents.core_core.js",
		"/lib/excel-import/infragistics.ext_collectionsextended.js",
		"/lib/excel-import/infragistics.excel_core.js",
		"/lib/excel-import/infragistics.ext_threading.js",
		"/lib/excel-import/infragistics.ext_web.js",
		"/lib/excel-import/infragistics.xml.js",
		"/lib/excel-import/infragistics.documents.core_openxml.js",
		"/lib/excel-import/infragistics.excel_serialization_openxml.js",
		"/lib/datetimepicker-0.0.11/js/bootstrap-datetimepicker.min.js"
	);
my @css=(
	"/css/style5.css?v=1.0.1",
	"/css/icons.css",
	"/lib/css/jquery.dataTables.min.css",
	"/lib/jquery/jquery-ui-1.11.1/jquery-ui.min.css",
	"/lib/js/multiselect/jquery.multiselect.css",
	"/lib/js/multiselect/jquery.multiselect.filter.css",
	"/lib/js/jquery.dataTables.yadcf.css",
	"/lib/js/select2.css",
	"/lib/multi-select.css",
	"/lib/sweetalert/sweetalert.css",
	"/lib/excel-import/infragistics.css",
	"/lib/excel-import/infragistics.theme.css",
	"/lib/datetimepicker-0.0.11/css/bootstrap-datetimepicker.min.css"
	);

if($in{'points_buildings_select'}){
	$gml = qq|&points_buildings_select=$in{'points_buildings_select'}|;
}

if(!$in{'noMenu'}){
	@links=(
		{ 'link'  => "$pDir?cmd=authorize_points&autorizado=0$gml", 'titleLink' => "Puntos por autorizar", 'class' => 'authorize_points','m'=>"$m_points_authorization",'p'=>"$pro",'u',"$user"},
		{ 'link'  => "$pDir?cmd=authorize_points&autorizado=1$gml", 'titleLink' => "Puntos autorizados", 'class' => 'autorizados','m'=>"$m_points_authorization",'p'=>"$pro",'u',"$user"},
		{ 'link'  => "$pDir?cmd=draw_throws$gml", 'titleLink' => "Dibujar tiros consecutivos", 'class' => 'draw_throws','m'=>"$m_draw_points",'p'=>"$pro",'u',"$user"},
		{ 'link'  => "$pDir?cmd=assign_banks$gml", 'titleLink' => "Asignar banco", 'class' => 'assign_banks','m'=>"$m_assign_banks",'p'=>"$pro",'u',"$user"}
	);

}

@log=();
$title = "Acarreos VISE - Bancos y tiros";
my $body = "";
my $foot = "";
my $ocem=0;
my $ac=Eflow::acceso->new();
eval {
	$CMD = $in{'cmd'};
    if (!$CMD) {
		$body = inicio();
		print "<input type=\"hidden\" value=\"inicio\" class=\"active-button\">";
	} elsif (exists &$CMD) {
		$body = &$CMD;
		print "<input type=\"hidden\" value=\"$CMD\" class=\"active-button\">";
	}else{
		die;
	}
	1;
} or do {
    my $e = $@;
    push @log ,{'log' => 'e' , 'mensaje' => "Algo paso mal no se encontro el metodo: $e\n"};
	$ocem = 1;
};
my $m=$gh->runHTML(\@js,\@css,\@links,$title,$body,$foot,"","",\@log,$ocem,\@js_post,\@MLateral,$in{'gmovilg'});
my $j=Eflow::jstl->new();
print $j->analiza($m);




sub inicio{

	return "";
}
=asignar_bancos

Subrutina para asignar bancos a una obra primero se llena el select
de bancos luego se muestra la tabla de bancos asignados
=cut
sub assign_banks{



	my $q = qq|
		select id_punto,nombre_banco, latitud, longitud 
		from 
			acarreos.dbo.acarreos_puntos 
		where 
			estatus = 'A' and
			tipo_punto in (1,3)
		order by
			cast(nombre_banco as varchar(200))
	|;

	$xpl2_acarreos_instance_1->execute($q);

	my $banks_option;

	while($r = $xpl2_acarreos_instance_1->getrow_hashref){
		my $selected = $id_punto eq $$r{'id_punto'} ? "selected" : "";
		$banks_option.=qq|<option value="$$r{'id_punto'}\|$$r{'latitud'},$$r{'longitud'}" co $selected>$$r{'nombre_banco'} ($$r{'latitud'},$$r{'longitud'})</option>|;
	}

=query_para_asignados
Se realiza una consulta para sacar los bancos asignados y mostrarlos al usuario.
=cut
	$q = qq|
		select obras.id_punto,
			obras.obra, puntos.nombre_banco
		from 
			acarreos.dbo.acarreos_puntos_por_obra obras 
				left join acarreos.dbo.acarreos_puntos puntos on obras.id_punto = puntos.id_punto
		where 
			/*obras.obra = 'OBRAS-1940' and*/
			obras.estatus = 'A' and 
			puntos.tipo_punto in (1,3) and 
			puntos.estatus = 'A'
		order by obras.obra;
	|;

	$xpl2_acarreos_instance_1->execute($q);

	my $table_content;

	while($r = $xpl2_acarreos_instance_1->getrow_hashref){
		$table_content.=qq|

			<tr>
				<td>$$r{'obra'}</td>
				<td>$$r{'nombre_banco'}</td>
				<td><button class="btn-danger" onclick="removeAssignation('$$r{'id_punto'}','$$r{'obra'}')"><i class="glyphicon glyphicon-remove"></i>Remover asignación</button></td>
			</tr>

		|;
	}


	my $html =  qq|
	<script type="text/javascript" src="/js/acarreos/config.js?v=$sec$min$hour$mday$mon"></script>
	<script type="text/javascript" src="/js/acarreos/assign_bank.js?v=$sec$min$hour$mday$mon"></script>
	<input type="hidden" name="user_session" id="user_session" value="$user">
	<form  action="points.cgi" method="get">
		<div class="form-group">
			<label for="point_to_assign" class="col-lg-2 control-label" >Punto a asignar</label>
			<input type="hidden" value="assign_bank" name="cmd" id="cmd">
			<div class="col-lg-10">
				<select name="point_to_assign" placeholder="Ingresa el nombre de un punto" id="point_to_assign">
					$banks_option
				</select>
				<button class="btn-success" onclick="viewOnMap()"><i class="glyphicon glyphicon-map-marker"></i>Ver en mapa</button>
			</div>
			<br><br>
			<label for="building_to_assign" class="col-lg-2 control-label" >Obra</label>
			<div class="col-lg-10">
					<select name ="building_to_assign" placeholder="Obra a asignar"  id="building_to_assign"></select>
			</div>
			<br>
			<button type="submit" class="col-lg-12 btn-primary" style="margin-bottom:40px; margin-top:20px;"><i class="glyphicon glyphicon-share"></i><b>ASIGNAR</b></button>
		</div>
	</form>
	<table id="assigned_table">
		<thead>
			<tr>
				<th>
					Obra
				</th>
				<th>
					Nombre del punto
				</th>
				<th>
					Acciones
				</th>
			</tr>
		</thead>
		<tbody>
			$table_content
		</tbody>
	</table>|;


	return $html;
}

sub assign_bank{

	my $point_to_assign =  $in{'point_to_assign'};
	my $building_to_assign = $in{'building_to_assign'};

	my @data = split /\|/, $point_to_assign;

	my $id_punto = $data[0];

	my $q = qq|
		select id_punto
		from acarreos.dbo.acarreos_puntos_por_obra
		where estatus = 'A' and 
			estatus = 'A' and id_punto = $id_punto and obra = '$building_to_assign';
	|;
	$xpl2_acarreos_instance_1->execute($q);

	if(!$xpl2_acarreos_instance_1->EOF){
		return " ".$gh->alertM("El punto ya esta asignado a la obra ".$building_to_assign,"e").assign_banks();
	} else {
		$q = qq|
			INSERT INTO acarreos.dbo.acarreos_puntos_por_obra
			(id_punto, obra, add_date, add_user, upd_date, upd_user, estatus)
			VALUES($id_punto, '$building_to_assign', getdate(), $user, getdate(), $user, 'A');

			update acarreos.dbo.acarreos_puntos
			set upd_date=getdate(), upd_user=$user
			where id_punto = $id_punto;
		|;
		$xpl2_acarreos_instance_1->execute($q);
		return " ".$gh->alertM("Punto asignado exitosamente.","i").assign_banks();
	}
}

sub authorize_points{

	my $building = shift;

	my $authorized = shift;

	if(!$authorized){
		$authorized = $in{'autorizado'} ? $in{'autorizado'} : 0;
	}

	if(!$building){
		$building = $in{'points_buildings_select'};
	}

	my $content_bank = get_points_by_type($building, $authorized, 1);

	my $content_throw = get_points_by_type($building, $authorized, 2);


	my $authorize_all_button = $authorized == 0 ?  qq|

		<div align="right"><button type='button' style="width:30%" class='btn btn-warning' onclick="authorizeAll('$building')">Autorizar todos</button></div>
	| : qq||;

	$html=qq|
	<input type="hidden" name="user_session" id="user_session" value="$user">
	<script type="text/javascript" src="/js/acarreos/config.js?v=$sec$min$hour$mday$mon"></script>
	<script type="text/javascript" src="/js/acarreos/points.js?v=$sec$min$hour$mday$mon"></script>
	<script>
		function loadMaterialsToAssignByBuilding(point){
		  var building = j("#selected_building").val();
		  j.ajax({
		    type: 'POST',
		    url: '/cgi-bin/acarreos_app/functions.cgi',
		    dataType: 'json',
		    data: { 
		      cmd:"get_materials_by_building",
		      obra: building,
		      id_punto:point
		    },
		    success: function(d){

		      //var selectMaterials = j("#materials-select-dialog");
		      j("#materials-select-dialog").empty();
		      j("#materials-select-dialog").change();

		      j.each(d.materials,function(i,o){

		        var selected = o.id_material_por_punto != 0 ? "selected":"";
		         j("#materials-select-dialog").append(
		        '<option value=' + o.id_asignacion+' '+selected+'>'
		         + o.id_material_navision+" - "+ o.descripcion    + '</option>');
		      });
		      j("#materials-select-dialog").multiSelect({
			      selectableHeader: "<input type='text' class='search-input' style='margin-bottom:20px;' autocomplete='off' placeholder='Buscar material a asignar{-o+'>",
			      selectionHeader: "<input type='text' class='search-input' style='margin-bottom:20px;' autocomplete='off' placeholder='Buscar material asignado'>",
			      afterInit: function(ms){
			        var that = this,
			            \$selectableSearch = that.\$selectableUl.prev(),
			            \$selectionSearch = that.\$selectionUl.prev(),
			            selectableSearchString = '#'+that.\$container.attr('id')+' .ms-elem-selectable:not(.ms-selected)',
			            selectionSearchString = '#'+that.\$container.attr('id')+' .ms-elem-selection.ms-selected';

			        that.qs1 = \$selectableSearch.quicksearch(selectableSearchString)
			        .on('keydown', function(e){
			          if (e.which === 40){
			            that.\$selectableUl.focus();
			            return false;
			          }
			        });

			        that.qs2 = \$selectionSearch.quicksearch(selectionSearchString)
			        .on('keydown', function(e){
			          if (e.which == 40){
			            that.\$selectionUl.focus();
			            return false;
			          }
			        });
			      },
			      afterSelect: function(){
			        this.qs1.cache();
			        this.qs2.cache();
			      },
			      afterDeselect: function(){
			        this.qs1.cache();
			        this.qs2.cache();
			      }
			    });
		      j("#materials-select-dialog").multiSelect('refresh');
		      //j("#materials-select-dialog").change();
		    },
		    error: function(e){
		      console.log(e);
		    }
		  }); 
		}

	</script>

		<input type="hidden" id="authorized-flag" value="$authorized">
		<input type="hidden" id="selected_building" value="$building">

		<div class="modal fade" id="DescModal" role="dialog">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-hidden="true">X</button>
						<h3 class="modal-title">Autorización de puntos</h3>
					</div>
					<div class="modal-body">
						<div class="row dataTable">
							<div class="col-md-4">
								<label class="control-label">Tipo punto</label>
							</div>
							<div class="col-md-4">
								<select id="point-type-dialog" style="margin-top: 10px; margin-bottom:10px;"> 
									<option value="1">Banco</option>
									<option value="2">Tiro</option>
									<option value="3">Banco de desperdicio</option>
								</select>
							</div>
						</div>
						<div class="row dataTable">
							<div class="col-md-4">
								<label class="control-label">Nombre de punto</label>
							</div>
							<div class="col-md-8">
								<input type="text" class="form-control" maxlength="50" id="point-name-dialog" name="point-name-dialog">
							</div>
						</div>
						<div class="row dataTable">
							<div class="col-md-4">
								<label class="control-label">Cadenamiento</label>
							</div>
							<div class="col-md-8">
								<input type="text" class="form-control" maxlength="30" id="chainage-dialog" name="chainage-dialog">
							</div>
						</div>
						<div class="row dataTable">
							<div class="col-md-4">
								<label class="control-label">Radio</label>
							</div>
							<div class="col-md-8">
								<input type="number" step="any" class="form-control" maxlength="30" id="radio-dialog" name="radio-dialog">
							</div>
						</div>
						<div class="row dataTable">
							<div class="col-md-4">
								<label class="control-label">Latitud</label>
							</div>
							<div class="col-md-8">
								<input type="number" step="any" class="form-control" maxlength="30" id="latitude-dialog" name="latitude-dialog">
							</div>
						</div>
						<div class="row dataTable">
							<div class="col-md-4">
								<label class="control-label">Longitud</label>
							</div>
							<div class="col-md-8">
								<input type="number" step="any" class="form-control" maxlength="30" id="longitude-dialog" name="longitude-dialog">
							</div>
						</div>
						<div class="row dataTable">
							<div class="col-md-4">
								<label class="control-label">Es banco y tiro</label>
							</div>
							<div class="col-md-8">
								<input type="checkbox" class="editor-active" id="is-bank-and-throw" style="-ms-transform: scale(3); /* IE */
					  -moz-transform: scale(3); /* FF */
					  -webkit-transform: scale(3); /* Safari and Chrome */
					  -o-transform: scale(3); /* Opera */
					  padding: 12px;
					  margin-left:40px; margin-top: 10px;">
							</div>
						</div>
					<br>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-default " data-dismiss="modal" id="authorize-button">Autorizar</button>
						<button type="button" data-dismiss="modal" class="btn btn-primary">Cancelar</button>
					</div>
				</div>
				<!-- /.modal-content -->
			</div>
			<!-- /.modal-dialog -->
		</div>
		<!-- /.modal -->

		<div class="modal fade" id="assign-material-dialog" role="dialog">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-hidden="true">X</button>
						<h3 class="modal-title">Asignación de materiales a banco</h3>
					</div>
					<div class="modal-body">
						<div align="center">
						<select class="form-control" id="materials-select-dialog" name="materials-select-dialog" multiple='multiple' >
						</select>
						</div>
					</div>
					<div class="modal-footer">
						<input type="hidden" name="selected_point" id="selected_point" value="">
						<button type="button" class="btn btn-default " data-dismiss="modal" id="save-materials-dialog-button" onclick="saveMaterialsAssignation()">Asignar</button>
						<button type="button" data-dismiss="modal" class="btn btn-primary">Cancelar</button>
					</div>
				</div>
			</div>
		</div>
		<div class="modal fade" id="assign-distances-dialog" role="dialog">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-hidden="true">X</button>
						<h3 class="modal-title">Asignación de distancias y tarifas a banco</h3>
					</div>
					<div class="modal-body">
						<div class="form-group">
							<label for="distance_to_add" class="col-lg-4 control-label" >Distancia</label>
							<div class="col-lg-8">
								<input type="number" name="distance_to_add" placeholder="Ingresa una distancia" step="0.1" id="distance_to_add">
							</div>
							<label for="km_inicial" class="col-lg-4 control-label" >KM inicial</label>
							<div class="col-lg-8">
			 					<input type="number" name ="km_inicial" placeholder="Tarifa KM inicial" step="0.1" id="km_inicial">
			 				</div>
							<label for="km_subsecuente" class="col-lg-4 control-label" >KM subsecuente</label>
							<div class="col-lg-8">
								<input type="number" name="km_subsecuente" placeholder="Tarifa KM subsecuente" step="0.1" id="km_subsecuente">
							</div>
							<!--<label for="time_elapsed" class="col-lg-4 control-label" >Tiempo de traslado</label>
							<div class="col-lg-8">
								<input data-format="hh:mm:ss" type="text" name="time_elapsed" placeholder="Ingresa tiempo" id="time_elapsed">
							</div>-->
							<br>
							<button class="col-lg-12" style="margin-bottom:40px; margin-top:20px;" onclick="addDistance()"><b>AGREGAR +</b></button>
							
							<div class="col-lg-12" align="center">
								<ul class="list-group" id="distances-list">
								</ul>
							</div>
						</div>
					</div>
					<div class="modal-footer">
						<input type="hidden" name="selected_point" id="selected_point" value="">
						<button type="button" class="btn btn-default " data-dismiss="modal" id="save-distances-dialog-button">Aceptar</button>
						<button type="button" data-dismiss="modal" class="btn btn-primary">Cancelar</button>
					</div>
				</div>
			</div>
		</div>
	<style>
      /* Always set the map height explicitly to define the size of the div
       * element that contains the map. */
      #map-throws {
        height: 50%;
      }
      #map-banks {
        height: 50%;
      }
    </style>

	<div class="panel panel-info">
		<div class="panel-heading">Opciones</div>
		<div class="panel-body">
			<form class="form-horizontal" name="F" id="F" action="points.cgi" method="post" enctype="multipart/form-data"> 
				<div class="form-group" id="divEmpleado">
					<label for="points_buildings_select" class="col-lg-2 control-label" id="points_buildings_select_label">Selecciona una obra</label>
					<div class="col-lg-10">
						<input type="hidden" id="cmd" name="cmd" value="authorize_points">
						<input type="hidden" id="autorizado" name="autorizado" value="$authorized">
						<select id="points_buildings_select" name="points_buildings_select">

						</select>
						<button type="submit" class="btn btn-primary btn-lg" style="margin-left: 80px;">Buscar</button>
					</div>
				</div>
			</form>
	  	</div>
	</div>
	<div class="panel panel-success">
	  <div class="panel-heading">Bancos</div>
	  <div class="panel-body">
		    <table  id="banks-table">
		        <thead>
		            <tr>
		            	<th>
		            		Acciones
		            	</th>
						<th>
							Nombre
						</th>
						<th>
							Cadenamiento
						</th>
						<th>
							Radio
						</th>
						<th>
							Usuario agregó
						</th>
						<th>
							Coordenadas
						</th>
						<th>
							Obra
						</th>
					</tr>
		        </thead>
		        <tbody>
		            $content_bank
		        </tbody>
       		</table>
       		<br>
			<div id="map-banks"></div>

	  </div>
	</div>
	<br>
	<br>
	<br>
	<br>
	<div class="panel panel-primary">
	  <div class="panel-heading">Tiros $authorize_all_button</div> 
	  <div class="panel-body">
	    	 <table id="throws-table">
		        <thead>
		            <tr>
		            	<th>
		            		Acciones
		            	</th>
						<th>
							Es banco
						</th>
						<th>
							Nombre
						</th>
		            	<th>
							Cadenamiento
						</th>
						<th>
							Radio
						</th>
						<th>
							Usuario agregó
						</th>
						<th>
							Coordenadas
						</th>
						<th>
							Obra
						</th>
					</tr>
		        </thead>
		        <tbody>
		            $content_throw
		        </tbody>
       		</table>
       		<br>
       		<div id="map-throws"></div>
	  </div>
	</div>	
		<script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyAL0t-HODDCPg5-tEGzQAmiI_JQODfMFLw&callback=initMap&v=$sec$min$hour$mday$mon" async defer></script>


	|;	
	return $html;		
}

sub get_points_by_type{

	my $building = shift;

	my $authorized = shift;

	my $point_type = shift;

	$point_type = $point_type eq 1 ? "1,3" : $point_type;


	$sql=qq|

		SELECT 
			aca.id_punto,
			aca.tipo_punto,
			aca.nombre_banco,
			aca.radio,
			aca.cadenamiento,
			aca.es_banco_y_tiro,
			aca.latitud, 
			aca.longitud,
			aca.autorizado,
			convert(varchar(100),aca.reg_date,103) as fecha_registro,
			convert(varchar(100),aca.reg_date,108) as hora_registro,
			aca.estatus, 
			convert(varchar(100),aca.add_date,103) as fecha_agregado,
			convert(varchar(100),aca.add_date,108) as hora_agregado, 
			aca.add_user, 
			aca.upd_date, 
			aca.upd_user,
			eflow.name,
			eflow.first_lastname,
			eflow.second_lastname,
			eflow.super_nomina_id,
			puntos_por_obra.obra
		FROM 
			(acarreos.dbo.acarreos_puntos aca left join
			usuario.dbo.sn_eflow eflow on aca.add_user = eflow.userid_eflow) inner join
			acarreos.dbo.acarreos_puntos_por_obra puntos_por_obra on aca.id_punto = puntos_por_obra.id_punto  
		WHERE aca.estatus = 'A' and autorizado = $authorized and tipo_punto in ($point_type) and puntos_por_obra.obra = '$building' and puntos_por_obra.estatus = 'A';

	|;

	$xpl2_acarreos_instance_1->execute($sql);

	$content = qq||;

	while ($d = $xpl2_acarreos_instance_1->getrow_hashref) {

		$$d{'radio'} = $lib->fnumeric($$d{'radio'});
		
		my $authorize_button = $authorized == 0 ? qq|
				<button type='button' style="width:100%" class='btn btn-warning' onclick="updatePoint('$$d{'id_punto'}', '0');">
					Autorizar
					<i class="glyphicon glyphicon-ok" style="margin-left:10px;"></i>
				</button>
				<br>
		| : qq||
		;
		
		my $asign_materials_button = $authorized != 0 ? qq|
				<button type='button' style="width:100%" class='btn btn-success' onclick="assignMaterial('$$d{'id_punto'}');">
					Asignar materiales
					<i class="glyphicon glyphicon-import" style="margin-left:10px;"></i>
				</button><br>
		| : qq||
		;

		if($point_type == 2 ){$asign_materials_button = "";	}

		my $assign_distances_button = $authorized != 0 ? qq|

				<button type='button' style="width:100%" class='btn btn-warning' onclick="assignDistances('$$d{'id_punto'}');">
					Asignar distancias
					<i class="glyphicon glyphicon-resize-full" style="margin-left:10px;"></i>
				</button><br>
		| : qq||
		;

		#if($point_type == 2){$assign_distances_button = "";}
		my $edit_button = $authorized != 0 ? qq|


				<button type='button' style="width:100%" class='btn btn-primary' onclick="updatePoint('$$d{'id_punto'}','1');">
					Editar
					<i class="glyphicon glyphicon-edit" style="margin-left:10px;"></i>
				</button><br>
		| : qq||
		;

		my $reject_button_text = $authorized == 0 ? "Rechazar punto" : "Dar de baja punto";

		my $reject_button = qq|

				<button type='button' style="width:100%" class='btn btn-danger' onclick="rejectPoint('$$d{'id_punto'}','$authorized');">
					$reject_button_text
					<i class="glyphicon glyphicon-remove" style="margin-left:10px;"></i>
				</button><br>
		|;

		my $checked = $$d{'es_banco_y_tiro'} == 1 ? "checked" : "";

		my $is_bank_checkbox = $point_type == 2 ? qq|

			<td>
				<input type="checkbox" class="editor-active" style="-ms-transform: scale(3); /* IE */
				  -moz-transform: scale(3); /* FF */
				  -webkit-transform: scale(3); /* Safari and Chrome */
				  -o-transform: scale(3); /* Opera */
				  padding: 12px;" $checked disabled>
			</td>
		| : qq||;



		$content.=qq¡
			<tr>
				<td>
				$edit_button
				$authorize_button
				$asign_materials_button
				$assign_distances_button
				<button type='button' style="width:100%" class='btn btn-info' onclick="showInMap('$$d{'latitud'}','$$d{'longitud'}',$point_type);">
						Ver en mapa
						<i class="glyphicon glyphicon-map-marker" style="margin-left:10px;"></i>
					</button>
				$reject_button
				<br>
				</td>
				$is_bank_checkbox
				<td >
					$$d{'nombre_banco'} 
				</td>
				<td>
			 		$$d{'cadenamiento'}
				</td>
				<td>
					$$d{'radio'} 
				</td>
				<td>
					$$d{'name'} $$d{'first_lastname'} $$d{'second_lastname'}  - $$d{'super_nomina_id'}
				</td>
				<td>
					$$d{'latitud'},$$d{'longitud'}
				</td>
				<td>
					$$d{'obra'}
				</td>
				
			</tr>
		¡;
	}

	return $content;
}
sub draw_throws{
	my $html = qq|
	<input type="hidden" name="user_session" id="user_session" value="$user">

	<script type="text/javascript" src="/js/acarreos/config.js?v=$sec$min$hour$mday$mon"></script>
	<script type="text/javascript" src="/js/acarreos/points.js?v=$sec$min$hour$mday$mon"></script>

		<style>
      /* Always set the map height explicitly to define the size of the div
       * element that contains the map. */
      #map-draw-throws {
        height: 50%;
      }
       #description {
        font-family: Roboto;
        font-size: 15px;
        font-weight: 300;
      }

      #infowindow-content .title {
        font-weight: bold;
      }

      #infowindow-content {
        display: none;
      }

      #map #infowindow-content {
        display: inline;
      }

      .pac-card {
        margin: 10px 10px 0 0;
        border-radius: 2px 0 0 2px;
        box-sizing: border-box;
        -moz-box-sizing: border-box;
        outline: none;
        box-shadow: 0 2px 6px rgba(0, 0, 0, 0.3);
        background-color: #fff;
        font-family: Roboto;
      }

      #pac-container {
        padding-bottom: 12px;
        margin-right: 12px;
      }

      .pac-controls {
        display: inline-block;
        padding: 5px 11px;
      }

      .pac-controls label {
        font-family: Roboto;
        font-size: 13px;
        font-weight: 300;
      }

      #pac-input {
        background-color: #fff;
        font-family: Roboto;
        font-size: 15px;
        font-weight: 300;
        margin-left: 12px;
        padding: 0 11px 0 13px;
        text-overflow: ellipsis;
        width: 400px;
      }

      #pac-input:focus {
        border-color: #4d90fe;
      }

      #title {
        color: #fff;
        background-color: #4d90fe;
        font-size: 25px;
        font-weight: 500;
        padding: 6px 12px;
      }
      #target {
        width: 345px;
      }
      .modal-body {
 		max-height:500px;
    	overflow:auto;
	  }
	  div.scroll {
      	overflow:auto;
	  }
    </style>
    <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/proj4js/2.5.0/proj4.js"></script>

    <div class="panel panel-success">
	  <div class="panel-heading">Opciones</div>
	  <div class="panel-body">
	  	<div align="center">
	  	<label for="coordinates_file">Obtener coordenadas de excel</label>
	  	<br>
	  	<input type="file" id="coordinates_file" accept="application/vnd.ms-excel, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"/>
        <div id="result"></div>
        <table id="grid1"></table>
		<br>
	  	<br>
		<label for="radio-draw">Radio de cada tiro:</label>
		<br>
		<input type="number" step="0.1" placeholder="Ingresa un radio" id="radio-draw"> 
		<br>
		<br>
		<label for="radio-draw" id="label-chainage">Cadenamiento inicial:</label>
		<br>
		<input type="number" placeholder="KM" id="km-chainage"> <b id="plus-sign">+</b> <input type="number" placeholder="M" id="m-chainage">
		<br>
		<br>
		<br>
		<button type="button" class="btn btn-success " id="start-end-drawing" onclick="startEndDrawing()">Comenzar dibujo de coordenadas</button>
		<button type="button" class="btn btn-warning " id="clean-drawing-map" onclick="cleanMap()">Reiniciar dibujado</button>
		<button type="button" class="btn btn-info " id="backward-drawing-map" onclick="backwardDrawing()">Retroceder punto</button>
		</div>
	  </div>
	</div>
	<br>
	<br>
	<div class="modal fade" id="assign-throw-to-building" role="dialog">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-hidden="true">X</button>
						<h3 class="modal-title">Asignación de tiro a obra</h3>
					</div>

					<div class="modal-body">
						<div align="center">
							Obra: <select class="form-control" id="points_buildings_select" name="points_buildings_select" multiple='multiple' >
							</select>
							<br>
						</div>
						<br>
						<br>
						<div height="50%">
							<div class='scroll'>
								<table id="new-drawn-throw">
									<thead>
										<tr>
											<th>No.</th>
											<th>Coordenadas</th>
											<th>Radio</th>
											<th>Cadenamiento</th>
										</tr>
									</thead>
									<tbody>

									</tbody>
								</table>
							</div>
						</div>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-default " data-dismiss="modal" id="save-materials-dialog-button" onclick="saveDrawnThrows()">Asignar tiros</button>
						<button type="button" data-dismiss="modal" class="btn btn-primary">Cancelar</button>
					</div>
				</div>
			</div>
		</div>
	<div class="panel panel-primary" id="panel-drawing">
	  <div class="panel-heading" id="draw-panel-title">Dibujar tiros</div>
	  <div class="panel-body">
	      	<input id="pac-input" class="controls" type="text" placeholder="Busca un lugar">
       		<div id="map-draw-throws"></div>
	  </div>
	</div>



	<script>
function initAutocomplete() {
  mapDrawThrows = new google.maps.Map(document.getElementById('map-draw-throws'), {
    center: {lat: -33.8688, lng: 151.2195},
    zoom: 13,
    mapTypeId: 'roadmap'
  });
   if (navigator.geolocation) {
     navigator.geolocation.getCurrentPosition(function (position) {
        initialLocation = new google.maps.LatLng(position.coords.latitude, position.coords.longitude);
          mapDrawThrows.setCenter(initialLocation);
     });
 }
  j("#clean-drawing-map").hide();
  j("#backward-drawing-map").hide();

  // Create the search box and link it to the UI element.
  var input = document.getElementById('pac-input');
  var searchBox = new google.maps.places.SearchBox(input);
  mapDrawThrows.controls[google.maps.ControlPosition.TOP_LEFT].push(input);

  // Bias the SearchBox results towards current mapDrawThrows's viewport.
  mapDrawThrows.addListener('bounds_changed', function() {
    searchBox.setBounds(mapDrawThrows.getBounds());
  });


  var markers = [];
  // Listen for the event fired when the user selects a prediction and retrieve
  // more details for that place.
  searchBox.addListener('places_changed', function() {
    var places = searchBox.getPlaces();

    if (places.length == 0) {
      return;
    }

    // Clear out the old markers.
    markers.forEach(function(marker) {
      marker.setMap(null);
    });
    markers = [];

    // For each place, get the icon, name and location.
    var bounds = new google.maps.LatLngBounds();
    places.forEach(function(place) {
      if (!place.geometry) {
        console.log("Returned place contains no geometry");
        return;
      }
      var icon = {
        url: place.icon,
        size: new google.maps.Size(71, 71),
        origin: new google.maps.Point(0, 0),
        anchor: new google.maps.Point(17, 34),
        scaledSize: new google.maps.Size(25, 25)
      };

      // Create a marker for each place.
      markers.push(new google.maps.Marker({
        map: mapDrawThrows,
        icon: icon,
        title: place.name,
        position: place.geometry.location
      }));

      if (place.geometry.viewport) {
        // Only geocodes have viewport.
        bounds.union(place.geometry.viewport);
      } else {
        bounds.extend(place.geometry.location);
      }
    });
    mapDrawThrows.fitBounds(bounds);
  });
}
</script>

	<script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyAL0t-HODDCPg5-tEGzQAmiI_JQODfMFLw&libraries=places&callback=initAutocomplete"></script>
	|;
	return $html;
}

sub authorize_point{
	my $id_punto = $in{'id_punto'};
	if($id_punto){
		$q = qq|
			update acarreos.dbo.acarreos_puntos
			set autorizado = 1
			WHERE id_punto=$id_punto;
		|;
		$xpl2_acarreos_instance_1->execute($q);

	}

	return " ".$gh->alertM("Punto autorizado exitosamente.","i").authorize_points();
}

sub reject_point{

	my $building = $in{'building'};
	my $authorized = $in{'authorized'};

	return " ".$gh->alertM("Punto retirado exitosamente.","w").authorize_points($building, $authorized);
}

sub conectadb {
	if (!$lib) {
		$lib = Libs->new();
	}
	if (!$xpl2_acarreos_instance_1) {
		$xpl2_acarreos_instance_1 = MSQL_VB->new();
		$xpl2_acarreos_instance_1->connectdb('acarreos','VISE-XPL2');
	}
	if (!$xpl2_acarreos_instance_2) {
		$xpl2_acarreos_instance_2 = MSQL_VB->new();
		$xpl2_acarreos_instance_2->connectdb('acarreos','VISE-XPL2');
	}
}
