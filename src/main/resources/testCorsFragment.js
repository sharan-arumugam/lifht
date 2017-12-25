as.controller('TestController', function ($scope, $rootScope, $http, base64, $location) {
        var actionUrl = 'admin/all';
        $scope.data = {};
        $scope.mew = "100";
        $scope.test = function () {
        		var actionUrl = 'http://localhost:8080/admin/all/',
                load = function () {
                $http.get(actionUrl, { withCredentials: true }).success(function (data) {
                   console.log(data);
                });
            };
            load();
        };
    });