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
use lib '/home/eflowweb/cgi-bin/acarreos_app';
use Package::Pago;


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
	@links=();
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

sub pago {
	my $mostrar = Package::Pago->new();
	my ($arrayjs,$arraycss,$htmlTemp)=$mostrar->mostrarPago($user);
	push @js_post,@{$arrayjs};
	push @css,@{$arraycss};
	my $html=qq¡ $htmlTemp ¡;
	return $html;
}
