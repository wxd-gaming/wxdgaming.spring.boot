package wxdgaming.spring.boot.core.format.string;

import wxdgaming.spring.boot.core.json.FastJsonUtil;
import wxdgaming.spring.boot.core.util.StringsUtil;

import java.util.ArrayList;
import java.util.List;

public class String2BoolArrayList {

    public static List<boolean[]> parse(String trim) {
        List<boolean[]> arrays;
        trim = trim.replace('|', ',');
        if (StringsUtil.notEmptyOrNull(trim)) {
            if (trim.startsWith("[") && trim.endsWith("]")) {
                arrays = FastJsonUtil.parseArray(trim, boolean[].class);
            } else {
                String[] split = trim.split("[;]");
                arrays = new ArrayList<>(split.length);
                for (int i = 0; i < split.length; i++) {
                    String[] split2 = split[i].split("[,，|]");
                    boolean[] vs1 = new boolean[split2.length];
                    for (int i1 = 0; i1 < split2.length; i1++) {
                        vs1[i1] = Boolean.parseBoolean(split2[i1]);
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
