package gangcheng1030.texasholdem.fastgto.sharding;

import com.dangdang.ddframe.rdb.sharding.api.ShardingValue;
import com.dangdang.ddframe.rdb.sharding.api.strategy.table.SingleKeyTableShardingAlgorithm;
import gangcheng1030.texasholdem.fastgto.util.HashUtil;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.LinkedHashSet;

@Component
public class TableShardingAlgorithm implements SingleKeyTableShardingAlgorithm<String> {
    public static final long ShardingTableNum = 1024;

    @Override
    public String doEqualSharding(Collection<String> collection, ShardingValue<String> shardingValue) {
        long hash = HashUtil.get32BitHash(shardingValue.getValue()) % ShardingTableNum;
        for (String tableName : collection) {
            if (tableName.endsWith("_" + hash)) {
                return tableName;
            }
        }
        return "";
    }

    @Override
    public Collection<String> doInSharding(Collection<String> collection, ShardingValue<String> shardingValue) {
        Collection<String> result = new LinkedHashSet<>(collection.size());
        for (String value : shardingValue.getValues()) {
            long hash = HashUtil.get32BitHash(shardingValue.getValue()) % ShardingTableNum;
            for (String tableName : collection) {
                if (tableName.endsWith("_" + hash)) {
                    result.add(tableName);
                }
            }
        }
        return result;
    }

    @Override
    public Collection<String> doBetweenSharding(Collection<String> collection, ShardingValue<String> shardingValue) {
        return null;
    }
}
