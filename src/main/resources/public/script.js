var app = angular.module("smartHome", ["ngRoute"]);
app.config(function($routeProvider) {
    $routeProvider
        .when("/", {
            templateUrl : "home.html",
        })
        .when("/viewer", {
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
        });
});
app.controller("devicesCtrl", function ($scope, $http, $interval) {

});
app.controller("contactCtrl", function ($scope, $http) {

});
app.controller("parentalCtrl", function ($scope, $http) {

});





