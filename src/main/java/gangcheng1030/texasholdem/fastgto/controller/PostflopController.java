package gangcheng1030.texasholdem.fastgto.controller;

import gangcheng1030.texasholdem.fastgto.core.Card;
import gangcheng1030.texasholdem.fastgto.service.PostflopService;
import gangcheng1030.texasholdem.fastgto.util.FlopCardConvertUtil;
import gangcheng1030.texasholdem.fastgto.util.RandomGenerator;
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
    private static final Map<String, String> preflopConvertMap = new HashMap<>();
    static {
        preflopConvertMap.put("LJ2bet:HJ3bet:COfold:BNfold:SBfold:BBfold:LJ4bet:HJcall", "HJ_VS_LJ_4BET");
        preflopConvertMap.put("LJfold:HJfold:COfold:BN2bet:SB3bet:BBfold:BNcall", "BN_VS_SB_3BET");
    }

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
        String innerPreflopActions = preflopConvertMap.get(preflopActions);
        if (innerPreflopActions == null) {
            model.addAttribute("errMsg", "翻前动作不合法");
            return "error";
        }

        if (StringUtils.isEmpty(flopCards) || StringUtils.isEmpty(privateCards)) {
            model.addAttribute("needInput", true);
            return "flop";
        }

        List<Card> flopCardList = convertFlopCards(flopCards);
        List<Card> privateCardList = convertPrivateCards(privateCards);
        List<String> postflopActionList = new ArrayList<>();
        if (postflopActions != null) {
            postflopActionList = Arrays.asList(postflopActions.split(":"));
        }
        int player = position.equals("ip") ? 0 : 1;
        TreeMap<String, Double> strategy = null;
        try {
            strategy = postflopService.getStrategy(innerPreflopActions, flopCardList, privateCardList, player, postflopActionList);
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

        String innerPreflopActions = preflopConvertMap.get(preflopActions);
        if (innerPreflopActions == null) {
            model.addAttribute("errMsg", "翻前动作不合法");
            return "error";
        }

        List<Card> flopCardList = convertFlopCards(flopCards);
        List<Card> privateCardList = convertPrivateCards(privateCards);
        List<String> postflopActionList = new ArrayList<>(Arrays.asList(postflopActions.split(":")));
        if (notChance == null || !notChance) {
            Card innerTurnCard = convertCard(flopCardList, turnCard);
            postflopActionList.add(innerTurnCard.getCard());
            postflopActions += ":" + innerTurnCard.getCard();
            model.addAttribute("postflopActions", postflopActions);
        }
        int player = position.equals("ip") ? 0 : 1;
        TreeMap<String, Double> strategy = null;
        try {
            strategy = postflopService.getStrategy(innerPreflopActions, flopCardList, privateCardList, player, postflopActionList);
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
                                  @RequestParam(value = "riverCard", required = false) String riverCard,
                                  @RequestParam(value = "notChance", required = false) Boolean notChance,
                                  Model model) {
        model.addAttribute("preflopActions", preflopActions);
        model.addAttribute("flopCards", flopCards);
        model.addAttribute("privateCards", privateCards);
        model.addAttribute("position", position);
        model.addAttribute("postflopActions", postflopActions);

        if (StringUtils.isEmpty(riverCard)) {
            model.addAttribute("needInput", true);
            return "river";
        }

        String innerPreflopActions = preflopConvertMap.get(preflopActions);
        if (innerPreflopActions == null) {
            model.addAttribute("errMsg", "翻前动作不合法");
            return "error";
        }

        List<Card> flopCardList = convertFlopCards(flopCards);
        List<Card> privateCardList = convertPrivateCards(privateCards);
        List<String> postflopActionList = new ArrayList<>(Arrays.asList(postflopActions.split(":")));
        if (notChance == null || !notChance) {
            Card innerTurnCard = convertCard(flopCardList, riverCard);
            postflopActionList.add(innerTurnCard.getCard());
            postflopActions += ":" + innerTurnCard.getCard();
            model.addAttribute("postflopActions", postflopActions);
        }
        int player = position.equals("ip") ? 0 : 1;
        TreeMap<String, Double> strategy = null;
        try {
            strategy = postflopService.getStrategy(innerPreflopActions, flopCardList, privateCardList, player, postflopActionList);
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

    private List<Card> convertPrivateCards(String cards) {
        cards = cards.replace('♠', 's');
        cards = cards.replace('♥', 'h');
        cards = cards.replace('♣', 'c');
        cards = cards.replace('♦', 'd');

        List<Card> cardList = new ArrayList<>();
        for (int i = 0; i < cards.length(); i+=2) {
            cardList.add(new Card(cards.substring(i, i+2)));
        }
        Collections.sort(cardList);
        Collections.reverse(cardList);

        return cardList;
    }

    private Card convertCard(List<Card> flopCards, String card) {
        card =  card.replace('♠', 's')
                .replace('♥', 'h')
                .replace('♣', 'c')
                .replace('♦', 'd');

        String flopCardSuits = "";
        for (Card c : flopCards) {
            flopCardSuits += c.getSuit();
        }
        Map<String, String> convertRules = FlopCardConvertUtil.getConvertRule(flopCardSuits);
        return postflopService.convertCard(new Card(card), convertRules);
    }
}
