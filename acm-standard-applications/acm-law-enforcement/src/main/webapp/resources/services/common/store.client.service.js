'use strict';

angular.module('services').factory('StoreService', ['Acm.StoreService',
    function (AcmStoreService) {
        var Service = AcmStoreService;

        console.log("Phase out warning: 'StoreService' is renamed. Please change it to 'Acm.StoreService'");

        return Service;
    }
]);