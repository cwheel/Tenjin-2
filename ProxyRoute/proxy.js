var express = require('express');
var bodyParser = require('body-parser');
var fs = require('fs');
var localStrategy = require('passport-local').Strategy;
var Passport = require('passport');
var session = require('express-session');
var bodyParser = require('body-parser');
var cookieParser = require('cookie-parser');

var app = express();
var http = require('http').Server(app);
var io = require('socket.io')(http);

var appSocket;
var config;

//Check for a valid config file
if (fs.existsSync("config.json")) {
	config = JSON.parse(fs.readFileSync("config.json", 'utf8'));

	if ("appKeys" in config) {
		if (config.appKeys.length > 0) {
			console.log("=> Configuration validated!");
		} else {
			console.log("=> Configuration invalid, no valid appKeys found, exiting...");
			process.exit();
		}
	} else {
		console.log("=> Configuration invalid, missing appKeys, exiting...");
		process.exit();
	}
} else {
	console.log("=> Valid configuration file not found, exiting...");
	process.exit();
}

//Setup Express and routing
app.use(bodyParser.urlencoded({
  extended: true
}));

//Setup Express cookie parser
app.use(cookieParser());
app.use(session({
	secret: 'testsecret', 
	saveUninitialized: true, 
	resave: true
}));

//Configure Passport
app.use(Passport.initialize());
app.use(Passport.session());

//Begin listening for proxied GET requests
http.listen(4000, function() {
  console.log("=> Listening on :4000");
  console.log("=> ProxyRoute started! Listening for application connections...");
});

//Configure authentication for the proxy application
require('socketio-auth')(io, {
  authenticate: socketAuthReq, 
  timeout: 1000
});

//An application is attempting to join the proxy
function socketAuthReq(data, callback) {
	var authed = false;
	for (var i = config.appKeys.length - 1; i >= 0; i--) {
		if (config.appKeys[i] == data.token) {
			authed = true;
		}
	}

	return callback(null, authed);
}

//Listen for incoming proxy applications
io.on('connection', function(socket){
	console.log("=> Application connected from " + socket.request.connection._peername.address + ":" + socket.request.connection._peername.port);

	//Ensure we don't already have an application connected
	if (appSocket) {
		socket.emit('error', 'app_already_connected');
		socket.disconnect();
	} else {
		appSocket = socket;
	}
});

//Watch for proxy client disconnects
io.on('disconnect', function(socket){
	console.log("=> Application at " + socket.request.connection._peername.address + ":" + socket.request.connection._peername.port + " disconnected");
	appSocket = null;
});

//Passport auth-request
Passport.use('local', new localStrategy(function(username, password, done) {
	console.log("ds");
	for (var user in Object.keys(config.users)) {
		console.log(user);
	}

	return done(null, true);
}));

//Authenticate the proxy user
app.get('/login', Passport.authenticate('local', { successRedirect: '/login/success', failureRedirect: '/login/failure', failureFlash: false }));

//Auth success
app.get('/login/success', function(req, res){res.send({ loginStatus: 'valid' });});

//Auth failure
app.get('/login/failure', function(req, res){res.send({ loginStatus: 'failure' });});

//Handle all incoming GET requests and proxy them to the NAT'd application over a socket
app.get('*', function(req, res) {
	if (appSocket) {
		//Send the GET request to the connected application
		appSocket.emit("GET", {originalUrl: req.originalUrl, query: req.query});

		//Handler for the applications response
		var proxyResponse = function(resp) {
			res.send(resp);
			appSocket.removeListener('GET_RESP', proxyResponse);
		};

		//Add the handler
		appSocket.on('GET_RESP', proxyResponse);
	} else {
		res.send("REMOTE_APP_NOT_CONNECTED");
	}
});

//Passport serialization
Passport.serializeUser(function(user, done) {
  done(null, user);
});

//Passport deserialization
Passport.deserializeUser(function(user, done) {
  done(null, user)
});

//Exports
exports = module.exports = app; 