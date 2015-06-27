/*******************************************************************************
 * Copyright (c) 2012 fm&selab.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     fm&selab - initial API and implementation
 ******************************************************************************/
package org.epop.dataprovider;

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
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.List;

import com.github.bfour.fpjcommons.services.DatalayerException;
import com.github.bfour.fpliteraturecollector.domain.Literature;

/**
 * given a query return the data
 * 
 * @author garganti, modified by Florian Pollak
 * 
 */
public abstract class DataProvider {

	// to be shown when a short info is needed
	public abstract String getDescription();

	/**
	 * run the query and returns the papers
	 * 
	 * @param htmlParams
	 *            the parameters passed (eg. "query=meow%20meow" or
	 *            "as_q=numbers&as_sauthors=turing")
	 * @return the papers, never null
	 * @throws DataUnavailableException
	 * @throws DatalayerException
	 * @throws  
	 * @throws URISyntaxException 
	 * @throws MalformedURLException 
	 */
	public final List<Literature> runQuery(String htmlParams, int pageTurnLimit)
			throws DataUnavailableException, DatalayerException, URISyntaxException {
		try {
			Reader r = getHTMLDoc(htmlParams, pageTurnLimit);
			if (r == null)
				throw new DataUnavailableException("cannot connect");
			List<Literature> result = parsePage(r);
			if (result == null)
				throw new DataUnavailableException(
						"parser failed to parse file");
			return result;
		} catch (DataUnavailableException | IOException e) {
			throw new DataUnavailableException(e.getMessage());
		}

	}

	// get the HTM Document - return null in case of error
	protected abstract Reader getHTMLDoc(String htmlParams, int pageTurnLimit)
			throws URISyntaxException, MalformedURLException, IOException;

	// parse it - return null in case of error
	protected abstract List<Literature> parsePage(Reader r)
			throws DatalayerException;

}
