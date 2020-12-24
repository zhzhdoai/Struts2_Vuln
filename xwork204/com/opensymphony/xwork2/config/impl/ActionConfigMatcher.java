/*
 * $Id: ActionConfigMatcher.java 1223 2006-11-23 20:33:15Z rainerh $
 *
 * Copyright 2003,2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.opensymphony.xwork2.config.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.opensymphony.xwork2.util.WildcardHelper;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.ExceptionMappingConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * <p> Matches paths against pre-compiled wildcard expressions pulled from
 * action configs. It uses the wildcard matcher from the Apache Cocoon
 * project. Patterns will be matched in the order they exist in the 
 * config file. The first match wins, so more specific patterns should be
 * defined before less specific patterns.
 */
public class ActionConfigMatcher implements Serializable {
    /**
     * <p> The logging instance </p>
     */
    private static final Log log = LogFactory.getLog(ActionConfigMatcher.class);

    /**
     * <p> Handles all wildcard pattern matching. </p>
     */
    private static final WildcardHelper wildcard = new WildcardHelper();

    /**
     * <p> The compiled paths and their associated ActionConfig's </p>
     */
    private List compiledPaths;

    /**
     * <p> Finds and precompiles the wildcard patterns from the ActionConfig
     * "path" attributes. ActionConfig's will be evaluated in the order they
     * exist in the config file. Only paths that actually contain a
     * wildcard will be compiled. Patterns will matched strictly.</p>
     *
     * @param configs An array of ActionConfig's to process
     */
    public ActionConfigMatcher(Map<String, ActionConfig> configs) {
        this(configs, false);
    }
    
    /**
     * <p> Finds and precompiles the wildcard patterns from the ActionConfig
     * "path" attributes. ActionConfig's will be evaluated in the order they
     * exist in the config file. Only paths that actually contain a
     * wildcard will be compiled. </p>
     * 
     * <p>Patterns can optionally be matched "loosely".  When
     * the end of the pattern matches \*[^*]\*$ (wildcard, no wildcard,
     * wildcard), if the pattern fails, it is also matched as if the 
     * last two characters didn't exist.  The goal is to support the 
     * legacy "*!*" syntax, where the "!*" is optional.</p> 
     *
     * @param configs An array of ActionConfig's to process
     * @param looseMatch To loosely match wildcards or not
     */
    public ActionConfigMatcher(Map<String, ActionConfig> configs,
            boolean looseMatch) {
        compiledPaths = new ArrayList();

        int[] pattern;

        for (String name : configs.keySet()) {

            if ((name != null) && (name.indexOf('*') > -1)) {
                if ((name.length() > 0) && (name.charAt(0) == '/')) {
                    name = name.substring(1);
                }

                if (log.isDebugEnabled()) {
                    log.debug("Compiling action config path '" + name + "'");
                }

                pattern = wildcard.compilePattern(name);
                compiledPaths.add(new Mapping(name, pattern, configs.get(name)));
                
                int lastStar = name.lastIndexOf('*');
                if (lastStar > 1 && lastStar == name.length() - 1) {
                    if (name.charAt(lastStar - 1) != '*') {
                        pattern = wildcard.compilePattern(name.substring(0, lastStar - 1));
                        compiledPaths.add(new Mapping(name, pattern, configs.get(name)));
                    }
                }
            }
        }
    }

    /**
     * <p> Matches the path against the compiled wildcard patterns. </p>
     *
     * @param path The portion of the request URI for selecting a config.
     * @return The action config if matched, else null
     */
    public ActionConfig match(String path) {
        ActionConfig config = null;

        if (compiledPaths.size() > 0) {
            if (log.isDebugEnabled()) {
                log.debug("Attempting to match '" + path
                    + "' to a wildcard pattern, "+ compiledPaths.size()
                    + " available");
            }

            Mapping m;
            HashMap vars = new HashMap();

            for (Iterator i = compiledPaths.iterator(); i.hasNext();) {
                m = (Mapping) i.next();
                if (wildcard.match(vars, path, m.getPattern())) {
                    if (log.isDebugEnabled()) {
                        log.debug("Path matches pattern '"
                            + m.getOriginal() + "'");
                    }

                    config =
                        convertActionConfig(path,
                            (ActionConfig) m.getActionConfig(), vars);
                    break;
                }
            }
        }

        return config;
    }

    /**
     * <p> Clones the ActionConfig and its children, replacing various
     * properties with the values of the wildcard-matched strings. </p>
     *
     * @param path The requested path
     * @param orig The original ActionConfig
     * @param vars A Map of wildcard-matched strings
     * @return A cloned ActionConfig with appropriate properties replaced with
     *         wildcard-matched values
     */
    protected ActionConfig convertActionConfig(String path, ActionConfig orig,
        Map vars) {
        
        String className = convertParam(orig.getClassName(), vars);
        String methodName = convertParam(orig.getMethodName(), vars);
        String pkgName = convertParam(orig.getPackageName(), vars);
        
        Map<String,Object> params = replaceParameters(orig.getParams(), vars);
        
        Map<String,ResultConfig> results = new LinkedHashMap<String,ResultConfig>();
        for (String name : orig.getResults().keySet()) {
            ResultConfig result = orig.getResults().get(name);
            name = convertParam(name, vars);
            String resultClassName = convertParam(result.getClassName(), vars);
            Map<String,Object> resultParams = replaceParameters(result.getParams(), vars);
            ResultConfig r = new ResultConfig(name, resultClassName, resultParams);
            results.put(name, r);
        }
        
        List<ExceptionMappingConfig> exs = new ArrayList<ExceptionMappingConfig>();
        for (ExceptionMappingConfig ex : orig.getExceptionMappings()) {
            String name = convertParam(ex.getName(), vars);
            String exClassName = convertParam(ex.getExceptionClassName(), vars);
            String exResult = convertParam(ex.getResult(), vars);
            Map<String,Object> exParams = replaceParameters(ex.getParams(), vars);
            ExceptionMappingConfig e = new ExceptionMappingConfig(name, exClassName, exResult, exParams);
            exs.add(e);
        }
        
        ActionConfig config = new ActionConfig(methodName, className, pkgName, 
                params, results, orig.getInterceptors(), exs);
        config.setLocation(orig.getLocation());
        
        return config;
    }

    /**
     * <p> Replaces parameter values
     * </p>
     *
     * @param orig  The original parameters with placehold values
     * @param vars  A Map of wildcard-matched strings
     */
    protected Map<String,Object> replaceParameters(Map<String, Object> orig, Map vars) {
        Map<String,Object> map = new LinkedHashMap<String,Object>();
        for (String key : orig.keySet()) {
            map.put(key, convertParam(String.valueOf(orig.get(key)), vars));
        }
        return map;
    }

    /**
     * <p> Inserts into a value wildcard-matched strings where specified
     * with the {x} syntax.  If a wildcard-matched value isn't found, the
     * replacement token is turned into an empty string. 
     * </p>
     *
     * @param val  The value to convert
     * @param vars A Map of wildcard-matched strings
     * @return The new value
     */
    protected String convertParam(String val, Map vars) {
        if (val == null) {
            return null;
        } 
        
        int len = val.length();
        StringBuilder ret = new StringBuilder();
        char c;
        String varVal;
        for (int x=0; x<len; x++) {
            c = val.charAt(x);
            if (x < len - 2 && 
                    c == '{' && '}' == val.charAt(x+2)) {
                varVal = (String)vars.get(String.valueOf(val.charAt(x + 1)));
                if (varVal != null) {
                    ret.append(varVal);
                } 
                x += 2;
            } else {
                ret.append(c);
            }
        }
        
        return ret.toString();
    }

    /**
     * <p> Stores a compiled wildcard pattern and the ActionConfig it came
     * from. </p>
     */
    private class Mapping implements Serializable {
        /**
         * <p> The original pattern. </p>
         */
        private String original;

        
        /**
         * <p> The compiled pattern. </p>
         */
        private int[] pattern;

        /**
         * <p> The original ActionConfig. </p>
         */
        private ActionConfig config;

        /**
         * <p> Contructs a read-only Mapping instance. </p>
         *
         * @param original The original pattern
         * @param pattern The compiled pattern
         * @param config  The original ActionConfig
         */
        public Mapping(String original, int[] pattern, ActionConfig config) {
            this.original = original;
            this.pattern = pattern;
            this.config = config;
        }

        /**
         * <p> Gets the compiled wildcard pattern. </p>
         *
         * @return The compiled pattern
         */
        public int[] getPattern() {
            return this.pattern;
        }

        /**
         * <p> Gets the ActionConfig that contains the pattern. </p>
         *
         * @return The associated ActionConfig
         */
        public ActionConfig getActionConfig() {
            return this.config;
        }
        
        /**
         * <p> Gets the original wildcard pattern. </p>
         *
         * @return The original pattern
         */
        public String getOriginal() {
            return this.original;
        }
    }
}
