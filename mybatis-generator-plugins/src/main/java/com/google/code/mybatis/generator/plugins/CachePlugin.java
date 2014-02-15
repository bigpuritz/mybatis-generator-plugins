/*
 * Copyright (c) 2014.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.code.mybatis.generator.plugins;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * Provides caching support to the mybatis generator by adding @CacheNamespace annotation to the generated mapper
 * interface.<br>
 * <p/>
 * Example configuration:<br/>
 * <tt><pre>
 * &lt;generatorConfiguration&gt;
 *  &lt;context ...&gt;
 * <p/>
 *      &lt;plugin type="com.google.code.mybatis.generator.plugins.CachePlugin"&gt;
 *          &lt;property name=".*FooMapper"
 *                     value="implementation=org.mybatis.caches.ehcache.LoggingEhcache.class,eviction=org.apache.ibatis.cache.decorators.LruCache.class,flushInterval=100,size=100" /&gt;
 *          &lt;property name=".*BarMapper"
 *                     value="implementation=org.mybatis.caches.ehcache.LoggingEhcache.class" /&gt;
 *          ...
 * <p/>
 *      &lt;/plugin&gt;
 *      ...
 * <p/>
 *  &lt;/context&gt;
 * &lt;/generatorConfiguration&gt;
 * </pre></tt>
 * This plugin can deal with any number of properties defined using following pattern:<br/>
 * <ol>
 * <li><strong>name</strong> is a regular expression to match a fully qualified name of the class.</li>
 * <li><strong>value</strong> is the value of the @CacheNamespace annotation</li>
 * </ol>
 *
 * @author Maxim Kalina
 * @version $Id$
 */
public class CachePlugin extends PluginAdapter {

    private static final String cacheNamespaceFQN = "org.apache.ibatis.annotations.CacheNamespace";

    private Config config;

    /**
     * {@inheritDoc}
     */
    public boolean validate(List<String> warnings) {
        if (config == null)
            config = new Config(getProperties());

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass,
                                   IntrospectedTable introspectedTable) {


        String cacheValue = config.getCacheValue(interfaze.getType().getFullyQualifiedName());
        if (cacheValue == null)
            return true;

        interfaze.addImportedType(new FullyQualifiedJavaType(cacheNamespaceFQN));

        StringBuilder sb = new StringBuilder();
        sb.append("@CacheNamespace(\n").append(cacheValue).append("\n)");
        interfaze.addAnnotation(sb.toString());

        return true;
    }


    private static final class Config {

        private List<CacheConfigItem> items;

        private Config(Properties props) {

            this.items = new ArrayList<CacheConfigItem>();

            Enumeration e = props.propertyNames();
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                items.add(CacheConfigItem.valueOf(key, props.getProperty(key)));
            }
        }

        private String getCacheValue(String classFQN) {

            for (CacheConfigItem item : items) {
                if (item.classRegexp.matcher(classFQN).matches())
                    return item.cacheValue;
            }
            return null;

        }
    }


    private static final class CacheConfigItem {

        private Pattern classRegexp;

        private String cacheValue;

        private CacheConfigItem(Pattern classRegexp, String cacheValue) {
            this.classRegexp = classRegexp;
            this.cacheValue = cacheValue;
        }

        public static final CacheConfigItem valueOf(String key, String value) {

            if (key == null) throw new IllegalArgumentException("Property's key should be specified!");
            if (value == null) throw new IllegalArgumentException("Property's value should be specified!");

            return new CacheConfigItem(Pattern.compile(key), value);

        }
    }

}
