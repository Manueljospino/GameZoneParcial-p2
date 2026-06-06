package Entities;

public abstract class VideoGame {
   private String title;
   private double basePrice;
   private int stock;
   private  String platform;

    public VideoGame(String title, double basePrice, int stock, String platform) {
        this.title = title;
        this.basePrice = basePrice;
        this.stock = stock;
        this.platform = platform;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    @Override
    public String toString() {
        return "VideoGame{" +
                "title='" + title + '\'' +
                ", basePrice=" + basePrice +
                ", stock=" + stock +
                ", platform='" + platform + '\'' +
                '}';
    }

    public abstract double calculateFinalPrice();
}
