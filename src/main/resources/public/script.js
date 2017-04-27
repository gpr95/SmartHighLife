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
        });
});
app.controller("devicesCtrl", function ($scope, $http, $interval) {

});
app.controller("contactCtrl", function ($scope, $http) {

});






