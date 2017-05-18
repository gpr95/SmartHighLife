var app = angular.module("smartHome", [ "ngRoute", "ngTable" ]);
app
		.config(function($routeProvider, $httpProvider) {
			$routeProvider.when("/", {
				templateUrl : "home.html",
				controller : "homeCtrl"
			}).when("/devices", {
				templateUrl : "devices.html",
				controller : "devicesCtrl"
			}).when("/contact", {
				templateUrl : "contact.html",
				controller : "contactCtrl"
			}).when("/parentalControl", {
				templateUrl : "parentalControl.html",
				controller : "parentalCtrl"
			}).when("/login", {
				templateUrl : "login.html",
				controller : "loginCtrl"
			}).when("/register", {
				templateUrl : "register.html",
				controller : "registerCtrl"
			});

			$httpProvider.defaults.headers.common["X-Requested-With"] = 'XMLHttpRequest';
		});

app.controller("homeCtrl", function($http) {
	var self = this;
	$http.get('/resource/').success(function(data) {
		self.a = data;
	});

});

app.controller("devicesCtrl", function($scope, $http, NgTableParams) {

	$http.get('/resources/').success(function(data) {
		$scope.data = data;
		$scope.tableParams = new NgTableParams({}, {
			dataset : data
		});
	}).error(function(data) {
		$scope.error = true;
	});

});

app.controller("contactCtrl", function($scope, $http) {

});

app.controller("parentalCtrl", function($scope, $http) {

});

app.controller("registerCtrl", function($scope, $http) {
	
	$scope.user = {};
	$scope.user.first_name = "";
	$scope.user.last_name = "";
	$scope.user.email = "";
	$scope.user.username = "";
	$scope.user.password = "";
	$scope.user.confirm_password = "";
	$scope.user.confirm_password = "";

	$scope.register = function() {

		$scope.error1 = false;
		$scope.error2 = false;
		$scope.error3 = false;
		
		if ($scope.user.first_name == "" || $scope.user.last_name == ""
				|| $scope.user.email == "" || $scope.user.username == ""
				|| $scope.user.password == ""
				|| $scope.user.confirm_password == "") {

			$scope.error2 = true;
		}
		

		if (!($scope.error2)) {
			if ($scope.user.password != $scope.user.confirm_password) {
				$scope.error3 = true;
			}else{
			$http.post('register', $scope.user, {
				headers : {
					"content-type" : "application/json",
					'Accept' : 'application/json'
				}
			}).success(function() {
				console.log("Success register, AUTOLOGIN TO_DO");

			}).error(function() {
				$scope.error1 = true;
			});
			}
		}
		$scope.user.first_name = "";
		$scope.user.last_name = "";
		$scope.user.email = "";
		$scope.user.username = "";
		$scope.user.password = "";
		$scope.user.confirm_password = "";
		$scope.user.confirm_password = "";
	}
});

app.controller('loginCtrl', function($rootScope, $scope, $http, $location,
		$window) {

	var authenticate = function(credentials, callback) {

		var headers = credentials ? {
			authorization : "Basic "
					+ btoa(credentials.username + ":" + credentials.password)
		} : {};

		$http.get('user', {
			headers : headers
		}).success(function(data) {
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
				'Accept' : 'application/json'
			}
		}).success(function(data) {
			authenticate($scope.credentials, function() {
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

	$rootScope.logout = function() {
		console.log("Logout action");
		$http.post('logout', {}).success(function() {
			$rootScope.authenticated = false;
			$window.location.reload();
			$location.path("/");
		}).error(function(data) {
			$rootScope.authenticated = false;
		});
	};

});
