package Entities;


import Entities.interfaces.Displayable;
import Entities.interfaces.Sellable;

public class PhysicalVideoGame extends VideoGame implements Sellable, Displayable {

    private String condition;    // "nuevo" o "usado"
    private String distributor;

    public PhysicalVideoGame(String title, double price, String platform, int stock,
                             String genre, String condition, String distributor) {
        super(title, price, platform, stock, genre);
        this.condition = condition;
        this.distributor = distributor;
    }


    public String getCondition()    { return condition; }
    public String getDistributor()  { return distributor; }


    public void setCondition(String condition)      { this.condition = condition; }
    public void setDistributor(String distributor)  { this.distributor = distributor; }

    @Override
    public double calculateFinalPrice() {
        if ("usado".equalsIgnoreCase(condition)) {
            return price * 0.75; // 25% de descuento
        }
        return price;
    }

    @Override
    public double sell(int qty) {
        if (qty > stock) {
            throw new IllegalArgumentException("Stock insuficiente. Disponible: " + stock);
        }
        stock -= qty;
        return calculateFinalPrice() * qty;
    }

    @Override
    public String getDisplayInfo() {
        return String.format("📦 [FÍSICO] %s | Plataforma: %s | Precio final: $%.2f | Stock: %d | Condición: %s",
                title, platform, calculateFinalPrice(), stock, condition);
    }

    @Override
    public Object[] toTableRow() {
        return new Object[]{ title, "Físico", platform, genre,
                calculateFinalPrice(), stock, condition, distributor };
    }

    @Override
    public String toString() {
        return "PhysicalVideoGame{" +
                "title='" + title + '\'' +
                ", price=" + price +
                ", platform='" + platform + '\'' +
                ", stock=" + stock +
                ", genre='" + genre + '\'' +
                ", condition='" + condition + '\'' +
                ", distributor='" + distributor + '\'' +
                ", finalPrice=" + calculateFinalPrice() +
                '}';
    }
}
