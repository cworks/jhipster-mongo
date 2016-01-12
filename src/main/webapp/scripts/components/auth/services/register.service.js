'use strict';

angular.module('hipstermongoApp')
    .factory('Register', function ($resource) {
        return $resource('api/register', {}, {
        });
    });


