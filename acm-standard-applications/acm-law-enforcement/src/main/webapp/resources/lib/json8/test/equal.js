"use strict";

var assert = require("assert");
var JSON8 = require("..");

var equal = function equal(a, b) {
    assert.strictEqual(JSON8.equal(a, b), true);
};

var differ = function differ(a, b) {
    assert.strictEqual(JSON8.equal(a, b), false);
};

describe("equal", function () {
    describe("array", function () {
        it("returns true for identical", function () {
            equal([1, 2], [1, 2]);
            equal([], []);
            var array = [];
            equal(array, array);
        });

        it("returns false for different", function () {
            differ([1, 2], [2, 1]);
            differ([1, 2], [1, 2, 3]);
            differ([1, 2, 3], [1, 2]);
        });
    });

    describe("set", function () {
        it("returns true for identical", function () {
            equal(new Set(), new Set());
            equal(new Set([1]), new Set([1]));
            var set = new Set();
            equal(set, set);
        });

        it("returns false for different", function () {
            differ(new Set([1]), new Set([2]));
            differ(new Set([1, 2]), new Set([1]));
            differ(new Set([1]), new Set([1, 2]));
        });
    });

    describe("array and set", function () {
        it("returns true for identical", function () {
            equal(new Set([1, 2]), [1, 2]);
            equal([1, 2], new Set([1, 2]));
        });

        it("returns false for different", function () {
            differ([1, 2], new Set([2, 1]));
            differ(new Set([1, 2]), [2, 1]);
        });
    });

    describe("object", function () {
        it("returns true for identical", function () {
            equal({ foo: "bar" }, { foo: "bar" });
            equal({ foo: "bar", bar: "foo" }, { foo: "bar", bar: "foo" });
            equal({}, {});
            var obj = {};
            equal(obj, obj);
        });

        it("returns false for different", function () {
            differ({ foo: "bar" }, {});
            differ({ foo: "bar" }, { bar: "foo" });
        });
    });

    describe("map", function () {
        it("returns true for identical", function () {
            equal(new Map(), new Map());
            var a = new Map();
            a.set("foo", "bar");
            a.set("bar", "foo");
            var b = new Map();
            b.set("bar", "foo");
            b.set("foo", "bar");
            equal(a, b);
            var map = new Map();
            equal(map, map);
        });

        it("returns false for different", function () {
            var a = new Map();
            a.set("foo", "bar");
            var b = new Map();
            b.set("bar", "foo");
            differ(a, b);
        });
    });

    describe("object and map", function () {
        it("returns true for identical", function () {
            equal({}, new Map());
            var a = { foo: "bar", bar: "foo" };
            var b = new Map();
            b.set("bar", "foo");
            b.set("foo", "bar");
            equal(a, b);
            var map = new Map();
            equal({}, map);
        });

        it("returns false for different", function () {
            var a = { foo: "bar" };
            var b = new Map();
            b.set("bar", "foo");
            differ(a, b);
        });
    });

    describe("boolean", function () {
        it("returns true for identical", function () {
            equal(true, true);
            equal(false, false);
        });

        it("returns false for different", function () {
            differ(true, false);
            differ(false, true);
        });
    });

    describe("number", function () {
        it("returns true for identical", function () {
            equal(42, 42);
        });

        it("returns false for different", function () {
            differ(42, 43);
        });

        describe("0", function () {
            it("returns true for identical", function () {
                equal(0, 0);
                equal(-0, -0);
                equal(+0, +0);
                equal(+0, 0);
            });

            it("returns false for different", function () {
                differ(-0, 0);
                differ(-0, +0);
            });
        });
        // TODO: figure out what to do with those
        // it('returns false for NaN', () => {
        //   differ(NaN, NaN)
        // })

        // it('returns false for undefined', () => {
        //   differ(undefined, undefined)
        // })

        // it('returns false for Infinity', () => {
        //   differ(Infinity, Infinity)
        // })

        // it('returns false for -Infinity', () => {
        //   differ(-Infinity, -Infinity)
        // })
    });

    describe("string", function () {
        it("returns true for identical", function () {
            equal("foo", "foo");
        });

        it("returns false for different", function () {
            differ("foo", "bar");
        });
    });

    describe("null", function () {
        it("returns true for identical", function () {
            equal(null, null);
        });

        it("returns false for different", function () {
            differ(null, "bar");
        });
    });
});