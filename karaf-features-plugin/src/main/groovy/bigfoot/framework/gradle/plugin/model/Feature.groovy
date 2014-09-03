package bigfoot.framework.gradle.plugin.model

/**
 * Created by hp on 2014/9/3.
 */
class Feature {
    String name;
    String version;
    String description = null;
    String resolver ="(obr)";
    String detail = null;

    List<Dependency> features = [];
    List<Bundle> bundles = [];

    void feature(String name){

    }

    void feature(String name,String version){

    }

    void feature(Map dependency){

    }

    void bundle(Map bundleInfo){

    }

    void bundle(String bundleInfo){

    }
}
