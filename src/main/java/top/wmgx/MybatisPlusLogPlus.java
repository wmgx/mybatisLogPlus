package top.wmgx;

/**
 * @author wmgx
 * @create 2021-02-14-0:47
 **/

import cn.hutool.db.sql.SqlFormatter;
import org.apache.ibatis.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;

/**
 * @author wmgx
 * @create 2021-02-13-23:06
 **/
public class MybatisPlusLogPlus implements Log {
    Logger logger = LoggerFactory.getLogger(Logger.class);
    String sql = "";

    public MybatisPlusLogPlus(String s) {
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
     * @param s
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
            return;
        }
        logger.info(s);
    }

    @Override
    public void trace(String s) {
        logger.trace(s);
    }

    @Override
    public void warn(String s) {
        logger.warn(s);
    }
}
