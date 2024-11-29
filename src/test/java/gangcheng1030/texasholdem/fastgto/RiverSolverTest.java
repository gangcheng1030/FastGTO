package gangcheng1030.texasholdem.fastgto;

import com.alibaba.fastjson.JSON;
import gangcheng1030.texasholdem.fastgto.core.PostflopTreeNode;
import gangcheng1030.texasholdem.fastgto.riversolver.Card;
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
import gangcheng1030.texasholdem.fastgto.riversolver.utils.PrivateRangeConverter;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class RiverSolverTest {
    static Compairer compairer = null;
    static Deck deck = null;

    @Before
    public void loadEnvironmentsTest() {
        String[] suits = new String[]{"c", "d", "s", "h"};
        String[] ranks = new String[]{"A", "K", "Q", "J", "T", "9", "8", "7", "6", "5", "4", "3", "2"};
        if (compairer == null) {
            try {
                compairer = new Dic5Compairer("./src/test/resources/compairer/card5_dic_sorted.txt", 2598961);
                deck = new Deck(Arrays.asList(ranks), Arrays.asList(suits));
            } catch (Exception e) {
                e.printStackTrace();
                assertTrue(false);
            }
        }

    }

    @Test
    public void testRiverSolver() throws Exception {
        long startTime = System.currentTimeMillis();

        float oop_commit = 10.0f;
        float ip_commit = 10.0f;
        int current_round = 4;
        int raise_limit = 3;
        float small_blind = 0.5f;
        float big_blind = 1.0f;
        float stack = 100.0f;
        float[] bet_sizes = new float[]{33, 66, 100, 120};
        float[] raise_sizes = new float[]{50, 100};
        float[] donk_sizes = new float[]{33, 66, 100, 120};
        GameTreeBuildingSettings.StreetSetting riverIp = new GameTreeBuildingSettings.StreetSetting(
                Arrays.copyOf(bet_sizes, 4),
                Arrays.copyOf(raise_sizes, 2),
                new float[]{},
                true);
        GameTreeBuildingSettings.StreetSetting riverOop = new GameTreeBuildingSettings.StreetSetting(
                Arrays.copyOf(bet_sizes, 4),
                Arrays.copyOf(raise_sizes, 2),
                Arrays.copyOf(donk_sizes, 4),
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

        String player1RangeStr = "AA,QQ";
        String player2RangeStr = "KK";

        int[] initialBoard = new int[]{
                Card.strCard2int("2c"),
                Card.strCard2int("2d"),
                Card.strCard2int("2h"),
                Card.strCard2int("3c"),
                Card.strCard2int("3d")
        };

        PrivateCards[] player1Range = PrivateRangeConverter.rangeStr2Cards(player1RangeStr,initialBoard);
        PrivateCards[] player2Range = PrivateRangeConverter.rangeStr2Cards(player2RangeStr,initialBoard);

        String logfile_name = "src/test/resources/outputs/outputs_log.txt";
        Solver solver = new ParallelCfrPlusSolver(gameTree
                , player1Range
                , player2Range
                , initialBoard
                , compairer
                , deck
                ,100
                ,false
                , 10
                , logfile_name
                , DiscountedCfrTrainable.class
                , MonteCarolAlg.NONE
                ,4
                ,1
                ,0
                , 1
                , 0
        );

        Map train_config = new HashMap();
        solver.train(train_config);

        String strategy_json = solver.getTree().dumps(false).toJSONString();
        PostflopTreeNode postflopTreeNode = JSON.parseObject(strategy_json, PostflopTreeNode.class);

        long endTime = System.currentTimeMillis();
        System.out.println(endTime - startTime);
    }
}
