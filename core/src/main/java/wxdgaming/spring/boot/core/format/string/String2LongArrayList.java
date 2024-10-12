package wxdgaming.spring.boot.core.format.string;

import wxdgaming.spring.boot.core.json.FastJsonUtil;
import wxdgaming.spring.boot.core.util.StringsUtil;

import java.util.ArrayList;
import java.util.List;

public class String2LongArrayList {

    public static List<long[]> parse(String trim) {
        List<long[]> arrays;
        trim = trim.replace('|', ',');
        if (StringsUtil.notEmptyOrNull(trim)) {
            if (trim.startsWith("[") && trim.endsWith("]")) {
                arrays = FastJsonUtil.parseArray(trim, long[].class);
            } else {
                String[] split = trim.split("[;]");
                arrays = new ArrayList<>(split.length);
                for (int i = 0; i < split.length; i++) {
                    String[] split2 = split[i].split("[,，|]");
                    long[] vs1 = new long[split2.length];
                    for (int i1 = 0; i1 < split2.length; i1++) {
                        vs1[i1] = Double.valueOf(split2[i1]).longValue();
                    }
                    arrays.add(vs1);
                }
            }
        } else {
            arrays = List.of();
        }
        return arrays;
    }
}
