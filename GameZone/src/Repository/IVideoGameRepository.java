package Repository;

import Entities.VideoGame;
import java.util.List;

public interface IVideoGameRepository {
    void save(VideoGame videoGame);
    List<VideoGame> findAll();
    VideoGame findByTitle(String title);
    List<VideoGame> findByPlatform(String platform);
    void update(String title, VideoGame updatedVideoGame);
    void delete(String title);
}