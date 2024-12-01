package gangcheng1030.texasholdem.fastgto.core;

import gangcheng1030.texasholdem.fastgto.config.Database0Config;
import gangcheng1030.texasholdem.fastgto.po.Postflop;
import gangcheng1030.texasholdem.fastgto.sharding.TableShardingAlgorithm;
import gangcheng1030.texasholdem.fastgto.util.HashUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

@Component
public class JDBCRepository {
    private static final String batchInsertPostflopsSQL = "insert into postflop_%d (id, preflop_actions, flop_cards, parent, player, node_type, action, strategy) values (?,?,?,?,?,?,?,?)";
    private DataSource dataSource;
    @Autowired
    private Database0Config database0Config;

    @PostConstruct
    public void init() {
        this.dataSource = database0Config.createDataSource();
    }

    public void addAllPostflops(List<Postflop> postflopList) {
        if (postflopList == null || postflopList.isEmpty()) {
            return;
        }
        long shard = HashUtil.get32BitHash(postflopList.get(0).getFlopCards()) % TableShardingAlgorithm.ShardingTableNum;
        String sql = String.format(batchInsertPostflopsSQL, shard);

        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                connection.setAutoCommit(false);
                for (Postflop postflop : postflopList) {
                    ps.setLong(1, postflop.getId());
                    if (postflop.getPreflopActions() != null) {
                        ps.setString(2, postflop.getPreflopActions());
                    } else {
                        ps.setNull(2, Types.VARCHAR);
                    }
                    ps.setString(3, postflop.getFlopCards());
                    ps.setLong(4, postflop.getParent());
                    if (postflop.getPlayer() != null) {
                        ps.setInt(5, postflop.getPlayer());
                    } else {
                        ps.setNull(5, Types.TINYINT);
                    }
                    ps.setInt(6, postflop.getNodeType());
                    if (postflop.getAction() != null) {
                        ps.setString(7, postflop.getAction());
                    } else {
                        ps.setNull(7, Types.VARCHAR);
                    }
                    if (postflop.getStrategy() != null) {
                        ps.setString(8, postflop.getStrategy());
                    } else {
                        ps.setNull(8, Types.VARCHAR);
                    }
                    ps.addBatch();
                }
                ps.executeBatch();
                connection.commit();
            }
        } catch (SQLException e) {
            System.out.printf("postflopList: %s \n", postflopList);
            e.printStackTrace();
        }
    }
}
