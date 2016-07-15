package attd.core;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import vk.core.api.CompileError;

public class ErrorControl extends TableView<CompileError> {
    public ErrorControl() {
        TableColumn<CompileError, String> tc1 = new TableColumn<>("Error");
        tc1.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<CompileError, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<CompileError, String> param) {
                return new SimpleStringProperty(param.getValue().getMessage());
            }
        });
        TableColumn<CompileError, String> tc2 = new TableColumn<>("Code Line");
        tc2.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<CompileError, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<CompileError, String> param) {
                return new SimpleStringProperty(param.getValue().getCodeLineContainingTheError());
            }
        });
        TableColumn<CompileError, String> tc3 = new TableColumn<>("Column Number");
        tc3.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<CompileError, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<CompileError, String> param) {
                return new SimpleStringProperty(Long.toString(param.getValue().getColumnNumber()));
            }
        });
        TableColumn<CompileError, String> tc4 = new TableColumn<>("Line Number");
        tc4.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<CompileError, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<CompileError, String> param) {
                return new SimpleStringProperty(Long.toString(param.getValue().getLineNumber()));
            }
        });
        setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        getColumns().addAll(tc1, tc2, tc3, tc4);
    }

}
