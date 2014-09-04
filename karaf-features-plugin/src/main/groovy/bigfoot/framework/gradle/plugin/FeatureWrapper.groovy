package bigfoot.framework.gradle.plugin

import org.apache.karaf.features.internal.model.*
import org.gradle.api.internal.project.ProjectInternal;

/**
 * Created by hp on 2014/9/4.
 */
class FeatureWrapper {
    ProjectInternal project;
    Feature feature;

    Feature getOrigin(){
        return feature;
    }

    FeatureWrapper(ProjectInternal project) {
        this(null,project);
    }

    FeatureWrapper(String resolver,ProjectInternal project) {
        this.project = project
        feature = new Feature();
        if(resolver!=null){
            feature.setResolver(resolver);
        }
    }

    void name(String name) {
        feature.setName(name);
    }

    void version(String version) {
        feature.setVersion(version);
    }

    void description(String description) {
        feature.setDescription(description);
    }

    void resolver(String resolver) {
        feature.setResolver(resolver);
    }

    void details(String detail) {
        feature.setDetails(detail);
    }

    void config(Map config){
        def name = config.name;
        config.remove('name')
        config(name,config);
    }

    void config(String name,Map values){
        def value = '';
        if(values!=null&&!values.isEmpty()){
            values.each{k,v->
                value += "$k=$v\r\n";
            }
        }
        config(name,value);
    }

    void config(String name,String value){
        if(name == null || name.isEmpty()) return;
        def config = new Config();
        config.setName(name);
        config.setValue(value);
        feature.getConfig().add(config);
    }

    void configFile(Map configFile){
        configFile(configFile.name,configFile.value,configFile.override);
    }

    void configFile(String name,String value,Boolean override){
        if(name == null || name.isEmpty()) return;
        def configFile = new ConfigFile();
        configFile.setFinalname(name);
        configFile.setLocation(value);
        configFile.setOverride(override)
        feature.getConfigfile().add(configFile);
    }



    void feature(String name){
        feature(name,null)
    }

    void feature(Map dependencyInfo){
        if(dependencyInfo!=null && dependencyInfo.containsKey("name")){
            feature(dependencyInfo.name,dependencyInfo.version);
        }
    }

    void feature(String name,String version){
        Dependency dependency = new Dependency()
        dependency.setName(name);
        dependency.setVersion(version);
        feature.getFeature().add(dependency);
    }

    void bundle(Map bundleInfo){
        bundle(bundleInfo.group,bundleInfo.name,bundleInfo.version,bundleInfo.wrap,
                bundleInfo.startLevel,bundleInfo.start,bundleInfo.dependency);
    }

    void bundle(String bundleInfo){
        def wrap=false;
        if(bundleInfo.startsWith("wrap:")){
            wrap = true;
            bundleInfo = bundleInfo.substring(5);
        }
        bundleInfo = bundleInfo.startsWith("mvn:")?bundleInfo.substring(4):bundleInfo;
        def info = bundleInfo.split(":");
        if(info.size() == 3) {
            bundle(info[0],info[1],info[2],wrap,null,null,null);
            return;
        }
        info = bundleInfo.split("/");
        if(info.size() == 3) bundle(info[0],info[1],info[2],wrap,null,null,null);
    }

    void bundle(String group, String name, String version,Boolean wrap, Integer startLevel,Boolean start,Boolean dependency){
        if(group == null || name ==null || version ==null) return;
        Bundle bundle = new Bundle("${wrap!=null && wrap?'wrap:':''}mvn:$group/$name/$version");
        bundle.setStartLevel(startLevel);
        bundle.setStart(start);
        bundle.setDependency(dependency);
        feature.getBundle().add(bundle);
    }

    boolean isValid(){
        return feature.name!=null;
    }
}
