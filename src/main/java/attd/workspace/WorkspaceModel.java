package attd.workspace;

import java.io.File;

import attd.loader.Config;
import attd.loader.ConfigLoader;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class WorkspaceModel {
	private StringProperty path = new SimpleStringProperty();
	ConfigLoader configLoader = new ConfigLoader();

	public WorkspaceModel() {
		if (configLoader.load() != null) {
			path.set(configLoader.load().getWorkspace());
		}
	}

	public void cancel() {
		System.exit(0);
	}

	public void browse() {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		File file = directoryChooser.showDialog(null);
		if (file != null) {
			path.set(file.getAbsolutePath());
		}
		System.out.println(file!=null);
	}

	public StringProperty pathProperty() {
		return path;
	}

	public void createDirectory(ActionEvent param) {
		configLoader.save(new Config(path.get()));
		((Stage) ((Node) param.getSource()).getScene().getWindow()).close();
	}
	
}
