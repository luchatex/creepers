package com.fosun.fc.projects.creepers.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 
 * 描述： 创建人：Administrator 创建时间：2016年12月22日 上午11:09:13
 * 
 */
public class TestBatchInsert {
    private static int COUNT = 10;
    private static int BATCH_SIZE = 1000;

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        long start = System.currentTimeMillis();
        Class.forName("oracle.jdbc.driver.OracleDriver");
        Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@10.166.10.215:1521:itgfortune", "creepers_itg",
                "creepers_itg");
        conn.setAutoCommit(false);
        PreparedStatement pstmt = conn.prepareStatement(
                "insert into t_creepers_task_list (id,url,task_type,http_type,param_map,memo,flag,version,created_by,created_dt,updated_by,updated_dt) values (seq_creepers_task_list.nextval,?,'medical_GSP_list','post','','','0',0,'admin',sysdate,'admin',sysdate)");
        for (int i = 1; i <= COUNT; i++) {
            pstmt.setInt(1, i);
            String url = "http://app1.sfda.gov.cn/datasearch/face3/content.jsp?tableId=23&tableName=TABLE23&tableView=GMP%E8%AE%A4%E8%AF%81&Id="
                    + i;
            pstmt.setString(1, url);
            pstmt.addBatch();
            if (i % BATCH_SIZE == 0) {
                pstmt.executeBatch();
                System.out.println(i);
            }
        }
        pstmt.executeBatch();
        conn.commit();
        pstmt.close();
        conn.close();
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }
}
