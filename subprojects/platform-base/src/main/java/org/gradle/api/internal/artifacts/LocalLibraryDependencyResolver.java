/*
 * Copyright 2015 the original author or authors.
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
package org.gradle.api.internal.artifacts;

import org.gradle.api.Project;
import org.gradle.api.artifacts.ModuleVersionIdentifier;
import org.gradle.api.artifacts.component.ComponentIdentifier;
import org.gradle.api.artifacts.component.LibraryComponentIdentifier;
import org.gradle.api.artifacts.component.LibraryComponentSelector;
import org.gradle.api.internal.artifacts.dsl.dependencies.ProjectFinder;
import org.gradle.api.internal.project.ProjectInternal;
import org.gradle.internal.component.local.model.DefaultLibraryComponentIdentifier;
import org.gradle.internal.component.local.model.DefaultLocalComponentMetaData;
import org.gradle.internal.component.model.ComponentOverrideMetadata;
import org.gradle.internal.component.model.DependencyMetaData;
import org.gradle.internal.resolve.ModuleVersionResolveException;
import org.gradle.internal.resolve.resolver.ComponentMetaDataResolver;
import org.gradle.internal.resolve.resolver.DependencyToComponentIdResolver;
import org.gradle.internal.resolve.result.BuildableComponentIdResolveResult;
import org.gradle.internal.resolve.result.BuildableComponentResolveResult;
import org.gradle.model.ModelMap;
import org.gradle.model.internal.core.ModelPath;
import org.gradle.model.internal.type.ModelType;
import org.gradle.platform.base.ComponentSpecContainer;
import org.gradle.platform.base.LibrarySpec;

import java.util.Collections;

public class LocalLibraryDependencyResolver implements DependencyToComponentIdResolver, ComponentMetaDataResolver {
    private final ProjectFinder projectFinder;

    public LocalLibraryDependencyResolver(ProjectFinder projectFinder) {
        this.projectFinder = projectFinder;
    }

    @Override
    public void resolve(DependencyMetaData dependency, BuildableComponentIdResolveResult result) {
        if (dependency.getSelector() instanceof LibraryComponentSelector) {

            DefaultLocalComponentMetaData metaData = null;
            LibraryComponentSelector selector = (LibraryComponentSelector) dependency.getSelector();
            ProjectInternal project = projectFinder.getProject(dependency.getRequested().getGroup());
            if (selector.getProjectPath() != null) {
                project = project.getRootProject().findProject(selector.getProjectPath());
            }
            if (project != null) {
                ComponentSpecContainer components = project.getModelRegistry().realize(
                    ModelPath.path("components"),
                    ModelType.of(ComponentSpecContainer.class));
                ModelMap<? extends LibrarySpec> libraries = components.withType(LibrarySpec.class);
                String libraryName = selector.getLibraryName();
                if (libraryName == null && libraries.size() == 1) {
                    libraryName = libraries.values().iterator().next().getName();
                }
                if (libraryName != null) {
                    String version = project.getVersion().toString();
                    String projectPath = project.getPath();
                    LibrarySpec library = libraries.get(libraryName);
                    if (library != null) {
                        ModuleVersionIdentifier id = new DefaultModuleVersionIdentifier(
                            projectPath, libraryName, version
                        );
                        ComponentIdentifier component = new DefaultLibraryComponentIdentifier(projectPath, library.getName());
                        metaData = new DefaultLocalComponentMetaData(id, component, Project.DEFAULT_STATUS);
                        metaData.addConfiguration(DefaultLibraryComponentIdentifier.libraryToConfigurationName(projectPath, libraryName), "Configuration for " + libraryName, Collections.<String>emptySet(), Collections.singleton(DefaultLibraryComponentIdentifier.libraryToConfigurationName(projectPath, libraryName)), true, true);
                    }
                }
            }
            if (metaData != null) {
                result.resolved(metaData.toResolveMetaData());
            } else {
                result.failed(new ModuleVersionResolveException(selector, String.format("Could not resolve dependency '%s'", selector)));
            }
        }
    }

    @Override
    public void resolve(ComponentIdentifier identifier, ComponentOverrideMetadata componentOverrideMetadata, BuildableComponentResolveResult result) {
        if (identifier instanceof LibraryComponentIdentifier) {
            throw new RuntimeException("Not yet implemented");
        }
    }
}