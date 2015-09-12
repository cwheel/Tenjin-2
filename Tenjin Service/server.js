var express = require('express');
var bodyParser = require('body-parser');
var SerialPort = require("serialport");
var request = require("request");
var fs = require("fs");
var moment = require('moment');
var app = express();

var lcDevices;
var config;

//Setup Express and routing
app.use(bodyParser.urlencoded({
  extended: true
}));

//Load the configuration
if (fs.existsSync("config.json")) {
	config = JSON.parse(fs.readFileSync("config.json", 'utf8'));
} else {
	console.log("=> Configuration file not found, exiting...");
	process.exit();
}

app.use(express.static(__dirname + '/app'));
require('./routes/cache')(app);
require('./routes/lights')(app);
require('./routes/uit')(app);
require('./routes/notifications')(app);
require('./routes/alarms')(app);
require('./proxyConnect')(app, config.proxyRouteHost, config.proxyRouteKey, ['/lights',  '/alarms'], ['/sms']);

//Begin listening for the web UI
app.listen(3000);
console.log("=> Listening on :3000"); 

//Weather Updater
var updateWeather = function() {
	request({
	    url: "https://api.forecast.io/forecast/" + config.forecastIOKey + "/" + config.forecastLocation,
	    json: true
	}, function (error, response, body, weather) {
	    if (!error && response.statusCode === 200) {
			fs.writeFile( 'cache/weather.json', JSON.stringify(body), function (err) {
				var now = new Date();
		  		console.log("=> Weather cached at " + moment().format('h:mm:ss a'));
			});
	    }
	})
};
var updateNPR = function(){
	request({
		url: "http://api.npr.org/query?output=JSON&numResults=20&apiKey=" + ,
		json: true
	}, function(error,response,body,weather){
		if (!error && response.statusCode === 200) {
			fs.writeFile( 'cache/npr.json', JSON.stringify(body), function (err) {
				var now = new Date();
		  		console.log("=> NPR News cached at " + moment().format('h:mm:ss a'));
			});
	    }	
	})
};

setInterval(updateWeather, (60 * 5 * 1000));
setInterval(updateNPR, (60 * 60 * 1000));
updateWeather();
updateNPR();

//Exports
exports = module.exports = app; 

//Routines for discovering and connecting to the proper serial device
if (process.platform == 'darwin') {
	//If on OS X
	//Read all the devices and filter them down to all the serial modems
	lcDevices = fs.readdirSync('/dev');
	lcDevices = lcDevices.filter(function(device) {
		return device.indexOf('cu.usbmodem') > -1;
	});

	//If we didn't find any
	if (lcDevices.length == 0) {
		console.log("=> No lights controller detected, starting without...");
	} else {
		//Begin probing each one to find out which is the controller
		console.log("=> Attempting to connect to device '" + lcDevices[0] + "'...");     

		//Open a connection to the first in the array
		app.lightsController = new SerialPort.SerialPort("/dev/" + lcDevices[0], {
			baudrate: 9600,
		    parser: SerialPort.parsers.readline(";")
		});

		//Once the connection is open, send the opcode for version
		app.lightsController.on("open", function () {
			app.lightsController.write("25;");

			app.lightsController.on('data', function(data) {
				//Check what the device sent back. If it's a version of ours, we know its the LC
			  	if (data.indexOf("TenjinLightingSystem") > -1) {
			  		app.lcConnected = true;
			  		console.log("    • Lights controller reported firmware version of: " + data + "!");
			  	}
			});
		});

		//The routine for probing the next device in line
		var connectToNext = function() {
			//Make sure the LC didn't open a connection already
			if (!app.lcConnected) {
				//Make sure we have enough devices to probe
				if (lcDevices.length > 1) {
					//Throw out the old device, it didn't work last time
					lcDevices.shift();

					//Kill off its serial connection and open a new one
					app.lightsController.close();
					app.lightsController = new SerialPort.SerialPort("/dev/" + lcDevices[0], {
						baudrate: 9600,
					    parser: SerialPort.parsers.readline(";")
					});

					app.lightsController.on("open", function () {
						//Ask the LC for its version
						app.lightsController.write("25;");

						//Make sure that the device responded with a version fitting the LC
						app.lightsController.on('data', function(data) {
						  if (data.indexOf("TenjinLightingSystem") > -1) {
						  		app.lcConnected = true;
						  		console.log("    • Lights controller reported firmware version of: " + data + "!");
						  }
						});
					});

					setTimeout(function() {
						connectToNext();
					}, 4000);
				} else {
					//None of the devices we found and tried worked
					console.log("=> No device correctly responded, ensure the lighting controller is running a proper firmware");
				}
			}
		};

		//Setup a timeout for probing the other devices
		setTimeout(function() {
			connectToNext();
		}, 4000);
	}
} else if (process.platform == 'linux') {
	//If on Mint
	//Read all the serial devices
	lcDevices = fs.readdirSync('/dev/');
	lcDevices = lcDevices.filter(function(device) {
		return device.indexOf('ttyACM') > -1;
	});

	//If we didn't find any
	if (lcDevices.length == 0) {
		console.log("=> No lights controller detected, starting without...");
	} else {
		//Begin probing each one to find out which is the controller
		console.log("=> Attempting to connect to device '" + lcDevices[0] + "'...");     

		//Open a connection to the first in the array
		app.lightsController = new SerialPort.SerialPort("/dev/" + lcDevices[0], {
			baudrate: 9600,
		    parser: SerialPort.parsers.readline(";")
		});

		//Once the connection is open, send the opcode for version
		app.lightsController.on("open", function () {
			app.lightsController.write("25;");

			app.lightsController.on('data', function(data) {
				//Check what the device sent back. If it's a version of ours, we know its the LC
			  	if (data.indexOf("TenjinLightingSystem") > -1) {
			  		app.lcConnected = true;
			  		console.log("    • Lights controller reported firmware version of: " + data + "!");
			  	}
			});
		});

		//The routine for probing the next device in line
		var connectToNext = function() {
			//Make sure the LC didn't open a connection already
			if (!app.lcConnected) {
				//Make sure we have enough devices to probe
				if (lcDevices.length > 1) {
					//Throw out the old device, it didn't work last time
					lcDevices.shift();

					//Kill off its serial connection and open a new one
					app.lightsController.close();
					app.lightsController = new SerialPort.SerialPort("/dev/" + lcDevices[0], {
						baudrate: 9600,
					    parser: SerialPort.parsers.readline(";")
					});

					app.lightsController.on("open", function () {
						//Ask the LC for its version
						app.lightsController.write("25;");

						//Make sure that the device responded with a version fitting the LC
						app.lightsController.on('data', function(data) {
						  if (data.indexOf("TenjinLightingSystem") > -1) {
						  		app.lcConnected = true;
						  		console.log("    • Lights controller reported firmware version of: " + data + "!");
						  }
						});
					})

					setTimeout(function() {
						connectToNext();
					}, 4000);å
				} else {
					//None of the devices we found and tried worked
					console.log("=> No device correctly responded, ensure the lighting controller is running a proper firmware");
				}
			}
		};

		//Setup a timeout for probing the other devices
		setTimeout(function() {
			connectToNext();
		}, 4000);
	}
}