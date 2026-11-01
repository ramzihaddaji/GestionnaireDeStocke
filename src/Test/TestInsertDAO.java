
package Test;

import config.MyConnexion;
import controler.CategoryDAO;
import controler.ArticleDAO;
import controler.SupplierDAO;
import controler.MvtDAO;
import controler.ArticleSupplierDAO;

import model.Category;
import model.Article;
import model.Supplier;
import model.Mvt;
import model.TypeMvt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.UUID;

public class TestInsertDAO {

    public static void main(String[] args) {

        CategoryDAO categoryDAO = new CategoryDAO();
        ArticleDAO articleDAO = new ArticleDAO();
        SupplierDAO supplierDAO = new SupplierDAO();
        MvtDAO mvtDAO = new MvtDAO();
        ArticleSupplierDAO associationDAO = new ArticleSupplierDAO();

        Connection connection =
                MyConnexion.getInstance().getConnection();

        try {
            System.out.println("=== DEBUT DES TESTS INSERT ===");

            // 1. CATEGORY
            String categoryName = "TEST-" + UUID.randomUUID();

            Category category = new Category();
            category.setName(categoryName);
            category.setDescription("Categorie informatique de test");

            if (!categoryDAO.insert(category)) {
                throw new RuntimeException("Insertion categorie echouee");
            }

            category = findCategory(connection, categoryName);
            System.out.println("Categorie inseree : " + category);

            // 2. SOUS-CATEGORY
            String subCategoryName = "TEST-SOUS-" + UUID.randomUUID();

            Category subCategory = new Category();
            subCategory.setName(subCategoryName);
            subCategory.setDescription("Sous-categorie de test");
            subCategory.setParent(category);

            if (!categoryDAO.insert(subCategory)) {
                throw new RuntimeException(
                        "Insertion sous-categorie echouee");
            }

            subCategory = findCategory(connection, subCategoryName);
            System.out.println("Sous-categorie inseree : " + subCategory);

            // 3. SUPPLIER
            String email =
                    "test-" + UUID.randomUUID() + "@example.com";

            Supplier supplier = new Supplier();
            supplier.setFullname("Fournisseur Test");
            supplier.setEmail(email);
            supplier.setTel("20111222");
            supplier.setVAT("VAT-" + UUID.randomUUID());

            if (!supplierDAO.insert(supplier)) {
                throw new RuntimeException(
                        "Insertion fournisseur echouee");
            }

            supplier = findSupplier(connection, email);
            System.out.println("Fournisseur insere : " + supplier);

            // 4. ARTICLE
            String label = "Article Test " + UUID.randomUUID();

            Article article = new Article(
                    label,
                    "Article pour tester les insertions",
                    30,
                    5,
                    100,
                    25.000,
                    39.900,
                    subCategory
            );

            if (!articleDAO.insert(article)) {
                throw new RuntimeException("Insertion article echouee");
            }

            // L'UUID est conserve dans le modèle.
            System.out.println("Article insere, UUID : "
                    + article.getUid());

            // Recuperer la cle numerique SQL necessaire aux FK existantes.
            int articleId = findArticleIdByUid(
                    connection, article.getUid().toString());

            System.out.println("ID SQL de l'article : " + articleId);

            // 5. ARTICLE_SUPPLIER
            if (!associationDAO.assign(articleId, supplier.getId())) {
                throw new RuntimeException(
                        "Insertion association article-fournisseur echouee");
            }

            System.out.println(
                    "Association article-fournisseur inseree.");

            // 6. MVT
            Mvt mvt = new Mvt();
            mvt.setDateTime(LocalDateTime.now());
            mvt.setNote("Entree initiale de stock pour test");
            mvt.setType(TypeMvt.INPUT);
            mvt.setQty(30);

            /*
             * Important :
             * Ce bloc fonctionne seulement si MvtDAO sait utiliser
             * un Article et retrouver son article.id SQL.
             *
             * Si MvtDAO insere mvt.getArticle().getId(), cela ne compilera
             * pas puisque Article ne possède plus getId().
             * Dans ce cas, il faut adapter MvtDAO pour recevoir articleId
             * ou rechercher l'ID SQL à partir de l'UUID.
             */
            Article articlePourMvt = articleDAO.findByUid(article.getUid());

            if (articlePourMvt == null) {
                throw new RuntimeException(
                        "Article introuvable via son UUID");
            }

            mvt.setArticle(articlePourMvt);

            if (!mvtDAO.insert(mvt)) {
                throw new RuntimeException(
                        "Insertion mouvement echouee. " +
                                "Verifier MvtDAO et sa cle etrangere.");
            }

            System.out.println("Mouvement de stock insere.");
            System.out.println("=== TOUS LES TESTS INSERT SONT TERMINES ===");

        } catch (Exception e) {
            System.err.println("Erreur : " + e.getMessage());
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
