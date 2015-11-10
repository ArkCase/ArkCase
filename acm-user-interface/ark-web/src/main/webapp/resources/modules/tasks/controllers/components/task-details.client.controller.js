'use strict';

angular.module('tasks').controller('Tasks.DetailsController', ['$scope', '$stateParams', 'UtilService', 'ValidationService', 'TasksService',
    function ($scope, $stateParams, Util, Validator, TasksService) {
        $scope.$emit('req-component-config', 'details');
        $scope.$on('component-config', function (e, componentId, config) {
            if ('details' == componentId) {
                $scope.config = config;
            }
        });

        $scope.$on('task-retrieved', function (e, data) {
            if (Validator.validateTask(data)) {
                $scope.taskInfo = data;
            }
        });


        $scope.options = {
            focus: true
            //,height: 120
        };

        //$scope.editDetails = function() {
        //    $scope.editor.summernote({focus: true});
        //}
        $scope.saveDetails = function () {
            //$scope.editor.destroy();
            var taskInfo = Util.omitNg($scope.taskInfo);
            Util.serviceCall({
                service: TasksService.save
                , data: taskInfo
            });
        };


        //$scope.taskSaveStat = {
        //	dataChanged: false
        //	,dataMoreChanged: false
        //	,saveInitiated: false
        //	,saveInProgress: false
        //	,lastError: null
        //}
        //
        //var saveIt = function() {
        //   console.log('debounce, $scope.taskInfo.details=' + $scope.taskInfo.details);
        //   $scope.taskSaveStat.saveInProgress = true;
        //   $scope.editor.summernote('saveRange');
        //
        //   var taskInfo = Util.omitNg($scope.taskInfo);
        //   TasksService.save({}, taskInfo
        //       ,function(successData) {
        //           console.log('success, successData.details=' + successData.details);
        //           $scope.taskSaveStat.saveInProgress = false;
        //           $scope.editor.summernote('restoreRange');
        //           if ($scope.taskSaveStat.dataMoreChanged) {
        //               console.log('successm dataMoreChanged, $scope.taskInfo.details=' + $scope.taskInfo.details);
        //               initiateSave();
        //
        //           } else {
        //               console.log('successm NOT dataMoreChanged, $scope.taskInfo.details=' + $scope.taskInfo.details);
        //               $scope.taskSaveStat.dataChanged = false;
        //               $scope.taskSaveStat.saveInitiated = false;
        //           }
        //           var z = 1;
        //       }
        //       ,function(errorData) {
        //           console.log('error, $scope.taskInfo.details=' + $scope.taskInfo.details);
        //           $scope.taskSaveStat.lastError = "Failed to save";
        //           $scope.taskSaveStat.saveInProgress = false;
        //           $scope.editor.summernote('restoreRange');
        //           if ($scope.taskSaveStat.dataMoreChanged) {
        //               initiateSave();
        //
        //           } else {
        //               $scope.taskSaveStat.dataChanged = false;
        //               $scope.taskSaveStat.saveInitiated = false;
        //           }
        //           var z = 1;
        //       }
        //   );
        //};
        //var saveLater = _.debounce(saveIt, 1000, false);
        //var initiateSave = function() {
        //   console.log('initiateSave, $scope.taskInfo.details=' + $scope.taskInfo.details);
        //	$scope.taskSaveStat.saveInitiated = true;
        //
        //   $scope.taskSaveStat.dataMoreChanged = false;
        //   saveLater();
        //};
        //$scope.change = function(contents) {
        //	console.log('contents 100: contents=', contents);
        //   return;
        //
        //	if ($scope.taskInfo.details != contents) {
        //       console.log('changed 111: contents=', contents);
        //		$scope.taskInfo.details = contents;
        //		$scope.taskSaveStat.dataChanged = true;
        //		if ($scope.taskSaveStat.saveInProgress) {
        //           console.log('changed 122: contents=', contents);
        //			$scope.taskSaveStat.dataMoreChanged = true;
        //		}
        //       console.log('changed 129: contents=', contents);
        //
        //		//if (true) { //can save
        //			if (!$scope.taskSaveStat.saveInitiated) {
        //               console.log('changed 133: contents=', contents);
        //				initiateSave();
        //               console.log('changed 144: contents=', contents);
        //			}
        //		//}
        //	}
        //   console.log('changed 199: contents=', contents);
        //};

    }
]);