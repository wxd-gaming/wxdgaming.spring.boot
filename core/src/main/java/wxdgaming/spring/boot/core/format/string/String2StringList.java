package wxdgaming.spring.boot.core.format.string;

import wxdgaming.spring.boot.core.json.FastJsonUtil;
import wxdgaming.spring.boot.core.util.StringsUtil;

import java.util.List;

public class String2StringList {

    public static List<String> parse(String trim) {
        List<String> arrays;
        trim = trim.replace('|', ',');
        if (StringsUtil.notEmptyOrNull(trim)) {
            if (trim.startsWith("[") && trim.endsWith("]")) {
                arrays = FastJsonUtil.parseArray(trim, String.class);
            } else {
                String[] split = trim.split("[，,|]");
                arrays = List.of(split);
            }
        } else {
            arrays = List.of();
        }
        return arrays;
    }
}
