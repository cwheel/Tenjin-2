var request = require("request");
var striptags = require('striptags');

module.exports = function(app) {
	app.get('/web/uit-status', function(req, res){
		request('http://www.it.umass.edu/status/raw', function (error, response, body) {
		  if (!error && response.statusCode == 200) {
		  	var resp = JSON.parse(body);

		  	for (var service in resp) {
		  	    if (resp.hasOwnProperty(service)) {
		  	        resp[service].messages = striptags(resp[service].messages);
		  	        resp[service].status = striptags(resp[service].status);
		  	    }
		  	}

		    res.send(resp);
		  } else {
		  	res.send("Failed to fetch status!");
		  }
		})
	});
};
