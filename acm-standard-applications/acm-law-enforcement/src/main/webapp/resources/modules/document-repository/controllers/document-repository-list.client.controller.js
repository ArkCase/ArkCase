'use strict';

angular.module('document-repository').controller('DocumentRepositoryListController', ['$scope', '$state', '$stateParams', '$translate'
    , 'UtilService', 'ObjectService', 'Helper.ObjectBrowserService', 'MessageService'
    , function ($scope, $state, $stateParams, $translate, Util, ObjectService, HelperObjectBrowserService, MessageService) {

        var eventName = "object.inserted";
        $scope.$bus.subscribe(eventName, function (data) {
            MessageService.info(data.objectType + " with ID " + data.objectId + " was created.");
        });
    }
]);