'use strict';

// Init the application configuration module for AngularJS application
var ApplicationConfiguration = (function () {
    // Init module configuration options
    var applicationModuleName = 'ArkCase';
    var applicationModuleVendorDependencies = [
        'cgNotify',
        'ngResource',
        'ngCookies',
        'ngAnimate',
        'ngTouch',
        'ngSanitize',
        'ngAnimate',
        'ngFileUpload',
        'ui.router',
        'ui.bootstrap',
        'pascalprecht.translate',
        'angularMoment',
        //'ui.utils',
        'ui.grid',
        'ui.grid.pagination',
        'ui.grid.resizeColumns',
        'ui.grid.autoResize',
        'ui.grid.selection',
        'ui.grid.expandable',
        'ui.grid.edit',
        'ui.grid.exporter',
        'ui.grid.moveColumns',
        'xeditable',
        'summernote',
        'ngBootbox',
        'ngHandsontable',
        'ngTagsInput',
        'ui.ace',
        'cfp.hotkeys',
        'angular-google-analytics',
        "com.2fdevs.videogular",
        "com.2fdevs.videogular.plugins.controls",
        "com.2fdevs.videogular.plugins.overlayplay",
        "com.2fdevs.videogular.plugins.poster",
        "com.2fdevs.videogular.plugins.buffering",
        "tmh.dynamicLocale",
        "ab-base64",
        "angular-cache"
    ];

    // Init list of errors that should not be shown to the end user
    var suppressedErrorList = [
        {"url": "http://api.openweathermap.org/data/2.5/weather", "status": 404}
    ];


    // Add a new vertical module
    var registerModule = function (moduleName, dependencies) {
        // Create angular module
        angular.module(moduleName, dependencies || []);

        // Add the module to the AngularJS configuration file
        angular.module(applicationModuleName).requires.push(moduleName);
    };

    return {
        applicationModuleName: applicationModuleName,
        applicationModuleVendorDependencies: applicationModuleVendorDependencies,
        registerModule: registerModule,
        suppressedErrorList: suppressedErrorList
    };
})();
