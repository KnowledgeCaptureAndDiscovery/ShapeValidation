package edu.isi.mint;

import edu.isi.mint.model.NodeShape;
import edu.isi.mint.model.PropertyShape;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//TODO This should be a REST API - Convert to Spring Boot MVC
public class ShapeConverterApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShapeConverterApplication.class);
    private static final Marker WTF_MARKER = MarkerFactory.getMarker("WTF");
    private static final String PREFIX = "http://example.com/ns#";
    private static final String SHACL = "http://www.w3.org/ns/shacl#";

    public static void main(String[] args) {
        try {
            // TODO This file should be uploaded by consumer of the REST API
            Path path = Paths.get(".").toAbsolutePath().normalize();
            String ont = "file:" + path.toFile().getAbsolutePath() + "/src/main/resources/ontology.ttl";

            Model model = ModelFactory.createDefaultModel();
            model.read(ont);

            // Get all Classes
            Map<String, NodeShape> classMap = new HashMap<>();
            ResIterator classIter = model.listResourcesWithProperty(RDF.type, OWL.Class);
            while (classIter.hasNext()) {
                Resource r = classIter.nextResource();
                if (null != r.getURI()) {
                    NodeShape nodeShape = new NodeShape(r.getURI());
                    nodeShape.setLocalName(r.getLocalName() + "Shape");
                    classMap.put(r.getURI(), nodeShape);
                    /*System.out.println("Next Resource: " + r.getURI());
                    StmtIterator sIter = r.listProperties();
                    System.out.println("Properties: ");
                    while (sIter.hasNext()) {
                        Statement s = sIter.nextStatement();
                        System.out.println(s.getSubject() + " " + s.getPredicate() + " " + s.getObject());
                    }*/
                }
            }

            // Get All Properties
            Map<String, PropertyShape> propertyMap = new HashMap<>();
            ResIterator objPropIter = model.listResourcesWithProperty(RDF.type, OWL.ObjectProperty);
            while (objPropIter.hasNext()) {
                Resource r = objPropIter.nextResource();
                if (null != r.getURI()) {
                    PropertyShape propertyShape = new PropertyShape(r.getURI());
                    propertyShape.setLocalName(r.getLocalName());

                    StmtIterator sIter = r.listProperties();
                    while (sIter.hasNext()) {
                        Statement s = sIter.nextStatement();
                        if (s.getPredicate().equals(RDFS.domain)) {
                            propertyShape.setDomain(s.getObject().asResource().getURI());
                        } else if (s.getPredicate().equals(RDFS.range)) {
                            propertyShape.setRange(s.getObject().toString());
                        }
                    }

                    propertyMap.put(r.getURI(), propertyShape);
                }
            }

            ResIterator dataPropIter = model.listResourcesWithProperty(RDF.type, OWL.DatatypeProperty);
            while (dataPropIter.hasNext()) {
                Resource r = dataPropIter.nextResource();
                if (null != r.getURI()) {
                    PropertyShape propertyShape = new PropertyShape(r.getURI());
                    propertyShape.setLocalName(r.getLocalName());

                    StmtIterator sIter = r.listProperties();
                    while (sIter.hasNext()) {
                        Statement s = sIter.nextStatement();
                        if (s.getPredicate().equals(RDFS.domain)) {
                            propertyShape.setDomain(s.getObject().asResource().getURI());
                        } else if (s.getPredicate().equals(RDFS.range)) {
                            propertyShape.setRange(s.getObject().toString());
                        }
                    }
                    propertyMap.put(r.getURI(), propertyShape);
                }
            }

            for (Map.Entry<String, PropertyShape> entry : propertyMap.entrySet()) {
                String domain = entry.getValue().getDomain();
                if (null != domain) {
                    NodeShape shape = classMap.get(domain);
                    List<PropertyShape> properties = shape.getProperties();
                    if (null == properties) {
                        properties = new ArrayList<>();
                    }
                    properties.add(entry.getValue());
                    shape.setProperties(properties);
                    classMap.put(domain, shape);
                }
            }

            // Write to Shacl file
            String shapePath = path.toFile().getAbsolutePath() + "/src/main/resources/shapes.ttl";
            File shapeFile = new File(shapePath);
            if (!shapeFile.exists()) {
                shapeFile.createNewFile();
            }
            FileWriter writer = new FileWriter(shapeFile);

            for (Map.Entry<String, NodeShape> entry : classMap.entrySet()) {
                NodeShape shape = entry.getValue();
                writer.write("<" + PREFIX + shape.getLocalName() + "> a <" + SHACL + "NodeShape> ;\n");
                writer.write("<" + SHACL + "targetClass> <" + shape.getURI() + "> ;\n");
                writer.write("<" + SHACL + "nodeKind> <" + SHACL + "IRI>");
                if (null == shape.getProperties()) {
                    writer.write(" .\n\n");
                } else {
                    writer.write(" ;\n");
                    writer.write(shape.getAllProperties() + " .\n\n");
                }
            }
            writer.close();
        } catch (Throwable t) {
            LOGGER.error(WTF_MARKER, t.getMessage(), t);
        }
    }
}
