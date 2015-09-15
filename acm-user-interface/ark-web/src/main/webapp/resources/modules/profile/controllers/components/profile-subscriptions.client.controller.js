'use strict';

angular.module('profile').controller('Profile.SubscriptionController', ['$scope', 'ConfigService', 'subscriptionService', '$modal',
    function ($scope, ConfigService, subscriptionService,$modal) {
        $scope.config = ConfigService.getModule({moduleId: 'profile'});
        $scope.$on('req-component-config', onConfigRequest);
        function onConfigRequest(e, componentId) {
            $scope.config.$promise.then(function (config) {
                var componentConfig = _.find(config.components, {id: componentId});
                $scope.$broadcast('component-config', componentId, componentConfig);
            });
        }
        function extractDate(date){
            var newDate=date.substring(0,date.indexOf('T')).split('-');
            newDate.push(newDate.shift());
            return newDate.join('-');  
        };
        
        $scope.unsubscript = function (rowEntity) {
            var index = $scope.subscribptionGridOptions.data.indexOf(rowEntity);
            var userID = rowEntity.userID;
            var parentID = rowEntity.parentID;
            var type=rowEntity.type;
            subscriptionService.removeSubscriptions(userID,type,parentID);
            $scope.subscribptionGridOptions.data.splice(index, 1);
        };
        $scope.unsubscriptSelected=function(){
             var rowSelected=$scope.gridApi.selection.getSelectedRows();
             for(var i=0;i<rowSelected.length;i++){
                    var index = $scope.subscribptionGridOptions.data.indexOf(rowSelected[i]);
                    var userID = rowSelected[i].userID;
                    var parentID = rowSelected[i].parentID;
                    var type=rowSelected[i].type;
                    subscriptionService.removeSubscriptions(userID,type,parentID);
                    $scope.subscribptionGridOptions.data.splice(index, 1);
             }
        };
        var deleteCellTemplate='<div align="center"><button type="button" ng-click="grid.appScope.unsubscript(row.entity)" class="btn btn-default btn-sm glyphicon glyphicon-minus"></button></div>';
        var headerDeleteCellTemplate='<div></div>';
        $scope.subscribptionGridOptions = {
            paginationPageSizes: [5, 10, 20],
            paginationPageSize: 5 ,
            columnDefs: [
                {name: 'Title', field: 'title' ,width:'40%'},
                {name: 'Type', field: 'type',width:'20%'},
                {name: 'Created', field: 'created',width:'25%'},
                {name: ' ', field: 'delete',cellTemplate: deleteCellTemplate, headerCellTemplate:headerDeleteCellTemplate,width:'*'},
                {name: 'ParentID', field: 'parentID',visible:false},
                {name: 'UserID', field: 'userID',visible:false}
            ]
        };
         $scope.subscribptionGridOptions.onRegisterApi = function( gridApi ) {
            $scope.gridApi = gridApi;
        };
        subscriptionService.getSubscriptions().then(function (data) {
            for (var i = 0; i<data.length; i++) {
                $scope.subscribptionGridOptions.data.push(
                    {
                        "title": data[i].objectTitle,
                        "type": data[i].subscriptionObjectType,
                        "created": extractDate(data[i].created),
                        "parentID": data[i].objectId,
                        "userID": data[i].userId
                    }
                );
            }
        });

    }
]);
