package com.dataanalytics.security;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Arrays;
import java.util.Calendar;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.binary.Base64;

public class SecurityFilter implements Filter {

	private static final String APP_ID_HEADER_KEY = "appId";

	private static final String DATE_HEADER_KEY = "date-key";

	// =================================================
	// Class Variables
	// =================================================

	public static final String SECRET_KEY = "secret-key";
	
	private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

	public static final String ACCESS_KEY = "accesskey";
	public static final String OVERRIDE_AUTHENTICATION = "overrideathentication";
	public static final String CONTENT_TYPE = "Content-Type";
	public static final String[] EXCLUDED_URLS = {"/swagger-ui/index.html", "/api-docs","/api-docs/default/basic-error-controller","/api-docs/default/diagnostic-controller","/api-docs/default/event-controller"};
	public static final String RESOURCE_URLS_REGEX = "/swagger-ui/lib/*.js";
	

	// =================================================
	// Instance Variables
	// =================================================
	private FilterConfig _config = null;

	// =================================================
	//     Instance Methods
	// ================================================

	/**
	 * This method is used by the container to initialize the filter.
	 * 
	 * @param FilterConfig
	 *            - The FilterConfig Object
	 */
	public void init(final FilterConfig iConfig) throws ServletException {
		this._config = iConfig;
	}

	/**
	 * This method is called by the container when the filter is about to be
	 * treminated.
	 */
	public void destroy() {
		_config = null;
	}

	/**
	 * This method validates whether the request is part of a valid session. If
	 * not it redirects to login page.
	 * 
	 * @param ServletRequest
	 * @param ServletResponse
	 * @param FilterChain
	 * @throws IOException
	 * @throws ServletException
	 */
	public void doFilter(final ServletRequest iRequest,final ServletResponse iResponse, final FilterChain iChain)
			throws IOException, ServletException {

		boolean aValidRequest = true;
		String aSecretCertificate = "";
		HttpServletRequest theHttpRequest = null;
		if (iRequest instanceof HttpServletRequest) {
			theHttpRequest = (HttpServletRequest) iRequest;
		} else {
			throw new ServletException("Request should be HTTP");
		}
		if (theHttpRequest != null) {
			String requestURI = theHttpRequest.getRequestURI();
			String currentDate = theHttpRequest.getHeader(DATE_HEADER_KEY);
			String appId = theHttpRequest.getHeader(APP_ID_HEADER_KEY);   // for this app we have shared secretkey 
			System.out.println(requestURI);
			if( (!requestURI.endsWith(".js") && !requestURI.endsWith(".css") && !requestURI.endsWith(".html") && !requestURI.startsWith("/kibana-3.1.2") && !Arrays.asList(EXCLUDED_URLS).contains(requestURI))){
				aSecretCertificate = theHttpRequest.getHeader(SECRET_KEY);  // this is client HMAC
				aValidRequest = SecurityFilter.isValidCertificate(aSecretCertificate, currentDate, appId);
			}else{
				aValidRequest = true;
			}

		if (aValidRequest) {
			iChain.doFilter(iRequest, iResponse);
		} else {
			throw new WebApplicationException(Response.Status.UNAUTHORIZED);
		}
		}

	}
	
	
	/**
	 * 
	 * @param aSecretCertificate
	 * @param currentDate
	 * @param appId
	 * @return
	 */
	private static boolean isValidCertificate(String aSecretCertificate, String currentDate, String appId) {
		boolean isValid = false;
		String hMacString;
		try {
			// load the privatekey for the appId
			//TODO load the private for this appid
			String privateKey = "kljlkdfldsflds";
			String data = currentDate+appId;
			hMacString = calculateRFC2104HMAC(privateKey, data);
			if(aSecretCertificate.equals(hMacString));
			isValid = true;
		} catch (InvalidKeyException e) {
				e.printStackTrace();
		} catch (SignatureException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return isValid;		
		
	}

	
	/**
	 * 
	 * @param privateKey
	 * @param data
	 * @return
	 * @throws SignatureException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 */
	private static String calculateRFC2104HMAC(String privateKey, String data) throws SignatureException, NoSuchAlgorithmException,InvalidKeyException {
		SecretKeySpec signingKey = new SecretKeySpec(privateKey.getBytes(),HMAC_SHA1_ALGORITHM);
		Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
		mac.init(signingKey);
		return Base64.encodeBase64String(mac.doFinal(data.getBytes()));
	}
	
	
	public static void main(String[] args) throws InvalidKeyException, SignatureException, NoSuchAlgorithmException{
		
	  long time  = Calendar.getInstance().getTimeInMillis();
	  
	  String privateKey = "kljlkdfldsflds";
	  System.out.println("time"+time);
	  String data = String.valueOf(time)+1341;
	  System.out.println(calculateRFC2104HMAC(privateKey, data));
	 
	}
		
		
	}
	
	
	
	
	


