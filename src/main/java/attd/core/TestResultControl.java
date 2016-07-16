package attd.core;

import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import vk.core.api.TestFailure;
import vk.core.api.TestResult;

import java.util.ArrayList;


/**
 * Created by student on 16/07/16.
 */
public class TestResultControl extends GridPane {
    private Label l1 = new Label();
    private Label l2 = new Label();
    private Label l3 = new Label();
    private Label l4 = new Label();
    private Button button = new Button("Testfehler");
    private TestResult testResult;

    public TestResultControl() {
        for (int j = 0; j < 4; j++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setHgrow(Priority.ALWAYS);
            getColumnConstraints().add(cc);
        }
        add(l1, 0, 0);
        add(l2, 1, 0);
        add(l3, 2, 0);
        add(l4, 3, 0);
        add(button, 4, 0);
        button.setVisible(false);
        button.setOnAction(e -> {
            Stage stage = new Stage();
            stage.setScene(new Scene(new TestResultControl2(testResult)));
            stage.setWidth(700);
            stage.setHeight(300);
            stage.show();
        });
    }


    public ObjectProperty<TestResult> ItemProperty() {
        return new ObjectProperty<TestResult>() {
            @Override
            public TestResult get() {
                return testResult;
            }

            @Override
            public void bind(ObservableValue<? extends TestResult> observable) {
                observable.addListener((observable1, oldValue, newValue) -> {
                    l1.setText("erfolgreiche Tests: " + newValue.getNumberOfSuccessfulTests());
                    l2.setText("fehlgeschlagene Tests: " + newValue.getNumberOfFailedTests());
                    l3.setText("ignorierten Tests: " + newValue.getNumberOfIgnoredTests());
                    l4.setText("Laufzeit: " + newValue.getTestDuration().toString());
                    testResult = newValue;
                    button.setVisible(true);
                });
            }

            @Override
            public void unbind() {

            }

            @Override
            public boolean isBound() {
                return false;
            }

            @Override
            public Object getBean() {
                return null;
            }

            @Override
            public String getName() {
                return null;
            }

            @Override
            public void addListener(ChangeListener<? super TestResult> listener) {

            }

            @Override
            public void removeListener(ChangeListener<? super TestResult> listener) {

            }

            @Override
            public void addListener(InvalidationListener listener) {

            }

            @Override
            public void removeListener(InvalidationListener listener) {

            }

            @Override
            public void set(TestResult value) {

            }
        };
    }

}
