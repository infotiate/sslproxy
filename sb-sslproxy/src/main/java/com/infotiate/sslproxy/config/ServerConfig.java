package com.infotiate.sslproxy.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ServerConfig {

	@Value("${server.ssl.trust-store-password}")
	private String trustStorePassword;

	@Value("${server.ssl.trust-store}")
	private String trustStoreFilePath;

	@Value("${server.ssl.fwdclientcertifiate}")
	private boolean forwardClientCertificate;

	@Value("${server.ssl.fwdclientcertifiateformat}")
	private String forwardClientCertificateFormat;
	
	@Value("${server.ssl.enableocspcheck}")
	private boolean enableOcspCheck;	

	@Value("${server.ssl.certificatelinelength}")
	private int certificateLineLength;
	
	@Value("${server.ssl.validateleafcertonly}")
	private boolean validateLeafCertificateOnly;
	
	@Value("${server.ssl.ignoreocspcheckresults}")
	private boolean ignoreOCSPStatusCheckResults;

	@Value("${server.ssl.failurestatuscode}")
	private int failureStatusCode;

	@Value("${server.ssl.failuremessage}")
	private String failuremessage;
	
	@Value("${server.allowedhttpmethods}")
	private String[] allowedHttpMethods;

	public String[] getAllowedHttpMethods() {
		return allowedHttpMethods;
	}

	public String getTrustStorePassword() {
		return trustStorePassword;
	}

	public String getTrustStoreFilePath() {
		return trustStoreFilePath;
	}

	public boolean isForwardClientCertificate() {
		return forwardClientCertificate;
	}

	public String getForwardClientCertificateFormat() {
		return forwardClientCertificateFormat;
	}

	public boolean isEnableOcspCheck() {
		return enableOcspCheck;
	}

	public int getCertificateLineLength() {
		return certificateLineLength;
	}

	public boolean isValidateLeafCertificateOnly() {
		return validateLeafCertificateOnly;
	}

	public boolean isIgnoreOCSPStatusCheckResults() {
		return ignoreOCSPStatusCheckResults;
	}

	public int getFailureStatusCode() {
		return failureStatusCode;
	}

	public String getFailuremessage() {
		return failuremessage;
	}


	
	

}
