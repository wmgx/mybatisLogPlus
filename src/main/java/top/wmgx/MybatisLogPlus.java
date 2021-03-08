package top.wmgx;

/**
 * @author wmgx
 * @create 2021-02-14-0:47
 **/


import cn.hutool.db.meta.Table;
import cn.hutool.db.sql.SqlFormatter;
import org.apache.ibatis.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author wmgx
 * @create 2021-02-13-23:06
 **/
@Configuration
public class MybatisLogPlus implements Log {
    private Logger logger;
    private String sql = "";

    /**
     * 是否输出结果
     */
    private static boolean enableShowResult;

    private final Table table = new Table();
    /**
     * 每列最大宽度，超过会被截断
     */
    private static Integer maxWidth;

    /**
     * 前n行
     */
    private static Integer topLine;

    /**
     * 前n列
     */
    private static Integer topColumn;


    public MybatisLogPlus(String s) {
        logger = LoggerFactory.getLogger(s);
    }
    public MybatisLogPlus() {
    }
    @Value("${top.wmgx.mybatisLogPlus.enableShowResult:false}")
    public  void setEnableShowResult(boolean enableShowResult) {
        MybatisLogPlus.enableShowResult = enableShowResult;
    }

    @Value("${top.wmgx.mybatisLogPlus.maxWidth:20}")
    public  void setMaxWidth(Integer maxWidth) {
        MybatisLogPlus.maxWidth = maxWidth;
    }

    @Value("${top.wmgx.mybatisLogPlus.topLine:30}")
    public  void setTopLine(Integer topLine) {
        MybatisLogPlus.topLine = topLine;
    }

    @Value("${top.wmgx.mybatisLogPlus.topColumn:9}")
    public  void setTopColumn(Integer topColumn) {
        MybatisLogPlus.topColumn = topColumn;
    }

    @Override
    public boolean isDebugEnabled() {
        return true;
    }

    @Override
    public boolean isTraceEnabled() {
        return true;
    }

    @Override
    public void error(String s, Throwable throwable) {
        logger.error(s, throwable);
    }

    @Override
    public void error(String s) {
        logger.error(s);
    }

    /**
     * debug 级别信息太多，Info级别信息差不多 调用的Info的输出
     *
     * @param s 输出结果
     */
    @Override
    public void debug(String s) {
        if (s.contains("Preparing: ")) {
            sql = s.split("Preparing: ")[1];
            return;
        }
        if (s.contains("Parameters: ")) {
            // ==> Parameters:  固定开头 16位
            s = s.split("Parameters: ")[1];
            for (String t : s.split("\\), ")) {
                // 用null开头
                if (t.startsWith("null")) {
                    for (String s1 : t.split(", ")) {
                        sql = sql.replaceFirst("\\?", "null");
                    }

                    continue;
                }

                if (t.contains("(")) {
                    sql = sql.replaceFirst("\\?",
                            "'" + Matcher.quoteReplacement(t.substring(0, t.indexOf("(")) + "'"));
                }
            }
            return;
        }
        if (s.contains("<==      Total:") || s.contains("<==    Updates:")) {
            logger.info("==> SQL statement: \n" + SqlFormatter.format(sql) + "\n" + s);
            if (enableShowResult && s.contains("<==      Total:")) {
                if(table.content.size()!=0){
                    logger.info(table.format());
                    table.clear();
                }
            }
            return;
        }
        logger.info(s);
    }

    @Override
    public void trace(String s) {
        if (!enableShowResult)
            return;
        if (table.content.size() > topLine)
            return;

        if (s.startsWith("<==    Columns: ")) {
            table.clear();
            List<String> list = new ArrayList<>(Arrays.asList(s.substring(16).split(", ")));
// 误以为是列号
//            if (!(list.get(0).startsWith("count") && list.size() == 1))
//                list.add(0, "row_index");
            table.addLine(list);
        }

        if (s.startsWith("<==        Row: "))
            table.addLine(new ArrayList<>(Arrays.asList(s.substring(16).split(", "))));


    }

    @Override
    public void warn(String s) {
        logger.warn(s);
    }


    private static class  Table {
        /**
         * 表格内容（含表头）
         */
        private final List<List<String>> content = new ArrayList<>();


        private List<Integer> getMaxWidthList() {
            return Arrays.stream(transpose())
                    .map(rows -> Arrays.stream(rows)
                            .mapToInt(s -> {
                                //sql查询结果如果为null，则认为长度为4
                                if (s == null) {
                                    return 4;
                                } else {
                                    //加上双字节字符出现的次数，最短为null，四个字符
                                    return s.length() + getZHCharCount(s);
                                }
                            }).max().orElse(0)
                    ).collect(Collectors.toList());
        }


        public void addLine(List<String> list) {
            content.add(list.stream().limit(topColumn + 1)
                    .map(cell -> cell == null ? null : cell.replaceAll("\t", " "))
                    .map(cell -> cell != null && cell.length() > maxWidth ? cell.substring(0, maxWidth) : cell)
                    .collect(Collectors.toList()));
        }

        public void clear() {
            this.content.clear();
        }


        public List<List<String>> getContent() {
            return content;
        }

        //获取表格行数
        public int getRowCount() {
            return content.size();
        }

        //获取表格列数，0行代表表头，默认认为content中至少含有表头
        int getColCount() {
            return content.get(0).size();
        }

        /**
         * 转置二维数组
         *
         * @return 转置后的结果
         */
        private String[][] transpose() {
            int rowCount = getRowCount();
            int colCount = getColCount();
            String[][] result = new String[colCount][rowCount];

            for (int i = 0; i < rowCount; i++) {
                for (int j = 0; j < colCount; j++) {
                    result[j][i] = content.get(i).get(j);
                }
            }
            return result;
        }

        public String format() {

            List<Integer> originMaxWidthList = getMaxWidthList();
            Integer tc = originMaxWidthList.stream().reduce(Integer::sum).get() + getColCount() * 3 + 1;
            StringBuilder stringBuilder = new StringBuilder();
            //获取原表每列最大宽度
            stringBuilder.append("查询结果如下：\n");
            stringBuilder.append(getRepeatChar("-", tc));

            //遍历原table，将每个单元格填充到该列最大长度
            content.stream().forEach(row -> {

                stringBuilder.append("\n");
                int j = 0;
                for (String cell : row) {
                    int cellSize =
                            originMaxWidthList.get(j) + 3 - getZHCharCount(cell);
                    cellSize = j == 0 ? cellSize + 1 : cellSize;
                    stringBuilder.append(getPadString(cell, cellSize, "|", j));
                    j++;
                }

                stringBuilder.append("\n");
                stringBuilder.append(getRepeatChar("-", tc));

            });

            return stringBuilder.toString();
        }


    }

    /**
     * 将str重复count次，返回结果
     *
     * @param str
     * @param count
     *
     * @return
     */
    public static String getRepeatChar(String str, int count) {
        StringBuilder res = new StringBuilder();
        IntStream.range(0, count).forEach(i -> res.append(str));
        return res.toString();
    }

    /**
     * 此方法主要为表格的单元格数据按照指定长度填充并居中对齐并带上分割符号
     *
     * @param str
     *         原始字符串
     * @param len
     *         输出字符串的总长度
     * @param symbol
     *         分割符号
     * @param index
     *         传入的cell在list的索引，如果为第一个则需要在前面增加分割符号
     *
     * @return
     */
    public static String getPadString(String str, Integer len, String symbol, int index) {
        int l = len;

        if (index == 0) {
            l -= 2;
        } else {
            l -= 1;
        }

        String tmp;
        StringBuilder res = new StringBuilder();
        if (str.length() < l) {
            int fixLen = (l - str.length()) / 2;
            String fix = getRepeatChar(" ", fixLen);
            res.append(fix).append(str).append(fix);
            if (res.length() > l) {
                tmp = res.substring(0, l);
            } else {
                res.append(getRepeatChar(" ", l - res.length()));
                tmp = res.toString();
            }
        } else {
            tmp = str.substring(0, l);
        }
        if (index == 0) {
            return symbol + tmp + symbol;
        } else {
            return tmp + symbol;
        }
    }


    /**
     * 得到一个字符串中单字节出现的次数
     *
     * @param cell
     *
     * @return
     */
    public static Integer getENCharCount(String cell) {
        if (cell == null) {
            return 0;
        }
        String reg = "[^\t\\x00-\\xff]";
//        String reg = "|[^\t\\x00-\\xff]";
        return cell.replaceAll(reg, "").length();
    }


    /**
     * 得到一个字符串中双字节出现的次数
     *
     * @param cell
     *
     * @return
     */
    public static Integer getZHCharCount(String cell) {
        if (cell == null) {
            return 0;
        }
        return cell.length() - getENCharCount(cell);
    }
}
