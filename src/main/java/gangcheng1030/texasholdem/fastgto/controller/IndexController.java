package gangcheng1030.texasholdem.fastgto.controller;

import gangcheng1030.texasholdem.fastgto.util.RandomGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class IndexController {
    @RequestMapping("")
    public String index(Model model) {
        return "index";
    }

    @RequestMapping("/index.html")
    public String indexHtml(Model model) {
        return "index";
    }

    @RequestMapping("/index_6_max_100BB")
    public String index6Max100BB(Model model) {
        model.addAttribute("randomNum", RandomGenerator.nextInt(100) + 1);
        return "index_6_max_100BB";
    }
}
