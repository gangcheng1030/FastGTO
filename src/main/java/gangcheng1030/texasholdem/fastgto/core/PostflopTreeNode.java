package gangcheng1030.texasholdem.fastgto.core;

import java.util.List;
import java.util.TreeMap;

public class PostflopTreeNode {
    private List<String> actions;
    private TreeMap<String, PostflopTreeNode> childrens;
    private String node_type;
    private Integer player;
    private Strategy strategy;
    private Integer deal_number;
    private TreeMap<String, PostflopTreeNode> dealcards;

    public List<String> getActions() {
        return actions;
    }

    public void setActions(List<String> actions) {
        this.actions = actions;
    }

    public TreeMap<String, PostflopTreeNode> getChildrens() {
        return childrens;
    }

    public void setChildrens(TreeMap<String, PostflopTreeNode> childrens) {
        this.childrens = childrens;
    }

    public String getNode_type() {
        return node_type;
    }

    public void setNode_type(String node_type) {
        this.node_type = node_type;
    }

    public Integer getPlayer() {
        return player;
    }

    public void setPlayer(Integer player) {
        this.player = player;
    }

    public Strategy getStrategy() {
        return strategy;
    }

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    public Integer getDeal_number() {
        return deal_number;
    }

    public void setDeal_number(Integer deal_number) {
        this.deal_number = deal_number;
    }

    public TreeMap<String, PostflopTreeNode> getDealcards() {
        return dealcards;
    }

    public void setDealcards(TreeMap<String, PostflopTreeNode> dealcards) {
        this.dealcards = dealcards;
    }
}