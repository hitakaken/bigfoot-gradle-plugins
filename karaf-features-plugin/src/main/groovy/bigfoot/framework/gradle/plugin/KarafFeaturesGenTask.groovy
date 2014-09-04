package bigfoot.framework.gradle.plugin

import org.apache.karaf.features.internal.model.JaxbUtil
import org.gradle.api.DefaultTask
import org.gradle.api.specs.Specs
import org.gradle.api.tasks.TaskAction

/**
 * Created by hp on 2014/9/4.
 */
class KarafFeaturesGenTask extends DefaultTask {
    public KarafFeaturesGenTask() {
        getOutputs().upToDateWhen(Specs.satisfyNone());
    }

    @TaskAction
    def doExecuteTask() {
        def features = project.extensions.findByName("features");
        if(features!=null && features instanceof FeaturesWrapper){
            println features.name;
            println features.version;
            println features.output;
            if(features.output!=null){
                def out = new BufferedWriter(new FileWriter(features.output));
                JaxbUtil.marshal(features.getOrigin(),out);
                out.close();
            }else{
                JaxbUtil.marshal(features.getOrigin(),System.out);
            }

        }
    }
}
