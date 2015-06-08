package com.github.bfour.fpliteraturecollector.application;

/*
 * -\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\-
 * FP-LiteratureCollector
 * =================================
 * Copyright (C) 2014 - 2015 Florian Pollak
 * =================================
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
 * -///////////////////////////////-
 */

import javax.swing.JOptionPane;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.github.bfour.fpjgui.FPJGUIManager;
import com.github.bfour.fpliteraturecollector.gui.MainWindow;
import com.github.bfour.fpliteraturecollector.service.ServiceManager;
import com.github.bfour.fpliteraturecollector.service.ServiceManager.ServiceManagerMode;

// TODO import mit einfacher text-file
// TODO evtl. request-generator tool (Kombinations-Tool)

// TODO letzter Schritt: Output nur distinct; CSV generieren

//@Configuration
@Import(MyNeo4jConfiguration.class)
public class Application {

	public static void main(String[] args) {

		try {

			// https://vvirlan.wordpress.com/2014/12/10/solved-caused-by-java-awt-headlessexception-when-trying-to-create-a-swingawt-frame-from-spring-boot/
			SpringApplicationBuilder builder = new SpringApplicationBuilder(
					Application.class);
			builder.headless(false);
			ConfigurableApplicationContext context = builder.run(args);
			
			// ConfigurableApplicationContext context;
			// context = new ClassPathXmlApplicationContext("SpringConfig.xml");

			// Neo4jResource myBean = context.getBean(Neo4jResource.class);
			// myBean.functionThatUsesTheRepo();

			// ServiceManager servMan = ServiceManager
			// .getInstance(ServiceManagerMode.TEST);
			ServiceManager servMan = context.getBean(ServiceManager.class);
			context.getAutowireCapableBeanFactory().autowireBeanProperties(
					servMan, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);

			FPJGUIManager.getInstance().initialize();

			MainWindow.getInstance(servMan).setVisible(true);

		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane
					.showMessageDialog(
							null,
							"Sorry, the application cannot continue and will terminate.\n\n"
									+ "This might be because the application is not configured properly or the database is unavailable.\n"
									+ "Reinstalling the application might solve this problem.\n"
									+ "Please report this to the developer at https://github.com/bfour/FP-LiteratureCollector/issues.\n\n"
									+ "Details: "
									+ (e.getMessage() == null ? e : e
											.getMessage()), "Error",
							JOptionPane.ERROR_MESSAGE);
		}

	}

}
