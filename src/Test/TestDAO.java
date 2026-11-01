
package Test;

import config.MyConnexion;
import controler.ArticleDAO;
import controler.CategoryDAO;
import controler.MvtDAO;
import controler.SupplierDAO;
import controler.ArticleSupplierDAO;

import model.Article;
import model.Category;
import model.Mvt;
import model.Supplier;
import model.TypeMvt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.UUID;

public class TestDAO {

    public static void main(String[] args) {

        CategoryDAO categoryDAO = new CategoryDAO();
        ArticleDAO articleDAO = new ArticleDAO();
        SupplierDAO supplierDAO = new SupplierDAO();
        MvtDAO mvtDAO = new MvtDAO();
        ArticleSupplierDAO associationDAO = new ArticleSupplierDAO();

        Connection cn = MyConnexion.getInstance().getConnection();

        try {
            System.out.println("==================================");
            System.out.println("   TEST CRUD - GESTION DE STOCK");
            System.out.println("==================================");

            // 1. CATEGORY
            System.out.println("\n--- CATEGORY ---");

            String categoryName = "TEST-CAT-" + UUID.randomUUID();

            Category category = new Category();
            category.setName(categoryName);
            category.setDescription("Categorie pour test CRUD");

            if (!categoryDAO.insert(category)) {
                throw new RuntimeException("Insertion categorie echouee");
            }

            category = findCategory(cn, categoryName);

            System.out.println("INSERT Category : OK");
            System.out.println("Categorie ID : " + category.getId());

            category.setDescription("Description modifiee");

            System.out.println("UPDATE Category : "
                    + categoryDAO.update(category, category.getId()));

            // 2. SUPPLIER
            System.out.println("\n--- SUPPLIER ---");

            String email = "test-" + UUID.randomUUID() + "@example.com";

            Supplier supplier = new Supplier();
            supplier.setFullname("Fournisseur Test");
            supplier.setEmail(email);
            supplier.setTel("20111222");
            supplier.setVAT("VAT-" + UUID.randomUUID());

            if (!supplierDAO.insert(supplier)) {
                throw new RuntimeException("Insertion fournisseur echouee");
            }

            supplier = findSupplier(cn, email);

            System.out.println("INSERT Supplier : OK");
            System.out.println("Fournisseur ID : " + supplier.getId());

            supplier.setTel("20999888");

            System.out.println("UPDATE Supplier : "
                    + supplierDAO.update(supplier, supplier.getId()));

            // 3. ARTICLE
            System.out.println("\n--- ARTICLE ---");

            String label = "Article-Test-" + UUID.randomUUID();

            Article article = new Article(
                    label,
                    "Article pour test CRUD",
                    30,
                    5,
                    100,
                    25.000,
                    39.900,
                    category
            );

            if (!articleDAO.insert(article)) {
                throw new RuntimeException("Insertion article echouee");
            }

            System.out.println("INSERT Article : OK");
            System.out.println("Article UUID : " + article.getUid());

            // L'article Java n'a pas de getId().
            // On récupère sa clé primaire SQL par son UUID.
            int articleId = findArticleIdByUid(
                    cn, article.getUid().toString());

            System.out.println("Article ID SQL : " + articleId);

            // UPDATE ARTICLE : l'ID SQL est fourni séparément
            article.setQty(35);
            article.setPrixVente(42.500);

            System.out.println("UPDATE Article : "
                    + articleDAO.update(article, articleId));

            // 4. ARTICLE_SUPPLIER
            System.out.println("\n--- ARTICLE_SUPPLIER ---");

            System.out.println("ASSIGN : "
                    + associationDAO.assign(articleId, supplier.getId()));

            System.out.println("Fournisseurs de l'article :");

            for (Supplier s :
                    associationDAO.findSuppliersByArticleId(articleId)) {
                System.out.println(s);
            }

            // 5. MOUVEMENT
            System.out.println("\n--- MVT ---");

            Mvt mouvement = new Mvt();
            mouvement.setDateTime(LocalDateTime.now());
            mouvement.setNote("Mouvement test");
            mouvement.setType(TypeMvt.INPUT);
            mouvement.setQty(10);

            /*
             * Important :
             * MvtDAO doit utiliser la clé article.id SQL récupérée
             * via l'UUID, ou proposer une méthode insert(mvt, articleId).
             * Le modèle Article ne contient plus de getId().
             */
            mouvement.setArticle(article);

            // Vérifier que MvtDAO utilise bien articleId en SQL.
            // Voir la remarque sous ce code si cette ligne échoue.
            System.out.println("INSERT Mvt : "
                    + mvtDAO.insert(mouvement));

            System.out.println("Mouvements de l'article :");
            for (Mvt m : mvtDAO.findByArticleId(articleId)) {
                System.out.println(m);
            }

            // 6. NETTOYAGE
            System.out.println("\n--- NETTOYAGE ---");

            for (Mvt m : mvtDAO.findByArticleId(articleId)) {
                System.out.println("DELETE Mvt : "
                        + mvtDAO.delete(m.getId()));
            }

            System.out.println("REMOVE Association : "
                    + associationDAO.remove(articleId, supplier.getId()));

            System.out.println("DELETE Article : "
                    + articleDAO.delete(articleId));

            System.out.println("DELETE Category : "
                    + categoryDAO.delete(category.getId()));

            System.out.println("DELETE Supplier : "
                    + supplierDAO.delete(supplier.getId()));

            System.out.println("\nTests termines.");

        } catch (Exception e) {
            System.err.println("Erreur pendant les tests : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static Category findCategory(
            Connection cn, String name) throws Exception {

        String sql = """
                SELECT id, name, description
                FROM category
                WHERE name = ?
                """;

        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, name);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Category c = new Category();
                    c.setId(rs.getInt("id"));
                    c.setName(rs.getString("name"));
                    c.setDescription(rs.getString("description"));
                    return c;
                }
            }
        }

        throw new Exception("Categorie introuvable : " + name);
    }

    private static Supplier findSupplier(
            Connection cn, String email) throws Exception {

        String sql = """
                SELECT id, fullname, email, tel, VAT
                FROM supplier
                WHERE email = ?
                """;

        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Supplier s = new Supplier();
                    s.setId(rs.getInt("id"));
                    s.setFullname(rs.getString("fullname"));
                    s.setEmail(rs.getString("email"));
                    s.setTel(rs.getString("tel"));
                    s.setVAT(rs.getString("VAT"));
                    return s;
                }
            }
        }

        throw new Exception("Fournisseur introuvable : " + email);
    }

    private static int findArticleIdByUid(
            Connection cn, String uid) throws Exception {

        String sql = "SELECT id FROM article WHERE uid = ?";

        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, uid);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }

        throw new Exception("Article introuvable pour UUID : " + uid);
    }
}
