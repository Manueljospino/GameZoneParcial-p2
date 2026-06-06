package Service;

import Entities.VideoGame;
import Repository.IVideoGameRepository;

import java.util.List;

public class VideoGameService implements IVideoGameService {

    private final IVideoGameRepository repository;

    public VideoGameService(IVideoGameRepository repository) {
        this.repository = repository;
    }

    @Override
    public void addVideoGame(VideoGame videoGame) {
        if (videoGame.getTitle() == null || videoGame.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("El título no puede ser nulo o vacío.");
        }
        if (videoGame.getPrice() <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a 0.");
        }
        if (videoGame.getStock() < 0) {
            throw new IllegalArgumentException("El stock debe ser mayor o igual a 0.");
        }
        repository.save(videoGame);
    }

    @Override
    public List<VideoGame> getAllVideoGames() {
        return repository.findAll();
    }

    @Override
    public VideoGame searchByTitle(String title) {
        return repository.findByTitle(title);
    }

    @Override
    public List<VideoGame> searchByPlatform(String platform) {
        return repository.findByPlatform(platform);
    }

    @Override
    public void updateVideoGame(String title, VideoGame updatedVideoGame) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("El título a buscar no puede ser nulo o vacío.");
        }
        if (updatedVideoGame.getPrice() <= 0) {
            throw new IllegalArgumentException("El precio actualizado debe ser mayor a 0.");
        }
        if (updatedVideoGame.getStock() < 0) {
            throw new IllegalArgumentException("El stock actualizado debe ser mayor o igual a 0.");
        }
        repository.update(title, updatedVideoGame);
    }

    @Override
    public void deleteVideoGame(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("El título no puede ser nulo o vacío.");
        }
        repository.delete(title);
    }
}