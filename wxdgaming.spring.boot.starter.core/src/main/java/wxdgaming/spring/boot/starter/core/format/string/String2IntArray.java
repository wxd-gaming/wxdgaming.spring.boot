package wxdgaming.spring.boot.starter.core.format.string;

import wxdgaming.spring.boot.starter.core.json.FastJsonUtil;
import wxdgaming.spring.boot.starter.core.util.StringsUtil;

public class String2IntArray {

    public static final int[] EMPTY = new int[0];

    public static int[] parse(String trim) {
        int[] arrays;
        if (StringsUtil.notEmptyOrNull(trim)) {
            trim = trim.replace('|', ',');
            if (trim.startsWith("[") && trim.endsWith("]")) {
                arrays = FastJsonUtil.parse(trim, int[].class);
            } else {
                String[] split = trim.split("[，,|]");
                arrays = new int[split.length];
                for (int i = 0; i < split.length; i++) {
                    arrays[i] = Double.valueOf(split[i]).intValue();
                }
            }
        } else {
            arrays = EMPTY;
        }
        return arrays;
    }
}
