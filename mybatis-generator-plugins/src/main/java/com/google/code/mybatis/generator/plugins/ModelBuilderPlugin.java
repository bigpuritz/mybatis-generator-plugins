package com.google.code.mybatis.generator.plugins;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;

import java.util.List;
import java.util.Properties;

/**
 * Adds "Builder" as inner-class to the generated model class. <br/> Using builder's fluent interface model objects can
 * be created in more efficient and intuitive way. <br/><br/> Example configuration:<br/> <tt>
 * <pre>
 * &lt;generatorConfiguration&gt;
 *  &lt;context ...&gt;
 *
 *      &lt;plugin type="com.google.code.mybatis.generator.plugins.ModelBuilderPlugin"/&gt;
 *      ...
 *
 *  &lt;/context&gt;
 * &lt;/generatorConfiguration&gt;
 * </pre>
 * </tt> <br/><br/> Alternative you can specify custom class name for the generated builder class and exclude specific
 * classes from generation process using regular expression.<br/> <tt>
 * <pre>
 * &lt;generatorConfiguration&gt;
 *  &lt;context ...&gt;
 *
 *      &lt;plugin type="com.google.code.mybatis.generator.plugins.ModelBuilderPlugin"&gt;
 *          &lt;property name="builderClassName" value="MyBuilder" /&gt;
 *          &lt;property name="excludeClassNamesRegexp" value="com.mycompany.*My.*Class" /&gt;
 *      &lt;plugin&gt;
 *      ...
 *
 *  &lt;/context&gt;
 * &lt;/generatorConfiguration&gt;
 * </pre>
 * </tt> <br/> Properties:<br/> <ul> <li><strong>builderClassName</strong> (optional) : the name of the method to
 * generate. Default: <strong>Builder</strong></li> <li><strong>excludeClassNamesRegexp</strong> (optional):
 * classes to exclude from generation as regular expression. Default: none</li> </ul>
 *
 * @author Maxim Kalina
 * @version $Id$
 */
public class ModelBuilderPlugin extends PluginAdapter {

    private Config config;

    /**
     * {@inheritDoc}
     */
    public boolean validate(List<String> warnings) {
        this.initConfig();
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass,
                                                 IntrospectedTable introspectedTable) {

        if (config.shouldExclude(topLevelClass.getType()))
            return true;


        List<Field> fields = topLevelClass.getFields();

        InnerClass innerClass = new InnerClass(config.builderClassName);
        innerClass.setVisibility(JavaVisibility.PUBLIC);
        innerClass.setStatic(true);
        context.getCommentGenerator().addClassComment(innerClass, introspectedTable);

        Field f = new Field("obj", topLevelClass.getType());
        f.setVisibility(JavaVisibility.PRIVATE);
        innerClass.addField(f);

        Method constructor = new Method(config.builderClassName);
        constructor.setVisibility(JavaVisibility.PUBLIC);
        constructor.setConstructor(true);
        constructor.addBodyLine(new StringBuilder("this.obj = new ")
                .append(topLevelClass.getType().getShortName()).append("();").toString());
        innerClass.addMethod(constructor);

        for (Field field : fields) {
            if (field.isStatic())
                continue;

            Method method = new Method(field.getName());
            method.setVisibility(JavaVisibility.PUBLIC);
            method.setReturnType(innerClass.getType());
            method.addParameter(new Parameter(field.getType(), field.getName()));
            method.addBodyLine(new StringBuilder().append("obj.").append(field.getName())
                    .append(" = ").append(field.getName()).append(";").toString());
            method.addBodyLine(new StringBuilder().append("return this;").toString());
            context.getCommentGenerator().addGeneralMethodComment(method, introspectedTable);
            innerClass.addMethod(method);
        }

        Method build = new Method("build");
        build.setReturnType(topLevelClass.getType());
        build.setVisibility(JavaVisibility.PUBLIC);
        build.addBodyLine("return this.obj;");
        context.getCommentGenerator().addGeneralMethodComment(build, introspectedTable);

        innerClass.addMethod(build);

        topLevelClass.addInnerClass(innerClass);
        return true;
    }

    private void initConfig() {

        if (this.config == null)
            this.config = new Config(getProperties());
    }

    private static final class Config extends BasePluginConfig {


        private static final String builderClassNameKey = "builderClassName";

        private String builderClassName;

        private Config(Properties props) {
            super(props);
            this.builderClassName = props.getProperty(builderClassNameKey, "Builder");
        }
    }

}
