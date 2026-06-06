package Repository;

import Entities.DigitalVideoGame;
import Entities.PhysicalVideoGame;
import Entities.VideoGame;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class VideoGameRepository implements IVideoGameRepository {

    private static final String FILE_PATH = "data/videogames.json";

    // ─────────────────────────────────────────────
    //  SAVE  (Create)
    // ─────────────────────────────────────────────
    @Override
    public void save(VideoGame videoGame) {
        List<VideoGame> games = findAll();

        // Duplicate title check (case-insensitive) → throws for UI alert
        for (VideoGame g : games) {
            if (g.getTitle().equalsIgnoreCase(videoGame.getTitle())) {
                throw new IllegalArgumentException("El videojuego ya existe en el catálogo");
            }
        }

        games.add(videoGame);
        writeAll(games);
    }

    // ─────────────────────────────────────────────
    //  FIND ALL  (Read)
    // ─────────────────────────────────────────────
    @Override
    public List<VideoGame> findAll() {
        List<VideoGame> games = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) return games;

        try (FileReader reader = new FileReader(file)) {
            JSONArray array = (JSONArray) new JSONParser().parse(reader);
            for (Object obj : array) {
                VideoGame game = parseVideoGame((JSONObject) obj);
                if (game != null) games.add(game);
            }
        } catch (Exception e) {
            System.err.println("Error reading videogames.json: " + e.getMessage());
        }
        return games;
    }

    // ─────────────────────────────────────────────
    //  FIND BY TITLE  (case-insensitive)
    // ─────────────────────────────────────────────
    @Override
    public VideoGame findByTitle(String title) {
        for (VideoGame g : findAll()) {
            if (g.getTitle().equalsIgnoreCase(title)) return g;
        }
        return null;
    }

    // ─────────────────────────────────────────────
    //  FIND BY PLATFORM  (case-insensitive)
    // ─────────────────────────────────────────────
    @Override
    public List<VideoGame> findByPlatform(String platform) {
        List<VideoGame> result = new ArrayList<>();
        for (VideoGame g : findAll()) {
            if (g.getPlatform().equalsIgnoreCase(platform)) result.add(g);
        }
        return result.isEmpty() ? null : result;
    }

    // ─────────────────────────────────────────────
    //  UPDATE
    // ─────────────────────────────────────────────
    @Override
    public void update(String title, VideoGame updatedVideoGame) {
        List<VideoGame> games = findAll();
        boolean found = false;
        for (int i = 0; i < games.size(); i++) {
            if (games.get(i).getTitle().equalsIgnoreCase(title)) {
                games.set(i, updatedVideoGame);
                found = true;
                break;
            }
        }
        if (!found) throw new IllegalArgumentException("Video game not found: " + title);
        writeAll(games);
    }

    // ─────────────────────────────────────────────
    //  DELETE
    // ─────────────────────────────────────────────
    @Override
    public void delete(String title) {
        List<VideoGame> games = findAll();
        boolean removed = games.removeIf(g -> g.getTitle().equalsIgnoreCase(title));
        if (!removed) throw new IllegalArgumentException("Video game not found: " + title);
        writeAll(games);
    }

    // ─────────────────────────────────────────────
    //  PRIVATE HELPERS
    // ─────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private void writeAll(List<VideoGame> games) {
        new File("data").mkdirs();
        JSONArray array = new JSONArray();
        for (VideoGame g : games) array.add(toJson(g));
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            writer.write(array.toJSONString());
        } catch (IOException e) {
            System.err.println("Error writing videogames.json: " + e.getMessage());
        }
    }

    /** VideoGame → JSONObject. Fields match exactly the entity constructors. */
    @SuppressWarnings("unchecked")
    private JSONObject toJson(VideoGame g) {
        JSONObject json = new JSONObject();
        // Common fields (VideoGame)
        json.put("type",     g instanceof DigitalVideoGame ? "digital" : "physical");
        json.put("title",    g.getTitle());
        json.put("price",    g.getPrice());        // field is "price" in the entity
        json.put("platform", g.getPlatform());
        json.put("stock",    g.getStock());
        json.put("genre",    g.getGenre());

        // Subclass-specific fields
        if (g instanceof DigitalVideoGame dg) {
            json.put("sizeGB",           dg.getSizeGB());
            json.put("downloadPlatform", dg.getDownloadPlatform());
        } else if (g instanceof PhysicalVideoGame pg) {
            json.put("condition",   pg.getCondition());
            json.put("distributor", pg.getDistributor());
        }
        return json;
    }

    /** JSONObject → correct VideoGame subclass using exact constructor signatures. */
    private VideoGame parseVideoGame(JSONObject json) {
        try {
            String type     = (String) json.get("type");
            String title    = (String) json.get("title");
            double price    = ((Number) json.get("price")).doubleValue();
            String platform = (String) json.get("platform");
            int    stock    = ((Number) json.get("stock")).intValue();
            String genre    = (String) json.get("genre");

            if ("digital".equals(type)) {
                // DigitalVideoGame(String title, double price, String platform,
                //                  int stock, String genre, double sizeGB, String downloadPlatform)
                double sizeGB           = ((Number) json.get("sizeGB")).doubleValue();
                String downloadPlatform = (String) json.get("downloadPlatform");
                return new DigitalVideoGame(title, price, platform, stock, genre, sizeGB, downloadPlatform);

            } else {
                // PhysicalVideoGame(String title, double price, String platform,
                //                   int stock, String genre, String condition, String distributor)
                String condition   = (String) json.get("condition");
                String distributor = (String) json.get("distributor");
                return new PhysicalVideoGame(title, price, platform, stock, genre, condition, distributor);
            }
        } catch (Exception e) {
            System.err.println("Error parsing game entry: " + e.getMessage());
            return null;
        }
    }
}
