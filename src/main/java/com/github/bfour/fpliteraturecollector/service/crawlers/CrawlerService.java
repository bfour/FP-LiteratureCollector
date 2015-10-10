package com.github.bfour.fpliteraturecollector.service.crawlers;

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


import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.github.bfour.fpjcommons.services.ServiceException;
import com.github.bfour.fpjcommons.services.CRUD.CRUDService;
import com.github.bfour.fpjcommons.services.CRUD.DataIterator;
import com.github.bfour.fpjcommons.services.CRUD.DataIteratorWrapper;
import com.github.bfour.fpliteraturecollector.service.crawlers.implementations.EpopACMCrawler;
import com.github.bfour.fpliteraturecollector.service.crawlers.implementations.EpopMSAcademicCrawler;
import com.github.bfour.fpliteraturecollector.service.crawlers.implementations.EpopScholarCrawler;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class CrawlerService implements CRUDService<Crawler> {

	private static CrawlerService instance;
	private BiMap<String,Crawler> identifierInstanceMap;
	
	private CrawlerService() {
		this.identifierInstanceMap = HashBiMap.create();
		this.identifierInstanceMap.put("epop Google Scholar", new EpopScholarCrawler());
		this.identifierInstanceMap.put("epop Microsoft Academic", new EpopMSAcademicCrawler());
		this.identifierInstanceMap.put("epop ACM Digital Library", new EpopACMCrawler());
	}
	
	public static CrawlerService getInstance() {
		if (instance == null) instance = new CrawlerService();
		return instance;
	}

	public Set<Crawler> getAvailableCrawlers() {
		return identifierInstanceMap.values();
	}
	
	public Crawler getCrawlerForIdentifier(String identifier) {
		return identifierInstanceMap.get(identifier);
	}
	
	public String getIdentifierForCrawler(Crawler crawler) {
		return identifierInstanceMap.inverse().get(crawler);
	}
	
	@Override
	public List<Crawler> getAll() throws ServiceException {
		return new ArrayList<>(getAvailableCrawlers());
	}

	@Override
	public DataIterator<Crawler> getAllByStream() throws ServiceException {
		return new DataIteratorWrapper<Crawler>(getAll().iterator());
	}

	@Override
	public Crawler get(Crawler entity) throws ServiceException {
		return entity;
	}

	@Override
	public Crawler create(Crawler entity) throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(Crawler entity) throws ServiceException {
		// TODO Auto-generated method stub
	}

	@Override
	public Crawler update(Crawler oldEntity, Crawler newEntity)
			throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean exists(Crawler entity) throws ServiceException {
		return identifierInstanceMap.containsKey(entity);
	}
	
}
