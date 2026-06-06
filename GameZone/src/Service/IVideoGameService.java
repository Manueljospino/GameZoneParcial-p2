package Service;

import Entities.VideoGame;
import java.util.List;

public interface IVideoGameService {
    void addVideoGame(VideoGame videoGame);
    List<VideoGame> getAllVideoGames();
    VideoGame searchByTitle(String title);
    List<VideoGame> searchByPlatform(String platform);
    void updateVideoGame(String title, VideoGame updatedVideoGame);
    void deleteVideoGame(String title);
}
