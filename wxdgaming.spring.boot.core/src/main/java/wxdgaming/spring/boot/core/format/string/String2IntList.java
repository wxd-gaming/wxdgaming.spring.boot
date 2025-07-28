package wxdgaming.spring.boot.core.format.string;


import wxdgaming.spring.boot.core.chatset.StringUtils;
import wxdgaming.spring.boot.core.chatset.json.FastJsonUtil;

import java.util.ArrayList;
import java.util.List;

public class String2IntList {

    public static List<Integer> parse(String trim) {
        List<Integer> arrays;
        trim = trim.replace('|', ',');
        if (StringUtils.isNotBlank(trim)) {
            if (trim.startsWith("[") && trim.endsWith("]")) {
                arrays = FastJsonUtil.parseArray(trim, Integer.class);
            } else {
                String[] split = trim.split("[，,|]");
                arrays = new ArrayList<>(split.length);
                for (int i = 0; i < split.length; i++) {
                    arrays.add(Double.valueOf(split[i]).intValue());
                }
            }
        } else {
            arrays = List.of();
        }
        return arrays;
    }
}
