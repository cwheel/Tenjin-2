module.exports = function(app){
	var notifications = [];

	app.get("/sms/incoming", function(req, res) {
		var mssgStart = "UMass Amherst Alerts:";

		if (req.query.From.indexOf("67283") > -1 && req.query.Body.indexOf(mssgStart) > -1) {

			request({
  		    	url:  'http://localhost:' + process.env.PORT + "/notif/add",
  		    	qs: {text: req.query.Body.replace(mssgStart, "")}
	  		}, function (error, response, body) {});

			res.send("SMS_RECIEVED");
		} else {
			res.send("SMS_REJECTED");
		}
	});

	app.get("/notf/list", function(req,res){
		res.send(notifications);
	});

	app.get("/notf/add", function(req, res){
		notitications.push(req.body.text);

		if (app.lcConnected && app.lightsController.isOpen()) {
			app.lightsController.write("26;");

			var step = 8;

			var revolutions = 0;
			var intensity = 1;
			var increasing =  true;

			while (revolutions < 7){
				app.lightsController.write("11," + intensity + ";");

				if (increasing){
					intensity += step;
				} else {
					intensity -= step;
				}

				if (intensity >= 255){
					intensity = 255;
					increasing = false;

				} else if (intensity <= 0){
					intensity = 0;
					increasing = true;
					revolutions++;
				}

				app.lightsController.write("11," + intensity + ";");

			}

			app.lightsController.write("27;");
		} else {
			res.send(unreachable);
		}
	});

};