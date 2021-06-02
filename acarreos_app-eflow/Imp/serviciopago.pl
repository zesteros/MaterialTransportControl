#!/usr/bin/perl

use Eflow::Utils;
use Eflow::UtilsObra;
use utf8;
binmode STDOUT,':utf8';
conectadb();

sub conectadb {
	if (!$lib) { $lib = Libs->new(); }
	if (!$libUtil) { $libUtil = Eflow::Utils->new(); }
	if (!$libUtilObra) { $libUtilObra = Eflow::UtilsObra->new(); }
}

sub getListObras{
	my $estatus = shift;
	my $where=qq|
		INNER JOIN [general].dbo.[Catalogo Obra Estatus] oe ON oe.id=fk_id_estatus
		WHERE fk_id_estatus in ($estatus)|;
	my @listInfo = $libUtilObra->getTableObra(',oe.nombre estatusobra',$where);
	my $tbody=qq||;my $t=0;

	foreach my $in (@listInfo) {
    	%info=%{$in};
		$t++;
		my $accion=qq|<button class="btn btn-success" onclick="verdetalleproveedores($info{'id_referencia'},$info{'fk_id_estatus'});" ><span class="glyphicon glyphicon-list"></span> Consultar</button>|;
		$tbody.=qq|<tr id="fila_obra_$info{'id_referencia'}">
				<td>$t</td>
			    <td>$info{'no'}</td>
			    <td>$info{'nombre_obra'}</td>
			    <td>$info{'estatusobra'}</td>
			    <td>$accion</td>
			</tr>
		|;
	}

	$tabla = qq|
		<div class="panel panel-info">
			<div class="panel-heading clearfix">
				<a data-toggle="collapse" href="#accordionPanelObra"><h3 class="panel-title pull-left" style="padding-top: 7.5px;"><b>Obras</b></h3></a>
			</div>
			<div id="accordionPanelObra" class="panel-collapse collapse in">
				<div id="accordionPanelProveedoresBody" class="panel-body">
					<table id="tablaoption" class="display table table-striped tabla" cellspacing="0" width="100%">
					   	<caption><h3>Obras</h3></caption>
					   	<thead>
					            <tr>
					            	<th>Nº</th>
					                <th>Obra</th>
					                <th>Nombre</th>
					                <th>Estatus</th>
					                <th>Acción</th>
					            </tr>
					    </thead>
					    <tbody id="tbodyopt">	$tbody  </tbody>
					</table>
				</div>
		      	<div class="panel-footer clearfix">
						<!---<div class="btn-group pull-right">
							<button type="button" class="btn btn-success" onclick="savelistproveedor()"><span class="glyphicon glyphicon-floppy-disk"></span> Guardar</button>
						</div>-->
				</div>
			</div>
		</div>
	
	|;
	return $tabla;
}

sub getListVisitaObra{
	my $where=qq| where  cvp.status='A' and  cvvo.estatus='A' and cvvo.id_estatus=1|;
	my @listInfo = $libUtil->getListVisita('',$where);
	my $tbody=qq||;my $t=0;

	foreach my $in (@listInfo) {
    	%info=%{$in};
		$t++;
		my $accion=qq|<button class="btn btn-success" onclick="verdetallevisitaobra($info{'id_vis_programada'});" ><span class="glyphicon glyphicon-list"></span> Consultar</button>|;
		my %cuentaeflow =  %{$libUtil->get_eflow_where("WHERE super_nomina_id=$info{'superintendente'}")};
		$tbody.=qq|<tr id="fila_obra_$info{'id_referencia'}">
				<td>$t</td>
			    <td>$info{'nombre'}</td>
			    <td>$info{'descripcion'}</td>
			    <td>$cuentaeflow{'nameT'} </td>
			    <td>$info{'formatofalla'}</td>
			    <td>$info{'formatolimite'}</td>
			    <td>$accion</td>
			</tr>
		|;
	}

	my $tabla = qq|
		<div class="panel panel-info">
			<div class="panel-heading clearfix">
				<a data-toggle="collapse" href="#accordionPanelVisita"><h3 class="panel-title pull-left" style="padding-top: 7.5px;"><b>Visita obra</b></h3></a>
			</div>
			<div id="accordionPanelVisita" class="panel-collapse collapse in">
				<div id="accordionPanelProveedoresBody" class="panel-body">
					<table id="tablaoption" class="display table table-striped tabla" cellspacing="0" width="100%">
					   	<caption><h3>Visitas programadas</h3></caption>
					   	<thead>
					        <tr>
					            <th>Nº</th>
					            <th>Licitación</th>
					            <th>Nombre</th>
					            <th>Superintendente</th>
					            <th>Fecha programada</th>
					            <th>Fecha limite</th>
					            <th>Acción</th>
					        </tr>
					    </thead>
					    <tbody id="tbodyopt">	$tbody  </tbody>
					</table>
				</div>
		      	<div class="panel-footer clearfix">
						<!---<div class="btn-group pull-right">
							<button type="button" class="btn btn-success" onclick="savelistproveedor()"><span class="glyphicon glyphicon-floppy-disk"></span> Guardar</button>
						</div>-->
				</div>
			</div>
		</div>
	
	|;
	return $tabla;
}
