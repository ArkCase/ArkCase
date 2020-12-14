"use strict";

var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _possibleConstructorReturn(self, call) { if (!self) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return call && (typeof call === "object" || typeof call === "function") ? call : self; }

function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function, not " + typeof superClass); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, enumerable: false, writable: true, configurable: true } }); if (superClass) Object.setPrototypeOf ? Object.setPrototypeOf(subClass, superClass) : subClass.__proto__ = superClass; }

var _require = require("stream"),
    Transform = _require.Transform;

var Parser = require("./Parser");

// https://nodejs.org/api/stream.html#stream_implementing_a_transform_stream

module.exports = function (_Transform) {
    _inherits(ParseStream, _Transform);

    function ParseStream() {
        var options = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : {};
        var parser = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : new Parser();

        _classCallCheck(this, ParseStream);

        var _this = _possibleConstructorReturn(this, (ParseStream.__proto__ || Object.getPrototypeOf(ParseStream)).call(this, Object.assign({ objectMode: true, decodeStrings: false }, options)));

        _this.parser = parser;
        _this.parser.on("truncated", function (seq) {
            _this.emit("truncated", seq);
        });
        _this.parser.on("invalid", function (seq) {
            _this.emit("invalid", seq);
        });
        _this.parser.on("error", function (err) {
            _this.emit("error", err);
        });
        _this.parser.on("data", function (data) {
            _this.push(data);
        });
        return _this;
    }

    _createClass(ParseStream, [{
        key: "_transform",
        value: function _transform(data, encoding, callback) {
            this.parser.write(data);
            callback();
        }
    }, {
        key: "_final",
        value: function _final(callback) {
            this.parser.end();
            callback();
        }
    }]);

    return ParseStream;
}(Transform);