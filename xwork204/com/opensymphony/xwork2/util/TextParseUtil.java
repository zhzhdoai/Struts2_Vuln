/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.util;

import com.opensymphony.xwork2.util.TextUtils;

import java.util.HashSet;
import java.util.Set;


/**
 * Utility class for text parsing.
 *
 * @author Jason Carreira
 * @author Rainer Hermanns
 * @author tm_jee
 * 
 * @version $Date: 2007-07-16 16:34:17 +0200 (Mo, 16 Jul 2007) $ $Id: TextParseUtil.java 1544 2007-07-16 14:34:17Z mrdon $
 */
public class TextParseUtil {
    
    private static final int MAX_RECURSION = 1;

    /**
     * Converts all instances of ${...} in <code>expression</code> to the value returned
     * by a call to {@link ValueStack#findValue(java.lang.String)}. If an item cannot
     * be found on the stack (null is returned), then the entire variable ${...} is not
     * displayed, just as if the item was on the stack but returned an empty string.
     *
     * @param expression an expression that hasn't yet been translated
     * @return the parsed expression
     */
    public static String translateVariables(String expression, ValueStack stack) {
        return translateVariables('$', expression, stack, String.class, null).toString();
    }
    
    
    /**
     * Function similarly as {@link #translateVariables(char, String, ValueStack)} 
     * except for the introduction of an additional <code>evaluator</code> that allows
     * the parsed value to be evaluated by the <code>evaluator</code>. The <code>evaluator</code>
     * could be null, if it is it will just be skipped as if it is just calling 
     * {@link #translateVariables(char, String, ValueStack)}.
     * 
     * <p/>
     * 
     * A typical use-case would be when we need to URL Encode the parsed value. To do so 
     * we could just supply a URLEncodingEvaluator for example. 
     * 
     * @param expression
     * @param stack
     * @param evaluator The parsed Value evaluator (could be null).
     * @return the parsed (and possibly evaluated) variable String.
     */
    public static String translateVariables(String expression, ValueStack stack, ParsedValueEvaluator evaluator) {
    	return translateVariables('$', expression, stack, String.class, evaluator).toString();
    }

    /**
     * Converts all instances of ${...} in <code>expression</code> to the value returned
     * by a call to {@link ValueStack#findValue(java.lang.String)}. If an item cannot
     * be found on the stack (null is returned), then the entire variable ${...} is not
     * displayed, just as if the item was on the stack but returned an empty string.
     *
     * @param open
     * @param expression
     * @param stack
     * @return Translated variable String
     */
    public static String translateVariables(char open, String expression, ValueStack stack) {
        return translateVariables(open, expression, stack, String.class, null).toString();
    }

    /**
     * Converted object from variable translation.
     *
     * @param open
     * @param expression
     * @param stack
     * @param asType
     * @return Converted object from variable translation.
     */
    public static Object translateVariables(char open, String expression, ValueStack stack, Class asType) {
    	return translateVariables(open, expression, stack, asType, null);
    }
    
    /**
     * Converted object from variable translation.
     *
     * @param open
     * @param expression
     * @param stack
     * @param asType
     * @param evaluator
     * @return Converted object from variable translation.
     */
    public static Object translateVariables(char open, String expression, ValueStack stack, Class asType, ParsedValueEvaluator evaluator) {
        return translateVariables(open, expression, stack, asType, evaluator, MAX_RECURSION);
    }
    
    /**
     * Converted object from variable translation.
     *
     * @param open
     * @param expression
     * @param stack
     * @param asType
     * @param evaluator
     * @return Converted object from variable translation.
     */
    public static Object translateVariables(char open, String expression, ValueStack stack, Class asType, ParsedValueEvaluator evaluator, int maxLoopCount) {
        // deal with the "pure" expressions first!
        //expression = expression.trim();
        Object result = expression;
        int loopCount = 1;
        int pos = 0;
        while (true) {
            
            int start = expression.indexOf(open + "{", pos);
            if (start == -1) {
                pos = 0;
                loopCount++;
                start = expression.indexOf(open + "{");
            }
            if (loopCount > maxLoopCount) {
                // translateVariables prevent infinite loop / expression recursive evaluation
                break;
            }
            int length = expression.length();
            int x = start + 2;
            int end;
            char c;
            int count = 1;
            while (start != -1 && x < length && count != 0) {
                c = expression.charAt(x++);
                if (c == '{') {
                    count++;
                } else if (c == '}') {
                    count--;
                }
            }
            end = x - 1;

            if ((start != -1) && (end != -1) && (count == 0)) {
                String var = expression.substring(start + 2, end);

                Object o = stack.findValue(var, asType);
                if (evaluator != null) {
                	o = evaluator.evaluate(o);
                }
                

                String left = expression.substring(0, start);
                String right = expression.substring(end + 1);
                String middle = null;
                if (o != null) {
                    middle = o.toString();
                    if (!TextUtils.stringSet(left)) {
                        result = o;
                    } else {
                        result = left + middle;
                    }
    
                    if (TextUtils.stringSet(right)) {
                        result = result + right;
                    }

                    expression = left + middle + right;
                } else {
                    // the variable doesn't exist, so don't display anything
                    result = left + right;
                    expression = left + right;
                }
                pos = (left != null && left.length() > 0 ? left.length() - 1: 0) +
                      (middle != null && middle.length() > 0 ? middle.length() - 1: 0) +
                      1;
                pos = Math.max(pos, 1);
            } else {
                break;
            }
        }

        return XWorkConverter.getInstance().convertValue(stack.getContext(), result, asType);
    }

    /**
     * Returns a set from comma delimted Strings.
     * @param s The String to parse.
     * @return A set from comma delimted Strings.
     */
    public static Set commaDelimitedStringToSet(String s) {
        Set set = new HashSet();
        String[] split = s.split(",");
        for (int i = 0; i < split.length; i++) {
            String trimmed = split[i].trim();
            if (trimmed.length() > 0)
                set.add(trimmed);
        }
        return set;
    }
    
    
    /**
     * A parsed value evaluator for {@link TextParseUtil}. It could be supplied by 
     * calling {@link TextParseUtil#translateVariables(char, String, ValueStack, Class, ParsedValueEvaluator)}.
     * 
     * <p/>
     * 
     * By supplying this <code>ParsedValueEvaluator</code>, the parsed value
     * (parsed against the value stack) value will be
     * given to <code>ParsedValueEvaluator</code> to be evaluated before the 
     * translateVariable process goes on. 
     * 
     * <p/>
     * 
     * A typical use-case would be to have a custom <code>ParseValueEvaluator</code>
     * to URL Encode the parsed value.
     * 
     * @author tm_jee
     * 
     * @version $Date: 2007-07-16 16:34:17 +0200 (Mo, 16 Jul 2007) $ $Id: TextParseUtil.java 1544 2007-07-16 14:34:17Z mrdon $
     */
    public static interface ParsedValueEvaluator {
    	
    	/**
    	 * Evaluated the value parsed by Ognl value stack.
    	 * 
    	 * @param parsedValue - value parsed by ognl value stack
    	 * @return return the evaluted value.
    	 */
    	Object evaluate(Object parsedValue);
    }
}
