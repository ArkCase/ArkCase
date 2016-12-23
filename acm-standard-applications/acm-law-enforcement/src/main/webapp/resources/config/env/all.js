'use strict';
var path = require('path');

module.exports = {

    appPath: '/arkcase/',

    config: {
        modulesConfigFolder: 'modules_config/config/',
        modulesConfigFile: 'modules_config/config/modules.json',
        modulesPermissionsFolder: 'modules_config/permission/',
        modulesSchemasFolder: 'modules_config/schemas/'
    },

    modules: {
        defaultModulesFolder: 'modules/',
        customModulesFolder: 'custom_modules/'
    },

    homePage: {
        title: "ArkCase Application",
        template: 'templates/home.tpl.html',
        target: 'home.html'
    },

    assets: {
        lib: {
            css: [
                'lib/jquery-ui/themes/ui-lightness/jquery-ui.css',
                'lib/bootstrap/dist/css/bootstrap.css',
                'lib/bootstrap/dist/css/bootstrap-theme.css',
                'lib/angular-ui-grid/ui-grid.css',
                'lib/components-font-awesome/css/font-awesome.css',
                'lib/fancytree/dist/skin-win8/ui.fancytree.css',
                'lib/angular-xeditable/dist/css/xeditable.css',
                'lib/angular-dashboard-framework-armedia/dist/angular-dashboard-framework.min.css',
                'lib/summernote/dist/summernote.css',
                'lib/angular-notify/dist/angular-notify.css',
                'lib/handsontable/dist/handsontable.full.css',
                'lib/ng-tags-input/ng-tags-input.css',
                'lib/fullcalendar/dist/fullcalendar.css',
                'lib/angular-chart.js/dist/angular-chart.css'
            ],
            customJs: [
                'https://project.armedia.com/jira/s/272b7e5d0b48558abb6f76f2cc38fb4c-T/en_US-f0xdna/6346/2/1.4.16/_/download/batch/com.atlassian.jira.collector.plugin.jira-issue-collector-plugin:issuecollector/com.atlassian.jira.collector.plugin.jira-issue-collector-plugin:issuecollector.js?locale=en-US&collectorId=2b76dcde'
            ],
            js: [
                'lib/jquery/dist/jquery.min.js',
                'lib/jquery-ui/jquery-ui.min.js',
                'lib/bootstrap/dist/js/bootstrap.min.js',
                'lib/lodash/lodash.min.js',
                'lib/moment/min/moment.min.js',
                'lib/multi-download/browser.js',
                'lib/angular/angular.min.js',
                'lib/angular-moment/angular-moment.min.js',
                'lib/angular-resource/angular-resource.min.js',
                'lib/angular-cookies/angular-cookies.min.js',
                'lib/angular-animate/angular-animate.min.js',
                'lib/angular-touch/angular-touch.min.js',
                'lib/angular-sanitize/angular-sanitize.min.js',
                'lib/angular-ui-router/release/angular-ui-router.min.js',
                'lib/angular-bootstrap/ui-bootstrap-tpls.min.js',
                'lib/angular-ui-grid/ui-grid.min.js',
                'lib/angular-notify/dist/angular-notify.min.js',
                'lib/angular-summernote/dist/angular-summernote.min.js',
                'lib/angular-translate/angular-translate.min.js',
                'lib/angular-translate-loader-partial/angular-translate-loader-partial.min.js',
                'lib/angular-xeditable/dist/js/xeditable.min.js',
                'lib/angular-dashboard-framework-armedia/dist/angular-dashboard-framework.min.js',
                'lib/Sortable/Sortable.min.js',
                'lib/fancytree/dist/jquery.fancytree.min.js',
                'lib/fancytree/dist/src/jquery.fancytree.table.js',
                'lib/fancytree/dist/src/jquery.fancytree.gridnav.js',
                'lib/fancytree/dist/src/jquery.fancytree.edit.js',
                'lib/fancytree/dist/src/jquery.fancytree.dnd.js',
                'lib/ng-file-upload/ng-file-upload.min.js',
                'lib/ng-file-upload-shim/ng-file-upload-shim.min.js',
                'lib/ng-tags-input/ng-tags-input.min.js',
                'lib/summernote/dist/summernote.min.js',
                'lib/ui-contextmenu/jquery.ui-contextmenu.min.js',
                'lib/bootbox/bootbox.js',
                'lib/ngBootbox/ngBootbox.js', // Don't use minified version here. It is broken.
                'lib/handsontable/dist/handsontable.full.min.js',
                'lib/ngHandsontable/dist/ngHandsontable.min.js',
                'lib/ace-builds/src-min-noconflict/ace.js',
                'lib/angular-ui-ace/ui-ace.min.js',
                'lib/fullcalendar/dist/fullcalendar.min.js',
                'lib/fullcalendar/dist/gcal.js',
                'lib/angular-ui-calendar/src/calendar.js',
                'lib/Chart.js/Chart.min.js',
                'lib/angular-chart.js/dist/angular-chart.min.js',
                'lib/sockjs-client/dist/sockjs.min.js',
                'lib/stomp-websocket/lib/stomp.min.js'
            ]
        },
        css: [
            'assets/css/application.css',
            'modules/**/css/*.css',
            'custom_modules/**/css/*.css',
            'assets/css/arkcase-extension.css',
            'custom_assets/css/arkcase-extension.css'
        ],
        scss: [
            'modules/**/scss/*.scss',
            'custom_modules/**/scss/*.scss'
        ],
        js: [
            'config.js',
            'application.js',
            'scripts/*/**/*.js',
            'services/*.js',   // Includes services/services.client.module.js file only
            'directives/*.js', // Includes directives/directives.client.js file only
            'filters/*.js',
            'filters/*/*.js'
        ],
        distJs: ['assets/dist/vendors.min.js', 'assets/dist/application.min.js'],
        distCss: ['assets/dist/application.min.css'], // Can't use this, because CSS has fonts and images dependencies
        jsModules: [
            'modules/*/*.js',
            'modules/*/**/*.js'
        ],
        jsCustomModules: [
            'custom_modules/*/*.js',
            'custom_modules/*/**/*.js'
        ],
        jsDirectives: [
            'directives/*/*.js',
            'directives/*/**/*.js'
        ],
        jsCustomDirectives: [
            'custom_directives/*/*.js',
            'custom_directives/*/**/*.js'
        ],
        jsServices: [
            'services/*/*.js',
            'services/*/**/*.js'
        ],
        jsCustomServices: [
            'custom_services/*/*.js',
            'custom_services/*/**/*.js'
        ]
    }
}
;
