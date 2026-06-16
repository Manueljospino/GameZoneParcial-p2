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

    @Override
    public double sellVideoGame(String title, int quantity) {

        VideoGame game = videoGameRepository.findByTitle(title);
        if (game == null) {
            throw new IllegalArgumentException(
                    "Videojuego no encontrado en el catálogo: " + title);
        }

        if (game.getStock() < quantity) {
            throw new IllegalArgumentException(
                    "Stock insuficiente. Disponible: " + game.getStock()
                            + ", Solicitado: " + quantity);
        }

        double unitPrice = game.calculateFinalPrice();
        game.setStock(game.getStock() - quantity);
        videoGameRepository.update(title, game);

        String saleId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Sale sale = new Sale(saleId, game, quantity, unitPrice);
        saleRepository.save(sale);

        return sale.getTotal();
    }

    @Override
    public List<Sale> getAllSales() {
        return saleRepository.findAll();
    }
}