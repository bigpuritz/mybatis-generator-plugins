package com.google.code.mybatis.generator.plugins;

import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;

import java.util.Properties;
import java.util.regex.Pattern;

/**
 * @author Maxim Kalina
 * @version $Id$
 */
public abstract class BasePluginConfig {

    private static final String excludeClassNamesRegexpKey = "excludeClassNamesRegexp";

    private Pattern excludeClassNamesRegexp;

    protected BasePluginConfig(Properties props) {

        Pattern excludeClassNamesRegexp = null;
        String regexp = props.getProperty(excludeClassNamesRegexpKey, null);
        if (regexp != null)
            this.excludeClassNamesRegexp = Pattern.compile(regexp);
    }


    boolean shouldExclude(FullyQualifiedJavaType type) {
        return this.shouldExclude(type.getFullyQualifiedName());
    }

    boolean shouldExclude(String className) {

        if (excludeClassNamesRegexp != null && excludeClassNamesRegexp.matcher(className).matches()) {
            return true;
        }

        return false;
    }
}
