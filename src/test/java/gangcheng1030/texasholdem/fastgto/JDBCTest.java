package gangcheng1030.texasholdem.fastgto;

import gangcheng1030.texasholdem.fastgto.config.Database0Config;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCTest {
    @Test
    public void testJDBC() {
        Database0Config database0Config = new Database0Config();
        database0Config.setUrl("jdbc:mysql://192.168.1.7:3306/texas_holdem?serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8");
        database0Config.setDriverClassName("com.mysql.jdbc.Driver");
        database0Config.setUsername("texas");
        database0Config.setPassword("891030");

        DataSource dataSource = database0Config.createDataSource();

        for (int i = 0; i  < 100; i++) {
            System.out.println(i);
            long startTime = System.currentTimeMillis();
            try (Connection connection = dataSource.getConnection()) {
                System.out.printf("耗时: %d \n", System.currentTimeMillis() - startTime);
                try (Statement statement = connection.createStatement()) {
                    System.out.printf("耗时: %d \n", System.currentTimeMillis() - startTime);
                    String sql = "insert into postflop_601 (id, preflop_actions, flop_cards, parent, player) values (12, 'abc', '2c2d2h', 0, 1)";
                    statement.executeUpdate(sql);
                    System.out.printf("update 耗时: %d \n", System.currentTimeMillis() - startTime);
                    statement.executeUpdate("delete from postflop_601");
                    System.out.printf("耗时: %d \n", System.currentTimeMillis() - startTime);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            System.out.printf("耗时: %d \n", System.currentTimeMillis() - startTime);
        }

    }
}
