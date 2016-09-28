package com.leeward.siteindexer.api.threads;

import java.net.SocketTimeoutException;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.leeward.siteindexer.api.constants.AppConstants;
import com.leeward.siteindexer.api.dao.SitePagesDAO;
import com.leeward.siteindexer.api.models.SitePagesModel;
import com.leeward.siteindexer.api.models.URLArrayBlockingQueue;

public class SearchThread implements Runnable {

	private static Logger log = LoggerFactory.getLogger(SearchThread.class);
	
	protected URLArrayBlockingQueue<String> urls = null;	
	private String siteName;

	public SearchThread(String s, URLArrayBlockingQueue<String> i) {
		this.urls = i;
		this.siteName = s;
	}
	
	@Override
	public void run() {
		long s = System.currentTimeMillis();
		log.debug("Searching urls");
		SitePagesModel srm = null;
		String url = "";
		SitePagesDAO dao = new SitePagesDAO();
		try {
			
			while(true) {
				url = this.urls.poll();
				if (StringUtils.isNotBlank(url)) {
					// If the queue is empty, break from this thread.
					if (AppConstants.END_PROCESSING.equals(url)) {
						this.urls.offer(url);
						break;
					}
					log.debug("Retrieving url '"+url+"'");
					Connection connection = Jsoup.connect(url).userAgent(AppConstants.USER_AGENT);
			        Document htmlDocument = connection.get();
			        Response resp = connection.response();
			        log.debug("response code: " + resp.statusCode() + "; content-type: " + resp.contentType());
			        if(resp.statusCode() == 200 && resp.contentType().contains("text/html"))
			        {
			        	srm = searchPage(url, htmlDocument);
			        	if (srm != null && StringUtils.isNotBlank(srm.getText())) {
			        		dao.addSitepages(srm);
						}
			        }
				}
			}
			
		} catch (UnsupportedMimeTypeException umte) {
			
		} catch (SocketTimeoutException ste) {
			log.error("Exception connecting to '"+url+"': " + ste.getMessage(),ste);
		} catch (Exception e) {
			log.error("Exception crawling url '"+url+"': "+e.getMessage(),e);
		}
		log.debug("finished search... took " + (System.currentTimeMillis()-s) + "ms to complete.");
	}

	private SitePagesModel searchPage(String url, Document htmlDocument) {
		SitePagesModel result = null;
		htmlDocument.select("a").remove().text();
		String bodyText = htmlDocument.body().text();
    	result = new SitePagesModel();
    	result.setSiteUrl(siteName);
    	result.setTitle(htmlDocument.title());
    	result.setPageUrl(url);
    	result.setText(bodyText);
		return result;
	}
	
}
