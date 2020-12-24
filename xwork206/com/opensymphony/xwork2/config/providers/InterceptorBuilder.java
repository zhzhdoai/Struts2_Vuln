/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.config.providers;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.entities.InterceptorConfig;
import com.opensymphony.xwork2.config.entities.InterceptorMapping;
import com.opensymphony.xwork2.config.entities.InterceptorStackConfig;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.interceptor.Interceptor;
import com.opensymphony.xwork2.util.location.Location;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;


/**
 * Builds a list of interceptors referenced by the refName in the supplied PackageConfig.
 *
 * @author Mike
 * @author Rainer Hermanns
 * @author tmjee
 * @version $Date: 2006-11-13 09:05:32 +0100 (Mo, 13 Nov 2006) $ $Id: InterceptorBuilder.java 1187 2006-11-13 08:05:32Z mrdon $
 */
public class InterceptorBuilder {

    private static final Log LOG = LogFactory.getLog(InterceptorBuilder.class);


    /**
     * Builds a list of interceptors referenced by the refName in the supplied PackageConfig.
     *
     * @param packageConfig
     * @param refName
     * @param refParams
     * @return list of interceptors referenced by the refName in the supplied PackageConfig.
     * @throws ConfigurationException
     */
    public static List<InterceptorMapping> constructInterceptorReference(PackageConfig packageConfig, 
            String refName, Map refParams, Location location, ObjectFactory objectFactory) throws ConfigurationException {
        Object referencedConfig = packageConfig.getAllInterceptorConfigs().get(refName);
        List<InterceptorMapping> result = new ArrayList<InterceptorMapping>();

        if (referencedConfig == null) {
            throw new ConfigurationException("Unable to find interceptor class referenced by ref-name " + refName, location);
        } else {
            if (referencedConfig instanceof InterceptorConfig) {
                InterceptorConfig config = (InterceptorConfig) referencedConfig;
                Interceptor inter = null;
                try {
                    
                    inter = objectFactory.buildInterceptor(config, refParams);
                    result.add(new InterceptorMapping(refName, inter));
                } catch (ConfigurationException ex) {
                    LOG.warn("Unable to load config class "+config.getClassName()+" at "+
                            ex.getLocation()+" probably due to a missing jar, which might "+
                            "be fine if you never plan to use the "+config.getName()+" interceptor");
                    LOG.error("Actual exception", ex);
                }
                
            } else if (referencedConfig instanceof InterceptorStackConfig) {
                InterceptorStackConfig stackConfig = (InterceptorStackConfig) referencedConfig;

                if ((refParams != null) && (refParams.size() > 0)) {
                    result = constructParameterizedInterceptorReferences(packageConfig, stackConfig, refParams, objectFactory);
                } else {
                    result.addAll(stackConfig.getInterceptors());
                }

            } else {
                LOG.error("Got unexpected type for interceptor " + refName + ". Got " + referencedConfig);
            }
        }

        return result;
    }

    /**
     * Builds a list of interceptors referenced by the refName in the supplied PackageConfig overriding the properties
     * of the referenced interceptor with refParams.
     *
     * @param packageConfig
     * @param stackConfig
     * @param refParams     The overridden interceptor properies
     * @return list of interceptors referenced by the refName in the supplied PackageConfig overridden with refParams.
     */
    private static List<InterceptorMapping> constructParameterizedInterceptorReferences(
            PackageConfig packageConfig, InterceptorStackConfig stackConfig, Map refParams,
            ObjectFactory objectFactory) {
        List<InterceptorMapping> result;
        Map<String, Map<Object, String>> params = new HashMap<String, Map<Object, String>>();

        for (Iterator iter = refParams.keySet().iterator(); iter.hasNext();) {
            String key = (String) iter.next();
            String value = (String) refParams.get(key);

            try {
                String name = key.substring(0, key.indexOf('.'));
                key = key.substring(key.indexOf('.') + 1);

                Map<Object, String> map;
                if (params.containsKey(name)) {
                    map = params.get(name);
                } else {
                    map = new HashMap<Object, String>();
                }

                map.put(key, value);
                params.put(name, map);

            } catch (Exception e) {
                LOG.warn("No interceptor found for name = " + key);
            }
        }

        result = new ArrayList<InterceptorMapping>(stackConfig.getInterceptors());

        for (String key : params.keySet()) {

            Map<Object, String> map = params.get(key);

            InterceptorConfig cfg = (InterceptorConfig) packageConfig.getAllInterceptorConfigs().get(key);
            Interceptor interceptor = objectFactory.buildInterceptor(cfg, map);

            InterceptorMapping mapping = new InterceptorMapping(key, interceptor);
            if (result != null && result.contains(mapping)) {
                int index = result.indexOf(mapping);
                result.set(index, mapping);
            } else {
                result.add(mapping);
            }
        }

        return result;
    }
}
