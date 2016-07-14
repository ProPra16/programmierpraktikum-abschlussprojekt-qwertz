package attd.core;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class CustomListView extends ListView<Exercise> {
	private ObjectProperty<Callback<String, Void>> CatalogLeftClick = new SimpleObjectProperty<>();
	private ObjectProperty<Callback<Exercise, Void>> CatalogRightClick = new SimpleObjectProperty<>();
	private ObjectProperty<CustomCallback<Version, Exercise, Void>> CatalogItemClick = new SimpleObjectProperty<>();

	public CustomListView() {

		setCellFactory(new Callback<ListView<Exercise>, ListCell<Exercise>>() {

			@Override
			public ListCell<Exercise> call(ListView<Exercise> param) {
				ListCell<Exercise> listCell = new ListCell<Exercise>() {
					@Override
					protected void updateItem(Exercise item, boolean empty) {
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

	public ObjectProperty<Callback<String, Void>> CatalogLeftClickProperty() {
		return CatalogLeftClick;
	}

	public ObjectProperty<Callback<Exercise, Void>> CatalogRightClickProperty() {
		return CatalogRightClick;
	}

	public ObjectProperty<CustomCallback<Version, Exercise, Void>> CatalogItemClickProperty() {
		return CatalogItemClick;
	}

	private Accordion createItem(Exercise exercise) {
		TitledPane titledPane = new TitledPane(exercise.getName(), createTableView(exercise));
		Accordion accordion = new Accordion(titledPane);
		accordion.setPrefWidth(0);
		titledPane.setOnMouseClicked(e -> {
			if (e.getButton() == MouseButton.PRIMARY && CatalogLeftClick != null) {
				if (titledPane.isExpanded()) {
					CatalogLeftClick.get().call(exercise.getDescription());
				} else {
					CatalogLeftClick.get().call("");
				}
			}
			if (e.getButton() == MouseButton.SECONDARY && titledPane.isExpanded()) {

				CatalogRightClick.get().call(exercise);

			}
		});
		return accordion;
	}

	
	private TableView<Version> createTableView(Exercise exercise) {
		TableView<Version> tableView = new TableView<>();

		TableColumn<Version, String> tc1 = new TableColumn<Version, String>("Version");
		tc1.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Version, String>, ObservableValue<String>>() {

			@Override
			public ObservableValue<String> call(CellDataFeatures<Version, String> param) {
				return new SimpleStringProperty(param.getValue().getName());
			}
		});
		tableView.getColumns().add(tc1);


		if(exercise.getConfigurations().BabyStepsEnabled()){
			
			TableColumn<Version, String> tc2 = new TableColumn<Version, String>("Zeit");
			tc2.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Version, String>, ObservableValue<String>>() {

				@Override
				public ObservableValue<String> call(CellDataFeatures<Version, String> param) {
					// TODO Auto-generated method stub
					return new SimpleStringProperty(
							LocalTime.ofSecondOfDay(exercise.getConfigurations().getTime()).format(DateTimeFormatter.ISO_LOCAL_TIME));
				}
			});
			tableView.getColumns().add(tc2);
		}
			
			
		
		
		
		
		
		if(exercise.getConfigurations().AcceptanceTestEnabled()){
			
			TableColumn<Version, GridPane> tc3 = new TableColumn<Version, GridPane>("Akzeptanztest");
			tc3.setStyle("-fx-padding: 0 0 0 0;");
			tc3.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Version, GridPane>, ObservableValue<GridPane>>() {

				@Override
				public ObservableValue<GridPane> call(CellDataFeatures<Version, GridPane> param) {
					GridPane gridPane = new GridPane();
					if (param.getValue().getCodes().getAcceptanceCode().implementedProperty().get()) {
						gridPane.setStyle("-fx-background-color: #00FF00");
					} else {
						gridPane.setStyle("-fx-background-color: #FF0000");
					}
					param.getValue().getCodes().getAcceptanceCode().implementedProperty().addListener(new ChangeListener<Boolean>() {

						@Override
						public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
							if (newValue) {
								gridPane.setStyle("-fx-background-color: #00FF00");
							} else {
								gridPane.setStyle("-fx-background-color: #FF0000");
							}
						}
					});

					return new SimpleObjectProperty<GridPane>(gridPane);
				}
			});
			
			tableView.getColumns().add(tc3);
		}

		tableView.itemsProperty().bind(exercise.versionsProperty());

		tableView.setRowFactory(new Callback<TableView<Version>, TableRow<Version>>() {
			@Override
			public TableRow<Version> call(TableView<Version> param) {
				TableRow<Version> tableRow = new TableRow<>();
				tableRow.setOnMouseClicked(event -> {
					if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {

						CatalogItemClick.get().call(tableRow.getItem(), exercise);

					}
				});
				return tableRow;
			}

		});

		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		return tableView;
	}
}
