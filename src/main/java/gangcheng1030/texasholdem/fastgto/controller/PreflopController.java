package gangcheng1030.texasholdem.fastgto.controller;

import gangcheng1030.texasholdem.fastgto.core.Constants;
import gangcheng1030.texasholdem.fastgto.core.PreflopTree;
import gangcheng1030.texasholdem.fastgto.core.PreflopTreeNode;
import gangcheng1030.texasholdem.fastgto.core.PrivateCardsConverter;
import gangcheng1030.texasholdem.fastgto.service.PreflopService;
import gangcheng1030.texasholdem.fastgto.util.RandomGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/preflop")
public class PreflopController {
    @Autowired
    private PreflopService preflopService;

    @Autowired
    private PreflopTree preflopTree;

    @GetMapping("/parentAndPosition")
    public String findByParentAndPosition(@RequestParam(value = "parent", required = false) String parent,
                                          @RequestParam(value = "randomNum", required = false) Integer randomNum,
                                          Model model) {
        String[] names = new String[]{};
        if (!StringUtils.isEmpty(parent)) {
            names = parent.split(":");
        }
        if (randomNum == null) {
            randomNum = RandomGenerator.nextInt(100) + 1;
        }
        Map<String, PreflopTreeNode> nodes = preflopTree.getRanges(names);
        Double[] raiseWeights = PrivateCardsConverter.zeroWeights;
        Double[] callWeights = PrivateCardsConverter.zeroWeights;
        List<String> nextActions = new ArrayList<>();
        List<String> nextActionNames = new ArrayList<>();
        for (PreflopTreeNode node : nodes.values()) {
            String next = StringUtils.isEmpty(parent) ? node.getName() : (parent + ":" + node.getName());
            nextActions.add(next);
            nextActionNames.add(node.getName());
            if (node.getAction().equals(Constants.ACTION_RAISE) || node.getAction().contains(Constants.ACTION_BET)) {
                raiseWeights = node.getRangesArr();
            } else if (node.getAction().equals(Constants.ACTION_CALL)) {
                callWeights = node.getRangesArr();
            }
        }
        String[] cards = PrivateCardsConverter.getAllPrivateCardsSummary();
        double randomNumD = randomNum / 100.0;
        for (int i = 0; i < cards.length; i++) {
            if (randomNumD <= raiseWeights[i]) {
                cards[i] += "R";
            } else if (randomNumD <= raiseWeights[i] + callWeights[i]) {
                cards[i] += "C";
            }
        }
        model.addAttribute("raiseWeights", raiseWeights);
        model.addAttribute("callWeights", callWeights);
        model.addAttribute("cards", cards);
        model.addAttribute("nextActions", nextActions);
        model.addAttribute("nextActionNames", nextActionNames);
        model.addAttribute("randomNum", randomNum);
        return "preflop";
    }
}
