var schedule = require('node-schedule');
var moment = require('moment');
var fs = require("fs");
var lame = require('lame');
var Speaker = require('speaker');

module.exports = function(app) {
	var alarms;
	var playingAlarm = null;
	var playingAlarmCount = -1;
	var sunriseStep = 0;
	var sunriseDelay = 0; 

	fs.readFile('alarms.json', 'utf8', function (err,data) {
		if (err) {
			alarms =  {};
		} else {
			alarms = JSON.parse(data);
			console.log("=> Loaded alarms database!")

			for (var alarm in alarms) {
		  	    if (alarms.hasOwnProperty(alarm)) {
		  	    	
		  	    	var alarmDate = moment(alarms[alarm].date).toDate();
		  	    	if (alarmDate > (new Date())) {
		  	    		if (alarms[alarm].type == "audio") {
		  	    			console.log("    • Scheduling alarm job for " + alarmDate);
		  	    			alarms[alarm].job = schedule.scheduleJob(alarmDate, audioOnlyAlarm);
		  	    		} else if (alarms[alarm].type == "audio-light") {
		  	    			console.log("    • Scheduling alarm job for " + alarmDate);
		  	    			alarms[alarm].job = schedule.scheduleJob(alarmDate, audioAndLightAlarm);
		  	    		}
		  	    	}
		  	    }
		  	}
		}
	});

	function saveAlarms() {
		fs.writeFile('alarms.json', JSON.stringify(alarms), function (err) {
		  if (err) return console.log(err);
		});
	}

	function runAlarm() {
		try {
			playingAlarm.end();
			playingAlarm = null;
		} catch (e) {}

		if (playingAlarmCount == -1) {
			return;
		} else {
			playingAlarm = fs.createReadStream('alarms/Helium.mp3').pipe(new lame.Decoder).pipe(new Speaker);
			playingAlarmCount++;
		}

		if (playingAlarmCount < 5 && playingAlarmCount > 0) {
			setTimeout(runAlarm, 9000);
		}
	}

	function audioOnlyAlarm() {
		playingAlarmCount = 0;
		runAlarm();
	}

	function audioAndLightAlarm() {
		sunriseStart(30);
	}

	function sunriseValue() {
		if (app.lcConnected && app.lightsController.isOpen()) {
			// Initialize Color Values
			var red = 0;
			var green = 0;
			var blue = 0;
			var white = 0;

			// Loop 1 Red 
			if (sunriseStep < 255){
				for(var i = 0; i < sunriseStep;i++){
					red++
					if (i % 50 == 0){
						green++;
					}
				}
			}
			// Loop 2 Yellow
			else if( sunriseStep < 350){
				red = 255;
				green = 6;
				for (var i = 0;  i < (sunriseStep - 255); i++){
					green++;
				}
			}
			// Loop 3 
			else if (sunriseStep < 506){
				red = 255;
				green = 100;
				for (var i = 0;i < (sunriseStep - 350); i++){
					green++;
					if (i % 4 == 0){
						blue++;
					}
				}
			}
			// Loop 4
			else if (sunriseStep < 722){
				red = 255;
				green = 255;
				blue = 39;
				for (var i = 0; i < (sunriseStep - 506); i++){
					blue++;
					if (i %2 == 0){
						white++;
					}
				}
			}
			// Loop 5
			else if (sunriseStep < 870){
				red = 255;
				green = 255;
				blue = 255;
				white = 108;
				for (var i =0; i < (sunriseStep - 722); i++){
					white++;
				}
			}
			if (sunriseStep == 870){
				red = 255;
				green = 255;
				blue = 255;
				white= 255;
				playingAlarmCount = 0;
				runAlarm();
			} else {
				setTimeout(sunriseValue, sunriseDelay);
			}
			sunriseStep += 1;
			app.lightsController.write("23," + red + "," + green + "," +  blue + "," + white + ";");
		} 
	}
	

	function sunriseStart(min){
		sunriseDelay = min / 870 * 60  * 1000;
		SunriseStep = 0;
		sunriseValue()
	}

	app.get('/alarms/new', function(req, res) {
		var execDate = moment(req.query.date);
		var alarm;

		if (req.query.type == "audio") {
			alarm = schedule.scheduleJob(execDate.toDate(), audioOnlyAlarm);
		} else if (req.query.type == "audio-light") {
			execDate.subtract(30, 'minutes');
			alarm = schedule.scheduleJob(execDate.toDate(), audioAndLightAlarm);
		}

		alarms[req.query.name] = {job: alarm, date: execDate.format(), type: req.query.type, prettyDate: req.query.prettyDate};

		saveAlarms();
		res.send("alarm_stored");
	});

	app.get('/alarms/remove', function(req, res) {
		try {
			alarms[req.query.name].job.cancel();
		} catch (e) {}
		
		delete alarms[req.query.name];

		saveAlarms();
		res.send("alarm_deleted");
	});

	app.get('/alarms/invalidate', function(req, res) {
		try {
			alarms[req.query.name].job.cancel();
		} catch (e) {}

		alarms[req.query.name].date = moment(alarms[req.query.name].date).subtract(1, 'days').format();

		saveAlarms();
		res.send("alarm_invalidated");
	});

	app.get('/alarms/settype', function(req, res) {
		if (alarms[req.query.name] == "audio" && req.query.type == "audio-light") {
			alarms[req.query.name].date = moment(alarms[req.query.name].date).subtract(30, 'minutes').format();
		} else if (alarms[req.query.name] == "audio-light" && req.query.type == "audio") {
			alarms[req.query.name].date = moment(alarms[req.query.name].date).add(30, 'minutes').format();
		}

		alarms[req.query.name].type = req.query.type;

		var alarmDate = moment(alarms[alarm].date).toDate();
		if (alarmDate > (new Date())) {
			try {
				alarms[req.query.name].job.cancel();
			} catch (e) {}

			if (alarms[alarm].type == "audio") {
				alarms[alarm].job = schedule.scheduleJob(alarmDate, audioOnlyAlarm);
			} else if (alarms[alarm].type == "audio-light") {
				alarms[alarm].job = schedule.scheduleJob(alarmDate, audioAndLightAlarm);
			}
		}

		saveAlarms();
		res.send("alarm_stored");
	});

	app.get('/alarms/off', function(req, res) {
		playingAlarm.end();
		playingAlarm = null;
		playingAlarmCount = -1;

		res.send("alarm_off");
	});

	app.get('/alarms/validate', function(req, res) {
		var old = moment(alarms[req.query.name].date);
		var timeToday = moment();

		timeToday.hour(old.hour());
		timeToday.minute(old.minute());

		if (timeToday < (new Date())) {
			var timeTom = moment();
			timeTom.add(1, 'days');

			timeTom.hour(old.hour());
			timeTom.minute(old.minute());

			alarms[req.query.name].date = timeTom.format();
		} else {
			alarms[req.query.name].date = timeToday.format();
		}

		if (alarms[req.query.name].type == "audio") {
			alarm = schedule.scheduleJob(moment(alarms[req.query.name].date).toDate(), audioOnlyAlarm);
		} else if (alarms[req.query.name].type == "audio-light") {
			alarm = schedule.scheduleJob(moment(alarms[req.query.name].date).toDate(), audioAndLightAlarm);
		}

		saveAlarms();
		res.send("alarm_validated");
	});

	app.get('/alarms/list', function(req, res) {
		res.send(alarms);
	});
};