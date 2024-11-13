package gangcheng1030.texasholdem.fastgto.core;

import java.util.Map;
import java.util.TreeMap;

public class PreflopTreeNode {
    private String name;
    private Map<String, PreflopTreeNode> children;
    private String ranges;
    private String action;
    private String position;

    private Double[] rangesArr;

    public PreflopTreeNode() {
        this.name = "";
        this.action = "";
        this.position = "";
        this.children = new TreeMap<>();
        this.ranges = null;
        this.rangesArr = null;
    }
    public PreflopTreeNode(String name, String action, String position) {
        this.name = name;
        this.action = action;
        this.position = position;
        this.children = new TreeMap<>();
        this.ranges = null;
        this.rangesArr = null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, PreflopTreeNode> getChildren() {
        return children;
    }

    public void setChildren(Map<String, PreflopTreeNode> children) {
        this.children = children;
    }

    public void addChild(String name, PreflopTreeNode child) {
        this.children.put(name, child);
    }

    public void setRanges(String ranges) {
        this.ranges = ranges;
        this.rangesArr = PrivateCardsConverter.rangeStr2Weights(ranges);
    }

    public String getRanges() {
        return ranges;
    }

    public Double[] getRangesArr() {
        return rangesArr;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getAction() {
        return action;
    }

    public String getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return "PreflopTreeNode{" +
                "name='" + name + '\'' +
                ", children=" + children +
                ", ranges='" + ranges + '\'' +
                ", action='" + action + '\'' +
                ", position='" + position + '\'' +
                '}';
    }
}