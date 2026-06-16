package Repository;

import Entities.Sale;
import java.util.List;

public interface ISaleRepository {
    void save(Sale sale);
    List<Sale> findAll();
}
