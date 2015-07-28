tenjin.controller('redditController',function($rootScope,$http){
	var reddit = {};
	$rootScope.reddit = "";
	var subreddits = ['technology','all','netsec'];

	for (var i = 0; i < subreddits.length; i++){
		$http.get('http://www.reddit.com/r/' + subreddits[i]  +'/top/.json')
		.then(function(res){
			var subreddit = res.data.data.children[0].data.subreddit;
			reddit[subreddit] = [];
			for (var j = 0; j < 3; j++){
				reddit[subreddit].push(res.data.data.children[j].data);
			}
		console.log(reddit);
		});
	}
});