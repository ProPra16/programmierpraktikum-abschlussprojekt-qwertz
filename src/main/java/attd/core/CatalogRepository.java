package attd.core;

import attd.loader.CatalogLoader;
import attd.loader.Config;
import attd.loader.ConfigLoader;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.stage.FileChooser;

import java.io.File;

public final class CatalogRepository {

	private final CatalogLoader catalogLoader;
	private final ConfigLoader configLoader;
	private Catalog catalog;
	private ListProperty<Exercise> catalog1 = new SimpleListProperty<>();
	
	public CatalogRepository() {
		catalogLoader = new CatalogLoader();
		configLoader = new ConfigLoader();
	}
		
	public ListProperty<Exercise> catalogProperty(){return catalog1;}
	
	public boolean load() {
		Config config = configLoader.load();
		if(config == null){return false;}
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(new File(config.getWorkspace()));
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML-Dateien", "*.xml"));
		File file = fileChooser.showOpenDialog(null);
		if (file != null) {
			catalog = catalogLoader.load(file.getAbsolutePath());
			catalog1.set(FXCollections.observableList(catalog.getExercises()));
		}
		
		return true;
	}

	public void save() {
		if (catalog != null) {
			Config config = configLoader.load();
			if (config != null)
				catalogLoader.save(catalog, config.getWorkspace());
		}
	}
}