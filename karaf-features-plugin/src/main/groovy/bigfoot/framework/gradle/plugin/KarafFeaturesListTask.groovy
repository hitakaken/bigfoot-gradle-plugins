package bigfoot.framework.gradle.plugin

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
        def handler = project.getFeaturesHandler();

    }
}
