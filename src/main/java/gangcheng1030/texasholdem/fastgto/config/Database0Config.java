package gangcheng1030.texasholdem.fastgto.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@ConfigurationProperties(prefix = "database0")
@Component
public class Database0Config {
    private String url;
    private String username;
    private String password;
    private String driverClassName;
    private String databaseName;

    public DataSource createDataSource() {
        DruidDataSource result = new DruidDataSource();
        result.setDriverClassName(getDriverClassName());
        result.setUrl(getUrl());
        result.setUsername(getUsername());
        result.setPassword(getPassword());

        // 设置初始连接数和最大连接数
        result.setInitialSize(8);
        result.setMaxActive(16);

        // 配置获取连接等待超时的时间
        result.setMaxWait(60000);

        // 配置间隔多久进行一次检测，检测需要关闭的空闲连接
        result.setTimeBetweenEvictionRunsMillis(60000);

        // 配置一个连接在池中最小生存的时间
        result.setMinEvictableIdleTimeMillis(300000);

        // 验证连接有效与否的SQL
        result.setValidationQuery("SELECT 1");
        result.setTestWhileIdle(true);
        result.setTestOnBorrow(false);
        result.setTestOnReturn(false);
        return result;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }
}
