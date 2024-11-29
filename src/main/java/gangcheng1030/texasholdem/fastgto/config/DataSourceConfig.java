package gangcheng1030.texasholdem.fastgto.config;

import com.dangdang.ddframe.rdb.sharding.api.ShardingDataSourceFactory;
import com.dangdang.ddframe.rdb.sharding.api.rule.DataSourceRule;
import com.dangdang.ddframe.rdb.sharding.api.rule.ShardingRule;
import com.dangdang.ddframe.rdb.sharding.api.rule.TableRule;
import com.dangdang.ddframe.rdb.sharding.api.strategy.database.DatabaseShardingStrategy;
import com.dangdang.ddframe.rdb.sharding.api.strategy.table.TableShardingStrategy;
import com.dangdang.ddframe.rdb.sharding.keygen.DefaultKeyGenerator;
import com.dangdang.ddframe.rdb.sharding.keygen.KeyGenerator;
import gangcheng1030.texasholdem.fastgto.sharding.DatabaseShardingAlgorithm;
import gangcheng1030.texasholdem.fastgto.sharding.TableShardingAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.*;

@Configuration
public class DataSourceConfig {
    @Autowired
    private Database0Config database0Config;

    @Autowired
    private DatabaseShardingAlgorithm databaseShardingAlgorithm;

    @Autowired
    private TableShardingAlgorithm tableShardingAlgorithm;

    @Bean
    public DataSource getDataSource() throws SQLException {
        Map<String, DataSource> dataSourceMap = new HashMap<>();
        dataSourceMap.put(database0Config.getDatabaseName(), database0Config.createDataSource());
        // 设置默认数据库
        DataSourceRule dataSourceRule = new DataSourceRule(dataSourceMap, database0Config.getDatabaseName());

        List<String> tableNames = new ArrayList<>();
        for (int i = 0; i < TableShardingAlgorithm.ShardingTableNum; i++) {
            tableNames.add("postflop_" + i);
        }
        TableRule tableRule = TableRule.builder("postflop")
                .actualTables(tableNames)
                .dataSourceRule(dataSourceRule)
                .build();

        // 分库分表策略
        ShardingRule shardingRule = ShardingRule.builder()
                .dataSourceRule(dataSourceRule)
                .tableRules(Arrays.asList(tableRule))
                .databaseShardingStrategy(new DatabaseShardingStrategy("flop_cards", databaseShardingAlgorithm))
                .tableShardingStrategy(new TableShardingStrategy("flop_cards", tableShardingAlgorithm))
                .build();
        DataSource dataSource = ShardingDataSourceFactory.createDataSource(shardingRule);
        return dataSource;
    }

    @Bean
    public KeyGenerator keyGenerator() {
        return new DefaultKeyGenerator();
    }
}
