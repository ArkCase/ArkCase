"use strict";

var assert = require("assert");

var _require = require(".."),
    clone = _require.clone,
    equal = _require.equal;

describe("clone", function () {
    var doc = null;
    var cl = null;

    it("returns an array clone copy", function () {
        doc = ["foo", "bar"];
        cl = clone(doc);
        assert.deepEqual(cl, doc);
        assert.notStrictEqual(cl, doc);
        assert(equal(doc, cl));
    });

    it("returns a set clone copy", function () {
        doc = new Set(["foo", "bar"]);
        cl = clone(doc);
        assert(equal(doc, cl));
        assert.notStrictEqual(cl, doc);
    });

    it("returns an object clone copy", function () {
        doc = { foo: "bar", bar: "foo" };
        cl = clone(doc);
        assert.deepEqual(cl, doc);
        assert.notStrictEqual(cl, doc);
        assert(equal(doc, cl));
    });

    it("returns a map clone copy", function () {
        doc = { foo: "bar", bar: "foo" };
        cl = clone(doc);
        assert(equal(doc, cl));
        assert.notStrictEqual(cl, doc);
    });

    it("returns false for false", function () {
        assert.strictEqual(clone(false), false);
    });

    it("returns true for true", function () {
        assert.strictEqual(clone(true), true);
    });

    it("returns the string for string", function () {
        var str = "foobar";
        assert.strictEqual(clone(str), str);
    });

    it("returns the number for number", function () {
        assert.strictEqual(clone(10), 10);
    });

    it("returns null for null", function () {
        assert.strictEqual(clone(null), null);
    });

    it("returns -0 for -0", function () {
        cl = clone(-0);
        assert(cl === 0);
        assert(1 / cl === -Infinity);
    });
});