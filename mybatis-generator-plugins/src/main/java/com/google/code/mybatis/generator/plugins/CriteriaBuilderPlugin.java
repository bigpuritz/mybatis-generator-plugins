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
import org.mybatis.generator.api.dom.java.*;

import java.util.List;

/**
 * Adds "builder-style" to the generated XXXExample classes. Thus inner XXXExample.Criteria class will become an
 * additional "example()" method the returns the example instance itself. <br/> Example configuration:<br/>
 * <pre>
 * &lt;generatorConfiguration&gt;
 *  &lt;context ...&gt;
 *
 *      &lt;plugin type="com.google.code.mybatis.generator.plugins.CriteriaBuilderPlugin"/&gt;
 *      ...
 *
 *  &lt;/context&gt;
 * &lt;/generatorConfiguration&gt;
 * </pre>
 *
 * @author Maxim Kalina
 * @version $Id$
 */
public class CriteriaBuilderPlugin extends PluginAdapter {

    /**
     * {@inheritDoc}
     */
    public boolean validate(List<String> warnings) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass,
                                              IntrospectedTable introspectedTable) {

        List<InnerClass> innerClasses = topLevelClass.getInnerClasses();
        for (InnerClass innerClass : innerClasses) {
            if ("Criteria".equals(innerClass.getType().getShortName())) {
                addFactoryMethodToCriteria(topLevelClass, innerClass, introspectedTable);
            }
        }

        List<Method> methods = topLevelClass.getMethods();
        for (Method method : methods) {
            if (!"createCriteriaInternal".equals(method.getName()))
                continue;

            method.getBodyLines().set(0, "Criteria criteria = new Criteria(this);");
        }

        return true;
    }

    private void addFactoryMethodToCriteria(TopLevelClass topLevelClass, InnerClass innerClass,
                                            IntrospectedTable introspectedTable) {

        Field f = new Field("example", topLevelClass.getType());
        f.setVisibility(JavaVisibility.PRIVATE);
        innerClass.addField(f);

        // overwrite constructor
        List<Method> methods = innerClass.getMethods();
        for (Method method : methods) {
            if (method.isConstructor()) {
                method.addParameter(new Parameter(topLevelClass.getType(), "example"));
                method.addBodyLine("this.example = example;");
            }
        }

        // add factory method "example"
        Method method = new Method("example");
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(topLevelClass.getType());
        method.addBodyLine("return this.example;");
        context.getCommentGenerator().addGeneralMethodComment(method, introspectedTable);
        innerClass.addMethod(method);
    }
}
