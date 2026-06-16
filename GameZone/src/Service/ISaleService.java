package Service;

import Entities.Sale;
import java.util.List;

public interface ISaleService {
    double sellVideoGame(String title, int quantity);
    List<Sale> getAllSales();
}