package bigfoot.framework.gradle.plugin.model

import org.gradle.util.ConfigureUtil


/**
 * Created by hp on 2014/9/3.
 */
class Features {
    File output = null;
    String name;
    String version;
    List<String> repositories = [];
    List<Feature> features = [];

    void feature(Closure configureClosure){
        def feature = new Feature()
        ConfigureUtil.configure(configureClosure, feature);
        features.add(feature);
    }
}
