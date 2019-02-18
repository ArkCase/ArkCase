"use strict";

function _asyncToGenerator(fn) { return function () { var gen = fn.apply(this, arguments); return new Promise(function (resolve, reject) { function step(key, arg) { try { var info = gen[key](arg); var value = info.value; } catch (error) { reject(error); return; } if (info.done) { resolve(value); } else { return Promise.resolve(value).then(function (value) { step("next", value); }, function (err) { step("throw", err); }); } } return step("next"); }); }; }

var _require = require("fs"),
    createReadStream = _require.createReadStream,
    readFile = _require.readFile;

var promisify = require("util.promisify");
var JSON8TextSequence = require("..");
var JSONStream = require("JSONStream");
var JSONTextSequence = require("json-text-sequence");
var pEvent = require("p-event");

var pReadFile = promisify(readFile);

var testJSON8TextSequence = function () {
    var _ref = _asyncToGenerator( /*#__PURE__*/regeneratorRuntime.mark(function _callee() {
        var stream, now;
        return regeneratorRuntime.wrap(function _callee$(_context) {
            while (1) {
                switch (_context.prev = _context.next) {
                    case 0:
                        stream = createReadStream(__dirname + "/data.jts");


                        stream.pipe(new JSON8TextSequence.ParseStream());

                        now = Date.now();

                        stream.on("end", function () {
                            console.log("json8-text-sequence", Date.now() - now, "ms");
                        });
                        return _context.abrupt("return", pEvent(stream, "end"));

                    case 5:
                    case "end":
                        return _context.stop();
                }
            }
        }, _callee, undefined);
    }));

    return function testJSON8TextSequence() {
        return _ref.apply(this, arguments);
    };
}();

var testJSONTextSequence = function () {
    var _ref2 = _asyncToGenerator( /*#__PURE__*/regeneratorRuntime.mark(function _callee2() {
        var stream, now;
        return regeneratorRuntime.wrap(function _callee2$(_context2) {
            while (1) {
                switch (_context2.prev = _context2.next) {
                    case 0:
                        stream = createReadStream(__dirname + "/data.jts");


                        stream.pipe(new JSONTextSequence.parser());

                        now = Date.now();

                        stream.on("end", function () {
                            console.log("json-text-sequence", Date.now() - now, "ms");
                        });
                        return _context2.abrupt("return", pEvent(stream, "end"));

                    case 5:
                    case "end":
                        return _context2.stop();
                }
            }
        }, _callee2, undefined);
    }));

    return function testJSONTextSequence() {
        return _ref2.apply(this, arguments);
    };
}();

var testJSONStream = function () {
    var _ref3 = _asyncToGenerator( /*#__PURE__*/regeneratorRuntime.mark(function _callee3() {
        var stream, now;
        return regeneratorRuntime.wrap(function _callee3$(_context3) {
            while (1) {
                switch (_context3.prev = _context3.next) {
                    case 0:
                        stream = createReadStream(__dirname + "/data.json");


                        stream.pipe(JSONStream.parse());

                        now = Date.now();

                        stream.on("end", function () {
                            console.log("JSONStream", Date.now() - now, "ms");
                        });
                        return _context3.abrupt("return", pEvent(stream, "end"));

                    case 5:
                    case "end":
                        return _context3.stop();
                }
            }
        }, _callee3, undefined);
    }));

    return function testJSONStream() {
        return _ref3.apply(this, arguments);
    };
}();

var testRequire = function () {
    var _ref4 = _asyncToGenerator( /*#__PURE__*/regeneratorRuntime.mark(function _callee4() {
        var now;
        return regeneratorRuntime.wrap(function _callee4$(_context4) {
            while (1) {
                switch (_context4.prev = _context4.next) {
                    case 0:
                        now = Date.now();

                        require(__dirname + "/data.json");
                        console.log("require (non streaming)", Date.now() - now, "ms");

                    case 3:
                    case "end":
                        return _context4.stop();
                }
            }
        }, _callee4, undefined);
    }));

    return function testRequire() {
        return _ref4.apply(this, arguments);
    };
}();

var testJSONParse = function () {
    var _ref5 = _asyncToGenerator( /*#__PURE__*/regeneratorRuntime.mark(function _callee5() {
        var now;
        return regeneratorRuntime.wrap(function _callee5$(_context5) {
            while (1) {
                switch (_context5.prev = _context5.next) {
                    case 0:
                        now = Date.now();
                        _context5.t0 = JSON;
                        _context5.next = 4;
                        return pReadFile(__dirname + "/data.json", "utf8");

                    case 4:
                        _context5.t1 = _context5.sent;

                        _context5.t0.parse.call(_context5.t0, _context5.t1);

                        console.log("fs.readFile and JSON.parse (non streaming)", Date.now() - now, "ms");

                    case 7:
                    case "end":
                        return _context5.stop();
                }
            }
        }, _callee5, undefined);
    }));

    return function testJSONParse() {
        return _ref5.apply(this, arguments);
    };
}();

var testJSON8TextSequenceParse = function () {
    var _ref6 = _asyncToGenerator( /*#__PURE__*/regeneratorRuntime.mark(function _callee6() {
        var now;
        return regeneratorRuntime.wrap(function _callee6$(_context6) {
            while (1) {
                switch (_context6.prev = _context6.next) {
                    case 0:
                        now = Date.now();
                        _context6.t0 = JSON8TextSequence;
                        _context6.next = 4;
                        return pReadFile(__dirname + "/data.jts", "utf8");

                    case 4:
                        _context6.t1 = _context6.sent;

                        _context6.t0.parse.call(_context6.t0, _context6.t1);

                        console.log("fs.readFile and json8-text-sequence parse (non streaming)", Date.now() - now, "ms");

                    case 7:
                    case "end":
                        return _context6.stop();
                }
            }
        }, _callee6, undefined);
    }));

    return function testJSON8TextSequenceParse() {
        return _ref6.apply(this, arguments);
    };
}();

_asyncToGenerator( /*#__PURE__*/regeneratorRuntime.mark(function _callee7() {
    return regeneratorRuntime.wrap(function _callee7$(_context7) {
        while (1) {
            switch (_context7.prev = _context7.next) {
                case 0:
                    _context7.next = 2;
                    return testJSONStream();

                case 2:
                    _context7.next = 4;
                    return testJSON8TextSequence();

                case 4:
                    _context7.next = 6;
                    return testJSONTextSequence();

                case 6:
                    _context7.next = 8;
                    return testRequire();

                case 8:
                    _context7.next = 10;
                    return testJSONParse();

                case 10:
                    _context7.next = 12;
                    return testJSON8TextSequenceParse();

                case 12:
                case "end":
                    return _context7.stop();
            }
        }
    }, _callee7, undefined);
}))();