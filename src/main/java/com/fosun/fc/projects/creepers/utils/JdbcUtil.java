package com.fosun.fc.projects.creepers.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 描述： 创建人：pengyk 创建时间：2016年12月27日 上午11:09:13
 * 
 */
public class JdbcUtil {
    private static String JDBC_URL = PropertiesUtil.getApplicationValue("jdbc.url");
    private static String JDBC_USERNAME = PropertiesUtil.getApplicationValue("jdbc.username");
    private static String JDBC_PASSWORD = PropertiesUtil.getApplicationValue("jdbc.password");
    private static String JDBC_DRIVER = PropertiesUtil.getApplicationValue("jdbc.driver");
    private final static Logger logger = LoggerFactory.getLogger(JobUtils.class); 
    
    public static void execute(String sql) throws ClassNotFoundException, SQLException {
        logger.info("JdbcUtil.execute SQL======>>> :" + sql);
        Class.forName(JDBC_DRIVER);
        Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USERNAME, JDBC_PASSWORD);
        conn.setAutoCommit(false);
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.execute();
        conn.commit();
        pstmt.close();
        conn.close();
        logger.info("JdbcUtil.execute succeed!");
    }

    public static void execute(List<String> sqlList) throws ClassNotFoundException,SQLException {
        Class.forName(JDBC_DRIVER);
        Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USERNAME, JDBC_PASSWORD);
        conn.setAutoCommit(false);
        Statement stmt = conn.createStatement(); 
        if(sqlList!=null && !sqlList.isEmpty()){
            for (String sql : sqlList) {
                stmt.addBatch(sql);
                stmt.executeBatch(); 
            }
        }
        conn.commit();
        stmt.close();
        conn.close();
    }
}
