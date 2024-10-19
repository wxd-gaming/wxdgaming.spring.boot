package wxdgaming.spring.boot.core.format.string;

import wxdgaming.spring.boot.core.json.FastJsonUtil;
import wxdgaming.spring.boot.core.util.StringsUtil;

import java.util.ArrayList;
import java.util.List;

public class String2BoolList {

    public static List<Boolean> parse(String trim) {
        List<Boolean> arrays;
        trim = trim.replace('|', ',');
        if (StringsUtil.notEmptyOrNull(trim)) {
            if (trim.startsWith("[") && trim.endsWith("]")) {
                arrays = FastJsonUtil.parseArray(trim, Boolean.class);
            } else {
                String[] split = trim.split("[，,|]");
                arrays = new ArrayList<>(split.length);
                for (int i = 0; i < split.length; i++) {
                    arrays.add(Boolean.parseBoolean(split[i]));
                }
            }
        } else {
            arrays = List.of();
        }
        return arrays;
    }
}
