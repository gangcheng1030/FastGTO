package gangcheng1030.texasholdem.fastgto.core;

import com.alibaba.fastjson.JSON;
import gangcheng1030.texasholdem.fastgto.riversolver.Deck;
import gangcheng1030.texasholdem.fastgto.riversolver.GameTree;
import gangcheng1030.texasholdem.fastgto.riversolver.compairer.Compairer;
import gangcheng1030.texasholdem.fastgto.riversolver.compairer.Dic5Compairer;
import gangcheng1030.texasholdem.fastgto.riversolver.ranges.PrivateCards;
import gangcheng1030.texasholdem.fastgto.riversolver.solver.GameTreeBuildingSettings;
import gangcheng1030.texasholdem.fastgto.riversolver.solver.MonteCarolAlg;
import gangcheng1030.texasholdem.fastgto.riversolver.solver.ParallelCfrPlusSolver;
import gangcheng1030.texasholdem.fastgto.riversolver.solver.Solver;
import gangcheng1030.texasholdem.fastgto.riversolver.trainable.DiscountedCfrTrainable;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class RiverSolverManager {
    private static Compairer compairer = null;
    private static Deck deck = null;
    private LRUCache<String, PostflopTreeNode> lruCache;

    public RiverSolverManager() {
        this.lruCache = new LRUCache<>(8);
        String[] suits = new String[]{"c", "d", "s", "h"};
        String[] ranks = new String[]{"A", "K", "Q", "J", "T", "9", "8", "7", "6", "5", "4", "3", "2"};
        try {
            String fileName = "compairer/card5_dic_sorted.txt";
            compairer = new Dic5Compairer(fileName, 2598961);
            deck = new Deck(Arrays.asList(ranks), Arrays.asList(suits));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public PostflopTreeNode solve(float pot,
                                  float effectiveStack,
                                  List<Card> flopCards,
                                  Card turnCard,
                                  Card riverCard,
                                  PrivateCards[] range1,
                                  PrivateCards[] range2) throws Exception {
        String cacheKey = String.format("%s%s%s_%d_%d", flopCards.toString(), turnCard.toString(), riverCard.toString(), (int)pot, (int)effectiveStack);
        if (this.lruCache.containsKey(cacheKey)) {
            return this.lruCache.get(cacheKey);
        }

        float oop_commit = pot / 2;
        float ip_commit = pot / 2;
        int current_round = 4;
        int raise_limit = 3;
        float small_blind = 0.5f;
        float big_blind = 1.0f;
        float stack = effectiveStack + pot / 2;
        float[] bet_sizes = new float[]{15, 33, 66, 100, 120, 200, 300};
        float[] raise_sizes = new float[]{25, 50, 100};
        float[] donk_sizes = new float[]{15, 33, 66, 100, 120, 200, 300};
        GameTreeBuildingSettings.StreetSetting riverIp = new GameTreeBuildingSettings.StreetSetting(
                Arrays.copyOf(bet_sizes, 7),
                Arrays.copyOf(raise_sizes, 3),
                new float[]{},
                true);
        GameTreeBuildingSettings.StreetSetting riverOop = new GameTreeBuildingSettings.StreetSetting(
                Arrays.copyOf(bet_sizes, 7),
                Arrays.copyOf(raise_sizes, 3),
                Arrays.copyOf(donk_sizes, 7),
                true);
        GameTreeBuildingSettings gameTreeBuildingSettings = new GameTreeBuildingSettings(riverIp, riverIp, riverIp, riverOop, riverOop, riverOop);

        GameTree gameTree = new GameTree(
                deck,
                oop_commit,
                ip_commit,
                current_round,
                raise_limit,
                small_blind,
                big_blind,
                stack,
                gameTreeBuildingSettings);

        int[] initialBoard = new int[]{
                flopCards.get(0).getCardInt(),
                flopCards.get(1).getCardInt(),
                flopCards.get(2).getCardInt(),
                turnCard.getCardInt(),
                riverCard.getCardInt()
        };

        String logfile_name = "/tmp/outputs_log.txt";
        Solver solver = new ParallelCfrPlusSolver(gameTree
                , range1
                , range2
                , initialBoard
                , compairer
                , deck
                ,100
                ,false
                , 10
                , logfile_name
                , DiscountedCfrTrainable.class
                , MonteCarolAlg.NONE
                ,8
                ,1
                ,0
                , 1
                , 0
        );

        Map train_config = new HashMap();
        train_config.put("stop_exploitibility", 0.5);
        solver.train(train_config);

        String strategy_json = solver.getTree().dumps(false).toJSONString();
        PostflopTreeNode res = JSON.parseObject(strategy_json, PostflopTreeNode.class);
        this.lruCache.put(cacheKey, res);
        return res;
    }
}
