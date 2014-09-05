package bigfoot.framework.gradle.plugin.utils

import java.security.MessageDigest

/**
 * Created by hp on 2014/9/5.
 */
class StringUtils {
    static String md5(String s) {
        MessageDigest digest = MessageDigest.getInstance("MD5")
        digest.update(s.bytes);
        new BigInteger(1, digest.digest()).toString(16).padLeft(32, '0')
    }
}
