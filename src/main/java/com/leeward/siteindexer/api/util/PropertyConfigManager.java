package com.leeward.siteindexer.api.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.MissingResourceException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyConfigManager {

	private static Logger log = LoggerFactory.getLogger(PropertyConfigManager.class.getName());
	private static final String RESOURCE_FILE = "config.properties";
	private static final Properties prop = new Properties();

	private static HashMap<String, Object> overriddenProperties = new HashMap<String, Object>();

	static {
		final InputStream resource = PropertyConfigManager.class.getClassLoader().getResourceAsStream(RESOURCE_FILE);
		try {
			prop.load(resource);
		} catch (IOException e) {
			log.error("Exception loading properties file: " + e.getMessage(), e);
		}
	}
	
	/** This class has static methods, so it should never need to be instantiated.*/
	private PropertyConfigManager() {}

	/**
	 * Allow the setting of a property incase you don't want to use what's in the property file
	 * @param name
	 * @param value
	 */
	public static void setProperty(String name, Object value) {
		overriddenProperties.put(name, value);
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
			return prop.getProperty(key);
		} catch(NullPointerException npe) {
			if(key == null) {
				throw npe;
			} else {
				logUsingDefaults("Could not load resource file", key, defaultValue);
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
