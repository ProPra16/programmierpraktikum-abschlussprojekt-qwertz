package attd.core;

import attd.mvc.InjectModel;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;

public class AttdController {
	@FXML
	private CustomListView CatalogListView;
	@FXML
	private TextArea DescriptionArea;
	@FXML
	private Label StateLabel;
	@FXML
	private Label RemainingTimeLabel;
	@FXML
	private TextArea CodeArea;
	@FXML
	private Button ReturnButton;

	@FXML
	private ErrorControl ErrorView;

	@InjectModel
	private AttdModel attdModel;

	@FXML
	private void onLoadCatalogClick(ActionEvent event) {
		attdModel.load();
	}

	@FXML
	private void onCatalogChangeClick(){
		attdModel.changeWorkspace();
	}
	
	@FXML
	private void onSaveCatalogClick(ActionEvent event) {
		attdModel.save();
	}

	@FXML
	private void onExecuteButtonClick(MouseEvent event) {
		attdModel.check();
	}

	@FXML
	private void onReturnButtonClick(MouseEvent event) {
		attdModel.returnToTest();
	}

	public void initialize() {
		CatalogListView.itemsProperty().bind(attdModel.getCatalogRepository().catalogProperty());
		
		ErrorView.itemsProperty().bind(attdModel.ErrorProperty());

		StateLabel.textProperty().bind(attdModel.StateProperty());
		RemainingTimeLabel.textProperty().bind(attdModel.TimeProperty());
		CodeArea.textProperty().bindBidirectional(attdModel.CodeProperty());

		ReturnButton.disableProperty().bind(Bindings.notEqual(State.makeTheTestPass.toString(), attdModel.StateProperty()));

		CatalogListView.CatalogLeftClickProperty().set((String param) -> {
			DescriptionArea.setText(param);
			return null;
		});

		CatalogListView.CatalogRightClickProperty().set((Exercise param) -> {
			attdModel.addCatalogItem(param);
			return null;
		});
		CatalogListView.CatalogItemClickProperty().set((Version paramA, Exercise paramB) -> {
			attdModel.init(paramA, paramB);
			return null;
		});

	}

}
