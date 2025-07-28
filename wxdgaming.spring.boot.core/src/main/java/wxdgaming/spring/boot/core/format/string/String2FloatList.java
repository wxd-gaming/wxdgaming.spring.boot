package wxdgaming.spring.boot.core.format.string;


import wxdgaming.spring.boot.core.chatset.StringUtils;
import wxdgaming.spring.boot.core.chatset.json.FastJsonUtil;

import java.util.ArrayList;
import java.util.List;

public class String2FloatList {

    public static List<Float> parse(String trim) {
        List<Float> arrays;
        trim = trim.replace('|', ',');
        if (StringUtils.isNotBlank(trim)) {
            if (trim.startsWith("[") && trim.endsWith("]")) {
                arrays = FastJsonUtil.parseArray(trim, Float.class);
            } else {
                String[] split = trim.split("[，,|]");
                arrays = new ArrayList<>(split.length);
                for (int i = 0; i < split.length; i++) {
                    arrays.add(Double.valueOf(split[i]).floatValue());
                }
            }
        } else {
            arrays = List.of();
        }
        return arrays;
    }
}
