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

import java.io.Reader;
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
	 */
	public final List<Literature> runQuery(String htmlParams, int pageTurnLimit)
			throws DataUnavailableException, DatalayerException {
		try {
			Reader r = getHTMLDoc(htmlParams, pageTurnLimit);
			if (r == null)
				throw new DataUnavailableException("cannot connect");
			List<Literature> result = parsePage(r);
			if (result == null)
				throw new DataUnavailableException(
						"parser failed to parse file");
			return result;
		} catch (DataUnavailableException e) {
			throw new DataUnavailableException(e.getMessage());
		}

	}

	// get the HTM Document - return null in case of error
	protected abstract Reader getHTMLDoc(String htmlParams, int pageTurnLimit);

	// parse it - return null in case of error
	protected abstract List<Literature> parsePage(Reader r)
			throws DatalayerException;

}
