
package model;

import java.util.ArrayList;
import java.util.List;

public class Supplier {

    private int id;
    private String fullname;
    private String email;
    private String tel;
    private String VAT;
    private List<Article> articles = new ArrayList<>();

    public Supplier() {
    }

    public Supplier(int id, String fullname, String email,
                    String tel, String VAT) {
        this.id = id;
        this.fullname = fullname;
        this.email = email;
        this.tel = tel;
        this.VAT = VAT;
    }

    public Supplier(String fullname, String email,
                    String tel, String VAT) {
        this.fullname = fullname;
        this.email = email;
        this.tel = tel;
        this.VAT = VAT;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getVAT() {
        return VAT;
    }

    public void setVAT(String VAT) {
        this.VAT = VAT;
    }

    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }

    @Override
    public String toString() {
        return "Supplier{" +
                "id=" + id +
                ", fullname='" + fullname + '\'' +
                ", email='" + email + '\'' +
                ", tel='" + tel + '\'' +
                ", VAT='" + VAT + '\'' +
                '}';
    }
}
