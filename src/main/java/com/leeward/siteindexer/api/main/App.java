package com.leeward.siteindexer.api.main;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.leeward.siteindexer.api.services.SiteIndexService;

public class App {

	private static Logger log = LoggerFactory.getLogger(App.class);
	
	public static void main(String[] args) {
		String site = "";
		long start = System.currentTimeMillis();
		try {
			if (args.length != 1) {
				System.out.println("1 input required: what is the url to index. enter in format www.sitename.com");
				System.exit(0);			
			}
			site = args[0];
			if (StringUtils.isNotBlank(site)) {
				SiteIndexService service = new SiteIndexService(site);
				service.execute();
			}
		} catch (Exception e) {
			log.error("Exception in main: " + e.getMessage(), e);
		}
		log.info("Finished indexing ["+site+"]. Process took " + (System.currentTimeMillis() - start) + "ms.");
	}

}
