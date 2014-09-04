package bigfoot.framework.gradle.plugin

import org.gradle.api.internal.project.ProjectInternal
import org.gradle.util.ConfigureUtil

/**
 * Is mixed in into the project when applying the  {@link bigfoot.framework.gradle.plugin.KarafFeaturesPlugin} .
 * Created by hp on 2014/9/4.
 */
class KarafFeaturesPluginConvention {
    private ProjectInternal project;
    public KarafFeaturesPluginConvention(ProjectInternal project){
        this.project = project;
    }

    public void features(){
        features(null);
    }

    public void features(Closure closure){
        ConfigureUtil.configure(closure, getFeatures(project));
    }

    private FeaturesWrapper getFeatures(ProjectInternal project){
        def features = project.extensions.findByName("features");
        if(features == null){
            features = new FeaturesWrapper(project);
            project.extensions.add("features",features);
        }
        return features;
    }
}
