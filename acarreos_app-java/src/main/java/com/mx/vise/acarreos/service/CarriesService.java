package com.mx.vise.acarreos.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder.In;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mx.vise.acarreos.pojos.CancelTicketPOJO;
import com.mx.vise.acarreos.pojos.RequestDeviceRegisterPOJO;
import com.mx.vise.acarreos.pojos.carries.DistancePOJO;
import com.mx.vise.acarreos.pojos.carries.KeyPOJO;
import com.mx.vise.acarreos.pojos.carries.MaterialsByPointPOJO;
import com.mx.vise.acarreos.pojos.carries.MaterialsPOJO;
import com.mx.vise.acarreos.pojos.carries.PointPOJO;
import com.mx.vise.acarreos.pojos.carries.ReprintPOJO;
import com.mx.vise.acarreos.pojos.carries.SyncPOJO;
import com.mx.vise.acarreos.pojos.carries.SyncRequestPOJO;
import com.mx.vise.acarreos.pojos.carries.SyncResponsePOJO;
import com.mx.vise.acarreos.pojos.carries.TicketPOJO;
import com.mx.vise.nav.sistemas.activos.dao.ActActivoDao;
import com.mx.vise.sq8.siv.dao.VISEItemDao;
import com.mx.vise.sq8.siv.model.VISEItem;
import com.mx.vise.util.BussinessException;
import com.mx.vise.xpl2.acarreos.dao.AcarreosBoletosDao;
import com.mx.vise.xpl2.acarreos.dao.AcarreosBoletosReimpresionesDao;
import com.mx.vise.xpl2.acarreos.dao.AcarreosLavesDao;
import com.mx.vise.xpl2.acarreos.dao.AcarreosMaterialesDao;
import com.mx.vise.xpl2.acarreos.dao.AcarreosMaterialesPorPuntoDao;
import com.mx.vise.xpl2.acarreos.dao.AcarreosPuntosDao;
import com.mx.vise.xpl2.acarreos.dao.AcarreosPuntosDistanciasDao;
import com.mx.vise.xpl2.acarreos.dao.AcarreosPuntosPorObraDao;
import com.mx.vise.xpl2.acarreos.dao.AcarreosUsuariosDao;
import com.mx.vise.xpl2.acarreos.dao.AcarreosUsuariosSincronizacionesDao;
import com.mx.vise.xpl2.acarreos.model.AcarreosBoletos;
import com.mx.vise.xpl2.acarreos.model.AcarreosBoletosReimpresiones;
import com.mx.vise.xpl2.acarreos.model.AcarreosLlaves;
import com.mx.vise.xpl2.acarreos.model.AcarreosMateriales;
import com.mx.vise.xpl2.acarreos.model.AcarreosMaterialesPorPunto;
import com.mx.vise.xpl2.acarreos.model.AcarreosPuntos;
import com.mx.vise.xpl2.acarreos.model.AcarreosPuntosDistancias;
import com.mx.vise.xpl2.acarreos.model.AcarreosPuntosPorObra;
import com.mx.vise.xpl2.acarreos.model.AcarreosUsuariosSincronizaciones;

/**
 * @author Angelo
 *
 */
@Service
public class CarriesService {
	private static final Logger LOGGER = LogManager.getLogger(CarriesService.class.getName());

	@Autowired
	private AcarreosPuntosDao puntosDao;
	@Autowired
	private AcarreosPuntosPorObraDao puntosPorObraDao;
	@Autowired
	private AcarreosMaterialesDao materialesDao;
	@Autowired
	private AcarreosMaterialesPorPuntoDao materialesPorPuntoDao;
	@Autowired
	private VISEItemDao itemDao;
	@Autowired
	private AcarreosBoletosDao boletosDao;
	@Autowired
	private ActActivoDao activoDao;
	@Autowired
	private AcarreosPuntosDistanciasDao distanciasDao;
	@Autowired
	private AcarreosUsuariosSincronizacionesDao syncDao;
	@Autowired
	private AcarreosBoletosReimpresionesDao reprintDao;
	@Autowired
	private AcarreosLavesDao llavesDao;

	/**
	 * @param pointPojo el punto en pojo
	 * @return el id del punto que se agregó
	 */
	public int addNewPoint(PointPOJO pointPojo) {

		puntosDao.insert(parseToEntity(pointPojo));

		Integer lastPoint = puntosDao.getLastItem();

		AcarreosPuntosPorObra acarreosPuntosPorObra = new AcarreosPuntosPorObra();
		acarreosPuntosPorObra.setAddDate(new Date());
		acarreosPuntosPorObra.setAddUser(pointPojo.getAddUser());
		acarreosPuntosPorObra.setEstatus("A");
		acarreosPuntosPorObra.setIdPunto(lastPoint);
		acarreosPuntosPorObra.setObra(pointPojo.getObra());

		puntosPorObraDao.insert(acarreosPuntosPorObra);

		return lastPoint;
	}

	public int addNewTicket(TicketPOJO ticketPojo) {
		AcarreosBoletos ticket = parseTicketPOJOToEntity(ticketPojo);
		Integer lastId = null;
		try {
			boletosDao.insert(ticket);
			lastId = boletosDao.getLastItem();
		} catch (BussinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return lastId;
		}
		return lastId;
	}

	private AcarreosBoletos parseTicketPOJOToEntity(TicketPOJO ticketPojo) {

		AcarreosBoletos ticket = new AcarreosBoletos();
		ticket.setBuilding(ticketPojo.getBuilding());
		ticket.setTicketType(ticketPojo.getTicketType());

		if (ticketPojo.getSheetNumber() != null)
			ticket.setSheetNumber(ticketPojo.getSheetNumber());

		ticket.setRearLicencePlate(ticketPojo.getRearLicensePlate());
		ticket.setIncrease(ticketPojo.getIncrease());
		ticket.setCapacity(ticketPojo.getCapacity());
		ticket.setIdMaterial(ticketPojo.getMaterial().getIdMaterialServer());
		ticket.setExitDate(ticketPojo.getExitDate());
		if(ticketPojo.getUserIdBank() != null)
			ticket.setUserIdBank(ticketPojo.getUserIdBank());
		ticket.setIdPointOrigin(ticketPojo.getOrigin().getIdPuntoServer());
		ticket.setExitCoordinates(
				ticketPojo.getExitCoordinates().latitude + "," + ticketPojo.getExitCoordinates().longitude);
		ticket.setDiscount(ticketPojo.getDiscount());
		// base
		if (ticketPojo.getUnitOfMeasure() != null)
			ticket.setUnitOfMeasure(ticketPojo.getUnitOfMeasure());
		ticket.setAddDate(new Date());
		ticket.setEstatus("A");
		ticket.setAddUser(ticketPojo.getAddUser());
		// improvement type
		if (ticketPojo.getDistance() != null)
			ticket.setDistance(ticketPojo.getDistance());
		if (ticketPojo.getDestiny() != null)
			ticket.setIdPointDestiny(ticketPojo.getDestiny().getIdPuntoServer());
		// carries type
		if (ticketPojo.getArrivalDate() != null)
			ticket.setArrivalDate(ticketPojo.getArrivalDate());
		if (ticketPojo.getUserIdThrow() != null)
			ticket.setIdUserThrow(ticketPojo.getUserIdThrow());
		if (ticketPojo.getArrivalCoordinates() != null)
			ticket.setArrivalCoordinates(
					ticketPojo.getArrivalCoordinates().latitude + "," + ticketPojo.getArrivalCoordinates().longitude);
		if (ticketPojo.getExpirationDate() != null)
			ticket.setExpirationDate(ticketPojo.getExpirationDate());

		return ticket;
	}

	/**
	 * @param points los puntos a extraer
	 * @return la lista de ids
	 */
	private List<Integer> getPointOnlyIds(List<PointPOJO> points) {

		List<Integer> ids = new ArrayList<>();

		for (PointPOJO point : points)
			ids.add(point.getIdPuntoServer());

		return ids;
	}

	/**
	 * @param pointsAlreadyRegistered los puntos ya registrados
	 * @return el pojo con la lista de puntos que no estan en la terminal y la lista
	 *         de puntos que cambiaron
	 */
	@Transactional(value = "transactionManagerAcarreos",rollbackFor = RuntimeException.class)
	public SyncResponsePOJO getPendingData(SyncRequestPOJO actualData) {

		SyncResponsePOJO responsePojo = new SyncResponsePOJO();

		try {
			responsePojo.setNewPoints(parsePointEntityListToPojoList(
					puntosDao.getPendingPoints(getPointOnlyIds(actualData.getPointsAlreadyRegistered()), actualData.getBuilding()),actualData.getBuilding()));
			responsePojo.setPointsChanged(getChangedPoints(actualData.getPointsAlreadyRegistered(),actualData.getBuilding()));
			responsePojo.setNewMaterials(parseMaterialEntityListToPojoList(
					materialesDao.getPendingMaterials(getMaterialOnlyIds(actualData.getMaterialsAlreadyRegistered()), actualData.getBuilding())));
			responsePojo.setMaterialsChanged(getChangedMaterials(actualData.getMaterialsAlreadyRegistered()));
			responsePojo.setNewMaterialsByPoint(
					parseMaterialsByPointEntityListToPojoList(materialesPorPuntoDao.getPendingMaterialsByPoint(
							getMaterialByPointOnlyIds(actualData.getMaterialsByPointRegistered()), actualData.getBuilding())));

			responsePojo
					.setMaterialsByPointChanged(getChangedMaterialsByPoint(actualData.getMaterialsByPointRegistered()));

			responsePojo.setTags(activoDao.getTags());

			responsePojo.setNewDistances(parseDistanceEntityListToPojoList(
					distanciasDao.getDistances(getDistanceOnlyIds(actualData.getDistancesRegistered()), actualData.getBuilding())));
			responsePojo.setDistancesChanged(getChangedDistances(actualData.getDistancesRegistered()));

			responsePojo.setNewKeys(getNewKeys(actualData.getKeysRegistered()));
			responsePojo.setKeysChanged(getKeysChanged(actualData.getKeysRegistered()));

			registerSync(actualData.getSyncData());

		} catch (BussinessException e) {
			e.printStackTrace();
		}

		return responsePojo;
	}
	@Transactional(value = "transactionManagerAcarreos",rollbackFor = RuntimeException.class)
	private ArrayList<KeyPOJO> getKeysChanged(List<KeyPOJO> keysRegistered) {
		ArrayList<KeyPOJO> keys = new ArrayList<KeyPOJO>();
		keys.addAll(convertKeyEntityToPojo(llavesDao.getUpdatedLlaves(convertKeyPojoToEntity(keysRegistered))));
		return keys;
	}

	private List<AcarreosLlaves> convertKeyPojoToEntity(List<KeyPOJO> pojos) {
		List<AcarreosLlaves> entities = new ArrayList<AcarreosLlaves>();
		for (KeyPOJO pojo : pojos) {
			AcarreosLlaves entity = new AcarreosLlaves();
			entity.setAddDate(pojo.getAddDate());
			entity.setAddUser(pojo.getAddUser());
			entity.setEstatus(pojo.getEstatus());
			entity.setIdKey(pojo.getIdKeyServer());
			entity.setKeyA(pojo.getKeyA());
			entity.setKeyB(pojo.getKeyB());
			entity.setSector(pojo.getSector());
			entity.setUpdDate(pojo.getUpdDate());
			entity.setUpdUser(pojo.getUpdUser());
			entity.setVersion(pojo.getVersion());
			entities.add(entity);
		}
		return entities;
	}

	private ArrayList<KeyPOJO> convertKeyEntityToPojo(List<AcarreosLlaves> entities) {
		ArrayList<KeyPOJO> pojos = new ArrayList<KeyPOJO>();
		for (AcarreosLlaves entity : entities) {
			KeyPOJO pojo = new KeyPOJO();
			pojo.setAddDate(entity.getAddDate());
			pojo.setAddUser(entity.getAddUser());
			pojo.setEstatus(entity.getEstatus());
			pojo.setIdKeyServer(entity.getIdKey());
			pojo.setKeyA(entity.getKeyA());
			pojo.setKeyB(entity.getKeyB());
			pojo.setSector(entity.getSector());
			pojo.setUpdDate(entity.getUpdDate());
			pojo.setUpdUser(entity.getUpdUser());
			pojo.setVersion(entity.getVersion());
			pojos.add(pojo);
		}
		return pojos;
	}

	@Transactional(value = "transactionManagerAcarreos",rollbackFor = RuntimeException.class)
	private ArrayList<KeyPOJO> getNewKeys(List<KeyPOJO> keysRegistered) {
		ArrayList<KeyPOJO> keys = new ArrayList<KeyPOJO>();
		keys.addAll(convertKeyEntityToPojo(llavesDao.getPendingKeys(convertKeyPojoToEntity(keysRegistered))));
		return keys;
	}

	private void registerSync(SyncPOJO syncPOJO) {

		AcarreosUsuariosSincronizaciones sync = new AcarreosUsuariosSincronizaciones();

		sync.setAddDate(new Date());
		sync.setAddUser(syncPOJO.getUserId());
		sync.setEstatus("A");
		sync.setImei(syncPOJO.getImei());
		sync.setLocation(syncPOJO.getLocation());
		try {
			syncDao.insert(sync);
		} catch (BussinessException e) {
			e.printStackTrace();
		}

	}

	private ArrayList<DistancePOJO> getChangedDistances(List<DistancePOJO> distancesRegistered) {
		ArrayList<DistancePOJO> distances = parseDistanceEntityListToPojoList(
				distanciasDao.getDistancesWithDifferentUpdDate(parseDistancePojoListToEntityList(distancesRegistered)));
		return distances;
	}

	private List<AcarreosPuntosDistancias> parseDistancePojoListToEntityList(List<DistancePOJO> pojos) {

		List<AcarreosPuntosDistancias> entities = new ArrayList<>();

		for (DistancePOJO pojo : pojos) {

			AcarreosPuntosDistancias entity = new AcarreosPuntosDistancias();
			entity.setAddDate(pojo.getAddDate());
			entity.setAddUser(pojo.getAddUser());
			entity.setDistance(pojo.getDistance());
			entity.setEstatus(pojo.getEstatus());
			entity.setIdDistance(pojo.getIdDistanceServer());
			entity.setIdPoint(pojo.getIdPoint());
			if (pojo.getUpdDate() != null)
				entity.setUpdDate(pojo.getUpdDate());
			if (pojo.getUpdUser() != null)
				entity.setUpdUser(pojo.getUpdUser());

			entities.add(entity);

		}
		return entities;
	}

	private ArrayList<DistancePOJO> parseDistanceEntityListToPojoList(List<AcarreosPuntosDistancias> entities) {
		ArrayList<DistancePOJO> pojos = new ArrayList<>();
		for (AcarreosPuntosDistancias entity : entities) {
			DistancePOJO distancePOJO = new DistancePOJO();
			distancePOJO.setAddDate(entity.getAddDate());
			distancePOJO.setAddUser(entity.getAddUser());
			distancePOJO.setDistance((float) entity.getDistance());
			distancePOJO.setEstatus(entity.getEstatus());
			distancePOJO.setIdDistanceServer(entity.getIdDistance());
			distancePOJO.setIdPoint(entity.getIdPoint());
			distancePOJO.setUpdDate(entity.getUpdDate());
			distancePOJO.setUpdUser(entity.getUpdUser());

			pojos.add(distancePOJO);
		}

		return pojos;
	}

	private List<Integer> getDistanceOnlyIds(List<DistancePOJO> distancesRegistered) {

		List<Integer> ids = new ArrayList<>();

		for (DistancePOJO distance : distancesRegistered)
			ids.add(distance.getIdDistanceServer());

		return ids;
	}

	private ArrayList<MaterialsByPointPOJO> getChangedMaterialsByPoint(
			List<MaterialsByPointPOJO> materialsByPointRegistered) {
		ArrayList<MaterialsByPointPOJO> materialsByPoint = parseMaterialsByPointEntityListToPojoList(
				materialesPorPuntoDao.getMaterialsByPointWithDifferentUpdDate(
						parseMaterialByPointPojoListToEntityList(materialsByPointRegistered)));
		return materialsByPoint;
	}

	private List<AcarreosMaterialesPorPunto> parseMaterialByPointPojoListToEntityList(
			List<MaterialsByPointPOJO> materialsByPointRegistered) {
		List<AcarreosMaterialesPorPunto> materialsByPointEntities = new ArrayList<>();
		for (MaterialsByPointPOJO materialsByPointPojo : materialsByPointRegistered) {

			AcarreosMaterialesPorPunto materialByPointEntity = new AcarreosMaterialesPorPunto();

			materialByPointEntity.setAddDate(materialsByPointPojo.getAddDate());
			materialByPointEntity.setAddUser(materialsByPointPojo.getAddUser());
			materialByPointEntity.setEstatus(materialsByPointPojo.getStatusServer());
			materialByPointEntity.setIdMaterial(materialsByPointPojo.getIdMaterialServer());
			materialByPointEntity.setIdMaterialPorPunto(materialsByPointPojo.getIdMaterialByPointServer());
			materialByPointEntity.setIdPunto(materialsByPointPojo.getIdPointServer());

			if (materialsByPointPojo.getUpdDate() != null)
				materialByPointEntity.setUpdDate(materialsByPointPojo.getUpdDate());

			materialsByPointEntities.add(materialByPointEntity);
		}
		return materialsByPointEntities;
	}

	private ArrayList<MaterialsByPointPOJO> parseMaterialsByPointEntityListToPojoList(
			List<AcarreosMaterialesPorPunto> pendingMaterialsByPoint) {

		ArrayList<MaterialsByPointPOJO> materialsByPointPojos = new ArrayList<>();

		for (AcarreosMaterialesPorPunto materialsByPointEntity : pendingMaterialsByPoint) {

			MaterialsByPointPOJO materialsByPointPojo = new MaterialsByPointPOJO();

			materialsByPointPojo.setAddDate(materialsByPointEntity.getAddDate());
			materialsByPointPojo.setAddUser(materialsByPointEntity.getAddUser());
			materialsByPointPojo.setIdMaterialByPointServer(materialsByPointEntity.getIdMaterialPorPunto());
			materialsByPointPojo.setIdMaterialServer(materialsByPointEntity.getIdMaterial());
			materialsByPointPojo.setIdPointServer(materialsByPointEntity.getIdPunto());
			materialsByPointPojo.setStatusServer(materialsByPointEntity.getEstatus());

			if (materialsByPointEntity.getUpdDate() != null)
				materialsByPointPojo.setUpdDate(materialsByPointEntity.getUpdDate());

			materialsByPointPojos.add(materialsByPointPojo);

		}

		return materialsByPointPojos;
	}

	private List<Integer> getMaterialByPointOnlyIds(List<MaterialsByPointPOJO> materialsByPointRegistered) {
		List<Integer> ids = new ArrayList<>();

		for (MaterialsByPointPOJO materialByPointPojo : materialsByPointRegistered)
			ids.add(materialByPointPojo.getIdMaterialByPointServer());

		return ids;
	}

	private ArrayList<MaterialsPOJO> getChangedMaterials(List<MaterialsPOJO> materialsAlreadyRegistered) {
		ArrayList<MaterialsPOJO> materials = parseMaterialEntityListToPojoList(materialesDao
				.getMaterialsWithDifferentUpdDate(parseMaterialPojoListToEntityList(materialsAlreadyRegistered)));
		return materials;
	}

	private List<AcarreosMateriales> parseMaterialPojoListToEntityList(List<MaterialsPOJO> materialsAlreadyRegistered) {

		List<AcarreosMateriales> materialEntities = new ArrayList<>();

		for (MaterialsPOJO materialPojo : materialsAlreadyRegistered) {

			AcarreosMateriales entity = new AcarreosMateriales();

			entity.setAcronimoParaTag(materialPojo.getAcronym());
			entity.setAddDate(materialPojo.getAddDate());
			entity.setAddUser(materialPojo.getAddUser());
			entity.setEstatus(materialPojo.getStatusServer());
			entity.setIdAsignacion(materialPojo.getIdMaterialServer());
			entity.setIdMaterialNavision(materialPojo.getIdMaterialNavision());
			entity.setObra(materialPojo.getBuilding());

			if (materialPojo.getUpdDate() != null)
				entity.setUpdDate(materialPojo.getUpdDate());

			materialEntities.add(entity);
		}
		return materialEntities;
	}

	private ArrayList<MaterialsPOJO> parseMaterialEntityListToPojoList(List<AcarreosMateriales> pendingMaterials) {

		ArrayList<MaterialsPOJO> pojoList = new ArrayList<>();

		for (AcarreosMateriales materialesEntity : pendingMaterials) {

			MaterialsPOJO pojo = new MaterialsPOJO();

			pojo.setAcronym(materialesEntity.getAcronimoParaTag());
			pojo.setAddDate(materialesEntity.getAddDate());
			pojo.setAddUser(materialesEntity.getAddUser());
			pojo.setBuilding(materialesEntity.getObra());
			pojo.setIdMaterialNavision(materialesEntity.getIdMaterialNavision());
			pojo.setIdMaterialServer(materialesEntity.getIdAsignacion());
			pojo.setStatusServer(materialesEntity.getEstatus());
			pojo.setUpdDate(materialesEntity.getUpdDate());

			try {
				VISEItem item = itemDao.getVISEItem(materialesEntity.getIdMaterialNavision());
				if (item != null) {
					pojo.setDescription(item.getDescription());
					pojo.setUnitOfMeasure(item.getBaseUnitOfMeasure());
				}

			} catch (BussinessException e) {
				e.printStackTrace();
			}

			pojoList.add(pojo);

		}

		return pojoList;
	}

	private List<Integer> getMaterialOnlyIds(List<MaterialsPOJO> materialsAlreadyRegistered) {
		List<Integer> ids = new ArrayList<>();
		for (MaterialsPOJO material : materialsAlreadyRegistered)
			ids.add(material.getIdMaterialServer());
		return ids;
	}

	/**
	 * @param pointsAlreadyRegistered los puntos ya registrados en forma de pojo
	 * @return la lista en forma de pojo de puntos que cambiaron
	 */
	private ArrayList<PointPOJO> getChangedPoints(List<PointPOJO> pointsAlreadyRegistered, String building) {
		/*
		 * A el dao se le envia la lista de puntos pero en forma de modelo/entidad se
		 * debe de convertir el pojo a entity para pasarselo al dao regresa otro entitiy
		 * que hay que convertir a pojo
		 * 
		 * es asi:
		 * 
		 * Pojo->Entity->Resultado->Entity->Pojo
		 */
		ArrayList<PointPOJO> points = parsePointEntityListToPojoList(
				puntosDao.getPointsWithDifferentUpdDate(parsePojoListToEntityList(pointsAlreadyRegistered)),building);

		return points;
	}

	/**
	 * @param entities la lista de entidades a convertir
	 * @return la lista de pojos resultado
	 */
	private ArrayList<PointPOJO> parsePointEntityListToPojoList(List<AcarreosPuntos> entities, String building) {

		ArrayList<PointPOJO> pointPojos = new ArrayList<>();

		for (AcarreosPuntos entity : entities)

			pointPojos.add(parseToPojo(entity, building));

		return pointPojos;
	}

	/**
	 * @param pointsPojo la lista de pojos a convertir
	 * @return una lista de entidades
	 */
	private List<AcarreosPuntos> parsePojoListToEntityList(List<PointPOJO> pointsPojo) {

		List<AcarreosPuntos> entities = new ArrayList<>();

		for (PointPOJO pointPojo : pointsPojo)
			entities.add(parseToEntity(pointPojo));

		return entities;
	}

	private AcarreosPuntos parseToEntity(PointPOJO pointPojo) {

		AcarreosPuntos pointEntity = new AcarreosPuntos();

		pointEntity.setAddDate(new Date());
		pointEntity.setAddUser(pointPojo.getAddUser());
		pointEntity.setAutorizado(0);
		pointEntity.setCadenamiento(pointPojo.getCadenamiento());
		pointEntity.setEsBancoYTiro(pointPojo.getEsBancoYTiro());
		pointEntity.setEstatus("A");
		pointEntity.setAutorizado(pointPojo.getAutorizado());
		pointEntity.setLatitud(pointPojo.getLatitud());
		pointEntity.setLongitud(pointPojo.getLongitud());
		pointEntity.setNombreBanco(pointPojo.getNombreBanco());
		pointEntity.setRadio(pointPojo.getRadio());
		pointEntity.setRegDate(pointPojo.getRegDate());
		pointEntity.setTipoPunto(pointPojo.getTipoPunto());
		if (pointPojo.getUpdDate() != null)
			pointEntity.setUpdDate(pointPojo.getUpdDate());
		if (pointPojo.getIdPuntoServer() != null)
			pointEntity.setIdPunto(pointPojo.getIdPuntoServer());

		return pointEntity;
	}

	private PointPOJO parseToPojo(AcarreosPuntos entity, String building) {

		PointPOJO pointPOJO = new PointPOJO();

		pointPOJO.setAddUser(entity.getAddUser());
		pointPOJO.setCadenamiento(entity.getCadenamiento());
		pointPOJO.setEsBancoYTiro(entity.getEsBancoYTiro());
		pointPOJO.setEstatus(entity.getEstatus());
		pointPOJO.setIdPuntoServer(entity.getIdPunto());
		pointPOJO.setLatitud(entity.getLatitud());
		pointPOJO.setLongitud(entity.getLongitud());
		pointPOJO.setNombreBanco(entity.getNombreBanco());
		pointPOJO.setAutorizado(entity.getAutorizado());
		
		/*
		 * obten la obra asignada a este punto
		 * */
		String obra = null;
		try {
			obra = puntosPorObraDao.getBuildingByPoint(entity.getIdPunto(), building);
		}catch(Exception e) {
			LOGGER.error("No se pudo obtener la obra",e);
		}
		/*
		 * Si no hay obra da de baja el punto para la terminal
		 * */
		if(obra == null) 
			pointPOJO.setEstatus("B");
		
		pointPOJO.setObra(obra);
		pointPOJO.setRadio(entity.getRadio());
		pointPOJO.setRegDate(entity.getRegDate());
		pointPOJO.setTipoPunto(entity.getTipoPunto());

		pointPOJO.setIdPuntoServer(entity.getIdPunto());
		pointPOJO.setIdPuntoLocal(0);

		if (entity.getUpdDate() != null)
			pointPOJO.setUpdDate(entity.getUpdDate());

		return pointPOJO;

	}

	public Integer addNewReprint(ReprintPOJO reprint) {
		try {
			reprintDao.insert(parseReprintPOJOtoEntity(reprint));
		} catch (BussinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			return reprintDao.getLastItem();
		} catch (BussinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private AcarreosBoletosReimpresiones parseReprintPOJOtoEntity(ReprintPOJO reprint) {
		AcarreosBoletosReimpresiones entity = new AcarreosBoletosReimpresiones();
		entity.setAddDate(reprint.getAddDate());
		entity.setAddUser(reprint.getAddUser());
		entity.setCoordinates(reprint.getCoordinates());
		entity.setEstatus("A");
		if (reprint.getSheetNumber() != null)
			entity.setSheetNumber(reprint.getSheetNumber());
		return entity;
	}

	public Integer cancelTicket(CancelTicketPOJO cancelTicketPOJO) {
		AcarreosBoletos ticket = parseTicketPOJOToEntity(cancelTicketPOJO.getTicketPOJO());
		Integer lastId = null;
		try {
			lastId = boletosDao.cancelTicket(ticket, cancelTicketPOJO.getIdEmpleado());
			
		} catch (BussinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return lastId;
		}
		return lastId;
	}


}
