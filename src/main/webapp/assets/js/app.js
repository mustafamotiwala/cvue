
var cvueApp = angular.module('cvueApp',[]);

cvueApp.controller('cvueController', function cvueController($scope, $http){
    $scope.subjects = [
        {'name': 'Numeracy'},
        {'name': 'Reading'},
        {'name': 'Spelling'}
    ];
    $scope.subject=""
    $scope.setSubject = function(s){
        $scope.subject=s;
        $scope.loadSchools();
    }
    $scope.loadSchools = function(){
        $http.get(/list/+$scope.subject).success(function(data){
            $scope.schools = data;
        });
    };
});

cvueApp.filter("subjectFilter", function(){
    return function(input){
        if(input) return input;
        else return "Select one...";
    };
});
