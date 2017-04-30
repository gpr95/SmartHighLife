var app = angular.module("smartHome", ["ngRoute"]);
app.config(function($routeProvider, $httpProvider) {
    $routeProvider
        .when("/", {
            templateUrl : "home.html"
        })
        .when("/devices", {
            templateUrl : "devices.html",
            controller : "devicesCtrl"
        })
        .when("/contact", {
            templateUrl : "contact.html",
            controller : "contactCtrl"
        })
        .when("/parentalControl", {
            templateUrl : "parentalControl.html",
            controller : "parentalCtrl"
        })
        .when("/login",{
        	templateUrl : "login.html",
        	controller : "navigation"
        });
    
    $httpProvider.defaults.headers.common["X-Requested-With"] = 'XMLHttpRequest';
});

app.controller("devicesCtrl", function ($http) {
	var self = this;
	$http.get('/resource/').success(function(data) {
		self.a = data;
	});
	
});

app.controller("contactCtrl", function ($scope, $http) {

});

app.controller("parentalCtrl", function ($scope, $http) {

});

app.controller('navigation', 
		function($rootScope, $scope, $http, $location){
	
	var authenticate = function(credentials, callback) {
		
		var headers = credentials ? {authorization : "Basic "
	        + btoa(credentials.username + ":" + credentials.password)
	    } : {};
		
	    $http.get('user', {headers : headers}).success(function(data) {
	      if (data.name) {
	    	  console.log("Success authentication");
	        $rootScope.authenticated = true;
	      } else {
	    	  console.log("Authentication failed");
	        $rootScope.authenticated = false;
	      }
	      callback && callback();
	    }).error(function() {
	      $rootScope.authenticated = false;
	      callback && callback();
	    });
	}
	
	$scope.login = function() {
	    $http.post('login-post', $scope.credentials, {
	      headers : {
	        "content-type" : "application/json",
	        'Accept': 'application/json'
	      }
	    }).success(function(data) {
	      authenticate($scope.credentials,function() {
	        if ($rootScope.authenticated) {
	          $location.path("/");
	          $scope.error = false;
	        } else {
	          $location.path("/login");
	          $scope.error = true;
	        }
	      });
	    }).error(function(data) {
	      $location.path("/login");
	      $scope.error = true;
	      $rootScope.authenticated = false;
	    })
	  };
	  
	  $scope.logout = function() {
		  $http.post('logout', {}).success(function() {
		    $rootScope.authenticated = false;
		    $location.path("/");
		  }).error(function(data) {
		    $rootScope.authenticated = false;
		  });
	  }
	
});




