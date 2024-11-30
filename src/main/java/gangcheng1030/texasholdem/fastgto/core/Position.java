package gangcheng1030.texasholdem.fastgto.core;

public enum Position {
    BN("BN", 0),
    CO("CO", 1),
    HJ("HJ", 2),
    LJ("LJ", 3),
    BB("BB", 7),
    SB("SB", 8)
    ;

    private final String name;
    private final int id;

    Position(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public static Position getByName(String name) {
        for (Position tmp : Position.values()) {
            if (tmp.getName().equals(name)) {
                return tmp;
            }
        }
        return BN;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }
}
