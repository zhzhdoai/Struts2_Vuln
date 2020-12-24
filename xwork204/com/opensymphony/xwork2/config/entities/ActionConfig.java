/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.config.entities;

import com.opensymphony.xwork2.util.location.Located;

import java.io.Serializable;
import java.util.*;


/**
 * Contains everything needed to configure and execute an action:
 * <ul>
 * <li>methodName - the method name to execute on the action. If this is null, the Action will be cast to the Action
 * Interface and the execute() method called</li>
 * <li>clazz - the class name for the action</li>
 * <li>params - the params to be set for this action just before execution</li>
 * <li>results - the result map {String -> View class}</li>
 * <li>resultParameters - params for results {String -> Map}</li>
 * <li>typeConverter - the Ognl TypeConverter to use when getting/setting properties</li>
 * </ul>
 *
 * @author Mike
 * @author Rainer Hermanns
 * @version $Revision: 1212 $
 */
public class ActionConfig extends Located implements InterceptorListHolder, Parameterizable, Serializable {

    protected List<InterceptorMapping> interceptors;
    protected Map<String, Object> params;
    protected Map<String, ResultConfig> results;
    protected List<ExceptionMappingConfig> exceptionMappings;
    protected String className;
    protected String methodName;
    protected String packageName;


    public ActionConfig() {
        params = new LinkedHashMap<String, Object>();
        results = new LinkedHashMap<String, ResultConfig>();
        interceptors = new ArrayList<InterceptorMapping>();
        exceptionMappings = new ArrayList<ExceptionMappingConfig>();
    }

    //Helper constuctor to maintain backward compatibility with objects that create ActionConfigs
    //TODO this should be removed if these changes are rolled in to xwork CVS
    public ActionConfig(String methodName, Class clazz, Map<String, Object> parameters, Map<String, ResultConfig> results, List<InterceptorMapping> interceptors) {
        this(methodName, clazz.getName(), parameters, results, interceptors);
    }

    public ActionConfig(String methodName, Class clazz, Map<String, Object> parameters, Map<String, ResultConfig> results, List<InterceptorMapping> interceptors, List<ExceptionMappingConfig> exceptionMappings) {
        this(methodName, clazz.getName(), parameters, results, interceptors, exceptionMappings);
    }

    public ActionConfig(String methodName, String className, Map<String, Object> parameters, Map<String, ResultConfig> results, List<InterceptorMapping> interceptors) {
        this(methodName, className, "", parameters, results, interceptors, Collections.EMPTY_LIST);
    }

    public ActionConfig(String methodName, String className, Map<String, Object> parameters, Map<String, ResultConfig> results, List<InterceptorMapping> interceptors, List<ExceptionMappingConfig> exceptionMappings) {
        this(methodName, className, "", parameters, results, interceptors, exceptionMappings);
    }

    public ActionConfig(String methodName, String className, String packageName, Map<String, Object> parameters, Map<String, ResultConfig> results, List<InterceptorMapping> interceptors) {
        this(methodName, className, packageName, parameters, results, interceptors, Collections.EMPTY_LIST);
    }

    public ActionConfig(String methodName, String className, String packageName, Map<String, Object> parameters,
                        Map<String, ResultConfig> results, List<InterceptorMapping> interceptors, List<ExceptionMappingConfig> exceptionMappings) {
        this.methodName = methodName;
        this.interceptors = interceptors;
        this.params = parameters;
        this.results = results;
        this.className = className;
        this.exceptionMappings = exceptionMappings;
        this.packageName = packageName;
    }
    
    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    public List<ExceptionMappingConfig> getExceptionMappings() {
        return exceptionMappings;
    }

    public List<InterceptorMapping> getInterceptors() {
        if (interceptors == null) {
            interceptors = new ArrayList<InterceptorMapping>();
        }

        return interceptors;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    /**
     * Returns name of the action method
     *
     * @return name of the method to execute
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * @param packageName The packageName to set.
     */
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    /**
     * @return Returns the packageName.
     */
    public String getPackageName() {
        return packageName;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public Map<String, Object> getParams() {
        if (params == null) {
            params = new LinkedHashMap<String, Object>();
        }

        return params;
    }

    public void setResults(Map<String, ResultConfig> results) {
        this.results = results;
    }

    public Map<String, ResultConfig> getResults() {
        if (results == null) {
            results = new LinkedHashMap<String, ResultConfig>();
        }

        return results;
    }

    public void addExceptionMapping(ExceptionMappingConfig exceptionMapping) {
        getExceptionMappings().add(exceptionMapping);
    }

    public void addExceptionMappings(List<? extends ExceptionMappingConfig> mappings) {
        getExceptionMappings().addAll(mappings);
    }

    public void addInterceptor(InterceptorMapping interceptor) {
        getInterceptors().add(interceptor);
    }

    public void addInterceptors(List<InterceptorMapping> interceptors) {
        getInterceptors().addAll(interceptors);
    }

    public void addParam(String name, Object value) {
        getParams().put(name, value);
    }

    public void addResultConfig(ResultConfig resultConfig) {
        getResults().put(resultConfig.getName(), resultConfig);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof ActionConfig)) {
            return false;
        }

        final ActionConfig actionConfig = (ActionConfig) o;

        if ((className != null) ? (!className.equals(actionConfig.className)) : (actionConfig.className != null)) {
            return false;
        }

        if ((interceptors != null) ? (!interceptors.equals(actionConfig.interceptors)) : (actionConfig.interceptors != null))
        {
            return false;
        }

        if ((methodName != null) ? (!methodName.equals(actionConfig.methodName)) : (actionConfig.methodName != null)) {
            return false;
        }

        if ((params != null) ? (!params.equals(actionConfig.params)) : (actionConfig.params != null)) {
            return false;
        }

        if ((results != null) ? (!results.equals(actionConfig.results)) : (actionConfig.results != null)) {
            return false;
        }
        
        return true;
    }

    public int hashCode() {
        int result;
        result = ((interceptors != null) ? interceptors.hashCode() : 0);
        result = (29 * result) + ((params != null) ? params.hashCode() : 0);
        result = (29 * result) + ((results != null) ? results.hashCode() : 0);
        result = (29 * result) + ((methodName != null) ? methodName.hashCode() : 0);

        return result;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("{ActionConfig ");
        sb.append(className);
        if (methodName != null) {
            sb.append(".").append(methodName).append("()");
        }
        sb.append(" - ").append(location);
        sb.append("}");
        return sb.toString();
    }
}
