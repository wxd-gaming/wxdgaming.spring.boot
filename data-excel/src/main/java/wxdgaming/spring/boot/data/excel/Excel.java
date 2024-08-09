package wxdgaming.spring.boot.data.excel;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import wxdgaming.spring.boot.core.Throw;
import wxdgaming.spring.boot.core.json.FastJsonUtil;
import wxdgaming.spring.boot.core.lang.ConvertUtil;
import wxdgaming.spring.boot.core.util.StringsUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2021-10-14 11:20
 **/
@Slf4j
public class Excel implements Serializable {

    private static final long serialVersionUID = 1L;

    protected String[] splitStrs = {"[,，]", "[:：]"};

    protected final Workbook builderWorkbook(File file) {
        if (file == null || StringsUtil.emptyOrNull(file.getName()) || file.getName().contains("@") || file.getName().contains("$")) {
            log.info("Excel文件不能解析：{}", file);
            return null;
        }
        try {
            String fileName = file.getName().toLowerCase();
            Workbook workbook;
            InputStream is = new FileInputStream(file.getPath());
            if (fileName.endsWith(".xls")) {
                workbook = new HSSFWorkbook(is);
            } else if (fileName.endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(is);
            } else {
                log.info("无法识别的文件：{}", file.getPath());
                return null;
            }
            if (workbook.getNumberOfSheets() < 1) {
                log.info("文件空的：{}", file.getPath());
                return null;
            }
            return workbook;
        } catch (Throwable throwable) {
            throw Throw.of(file.getPath(), throwable);
        }
    }

    protected Object getCellValue(TableInfo entityTable, int rowNumber, CellInfo entityField, Cell cellData) {
        /*空白的话，根据传入的类型返回默认值*/
        String trim = "";
        try {
            if (cellData != null) {

                /*以下是判断数据的类型*/
                switch (cellData.getCellType()) {
                    case NUMERIC: /*数字*/
                        DecimalFormat df = new DecimalFormat("0");
                        trim = df.format(cellData.getNumericCellValue());
                        break;
                    case STRING: /*字符串*/
                        trim = cellData.getStringCellValue();
                        break;
                    case BOOLEAN: /*Boolean*/
                        trim = String.valueOf(cellData.getBooleanCellValue());
                        break;
                    case FORMULA: /*公式*/ {
                        /*得到对应单元格的字符串*/
                        try {
                            trim = String.valueOf(cellData.getNumericCellValue());
                        } catch (IllegalStateException e) {
                            trim = String.valueOf(cellData.getRichStringCellValue());
                        }
                    }
                    break;
                    case BLANK: /*空值*/
                        trim = "";
                        break;
                    case ERROR: /*故障*/
                        trim = "非法字符";
                        break;
                    default:
                        trim = "未知类型";
                        break;
                }

                switch (entityField.getFieldTypeString().toLowerCase()) {
                    case "byte[]": {
                        byte[] arrays;
                        if (notNullOrEmpty(trim)) {
                            if (trim.startsWith("[") && trim.endsWith("]")) {
                                arrays = FastJsonUtil.parse(trim.replace('|', ','), byte[].class);
                            } else {
                                String[] split = trim.split(splitStrs[1]);
                                arrays = new byte[split.length];
                                for (int i = 0; i < split.length; i++) {
                                    arrays[i] = Double.valueOf(split[i]).byteValue();
                                }
                            }
                        } else {
                            arrays = new byte[0];
                        }
                        return arrays;
                    }
                    case "byte[][]": {
                        byte[][] arrays;
                        if (notNullOrEmpty(trim)) {
                            if (trim.startsWith("[") && trim.endsWith("]")) {
                                arrays = FastJsonUtil.parse(trim.replace('|', ','), byte[][].class);
                            } else {
                                String[] split0 = trim.split(splitStrs[0]);
                                arrays = new byte[split0.length][];
                                for (int i0 = 0; i0 < split0.length; i0++) {
                                    String[] split1 = split0[i0].split(splitStrs[1]);
                                    byte[] integers = new byte[split1.length];
                                    for (int i1 = 0; i1 < split1.length; i1++) {
                                        integers[i1] = Double.valueOf(split1[i1]).byteValue();
                                    }
                                    arrays[i0] = integers;
                                }
                            }
                        } else {
                            arrays = new byte[0][];
                        }
                        return arrays;
                    }
                    case "int[]": {
                        int[] arrays;
                        if (notNullOrEmpty(trim)) {
                            if (trim.startsWith("[") && trim.endsWith("]")) {
                                arrays = FastJsonUtil.parse(trim.replace('|', ','), int[].class);
                            } else {
                                String[] split = trim.split(splitStrs[1]);
                                arrays = new int[split.length];
                                for (int i = 0; i < split.length; i++) {
                                    arrays[i] = Double.valueOf(split[i]).intValue();
                                }
                            }
                        } else {
                            arrays = new int[0];
                        }
                        return arrays;
                    }
                    case "int[][]": {
                        int[][] arrays;
                        if (notNullOrEmpty(trim)) {
                            if (trim.startsWith("[") && trim.endsWith("]")) {
                                arrays = FastJsonUtil.parse(trim.replace('|', ','), int[][].class);
                            } else {
                                String[] split0 = trim.split(splitStrs[0]);
                                arrays = new int[split0.length][];
                                for (int i0 = 0; i0 < split0.length; i0++) {
                                    String[] split1 = split0[i0].split(splitStrs[1]);
                                    int[] integers = new int[split1.length];
                                    for (int i1 = 0; i1 < split1.length; i1++) {
                                        integers[i1] = Double.valueOf(split1[i1]).intValue();
                                    }
                                    arrays[i0] = integers;
                                }
                            }
                        } else {
                            arrays = new int[0][];
                        }
                        return arrays;
                    }
                    case "long[]": {
                        long[] arrays;
                        if (notNullOrEmpty(trim)) {
                            if (trim.startsWith("[") && trim.endsWith("]")) {
                                arrays = FastJsonUtil.parse(trim.replace('|', ','), long[].class);
                            } else {
                                String[] split = trim.split(splitStrs[1]);
                                arrays = new long[split.length];
                                for (int i = 0; i < split.length; i++) {
                                    arrays[i] = Double.valueOf(split[i]).longValue();
                                }
                            }
                        } else {
                            arrays = new long[0];
                        }
                        return arrays;
                    }
                    case "long[][]": {
                        long[][] arrays;
                        if (notNullOrEmpty(trim)) {
                            if (trim.startsWith("[") && trim.endsWith("]")) {
                                arrays = FastJsonUtil.parse(trim.replace('|', ','), long[][].class);
                            } else {
                                String[] split0 = trim.split(splitStrs[0]);
                                arrays = new long[split0.length][];
                                for (int i0 = 0; i0 < split0.length; i0++) {
                                    String[] split1 = split0[i0].split(splitStrs[1]);
                                    long[] vs1 = new long[split1.length];
                                    for (int i1 = 0; i1 < split1.length; i1++) {
                                        vs1[i1] = Double.valueOf(split1[i1]).longValue();
                                    }
                                    arrays[i0] = vs1;
                                }
                            }
                        } else {
                            arrays = new long[0][];
                        }
                        return arrays;
                    }
                    case "float[]": {
                        float[] arrays;
                        if (notNullOrEmpty(trim)) {
                            if (trim.startsWith("[") && trim.endsWith("]")) {
                                arrays = FastJsonUtil.parse(trim.replace('|', ','), float[].class);
                            } else {
                                String[] split = trim.split(splitStrs[1]);
                                arrays = new float[split.length];
                                for (int i = 0; i < split.length; i++) {
                                    arrays[i] = Double.valueOf(split[i]).floatValue();
                                }
                            }
                        } else {
                            arrays = new float[0];
                        }
                        return arrays;
                    }
                    case "float[][]": {
                        float[][] arrays;
                        if (notNullOrEmpty(trim)) {
                            if (trim.startsWith("[") && trim.endsWith("]")) {
                                arrays = FastJsonUtil.parse(trim.replace('|', ','), float[][].class);
                            } else {
                                String[] split0 = trim.split(splitStrs[0]);
                                arrays = new float[split0.length][];
                                for (int i0 = 0; i0 < split0.length; i0++) {
                                    String[] split1 = split0[i0].split(splitStrs[1]);
                                    float[] vs1 = new float[split1.length];
                                    for (int i = 0; i < split1.length; i++) {
                                        vs1[i] = Double.valueOf(split1[i]).floatValue();
                                    }
                                    arrays[i0] = vs1;
                                }
                            }
                        } else {
                            arrays = new float[0][];
                        }
                        return arrays;
                    }
                    case "string[]": {
                        String[] arrays;
                        if (notNullOrEmpty(trim)) {
                            if (trim.startsWith("[") && trim.endsWith("]")) {
                                arrays = FastJsonUtil.parse(trim.replace('|', ','), String[].class);
                            } else {
                                arrays = trim.split(splitStrs[1]);
                            }
                        } else {
                            arrays = new String[0];
                        }
                        return arrays;
                    }
                    case "string[][]": {
                        String[][] arrays;
                        if (notNullOrEmpty(trim)) {
                            if (trim.startsWith("[") && trim.endsWith("]")) {
                                arrays = FastJsonUtil.parse(trim.replace('|', ','), String[][].class);
                            } else {
                                String[] split0 = trim.split(splitStrs[0]);
                                arrays = new String[split0.length][];
                                for (int i = 0; i < split0.length; i++) {
                                    arrays[i] = split0[i].split(splitStrs[1]);
                                }
                            }
                        } else {
                            arrays = new String[0][];
                        }
                        return arrays;
                    }
                    case "list<bool>":
                    case "list<boolean>":
                    case "arraylist<boolean>": {
                        List<Boolean> list;
                        if (notNullOrEmpty(trim)) {
                            list = FastJsonUtil.parseArray(trim.replace('|', ','), Boolean.class);
                        } else {
                            list = new ArrayList<>();
                        }
                        return list;
                    }
                    case "list<byte>":
                    case "arraylist<byte>": {
                        List<Byte> list;
                        if (notNullOrEmpty(trim)) {
                            list = FastJsonUtil.parseArray(trim.replace('|', ','), Byte.class);
                        } else {
                            list = new ArrayList<>();
                        }
                        return list;
                    }
                    case "list<int>":
                    case "list<integer>":
                    case "arraylist<int>":
                    case "arraylist<integer>": {
                        List<Integer> list;
                        if (notNullOrEmpty(trim)) {
                            list = FastJsonUtil.parseArray(trim.replace('|', ','), Integer.class);
                        } else {
                            list = new ArrayList<>();
                        }
                        return list;
                    }
                    case "list<long>":
                    case "arraylist<long>": {
                        List<Long> list;
                        if (notNullOrEmpty(trim)) {
                            list = FastJsonUtil.parseArray(trim.replace('|', ','), Long.class);
                        } else {
                            list = new ArrayList<>();
                        }
                        return list;
                    }
                    case "list<string>":
                    case "arraylist<string>": {
                        List<String> list;
                        if (notNullOrEmpty(trim)) {
                            list = FastJsonUtil.parseArray(trim.replace('|', ','), String.class);
                        } else {
                            list = new ArrayList<>();
                        }
                        return list;
                    }
                    case "set<byte>": {
                        Set<Byte> list;
                        if (notNullOrEmpty(trim)) {
                            list = new LinkedHashSet<>(FastJsonUtil.parseArray(trim.replace('|', ','), Byte.class));
                        } else {
                            list = new LinkedHashSet<>();
                        }
                        return list;
                    }
                    case "set<int>": {
                        Set<Integer> list;
                        if (notNullOrEmpty(trim)) {
                            list = new LinkedHashSet<>(FastJsonUtil.parseArray(trim.replace('|', ','), Integer.class));
                        } else {
                            list = new LinkedHashSet<>();
                        }
                        return list;
                    }
                    case "set<long>": {
                        Set<Long> list;
                        if (notNullOrEmpty(trim)) {
                            list = new LinkedHashSet<>(FastJsonUtil.parseArray(trim.replace('|', ','), Long.class));
                        } else {
                            list = new LinkedHashSet<>();
                        }
                        return list;
                    }
                    case "set<string>": {
                        Set<String> list;
                        if (notNullOrEmpty(trim)) {
                            list = new LinkedHashSet<>(FastJsonUtil.parseArray(trim.replace('|', ','), String.class));
                        } else {
                            list = new LinkedHashSet<>();
                        }
                        return list;
                    }
                    default: {
                        try {
                            return ConvertUtil.changeType(trim, entityField.getFieldType());
                        } catch (Exception e) {
                            return ConvertUtil.changeType(trim, entityField.getFieldType());
                        }
                    }
                }
            }
            return ConvertUtil.defaultValue(entityField.getFieldType());
        } catch (Exception ex) {
            final RuntimeException runtimeException = new RuntimeException(
                    ex.getMessage()
                            + "\n文件：" + entityTable.getTableComment()
                            + ";\nsheet：" + entityTable.getTableName()
                            + ";\n列：" + entityField.getFieldName()
                            + ";\n行：" + rowNumber
                            + ";\n数据类型：" + entityField.getFieldTypeString().toLowerCase()
                            + ";\n数据：" + trim + "----无法转换");
            runtimeException.setStackTrace(ex.getStackTrace());
            throw runtimeException;
        }
    }

    boolean notNullOrEmpty(String source) {
        if (StringsUtil.notEmptyOrNull(source)) {
            return !"#null".equalsIgnoreCase(source);
        }
        return false;
    }

    /**
     * 获取一列的字符
     *
     * @param data
     * @param isColumnName 如果是列名，需要转化首字母小写
     * @return
     */
    protected String getCellString(Cell data, boolean isColumnName) {
        String trim = "";
        if (data != null) {
            /*空白的话，根据传入的类型返回默认值*/
            /*默认类型*/
            if (data.getCellType() == CellType.STRING
                    || (data.getCellType() == CellType.FORMULA && data.getCachedFormulaResultType() == CellType.STRING)) {
                /*字符类型*/
                trim = data.getStringCellValue().trim();
            }
            if (StringsUtil.emptyOrNull(trim)) {
                trim = data.toString().trim();
            }
        }
        if (StringsUtil.notEmptyOrNull(trim)) {
            trim = trim.replace("class", "clazz")
                    .replace("-", "_");
            if (isColumnName) {
                trim = StringsUtil.lowerFirst(trim);
            }
        }
        return trim.trim();
    }

    public void buildColumnType(CellInfo entityField, String fieldTypeName, boolean client) {
        final String typeString = typeString(fieldTypeName);
        switch (typeString.toLowerCase()) {
            case "bool":
            case "boolean":
                entityField.setFieldType(boolean.class);
                entityField.setFieldTypeString("boolean");
                break;
            case "java.lang.boolean":
                entityField.setFieldType(Boolean.class);
                entityField.setFieldTypeString("Boolean");
                break;
            case "byte":
                entityField.setFieldType(byte.class);
                entityField.setFieldTypeString("byte");
                break;
            case "java.lang.byte":
                entityField.setFieldType(Byte.class);
                entityField.setFieldTypeString("Byte");
                break;
            case "short":
                entityField.setFieldType(short.class);
                entityField.setFieldTypeString("short");
                break;
            case "java.lang.short":
                entityField.setFieldType(Short.class);
                entityField.setFieldTypeString("Short");
                break;
            case "int":
                entityField.setFieldType(int.class);
                entityField.setFieldTypeString("int");
                break;
            case "java.lang.integer":
                entityField.setFieldType(Integer.class);
                entityField.setFieldTypeString("Integer");
                break;
            case "long":
                entityField.setFieldType(long.class);
                entityField.setFieldTypeString("long");
                break;
            case "java.lang.long":
                entityField.setFieldType(Long.class);
                entityField.setFieldTypeString("Long");
                break;
            case "float":
                entityField.setFieldType(float.class);
                entityField.setFieldTypeString("float");
                break;
            case "java.lang.float":
                entityField.setFieldType(Float.class);
                entityField.setFieldTypeString("Float");
                break;
            case "double":
                entityField.setFieldType(double.class);
                entityField.setFieldTypeString("double");
                break;
            case "java.lang.double":
                entityField.setFieldType(Double.class);
                entityField.setFieldTypeString("Double");
                break;
            case "java.util.date":
                entityField.setFieldType(Date.class);
                entityField.setFieldTypeString("Date");
                break;
            case "java.math.biginteger":
                entityField.setFieldType(BigInteger.class);
                entityField.setFieldTypeString("BigInteger");
                break;
            case "java.math.bigdecimal":
                entityField.setFieldType(BigDecimal.class);
                entityField.setFieldTypeString("BigDecimal");
                break;
            case "string[]":
            case "java.lang.string[]":
                /*String[]*/
                entityField.setFieldType(String[].class);
                entityField.setFieldTypeString("String[]");
                break;
            case "list<string>":
                /*String[]*/
                entityField.setFieldType(ArrayList.class);
                entityField.setFieldTypeString("ArrayList<String>");
                break;
            case "string[][]":
            case "java.lang.string[][]":
                /*String[][]*/
                entityField.setFieldType(String[][].class);
                entityField.setFieldTypeString("String[][]");
                break;
            case "bool[]":
            case "boolean[]":
                entityField.setFieldType(boolean[].class);
                entityField.setFieldTypeString("boolean[]");
                break;
            case "java.lang.boolean[]":
                entityField.setFieldType(Boolean[].class);
                entityField.setFieldTypeString("Boolean[]");
                break;
            case "list<bool>":
            case "list<boolean>":
                entityField.setFieldType(ArrayList.class);
                entityField.setFieldTypeString("ArrayList<Boolean>");
                break;
            case "bool[][]":
            case "boolean[][]":
                entityField.setFieldType(boolean[][].class);
                entityField.setFieldTypeString("boolean[][]");
                break;
            case "java.lang.boolean[][]":
                entityField.setFieldType(Boolean[][].class);
                entityField.setFieldTypeString("Boolean[][]");
                break;
            case "byte[]":
                entityField.setFieldType(byte[].class);
                entityField.setFieldTypeString("byte[]");
                break;
            case "list<byte>":
                entityField.setFieldType(ArrayList.class);
                entityField.setFieldTypeString("ArrayList<Byte>");
                break;
            case "byte[][]":
                entityField.setFieldType(byte[][].class);
                entityField.setFieldTypeString("byte[][]");
                break;
            case "java.lang.byte[]":
                entityField.setFieldType(Byte[].class);
                entityField.setFieldTypeString("Byte[]");
                break;
            case "java.lang.byte[][]":
                entityField.setFieldType(Byte[][].class);
                entityField.setFieldTypeString("Byte[][]");
                break;
            case "int[]":
                entityField.setFieldType(int[].class);
                entityField.setFieldTypeString("int[]");
                break;
            case "java.lang.integer[]":
                entityField.setFieldType(Integer[].class);
                entityField.setFieldTypeString("Integer[]");
                break;
            case "list":
            case "list<int>":
            case "list<integer>":
            case "arraylist<int>":
            case "arraylist<integer>":
                entityField.setFieldType(ArrayList.class);
                entityField.setFieldTypeString("ArrayList<Integer>");
                break;
            case "int[][]":
                entityField.setFieldType(int[][].class);
                entityField.setFieldTypeString("int[][]");
                break;
            case "java.lang.integer[][]":
                entityField.setFieldType(Integer[][].class);
                entityField.setFieldTypeString("Integer[][]");
                break;
            case "list<int[][]>":
            case "arraylist<int[][]>":
                entityField.setFieldType(ArrayList.class);
                entityField.setFieldTypeString("ArrayList<int[][]>");
                break;
            case "list<java.lang.integer[][]>":
            case "arraylist<java.lang.integer[][]>":
                entityField.setFieldType(ArrayList.class);
                entityField.setFieldTypeString("ArrayList<Integer[][]>");
                break;
            case "long[]":
                entityField.setFieldType(long[].class);
                entityField.setFieldTypeString("long[]");
                break;
            case "java.lang.long[]":
                entityField.setFieldType(Long[].class);
                entityField.setFieldTypeString("Long[]");
                break;
            case "list<long>":
            case "list<java.lang.long>":
            case "arraylist<long>":
            case "arraylist<java.lang.long>":
                entityField.setFieldType(ArrayList.class);
                entityField.setFieldTypeString("ArrayList<Long>");
                break;
            case "[[j":
            case "long[][]":
                entityField.setFieldType(long[][].class);
                entityField.setFieldTypeString("long[][]");
                break;
            case "java.lang.long[][]":
                entityField.setFieldType(Long[][].class);
                entityField.setFieldTypeString("Long[][]");
                break;
            case "list<long[]>":
            case "arraylist<long[]>":
                entityField.setFieldType(ArrayList.class);
                entityField.setFieldTypeString("ArrayList<long[]>");
                break;
            case "list<java.lang.long[]>":
            case "arraylist<java.lang.long[]>":
                entityField.setFieldType(ArrayList.class);
                entityField.setFieldTypeString("ArrayList<Long[][]>");
                break;
            case "float[]":
                entityField.setFieldType(float[].class);
                entityField.setFieldTypeString("float[]");
                break;
            case "java.lang.float[]":
                entityField.setFieldType(Float[].class);
                entityField.setFieldTypeString("Float[]");
                break;
            case "list<float>":
            case "list<java.lang.float>":
            case "arraylist<float>":
            case "arraylist<java.lang.float>":
                entityField.setFieldType(ArrayList.class);
                entityField.setFieldTypeString("ArrayList<Float>");
                break;
            case "float[][]":
                entityField.setFieldType(float[][].class);
                entityField.setFieldTypeString("float[][]");
                break;
            case "java.lang.float[][]":
                entityField.setFieldType(Float[][].class);
                entityField.setFieldTypeString("Float[][]");
                break;
            case "list<float[]>":
            case "arraylist<float[]>":
                entityField.setFieldType(ArrayList.class);
                entityField.setFieldTypeString("ArrayList<float[]>");
                break;
            case "list<java.lang.float[]>":
            case "arraylist<java.lang.float[]>":
                entityField.setFieldType(ArrayList.class);
                entityField.setFieldTypeString("ArrayList<Float[]>");
                break;
            case "string":
            case "java.lang.string":
            default:
                entityField.setFieldType(String.class);
                entityField.setFieldTypeString("String");
                break;
        }
    }

    /**
     * 把数组类型还原成代码 字符串
     *
     * @param source
     * @return
     */
    public static String typeString(String source) {
        if (source.startsWith("[[L")) {
            source = source.substring(2, source.length() - 1) + "[][]";
        } else if (source.startsWith("[L")) {
            source = source.substring(2, source.length() - 1) + "[]";
        } else if (source.startsWith("[[Z")) {
            source = "boolean[][]";
        } else if (source.startsWith("[Z")) {
            source = "boolean[]";
        } else if (source.startsWith("[[B")) {
            source = "byte[][]";
        } else if (source.startsWith("[B")) {
            source = "byte[]";
        } else if (source.startsWith("[[C")) {
            source = "char[][]";
        } else if (source.startsWith("[C")) {
            source = "char[]";
        } else if (source.startsWith("[[S")) {
            source = "short[][]";
        } else if (source.startsWith("[S")) {
            source = "short[]";
        } else if (source.startsWith("[[I")) {
            source = "int[][]";
        } else if (source.startsWith("[I")) {
            source = "int[]";
        } else if (source.startsWith("[[J")) {
            source = "long[][]";
        } else if (source.startsWith("[J")) {
            source = "long[]";
        } else if (source.startsWith("[[F")) {
            source = "float[][]";
        } else if (source.startsWith("[F")) {
            source = "float[]";
        } else if (source.startsWith("[[D")) {
            source = "double[][]";
        } else if (source.startsWith("[D")) {
            source = "double[]";
        }
        source = source.replace("$", ".");
        return source;
    }

}
