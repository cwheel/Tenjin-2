tenjin.controller('nprController', function($scope, $location, $http) {
	$http.get('/cache/npr/')
	    .then(function(res){
	    	$scope.stories = [];
	    	var cur = 0;
	    	for (var i = 0; i < 20; i++){
	    		if("image" in res.data.list.story[i]){
	    			if (cur % 2 == 1){
						res.data.list.story[i].teaser.$text = String(res.data.list.story[i].teaser.$text).replace(/<[^>]+>/gm, '');
		    			$scope.stories[Math.floor(cur/2)].storytwo = res.data.list.story[i];
		    			console.log($scope.stories);
		    			cur++;
	    			}else{
		    			res.data.list.story[i].teaser.$text = String(res.data.list.story[i].teaser.$text).replace(/<[^>]+>/gm, '');
		    			$scope.stories.push(res.data.list.story[i]);
		    			cur++;
	    			}
	    		}
	    	}
	});
});