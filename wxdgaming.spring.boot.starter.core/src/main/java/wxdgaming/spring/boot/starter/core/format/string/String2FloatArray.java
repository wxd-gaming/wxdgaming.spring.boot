package wxdgaming.spring.boot.starter.core.format.string;

import wxdgaming.spring.boot.starter.core.json.FastJsonUtil;
import wxdgaming.spring.boot.starter.core.util.StringsUtil;

public class String2FloatArray {

    public static final float[] EMPTY = new float[0];

    public static float[] parse(String trim) {
        float[] arrays;
        trim = trim.replace('|', ',');
        if (StringsUtil.notEmptyOrNull(trim)) {
            if (trim.startsWith("[") && trim.endsWith("]")) {
                arrays = FastJsonUtil.parse(trim, float[].class);
            } else {
                String[] split = trim.split("[，,|]");
                arrays = new float[split.length];
                for (int i = 0; i < split.length; i++) {
                    arrays[i] = Double.valueOf(split[i]).floatValue();
                }
            }
        } else {
            arrays = EMPTY;
        }
        return arrays;
    }
}
