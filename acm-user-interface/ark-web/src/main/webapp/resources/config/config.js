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
 * Process properties like 'property[0]' to add item into array
 * @param obj
 */
var processMergedProperties = function(obj) {
    _.forIn(obj, function (val, key) {

        // If property contains index like prop[3] then put this property into arary 'prop'
        // If 'prop' array is absent then crete this array

        if (key.match(/\[\s*[0-9]+\s*\]$/)) {
            var index = key.match(/\[\s*([0-9])+\s*\]$/)[1];

            var arrPropertyName = key.replace(/\[\s*[0-9]+\s*\]$/, '');
            if (_.isArray(obj[arrPropertyName])) {
                if (_.isObject(obj[arrPropertyName][index]) && _.isObject(val)) {
                    _.merge(obj[arrPropertyName][index], val)
                } else {
                    obj[arrPropertyName][index] = val;
                }
                delete obj[key];
            }
            key = arrPropertyName;
            val = obj[arrPropertyName][index];
        }

        if (_.isArray(val)) {
            val.forEach(function(el) {
                if (_.isObject(el)) {
                    processMergedProperties(el);
                }
            });
        }
        if (_.isObject(val)) {
            processMergedProperties(obj[key]);
        }
    });
};


/**
 * Recursively merges 2 configuration files
 * @param src
 * @param obj
 */
module.exports.mergeConfigFiles = function(src, obj) {
    _.merge(src, obj);
    processMergedProperties(src);
};

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