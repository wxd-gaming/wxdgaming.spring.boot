package wxdgaming.spring.boot.starter.core.format.string;

import wxdgaming.spring.boot.starter.core.json.FastJsonUtil;
import wxdgaming.spring.boot.starter.core.util.StringsUtil;

public class String2LongArray {

    public static final long[] EMPTY = new long[0];

    public static long[] parse(String trim) {
        long[] arrays;
        trim = trim.replace('|', ',');
        if (StringsUtil.notEmptyOrNull(trim)) {
            if (trim.startsWith("[") && trim.endsWith("]")) {
                arrays = FastJsonUtil.parse(trim, long[].class);
            } else {
                String[] split = trim.split("[，,|]");
                arrays = new long[split.length];
                for (int i = 0; i < split.length; i++) {
                    arrays[i] = Double.valueOf(split[i]).longValue();
                }
            }
        } else {
            arrays = EMPTY;
        }
        return arrays;
    }
}
