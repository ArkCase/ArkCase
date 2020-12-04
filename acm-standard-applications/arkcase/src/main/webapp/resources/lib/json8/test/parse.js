"use strict";

var _typeof = typeof Symbol === "function" && typeof Symbol.iterator === "symbol" ? function (obj) { return typeof obj; } : function (obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; };

var assert = require("assert");

var _require = require(".."),
    parse = _require.parse,
    OBJECT = _require.OBJECT;

var valid = [["true", true], ["false", false], ["null", null], ['"foo"', "foo"], ["{}", {}], ["[]", []], ["42", 42], ["-0", -0], ["-42", -42]];

var invalid = [["Infinity", Infinity], ["-Infinity", -Infinity], ["function () {}", function () {}], ["undefined", undefined], ["NaN", NaN]];

if (global.Symbol && _typeof(Symbol()) === "symbol") invalid.push(["symbol", Symbol()]);

var forEach = function forEach(obj, fn) {
    obj.forEach(function (item) {
        fn(item[0], item[1]);
    });
};

describe("parse", function () {
    forEach(valid, function (k, v) {
        it("returns " + k + " for " + k, function () {
            var parsed = parse(k);
            if (k === "{}") assert.equal(typeof parsed === "undefined" ? "undefined" : _typeof(parsed), "object");else if (k === "[]") assert(Array.isArray(parsed));else assert.strictEqual(parsed, v);
        });
    });

    forEach(invalid, function (k) {
        it("throws a SyntaxError for " + k, function () {
            assert.throws(function () {
                parse(k);
            }, SyntaxError);
        });
    });

    it("parse objects as Maps if enabled", function () {
        var str = '{"foo":"bar"}';
        var parsed = parse(str, { map: true });
        assert(parsed instanceof Map);
        assert.equal(parsed.get("foo"), "bar");
    });

    it("doesn't parse objects as Maps if not enabled", function () {
        var str = '{"foo":"bar"}';
        var parsed = parse(str);
        assert.equal(typeof parsed === "undefined" ? "undefined" : _typeof(parsed), OBJECT);
        assert.equal(parsed.foo, "bar");
    });

    it("doesn't parse objects as Maps if disabled", function () {
        var str = '{"foo":"bar"}';
        var parsed = parse(str, { map: false });
        assert.equal(typeof parsed === "undefined" ? "undefined" : _typeof(parsed), OBJECT);
        assert.equal(parsed.foo, "bar");
    });

    it("parse arrays as Sets if enabled", function () {
        var str = '["foo"]';
        var parsed = parse(str, { set: true });
        assert(parsed instanceof Set);
        assert.equal(parsed.has("foo"), true);
    });

    it("doesn't parse arrays as Sets if not enabled", function () {
        var str = '["foo"]';
        var parsed = parse(str);
        assert(Array.isArray(parsed));
        assert.equal(parsed.indexOf("foo"), 0);
    });

    it("doesn't parse arrays as Sets if disabled", function () {
        var str = '["foo"]';
        var parsed = parse(str, { set: false });
        assert(Array.isArray(parsed));
        assert.equal(parsed.indexOf("foo"), 0);
    });
});