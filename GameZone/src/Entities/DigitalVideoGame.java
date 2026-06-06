package Entities;

import Entities.interfaces.Displayable;
import Entities.interfaces.Sellable;

public class DigitalVideoGame extends VideoGame implements Sellable, Displayable {

    private double sizeGB;
    private String downloadPlatform;

    public DigitalVideoGame(String title, double price, String platform, int stock,
                            String genre, double sizeGB, String downloadPlatform) {
        super(title, price, platform, stock, genre);
        this.sizeGB = sizeGB;
        this.downloadPlatform = downloadPlatform;
    }


    public double getSizeGB()             { return sizeGB; }
    public String getDownloadPlatform()   { return downloadPlatform; }


    public void setSizeGB(double sizeGB)                    { this.sizeGB = sizeGB; }
    public void setDownloadPlatform(String downloadPlatform){ this.downloadPlatform = downloadPlatform; }

    @Override
    public double calculateFinalPrice() {
        if (sizeGB > 50) {
            return price + 5000;
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
        return String.format(" [DIGITAL] %s | Plataforma: %s | Precio final: $%.2f | Stock: %d | Tamaño: %.1f GB",
                title, platform, calculateFinalPrice(), stock, sizeGB);
    }

    @Override
    public Object[] toTableRow() {
        return new Object[]{ title, "Digital", platform, genre,
                calculateFinalPrice(), stock, sizeGB + " GB", downloadPlatform };
    }

    @Override
    public String toString() {
        return "DigitalVideoGame{" +
                "title='" + title + '\'' +
                ", price=" + price +
                ", platform='" + platform + '\'' +
                ", stock=" + stock +
                ", genre='" + genre + '\'' +
                ", sizeGB=" + sizeGB +
                ", downloadPlatform='" + downloadPlatform + '\'' +
                ", finalPrice=" + calculateFinalPrice() +
                '}';
    }
}