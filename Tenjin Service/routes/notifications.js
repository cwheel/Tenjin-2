module.exports = function(app){
	var notifications = [];
	var twilio = require('twilio')('AC5d168be31cd95bf7e3a21d683e529c3f', '3dcfe6820a10636ae35ae6b1a2224764');

	app.get("/sms/incoming", function(req, res) {
		console.log(req.query);

		/*
		client.messages.create({  
			from: "+14135294014", 
			body: "Hello World",   
		}, function(err, message) { 
			console.log(err); 
		});*/
	});

	app.get("/notf/list", function(req,res){
		res.send(notifications);
	});

	app.get("/notf/add", function(req,res){
		notitications.push(req.body);

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