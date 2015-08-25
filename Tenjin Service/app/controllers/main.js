tenjin.controller('mainController', function($scope,$location,$timeout,$route, $http) {
    var pageId = 1;
    var tfhr = false;
    var subreddits = ['technology','news','netsec', 'todayilearned'];
    $scope.backgroundId = 1;
    var numBackgrounds = 1;

    $scope.backgroundsUpdater = function(){
        $scope.backgroundId = ++$scope.backgroundId % (numBackgrounds +27) || 1;
        $timeout(function() {
          $scope.backgroundsUpdater();
        }, 10000);
    }

    $scope.nextPage = function() {
    	pageId = (++pageId % (((Object.keys($route.routes).length - 2) / 2) + 1)) || 1;
        $location.path("/" + pageId);

        $timeout(function() {
          $scope.nextPage();
        }, 30000);
    };

    $("#scrollerContent2").css("left", $(window).width());
    $("#scrollerContent").css("left", $(window).width());

    $scope.updateReddit = function(scroller) {
    	if (scroller == 1) {
    		$scope.reddit = {};
    	} else {
    		$scope.reddit2 = {};
    	}
        
        for (var i = 0; i < subreddits.length; i++){
			$http.get('http://www.reddit.com/r/' + subreddits[i]  +'/top/.json').then(function(res){
				var subreddit = res.data.data.children[0].data.subreddit;

				if (scroller == 1) {
					$scope.reddit["/r/" + subreddit] = [];
				} else {
					$scope.reddit2["/r/" + subreddit] = [];
				}
				
				for (var j = 0; j < 3; j++){
					var extra = "";
					if (j < 2) extra = " • ";

					if (scroller == 1) {
						$scope.reddit["/r/" + subreddit].push(res.data.data.children[j].data.title + extra);
					} else {
						$scope.reddit2["/r/" + subreddit].push(res.data.data.children[j].data.title + extra);
					}
				}
			});
        }
    };

    $scope.scroll = function() {
    	var scroller2 = false;
    	var scroller = false;
    	
    	$scope.$watch(function () { return $("#scrollerContent").css("left") }, function(newVal, oldVal) {
    		var pos = Math.abs($("#scrollerContent").width() + Number(newVal.replace("px", "")));
    		if (pos < $(window).width() && !scroller2) {
    			scroller2 = true;

    			$("#scrollerContent2").css("left", $(window).width());
    			$("#scrollerContent2").animate({left: "-=" + ($("#scrollerContent2").width() + $(window).width())}, 60000, "linear");

    			$scope.$watch(function () { return $("#scrollerContent2").css("left") }, function(newVal2, oldVal2) {
    				var pos2 = Math.abs($("#scrollerContent2").width() + Number(newVal2.replace("px", "")));
    				if (pos2 < $(window).width() && !scroller) {
    					scroller = true;

    					$("#scrollerContent").css("left", $(window).width());
    					$("#scrollerContent").animate({left: "-=" + ($("#scrollerContent").width() + $(window).width())}, 60000, "linear");
    					$scope.scroll();
    				} else if (pos2 == 0) {
    					$scope.updateReddit(2);
    				}
    			});
    		} else if (pos == 0) {
    			$scope.updateReddit(1);
    		}
    	});
    };

    $scope.updateReddit(1);
    $scope.updateReddit(2);

	$timeout(function() {
		$("#scrollerContent").animate({left: "-=" + ($("#scrollerContent").width() + $(window).width())}, 60000, "linear");
		$scope.scroll();
	}, 1000);

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
			$scope.day = now.getDate();
            
			if ($scope.day.toString().slice(-1) == 1){
				$scope.ending = "st";
			} else if ($scope.day.toString().slice(-1) == 2){
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

    $scope.updateClock();
    $scope.backgroundsUpdater();
});