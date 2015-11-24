package org.awesomeagile.webapp.config;

/*
 * ================================================================================================
 * Awesome Agile
 * %%
 * Copyright (C) 2015 Mark Warren, Phillip Heller, Matt Kubej, Linghong Chen, Stanislav Belov, Qanit Al
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------------------------------------------
 */

import org.apache.catalina.*;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardEngine;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.core.StandardService;
import org.apache.catalina.startup.Tomcat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class SslRedirectConfig {

	@Value("${ssl.redirect.port:8887}")
	private Integer sslRedirectPort;

	@Bean
	public TomcatEmbeddedServletContainerFactory tomcatFactory() {
		return new TomcatEmbeddedServletContainerFactory() {
			@Override
			protected TomcatEmbeddedServletContainer getTomcatEmbeddedServletContainer(
					Tomcat tomcat) {
				Server server = tomcat.getServer();

				Service service = new StandardService();
				service.setName("ssl-redirect-service");
				Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
				connector.setPort(sslRedirectPort);
				service.addConnector(connector);
				server.addService(service);

				Engine engine = new StandardEngine();
				service.setContainer(engine);

				Host host = new StandardHost();
				host.setName("ssl-redirect-host");
				engine.addChild(host);
				engine.setDefaultHost(host.getName());

				Context context = new StandardContext();
				context.addLifecycleListener(new Tomcat.FixContextListener());
				context.setName("ssl-redirect-context");
				context.setPath("");
				host.addChild(context);

				Wrapper wrapper = context.createWrapper();
				wrapper.setServlet(new HttpServlet() {
					@Override
					public void service(HttpServletRequest req, HttpServletResponse res)
							throws ServletException, IOException {
						UriComponentsBuilder b = UriComponentsBuilder.fromHttpUrl(req.getRequestURL().toString());
						b.scheme("https");
						b.port(null);
						res.sendRedirect(b.toUriString());
					}
				});
				wrapper.setName("ssl-redirect-servlet");
				context.addChild(wrapper);
				context.addServletMapping("/", wrapper.getName());

				return super.getTomcatEmbeddedServletContainer(tomcat);
			}
		};
	}

}
