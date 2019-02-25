"use strict";

var assert = require("assert");

var _require = require(".."),
    hasKey = _require.hasKey;

var has = function has(obj, key) {
    assert.deepEqual(hasKey(obj, key), true);
};

var lacks = function lacks(obj, key) {
    assert.deepEqual(hasKey(obj, key), false);
};

describe("hasKey", function () {
    it("returns false if the obj is not an object", function () {
        lacks(null, "foo");
    });

    describe("object", function () {
        it("returns false if key is undefined", function () {
            lacks({ undefined: "foo" }, undefined);
        });

        it("returns true if the key is present", function () {
            has({ foo: "bar" }, "foo");
        });

        it("returns false if the key value is undefined", function () {
            lacks({ foo: undefined }, "foo");
        });

        it("returns false if the key lives in the prototype chain", function () {
            lacks(Object.create({ foo: "bar" }), "foo");
        });

        it("returns false if the key is not a string", function () {
            lacks({ false: "foo" }, false);
        });
    });

    describe("Map", function () {
        var obj = void 0;
        beforeEach(function () {
            obj = new Map();
        });

        it("returns false if key is undefined", function () {
            obj.set(undefined, "foo");
            lacks(obj, undefined);
        });

        it("returns true if the key is present", function () {
            obj.set("foo", "bar");
            has(obj, "foo");
        });

        it("returns false if the key is undefined", function () {
            obj.set("foo", undefined);
            lacks(obj, "foo");
        });

        it("returns false if the key is not a string", function () {
            obj.set({}, true);
            lacks(obj, {});
        });
    });
});