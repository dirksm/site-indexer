package com.leeward.siteindexer.api.threads;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.leeward.siteindexer.api.constants.AppConstants;
import com.leeward.siteindexer.api.models.URLArrayBlockingQueue;
import com.leeward.siteindexer.api.util.AppUtil;

public class IndexerThread implements Runnable {

	private static Logger log = LoggerFactory.getLogger(IndexerThread.class);
	
	private URLArrayBlockingQueue<String> urls = new URLArrayBlockingQueue<String>(AppConstants.READER_QUEUE_SIZE);
	private URLArrayBlockingQueue<String> queue = null;
	private int count = 0;
	private String prefix = "";
    
	
	
	public IndexerThread() {
		this.count = 0;
	}
	
	public IndexerThread(URLArrayBlockingQueue<String> q, URLArrayBlockingQueue<String> r, String url) {
		this();
		this.queue = q;
		this.urls = r;
		this.prefix = url;
	}
	
	@Override
	public void run() {
		long s = System.currentTimeMillis();
		log.info("Indexing urls");
		String currentUrl = this.prefix;
		try {
			this.queue.offer(currentUrl);
	        crawl(currentUrl);
	        while (!this.urls.isEmpty()) {
				currentUrl = this.urls.poll();
		        crawl(currentUrl);
		        log.debug("Size of urls queue: " + this.urls.size());
		        log.debug("Size of queue: " + this.queue.getSize());
			}
		} catch(IOException ioe) {
			log.error("IOException searching pages: " + ioe.getMessage(), ioe);
		} catch (Exception e) {
			log.error("Exception searching pages: " + e.getMessage(), e);
		}
		log.info("finished indexing... took " + (System.currentTimeMillis()-s) + "ms to complete.");
	}
	
	private void crawl(String url) throws IOException {
		Connection connection = null;
        Document htmlDocument = null;
        Response resp = null;
        if (AppUtil.isMicrosoftFile(url) || url.endsWith(".pdf")) {
        	this.queue.offer(url);
		} else {
			try {
				connection = Jsoup.connect(url).userAgent(AppConstants.USER_AGENT);
		        htmlDocument = connection.get();
		        resp = connection.response();
		        if(resp.statusCode() == 200 && resp.contentType().contains("text/html"))
		        {
		        	populateURLs(htmlDocument);
		        } else {
		        	log.error("Invalid response while indexing url: " + url);
		        }
			} catch (UnsupportedMimeTypeException umte) {
				log.error("url["+url+"]: " + umte.getMessage(), umte);
			} catch (SocketTimeoutException ste) {
				log.error("Exception connecting to '"+url+"': " + ste.getMessage(),ste);
			} catch (Exception e) {
				log.error("Exception crawling url '"+url+"': "+e.getMessage(),e);
			}
		}
	}

	
	private List<String> getLinks(Document htmlDocument) {
		List<String> links = new ArrayList<String>();
		Elements linksOnPage = htmlDocument.select("a[href]");
        for(Element link : linksOnPage)
        {
            links.add(link.absUrl("href"));
        }		
        return links;
	}

	private void populateURLs(Document htmlDocument) {
    	List<String> urlLinks = getLinks(htmlDocument);
    	for (String urlLink : urlLinks) {
    		if (urlLink.startsWith(this.prefix) && isValidURI(urlLink)) {
    			String link = removeAnchorTags(urlLink);
        		this.urls.offer(link);
        		this.queue.offer(link);
			}
		}
	}

	private String removeAnchorTags(String uriStr) {
		return uriStr.indexOf("#")!=-1?uriStr.substring(0, uriStr.indexOf("#")):uriStr;
	}
	
	private boolean isValidURI(String uriStr) {
	    try {
	      URI uri = new URI(uriStr);
	      return true;
	    }
	    catch (URISyntaxException e) {
	        return false;
	    }
	}	

	public int getCount() {
		return count;
	}

}
