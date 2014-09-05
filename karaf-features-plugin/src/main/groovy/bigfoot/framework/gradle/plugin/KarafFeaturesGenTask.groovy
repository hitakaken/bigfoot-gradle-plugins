package bigfoot.framework.gradle.plugin

import org.apache.karaf.features.internal.model.JaxbUtil
import org.gradle.api.DefaultTask
import org.gradle.api.specs.Specs
import org.gradle.api.tasks.TaskAction

/**
 * 生成 feature.xml
 * Created by hp on 2014/9/4.
 */
class KarafFeaturesGenTask extends DefaultTask {
    public KarafFeaturesGenTask() {
        getOutputs().upToDateWhen(Specs.satisfyNone());
    }

    @TaskAction
    def doExecuteTask() {
        def features = project.extensions.findByName("features");
        if(features!=null && features instanceof FeaturesWrapper) {
            def out = features.output!=null?new BufferedWriter(new FileWriter(features.output)):
                    (features.store!=null?new BufferedWriter(new FileWriter(new File(features.store,"features.xml"))):System.out);
            JaxbUtil.marshal(features.getOrigin(), out);
            if(out!=System.out) out.close();
        }
    }
}
