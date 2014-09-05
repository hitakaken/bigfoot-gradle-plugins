package bigfoot.framework.gradle.plugin.utils

import sun.net.www.protocol.file.FileURLConnection

/**
 * Created by hp on 2014/9/5.
 */
class NetUtils {
    static exists(URL url){
        URLConnection connection =  url.openConnection();
        if (connection instanceof HttpURLConnection){
            HttpURLConnection.setFollowRedirects(false);
            connection.setRequestMethod("GET");//connection.setRequestMethod("HEAD");
            return connection.getResponseCode() == HttpURLConnection.HTTP_OK
        }else if (connection instanceof  FileURLConnection){
            return new File(url.toURI()).exists();
        }else{
            return false;
        }

    }
}
