package gangcheng1030.texasholdem.fastgto.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;

public class PreflopTreeBuilder {
    public static PreflopTreeNode buildPreflopTree(Path path) throws IOException {
        PreflopTreeNode rootNode = buildPreflopTreeRecursive(path, true);
        rootNode = rewritePreflopTree(rootNode);
        rootNode = rewritePreflopTree2(rootNode);
        return rootNode;
    }

    private static PreflopTreeNode rewritePreflopTree(PreflopTreeNode root) {
        if (root == null) {
            return null;
        }

        if (root.getChildren().isEmpty()) {
            return root;
        }

        PreflopTreeNode betNode = null;
        PreflopTreeNode callNode = null;
        Map<String, PreflopTreeNode> newChildren = new TreeMap<>();
        for (Map.Entry<String, PreflopTreeNode> entry : root.getChildren().entrySet()) {
            entry.getValue().setName(entry.getValue().getPosition() + entry.getValue().getAction());
            if (entry.getKey().contains("bets")) {
                betNode = entry.getValue();
            } else if (entry.getKey().contains("calls")) {
                callNode = entry.getValue();
            }
            newChildren.put(entry.getKey(), entry.getValue());

        }

        for (Map.Entry<String, PreflopTreeNode> entry : root.getChildren().entrySet()) {
            if (!entry.getKey().contains("txt")) {
                continue;
            }
            if (entry.getKey().contains("raise") && betNode != null) {
                betNode.setRanges(entry.getValue().getRanges());
                newChildren.remove(entry.getKey());
            } else if (entry.getKey().contains("call") && callNode != null) {
                callNode.setRanges(entry.getValue().getRanges());
                newChildren.remove(entry.getKey());
            }
        }

        root.setChildren(newChildren);
        for (PreflopTreeNode node : root.getChildren().values()) {
            rewritePreflopTree(node);
        }

        return root;
    }

    // 补充 BBfolds
    private static PreflopTreeNode rewritePreflopTree2(PreflopTreeNode root) {
        if (root == null) {
            return null;
        }

        if (root.getChildren().isEmpty()) {
            return root;
        }

        boolean isBBNode = false;
        boolean containFoldsNode = false;
        for (Map.Entry<String, PreflopTreeNode> entry : root.getChildren().entrySet()) {
            if (entry.getValue().getPosition().equals(Constants.POSITION_BB)) {
                isBBNode = true;
            }
            if (entry.getKey().contains("fold")) {
                containFoldsNode = true;
            }
        }
        if (isBBNode && !containFoldsNode) {
            PreflopTreeNode node = new PreflopTreeNode("BBfold", "fold", "BB");
            root.getChildren().put("BBfold", node);
        }

        for (PreflopTreeNode node : root.getChildren().values()) {
            rewritePreflopTree2(node);
        }

        return root;
    }
    private static PreflopTreeNode buildPreflopTreeRecursive(Path path, boolean isRoot) throws IOException {
        PreflopTreeNode rootNode = new PreflopTreeNode();
        if (!isRoot) {
            String name = path.getName(path.getNameCount() - 1).toString();
            name = rewriteName(name);
            rootNode.setName(name);
            rootNode.setPosition(parsePosition(name));
            rootNode.setAction(parseAction(name));
        }
        if (Files.isDirectory(path)) {
            Files.list(path).forEach(subPath -> {
                try {
                    PreflopTreeNode node =  buildPreflopTreeRecursive(subPath, false);
                    rootNode.addChild(node.getName(), node);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } else {
            String content = new String(Files.readAllBytes(path));
            rootNode.setRanges(content);
        }
        return rootNode;
    }

    private static String rewriteName(String name) {
        return name.replace("(1)", "");
    }
    private static String parseAction(String name) {
        name = name.replace("_", "");
        name = name.replace(".txt", "");
        String position = parsePosition(name);
        if (position.equals(Constants.POSITION_BN)) {
            name = name.substring(1);
        } else {
            name = name.substring(2);
        }
        if (name.endsWith("s")) {
            name = name.substring(0, name.length() - 1);
        }

        return name;
    }

    private static String parsePosition(String name) {
        if (name.startsWith(Constants.POSITION_LJ)) {
            return Constants.POSITION_LJ;
        } else if (name.startsWith(Constants.POSITION_HJ)) {
            return Constants.POSITION_HJ;
        } else if (name.startsWith(Constants.POSITION_CO)) {
            return Constants.POSITION_CO;
        } else if (name.startsWith(Constants.POSITION_SB)) {
            return Constants.POSITION_SB;
        } else if (name.startsWith(Constants.POSITION_BB)) {
            return Constants.POSITION_BB;
        } else if (name.startsWith("B")) {
            return Constants.POSITION_BN;
        }

        throw new RuntimeException("invalid position: " + name);
    }
}