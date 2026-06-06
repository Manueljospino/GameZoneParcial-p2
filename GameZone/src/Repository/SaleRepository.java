package Repository;

import Entities.DigitalVideoGame;
import Entities.PhysicalVideoGame;
import Entities.Sale;
import Entities.VideoGame;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SaleRepository implements ISaleRepository {

    private static final String FILE_PATH = "data/sales.json";

    @Override
    public void save(Sale sale) {
        List<Sale> sales = findAll();
        sales.add(sale);
        writeAll(sales);
    }

    @Override
    public List<Sale> findAll() {
        List<Sale> sales = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) return sales;

        try (FileReader reader = new FileReader(file)) {
            JSONArray array = (JSONArray) new JSONParser().parse(reader);
            for (Object obj : array) {
                Sale sale = parseSale((JSONObject) obj);
                if (sale != null) sales.add(sale);
            }
        } catch (Exception e) {
            System.err.println("Error al leer ventas.json: " + e.getMessage());
        }
        return sales;
    }

    @SuppressWarnings("unchecked")
    private void writeAll(List<Sale> sales) {
        new File("data").mkdirs();
        JSONArray array = new JSONArray();
        for (Sale s : sales) array.add(toJson(s));
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            writer.write(array.toJSONString());
        } catch (IOException e) {
            System.err.println("Error al guardar ventas.json: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private JSONObject toJson(Sale s) {
        JSONObject json = new JSONObject();
        json.put("id",             s.getId());
        json.put("videoGameTitle", s.getVideoGame().getTitle());
        json.put("videoGameType",  s.getVideoGame() instanceof DigitalVideoGame ? "digital" : "physical");
        json.put("quantity",       s.getQuantity());
        json.put("unitPrice",      s.getUnitPrice());
        json.put("total",          s.getTotal());
        json.put("saleDate",       s.getSaleDate().toString());
        return json;
    }

    private Sale parseSale(JSONObject json) {
        try {
            String id        = (String) json.get("id");
            String gameTitle = (String) json.get("videoGameTitle");
            String gameType  = (String) json.get("videoGameType");
            int    quantity  = ((Number) json.get("quantity")).intValue();
            double unitPrice = ((Number) json.get("unitPrice")).doubleValue();

            VideoGame stub;
            if ("digital".equals(gameType)) {
                stub = new DigitalVideoGame(gameTitle, unitPrice, "", 0, "", 0, "");
            } else {
                stub = new PhysicalVideoGame(gameTitle, unitPrice, "", 0, "", "nuevo", "");
            }

            return new Sale(id, stub, quantity, unitPrice);
        } catch (Exception e) {
            System.err.println("Error al procesar registro de venta: " + e.getMessage());
            return null;
        }
    }
}