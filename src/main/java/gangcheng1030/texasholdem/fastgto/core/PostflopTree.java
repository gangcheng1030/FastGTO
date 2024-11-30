package gangcheng1030.texasholdem.fastgto.core;

import gangcheng1030.texasholdem.fastgto.exceptions.ActionNotFoundException;

import java.util.Collections;
import java.util.List;

public class PostflopTree {
    public static final String ACTION_NODE = "action_node";
    public static final String CHANCE_NODE = "chance_node";

    private PostflopTreeNode root;

    public PostflopTree(PostflopTreeNode root) {
        this.root = root;
    }

    public PostflopTree() {
    }

    public PostflopTreeNode search(List<String> actions) {
        if (actions == null || actions.size() == 0) {
            return this.root;
        }

        PostflopTreeNode cur = this.root;
        for (String action : actions) {
            if (cur == null) {
                throw new ActionNotFoundException(action);
            }
            if (cur.getNode_type().equals(ACTION_NODE)) {
                cur = cur.getChildrens().get(action);
            } else if (cur.getNode_type().equals(CHANCE_NODE)) {
                cur = cur.getDealcards().get(action);
            } else {
                throw new ActionNotFoundException(action);
            }
        }

        return cur;
    }
}