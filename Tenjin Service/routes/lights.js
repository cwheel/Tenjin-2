module.exports = function(app) {
	var unreachable = "LC_unreachable";
	var success = "LC_success";

	var isAnimating = false;
	var animationContext = {};

	app.get('/lights/r1', function(req, res) {
		if (app.lcConnected && app.lightsController.isOpen()) {
			app.lightsController.write("1," + req.query.val + ";");
			res.send(success);
		} else {
			res.send(unreachable);
		}
	});

	app.get('/lights/b1', function(req, res) {
		if (app.lcConnected && app.lightsController.isOpen()) {
			app.lightsController.write("2," + req.query.val + ";");
			res.send(success);
		} else {
			res.send(unreachable);
		}
	});

	app.get('/lights/g1', function(req, res) {
		if (app.lcConnected && app.lightsController.isOpen()) {
			app.lightsController.write("3," + req.query.val + ";");
			res.send(success);
		} else {
			res.send(unreachable);
		}
	});

	app.get('/lights/w1', function(req, res) {
		if (app.lcConnected && app.lightsController.isOpen()) {
			app.lightsController.write("4," + req.query.val + ";");
			res.send(success);
		} else {
			res.send(unreachable);
		}
	});

	app.get('/lights/r2', function(req, res) {
		if (app.lcConnected && app.lightsController.isOpen()) {
			app.lightsController.write("5," + req.query.val + ";");
			res.send(success);
		} else {
			res.send(unreachable);
		}
	});

	app.get('/lights/b2', function(req, res) {
		if (app.lcConnected && app.lightsController.isOpen()) {
			app.lightsController.write("6," + req.query.val + ";");
			res.send(success);
		} else {
			res.send(unreachable);
		}
	});

	app.get('/lights/g2', function(req, res) {
		if (app.lcConnected && app.lightsController.isOpen()) {
			app.lightsController.write("7," + req.query.val + ";");
			res.send(success);
		} else {
			res.send(unreachable);
		}
	});

	app.get('/lights/w2', function(req, res) {
		if (app.lcConnected && app.lightsController.isOpen()) {
			app.lightsController.write("8," + req.query.val + ";");
			res.send(success);
		} else {
			res.send(unreachable);
		}
	});

	app.get('/lights/w3', function(req, res) {
		if (app.lcConnected && app.lightsController.isOpen()) {
			app.lightsController.write("9," + req.query.val + ";");
			res.send(success);
		} else {
			res.send(unreachable);
		}
	});

	app.get('/lights/r1and2', function(req, res) {
		if (app.lcConnected && app.lightsController.isOpen()) {
			app.lightsController.write("11," + req.query.val + ";");
			res.send(success);
		} else {
			res.send(unreachable);
		}
	});

	app.get('/lights/b1and2', function(req, res) {
		if (app.lcConnected && app.lightsController.isOpen()) {
			app.lightsController.write("12," + req.query.val + ";");
			res.send(success);
		} else {
			res.send(unreachable);
		}
	});

	app.get('/lights/g1and2', function(req, res) {
		if (app.lcConnected && app.lightsController.isOpen()) {
			app.lightsController.write("13," + req.query.val + ";");
			res.send(success);
		} else {
			res.send(unreachable);
		}
	});

	app.get('/lights/w1and2', function(req, res) {
		if (app.lcConnected && app.lightsController.isOpen()) {
			app.lightsController.write("14," + req.query.val + ";");
			res.send(success);
		} else {
			res.send(unreachable);
		}
	});

	app.get('/lights/sw1', function(req, res) {
		if (app.lcConnected && app.lightsController.isOpen()) {
			app.lightsController.write("15," + req.query.val + ";");
			res.send(success);
		} else {
			res.send(unreachable);
		}
	});

	app.get('/lights/sw2', function(req, res) {
		if (app.lcConnected && app.lightsController.isOpen()) {
			app.lightsController.write("16," + req.query.val + ";");
			res.send(success);
		} else {
			res.send(unreachable);
		}
	});

	app.get('/lights/sw1and2', function(req, res) {
		if (app.lcConnected && app.lightsController.isOpen()) {
			app.lightsController.write("18," + req.query.val + ";");
			res.send(success);
		} else {
			res.send(unreachable);
		}
	});

	app.get('/lights/sw1and2and3', function(req, res) {
		if (app.lcConnected && app.lightsController.isOpen()) {
			app.lightsController.write("19," + req.query.val + ";");
			res.send(success);
		} else {
			res.send(unreachable);
		}
	});

	app.get('/lights/rgbw1', function(req, res) {
		if (app.lcConnected && app.lightsController.isOpen()) {
			app.lightsController.write("20," + req.query.r + "," + req.query.g + "," + req.query.b + "," + req.query.w + ";");
			res.send(success);
		} else {
			res.send(unreachable);
		}
	});

	app.get('/lights/rgbw2', function(req, res) {
		if (app.lcConnected && app.lightsController.isOpen()) {
			app.lightsController.write("21," + req.query.r + "," + req.query.g + "," + req.query.b + "," + req.query.w + ";");
			res.send(success);
		} else {
			res.send(unreachable);
		}
	});

	app.get('/lights/rgbw1and2', function(req, res) {
		if (app.lcConnected && app.lightsController.isOpen()) {
			app.lightsController.write("23," + req.query.r + "," + req.query.g + "," + req.query.b + "," + req.query.w + ";");
			res.send(success);
		} else {
			res.send(unreachable);
		}
	});

	app.get('/lights/cxsave', function(req, res) {
		if (app.lcConnected && app.lightsController.isOpen()) {
			app.lightsController.write("26;");
			res.send(success);
		} else {
			res.send(unreachable);
		}
	});

	app.get('/lights/cxrestore', function(req, res) {
		if (app.lcConnected && app.lightsController.isOpen()) {
			app.lightsController.write("27;");
			res.send(success);
		} else {
			res.send(unreachable);
		}
	});

	app.get('/lights/cxfetch', function(req, res) {
		if (app.lcConnected && app.lightsController.isOpen()) {
			app.lightsController.write("28;");

			var respond = function(data) {
				res.send(data.replace("CTX_", ""));
				app.lightsController.removeListener('data', respond);
			};

			app.lightsController.on('data', respond);
		} else {
			res.send(unreachable);
		}
	});

	app.get('/lights/animation', function(req, res) {
		if (app.lcConnected && app.lightsController.isOpen()) {
			animation = req.query.type;

			app.lightsController.write("28;");

			var respond = function(data) {
				app.lightsController.write("26;");
				app.lightsController.removeListener('data', respond);

				animationContext.initContext = data.replace("CTX_","").split(",");

				performAniamtionStep();
			};

			app.lightsController.on('data', respond);
			res.send("ANIMATION_STARTING");
		} else {
			res.send(unreachable);
		}
	});

	app.get('/lights/animationStop', function(req, res) {
		animation = "";
		animationContext = {};

		if (app.lcConnected && app.lightsController.isOpen()) {
			app.lightsController.write("27;");
		}

		res.send("ANIMATION_STOPPED");
	});

	function performAniamtionStep() {
		if (animation == "fade") {
			
		}

		if (animation != "") {
			setTimeout(performAniamtionStep, 100);
		}
	}
};