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

package com.google.code.mybatis.generator.plugins.test;

import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.visitor.VoidVisitorAdapter;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;

/**
 * @author Maxim Kalina (Maxim.Kalina@extern.isban.de)
 * @version $Id$
 */
public class GenerationTest {


    @Test
    public void testBuilderPlugin() {
        try {

            InnerClassVisitor visitor = new InnerClassVisitor("Builder");
            CompilationUnit cu = loadCompilationUnit("User.java");
            visitor.visitAndAssert(cu, null);

//            cu = loadCompilationUnit("Blog.java");
//            visitor.visitAndAssert(cu, null);

        } catch (Throwable t) {

            Assert.fail(t.getMessage());
        }


    }


    private static class InnerClassVisitor extends VoidVisitorAdapter {
        private String innerClassName;

        private boolean found = false;

        private InnerClassVisitor(String innerClassName) {
            this.innerClassName = innerClassName;
        }


        public void visitAndAssert(CompilationUnit n, Object arg) {
            found = false;
            visit(n, arg);
            Assert.assertTrue("No inner builder class found in compilation unit: " +
                    n.getTypes().get(0).getName(), found);
        }

        @Override
        public void visit(ClassOrInterfaceDeclaration n, Object arg) {

            if (this.innerClassName.equals(n.getName())) {
                found = true;
            } else {
                super.visit(n, arg);
            }

        }

    }

    private static class MethodVisitor extends VoidVisitorAdapter {


//        @Override
//        public void visit(ClassOrInterfaceType n, Object arg) {
//           System.out.println("2"+  n.getName());
//        }


        @Override
        public void visit(ClassOrInterfaceDeclaration n, Object arg) {
            System.out.println("3" + n.getName());
            super.visit(n, arg);
        }


        //        @Override
//        public void visit(MethodDeclaration n, Object arg) {
//            // here you can access the attributes of the method.
//            // this method will be called for all methods in this
//            // CompilationUnit, including inner class methods
//            System.out.println(n.getName());
//        }
    }

    protected CompilationUnit loadCompilationUnit(String javaFile) throws Exception {
        File f = new File(getBaseDir() + javaFile);

        // creates an input stream for the file to be parsed
        FileInputStream in = new FileInputStream(f);

        CompilationUnit cu;
        try {
            // parse the file
            cu = JavaParser.parse(in);
        } finally {
            in.close();
        }
        return cu;
    }

    private String getBaseDir() {
        String userDir = System.getProperty("user.dir");
        if (!userDir.endsWith("mybatis-generator-plugins-tests")) userDir += "/mybatis-generator-plugins-tests";
        return userDir + "/src/main/java/com/google/code/mybatis/generator/plugins/gen/";

    }
}
