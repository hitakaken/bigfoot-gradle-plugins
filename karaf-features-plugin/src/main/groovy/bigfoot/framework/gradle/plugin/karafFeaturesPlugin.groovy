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

import bigfoot.framework.gradle.plugin.model.Features
import groovy.xml.MarkupBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.specs.Specs
import org.gradle.api.tasks.TaskAction

/**
 * Created by Owner on 2014/8/14.
 */
class KarafFeaturesPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.extensions.create("features",Features)
        project.features.name = project.name
        project.features.version = project.version
        project.task("genFeatures",type:GenFeaturesTask)
    }


    class GenFeaturesTask extends DefaultTask{
        public GenFeatureTask() {
            getOutputs().upToDateWhen(Specs.satisfyNone());
        }
        /**
         *
         */
        @TaskAction
        def doExecuteTask() {
            def writer = new StringWriter()
            def builder = new MarkupBuilder(writer)
            builder.features(
                    name:"${project.features.name}-${project.features.version}",
                    xmlns:"http://karaf.apache.org/xmlns/features/v1.2.0",
                    "xmlns:xsi":"http://www.w3.org/2001/XMLSchema-instance",
                    "xsi:schemaLocation":"http://karaf.apache.org/xmlns/features/v1.2.0 http://karaf.apache.org/xmlns/features/v1.2.0"){
                if(project.features.repositories.size > 0){
                    project.features.repositories.each{ repository ->
                        repository(repository);
                    }
                }
                if(project.features.features.size> 0){
                    project.features.features.each{ feature ->
                        def featureAttributes = [name:feature.name,version:feature.name,resolver:feature.resolver]
                        if(feature.description != null) featureAttributes.put("description",feature.description);
                        feature(featureAttributes){
                            if(feature.detail !=null){
                                detail(feature.detail);
                            }
                            if(feature.features.size>0){
                                feature.features.each{dependency ->
                                    if(dependency.version!=null){
                                        feature(name:dependency.name,version:dependency.version);
                                    }else{
                                        feature(name:dependency.name);
                                    }
                                }
                            }
                            if(feature.bundles.size>0){
                                feature.bundles.each{ bundle->
                                    def bundleAttributes = [:];
                                    if(bundle.isStart) bundleAttributes.put("start",bundle.isStart);
                                    if(bundle.startLevel != 100) bundleAttributes.put("startLevel",bundle.startLevel);
                                    def start = bundle.isWrapped?"wrap:mvn":"mvn";
                                    bundle(bundleAttributes,"$start:${bundle.group}/${bundle.name}/${bundle.version}");
                                }
                            }
                        }
                    }
                }
            }
            if(project.features.output !=null){
                def out = new BufferedWriter(new FileWriter(project.features.output));
                out.write(writer.toString())
                out.close()
            } else {
                println writer.toString()
            }
        }
    }
}


