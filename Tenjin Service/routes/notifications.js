// Umass Alerts
// Severe Weather Warnings
//  

module.exports = function(app){
	var notifications = [];
	app.get("/notf/list",function(req,res){
		res.send(notifications);
	});
	app.get("/notf/add", function(req,res){
		notitications.push(req.body);
		if (app.lcConnected && app.lightsController.isOpen()) {
			var step = 1;

			var revolutions = 0;
			var intensity = 1;
			var increasing =  true;
			while (revolutions < 4){
				app.lightsController.write("11," + intensity + ";");
				if (increasing){
					intensity += step;
				}else{
					intensity -= step;
				}
				if (intensity >= 255){
					intensity = 255;
					increasing = false;
				}else if (intensity <= 0){
					intensity = 0;
					increasing = true;
					revolutions++;
				}
				app.lightsController.write("11," + intensity + ";");

			}
		} else {
			res.send(unreachable);
		}
	});

};