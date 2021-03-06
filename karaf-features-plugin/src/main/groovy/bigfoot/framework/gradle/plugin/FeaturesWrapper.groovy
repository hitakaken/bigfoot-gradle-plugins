package bigfoot.framework.gradle.plugin

import org.apache.karaf.features.internal.model.Features
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.util.ConfigureUtil


/**
 * 封装 Karaf Features 对象
 * Created by hp on 2014/9/3.
 */
class FeaturesWrapper {
    private ProjectInternal project;
    File output;
    File store;
    String name;
    String version;
    String defaultResolver = null
    Features features;

    FeaturesWrapper(ProjectInternal project) {
        this.project = project
        features = new Features();
    }

    Features getOrigin(){
        return features;
    }

    public void output(Object path){
        output = project.file(path)
    }

    public void store(Object path){
        def file = project.file(path)
        store = !file.exists() || file.isDirectory()?file:null;
        if(store!=null && !store.exists()) store.mkdirs();
    }

    public void name(String name){
        this.name = name;
        features.setName(name+(version!=null?"-$version":''));
    }

    public void version(String verison){
        this.version = verison
        features.setName(name+(version!=null?"-$version":''));
    }

    public void resolver(String defaultResolver){
        this.defaultResolver=defaultResolver;
    }

    public void repositories(List<String> repositories){
        repositories.each{ repository->
            repository(repository);
        }
    }

    public void repository(String repository){
        features.getRepository().add(repository);
    }

    public void feature(Closure configureClosure){
        def featureWrapper = new FeatureWrapper(defaultResolver,project)
        ConfigureUtil.configure(configureClosure, featureWrapper);
        if(featureWrapper.isValid()) features.getFeature().add(featureWrapper.getOrigin());
    }
}
