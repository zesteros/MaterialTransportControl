#!/usr/bin/perl -w

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
print PrintHeader();
use CGI::Carp qw(fatalsToBrowser);
use Eflow::Utils;
use Switch;

$post = new CGI;
%ine = $post->Vars;
$lib = Libs->new();
$lib->hash_decode(\%ine);


$|=1;
$qt = new CGI;
%in = $qt->Vars;
$lib->hash_decode(\%in);

$user = user_check(1);##ID DEL EMPLEADO LOGEADO
$home ="/cgi-bin/acarreos_app";
$pDir = "$home/supervision.cgi";

conectadb();

require 'menu.pl';
($sec,$min,$hour,$mday,$mon,$year,$wday,$yday,$isdst) = localtime(time);
$CMD=$in{'cmd'}; 
my $gh=Eflow::GH->new();
my @js =("/lib/validate.js","/lib/navigate.js"); 
my @js_post =(
		"/lib/jquery/jquery-ui-1.11.1/jquery-ui.min.js",
		"/js/classie.js",
		"/js/borderMenu.js?v=1.1",
		"/lib/js/jquery.dataTables.min.js",
		"/lib/js/datepicker.spanish.js",
		"/lib/js/multiselect/src/jquery.multiselect.js",
		"/lib/js/multiselect/src/jquery.multiselect.filter.js",
		"/js/SUtil/ajaxUtil.js?val=0.0.1",
		"/js/SUtil/utilNumbers.js?val=0.0.1",
		"/lib/js/select2.js",
		"/lib/js/blockui.js",
		"/lib/js/jquery.dataTables.yadcf.js",
		"/js/acarreos/config.js?v=$sec$min$hour$mday$mon",
		"/js/SUtil/utilErrors.js",
		"/lib/highcharts/chart-helper.js?v=$sec$min$hour$mday$mon",
		"/js/acarreos/supervision.js?v=$sec$min$hour$mday$mon",
		"/js/acarreos/advance.js?v=$sec$min$hour$mday$mon",
		"/lib/daterangepicker/moment.min.js",
		"/lib/daterangepicker/daterangepicker.min.js",
		"/lib/sweetalert/sweetalert.min.js"
	);
my @css=(
	"/css/style5.css?v=1.0.1",
	"/css/icons.css",
	"/lib/css/jquery.dataTables.min.css",
	"/lib/jquery/jquery-ui-1.11.1/jquery-ui.min.css",
	"/lib/js/multiselect/jquery.multiselect.css",
	"/lib/js/jquery.dataTables.yadcf.css",
	"/lib/js/multiselect/jquery.multiselect.filter.css",
	"/lib/js/select2.css",
	"/lib/daterangepicker/daterangepicker.css",
	"/lib/sweetalert/sweetalert.css"
	);
#our $m_tickets_carries = 409;
#our $m_tickets_gral = 410;
#our $m_locations = 411;
#our $m_reprints = 412;
if(!$in{'noMenu'}){
	@links=(
		{ 
			'link'  => "$pDir?cmd=carries_tickets&carries=true",
			'titleLink' => "Acarreos",
			'class' => 'carries_tickets',
			'm'=>"$m_tickets_carries",
			'p'=>"$pro",
			'u',
			"$user"
		},
		{ 
			'link'  => "$pDir?cmd=advance",
			'titleLink' => "Avance de obra",
			'class' => 'advance',
			'm'=>"$m_advance",
			'p'=>"$pro",
			'u',
			"$user"
		},
		{ 
			'link'  => "$pDir?cmd=carries_tickets&carries=false",
			 'titleLink' => "Boletos",
			  'class' => 'carries_tickets_filters',
			  'm'=>"$m_tickets_gral",
			  'p'=>"$pro",
			  'u',
			  "$user"
		},
		{ 
			'link'  => "$pDir?cmd=locations$gml",
			'titleLink' => "Ubicaciones",
			'class' => 'locations',
			'm'=>"$m_locations",
			'p'=>"$pro",
			'u',
			"$user"
		},
		{ 
			'link'  => "$pDir?cmd=reprints$gml",
			'titleLink' => "Reimpresiones", 
			'class' => 'reprints',
			'm'=>"$m_reprints",
			'p'=>"$pro",
			'u',
			"$user"
		},


	);
}

@log=();
$title = "Acarreos VISE - Supervision";
my $body = "";
my $foot = "";
my $ocem=0;
my $ac=Eflow::acceso->new();
eval {
	$CMD = $in{'cmd'};
    if (!$CMD) {
		$body = inicio();
		print "<input type=\"hidden\" value=\"inicio\" class=\"inicio\">";
	} elsif (exists &$CMD) {
		$body = &$CMD;
		print "<input type=\"hidden\" value=\"$CMD\" class=\"inicio\">";
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

sub advance{

	my $building = $ine{'buildings_select'};
	my $date = $ine{'daterange'};

	my $html = qq|
		<input type="hidden" name="user_session" id="user_session" value="$user">
			<script src="https://code.highcharts.com/highcharts.js"></script>
			<script src="https://code.highcharts.com/modules/exporting.js"></script>
			<script src="https://code.highcharts.com/modules/offline-exporting.js"></script>
			<script src="https://code.highcharts.com/modules/export-data.js"></script>
		  <style>
	      /* Always set the map height explicitly to define the size of the div
	       * element that contains the map. */
	      #advance_map {
	        min-width: 310px;
	         height: 600px;
	         width:800px;
	         max-width: 800px;
	         margin: 0 auto;
	      }
	      #floating-panel {
	        position: absolute;
	        top: 10px;
	        left: 25%;
	        z-index: 5;
	        background-color: #fff;
	        padding: 5px;
	        border: 1px solid #999;
	        text-align: center;
	        font-family: 'Roboto','sans-serif';
	        line-height: 30px;
	        padding-left: 10px;
	      }
	    </style>
	    <div id="main_div_advance">
			<div class="panel panel-success" id="advance_map_panel"> 
				<div class="panel-heading"> 
					<h3 class="panel-title">Mapa de avance</h3> 
				</div> 
				<div class="panel-body"> 
			<center>
					<center>
					 	<div style="width:60%">
							<input type="hidden" name="selected_building" value="$building" id="selected_building">
							<input type="hidden" name="cmd" value="advance"/>
							<div class="form-group row">
								<label for="buildings_select" class="col-sm-2 col-form-label">Obra</label>
							    <div class="col-sm-10">
									<select class="form-control" width="80%" id="buildings_select" name="buildings_select"></select>
								</div>
							</div>
							<div class="form-group row">
							    <label for="daterange" class="col-sm-2 col-form-label">Rango de fechas</label>
							    <div class="col-sm-10">
							    	<input type="text" class="form-control" type="text" name="daterange" id="daterange" value="$date" placeholder="Ingresa fecha"/>
							    </div>
							</div>
							<div class="form-group row">
								<div class="col-sm-10">
							    </div>
							    <button onclick="searchAdvance()" class="col-sm-2 btn btn-primary">Buscar</button>
							</div>
							
							<div class="form-group row">
								<div class="col-sm-12">
									<div id="advance_map"></div>
								</div>
							</div>
						</div>
					</center>
			</center>
			</div>
			</div>

			<script>
				var advanceMap;
				var advanceMapMarkers = [];

				function initMap() {

					advanceMap = new google.maps.Map(document.getElementById('advance_map'), {
					  zoom: 14,
					  center: {lat:21.104736028181254, lng: -101.62394993853323}
					});
				}
			</script>
			<script async defer src="https://maps.googleapis.com/maps/api/js?key=AIzaSyAL0t-HODDCPg5-tEGzQAmiI_JQODfMFLw&callback=initMap">
		    </script>
			<div class="panel panel-warning" id="supplied_material_panel"> 
				<div class="panel-heading"> 
					<h3 class="panel-title">Materiales suministrados</h3> 
				</div> 
				<div class="panel-body"> 
				    <table id="advance_table">
				    	<thead>
				    		<tr>
				    			<th>Material</th>
				    			<th>M3</th>
				    			<th>M3 Explosión</th>
				    			<th>Porcentaje completado</th>
				    			<th>Viajes</th>
				    			<th>Costo material</th>
				    			<th>Costo acarreo</th>
				    			<th>Costo total</th>
				    		</tr>
				    	</thead>
				    	<tbody>
				    	
				    	</tbody>
				    	<tfoot>
				    		<tr>
				    			<th>Material</th>
				    			<th>M3</th>
				    			<th>M3 Explosión</th>
				    			<th>Porcentaje completado</th>
				    			<th>Viajes</th>
				    			<th>Costo material</th>
				    			<th>Costo acarreo</th>
				    			<th>Costo total</th>
				    		</tr>
				    	</tfoot>
				    </table>
		    	</div>
		    </div>
		    <div class="panel panel-info" id="advance_plot_panel"> 
				<div class="panel-heading"> 
					<h3 class="panel-title">Gráficas</h3> 
				</div> 
				<div class="panel-body"> 
					<div id="advance_by_date_plot" style="min-width: 310px; height: 600px; max-width: 800px; margin: 0 auto"></div>
					<div id="trips_by_date_plot" style="min-width: 310px; height: 600px; max-width: 800px; margin: 0 auto"></div>
					<div id="trips_by_provider" style="min-width: 310px; height: 600px; max-width: 800px; margin: 0 auto"></div>
					<div id="money_by_provider" style="min-width: 310px; height: 600px; max-width: 800px; margin: 0 auto"></div>
				</div>
			</div>
		</div>


	|;
	return $html;
}

sub carries_tickets {
	my $provider = $ine{'provider_select'};
	my $building = $ine{'buildings_select'};
	my $date = $ine{'daterange'};
	my $unique_date = $ine{'unique_date'};
	my $sheet_number = $ine{'sheet_number'};
	my $id_point_origin = $ine{'id_point_origin'};
	my $origin = $ine{'origin'};
	my $rear_plate = $ine{'rear_plate'};
	my $material = $ine{'material'};
	my $id_material = $ine{'id_material'};
	my $id_user = $ine{'id_user'};
	my $user_bank = $ine{'user'};
	my $ticket_type = $ine{'ticket_type'};

	my $filters_form = 
		$in{'carries'} eq 'false' ?
		get_ticket_filters(
			$sheet_number,
			$origin,
			$id_point_origin,
			$rear_plate,
			$material,
			$id_material,
			$user_bank,
			$id_user,
			$ticket_type
		) :
		"";

if($user eq 871){
	#print $provider;
}

	my $html = qq|
		<br>
		<input type="hidden" name="user_session" id="user_session" value="$user">

		<script type="text/javascript" src="/js/acarreos/supervision.js?v=$sec$min$hour$mday$mon"></script>
		<script src="https://cdn.datatables.net/1.10.19/js/jquery.dataTables.min.js"></script>
		<script src="https://cdn.datatables.net/buttons/1.5.2/js/dataTables.buttons.min.js"></script>
		<script src="https://cdn.datatables.net/buttons/1.5.2/js/buttons.flash.min.js"></script>
		<script src="https://cdnjs.cloudflare.com/ajax/libs/jszip/3.1.3/jszip.min.js"></script>
		<script src="https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.1.36/pdfmake.min.js"></script>
		<script src="https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.1.36/vfs_fonts.js"></script>
		<script src="https://cdn.datatables.net/buttons/1.5.2/js/buttons.html5.min.js"></script>
		<script src="https://cdn.datatables.net/buttons/1.5.2/js/buttons.print.min.js"></script>
		<center>
			<form autocomplete="off" action="supervision.cgi">
			    <input autocomplete="false" name="hidden" type="text" style="display:none;">

				<center>
				 	<div style="width:60%">
						<input type="hidden" name="selected_building" value="$building" id="selected_building">
						<input type="hidden" name="cmd" value="carries_tickets"/>
						<div class="form-group row">
							<label for="buildings_select" class="col-sm-2 col-form-label">Obra</label>
						    <div class="col-sm-10">
								<select class="form-control" width="80%" id="buildings_select" name="buildings_select"></select>
							</div>
						</div>
						<div class="form-group row">
						    <label for="daterange" class="col-sm-2 col-form-label">Rango de fechas</label>
						    <div class="col-sm-10">
						    	<input type="text" autocomplete="false" class="form-control" type="text" name="daterange" id="daterange" value="$date" placeholder="Ingresa rango de fechas"/>
						    </div>
						</div>
						<div class="form-group row">
						    <label for="unique_date" class="col-sm-2 col-form-label">Fecha única</label>
						    <div class="col-sm-10">
						    	<input type="text" autocomplete="false" class="form-control" type="text" name="unique_date" value="$unique_date" id="unique_date" placeholder="Ingresa fecha única"/>
						    </div>
						</div>
						<div class="form-group row">
							<label for="provider_select" class="col-sm-2 col-form-label">Proveedor acarreos</label>
						    <div class="col-sm-10">
								<select class="form-control" width="80%" id="provider_select" name="provider_select"></select>
							</div>
						</div>
						<input type="hidden" name="carries" id="carries" value="$in{'carries'}">
						<input type="hidden" name="selected_provider" id="selected_provider" value="$provider">
						$filters_form
						<div class="form-group row">
							<div class="col-sm-10">
						    </div>
						    <button type="submit" class="col-sm-2 btn btn-primary">Buscar</button>
						</div>
					</div>
				</center>
			</form>
		</center>

	|;
	if($building){

		if($date){

			my @dates = format_date($date,' - ');

			my $start_date = $dates[0];

			my $end_date = $dates[1];


			$date = get_date_for_sql($start_date, $end_date);
		}
		my $is_carry = $in{'carries'} eq 'true';

		$html .= get_tickets_by_parameters(
			$provider,
			$unique_date,
			$date,
			$building,
			$sheet_number,
			$id_point_origin, 
			$rear_plate,
			$id_material, 
			$id_user,
			$ticket_type,
			$is_carry
		);
		

	}
	return $html;
}

sub get_ticket_filters{
	my $sheet_number = shift;
	my $origin = shift;
	my $id_point_origin = shift;
	my $plates = shift;
	my $material = shift;
	my $id_material = shift;
	my $user_bank = shift;
	my $id_user = shift;
	my $ticket_type = shift;

	my $ticket_type_options = get_ticket_types($ticket_type);

	return qq|
		<div class="form-group row">

			<label for="ticket_type" class="col-sm-2 col-form-label">Tipo de boleto</label>
			<div class="col-sm-10">
				<select class="form-control" width="80%" id="ticket_type" name="ticket_type">
					$ticket_type_options
				</select>
			</div>
			
		</div>
		<div class="form-group row">
			<label for="sheet_number" class="col-sm-2 col-form-label">Folio</label>
			<div class="col-sm-10">
				<input class="form-control" type="text" name="sheet_number" id="sheet_number" placeholder="Ingresa un folio" value="$sheet_number">
			</div>
		</div>
		<div class="form-group row">
			<label for="origin" class="col-sm-2 col-form-label">Origen</label>
			<div class="col-sm-10">
				<input type="hidden" id="id_point_origin" value="$id_point_origin">
				<input class="form-control" type="text" name="origin"  id="origin" placeholder="Ingresa un origen" value="$origin">
			</div>
		</div>
		<div class="form-group row">
			<label for="rear_plate" class="col-sm-2 col-form-label">Camión</label>
			<div class="col-sm-10">
				<input class="form-control" value="$plates" type="text" name="rear_plate" id="rear_plate" placeholder="Ingresa una placa trasera" >
			</div>
		</div>
		<div class="form-group row">
			<label for="material" class="col-sm-2 col-form-label">Material</label>
			<div class="col-sm-10">
				<input type="hidden" name="id_material" id="id_material" value="$id_material">
				<input class="form-control" type="text" name="material" id="material" placeholder="Ingresa un material" value="$material">
			</div>
		</div>
		<div class="form-group row">
			<label for="user" class="col-sm-2 col-form-label">Usuario</label>
			<div class="col-sm-10">
				<input type="hidden" id="id_user" name="id_user" value="$id_user">
				<input class="form-control" type="text" name="user" id="user" placeholder="Ingresa nombre de usuario" value="$user_bank">
			</div>
		</div>
	|;

}

sub get_ticket_types{

	my $ticket_selected = shift;

	my $ticket_types = "";

	my $q = qq|
		SELECT id_type, name, add_date, add_user, upd_date, upd_user, estatus
		FROM acarreos.dbo.acarreos_boletos_tipo_boleto;

	|;

	$xpl2_acarreos_instance_1->execute($q);

	while ($d = $xpl2_acarreos_instance_1->getrow_hashref) {
		$ticket_types .= 
			$ticket_selected != $$d{'id_type'} ? 
			qq|<option value="$$d{'id_type'}"> $$d{'name'}</option>| :
			qq|<option value="$$d{'id_type'}" selected> $$d{'name'}</option>| 
			;
	}

	return $ticket_types; 
}

sub get_date_for_sql{
	my $start_date_to_format = shift;
	my $end_date_to_format = shift;
	my @start_date_splitted = format_date($start_date_to_format,'/');
	my @end_date_splitted = format_date($end_date_to_format,'/');
	return qq|
		'$start_date_splitted[1]-$start_date_splitted[0]-$start_date_splitted[2]'
			and
		'$end_date_splitted[1]-$end_date_splitted[0]-$end_date_splitted[2]'
	 |;

}
sub format_date{
	my $date = shift;
	my $split_symbol = shift;
	my @data = split /$split_symbol/, $date;
	return @data;
}

sub get_carry_data{
	my $provider = shift;

	$provider = $provider eq -1 ? "" : "and id_proveedor = $provider";

	my $unique_date = shift;
	my $date = shift;
	my $building = shift; 
	$date = $unique_date ? "and fecha_salida='$unique_date'" : $date;
	my $q = qq|
		select * from carries
		where building_origin = '$building'
		$date
		$provider
		order by exit_date desc;
	|;
	if($user eq 871){
		#print $provider;
		#print $q;
	}
	$xpl2_acarreos_instance_1 -> execute($q);
	while ($d = $xpl2_acarreos_instance_1->getrow_hashref) {


		$$d{'distance'} = $lib->fnumeric($$d{'distance'});

		my $subtotal = $$d{'cancelado_en_app'} ? $lib->fnumeric(0): $lib->fnumeric($$d{'importe_acarreo'}+$$d{'importe_material'});

		$$d{'importe_acarreo'} =  $$d{'cancelado_en_app'} ? $lib->fnumeric(0): $lib->fnumeric($$d{'importe_acarreo'});
		$$d{'importe_material'} =  $$d{'cancelado_en_app'} ? $lib->fnumeric(0): $lib->fnumeric($$d{'importe_material'});
		$$d{'m3'} = $$d{'cancelado_en_app'} ? $lib->fnumeric(0): $lib->fnumeric($$d{'m3'});

		$$d{'recibio'} = $$d{'recibio'} ? $$d{'recibio'} : $$d{'recibio_movil'};
		$$d{'elaboro'} = $$d{'elaboro'} ? $$d{'elaboro'} : $$d{'elaboro_movil'};

		if($$d{'actualizado_por'}){

		} else {
			if($$d{'actualizado_por_movil'}){
				$$d{'actualizado_por'} = $$d{'actualizado_por_movil'};
			} else {
				$$d{'actualizado_por'} = "N/A";
			}
		}

		if(!$$d{'cancelado_en_app'}){
			$$d{'actualizado_por'} = "N/A";
		}

		if($$d{'precio_unico_material'} or  $$d{'precio_unico_material'} eq 0){
			$$d{'importe_material'} = $lib->fnumeric($$d{'precio_unico_material'});
		}
	



		$content.=qq¡
			<tr>
				<td class="details-control"></td>
				<td>
					$$d{'tipo_boleto_nombre'}
				</td>
				<td>
					$$d{'fecha_salida'} $$d{'hora_salida'}
				</td>
				<td>
					$$d{'folio'}
				</td>
				<td>
					<a href="/cgi-bin/cubicacion/cubicacion.cgi?cmd=viewCi&placaT=$$d{'plates'}">$$d{'plates'}</a> 
				</td>
				<td>
					$$d{'origen'}
				</td>
				<td>
					$$d{'destino'}
				</td>
				<td>
					$$d{'material'}
				</td>
				<td>
					$$d{'m3'} M3
				</td>
				<td>
					$$d{'elaboro'} 
				</td>
				<td>
					$$d{'fecha_entrega'} $$d{'hora_entrega'} 
				</td>
				<td>
					$$d{'recibio'}
				</td>
				<td>
					$$d{'tiempo'}
				</td>
				<td>
					$$d{'distance'} KM
				</td>
				<td>
					\$$$d{'importe_acarreo'}
				</td>
				<td>
					\$$$d{'importe_material'}
				</td>
				<td>
					\$$subtotal
				</td>

				<td>
					$$d{'actualizado_por'}
				</td>
				<td>$$d{'proveedor'}</td>
				<td>$$d{'id_proveedor_navision'}</td>

			</tr>
		¡;
	}
	return qq|  
		<input type="hidden" name="user_session" id="user_session" value="$user">


		  <style>
	      /* Always set the map height explicitly to define the size of the div
	       * element that contains the map. */
	      #map {
	        height: 20%;
	      }
	      #floating-panel {
	        position: absolute;
	        top: 10px;
	        left: 25%;
	        z-index: 5;
	        background-color: #fff;
	        padding: 5px;
	        border: 1px solid #999;
	        text-align: center;
	        font-family: 'Roboto','sans-serif';
	        line-height: 30px;
	        padding-left: 10px;
	      }
	    </style>

		<div class="modal fade" id="show_route_map_modal" role="dialog">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-hidden="true">X</button>
						<h3 class="modal-title">Ver ruta de acarreo</h3>
					</div>
					<div class="modal-body">
							<h3>Distancia calculada: <b id="distance"></b></h3>
							<h3>Tiempo de traslado calculado: <b id="time"></b></h3>
						    <div id="map"></div>

					</div>
					<div class="modal-footer">
						<input type="hidden" name="selected_point" id="selected_point" value="">
						<button type="button" class="btn btn-default " data-dismiss="modal">Aceptar</button>
						<!--<button type="button" data-dismiss="modal" class="btn btn-primary">Cancelar</button>-->
					</div>
				</div>
			</div>
		</div>

		<script>
		var map;
		var directionsService;
		var directionsRenderer;

		function initMap() {

			map = new google.maps.Map(document.getElementById('map'), {
			  zoom: 7,
			  center: {lat: 41.85, lng: -87.65}
			});

	        directionsService = new google.maps.DirectionsService();
	        directionsRenderer = new google.maps.DirectionsRenderer();

		}
	    </script>
	    <script async defer
	    src="https://maps.googleapis.com/maps/api/js?key=AIzaSyAL0t-HODDCPg5-tEGzQAmiI_JQODfMFLw&callback=initMap">
	    </script>

		<div class="panel panel-success"> 
			<div class="panel-heading"> 
				<h3 class="panel-title">Simbología</h3> 
			</div> 
			<div class="panel-body"> 
				<div class="col-md-12">
					<div class="col-md-12">
			            <div class="col-lg-3 form-group" style="text-aling:center">
			            	<div style="background:#9FD59C;width: 30px;height: 30px;" class="col-lg-12 img-circle"></div>
							<label for="" class="col-md-12 control-label">Viaje terminado</label>
			            </div>	

			            <div class="col-lg-3 form-group" style="text-aling:center">
			            	<div style="background:#E9CBA3;width: 30px;height: 30px;" class="col-lg-12 img-circle"></div>
							<label for="" class="col-md-12 control-label">Viaje sin terminar</label>
			            </div>	

			            <div class="col-lg-3 form-group" style="text-aling:center">
			            	<div style="background:#D49A94;width: 30px;height: 30px;" class="col-lg-12 img-circle"></div>
							<label for="" class="col-md-12 control-label">Folio cancelado por checador</label>
			            </div>	
			        </div>	
				</div>
			</div> 
		</div>
		<div class="panel panel-info"> 
			<div class="panel-heading"> 
				<h3 class="panel-title">Folios generados</h3> 
			</div> 
			<div class="panel-body"> 
				<table  id="tickets-table" class="cell-border" style="font-size:10px;">
			        <thead>
			            <tr>
			            	<th></th>
			            	<th>Tipo boleto</th>
			            	<th>
			            		Fecha salida
			            	</th>
			            	<th>
			            		Folio
			            	</th>
							<th>
								Placa trasera
							</th>
							<th>
								Origen
							</th>
							<th>
								Destino
							</th>
							<th>
								Material
							</th>
							<th>
								M3
							</th>
							<th>
								Elaboró
							</th>
							<th>
								Fecha entrega
							</th>
							<th>
								Recibió
							</th>
							<th>
								Tiempo
							</th>
							<th>
								Distancia
							</th>
							<th>
								Importe acarreo
							</th>
							<th>
								Importe material
							</th>
							<th>
								Subtotal
							</th>
							<th>
								Cancelado por
							</th>
							<th>Proveedor</th>
							<th>ID Navision Proveedor</th>
						</tr>
			        </thead>
			        <tbody>
			            $content
			        </tbody>
			        <tfoot>
		  				<tr>
			            	<th></th>
			            	<th>
			            		
			            	</th>
			            	<th>
			            		
			            	</th>
							<th>
								
							</th>
							<th>
								
							</th>
							<th>
								
							</th>
							<th>
								
							</th>
							<th>
								
							</th>
							<th>
								
							</th>
							<th>
								
							</th>
							<th></th>
							<th></th>
							<th></th>
							<th> </th>
							<th> </th>
							<th>
								
							</th>
							<th></th>
							<th></th>
							<th></th>
						</tr>
			        </tfoot>
		   		</table>
   			</div> 
		</div>|;
}

sub get_general_ticket_data{

	my $date = shift;
	my $building = shift; 
	my $sheet_number = shift;
	my $origin = shift;
	my $plates = shift;
	my $material = shift;
	my $user_bank = shift;
	my $ticket_type = shift;

	my $q = qq|
		select 
			sheet_number, 
			rear_licence_plate,
			increase,
			capacity,
			id_material,
			material_description,
			discount,
			convert(varchar(100),exit_date,103) as exit_date,
			convert(varchar(100),exit_date,108) as exit_hour,
			nombre_banco_origin as point_name_origin, 
			cadenamiento_origin as chainage_origin,
			exit_coordinates, 
			convert(varchar(100),expiration_date,103) as expiration_date,
			convert(varchar(100),expiration_date,108) as expiration_hour
		from tickets
		where
			estatus_tickets = 'A'
			and building = '$building'
			$date
			$sheet_number
			$plates
			$material
			$user_bank
			$ticket_type;
	|;

	$xpl2_acarreos_instance_1->execute($q);


	while ($d = $xpl2_acarreos_instance_1->getrow_hashref) {

		$$d{'increase'} = $lib->fnumeric($$d{'increase'});
		$$d{'capacity'} = $lib->fnumeric($$d{'capacity'});
		$$d{'discount'} = $lib->fnumeric($$d{'discount'});

		$content.=qq¡
			<tr>
				<td>$$d{'sheet_number'}</td>
				<td>$$d{'rear_licence_plate'}</td>
				<td>$$d{'increase'}</td>
				<td>$$d{'capacity'}</td>
				<td>$$d{'id_material'} - $$d{'material_description'}</td>
				<td>$$d{'discount'}</td>
				<td>$$d{'exit_date'} $$d{'exit_hour'}</td>
				<td>$$d{'point_name_origin'} - $$d{'chainage_origin'}</td>
				<td>$$d{'exit_coordinates'}</td>
				<td>$$d{'expiration_date'} $$d{'expiration_hour'}</td>
			</tr>
		,¡;
	}

	return qq|
			<table id="tickets-table">
				<thead>
					<tr>
						<th>
							FOLIO
						</th>
						<th>
							PLACA TRASERA
						</th>
						<th>
							MONTEN
						</th>
						<th>
							CAPACIDAD
						</th>
						<th>
							MATERIAL
						</th>
						<th>
							DESCUENTO
						</th>
						<th>
							FECHA DE SALIDA
						</th>
						<th>
							ORIGEN
						</th>
						<th>
							COORDENADAS DE SALIDA
						</th>
						<th>
							VIGENCIA
						</th>
					</tr>
				</thead>
				<tbody>
					$content
				</tbody>
				<tfoot>
				</tfoot>
			</table>
		|;
}

sub get_tickets_by_parameters{
	my $provider = shift;
	if($user eq 871){
		#print " get_tickets_by_parameters: provider $provider";
	}
	my $unique_date = shift;
	my $date = shift;
	$date = $date ? "and exit_date between $date" : "";
	my $building = shift; 
	my $sheet_number = shift;
	$sheet_number = $sheet_number ? "and sheet_number = '$sheet_number'": "";
	my $origin = shift;
	$origin = $origin ? "and id_point_origin = $origin":"";
	my $plates = shift;
	$plates = $plates ? "and rear_licence_plate = '$plates'":"";
	my $material = shift;
	$material = $material ? "and id_material=$material":"";
	my $user_bank = shift;
	$user_bank = $user_bank ? "and user_id_bank = $user_bank":"";
	my $ticket_type = shift;
	$ticket_type = $ticket_type ? "and ticket_type = $ticket_type":"";
	my $is_carry = shift;

	return  $is_carry ? 
			get_carry_data($provider, $unique_date, $date, $building) :
			get_general_ticket_data(
					$date,
					$building,
					$sheet_number,
					$origin,
					$plates,
					$material,
					$user_bank,
					$ticket_type
			);

}

sub locations{
	my $html = qq|
		<input type="hidden" name="user_session" id="user_session" value="$user">

	<style>
      /* Always set the map height explicitly to define the size of the div
       * element that contains the map. */
      #map-user-locations {
        height: 100%;
      }
      #floating-panel {
        position: absolute;
        top: 10px;
        left: 25%;
        z-index: 5;
        background-color: #fff;
        padding: 5px;
        border: 1px solid #999;
        text-align: center;
        font-family: 'Roboto','sans-serif';
        line-height: 30px;
        padding-left: 10px;
      }
      #floating-panel {
        background-color: #fff;
        border: 1px solid #999;
        left: 25%;
        padding: 5px;
        position: absolute;
        top: 10px;
        z-index: 5;
      }
    </style>
    <script>
    	function initUserMap() {

		  mapUserLocations = new google.maps.Map(document.getElementById('map-user-locations'), {
		    center: {lat: -34.397, lng: 150.644},
		    zoom: 14
		  });
		  if (navigator.geolocation) {
		       navigator.geolocation.getCurrentPosition(function (position) {
		           initialLocation = new google.maps.LatLng(position.coords.latitude, position.coords.longitude);
		           mapUserLocations.setCenter(initialLocation);
		       });
		   }
		}
    </script>
	<script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyAL0t-HODDCPg5-tEGzQAmiI_JQODfMFLw&libraries=visualization&callback=initUserMap&v=$sec$min$hour$mday$mon" async defer></script>
	<div class="panel panel-success">
		<div class="panel-heading">Ubicaciones</div>
		<div class="panel-body">
			<div class="form-group row">
				<label for="username" class="col-sm-1 col-form-label">Usuario</label>
				<div class="col-sm-8">
					<input type="hidden" id="id_user" name="id_user" value="$id_user">
					<input class="form-control" type="text" name="username" id="username" placeholder="Ingresa nombre de usuario" value="$user_bank">
				</div>
				<button class="col-sm-3 btn btn-primary" onclick="setHeatMap()">Buscar</button>

			</div>
			<div id="map-user-locations"></div>
		</div>
	</div>
	<div class="panel panel-info">
		<div class="panel-heading">Sincronizaciones</div>
		<div class="panel-body">
			<div class="form-group row">
				<table  id="sync-table">
	        <thead>
	            <tr>
	            	<th>
	            		Usuario
	            	</th>
	            	<th>
						Fecha de sincronización
					</th>

	            	<th>
	            		Ubicación
	            	</th>
	            	<th>
	            		Terminal
	            	</th>
	            	
					
				</tr>
	        </thead>
	        <tbody>
	        </tbody>
   		</table>
			</div>
		</div>
	</div>

	|;
	return $html;
}

sub reprints{
	my $date = $ine{'daterange'};
	my $building = $ine{'buildings_select'};


	my $html =qq|
			<input type="hidden" name="user_session" id="user_session" value="$user">

		<center>
			<form action="supervision.cgi">
				<center>
				 	<div style="width:60%">
						<input type="hidden" name="selected_building" value="$building" id="selected_building">
						<input type="hidden" name="cmd" value="reprints"/>
						<div class="form-group row">
							<label for="buildings_select" class="col-sm-2 col-form-label">Obra</label>
						    <div class="col-sm-10">
								<select class="form-control" width="80%" id="buildings_select" name="buildings_select"></select>
							</div>
						</div>
						<div class="form-group row">
						    <label for="daterange" class="col-sm-2 col-form-label">Fecha</label>
						    <div class="col-sm-10">
						    	<input type="text" class="form-control" type="text" name="daterange" value="$date" placeholder="Ingresa fecha" autocomplete="off"/>
						    </div>
						</div>
						<div class="form-group row">
							<div class="col-sm-10">
						    </div>
						    <button type="submit" class="col-sm-2 btn btn-primary">Buscar</button>
						</div>
					</div>
				</center>
			</form>
		</center>


	|;
	if($building){

		if($date){

			my @dates = format_date($date,' - ');

			my $start_date = $dates[0];

			my $end_date = $dates[1];


			$date = get_date_for_sql($start_date, $end_date);
		}
		$html .= get_reprints_by_parameters(
			$date,
			$building
		);
		

	}
	return $html;
}

sub get_reprints_by_parameters{
	my $date = shift;
	$date = $date ? "and reprints.add_date between $date" : "";
	my $building = shift; 

	my $q = qq|
		SELECT count(*) as impresiones,reprints.sheet_number,
			(select top 1 coordinates from acarreos.dbo.acarreos_boletos_reimpresiones where sheet_number = reprints.sheet_number) as coordinates, 
			(select top 1 convert(varchar(100),add_date,103) as fecha  from acarreos.dbo.acarreos_boletos_reimpresiones where sheet_number = reprints.sheet_number) as fecha,
			(select top 1 convert(varchar(100),add_date,108) as hora from acarreos.dbo.acarreos_boletos_reimpresiones where sheet_number = reprints.sheet_number) as hora,
			(select top 1 users.nameT FROM acarreos.dbo.acarreos_boletos_reimpresiones aux left join usuario.dbo.sn_eflow users 
				on aux.add_user = users.userid_eflow where aux.sheet_number = reprints.sheet_number) as nameT,
			(select top 1 users.super_nomina_id FROM acarreos.dbo.acarreos_boletos_reimpresiones aux left join usuario.dbo.sn_eflow users 
				on aux.add_user = users.userid_eflow where aux.sheet_number = reprints.sheet_number) as super_nomina_id,
			(select top 1 points.nombre_banco from acarreos.dbo.acarreos_boletos_reimpresiones aux 
				left join acarreos.dbo.acarreos_boletos tickets 
					on aux.sheet_number = tickets.sheet_number
				left join acarreos.dbo.acarreos_puntos points 
					on tickets.id_point_origin = points.id_punto
					where aux.sheet_number = reprints.sheet_number) as nombre_banco,
			(select top 1 points.cadenamiento from acarreos.dbo.acarreos_boletos_reimpresiones aux 
				left join acarreos.dbo.acarreos_boletos tickets 
					on aux.sheet_number = tickets.sheet_number
				left join acarreos.dbo.acarreos_puntos points 
					on tickets.id_point_origin = points.id_punto
					where aux.sheet_number = reprints.sheet_number) as cadenamiento
		FROM acarreos.dbo.acarreos_boletos_reimpresiones reprints
			left join acarreos.dbo.acarreos_boletos tickets_aux on tickets_aux.sheet_number = reprints.sheet_number 
		where exists(select sheet_number from acarreos.dbo.acarreos_boletos where building='$building') and tickets_aux.building = '$building'
		$date
		group by reprints.sheet_number;

	|;

	$xpl2_acarreos_instance_1->execute($q);

	while ($d = $xpl2_acarreos_instance_1->getrow_hashref) {


		my $q1 = qq|

		SELECT 
			count(*) as amount
		FROM acarreos.dbo.acarreos_boletos_reimpresiones
		where estatus='A' and sheet_number = $$d{'sheet_number'};
		|;


		$xpl2_acarreos_instance_2->execute($q1);

		my $prints = $xpl2_acarreos_instance_2->itemvalue('amount');

		$content.=qq¡
			<tr>
				<td class="details-control"></td>
				<td>
					<b>$prints</b>
				</td>
				<td>
					$$d{'fecha'} $$d{'hora'}
				</td>
				<td>
					$$d{'sheet_number'}
				</td>
				<td>
					$$d{'nameT'} - $$d{'super_nomina_id'}
				</td>
				<td>
					$$d{'nombre_banco'} $$d{'cadenamiento'}
				</td>
				<td>
					<a href="#" onclick="showInMap('$$d{'coordinates'}')">Mostrar en mapa</a>
				</td>
			</tr>
		¡;
	}
	return qq|  
		<table  id="reprints-table">
	        <thead>
	            <tr>
	            	<th></th>
	            	<th>
	            		Cantidad de reimpresiones
	            	</th>
	            	<th>
	            		Fecha primera reimpresión
	            	</th>
	            	<th>
	            		Folio
	            	</th>
					<th>
						Usuario
					</th>
					<th>
						Origen
					</th>
					<th>
						Coordenadas
					</th>
					
				</tr>
	        </thead>
	        <tbody>
	            $content
	        </tbody>
   		</table>|;


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


