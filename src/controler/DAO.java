
package controler;

import java.util.ArrayList;

public interface DAO<T> {

    boolean insert(T o);

    boolean update(T o, int id);

    boolean delete(int id);

    ArrayList<T> select();
}
