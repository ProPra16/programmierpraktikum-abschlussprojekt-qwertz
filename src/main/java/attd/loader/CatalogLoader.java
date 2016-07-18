package attd.loader;

import attd.core.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public final class CatalogLoader extends XmlLoader<Catalog> {

    public Catalog load(String path) {
        try {
            return readFromFile(Paths.get(path));
        } catch (IOException | SAXException e) {
            return null;
        }
    }

    public boolean save(Catalog catalog, String path) {
        try {
            saveToFile(catalog, Paths.get(path, catalog.getName() + ".xml"));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    protected Catalog read(Document document) {
        Element rootElement = document.getDocumentElement();
        if (!rootElement.getTagName().equals("catalog"))
            return null;
        List<Exercise> exercises = new ArrayList<>();
        Element exercisesElement = (Element) getFirst(rootElement, "exercises");
        if (exercisesElement != null) {
            NodeList exerciseElements = exercisesElement.getElementsByTagName("exercise");
            for (int i = 0; i < exerciseElements.getLength(); i++) {
                Element exerciseElement = (Element) exerciseElements.item(i);

                String name = getFirstTextContent(exerciseElement, "name", "");
                String description = getFirstTextContent(exerciseElement, "description", "");

                Configurations configurations = readConfiguration((Element) getFirst(exerciseElement, "configuration"));
                Codes codes = readCodes((Element) getFirst(exerciseElement, "codes"));

                List<Version> versions = new ArrayList<>();
                Element versionsElement = (Element) getFirst(exerciseElement, "versions");
                if (versionsElement != null) {
                    NodeList versionElements = versionsElement.getElementsByTagName("version");
                    for (int j = 0; j < versionElements.getLength(); j++) {
                        Element versionElement = (Element) versionElements.item(j);
                        String versionName = getFirstTextContent(versionElement, "name", "");

                        State state = configurations.AcceptanceTestEnabled() ? State.acceptanceTest
                                : State.writeFailingTest;
                        String stateValue = getFirstTextContent(versionElement, "state", null);
                        if (stateValue != null) {
                            try {
                                state = Enum.valueOf(State.class, stateValue);
                            } catch (Exception ignored) {
                            }
                        }

                        Codes versionCodes = readCodes((Element) getFirst(versionElement, "codes"));
                        versions.add(new Version(versionName, versionCodes, state));
                    }
                }

                exercises.add(new Exercise(name, description, versions, configurations, codes));
            }
        }
        Path xmlPath = Paths.get(document.getDocumentURI());
        String xmlName = xmlPath.getFileName().toString();
        if (xmlName.endsWith(".xml"))
            xmlName = xmlName.substring(0, xmlName.lastIndexOf('.'));
        return new Catalog(xmlName, exercises);
    }

    private Configurations readConfiguration(Element configurationElement) {
        int babyStepsTime = 0;
        boolean acceptanceTestsEnabled = false;
        if (configurationElement != null) {
            String babySteps = getFirstTextContent(configurationElement, "babysteps", null);
            if (babySteps != null) {
                try {
                    babyStepsTime = Integer.parseInt(babySteps);
                } catch (NumberFormatException ignored) {
                }
            }
            String acceptanceTests = getFirstTextContent(configurationElement, "acceptancetests", null);
            if ("true".equals(acceptanceTests))
                acceptanceTestsEnabled = true;
        }
        return new Configurations(babyStepsTime, acceptanceTestsEnabled);
    }

    private Codes readCodes(Element codesElement) {
        String testCode = null;
        String classCode = null;
        String acceptanceCode = null;
        boolean acceptanceImplemented = false;
        if (codesElement != null) {
            testCode = getFirstTextContent(codesElement, "testcode", null);
            classCode = getFirstTextContent(codesElement, "classcode", null);
            Element acceptanceElement = (Element) getFirst(codesElement, "acceptancecode");
            if (acceptanceElement != null) {
                if (acceptanceElement.hasAttribute("implemented")) {
                    String attributeValue = acceptanceElement.getAttribute("implemented");
                    acceptanceImplemented = Boolean.parseBoolean(attributeValue);
                }
                acceptanceCode = acceptanceElement.getTextContent();
            }
        }
        return new Codes(new Code(testCode), new Code(classCode),
                new AcceptanceCode(acceptanceCode, acceptanceImplemented));
    }

    @Override
    protected void write(Document document, Catalog catalog) {
        Element rootElement = document.createElement("catalog");
        Element exercisesElement = document.createElement("exercises");

        for (Exercise exercise : catalog.getExercises()) {
            Element exerciseElement = document.createElement("exercise");

            appendElementWithText(document, exerciseElement, "name", exercise.getName());
            appendElementWithText(document, exerciseElement, "description", exercise.getDescription());

            Configurations configuration = exercise.getConfigurations();
            Element configurationElement = document.createElement("configuration");
            appendElementWithText(document, configurationElement, "babysteps",
                    Integer.toString(configuration.getTime()));
            appendElementWithText(document, configurationElement, "acceptancetests",
                    Boolean.toString(configuration.AcceptanceTestEnabled()));
            exerciseElement.appendChild(configurationElement);

            Codes codes = exercise.getCodes();
            Element codesElement = document.createElement("codes");
            writeCodes(document, codesElement, codes, false);
            exerciseElement.appendChild(codesElement);

            Element versionsElement = document.createElement("versions");
            for (Version version : exercise.getVersions()) {
                Element versionElement = document.createElement("version");
                appendElementWithText(document, versionElement, "name", version.getName());
                appendElementWithText(document, versionElement, "state", version.getState().name());

                codes = version.getCodes();
                codesElement = document.createElement("codes");
                writeCodes(document, codesElement, codes, true);
                versionElement.appendChild(codesElement);

                versionsElement.appendChild(versionElement);
            }
            exerciseElement.appendChild(versionsElement);
            exercisesElement.appendChild(exerciseElement);
        }

        rootElement.appendChild(exercisesElement);
        document.appendChild(rootElement);
    }

    private void writeCodes(Document document, Element codesElement, Codes codes, boolean writeAcceptanceImplemented) {
        String testCode = codes.getTestCode().getCode();
        String classCode = codes.getClassCode().getCode();
        String acceptanceCode = codes.getAcceptanceCode().getCode();
        if (testCode != null)
            appendElementWithText(document, codesElement, "testcode", testCode);
        if (classCode != null)
            appendElementWithText(document, codesElement, "classcode", classCode);
        if (acceptanceCode != null) {
            Element acceptanceElement = appendElementWithText(document, codesElement, "acceptancecode", acceptanceCode);
            if (writeAcceptanceImplemented) {
                boolean isImplemented = codes.getAcceptanceCode().implementedProperty().get();
                acceptanceElement.setAttribute("implemented", Boolean.toString(isImplemented));
            }
        }
    }
}