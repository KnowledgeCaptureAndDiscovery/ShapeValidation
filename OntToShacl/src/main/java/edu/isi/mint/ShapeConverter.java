package edu.isi.mint;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

//TODO This should be a REST API
public class ShapeConverter {
    public static void main(String[] args) throws IOException {
        // TODO This file should be uploaded by comsumer of a REST API
        BufferedReader reader = new BufferedReader(new FileReader(new File("D:\\USC\\ISI\\mint\\ontology.ttl")));
        int c;
        StringBuilder builder = new StringBuilder();
        while ((c = reader.read()) != -1) {
            char ch = (char) c;
            if (ch == '\n')
                continue;
            if (ch == '#' && builder.length() == 0) {
                while ((char) reader.read() != '\n')
                    continue;
            } else if (ch == '.') {
                int newCh = reader.read();
                if (newCh != -1 && (char) newCh == '\n') {
                    String entity = builder.toString().trim().replaceAll(" +", " ");
                    // TODO Use this entity as fundamental unit to convert from Ont to Shacl
                    System.out.println(entity);
                    builder = new StringBuilder();
                } else {
                    builder.append(ch);
                    if (newCh != -1) {
                        builder.append((char) newCh);
                    }
                }
            } else {
                builder.append(ch);
            }
        }
        reader.close();
        // TODO return Shacl file
    }
}
