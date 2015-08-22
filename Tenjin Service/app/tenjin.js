var tenjin = angular.module('tenjin', ['ngRoute', 'ngAnimate','angular-skycons',]);

tenjin.config(['$routeProvider',
	function($routeProvider) {
	
	$routeProvider.when('/',{
		redirectTo: '/1'
	}).when('/1', {
		templateUrl: '/pages/hourlyWeather.html',
		controller: 'hourlyWeatherController'
	}).when('/2',{
		templateUrl: '/pages/uit.html',
		controller: 'uitController'   
	}).when('/3',{
		templateUrl: '/pages/weeklyWeather.html',
		controller: 'weeklyWeatherController'
	}).when('/4',{
		templateUrl: '/pages/npr.html',
		controller: 'nprController'
	})
}]);