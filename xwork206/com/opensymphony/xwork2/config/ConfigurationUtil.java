/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.config;

import com.opensymphony.xwork2.config.entities.PackageConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;


/**
 * ConfigurationUtil
 *
 * @author Jason Carreira
 *         Created May 23, 2003 11:22:49 PM
 */
public class ConfigurationUtil {

    private static final Log LOG = LogFactory.getLog(ConfigurationUtil.class);


    private ConfigurationUtil() {
    }


    public static List buildParentsFromString(Configuration configuration, String parent) {
        if ((parent == null) || (parent.equals(""))) {
            return Collections.EMPTY_LIST;
        }

        StringTokenizer tokenizer = new StringTokenizer(parent, ", ");
        List parents = new ArrayList();

        while (tokenizer.hasMoreTokens()) {
            String parentName = tokenizer.nextToken().trim();

            if (!parentName.equals("")) {
                PackageConfig parentPackageContext = configuration.getPackageConfig(parentName);

                if (parentPackageContext != null) {
                    parents.add(parentPackageContext);
                }
            }
        }

        return parents;
    }
}
