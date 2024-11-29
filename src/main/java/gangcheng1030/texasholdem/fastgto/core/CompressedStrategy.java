package gangcheng1030.texasholdem.fastgto.core;

import java.util.List;
import java.util.TreeMap;

public class CompressedStrategy {
    private List<String> actions;
    // 其中的 weight 是将原 weight * 1000 取整得到的
    private TreeMap<String, List<Integer>> strategy;

    public List<String> getActions() {
        return actions;
    }

    public void setActions(List<String> actions) {
        this.actions = actions;
    }

    public TreeMap<String, List<Integer>> getStrategy() {
        return strategy;
    }

    public void setStrategy(TreeMap<String, List<Integer>> strategy) {
        this.strategy = strategy;
    }
}
