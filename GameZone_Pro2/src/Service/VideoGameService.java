package Service;

import Entities.VideoGame;
import Repository.IVideoGameRepository;

import java.util.List;

public class VideoGameService implements IVideoGameService {

    private final IVideoGameRepository repository;

    public VideoGameService(IVideoGameRepository repository) {
        this.repository = repository;
    }

    // ─────────────────────────────────────────────
    //  ADD VIDEO GAME  — BR validations (Image 3)
    // ─────────────────────────────────────────────
    @Override
    public void addVideoGame(VideoGame videoGame) {
        // BR-01: title must not be null or empty
        if (videoGame.getTitle() == null || videoGame.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty.");
        }
        // BR-02: price must be greater than 0
        if (videoGame.getPrice() <= 0) {
            throw new IllegalArgumentException("Price must be greater than 0.");
        }
        // BR-03: stock must be >= 0
        if (videoGame.getStock() < 0) {
            throw new IllegalArgumentException("Stock must be 0 or greater.");
        }
        // Duplicate-title check is delegated to the repository (throws → UI alert)
        repository.save(videoGame);
    }

    // ─────────────────────────────────────────────
    //  GET ALL
    // ─────────────────────────────────────────────
    @Override
    public List<VideoGame> getAllVideoGames() {
        return repository.findAll();
    }

    // ─────────────────────────────────────────────
    //  SEARCH BY TITLE  (case-insensitive)
    // ─────────────────────────────────────────────
    @Override
    public VideoGame searchByTitle(String title) {
        return repository.findByTitle(title);
    }

    // ─────────────────────────────────────────────
    //  SEARCH BY PLATFORM  (case-insensitive)
    // ─────────────────────────────────────────────
    @Override
    public List<VideoGame> searchByPlatform(String platform) {
        return repository.findByPlatform(platform);
    }

    // ─────────────────────────────────────────────
    //  UPDATE
    // ─────────────────────────────────────────────
    @Override
    public void updateVideoGame(String title, VideoGame updatedVideoGame) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title to search cannot be null or empty.");
        }
        if (updatedVideoGame.getPrice() <= 0) {
            throw new IllegalArgumentException("Updated price must be greater than 0.");
        }
        if (updatedVideoGame.getStock() < 0) {
            throw new IllegalArgumentException("Updated stock must be 0 or greater.");
        }
        repository.update(title, updatedVideoGame);
    }

    // ─────────────────────────────────────────────
    //  DELETE
    // ─────────────────────────────────────────────
    @Override
    public void deleteVideoGame(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty.");
        }
        repository.delete(title);
    }
}
