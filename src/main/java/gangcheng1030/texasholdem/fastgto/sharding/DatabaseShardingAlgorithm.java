package gangcheng1030.texasholdem.fastgto.sharding;

import com.dangdang.ddframe.rdb.sharding.api.ShardingValue;
import com.dangdang.ddframe.rdb.sharding.api.strategy.database.SingleKeyDatabaseShardingAlgorithm;
import gangcheng1030.texasholdem.fastgto.config.Database0Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.LinkedHashSet;

@Component
public class DatabaseShardingAlgorithm implements SingleKeyDatabaseShardingAlgorithm<String> {
    @Autowired
    private Database0Config database0Config;

    @Override
    public String doEqualSharding(Collection<String> collection, ShardingValue<String> shardingValue) {
        return database0Config.getDatabaseName();
    }

    @Override
    public Collection<String> doInSharding(Collection<String> collection, ShardingValue<String> shardingValue) {
        Collection<String> result = new LinkedHashSet<>(collection);
        result.add(database0Config.getDatabaseName());
        return result;
    }

    @Override
    public Collection<String> doBetweenSharding(Collection<String> collection, ShardingValue<String> shardingValue) {
        Collection<String> result = new LinkedHashSet<>(collection);
        result.add(database0Config.getDatabaseName());
        return result;
    }
}
