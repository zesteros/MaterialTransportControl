package com.mx.vise.acarreos.util;

import com.mx.vise.acarreos.dao.entities.Tickets;

import java.util.List;

public class CancelTicketRequest {

    private List<Tickets> tickets;
    private CancelStatus cancelStatus;

    public List<Tickets> getTickets() {
        return tickets;
    }

    public void setTickets(List<Tickets> tickets) {
        this.tickets = tickets;
    }

    public CancelStatus getCancelStatus() {
        return cancelStatus;
    }

    public void setCancelStatus(CancelStatus cancelStatus) {
        this.cancelStatus = cancelStatus;
    }

    public enum CancelStatus{
        ALREADY_CANCELED, DOES_NOT_EXIST_IN_DEVICE, SUCCESS
    }
}
