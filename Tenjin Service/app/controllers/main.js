tenjin.controller('mainController', function($scope,$location,$timeout,$route, $http) {
    var pageId = 1;
    var tfhr = false;
    var subreddits = ['technology','all','netsec'];
    $scope.backgroundId = 1;
    var numBackgrounds = 1;

    $scope.backgroundsUpdater = function(){
        $scope.backgroundId = ++$scope.backgroundId % (numBackgrounds +1) || 1;
        $timeout(function() {
          $scope.backgroundsUpdater();
        }, 3000);
    }

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
			$http.get('http://www.reddit.com/r/' + subreddits[i]  +'/top/.json').then(function(res){
				var subreddit = res.data.data.children[0].data.subreddit;
				$scope.reddit["/r/" + subreddit] = [];
				
				for (var j = 0; j < 3; j++){
					var extra = "";
					if (j < 3) extra = " • ";

					$scope.reddit["/r/" + subreddit].push(res.data.data.children[j].data.title + extra);
				}
			});
        }

        $timeout(function() {
        	$scope.updateReddit();
        }, 30000);
    }

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

    $timeout(function() {
      $scope.nextPage();
    }, 30000);

    $scope.updateReddit();
    $scope.updateClock();
    $scope.backgroundsUpdater();
});