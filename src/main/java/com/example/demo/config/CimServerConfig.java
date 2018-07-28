package com.example.demo.config;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.demo.handler.BindHandler;
import com.example.demo.handler.SessionClosedHandler;
import com.example.demo.push.DefaultMessagePusher;
import com.example.demo.push.SystemMessagePusher;
import com.farsunset.cim.sdk.server.handler.CIMNioSocketAcceptor;
import com.farsunset.cim.sdk.server.handler.CIMRequestHandler;
import com.farsunset.cim.sdk.server.session.DefaultSessionManager;

@Configuration
public class CimServerConfig {

	@Bean
	public DefaultSessionManager CIMSessionManager() {
		return new DefaultSessionManager();
	}
	
	@Bean
	public DefaultMessagePusher messagePusher() {
		DefaultMessagePusher messagePusher = new DefaultMessagePusher();
		messagePusher.setSessionManager(CIMSessionManager());
		return messagePusher;
	}
	
	@Bean
	public SystemMessagePusher systemMessagePusher() {
		SystemMessagePusher systemMessagePusher = new SystemMessagePusher();
		systemMessagePusher.setSessionManager(CIMSessionManager());
		return systemMessagePusher;
	}
	
	@Autowired
	private BindHandler bindHandler;
	
	@Autowired
	private SessionClosedHandler sessionClosedHandler;
	
	@Bean(initMethod="bind", destroyMethod="unbind")
	public CIMNioSocketAcceptor CIMNioAcceptor() {
		
		CIMNioSocketAcceptor CIMNioAcceptor = new CIMNioSocketAcceptor();
		CIMNioAcceptor.setPort(23456);
		HashMap<String, CIMRequestHandler> handlers = new HashMap<>();
		
		handlers.put("client_bind", bindHandler);
		handlers.put("client_cimsession_closed", sessionClosedHandler);
		
		CIMNioAcceptor.setHandlers(handlers);
		
		
		
		return CIMNioAcceptor;
	}
}
