package Presentation;

import Entities.DigitalVideoGame;
import Entities.PhysicalVideoGame;
import Entities.Sale;
import Entities.VideoGame;
import Repository.SaleRepository;
import Repository.VideoGameRepository;
import Service.SaleService;
import Service.VideoGameService;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;

public class MenuPrincipal extends Application {

    // ── Services ──────────────────────────────────────────────────────────
    private final VideoGameRepository videoGameRepo = new VideoGameRepository();
    private final SaleRepository      saleRepo      = new SaleRepository();
    private final VideoGameService    gameService   = new VideoGameService(videoGameRepo);
    private final SaleService         saleService   = new SaleService(videoGameRepo, saleRepo);

    private TableView<VideoGame> tableView;

    // ═════════════════════════════════════════════════════════════════════
    //  START
    // ═════════════════════════════════════════════════════════════════════
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("🎮 GameZone — Sistema de Gestión");

        // ── Sidebar buttons ───────────────────────────────────────────────
        Button btnAdd      = menuButton("➕  Agregar Videojuego");
        Button btnList     = menuButton("📋  Listar Todos");
        Button btnSearch   = menuButton("🔍  Buscar por Título");
        Button btnPlatform = menuButton("🕹️  Buscar por Plataforma");
        Button btnSell     = menuButton("💰  Realizar Venta");
        Button btnSales    = menuButton("📊  Mostrar Ventas");
        Button btnExit     = menuButton("🚪  Salir");
        btnExit.setStyle(btnExit.getStyle() + "-fx-background-color:#c0392b;");

        VBox sidebar = new VBox(10, btnAdd, btnList, btnSearch, btnPlatform,
                btnSell, btnSales, new Separator(), btnExit);
        sidebar.setPadding(new Insets(20));
        sidebar.setPrefWidth(220);
        sidebar.setStyle("-fx-background-color:#1a1a2e;");

        // ── Main content ──────────────────────────────────────────────────
        tableView = buildGameTable();

        Label title = new Label("SISTEMA DE GESTIÓN — GAMEZONE");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        title.setTextFill(Color.WHITE);

        VBox content = new VBox(15, title, tableView);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color:#16213e;");
        VBox.setVgrow(tableView, Priority.ALWAYS);

        HBox root = new HBox(sidebar, content);
        HBox.setHgrow(content, Priority.ALWAYS);

        // ── Actions ───────────────────────────────────────────────────────
        btnAdd.setOnAction(e      -> showAddDialog(primaryStage));
        btnList.setOnAction(e     -> loadAllGames());
        btnSearch.setOnAction(e   -> showSearchByTitleDialog(primaryStage));
        btnPlatform.setOnAction(e -> showSearchByPlatformDialog(primaryStage));
        btnSell.setOnAction(e     -> showSellDialog(primaryStage));
        btnSales.setOnAction(e    -> showSalesWindow(primaryStage));
        btnExit.setOnAction(e     -> primaryStage.close());

        loadAllGames();

        primaryStage.setScene(new Scene(root, 950, 600));
        primaryStage.show();
    }

    // ═════════════════════════════════════════════════════════════════════
    //  1. ADD VIDEO GAME
    // ═════════════════════════════════════════════════════════════════════
    private void showAddDialog(Stage owner) {
        Stage dialog = dialog(owner, "Agregar Videojuego", 460, 540);

        ToggleGroup typeGroup = new ToggleGroup();
        RadioButton rbDigital  = radio("Digital",  typeGroup, true);
        RadioButton rbPhysical = radio("Físico",   typeGroup, false);

        TextField tfTitle    = field("Título");
        TextField tfPrice    = field("Precio");
        TextField tfPlatform = field("Plataforma");
        TextField tfStock    = field("Stock");
        TextField tfGenre    = field("Género");

        // Digital fields
        TextField tfSize     = field("Tamaño en GB");
        TextField tfDownload = field("Plataforma de descarga");
        VBox digitalBox = new VBox(5, label("Tamaño (GB)"), tfSize,
                label("Plataforma de descarga"), tfDownload);

        // Physical fields
        TextField tfCondition   = field("nuevo / usado");
        TextField tfDistributor = field("Distribuidor");
        VBox physicalBox = new VBox(5, label("Condición (nuevo/usado)"), tfCondition,
                label("Distribuidor"), tfDistributor);
        physicalBox.setVisible(false);
        physicalBox.setManaged(false);

        typeGroup.selectedToggleProperty().addListener((obs, o, n) -> {
            boolean digital = n == rbDigital;
            digitalBox.setVisible(digital);  digitalBox.setManaged(digital);
            physicalBox.setVisible(!digital); physicalBox.setManaged(!digital);
        });

        Button btnSave = actionButton("💾 Guardar");
        btnSave.setOnAction(e -> {
            try {
                String title    = tfTitle.getText().trim();
                double price    = Double.parseDouble(tfPrice.getText().trim());
                String platform = tfPlatform.getText().trim();
                int    stock    = Integer.parseInt(tfStock.getText().trim());
                String genre    = tfGenre.getText().trim();

                VideoGame game;
                if (rbDigital.isSelected()) {
                    double sizeGB   = Double.parseDouble(tfSize.getText().trim());
                    String download = tfDownload.getText().trim();
                    game = new DigitalVideoGame(title, price, platform, stock, genre, sizeGB, download);
                } else {
                    String condition   = tfCondition.getText().trim();
                    String distributor = tfDistributor.getText().trim();
                    game = new PhysicalVideoGame(title, price, platform, stock, genre, condition, distributor);
                }

                gameService.addVideoGame(game);
                loadAllGames();
                dialog.close();
                alert(Alert.AlertType.INFORMATION, "Éxito", "Videojuego agregado correctamente.");

            } catch (IllegalArgumentException ex) {
                alert(Alert.AlertType.WARNING, "Atención", ex.getMessage());
            } catch (NumberFormatException ex) {
                alert(Alert.AlertType.ERROR, "Error de formato",
                        "Precio, stock y tamaño deben ser números válidos.");
            }
        });

        VBox form = new VBox(8,
                label("Tipo"), new HBox(20, rbDigital, rbPhysical),
                label("Título"),     tfTitle,
                label("Precio"),     tfPrice,
                label("Plataforma"), tfPlatform,
                label("Stock"),      tfStock,
                label("Género"),     tfGenre,
                digitalBox, physicalBox,
                btnSave);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color:#16213e;");

        ScrollPane scroll = new ScrollPane(form);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color:#16213e;");
        dialog.setScene(new Scene(scroll, 460, 540));
        dialog.show();
    }

    // ═════════════════════════════════════════════════════════════════════
    //  2. LIST ALL
    // ═════════════════════════════════════════════════════════════════════
    private void loadAllGames() {
        tableView.setItems(FXCollections.observableArrayList(gameService.getAllVideoGames()));
    }

    // ═════════════════════════════════════════════════════════════════════
    //  3. SEARCH BY TITLE
    // ═════════════════════════════════════════════════════════════════════
    private void showSearchByTitleDialog(Stage owner) {
        Stage dialog = dialog(owner, "Buscar por Título", 400, 160);
        TextField tfTitle  = field("Ingrese el título exacto");
        Button    btnSearch = actionButton("🔍 Buscar");

        btnSearch.setOnAction(e -> {
            VideoGame g = gameService.searchByTitle(tfTitle.getText().trim());
            if (g == null) {
                alert(Alert.AlertType.WARNING, "No encontrado",
                        "No existe un videojuego con ese título.");
            } else {
                tableView.setItems(FXCollections.observableArrayList(g));
                dialog.close();
            }
        });

        VBox box = new VBox(10, label("Título"), tfTitle, btnSearch);
        box.setPadding(new Insets(20));
        box.setStyle("-fx-background-color:#16213e;");
        dialog.setScene(new Scene(box, 400, 160));
        dialog.show();
    }

    // ═════════════════════════════════════════════════════════════════════
    //  4. SEARCH BY PLATFORM
    // ═════════════════════════════════════════════════════════════════════
    private void showSearchByPlatformDialog(Stage owner) {
        Stage dialog = dialog(owner, "Buscar por Plataforma", 400, 160);
        TextField tfPlatform = field("Ej: PC, PS5, Xbox");
        Button    btnSearch  = actionButton("🔍 Buscar");

        btnSearch.setOnAction(e -> {
            List<VideoGame> results = gameService.searchByPlatform(tfPlatform.getText().trim());
            if (results == null || results.isEmpty()) {
                alert(Alert.AlertType.WARNING, "No encontrado",
                        "No hay videojuegos para esa plataforma.");
            } else {
                tableView.setItems(FXCollections.observableArrayList(results));
                dialog.close();
            }
        });

        VBox box = new VBox(10, label("Plataforma"), tfPlatform, btnSearch);
        box.setPadding(new Insets(20));
        box.setStyle("-fx-background-color:#16213e;");
        dialog.setScene(new Scene(box, 400, 160));
        dialog.show();
    }

    // ═════════════════════════════════════════════════════════════════════
    //  5. SELL
    // ═════════════════════════════════════════════════════════════════════
    private void showSellDialog(Stage owner) {
        Stage dialog = dialog(owner, "Realizar Venta", 400, 210);
        TextField tfTitle = field("Título del videojuego");
        TextField tfQty   = field("Cantidad");
        Button    btnSell = actionButton("💰 Vender");

        btnSell.setOnAction(e -> {
            try {
                String title = tfTitle.getText().trim();
                int    qty   = Integer.parseInt(tfQty.getText().trim());
                double total = saleService.sellVideoGame(title, qty);
                loadAllGames();
                dialog.close();
                alert(Alert.AlertType.INFORMATION, "Venta Exitosa",
                        String.format("✅ Total de la venta: $%.2f", total));
            } catch (IllegalArgumentException ex) {
                alert(Alert.AlertType.WARNING, "No se pudo realizar la venta", ex.getMessage());
            } catch (NumberFormatException ex) {
                alert(Alert.AlertType.ERROR, "Error", "La cantidad debe ser un número entero.");
            }
        });

        VBox box = new VBox(10,
                label("Título del videojuego"), tfTitle,
                label("Cantidad"), tfQty,
                btnSell);
        box.setPadding(new Insets(20));
        box.setStyle("-fx-background-color:#16213e;");
        dialog.setScene(new Scene(box, 400, 210));
        dialog.show();
    }

    // ═════════════════════════════════════════════════════════════════════
    //  6. SHOW SALES
    // ═════════════════════════════════════════════════════════════════════
    private void showSalesWindow(Stage owner) {
        Stage window = dialog(owner, "📊 Ventas Realizadas", 720, 420);

        TableView<Sale> salesTable = new TableView<>();
        salesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        salesTable.setStyle("-fx-background-color:#1a1a2e;");

        salesTable.getColumns().addAll(
                col("ID",           "id",        120),
                col("Videojuego",   "videoGame", 200),
                col("Cantidad",     "quantity",   80),
                col("Precio Unit.", "unitPrice", 110),
                col("Total",        "total",     110),
                col("Fecha",        "saleDate",  160)
        );

        salesTable.setItems(FXCollections.observableArrayList(saleService.getAllSales()));
        VBox.setVgrow(salesTable, Priority.ALWAYS);

        VBox box = new VBox(salesTable);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-background-color:#16213e;");
        window.setScene(new Scene(box, 720, 420));
        window.show();
    }

    // ═════════════════════════════════════════════════════════════════════
    //  MAIN GAME TABLE  (with right-click Update / Delete)
    // ═════════════════════════════════════════════════════════════════════
    @SuppressWarnings("unchecked")
    private TableView<VideoGame> buildGameTable() {
        TableView<VideoGame> table = new TableView<>();
        table.setStyle("-fx-background-color:#1a1a2e;");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Label("No hay videojuegos registrados."));

        table.getColumns().addAll(
                col("Título",     "title",    180),
                col("Precio",     "price",     80),
                col("Plataforma", "platform",  90),
                col("Stock",      "stock",     60),
                col("Género",     "genre",     90)
        );

        table.setRowFactory(tv -> {
            TableRow<VideoGame> row = new TableRow<>();

            MenuItem miUpdate = new MenuItem("✏️ Actualizar");
            miUpdate.setOnAction(e -> { if (!row.isEmpty()) showUpdateDialog(row.getItem()); });

            MenuItem miDelete = new MenuItem("🗑️ Eliminar");
            miDelete.setOnAction(e -> {
                if (!row.isEmpty()) {
                    gameService.deleteVideoGame(row.getItem().getTitle());
                    loadAllGames();
                    alert(Alert.AlertType.INFORMATION, "Eliminado",
                            "Videojuego eliminado correctamente.");
                }
            });

            ContextMenu menu = new ContextMenu(miUpdate, miDelete);
            row.contextMenuProperty().bind(
                    Bindings.when(row.emptyProperty()).then((ContextMenu) null).otherwise(menu));
            return row;
        });

        return table;
    }

    // ═════════════════════════════════════════════════════════════════════
    //  UPDATE DIALOG
    // ═════════════════════════════════════════════════════════════════════
    private void showUpdateDialog(VideoGame game) {
        Stage dialog = new Stage();
        dialog.setTitle("Actualizar — " + game.getTitle());
        dialog.initModality(Modality.APPLICATION_MODAL);

        TextField tfPrice    = field(String.valueOf(game.getPrice()));
        TextField tfPlatform = field(game.getPlatform());
        TextField tfStock    = field(String.valueOf(game.getStock()));
        TextField tfGenre    = field(game.getGenre());

        Button btnSave = actionButton("💾 Guardar cambios");
        btnSave.setOnAction(e -> {
            try {
                game.setPrice(Double.parseDouble(tfPrice.getText().trim()));
                game.setPlatform(tfPlatform.getText().trim());
                game.setStock(Integer.parseInt(tfStock.getText().trim()));
                game.setGenre(tfGenre.getText().trim());
                gameService.updateVideoGame(game.getTitle(), game);
                loadAllGames();
                dialog.close();
                alert(Alert.AlertType.INFORMATION, "Actualizado",
                        "Videojuego actualizado correctamente.");
            } catch (IllegalArgumentException ex) {
                alert(Alert.AlertType.WARNING, "Error de validación", ex.getMessage());
            }
        });

        VBox form = new VBox(8,
                label("Precio"),     tfPrice,
                label("Plataforma"), tfPlatform,
                label("Stock"),      tfStock,
                label("Género"),     tfGenre,
                btnSave);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color:#16213e;");
        dialog.setScene(new Scene(form, 380, 300));
        dialog.show();
    }

    // ═════════════════════════════════════════════════════════════════════
    //  UI HELPERS
    // ═════════════════════════════════════════════════════════════════════
    private Button menuButton(String text) {
        Button b = new Button(text);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setStyle("-fx-background-color:#0f3460; -fx-text-fill:white;" +
                "-fx-font-size:13px; -fx-padding:10 15; -fx-cursor:hand;");
        b.setOnMouseEntered(e -> b.setStyle(b.getStyle().replace("#0f3460","#e94560")));
        b.setOnMouseExited(e  -> b.setStyle(b.getStyle().replace("#e94560","#0f3460")));
        return b;
    }

    private Button actionButton(String text) {
        Button b = new Button(text);
        b.setStyle("-fx-background-color:#e94560; -fx-text-fill:white;" +
                "-fx-font-size:13px; -fx-padding:8 20; -fx-cursor:hand;");
        return b;
    }

    private TextField field(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle("-fx-background-color:#0f3460; -fx-text-fill:white;" +
                "-fx-prompt-text-fill:#888;");
        return tf;
    }

    private Label label(String text) {
        Label l = new Label(text);
        l.setTextFill(Color.LIGHTGRAY);
        l.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        return l;
    }

    private RadioButton radio(String text, ToggleGroup group, boolean selected) {
        RadioButton rb = new RadioButton(text);
        rb.setToggleGroup(group);
        rb.setSelected(selected);
        rb.setStyle("-fx-text-fill:white;");
        return rb;
    }

    private Stage dialog(Stage owner, String title, double w, double h) {
        Stage s = new Stage();
        s.setTitle(title);
        s.initOwner(owner);
        s.initModality(Modality.APPLICATION_MODAL);
        s.setWidth(w);
        s.setHeight(h);
        return s;
    }

    private void alert(Alert.AlertType type, String title, String msg) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    @SuppressWarnings("unchecked")
    private <T> TableColumn<T, Object> col(String name, String prop, int width) {
        TableColumn<T, Object> c = new TableColumn<>(name);
        c.setCellValueFactory(new PropertyValueFactory<>(prop));
        c.setPrefWidth(width);
        return c;
    }
}