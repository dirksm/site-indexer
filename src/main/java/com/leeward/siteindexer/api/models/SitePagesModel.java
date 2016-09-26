package com.leeward.siteindexer.api.models;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Date;


public class SitePagesModel implements Serializable {

    private static Logger log = LoggerFactory.getLogger(SitePagesModel.class);
    private Integer id;
    private String siteUrl;
    private String title;
    private String text;
    private String pageUrl;
    private Date updated;

	
    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public String getSiteUrl() {
        return this.siteUrl;
    }

    public void setSiteUrl(String siteUrl) {
        this.siteUrl = siteUrl;
    }


    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }


    public String getPageUrl() {
        return this.pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }


    public Date getUpdated() {
        return this.updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }



	
    @Override
	public String toString() {
		return "SitePagesModel [id=" + id + ", siteUrl=" + siteUrl + ", title=" + title + ", text=" + text
				+ ", pageUrl=" + pageUrl + ", updated=" + updated + "]";
	}

}
