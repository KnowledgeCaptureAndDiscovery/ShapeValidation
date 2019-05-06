#!/usr/bin/env bash

input=$1
output=$2

if [[ -z "$1" ]] || [[ -z "$2" ]]; then
    echo "Not Enough Arguments..."
    echo "Please provide at least 2 arguments -"
    echo "1. Absolute path of OWL ontology as input"
    echo "2. Absolute path of the directory where you want the output"
    echo "Usage : $0 <input_file_path> <output_directory_path>"
    echo "Exiting..."
    exit 1
fi

mvn clean install exec:java -Dexec.mainClass="edu.isi.mint.ShapeConverterApplication" -Dexec.args="$input $output"
