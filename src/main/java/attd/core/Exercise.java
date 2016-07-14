package attd.core;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public final class Exercise {

	private final String name, description;
	private final Configurations configurations;
	private final Codes codes;
	private final List<Version> versions;
	private final ObjectProperty<ObservableList<Version>> versionsProperty = new SimpleObjectProperty<>();

	public Exercise(String name, String description, List<Version> versions, Configurations configurations, Codes codes) {
		this.name = name;
		this.description = description;
		this.configurations = configurations;
		this.codes = codes;
		this.versions = versions;
		versionsProperty.set(FXCollections.observableList(versions));
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public Codes getCodes() {
		return codes;
	}

	public List<Version> getVersions() {
		return versions;
	}

	public Configurations getConfigurations() {
		return configurations;
	}

	public void addVersion(String name) {
		Codes newCodes = new Codes(new Code(codes.getTestCode().getCode()), new Code(codes.getClassCode().getCode()),
				new AcceptanceCode(codes.getAcceptanceCode().getCode(), false));
		State state = configurations.AcceptanceTestEnabled() ? State.acceptanceTest : State.writeFailingTest;
		versionsProperty.get().add(new Version(name, newCodes, state));
	}

	public ObjectProperty<ObservableList<Version>> versionsProperty() {
		return versionsProperty;
	}
}