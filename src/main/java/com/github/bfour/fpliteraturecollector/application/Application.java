/*
 * Copyright 2016 Florian Pollak
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.github.bfour.fpliteraturecollector.application;

import java.io.IOException;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;

import com.github.bfour.fpjgui.FPJGUIManager;
import com.github.bfour.fpjgui.components.ApplicationErrorDialogue;
import com.github.bfour.fpliteraturecollector.gui.MainWindow;
import com.github.bfour.fpliteraturecollector.service.ServiceManager;

// TODO import mit einfacher text-file
// TODO evtl. request-generator tool (Kombinations-Tool)

// TODO letzter Schritt: Output nur distinct; CSV generieren

//@Configuration
@Import(FPLCNeo4jConfiguration.class)
public class Application {

	private static final String BUG_REPORT_URL = "https://github.com/bfour/FP-LiteratureCollector/issues";

	public static void main(String[] args) {

		try {

			// https://vvirlan.wordpress.com/2014/12/10/solved-caused-by-java-awt-headlessexception-when-trying-to-create-a-swingawt-frame-from-spring-boot/
			SpringApplicationBuilder builder = new SpringApplicationBuilder(
					Application.class);
			builder.headless(false);
			ConfigurableApplicationContext context = builder.run(args);

			// Neo4jResource myBean = context.getBean(Neo4jResource.class);
			// myBean.functionThatUsesTheRepo();

			// ServiceManager servMan = ServiceManager
			// .getInstance(ServiceManagerMode.TEST);
			ServiceManager servMan = context.getBean(ServiceManager.class);
			context.getAutowireCapableBeanFactory().autowireBeanProperties(
					servMan, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);

			FPJGUIManager.getInstance().initialize();

			MainWindow.getInstance(servMan).setVisible(true);

		} catch (BeanCreationException e) {
			e.printStackTrace();
			if (ExceptionUtils.getRootCause(e) instanceof IOException)
				ApplicationErrorDialogue
						.showMessage("Sorry, could not access the database.\n"
								+ "This might be because it is currently in use or because there are insufficient access rights.\n"
								+ "Try closing all running instances of this application and restart.");
			else
				ApplicationErrorDialogue.showDefaultMessage(e, BUG_REPORT_URL);
		} catch (Exception e) {
			e.printStackTrace();
			ApplicationErrorDialogue.showDefaultMessage(e, BUG_REPORT_URL);
		}

	}

}
