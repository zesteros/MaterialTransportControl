#!/usr/bin/perl

use Eflow::user_check;
use Eflow::Libs;
use Eflow::GH;
use MSQL_VB;
use HTTP::Tiny;
use JSON;
use Data::Dumper;
use Switch;
binmode STDOUT,':utf8';
use utf8;


conectadb();
check_employee(9103, 336.23);
check_employee(7319, 392);
check_employee(8882, 336.23);
check_employee(5906, 430.02);




sub check_employee{

	my $id_employee = shift;
	my $compare_to_salary = shift;

	my $q = qq| 
		SELECT top 1 MTRAB, MFecha, MSueldo, MSueldo*15+272.55 as fortnight
		FROM [vise-nomina\\nomina].[VISEIND2009_F141115113346].dbo.[Catalogo Empleados Movimientos]
		where mtrab = $id_employee
		order by mfecha desc;
	|;
	#7319

	$rsp->execute($q);

	my $sal = $rsp->itemvalue('MSueldo');
	my $fortnight = $lib->fnumeric($rsp->itemvalue('fortnight'));

	$q = qq|select nameT, email from usuario.dbo.sn_eflow where super_nomina_id = $id_employee;|;

	$rsp->execute($q);

	my $email = $rsp->itemvalue('email');
	my $name = $rsp->itemvalue('nameT');

	if($sal != $compare_to_salary){
		$lib->email_user_html_noFile($email,
			"Increases report",
			"<html>
				Hi $name 
				<br>
				Your day salary has been changed to <b>\$ $sal</b>
				<br>
				That means your fortnight pay will be <b>\$ $fortnight</b>
			</html>");
	}
}

sub conectadb {	
	if(!$lib){
		$lib = Libs->new();
	}
	if (!$rsp) {
		$rsp = MSQL_VB->new();
		$rsp->connectdb('usuario','Vise-XPL2');
	}
}