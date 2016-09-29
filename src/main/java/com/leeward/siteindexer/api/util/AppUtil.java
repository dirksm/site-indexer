package com.leeward.siteindexer.api.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;

public class AppUtil {

	public static void logPrepStatment(String sql, PreparedStatementHelper prepHelper, Logger log) {

		try {
		   Pattern pattern = Pattern.compile("\\?");
		   Matcher matcher = pattern.matcher(sql);
		   StringBuffer sb = new StringBuffer();
		   int indx = 1;  // Parameter begin with index 1
		   while (matcher.find()) {
			   matcher.appendReplacement(sb, prepHelper.getParameter(indx++));
		   }
		   matcher.appendTail(sb);
		   log.trace("Executing Query [" + sb.toString() + "]");
		   } catch (Exception ex) {
		    log.trace("Executing Query [" + sql + "]");
		}
	}

	public static boolean isMicrosoftFile(String url) {
		return url.endsWith(".doc") || url.endsWith(".docx") || url.endsWith(".xls") || url.endsWith(".xlsx") || url.endsWith(".ppt") || url.endsWith(".pptx");
	}
	
}
