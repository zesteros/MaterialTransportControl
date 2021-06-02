#!/usr/bin/perl

use utf8;
binmode STDOUT,':utf8';

our $pro=82;
our $m_points=402;
our $m_points_authorization=363;
our $m_draw_points=364;
our $m_config_users=365;
our $m_config_materials=366;

our $m_config=403;
our $m_supervision=404;
our $m_supplies=405;
our $m_link_cubages=406;
our $m_link_terminals=407;
our $m_link_tags=408;
our $m_tickets_carries = 409;
our $m_tickets_gral = 410;
our $m_locations = 411;
our $m_reprints = 412;
our $m_process_payment = 450;
our $m_desarrollo = 451;
our $m_assign_banks=465;

our @MLateral;

if($in{'noMenu'}!=1){
	@MLateral=(
		{ 'href' => "$home/points.cgi?cmd=inicio&autorizado=0", 'title'=> "Bancos y tiros", 'class' => '','m'=>"$m_points",'p'=>"$pro",'u',"$user"},
		{ 'href' => "$home/config.cgi?cmd=inicio", 'title'=> "Configuración", 'class' => '','m'=>"$m_config",'p'=>"$pro",'u',"$user"},
		{ 'href' => "$home/supervision.cgi?cmd=inicio", 'title'=> "Supervisión", 'class' => '','m'=>"$m_supervision",'p'=>"$pro",'u',"$user"},
		{ 'href' => "$home/payment.cgi?cmd=inicio", 'title'=> "Pago", 'class' => '','m'=>"$m_process_payment",'p'=>"$pro",'u',"$user"},
		{ 'href' => "$home/supplies.cgi?cmd=inicio", 'title'=> "Suministros", 'class' => '','m'=>"$m_supplies",'p'=>"$pro",'u',"$user"},
		{ 'href' => "/cgi-bin/cubicacion/cubicacion.cgi?cmd=inicio", 'title'=> "Cubicaciones", 'class' => '','m'=>"$m_link_cubages",'p'=>"$pro",'u',"$user"},
		{ 'href' => "/cgi-bin/prenomina_che/terminales.cgi", 'title'=> "Terminales", 'class' => '','m'=>"$m_link_terminals",'p'=>"$pro",'u',"$user"},
		{ 'href' => "/cgi-bin/activos/activos.cgi?id_almacen=10", 'title'=> "Activos (Tags)", 'class' => '','m'=>"$m_link_tags",'p'=>"$pro",'u',"$user"},
		{ 'href' => "$home/Pago/paymentV2.cgi?cmd=pago", 'title'=> "Pago V2", 'class' => '','m'=>"$m_desarrollo",'p'=>"$pro",'u',"$user"}
	);


}

our $VLIBSV2 = "0.0.03";
our $VFUNCTIONSUTILSV2 = "0.0.22";
our $VOBJECTSGENERAL  = "0.0.08";
our $VOBJECTSINSUMOS  = "0.0.03";
our $VOBJECTSPERSONA = "0.0.04";
our $VOBJECTSPERSONAOBRA = "0.0.03";
our $VOBJECTSOBRA= "0.0.09";
our $VOBJECTSOBRAPROVEEDOR = "0.0.15";

1;