package bigfoot.framework.gradle.plugin

import bigfoot.framework.gradle.plugin.utils.NetUtils
import bigfoot.framework.gradle.plugin.utils.StringUtils
import org.apache.karaf.features.internal.model.Bundle
import org.apache.karaf.features.internal.model.Feature
import org.apache.karaf.features.internal.model.Features
import org.apache.karaf.features.internal.model.JaxbUtil
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.internal.project.ProjectInternal

/**
 * Created by hp on 2014/9/5.
 */
class FeaturesHandler {
    ProjectInternal project;
    Map<String,Features> resolvedRepositories;
    List<Feature> resolvedFeatures;
    List<Bundle> resolvedBundles;

    FeaturesHandler(ProjectInternal project) {
        this.project = project
    }

    Features getInstalledFeatures(){
        return project.getFeatures().getOrigin();
    }

    Map<String,Features> getRepositories(){
        if(resolvedRepositories == null){
            createRepositories();
        }
        return resolvedRepositories;
    }

    void createRepositories(){
        resolvedRepositories = [:];
        traversal(getInstalledFeatures());
    }

    void traversal(Features features){
        features.getRepository().each{ repository->
            if(repositories.containsKey(repository)) return ;
            URL url;
            File file;
            if(project.getFeatures().store!=null) file = new File(project.getFeatures().store,"features-${StringUtils.md5(repository)}.xml");
            if(file!=null && file.exists()){
                def fis = new FileInputStream(file);
                Features temp = JaxbUtil.unmarshal(fis,true);
                fis.close();
                traversal(temp);
                return ;
            }
            if(repository.startsWith("mvn:")){
                def path = repository.substring(4).split("/");
                def group = path[0];
                def name = path[1];
                def version = path[2];
                def type = path[3];
                def classifier = path[4];
                project.getRepositories().asList().each{artifactRepository ->
                    if(artifactRepository instanceof  MavenArtifactRepository) {
                        URI uri = artifactRepository.getUrl().resolve("${group.replaceAll('\\.','/')}/$name/$version/$name-$version-$classifier.$type");
                        def tempUrl = uri.toURL();
                        if(NetUtils.exists(tempUrl)){
                            url = tempUrl;
                            return true;
                        }
                    }
                }
            }else{
                url = repository;
            }
            if(url!=null){
                def text = url.text;
                def bis = new ByteArrayInputStream(text.getBytes());
                Features temp = JaxbUtil.unmarshal(bis,true);
                bis.close();
                getRepositories().put(repository,temp);
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
            createFeatures();
        }
        return resolvedFeatures;
    }

    void createFeatures(){
        resolvedFeatures = [];
        getInstalledFeatures().getFeature().each {feature->
            addFeature(feature);
        }
        getRepositories().each {name,features->
            features.each{feature->
                addFeature(feature);
            }
        }
        resolvedFeatures.sort{f1,f2->
            f1.name.compareTo(f2.name)!=0?f1.name.compareTo(f2.name):0;
        };
    }

    void addFeature(Feature feature){
        if(findFeature(feature.getName(),feature.getVersion())==null){
            resolvedFeatures.add(feature);
        }
    }

    Feature findFeature(String name, String version){
        def result = resolvedFeatures.find{elem ->
            elem.name == name && elem.version == version;
        }
        return result;
    }

    List<Feature> findFeatures(String name){
        def result = resolvedFeatures.findAll{elem ->
            elem.name == name
        }
        return result;
    }



    List<Feature> getInstalledFeatureList(){
        getFeatureList()
    }

    List<Bundle> getBundleList(){

    }

    List<Bundle> getInstalledBundleList(){

    }

}
