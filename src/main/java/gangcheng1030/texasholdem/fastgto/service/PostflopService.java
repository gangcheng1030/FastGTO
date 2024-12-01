package gangcheng1030.texasholdem.fastgto.service;

import com.alibaba.fastjson.JSON;
import gangcheng1030.texasholdem.fastgto.core.*;
import gangcheng1030.texasholdem.fastgto.dao.PostflopRepository;
import gangcheng1030.texasholdem.fastgto.core.NodeType;
import gangcheng1030.texasholdem.fastgto.exceptions.RangesIllegalException;
import gangcheng1030.texasholdem.fastgto.exceptions.StrategyNotFoundException;
import gangcheng1030.texasholdem.fastgto.po.Postflop;
import gangcheng1030.texasholdem.fastgto.riversolver.ranges.PrivateCards;
import gangcheng1030.texasholdem.fastgto.riversolver.utils.PrivateRangeConverter;
import gangcheng1030.texasholdem.fastgto.util.IncrementalIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class PostflopService {
    @Autowired
    private PostflopRepository postflopRepository;
    @Autowired
    private RiverSolverManager riverSolverManager;
    @Autowired
    private PreflopTree preflopTree;
    @Autowired
    private JDBCRepository jdbcRepository;

    // 这里的flopCards，turnCard，riverCard，privateCards必须是标准化后的
    public TreeMap<String, Double> getRiverStrategy(String preflopActions,
                                                    List<Card> flopCards,
                                                    Card turnCard,
                                                    Card riverCard,
                                                    List<Card> privateCards,
                                                    int player,
                                                    List<String> postflopActions,
                                                    List<String> riverActions,
                                                    float pot,
                                                    float effectiveStack) throws Exception {
        int[] initialBoard = new int[]{
                flopCards.get(0).getCardInt(),
                flopCards.get(1).getCardInt(),
                flopCards.get(2).getCardInt()
        };

        // 获取preflop范围
        String[] names = preflopActions.split(":");
        List<String> preflopRanges = preflopTree.getSoloRanges(names);
        TreeMap<String, Double> range1Map = PrivateRangeConverter.rangeStr2CardsMap(preflopRanges.get(0), initialBoard);
        TreeMap<String, Double> range2Map = PrivateRangeConverter.rangeStr2CardsMap(preflopRanges.get(1), initialBoard);

        // 获取river范围
        String flopCardsStr = convertCardsToString(flopCards);
        Postflop postflop = postflopRepository.findFirstByFlopCardsAndParentAndPreflopActions(flopCardsStr, 0L, preflopActions);
        if (postflop == null) {
            throw new StrategyNotFoundException("找不到相应的策略树");
        }

        for (String postflopAction : postflopActions) {
            if (!StringUtils.isEmpty(postflop.getStrategy())) {
                CompressedStrategy compressedStrategy = JSON.parseObject(postflop.getStrategy(), CompressedStrategy.class);
                int actionIndex = compressedStrategy.getActions().indexOf(postflopAction);
                for (String tCards : compressedStrategy.getStrategy().keySet()) {
                    if (postflop.getPlayer() == 0) {
                        if (!range1Map.containsKey(tCards)) {
                            throw new RangesIllegalException("翻前范围与翻后范围不匹配");
                        }
                        Double weight = range1Map.get(tCards);
                        weight = weight * compressedStrategy.getStrategy().get(tCards).get(actionIndex) / Constants.WEIGHTS_RADIX;
                        range1Map.put(tCards, weight);
                    } else {
                        if (!range2Map.containsKey(tCards)) {
                            throw new RangesIllegalException("翻前范围与翻后范围不匹配");
                        }
                        Double weight = range2Map.get(tCards);
                        weight = weight * compressedStrategy.getStrategy().get(tCards).get(actionIndex) / Constants.WEIGHTS_RADIX;
                        range2Map.put(tCards, weight);
                    }
                }
            }
            postflop = postflopRepository.findFirstByFlopCardsAndParentAndAction(flopCardsStr, postflop.getId(), postflopAction);
            if (postflop == null) {
                throw new StrategyNotFoundException("策略树中找不到相应的行动线");
            }
        }

        // river solver
        PrivateCards[] range1Array = PrivateRangeConverter.map2Cards(range1Map);
        PrivateCards[] range2Array = PrivateRangeConverter.map2Cards(range2Map);
        PostflopTreeNode postflopTreeNode = riverSolverManager.solve(
                pot,
                effectiveStack,
                flopCards,
                turnCard,
                riverCard,
                range1Array,
                range2Array);
        if (riverActions != null && !riverActions.isEmpty()) {
            PostflopTree postflopTree = new PostflopTree(postflopTreeNode);
            postflopTreeNode = postflopTree.search(riverActions);
        }
        if (postflopTreeNode == null) {
            return new TreeMap<>();
        }

        String privateCardsStr = convertCardsToString(privateCards);
        Strategy strategy = postflopTreeNode.getStrategy();
        List<String> actions = strategy.getActions();
        List<Double> weights = new ArrayList<>();
        if (player == postflopTreeNode.getPlayer()) {
            weights = strategy.getStrategy().get(privateCardsStr);
        } else {
            for (int i = 0; i < actions.size(); i++) {
                weights.add(0.0);
            }
        }

        TreeMap<String, Double> res = new TreeMap<>();
        for (int i = 0; i < weights.size(); i++) {
            res.put(actions.get(i), weights.get(i));
        }

        return res;
    }

    public TreeMap<String, Double> getStrategyByDB(String preflopActions, List<Card> flopCards, List<Card> privateCards, int player, List<String> postflopActions) {
        String flopCardsStr = convertCardsToString(flopCards);
        Postflop postflop = postflopRepository.findFirstByFlopCardsAndParentAndPreflopActions(flopCardsStr, 0L, preflopActions);
        if (postflop == null) {
            throw new StrategyNotFoundException("找不到相应的策略树");
        }
        if (postflopActions != null && !postflopActions.isEmpty()) {
            postflop = getPostflopByPostflopActions(flopCardsStr, postflop.getId(), postflopActions);
            if (postflop == null) {
                throw new StrategyNotFoundException("策略树中找不到相应的行动线");
            }
        }
        if (postflop.getNodeType() == NodeType.CHANCE_NODE.getId()) {
            return new TreeMap<>();
        }

        String privateCardsStr = convertCardsToString(privateCards);
        CompressedStrategy strategy = JSON.parseObject(postflop.getStrategy(), CompressedStrategy.class);
        List<String> actions = strategy.getActions();
        List<Integer> weights = new ArrayList<>();
        if (player == postflop.getPlayer()) {
            weights = strategy.getStrategy().get(privateCardsStr);
        } else {
            for (int i = 0; i < actions.size(); i++) {
                weights.add(0);
            }
        }

        TreeMap<String, Double> res = new TreeMap<>();
        for (int i = 0; i < weights.size(); i++) {
            res.put(actions.get(i), weights.get(i) * 1.0 / Constants.WEIGHTS_RADIX);
        }
        return res;
    }

    private Postflop getPostflopByPostflopActions(String flopCards, Long root, List<String> postflopActions) {
        Postflop res = null;
        Long parent = root;
        for (String action : postflopActions) {
            res = postflopRepository.findFirstByFlopCardsAndParentAndAction(flopCards, parent, action);
            if (res == null) {
                return null;
            }
            parent = res.getId();
        }
        return res;
    }

    public void importPostflopTree(PostflopTreeNode root, String preflopActions, String flopCards) {
        Postflop postflop = new Postflop();
        postflop.setId(IncrementalIdGenerator.nextId());
        postflop.setPreflopActions(preflopActions);
        postflop.setFlopCards(flopCards);
        postflop.setParent(0L);
        postflop.setPlayer(root.getPlayer());
        postflop.setNodeType(NodeType.typeToId(root.getNode_type()));
        CompressedStrategy compressedStrategy = root.getStrategy().compress();
        postflop.setStrategy(JSON.toJSONString(compressedStrategy));

        List<Postflop> postflopList = new ArrayList<>();
        postflopList.add(postflop);
        try {
            Map<String, PostflopTreeNode> children = root.getChildrens();
            for (String action : children.keySet()) {
                importPostflopTreeInner(children.get(action), postflop.getId(), flopCards, action, postflopList);
            }
        } catch (Throwable e) {
            System.out.println(postflop);
            e.printStackTrace();
            throw e;
        }
        if (postflopList.size() > 0) {
            jdbcRepository.addAllPostflops(postflopList);
        }
    }

    private void importPostflopTreeInner(PostflopTreeNode root, Long parent, String flopCards, String action, List<Postflop> postflopList) {
        Postflop postflop = new Postflop();
        postflop.setId(IncrementalIdGenerator.nextId());
        postflop.setFlopCards(flopCards);
        postflop.setParent(parent);
        postflop.setPlayer(root.getPlayer());
        postflop.setNodeType(NodeType.typeToId(root.getNode_type()));
        postflop.setAction(action);
        if (root.getStrategy() != null) {
            CompressedStrategy compressedStrategy = root.getStrategy().compress();
            postflop.setStrategy(JSON.toJSONString(compressedStrategy));
        }

        postflopList.add(postflop);
        Map<String, PostflopTreeNode> children = null;
        if (root.getNode_type().equals(NodeType.ACTION_NODE.getType())) {
            children = root.getChildrens();
        } else {
            children = root.getDealcards();
        }

        if (children != null) {
            for (String childAction : children.keySet()) {
                importPostflopTreeInner(children.get(childAction), postflop.getId(), flopCards, childAction, postflopList);
            }
        }

        if (postflopList.size() >= 10) {
            jdbcRepository.addAllPostflops(postflopList);
            postflopList.clear();
        }
    }
    private String convertCardsToString(List<Card> cards) {
        StringBuilder sb = new StringBuilder();
        for (Card card : cards) {
            sb.append(card.getCard());
        }

        return sb.toString();
    }
}
