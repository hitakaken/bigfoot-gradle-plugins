package bigfoot.framework.gradle.plugin

import org.apache.karaf.features.internal.model.Features
import org.gradle.api.DefaultTask
import org.gradle.api.specs.Specs
import org.gradle.api.tasks.TaskAction

/**
 * Created by hp on 2014/9/5.
 */
class KarafFeaturesListTask extends DefaultTask{
    public KarafFeaturesListTask() {
        getOutputs().upToDateWhen(Specs.satisfyNone());
    }

    @TaskAction
    def doExecuteTask() {
        FeaturesHandler handler = project.getFeaturesHandler();
        Features features = handler.getInstalledFeatures();
        println features.name;
        println "[List Feature]"
        handler.getFeatureList().each{feature->
            println "${feature.name}";
        }
        println "[List Installed Feature]"
        handler.getInstalledFeatureList().each{feature->
            println "${feature.name}";
        }
        println"[List Bundle]"
        handler.getBundleList().each{bundle->
            println bundle.getLocation();
        }
    }
}
