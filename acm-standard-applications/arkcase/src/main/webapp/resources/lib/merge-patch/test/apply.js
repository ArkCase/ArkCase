"use strict";

var _typeof = typeof Symbol === "function" && typeof Symbol.iterator === "symbol" ? function (obj) { return typeof obj; } : function (obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; };

var assert = require("assert");
var apply = require("../lib/apply");

describe("apply", function () {
    it("returns the patch argument if it's not an object", function () {
        [true, false, null, [], 42, "foo"].forEach(function (v) {
            assert.equal(apply({}, v), v);
        });
    });

    it("returns an object if document argument is not an object", function () {
        [true, false, null, [], 42, "foo"].forEach(function (v) {
            var doc = apply(v, {});
            assert.equal(typeof doc === "undefined" ? "undefined" : _typeof(doc), "object");
            assert(doc !== null);
        });
    });

    it("deletes patch properties with value null", function () {
        var doc = { foo: "bar" };
        doc = apply(doc, { foo: null });
        assert.deepEqual(doc, {});
    });

    it("deletes nested patch properties with value null", function () {
        var doc = { foo: { bar: "foo" } };
        doc = apply(doc, { foo: { bar: null } });
        assert.deepEqual(doc, { foo: {} });
    });

    it("adds patch properties with non null value", function () {
        var doc = {};
        var patch = { foo: "bar", bar: "foo" };
        doc = apply(doc, patch);
        assert.deepEqual(doc, patch);
    });

    it("adds nested patch properties with non null value", function () {
        var doc = {};
        var patch = { foo: { bar: "foo" } };
        doc = apply(doc, patch);
        assert.deepEqual(doc, patch);
    });
});