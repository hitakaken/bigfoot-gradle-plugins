package bigfoot.framework.gradle.plugin

import org.apache.maven.artifact.repository.ArtifactRepository
import org.gradle.api.DefaultTask
import org.gradle.api.specs.Specs
import org.gradle.api.tasks.TaskAction

/**
 * Created by Owner on 2014/8/14.
 */
class KarafFeatureRepositoryTasks {
    class ListFeatureTask extends DefaultTask{
        public ListFeatureTask() {
            getOutputs().upToDateWhen(Specs.satisfyNone());
        }

        /**
         *
         */
        @TaskAction
        def doExecuteTask() {
            project.KarafFeatureRepository.repos.each{ repo ->

                for(ArtifactRepository artifactRepository:project.repositories.listIterator()){

                }
            }
        }
    }
}



