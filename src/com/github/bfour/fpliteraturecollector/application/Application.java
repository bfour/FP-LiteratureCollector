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




import java.util.List;

import javax.swing.JOptionPane;

import com.github.bfour.fpjcommons.services.ServiceException;
import com.github.bfour.fpliteraturecollector.domain.Person;
import com.github.bfour.fpliteraturecollector.service.PersonService;
import com.github.bfour.fpliteraturecollector.service.ServiceManager;
import com.github.bfour.fpliteraturecollector.service.ServiceManager.ServiceManagerMode;

public class Application {

	public static void main(String[] args) {
		
		try {
			ServiceManager servMan = ServiceManager.getInstance(ServiceManagerMode.DEFAULT);
			
			// test
			PersonService personServ = servMan.getPersonService();
			
			personServ.create(new Person("Miezi", "Katz"));
			
			List<Person> list = personServ.getAll();
			for (Person p : list) {
				System.out.println(p);
			}
			
		} catch (ServiceException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Sorry, the application cannot continue and will terminate.\n\n"
					+ "This might be because the application is not configured properly or the database is unavailable. "
					+ "Reinstalling the application might solve this problem.\n"
					+ "Please report this to the developer at .\n"
					+ "Details: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
		
	}

}
