
package model;

import java.time.LocalDateTime;

public class Mvt {

    private int id;
    private LocalDateTime dateTime;
    private String note;
    private TypeMvt type;
    private double qty;
    private Article article;

    public Mvt() {
        this.dateTime = LocalDateTime.now();
    }

    public Mvt(LocalDateTime dateTime, String note,
               TypeMvt type, double qty, Article article) {
        this.dateTime = dateTime;
        this.note = note;
        this.type = type;
        this.qty = qty;
        this.article = article;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public TypeMvt getType() {
        return type;
    }

    public void setType(TypeMvt type) {
        this.type = type;
    }

    public double getQty() {
        return qty;
    }

    public void setQty(double qty) {
        this.qty = qty;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    @Override
    public String toString() {
        return "Mvt{" +
                "id=" + id +
                ", dateTime=" + dateTime +
                ", note='" + note + '\'' +
                ", type=" + type +
                ", qty=" + qty +
                '}';
    }
}
