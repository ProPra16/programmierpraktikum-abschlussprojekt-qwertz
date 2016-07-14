package attd.core;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public final class AcceptanceCode extends Code {

    private BooleanProperty implemented = new SimpleBooleanProperty();

    public AcceptanceCode(String code, boolean fullyImplemented) {
        super(code);
        implemented.set(fullyImplemented);
    }

    public BooleanProperty implementedProperty() {
        return implemented;
    }
}