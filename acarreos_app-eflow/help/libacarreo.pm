#!/usr/bin/perl

use DBI;

package libacarreo;
use Eflow::Libs;
use MSQL_VB;
use Encode;
use utf8;
use Switch;

require 'cgi-lib.pl';
use Eflow::user_check;
use CGI; 
use Encode;
use MIME::Lite;
use utf8;
use Eflow::Utils;

binmode(STDOUT,':utf8');
conectadb();

sub new { my $self = {}; bless $self; return $self; }

sub conectadb {
	if (!$lib) { $lib = Libs->new(); }
	if (!$libUtil) { $libUtil = Eflow::Utils->new(); }
	if (!$rAcarreos) { $rAcarreos = MSQL_VB->new(); $rAcarreos->connectdb('acarreos','Vise-XPL2');}
}

sub is_number{ $_[0] =~ /^([+-]?)(?=\d|\.\d)\d*(\.\d*)?([Ee]([+-]?\d+))?$/; }

#######################################################################################################################
#RECURSO
sub getViewBoletos{#HACE REFERENCIA AL PROYECTO VISITA, EN CASO DE OCUPAR EL RECURSO DE PROVEEDORES ESTÁ EN FUNCIONES GENERAL PERSONA
	my $self = shift;
	my $select = shift;
	my $where = shift;
	my %info;
	my @listInfo;
	my $sql=qq||;
	$sql = qq|SELECT fecha_salida, hora_salida, folio, plates, origen, destino, material, m3, elaboro,
		fecha_entrega, hora_entrega, recibio, tiempo, distance, trips, building_origin, building_destiny, 
		exit_date, id_point_origin, id_material, user_id_bank, importe_acarreo, importe_material,ticket_type $select
		FROM acarreos.dbo.carriesV2 b 
		$where|;
	$rAcarreos->execute($sql);
	if(!$rAcarreos->EOF){
		while (!$rAcarreos->EOF) {
			%info = $rAcarreos->getHash;
			foreach my $data (keys %info) {  
				if( is_number($info{$data}) ){  $info{$data} = $info{$data}*1; }
				elsif( !$info{$data} ){ $info{$data} = '' }else{$info{$data} }
			}
			push @listInfo,{%info};
			$rAcarreos->movenext;
		}
	}
	return @listInfo;
}

1;
