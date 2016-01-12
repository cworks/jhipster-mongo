'use strict';

angular.module('hipstermongoApp')
    .controller('AppDetailController', function ($scope, $rootScope, $stateParams, entity, App) {
        $scope.app = entity;
        $scope.load = function (id) {
            App.get({id: id}, function(result) {
                $scope.app = result;
            });
        };
        var unsubscribe = $rootScope.$on('hipstermongoApp:appUpdate', function(event, result) {
            $scope.app = result;
        });
        $scope.$on('$destroy', unsubscribe);

    });
