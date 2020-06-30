//let the file require these packages/files
const express = require('express');
const path = require('path');
const server = require('./server/server.js');
const router = require('./routers/routes.js');
const http = require('http');
const https = require('https');
const fs = require('fs');

const cors = require('cors');
const bodyParser = require('body-parser');

//Init express
const app = express();
const swaggerJsDoc = require('swagger-jsdoc');
const swaggerUI = require('swagger-ui-express');

//the http port # we will be listening on
const port = 12020;
//the https port #
const httpsPort = 12021;

// for parsing application/json
app.use(express.json());

// for parsing application/x-www-form-urlencoded

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended:true}));

app.use(cors());

app.use('/', router);




// https Express tutorial
// https://dev.to/omergulen/step-by-step-node-express-ssl-certificate-run-https-server-from-scratch-in-5-steps-5b87


const swaggerOptions = {
	swaggerDefinition: {
		openapi: '3.0.0',
		info: {
			title: 'Properties API',
			description: "Properties API that provides CRUD functionality",
			contact: {
				name: "Williamn Lippard",
				name: "Octavio C"
			},
		},

		servers: [
			{url: "http://localhost:" + port},
			{url: "https://localhost:" + httpsPort}
		],

		host: ['localhost'],

		components: {
		securitySchemes: {
			ApiKeyAuth: {
				type: 'apiKey',
				in: 'header',
				name: 'api-key'
			}
		}
		}


	},

	// Path to the API docs
	apis: ["./routers/routes.js"]
};

// Initialize swagger-jsdoc -> returns validated swagger spec in json format
const swaggerDocs = swaggerJsDoc(swaggerOptions);

app.use("/api-docs", swaggerUI.serve, swaggerUI.setup(swaggerDocs));


//setting up the HTTP port to listen.

const httpServer = http.createServer(app);
httpServer.listen(port, () =>
{
	console.log("http server running on port " + port);
});


/*Starting HTTPS Config*/
const httpsOptions =
{
	cert: fs.readFileSync("./ssl/server.cert"),
	key: fs.readFileSync("./ssl/server.key")
};



const httpsServer = https.createServer(httpsOptions, app);

httpsServer.listen(httpsPort, function ()
{
	console.log("Serving the https request at https://localhost:" + httpsPort);
});





module.exports = app;
