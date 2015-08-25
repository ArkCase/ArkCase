'use strict';
var appRoot = require('app-root-path');
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
                'lib/bootstrap/dist/css/bootstrap.css',
                'lib/bootstrap/dist/css/bootstrap-theme.css',
                'lib/angular-ui-grid/ui-grid.css',
                'lib/font-awesome/css/font-awesome.css',
                'lib/fancytree/dist/skin-win8/ui.fancytree.css',
                'lib/angular-xeditable/dist/css/xeditable.css',
                'lib/summernote/dist/summernote.css'
            ],
            js: [
                'lib/jquery/dist/jquery.js',
                'lib/jquery-ui/jquery-ui.js',
                'lib/bootstrap/dist/js/bootstrap.js',
                'lib/tv4/tv4.js',
                'lib/objectpath/lib/ObjectPath.js',
                'lib/lodash/lodash.js',
                'lib/angular/angular.js',
                'lib/angular-resource/angular-resource.js',
                'lib/angular-cookies/angular-cookies.js',
                'lib/angular-animate/angular-animate.js',
                'lib/angular-touch/angular-touch.js',
                'lib/angular-sanitize/angular-sanitize.js',
                'lib/angular-ui-router/release/angular-ui-router.js',
                'lib/angular-bootstrap/ui-bootstrap-tpls.js',
                'lib/angular-ui-grid/ui-grid.js',
                'lib/angular-schema-form/dist/schema-form.js',
                'lib/angular-schema-form/dist/bootstrap-decorator.js',
                'lib/angular-translate/angular-translate.js',
                'lib/angular-translate-loader-partial/angular-translate-loader-partial.js',
                'lib/fancytree/dist/jquery.fancytree.js',
                'lib/angular-xeditable/dist/js/xeditable.js',
                'lib/summernote/dist/summernote.js',
                'lib/angular-summernote/dist/angular-summernote.js'
            ]
        },
        css: [
            'assets/css/application.css',
            'modules/**/css/*.css',
            'custom_modules/**/css/*.css'
        ],
        scss: [
            'modules/**/scss/*.scss',
            'custom_modules/**/scss/*.scss'
        ],
        js: [
            'config.js',
            'application.js',
            'scripts/*/*.js',
            'scripts/*/**/*.js',
            'services/*.js',
            'services/*/*.js',
            'directives/*.js',
            'directives/*/*.js'
        ],
        jsModules: [
            'modules/*/*.js',
            'modules/*/**/*.js'
        ],
        jsCustomModules: [
            'custom_modules/*/*.js',
            'custom_modules/*/**/*.js'
        ]
    }
}