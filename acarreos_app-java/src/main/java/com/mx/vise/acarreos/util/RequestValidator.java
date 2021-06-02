package com.mx.vise.acarreos.util;

import java.nio.charset.Charset;
import java.util.Base64;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.mx.vise.util.UtilLoadProperties;

import com.mx.vise.util.AESencrpPublic;

@Service
public class RequestValidator {

	public static final String IMEI_HEADER_NAME = "DTZT";
	public static final String PROJECT_HEADER_NAME = "PRJT";

	/*
	 * Método para saber si la solicitud viene de un cliente certificado.
	 */
	public boolean isAValidRequest(HttpServletRequest request) {

		/*
		 * Obtiene mediante el algoritmo el usuario y contraseña del encabezado http.
		 */
		RequestUser user = getUserByRequest(request);
		/*
		 * Si no es nulo (existe)
		 */
		if (user != null) {
			/*
			 * Compara con los datos guardados en config.properties
			 */
			String userProperty = UtilLoadProperties.getInstance().getProperty(Constants.ANDROID_USER);
			String passProperty = UtilLoadProperties.getInstance().getProperty(Constants.ANDROID_KEY);
			/*
			 * Regresa si son validos o no lo son
			 */
			return user.getContra().equals(passProperty) && user.getUser().equals(userProperty);
		}
		return false;
	}

	/**
	 * busca los datos de la cabecera, los campos son: <br>
	 * Authorization Basic user:pass son los campos que se tienen que enviar.<br>
	 * DTZT DTZ ?IMEI
	 * 
	 * @param request
	 *            tiene los datos de la cabecera
	 * @return
	 */
	private RequestUser getUserByRequest(HttpServletRequest request) {
		RequestUser user = null;
		final String authorization = request.getHeader("Authorization");
		if (authorization != null && authorization.startsWith("Basic")) {
			String base64Credentials = authorization.substring("Basic".length()).trim();
			try {
				AESencrpPublic aeSencrpPublic = new AESencrpPublic(Constants.KEYVALUE);
				base64Credentials = aeSencrpPublic.decrypt(base64Credentials);
			} catch (Exception e) {
				System.out.println("No se pudo desencriptar el siguiente valor: " + base64Credentials);
			}
			String credentials = new String(Base64.getDecoder().decode(base64Credentials), Charset.forName("UTF-8"));
			final String[] values = credentials.split(":", 2);

			if (values != null && values.length > 1)
				user = new RequestUser(values[0], values[1]);

		}
		return user;
	}

	public String getIMEIFromRequest(HttpServletRequest request) {

		return decryptHeader(request, IMEI_HEADER_NAME);
	}

	public String getProjectFromRequest(HttpServletRequest request) {

		return decryptHeader(request, PROJECT_HEADER_NAME);
	}

	private String decryptHeader(HttpServletRequest request, String headerName) {

		String data = request.getHeader(headerName);

		String base64Credentials = data.substring(headerName.length()).trim();
		try {
			AESencrpPublic aeSencrpPublic = new AESencrpPublic(Constants.KEYVALUE);
			base64Credentials = aeSencrpPublic.decrypt(base64Credentials);
		} catch (Exception e) {
			System.out.println("No se pudo desencriptar el siguiente valor: " + base64Credentials);
		}
		return new String(Base64.getDecoder().decode(base64Credentials), Charset.forName("UTF-8"));
	}

	/**
	 * clase que ayuda a tener los datos que se sacan del encabezado, solo la uso
	 * aqui para mantener la integridad de los datos.
	 * 
	 * @author ernestochavez
	 *
	 */
	private class RequestUser {
		private String user;
		private String contra;

		public RequestUser(String user, String contra) {
			this.user = user;
			this.contra = contra;
		}

		public String getUser() {
			return user;
		}

		public String getContra() {
			return contra;
		}

		@Override
		public String toString() {
			return "headerUser [user=" + user + ", contra=" + contra + "]";
		}

	}
}
