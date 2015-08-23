tenjin.controller('nprController', function($scope, $location, $http) {
	$http.get('http://api.npr.org/query?output=JSON&numResults=20&apiKey=MDE1NzM4ODYyMDE0MDcxNzIyOTZlYWQxZQ001')
	    .then(function(res){
	    	$scope.stories = [];
	    	for (var i = 0; i < 20; i++){
	    		if("image" in res.data.list.story[i]){
	    			res.data.list.story[i].teaser.$text = String(res.data.list.story[i].teaser.$text).replace(/<[^>]+>/gm, '');
	    			$scope.stories.push(res.data.list.story[i]);
	    		}
	    	}
	});
});