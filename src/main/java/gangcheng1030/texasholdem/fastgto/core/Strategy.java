package gangcheng1030.texasholdem.fastgto.core;

import java.util.List;
import java.util.TreeMap;

public class Strategy {
    private List<String> actions;
    private TreeMap<String, List<Double>> strategy;

    public List<String> getActions() {
        return actions;
    }

    public void setActions(List<String> actions) {
        this.actions = actions;
    }

    public TreeMap<String, List<Double>> getStrategy() {
        return strategy;
    }

    public void setStrategy(TreeMap<String, List<Double>> strategy) {
        this.strategy = strategy;
    }
}
