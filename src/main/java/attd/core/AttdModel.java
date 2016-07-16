package attd.core;

import javafx.animation.Animation.Status;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.*;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.util.Duration;

import vk.core.api.CompileError;


import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;


import attd.mvc.FxmlLoader;
import attd.mvc.ViewTuple;
import attd.workspace.WorkspaceController;
import attd.workspace.WorkspaceModel;
import vk.core.api.TestResult;

public class AttdModel {
	private Version currentVersion;
	private Version oldVersion;
	private Exercise exercise;


	private StringProperty stateProperty = new SimpleStringProperty();
	private StringProperty timeProperty = new SimpleStringProperty();
	private StringProperty codeProperty = new SimpleStringProperty();
	private CatalogRepository catalogRepository = new CatalogRepository();
	private JavaStringCompilerWrapper compiler = new JavaStringCompilerWrapper();
	private ListProperty<CompileError> errorProperty = new SimpleListProperty<>();
	public ObjectProperty<TestResult> testResultProperty = new SimpleObjectProperty<>();
	public ObjectProperty<TestResult> TestResultProperty() {return testResultProperty;}


	public boolean exerciseLoaded() {
		return currentVersion != null;
	}

	public CatalogRepository getCatalogRepository() {
		return catalogRepository;
	}

	private Timeline timeline;
	private long seconds = 0;

	public StringProperty TimeProperty() {
		return timeProperty;
	}

	public StringProperty CodeProperty() {
		return codeProperty;
	}

	public StringProperty StateProperty() {
		return stateProperty;
	}

	public ListProperty<CompileError> ErrorProperty() {
		return errorProperty;
	}

	private void initTimer() {
		if (exercise.getConfigurations().BabyStepsEnabled()) {
			seconds = 0;
			if (timeline == null) {
				timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
					seconds++;
					timeProperty.set(LocalTime.ofSecondOfDay(seconds).format(DateTimeFormatter.ISO_LOCAL_TIME));
				}));
				timeline.setOnFinished(e -> {
					stopTimeline();
					timeExpired();
				});
			}
			timeline.setCycleCount(exercise.getConfigurations().getTime());
			timeline.playFromStart();
		}
	}

	public void save() {
		if(currentVersion==null){return;}
		switch (currentVersion.getState()) {
		case writeFailingTest:
			currentVersion.getCodes().getTestCode().setCode(codeProperty.get());
			break;
		case acceptanceTest:
			currentVersion.getCodes().getAcceptanceCode().setCode(codeProperty.get());
			break;
		default:
			currentVersion.getCodes().getClassCode().setCode(codeProperty.get());
			break;
		}
		if (showAlert(new Alert(AlertType.CONFIRMATION, "Möchten Sie die Änderungen am Code speichern?", ButtonType.YES, ButtonType.NO))) {
			System.out.print("ok");
			catalogRepository.save();
		}

	}

	private void loadCode(State state) {
		switch (state) {
		case writeFailingTest:
			codeProperty.set(currentVersion.getCodes().getTestCode().getCode());
			break;
		case acceptanceTest:
			codeProperty.set(currentVersion.getCodes().getAcceptanceCode().getCode());
			break;
		default:
			codeProperty.set(currentVersion.getCodes().getClassCode().getCode());
			break;
		}
	}

	public void check() {

		if (currentVersion != null) {
			errorProperty.set(null);
			String code = currentVersion.getCodes().getClassCode().getCode();
			String inc = codeProperty.get();
			String test = currentVersion.getCodes().getTestCode().getCode();
			
			try {
				switch (currentVersion.getState()) {
				case writeFailingTest:
					
					if (check(code, inc, 1)) { // prüft
																						// ob
																						// der
																						// test
						// kompiliert und es
						// einen fehlschlagenden
						// test gibt

						stopTimeline();
						save();
						changeState(State.makeTheTestPass);

					}



					break;
				case makeTheTestPass:
					
					if (check(inc, test, 0)) { // prüft
																						// ob
																						// der
																						// Code
						// die Tests besteht
						// && der code
						// kompiliert
						stopTimeline();
						save();
						changeState(State.refactor);

					}
					break;
				case refactor:
					
					if (check(inc, test, 0)) { // prüft
																						// ob
																						// refract
						// kompiliert &&
						// tests besteht
						
						save();
						if (exercise.getConfigurations().AcceptanceTestEnabled()&& check(inc, currentVersion.getCodes().getAcceptanceCode().getCode(), 0)) {// prüft
																						// ob
																						// acceptancetestenabled
																						// &&
																						// code
																						// besteht
																						// acceptancetests
							save();
							currentVersion.getCodes().getAcceptanceCode().implementedProperty().set(true);
							changeState(State.acceptanceTest);
						} else {

							changeState(State.writeFailingTest);
						}
					}
					break;
					case acceptanceTest:
					
					if (check(code, inc, 1)) { // prüft
																						// ob
																						// der
						// akzeptanztest
						// kompiliert && es
						// einen fehlschlangeden
						// Akzeptanztest gibt
						save();
						changeState(State.writeFailingTest);
					}

					break;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	private boolean check(String code,String test, int failures){
		CustomCompilerResult ccr= compiler.getCustomCompilerResult(code, test);

		if(ccr.hasCompileErrors()){
			setErrors(ccr.getCompileErrors());
			return false;
		}
		if(ccr.getTestResult().getNumberOfFailedTests() == failures){
			return true;
		}
		testResultProperty.setValue(ccr.getTestResult());
		return false;
	}

	private void setErrors(Collection<CompileError> ccr) {
		errorProperty.set(FXCollections.observableList(new ArrayList<CompileError>(ccr)));
	}

	private void timeExpired() {
		if (currentVersion.getState() == State.writeFailingTest) {
			changeState(State.refactor);
		}
		if (currentVersion.getState() == State.makeTheTestPass) {
			changeState(State.writeFailingTest);
		}
	}

	private boolean showAlert(Alert alert) {
		alert.showAndWait();
		return alert.getResult() == ButtonType.YES;
	}

	public void init(Version version, Exercise exercise) {

		if (oldVersion != null
				&& showAlert(new Alert(AlertType.WARNING, "Möchten Sie die Änderungen an der Aufgabe speichern?", ButtonType.YES, ButtonType.NO))) {
			save();
		} else {
			currentVersion = oldVersion;
		}
		if (showAlert(new Alert(AlertType.CONFIRMATION, "Möchten Sie die Aufgabe starten?", ButtonType.YES, ButtonType.NO))) {

			stopTimeline();
			this.oldVersion = version;
			this.currentVersion = version;
			this.exercise = exercise;

			changeState(version.getState());
		}

	}

	public void returnToTest() {
		stopTimeline();
		changeState(State.writeFailingTest);
	}

	private void stopTimeline() {
		if (timeline != null && timeline.getStatus() == Status.RUNNING) {
			timeline.stop();
		}
		timeProperty.set("");
	}

	private void changeState(State state) {
		loadCode(state);
		if (state == State.writeFailingTest || state == State.makeTheTestPass) {
			initTimer();
		}
		////////////////////////this.state = state;
		currentVersion.setState(state);
		stateProperty.set(state.toString());
	}
	

	public void addCatalogItem(Exercise param) {
		TextInputDialog textInputDialog = new TextInputDialog();
		textInputDialog.setTitle("Neue Version erstellen");
		textInputDialog.setHeaderText("Geben Sie den Titel der Version ein.");
		textInputDialog.showAndWait();

		if (textInputDialog.getResult().equals("")) {
			showAlert(new Alert(AlertType.ERROR, "Es wurde kein Titel eingegeben.", ButtonType.OK));
			return;
		}
		if (param.getVersions().stream().filter(p -> p.getName().equals(textInputDialog.getResult())).findAny().isPresent()) {
			showAlert(new Alert(AlertType.ERROR, "Eine Version mit diesem Titel existiert bereits.", ButtonType.OK));
			return;
		}
		param.addVersion(textInputDialog.getResult());

	}


	public void load() {
		if (!catalogRepository.load()) {
			changeWorkspace();
		}
	}

	public void changeWorkspace() {
		FxmlLoader fxmlLoader = new FxmlLoader();
		Stage stage = new Stage();
		stage.setTitle("Workspace Launcher");
		stage.setResizable(false);
		ViewTuple<WorkspaceController, WorkspaceModel> workspaceTuple = fxmlLoader.load(WorkspaceController.class, WorkspaceModel.class,
				"/WorkspaceView.fxml");
		stage.setScene(new Scene(workspaceTuple.getParent()));
		stage.showAndWait();
	}


}
