'use strict';

/**
 * Module dependencies.
 */
var _ = require('lodash'), glob = require('glob');

/**
 * Load app configurations
 */
module.exports = _.extend(require('./env/all'));

/**
 * Load active profiles
 */
var activeProfiles = require('./../profiles');

/**
 * Get files by glob patterns
 */
module.exports.getGlobbedFiles = function(globPatterns, removeRoot) {
    // For context switching
    var _this = this;

    // URL paths regex
    var urlRegex = new RegExp('^(?:[a-z]+:)?\/\/', 'i');

    // The output array
    var output = [];

    // If glob pattern is array so we use each pattern in a recursive way, otherwise we use glob
    if (_.isArray(globPatterns)) {
        globPatterns.forEach(function(globPattern) {
            output = _.union(output, _this.getGlobbedFiles(globPattern, removeRoot));
        });
    } else if (_.isString(globPatterns)) {
        if (urlRegex.test(globPatterns)) {
            output.push(globPatterns);
        } else {
            var files = glob.sync(globPatterns);
            if (removeRoot) {
                files = files.map(function(file) {
                    return file.replace(removeRoot, '');
                });
            }
            output = _.union(output, files);
        }
    }

    return output;
};

/**
 * Get the modules JavaScript files
 */
module.exports.getJavaScriptAssets = function() {
    return this.getGlobbedFiles(this.assets.lib.js.concat(this.assets.js, this.assets.lib.customJs), '');
};

module.exports.getModulesJavaScriptAssets = function() {
    var output = [];
    var _this = this;

    var jsModules = _this.getGlobbedFiles(this.assets.jsModules, 'modules/');
    jsModules = _.map(jsModules, function(item) {
        return 'modules/' + item;
    });

    var jsDirectives = _this.getGlobbedFiles(_this.assets.jsDirectives, 'directives/');
    jsDirectives = _.map(jsDirectives, function(item) {
        return 'directives/' + item;
    });

    var jsServices = _this.getGlobbedFiles(_this.assets.jsServices, 'services/');
    jsServices = _.map(jsServices, function(item) {
        return 'services/' + item;
    });

    var jsCustomModules = [];
    var jsCustomDirectives = [];
    var jsCustomServices = [];

    _.forEach(activeProfiles, function(profile) {
        var jsProfileModuleDirs = _.map(_this.assets.jsCustomModules, function(dir) {
            return profile + dir;
        });
        var customModulesFiles = _this.getGlobbedFiles(jsProfileModuleDirs, profile + '_modules/');
        jsModules = _.difference(jsModules, customModulesFiles);

        var profileModulesFiles = _.map(customModulesFiles, function(item) {
            return profile + '_modules/' + item;
        });
        jsCustomModules.concat(profileModulesFiles);

        var jsProfileDirectiveDirs = _.map(_this.assets.jsCustomDirectives, function(dir) {
            return profile + dir;
        });
        var customDirectivesFiles = _this.getGlobbedFiles(jsProfileDirectiveDirs, profile + '_directives/');
        jsDirectives = _.difference(jsDirectives, customDirectivesFiles);

        var profileDirectivesFiles = _.map(customDirectivesFiles, function(item) {
            return profile + '_directives/' + item;
        });
        jsCustomDirectives.concat(profileDirectivesFiles);

        var jsProfileServiceDirs = _.map(_this.assets.jsCustomServices, function(dir) {
            return profile + dir;
        });
        var customServicesFiles = _this.getGlobbedFiles(jsProfileServiceDirs, profile + '_services/');
        jsServices = _.difference(jsServices, customServicesFiles);

        var profileServicesFiles = _.map(customServicesFiles, function(item) {
            return profile + '_services/' + item;
        });
        jsCustomServices.concat(profileServicesFiles);
    });

    output = output.concat(jsModules);
    output = output.concat(jsCustomModules);
    output = output.concat(jsDirectives);
    output = output.concat(jsCustomDirectives);
    output = output.concat(jsServices);
    output = output.concat(jsCustomServices);
    return output;
};

/**
 * Get the modules CSS files
 */
module.exports.getCSSAssets = function() {
    var _this = this;
    var cssResources = _this.assets.lib.css.concat(_this.assets.css);
    _.forEach(activeProfiles, function(profile) {
        var customCssResources = _.map(_this.assets.jsCustomCss, function(item) {
            return profile + item;
        });
        cssResources.concat(customCssResources);
    });
    return this.getGlobbedFiles(cssResources, '');
};
