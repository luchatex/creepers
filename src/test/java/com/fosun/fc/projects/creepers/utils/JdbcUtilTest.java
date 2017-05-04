/**
 * 
 */
package com.fosun.fc.projects.creepers.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.fosun.fc.projects.creepers.utils.PropertiesUtil;

/**
 * 
 * 描述： 创建人：pengyk 创建时间：2016年12月27日 上午11:09:13
 * 
 */
public class JdbcUtilTest {
    private static String JDBC_URL = PropertiesUtil.getApplicationValue("jdbc.url");
    private static String JDBC_USERNAME = PropertiesUtil.getApplicationValue("jdbc.username");
    private static String JDBC_PASSWORD = PropertiesUtil.getApplicationValue("jdbc.password");
    private static String JDBC_DRIVER = PropertiesUtil.getApplicationValue("jdbc.driver");

    public static void execute(String sql) throws ClassNotFoundException,SQLException {
        Class.forName(JDBC_DRIVER);
        Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USERNAME, JDBC_PASSWORD);
        conn.setAutoCommit(false);
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.execute();
        conn.commit();
        pstmt.close();
        conn.close();
    }
    
    public static void main(String args[]) throws ClassNotFoundException, SQLException{
        execute("insert into t_creepers_task_list(id,task_type,url,http_type,param_map,memo,flag,version,created_by,created_dt,updated_by,updated_dt)values(seq_creepers_list.nextval,'dishonest_baidu_list_by_name_list','https://sp0.baidu.com/8aQDcjqpAAV3otqbppnN2DJv/api.php?resource_id=6899&query=%E5%A4%B1%E4%BF%A1%E8%A2%AB%E6%89%A7%E8%A1%8C%E4%BA%BA%E5%90%8D%E5%8D%95&cardNum=&areaName=&ie=utf-8&oe=utf-8&format=jso&iname=深圳菲尼柯思标准市场研究有限公司','get','','null','0','0','admin',sysdate,'admin',sysdate)");
        System.err.println("ok");
    }
}
