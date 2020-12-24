/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.spring;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Collections;

/**
 * Simple implementation of the ObjectFactory that makes use of Spring's application context if one has been configured,
 * before falling back on the default mechanism of instantiating a new class using the class name. <p/> In order to use
 * this class in your application, you will need to instantiate a copy of this class and set it as XWork's ObjectFactory
 * before the xwork.xml file is parsed. In a servlet environment, this could be done using a ServletContextListener.
 *
 * @author Simon Stewart (sms@lateral.net)
 */
public class SpringObjectFactory extends ObjectFactory implements ApplicationContextAware {
    private static final Log log = LogFactory.getLog(SpringObjectFactory.class);

    protected ApplicationContext appContext;
    protected AutowireCapableBeanFactory autoWiringFactory;
    protected int autowireStrategy = AutowireCapableBeanFactory.AUTOWIRE_BY_NAME;
    private Map classes = Collections.synchronizedMap(new HashMap());
    private boolean useClassCache = true;

    @Inject(value="applicationContextPath",required=false)
    public void setApplicationContextPath(String ctx) {
        if (ctx != null) {
            setApplicationContext(new ClassPathXmlApplicationContext(ctx));
        }
    }
    
    /**
     * Set the Spring ApplicationContext that should be used to look beans up with.
     *
     * @param appContext The Spring ApplicationContext that should be used to look beans up with.
     */
    public void setApplicationContext(ApplicationContext appContext)
            throws BeansException {
        this.appContext = appContext;
        autoWiringFactory = findAutoWiringBeanFactory(this.appContext);
    }

    /**
     * Sets the autowiring strategy
     *
     * @param autowireStrategy
     */
    public void setAutowireStrategy(int autowireStrategy) {
        switch (autowireStrategy) {
            case AutowireCapableBeanFactory.AUTOWIRE_AUTODETECT:
                log.info("Setting autowire strategy to autodetect");
                this.autowireStrategy = autowireStrategy;
                break;
            case AutowireCapableBeanFactory.AUTOWIRE_BY_NAME:
                log.info("Setting autowire strategy to name");
                this.autowireStrategy = autowireStrategy;
                break;
            case AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE:
                log.info("Setting autowire strategy to type");
                this.autowireStrategy = autowireStrategy;
                break;
            case AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR:
                log.info("Setting autowire strategy to constructor");
                this.autowireStrategy = autowireStrategy;
                break;
            default:
                throw new IllegalStateException("Invalid autowire type set");
        }
    }

    public int getAutowireStrategy() {
        return autowireStrategy;
    }


    /**
     * If the given context is assignable to AutowireCapbleBeanFactory or contains a parent or a factory that is, then
     * set the autoWiringFactory appropriately.
     *
     * @param context
     */
    protected AutowireCapableBeanFactory findAutoWiringBeanFactory(ApplicationContext context) {
        if (context instanceof AutowireCapableBeanFactory) {
            // Check the context
            return (AutowireCapableBeanFactory) context;
        } else if (context instanceof ConfigurableApplicationContext) {
            // Try and grab the beanFactory
            return ((ConfigurableApplicationContext) context).getBeanFactory();
        } else if (context.getParent() != null) {
            // And if all else fails, try again with the parent context
            return findAutoWiringBeanFactory(context.getParent());
        }
        return null;
    }

    /**
     * Looks up beans using Spring's application context before falling back to the method defined in the {@link
     * ObjectFactory}.
     *
     * @param beanName     The name of the bean to look up in the application context
     * @param extraContext
     * @return A bean from Spring or the result of calling the overridden
     *         method.
     * @throws Exception
     */
    public Object buildBean(String beanName, Map extraContext, boolean injectInternal) throws Exception {
        Object o = null;
        try {
            o = appContext.getBean(beanName);
        } catch (NoSuchBeanDefinitionException e) {
            Class beanClazz = getClassInstance(beanName);
            o = buildBean(beanClazz, extraContext);
        }
        if (injectInternal) {
            injectInternalBeans(o);
        }
        return o;
    }

    /**
     * @param clazz
     * @param extraContext
     * @throws Exception
     */
    public Object buildBean(Class clazz, Map extraContext) throws Exception {
        Object bean;

        try {
            bean = autoWiringFactory.autowire(clazz, AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR, false);
        } catch (UnsatisfiedDependencyException e) {
            // Fall back
            bean = super.buildBean(clazz, extraContext);
        }

        bean = autoWiringFactory.applyBeanPostProcessorsBeforeInitialization(bean, bean.getClass().getName());
        // We don't need to call the init-method since one won't be registered.
        bean = autoWiringFactory.applyBeanPostProcessorsAfterInitialization(bean, bean.getClass().getName());
        return autoWireBean(bean, autoWiringFactory);
    }

    public Object autoWireBean(Object bean) {
        return autoWireBean(bean, autoWiringFactory);
    }

    /**
     * @param bean
     * @param autoWiringFactory
     */
    public Object autoWireBean(Object bean, AutowireCapableBeanFactory autoWiringFactory) {
        if (autoWiringFactory != null) {
            autoWiringFactory.autowireBeanProperties(bean,
                    autowireStrategy, false);
        }
        if (bean instanceof ApplicationContextAware) {
            ((ApplicationContextAware) bean).setApplicationContext(appContext);
        }
        
        injectInternalBeans(bean);

        return bean;
    }

    public Class getClassInstance(String className) throws ClassNotFoundException {
        Class clazz = null;
        if (useClassCache) {
            // this cache of classes is needed because Spring sucks at dealing with situations where the
            // class instance changes 
            clazz = (Class) classes.get(className);
        }

        if (clazz == null) {
            if (appContext.containsBean(className)) {
                clazz = appContext.getBean(className).getClass();
            } else {
                clazz = super.getClassInstance(className);
            }

            if (useClassCache) {
                classes.put(className, clazz);
            }
        }

        return clazz;
    }

    /**
     * This method sets the ObjectFactory used by XWork to this object. It's best used as the "init-method" of a Spring
     * bean definition in order to hook Spring and XWork together properly (as an alternative to the
     * org.apache.struts2.spring.lifecycle.SpringObjectFactoryListener)
     */
    public void initObjectFactory() {
        ObjectFactory.setObjectFactory(this);
    }

    /**
     * Allows for ObjectFactory implementations that support
     * Actions without no-arg constructors.
     *
     * @return false
     */
    public boolean isNoArgConstructorRequired() {
        return false;
    }

    /**
     *  Enable / disable caching of classes loaded by Spring.
     *  
     * @param useClassCache
     */
    public void setUseClassCache(boolean useClassCache) {
        this.useClassCache = useClassCache;
    }
}
