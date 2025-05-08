/*
 * Copyright 2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.alexfalappa.nbspringboot.codegen;

import java.util.List;

import javax.swing.text.JTextComponent;

import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.modules.maven.api.Constants;
import org.netbeans.modules.maven.model.pom.Dependency;
import org.netbeans.modules.maven.model.pom.POMModel;
import org.netbeans.spi.editor.codegen.CodeGenerator;
import org.openide.util.Lookup;

/**
 * Factory of Spring Boot related POM code generators.
 *
 * @author Alessandro Falappa
 */
@MimeRegistration(mimeType = Constants.POM_MIME_TYPE, service = CodeGenerator.Factory.class, position = 1000)
public class SpringBootCodeGeneratorFactory implements CodeGenerator.Factory {

    @Override
    public List<? extends CodeGenerator> create(Lookup context) {
        final POMModel model = context.lookup(POMModel.class);
        if (model != null) {
            final JTextComponent component = context.lookup(JTextComponent.class);
            return hasBootStarter(model)?
                List.of(new SpringDependenciesGenerator(model, component)):
                List.of(new InjectSpringBootGenerator(model, component));
        }
        return List.of();
    }

    // check if there is at least a dependency whose artifactId contains 'spring-boot-starter'
    private static boolean hasBootStarter(POMModel model) {
        final List<Dependency> dependencies = model.getProject().getDependencies();
        if (dependencies != null) {
            return dependencies.stream()
                .map(Dependency::getArtifactId)
                .filter(SpringBootCodeGeneratorFactory::hasBootStarter)
                .findFirst()
                .isPresent();
        }
        return false;
    }

    private static boolean hasBootStarter(String id) {
        return id.contains("spring-boot-starter");
    }

}
