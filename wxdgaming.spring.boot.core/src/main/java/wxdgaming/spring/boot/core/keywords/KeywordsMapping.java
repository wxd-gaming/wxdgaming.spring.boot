package wxdgaming.spring.boot.core.keywords;

import lombok.Getter;
import wxdgaming.spring.boot.core.chatset.json.FastJsonUtil;
import wxdgaming.spring.boot.core.lang.DiffTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 关键字
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-21 11:40
 **/
@Getter
public class KeywordsMapping {

    Map<Character, Object> map = new HashMap<>();

    public static void main(String[] args) {
        KeywordsMapping keywordsMapping = new KeywordsMapping();
        keywordsMapping.add("你好");
        keywordsMapping.add("你妈的");
        keywordsMapping.add("我是你");

        String source = "我无法我无法我无法我无法我无法我无法我无法我无法我无法我无法我无法我无法我无法我无法我无法我无法我无法我无法我无法我无法我无法我无法我无法我无法我无法我无法我无法我无法我无法我无法我无法我无法我无法我无法我无法我无法我无法我无法我无你好法我无法为你妈的各位各位";

        System.out.println(FastJsonUtil.toJSONStringAsFmt(keywordsMapping));
        System.out.println(keywordsMapping.replace(source, '*'));
        DiffTime diffTime = new DiffTime();
        for (int i = 0; i < 10; i++) {
            diffTime.reset();
            boolean contains = keywordsMapping.contains(source);
            float diff = diffTime.diffMs5();
            System.out.println(diff + " ms " + contains);
            diffTime.reset();
            List<String> wordsed = keywordsMapping.words(source);
            diff = diffTime.diffMs5();
            System.out.println(diff + " ms " + wordsed);
            System.out.println("==================================");
        }
    }

    public void reset() {
        map = new HashMap<>();
    }

    /** 请注意非线程安全的 */
    public void add(String str) {
        if (str == null || str.isBlank()) {
            return;
        }
        char[] charArray = str.toCharArray();
        Map o = map;
        for (char c : charArray) {
            o = (Map) o.computeIfAbsent(c, k -> new HashMap<>());
        }
        o.put("end", "0");
    }

    public boolean contains(String str) {
        if (str == null || str.isBlank()) {
            return false;
        }
        char[] charArray = str.toCharArray();

        for (int i = 0; i < charArray.length; i++) {
            Map o = map;
            for (int k = i; k < charArray.length; k++) {
                char c = charArray[k];
                Map tmp = (Map) o.get(c);
                if (tmp == null) {
                    break;
                }
                o = tmp;
                if (o.get("end") != null) {
                    return true;
                }
            }
        }
        return false;
    }

    public String replace(String str, char replacement) {
        if (str == null || str.isBlank()) {
            return str;
        }
        char[] charArray = str.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            Map o = map;
            for (int k = i; k < charArray.length; k++) {
                char c = charArray[k];
                Map tmp = (Map) o.get(c);
                if (tmp == null) {
                    break;
                }
                o = tmp;
                if (o.get("end") != null) {
                    for (int j = i; j <= k; j++) {
                        charArray[j] = replacement;
                    }
                    i = k - 1;
                    break;
                }
            }
        }
        return new String(charArray);
    }

    public List<String> words(String str) {
        if (str == null || str.isBlank()) {
            return List.of();
        }
        char[] charArray = str.toCharArray();
        List<String> strings = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < charArray.length; i++) {
            Map o = map;
            for (int k = i; k < charArray.length; k++) {
                char c = charArray[k];
                Map tmp = (Map) o.get(c);
                if (tmp == null) {
                    stringBuilder.setLength(0);
                    break;
                }
                o = tmp;
                stringBuilder.append(c);
                if (o.get("end") != null) {
                    String string = stringBuilder.toString();
                    strings.add(string);
                    stringBuilder.setLength(0);
                    i = k - 1;
                    break;
                }
            }
        }
        return strings;
    }

}
