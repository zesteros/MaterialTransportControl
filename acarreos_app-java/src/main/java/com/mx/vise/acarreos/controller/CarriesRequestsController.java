package com.mx.vise.acarreos.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mx.vise.acarreos.pojos.CancelTicketPOJO;
import com.mx.vise.acarreos.pojos.RequestDeviceRegisterPOJO;
import com.mx.vise.acarreos.pojos.carries.PointPOJO;
import com.mx.vise.acarreos.pojos.carries.ReprintPOJO;
import com.mx.vise.acarreos.pojos.carries.SyncRequestPOJO;
import com.mx.vise.acarreos.pojos.carries.SyncResponsePOJO;
import com.mx.vise.acarreos.pojos.carries.TicketPOJO;
import com.mx.vise.acarreos.service.CarriesService;
import com.mx.vise.acarreos.util.RequestValidator;
import com.mx.vise.acarreos.util.TotalURL;

@Controller
@RequestMapping(TotalURL.REQUESTS_CARRIES)
public class CarriesRequestsController {

	@Autowired
	private RequestValidator requestValidator;
	@Autowired
	private CarriesService carriesService;

	
	/**
	 * @param session     el objeto de session
	 * @param httpRequest
	 * @return
	 */
	@SuppressWarnings({})
	@RequestMapping(value = "/putPoint", method = { RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<Integer> putPoint(@RequestBody PointPOJO point, final HttpServletRequest httpRequest) {
		/*
		 * Determina si es v�lido el encabezado http (con sus respectivas credenciales)
		 */

		if (requestValidator.isAValidRequest(httpRequest)) {
			Integer idPoint = carriesService.addNewPoint(point);
			if (idPoint != null && idPoint != 0)
				/*
				 * Si es v�lido regresa los datos
				 */
				return new ResponseEntity<Integer>(idPoint, HttpStatus.OK);
		}
		return new ResponseEntity<Integer>(HttpStatus.UNAUTHORIZED);// 204 si no es valido
	}

	@RequestMapping(value = "/syncPoints", method = { RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<SyncResponsePOJO> syncPoints(@RequestBody SyncRequestPOJO actualData,
			final HttpServletRequest httpRequest) {
		try {
			if (requestValidator.isAValidRequest(httpRequest)) {

				SyncResponsePOJO response = carriesService.getPendingData(actualData);


				return new ResponseEntity<SyncResponsePOJO>(response, HttpStatus.OK);
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getMessage());

		}

		return new ResponseEntity<SyncResponsePOJO>(HttpStatus.UNAUTHORIZED);
	}
	/**
	 * @param session     el objeto de session
	 * @param httpRequest
	 * @return
	 */
	@SuppressWarnings({})
	@RequestMapping(value = "/putTicket", method = { RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<Integer> putTicket(@RequestBody TicketPOJO ticket, final HttpServletRequest httpRequest) {
		/*
		 * Determina si es v�lido el encabezado http (con sus respectivas credenciales)
		 */

		if (requestValidator.isAValidRequest(httpRequest)) {
			Integer idTicket = carriesService.addNewTicket(ticket);
			if (idTicket != null && idTicket != 0)
				/*
				 * Si es v�lido regresa los datos
				 */
				return new ResponseEntity<Integer>(idTicket, HttpStatus.OK);
		}
		return new ResponseEntity<Integer>(HttpStatus.UNAUTHORIZED);// 204 si no es valido
	}
	
	/**
	 * @param session     el objeto de session
	 * @param httpRequest
	 * @return
	 */
	@SuppressWarnings({})
	@RequestMapping(value = "/putCanceledTicket", method = { RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<Integer> putCanceledTicket(@RequestBody CancelTicketPOJO cancelTicket, final HttpServletRequest httpRequest) {
		/*
		 * Determina si es v�lido el encabezado http (con sus respectivas credenciales)
		 */

		if (requestValidator.isAValidRequest(httpRequest)) {
			Integer idTicket = carriesService.cancelTicket(cancelTicket);
			if (idTicket != null && idTicket != 0)
				/*
				 * Si es v�lido regresa los datos
				 */
				return new ResponseEntity<Integer>(idTicket, HttpStatus.OK);
		}
		return new ResponseEntity<Integer>(HttpStatus.UNAUTHORIZED);// 204 si no es valido
	}
	
	
	
	/**
	 * @param session     el objeto de session
	 * @param httpRequest
	 * @return
	 */
	@SuppressWarnings({})
	@RequestMapping(value = "/putReprint", method = { RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<Integer> putTicket(@RequestBody ReprintPOJO reprint, final HttpServletRequest httpRequest) {
		/*
		 * Determina si es v�lido el encabezado http (con sus respectivas credenciales)
		 */

		if (requestValidator.isAValidRequest(httpRequest)) {
			Integer idReprint = carriesService.addNewReprint(reprint);
			if (idReprint != null && idReprint != 0)
				/*
				 * Si es v�lido regresa los datos
				 */
				return new ResponseEntity<Integer>(idReprint, HttpStatus.OK);
		}
		return new ResponseEntity<Integer>(HttpStatus.UNAUTHORIZED);// 204 si no es valido
	}
}
