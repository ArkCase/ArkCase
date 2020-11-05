"use strict";

var _typeof = typeof Symbol === "function" && typeof Symbol.iterator === "symbol" ? function (obj) { return typeof obj; } : function (obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; };

var assert = require("assert");

var _require = require(".."),
    serialize = _require.serialize;

var _require2 = require("fs"),
    readFileSync = _require2.readFileSync;

var valid = [["true", true], ["false", false], ["null", null], ['"foo"', "foo"], ["{}", {}], ["[]", []], ["42", 42], ["-0", -0], ["-42", -42], ["[]", new Set()], ["{}", new Map()]];

var invalid = [["Infinity", Infinity], ["-Infinity", -Infinity], ["function", function () {}], ["undefined", undefined], ["NaN", NaN]];

if (global.Symbol && _typeof(Symbol()) === "symbol") invalid.push(["symbol", Symbol()]);

var compare = {
    string: "hello",
    null: null,
    true: true,
    false: false,
    "empty string": "",
    "empty object": {},
    "empty map": new Map(),
    "empty array": [],
    "empty set": new Set()
};

var forEach = function forEach(obj, fn) {
    obj.forEach(function (item) {
        fn(item[0], item[1]);
    });
};

describe("serialize", function () {
    describe("escapes u2028 and u2029", function () {
        assert.strictEqual(serialize(readFileSync("./test/fixtures.txt", "utf8")), "\"ro\\u2028cks! ro\\u2029cks!\\n\"");
    });

    describe("compare", function () {
        var _loop = function _loop(key) {
            var v = compare[key];
            it(key, function () {
                // eslint-disable-line
                if (!(v instanceof Map) && !(v instanceof Set)) {
                    assert.strictEqual(serialize(v), JSON.stringify(v));
                    assert.strictEqual(serialize(v, { space: 2 }), JSON.stringify(v, null, 2));
                    assert.strictEqual(serialize(v, { space: "    " }), JSON.stringify(v, null, "    "));
                }
            });
        };

        for (var key in compare) {
            _loop(key);
        }
    });

    forEach(valid, function (k, v) {
        it("returns " + k + " for " + k, function () {
            assert.deepEqual(serialize(v), k);
        });
    });

    forEach(invalid, function (k, v) {
        it("throws a TypeError for " + k, function () {
            assert.throws(function () {
                serialize(v);
            }, TypeError);
        });
    });

    describe("map", function () {
        it("throws an error for non string keys", function () {
            var map = new Map();
            map.set(null, "hello");
            assert.throws(function () {
                serialize(map);
            }, TypeError);
        });
    });

    describe("toJSON option", function () {
        it("uses toJSON if options are not provided", function () {
            var obj = {};
            obj.toJSON = function () {
                return "lol";
            };
            assert.strictEqual(serialize(obj), '"lol"');
        });

        it("uses toJSON toJSON option is not provided", function () {
            var obj = {};
            obj.toJSON = function () {
                return "lol";
            };
            assert.strictEqual(serialize(obj, {}), '"lol"');
        });

        it("uses toJSON toJSON option is set to true", function () {
            var obj = {};
            obj.toJSON = function () {
                return "lol";
            };
            assert.strictEqual(serialize(obj, { toJSON: true }), '"lol"');
        });

        it("does not use and serialize toJSON if toJSON option is set to false", function () {
            var obj = {};
            obj.toJSON = function () {
                return "lol";
            };
            assert.strictEqual(serialize(obj, { toJSON: false }), "{}");
        });

        it("serializes toJSON if it is not a function", function () {
            var obj = { toJSON: true };
            var expect = '{"toJSON":true}';
            assert.strictEqual(serialize(obj), expect);
            assert.strictEqual(serialize(obj, {}), expect);
            assert.strictEqual(serialize(obj, { toJSON: false }), expect);
            assert.strictEqual(serialize(obj, { toJSON: true }), expect);
        });
    });

    describe("space option", function () {
        var arr = [1, "foo", [], {}];
        arr.toJSON = "hello";

        var obj = {
            array: arr,
            boolean: true,
            toJSON: 123,
            string: "foobar",
            "Déjà vu": "Déjà vu",
            /* eslint-disable */
            bar: {
                toJSON: function toJSON() {
                    return { foo: "bar" };
                }
            }
            /* eslint-enable */
        };

        it("serializes equally to it", function () {
            assert.strictEqual(serialize(obj), JSON.stringify(obj));
        });

        it("serializes equally to it with space param as number", function () {
            assert.strictEqual(serialize(obj, { space: 2 }), JSON.stringify(obj, null, 2));
            assert.strictEqual(serialize(obj, { space: 2 }), JSON.stringify(obj, null, "  "));
        });

        it("serializes equally to it with space param as string", function () {
            assert.strictEqual(serialize(obj, { space: "    " }), JSON.stringify(obj, null, "    "));
            assert.strictEqual(serialize(obj, { space: "    " }), JSON.stringify(obj, null, 4));
        });

        it("works equally with Object and Map", function () {
            var map = new Map();
            for (var i in obj) {
                map.set(i, obj[i]);
            }
            assert.strictEqual(serialize(obj, { space: 2 }), serialize(map, { space: 2 }));
        });
    });

    describe("replacer option", function () {
        it("is called with the object as this context", function () {
            var obj = {
                foo: "bar"
            };
            var replacer = function replacer(k, v) {
                assert.strictEqual(this, obj); // eslint-disable-line no-invalid-this
                assert.strictEqual(k, "foo");
                assert.strictEqual(v, "bar");
            };
            serialize(obj, { replacer: replacer });
        });

        it("deletes the value if the replacer return undefined for object", function () {
            var obj = {
                foo: "bar"
            };
            var replacer = function replacer() {
                return undefined;
            };
            assert.strictEqual(serialize(obj, { replacer: replacer }), "{}");
        });

        it("deletes the value if the replacer return undefined for array", function () {
            var arr = ["foo"];
            var replacer = function replacer(k, v) {
                assert.strictEqual(this, arr); // eslint-disable-line no-invalid-this
                assert.strictEqual(k, 0);
                assert.strictEqual(v, "foo");
                return undefined;
            };
            assert.strictEqual(serialize(arr, { replacer: replacer }), "[]");
        });

        it("splice the object if an item is deleted in between for object", function () {
            var obj = { foo: "bar", bar: undefined, baz: "baz" };
            var replacer = function replacer(k, v) {
                if (k === "bar") return undefined;
                return v;
            };
            assert.strictEqual(serialize(obj, { replacer: replacer }), '{"foo":"bar","baz":"baz"}');
        });

        it("splice the map if an item is deleted in between for map", function () {
            var map = new Map();
            map.set("foo", "bar");
            map.set("bar", undefined);
            map.set("baz", "baz");
            var replacer = function replacer(k, v) {
                if (k === "bar") return undefined;
                return v;
            };
            assert.strictEqual(serialize(map, { replacer: replacer }), '{"foo":"bar","baz":"baz"}');
        });

        it("splice the array if an item is deleted in between for array", function () {
            var arr = ["foo", "bar", "foo"];
            var replacer = function replacer(k, v) {
                if (k === 1) return undefined;
                return v;
            };
            assert.strictEqual(serialize(arr, { replacer: replacer }), '["foo","foo"]');
        });

        it("splice the array if an item is deleted in between for set", function () {
            var set = new Set(["foo", "bar", "baz"]);
            var replacer = function replacer(k, v) {
                if (k === "bar") return undefined;
                return v;
            };
            assert.strictEqual(serialize(set, { replacer: replacer }), '["foo","baz"]');
        });

        describe("with space option", function () {
            it("object", function () {
                var replacer = function replacer() {
                    return undefined;
                };
                var s = serialize({ foo: "bar" }, { replacer: replacer, space: 2 });
                assert.strictEqual(s, "{}");
            });

            it("array", function () {
                var replacer = function replacer() {
                    return undefined;
                };
                var s = serialize(["foo"], { replacer: replacer, space: 2 });
                assert.strictEqual(s, "[]");
            });

            it("set", function () {
                var replacer = function replacer() {
                    return undefined;
                };
                var s = serialize(new Set(["foo"]), { replacer: replacer, space: 2 });
                assert.strictEqual(s, "[]");
            });

            it("map", function () {
                var replacer = function replacer() {
                    return undefined;
                };
                var map = new Map();
                map.set("foo", "bar");
                var s = serialize(map, { replacer: replacer, space: 2 });
                assert.strictEqual(s, "{}");
            });
        });

        // https://github.com/sonnyp/JSON8/issues/18
        describe("returns undefined", function () {
            var replacer = function replacer(k, v) {
                return v;
            };

            it("produces correct JSON for object", function () {
                var s = serialize({ foo: "bar", baz: undefined }, { replacer: replacer });
                assert.strictEqual(s, '{"foo":"bar"}');
            });

            it("produces correct JSON for array", function () {
                var s = serialize(["foo", undefined], { replacer: replacer });
                assert.strictEqual(s, '["foo"]');
            });

            it("produces correct JSON for Set", function () {
                var set = new Set(["foo", undefined]);
                var s = serialize(set, { replacer: replacer });
                assert.strictEqual(s, '["foo"]');
            });

            it("produces correct JSON for Map", function () {
                var map = new Map();
                map.set("foo", "bar");
                map.set("baz", undefined);
                var s = serialize(map, { replacer: replacer });
                assert.strictEqual(s, '{"foo":"bar"}');
            });
        });

        describe("maxIndentLevel", function () {
            var obj = [{ foo: { bar: "foo" } }];

            assert.strictEqual(serialize(obj, { space: 0, maxIndentLevel: 0 }), '[{"foo":{"bar":"foo"}}]');

            assert.strictEqual(serialize(obj, { space: 0, maxIndentLevel: 1 }), '[{"foo":{"bar":"foo"}}]');

            assert.strictEqual(serialize(obj, { maxIndentLevel: 0 }), '[{"foo":{"bar":"foo"}}]');

            assert.strictEqual(serialize(obj, { maxIndentLevel: 1 }), '[{"foo":{"bar":"foo"}}]');

            assert.strictEqual(serialize(obj, { space: 2, maxIndentLevel: 0 }), '[{"foo": {"bar": "foo"}}]');

            assert.strictEqual(serialize(obj, { space: 2, maxIndentLevel: 1 }), '[\n  {"foo": {"bar": "foo"}}\n]');

            assert.strictEqual(serialize(obj, { space: 2, maxIndentLevel: 2 }), '[\n  {\n    "foo": {"bar": "foo"}\n  }\n]');
        });
    });
});