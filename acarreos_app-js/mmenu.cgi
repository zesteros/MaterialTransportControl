#!/usr/bin/perl 

use Eflow::user_check;
use Eflow::jstl;
use Eflow::acceso;
use utf8;
binmode(STDOUT, ':utf8' );

our $img;
$user = user_check(1);
ReadParse();
$user_name=User_Name();

print PrintHeader();
$css = 'menu.css';
if($user == 433){
	#$user=787;
}
# Collect allowed menus
if ($user_permissions & 2**28) {
	$external_user = qq¡
	¡;
} else {
	$internal_user = qq¡
	  <a href="/cgi-bin/eflow/view_process.cgi?help=1"><img src="/images/bullet.gif" align="absMiddle"/> Apoyos</a>
	  <a href="/cgi-bin/eflow/encargos/menu.cgi"><img src="/images/bullet.gif" align="absMiddle"/> Tareas</a>
	¡;
}
if ($EF_USER==24) {
	$menu_caja_hans = qq¡
	  <a href="/cgi-bin/caja/mmenu_hans.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/> Caja Ahorro</a>
	  <a href="/cgi-bin/mercadeo/mmenu.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/> Maq. Externa</a>
	  <a href="/cgi-bin/legal/mmenu.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/> Legal</a>
	  ¡;
}

if (($user_permissions < 0) || ($user_permissions & 24576))  {
  ##Se quita las regalias era el sistema viejo
	##$menu_regalias = qq¡<a href="/cgi-bin/regalias/mmenu.cgi?IE=$STime" target="_self"><img src="/images/bullet.gif" align="absMiddle"/> Regalias</a>¡;
}
if (($user_permissions < 0) || ($user_permissions & 4)) {
	$menu_ventas = qq¡<a href="/cgi-bin/ventas_planta/mmenu.cgi?IE=$STime" target="_self"><img src="/images/bullet.gif" align="absMiddle"/> Ventas Planta</a>¡;
}
my $j=Eflow::jstl->new();

$menu_combustible_new=qq¡<c:ifm(1,$user)><a href="/cgi-bin/Combustible/app/mmenu.cgi?IE=$STime" target="_self"><img src="/images/bullet.gif" align="absMiddle"/> Combustible </a></c:ifm>¡;

$menu_fecha=qq¡<c:ifm(6,$user)><a href="/cgi-bin/util/fechasImportantes/mmenu.cgi?IE=$STime" target="_self"><img src="/images/bullet.gif" align="absMiddle"/>Fechas</a></c:ifm>¡;

$menu_combustible_new=$j->analiza($menu_combustible_new);
$menu_fecha=$j->analiza($menu_fecha);

$menu_proyecto=qq¡<c:ifm(2,$user)><a href="/cgi-bin/projects/mmenu.cgi?IE=$STime" target="_self"><img src="/images/bullet.gif" align="absMiddle"/>Seguridad (Proyectos)</a></c:ifm>¡;
$menu_proyecto=$j->analiza($menu_proyecto);

$menu_presupuesto=qq¡<c:ifm(9,$user)><a href="/cgi-bin/presupuesto/mmenu.cgi?IE=$STime" target="_self"><img src="/images/bullet.gif" align="absMiddle"/> Presupuesto</a></c:ifm>¡;
$menu_presupuesto=$j->analiza($menu_presupuesto);

$menu_almacenLopez=qq¡<c:ifm(12,$user)><a href="/cgi-bin/almacenlopez/mmenu.cgi?IE=$STime" target="_self"><img src="/images/bullet.gif" align="absMiddle"/> Almacén Los López</a></c:ifm>¡;
$menu_almacenLopez=$j->analiza($menu_almacenLopez);

$menu_proveedores_aceeso=qq¡<c:ifm(11,$user)><a href="/cgi-bin/proveedores/mmenu.cgi?IE=$STime" target="_self"><img src="/images/bullet.gif" align="absMiddle"/> Proveedores</a></c:ifm>¡;
$menu_proveedores_aceeso=$j->analiza($menu_proveedores_aceeso);

$_menu_caja = qq¡<c:ifm(10,$user)><a href="/cgi-bin/caja/mmenu.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/> Caja Ahorro</a></c:ifm>¡;
$_menu_caja=$j->analiza($_menu_caja);

$_diario_operacion = qq¡<c:ifm(13,$user)><a href="/cgi-bin/ventas_planta/mmenu2.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/> Diario Operación</a></c:ifm>¡;
$_diario_operacion=$j->analiza($_diario_operacion);

$_diario_operacion2 = qq¡<c:ifm(27,$user)><a href="/cgi-bin/ventas_planta/mmenu3.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/>Diario Operación</a></c:ifm>¡;
$_diario_operacion2 =$j->analiza($_diario_operacion2);

$menu_papeleria= qq¡<c:ifm(14,$user)><a href="/cgi-bin/papeleria/mmenu.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/> Papelería</a></c:ifm>¡;
$menu_papeleria=$j->analiza($menu_papeleria);

$menu_cas= qq¡<c:ifm(15,$user)><a href="/cgi-bin/cas/mmenu.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/> CAS</a></c:ifm>¡;
$menu_cas=$j->analiza($menu_cas);

$menu_casillas= qq¡<c:ifm(16,$user)><a href="/cgi-bin/casillas/mmenu.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/> Cintillas</a></c:ifm>¡;
$menu_casillas=$j->analiza($menu_casillas);

$menu_cedevi= qq¡<c:ifm(17,$user)><a href="/cgi-bin/cedevi/mmenu.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/> CEDEVI</a></c:ifm>¡;
$menu_cedevi=$j->analiza($menu_cedevi);

$menu_mercadeo= qq¡<c:ifm(18,$user)><a href="/cgi-bin/mercadeo/mmenu.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/> Maq. Externa</a></c:ifm>¡;
$menu_mercadeo=$j->analiza($menu_mercadeo);

$menu_consu= qq¡<c:ifm(24,$user)><a href="/cgi-bin/consultorio/mmenu.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/> Consultorio</a></c:ifm>¡;
$menu_consu=$j->analiza($menu_consu);

$menu_digital= qq¡<c:ifm(25,$user)><a href="/cgi-bin/biblioteca/mmenu.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/> Biblioteca Digital</a></c:ifm>¡;
$menu_digital=$j->analiza($menu_digital);

$menu_vacaciones= qq¡<c:ifm(26,$user)><a href="/cgi-bin/vacaciones/mmenu.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/> Vacaciones</a></c:ifm>¡;
#$menu_vacaciones=$j->analiza($menu_vacaciones);

$menu_pruebas_carlos= qq¡<c:ifm(84,$user)><a href="/cgi-bin/pruebas_carlos/mmenu.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/>Pruebas Barron</a></c:ifm>¡;
$menu_pruebas_carlos=$j->analiza($menu_pruebas_carlos);


$menu_obras= qq¡<c:ifm(28,$user)><a href="/cgi-bin/obras/mmenu.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/> Contratos Obras</a></c:ifm>¡;
$menu_obras=$j->analiza($menu_obras);

$menu_concurs= qq¡<c:ifm(29,$user)><a href="/cgi-bin/concurso/mmenu.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/> Concurso</a></c:ifm>¡;
$menu_concurs=$j->analiza($menu_concurs);

$menu_requis= qq¡<c:ifm(30,$user)><a href="/cgi-bin/requisi/mmenu.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/> Requisición</a></c:ifm>¡;
$menu_requis=$j->analiza($menu_requis);

$menu_bi= qq¡<c:ifm(31,$user)><a href="/cgi-bin/BI/mmenu.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/>VI</a></c:ifm>¡;
$menu_bi=$j->analiza($menu_bi);

$menu_seguridad= qq¡<c:ifm(32,$user)><a href="/cgi-bin/seguridad/mmenu.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/>Pruebas Seguridad</a></c:ifm>¡;
$menu_seguridad=$j->analiza($menu_seguridad);

$menu_gps= qq¡<c:ifm(33,$user)><a href="/cgi-bin/gps/mmenu.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/>GPS</a></c:ifm>¡;
$menu_gps=$j->analiza($menu_gps);

$menu_proyti= qq¡<c:ifm(34,$user)><a href="/cgi-bin/proyti/mmenu.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/>Proyectos TI</a></c:ifm>¡;
$menu_proyti=$j->analiza($menu_proyti);

$menu_encu= qq¡<c:ifm(35,$user)><a href="/cgi-bin/encuest/mmenu.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/>Encuestas TI</a></c:ifm>¡;

$menu_serv= qq¡<c:ifm(36,$user)><a href="/cgi-bin/servicios/mmenu.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/>Servicios</a></c:ifm>¡;
$menu_serv=$j->analiza($menu_serv);

$menu_llamadas= qq¡<c:ifm(37,$user)><a href="/cgi-bin/llamadas/mmenu.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/>LLamadas</a></c:ifm>¡;
$menu_llamadas=$j->analiza($menu_llamadas);

$menu_m_falla= qq¡<c:ifm(39,$user)><a href="/cgi-bin/maquif/mmenu.cgi" 
target="_self"><img src="/images/bullet.gif" align="absMiddle"/>Maq. Fallas</a></c:ifm>¡;
$menu_m_falla=$j->analiza($menu_m_falla);

$menu_m_co= qq¡<c:ifm(40,$user)><a href="/cgi-bin/controlobra/mmenu.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/>Control Obra</a></c:ifm>¡;
$menu_m_co=$j->analiza($menu_m_co);

$menu_uni= qq¡<c:ifm(41,$user)><a href="/cgi-bin/uniforme/mmenu.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/>Uniforme</a></c:ifm>¡;
$menu_uni=$j->analiza($menu_uni);

$menu_respaldosti= qq¡<c:ifm(42,$user)><a href="/cgi-bin/respaldosti/mmenu.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/>Respaldos TI</a></c:ifm>¡;
$menu_respaldosti=$j->analiza($menu_respaldosti);

$menu_controlCore= qq¡<c:ifm(43,$user)><a href="/cgi-bin/controlCore/mmenu.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/>Control Core</a></c:ifm>¡;
$menu_controlCore=$j->analiza($menu_controlCore);

$menu_eliejemplo= qq¡<c:ifm(44,$user)><a href="/cgi-bin/eliEjemplo/mmenu.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/>Ejemplo Eli</a></c:ifm>¡;
$menu_eliejemplo=$j->analiza($menu_eliejemplo);

$menu_visita= qq¡<c:ifm(45,$user)><a href="/cgi-bin/visita/mmenu.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/>Visita de Obras</a></c:ifm>¡;
$menu_visita=$j->analiza($menu_visita);

$menu_concurso_privado= qq¡<c:ifm(80,$user)><a href="/cgi-bin/concurso_p/mmenu.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/>Concurso Ventas</a></c:ifm>¡;
$menu_concurso_privado=$j->analiza($menu_concurso_privado);

#$menu_encuesta_gral= qq¡<c:ifm(47,$user)><a href="/cgi-bin/encuesta_gral/mmenu.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/>Encuestas VISE</a></c:ifm>¡;
#$menu_encuesta_gral=$j->analiza($menu_encuesta_gral);

$menu_edo_resul= qq¡<c:ifm(48,$user)><a href="/cgi-bin/resultado/mmenu.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/>Estado de resultado</a></c:ifm>¡;
$menu_edo_resul=$j->analiza($menu_edo_resul);

$menu_organ= qq¡<c:ifm(49,$user)><a href="/cgi-bin/organigrama/mmenu.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/>Organigrama</a></c:ifm>¡;
$menu_organ=$j->analiza($menu_organ);

$menu_regalias_e= qq¡<c:ifm(50,$user)><a href="/cgi-bin/proy_regalias/mmenu.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/>Regalías</a></c:ifm>¡;
$menu_regalias_e=$j->analiza($menu_regalias_e);


$menu_destajo= qq¡<c:ifm(51,$user)><a href="/cgi-bin/destajo/mmenu.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/>Destajo</a></c:ifm>¡;
$menu_destajo=$j->analiza($menu_destajo);

$menu_estadisticas= qq¡<c:ifm(52,$user)><a href="/cgi-bin/indicadores/mmenu2.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/>Indicadores</a></c:ifm>¡;
$menu_estadisticas=$j->analiza($menu_estadisticas);

$menu_minuta= qq¡<a href="/cgi-bin/minuta/mmenu.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/>Minuta</a>¡;

#$menu_encues_vise= qq¡<c:ifm(54,$user)><a href="/cgi-bin/encuestasvise/mmenu.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/>Encuestas gral.</a></c:ifm>¡;
$menu_encues_vise= qq¡<a href="/cgi-bin/encuestasvise/mmenu.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/>ENCUESTAS VISE</a>¡;
#$menu_encues_vise=$j->analiza($menu_encues_vise);

$menu_encue_cubi= qq¡<c:ifm(55,$user)><a href="/cgi-bin/cubicacion/mmenu.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/>Cubicación</a></c:ifm>¡;
$menu_encue_cubi=$j->analiza($menu_encue_cubi);

$menu_almobra= qq¡<c:ifm(57,$user)><a href="/cgi-bin/almacenobra/mmenu.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/>Almacén Obra</a></c:ifm>¡;
$menu_almobra=$j->analiza($menu_almobra);


$menu_inicio= qq¡<c:ifm(62,$user)><a href="/cgi-bin/inicial/mmenu.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/>Inicio</a></c:ifm>¡;
$menu_inicio=$j->analiza($menu_inicio);

$menu_prenomina_che= qq¡<c:ifm(63,$user)><a href="/cgi-bin/prenomina_che/mmenu.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/>Prenómina Che</a></c:ifm>¡;
$menu_prenomina_che=$j->analiza($menu_prenomina_che);

$menu_nasus= qq¡<c:ifm(61,$user)><a href="/cgi-bin/nasus/mmenu.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/>Nasus</a></c:ifm>¡;
$menu_nasus=$j->analiza($menu_nasus);

$menu_msn= qq¡<c:ifm(66,$user)><a href="/cgi-bin/msn/mmenu.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/>MSN</a></c:ifm>¡;
$menu_msn=$j->analiza($menu_msn);

$menu_sol_maqui= qq¡<c:ifm(68,$user)><a href="/cgi-bin/solMaquinaria/mmenu.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/>Sol Maquinaria</a></c:ifm>¡;
$menu_sol_maqui=$j->analiza($menu_sol_maqui);

$menu_arduino_magua = qq¡<c:ifm(71,$user)><a href="/cgi-bin/siloagua/mmenu.cgi"  target="_self"><img src="/images/bullet.gif" align="absMiddle"/>Monitoreo Recursos</a></c:ifm>¡;
$menu_arduino_magua= $j->analiza($menu_arduino_magua);

$menu_proyecto_ti = qq¡<c:ifm(72,$user)><a href="/cgi-bin/proyectos_ti/mmenu.cgi"  target="_self"><img src="/images/bullet.gif" align="absMiddle"/>Proyectos T.I.</a></c:ifm>¡;
$menu_proyecto_ti= $j->analiza($menu_proyecto_ti);

$menu_Obra_J = qq¡<a href="/cgi-bin/obra/mmenu.cgi"  target="_self"><img src="/images/bullet.gif" align="absMiddle"/>OBRA</a>¡;
$menu_Obra_J = $j->analiza($menu_Obra_J);

$menu_rifas = qq¡<c:ifm(78,$user)><a href="/cgi-bin/rifas/mmenu.cgi"  target="_self"><img src="/images/bullet.gif" align="absMiddle"/>Eventos</a></c:ifm>¡;
$menu_rifas = $j->analiza($menu_rifas);

$menu_concesionarias = qq¡<c:ifm(83,$user)><a href="/cgi-bin/project_concesiones/mmenu.cgi"  target="_self"><img src="/images/bullet.gif" align="absMiddle"/>Concursos Concesiones</a></c:ifm>¡;
$menu_concesionarias = $j->analiza($menu_concesionarias);

# pendiente por eleiminar 
if($user eq 568){
    $menu_requis_new= qq¡<c:ifm(30,$user)><a href="/cgi-bin/requisi_new/mmenu.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/> Requisición2</a></c:ifm>¡;
    $menu_requis_new=$j->analiza($menu_requis_new);
}

$menu_requests=qq|<a href="/cgi-bin/project_requests/mmenu.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/> Solicitudes T.I.</a>|;

$menu_carries=qq|<c:ifm(82,$user)><a href="/cgi-bin/acarreos_app/mmenu.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/> Acarreos</a></c:ifm>|;
$menu_carries=$j->analiza($menu_carries);
#jony
$menu_control_obra=qq|<c:ifm(86,$user)><a href="/cgi-bin/project_control_obra/mmenu.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/> Control de Obras Requisiciones</a></c:ifm>|;
$menu_control_obra = $j->analiza($menu_control_obra);


#if($user eq 568){
	#$informacionAplicacion=qq|<c:ifm(64,$user)><a href="/cgi-bin/infomracionEmpledo/mmenu.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/> Informacion Empleado </a></c:ifm>|;
	#$informacionAplicacion=$j->analiza($informacionAplicacion);

#}

$menu_presentacion= qq¡<a href="/cgi-bin/presentacion/mmenu.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/> Presentación</a>¡;

$menu_ipg_pld = qq¡<c:ifm(56,$user)><a href="/cgi-bin/proy_ipg_pld/mmenu.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/>PLD</a></c:ifm>¡;
$menu_ipg_pld =$j->analiza($menu_ipg_pld);


$menu_prueba_jony = qq¡<c:ifm(97,$user)><a href="/cgi-bin/prueba_jony/mmenu.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/>Pruebas Jony</a></c:ifm>¡;
$menu_prueba_jony =$j->analiza($menu_prueba_jony);

$rsp=MSQL_VB->new();
$rsp->connectdb('sistemas','VISE-NAV');
$q=qq¡ SELECT id_almacen, nombre, id_proyecto_acceso FROM dbo.act_almacen a WHERE a.estatus = 'A' ¡;
$rsp->execute($q);
my $todosActivos="";
my $ac=Eflow::acceso->new();
my $accesoActivos=0;
while ($d = $rsp->getrow_hashref) {
	#my $temtodosActivos.= qq¡<c:ifm($$d{'id_proyecto_acceso'},$user)><a href="/cgi-bin/activos/mmenu.cgi?id_almacen=$$d{'id_almacen'}" target="_self"><img src="/images/bullet.gif" align="absMiddle"/> $$d{'nombre'}</a></c:ifm>¡;
	#$todosActivos.=$j->analiza($temtodosActivos);	
	my $ver=$ac->m($$d{'id_proyecto_acceso'},$user);
	if($ver){
		$todosActivos.=qq¡<a href="/cgi-bin/activos/mmenu.cgi?id_almacen=$$d{'id_almacen'}" target="_self"><img src="/images/bullet.gif" align="absMiddle"/> Activos Vise</a>¡;
		$accesoActivos=1;
		last;
	}
}
if(!$accesoActivos){
	$todosActivos.=qq¡<a href="/cgi-bin/activos/mmenu.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/> Activos Vise</a>¡;
}

if (($user_permissions < 0) || ($user_permissions & 16) ) {
	#$menu_souvenirs = qq¡<a href="/cgi-bin/souvenirs/mmenu.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/> Souvenirs</a>¡;
}
if (($user_permissions < 0) || ($user_permissions & 128) ) {
	$menu_prenomina = qq¡<a href="/cgi-bin/prenomina/mmenu.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/> Prenomina</a>¡;
}
if (($user_permissions < 0) || ($user_permissions & 32) ) {
	$inmob = qq¡<a href="/cgi-bin/Inmobiliaria/contratos/mmenu.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/> Inmobiliaria</a>¡;
}
#if (($user_permissions < 0) || ($user_permissions & 131072)) {
if($user==433){
	$menu_reembolsos = qq¡<a href="/cgi-bin/reembolsos/mmenu.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/> Reembolsos</a>¡;
}

if (($user_permissions < 0) || ($user_permissions & 1024)) {
	$menu_presupuestal = qq¡<a href="/cgi-bin/control_presupuestal/mmenu.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/>Control Presupuestal</a>¡;
}
if (($user_permissions < 0) || ($user_permissions & 256)) {
	$menu_relaciones = qq¡<a href="/cgi-bin/relacion_cliente/mmenu.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/> Relaciones</a>¡;
}

if (($user_permissions < 0) || ($user_permissions & 256)) {
	$menu_relaciones = qq¡<a href="/cgi-bin/legal/mmenu.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/>Legal Grupo</a>¡;
}


#grupo de equipo menor
if (($user_permissions < 0) || ($user_permissions & 40960)) {
	#$menu_combustible = qq¡<a href="/cgi-bin/Combustible/mmenu.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/> Combustible</a>¡;
}

#NEW REEMBOLSOS
$menu_reembolsos_e= qq¡<c:ifm(65,$user)><a href="/cgi-bin/proy_reembolsos/mmenu.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/>Reembolsos New</a></c:ifm>¡;
$menu_reembolsos_e=$j->analiza($menu_reembolsos_e);

#EQUIPO MENOR
$menu_equipo_menor= qq¡<c:ifm(69,$user)><a href="/cgi-bin/proy_equipo_menor/mmenu.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/>Equipo menor</a></c:ifm>¡;
$menu_equipo_menor=$j->analiza($menu_equipo_menor);

#MTTO CORRECTIVO MAQUINARIA
$menu_mtto_correctivo_maq= qq¡<c:ifm(81,$user)><a href="/cgi-bin/proy_mtto_correctivo_maq/mmenu.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/>Mtto correctivo maq</a></c:ifm>¡;
$menu_mtto_correctivo_maq=$j->analiza($menu_mtto_correctivo_maq);

#REGALIAS V2
$menu_regaliasv2= qq¡<c:ifm(85,$user)><a href="/cgi-bin/proy_regaliasv2/mmenu.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/>RegaliasV2</a></c:ifm>¡;
$menu_regaliasv2=$j->analiza($menu_regaliasv2);

$menu_cursos_capacitacion= qq¡<c:ifm(88,$user)><a href="/cgi-bin/cursos_capacitacion/mmenu.cgi" target="_self"><img src="/images/bullet.gif" align="absMiddle"/>Cursos Capacitación</a></c:ifm>¡;
$menu_cursos_capacitacion=$j->analiza($menu_cursos_capacitacion);

display(5);
display(6);
display(7);
display(8);
if (-e "$EF_CGI/bsc") {
	display(9);
}

if (-e "$EF_WEB/images/logo$EF.$EXT") {
	$img = "logo$EF.$EXT";
	$w = "width='100'";
} else {
	$img = "logo_eflow.jpg";
	$w = "width='80'";
	$h = "height='10'";
}

if($user==433 or $user==507 or $user==568 or $user==308 or $user==871 or $user == 480){
	$curso_temp =qq¡
		<div class="collapsed">
		<span><small>Ambiente prueba</small></span>
		<a href="/cgi-bin/cursos/echavez/mmenu.cgi" target="_self">- Curso Ernesto</a>
		<a href="/cgi-bin/cursos/esanchez/mmenu.cgi" target="_self">- Curso Ely</a>
		<a href="/cgi-bin/cursos/jazz/mmenu.cgi" target="_self">- Curso Bertha</a>
		<a href="/cgi-bin/cursos/fernando/mmenu.cgi" target="_self">- Curso Fernando</a>
		<a href="/cgi-bin/cursos/ces/mmenu.cgi" target="_self">- Curso Cesar</a>
		<a href="/cgi-bin/inicial/mmenu.cgi" target="_self">- Pruebas Angelo</a>
     $menu_requis_new
		</div>
	¡;
}

print qq¡
  <html>
  <head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
  <link rel="stylesheet" type="text/css" href="/css/menu.css" />
  <script type="text/javascript" src="/sdmenu/sdmenu.js"></script>
  <script type="text/javascript" language="JavaScript" src="/lib/js/jquery.min.js?v=1"></script>
  <script>var j = jQuery.noConflict();
    j( document ).ready(function() {
      j("#divAplicaciones a").attr("class","findEndAplicaciones");
      j("#inputFindAplicaciones").keyup(function(e){
      if(j(this).val()==''){
        j(".findEndAplicaciones").show();
      }else{
        j(".findEndAplicaciones").hide();
        j(".findEndAplicaciones").filter(function( index ) {
          //console.info(j(this).text() + j(this).text().toLowerCase() == "VI".toLowerCase());
          var string = j(this).text().toLowerCase(),
          substring = j("#inputFindAplicaciones").val().toLowerCase();
          var valCons = string.includes(substring);
          return  string==substring ||  valCons;
        }).show();
      }
      });
    });
    </script>
    <style>
    .inputTextFilter:focus {
        border: 3px solid #555;
    }
    .inputTextFilter{
      width: 100%;
      padding: 5px 10px;
      margin: 8px 0;
      box-sizing: border-box;
      border: 2px solid #1671AE;
      border-radius: 4px;
    }

    </style>
  <title>menu</title>
  <base target="work"></base>
  <script language="JavaScript">
  var myMenu;
    function iniciamenu() {
	myMenu = new SDMenu("my_menu");
	myMenu.speed = 5;
	myMenu.oneSmOnly = false;
	myMenu.markCurrent = true;
	myMenu.init();
  };
  </script>
  </head>
  <body bgcolor="#FFFFFF" onload="javascript: iniciamenu();">
  <table border="0" width="133" cellspacing="0" cellpadding="2" bgcolor="#ffffff" style="left: 0px; position: absolute; top: 0px;">
  <tr>
  <p align="left"><td colspan='2' height="30"><img src="/images/logo_vise1.jpg"></td></p>

  </tr>
  <tr>
  </table>
  <div style="float: left;left: 2px; position: absolute; top: 60px" id="my_menu" class="sdmenu">
  <div><span><img src="/images/Fabrica.$EXT" align="absMiddle"> Procesos</span>
  <a href="/cgi-bin/eflow/admin/menu_add_folio.cgi"><img src="/images/bullet.gif" align="absMiddle"/> Alta folio</a>
  <a href="/cgi-bin/eflow/view_process.cgi"><img src="/images/bullet.gif" align="absMiddle"/> Seguimientos</a>
  $external_user
  $internal_user
  $menu8
  </div>
  $menuBSC
  $menu5
  $menu7
  $menu6
  <div id="divAplicaciones" class="collapsed"><span><img src="/images/pcg.$EXT" align="absMiddle"> Aplicaciones</span>
  <div><input type="text" class="inputTextFilter" id="inputFindAplicaciones" placeholder="Buscar Aplicación" autocomplete="off"/> </div>
  $menu_caja_hans
  $menu_reembolsos
  $_menu_caja
  $menu_regalias
  $menu_ventas
  $menu_souvenirs
  $inmob
  $menu_prenomina
  $menu_presupuestal
  $menu_relaciones
  $menu_combustible
  $menu_combustible_new
  $menu_fecha
  $menu_proyecto
  $menu_presupuesto
  $menu_proveedores_aceeso
  $menu_almacenLopez
  $_diario_operacion
  $_diario_operacion2
  $menu_papeleria
  $menu_cas
  $menu_casillas
  $menu_cedevi
  $menu_mercadeo
  $todosActivos
  $menu_consu
  $menu_digital
  $menu_vacaciones
  $menu_obras
  $menu_concurs
  $menu_requis
  $menu_bi
  $menu_seguridad
  $menu_proyti
  $menu_gps
  $menu_encu
  $menu_serv
  $menu_llamadas
  $menu_m_falla
  $menu_m_co
  $menu_uni
  $menu_respaldosti
  $menu_controlCore
  $menu_eliejemplo
  $menu_visita
  $menu_concurso_privado
  $menu_encuesta_gral
  $menu_edo_resul
  $menu_organ
  $menu_regalias_e
  $menu_destajo
  $menu_encues_vise
  $menu_estadisticas
  $menu_minuta
  $menu_encue_cubi
  $menu_pld
  $menu_almobra
  $menu_inicio
  $menu_nasus
  $menu_ipg_pld
  $menu_prenomina_che
  $informacionAplicacion
  $menu_reembolsos_e
  $menu_mtto_correctivo_maq
  $menu_msn
  $menu_sol_maqui
  $menu_equipo_menor
  $menu_regaliasv2
  $menu_arduino_magua
  $menu_proyecto_ti
  $menu_requests
  $menu_Obra_J
  $menu_rifas
  $menu_concesionarias
  $menu_carries
  $menu_pruebas_carlos
  $menu_control_obra
  $menu_cursos_capacitacion
  $menu_prueba_jony
   </div>
  $curso_temp
  <div><span><img src="/images/UserAccounts.$EXT" align="absMiddle"> Login</span>
  <a href="#" onclick="parent.work.location.href = 'https://tickets.vise.com.mx/login.php';"><img align="absMiddle" src="/images/bullet.gif"  />Mesa Ayuda(Tickets)</a>
  <a target="_blank" href="https://oportunidad.vise.com.mx/" ><img align="absMiddle" src="/images/bullet.gif" />Oportunidad de Obra</a>
  <a href="/cgi-bin/eflow/admin/chg_passwd.cgi"><img align="absMiddle" src="/images/bullet.gif" /> Configuración</a>
  <a id="a_salir" href="/cgi/eflow/logout.cgi?user=$user&EF=$EF" target="_parent" onclick='parent.topf.document.MyForm.Forced.value=0;'><img align="absMiddle" src="/images/logoff.$EXT" /> Salir</a>
  </div>
  </div>
  </body>
  </html>
¡;

if ($EF) {
	my $repdir = "${EF_ROOT}_reports$EF";
	my $admindir = "${EF_ROOT}_admins$EF";
	
	if (!-d $repdir) {
		system "mkdir $repdir";
		system "chmod 755 $repdir";
		system "cp ${EF_ROOT}_reports/menu.cgi $repdir";
	}
	if (!-d $admindir) {
		system "mkdir $admindir";
		system "chmod 755 $admindir";
		system "cp ${EF_ROOT}_admins/menu.cgi $admindir";
	}
}

sub display {
	my $which = shift;

	# No access to this section to any user marked as external.
	if ($user_permissions & 2**28) {
		return 0;
	}
	if ($which == 5) {
		$dir_report="";
		if (($user_permissions < 0) || ($user_permissions & 2**30)) {
		    $dir_report = qq¡
			  <a href="/cgi-bin/eflow/reports/bottle_neck.cgi"><img src="/images/bullet.gif" align="absMiddle"/> Procesos</a>
			¡;
		}
		$menu5 = qq¡
		  <div class="collapsed"><span><img src="/images/Chart.$EXT" align="absMiddle"> Reportes</span>
		  <a href="/cgi-bin/eflow_reports/menu.cgi"><img src="/images/bullet.gif" align="absMiddle"/> Empresa</a>
		  $dir_report
		  <a href="/cgi-bin/bsc/reports/edoc.cgi"><img src="/images/bullet.gif" align="absMiddle"/> Medidores</a>
		  <a href="/cgi-bin/eflow/reports/folio_report.cgi"><img src="/images/bullet.gif" align="absMiddle"/> Folios</a>
		  <a href="/cgi-bin/eflow/reports/find_folio.cgi"><img src="/images/bullet.gif" align="absMiddle"/> Buscar</a>
		  </div>
		¡;
	}
	if ($which == 6) {
		if ($user_permissions < 0) {
			$menu6 = qq¡
			  <div class="collapsed"><span><img src="/images/config.$EXT" align="absMiddle"> Admin</span>
			  <a href="/cgi-bin/eflow/admin/delete_all_folios.cgi"><img src="/images/bullet.gif" align="absMiddle"/> Reiniciar</a>
			  <a href="/cgi-bin/designer/users.cgi"><img src="/images/bullet.gif" align="absMiddle"/> Usuarios</a>
			  <a href="/cgi-bin/eflow/admin/folios.cgi"><img src="/images/bullet.gif" align="absMiddle"/> Folios</a>
			  <a href="/cgi-bin/eflow/admin/date.cgi"><img src="/images/bullet.gif" align="absMiddle"/> Fecha/Hora</a>
			  <a href="/cgi-bin/eflow/admin/backup.cgi"><img src="/images/bullet.gif" align="absMiddle"/> Respaldar Ahora !</a>
			  <a href="/cgi-bin/eflow/admin/install.cgi"><img src="/images/bullet.gif" align="absMiddle"/> Actualizaciones</a>
			  </div>
			¡;
#			  <a href="/cgi-bin/eflow/admin/style.cgi"><img src="/images/bullet.gif" align="absMiddle"/> Diseño</a>
		}
	}
    if ($which == 7) {
		if (($user_permissions < 0) || ($user_permissions & 2**29)) {
			$menu7 = qq¡
			  <div class="collapsed"><span><img src="/images/config2.$EXT" align="absMiddle"> Administrar</span>
			  <a href="/cgi-bin/eflow_admins/menu.cgi"><img border='0' src="/images/bullet.gif" /> Procesos</a>
			  </div>
			¡;
		}
	}
    if ($which == 8) {
		if ($user_permissions & 2**27) {
			$menu8 = qq¡
			  <a href="/cgi-bin/designer/mmenu.cgi" target="_self"><img src="/images/bullet.gif" /> Diseñador</a>
			¡;
		}
	}
	if ($which == 9) {
        if ($user_permissions & 2**27) {
			$BSCdesigner = qq¡
			  <a href="/cgi-bin/bsc/designer/mmenu.cgi" target="_self" onclick="javascript: mark_selection(this);"><img src="/images/bullet.gif" /> Diseñador</a>
			  ¡;
		}
        if (($user_permissions < 0) || ($user_permissions & 2**26)) {
			$BSCAdmin = qq¡
			  <a href="/cgi-bin/bsc/admin.cgi" onclick="javascript: mark_selection(this);"><img src="/images/bullet.gif" /> Administración</a>
			  ¡;
		}
        $menuBSC = qq¡
          <div class="collapsed"><span><img src="/images/puzzle_yellow18.$EXT" align="absMiddle"> Medidores</span>
          <a href="/cgi-bin/bsc/display.cgi" onclick="javascript: mark_selection(this);"><img src="/images/bullet.gif" align="absMiddle"/> Seguimientos</a>
          <a href="/cgi-bin/bsc/dashboards.cgi" onclick="javascript: mark_selection(this);"><img src="/images/bullet.gif" align="absMiddle"/> Tableros</a>
          $BSCAdmin
          $BSCdesigner
          </div>
          ¡;
    }
}

__END__
SOFTWARE PROTEGIDO POR DERECHOS DE AUTOR
ARTICULOS 13,162,163 FRACCION I, 164 FRACCION 168,169,209 FRACCION III
INSCRITA BAJO REGISTRO: 03-2002-111414354400-01
SE PROHIBE EL USO, COPIA PARCIAL O TOTAL DE ESTE CODIGO SIN EL DEBIDO CONTRATO
DE LICENCIAMIENTO DE MICROFLOW SOFTWARE SA DE CV
PASEO DE LAS GARZAS 101, JARDINES DEL LAGO, AGS. 449-9760940
email:soporte@eflow.com.mx
