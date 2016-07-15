package attd.main;

import attd.core.AttdController;
import attd.core.AttdModel;
import attd.mvc.FxmlLoader;
import attd.mvc.ViewTuple;
import attd.workspace.WorkspaceController;
import attd.workspace.WorkspaceModel;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class Main extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		FxmlLoader fxmlLoader = new FxmlLoader();
		Stage stage = new Stage();
		stage.setTitle("ATTD");
		ViewTuple<AttdController, AttdModel> attdTuple = fxmlLoader.load(AttdController.class, AttdModel.class, "/AttdView.fxml");
		stage.setOnCloseRequest(e -> {
			Alert alert = new Alert(AlertType.WARNING, "Möchten Sie die Änderungen an der Aufgabe speichern?", ButtonType.YES, ButtonType.NO);
			if (attdTuple.getModel().exerciseLoaded() && alert.showAndWait().get() == ButtonType.YES) {
				attdTuple.getModel().save();
			}
		});
		stage.setScene(new Scene(attdTuple.getParent()));
		stage.show();

	}


}
