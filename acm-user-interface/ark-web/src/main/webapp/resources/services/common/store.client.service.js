'use strict';

angular.module('services').factory('StoreService', ['$rootScope',
    function ($rootScope) {
        var Store = {};

        Store.Variable = function(arg) {
            this.name = arg.name;
            this.value = arg.initValue;
        }
        Store.Variable.prototype = {
            get: function() {
                return this.value;
            }
            ,set: function(value) {
                this.value = value;
            }
        }
        return Store;

    }
]);