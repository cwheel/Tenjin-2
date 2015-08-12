tenjin.controller('uitController', function($scope, $location, $http) {
	$http.get('/web/uit-status')
	    .then(function(res){
	    	$scope.data = res.data;
	});
 
	$scope.getMessageClass = function(status) {
		if (status != "OK") {
			return "uit-message " + status;
		}
		return "uit-message";
	};
});