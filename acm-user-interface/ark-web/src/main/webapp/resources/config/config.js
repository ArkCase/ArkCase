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
//    ,require('./env/' + process.env.NODE_ENV) || {}
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
    var output = this.getGlobbedFiles(this.assets.lib.js.concat(this.assets.js), '');

    var jsModules = this.getGlobbedFiles(this.assets.jsModules, 'modules/');
    var jsCustomModules = this.getGlobbedFiles(this.assets.jsCustomModules, 'custom_modules/');

    //  Remove duplicated JS files from modules
    jsModules = _.difference(jsModules, jsCustomModules);

    _.forEach(jsModules, function (item, index, arr) {
        item = 'modules/' + item;
        arr[index] = item;
    });

    _.forEach(jsCustomModules, function (item, index, arr) {
        item = 'custom_modules/' + item;
        arr[index] = item;
    });


    output = output.concat(jsModules);
    output = output.concat(jsCustomModules);

    return output;
};

/**
 * Get the modules CSS files
 */
module.exports.getCSSAssets = function () {
    return this.getGlobbedFiles(this.assets.lib.css.concat(this.assets.css), '');
};
