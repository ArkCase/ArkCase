'use strict';


//needed for extension projects as of now
//can refactor later

angular.module('services').factory('CacheService', function($cacheFactory) {
    return $cacheFactory('cache', {
        capacity: 8
    });
});