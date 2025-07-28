package wxdgaming.spring.boot.core.format.string;

import wxdgaming.spring.boot.core.chatset.StringUtils;
import wxdgaming.spring.boot.core.chatset.json.FastJsonUtil;

import java.util.ArrayList;
import java.util.List;

public class String2StringArrayList {


    public static List<String[]> parse(String trim) {
        List<String[]> arrays;
        trim = trim.replace('|', ',');
        if (StringUtils.isNotBlank(trim)) {
            if (trim.startsWith("[") && trim.endsWith("]")) {
                arrays = FastJsonUtil.parseArray(trim, String[].class);
            } else {
                String[] split = trim.split("[;]");
                arrays = new ArrayList<>(split.length);
                for (int i = 0; i < split.length; i++) {
                    String[] split2 = split[i].split("[,，|]");
                    arrays.add(split2);
                }
            }
        } else {
            arrays = List.of();
        }
        return arrays;
    }
}
