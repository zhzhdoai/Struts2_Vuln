/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.config.entities;

import com.opensymphony.xwork2.util.TextUtils;
import com.opensymphony.xwork2.util.location.Located;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;
import java.util.*;


/**
 * Configuration for Package.
 * <p/>
 * In the xml configuration file this is defined as the <code>package</code> tag.
 *
 * @author Rainer Hermanns
 * @version $Revision: 1445 $
 */
public class PackageConfig extends Located implements Comparable, Serializable {

    private static final Log LOG = LogFactory.getLog(PackageConfig.class);


    private Map<String, ActionConfig> actionConfigs = new LinkedHashMap<String, ActionConfig>();
    private Map<String, ResultConfig> globalResultConfigs = new LinkedHashMap<String, ResultConfig>();
    private Map interceptorConfigs = new LinkedHashMap();
    private Map<String, ResultTypeConfig> resultTypeConfigs = new LinkedHashMap<String, ResultTypeConfig>();
    private List globalExceptionMappingConfigs = new ArrayList();
    private List<PackageConfig> parents = new ArrayList<PackageConfig>();
    private String defaultInterceptorRef;
    private String defaultActionRef;
    private String defaultResultType;
    private String defaultClassRef;
    private String name;
    private String namespace = "";
    private boolean isAbstract = false;

    private boolean needsRefresh;
    


    public PackageConfig() {
    }

    public PackageConfig(String name) {
        this.name = name;
    }

    public PackageConfig(String name, String namespace, boolean isAbstract) {
        this(name);
        this.namespace = TextUtils.noNull(namespace);
        this.isAbstract = isAbstract;
    }

    public PackageConfig(String name, String namespace, boolean isAbstract, List parents) {
        this(name, namespace, isAbstract);

        if (parents != null) {
            for (Iterator iterator = parents.iterator(); iterator.hasNext();) {
                PackageConfig parent = (PackageConfig) iterator.next();
                addParent(parent);
            }
        }
    }


    public void setAbstract(boolean isAbstract) {
        this.isAbstract = isAbstract;
    }

    public boolean isAbstract() {
        return isAbstract;
    }

    public Map<String, ActionConfig> getActionConfigs() {
        return actionConfigs;
    }

    /**
     * returns the Map of all the ActionConfigs available in the current package.
     * ActionConfigs defined in ancestor packages will be included in this Map.
     *
     * @return a Map of ActionConfig Objects with the action name as the key
     * @see ActionConfig
     */
    public Map<String, ActionConfig> getAllActionConfigs() {
        Map<String, ActionConfig> retMap = new LinkedHashMap<String, ActionConfig>();

        if (!parents.isEmpty()) {
            for (PackageConfig parent : parents) {
                retMap.putAll(parent.getAllActionConfigs());
            }
        }

        retMap.putAll(getActionConfigs());

        return retMap;
    }

    /**
     * returns the Map of all the global ResultConfigs available in the current package.
     * Global ResultConfigs defined in ancestor packages will be included in this Map.
     *
     * @return a Map of Result Objects with the result name as the key
     * @see ResultConfig
     */
    public Map<String, ResultConfig> getAllGlobalResults() {
        Map<String, ResultConfig> retMap = new LinkedHashMap<String, ResultConfig>();

        if (!parents.isEmpty()) {
            for (PackageConfig parentConfig : parents) {
                retMap.putAll(parentConfig.getAllGlobalResults());
            }
        }

        retMap.putAll(getGlobalResultConfigs());

        return retMap;
    }

    /**
     * returns the Map of all InterceptorConfigs and InterceptorStackConfigs available in the current package.
     * InterceptorConfigs defined in ancestor packages will be included in this Map.
     *
     * @return a Map of InterceptorConfig and InterceptorStackConfig Objects with the ref-name as the key
     * @see InterceptorConfig
     * @see InterceptorStackConfig
     */
    public Map getAllInterceptorConfigs() {
        Map retMap = new LinkedHashMap();

        if (!parents.isEmpty()) {
            for (Iterator<PackageConfig> iterator = parents.iterator(); iterator.hasNext();) {
                PackageConfig parentContext = iterator.next();
                retMap.putAll(parentContext.getAllInterceptorConfigs());
            }
        }

        retMap.putAll(getInterceptorConfigs());

        return retMap;
    }

    /**
     * returns the Map of all the ResultTypeConfigs available in the current package.
     * ResultTypeConfigs defined in ancestor packages will be included in this Map.
     *
     * @return a Map of ResultTypeConfig Objects with the result type name as the key
     * @see ResultTypeConfig
     */
    public Map<String, ResultTypeConfig> getAllResultTypeConfigs() {
        Map<String, ResultTypeConfig> retMap = new LinkedHashMap<String, ResultTypeConfig>();

        if (!parents.isEmpty()) {
            for (Iterator<PackageConfig> iterator = parents.iterator(); iterator.hasNext();) {
                PackageConfig parentContext = iterator.next();
                retMap.putAll(parentContext.getAllResultTypeConfigs());
            }
        }

        retMap.putAll(getResultTypeConfigs());

        return retMap;
    }

    /**
     * returns the List of all the ExceptionMappingConfigs available in the current package.
     * ExceptionMappingConfigs defined in ancestor packages will be included in this list.
     *
     * @return a List of ExceptionMappingConfigs Objects with the result type name as the key
     * @see ExceptionMappingConfig
     */
    public List<ExceptionMappingConfig> getAllExceptionMappingConfigs() {
        List<ExceptionMappingConfig> allExceptionMappings = new ArrayList<ExceptionMappingConfig>();

        if (!parents.isEmpty()) {
            for (Iterator<PackageConfig> iterator = parents.iterator(); iterator.hasNext();) {
                PackageConfig parentContext = iterator.next();
                allExceptionMappings.addAll(parentContext.getAllExceptionMappingConfigs());
            }
        }

        allExceptionMappings.addAll(getGlobalExceptionMappingConfigs());

        return allExceptionMappings;
    }


    public void setDefaultInterceptorRef(String name) {
        defaultInterceptorRef = name;
    }

    public String getDefaultInterceptorRef() {
        return defaultInterceptorRef;
    }

    public void setDefaultActionRef(String name) {
        defaultActionRef = name;
    }

    public String getDefaultActionRef() {
        return defaultActionRef;
    }

    public void setDefaultClassRef( String defaultClassRef ) {
       this.defaultClassRef = defaultClassRef;
    }
    
    public String getDefaultClassRef() {
       return defaultClassRef;
    }
    
    /**
     * sets the default Result type for this package
     *
     * @param defaultResultType
     */
    public void setDefaultResultType(String defaultResultType) {
        this.defaultResultType = defaultResultType;
    }

    /**
     * Returns the default result type for this package.
     */
    public String getDefaultResultType() {
        return defaultResultType;
    }

    /**
     * gets the default interceptor-ref name. If this is not set on this PackageConfig, it searches the parent
     * PackageConfigs in order until it finds one.
     */
    public String getFullDefaultInterceptorRef() {
        if ((defaultInterceptorRef == null) && !parents.isEmpty()) {
            for (Iterator<PackageConfig> iterator = parents.iterator(); iterator.hasNext();) {
                PackageConfig parent = iterator.next();
                String parentDefault = parent.getFullDefaultInterceptorRef();

                if (parentDefault != null) {
                    return parentDefault;
                }
            }
        }

        return defaultInterceptorRef;
    }

    /**
     * gets the default action-ref name. If this is not set on this PackageConfig, it searches the parent
     * PackageConfigs in order until it finds one.
     */
    public String getFullDefaultActionRef() {
        if ((defaultActionRef == null) && !parents.isEmpty()) {
            for (Iterator<PackageConfig> iterator = parents.iterator(); iterator.hasNext();) {
                PackageConfig parent = iterator.next();
                String parentDefault = parent.getFullDefaultActionRef();

                if (parentDefault != null) {
                    return parentDefault;
                }
            }
        }
        return defaultActionRef;
    }

    /**
     * Returns the default result type for this package.
     * <p/>
     * If there is no default result type, but this package has parents - we will try to
     * look up the default result type of a parent.
     */
    public String getFullDefaultResultType() {
        if ((defaultResultType == null) && !parents.isEmpty()) {
            for (Iterator<PackageConfig> iterator = parents.iterator(); iterator.hasNext();) {
                PackageConfig parent = iterator.next();
                String parentDefault = parent.getFullDefaultResultType();

                if (parentDefault != null) {
                    return parentDefault;
                }
            }
        }

        return defaultResultType;
    }

    /**
     * gets the global ResultConfigs local to this package
     *
     * @return a Map of ResultConfig objects keyed by result name
     * @see ResultConfig
     */
    public Map<String, ResultConfig> getGlobalResultConfigs() {
        return globalResultConfigs;
    }

    /**
     * gets the InterceptorConfigs and InterceptorStackConfigs local to this package
     *
     * @return a Map of InterceptorConfig and InterceptorStackConfig objects keyed by ref-name
     * @see InterceptorConfig
     * @see InterceptorStackConfig
     */
    public Map getInterceptorConfigs() {
        return interceptorConfigs;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setNamespace(String namespace) {
        if (namespace == null) {
            this.namespace = "";
        } else {
            this.namespace = namespace;
        }
    }

    public String getNamespace() {
        return namespace;
    }

    public List<PackageConfig> getParents() {
        return new ArrayList<PackageConfig>(parents);
    }

    /**
     * gets the ResultTypeConfigs local to this package
     *
     * @return a Map of ResultTypeConfig objects keyed by result name
     * @see ResultTypeConfig
     */
    public Map<String, ResultTypeConfig> getResultTypeConfigs() {
        return resultTypeConfigs;
    }

    /**
     * gets the ExceptionMappingConfigs local to this package
     *
     * @return a Map of ExceptionMappingConfig objects keyed by result name
     * @see ExceptionMappingConfig
     */
    public List<ExceptionMappingConfig> getGlobalExceptionMappingConfigs() {
        return globalExceptionMappingConfigs;
    }

    public void addActionConfig(String name, ActionConfig action) {
        actionConfigs.put(name, action);
    }

    public void addAllParents(List<PackageConfig> parents) {
        for (PackageConfig config : parents) {
            addParent(config);
        }
    }

    public void addGlobalResultConfig(ResultConfig resultConfig) {
        globalResultConfigs.put(resultConfig.getName(), resultConfig);
    }

    public void addGlobalResultConfigs(Map resultConfigs) {
        globalResultConfigs.putAll(resultConfigs);
    }

    public void addExceptionMappingConfig(ExceptionMappingConfig exceptionMappingConfig) {
        globalExceptionMappingConfigs.add(exceptionMappingConfig);
    }

    public void addGlobalExceptionMappingConfigs(List exceptionMappingConfigs) {
        globalExceptionMappingConfigs.addAll(exceptionMappingConfigs);
    }

    public void addInterceptorConfig(InterceptorConfig config) {
        interceptorConfigs.put(config.getName(), config);
    }

    public void addInterceptorStackConfig(InterceptorStackConfig config) {
        interceptorConfigs.put(config.getName(), config);
    }

    public void addParent(PackageConfig parent) {
        if (this.equals(parent)) {
            LOG.error("A package cannot extend itself: " + name);
        }

        parents.add(0, parent);
    }

    public void addResultTypeConfig(ResultTypeConfig config) {
        resultTypeConfigs.put(config.getName(), config);
    }

    public boolean isNeedsRefresh() {
        return needsRefresh;
    }

    public void setNeedsRefresh(boolean needsRefresh) {
        this.needsRefresh = needsRefresh;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof PackageConfig)) {
            return false;
        }

        final PackageConfig packageConfig = (PackageConfig) o;

        if (isAbstract != packageConfig.isAbstract) {
            return false;
        }

        if ((actionConfigs != null) ? (!actionConfigs.equals(packageConfig.actionConfigs)) : (packageConfig.actionConfigs != null))
        {
            return false;
        }

        if ((defaultResultType != null) ? (!defaultResultType.equals(packageConfig.defaultResultType)) : (packageConfig.defaultResultType != null))
        {
            return false;
        }

        if ((defaultClassRef != null) ? (!defaultClassRef.equals(packageConfig.defaultClassRef)) : (packageConfig.defaultClassRef != null))
        {
            return false;
        }

        if ((globalResultConfigs != null) ? (!globalResultConfigs.equals(packageConfig.globalResultConfigs)) : (packageConfig.globalResultConfigs != null))
        {
            return false;
        }

        if ((interceptorConfigs != null) ? (!interceptorConfigs.equals(packageConfig.interceptorConfigs)) : (packageConfig.interceptorConfigs != null))
        {
            return false;
        }

        if ((name != null) ? (!name.equals(packageConfig.name)) : (packageConfig.name != null)) {
            return false;
        }

        if ((namespace != null) ? (!namespace.equals(packageConfig.namespace)) : (packageConfig.namespace != null)) {
            return false;
        }

        if ((parents != null) ? (!parents.equals(packageConfig.parents)) : (packageConfig.parents != null)) {
            return false;
        }

        if ((resultTypeConfigs != null) ? (!resultTypeConfigs.equals(packageConfig.resultTypeConfigs)) : (packageConfig.resultTypeConfigs != null))
        {
            return false;
        }

        if ((globalExceptionMappingConfigs != null) ? (!globalExceptionMappingConfigs.equals(packageConfig.globalExceptionMappingConfigs)) : (packageConfig.globalExceptionMappingConfigs != null))
        {
            return false;
        }

        return true;
    }

    public int hashCode() {
        // System.out.println("hashCode() + {Name:"+name+" abstract:"+isAbstract+" namespace:"+namespace+" parents: "+parents+"}");
        int result;
        result = ((name != null) ? name.hashCode() : 0);
        result = (29 * result) + ((parents != null) ? parents.hashCode() : 0);
        result = (29 * result) + ((actionConfigs != null) ? actionConfigs.hashCode() : 0);
        result = (29 * result) + ((globalResultConfigs != null) ? globalResultConfigs.hashCode() : 0);
        result = (29 * result) + ((interceptorConfigs != null) ? interceptorConfigs.hashCode() : 0);
        result = (29 * result) + ((resultTypeConfigs != null) ? resultTypeConfigs.hashCode() : 0);
        result = (29 * result) + ((globalExceptionMappingConfigs != null) ? globalExceptionMappingConfigs.hashCode() : 0);
        result = (29 * result) + ((defaultResultType != null) ? defaultResultType.hashCode() : 0);
        result = (29 * result) + ((defaultClassRef != null) ? defaultClassRef.hashCode() : 0);
        result = (29 * result) + ((namespace != null) ? namespace.hashCode() : 0);
        result = (29 * result) + (isAbstract ? 1 : 0);

        return result;
    }

    public void removeParent(PackageConfig parent) {
        parents.remove(parent);
    }

    public String toString() {
        return "{PackageConfig Name:" + name + " namespace:" + namespace + " abstract:" + isAbstract + " parents:" + parents + "}";
    }

    public int compareTo(Object o) {
        PackageConfig other = (PackageConfig) o;
        String full = namespace + "!" + name;
        String otherFull = other.namespace + "!" + other.name;

        // note, this isn't perfect (could come from different parents), but it is "good enough"
        return full.compareTo(otherFull);
    }

}
