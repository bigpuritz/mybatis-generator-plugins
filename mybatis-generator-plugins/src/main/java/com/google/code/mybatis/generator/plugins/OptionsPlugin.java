package com.google.code.mybatis.generator.plugins;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Adds @Options annotation to the specified mapper method.<br/>
 * Example configuration:<br/>
 * <tt><pre>
  * &lt;generatorConfiguration&gt;
 *  &lt;context ...&gt;
 *
 *      &lt;plugin type="com.google.code.mybatis.generator.plugins.OptionsPlugin"&gt;
 *          &lt;property name=".*UserMapper#.*ByExample"
 *                     value="fetchSize=1,timeout=0,useCache=true,flushCache=true" /&gt;
 *          &lt;property name=".*GroupMapper#.*ByExample"
 *                     value="fetchSize=10,timeout=10,useCache=false,flushCache=true" /&gt;
 *          ...
 *
 *      &lt;/plugin&gt;
 *      ...
 *
 *  &lt;/context&gt;
 * &lt;/generatorConfiguration&gt;
 * </pre></tt>
 * This plugin can deal with any number of properties defined using following pattern:<br/>
 * <ol>
 *     <li><strong>name</strong> is a regular expression pair to match a fully qualified name of the class / method.
 *     It should be defined using following rule: <strong>classNameRegexp#methodnameRegexp</strong></li>
 *     <li><strong>value</strong> is a value of the @Options annotation</li>
 * </ol>
 *
 *
 * @author Maxim Kalina
 * @version $Id$
 */
public class OptionsPlugin extends PluginAdapter {

    private Config config;

    public boolean validate(List<String> warnings) {
        if (config == null)
            config = new Config(getProperties());
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {

        if (interfaze == null)
            return true;


        List<Method> methods = interfaze.getMethods();
        for (Method m : methods) {

            String value = config.getOptionsValue(interfaze.getType().getFullyQualifiedName(), m.getName());
            if (value != null) {

                interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Options"));

                String existingOptionsAnnotation = null;
                List<String> annotations = m.getAnnotations();
                Iterator<String> it = annotations.iterator();
                while (it.hasNext()) {
                    String annotation = it.next();
                    if (annotation.startsWith("@Options")) {
                        existingOptionsAnnotation = annotation;
                        it.remove();
                        break;
                    }
                }

                if (existingOptionsAnnotation != null) {
                    m.addAnnotation(existingOptionsAnnotation.replace(")", "," + value + ")"));
                } else {
                    m.addAnnotation("@Options(" + value + ")");
                }
            }

        }


        return true;
    }

    private static final class Config {

        private List<OptionsConfigItem> items;

        private Config(Properties props) {

            this.items = new ArrayList<OptionsConfigItem>();

            Enumeration e = props.propertyNames();
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                items.add(OptionsConfigItem.valueOf(key, props.getProperty(key)));
            }
        }

        private String getOptionsValue(String classFQN, String methodName) {

            for (OptionsConfigItem item : items) {
                if (item.classRegexp.matcher(classFQN).matches() && item.methodRegexp.matcher(methodName).matches())
                    return item.optionsValue;
            }
            return null;

        }
    }


    private static final class OptionsConfigItem {

        private Pattern classRegexp;

        private Pattern methodRegexp;

        private String optionsValue;

        private OptionsConfigItem(Pattern classRegexp, Pattern methodRegexp, String optionsValue) {
            this.classRegexp = classRegexp;
            this.methodRegexp = methodRegexp;
            this.optionsValue = optionsValue;
        }

        public static final OptionsConfigItem valueOf(String key, String value) {

            if (key == null) throw new IllegalArgumentException("Property's key should be specified!");
            if (value == null) throw new IllegalArgumentException("Property's value should be specified!");

            if (!key.contains("#")) throw new IllegalArgumentException("Wrong format for property key '" + key + "' " +
                    "found! Expected: name=\"classRegexp#methodRegexp\"");

            String classRegexp = key.substring(0, key.indexOf("#"));
            String methodRegexp = key.substring(key.indexOf("#") + 1);

            return new OptionsConfigItem(Pattern.compile(classRegexp), Pattern.compile(methodRegexp), value);

        }
    }
}
