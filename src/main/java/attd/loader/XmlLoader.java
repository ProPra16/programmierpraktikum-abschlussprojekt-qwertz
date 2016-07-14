package attd.loader;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class XmlLoader<T> {

    // zum Parsen und Erstellen von XML-Dokumenten
    private final DocumentBuilder documentBuilder;
    // zum Speichern von XML-Dateien
    private final Transformer transformer;

    public XmlLoader() {
        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
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

    protected static Node getFirst(Element element, String tagName) {
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList.getLength() >= 1)
            return nodeList.item(0);
        else
            return null;
    }

    protected static String getFirstTextContent(Element element, String tagName, String defaultValue) {
        Node node = getFirst(element, tagName);
        return node != null ? node.getTextContent() : defaultValue;
    }

    protected static Element appendElementWithText(Document document, Element parent, String tagName, String text) {
        Element element = document.createElement(tagName);
        element.setTextContent(text);
        parent.appendChild(element);
        return element;
    }

    protected final T readFromFile(Path path) throws IOException, SAXException {
        InputStream inputStream = Files.newInputStream(path);
        Document document = documentBuilder.parse(inputStream);
        document.setDocumentURI(path.toString());
        T result = read(document);
        documentBuilder.reset();
        return result;
    }

    protected final void saveToFile(T value, Path path) throws IOException {
        Document document = documentBuilder.newDocument();
        documentBuilder.reset();
        document.setXmlStandalone(true);
        write(document, value);

        try (OutputStream outputStream = Files.newOutputStream(path)) {
            transformer.transform(new DOMSource(document), new StreamResult(outputStream));
        } catch (TransformerException e) {
            throw new IOException(e);
        }
    }

    protected abstract T read(Document document);

    protected abstract void write(Document document, T value);
}