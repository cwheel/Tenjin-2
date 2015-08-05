var request = require("request");

module.exports = function(app, server, accessToken, privRoutes, pubRoutes) {
	var socket = require('socket.io-client')('https://' + server + ":4000");

	//We connected to the proxy server
	socket.on('connect', function() {
		console.log("=> Connected to ProxyRoute server at " + server + "!");

		//Attempt to authenticate with the provided key
		socket.emit('authentication', {token : accessToken});
		socket.on('authenticated', function() {
	    	console.log("=> Authenticated (SSL) with ProxyRoute server at " + server + "!");
        socket.emit('publicRoutes', pubRoutes);
	  });
	});

	//A GET request was passed through
  	socket.on('GET', function(req) {
  		//Verify that the route is approved for proxying
  		var verified = false;
  		for (var i = privRoutes.length - 1; i >= 0; i--) {
  			if (req.originalUrl.indexOf(privRoutes[i]) === 0) {
  				verified = true;
  				break;
  			}
  		}

      if (!verified) {
        for (var i = pubRoutes.length - 1; i >= 0; i--) {
          if (req.originalUrl.indexOf(pubRoutes[i]) === 0) {
            verified = true;
            break;
          }
        }
      }

  		//If we coulden't verify that the request was allowed, deny it
  		if (!verified) {
  			socket.emit('GET_RESP', 'PERMISSION_DENIED');
  			return;
  		}

  		//Create a request for the local server
  		request({
  		    url:  'http://localhost:' + process.env.PORT + req.originalUrl,
  		    qs: req.query
  		}, function (error, response, body) {
  			//Send the response back to the proxy
  			socket.emit('GET_RESP', body);
   		});
  	});

  	//Connnection to the proxy was dropped
 	socket.on('disconnect', function() {
 		//Try to reconnect
 		socket = require('socket.io-client')('http://' + server + ":4000");
 	});
};