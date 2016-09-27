package com.leeward.siteindexer.api.models;

import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.leeward.siteindexer.api.constants.AppConstants;

/**
 * This is a unique ArrayBlockingQueue ensuring that no duplicate URL's are added to the queue.
 * @author Michael R Dirks
 *
 */
public class URLArrayBlockingQueue<E> extends ArrayBlockingQueue<String> {

	private static final long serialVersionUID = 6601585653297588806L;
	TreeSet<String> urls;

	
	public URLArrayBlockingQueue(int capacity) {
		super(capacity);
		urls = new TreeSet<String>();
	}
	
	public void addAll(List<String> e) {
		for (String s : e) {
			this.offer(s);
		}
	}
	
	@Override
	public void put(String e) throws InterruptedException {
		if(e.equals(AppConstants.END_PROCESSING)) {
			super.put(e);
		} else if (!e.equals(AppConstants.END_PROCESSING) && urls.add(e)) {
			super.put(e);
		}
	}
	
	@Override
	public boolean offer(String e) {
		boolean o = false;
		if(e.equals(AppConstants.END_PROCESSING)) {
			o = super.offer(e);
		} else if (urls.add(e)) {
			o = super.offer(e);
		}
		return o;
	}
	
	public int getSize() {
		return this.urls.size();
	}

	@Override
	public boolean offer(String e, long timeout, TimeUnit unit) throws InterruptedException {
		boolean o = false;
		if(e.equals(AppConstants.END_PROCESSING)) {
			o = super.offer(e, timeout, unit);
		} else if (urls.add(e)) {
			o = super.offer(e, timeout, unit);
		}
		return o;
	}
}
