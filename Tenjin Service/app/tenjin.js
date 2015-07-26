var tenjin = angular.module('tenjin', ['ngRoute', 'ngAnimate','angular-skycons',]);
  //https://github.com/dfsq/ngView-animation-effects
tenjin.config(['$routeProvider',
  function($routeProvider) {
    $routeProvider
    // We are using numbers to make our lives easier for making a page loop script
    .when('/',{
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
        templateUrl: '/pages/reddit.html',
        controller: 'redditController'
    })


  }]);
  tenjin.controller('mainController', function($scope,$location,$timeout,$route) {
        var pageId = 1;
        var tfhr = false;

        $scope.nextPage = function() {
            pageId = (++pageId % (((Object.keys($route.routes).length - 2) / 2) + 1)) || 1;
            $location.path("/" + pageId);

            $timeout(function() {
              $scope.nextPage();
            }, 300000);
        };

        // Used to Start the Loop for the first time.
        $timeout(function() {
          $scope.nextPage();
        }, 30000);

        $scope.updateClock = function() {
          if (tfhr) {
            var now = new Date();
            var min = now.getMinutes();
            var hour = now.getHours() >= 12 ? now.getHours() + 12 : now.getHours();

            $scope.time = hour + ":" + min;
          } else {
            var now = new Date();
            var min = now.getMinutes();
            var hour = now.getHours();
            var section = now.getHours() > 12 ? " PM" : " AM";

            if (min < 10) { 
              min = "0" + min;
            }

            $scope.time = hour + ":" + min;
          }

          $timeout(function() {
            $scope.updateClock();
          }, 5);
        };

        $scope.updateClock();

    });
  tenjin.controller('secondController', function($scope,$location,$timeout) {
      // create a message to display in our view
      $scope.message = 'red';

    });
    tenjin.controller('firstController', function($scope,$location,$timeout) {
      // create a message to display in our view
      $scope.message = 'green';

    });

