package bigfoot.framework.gradle.plugin.model

/**
 * Created by hp on 2014/9/3.
 */
class Bundle {
    String group;
    String name;
    String version;
    boolean isStart = false;
    boolean isWrapped = false;
    int startLevel = 100;
}
