# Instructions to use OntToShacl utility
### Prerequisites
- Java
- Maven
- Bash
### To run this utility
1. Clone OntToShacl.
2. Run `convert.sh` with following 2 arguments (in gven order):
    - Absolute path of OWL ontology as input
    - Absolute path of the directory where you want the output

##### Usage:
```
sh convert.sh <input_file_path> <output_directory_path>
```

### Examples
Examples present in examples directory - 
- ontology.ttl - Ontology file used as input to `OntToShacl/convert.sh`
- shapes.ttl - Shacl file output produced from `OntToShacl/convert.sh`