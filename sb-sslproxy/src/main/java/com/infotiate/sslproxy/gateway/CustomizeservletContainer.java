package com.infotiate.sslproxy.gateway;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

@Component
public class CustomizeservletContainer implements
  WebServerFactoryCustomizer<TomcatServletWebServerFactory> {
 
    @Override
    public void customize(TomcatServletWebServerFactory factory) {
    	factory.addConnectorCustomizers(new TomcatConnectorCustomizer() {
            @Override
            public void customize(Connector connector) {
				/*
				 * connector.setPort(serverPort); connector.setSecure(true);
				 * connector.setScheme("https"); connector.setAttribute("keyAlias", "tomcat");
				 * connector.setAttribute("keystorePass", "password"); try {
				 * connector.setAttribute("keystoreFile",
				 * ResourceUtils.getFile("src/ssl/tomcat.keystore").getAbsolutePath()); } catch
				 * (FileNotFoundException e) { throw new
				 * IllegalStateException("Cannot load keystore", e); }
				 */                
            	//connector.setAttribute("SSLVerifyClient", "require");
            	//connector.setAttribute("SSLVerifyDepth", "10");    
            	
            	
            	//connector.findSslHostConfigs()[0].setCertificateVerification("true");
            	//SSLVerifyClient
            }
        });
    }
}