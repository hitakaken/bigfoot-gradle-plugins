/**
 * Copyright 2013 Cao Ke
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
package bigfoot.framework.gradle.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.internal.artifacts.dsl.dependencies.DefaultDependencyHandler
import org.gradle.api.specs.Specs
import org.gradle.api.tasks.TaskAction

/**
 * Created by Owner on 2014/8/14.
 */
class KarafFeaturesPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.extensions.create("karaf_feature_repos",DefaultDependencyHandler)
        //project.extensions.create("features")
        project.task("list_features",type:ListFeatureTask)
    }

    class ListFeatureTask extends DefaultTask{
        public ListFeatureTask() {
            getOutputs().upToDateWhen(Specs.satisfyNone());
        }
        /**
         *
         */
        @TaskAction
        def doExecuteTask() {
            DependencyHandler dependencyHandler = project.extensions.findByName("karaf_feature_repos");
            def result = dependencyHandler.createArtifactResolutionQuery().execute();
            for(component in result.resolvedComponents){
                println component
            }
        }
    }
}


