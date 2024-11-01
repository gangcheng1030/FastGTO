package gangcheng1030.texasholdem.fastgto.controller;

import gangcheng1030.texasholdem.fastgto.service.PreflopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/preflop")
public class PreflopController {
    @Autowired
    private PreflopService preflopService;

    @GetMapping("/parentAndPosition")
    public String findByParentAndPosition(@RequestParam("parent") Integer parent,
                                          @RequestParam("position") String position,
                                          Model model) {
        double[] ratios = {0.2, 0.3, 0.5};
        model.addAttribute("ratios", ratios);
        double[] ratios2 = {0.3, 0.4, 0.3};
        model.addAttribute("ratios2", ratios2);
        String[] cards = {"AA", "AKs", "AKo"};
        model.addAttribute("cards", cards);
        return "preflop";
    }
}
