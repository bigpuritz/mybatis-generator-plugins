package com.google.code.mybatis.generator.plugins;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;

import java.util.List;
import java.util.Properties;

/**
 * Adds "selectOneByExample" method to the appropriate Mapper interface returning exactly one object instance.<br/>
 * Example configuration:<br/>
 * <tt>
 * <pre>
 * &lt;generatorConfiguration&gt;
 *  &lt;context ...&gt;
 *
 *      &lt;plugin type="com.google.code.mybatis.generator.plugins.SelectOneByExamplePlugin"/&gt;
 *      ...
 *
 *  &lt;/context&gt;
 * &lt;/generatorConfiguration&gt;
 * </pre>
 * </tt>
 * <br/> Properties:<br/> <ul> <li><strong>methodToGenerate</strong> (optional) : the name of the method to generate.
 * Default: <strong>selectOneByExample</strong></li> <li><strong>excludeClassNamesRegexp</strong> (optional): classes to
 * exclude from generation as regular expression. Default: none</li> </ul>
 *
 * @author Maxim Kalina
 * @version $Id$
 */
public class SelectOneByExamplePlugin extends PluginAdapter {

    private Config config;

    /**
     * {@inheritDoc}
     */
    public boolean validate(List<String> warnings) {
        if (this.config == null)
            this.config = new Config(getProperties());
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean clientSelectByExampleWithBLOBsMethodGenerated(Method method, Interface interfaze,
                                                                 IntrospectedTable introspectedTable) {

        if (!config.shouldExclude(interfaze.getType()))
            interfaze.addMethod(generateSelectOneByExample(method, introspectedTable));

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean clientSelectByExampleWithoutBLOBsMethodGenerated(Method method, Interface interfaze,
                                                                    IntrospectedTable introspectedTable) {

        if (!config.shouldExclude(interfaze.getType()))
            interfaze.addMethod(generateSelectOneByExample(method, introspectedTable));

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean clientSelectByExampleWithBLOBsMethodGenerated(Method method,
                                                                 TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        if (!config.shouldExclude(topLevelClass.getType()))
            topLevelClass.addMethod(generateSelectOneByExample(method, introspectedTable));
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean clientSelectByExampleWithoutBLOBsMethodGenerated(Method method,
                                                                    TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        if (!config.shouldExclude(topLevelClass.getType()))
            topLevelClass.addMethod(generateSelectOneByExample(method, introspectedTable));
        return true;
    }

    private Method generateSelectOneByExample(Method method, IntrospectedTable introspectedTable) {
        Method m = new Method(config.methodToGenerate);
        m.setVisibility(method.getVisibility());
        FullyQualifiedJavaType returnType = introspectedTable.getRules().calculateAllFieldsClass();
        m.setReturnType(returnType);

        List<String> annotations = method.getAnnotations();
        for (String a : annotations) {
            m.addAnnotation(a);
        }

        List<Parameter> params = method.getParameters();
        for (Parameter p : params) {
            m.addParameter(p);
        }

        context.getCommentGenerator().addGeneralMethodComment(m, introspectedTable);
        return m;
    }

    private static final class Config extends BasePluginConfig {

        private static final String defaultMethodToGenerate = "selectOneByExample";
        private static final String methodToGenerateKey = "methodToGenerate";

        private String methodToGenerate;

        protected Config(Properties props) {
            super(props);
            this.methodToGenerate = props.getProperty(methodToGenerateKey, defaultMethodToGenerate);
        }
    }
}
