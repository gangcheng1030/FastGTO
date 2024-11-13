package gangcheng1030.texasholdem.fastgto.core;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class PreflopTree {
    private PreflopTreeNode root = null;

    public PreflopTree() {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            String path = "PioRanges_nlhe_100bb_2.5x_NL200";
            root = PreflopTreeBuilder.buildPreflopTree(classLoader.getResource(path).getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, PreflopTreeNode> getRanges(String[] names) {
        PreflopTreeNode curNode = root;
        for (String name : names) {
            String curPosition = parsePosition(name);
            String curAction = parseAction(name);
            curNode = search(curNode, curPosition, curAction);
            if (curNode == null) {
                return new HashMap<>();
            }
        }

        return curNode.getChildren();
    }

    public Double[] getRaiseRanges(String position, String[] names) {
        return getRaiseRanges(this.root, position, names);
    }

    public Double[] getRaiseRanges(PreflopTreeNode root, String position, String[] names) {
        PreflopTreeNode curNode = root;
        for (String name : names) {
            String curPosition = parsePosition(name);
            String curAction = parseAction(name);
            curNode = search(curNode, curPosition, curAction);
            if (curNode == null) {
                return PrivateCardsConverter.zeroWeights;
            }
        }

        curNode = search(curNode, position, "bet");
        if (curNode == null) {
            return PrivateCardsConverter.zeroWeights;
        }
        return curNode.getRangesArr();
    }

    public Double[] getCallRanges(String position, String[] names) {
        return getCallRanges(this.root, position, names);
    }
    public Double[] getCallRanges(PreflopTreeNode root, String position, String[] names) {
        PreflopTreeNode curNode = root;
        for (String name : names) {
            String curPosition = parsePosition(name);
            String curAction = parseAction(name);
            curNode = search(curNode, curPosition, curAction);
            if (curNode == null) {
                return PrivateCardsConverter.zeroWeights;
            }
        }

        curNode = search(curNode, position, "call");
        if (curNode == null) {
            return PrivateCardsConverter.zeroWeights;
        }
        return curNode.getRangesArr();
    }

    private PreflopTreeNode search(PreflopTreeNode root, String position, String action) {
        if (root.getPosition().equals(position) && root.getAction().contains(action)) {
            return root;
        }

        if (root.getPosition().equals(position) || root.getChildren().isEmpty()) {
            return null;
        }

        for (PreflopTreeNode childNode : root.getChildren().values()) {
            PreflopTreeNode res = search(childNode, position, action);
            if (res != null) {
                return res;
            }
        }

        return null;
    }

    private String parseAction(String name) {
        return name.substring(2).toLowerCase();
    }

    private String parsePosition(String name) {
        return name.substring(0, 2).toUpperCase();
    }
}
