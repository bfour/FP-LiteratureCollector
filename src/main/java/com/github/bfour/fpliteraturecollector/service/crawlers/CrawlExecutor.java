package com.github.bfour.fpliteraturecollector.service.crawlers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.SwingWorker;

import org.epop.dataprovider.DataProvider;

import com.github.bfour.fpjcommons.services.ServiceException;
import com.github.bfour.fpliteraturecollector.domain.Literature;
import com.github.bfour.fpliteraturecollector.domain.Query;
import com.github.bfour.fpliteraturecollector.domain.SupportedSearchEngine;
import com.github.bfour.fpliteraturecollector.domain.Query.QueryStatus;
import com.github.bfour.fpliteraturecollector.domain.builders.QueryBuilder;
import com.github.bfour.fpliteraturecollector.service.QueryService;
import com.github.bfour.fpliteraturecollector.service.ServiceManager;
import com.github.bfour.fpliteraturecollector.service.abstraction.BackgroundWorker;
import com.github.bfour.fpliteraturecollector.service.abstraction.BackgroundWorker.BackgroundWorkerState;

public class CrawlExecutor extends BackgroundWorker {

	private class CrawlerWorker extends SwingWorker<Void, Void> {

		private List<Exception> exceptions;
		private QueryService qServ;
		private List<Exception> errors;

		public CrawlerWorker(ServiceManager servMan) {
			exceptions = new LinkedList<Exception>();
			this.qServ = servMan.getQueryService();
			this.errors = new ArrayList<Exception>();
		}

		@Override
		protected Void doInBackground() {
			Query topQuery;
			try {
				while ((topQuery = qServ.getByQueuePosition(1)) != null) {
					QueryBuilder builder = new QueryBuilder(topQuery);
					builder.setStatus(QueryStatus.CRAWLING);
					topQuery = qServ.update(topQuery, builder.getObject());
					// TODO
				}
			} catch (ServiceException e) {
				exceptions.add(e);
			}
			return null;
		}

		@Override
		protected void done() {
			super.done();
			finish();
		}

		public List<Exception> getErrors() {
			return errors;
		}

	}

	private static CrawlExecutor instance;
	private ServiceManager servMan;

	private CrawlExecutor(ServiceManager servMan) {
		this.servMan = servMan;
	}

	public static CrawlExecutor getInstance(ServiceManager servMan) {
		if (instance == null)
			instance = new CrawlExecutor(servMan);
		return instance;
	}

	public synchronized void start() {
		setState(BackgroundWorkerState.RUNNING);
		new CrawlerWorker(servMan);
	}

	public synchronized void rerunAll() {

	}

	@Override
	public synchronized void abort() {
		// TODO Auto-generated method stub

	}

	@Override
	protected synchronized void finish() {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Exception> getErrors() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SupportedSearchEngine> getSearchEnginesBeingAccessed() {
		// TODO Auto-generated method stub
		return null;
	}

}
