package Service;

import Entities.Sale;
import Entities.VideoGame;
import Repository.ISaleRepository;
import Repository.IVideoGameRepository;

import java.util.List;
import java.util.UUID;

public class SaleService implements ISaleService {

    private final IVideoGameRepository videoGameRepository;
    private final ISaleRepository      saleRepository;

    public SaleService(IVideoGameRepository videoGameRepository,
                       ISaleRepository      saleRepository) {
        this.videoGameRepository = videoGameRepository;
        this.saleRepository      = saleRepository;
    }

    // ─────────────────────────────────────────────
    //  SELL VIDEO GAME  — BR (Image 3 / venderVideojuego)
    // ─────────────────────────────────────────────
    /**
     * Business rules:
     *  1. Search the game by title → must exist (throws if not found → UI Alert).
     *  2. Verify sufficient stock     (throws if insufficient → UI Alert).
     *  3. Reduce stock via sell(qty)  (uses Sellable interface already in PhysicalVideoGame/DigitalVideoGame).
     *  4. Persist the sale to JSON.
     *  5. Return total = calculateFinalPrice() × quantity.
     */
    @Override
    public double sellVideoGame(String title, int quantity) {

        // Rule 1 — game must exist in the catalogue
        VideoGame game = videoGameRepository.findByTitle(title);
        if (game == null) {
            throw new IllegalArgumentException(
                    "Video game not found in the catalogue: " + title);
        }

        // Rule 2 — sufficient stock check (sell() also checks, but we validate first for a clear message)
        if (game.getStock() < quantity) {
            throw new IllegalArgumentException(
                    "Insufficient stock. Available: " + game.getStock()
                            + ", Requested: " + quantity);
        }

        // Rule 3 — reduce stock and get total using the Sellable interface
        double unitPrice = game.calculateFinalPrice();   // applies 25% discount / +$5000 surcharge
        game.setStock(game.getStock() - quantity);
        videoGameRepository.update(title, game);         // persist updated stock

        // Rule 4 — persist the sale
        String saleId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Sale sale = new Sale(saleId, game, quantity, unitPrice);
        saleRepository.save(sale);

        // Rule 5 — return the total
        return sale.getTotal();
    }

    // ─────────────────────────────────────────────
    //  GET ALL SALES
    // ─────────────────────────────────────────────
    @Override
    public List<Sale> getAllSales() {
        return saleRepository.findAll();
    }
}
