package com.mx.vise.acarreos.pojos;

import java.io.Serializable;

public class CancelTicketPOJO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3094812574862254447L;
	private Integer idEmpleado;
	private TicketPOJO ticketPOJO;
	public Integer getIdEmpleado() {
		return idEmpleado;
	}
	public void setIdEmpleado(Integer idEmpleado) {
		this.idEmpleado = idEmpleado;
	}
	public TicketPOJO getTicketPOJO() {
		return ticketPOJO;
	}
	public void setTicketPOJO(TicketPOJO ticketPOJO) {
		this.ticketPOJO = ticketPOJO;
	}

}
