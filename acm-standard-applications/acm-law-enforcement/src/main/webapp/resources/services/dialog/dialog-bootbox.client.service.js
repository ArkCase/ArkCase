'use strict';

/**
 * @ngdoc service
 * @name services:Dialog.BootboxService
 *
 * @description
 *
 * {@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/services/dialog/dialog-bootbox.client.service.js services/dialog/dialog-bootbox.client.service.js}
 *
 * This service provides wrap function to common bootbox dialogs
 */

angular.module('services').factory('Dialog.BootboxService', ['UtilService', '$q'
    , function (Util, $q) {

        var Service = {

            /**
             * @ngdoc method
             * @name alert
             * @methodOf services:Dialog.BootboxService
             *
             * @param {String} msg Message
             * @param {Function} callback Callback function
             *
             * @description
             * Alert dialog
             * @example
<example>
<file name="app.js">
angular.module('ngAppDemo', []).controller('ngAppDemoController', ['Dialog.BootboxService', '$log', function(dlg, $log) {
    dlg.alert("Hello world!", function() {
      $log.info("Hello world callback");
    });
}]);
</file>
</example>
             */
            alert: function (msg, callback) {
            	var deferred = $q.defer();
                bootbox.alert({
                    message: msg,
                    callback: function(){
                        deferred.resolve();                       
                    }
                });
                return deferred.promise;
            }

            /**
             * @ngdoc method
             * @name confirm
             * @methodOf services:Dialog.BootboxService
             *
             * @param {String} msg Message
             * @param {Function} callback Callback function
             *
             * @description
             * Confirm dialog
             * @example
<example>
<file name="app.js">
angular.module('ngAppDemo', []).controller('ngAppDemoController', ['Dialog.BootboxService', '$log', function(dlg, $log) {
    dlg.alert("Are you sure?", function(result) {
      $log.info("Confirm result: " + result);
    });
}]);
</file>
</example>
             */
            , confirm: function (msg, callback) {
                bootbox.confirm(msg, callback);
            }

            /**
             * @ngdoc method
             * @name prompt
             * @methodOf services:Dialog.BootboxService
             *
             * @param {String} msg Message
             * @param {Function} callback Callback function
             * @param {String} initValue (Optional)Initial value
             *
             * @description
             * Prompt dialog
             * @example
<example>
<file name="app.js">
angular.module('ngAppDemo', []).controller('ngAppDemoController', ['Dialog.BootboxService', '$log', function(dlg, $log) {
    dlg.prompt("What is your name?", function(result) {
       if (result === null) {
          $log.info("Prompt dismissed");
       } else {
          $log.info("Hi " + result);
       }
    });
}]);
</file>
</example>
             */
            , prompt: function (msg, callback, initValue) {
                bootbox.prompt({
                    title: msg
                    , value: Util.goodValue(initValue)
                    , callback: callback
                });
            }

            /**
             * @ngdoc method
             * @name hideAll
             * @methodOf services:Dialog.BootboxService
             *
             * @description
             * Hide opened bootbox dialogs
             * @example
<example>
<file name="app.js">
angular.module('ngAppDemo', []).controller('ngAppDemoController', ['Dialog.BootboxService', '$log', function(dlg, $log) {
    dlg.hideAll();
}]);
</file>
</example>
             */
            , hideAll: function () {
                bootbox.hideAll();
            }


        };

        return Service;
    }
]);