"use strict";

var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _possibleConstructorReturn(self, call) { if (!self) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return call && (typeof call === "object" || typeof call === "function") ? call : self; }

function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function, not " + typeof superClass); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, enumerable: false, writable: true, configurable: true } }); if (superClass) Object.setPrototypeOf ? Object.setPrototypeOf(subClass, superClass) : subClass.__proto__ = superClass; }

var _require = require("./chars"),
    RS = _require.RS,
    LF = _require.LF;

var _require2 = require("events"),
    EventEmitter = _require2.EventEmitter;

module.exports = function (_EventEmitter) {
    _inherits(Parser, _EventEmitter);

    function Parser() {
        _classCallCheck(this, Parser);

        var _this = _possibleConstructorReturn(this, (Parser.__proto__ || Object.getPrototypeOf(Parser)).call(this));

        _this.seq = "";
        _this.open = false;
        return _this;
    }

    _createClass(Parser, [{
        key: "write",
        value: function write(data) {
            if (typeof data !== "string") data = data.toString();
            for (var pos = 0, l = data.length; pos < l; pos++) {
                var char = data[pos];
                this.last = char;
                if (char === RS) {
                    this.open = true;
                    if (this.seq.length > 0) {
                        this.emit("truncated", this.seq);
                        this.seq = "";
                    }
                } else if (char === LF) {
                    if (this.open === false) {
                        this.emit("truncated", this.seq);
                        this.seq = "";
                        continue;
                    }
                    this.open = false;
                    try {
                        this.emit("data", JSON.parse(this.seq));
                    } catch (err) {
                        this.emit("invalid", this.seq);
                    }
                    this.seq = "";
                } else {
                    this.seq += char;
                }
            }
        }
    }, {
        key: "end",
        value: function end(data) {
            if (data) this.write(data);
            if (this.seq.length > 0 || this.open === true) {
                this.emit("truncated", this.seq);
                this.seq = "";
            }
            this.emit("end");
        }
    }]);

    return Parser;
}(EventEmitter);