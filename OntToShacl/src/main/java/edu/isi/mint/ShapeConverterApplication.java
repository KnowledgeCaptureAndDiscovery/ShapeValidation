package edu.isi.mint;

import edu.isi.mint.model.NodeShape;
import edu.isi.mint.model.PropertyShape;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShapeConverterApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShapeConverterApplication.class);
    private static final String PREFIX = "http://example.com/ns#";
    private static final String SHACL = "http://www.w3.org/ns/shacl#";

    public static void main(String[] args) {
        try {
            if (args.length < 2) {
                LOGGER.error("Not Enough Arguments...\n" +
                        "Please provide at least 2 arguments -\n" +
                        "1. Absolute path of OWL ontology as input\n" +
                        "2. Absolute path of the directory where you want the output\n" +
                        "Exiting...");
                System.exit(1);
            }

            Model model = ModelFactory.createDefaultModel();
            model.read("file:" + args[0]);

            // Get all Classes
            Map<String, NodeShape> classMap = new HashMap<>();
            ResIterator classIter = model.listResourcesWithProperty(RDF.type, OWL.Class);
            while (classIter.hasNext()) {
                Resource r = classIter.nextResource();
                if (null != r.getURI()) {
                    NodeShape nodeShape = new NodeShape(r.getURI());
                    nodeShape.setLocalName(r.getLocalName() + "Shape");
                    classMap.put(r.getURI(), nodeShape);
                }
            }

            // Get All Properties
            Map<String, PropertyShape> propertyMap = new HashMap<>();

            // Functional Properties
            ResIterator funPropIter = model.listResourcesWithProperty(RDF.type, OWL.FunctionalProperty);
            extractProperties(propertyMap, funPropIter, true);

            // Object Properties
            ResIterator objPropIter = model.listResourcesWithProperty(RDF.type, OWL.ObjectProperty);
            extractProperties(propertyMap, objPropIter, false);

            // Datatype Properties
            ResIterator dataPropIter = model.listResourcesWithProperty(RDF.type, OWL.DatatypeProperty);
            extractProperties(propertyMap, dataPropIter, false);

            // Create Class - Property Association
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
            String shapePath;
            if (args[1].charAt(args[1].length() - 1) != '/') {
                shapePath = args[1] + "/shapes.ttl";
            } else {
                shapePath = args[1] + "shapes.ttl";
            }

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
            LOGGER.info("Output written as: " + shapePath);
        } catch (Throwable t) {
            LOGGER.error(t.getMessage(), t);
        }
    }

    private static void extractProperties(Map<String, PropertyShape> propertyMap, ResIterator iter, boolean isFunctional) {
        while (iter.hasNext()) {
            Resource r = iter.nextResource();
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
                    } else if (s.getPredicate().equals(OWL.minCardinality)) {
                        propertyShape.setMinCardinality(s.getObject().asLiteral().getInt());
                    } else if (s.getPredicate().equals(OWL.maxCardinality)) {
                        propertyShape.setMaxCardinality(s.getObject().asLiteral().getInt());
                    }
                }

                if (isFunctional) {
                    propertyShape.setMaxCardinality(1);
                }

                propertyMap.put(r.getURI(), propertyShape);
            }
        }
    }
}
