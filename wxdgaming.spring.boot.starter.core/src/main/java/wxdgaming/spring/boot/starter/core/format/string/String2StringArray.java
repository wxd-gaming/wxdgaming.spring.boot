package wxdgaming.spring.boot.starter.core.format.string;

import wxdgaming.spring.boot.starter.core.json.FastJsonUtil;
import wxdgaming.spring.boot.starter.core.util.StringsUtil;

public class String2StringArray {

    public static final String[] EMPTY = new String[0];

    public static String[] parse(String trim) {
        String[] arrays;
        trim = trim.replace('|', ',');
        if (StringsUtil.notEmptyOrNull(trim)) {
            if (trim.startsWith("[") && trim.endsWith("]")) {
                arrays = FastJsonUtil.parse(trim, String[].class);
            } else {
                String[] split = trim.split("[，,|]");
                arrays = new String[split.length];
                for (int i = 0; i < split.length; i++) {
                    arrays[i] = split[i];
                }
            }
        } else {
            arrays = EMPTY;
        }
        return arrays;
    }
}
