var schedule = require('node-schedule');
var moment = require('moment');
var fs = require("fs");
var player = require('play-sound')(opts = {});

module.exports = function(app) {
	var alarms;

	fs.readFile('alarms.json', 'utf8', function (err,data) {
		player.play('alarms/Helium.ogg')
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
		  	    			cconsole.log("    • Scheduling alarm job for " + alarmDate);
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

	function audioOnlyAlarm() {
		console.log("alarm triggered");
	}

	function audioAndLightAlarm() {
		console.log("alarm triggered");
	}

	//Uses YYYY-MM-DDTHH:MM:SS format
	app.get('/alarms/new', function(req, res) {
		var execDate = moment(req.query.date);
		var alarm;

		if (req.query.type == "audio") {
			alarm = schedule.scheduleJob(execDate.toDate(), audioOnlyAlarm);
		} else if (req.query.type == "audio-light") {
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
		alarms[req.query.name].type = req.query.type;

		saveAlarms();
		res.send("alarm_stored");
	});

	app.get('/alarms/validate', function(req, res) {
		var old = moment(alarms[req.query.name].date);
		var timeToday = moment();

		timeToday.hour(old.hour());
		timeToday.minute(old.minute());

		//The date today already passed,the alarm must be for tommorow
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