package bigfoot.framework.gradle.plugin.utils

/**
 * Created by hp on 2014/9/5.
 */
class NetUtils {
    static exists(URL url){
        HttpURLConnection huc =  (HttpURLConnection)  url.openConnection();
        HttpURLConnection.setFollowRedirects(false);
        huc.setRequestMethod("HEAD");
        return huc.getResponseCode() == HttpURLConnection.HTTP_OK
    }
}
