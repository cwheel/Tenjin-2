tenjin.controller('uitController', function($scope, $location, $http){
	$http.get('/web/uit-status')
	    .then(function(res){
	    	$scope.data = res.data;
	    });
});