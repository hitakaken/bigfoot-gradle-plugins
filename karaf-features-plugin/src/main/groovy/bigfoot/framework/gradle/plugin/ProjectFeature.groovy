package bigfoot.framework.gradle.plugin

import org.apache.karaf.features.internal.model.Bundle
import org.gradle.api.internal.project.ProjectInternal

/**
 * Created by hp on 2014/9/4.
 */
class ProjectFeature extends FeatureWrapper{

    ProjectFeature(String resolver,ProjectInternal project) {
        super(resolver,project)
        name project.name
        version project.version
        description project.description
        if(project.extensions.findByName('details')!=null) details(project.extensions.findByName('details'));
    }

    void self(){
        self(null);
    }

    void self(Map extInfo){
        def map = project(null);
        if(extInfo!=null) extInfo.each {k,v->
            map.put(k,v);
        }
        super.bundle(map);
    }

    Map project(String path){
        return getProjectInfo(path!=null&&!path.isEmpty()?project.findProject(path):null);

    }

    Map getProjectInfo(ProjectInternal project){
        if(project==null) project = this.project;
        def map = [
                group:project.group,name:project.name,version:project.version,
                wrap:project.extensions.findByName('bundle_wrap')!=null?project.extensions.findByName('bundle_wrap'):false,
                startLevel:project.extensions.findByName('bundle_start_level')!=null?project.extensions.findByName('bundle_start_level'):null,
                start:project.project.extensions.findByName('bundle_start')!=null?project.extensions.findByName('bundle_start'):null ,
                dependency:project.extensions.findByName('bundle_dependency')!=null?project.extensions.findByName('bundle_dependency'):null
        ];
        return map;
    }
}
