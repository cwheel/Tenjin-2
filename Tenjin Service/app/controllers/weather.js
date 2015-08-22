tenjin.controller('weeklyWeatherController', function($scope,$http) {
	$scope.weekWeather = {};
	var days = ['Sunday','Monday','Tuesday','Wednesday','Thursday','Friday','Saturday'];
	$http.get('/web/weather')
	    .then(function(res){
	    	$scope.weekWeather = res.data.daily;
	    	for (var i = 0; i < 6; i++){
	    		//
	    		if (i == 0){
	    			$scope.weekWeather.data[i].day = "Today";
	    		} else if (i == 1){
	    			$scope.weekWeather.data[i].day = "Tommorow";
	    		} else{
	    			var now = new Date();
	    			$scope.weekWeather.data[i].day = days[(now.getDay() + i) % 7];
	    		}
	    		//Removes Signifigant Digits to Rain Amount 
	    		if ($scope.weekWeather.data[i].precipIntensity != 0){
		    		$scope.weekWeather.data[i].precipIntensity = $scope.weekWeather.data[i].precipIntensity * 100;
		    		$scope.weekWeather.data[i].precipIntensity = Math.round($scope.weekWeather.data[i].precipIntensity) /100;
		    		if ($scope.weekWeather.data[i].precipIntensity == 0){
		    			$scope.weekWeather.data[i].precipIntensity =  "> 0.01"
		    		}
		    	}
		    	// Rounds Temperature to a whole number
		    	$scope.weekWeather.data[i].apparentTemperatureMax = Math.floor($scope.weekWeather.data[i].apparentTemperatureMax);
		    	$scope.weekWeather.data[i].apparentTemperatureMin = Math.floor($scope.weekWeather.data[i].apparentTemperatureMin);
		    	//Turns Probability to a Percent
		    	$scope.weekWeather.data[i].precipProbability = Math.floor($scope.weekWeather.data[i].precipProbability * 100);
		    	//Include Time of hot and cold
		    	var min = new Date(($scope.weekWeather.data[i].apparentTemperatureMinTime * 1000));
		    	var max = new Date(($scope.weekWeather.data[i].apparentTemperatureMaxTime * 1000));
		    	$scope.weekWeather.data[i].minTime = min.getHours();
		    	$scope.weekWeather.data[i].maxTime = max.getHours();
	    	}
	});

});
tenjin.controller('hourlyWeatherController', function($scope,$http){
	$scope.hourWeather = {};
	$scope.minWeather = {};
	$http.get('/web/weather')
	.then(function(res){
		$scope.hourWeather = res.data.hourly;
		console.log($scope.hourWeather);
		$scope.minWeather = res.data.minutely;
		console.log($scope.minWeather);
	});
	$scope.getHour = function(unix){
		return new Date(unix * 1000).getHours();
	}
});

