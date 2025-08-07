package wxdgaming.spring.boot.net;

import com.alibaba.fastjson.JSONObject;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * http 协议数据处理
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2022-01-28 09:56
 **/
public class HttpDataAction {

    /** 使用 gzip 的最小值 */
    public static final int USE_GZIP_MIN_LENGTH = 1024 * 8;

    public static String urlEncoder(Object text) {
        final String valueOf = String.valueOf(text);
        return URLEncoder.encode(valueOf, StandardCharsets.UTF_8);
    }


    public static String urlDecoder(Object text) {
        final String valueOf = String.valueOf(text);
        return URLDecoder.decode(valueOf, StandardCharsets.UTF_8);
    }

    /** 实现php */
    public static String rawUrlEncode(Object object) {
        final String valueOf = String.valueOf(object);
        return URLEncoder.encode(valueOf, StandardCharsets.UTF_8)
                .replace("*", "%2A")
                .replace("+", "%20")
                .replace("%7E", "~");
    }

    /** 实现php */
    public static String rawUrlDecode(Object object) {
        String valueOf = String.valueOf(object);
        valueOf = valueOf
                .replace("%2A", "*")
                .replace("%20", "+")
                .replace("~", "%7E");
        return URLDecoder.decode(valueOf, StandardCharsets.UTF_8);
    }

    public static String httpData(Map<String, ?> paramsMap) {
        if (paramsMap == null) return "";
        return paramsMap.entrySet()
                .stream()
                .map(v -> v.getKey() + "=" + v.getValue())
                .collect(Collectors.joining("&"));
    }

    public static String httpDataEncoder(Map<String, ?> paramsMap) {
        if (paramsMap == null) return "";
        return paramsMap.entrySet()
                .stream()
                .map(v -> v.getKey() + "=" + urlEncoder(v.getValue()))
                .collect(Collectors.joining("&"));
    }

    /** php一样的算法 */
    public static String httpDataRawEncoder(Map paramsMap) {
        if (paramsMap == null) return "";
        Map<Object, Object> paramsMaps = paramsMap;
        return paramsMaps.entrySet()
                .stream()
                .map(v -> v.getKey() + "=" + rawUrlEncode(v.getValue()))
                .collect(Collectors.joining("&"));
    }

    public static JSONObject httpDataDecoder(String data) {
        JSONObject jsonObject = new JSONObject(true);
        httpDataDecoder(jsonObject, data);
        return jsonObject;
    }

    public static void httpDataDecoder(JSONObject jsonObject, String data) {

        String[] split = data.split("&");
        for (String s : split) {
            int index = s.indexOf("=");
            if (index < 0) {
                jsonObject.put(s, "");
            } else {
                jsonObject.put(s.substring(0, index), s.substring(index + 1));
            }
        }

    }

}
