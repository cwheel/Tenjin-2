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
    })

  }]);
  tenjin.controller('mainController', function($scope,$location,$timeout,$route, $http) {
        var pageId = 1;
        var tfhr = false;
        var subreddits = ['technology','all','netsec'];


        $scope.nextPage = function() {
            pageId = (++pageId % (((Object.keys($route.routes).length - 2) / 2) + 1)) || 1;
            $location.path("/" + pageId);

            $timeout(function() {
              $scope.nextPage();
            }, 300000);
        };

        $scope.updateReddit = function(){
            $scope.reddit = {};
            for (var i = 0; i < subreddits.length; i++){
              $http.get('http://www.reddit.com/r/' + subreddits[i]  +'/top/.json')
              .then(function(res){
                var subreddit = res.data.data.children[0].data.subreddit;
                $scope.reddit[subreddit] = [];
                for (var j = 0; j < 3; j++){
                  $scope.reddit[subreddit].push(res.data.data.children[j].data);
                }
              });
            }
            $timeout(function() {
              $scope.updateReddit();
            }, 30000);
        }

        // Used to Start the Loop for the first time.
        $timeout(function() {
          $scope.nextPage();
        }, 30000);
        $scope.updateReddit();


        $scope.updateClock = function() {
          if (tfhr) {
            var now = new Date();
            var min = now.getMinutes();
            var hour = now.getHours() >= 12 ? now.getHours() + 12 : now.getHours();

            $scope.time = hour + ":" + min;
          } else {
            var now = new Date();
            var monthNames = ["January", "February", "March", "April", "May", "June","July", "August", "September", "October", "November", "December"];

            var min = now.getMinutes();
            var hour = now.getHours();
            $scope.day = now.getDay();
            if ($scope.day.toString().slice(-1) == 1){
              $scope.ending = "st";
            }else if ($scope.day.toString().slice(-1) == 2){
              $scope.ending = "nd";
            } else if ($scope.day.toString().slice(-1) == 3){
              $scope.ending = "rd";
            } else {
              $scope.ending = "th";
            }
            $scope.month = monthNames[now.getMonth()];
            var section = now.getHours() > 12 ? " PM" : " AM";

            if (min < 10) { 
              min = "0" + min;
            }

            $scope.time = hour + ":" + min;
          }

          $timeout(function() {
            $scope.updateClock();
          }, 500);
        };

        $scope.updateClock();

    });