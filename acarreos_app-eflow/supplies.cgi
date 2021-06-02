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
$pDir = "$home/supplies.cgi";

conectadb();

require 'menu.pl';
($sec,$min,$hour,$mday,$mon,$year,$wday,$yday,$isdst) = localtime(time);
$CMD=$in{'cmd'}; 
my $gh=Eflow::GH->new();
my @js =("/lib/validate.js","/lib/navigate.js"); 
my @js_post =(
		"/lib/jquery/jquery-ui-1.11.1/jquery-ui.min.js",
		"/js/classie.js","/js/borderMenu.js?v=1.1",
		"/lib/js/jquery.dataTables.min.js",
		"/lib/js/datepicker.spanish.js",
		"/lib/js/multiselect/src/jquery.multiselect.js",
		"/lib/js/multiselect/src/jquery.multiselect.filter.js",
		"/js/SUtil/ajaxUtil.js?val=0.0.1","/js/SUtil/utilNumbers.js?val=0.0.1",
		"/lib/js/select2.js","/lib/js/jquery.dataTables.yadcf.js",
		"/js/acarreos/config.js?v=$sec$min$hour$mday$mon",
		"/js/SUtil/utilErrors.js",
		"/js/acarreos/supplies.js?v=$sec$min$hour$mday$mon",
		"/lib/daterangepicker/moment.min.js",
		"/lib/daterangepicker/daterangepicker.min.js"
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
	"/lib/daterangepicker/daterangepicker.css"
	);

if(!$in{'noMenu'}){
	@links=(
		{
			'link'  => "$pDir?cmd=capture_ticket",
			'titleLink' => "Capturar boleto",
			'class' => 'capture_ticket',
			'm'=>"$m_tickets_main",
			'p'=>"$pro",
			'u',
			"$user"
		},
	);
}

@log=();
$title = "Acarreos VISE - Suministros de plantas";
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

sub capture_ticket{

	my $building = $ine{'buildings_select'};
	my $date =  $ine{'daterange'};
	my $sheet_number = $ine{'sheet_number'};

	my $html = qq|
		<br>
		<center>
			<form action="supplies.cgi">
				<center>
				 	<div style="width:60%">
						<input type="hidden" name="selected_building" value="$building" id="selected_building">
						<input type="hidden" name="cmd" value="capture_ticket"/>
						<div class="form-group row">
							<label for="buildings_select" class="col-sm-2 col-form-label">Obra</label>
						    <div class="col-sm-10">
								<select class="form-control" width="80%" id="buildings_select" name="buildings_select"></select>
							</div>
						</div>
						<div class="form-group row">
						    <label for="daterange" class="col-sm-2 col-form-label">Fecha</label>
						    <div class="col-sm-10">
						    	<input type="text" autocomplete="off" class="form-control" type="text" name="daterange" value="$date" placeholder="Ingresa fecha"/>
						    </div>
						</div>
						<div class="form-group row">
							<label for="sheet_number" class="col-sm-2 col-form-label">Folio</label>
							<div class="col-sm-10">
								<input class="form-control" type="text" name="sheet_number" id="sheet_number" placeholder="Ingresa un folio" value="$sheet_number">
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
		$html .= get_tickets_by_parameters(
			$date,
			$building,
			$sheet_number
		);

	}
	return $html;
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
sub get_tickets_by_parameters{
	my $date = shift;
	$date = $date ? qq| and exit_date between $date| : qq||;
	my $building = shift;
	my $sheet_number = shift;
	$sheet_number = $sheet_number ? qq| and sheet_number = '$sheet_number'|:qq||; 

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
		where ticket_type = 7
			and estatus_tickets = 'A'
			and building = '$building'
			$date
			$sheet_number;
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

	$html.=qq|
			<table id="material-supplies">
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
			</table>
		|;
}

sub conectadb {
	if (!$lib) {
		$lib = Libs->new();
	}
	if (!$xpl2_acarreos_instance_1) {
		$xpl2_acarreos_instance_1 = MSQL_VB->new();
		$xpl2_acarreos_instance_1->connectdb('acarreos','VISE-XPL2');
	}
}