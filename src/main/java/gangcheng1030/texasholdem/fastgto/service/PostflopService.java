package gangcheng1030.texasholdem.fastgto.service;

import gangcheng1030.texasholdem.fastgto.core.*;
import gangcheng1030.texasholdem.fastgto.exceptions.CardsNotFoundException;
import gangcheng1030.texasholdem.fastgto.exceptions.StrategyNotFoundException;
import gangcheng1030.texasholdem.fastgto.util.FlopCardConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PostflopService {
    @Autowired
    private PostflopTreeManager postflopTreeManager;

    public TreeMap<String, Double> getStrategy(String preflopActions, List<Card> flopCards, List<Card> privateCards, int player, List<String> postflopActions) {
        String flopCardSuits = "";
        for (Card c : flopCards) {
            flopCardSuits += c.getSuit();
        }
        Map<String, String> convertRules = FlopCardConvertUtil.getConvertRule(flopCardSuits);
        if (convertRules == null) {
            throw new CardsNotFoundException("不存在的花色");
        }
        List<Card> innerFlopCards = convertCardList(flopCards, convertRules);
        List<Card> innerPrivateCards = convertCardList(privateCards, convertRules);

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

    public List<Card> convertFlopCards(List<Card> srcFlopCards, Map<String, String> convertRules) {
        List<Card> dstFlopCards = new ArrayList<>();
        if (!srcFlopCards.get(0).getSuit().equals("c")) {
            convertRules.put(srcFlopCards.get(0).getSuit(), "c");
        }
        dstFlopCards.add(convertCard(srcFlopCards.get(0), convertRules));

        if (!srcFlopCards.get(1).getSuit().equals(srcFlopCards.get(0).getSuit())
                && !srcFlopCards.get(1).getSuit().equals("d")) {
            convertRules.put(srcFlopCards.get(1).getSuit(), "d");
        }
        dstFlopCards.add(convertCard(srcFlopCards.get(1), convertRules));

        if (!srcFlopCards.get(2).getSuit().equals(srcFlopCards.get(0).getSuit())
                && !srcFlopCards.get(2).getSuit().equals(srcFlopCards.get(1).getSuit())
                && !srcFlopCards.get(2).getSuit().equals("h")) {
            convertRules.put(srcFlopCards.get(2).getSuit(), "h");
        }
        dstFlopCards.add(convertCard(srcFlopCards.get(2), convertRules));

        return dstFlopCards;
    }

    public Card convertCard(Card src, Map<String, String> convertRules) {
        if (!convertRules.containsKey(src.getSuit())) {
            return new Card(src.getCard());
        }


        String dstCard = src.getCard().replace(src.getSuit(), convertRules.get(src.getSuit()));
        return new Card(dstCard);
    }

    private List<Card> convertCardList(List<Card> srcList, Map<String, String> convertRules) {
        List<Card> dstList = new ArrayList<>();
        for (Card src : srcList) {
            dstList.add(convertCard(src, convertRules));
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
