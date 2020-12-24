/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.config.providers;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Map;
import java.util.LinkedHashMap;


/**
 * XML utilities.
 *
 * @author Mike
 */
public class XmlHelper {

    public static Map getParams(Element paramsElement) {
        LinkedHashMap params = new LinkedHashMap();

        if (paramsElement == null) {
            return params;
        }

        NodeList childNodes = paramsElement.getChildNodes();

        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);

            if ((childNode.getNodeType() == Node.ELEMENT_NODE) && "param".equals(childNode.getNodeName())) {
                Element paramElement = (Element) childNode;
                String paramName = paramElement.getAttribute("name");

                StringBuffer paramValue = new StringBuffer();
                for (int j=0; j <paramElement.getChildNodes().getLength(); j++) {
                	if (paramElement.getChildNodes().item(j) != null && 
                			paramElement.getChildNodes().item(j).getNodeType() == Node.TEXT_NODE) {
                		String val = paramElement.getChildNodes().item(j).getNodeValue();
                		if (val != null) {
                			paramValue .append(val.trim());
                		} 
                	}
                }
                String val = paramValue.toString().trim();
                if (val.length() > 0) {
                	params.put(paramName, val);
                }
            }
        }

        return params;
    }
}
