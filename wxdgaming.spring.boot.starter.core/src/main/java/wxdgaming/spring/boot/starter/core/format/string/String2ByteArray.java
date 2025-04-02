package wxdgaming.spring.boot.starter.core.format.string;

import wxdgaming.spring.boot.starter.core.json.FastJsonUtil;
import wxdgaming.spring.boot.starter.core.util.StringsUtil;

public class String2ByteArray {

    public static final byte[] EMPTY = new byte[0];

    public static byte[] parse(String trim) {
        byte[] arrays;
        if (StringsUtil.notEmptyOrNull(trim)) {
            trim = trim.replace('|', ',');
            if (trim.startsWith("[") && trim.endsWith("]")) {
                arrays = FastJsonUtil.parse(trim, byte[].class);
            } else {
                String[] split = trim.split("[，,|]");
                arrays = new byte[split.length];
                for (int i = 0; i < split.length; i++) {
                    arrays[i] = Double.valueOf(split[i]).byteValue();
                }
            }
        } else {
            arrays = EMPTY;
        }
        return arrays;
    }
}
