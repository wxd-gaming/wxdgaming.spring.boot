package wxdgaming.spring.boot.data.excel;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import wxdgaming.spring.boot.core.InitPrint;
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
import java.util.concurrent.ConcurrentHashMap;

/**
 * excel 仓储
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2021-10-14 11:20
 **/
@Slf4j
@Getter
@Service
public class ExcelRepository implements Serializable, InitPrint {

    private static final long serialVersionUID = 1L;

    private final String[] String_Split = {"[,，]", "[:：]"};

    private final Map<String, TableData> tableInfoMap = new ConcurrentHashMap<>();

    public Optional<TableData> tableData(String tableName) {
        return Optional.ofNullable(tableInfoMap.get(tableName));
    }

    public final void readExcel(File file) {
        if (file == null || StringsUtil.emptyOrNull(file.getName()) || file.getName().contains("@") || file.getName().contains("$")) {
            log.info("Excel文件不能解析：{}", file);
            return;
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
                return;
            }
            if (workbook.getNumberOfSheets() < 1) {
                log.info("文件空的：{}", file.getPath());
                return;
            }
            /*多少页签*/
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                String sheetName = sheet.getSheetName().trim().toLowerCase();

                if (StringsUtil.emptyOrNull(sheetName)
                        || sheetName.startsWith("sheet")
                        || sheetName.contains("@")
                        || sheetName.contains("$")
                        || !sheetName.startsWith("q_")) {
                    log.debug("Excel文件不能解析：{}, sheetName={} - 需要是 q_ 开头 sheet name 才能解析", file, sheetName);
                    continue;
                }

                Cell tableCommentCall = sheet.getRow(0).getCell(0);
                String tableComment = readCellString(tableCommentCall, false);

                TableData tableData = new TableData(file.getPath(), file.getName(), sheet.getSheetName(), sheetName, tableComment);

                Row fieldBelongRow = sheet.getRow(1);/*归属*/
                Row fieldNameRow = sheet.getRow(2);/*字段名字*/
                Row fieldTypeRow = sheet.getRow(3);/*字段类型*/
                Row fieldCommentRow = sheet.getRow(4);/*字段含义*/

                TreeMap<Integer, CellInfo> cellInfoMap = new TreeMap<>();

                short lastCellNum = fieldNameRow.getLastCellNum();
                for (int cellIndex = 0; cellIndex < lastCellNum; cellIndex++) {
                    String fieldBelongCell = readCellString(fieldBelongRow.getCell(cellIndex), false);
                    String fieldNameCell = readCellString(fieldNameRow.getCell(cellIndex), true);/*字段名字*/
                    String fieldTypeCell = readCellString(fieldTypeRow.getCell(cellIndex), false);/*字段类型*/
                    String fieldCommentCell = readCellString(fieldCommentRow.getCell(cellIndex), false);/*字段含义*/
                    CellInfo cellInfo = new CellInfo()
                            .setCellIndex(cellIndex)
                            .setFieldBelong(fieldBelongCell)
                            .setFieldName(fieldNameCell)
                            .setCellType(fieldTypeCell)
                            .setFieldComment(fieldCommentCell);

                    buildFieldType(cellInfo, fieldTypeCell);

                    if (StringsUtil.emptyOrNull(fieldBelongCell)
                            && StringsUtil.emptyOrNull(fieldNameCell)
                            && StringsUtil.emptyOrNull(fieldTypeCell)) {
                        break;
                    }

                    if (StringsUtil.emptyOrNull(fieldNameCell))
                        continue;

                    if (cellInfoMap.put(cellIndex, cellInfo) != null) {
                        throw new RuntimeException("Excel文件不能解析：" + file + ", sheetName=" + sheetName + " 存在重复的字段：" + cellInfo.getFieldName());
                    }
                }

                tableData.cellInfo4IndexMap = Map.copyOf(cellInfoMap);

                final Map<Object, RowData> rows = new LinkedHashMap<>();
                int lastRowNum = sheet.getLastRowNum();
                for (int rowIndex = 5; rowIndex < lastRowNum; rowIndex++) {
                    Row row = sheet.getRow(rowIndex);
                    RowData rowData = new RowData(true);
                    for (int cellIndex = 0; cellIndex < lastCellNum; cellIndex++) {
                        CellInfo cellInfo = tableData.getCellInfo4IndexMap().get(cellIndex);
                        if (cellInfo == null) continue;
                        Cell cell = row.getCell(cellIndex);
                        Object object = readCellValue(tableData, cellIndex, cellInfo, cell);
                        rowData.put(cellInfo.getFieldName(), object);
                    }
                    if (rowData.values().stream().allMatch(v -> v == null || (v instanceof String && StringsUtil.emptyOrNull(String.valueOf(v))))) {
                        continue;
                    }
                    Object object = rowData.getOrDefault("q_id", rowData.get("id"));
                    if (object == null) {
                        throw new RuntimeException("Excel文件不能解析：" + file + ", sheetName=" + sheetName + " 字段内容异常：" + rowIndex);
                    }
                    rows.put(object, rowData);
                }

                tableData.rows = Map.copyOf(rows);

                tableInfoMap.put(tableData.getTableName(), tableData);
            }
        } catch (Throwable throwable) {
            throw Throw.of(file.getPath(), throwable);
        }
    }

    /**
     * 读取单元格
     *
     * @param tableData   表格信息
     * @param rowNumber   所在行
     * @param entityField 单元格
     * @param cellData    单元格数据
     * @return
     * @author: wxd-gaming(無心道, 15388152619)
     * @version: 2024-08-10 10:02
     */
    private Object readCellValue(TableData tableData, int rowNumber, CellInfo entityField, Cell cellData) {
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
                                String[] split = trim.split(String_Split[1]);
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
                                String[] split0 = trim.split(String_Split[0]);
                                arrays = new byte[split0.length][];
                                for (int i0 = 0; i0 < split0.length; i0++) {
                                    String[] split1 = split0[i0].split(String_Split[1]);
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
                                String[] split = trim.split(String_Split[1]);
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
                                String[] split0 = trim.split(String_Split[0]);
                                arrays = new int[split0.length][];
                                for (int i0 = 0; i0 < split0.length; i0++) {
                                    String[] split1 = split0[i0].split(String_Split[1]);
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
                                String[] split = trim.split(String_Split[1]);
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
                                String[] split0 = trim.split(String_Split[0]);
                                arrays = new long[split0.length][];
                                for (int i0 = 0; i0 < split0.length; i0++) {
                                    String[] split1 = split0[i0].split(String_Split[1]);
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
                                String[] split = trim.split(String_Split[1]);
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
                                String[] split0 = trim.split(String_Split[0]);
                                arrays = new float[split0.length][];
                                for (int i0 = 0; i0 < split0.length; i0++) {
                                    String[] split1 = split0[i0].split(String_Split[1]);
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
                                arrays = trim.split(String_Split[1]);
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
                                String[] split0 = trim.split(String_Split[0]);
                                arrays = new String[split0.length][];
                                for (int i = 0; i < split0.length; i++) {
                                    arrays[i] = split0[i].split(String_Split[1]);
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
                            + "\n文件：" + tableData.getTableComment()
                            + ";\nsheet：" + tableData.getTableName()
                            + ";\n列：" + entityField.getFieldName()
                            + ";\n行：" + rowNumber
                            + ";\n数据类型：" + entityField.getFieldTypeString().toLowerCase()
                            + ";\n数据：" + trim + "----无法转换");
            runtimeException.setStackTrace(ex.getStackTrace());
            throw runtimeException;
        }
    }

    private boolean notNullOrEmpty(String source) {
        if (StringsUtil.notEmptyOrNull(source)) {
            return !"#null".equalsIgnoreCase(source);
        }
        return false;
    }

    /**
     * 获取一列的字符
     *
     * @param data 单元格
     */
    private String readCellString(Cell data, boolean isColumnName) {
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
            trim = trim
                    .replace("class", "clazz")
                    .replace("-", "_");
            if (isColumnName) {
                trim = StringsUtil.lowerFirst(trim);
            }
        }
        return trim.trim();
    }

    private void buildFieldType(CellInfo entityField, String fieldTypeName) {
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
    private static String typeString(String source) {
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
