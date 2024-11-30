package gangcheng1030.texasholdem.fastgto.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Strategy {
    private List<String> actions;
    private TreeMap<String, List<Double>> strategy;

    public CompressedStrategy compress() {
        CompressedStrategy compressedStrategy = new CompressedStrategy();
        compressedStrategy.setActions(actions);
        TreeMap<String, List<Integer>> cstrategy = new TreeMap<>();
        for (Map.Entry<String, List<Double>> entry : strategy.entrySet()) {
            String key = entry.getKey();
            List<Integer> cweights = new ArrayList<>();
            for (Double weight : entry.getValue()) {
                cweights.add((int)(weight * Constants.WEIGHTS_RADIX));
            }
            cstrategy.put(key, cweights);
        }
        compressedStrategy.setStrategy(cstrategy);
        return compressedStrategy;
    }

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
