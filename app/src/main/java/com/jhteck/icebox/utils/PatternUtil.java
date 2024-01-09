package com.jhteck.icebox.utils;

import static com.jhteck.icebox.api.AppConstantsKt.SHOW_ALL_NAME;

import com.hele.mrd.app.lib.base.BaseApp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wade
 * @Description:(用一句话描述)
 * @date 2023/7/13 16:14
 */
public class PatternUtil {

    /**
     * 剔除数字
     *
     * @param value
     */
    public static String removeDigital(String value) {
        Pattern p = Pattern.compile("[\\d]");
        Matcher matcher = p.matcher(value);
        String result = matcher.replaceAll("");
        return result;
    }

    /**
     * 剔除字母
     *
     * @param value
     */
    public static String removeLetter(String value) {
        Pattern p = Pattern.compile("[a-zA-z]");
        Matcher matcher = p.matcher(value);
        String result = matcher.replaceAll("");
        return result;
    }

    /**
     * 字符串只保留数字、字母、中文
     *
     * @param str
     * @return
     */
    public static String removeMatch(String str) {
        str = str.replaceAll("-", "");
        str = str.replaceAll("/", "");
        str = str.replaceAll("，", "");
        str = str.replaceAll("_", "");
        return str.replaceAll("[^0-9a-zA-Z\u4e00-\u9fa5]", "");
    }

    public static String removeDigitalAndLetter(String value) {
        if (!SharedPreferencesUtils.getPrefBoolean(BaseApp.app, SHOW_ALL_NAME, true)) {
            String result = removeMatch(value);
            result = removeDigital(result);
            result = removeLetter(result);
//        result = removeAll(result);
            return result;
        } else {
            return value;
        }
    }

}
