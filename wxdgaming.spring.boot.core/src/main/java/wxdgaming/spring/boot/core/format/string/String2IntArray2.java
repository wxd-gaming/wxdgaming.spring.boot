package wxdgaming.spring.boot.core.format.string;

import wxdgaming.spring.boot.core.json.FastJsonUtil;
import wxdgaming.spring.boot.core.util.StringsUtil;

public class String2IntArray2 {

    public static final int[][] EMPTY = new int[0][];

    public static int[][] parse(String trim) {
        int[][] arrays;
        trim = trim.replace('|', ',');
        if (StringsUtil.notEmptyOrNull(trim)) {
            if (trim.startsWith("[") && trim.endsWith("]")) {
                arrays = FastJsonUtil.parse(trim, int[][].class);
            } else {
                String[] split = trim.split("[;]");
                arrays = new int[split.length][];
                for (int i = 0; i < split.length; i++) {
                    String[] split2 = split[i].split("[,，|]");
                    int[] vs1 = new int[split2.length];
                    for (int i1 = 0; i1 < split2.length; i1++) {
                        vs1[i1] = Double.valueOf(split2[i1]).intValue();
                    }
                    arrays[i] = vs1;
                }
            }
        } else {
            arrays = EMPTY;
        }
        return arrays;
    }
}
