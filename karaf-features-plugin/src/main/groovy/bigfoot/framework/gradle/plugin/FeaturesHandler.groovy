package bigfoot.framework.gradle.plugin

import bigfoot.framework.gradle.plugin.utils.NetUtils
import bigfoot.framework.gradle.plugin.utils.StringUtils
import org.apache.ivy.plugins.version.VersionMatcher
import org.apache.karaf.features.internal.model.Bundle
import org.apache.karaf.features.internal.model.Feature
import org.apache.karaf.features.internal.model.Features
import org.apache.karaf.features.internal.model.JaxbUtil
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.mvn3.org.sonatype.aether.util.version.GenericVersion
import org.gradle.mvn3.org.sonatype.aether.util.version.GenericVersionRange

/**
 * Created by hp on 2014/9/5.
 */
class FeaturesHandler {
    ProjectInternal project;
    Map<String,Features> resolvedRepositories;
    List<Feature> resolvedFeatures;
    List<Feature> resolvedInstalledFeatures;
    List<Bundle> resolvedBundles;

    FeaturesHandler(ProjectInternal project) {
        this.project = project
    }

    Features getInstalledFeatures(){
        return project.getFeatures().getOrigin();
    }

    Map<String,Features> getRepositories(){
        if(resolvedRepositories == null){
            resolvedRepositories();
        }
        return resolvedRepositories;
    }

    void resolvedRepositories(){
        resolvedRepositories = [:];
        traversal(getInstalledFeatures());
    }

    void traversal(Features features){
        features.getRepository().each{ repository->
            if(resolvedRepositories.containsKey(repository)) return ;
            URL url;
            File file;
            if(project.getFeatures().store!=null) file = new File(project.getFeatures().store,"features-${StringUtils.md5(repository)}.xml");
            if(file!=null && file.exists()){
                def fis = new FileInputStream(file);
                Features temp = JaxbUtil.unmarshal(fis,false);
                fis.close();
                resolvedRepositories.put(repository,temp);
                traversal(temp);
                return
            }
            if(repository.startsWith("mvn:")){
                def path = repository.substring(4).split("/");
                def group = path[0];
                def name = path[1];
                def version = path[2];
                def type = path[3];
                def classifier = path[4];
                def additionUrl = "${group.replaceAll('\\.','/')}/$name/$version/$name-$version-$classifier.$type";
                project.getRepositories().asList().find{artifactRepository ->
                    if(artifactRepository instanceof  MavenArtifactRepository) {
                        URI uri = artifactRepository.getUrl().resolve(additionUrl);
                        def tempUrl = uri.toURL();
                        if(NetUtils.exists(tempUrl)){
                            url = tempUrl;
                            return true;
                        }
                    }
                    return false;
                }
            }else{
                url = new URL(repository);
            }
            if(url!=null){
                def text = url.getText();
                def bis = new ByteArrayInputStream(text.getBytes());
                Features temp = JaxbUtil.unmarshal(bis,false);
                bis.close();
                resolvedRepositories.put(repository,temp);
                if(file!=null) {
                    if(!file.getParentFile().exists()) file.getParentFile().mkdirs();
                    file << text;
                }

                traversal(temp);
            }
        }
    }

    List<Feature> getFeatureList(){
        if(resolvedFeatures ==null){
            resolvedFeatures();
        }
        return resolvedFeatures;
    }

    void resolvedFeatures(){
        resolvedFeatures = [];
        getInstalledFeatures().getFeature().each {feature->
            addFeature(feature);
        }
        getRepositories().each {name,features->
            features.getFeature().each{feature->
                addFeature(feature);
            }
        }
        resolvedFeatures.sort{f1,f2->
            if(f1.name.compareTo(f2.name) != 0) return f1.name.compareTo(f2.name);
            GenericVersion v1 = new GenericVersion(f1.version);
            GenericVersion v2 = new GenericVersion(f2.version);
            return v2.compareTo(v1);
        };
    }

    void addFeature(Feature feature){
        if(!featureResolved(feature.getName(),feature.getVersion())){
            resolvedFeatures.add(feature);
        }
    }

    boolean featureResolved(String name, String version){
        def result = resolvedFeatures.find{elem ->
            elem.name == name && elem.version == version;
        }
        return result!=null;
    }

    List<Feature> getInstalledFeatureList(){
        if(resolvedInstalledFeatures ==null){
            resolvedInstalledFeatures();
        }
        return resolvedInstalledFeatures;
    }

    void resolvedInstalledFeatures(){
        resolvedInstalledFeatures = [];
        getInstalledFeatures().getFeature().each{feature->
            traversal(feature)
        }
    }

    void traversal(Feature feature){
        if(featureInstalled(feature.getName(),feature.getVersion())) return;
        resolvedInstalledFeatures.add(feature);
        feature.getDependencies().each{dependency ->
            def dependencyFeature = matchFeature(dependency.getName(),dependency.getVersion());
            if(dependencyFeature!=null) traversal(dependencyFeature);
        }
    }

    boolean featureInstalled(String name,String version){
        def result = resolvedInstalledFeatures.find{elem ->
            elem.name == name && elem.version == version;
        }
        return result!=null;
    }

    Feature matchFeature(String name, String versionRange){
        def vr =(versionRange!=null && versionRange!='0.0.0')?
            (versionRange.startsWith("[")||versionRange.startsWith("(")?
                    new GenericVersionRange(versionRange):new GenericVersionRange("[$versionRange,$versionRange]")):null;
        def result = getFeatureList().find{elem ->
            elem.name == name && (vr==null || vr.containsVersion(new GenericVersion(elem.version)))
        }
        return result;
    }

    List<Bundle> getBundleList(){
        if(resolvedBundles ==null){
            resolvedBundles();
        }
        return resolvedBundles;
    }

    void resolvedBundles(){
        resolvedBundles = [];
        getInstalledFeatureList().each {feature ->
            feature.getBundle().each {bundle->
                if(!bundleResolved(bundle.getLocation())){
                    resolvedBundles.add(bundle);
                }
            }
            feature.getConditional().each{conditional->
                conditional.getBundle().each{bundle->
                    if(!bundleResolved(bundle.getLocation())){
                        resolvedBundles.add(bundle);
                    }
                }
            }
        }
    }

    boolean bundleResolved(String location){
        def result = resolvedBundles.find{elem ->
            elem.getLocation() == location ;
        }
        return result!=null;
    }

}
