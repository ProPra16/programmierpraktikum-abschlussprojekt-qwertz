package attd.workspace;

import java.net.URL;
import java.util.ResourceBundle;

import attd.mvc.InjectModel;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class WorkspaceController{

	@InjectModel
	private WorkspaceModel workspaceModel;
	@FXML
	private TextField TextField;
	@FXML
	private Button OkButton;

	@FXML
	private void okButtonAction(ActionEvent event) {
		workspaceModel.createDirectory(event);
	}

	@FXML
	private void cancelButtonAction() {
		workspaceModel.cancel();
	}

	@FXML
	private void browseButtonAction() {
		workspaceModel.browse();
	}

	public void initialize() {
		TextField.textProperty().bind(workspaceModel.pathProperty());
		OkButton.disableProperty().bind(Bindings.equal("", workspaceModel.pathProperty()));
	}

}
