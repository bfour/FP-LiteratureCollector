package com.github.bfour.fpliteraturecollector.service;

/*
 * -\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\-
 * FP-LiteratureCollector
 * =================================
 * Copyright (C) 2015 Florian Pollak
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


import java.io.IOException;
import java.io.StringWriter;

import org.python.util.PythonInterpreter;

public class Utils {

	@Deprecated
	public static boolean pythonIsInstalled() throws IOException {
		
		PythonInterpreter interpreter = new PythonInterpreter();
		PythonInterpreter.initialize(System.getProperties(),
				System.getProperties(), new String[] { "-c 1",
						"--author \"albert einstein\"",
						"--phrase \"quantum theory\"" });
		String scriptname = "scholar.py-master/scholar.py";

		StringWriter out = new StringWriter();
		interpreter.setOut(out);
		interpreter.execfile(scriptname);
		
		System.out.println(out.toString());
		
		return false;
		
//		CommandLine cmdLine = new CommandLine("python");
//		cmdLine.addArgument("--version");
//		
//		DefaultExecutor executor = new DefaultExecutor();
//		executor.setExitValue(1);
//		
//		ExecuteWatchdog watchdog = new ExecuteWatchdog(4861);
//		executor.setWatchdog(watchdog);
//		
//	    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//	    PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
//	    executor.setStreamHandler(streamHandler);
//		
//		try {
//			int exitValue = executor.execute(cmdLine);
//			System.out.println(outputStream.toString());
//			return true; // TODO
//		} catch (ExecuteException e) {
//			return false;
//		}
		
	}
}
