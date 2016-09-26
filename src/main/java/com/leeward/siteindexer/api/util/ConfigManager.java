package com.leeward.siteindexer.api.util;

import java.util.HashMap;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class is a wrapper around our configuration text files.
 *
 * @author tbrown2
 */
public class ConfigManager {
	
	private static Logger log = LoggerFactory.getLogger(ConfigManager.class.getName());
	private static final String RESOURCE_FILE = "properties.config";
	private static ResourceBundle itsResources = null;
	private static boolean resourcesLoaded = false;
	private static HashMap<String, Object> overriddenProperties = new HashMap<String, Object>();

	/** This class has static methods, so it should never need to be instantiated.*/
	private ConfigManager() {}

	/**
	 * Allow the setting of a property incase you don't want to use what's in the property file
	 * @param name
	 * @param value
	 */
	public static void setProperty(String name, Object value) {
		overriddenProperties.put(name, value);
	}

	/**
	 * Loads resource files if they have yet to be loaded; otherwise simply
	 * returns the resources that have already been gathered.
	 *
	 *@return
	 *  A ResourceBundle to use.
	 */
	private static ResourceBundle getResources(){
		if(!resourcesLoaded){
			resourcesLoaded=true;
			try{
				itsResources = PropertyResourceBundle.getBundle(ConfigManager.RESOURCE_FILE);
			}catch(MissingResourceException mre){
				log.warn("Configuration File \""+ConfigManager.RESOURCE_FILE+"\" was not found.");
				itsResources = null;
			}
		}
		return itsResources;
	}
	
	/**
	 * Retreives the requested value from the resource files as a boolean.
	 *
	 *@param key
	 *  The key to retreive the value of.
	 *@param defaultValue
	 *  The value to return if the there is an error getting the key's value
	 *@return
	 *  The boolean value of the key if possible or the defaultValue
	 *@throws NullPointerException
	 *  If the requested key is null
	 */
	public static boolean getBoolean(String key, boolean defaultValue){
		String s = getString(key, String.valueOf(defaultValue));
		return Boolean.parseBoolean(s);
	}
	
	/**
	 * Retreives the requested value from the resource files as an int.
	 *
	 *@param key
	 *  The key to retreive the value of.
	 *@param defaultValue
	 *  The value to return if the there is an error getting the key's value
	 *@return
	 *  The int value of the key if possible or the defaultValue
	 *@throws NullPointerException
	 *  If the requested key is null
	 */
	public static int getInt(String key, int defaultValue){
		String defaultVal = String.valueOf(defaultValue);
		try{
			String s = getString(key, defaultVal);
			return Integer.parseInt(s);
		}catch(NumberFormatException nfe){
			logUsingDefaults("Value for the given key could not be parsed", key, defaultVal);
		}
		return defaultValue;
	}
	
	/**
	 * Retreives the requested value from the resource files as a String.
	 *
	 *@param key
	 *  The key to retreive the value of.
	 *@param defaultValue
	 *  The value to return if the there is an error getting the key's value
	 *@return
	 *  The String value of the key if possible or the defaultValue
	 *@throws NullPointerException
	 *  If the requested key is null
	 */
	public static String getString(String key, String defaultValue) {
		try {
			String overriddenProperty = (String)overriddenProperties.get(key);
			if (overriddenProperty != null) {
				return overriddenProperty;
			}
			return getResources().getString(key);
		} catch(NullPointerException npe) {
			if(key == null) {
				throw npe;
			} else {
				logUsingDefaults("Could not load reasource file", key, defaultValue);
			}
		} catch(MissingResourceException mre) {
			logUsingDefaults("Could not find the requested key", key, defaultValue);
		} catch(ClassCastException cce) {
			logUsingDefaults("Value for the given key was not a String", key, defaultValue);
		}
		return defaultValue;
	}
	
	/**
	 * Helper method logs anytime a default value is used instead of the value
	 * gathered from a resource file.
	 *
	 *@param cause
	 *  Why the value could not be retreived for the key.
	 *@param problemKey
	 *  The key whose value could not be retreived.
	 *@param defaultValue
	 *  The defaultValue to use.
	 */
	private static void logUsingDefaults(String cause, String problemKey, String defaultValue){
		log.warn(cause+"; using default value ("+defaultValue+") for requested key ("+problemKey+").");
	}
	
	/**
	 * Returns false unless the requested key is defined as true
	 */
	public static boolean isEnabled(String key) {
		if (key == null || key.trim().length() == 0) return false;
		key = key + (key.toLowerCase().endsWith(".enable") ? "" : ".enable");
		return getBoolean(key, false);
	}
}