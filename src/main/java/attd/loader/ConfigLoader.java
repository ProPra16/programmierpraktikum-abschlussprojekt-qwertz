package attd.loader;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class ConfigLoader extends XmlLoader<Config> {

	private Path getConfigPath() {
		return Paths.get(System.getProperty("user.dir"), "config.xml");
	}

	public Config load() {
		try {
			return readFromFile(getConfigPath());
		} catch (IOException | SAXException e) {
			return null;
		}
	}

	public boolean save(Config config) {
		try {
			saveToFile(config, getConfigPath());
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	@Override
	protected Config read(Document document) {
		Element rootElement = document.getDocumentElement();
		if (!rootElement.getTagName().equals("config"))
			return null;
		String workspace = getFirstTextContent(rootElement, "workspace", null);
		return new Config(workspace);
	}

	@Override
	protected void write(Document document, Config config) {
		Element rootElement = document.createElement("config");
		appendElementWithText(document, rootElement, "workspace", config.getWorkspace());
		document.appendChild(rootElement);
	}
}