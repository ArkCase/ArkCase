'use strict';

/**
 * Module dependencies.
 */
var _ = require('lodash'),
    glob = require('glob');

/**
 * Load app configurations
 */
module.exports = _.extend(
    require('./env/all')
);

/**
 * Get files by glob patterns
 */
module.exports.getGlobbedFiles = function (globPatterns, removeRoot) {
    // For context switching
    var _this = this;

    // URL paths regex
    var urlRegex = new RegExp('^(?:[a-z]+:)?\/\/', 'i');

    // The output array
    var output = [];

    // If glob pattern is array so we use each pattern in a recursive way, otherwise we use glob
    if (_.isArray(globPatterns)) {
        globPatterns.forEach(function (globPattern) {
            output = _.union(output, _this.getGlobbedFiles(globPattern, removeRoot));
        });
    } else if (_.isString(globPatterns)) {
        if (urlRegex.test(globPatterns)) {
            output.push(globPatterns);
        } else {
            var files = glob.sync(globPatterns);
            if (removeRoot) {
                files = files.map(function (file) {
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
module.exports.getJavaScriptAssets = function () {
    var output = this.getGlobbedFiles(this.assets.lib.js.concat(this.assets.js, this.assets.lib.customJs), '');
    return output;
};

module.exports.getModulesJavaScriptAssets = function(){
    var output = [];

    var jsModules = this.getGlobbedFiles(this.assets.jsModules, 'modules/');
    var jsCustomModules = this.getGlobbedFiles(this.assets.jsCustomModules, 'custom_modules/');

    var jsDirectives = this.getGlobbedFiles(this.assets.jsDirectives, 'directives/');
    var jsCustomDirectives = this.getGlobbedFiles(this.assets.jsCustomDirectives, 'custom_directives/');

    var jsServices = this.getGlobbedFiles(this.assets.jsServices, 'services/');
    var jsCustomServices = this.getGlobbedFiles(this.assets.jsCustomServices, 'custom_services/');

    //  Remove duplicated JS files from modules
    jsModules = _.difference(jsModules, jsCustomModules);
    jsDirectives = _.difference(jsDirectives, jsCustomDirectives);
    jsServices = _.difference(jsServices, jsCustomServices);

    _.forEach(jsModules, function (item, index, arr) {
        item = 'modules/' + item;
        arr[index] = item;
    });

    _.forEach(jsCustomModules, function (item, index, arr) {
        item = 'custom_modules/' + item;
        arr[index] = item;
    });

    _.forEach(jsDirectives, function (item, index, arr) {
        item = 'directives/' + item;
        arr[index] = item;
    });

    _.forEach(jsCustomDirectives, function (item, index, arr) {
        item = 'custom_directives/' + item;
        arr[index] = item;
    });

    _.forEach(jsServices, function (item, index, arr) {
        item = 'services/' + item;
        arr[index] = item;
    });

    _.forEach(jsCustomServices, function (item, index, arr) {
        item = 'custom_services/' + item;
        arr[index] = item;
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
module.exports.getCSSAssets = function () {
    return this.getGlobbedFiles(this.assets.lib.css.concat(this.assets.css), '');
};
