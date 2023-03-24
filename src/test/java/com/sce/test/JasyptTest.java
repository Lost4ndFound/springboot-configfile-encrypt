package com.sce.test;

import org.jasypt.encryption.StringEncryptor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author: lsw
 * @date: 2023/3/24 11:03
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class JasyptTest {

    @Autowired
    @Qualifier("customEnc")
    private StringEncryptor stringEncryptor;

    @Autowired
    private DataSource dataSource;

    /**
     * 加密解密测试
     */
    @Test
    public void jasyptTest(){
        // 加密
        String encrypt = stringEncryptor.encrypt("123456");
        System.out.println(encrypt);
        // 解密
        System.out.println(stringEncryptor.decrypt(encrypt));
    }

    /**
     * 测试加密之后的数据源使用是否正常
     *  查看是否能正常获取链接
     */
    @Test
    public void testDatasource() throws SQLException {
        Connection connection = dataSource.getConnection();
        System.out.println(connection); // HikariProxyConnection@109987815 wrapping com.mysql.cj.jdbc.ConnectionImpl@1c00d406
        connection.close();
    }

}
