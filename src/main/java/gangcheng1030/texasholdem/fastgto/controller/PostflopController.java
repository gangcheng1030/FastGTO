package gangcheng1030.texasholdem.fastgto.controller;

import gangcheng1030.texasholdem.fastgto.core.Card;
import gangcheng1030.texasholdem.fastgto.service.PostflopService;
import gangcheng1030.texasholdem.fastgto.util.FlopCardConvertUtil;
import gangcheng1030.texasholdem.fastgto.util.RandomGenerator;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Controller
@RequestMapping("/postflop")
public class PostflopController {
    @Autowired
    private PostflopService postflopService;

    @GetMapping("/flop")
    public String getFlopStrategy(@RequestParam(value = "preflopActions") String preflopActions,
                              @RequestParam(value = "flopCards", required = false) String flopCards,
                              @RequestParam(value = "privateCards", required = false) String privateCards,
                              @RequestParam(value = "position", required = false) String position,
                              @RequestParam(value = "postflopActions", required = false) String postflopActions,
                              Model model) {
        model.addAttribute("preflopActions", preflopActions);

        if (StringUtils.isEmpty(flopCards) || StringUtils.isEmpty(privateCards)) {
            model.addAttribute("needInput", true);
            return "flop";
        }

        List<Card> flopCardList = convertFlopCards(flopCards);
        Map<String, String> convertRules = getConvertRules(flopCardList);
        flopCardList = convertCardListByRules(flopCardList, convertRules, false);
        List<Card> privateCardList = convertPrivateCards(privateCards, convertRules);
        List<String> postflopActionList = new ArrayList<>();
        if (postflopActions != null) {
            postflopActionList = Arrays.asList(postflopActions.split(":"));
        }
        int player = position.equals("ip") ? 0 : 1;
        TreeMap<String, Double> strategy = null;
        try {
            strategy = postflopService.getStrategyByDB(preflopActions, flopCardList, privateCardList, player, postflopActionList);
        } catch (RuntimeException e) {
            model.addAttribute("errMsg", e.getMessage());
            return "error";
        }

        TreeMap<String, String> outStrategy = new TreeMap<>();
        int r = RandomGenerator.nextInt(10000);
        int cur = 0;
        String suggestedAction = null;
        for (String action : strategy.keySet()) {
            String key = String.format("%s: %.2f", action, strategy.get(action));
            String value = StringUtils.isEmpty(postflopActions) ? action : postflopActions + ":" + action;
            cur += strategy.get(action) * 10000;
            if (cur > r && suggestedAction == null) {
                suggestedAction = action;
                key += "(建议)";
            }
            outStrategy.put(key, value);
        }

        if (strategy.isEmpty()) {
            model.addAttribute("postflopActions", postflopActions);
        }
        model.addAttribute("flopCards", flopCards);
        model.addAttribute("privateCards", privateCards);
        model.addAttribute("position", position);
        model.addAttribute("strategy", outStrategy);
        return "flop";
    }

    @GetMapping("/turn")
    public String getTurnStrategy(@RequestParam(value = "preflopActions") String preflopActions,
                                  @RequestParam(value = "flopCards") String flopCards,
                                  @RequestParam(value = "privateCards") String privateCards,
                                  @RequestParam(value = "position") String position,
                                  @RequestParam(value = "postflopActions") String postflopActions,
                                  @RequestParam(value = "turnCard", required = false) String turnCard,
                                  @RequestParam(value = "notChance", required = false) Boolean notChance,
                                  Model model) {
        model.addAttribute("preflopActions", preflopActions);
        model.addAttribute("flopCards", flopCards);
        model.addAttribute("privateCards", privateCards);
        model.addAttribute("position", position);
        model.addAttribute("postflopActions", postflopActions);

        if (StringUtils.isEmpty(turnCard)) {
            model.addAttribute("needInput", true);
            return "turn";
        }

        List<Card> flopCardList = convertFlopCards(flopCards);
        Map<String, String> convertRules = getConvertRules(flopCardList);
        flopCardList = convertCardListByRules(flopCardList, convertRules, false);
        List<Card> privateCardList = convertPrivateCards(privateCards, convertRules);
        List<String> postflopActionList = new ArrayList<>(Arrays.asList(postflopActions.split(":")));
        if (notChance == null || !notChance) {
            Card innerTurnCard = convertCard(turnCard, convertRules);
            postflopActionList.add(innerTurnCard.getCard());
            postflopActions += ":" + innerTurnCard.getCard();
            model.addAttribute("postflopActions", postflopActions);
        }
        int player = position.equals("ip") ? 0 : 1;
        TreeMap<String, Double> strategy = null;
        try {
            strategy = postflopService.getStrategyByDB(preflopActions, flopCardList, privateCardList, player, postflopActionList);
        } catch (RuntimeException e) {
            model.addAttribute("errMsg", e.getMessage());
            return "error";
        }

        TreeMap<String, String> outStrategy = new TreeMap<>();
        int r = RandomGenerator.nextInt(10000);
        int cur = 0;
        String suggestedAction = null;
        for (String action : strategy.keySet()) {
            String key = String.format("%s: %.2f", action, strategy.get(action));
            String value = StringUtils.isEmpty(postflopActions) ? action : postflopActions + ":" + action;
            cur += strategy.get(action) * 10000;
            if (cur > r && suggestedAction == null) {
                suggestedAction = action;
                key += "(建议)";
            }
            outStrategy.put(key, value);
        }

        if (strategy.isEmpty()) {
            model.addAttribute("nextStreet", true);
        }
        model.addAttribute("turnCard", turnCard);
        model.addAttribute("strategy", outStrategy);
        return "turn";
    }

    @GetMapping("/river")
    public String getRiverStrategy(@RequestParam(value = "preflopActions") String preflopActions,
                                  @RequestParam(value = "flopCards") String flopCards,
                                  @RequestParam(value = "privateCards") String privateCards,
                                  @RequestParam(value = "position") String position,
                                  @RequestParam(value = "postflopActions") String postflopActions,
                                  @RequestParam(value = "turnCard") String turnCard,
                                  @RequestParam(value = "riverCard", required = false) String riverCard,
                                  @RequestParam(value = "pot", required = false) Integer pot,
                                  @RequestParam(value = "effectiveStack", required = false) Integer effectiveStack,
                                  @RequestParam(value = "riverActions", required = false) String riverActions,
                                  Model model) {
        model.addAttribute("preflopActions", preflopActions);
        model.addAttribute("flopCards", flopCards);
        model.addAttribute("privateCards", privateCards);
        model.addAttribute("position", position);
        model.addAttribute("postflopActions", postflopActions);
        model.addAttribute("turnCard", turnCard);

        if (StringUtils.isEmpty(riverCard)) {
            model.addAttribute("needInput", true);
            return "river";
        }

        List<Card> flopCardList = convertFlopCards(flopCards);
        Map<String, String> convertRules = getConvertRules(flopCardList);
        flopCardList = convertCardListByRules(flopCardList, convertRules, false);
        List<Card> privateCardList = convertPrivateCards(privateCards, convertRules);
        List<String> postflopActionList = new ArrayList<>(Arrays.asList(postflopActions.split(":")));
        Card innerTurnCard = convertCard(turnCard, convertRules);
        Card innerRiverCard = convertCard(riverCard, convertRules);
        int player = position.equals("ip") ? 0 : 1;
        List<String> riverActionList = new ArrayList<>();
        if (!StringUtils.isEmpty(riverActions)) {
            riverActionList = Arrays.asList(riverActions.split(":"));
        }
        TreeMap<String, Double> strategy = null;
        try {
            strategy = postflopService.getRiverStrategy(
                    preflopActions,
                    flopCardList,
                    innerTurnCard,
                    innerRiverCard,
                    privateCardList,
                    player,
                    postflopActionList,
                    riverActionList,
                    pot,
                    effectiveStack);
        } catch (Throwable e) {
            model.addAttribute("errMsg", e.getMessage());
            return "error";
        }

        TreeMap<String, String> outStrategy = new TreeMap<>();
        int r = RandomGenerator.nextInt(10000);
        int cur = 0;
        String suggestedAction = null;
        for (String action : strategy.keySet()) {
            String key = String.format("%s: %.2f", action, strategy.get(action));
            String value = StringUtils.isEmpty(riverActions) ? action : riverActions + ":" + action;
            cur += strategy.get(action) * 10000;
            if (cur > r && suggestedAction == null) {
                suggestedAction = action;
                key += "(建议)";
            }
            outStrategy.put(key, value);
        }

        if (strategy.isEmpty()) {
            model.addAttribute("nextStreet", true);
        }
        model.addAttribute("pot", pot);
        model.addAttribute("effectiveStack", effectiveStack);
        model.addAttribute("riverCard", riverCard);
        model.addAttribute("strategy", outStrategy);
        return "river";
    }

    private List<Card> convertFlopCards(String cards) {
        cards = cards.replace('♠', 's');
        cards = cards.replace('♥', 'h');
        cards = cards.replace('♣', 'c');
        cards = cards.replace('♦', 'd');

        List<Card> cardList = new ArrayList<>();
        for (int i = 0; i < cards.length(); i+=2) {
            cardList.add(new Card(cards.substring(i, i+2)));
        }
        Collections.sort(cardList);

        return cardList;
    }

    private List<Card> convertPrivateCards(String cards, Map<String, String> convertRules) {
        cards = cards.replace('♠', 's');
        cards = cards.replace('♥', 'h');
        cards = cards.replace('♣', 'c');
        cards = cards.replace('♦', 'd');

        List<Card> cardList = new ArrayList<>();
        for (int i = 0; i < cards.length(); i+=2) {
            cardList.add(new Card(cards.substring(i, i+2)));
        }
        convertCardListByRules(cardList, convertRules, true);

        return cardList;
    }

    private Card convertCard(String card, Map<String, String> convertRules) {
        card =  card.replace('♠', 's')
                .replace('♥', 'h')
                .replace('♣', 'c')
                .replace('♦', 'd');

        return convertCardByRules(new Card(card), convertRules);
    }

    private Map<String, String> getConvertRules(List<Card> flopCards) {
        String flopCardSuits = "";
        for (Card c : flopCards) {
            flopCardSuits += c.getSuit();
        }
        boolean formerTwoIsEqual = flopCards.get(0).getRank().equals(flopCards.get(1).getRank());
        return FlopCardConvertUtil.getConvertRule(flopCardSuits, formerTwoIsEqual);
    }

    private List<Card> convertCardListByRules(List<Card> srcList, Map<String, String> convertRules, boolean reverse) {
        List<Card> dstList = new ArrayList<>();
        for (Card src : srcList) {
            dstList.add(convertCardByRules(src, convertRules));
        }
        Collections.sort(dstList);
        if (reverse) {
            Collections.reverse(dstList);
        }
        return dstList;
    }

    public Card convertCardByRules(Card src, Map<String, String> convertRules) {
        if (!convertRules.containsKey(src.getSuit())) {
            return new Card(src.getCard());
        }


        String dstCard = src.getCard().replace(src.getSuit(), convertRules.get(src.getSuit()));
        return new Card(dstCard);
    }
}
