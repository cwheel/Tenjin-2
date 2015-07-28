var schedule = require('node-schedule');
var fs = require("fs");

module.exports = function(app) {
	var alarms;

	fs.readFile('alarms.json', 'utf8', function (err,data) {
		if (err) {
			alarms =  {};
		} else {
			alarms = data;

			for (var alarm in alarms) {
		  	    if (alarms.hasOwnProperty(alarm)) {
		  	    	if ((new Date(alarms[alarm].date) > (new Date())) {
		  	    		if (alarms[alarm].type == "audio") {
		  	    			alarms[alarm].job = schedule.scheduleJob(alarms[alarm].date, audioOnlyAlarm);
		  	    		} else if (alarms[alarm].type == "audio-light") {
		  	    			alarms[alarm].job = schedule.scheduleJob(alarms[alarm].date, audioAndLightAlarm);
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
		var execDate = new Date(req.query.date);
		var alarm;

		if (req.query.type == "audio") {
			alarm = schedule.scheduleJob(execDate, audioOnlyAlarm);
		} else if (req.query.type == "audio-light") {
			alarm = schedule.scheduleJob(execDate, audioAndLightAlarm);
		}

		alarms[req.query.name] = {job: alarm, date: execDate, type: req.query.type, prettyDate: req.query.prettyDate};

		saveAlarms();
		res.send("alarm_stored");
	});

	app.get('/alarms/delete', function(req, res) {
		alarms[req.query.name].job.cancel();
		delete alarms[req.query.name];

		saveAlarms();
		res.send("alarm_deleted");
	});

	app.get('/alarms/list', function(req, res) {
		res.send(alarms);
	});
};