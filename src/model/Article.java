
package model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Article {

    private UUID uid;
    private String label;
    private String description;
    private double qty;
    private double minLimite;
    private double maxLimit;
    private double prixAchat;
    private double prixVente;

    private Category category;
    private List<Mvt> mouvements = new ArrayList<>();
    private List<Supplier> suppliers = new ArrayList<>();

    public Article() {
        this.uid = UUID.randomUUID();
    }

    public Article(String label, String description, double qty,
                   double minLimite, double maxLimit,
                   double prixAchat, double prixVente,
                   Category category) {
        this();
        this.label = label;
        this.description = description;
        this.qty = qty;
        this.minLimite = minLimite;
        this.maxLimit = maxLimit;
        this.prixAchat = prixAchat;
        this.prixVente = prixVente;
        this.category = category;
    }

    public UUID getUid() {
        return uid;
    }

    public void setUid(UUID uid) {
        this.uid = uid;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getQty() {
        return qty;
    }

    public void setQty(double qty) {
        this.qty = qty;
    }

    public double getMinLimite() {
        return minLimite;
    }

    public void setMinLimite(double minLimite) {
        this.minLimite = minLimite;
    }

    public double getMaxLimit() {
        return maxLimit;
    }

    public void setMaxLimit(double maxLimit) {
        this.maxLimit = maxLimit;
    }

    public double getPrixAchat() {
        return prixAchat;
    }

    public void setPrixAchat(double prixAchat) {
        this.prixAchat = prixAchat;
    }

    public double getPrixVente() {
        return prixVente;
    }

    public void setPrixVente(double prixVente) {
        this.prixVente = prixVente;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<Mvt> getMouvements() {
        return mouvements;
    }

    public void setMouvements(List<Mvt> mouvements) {
        this.mouvements = mouvements;
    }

    public List<Supplier> getSuppliers() {
        return suppliers;
    }

    public void setSuppliers(List<Supplier> suppliers) {
        this.suppliers = suppliers;
    }

    @Override
    public String toString() {
        return "Article{" +
                "uid=" + uid +
                ", label='" + label + '\'' +
                ", qty=" + qty +
                ", prixAchat=" + prixAchat +
                ", prixVente=" + prixVente +
                '}';
    }
}
