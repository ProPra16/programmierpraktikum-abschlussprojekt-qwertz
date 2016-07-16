package attd.core;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import vk.core.api.TestFailure;
import vk.core.api.TestResult;

import java.util.ArrayList;

/**
 * Created by student on 16/07/16.
 */
public class TestResultControl2 extends TableView<TestFailure> {

    public TestResultControl2(TestResult testResult) {
        TableColumn<TestFailure, String> errorColumn = new TableColumn<>("StackTrace");
        errorColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getExceptionStackTrace()));

        TableColumn<TestFailure, String> codeLineColumn = new TableColumn<>("Message");
        codeLineColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getMessage()));

        TableColumn<TestFailure, String> errorColumnColumn = new TableColumn<>("MethodName");
        errorColumnColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getMethodName()));

        TableColumn<TestFailure, String> errorLineColumn = new TableColumn<>("TestClassName");
        errorLineColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getTestClassName()));

        setPlaceholder(new Label("Keine Fehler"));
        setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        getColumns().addAll(errorColumn, codeLineColumn, errorColumnColumn, errorLineColumn);

        setItems(FXCollections.observableList(new ArrayList<TestFailure>(testResult.getTestFailures())));
    }
}
