package wxdgaming.spring.boot.net.message;

import io.protostuff.Tag;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import wxdgaming.spring.boot.core.collection.MapOf;
import wxdgaming.spring.boot.core.io.FileReadUtil;
import wxdgaming.spring.boot.core.io.FileUtil;
import wxdgaming.spring.boot.core.io.FileWriteUtil;
import wxdgaming.spring.boot.core.lang.ObjectBase;
import wxdgaming.spring.boot.core.timer.MyClock;
import wxdgaming.spring.boot.core.util.StringsUtil;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * pojo生成
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-05-27 16:39
 **/
public class ProtoBuf2Pojo {

    public static void actionProtoFile(String outPath, String readPath) {

        FileUtil
                .walkFiles(readPath, ".proto")
                .forEach(filePath -> {

                    AtomicReference<BeanInfo> comment = new AtomicReference<>(new BeanInfo());
                    AtomicBoolean start = new AtomicBoolean();
                    AtomicReference<String> packageName = new AtomicReference<>("");

                    TreeSet<String> imports = new TreeSet<>();
                    imports.add(Tag.class.getName());
                    imports.add(Getter.class.getName());
                    imports.add(Setter.class.getName());
                    imports.add(PojoBase.class.getName());
                    imports.add(Accessors.class.getName());
                    imports.add(List.class.getName());
                    imports.add(Map.class.getName());
                    imports.add(MapOf.class.getName());

                    StringBuilder stringBuilder = new StringBuilder();

                    FileReadUtil.readLine(filePath, StandardCharsets.UTF_8, line -> {
                        line = line.trim();
                        if (StringsUtil.emptyOrNull(line)) return;
                        if (line.contains("java_multiple_files") && line.contains("true")) {

                        } else if (line.contains("java_package")) {
                            int i = line.indexOf("=");
                            int i1 = line.indexOf(";");
                            String trim = line.substring(i + 1, i1).trim().replace("\"", "");
                            packageName.set(trim);
                        } else if (line.startsWith("//")) {
                            comment.get().comment = line.substring(2);
                        } else if (line.startsWith("message") || line.startsWith("enum")) {
                            String[] split = line.split(" ");
                            System.out.println("【" + split[1] + "】");

                            comment.get().classType = line.startsWith("message") ? "class" : "enum";
                            comment.get().className = split[1];

                            start.set(true);
                        } else if (line.contains("}")) {
                            stringBuilder.append(comment.get().string());
                            comment.set(new BeanInfo());
                            start.set(false);
                        } else {
                            if (start.get()) {
                                comment.get().addField(line);
                            }
                        }
                    });

                    String to = "package " + packageName + ";";
                    to += "\n";
                    for (String anImport : imports) {
                        to += "\nimport " + anImport + ";";
                    }
                    to += "\n";
                    to += "\n";
                    to += "\n";
                    to += "/**\n" +
                          " * rpc.proto\n" +
                          " *\n" +
                          " * @author: wxd-gaming(無心道, 15388152619)\n" +
                          " * @version: " + MyClock.nowString() + "\n" +
                          " */";
                    String className = filePath.getFileName().toString().replace(".proto", "");
                    to += "\npublic class " + className + " {";
                    to += stringBuilder.toString();
                    to += "\n}";
                    System.out.println(to);
                    FileWriteUtil.writeString(outPath + "/" + packageName.get().replace(".", "/") + "/" + className + ".java", to);
                });

    }

    @Getter
    public static class BeanInfo {

        private String classType;
        private String className;
        private String comment;
        private final List<FiledInfo> filedInfos = new ArrayList<>();

        public void addField(String line) {
            FiledInfo filedInfo = new FiledInfo(classType, line);
            addField(filedInfo);
        }

        public void addField(FiledInfo filedInfo) {
            if (StringsUtil.notEmptyOrNull(filedInfo.getField())) {
                if (filedInfos.stream().anyMatch(v -> v.getTag() == filedInfo.getTag())) {
                    // tag重复
                    throw new RuntimeException(className + " - tag " + filedInfo.getTag() + " 重复");
                }
                if (filedInfos.stream().anyMatch(v -> Objects.equals(v.getFieldName(), filedInfo.getFieldName()))) {
                    // tag重复
                    throw new RuntimeException(className + " - tag " + filedInfo.getTag() + " 重复");
                }
                getFiledInfos().add(filedInfo);
            }
        }

        public String string() {
            if ("class".equals(classType)) {
                return classString();
            }
            return enumString();
        }

        public String classString() {
            String f = "\n";
            for (FiledInfo filedInfo : filedInfos) {
                f += filedInfo.classFiled();
            }
            String to = """
                    
                       /** %s */
                       @Getter
                       @Setter
                       @Accessors(chain = true)
                       public static class %s extends PojoBase {
                       %s
                       }
                    """.formatted(comment, className, f);
            return to;
        }

        public String enumString() {
            String f = "\n";
            for (FiledInfo filedInfo : filedInfos) {
                f += filedInfo.enumFiled();
            }
            String to = """
                    
                    
                        /** %s */
                        @Getter
                        public enum %s {
                        %s
                            ;
                    
                            private static final Map<Integer, %s> static_map = MapOf.asMap(%s::getCode, %s.values());
                    
                            public static %s valueOf(int code) {
                                return static_map.get(code);
                            }
                    
                            /** code */
                            private final int code;
                            /** 备注 */
                            private final String command;
                    
                            %s(int code, String command) {
                                this.code = code;
                                this.command = command;
                            }
                        }
                    """.formatted(comment, className, f, className, className, className, className, className);
            return to;
        }

    }

    @Getter
    @Setter
    @Accessors(chain = true)
    public static class FiledInfo extends ObjectBase {
        private int tag;
        private String fieldName;
        private String field;
        private String comment = "";

        public FiledInfo(String classType, String line) {
            if (StringsUtil.emptyOrNull(line)) {
                return;
            }
            line = line.trim();
            String[] split = line.split("[;；]");
            if (split.length > 1) {
                comment = split[1].replace("//", "");
            }
            List<String> split1 = List.of(split[0].split(" "));
            ArrayList<String> tmp = new ArrayList<>();
            if ("enum".equals(classType)) {
                tmp.add("enum");
            }
            for (String string : split1) {
                if (StringsUtil.emptyOrNull(string)) continue;
                tmp.add(string.trim());
            }

            String string = tmp.getFirst();
            boolean repeated = false;
            if (string.equals("repeated")) {
                repeated = true;
                tmp.removeFirst();
                string = tmp.getFirst();
            }
            switch (string) {
                case "bool":
                    if (repeated) {
                        field = "List<" + Boolean.class.getSimpleName() + ">";
                    } else {
                        field = boolean.class.getSimpleName();
                    }
                    break;
                case "int32":
                    if (repeated) {
                        field = "List<" + Integer.class.getSimpleName() + ">";
                    } else {
                        field = int.class.getSimpleName();
                    }
                    break;
                case "int64":
                    if (repeated) {
                        field = "List<" + Long.class.getSimpleName() + ">";
                    } else {
                        field = long.class.getSimpleName();
                    }
                    break;
                case "string":
                    if (repeated) {
                        field = "List<" + String.class.getSimpleName() + ">";
                    } else {
                        field = String.class.getSimpleName();
                    }
                    break;
                case "enum": {
                    field = "";
                }
                break;
                case "bytes": {
                    field = "byte[]";
                }
                break;
                default: {
                    if (string.startsWith("map<") && string.endsWith(">")) {
                        field = String.class.getSimpleName();
                        field = StringsUtil.upperFirst(field);
                        field = field
                                .replace("bool", Boolean.class.getSimpleName())
                                .replace("int32", Integer.class.getSimpleName())
                                .replace("int64", Long.class.getSimpleName())
                                .replace("bytes", byte[].class.getSimpleName())
                                .replace("string", String.class.getSimpleName())
                        ;
                    } else {
                        field = string;
                    }
                }
            }
            fieldName = tmp.get(1);
            field += " " + tmp.get(1);
            tag = Integer.parseInt(tmp.get(3));
        }

        public String classFiled() {
            return """
                           /** %s */
                           @Tag(%s)
                           private %s;
                    """.formatted(comment, tag, field);
        }

        public String enumFiled() {
            return """
                           /** %s */
                           @Tag(%s)
                           %s(%s, "%s"),
                    """.formatted(comment, tag, field.trim(), tag, comment);
        }
    }

}
