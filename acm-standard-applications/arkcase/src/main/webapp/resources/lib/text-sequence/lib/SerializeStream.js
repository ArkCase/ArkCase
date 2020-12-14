"use strict";

var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _possibleConstructorReturn(self, call) { if (!self) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return call && (typeof call === "object" || typeof call === "function") ? call : self; }

function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function, not " + typeof superClass); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, enumerable: false, writable: true, configurable: true } }); if (superClass) Object.setPrototypeOf ? Object.setPrototypeOf(subClass, superClass) : subClass.__proto__ = superClass; }

var _require = require("stream"),
    Transform = _require.Transform;

var Serializer = require("./Serializer");

// https://nodejs.org/api/stream.html#stream_implementing_a_transform_stream

module.exports = function (_Transform) {
    _inherits(SerializeStream, _Transform);

    function SerializeStream() {
        var options = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : {};

        _classCallCheck(this, SerializeStream);

        var _this = _possibleConstructorReturn(this, (SerializeStream.__proto__ || Object.getPrototypeOf(SerializeStream)).call(this, Object.assign({ objectMode: true, decodeStrings: false }, options)));

        _this.serializer = new Serializer();
        _this.serializer.on("data", function (data) {
            _this.push(data);
        });
        return _this;
    }

    _createClass(SerializeStream, [{
        key: "_transform",
        value: function _transform(data, encoding, callback) {
            try {
                this.serializer.write(data);
            } catch (err) {
                callback(err);
                return;
            }
            callback();
        }
    }, {
        key: "_final",
        value: function _final(callback) {
            this.serializer.end();
            callback();
        }
    }]);

    return SerializeStream;
}(Transform);