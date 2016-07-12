package loader;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ConfigLoader implements IConfigLoader {

    // zum Parsen und Erstellen von XML-Dokumenten
    private final DocumentBuilder documentBuilder;
    // zum Speichern von XML-Dateien
    private final Transformer transformer;

    public ConfigLoader() {
        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            builderFactory.setValidating(true);
            documentBuilder = builderFactory.newDocumentBuilder();
            documentBuilder.setErrorHandler(null);

            transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        } catch (ParserConfigurationException | TransformerConfigurationException e) {
            throw new RuntimeException("XML not supported", e);
        }
    }

    @Override
    public void save(Config config, String path) {
        try {
            // XML-Dokument erstellen
            Document document = documentBuilder.newDocument();
            document.setXmlStandalone(true);
            appendConfig(document, config);
            documentBuilder.reset();
            // XML-Dokument in Datei speichern
            OutputStream outputStream = Files.newOutputStream(Paths.get(path));
            transformer.transform(new DOMSource(document), new StreamResult(outputStream));
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Config getConfig(String path) {
        try {
            // XML-Datei parsen
            InputStream inputStream = Files.newInputStream(Paths.get(path));
            Document document = documentBuilder.parse(inputStream);
            // Config auslesen
            Config config = readConfig(document);
            documentBuilder.reset();
            return config;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void appendConfig(Document document, Config config) {
        Element rootElement = document.createElement("config");
        document.appendChild(rootElement);
        Element workspaceElement = document.createElement("workspace");
        workspaceElement.setTextContent(config.getWorkspace());
        rootElement.appendChild(workspaceElement);
    }

    private Config readConfig(Document document) {
        Element rootElement = document.getDocumentElement();
        if (!rootElement.getTagName().equals("config")) return null;
        Node workspaceElement = rootElement.getElementsByTagName("workspace").item(0);
        String workspace = workspaceElement.getTextContent();
        return new Config(workspace);
    }
}