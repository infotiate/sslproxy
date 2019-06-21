package com.infotiate.openbanking.filter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.cert.CertPath;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertPathValidator;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.PKIXParameters;
import java.security.cert.PKIXRevocationChecker;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.EnumSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.infotiate.openbanking.config.ServerConfig;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

@Component
public class CertificateCheckingBean extends ZuulFilter {
	private static final String REQUEST_ATTRIBUTE_X509_CERTIFICATE = "javax.servlet.request.X509Certificate";

	public static final String BEGIN_CERT = "-----BEGIN CERTIFICATE-----";
	public static final String END_CERT = "-----END CERTIFICATE-----";
	public final static String LINE_SEPARATOR = System.getProperty("line.separator");

	@Autowired
	ServerConfig config;

	private final Logger m_log = LoggerFactory.getLogger(getClass());

	private KeyStore rootCAs = null;
	
	private void checkClientCertificate(HttpServletRequest request) throws ZuulException {
		if (!config.isForwardClientCertificate()) {
			m_log.info("Client certificate chain forwarding disabled");
			return;
		}

		X509Certificate[] certChain = (X509Certificate[]) request.getAttribute(REQUEST_ATTRIBUTE_X509_CERTIFICATE);
		if (certChain == null || certChain.length == 0) {
			m_log.info("No client certificate provided - not checking validity");
		} else {
			m_log.info("Client certificate[" + certChain.length + "]");
			verifyCertificateChain(certChain);
			int index = 0;
			for (X509Certificate cert : certChain) {
				m_log.info(cert.getSubjectDN().toString());
				String pem = getPEMCert(cert);
				m_log.info(pem);
				RequestContext.getCurrentContext().addZuulRequestHeader("x-client-cert-"+index++, pem);
			}
		}
	}

	public String getPEMCert(final Certificate certificate) {
		final Base64.Encoder encoder = Base64.getMimeEncoder(config.getCertificateLineLength(),
				LINE_SEPARATOR.getBytes());

		byte[] rawCrtText;
		try {
			rawCrtText = certificate.getEncoded();
			final String encodedCertText;
			if (config.getForwardClientCertificateFormat().equalsIgnoreCase("pem")) {
				
				if (config.getCertificateLineLength() > 0)
					encodedCertText = BEGIN_CERT +LINE_SEPARATOR+ new String(encoder.encode(rawCrtText)) + LINE_SEPARATOR + END_CERT;
				else
					encodedCertText = BEGIN_CERT + new String(encoder.encode(rawCrtText)) + END_CERT;
					
			} else {
				encodedCertText = new String(encoder.encode(rawCrtText));
			}
			final String prettified_cert = encodedCertText;
			return prettified_cert;
		} catch (CertificateEncodingException e) {
			// TODO Auto-generated catch block
			m_log.error("Certificate encoding error!", e);
			return "";
		}

	}

	private KeyStore getRootCAs() throws ZuulException 
	{
		if (rootCAs != null)
			return rootCAs;
		
		
		File cacertsFile;
		try {
			cacertsFile = new ClassPathResource("trust.jks").getFile();
		

		String cacertsPassword = config.getTrustStorePassword();
		// read the certificate from the file
		m_log.info("Loading root CAs...###############");
		// read the cacerts keystore to check signature
	
		rootCAs = KeyStore.getInstance(KeyStore.getDefaultType());
		rootCAs.load(new FileInputStream(cacertsFile), cacertsPassword.toCharArray());
		m_log.info("Loaded " + rootCAs.size() +" CAs");
		return rootCAs;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new ZuulException(e,"Internal Server Error", 500, "UNKNOWN");
		}

	}
	
	private void verifyCertificateChain(X509Certificate[] certChain) throws ZuulException {

		if (!config.isEnableOcspCheck()) {
			m_log.info("OCSP check for client certificate is disabled");
			return;
		}

		try {
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			KeyStore cacerts = getRootCAs();
			// set security options to ocsp validation (only ocsp)
			CertPathBuilder cpb = CertPathBuilder.getInstance("PKIX");
			PKIXRevocationChecker rc = (PKIXRevocationChecker) cpb.getRevocationChecker();
			rc.setOptions(EnumSet.of(PKIXRevocationChecker.Option.NO_FALLBACK));
			if (config.isValidateLeafCertificateOnly())
				rc.setOptions(EnumSet.of(PKIXRevocationChecker.Option.ONLY_END_ENTITY));

			List<X509Certificate> certs = new ArrayList<X509Certificate>();
			certs.addAll(Arrays.asList(certChain));
			CertPath certPath = cf.generateCertPath(certs);
			CertPathValidator cpv = CertPathValidator.getInstance("PKIX");
			PKIXParameters params = new PKIXParameters(cacerts);
			params.addCertPathChecker(rc);
			// params.setRevocationEnabled(false);
			m_log.info("Performing PKIX validation...");
			PKIXCertPathValidatorResult cpvResult = (PKIXCertPathValidatorResult) cpv.validate(certPath, params);
			m_log.info("Result: OK");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			m_log.error("Error while validating client certificate path", e);
			throw new ZuulException(e,config.getFailuremessage(), config.getFailureStatusCode(), "");
		}

	}

	@Override
	public boolean shouldFilter() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public Object run() throws ZuulException {
		// TODO Auto-generated method stub
		m_log.info("*****Executing Zuul Filter******");
		RequestContext context = RequestContext.getCurrentContext();
		HttpServletRequest request = context.getRequest();
		checkClientCertificate(request);
		return null;
	}

	@Override
	public String filterType() {
		// TODO Auto-generated method stub
		return "pre";
	}

	@Override
	public int filterOrder() {
		// TODO Auto-generated method stub
		return 0;
	}

}
