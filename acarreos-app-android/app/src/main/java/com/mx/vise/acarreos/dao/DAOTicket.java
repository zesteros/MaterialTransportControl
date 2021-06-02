package com.mx.vise.acarreos.dao;

import android.content.Context;
import android.util.Log;

import com.mx.vise.acarreos.App;
import com.mx.vise.acarreos.dao.entities.Tickets;
import com.mx.vise.acarreos.dao.entities.TicketsDao;
import com.mx.vise.acarreos.pojos.TicketPOJO;
import com.mx.vise.acarreos.tasks.Progress;
import com.mx.vise.acarreos.tasks.SyncData;
import com.mx.vise.acarreos.util.CancelTicketRequest;
import com.mx.vise.acarreos.util.gps.LatLng;
import com.mx.vise.login.pojos.EmployeePojo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.mx.vise.acarreos.dao.DAOMaterials.getMaterialByID;
import static com.mx.vise.acarreos.dao.DAOPoints.getPointById;

/**
 * **************************VISE*******************************
 * *******************DEPARTAMENTO DE T.I.**********************
 * <p>
 * Creado por Angelo el lunes 18 de febrero del 2019 a las 18:53
 *
 * @author Angelo de Jesus Loza Martinez
 * @version acarreos_app-android
 */
public class DAOTicket {


    private static final String TAG = "VISE";

    public static Tickets setExitDateAndBankUser(Tickets ticketEntity, TicketPOJO ticket) {

        ticketEntity.setExitDate(ticket.getExitDate());
        ticketEntity.setUserIdBank(ticket.getUserIdBank());
        ticketEntity.setUsernameBank(ticket.getUsernameBank());

        return ticketEntity;
    }

    public static Tickets setArrivalData(Tickets ticketEntity, TicketPOJO ticket) {
        ticketEntity.setArrivalDate(ticket.getArrivalDate());
        ticketEntity.setUserIdThrow(ticket.getUserIdThrow());
        ticketEntity.setUserNameThrow(ticket.getUserNameThrow());
        ticketEntity.setArrivalCoordinates(ticket.getArrivalCoordinates().latitude.floatValue() + "," + ticket.getArrivalCoordinates().longitude.floatValue());
        return ticketEntity;
    }

    public static Tickets setExpirationDate(Tickets ticketEntity, TicketPOJO ticket) {


        ticketEntity.setExpirationDate(ticket.getExpirationDate());

        return ticketEntity;
    }

    public static Tickets setDistanceAndDestiny(Tickets ticketEntity, TicketPOJO ticket) {
        ticketEntity.setDistance(ticket.getDistance());
        ticketEntity.setGetIdPuntoServerDestiny(Long.valueOf(ticket.getDestiny().getIdPuntoServer()));
        return ticketEntity;
    }

    public static Tickets setSheetNumber(Tickets ticketEntity, TicketPOJO ticket) {

        ticketEntity.setSheetNumber(ticket.getSheetNumber());

        return ticketEntity;
    }

    public static void cancelTickets(Context context, String sheetNumber) {

        TicketsDao ticketsDao = ((App) context.getApplicationContext()).getDaoSession().getTicketsDao();
        /*
         * Saca todos los boletos con ese numero de folio
         * */
        List<Tickets> availableTickets = ticketsDao.queryBuilder().where(TicketsDao.Properties.SheetNumber.eq(sheetNumber)).list();

        for (Tickets ticket : availableTickets) {
            ticket.setCancelInApp(1);
            availableTickets.set(availableTickets.indexOf(ticket), ticket);
        }

        ticketsDao.updateInTx(availableTickets);

    }

    /**
     * Este método es el principal del DAO ya que forma el boleto base
     * es decir un boleto con los parametros que ocupan todos los tipos de boleto:
     *
     * 1-obra
     * 2-tipo de boleto
     * 3-placa trasera
     * 4-aumento
     * 5-capacidad
     * 6-id del material
     * 7-id del punto de origen
     * 8-coordenadas de salida
     * 9-descuento
     * 10-unidad de medida
     * 11-add user
     * 12-add date
     * 13-estatus de subida
     *
     * @param employeePojo la sesion del empleado
     * @param ticket el boleto a formar
     * @return el boleto base
     */
    public static Tickets getBaseTicket(EmployeePojo employeePojo, TicketPOJO ticket) {
        Tickets ticketsEntity = new Tickets();

        ticketsEntity.setBuilding(ticket.getBuilding());
        ticketsEntity.setTicketType(ticket.getTicketType());
        ticketsEntity.setRearLicensePlate(ticket.getRearLicensePlate());
        ticketsEntity.setIncrease(ticket.getIncrease());
        ticketsEntity.setCapacity(ticket.getCapacity());
        ticketsEntity.setIdMaterialServer(ticket.getMaterial().getIdMaterialServer());
        ticketsEntity.setIdPuntoServerOrigin(Long.valueOf(ticket.getOrigin().getIdPuntoServer()));
        ticketsEntity.setExitCoordinates(ticket.getExitCoordinates().latitude.floatValue() + "," + ticket.getExitCoordinates().longitude.floatValue());
        ticketsEntity.setDiscount(ticket.getDiscount());
        ticketsEntity.setUnitOfMeasure(ticket.getUnitOfMeasure());
        ticketsEntity.setAddUser(employeePojo.getEmployeeId());
        ticketsEntity.setAddDate(new Date());
        ticketsEntity.setUploadStatus("A");

        return ticketsEntity;
    }

    /**
     * @param context el contexto
     * @param employeePojo la sesion
     * @param royalty la regalia (boleto)
     * @return si se guardo
     */
    public static boolean saveRoyalty(Context context, EmployeePojo employeePojo, TicketPOJO royalty) {

        TicketsDao ticketsDao = ((App) context.getApplicationContext()).getDaoSession().getTicketsDao();

        /*
        * Construye el boleto base
        * */
        Tickets values = getBaseTicket(employeePojo, royalty);
        /*
        * Asigna el numero de folio (no todos los boletos llevan folio)
        * */
        values = setSheetNumber(values, royalty);
        /*
        * Asigna fecha de salida y usuario del banco (no todos los boletos lo llevan)
        * */
        values = setExitDateAndBankUser(values, royalty);

        ticketsDao.insert(values);


        return true;
    }

    public static List<TicketPOJO> getTicketsToSend(Context context, SyncData syncData) {
        List<TicketPOJO> tickets = new ArrayList();


        TicketsDao ticketsDao = ((App) context.getApplicationContext()).getDaoSession().getTicketsDao();

        List<Tickets> ticketsEntities = ticketsDao
                .queryBuilder()
                .where(TicketsDao.Properties.UploadStatus.eq("A"))
                .list();
        int totalElements = ticketsEntities.size();

        if (syncData != null)
            syncData.publishProgress(new Progress(0, 0, totalElements, "Procesando boletos..."));
        int i = 0;

        for (Tickets ticketsEntity : ticketsEntities) {
            tickets.add(convertEntityToPojo(context, ticketsEntity));
            if (syncData != null)
                syncData.publishProgress(new Progress(i * 100 / totalElements, i, totalElements, "Obteniendo nuevos boletos..."));
            i++;

        }

        return tickets;
    }

    /**
     * @param context el contexto
     * @param ticketEntity la entidad
     * @return el pojo del boleto
     */
    public static TicketPOJO convertEntityToPojo(Context context, Tickets ticketEntity) {
        TicketPOJO ticketPojo = new TicketPOJO();
        ticketPojo.setIdTicket(ticketEntity.getIdTicketLocal());
        ticketPojo.setBuilding(ticketEntity.getBuilding());
        ticketPojo.setTicketType(ticketEntity.getTicketType());
        ticketPojo.setRearLicensePlate(ticketEntity.getRearLicensePlate());
        ticketPojo.setIncrease(ticketEntity.getIncrease());
        ticketPojo.setCapacity(ticketEntity.getCapacity());
        ticketPojo.setMaterial(getMaterialByID(context, String.valueOf(ticketEntity.getIdMaterialServer())));
        ticketPojo.setOrigin(getPointById(context, ticketEntity.getIdPuntoServerOrigin()));
        ticketPojo.setExitCoordinates(
                new LatLng()
                        .setLatitude(
                                BigDecimal.valueOf(Double.parseDouble(ticketEntity.getExitCoordinates().split(",")[0]))
                        )
                        .setLongitude(
                                BigDecimal.valueOf(Double.parseDouble(ticketEntity.getExitCoordinates().split(",")[1]))
                        )
        );
        ticketPojo.setDiscount(ticketEntity.getDiscount());
        ticketPojo.setExitDate(ticketEntity.getExitDate());

        ticketPojo.setUserIdBank(ticketEntity.getUserIdBank());
        ticketPojo.setSheetNumber(ticketEntity.getSheetNumber());
        ticketPojo.setDistance(ticketEntity.getDistance());

        ticketPojo.setArrivalDate(ticketEntity.getArrivalDate());

        ticketPojo.setUserIdThrow(ticketEntity.getUserIdThrow());

        if (ticketEntity.getArrivalCoordinates() != null) {
            ticketPojo.setArrivalCoordinates(
                    new LatLng()
                            .setLatitude(
                                    BigDecimal.valueOf(Double.parseDouble(ticketEntity.getArrivalCoordinates().split(",")[0]))
                            )
                            .setLongitude(
                                    BigDecimal.valueOf(Double.parseDouble(ticketEntity.getArrivalCoordinates().split(",")[1]))
                            )
            );
        }
        if (ticketEntity.getGetIdPuntoServerDestiny() != null)
            ticketPojo.setDestiny(getPointById(context, ticketEntity.getGetIdPuntoServerDestiny()));
        ticketPojo.setExpirationDate(ticketEntity.getExpirationDate());

        ticketPojo.setUnitOfMeasure(ticketEntity.getUnitOfMeasure());
        ticketPojo.setAddUser(ticketEntity.getAddUser());

        return ticketPojo;

    }

    /**
     * Cambia el estado del boleto
     *
     * @param context
     * @param status
     * @param idTicket
     * @param idTicketServer
     * @param isCanceled
     * @return
     */
    public static boolean changeTicketStatus(Context context, String status, Long
            idTicket, Long idTicketServer, boolean isCanceled) {

        TicketsDao ticketsDao = ((App) context.getApplicationContext()).getDaoSession().getTicketsDao();

        Tickets ticketToUpdate = ticketsDao.queryBuilder().where(TicketsDao.Properties.IdTicketLocal.eq(idTicket))
                .limit(1).unique();

        ticketToUpdate.setUploadDate(new Date());
        ticketToUpdate.setIdTicketServer(idTicketServer);

        if (!isCanceled)
            ticketToUpdate.setUploadStatus(status);
        else
            ticketToUpdate.setCancelUploadStatus(status);


        ticketsDao.update(ticketToUpdate);

        return true;
    }

    /**
     *
     * guarda boleto de mejora de caminos
     *
     * @param context
     * @param employeePojo
     * @param roadImprovement
     * @return
     */
    public static boolean saveRoadImprovement(Context context, EmployeePojo
            employeePojo, TicketPOJO roadImprovement) {

        TicketsDao ticketsDao = ((App) context.getApplicationContext()).getDaoSession().getTicketsDao();


        Tickets values = getBaseTicket(employeePojo, roadImprovement);
        values = setSheetNumber(values, roadImprovement);
        values = setExitDateAndBankUser(values, roadImprovement);
        values = setDistanceAndDestiny(values, roadImprovement);

        ticketsDao.insert(values);

        return true;
    }

    /**
     * @param context
     * @param employeePojo
     * @param carry
     * @return
     */
    public static boolean saveCarry(Context context, EmployeePojo employeePojo, TicketPOJO
            carry) {

        Tickets values = getBaseTicket(employeePojo, carry);
        values = setSheetNumber(values, carry);
        values = setExitDateAndBankUser(values, carry);
        values = setDistanceAndDestiny(values, carry);
        values = setArrivalData(values, carry);

        TicketsDao ticketsDao = ((App) context.getApplicationContext()).getDaoSession().getTicketsDao();
        ticketsDao.insert(values);


        return true;
    }

    public static boolean saveFreeInternCarry(Context context, EmployeePojo
            employeePojo, TicketPOJO freeIntern) {
        Tickets values = getBaseTicket(employeePojo, freeIntern);
        values = setSheetNumber(values, freeIntern);
        values = setExitDateAndBankUser(values, freeIntern);
        values = setDistanceAndDestiny(values, freeIntern);
        TicketsDao ticketsDao = ((App) context.getApplicationContext()).getDaoSession().getTicketsDao();
        ticketsDao.insert(values);
        return true;
    }

    public static boolean saveMaterialRequest(Context context, EmployeePojo
            employeePojo, TicketPOJO materialRequest) {


        Tickets values = getBaseTicket(employeePojo, materialRequest);
        values = setSheetNumber(values, materialRequest);
        values = setExitDateAndBankUser(values, materialRequest);
        values = setExpirationDate(values, materialRequest);
        TicketsDao ticketsDao = ((App) context.getApplicationContext()).getDaoSession().getTicketsDao();
        ticketsDao.insert(values);
        return true;
    }

    /**
     * @param context el contexto
     * @param barcode el numero de folio
     * @return si el ticket existe
     */
    public static CancelTicketRequest ticketExist(Context context, String barcode) {
        TicketsDao ticketsDao = ((App) context.getApplicationContext()).getDaoSession().getTicketsDao();

        CancelTicketRequest cancelTicketRequest = new CancelTicketRequest();

        List<Tickets> ticketsResult = ticketsDao
                .queryBuilder()
                .where(TicketsDao.Properties.SheetNumber.eq(barcode))
                .list();

        cancelTicketRequest.setCancelStatus(CancelTicketRequest.CancelStatus.SUCCESS);

        if (ticketsResult.isEmpty())
            cancelTicketRequest.setCancelStatus(CancelTicketRequest.CancelStatus.DOES_NOT_EXIST_IN_DEVICE);


        for (Tickets tickets : ticketsResult) {
            if (tickets.getCancelInApp() != null && tickets.getCancelInApp() == 1){
                cancelTicketRequest.setCancelStatus(CancelTicketRequest.CancelStatus.ALREADY_CANCELED);
                break;
            }
        }

        cancelTicketRequest.setTickets(ticketsResult);

        return cancelTicketRequest;
    }

    /**
     * @param context el contexto
     * @param tickets  el boleto a cancelar
     */
    public static void cancelTickets(Context context, List<Tickets> tickets) {
        TicketsDao ticketsDao = ((App) context.getApplicationContext()).getDaoSession().getTicketsDao();
        for (Tickets ticket : tickets) {
            Log.i(TAG, "cancelTickets: canceling tickets: "+ticket.getIdTicketLocal()+","+ticket.getSheetNumber());
            ticket.setCancelInApp(1);
            ticket.setCancelUploadStatus("A");
            ticketsDao.update(ticket);
        }
    }

    public static List<TicketPOJO> getTicketsCanceledToSend(Context context, SyncData syncData) {

        List<TicketPOJO> tickets = new ArrayList<>();


        TicketsDao ticketsDao = ((App) context.getApplicationContext()).getDaoSession().getTicketsDao();

        List<Tickets> ticketsEntities = ticketsDao
                .queryBuilder()
                .where(TicketsDao.Properties.CancelUploadStatus.eq("A"))
                .list();
        int totalElements = ticketsEntities.size();

        if (syncData != null)
            syncData.publishProgress(new Progress(0, 0, totalElements, "Procesando boletos..."));
        int i = 0;

        for (Tickets ticketsEntity : ticketsEntities) {
            tickets.add(convertEntityToPojo(context, ticketsEntity));
            if (syncData != null)
                syncData.publishProgress(new Progress(i * 100 / totalElements, i, totalElements, "Obteniendo nuevos boletos..."));
            i++;

        }

        return tickets;
    }
}
