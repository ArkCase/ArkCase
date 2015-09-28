'use strict';

angular.module('cases').controller('Cases.CostController', ['$scope', '$stateParams', '$q', '$window', 'UtilService', 'ValidationService', 'LookupService', 'CasesService',
    function ($scope, $stateParams, $q, $window, Util, Validator, LookupService, CasesService) {
        $scope.$emit('req-component-config', 'cost');

        $scope.currentId = $stateParams.id;
        $scope.start = 0;
        $scope.pageSize = 10;
        $scope.sort = {by: "", dir: "asc"};
        $scope.filters = [];


        var promiseUsers = Util.servicePromise({
            service: LookupService.getUsers
            , callback: function (data) {
                $scope.userFullNames = [];
                var arr = Util.goodArray(data);
                for (var i = 0; i < arr.length; i++) {
                    var obj = Util.goodJsonObj(arr[i]);
                    if (obj) {
                        var user = {};
                        user.id = Util.goodValue(obj.object_id_s);
                        user.name = Util.goodValue(obj.name);
                        $scope.userFullNames.push(user);
                    }
                }
                return $scope.userFullNames;
            }
        });


        $scope.config = null;
        $scope.$on('component-config', applyConfig);
        function applyConfig(e, componentId, config) {
            if (componentId == 'cost') {
                $scope.config = config;
            }
        }
    }
]);