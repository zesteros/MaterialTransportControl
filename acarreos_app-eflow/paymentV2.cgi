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
print PrintHeader();
use CGI::Carp qw(fatalsToBrowser);
use Eflow::Utils;

$|=1;
$post = new CGI;
%ine = $post->Vars;
$lib = Libs->new();
$lib->hash_decode(\%ine);
$qt = new CGI;
%in = $qt->Vars;
conectadb();
$lib->hash_decode(\%in);

$user = user_check(1);##ID DEL EMPLEADO LOGEADO
$home ="/cgi-bin/acarreos_app";
$pDir = "$home/paymentV2.cgi";

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
		"/lib/datetimepicker-0.0.11/js/bootstrap-datetimepicker.min.js",
		"/lib/daterangepicker/moment.min.js",
		"/lib/daterangepicker/daterangepicker.min.js",
		"/lib/bootstrap-tagsinput-2.3.2/bootstrap-tagsinput.js"
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
	"/lib/datetimepicker-0.0.11/css/bootstrap-datetimepicker.min.css",
	"/lib/daterangepicker/daterangepicker.css",
	"/lib/bootstrap-tagsinput-2.3.2/bootstrap-tagsinput.css"

	);

if(!$in{'noMenu'}){
	@links=(
		{ 'link'  => "$pDir?cmd=process_payment", 'titleLink' => "Pago V2", 'class' => 'process_payment','m'=>"$m_process_payment",'p'=>"$pro",'u',"$user"}

	);

}

@log=();
$title = "Acarreos VISE - Proceso de pago V2";
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


sub conectadb {
	if (!$lib) {$lib = Libs->new();}
	if (!$xpl2_acarreos_instance_1) { $xpl2_acarreos_instance_1 = MSQL_VB->new(); $xpl2_acarreos_instance_1->connectdb('acarreos','VISE-XPL2');}
	if (!$xpl2_acarreos_instance_2) { $xpl2_acarreos_instance_2 = MSQL_VB->new(); $xpl2_acarreos_instance_2->connectdb('acarreos','VISE-XPL2'); }
}

sub inicio{ $html= qq|- |; return $html; }

sub process_payment{


	my $building = $ine{'buildings_select'};
	my $date = $ine{'daterange'};
	my $tickets_to_pay = $ine{'scan_tickets'};

	my $html = qq|<br>
		<script type="text/javascript" src="/js/acarreos/payment.js?v=$sec$min$hour$mday$mon"></script>
		<script src="https://cdn.datatables.net/1.10.19/js/jquery.dataTables.min.js"></script>
		<script src="https://cdn.datatables.net/buttons/1.5.2/js/dataTables.buttons.min.js"></script>
		<script src="https://cdn.datatables.net/buttons/1.5.2/js/buttons.flash.min.js"></script>
		<script src="https://cdnjs.cloudflare.com/ajax/libs/jszip/3.1.3/jszip.min.js"></script>
		<script src="https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.1.36/pdfmake.min.js"></script>
		<script src="https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.1.36/vfs_fonts.js"></script>
		<script src="https://cdn.datatables.net/buttons/1.5.2/js/buttons.html5.min.js"></script>
		<script src="https://cdn.datatables.net/buttons/1.5.2/js/buttons.print.min.js"></script>
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
								<select class="form-control id="tipo_pago" class="col-sm-4">
									<option> Acarreo </option>
									<option> Regalía - Renta camión </option>
									<option> Regalía - Compra de material </option>
								</select>
							</div>
						</div>
						<div class="form-group row">
							<label for="scan_tickets" class="col-sm-2 col-form-label">Boletos a pagar</label>
							<div class="col-sm-10">
							    <input type="text" value="$tickets_to_pay" style="font-size:15px;" name="scan_tickets" id="scan_tickets" placeholder="Escanea boletos">
							</div>
						</div>
					</center>
				</div>
		      	<div class="panel-footer clearfix">
					<!---<div class="btn-group pull-right">
						<button type="button" class="btn btn-success" onclick="savelistproveedor()"><span class="glyphicon glyphicon-floppy-disk"></span> Guardar</button>
					</div>-->
				</div>
			</div>
		</div>



		<center>
			<div style="width:60%">
				<div class="form-group row">
						    <label for="scan_tickets" class="col-sm-2 col-form-label">Boletos a pagar</label>
						    <div class="col-sm-10">
						    	<!--<textarea type="text" class="form-control" type="text" name="scan_tickets" id="scan_tickets" placeholder="Escanea boletos" cols="50"></textarea>-->
						    	<input type="text" value="$tickets_to_pay" style="font-size:15px;" name="scan_tickets" id="scan_tickets" placeholder="Escanea boletos">
						    </div>
				</div>
				<input type="hidden" name="carries" id="carries" value="$in{'carries'}">
				$filters_form
				<div class="form-group row">
					<div class="col-sm-10"> </div>
					<button type="submit" class="col-sm-2 btn btn-primary">Buscar</button>
				</div>
			</div>
		</center>|;



	if($date){

		my @dates = format_date($date,' - ');

		my $start_date = $dates[0];

		my $end_date = $dates[1];


		$date = get_date_for_sql($start_date, $end_date);
	}

		
	$html.=search_tickets($date,$building, $tickets_to_pay);
	

	return $html;
}
sub search_tickets{

	my $date = shift;
	my $building = shift; 
	my $sheet_numbers = shift;

	my $content = get_carries_data_by_filter($date, $building);

	#my $content_tickets_to_pay =  get_carries_data_by_filter($date, $building, $sheet_numbers);

	my $html = qq|
		<style type="text/css">
		* {margin: 0; padding: 0;}
		#container {height: 100%; width:100%; font-size: 0;}
		#left, #middle, #right {display: inline-block; *display: inline; zoom: 1; vertical-align: top; font-size: 12px;}
		#left {width: 50%; padding: 13px;}
		#right {width: 50%; padding: 13px;}
		</style>
		<div id="container">
		    <div id="left">
		    <table  id="tickets-table" class="cell-border" style="font-size:10px;">
		        <thead>
		            <tr>
		            	<th></th>
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
						<th>Recibió</th>
						<th>Tiempo</th>
						<th>Distancia</th>
						<th>Importe acarreo</td>
						<th>Importe material</td>
						<th>
							Subtotal
						</th>
					</tr>
		        </thead>
		        <tbody id="content">
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
					</tr>
		        </tfoot>
   			</table>
   			</div>
		    <div id="right">
		      <table  id="tickets-table-to-pay" class="cell-border" style="font-size:10px;">
		        <thead>
		            <tr>
		            	<th></th>
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
						<th>Recibió</th>
						<th>Tiempo</th>
						<th>Distancia</th>
						<th>Importe acarreo</td>
						<th>Importe material</td>
						<th>
							Subtotal
						</th>
					</tr>
		        </thead>
		        <tbody id="content">
		        	$content_tickets_to_pay
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
					</tr>
		        </tfoot>
   				</table>

		    </div>
		</div>
		

	|;

	return qq|<table  id="tickets-table-to-pay" class="cell-border" style="font-size:10px;">
		        <thead>
		            <tr>
		            	<th></th>
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
						<th>Recibió</th>
						<th>Tiempo</th>
						<th>Distancia</th>
						<th>Importe acarreo</td>
						<th>Importe material</td>
						<th>
							Subtotal
						</th>
					</tr>
		        </thead>
		        <tbody id="content">
		        	$content_tickets_to_pay
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
					</tr>
		        </tfoot>
   				</table>|;
}

sub get_carries_data_by_filter{


	my $date = shift;
	my $building = shift; 
	my $sheet_numbers = shift;

	$building = $sheet_numbers ? "" : " and building_origin = '$building'";

	$date = $sheet_numbers ? "" : $date;

	if($sheet_numbers){

		$building = "";
		$date ="";

		my @sheet_numbers_array =  split /,/, $sheet_numbers;

		$sheet_numbers = qq| and folio in(|;

		for my $i (0 .. $sheet_numbers_array) {
			$sheet_numbers.=qq|'$sheet_numbers_array[i]',|;
		}

		chop($sheet_numbers);

		$sheet_numbers.=")";
	}

	my $q = qq|
		select * from carries
		where 1 = 1
		$building
		$date
		$sheet_numbers
		order by exit_date desc;
	|;

	$xpl2_acarreos_instance_1 -> execute($q);

	while ($d = $xpl2_acarreos_instance_1->getrow_hashref) {


		$$d{'m3'} = $lib->fnumeric($$d{'m3'});
		$$d{'distance'} = $lib->fnumeric($$d{'distance'});

		my $subtotal = $lib->fnumeric($$d{'importe_acarreo'}+$$d{'importe_material'});

		$$d{'importe_acarreo'} = $lib->fnumeric($$d{'importe_acarreo'});
		$$d{'importe_material'} = $lib->fnumeric($$d{'importe_material'});




		$content.=qq¡
			<tr>
				<td class="details-control"></td>
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

			</tr>
		¡;


	}

	return $content;
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

