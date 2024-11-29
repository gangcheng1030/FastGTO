package gangcheng1030.texasholdem.fastgto.core;


public enum NodeType {
    ACTION_NODE("action_node", 0),
    CHANCE_NODE("chance_node", 1)
    ;

    private final String type;
    private final int id;

    NodeType(String type, int id) {
        this.type = type;
        this.id = id;
    }

    public static NodeType getByType(String type) {
        for (NodeType tmp : NodeType.values()) {
            if (tmp.getType().equals(type)) {
                return tmp;
            }
        }
        return ACTION_NODE;
    }

    public static NodeType getById(int id) {
        for (NodeType tmp : NodeType.values()) {
            if (tmp.getId() == id) {
                return tmp;
            }
        }
        return ACTION_NODE;
    }

    public static int typeToId(String type) {
        return getByType(type).getId();
    }

    public String getType() {
        return type;
    }

    public int getId() {
        return id;
    }
}
