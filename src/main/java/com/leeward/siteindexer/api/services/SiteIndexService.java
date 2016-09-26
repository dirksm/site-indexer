package com.leeward.siteindexer.api.services;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.leeward.siteindexer.api.constants.AppConstants;
import com.leeward.siteindexer.api.dao.SitePagesDAO;
import com.leeward.siteindexer.api.models.URLArrayBlockingQueue;
import com.leeward.siteindexer.api.threads.IndexerThread;
import com.leeward.siteindexer.api.threads.SearchThread;

public class SiteIndexService {

	private static Logger log = LoggerFactory.getLogger(SiteIndexService.class);

	private int threadCount = 10;
	URLArrayBlockingQueue<String> urls = null;
	URLArrayBlockingQueue<String> inurls = null;
	private String siteName = "";
	
	public SiteIndexService(String siteName) {
		super();
		this.siteName = siteName;
	}
	
	public int execute() {
		int success = 0;
		try {
			// Populate the queue for urls
			urls = new URLArrayBlockingQueue<String>(AppConstants.READER_QUEUE_SIZE);
			inurls = new URLArrayBlockingQueue<String>(AppConstants.READER_QUEUE_SIZE);
			
			SitePagesDAO dao = new SitePagesDAO();
			dao.deleteSitepages(siteName);

			// Submit thread to populate the url's
			ExecutorService fillES = Executors.newFixedThreadPool(threadCount);
			fillES.submit(new IndexerThread(urls, inurls, "http://"+siteName));
			
			// Terminate the url population thread
			fillES.shutdown();
			
			// Wait until the indexer thread is complete
			while (!fillES.isTerminated()) {
				Thread.sleep(2000);
			}
						
			// Set the end processing flag in the input queue
			urls.offer(AppConstants.END_PROCESSING, 365, TimeUnit.DAYS);

			// Submit threads for searching the url's
			ExecutorService es = Executors.newFixedThreadPool(threadCount);
			for (int i = 0; i < threadCount; i++) {
				es.submit(new SearchThread(siteName, urls));
			}
			
			// Terminate the search threads
			es.shutdown();
			
			// Wait until the search threads are complete
			while (!es.isTerminated()) {
				Thread.sleep(2000);
			}
			
		
		} catch (Exception e) {
			// TODO: handle exception
		}
		return success;
	}
}
