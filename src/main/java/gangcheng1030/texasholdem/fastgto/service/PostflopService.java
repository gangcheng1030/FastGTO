package gangcheng1030.texasholdem.fastgto.service;

import com.alibaba.fastjson.JSON;
import gangcheng1030.texasholdem.fastgto.core.*;
import gangcheng1030.texasholdem.fastgto.dao.PostflopRepository;
import gangcheng1030.texasholdem.fastgto.core.NodeType;
import gangcheng1030.texasholdem.fastgto.exceptions.CardsNotFoundException;
import gangcheng1030.texasholdem.fastgto.exceptions.StrategyNotFoundException;
import gangcheng1030.texasholdem.fastgto.po.Postflop;
import gangcheng1030.texasholdem.fastgto.util.FlopCardConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PostflopService {
    @Autowired
    private PostflopTreeManager postflopTreeManager;

    @Autowired
    private PostflopRepository postflopRepository;

    public TreeMap<String, Double> getStrategy(String preflopActions, List<Card> flopCards, List<Card> privateCards, int player, List<String> postflopActions) {
        String flopCardSuits = "";
        for (Card c : flopCards) {
            flopCardSuits += c.getSuit();
        }
        boolean formerTwoIsEqual = flopCards.get(0).getRank().equals(flopCards.get(1).getRank());
        Map<String, String> convertRules = FlopCardConvertUtil.getConvertRule(flopCardSuits, formerTwoIsEqual);
        if (convertRules == null) {
            throw new CardsNotFoundException("不存在的花色");
        }
        List<Card> innerFlopCards = convertCardList(flopCards, convertRules, false);
        List<Card> innerPrivateCards = convertCardList(privateCards, convertRules, true);

        String flopCardsStr = convertCardsToString(innerFlopCards);
        PostflopTreeNode root = postflopTreeManager.get(preflopActions, flopCardsStr);
        if (root == null) {
            throw new StrategyNotFoundException("找不到相应的策略树");
        }

        PostflopTree postflopTree = new PostflopTree(root);
        PostflopTreeNode postflopTreeNode = postflopTree.search(postflopActions);
        if (postflopTreeNode == null) {
            throw new StrategyNotFoundException("策略树中找不到相应的行动线");
        }
        if (postflopTreeNode.getNode_type().equals(PostflopTree.CHANCE_NODE)) {
            return new TreeMap<>();
        }

        String privateCardsStr = convertCardsToString(innerPrivateCards);
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
        String flopCardSuits = "";
        for (Card c : flopCards) {
            flopCardSuits += c.getSuit();
        }
        boolean formerTwoIsEqual = flopCards.get(0).getRank().equals(flopCards.get(1).getRank());
        Map<String, String> convertRules = FlopCardConvertUtil.getConvertRule(flopCardSuits, formerTwoIsEqual);
        if (convertRules == null) {
            throw new CardsNotFoundException("不存在的花色");
        }
        List<Card> innerFlopCards = convertCardList(flopCards, convertRules, false);
        List<Card> innerPrivateCards = convertCardList(privateCards, convertRules, true);

        String flopCardsStr = convertCardsToString(innerFlopCards);
        Postflop root = postflopRepository.findFirstByFlopCardsAndParentAndPreflopActions(flopCardsStr, 0, preflopActions);
        if (root == null) {
            throw new StrategyNotFoundException("找不到相应的策略树");
        }

        return null;
    }

    private Postflop getPostflopByPostflopActions(String flopCards, Integer parent, List<String> postflopActions) {
        return null;
    }

    public void importPostflopTree(PostflopTreeNode root, String preflopActions, String flopCards) {
        Postflop postflop = new Postflop();
        postflop.setPreflopActions(preflopActions);
        postflop.setFlopCards(flopCards);
        postflop.setParent(0);
        postflop.setPlayer(root.getPlayer());
        postflop.setNodeType(NodeType.typeToId(root.getNode_type()));
        CompressedStrategy compressedStrategy = root.getStrategy().compress();
        postflop.setStrategy(JSON.toJSONString(compressedStrategy));

        Postflop savedPostflop = postflopRepository.save(postflop);
        Map<String, PostflopTreeNode> children = root.getChildrens();
        for (String action : children.keySet()) {
            importPostflopTreeInner(children.get(action), savedPostflop.getId(), flopCards, action);
        }
    }

    private void importPostflopTreeInner(PostflopTreeNode root, int parent, String flopCards, String action) {
        Postflop postflop = new Postflop();
        postflop.setFlopCards(flopCards);
        postflop.setParent(parent);
        postflop.setPlayer(root.getPlayer());
        postflop.setNodeType(NodeType.typeToId(root.getNode_type()));
        postflop.setAction(action);
        if (root.getStrategy() != null) {
            CompressedStrategy compressedStrategy = root.getStrategy().compress();
            postflop.setStrategy(JSON.toJSONString(compressedStrategy));
        }

        Postflop savedPostflop = postflopRepository.save(postflop);
        Map<String, PostflopTreeNode> children = null;
        if (root.getNode_type().equals(NodeType.ACTION_NODE.getType())) {
            children = root.getChildrens();
        } else {
            children = root.getDealcards();
        }

        if (children != null) {
            for (String childAction : children.keySet()) {
                importPostflopTreeInner(children.get(childAction), savedPostflop.getId(), flopCards, childAction);
            }
        }
    }

    public Card convertCard(Card src, Map<String, String> convertRules) {
        if (!convertRules.containsKey(src.getSuit())) {
            return new Card(src.getCard());
        }


        String dstCard = src.getCard().replace(src.getSuit(), convertRules.get(src.getSuit()));
        return new Card(dstCard);
    }

    private List<Card> convertCardList(List<Card> srcList, Map<String, String> convertRules, boolean reverse) {
        List<Card> dstList = new ArrayList<>();
        for (Card src : srcList) {
            dstList.add(convertCard(src, convertRules));
        }
        Collections.sort(dstList);
        if (reverse) {
            Collections.reverse(dstList);
        }
        return dstList;
    }

    private String convertCardsToString(List<Card> cards) {
        StringBuilder sb = new StringBuilder();
        for (Card card : cards) {
            sb.append(card.getCard());
        }

        return sb.toString();
    }
}
