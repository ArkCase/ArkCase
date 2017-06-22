angular.module('admin').controller('Admin.EmailTemplatesModalController', ['$scope', '$modalInstance', 'params',
    function ($scope, $modalInstance, params) {

        $scope.templateFile = null;
        $scope.isEdit = params.isEdit;
        $scope.template = params.template;
        $scope.objectTypeList = params.objectTypeList;
        $scope.actionList = params.actionList;
        $scope.sourceList = params.sourceList;

        $scope.onClickCancel = function () {
            $modalInstance.dismiss('Cancel');
        };

        $scope.onClickOk = function () {

            $modalInstance.close(
                {
                    file: $scope.templateFile,
                    template: $scope.template,
                    isEdit: $scope.isEdit
                }
            );
        };

        $scope.toggleObjectTypes = function (objectType) {
            var idx = $scope.template.objectTypes.indexOf(objectType);

            // Is currently selected
            if (idx > -1) {
                $scope.template.objectTypes.splice(idx, 1);
            }

            // Is newly selected
            else {
                $scope.template.objectTypes.push(objectType);
            }
        };

        $scope.toggleActions = function (action) {
            var idx = $scope.template.actions.indexOf(action);

            // Is currently selected
            if (idx > -1) {
                $scope.template.actions.splice(idx, 1);
            }

            // Is newly selected
            else {
                $scope.template.actions.push(action);
            }
        };

        /*        $scope.toggleSources = function (source) {
         var idx = $scope.template.sources.indexOf(source);

         // Is currently selected
         if (idx > -1) {
         $scope.template.sources.splice(idx, 1);
         }

         // Is newly selected
         else {
         $scope.template.sources.push(source);
         }
         };*/

    }
]);