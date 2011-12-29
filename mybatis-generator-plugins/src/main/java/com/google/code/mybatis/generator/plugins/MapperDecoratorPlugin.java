package com.google.code.mybatis.generator.plugins;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

/**
 * Decorates existing mapper methods.<br/> Example configuration:<br/> <tt>
 * <pre>
 * &lt;generatorConfiguration&gt;
 *  &lt;context ...&gt;
 *
 *      &lt;plugin type="com.google.code.mybatis.generator.plugins.MapperDecoratorPlugin"&gt;
 *          &lt;property name="methodToDecorate" value="selectByExample"/&gt;
 *          &lt;property name="methodToGenerate" value="selectByExampleDecorated"/&gt;
 *          &lt;property name="sql" value="select * from (#{methodToDecorate}) x where x.a = ${a} and x.b = ${b}"/&gt;
 *          &lt;property name="excludeClassNamesRegexp" value="com.*Blog"/&gt;
 *          &lt;property name="a" value="long"/&gt;
 *          &lt;property name="b" value="long"/&gt;
 *      &lt;/plugin&gt;
 *  &lt;/context&gt;
 * &lt;/generatorConfiguration&gt;
 *
 * </pre>
 * </tt> <br/> Properties:<br/> <ul> <li><strong>methodToDecorate</strong> (required) : the name of the mapper method to
 * decorate.</li> <li><strong>methodToGenerate</strong> (required) : the name of the new mapper method to generate.</li>
 * <li><strong>excludeClassNamesRegexp</strong> (optional): classes to exclude from generation as regular expression.
 * Default: none</li> <li><strong>sql</strong> (required) : is an sql statement to use in the new method</li>
 * <li>(Optional) Any number of property definitions <tt>&lt;property name="propName" value="propType"/&gt;</tt>
 * describing new properties to add to the corresponding XXXExample class</li> </ul>
 *
 * @author Maxim Kalina
 * @version $Id$
 */
public class MapperDecoratorPlugin extends PluginAdapter {

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
    @SuppressWarnings("unchecked")
    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass,
                                              IntrospectedTable introspectedTable) {

        Properties props = getProperties();
        Enumeration<String> propNames = (Enumeration<String>) props.propertyNames();
        while (propNames.hasMoreElements()) {

            String propName = propNames.nextElement();
            if (propName == null || config.isReservedPropertyKey(propName))
                continue;

            String propType = props.getProperty(propName);

            FullyQualifiedJavaType fqType = new FullyQualifiedJavaType(propType);
            Field f = new Field(propName, fqType);
            f.setVisibility(JavaVisibility.PROTECTED);
            topLevelClass.addField(f);

            String capitalizedPropName = propName.substring(0, 1).toUpperCase()
                    + propName.substring(1);

            Method getter = new Method("get" + capitalizedPropName);
            getter.setVisibility(JavaVisibility.PUBLIC);
            getter.setReturnType(fqType);
            getter.addBodyLine("return this." + propName + ";");
            context.getCommentGenerator().addGeneralMethodComment(getter, introspectedTable);
            topLevelClass.addMethod(getter);

            Method setter = new Method("set" + capitalizedPropName);
            setter.setVisibility(JavaVisibility.PUBLIC);
            setter.addParameter(new Parameter(fqType, propName));
            setter.addBodyLine("this." + propName + " = " + propName + ";");
            context.getCommentGenerator().addGeneralMethodComment(setter, introspectedTable);
            topLevelClass.addMethod(setter);
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean providerSelectByExampleWithBLOBsMethodGenerated(Method method, TopLevelClass topLevelClass,
                                                                   IntrospectedTable introspectedTable) {
        if (config.shouldApplyGeneration(topLevelClass.getType(), method))
            topLevelClass.addMethod(generateDecoratedProviderMethod(method, topLevelClass, introspectedTable));
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean providerSelectByExampleWithoutBLOBsMethodGenerated(Method method, TopLevelClass topLevelClass,
                                                                      IntrospectedTable introspectedTable) {
        if (config.shouldApplyGeneration(topLevelClass.getType(), method))
            topLevelClass.addMethod(generateDecoratedProviderMethod(method, topLevelClass, introspectedTable));
        return true;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean providerCountByExampleMethodGenerated(Method method, TopLevelClass topLevelClass,
                                                         IntrospectedTable introspectedTable) {
        if (config.shouldApplyGeneration(topLevelClass.getType(), method))
            topLevelClass.addMethod(generateDecoratedProviderMethod(method, topLevelClass, introspectedTable));
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean providerDeleteByExampleMethodGenerated(Method method, TopLevelClass topLevelClass,
                                                          IntrospectedTable introspectedTable) {
        if (config.shouldApplyGeneration(topLevelClass.getType(), method))
            topLevelClass.addMethod(generateDecoratedProviderMethod(method, topLevelClass, introspectedTable));
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean providerInsertSelectiveMethodGenerated(Method method, TopLevelClass topLevelClass,
                                                          IntrospectedTable introspectedTable) {
        if (config.shouldApplyGeneration(topLevelClass.getType(), method))
            topLevelClass.addMethod(generateDecoratedProviderMethod(method, topLevelClass, introspectedTable));
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean providerUpdateByExampleSelectiveMethodGenerated(Method method, TopLevelClass topLevelClass,
                                                                   IntrospectedTable introspectedTable) {
        if (config.shouldApplyGeneration(topLevelClass.getType(), method))
            topLevelClass.addMethod(generateDecoratedProviderMethod(method, topLevelClass, introspectedTable));
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean providerUpdateByExampleWithBLOBsMethodGenerated(Method method, TopLevelClass topLevelClass,
                                                                   IntrospectedTable introspectedTable) {
        if (config.shouldApplyGeneration(topLevelClass.getType(), method))
            topLevelClass.addMethod(generateDecoratedProviderMethod(method, topLevelClass, introspectedTable));
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean providerUpdateByExampleWithoutBLOBsMethodGenerated(Method method, TopLevelClass topLevelClass,
                                                                      IntrospectedTable introspectedTable) {
        if (config.shouldApplyGeneration(topLevelClass.getType(), method))
            topLevelClass.addMethod(generateDecoratedProviderMethod(method, topLevelClass, introspectedTable));
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean providerUpdateByPrimaryKeySelectiveMethodGenerated(Method method, TopLevelClass topLevelClass,
                                                                      IntrospectedTable introspectedTable) {
        if (config.shouldApplyGeneration(topLevelClass.getType(), method))
            topLevelClass.addMethod(generateDecoratedProviderMethod(method, topLevelClass, introspectedTable));
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean clientSelectByExampleWithBLOBsMethodGenerated(Method method, Interface interfaze,
                                                                 IntrospectedTable introspectedTable) {
        if (config.shouldApplyGeneration(interfaze.getType(), method))
            interfaze.addMethod(generateDecoratedMapperMethod(method, introspectedTable));
        return true;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean clientSelectByExampleWithoutBLOBsMethodGenerated(Method method, Interface interfaze,
                                                                    IntrospectedTable introspectedTable) {
        if (config.shouldApplyGeneration(interfaze.getType(), method))
            interfaze.addMethod(generateDecoratedMapperMethod(method, introspectedTable));
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean clientSelectByExampleWithBLOBsMethodGenerated(Method method, TopLevelClass topLevelClass,
                                                                 IntrospectedTable introspectedTable) {
        if (config.shouldApplyGeneration(topLevelClass.getType(), method))
            topLevelClass.addMethod(generateDecoratedMapperMethod(method, introspectedTable));
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean clientSelectByExampleWithoutBLOBsMethodGenerated(Method method, TopLevelClass topLevelClass,
                                                                    IntrospectedTable introspectedTable) {
        if (config.shouldApplyGeneration(topLevelClass.getType(), method))
            topLevelClass.addMethod(generateDecoratedMapperMethod(method, introspectedTable));
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean clientCountByExampleMethodGenerated(Method method, Interface interfaze,
                                                       IntrospectedTable introspectedTable) {
        if (config.shouldApplyGeneration(interfaze.getType(), method))
            interfaze.addMethod(generateDecoratedMapperMethod(method, introspectedTable));
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean clientCountByExampleMethodGenerated(Method method, TopLevelClass topLevelClass,
                                                       IntrospectedTable introspectedTable) {
        if (config.shouldApplyGeneration(topLevelClass.getType(), method))
            topLevelClass.addMethod(generateDecoratedMapperMethod(method, introspectedTable));
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean clientDeleteByExampleMethodGenerated(Method method, Interface interfaze,
                                                        IntrospectedTable introspectedTable) {
        if (config.shouldApplyGeneration(interfaze.getType(), method))
            interfaze.addMethod(generateDecoratedMapperMethod(method, introspectedTable));
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean clientDeleteByExampleMethodGenerated(Method method, TopLevelClass topLevelClass,
                                                        IntrospectedTable introspectedTable) {
        if (config.shouldApplyGeneration(topLevelClass.getType(), method))
            topLevelClass.addMethod(generateDecoratedMapperMethod(method, introspectedTable));
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean clientDeleteByPrimaryKeyMethodGenerated(Method method, Interface interfaze,
                                                           IntrospectedTable introspectedTable) {
        if (config.shouldApplyGeneration(interfaze.getType(), method))
            interfaze.addMethod(generateDecoratedMapperMethod(method, introspectedTable));
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean clientDeleteByPrimaryKeyMethodGenerated(Method method, TopLevelClass topLevelClass,
                                                           IntrospectedTable introspectedTable) {
        if (config.shouldApplyGeneration(topLevelClass.getType(), method))
            topLevelClass.addMethod(generateDecoratedMapperMethod(method, introspectedTable));
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean clientInsertMethodGenerated(Method method, Interface interfaze,
                                               IntrospectedTable introspectedTable) {
        if (config.shouldApplyGeneration(interfaze.getType(), method))
            interfaze.addMethod(generateDecoratedMapperMethod(method, introspectedTable));
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean clientInsertMethodGenerated(Method method, TopLevelClass topLevelClass,
                                               IntrospectedTable introspectedTable) {
        if (config.shouldApplyGeneration(topLevelClass.getType(), method))
            topLevelClass.addMethod(generateDecoratedMapperMethod(method, introspectedTable));
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean clientSelectByPrimaryKeyMethodGenerated(Method method, Interface interfaze,
                                                           IntrospectedTable introspectedTable) {
        if (config.shouldApplyGeneration(interfaze.getType(), method))
            interfaze.addMethod(generateDecoratedMapperMethod(method, introspectedTable));
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean clientSelectByPrimaryKeyMethodGenerated(Method method, TopLevelClass topLevelClass,
                                                           IntrospectedTable introspectedTable) {
        if (config.shouldApplyGeneration(topLevelClass.getType(), method))
            topLevelClass.addMethod(generateDecoratedMapperMethod(method, introspectedTable));
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean clientUpdateByExampleSelectiveMethodGenerated(Method method, Interface interfaze,
                                                                 IntrospectedTable introspectedTable) {
        if (config.shouldApplyGeneration(interfaze.getType(), method))
            interfaze.addMethod(generateDecoratedMapperMethod(method, introspectedTable));
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean clientUpdateByExampleSelectiveMethodGenerated(Method method, TopLevelClass topLevelClass,
                                                                 IntrospectedTable introspectedTable) {
        if (config.shouldApplyGeneration(topLevelClass.getType(), method))
            topLevelClass.addMethod(generateDecoratedMapperMethod(method, introspectedTable));
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean clientUpdateByExampleWithBLOBsMethodGenerated(Method method, Interface interfaze,
                                                                 IntrospectedTable introspectedTable) {
        if (config.shouldApplyGeneration(interfaze.getType(), method))
            interfaze.addMethod(generateDecoratedMapperMethod(method, introspectedTable));
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean clientUpdateByExampleWithBLOBsMethodGenerated(Method method, TopLevelClass topLevelClass,
                                                                 IntrospectedTable introspectedTable) {
        if (config.shouldApplyGeneration(topLevelClass.getType(), method))
            topLevelClass.addMethod(generateDecoratedMapperMethod(method, introspectedTable));
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean clientUpdateByExampleWithoutBLOBsMethodGenerated(Method method, Interface interfaze,
                                                                    IntrospectedTable introspectedTable) {
        if (config.shouldApplyGeneration(interfaze.getType(), method))
            interfaze.addMethod(generateDecoratedMapperMethod(method, introspectedTable));
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean clientUpdateByExampleWithoutBLOBsMethodGenerated(Method method, TopLevelClass topLevelClass,
                                                                    IntrospectedTable introspectedTable) {
        if (config.shouldApplyGeneration(topLevelClass.getType(), method))
            topLevelClass.addMethod(generateDecoratedMapperMethod(method, introspectedTable));
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean clientUpdateByPrimaryKeySelectiveMethodGenerated(Method method, Interface interfaze,
                                                                    IntrospectedTable introspectedTable) {
        if (config.shouldApplyGeneration(interfaze.getType(), method))
            interfaze.addMethod(generateDecoratedMapperMethod(method, introspectedTable));
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean clientUpdateByPrimaryKeySelectiveMethodGenerated(Method method, TopLevelClass topLevelClass,
                                                                    IntrospectedTable introspectedTable) {
        if (config.shouldApplyGeneration(topLevelClass.getType(), method))
            topLevelClass.addMethod(generateDecoratedMapperMethod(method, introspectedTable));
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean clientUpdateByPrimaryKeyWithBLOBsMethodGenerated(Method method, Interface interfaze,
                                                                    IntrospectedTable introspectedTable) {
        if (config.shouldApplyGeneration(interfaze.getType(), method))
            interfaze.addMethod(generateDecoratedMapperMethod(method, introspectedTable));
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean clientUpdateByPrimaryKeyWithBLOBsMethodGenerated(Method method, TopLevelClass topLevelClass,
                                                                    IntrospectedTable introspectedTable) {
        if (config.shouldApplyGeneration(topLevelClass.getType(), method))
            topLevelClass.addMethod(generateDecoratedMapperMethod(method, introspectedTable));
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean clientUpdateByPrimaryKeyWithoutBLOBsMethodGenerated(Method method, Interface interfaze,
                                                                       IntrospectedTable introspectedTable) {
        if (config.shouldApplyGeneration(interfaze.getType(), method))
            interfaze.addMethod(generateDecoratedMapperMethod(method, introspectedTable));
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean clientUpdateByPrimaryKeyWithoutBLOBsMethodGenerated(Method method, TopLevelClass topLevelClass,
                                                                       IntrospectedTable introspectedTable) {
        if (config.shouldApplyGeneration(topLevelClass.getType(), method))
            topLevelClass.addMethod(generateDecoratedMapperMethod(method, introspectedTable));
        return true;
    }


    private Method generateDecoratedMapperMethod(Method method, IntrospectedTable introspectedTable) {

        Method methodToGenerate = new Method(config.methodToGenerateName);
        methodToGenerate.setVisibility(method.getVisibility());
        methodToGenerate.setReturnType(method.getReturnType());
        context.getCommentGenerator().addGeneralMethodComment(methodToGenerate, introspectedTable);

        List<String> annotations = method.getAnnotations();

        for (String a : annotations) {
            if (a.matches("@.*Provider.*")) {
                methodToGenerate.addAnnotation(a.replace(config.methodToDecorateName, config.methodToGenerateName));
            } else
                methodToGenerate.addAnnotation(a);
        }

        List<Parameter> params = method.getParameters();
        for (Parameter p : params) {
            methodToGenerate.addParameter(p);
        }
        return methodToGenerate;
    }


    private Method generateDecoratedProviderMethod(Method method, TopLevelClass topLevelClass,
                                                   IntrospectedTable introspectedTable) {

        Method m = new Method(config.methodToGenerateName);
        m.setVisibility(method.getVisibility());
        m.setReturnType(method.getReturnType());
        List<Parameter> params = method.getParameters();
        for (Parameter p : params) {
            m.addParameter(p);
        }

        StringBuilder sb =
                new StringBuilder("String sql = ")
                        .append("this.").append(method.getName()).append("(");
        for (Parameter p : params) {
            sb.append(p.getName());
            sb.append(",");
        }

        sb.deleteCharAt(sb.length() - 1);
        sb.append(");");

        m.addBodyLine(sb.toString());
        m.addBodyLine("return \"" + config.sql + "\".replace(\"#{methodToDecorate}\", sql);");
        return m;
    }


    private static class Config extends BasePluginConfig {

        private static final String methodToGenerateKey = "methodToGenerate";
        private static final String methodToDecorateKey = "methodToDecorate";
        private static final String sqlKey = "sql";

        private static final List<String> reservedPropertyKeys = Arrays.asList(methodToGenerateKey, methodToDecorateKey,
                sqlKey);

        private String sql;
        private String methodToDecorateName;
        private String methodToGenerateName;

        private Config(Properties props) {
            super(props);

            methodToGenerateName = props.getProperty(methodToGenerateKey);
            if (methodToGenerateName == null)
                throw new IllegalStateException("Property '" + methodToGenerateKey + "' should be specified for plugin "
                        + this.getClass().getName());

            methodToDecorateName = props.getProperty(methodToDecorateKey);
            if (methodToDecorateName == null)
                throw new IllegalStateException("Property '" + methodToDecorateKey + "' should be specified for plugin "
                        + this.getClass().getName());

            sql = props.getProperty(sqlKey);
            if (sql == null)
                throw new IllegalStateException("Property '" + sqlKey + "' should be specified for plugin "
                        + this.getClass().getName());
        }

        boolean isReservedPropertyKey(String key) {
            return reservedPropertyKeys.contains(key);
        }

        boolean shouldApplyGeneration(FullyQualifiedJavaType type, Method method) {
            return !shouldExclude(type) && methodToDecorateName.equals(method.getName());
        }
    }
}
