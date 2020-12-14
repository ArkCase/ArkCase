"use strict";

var assert = require("assert");

var _require = require(".."),
    hasValue = _require.hasValue;

var has = function has(obj, value) {
    assert.deepEqual(hasValue(obj, value), true);
};

var lacks = function lacks(obj, value) {
    assert.deepEqual(hasValue(obj, value), false);
};

describe("hasValue", function () {
    it("returns false if the obj is not an object", function () {
        lacks(null, "foo");
    });

    describe("object", function () {
        it("returns false if value is undefined", function () {
            lacks({ foo: undefined }, undefined);
        });

        it("returns true if the value is present", function () {
            has({ foo: "bar" }, "bar");
        });

        it("returns true if the key is undefined", function () {
            has({ undefined: "foo" }, "foo");
        });

        it("returns false if the value lives in the prototype chain", function () {
            lacks(Object.create({ foo: "bar" }), "bar");
        });

        it("returns true if the value is present and is an object", function () {
            has({ foo: {} }, {});
            lacks({}, { foo: "bar" });
        });
    });

    describe("Map", function () {
        var obj = void 0;
        beforeEach(function () {
            obj = new Map();
        });

        it("returns false if value is undefined", function () {
            obj.set("foo", undefined);
            lacks(obj, undefined);
        });

        it("returns true if the key is present", function () {
            obj.set("foo", "bar");
            has(obj, "bar");
        });

        it("returns false if the key is undefined", function () {
            obj.set(undefined, "foo");
            lacks(obj, "foo");
        });

        it("returns true if the value is present and is an object", function () {
            obj.set({}, {});
            has(obj, {});
            lacks(obj, { foo: "bar" });
        });
    });
});