package Attd;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;

public class CustomListView extends ListView<Catalog> {
	private ObjectProperty<Callback<String, Void>> onCatalogLeftClick = new SimpleObjectProperty<>();
	public ObjectProperty<Callback<String, Void>> onCatalogLeftClickProperty(){return onCatalogLeftClick;}

	
	private ObjectProperty<Callback<Catalog, Void>> onCatalogRightClick = new SimpleObjectProperty<>();;
	public ObjectProperty<Callback<Catalog, Void>> onCatalogRightClickProperty(){return onCatalogRightClick;}

	private ObjectProperty<Callback<CatalogItem, Void>> onCatalogItemClick = new SimpleObjectProperty<>();;
	public ObjectProperty<Callback<CatalogItem, Void>> onCatalogItemClickProperty(){return onCatalogItemClick;}

	public CustomListView() {
		
		setCellFactory(new Callback<ListView<Catalog>, ListCell<Catalog>>() {
			
			@Override
			public ListCell<Catalog> call(ListView<Catalog> param) {
				ListCell<Catalog> listCell = new ListCell<Catalog>() {
					@Override
					protected void updateItem(Catalog item, boolean empty) {
						super.updateItem(item, empty);
						if (empty) {
							setGraphic(null);
						} else {
							setGraphic(createItem(item));
							
						}
					}
				};
				return listCell;
			}
		});

	}

	private Accordion createItem(Catalog catalog) {
		TitledPane titledPane = new TitledPane(catalog.getCatalogName(), createTableView(catalog));
		Accordion accordion = new Accordion(titledPane);
		accordion.setPrefWidth(0);
		titledPane.setOnMouseClicked(e -> {
			if (e.getButton() == MouseButton.PRIMARY && onCatalogLeftClick != null) {
				if (titledPane.isExpanded()) {
					onCatalogLeftClick.get().call(catalog.getDescription());
				} else {
					onCatalogLeftClick.get().call("");
				}
			}
			if (e.getButton() == MouseButton.SECONDARY && titledPane.isExpanded()) {
				Alert alert = new Alert(AlertType.CONFIRMATION, "Soll eine neue Version erstellt werden?",
						ButtonType.YES, ButtonType.NO);
				alert.showAndWait();
				if (alert.getResult() == ButtonType.YES && onCatalogRightClick != null) {
					onCatalogRightClick.get().call(catalog);
				}
			}
		});
		return accordion;
	}

	//private TableView<CatalogItem> createTableView(List<CatalogItem> catalogItems) {
	private TableView<CatalogItem> createTableView(Catalog catalog) {
		TableView<CatalogItem> tableView = new TableView<>();

		TableColumn<CatalogItem, String> tc1 = new TableColumn<CatalogItem, String>("Version");
		tc1.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<CatalogItem, String>, ObservableValue<String>>() {

					@Override
					public ObservableValue<String> call(CellDataFeatures<CatalogItem, String> param) {
						return new SimpleStringProperty(param.getValue().getVersion());
					}
				});

		TableColumn<CatalogItem, String> tc2 = new TableColumn<CatalogItem, String>("Zeit");
		tc2.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<CatalogItem, String>, ObservableValue<String>>() {

					@Override
					public ObservableValue<String> call(CellDataFeatures<CatalogItem, String> param) {
						return new SimpleStringProperty(
								LocalTime.ofSecondOfDay(param.getValue().getConfigurations().getTime())
										.format(DateTimeFormatter.ISO_LOCAL_TIME));
					}
				});
		TableColumn<CatalogItem, GridPane> tc3 = new TableColumn<CatalogItem, GridPane>("Akzeptanztest");
		tc3.setStyle("-fx-padding: 0 0 0 0;");
		tc3.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<CatalogItem, GridPane>, ObservableValue<GridPane>>() {

					@Override
					public ObservableValue<GridPane> call(CellDataFeatures<CatalogItem, GridPane> param) {
						GridPane gridPane = new GridPane();
						if (param.getValue().getCodes().getAcceptanceCode().getFullyImplemented()) {
							gridPane.setStyle("-fx-background-color: #00FF00");
						} else {
							gridPane.setStyle("-fx-background-color: #FF0000");
						}
						param.getValue().getCodes().getAcceptanceCode().getTest().addListener(new ChangeListener<Boolean>() {
							
							@Override
							public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
									Boolean newValue) {
								if (newValue) {
									gridPane.setStyle("-fx-background-color: #00FF00");
								} else {
									gridPane.setStyle("-fx-background-color: #FF0000");
								}
							}
						});
						
						// TODO Auto-generated method stub
						return new SimpleObjectProperty<GridPane>(gridPane);
					}
				});

		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		tableView.getColumns().addAll(tc1, tc2, tc3);
		
		tableView.itemsProperty().bind(catalog.CatalogItemsProperty());

		tableView.setRowFactory(new Callback<TableView<CatalogItem>, TableRow<CatalogItem>>() {
			@Override
			public TableRow<CatalogItem> call(TableView<CatalogItem> param) {
				TableRow<CatalogItem> tableRow = new TableRow<>();
				tableRow.setOnMouseClicked(event -> {
					if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
						Alert alert = new Alert(AlertType.CONFIRMATION, "Ausführen?", ButtonType.YES, ButtonType.NO);
						alert.showAndWait();
						if (alert.getResult() == ButtonType.YES && onCatalogItemClick != null) {
							onCatalogItemClick.get().call(tableRow.getItem());
						}
					}
				});
				return tableRow;
			}

		});

		return tableView;
	}
}
