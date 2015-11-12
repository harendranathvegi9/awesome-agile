var homepageApp = angular.module('homepageApp', []);

homepageApp.controller('HomePageController',  function($scope) {
  
	 $scope.toolExplain = function() {
		 console.log("clicked");
    	$("#populartools").html("<Strong>Click A Tool To See How It Is Integerated In Our Site</strong>");
	};
	 $scope.toolBack = function() {
		 console.log("clicked");
    	$("#populartools").html("Tools");
	};

});