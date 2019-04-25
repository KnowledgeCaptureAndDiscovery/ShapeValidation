var mintInstance = "<https://w3id.org/mint/instance/";

var modelObject = "<https://w3id.org/mint/modelCatalog#Model>";
var modelVersionPredicate = "<http://ontosoft.org/software#hasSoftwareVersion>";
var descriptionPredicate = "<http://purl.org/dc/terms/description>";
var modelCategoryPredicate = "<https://w3id.org/mint/modelCatalog#hasModelCategory>";

var modelVersionObject = "<https://w3id.org/mint/modelCatalog#ModelVersion>";
var softwareVersionObject = "<http://ontosoft.org/software#SoftwareVersion>";

var modelConfigurationPredicate = "<https://w3id.org/mint/modelCatalog#hasConfiguration>";
var modelConfigComponentLocationPredicate = "<https://w3id.org/mint/modelCatalog#hasComponentLocation>";
var modelConfigurationObject = "<https://w3id.org/mint/modelCatalog#ModelConfiguration>";

var inputPredicate = "<https://w3id.org/mint/modelCatalog#hasInput>";
var outputPredicate = "<https://w3id.org/mint/modelCatalog#hasOutput>";

var datasetSpecificationObject = "<https://w3id.org/mint/modelCatalog#DatasetSpecification>";
var dimensionalityPredicate = "<https://w3id.org/mint/modelCatalog#hasDimensionality>";
var formataPredicate = "<https://w3id.org/mint/modelCatalog#hasFormat>";
var variablePredicate = "<https://w3id.org/mint/modelCatalog#hasPresentation>";

var variableObject = "<https://w3id.org/mint/modelCatalog#VariablePresentation>";
var longNamePredicate = "<https://w3id.org/mint/modelCatalog#hasLongName>";
var shortNamePredicate = "<https://w3id.org/mint/modelCatalog#hasShortName>";
var unitPredicate = "<https://w3id.org/mint/modelCatalog#usesUnit>";
var standardVariablePredicate = "<https://w3id.org/mint/modelCatalog#hasStandardVariable>";

var output = [];
var modelName = "";
var softwareVersion = "";
var modelConfig = "";
var ioType = 0;
var io = "";

var node = "";
var data = "";

$("#model_form").bind('submit', function (event) {
    var elements = this.elements;
    // console.log(elements);
    output.push("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>");
    $("#model_form :input").each(function () {
        var e = $(this)[0];
        // console.log(e.id + " : " + e.value);

        switch (e.id) {
            case "Model":
                modelName = e.value.trim().toLowerCase();
                node = mintInstance + e.value.trim().toUpperCase() + '>';
                output.push(node + ' a ' + modelObject + ' ;');
                output.push('rdfs:label "' + e.value + '" ;');
                break;
            case "SoftwareVersion":
                softwareVersion = e.value.trim();
                output.push(modelVersionPredicate + ' ' + mintInstance + modelName + '_' + softwareVersion + '> ;');
                break;
            case "ModelDescription":
                output.push(descriptionPredicate + ' "' + e.value.trim() + '" ;');
                break;
            case "ModelCategory":
                output.push(modelCategoryPredicate + ' "' + e.value.trim() + '" .');
                break;
            case "ModelConfig":
                modelConfig = e.value.trim().replace(/ /g, '_');
                output.push(mintInstance + modelName + '_' + softwareVersion + '> a ' + modelVersionObject + ' , ' + softwareVersionObject + ' ;');
                output.push('rdfs:label "' + modelName + ' v' + softwareVersion + '" ;');
                output.push(modelConfigurationPredicate + ' ' + mintInstance + modelConfig + '> .');
                output.push(mintInstance + modelConfig + '> a ' + modelConfigurationObject + ' ;');
                output.push('rdfs:label "' + modelConfig + '" ;');
                break;
            case "ModelConfigDescription":
                output.push(descriptionPredicate + ' "' + e.value.trim() + '" ;');
                break;
            case "ModelConfigComponentLocation":
                output.push(modelConfigComponentLocationPredicate + ' "' + e.value.trim() + '" .');
                break;
            case "ioType":
                ioType = e.value.trim();
                break;
            case "io":
                io = e.value.trim();
                output.push(mintInstance + io.replace(/ /g, '_') + '> a ' + datasetSpecificationObject + ' ;');
                output.push('rdfs:label "' + io + '" ;');
                break;
            case "ioDescription":
                output.push(descriptionPredicate + ' "' + e.value.trim() + '" ;');
                break;
            case "ioFormat":
                output.push(formataPredicate + ' "' + e.value.trim() + '" ;');
                break;
            case "ioDimensionality":
                output.push(dimensionalityPredicate + ' "' + e.value.trim() + '" ;');
                break;
            case "ioVariables":
                vars = e.value.trim().split(',');
                for (var i = 0; i < vars.length; i++) vars[i] = mintInstance + vars[i] + '>';
                output.push(variablePredicate + vars.join(' , ') + ' .');
                if (ioType == 0)
                    output.push(mintInstance + modelConfig + '> ' + inputPredicate + ' ' + mintInstance + io.replace(/ /g, '_') + '> .');
                else
                    output.push(mintInstance + modelConfig + '> ' + outputPredicate + ' ' + mintInstance + io.replace(/ /g, '_') + '> .');
                break;
            case "label":
                output.push(mintInstance + e.value.trim() + '> a ' + variableObject + ' ;');
                output.push('rdfs:label "' + e.value.trim() + '" ;');
                break;
            case "description":
                output.push(descriptionPredicate + ' "' + e.value.trim() + '" ;');
                break;
            case "shortName":
                output.push(shortNamePredicate + ' "' + e.value.trim() + '" ;');
                break;
            case "longName":
                output.push(longNamePredicate + ' "' + e.value.trim() + '" ;');
                break;
            case "unit":
                output.push(unitPredicate + ' "' + e.value.trim() + '" ;');
                break;
            case "standardVariable":
                output.push(standardVariablePredicate + ' "' + e.value.trim() + '" .');
                break;
        }
    });

    data = output.join('\n');
    // console.log(data);

    $.ajax({
        type: "POST",
        url: "http://localhost:3000/validate",
        data: JSON.stringify({
            node: node.substring(1, node.length - 1),
            data: data
        }),
        contentType: "application/json",
        dataType: "json",
        success: function (data) {
            alert(data);
        },
        failure: function (errMsg) {
            alert(errMsg);
        }
    });

    event.preventDefault();
});