 'use strict';

angular.module('hipstermongoApp')
    .factory('notificationInterceptor', function ($q, AlertService) {
        return {
            response: function(response) {
                var alertKey = response.headers('X-hipstermongoApp-alert');
                if (angular.isString(alertKey)) {
                    AlertService.success(alertKey, { param : response.headers('X-hipstermongoApp-params')});
                }
                return response;
            }
        };
    });
