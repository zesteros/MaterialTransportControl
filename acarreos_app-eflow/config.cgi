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

$post = new CGI;
%ine = $post->Vars;
$lib = Libs->new();
$lib->hash_decode(\%ine);


$|=1;
$qt = new CGI;
%in = $qt->Vars;
conectadb();
$lib->hash_decode(\%in);

$user = user_check(1);##ID DEL EMPLEADO LOGEADO
$home ="/cgi-bin/acarreos_app";
$pDir = "$home/config.cgi";

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
		"/js/SUtil/utilErrors.js",
		"/js/acarreos/config.js?v=$sec$min$hour$mday$mon",
	);
my @css=(
	"/css/style5.css?v=1.0.1",
	"/css/icons.css",
	"/lib/css/jquery.dataTables.min.css",
	"/lib/jquery/jquery-ui-1.11.1/jquery-ui.min.css",
	"/lib/js/multiselect/jquery.multiselect.css",
	"/lib/js/jquery.dataTables.yadcf.css",
	"/lib/js/multiselect/jquery.multiselect.filter.css",
	"/lib/js/select2.css"
	);

if(!$in{'noMenu'}){
	@links=(
		{ 'link'  => "$pDir?cmd=config_users$gml", 'titleLink' => "Configuración de usuarios", 'class' => 'config_users','m'=>"$m_config_users",'p'=>"$pro",'u',"$user"},
		{ 'link'  => "$pDir?cmd=config_materials$gml", 'titleLink' => "Configuración de materiales", 'class' => 'config_materials','m'=>"$m_config_materials",'p'=>"$pro",'u',"$user"},

	);
}

@log=();
$title = "Acarreos VISE - Configuración";
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

sub config_users{
	my $q = qq|
		SELECT 
			acarreos_usuarios.id_empleado,
			nasus.nombre, 
			nasus.apellido_paterno,
			nasus.apellido_materno,
			acarreos_usuarios.obra,
			obras.DESCRIPCION,
			acarreos_usuarios.estatus,
			acarreos_usuarios.add_date,
			acarreos_usuarios.add_user,
			acarreos_usuarios.imei,
			acarreos_usuarios.model,
			equipo.activo
		FROM acarreos.dbo.acarreos_usuarios acarreos_usuarios 
			left join usuario.dbo.nasus_empleados nasus on idEmpleado = id_empleado
			left join combustible.dbo.obras_navision obras on acarreos_usuarios.obra = obras.OBRA COLLATE Latin1_General_CI_AS
			left join [vise-nav].sistemas.dbo.cas_equipo equipo
				on acarreos_usuarios.imei = equipo.imei collate SQL_Latin1_General_CP850_CI_AI
		where acarreos_usuarios.estatus = 'A'
		order by acarreos_usuarios.obra desc;
	|;


	$xpl2_acarreos_instance_1 -> execute($q);

	while ($d = $xpl2_acarreos_instance_1->getrow_hashref) {
		$$d{'imei'} = $$d{'imei'} eq "0" ? "N/A":$$d{'imei'};
		$content.=qq¡
			<tr>
				<td>
					$$d{'id_empleado'}
				</td>
				<td>
					$$d{'nombre'} $$d{'apellido_paterno'} $$d{'apellido_materno'}
				</td>
				<td>
					$$d{'obra'} / $$d{'DESCRIPCION'}
				</td>
				<td>$$d{'imei'}</td>
				<td>$$d{'model'}</td>
				<td>$$d{'activo'}</td>
				<td>
			 		<p>
					<br>
					<button type='button' class='btn btn-warning' onclick="deleteAssignment('$$d{'id_empleado'}');">
						Eliminar asignación
						<i class="glyphicon glyphicon-remove" style="margin-left:10px;"></i>
					</button></p>
				</td>
			</tr>
		¡;
	}

	$q = qq|
		SELECT 
			acarreos_usuarios.id_empleado,
			nasus.nombre, 
			nasus.apellido_paterno,
			nasus.apellido_materno,
			acarreos_usuarios.obra,
			obras.DESCRIPCION,
			acarreos_usuarios.estatus,
			acarreos_usuarios.add_date,
			acarreos_usuarios.add_user,
			acarreos_usuarios.imei,
			acarreos_usuarios.model,
			acarreos_usuarios.imei_requested,
			acarreos_usuarios.model_requested,
			convert(varchar(100),acarreos_usuarios.upd_date, 103) + ' '+convert(varchar(100),acarreos_usuarios.upd_date, 108) as request_date
		FROM acarreos.dbo.acarreos_usuarios acarreos_usuarios 
			left join usuario.dbo.nasus_empleados nasus on idEmpleado = id_empleado
			left join combustible.dbo.obras_navision obras on acarreos_usuarios.obra = obras.OBRA COLLATE Latin1_General_CI_AS
		where acarreos_usuarios.estatus = 'A' AND
			acarreos_usuarios.imei_requested is not null and 
			acarreos_usuarios.imei_requested not in (
				SELECT aux.imei from acarreos.dbo.acarreos_usuarios aux
				where aux.id_empleado = acarreos_usuarios.id_empleado and aux.estatus = 'A'
			)
		order by acarreos_usuarios.obra desc;
	|;


	$xpl2_acarreos_instance_1 -> execute($q);

	while ($d = $xpl2_acarreos_instance_1->getrow_hashref) {
		$devices.=qq¡
			<tr>
				<td>
					$$d{'id_empleado'}
				</td>
				<td>
					$$d{'nombre'} $$d{'apellido_paterno'} $$d{'apellido_materno'}
				</td>
				<td>
					$$d{'obra'} / $$d{'DESCRIPCION'}
				</td>
				<td>$$d{'model_requested'}</td>
				<td>$$d{'imei_requested'}</td>
				<td>$$d{'request_date'}</td>
				<td>
			 		<p>
					<br>
					<button type='button' class='btn btn-success' onclick="approveDevice('$$d{'id_empleado'}','$$d{'model_requested'}', '$$d{'imei_requested'}');">
						Aprobar dispositivo
						<i class="glyphicon glyphicon-ok" style="margin-left:10px;"></i>
					</button></p>
				</td>
			</tr>
		¡;
	}



	my $html = qq¡
		<input type="hidden" name="user_session" id="user_session" value="$user">
		<div class="panel panel-warning">
			<div class="panel-heading">Asignar usuario a obra</div>
			<div class="panel-body">
				<form method="POST" class="form-horizontal" name="F" id="F" action="config.cgi" enctype="multipart/form-data">
					<input type="hidden" name="cmd" value="save_building">
					<div class="form-group" id="divEmpleado">
						<label for="nombre_empleado" class="col-lg-2 control-label" id="lab_id_empleado">Empleado a configurar</label>
						<div class="col-lg-10">
							<input id="id_empleado" name="id_empleado" type="hidden" value="">
						  <input  type="text" class="form-control" id="nombre_empleado" name="nombre_empleado" value="$t{'employee_name'}" placeholder="Seleccionar Empleado" required>
						</div>
					  </div>

					  <div class="form-group">
						<label for="notify_when" class="col-lg-2 control-label">Obra</label>
						<div class="col-lg-10">
						  	<select id="building" name="building">
						  		<option>Selecciona una obra</option>
						  	</select>
						</div>
					</div>
					  <div class="form-group">
						<div class="col-lg-offset-2 col-lg-10">
						  <button type="submit" class="btn btn-primary" id="guardarButton" >Guardar</button>
						</div>
					  </div>
				</form>
			</div>
		</div>

		<div class="panel panel-success">
		  <div class="panel-heading">Usuarios asignados</div>
		  <div class="panel-body">
			    <table  id="users-table">
			        <thead>
			            <tr>
							<th>
								ID Empleado
							</th>
							<th>
								Nombre
							</th>
							<th>
								Obra
							</th>
							<th>
								IMEI
							</th>
							<th>
								Dispositivo
							</th>
							<th>
								No. Activo CAS
							</th>
							<th>
								Acciones
							</th>
						</tr>
			        </thead>
			        <tbody>
			            $content
			        </tbody>
	       		</table>
	       		<br>

		  </div>
		</div>


		<div class="panel panel-info">
		  <div class="panel-heading">Autorizar dispositivo</div>
		  <div class="panel-body">
			    <table  id="devices-table">
			        <thead>
			            <tr>
							<th>
								ID Empleado
							</th>
							<th>
								Nombre
							</th>
							<th>
								Obra
							</th>
							<th>
								Dispositivo solicitado
							</th>
							<th>
								IMEI del dispositivo
							</th>
							<th>
								Fecha de solicitud
							</th>
							<th>
								Acciones
							</th>
						</tr>
			        </thead>
			        <tbody>
			            $devices
			        </tbody>
	       		</table>
	       		<br>

		  </div>
		</div>

		<script>


		</script>
	 ¡;
	return $html;
}


sub config_materials{

		my $building_to_show = $ine{'building_to_show'};


		my $q = qq|
		SELECT 
			materiales.id_asignacion,
			materiales.id_material_navision,
			materiales.obra,
			materiales.acronimo_para_tag,
			materiales.add_user,
			materiales.precio_unitario,
			(select top 1 name from usuario.dbo.sn_eflow where userid_eflow = materiales.add_user) as nombre,
			(select top 1first_lastname from usuario.dbo.sn_eflow where userid_eflow = materiales.add_user) as apellido_paterno,
			(select top 1 second_lastname from usuario.dbo.sn_eflow where userid_eflow = materiales.add_user) as apellido_materno,
			(select top 1 super_nomina_id from usuario.dbo.sn_eflow where userid_eflow = materiales.add_user) as id_empleado,
			materiales.add_date,
			materiales.estatus,
			item_navision.[No_ 2],
		    item_navision.[Description] as descripcion,
		    item_navision.[Description 2],
		    item_navision.[Base Unit of Measure] as unidad
		FROM acarreos.dbo.acarreos_materiales materiales
			left join  [VISE-SQ8].visesiv.dbo.[VISE\$Item] item_navision on materiales.id_material_navision = item_navision.[No_] COLLATE Latin1_General_CI_AS
		WHERE 
			materiales.obra = '$building_to_show' and estatus = 'A';
	|;

	$xpl2_acarreos_instance_1 -> execute($q);

	my $row_count = 0;

	while ($d = $xpl2_acarreos_instance_1->getrow_hashref) {

		$$d{'precio_unitario'} = $$d{'precio_unitario'} ? $lib->fnumeric($$d{'precio_unitario'}) : "0.00";

		$content.=qq¡
			<tr>
				<td>
					$$d{'id_material_navision'}
				</td>
				<td>
					$$d{'descripcion'}
				</td>
				<td>
					$$d{'unidad'}
				</td>
				<td>
					<div class="row">
						<div class="col-md-10">
							<input class="form-control" type="text" name="unit_price" value="\$ $$d{'precio_unitario'}" disabled>
						</div>
						<div class="col-md-2">
							<div class="row">
							<button class="btn btn-success" onclick="saveUnitPrice('$$d{'id_asignacion'}'"><i class="glyphicon glyphicon-floppy-disk"></i></button>
							</div>
							<div class="row">
							<button class="btn btn-warning" onclick="editField('$$d{'id_asignacion'}','$row_count')"><i class="glyphicon glyphicon-edit"></i></button>
							</div>
						</div>
						
					</div>
				
				</td>
				<td>
					$$d{'nombre'} $$d{'apellido_paterno'} $$d{'apellido_materno'} ($$d{'id_empleado'})
				</td>
				<td>
					$$d{'obra'}
				</td>
				<td>
			 		<p>
					<br>
					<button type='button' class='btn btn-warning' onclick="deleteMaterial('$$d{'id_asignacion'}','$building_to_show');">
						Eliminar asignación
						<i class="glyphicon glyphicon-remove" style="margin-left:10px;"></i>
					</button></p>
				</td>
			</tr>
		¡;

		$row_count++;
	}


	my $html = qq¡
				<input type="hidden" name="user_session" id="user_session" value="$user">

		<div class="panel panel-success">
		  <div class="panel-heading">Materiales asignados</div>
		  <div class="panel-body">
		  	<form class="form-horizontal" name="F" id="F" action="config.cgi" method="post" enctype="multipart/form-data"> 
		  		<input type="hidden" id="cmd" name="cmd" value="config_materials">
		  		<input type="hidden" id="selected_building" name="selected_building" value="$building_to_show">
		  		<div class="form-group" id="divEmpleado">
					<label for="nombre_obra" class="col-lg-2 control-label" id="lab_id_empleado">Selecciona una obra</label>
					<div class="col-lg-10">
						<select id="all-buildings-1" name="building_to_show">
						</select>
						<button type="submit" class="btn btn-primary text-left">Buscar</button>
					</div>
					<br><br>
					
				</div>
			</form>
				
			      <table  id="users-table">
			        <thead>
			            <tr>
			            	<th>
			            		ID Material Navision
			            	</th>
			            	<th>
			            		Descripción
			            	</th>
			            	<th>Unidad</th>
			            	<th>
			            		Precio unitario
			            	</th>
							<th>
								Usuario agregó
							</th>
							<th>
								Asignado a obra
							</th>
							<th>
								Acciones
							</th>
						</tr>
			        </thead>
			        <tbody>
			            $content
			        </tbody>
	       		</table>
					  
	       		<br>

		  </div>
		</div>
<div class="panel panel-warning">
			<div class="panel-heading">Asignar materiales a obra</div>
			<div class="panel-body">

				<input type="hidden" id="eflow-id" name="eflow-id" value="$user">
			
					<label>Obra a configurar</label>
					<div >
						<select id="all-buildings">
						</select>
					</div>

					<br>	
					<label>Selecciona un material de Navision</label>
					<div ">
					  	<input id="id_material" name="id_material" type="hidden" value="">
						<input type="hidden" id="material_description" name="material_description" value="">
					 	<input  type="text" class="form-control" id="nombre_material" name="nombre_material" value="$t{'employee_name'}" placeholder="Seleccionar material" required>

					</div>
					<br>
					<label>Ingresa un acrónimo del material para el TAG</label>
					<div>
					 	<input  type="text" class="form-control" id="acronimo" name="acronimo" value="$t{'employee_name'}" placeholder="Seleccionar acrónimo" required>
					</div>
					<br>
					<label>Ingresa un precio unitario</label>
					<div class="input-group"> 
				        <span class="input-group-addon">\$</span>
				        <input type="number" value="0.00" min="0" step="0.01" data-number-to-fixed="2" data-number-stepfactor="100" class="form-control currency" id="unit_price" required/>
				    </div> 
					<br>		 
					<div align="right" style="margin-right:30px;">
					  <button class="btn btn-primary btn-lg float-right" id="add-material-button" onclick="addMaterialToTable();" >Agregar</button>
					</div>
					<br>
					<br>
					
				<table id="new-materials-to-building">
					<thead>
				  	<tr>
		            	<th>
		            		ID Material Navision
		            	</th>
		            	<th>
		            		Descripción
		            	</th>
		            	<th>
		            		Precio unitario
		            	</th>
						<th>
							Usuario agregó
						</th>
						<th>
							Asignado a obra
						</th>
						<th>
							Acciones
						</th>
					</tr>
					</thead>
					<tbody>
					</tbody>
				</table>
				<br>
				<div align="right" style="margin-right:30px;">
				  <button class="btn btn-success btn-lg" id="saveButton" onclick="saveMaterials();">Guardar</button>
				</div>
				<br>
			</div>
		</div>

		<script>


		</script>
	 ¡;
	return $html;
}


sub save_building{
	my $id_empleado = $ine{'id_empleado'};
	my $obra = $ine{'building'};

	my $query_insert = qq|

		BEGIN
   				IF NOT EXISTS (SELECT id_empleado FROM acarreos.dbo.acarreos_usuarios
                   WHERE id_empleado = $id_empleado and estatus = 'A')
		   		BEGIN
					INSERT INTO acarreos.dbo.acarreos_usuarios
					(id_empleado, obra, estatus, add_date, add_user,imei)
					VALUES($id_empleado, '$obra', 'A', (getdate()), $user,'0')
		   		END
		END
	|;

	$xpl2_acarreos_instance_1->execute($query_insert);



	return " ".$gh->alertM("Registro agregado exitosamente.","i").config_users();
}

sub update_user{
	my $id_empleado = $in{'id_empleado'};
	my $update_imei = $in{'update_imei'};



	if($id_empleado and !$update_imei){
		$q = qq|
			update acarreos.dbo.acarreos_usuarios
			set estatus='B', upd_date=(getdate()), upd_user=$user
			WHERE id_empleado=$id_empleado;
		|;
		$xpl2_acarreos_instance_1->execute($q);

	} 
	if($id_empleado and $update_imei){

		$q = qq|

			SELECT top 1  imei_requested, model_requested 
			from acarreos.dbo.acarreos_usuarios 
			where 
				imei_requested is not null and 
				imei_requested not in (
					SELECT imei from acarreos.dbo.acarreos_usuarios aux
					where aux.id_empleado = $id_empleado and estatus='A'
				) and id_empleado = $id_empleado and estatus = 'A'
		|;
		if($user eq 871){print $q}

		$xpl2_acarreos_instance_1->execute($q);

		my $imei_requested = $xpl2_acarreos_instance_1->itemvalue('imei_requested');
		my $model_requested = $xpl2_acarreos_instance_1->itemvalue('model_requested');

		$q = qq|
			update acarreos.dbo.acarreos_usuarios
			set imei = '$imei_requested', model='$model_requested', upd_date=(getdate()), upd_user=$user
			WHERE id_empleado=$id_empleado;
		|;
		$xpl2_acarreos_instance_1->execute($q);
	}

	return " ".$gh->alertM("Asignación removida exitosamente.","i").config_users();
}

sub update_material{
	my $id_material = $in{'id_material'};

	if($id_material){
		$q = qq|
			update acarreos.dbo.acarreos_materiales
			set estatus='B', upd_date=(getdate()), upd_user=$user
			WHERE id_asignacion=$id_material;
		|;
		$xpl2_acarreos_instance_1->execute($q);

	}

	return " ".$gh->alertM("Material removido exitosamente.","i").config_materials();
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
