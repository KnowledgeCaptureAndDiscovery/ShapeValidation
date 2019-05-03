package edu.isi.mint.model;

public class PropertyShape {
    private String URI;
    private String localName;
    private String domain;
    private String range;
    private int minCardinality;
    private int maxCardinality;

    public PropertyShape(String URI) {
        this.URI = URI;
        this.minCardinality = 0;
        this.maxCardinality = -1;
    }

    public String getURI() {
        return URI;
    }

    public void setURI(String URI) {
        this.URI = URI;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getLocalName() {
        return localName;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public int getMinCardinality() {
        return minCardinality;
    }

    public void setMinCardinality(int minCardinality) {
        this.minCardinality = minCardinality;
    }

    public int getMaxCardinality() {
        return maxCardinality;
    }

    public void setMaxCardinality(int maxCardinality) {
        this.maxCardinality = maxCardinality;
    }

    @Override
    public String toString() {
        return getURI();
    }

    public String getShaclString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(" <http://www.w3.org/ns/shacl#path> <" + getURI() + "> ;");
        if (getMinCardinality() != 0) {
            sb.append(" <http://www.w3.org/ns/shacl#minCount> " + getMinCardinality() + " ;");
        }
        if (getMaxCardinality() != -1) {
            sb.append(" <http://www.w3.org/ns/shacl#maxCount> " + getMaxCardinality() + " ;");
        }
        if (getRange().contains("XMLSchema")) {
            sb.append(" <http://www.w3.org/ns/shacl#datatype> <" + getRange() + "> ; ]");
        } else {
            sb.append(" <http://www.w3.org/ns/shacl#class> <" + getRange() + "> ; <http://www.w3.org/ns/shacl#nodeKind> <http://www.w3.org/ns/shacl#IRI> ; ]");
        }
        return sb.toString();
    }
}
