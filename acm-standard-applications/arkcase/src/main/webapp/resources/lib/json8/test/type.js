"use strict";

var _typeof = typeof Symbol === "function" && typeof Symbol.iterator === "symbol" ? function (obj) { return typeof obj; } : function (obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; };

var assert = require("assert");

var _require = require(".."),
    type = _require.type;

describe("type", function () {
    it("returns boolean for false", function () {
        assert.strictEqual(type(false), "boolean");
    });

    it("returns boolean for true", function () {
        assert.strictEqual(type(true), "boolean");
    });

    it("returns array for array", function () {
        assert.strictEqual(type([]), "array");
    });

    it("returns array for set", function () {
        assert.strictEqual(type(new Set()), "array");
    });

    it("returns null for null", function () {
        assert.strictEqual(type(null), "null");
    });

    it("returns object for object", function () {
        assert.strictEqual(type({}), "object");
    });

    it("returns object for map", function () {
        assert.strictEqual(type(new Map()), "object");
    });

    it("returns number for integer", function () {
        assert.strictEqual(type(1234), "number");
    });

    it("returns number for negative integer", function () {
        assert.strictEqual(type(-1234), "number");
    });

    it("returns number for float", function () {
        assert.strictEqual(type(12.34), "number");
    });

    it("returns number for negative float", function () {
        assert.strictEqual(type(-12.34), "number");
    });

    it("returns undefined for undefined", function () {
        assert.strictEqual(type(undefined), undefined);
    });

    it("returns undefined for NaN", function () {
        assert.strictEqual(type(NaN), undefined);
    });

    it("returns undefined for Infinity", function () {
        assert.strictEqual(type(Infinity), undefined);
    });

    it("returns undefined for -Infinity", function () {
        assert.strictEqual(type(-Infinity), undefined);
    });

    it("returns undefined for function", function () {
        assert.strictEqual(type(function () {}), undefined);
    });

    if (global.Symbol && _typeof(Symbol()) === "symbol") {
        it("returns undefined for sybmol", function () {
            assert.strictEqual(type(Symbol()), undefined);
        });
    }
});