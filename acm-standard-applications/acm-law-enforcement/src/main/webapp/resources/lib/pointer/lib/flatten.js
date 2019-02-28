"use strict";

var _typeof = typeof Symbol === "function" && typeof Symbol.iterator === "symbol" ? function (obj) { return typeof obj; } : function (obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; };

var walk = require("./walk");

module.exports = function index(json) {
    var idxs = Object.create(null);
    walk(json, function (value, pointer) {
        var v = void 0;
        if (Array.isArray(value)) {
            v = [];
        } else if (global.Map && value instanceof Map) {
            v = new Map();
        } else if (global.Set && value instanceof Set) {
            v = new Set();
        } else if ((typeof value === "undefined" ? "undefined" : _typeof(value)) === "object" && value !== null) {
            v = {};
        } else {
            v = value;
        }
        idxs[pointer] = v;
    });
    return idxs;
};