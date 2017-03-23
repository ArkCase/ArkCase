'use strict';

angular.module('people').controller('PeopleListController', ['$scope', '$state', '$stateParams', '$translate'
    , 'UtilService', 'ObjectService', 'People.ListService', 'People.InfoService', 'Helper.ObjectBrowserService'
    , 'ServCommService', 'MessageService'
    , function ($scope, $state, $stateParams, $translate
        , Util, ObjectService, PeopleListService, PeopleInfoService, HelperObjectBrowserService
        , ServCommService, MessageService) {

        // maybe optional listener for "close-complaint"?
        var eventName = "object.inserted";
        $scope.$bus.subscribe(eventName, function (data) {
            if (data.objectType === ObjectService.ObjectTypes.PERSON) {

                var objectTypeString = $translate.instant('common.objectTypes.' + data.objectType);
                var objectWasCreatedMessage = $translate.instant('common.objects.objectWasCreatedMessage ', {
                    objectTypeString: objectTypeString,
                    objectId: data.objectId
                });

                MessageService.info(objectWasCreatedMessage);
            }
        });

        //"treeConfig", "treeData", "onLoad", and "onSelect" will be set by Tree Helper
        new HelperObjectBrowserService.Tree({
            scope: $scope
            , state: $state
            , stateParams: $stateParams
            , moduleId: "people"
            , resetTreeData: function () {
                return PeopleListService.resetPeopleTreeData();
            }
            , updateTreeData: function (start, n, sort, filters, query, nodeData) {
                return PeopleListService.updatePeopleTreeData(start, n, sort, filters, query, nodeData);
            }
            , getTreeData: function (start, n, sort, filters, query) {
                return PeopleListService.queryPeopleTreeData(start, n, sort, filters, query);
            }
            , getNodeData: function (personId) {
                return PeopleInfoService.getPersonInfo(personId);
            }
            , makeTreeNode: function (personInfo) {
                return {
                    nodeId: Util.goodValue(personInfo.personId, 0)
                    , nodeType: ObjectService.ObjectTypes.PERSON
                    , nodeTitle: Util.goodValue(personInfo.personTitle)
                    , nodeToolTip: Util.goodValue(personInfo.personTitle)
                };
            }
        });

    }
]);
