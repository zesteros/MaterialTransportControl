#!/usr/bin/perl

use Eflow::user_check;
use Eflow::Libs;
use utf8;
use MSQL_VB;
use CGI;
use Encode;
use MIME::Lite;
use Eflow::GH;
use Eflow::jstl;
use Eflow::Utils;
use Eflow::UtilsV2;

use JSON;
use Data::Dumper;
use Switch;

require '/home/eflowweb/cgi-bin/acarreos_app/help/libacarreo.pm'; 

binmode STDOUT,':utf8';
use utf8;

print PrintHeader();

ReadParse();
$lib = Libs->new();
$lib->hash_decode(\%in);
use CGI::Carp qw(fatalsToBrowser);#error
$user = user_check(1);
$q = new CGI;
%ine = $q->Vars;
our %DATA;

require '/home/eflowweb/cgi-bin/proy_obra/menu.pl';

$CMD=$in{'cmd'};
conectadb();
if($ine{'cmd'}){
	$CMD=$ine{'cmd'};
}
#print "el cmd es -->$CMD<---  -->$in{'cmd'}<--  ---->$in{'q'}<----";
eval { if (!$CMD) { $body = inicio();} elsif (exists &$CMD) { $body = &$CMD; }else{die;} 1;} 
or do { print "No se localizo el metodo $CMD";  };

#/////////////////////////////////////////////////////////////////////
sub conectadb {	
	if (!$lib) { $lib = Libs->new(); }
	if (!$libUtil) { $libUtil = Eflow::Utils->new(); }
	if (!$libUtilV2) { $libUtilV2 = Eflow::UtilsV2->new(); }
	if (!$libAcarreo) { $libAcarreo = libacarreo->new(); }
	if (!$rAcarreos) { $rAcarreos = MSQL_VB->new(); $rAcarreos->connectdb('acarreos','Vise-XPL2');}
}
#/////////////////////////////////////////////////////////////////
#*BOLETOS****************************************
sub getValidarBoleto{ #información de la ubicación del banco
	my $where=qq|WHERE folio=$ine{'sheet_number'} |;
	my @listInfo = $libAcarreo->getViewBoletos('',$where);
	my %boleto = (@listInfo.length > 0) ? %{$listInfo[0]}:();
	$info{'ticket'} = ( @listInfo.length > 0 ) ? \%boleto:();
	$info{'mensaje'} = ( @listInfo.length > 0 ) ? "":"No existe o fue eliminado del sistema";

	if( @listInfo.length > 0){
		if($boleto{'ticket_type'} != 1 || $boleto{'ticket_type'} != 4){
			$info{'mensaje'} = "No es correcto para el tipo de pago"; 
		}
	}



	my $json = JSON->new->allow_nonref;
   	my $json_text = encode_json \%info;
	print $json_text;
}



