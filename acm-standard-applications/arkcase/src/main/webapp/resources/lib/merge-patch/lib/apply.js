"use strict";

var _typeof = typeof Symbol === "function" && typeof Symbol.iterator === "symbol" ? function (obj) { return typeof obj; } : function (obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; };

var OBJECT = "object";

/**
 * apply a JSON merge patch
 * https://tools.ietf.org/html/rfc7396
 * @param  {Object} doc    - JSON object document
 * @param  {Object} patch  - JSON object patch
 * @return {Object}        - JSON object document
 */
module.exports = function apply(doc, patch) {
    if ((typeof patch === "undefined" ? "undefined" : _typeof(patch)) !== OBJECT || patch === null || Array.isArray(patch)) {
        return patch;
    }

    if ((typeof doc === "undefined" ? "undefined" : _typeof(doc)) !== OBJECT || doc === null || Array.isArray(doc)) doc = {};

    for (var k in patch) {
        var v = patch[k];
        if (v === null) {
            delete doc[k];
            continue;
        }
        doc[k] = apply(doc[k], v);
    }

    return doc;
};