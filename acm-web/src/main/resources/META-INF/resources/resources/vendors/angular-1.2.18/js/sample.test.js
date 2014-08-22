/*! angular-dashboard-framework 0.1.0 (2014-08-22) */
angular.module("ui.bootstrap", ["ui.bootstrap.transition", "ui.bootstrap.collapse", "ui.bootstrap.accordion", "ui.bootstrap.alert", "ui.bootstrap.bindHtml", "ui.bootstrap.buttons", "ui.bootstrap.carousel", "ui.bootstrap.dateparser", "ui.bootstrap.position", "ui.bootstrap.datepicker", "ui.bootstrap.dropdown", "ui.bootstrap.modal", "ui.bootstrap.pagination", "ui.bootstrap.tooltip", "ui.bootstrap.popover", "ui.bootstrap.progressbar", "ui.bootstrap.rating", "ui.bootstrap.tabs", "ui.bootstrap.timepicker", "ui.bootstrap.typeahead"]), angular.module("ui.bootstrap.transition", []).factory("$transition", ["$q", "$timeout", "$rootScope",
    function($q, $timeout, $rootScope) {
        function findEndEventName(endEventNames) {
            for (var name in endEventNames)
                if (void 0 !== transElement.style[name]) return endEventNames[name]
        }
        var $transition = function(element, trigger, options) {
                options = options || {};
                var deferred = $q.defer(),
                    endEventName = $transition[options.animation ? "animationEndEventName" : "transitionEndEventName"],
                    transitionEndHandler = function() {
                        $rootScope.$apply(function() {
                            element.unbind(endEventName, transitionEndHandler), deferred.resolve(element)
                        })
                    };
                return endEventName && element.bind(endEventName, transitionEndHandler), $timeout(function() {
                    angular.isString(trigger) ? element.addClass(trigger) : angular.isFunction(trigger) ? trigger(element) : angular.isObject(trigger) && element.css(trigger), endEventName || deferred.resolve(element)
                }), deferred.promise.cancel = function() {
                    endEventName && element.unbind(endEventName, transitionEndHandler), deferred.reject("Transition cancelled")
                }, deferred.promise
            },
            transElement = document.createElement("trans"),
            transitionEndEventNames = {
                WebkitTransition: "webkitTransitionEnd",
                MozTransition: "transitionend",
                OTransition: "oTransitionEnd",
                transition: "transitionend"
            },
            animationEndEventNames = {
                WebkitTransition: "webkitAnimationEnd",
                MozTransition: "animationend",
                OTransition: "oAnimationEnd",
                transition: "animationend"
            };
        return $transition.transitionEndEventName = findEndEventName(transitionEndEventNames), $transition.animationEndEventName = findEndEventName(animationEndEventNames), $transition
    }
]), angular.module("ui.bootstrap.collapse", ["ui.bootstrap.transition"]).directive("collapse", ["$transition",
    function($transition) {
        return {
            link: function(scope, element, attrs) {
                function doTransition(change) {
                    function newTransitionDone() {
                        currentTransition === newTransition && (currentTransition = void 0)
                    }
                    var newTransition = $transition(element, change);
                    return currentTransition && currentTransition.cancel(), currentTransition = newTransition, newTransition.then(newTransitionDone, newTransitionDone), newTransition
                }

                function expand() {
                    initialAnimSkip ? (initialAnimSkip = !1, expandDone()) : (element.removeClass("collapse").addClass("collapsing"), doTransition({
                        height: element[0].scrollHeight + "px"
                    }).then(expandDone))
                }

                function expandDone() {
                    element.removeClass("collapsing"), element.addClass("collapse in"), element.css({
                        height: "auto"
                    })
                }

                function collapse() {
                    if (initialAnimSkip) initialAnimSkip = !1, collapseDone(), element.css({
                        height: 0
                    });
                    else {
                        element.css({
                            height: element[0].scrollHeight + "px"
                        }); {
                            element[0].offsetWidth
                        }
                        element.removeClass("collapse in").addClass("collapsing"), doTransition({
                            height: 0
                        }).then(collapseDone)
                    }
                }

                function collapseDone() {
                    element.removeClass("collapsing"), element.addClass("collapse")
                }
                var currentTransition, initialAnimSkip = !0;
                scope.$watch(attrs.collapse, function(shouldCollapse) {
                    shouldCollapse ? collapse() : expand()
                })
            }
        }
    }
]), angular.module("ui.bootstrap.accordion", ["ui.bootstrap.collapse"]).constant("accordionConfig", {
    closeOthers: !0
}).controller("AccordionController", ["$scope", "$attrs", "accordionConfig",
    function($scope, $attrs, accordionConfig) {
        this.groups = [], this.closeOthers = function(openGroup) {
            var closeOthers = angular.isDefined($attrs.closeOthers) ? $scope.$eval($attrs.closeOthers) : accordionConfig.closeOthers;
            closeOthers && angular.forEach(this.groups, function(group) {
                group !== openGroup && (group.isOpen = !1)
            })
        }, this.addGroup = function(groupScope) {
            var that = this;
            this.groups.push(groupScope), groupScope.$on("$destroy", function() {
                that.removeGroup(groupScope)
            })
        }, this.removeGroup = function(group) {
            var index = this.groups.indexOf(group); - 1 !== index && this.groups.splice(index, 1)
        }
    }
]).directive("accordion", function() {
    return {
        restrict: "EA",
        controller: "AccordionController",
        transclude: !0,
        replace: !1,
        templateUrl: "template/accordion/accordion.html"
    }
}).directive("accordionGroup", function() {
    return {
        require: "^accordion",
        restrict: "EA",
        transclude: !0,
        replace: !0,
        templateUrl: "template/accordion/accordion-group.html",
        scope: {
            heading: "@",
            isOpen: "=?",
            isDisabled: "=?"
        },
        controller: function() {
            this.setHeading = function(element) {
                this.heading = element
            }
        },
        link: function(scope, element, attrs, accordionCtrl) {
            accordionCtrl.addGroup(scope), scope.$watch("isOpen", function(value) {
                value && accordionCtrl.closeOthers(scope)
            }), scope.toggleOpen = function() {
                scope.isDisabled || (scope.isOpen = !scope.isOpen)
            }
        }
    }
}).directive("accordionHeading", function() {
    return {
        restrict: "EA",
        transclude: !0,
        template: "",
        replace: !0,
        require: "^accordionGroup",
        link: function(scope, element, attr, accordionGroupCtrl, transclude) {
            accordionGroupCtrl.setHeading(transclude(scope, function() {}))
        }
    }
}).directive("accordionTransclude", function() {
    return {
        require: "^accordionGroup",
        link: function(scope, element, attr, controller) {
            scope.$watch(function() {
                return controller[attr.accordionTransclude]
            }, function(heading) {
                heading && (element.html(""), element.append(heading))
            })
        }
    }
}), angular.module("ui.bootstrap.alert", []).controller("AlertController", ["$scope", "$attrs",
    function($scope, $attrs) {
        $scope.closeable = "close" in $attrs
    }
]).directive("alert", function() {
    return {
        restrict: "EA",
        controller: "AlertController",
        templateUrl: "template/alert/alert.html",
        transclude: !0,
        replace: !0,
        scope: {
            type: "@",
            close: "&"
        }
    }
}), angular.module("ui.bootstrap.bindHtml", []).directive("bindHtmlUnsafe", function() {
    return function(scope, element, attr) {
        element.addClass("ng-binding").data("$binding", attr.bindHtmlUnsafe), scope.$watch(attr.bindHtmlUnsafe, function(value) {
            element.html(value || "")
        })
    }
}), angular.module("ui.bootstrap.buttons", []).constant("buttonConfig", {
    activeClass: "active",
    toggleEvent: "click"
}).controller("ButtonsController", ["buttonConfig",
    function(buttonConfig) {
        this.activeClass = buttonConfig.activeClass || "active", this.toggleEvent = buttonConfig.toggleEvent || "click"
    }
]).directive("btnRadio", function() {
    return {
        require: ["btnRadio", "ngModel"],
        controller: "ButtonsController",
        link: function(scope, element, attrs, ctrls) {
            var buttonsCtrl = ctrls[0],
                ngModelCtrl = ctrls[1];
            ngModelCtrl.$render = function() {
                element.toggleClass(buttonsCtrl.activeClass, angular.equals(ngModelCtrl.$modelValue, scope.$eval(attrs.btnRadio)))
            }, element.bind(buttonsCtrl.toggleEvent, function() {
                var isActive = element.hasClass(buttonsCtrl.activeClass);
                (!isActive || angular.isDefined(attrs.uncheckable)) && scope.$apply(function() {
                    ngModelCtrl.$setViewValue(isActive ? null : scope.$eval(attrs.btnRadio)), ngModelCtrl.$render()
                })
            })
        }
    }
}).directive("btnCheckbox", function() {
    return {
        require: ["btnCheckbox", "ngModel"],
        controller: "ButtonsController",
        link: function(scope, element, attrs, ctrls) {
            function getTrueValue() {
                return getCheckboxValue(attrs.btnCheckboxTrue, !0)
            }

            function getFalseValue() {
                return getCheckboxValue(attrs.btnCheckboxFalse, !1)
            }

            function getCheckboxValue(attributeValue, defaultValue) {
                var val = scope.$eval(attributeValue);
                return angular.isDefined(val) ? val : defaultValue
            }
            var buttonsCtrl = ctrls[0],
                ngModelCtrl = ctrls[1];
            ngModelCtrl.$render = function() {
                element.toggleClass(buttonsCtrl.activeClass, angular.equals(ngModelCtrl.$modelValue, getTrueValue()))
            }, element.bind(buttonsCtrl.toggleEvent, function() {
                scope.$apply(function() {
                    ngModelCtrl.$setViewValue(element.hasClass(buttonsCtrl.activeClass) ? getFalseValue() : getTrueValue()), ngModelCtrl.$render()
                })
            })
        }
    }
}), angular.module("ui.bootstrap.carousel", ["ui.bootstrap.transition"]).controller("CarouselController", ["$scope", "$timeout", "$transition",
    function($scope, $timeout, $transition) {
        function restartTimer() {
            resetTimer();
            var interval = +$scope.interval;
            !isNaN(interval) && interval >= 0 && (currentTimeout = $timeout(timerFn, interval))
        }

        function resetTimer() {
            currentTimeout && ($timeout.cancel(currentTimeout), currentTimeout = null)
        }

        function timerFn() {
            isPlaying ? ($scope.next(), restartTimer()) : $scope.pause()
        }
        var currentTimeout, isPlaying, self = this,
            slides = self.slides = $scope.slides = [],
            currentIndex = -1;
        self.currentSlide = null;
        var destroyed = !1;
        self.select = $scope.select = function(nextSlide, direction) {
            function goNext() {
                if (!destroyed) {
                    if (self.currentSlide && angular.isString(direction) && !$scope.noTransition && nextSlide.$element) {
                        nextSlide.$element.addClass(direction); {
                            nextSlide.$element[0].offsetWidth
                        }
                        angular.forEach(slides, function(slide) {
                            angular.extend(slide, {
                                direction: "",
                                entering: !1,
                                leaving: !1,
                                active: !1
                            })
                        }), angular.extend(nextSlide, {
                            direction: direction,
                            active: !0,
                            entering: !0
                        }), angular.extend(self.currentSlide || {}, {
                            direction: direction,
                            leaving: !0
                        }), $scope.$currentTransition = $transition(nextSlide.$element, {}),
                            function(next, current) {
                                $scope.$currentTransition.then(function() {
                                    transitionDone(next, current)
                                }, function() {
                                    transitionDone(next, current)
                                })
                            }(nextSlide, self.currentSlide)
                    } else transitionDone(nextSlide, self.currentSlide);
                    self.currentSlide = nextSlide, currentIndex = nextIndex, restartTimer()
                }
            }

            function transitionDone(next, current) {
                angular.extend(next, {
                    direction: "",
                    active: !0,
                    leaving: !1,
                    entering: !1
                }), angular.extend(current || {}, {
                    direction: "",
                    active: !1,
                    leaving: !1,
                    entering: !1
                }), $scope.$currentTransition = null
            }
            var nextIndex = slides.indexOf(nextSlide);
            void 0 === direction && (direction = nextIndex > currentIndex ? "next" : "prev"), nextSlide && nextSlide !== self.currentSlide && ($scope.$currentTransition ? ($scope.$currentTransition.cancel(), $timeout(goNext)) : goNext())
        }, $scope.$on("$destroy", function() {
            destroyed = !0
        }), self.indexOfSlide = function(slide) {
            return slides.indexOf(slide)
        }, $scope.next = function() {
            var newIndex = (currentIndex + 1) % slides.length;
            return $scope.$currentTransition ? void 0 : self.select(slides[newIndex], "next")
        }, $scope.prev = function() {
            var newIndex = 0 > currentIndex - 1 ? slides.length - 1 : currentIndex - 1;
            return $scope.$currentTransition ? void 0 : self.select(slides[newIndex], "prev")
        }, $scope.isActive = function(slide) {
            return self.currentSlide === slide
        }, $scope.$watch("interval", restartTimer), $scope.$on("$destroy", resetTimer), $scope.play = function() {
            isPlaying || (isPlaying = !0, restartTimer())
        }, $scope.pause = function() {
            $scope.noPause || (isPlaying = !1, resetTimer())
        }, self.addSlide = function(slide, element) {
            slide.$element = element, slides.push(slide), 1 === slides.length || slide.active ? (self.select(slides[slides.length - 1]), 1 == slides.length && $scope.play()) : slide.active = !1
        }, self.removeSlide = function(slide) {
            var index = slides.indexOf(slide);
            slides.splice(index, 1), slides.length > 0 && slide.active ? self.select(index >= slides.length ? slides[index - 1] : slides[index]) : currentIndex > index && currentIndex--
        }
    }
]).directive("carousel", [
    function() {
        return {
            restrict: "EA",
            transclude: !0,
            replace: !0,
            controller: "CarouselController",
            require: "carousel",
            templateUrl: "template/carousel/carousel.html",
            scope: {
                interval: "=",
                noTransition: "=",
                noPause: "="
            }
        }
    }
]).directive("slide", function() {
    return {
        require: "^carousel",
        restrict: "EA",
        transclude: !0,
        replace: !0,
        templateUrl: "template/carousel/slide.html",
        scope: {
            active: "=?"
        },
        link: function(scope, element, attrs, carouselCtrl) {
            carouselCtrl.addSlide(scope, element), scope.$on("$destroy", function() {
                carouselCtrl.removeSlide(scope)
            }), scope.$watch("active", function(active) {
                active && carouselCtrl.select(scope)
            })
        }
    }
}), angular.module("ui.bootstrap.dateparser", []).service("dateParser", ["$locale", "orderByFilter",
    function($locale, orderByFilter) {
        function isValid(year, month, date) {
            return 1 === month && date > 28 ? 29 === date && (year % 4 === 0 && year % 100 !== 0 || year % 400 === 0) : 3 === month || 5 === month || 8 === month || 10 === month ? 31 > date : !0
        }
        this.parsers = {};
        var formatCodeToRegex = {
            yyyy: {
                regex: "\\d{4}",
                apply: function(value) {
                    this.year = +value
                }
            },
            yy: {
                regex: "\\d{2}",
                apply: function(value) {
                    this.year = +value + 2e3
                }
            },
            y: {
                regex: "\\d{1,4}",
                apply: function(value) {
                    this.year = +value
                }
            },
            MMMM: {
                regex: $locale.DATETIME_FORMATS.MONTH.join("|"),
                apply: function(value) {
                    this.month = $locale.DATETIME_FORMATS.MONTH.indexOf(value)
                }
            },
            MMM: {
                regex: $locale.DATETIME_FORMATS.SHORTMONTH.join("|"),
                apply: function(value) {
                    this.month = $locale.DATETIME_FORMATS.SHORTMONTH.indexOf(value)
                }
            },
            MM: {
                regex: "0[1-9]|1[0-2]",
                apply: function(value) {
                    this.month = value - 1
                }
            },
            M: {
                regex: "[1-9]|1[0-2]",
                apply: function(value) {
                    this.month = value - 1
                }
            },
            dd: {
                regex: "[0-2][0-9]{1}|3[0-1]{1}",
                apply: function(value) {
                    this.date = +value
                }
            },
            d: {
                regex: "[1-2]?[0-9]{1}|3[0-1]{1}",
                apply: function(value) {
                    this.date = +value
                }
            },
            EEEE: {
                regex: $locale.DATETIME_FORMATS.DAY.join("|")
            },
            EEE: {
                regex: $locale.DATETIME_FORMATS.SHORTDAY.join("|")
            }
        };
        this.createParser = function(format) {
            var map = [],
                regex = format.split("");
            return angular.forEach(formatCodeToRegex, function(data, code) {
                var index = format.indexOf(code);
                if (index > -1) {
                    format = format.split(""), regex[index] = "(" + data.regex + ")", format[index] = "$";
                    for (var i = index + 1, n = index + code.length; n > i; i++) regex[i] = "", format[i] = "$";
                    format = format.join(""), map.push({
                        index: index,
                        apply: data.apply
                    })
                }
            }), {
                regex: new RegExp("^" + regex.join("") + "$"),
                map: orderByFilter(map, "index")
            }
        }, this.parse = function(input, format) {
            if (!angular.isString(input)) return input;
            format = $locale.DATETIME_FORMATS[format] || format, this.parsers[format] || (this.parsers[format] = this.createParser(format));
            var parser = this.parsers[format],
                regex = parser.regex,
                map = parser.map,
                results = input.match(regex);
            if (results && results.length) {
                for (var dt, fields = {
                    year: 1900,
                    month: 0,
                    date: 1,
                    hours: 0
                }, i = 1, n = results.length; n > i; i++) {
                    var mapper = map[i - 1];
                    mapper.apply && mapper.apply.call(fields, results[i])
                }
                return isValid(fields.year, fields.month, fields.date) && (dt = new Date(fields.year, fields.month, fields.date, fields.hours)), dt
            }
        }
    }
]), angular.module("ui.bootstrap.position", []).factory("$position", ["$document", "$window",
    function($document, $window) {
        function getStyle(el, cssprop) {
            return el.currentStyle ? el.currentStyle[cssprop] : $window.getComputedStyle ? $window.getComputedStyle(el)[cssprop] : el.style[cssprop]
        }

        function isStaticPositioned(element) {
            return "static" === (getStyle(element, "position") || "static")
        }
        var parentOffsetEl = function(element) {
            for (var docDomEl = $document[0], offsetParent = element.offsetParent || docDomEl; offsetParent && offsetParent !== docDomEl && isStaticPositioned(offsetParent);) offsetParent = offsetParent.offsetParent;
            return offsetParent || docDomEl
        };
        return {
            position: function(element) {
                var elBCR = this.offset(element),
                    offsetParentBCR = {
                        top: 0,
                        left: 0
                    },
                    offsetParentEl = parentOffsetEl(element[0]);
                offsetParentEl != $document[0] && (offsetParentBCR = this.offset(angular.element(offsetParentEl)), offsetParentBCR.top += offsetParentEl.clientTop - offsetParentEl.scrollTop, offsetParentBCR.left += offsetParentEl.clientLeft - offsetParentEl.scrollLeft);
                var boundingClientRect = element[0].getBoundingClientRect();
                return {
                    width: boundingClientRect.width || element.prop("offsetWidth"),
                    height: boundingClientRect.height || element.prop("offsetHeight"),
                    top: elBCR.top - offsetParentBCR.top,
                    left: elBCR.left - offsetParentBCR.left
                }
            },
            offset: function(element) {
                var boundingClientRect = element[0].getBoundingClientRect();
                return {
                    width: boundingClientRect.width || element.prop("offsetWidth"),
                    height: boundingClientRect.height || element.prop("offsetHeight"),
                    top: boundingClientRect.top + ($window.pageYOffset || $document[0].documentElement.scrollTop),
                    left: boundingClientRect.left + ($window.pageXOffset || $document[0].documentElement.scrollLeft)
                }
            },
            positionElements: function(hostEl, targetEl, positionStr, appendToBody) {
                var hostElPos, targetElWidth, targetElHeight, targetElPos, positionStrParts = positionStr.split("-"),
                    pos0 = positionStrParts[0],
                    pos1 = positionStrParts[1] || "center";
                hostElPos = appendToBody ? this.offset(hostEl) : this.position(hostEl), targetElWidth = targetEl.prop("offsetWidth"), targetElHeight = targetEl.prop("offsetHeight");
                var shiftWidth = {
                        center: function() {
                            return hostElPos.left + hostElPos.width / 2 - targetElWidth / 2
                        },
                        left: function() {
                            return hostElPos.left
                        },
                        right: function() {
                            return hostElPos.left + hostElPos.width
                        }
                    },
                    shiftHeight = {
                        center: function() {
                            return hostElPos.top + hostElPos.height / 2 - targetElHeight / 2
                        },
                        top: function() {
                            return hostElPos.top
                        },
                        bottom: function() {
                            return hostElPos.top + hostElPos.height
                        }
                    };
                switch (pos0) {
                    case "right":
                        targetElPos = {
                            top: shiftHeight[pos1](),
                            left: shiftWidth[pos0]()
                        };
                        break;
                    case "left":
                        targetElPos = {
                            top: shiftHeight[pos1](),
                            left: hostElPos.left - targetElWidth
                        };
                        break;
                    case "bottom":
                        targetElPos = {
                            top: shiftHeight[pos0](),
                            left: shiftWidth[pos1]()
                        };
                        break;
                    default:
                        targetElPos = {
                            top: hostElPos.top - targetElHeight,
                            left: shiftWidth[pos1]()
                        }
                }
                return targetElPos
            }
        }
    }
]), angular.module("ui.bootstrap.datepicker", ["ui.bootstrap.dateparser", "ui.bootstrap.position"]).constant("datepickerConfig", {
    formatDay: "dd",
    formatMonth: "MMMM",
    formatYear: "yyyy",
    formatDayHeader: "EEE",
    formatDayTitle: "MMMM yyyy",
    formatMonthTitle: "yyyy",
    datepickerMode: "day",
    minMode: "day",
    maxMode: "year",
    showWeeks: !0,
    startingDay: 0,
    yearRange: 20,
    minDate: null,
    maxDate: null
}).controller("DatepickerController", ["$scope", "$attrs", "$parse", "$interpolate", "$timeout", "$log", "dateFilter", "datepickerConfig",
    function($scope, $attrs, $parse, $interpolate, $timeout, $log, dateFilter, datepickerConfig) {
        var self = this,
            ngModelCtrl = {
                $setViewValue: angular.noop
            };
        this.modes = ["day", "month", "year"], angular.forEach(["formatDay", "formatMonth", "formatYear", "formatDayHeader", "formatDayTitle", "formatMonthTitle", "minMode", "maxMode", "showWeeks", "startingDay", "yearRange"], function(key, index) {
            self[key] = angular.isDefined($attrs[key]) ? 8 > index ? $interpolate($attrs[key])($scope.$parent) : $scope.$parent.$eval($attrs[key]) : datepickerConfig[key]
        }), angular.forEach(["minDate", "maxDate"], function(key) {
            $attrs[key] ? $scope.$parent.$watch($parse($attrs[key]), function(value) {
                self[key] = value ? new Date(value) : null, self.refreshView()
            }) : self[key] = datepickerConfig[key] ? new Date(datepickerConfig[key]) : null
        }), $scope.datepickerMode = $scope.datepickerMode || datepickerConfig.datepickerMode, $scope.uniqueId = "datepicker-" + $scope.$id + "-" + Math.floor(1e4 * Math.random()), this.activeDate = angular.isDefined($attrs.initDate) ? $scope.$parent.$eval($attrs.initDate) : new Date, $scope.isActive = function(dateObject) {
            return 0 === self.compare(dateObject.date, self.activeDate) ? ($scope.activeDateId = dateObject.uid, !0) : !1
        }, this.init = function(ngModelCtrl_) {
            ngModelCtrl = ngModelCtrl_, ngModelCtrl.$render = function() {
                self.render()
            }
        }, this.render = function() {
            if (ngModelCtrl.$modelValue) {
                var date = new Date(ngModelCtrl.$modelValue),
                    isValid = !isNaN(date);
                isValid ? this.activeDate = date : $log.error('Datepicker directive: "ng-model" value must be a Date object, a number of milliseconds since 01.01.1970 or a string representing an RFC2822 or ISO 8601 date.'), ngModelCtrl.$setValidity("date", isValid)
            }
            this.refreshView()
        }, this.refreshView = function() {
            if (this.element) {
                this._refreshView();
                var date = ngModelCtrl.$modelValue ? new Date(ngModelCtrl.$modelValue) : null;
                ngModelCtrl.$setValidity("date-disabled", !date || this.element && !this.isDisabled(date))
            }
        }, this.createDateObject = function(date, format) {
            var model = ngModelCtrl.$modelValue ? new Date(ngModelCtrl.$modelValue) : null;
            return {
                date: date,
                label: dateFilter(date, format),
                selected: model && 0 === this.compare(date, model),
                disabled: this.isDisabled(date),
                current: 0 === this.compare(date, new Date)
            }
        }, this.isDisabled = function(date) {
            return this.minDate && this.compare(date, this.minDate) < 0 || this.maxDate && this.compare(date, this.maxDate) > 0 || $attrs.dateDisabled && $scope.dateDisabled({
                date: date,
                mode: $scope.datepickerMode
            })
        }, this.split = function(arr, size) {
            for (var arrays = []; arr.length > 0;) arrays.push(arr.splice(0, size));
            return arrays
        }, $scope.select = function(date) {
            if ($scope.datepickerMode === self.minMode) {
                var dt = ngModelCtrl.$modelValue ? new Date(ngModelCtrl.$modelValue) : new Date(0, 0, 0, 0, 0, 0, 0);
                dt.setFullYear(date.getFullYear(), date.getMonth(), date.getDate()), ngModelCtrl.$setViewValue(dt), ngModelCtrl.$render()
            } else self.activeDate = date, $scope.datepickerMode = self.modes[self.modes.indexOf($scope.datepickerMode) - 1]
        }, $scope.move = function(direction) {
            var year = self.activeDate.getFullYear() + direction * (self.step.years || 0),
                month = self.activeDate.getMonth() + direction * (self.step.months || 0);
            self.activeDate.setFullYear(year, month, 1), self.refreshView()
        }, $scope.toggleMode = function(direction) {
            direction = direction || 1, $scope.datepickerMode === self.maxMode && 1 === direction || $scope.datepickerMode === self.minMode && -1 === direction || ($scope.datepickerMode = self.modes[self.modes.indexOf($scope.datepickerMode) + direction])
        }, $scope.keys = {
            13: "enter",
            32: "space",
            33: "pageup",
            34: "pagedown",
            35: "end",
            36: "home",
            37: "left",
            38: "up",
            39: "right",
            40: "down"
        };
        var focusElement = function() {
            $timeout(function() {
                self.element[0].focus()
            }, 0, !1)
        };
        $scope.$on("datepicker.focus", focusElement), $scope.keydown = function(evt) {
            var key = $scope.keys[evt.which];
            if (key && !evt.shiftKey && !evt.altKey)
                if (evt.preventDefault(), evt.stopPropagation(), "enter" === key || "space" === key) {
                    if (self.isDisabled(self.activeDate)) return;
                    $scope.select(self.activeDate), focusElement()
                } else !evt.ctrlKey || "up" !== key && "down" !== key ? (self.handleKeyDown(key, evt), self.refreshView()) : ($scope.toggleMode("up" === key ? 1 : -1), focusElement())
        }
    }
]).directive("datepicker", function() {
    return {
        restrict: "EA",
        replace: !0,
        templateUrl: "template/datepicker/datepicker.html",
        scope: {
            datepickerMode: "=?",
            dateDisabled: "&"
        },
        require: ["datepicker", "?^ngModel"],
        controller: "DatepickerController",
        link: function(scope, element, attrs, ctrls) {
            var datepickerCtrl = ctrls[0],
                ngModelCtrl = ctrls[1];
            ngModelCtrl && datepickerCtrl.init(ngModelCtrl)
        }
    }
}).directive("daypicker", ["dateFilter",
    function(dateFilter) {
        return {
            restrict: "EA",
            replace: !0,
            templateUrl: "template/datepicker/day.html",
            require: "^datepicker",
            link: function(scope, element, attrs, ctrl) {
                function getDaysInMonth(year, month) {
                    return 1 !== month || year % 4 !== 0 || year % 100 === 0 && year % 400 !== 0 ? DAYS_IN_MONTH[month] : 29
                }

                function getDates(startDate, n) {
                    var dates = new Array(n),
                        current = new Date(startDate),
                        i = 0;
                    for (current.setHours(12); n > i;) dates[i++] = new Date(current), current.setDate(current.getDate() + 1);
                    return dates
                }

                function getISO8601WeekNumber(date) {
                    var checkDate = new Date(date);
                    checkDate.setDate(checkDate.getDate() + 4 - (checkDate.getDay() || 7));
                    var time = checkDate.getTime();
                    return checkDate.setMonth(0), checkDate.setDate(1), Math.floor(Math.round((time - checkDate) / 864e5) / 7) + 1
                }
                scope.showWeeks = ctrl.showWeeks, ctrl.step = {
                    months: 1
                }, ctrl.element = element;
                var DAYS_IN_MONTH = [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];
                ctrl._refreshView = function() {
                    var year = ctrl.activeDate.getFullYear(),
                        month = ctrl.activeDate.getMonth(),
                        firstDayOfMonth = new Date(year, month, 1),
                        difference = ctrl.startingDay - firstDayOfMonth.getDay(),
                        numDisplayedFromPreviousMonth = difference > 0 ? 7 - difference : -difference,
                        firstDate = new Date(firstDayOfMonth);
                    numDisplayedFromPreviousMonth > 0 && firstDate.setDate(-numDisplayedFromPreviousMonth + 1);
                    for (var days = getDates(firstDate, 42), i = 0; 42 > i; i++) days[i] = angular.extend(ctrl.createDateObject(days[i], ctrl.formatDay), {
                        secondary: days[i].getMonth() !== month,
                        uid: scope.uniqueId + "-" + i
                    });
                    scope.labels = new Array(7);
                    for (var j = 0; 7 > j; j++) scope.labels[j] = {
                        abbr: dateFilter(days[j].date, ctrl.formatDayHeader),
                        full: dateFilter(days[j].date, "EEEE")
                    };
                    if (scope.title = dateFilter(ctrl.activeDate, ctrl.formatDayTitle), scope.rows = ctrl.split(days, 7), scope.showWeeks) {
                        scope.weekNumbers = [];
                        for (var weekNumber = getISO8601WeekNumber(scope.rows[0][0].date), numWeeks = scope.rows.length; scope.weekNumbers.push(weekNumber++) < numWeeks;);
                    }
                }, ctrl.compare = function(date1, date2) {
                    return new Date(date1.getFullYear(), date1.getMonth(), date1.getDate()) - new Date(date2.getFullYear(), date2.getMonth(), date2.getDate())
                }, ctrl.handleKeyDown = function(key) {
                    var date = ctrl.activeDate.getDate();
                    if ("left" === key) date -= 1;
                    else if ("up" === key) date -= 7;
                    else if ("right" === key) date += 1;
                    else if ("down" === key) date += 7;
                    else if ("pageup" === key || "pagedown" === key) {
                        var month = ctrl.activeDate.getMonth() + ("pageup" === key ? -1 : 1);
                        ctrl.activeDate.setMonth(month, 1), date = Math.min(getDaysInMonth(ctrl.activeDate.getFullYear(), ctrl.activeDate.getMonth()), date)
                    } else "home" === key ? date = 1 : "end" === key && (date = getDaysInMonth(ctrl.activeDate.getFullYear(), ctrl.activeDate.getMonth()));
                    ctrl.activeDate.setDate(date)
                }, ctrl.refreshView()
            }
        }
    }
]).directive("monthpicker", ["dateFilter",
    function(dateFilter) {
        return {
            restrict: "EA",
            replace: !0,
            templateUrl: "template/datepicker/month.html",
            require: "^datepicker",
            link: function(scope, element, attrs, ctrl) {
                ctrl.step = {
                    years: 1
                }, ctrl.element = element, ctrl._refreshView = function() {
                    for (var months = new Array(12), year = ctrl.activeDate.getFullYear(), i = 0; 12 > i; i++) months[i] = angular.extend(ctrl.createDateObject(new Date(year, i, 1), ctrl.formatMonth), {
                        uid: scope.uniqueId + "-" + i
                    });
                    scope.title = dateFilter(ctrl.activeDate, ctrl.formatMonthTitle), scope.rows = ctrl.split(months, 3)
                }, ctrl.compare = function(date1, date2) {
                    return new Date(date1.getFullYear(), date1.getMonth()) - new Date(date2.getFullYear(), date2.getMonth())
                }, ctrl.handleKeyDown = function(key) {
                    var date = ctrl.activeDate.getMonth();
                    if ("left" === key) date -= 1;
                    else if ("up" === key) date -= 3;
                    else if ("right" === key) date += 1;
                    else if ("down" === key) date += 3;
                    else if ("pageup" === key || "pagedown" === key) {
                        var year = ctrl.activeDate.getFullYear() + ("pageup" === key ? -1 : 1);
                        ctrl.activeDate.setFullYear(year)
                    } else "home" === key ? date = 0 : "end" === key && (date = 11);
                    ctrl.activeDate.setMonth(date)
                }, ctrl.refreshView()
            }
        }
    }
]).directive("yearpicker", ["dateFilter",
    function() {
        return {
            restrict: "EA",
            replace: !0,
            templateUrl: "template/datepicker/year.html",
            require: "^datepicker",
            link: function(scope, element, attrs, ctrl) {
                function getStartingYear(year) {
                    return parseInt((year - 1) / range, 10) * range + 1
                }
                var range = ctrl.yearRange;
                ctrl.step = {
                    years: range
                }, ctrl.element = element, ctrl._refreshView = function() {
                    for (var years = new Array(range), i = 0, start = getStartingYear(ctrl.activeDate.getFullYear()); range > i; i++) years[i] = angular.extend(ctrl.createDateObject(new Date(start + i, 0, 1), ctrl.formatYear), {
                        uid: scope.uniqueId + "-" + i
                    });
                    scope.title = [years[0].label, years[range - 1].label].join(" - "), scope.rows = ctrl.split(years, 5)
                }, ctrl.compare = function(date1, date2) {
                    return date1.getFullYear() - date2.getFullYear()
                }, ctrl.handleKeyDown = function(key) {
                    var date = ctrl.activeDate.getFullYear();
                    "left" === key ? date -= 1 : "up" === key ? date -= 5 : "right" === key ? date += 1 : "down" === key ? date += 5 : "pageup" === key || "pagedown" === key ? date += ("pageup" === key ? -1 : 1) * ctrl.step.years : "home" === key ? date = getStartingYear(ctrl.activeDate.getFullYear()) : "end" === key && (date = getStartingYear(ctrl.activeDate.getFullYear()) + range - 1), ctrl.activeDate.setFullYear(date)
                }, ctrl.refreshView()
            }
        }
    }
]).constant("datepickerPopupConfig", {
    datepickerPopup: "yyyy-MM-dd",
    currentText: "Today",
    clearText: "Clear",
    closeText: "Done",
    closeOnDateSelection: !0,
    appendToBody: !1,
    showButtonBar: !0
}).directive("datepickerPopup", ["$compile", "$parse", "$document", "$position", "dateFilter", "dateParser", "datepickerPopupConfig",
    function($compile, $parse, $document, $position, dateFilter, dateParser, datepickerPopupConfig) {
        return {
            restrict: "EA",
            require: "ngModel",
            scope: {
                isOpen: "=?",
                currentText: "@",
                clearText: "@",
                closeText: "@",
                dateDisabled: "&"
            },
            link: function(scope, element, attrs, ngModel) {
                function cameltoDash(string) {
                    return string.replace(/([A-Z])/g, function($1) {
                        return "-" + $1.toLowerCase()
                    })
                }

                function parseDate(viewValue) {
                    if (viewValue) {
                        if (angular.isDate(viewValue) && !isNaN(viewValue)) return ngModel.$setValidity("date", !0), viewValue;
                        if (angular.isString(viewValue)) {
                            var date = dateParser.parse(viewValue, dateFormat) || new Date(viewValue);
                            return isNaN(date) ? void ngModel.$setValidity("date", !1) : (ngModel.$setValidity("date", !0), date)
                        }
                        return void ngModel.$setValidity("date", !1)
                    }
                    return ngModel.$setValidity("date", !0), null
                }
                var dateFormat, closeOnDateSelection = angular.isDefined(attrs.closeOnDateSelection) ? scope.$parent.$eval(attrs.closeOnDateSelection) : datepickerPopupConfig.closeOnDateSelection,
                    appendToBody = angular.isDefined(attrs.datepickerAppendToBody) ? scope.$parent.$eval(attrs.datepickerAppendToBody) : datepickerPopupConfig.appendToBody;
                scope.showButtonBar = angular.isDefined(attrs.showButtonBar) ? scope.$parent.$eval(attrs.showButtonBar) : datepickerPopupConfig.showButtonBar, scope.getText = function(key) {
                    return scope[key + "Text"] || datepickerPopupConfig[key + "Text"]
                }, attrs.$observe("datepickerPopup", function(value) {
                    dateFormat = value || datepickerPopupConfig.datepickerPopup, ngModel.$render()
                });
                var popupEl = angular.element("<div datepicker-popup-wrap><div datepicker></div></div>");
                popupEl.attr({
                    "ng-model": "date",
                    "ng-change": "dateSelection()"
                });
                var datepickerEl = angular.element(popupEl.children()[0]);
                attrs.datepickerOptions && angular.forEach(scope.$parent.$eval(attrs.datepickerOptions), function(value, option) {
                    datepickerEl.attr(cameltoDash(option), value)
                }), angular.forEach(["minDate", "maxDate"], function(key) {
                    attrs[key] && (scope.$parent.$watch($parse(attrs[key]), function(value) {
                        scope[key] = value
                    }), datepickerEl.attr(cameltoDash(key), key))
                }), attrs.dateDisabled && datepickerEl.attr("date-disabled", "dateDisabled({ date: date, mode: mode })"), ngModel.$parsers.unshift(parseDate), scope.dateSelection = function(dt) {
                    angular.isDefined(dt) && (scope.date = dt), ngModel.$setViewValue(scope.date), ngModel.$render(), closeOnDateSelection && (scope.isOpen = !1, element[0].focus())
                }, element.bind("input change keyup", function() {
                    scope.$apply(function() {
                        scope.date = ngModel.$modelValue
                    })
                }), ngModel.$render = function() {
                    var date = ngModel.$viewValue ? dateFilter(ngModel.$viewValue, dateFormat) : "";
                    element.val(date), scope.date = parseDate(ngModel.$modelValue)
                };
                var documentClickBind = function(event) {
                        scope.isOpen && event.target !== element[0] && scope.$apply(function() {
                            scope.isOpen = !1
                        })
                    },
                    keydown = function(evt) {
                        scope.keydown(evt)
                    };
                element.bind("keydown", keydown), scope.keydown = function(evt) {
                    27 === evt.which ? (evt.preventDefault(), evt.stopPropagation(), scope.close()) : 40 !== evt.which || scope.isOpen || (scope.isOpen = !0)
                }, scope.$watch("isOpen", function(value) {
                    value ? (scope.$broadcast("datepicker.focus"), scope.position = appendToBody ? $position.offset(element) : $position.position(element), scope.position.top = scope.position.top + element.prop("offsetHeight"), $document.bind("click", documentClickBind)) : $document.unbind("click", documentClickBind)
                }), scope.select = function(date) {
                    if ("today" === date) {
                        var today = new Date;
                        angular.isDate(ngModel.$modelValue) ? (date = new Date(ngModel.$modelValue), date.setFullYear(today.getFullYear(), today.getMonth(), today.getDate())) : date = new Date(today.setHours(0, 0, 0, 0))
                    }
                    scope.dateSelection(date)
                }, scope.close = function() {
                    scope.isOpen = !1, element[0].focus()
                };
                var $popup = $compile(popupEl)(scope);
                appendToBody ? $document.find("body").append($popup) : element.after($popup), scope.$on("$destroy", function() {
                    $popup.remove(), element.unbind("keydown", keydown), $document.unbind("click", documentClickBind)
                })
            }
        }
    }
]).directive("datepickerPopupWrap", function() {
    return {
        restrict: "EA",
        replace: !0,
        transclude: !0,
        templateUrl: "template/datepicker/popup.html",
        link: function(scope, element) {
            element.bind("click", function(event) {
                event.preventDefault(), event.stopPropagation()
            })
        }
    }
}), angular.module("ui.bootstrap.dropdown", []).constant("dropdownConfig", {
    openClass: "open"
}).service("dropdownService", ["$document",
    function($document) {
        var openScope = null;
        this.open = function(dropdownScope) {
            openScope || ($document.bind("click", closeDropdown), $document.bind("keydown", escapeKeyBind)), openScope && openScope !== dropdownScope && (openScope.isOpen = !1), openScope = dropdownScope
        }, this.close = function(dropdownScope) {
            openScope === dropdownScope && (openScope = null, $document.unbind("click", closeDropdown), $document.unbind("keydown", escapeKeyBind))
        };
        var closeDropdown = function(evt) {
                evt && evt.isDefaultPrevented() || openScope.$apply(function() {
                    openScope.isOpen = !1
                })
            },
            escapeKeyBind = function(evt) {
                27 === evt.which && (openScope.focusToggleElement(), closeDropdown())
            }
    }
]).controller("DropdownController", ["$scope", "$attrs", "$parse", "dropdownConfig", "dropdownService", "$animate",
    function($scope, $attrs, $parse, dropdownConfig, dropdownService, $animate) {
        var getIsOpen, self = this,
            scope = $scope.$new(),
            openClass = dropdownConfig.openClass,
            setIsOpen = angular.noop,
            toggleInvoker = $attrs.onToggle ? $parse($attrs.onToggle) : angular.noop;
        this.init = function(element) {
            self.$element = element, $attrs.isOpen && (getIsOpen = $parse($attrs.isOpen), setIsOpen = getIsOpen.assign, $scope.$watch(getIsOpen, function(value) {
                scope.isOpen = !!value
            }))
        }, this.toggle = function(open) {
            return scope.isOpen = arguments.length ? !!open : !scope.isOpen
        }, this.isOpen = function() {
            return scope.isOpen
        }, scope.focusToggleElement = function() {
            self.toggleElement && self.toggleElement[0].focus()
        }, scope.$watch("isOpen", function(isOpen, wasOpen) {
            $animate[isOpen ? "addClass" : "removeClass"](self.$element, openClass), isOpen ? (scope.focusToggleElement(), dropdownService.open(scope)) : dropdownService.close(scope), setIsOpen($scope, isOpen), angular.isDefined(isOpen) && isOpen !== wasOpen && toggleInvoker($scope, {
                open: !!isOpen
            })
        }), $scope.$on("$locationChangeSuccess", function() {
            scope.isOpen = !1
        }), $scope.$on("$destroy", function() {
            scope.$destroy()
        })
    }
]).directive("dropdown", function() {
    return {
        restrict: "CA",
        controller: "DropdownController",
        link: function(scope, element, attrs, dropdownCtrl) {
            dropdownCtrl.init(element)
        }
    }
}).directive("dropdownToggle", function() {
    return {
        restrict: "CA",
        require: "?^dropdown",
        link: function(scope, element, attrs, dropdownCtrl) {
            if (dropdownCtrl) {
                dropdownCtrl.toggleElement = element;
                var toggleDropdown = function(event) {
                    event.preventDefault(), element.hasClass("disabled") || attrs.disabled || scope.$apply(function() {
                        dropdownCtrl.toggle()
                    })
                };
                element.bind("click", toggleDropdown), element.attr({
                    "aria-haspopup": !0,
                    "aria-expanded": !1
                }), scope.$watch(dropdownCtrl.isOpen, function(isOpen) {
                    element.attr("aria-expanded", !!isOpen)
                }), scope.$on("$destroy", function() {
                    element.unbind("click", toggleDropdown)
                })
            }
        }
    }
}), angular.module("ui.bootstrap.modal", ["ui.bootstrap.transition"]).factory("$$stackedMap", function() {
    return {
        createNew: function() {
            var stack = [];
            return {
                add: function(key, value) {
                    stack.push({
                        key: key,
                        value: value
                    })
                },
                get: function(key) {
                    for (var i = 0; i < stack.length; i++)
                        if (key == stack[i].key) return stack[i]
                },
                keys: function() {
                    for (var keys = [], i = 0; i < stack.length; i++) keys.push(stack[i].key);
                    return keys
                },
                top: function() {
                    return stack[stack.length - 1]
                },
                remove: function(key) {
                    for (var idx = -1, i = 0; i < stack.length; i++)
                        if (key == stack[i].key) {
                            idx = i;
                            break
                        }
                    return stack.splice(idx, 1)[0]
                },
                removeTop: function() {
                    return stack.splice(stack.length - 1, 1)[0]
                },
                length: function() {
                    return stack.length
                }
            }
        }
    }
}).directive("modalBackdrop", ["$timeout",
    function($timeout) {
        return {
            restrict: "EA",
            replace: !0,
            templateUrl: "template/modal/backdrop.html",
            link: function(scope) {
                scope.animate = !1, $timeout(function() {
                    scope.animate = !0
                })
            }
        }
    }
]).directive("modalWindow", ["$modalStack", "$timeout",
    function($modalStack, $timeout) {
        return {
            restrict: "EA",
            scope: {
                index: "@",
                animate: "="
            },
            replace: !0,
            transclude: !0,
            templateUrl: function(tElement, tAttrs) {
                return tAttrs.templateUrl || "template/modal/window.html"
            },
            link: function(scope, element, attrs) {
                element.addClass(attrs.windowClass || ""), scope.size = attrs.size, $timeout(function() {
                    scope.animate = !0, element[0].focus()
                }), scope.close = function(evt) {
                    var modal = $modalStack.getTop();
                    modal && modal.value.backdrop && "static" != modal.value.backdrop && evt.target === evt.currentTarget && (evt.preventDefault(), evt.stopPropagation(), $modalStack.dismiss(modal.key, "backdrop click"))
                }
            }
        }
    }
]).factory("$modalStack", ["$transition", "$timeout", "$document", "$compile", "$rootScope", "$$stackedMap",
    function($transition, $timeout, $document, $compile, $rootScope, $$stackedMap) {
        function backdropIndex() {
            for (var topBackdropIndex = -1, opened = openedWindows.keys(), i = 0; i < opened.length; i++) openedWindows.get(opened[i]).value.backdrop && (topBackdropIndex = i);
            return topBackdropIndex
        }

        function removeModalWindow(modalInstance) {
            var body = $document.find("body").eq(0),
                modalWindow = openedWindows.get(modalInstance).value;
            openedWindows.remove(modalInstance), removeAfterAnimate(modalWindow.modalDomEl, modalWindow.modalScope, 300, function() {
                modalWindow.modalScope.$destroy(), body.toggleClass(OPENED_MODAL_CLASS, openedWindows.length() > 0), checkRemoveBackdrop()
            })
        }

        function checkRemoveBackdrop() {
            if (backdropDomEl && -1 == backdropIndex()) {
                var backdropScopeRef = backdropScope;
                removeAfterAnimate(backdropDomEl, backdropScope, 150, function() {
                    backdropScopeRef.$destroy(), backdropScopeRef = null
                }), backdropDomEl = void 0, backdropScope = void 0
            }
        }

        function removeAfterAnimate(domEl, scope, emulateTime, done) {
            function afterAnimating() {
                afterAnimating.done || (afterAnimating.done = !0, domEl.remove(), done && done())
            }
            scope.animate = !1;
            var transitionEndEventName = $transition.transitionEndEventName;
            if (transitionEndEventName) {
                var timeout = $timeout(afterAnimating, emulateTime);
                domEl.bind(transitionEndEventName, function() {
                    $timeout.cancel(timeout), afterAnimating(), scope.$apply()
                })
            } else $timeout(afterAnimating, 0)
        }
        var backdropDomEl, backdropScope, OPENED_MODAL_CLASS = "modal-open",
            openedWindows = $$stackedMap.createNew(),
            $modalStack = {};
        return $rootScope.$watch(backdropIndex, function(newBackdropIndex) {
            backdropScope && (backdropScope.index = newBackdropIndex)
        }), $document.bind("keydown", function(evt) {
            var modal;
            27 === evt.which && (modal = openedWindows.top(), modal && modal.value.keyboard && (evt.preventDefault(), $rootScope.$apply(function() {
                $modalStack.dismiss(modal.key, "escape key press")
            })))
        }), $modalStack.open = function(modalInstance, modal) {
            openedWindows.add(modalInstance, {
                deferred: modal.deferred,
                modalScope: modal.scope,
                backdrop: modal.backdrop,
                keyboard: modal.keyboard
            });
            var body = $document.find("body").eq(0),
                currBackdropIndex = backdropIndex();
            currBackdropIndex >= 0 && !backdropDomEl && (backdropScope = $rootScope.$new(!0), backdropScope.index = currBackdropIndex, backdropDomEl = $compile("<div modal-backdrop></div>")(backdropScope), body.append(backdropDomEl));
            var angularDomEl = angular.element("<div modal-window></div>");
            angularDomEl.attr({
                "template-url": modal.windowTemplateUrl,
                "window-class": modal.windowClass,
                size: modal.size,
                index: openedWindows.length() - 1,
                animate: "animate"
            }).html(modal.content);
            var modalDomEl = $compile(angularDomEl)(modal.scope);
            openedWindows.top().value.modalDomEl = modalDomEl, body.append(modalDomEl), body.addClass(OPENED_MODAL_CLASS)
        }, $modalStack.close = function(modalInstance, result) {
            var modalWindow = openedWindows.get(modalInstance).value;
            modalWindow && (modalWindow.deferred.resolve(result), removeModalWindow(modalInstance))
        }, $modalStack.dismiss = function(modalInstance, reason) {
            var modalWindow = openedWindows.get(modalInstance).value;
            modalWindow && (modalWindow.deferred.reject(reason), removeModalWindow(modalInstance))
        }, $modalStack.dismissAll = function(reason) {
            for (var topModal = this.getTop(); topModal;) this.dismiss(topModal.key, reason), topModal = this.getTop()
        }, $modalStack.getTop = function() {
            return openedWindows.top()
        }, $modalStack
    }
]).provider("$modal", function() {
    var $modalProvider = {
        options: {
            backdrop: !0,
            keyboard: !0
        },
        $get: ["$injector", "$rootScope", "$q", "$http", "$templateCache", "$controller", "$modalStack",
            function($injector, $rootScope, $q, $http, $templateCache, $controller, $modalStack) {
                function getTemplatePromise(options) {
                    return options.template ? $q.when(options.template) : $http.get(options.templateUrl, {
                        cache: $templateCache
                    }).then(function(result) {
                        return result.data
                    })
                }

                function getResolvePromises(resolves) {
                    var promisesArr = [];
                    return angular.forEach(resolves, function(value) {
                        (angular.isFunction(value) || angular.isArray(value)) && promisesArr.push($q.when($injector.invoke(value)))
                    }), promisesArr
                }
                var $modal = {};
                return $modal.open = function(modalOptions) {
                    var modalResultDeferred = $q.defer(),
                        modalOpenedDeferred = $q.defer(),
                        modalInstance = {
                            result: modalResultDeferred.promise,
                            opened: modalOpenedDeferred.promise,
                            close: function(result) {
                                $modalStack.close(modalInstance, result)
                            },
                            dismiss: function(reason) {
                                $modalStack.dismiss(modalInstance, reason)
                            }
                        };
                    if (modalOptions = angular.extend({}, $modalProvider.options, modalOptions), modalOptions.resolve = modalOptions.resolve || {}, !modalOptions.template && !modalOptions.templateUrl) throw new Error("One of template or templateUrl options is required.");
                    var templateAndResolvePromise = $q.all([getTemplatePromise(modalOptions)].concat(getResolvePromises(modalOptions.resolve)));
                    return templateAndResolvePromise.then(function(tplAndVars) {
                        var modalScope = (modalOptions.scope || $rootScope).$new();
                        modalScope.$close = modalInstance.close, modalScope.$dismiss = modalInstance.dismiss;
                        var ctrlInstance, ctrlLocals = {},
                            resolveIter = 1;
                        modalOptions.controller && (ctrlLocals.$scope = modalScope, ctrlLocals.$modalInstance = modalInstance, angular.forEach(modalOptions.resolve, function(value, key) {
                            ctrlLocals[key] = tplAndVars[resolveIter++]
                        }), ctrlInstance = $controller(modalOptions.controller, ctrlLocals)), $modalStack.open(modalInstance, {
                            scope: modalScope,
                            deferred: modalResultDeferred,
                            content: tplAndVars[0],
                            backdrop: modalOptions.backdrop,
                            keyboard: modalOptions.keyboard,
                            windowClass: modalOptions.windowClass,
                            windowTemplateUrl: modalOptions.windowTemplateUrl,
                            size: modalOptions.size
                        })
                    }, function(reason) {
                        modalResultDeferred.reject(reason)
                    }), templateAndResolvePromise.then(function() {
                        modalOpenedDeferred.resolve(!0)
                    }, function() {
                        modalOpenedDeferred.reject(!1)
                    }), modalInstance
                }, $modal
            }
        ]
    };
    return $modalProvider
}), angular.module("ui.bootstrap.pagination", []).controller("PaginationController", ["$scope", "$attrs", "$parse",
    function($scope, $attrs, $parse) {
        var self = this,
            ngModelCtrl = {
                $setViewValue: angular.noop
            },
            setNumPages = $attrs.numPages ? $parse($attrs.numPages).assign : angular.noop;
        this.init = function(ngModelCtrl_, config) {
            ngModelCtrl = ngModelCtrl_, this.config = config, ngModelCtrl.$render = function() {
                self.render()
            }, $attrs.itemsPerPage ? $scope.$parent.$watch($parse($attrs.itemsPerPage), function(value) {
                self.itemsPerPage = parseInt(value, 10), $scope.totalPages = self.calculateTotalPages()
            }) : this.itemsPerPage = config.itemsPerPage
        }, this.calculateTotalPages = function() {
            var totalPages = this.itemsPerPage < 1 ? 1 : Math.ceil($scope.totalItems / this.itemsPerPage);
            return Math.max(totalPages || 0, 1)
        }, this.render = function() {
            $scope.page = parseInt(ngModelCtrl.$viewValue, 10) || 1
        }, $scope.selectPage = function(page) {
            $scope.page !== page && page > 0 && page <= $scope.totalPages && (ngModelCtrl.$setViewValue(page), ngModelCtrl.$render())
        }, $scope.getText = function(key) {
            return $scope[key + "Text"] || self.config[key + "Text"]
        }, $scope.noPrevious = function() {
            return 1 === $scope.page
        }, $scope.noNext = function() {
            return $scope.page === $scope.totalPages
        }, $scope.$watch("totalItems", function() {
            $scope.totalPages = self.calculateTotalPages()
        }), $scope.$watch("totalPages", function(value) {
            setNumPages($scope.$parent, value), $scope.page > value ? $scope.selectPage(value) : ngModelCtrl.$render()
        })
    }
]).constant("paginationConfig", {
    itemsPerPage: 10,
    boundaryLinks: !1,
    directionLinks: !0,
    firstText: "First",
    previousText: "Previous",
    nextText: "Next",
    lastText: "Last",
    rotate: !0
}).directive("pagination", ["$parse", "paginationConfig",
    function($parse, paginationConfig) {
        return {
            restrict: "EA",
            scope: {
                totalItems: "=",
                firstText: "@",
                previousText: "@",
                nextText: "@",
                lastText: "@"
            },
            require: ["pagination", "?ngModel"],
            controller: "PaginationController",
            templateUrl: "template/pagination/pagination.html",
            replace: !0,
            link: function(scope, element, attrs, ctrls) {
                function makePage(number, text, isActive) {
                    return {
                        number: number,
                        text: text,
                        active: isActive
                    }
                }

                function getPages(currentPage, totalPages) {
                    var pages = [],
                        startPage = 1,
                        endPage = totalPages,
                        isMaxSized = angular.isDefined(maxSize) && totalPages > maxSize;
                    isMaxSized && (rotate ? (startPage = Math.max(currentPage - Math.floor(maxSize / 2), 1), endPage = startPage + maxSize - 1, endPage > totalPages && (endPage = totalPages, startPage = endPage - maxSize + 1)) : (startPage = (Math.ceil(currentPage / maxSize) - 1) * maxSize + 1, endPage = Math.min(startPage + maxSize - 1, totalPages)));
                    for (var number = startPage; endPage >= number; number++) {
                        var page = makePage(number, number, number === currentPage);
                        pages.push(page)
                    }
                    if (isMaxSized && !rotate) {
                        if (startPage > 1) {
                            var previousPageSet = makePage(startPage - 1, "...", !1);
                            pages.unshift(previousPageSet)
                        }
                        if (totalPages > endPage) {
                            var nextPageSet = makePage(endPage + 1, "...", !1);
                            pages.push(nextPageSet)
                        }
                    }
                    return pages
                }
                var paginationCtrl = ctrls[0],
                    ngModelCtrl = ctrls[1];
                if (ngModelCtrl) {
                    var maxSize = angular.isDefined(attrs.maxSize) ? scope.$parent.$eval(attrs.maxSize) : paginationConfig.maxSize,
                        rotate = angular.isDefined(attrs.rotate) ? scope.$parent.$eval(attrs.rotate) : paginationConfig.rotate;
                    scope.boundaryLinks = angular.isDefined(attrs.boundaryLinks) ? scope.$parent.$eval(attrs.boundaryLinks) : paginationConfig.boundaryLinks, scope.directionLinks = angular.isDefined(attrs.directionLinks) ? scope.$parent.$eval(attrs.directionLinks) : paginationConfig.directionLinks, paginationCtrl.init(ngModelCtrl, paginationConfig), attrs.maxSize && scope.$parent.$watch($parse(attrs.maxSize), function(value) {
                        maxSize = parseInt(value, 10), paginationCtrl.render()
                    });
                    var originalRender = paginationCtrl.render;
                    paginationCtrl.render = function() {
                        originalRender(), scope.page > 0 && scope.page <= scope.totalPages && (scope.pages = getPages(scope.page, scope.totalPages))
                    }
                }
            }
        }
    }
]).constant("pagerConfig", {
    itemsPerPage: 10,
    previousText: "« Previous",
    nextText: "Next »",
    align: !0
}).directive("pager", ["pagerConfig",
    function(pagerConfig) {
        return {
            restrict: "EA",
            scope: {
                totalItems: "=",
                previousText: "@",
                nextText: "@"
            },
            require: ["pager", "?ngModel"],
            controller: "PaginationController",
            templateUrl: "template/pagination/pager.html",
            replace: !0,
            link: function(scope, element, attrs, ctrls) {
                var paginationCtrl = ctrls[0],
                    ngModelCtrl = ctrls[1];
                ngModelCtrl && (scope.align = angular.isDefined(attrs.align) ? scope.$parent.$eval(attrs.align) : pagerConfig.align, paginationCtrl.init(ngModelCtrl, pagerConfig))
            }
        }
    }
]), angular.module("ui.bootstrap.tooltip", ["ui.bootstrap.position", "ui.bootstrap.bindHtml"]).provider("$tooltip", function() {
    function snake_case(name) {
        var regexp = /[A-Z]/g,
            separator = "-";
        return name.replace(regexp, function(letter, pos) {
            return (pos ? separator : "") + letter.toLowerCase()
        })
    }
    var defaultOptions = {
            placement: "top",
            animation: !0,
            popupDelay: 0
        },
        triggerMap = {
            mouseenter: "mouseleave",
            click: "click",
            focus: "blur"
        },
        globalOptions = {};
    this.options = function(value) {
        angular.extend(globalOptions, value)
    }, this.setTriggers = function(triggers) {
        angular.extend(triggerMap, triggers)
    }, this.$get = ["$window", "$compile", "$timeout", "$parse", "$document", "$position", "$interpolate",
        function($window, $compile, $timeout, $parse, $document, $position, $interpolate) {
            return function(type, prefix, defaultTriggerShow) {
                function getTriggers(trigger) {
                    var show = trigger || options.trigger || defaultTriggerShow,
                        hide = triggerMap[show] || show;
                    return {
                        show: show,
                        hide: hide
                    }
                }
                var options = angular.extend({}, defaultOptions, globalOptions),
                    directiveName = snake_case(type),
                    startSym = $interpolate.startSymbol(),
                    endSym = $interpolate.endSymbol(),
                    template = "<div " + directiveName + '-popup title="' + startSym + "tt_title" + endSym + '" content="' + startSym + "tt_content" + endSym + '" placement="' + startSym + "tt_placement" + endSym + '" animation="tt_animation" is-open="tt_isOpen"></div>';
                return {
                    restrict: "EA",
                    scope: !0,
                    compile: function() {
                        var tooltipLinker = $compile(template);
                        return function(scope, element, attrs) {
                            function toggleTooltipBind() {
                                scope.tt_isOpen ? hideTooltipBind() : showTooltipBind()
                            }

                            function showTooltipBind() {
                                (!hasEnableExp || scope.$eval(attrs[prefix + "Enable"])) && (scope.tt_popupDelay ? popupTimeout || (popupTimeout = $timeout(show, scope.tt_popupDelay, !1), popupTimeout.then(function(reposition) {
                                    reposition()
                                })) : show()())
                            }

                            function hideTooltipBind() {
                                scope.$apply(function() {
                                    hide()
                                })
                            }

                            function show() {
                                return popupTimeout = null, transitionTimeout && ($timeout.cancel(transitionTimeout), transitionTimeout = null), scope.tt_content ? (createTooltip(), tooltip.css({
                                    top: 0,
                                    left: 0,
                                    display: "block"
                                }), appendToBody ? $document.find("body").append(tooltip) : element.after(tooltip), positionTooltip(), scope.tt_isOpen = !0, scope.$digest(), positionTooltip) : angular.noop
                            }

                            function hide() {
                                scope.tt_isOpen = !1, $timeout.cancel(popupTimeout), popupTimeout = null, scope.tt_animation ? transitionTimeout || (transitionTimeout = $timeout(removeTooltip, 500)) : removeTooltip()
                            }

                            function createTooltip() {
                                tooltip && removeTooltip(), tooltip = tooltipLinker(scope, function() {}), scope.$digest()
                            }

                            function removeTooltip() {
                                transitionTimeout = null, tooltip && (tooltip.remove(), tooltip = null)
                            }
                            var tooltip, transitionTimeout, popupTimeout, appendToBody = angular.isDefined(options.appendToBody) ? options.appendToBody : !1,
                                triggers = getTriggers(void 0),
                                hasEnableExp = angular.isDefined(attrs[prefix + "Enable"]),
                                positionTooltip = function() {
                                    var ttPosition = $position.positionElements(element, tooltip, scope.tt_placement, appendToBody);
                                    ttPosition.top += "px", ttPosition.left += "px", tooltip.css(ttPosition)
                                };
                            scope.tt_isOpen = !1, attrs.$observe(type, function(val) {
                                scope.tt_content = val, !val && scope.tt_isOpen && hide()
                            }), attrs.$observe(prefix + "Title", function(val) {
                                scope.tt_title = val
                            }), attrs.$observe(prefix + "Placement", function(val) {
                                scope.tt_placement = angular.isDefined(val) ? val : options.placement
                            }), attrs.$observe(prefix + "PopupDelay", function(val) {
                                var delay = parseInt(val, 10);
                                scope.tt_popupDelay = isNaN(delay) ? options.popupDelay : delay
                            });
                            var unregisterTriggers = function() {
                                element.unbind(triggers.show, showTooltipBind), element.unbind(triggers.hide, hideTooltipBind)
                            };
                            attrs.$observe(prefix + "Trigger", function(val) {
                                unregisterTriggers(), triggers = getTriggers(val), triggers.show === triggers.hide ? element.bind(triggers.show, toggleTooltipBind) : (element.bind(triggers.show, showTooltipBind), element.bind(triggers.hide, hideTooltipBind))
                            });
                            var animation = scope.$eval(attrs[prefix + "Animation"]);
                            scope.tt_animation = angular.isDefined(animation) ? !!animation : options.animation, attrs.$observe(prefix + "AppendToBody", function(val) {
                                appendToBody = angular.isDefined(val) ? $parse(val)(scope) : appendToBody
                            }), appendToBody && scope.$on("$locationChangeSuccess", function() {
                                scope.tt_isOpen && hide()
                            }), scope.$on("$destroy", function() {
                                $timeout.cancel(transitionTimeout), $timeout.cancel(popupTimeout), unregisterTriggers(), removeTooltip()
                            })
                        }
                    }
                }
            }
        }
    ]
}).directive("tooltipPopup", function() {
    return {
        restrict: "EA",
        replace: !0,
        scope: {
            content: "@",
            placement: "@",
            animation: "&",
            isOpen: "&"
        },
        templateUrl: "template/tooltip/tooltip-popup.html"
    }
}).directive("tooltip", ["$tooltip",
    function($tooltip) {
        return $tooltip("tooltip", "tooltip", "mouseenter")
    }
]).directive("tooltipHtmlUnsafePopup", function() {
    return {
        restrict: "EA",
        replace: !0,
        scope: {
            content: "@",
            placement: "@",
            animation: "&",
            isOpen: "&"
        },
        templateUrl: "template/tooltip/tooltip-html-unsafe-popup.html"
    }
}).directive("tooltipHtmlUnsafe", ["$tooltip",
    function($tooltip) {
        return $tooltip("tooltipHtmlUnsafe", "tooltip", "mouseenter")
    }
]), angular.module("ui.bootstrap.popover", ["ui.bootstrap.tooltip"]).directive("popoverPopup", function() {
    return {
        restrict: "EA",
        replace: !0,
        scope: {
            title: "@",
            content: "@",
            placement: "@",
            animation: "&",
            isOpen: "&"
        },
        templateUrl: "template/popover/popover.html"
    }
}).directive("popover", ["$tooltip",
    function($tooltip) {
        return $tooltip("popover", "popover", "click")
    }
]), angular.module("ui.bootstrap.progressbar", []).constant("progressConfig", {
    animate: !0,
    max: 100
}).controller("ProgressController", ["$scope", "$attrs", "progressConfig",
    function($scope, $attrs, progressConfig) {
        var self = this,
            animate = angular.isDefined($attrs.animate) ? $scope.$parent.$eval($attrs.animate) : progressConfig.animate;
        this.bars = [], $scope.max = angular.isDefined($attrs.max) ? $scope.$parent.$eval($attrs.max) : progressConfig.max, this.addBar = function(bar, element) {
            animate || element.css({
                transition: "none"
            }), this.bars.push(bar), bar.$watch("value", function(value) {
                bar.percent = +(100 * value / $scope.max).toFixed(2)
            }), bar.$on("$destroy", function() {
                element = null, self.removeBar(bar)
            })
        }, this.removeBar = function(bar) {
            this.bars.splice(this.bars.indexOf(bar), 1)
        }
    }
]).directive("progress", function() {
    return {
        restrict: "EA",
        replace: !0,
        transclude: !0,
        controller: "ProgressController",
        require: "progress",
        scope: {},
        templateUrl: "template/progressbar/progress.html"
    }
}).directive("bar", function() {
    return {
        restrict: "EA",
        replace: !0,
        transclude: !0,
        require: "^progress",
        scope: {
            value: "=",
            type: "@"
        },
        templateUrl: "template/progressbar/bar.html",
        link: function(scope, element, attrs, progressCtrl) {
            progressCtrl.addBar(scope, element)
        }
    }
}).directive("progressbar", function() {
    return {
        restrict: "EA",
        replace: !0,
        transclude: !0,
        controller: "ProgressController",
        scope: {
            value: "=",
            type: "@"
        },
        templateUrl: "template/progressbar/progressbar.html",
        link: function(scope, element, attrs, progressCtrl) {
            progressCtrl.addBar(scope, angular.element(element.children()[0]))
        }
    }
}), angular.module("ui.bootstrap.rating", []).constant("ratingConfig", {
    max: 5,
    stateOn: null,
    stateOff: null
}).controller("RatingController", ["$scope", "$attrs", "ratingConfig",
    function($scope, $attrs, ratingConfig) {
        var ngModelCtrl = {
            $setViewValue: angular.noop
        };
        this.init = function(ngModelCtrl_) {
            ngModelCtrl = ngModelCtrl_, ngModelCtrl.$render = this.render, this.stateOn = angular.isDefined($attrs.stateOn) ? $scope.$parent.$eval($attrs.stateOn) : ratingConfig.stateOn, this.stateOff = angular.isDefined($attrs.stateOff) ? $scope.$parent.$eval($attrs.stateOff) : ratingConfig.stateOff;
            var ratingStates = angular.isDefined($attrs.ratingStates) ? $scope.$parent.$eval($attrs.ratingStates) : new Array(angular.isDefined($attrs.max) ? $scope.$parent.$eval($attrs.max) : ratingConfig.max);
            $scope.range = this.buildTemplateObjects(ratingStates)
        }, this.buildTemplateObjects = function(states) {
            for (var i = 0, n = states.length; n > i; i++) states[i] = angular.extend({
                index: i
            }, {
                stateOn: this.stateOn,
                stateOff: this.stateOff
            }, states[i]);
            return states
        }, $scope.rate = function(value) {
            !$scope.readonly && value >= 0 && value <= $scope.range.length && (ngModelCtrl.$setViewValue(value), ngModelCtrl.$render())
        }, $scope.enter = function(value) {
            $scope.readonly || ($scope.value = value), $scope.onHover({
                value: value
            })
        }, $scope.reset = function() {
            $scope.value = ngModelCtrl.$viewValue, $scope.onLeave()
        }, $scope.onKeydown = function(evt) {
            /(37|38|39|40)/.test(evt.which) && (evt.preventDefault(), evt.stopPropagation(), $scope.rate($scope.value + (38 === evt.which || 39 === evt.which ? 1 : -1)))
        }, this.render = function() {
            $scope.value = ngModelCtrl.$viewValue
        }
    }
]).directive("rating", function() {
    return {
        restrict: "EA",
        require: ["rating", "ngModel"],
        scope: {
            readonly: "=?",
            onHover: "&",
            onLeave: "&"
        },
        controller: "RatingController",
        templateUrl: "template/rating/rating.html",
        replace: !0,
        link: function(scope, element, attrs, ctrls) {
            var ratingCtrl = ctrls[0],
                ngModelCtrl = ctrls[1];
            ngModelCtrl && ratingCtrl.init(ngModelCtrl)
        }
    }
}), angular.module("ui.bootstrap.tabs", []).controller("TabsetController", ["$scope",
    function($scope) {
        var ctrl = this,
            tabs = ctrl.tabs = $scope.tabs = [];
        ctrl.select = function(selectedTab) {
            angular.forEach(tabs, function(tab) {
                tab.active && tab !== selectedTab && (tab.active = !1, tab.onDeselect())
            }), selectedTab.active = !0, selectedTab.onSelect()
        }, ctrl.addTab = function(tab) {
            tabs.push(tab), 1 === tabs.length ? tab.active = !0 : tab.active && ctrl.select(tab)
        }, ctrl.removeTab = function(tab) {
            var index = tabs.indexOf(tab);
            if (tab.active && tabs.length > 1) {
                var newActiveIndex = index == tabs.length - 1 ? index - 1 : index + 1;
                ctrl.select(tabs[newActiveIndex])
            }
            tabs.splice(index, 1)
        }
    }
]).directive("tabset", function() {
    return {
        restrict: "EA",
        transclude: !0,
        replace: !0,
        scope: {
            type: "@"
        },
        controller: "TabsetController",
        templateUrl: "template/tabs/tabset.html",
        link: function(scope, element, attrs) {
            scope.vertical = angular.isDefined(attrs.vertical) ? scope.$parent.$eval(attrs.vertical) : !1, scope.justified = angular.isDefined(attrs.justified) ? scope.$parent.$eval(attrs.justified) : !1
        }
    }
}).directive("tab", ["$parse",
    function($parse) {
        return {
            require: "^tabset",
            restrict: "EA",
            replace: !0,
            templateUrl: "template/tabs/tab.html",
            transclude: !0,
            scope: {
                active: "=?",
                heading: "@",
                onSelect: "&select",
                onDeselect: "&deselect"
            },
            controller: function() {},
            compile: function(elm, attrs, transclude) {
                return function(scope, elm, attrs, tabsetCtrl) {
                    scope.$watch("active", function(active) {
                        active && tabsetCtrl.select(scope)
                    }), scope.disabled = !1, attrs.disabled && scope.$parent.$watch($parse(attrs.disabled), function(value) {
                        scope.disabled = !!value
                    }), scope.select = function() {
                        scope.disabled || (scope.active = !0)
                    }, tabsetCtrl.addTab(scope), scope.$on("$destroy", function() {
                        tabsetCtrl.removeTab(scope)
                    }), scope.$transcludeFn = transclude
                }
            }
        }
    }
]).directive("tabHeadingTransclude", [
    function() {
        return {
            restrict: "A",
            require: "^tab",
            link: function(scope, elm) {
                scope.$watch("headingElement", function(heading) {
                    heading && (elm.html(""), elm.append(heading))
                })
            }
        }
    }
]).directive("tabContentTransclude", function() {
    function isTabHeading(node) {
        return node.tagName && (node.hasAttribute("tab-heading") || node.hasAttribute("data-tab-heading") || "tab-heading" === node.tagName.toLowerCase() || "data-tab-heading" === node.tagName.toLowerCase())
    }
    return {
        restrict: "A",
        require: "^tabset",
        link: function(scope, elm, attrs) {
            var tab = scope.$eval(attrs.tabContentTransclude);
            tab.$transcludeFn(tab.$parent, function(contents) {
                angular.forEach(contents, function(node) {
                    isTabHeading(node) ? tab.headingElement = node : elm.append(node)
                })
            })
        }
    }
}), angular.module("ui.bootstrap.timepicker", []).constant("timepickerConfig", {
    hourStep: 1,
    minuteStep: 1,
    showMeridian: !0,
    meridians: null,
    readonlyInput: !1,
    mousewheel: !0
}).controller("TimepickerController", ["$scope", "$attrs", "$parse", "$log", "$locale", "timepickerConfig",
    function($scope, $attrs, $parse, $log, $locale, timepickerConfig) {
        function getHoursFromTemplate() {
            var hours = parseInt($scope.hours, 10),
                valid = $scope.showMeridian ? hours > 0 && 13 > hours : hours >= 0 && 24 > hours;
            return valid ? ($scope.showMeridian && (12 === hours && (hours = 0), $scope.meridian === meridians[1] && (hours += 12)), hours) : void 0
        }

        function getMinutesFromTemplate() {
            var minutes = parseInt($scope.minutes, 10);
            return minutes >= 0 && 60 > minutes ? minutes : void 0
        }

        function pad(value) {
            return angular.isDefined(value) && value.toString().length < 2 ? "0" + value : value
        }

        function refresh(keyboardChange) {
            makeValid(), ngModelCtrl.$setViewValue(new Date(selected)), updateTemplate(keyboardChange)
        }

        function makeValid() {
            ngModelCtrl.$setValidity("time", !0), $scope.invalidHours = !1, $scope.invalidMinutes = !1
        }

        function updateTemplate(keyboardChange) {
            var hours = selected.getHours(),
                minutes = selected.getMinutes();
            $scope.showMeridian && (hours = 0 === hours || 12 === hours ? 12 : hours % 12), $scope.hours = "h" === keyboardChange ? hours : pad(hours), $scope.minutes = "m" === keyboardChange ? minutes : pad(minutes), $scope.meridian = selected.getHours() < 12 ? meridians[0] : meridians[1]
        }

        function addMinutes(minutes) {
            var dt = new Date(selected.getTime() + 6e4 * minutes);
            selected.setHours(dt.getHours(), dt.getMinutes()), refresh()
        }
        var selected = new Date,
            ngModelCtrl = {
                $setViewValue: angular.noop
            },
            meridians = angular.isDefined($attrs.meridians) ? $scope.$parent.$eval($attrs.meridians) : timepickerConfig.meridians || $locale.DATETIME_FORMATS.AMPMS;
        this.init = function(ngModelCtrl_, inputs) {
            ngModelCtrl = ngModelCtrl_, ngModelCtrl.$render = this.render;
            var hoursInputEl = inputs.eq(0),
                minutesInputEl = inputs.eq(1),
                mousewheel = angular.isDefined($attrs.mousewheel) ? $scope.$parent.$eval($attrs.mousewheel) : timepickerConfig.mousewheel;
            mousewheel && this.setupMousewheelEvents(hoursInputEl, minutesInputEl), $scope.readonlyInput = angular.isDefined($attrs.readonlyInput) ? $scope.$parent.$eval($attrs.readonlyInput) : timepickerConfig.readonlyInput, this.setupInputEvents(hoursInputEl, minutesInputEl)
        };
        var hourStep = timepickerConfig.hourStep;
        $attrs.hourStep && $scope.$parent.$watch($parse($attrs.hourStep), function(value) {
            hourStep = parseInt(value, 10)
        });
        var minuteStep = timepickerConfig.minuteStep;
        $attrs.minuteStep && $scope.$parent.$watch($parse($attrs.minuteStep), function(value) {
            minuteStep = parseInt(value, 10)
        }), $scope.showMeridian = timepickerConfig.showMeridian, $attrs.showMeridian && $scope.$parent.$watch($parse($attrs.showMeridian), function(value) {
            if ($scope.showMeridian = !!value, ngModelCtrl.$error.time) {
                var hours = getHoursFromTemplate(),
                    minutes = getMinutesFromTemplate();
                angular.isDefined(hours) && angular.isDefined(minutes) && (selected.setHours(hours), refresh())
            } else updateTemplate()
        }), this.setupMousewheelEvents = function(hoursInputEl, minutesInputEl) {
            var isScrollingUp = function(e) {
                e.originalEvent && (e = e.originalEvent);
                var delta = e.wheelDelta ? e.wheelDelta : -e.deltaY;
                return e.detail || delta > 0
            };
            hoursInputEl.bind("mousewheel wheel", function(e) {
                $scope.$apply(isScrollingUp(e) ? $scope.incrementHours() : $scope.decrementHours()), e.preventDefault()
            }), minutesInputEl.bind("mousewheel wheel", function(e) {
                $scope.$apply(isScrollingUp(e) ? $scope.incrementMinutes() : $scope.decrementMinutes()), e.preventDefault()
            })
        }, this.setupInputEvents = function(hoursInputEl, minutesInputEl) {
            if ($scope.readonlyInput) return $scope.updateHours = angular.noop, void($scope.updateMinutes = angular.noop);
            var invalidate = function(invalidHours, invalidMinutes) {
                ngModelCtrl.$setViewValue(null), ngModelCtrl.$setValidity("time", !1), angular.isDefined(invalidHours) && ($scope.invalidHours = invalidHours), angular.isDefined(invalidMinutes) && ($scope.invalidMinutes = invalidMinutes)
            };
            $scope.updateHours = function() {
                var hours = getHoursFromTemplate();
                angular.isDefined(hours) ? (selected.setHours(hours), refresh("h")) : invalidate(!0)
            }, hoursInputEl.bind("blur", function() {
                !$scope.invalidHours && $scope.hours < 10 && $scope.$apply(function() {
                    $scope.hours = pad($scope.hours)
                })
            }), $scope.updateMinutes = function() {
                var minutes = getMinutesFromTemplate();
                angular.isDefined(minutes) ? (selected.setMinutes(minutes), refresh("m")) : invalidate(void 0, !0)
            }, minutesInputEl.bind("blur", function() {
                !$scope.invalidMinutes && $scope.minutes < 10 && $scope.$apply(function() {
                    $scope.minutes = pad($scope.minutes)
                })
            })
        }, this.render = function() {
            var date = ngModelCtrl.$modelValue ? new Date(ngModelCtrl.$modelValue) : null;
            isNaN(date) ? (ngModelCtrl.$setValidity("time", !1), $log.error('Timepicker directive: "ng-model" value must be a Date object, a number of milliseconds since 01.01.1970 or a string representing an RFC2822 or ISO 8601 date.')) : (date && (selected = date), makeValid(), updateTemplate())
        }, $scope.incrementHours = function() {
            addMinutes(60 * hourStep)
        }, $scope.decrementHours = function() {
            addMinutes(60 * -hourStep)
        }, $scope.incrementMinutes = function() {
            addMinutes(minuteStep)
        }, $scope.decrementMinutes = function() {
            addMinutes(-minuteStep)
        }, $scope.toggleMeridian = function() {
            addMinutes(720 * (selected.getHours() < 12 ? 1 : -1))
        }
    }
]).directive("timepicker", function() {
    return {
        restrict: "EA",
        require: ["timepicker", "?^ngModel"],
        controller: "TimepickerController",
        replace: !0,
        scope: {},
        templateUrl: "template/timepicker/timepicker.html",
        link: function(scope, element, attrs, ctrls) {
            var timepickerCtrl = ctrls[0],
                ngModelCtrl = ctrls[1];
            ngModelCtrl && timepickerCtrl.init(ngModelCtrl, element.find("input"))
        }
    }
}), angular.module("ui.bootstrap.typeahead", ["ui.bootstrap.position", "ui.bootstrap.bindHtml"]).factory("typeaheadParser", ["$parse",
    function($parse) {
        var TYPEAHEAD_REGEXP = /^\s*(.*?)(?:\s+as\s+(.*?))?\s+for\s+(?:([\$\w][\$\w\d]*))\s+in\s+(.*)$/;
        return {
            parse: function(input) {
                var match = input.match(TYPEAHEAD_REGEXP);
                if (!match) throw new Error('Expected typeahead specification in form of "_modelValue_ (as _label_)? for _item_ in _collection_" but got "' + input + '".');
                return {
                    itemName: match[3],
                    source: $parse(match[4]),
                    viewMapper: $parse(match[2] || match[1]),
                    modelMapper: $parse(match[1])
                }
            }
        }
    }
]).directive("typeahead", ["$compile", "$parse", "$q", "$timeout", "$document", "$position", "typeaheadParser",
    function($compile, $parse, $q, $timeout, $document, $position, typeaheadParser) {
        var HOT_KEYS = [9, 13, 27, 38, 40];
        return {
            require: "ngModel",
            link: function(originalScope, element, attrs, modelCtrl) {
                var hasFocus, minSearch = originalScope.$eval(attrs.typeaheadMinLength) || 1,
                    waitTime = originalScope.$eval(attrs.typeaheadWaitMs) || 0,
                    isEditable = originalScope.$eval(attrs.typeaheadEditable) !== !1,
                    isLoadingSetter = $parse(attrs.typeaheadLoading).assign || angular.noop,
                    onSelectCallback = $parse(attrs.typeaheadOnSelect),
                    inputFormatter = attrs.typeaheadInputFormatter ? $parse(attrs.typeaheadInputFormatter) : void 0,
                    appendToBody = attrs.typeaheadAppendToBody ? originalScope.$eval(attrs.typeaheadAppendToBody) : !1,
                    $setModelValue = $parse(attrs.ngModel).assign,
                    parserResult = typeaheadParser.parse(attrs.typeahead),
                    scope = originalScope.$new();
                originalScope.$on("$destroy", function() {
                    scope.$destroy()
                });
                var popupId = "typeahead-" + scope.$id + "-" + Math.floor(1e4 * Math.random());
                element.attr({
                    "aria-autocomplete": "list",
                    "aria-expanded": !1,
                    "aria-owns": popupId
                });
                var popUpEl = angular.element("<div typeahead-popup></div>");
                popUpEl.attr({
                    id: popupId,
                    matches: "matches",
                    active: "activeIdx",
                    select: "select(activeIdx)",
                    query: "query",
                    position: "position"
                }), angular.isDefined(attrs.typeaheadTemplateUrl) && popUpEl.attr("template-url", attrs.typeaheadTemplateUrl);
                var resetMatches = function() {
                        scope.matches = [], scope.activeIdx = -1, element.attr("aria-expanded", !1)
                    },
                    getMatchId = function(index) {
                        return popupId + "-option-" + index
                    };
                scope.$watch("activeIdx", function(index) {
                    0 > index ? element.removeAttr("aria-activedescendant") : element.attr("aria-activedescendant", getMatchId(index))
                });
                var getMatchesAsync = function(inputValue) {
                    var locals = {
                        $viewValue: inputValue
                    };
                    isLoadingSetter(originalScope, !0), $q.when(parserResult.source(originalScope, locals)).then(function(matches) {
                        var onCurrentRequest = inputValue === modelCtrl.$viewValue;
                        if (onCurrentRequest && hasFocus)
                            if (matches.length > 0) {
                                scope.activeIdx = 0, scope.matches.length = 0;
                                for (var i = 0; i < matches.length; i++) locals[parserResult.itemName] = matches[i], scope.matches.push({
                                    id: getMatchId(i),
                                    label: parserResult.viewMapper(scope, locals),
                                    model: matches[i]
                                });
                                scope.query = inputValue, scope.position = appendToBody ? $position.offset(element) : $position.position(element), scope.position.top = scope.position.top + element.prop("offsetHeight"), element.attr("aria-expanded", !0)
                            } else resetMatches();
                        onCurrentRequest && isLoadingSetter(originalScope, !1)
                    }, function() {
                        resetMatches(), isLoadingSetter(originalScope, !1)
                    })
                };
                resetMatches(), scope.query = void 0;
                var timeoutPromise;
                modelCtrl.$parsers.unshift(function(inputValue) {
                    return hasFocus = !0, inputValue && inputValue.length >= minSearch ? waitTime > 0 ? (timeoutPromise && $timeout.cancel(timeoutPromise), timeoutPromise = $timeout(function() {
                        getMatchesAsync(inputValue)
                    }, waitTime)) : getMatchesAsync(inputValue) : (isLoadingSetter(originalScope, !1), resetMatches()), isEditable ? inputValue : inputValue ? void modelCtrl.$setValidity("editable", !1) : (modelCtrl.$setValidity("editable", !0), inputValue)
                }), modelCtrl.$formatters.push(function(modelValue) {
                    var candidateViewValue, emptyViewValue, locals = {};
                    return inputFormatter ? (locals.$model = modelValue, inputFormatter(originalScope, locals)) : (locals[parserResult.itemName] = modelValue, candidateViewValue = parserResult.viewMapper(originalScope, locals), locals[parserResult.itemName] = void 0, emptyViewValue = parserResult.viewMapper(originalScope, locals), candidateViewValue !== emptyViewValue ? candidateViewValue : modelValue)
                }), scope.select = function(activeIdx) {
                    var model, item, locals = {};
                    locals[parserResult.itemName] = item = scope.matches[activeIdx].model, model = parserResult.modelMapper(originalScope, locals), $setModelValue(originalScope, model), modelCtrl.$setValidity("editable", !0), onSelectCallback(originalScope, {
                        $item: item,
                        $model: model,
                        $label: parserResult.viewMapper(originalScope, locals)
                    }), resetMatches(), $timeout(function() {
                        element[0].focus()
                    }, 0, !1)
                }, element.bind("keydown", function(evt) {
                    0 !== scope.matches.length && -1 !== HOT_KEYS.indexOf(evt.which) && (evt.preventDefault(), 40 === evt.which ? (scope.activeIdx = (scope.activeIdx + 1) % scope.matches.length, scope.$digest()) : 38 === evt.which ? (scope.activeIdx = (scope.activeIdx ? scope.activeIdx : scope.matches.length) - 1, scope.$digest()) : 13 === evt.which || 9 === evt.which ? scope.$apply(function() {
                        scope.select(scope.activeIdx)
                    }) : 27 === evt.which && (evt.stopPropagation(), resetMatches(), scope.$digest()))
                }), element.bind("blur", function() {
                    hasFocus = !1
                });
                var dismissClickHandler = function(evt) {
                    element[0] !== evt.target && (resetMatches(), scope.$digest())
                };
                $document.bind("click", dismissClickHandler), originalScope.$on("$destroy", function() {
                    $document.unbind("click", dismissClickHandler)
                });
                var $popup = $compile(popUpEl)(scope);
                appendToBody ? $document.find("body").append($popup) : element.after($popup)
            }
        }
    }
]).directive("typeaheadPopup", function() {
    return {
        restrict: "EA",
        scope: {
            matches: "=",
            query: "=",
            active: "=",
            position: "=",
            select: "&"
        },
        replace: !0,
        templateUrl: "template/typeahead/typeahead-popup.html",
        link: function(scope, element, attrs) {
            scope.templateUrl = attrs.templateUrl, scope.isOpen = function() {
                return scope.matches.length > 0
            }, scope.isActive = function(matchIdx) {
                return scope.active == matchIdx
            }, scope.selectActive = function(matchIdx) {
                scope.active = matchIdx
            }, scope.selectMatch = function(activeIdx) {
                scope.select({
                    activeIdx: activeIdx
                })
            }
        }
    }
}).directive("typeaheadMatch", ["$http", "$templateCache", "$compile", "$parse",
    function($http, $templateCache, $compile, $parse) {
        return {
            restrict: "EA",
            scope: {
                index: "=",
                match: "=",
                query: "="
            },
            link: function(scope, element, attrs) {
                var tplUrl = $parse(attrs.templateUrl)(scope.$parent) || "template/typeahead/typeahead-match.html";
                $http.get(tplUrl, {
                    cache: $templateCache
                }).success(function(tplContent) {
                    element.replaceWith($compile(tplContent.trim())(scope))
                })
            }
        }
    }
]).filter("typeaheadHighlight", function() {
    function escapeRegexp(queryToEscape) {
        return queryToEscape.replace(/([.?*+^$[\]\\(){}|-])/g, "\\$1")
    }
    return function(matchItem, query) {
        return query ? ("" + matchItem).replace(new RegExp(escapeRegexp(query), "gi"), "<strong>$&</strong>") : matchItem
    }
}), angular.module("ui.bootstrap", ["ui.bootstrap.tpls", "ui.bootstrap.transition", "ui.bootstrap.collapse", "ui.bootstrap.accordion", "ui.bootstrap.alert", "ui.bootstrap.bindHtml", "ui.bootstrap.buttons", "ui.bootstrap.carousel", "ui.bootstrap.dateparser", "ui.bootstrap.position", "ui.bootstrap.datepicker", "ui.bootstrap.dropdown", "ui.bootstrap.modal", "ui.bootstrap.pagination", "ui.bootstrap.tooltip", "ui.bootstrap.popover", "ui.bootstrap.progressbar", "ui.bootstrap.rating", "ui.bootstrap.tabs", "ui.bootstrap.timepicker", "ui.bootstrap.typeahead"]), angular.module("ui.bootstrap.tpls", ["template/accordion/accordion-group.html", "template/accordion/accordion.html", "template/alert/alert.html", "template/carousel/carousel.html", "template/carousel/slide.html", "template/datepicker/datepicker.html", "template/datepicker/day.html", "template/datepicker/month.html", "template/datepicker/popup.html", "template/datepicker/year.html", "template/modal/backdrop.html", "template/modal/window.html", "template/pagination/pager.html", "template/pagination/pagination.html", "template/tooltip/tooltip-html-unsafe-popup.html", "template/tooltip/tooltip-popup.html", "template/popover/popover.html", "template/progressbar/bar.html", "template/progressbar/progress.html", "template/progressbar/progressbar.html", "template/rating/rating.html", "template/tabs/tab.html", "template/tabs/tabset.html", "template/timepicker/timepicker.html", "template/typeahead/typeahead-match.html", "template/typeahead/typeahead-popup.html"]), angular.module("ui.bootstrap.transition", []).factory("$transition", ["$q", "$timeout", "$rootScope",
    function($q, $timeout, $rootScope) {
        function findEndEventName(endEventNames) {
            for (var name in endEventNames)
                if (void 0 !== transElement.style[name]) return endEventNames[name]
        }
        var $transition = function(element, trigger, options) {
                options = options || {};
                var deferred = $q.defer(),
                    endEventName = $transition[options.animation ? "animationEndEventName" : "transitionEndEventName"],
                    transitionEndHandler = function() {
                        $rootScope.$apply(function() {
                            element.unbind(endEventName, transitionEndHandler), deferred.resolve(element)
                        })
                    };
                return endEventName && element.bind(endEventName, transitionEndHandler), $timeout(function() {
                    angular.isString(trigger) ? element.addClass(trigger) : angular.isFunction(trigger) ? trigger(element) : angular.isObject(trigger) && element.css(trigger), endEventName || deferred.resolve(element)
                }), deferred.promise.cancel = function() {
                    endEventName && element.unbind(endEventName, transitionEndHandler), deferred.reject("Transition cancelled")
                }, deferred.promise
            },
            transElement = document.createElement("trans"),
            transitionEndEventNames = {
                WebkitTransition: "webkitTransitionEnd",
                MozTransition: "transitionend",
                OTransition: "oTransitionEnd",
                transition: "transitionend"
            },
            animationEndEventNames = {
                WebkitTransition: "webkitAnimationEnd",
                MozTransition: "animationend",
                OTransition: "oAnimationEnd",
                transition: "animationend"
            };
        return $transition.transitionEndEventName = findEndEventName(transitionEndEventNames), $transition.animationEndEventName = findEndEventName(animationEndEventNames), $transition
    }
]), angular.module("ui.bootstrap.collapse", ["ui.bootstrap.transition"]).directive("collapse", ["$transition",
    function($transition) {
        return {
            link: function(scope, element, attrs) {
                function doTransition(change) {
                    function newTransitionDone() {
                        currentTransition === newTransition && (currentTransition = void 0)
                    }
                    var newTransition = $transition(element, change);
                    return currentTransition && currentTransition.cancel(), currentTransition = newTransition, newTransition.then(newTransitionDone, newTransitionDone), newTransition
                }

                function expand() {
                    initialAnimSkip ? (initialAnimSkip = !1, expandDone()) : (element.removeClass("collapse").addClass("collapsing"), doTransition({
                        height: element[0].scrollHeight + "px"
                    }).then(expandDone))
                }

                function expandDone() {
                    element.removeClass("collapsing"), element.addClass("collapse in"), element.css({
                        height: "auto"
                    })
                }

                function collapse() {
                    if (initialAnimSkip) initialAnimSkip = !1, collapseDone(), element.css({
                        height: 0
                    });
                    else {
                        element.css({
                            height: element[0].scrollHeight + "px"
                        }); {
                            element[0].offsetWidth
                        }
                        element.removeClass("collapse in").addClass("collapsing"), doTransition({
                            height: 0
                        }).then(collapseDone)
                    }
                }

                function collapseDone() {
                    element.removeClass("collapsing"), element.addClass("collapse")
                }
                var currentTransition, initialAnimSkip = !0;
                scope.$watch(attrs.collapse, function(shouldCollapse) {
                    shouldCollapse ? collapse() : expand()
                })
            }
        }
    }
]), angular.module("ui.bootstrap.accordion", ["ui.bootstrap.collapse"]).constant("accordionConfig", {
    closeOthers: !0
}).controller("AccordionController", ["$scope", "$attrs", "accordionConfig",
    function($scope, $attrs, accordionConfig) {
        this.groups = [], this.closeOthers = function(openGroup) {
            var closeOthers = angular.isDefined($attrs.closeOthers) ? $scope.$eval($attrs.closeOthers) : accordionConfig.closeOthers;
            closeOthers && angular.forEach(this.groups, function(group) {
                group !== openGroup && (group.isOpen = !1)
            })
        }, this.addGroup = function(groupScope) {
            var that = this;
            this.groups.push(groupScope), groupScope.$on("$destroy", function() {
                that.removeGroup(groupScope)
            })
        }, this.removeGroup = function(group) {
            var index = this.groups.indexOf(group); - 1 !== index && this.groups.splice(index, 1)
        }
    }
]).directive("accordion", function() {
    return {
        restrict: "EA",
        controller: "AccordionController",
        transclude: !0,
        replace: !1,
        templateUrl: "template/accordion/accordion.html"
    }
}).directive("accordionGroup", function() {
    return {
        require: "^accordion",
        restrict: "EA",
        transclude: !0,
        replace: !0,
        templateUrl: "template/accordion/accordion-group.html",
        scope: {
            heading: "@",
            isOpen: "=?",
            isDisabled: "=?"
        },
        controller: function() {
            this.setHeading = function(element) {
                this.heading = element
            }
        },
        link: function(scope, element, attrs, accordionCtrl) {
            accordionCtrl.addGroup(scope), scope.$watch("isOpen", function(value) {
                value && accordionCtrl.closeOthers(scope)
            }), scope.toggleOpen = function() {
                scope.isDisabled || (scope.isOpen = !scope.isOpen)
            }
        }
    }
}).directive("accordionHeading", function() {
    return {
        restrict: "EA",
        transclude: !0,
        template: "",
        replace: !0,
        require: "^accordionGroup",
        link: function(scope, element, attr, accordionGroupCtrl, transclude) {
            accordionGroupCtrl.setHeading(transclude(scope, function() {}))
        }
    }
}).directive("accordionTransclude", function() {
    return {
        require: "^accordionGroup",
        link: function(scope, element, attr, controller) {
            scope.$watch(function() {
                return controller[attr.accordionTransclude]
            }, function(heading) {
                heading && (element.html(""), element.append(heading))
            })
        }
    }
}), angular.module("ui.bootstrap.alert", []).controller("AlertController", ["$scope", "$attrs",
    function($scope, $attrs) {
        $scope.closeable = "close" in $attrs
    }
]).directive("alert", function() {
    return {
        restrict: "EA",
        controller: "AlertController",
        templateUrl: "template/alert/alert.html",
        transclude: !0,
        replace: !0,
        scope: {
            type: "@",
            close: "&"
        }
    }
}), angular.module("ui.bootstrap.bindHtml", []).directive("bindHtmlUnsafe", function() {
    return function(scope, element, attr) {
        element.addClass("ng-binding").data("$binding", attr.bindHtmlUnsafe), scope.$watch(attr.bindHtmlUnsafe, function(value) {
            element.html(value || "")
        })
    }
}), angular.module("ui.bootstrap.buttons", []).constant("buttonConfig", {
    activeClass: "active",
    toggleEvent: "click"
}).controller("ButtonsController", ["buttonConfig",
    function(buttonConfig) {
        this.activeClass = buttonConfig.activeClass || "active", this.toggleEvent = buttonConfig.toggleEvent || "click"
    }
]).directive("btnRadio", function() {
    return {
        require: ["btnRadio", "ngModel"],
        controller: "ButtonsController",
        link: function(scope, element, attrs, ctrls) {
            var buttonsCtrl = ctrls[0],
                ngModelCtrl = ctrls[1];
            ngModelCtrl.$render = function() {
                element.toggleClass(buttonsCtrl.activeClass, angular.equals(ngModelCtrl.$modelValue, scope.$eval(attrs.btnRadio)))
            }, element.bind(buttonsCtrl.toggleEvent, function() {
                var isActive = element.hasClass(buttonsCtrl.activeClass);
                (!isActive || angular.isDefined(attrs.uncheckable)) && scope.$apply(function() {
                    ngModelCtrl.$setViewValue(isActive ? null : scope.$eval(attrs.btnRadio)), ngModelCtrl.$render()
                })
            })
        }
    }
}).directive("btnCheckbox", function() {
    return {
        require: ["btnCheckbox", "ngModel"],
        controller: "ButtonsController",
        link: function(scope, element, attrs, ctrls) {
            function getTrueValue() {
                return getCheckboxValue(attrs.btnCheckboxTrue, !0)
            }

            function getFalseValue() {
                return getCheckboxValue(attrs.btnCheckboxFalse, !1)
            }

            function getCheckboxValue(attributeValue, defaultValue) {
                var val = scope.$eval(attributeValue);
                return angular.isDefined(val) ? val : defaultValue
            }
            var buttonsCtrl = ctrls[0],
                ngModelCtrl = ctrls[1];
            ngModelCtrl.$render = function() {
                element.toggleClass(buttonsCtrl.activeClass, angular.equals(ngModelCtrl.$modelValue, getTrueValue()))
            }, element.bind(buttonsCtrl.toggleEvent, function() {
                scope.$apply(function() {
                    ngModelCtrl.$setViewValue(element.hasClass(buttonsCtrl.activeClass) ? getFalseValue() : getTrueValue()), ngModelCtrl.$render()
                })
            })
        }
    }
}), angular.module("ui.bootstrap.carousel", ["ui.bootstrap.transition"]).controller("CarouselController", ["$scope", "$timeout", "$transition",
    function($scope, $timeout, $transition) {
        function restartTimer() {
            resetTimer();
            var interval = +$scope.interval;
            !isNaN(interval) && interval >= 0 && (currentTimeout = $timeout(timerFn, interval))
        }

        function resetTimer() {
            currentTimeout && ($timeout.cancel(currentTimeout), currentTimeout = null)
        }

        function timerFn() {
            isPlaying ? ($scope.next(), restartTimer()) : $scope.pause()
        }
        var currentTimeout, isPlaying, self = this,
            slides = self.slides = $scope.slides = [],
            currentIndex = -1;
        self.currentSlide = null;
        var destroyed = !1;
        self.select = $scope.select = function(nextSlide, direction) {
            function goNext() {
                if (!destroyed) {
                    if (self.currentSlide && angular.isString(direction) && !$scope.noTransition && nextSlide.$element) {
                        nextSlide.$element.addClass(direction); {
                            nextSlide.$element[0].offsetWidth
                        }
                        angular.forEach(slides, function(slide) {
                            angular.extend(slide, {
                                direction: "",
                                entering: !1,
                                leaving: !1,
                                active: !1
                            })
                        }), angular.extend(nextSlide, {
                            direction: direction,
                            active: !0,
                            entering: !0
                        }), angular.extend(self.currentSlide || {}, {
                            direction: direction,
                            leaving: !0
                        }), $scope.$currentTransition = $transition(nextSlide.$element, {}),
                            function(next, current) {
                                $scope.$currentTransition.then(function() {
                                    transitionDone(next, current)
                                }, function() {
                                    transitionDone(next, current)
                                })
                            }(nextSlide, self.currentSlide)
                    } else transitionDone(nextSlide, self.currentSlide);
                    self.currentSlide = nextSlide, currentIndex = nextIndex, restartTimer()
                }
            }

            function transitionDone(next, current) {
                angular.extend(next, {
                    direction: "",
                    active: !0,
                    leaving: !1,
                    entering: !1
                }), angular.extend(current || {}, {
                    direction: "",
                    active: !1,
                    leaving: !1,
                    entering: !1
                }), $scope.$currentTransition = null
            }
            var nextIndex = slides.indexOf(nextSlide);
            void 0 === direction && (direction = nextIndex > currentIndex ? "next" : "prev"), nextSlide && nextSlide !== self.currentSlide && ($scope.$currentTransition ? ($scope.$currentTransition.cancel(), $timeout(goNext)) : goNext())
        }, $scope.$on("$destroy", function() {
            destroyed = !0
        }), self.indexOfSlide = function(slide) {
            return slides.indexOf(slide)
        }, $scope.next = function() {
            var newIndex = (currentIndex + 1) % slides.length;
            return $scope.$currentTransition ? void 0 : self.select(slides[newIndex], "next")
        }, $scope.prev = function() {
            var newIndex = 0 > currentIndex - 1 ? slides.length - 1 : currentIndex - 1;
            return $scope.$currentTransition ? void 0 : self.select(slides[newIndex], "prev")
        }, $scope.isActive = function(slide) {
            return self.currentSlide === slide
        }, $scope.$watch("interval", restartTimer), $scope.$on("$destroy", resetTimer), $scope.play = function() {
            isPlaying || (isPlaying = !0, restartTimer())
        }, $scope.pause = function() {
            $scope.noPause || (isPlaying = !1, resetTimer())
        }, self.addSlide = function(slide, element) {
            slide.$element = element, slides.push(slide), 1 === slides.length || slide.active ? (self.select(slides[slides.length - 1]), 1 == slides.length && $scope.play()) : slide.active = !1
        }, self.removeSlide = function(slide) {
            var index = slides.indexOf(slide);
            slides.splice(index, 1), slides.length > 0 && slide.active ? self.select(index >= slides.length ? slides[index - 1] : slides[index]) : currentIndex > index && currentIndex--
        }
    }
]).directive("carousel", [
    function() {
        return {
            restrict: "EA",
            transclude: !0,
            replace: !0,
            controller: "CarouselController",
            require: "carousel",
            templateUrl: "template/carousel/carousel.html",
            scope: {
                interval: "=",
                noTransition: "=",
                noPause: "="
            }
        }
    }
]).directive("slide", function() {
    return {
        require: "^carousel",
        restrict: "EA",
        transclude: !0,
        replace: !0,
        templateUrl: "template/carousel/slide.html",
        scope: {
            active: "=?"
        },
        link: function(scope, element, attrs, carouselCtrl) {
            carouselCtrl.addSlide(scope, element), scope.$on("$destroy", function() {
                carouselCtrl.removeSlide(scope)
            }), scope.$watch("active", function(active) {
                active && carouselCtrl.select(scope)
            })
        }
    }
}), angular.module("ui.bootstrap.dateparser", []).service("dateParser", ["$locale", "orderByFilter",
    function($locale, orderByFilter) {
        function isValid(year, month, date) {
            return 1 === month && date > 28 ? 29 === date && (year % 4 === 0 && year % 100 !== 0 || year % 400 === 0) : 3 === month || 5 === month || 8 === month || 10 === month ? 31 > date : !0
        }
        this.parsers = {};
        var formatCodeToRegex = {
            yyyy: {
                regex: "\\d{4}",
                apply: function(value) {
                    this.year = +value
                }
            },
            yy: {
                regex: "\\d{2}",
                apply: function(value) {
                    this.year = +value + 2e3
                }
            },
            y: {
                regex: "\\d{1,4}",
                apply: function(value) {
                    this.year = +value
                }
            },
            MMMM: {
                regex: $locale.DATETIME_FORMATS.MONTH.join("|"),
                apply: function(value) {
                    this.month = $locale.DATETIME_FORMATS.MONTH.indexOf(value)
                }
            },
            MMM: {
                regex: $locale.DATETIME_FORMATS.SHORTMONTH.join("|"),
                apply: function(value) {
                    this.month = $locale.DATETIME_FORMATS.SHORTMONTH.indexOf(value)
                }
            },
            MM: {
                regex: "0[1-9]|1[0-2]",
                apply: function(value) {
                    this.month = value - 1
                }
            },
            M: {
                regex: "[1-9]|1[0-2]",
                apply: function(value) {
                    this.month = value - 1
                }
            },
            dd: {
                regex: "[0-2][0-9]{1}|3[0-1]{1}",
                apply: function(value) {
                    this.date = +value
                }
            },
            d: {
                regex: "[1-2]?[0-9]{1}|3[0-1]{1}",
                apply: function(value) {
                    this.date = +value
                }
            },
            EEEE: {
                regex: $locale.DATETIME_FORMATS.DAY.join("|")
            },
            EEE: {
                regex: $locale.DATETIME_FORMATS.SHORTDAY.join("|")
            }
        };
        this.createParser = function(format) {
            var map = [],
                regex = format.split("");
            return angular.forEach(formatCodeToRegex, function(data, code) {
                var index = format.indexOf(code);
                if (index > -1) {
                    format = format.split(""), regex[index] = "(" + data.regex + ")", format[index] = "$";
                    for (var i = index + 1, n = index + code.length; n > i; i++) regex[i] = "", format[i] = "$";
                    format = format.join(""), map.push({
                        index: index,
                        apply: data.apply
                    })
                }
            }), {
                regex: new RegExp("^" + regex.join("") + "$"),
                map: orderByFilter(map, "index")
            }
        }, this.parse = function(input, format) {
            if (!angular.isString(input)) return input;
            format = $locale.DATETIME_FORMATS[format] || format, this.parsers[format] || (this.parsers[format] = this.createParser(format));
            var parser = this.parsers[format],
                regex = parser.regex,
                map = parser.map,
                results = input.match(regex);
            if (results && results.length) {
                for (var dt, fields = {
                    year: 1900,
                    month: 0,
                    date: 1,
                    hours: 0
                }, i = 1, n = results.length; n > i; i++) {
                    var mapper = map[i - 1];
                    mapper.apply && mapper.apply.call(fields, results[i])
                }
                return isValid(fields.year, fields.month, fields.date) && (dt = new Date(fields.year, fields.month, fields.date, fields.hours)), dt
            }
        }
    }
]), angular.module("ui.bootstrap.position", []).factory("$position", ["$document", "$window",
    function($document, $window) {
        function getStyle(el, cssprop) {
            return el.currentStyle ? el.currentStyle[cssprop] : $window.getComputedStyle ? $window.getComputedStyle(el)[cssprop] : el.style[cssprop]
        }

        function isStaticPositioned(element) {
            return "static" === (getStyle(element, "position") || "static")
        }
        var parentOffsetEl = function(element) {
            for (var docDomEl = $document[0], offsetParent = element.offsetParent || docDomEl; offsetParent && offsetParent !== docDomEl && isStaticPositioned(offsetParent);) offsetParent = offsetParent.offsetParent;
            return offsetParent || docDomEl
        };
        return {
            position: function(element) {
                var elBCR = this.offset(element),
                    offsetParentBCR = {
                        top: 0,
                        left: 0
                    },
                    offsetParentEl = parentOffsetEl(element[0]);
                offsetParentEl != $document[0] && (offsetParentBCR = this.offset(angular.element(offsetParentEl)), offsetParentBCR.top += offsetParentEl.clientTop - offsetParentEl.scrollTop, offsetParentBCR.left += offsetParentEl.clientLeft - offsetParentEl.scrollLeft);
                var boundingClientRect = element[0].getBoundingClientRect();
                return {
                    width: boundingClientRect.width || element.prop("offsetWidth"),
                    height: boundingClientRect.height || element.prop("offsetHeight"),
                    top: elBCR.top - offsetParentBCR.top,
                    left: elBCR.left - offsetParentBCR.left
                }
            },
            offset: function(element) {
                var boundingClientRect = element[0].getBoundingClientRect();
                return {
                    width: boundingClientRect.width || element.prop("offsetWidth"),
                    height: boundingClientRect.height || element.prop("offsetHeight"),
                    top: boundingClientRect.top + ($window.pageYOffset || $document[0].documentElement.scrollTop),
                    left: boundingClientRect.left + ($window.pageXOffset || $document[0].documentElement.scrollLeft)
                }
            },
            positionElements: function(hostEl, targetEl, positionStr, appendToBody) {
                var hostElPos, targetElWidth, targetElHeight, targetElPos, positionStrParts = positionStr.split("-"),
                    pos0 = positionStrParts[0],
                    pos1 = positionStrParts[1] || "center";
                hostElPos = appendToBody ? this.offset(hostEl) : this.position(hostEl), targetElWidth = targetEl.prop("offsetWidth"), targetElHeight = targetEl.prop("offsetHeight");
                var shiftWidth = {
                        center: function() {
                            return hostElPos.left + hostElPos.width / 2 - targetElWidth / 2
                        },
                        left: function() {
                            return hostElPos.left
                        },
                        right: function() {
                            return hostElPos.left + hostElPos.width
                        }
                    },
                    shiftHeight = {
                        center: function() {
                            return hostElPos.top + hostElPos.height / 2 - targetElHeight / 2
                        },
                        top: function() {
                            return hostElPos.top
                        },
                        bottom: function() {
                            return hostElPos.top + hostElPos.height
                        }
                    };
                switch (pos0) {
                    case "right":
                        targetElPos = {
                            top: shiftHeight[pos1](),
                            left: shiftWidth[pos0]()
                        };
                        break;
                    case "left":
                        targetElPos = {
                            top: shiftHeight[pos1](),
                            left: hostElPos.left - targetElWidth
                        };
                        break;
                    case "bottom":
                        targetElPos = {
                            top: shiftHeight[pos0](),
                            left: shiftWidth[pos1]()
                        };
                        break;
                    default:
                        targetElPos = {
                            top: hostElPos.top - targetElHeight,
                            left: shiftWidth[pos1]()
                        }
                }
                return targetElPos
            }
        }
    }
]), angular.module("ui.bootstrap.datepicker", ["ui.bootstrap.dateparser", "ui.bootstrap.position"]).constant("datepickerConfig", {
    formatDay: "dd",
    formatMonth: "MMMM",
    formatYear: "yyyy",
    formatDayHeader: "EEE",
    formatDayTitle: "MMMM yyyy",
    formatMonthTitle: "yyyy",
    datepickerMode: "day",
    minMode: "day",
    maxMode: "year",
    showWeeks: !0,
    startingDay: 0,
    yearRange: 20,
    minDate: null,
    maxDate: null
}).controller("DatepickerController", ["$scope", "$attrs", "$parse", "$interpolate", "$timeout", "$log", "dateFilter", "datepickerConfig",
    function($scope, $attrs, $parse, $interpolate, $timeout, $log, dateFilter, datepickerConfig) {
        var self = this,
            ngModelCtrl = {
                $setViewValue: angular.noop
            };
        this.modes = ["day", "month", "year"], angular.forEach(["formatDay", "formatMonth", "formatYear", "formatDayHeader", "formatDayTitle", "formatMonthTitle", "minMode", "maxMode", "showWeeks", "startingDay", "yearRange"], function(key, index) {
            self[key] = angular.isDefined($attrs[key]) ? 8 > index ? $interpolate($attrs[key])($scope.$parent) : $scope.$parent.$eval($attrs[key]) : datepickerConfig[key]
        }), angular.forEach(["minDate", "maxDate"], function(key) {
            $attrs[key] ? $scope.$parent.$watch($parse($attrs[key]), function(value) {
                self[key] = value ? new Date(value) : null, self.refreshView()
            }) : self[key] = datepickerConfig[key] ? new Date(datepickerConfig[key]) : null
        }), $scope.datepickerMode = $scope.datepickerMode || datepickerConfig.datepickerMode, $scope.uniqueId = "datepicker-" + $scope.$id + "-" + Math.floor(1e4 * Math.random()), this.activeDate = angular.isDefined($attrs.initDate) ? $scope.$parent.$eval($attrs.initDate) : new Date, $scope.isActive = function(dateObject) {
            return 0 === self.compare(dateObject.date, self.activeDate) ? ($scope.activeDateId = dateObject.uid, !0) : !1
        }, this.init = function(ngModelCtrl_) {
            ngModelCtrl = ngModelCtrl_, ngModelCtrl.$render = function() {
                self.render()
            }
        }, this.render = function() {
            if (ngModelCtrl.$modelValue) {
                var date = new Date(ngModelCtrl.$modelValue),
                    isValid = !isNaN(date);
                isValid ? this.activeDate = date : $log.error('Datepicker directive: "ng-model" value must be a Date object, a number of milliseconds since 01.01.1970 or a string representing an RFC2822 or ISO 8601 date.'), ngModelCtrl.$setValidity("date", isValid)
            }
            this.refreshView()
        }, this.refreshView = function() {
            if (this.element) {
                this._refreshView();
                var date = ngModelCtrl.$modelValue ? new Date(ngModelCtrl.$modelValue) : null;
                ngModelCtrl.$setValidity("date-disabled", !date || this.element && !this.isDisabled(date))
            }
        }, this.createDateObject = function(date, format) {
            var model = ngModelCtrl.$modelValue ? new Date(ngModelCtrl.$modelValue) : null;
            return {
                date: date,
                label: dateFilter(date, format),
                selected: model && 0 === this.compare(date, model),
                disabled: this.isDisabled(date),
                current: 0 === this.compare(date, new Date)
            }
        }, this.isDisabled = function(date) {
            return this.minDate && this.compare(date, this.minDate) < 0 || this.maxDate && this.compare(date, this.maxDate) > 0 || $attrs.dateDisabled && $scope.dateDisabled({
                date: date,
                mode: $scope.datepickerMode
            })
        }, this.split = function(arr, size) {
            for (var arrays = []; arr.length > 0;) arrays.push(arr.splice(0, size));
            return arrays
        }, $scope.select = function(date) {
            if ($scope.datepickerMode === self.minMode) {
                var dt = ngModelCtrl.$modelValue ? new Date(ngModelCtrl.$modelValue) : new Date(0, 0, 0, 0, 0, 0, 0);
                dt.setFullYear(date.getFullYear(), date.getMonth(), date.getDate()), ngModelCtrl.$setViewValue(dt), ngModelCtrl.$render()
            } else self.activeDate = date, $scope.datepickerMode = self.modes[self.modes.indexOf($scope.datepickerMode) - 1]
        }, $scope.move = function(direction) {
            var year = self.activeDate.getFullYear() + direction * (self.step.years || 0),
                month = self.activeDate.getMonth() + direction * (self.step.months || 0);
            self.activeDate.setFullYear(year, month, 1), self.refreshView()
        }, $scope.toggleMode = function(direction) {
            direction = direction || 1, $scope.datepickerMode === self.maxMode && 1 === direction || $scope.datepickerMode === self.minMode && -1 === direction || ($scope.datepickerMode = self.modes[self.modes.indexOf($scope.datepickerMode) + direction])
        }, $scope.keys = {
            13: "enter",
            32: "space",
            33: "pageup",
            34: "pagedown",
            35: "end",
            36: "home",
            37: "left",
            38: "up",
            39: "right",
            40: "down"
        };
        var focusElement = function() {
            $timeout(function() {
                self.element[0].focus()
            }, 0, !1)
        };
        $scope.$on("datepicker.focus", focusElement), $scope.keydown = function(evt) {
            var key = $scope.keys[evt.which];
            if (key && !evt.shiftKey && !evt.altKey)
                if (evt.preventDefault(), evt.stopPropagation(), "enter" === key || "space" === key) {
                    if (self.isDisabled(self.activeDate)) return;
                    $scope.select(self.activeDate), focusElement()
                } else !evt.ctrlKey || "up" !== key && "down" !== key ? (self.handleKeyDown(key, evt), self.refreshView()) : ($scope.toggleMode("up" === key ? 1 : -1), focusElement())
        }
    }
]).directive("datepicker", function() {
    return {
        restrict: "EA",
        replace: !0,
        templateUrl: "template/datepicker/datepicker.html",
        scope: {
            datepickerMode: "=?",
            dateDisabled: "&"
        },
        require: ["datepicker", "?^ngModel"],
        controller: "DatepickerController",
        link: function(scope, element, attrs, ctrls) {
            var datepickerCtrl = ctrls[0],
                ngModelCtrl = ctrls[1];
            ngModelCtrl && datepickerCtrl.init(ngModelCtrl)
        }
    }
}).directive("daypicker", ["dateFilter",
    function(dateFilter) {
        return {
            restrict: "EA",
            replace: !0,
            templateUrl: "template/datepicker/day.html",
            require: "^datepicker",
            link: function(scope, element, attrs, ctrl) {
                function getDaysInMonth(year, month) {
                    return 1 !== month || year % 4 !== 0 || year % 100 === 0 && year % 400 !== 0 ? DAYS_IN_MONTH[month] : 29
                }

                function getDates(startDate, n) {
                    var dates = new Array(n),
                        current = new Date(startDate),
                        i = 0;
                    for (current.setHours(12); n > i;) dates[i++] = new Date(current), current.setDate(current.getDate() + 1);
                    return dates
                }

                function getISO8601WeekNumber(date) {
                    var checkDate = new Date(date);
                    checkDate.setDate(checkDate.getDate() + 4 - (checkDate.getDay() || 7));
                    var time = checkDate.getTime();
                    return checkDate.setMonth(0), checkDate.setDate(1), Math.floor(Math.round((time - checkDate) / 864e5) / 7) + 1
                }
                scope.showWeeks = ctrl.showWeeks, ctrl.step = {
                    months: 1
                }, ctrl.element = element;
                var DAYS_IN_MONTH = [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];
                ctrl._refreshView = function() {
                    var year = ctrl.activeDate.getFullYear(),
                        month = ctrl.activeDate.getMonth(),
                        firstDayOfMonth = new Date(year, month, 1),
                        difference = ctrl.startingDay - firstDayOfMonth.getDay(),
                        numDisplayedFromPreviousMonth = difference > 0 ? 7 - difference : -difference,
                        firstDate = new Date(firstDayOfMonth);
                    numDisplayedFromPreviousMonth > 0 && firstDate.setDate(-numDisplayedFromPreviousMonth + 1);
                    for (var days = getDates(firstDate, 42), i = 0; 42 > i; i++) days[i] = angular.extend(ctrl.createDateObject(days[i], ctrl.formatDay), {
                        secondary: days[i].getMonth() !== month,
                        uid: scope.uniqueId + "-" + i
                    });
                    scope.labels = new Array(7);
                    for (var j = 0; 7 > j; j++) scope.labels[j] = {
                        abbr: dateFilter(days[j].date, ctrl.formatDayHeader),
                        full: dateFilter(days[j].date, "EEEE")
                    };
                    if (scope.title = dateFilter(ctrl.activeDate, ctrl.formatDayTitle), scope.rows = ctrl.split(days, 7), scope.showWeeks) {
                        scope.weekNumbers = [];
                        for (var weekNumber = getISO8601WeekNumber(scope.rows[0][0].date), numWeeks = scope.rows.length; scope.weekNumbers.push(weekNumber++) < numWeeks;);
                    }
                }, ctrl.compare = function(date1, date2) {
                    return new Date(date1.getFullYear(), date1.getMonth(), date1.getDate()) - new Date(date2.getFullYear(), date2.getMonth(), date2.getDate())
                }, ctrl.handleKeyDown = function(key) {
                    var date = ctrl.activeDate.getDate();
                    if ("left" === key) date -= 1;
                    else if ("up" === key) date -= 7;
                    else if ("right" === key) date += 1;
                    else if ("down" === key) date += 7;
                    else if ("pageup" === key || "pagedown" === key) {
                        var month = ctrl.activeDate.getMonth() + ("pageup" === key ? -1 : 1);
                        ctrl.activeDate.setMonth(month, 1), date = Math.min(getDaysInMonth(ctrl.activeDate.getFullYear(), ctrl.activeDate.getMonth()), date)
                    } else "home" === key ? date = 1 : "end" === key && (date = getDaysInMonth(ctrl.activeDate.getFullYear(), ctrl.activeDate.getMonth()));
                    ctrl.activeDate.setDate(date)
                }, ctrl.refreshView()
            }
        }
    }
]).directive("monthpicker", ["dateFilter",
    function(dateFilter) {
        return {
            restrict: "EA",
            replace: !0,
            templateUrl: "template/datepicker/month.html",
            require: "^datepicker",
            link: function(scope, element, attrs, ctrl) {
                ctrl.step = {
                    years: 1
                }, ctrl.element = element, ctrl._refreshView = function() {
                    for (var months = new Array(12), year = ctrl.activeDate.getFullYear(), i = 0; 12 > i; i++) months[i] = angular.extend(ctrl.createDateObject(new Date(year, i, 1), ctrl.formatMonth), {
                        uid: scope.uniqueId + "-" + i
                    });
                    scope.title = dateFilter(ctrl.activeDate, ctrl.formatMonthTitle), scope.rows = ctrl.split(months, 3)
                }, ctrl.compare = function(date1, date2) {
                    return new Date(date1.getFullYear(), date1.getMonth()) - new Date(date2.getFullYear(), date2.getMonth())
                }, ctrl.handleKeyDown = function(key) {
                    var date = ctrl.activeDate.getMonth();
                    if ("left" === key) date -= 1;
                    else if ("up" === key) date -= 3;
                    else if ("right" === key) date += 1;
                    else if ("down" === key) date += 3;
                    else if ("pageup" === key || "pagedown" === key) {
                        var year = ctrl.activeDate.getFullYear() + ("pageup" === key ? -1 : 1);
                        ctrl.activeDate.setFullYear(year)
                    } else "home" === key ? date = 0 : "end" === key && (date = 11);
                    ctrl.activeDate.setMonth(date)
                }, ctrl.refreshView()
            }
        }
    }
]).directive("yearpicker", ["dateFilter",
    function() {
        return {
            restrict: "EA",
            replace: !0,
            templateUrl: "template/datepicker/year.html",
            require: "^datepicker",
            link: function(scope, element, attrs, ctrl) {
                function getStartingYear(year) {
                    return parseInt((year - 1) / range, 10) * range + 1
                }
                var range = ctrl.yearRange;
                ctrl.step = {
                    years: range
                }, ctrl.element = element, ctrl._refreshView = function() {
                    for (var years = new Array(range), i = 0, start = getStartingYear(ctrl.activeDate.getFullYear()); range > i; i++) years[i] = angular.extend(ctrl.createDateObject(new Date(start + i, 0, 1), ctrl.formatYear), {
                        uid: scope.uniqueId + "-" + i
                    });
                    scope.title = [years[0].label, years[range - 1].label].join(" - "), scope.rows = ctrl.split(years, 5)
                }, ctrl.compare = function(date1, date2) {
                    return date1.getFullYear() - date2.getFullYear()
                }, ctrl.handleKeyDown = function(key) {
                    var date = ctrl.activeDate.getFullYear();
                    "left" === key ? date -= 1 : "up" === key ? date -= 5 : "right" === key ? date += 1 : "down" === key ? date += 5 : "pageup" === key || "pagedown" === key ? date += ("pageup" === key ? -1 : 1) * ctrl.step.years : "home" === key ? date = getStartingYear(ctrl.activeDate.getFullYear()) : "end" === key && (date = getStartingYear(ctrl.activeDate.getFullYear()) + range - 1), ctrl.activeDate.setFullYear(date)
                }, ctrl.refreshView()
            }
        }
    }
]).constant("datepickerPopupConfig", {
    datepickerPopup: "yyyy-MM-dd",
    currentText: "Today",
    clearText: "Clear",
    closeText: "Done",
    closeOnDateSelection: !0,
    appendToBody: !1,
    showButtonBar: !0
}).directive("datepickerPopup", ["$compile", "$parse", "$document", "$position", "dateFilter", "dateParser", "datepickerPopupConfig",
    function($compile, $parse, $document, $position, dateFilter, dateParser, datepickerPopupConfig) {
        return {
            restrict: "EA",
            require: "ngModel",
            scope: {
                isOpen: "=?",
                currentText: "@",
                clearText: "@",
                closeText: "@",
                dateDisabled: "&"
            },
            link: function(scope, element, attrs, ngModel) {
                function cameltoDash(string) {
                    return string.replace(/([A-Z])/g, function($1) {
                        return "-" + $1.toLowerCase()
                    })
                }

                function parseDate(viewValue) {
                    if (viewValue) {
                        if (angular.isDate(viewValue) && !isNaN(viewValue)) return ngModel.$setValidity("date", !0), viewValue;
                        if (angular.isString(viewValue)) {
                            var date = dateParser.parse(viewValue, dateFormat) || new Date(viewValue);
                            return isNaN(date) ? void ngModel.$setValidity("date", !1) : (ngModel.$setValidity("date", !0), date)
                        }
                        return void ngModel.$setValidity("date", !1)
                    }
                    return ngModel.$setValidity("date", !0), null
                }
                var dateFormat, closeOnDateSelection = angular.isDefined(attrs.closeOnDateSelection) ? scope.$parent.$eval(attrs.closeOnDateSelection) : datepickerPopupConfig.closeOnDateSelection,
                    appendToBody = angular.isDefined(attrs.datepickerAppendToBody) ? scope.$parent.$eval(attrs.datepickerAppendToBody) : datepickerPopupConfig.appendToBody;
                scope.showButtonBar = angular.isDefined(attrs.showButtonBar) ? scope.$parent.$eval(attrs.showButtonBar) : datepickerPopupConfig.showButtonBar, scope.getText = function(key) {
                    return scope[key + "Text"] || datepickerPopupConfig[key + "Text"]
                }, attrs.$observe("datepickerPopup", function(value) {
                    dateFormat = value || datepickerPopupConfig.datepickerPopup, ngModel.$render()
                });
                var popupEl = angular.element("<div datepicker-popup-wrap><div datepicker></div></div>");
                popupEl.attr({
                    "ng-model": "date",
                    "ng-change": "dateSelection()"
                });
                var datepickerEl = angular.element(popupEl.children()[0]);
                attrs.datepickerOptions && angular.forEach(scope.$parent.$eval(attrs.datepickerOptions), function(value, option) {
                    datepickerEl.attr(cameltoDash(option), value)
                }), angular.forEach(["minDate", "maxDate"], function(key) {
                    attrs[key] && (scope.$parent.$watch($parse(attrs[key]), function(value) {
                        scope[key] = value
                    }), datepickerEl.attr(cameltoDash(key), key))
                }), attrs.dateDisabled && datepickerEl.attr("date-disabled", "dateDisabled({ date: date, mode: mode })"), ngModel.$parsers.unshift(parseDate), scope.dateSelection = function(dt) {
                    angular.isDefined(dt) && (scope.date = dt), ngModel.$setViewValue(scope.date), ngModel.$render(), closeOnDateSelection && (scope.isOpen = !1, element[0].focus())
                }, element.bind("input change keyup", function() {
                    scope.$apply(function() {
                        scope.date = ngModel.$modelValue
                    })
                }), ngModel.$render = function() {
                    var date = ngModel.$viewValue ? dateFilter(ngModel.$viewValue, dateFormat) : "";
                    element.val(date), scope.date = parseDate(ngModel.$modelValue)
                };
                var documentClickBind = function(event) {
                        scope.isOpen && event.target !== element[0] && scope.$apply(function() {
                            scope.isOpen = !1
                        })
                    },
                    keydown = function(evt) {
                        scope.keydown(evt)
                    };
                element.bind("keydown", keydown), scope.keydown = function(evt) {
                    27 === evt.which ? (evt.preventDefault(), evt.stopPropagation(), scope.close()) : 40 !== evt.which || scope.isOpen || (scope.isOpen = !0)
                }, scope.$watch("isOpen", function(value) {
                    value ? (scope.$broadcast("datepicker.focus"), scope.position = appendToBody ? $position.offset(element) : $position.position(element), scope.position.top = scope.position.top + element.prop("offsetHeight"), $document.bind("click", documentClickBind)) : $document.unbind("click", documentClickBind)
                }), scope.select = function(date) {
                    if ("today" === date) {
                        var today = new Date;
                        angular.isDate(ngModel.$modelValue) ? (date = new Date(ngModel.$modelValue), date.setFullYear(today.getFullYear(), today.getMonth(), today.getDate())) : date = new Date(today.setHours(0, 0, 0, 0))
                    }
                    scope.dateSelection(date)
                }, scope.close = function() {
                    scope.isOpen = !1, element[0].focus()
                };
                var $popup = $compile(popupEl)(scope);
                appendToBody ? $document.find("body").append($popup) : element.after($popup), scope.$on("$destroy", function() {
                    $popup.remove(), element.unbind("keydown", keydown), $document.unbind("click", documentClickBind)
                })
            }
        }
    }
]).directive("datepickerPopupWrap", function() {
    return {
        restrict: "EA",
        replace: !0,
        transclude: !0,
        templateUrl: "template/datepicker/popup.html",
        link: function(scope, element) {
            element.bind("click", function(event) {
                event.preventDefault(), event.stopPropagation()
            })
        }
    }
}), angular.module("ui.bootstrap.dropdown", []).constant("dropdownConfig", {
    openClass: "open"
}).service("dropdownService", ["$document",
    function($document) {
        var openScope = null;
        this.open = function(dropdownScope) {
            openScope || ($document.bind("click", closeDropdown), $document.bind("keydown", escapeKeyBind)), openScope && openScope !== dropdownScope && (openScope.isOpen = !1), openScope = dropdownScope
        }, this.close = function(dropdownScope) {
            openScope === dropdownScope && (openScope = null, $document.unbind("click", closeDropdown), $document.unbind("keydown", escapeKeyBind))
        };
        var closeDropdown = function(evt) {
                evt && evt.isDefaultPrevented() || openScope.$apply(function() {
                    openScope.isOpen = !1
                })
            },
            escapeKeyBind = function(evt) {
                27 === evt.which && (openScope.focusToggleElement(), closeDropdown())
            }
    }
]).controller("DropdownController", ["$scope", "$attrs", "$parse", "dropdownConfig", "dropdownService", "$animate",
    function($scope, $attrs, $parse, dropdownConfig, dropdownService, $animate) {
        var getIsOpen, self = this,
            scope = $scope.$new(),
            openClass = dropdownConfig.openClass,
            setIsOpen = angular.noop,
            toggleInvoker = $attrs.onToggle ? $parse($attrs.onToggle) : angular.noop;
        this.init = function(element) {
            self.$element = element, $attrs.isOpen && (getIsOpen = $parse($attrs.isOpen), setIsOpen = getIsOpen.assign, $scope.$watch(getIsOpen, function(value) {
                scope.isOpen = !!value
            }))
        }, this.toggle = function(open) {
            return scope.isOpen = arguments.length ? !!open : !scope.isOpen
        }, this.isOpen = function() {
            return scope.isOpen
        }, scope.focusToggleElement = function() {
            self.toggleElement && self.toggleElement[0].focus()
        }, scope.$watch("isOpen", function(isOpen, wasOpen) {
            $animate[isOpen ? "addClass" : "removeClass"](self.$element, openClass), isOpen ? (scope.focusToggleElement(), dropdownService.open(scope)) : dropdownService.close(scope), setIsOpen($scope, isOpen), angular.isDefined(isOpen) && isOpen !== wasOpen && toggleInvoker($scope, {
                open: !!isOpen
            })
        }), $scope.$on("$locationChangeSuccess", function() {
            scope.isOpen = !1
        }), $scope.$on("$destroy", function() {
            scope.$destroy()
        })
    }
]).directive("dropdown", function() {
    return {
        restrict: "CA",
        controller: "DropdownController",
        link: function(scope, element, attrs, dropdownCtrl) {
            dropdownCtrl.init(element)
        }
    }
}).directive("dropdownToggle", function() {
    return {
        restrict: "CA",
        require: "?^dropdown",
        link: function(scope, element, attrs, dropdownCtrl) {
            if (dropdownCtrl) {
                dropdownCtrl.toggleElement = element;
                var toggleDropdown = function(event) {
                    event.preventDefault(), element.hasClass("disabled") || attrs.disabled || scope.$apply(function() {
                        dropdownCtrl.toggle()
                    })
                };
                element.bind("click", toggleDropdown), element.attr({
                    "aria-haspopup": !0,
                    "aria-expanded": !1
                }), scope.$watch(dropdownCtrl.isOpen, function(isOpen) {
                    element.attr("aria-expanded", !!isOpen)
                }), scope.$on("$destroy", function() {
                    element.unbind("click", toggleDropdown)
                })
            }
        }
    }
}), angular.module("ui.bootstrap.modal", ["ui.bootstrap.transition"]).factory("$$stackedMap", function() {
    return {
        createNew: function() {
            var stack = [];
            return {
                add: function(key, value) {
                    stack.push({
                        key: key,
                        value: value
                    })
                },
                get: function(key) {
                    for (var i = 0; i < stack.length; i++)
                        if (key == stack[i].key) return stack[i]
                },
                keys: function() {
                    for (var keys = [], i = 0; i < stack.length; i++) keys.push(stack[i].key);
                    return keys
                },
                top: function() {
                    return stack[stack.length - 1]
                },
                remove: function(key) {
                    for (var idx = -1, i = 0; i < stack.length; i++)
                        if (key == stack[i].key) {
                            idx = i;
                            break
                        }
                    return stack.splice(idx, 1)[0]
                },
                removeTop: function() {
                    return stack.splice(stack.length - 1, 1)[0]
                },
                length: function() {
                    return stack.length
                }
            }
        }
    }
}).directive("modalBackdrop", ["$timeout",
    function($timeout) {
        return {
            restrict: "EA",
            replace: !0,
            templateUrl: "template/modal/backdrop.html",
            link: function(scope) {
                scope.animate = !1, $timeout(function() {
                    scope.animate = !0
                })
            }
        }
    }
]).directive("modalWindow", ["$modalStack", "$timeout",
    function($modalStack, $timeout) {
        return {
            restrict: "EA",
            scope: {
                index: "@",
                animate: "="
            },
            replace: !0,
            transclude: !0,
            templateUrl: function(tElement, tAttrs) {
                return tAttrs.templateUrl || "template/modal/window.html"
            },
            link: function(scope, element, attrs) {
                element.addClass(attrs.windowClass || ""), scope.size = attrs.size, $timeout(function() {
                    scope.animate = !0, element[0].focus()
                }), scope.close = function(evt) {
                    var modal = $modalStack.getTop();
                    modal && modal.value.backdrop && "static" != modal.value.backdrop && evt.target === evt.currentTarget && (evt.preventDefault(), evt.stopPropagation(), $modalStack.dismiss(modal.key, "backdrop click"))
                }
            }
        }
    }
]).factory("$modalStack", ["$transition", "$timeout", "$document", "$compile", "$rootScope", "$$stackedMap",
    function($transition, $timeout, $document, $compile, $rootScope, $$stackedMap) {
        function backdropIndex() {
            for (var topBackdropIndex = -1, opened = openedWindows.keys(), i = 0; i < opened.length; i++) openedWindows.get(opened[i]).value.backdrop && (topBackdropIndex = i);
            return topBackdropIndex
        }

        function removeModalWindow(modalInstance) {
            var body = $document.find("body").eq(0),
                modalWindow = openedWindows.get(modalInstance).value;
            openedWindows.remove(modalInstance), removeAfterAnimate(modalWindow.modalDomEl, modalWindow.modalScope, 300, function() {
                modalWindow.modalScope.$destroy(), body.toggleClass(OPENED_MODAL_CLASS, openedWindows.length() > 0), checkRemoveBackdrop()
            })
        }

        function checkRemoveBackdrop() {
            if (backdropDomEl && -1 == backdropIndex()) {
                var backdropScopeRef = backdropScope;
                removeAfterAnimate(backdropDomEl, backdropScope, 150, function() {
                    backdropScopeRef.$destroy(), backdropScopeRef = null
                }), backdropDomEl = void 0, backdropScope = void 0
            }
        }

        function removeAfterAnimate(domEl, scope, emulateTime, done) {
            function afterAnimating() {
                afterAnimating.done || (afterAnimating.done = !0, domEl.remove(), done && done())
            }
            scope.animate = !1;
            var transitionEndEventName = $transition.transitionEndEventName;
            if (transitionEndEventName) {
                var timeout = $timeout(afterAnimating, emulateTime);
                domEl.bind(transitionEndEventName, function() {
                    $timeout.cancel(timeout), afterAnimating(), scope.$apply()
                })
            } else $timeout(afterAnimating, 0)
        }
        var backdropDomEl, backdropScope, OPENED_MODAL_CLASS = "modal-open",
            openedWindows = $$stackedMap.createNew(),
            $modalStack = {};
        return $rootScope.$watch(backdropIndex, function(newBackdropIndex) {
            backdropScope && (backdropScope.index = newBackdropIndex)
        }), $document.bind("keydown", function(evt) {
            var modal;
            27 === evt.which && (modal = openedWindows.top(), modal && modal.value.keyboard && (evt.preventDefault(), $rootScope.$apply(function() {
                $modalStack.dismiss(modal.key, "escape key press")
            })))
        }), $modalStack.open = function(modalInstance, modal) {
            openedWindows.add(modalInstance, {
                deferred: modal.deferred,
                modalScope: modal.scope,
                backdrop: modal.backdrop,
                keyboard: modal.keyboard
            });
            var body = $document.find("body").eq(0),
                currBackdropIndex = backdropIndex();
            currBackdropIndex >= 0 && !backdropDomEl && (backdropScope = $rootScope.$new(!0), backdropScope.index = currBackdropIndex, backdropDomEl = $compile("<div modal-backdrop></div>")(backdropScope), body.append(backdropDomEl));
            var angularDomEl = angular.element("<div modal-window></div>");
            angularDomEl.attr({
                "template-url": modal.windowTemplateUrl,
                "window-class": modal.windowClass,
                size: modal.size,
                index: openedWindows.length() - 1,
                animate: "animate"
            }).html(modal.content);
            var modalDomEl = $compile(angularDomEl)(modal.scope);
            openedWindows.top().value.modalDomEl = modalDomEl, body.append(modalDomEl), body.addClass(OPENED_MODAL_CLASS)
        }, $modalStack.close = function(modalInstance, result) {
            var modalWindow = openedWindows.get(modalInstance).value;
            modalWindow && (modalWindow.deferred.resolve(result), removeModalWindow(modalInstance))
        }, $modalStack.dismiss = function(modalInstance, reason) {
            var modalWindow = openedWindows.get(modalInstance).value;
            modalWindow && (modalWindow.deferred.reject(reason), removeModalWindow(modalInstance))
        }, $modalStack.dismissAll = function(reason) {
            for (var topModal = this.getTop(); topModal;) this.dismiss(topModal.key, reason), topModal = this.getTop()
        }, $modalStack.getTop = function() {
            return openedWindows.top()
        }, $modalStack
    }
]).provider("$modal", function() {
    var $modalProvider = {
        options: {
            backdrop: !0,
            keyboard: !0
        },
        $get: ["$injector", "$rootScope", "$q", "$http", "$templateCache", "$controller", "$modalStack",
            function($injector, $rootScope, $q, $http, $templateCache, $controller, $modalStack) {
                function getTemplatePromise(options) {
                    return options.template ? $q.when(options.template) : $http.get(options.templateUrl, {
                        cache: $templateCache
                    }).then(function(result) {
                        return result.data
                    })
                }

                function getResolvePromises(resolves) {
                    var promisesArr = [];
                    return angular.forEach(resolves, function(value) {
                        (angular.isFunction(value) || angular.isArray(value)) && promisesArr.push($q.when($injector.invoke(value)))
                    }), promisesArr
                }
                var $modal = {};
                return $modal.open = function(modalOptions) {
                    var modalResultDeferred = $q.defer(),
                        modalOpenedDeferred = $q.defer(),
                        modalInstance = {
                            result: modalResultDeferred.promise,
                            opened: modalOpenedDeferred.promise,
                            close: function(result) {
                                $modalStack.close(modalInstance, result)
                            },
                            dismiss: function(reason) {
                                $modalStack.dismiss(modalInstance, reason)
                            }
                        };
                    if (modalOptions = angular.extend({}, $modalProvider.options, modalOptions), modalOptions.resolve = modalOptions.resolve || {}, !modalOptions.template && !modalOptions.templateUrl) throw new Error("One of template or templateUrl options is required.");
                    var templateAndResolvePromise = $q.all([getTemplatePromise(modalOptions)].concat(getResolvePromises(modalOptions.resolve)));
                    return templateAndResolvePromise.then(function(tplAndVars) {
                        var modalScope = (modalOptions.scope || $rootScope).$new();
                        modalScope.$close = modalInstance.close, modalScope.$dismiss = modalInstance.dismiss;
                        var ctrlInstance, ctrlLocals = {},
                            resolveIter = 1;
                        modalOptions.controller && (ctrlLocals.$scope = modalScope, ctrlLocals.$modalInstance = modalInstance, angular.forEach(modalOptions.resolve, function(value, key) {
                            ctrlLocals[key] = tplAndVars[resolveIter++]
                        }), ctrlInstance = $controller(modalOptions.controller, ctrlLocals)), $modalStack.open(modalInstance, {
                            scope: modalScope,
                            deferred: modalResultDeferred,
                            content: tplAndVars[0],
                            backdrop: modalOptions.backdrop,
                            keyboard: modalOptions.keyboard,
                            windowClass: modalOptions.windowClass,
                            windowTemplateUrl: modalOptions.windowTemplateUrl,
                            size: modalOptions.size
                        })
                    }, function(reason) {
                        modalResultDeferred.reject(reason)
                    }), templateAndResolvePromise.then(function() {
                        modalOpenedDeferred.resolve(!0)
                    }, function() {
                        modalOpenedDeferred.reject(!1)
                    }), modalInstance
                }, $modal
            }
        ]
    };
    return $modalProvider
}), angular.module("ui.bootstrap.pagination", []).controller("PaginationController", ["$scope", "$attrs", "$parse",
    function($scope, $attrs, $parse) {
        var self = this,
            ngModelCtrl = {
                $setViewValue: angular.noop
            },
            setNumPages = $attrs.numPages ? $parse($attrs.numPages).assign : angular.noop;
        this.init = function(ngModelCtrl_, config) {
            ngModelCtrl = ngModelCtrl_, this.config = config, ngModelCtrl.$render = function() {
                self.render()
            }, $attrs.itemsPerPage ? $scope.$parent.$watch($parse($attrs.itemsPerPage), function(value) {
                self.itemsPerPage = parseInt(value, 10), $scope.totalPages = self.calculateTotalPages()
            }) : this.itemsPerPage = config.itemsPerPage
        }, this.calculateTotalPages = function() {
            var totalPages = this.itemsPerPage < 1 ? 1 : Math.ceil($scope.totalItems / this.itemsPerPage);
            return Math.max(totalPages || 0, 1)
        }, this.render = function() {
            $scope.page = parseInt(ngModelCtrl.$viewValue, 10) || 1
        }, $scope.selectPage = function(page) {
            $scope.page !== page && page > 0 && page <= $scope.totalPages && (ngModelCtrl.$setViewValue(page), ngModelCtrl.$render())
        }, $scope.getText = function(key) {
            return $scope[key + "Text"] || self.config[key + "Text"]
        }, $scope.noPrevious = function() {
            return 1 === $scope.page
        }, $scope.noNext = function() {
            return $scope.page === $scope.totalPages
        }, $scope.$watch("totalItems", function() {
            $scope.totalPages = self.calculateTotalPages()
        }), $scope.$watch("totalPages", function(value) {
            setNumPages($scope.$parent, value), $scope.page > value ? $scope.selectPage(value) : ngModelCtrl.$render()
        })
    }
]).constant("paginationConfig", {
    itemsPerPage: 10,
    boundaryLinks: !1,
    directionLinks: !0,
    firstText: "First",
    previousText: "Previous",
    nextText: "Next",
    lastText: "Last",
    rotate: !0
}).directive("pagination", ["$parse", "paginationConfig",
    function($parse, paginationConfig) {
        return {
            restrict: "EA",
            scope: {
                totalItems: "=",
                firstText: "@",
                previousText: "@",
                nextText: "@",
                lastText: "@"
            },
            require: ["pagination", "?ngModel"],
            controller: "PaginationController",
            templateUrl: "template/pagination/pagination.html",
            replace: !0,
            link: function(scope, element, attrs, ctrls) {
                function makePage(number, text, isActive) {
                    return {
                        number: number,
                        text: text,
                        active: isActive
                    }
                }

                function getPages(currentPage, totalPages) {
                    var pages = [],
                        startPage = 1,
                        endPage = totalPages,
                        isMaxSized = angular.isDefined(maxSize) && totalPages > maxSize;
                    isMaxSized && (rotate ? (startPage = Math.max(currentPage - Math.floor(maxSize / 2), 1), endPage = startPage + maxSize - 1, endPage > totalPages && (endPage = totalPages, startPage = endPage - maxSize + 1)) : (startPage = (Math.ceil(currentPage / maxSize) - 1) * maxSize + 1, endPage = Math.min(startPage + maxSize - 1, totalPages)));
                    for (var number = startPage; endPage >= number; number++) {
                        var page = makePage(number, number, number === currentPage);
                        pages.push(page)
                    }
                    if (isMaxSized && !rotate) {
                        if (startPage > 1) {
                            var previousPageSet = makePage(startPage - 1, "...", !1);
                            pages.unshift(previousPageSet)
                        }
                        if (totalPages > endPage) {
                            var nextPageSet = makePage(endPage + 1, "...", !1);
                            pages.push(nextPageSet)
                        }
                    }
                    return pages
                }
                var paginationCtrl = ctrls[0],
                    ngModelCtrl = ctrls[1];
                if (ngModelCtrl) {
                    var maxSize = angular.isDefined(attrs.maxSize) ? scope.$parent.$eval(attrs.maxSize) : paginationConfig.maxSize,
                        rotate = angular.isDefined(attrs.rotate) ? scope.$parent.$eval(attrs.rotate) : paginationConfig.rotate;
                    scope.boundaryLinks = angular.isDefined(attrs.boundaryLinks) ? scope.$parent.$eval(attrs.boundaryLinks) : paginationConfig.boundaryLinks, scope.directionLinks = angular.isDefined(attrs.directionLinks) ? scope.$parent.$eval(attrs.directionLinks) : paginationConfig.directionLinks, paginationCtrl.init(ngModelCtrl, paginationConfig), attrs.maxSize && scope.$parent.$watch($parse(attrs.maxSize), function(value) {
                        maxSize = parseInt(value, 10), paginationCtrl.render()
                    });
                    var originalRender = paginationCtrl.render;
                    paginationCtrl.render = function() {
                        originalRender(), scope.page > 0 && scope.page <= scope.totalPages && (scope.pages = getPages(scope.page, scope.totalPages))
                    }
                }
            }
        }
    }
]).constant("pagerConfig", {
    itemsPerPage: 10,
    previousText: "« Previous",
    nextText: "Next »",
    align: !0
}).directive("pager", ["pagerConfig",
    function(pagerConfig) {
        return {
            restrict: "EA",
            scope: {
                totalItems: "=",
                previousText: "@",
                nextText: "@"
            },
            require: ["pager", "?ngModel"],
            controller: "PaginationController",
            templateUrl: "template/pagination/pager.html",
            replace: !0,
            link: function(scope, element, attrs, ctrls) {
                var paginationCtrl = ctrls[0],
                    ngModelCtrl = ctrls[1];
                ngModelCtrl && (scope.align = angular.isDefined(attrs.align) ? scope.$parent.$eval(attrs.align) : pagerConfig.align, paginationCtrl.init(ngModelCtrl, pagerConfig))
            }
        }
    }
]), angular.module("ui.bootstrap.tooltip", ["ui.bootstrap.position", "ui.bootstrap.bindHtml"]).provider("$tooltip", function() {
    function snake_case(name) {
        var regexp = /[A-Z]/g,
            separator = "-";
        return name.replace(regexp, function(letter, pos) {
            return (pos ? separator : "") + letter.toLowerCase()
        })
    }
    var defaultOptions = {
            placement: "top",
            animation: !0,
            popupDelay: 0
        },
        triggerMap = {
            mouseenter: "mouseleave",
            click: "click",
            focus: "blur"
        },
        globalOptions = {};
    this.options = function(value) {
        angular.extend(globalOptions, value)
    }, this.setTriggers = function(triggers) {
        angular.extend(triggerMap, triggers)
    }, this.$get = ["$window", "$compile", "$timeout", "$parse", "$document", "$position", "$interpolate",
        function($window, $compile, $timeout, $parse, $document, $position, $interpolate) {
            return function(type, prefix, defaultTriggerShow) {
                function getTriggers(trigger) {
                    var show = trigger || options.trigger || defaultTriggerShow,
                        hide = triggerMap[show] || show;
                    return {
                        show: show,
                        hide: hide
                    }
                }
                var options = angular.extend({}, defaultOptions, globalOptions),
                    directiveName = snake_case(type),
                    startSym = $interpolate.startSymbol(),
                    endSym = $interpolate.endSymbol(),
                    template = "<div " + directiveName + '-popup title="' + startSym + "tt_title" + endSym + '" content="' + startSym + "tt_content" + endSym + '" placement="' + startSym + "tt_placement" + endSym + '" animation="tt_animation" is-open="tt_isOpen"></div>';
                return {
                    restrict: "EA",
                    scope: !0,
                    compile: function() {
                        var tooltipLinker = $compile(template);
                        return function(scope, element, attrs) {
                            function toggleTooltipBind() {
                                scope.tt_isOpen ? hideTooltipBind() : showTooltipBind()
                            }

                            function showTooltipBind() {
                                (!hasEnableExp || scope.$eval(attrs[prefix + "Enable"])) && (scope.tt_popupDelay ? popupTimeout || (popupTimeout = $timeout(show, scope.tt_popupDelay, !1), popupTimeout.then(function(reposition) {
                                    reposition()
                                })) : show()())
                            }

                            function hideTooltipBind() {
                                scope.$apply(function() {
                                    hide()
                                })
                            }

                            function show() {
                                return popupTimeout = null, transitionTimeout && ($timeout.cancel(transitionTimeout), transitionTimeout = null), scope.tt_content ? (createTooltip(), tooltip.css({
                                    top: 0,
                                    left: 0,
                                    display: "block"
                                }), appendToBody ? $document.find("body").append(tooltip) : element.after(tooltip), positionTooltip(), scope.tt_isOpen = !0, scope.$digest(), positionTooltip) : angular.noop
                            }

                            function hide() {
                                scope.tt_isOpen = !1, $timeout.cancel(popupTimeout), popupTimeout = null, scope.tt_animation ? transitionTimeout || (transitionTimeout = $timeout(removeTooltip, 500)) : removeTooltip()
                            }

                            function createTooltip() {
                                tooltip && removeTooltip(), tooltip = tooltipLinker(scope, function() {}), scope.$digest()
                            }

                            function removeTooltip() {
                                transitionTimeout = null, tooltip && (tooltip.remove(), tooltip = null)
                            }
                            var tooltip, transitionTimeout, popupTimeout, appendToBody = angular.isDefined(options.appendToBody) ? options.appendToBody : !1,
                                triggers = getTriggers(void 0),
                                hasEnableExp = angular.isDefined(attrs[prefix + "Enable"]),
                                positionTooltip = function() {
                                    var ttPosition = $position.positionElements(element, tooltip, scope.tt_placement, appendToBody);
                                    ttPosition.top += "px", ttPosition.left += "px", tooltip.css(ttPosition)
                                };
                            scope.tt_isOpen = !1, attrs.$observe(type, function(val) {
                                scope.tt_content = val, !val && scope.tt_isOpen && hide()
                            }), attrs.$observe(prefix + "Title", function(val) {
                                scope.tt_title = val
                            }), attrs.$observe(prefix + "Placement", function(val) {
                                scope.tt_placement = angular.isDefined(val) ? val : options.placement
                            }), attrs.$observe(prefix + "PopupDelay", function(val) {
                                var delay = parseInt(val, 10);
                                scope.tt_popupDelay = isNaN(delay) ? options.popupDelay : delay
                            });
                            var unregisterTriggers = function() {
                                element.unbind(triggers.show, showTooltipBind), element.unbind(triggers.hide, hideTooltipBind)
                            };
                            attrs.$observe(prefix + "Trigger", function(val) {
                                unregisterTriggers(), triggers = getTriggers(val), triggers.show === triggers.hide ? element.bind(triggers.show, toggleTooltipBind) : (element.bind(triggers.show, showTooltipBind), element.bind(triggers.hide, hideTooltipBind))
                            });
                            var animation = scope.$eval(attrs[prefix + "Animation"]);
                            scope.tt_animation = angular.isDefined(animation) ? !!animation : options.animation, attrs.$observe(prefix + "AppendToBody", function(val) {
                                appendToBody = angular.isDefined(val) ? $parse(val)(scope) : appendToBody
                            }), appendToBody && scope.$on("$locationChangeSuccess", function() {
                                scope.tt_isOpen && hide()
                            }), scope.$on("$destroy", function() {
                                $timeout.cancel(transitionTimeout), $timeout.cancel(popupTimeout), unregisterTriggers(), removeTooltip()
                            })
                        }
                    }
                }
            }
        }
    ]
}).directive("tooltipPopup", function() {
    return {
        restrict: "EA",
        replace: !0,
        scope: {
            content: "@",
            placement: "@",
            animation: "&",
            isOpen: "&"
        },
        templateUrl: "template/tooltip/tooltip-popup.html"
    }
}).directive("tooltip", ["$tooltip",
    function($tooltip) {
        return $tooltip("tooltip", "tooltip", "mouseenter")
    }
]).directive("tooltipHtmlUnsafePopup", function() {
    return {
        restrict: "EA",
        replace: !0,
        scope: {
            content: "@",
            placement: "@",
            animation: "&",
            isOpen: "&"
        },
        templateUrl: "template/tooltip/tooltip-html-unsafe-popup.html"
    }
}).directive("tooltipHtmlUnsafe", ["$tooltip",
    function($tooltip) {
        return $tooltip("tooltipHtmlUnsafe", "tooltip", "mouseenter")
    }
]), angular.module("ui.bootstrap.popover", ["ui.bootstrap.tooltip"]).directive("popoverPopup", function() {
    return {
        restrict: "EA",
        replace: !0,
        scope: {
            title: "@",
            content: "@",
            placement: "@",
            animation: "&",
            isOpen: "&"
        },
        templateUrl: "template/popover/popover.html"
    }
}).directive("popover", ["$tooltip",
    function($tooltip) {
        return $tooltip("popover", "popover", "click")
    }
]), angular.module("ui.bootstrap.progressbar", []).constant("progressConfig", {
    animate: !0,
    max: 100
}).controller("ProgressController", ["$scope", "$attrs", "progressConfig",
    function($scope, $attrs, progressConfig) {
        var self = this,
            animate = angular.isDefined($attrs.animate) ? $scope.$parent.$eval($attrs.animate) : progressConfig.animate;
        this.bars = [], $scope.max = angular.isDefined($attrs.max) ? $scope.$parent.$eval($attrs.max) : progressConfig.max, this.addBar = function(bar, element) {
            animate || element.css({
                transition: "none"
            }), this.bars.push(bar), bar.$watch("value", function(value) {
                bar.percent = +(100 * value / $scope.max).toFixed(2)
            }), bar.$on("$destroy", function() {
                element = null, self.removeBar(bar)
            })
        }, this.removeBar = function(bar) {
            this.bars.splice(this.bars.indexOf(bar), 1)
        }
    }
]).directive("progress", function() {
    return {
        restrict: "EA",
        replace: !0,
        transclude: !0,
        controller: "ProgressController",
        require: "progress",
        scope: {},
        templateUrl: "template/progressbar/progress.html"
    }
}).directive("bar", function() {
    return {
        restrict: "EA",
        replace: !0,
        transclude: !0,
        require: "^progress",
        scope: {
            value: "=",
            type: "@"
        },
        templateUrl: "template/progressbar/bar.html",
        link: function(scope, element, attrs, progressCtrl) {
            progressCtrl.addBar(scope, element)
        }
    }
}).directive("progressbar", function() {
    return {
        restrict: "EA",
        replace: !0,
        transclude: !0,
        controller: "ProgressController",
        scope: {
            value: "=",
            type: "@"
        },
        templateUrl: "template/progressbar/progressbar.html",
        link: function(scope, element, attrs, progressCtrl) {
            progressCtrl.addBar(scope, angular.element(element.children()[0]))
        }
    }
}), angular.module("ui.bootstrap.rating", []).constant("ratingConfig", {
    max: 5,
    stateOn: null,
    stateOff: null
}).controller("RatingController", ["$scope", "$attrs", "ratingConfig",
    function($scope, $attrs, ratingConfig) {
        var ngModelCtrl = {
            $setViewValue: angular.noop
        };
        this.init = function(ngModelCtrl_) {
            ngModelCtrl = ngModelCtrl_, ngModelCtrl.$render = this.render, this.stateOn = angular.isDefined($attrs.stateOn) ? $scope.$parent.$eval($attrs.stateOn) : ratingConfig.stateOn, this.stateOff = angular.isDefined($attrs.stateOff) ? $scope.$parent.$eval($attrs.stateOff) : ratingConfig.stateOff;
            var ratingStates = angular.isDefined($attrs.ratingStates) ? $scope.$parent.$eval($attrs.ratingStates) : new Array(angular.isDefined($attrs.max) ? $scope.$parent.$eval($attrs.max) : ratingConfig.max);
            $scope.range = this.buildTemplateObjects(ratingStates)
        }, this.buildTemplateObjects = function(states) {
            for (var i = 0, n = states.length; n > i; i++) states[i] = angular.extend({
                index: i
            }, {
                stateOn: this.stateOn,
                stateOff: this.stateOff
            }, states[i]);
            return states
        }, $scope.rate = function(value) {
            !$scope.readonly && value >= 0 && value <= $scope.range.length && (ngModelCtrl.$setViewValue(value), ngModelCtrl.$render())
        }, $scope.enter = function(value) {
            $scope.readonly || ($scope.value = value), $scope.onHover({
                value: value
            })
        }, $scope.reset = function() {
            $scope.value = ngModelCtrl.$viewValue, $scope.onLeave()
        }, $scope.onKeydown = function(evt) {
            /(37|38|39|40)/.test(evt.which) && (evt.preventDefault(), evt.stopPropagation(), $scope.rate($scope.value + (38 === evt.which || 39 === evt.which ? 1 : -1)))
        }, this.render = function() {
            $scope.value = ngModelCtrl.$viewValue
        }
    }
]).directive("rating", function() {
    return {
        restrict: "EA",
        require: ["rating", "ngModel"],
        scope: {
            readonly: "=?",
            onHover: "&",
            onLeave: "&"
        },
        controller: "RatingController",
        templateUrl: "template/rating/rating.html",
        replace: !0,
        link: function(scope, element, attrs, ctrls) {
            var ratingCtrl = ctrls[0],
                ngModelCtrl = ctrls[1];
            ngModelCtrl && ratingCtrl.init(ngModelCtrl)
        }
    }
}), angular.module("ui.bootstrap.tabs", []).controller("TabsetController", ["$scope",
    function($scope) {
        var ctrl = this,
            tabs = ctrl.tabs = $scope.tabs = [];
        ctrl.select = function(selectedTab) {
            angular.forEach(tabs, function(tab) {
                tab.active && tab !== selectedTab && (tab.active = !1, tab.onDeselect())
            }), selectedTab.active = !0, selectedTab.onSelect()
        }, ctrl.addTab = function(tab) {
            tabs.push(tab), 1 === tabs.length ? tab.active = !0 : tab.active && ctrl.select(tab)
        }, ctrl.removeTab = function(tab) {
            var index = tabs.indexOf(tab);
            if (tab.active && tabs.length > 1) {
                var newActiveIndex = index == tabs.length - 1 ? index - 1 : index + 1;
                ctrl.select(tabs[newActiveIndex])
            }
            tabs.splice(index, 1)
        }
    }
]).directive("tabset", function() {
    return {
        restrict: "EA",
        transclude: !0,
        replace: !0,
        scope: {
            type: "@"
        },
        controller: "TabsetController",
        templateUrl: "template/tabs/tabset.html",
        link: function(scope, element, attrs) {
            scope.vertical = angular.isDefined(attrs.vertical) ? scope.$parent.$eval(attrs.vertical) : !1, scope.justified = angular.isDefined(attrs.justified) ? scope.$parent.$eval(attrs.justified) : !1
        }
    }
}).directive("tab", ["$parse",
    function($parse) {
        return {
            require: "^tabset",
            restrict: "EA",
            replace: !0,
            templateUrl: "template/tabs/tab.html",
            transclude: !0,
            scope: {
                active: "=?",
                heading: "@",
                onSelect: "&select",
                onDeselect: "&deselect"
            },
            controller: function() {},
            compile: function(elm, attrs, transclude) {
                return function(scope, elm, attrs, tabsetCtrl) {
                    scope.$watch("active", function(active) {
                        active && tabsetCtrl.select(scope)
                    }), scope.disabled = !1, attrs.disabled && scope.$parent.$watch($parse(attrs.disabled), function(value) {
                        scope.disabled = !!value
                    }), scope.select = function() {
                        scope.disabled || (scope.active = !0)
                    }, tabsetCtrl.addTab(scope), scope.$on("$destroy", function() {
                        tabsetCtrl.removeTab(scope)
                    }), scope.$transcludeFn = transclude
                }
            }
        }
    }
]).directive("tabHeadingTransclude", [
    function() {
        return {
            restrict: "A",
            require: "^tab",
            link: function(scope, elm) {
                scope.$watch("headingElement", function(heading) {
                    heading && (elm.html(""), elm.append(heading))
                })
            }
        }
    }
]).directive("tabContentTransclude", function() {
    function isTabHeading(node) {
        return node.tagName && (node.hasAttribute("tab-heading") || node.hasAttribute("data-tab-heading") || "tab-heading" === node.tagName.toLowerCase() || "data-tab-heading" === node.tagName.toLowerCase())
    }
    return {
        restrict: "A",
        require: "^tabset",
        link: function(scope, elm, attrs) {
            var tab = scope.$eval(attrs.tabContentTransclude);
            tab.$transcludeFn(tab.$parent, function(contents) {
                angular.forEach(contents, function(node) {
                    isTabHeading(node) ? tab.headingElement = node : elm.append(node)
                })
            })
        }
    }
}), angular.module("ui.bootstrap.timepicker", []).constant("timepickerConfig", {
    hourStep: 1,
    minuteStep: 1,
    showMeridian: !0,
    meridians: null,
    readonlyInput: !1,
    mousewheel: !0
}).controller("TimepickerController", ["$scope", "$attrs", "$parse", "$log", "$locale", "timepickerConfig",
    function($scope, $attrs, $parse, $log, $locale, timepickerConfig) {
        function getHoursFromTemplate() {
            var hours = parseInt($scope.hours, 10),
                valid = $scope.showMeridian ? hours > 0 && 13 > hours : hours >= 0 && 24 > hours;
            return valid ? ($scope.showMeridian && (12 === hours && (hours = 0), $scope.meridian === meridians[1] && (hours += 12)), hours) : void 0
        }

        function getMinutesFromTemplate() {
            var minutes = parseInt($scope.minutes, 10);
            return minutes >= 0 && 60 > minutes ? minutes : void 0
        }

        function pad(value) {
            return angular.isDefined(value) && value.toString().length < 2 ? "0" + value : value
        }

        function refresh(keyboardChange) {
            makeValid(), ngModelCtrl.$setViewValue(new Date(selected)), updateTemplate(keyboardChange)
        }

        function makeValid() {
            ngModelCtrl.$setValidity("time", !0), $scope.invalidHours = !1, $scope.invalidMinutes = !1
        }

        function updateTemplate(keyboardChange) {
            var hours = selected.getHours(),
                minutes = selected.getMinutes();
            $scope.showMeridian && (hours = 0 === hours || 12 === hours ? 12 : hours % 12), $scope.hours = "h" === keyboardChange ? hours : pad(hours), $scope.minutes = "m" === keyboardChange ? minutes : pad(minutes), $scope.meridian = selected.getHours() < 12 ? meridians[0] : meridians[1]
        }

        function addMinutes(minutes) {
            var dt = new Date(selected.getTime() + 6e4 * minutes);
            selected.setHours(dt.getHours(), dt.getMinutes()), refresh()
        }
        var selected = new Date,
            ngModelCtrl = {
                $setViewValue: angular.noop
            },
            meridians = angular.isDefined($attrs.meridians) ? $scope.$parent.$eval($attrs.meridians) : timepickerConfig.meridians || $locale.DATETIME_FORMATS.AMPMS;
        this.init = function(ngModelCtrl_, inputs) {
            ngModelCtrl = ngModelCtrl_, ngModelCtrl.$render = this.render;
            var hoursInputEl = inputs.eq(0),
                minutesInputEl = inputs.eq(1),
                mousewheel = angular.isDefined($attrs.mousewheel) ? $scope.$parent.$eval($attrs.mousewheel) : timepickerConfig.mousewheel;
            mousewheel && this.setupMousewheelEvents(hoursInputEl, minutesInputEl), $scope.readonlyInput = angular.isDefined($attrs.readonlyInput) ? $scope.$parent.$eval($attrs.readonlyInput) : timepickerConfig.readonlyInput, this.setupInputEvents(hoursInputEl, minutesInputEl)
        };
        var hourStep = timepickerConfig.hourStep;
        $attrs.hourStep && $scope.$parent.$watch($parse($attrs.hourStep), function(value) {
            hourStep = parseInt(value, 10)
        });
        var minuteStep = timepickerConfig.minuteStep;
        $attrs.minuteStep && $scope.$parent.$watch($parse($attrs.minuteStep), function(value) {
            minuteStep = parseInt(value, 10)
        }), $scope.showMeridian = timepickerConfig.showMeridian, $attrs.showMeridian && $scope.$parent.$watch($parse($attrs.showMeridian), function(value) {
            if ($scope.showMeridian = !!value, ngModelCtrl.$error.time) {
                var hours = getHoursFromTemplate(),
                    minutes = getMinutesFromTemplate();
                angular.isDefined(hours) && angular.isDefined(minutes) && (selected.setHours(hours), refresh())
            } else updateTemplate()
        }), this.setupMousewheelEvents = function(hoursInputEl, minutesInputEl) {
            var isScrollingUp = function(e) {
                e.originalEvent && (e = e.originalEvent);
                var delta = e.wheelDelta ? e.wheelDelta : -e.deltaY;
                return e.detail || delta > 0
            };
            hoursInputEl.bind("mousewheel wheel", function(e) {
                $scope.$apply(isScrollingUp(e) ? $scope.incrementHours() : $scope.decrementHours()), e.preventDefault()
            }), minutesInputEl.bind("mousewheel wheel", function(e) {
                $scope.$apply(isScrollingUp(e) ? $scope.incrementMinutes() : $scope.decrementMinutes()), e.preventDefault()
            })
        }, this.setupInputEvents = function(hoursInputEl, minutesInputEl) {
            if ($scope.readonlyInput) return $scope.updateHours = angular.noop, void($scope.updateMinutes = angular.noop);
            var invalidate = function(invalidHours, invalidMinutes) {
                ngModelCtrl.$setViewValue(null), ngModelCtrl.$setValidity("time", !1), angular.isDefined(invalidHours) && ($scope.invalidHours = invalidHours), angular.isDefined(invalidMinutes) && ($scope.invalidMinutes = invalidMinutes)
            };
            $scope.updateHours = function() {
                var hours = getHoursFromTemplate();
                angular.isDefined(hours) ? (selected.setHours(hours), refresh("h")) : invalidate(!0)
            }, hoursInputEl.bind("blur", function() {
                !$scope.invalidHours && $scope.hours < 10 && $scope.$apply(function() {
                    $scope.hours = pad($scope.hours)
                })
            }), $scope.updateMinutes = function() {
                var minutes = getMinutesFromTemplate();
                angular.isDefined(minutes) ? (selected.setMinutes(minutes), refresh("m")) : invalidate(void 0, !0)
            }, minutesInputEl.bind("blur", function() {
                !$scope.invalidMinutes && $scope.minutes < 10 && $scope.$apply(function() {
                    $scope.minutes = pad($scope.minutes)
                })
            })
        }, this.render = function() {
            var date = ngModelCtrl.$modelValue ? new Date(ngModelCtrl.$modelValue) : null;
            isNaN(date) ? (ngModelCtrl.$setValidity("time", !1), $log.error('Timepicker directive: "ng-model" value must be a Date object, a number of milliseconds since 01.01.1970 or a string representing an RFC2822 or ISO 8601 date.')) : (date && (selected = date), makeValid(), updateTemplate())
        }, $scope.incrementHours = function() {
            addMinutes(60 * hourStep)
        }, $scope.decrementHours = function() {
            addMinutes(60 * -hourStep)
        }, $scope.incrementMinutes = function() {
            addMinutes(minuteStep)
        }, $scope.decrementMinutes = function() {
            addMinutes(-minuteStep)
        }, $scope.toggleMeridian = function() {
            addMinutes(720 * (selected.getHours() < 12 ? 1 : -1))
        }
    }
]).directive("timepicker", function() {
    return {
        restrict: "EA",
        require: ["timepicker", "?^ngModel"],
        controller: "TimepickerController",
        replace: !0,
        scope: {},
        templateUrl: "template/timepicker/timepicker.html",
        link: function(scope, element, attrs, ctrls) {
            var timepickerCtrl = ctrls[0],
                ngModelCtrl = ctrls[1];
            ngModelCtrl && timepickerCtrl.init(ngModelCtrl, element.find("input"))
        }
    }
}), angular.module("ui.bootstrap.typeahead", ["ui.bootstrap.position", "ui.bootstrap.bindHtml"]).factory("typeaheadParser", ["$parse",
    function($parse) {
        var TYPEAHEAD_REGEXP = /^\s*(.*?)(?:\s+as\s+(.*?))?\s+for\s+(?:([\$\w][\$\w\d]*))\s+in\s+(.*)$/;
        return {
            parse: function(input) {
                var match = input.match(TYPEAHEAD_REGEXP);
                if (!match) throw new Error('Expected typeahead specification in form of "_modelValue_ (as _label_)? for _item_ in _collection_" but got "' + input + '".');
                return {
                    itemName: match[3],
                    source: $parse(match[4]),
                    viewMapper: $parse(match[2] || match[1]),
                    modelMapper: $parse(match[1])
                }
            }
        }
    }
]).directive("typeahead", ["$compile", "$parse", "$q", "$timeout", "$document", "$position", "typeaheadParser",
    function($compile, $parse, $q, $timeout, $document, $position, typeaheadParser) {
        var HOT_KEYS = [9, 13, 27, 38, 40];
        return {
            require: "ngModel",
            link: function(originalScope, element, attrs, modelCtrl) {
                var hasFocus, minSearch = originalScope.$eval(attrs.typeaheadMinLength) || 1,
                    waitTime = originalScope.$eval(attrs.typeaheadWaitMs) || 0,
                    isEditable = originalScope.$eval(attrs.typeaheadEditable) !== !1,
                    isLoadingSetter = $parse(attrs.typeaheadLoading).assign || angular.noop,
                    onSelectCallback = $parse(attrs.typeaheadOnSelect),
                    inputFormatter = attrs.typeaheadInputFormatter ? $parse(attrs.typeaheadInputFormatter) : void 0,
                    appendToBody = attrs.typeaheadAppendToBody ? originalScope.$eval(attrs.typeaheadAppendToBody) : !1,
                    $setModelValue = $parse(attrs.ngModel).assign,
                    parserResult = typeaheadParser.parse(attrs.typeahead),
                    scope = originalScope.$new();
                originalScope.$on("$destroy", function() {
                    scope.$destroy()
                });
                var popupId = "typeahead-" + scope.$id + "-" + Math.floor(1e4 * Math.random());
                element.attr({
                    "aria-autocomplete": "list",
                    "aria-expanded": !1,
                    "aria-owns": popupId
                });
                var popUpEl = angular.element("<div typeahead-popup></div>");
                popUpEl.attr({
                    id: popupId,
                    matches: "matches",
                    active: "activeIdx",
                    select: "select(activeIdx)",
                    query: "query",
                    position: "position"
                }), angular.isDefined(attrs.typeaheadTemplateUrl) && popUpEl.attr("template-url", attrs.typeaheadTemplateUrl);
                var resetMatches = function() {
                        scope.matches = [], scope.activeIdx = -1, element.attr("aria-expanded", !1)
                    },
                    getMatchId = function(index) {
                        return popupId + "-option-" + index
                    };
                scope.$watch("activeIdx", function(index) {
                    0 > index ? element.removeAttr("aria-activedescendant") : element.attr("aria-activedescendant", getMatchId(index))
                });
                var getMatchesAsync = function(inputValue) {
                    var locals = {
                        $viewValue: inputValue
                    };
                    isLoadingSetter(originalScope, !0), $q.when(parserResult.source(originalScope, locals)).then(function(matches) {
                        var onCurrentRequest = inputValue === modelCtrl.$viewValue;
                        if (onCurrentRequest && hasFocus)
                            if (matches.length > 0) {
                                scope.activeIdx = 0, scope.matches.length = 0;
                                for (var i = 0; i < matches.length; i++) locals[parserResult.itemName] = matches[i], scope.matches.push({
                                    id: getMatchId(i),
                                    label: parserResult.viewMapper(scope, locals),
                                    model: matches[i]
                                });
                                scope.query = inputValue, scope.position = appendToBody ? $position.offset(element) : $position.position(element), scope.position.top = scope.position.top + element.prop("offsetHeight"), element.attr("aria-expanded", !0)
                            } else resetMatches();
                        onCurrentRequest && isLoadingSetter(originalScope, !1)
                    }, function() {
                        resetMatches(), isLoadingSetter(originalScope, !1)
                    })
                };
                resetMatches(), scope.query = void 0;
                var timeoutPromise;
                modelCtrl.$parsers.unshift(function(inputValue) {
                    return hasFocus = !0, inputValue && inputValue.length >= minSearch ? waitTime > 0 ? (timeoutPromise && $timeout.cancel(timeoutPromise), timeoutPromise = $timeout(function() {
                        getMatchesAsync(inputValue)
                    }, waitTime)) : getMatchesAsync(inputValue) : (isLoadingSetter(originalScope, !1), resetMatches()), isEditable ? inputValue : inputValue ? void modelCtrl.$setValidity("editable", !1) : (modelCtrl.$setValidity("editable", !0), inputValue)
                }), modelCtrl.$formatters.push(function(modelValue) {
                    var candidateViewValue, emptyViewValue, locals = {};
                    return inputFormatter ? (locals.$model = modelValue, inputFormatter(originalScope, locals)) : (locals[parserResult.itemName] = modelValue, candidateViewValue = parserResult.viewMapper(originalScope, locals), locals[parserResult.itemName] = void 0, emptyViewValue = parserResult.viewMapper(originalScope, locals), candidateViewValue !== emptyViewValue ? candidateViewValue : modelValue)
                }), scope.select = function(activeIdx) {
                    var model, item, locals = {};
                    locals[parserResult.itemName] = item = scope.matches[activeIdx].model, model = parserResult.modelMapper(originalScope, locals), $setModelValue(originalScope, model), modelCtrl.$setValidity("editable", !0), onSelectCallback(originalScope, {
                        $item: item,
                        $model: model,
                        $label: parserResult.viewMapper(originalScope, locals)
                    }), resetMatches(), $timeout(function() {
                        element[0].focus()
                    }, 0, !1)
                }, element.bind("keydown", function(evt) {
                    0 !== scope.matches.length && -1 !== HOT_KEYS.indexOf(evt.which) && (evt.preventDefault(), 40 === evt.which ? (scope.activeIdx = (scope.activeIdx + 1) % scope.matches.length, scope.$digest()) : 38 === evt.which ? (scope.activeIdx = (scope.activeIdx ? scope.activeIdx : scope.matches.length) - 1, scope.$digest()) : 13 === evt.which || 9 === evt.which ? scope.$apply(function() {
                        scope.select(scope.activeIdx)
                    }) : 27 === evt.which && (evt.stopPropagation(), resetMatches(), scope.$digest()))
                }), element.bind("blur", function() {
                    hasFocus = !1
                });
                var dismissClickHandler = function(evt) {
                    element[0] !== evt.target && (resetMatches(), scope.$digest())
                };
                $document.bind("click", dismissClickHandler), originalScope.$on("$destroy", function() {
                    $document.unbind("click", dismissClickHandler)
                });
                var $popup = $compile(popUpEl)(scope);
                appendToBody ? $document.find("body").append($popup) : element.after($popup)
            }
        }
    }
]).directive("typeaheadPopup", function() {
    return {
        restrict: "EA",
        scope: {
            matches: "=",
            query: "=",
            active: "=",
            position: "=",
            select: "&"
        },
        replace: !0,
        templateUrl: "template/typeahead/typeahead-popup.html",
        link: function(scope, element, attrs) {
            scope.templateUrl = attrs.templateUrl, scope.isOpen = function() {
                return scope.matches.length > 0
            }, scope.isActive = function(matchIdx) {
                return scope.active == matchIdx
            }, scope.selectActive = function(matchIdx) {
                scope.active = matchIdx
            }, scope.selectMatch = function(activeIdx) {
                scope.select({
                    activeIdx: activeIdx
                })
            }
        }
    }
}).directive("typeaheadMatch", ["$http", "$templateCache", "$compile", "$parse",
    function($http, $templateCache, $compile, $parse) {
        return {
            restrict: "EA",
            scope: {
                index: "=",
                match: "=",
                query: "="
            },
            link: function(scope, element, attrs) {
                var tplUrl = $parse(attrs.templateUrl)(scope.$parent) || "template/typeahead/typeahead-match.html";
                $http.get(tplUrl, {
                    cache: $templateCache
                }).success(function(tplContent) {
                    element.replaceWith($compile(tplContent.trim())(scope))
                })
            }
        }
    }
]).filter("typeaheadHighlight", function() {
    function escapeRegexp(queryToEscape) {
        return queryToEscape.replace(/([.?*+^$[\]\\(){}|-])/g, "\\$1")
    }
    return function(matchItem, query) {
        return query ? ("" + matchItem).replace(new RegExp(escapeRegexp(query), "gi"), "<strong>$&</strong>") : matchItem
    }
}), angular.module("template/accordion/accordion-group.html", []).run(["$templateCache",
    function($templateCache) {
        $templateCache.put("template/accordion/accordion-group.html", '<div class="panel panel-default">\n  <div class="panel-heading">\n    <h4 class="panel-title">\n      <a class="accordion-toggle" ng-click="toggleOpen()" accordion-transclude="heading"><span ng-class="{\'text-muted\': isDisabled}">{{heading}}</span></a>\n    </h4>\n  </div>\n  <div class="panel-collapse" collapse="!isOpen">\n	  <div class="panel-body" ng-transclude></div>\n  </div>\n</div>')
    }
]), angular.module("template/accordion/accordion.html", []).run(["$templateCache",
    function($templateCache) {
        $templateCache.put("template/accordion/accordion.html", '<div class="panel-group" ng-transclude></div>')
    }
]), angular.module("template/alert/alert.html", []).run(["$templateCache",
    function($templateCache) {
        $templateCache.put("template/alert/alert.html", '<div class="alert" ng-class="{\'alert-{{type || \'warning\'}}\': true, \'alert-dismissable\': closeable}" role="alert">\n    <button ng-show="closeable" type="button" class="close" ng-click="close()">\n        <span aria-hidden="true">&times;</span>\n        <span class="sr-only">Close</span>\n    </button>\n    <div ng-transclude></div>\n</div>\n')
    }
]), angular.module("template/carousel/carousel.html", []).run(["$templateCache",
    function($templateCache) {
        $templateCache.put("template/carousel/carousel.html", '<div ng-mouseenter="pause()" ng-mouseleave="play()" class="carousel" ng-swipe-right="prev()" ng-swipe-left="next()">\n    <ol class="carousel-indicators" ng-show="slides.length > 1">\n        <li ng-repeat="slide in slides track by $index" ng-class="{active: isActive(slide)}" ng-click="select(slide)"></li>\n    </ol>\n    <div class="carousel-inner" ng-transclude></div>\n    <a class="left carousel-control" ng-click="prev()" ng-show="slides.length > 1"><span class="glyphicon glyphicon-chevron-left"></span></a>\n    <a class="right carousel-control" ng-click="next()" ng-show="slides.length > 1"><span class="glyphicon glyphicon-chevron-right"></span></a>\n</div>\n')
    }
]), angular.module("template/carousel/slide.html", []).run(["$templateCache",
    function($templateCache) {
        $templateCache.put("template/carousel/slide.html", "<div ng-class=\"{\n    'active': leaving || (active && !entering),\n    'prev': (next || active) && direction=='prev',\n    'next': (next || active) && direction=='next',\n    'right': direction=='prev',\n    'left': direction=='next'\n  }\" class=\"item text-center\" ng-transclude></div>\n")
    }
]), angular.module("template/datepicker/datepicker.html", []).run(["$templateCache",
    function($templateCache) {
        $templateCache.put("template/datepicker/datepicker.html", '<div ng-switch="datepickerMode" role="application" ng-keydown="keydown($event)">\n  <daypicker ng-switch-when="day" tabindex="0"></daypicker>\n  <monthpicker ng-switch-when="month" tabindex="0"></monthpicker>\n  <yearpicker ng-switch-when="year" tabindex="0"></yearpicker>\n</div>')
    }
]), angular.module("template/datepicker/day.html", []).run(["$templateCache",
    function($templateCache) {
        $templateCache.put("template/datepicker/day.html", '<table role="grid" aria-labelledby="{{uniqueId}}-title" aria-activedescendant="{{activeDateId}}">\n  <thead>\n    <tr>\n      <th><button type="button" class="btn btn-default btn-sm pull-left" ng-click="move(-1)" tabindex="-1"><i class="glyphicon glyphicon-chevron-left"></i></button></th>\n      <th colspan="{{5 + showWeeks}}"><button id="{{uniqueId}}-title" role="heading" aria-live="assertive" aria-atomic="true" type="button" class="btn btn-default btn-sm" ng-click="toggleMode()" tabindex="-1" style="width:100%;"><strong>{{title}}</strong></button></th>\n      <th><button type="button" class="btn btn-default btn-sm pull-right" ng-click="move(1)" tabindex="-1"><i class="glyphicon glyphicon-chevron-right"></i></button></th>\n    </tr>\n    <tr>\n      <th ng-show="showWeeks" class="text-center"></th>\n      <th ng-repeat="label in labels track by $index" class="text-center"><small aria-label="{{label.full}}">{{label.abbr}}</small></th>\n    </tr>\n  </thead>\n  <tbody>\n    <tr ng-repeat="row in rows track by $index">\n      <td ng-show="showWeeks" class="text-center h6"><em>{{ weekNumbers[$index] }}</em></td>\n      <td ng-repeat="dt in row track by dt.date" class="text-center" role="gridcell" id="{{dt.uid}}" aria-disabled="{{!!dt.disabled}}">\n        <button type="button" style="width:100%;" class="btn btn-default btn-sm" ng-class="{\'btn-info\': dt.selected, active: isActive(dt)}" ng-click="select(dt.date)" ng-disabled="dt.disabled" tabindex="-1"><span ng-class="{\'text-muted\': dt.secondary, \'text-info\': dt.current}">{{dt.label}}</span></button>\n      </td>\n    </tr>\n  </tbody>\n</table>\n')
    }
]), angular.module("template/datepicker/month.html", []).run(["$templateCache",
    function($templateCache) {
        $templateCache.put("template/datepicker/month.html", '<table role="grid" aria-labelledby="{{uniqueId}}-title" aria-activedescendant="{{activeDateId}}">\n  <thead>\n    <tr>\n      <th><button type="button" class="btn btn-default btn-sm pull-left" ng-click="move(-1)" tabindex="-1"><i class="glyphicon glyphicon-chevron-left"></i></button></th>\n      <th><button id="{{uniqueId}}-title" role="heading" aria-live="assertive" aria-atomic="true" type="button" class="btn btn-default btn-sm" ng-click="toggleMode()" tabindex="-1" style="width:100%;"><strong>{{title}}</strong></button></th>\n      <th><button type="button" class="btn btn-default btn-sm pull-right" ng-click="move(1)" tabindex="-1"><i class="glyphicon glyphicon-chevron-right"></i></button></th>\n    </tr>\n  </thead>\n  <tbody>\n    <tr ng-repeat="row in rows track by $index">\n      <td ng-repeat="dt in row track by dt.date" class="text-center" role="gridcell" id="{{dt.uid}}" aria-disabled="{{!!dt.disabled}}">\n        <button type="button" style="width:100%;" class="btn btn-default" ng-class="{\'btn-info\': dt.selected, active: isActive(dt)}" ng-click="select(dt.date)" ng-disabled="dt.disabled" tabindex="-1"><span ng-class="{\'text-info\': dt.current}">{{dt.label}}</span></button>\n      </td>\n    </tr>\n  </tbody>\n</table>\n')
    }
]), angular.module("template/datepicker/popup.html", []).run(["$templateCache",
    function($templateCache) {
        $templateCache.put("template/datepicker/popup.html", '<ul class="dropdown-menu" ng-style="{display: (isOpen && \'block\') || \'none\', top: position.top+\'px\', left: position.left+\'px\'}" ng-keydown="keydown($event)">\n	<li ng-transclude></li>\n	<li ng-if="showButtonBar" style="padding:10px 9px 2px">\n		<span class="btn-group">\n			<button type="button" class="btn btn-sm btn-info" ng-click="select(\'today\')">{{ getText(\'current\') }}</button>\n			<button type="button" class="btn btn-sm btn-danger" ng-click="select(null)">{{ getText(\'clear\') }}</button>\n		</span>\n		<button type="button" class="btn btn-sm btn-success pull-right" ng-click="close()">{{ getText(\'close\') }}</button>\n	</li>\n</ul>\n')
    }
]), angular.module("template/datepicker/year.html", []).run(["$templateCache",
    function($templateCache) {
        $templateCache.put("template/datepicker/year.html", '<table role="grid" aria-labelledby="{{uniqueId}}-title" aria-activedescendant="{{activeDateId}}">\n  <thead>\n    <tr>\n      <th><button type="button" class="btn btn-default btn-sm pull-left" ng-click="move(-1)" tabindex="-1"><i class="glyphicon glyphicon-chevron-left"></i></button></th>\n      <th colspan="3"><button id="{{uniqueId}}-title" role="heading" aria-live="assertive" aria-atomic="true" type="button" class="btn btn-default btn-sm" ng-click="toggleMode()" tabindex="-1" style="width:100%;"><strong>{{title}}</strong></button></th>\n      <th><button type="button" class="btn btn-default btn-sm pull-right" ng-click="move(1)" tabindex="-1"><i class="glyphicon glyphicon-chevron-right"></i></button></th>\n    </tr>\n  </thead>\n  <tbody>\n    <tr ng-repeat="row in rows track by $index">\n      <td ng-repeat="dt in row track by dt.date" class="text-center" role="gridcell" id="{{dt.uid}}" aria-disabled="{{!!dt.disabled}}">\n        <button type="button" style="width:100%;" class="btn btn-default" ng-class="{\'btn-info\': dt.selected, active: isActive(dt)}" ng-click="select(dt.date)" ng-disabled="dt.disabled" tabindex="-1"><span ng-class="{\'text-info\': dt.current}">{{dt.label}}</span></button>\n      </td>\n    </tr>\n  </tbody>\n</table>\n')
    }
]), angular.module("template/modal/backdrop.html", []).run(["$templateCache",
    function($templateCache) {
        $templateCache.put("template/modal/backdrop.html", '<div class="modal-backdrop fade"\n     ng-class="{in: animate}"\n     ng-style="{\'z-index\': 1040 + (index && 1 || 0) + index*10}"\n></div>\n')
    }
]), angular.module("template/modal/window.html", []).run(["$templateCache",
    function($templateCache) {
        $templateCache.put("template/modal/window.html", '<div tabindex="-1" role="dialog" class="modal fade" ng-class="{in: animate}" ng-style="{\'z-index\': 1050 + index*10, display: \'block\'}" ng-click="close($event)">\n    <div class="modal-dialog" ng-class="{\'modal-sm\': size == \'sm\', \'modal-lg\': size == \'lg\'}"><div class="modal-content" ng-transclude></div></div>\n</div>')
    }
]), angular.module("template/pagination/pager.html", []).run(["$templateCache",
    function($templateCache) {
        $templateCache.put("template/pagination/pager.html", '<ul class="pager">\n  <li ng-class="{disabled: noPrevious(), previous: align}"><a href ng-click="selectPage(page - 1)">{{getText(\'previous\')}}</a></li>\n  <li ng-class="{disabled: noNext(), next: align}"><a href ng-click="selectPage(page + 1)">{{getText(\'next\')}}</a></li>\n</ul>')
    }
]), angular.module("template/pagination/pagination.html", []).run(["$templateCache",
    function($templateCache) {
        $templateCache.put("template/pagination/pagination.html", '<ul class="pagination">\n  <li ng-if="boundaryLinks" ng-class="{disabled: noPrevious()}"><a href ng-click="selectPage(1)">{{getText(\'first\')}}</a></li>\n  <li ng-if="directionLinks" ng-class="{disabled: noPrevious()}"><a href ng-click="selectPage(page - 1)">{{getText(\'previous\')}}</a></li>\n  <li ng-repeat="page in pages track by $index" ng-class="{active: page.active}"><a href ng-click="selectPage(page.number)">{{page.text}}</a></li>\n  <li ng-if="directionLinks" ng-class="{disabled: noNext()}"><a href ng-click="selectPage(page + 1)">{{getText(\'next\')}}</a></li>\n  <li ng-if="boundaryLinks" ng-class="{disabled: noNext()}"><a href ng-click="selectPage(totalPages)">{{getText(\'last\')}}</a></li>\n</ul>')
    }
]), angular.module("template/tooltip/tooltip-html-unsafe-popup.html", []).run(["$templateCache",
    function($templateCache) {
        $templateCache.put("template/tooltip/tooltip-html-unsafe-popup.html", '<div class="tooltip {{placement}}" ng-class="{ in: isOpen(), fade: animation() }">\n  <div class="tooltip-arrow"></div>\n  <div class="tooltip-inner" bind-html-unsafe="content"></div>\n</div>\n')
    }
]), angular.module("template/tooltip/tooltip-popup.html", []).run(["$templateCache",
    function($templateCache) {
        $templateCache.put("template/tooltip/tooltip-popup.html", '<div class="tooltip {{placement}}" ng-class="{ in: isOpen(), fade: animation() }">\n  <div class="tooltip-arrow"></div>\n  <div class="tooltip-inner" ng-bind="content"></div>\n</div>\n')
    }
]), angular.module("template/popover/popover.html", []).run(["$templateCache",
    function($templateCache) {
        $templateCache.put("template/popover/popover.html", '<div class="popover {{placement}}" ng-class="{ in: isOpen(), fade: animation() }">\n  <div class="arrow"></div>\n\n  <div class="popover-inner">\n      <h3 class="popover-title" ng-bind="title" ng-show="title"></h3>\n      <div class="popover-content" ng-bind="content"></div>\n  </div>\n</div>\n')
    }
]), angular.module("template/progressbar/bar.html", []).run(["$templateCache",
    function($templateCache) {
        $templateCache.put("template/progressbar/bar.html", '<div class="progress-bar" ng-class="type && \'progress-bar-\' + type" role="progressbar" aria-valuenow="{{value}}" aria-valuemin="0" aria-valuemax="{{max}}" ng-style="{width: percent + \'%\'}" aria-valuetext="{{percent | number:0}}%" ng-transclude></div>')
    }
]), angular.module("template/progressbar/progress.html", []).run(["$templateCache",
    function($templateCache) {
        $templateCache.put("template/progressbar/progress.html", '<div class="progress" ng-transclude></div>')
    }
]), angular.module("template/progressbar/progressbar.html", []).run(["$templateCache",
    function($templateCache) {
        $templateCache.put("template/progressbar/progressbar.html", '<div class="progress">\n  <div class="progress-bar" ng-class="type && \'progress-bar-\' + type" role="progressbar" aria-valuenow="{{value}}" aria-valuemin="0" aria-valuemax="{{max}}" ng-style="{width: percent + \'%\'}" aria-valuetext="{{percent | number:0}}%" ng-transclude></div>\n</div>')
    }
]), angular.module("template/rating/rating.html", []).run(["$templateCache",
    function($templateCache) {
        $templateCache.put("template/rating/rating.html", '<span ng-mouseleave="reset()" ng-keydown="onKeydown($event)" tabindex="0" role="slider" aria-valuemin="0" aria-valuemax="{{range.length}}" aria-valuenow="{{value}}">\n    <i ng-repeat="r in range track by $index" ng-mouseenter="enter($index + 1)" ng-click="rate($index + 1)" class="glyphicon" ng-class="$index < value && (r.stateOn || \'glyphicon-star\') || (r.stateOff || \'glyphicon-star-empty\')">\n        <span class="sr-only">({{ $index < value ? \'*\' : \' \' }})</span>\n    </i>\n</span>')
    }
]), angular.module("template/tabs/tab.html", []).run(["$templateCache",
    function($templateCache) {
        $templateCache.put("template/tabs/tab.html", '<li ng-class="{active: active, disabled: disabled}">\n  <a ng-click="select()" tab-heading-transclude>{{heading}}</a>\n</li>\n')
    }
]), angular.module("template/tabs/tabset-titles.html", []).run(["$templateCache",
    function($templateCache) {
        $templateCache.put("template/tabs/tabset-titles.html", "<ul class=\"nav {{type && 'nav-' + type}}\" ng-class=\"{'nav-stacked': vertical}\">\n</ul>\n")
    }
]), angular.module("template/tabs/tabset.html", []).run(["$templateCache",
    function($templateCache) {
        $templateCache.put("template/tabs/tabset.html", '\n<div>\n  <ul class="nav nav-{{type || \'tabs\'}}" ng-class="{\'nav-stacked\': vertical, \'nav-justified\': justified}" ng-transclude></ul>\n  <div class="tab-content">\n    <div class="tab-pane" \n         ng-repeat="tab in tabs" \n         ng-class="{active: tab.active}"\n         tab-content-transclude="tab">\n    </div>\n  </div>\n</div>\n')
    }
]), angular.module("template/timepicker/timepicker.html", []).run(["$templateCache",
    function($templateCache) {
        $templateCache.put("template/timepicker/timepicker.html", '<table>\n	<tbody>\n		<tr class="text-center">\n			<td><a ng-click="incrementHours()" class="btn btn-link"><span class="glyphicon glyphicon-chevron-up"></span></a></td>\n			<td>&nbsp;</td>\n			<td><a ng-click="incrementMinutes()" class="btn btn-link"><span class="glyphicon glyphicon-chevron-up"></span></a></td>\n			<td ng-show="showMeridian"></td>\n		</tr>\n		<tr>\n			<td style="width:50px;" class="form-group" ng-class="{\'has-error\': invalidHours}">\n				<input type="text" ng-model="hours" ng-change="updateHours()" class="form-control text-center" ng-mousewheel="incrementHours()" ng-readonly="readonlyInput" maxlength="2">\n			</td>\n			<td>:</td>\n			<td style="width:50px;" class="form-group" ng-class="{\'has-error\': invalidMinutes}">\n				<input type="text" ng-model="minutes" ng-change="updateMinutes()" class="form-control text-center" ng-readonly="readonlyInput" maxlength="2">\n			</td>\n			<td ng-show="showMeridian"><button type="button" class="btn btn-default text-center" ng-click="toggleMeridian()">{{meridian}}</button></td>\n		</tr>\n		<tr class="text-center">\n			<td><a ng-click="decrementHours()" class="btn btn-link"><span class="glyphicon glyphicon-chevron-down"></span></a></td>\n			<td>&nbsp;</td>\n			<td><a ng-click="decrementMinutes()" class="btn btn-link"><span class="glyphicon glyphicon-chevron-down"></span></a></td>\n			<td ng-show="showMeridian"></td>\n		</tr>\n	</tbody>\n</table>\n')
    }
]), angular.module("template/typeahead/typeahead-match.html", []).run(["$templateCache",
    function($templateCache) {
        $templateCache.put("template/typeahead/typeahead-match.html", '<a tabindex="-1" bind-html-unsafe="match.label | typeaheadHighlight:query"></a>')
    }
]), angular.module("template/typeahead/typeahead-popup.html", []).run(["$templateCache",
    function($templateCache) {
        $templateCache.put("template/typeahead/typeahead-popup.html", '<ul class="dropdown-menu" ng-if="isOpen()" ng-style="{top: position.top+\'px\', left: position.left+\'px\'}" style="display: block;" role="listbox" aria-hidden="{{!isOpen()}}">\n    <li ng-repeat="match in matches track by $index" ng-class="{active: isActive($index) }" ng-mouseenter="selectActive($index)" ng-click="selectMatch($index)" role="option" id="{{match.id}}">\n        <div typeahead-match index="$index" match="match" query="query" template-url="templateUrl"></div>\n    </li>\n</ul>')
    }
]), angular.module("adf", ["adf.provider", "ui.bootstrap", "ui.sortable"]), angular.module("adf.provider", []).provider("dashboard", function() {
    var widgets = {},
        structures = {},
        messageTemplate = '<div class="alert alert-danger">{}</div>',
        loadingTemplate = '      <div class="progress progress-striped active">\n        <div class="progress-bar" role="progressbar" style="width: 100%">\n          <span class="sr-only">loading ...</span>\n        </div>\n      </div>';
    this.widget = function(name, widget) {
        var w = angular.extend({
            reload: !1
        }, widget);
        if (w.edit) {
            var edit = {
                reload: !0
            };
            angular.extend(edit, w.edit), w.edit = edit
        }
        return widgets[name] = w, this
    }, this.structure = function(name, structure) {
        return structures[name] = structure, this
    }, this.messageTemplate = function(template) {
        return messageTemplate = template, this
    }, this.loadingTemplate = function(template) {
        return loadingTemplate = template, this
    }, this.$get = function() {
        return {
            widgets: widgets,
            structures: structures,
            messageTemplate: messageTemplate,
            loadingTemplate: loadingTemplate
        }
    }
}), angular.module("ui.sortable", []).value("uiSortableConfig", {}).directive("uiSortable", ["uiSortableConfig", "$timeout", "$log",
    function(uiSortableConfig, $timeout, $log) {
        return {
            require: "?ngModel",
            link: function(scope, element, attrs, ngModel) {
                function combineCallbacks(first, second) {
                    return second && "function" == typeof second ? function(e, ui) {
                        first(e, ui), second(e, ui)
                    } : first
                }
                var savedNodes, opts = {},
                    callbacks = {
                        receive: null,
                        remove: null,
                        start: null,
                        stop: null,
                        update: null
                    };
                angular.extend(opts, uiSortableConfig), ngModel ? (scope.$watch(attrs.ngModel + ".length", function() {
                    $timeout(function() {
                        element.sortable("refresh")
                    })
                }), callbacks.start = function(e, ui) {
                    ui.item.sortable = {
                        index: ui.item.index()
                    }
                }, callbacks.activate = function() {
                    savedNodes = element.contents();
                    var placeholder = element.sortable("option", "placeholder");
                    placeholder && placeholder.element && (savedNodes = savedNodes.not(element.find("." + placeholder.element().attr("class").split(/\s+/).join("."))))
                }, callbacks.update = function(e, ui) {
                    ui.item.sortable.received || (ui.item.sortable.dropindex = ui.item.index(), element.sortable("cancel")), savedNodes.detach().appendTo(element), ui.item.sortable.received && scope.$apply(function() {
                        ngModel.$modelValue.splice(ui.item.sortable.dropindex, 0, ui.item.sortable.moved)
                    })
                }, callbacks.stop = function(e, ui) {
                    !ui.item.sortable.received && "dropindex" in ui.item.sortable && scope.$apply(function() {
                        ngModel.$modelValue.splice(ui.item.sortable.dropindex, 0, ngModel.$modelValue.splice(ui.item.sortable.index, 1)[0])
                    })
                }, callbacks.receive = function(e, ui) {
                    ui.item.sortable.received = !0
                }, callbacks.remove = function(e, ui) {
                    scope.$apply(function() {
                        ui.item.sortable.moved = ngModel.$modelValue.splice(ui.item.sortable.index, 1)[0]
                    })
                }, scope.$watch(attrs.uiSortable, function(newVal) {
                    angular.forEach(newVal, function(value, key) {
                        callbacks[key] && (value = combineCallbacks(callbacks[key], value)), element.sortable("option", key, value)
                    })
                }, !0), angular.forEach(callbacks, function(value, key) {
                    opts[key] = combineCallbacks(value, opts[key])
                })) : $log.info("ui.sortable: ngModel not provided!", element), element.sortable(opts)
            }
        }
    }
]), angular.module("adf").directive("adfWidgetContent", ["$log", "$q", "$sce", "$http", "$templateCache", "$compile", "$controller", "$injector", "dashboard",
    function($log, $q, $sce, $http, $templateCache, $compile, $controller, $injector, dashboard) {
        function getTemplate(widget) {
            var deferred = $q.defer();
            if (widget.template) deferred.resolve(widget.template);
            else if (widget.templateUrl) {
                var url = $sce.getTrustedResourceUrl(widget.templateUrl);
                $http.get(url, {
                    cache: $templateCache
                }).success(function(response) {
                    deferred.resolve(response)
                }).error(function() {
                    deferred.reject("could not load template")
                })
            }
            return deferred.promise
        }

        function compileWidget($scope, $element) {
            var model = $scope.model,
                content = $scope.content;
            $element.html(dashboard.loadingTemplate);
            var templateScope = $scope.$new();
            model.config || (model.config = {}), templateScope.config = model.config;
            var base = {
                    $scope: templateScope,
                    widget: model,
                    config: model.config
                },
                resolvers = {};
            resolvers.$tpl = getTemplate(content), content.resolve && angular.forEach(content.resolve, function(promise, key) {
                resolvers[key] = angular.isString(promise) ? $injector.get(promise) : $injector.invoke(promise, promise, base)
            }), $q.all(resolvers).then(function(locals) {
                angular.extend(locals, base);
                var template = locals.$tpl;
                if ($element.html(template), content.controller) {
                    var templateCtrl = $controller(content.controller, locals);
                    $element.children().data("$ngControllerController", templateCtrl)
                }
                $compile($element.contents())(templateScope)
            }, function(reason) {
                var msg = "Could not resolve all promises";
                reason && (msg += ": " + reason), $log.warn(msg), $element.html(dashboard.messageTemplate.replace(/{}/g, msg))
            })
        }
        return {
            replace: !0,
            restrict: "EA",
            transclude: !1,
            scope: {
                model: "=",
                content: "="
            },
            link: function($scope, $element) {
                compileWidget($scope, $element), $scope.$on("widgetConfigChanged", function() {
                    compileWidget($scope, $element)
                }), $scope.$on("widgetReload", function() {
                    compileWidget($scope, $element)
                })
            }
        }
    }
]), angular.module("adf").directive("adfWidget", ["$log", "$modal", "dashboard",
    function($log, $modal, dashboard) {
        function preLink($scope, $element, $attr) {
            var definition = $scope.definition;
            if (definition) {
                var w = dashboard.widgets[definition.type];
                if (w) {
                    definition.title || (definition.title = w.title), $scope.editMode = $attr.editMode, $scope.widget = angular.copy(w);
                    var config = definition.config;
                    config ? angular.isString(config) && (config = angular.fromJson(config)) : config = {}, $scope.config = config, $scope.isCollapsed = !1
                } else $log.warn("could not find widget " + type)
            } else $log.debug("definition not specified, widget was probably removed")
        }

        function postLink($scope, $element) {
            var definition = $scope.definition;
            definition ? ($scope.close = function() {
                var column = $scope.col;
                if (column) {
                    var index = column.widgets.indexOf(definition);
                    index >= 0 && column.widgets.splice(index, 1)
                }
                $element.remove()
            }, $scope.reload = function() {
                $scope.$broadcast("widgetReload")
            }, $scope.edit = function() {
                var editScope = $scope.$new(),
                    opts = {
                        scope: editScope,
                        templateUrl: "../src/templates/widget-edit.html"
                    },
                    instance = $modal.open(opts);
                editScope.closeDialog = function() {
                    instance.close(), editScope.$destroy();
                    var widget = $scope.widget;
                    widget.edit && widget.edit.reload && $scope.$broadcast("widgetConfigChanged")
                }
            }) : $log.debug("widget not found")
        }
        return {
            replace: !0,
            restrict: "EA",
            transclude: !1,
            templateUrl: "../src/templates/widget.html",
            scope: {
                definition: "=",
                col: "=column",
                editMode: "@",
                collapsible: "="
            },
            compile: function() {
                return {
                    pre: preLink,
                    post: postLink
                }
            }
        }
    }
]), angular.module("adf").directive("adfDashboard", ["$rootScope", "$log", "$modal", "dashboard",
    function($rootScope, $log, $modal, dashboard) {
        function copyWidgets(source, target) {
            if (source.widgets && source.widgets.length > 0)
                for (var w = source.widgets.shift(); w;) target.widgets.push(w), w = source.widgets.shift()
        }

        function fillStructure(model, columns, counter) {
            return angular.forEach(model.rows, function(row) {
                angular.forEach(row.columns, function(column) {
                    column.widgets || (column.widgets = []), columns[counter] && (copyWidgets(columns[counter], column), counter++)
                })
            }), counter
        }

        function readColumns(model) {
            var columns = [];
            return angular.forEach(model.rows, function(row) {
                angular.forEach(row.columns, function(col) {
                    columns.push(col)
                })
            }), columns
        }

        function changeStructure(model, structure) {
            var columns = readColumns(model);
            model.rows = structure.rows;
            for (var counter = 0; counter < columns.length;) counter = fillStructure(model, columns, counter)
        }

        function createConfiguration(type) {
            var cfg = {},
                config = dashboard.widgets[type].config;
            return config && (cfg = angular.copy(config)), cfg
        }
        return {
            replace: !0,
            restrict: "EA",
            transclude: !1,
            scope: {
                structure: "@",
                name: "@",
                collapsible: "@",
                adfModel: "="
            },
            controller: ["$scope",
                function($scope) {
                    $scope.sortableOptions = {
                        connectWith: ".column",
                        handle: ".fa-arrows",
                        cursor: "move",
                        tolerance: "pointer",
                        placeholder: "placeholder",
                        forcePlaceholderSize: !0,
                        opacity: .4
                    };
                    var name = $scope.name,
                        model = $scope.adfModel;
                    if (!model || !model.rows) {
                        var structureName = $scope.structure,
                            structure = dashboard.structures[structureName];
                        structure ? (model ? model.rows = angular.copy(structure).rows : model = angular.copy(structure), model.structure = structureName) : $log.error("could not find structure " + structureName)
                    }
                    model ? (model.title || (model.title = "Dashboard"), $scope.model = model) : $log.error("could not find or create model"), $scope.editMode = !1, $scope.editClass = "", $scope.toggleEditMode = function() {
                        $scope.editMode = !$scope.editMode, $scope.editClass = "" === $scope.editClass ? "edit" : "", $scope.editMode || $rootScope.$broadcast("adfDashboardChanged", name, model)
                    }, $scope.editDashboardDialog = function() {
                        var editDashboardScope = $scope.$new();
                        editDashboardScope.structures = dashboard.structures;
                        var instance = $modal.open({
                            scope: editDashboardScope,
                            templateUrl: "../src/templates/dashboard-edit.html"
                        });
                        $scope.changeStructure = function(name, structure) {
                            $log.info("change structure to " + name), changeStructure(model, structure)
                        }, editDashboardScope.closeDialog = function() {
                            instance.close(), editDashboardScope.$destroy()
                        }
                    }, $scope.addWidgetDialog = function() {
                        var addScope = $scope.$new();
                        addScope.widgets = dashboard.widgets;
                        var opts = {
                                scope: addScope,
                                templateUrl: "../src/templates/widget-add.html"
                            },
                            instance = $modal.open(opts);
                        addScope.addWidget = function(widget) {
                            var w = {
                                type: widget,
                                config: createConfiguration(widget)
                            };
                            addScope.model.rows[0].columns[0].widgets.unshift(w), instance.close(), addScope.$destroy()
                        }, addScope.closeDialog = function() {
                            instance.close(), addScope.$destroy()
                        }
                    }
                }
            ],
            link: function($scope, $element, $attr) {
                $scope.name = $attr.name, $scope.structure = $attr.structure
            },
            templateUrl: "../src/templates/dashboard.html"
        }
    }
]),
    function() {
        "use strict";
        var angularLocalStorage = angular.module("LocalStorageModule", []);
        angularLocalStorage.provider("localStorageService", function() {
            this.prefix = "ls", this.storageType = "localStorage", this.cookie = {
                expiry: 30,
                path: "/"
            }, this.notify = {
                setItem: !0,
                removeItem: !1
            }, this.setPrefix = function(prefix) {
                this.prefix = prefix
            }, this.setStorageType = function(storageType) {
                this.storageType = storageType
            }, this.setStorageCookie = function(exp, path) {
                this.cookie = {
                    expiry: exp,
                    path: path
                }
            }, this.setStorageCookieDomain = function(domain) {
                this.cookie.domain = domain
            }, this.setNotify = function(itemSet, itemRemove) {
                this.notify = {
                    setItem: itemSet,
                    removeItem: itemRemove
                }
            }, this.$get = ["$rootScope", "$window", "$document",
                function($rootScope, $window, $document) {
                    var prefix = this.prefix,
                        cookie = this.cookie,
                        notify = this.notify,
                        storageType = this.storageType,
                        webStorage = $window[storageType];
                    $document || ($document = document), "." !== prefix.substr(-1) && (prefix = prefix ? prefix + "." : "");
                    var browserSupportsLocalStorage = function() {
                            try {
                                var supported = storageType in $window && null !== $window[storageType],
                                    key = prefix + "__" + Math.round(1e7 * Math.random());
                                return supported && (webStorage.setItem(key, ""), webStorage.removeItem(key)), !0
                            } catch (e) {
                                return storageType = "cookie", $rootScope.$broadcast("LocalStorageModule.notification.error", e.message), !1
                            }
                        }(),
                        addToLocalStorage = function(key, value) {
                            if (!browserSupportsLocalStorage) return $rootScope.$broadcast("LocalStorageModule.notification.warning", "LOCAL_STORAGE_NOT_SUPPORTED"), notify.setItem && $rootScope.$broadcast("LocalStorageModule.notification.setitem", {
                                key: key,
                                newvalue: value,
                                storageType: "cookie"
                            }), addToCookies(key, value);
                            "undefined" == typeof value && (value = null);
                            try {
                                (angular.isObject(value) || angular.isArray(value)) && (value = angular.toJson(value)), webStorage.setItem(prefix + key, value), notify.setItem && $rootScope.$broadcast("LocalStorageModule.notification.setitem", {
                                    key: key,
                                    newvalue: value,
                                    storageType: this.storageType
                                })
                            } catch (e) {
                                return $rootScope.$broadcast("LocalStorageModule.notification.error", e.message), addToCookies(key, value)
                            }
                            return !0
                        },
                        getFromLocalStorage = function(key) {
                            if (!browserSupportsLocalStorage) return $rootScope.$broadcast("LocalStorageModule.notification.warning", "LOCAL_STORAGE_NOT_SUPPORTED"), getFromCookies(key);
                            var item = webStorage.getItem(prefix + key);
                            return item && "null" !== item ? "{" === item.charAt(0) || "[" === item.charAt(0) ? angular.fromJson(item) : item : null
                        },
                        removeFromLocalStorage = function(key) {
                            if (!browserSupportsLocalStorage) return $rootScope.$broadcast("LocalStorageModule.notification.warning", "LOCAL_STORAGE_NOT_SUPPORTED"), notify.removeItem && $rootScope.$broadcast("LocalStorageModule.notification.removeitem", {
                                key: key,
                                storageType: "cookie"
                            }), removeFromCookies(key);
                            try {
                                webStorage.removeItem(prefix + key), notify.removeItem && $rootScope.$broadcast("LocalStorageModule.notification.removeitem", {
                                    key: key,
                                    storageType: this.storageType
                                })
                            } catch (e) {
                                return $rootScope.$broadcast("LocalStorageModule.notification.error", e.message), removeFromCookies(key)
                            }
                            return !0
                        },
                        getKeysForLocalStorage = function() {
                            if (!browserSupportsLocalStorage) return $rootScope.$broadcast("LocalStorageModule.notification.warning", "LOCAL_STORAGE_NOT_SUPPORTED"), !1;
                            var prefixLength = prefix.length,
                                keys = [];
                            for (var key in webStorage)
                                if (key.substr(0, prefixLength) === prefix) try {
                                    keys.push(key.substr(prefixLength))
                                } catch (e) {
                                    return $rootScope.$broadcast("LocalStorageModule.notification.error", e.Description), []
                                }
                            return keys
                        },
                        clearAllFromLocalStorage = function(regularExpression) {
                            var regularExpression = regularExpression || "",
                                tempPrefix = prefix.slice(0, -1) + ".",
                                testRegex = RegExp(tempPrefix + regularExpression);
                            if (!browserSupportsLocalStorage) return $rootScope.$broadcast("LocalStorageModule.notification.warning", "LOCAL_STORAGE_NOT_SUPPORTED"), clearAllFromCookies();
                            var prefixLength = prefix.length;
                            for (var key in webStorage)
                                if (testRegex.test(key)) try {
                                    removeFromLocalStorage(key.substr(prefixLength))
                                } catch (e) {
                                    return $rootScope.$broadcast("LocalStorageModule.notification.error", e.message), clearAllFromCookies()
                                }
                            return !0
                        },
                        browserSupportsCookies = function() {
                            try {
                                return navigator.cookieEnabled || "cookie" in $document && ($document.cookie.length > 0 || ($document.cookie = "test").indexOf.call($document.cookie, "test") > -1)
                            } catch (e) {
                                return $rootScope.$broadcast("LocalStorageModule.notification.error", e.message), !1
                            }
                        },
                        addToCookies = function(key, value) {
                            if ("undefined" == typeof value) return !1;
                            if (!browserSupportsCookies()) return $rootScope.$broadcast("LocalStorageModule.notification.error", "COOKIES_NOT_SUPPORTED"), !1;
                            try {
                                var expiry = "",
                                    expiryDate = new Date,
                                    cookieDomain = "";
                                if (null === value ? (expiryDate.setTime(expiryDate.getTime() + -864e5), expiry = "; expires=" + expiryDate.toGMTString(), value = "") : 0 !== cookie.expiry && (expiryDate.setTime(expiryDate.getTime() + 24 * cookie.expiry * 60 * 60 * 1e3), expiry = "; expires=" + expiryDate.toGMTString()), key) {
                                    var cookiePath = "; path=" + cookie.path;
                                    cookie.domain && (cookieDomain = "; domain=" + cookie.domain), $document.cookie = prefix + key + "=" + encodeURIComponent(value) + expiry + cookiePath + cookieDomain
                                }
                            } catch (e) {
                                return $rootScope.$broadcast("LocalStorageModule.notification.error", e.message), !1
                            }
                            return !0
                        },
                        getFromCookies = function(key) {
                            if (!browserSupportsCookies()) return $rootScope.$broadcast("LocalStorageModule.notification.error", "COOKIES_NOT_SUPPORTED"), !1;
                            for (var cookies = $document.cookie && $document.cookie.split(";") || [], i = 0; i < cookies.length; i++) {
                                for (var thisCookie = cookies[i];
                                     " " === thisCookie.charAt(0);) thisCookie = thisCookie.substring(1, thisCookie.length);
                                if (0 === thisCookie.indexOf(prefix + key + "=")) return decodeURIComponent(thisCookie.substring(prefix.length + key.length + 1, thisCookie.length))
                            }
                            return null
                        },
                        removeFromCookies = function(key) {
                            addToCookies(key, null)
                        },
                        clearAllFromCookies = function() {
                            for (var thisCookie = null, prefixLength = prefix.length, cookies = $document.cookie.split(";"), i = 0; i < cookies.length; i++) {
                                for (thisCookie = cookies[i];
                                     " " === thisCookie.charAt(0);) thisCookie = thisCookie.substring(1, thisCookie.length);
                                var key = thisCookie.substring(prefixLength, thisCookie.indexOf("="));
                                removeFromCookies(key)
                            }
                        },
                        getStorageType = function() {
                            return storageType
                        };
                    return {
                        isSupported: browserSupportsLocalStorage,
                        getStorageType: getStorageType,
                        set: addToLocalStorage,
                        add: addToLocalStorage,
                        get: getFromLocalStorage,
                        keys: getKeysForLocalStorage,
                        remove: removeFromLocalStorage,
                        clearAll: clearAllFromLocalStorage,
                        cookie: {
                            set: addToCookies,
                            add: addToCookies,
                            get: getFromCookies,
                            remove: removeFromCookies,
                            clearAll: clearAllFromCookies
                        }
                    }
                }
            ]
        })
    }.call(this), angular.module("sample", ["adf", "sample.widgets.mytasks", "sample.widgets.mycomplaints", "sample.widgets.teamtaskworkload", "LocalStorageModule", "structures", "sample-01", "sample-02", "ngRoute", "ngTable"]).config(["$routeProvider", "localStorageServiceProvider",
    function($routeProvider, localStorageServiceProvider) {
        localStorageServiceProvider.setPrefix("adf"), $routeProvider.when("/sample/01", {
            templateUrl: "partials/sample.html",
            controller: "sample01Ctrl",
            resolve: {
                model: function($q, $http) {
                    var q = $q.defer(),
                        url = App.Object.getContextPath() + "/api/latest/plugin/dashboard/get";
                    return $http.get(url).success(function(data) {
                        q.resolve(data.dashboardConfig);
                    }).error(q.reject), q.promise
                }
            }
        }).when("/sample/02", {
            templateUrl: "partials/sample.html",
            controller: "sample02Ctrl"
        }).otherwise({
            redirectTo: "/sample/01"
        })
    }
]).controller("navigationCtrl", ["$scope", "$location",
    function($scope, $location) {
        $scope.navCollapsed = !0, $scope.toggleNav = function() {
            $scope.navCollapsed = !$scope.navCollapsed
        }, $scope.$on("$routeChangeStart", function() {
            $scope.navCollapsed = !0
        }), $scope.navClass = function(page) {
            var currentRoute = $location.path().substring(1) || "Sample 01";
            return page === currentRoute || new RegExp(page).test(currentRoute) ? "active" : ""
        }
    }
]), angular.module("sample-01", ["adf", "LocalStorageModule"]).controller("sample01Ctrl", ["$scope", "$http", "localStorageService", "model",
    function($scope, $http, localStorageService, model) {
        $scope.name = "sample-01";
        $scope.model = angular.fromJson(model);
        $scope.collapsible = !1;

        $scope.$on("adfDashboardChanged", function(event, name, model) {
            localStorageService.set(name, model);
            var url = App.Object.getContextPath() + "/api/latest/plugin/dashboard/set",
                postObject = new Object;
            postObject.dashboardConfig = JSON.stringify(model), $http({
                method: "POST",
                url: url,
                data: JSON.stringify(postObject),
                headers: {
                    "Content-Type": "application/json"
                }
            }).success(function() {}).error(function() {})
        })
    }
]), angular.module("sample-02", ["adf", "LocalStorageModule"]).controller("sample02Ctrl", ["$scope", "localStorageService",
    function($scope, localStorageService) {
        var name = "sample-02",
            model = localStorageService.get(name);
        model || (model = {
            title: "Sample 02",
            structure: "6-6",
            rows: [{
                columns: [{
                    styleClass: "col-md-6",
                    widgets: [{
                        type: "markdown",
                        config: {
                            content: "# angular-dashboard-framework\n\n> Dashboard framework with Angular.js, Twitter Bootstrap and Font Awesome.\n\nThe api of angular-dashboard-framework (adf) is documented [here](http://sdorra.github.io/angular-dashboard-framework/docs/).\n\n## Demo\n\nA live demo of the adf can be viewed [here](http://sdorra.github.io/angular-dashboard-framework/). The demo uses html5 localStorage to store the state of the dashboard. The source of the demo can be found [here](https://github.com/sdorra/angular-dashboard-framework/tree/master/sample).\n\n## Getting started\n\nInstall bower and grunt:\n\n```bash\nnpm install -g bower\nnpm install -g grunt-cli\n```\n\nClone the repository:\n\n```bash\ngit clone https://github.com/sdorra/angular-dashboard-framework\ncd angular-dashboard-framework\n```\n\nInstall npm and bower dependencies:\n\n```bash\nnpm install --save\nbower install\n```\n\nYou can start the sample dashboard, by using the server grunt task:\n\n```bash\ngrunt server\n```\n\nNow you open the sample in your browser at http://localhost:9001/sample\n\nOr you can create a release build of angular-dashboard-framework and the samples:\n\n```bash\ngrunt\ngrunt sample\n```\nThe sample and the final build of angular-dashboard-framework are now in the dist directory."
                        },
                        title: "Markdown"
                    }]
                }, {
                    styleClass: "col-md-6",
                    widgets: [{
                        type: "githubAuthor",
                        config: {
                            path: "angular/angular.js"
                        },
                        title: "Github Author"
                    }, {
                        type: "githubHistory",
                        config: {
                            path: "sdorra/angular-dashboard-framework"
                        },
                        title: "Github History"
                    }]
                }]
            }]
        }), $scope.name = name, $scope.model = model, $scope.collapsible = !1, $scope.$on("adfDashboardChanged", function(event, name, model) {
            localStorageService.set(name, model)
        })
    }
]), angular.module("structures", ["adf"]).config(["dashboardProvider",
    function(dashboardProvider) {
        dashboardProvider.structure("6-6", {
            rows: [{
                columns: [{
                    styleClass: "col-md-6"
                }, {
                    styleClass: "col-md-6"
                }]
            }]
        }).structure("4-8", {
            rows: [{
                columns: [{
                    styleClass: "col-md-4",
                    widgets: []
                }, {
                    styleClass: "col-md-8",
                    widgets: []
                }]
            }]
        }).structure("12/4-4-4", {
            rows: [{
                columns: [{
                    styleClass: "col-md-12"
                }]
            }, {
                columns: [{
                    styleClass: "col-md-4"
                }, {
                    styleClass: "col-md-4"
                }, {
                    styleClass: "col-md-4"
                }]
            }]
        }).structure("12/6-6", {
            rows: [{
                columns: [{
                    styleClass: "col-md-12"
                }]
            }, {
                columns: [{
                    styleClass: "col-md-6"
                }, {
                    styleClass: "col-md-6"
                }]
            }]
        }).structure("12/6-6/12", {
            rows: [{
                columns: [{
                    styleClass: "col-md-12"
                }]
            }, {
                columns: [{
                    styleClass: "col-md-6"
                }, {
                    styleClass: "col-md-6"
                }]
            }, {
                columns: [{
                    styleClass: "col-md-12"
                }]
            }]
        })
    }
]), angular.module("sample.widgets.news", ["adf.provider"]).value("newsServiceUrl", "https://ajax.googleapis.com/ajax/services/feed/load?v=1.0&callback=JSON_CALLBACK&q=").config(["dashboardProvider",
    function(dashboardProvider) {
        dashboardProvider.widget("news", {
            title: "News",
            description: "Displays a RSS/Atom feed",
            templateUrl: "scripts/widgets/news/news.html",
            controller: "newsCtrl",
            resolve: {
                feed: function(newsService, config) {
                    return config.url ? newsService.get(config.url) : void 0
                }
            },
            edit: {
                templateUrl: "scripts/widgets/news/edit.html"
            }
        })
    }
]).service("newsService", ["$q", "$http", "newsServiceUrl",
    function($q, $http, newsServiceUrl) {
        return {
            get: function(url) {
                var deferred = $q.defer();
                return $http.jsonp(newsServiceUrl + encodeURIComponent(url)).success(function(data) {
                    data && data.responseData && data.responseData.feed ? deferred.resolve(data.responseData.feed) : deferred.reject()
                }).error(function() {
                    deferred.reject()
                }), deferred.promise
            }
        }
    }
]).controller("newsCtrl", ["$scope", "feed",
    function($scope, feed) {
        $scope.feed = feed
    }
]), angular.module("sample.widgets.mytasks", ["adf.provider"]).config(["dashboardProvider",
    function(dashboardProvider) {
        dashboardProvider.widget("mytasks", {
            title: "My Tasks",
            description: "Displays a user tasks",
            templateUrl: "scripts/widgets/mytasks/mytasks.html",
            controller: "myTasksCtrl",
            edit: {
                templateUrl: "scripts/widgets/mytasks/edit.html"
            }
        })
    }
]).controller("myTasksCtrl", ["$scope", "$filter", "$http", "ngTableParams",
    function($scope, $filter, $http, ngTableParams) {
        var url = App.Object.getContextPath() + "/api/latest/plugin/task/forUser/" + App.Object.getUserName();
        $http.get(url).success(function(data) {
            $scope.tableParams = new ngTableParams({
                page: 1,
                count: 5,
                sorting: {
                    due: "asc"
                }
            }, {
                total: data.length,
                getData: function($defer, params) {
                    var filteredData = params.filter() ? $filter("filter")(data, params.filter()) : data,
                        orderedData = params.sorting() ? $filter("orderBy")(filteredData, params.orderBy()) : data;
                    params.total(orderedData.length), $defer.resolve(orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count()))
                }
            })
        })
    }
]), angular.module("sample.widgets.mycomplaints", ["adf.provider"]).config(["dashboardProvider",
    function(dashboardProvider) {
        dashboardProvider.widget("mycomplaints", {
            title: "My Complaints",
            description: "Displays complaints created by user",
            templateUrl: "scripts/widgets/mycomplaints/mycomplaints.html",
            controller: "myComplaintsCtrl",
            edit: {
                templateUrl: "scripts/widgets/mycomplaints/edit.html"
            }
        })
    }
]).controller("myComplaintsCtrl", ["$scope", "$filter", "$http", "ngTableParams",
    function($scope, $filter, $http, ngTableParams) {
        var url = App.Object.getContextPath() + "/api/latest/plugin/complaints/forUser/" + App.Object.getUserName();
        $http.get(url).success(function(data) {
            $scope.tableParams = new ngTableParams({
                page: 1,
                count: 5,
                sorting: {
                    created: "asc"
                }
            }, {
                total: data.length,
                getData: function($defer, params) {
                    var filteredData = params.filter() ? $filter("filter")(data, params.filter()) : data,
                        orderedData = params.sorting() ? $filter("orderBy")(filteredData, params.orderBy()) : data;
                    params.total(orderedData.length), $defer.resolve(orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count()))
                }
            })
        })
    }
]), angular.module("sample.widgets.weather", ["adf.provider"]).value("weatherServiceUrl", "http://api.openweathermap.org/data/2.5/weather?units=metric&callback=JSON_CALLBACK&q=").config(["dashboardProvider",
    function(dashboardProvider) {
        dashboardProvider.widget("weather", {
            title: "Weather",
            description: "Display the current temperature of a city",
            templateUrl: "scripts/widgets/weather/weather.html",
            controller: "weatherCtrl",
            reload: !0,
            resolve: {
                data: function(weatcherService, config) {
                    return config.location ? weatcherService.get(config.location) : void 0
                }
            },
            edit: {
                templateUrl: "scripts/widgets/weather/edit.html"
            }
        })
    }
]).service("weatcherService", ["$q", "$http", "weatherServiceUrl",
    function($q, $http, weatherServiceUrl) {
        return {
            get: function(location) {
                var deferred = $q.defer(),
                    url = weatherServiceUrl + location;
                return $http.jsonp(url).success(function(data) {
                    data && 200 === data.cod ? deferred.resolve(data) : deferred.reject()
                }).error(function() {
                    deferred.reject()
                }), deferred.promise
            }
        }
    }
]).controller("weatherCtrl", ["$scope", "data",
    function($scope, data) {
        $scope.data = data
    }
]), angular.module("sample.widgets.linklist", ["adf.provider"]).config(["dashboardProvider",
    function(dashboardProvider) {
        dashboardProvider.widget("linklist", {
            title: "Links",
            description: "Displays a list of links",
            controller: "linklistCtrl",
            templateUrl: "scripts/widgets/linklist/linklist.html",
            edit: {
                templateUrl: "scripts/widgets/linklist/edit.html",
                reload: !1,
                controller: "linklistEditCtrl"
            }
        })
    }
]).controller("linklistCtrl", ["$scope", "config",
    function($scope, config) {
        config.links || (config.links = []), $scope.links = config.links
    }
]).controller("linklistEditCtrl", ["$scope",
    function($scope) {
        function getLinks() {
            return $scope.config.links || ($scope.config.links = []), $scope.config.links
        }
        $scope.addLink = function() {
            getLinks().push({})
        }, $scope.removeLink = function(index) {
            getLinks().splice(index, 1)
        }
    }
]);
var Showdown = {
        extensions: {}
    },
    forEach = Showdown.forEach = function(obj, callback) {
        if ("function" == typeof obj.forEach) obj.forEach(callback);
        else {
            var i, len = obj.length;
            for (i = 0; len > i; i++) callback(obj[i], i, obj)
        }
    },
    stdExtName = function(s) {
        return s.replace(/[_-]||\s/g, "").toLowerCase()
    };
Showdown.converter = function(converter_options) {
    var g_urls, g_titles, g_html_blocks, g_list_level = 0,
        g_lang_extensions = [],
        g_output_modifiers = [];
    if ("undefind" != typeof module && "undefined" != typeof exports && "undefind" != typeof require) {
        var fs = require("fs");
        if (fs) {
            var extensions = fs.readdirSync((__dirname || ".") + "/extensions").filter(function(file) {
                return ~file.indexOf(".js")
            }).map(function(file) {
                return file.replace(/\.js$/, "")
            });
            Showdown.forEach(extensions, function(ext) {
                var name = stdExtName(ext);
                Showdown.extensions[name] = require("./extensions/" + ext)
            })
        }
    }
    if (this.makeHtml = function(text) {
        return g_urls = {}, g_titles = {}, g_html_blocks = [], text = text.replace(/~/g, "~T"), text = text.replace(/\$/g, "~D"), text = text.replace(/\r\n/g, "\n"), text = text.replace(/\r/g, "\n"), text = "\n\n" + text + "\n\n", text = _Detab(text), text = text.replace(/^[ \t]+$/gm, ""), Showdown.forEach(g_lang_extensions, function(x) {
            text = _ExecuteExtension(x, text)
        }), text = _DoGithubCodeBlocks(text), text = _HashHTMLBlocks(text), text = _StripLinkDefinitions(text), text = _RunBlockGamut(text), text = _UnescapeSpecialChars(text), text = text.replace(/~D/g, "$$"), text = text.replace(/~T/g, "~"), Showdown.forEach(g_output_modifiers, function(x) {
            text = _ExecuteExtension(x, text)
        }), text
    }, converter_options && converter_options.extensions) {
        var self = this;
        Showdown.forEach(converter_options.extensions, function(plugin) {
            if ("string" == typeof plugin && (plugin = Showdown.extensions[stdExtName(plugin)]), "function" != typeof plugin) throw "Extension '" + plugin + "' could not be loaded.  It was either not found or is not a valid extension.";
            Showdown.forEach(plugin(self), function(ext) {
                ext.type ? "language" === ext.type || "lang" === ext.type ? g_lang_extensions.push(ext) : ("output" === ext.type || "html" === ext.type) && g_output_modifiers.push(ext) : g_output_modifiers.push(ext)
            })
        })
    }
    var _ProcessListItems, _ExecuteExtension = function(ext, text) {
            if (ext.regex) {
                var re = new RegExp(ext.regex, "g");
                return text.replace(re, ext.replace)
            }
            return ext.filter ? ext.filter(text) : void 0
        },
        _StripLinkDefinitions = function(text) {
            return text += "~0", text = text.replace(/^[ ]{0,3}\[(.+)\]:[ \t]*\n?[ \t]*<?(\S+?)>?[ \t]*\n?[ \t]*(?:(\n*)["(](.+?)[")][ \t]*)?(?:\n+|(?=~0))/gm, function(wholeMatch, m1, m2, m3, m4) {
                return m1 = m1.toLowerCase(), g_urls[m1] = _EncodeAmpsAndAngles(m2), m3 ? m3 + m4 : (m4 && (g_titles[m1] = m4.replace(/"/g, "&quot;")), "")
            }), text = text.replace(/~0/, "")
        },
        _HashHTMLBlocks = function(text) {
            text = text.replace(/\n/g, "\n\n");
            return text = text.replace(/^(<(p|div|h[1-6]|blockquote|pre|table|dl|ol|ul|script|noscript|form|fieldset|iframe|math|ins|del)\b[^\r]*?\n<\/\2>[ \t]*(?=\n+))/gm, hashElement), text = text.replace(/^(<(p|div|h[1-6]|blockquote|pre|table|dl|ol|ul|script|noscript|form|fieldset|iframe|math|style|section|header|footer|nav|article|aside)\b[^\r]*?<\/\2>[ \t]*(?=\n+)\n)/gm, hashElement), text = text.replace(/(\n[ ]{0,3}(<(hr)\b([^<>])*?\/?>)[ \t]*(?=\n{2,}))/g, hashElement), text = text.replace(/(\n\n[ ]{0,3}<!(--[^\r]*?--\s*)+>[ \t]*(?=\n{2,}))/g, hashElement), text = text.replace(/(?:\n\n)([ ]{0,3}(?:<([?%])[^\r]*?\2>)[ \t]*(?=\n{2,}))/g, hashElement), text = text.replace(/\n\n/g, "\n")
        },
        hashElement = function(wholeMatch, m1) {
            var blockText = m1;
            return blockText = blockText.replace(/\n\n/g, "\n"), blockText = blockText.replace(/^\n/, ""), blockText = blockText.replace(/\n+$/g, ""), blockText = "\n\n~K" + (g_html_blocks.push(blockText) - 1) + "K\n\n"
        },
        _RunBlockGamut = function(text) {
            text = _DoHeaders(text);
            var key = hashBlock("<hr />");
            return text = text.replace(/^[ ]{0,2}([ ]?\*[ ]?){3,}[ \t]*$/gm, key), text = text.replace(/^[ ]{0,2}([ ]?\-[ ]?){3,}[ \t]*$/gm, key), text = text.replace(/^[ ]{0,2}([ ]?\_[ ]?){3,}[ \t]*$/gm, key), text = _DoLists(text), text = _DoCodeBlocks(text), text = _DoBlockQuotes(text), text = _HashHTMLBlocks(text), text = _FormParagraphs(text)
        },
        _RunSpanGamut = function(text) {
            return text = _DoCodeSpans(text), text = _EscapeSpecialCharsWithinTagAttributes(text), text = _EncodeBackslashEscapes(text), text = _DoImages(text), text = _DoAnchors(text), text = _DoAutoLinks(text), text = _EncodeAmpsAndAngles(text), text = _DoItalicsAndBold(text), text = text.replace(/  +\n/g, " <br />\n")
        },
        _EscapeSpecialCharsWithinTagAttributes = function(text) {
            var regex = /(<[a-z\/!$]("[^"]*"|'[^']*'|[^'">])*>|<!(--.*?--\s*)+>)/gi;
            return text = text.replace(regex, function(wholeMatch) {
                var tag = wholeMatch.replace(/(.)<\/?code>(?=.)/g, "$1`");
                return tag = escapeCharacters(tag, "\\`*_")
            })
        },
        _DoAnchors = function(text) {
            return text = text.replace(/(\[((?:\[[^\]]*\]|[^\[\]])*)\][ ]?(?:\n[ ]*)?\[(.*?)\])()()()()/g, writeAnchorTag), text = text.replace(/(\[((?:\[[^\]]*\]|[^\[\]])*)\]\([ \t]*()<?(.*?(?:\(.*?\).*?)?)>?[ \t]*((['"])(.*?)\6[ \t]*)?\))/g, writeAnchorTag), text = text.replace(/(\[([^\[\]]+)\])()()()()()/g, writeAnchorTag)
        },
        writeAnchorTag = function(wholeMatch, m1, m2, m3, m4, m5, m6, m7) {
            void 0 == m7 && (m7 = "");
            var whole_match = m1,
                link_text = m2,
                link_id = m3.toLowerCase(),
                url = m4,
                title = m7;
            if ("" == url)
                if ("" == link_id && (link_id = link_text.toLowerCase().replace(/ ?\n/g, " ")), url = "#" + link_id, void 0 != g_urls[link_id]) url = g_urls[link_id], void 0 != g_titles[link_id] && (title = g_titles[link_id]);
                else {
                    if (!(whole_match.search(/\(\s*\)$/m) > -1)) return whole_match;
                    url = ""
                }
            url = escapeCharacters(url, "*_");
            var result = '<a href="' + url + '"';
            return "" != title && (title = title.replace(/"/g, "&quot;"), title = escapeCharacters(title, "*_"), result += ' title="' + title + '"'), result += ">" + link_text + "</a>"
        },
        _DoImages = function(text) {
            return text = text.replace(/(!\[(.*?)\][ ]?(?:\n[ ]*)?\[(.*?)\])()()()()/g, writeImageTag), text = text.replace(/(!\[(.*?)\]\s?\([ \t]*()<?(\S+?)>?[ \t]*((['"])(.*?)\6[ \t]*)?\))/g, writeImageTag)
        },
        writeImageTag = function(wholeMatch, m1, m2, m3, m4, m5, m6, m7) {
            var whole_match = m1,
                alt_text = m2,
                link_id = m3.toLowerCase(),
                url = m4,
                title = m7;
            if (title || (title = ""), "" == url) {
                if ("" == link_id && (link_id = alt_text.toLowerCase().replace(/ ?\n/g, " ")), url = "#" + link_id, void 0 == g_urls[link_id]) return whole_match;
                url = g_urls[link_id], void 0 != g_titles[link_id] && (title = g_titles[link_id])
            }
            alt_text = alt_text.replace(/"/g, "&quot;"), url = escapeCharacters(url, "*_");
            var result = '<img src="' + url + '" alt="' + alt_text + '"';
            return title = title.replace(/"/g, "&quot;"), title = escapeCharacters(title, "*_"), result += ' title="' + title + '"', result += " />"
        },
        _DoHeaders = function(text) {
            function headerId(m) {
                return m.replace(/[^\w]/g, "").toLowerCase()
            }
            return text = text.replace(/^(.+)[ \t]*\n=+[ \t]*\n+/gm, function(wholeMatch, m1) {
                return hashBlock('<h1 id="' + headerId(m1) + '">' + _RunSpanGamut(m1) + "</h1>")
            }), text = text.replace(/^(.+)[ \t]*\n-+[ \t]*\n+/gm, function(matchFound, m1) {
                return hashBlock('<h2 id="' + headerId(m1) + '">' + _RunSpanGamut(m1) + "</h2>")
            }), text = text.replace(/^(\#{1,6})[ \t]*(.+?)[ \t]*\#*\n+/gm, function(wholeMatch, m1, m2) {
                var h_level = m1.length;
                return hashBlock("<h" + h_level + ' id="' + headerId(m2) + '">' + _RunSpanGamut(m2) + "</h" + h_level + ">")
            })
        },
        _DoLists = function(text) {
            text += "~0";
            var whole_list = /^(([ ]{0,3}([*+-]|\d+[.])[ \t]+)[^\r]+?(~0|\n{2,}(?=\S)(?![ \t]*(?:[*+-]|\d+[.])[ \t]+)))/gm;
            return g_list_level ? text = text.replace(whole_list, function(wholeMatch, m1, m2) {
                var list = m1,
                    list_type = m2.search(/[*+-]/g) > -1 ? "ul" : "ol";
                list = list.replace(/\n{2,}/g, "\n\n\n");
                var result = _ProcessListItems(list);
                return result = result.replace(/\s+$/, ""), result = "<" + list_type + ">" + result + "</" + list_type + ">\n"
            }) : (whole_list = /(\n\n|^\n?)(([ ]{0,3}([*+-]|\d+[.])[ \t]+)[^\r]+?(~0|\n{2,}(?=\S)(?![ \t]*(?:[*+-]|\d+[.])[ \t]+)))/g, text = text.replace(whole_list, function(wholeMatch, m1, m2, m3) {
                var runup = m1,
                    list = m2,
                    list_type = m3.search(/[*+-]/g) > -1 ? "ul" : "ol",
                    list = list.replace(/\n{2,}/g, "\n\n\n"),
                    result = _ProcessListItems(list);
                return result = runup + "<" + list_type + ">\n" + result + "</" + list_type + ">\n"
            })), text = text.replace(/~0/, "")
        };
    _ProcessListItems = function(list_str) {
        return g_list_level++, list_str = list_str.replace(/\n{2,}$/, "\n"), list_str += "~0", list_str = list_str.replace(/(\n)?(^[ \t]*)([*+-]|\d+[.])[ \t]+([^\r]+?(\n{1,2}))(?=\n*(~0|\2([*+-]|\d+[.])[ \t]+))/gm, function(wholeMatch, m1, m2, m3, m4) {
            var item = m4,
                leading_line = m1;
            return leading_line || item.search(/\n{2,}/) > -1 ? item = _RunBlockGamut(_Outdent(item)) : (item = _DoLists(_Outdent(item)), item = item.replace(/\n$/, ""), item = _RunSpanGamut(item)), "<li>" + item + "</li>\n"
        }), list_str = list_str.replace(/~0/g, ""), g_list_level--, list_str
    };
    var _DoCodeBlocks = function(text) {
            return text += "~0", text = text.replace(/(?:\n\n|^)((?:(?:[ ]{4}|\t).*\n+)+)(\n*[ ]{0,3}[^ \t\n]|(?=~0))/g, function(wholeMatch, m1, m2) {
                var codeblock = m1,
                    nextChar = m2;
                return codeblock = _EncodeCode(_Outdent(codeblock)), codeblock = _Detab(codeblock), codeblock = codeblock.replace(/^\n+/g, ""), codeblock = codeblock.replace(/\n+$/g, ""), codeblock = "<pre><code>" + codeblock + "\n</code></pre>", hashBlock(codeblock) + nextChar
            }), text = text.replace(/~0/, "")
        },
        _DoGithubCodeBlocks = function(text) {
            return text += "~0", text = text.replace(/(?:^|\n)```(.*)\n([\s\S]*?)\n```/g, function(wholeMatch, m1, m2) {
                var language = m1,
                    codeblock = m2;
                return codeblock = _EncodeCode(codeblock), codeblock = _Detab(codeblock), codeblock = codeblock.replace(/^\n+/g, ""), codeblock = codeblock.replace(/\n+$/g, ""), codeblock = "<pre><code" + (language ? ' class="' + language + '"' : "") + ">" + codeblock + "\n</code></pre>", hashBlock(codeblock)
            }), text = text.replace(/~0/, "")
        },
        hashBlock = function(text) {
            return text = text.replace(/(^\n+|\n+$)/g, ""), "\n\n~K" + (g_html_blocks.push(text) - 1) + "K\n\n"
        },
        _DoCodeSpans = function(text) {
            return text = text.replace(/(^|[^\\])(`+)([^\r]*?[^`])\2(?!`)/gm, function(wholeMatch, m1, m2, m3) {
                var c = m3;
                return c = c.replace(/^([ \t]*)/g, ""), c = c.replace(/[ \t]*$/g, ""), c = _EncodeCode(c), m1 + "<code>" + c + "</code>"
            })
        },
        _EncodeCode = function(text) {
            return text = text.replace(/&/g, "&amp;"), text = text.replace(/</g, "&lt;"), text = text.replace(/>/g, "&gt;"), text = escapeCharacters(text, "*_{}[]\\", !1)
        },
        _DoItalicsAndBold = function(text) {
            return text = text.replace(/(\*\*|__)(?=\S)([^\r]*?\S[*_]*)\1/g, "<strong>$2</strong>"), text = text.replace(/(\*|_)(?=\S)([^\r]*?\S)\1/g, "<em>$2</em>")
        },
        _DoBlockQuotes = function(text) {
            return text = text.replace(/((^[ \t]*>[ \t]?.+\n(.+\n)*\n*)+)/gm, function(wholeMatch, m1) {
                var bq = m1;
                return bq = bq.replace(/^[ \t]*>[ \t]?/gm, "~0"), bq = bq.replace(/~0/g, ""), bq = bq.replace(/^[ \t]+$/gm, ""), bq = _RunBlockGamut(bq), bq = bq.replace(/(^|\n)/g, "$1  "), bq = bq.replace(/(\s*<pre>[^\r]+?<\/pre>)/gm, function(wholeMatch, m1) {
                    var pre = m1;
                    return pre = pre.replace(/^  /gm, "~0"), pre = pre.replace(/~0/g, "")
                }), hashBlock("<blockquote>\n" + bq + "\n</blockquote>")
            })
        },
        _FormParagraphs = function(text) {
            text = text.replace(/^\n+/g, ""), text = text.replace(/\n+$/g, "");
            for (var grafs = text.split(/\n{2,}/g), grafsOut = [], end = grafs.length, i = 0; end > i; i++) {
                var str = grafs[i];
                str.search(/~K(\d+)K/g) >= 0 ? grafsOut.push(str) : str.search(/\S/) >= 0 && (str = _RunSpanGamut(str), str = str.replace(/^([ \t]*)/g, "<p>"), str += "</p>", grafsOut.push(str))
            }
            end = grafsOut.length;
            for (var i = 0; end > i; i++)
                for (; grafsOut[i].search(/~K(\d+)K/) >= 0;) {
                    var blockText = g_html_blocks[RegExp.$1];
                    blockText = blockText.replace(/\$/g, "$$$$"), grafsOut[i] = grafsOut[i].replace(/~K\d+K/, blockText)
                }
            return grafsOut.join("\n\n")
        },
        _EncodeAmpsAndAngles = function(text) {
            return text = text.replace(/&(?!#?[xX]?(?:[0-9a-fA-F]+|\w+);)/g, "&amp;"), text = text.replace(/<(?![a-z\/?\$!])/gi, "&lt;")
        },
        _EncodeBackslashEscapes = function(text) {
            return text = text.replace(/\\(\\)/g, escapeCharacters_callback), text = text.replace(/\\([`*_{}\[\]()>#+-.!])/g, escapeCharacters_callback)
        },
        _DoAutoLinks = function(text) {
            return text = text.replace(/<((https?|ftp|dict):[^'">\s]+)>/gi, '<a href="$1">$1</a>'), text = text.replace(/<(?:mailto:)?([-.\w]+\@[-a-z0-9]+(\.[-a-z0-9]+)*\.[a-z]+)>/gi, function(wholeMatch, m1) {
                return _EncodeEmailAddress(_UnescapeSpecialChars(m1))
            })
        },
        _EncodeEmailAddress = function(addr) {
            var encode = [
                function(ch) {
                    return "&#" + ch.charCodeAt(0) + ";"
                },
                function(ch) {
                    return "&#x" + ch.charCodeAt(0).toString(16) + ";"
                },
                function(ch) {
                    return ch
                }
            ];
            return addr = "mailto:" + addr, addr = addr.replace(/./g, function(ch) {
                if ("@" == ch) ch = encode[Math.floor(2 * Math.random())](ch);
                else if (":" != ch) {
                    var r = Math.random();
                    ch = r > .9 ? encode[2](ch) : r > .45 ? encode[1](ch) : encode[0](ch)
                }
                return ch
            }), addr = '<a href="' + addr + '">' + addr + "</a>", addr = addr.replace(/">.+:/g, '">')
        },
        _UnescapeSpecialChars = function(text) {
            return text = text.replace(/~E(\d+)E/g, function(wholeMatch, m1) {
                var charCodeToReplace = parseInt(m1);
                return String.fromCharCode(charCodeToReplace)
            })
        },
        _Outdent = function(text) {
            return text = text.replace(/^(\t|[ ]{1,4})/gm, "~0"), text = text.replace(/~0/g, "")
        },
        _Detab = function(text) {
            return text = text.replace(/\t(?=\t)/g, "    "), text = text.replace(/\t/g, "~A~B"), text = text.replace(/~B(.+?)~A/g, function(wholeMatch, m1) {
                for (var leadingText = m1, numSpaces = 4 - leadingText.length % 4, i = 0; numSpaces > i; i++) leadingText += " ";
                return leadingText
            }), text = text.replace(/~A/g, "    "), text = text.replace(/~B/g, "")
        },
        escapeCharacters = function(text, charsToEscape, afterBackslash) {
            var regexString = "([" + charsToEscape.replace(/([\[\]\\])/g, "\\$1") + "])";
            afterBackslash && (regexString = "\\\\" + regexString);
            var regex = new RegExp(regexString, "g");
            return text = text.replace(regex, escapeCharacters_callback)
        },
        escapeCharacters_callback = function(wholeMatch, m1) {
            var charCodeToEscape = m1.charCodeAt(0);
            return "~E" + charCodeToEscape + "E"
        }
}, "undefined" != typeof module && (module.exports = Showdown), "function" == typeof define && define.amd && define("showdown", function() {
    return Showdown
}), angular.module("btford.markdown", []).directive("btfMarkdown", function() {
    var converter = new Showdown.converter;
    return {
        restrict: "AE",
        link: function(scope, element, attrs) {
            if (attrs.btfMarkdown) scope.$watch(attrs.btfMarkdown, function(newVal) {
                var html = converter.makeHtml(newVal);
                element.html(html)
            });
            else {
                var html = converter.makeHtml(element.text());
                element.html(html)
            }
        }
    }
}), angular.module("sample.widgets.markdown", ["adf.provider", "btford.markdown"]).config(["dashboardProvider",
    function(dashboardProvider) {
        dashboardProvider.widget("markdown", {
            title: "Markdown",
            description: "Markdown widget",
            controller: "markdownCtrl",
            templateUrl: "scripts/widgets/markdown/markdown.html",
            edit: {
                templateUrl: "scripts/widgets/markdown/edit.html",
                reload: !1
            }
        })
    }
]).controller("markdownCtrl", ["$scope", "config",
    function($scope, config) {
        config.content || (config.content = ""), $scope.config = config
    }
]), angular.module("sample.widgets.randommsg", ["adf.provider"]).config(["dashboardProvider",
    function(dashboardProvider) {
        dashboardProvider.widget("randommsg", {
            title: "Random Message",
            description: "Display a random quote of Douglas Adams",
            templateUrl: "scripts/widgets/randommsg/randommsg.html",
            controller: "randommsgCtrl"
        })
    }
]).service("randommsgService", function() {
    var msgs = ["There is a theory which states that if ever anyone discovers exactly what the Universe is for and why it is here, it will instantly disappear and be replaced by something even more bizarre and inexplicable. There is another theory which states that this has already happened.", "Many were increasingly of the opinion that they’d all made a big mistake in coming down from the trees in the first place. And some said that even the trees had been a bad move, and that no one should ever have left the oceans.", "“My doctor says that I have a malformed public-duty gland and a natural deficiency in moral fibre,” Ford muttered to himself, “and that I am therefore excused from saving Universes.”", "The ships hung in the sky in much the same way that bricks don’t.", "“You know,” said Arthur, “it’s at times like this, when I’m trapped in a Vogon airlock with a man from Betelgeuse, and about to die of asphyxiation in deep space that I really wish I’d listened to what my mother told me when I was young.”", "“Why, what did she tell you?”", "“I don’t know, I didn’t listen.”", "“Space,” it says, “is big. Really big. You just won’t believe how vastly, hugely, mindbogglingly big it is. I mean, you may think it’s a long way down the road to the chemist’s, but that’s just peanuts to space.”", "“Funny,” he intoned funereally, “how just when you think life can’t possibly get any worse it suddenly does.”", "Isn’t it enough to see that a garden is beautiful without having to believe that there are fairies at the bottom of it too?", "A common mistake that people make when trying to design something completely foolproof is to underestimate the ingenuity of complete fools.", "Curiously enough, the only thing that went through the mind of the bowl of petunias as it fell was Oh no, not again. Many people have speculated that if we knew exactly why the bowl of petunias had thought that we would know a lot more about the nature of the Universe than we do now.", "The reason why it was published in the form of a micro sub meson electronic component is that if it were printed in normal book form, an interstellar hitchhiker would require several inconveniently large buildings to carry it around in.", "For instance, on the planet Earth, man had always assumed that he was more intelligent than dolphins because he had achieved so much — the wheel, New York, wars and so on — whilst all the dolphins had ever done was muck about in the water having a good time. But conversely, the dolphins had always believed that they were far more intelligent than man — for precisely the same reasons.", "The last ever dolphin message was misinterpreted as a surprisingly sophisticated attempt to do a double-backwards-somersault through a hoop whilst whistling the ‘Star Spangled Banner’, but in fact the message was this: So long and thanks for all the fish.", "The chances of finding out what’s really going on in the universe are so remote, the only thing to do is hang the sense of it and keep yourself occupied.", "“Listen, three eyes,” he said, “don’t you try to outweird me, I get stranger things than you free with my breakfast cereal.”", "“Forty-two,” said Deep Thought, with infinite majesty and calm.", "Not unnaturally, many elevators imbued with intelligence and precognition became terribly frustrated with the mindless business of going up and down, up and down, experimented briefly with the notion of going sideways, as a sort of existential protest, demanded participation in the decision-making process and finally took to squatting in basements sulking.", "The Total Perspective Vortex derives its picture of the whole Universe on the principle of extrapolated matter analyses.To explain — since every piece of matter in the Universe is in some way affected by every other piece of matter in the Universe, it is in theory possible to extrapolate the whole of creation — every sun, every planet, their orbits, their composition and their economic and social history from, say, one small piece of fairy cake. The man who invented the Total Perspective Vortex did so basically in order to annoy his wife.", "“Shee, you guys are so unhip it’s a wonder your bums don’t fall off.”", "It is known that there are an infinite number of worlds, simply because there is an infinite amount of space for them to be in. However, not every one of them is inhabited. Therefore, there must be a finite number of inhabited worlds. Any finite number divided by infinity is as near to nothing as makes no odds, so the average population of all the planets in the Universe can be said to be zero. From this it follows that the population of the whole Universe is also zero, and that any people you may meet from time to time are merely the products of a deranged imagination.", "The disadvantages involved in pulling lots of black sticky slime from out of the ground where it had been safely hidden out of harm’s way, turning it into tar to cover the land with, smoke to fill the air with and pouring the rest into the sea, all seemed to outweigh the advantages of being able to get more quickly from one place to another.", "Make it totally clear that this gun has a right end and a wrong end. Make it totally clear to anyone standing at the wrong end that things are going badly for them. If that means sticking all sort of spikes and prongs and blackened bits all over it then so be it. This is not a gun for hanging over the fireplace or sticking in the umbrella stand, it is a gun for going out and making people miserable with.", "It is a well known fact that those people who most want to rule people are, ipso facto, those least suited to do it. To summarize the summary: anyone who is capable of getting themselves made President should on no account be allowed to do the job.", "“Since we decided a few weeks ago to adopt the leaf as legal tender, we have, of course, all become immensely rich.”", "In the end, it was the Sunday afternoons he couldn’t cope with, and that terrible listlessness that starts to set in about 2:55, when you know you’ve taken all the baths that you can usefully take that day, that however hard you stare at any given paragraph in the newspaper you will never actually read it, or use the revolutionary new pruning technique it describes, and that as you stare at the clock the hands will move relentlessly on to four o’clock, and you will enter the long dark teatime of the soul.", "He gazed keenly into the distance and looked as if he would quite like the wind to blow his hair back dramatically at that point, but the wind was busy fooling around with some leaves a little way off.", "“He was staring at the instruments with the air of one who is trying to convert Fahrenheit to centigrade in his head while his house is burning down.”", "There is a moment in every dawn when light floats, there is the possibility of magic. Creation holds its breath.", "“You may not instantly see why I bring the subject up, but that is because my mind works so phenomenally fast, and I am at a rough estimate thirty billion times more intelligent than you. Let me give you an example. Think of a number, any number.”\n       “Er, five,” said the mattress.\n       “Wrong,” said Marvin. “You see?”", "There is an art, it says, or rather, a knack to flying. The knack lies in learning how to throw yourself at the ground and miss.", "It is a mistake to think you can solve any major problems just with potatoes.", "He hoped and prayed that there wasn’t an afterlife. Then he realized there was a contradiction involved here and merely hoped that there wasn’t an afterlife.", "Eskimos had over two hundred different words for snow, without which their conversation would probably have got very monotonous. So they would distinguish between thin snow and thick snow, light snow and heavy snow, sludgy snow, brittle snow, snow that came in flurries, snow that came in drifts, snow that came in on the bottom of your neighbor’s boots all over your nice clean igloo floor, the snows of winter, the snows of spring, the snows you remember from your childhood that were so much better than any of your modern snow, fine snow, feathery snow, hill snow, valley snow, snow that falls in the morning, snow that falls at night, snow that falls all of a sudden just when you were going out fishing, and snow that despite all your efforts to train them, the huskies have pissed on.", "The storm had now definitely abated, and what thunder there was now grumbled over more distant hills, like a man saying “And another thing…” twenty minutes after admitting he’s lost the argument.", "He was wrong to think he could now forget that the big, hard, oily, dirty, rainbow-hung Earth on which he lived was a microscopic dot on a microscopic dot lost in the unimaginable infinity of the Universe.", "“It seemed to me,” said Wonko the Sane, “that any civilization that had so far lost its head as to need to include a set of detailed instructions for use in a packet of toothpicks, was no longer a civilization in which I could live and stay sane.”", "“Nothing travels faster than the speed of light with the possible exception of bad news, which obeys its own special laws.”", "The last time anybody made a list of the top hundred character attributes of New Yorkers, common sense snuck in at number 79.", "Protect me from knowing what I don’t need to know. Protect me from even knowing that there are things to know that I don’t know. Protect me from knowing that I decided not to know about the things that I decided not to know about. Amen.", "All you really need to know for the moment is that the universe is a lot more complicated than you might think, even if you start from a position of thinking it’s pretty damn complicated in the first place.", "In the beginning the Universe was created. This has made a lot of people very angry and been widely regarded as a bad move.", "Don’t Panic."];
    return {
        get: function() {
            return {
                text: msgs[Math.floor(Math.random() * msgs.length)],
                author: "Douglas Adams"
            }
        }
    }
}).controller("randommsgCtrl", ["$scope", "randommsgService",
    function($scope, randommsgService) {
        $scope.msg = randommsgService.get()
    }
]),
    function() {
        function r(a, b) {
            var c;
            a || (a = {});
            for (c in b) a[c] = b[c];
            return a
        }

        function w() {
            var a, c, b = arguments,
                d = {},
                e = function(a, b) {
                    var c, d;
                    "object" != typeof a && (a = {});
                    for (d in b) b.hasOwnProperty(d) && (c = b[d], a[d] = c && "object" == typeof c && "[object Array]" !== Object.prototype.toString.call(c) && "renderTo" !== d && "number" != typeof c.nodeType ? e(a[d] || {}, c) : b[d]);
                    return a
                };
            for (b[0] === !0 && (d = b[1], b = Array.prototype.slice.call(b, 2)), c = b.length, a = 0; c > a; a++) d = e(d, b[a]);
            return d
        }

        function z(a, b) {
            return parseInt(a, b || 10)
        }

        function Fa(a) {
            return "string" == typeof a
        }

        function da(a) {
            return a && "object" == typeof a
        }

        function La(a) {
            return "[object Array]" === Object.prototype.toString.call(a)
        }

        function ia(a) {
            return "number" == typeof a
        }

        function za(a) {
            return V.log(a) / V.LN10
        }

        function ja(a) {
            return V.pow(10, a)
        }

        function ka(a, b) {
            for (var c = a.length; c--;)
                if (a[c] === b) {
                    a.splice(c, 1);
                    break
                }
        }

        function s(a) {
            return a !== t && null !== a
        }

        function F(a, b, c) {
            var d, e;
            if (Fa(b)) s(c) ? a.setAttribute(b, c) : a && a.getAttribute && (e = a.getAttribute(b));
            else if (s(b) && da(b))
                for (d in b) a.setAttribute(d, b[d]);
            return e
        }

        function ra(a) {
            return La(a) ? a : [a]
        }

        function p() {
            var b, c, a = arguments,
                d = a.length;
            for (b = 0; d > b; b++)
                if (c = a[b], c !== t && null !== c) return c
        }

        function A(a, b) {
            Aa && !ba && b && b.opacity !== t && (b.filter = "alpha(opacity=" + 100 * b.opacity + ")"), r(a.style, b)
        }

        function $(a, b, c, d, e) {
            return a = x.createElement(a), b && r(a, b), e && A(a, {
                padding: 0,
                border: P,
                margin: 0
            }), c && A(a, c), d && d.appendChild(a), a
        }

        function la(a, b) {
            var c = function() {
                return t
            };
            return c.prototype = new a, r(c.prototype, b), c
        }

        function Ga(a, b, c, d) {
            var e = L.lang,
                a = +a || 0,
                f = -1 === b ? (a.toString().split(".")[1] || "").length : isNaN(b = Q(b)) ? 2 : b,
                b = void 0 === c ? e.decimalPoint : c,
                d = void 0 === d ? e.thousandsSep : d,
                e = 0 > a ? "-" : "",
                c = String(z(a = Q(a).toFixed(f))),
                g = c.length > 3 ? c.length % 3 : 0;
            return e + (g ? c.substr(0, g) + d : "") + c.substr(g).replace(/(\d{3})(?=\d)/g, "$1" + d) + (f ? b + Q(a - c).toFixed(f).slice(2) : "")
        }

        function Ha(a, b) {
            return Array((b || 2) + 1 - String(a).length).join(0) + a
        }

        function Ma(a, b, c) {
            var d = a[b];
            a[b] = function() {
                var a = Array.prototype.slice.call(arguments);
                return a.unshift(d), c.apply(this, a)
            }
        }

        function Ia(a, b) {
            for (var e, f, g, h, i, c = "{", d = !1, j = []; - 1 !== (c = a.indexOf(c));) {
                if (e = a.slice(0, c), d) {
                    for (f = e.split(":"), g = f.shift().split("."), i = g.length, e = b, h = 0; i > h; h++) e = e[g[h]];
                    f.length && (f = f.join(":"), g = /\.([0-9])/, h = L.lang, i = void 0, /f$/.test(f) ? (i = (i = f.match(g)) ? i[1] : -1, null !== e && (e = Ga(e, i, h.decimalPoint, f.indexOf(",") > -1 ? h.thousandsSep : ""))) : e = bb(f, e))
                }
                j.push(e), a = a.slice(c + 1), c = (d = !d) ? "}" : "{"
            }
            return j.push(a), j.join("")
        }

        function lb(a) {
            return V.pow(10, U(V.log(a) / V.LN10))
        }

        function mb(a, b, c, d) {
            var e, c = p(c, 1);
            for (e = a / c, b || (b = [1, 2, 2.5, 5, 10], d && d.allowDecimals === !1 && (1 === c ? b = [1, 2, 5, 10] : .1 >= c && (b = [1 / c]))), d = 0; d < b.length && (a = b[d], !(e <= (b[d] + (b[d + 1] || b[d])) / 2)); d++);
            return a *= c
        }

        function nb(a, b) {
            var d, e, c = a.length;
            for (e = 0; c > e; e++) a[e].ss_i = e;
            for (a.sort(function(a, c) {
                return d = b(a, c), 0 === d ? a.ss_i - c.ss_i : d
            }), e = 0; c > e; e++) delete a[e].ss_i
        }

        function Na(a) {
            for (var b = a.length, c = a[0]; b--;) a[b] < c && (c = a[b]);
            return c
        }

        function Ba(a) {
            for (var b = a.length, c = a[0]; b--;) a[b] > c && (c = a[b]);
            return c
        }

        function Oa(a, b) {
            for (var c in a) a[c] && a[c] !== b && a[c].destroy && a[c].destroy(), delete a[c]
        }

        function Pa(a) {
            cb || (cb = $(Ja)), a && cb.appendChild(a), cb.innerHTML = ""
        }

        function ea(a) {
            return parseFloat(a.toPrecision(14))
        }

        function Qa(a, b) {
            va = p(a, b.animation)
        }

        function Ab() {
            var a = L.global.useUTC,
                b = a ? "getUTC" : "get",
                c = a ? "setUTC" : "set";
            Ra = 6e4 * (a && L.global.timezoneOffset || 0), db = a ? Date.UTC : function(a, b, c, g, h, i) {
                return new Date(a, b, p(c, 1), p(g, 0), p(h, 0), p(i, 0)).getTime()
            }, ob = b + "Minutes", pb = b + "Hours", qb = b + "Day", Wa = b + "Date", eb = b + "Month", fb = b + "FullYear", Bb = c + "Minutes", Cb = c + "Hours", rb = c + "Date", Db = c + "Month", Eb = c + "FullYear"
        }

        function G() {}

        function Sa(a, b, c, d) {
            this.axis = a, this.pos = b, this.type = c || "", this.isNew = !0, !c && !d && this.addLabel()
        }

        function ma() {
            this.init.apply(this, arguments)
        }

        function Xa() {
            this.init.apply(this, arguments)
        }

        function Fb(a, b, c, d, e) {
            var f = a.chart.inverted;
            this.axis = a, this.isNegative = c, this.options = b, this.x = d, this.total = null, this.points = {}, this.stack = e, this.alignOptions = {
                align: b.align || (f ? c ? "left" : "right" : "center"),
                verticalAlign: b.verticalAlign || (f ? "middle" : c ? "bottom" : "top"),
                y: p(b.y, f ? 4 : c ? 14 : -6),
                x: p(b.x, f ? c ? -6 : 6 : 0)
            }, this.textAlign = b.textAlign || (f ? c ? "right" : "left" : "center")
        }
        var t, Ya, Za, cb, L, bb, va, ub, B, oa, db, Ra, ob, pb, qb, Wa, eb, fb, Bb, Cb, rb, Db, Eb, S, x = document,
            H = window,
            V = Math,
            v = V.round,
            U = V.floor,
            Ka = V.ceil,
            u = V.max,
            C = V.min,
            Q = V.abs,
            aa = V.cos,
            fa = V.sin,
            na = V.PI,
            Ca = 2 * na / 360,
            wa = navigator.userAgent,
            Gb = H.opera,
            Aa = /msie/i.test(wa) && !Gb,
            gb = 8 === x.documentMode,
            sb = /AppleWebKit/.test(wa),
            Ta = /Firefox/.test(wa),
            Hb = /(Mobile|Android|Windows Phone)/.test(wa),
            xa = "http://www.w3.org/2000/svg",
            ba = !!x.createElementNS && !!x.createElementNS(xa, "svg").createSVGRect,
            Nb = Ta && parseInt(wa.split("Firefox/")[1], 10) < 4,
            ga = !ba && !Aa && !!x.createElement("canvas").getContext,
            Ib = {},
            tb = 0,
            sa = function() {
                return t
            },
            W = [],
            $a = 0,
            Ja = "div",
            P = "none",
            Ob = /^[0-9]+$/,
            Pb = "stroke-width",
            J = {};
        H.Highcharts ? oa(16, !0) : S = H.Highcharts = {}, bb = function(a, b, c) {
            if (!s(b) || isNaN(b)) return "Invalid date";
            var e, a = p(a, "%Y-%m-%d %H:%M:%S"),
                d = new Date(b - Ra),
                f = d[pb](),
                g = d[qb](),
                h = d[Wa](),
                i = d[eb](),
                j = d[fb](),
                k = L.lang,
                l = k.weekdays,
                d = r({
                    a: l[g].substr(0, 3),
                    A: l[g],
                    d: Ha(h),
                    e: h,
                    b: k.shortMonths[i],
                    B: k.months[i],
                    m: Ha(i + 1),
                    y: j.toString().substr(2, 2),
                    Y: j,
                    H: Ha(f),
                    I: Ha(f % 12 || 12),
                    l: f % 12 || 12,
                    M: Ha(d[ob]()),
                    p: 12 > f ? "AM" : "PM",
                    P: 12 > f ? "am" : "pm",
                    S: Ha(d.getSeconds()),
                    L: Ha(v(b % 1e3), 3)
                }, S.dateFormats);
            for (e in d)
                for (; - 1 !== a.indexOf("%" + e);) a = a.replace("%" + e, "function" == typeof d[e] ? d[e](b) : d[e]);
            return c ? a.substr(0, 1).toUpperCase() + a.substr(1) : a
        }, oa = function(a, b) {
            var c = "Highcharts error #" + a + ": www.highcharts.com/errors/" + a;
            if (b) throw c;
            H.console && console.log(c)
        }, B = {
            millisecond: 1,
            second: 1e3,
            minute: 6e4,
            hour: 36e5,
            day: 864e5,
            week: 6048e5,
            month: 26784e5,
            year: 31556952e3
        }, ub = {
            init: function(a, b, c) {
                var g, h, i, b = b || "",
                    d = a.shift,
                    e = b.indexOf("C") > -1,
                    f = e ? 7 : 3,
                    b = b.split(" "),
                    c = [].concat(c),
                    j = function(a) {
                        for (g = a.length; g--;) "M" === a[g] && a.splice(g + 1, 0, a[g + 1], a[g + 2], a[g + 1], a[g + 2])
                    };
                if (e && (j(b), j(c)), a.isArea && (h = b.splice(b.length - 6, 6), i = c.splice(c.length - 6, 6)), d <= c.length / f && b.length === c.length)
                    for (; d--;) c = [].concat(c).splice(0, f).concat(c);
                if (a.shift = 0, b.length)
                    for (a = c.length; b.length < a;) d = [].concat(b).splice(b.length - f, f), e && (d[f - 6] = d[f - 2], d[f - 5] = d[f - 1]), b = b.concat(d);
                return h && (b = b.concat(h), c = c.concat(i)), [b, c]
            },
            step: function(a, b, c, d) {
                var e = [],
                    f = a.length;
                if (1 === c) e = d;
                else if (f === b.length && 1 > c)
                    for (; f--;) d = parseFloat(a[f]), e[f] = isNaN(d) ? a[f] : c * parseFloat(b[f] - d) + d;
                else e = b;
                return e
            }
        },
            function(a) {
                H.HighchartsAdapter = H.HighchartsAdapter || a && {
                    init: function(b) {
                        var e, c = a.fx,
                            d = c.step,
                            f = a.Tween,
                            g = f && f.propHooks;
                        e = a.cssHooks.opacity, a.extend(a.easing, {
                            easeOutQuad: function(a, b, c, d, e) {
                                return -d * (b /= e) * (b - 2) + c
                            }
                        }), a.each(["cur", "_default", "width", "height", "opacity"], function(a, b) {
                            var k, e = d;
                            "cur" === b ? e = c.prototype : "_default" === b && f && (e = g[b], b = "set"), (k = e[b]) && (e[b] = function(c) {
                                var d, c = a ? c : this;
                                return "align" !== c.prop ? (d = c.elem, d.attr ? d.attr(c.prop, "cur" === b ? t : c.now) : k.apply(this, arguments)) : void 0
                            })
                        }), Ma(e, "get", function(a, b, c) {
                            return b.attr ? b.opacity || 0 : a.call(this, b, c)
                        }), e = function(a) {
                            var d, c = a.elem;
                            a.started || (d = b.init(c, c.d, c.toD), a.start = d[0], a.end = d[1], a.started = !0), c.attr("d", b.step(a.start, a.end, a.pos, c.toD))
                        }, f ? g.d = {
                            set: e
                        } : d.d = e, this.each = Array.prototype.forEach ? function(a, b) {
                            return Array.prototype.forEach.call(a, b)
                        } : function(a, b) {
                            var c, d = a.length;
                            for (c = 0; d > c; c++)
                                if (b.call(a[c], a[c], c, a) === !1) return c
                        }, a.fn.highcharts = function() {
                            var c, d, a = "Chart",
                                b = arguments;
                            return this[0] && (Fa(b[0]) && (a = b[0], b = Array.prototype.slice.call(b, 1)), c = b[0], c !== t && (c.chart = c.chart || {}, c.chart.renderTo = this[0], new S[a](c, b[1]), d = this), c === t && (d = W[F(this[0], "data-highcharts-chart")])), d
                        }
                    },
                    getScript: a.getScript,
                    inArray: a.inArray,
                    adapterRun: function(b, c) {
                        return a(b)[c]()
                    },
                    grep: a.grep,
                    map: function(a, c) {
                        for (var d = [], e = 0, f = a.length; f > e; e++) d[e] = c.call(a[e], a[e], e, a);
                        return d
                    },
                    offset: function(b) {
                        return a(b).offset()
                    },
                    addEvent: function(b, c, d) {
                        a(b).bind(c, d)
                    },
                    removeEvent: function(b, c, d) {
                        var e = x.removeEventListener ? "removeEventListener" : "detachEvent";
                        x[e] && b && !b[e] && (b[e] = function() {}), a(b).unbind(c, d)
                    },
                    fireEvent: function(b, c, d, e) {
                        var h, f = a.Event(c),
                            g = "detached" + c;
                        !Aa && d && (delete d.layerX, delete d.layerY, delete d.returnValue), r(f, d), b[c] && (b[g] = b[c], b[c] = null), a.each(["preventDefault", "stopPropagation"], function(a, b) {
                            var c = f[b];
                            f[b] = function() {
                                try {
                                    c.call(f)
                                } catch (a) {
                                    "preventDefault" === b && (h = !0)
                                }
                            }
                        }), a(b).trigger(f), b[g] && (b[c] = b[g], b[g] = null), e && !f.isDefaultPrevented() && !h && e(f)
                    },
                    washMouseEvent: function(a) {
                        var c = a.originalEvent || a;
                        return c.pageX === t && (c.pageX = a.pageX, c.pageY = a.pageY), c
                    },
                    animate: function(b, c, d) {
                        var e = a(b);
                        b.style || (b.style = {}), c.d && (b.toD = c.d, c.d = 1), e.stop(), c.opacity !== t && b.attr && (c.opacity += "px"), e.animate(c, d)
                    },
                    stop: function(b) {
                        a(b).stop()
                    }
                }
            }(H.jQuery);
        var T = H.HighchartsAdapter,
            M = T || {};
        T && T.init.call(T, ub);
        var hb = M.adapterRun,
            Qb = M.getScript,
            Da = M.inArray,
            q = M.each,
            vb = M.grep,
            Rb = M.offset,
            Ua = M.map,
            N = M.addEvent,
            X = M.removeEvent,
            K = M.fireEvent,
            Sb = M.washMouseEvent,
            ib = M.animate,
            ab = M.stop,
            M = {
                enabled: !0,
                x: 0,
                y: 15,
                style: {
                    color: "#606060",
                    cursor: "default",
                    fontSize: "11px"
                }
            };
        L = {
            colors: "#7cb5ec,#434348,#90ed7d,#f7a35c,#8085e9,#f15c80,#e4d354,#8085e8,#8d4653,#91e8e1".split(","),
            symbols: ["circle", "diamond", "square", "triangle", "triangle-down"],
            lang: {
                loading: "Loading...",
                months: "January,February,March,April,May,June,July,August,September,October,November,December".split(","),
                shortMonths: "Jan,Feb,Mar,Apr,May,Jun,Jul,Aug,Sep,Oct,Nov,Dec".split(","),
                weekdays: "Sunday,Monday,Tuesday,Wednesday,Thursday,Friday,Saturday".split(","),
                decimalPoint: ".",
                numericSymbols: "k,M,G,T,P,E".split(","),
                resetZoom: "Reset zoom",
                resetZoomTitle: "Reset zoom level 1:1",
                thousandsSep: ","
            },
            global: {
                useUTC: !0,
                canvasToolsURL: "http://code.highcharts.com/4.0.3/modules/canvas-tools.js",
                VMLRadialGradientURL: "http://code.highcharts.com/4.0.3/gfx/vml-radial-gradient.png"
            },
            chart: {
                borderColor: "#4572A7",
                borderRadius: 0,
                defaultSeriesType: "line",
                ignoreHiddenSeries: !0,
                spacing: [10, 10, 15, 10],
                backgroundColor: "#FFFFFF",
                plotBorderColor: "#C0C0C0",
                resetZoomButton: {
                    theme: {
                        zIndex: 20
                    },
                    position: {
                        align: "right",
                        x: -10,
                        y: 10
                    }
                }
            },
            title: {
                text: "Chart title",
                align: "center",
                margin: 15,
                style: {
                    color: "#333333",
                    fontSize: "18px"
                }
            },
            subtitle: {
                text: "",
                align: "center",
                style: {
                    color: "#555555"
                }
            },
            plotOptions: {
                line: {
                    allowPointSelect: !1,
                    showCheckbox: !1,
                    animation: {
                        duration: 1e3
                    },
                    events: {},
                    lineWidth: 2,
                    marker: {
                        lineWidth: 0,
                        radius: 4,
                        lineColor: "#FFFFFF",
                        states: {
                            hover: {
                                enabled: !0,
                                lineWidthPlus: 1,
                                radiusPlus: 2
                            },
                            select: {
                                fillColor: "#FFFFFF",
                                lineColor: "#000000",
                                lineWidth: 2
                            }
                        }
                    },
                    point: {
                        events: {}
                    },
                    dataLabels: w(M, {
                        align: "center",
                        enabled: !1,
                        formatter: function() {
                            return null === this.y ? "" : Ga(this.y, -1)
                        },
                        verticalAlign: "bottom",
                        y: 0
                    }),
                    cropThreshold: 300,
                    pointRange: 0,
                    states: {
                        hover: {
                            lineWidthPlus: 1,
                            marker: {},
                            halo: {
                                size: 10,
                                opacity: .25
                            }
                        },
                        select: {
                            marker: {}
                        }
                    },
                    stickyTracking: !0,
                    turboThreshold: 1e3
                }
            },
            labels: {
                style: {
                    position: "absolute",
                    color: "#3E576F"
                }
            },
            legend: {
                enabled: !0,
                align: "center",
                layout: "horizontal",
                labelFormatter: function() {
                    return this.name
                },
                borderColor: "#909090",
                borderRadius: 0,
                navigation: {
                    activeColor: "#274b6d",
                    inactiveColor: "#CCC"
                },
                shadow: !1,
                itemStyle: {
                    color: "#333333",
                    fontSize: "12px",
                    fontWeight: "bold"
                },
                itemHoverStyle: {
                    color: "#000"
                },
                itemHiddenStyle: {
                    color: "#CCC"
                },
                itemCheckboxStyle: {
                    position: "absolute",
                    width: "13px",
                    height: "13px"
                },
                symbolPadding: 5,
                verticalAlign: "bottom",
                x: 0,
                y: 0,
                title: {
                    style: {
                        fontWeight: "bold"
                    }
                }
            },
            loading: {
                labelStyle: {
                    fontWeight: "bold",
                    position: "relative",
                    top: "45%"
                },
                style: {
                    position: "absolute",
                    backgroundColor: "white",
                    opacity: .5,
                    textAlign: "center"
                }
            },
            tooltip: {
                enabled: !0,
                animation: ba,
                backgroundColor: "rgba(249, 249, 249, .85)",
                borderWidth: 1,
                borderRadius: 3,
                dateTimeLabelFormats: {
                    millisecond: "%A, %b %e, %H:%M:%S.%L",
                    second: "%A, %b %e, %H:%M:%S",
                    minute: "%A, %b %e, %H:%M",
                    hour: "%A, %b %e, %H:%M",
                    day: "%A, %b %e, %Y",
                    week: "Week from %A, %b %e, %Y",
                    month: "%B %Y",
                    year: "%Y"
                },
                headerFormat: '<span style="font-size: 10px">{point.key}</span><br/>',
                pointFormat: '<span style="color:{series.color}">●</span> {series.name}: <b>{point.y}</b><br/>',
                shadow: !0,
                snap: Hb ? 25 : 10,
                style: {
                    color: "#333333",
                    cursor: "default",
                    fontSize: "12px",
                    padding: "8px",
                    whiteSpace: "nowrap"
                }
            },
            credits: {
                enabled: !0,
                text: "Highcharts.com",
                href: "http://www.highcharts.com",
                position: {
                    align: "right",
                    x: -10,
                    verticalAlign: "bottom",
                    y: -5
                },
                style: {
                    cursor: "pointer",
                    color: "#909090",
                    fontSize: "9px"
                }
            }
        };
        var ca = L.plotOptions,
            T = ca.line;
        Ab();
        var Tb = /rgba\(\s*([0-9]{1,3})\s*,\s*([0-9]{1,3})\s*,\s*([0-9]{1,3})\s*,\s*([0-9]?(?:\.[0-9]+)?)\s*\)/,
            Ub = /#([a-fA-F0-9]{2})([a-fA-F0-9]{2})([a-fA-F0-9]{2})/,
            Vb = /rgb\(\s*([0-9]{1,3})\s*,\s*([0-9]{1,3})\s*,\s*([0-9]{1,3})\s*\)/,
            ya = function(a) {
                var c, d, b = [];
                return function(a) {
                    a && a.stops ? d = Ua(a.stops, function(a) {
                        return ya(a[1])
                    }) : (c = Tb.exec(a)) ? b = [z(c[1]), z(c[2]), z(c[3]), parseFloat(c[4], 10)] : (c = Ub.exec(a)) ? b = [z(c[1], 16), z(c[2], 16), z(c[3], 16), 1] : (c = Vb.exec(a)) && (b = [z(c[1]), z(c[2]), z(c[3]), 1])
                }(a), {
                    get: function(c) {
                        var f;
                        return d ? (f = w(a), f.stops = [].concat(f.stops), q(d, function(a, b) {
                            f.stops[b] = [f.stops[b][0], a.get(c)]
                        })) : f = b && !isNaN(b[0]) ? "rgb" === c ? "rgb(" + b[0] + "," + b[1] + "," + b[2] + ")" : "a" === c ? b[3] : "rgba(" + b.join(",") + ")" : a, f
                    },
                    brighten: function(a) {
                        if (d) q(d, function(b) {
                            b.brighten(a)
                        });
                        else if (ia(a) && 0 !== a) {
                            var c;
                            for (c = 0; 3 > c; c++) b[c] += z(255 * a), b[c] < 0 && (b[c] = 0), b[c] > 255 && (b[c] = 255)
                        }
                        return this
                    },
                    rgba: b,
                    setOpacity: function(a) {
                        return b[3] = a, this
                    }
                }
            };
        G.prototype = {
            opacity: 1,
            textProps: "fontSize,fontWeight,fontFamily,color,lineHeight,width,textDecoration,textShadow,HcTextStroke".split(","),
            init: function(a, b) {
                this.element = "span" === b ? $(b) : x.createElementNS(xa, b), this.renderer = a
            },
            animate: function(a, b, c) {
                return b = p(b, va, !0), ab(this), b ? (b = w(b, {}), c && (b.complete = c), ib(this, a, b)) : (this.attr(a), c && c()), this
            },
            colorGradient: function(a, b, c) {
                var e, f, g, h, i, j, k, l, m, n, d = this.renderer,
                    o = [];
                if (a.linearGradient ? f = "linearGradient" : a.radialGradient && (f = "radialGradient"), f) {
                    g = a[f], h = d.gradients, j = a.stops, m = c.radialReference, La(g) && (a[f] = g = {
                        x1: g[0],
                        y1: g[1],
                        x2: g[2],
                        y2: g[3],
                        gradientUnits: "userSpaceOnUse"
                    }), "radialGradient" === f && m && !s(g.gradientUnits) && (g = w(g, {
                        cx: m[0] - m[2] / 2 + g.cx * m[2],
                        cy: m[1] - m[2] / 2 + g.cy * m[2],
                        r: g.r * m[2],
                        gradientUnits: "userSpaceOnUse"
                    }));
                    for (n in g) "id" !== n && o.push(n, g[n]);
                    for (n in j) o.push(j[n]);
                    o = o.join(","), h[o] ? a = h[o].attr("id") : (g.id = a = "highcharts-" + tb++, h[o] = i = d.createElement(f).attr(g).add(d.defs), i.stops = [], q(j, function(a) {
                        0 === a[1].indexOf("rgba") ? (e = ya(a[1]), k = e.get("rgb"), l = e.get("a")) : (k = a[1], l = 1), a = d.createElement("stop").attr({
                            offset: a[0],
                            "stop-color": k,
                            "stop-opacity": l
                        }).add(i), i.stops.push(a)
                    })), c.setAttribute(b, "url(" + d.url + "#" + a + ")")
                }
            },
            attr: function(a, b) {
                var c, d, f, h, e = this.element,
                    g = this;
                if ("string" == typeof a && b !== t && (c = a, a = {}, a[c] = b), "string" == typeof a) g = (this[a + "Getter"] || this._defaultGetter).call(this, a, e);
                else {
                    for (c in a) d = a[c], h = !1, this.symbolName && /^(x|y|width|height|r|start|end|innerR|anchorX|anchorY)/.test(c) && (f || (this.symbolAttr(a), f = !0), h = !0), !this.rotation || "x" !== c && "y" !== c || (this.doTransform = !0), h || (this[c + "Setter"] || this._defaultSetter).call(this, d, c, e), this.shadows && /^(width|height|visibility|x|y|d|transform|cx|cy|r)$/.test(c) && this.updateShadows(c, d);
                    this.doTransform && (this.updateTransform(), this.doTransform = !1)
                }
                return g
            },
            updateShadows: function(a, b) {
                for (var c = this.shadows, d = c.length; d--;) c[d].setAttribute(a, "height" === a ? u(b - (c[d].cutHeight || 0), 0) : "d" === a ? this.d : b)
            },
            addClass: function(a) {
                var b = this.element,
                    c = F(b, "class") || "";
                return -1 === c.indexOf(a) && F(b, "class", c + " " + a), this
            },
            symbolAttr: function(a) {
                var b = this;
                q("x,y,r,start,end,width,height,innerR,anchorX,anchorY".split(","), function(c) {
                    b[c] = p(a[c], b[c])
                }), b.attr({
                    d: b.renderer.symbols[b.symbolName](b.x, b.y, b.width, b.height, b)
                })
            },
            clip: function(a) {
                return this.attr("clip-path", a ? "url(" + this.renderer.url + "#" + a.id + ")" : P)
            },
            crisp: function(a) {
                var b, d, c = {},
                    e = a.strokeWidth || this.strokeWidth || 0;
                d = v(e) % 2 / 2, a.x = U(a.x || this.x || 0) + d, a.y = U(a.y || this.y || 0) + d, a.width = U((a.width || this.width || 0) - 2 * d), a.height = U((a.height || this.height || 0) - 2 * d), a.strokeWidth = e;
                for (b in a) this[b] !== a[b] && (this[b] = c[b] = a[b]);
                return c
            },
            css: function(a) {
                var e, f, b = this.styles,
                    c = {},
                    d = this.element,
                    g = "";
                if (e = !b, a && a.color && (a.fill = a.color), b)
                    for (f in a) a[f] !== b[f] && (c[f] = a[f], e = !0);
                if (e) {
                    if (e = this.textWidth = a && a.width && "text" === d.nodeName.toLowerCase() && z(a.width), b && (a = r(b, c)), this.styles = a, e && (ga || !ba && this.renderer.forExport) && delete a.width, Aa && !ba) A(this.element, a);
                    else {
                        b = function(a, b) {
                            return "-" + b.toLowerCase()
                        };
                        for (f in a) g += f.replace(/([A-Z])/g, b) + ":" + a[f] + ";";
                        F(d, "style", g)
                    }
                    e && this.added && this.renderer.buildText(this)
                }
                return this
            },
            on: function(a, b) {
                var c = this,
                    d = c.element;
                return Za && "click" === a ? (d.ontouchstart = function(a) {
                    c.touchEventFired = Date.now(), a.preventDefault(), b.call(d, a)
                }, d.onclick = function(a) {
                    (-1 === wa.indexOf("Android") || Date.now() - (c.touchEventFired || 0) > 1100) && b.call(d, a)
                }) : d["on" + a] = b, this
            },
            setRadialReference: function(a) {
                return this.element.radialReference = a, this
            },
            translate: function(a, b) {
                return this.attr({
                    translateX: a,
                    translateY: b
                })
            },
            invert: function() {
                return this.inverted = !0, this.updateTransform(), this
            },
            updateTransform: function() {
                var a = this.translateX || 0,
                    b = this.translateY || 0,
                    c = this.scaleX,
                    d = this.scaleY,
                    e = this.inverted,
                    f = this.rotation,
                    g = this.element;
                e && (a += this.attr("width"), b += this.attr("height")), a = ["translate(" + a + "," + b + ")"], e ? a.push("rotate(90) scale(-1,1)") : f && a.push("rotate(" + f + " " + (g.getAttribute("x") || 0) + " " + (g.getAttribute("y") || 0) + ")"), (s(c) || s(d)) && a.push("scale(" + p(c, 1) + " " + p(d, 1) + ")"), a.length && g.setAttribute("transform", a.join(" "))
            },
            toFront: function() {
                var a = this.element;
                return a.parentNode.appendChild(a), this
            },
            align: function(a, b, c) {
                var d, e, f, g, h = {};
                return e = this.renderer, f = e.alignedObjects, a ? (this.alignOptions = a, this.alignByTranslate = b, (!c || Fa(c)) && (this.alignTo = d = c || "renderer", ka(f, this), f.push(this), c = null)) : (a = this.alignOptions, b = this.alignByTranslate, d = this.alignTo), c = p(c, e[d], e), d = a.align, e = a.verticalAlign, f = (c.x || 0) + (a.x || 0), g = (c.y || 0) + (a.y || 0), ("right" === d || "center" === d) && (f += (c.width - (a.width || 0)) / {
                    right: 1,
                    center: 2
                }[d]), h[b ? "translateX" : "x"] = v(f), ("bottom" === e || "middle" === e) && (g += (c.height - (a.height || 0)) / ({
                    bottom: 1,
                    middle: 2
                }[e] || 1)), h[b ? "translateY" : "y"] = v(g), this[this.placed ? "animate" : "attr"](h), this.placed = !0, this.alignAttr = h, this
            },
            getBBox: function() {
                var c, d, a = this.bBox,
                    b = this.renderer,
                    e = this.rotation;
                c = this.element;
                var f = this.styles,
                    g = e * Ca;
                d = this.textStr;
                var h;
                if (("" === d || Ob.test(d)) && (h = "num." + d.toString().length + (f ? "|" + f.fontSize + "|" + f.fontFamily : "")), h && (a = b.cache[h]), !a) {
                    if (c.namespaceURI === xa || b.forExport) {
                        try {
                            a = c.getBBox ? r({}, c.getBBox()) : {
                                width: c.offsetWidth,
                                height: c.offsetHeight
                            }
                        } catch (i) {}(!a || a.width < 0) && (a = {
                            width: 0,
                            height: 0
                        })
                    } else a = this.htmlGetBBox();
                    b.isSVG && (c = a.width, d = a.height, Aa && f && "11px" === f.fontSize && "16.9" === d.toPrecision(3) && (a.height = d = 14), e && (a.width = Q(d * fa(g)) + Q(c * aa(g)), a.height = Q(d * aa(g)) + Q(c * fa(g)))), this.bBox = a, h && (b.cache[h] = a)
                }
                return a
            },
            show: function(a) {
                return a && this.element.namespaceURI === xa ? (this.element.removeAttribute("visibility"), this) : this.attr({
                    visibility: a ? "inherit" : "visible"
                })
            },
            hide: function() {
                return this.attr({
                    visibility: "hidden"
                })
            },
            fadeOut: function(a) {
                var b = this;
                b.animate({
                    opacity: 0
                }, {
                    duration: a || 150,
                    complete: function() {
                        b.hide()
                    }
                })
            },
            add: function(a) {
                var g, h, b = this.renderer,
                    c = a || b,
                    d = c.element || b.box,
                    e = this.element,
                    f = this.zIndex;
                if (a && (this.parentGroup = a), this.parentInverted = a && a.inverted, void 0 !== this.textStr && b.buildText(this), f && (c.handleZ = !0, f = z(f)), c.handleZ)
                    for (a = d.childNodes, g = 0; g < a.length; g++)
                        if (b = a[g], c = F(b, "zIndex"), b !== e && (z(c) > f || !s(f) && s(c))) {
                            d.insertBefore(e, b), h = !0;
                            break
                        }
                return h || d.appendChild(e), this.added = !0, this.onAdd && this.onAdd(), this
            },
            safeRemoveChild: function(a) {
                var b = a.parentNode;
                b && b.removeChild(a)
            },
            destroy: function() {
                var e, f, a = this,
                    b = a.element || {},
                    c = a.shadows,
                    d = a.renderer.isSVG && "SPAN" === b.nodeName && a.parentGroup;
                if (b.onclick = b.onmouseout = b.onmouseover = b.onmousemove = b.point = null, ab(a), a.clipPath && (a.clipPath = a.clipPath.destroy()), a.stops) {
                    for (f = 0; f < a.stops.length; f++) a.stops[f] = a.stops[f].destroy();
                    a.stops = null
                }
                for (a.safeRemoveChild(b), c && q(c, function(b) {
                    a.safeRemoveChild(b)
                }); d && d.div && 0 === d.div.childNodes.length;) b = d.parentGroup, a.safeRemoveChild(d.div), delete d.div, d = b;
                a.alignTo && ka(a.renderer.alignedObjects, a);
                for (e in a) delete a[e];
                return null
            },
            shadow: function(a, b, c) {
                var e, f, h, i, j, k, d = [],
                    g = this.element;
                if (a) {
                    for (i = p(a.width, 3), j = (a.opacity || .15) / i, k = this.parentInverted ? "(-1,-1)" : "(" + p(a.offsetX, 1) + ", " + p(a.offsetY, 1) + ")", e = 1; i >= e; e++) f = g.cloneNode(0), h = 2 * i + 1 - 2 * e, F(f, {
                        isShadow: "true",
                        stroke: a.color || "black",
                        "stroke-opacity": j * e,
                        "stroke-width": h,
                        transform: "translate" + k,
                        fill: P
                    }), c && (F(f, "height", u(F(f, "height") - h, 0)), f.cutHeight = h), b ? b.element.appendChild(f) : g.parentNode.insertBefore(f, g), d.push(f);
                    this.shadows = d
                }
                return this
            },
            xGetter: function(a) {
                return "circle" === this.element.nodeName && (a = {
                    x: "cx",
                    y: "cy"
                }[a] || a), this._defaultGetter(a)
            },
            _defaultGetter: function(a) {
                return a = p(this[a], this.element ? this.element.getAttribute(a) : null, 0), /^[\-0-9\.]+$/.test(a) && (a = parseFloat(a)), a
            },
            dSetter: function(a, b, c) {
                a && a.join && (a = a.join(" ")), /(NaN| {2}|^$)/.test(a) && (a = "M 0 0"), c.setAttribute(b, a), this[b] = a
            },
            dashstyleSetter: function(a) {
                var b;
                if (a = a && a.toLowerCase()) {
                    for (a = a.replace("shortdashdotdot", "3,1,1,1,1,1,").replace("shortdashdot", "3,1,1,1").replace("shortdot", "1,1,").replace("shortdash", "3,1,").replace("longdash", "8,3,").replace(/dot/g, "1,3,").replace("dash", "4,3,").replace(/,$/, "").replace("solid", 1).split(","), b = a.length; b--;) a[b] = z(a[b]) * this["stroke-width"];
                    a = a.join(","), this.element.setAttribute("stroke-dasharray", a)
                }
            },
            alignSetter: function(a) {
                this.element.setAttribute("text-anchor", {
                    left: "start",
                    center: "middle",
                    right: "end"
                }[a])
            },
            opacitySetter: function(a, b, c) {
                this[b] = a, c.setAttribute(b, a)
            },
            titleSetter: function(a) {
                var b = this.element.getElementsByTagName("title")[0];
                b || (b = x.createElementNS(xa, "title"), this.element.appendChild(b)), b.textContent = a
            },
            textSetter: function(a) {
                a !== this.textStr && (delete this.bBox, this.textStr = a, this.added && this.renderer.buildText(this))
            },
            fillSetter: function(a, b, c) {
                "string" == typeof a ? c.setAttribute(b, a) : a && this.colorGradient(a, b, c)
            },
            zIndexSetter: function(a, b, c) {
                c.setAttribute(b, a), this[b] = a
            },
            _defaultSetter: function(a, b, c) {
                c.setAttribute(b, a)
            }
        }, G.prototype.yGetter = G.prototype.xGetter, G.prototype.translateXSetter = G.prototype.translateYSetter = G.prototype.rotationSetter = G.prototype.verticalAlignSetter = G.prototype.scaleXSetter = G.prototype.scaleYSetter = function(a, b) {
            this[b] = a, this.doTransform = !0
        }, G.prototype["stroke-widthSetter"] = G.prototype.strokeSetter = function(a, b, c) {
            this[b] = a, this.stroke && this["stroke-width"] ? (this.strokeWidth = this["stroke-width"], G.prototype.fillSetter.call(this, this.stroke, "stroke", c), c.setAttribute("stroke-width", this["stroke-width"]), this.hasStroke = !0) : "stroke-width" === b && 0 === a && this.hasStroke && (c.removeAttribute("stroke"), this.hasStroke = !1)
        };
        var ta = function() {
            this.init.apply(this, arguments)
        };
        ta.prototype = {
            Element: G,
            init: function(a, b, c, d, e) {
                var g, f = location,
                    d = this.createElement("svg").attr({
                        version: "1.1"
                    }).css(this.getStyle(d));
                g = d.element, a.appendChild(g), -1 === a.innerHTML.indexOf("xmlns") && F(g, "xmlns", xa), this.isSVG = !0, this.box = g, this.boxWrapper = d, this.alignedObjects = [], this.url = (Ta || sb) && x.getElementsByTagName("base").length ? f.href.replace(/#.*?$/, "").replace(/([\('\)])/g, "\\$1").replace(/ /g, "%20") : "", this.createElement("desc").add().element.appendChild(x.createTextNode("Created with Highcharts 4.0.3")), this.defs = this.createElement("defs").add(), this.forExport = e, this.gradients = {}, this.cache = {}, this.setSize(b, c, !1);
                var h;
                Ta && a.getBoundingClientRect && (this.subPixelFix = b = function() {
                    A(a, {
                        left: 0,
                        top: 0
                    }), h = a.getBoundingClientRect(), A(a, {
                        left: Ka(h.left) - h.left + "px",
                        top: Ka(h.top) - h.top + "px"
                    })
                }, b(), N(H, "resize", b))
            },
            getStyle: function(a) {
                return this.style = r({
                    fontFamily: '"Lucida Grande", "Lucida Sans Unicode", Arial, Helvetica, sans-serif',
                    fontSize: "12px"
                }, a)
            },
            isHidden: function() {
                return !this.boxWrapper.getBBox().width
            },
            destroy: function() {
                var a = this.defs;
                return this.box = null, this.boxWrapper = this.boxWrapper.destroy(), Oa(this.gradients || {}), this.gradients = null, a && (this.defs = a.destroy()), this.subPixelFix && X(H, "resize", this.subPixelFix), this.alignedObjects = null
            },
            createElement: function(a) {
                var b = new this.Element;
                return b.init(this, a), b
            },
            draw: function() {},
            buildText: function(a) {
                for (var h, i, b = a.element, c = this, d = c.forExport, e = p(a.textStr, "").toString(), f = -1 !== e.indexOf("<"), g = b.childNodes, j = F(b, "x"), k = a.styles, l = a.textWidth, m = k && k.lineHeight, n = k && k.HcTextStroke, o = g.length, Y = function(a) {
                    return m ? z(m) : c.fontMetrics(/(px|em)$/.test(a && a.style.fontSize) ? a.style.fontSize : k && k.fontSize || c.style.fontSize || 12, a).h
                }; o--;) b.removeChild(g[o]);
                f || n || -1 !== e.indexOf(" ") ? (h = /<.*style="([^"]+)".*>/, i = /<.*href="(http[^"]+)".*>/, l && !a.added && this.box.appendChild(b), e = f ? e.replace(/<(b|strong)>/g, '<span style="font-weight:bold">').replace(/<(i|em)>/g, '<span style="font-style:italic">').replace(/<a/g, "<span").replace(/<\/(b|strong|i|em|a)>/g, "</span>").split(/<br.*?>/g) : [e], "" === e[e.length - 1] && e.pop(), q(e, function(e, f) {
                    var g, m = 0,
                        e = e.replace(/<span/g, "|||<span").replace(/<\/span>/g, "</span>|||");
                    g = e.split("|||"), q(g, function(e) {
                        if ("" !== e || 1 === g.length) {
                            var p, n = {},
                                o = x.createElementNS(xa, "tspan");
                            if (h.test(e) && (p = e.match(h)[1].replace(/(;| |^)color([ :])/, "$1fill$2"), F(o, "style", p)), i.test(e) && !d && (F(o, "onclick", 'location.href="' + e.match(i)[1] + '"'), A(o, {
                                cursor: "pointer"
                            })), e = (e.replace(/<(.|\n)*?>/g, "") || " ").replace(/&lt;/g, "<").replace(/&gt;/g, ">"), " " !== e) {
                                if (o.appendChild(x.createTextNode(e)), m ? n.dx = 0 : f && null !== j && (n.x = j), F(o, n), b.appendChild(o), !m && f && (!ba && d && A(o, {
                                    display: "block"
                                }), F(o, "dy", Y(o))), l)
                                    for (var q, E, e = e.replace(/([^\^])-/g, "$1- ").split(" "), n = g.length > 1 || e.length > 1 && "nowrap" !== k.whiteSpace, s = k.HcHeight, u = [], t = Y(o), Kb = 1; n && (e.length || u.length);) delete a.bBox, q = a.getBBox(), E = q.width, !ba && c.forExport && (E = c.measureSpanWidth(o.firstChild.data, a.styles)), q = E > l, q && 1 !== e.length ? (o.removeChild(o.firstChild), u.unshift(e.pop())) : (e = u, u = [], e.length && (Kb++, s && Kb * t > s ? (e = ["..."], a.attr("title", a.textStr)) : (o = x.createElementNS(xa, "tspan"), F(o, {
                                        dy: t,
                                        x: j
                                    }), p && F(o, "style", p), b.appendChild(o))), E > l && (l = E)), e.length && o.appendChild(x.createTextNode(e.join(" ").replace(/- /g, "-")));
                                m++
                            }
                        }
                    })
                })) : b.appendChild(x.createTextNode(e))
            },
            button: function(a, b, c, d, e, f, g, h, i) {
                var l, m, n, o, p, q, j = this.label(a, b, c, i, null, null, null, null, "button"),
                    k = 0,
                    a = {
                        x1: 0,
                        y1: 0,
                        x2: 0,
                        y2: 1
                    },
                    e = w({
                        "stroke-width": 1,
                        stroke: "#CCCCCC",
                        fill: {
                            linearGradient: a,
                            stops: [
                                [0, "#FEFEFE"],
                                [1, "#F6F6F6"]
                            ]
                        },
                        r: 2,
                        padding: 5,
                        style: {
                            color: "black"
                        }
                    }, e);
                return n = e.style, delete e.style, f = w(e, {
                    stroke: "#68A",
                    fill: {
                        linearGradient: a,
                        stops: [
                            [0, "#FFF"],
                            [1, "#ACF"]
                        ]
                    }
                }, f), o = f.style, delete f.style, g = w(e, {
                    stroke: "#68A",
                    fill: {
                        linearGradient: a,
                        stops: [
                            [0, "#9BD"],
                            [1, "#CDF"]
                        ]
                    }
                }, g), p = g.style, delete g.style, h = w(e, {
                    style: {
                        color: "#CCC"
                    }
                }, h), q = h.style, delete h.style, N(j.element, Aa ? "mouseover" : "mouseenter", function() {
                    3 !== k && j.attr(f).css(o)
                }), N(j.element, Aa ? "mouseout" : "mouseleave", function() {
                    3 !== k && (l = [e, f, g][k], m = [n, o, p][k], j.attr(l).css(m))
                }), j.setState = function(a) {
                    (j.state = k = a) ? 2 === a ? j.attr(g).css(p) : 3 === a && j.attr(h).css(q): j.attr(e).css(n)
                }, j.on("click", function() {
                    3 !== k && d.call(j)
                }).attr(e).css(r({
                    cursor: "default"
                }, n))
            },
            crispLine: function(a, b) {
                return a[1] === a[4] && (a[1] = a[4] = v(a[1]) - b % 2 / 2), a[2] === a[5] && (a[2] = a[5] = v(a[2]) + b % 2 / 2), a
            },
            path: function(a) {
                var b = {
                    fill: P
                };
                return La(a) ? b.d = a : da(a) && r(b, a), this.createElement("path").attr(b)
            },
            circle: function(a, b, c) {
                return a = da(a) ? a : {
                    x: a,
                    y: b,
                    r: c
                }, b = this.createElement("circle"), b.xSetter = function(a) {
                    this.element.setAttribute("cx", a)
                }, b.ySetter = function(a) {
                    this.element.setAttribute("cy", a)
                }, b.attr(a)
            },
            arc: function(a, b, c, d, e, f) {
                return da(a) && (b = a.y, c = a.r, d = a.innerR, e = a.start, f = a.end, a = a.x), a = this.symbol("arc", a || 0, b || 0, c || 0, c || 0, {
                    innerR: d || 0,
                    start: e || 0,
                    end: f || 0
                }), a.r = c, a
            },
            rect: function(a, b, c, d, e, f) {
                var e = da(a) ? a.r : e,
                    g = this.createElement("rect"),
                    a = da(a) ? a : a === t ? {} : {
                        x: a,
                        y: b,
                        width: u(c, 0),
                        height: u(d, 0)
                    };
                return f !== t && (a.strokeWidth = f, a = g.crisp(a)), e && (a.r = e), g.rSetter = function(a) {
                    F(this.element, {
                        rx: a,
                        ry: a
                    })
                }, g.attr(a)
            },
            setSize: function(a, b, c) {
                var d = this.alignedObjects,
                    e = d.length;
                for (this.width = a, this.height = b, this.boxWrapper[p(c, !0) ? "animate" : "attr"]({
                    width: a,
                    height: b
                }); e--;) d[e].align()
            },
            g: function(a) {
                var b = this.createElement("g");
                return s(a) ? b.attr({
                    "class": "highcharts-" + a
                }) : b
            },
            image: function(a, b, c, d, e) {
                var f = {
                    preserveAspectRatio: P
                };
                return arguments.length > 1 && r(f, {
                    x: b,
                    y: c,
                    width: d,
                    height: e
                }), f = this.createElement("image").attr(f), f.element.setAttributeNS ? f.element.setAttributeNS("http://www.w3.org/1999/xlink", "href", a) : f.element.setAttribute("hc-svg-href", a), f
            },
            symbol: function(a, b, c, d, e, f) {
                var g, j, k, h = this.symbols[a],
                    h = h && h(v(b), v(c), d, e, f),
                    i = /^url\((.*?)\)$/;
                return h ? (g = this.path(h), r(g, {
                    symbolName: a,
                    x: b,
                    y: c,
                    width: d,
                    height: e
                }), f && r(g, f)) : i.test(a) && (k = function(a, b) {
                    a.element && (a.attr({
                        width: b[0],
                        height: b[1]
                    }), a.alignByTranslate || a.translate(v((d - b[0]) / 2), v((e - b[1]) / 2)))
                }, j = a.match(i)[1], a = Ib[j], g = this.image(j).attr({
                    x: b,
                    y: c
                }), g.isImg = !0, a ? k(g, a) : (g.attr({
                    width: 0,
                    height: 0
                }), $("img", {
                    onload: function() {
                        k(g, Ib[j] = [this.width, this.height])
                    },
                    src: j
                }))), g
            },
            symbols: {
                circle: function(a, b, c, d) {
                    var e = .166 * c;
                    return ["M", a + c / 2, b, "C", a + c + e, b, a + c + e, b + d, a + c / 2, b + d, "C", a - e, b + d, a - e, b, a + c / 2, b, "Z"]
                },
                square: function(a, b, c, d) {
                    return ["M", a, b, "L", a + c, b, a + c, b + d, a, b + d, "Z"]
                },
                triangle: function(a, b, c, d) {
                    return ["M", a + c / 2, b, "L", a + c, b + d, a, b + d, "Z"]
                },
                "triangle-down": function(a, b, c, d) {
                    return ["M", a, b, "L", a + c, b, a + c / 2, b + d, "Z"]
                },
                diamond: function(a, b, c, d) {
                    return ["M", a + c / 2, b, "L", a + c, b + d / 2, a + c / 2, b + d, a, b + d / 2, "Z"]
                },
                arc: function(a, b, c, d, e) {
                    var f = e.start,
                        c = e.r || c || d,
                        g = e.end - .001,
                        d = e.innerR,
                        h = e.open,
                        i = aa(f),
                        j = fa(f),
                        k = aa(g),
                        g = fa(g),
                        e = e.end - f < na ? 0 : 1;
                    return ["M", a + c * i, b + c * j, "A", c, c, 0, e, 1, a + c * k, b + c * g, h ? "M" : "L", a + d * k, b + d * g, "A", d, d, 0, e, 0, a + d * i, b + d * j, h ? "" : "Z"]
                },
                callout: function(a, b, c, d, e) {
                    var f = C(e && e.r || 0, c, d),
                        g = f + 6,
                        h = e && e.anchorX,
                        i = e && e.anchorY,
                        e = v(e.strokeWidth || 0) % 2 / 2;
                    return a += e, b += e, e = ["M", a + f, b, "L", a + c - f, b, "C", a + c, b, a + c, b, a + c, b + f, "L", a + c, b + d - f, "C", a + c, b + d, a + c, b + d, a + c - f, b + d, "L", a + f, b + d, "C", a, b + d, a, b + d, a, b + d - f, "L", a, b + f, "C", a, b, a, b, a + f, b], h && h > c && i > b + g && b + d - g > i ? e.splice(13, 3, "L", a + c, i - 6, a + c + 6, i, a + c, i + 6, a + c, b + d - f) : h && 0 > h && i > b + g && b + d - g > i ? e.splice(33, 3, "L", a, i + 6, a - 6, i, a, i - 6, a, b + f) : i && i > d && h > a + g && a + c - g > h ? e.splice(23, 3, "L", h + 6, b + d, h, b + d + 6, h - 6, b + d, a + f, b + d) : i && 0 > i && h > a + g && a + c - g > h && e.splice(3, 3, "L", h - 6, b, h, b - 6, h + 6, b, c - f, b), e
                }
            },
            clipRect: function(a, b, c, d) {
                var e = "highcharts-" + tb++,
                    f = this.createElement("clipPath").attr({
                        id: e
                    }).add(this.defs),
                    a = this.rect(a, b, c, d, 0).add(f);
                return a.id = e, a.clipPath = f, a
            },
            text: function(a, b, c, d) {
                var e = ga || !ba && this.forExport,
                    f = {};
                return d && !this.forExport ? this.html(a, b, c) : (f.x = Math.round(b || 0), c && (f.y = Math.round(c)), (a || 0 === a) && (f.text = a), a = this.createElement("text").attr(f), e && a.css({
                    position: "absolute"
                }), d || (a.xSetter = function(a, b, c) {
                    var e, m, d = c.getElementsByTagName("tspan"),
                        f = c.getAttribute(b);
                    for (m = 0; m < d.length; m++) e = d[m], e.getAttribute(b) === f && e.setAttribute(b, a);
                    c.setAttribute(b, a)
                }), a)
            },
            fontMetrics: function(a, b) {
                a = a || this.style.fontSize, b && H.getComputedStyle && (b = b.element || b, a = H.getComputedStyle(b, "").fontSize);
                var a = /px/.test(a) ? z(a) : /em/.test(a) ? 12 * parseFloat(a) : 12,
                    c = 24 > a ? a + 4 : v(1.2 * a),
                    d = v(.8 * c);
                return {
                    h: c,
                    b: d,
                    f: a
                }
            },
            label: function(a, b, c, d, e, f, g, h, i) {
                function j() {
                    var a, b;
                    a = o.element.style, E = (void 0 === u || void 0 === wb || n.styles.textAlign) && o.textStr && o.getBBox(), n.width = (u || E.width || 0) + 2 * D + jb, n.height = (wb || E.height || 0) + 2 * D, R = D + m.fontMetrics(a && a.fontSize, o).b, z && (p || (a = v(-I * D), b = h ? -R : 0, n.box = p = d ? m.symbol(d, a, b, n.width, n.height, y) : m.rect(a, b, n.width, n.height, 0, y[Pb]), p.attr("fill", P).add(n)), p.isImg || p.attr(r({
                        width: v(n.width),
                        height: v(n.height)
                    }, y)), y = null)
                }

                function k() {
                    var c, a = n.styles,
                        a = a && a.textAlign,
                        b = jb + D * (1 - I);
                    c = h ? 0 : R, s(u) && E && ("center" === a || "right" === a) && (b += {
                        center: .5,
                        right: 1
                    }[a] * (u - E.width)), (b !== o.x || c !== o.y) && (o.attr("x", b), c !== t && o.attr("y", c)), o.x = b, o.y = c
                }

                function l(a, b) {
                    p ? p.attr(a, b) : y[a] = b
                }
                var p, E, u, wb, xb, x, R, z, m = this,
                    n = m.g(i),
                    o = m.text("", 0, 0, g).attr({
                        zIndex: 1
                    }),
                    I = 0,
                    D = 3,
                    jb = 0,
                    Jb = 0,
                    y = {};
                n.onAdd = function() {
                    o.add(n), n.attr({
                        text: a || "",
                        x: b,
                        y: c
                    }), p && s(e) && n.attr({
                        anchorX: e,
                        anchorY: f
                    })
                }, n.widthSetter = function(a) {
                    u = a
                }, n.heightSetter = function(a) {
                    wb = a
                }, n.paddingSetter = function(a) {
                    s(a) && a !== D && (D = a, k())
                }, n.paddingLeftSetter = function(a) {
                    s(a) && a !== jb && (jb = a, k())
                }, n.alignSetter = function(a) {
                    I = {
                        left: 0,
                        center: .5,
                        right: 1
                    }[a]
                }, n.textSetter = function(a) {
                    a !== t && o.textSetter(a), j(), k()
                }, n["stroke-widthSetter"] = function(a, b) {
                    a && (z = !0), Jb = a % 2 / 2, l(b, a)
                }, n.strokeSetter = n.fillSetter = n.rSetter = function(a, b) {
                    "fill" === b && a && (z = !0), l(b, a)
                }, n.anchorXSetter = function(a, b) {
                    e = a, l(b, a + Jb - xb)
                }, n.anchorYSetter = function(a, b) {
                    f = a, l(b, a - x)
                }, n.xSetter = function(a) {
                    n.x = a, I && (a -= I * ((u || E.width) + D)), xb = v(a), n.attr("translateX", xb)
                }, n.ySetter = function(a) {
                    x = n.y = v(a), n.attr("translateY", x)
                };
                var C = n.css;
                return r(n, {
                    css: function(a) {
                        if (a) {
                            var b = {},
                                a = w(a);
                            q(n.textProps, function(c) {
                                a[c] !== t && (b[c] = a[c], delete a[c])
                            }), o.css(b)
                        }
                        return C.call(n, a)
                    },
                    getBBox: function() {
                        return {
                            width: E.width + 2 * D,
                            height: E.height + 2 * D,
                            x: E.x - D,
                            y: E.y - D
                        }
                    },
                    shadow: function(a) {
                        return p && p.shadow(a), n
                    },
                    destroy: function() {
                        X(n.element, "mouseenter"), X(n.element, "mouseleave"), o && (o = o.destroy()), p && (p = p.destroy()), G.prototype.destroy.call(n), n = m = j = k = l = null
                    }
                })
            }
        }, Ya = ta, r(G.prototype, {
            htmlCss: function(a) {
                var b = this.element;
                return (b = a && "SPAN" === b.tagName && a.width) && (delete a.width, this.textWidth = b, this.updateTransform()), this.styles = r(this.styles, a), A(this.element, a), this
            },
            htmlGetBBox: function() {
                var a = this.element,
                    b = this.bBox;
                return b || ("text" === a.nodeName && (a.style.position = "absolute"), b = this.bBox = {
                    x: a.offsetLeft,
                    y: a.offsetTop,
                    width: a.offsetWidth,
                    height: a.offsetHeight
                }), b
            },
            htmlUpdateTransform: function() {
                if (this.added) {
                    var a = this.renderer,
                        b = this.element,
                        c = this.translateX || 0,
                        d = this.translateY || 0,
                        e = this.x || 0,
                        f = this.y || 0,
                        g = this.textAlign || "left",
                        h = {
                            left: 0,
                            center: .5,
                            right: 1
                        }[g],
                        i = this.shadows;
                    if (A(b, {
                        marginLeft: c,
                        marginTop: d
                    }), i && q(i, function(a) {
                        A(a, {
                            marginLeft: c + 1,
                            marginTop: d + 1
                        })
                    }), this.inverted && q(b.childNodes, function(c) {
                        a.invertChild(c, b)
                    }), "SPAN" === b.tagName) {
                        var k, j = this.rotation,
                            l = z(this.textWidth),
                            m = [j, g, b.innerHTML, this.textWidth].join(",");
                        m !== this.cTT && (k = a.fontMetrics(b.style.fontSize).b, s(j) && this.setSpanRotation(j, h, k), i = p(this.elemWidth, b.offsetWidth), i > l && /[ \-]/.test(b.textContent || b.innerText) && (A(b, {
                            width: l + "px",
                            display: "block",
                            whiteSpace: "normal"
                        }), i = l), this.getSpanCorrection(i, k, h, j, g)), A(b, {
                            left: e + (this.xCorr || 0) + "px",
                            top: f + (this.yCorr || 0) + "px"
                        }), sb && (k = b.offsetHeight), this.cTT = m
                    }
                } else this.alignOnAdd = !0
            },
            setSpanRotation: function(a, b, c) {
                var d = {},
                    e = Aa ? "-ms-transform" : sb ? "-webkit-transform" : Ta ? "MozTransform" : Gb ? "-o-transform" : "";
                d[e] = d.transform = "rotate(" + a + "deg)", d[e + (Ta ? "Origin" : "-origin")] = d.transformOrigin = 100 * b + "% " + c + "px", A(this.element, d)
            },
            getSpanCorrection: function(a, b, c) {
                this.xCorr = -a * c, this.yCorr = -b
            }
        }), r(ta.prototype, {
            html: function(a, b, c) {
                var d = this.createElement("span"),
                    e = d.element,
                    f = d.renderer;
                return d.textSetter = function(a) {
                    a !== e.innerHTML && delete this.bBox, e.innerHTML = this.textStr = a
                }, d.xSetter = d.ySetter = d.alignSetter = d.rotationSetter = function(a, b) {
                    "align" === b && (b = "textAlign"), d[b] = a, d.htmlUpdateTransform()
                }, d.attr({
                    text: a,
                    x: v(b),
                    y: v(c)
                }).css({
                    position: "absolute",
                    whiteSpace: "nowrap",
                    fontFamily: this.style.fontFamily,
                    fontSize: this.style.fontSize
                }), d.css = d.htmlCss, f.isSVG && (d.add = function(a) {
                    var b, c = f.box.parentNode,
                        j = [];
                    if (this.parentGroup = a) {
                        if (b = a.div, !b) {
                            for (; a;) j.push(a), a = a.parentGroup;
                            q(j.reverse(), function(a) {
                                var d;
                                b = a.div = a.div || $(Ja, {
                                    className: F(a.element, "class")
                                }, {
                                    position: "absolute",
                                    left: (a.translateX || 0) + "px",
                                    top: (a.translateY || 0) + "px"
                                }, b || c), d = b.style, r(a, {
                                    translateXSetter: function(b, c) {
                                        d.left = b + "px", a[c] = b, a.doTransform = !0
                                    },
                                    translateYSetter: function(b, c) {
                                        d.top = b + "px", a[c] = b, a.doTransform = !0
                                    },
                                    visibilitySetter: function(a, b) {
                                        d[b] = a
                                    }
                                })
                            })
                        }
                    } else b = c;
                    return b.appendChild(e), d.added = !0, d.alignOnAdd && d.htmlUpdateTransform(), d
                }), d
            }
        });
        var Z;
        if (!ba && !ga) {
            Z = {
                init: function(a, b) {
                    var c = ["<", b, ' filled="f" stroked="f"'],
                        d = ["position: ", "absolute", ";"],
                        e = b === Ja;
                    ("shape" === b || e) && d.push("left:0;top:0;width:1px;height:1px;"), d.push("visibility: ", e ? "hidden" : "visible"), c.push(' style="', d.join(""), '"/>'), b && (c = e || "span" === b || "img" === b ? c.join("") : a.prepVML(c), this.element = $(c)), this.renderer = a
                },
                add: function(a) {
                    var b = this.renderer,
                        c = this.element,
                        d = b.box,
                        d = a ? a.element || a : d;
                    return a && a.inverted && b.invertChild(c, d), d.appendChild(c), this.added = !0, this.alignOnAdd && !this.deferUpdateTransform && this.updateTransform(), this.onAdd && this.onAdd(), this
                },
                updateTransform: G.prototype.htmlUpdateTransform,
                setSpanRotation: function() {
                    var a = this.rotation,
                        b = aa(a * Ca),
                        c = fa(a * Ca);
                    A(this.element, {
                        filter: a ? ["progid:DXImageTransform.Microsoft.Matrix(M11=", b, ", M12=", -c, ", M21=", c, ", M22=", b, ", sizingMethod='auto expand')"].join("") : P
                    })
                },
                getSpanCorrection: function(a, b, c, d, e) {
                    var i, f = d ? aa(d * Ca) : 1,
                        g = d ? fa(d * Ca) : 0,
                        h = p(this.elemHeight, this.element.offsetHeight);
                    this.xCorr = 0 > f && -a, this.yCorr = 0 > g && -h, i = 0 > f * g, this.xCorr += g * b * (i ? 1 - c : c), this.yCorr -= f * b * (d ? i ? c : 1 - c : 1), e && "left" !== e && (this.xCorr -= a * c * (0 > f ? -1 : 1), d && (this.yCorr -= h * c * (0 > g ? -1 : 1)), A(this.element, {
                        textAlign: e
                    }))
                },
                pathToVML: function(a) {
                    for (var b = a.length, c = []; b--;) ia(a[b]) ? c[b] = v(10 * a[b]) - 5 : "Z" === a[b] ? c[b] = "x" : (c[b] = a[b], !a.isArc || "wa" !== a[b] && "at" !== a[b] || (c[b + 5] === c[b + 7] && (c[b + 7] += a[b + 7] > a[b + 5] ? 1 : -1), c[b + 6] === c[b + 8] && (c[b + 8] += a[b + 8] > a[b + 6] ? 1 : -1)));
                    return c.join(" ") || "x"
                },
                clip: function(a) {
                    var c, b = this;
                    return a ? (c = a.members, ka(c, b), c.push(b), b.destroyClip = function() {
                        ka(c, b)
                    }, a = a.getCSS(b)) : (b.destroyClip && b.destroyClip(), a = {
                        clip: gb ? "inherit" : "rect(auto)"
                    }), b.css(a)
                },
                css: G.prototype.htmlCss,
                safeRemoveChild: function(a) {
                    a.parentNode && Pa(a)
                },
                destroy: function() {
                    return this.destroyClip && this.destroyClip(), G.prototype.destroy.apply(this)
                },
                on: function(a, b) {
                    return this.element["on" + a] = function() {
                        var a = H.event;
                        a.target = a.srcElement, b(a)
                    }, this
                },
                cutOffPath: function(a, b) {
                    var c, a = a.split(/[ ,]/);
                    return c = a.length, (9 === c || 11 === c) && (a[c - 4] = a[c - 2] = z(a[c - 2]) - 10 * b), a.join(" ")
                },
                shadow: function(a, b, c) {
                    var e, h, j, l, m, n, o, d = [],
                        f = this.element,
                        g = this.renderer,
                        i = f.style,
                        k = f.path;
                    if (k && "string" != typeof k.value && (k = "x"), m = k, a) {
                        for (n = p(a.width, 3), o = (a.opacity || .15) / n, e = 1; 3 >= e; e++) l = 2 * n + 1 - 2 * e, c && (m = this.cutOffPath(k.value, l + .5)), j = ['<shape isShadow="true" strokeweight="', l, '" filled="false" path="', m, '" coordsize="10 10" style="', f.style.cssText, '" />'], h = $(g.prepVML(j), null, {
                            left: z(i.left) + p(a.offsetX, 1),
                            top: z(i.top) + p(a.offsetY, 1)
                        }), c && (h.cutOff = l + 1), j = ['<stroke color="', a.color || "black", '" opacity="', o * e, '"/>'], $(g.prepVML(j), null, null, h), b ? b.element.appendChild(h) : f.parentNode.insertBefore(h, f), d.push(h);
                        this.shadows = d
                    }
                    return this
                },
                updateShadows: sa,
                setAttr: function(a, b) {
                    gb ? this.element[a] = b : this.element.setAttribute(a, b)
                },
                classSetter: function(a) {
                    this.element.className = a
                },
                dashstyleSetter: function(a, b, c) {
                    (c.getElementsByTagName("stroke")[0] || $(this.renderer.prepVML(["<stroke/>"]), null, null, c))[b] = a || "solid", this[b] = a
                },
                dSetter: function(a, b, c) {
                    var d = this.shadows,
                        a = a || [];
                    if (this.d = a.join && a.join(" "), c.path = a = this.pathToVML(a), d)
                        for (c = d.length; c--;) d[c].path = d[c].cutOff ? this.cutOffPath(a, d[c].cutOff) : a;
                    this.setAttr(b, a)
                },
                fillSetter: function(a, b, c) {
                    var d = c.nodeName;
                    "SPAN" === d ? c.style.color = a : "IMG" !== d && (c.filled = a !== P, this.setAttr("fillcolor", this.renderer.color(a, c, b, this)))
                },
                opacitySetter: sa,
                rotationSetter: function(a, b, c) {
                    c = c.style, this[b] = c[b] = a, c.left = -v(fa(a * Ca) + 1) + "px", c.top = v(aa(a * Ca)) + "px"
                },
                strokeSetter: function(a, b, c) {
                    this.setAttr("strokecolor", this.renderer.color(a, c, b))
                },
                "stroke-widthSetter": function(a, b, c) {
                    c.stroked = !!a, this[b] = a, ia(a) && (a += "px"), this.setAttr("strokeweight", a)
                },
                titleSetter: function(a, b) {
                    this.setAttr(b, a)
                },
                visibilitySetter: function(a, b, c) {
                    "inherit" === a && (a = "visible"), this.shadows && q(this.shadows, function(c) {
                        c.style[b] = a
                    }), "DIV" === c.nodeName && (a = "hidden" === a ? "-999em" : 0, gb || (c.style[b] = a ? "visible" : "hidden"), b = "top"), c.style[b] = a
                },
                xSetter: function(a, b, c) {
                    this[b] = a, "x" === b ? b = "left" : "y" === b && (b = "top"), this.updateClipping ? (this[b] = a, this.updateClipping()) : c.style[b] = a
                },
                zIndexSetter: function(a, b, c) {
                    c.style[b] = a
                }
            }, S.VMLElement = Z = la(G, Z), Z.prototype.ySetter = Z.prototype.widthSetter = Z.prototype.heightSetter = Z.prototype.xSetter;
            var ha = {
                Element: Z,
                isIE8: wa.indexOf("MSIE 8.0") > -1,
                init: function(a, b, c, d) {
                    var e;
                    if (this.alignedObjects = [], d = this.createElement(Ja).css(r(this.getStyle(d), {
                        position: "relative"
                    })), e = d.element, a.appendChild(d.element), this.isVML = !0, this.box = e, this.boxWrapper = d, this.cache = {}, this.setSize(b, c, !1), !x.namespaces.hcv) {
                        x.namespaces.add("hcv", "urn:schemas-microsoft-com:vml");
                        try {
                            x.createStyleSheet().cssText = "hcv\\:fill, hcv\\:path, hcv\\:shape, hcv\\:stroke{ behavior:url(#default#VML); display: inline-block; } "
                        } catch (f) {
                            x.styleSheets[0].cssText += "hcv\\:fill, hcv\\:path, hcv\\:shape, hcv\\:stroke{ behavior:url(#default#VML); display: inline-block; } "
                        }
                    }
                },
                isHidden: function() {
                    return !this.box.offsetWidth
                },
                clipRect: function(a, b, c, d) {
                    var e = this.createElement(),
                        f = da(a);
                    return r(e, {
                        members: [],
                        left: (f ? a.x : a) + 1,
                        top: (f ? a.y : b) + 1,
                        width: (f ? a.width : c) - 1,
                        height: (f ? a.height : d) - 1,
                        getCSS: function(a) {
                            var b = a.element,
                                c = b.nodeName,
                                a = a.inverted,
                                d = this.top - ("shape" === c ? b.offsetTop : 0),
                                e = this.left,
                                b = e + this.width,
                                f = d + this.height,
                                d = {
                                    clip: "rect(" + v(a ? e : d) + "px," + v(a ? f : b) + "px," + v(a ? b : f) + "px," + v(a ? d : e) + "px)"
                                };
                            return !a && gb && "DIV" === c && r(d, {
                                width: b + "px",
                                height: f + "px"
                            }), d
                        },
                        updateClipping: function() {
                            q(e.members, function(a) {
                                a.element && a.css(e.getCSS(a))
                            })
                        }
                    })
                },
                color: function(a, b, c, d) {
                    var f, h, i, e = this,
                        g = /^rgba/,
                        j = P;
                    if (a && a.linearGradient ? i = "gradient" : a && a.radialGradient && (i = "pattern"), i) {
                        var k, l, n, o, p, E, I, u, m = a.linearGradient || a.radialGradient,
                            D = "",
                            a = a.stops,
                            s = [],
                            t = function() {
                                h = ['<fill colors="' + s.join(",") + '" opacity="', p, '" o:opacity2="', o, '" type="', i, '" ', D, 'focus="100%" method="any" />'], $(e.prepVML(h), null, null, b)
                            };
                        if (n = a[0], u = a[a.length - 1], n[0] > 0 && a.unshift([0, n[1]]), u[0] < 1 && a.push([1, u[1]]), q(a, function(a, b) {
                            g.test(a[1]) ? (f = ya(a[1]), k = f.get("rgb"), l = f.get("a")) : (k = a[1], l = 1), s.push(100 * a[0] + "% " + k), b ? (p = l, E = k) : (o = l, I = k)
                        }), "fill" === c)
                            if ("gradient" === i) c = m.x1 || m[0] || 0, a = m.y1 || m[1] || 0, n = m.x2 || m[2] || 0, m = m.y2 || m[3] || 0, D = 'angle="' + (90 - 180 * V.atan((m - a) / (n - c)) / na) + '"', t();
                            else {
                                var w, j = m.r,
                                    r = 2 * j,
                                    v = 2 * j,
                                    x = m.cx,
                                    y = m.cy,
                                    R = b.radialReference,
                                    j = function() {
                                        R && (w = d.getBBox(), x += (R[0] - w.x) / w.width - .5, y += (R[1] - w.y) / w.height - .5, r *= R[2] / w.width, v *= R[2] / w.height), D = 'src="' + L.global.VMLRadialGradientURL + '" size="' + r + "," + v + '" origin="0.5,0.5" position="' + x + "," + y + '" color2="' + I + '" ', t()
                                    };
                                d.added ? j() : d.onAdd = j, j = E
                            } else j = k
                    } else g.test(a) && "IMG" !== b.tagName ? (f = ya(a), h = ["<", c, ' opacity="', f.get("a"), '"/>'], $(this.prepVML(h), null, null, b), j = f.get("rgb")) : (j = b.getElementsByTagName(c), j.length && (j[0].opacity = 1, j[0].type = "solid"), j = a);
                    return j
                },
                prepVML: function(a) {
                    var b = this.isIE8,
                        a = a.join("");
                    return b ? (a = a.replace("/>", ' xmlns="urn:schemas-microsoft-com:vml" />'), a = -1 === a.indexOf('style="') ? a.replace("/>", ' style="display:inline-block;behavior:url(#default#VML);" />') : a.replace('style="', 'style="display:inline-block;behavior:url(#default#VML);')) : a = a.replace("<", "<hcv:"), a
                },
                text: ta.prototype.html,
                path: function(a) {
                    var b = {
                        coordsize: "10 10"
                    };
                    return La(a) ? b.d = a : da(a) && r(b, a), this.createElement("shape").attr(b)
                },
                circle: function(a, b, c) {
                    var d = this.symbol("circle");
                    return da(a) && (c = a.r, b = a.y, a = a.x), d.isCircle = !0, d.r = c, d.attr({
                        x: a,
                        y: b
                    })
                },
                g: function(a) {
                    var b;
                    return a && (b = {
                        className: "highcharts-" + a,
                        "class": "highcharts-" + a
                    }), this.createElement(Ja).attr(b)
                },
                image: function(a, b, c, d, e) {
                    var f = this.createElement("img").attr({
                        src: a
                    });
                    return arguments.length > 1 && f.attr({
                        x: b,
                        y: c,
                        width: d,
                        height: e
                    }), f
                },
                createElement: function(a) {
                    return "rect" === a ? this.symbol(a) : ta.prototype.createElement.call(this, a)
                },
                invertChild: function(a, b) {
                    var c = this,
                        d = b.style,
                        e = "IMG" === a.tagName && a.style;
                    A(a, {
                        flip: "x",
                        left: z(d.width) - (e ? z(e.top) : 1),
                        top: z(d.height) - (e ? z(e.left) : 1),
                        rotation: -90
                    }), q(a.childNodes, function(b) {
                        c.invertChild(b, a)
                    })
                },
                symbols: {
                    arc: function(a, b, c, d, e) {
                        var f = e.start,
                            g = e.end,
                            h = e.r || c || d,
                            c = e.innerR,
                            d = aa(f),
                            i = fa(f),
                            j = aa(g),
                            k = fa(g);
                        return g - f === 0 ? ["x"] : (f = ["wa", a - h, b - h, a + h, b + h, a + h * d, b + h * i, a + h * j, b + h * k], e.open && !c && f.push("e", "M", a, b), f.push("at", a - c, b - c, a + c, b + c, a + c * j, b + c * k, a + c * d, b + c * i, "x", "e"), f.isArc = !0, f)
                    },
                    circle: function(a, b, c, d, e) {
                        return e && (c = d = 2 * e.r), e && e.isCircle && (a -= c / 2, b -= d / 2), ["wa", a, b, a + c, b + d, a + c, b + d / 2, a + c, b + d / 2, "e"]
                    },
                    rect: function(a, b, c, d, e) {
                        return ta.prototype.symbols[s(e) && e.r ? "callout" : "square"].call(0, a, b, c, d, e)
                    }
                }
            };
            S.VMLRenderer = Z = function() {
                this.init.apply(this, arguments)
            }, Z.prototype = w(ta.prototype, ha), Ya = Z
        }
        ta.prototype.measureSpanWidth = function(a, b) {
            var d, c = x.createElement("span");
            return d = x.createTextNode(a), c.appendChild(d), A(c, b), this.box.appendChild(c), d = c.offsetWidth, Pa(c), d
        };
        var Lb;
        ga && (S.CanVGRenderer = Z = function() {
            xa = "http://www.w3.org/1999/xhtml"
        }, Z.prototype.symbols = {}, Lb = function() {
            function a() {
                var d, a = b.length;
                for (d = 0; a > d; d++) b[d]();
                b = []
            }
            var b = [];
            return {
                push: function(c, d) {
                    0 === b.length && Qb(d, a), b.push(c)
                }
            }
        }(), Ya = Z), Sa.prototype = {
            addLabel: function() {
                var m, a = this.axis,
                    b = a.options,
                    c = a.chart,
                    d = a.horiz,
                    e = a.categories,
                    f = a.names,
                    g = this.pos,
                    h = b.labels,
                    i = h.rotation,
                    j = a.tickPositions,
                    d = d && e && !h.step && !h.staggerLines && !h.rotation && c.plotWidth / j.length || !d && (c.margin[3] || .33 * c.chartWidth),
                    k = g === j[0],
                    l = g === j[j.length - 1],
                    f = e ? p(e[g], f[g], g) : g,
                    e = this.label,
                    n = j.info;
                a.isDatetimeAxis && n && (m = b.dateTimeLabelFormats[n.higherRanks[g] || n.unitName]), this.isFirst = k, this.isLast = l, b = a.labelFormatter.call({
                    axis: a,
                    chart: c,
                    isFirst: k,
                    isLast: l,
                    dateTimeLabelFormat: m,
                    value: a.isLog ? ea(ja(f)) : f
                }), g = d && {
                    width: u(1, v(d - 2 * (h.padding || 10))) + "px"
                }, g = r(g, h.style), s(e) ? e && e.attr({
                    text: b
                }).css(g) : (m = {
                    align: a.labelAlign
                }, ia(i) && (m.rotation = i), d && h.ellipsis && (g.HcHeight = a.len / j.length), this.label = e = s(b) && h.enabled ? c.renderer.text(b, 0, 0, h.useHTML).attr(m).css(g).add(a.labelGroup) : null, a.tickBaseline = c.renderer.fontMetrics(h.style.fontSize, e).b, i && 2 === a.side && (a.tickBaseline *= aa(i * Ca))), this.yOffset = e ? p(h.y, a.tickBaseline + (2 === a.side ? 8 : -(e.getBBox().height / 2))) : 0
            },
            getLabelSize: function() {
                var a = this.label,
                    b = this.axis;
                return a ? a.getBBox()[b.horiz ? "height" : "width"] : 0
            },
            getLabelSides: function() {
                var a = this.label.getBBox(),
                    b = this.axis,
                    c = b.horiz,
                    d = b.options.labels,
                    a = c ? a.width : a.height,
                    b = c ? d.x - a * {
                        left: 0,
                        center: .5,
                        right: 1
                    }[b.labelAlign] : 0;
                return [b, c ? a + b : a]
            },
            handleOverflow: function(a, b) {
                var l, m, n, c = !0,
                    d = this.axis,
                    e = this.isFirst,
                    f = this.isLast,
                    g = d.horiz ? b.x : b.y,
                    h = d.reversed,
                    i = d.tickPositions,
                    j = this.getLabelSides(),
                    k = j[0],
                    j = j[1],
                    o = this.label.line || 0;
                if (l = d.labelEdge, m = d.justifyLabels && (e || f), l[o] === t || g + k > l[o] ? l[o] = g + j : m || (c = !1), m) {
                    l = (m = d.justifyToPlot) ? d.pos : 0, m = m ? l + d.len : d.chart.chartWidth;
                    do a += e ? 1 : -1, n = d.ticks[i[a]]; while (i[a] && (!n || !n.label || n.label.line !== o));
                    d = n && n.label.xy && n.label.xy.x + n.getLabelSides()[e ? 0 : 1], e && !h || f && h ? l > g + k && (g = l - k, n && g + j > d && (c = !1)) : g + j > m && (g = m - j, n && d > g + k && (c = !1)), b.x = g
                }
                return c
            },
            getPosition: function(a, b, c, d) {
                var e = this.axis,
                    f = e.chart,
                    g = d && f.oldChartHeight || f.chartHeight;
                return {
                    x: a ? e.translate(b + c, null, null, d) + e.transB : e.left + e.offset + (e.opposite ? (d && f.oldChartWidth || f.chartWidth) - e.right - e.left : 0),
                    y: a ? g - e.bottom + e.offset - (e.opposite ? e.height : 0) : g - e.translate(b + c, null, null, d) - e.transB
                }
            },
            getLabelPosition: function(a, b, c, d, e, f, g, h) {
                var i = this.axis,
                    j = i.transA,
                    k = i.reversed,
                    l = i.staggerLines,
                    a = a + e.x - (f && d ? f * j * (k ? -1 : 1) : 0),
                    b = b + this.yOffset - (f && !d ? f * j * (k ? 1 : -1) : 0);
                return l && (c.line = g / (h || 1) % l, b += c.line * (i.labelOffset / l)), {
                    x: a,
                    y: b
                }
            },
            getMarkPath: function(a, b, c, d, e, f) {
                return f.crispLine(["M", a, b, "L", a + (e ? 0 : -c), b + (e ? c : 0)], d)
            },
            render: function(a, b, c) {
                var d = this.axis,
                    e = d.options,
                    f = d.chart.renderer,
                    g = d.horiz,
                    h = this.type,
                    i = this.label,
                    j = this.pos,
                    k = e.labels,
                    l = this.gridLine,
                    m = h ? h + "Grid" : "grid",
                    n = h ? h + "Tick" : "tick",
                    o = e[m + "LineWidth"],
                    q = e[m + "LineColor"],
                    E = e[m + "LineDashStyle"],
                    I = e[n + "Length"],
                    m = e[n + "Width"] || 0,
                    D = e[n + "Color"],
                    u = e[n + "Position"],
                    n = this.mark,
                    s = k.step,
                    r = !0,
                    v = d.tickmarkOffset,
                    w = this.getPosition(g, j, v, b),
                    x = w.x,
                    w = w.y,
                    y = g && x === d.pos + d.len || !g && w === d.pos ? -1 : 1,
                    c = p(c, 1);
                this.isActive = !0, o && (j = d.getPlotLinePath(j + v, o * y, b, !0), l === t && (l = {
                    stroke: q,
                    "stroke-width": o
                }, E && (l.dashstyle = E), h || (l.zIndex = 1), b && (l.opacity = 0), this.gridLine = l = o ? f.path(j).attr(l).add(d.gridGroup) : null), !b && l && j && l[this.isNew ? "attr" : "animate"]({
                    d: j,
                    opacity: c
                })), m && I && ("inside" === u && (I = -I), d.opposite && (I = -I), h = this.getMarkPath(x, w, I, m * y, g, f), n ? n.animate({
                    d: h,
                    opacity: c
                }) : this.mark = f.path(h).attr({
                    stroke: D,
                    "stroke-width": m,
                    opacity: c
                }).add(d.axisGroup)), i && !isNaN(x) && (i.xy = w = this.getLabelPosition(x, w, i, g, k, v, a, s), this.isFirst && !this.isLast && !p(e.showFirstLabel, 1) || this.isLast && !this.isFirst && !p(e.showLastLabel, 1) ? r = !1 : !d.isRadial && !k.step && !k.rotation && !b && 0 !== c && (r = this.handleOverflow(a, w)), s && a % s && (r = !1), r && !isNaN(w.y) ? (w.opacity = c, i[this.isNew ? "attr" : "animate"](w), this.isNew = !1) : i.attr("y", -9999))
            },
            destroy: function() {
                Oa(this, this.axis)
            }
        }, S.PlotLineOrBand = function(a, b) {
            this.axis = a, b && (this.options = b, this.id = b.id)
        }, S.PlotLineOrBand.prototype = {
            render: function() {
                var p, a = this,
                    b = a.axis,
                    c = b.horiz,
                    d = (b.pointRange || 0) / 2,
                    e = a.options,
                    f = e.label,
                    g = a.label,
                    h = e.width,
                    i = e.to,
                    j = e.from,
                    k = s(j) && s(i),
                    l = e.value,
                    m = e.dashStyle,
                    n = a.svgElem,
                    o = [],
                    q = e.color,
                    I = e.zIndex,
                    D = e.events,
                    r = {},
                    t = b.chart.renderer;
                if (b.isLog && (j = za(j), i = za(i), l = za(l)), h) o = b.getPlotLinePath(l, h), r = {
                    stroke: q,
                    "stroke-width": h
                }, m && (r.dashstyle = m);
                else {
                    if (!k) return;
                    j = u(j, b.min - d), i = C(i, b.max + d), o = b.getPlotBandPath(j, i, e), q && (r.fill = q), e.borderWidth && (r.stroke = e.borderColor, r["stroke-width"] = e.borderWidth)
                } if (s(I) && (r.zIndex = I), n) o ? n.animate({
                    d: o
                }, null, n.onGetPath) : (n.hide(), n.onGetPath = function() {
                    n.show()
                }, g && (a.label = g = g.destroy()));
                else if (o && o.length && (a.svgElem = n = t.path(o).attr(r).add(), D))
                    for (p in d = function(b) {
                        n.on(b, function(c) {
                            D[b].apply(a, [c])
                        })
                    }, D) d(p);
                return f && s(f.text) && o && o.length && b.width > 0 && b.height > 0 ? (f = w({
                    align: c && k && "center",
                    x: c ? !k && 4 : 10,
                    verticalAlign: !c && k && "middle",
                    y: c ? k ? 16 : 10 : k ? 6 : -4,
                    rotation: c && !k && 90
                }, f), g || (r = {
                    align: f.textAlign || f.align,
                    rotation: f.rotation
                }, s(I) && (r.zIndex = I), a.label = g = t.text(f.text, 0, 0, f.useHTML).attr(r).css(f.style).add()), b = [o[1], o[4], k ? o[6] : o[1]], k = [o[2], o[5], k ? o[7] : o[2]], o = Na(b), c = Na(k), g.align(f, !1, {
                    x: o,
                    y: c,
                    width: Ba(b) - o,
                    height: Ba(k) - c
                }), g.show()) : g && g.hide(), a
            },
            destroy: function() {
                ka(this.axis.plotLinesAndBands, this), delete this.axis, Oa(this)
            }
        }, ma.prototype = {
            defaultOptions: {
                dateTimeLabelFormats: {
                    millisecond: "%H:%M:%S.%L",
                    second: "%H:%M:%S",
                    minute: "%H:%M",
                    hour: "%H:%M",
                    day: "%e. %b",
                    week: "%e. %b",
                    month: "%b '%y",
                    year: "%Y"
                },
                endOnTick: !1,
                gridLineColor: "#C0C0C0",
                labels: M,
                lineColor: "#C0D0E0",
                lineWidth: 1,
                minPadding: .01,
                maxPadding: .01,
                minorGridLineColor: "#E0E0E0",
                minorGridLineWidth: 1,
                minorTickColor: "#A0A0A0",
                minorTickLength: 2,
                minorTickPosition: "outside",
                startOfWeek: 1,
                startOnTick: !1,
                tickColor: "#C0D0E0",
                tickLength: 10,
                tickmarkPlacement: "between",
                tickPixelInterval: 100,
                tickPosition: "outside",
                tickWidth: 1,
                title: {
                    align: "middle",
                    style: {
                        color: "#707070"
                    }
                },
                type: "linear"
            },
            defaultYAxisOptions: {
                endOnTick: !0,
                gridLineWidth: 1,
                tickPixelInterval: 72,
                showLastLabel: !0,
                labels: {
                    x: -8,
                    y: 3
                },
                lineWidth: 0,
                maxPadding: .05,
                minPadding: .05,
                startOnTick: !0,
                tickWidth: 0,
                title: {
                    rotation: 270,
                    text: "Values"
                },
                stackLabels: {
                    enabled: !1,
                    formatter: function() {
                        return Ga(this.total, -1)
                    },
                    style: M.style
                }
            },
            defaultLeftAxisOptions: {
                labels: {
                    x: -15,
                    y: null
                },
                title: {
                    rotation: 270
                }
            },
            defaultRightAxisOptions: {
                labels: {
                    x: 15,
                    y: null
                },
                title: {
                    rotation: 90
                }
            },
            defaultBottomAxisOptions: {
                labels: {
                    x: 0,
                    y: null
                },
                title: {
                    rotation: 0
                }
            },
            defaultTopAxisOptions: {
                labels: {
                    x: 0,
                    y: -15
                },
                title: {
                    rotation: 0
                }
            },
            init: function(a, b) {
                var c = b.isX;
                this.horiz = a.inverted ? !c : c, this.coll = (this.isXAxis = c) ? "xAxis" : "yAxis", this.opposite = b.opposite, this.side = b.side || (this.horiz ? this.opposite ? 0 : 2 : this.opposite ? 1 : 3), this.setOptions(b);
                var d = this.options,
                    e = d.type;
                this.labelFormatter = d.labels.formatter || this.defaultLabelFormatter, this.userOptions = b, this.minPixelPadding = 0, this.chart = a, this.reversed = d.reversed, this.zoomEnabled = d.zoomEnabled !== !1, this.categories = d.categories || "category" === e, this.names = [], this.isLog = "logarithmic" === e, this.isDatetimeAxis = "datetime" === e, this.isLinked = s(d.linkedTo), this.tickmarkOffset = this.categories && "between" === d.tickmarkPlacement ? .5 : 0, this.ticks = {}, this.labelEdge = [], this.minorTicks = {}, this.plotLinesAndBands = [], this.alternateBands = {}, this.len = 0, this.minRange = this.userMinRange = d.minRange || d.maxZoom, this.range = d.range, this.offset = d.offset || 0, this.stacks = {}, this.oldStacks = {}, this.min = this.max = null, this.crosshair = p(d.crosshair, ra(a.options.tooltip.crosshairs)[c ? 0 : 1], !1);
                var f, d = this.options.events; - 1 === Da(this, a.axes) && (c && !this.isColorAxis ? a.axes.splice(a.xAxis.length, 0, this) : a.axes.push(this), a[this.coll].push(this)), this.series = this.series || [], a.inverted && c && this.reversed === t && (this.reversed = !0), this.removePlotLine = this.removePlotBand = this.removePlotBandOrLine;
                for (f in d) N(this, f, d[f]);
                this.isLog && (this.val2lin = za, this.lin2val = ja)
            },
            setOptions: function(a) {
                this.options = w(this.defaultOptions, this.isXAxis ? {} : this.defaultYAxisOptions, [this.defaultTopAxisOptions, this.defaultRightAxisOptions, this.defaultBottomAxisOptions, this.defaultLeftAxisOptions][this.side], w(L[this.coll], a))
            },
            defaultLabelFormatter: function() {
                var g, a = this.axis,
                    b = this.value,
                    c = a.categories,
                    d = this.dateTimeLabelFormat,
                    e = L.lang.numericSymbols,
                    f = e && e.length,
                    h = a.options.labels.format,
                    a = a.isLog ? b : a.tickInterval;
                if (h) g = Ia(h, this);
                else if (c) g = b;
                else if (d) g = bb(d, b);
                else if (f && a >= 1e3)
                    for (; f-- && g === t;) c = Math.pow(1e3, f + 1), a >= c && null !== e[f] && (g = Ga(b / c, -1) + e[f]);
                return g === t && (g = Q(b) >= 1e4 ? Ga(b, 0) : Ga(b, -1, t, "")), g
            },
            getSeriesExtremes: function() {
                var a = this,
                    b = a.chart;
                a.hasVisibleSeries = !1, a.dataMin = a.dataMax = null, a.buildStacks && a.buildStacks(), q(a.series, function(c) {
                    if (c.visible || !b.options.chart.ignoreHiddenSeries) {
                        var d;
                        d = c.options.threshold;
                        var e;
                        a.hasVisibleSeries = !0, a.isLog && 0 >= d && (d = null), a.isXAxis ? (d = c.xData, d.length && (a.dataMin = C(p(a.dataMin, d[0]), Na(d)), a.dataMax = u(p(a.dataMax, d[0]), Ba(d)))) : (c.getExtremes(), e = c.dataMax, c = c.dataMin, s(c) && s(e) && (a.dataMin = C(p(a.dataMin, c), c), a.dataMax = u(p(a.dataMax, e), e)), s(d) && (a.dataMin >= d ? (a.dataMin = d, a.ignoreMinPadding = !0) : a.dataMax < d && (a.dataMax = d, a.ignoreMaxPadding = !0)))
                    }
                })
            },
            translate: function(a, b, c, d, e, f) {
                var g = 1,
                    h = 0,
                    i = d ? this.oldTransA : this.transA,
                    d = d ? this.oldMin : this.min,
                    j = this.minPixelPadding,
                    e = (this.options.ordinal || this.isLog && e) && this.lin2val;
                return i || (i = this.transA), c && (g *= -1, h = this.len), this.reversed && (g *= -1, h -= g * (this.sector || this.len)), b ? (a = a * g + h, a -= j, a = a / i + d, e && (a = this.lin2val(a))) : (e && (a = this.val2lin(a)), "between" === f && (f = .5), a = g * (a - d) * i + h + g * j + (ia(f) ? i * f * this.pointRange : 0)), a
            },
            toPixels: function(a, b) {
                return this.translate(a, !1, !this.horiz, null, !0) + (b ? 0 : this.pos)
            },
            toValue: function(a, b) {
                return this.translate(a - (b ? 0 : this.pos), !0, !this.horiz, null, !0)
            },
            getPlotLinePath: function(a, b, c, d, e) {
                var i, j, m, f = this.chart,
                    g = this.left,
                    h = this.top,
                    k = c && f.oldChartHeight || f.chartHeight,
                    l = c && f.oldChartWidth || f.chartWidth;
                return i = this.transB, e = p(e, this.translate(a, null, null, c)), a = c = v(e + i), i = j = v(k - e - i), isNaN(e) ? m = !0 : this.horiz ? (i = h, j = k - this.bottom, (g > a || a > g + this.width) && (m = !0)) : (a = g, c = l - this.right, (h > i || i > h + this.height) && (m = !0)), m && !d ? null : f.renderer.crispLine(["M", a, i, "L", c, j], b || 1)
            },
            getLinearTickPositions: function(a, b, c) {
                var d, e = ea(U(b / a) * a),
                    f = ea(Ka(c / a) * a),
                    g = [];
                if (b === c && ia(b)) return [b];
                for (b = e; f >= b && (g.push(b), b = ea(b + a), b !== d);) d = b;
                return g
            },
            getMinorTickPositions: function() {
                var e, a = this.options,
                    b = this.tickPositions,
                    c = this.minorTickInterval,
                    d = [];
                if (this.isLog)
                    for (e = b.length, a = 1; e > a; a++) d = d.concat(this.getLogTickPositions(c, b[a - 1], b[a], !0));
                else if (this.isDatetimeAxis && "auto" === a.minorTickInterval) d = d.concat(this.getTimeTicks(this.normalizeTimeTickInterval(c), this.min, this.max, a.startOfWeek)), d[0] < this.min && d.shift();
                else
                    for (b = this.min + (b[0] - this.min) % c; b <= this.max; b += c) d.push(b);
                return d
            },
            adjustForMinRange: function() {
                var d, f, g, h, i, j, a = this.options,
                    b = this.min,
                    c = this.max,
                    e = this.dataMax - this.dataMin >= this.minRange;
                if (this.isXAxis && this.minRange === t && !this.isLog && (s(a.min) || s(a.max) ? this.minRange = null : (q(this.series, function(a) {
                    for (i = a.xData, g = j = a.xIncrement ? 1 : i.length - 1; g > 0; g--) h = i[g] - i[g - 1], (f === t || f > h) && (f = h)
                }), this.minRange = C(5 * f, this.dataMax - this.dataMin))), c - b < this.minRange) {
                    var k = this.minRange;
                    d = (k - c + b) / 2, d = [b - d, p(a.min, b - d)], e && (d[2] = this.dataMin), b = Ba(d), c = [b + k, p(a.max, b + k)], e && (c[2] = this.dataMax), c = Na(c), k > c - b && (d[0] = c - k, d[1] = p(a.min, c - k), b = Ba(d))
                }
                this.min = b, this.max = c
            },
            setAxisTranslation: function(a) {
                var e, b = this,
                    c = b.max - b.min,
                    d = b.axisPointRange || 0,
                    f = 0,
                    g = 0,
                    h = b.linkedParent,
                    i = !!b.categories,
                    j = b.transA;
                (b.isXAxis || i || d) && (h ? (f = h.minPointOffset, g = h.pointRangePadding) : q(b.series, function(a) {
                    var h = i ? 1 : b.isXAxis ? a.pointRange : b.axisPointRange || 0,
                        j = a.options.pointPlacement,
                        n = a.closestPointRange;
                    h > c && (h = 0), d = u(d, h), f = u(f, Fa(j) ? 0 : h / 2), g = u(g, "on" === j ? 0 : h), !a.noSharedTooltip && s(n) && (e = s(e) ? C(e, n) : n)
                }), h = b.ordinalSlope && e ? b.ordinalSlope / e : 1, b.minPointOffset = f *= h, b.pointRangePadding = g *= h, b.pointRange = C(d, c), b.closestPointRange = e), a && (b.oldTransA = j), b.translationSlope = b.transA = j = b.len / (c + g || 1), b.transB = b.horiz ? b.left : b.bottom, b.minPixelPadding = j * f
            },
            setTickPositions: function(a) {
                var E, b = this,
                    c = b.chart,
                    d = b.options,
                    e = d.startOnTick,
                    f = d.endOnTick,
                    g = b.isLog,
                    h = b.isDatetimeAxis,
                    i = b.isXAxis,
                    j = b.isLinked,
                    k = b.options.tickPositioner,
                    l = d.maxPadding,
                    m = d.minPadding,
                    n = d.tickInterval,
                    o = d.minTickInterval,
                    Y = d.tickPixelInterval,
                    I = b.categories;
                j ? (b.linkedParent = c[b.coll][d.linkedTo], c = b.linkedParent.getExtremes(), b.min = p(c.min, c.dataMin), b.max = p(c.max, c.dataMax), d.type !== b.linkedParent.options.type && oa(11, 1)) : (b.min = p(b.userMin, d.min, b.dataMin), b.max = p(b.userMax, d.max, b.dataMax)), g && (!a && C(b.min, p(b.dataMin, b.min)) <= 0 && oa(10, 1), b.min = ea(za(b.min)), b.max = ea(za(b.max))), b.range && s(b.max) && (b.userMin = b.min = u(b.min, b.max - b.range), b.userMax = b.max, b.range = null), b.beforePadding && b.beforePadding(), b.adjustForMinRange(), I || b.axisPointRange || b.usePercentage || j || !s(b.min) || !s(b.max) || !(c = b.max - b.min) || (s(d.min) || s(b.userMin) || !m || !(b.dataMin < 0) && b.ignoreMinPadding || (b.min -= c * m), s(d.max) || s(b.userMax) || !l || !(b.dataMax > 0) && b.ignoreMaxPadding || (b.max += c * l)), ia(d.floor) && (b.min = u(b.min, d.floor)), ia(d.ceiling) && (b.max = C(b.max, d.ceiling)), b.min === b.max || void 0 === b.min || void 0 === b.max ? b.tickInterval = 1 : j && !n && Y === b.linkedParent.options.tickPixelInterval ? b.tickInterval = b.linkedParent.tickInterval : (b.tickInterval = p(n, I ? 1 : (b.max - b.min) * Y / u(b.len, Y)), !s(n) && b.len < Y && !this.isRadial && !this.isLog && !I && e && f && (E = !0, b.tickInterval /= 4)), i && !a && q(b.series, function(a) {
                    a.processData(b.min !== b.oldMin || b.max !== b.oldMax)
                }), b.setAxisTranslation(!0), b.beforeSetTickPositions && b.beforeSetTickPositions(), b.postProcessTickInterval && (b.tickInterval = b.postProcessTickInterval(b.tickInterval)), b.pointRange && (b.tickInterval = u(b.pointRange, b.tickInterval)), !n && b.tickInterval < o && (b.tickInterval = o), h || g || n || (b.tickInterval = mb(b.tickInterval, null, lb(b.tickInterval), d)), b.minorTickInterval = "auto" === d.minorTickInterval && b.tickInterval ? b.tickInterval / 5 : d.minorTickInterval, b.tickPositions = a = d.tickPositions ? [].concat(d.tickPositions) : k && k.apply(b, [b.min, b.max]), a || (!b.ordinalPositions && (b.max - b.min) / b.tickInterval > u(2 * b.len, 200) && oa(19, !0), a = h ? b.getTimeTicks(b.normalizeTimeTickInterval(b.tickInterval, d.units), b.min, b.max, d.startOfWeek, b.ordinalPositions, b.closestPointRange, !0) : g ? b.getLogTickPositions(b.tickInterval, b.min, b.max) : b.getLinearTickPositions(b.tickInterval, b.min, b.max), E && a.splice(1, a.length - 2), b.tickPositions = a), j || (d = a[0], g = a[a.length - 1], h = b.minPointOffset || 0, !e && !f && !I && 2 === a.length && a.splice(1, 0, (g + d) / 2), e ? b.min = d : b.min - h > d && a.shift(), f ? b.max = g : b.max + h < g && a.pop(), 1 === a.length && (e = Q(b.max) > 1e13 ? 1 : .001, b.min -= e, b.max += e))
            },
            setMaxTicks: function() {
                var a = this.chart,
                    b = a.maxTicks || {},
                    c = this.tickPositions,
                    d = this._maxTicksKey = [this.coll, this.pos, this.len].join("-");
                !this.isLinked && !this.isDatetimeAxis && c && c.length > (b[d] || 0) && this.options.alignTicks !== !1 && (b[d] = c.length), a.maxTicks = b
            },
            adjustTickAmount: function() {
                var a = this._maxTicksKey,
                    b = this.tickPositions,
                    c = this.chart.maxTicks;
                if (c && c[a] && !this.isDatetimeAxis && !this.categories && !this.isLinked && this.options.alignTicks !== !1 && this.min !== t) {
                    var d = this.tickAmount,
                        e = b.length;
                    if (this.tickAmount = a = c[a], a > e) {
                        for (; b.length < a;) b.push(ea(b[b.length - 1] + this.tickInterval));
                        this.transA *= (e - 1) / (a - 1), this.max = b[b.length - 1]
                    }
                    s(d) && a !== d && (this.isDirty = !0)
                }
            },
            setScale: function() {
                var b, c, d, e, a = this.stacks;
                if (this.oldMin = this.min, this.oldMax = this.max, this.oldAxisLength = this.len, this.setAxisSize(), e = this.len !== this.oldAxisLength, q(this.series, function(a) {
                    (a.isDirtyData || a.isDirty || a.xAxis.isDirty) && (d = !0)
                }), e || d || this.isLinked || this.forceRedraw || this.userMin !== this.oldUserMin || this.userMax !== this.oldUserMax) {
                    if (!this.isXAxis)
                        for (b in a)
                            for (c in a[b]) a[b][c].total = null, a[b][c].cum = 0;
                    this.forceRedraw = !1, this.getSeriesExtremes(), this.setTickPositions(), this.oldUserMin = this.userMin, this.oldUserMax = this.userMax, this.isDirty || (this.isDirty = e || this.min !== this.oldMin || this.max !== this.oldMax)
                } else if (!this.isXAxis) {
                    this.oldStacks && (a = this.stacks = this.oldStacks);
                    for (b in a)
                        for (c in a[b]) a[b][c].cum = a[b][c].total
                }
                this.setMaxTicks()
            },
            setExtremes: function(a, b, c, d, e) {
                var f = this,
                    g = f.chart,
                    c = p(c, !0),
                    e = r(e, {
                        min: a,
                        max: b
                    });
                K(f, "setExtremes", e, function() {
                    f.userMin = a, f.userMax = b, f.eventArgs = e, f.isDirtyExtremes = !0, c && g.redraw(d)
                })
            },
            zoom: function(a, b) {
                var c = this.dataMin,
                    d = this.dataMax,
                    e = this.options;
                return this.allowZoomOutside || (s(c) && a <= C(c, p(e.min, c)) && (a = t), s(d) && b >= u(d, p(e.max, d)) && (b = t)), this.displayBtn = a !== t || b !== t, this.setExtremes(a, b, !1, t, {
                    trigger: "zoom"
                }), !0
            },
            setAxisSize: function() {
                var a = this.chart,
                    b = this.options,
                    c = b.offsetLeft || 0,
                    d = this.horiz,
                    e = p(b.width, a.plotWidth - c + (b.offsetRight || 0)),
                    f = p(b.height, a.plotHeight),
                    g = p(b.top, a.plotTop),
                    b = p(b.left, a.plotLeft + c),
                    c = /%$/;
                c.test(f) && (f = parseInt(f, 10) / 100 * a.plotHeight), c.test(g) && (g = parseInt(g, 10) / 100 * a.plotHeight + a.plotTop), this.left = b, this.top = g, this.width = e, this.height = f, this.bottom = a.chartHeight - f - g, this.right = a.chartWidth - e - b, this.len = u(d ? e : f, 0), this.pos = d ? b : g
            },
            getExtremes: function() {
                var a = this.isLog;
                return {
                    min: a ? ea(ja(this.min)) : this.min,
                    max: a ? ea(ja(this.max)) : this.max,
                    dataMin: this.dataMin,
                    dataMax: this.dataMax,
                    userMin: this.userMin,
                    userMax: this.userMax
                }
            },
            getThreshold: function(a) {
                var b = this.isLog,
                    c = b ? ja(this.min) : this.min,
                    b = b ? ja(this.max) : this.max;
                return c > a || null === a ? a = c : a > b && (a = b), this.translate(a, 0, 1, 0, 1)
            },
            autoLabelAlign: function(a) {
                return a = (p(a, 0) - 90 * this.side + 720) % 360, a > 15 && 165 > a ? "right" : a > 195 && 345 > a ? "left" : "center"
            },
            getOffset: function() {
                var j, k, m, r, x, z, C, y, R, a = this,
                    b = a.chart,
                    c = b.renderer,
                    d = a.options,
                    e = a.tickPositions,
                    f = a.ticks,
                    g = a.horiz,
                    h = a.side,
                    i = b.inverted ? [1, 0, 3, 2][h] : h,
                    l = 0,
                    n = 0,
                    o = d.title,
                    Y = d.labels,
                    E = 0,
                    I = b.axisOffset,
                    b = b.clipOffset,
                    D = [-1, 1, 1, -1][h],
                    v = 1,
                    w = p(Y.maxStaggerLines, 5);
                if (a.hasData = j = a.hasVisibleSeries || s(a.min) && s(a.max) && !!e, a.showAxis = k = j || p(d.showEmpty, !0), a.staggerLines = a.horiz && Y.staggerLines, a.axisGroup || (a.gridGroup = c.g("grid").attr({
                    zIndex: d.gridZIndex || 1
                }).add(), a.axisGroup = c.g("axis").attr({
                    zIndex: d.zIndex || 2
                }).add(), a.labelGroup = c.g("axis-labels").attr({
                    zIndex: Y.zIndex || 7
                }).addClass("highcharts-" + a.coll.toLowerCase() + "-labels").add()), j || a.isLinked) {
                    if (a.labelAlign = p(Y.align || a.autoLabelAlign(Y.rotation)), q(e, function(b) {
                        f[b] ? f[b].addLabel() : f[b] = new Sa(a, b)
                    }), a.horiz && !a.staggerLines && w && !Y.rotation) {
                        for (j = a.reversed ? [].concat(e).reverse() : e; w > v;) {
                            for (x = [], z = !1, r = 0; r < j.length; r++) C = j[r], y = (y = f[C].label && f[C].label.getBBox()) ? y.width : 0, R = r % v, y && (C = a.translate(C), x[R] !== t && C < x[R] && (z = !0), x[R] = C + y);
                            if (!z) break;
                            v++
                        }
                        v > 1 && (a.staggerLines = v)
                    }
                    q(e, function(b) {
                        (0 === h || 2 === h || {
                            1: "left",
                            3: "right"
                        }[h] === a.labelAlign) && (E = u(f[b].getLabelSize(), E))
                    }), a.staggerLines && (E *= a.staggerLines, a.labelOffset = E)
                } else
                    for (r in f) f[r].destroy(), delete f[r];
                o && o.text && o.enabled !== !1 && (a.axisTitle || (a.axisTitle = c.text(o.text, 0, 0, o.useHTML).attr({
                    zIndex: 7,
                    rotation: o.rotation || 0,
                    align: o.textAlign || {
                        low: "left",
                        middle: "center",
                        high: "right"
                    }[o.align]
                }).addClass("highcharts-" + this.coll.toLowerCase() + "-title").css(o.style).add(a.axisGroup), a.axisTitle.isNew = !0), k && (l = a.axisTitle.getBBox()[g ? "height" : "width"], m = o.offset, n = s(m) ? 0 : p(o.margin, g ? 5 : 10)), a.axisTitle[k ? "show" : "hide"]()), a.offset = D * p(d.offset, I[h]), c = 2 === h ? a.tickBaseline : 0, g = E + n + (E && D * (g ? p(Y.y, a.tickBaseline + 8) : Y.x) - c), a.axisTitleMargin = p(m, g), I[h] = u(I[h], a.axisTitleMargin + l + D * a.offset, g), b[i] = u(b[i], 2 * U(d.lineWidth / 2))
            },
            getLinePath: function(a) {
                var b = this.chart,
                    c = this.opposite,
                    d = this.offset,
                    e = this.horiz,
                    f = this.left + (c ? this.width : 0) + d,
                    d = b.chartHeight - this.bottom - (c ? this.height : 0) + d;
                return c && (a *= -1), b.renderer.crispLine(["M", e ? this.left : f, e ? d : this.top, "L", e ? b.chartWidth - this.right : f, e ? d : b.chartHeight - this.bottom], a)
            },
            getTitlePosition: function() {
                var a = this.horiz,
                    b = this.left,
                    c = this.top,
                    d = this.len,
                    e = this.options.title,
                    f = a ? b : c,
                    g = this.opposite,
                    h = this.offset,
                    i = z(e.style.fontSize || 12),
                    d = {
                        low: f + (a ? 0 : d),
                        middle: f + d / 2,
                        high: f + (a ? d : 0)
                    }[e.align],
                    b = (a ? c + this.height : b) + (a ? 1 : -1) * (g ? -1 : 1) * this.axisTitleMargin + (2 === this.side ? i : 0);
                return {
                    x: a ? d : b + (g ? this.width : 0) + h + (e.x || 0),
                    y: a ? b - (g ? this.height : 0) + h : d + (e.y || 0)
                }
            },
            render: function() {
                var j, v, z, a = this,
                    b = a.horiz,
                    c = a.reversed,
                    d = a.chart,
                    e = d.renderer,
                    f = a.options,
                    g = a.isLog,
                    h = a.isLinked,
                    i = a.tickPositions,
                    k = a.axisTitle,
                    l = a.ticks,
                    m = a.minorTicks,
                    n = a.alternateBands,
                    o = f.stackLabels,
                    p = f.alternateGridColor,
                    E = a.tickmarkOffset,
                    I = f.lineWidth,
                    D = d.hasRendered && s(a.oldMin) && !isNaN(a.oldMin),
                    r = a.hasData,
                    u = a.showAxis,
                    w = f.labels.overflow,
                    x = a.justifyLabels = b && w !== !1;
                a.labelEdge.length = 0, a.justifyToPlot = "justify" === w, q([l, m, n], function(a) {
                    for (var b in a) a[b].isActive = !1
                }), (r || h) && (a.minorTickInterval && !a.categories && q(a.getMinorTickPositions(), function(b) {
                    m[b] || (m[b] = new Sa(a, b, "minor")), D && m[b].isNew && m[b].render(null, !0), m[b].render(null, !1, 1)
                }), i.length && (j = i.slice(), (b && c || !b && !c) && j.reverse(), x && (j = j.slice(1).concat([j[0]])), q(j, function(b, c) {
                    x && (c = c === j.length - 1 ? 0 : c + 1), (!h || b >= a.min && b <= a.max) && (l[b] || (l[b] = new Sa(a, b)), D && l[b].isNew && l[b].render(c, !0, .1), l[b].render(c))
                }), E && 0 === a.min && (l[-1] || (l[-1] = new Sa(a, -1, null, !0)), l[-1].render(-1))), p && q(i, function(b, c) {
                    c % 2 === 0 && b < a.max && (n[b] || (n[b] = new S.PlotLineOrBand(a)), v = b + E, z = i[c + 1] !== t ? i[c + 1] + E : a.max, n[b].options = {
                        from: g ? ja(v) : v,
                        to: g ? ja(z) : z,
                        color: p
                    }, n[b].render(), n[b].isActive = !0)
                }), a._addedPlotLB || (q((f.plotLines || []).concat(f.plotBands || []), function(b) {
                    a.addPlotBandOrLine(b)
                }), a._addedPlotLB = !0)), q([l, m, n], function(a) {
                    var b, c, e = [],
                        f = va ? va.duration || 500 : 0,
                        g = function() {
                            for (c = e.length; c--;) a[e[c]] && !a[e[c]].isActive && (a[e[c]].destroy(), delete a[e[c]])
                        };
                    for (b in a) a[b].isActive || (a[b].render(b, !1, 0), a[b].isActive = !1, e.push(b));
                    a !== n && d.hasRendered && f ? f && setTimeout(g, f) : g()
                }), I && (b = a.getLinePath(I), a.axisLine ? a.axisLine.animate({
                    d: b
                }) : a.axisLine = e.path(b).attr({
                    stroke: f.lineColor,
                    "stroke-width": I,
                    zIndex: 7
                }).add(a.axisGroup), a.axisLine[u ? "show" : "hide"]()), k && u && (k[k.isNew ? "attr" : "animate"](a.getTitlePosition()), k.isNew = !1), o && o.enabled && a.renderStackTotals(), a.isDirty = !1
            },
            redraw: function() {
                this.render(), q(this.plotLinesAndBands, function(a) {
                    a.render()
                }), q(this.series, function(a) {
                    a.isDirty = !0
                })
            },
            destroy: function(a) {
                var d, b = this,
                    c = b.stacks,
                    e = b.plotLinesAndBands;
                a || X(b);
                for (d in c) Oa(c[d]), c[d] = null;
                for (q([b.ticks, b.minorTicks, b.alternateBands], function(a) {
                    Oa(a)
                }), a = e.length; a--;) e[a].destroy();
                q("stackTotalGroup,axisLine,axisTitle,axisGroup,cross,gridGroup,labelGroup".split(","), function(a) {
                    b[a] && (b[a] = b[a].destroy())
                }), this.cross && this.cross.destroy()
            },
            drawCrosshair: function(a, b) {
                if (this.crosshair)
                    if ((s(b) || !p(this.crosshair.snap, !0)) === !1) this.hideCrosshair();
                    else {
                        var c, d = this.crosshair,
                            e = d.animation;
                        p(d.snap, !0) ? s(b) && (c = this.chart.inverted != this.horiz ? b.plotX : this.len - b.plotY) : c = this.horiz ? a.chartX - this.pos : this.len - a.chartY + this.pos, c = this.isRadial ? this.getPlotLinePath(this.isXAxis ? b.x : p(b.stackY, b.y)) : this.getPlotLinePath(null, null, null, null, c), null === c ? this.hideCrosshair() : this.cross ? this.cross.attr({
                            visibility: "visible"
                        })[e ? "animate" : "attr"]({
                            d: c
                        }, e) : (e = {
                            "stroke-width": d.width || 1,
                            stroke: d.color || "#C0C0C0",
                            zIndex: d.zIndex || 2
                        }, d.dashStyle && (e.dashstyle = d.dashStyle), this.cross = this.chart.renderer.path(c).attr(e).add())
                    }
            },
            hideCrosshair: function() {
                this.cross && this.cross.hide()
            }
        }, r(ma.prototype, {
            getPlotBandPath: function(a, b) {
                var c = this.getPlotLinePath(b),
                    d = this.getPlotLinePath(a);
                return d && c ? d.push(c[4], c[5], c[1], c[2]) : d = null, d
            },
            addPlotBand: function(a) {
                return this.addPlotBandOrLine(a, "plotBands")
            },
            addPlotLine: function(a) {
                return this.addPlotBandOrLine(a, "plotLines")
            },
            addPlotBandOrLine: function(a, b) {
                var c = new S.PlotLineOrBand(this, a).render(),
                    d = this.userOptions;
                return c && (b && (d[b] = d[b] || [], d[b].push(a)), this.plotLinesAndBands.push(c)), c
            },
            removePlotBandOrLine: function(a) {
                for (var b = this.plotLinesAndBands, c = this.options, d = this.userOptions, e = b.length; e--;) b[e].id === a && b[e].destroy();
                q([c.plotLines || [], d.plotLines || [], c.plotBands || [], d.plotBands || []], function(b) {
                    for (e = b.length; e--;) b[e].id === a && ka(b, b[e])
                })
            }
        }), ma.prototype.getTimeTicks = function(a, b, c, d) {
            var h, e = [],
                f = {},
                g = L.global.useUTC,
                i = new Date(b - Ra),
                j = a.unitRange,
                k = a.count;
            if (s(b)) {
                j >= B.second && (i.setMilliseconds(0), i.setSeconds(j >= B.minute ? 0 : k * U(i.getSeconds() / k))), j >= B.minute && i[Bb](j >= B.hour ? 0 : k * U(i[ob]() / k)), j >= B.hour && i[Cb](j >= B.day ? 0 : k * U(i[pb]() / k)), j >= B.day && i[rb](j >= B.month ? 1 : k * U(i[Wa]() / k)), j >= B.month && (i[Db](j >= B.year ? 0 : k * U(i[eb]() / k)), h = i[fb]()), j >= B.year && (h -= h % k, i[Eb](h)), j === B.week && i[rb](i[Wa]() - i[qb]() + p(d, 1)), b = 1, Ra && (i = new Date(i.getTime() + Ra)), h = i[fb]();
                for (var d = i.getTime(), l = i[eb](), m = i[Wa](), n = g ? Ra : (864e5 + 6e4 * i.getTimezoneOffset()) % 864e5; c > d;) e.push(d), j === B.year ? d = db(h + b * k, 0) : j === B.month ? d = db(h, l + b * k) : g || j !== B.day && j !== B.week ? d += j * k : d = db(h, l, m + b * k * (j === B.day ? 1 : 7)), b++;
                e.push(d), q(vb(e, function(a) {
                    return j <= B.hour && a % B.day === n
                }), function(a) {
                    f[a] = "day"
                })
            }
            return e.info = r(a, {
                higherRanks: f,
                totalRange: j * k
            }), e
        }, ma.prototype.normalizeTimeTickInterval = function(a, b) {
            var g, c = b || [
                    ["millisecond", [1, 2, 5, 10, 20, 25, 50, 100, 200, 500]],
                    ["second", [1, 2, 5, 10, 15, 30]],
                    ["minute", [1, 2, 5, 10, 15, 30]],
                    ["hour", [1, 2, 3, 4, 6, 8, 12]],
                    ["day", [1, 2]],
                    ["week", [1, 2]],
                    ["month", [1, 2, 3, 4, 6]],
                    ["year", null]
                ],
                d = c[c.length - 1],
                e = B[d[0]],
                f = d[1];
            for (g = 0; g < c.length && (d = c[g], e = B[d[0]], f = d[1], !(c[g + 1] && a <= (e * f[f.length - 1] + B[c[g + 1][0]]) / 2)); g++);
            return e === B.year && 5 * e > a && (f = [1, 2, 5]), c = mb(a / e, f, "year" === d[0] ? u(lb(a / e), 1) : 1), {
                unitRange: e,
                count: c,
                unitName: d[0]
            }
        }, ma.prototype.getLogTickPositions = function(a, b, c, d) {
            var e = this.options,
                f = this.len,
                g = [];
            if (d || (this._minorAutoInterval = null), a >= .5) a = v(a), g = this.getLinearTickPositions(a, b, c);
            else if (a >= .08)
                for (var h, i, j, k, l, f = U(b), e = a > .3 ? [1, 2, 4] : a > .15 ? [1, 2, 4, 6, 8] : [1, 2, 3, 4, 5, 6, 7, 8, 9]; c + 1 > f && !l; f++)
                    for (i = e.length, h = 0; i > h && !l; h++) j = za(ja(f) * e[h]), j > b && (!d || c >= k) && k !== t && g.push(k), k > c && (l = !0), k = j;
            else b = ja(b), c = ja(c), a = e[d ? "minorTickInterval" : "tickInterval"], a = p("auto" === a ? null : a, this._minorAutoInterval, (c - b) * (e.tickPixelInterval / (d ? 5 : 1)) / ((d ? f / this.tickPositions.length : f) || 1)), a = mb(a, null, lb(a)), g = Ua(this.getLinearTickPositions(a, b, c), za), d || (this._minorAutoInterval = a / 5);
            return d || (this.tickInterval = a), g
        };
        var Mb = S.Tooltip = function() {
            this.init.apply(this, arguments)
        };
        Mb.prototype = {
            init: function(a, b) {
                var c = b.borderWidth,
                    d = b.style,
                    e = z(d.padding);
                this.chart = a, this.options = b, this.crosshairs = [], this.now = {
                    x: 0,
                    y: 0
                }, this.isHidden = !0, this.label = a.renderer.label("", 0, 0, b.shape || "callout", null, null, b.useHTML, null, "tooltip").attr({
                    padding: e,
                    fill: b.backgroundColor,
                    "stroke-width": c,
                    r: b.borderRadius,
                    zIndex: 8
                }).css(d).css({
                    padding: 0
                }).add().attr({
                    y: -9999
                }), ga || this.label.shadow(b.shadow), this.shared = b.shared
            },
            destroy: function() {
                this.label && (this.label = this.label.destroy()), clearTimeout(this.hideTimer), clearTimeout(this.tooltipTimeout)
            },
            move: function(a, b, c, d) {
                var e = this,
                    f = e.now,
                    g = e.options.animation !== !1 && !e.isHidden && (Q(a - f.x) > 1 || Q(b - f.y) > 1),
                    h = e.followPointer || e.len > 1;
                r(f, {
                    x: g ? (2 * f.x + a) / 3 : a,
                    y: g ? (f.y + b) / 2 : b,
                    anchorX: h ? t : g ? (2 * f.anchorX + c) / 3 : c,
                    anchorY: h ? t : g ? (f.anchorY + d) / 2 : d
                }), e.label.attr(f), g && (clearTimeout(this.tooltipTimeout), this.tooltipTimeout = setTimeout(function() {
                    e && e.move(a, b, c, d)
                }, 32))
            },
            hide: function() {
                var b, a = this;
                clearTimeout(this.hideTimer), this.isHidden || (b = this.chart.hoverPoints, this.hideTimer = setTimeout(function() {
                    a.label.fadeOut(), a.isHidden = !0
                }, p(this.options.hideDelay, 500)), b && q(b, function(a) {
                    a.setState()
                }), this.chart.hoverPoints = null)
            },
            getAnchor: function(a, b) {
                var c, i, d = this.chart,
                    e = d.inverted,
                    f = d.plotTop,
                    g = 0,
                    h = 0,
                    a = ra(a);
                return c = a[0].tooltipPos, this.followPointer && b && (b.chartX === t && (b = d.pointer.normalize(b)), c = [b.chartX - d.plotLeft, b.chartY - f]), c || (q(a, function(a) {
                    i = a.series.yAxis, g += a.plotX, h += (a.plotLow ? (a.plotLow + a.plotHigh) / 2 : a.plotY) + (!e && i ? i.top - f : 0)
                }), g /= a.length, h /= a.length, c = [e ? d.plotWidth - h : g, this.shared && !e && a.length > 1 && b ? b.chartY - f : e ? d.plotHeight - g : h]), Ua(c, v)
            },
            getPosition: function(a, b, c) {
                var g, d = this.chart,
                    e = this.distance,
                    f = {},
                    h = ["y", d.chartHeight, b, c.plotY + d.plotTop],
                    i = ["x", d.chartWidth, a, c.plotX + d.plotLeft],
                    j = c.ttBelow || d.inverted && !c.negative || !d.inverted && c.negative,
                    k = function(a, b, c, d) {
                        var g = d - e > c,
                            b = b > d + e + c,
                            c = d - e - c;
                        if (d += e, j && b) f[a] = d;
                        else if (!j && g) f[a] = c;
                        else if (g) f[a] = c;
                        else {
                            if (!b) return !1;
                            f[a] = d
                        }
                    },
                    l = function(a, b, c, d) {
                        return e > d || d > b - e ? !1 : void(f[a] = c / 2 > d ? 1 : d > b - c / 2 ? b - c - 2 : d - c / 2)
                    },
                    m = function(a) {
                        var b = h;
                        h = i, i = b, g = a
                    },
                    n = function() {
                        k.apply(0, h) !== !1 ? l.apply(0, i) === !1 && !g && (m(!0), n()) : g ? f.x = f.y = 0 : (m(!0), n())
                    };
                return (d.inverted || this.len > 1) && m(), n(), f
            },
            defaultFormatter: function(a) {
                var d, b = this.points || ra(this),
                    c = b[0].series;
                return d = [a.tooltipHeaderFormatter(b[0])], q(b, function(a) {
                    c = a.series, d.push(c.tooltipFormatter && c.tooltipFormatter(a) || a.point.tooltipFormatter(c.tooltipOptions.pointFormat))
                }), d.push(a.options.footerFormat || ""), d.join("")
            },
            refresh: function(a, b) {
                var f, g, i, c = this.chart,
                    d = this.label,
                    e = this.options,
                    h = {},
                    j = [];
                i = e.formatter || this.defaultFormatter;
                var k, h = c.hoverPoints,
                    l = this.shared;
                clearTimeout(this.hideTimer), this.followPointer = ra(a)[0].series.tooltipOptions.followPointer, g = this.getAnchor(a, b), f = g[0], g = g[1], !l || a.series && a.series.noSharedTooltip ? h = a.getLabelConfig() : (c.hoverPoints = a, h && q(h, function(a) {
                    a.setState()
                }), q(a, function(a) {
                    a.setState("hover"), j.push(a.getLabelConfig())
                }), h = {
                    x: a[0].category,
                    y: a[0].y
                }, h.points = j, this.len = j.length, a = a[0]), i = i.call(h, this), h = a.series, this.distance = p(h.tooltipOptions.distance, 16), i === !1 ? this.hide() : (this.isHidden && (ab(d), d.attr("opacity", 1).show()), d.attr({
                    text: i
                }), k = e.borderColor || a.color || h.color || "#606060", d.attr({
                    stroke: k
                }), this.updatePosition({
                    plotX: f,
                    plotY: g,
                    negative: a.negative,
                    ttBelow: a.ttBelow
                }), this.isHidden = !1), K(c, "tooltipRefresh", {
                    text: i,
                    x: f + c.plotLeft,
                    y: g + c.plotTop,
                    borderColor: k
                })
            },
            updatePosition: function(a) {
                var b = this.chart,
                    c = this.label,
                    c = (this.options.positioner || this.getPosition).call(this, c.width, c.height, a);
                this.move(v(c.x), v(c.y), a.plotX + b.plotLeft, a.plotY + b.plotTop)
            },
            tooltipHeaderFormatter: function(a) {
                var h, b = a.series,
                    c = b.tooltipOptions,
                    d = c.dateTimeLabelFormats,
                    e = c.xDateFormat,
                    f = b.xAxis,
                    g = f && "datetime" === f.options.type && ia(a.key),
                    c = c.headerFormat,
                    f = f && f.closestPointRange;
                if (g && !e) {
                    if (f) {
                        for (h in B)
                            if (B[h] >= f || B[h] <= B.day && a.key % B[h] > 0) {
                                e = d[h];
                                break
                            }
                    } else e = d.day;
                    e = e || d.year
                }
                return g && e && (c = c.replace("{point.key}", "{point.key:" + e + "}")), Ia(c, {
                    point: a,
                    series: b
                })
            }
        };
        var pa;
        Za = x.documentElement.ontouchstart !== t;
        var Va = S.Pointer = function(a, b) {
            this.init(a, b)
        };
        if (Va.prototype = {
            init: function(a, b) {
                var f, c = b.chart,
                    d = c.events,
                    e = ga ? "" : c.zoomType,
                    c = a.inverted;
                this.options = b, this.chart = a, this.zoomX = f = /x/.test(e), this.zoomY = e = /y/.test(e), this.zoomHor = f && !c || e && c, this.zoomVert = e && !c || f && c, this.hasZoom = f || e, this.runChartClick = d && !!d.click, this.pinchDown = [], this.lastValidTouch = {}, S.Tooltip && b.tooltip.enabled && (a.tooltip = new Mb(a, b.tooltip), this.followTouchMove = b.tooltip.followTouchMove), this.setDOMEvents()
            },
            normalize: function(a, b) {
                var c, d, a = a || window.event,
                    a = Sb(a);
                return a.target || (a.target = a.srcElement), d = a.touches ? a.touches.length ? a.touches.item(0) : a.changedTouches[0] : a, b || (this.chartPosition = b = Rb(this.chart.container)), d.pageX === t ? (c = u(a.x, a.clientX - b.left), d = a.y) : (c = d.pageX - b.left, d = d.pageY - b.top), r(a, {
                    chartX: v(c),
                    chartY: v(d)
                })
            },
            getCoordinates: function(a) {
                var b = {
                    xAxis: [],
                    yAxis: []
                };
                return q(this.chart.axes, function(c) {
                    b[c.isXAxis ? "xAxis" : "yAxis"].push({
                        axis: c,
                        value: c.toValue(a[c.horiz ? "chartX" : "chartY"])
                    })
                }), b
            },
            getIndex: function(a) {
                var b = this.chart;
                return b.inverted ? b.plotHeight + b.plotTop - a.chartY : a.chartX - b.plotLeft
            },
            runPointActions: function(a) {
                var e, f, i, j, b = this.chart,
                    c = b.series,
                    d = b.tooltip,
                    g = b.hoverPoint,
                    h = b.hoverSeries,
                    k = b.chartWidth,
                    l = this.getIndex(a);
                if (d && this.options.tooltip.shared && (!h || !h.noSharedTooltip)) {
                    for (f = [], i = c.length, j = 0; i > j; j++) c[j].visible && c[j].options.enableMouseTracking !== !1 && !c[j].noSharedTooltip && c[j].singularTooltips !== !0 && c[j].tooltipPoints.length && (e = c[j].tooltipPoints[l]) && e.series && (e._dist = Q(l - e.clientX), k = C(k, e._dist), f.push(e));
                    for (i = f.length; i--;) f[i]._dist > k && f.splice(i, 1);
                    f.length && f[0].clientX !== this.hoverX && (d.refresh(f, a), this.hoverX = f[0].clientX)
                }
                c = h && h.tooltipOptions.followPointer, h && h.tracker && !c ? (e = h.tooltipPoints[l]) && e !== g && e.onMouseOver(a) : d && c && !d.isHidden && (h = d.getAnchor([{}], a), d.updatePosition({
                    plotX: h[0],
                    plotY: h[1]
                })), d && !this._onDocumentMouseMove && (this._onDocumentMouseMove = function(a) {
                    W[pa] && W[pa].pointer.onDocumentMouseMove(a)
                }, N(x, "mousemove", this._onDocumentMouseMove)), q(b.axes, function(b) {
                    b.drawCrosshair(a, p(e, g))
                })
            },
            reset: function(a) {
                var b = this.chart,
                    c = b.hoverSeries,
                    d = b.hoverPoint,
                    e = b.tooltip,
                    f = e && e.shared ? b.hoverPoints : d;
                (a = a && e && f) && ra(f)[0].plotX === t && (a = !1), a ? (e.refresh(f), d && d.setState(d.state, !0)) : (d && d.onMouseOut(), c && c.onMouseOut(), e && e.hide(), this._onDocumentMouseMove && (X(x, "mousemove", this._onDocumentMouseMove), this._onDocumentMouseMove = null), q(b.axes, function(a) {
                    a.hideCrosshair()
                }), this.hoverX = null)
            },
            scaleGroups: function(a, b) {
                var d, c = this.chart;
                q(c.series, function(e) {
                    d = a || e.getPlotBox(), e.xAxis && e.xAxis.zoomEnabled && (e.group.attr(d), e.markerGroup && (e.markerGroup.attr(d), e.markerGroup.clip(b ? c.clipRect : null)), e.dataLabelsGroup && e.dataLabelsGroup.attr(d))
                }), c.clipRect.attr(b || c.clipBox)
            },
            dragStart: function(a) {
                var b = this.chart;
                b.mouseIsDown = a.type, b.cancelClick = !1, b.mouseDownX = this.mouseDownX = a.chartX, b.mouseDownY = this.mouseDownY = a.chartY
            },
            drag: function(a) {
                var l, b = this.chart,
                    c = b.options.chart,
                    d = a.chartX,
                    e = a.chartY,
                    f = this.zoomHor,
                    g = this.zoomVert,
                    h = b.plotLeft,
                    i = b.plotTop,
                    j = b.plotWidth,
                    k = b.plotHeight,
                    m = this.mouseDownX,
                    n = this.mouseDownY,
                    o = c.panKey && a[c.panKey + "Key"];
                h > d ? d = h : d > h + j && (d = h + j), i > e ? e = i : e > i + k && (e = i + k), this.hasDragged = Math.sqrt(Math.pow(m - d, 2) + Math.pow(n - e, 2)), this.hasDragged > 10 && (l = b.isInsidePlot(m - h, n - i), b.hasCartesianSeries && (this.zoomX || this.zoomY) && l && !o && !this.selectionMarker && (this.selectionMarker = b.renderer.rect(h, i, f ? 1 : j, g ? 1 : k, 0).attr({
                    fill: c.selectionMarkerFill || "rgba(69,114,167,0.25)",
                    zIndex: 7
                }).add()), this.selectionMarker && f && (d -= m, this.selectionMarker.attr({
                    width: Q(d),
                    x: (d > 0 ? 0 : d) + m
                })), this.selectionMarker && g && (d = e - n, this.selectionMarker.attr({
                    height: Q(d),
                    y: (d > 0 ? 0 : d) + n
                })), l && !this.selectionMarker && c.panning && b.pan(a, c.panning))
            },
            drop: function(a) {
                var b = this.chart,
                    c = this.hasPinched;
                if (this.selectionMarker) {
                    var j, d = {
                            xAxis: [],
                            yAxis: [],
                            originalEvent: a.originalEvent || a
                        },
                        e = this.selectionMarker,
                        f = e.attr ? e.attr("x") : e.x,
                        g = e.attr ? e.attr("y") : e.y,
                        h = e.attr ? e.attr("width") : e.width,
                        i = e.attr ? e.attr("height") : e.height;
                    (this.hasDragged || c) && (q(b.axes, function(b) {
                        if (b.zoomEnabled) {
                            var c = b.horiz,
                                e = "touchend" === a.type ? b.minPixelPadding : 0,
                                n = b.toValue((c ? f : g) + e),
                                c = b.toValue((c ? f + h : g + i) - e);
                            !isNaN(n) && !isNaN(c) && (d[b.coll].push({
                                axis: b,
                                min: C(n, c),
                                max: u(n, c)
                            }), j = !0)
                        }
                    }), j && K(b, "selection", d, function(a) {
                        b.zoom(r(a, c ? {
                            animation: !1
                        } : null))
                    })), this.selectionMarker = this.selectionMarker.destroy(), c && this.scaleGroups()
                }
                b && (A(b.container, {
                    cursor: b._cursor
                }), b.cancelClick = this.hasDragged > 10, b.mouseIsDown = this.hasDragged = this.hasPinched = !1, this.pinchDown = [])
            },
            onContainerMouseDown: function(a) {
                a = this.normalize(a), a.preventDefault && a.preventDefault(), this.dragStart(a)
            },
            onDocumentMouseUp: function(a) {
                W[pa] && W[pa].pointer.drop(a)
            },
            onDocumentMouseMove: function(a) {
                var b = this.chart,
                    c = this.chartPosition,
                    d = b.hoverSeries,
                    a = this.normalize(a, c);
                c && d && !this.inClass(a.target, "highcharts-tracker") && !b.isInsidePlot(a.chartX - b.plotLeft, a.chartY - b.plotTop) && this.reset()
            },
            onContainerMouseLeave: function() {
                var a = W[pa];
                a && (a.pointer.reset(), a.pointer.chartPosition = null)
            },
            onContainerMouseMove: function(a) {
                var b = this.chart;
                pa = b.index, a = this.normalize(a), a.returnValue = !1, "mousedown" === b.mouseIsDown && this.drag(a), (this.inClass(a.target, "highcharts-tracker") || b.isInsidePlot(a.chartX - b.plotLeft, a.chartY - b.plotTop)) && !b.openMenu && this.runPointActions(a)
            },
            inClass: function(a, b) {
                for (var c; a;) {
                    if (c = F(a, "class")) {
                        if (-1 !== c.indexOf(b)) return !0;
                        if (-1 !== c.indexOf("highcharts-container")) return !1
                    }
                    a = a.parentNode
                }
            },
            onTrackerMouseOut: function(a) {
                var b = this.chart.hoverSeries,
                    c = (a = a.relatedTarget || a.toElement) && a.point && a.point.series;
                !b || b.options.stickyTracking || this.inClass(a, "highcharts-tooltip") || c === b || b.onMouseOut()
            },
            onContainerClick: function(a) {
                var b = this.chart,
                    c = b.hoverPoint,
                    d = b.plotLeft,
                    e = b.plotTop,
                    a = this.normalize(a);
                a.cancelBubble = !0, b.cancelClick || (c && this.inClass(a.target, "highcharts-tracker") ? (K(c.series, "click", r(a, {
                    point: c
                })), b.hoverPoint && c.firePointEvent("click", a)) : (r(a, this.getCoordinates(a)), b.isInsidePlot(a.chartX - d, a.chartY - e) && K(b, "click", a)))
            },
            setDOMEvents: function() {
                var a = this,
                    b = a.chart.container;
                b.onmousedown = function(b) {
                    a.onContainerMouseDown(b)
                }, b.onmousemove = function(b) {
                    a.onContainerMouseMove(b)
                }, b.onclick = function(b) {
                    a.onContainerClick(b)
                }, N(b, "mouseleave", a.onContainerMouseLeave), 1 === $a && N(x, "mouseup", a.onDocumentMouseUp), Za && (b.ontouchstart = function(b) {
                    a.onContainerTouchStart(b)
                }, b.ontouchmove = function(b) {
                    a.onContainerTouchMove(b)
                }, 1 === $a && N(x, "touchend", a.onDocumentTouchEnd))
            },
            destroy: function() {
                var a;
                X(this.chart.container, "mouseleave", this.onContainerMouseLeave), $a || (X(x, "mouseup", this.onDocumentMouseUp), X(x, "touchend", this.onDocumentTouchEnd)), clearInterval(this.tooltipTimeout);
                for (a in this) this[a] = null
            }
        }, r(S.Pointer.prototype, {
            pinchTranslate: function(a, b, c, d, e, f) {
                (this.zoomHor || this.pinchHor) && this.pinchTranslateDirection(!0, a, b, c, d, e, f), (this.zoomVert || this.pinchVert) && this.pinchTranslateDirection(!1, a, b, c, d, e, f)
            },
            pinchTranslateDirection: function(a, b, c, d, e, f, g, h) {
                var o, p, x, i = this.chart,
                    j = a ? "x" : "y",
                    k = a ? "X" : "Y",
                    l = "chart" + k,
                    m = a ? "width" : "height",
                    n = i["plot" + (a ? "Left" : "Top")],
                    q = h || 1,
                    r = i.inverted,
                    D = i.bounds[a ? "h" : "v"],
                    u = 1 === b.length,
                    s = b[0][l],
                    v = c[0][l],
                    t = !u && b[1][l],
                    w = !u && c[1][l],
                    c = function() {
                        !u && Q(s - t) > 20 && (q = h || Q(v - w) / Q(s - t)), p = (n - v) / q + s, o = i["plot" + (a ? "Width" : "Height")] / q
                    };
                c(), b = p, b < D.min ? (b = D.min, x = !0) : b + o > D.max && (b = D.max - o, x = !0), x ? (v -= .8 * (v - g[j][0]), u || (w -= .8 * (w - g[j][1])), c()) : g[j] = [v, w], r || (f[j] = p - n, f[m] = o), f = r ? 1 / q : q, e[m] = o, e[j] = b, d[r ? a ? "scaleY" : "scaleX" : "scale" + k] = q, d["translate" + k] = f * n + (v - f * s)
            },
            pinch: function(a) {
                var b = this,
                    c = b.chart,
                    d = b.pinchDown,
                    e = b.followTouchMove,
                    f = a.touches,
                    g = f.length,
                    h = b.lastValidTouch,
                    i = b.hasZoom,
                    j = b.selectionMarker,
                    k = {},
                    l = 1 === g && (b.inClass(a.target, "highcharts-tracker") && c.runTrackerClick || c.runChartClick),
                    m = {};
                (i || e) && !l && a.preventDefault(), Ua(f, function(a) {
                    return b.normalize(a)
                }), "touchstart" === a.type ? (q(f, function(a, b) {
                    d[b] = {
                        chartX: a.chartX,
                        chartY: a.chartY
                    }
                }), h.x = [d[0].chartX, d[1] && d[1].chartX], h.y = [d[0].chartY, d[1] && d[1].chartY], q(c.axes, function(a) {
                    if (a.zoomEnabled) {
                        var b = c.bounds[a.horiz ? "h" : "v"],
                            d = a.minPixelPadding,
                            e = a.toPixels(p(a.options.min, a.dataMin)),
                            f = a.toPixels(p(a.options.max, a.dataMax)),
                            g = C(e, f),
                            e = u(e, f);
                        b.min = C(a.pos, g - d), b.max = u(a.pos + a.len, e + d)
                    }
                })) : d.length && (j || (b.selectionMarker = j = r({
                    destroy: sa
                }, c.plotBox)), b.pinchTranslate(d, f, k, j, m, h), b.hasPinched = i, b.scaleGroups(k, m), !i && e && 1 === g && this.runPointActions(b.normalize(a)))
            },
            onContainerTouchStart: function(a) {
                var b = this.chart;
                pa = b.index, 1 === a.touches.length ? (a = this.normalize(a), b.isInsidePlot(a.chartX - b.plotLeft, a.chartY - b.plotTop) ? (this.runPointActions(a), this.pinch(a)) : this.reset()) : 2 === a.touches.length && this.pinch(a)
            },
            onContainerTouchMove: function(a) {
                (1 === a.touches.length || 2 === a.touches.length) && this.pinch(a)
            },
            onDocumentTouchEnd: function(a) {
                W[pa] && W[pa].pointer.drop(a)
            }
        }), H.PointerEvent || H.MSPointerEvent) {
            var ua = {},
                yb = !!H.PointerEvent,
                Wb = function() {
                    var a, b = [];
                    b.item = function(a) {
                        return this[a]
                    };
                    for (a in ua) ua.hasOwnProperty(a) && b.push({
                        pageX: ua[a].pageX,
                        pageY: ua[a].pageY,
                        target: ua[a].target
                    });
                    return b
                },
                zb = function(a, b, c, d) {
                    a = a.originalEvent || a, "touch" !== a.pointerType && a.pointerType !== a.MSPOINTER_TYPE_TOUCH || !W[pa] || (d(a), d = W[pa].pointer, d[b]({
                        type: c,
                        target: a.currentTarget,
                        preventDefault: sa,
                        touches: Wb()
                    }))
                };
            r(Va.prototype, {
                onContainerPointerDown: function(a) {
                    zb(a, "onContainerTouchStart", "touchstart", function(a) {
                        ua[a.pointerId] = {
                            pageX: a.pageX,
                            pageY: a.pageY,
                            target: a.currentTarget
                        }
                    })
                },
                onContainerPointerMove: function(a) {
                    zb(a, "onContainerTouchMove", "touchmove", function(a) {
                        ua[a.pointerId] = {
                            pageX: a.pageX,
                            pageY: a.pageY
                        }, ua[a.pointerId].target || (ua[a.pointerId].target = a.currentTarget)
                    })
                },
                onDocumentPointerUp: function(a) {
                    zb(a, "onContainerTouchEnd", "touchend", function(a) {
                        delete ua[a.pointerId]
                    })
                },
                batchMSEvents: function(a) {
                    a(this.chart.container, yb ? "pointerdown" : "MSPointerDown", this.onContainerPointerDown), a(this.chart.container, yb ? "pointermove" : "MSPointerMove", this.onContainerPointerMove), a(x, yb ? "pointerup" : "MSPointerUp", this.onDocumentPointerUp)
                }
            }), Ma(Va.prototype, "init", function(a, b, c) {
                a.call(this, b, c), (this.hasZoom || this.followTouchMove) && A(b.container, {
                    "-ms-touch-action": P,
                    "touch-action": P
                })
            }), Ma(Va.prototype, "setDOMEvents", function(a) {
                a.apply(this), (this.hasZoom || this.followTouchMove) && this.batchMSEvents(N)
            }), Ma(Va.prototype, "destroy", function(a) {
                this.batchMSEvents(X), a.call(this)
            })
        }
        var kb = S.Legend = function(a, b) {
            this.init(a, b)
        };
        kb.prototype = {
            init: function(a, b) {
                var c = this,
                    d = b.itemStyle,
                    e = p(b.padding, 8),
                    f = b.itemMarginTop || 0;
                this.options = b, b.enabled && (c.itemStyle = d, c.itemHiddenStyle = w(d, b.itemHiddenStyle), c.itemMarginTop = f, c.padding = e, c.initialItemX = e, c.initialItemY = e - 5, c.maxItemWidth = 0, c.chart = a, c.itemHeight = 0, c.lastLineHeight = 0, c.symbolWidth = p(b.symbolWidth, 16), c.pages = [], c.render(), N(c.chart, "endResize", function() {
                    c.positionCheckboxes()
                }))
            },
            colorizeItem: function(a, b) {
                var j, c = this.options,
                    d = a.legendItem,
                    e = a.legendLine,
                    f = a.legendSymbol,
                    g = this.itemHiddenStyle.color,
                    c = b ? c.itemStyle.color : g,
                    h = b ? a.legendColor || a.color || "#CCC" : g,
                    g = a.options && a.options.marker,
                    i = {
                        fill: h
                    };
                if (d && d.css({
                    fill: c,
                    color: c
                }), e && e.attr({
                    stroke: h
                }), f) {
                    if (g && f.isMarker)
                        for (j in i.stroke = h, g = a.convertAttribs(g)) d = g[j], d !== t && (i[j] = d);
                    f.attr(i)
                }
            },
            positionItem: function(a) {
                var b = this.options,
                    c = b.symbolPadding,
                    b = !b.rtl,
                    d = a._legendItemPos,
                    e = d[0],
                    d = d[1],
                    f = a.checkbox;
                a.legendGroup && a.legendGroup.translate(b ? e : this.legendWidth - e - 2 * c - 4, d), f && (f.x = e, f.y = d)
            },
            destroyItem: function(a) {
                var b = a.checkbox;
                q(["legendItem", "legendLine", "legendSymbol", "legendGroup"], function(b) {
                    a[b] && (a[b] = a[b].destroy())
                }), b && Pa(a.checkbox)
            },
            destroy: function() {
                var a = this.group,
                    b = this.box;
                b && (this.box = b.destroy()), a && (this.group = a.destroy())
            },
            positionCheckboxes: function(a) {
                var c, b = this.group.alignAttr,
                    d = this.clipHeight || this.legendHeight;
                b && (c = b.translateY, q(this.allItems, function(e) {
                    var g, f = e.checkbox;
                    f && (g = c + f.y + (a || 0) + 3, A(f, {
                        left: b.translateX + e.checkboxOffset + f.x - 20 + "px",
                        top: g + "px",
                        display: g > c - 6 && c + d - 6 > g ? "" : P
                    }))
                }))
            },
            renderTitle: function() {
                var a = this.padding,
                    b = this.options.title,
                    c = 0;
                b.text && (this.title || (this.title = this.chart.renderer.label(b.text, a - 3, a - 4, null, null, null, null, null, "legend-title").attr({
                    zIndex: 1
                }).css(b.style).add(this.group)), a = this.title.getBBox(), c = a.height, this.offsetWidth = a.width, this.contentGroup.attr({
                    translateY: c
                })), this.titleHeight = c
            },
            renderItem: function(a) {
                var b = this.chart,
                    c = b.renderer,
                    d = this.options,
                    e = "horizontal" === d.layout,
                    f = this.symbolWidth,
                    g = d.symbolPadding,
                    h = this.itemStyle,
                    i = this.itemHiddenStyle,
                    j = this.padding,
                    k = e ? p(d.itemDistance, 20) : 0,
                    l = !d.rtl,
                    m = d.width,
                    n = d.itemMarginBottom || 0,
                    o = this.itemMarginTop,
                    q = this.initialItemX,
                    r = a.legendItem,
                    s = a.series && a.series.drawLegendSymbol ? a.series : a,
                    D = s.options,
                    D = this.createCheckboxForItem && D && D.showCheckbox,
                    t = d.useHTML;
                r || (a.legendGroup = c.g("legend-item").attr({
                    zIndex: 1
                }).add(this.scrollGroup), a.legendItem = r = c.text(d.labelFormat ? Ia(d.labelFormat, a) : d.labelFormatter.call(a), l ? f + g : -g, this.baseline || 0, t).css(w(a.visible ? h : i)).attr({
                    align: l ? "left" : "right",
                    zIndex: 2
                }).add(a.legendGroup), this.baseline || (this.baseline = c.fontMetrics(h.fontSize, r).f + 3 + o, r.attr("y", this.baseline)), s.drawLegendSymbol(this, a), this.setItemEvents && this.setItemEvents(a, r, t, h, i), this.colorizeItem(a, a.visible), D && this.createCheckboxForItem(a)), c = r.getBBox(), f = a.checkboxOffset = d.itemWidth || a.legendItemWidth || f + g + c.width + k + (D ? 20 : 0), this.itemHeight = g = v(a.legendItemHeight || c.height), e && this.itemX - q + f > (m || b.chartWidth - 2 * j - q - d.x) && (this.itemX = q, this.itemY += o + this.lastLineHeight + n, this.lastLineHeight = 0), this.maxItemWidth = u(this.maxItemWidth, f), this.lastItemY = o + this.itemY + n, this.lastLineHeight = u(g, this.lastLineHeight), a._legendItemPos = [this.itemX, this.itemY], e ? this.itemX += f : (this.itemY += o + g + n, this.lastLineHeight = g), this.offsetWidth = m || u((e ? this.itemX - q - k : f) + j, this.offsetWidth)
            },
            getAllItems: function() {
                var a = [];
                return q(this.chart.series, function(b) {
                    var c = b.options;
                    p(c.showInLegend, s(c.linkedTo) ? !1 : t, !0) && (a = a.concat(b.legendItems || ("point" === c.legendType ? b.data : b)))
                }), a
            },
            render: function() {
                var e, f, g, h, a = this,
                    b = a.chart,
                    c = b.renderer,
                    d = a.group,
                    i = a.box,
                    j = a.options,
                    k = a.padding,
                    l = j.borderWidth,
                    m = j.backgroundColor;
                a.itemX = a.initialItemX, a.itemY = a.initialItemY, a.offsetWidth = 0, a.lastItemY = 0, d || (a.group = d = c.g("legend").attr({
                    zIndex: 7
                }).add(), a.contentGroup = c.g().attr({
                    zIndex: 1
                }).add(d), a.scrollGroup = c.g().add(a.contentGroup)), a.renderTitle(), e = a.getAllItems(), nb(e, function(a, b) {
                    return (a.options && a.options.legendIndex || 0) - (b.options && b.options.legendIndex || 0)
                }), j.reversed && e.reverse(), a.allItems = e, a.display = f = !!e.length, q(e, function(b) {
                    a.renderItem(b)
                }), g = j.width || a.offsetWidth, h = a.lastItemY + a.lastLineHeight + a.titleHeight, h = a.handleOverflow(h), (l || m) && (g += k, h += k, i ? g > 0 && h > 0 && (i[i.isNew ? "attr" : "animate"](i.crisp({
                    width: g,
                    height: h
                })), i.isNew = !1) : (a.box = i = c.rect(0, 0, g, h, j.borderRadius, l || 0).attr({
                    stroke: j.borderColor,
                    "stroke-width": l || 0,
                    fill: m || P
                }).add(d).shadow(j.shadow), i.isNew = !0), i[f ? "show" : "hide"]()), a.legendWidth = g, a.legendHeight = h, q(e, function(b) {
                    a.positionItem(b)
                }), f && d.align(r({
                    width: g,
                    height: h
                }, j), !0, "spacingBox"), b.isResizing || this.positionCheckboxes()
            },
            handleOverflow: function(a) {
                var h, o, b = this,
                    c = this.chart,
                    d = c.renderer,
                    e = this.options,
                    f = e.y,
                    f = c.spacingBox.height + ("top" === e.verticalAlign ? -f : f) - this.padding,
                    g = e.maxHeight,
                    i = this.clipRect,
                    j = e.navigation,
                    k = p(j.animation, !0),
                    l = j.arrowSize || 12,
                    m = this.nav,
                    n = this.pages,
                    r = this.allItems;
                return "horizontal" === e.layout && (f /= 2), g && (f = C(f, g)), n.length = 0, a > f && !e.useHTML ? (this.clipHeight = h = u(f - 20 - this.titleHeight - this.padding, 0), this.currentPage = p(this.currentPage, 1), this.fullHeight = a, q(r, function(a, b) {
                    var c = a._legendItemPos[1],
                        d = v(a.legendItem.getBBox().height),
                        e = n.length;
                    (!e || c - n[e - 1] > h && (o || c) !== n[e - 1]) && (n.push(o || c), e++), b === r.length - 1 && c + d - n[e - 1] > h && n.push(c), c !== o && (o = c)
                }), i || (i = b.clipRect = d.clipRect(0, this.padding, 9999, 0), b.contentGroup.clip(i)), i.attr({
                    height: h
                }), m || (this.nav = m = d.g().attr({
                    zIndex: 1
                }).add(this.group), this.up = d.symbol("triangle", 0, 0, l, l).on("click", function() {
                    b.scroll(-1, k)
                }).add(m), this.pager = d.text("", 15, 10).css(j.style).add(m), this.down = d.symbol("triangle-down", 0, 0, l, l).on("click", function() {
                    b.scroll(1, k)
                }).add(m)), b.scroll(0), a = f) : m && (i.attr({
                    height: c.chartHeight
                }), m.hide(), this.scrollGroup.attr({
                    translateY: 1
                }), this.clipHeight = 0), a
            },
            scroll: function(a, b) {
                var c = this.pages,
                    d = c.length,
                    e = this.currentPage + a,
                    f = this.clipHeight,
                    g = this.options.navigation,
                    h = g.activeColor,
                    g = g.inactiveColor,
                    i = this.pager,
                    j = this.padding;
                e > d && (e = d), e > 0 && (b !== t && Qa(b, this.chart), this.nav.attr({
                    translateX: j,
                    translateY: f + this.padding + 7 + this.titleHeight,
                    visibility: "visible"
                }), this.up.attr({
                    fill: 1 === e ? g : h
                }).css({
                    cursor: 1 === e ? "default" : "pointer"
                }), i.attr({
                    text: e + "/" + d
                }), this.down.attr({
                    x: 18 + this.pager.getBBox().width,
                    fill: e === d ? g : h
                }).css({
                    cursor: e === d ? "default" : "pointer"
                }), c = -c[e - 1] + this.initialItemY, this.scrollGroup.animate({
                    translateY: c
                }), this.currentPage = e, this.positionCheckboxes(c))
            }
        }, M = S.LegendSymbolMixin = {
            drawRectangle: function(a, b) {
                var c = a.options.symbolHeight || 12;
                b.legendSymbol = this.chart.renderer.rect(0, a.baseline - 5 - c / 2, a.symbolWidth, c, a.options.symbolRadius || 0).attr({
                    zIndex: 3
                }).add(b.legendGroup)
            },
            drawLineMarker: function(a) {
                var d, b = this.options,
                    c = b.marker;
                d = a.symbolWidth;
                var g, e = this.chart.renderer,
                    f = this.legendGroup,
                    a = a.baseline - v(.3 * e.fontMetrics(a.options.itemStyle.fontSize, this.legendItem).b);
                b.lineWidth && (g = {
                    "stroke-width": b.lineWidth
                }, b.dashStyle && (g.dashstyle = b.dashStyle), this.legendLine = e.path(["M", 0, a, "L", d, a]).attr(g).add(f)), c && c.enabled !== !1 && (b = c.radius, this.legendSymbol = d = e.symbol(this.symbol, d / 2 - b, a - b, 2 * b, 2 * b).add(f), d.isMarker = !0)
            }
        }, (/Trident\/7\.0/.test(wa) || Ta) && Ma(kb.prototype, "positionItem", function(a, b) {
            var c = this,
                d = function() {
                    b._legendItemPos && a.call(c, b)
                };
            d(), setTimeout(d)
        }), Xa.prototype = {
            init: function(a, b) {
                var c, d = a.series;
                a.series = null, c = w(L, a), c.series = a.series = d, this.userOptions = a, d = c.chart, this.margin = this.splashArray("margin", d), this.spacing = this.splashArray("spacing", d);
                var e = d.events;
                this.bounds = {
                    h: {},
                    v: {}
                }, this.callback = b, this.isResizing = 0, this.options = c, this.axes = [], this.series = [], this.hasCartesianSeries = d.showAxes;
                var g, f = this;
                if (f.index = W.length, W.push(f), $a++, d.reflow !== !1 && N(f, "load", function() {
                    f.initReflow()
                }), e)
                    for (g in e) N(f, g, e[g]);
                f.xAxis = [], f.yAxis = [], f.animation = ga ? !1 : p(d.animation, !0), f.pointCount = f.colorCounter = f.symbolCounter = 0, f.firstRender()
            },
            initSeries: function(a) {
                var b = this.options.chart;
                return (b = J[a.type || b.type || b.defaultSeriesType]) || oa(17, !0), b = new b, b.init(this, a), b
            },
            isInsidePlot: function(a, b, c) {
                var d = c ? b : a,
                    a = c ? a : b;
                return d >= 0 && d <= this.plotWidth && a >= 0 && a <= this.plotHeight
            },
            adjustTickAmounts: function() {
                this.options.chart.alignTicks !== !1 && q(this.axes, function(a) {
                    a.adjustTickAmount()
                }), this.maxTicks = null
            },
            redraw: function(a) {
                var g, h, b = this.axes,
                    c = this.series,
                    d = this.pointer,
                    e = this.legend,
                    f = this.isDirtyLegend,
                    i = this.hasCartesianSeries,
                    j = this.isDirtyBox,
                    k = c.length,
                    l = k,
                    m = this.renderer,
                    n = m.isHidden(),
                    o = [];
                for (Qa(a, this), n && this.cloneRenderTo(), this.layOutTitles(); l--;)
                    if (a = c[l], a.options.stacking && (g = !0, a.isDirty)) {
                        h = !0;
                        break
                    }
                if (h)
                    for (l = k; l--;) a = c[l], a.options.stacking && (a.isDirty = !0);
                q(c, function(a) {
                    a.isDirty && "point" === a.options.legendType && (f = !0)
                }), f && e.options.enabled && (e.render(), this.isDirtyLegend = !1), g && this.getStacks(), i && (this.isResizing || (this.maxTicks = null, q(b, function(a) {
                    a.setScale()
                })), this.adjustTickAmounts()), this.getMargins(), i && (q(b, function(a) {
                    a.isDirty && (j = !0)
                }), q(b, function(a) {
                    a.isDirtyExtremes && (a.isDirtyExtremes = !1, o.push(function() {
                        K(a, "afterSetExtremes", r(a.eventArgs, a.getExtremes())), delete a.eventArgs
                    })), (j || g) && a.redraw()
                })), j && this.drawChartBox(), q(c, function(a) {
                    a.isDirty && a.visible && (!a.isCartesian || a.xAxis) && a.redraw()
                }), d && d.reset(!0), m.draw(), K(this, "redraw"), n && this.cloneRenderTo(!0), q(o, function(a) {
                    a.call()
                })
            },
            get: function(a) {
                var d, e, b = this.axes,
                    c = this.series;
                for (d = 0; d < b.length; d++)
                    if (b[d].options.id === a) return b[d];
                for (d = 0; d < c.length; d++)
                    if (c[d].options.id === a) return c[d];
                for (d = 0; d < c.length; d++)
                    for (e = c[d].points || [], b = 0; b < e.length; b++)
                        if (e[b].id === a) return e[b];
                return null
            },
            getAxes: function() {
                var a = this,
                    b = this.options,
                    c = b.xAxis = ra(b.xAxis || {}),
                    b = b.yAxis = ra(b.yAxis || {});
                q(c, function(a, b) {
                    a.index = b, a.isX = !0
                }), q(b, function(a, b) {
                    a.index = b
                }), c = c.concat(b), q(c, function(b) {
                    new ma(a, b)
                }), a.adjustTickAmounts()
            },
            getSelectedPoints: function() {
                var a = [];
                return q(this.series, function(b) {
                    a = a.concat(vb(b.points || [], function(a) {
                        return a.selected
                    }))
                }), a
            },
            getSelectedSeries: function() {
                return vb(this.series, function(a) {
                    return a.selected
                })
            },
            getStacks: function() {
                var a = this;
                q(a.yAxis, function(a) {
                    a.stacks && a.hasVisibleSeries && (a.oldStacks = a.stacks)
                }), q(a.series, function(b) {
                    !b.options.stacking || b.visible !== !0 && a.options.chart.ignoreHiddenSeries !== !1 || (b.stackKey = b.type + p(b.options.stack, ""))
                })
            },
            setTitle: function(a, b, c) {
                var g, f, d = this,
                    e = d.options;
                f = e.title = w(e.title, a), g = e.subtitle = w(e.subtitle, b), e = g, q([
                    ["title", a, f],
                    ["subtitle", b, e]
                ], function(a) {
                    var b = a[0],
                        c = d[b],
                        e = a[1],
                        a = a[2];
                    c && e && (d[b] = c = c.destroy()), a && a.text && !c && (d[b] = d.renderer.text(a.text, 0, 0, a.useHTML).attr({
                        align: a.align,
                        "class": "highcharts-" + b,
                        zIndex: a.zIndex || 4
                    }).css(a.style).add())
                }), d.layOutTitles(c)
            },
            layOutTitles: function(a) {
                var b = 0,
                    c = this.title,
                    d = this.subtitle,
                    e = this.options,
                    f = e.title,
                    e = e.subtitle,
                    g = this.renderer,
                    h = this.spacingBox.width - 44;
                !c || (c.css({
                    width: (f.width || h) + "px"
                }).align(r({
                    y: g.fontMetrics(f.style.fontSize, c).b - 3
                }, f), !1, "spacingBox"), f.floating || f.verticalAlign) || (b = c.getBBox().height), d && (d.css({
                    width: (e.width || h) + "px"
                }).align(r({
                    y: b + (f.margin - 13) + g.fontMetrics(f.style.fontSize, d).b
                }, e), !1, "spacingBox"), !e.floating && !e.verticalAlign && (b = Ka(b + d.getBBox().height))), c = this.titleOffset !== b, this.titleOffset = b, !this.isDirtyBox && c && (this.isDirtyBox = c, this.hasRendered && p(a, !0) && this.isDirtyBox && this.redraw())
            },
            getChartSize: function() {
                var a = this.options.chart,
                    b = a.width,
                    a = a.height,
                    c = this.renderToClone || this.renderTo;
                s(b) || (this.containerWidth = hb(c, "width")), s(a) || (this.containerHeight = hb(c, "height")), this.chartWidth = u(0, b || this.containerWidth || 600), this.chartHeight = u(0, p(a, this.containerHeight > 19 ? this.containerHeight : 400))
            },
            cloneRenderTo: function(a) {
                var b = this.renderToClone,
                    c = this.container;
                a ? b && (this.renderTo.appendChild(c), Pa(b), delete this.renderToClone) : (c && c.parentNode === this.renderTo && this.renderTo.removeChild(c), this.renderToClone = b = this.renderTo.cloneNode(0), A(b, {
                    position: "absolute",
                    top: "-9999px",
                    display: "block"
                }), b.style.setProperty && b.style.setProperty("display", "block", "important"), x.body.appendChild(b), c && b.appendChild(c))
            },
            getContainer: function() {
                var a, c, d, e, b = this.options.chart;
                this.renderTo = a = b.renderTo, e = "highcharts-" + tb++, Fa(a) && (this.renderTo = a = x.getElementById(a)), a || oa(13, !0), c = z(F(a, "data-highcharts-chart")), !isNaN(c) && W[c] && W[c].hasRendered && W[c].destroy(), F(a, "data-highcharts-chart", this.index), a.innerHTML = "", !b.skipClone && !a.offsetWidth && this.cloneRenderTo(), this.getChartSize(), c = this.chartWidth, d = this.chartHeight, this.container = a = $(Ja, {
                    className: "highcharts-container" + (b.className ? " " + b.className : ""),
                    id: e
                }, r({
                    position: "relative",
                    overflow: "hidden",
                    width: c + "px",
                    height: d + "px",
                    textAlign: "left",
                    lineHeight: "normal",
                    zIndex: 0,
                    "-webkit-tap-highlight-color": "rgba(0,0,0,0)"
                }, b.style), this.renderToClone || a), this._cursor = a.style.cursor, this.renderer = b.forExport ? new ta(a, c, d, b.style, !0) : new Ya(a, c, d, b.style), ga && this.renderer.create(this, a, c, d)
            },
            getMargins: function() {
                var b, a = this.spacing,
                    c = this.legend,
                    d = this.margin,
                    e = this.options.legend,
                    f = p(e.margin, 20),
                    g = e.x,
                    h = e.y,
                    i = e.align,
                    j = e.verticalAlign,
                    k = this.titleOffset;
                this.resetMargins(), b = this.axisOffset, k && !s(d[0]) && (this.plotTop = u(this.plotTop, k + this.options.title.margin + a[0])), c.display && !e.floating && ("right" === i ? s(d[1]) || (this.marginRight = u(this.marginRight, c.legendWidth - g + f + a[1])) : "left" === i ? s(d[3]) || (this.plotLeft = u(this.plotLeft, c.legendWidth + g + f + a[3])) : "top" === j ? s(d[0]) || (this.plotTop = u(this.plotTop, c.legendHeight + h + f + a[0])) : "bottom" !== j || s(d[2]) || (this.marginBottom = u(this.marginBottom, c.legendHeight - h + f + a[2]))), this.extraBottomMargin && (this.marginBottom += this.extraBottomMargin), this.extraTopMargin && (this.plotTop += this.extraTopMargin), this.hasCartesianSeries && q(this.axes, function(a) {
                    a.getOffset()
                }), s(d[3]) || (this.plotLeft += b[3]), s(d[0]) || (this.plotTop += b[0]), s(d[2]) || (this.marginBottom += b[2]), s(d[1]) || (this.marginRight += b[1]), this.setChartSize()
            },
            reflow: function(a) {
                var b = this,
                    c = b.options.chart,
                    d = b.renderTo,
                    e = c.width || hb(d, "width"),
                    f = c.height || hb(d, "height"),
                    c = a ? a.target : H,
                    d = function() {
                        b.container && (b.setSize(e, f, !1), b.hasUserSize = null)
                    };
                b.hasUserSize || !e || !f || c !== H && c !== x || ((e !== b.containerWidth || f !== b.containerHeight) && (clearTimeout(b.reflowTimeout), a ? b.reflowTimeout = setTimeout(d, 100) : d()), b.containerWidth = e, b.containerHeight = f)
            },
            initReflow: function() {
                var a = this,
                    b = function(b) {
                        a.reflow(b)
                    };
                N(H, "resize", b), N(a, "destroy", function() {
                    X(H, "resize", b)
                })
            },
            setSize: function(a, b, c) {
                var e, f, g, d = this;
                d.isResizing += 1, g = function() {
                    d && K(d, "endResize", null, function() {
                        d.isResizing -= 1
                    })
                }, Qa(c, d), d.oldChartHeight = d.chartHeight, d.oldChartWidth = d.chartWidth, s(a) && (d.chartWidth = e = u(0, v(a)), d.hasUserSize = !!e), s(b) && (d.chartHeight = f = u(0, v(b))), (va ? ib : A)(d.container, {
                    width: e + "px",
                    height: f + "px"
                }, va), d.setChartSize(!0), d.renderer.setSize(e, f, c), d.maxTicks = null, q(d.axes, function(a) {
                    a.isDirty = !0, a.setScale()
                }), q(d.series, function(a) {
                    a.isDirty = !0
                }), d.isDirtyLegend = !0, d.isDirtyBox = !0, d.layOutTitles(), d.getMargins(), d.redraw(c), d.oldChartHeight = null, K(d, "resize"), va === !1 ? g() : setTimeout(g, va && va.duration || 500)
            },
            setChartSize: function(a) {
                var i, j, k, l, b = this.inverted,
                    c = this.renderer,
                    d = this.chartWidth,
                    e = this.chartHeight,
                    f = this.options.chart,
                    g = this.spacing,
                    h = this.clipOffset;
                this.plotLeft = i = v(this.plotLeft), this.plotTop = j = v(this.plotTop), this.plotWidth = k = u(0, v(d - i - this.marginRight)), this.plotHeight = l = u(0, v(e - j - this.marginBottom)), this.plotSizeX = b ? l : k, this.plotSizeY = b ? k : l, this.plotBorderWidth = f.plotBorderWidth || 0, this.spacingBox = c.spacingBox = {
                    x: g[3],
                    y: g[0],
                    width: d - g[3] - g[1],
                    height: e - g[0] - g[2]
                }, this.plotBox = c.plotBox = {
                    x: i,
                    y: j,
                    width: k,
                    height: l
                }, d = 2 * U(this.plotBorderWidth / 2), b = Ka(u(d, h[3]) / 2), c = Ka(u(d, h[0]) / 2), this.clipBox = {
                    x: b,
                    y: c,
                    width: U(this.plotSizeX - u(d, h[1]) / 2 - b),
                    height: u(0, U(this.plotSizeY - u(d, h[2]) / 2 - c))
                }, a || q(this.axes, function(a) {
                    a.setAxisSize(), a.setAxisTranslation()
                })
            },
            resetMargins: function() {
                var a = this.spacing,
                    b = this.margin;
                this.plotTop = p(b[0], a[0]), this.marginRight = p(b[1], a[1]), this.marginBottom = p(b[2], a[2]), this.plotLeft = p(b[3], a[3]), this.axisOffset = [0, 0, 0, 0], this.clipOffset = [0, 0, 0, 0]
            },
            drawChartBox: function() {
                var n, a = this.options.chart,
                    b = this.renderer,
                    c = this.chartWidth,
                    d = this.chartHeight,
                    e = this.chartBackground,
                    f = this.plotBackground,
                    g = this.plotBorder,
                    h = this.plotBGImage,
                    i = a.borderWidth || 0,
                    j = a.backgroundColor,
                    k = a.plotBackgroundColor,
                    l = a.plotBackgroundImage,
                    m = a.plotBorderWidth || 0,
                    o = this.plotLeft,
                    p = this.plotTop,
                    q = this.plotWidth,
                    r = this.plotHeight,
                    u = this.plotBox,
                    s = this.clipRect,
                    v = this.clipBox;
                n = i + (a.shadow ? 8 : 0), (i || j) && (e ? e.animate(e.crisp({
                    width: c - n,
                    height: d - n
                })) : (e = {
                    fill: j || P
                }, i && (e.stroke = a.borderColor, e["stroke-width"] = i), this.chartBackground = b.rect(n / 2, n / 2, c - n, d - n, a.borderRadius, i).attr(e).addClass("highcharts-background").add().shadow(a.shadow))), k && (f ? f.animate(u) : this.plotBackground = b.rect(o, p, q, r, 0).attr({
                    fill: k
                }).add().shadow(a.plotShadow)), l && (h ? h.animate(u) : this.plotBGImage = b.image(l, o, p, q, r).add()), s ? s.animate({
                    width: v.width,
                    height: v.height
                }) : this.clipRect = b.clipRect(v), m && (g ? g.animate(g.crisp({
                    x: o,
                    y: p,
                    width: q,
                    height: r
                })) : this.plotBorder = b.rect(o, p, q, r, 0, -m).attr({
                    stroke: a.plotBorderColor,
                    "stroke-width": m,
                    fill: P,
                    zIndex: 1
                }).add()), this.isDirtyBox = !1
            },
            propFromSeries: function() {
                var c, e, f, a = this,
                    b = a.options.chart,
                    d = a.options.series;
                q(["inverted", "angular", "polar"], function(g) {
                    for (c = J[b.type || b.defaultSeriesType], f = a[g] || b[g] || c && c.prototype[g], e = d && d.length; !f && e--;)(c = J[d[e].type]) && c.prototype[g] && (f = !0);
                    a[g] = f
                })
            },
            linkSeries: function() {
                var a = this,
                    b = a.series;
                q(b, function(a) {
                    a.linkedSeries.length = 0
                }), q(b, function(b) {
                    var d = b.options.linkedTo;
                    Fa(d) && (d = ":previous" === d ? a.series[b.index - 1] : a.get(d)) && (d.linkedSeries.push(b), b.linkedParent = d)
                })
            },
            renderSeries: function() {
                q(this.series, function(a) {
                    a.translate(), a.setTooltipPoints && a.setTooltipPoints(), a.render()
                })
            },
            renderLabels: function() {
                var a = this,
                    b = a.options.labels;
                b.items && q(b.items, function(c) {
                    var d = r(b.style, c.style),
                        e = z(d.left) + a.plotLeft,
                        f = z(d.top) + a.plotTop + 12;
                    delete d.left, delete d.top, a.renderer.text(c.html, e, f).attr({
                        zIndex: 2
                    }).css(d).add()
                })
            },
            render: function() {
                var a = this.axes,
                    b = this.renderer,
                    c = this.options;
                this.setTitle(), this.legend = new kb(this, c.legend), this.getStacks(), q(a, function(a) {
                    a.setScale()
                }), this.getMargins(), this.maxTicks = null, q(a, function(a) {
                    a.setTickPositions(!0), a.setMaxTicks()
                }), this.adjustTickAmounts(), this.getMargins(), this.drawChartBox(), this.hasCartesianSeries && q(a, function(a) {
                    a.render()
                }), this.seriesGroup || (this.seriesGroup = b.g("series-group").attr({
                    zIndex: 3
                }).add()), this.renderSeries(), this.renderLabels(), this.showCredits(c.credits), this.hasRendered = !0
            },
            showCredits: function(a) {
                a.enabled && !this.credits && (this.credits = this.renderer.text(a.text, 0, 0).on("click", function() {
                    a.href && (location.href = a.href)
                }).attr({
                    align: a.position.align,
                    zIndex: 8
                }).css(a.style).add().align(a.position))
            },
            destroy: function() {
                var e, a = this,
                    b = a.axes,
                    c = a.series,
                    d = a.container,
                    f = d && d.parentNode;
                for (K(a, "destroy"), W[a.index] = t, $a--, a.renderTo.removeAttribute("data-highcharts-chart"), X(a), e = b.length; e--;) b[e] = b[e].destroy();
                for (e = c.length; e--;) c[e] = c[e].destroy();
                q("title,subtitle,chartBackground,plotBackground,plotBGImage,plotBorder,seriesGroup,clipRect,credits,pointer,scroller,rangeSelector,legend,resetZoomButton,tooltip,renderer".split(","), function(b) {
                    var c = a[b];
                    c && c.destroy && (a[b] = c.destroy())
                }), d && (d.innerHTML = "", X(d), f && Pa(d));
                for (e in a) delete a[e]
            },
            isReadyToRender: function() {
                var a = this;
                return !ba && H == H.top && "complete" !== x.readyState || ga && !H.canvg ? (ga ? Lb.push(function() {
                    a.firstRender()
                }, a.options.global.canvasToolsURL) : x.attachEvent("onreadystatechange", function() {
                    x.detachEvent("onreadystatechange", a.firstRender), "complete" === x.readyState && a.firstRender()
                }), !1) : !0
            },
            firstRender: function() {
                var a = this,
                    b = a.options,
                    c = a.callback;
                a.isReadyToRender() && (a.getContainer(), K(a, "init"), a.resetMargins(), a.setChartSize(), a.propFromSeries(), a.getAxes(), q(b.series || [], function(b) {
                    a.initSeries(b)
                }), a.linkSeries(), K(a, "beforeRender"), S.Pointer && (a.pointer = new Va(a, b)), a.render(), a.renderer.draw(), c && c.apply(a, [a]), q(a.callbacks, function(b) {
                    b.apply(a, [a])
                }), a.cloneRenderTo(!0), K(a, "load"))
            },
            splashArray: function(a, b) {
                var c = b[a],
                    c = da(c) ? c : [c, c, c, c];
                return [p(b[a + "Top"], c[0]), p(b[a + "Right"], c[1]), p(b[a + "Bottom"], c[2]), p(b[a + "Left"], c[3])]
            }
        }, Xa.prototype.callbacks = [], Z = S.CenteredSeriesMixin = {
            getCenter: function() {
                var d, h, a = this.options,
                    b = this.chart,
                    c = 2 * (a.slicedOffset || 0),
                    e = b.plotWidth - 2 * c,
                    f = b.plotHeight - 2 * c,
                    b = a.center,
                    a = [p(b[0], "50%"), p(b[1], "50%"), a.size || "100%", a.innerSize || 0],
                    g = C(e, f);
                return Ua(a, function(a, b) {
                    return h = /%$/.test(a), d = 2 > b || 2 === b && h, (h ? [e, f, g, g][b] * z(a) / 100 : a) + (d ? c : 0)
                })
            }
        };
        var Ea = function() {};
        Ea.prototype = {
            init: function(a, b, c) {
                return this.series = a, this.applyOptions(b, c), this.pointAttr = {}, a.options.colorByPoint && (b = a.options.colors || a.chart.options.colors, this.color = this.color || b[a.colorCounter++], a.colorCounter === b.length) && (a.colorCounter = 0), a.chart.pointCount++, this
            },
            applyOptions: function(a, b) {
                var c = this.series,
                    d = c.options.pointValKey || c.pointValKey,
                    a = Ea.prototype.optionsToObject.call(this, a);
                return r(this, a), this.options = this.options ? r(this.options, a) : a, d && (this.y = this[d]), this.x === t && c && (this.x = b === t ? c.autoIncrement() : b), this
            },
            optionsToObject: function(a) {
                var b = {},
                    c = this.series,
                    d = c.pointArrayMap || ["y"],
                    e = d.length,
                    f = 0,
                    g = 0;
                if ("number" == typeof a || null === a) b[d[0]] = a;
                else if (La(a))
                    for (a.length > e && (c = typeof a[0], "string" === c ? b.name = a[0] : "number" === c && (b.x = a[0]), f++); e > g;) b[d[g++]] = a[f++];
                else "object" == typeof a && (b = a, a.dataLabels && (c._hasPointLabels = !0), a.marker && (c._hasPointMarkers = !0));
                return b
            },
            destroy: function() {
                var c, a = this.series.chart,
                    b = a.hoverPoints;
                a.pointCount--, b && (this.setState(), ka(b, this), !b.length) && (a.hoverPoints = null), this === a.hoverPoint && this.onMouseOut(), (this.graphic || this.dataLabel) && (X(this), this.destroyElements()), this.legendItem && a.legend.destroyItem(this);
                for (c in this) this[c] = null
            },
            destroyElements: function() {
                for (var b, a = "graphic,dataLabel,dataLabelUpper,group,connector,shadowGroup".split(","), c = 6; c--;) b = a[c], this[b] && (this[b] = this[b].destroy())
            },
            getLabelConfig: function() {
                return {
                    x: this.category,
                    y: this.y,
                    key: this.name || this.category,
                    series: this.series,
                    point: this,
                    percentage: this.percentage,
                    total: this.total || this.stackTotal
                }
            },
            tooltipFormatter: function(a) {
                var b = this.series,
                    c = b.tooltipOptions,
                    d = p(c.valueDecimals, ""),
                    e = c.valuePrefix || "",
                    f = c.valueSuffix || "";
                return q(b.pointArrayMap || ["y"], function(b) {
                    b = "{point." + b, (e || f) && (a = a.replace(b + "}", e + b + "}" + f)), a = a.replace(b + "}", b + ":,." + d + "f}")
                }), Ia(a, {
                    point: this,
                    series: this.series
                })
            },
            firePointEvent: function(a, b, c) {
                var d = this,
                    e = this.series.options;
                (e.point.events[a] || d.options && d.options.events && d.options.events[a]) && this.importEvents(), "click" === a && e.allowPointSelect && (c = function(a) {
                    d.select(null, a.ctrlKey || a.metaKey || a.shiftKey)
                }), K(this, a, b, c)
            }
        };
        var O = function() {};
        O.prototype = {
            isCartesian: !0,
            type: "line",
            pointClass: Ea,
            sorted: !0,
            requireSorting: !0,
            pointAttrToOptions: {
                stroke: "lineColor",
                "stroke-width": "lineWidth",
                fill: "fillColor",
                r: "radius"
            },
            axisTypes: ["xAxis", "yAxis"],
            colorCounter: 0,
            parallelArrays: ["x", "y"],
            init: function(a, b) {
                var d, e, c = this,
                    f = a.series,
                    g = function(a, b) {
                        return p(a.options.index, a._i) - p(b.options.index, b._i)
                    };
                c.chart = a, c.options = b = c.setOptions(b), c.linkedSeries = [], c.bindAxes(), r(c, {
                    name: b.name,
                    state: "",
                    pointAttr: {},
                    visible: b.visible !== !1,
                    selected: b.selected === !0
                }), ga && (b.animation = !1), e = b.events;
                for (d in e) N(c, d, e[d]);
                (e && e.click || b.point && b.point.events && b.point.events.click || b.allowPointSelect) && (a.runTrackerClick = !0), c.getColor(), c.getSymbol(), q(c.parallelArrays, function(a) {
                    c[a + "Data"] = []
                }), c.setData(b.data, !1), c.isCartesian && (a.hasCartesianSeries = !0), f.push(c), c._i = f.length - 1, nb(f, g), this.yAxis && nb(this.yAxis.series, g), q(f, function(a, b) {
                    a.index = b, a.name = a.name || "Series " + (b + 1)
                })
            },
            bindAxes: function() {
                var d, a = this,
                    b = a.options,
                    c = a.chart;
                q(a.axisTypes || [], function(e) {
                    q(c[e], function(c) {
                        d = c.options, (b[e] === d.index || b[e] !== t && b[e] === d.id || b[e] === t && 0 === d.index) && (c.series.push(a), a[e] = c, c.isDirty = !0)
                    }), !a[e] && a.optionalAxis !== e && oa(18, !0)
                })
            },
            updateParallelArrays: function(a, b) {
                var c = a.series,
                    d = arguments;
                q(c.parallelArrays, "number" == typeof b ? function(d) {
                    var f = "y" === d && c.toYData ? c.toYData(a) : a[d];
                    c[d + "Data"][b] = f
                } : function(a) {
                    Array.prototype[b].apply(c[a + "Data"], Array.prototype.slice.call(d, 2))
                })
            },
            autoIncrement: function() {
                var a = this.options,
                    b = this.xIncrement,
                    b = p(b, a.pointStart, 0);
                return this.pointInterval = p(this.pointInterval, a.pointInterval, 1), this.xIncrement = b + this.pointInterval, b
            },
            getSegments: function() {
                var c, a = -1,
                    b = [],
                    d = this.points,
                    e = d.length;
                if (e)
                    if (this.options.connectNulls) {
                        for (c = e; c--;) null === d[c].y && d.splice(c, 1);
                        d.length && (b = [d])
                    } else q(d, function(c, g) {
                        null === c.y ? (g > a + 1 && b.push(d.slice(a + 1, g)), a = g) : g === e - 1 && b.push(d.slice(a + 1, g + 1))
                    });
                this.segments = b
            },
            setOptions: function(a) {
                var b = this.chart,
                    c = b.options.plotOptions,
                    b = b.userOptions || {},
                    d = b.plotOptions || {},
                    e = c[this.type];
                return this.userOptions = a, c = w(e, c.series, a), this.tooltipOptions = w(L.tooltip, L.plotOptions[this.type].tooltip, b.tooltip, d.series && d.series.tooltip, d[this.type] && d[this.type].tooltip, a.tooltip), null === e.marker && delete c.marker, c
            },
            getCyclic: function(a, b, c) {
                var d = this.userOptions,
                    e = "_" + a + "Index",
                    f = a + "Counter";
                b || (s(d[e]) ? b = d[e] : (d[e] = b = this.chart[f] % c.length, this.chart[f] += 1), b = c[b]), this[a] = b
            },
            getColor: function() {
                this.options.colorByPoint || this.getCyclic("color", this.options.color || ca[this.type].color, this.chart.options.colors)
            },
            getSymbol: function() {
                var a = this.options.marker;
                this.getCyclic("symbol", a.symbol, this.chart.options.symbols), /^url/.test(this.symbol) && (a.radius = 0)
            },
            drawLegendSymbol: M.drawLineMarker,
            setData: function(a, b, c, d) {
                var h, e = this,
                    f = e.points,
                    g = f && f.length || 0,
                    i = e.options,
                    j = e.chart,
                    k = null,
                    l = e.xAxis,
                    m = l && !!l.categories,
                    n = e.tooltipPoints,
                    o = i.turboThreshold,
                    r = this.xData,
                    u = this.yData,
                    s = (h = e.pointArrayMap) && h.length,
                    a = a || [];
                if (h = a.length, b = p(b, !0), d === !1 || !h || g !== h || e.cropped || e.hasGroupedData) {
                    if (e.xIncrement = null, e.pointRange = m ? 1 : i.pointRange, e.colorCounter = 0, q(this.parallelArrays, function(a) {
                        e[a + "Data"].length = 0
                    }), o && h > o) {
                        for (c = 0; null === k && h > c;) k = a[c], c++;
                        if (ia(k)) {
                            for (m = p(i.pointStart, 0), i = p(i.pointInterval, 1), c = 0; h > c; c++) r[c] = m, u[c] = a[c], m += i;
                            e.xIncrement = m
                        } else if (La(k))
                            if (s)
                                for (c = 0; h > c; c++) i = a[c], r[c] = i[0], u[c] = i.slice(1, s + 1);
                            else
                                for (c = 0; h > c; c++) i = a[c], r[c] = i[0], u[c] = i[1];
                        else oa(12)
                    } else
                        for (c = 0; h > c; c++) a[c] !== t && (i = {
                            series: e
                        }, e.pointClass.prototype.applyOptions.apply(i, [a[c]]), e.updateParallelArrays(i, c), m && i.name) && (l.names[i.x] = i.name);
                    for (Fa(u[0]) && oa(14, !0), e.data = [], e.options.data = a, c = g; c--;) f[c] && f[c].destroy && f[c].destroy();
                    n && (n.length = 0), l && (l.minRange = l.userMinRange), e.isDirty = e.isDirtyData = j.isDirtyBox = !0, c = !1
                } else q(a, function(a, b) {
                    f[b].update(a, !1)
                });
                b && j.redraw(c)
            },
            processData: function(a) {
                var e, b = this.xData,
                    c = this.yData,
                    d = b.length;
                e = 0;
                var f, g, m, n, h = this.xAxis,
                    i = this.options,
                    j = i.cropThreshold,
                    k = 0,
                    l = this.isCartesian;
                if (l && !this.isDirty && !h.isDirty && !this.yAxis.isDirty && !a) return !1;
                for (l && this.sorted && (!j || d > j || this.forceCrop) && (m = h.getExtremes(), n = m.min, m = m.max, b[d - 1] < n || b[0] > m ? (b = [], c = []) : (b[0] < n || b[d - 1] > m) && (e = this.cropData(this.xData, this.yData, n, m), b = e.xData, c = e.yData, e = e.start, f = !0, k = b.length)), a = b.length - 1; a >= 0; a--) d = b[a] - b[a - 1], !f && b[a] > n && b[a] < m && k++, d > 0 && (g === t || g > d) ? g = d : 0 > d && this.requireSorting && oa(15);
                this.cropped = f, this.cropStart = e, this.processedXData = b, this.processedYData = c, this.activePointCount = k, null === i.pointRange && (this.pointRange = g || 1), this.closestPointRange = g
            },
            cropData: function(a, b, c, d) {
                var i, e = a.length,
                    f = 0,
                    g = e,
                    h = p(this.cropShoulder, 1);
                for (i = 0; e > i; i++)
                    if (a[i] >= c) {
                        f = u(0, i - h);
                        break
                    }
                for (; e > i; i++)
                    if (a[i] > d) {
                        g = i + h;
                        break
                    }
                return {
                    xData: a.slice(f, g),
                    yData: b.slice(f, g),
                    start: f,
                    end: g
                }
            },
            generatePoints: function() {
                var c, i, k, m, a = this.options.data,
                    b = this.data,
                    d = this.processedXData,
                    e = this.processedYData,
                    f = this.pointClass,
                    g = d.length,
                    h = this.cropStart || 0,
                    j = this.hasGroupedData,
                    l = [];
                for (b || j || (b = [], b.length = a.length, b = this.data = b), m = 0; g > m; m++) i = h + m, j ? l[m] = (new f).init(this, [d[m]].concat(ra(e[m]))) : (b[i] ? k = b[i] : a[i] !== t && (b[i] = k = (new f).init(this, a[i], d[m])), l[m] = k);
                if (b && (g !== (c = b.length) || j))
                    for (m = 0; c > m; m++) m === h && !j && (m += g), b[m] && (b[m].destroyElements(), b[m].plotX = t);
                this.data = b, this.points = l
            },
            getExtremes: function(a) {
                var d, b = this.yAxis,
                    c = this.processedXData,
                    e = [],
                    f = 0;
                d = this.xAxis.getExtremes();
                var i, j, k, l, g = d.min,
                    h = d.max,
                    a = a || this.stackedYData || this.processedYData;
                for (d = a.length, l = 0; d > l; l++)
                    if (j = c[l], k = a[l], i = null !== k && k !== t && (!b.isLog || k.length || k > 0), j = this.getExtremesFromAll || this.cropped || (c[l + 1] || j) >= g && (c[l - 1] || j) <= h, i && j)
                        if (i = k.length)
                            for (; i--;) null !== k[i] && (e[f++] = k[i]);
                        else e[f++] = k;
                this.dataMin = p(void 0, Na(e)), this.dataMax = p(void 0, Ba(e))
            },
            translate: function() {
                this.processedXData || this.processData(), this.generatePoints();
                for (var a = this.options, b = a.stacking, c = this.xAxis, d = c.categories, e = this.yAxis, f = this.points, g = f.length, h = !!this.modifyValue, i = a.pointPlacement, j = "between" === i || ia(i), k = a.threshold, a = 0; g > a; a++) {
                    var l = f[a],
                        m = l.x,
                        n = l.y,
                        o = l.low,
                        q = b && e.stacks[(this.negStacks && k > n ? "-" : "") + this.stackKey];
                    e.isLog && 0 >= n && (l.y = n = null), l.plotX = c.translate(m, 0, 0, 0, 1, i, "flags" === this.type), b && this.visible && q && q[m] && (q = q[m], n = q.points[this.index + "," + a], o = n[0], n = n[1], 0 === o && (o = p(k, e.min)), e.isLog && 0 >= o && (o = null), l.total = l.stackTotal = q.total, l.percentage = q.total && l.y / q.total * 100, l.stackY = n, q.setOffset(this.pointXOffset || 0, this.barW || 0)), l.yBottom = s(o) ? e.translate(o, 0, 1, 0, 1) : null, h && (n = this.modifyValue(n, l)), l.plotY = "number" == typeof n && 1 / 0 !== n ? e.translate(n, 0, 1, 0, 1) : t, l.clientX = j ? c.translate(m, 0, 0, 0, 1) : l.plotX, l.negative = l.y < (k || 0), l.category = d && d[l.x] !== t ? d[l.x] : l.x
                }
                this.getSegments()
            },
            animate: function(a) {
                var d, b = this.chart,
                    c = b.renderer;
                d = this.options.animation;
                var g, e = this.clipBox || b.clipBox,
                    f = b.inverted;
                d && !da(d) && (d = ca[this.type].animation), g = ["_sharedClip", d.duration, d.easing, e.height].join(","), a ? (a = b[g], d = b[g + "m"], a || (b[g] = a = c.clipRect(r(e, {
                    width: 0
                })), b[g + "m"] = d = c.clipRect(-99, f ? -b.plotLeft : -b.plotTop, 99, f ? b.chartWidth : b.chartHeight)), this.group.clip(a), this.markerGroup.clip(d), this.sharedClipKey = g) : ((a = b[g]) && a.animate({
                    width: b.plotSizeX
                }, d), b[g + "m"] && b[g + "m"].animate({
                    width: b.plotSizeX + 99
                }, d), this.animate = null)
            },
            afterAnimate: function() {
                var a = this.chart,
                    b = this.sharedClipKey,
                    c = this.group,
                    d = this.clipBox;
                c && this.options.clip !== !1 && (b && d || c.clip(d ? a.renderer.clipRect(d) : a.clipRect), this.markerGroup.clip()), K(this, "afterAnimate"), setTimeout(function() {
                    b && a[b] && (d || (a[b] = a[b].destroy()), a[b + "m"] && (a[b + "m"] = a[b + "m"].destroy()))
                }, 100)
            },
            drawPoints: function() {
                var a, d, e, f, g, h, i, j, k, b = this.points,
                    c = this.chart;
                d = this.options.marker;
                var m, l = this.pointAttr[""],
                    n = this.markerGroup,
                    o = p(d.enabled, this.activePointCount < .5 * this.xAxis.len / d.radius);
                if (d.enabled !== !1 || this._hasPointMarkers)
                    for (f = b.length; f--;) g = b[f], d = U(g.plotX), e = g.plotY, k = g.graphic, i = g.marker || {}, a = o && i.enabled === t || i.enabled, m = c.isInsidePlot(v(d), e, c.inverted), a && e !== t && !isNaN(e) && null !== g.y ? (a = g.pointAttr[g.selected ? "select" : ""] || l, h = a.r, i = p(i.symbol, this.symbol), j = 0 === i.indexOf("url"), k ? k[m ? "show" : "hide"](!0).animate(r({
                        x: d - h,
                        y: e - h
                    }, k.symbolName ? {
                        width: 2 * h,
                        height: 2 * h
                    } : {})) : m && (h > 0 || j) && (g.graphic = c.renderer.symbol(i, d - h, e - h, 2 * h, 2 * h).attr(a).add(n))) : k && (g.graphic = k.destroy())
            },
            convertAttribs: function(a, b, c, d) {
                var f, g, e = this.pointAttrToOptions,
                    h = {},
                    a = a || {},
                    b = b || {},
                    c = c || {},
                    d = d || {};
                for (f in e) g = e[f], h[f] = p(a[g], b[f], c[f], d[f]);
                return h
            },
            getAttribs: function() {
                var f, a = this,
                    b = a.options,
                    c = ca[a.type].marker ? b.marker : b,
                    d = c.states,
                    e = d.hover,
                    g = a.color;
                f = {
                    stroke: g,
                    fill: g
                };
                var i, k, h = a.points || [],
                    j = [],
                    l = a.pointAttrToOptions;
                k = a.hasPointSpecificOptions;
                var m = b.negativeColor,
                    n = c.lineColor,
                    o = c.fillColor;
                i = b.turboThreshold;
                var p;
                if (b.marker ? (e.radius = e.radius || c.radius + e.radiusPlus, e.lineWidth = e.lineWidth || c.lineWidth + e.lineWidthPlus) : e.color = e.color || ya(e.color || g).brighten(e.brightness).get(), j[""] = a.convertAttribs(c, f), q(["hover", "select"], function(b) {
                    j[b] = a.convertAttribs(d[b], j[""])
                }), a.pointAttr = j, g = h.length, !i || i > g || k)
                    for (; g--;) {
                        if (i = h[g], (c = i.options && i.options.marker || i.options) && c.enabled === !1 && (c.radius = 0), i.negative && m && (i.color = i.fillColor = m), k = b.colorByPoint || i.color, i.options)
                            for (p in l) s(c[l[p]]) && (k = !0);
                        k ? (c = c || {}, k = [], d = c.states || {}, f = d.hover = d.hover || {}, b.marker || (f.color = f.color || !i.options.color && e.color || ya(i.color).brighten(f.brightness || e.brightness).get()), f = {
                            color: i.color
                        }, o || (f.fillColor = i.color), n || (f.lineColor = i.color), k[""] = a.convertAttribs(r(f, c), j[""]), k.hover = a.convertAttribs(d.hover, j.hover, k[""]), k.select = a.convertAttribs(d.select, j.select, k[""])) : k = j, i.pointAttr = k
                    }
            },
            destroy: function() {
                var d, e, g, h, i, a = this,
                    b = a.chart,
                    c = /AppleWebKit\/533/.test(wa),
                    f = a.data || [];
                for (K(a, "destroy"), X(a), q(a.axisTypes || [], function(b) {
                    (i = a[b]) && (ka(i.series, a), i.isDirty = i.forceRedraw = !0)
                }), a.legendItem && a.chart.legend.destroyItem(a), e = f.length; e--;)(g = f[e]) && g.destroy && g.destroy();
                a.points = null, clearTimeout(a.animationTimeout), q("area,graph,dataLabelsGroup,group,markerGroup,tracker,graphNeg,areaNeg,posClip,negClip".split(","), function(b) {
                    a[b] && (d = c && "group" === b ? "hide" : "destroy", a[b][d]())
                }), b.hoverSeries === a && (b.hoverSeries = null), ka(b.series, a);
                for (h in a) delete a[h]
            },
            getSegmentPath: function(a) {
                var b = this,
                    c = [],
                    d = b.options.step;
                return q(a, function(e, f) {
                    var i, g = e.plotX,
                        h = e.plotY;
                    b.getPointSpline ? c.push.apply(c, b.getPointSpline(a, e, f)) : (c.push(f ? "L" : "M"), d && f && (i = a[f - 1], "right" === d ? c.push(i.plotX, h) : "center" === d ? c.push((i.plotX + g) / 2, i.plotY, (i.plotX + g) / 2, h) : c.push(g, i.plotY)), c.push(e.plotX, e.plotY))
                }), c
            },
            getGraphPath: function() {
                var c, a = this,
                    b = [],
                    d = [];
                return q(a.segments, function(e) {
                    c = a.getSegmentPath(e), e.length > 1 ? b = b.concat(c) : d.push(e[0])
                }), a.singlePoints = d, a.graphPath = b
            },
            drawGraph: function() {
                var a = this,
                    b = this.options,
                    c = [
                        ["graph", b.lineColor || this.color]
                    ],
                    d = b.lineWidth,
                    e = b.dashStyle,
                    f = "square" !== b.linecap,
                    g = this.getGraphPath(),
                    h = b.negativeColor;
                h && c.push(["graphNeg", h]), q(c, function(c, h) {
                    var k = c[0],
                        l = a[k];
                    l ? (ab(l), l.animate({
                        d: g
                    })) : d && g.length && (l = {
                        stroke: c[1],
                        "stroke-width": d,
                        fill: P,
                        zIndex: 1
                    }, e ? l.dashstyle = e : f && (l["stroke-linecap"] = l["stroke-linejoin"] = "round"), a[k] = a.chart.renderer.path(g).attr(l).add(a.group).shadow(!h && b.shadow))
                })
            },
            clipNeg: function() {
                var e, a = this.options,
                    b = this.chart,
                    c = b.renderer,
                    d = a.negativeColor || a.negativeFillColor,
                    f = this.graph,
                    g = this.area,
                    h = this.posClip,
                    i = this.negClip;
                e = b.chartWidth;
                var j = b.chartHeight,
                    k = u(e, j),
                    l = this.yAxis;
                d && (f || g) && (d = v(l.toPixels(a.threshold || 0, !0)), 0 > d && (k -= d), a = {
                    x: 0,
                    y: 0,
                    width: k,
                    height: d
                }, k = {
                    x: 0,
                    y: d,
                    width: k,
                    height: k
                }, b.inverted && (a.height = k.y = b.plotWidth - d, c.isVML && (a = {
                    x: b.plotWidth - d - b.plotLeft,
                    y: 0,
                    width: e,
                    height: j
                }, k = {
                    x: d + b.plotLeft - e,
                    y: 0,
                    width: b.plotLeft + d,
                    height: e
                })), l.reversed ? (b = k, e = a) : (b = a, e = k), h ? (h.animate(b), i.animate(e)) : (this.posClip = h = c.clipRect(b), this.negClip = i = c.clipRect(e), f && this.graphNeg && (f.clip(h), this.graphNeg.clip(i)), g && (g.clip(h), this.areaNeg.clip(i))))
            },
            invertGroups: function() {
                function a() {
                    var a = {
                        width: b.yAxis.len,
                        height: b.xAxis.len
                    };
                    q(["group", "markerGroup"], function(c) {
                        b[c] && b[c].attr(a).invert()
                    })
                }
                var b = this,
                    c = b.chart;
                b.xAxis && (N(c, "resize", a), N(b, "destroy", function() {
                    X(c, "resize", a)
                }), a(), b.invertGroups = a)
            },
            plotGroup: function(a, b, c, d, e) {
                var f = this[a],
                    g = !f;
                return g && (this[a] = f = this.chart.renderer.g(b).attr({
                    visibility: c,
                    zIndex: d || .1
                }).add(e)), f[g ? "attr" : "animate"](this.getPlotBox()), f
            },
            getPlotBox: function() {
                var a = this.chart,
                    b = this.xAxis,
                    c = this.yAxis;
                return a.inverted && (b = c, c = this.xAxis), {
                    translateX: b ? b.left : a.plotLeft,
                    translateY: c ? c.top : a.plotTop,
                    scaleX: 1,
                    scaleY: 1
                }
            },
            render: function() {
                var c, a = this,
                    b = a.chart,
                    d = a.options,
                    e = (c = d.animation) && !!a.animate && b.renderer.isSVG && p(c.duration, 500) || 0,
                    f = a.visible ? "visible" : "hidden",
                    g = d.zIndex,
                    h = a.hasRendered,
                    i = b.seriesGroup;
                c = a.plotGroup("group", "series", f, g, i), a.markerGroup = a.plotGroup("markerGroup", "markers", f, g, i), e && a.animate(!0), a.getAttribs(), c.inverted = a.isCartesian ? b.inverted : !1, a.drawGraph && (a.drawGraph(), a.clipNeg()), a.drawDataLabels && a.drawDataLabels(), a.visible && a.drawPoints(), a.drawTracker && a.options.enableMouseTracking !== !1 && a.drawTracker(), b.inverted && a.invertGroups(), d.clip !== !1 && !a.sharedClipKey && !h && c.clip(b.clipRect), e && a.animate(), h || (e ? a.animationTimeout = setTimeout(function() {
                    a.afterAnimate()
                }, e) : a.afterAnimate()), a.isDirty = a.isDirtyData = !1, a.hasRendered = !0
            },
            redraw: function() {
                var a = this.chart,
                    b = this.isDirtyData,
                    c = this.group,
                    d = this.xAxis,
                    e = this.yAxis;
                c && (a.inverted && c.attr({
                    width: a.plotWidth,
                    height: a.plotHeight
                }), c.animate({
                    translateX: p(d && d.left, a.plotLeft),
                    translateY: p(e && e.top, a.plotTop)
                })), this.translate(), this.setTooltipPoints && this.setTooltipPoints(!0), this.render(), b && K(this, "updatedData")
            }
        }, Fb.prototype = {
            destroy: function() {
                Oa(this, this.axis)
            },
            render: function(a) {
                var b = this.options,
                    c = b.format,
                    c = c ? Ia(c, this) : b.formatter.call(this);
                this.label ? this.label.attr({
                    text: c,
                    visibility: "hidden"
                }) : this.label = this.axis.chart.renderer.text(c, null, null, b.useHTML).css(b.style).attr({
                    align: this.textAlign,
                    rotation: b.rotation,
                    visibility: "hidden"
                }).add(a)
            },
            setOffset: function(a, b) {
                var c = this.axis,
                    d = c.chart,
                    e = d.inverted,
                    f = this.isNegative,
                    g = c.translate(c.usePercentage ? 100 : this.total, 0, 0, 0, 1),
                    c = c.translate(0),
                    c = Q(g - c),
                    h = d.xAxis[0].translate(this.x) + a,
                    i = d.plotHeight,
                    f = {
                        x: e ? f ? g : g - c : h,
                        y: e ? i - h - b : f ? i - g - c : i - g,
                        width: e ? c : b,
                        height: e ? b : c
                    };
                (e = this.label) && (e.align(this.alignOptions, null, f), f = e.alignAttr, e[this.options.crop === !1 || d.isInsidePlot(f.x, f.y) ? "show" : "hide"](!0))
            }
        }, ma.prototype.buildStacks = function() {
            var a = this.series,
                b = p(this.options.reversedStacks, !0),
                c = a.length;
            if (!this.isXAxis) {
                for (this.usePercentage = !1; c--;) a[b ? c : a.length - c - 1].setStackedPoints();
                if (this.usePercentage)
                    for (c = 0; c < a.length; c++) a[c].setPercentStacks()
            }
        }, ma.prototype.renderStackTotals = function() {
            var d, e, a = this.chart,
                b = a.renderer,
                c = this.stacks,
                f = this.stackTotalGroup;
            f || (this.stackTotalGroup = f = b.g("stack-labels").attr({
                visibility: "visible",
                zIndex: 6
            }).add()), f.translate(a.plotLeft, a.plotTop);
            for (d in c)
                for (e in a = c[d]) a[e].render(f)
        }, O.prototype.setStackedPoints = function() {
            if (this.options.stacking && (this.visible === !0 || this.chart.options.chart.ignoreHiddenSeries === !1)) {
                var n, o, p, q, r, s, a = this.processedXData,
                    b = this.processedYData,
                    c = [],
                    d = b.length,
                    e = this.options,
                    f = e.threshold,
                    g = e.stack,
                    e = e.stacking,
                    h = this.stackKey,
                    i = "-" + h,
                    j = this.negStacks,
                    k = this.yAxis,
                    l = k.stacks,
                    m = k.oldStacks;
                for (q = 0; d > q; q++) r = a[q], s = b[q], p = this.index + "," + q, o = (n = j && f > s) ? i : h, l[o] || (l[o] = {}), l[o][r] || (m[o] && m[o][r] ? (l[o][r] = m[o][r], l[o][r].total = null) : l[o][r] = new Fb(k, k.options.stackLabels, n, r, g)), o = l[o][r], o.points[p] = [o.cum || 0], "percent" === e ? (n = n ? h : i, j && l[n] && l[n][r] ? (n = l[n][r], o.total = n.total = u(n.total, o.total) + Q(s) || 0) : o.total = ea(o.total + (Q(s) || 0))) : o.total = ea(o.total + (s || 0)), o.cum = (o.cum || 0) + (s || 0), o.points[p].push(o.cum), c[q] = o.cum;
                "percent" === e && (k.usePercentage = !0), this.stackedYData = c, k.oldStacks = {}
            }
        }, O.prototype.setPercentStacks = function() {
            var a = this,
                b = a.stackKey,
                c = a.yAxis.stacks,
                d = a.processedXData;
            q([b, "-" + b], function(b) {
                for (var e, g, h, f = d.length; f--;) g = d[f], e = (h = c[b] && c[b][g]) && h.points[a.index + "," + f], (g = e) && (h = h.total ? 100 / h.total : 0, g[0] = ea(g[0] * h), g[1] = ea(g[1] * h), a.stackedYData[f] = g[1])
            })
        }, r(Xa.prototype, {
            addSeries: function(a, b, c) {
                var d, e = this;
                return a && (b = p(b, !0), K(e, "addSeries", {
                    options: a
                }, function() {
                    d = e.initSeries(a), e.isDirtyLegend = !0, e.linkSeries(), b && e.redraw(c)
                })), d
            },
            addAxis: function(a, b, c, d) {
                var e = b ? "xAxis" : "yAxis",
                    f = this.options;
                new ma(this, w(a, {
                    index: this[e].length,
                    isX: b
                })), f[e] = ra(f[e] || {}), f[e].push(a), p(c, !0) && this.redraw(d)
            },
            showLoading: function(a) {
                var b = this,
                    c = b.options,
                    d = b.loadingDiv,
                    e = c.loading,
                    f = function() {
                        d && A(d, {
                            left: b.plotLeft + "px",
                            top: b.plotTop + "px",
                            width: b.plotWidth + "px",
                            height: b.plotHeight + "px"
                        })
                    };
                d || (b.loadingDiv = d = $(Ja, {
                    className: "highcharts-loading"
                }, r(e.style, {
                    zIndex: 10,
                    display: P
                }), b.container), b.loadingSpan = $("span", null, e.labelStyle, d), N(b, "redraw", f)), b.loadingSpan.innerHTML = a || c.lang.loading, b.loadingShown || (A(d, {
                    opacity: 0,
                    display: ""
                }), ib(d, {
                    opacity: e.style.opacity
                }, {
                    duration: e.showDuration || 0
                }), b.loadingShown = !0), f()
            },
            hideLoading: function() {
                var a = this.options,
                    b = this.loadingDiv;
                b && ib(b, {
                    opacity: 0
                }, {
                    duration: a.loading.hideDuration || 100,
                    complete: function() {
                        A(b, {
                            display: P
                        })
                    }
                }), this.loadingShown = !1
            }
        }), r(Ea.prototype, {
            update: function(a, b, c) {
                var g, d = this,
                    e = d.series,
                    f = d.graphic,
                    h = e.data,
                    i = e.chart,
                    j = e.options,
                    b = p(b, !0);
                d.firePointEvent("update", {
                    options: a
                }, function() {
                    d.applyOptions(a), da(a) && (e.getAttribs(), f && (a && a.marker && a.marker.symbol ? d.graphic = f.destroy() : f.attr(d.pointAttr[d.state || ""])), a && a.dataLabels && d.dataLabel && (d.dataLabel = d.dataLabel.destroy())), g = Da(d, h), e.updateParallelArrays(d, g), j.data[g] = d.options, e.isDirty = e.isDirtyData = !0, !e.fixedBox && e.hasCartesianSeries && (i.isDirtyBox = !0), "point" === j.legendType && i.legend.destroyItem(d), b && i.redraw(c)
                })
            },
            remove: function(a, b) {
                var g, c = this,
                    d = c.series,
                    e = d.points,
                    f = d.chart,
                    h = d.data;
                Qa(b, f), a = p(a, !0), c.firePointEvent("remove", null, function() {
                    g = Da(c, h), h.length === e.length && e.splice(g, 1), h.splice(g, 1), d.options.data.splice(g, 1), d.updateParallelArrays(c, "splice", g, 1), c.destroy(), d.isDirty = !0, d.isDirtyData = !0, a && f.redraw()
                })
            }
        }), r(O.prototype, {
            addPoint: function(a, b, c, d) {
                var m, e = this.options,
                    f = this.data,
                    g = this.graph,
                    h = this.area,
                    i = this.chart,
                    j = this.xAxis && this.xAxis.names,
                    k = g && g.shift || 0,
                    l = e.data,
                    n = this.xData;
                if (Qa(d, i), c && q([g, h, this.graphNeg, this.areaNeg], function(a) {
                    a && (a.shift = k + 1)
                }), h && (h.isArea = !0), b = p(b, !0), d = {
                    series: this
                }, this.pointClass.prototype.applyOptions.apply(d, [a]), g = d.x, h = n.length, this.requireSorting && g < n[h - 1])
                    for (m = !0; h && n[h - 1] > g;) h--;
                this.updateParallelArrays(d, "splice", h, 0, 0), this.updateParallelArrays(d, h), j && (j[g] = d.name), l.splice(h, 0, a), m && (this.data.splice(h, 0, null), this.processData()), "point" === e.legendType && this.generatePoints(), c && (f[0] && f[0].remove ? f[0].remove(!1) : (f.shift(), this.updateParallelArrays(d, "shift"), l.shift())), this.isDirtyData = this.isDirty = !0, b && (this.getAttribs(), i.redraw())
            },
            remove: function(a, b) {
                var c = this,
                    d = c.chart,
                    a = p(a, !0);
                c.isRemoving || (c.isRemoving = !0, K(c, "remove", null, function() {
                    c.destroy(), d.isDirtyLegend = d.isDirtyBox = !0, d.linkSeries(), a && d.redraw(b)
                })), c.isRemoving = !1
            },
            update: function(a, b) {
                var i, c = this,
                    d = this.chart,
                    e = this.userOptions,
                    f = this.type,
                    g = J[f].prototype,
                    h = ["group", "markerGroup", "dataLabelsGroup"];
                q(h, function(a) {
                    h[a] = c[a], delete c[a]
                }), a = w(e, {
                    animation: !1,
                    index: this.index,
                    pointStart: this.xData[0]
                }, {
                    data: this.options.data
                }, a), this.remove(!1);
                for (i in g) g.hasOwnProperty(i) && (this[i] = t);
                r(this, J[a.type || f].prototype), q(h, function(a) {
                    c[a] = h[a]
                }), this.init(d, a), d.linkSeries(), p(b, !0) && d.redraw(!1)
            }
        }), r(ma.prototype, {
            update: function(a, b) {
                var c = this.chart,
                    a = c.options[this.coll][this.options.index] = w(this.userOptions, a);
                this.destroy(!0), this._addedPlotLB = t, this.init(c, r(a, {
                    events: t
                })), c.isDirtyBox = !0, p(b, !0) && c.redraw()
            },
            remove: function(a) {
                for (var b = this.chart, c = this.coll, d = this.series, e = d.length; e--;) d[e] && d[e].remove(!1);
                ka(b.axes, this), ka(b[c], this), b.options[c].splice(this.options.index, 1), q(b[c], function(a, b) {
                    a.options.index = b
                }), this.destroy(), b.isDirtyBox = !0, p(a, !0) && b.redraw()
            },
            setTitle: function(a, b) {
                this.update({
                    title: a
                }, b)
            },
            setCategories: function(a, b) {
                this.update({
                    categories: a
                }, b)
            }
        }), ha = la(O), J.line = ha, ca.area = w(T, {
            threshold: 0
        });
        var qa = la(O, {
            type: "area",
            getSegments: function() {
                var i, j, m, n, a = this,
                    b = [],
                    c = [],
                    d = [],
                    e = this.xAxis,
                    f = this.yAxis,
                    g = f.stacks[this.stackKey],
                    h = {},
                    k = this.points,
                    l = this.options.connectNulls;
                if (this.options.stacking && !this.cropped) {
                    for (m = 0; m < k.length; m++) h[k[m].x] = k[m];
                    for (n in g) null !== g[n].total && d.push(+n);
                    d.sort(function(a, b) {
                        return a - b
                    }), q(d, function(b) {
                        var k, d = 0;
                        if (!l || h[b] && null !== h[b].y)
                            if (h[b]) c.push(h[b]);
                            else {
                                for (m = a.index; m <= f.series.length; m++)
                                    if (k = g[b].points[m + "," + b]) {
                                        d = k[1];
                                        break
                                    }
                                i = e.translate(b), j = f.toPixels(d, !0), c.push({
                                    y: null,
                                    plotX: i,
                                    clientX: i,
                                    plotY: j,
                                    yBottom: j,
                                    onMouseOver: sa
                                })
                            }
                    }), c.length && b.push(c)
                } else O.prototype.getSegments.call(this), b = this.segments;
                this.segments = b
            },
            getSegmentPath: function(a) {
                var d, b = O.prototype.getSegmentPath.call(this, a),
                    c = [].concat(b),
                    e = this.options;
                d = b.length;
                var g, f = this.yAxis.getThreshold(e.threshold);
                if (3 === d && c.push("L", b[1], b[2]), e.stacking && !this.closedStacks)
                    for (d = a.length - 1; d >= 0; d--) g = p(a[d].yBottom, f), d < a.length - 1 && e.step && c.push(a[d + 1].plotX, g), c.push(a[d].plotX, g);
                else this.closeSegment(c, a, f);
                return this.areaPath = this.areaPath.concat(c), b
            },
            closeSegment: function(a, b, c) {
                a.push("L", b[b.length - 1].plotX, c, "L", b[0].plotX, c)
            },
            drawGraph: function() {
                this.areaPath = [], O.prototype.drawGraph.apply(this);
                var a = this,
                    b = this.areaPath,
                    c = this.options,
                    d = c.negativeColor,
                    e = c.negativeFillColor,
                    f = [
                        ["area", this.color, c.fillColor]
                    ];
                (d || e) && f.push(["areaNeg", d, e]), q(f, function(d) {
                    var e = d[0],
                        f = a[e];
                    f ? f.animate({
                        d: b
                    }) : a[e] = a.chart.renderer.path(b).attr({
                        fill: p(d[2], ya(d[1]).setOpacity(p(c.fillOpacity, .75)).get()),
                        zIndex: 0
                    }).add(a.group)
                })
            },
            drawLegendSymbol: M.drawRectangle
        });
        J.area = qa, ca.spline = w(T), ha = la(O, {
            type: "spline",
            getPointSpline: function(a, b, c) {
                var h, i, j, k, d = b.plotX,
                    e = b.plotY,
                    f = a[c - 1],
                    g = a[c + 1];
                if (f && g) {
                    a = f.plotY, j = g.plotX;
                    var l, g = g.plotY;
                    h = (1.5 * d + f.plotX) / 2.5, i = (1.5 * e + a) / 2.5, j = (1.5 * d + j) / 2.5, k = (1.5 * e + g) / 2.5, l = (k - i) * (j - d) / (j - h) + e - k, i += l, k += l, i > a && i > e ? (i = u(a, e), k = 2 * e - i) : a > i && e > i && (i = C(a, e), k = 2 * e - i), k > g && k > e ? (k = u(g, e), i = 2 * e - k) : g > k && e > k && (k = C(g, e), i = 2 * e - k), b.rightContX = j, b.rightContY = k
                }
                return c ? (b = ["C", f.rightContX || f.plotX, f.rightContY || f.plotY, h || d, i || e, d, e], f.rightContX = f.rightContY = null) : b = ["M", d, e], b
            }
        }), J.spline = ha, ca.areaspline = w(ca.area), qa = qa.prototype, ha = la(ha, {
            type: "areaspline",
            closedStacks: !0,
            getSegmentPath: qa.getSegmentPath,
            closeSegment: qa.closeSegment,
            drawGraph: qa.drawGraph,
            drawLegendSymbol: M.drawRectangle
        }), J.areaspline = ha, ca.column = w(T, {
            borderColor: "#FFFFFF",
            borderRadius: 0,
            groupPadding: .2,
            marker: null,
            pointPadding: .1,
            minPointLength: 0,
            cropThreshold: 50,
            pointRange: null,
            states: {
                hover: {
                    brightness: .1,
                    shadow: !1,
                    halo: !1
                },
                select: {
                    color: "#C0C0C0",
                    borderColor: "#000000",
                    shadow: !1
                }
            },
            dataLabels: {
                align: null,
                verticalAlign: null,
                y: null
            },
            stickyTracking: !1,
            tooltip: {
                distance: 6
            },
            threshold: 0
        }), ha = la(O, {
            type: "column",
            pointAttrToOptions: {
                stroke: "borderColor",
                fill: "color",
                r: "borderRadius"
            },
            cropShoulder: 0,
            trackerGroups: ["group", "dataLabelsGroup"],
            negStacks: !0,
            init: function() {
                O.prototype.init.apply(this, arguments);
                var a = this,
                    b = a.chart;
                b.hasRendered && q(b.series, function(b) {
                    b.type === a.type && (b.isDirty = !0)
                })
            },
            getColumnMetrics: function() {
                var f, h, a = this,
                    b = a.options,
                    c = a.xAxis,
                    d = a.yAxis,
                    e = c.reversed,
                    g = {},
                    i = 0;
                b.grouping === !1 ? i = 1 : q(a.chart.series, function(b) {
                    var c = b.options,
                        e = b.yAxis;
                    b.type === a.type && b.visible && d.len === e.len && d.pos === e.pos && (c.stacking ? (f = b.stackKey, g[f] === t && (g[f] = i++), h = g[f]) : c.grouping !== !1 && (h = i++), b.columnIndex = h)
                });
                var c = C(Q(c.transA) * (c.ordinalSlope || b.pointRange || c.closestPointRange || c.tickInterval || 1), c.len),
                    j = c * b.groupPadding,
                    k = (c - 2 * j) / i,
                    l = b.pointWidth,
                    b = s(l) ? (k - l) / 2 : k * b.pointPadding,
                    l = p(l, k - 2 * b);
                return a.columnMetrics = {
                    width: l,
                    offset: b + (j + ((e ? i - (a.columnIndex || 0) : a.columnIndex) || 0) * k - c / 2) * (e ? -1 : 1)
                }
            },
            translate: function() {
                var a = this,
                    b = a.chart,
                    c = a.options,
                    d = a.borderWidth = p(c.borderWidth, a.activePointCount > .5 * a.xAxis.len ? 0 : 1),
                    e = a.yAxis,
                    f = a.translatedThreshold = e.getThreshold(c.threshold),
                    g = p(c.minPointLength, 5),
                    h = a.getColumnMetrics(),
                    i = h.width,
                    j = a.barW = u(i, 1 + 2 * d),
                    k = a.pointXOffset = h.offset,
                    l = -(d % 2 ? .5 : 0),
                    m = d % 2 ? .5 : 1;
                b.renderer.isVML && b.inverted && (m += 1), c.pointPadding && (j = Ka(j)), O.prototype.translate.apply(a), q(a.points, function(c) {
                    var t, d = p(c.yBottom, f),
                        h = C(u(-999 - d, c.plotY), e.len + 999 + d),
                        q = c.plotX + k,
                        r = j,
                        s = C(h, d);
                    t = u(h, d) - s, Q(t) < g && g && (t = g, s = v(Q(s - f) > g ? d - g : f - (e.translate(c.y, 0, 1, 0, 1) <= f ? g : 0))), c.barX = q, c.pointWidth = i, c.tooltipPos = b.inverted ? [e.len - h, a.xAxis.len - q - r / 2] : [q + r / 2, h], r = v(q + r) + l, q = v(q) + l, r -= q, d = Q(s) < .5, t = v(s + t) + m, s = v(s) + m, t -= s, d && (s -= 1, t += 1), c.shapeType = "rect", c.shapeArgs = {
                        x: q,
                        y: s,
                        width: r,
                        height: t
                    }
                })
            },
            getSymbol: sa,
            drawLegendSymbol: M.drawRectangle,
            drawGraph: sa,
            drawPoints: function() {
                var f, g, a = this,
                    b = this.chart,
                    c = a.options,
                    d = b.renderer,
                    e = c.animationLimit || 250;
                q(a.points, function(h) {
                    var i = h.plotY,
                        j = h.graphic;
                    i === t || isNaN(i) || null === h.y ? j && (h.graphic = j.destroy()) : (f = h.shapeArgs, i = s(a.borderWidth) ? {
                        "stroke-width": a.borderWidth
                    } : {}, g = h.pointAttr[h.selected ? "select" : ""] || a.pointAttr[""], j ? (ab(j), j.attr(i)[b.pointCount < e ? "animate" : "attr"](w(f))) : h.graphic = d[h.shapeType](f).attr(g).attr(i).add(a.group).shadow(c.shadow, null, c.stacking && !c.borderRadius))
                })
            },
            animate: function(a) {
                var b = this.yAxis,
                    c = this.options,
                    d = this.chart.inverted,
                    e = {};
                ba && (a ? (e.scaleY = .001, a = C(b.pos + b.len, u(b.pos, b.toPixels(c.threshold))), d ? e.translateX = a - b.len : e.translateY = a, this.group.attr(e)) : (e.scaleY = 1, e[d ? "translateX" : "translateY"] = b.pos, this.group.animate(e, this.options.animation), this.animate = null))
            },
            remove: function() {
                var a = this,
                    b = a.chart;
                b.hasRendered && q(b.series, function(b) {
                    b.type === a.type && (b.isDirty = !0)
                }), O.prototype.remove.apply(a, arguments)
            }
        }), J.column = ha, ca.bar = w(ca.column), qa = la(ha, {
            type: "bar",
            inverted: !0
        }), J.bar = qa, ca.scatter = w(T, {
            lineWidth: 0,
            tooltip: {
                headerFormat: '<span style="color:{series.color}">●</span> <span style="font-size: 10px;"> {series.name}</span><br/>',
                pointFormat: "x: <b>{point.x}</b><br/>y: <b>{point.y}</b><br/>"
            },
            stickyTracking: !1
        }), qa = la(O, {
            type: "scatter",
            sorted: !1,
            requireSorting: !1,
            noSharedTooltip: !0,
            trackerGroups: ["markerGroup", "dataLabelsGroup"],
            takeOrdinalPosition: !1,
            singularTooltips: !0,
            drawGraph: function() {
                this.options.lineWidth && O.prototype.drawGraph.call(this)
            }
        }), J.scatter = qa, ca.pie = w(T, {
            borderColor: "#FFFFFF",
            borderWidth: 1,
            center: [null, null],
            clip: !1,
            colorByPoint: !0,
            dataLabels: {
                distance: 30,
                enabled: !0,
                formatter: function() {
                    return this.point.name
                }
            },
            ignoreHiddenPoint: !0,
            legendType: "point",
            marker: null,
            size: null,
            showInLegend: !1,
            slicedOffset: 10,
            states: {
                hover: {
                    brightness: .1,
                    shadow: !1
                }
            },
            stickyTracking: !1,
            tooltip: {
                followPointer: !0
            }
        }), T = {
            type: "pie",
            isCartesian: !1,
            pointClass: la(Ea, {
                init: function() {
                    Ea.prototype.init.apply(this, arguments);
                    var b, a = this;
                    return a.y < 0 && (a.y = null), r(a, {
                        visible: a.visible !== !1,
                        name: p(a.name, "Slice")
                    }), b = function(b) {
                        a.slice("select" === b.type)
                    }, N(a, "select", b), N(a, "unselect", b), a
                },
                setVisible: function(a) {
                    var b = this,
                        c = b.series,
                        d = c.chart;
                    b.visible = b.options.visible = a = a === t ? !b.visible : a, c.options.data[Da(b, c.data)] = b.options, q(["graphic", "dataLabel", "connector", "shadowGroup"], function(c) {
                        b[c] && b[c][a ? "show" : "hide"](!0)
                    }), b.legendItem && d.legend.colorizeItem(b, a), !c.isDirty && c.options.ignoreHiddenPoint && (c.isDirty = !0, d.redraw())
                },
                slice: function(a, b, c) {
                    var d = this.series;
                    Qa(c, d.chart), p(b, !0), this.sliced = this.options.sliced = a = s(a) ? a : !this.sliced, d.options.data[Da(this, d.data)] = this.options, a = a ? this.slicedTranslation : {
                        translateX: 0,
                        translateY: 0
                    }, this.graphic.animate(a), this.shadowGroup && this.shadowGroup.animate(a)
                },
                haloPath: function(a) {
                    var b = this.shapeArgs,
                        c = this.series.chart;
                    return this.sliced || !this.visible ? [] : this.series.chart.renderer.symbols.arc(c.plotLeft + b.x, c.plotTop + b.y, b.r + a, b.r + a, {
                        innerR: this.shapeArgs.r,
                        start: b.start,
                        end: b.end
                    })
                }
            }),
            requireSorting: !1,
            noSharedTooltip: !0,
            trackerGroups: ["group", "dataLabelsGroup"],
            axisTypes: [],
            pointAttrToOptions: {
                stroke: "borderColor",
                "stroke-width": "borderWidth",
                fill: "color"
            },
            singularTooltips: !0,
            getColor: sa,
            animate: function(a) {
                var b = this,
                    c = b.points,
                    d = b.startAngleRad;
                a || (q(c, function(a) {
                    var c = a.graphic,
                        a = a.shapeArgs;
                    c && (c.attr({
                        r: b.center[3] / 2,
                        start: d,
                        end: d
                    }), c.animate({
                        r: a.r,
                        start: a.start,
                        end: a.end
                    }, b.options.animation))
                }), b.animate = null)
            },
            setData: function(a, b, c, d) {
                O.prototype.setData.call(this, a, !1, c, d), this.processData(), this.generatePoints(), p(b, !0) && this.chart.redraw(c)
            },
            generatePoints: function() {
                var a, c, d, e, b = 0,
                    f = this.options.ignoreHiddenPoint;
                for (O.prototype.generatePoints.call(this), c = this.points, d = c.length, a = 0; d > a; a++) e = c[a], b += f && !e.visible ? 0 : e.y;
                for (this.total = b, a = 0; d > a; a++) e = c[a], e.percentage = b > 0 ? e.y / b * 100 : 0, e.total = b
            },
            translate: function(a) {
                this.generatePoints();
                var f, g, h, m, o, b = 0,
                    c = this.options,
                    d = c.slicedOffset,
                    e = d + c.borderWidth,
                    i = c.startAngle || 0,
                    j = this.startAngleRad = na / 180 * (i - 90),
                    i = (this.endAngleRad = na / 180 * (p(c.endAngle, i + 360) - 90)) - j,
                    k = this.points,
                    l = c.dataLabels.distance,
                    c = c.ignoreHiddenPoint,
                    n = k.length;
                for (a || (this.center = a = this.getCenter()), this.getX = function(b, c) {
                    return h = V.asin(C((b - a[1]) / (a[2] / 2 + l), 1)), a[0] + (c ? -1 : 1) * aa(h) * (a[2] / 2 + l)
                }, m = 0; n > m; m++) o = k[m], f = j + b * i, (!c || o.visible) && (b += o.percentage / 100), g = j + b * i, o.shapeType = "arc", o.shapeArgs = {
                    x: a[0],
                    y: a[1],
                    r: a[2] / 2,
                    innerR: a[3] / 2,
                    start: v(1e3 * f) / 1e3,
                    end: v(1e3 * g) / 1e3
                }, h = (g + f) / 2, h > 1.5 * na ? h -= 2 * na : -na / 2 > h && (h += 2 * na), o.slicedTranslation = {
                    translateX: v(aa(h) * d),
                    translateY: v(fa(h) * d)
                }, f = aa(h) * a[2] / 2, g = fa(h) * a[2] / 2, o.tooltipPos = [a[0] + .7 * f, a[1] + .7 * g], o.half = -na / 2 > h || h > na / 2 ? 1 : 0, o.angle = h, e = C(e, l / 2), o.labelPos = [a[0] + f + aa(h) * l, a[1] + g + fa(h) * l, a[0] + f + aa(h) * e, a[1] + g + fa(h) * e, a[0] + f, a[1] + g, 0 > l ? "center" : o.half ? "right" : "left", h]
            },
            drawGraph: null,
            drawPoints: function() {
                var c, d, f, g, a = this,
                    b = a.chart.renderer,
                    e = a.options.shadow;
                e && !a.shadowGroup && (a.shadowGroup = b.g("shadow").add(a.group)), q(a.points, function(h) {
                    d = h.graphic, g = h.shapeArgs, f = h.shadowGroup, e && !f && (f = h.shadowGroup = b.g("shadow").add(a.shadowGroup)), c = h.sliced ? h.slicedTranslation : {
                        translateX: 0,
                        translateY: 0
                    }, f && f.attr(c), d ? d.animate(r(g, c)) : h.graphic = d = b[h.shapeType](g).setRadialReference(a.center).attr(h.pointAttr[h.selected ? "select" : ""]).attr({
                        "stroke-linejoin": "round"
                    }).attr(c).add(a.group).shadow(e, f), void 0 !== h.visible && h.setVisible(h.visible)
                })
            },
            sortByAngle: function(a, b) {
                a.sort(function(a, d) {
                    return void 0 !== a.angle && (d.angle - a.angle) * b
                })
            },
            drawLegendSymbol: M.drawRectangle,
            getCenter: Z.getCenter,
            getSymbol: sa
        }, T = la(O, T), J.pie = T, O.prototype.drawDataLabels = function() {
            var f, g, h, i, a = this,
                b = a.options,
                c = b.cursor,
                d = b.dataLabels,
                e = a.points;
            (d.enabled || a._hasPointLabels) && (a.dlProcessOptions && a.dlProcessOptions(d), i = a.plotGroup("dataLabelsGroup", "data-labels", d.defer ? "hidden" : "visible", d.zIndex || 6), !a.hasRendered && p(d.defer, !0) && (i.attr({
                opacity: 0
            }), N(a, "afterAnimate", function() {
                a.visible && i.show(), i[b.animation ? "animate" : "attr"]({
                    opacity: 1
                }, {
                    duration: 200
                })
            })), g = d, q(e, function(b) {
                var e, m, n, l = b.dataLabel,
                    o = b.connector,
                    q = !0;
                if (f = b.options && b.options.dataLabels, e = p(f && f.enabled, g.enabled), l && !e) b.dataLabel = l.destroy();
                else if (e) {
                    if (d = w(g, f), e = d.rotation, m = b.getLabelConfig(), h = d.format ? Ia(d.format, m) : d.formatter.call(m, d), d.style.color = p(d.color, d.style.color, a.color, "black"), l) s(h) ? (l.attr({
                        text: h
                    }), q = !1) : (b.dataLabel = l = l.destroy(), o && (b.connector = o.destroy()));
                    else if (s(h)) {
                        l = {
                            fill: d.backgroundColor,
                            stroke: d.borderColor,
                            "stroke-width": d.borderWidth,
                            r: d.borderRadius || 0,
                            rotation: e,
                            padding: d.padding,
                            zIndex: 1
                        };
                        for (n in l) l[n] === t && delete l[n];
                        l = b.dataLabel = a.chart.renderer[e ? "text" : "label"](h, 0, -999, null, null, null, d.useHTML).attr(l).css(r(d.style, c && {
                            cursor: c
                        })).add(i).shadow(d.shadow)
                    }
                    l && a.alignDataLabel(b, l, d, null, q)
                }
            }))
        }, O.prototype.alignDataLabel = function(a, b, c, d, e) {
            var f = this.chart,
                g = f.inverted,
                h = p(a.plotX, -999),
                i = p(a.plotY, -999),
                j = b.getBBox();
            (a = this.visible && (a.series.forceDL || f.isInsidePlot(h, v(i), g) || d && f.isInsidePlot(h, g ? d.x + 1 : d.y + d.height - 1, g))) && (d = r({
                x: g ? f.plotWidth - i : h,
                y: v(g ? f.plotHeight - h : i),
                width: 0,
                height: 0
            }, d), r(c, {
                width: j.width,
                height: j.height
            }), c.rotation ? b[e ? "attr" : "animate"]({
                x: d.x + c.x + d.width / 2,
                y: d.y + c.y + d.height / 2
            }).attr({
                align: c.align
            }) : (b.align(c, null, d), g = b.alignAttr, "justify" === p(c.overflow, "justify") ? this.justifyDataLabel(b, c, g, j, d, e) : p(c.crop, !0) && (a = f.isInsidePlot(g.x, g.y) && f.isInsidePlot(g.x + j.width, g.y + j.height)))), a || (b.attr({
                y: -999
            }), b.placed = !1)
        }, O.prototype.justifyDataLabel = function(a, b, c, d, e, f) {
            var j, k, g = this.chart,
                h = b.align,
                i = b.verticalAlign;
            j = c.x, 0 > j && ("right" === h ? b.align = "left" : b.x = -j, k = !0), j = c.x + d.width, j > g.plotWidth && ("left" === h ? b.align = "right" : b.x = g.plotWidth - j, k = !0), j = c.y, 0 > j && ("bottom" === i ? b.verticalAlign = "top" : b.y = -j, k = !0), j = c.y + d.height, j > g.plotHeight && ("top" === i ? b.verticalAlign = "bottom" : b.y = g.plotHeight - j, k = !0), k && (a.placed = !f, a.align(b, null, e))
        }, J.pie && (J.pie.prototype.drawDataLabels = function() {
            var c, j, k, t, w, x, B, A, K, J, y, a = this,
                b = a.data,
                d = a.chart,
                e = a.options.dataLabels,
                f = p(e.connectorPadding, 10),
                g = p(e.connectorWidth, 1),
                h = d.plotWidth,
                i = d.plotHeight,
                l = p(e.softConnector, !0),
                m = e.distance,
                n = a.center,
                o = n[2] / 2,
                r = n[1],
                s = m > 0,
                z = [
                    [],
                    []
                ],
                R = [0, 0, 0, 0],
                N = function(a, b) {
                    return b.y - a.y
                };
            if (a.visible && (e.enabled || a._hasPointLabels)) {
                for (O.prototype.drawDataLabels.apply(a), q(b, function(a) {
                    a.dataLabel && a.visible && z[a.half].push(a)
                }), J = 2; J--;) {
                    var G, H = [],
                        M = [],
                        F = z[J],
                        L = F.length;
                    if (L) {
                        for (a.sortByAngle(F, J - .5), y = b = 0; !b && F[y];) b = F[y] && F[y].dataLabel && (F[y].dataLabel.getBBox().height || 21), y++;
                        if (m > 0) {
                            for (w = C(r + o + m, d.plotHeight), y = u(0, r - o - m); w >= y; y += b) H.push(y);
                            if (w = H.length, L > w) {
                                for (c = [].concat(F), c.sort(N), y = L; y--;) c[y].rank = y;
                                for (y = L; y--;) F[y].rank >= w && F.splice(y, 1);
                                L = F.length
                            }
                            for (y = 0; L > y; y++) {
                                c = F[y], x = c.labelPos, c = 9999;
                                var S, P;
                                for (P = 0; w > P; P++) S = Q(H[P] - x[1]), c > S && (c = S, G = P);
                                if (y > G && null !== H[y]) G = y;
                                else
                                    for (L - y + G > w && null !== H[y] && (G = w - L + y); null === H[G];) G++;
                                M.push({
                                    i: G,
                                    y: H[G]
                                }), H[G] = null
                            }
                            M.sort(N)
                        }
                        for (y = 0; L > y; y++) c = F[y], x = c.labelPos, t = c.dataLabel, K = c.visible === !1 ? "hidden" : "visible", c = x[1], m > 0 ? (w = M.pop(), G = w.i, A = w.y, (c > A && null !== H[G + 1] || A > c && null !== H[G - 1]) && (A = C(u(0, c), d.plotHeight))) : A = c, B = e.justify ? n[0] + (J ? -1 : 1) * (o + m) : a.getX(A === r - o - m || A === r + o + m ? c : A, J), t._attr = {
                            visibility: K,
                            align: x[6]
                        }, t._pos = {
                            x: B + e.x + ({
                                left: f,
                                right: -f
                            }[x[6]] || 0),
                            y: A + e.y - 10
                        }, t.connX = B, t.connY = A, null === this.options.size && (w = t.width, f > B - w ? R[3] = u(v(w - B + f), R[3]) : B + w > h - f && (R[1] = u(v(B + w - h + f), R[1])), 0 > A - b / 2 ? R[0] = u(v(-A + b / 2), R[0]) : A + b / 2 > i && (R[2] = u(v(A + b / 2 - i), R[2])))
                    }
                }(0 === Ba(R) || this.verifyDataLabelOverflow(R)) && (this.placeDataLabels(), s && g && q(this.points, function(b) {
                    j = b.connector, x = b.labelPos, (t = b.dataLabel) && t._pos ? (K = t._attr.visibility, B = t.connX, A = t.connY, k = l ? ["M", B + ("left" === x[6] ? 5 : -5), A, "C", B, A, 2 * x[2] - x[4], 2 * x[3] - x[5], x[2], x[3], "L", x[4], x[5]] : ["M", B + ("left" === x[6] ? 5 : -5), A, "L", x[2], x[3], "L", x[4], x[5]], j ? (j.animate({
                        d: k
                    }), j.attr("visibility", K)) : b.connector = j = a.chart.renderer.path(k).attr({
                        "stroke-width": g,
                        stroke: e.connectorColor || b.color || "#606060",
                        visibility: K
                    }).add(a.dataLabelsGroup)) : j && (b.connector = j.destroy())
                }))
            }
        }, J.pie.prototype.placeDataLabels = function() {
            q(this.points, function(a) {
                var b, a = a.dataLabel;
                a && ((b = a._pos) ? (a.attr(a._attr), a[a.moved ? "animate" : "attr"](b), a.moved = !0) : a && a.attr({
                    y: -999
                }))
            })
        }, J.pie.prototype.alignDataLabel = sa, J.pie.prototype.verifyDataLabelOverflow = function(a) {
            var f, b = this.center,
                c = this.options,
                d = c.center,
                e = c = c.minSize || 80;
            return null !== d[0] ? e = u(b[2] - u(a[1], a[3]), c) : (e = u(b[2] - a[1] - a[3], c), b[0] += (a[3] - a[1]) / 2), null !== d[1] ? e = u(C(e, b[2] - u(a[0], a[2])), c) : (e = u(C(e, b[2] - a[0] - a[2]), c), b[1] += (a[0] - a[2]) / 2), e < b[2] ? (b[2] = e, this.translate(b), q(this.points, function(a) {
                a.dataLabel && (a.dataLabel._pos = null)
            }), this.drawDataLabels && this.drawDataLabels()) : f = !0, f
        }), J.column && (J.column.prototype.alignDataLabel = function(a, b, c, d, e) {
            var f = this.chart,
                g = f.inverted,
                h = a.dlBox || a.shapeArgs,
                i = a.below || a.plotY > p(this.translatedThreshold, f.plotSizeY),
                j = p(c.inside, !!this.options.stacking);
            h && (d = w(h), g && (d = {
                x: f.plotWidth - d.y - d.height,
                y: f.plotHeight - d.x - d.width,
                width: d.height,
                height: d.width
            }), !j) && (g ? (d.x += i ? 0 : d.width, d.width = 0) : (d.y += i ? d.height : 0, d.height = 0)), c.align = p(c.align, !g || j ? "center" : i ? "right" : "left"), c.verticalAlign = p(c.verticalAlign, g || j ? "middle" : i ? "top" : "bottom"), O.prototype.alignDataLabel.call(this, a, b, c, d, e)
        }), T = S.TrackerMixin = {
            drawTrackerPoint: function() {
                var a = this,
                    b = a.chart,
                    c = b.pointer,
                    d = a.options.cursor,
                    e = d && {
                        cursor: d
                    },
                    f = function(c) {
                        var e, d = c.target;
                        for (b.hoverSeries !== a && a.onMouseOver(); d && !e;) e = d.point, d = d.parentNode;
                        e !== t && e !== b.hoverPoint && e.onMouseOver(c)
                    };
                q(a.points, function(a) {
                    a.graphic && (a.graphic.element.point = a), a.dataLabel && (a.dataLabel.element.point = a)
                }), a._hasTracking || (q(a.trackerGroups, function(b) {
                    a[b] && (a[b].addClass("highcharts-tracker").on("mouseover", f).on("mouseout", function(a) {
                        c.onTrackerMouseOut(a)
                    }).css(e), Za) && a[b].on("touchstart", f)
                }), a._hasTracking = !0)
            },
            drawTrackerGraph: function() {
                var m, a = this,
                    b = a.options,
                    c = b.trackByArea,
                    d = [].concat(c ? a.areaPath : a.graphPath),
                    e = d.length,
                    f = a.chart,
                    g = f.pointer,
                    h = f.renderer,
                    i = f.options.tooltip.snap,
                    j = a.tracker,
                    k = b.cursor,
                    l = k && {
                        cursor: k
                    },
                    k = a.singlePoints,
                    n = function() {
                        f.hoverSeries !== a && a.onMouseOver()
                    },
                    o = "rgba(192,192,192," + (ba ? 1e-4 : .002) + ")";
                if (e && !c)
                    for (m = e + 1; m--;) "M" === d[m] && d.splice(m + 1, 0, d[m + 1] - i, d[m + 2], "L"), (m && "M" === d[m] || m === e) && d.splice(m, 0, "L", d[m - 2] + i, d[m - 1]);
                for (m = 0; m < k.length; m++) e = k[m], d.push("M", e.plotX - i, e.plotY, "L", e.plotX + i, e.plotY);
                j ? j.attr({
                    d: d
                }) : (a.tracker = h.path(d).attr({
                    "stroke-linejoin": "round",
                    visibility: a.visible ? "visible" : "hidden",
                    stroke: o,
                    fill: c ? o : P,
                    "stroke-width": b.lineWidth + (c ? 0 : 2 * i),
                    zIndex: 2
                }).add(a.group), q([a.tracker, a.markerGroup], function(a) {
                    a.addClass("highcharts-tracker").on("mouseover", n).on("mouseout", function(a) {
                        g.onTrackerMouseOut(a)
                    }).css(l), Za && a.on("touchstart", n)
                }))
            }
        }, J.column && (ha.prototype.drawTracker = T.drawTrackerPoint), J.pie && (J.pie.prototype.drawTracker = T.drawTrackerPoint), J.scatter && (qa.prototype.drawTracker = T.drawTrackerPoint), r(kb.prototype, {
            setItemEvents: function(a, b, c, d, e) {
                var f = this;
                (c ? b : a.legendGroup).on("mouseover", function() {
                    a.setState("hover"), b.css(f.options.itemHoverStyle)
                }).on("mouseout", function() {
                    b.css(a.visible ? d : e), a.setState()
                }).on("click", function(b) {
                    var c = function() {
                            a.setVisible()
                        },
                        b = {
                            browserEvent: b
                        };
                    a.firePointEvent ? a.firePointEvent("legendItemClick", b, c) : K(a, "legendItemClick", b, c)
                })
            },
            createCheckboxForItem: function(a) {
                a.checkbox = $("input", {
                    type: "checkbox",
                    checked: a.selected,
                    defaultChecked: a.selected
                }, this.options.itemCheckboxStyle, this.chart.container), N(a.checkbox, "click", function(b) {
                    K(a, "checkboxClick", {
                        checked: b.target.checked
                    }, function() {
                        a.select()
                    })
                })
            }
        }), L.legend.itemStyle.cursor = "pointer", r(Xa.prototype, {
            showResetZoom: function() {
                var a = this,
                    b = L.lang,
                    c = a.options.chart.resetZoomButton,
                    d = c.theme,
                    e = d.states,
                    f = "chart" === c.relativeTo ? null : "plotBox";
                this.resetZoomButton = a.renderer.button(b.resetZoom, null, null, function() {
                    a.zoomOut()
                }, d, e && e.hover).attr({
                    align: c.position.align,
                    title: b.resetZoomTitle
                }).add().align(c.position, !1, f)
            },
            zoomOut: function() {
                var a = this;
                K(a, "selection", {
                    resetSelection: !0
                }, function() {
                    a.zoom()
                })
            },
            zoom: function(a) {
                var b, e, c = this.pointer,
                    d = !1;
                !a || a.resetSelection ? q(this.axes, function(a) {
                    b = a.zoom()
                }) : q(a.xAxis.concat(a.yAxis), function(a) {
                    var e = a.axis,
                        h = e.isXAxis;
                    (c[h ? "zoomX" : "zoomY"] || c[h ? "pinchX" : "pinchY"]) && (b = e.zoom(a.min, a.max), e.displayBtn && (d = !0))
                }), e = this.resetZoomButton, d && !e ? this.showResetZoom() : !d && da(e) && (this.resetZoomButton = e.destroy()), b && this.redraw(p(this.options.chart.animation, a && a.animation, this.pointCount < 100))
            },
            pan: function(a, b) {
                var e, c = this,
                    d = c.hoverPoints;
                d && q(d, function(a) {
                    a.setState()
                }), q("xy" === b ? [1, 0] : [1], function(b) {
                    var d = a[b ? "chartX" : "chartY"],
                        h = c[b ? "xAxis" : "yAxis"][0],
                        i = c[b ? "mouseDownX" : "mouseDownY"],
                        j = (h.pointRange || 0) / 2,
                        k = h.getExtremes(),
                        l = h.toValue(i - d, !0) + j,
                        i = h.toValue(i + c[b ? "plotWidth" : "plotHeight"] - d, !0) - j;
                    h.series.length && l > C(k.dataMin, k.min) && i < u(k.dataMax, k.max) && (h.setExtremes(l, i, !1, !1, {
                        trigger: "pan"
                    }), e = !0), c[b ? "mouseDownX" : "mouseDownY"] = d
                }), e && c.redraw(!1), A(c.container, {
                    cursor: "move"
                })
            }
        }), r(Ea.prototype, {
            select: function(a, b) {
                var c = this,
                    d = c.series,
                    e = d.chart,
                    a = p(a, !c.selected);
                c.firePointEvent(a ? "select" : "unselect", {
                    accumulate: b
                }, function() {
                    c.selected = c.options.selected = a, d.options.data[Da(c, d.data)] = c.options, c.setState(a && "select"), b || q(e.getSelectedPoints(), function(a) {
                        a.selected && a !== c && (a.selected = a.options.selected = !1, d.options.data[Da(a, d.data)] = a.options, a.setState(""), a.firePointEvent("unselect"))
                    })
                })
            },
            onMouseOver: function(a) {
                var b = this.series,
                    c = b.chart,
                    d = c.tooltip,
                    e = c.hoverPoint;
                e && e !== this && e.onMouseOut(), this.firePointEvent("mouseOver"), d && (!d.shared || b.noSharedTooltip) && d.refresh(this, a), this.setState("hover"), c.hoverPoint = this
            },
            onMouseOut: function() {
                var a = this.series.chart,
                    b = a.hoverPoints;
                this.firePointEvent("mouseOut"), b && -1 !== Da(this, b) || (this.setState(), a.hoverPoint = null)
            },
            importEvents: function() {
                if (!this.hasImportedEvents) {
                    var b, a = w(this.series.options.point, this.options).events;
                    this.events = a;
                    for (b in a) N(this, b, a[b]);
                    this.hasImportedEvents = !0
                }
            },
            setState: function(a, b) {
                var o, c = this.plotX,
                    d = this.plotY,
                    e = this.series,
                    f = e.options.states,
                    g = ca[e.type].marker && e.options.marker,
                    h = g && !g.enabled,
                    i = g && g.states[a],
                    j = i && i.enabled === !1,
                    k = e.stateMarkerGraphic,
                    l = this.marker || {},
                    m = e.chart,
                    n = e.halo,
                    a = a || "";
                o = this.pointAttr[a] || e.pointAttr[a], a === this.state && !b || this.selected && "select" !== a || f[a] && f[a].enabled === !1 || a && (j || h && i.enabled === !1) || a && l.states && l.states[a] && l.states[a].enabled === !1 || (this.graphic ? (g = g && this.graphic.symbolName && o.r, this.graphic.attr(w(o, g ? {
                    x: c - g,
                    y: d - g,
                    width: 2 * g,
                    height: 2 * g
                } : {})), k && k.hide()) : (a && i && (g = i.radius, l = l.symbol || e.symbol, k && k.currentSymbol !== l && (k = k.destroy()), k ? k[b ? "animate" : "attr"]({
                    x: c - g,
                    y: d - g
                }) : l && (e.stateMarkerGraphic = k = m.renderer.symbol(l, c - g, d - g, 2 * g, 2 * g).attr(o).add(e.markerGroup), k.currentSymbol = l)), k && k[a && m.isInsidePlot(c, d, m.inverted) ? "show" : "hide"]()), (c = f[a] && f[a].halo) && c.size ? (n || (e.halo = n = m.renderer.path().add(e.seriesGroup)), n.attr(r({
                    fill: ya(this.color || e.color).setOpacity(c.opacity).get()
                }, c.attributes))[b ? "animate" : "attr"]({
                    d: this.haloPath(c.size)
                })) : n && n.attr({
                    d: []
                }), this.state = a)
            },
            haloPath: function(a) {
                var b = this.series,
                    c = b.chart,
                    d = b.getPlotBox(),
                    e = c.inverted;
                return c.renderer.symbols.circle(d.translateX + (e ? b.yAxis.len - this.plotY : this.plotX) - a, d.translateY + (e ? b.xAxis.len - this.plotX : this.plotY) - a, 2 * a, 2 * a)
            }
        }), r(O.prototype, {
            onMouseOver: function() {
                var a = this.chart,
                    b = a.hoverSeries;
                b && b !== this && b.onMouseOut(), this.options.events.mouseOver && K(this, "mouseOver"), this.setState("hover"), a.hoverSeries = this
            },
            onMouseOut: function() {
                var a = this.options,
                    b = this.chart,
                    c = b.tooltip,
                    d = b.hoverPoint;
                d && d.onMouseOut(), this && a.events.mouseOut && K(this, "mouseOut"), c && !a.stickyTracking && (!c.shared || this.noSharedTooltip) && c.hide(), this.setState(), b.hoverSeries = null
            },
            setState: function(a) {
                var b = this.options,
                    c = this.graph,
                    d = this.graphNeg,
                    e = b.states,
                    b = b.lineWidth,
                    a = a || "";
                this.state !== a && (this.state = a, e[a] && e[a].enabled === !1 || (a && (b = e[a].lineWidth || b + (e[a].lineWidthPlus || 0)), c && !c.dashstyle && (a = {
                    "stroke-width": b
                }, c.attr(a), d && d.attr(a))))
            },
            setVisible: function(a, b) {
                var f, c = this,
                    d = c.chart,
                    e = c.legendItem,
                    g = d.options.chart.ignoreHiddenSeries,
                    h = c.visible;
                f = (c.visible = a = c.userOptions.visible = a === t ? !h : a) ? "show" : "hide", q(["group", "dataLabelsGroup", "markerGroup", "tracker"], function(a) {
                    c[a] && c[a][f]()
                }), d.hoverSeries === c && c.onMouseOut(), e && d.legend.colorizeItem(c, a), c.isDirty = !0, c.options.stacking && q(d.series, function(a) {
                    a.options.stacking && a.visible && (a.isDirty = !0)
                }), q(c.linkedSeries, function(b) {
                    b.setVisible(a, !1)
                }), g && (d.isDirtyBox = !0), b !== !1 && d.redraw(), K(c, f)
            },
            setTooltipPoints: function(a) {
                var c, d, h, i, b = [],
                    e = this.xAxis,
                    f = e && e.getExtremes(),
                    g = e ? e.tooltipLen || e.len : this.chart.plotSizeX,
                    j = [];
                if (this.options.enableMouseTracking !== !1 && !this.singularTooltips) {
                    for (a && (this.tooltipPoints = null), q(this.segments || this.points, function(a) {
                        b = b.concat(a)
                    }), e && e.reversed && (b = b.reverse()), this.orderTooltipPoints && this.orderTooltipPoints(b), a = b.length, i = 0; a > i; i++)
                        if (e = b[i], c = e.x, c >= f.min && c <= f.max)
                            for (h = b[i + 1], c = d === t ? 0 : d + 1, d = b[i + 1] ? C(u(0, U((e.clientX + (h ? h.wrappedClientX || h.clientX : g)) / 2)), g) : g; c >= 0 && d >= c;) j[c++] = e;
                    this.tooltipPoints = j
                }
            },
            show: function() {
                this.setVisible(!0)
            },
            hide: function() {
                this.setVisible(!1)
            },
            select: function(a) {
                this.selected = a = a === t ? !this.selected : a, this.checkbox && (this.checkbox.checked = a), K(this, a ? "select" : "unselect")
            },
            drawTracker: T.drawTrackerGraph
        }), r(S, {
            Axis: ma,
            Chart: Xa,
            Color: ya,
            Point: Ea,
            Tick: Sa,
            Renderer: Ya,
            Series: O,
            SVGElement: G,
            SVGRenderer: ta,
            arrayMin: Na,
            arrayMax: Ba,
            charts: W,
            dateFormat: bb,
            format: Ia,
            pathAnim: ub,
            getOptions: function() {
                return L
            },
            hasBidiBug: Nb,
            isTouchDevice: Hb,
            numberFormat: Ga,
            seriesTypes: J,
            setOptions: function(a) {
                return L = w(!0, L, a), Ab(), L
            },
            addEvent: N,
            removeEvent: X,
            createElement: $,
            discardElement: Pa,
            css: A,
            each: q,
            extend: r,
            map: Ua,
            merge: w,
            pick: p,
            splat: ra,
            extendClass: la,
            pInt: z,
            wrap: Ma,
            svg: ba,
            canvas: ga,
            vml: !ba && !ga,
            product: "Highcharts",
            version: "4.0.3"
        })
    }(), angular.module("highcharts-ng", []).directive("highchart", function() {
    function prependMethod(obj, method, func) {
        var original = obj[method];
        obj[method] = function() {
            var args = Array.prototype.slice.call(arguments);
            return func.apply(this, args), original ? original.apply(this, args) : void 0
        }
    }

    function deepExtend(destination, source) {
        for (var property in source) source[property] && source[property].constructor && source[property].constructor === Object ? (destination[property] = destination[property] || {}, deepExtend(destination[property], source[property])) : destination[property] = source[property];
        return destination
    }
    var indexOf = function(arr, find, i) {
            void 0 === i && (i = 0), 0 > i && (i += arr.length), 0 > i && (i = 0);
            for (var n = arr.length; n > i; i++)
                if (i in arr && arr[i] === find) return i;
            return -1
        },
        seriesId = 0,
        ensureIds = function(series) {
            var changed = !1;
            return angular.forEach(series, function(s) {
                angular.isDefined(s.id) || (s.id = "series-" + seriesId++, changed = !0)
            }), changed
        },
        axisNames = ["xAxis", "yAxis"],
        getMergedOptions = function(scope, element, config) {
            var mergedOptions = {},
                defaultOptions = {
                    chart: {
                        events: {}
                    },
                    title: {},
                    subtitle: {},
                    series: [],
                    credits: {},
                    plotOptions: {},
                    navigator: {
                        enabled: !1
                    }
                };
            return mergedOptions = config.options ? deepExtend(defaultOptions, config.options) : defaultOptions, mergedOptions.chart.renderTo = element[0], angular.forEach(axisNames, function(axisName) {
                angular.isDefined(config[axisName]) && (mergedOptions[axisName] = angular.copy(config[axisName]), (angular.isDefined(config[axisName].currentMin) || angular.isDefined(config[axisName].currentMax)) && (prependMethod(mergedOptions.chart.events, "selection", function(e) {
                    var thisChart = this;
                    scope.$apply(e[axisName] ? function() {
                        scope.config[axisName].currentMin = e[axisName][0].min, scope.config[axisName].currentMax = e[axisName][0].max
                    } : function() {
                        scope.config[axisName].currentMin = thisChart[axisName][0].dataMin, scope.config[axisName].currentMax = thisChart[axisName][0].dataMax
                    })
                }), prependMethod(mergedOptions.chart.events, "addSeries", function() {
                    scope.config[axisName].currentMin = this[axisName][0].min || scope.config[axisName].currentMin, scope.config[axisName].currentMax = this[axisName][0].max || scope.config[axisName].currentMax
                })))
            }), config.title && (mergedOptions.title = config.title), config.subtitle && (mergedOptions.subtitle = config.subtitle), config.credits && (mergedOptions.credits = config.credits), config.size && (config.size.width && (mergedOptions.chart.width = config.size.width), config.size.height && (mergedOptions.chart.height = config.size.height)), mergedOptions
        },
        updateZoom = function(axis, modelAxis) {
            var extremes = axis.getExtremes();
            (modelAxis.currentMin !== extremes.dataMin || modelAxis.currentMax !== extremes.dataMax) && axis.setExtremes(modelAxis.currentMin, modelAxis.currentMax, !1)
        },
        processExtremes = function(chart, axis, axisName) {
            (axis.currentMin || axis.currentMax) && chart[axisName][0].setExtremes(axis.currentMin, axis.currentMax, !0)
        },
        chartOptionsWithoutEasyOptions = function(options) {
            return angular.extend({}, options, {
                data: null,
                visible: null
            })
        };
    return {
        restrict: "EAC",
        replace: !0,
        template: "<div></div>",
        scope: {
            config: "="
        },
        link: function(scope, element) {
            var prevSeriesOptions = {},
                processSeries = function(series) {
                    var i, ids = [];
                    if (series) {
                        var setIds = ensureIds(series);
                        if (setIds) return !1;
                        if (angular.forEach(series, function(s) {
                            ids.push(s.id);
                            var chartSeries = chart.get(s.id);
                            chartSeries ? angular.equals(prevSeriesOptions[s.id], chartOptionsWithoutEasyOptions(s)) ? (void 0 !== s.visible && chartSeries.visible !== s.visible && chartSeries.setVisible(s.visible, !1), chartSeries.setData(angular.copy(s.data), !1)) : chartSeries.update(angular.copy(s), !1) : chart.addSeries(angular.copy(s), !1), prevSeriesOptions[s.id] = chartOptionsWithoutEasyOptions(s)
                        }), scope.config.noData) {
                            var chartContainsData = !1;
                            for (i = 0; i < series.length; i++)
                                if (series[i].data && series[i].data.length > 0) {
                                    chartContainsData = !0;
                                    break
                                }
                            chartContainsData ? chart.hideLoading() : chart.showLoading(scope.config.noData)
                        }
                    }
                    for (i = chart.series.length - 1; i >= 0; i--) {
                        var s = chart.series[i];
                        indexOf(ids, s.options.id) < 0 && s.remove(!1)
                    }
                    return !0
                },
                chart = !1,
                initChart = function() {
                    chart && chart.destroy(), prevSeriesOptions = {};
                    var config = scope.config || {},
                        mergedOptions = getMergedOptions(scope, element, config);
                    chart = config.useHighStocks ? new Highcharts.StockChart(mergedOptions) : new Highcharts.Chart(mergedOptions);
                    for (var i = 0; i < axisNames.length; i++) config[axisNames[i]] && processExtremes(chart, config[axisNames[i]], axisNames[i]);
                    config.loading && chart.showLoading()
                };
            initChart(), scope.$watch("config.series", function(newSeries) {
                var needsRedraw = processSeries(newSeries);
                needsRedraw && chart.redraw()
            }, !0), scope.$watch("config.title", function(newTitle) {
                chart.setTitle(newTitle, !0)
            }, !0), scope.$watch("config.subtitle", function(newSubtitle) {
                chart.setTitle(!0, newSubtitle)
            }, !0), scope.$watch("config.loading", function(loading) {
                loading ? chart.showLoading() : chart.hideLoading()
            }), scope.$watch("config.credits.enabled", function(enabled) {
                enabled ? chart.credits.show() : chart.credits && chart.credits.hide()
            }), scope.$watch("config.useHighStocks", function(useHighStocks, oldUseHighStocks) {
                useHighStocks !== oldUseHighStocks && initChart()
            }), angular.forEach(axisNames, function(axisName) {
                scope.$watch("config." + axisName, function(newAxes, oldAxes) {
                    newAxes !== oldAxes && newAxes && (chart[axisName][0].update(newAxes, !1), updateZoom(chart[axisName][0], angular.copy(newAxes)), chart.redraw())
                }, !0)
            }), scope.$watch("config.options", function(newOptions, oldOptions, scope) {
                newOptions !== oldOptions && (initChart(), processSeries(scope.config.series), chart.redraw())
            }, !0), scope.$watch("config.size", function(newSize, oldSize) {
                newSize !== oldSize && newSize && chart.setSize(newSize.width || void 0, newSize.height || void 0)
            }, !0), scope.$on("highchartsng.reflow", function() {
                chart.reflow()
            }), scope.$on("$destroy", function() {
                chart && chart.destroy(), element.remove()
            })
        }
    }
}), angular.module("sample.widgets.github", ["adf.provider", "highcharts-ng"]).value("githubApiUrl", "https://api.github.com/repos/").config(["dashboardProvider",
    function(dashboardProvider) {
        var widget = {
            templateUrl: "scripts/widgets/github/github.html",
            reload: !0,
            resolve: {
                commits: function(githubService, config) {
                    return config.path ? githubService.get(config.path) : void 0
                }
            },
            edit: {
                templateUrl: "scripts/widgets/github/edit.html"
            }
        };
        dashboardProvider.widget("githubHistory", angular.extend({
            title: "Github History",
            description: "Display the commit history of a GitHub project as chart",
            controller: "githubHistoryCtrl"
        }, widget)).widget("githubAuthor", angular.extend({
            title: "Github Author",
            description: "Displays the commits per author as pie chart",
            controller: "githubAuthorCtrl"
        }, widget))
    }
]).service("githubService", ["$q", "$http", "githubApiUrl",
    function($q, $http, githubApiUrl) {
        return {
            get: function(path) {
                var deferred = $q.defer(),
                    url = githubApiUrl + path + "/commits?callback=JSON_CALLBACK";
                return $http.jsonp(url).success(function(data) {
                    data && data.data ? deferred.resolve(data.data) : deferred.reject()
                }).error(function() {
                    deferred.reject()
                }), deferred.promise
            }
        }
    }
]).controller("githubHistoryCtrl", ["$scope", "config", "commits",
    function($scope, config, commits) {
        function parseDate(input) {
            var parts = input.split("-");
            return Date.UTC(parts[0], parts[1] - 1, parts[2])
        }
        var data = {};
        angular.forEach(commits, function(commit) {
            var day = commit.commit.author.date;
            day = day.substring(0, day.indexOf("T")), data[day] ? data[day] ++ : data[day] = 1
        });
        var seriesData = [];
        angular.forEach(data, function(count, day) {
            seriesData.push([parseDate(day), count])
        }), seriesData.sort(function(a, b) {
            return a[0] - b[0]
        }), commits && ($scope.chartConfig = {
            chart: {
                type: "spline"
            },
            title: {
                text: "Github commit history"
            },
            xAxis: {
                type: "datetime"
            },
            yAxis: {
                title: {
                    text: "Commits"
                },
                min: 0
            },
            series: [{
                name: config.path,
                data: seriesData
            }]
        })
    }
]).controller("githubAuthorCtrl", ["$scope", "config", "commits",
    function($scope, config, commits) {
        var data = {};
        angular.forEach(commits, function(commit) {
            var author = commit.commit.author.name;
            data[author] ? data[author] ++ : data[author] = 1
        });
        var seriesData = [];
        if (angular.forEach(data, function(count, author) {
            seriesData.push([author, count])
        }), seriesData.length > 0) {
            seriesData.sort(function(a, b) {
                return b[1] - a[1]
            });
            var s = seriesData[0];
            seriesData[0] = {
                name: s[0],
                y: s[1],
                sliced: !0,
                selected: !0
            }
        }
        commits && ($scope.chartConfig = {
            chart: {
                plotBackgroundColor: null,
                plotBorderWidth: null,
                plotShadow: !1
            },
            title: {
                text: config.path
            },
            plotOptions: {
                pie: {
                    allowPointSelect: !0,
                    cursor: "pointer",
                    dataLabels: {
                        enabled: !0,
                        color: "#000000",
                        connectorColor: "#000000",
                        format: "<b>{point.name}</b>: {point.percentage:.1f} %"
                    }
                }
            },
            series: [{
                type: "pie",
                name: config.path,
                data: seriesData
            }]
        })
    }
]), angular.module("sample.widgets.teamtaskworkload", ["adf.provider", "highcharts-ng"]).config(["dashboardProvider",
    function(dashboardProvider) {
        var widget = {
            templateUrl: "scripts/widgets/teamtaskworkload/teamtaskworkload.html",
            reload: !0,
            resolve: {
                tasks: function(teamTaskWorkloadService) {
                    return teamTaskWorkloadService.getTasks()
                }
            },
            edit: {
                templateUrl: "scripts/widgets/teamtaskworkload/edit.html"
            }
        };
        dashboardProvider.widget("teamTaskWorkload", angular.extend({
            title: "Team Task Workload",
            description: "Displays tasks per user as pie chart",
            controller: "teamTaskWorkloadCtrl"
        }, widget))
    }
]).service("teamTaskWorkloadService", ["$q", "$http",
    function($q, $http) {
        return {
            getTasks: function() {
                var deferred = $q.defer(),
                    url = App.Object.getContextPath() + "/api/latest/plugin/task/list";
                return $http.get(url).success(function(data) {
                    data ? deferred.resolve(data) : deferred.reject()
                }).error(function() {
                    deferred.reject()
                }), deferred.promise
            }
        }
    }
]).controller("teamTaskWorkloadCtrl", ["$scope", "config", "tasks",
    function($scope, config, tasks) {
        var data = {};
        angular.forEach(tasks, function(task) {
            var user = task.assignee;
            data[user] ? data[user] ++ : data[user] = 1
        });
        var seriesData = [];
        if (angular.forEach(data, function(count, user) {
            seriesData.push([user, count])
        }), seriesData.length > 0) {
            seriesData.sort(function(a, b) {
                return b[1] - a[1]
            });
            var s = seriesData[0];
            seriesData[0] = {
                name: s[0],
                y: s[1],
                sliced: !0,
                selected: !0
            }
        }
        tasks && ($scope.chartConfig = {
            chart: {
                plotBackgroundColor: null,
                plotBorderWidth: null,
                plotShadow: !1
            },
            title: {
                text: config.path
            },
            plotOptions: {
                pie: {
                    allowPointSelect: !0,
                    cursor: "pointer",
                    dataLabels: {
                        enabled: !0,
                        color: "#000000",
                        connectorColor: "#000000",
                        format: "<b>{point.name}</b>: {point.percentage:.1f} %"
                    }
                }
            },
            series: [{
                type: "pie",
                name: config.path,
                data: seriesData
            }]
        })
    }
]), angular.module("adf").run(["$templateCache",
    function($templateCache) {
        "use strict";
        $templateCache.put("../src/templates/dashboard-edit.html", '<div class="modal-header"><button type="button" class="close" ng-click="closeDialog()" aria-hidden="true">&times;</button><h4 class="modal-title">Edit Dashboard</h4></div><div class="modal-body"><form role="form"><div class="form-group"><label for="dashboardTitle">Title</label><input type="text" class="form-control" id="dashboardTitle" ng-model="model.title" required></div><div class="form-group"><label>Structure</label><div class="radio" ng-repeat="(key, structure) in structures"><label><input type="radio" value="{{key}}" ng-model="model.structure" ng-change="changeStructure(key, structure)">{{key}}</label></div></div></form></div><div class="modal-footer"><button type="button" class="btn btn-primary" ng-click="closeDialog()">Close</button></div>'), $templateCache.put("../src/templates/dashboard.html", '<div class="dashboard-container"><h1>{{model.title}} <span style="font-size: 16px" class="pull-right"><a href="" ng-if="editMode" title="add new widget" ng-click="addWidgetDialog()"><i class="fa fa-plus-circle"></i></a> <a href="" ng-if="editMode" title="edit dashboard" ng-click="editDashboardDialog()"><i class="fa fa-cog"></i></a> <a href="" title="{{editMode ? \'disable edit mode\' : \'enable edit mode\'}}" ng-click="toggleEditMode()"><i class="fa fa-edit"></i></a></span></h1><div class="dashboard" ng-class="editClass"><div ng-repeat="row in model.rows" class="row" ng-class="row.styleClass"><div ng-repeat="col in row.columns" class="column" ng-class="col.styleClass" ui-sortable="sortableOptions" ng-model="col.widgets"><div class="widgets" ng-repeat="definition in col.widgets"><adf-widget definition="definition" column="col" edit-mode="{{editMode}}" collapsible="collapsible"></div></div></div></div></div>'), $templateCache.put("../src/templates/widget-add.html", '<div class="modal-header"><button type="button" class="close" ng-click="closeDialog()" aria-hidden="true">&times;</button><h4 class="modal-title">Add new widget</h4></div><div class="modal-body"><div style="display: inline-block"><dl class="dl-horizontal"><dt ng-repeat-start="(key, widget) in widgets"><a href="" ng-click="addWidget(key)">{{widget.title}}</a></dt><dd ng-repeat-end="" ng-if="widget.description">{{widget.description}}</dd></dl></div></div><div class="modal-footer"><button type="button" class="btn btn-primary" ng-click="closeDialog()">Close</button></div>'), $templateCache.put("../src/templates/widget-edit.html", '<div class="modal-header"><button type="button" class="close" ng-click="closeDialog()" aria-hidden="true">&times;</button><h4 class="modal-title">{{widget.title}}</h4></div><div class="modal-body"><form role="form"><div class="form-group"><label for="widgetTitle">Title</label><input type="text" class="form-control" id="widgetTitle" ng-model="definition.title" placeholder="Enter title" required></div></form><div ng-if="widget.edit"><adf-widget-content model="definition" content="widget.edit"></div></div><div class="modal-footer"><button type="button" class="btn btn-primary" ng-click="closeDialog()">Close</button></div>'), $templateCache.put("../src/templates/widget.html", '<div class="widget panel panel-default"><div class="panel-heading"><h3 class="panel-title">{{definition.title}} <span class="pull-right"><a href="" title="reload widget content" ng-if="widget.reload" ng-click="reload()"><i class="fa fa-refresh"></i></a>  <a href="" title="change widget location" ng-if="editMode"><i class="fa fa-arrows"></i></a>  <a href="" title="collapse widget" ng-show="collapsible && !isCollapsed" ng-click="isCollapsed = !isCollapsed"><i class="fa fa-minus"></i></a>  <a href="" title="expand widget" ng-show="collapsible && isCollapsed" ng-click="isCollapsed = !isCollapsed"><i class="fa fa-plus"></i></a>  <a href="" title="edit widget configuration" ng-click="edit()" ng-if="editMode"><i class="fa fa-cog"></i></a>  <a href="" title="remove widget" ng-click="close()" ng-if="editMode"><i class="fa fa-times"></i></a></span></h3></div><div class="panel-body" collapse="isCollapsed"><adf-widget-content model="definition" content="widget"></div></div>')
    }
]), angular.module("sample").run(["$templateCache",
    function($templateCache) {
        "use strict";
        $templateCache.put("scripts/widgets/github/edit.html", '<form role="form"><div class="form-group"><label for="path">Github Repository Path</label><input type="text" class="form-control" id="path" ng-model="config.path" placeholder="Enter Path (username/reponame)"></div></form>'), $templateCache.put("scripts/widgets/github/github.html", '<div><div class="alert alert-info" ng-if="!chartConfig">Please insert a repository path in the widget configuration</div><div ng-if="chartConfig"><highchart id="chart1" config="chartConfig"></highchart></div></div>'), $templateCache.put("scripts/widgets/linklist/edit.html", '<form class="form-inline" role="form"><div><label>Links</label></div><div class="padding-bottom" ng-repeat="link in config.links"><div class="form-group"><label class="sr-only" for="title-{{$index}}">Title</label><input type="text" id="title-{{$index}}" class="form-control" placeholder="Title" ng-model="link.title" required></div><div class="form-group"><label class="sr-only" for="href-{{$index}}">URL</label><input type="url" id="href-{{$index}}" class="form-control" placeholder="http://example.com" ng-model="link.href" required></div><button type="button" class="btn btn-warning" ng-click="removeLink($index)"><i class="fa fa-minus"></i> Remove</button></div><button type="button" class="btn btn-primary" ng-click="addLink()"><i class="fa fa-plus"></i> Add</button></form>'), $templateCache.put("scripts/widgets/linklist/linklist.html", '<div class="linklist"><ul><li ng-repeat="link in links | orderBy:\'title\'"><a target="_blank" ng-href="{{link.href}}">{{link.title}}</a></li></ul></div>'), $templateCache.put("scripts/widgets/markdown/edit.html", '<form role="form"><div class="form-group"><label for="content">Markdown content</label><textarea id="content" class="form-control" rows="5" ng-model="config.content"></textarea></div></form>'), $templateCache.put("scripts/widgets/markdown/markdown.html", '<div class="markdown" btf-markdown="config.content"></div>'), $templateCache.put("scripts/widgets/mycomplaints/edit.html", '<form role="form"><div class="form-group"><label for="url">Feed url</label><input type="url" class="form-control" id="url" ng-model="config.url" placeholder="Enter feed url"></div></form>'), $templateCache.put("scripts/widgets/mycomplaints/mycomplaints.html", '<div class="mycomplaints"><div ng-controller="myComplaintsCtrl"><table ng-table="tableParams" show-filter="true" class="table"><tr ng-repeat="complaint in $data"><td data-title="\'ID\'" sortable>{{complaint.complaintId}}</td><td data-title="\'Title\'" sortable filter="{ \'title\': \'text\' }">{{complaint.complaintTitle}}</td><td data-title="\'Priority\'" sortable filter="{ \'priority\': \'text\' }">{{complaint.priority}}</td><td data-title="\'Created\'" sortable filter="{ \'created\': \'text\' }">{{complaint.created}}</td><td data-title="\'Status\'" sortable>{{complaint.status}}</td></tr></table></div></div>'), $templateCache.put("scripts/widgets/mytasks/edit.html", '<form role="form"><div class="form-group"><label for="url">Feed url</label><input type="url" class="form-control" id="url" ng-model="config.url" placeholder="Enter feed url"></div></form>'), $templateCache.put("scripts/widgets/mytasks/mytasks.html", '<div class="mytasks"><div ng-controller="myTasksCtrl"><table ng-table="tableParams" show-filter="true" class="table"><tr ng-repeat="task in $data"><td data-title="\'ID\'" sortable>{{task.taskId}}</td><td data-title="\'Title\'" sortable filter="{ \'title\': \'text\' }">{{task.title}}</td><td data-title="\'Priority\'" sortable filter="{ \'priority\': \'text\' }">{{task.priority}}</td><td data-title="\'Due\'" sortable filter="{ \'due\': \'text\' }">{{task.dueDate}}</td><td data-title="\'Status\'" sortable>{{task.completed}}</td></tr></table></div></div>'), $templateCache.put("scripts/widgets/news/edit.html", '<form role="form"><div class="form-group"><label for="url">Feed url</label><input type="url" class="form-control" id="url" ng-model="config.url" placeholder="Enter feed url"></div></form>'), $templateCache.put("scripts/widgets/news/news.html", '<div class="news"><div class="alert alert-info" ng-if="!feed">Please insert a feed url in the widget configuration</div><h4><a ng-href="{{feed.link}}" target="_blank">{{feed.title}}</a></h4><ul><li ng-repeat="entry in feed.entries"><a ng-href="{{entry.link}}" target="_blank">{{entry.title}}</a></li></ul></div>'), $templateCache.put("scripts/widgets/randommsg/randommsg.html", "<blockquote><p>{{msg.text}}</p><small>{{msg.author}}</small></blockquote>"), $templateCache.put("scripts/widgets/teamtaskworkload/edit.html", '<form role="form"><div class="form-group"><label for="path">Team Task Workload</label><input type="text" class="form-control" id="path" ng-model="config.path" placeholder="Enter Path (username/reponame)"></div></form>'), $templateCache.put("scripts/widgets/teamtaskworkload/teamtaskworkload.html", '<div><div class="alert alert-info" ng-if="!chartConfig">Please insert a repository path in the widget configuration</div><div ng-if="chartConfig"><highchart id="chart1" config="chartConfig"></highchart></div></div>'), $templateCache.put("scripts/widgets/weather/edit.html", '<form role="form"><div class="form-group"><label for="location">Location</label><input type="location" class="form-control" id="location" ng-model="config.location" placeholder="Enter location"></div></form>'), $templateCache.put("scripts/widgets/weather/weather.html", '<div class="text-center"><div class="alert alert-info" ng-if="!data">Please insert a location in the widget configuration</div><div class="weather" ng-if="data"><h4>{{data.name}} ({{data.sys.country}})</h4><dl><dt>Temprature:</dt><dd>{{data.main.temp | number:2}}</dd></dl></div></div>'), $templateCache.put("partials/sample.html", '<adf-dashboard name="{{name}}" structure="4-8" adf-model="model">')
    }
]);