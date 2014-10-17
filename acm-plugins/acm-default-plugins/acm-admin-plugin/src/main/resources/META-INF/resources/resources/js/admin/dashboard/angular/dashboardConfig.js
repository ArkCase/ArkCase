/**
 * Created by marjan.stefanoski on 9/25/2014.
 */
var app = angular.module('config', ['ngTable','dashboardConfigServices'])
.controller('DemoCtrl',['$scope', 'RolesByWidgets','$http' ,function($scope, RolesByWidgets, $http) {
        $scope.tempWidgets = RolesByWidgets.query();
        $scope.tempWidgets.$promise.then(function (result) {
            $scope.widgets = result;
            $scope.allWidgets = [];
            angular.forEach($scope.widgets, function(widget){
                var element = new Object;
                element.name=widget.name;
                $scope.allWidgets.push(element);
            });
            $scope.selectedWidget = $scope.allWidgets[0];
        });


        $scope.authorized;
        $scope.notAuthorized;
        $scope.selectedAuthorized;
        $scope.selectedNotAuthorized;
        $scope.selectedWidget;
        var firstSelection = true;

        $scope.indexSelections = function() {
                if(!firstSelection) {
                $scope.authorized = [];
                $scope.notAuthorized = [];
                $scope.selectedAuthorized = [];
                $scope.selectedNotAuthorized = [];
            } else {
                $scope.authorized = [];
                $scope.notAuthorized = [];
                firstSelection = false;
            }
        };

        $scope.moveRight = function() {
            angular.forEach($scope.widgets, function(widget) {
                if(angular.equals(widget.name,$scope.selectedWidget.name)) {// $scope.tmpWidget[0].name)) {
                    angular.forEach($scope.selectedNotAuthorized, function(selectedRole,index) {
                        console.log(selectedRole);
                        widget.widgetAuthorizedRoles.push(selectedRole);
                        var i = elementIndexFromArray(widget.widgetNotAuthorizedRoles,selectedRole);
                        if(i!=-1) {
                            widget.widgetNotAuthorizedRoles.splice(i, 1);
                        }
                    });
                    $scope.selectedNotAuthorized = [];
                }
            });
            firstSelection = true;
            $scope.saveAuthorized();
        };

        $scope.moveLeft = function() {
            angular.forEach($scope.widgets, function(widget) {
                if(angular.equals(widget.name,$scope.selectedWidget.name)) {// $scope.tmpWidget[0].name)) {
                    angular.forEach($scope.selectedAuthorized, function (selectedRole, index) {
                        console.log(selectedRole);
                        widget.widgetNotAuthorizedRoles.push(selectedRole);
                        var i = elementIndexFromArray(widget.widgetAuthorizedRoles, selectedRole);
                        if (i != -1) {
                            widget.widgetAuthorizedRoles.splice(i, 1);
                        }
                    });
                    $scope.selectedAuthorized = [];
                }
            });
                    firstSelection = true;
                    $scope.saveAuthorized();
        };

        $scope.saveAuthorized = function() {
            angular.forEach($scope.widgets, function(widget) {
                if(angular.equals(widget.name, $scope.selectedWidget.name)) {
                    var url = App.Object.getContextPath() + "/api/latest/plugin/dashboard/widgets/set",
                    postObject = new Object;
                    postObject.widgetName = widget.widgetName;
                    postObject.widgetAuthorizedRoles = [];
                    angular.forEach(widget.widgetAuthorizedRoles, function(role){
                        aRole = new Object;
                        aRole.name=  role.name;
                        postObject.widgetAuthorizedRoles.push(aRole);
                    });
                    postObject.widgetNotAuthorizedRoles = [];
                    angular.forEach(widget.widgetNotAuthorizedRoles,function(role){
                      nARole = new Object;
                      nARole.name = role.name;
                      postObject.widgetNotAuthorizedRoles.push(nARole);
                    });
                    $http({
                        method: "POST",
                        url: url,
                        data: JSON.stringify(postObject),
                        headers: {
                            "Content-Type": "application/json"
                        }
                    }).success(function() {})
                        .error(function() {});
                }
            });
        };

        $scope.select = function() {
            angular.forEach($scope.widgets, function(widget){
                if(angular.equals(widget.name,$scope.selectedWidget.name)) {
                       $scope.authorized = widget.widgetAuthorizedRoles;
                       $scope.notAuthorized = widget.widgetNotAuthorizedRoles;
                   }
            });
            console.log($scope.selectedWidget);
        }

         var elementIndexFromArray = function(arr,elm){
             for(var i = 0; i < arr.length; i++){
                 if(angular.equals(arr[i], elm)){
                   return i;
                 }
             };
             return -1;
         }

        //this save is functional example when used multiselect with chosen.
        $scope.save = function(idx){
           var allRoles="";
           console.log($scope.widgets[idx].widgetRoles);
           angular.forEach($scope.widgets[idx].widgetAuthorizedRoles ,function(role) {
               allRoles += role.name;
           });
           var url = App.Object.getContextPath() + "/api/latest/plugin/dashboard/widgets/set",
               postObject = new Object;
               postObject.widgetName = $scope.widgets[idx].widgetName;
               postObject.widgetAuthorizedRoles = $scope.widgets[idx].widgetAuthorizedRoles;
               postObject.widgetNotAuthorizedRoles = $scope.widgets[idx].widgetNotAuthorizedRoles;
                   $http({
                            method: "POST",
                            url: url,
                            data: JSON.stringify(postObject),
                            headers: {
                                "Content-Type": "application/json"
                   }
                   }).success(function() {})
                     .error(function() {});
           alert(allRoles);
       };
    }]).directive('chosen', function (){
       //this directive is needed to trigger event so chosen will be able to see changes.
        var linker = function (scope, element, attr) {
           scope.$watch('myRole',function(){
                element.trigger('chosen:updated');
           });
           element.chosen();
       };
        return {
            restrict: 'A',
            link: linker
        }
    })

