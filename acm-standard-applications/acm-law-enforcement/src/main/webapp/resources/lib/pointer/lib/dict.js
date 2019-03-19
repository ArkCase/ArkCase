"use strict";

var _typeof = typeof Symbol === "function" && typeof Symbol.iterator === "symbol" ? function (obj) { return typeof obj; } : function (obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; };

var walk = require("./walk");

module.exports = function index(json) {
    var dict = Object.create(null);
    walk(json, function (value, pointer) {
        if ((typeof value === "undefined" ? "undefined" : _typeof(value)) !== "object" || value === null) {
            dict[pointer] = value;
        }
    });
    return dict;
};