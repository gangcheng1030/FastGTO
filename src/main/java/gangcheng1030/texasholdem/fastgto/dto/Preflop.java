package gangcheng1030.texasholdem.fastgto.dto;

import jakarta.persistence.*;

@Entity
public class Preflop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(columnDefinition = "CHAR(4)")
    private String position;
    @Column(columnDefinition = "CHAR(5)")
    private String action;
    private Integer parent;
    @Column(columnDefinition = "VARCHAR(3000)")
    private String ranges;

    public Preflop() {
    }

    public Preflop(String position, String action, Integer parent, String ranges) {
        this.position = position;
        this.action = action;
        this.parent = parent;
        this.ranges = ranges;
    }

    @Override
    public String toString() {
        return "Preflop{" +
                "id=" + id +
                ", position='" + position + '\'' +
                ", action='" + action + '\'' +
                ", parent=" + parent +
                ", ranges='" + ranges + '\'' +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Integer getParent() {
        return parent;
    }

    public void setParent(Integer parent) {
        this.parent = parent;
    }

    public String getRanges() {
        return ranges;
    }

    public void setRanges(String ranges) {
        this.ranges = ranges;
    }
}
