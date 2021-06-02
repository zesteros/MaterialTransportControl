#!/usr/bin/perl -w

use Eflow::user_check;
use MSQL_VB;

use CGI;

binmode STDOUT,':utf8';
use utf8;
use JSON;
use Data::Dumper;
use Time::Local;

use CGI::Carp qw(fatalsToBrowser);#error

print PrintHeader();
ReadParse();
#Instrucciones para agregar el post (ine)
$post = new CGI;
%ine = $post->Vars;
$lib = Libs->new();

$lib->hash_decode(\%in);
#post
$lib->hash_decode(\%ine);
$user = user_check(1);


our $CMD=$in{'cmd'};
conectadb();

#instruccion para sacar el cmd desde el post
if($ine{'cmd'}){
	$CMD=$ine{'cmd'};
}

#hola


#print "el cmd es $CMD";
eval {
    if (!$CMD) {
		$body = inicio();
	} elsif (exists &$CMD) {
		$body = &$CMD;
	}else{
		die;
	}
	1;
} or do {
	print "No se localizo el metodo";
    #my $e = $\@;
    #push \@log ,{'log' => 'e' , 'mensaje' => "Algo paso mal no se encontro el metodo: $e\n"};
	#$ocem = 1;
};
sub get_all_points{
	my $q = qq¡
		SELECT 
			aca.id_punto,
			aca.tipo_punto,
			aca.nombre_banco,
			aca.radio,
			aca.cadenamiento,
			aca.es_banco_y_tiro,
			aca.latitud,
			aca.longitud,
			aca.autorizado,
			convert(varchar(100),aca.reg_date,103) as fecha_registro,
			convert(varchar(100),aca.reg_date,108) as hora_registro,
			aca.estatus, 
			convert(varchar(100),aca.add_date,103) as fecha_agregado,
			convert(varchar(100),aca.add_date,108) as hora_agregado, 
			aca.add_user, 
			aca.upd_date, 
			aca.upd_user,
			puntos_por_obra.obra
		FROM 
			acarreos.dbo.acarreos_puntos aca inner join 
			acarreos.dbo.acarreos_puntos_por_obra puntos_por_obra on aca.id_punto = puntos_por_obra.id_punto 
		WHERE 
			aca.estatus = 'A';
	¡;
	$xpl2_instance_1 -> execute($q);

	if($xpl2_instance_1->EOF){
		$content = qq|{"data":false}|;
	} else {

		$content = "\{\"data\": \[";
		while ($d = $xpl2_instance_1->getrow_hashref) {
				$content .= "\{
					\"id_punto\":$$d{'id_punto'},
					\"latitud\":$$d{'latitud'},
					\"longitud\":$$d{'longitud'},
					\"nombre_banco\":\"$$d{'nombre_banco'}\",
					\"cadenamiento\":\"$$d{'cadenamiento'}\",
					\"radio\":$$d{'radio'},
					\"es_banco_y_tiro\":$$d{'es_banco_y_tiro'},
					\"autorizado\":$$d{'autorizado'},
					\"fecha_registro\":\"$$d{'fecha_registro'}\",
					\"hora_registro\":\"$$d{'hora_registro'}\",
					\"fecha_agregado\":\"$$d{'fecha_agregado'}\",
					\"hora_agregado\":\"$$d{'hora_agregado'}\",
					\"add_user\":$$d{'add_user'},
					\"obra\":\"$$d{'obra'}\"
				\},";
		}
		chop($content);
		$content .= "\] \}";
	}
	print $content;
}

sub get_user_locations{
	my $id_user = $ine{'id_user'};

	#$id_user = $id_user>0 ? $id_user : ($id_user)*(-1);

	my $q = $id_user > 0 ? qq|
		SELECT  
			top 100 location,
			imei,
			add_user,
			users.nameT,
			users.super_nomina_id,
			convert(varchar(100),add_date,103) as fecha_registro,
			convert(varchar(100),add_date,108) as hora_registro
		FROM acarreos.dbo.acarreos_usuarios_sincronizaciones sync 
			left join usuario.dbo.sn_eflow users on sync.add_user = users.userid_eflow
		WHERE add_user = $id_user
		order by add_date desc;
	| : 
	qq|
		SELECT  
			top 100 sync.location,
			sync.imei,
			sync.add_user,
			users.nombre + ' ' +users.apellido_paterno+ ' ' +users.apellido_materno as nameT,
			users.idEmpleado as super_nomina_id,
			convert(varchar(100),sync.add_date,103) as fecha_registro,
			convert(varchar(100),sync.add_date,108) as hora_registro
		FROM acarreos.dbo.acarreos_usuarios_sincronizaciones sync 
			left join usuario.dbo.nasus_empleados users on sync.add_user*(-1) = users.idEmpleado 
		WHERE sync.add_user = $id_user
		order by sync.add_date desc;
	| 
	;

	$xpl2_instance_1->execute($q);

	while ($d = $xpl2_instance_1->getrow_hashref) {
		if($xpl2_instance_1->EOF){
			$content = qq|{"data":false}|;
		} else {

			$content = "\{\"data\": \[";
			while ($d = $xpl2_instance_1->getrow_hashref) {
					$content .= "\{
						\"location\":\"$$d{'location'}\",
						\"imei\":\"$$d{'imei'}\",
						\"username\":\"$$d{'nameT'}\",
						\"super_nomina_id\":\"$$d{'super_nomina_id'}\",
						\"fecha\":\"$$d{'fecha_registro'}\",
						\"hora\":\"$$d{'hora_registro'}\"
					\},";
			}
			chop($content);
			$content .= "\] \}";
		}
	}
	print $content;
} 

sub get_points_in_tickets{
	my $data = $ine{'q'};

	my $sql = qq|
		select distinct aux.id_point_origin as id_punto,
			CONVERT(VARCHAR(MAX),puntos.nombre_banco) as nombre,
			CONVERT(VARCHAR(MAX),puntos.cadenamiento) as cadenamiento
		from acarreos.dbo.acarreos_boletos aux
			left join acarreos.dbo.acarreos_puntos puntos 
				on aux.id_point_origin = puntos.id_punto
		where aux.id_point_origin like '%$data%'
		or puntos.nombre_banco like '%$data%'
		or puntos.cadenamiento like '%$data%';
	|;
	$xpl2_instance_1 -> execute($sql);

	my $row;

	while ($d = $xpl2_instance_1->getrow_hashref) {
		$row.=qq¡{
			"key":"$$d{'id_punto'}",
			"value":"$$d{'nombre'} - $$d{'cadenamiento'}"
		},¡;	
	}
	if($row){
		chop($row);
	}

	print  qq¡ {"u":[$row]} ¡;
}

sub get_points_by_type_and_authorized{
	my $tipo_punto =  $ine{'tipo_punto'};
	my $autorizado = $ine{'autorizado'};
	my $obra = $ine{'obra'};
	my $q = qq¡
		SELECT 
			aca.id_punto,
			aca.tipo_punto,
			aca.nombre_banco,
			aca.radio,
			aca.cadenamiento,
			aca.es_banco_y_tiro,
			aca.latitud,
			aca.longitud,
			aca.autorizado,
			convert(varchar(100),aca.reg_date,103) as fecha_registro,
			convert(varchar(100),aca.reg_date,108) as hora_registro,
			aca.estatus, 
			convert(varchar(100),aca.add_date,103) as fecha_agregado,
			convert(varchar(100),aca.add_date,108) as hora_agregado, 
			aca.add_user, 
			aca.upd_date, 
			aca.upd_user,
			puntos_por_obra.obra
		FROM 
			acarreos.dbo.acarreos_puntos aca inner join 
			acarreos.dbo.acarreos_puntos_por_obra puntos_por_obra on aca.id_punto = puntos_por_obra.id_punto 
		WHERE 
			aca.estatus = 'A' and 
			aca.tipo_punto = $tipo_punto and 
			aca.autorizado = $autorizado and
			puntos_por_obra.obra = '$obra';
	¡;
	$xpl2_instance_1 -> execute($q);

	if($xpl2_instance_1->EOF){
		$content = qq|{"data":false}|;
	} else {

		$content = "\{\"data\": \[";
		while ($d = $xpl2_instance_1->getrow_hashref) {
				$content .= "\{
					\"id_punto\":$$d{'id_punto'},
					\"latitud\":$$d{'latitud'},
					\"longitud\":$$d{'longitud'},
					\"nombre_banco\":\"$$d{'nombre_banco'}\",
					\"cadenamiento\":\"$$d{'cadenamiento'}\",
					\"radio\":$$d{'radio'},
					\"es_banco_y_tiro\":$$d{'es_banco_y_tiro'},
					\"autorizado\":$$d{'autorizado'},
					\"fecha_registro\":\"$$d{'fecha_registro'}\",
					\"hora_registro\":\"$$d{'hora_registro'}\",
					\"fecha_agregado\":\"$$d{'fecha_agregado'}\",
					\"hora_agregado\":\"$$d{'hora_agregado'}\",
					\"add_user\":$$d{'add_user'},
					\"obra\":\"$$d{'obra'}\"
				\},";
		}
		chop($content);
		$content .= "\] \}";
	}
	print $content;
}

sub get_advance_by_date{
	my $obra = $ine{'obra'};

	my $q = qq|
		select distinct id_material, material
		from acarreos.dbo.carries 
		where 
			(cancelado_en_app is null or cancelado_en_app = 0)
			and building_origin = '$obra';
	|;

	$xpl2_instance_1 -> execute($q);

	if($xpl2_instance_1->EOF){
		$content = qq|{"series":false}|;
	} else {

		$plot_json = qq|{"series":[|;

		while ($d = $xpl2_instance_1->getrow_hashref) {

			$plot_json.=qq|{ "name":"$$d{'material'}", "data":[|;

			$q = qq|
				select convert(varchar(100),(CAST(exit_date AS DATE)),103) as fecha, sum(m3) as volumen
				from acarreos.dbo.carries
				where building_origin = '$obra' and id_material = $$d{'id_material'} and cancelado_en_app is null
				group by CAST(exit_date AS DATE)
				order by CAST(exit_date AS DATE) desc
			|;

			$xpl2_instance_2->execute($q);

			if($xpl2_instance_2->EOF){
				$time = time*1000;
				$plot_json.=qq|[$time, 0]|;
			} else {

				while ($r = $xpl2_instance_2->getrow_hashref) {
					my @date_splitted = split /\//, $$r{'fecha'};
					my $time = timelocal(0,0,0,$date_splitted[0],$date_splitted[1]-1,$date_splitted[2])*1000;
					$plot_json.=qq|[$time,$$r{'volumen'}],|;
				}
				chop($plot_json);
			}
			$plot_json .= "\]";
			$plot_json .= "\},";

		}
		chop($plot_json);
		$plot_json .= "\]";
		$plot_json .= "\}";
	}
	print $plot_json;
}

sub get_trips_by_date{
	my $obra = $ine{'obra'};

	$plot_json = qq|{"series":[|;


	$plot_json.=qq|{ "name":"Viajes", "color": "#E3AD04", "data":[|;

	$q = qq|
		select 
			convert(varchar(100),
			(CAST(exit_date AS DATE)),103) as fecha, 
			count(*) as viajes
		from acarreos.dbo.carries
		where building_origin = '$obra' and cancelado_en_app is null
		group by CAST(exit_date AS DATE)
		order by CAST(exit_date AS DATE);
	|;

	$xpl2_instance_2->execute($q);

	if($xpl2_instance_2->EOF){
		$time = time*1000;
		$plot_json.=qq|[$time, 0]|;
	} else {

		while ($r = $xpl2_instance_2->getrow_hashref) {
			my @date_splitted = split /\//, $$r{'fecha'};
			my $time = timelocal(0,0,0,$date_splitted[0],$date_splitted[1]-1,$date_splitted[2])*1000;
			$plot_json.=qq|[$time,$$r{'viajes'}],|;
		}
		chop($plot_json);
	}
	$plot_json .= "\]";
	$plot_json .= "\},";

	
	chop($plot_json);
	$plot_json .= "\]";
	$plot_json .= "\}";
	
	print $plot_json;
}

sub get_trips_by_provider{
	my $obra = $ine{'obra'};

	$q = qq|
		select 
			proveedor,
			count(*) as viajes_x_proveedor
			/*,sum(importe_acarreo)+sum(importe_material) as importe_total*/
		from acarreos.dbo.carries
		where cancelado_en_app is null and building_origin = '$obra'
		group by proveedor
		order by count(*) desc;

	|;

	$xpl2_instance_1->execute($q);

	my $categories = qq|"categories":[|;
	my $data = qq|"data":[|; 

	if($xpl2_instance_1->EOF){
		print qq|{"data":false}|;
	} else {

		while ($r = $xpl2_instance_1->getrow_hashref) {
			$categories .= qq|"$$r{'proveedor'}",|;
			$data .= qq|$$r{'viajes_x_proveedor'},|;
		}

		chop($categories);
		chop($data);

		print qq|
			{"data":
				{
					$categories],
					$data]
				}
			}
			
		|;
	}
}

sub get_money_by_provider{
	my $obra = $ine{'obra'};

	$q = qq|
		select 
			proveedor,
			count(*) as viajes_x_proveedor,
			sum(importe_acarreo)+sum(importe_material) as importe_total
		from acarreos.dbo.carries
		where cancelado_en_app is null and building_origin = '$obra'
		group by proveedor
		order by count(*) desc;

	|;

	$xpl2_instance_1->execute($q);

	my $categories = qq|"categories":[|;
	my $data = qq|"data":[|; 

	if($xpl2_instance_1->EOF){
		print qq|{"data":false}|;
	} else {

		while ($r = $xpl2_instance_1->getrow_hashref) {
			$categories .= qq|"$$r{'proveedor'}",|;
			$data .= qq|$$r{'importe_total'},|;
		}

		chop($categories);
		chop($data);

		print qq|
			{"data":
				{
					$categories],
					$data]
				}
			}
			
		|;
	}
}

sub get_building_advance{
	my $tipo_punto =  $ine{'tipo_punto'};
	my $autorizado = $ine{'autorizado'};
	my $obra = $ine{'obra'};
	my $q = qq¡
		SELECT 
			puntos.id_punto,
			puntos.tipo_punto,
			puntos.nombre_banco,
			puntos.radio,
			puntos.cadenamiento,
			puntos.es_banco_y_tiro,
			puntos.latitud,
			puntos.longitud,
			puntos.autorizado,
			convert(varchar(100),puntos.reg_date,103) as fecha_registro,
			convert(varchar(100),puntos.reg_date,108) as hora_registro,
			puntos.estatus, 
			convert(varchar(100),puntos.add_date,103) as fecha_agregado,
			convert(varchar(100),puntos.add_date,108) as hora_agregado, 
			puntos.add_user, 
			puntos.upd_date, 
			puntos.upd_user,
			avance.destino,
			avance.m3_total,
			avance.material_total,
			avance.acarreo_total
		FROM 
			acarreos.dbo.acarreos_puntos puntos
			left join avance_de_obra_v2 avance on puntos.id_punto = avance.id_point_destiny
		where 
			puntos.id_punto in (select id_punto from acarreos.dbo.acarreos_puntos_por_obra where obra = '$obra') AND
			puntos.estatus = 'A' and
			puntos.autorizado = 1 and
			puntos.tipo_punto = 2
	¡;
	$xpl2_instance_1 -> execute($q);

	if($xpl2_instance_1->EOF){
		$content = qq|{"data":false}|;
	} else {

		$content = "\{\"data\": \[";
		while ($d = $xpl2_instance_1->getrow_hashref) {
				$content .= "\{
					\"id_punto\":$$d{'id_punto'},
					\"tipo_punto\":\"$$d{'tipo_punto'}\",
					\"latitud\":$$d{'latitud'},
					\"longitud\":$$d{'longitud'},
					\"nombre_banco\":\"$$d{'nombre_banco'}\",
					\"cadenamiento\":\"$$d{'cadenamiento'}\",
					\"radio\":$$d{'radio'},
					\"es_banco_y_tiro\":$$d{'es_banco_y_tiro'},
					\"autorizado\":$$d{'autorizado'},
					\"fecha_registro\":\"$$d{'fecha_registro'}\",
					\"hora_registro\":\"$$d{'hora_registro'}\",
					\"fecha_agregado\":\"$$d{'fecha_agregado'}\",
					\"hora_agregado\":\"$$d{'hora_agregado'}\",
					\"add_user\":$$d{'add_user'},
					\"obra\":\"$$d{'obra'}\",
					\"destino\":\"$$d{'destino'}\",
					\"m3_total\":\"$$d{'m3_total'}\",
					\"material_total\":\"$$d{'material_total'}\",
					\"acarreo_total\":\"$$d{'acarreo_total'}\"
				\},";
		}
		chop($content);
		$content .= "\] \}";
	}
	print $content;
}

sub get_low_and_high_volume{
	my $obra = $ine{'obra'};

	my $q = qq|
		select top 1 * 
		from acarreos.dbo.avance_de_obra_v2 
		where building_origin = '$obra'
		order by m3_total desc;
	|;

	my $top_level = 0;
	my $low_level = 0;

	$xpl2_instance_1->execute($q);

	if(!$xpl2_instance_1->EOF){
		$top_level = $xpl2_instance_1->itemvalue('m3_total');
	} 

	$q = qq|
		select top 1 * 
		from acarreos.dbo.avance_de_obra_v2 
		where building_origin = '$obra'
		order by m3_total;
	|;

	$xpl2_instance_1->execute($q);

	if(!$xpl2_instance_1->EOF){
		$low_level = $xpl2_instance_1->itemvalue('m3_total');
	} 


	print qq|
		{"levels":{"top":$top_level,"low":$low_level }}
	|;


}


sub get_dropped_materials_by_point{
	my $id_point = $ine{'id_point'};

	my $q = qq|
		select * FROM
		acarreos.dbo.avance_de_obra_por_material_v2
		where id_point_destiny = $id_point;
	|;

	$xpl2_instance_1->execute($q);

	my $json_return=qq|{"dropped_materials":[|;;

	if(!$xpl2_instance_1->EOF){
		while ($d = $xpl2_instance_1->getrow_hashref) {
			$json_return.=qq|{
				"material":"$$d{'material'}",
				"destino":"$$d{'destino'}",
				"id_material":"$$d{'id_material'}",
				"m3":"$$d{'m3_total'}"
			},|;
		}
		chop($json_return);
		print qq|$json_return]}|;
		return;
	}
	print qq|{"dropped_materials":false}|;
}

sub get_point_by_id{
	my $id_punto =  $ine{'id_punto'};
	my $q = qq¡
		SELECT 
			id_punto,
			tipo_punto,
			nombre_banco,
			radio,
			cadenamiento,
			es_banco_y_tiro,
			latitud,
			longitud,
			autorizado,
			convert(varchar(100),reg_date,103) as fecha_registro,
			convert(varchar(100),reg_date,108) as hora_registro,
			estatus, 
			convert(varchar(100),add_date,103) as fecha_agregado,
			convert(varchar(100),add_date,108) as hora_agregado, 
			add_user, 
			upd_date, 
			upd_user
		FROM 
			acarreos.dbo.acarreos_puntos
		WHERE 
			estatus = 'A' and 
			id_punto = $id_punto;
	¡;
	$xpl2_instance_1 -> execute($q);
	if($xpl2_instance_1->EOF){
		$content = qq|{"data":false}|;
	}else{
		$content = "\{\"data\": \[";
		while ($d = $xpl2_instance_1->getrow_hashref) {
				$content .= "\{
					\"id_punto\":$$d{'id_punto'},
					\"tipo_punto\":$$d{'tipo_punto'},
					\"latitud\":$$d{'latitud'},
					\"longitud\":$$d{'longitud'},
					\"nombre_banco\":\"$$d{'nombre_banco'}\",
					\"cadenamiento\":\"$$d{'cadenamiento'}\",
					\"radio\":$$d{'radio'},
					\"es_banco_y_tiro\":$$d{'es_banco_y_tiro'},
					\"autorizado\":$$d{'autorizado'},
					\"fecha_registro\":\"$$d{'fecha_registro'}\",
					\"hora_registro\":\"$$d{'hora_registro'}\",
					\"fecha_agregado\":\"$$d{'fecha_agregado'}\",
					\"hora_agregado\":\"$$d{'hora_agregado'}\",
					\"add_user\":$$d{'add_user'}
				\},";
		}
		chop($content);
		$content .= "\] \}";
	}
	print $content;
}

sub get_all_active_buildings{

	my $user_session = $ine{'user'};

	my $q = qq|
		SELECT 
			CECOS,
			OBRA,
			SUPERINTENDENTE,
			DESCRIPCION
		from 
			combustible.dbo.obras_navision 
		where 
			((SUPERINTENDENTE = (
				select account 
				from  [EFLOW-WORKFLOW].[workflow].[public].users 
				WHERE id = 
					(select boss 
					from  [EFLOW-WORKFLOW].[workflow].[public].users 
					WHERE id =$user_session)
			) COLLATE Latin1_General_CI_AS ) or 
			($user_session in (
				select eflow.userid_eflow 
				from acarreos.dbo.acarreos_permisos_root root
				left join usuario.dbo.sn_eflow eflow on root.id_supernomina = eflow.super_nomina_id
				where estatus = 'A'
			) 
			))
			and activo = 1
		order by obra desc;

	|;

	$xpl2_instance_1 -> execute($q);



	while ($d = $xpl2_instance_1->getrow_hashref) {
		$content .= qq|{
				"CECOS":"$$d{'CECOS'}",
				"OBRA":"$$d{'OBRA'}",
				"DESCRIPCION":"$$d{'DESCRIPCION'}"
			},|;
	}
	chop($content);

	print qq|{"data":[$content]}|;
}

sub get_buildings_by_filter{
	$nombre_obra=$in{'q'};
	$sql = qq|
			  select top 20 * from [VISE-sq8].[visesiv].[dbo].[vise\$job] 
 				where no_ like '%$nombre_obra%' or [Search Description] like '%$nombre_obra%' or [Description] like '%$nombre_obra%' order by No_;
	|;
	$emp;
	$rpx->execute($sql);

	while ($d = $rpx->getrow_hashref) {
		$emp.=qq¡\{"key":"$$d{'No_'}","CECO":"$$d{'Global Dimension 1 Code'}","Almacen":"$$d{'Global Dimension 2 Code'}","value": "$$d{'No_'} - $$d{'Description'}"\},¡;
	}

	if($emp){
		$emp=substr $emp, 0, (length($emp)-1);
	}
	my $json = qq¡ {"u":[$emp]} ¡;
	print $json;
}

sub save_point{
	my $id_punto = $ine{'id_punto'};
	my $tipo_punto = $ine{'tipo_punto'};
	my $es_banco_y_tiro = $ine{'es_banco_y_tiro'};
	my $nombre_banco = $ine{'nombre_banco'};
	my $cadenamiento = $ine{'cadenamiento'};
	my $radio = $ine{'radio'};
	my $latitud = $ine{'latitud'};
	my $longitud = $ine{'longitud'};

	my $q = qq|

		UPDATE acarreos.dbo.acarreos_puntos
		SET 
			tipo_punto=$tipo_punto,
			nombre_banco='$nombre_banco',
			radio=$radio,
			cadenamiento='$cadenamiento',
			es_banco_y_tiro=$es_banco_y_tiro,
			latitud=$latitud,
			longitud=$longitud,
			autorizado=1,
			upd_date=(getdate()),
			upd_user=$user
		WHERE id_punto = $id_punto;


	|;

	$xpl2_instance_1->execute($q);

	print qq|{"result":true}|;
}

sub get_tickets_by_type {
	my $building = $ine{'building'};
	my $date = $ine{'date'};
	my $ticket_type = $ine{'ticket_type'};

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
		where ticket_type = $ticket_type
			and estatus_tickets = 'A'
			and building = '$building';
	|;

	$xpl2_instance_1->execute($q);

	my $row;

	while ($d = $xpl2_instance_1->getrow_hashref) {
		$row.=qq¡{
			"sheet_number":"$$d{'sheet_number'}",
			"rear_licence_plate":"$$d{'rear_licence_plate'}",
			"increase":$$d{'increase'},
			"capacity":$$d{'capacity'},
			"id_material":"$$d{'id_material'}",
			"material_description":"$$d{'material_description'}",
			"discount":$$d{'discount'},
			"exit_date":"$$d{'exit_date'}",
			"exit_hour":"$$d{'exit_hour'}",
			"point_name_origin":"$$d{'point_name_origin'}",
			"chainage_origin":"$$d{'chainage_origin'}",
			"exit_coordinates":"$$d{'exit_coordinates'}",
			"rear_licence_plate":"$$d{'rear_licence_plate'}",
			"expiration_date":"$$d{'expiration_date'}",
			"expiration_hour":"$$d{'expiration_hour'}"
		},¡;
	}
	if($row){
		chop($row);
	}
	print qq|{"tickets":[$row]}|;

}

sub get_carry_tickets_by_sheet_number {
	my $sheet_number = $ine{'sheet_number'};

	my $q = qq|
		SELECT 
			fecha_salida,
		  	hora_salida, 
		  	folio, 
		  	plates, 
		  	origen, 
		  	destino, 
		  	material, 
		  	m3, 
		  	elaboro, 
		  	fecha_entrega, 
		  	hora_entrega, 
		  	recibio, 
		  	tiempo, 
		  	distance, 
		  	trips, 
		  	building_origin, 
		  	building_destiny, 
		  	exit_date, 
		  	id_point_origin, 
		  	id_material, 
		  	user_id_bank,
		  	importe_acarreo,
		  	importe_material
		FROM acarreos.dbo.carries
		where 1 = 1
		and folio = '$sheet_number'
	|;

	$xpl2_instance_1->execute($q);

	if($xpl2_instance_1->EOF){
		print qq|{"tickets":false}|;
	} else {
		my $row;



		while ($d = $xpl2_instance_1->getrow_hashref) {
			$row.=qq¡{
				"fecha_salida":"$$d{'fecha_salida'}",
				"hora_salida":"$$d{'hora_salida'}",
				"folio":"$$d{'folio'}",
				"plates":"$$d{'plates'}",
				"origen":"$$d{'origen'}",
				"destino":"$$d{'destino'}",
				"material":"$$d{'material'}",
				"m3":$$d{'m3'},
				"elaboro":"$$d{'elaboro'}",
				"fecha_entrega":"$$d{'fecha_entrega'}",
				"hora_entrega":"$$d{'hora_entrega'}",
				"recibio":"$$d{'recibio'}",
				"tiempo":"$$d{'tiempo'}",
				"distancia":"$$d{'distance'}",
				"trips":"$$d{'trips'}",
				"building_origin":"$$d{'building_origin'}",
				"building_destiny":"$$d{'building_destiny'}",
				"exit_date":"$$d{'exit_date'}",
				"id_point_origin":$$d{'id_point_origin'},
				"id_material":$$d{'id_material'},
				"importe_acarreo":"$$d{'importe_acarreo'}",
				"importe_material":"$$d{'importe_material'}"
			},¡;
		}
		if($row){
			chop($row);
		}
		print qq|{"tickets":[$row]}|;
	}
}

=comment
	Para obtener los materiales y su avance en obra.
	regresa un json con los materiales por obra.
=cut

sub get_material_advance{
	my $obra = $ine{'obra'};

	my $q = qq|
		select 
				sum(avance.m3_total) as  m3 ,
				material,
				sum(avance.m3_total) * mat.precio_unitario as costo_material,
				SUM(avance.pu_material_total) as costo_material_x_viaje,
				sum(avance.acarreo_total) as costo_acarreo,
				sum(avance.viajes) as cuantos_viajes,
				SUM(avance.pu_material_total)+sum(avance.acarreo_total) as total_con_material_x_viaje,
				(sum(avance.m3_total) * mat.precio_unitario)+sum(avance.acarreo_total) as total_con_material,
				expl.cantidad AS limite_presupuestado,
				sum(avance.m3_total) * 100 / expl.cantidad as porcentaje_completado
			FROM
				acarreos.dbo.avance_de_obra_por_material_v2 avance 
				left join acarreos.dbo.acarreos_materiales mat on avance.id_material = mat.id_asignacion
				LEFT JOIN acarreos.dbo.explosion_insumos expl on mat.cod_costo = expl.codigo_costo
			where avance.building_origin = '$obra' and (expl.no_obra = '$obra' or mat.cod_costo is null)
		GROUP by avance.material, mat.precio_unitario, expl.cantidad
		order by sum(avance.viajes) desc;
	|;
	$xpl2_instance_1->execute($q);


	if(!$xpl2_instance_1->EOF){
		my $json_return = qq|{"materials":[|;

		while ($d = $xpl2_instance_1->getrow_hashref) {
			$json_return.= qq|
				{
					"material":"$$d{'material'}",
					"m3":"$$d{'m3'}",
					"costo_material":"$$d{'costo_material'}",
					"costo_material_x_viaje":"$$d{'costo_material_x_viaje'}",
					"costo_acarreo":"$$d{'costo_acarreo'}",
					"viajes":"$$d{'cuantos_viajes'}",
					"total_con_material_x_viaje":"$$d{'total_con_material_x_viaje'}",
					"total_con_material":"$$d{'total_con_material'}",
					"limite_presupuestado":"$$d{'limite_presupuestado'}",
					"porcentaje_completado":"$$d{'porcentaje_completado'}"
				},|;
		}
		chop($json_return);

		print qq|$json_return]}|;
		return;
	} else {
		print qq|{"materials":false}|;
		return;
	}
}

sub get_providers_by_building{

	my $obra = $ine{'obra'};

	my $q = qq|
		SELECT 
			DISTINCT tra.proveedor, 
			tra.id_proveedor, 
			tra.id_navision_proveedor
		from acarreos.dbo.camiones_trabajando tra
		where 
		tra.obra_cubicacion ='$obra' 
		and tra.obra_viaje = '$obra';
	|;

	$xpl2_instance_1->execute($q);

	my $json;

	while($d = $xpl2_instance_1->getrow_hashref){
		$json.=qq|{"nombre":"$$d{'proveedor'}","id_proveedor":"$$d{'id_proveedor'}","id_navision_proveedor":"$$d{'id_navision_proveedor'}"},|;
	}

	chop($json);

	$json = qq|{"proveedores":[$json]}|;

	print $json;
}

sub get_material{
	my $to_search=$ine{'q'};
	my $sql = qq|
		select 
			top 20
			mat_acarreos.[No_],
			mat_acarreos.[No_ 2],
			mat_acarreos.[Description],
			mat_acarreos.[Description 2],
			mat_acarreos.[Base Unit of Measure]
		from (SELECT [No_],
			[No_ 2],
			[Description],
			[Description 2],
			[Base Unit of Measure] FROM [VISE-sq8].[visesiv].[dbo].[vise\$item]
			WHERE Blocked=0 and [Inventory Posting Group] ='FLETE Y AC') as mat_acarreos
		where 
			mat_acarreos.[No_] like '%$to_search%' or 
			mat_acarreos.[no_ 2] like '%$to_search%' or 
			mat_acarreos.[Description] like '%$to_search%' or 
			mat_acarreos.[Description 2] like '%$to_search%' ;
	|;
	$emp;
	$rpx->execute($sql);

	while ($d = $rpx->getrow_hashref) {
		$$d{'No_'}=~s/\"//g;
		$$d{'No_ 2'}=~s/\"//g;
		$$d{'Description'}=~s/\"//g;
		$emp.=qq¡\{
				"key":"$$d{'No_'}",
				"no_2":"$$d{'No_ 2'}",
				"description":"$$d{'Description'}",
				"value": "$$d{'No_'} - $$d{'Description'}"
		\},¡;
	}

	if($emp){
		chop($emp);
	}
	my $json = qq¡ {"u":[$emp]} ¡;
	print $json;
}

sub get_user_data_by_eflow_id{

	my $eflow_id = $ine{'eflow_id'};

	my $q = qq|

		SELECT 
			super_nomina_id,
			userid_eflow, name, 
			first_lastname, 
			second_lastname, 
			nameT, 
			ruser_id, 
			account, 
			email, 
			status, 
			depart, 
			ceco, 
			id_boss, 
			fk_id_puesto
		FROM usuario.dbo.sn_eflow 
		where userid_eflow = $eflow_id;
	|;

	$xpl2_instance_1->execute($q);

	if($xpl2_instance_1->EOF){
		print qq|{"user":false}|;
	} else {

		my $super_nomina_id = $xpl2_instance_1->itemvalue('super_nomina_id');
		my $name = $xpl2_instance_1->itemvalue('nameT');
		my $account = $xpl2_instance_1->itemvalue('account');


		print qq|
			{
				"user":{
					"super_nomina_id":$super_nomina_id, "name":"$name","account":"$account"
				}
			}
		|;
	}
}

sub save_materials{
	my $json_materials = decode_json($ine{'data_to_save'});

	my @array = @{$json_materials};
	foreach $a (@array) {
		my $insert_material_sql = qq|
			BEGIN
   				IF NOT EXISTS ( SELECT id_material_navision FROM acarreos.dbo.acarreos_materiales
                   WHERE id_material_navision = '$$a{'id_material'}' and obra = '$$a{'obra'}' and estatus = 'A')
		   		BEGIN
		      		INSERT INTO acarreos.dbo.acarreos_materiales
					(id_material_navision, obra, acronimo_para_tag, add_user, add_date, estatus, precio_unitario)
					VALUES('$$a{'id_material'}', '$$a{'obra'}', 'A', $user, getdate(), 'A',$$a{'precio_unitario'});
		   		END
			END
		|;
		$xpl2_instance_1->execute($insert_material_sql);
	}

	print qq|{"result":true}|;
}

sub reject_point{
	my $id_punto = $ine{'id_punto'};
	my $building = $ine{'building'};
	my $borrar = $ine{'borrar'};

	my $q =$borrar eq 1 ? qq|

		UPDATE acarreos.dbo.acarreos_puntos_por_obra
		SET 
			estatus='B',
			upd_date=(getdate()),
			upd_user=$user
		WHERE id_punto = $id_punto and obra = '$building';

		UPDATE acarreos.dbo.acarreos_puntos
		SET 
			upd_date=(getdate()),
			upd_user=$user
		WHERE id_punto = $id_punto;
	| : 
	qq|

		UPDATE acarreos.dbo.acarreos_puntos
		SET 
			estatus='B',
			upd_date=(getdate()),
			upd_user=$user
		WHERE id_punto = $id_punto;


	|;

	$xpl2_instance_1->execute($q);

	print qq|{"result":true}|;
}

sub get_all_materials{

	my $sql = qq|
		SELECT [No_],
			[No_ 2],
			[Description],
			[Description 2],
			[Base Unit of Measure] FROM [VISE-sq8].[visesiv].[dbo].[vise\$item]
			WHERE [Inventory Posting Group] ='FLETE Y AC' AND Blocked=0;
	|;
	$rpx->execute($sql);

	while ($d = $rpx->getrow_hashref) {
		#regex para eliminar comillas y caracteres no deseados
		$$d{'No_'}=~s/\"//g;
		$$d{'No_ 2'}=~s/\"//g;
		$$d{'Description'}=~s/\"//g;
		$$d{'Description 2'}=~s/\"//g;
		$$d{'No_'}=~s/\'//g;
		$$d{'No_ 2'}=~s/\'//g;
		$$d{'Description'}=~s/\'//g;
		$$d{'Description'}=~s/&nbsp/' '/g;
		$$d{'Description 2'}=~s/\'//g;

		$emp.=qq¡{
				"id_material":"$$d{'No_'}",
				"id_material_2":"$$d{'No_ 2'}",
				"descripcion":"$$d{'Description'}",
				"descripcion_2":"$$d{'Description 2'}",
				"unidad_medida": "$$d{'Base Unit of Measure'}"
		},¡;
	}

	if($emp){
		chop($emp);
	}

	print  qq¡ {"data":[$emp]} ¡;
}

sub get_sheet_numbers{
	my $data = $ine{'q'};
	my $ticket_type = $ine{'ticket_type'} ? qq|and ticket_type=$ine{'ticket_type'}| : "";

	my $sql = qq|
		select distinct sheet_number
		from acarreos.dbo.acarreos_boletos
		where sheet_number like '%$data%'
		$ticket_type;
	|;
	$xpl2_instance_1->execute($sql);
	my $row;

	while ($d = $xpl2_instance_1->getrow_hashref) {
		$row.=qq¡{
			"key":"$$d{'sheet_number'}",
			"value":"$$d{'sheet_number'}"
		},¡;	
	}
	if($row){
		chop($row);
	}

	print  qq¡ {"u":[$row]} ¡;
}

sub get_license_plates{
	my $data = $ine{'q'};

	my $sql = qq|
		select distinct rear_licence_plate
		from acarreos.dbo.acarreos_boletos
		where rear_licence_plate like '%$data%';
	|;
	$xpl2_instance_1->execute($sql);
	my $row;

	while ($d = $xpl2_instance_1->getrow_hashref) {
		$row.=qq¡{
			"key":"$$d{'rear_licence_plate'}",
			"value":"$$d{'rear_licence_plate'}"
		},¡;	
	}
	if($row){
		chop($row);
	}

	print  qq¡ {"u":[$row]} ¡;
}

sub get_material_in_tickets {
	my $data = $ine{'q'};

	my $sql = qq|
		select distinct mat.id_asignacion, material_navision.[No_], material_navision.[Description]
		from acarreos.dbo.acarreos_boletos  aux
			left join acarreos.dbo.acarreos_materiales mat 
				on aux.id_material = mat.id_asignacion
			left join [VISE-SQ8].[visesiv].[dbo].[vise\$item] material_navision 
				on mat.id_material_navision = material_navision.[No_] COLLATE Latin1_General_CI_AS
				where material_navision.[No_] like '%$data%'
				or material_navision.[Description] like '%$data%';
	|;
	$xpl2_instance_1->execute($sql);
	my $row;

	while ($d = $xpl2_instance_1->getrow_hashref) {
		$row.=qq¡{
			"key":"$$d{'id_asignacion'}",
			"value":"$$d{'No_'} - $$d{'Description'}"
		},¡;	
	}
	if($row){
		chop($row);
	}

	print  qq¡ {"u":[$row]} ¡;
}

sub get_user_in_tickets {

	my $data = $ine{'q'};

	my $sql = qq|
		select distinct aux.user_id_bank,usuarios.nameT, usuarios.super_nomina_id 
			from acarreos.dbo.acarreos_boletos aux
			left join usuario.dbo.sn_eflow usuarios 
				on aux.user_id_bank = usuarios.userid_eflow
			where usuarios.super_nomina_id like '%$data%' or usuarios.nameT like '%$data%';
	|;
	$xpl2_instance_1->execute($sql);
	my $row;

	while ($d = $xpl2_instance_1->getrow_hashref) {
		$row.=qq¡{
			"key":"$$d{'user_id_bank'}",
			"value":"$$d{'nameT'} - $$d{'super_nomina_id'}"
		},¡;	
	}
	if($row){
		chop($row);
	}

	print  qq¡ {"u":[$row]} ¡;
}


sub get_materials_by_building{
	my $obra = $ine{'obra'};
	my $punto = $ine{'id_punto'};
	my $q = qq|
		SELECT 
			id_asignacion,
			id_material_navision,
			obra,
			acronimo_para_tag,
			estatus,
			mat_acarreos.[Description],
			(SELECT 
				id_material_por_punto 
			from acarreos.dbo.acarreos_materiales_por_punto 
			where id_punto = $punto and id_material = id_asignacion and estatus = 'A') as id_material_por_punto 
		FROM acarreos.dbo.acarreos_materiales mat_saved JOIN  (SELECT [No_],
					[No_ 2],
					[Description],
					[Description 2],
					[Base Unit of Measure] FROM [VISE-sq8].[visesiv].[dbo].[vise\$item]
					) as mat_acarreos
					 ON mat_acarreos.[No_] = mat_saved.id_material_navision COLLATE Latin1_General_CI_AS 
		where obra = '$obra' and mat_saved.estatus = 'A';
	|;

	$xpl2_instance_1->execute($q);

	if($xpl2_instance_1->EOF){
		$content = qq|{"materials":false}|;
	} else {
		while($d = $xpl2_instance_1->getrow_hashref){

			$$d{'id_material_por_punto'} = $$d{'id_material_por_punto'} ? $$d{'id_material_por_punto'} : 0;

			$json .= qq|{
				"id_asignacion":$$d{'id_asignacion'},
				"id_material_navision":"$$d{'id_material_navision'}",
				"descripcion":"$$d{'Description'}",
				"acronimo_para_tag": "$$d{'acronimo_para_tag'}",
				"id_material_por_punto": $$d{'id_material_por_punto'}
			},|;
		}
		chop($json);		
		$content =  qq|{"materials":[$json]}|;
	}
	print $content;
}

sub remove_distance{
	my $id_distance = $ine{'id_distance'};

	my $q = qq|
		update acarreos.dbo.acarreos_puntos_distancias
		set estatus = 'B', upd_date=(getdate()), upd_user = $user
		where id_distance = $id_distance;
	|;

	$xpl2_instance_1->execute($q);

	print qq|{"success":true}|;

}

sub get_distances_by_point{
	my $punto = $ine{'id_punto'};

	my $q = qq|
		SELECT 
			distancias.id_distance,
			distancias.id_point,
			distancias.distance,
			distancias.add_user,
			distancias.add_date,
			distancias.upd_user,
			distancias.upd_date,
			distancias.estatus,
			distancias.precio_km_inicial,
			distancias.precio_km_subsecuente
		FROM acarreos.dbo.acarreos_puntos_distancias distancias 
		where distancias.estatus='A' and distancias.id_point = $punto;
	|;

	$xpl2_instance_1->execute($q);

	if($xpl2_instance_1->EOF){
		$content = qq|{"distances":false}|;
	} else {
		while($d = $xpl2_instance_1->getrow_hashref){

			my $fee = $$d{'precio_km_inicial'} ? qq|,"precio_km_inicial":$$d{'precio_km_inicial'},"precio_km_subsecuente":$$d{'precio_km_subsecuente'}| : "";
			
			$json .= qq|{
				"id_distance":$$d{'id_distance'},
				"distance":$$d{'distance'}
				$fee
			},|;
		}
		chop($json);		
		$content =  qq|{"distances":[$json]}|;
	}
	print $content;


}


sub get_buildings_by_nomina_id{

	my $nomina_id = $ine{'nomina_id'};

	my $query_buildings=qq|

		SELECT 
			CECOS,
			OBRA,
			SUPERINTENDENTE,
			DESCRIPCION
		from 
			obras_navision 
		where 
			SUPERINTENDENTE = (
				select account 
				from  [EFLOW-WORKFLOW].[workflow].[public].users 
				WHERE id = 
					(select boss 
					from  [EFLOW-WORKFLOW].[workflow].[public].users 
					WHERE id =
						(select userid_eflow 
						FROM usuario.dbo.sn_eflow 
						where super_nomina_id = $nomina_id)
					)
			) COLLATE Latin1_General_CI_AS 
			and activo = 1;
	|;

	$sql_combustible->execute($query_buildings);

	if($sql_combustible->EOF){
		$content = qq|{"data":false}|;
	} else {

		$content = "\{\"data\": \[";

		while ($d = $sql_combustible->getrow_hashref) {
			$content .= "\{
				\"CECOS\":\"$$d{'CECOS'}\",
				\"OBRA\":\"$$d{'OBRA'}\",
				\"DESCRIPCION\":\"$$d{'DESCRIPCION'}\"
			\},";
		}
		chop($content);
		$content .= "\] \}";
	}
	print $content;
}

sub save_distance{
	my $distance_to_add = $ine{'distance_to_add'};
	my $id_point = $ine{'id_point'};
	my $start_km = $ine{'start_km'};
	my $next_km = $ine{'next_km'};

	my $q = qq|
		INSERT INTO acarreos.dbo.acarreos_puntos_distancias
		(id_point, distance, add_user, add_date, estatus, precio_km_inicial, precio_km_subsecuente)
		VALUES($id_point, $distance_to_add, $user, (getdate()), 'A', $start_km, $next_km);
	|;

	$xpl2_instance_1->execute($q);

	print qq|{"success":true}|;
}

sub save_materials_to_point{
	my $json_materials = decode_json($ine{'materiales'});


	my @array = @{$$json_materials{'materials'}};
	my $id_point = $$json_materials{'point'};

	my $q_delete = qq|UPDATE acarreos.dbo.acarreos_materiales_por_punto set estatus = 'B',upd_user=$user, upd_date =(getdate()) where id_punto = $id_point|;

	$xpl2_instance_1->execute($q_delete);

	foreach $a (@array) {
		if($a != 0){ 
			my $q = qq|
				BEGIN
	   				IF NOT EXISTS ( SELECT id_material FROM acarreos.dbo.acarreos_materiales_por_punto
	                   WHERE id_material = $a and id_punto = $id_point and estatus = 'A')
			   		BEGIN
						INSERT INTO acarreos.dbo.acarreos_materiales_por_punto
						(id_material, id_punto, add_user, add_date, estatus)
						VALUES($a, $id_point, $user, getdate(),  'A');
					END
				END
			|;
			$xpl2_instance_1->execute($q);
		}

	}

	print qq|{"success":true}|;
}

sub update_coordinates{

	my $latitud = $ine{'latitud'};
	my $longitud = $ine{'longitud'};
	my $id_punto = $ine{'id_punto'};

	my $q = qq|

		UPDATE acarreos.dbo.acarreos_puntos
		SET 
			latitud=$latitud,
			longitud=$longitud,
			upd_date=(getdate()),
			upd_user=$user
		WHERE id_punto = $id_punto;


	|;

	$xpl2_instance_1->execute($q);

	print qq|{"result":true}|;

}

sub get_reprints{
	my $sheet_number = $ine{'sheet_number'};

	my $q = qq|

		SELECT 
			coordinates,
			convert(varchar(100),
			 add_date,103) as fecha,
			convert(varchar(100),
			 add_date,108) as hora
		FROM acarreos.dbo.acarreos_boletos_reimpresiones
		where estatus='A' and sheet_number = $sheet_number;
	|;

	$xpl2_instance_1->execute($q);

	if($xpl2_instance_1->EOF){
		$content = qq|{"reprints":false}|;
	} else {
		while($d = $xpl2_instance_1->getrow_hashref){
			@coordinates = split /,/,$$d{'coordinates'};
			my $lat = $coordinates[0];
			my $lon = $coordinates[1];
			$json .= qq|{
				"fecha":"$$d{'fecha'}",
				"hora":"$$d{'hora'}",
				"lat":$lat,
				"lon":$lon
			},|;
		}
		chop($json);		
		$content =  qq|{"reprints":[$json]}|;
	}
	print $content;

}

sub get_throw_and_bank_coordinates {

	my $sheet_number = $ine{'sheet_number'};

	my $q = qq|

		SELECT 
			origen_coordenadas,
			destino_coordenadas
		FROM acarreos.dbo.carries
		where 1 = 1
		and folio = '$sheet_number'
	|;

	$xpl2_instance_1->execute($q);

	if($xpl2_instance_1->EOF){
		$content = qq|{"coordinates":false}|;
	} else {
		while($d = $xpl2_instance_1->getrow_hashref){
			$json .= qq|{
				"fecha":"$$d{'fecha'}",
				"hora":"$$d{'hora'}",
				"origen_coordenadas":"$$d{'origen_coordenadas'}",
				"destino_coordenadas":"$$d{'destino_coordenadas'}"
			},|;
		}
		chop($json);		
		$content =  qq|{"coordinates":[$json]}|;
	}
	print $content;
}

sub add_points{
	my $json_points = decode_json($ine{'points_to_add'});
	my $building = $ine{'building'};
	my @array = @{$json_points};
	foreach $a (@array) {

		#insertar puntos 
		my $insert_point_sql = qq|

			INSERT INTO acarreos.dbo.acarreos_puntos
			(tipo_punto, nombre_banco, radio, cadenamiento, es_banco_y_tiro, latitud, longitud, autorizado, reg_date, estatus, add_date, add_user)
			VALUES(
				$$a{'point_type'},
				'$$a{'point_name'}',
				$$a{'radio'},
				'$$a{'chainage'}',
				$$a{'generate_royalty'},
				$$a{'latitude'},
				$$a{'longitude'},
				1, 
				getdate(),
				'A',
				getdate(),
				$user);
			SELECT SCOPE_IDENTITY() as id_inserted;
		   
		|;

		$xpl2_instance_1->execute($insert_point_sql);

		my $inserted_value = $xpl2_instance_1->itemvalue('id_inserted');

		#asignar a la obra los puntos

		my $insert_point_by_building = qq|
			INSERT INTO acarreos.dbo.acarreos_puntos_por_obra
			(id_punto, obra, add_date, add_user, estatus)
			VALUES($inserted_value, '$building', getdate(), $user, 'A');
		|;

		$xpl2_instance_1->execute($insert_point_by_building);

			
		#asignar los materiales a los puntos

		#$q = qq|

		#	SELECT id_asignacion, id_material_navision, obra, acronimo_para_tag, add_user, add_date, upd_user, upd_date, estatus
		#	FROM acarreos.dbo.acarreos_materiales
		#	where obra = '$building' and estatus = 'A';
		#|;

		#$xpl2_instance_1->execute($q);

		#if($xpl2_instance_1->EOF){
		#	print qq|{"result": false}|;
		#} else {
		#	while($d = $xpl2_instance_1->getrow_hashref){
		#			#asigna los materiales
		#			$q = qq|
		#				insert into acarreos.dbo.acarreos_materiales_por_punto(id_material, id_punto,add_user,add_date, estatus)
		#				select $$d{'id_asignacion'}, puntos.id_punto, $user, getdate(),'A'
		#				from acarreos.dbo.acarreos_puntos puntos join acarreos.dbo.acarreos_puntos_por_obra obra
		#				on puntos.id_punto = obra.id_punto
		#				where obra.obra = '$building' and puntos.autorizado = 0 and puntos.tipo_punto = 2;
		#			|;
		#			$xpl2_instance_2->execute($q);
		#	}
		#}

		#asignar las distancias


	}
			print qq|{"result": true}|;


}

sub authorize_all_throws{
	my $building = $ine{'obra'};

	#selecciona los materiales de esa obra

	$q = qq|

		SELECT id_asignacion, id_material_navision, obra, acronimo_para_tag, add_user, add_date, upd_user, upd_date, estatus
		FROM acarreos.dbo.acarreos_materiales
		where obra = '$building' and estatus = 'A';
	|;

	$xpl2_instance_1->execute($q);

	if($xpl2_instance_1->EOF){
		print qq|{"result": false}|;
	} else {
		while($d = $xpl2_instance_1->getrow_hashref){
				#asigna los materiales
				$q = qq|
					insert into acarreos.dbo.acarreos_materiales_por_punto(id_material, id_punto,add_user,add_date, estatus)
					select $$d{'id_asignacion'}, puntos.id_punto, $user, getdate(),'A'
					from acarreos.dbo.acarreos_puntos puntos join acarreos.dbo.acarreos_puntos_por_obra obra
					on puntos.id_punto = obra.id_punto
					where obra.obra = '$building' and puntos.autorizado = 0 and puntos.tipo_punto = 2;
				|;
				$xpl2_instance_2->execute($q);
		}
	}


	#selecciona las distancias de los bancos e inserta para cada uno de los tiros nuevos


	$q = qq|

		SELECT *
		FROM acarreos.dbo.acarreos_puntos_distancias
		where id_point in (select points.id_punto 
		from acarreos.dbo.acarreos_puntos points join acarreos.dbo.acarreos_puntos_por_obra obra
			on points.id_punto = obra.id_punto
		where  points.autorizado = 1 and points.tipo_punto = 1 and obra = '$building' and points.estatus = 'A')
		and estatus = 'A';
	|;

	$xpl2_instance_1->execute($q);

	if($xpl2_instance_1->EOF){
		print qq|{"result": false}|;
	} else {
		while($d = $xpl2_instance_1->getrow_hashref){
				$q = qq|
					insert into acarreos.dbo.acarreos_puntos_distancias(id_point, distance,add_user,add_date, estatus)
					select puntos.id_punto,$$d{'distance'}, $user, getdate(),'A'
					from acarreos.dbo.acarreos_puntos puntos join acarreos.dbo.acarreos_puntos_por_obra obra
					on puntos.id_punto = obra.id_punto
					where obra.obra = '$building' and puntos.autorizado = 0 and puntos.tipo_punto = 2;
				|;
				$xpl2_instance_2->execute($q);
		}
	}


	#autorizalos
	my $q = qq|
	update acarreos.dbo.acarreos_puntos set autorizado = 1, es_banco_y_tiro = 1, upd_date = getdate(), upd_user = $user
	where acarreos.dbo.acarreos_puntos.id_punto in (
		select puntos.id_punto from 
		acarreos.dbo.acarreos_puntos puntos join acarreos.dbo.acarreos_puntos_por_obra obra
		on puntos.id_punto = obra.id_punto
		WHERE obra.obra = '$building') and autorizado = 0 and tipo_punto = 2;

	|;

	$xpl2_instance_1->execute($q);

	print qq|{"result":true}|;

}




sub conectadb {
	if (!$xpl2_instance_1) {
		$xpl2_instance_1 = MSQL_VB->new();
		$xpl2_instance_1->connectdb('acarreos','Vise-XPL2');
	}
	if (!$xpl2_instance_2) {
		$xpl2_instance_2 = MSQL_VB->new();
		$xpl2_instance_2->connectdb('acarreos','Vise-XPL2');
	}
	if(!$sql_combustible){
		$sql_combustible = MSQL_VB->new();
		$sql_combustible->connectdb("combustible","VISE-XPL2");
	}
	if (!$rpx) {
		$rpx = MSQL_VB->new();
		$rpx->connectdb("VISESIV",'Vise-SIV8');
	}
}
