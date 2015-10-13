'use strict';

angular.module('services').factory('CacheService', function($cacheFactory) {
    return $cacheFactory('cache', {
        capacity: 8
    });
});