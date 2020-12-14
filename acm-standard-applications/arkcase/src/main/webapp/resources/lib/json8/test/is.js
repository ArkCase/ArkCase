"use strict";

var _typeof = typeof Symbol === "function" && typeof Symbol.iterator === "symbol" ? function (obj) { return typeof obj; } : function (obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; };

var assert = require("assert");

var _require = require(".."),
    is = _require.is,
    isObject = _require.isObject,
    isPrimitive = _require.isPrimitive,
    isNull = _require.isNull,
    isJSON = _require.isJSON,
    isArray = _require.isArray,
    isStructure = _require.isStructure,
    isNumber = _require.isNumber,
    isString = _require.isString,
    isBoolean = _require.isBoolean;

var primitives = {
    null: null,
    true: true,
    false: false,
    "'foo'": "string",
    "positive integer": 42,
    "negative integer": -42,
    "positive float": 4.2,
    "negative float": -4.2
};

var numbers = {
    "positive integer": 42,
    "negative integer": -42,
    "positive float": 4.2,
    "negative float": -4.2
};

var structures = {
    "[]": [],
    "{}": {},
    map: new Map(),
    set: new Set()
};

var VALID = {};
for (var p in primitives) {
    VALID[p] = primitives[p];
}for (var s in structures) {
    VALID[s] = structures[s];
} /*eslint-disable object-shorthand*/
var INVALID = {
    Infinity: Infinity,
    "-Infinity": -Infinity,
    NaN: NaN,
    undefined: undefined,
    function: function _function() {}
};
/*eslint-enable object-shorthand*/

var ALL = {};
for (var valid in VALID) {
    ALL[valid] = VALID[valid];
}for (var invalid in INVALID) {
    ALL[invalid] = INVALID[invalid];
}if (global.Symbol && _typeof(Symbol()) === "symbol") INVALID.symbol = Symbol();

var forEach = function forEach(obj, fn) {
    for (var i in obj) {
        fn(i, obj[i]);
    }
};

var forEachBut = function forEachBut(obj, but, fn) {
    if (isObject(but)) but = Object.keys(but);else but = isArray(but) ? but : [but];

    forEach(obj, function (k, v) {
        if (but.indexOf(k) === -1) fn(k, v);
    });
};

var validBut = function validBut(but, fn) {
    forEachBut(VALID, but, fn);
};

var invalidBut = function invalidBut(but, fn) {
    forEachBut(INVALID, but, fn);
};

var allBut = function allBut(but, fn) {
    validBut(but, fn);
    invalidBut(but, fn);
};

describe("is", function () {
    it("throws an error if the type is unknown", function () {
        assert.throws(function () {
            is("foo", "bar");
        }, Error);
    });

    describe("JSON", function () {
        forEach(VALID, function (k, v) {
            it("returns true for " + k, function () {
                assert.strictEqual(is(v, "JSON"), true);
                assert.strictEqual(isJSON(v), true);
            });
        });

        forEach(INVALID, function (k, v) {
            it("returns false for " + k, function () {
                assert.strictEqual(is(v, "JSON"), false);
                assert.strictEqual(isJSON(v), false);
            });
        });
    });

    describe("structure", function () {
        forEach(structures, function (k, v) {
            it("returns true for " + k, function () {
                assert.strictEqual(is(v, "structure"), true);
                assert.strictEqual(isStructure(v), true);
            });
        });

        allBut(structures, function (k, v) {
            it("returns false for " + k, function () {
                assert.strictEqual(is(v, "structure"), false);
                assert.strictEqual(isStructure(v), false);
            });
        });
    });

    describe("object", function () {
        it("returns true for {}", function () {
            assert.strictEqual(is({}, "object"), true);
            assert.strictEqual(isObject({}), true);
        });

        it("returns true for map", function () {
            assert.strictEqual(is(new Map(), "object"), true);
            assert.strictEqual(isObject(new Map()), true);
        });

        allBut(["{}", "map"], function (k, v) {
            it("returns false for " + k, function () {
                assert.strictEqual(is(v, "object"), false);
                assert.strictEqual(isObject(v), false);
            });
        });
    });

    describe("array", function () {
        it("returns true for []", function () {
            assert.strictEqual(is([], "array"), true);
            assert.strictEqual(isArray([]), true);
        });

        it("returns true for set", function () {
            assert.strictEqual(is(new Set(), "array"), true);
            assert.strictEqual(isArray(new Set()), true);
        });

        allBut(["[]", "set"], function (k, v) {
            it("returns false for " + k, function () {
                assert.strictEqual(isArray(v), false);
            });
        });
    });

    describe("primitive", function () {
        forEach(primitives, function (k, v) {
            it("returns true for " + k, function () {
                assert.strictEqual(is(v, "primitive"), true);
                assert.strictEqual(isPrimitive(v), true);
            });
        });

        allBut(primitives, function (k, v) {
            it("returns false for " + k, function () {
                assert.strictEqual(is(v, "primitive"), false);
                assert.strictEqual(isPrimitive(v), false);
            });
        });
    });

    describe("number", function () {
        forEach(numbers, function (k, v) {
            it("returns true for " + k, function () {
                assert.strictEqual(is(v, "number"), true);
                assert.strictEqual(isNumber(v), true);
            });
        });

        allBut(numbers, function (k, v) {
            it("returns false for " + k, function () {
                assert.strictEqual(is(v, "number"), false);
                assert.strictEqual(isNumber(v), false);
            });
        });
    });

    describe("string", function () {
        it("returns true for 'foo'", function () {
            assert.strictEqual(is("foo", "string"), true);
            assert.strictEqual(isString("foo"), true);
        });

        allBut("'foo'", function (k, v) {
            it("returns false for " + k, function () {
                assert.strictEqual(is(v, "string"), false);
                assert.strictEqual(isString(v), false);
            });
        });
    });

    describe("boolean", function () {
        it("returns true for true", function () {
            assert.strictEqual(is(true, "boolean"), true);
            assert.strictEqual(isBoolean(true), true);
        });

        it("returns true for false", function () {
            assert.strictEqual(is(false, "boolean"), true);
            assert.strictEqual(isBoolean(false), true);
        });

        allBut(["true", "false"], function (k, v) {
            it("returns false for " + k, function () {
                assert.strictEqual(is(v, "boolean"), false);
                assert.strictEqual(isBoolean(v), false);
            });
        });
    });

    describe("null", function () {
        it("returns true for null", function () {
            assert.strictEqual(is(null, "null"), true);
            assert.strictEqual(isNull(null), true);
        });

        allBut("null", function (k, v) {
            it("returns false for " + k, function () {
                assert.strictEqual(is(v, "null"), false);
                assert.strictEqual(isNull(v), false);
            });
        });
    });
});