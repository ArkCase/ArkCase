'use strict';

angular.module('profile').controller('Profile.SubscriptionController', ['$scope', 'ConfigService', 'subscriptionService', '$http',
    function ($scope, ConfigService, subscriptionService) {
        $scope.config = ConfigService.getModule({moduleId: 'profile'});
        $scope.$on('req-component-config', onConfigRequest);
        function onConfigRequest(e, componentId) {
            $scope.config.$promise.then(function (config) {
                var componentConfig = _.find(config.components, {id: componentId});
                $scope.$broadcast('component-config', componentId, componentConfig);
            });
        }
        $scope.subscribptionGridOptions = {
            columnDefs: [
                {field: 'Title'},
                {field: 'Type'},
                {field: 'Created'},
//                {field: "Discontinued",template: '<input type="checkbox" #= Discontinued ? "checked=checked" : "" # disabled="disabled" ></input>'}
            ]
        };
        subscriptionService.getSubscriptions().then(function (data) {
            for (var i = 0; i<data.length; i++) {
                $scope.subscribptionGridOptions.data.push(
                    {
                        "Title": data[i].objectTitle,
                        "Type": data[i].subscriptionObjectType,
                        "Created": data[i].created
                    }
                );
            }
        });

    }
]);
