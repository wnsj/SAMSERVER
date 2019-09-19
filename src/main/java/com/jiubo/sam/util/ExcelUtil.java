package com.jiubo.sam.util;

import com.alibaba.fastjson.JSONObject;
import com.jiubo.sam.common.Constant;
import com.jiubo.sam.exception.MessageException;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;


/**
 * Created by Administrator on 2018-06-29.
 * <p>
 * 导出Excel
 */
public class ExcelUtil {

    public static Logger log = LoggerFactory.getLogger(ExcelUtil.class);

    //显示的导出表的标题
    private String title;
    //导出表的列名
    private String[] rowName;
    //数据集
    private List<Object[]> dataList = new ArrayList<Object[]>();

    //构造方法，传入要导出的数据
    public ExcelUtil(String title, String[] rowName, List<Object[]> dataList) {
        this.dataList = dataList;
        this.rowName = rowName;
        this.title = title;
    }

    /*
     * 导出数据
     * */
    public JSONObject export(Map<String, Object> parm) throws Exception {
        JSONObject result = new JSONObject();
        try {
            HSSFWorkbook workbook = new HSSFWorkbook();                        // 创建工作簿对象
            HSSFSheet sheet = workbook.createSheet(title);                     // 创建工作表

            // 产生表格标题行
            HSSFRow rowm = sheet.createRow(0);
            HSSFCell cellTiltle = rowm.createCell(0);


            rowm.setHeight((short) (25 * 30)); //设置高度

            //sheet样式定义【getColumnTopStyle()/getStyle()均为自定义方法 - 在下面  - 可扩展】
            HSSFCellStyle columnTopStyle = getColumnTopStyle(workbook);//获取列头样式对象
            HSSFCellStyle style = getStyle(workbook);                    //单元格样式对象


            //设置表格标题   合并单元格
            sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, (rowName.length - 1)));

            cellTiltle.setCellStyle(columnTopStyle);
            cellTiltle.setCellValue(title);


            // 定义所需列数
            int columnNum = rowName.length;
            HSSFRow rowRowName = sheet.createRow(2);                // 在索引2的位置创建行(最顶端的行开始的第二行)

            rowRowName.setHeight((short) (25 * 30)); //设置高度

            // 将列头设置到sheet的单元格中
            for (int n = 0; n < columnNum; n++) {
                HSSFCell cellRowName = rowRowName.createCell(n);                //创建列头对应个数的单元格
                cellRowName.setCellType(HSSFCell.CELL_TYPE_STRING);                //设置列头单元格的数据类型
                HSSFRichTextString text = new HSSFRichTextString(rowName[n]);
                cellRowName.setCellValue(text);                                    //设置列头单元格的值
                cellRowName.setCellStyle(columnTopStyle);                        //设置列头单元格样式
            }

            //将查询出的数据设置到sheet对应的单元格中
            for (int i = 0; i < dataList.size(); i++) {

                Object[] obj = dataList.get(i);//遍历每个对象
                HSSFRow row = sheet.createRow(i + 3);//创建所需的行数

                row.setHeight((short) (25 * 20)); //设置高度

                for (int j = 0; j < obj.length; j++) {
                    HSSFCell cell = null;   //设置单元格的数据类型
                    if (j == 0) {
                        cell = row.createCell(j, HSSFCell.CELL_TYPE_NUMERIC);
                        cell.setCellValue(i + 1);
                    } else {
                        cell = row.createCell(j, HSSFCell.CELL_TYPE_STRING);
                        if (!"".equals(obj[j]) && obj[j] != null) {
                            cell.setCellValue(obj[j].toString());                        //设置单元格的值
                        }
                    }
                    cell.setCellStyle(style);                                    //设置单元格样式
                }
            }
            //让列宽随着导出的列长自动适应
            for (int colNum = 0; colNum < columnNum; colNum++) {
                int columnWidth = sheet.getColumnWidth(colNum) / 256;
                for (int rowNum = 0; rowNum < sheet.getLastRowNum(); rowNum++) {
                    HSSFRow currentRow;
                    //当前行未被使用过
                    if (sheet.getRow(rowNum) == null) {
                        currentRow = sheet.createRow(rowNum);
                    } else {
                        currentRow = sheet.getRow(rowNum);
                    }
                    if (currentRow.getCell(colNum) != null) {
                        HSSFCell currentCell = currentRow.getCell(colNum);
                        if (currentCell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
                            try {
                                int length = currentCell.getStringCellValue().getBytes().length;
                                if (columnWidth < length) {
                                    columnWidth = length;
                                }
                            } catch (Exception e) {

                            }
                        }
                    }
                }
                if (colNum == 0) {
                    sheet.setColumnWidth(colNum, (columnWidth - 2) * 128);
                } else {
                    sheet.setColumnWidth(colNum, (columnWidth + 4) * 256);
                }
            }

            if (workbook != null) {
                try {
                    String out_path = parm.get("path").toString() + ".xls";

                    File file = new File(Constant.FILE_PARAM.EXCEL_FILE_PATH);
                    if (!file.exists()) {
                        file.mkdirs();
                    }

                    FileOutputStream out = new FileOutputStream(Constant.FILE_PARAM.EXCEL_FILE_PATH + out_path);
                    workbook.write(out);

                    result.put("path", Constant.FILE_PARAM.EXCEL_FILE_PATH + out_path);
                    result.put("code", 0);
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    result.put("code", 1);
                    result.put("msg", e.getMessage());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            result.put("code", 1);
            result.put("msg", e.getMessage());
        }
        return result;

    }

    public JSONObject export2(Map<String, Object> parm) throws Exception {
        JSONObject result = new JSONObject();
        try {
            HSSFWorkbook workbook = new HSSFWorkbook();                        // 创建工作簿对象
            HSSFSheet sheet = workbook.createSheet(title);                     // 创建工作表

            //sheet样式定义【getColumnTopStyle()/getStyle()均为自定义方法 - 在下面  - 可扩展】
            HSSFCellStyle columnTopStyle = getColumnTopStyle(workbook);//获取列头样式对象
            HSSFCellStyle style = getStyle(workbook);                    //单元格样式对象


            for (int i = 0; i < 3; i++) {
                // 产生表格标题行
                HSSFRow rowm = sheet.createRow(i);
                HSSFCell cellTiltle = rowm.createCell(i);

                rowm.setHeight((short) (25 * 30)); //设置高度
                //设置表格标题   合并单元格
                sheet.addMergedRegion(new CellRangeAddress(i, i, 0, i + 1));

                cellTiltle.setCellStyle(columnTopStyle);
                cellTiltle.setCellValue("a" + i);

            }

            // 定义所需列数
            int columnNum = rowName.length;
            HSSFRow rowRowName = sheet.createRow(4);                // 在索引2的位置创建行(最顶端的行开始的第二行)

            rowRowName.setHeight((short) (25 * 30)); //设置高度

            // 将列头设置到sheet的单元格中
            for (int n = 0; n < columnNum; n++) {
                HSSFCell cellRowName = rowRowName.createCell(n);                //创建列头对应个数的单元格
                cellRowName.setCellType(HSSFCell.CELL_TYPE_STRING);                //设置列头单元格的数据类型
                HSSFRichTextString text = new HSSFRichTextString(rowName[n]);
                cellRowName.setCellValue(text);                                    //设置列头单元格的值
                cellRowName.setCellStyle(columnTopStyle);                        //设置列头单元格样式
            }

            //将查询出的数据设置到sheet对应的单元格中
            for (int i = 0; i < dataList.size(); i++) {

                Object[] obj = dataList.get(i);//遍历每个对象
                HSSFRow row = sheet.createRow(i + 3);//创建所需的行数

                row.setHeight((short) (25 * 20)); //设置高度

                for (int j = 0; j < obj.length; j++) {
                    HSSFCell cell = null;   //设置单元格的数据类型
                    if (j == 0) {
                        cell = row.createCell(j, HSSFCell.CELL_TYPE_NUMERIC);
                        cell.setCellValue(i + 1);
                    } else {
                        cell = row.createCell(j, HSSFCell.CELL_TYPE_STRING);
                        if (!"".equals(obj[j]) && obj[j] != null) {
                            cell.setCellValue(obj[j].toString());                        //设置单元格的值
                        }
                    }
                    cell.setCellStyle(style);                                    //设置单元格样式
                }
            }
            //让列宽随着导出的列长自动适应
            for (int colNum = 0; colNum < columnNum; colNum++) {
                int columnWidth = sheet.getColumnWidth(colNum) / 256;
                for (int rowNum = 0; rowNum < sheet.getLastRowNum(); rowNum++) {
                    HSSFRow currentRow;
                    //当前行未被使用过
                    if (sheet.getRow(rowNum) == null) {
                        currentRow = sheet.createRow(rowNum);
                    } else {
                        currentRow = sheet.getRow(rowNum);
                    }
                    if (currentRow.getCell(colNum) != null) {
                        HSSFCell currentCell = currentRow.getCell(colNum);
                        if (currentCell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
                            try {
                                int length = currentCell.getStringCellValue().getBytes().length;
                                if (columnWidth < length) {
                                    columnWidth = length;
                                }
                            } catch (Exception e) {

                            }
                        }
                    }
                }
                if (colNum == 0) {
                    sheet.setColumnWidth(colNum, (columnWidth - 2) * 128);
                } else {
                    sheet.setColumnWidth(colNum, (columnWidth + 4) * 256);
                }
            }

            if (workbook != null) {
                try {
                    String out_path = parm.get("path").toString() + ".xls";

                    File file = new File(Constant.FILE_PARAM.EXCEL_FILE_PATH);
                    if (!file.exists()) {
                        file.mkdirs();
                    }

                    FileOutputStream out = new FileOutputStream(Constant.FILE_PARAM.EXCEL_FILE_PATH + out_path);
                    workbook.write(out);

                    result.put("path", Constant.FILE_PARAM.EXCEL_FILE_PATH + out_path);
                    result.put("code", 0);
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    result.put("code", 1);
                    result.put("msg", e.getMessage());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            result.put("code", 1);
            result.put("msg", e.getMessage());
        }
        return result;

    }


    /*
     * 列头单元格样式
     */
    public static HSSFCellStyle getColumnTopStyle(HSSFWorkbook workbook) {

        // 设置字体
        HSSFFont font = workbook.createFont();
        //设置字体大小
        font.setFontHeightInPoints((short) 11);
        //字体加粗
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        //设置字体名字
        //font.setFontName("Courier New");
        font.setFontName("微软雅黑");
        //设置样式;
        HSSFCellStyle style = workbook.createCellStyle();
        //设置底边框;
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        //设置底边框颜色;
        style.setBottomBorderColor(HSSFColor.BLACK.index);
        //设置左边框;
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        //设置左边框颜色;
        style.setLeftBorderColor(HSSFColor.BLACK.index);
        //设置右边框;
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        //设置右边框颜色;
        style.setRightBorderColor(HSSFColor.BLACK.index);
        //设置顶边框;
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        //设置顶边框颜色;
        style.setTopBorderColor(HSSFColor.BLACK.index);
        //在样式用应用设置的字体;
        style.setFont(font);
        //设置自动换行;
        style.setWrapText(false);
        //设置水平对齐的样式为居中对齐;
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        //设置垂直对齐的样式为居中对齐;
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

        //设置单元格背景颜色
        style.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        return style;

    }

    /*
     * 列数据信息单元格样式
     */
    public static HSSFCellStyle getStyle(HSSFWorkbook workbook) {
        // 设置字体
        HSSFFont font = workbook.createFont();
        //设置字体大小
        //font.setFontHeightInPoints((short)10);
        //字体加粗
        //font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        //设置字体名字
        font.setFontName("Courier New");
        //设置样式;
        HSSFCellStyle style = workbook.createCellStyle();
        //设置底边框;
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        //设置底边框颜色;
        style.setBottomBorderColor(HSSFColor.BLACK.index);
        //设置左边框;
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        //设置左边框颜色;
        style.setLeftBorderColor(HSSFColor.BLACK.index);
        //设置右边框;
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        //设置右边框颜色;
        style.setRightBorderColor(HSSFColor.BLACK.index);
        //设置顶边框;
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        //设置顶边框颜色;
        style.setTopBorderColor(HSSFColor.BLACK.index);
        //在样式用应用设置的字体;
        style.setFont(font);
        //设置自动换行;
        style.setWrapText(false);
        //设置水平对齐的样式为居中对齐;
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        //设置垂直对齐的样式为居中对齐;
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

        return style;

    }

    public static void CreateExcel() throws Exception {
        String[] rowsName = {"列1", "列2", "列3"};

        List<Object[]> dataList = new ArrayList<Object[]>();

        Object[] objs = null;
        for (int i = 0; i < 10; i++) {
            objs = new Object[rowsName.length];
            objs[0] = i;
            //时间
            objs[1] = TimeUtil.getDateYYYY_MM_DD_HH_MM_SS_SSS(new Date());
            String s = "";
            for (int j = 0; j < new Random().nextInt(50) + 1; j++) {
                s += String.valueOf(new Random().nextInt(10) + 1);
            }
            objs[2] = s;
            dataList.add(objs);
        }

        ExcelUtil e = new ExcelUtil("表格标题", rowsName, dataList);

        Map<String, Object> parm = new HashMap<String, Object>();
        parm.put("path", TimeUtil.getDateYYYYMMDDHHMMSSSSS(new Date()));
        System.out.println(e.export(parm));
    }


    /**
     * @throws Exception
     */
    public static Map<Object, Object> updateExcel(String fileName, MultipartFile file, boolean existColumn) {
        Map<Object, Object> dataMap = new TreeMap<Object, Object>();
        InputStream fileInputStream = null;
        try {
            boolean notNull = false;
            if (!fileName.matches("^.+\\.(?i)(xls)$") && !fileName.matches("^.+\\.(?i)(xlsx)$")) {
                throw new MessageException("上传文件格式不正确");
            }
            boolean isExcel2003 = true;
            if (fileName.matches("^.+\\.(?i)(xlsx)$")) {
                isExcel2003 = false;
            }
            fileInputStream = file.getInputStream();
            Workbook wb = null;
            if (isExcel2003) {
                wb = new HSSFWorkbook(fileInputStream);
            } else {
                wb = new XSSFWorkbook(fileInputStream);
            }
            Sheet sheet = wb.getSheetAt(0);
            if (sheet != null) {
                notNull = true;
            }
            int an = 0;
            if (existColumn) an = 1;
            //当前列数
            int Column = 1;
            List<Object> dataList = null;
            for (int i = an; i < sheet.getPhysicalNumberOfRows(); i++) {
                org.apache.poi.ss.usermodel.Row row = sheet.getRow(i);
                dataList = new ArrayList<Object>();
                if(row == null)continue;
                for (int j = 0; j < row.getLastCellNum(); j++) {
                    org.apache.poi.ss.usermodel.Cell cell = row.getCell(j);
                    Object value = null;

                    if (cell != null) {
                        if (cell.getCellType() == org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING) {
                            value = cell.getRichStringCellValue().getString();
                        } else if (cell.getCellType() == org.apache.poi.ss.usermodel.Cell.CELL_TYPE_BOOLEAN) {
                            value = cell.getBooleanCellValue();
                        }else if (cell.getCellType() == org.apache.poi.ss.usermodel.Cell.CELL_TYPE_NUMERIC) {
                            if (DateUtil.isCellDateFormatted(cell)) {
                                value = cell.getDateCellValue();
                            } else {
                                value = cell.getNumericCellValue();
                            }
                        }
                    }
                    dataList.add(value);
                }
                Column++;
                if (null != dataList && dataList.size() > 0) {
                    dataMap.put(Column, dataList);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != fileInputStream) {
                    fileInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                return dataMap;
            }
        }
    }


    /**
     * @param path        文件路径
     * @param existColumn 是否有列名
     * @throws Exception
     */
    public static Map<Object, Object> readExcel(String path, boolean existColumn) {
        File excelFile = new File(path);
        Map<Object, Object> dataMap = new TreeMap<Object, Object>();
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(excelFile);
            HSSFWorkbook wb = new HSSFWorkbook(fileInputStream);
            HSSFSheet sheet = wb.getSheetAt(0);
            //如果是有标题就从第二个开始取
            int an = 0;
            if (existColumn) an = 1;
            //当前列数
            int Column = 1;
            List<Object> dataList = null;
            for (int i = an; i < sheet.getPhysicalNumberOfRows(); i++) {
                org.apache.poi.ss.usermodel.Row row = sheet.getRow(i);
                dataList = new ArrayList<Object>();
                for (int j = 0; j < row.getPhysicalNumberOfCells(); j++) {
                    org.apache.poi.ss.usermodel.Cell cell = row.getCell(j);
                    Object value = null;
                    /**
                     * CellType

                     说明

                     CELL_TYPE_BLANK

                     空值

                     CELL_TYPE_BOOLEAN

                     布尔型

                     CELL_TYPE_ERROR

                     错误

                     CELL_TYPE_FORMULA

                     公式型

                     CELL_TYPE_STRING

                     字符串型

                     CELL_TYPE_NUMERIC

                     数值型
                     */
                    if (cell.getCellType() == org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING) {
                        value = cell.getRichStringCellValue().getString();
                    } else if (cell.getCellType() == org.apache.poi.ss.usermodel.Cell.CELL_TYPE_NUMERIC) {
                        if (DateUtil.isCellDateFormatted(cell)) {
                            value = String.valueOf(cell.getDateCellValue());
                        } else {
                            value = cell.getNumericCellValue();
                        }
                    } else if (cell.getCellType() == org.apache.poi.ss.usermodel.Cell.CELL_TYPE_BOOLEAN) {
                        value = cell.getBooleanCellValue();
                    }
                    dataList.add(value);
                }
                Column++;
                //列 : 值
                if (null != dataList && dataList.size() > 0) {
                    dataMap.put(Column, dataList);
                }
            }


//		//读取图片
//		List<HSSFPictureData> pictures = wb.getAllPictures();
//		if(null!=pictures&&pictures.size()>0){
//			for (HSSFShape shape : sheet.getDrawingPatriarch().getChildren()) {
//				if (shape instanceof HSSFPicture) {
//					HSSFPicture pic = (HSSFPicture) shape;
//					int pictureIndex = pic.getPictureIndex()-1;
//					HSSFPictureData picData = pictures.get(pictureIndex);
//					System.out.println("image-size:" + picData.getData().length);
//				}
//			}
//		}
//		System.out.println("sheetName:"+wb.getSheetName(0));


        } catch (Exception e) {

        } finally {
            try {
                if (null != fileInputStream) {
                    fileInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                return dataMap;
            }
        }
    }

    public static Map<Object, Object> readExcel2(String path, boolean existColumn) {
        File excelFile = new File(path);
        Map<Object, Object> dataMap = new TreeMap<Object, Object>();
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(excelFile);
            HSSFWorkbook wb = new HSSFWorkbook(fileInputStream);
            HSSFSheet sheet = wb.getSheetAt(0);
            //如果是有标题就从第二个开始取
            int an = 0;
            if (existColumn) an = 1;
            //当前列数
            int Column = 1;
            List<Object> dataList = null;
            for (int i = an; i < sheet.getPhysicalNumberOfRows(); i++) {
                org.apache.poi.ss.usermodel.Row row = sheet.getRow(i);
                dataList = new ArrayList<Object>();
                for (int j = 0; j < row.getPhysicalNumberOfCells(); j++) {
                    org.apache.poi.ss.usermodel.Cell cell = row.getCell(j);
                    Object value = null;
                    if (cell.getCellType() == org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING) {
                        value = cell.getRichStringCellValue().getString();
                    } else if (cell.getCellType() == org.apache.poi.ss.usermodel.Cell.CELL_TYPE_NUMERIC) {
                        if (DateUtil.isCellDateFormatted(cell)) {
                            value = String.valueOf(cell.getDateCellValue());
                        } else {
                            value = cell.getNumericCellValue();
                        }
                    } else if (cell.getCellType() == org.apache.poi.ss.usermodel.Cell.CELL_TYPE_BOOLEAN) {
                        value = cell.getBooleanCellValue();
                    }
                    dataList.add(value);
                }
                Column++;
                //列 : 值
                if (null != dataList && dataList.size() > 0) {
                    dataMap.put(Column, dataList);
                }
            }
        } catch (Exception e) {

        } finally {
            try {
                if (null != fileInputStream) {
                    fileInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                return dataMap;
            }
        }
    }


//    public static void main(String[] args) throws Exception {
//        //CreateExcel();
//        //System.out.println(readExcel("d:\\2.xls",true));
//
//        List<Object> l = new ArrayList();
//        l.add("1.0, 2018-01-22 12:23:11, 商户, SX-21, 6236681544444440000, 1.0, 10000.0");
//        l.add("2.0, 2018-01-22 12:23:12, 测试商户, SX-21, 6236681544444440001, 2.0, 10001.0");
//
//        List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
//        for (Object o : l) {
//            Map<String, Object> dataMap = new TreeMap<String, Object>();
//            dataMap.put("createDate", o.toString().split(",")[1]);
//            dataMap.put("merName", o.toString().split(",")[2]);
//            dataMap.put("sBooth", o.toString().split(",")[3]);
//            dataMap.put("bankId", o.toString().split(",")[4]);
//            dataMap.put("poundageAmt", o.toString().split(",")[5]);
//            dataMap.put("payAmt", o.toString().split(",")[6]);
//            dataList.add(dataMap);
//        }
//        JSONArray jsonArray = JSONArray.parseArray(JSON.toJSONString(dataList));
//        System.out.println(jsonArray);
//    }
}