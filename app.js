var express = require('express');
var shex = require('shex');
var bodyParser = require('body-parser');
var fs = require('fs');

var app = express();
var router = express.Router();

var jsonParser = bodyParser.json();

app.use(express.static('schemas'));
app.use(express.static('data'));

router.use(function (req, res, next) {
    console.log("app started...");
    res.header("Access-Control-Allow-Origin", "*");
    res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
    next();
});

router.get('/', function (req, res) {
    res.json("This is app to validate RDF data using given ShEx schema");
});

router.use(function (err, req, res, next) {
    console.error(err.stack);
    next();
});

router.route('/validate').post(jsonParser, function (req, res) {
    var shexc = "http://localhost:3000/model_catalog.shex";
    var shape = "http://localhost:3000/Model";
    var node = req.body.node;
    var data = req.body.data;

    fs.writeFileSync('data/model_catalog.ttl', data);

    data = "http://localhost:3000/model_catalog.ttl";

    shex.Loader.load([shexc], [], [data], []).then(function (loaded) {
        res.send(shex.Validator.construct(loaded.schema).validate(loaded.data, node, shape));
    });

    res.status(500).send('Server side error: Please check server side logs');
});

function normalizePort(val) {
    var port = parseInt(val, 10);

    if (isNaN(port)) {
        // named pipe
        return val;
    }

    if (port >= 0) {
        // port number
        return port;
    }

    return false;
}

app.use('/', router);
var port = normalizePort(process.env.PORT || '3000');
app.listen(port);
module.exports = app;