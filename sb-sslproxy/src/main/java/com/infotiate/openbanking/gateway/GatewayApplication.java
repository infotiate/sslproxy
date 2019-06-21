package com.infotiate.openbanking.gateway;

import java.security.Security;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.web.firewall.StrictHttpFirewall;

import com.infotiate.openbanking.config.ServerConfig;

@SpringBootApplication
@EnableZuulProxy
@EnableAutoConfiguration
@ComponentScan(basePackages = "com.infotiate")
public class GatewayApplication {
	
	@Autowired
	ServerConfig config;
	
	public static void main(String[] args) {
		System.out.println("******Initializing System Properties for OCSP********");
		System.setProperty("com.sun.net.ssl.checkRevocation", "true");
		Security.setProperty("com.sun.net.ssl.checkRevocation", "true");
		Security.setProperty("ocsp.enable", "true");
		//Security.setProperty("ocsp.responderURL", "http://ocsp.godaddy.com/");
		
		Security.setProperty("com.sun.security.enableCRLDP", "true");
		System.setProperty("com.sun.security.enableCRLDP", "true");
		
		System.setProperty("jdk.tls.server.enableStatusRequestExtension", "true");
		System.setProperty("jdk.tls.client.enableStatusRequestExtension", "true");
		//System.setProperty("javax.net.debug", "ssl:handshake");
		//System.setProperty("javax.net.debug", "all");
		//System.setProperty("java.security.debug", "ssl:handshake");
		
		
		System.out.println("******Initializing System Properties for OCSP Done.********");
	    SpringApplication.run(GatewayApplication.class, args);
	  }
	
	@Bean
	public StrictHttpFirewall httpFirewall() {
	    StrictHttpFirewall firewall = new StrictHttpFirewall();
	    firewall.setAllowSemicolon(true);
	    String[] httpmethods = config.getAllowedHttpMethods();
	    firewall.setAllowedHttpMethods(Arrays.asList(httpmethods));
	    return firewall;
	}
	
}
