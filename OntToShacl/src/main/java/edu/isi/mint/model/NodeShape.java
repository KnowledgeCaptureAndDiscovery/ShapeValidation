package edu.isi.mint.model;

import java.util.List;

public class NodeShape {
    private String URI;
    private String localName;
    private List<PropertyShape> properties;

    public NodeShape(String URI) {
        this.URI = URI;
    }

    public String getURI() {
        return URI;
    }

    public void setURI(String URI) {
        this.URI = URI;
    }

    public List<PropertyShape> getProperties() {
        return properties;
    }

    public void setProperties(List<PropertyShape> properties) {
        this.properties = properties;
    }

    public String getLocalName() {
        return localName;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }

    @Override
    public String toString() {
        return getURI();
    }

    public String getAllProperties() {
        StringBuilder sb = new StringBuilder();
        for (PropertyShape property : getProperties()) {
            sb.append("<http://www.w3.org/ns/shacl#property> " + property.getShaclString() + " ; ");
        }
        return sb.substring(0, sb.length() - 3);
    }
}
