package com.leeward.siteindexer.api.threads;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.poi.hslf.extractor.PowerPointExtractor;
import org.apache.poi.hssf.extractor.ExcelExtractor;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
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
import com.leeward.siteindexer.api.util.AppUtil;
import com.leeward.siteindexer.api.util.XParsers;

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
		log.info("Searching urls");
		SitePagesModel srm = null;
		String url = "";
		SitePagesDAO dao = new SitePagesDAO();
		try {
			
			while(true) {
				url = this.urls.poll();
				log.debug("polling url: " + url);
				if (StringUtils.isNotBlank(url)) {
					// If the queue is empty, break from this thread.
					if (AppConstants.END_PROCESSING.equals(url)) {
						this.urls.offer(url, 365, TimeUnit.DAYS);
						break;
					}
					log.debug("Retrieving url '"+url+"'");
					if (url.endsWith(".pdf")) {
						srm = searchPdf(url);
			        	if (srm != null && StringUtils.isNotBlank(srm.getText())) {
			        		dao.addSitepages(srm);
						}
					} else if (AppUtil.isMicrosoftFile(url)) {
						srm = searchMicrosoftFile(url);
			        	if (srm != null && StringUtils.isNotBlank(srm.getText())) {
			        		dao.addSitepages(srm);
						}
					} else {
						Connection connection = null;
				        Document htmlDocument = null;
				        Response resp = null;
						try {
							connection = Jsoup.connect(url).userAgent(AppConstants.USER_AGENT);
					        htmlDocument = connection.get();
					        resp = connection.response();
					        if(resp.statusCode() == 200 && resp.contentType().contains("text/html"))
					        {
					        	srm = searchPage(url, htmlDocument);
					        	if (srm != null && StringUtils.isNotBlank(srm.getText())) {
					        		dao.addSitepages(srm);
								}
					        }
						} catch (UnsupportedMimeTypeException umte) {
							log.error("url["+url+"]: " + umte.getMessage(), umte);
						} catch (SocketTimeoutException ste) {
							log.error("Exception connecting to '"+url+"': " + ste.getMessage(),ste);
						}
					}
				}
			}
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
    	result.setSiteUrl(this.siteName);
    	result.setTitle(htmlDocument.title());
    	result.setPageUrl(url);
    	result.setText(bodyText);
		return result;
	}
	
	public String parsePdfFile(String url) {
        String content = "";
        COSDocument cd = null;
        try {
            URL uri = new URL(url);
            BufferedInputStream bi = new BufferedInputStream(uri.openStream());
            PDFParser parser = new PDFParser(bi);
            parser.parse();
            cd = parser.getDocument();
            PDDocumentInformation info = parser.getPDDocument().getDocumentInformation();
            log.debug("pdf title: "+info.getTitle());
            PDFTextStripper stripper = new PDFTextStripper();
            content = stripper.getText(new PDDocument(cd));
		} catch (IOException ioe) {
			log.error("Error parsing ["+url+"]: "+ ioe.getMessage(),ioe);
			content = "";
		} finally {
			if (cd != null) {
				try {
					cd.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
				}
			}
		}
        content = StringUtils.replace(content, "\n", " ");
        return content;
    }
	
	private SitePagesModel searchPdf(String url) throws IOException {
		String title = url.substring(url.lastIndexOf("/")+1, url.length());
		log.debug("searching pdf at url ["+url+"] and title ["+title+"]");
		SitePagesModel result = new SitePagesModel();
    	result.setSiteUrl(this.siteName);
    	result.setTitle(title);
    	result.setPageUrl(url);
    	result.setText(parsePdfFile(url));
    	return result;
	}
	
	
	private SitePagesModel searchMicrosoftFile(String url) {
		String title = url.substring(url.lastIndexOf("/")+1, url.length());
		log.debug("searching microsoft file at url ["+url+"] and title ["+title+"]");
		SitePagesModel result = new SitePagesModel();
    	result.setSiteUrl(this.siteName);
    	result.setTitle(title);
    	result.setPageUrl(url);
    	result.setText(parseMicrosoftFile(url));
    	return result;
	}
	
	private String parseMicrosoftFile(String url) {
		String content = "";
		try {
	        URL uri = new URL(url);
	        BufferedInputStream bi = new BufferedInputStream(uri.openStream());
	        POIFSFileSystem fs = null;
			if (url.endsWith(".xls")) { //if the file is excel file
				fs = new POIFSFileSystem(bi);
	            ExcelExtractor ex = new ExcelExtractor(fs);
	            content =  ex.getText(); //returns text of the excel file
	        } else if (url.endsWith(".ppt")) { //if the file is power point file
				fs = new POIFSFileSystem(bi);
	            PowerPointExtractor extractor = new PowerPointExtractor(fs);
	            content =  extractor.getText(); //returns text of the power point file
	        } else if (url.endsWith(".doc")) {
				fs = new POIFSFileSystem(bi);
	            HWPFDocument doc = new HWPFDocument(fs);
	            WordExtractor we = new WordExtractor(doc);
	            content = we.getText();//if the extension is .doc
	        } else if (url.endsWith(".docx")) {
	        	OPCPackage d = OPCPackage.open(bi);
	            XParsers xp = new XParsers();
	            content = xp.docFileContentParser(d);
	        } else if (url.endsWith(".xlsx")) {
	        	OPCPackage d = OPCPackage.open(bi);
	            XParsers xp = new XParsers();
	            content = xp.excelContentParser(d);
	        } else if (url.endsWith(".pptx")) {
	        	OPCPackage d = OPCPackage.open(bi);
	            XParsers xp = new XParsers();
	            content = xp.ppFileContentParser(d);
	        } 
		} catch (IOException ioe) {
			log.error("Error parsing ["+url+"]: "+ ioe.getMessage(),ioe);
			content = "";
		} catch (Exception e) {
			log.error("Error parsing ["+url+"]: "+ e.getMessage(),e);
			content = "";
		}
		content = StringUtils.replace(content, "\n", " ");
		return content;
	}
}
