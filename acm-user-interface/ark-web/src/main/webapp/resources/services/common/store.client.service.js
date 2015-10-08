'use strict';

angular.module('services').factory('StoreService', ['$rootScope',
    function ($rootScope) {
        var Store = {};

        Store.Variable = function(name, initValue) {
            this.name = name;
            this.value = initValue;
        }
        Store.Variable.prototype = {
            get: function(name) {
                return this.value;
            }
            ,set: function(name, value) {
                this.value = value;
            }
        }
        return Store;

    }
]);