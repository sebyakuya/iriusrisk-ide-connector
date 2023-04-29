package com.iriusrisk.ide.connector.services;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.iriusrisk.ide.connector.controllers.IriusRiskMainController;
import com.iriusrisk.ide.connector.models.IriusRiskRelation;
import com.iriusrisk.ide.connector.settings.AppSettingsState;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class IriusRiskAPI {

    private HashMap<String, String> functionalComponents;
    private HashMap<String, String> reversedFunctionalComponents;

    public IriusRiskAPI() {
        functionalComponents = getFunctionalComponents();
        reversedFunctionalComponents = getReversedFunctionalComponents();
    }

    public HashMap<String, String> getFunctionalComponents() {
        HashMap<String, String> components = new HashMap<>();
        components.put("", "");
        components.put("empty-component", "Empty Component");
        components.put("out-of-scope", "Out Of Scope");
        components.put("CD-ACCESS-TOKEN", "Access Token");
        components.put("CD-ADMINISTRATION-INTERFACE", "Administration interface");
        components.put("CD-API-ENDPOINT", "API Endpoint");
        components.put("CD-AUDIT-LOG", "Audit Log");
        components.put("CD-CHANGE-PASSWORD", "Change Password");
        components.put("CD-EXCEPTION-HANDLER", "Exception Handler");
        components.put("CD-FILE-CHOOSER", "File Chooser");
        components.put("CD-FILE-HANDLER", "File Handler");
        components.put("CD-FORMATTER", "Formatter");
        components.put("CD-JSON-PROCESSING-SERVICE", "JSON processing service");
        components.put("CD-JWT-TOKEN", "JWT token");
        components.put("CD-LOGIN", "Login");
        components.put("CD-PRIVATE-SIGNATURE-KEY", "Private signature key");
        components.put("CD-RESET-PASSWORD", "Reset Password");
        components.put("CD-SESSION-IDENTIFIER", "Session identifier");
        components.put("CD-SUBDOMAIN-DNS-ENTRY-CONFIGURATION", "Subdomain DNS-entry configuration");
        components.put("CD-URL-REDIRECTION", "URL Redirection");
        components.put("CD-USER-PROFILE", "User Profile");
        components.put("CD-USER-REGISTRATION", "User Registration");
        components.put("CD-WEB-FORM", "Web Form");
        components.put("CD-WYSIWYG-EDITOR", "WYSIWYG editor");
        components.put("CD-XML-PROCESSING", "XML processing");
        components.put("CD-XPATH-QUERY", "XPATH query");

        // TODO: Ensure that, at some point in the future, the following functional components are returned
        // DTO, Unit test, UI component, Validator, Builder, Constants, Main

        return components;
    }

    public TreeSet<String> getFunctionalComponentsOrdered() {
        return new TreeSet<>(functionalComponents.values());
    }

    public HashMap<String, String> getReversedFunctionalComponents() {
        HashMap<String, String> components = new HashMap<>();

        for (Map.Entry<String, String> e : getFunctionalComponents().entrySet()) {
            components.put(e.getValue(), e.getKey());
        }

        return components;
    }

    public String testConnection() throws IOException {
        AppSettingsState appSettingsState = AppSettingsState.getInstance();
        String result;
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(appSettingsState.iriusrisk_url + "/health")
                .build();

        Call call = client.newCall(request);
        Response response = call.execute();

        if (response.code() != 200) {
            result = "Failed (Status code: " + response.code() + ")";
        } else {
            String body = Objects.requireNonNull(response.body()).string();
            if (body.contains("apiVersion")) {
                result = "Connection with " + appSettingsState.iriusrisk_url + " successful.";
            } else {
                result = "Failed (API version cannot be retrieved)";
            }
        }

        return result;
    }

    public String testAPIToken() throws IOException {
        AppSettingsState appSettingsState = AppSettingsState.getInstance();

        String result = downloadThreatModel();
        if (result.contains("project")) {
            result = "Details from " + appSettingsState.iriusrisk_project + " project successfully retrieved.";
        }

        return result;
    }

    public String updateThreatModel(IriusRiskMainController controller) {
        // TODO: Because the API is not very useful for this we have no choice but download the entire threat model, update it and upload it again
        // This would be as easy as calling a CRUD component endpoint with the changes :(
        // Hence, this entire code is absurd but at least it works

        String result;

        try {
            // 1. Download the current threat model
            String xml = downloadThreatModel();

            // 2. Extract components
            HashMap<String, String> map = extractComponents(xml);

            // 3. Compare with components in plugin
            ByteArrayInputStream output = compareComponents(xml, map);

            // 4. Upload threat model to IriusRisk
            result = uploadToInstance(output, controller);

        } catch (Exception e) {
            result = "Something went wrong when uploading to IriusRisk: " + e.getClass();
        }


        return result;
    }

    private String downloadThreatModel() throws IOException {
        AppSettingsState appSettingsState = AppSettingsState.getInstance();
        String result;
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(appSettingsState.iriusrisk_url + "/api/v1/products/" + appSettingsState.iriusrisk_project)
                .addHeader("Accept", "application/xml")
                .addHeader("api-token", appSettingsState.iriusrisk_token)
                .build();

        Call call = client.newCall(request);
        Response response = call.execute();

        if (response.code() != 200) {
            result = "Failed (Status code: " + response.code() + ")";
        } else {
            result = Objects.requireNonNull(response.body()).string();

        }

        return result;
    }

    private HashMap<String, String> extractComponents(String xml) throws ParserConfigurationException, IOException, SAXException {

        Document doc = loadDocument(xml);
        HashMap<String, String> map = new HashMap<>();

        NodeList componentList = doc.getElementsByTagName("component");
        for (int i = 0; i < componentList.getLength(); i++) {
            Node component = componentList.item(i);
            String name = component.getAttributes().getNamedItem("name").getTextContent();
            String componentDefinitionRef = component.getAttributes().getNamedItem("componentDefinitionRef").getTextContent();
            map.put(name, componentDefinitionRef);
        }

        return map;

    }

    private ByteArrayInputStream compareComponents(String xml, HashMap<String, String> map) throws IOException, SAXException, ParserConfigurationException, TransformerException {
        AppSettingsState appSettingsState = AppSettingsState.getInstance();
        Project openProject = ProjectManager.getInstance().getOpenProjects()[0];

        // First part is to separate components and actions
        HashSet<IriusRiskRelation> componentsToAddInThreatModel = new HashSet<>();
        HashSet<IriusRiskRelation> componentsToUpdateInThreatModel = new HashSet<>();
        HashSet<IriusRiskRelation> componentsToRemoveFromThreatModel = new HashSet<>();

        for (IriusRiskRelation r : appSettingsState.relations) {
            if (r.getProject().equals(openProject.getBasePath())) {

                if (!map.containsKey(r.getClassName())) {
                    if (!"".equals(r.getComponent())) {
                        System.out.println("New component " + r.getClassName());
                        componentsToAddInThreatModel.add(r);
                    }
                }

                if (map.containsKey(r.getClassName())) {
                    // If the element is in the threat model but the component is not the same it has to be updated
                    if (!map.get(r.getClassName()).equals(reversedFunctionalComponents.get(r.getComponent()))) {
                        System.out.println("To update " + r.getClassName());
                        componentsToUpdateInThreatModel.add(r);
                    }
                }

            }
        }

        Set<String> savedComponentNames = appSettingsState.relations.stream().map(IriusRiskRelation::getClassName).collect(Collectors.toSet());

        for (String k : map.keySet()) {
            // If the component list in the plugin doesn't contain the element from the threat model it means that we must remove it
            if (!savedComponentNames.contains(k)) {
                System.out.println("To remove " + k);
                componentsToRemoveFromThreatModel.add(new IriusRiskRelation("", k, "", "", ""));
            }
        }

        // Now that we have the modifications to do, we have to modify the original XML file to do the changes
        Document doc = loadDocument(xml);
        Node componentsNode = doc.getElementsByTagName("components").item(0);
        NodeList componentList = doc.getElementsByTagName("component");


        for (int i = 0; i < componentList.getLength(); i++) {
            Node component = componentList.item(i);
            String name = component.getAttributes().getNamedItem("name").getTextContent();

            for (IriusRiskRelation r : componentsToUpdateInThreatModel) {
                if (r.getClassName().equals(name)) {
                    // Though in the XML the node is removed I cannot remove the component using the Update threat model function in IR
                    System.out.println("This component should be updated");
//                    componentsToRemoveFromThreatModel.add(r);
//                    componentsToAddInThreatModel.add(r);
                }
            }
        }

        // For each component in the XML, if the component has to be removed it will be removed from the parent node
        Set<Node> targetElements = new HashSet<>();
        for (int i = 0; i < componentList.getLength(); i++) {
            Node component = componentList.item(i);
            String name = component.getAttributes().getNamedItem("name").getTextContent();

            for (IriusRiskRelation r : componentsToRemoveFromThreatModel) {
                if (r.getClassName().equals(name)) {
                    // Though in the XML the node is removed I cannot remove the component using the Update threat model function in IR
                    targetElements.add(component);
                }
            }
        }
        for (Node e : targetElements) {
            e.getParentNode().removeChild(e);
        }

        // For each component that has to be added to the threat model a new node will be created
        for (IriusRiskRelation r : componentsToAddInThreatModel) {
            Element newComponent = doc.createElement("component");
            newComponent.setAttribute("uuid", UUID.randomUUID().toString());
            newComponent.setAttribute("diagramComponentId", UUID.randomUUID().toString());
            newComponent.setAttribute("ref", r.getClassName().toLowerCase());
            newComponent.setAttribute("name", r.getClassName());
            newComponent.setAttribute("desc", "");
            newComponent.setAttribute("library", "IR-Functional-Components");
            newComponent.setAttribute("parentComponentRef", r.getParent().toLowerCase());
            newComponent.setAttribute("componentDefinitionRef", reversedFunctionalComponents.get(r.getComponent()));
            newComponent.setAttribute("generatedByRules", "false");
            newComponent.setAttribute("persistent", "false");
            newComponent.appendChild(doc.createElement("tags"));
            newComponent.appendChild(doc.createElement("questions"));
            Element trustzones = doc.createElement("trustZones");
            Element trustzone = doc.createElement("trustZone");
            trustzone.setAttribute("ref", "internet");
            trustzones.appendChild(trustzone);
            newComponent.appendChild(trustzones);
            newComponent.appendChild(doc.createElement("assets"));
            newComponent.appendChild(doc.createElement("settings"));
            newComponent.appendChild(doc.createElement("weaknesses"));
            newComponent.appendChild(doc.createElement("countermeasures"));
            newComponent.appendChild(doc.createElement("usecases"));
            componentsNode.appendChild(newComponent);
        }


        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Result output = new StreamResult(outputStream);
        File file = new File(openProject.getBasePath() + "/" + appSettingsState.iriusrisk_project + ".xml");
        Result fileoutput = new StreamResult(file);
        Source input = new DOMSource(doc);
        transformer.transform(input, output);
        transformer.transform(input, fileoutput);


        return new ByteArrayInputStream(outputStream.toByteArray());

    }

    private String uploadToInstance(ByteArrayInputStream output, IriusRiskMainController controller) {

        new Thread(() -> {
            AppSettingsState appSettingsState = AppSettingsState.getInstance();
            String result;
            OkHttpClient client = new OkHttpClient.Builder().readTimeout(30, TimeUnit.SECONDS).build();

            RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("fileName", "file.xml", RequestBody.create(output.readAllBytes(), MediaType.parse("text/xml")))
                    .addFormDataPart("deleteCountermeasures", "false")
                    .build();

            Request request = new Request.Builder()
                    .url(appSettingsState.iriusrisk_url + "/api/v1/products/upload/" + appSettingsState.iriusrisk_project)
                    .addHeader("Accept", "application/json")
                    .addHeader("Content-Type", "multipart/form-data")
                    .addHeader("api-token", appSettingsState.iriusrisk_token)
                    .post(requestBody)
                    .build();

            Call call = client.newCall(request);
            Response response = null;
            try {
                response = call.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

            assert response != null;
            if (response.code() != 200) {
                result = "Failed (Status code: " + response.code() + ")";
            } else {
                result = "Successfully uploaded to IriusRisk!";
            }
            controller.setStatusText(result);

        }).start();


        return "Threat model is being uploaded to IriusRisk, this might take a while...";

    }

    private Document loadDocument(String xml) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        dbFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        dbFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        dbFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        dbFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        dbFactory.setXIncludeAware(false);
        dbFactory.setExpandEntityReferences(false);

        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xml));
        return dBuilder.parse(is);
    }


}
