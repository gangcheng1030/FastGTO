package gangcheng1030.texasholdem.fastgto.po;

import jakarta.persistence.*;

@Entity
@Table(name = "postflop", uniqueConstraints = {@UniqueConstraint(name = "parent_action_idx", columnNames = {"parent", "flop_cards", "preflop_actions", "action"})})
public class Postflop {
    private static final long serialVersionUID = 7230052310725727465L;

    @Id
    private Long id;
    @Column(name = "preflop_actions", columnDefinition = "varchar(256)")
    private String preflopActions;
    @Column(name = "flop_cards", columnDefinition = "char(6)")
    private String flopCards;
    @Column(name = "parent")
    private Long parent;
    @Column(name = "player", columnDefinition = "tinyint")
    private Integer player;
    @Column(name = "node_type", columnDefinition = "tinyint")
    private Integer nodeType;
    @Column(name = "action", columnDefinition = "varchar(32)")
    private String action;
    @Column(name = "strategy", columnDefinition = "varchar(4096)")
    private String strategy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPreflopActions() {
        return preflopActions;
    }

    public void setPreflopActions(String preflopActions) {
        this.preflopActions = preflopActions;
    }

    public String getFlopCards() {
        return flopCards;
    }

    public void setFlopCards(String flopCards) {
        this.flopCards = flopCards;
    }

    public Long getParent() {
        return parent;
    }

    public void setParent(Long parent) {
        this.parent = parent;
    }

    public Integer getPlayer() {
        return player;
    }

    public void setPlayer(Integer player) {
        this.player = player;
    }

    public Integer getNodeType() {
        return nodeType;
    }

    public void setNodeType(Integer nodeType) {
        this.nodeType = nodeType;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    @Override
    public String toString() {
        return "Postflop{" +
                "id=" + id +
                ", preflopActions='" + preflopActions + '\'' +
                ", flopCards='" + flopCards + '\'' +
                ", parent=" + parent +
                ", player=" + player +
                ", nodeType=" + nodeType +
                ", action='" + action + '\'' +
                ", strategy='" + strategy + '\'' +
                '}';
    }
}
