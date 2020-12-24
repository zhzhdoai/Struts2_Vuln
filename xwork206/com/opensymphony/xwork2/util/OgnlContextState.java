/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */

package com.opensymphony.xwork2.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages variables in the OgnlContext and returns values
 * to be used by the application.
 *
 * @author Gabe
 */
public class OgnlContextState {

    public static final String CURRENT_PROPERTY_PATH="current.property.path";
    public static final String FULL_PROPERTY_PATH="current.property.path";
    private static final String GETTING_BY_KEY_PROPERTY="xwork.getting.by.key.property";

    private static final String SET_MAP_KEY="set.map.key";

    public static boolean isCreatingNullObjects(Map context) {
		//TODO
		return getBooleanProperty(InstantiatingNullHandler.CREATE_NULL_OBJECTS, context);
	}

	public static void setCreatingNullObjects(Map context, boolean creatingNullObjects) {
		setBooleanValue(InstantiatingNullHandler.CREATE_NULL_OBJECTS, context, creatingNullObjects);
	}

	public static boolean isGettingByKeyProperty(Map context) {
		return getBooleanProperty(GETTING_BY_KEY_PROPERTY, context);
	}
	
	public static void setDenyMethodExecution(Map context, boolean denyMethodExecution) {
		setBooleanValue(XWorkMethodAccessor.DENY_METHOD_EXECUTION, context, denyMethodExecution);
	}
	
	public static boolean isDenyMethodExecution(Map context) {
		return getBooleanProperty(XWorkMethodAccessor.DENY_METHOD_EXECUTION, context);
	}

	public static void setGettingByKeyProperty(Map context, boolean gettingByKeyProperty) {
		setBooleanValue(GETTING_BY_KEY_PROPERTY, context, gettingByKeyProperty);
	}	
	
	public static boolean isReportingConversionErrors(Map context) {
		return getBooleanProperty(XWorkConverter.REPORT_CONVERSION_ERRORS, context);
	}

	public static void setReportingConversionErrors(Map context, boolean reportingErrors) {
		setBooleanValue(XWorkConverter.REPORT_CONVERSION_ERRORS, context, reportingErrors);
	}

	public static Class getLastBeanClassAccessed(Map context) {
		return (Class)context.get(XWorkConverter.LAST_BEAN_CLASS_ACCESSED);
	}

	public static void setLastBeanPropertyAccessed(Map context, String property) {
		context.put(XWorkConverter.LAST_BEAN_PROPERTY_ACCESSED, property);
	}

	public static String getLastBeanPropertyAccessed(Map context) {
		return (String)context.get(XWorkConverter.LAST_BEAN_PROPERTY_ACCESSED);
	}

	public static void setLastBeanClassAccessed(Map context, Class clazz) {
		context.put(XWorkConverter.LAST_BEAN_CLASS_ACCESSED, clazz);
	}
	/**
	 * Gets the current property path but not completely.
	 * It does not use the [ and ] used in some representations
	 * of Maps and Lists. The reason for this is that the current
	 * property path is only currently used for caching purposes
	 * so there is no real reason to have an exact replica.
     *
	 * <p/>So if the real path is myProp.myMap['myKey'] this would
	 * return myProp.myMap.myKey.
     * 
	 * @param context
	 */
	public static String getCurrentPropertyPath(Map context) {
		return (String)context.get(CURRENT_PROPERTY_PATH);
	}

	public static String getFullPropertyPath(Map context) {
		return (String)context.get(FULL_PROPERTY_PATH);
	}

	public static void setFullPropertyPath(Map context, String path) {
		context.put(FULL_PROPERTY_PATH, path);

	}

	public static void updateCurrentPropertyPath(Map context, Object name) {
		String currentPath=getCurrentPropertyPath(context);
		if (name!=null) {
			if (currentPath!=null) {
				currentPath=currentPath + "." + name.toString();
			}	else {
				currentPath=name.toString();
			}
			context.put(CURRENT_PROPERTY_PATH, currentPath);
		}
	}

	public static void setSetMap(Map context, Map setMap, String path) {
		Map mapOfSetMaps=(Map)context.get(SET_MAP_KEY);
		if (mapOfSetMaps==null) {
			mapOfSetMaps=new HashMap();
			context.put(SET_MAP_KEY, mapOfSetMaps);
		}
		mapOfSetMaps.put(path, setMap);
	}

	public static Map getSetMap(Map context, String path) {
		Map mapOfSetMaps=(Map)context.get(SET_MAP_KEY);
		if (mapOfSetMaps==null) {
			return null;
		}
		return (Map)mapOfSetMaps.get(path);
	}

	private static boolean getBooleanProperty(String property, Map context) {
		Boolean myBool=(Boolean)context.get(property);
		return (myBool==null)?false:myBool.booleanValue();
	}

	private static void setBooleanValue(String property, Map context, boolean value) {
		context.put(property, new Boolean(value));
	}

	/**
	 *
	 */
	public static void clearCurrentPropertyPath(Map context) {
		context.put(CURRENT_PROPERTY_PATH, null);

	}


    public static void clear(Map context) {
        context.put(XWorkConverter.LAST_BEAN_CLASS_ACCESSED,null);
        context.put(XWorkConverter.LAST_BEAN_PROPERTY_ACCESSED,null);

        context.put(CURRENT_PROPERTY_PATH,null);
        context.put(FULL_PROPERTY_PATH,null);

    }



}
