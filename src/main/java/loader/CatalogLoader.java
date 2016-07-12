package loader;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import Attd.AcceptanceCode;
import Attd.Catalog;
import Attd.CatalogItem;
import Attd.Code;
import Attd.Codes;
import Attd.Configurations;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class CatalogLoader implements ICatalogLoader {

    // zum Parsen und Erstellen von XML-Dokumenten
    private final DocumentBuilder documentBuilder;
    // zum Speichern von XML-Dateien
    private final Transformer transformer;

    public CatalogLoader() {
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
    public List<Catalog> getCatalogs(String path) throws IOException {
        Path catalogDirectory = Paths.get(path);
        List<Catalog> catalogs = new ArrayList<>();
        Files.walkFileTree(catalogDirectory, new SimpleFileVisitor<Path>() {

            private int index = 0;
            private List<CatalogItem> currentCatalogItems;

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                index++;
                if (index > 2) {
                    index--;
                    return FileVisitResult.SKIP_SUBTREE;
                }
                if (index == 2) {
                    if (Files.notExists(dir.resolve("RawCatalogItem.xml"))) {
                        index--;
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                    currentCatalogItems = new ArrayList<>();
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                index--;
                if (index == 1) {
                    Catalog catalog = new Catalog(dir.getFileName().toString(), "", currentCatalogItems);
                    catalogs.add(catalog);
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (index == 2) {
                    if (file.getFileName().toString().startsWith("CatalogItem-")) {
                        currentCatalogItems.add(readCatalogItem(file));
                    }
                }
                return FileVisitResult.CONTINUE;
            }
        });
        return catalogs;
    }

    @Override
    public void save(List<Catalog> catalogs, String path) throws IOException {
        Path p = Paths.get(path);
        for (Catalog catalog : catalogs) {
            Path catalogDirectory = p.resolve(catalog.getCatalogName());
            if (Files.notExists(catalogDirectory)) Files.createDirectory(catalogDirectory);
            for (CatalogItem catalogItem : catalog.getCatalogItems()) {
                String fileName = "CatalogItem-" + catalogItem.getVersion() + ".xml";
                saveCatalogItem(catalogItem, catalogDirectory.resolve(fileName));
            }
        }
    }

    private void saveCatalogItem(CatalogItem item, Path path) {
        try {
            // XML-Dokument erstellen
            Document document = documentBuilder.newDocument();
            document.setXmlStandalone(true);

            Codes codes = item.getCodes();
            Element codesElement = document.createElement("codes");
            Element testCodeElement = document.createElement("testcode");
            testCodeElement.setTextContent(codes.getTestCode().getCode());
            Element classCodeElement = document.createElement("classcode");
            classCodeElement.setTextContent(codes.getClassCode().getCode());
            Element acceptanceCodeElement = document.createElement("acceptancecode");
            acceptanceCodeElement.setTextContent(codes.getAcceptanceCode().getCode());
            codesElement.appendChild(testCodeElement);
            codesElement.appendChild(classCodeElement);
            codesElement.appendChild(acceptanceCodeElement);

            Element rootElement = document.createElement("catalogitem");
            document.appendChild(rootElement);
            rootElement.appendChild(codesElement);

            documentBuilder.reset();
            // XML-Dokument in Datei speichern
            OutputStream outputStream = Files.newOutputStream(path);
            transformer.transform(new DOMSource(document), new StreamResult(outputStream));
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private CatalogItem readCatalogItem(Path path) throws IOException {
        // XML-Datei parsen
        InputStream inputStream = Files.newInputStream(path);
        Document document;
        try {
            document = documentBuilder.parse(inputStream);
        } catch (SAXException e) {
            e.printStackTrace();
            return null;
        }

        String version = path.getFileName().toString().substring("CatalogItem-".length());
        version = version.substring(0, version.lastIndexOf('.'));

        Element rootElement = document.getDocumentElement();
        if (!rootElement.getTagName().equals("catalogitem")) return null;
        Element codesElement = (Element) rootElement.getElementsByTagName("codes").item(0);
        Node testCodeElement = codesElement.getElementsByTagName("testcode").item(0);
        Node classCodeElement = codesElement.getElementsByTagName("classcode").item(0);
        Node acceptanceCodeElement = codesElement.getElementsByTagName("acceptancecode").item(0);
        Codes codes = new Codes(new Code(testCodeElement.getTextContent()),
                new Code(classCodeElement.getTextContent()), new AcceptanceCode(acceptanceCodeElement.getTextContent(), false));

        documentBuilder.reset();
        return new CatalogItem(version, codes, null);
    }

    CatalogItem getRawCatalogItem(String path, Catalog catalog, String version) {
        Path filePath = Paths.get(path, catalog.getCatalogName(), "RawCatalogItem.xml");

        // XML-Datei parsen
        InputStream inputStream = null;
        try {
            inputStream = Files.newInputStream(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Document document = null;
        try {
            try {
                document = documentBuilder.parse(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (SAXException e) {
            e.printStackTrace();
            return null;
        }

        Element rootElement = document.getDocumentElement();
        if (!rootElement.getTagName().equals("catalogitem")) return null;
        Element configurationElement = (Element) rootElement.getElementsByTagName("configuration").item(0);
        NodeList babyStepsElements = configurationElement.getElementsByTagName("babysteps");
        int babyStepsTime = 0;
        if (babyStepsElements.getLength() > 0) {
            try {
                babyStepsTime = Integer.parseInt(babyStepsElements.item(0).getTextContent());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        NodeList acceptanceTestElements = configurationElement.getElementsByTagName("acceptancetestenabled");
        boolean acceptanceTestEnabled = (acceptanceTestElements.getLength() > 0 &&
                acceptanceTestElements.item(0).getTextContent().equals("true"));
        Configurations configurations = new Configurations(babyStepsTime, acceptanceTestEnabled);

        Element codesElement = (Element) rootElement.getElementsByTagName("codes").item(0);
        Node testCodeElement = codesElement.getElementsByTagName("testcode").item(0);
        Node classCodeElement = codesElement.getElementsByTagName("classcode").item(0);
        Node acceptanceCodeElement = codesElement.getElementsByTagName("acceptancecode").item(0);
        Codes codes = new Codes(new Code(testCodeElement.getTextContent()),
                new Code(classCodeElement.getTextContent()), new AcceptanceCode(acceptanceCodeElement.getTextContent(), false));

        documentBuilder.reset();
        return new CatalogItem(version, codes, configurations);
    }
}