tenjin.controller('redditController',function($rootScope,$http){
	var reddit = {};
	$rootScope.reddit = "";
	var subreddits = ['technology','all','netsec'];

	for (var i = 0; i < subreddits.length; i++){
		var subreddit = subreddits[i];
		$http.get('http://www.reddit.com/r/' + subreddits[i]  +'/top/.json')
		.then(function(res, i){
			console.log(subreddits[i]);
			reddit[subreddits[i]] = [];
			for (var j = 0; j < 3; j++){
				reddit[subreddits[i]].push(res.data.data.children[j].data);
			}
		console.log(reddit);
		});
	}
});