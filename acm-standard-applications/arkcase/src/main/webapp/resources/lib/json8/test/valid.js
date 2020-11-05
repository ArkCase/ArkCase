"use strict";

var _typeof = typeof Symbol === "function" && typeof Symbol.iterator === "symbol" ? function (obj) { return typeof obj; } : function (obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; };

var assert = require("assert");

var _require = require(".."),
    valid = _require.valid;

/*eslint-disable object-shorthand*/


var VALID = {
    true: true,
    false: false,
    null: null,
    '"foo"': "foo",
    "{}": {},
    "[]": [],
    "42": 42,
    "-0": -0,
    "-42": -42,
    map: new Map(),
    set: new Set()
};

var INVALID = {
    Infinity: Infinity,
    "-Infinity": -Infinity,
    function: function _function() {},
    undefined: undefined,
    NaN: NaN
};
/*eslint-enable object-shorthand*/

var forEach = function forEach(obj, fn) {
    for (var i in obj) {
        fn(i, obj[i]);
    }
};

if (global.Symbol && _typeof(Symbol()) === "symbol") INVALID.symbol = Symbol();

describe("valid", function () {
    forEach(VALID, function (k, v) {
        it("returns true for " + k, function () {
            assert.strictEqual(valid(v), true);
        });
    });

    forEach(INVALID, function (k, v) {
        it("returns false for " + k, function () {
            assert.strictEqual(valid(v), false);
        });
    });

    describe("map", function () {
        it("return false for non string keys", function () {
            var map = new Map();
            map.set(null, "hello");
            assert.strictEqual(valid(map), false);
        });
    });
});