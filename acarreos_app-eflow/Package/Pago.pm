#!/usr/bin/perl

package Package::Pago;
binmode(STDOUT,':utf8' );
use utf8;

use strict;
use warnings;
require 'Imp/serviciopago.pl';

sub new {
    my $class = shift;
    my ($sec,$min,$hour,$mday,$mon,$year,$wday,$yday,$isdst) = localtime(time);
	my $version=$year.$mon.$mday.$hour.$min.$sec;
	my $self = {
		_homelibs=>'/lib/js/',
		_home=>'/js/acarreos/Pago/',
		_version=>$version,
	};
    bless $self, $class;
    return $self;
}

sub mostrarPago{
	my $self = shift;
	my $fk_user = shift;
	my @arrayjs;
	my @arraycss;
	my $html = qq¡
	<div id="divmenu"></div>
	<input type="hidden" id="fk_user" name="fk_user" value="$fk_user">
	<div id="contenedorinfogeneral"></div>¡;
	push @arrayjs,$self->{_home}.'principalPago.js?v='.$self->{_version};
	push @arrayjs,$self->{_home}.'cargarpago.js?v='.$self->{_version};
	return (\@arrayjs,\@arraycss,$html);
}

sub mostrarVisita{
	my $self = shift;
	my $fk_id_visita = shift;
	my @arrayjs;
	my @arraycss;
	my $html = qq¡
	<div id="divmenu"></div>
	<input type="hidden" id="fk_id_visita" name="fk_id_visita" value="$fk_id_visita">
	<div id="contenedorinfogeneral"></div>¡;
	push @arrayjs,$self->{_home}.'principalvisita.js?v='.$self->{_version};
	push @arrayjs,$self->{_home}.'cargarvisita.js?v='.$self->{_version};
	return (\@arrayjs,\@arraycss,$html);
}

sub mostrarBancos{
	my $self = shift;
	my $fk_id_visita = shift;
	my @arrayjs;
	my @arraycss;
	my $html = qq¡
	<div id="divmenu"></div>
	<input type="hidden" id="fk_id_visita" name="fk_id_visita" value="$fk_id_visita">
	<div id="contenedorbancos"></div>¡;
	push @arrayjs,$self->{_home}.'bancos.js?v='.$self->{_version};
	return (\@arrayjs,\@arraycss,$html);
}

sub mostrarBancos{
	my $self = shift;
	my $fk_id_visita = shift;
	my @arrayjs;
	my @arraycss;
	my $html = qq¡
	<div id="divmenu"></div>
	<input type="hidden" id="fk_id_visita" name="fk_id_visita" value="$fk_id_visita">
	<div id="contenedorbancos"></div>¡;
	push @arrayjs,$self->{_home}.'bancos.js?v='.$self->{_version};
	return (\@arrayjs,\@arraycss,$html);
}

sub mostrarInformacionAdicional{
	my $self = shift;
	my $fk_id_visita = shift;
	my @arrayjs;
	my @arraycss;
	my $html = qq¡
	<div id="divmenu"></div>
	<input type="hidden" id="fk_id_visita" name="fk_id_visita" value="$fk_id_visita">
	<div id="contenedorinfoadicional"></div>¡;
	push @arrayjs,$self->{_home}.'informacionadicional.js?v='.$self->{_version};
	return (\@arrayjs,\@arraycss,$html);
}

sub mostraReporteFotografico{
	my $self = shift;
	my $fk_id_visita = shift;
	my @arrayjs;
	my @arraycss;
	my $html = qq¡
	<div id="divmenu"></div>
	<input type="hidden" id="fk_id_visita" name="fk_id_visita" value="$fk_id_visita">
	<div id="contenedoreportefotografico"></div>¡;
	push @arrayjs,$self->{_home}.'reportefotografico.js?v='.$self->{_version};
	return (\@arrayjs,\@arraycss,$html);
}

sub mostraEstrategias{
	my $self = shift;
	my $fk_id_visita = shift;
	my @arrayjs;
	my @arraycss;
	my $html = qq¡
	<div id="divmenu"></div>
	<input type="hidden" id="fk_id_visita" name="fk_id_visita" value="$fk_id_visita">
	<div id="contenedorestrategias"></div>¡;
	push @arrayjs,$self->{_home}.'estrategias.js?v='.$self->{_version};
	return (\@arrayjs,\@arraycss,$html);
}

1;