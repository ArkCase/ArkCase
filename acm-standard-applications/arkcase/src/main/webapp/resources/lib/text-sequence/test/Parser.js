"use strict";

var test = require("ava");
var Parser = require("../lib/Parser");
var assert = require("assert");

function catchEvents(str) {
    var parser = new Parser();
    var events = [];
    var ended = false;
    ["invalid", "error", "data", "truncated"].forEach(function (event) {
        parser.on(event, function () {
            for (var _len = arguments.length, args = Array(_len), _key = 0; _key < _len; _key++) {
                args[_key] = arguments[_key];
            }

            events.push([event].concat(args));
        });
    });
    parser.on("end", function () {
        ended = true;
    });
    parser.write(str);
    parser.end();
    assert(ended === true);
    // console.log(events);
    return events;
}

test("invalid json", function (t) {
    t.deepEqual(catchEvents('\x1e"hello\x0A'), [["invalid", '"hello']]);
    t.deepEqual(catchEvents("\x1e'hello'\x0A"), [["invalid", "'hello'"]]);
    t.deepEqual(catchEvents("\x1e[\x0A"), [["invalid", "["]]);
    t.deepEqual(catchEvents("\x1e{\x0A"), [["invalid", "{"]]);
    t.deepEqual(catchEvents("\x1efoobar\x0A"), [["invalid", "foobar"]]);
});

test("truncated", function (t) {
    t.deepEqual(catchEvents("\x1enull\x1e"), [["truncated", "null"], ["truncated", ""]]);
    t.deepEqual(catchEvents("\x0Anull\x0A"), [["truncated", ""], ["truncated", "null"]]);
    t.deepEqual(catchEvents('\x1e"foo"\x0Abar'), [["data", "foo"], ["truncated", "bar"]]);
    t.deepEqual(catchEvents('\x1e"foo"\x1ebar'), [["truncated", '"foo"'], ["truncated", "bar"]]);
    t.deepEqual(catchEvents("123\x0A"), [["truncated", "123"]]);
    t.deepEqual(catchEvents("\x1e123"), [["truncated", "123"]]);
    t.deepEqual(catchEvents("\x1e"), [["truncated", ""]]);
    t.deepEqual(catchEvents("\x0A"), [["truncated", ""]]);
});

test("empty sequence", function (t) {
    t.deepEqual(catchEvents("\x1e\x0A"), [["invalid", ""]]);
    t.deepEqual(catchEvents(""), []);
    // Multiple consecutive RS octets do not denote empty
    // sequence elements between them and can be ignored.
    // https://tools.ietf.org/html/rfc7464#section-2.1
    t.deepEqual(catchEvents("\x1e\x1e"), [["truncated", ""]]);
    t.deepEqual(catchEvents("\x1e\x0A\x1e"), [["invalid", ""], ["truncated", ""]]);
});

test("valid json", function (t) {
    t.deepEqual(catchEvents('\x1e"hello"\x0A'), [["data", "hello"]]);
    t.deepEqual(catchEvents("\x1etrue\x0A"), [["data", true]]);
    t.deepEqual(catchEvents("\x1efalse\x0A"), [["data", false]]);
    t.deepEqual(catchEvents("\x1enull\x0A"), [["data", null]]);
    t.deepEqual(catchEvents("\x1e0\x0A"), [["data", 0]]);
    t.deepEqual(catchEvents("\x1e-1\x0A"), [["data", -1]]);
    t.deepEqual(catchEvents("\x1e1\x0A"), [["data", 1]]);
});