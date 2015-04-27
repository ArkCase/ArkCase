var FlowController = {
    rules: {
        "#frevvo-form-footer": function(a) {
            _frevvo.uberController.flow = true;
            FlowController.setup(a);
        }
    },
    saveBtn: null,
    loadBtn: null,
    setup: function(a) {
        this.saveBtn = FlowView.getSaveButton();
        this.save.setup(this.saveBtn);
    },
    save: {
        setup: function(a) {
            FEvent.observe(a, "click", this.doIt.bindAsObserver(this, a));
            FEvent.observe(a, "keydown", this.doIt.bindAsObserver(this, a));
        },
        doIt: function(a, b) {
            if (FlowController.event.isActivationEvent(a)) {
                Event.stop(a);
                if (Element.hasClassName(b, "s-disabled")) {
                    return false;
                }
                _frevvo.formController.auth.doIt({
                    el: b,
                    goOn: this.goOn,
                    action: "getforms"
                }, JSONUtil.objectToJSONString({
                    id: Flows.flowId(),
                    flowId: Flows.flowId()
                }));
            }
        },
        goOn: function(a) {
            Flows.saveFlow("save", FlowController.save.success, FlowController.save.failure, {
                el: a
            });
        },
        success: function(t, json, options) {
            var jstate = eval(t.responseText);
            var status = _frevvo.protoView.$childByClass(options.el.parentNode, "f-buttons-status");
            if (!status) {
                status = $("f-mobile-status");
            }
            if (jstate.status & FrevvoConstants.SAVE_SUCCESS) {
                if (status) {
                    status.innerHTML = _frevvo.localeStrings.savesuccessful;
                }
                if (_frevvo.utilities.util.isDefined(window.CustomFlowEventHandlers) && CustomFlowEventHandlers.onSaveSuccess) {
                    CustomFlowEventHandlers.onSaveSuccess(jstate.submissionId);
                }
            } else {
                if (jstate.status & FrevvoConstants.SAVE_OU_SUCCESS) {
                    if (_frevvo.utilities.util.isDefined(window.CustomFlowEventHandlers) && CustomFlowEventHandlers.onSaveSuccess) {
                        CustomFlowEventHandlers.onSaveSuccess(jstate.submissionId);
                    }
                    if ($("_frevvo-taskworkarea-frameless")) {
                        var url = jstate.redirectUrl;
                        url += (jstate.redirectUrl.indexOf("?") === -1) ? "?" : "&";
                        url += "_frameless=true";
                        _frevvo.utilities.ajaxRequest.send(url, {
                            method: "get",
                            onSuccess: function(t) {
                                $("_frevvo-taskworkarea-frameless").innerHTML = t.responseText;
                            }
                        });
                        TaskPoller.doRefresh();
                    } else {
                        window.location.href = jstate.redirectUrl;
                    }
                    return;
                } else {
                    if (status) {
                        status.innerHTML = _frevvo.localeStrings.savefailed + ": Status " + jstate.status;
                    }
                    if (_frevvo.utilities.util.isDefined(window.CustomFlowEventHandlers) && CustomFlowEventHandlers.onSaveFailure) {
                        CustomFlowEventHandlers.onSaveFailure();
                    }
                }
            }
            _frevvo.formController.showTimedStatus(status);
            if (parent.TaskPoller) {
                parent.TaskPoller.doRefresh();
            }
        },
        failure: function(d, c, b) {
            var a = _frevvo.protoView.$childByClass(b.el.parentNode, "f-buttons-status");
            if (a) {
                a.innerHTML = _frevvo.localeStrings.saveerror;
            }
            if (_frevvo.utilities.util.isDefined(window.CustomFlowEventHandlers) && CustomFlowEventHandlers.onSaveFailure) {
                CustomFlowEventHandlers.onSaveFailure();
            }
        }
    },
    load: {
        setup: function(a) {
            FEvent.observe(a, "click", this.doIt.bindAsObserver(this, a));
        },
        doIt: function(a, b) {
            Event.stop(a);
            _frevvo.formController.auth.doIt({
                el: b,
                goOn: this.goOn,
                action: "getforms"
            }, JSONUtil.objectToJSONString({
                id: Flows.flowId()
            }));
        },
        goOn: function(a) {
            var b = {
                flowId: Flows.flowId()
            };
            Flows.getFlows(FlowController.load.success, FlowController.load.failure, {
                el: a,
                locale: _frevvo.localeStrings.locale
            }, encodeURIComponent(JSONUtil.objectToJSONString(b)));
        },
        success: function(t, json, options) {
            var jstate = eval(t.responseText);
            if (jstate && jstate.submissionId) {
                var url = document.URL;
                var index = url.search(/&_submission=[A-Za-z0-9_-]+/);
                if (index < 0) {
                    url = url + "&_submission=" + jstate.submissionId;
                }
                window.location.href = url;
            } else {
                if (jstate && jstate.tasklist) {
                    window.location.href = jstate.tasklistUrl;
                } else {
                    var status = _frevvo.protoView.$childByClass(options.el.parentNode, "f-buttons-status");
                    if (status) {
                        status.innerHTML = _frevvo.localeStrings.saveerror;
                    }
                    if (_frevvo.utilities.util.isDefined(window.CustomFlowEventHandlers) && CustomFlowEventHandlers.onSaveFailure) {
                        CustomFlowEventHandlers.onSaveFailure();
                    }
                }
            }
        },
        failure: function(d, c, b) {
            var a = _frevvo.protoView.$childByClass(b.el.parentNode, "f-buttons-status");
            if (a) {
                a.innerHTML = _frevvo.localeStrings.savefailed;
            }
            if (_frevvo.utilities.util.isDefined(window.CustomFlowEventHandlers) && CustomFlowEventHandlers.onSaveFailure) {
                CustomFlowEventHandlers.onSaveFailure();
            }
        }
    },
    next: {
        handleClick: function(a) {
            FlowController.next.doIt(a);
        },
        handleKbActivation: function(a, b) {
            if (a) {
                if (FlowController.event.isActivationEvent(a)) {
                    Event.stop(a);
                    FlowController.next.doIt(b);
                    return false;
                }
            }
            return true;
        },
        doIt: function(b) {
            if (Element.hasClassName(b, "s-disabled")) {
                return false;
            }
            if (_frevvo.utilities.util.isDefined(window.CustomFlowEventHandlers) && CustomFlowEventHandlers.onNextClicked) {
                CustomFlowEventHandlers.onNextClicked(b.getAttribute("aname"), b.getAttribute("aid"));
            }
            Element.addClassName($("page-form"), "s-submitted");
            Element.addClassName(b, "s-disabled");
            var a = _frevvo.protoView && _frevvo.protoView.$childByClass(b, "facade");
            a && (a.innerHTML = _frevvo.localeStrings.pleasewait);
            FlowController.doOp(b.href);
        }
    },
    fastForward: {
        handleClick: function(a) {
            FlowController.fastForward.doIt(a);
        },
        handleKbActivation: function(a, b) {
            if (a) {
                if (FlowController.event.isActivationEvent(a)) {
                    Event.stop(a);
                    FlowController.fastForward.doIt(b);
                    return false;
                }
            }
            return true;
        },
        doIt: function(b) {
            if (Element.hasClassName(b, "s-disabled")) {
                return false;
            }
            if (_frevvo.utilities.util.isDefined(window.CustomFlowEventHandlers) && CustomFlowEventHandlers.onForceFinishClicked) {
                CustomFlowEventHandlers.onFastForwardClicked(b.getAttribute("aname"), b.getAttribute("aid"));
            }
            Element.addClassName($("page-form"), "s-submitted");
            Element.addClassName(b, "s-disabled");
            var a = _frevvo.protoView.$childByClass(b, "facade");
            a.innerHTML = _frevvo.localeStrings.pleasewait;
            FlowController.doOp(b.href);
        }
    },
    reject: {
        pendingRejectCancel: false,
        el: null,
        handleClick: function(b, a) {
            FlowController.reject.doIt(b, a);
        },
        handleKbActivation: function(a, b) {
            if (a) {
                if (FlowController.event.isActivationEvent(a)) {
                    Event.stop(a);
                    FlowController.reject.doIt(b);
                    return false;
                }
            }
            return true;
        },
        doIt: function(c, a) {
            var b;
            if (Element.hasClassName(c, "s-disabled")) {
                return false;
            }
            b = FlowController.getFlowBaseUrl() + "/reject?locale=" + _frevvo.localeStrings.locale;
            FlowController.reject.el = c;
            if (_frevvo.utilities.util.isDefined(window.CustomFlowEventHandlers) && CustomFlowEventHandlers.onRejectClicked) {
                CustomFlowEventHandlers.onRejectClicked(c.getAttribute("aname"), c.getAttribute("aid"));
            }
            if (a) {
                var d = MUtil.device();
                if (d) {
                    b += "?" + d;
                }
                document.location.href = b;
            } else {
                _frevvo.lightBoxView.showPage(b, "Rejection", "s-flowreject-lightbox", {
                    callback: this.showDone
                }, null, document.activeElement);
            }
        }
    },
    previous: {
        handleClick: function(a) {
            FlowController.previous.doIt(a);
        },
        handleKbActivation: function(a, b) {
            if (a) {
                if (FlowController.event.isActivationEvent(a)) {
                    Event.stop(a);
                    FlowController.previous.doIt(b);
                    return false;
                }
            }
            return true;
        },
        doIt: function(a) {
            if (Element.hasClassName(a, "s-disabled")) {
                return false;
            }
            if (_frevvo.utilities.util.isDefined(window.CustomFlowEventHandlers) && CustomFlowEventHandlers.onPreviousClicked) {
                CustomFlowEventHandlers.onPreviousClicked(a.getAttribute("aname"), a.getAttribute("aid"));
            }
            Element.addClassName($("page-form"), "s-submitted");
            Element.addClassName(a, "s-disabled");
            a.innerHTML = _frevvo.localeStrings.pleasewait;
            window.location.href = a.href;
        }
    },
    nav: {
        handleClick: function(b, a, c) {
            FlowController.nav.doIt(b);
        },
        handleKbActivation: function(a, c, b, d) {
            if (a) {
                if (FlowController.event.isActivationEvent(a)) {
                    Event.stop(a);
                    FlowController.nav.doIt(c, b, d);
                    return false;
                }
            }
            return true;
        },
        doIt: function(b, a, c) {
            if (Element.hasClassName(b, "s-disabled")) {
                return false;
            }
            if (_frevvo.utilities.util.isDefined(window.CustomFlowEventHandlers) && CustomFlowEventHandlers.onNavClicked) {
                CustomFlowEventHandlers.onNavClicked(a, c);
            }
            FlowController.doOp(b.href);
        }
    },
    getFlowBaseUrl: function() {
        return _frevvo.forms.formUrl().substr(0, _frevvo.forms.formUrl().indexOf("/current"));
    },
    doOp: function(a) {
        var c;
        var b;
        var d;
        this.doOpInProgress = true;
        var e = this;
        if ($("_frevvo-taskworkarea-frameless")) {
            c = FlowController.getFlowBaseUrl() + a.substr(a.indexOf("?"));
            if (c.indexOf("_frameless") === -1) {
                c += "&_frameless=true";
            }
            b = function(f) {
                e.doOpInProgress = false;
                if (f.responseText.indexOf('<div id="form-container"') === -1) {
                    if (f.responseText.indexOf("http") === 0) {
                        document.getElementById("wa-iframe").children[1].src = f.responseText;
                        _frevvo.workAreaView.showIFrame();
                    } else {
                        $("_frevvo-taskworkarea-frameless").innerHTML = f.responseText;
                    }
                } else {
                    _frevvo.api.attachFlowsNextFormHtml(f.responseText, "_frevvo-taskworkarea-frameless");
                    setTimeout("_frevvo.workAreaView.sizeHeight();", 500);
                }
                TaskPoller.doRefresh();
            };
            d = function() {
                e.doOpInProgress = false;
            };
            _frevvo.utilities.ajaxRequest.send(c, {
                method: "get",
                onSuccess: b,
                onFailure: d
            });
        } else {
            window.location.href = a;
        }
    },
    edit: {
        handleActivation: function(a, b) {
            a = a || window.event;
            if (FlowController.event.isActivationEvent(a)) {
                this.doIt(b);
            }
        },
        doIt: function(a) {
            FlowController.doOp(a.getAttribute("purl"));
        }
    },
    print: {
        handlePrint: function(a) {
            a = a || window.event;
            if (FlowController.event.isActivationEvent(a)) {
                window.print();
                return false;
            }
            return true;
        }
    },
    event: {
        isActivationEvent: function(a) {
            if (a) {
                return (a.type == "click" || (a.type == "keydown" && (a.keyCode == "13" || a.keyCode == "32")));
            }
            return false;
        }
    },
};
var FlowView = {
    getFlowButton: function() {
        return ($("flow-button"));
    },
    getPreviousButton: function() {
        return ($("flow-previous-button"));
    },
    getSaveButton: function() {
        return ($("flow-save-button"));
    },
    getRejectButton: function() {
        return ($("flow-reject-button"));
    },
    getFastForwardButton: function() {
        return ($("flow-fast-forward-button"));
    }
};
var FlowModel = {
    moveActivity: function(a, g, e, i, b) {
        var d = BaseFlowView.getView(g);
        var f = BaseFlowView.getView(a);
        var c = d.getId(g);
        var h = f.getId(a);
        Flows.moveActivity(h, c, e, i, b, {
            before: e,
            src: a,
            dest: g
        });
    },
    insertPaletteItem: function(c, f, e, g, b) {
        var a = null;
        if (f) {
            var d = BaseFlowView.getView(f);
            a = d.getId(f);
        }
        Flows.createActivity(c.getAttribute("id"), a, e, g, b, {
            before: e,
            paletteItem: c,
            activity: f
        });
    },
    linkActivity: function(d, e, c) {
        var a = BaseFlowView.getView(d);
        var b = a.getId(d);
        Flows.linkActivity(b, e, c, {
            activity: d
        });
    },
    removeActivity: function(e, f, d, c) {
        var a = BaseFlowView.getView(e);
        var b = a.getId(e);
        c || (c = {});
        Object.extend(c, {
            activity: e
        });
        Flows.removeActivity(b, f, d, c);
    },
    loadControlChildren: function(f, a, d, e, c, b) {
        Flows.loadControlChildren(f, a, d, e, c, b);
    },
    reloadFlowOutlineTree: function(c, b, a) {
        Flows.reloadFlowOutlineTree(c, b, a);
    },
    notifyExpansionStateChange: function(h, a, f, b, c, g, e, d) {
        Flows.notifyExpansionStateChange(h, a, f, b, c, g, e, d);
    }
};
var Flows = {
    flowId: function() {
        var b = document.URL.indexOf("/frevvo/web");
        var a = /\/frevvo\/web\/tn\/([^\/]*)\/user\/([^\/]*)\/app\/([^\/]*)\/flow\/([^\/]*)/.exec(document.URL.substring(b));
        return (a && a[4].split("?")[0]) || null;
    },
    taskListUrl: function() {
        var d = gup("embed");
        var b = document.URL.indexOf("/frevvo/web");
        var c = document.URL.substring(0, b);
        var a = /(\/frevvo\/web\/tn\/([^\/]*)\/user\/([^\/]*))/.exec(document.URL.substring(b));
        return c + a[1] + "/subject/tasks" + (d != null ? "?embed=true" : "");
    },
    flowTypeUrl: function() {
        var c = gup("typeId");
        var b = document.URL.indexOf("/frevvo/web");
        var d = document.URL.substring(0, b);
        var a = /(\/frevvo\/web\/tn\/([^\/]*)\/user\/([^\/]*)\/app\/([^\/]*))/.exec(document.URL.substring(b));
        return d + a[1] + "/flowtype/" + gup("typeId");
    },
    flowUrl: function() {
        var b = (_frevvo.forms) ? _frevvo.forms.formUrl() : document.URL;
        var a = b.indexOf("/flow/");
        if (a > -1) {
            var c = b.indexOf("/", a + 6);
            if (c > -1) {
                return b.substring(0, c);
            }
            c = b.indexOf("?", a + 6);
            if (c > -1) {
                return b.substring(0, c);
            }
            c = b.indexOf("#", a + 6);
            if (c > -1) {
                return b.substring(0, c);
            }
        }
        return b;
    },
    activityUrl: function(a) {
        url = this.flowUrl() + "/activity";
        if (a && a.length > 0) {
            url = url + "/" + a;
        }
        return url;
    },
    activityTypeUrl: function(a) {
        return this.activityUrl(a) + "/type";
    },
    saveFlow: function(b, e, d, a) {
        var c = this.flowUrl() + "?nextActivityTypeId=" + b;
        Object.extend(a, {
            method: "post",
            onSuccess: e,
            onFailure: d,
            onException: this.onException
        });
        _frevvo.utilities.ajaxRequest.send(c, a);
    },
    runFlow: function(b, e, d, a) {
        var c = this.flowUrl() + "?nextActivityTypeId=" + b;
        Object.extend(a, {
            method: "post",
            onSuccess: e,
            onFailure: d,
            onException: this.onException
        });
        _frevvo.utilities.ajaxRequest.send(c, a);
    },
    finishFlow: function(b, e, d, a) {
        var c = this.flowUrl() + "?_method=post&nextActivityTypeId=" + b;
        Object.extend(a, {
            method: "post",
            onSuccess: e,
            onFailure: d,
            onException: this.onException
        });
        _frevvo.utilities.ajaxRequest.send(c, a);
    },
    dispose: function(d, c, a) {
        var b = this.flowUrl() + "?dispose=true&render=false&_method=get&random=" + Math.random();
        Object.extend(a, {
            method: "get",
            ignorePageError: true,
            onSuccess: d,
            onFailure: c,
            onException: this.onException
        });
        _frevvo.utilities.ajaxRequest.send(b, a);
    },
    updateFlow: function(d, e, c, a) {
        var b = this.flowUrl() + "/type?edit=true&state=" + _frevvo.utilities.util.escapePlus(d);
        Object.extend(a, {
            method: "put",
            onSuccess: e,
            onFailure: c,
            onException: this.onException
        });
        _frevvo.utilities.ajaxRequest.send(b, a);
    },
    getFlows: function(e, c, a, d) {
        var b = this.flowTypeUrl() + "/flows";
        if (d != null) {
            b += "?state=" + d + "&random=" + Math.random();
        }
        if (a.locale) {
            b += "&locale=" + a.locale;
        }
        b = _frevvo.forms.addReferrerUrlAndIframeId(b);
        Object.extend(a, {
            method: "get",
            onSuccess: e,
            onFailure: c,
            onException: this.onException
        });
        _frevvo.utilities.ajaxRequest.send(b, a);
    },
    editFlowType: function(d, c, a) {
        var b = this.flowUrl() + "/editor?edit=true&random=" + Math.random();
        Object.extend(a, {
            method: "get",
            onSuccess: d,
            onFailure: c,
            onException: this.onException
        });
        _frevvo.utilities.ajaxRequest.send(b, a);
    },
    editActivity: function(a, e, d, b) {
        var c = this.activityUrl(a) + "/editor?edit=true&random=" + Math.random();
        Object.extend(b, {
            method: "get",
            onSuccess: e,
            onFailure: d,
            onException: this.onException
        });
        _frevvo.utilities.ajaxRequest.send(c, b);
    },
    updateActivity: function(a, e, f, d, b) {
        var c = this.activityUrl(a) + "/type?edit=true&state=" + _frevvo.utilities.util.escapePlus(e);
        Object.extend(b, {
            method: "put",
            onSuccess: f,
            onFailure: d,
            onException: this.onException
        });
        _frevvo.utilities.ajaxRequest.send(c, b);
    },
    moveActivity: function(e, g, d, f, c, a) {
        var b = this.activityTypeUrl(g) + "?edit=true&sourceId=" + e + "&action=move" + (d ? "&before=true" : "");
        Object.extend(a, {
            method: "post",
            onSuccess: f,
            onFailure: c,
            onException: this.onException
        });
        _frevvo.utilities.ajaxRequest.send(b, a);
    },
    linkActivity: function(a, e, d, b) {
        var c = this.activityUrl(a) + "/palette?edit=true&typeId=link-form";
        Object.extend(b, {
            method: "post",
            onSuccess: e,
            onFailure: d,
            onException: this.onException
        });
        _frevvo.utilities.ajaxRequest.send(c, b);
    },
    removeActivity: function(a, e, d, b) {
        var c = this.activityTypeUrl(a) + "?edit=true";
        Object.extend(b, {
            method: "delete",
            onSuccess: e,
            onFailure: d,
            onException: this.onException
        });
        _frevvo.utilities.ajaxRequest.send(c, b);
    },
    createActivity: function(e, a, f, g, d, b) {
        var c = (a != null ? this.activityUrl(a) : this.flowUrl()) + "/palette?edit=true&typeId=" + e + (f ? "&before=true" : "");
        Object.extend(b, {
            method: "post",
            onSuccess: g,
            onFailure: d,
            onException: this.onException
        });
        _frevvo.utilities.ajaxRequest.send(c, b);
    },
    loadControlChildren: function(h, a, f, g, e, c) {
        var b = false;
        if (c.mappingMode) {
            b = true;
        }
        var d = this.flowUrl() + "/activityFormType/" + a + "/defaultform/" + h;
        d += "/control/" + f + "/outlineTree?random=" + Math.random() + "&mappingMode=" + b;
        Object.extend(c, {
            method: "get",
            onSuccess: g,
            onFailure: e,
            onException: this.onException
        });
        _frevvo.utilities.ajaxRequest.send(d, c);
    },
    reloadFlowOutlineTree: function(e, d, b) {
        var a = false;
        if (b.mappingMode) {
            a = true;
        }
        var c = this.flowUrl() + "/outlineTree?random=" + Math.random() + "&mappingMode=" + a;
        Object.extend(b, {
            method: "get",
            onSuccess: e,
            onFailure: d,
            onException: this.onException
        });
        _frevvo.utilities.ajaxRequest.send(c, b);
    },
    notifyExpansionStateChange: function(g, f, h, b, c, d, a, i) {
        var e = this.flowUrl() + "/activityFormType/" + f + "/defaultform/" + g + "/control/" + h + "/outlineTree?nodeType=" + b + "&expanded=" + c + "&random=" + Math.random();
        Object.extend(i, {
            method: "put",
            onSuccess: d,
            onFailure: a,
            onException: this.onException
        });
        _frevvo.utilities.ajaxRequest.send(e, i);
    }
};
var Behaviour = {
    list: new Array,
    register: function(a) {
        Behaviour.list.push(a);
    },
    start: function() {
        Behaviour.addLoadEvent(function() {
            console.log("behavior.js Behavior.start");
            Behaviour.apply();
        });
    },
    apply: function() {
        var d = null;
        var a = null;
        var c = null;
        var f = null;
        for (var e = 0; d = Behaviour.list[e]; e++) {
            for (a in d) {
                f = document.getElementsBySelector(a);
                if (!f) {
                    continue;
                }
                for (var b = 0; c = f[b]; b++) {
                    d[a](c);
                }
            }
        }
    },
    addLoadEvent: function(a) {
        var b = window.onload;
        if (typeof window.onload != "function") {
            console.log("no existing onload");
            window.onload = a;
        } else {
            window.onload = function() {
                b();
                a();
            };
        }
    }
};

function getAllChildren(a) {
    return a.all ? a.all : a.getElementsByTagName("*");
}
document.getElementsBySelector = function(u) {
    if (!document.getElementsByTagName) {
        return new Array();
    }
    var o = u.split(" ");
    var f = new Array(document);
    for (var w = 0; w < o.length; w++) {
        token = o[w].replace(/^\s+/, "").replace(/\s+$/, "");
        if (token.indexOf("#") > -1) {
            var r = token.split("#");
            var d = r[0];
            var q = r[1];
            var b = document.getElementById(q);
            if (d && b.nodeName.toLowerCase() != d) {
                return new Array();
            }
            f = new Array(b);
            continue;
        }
        if (token.indexOf(".") > -1) {
            var r = token.split(".");
            var d = r[0];
            var c = r[1];
            if (!d) {
                d = "*";
            }
            var l = new Array;
            var g = 0;
            for (var x = 0; x < f.length; x++) {
                var m;
                if (d == "*") {
                    m = getAllChildren(f[x]);
                } else {
                    m = f[x].getElementsByTagName(d);
                }
                for (var t = 0; t < m.length; t++) {
                    l[g++] = m[t];
                }
            }
            f = new Array;
            var p = 0;
            for (var s = 0; s < l.length; s++) {
                if (l[s].className && l[s].className.match(new RegExp("\\b" + c + "\\b"))) {
                    f[p++] = l[s];
                }
            }
            continue;
        }
        if (token.match(/^(\w*)\[(\w+)([=~\|\^\$\*]?)=?"?([^\]"]*)"?\]$/)) {
            var d = RegExp.$1;
            var v = RegExp.$2;
            var a = RegExp.$3;
            var n = RegExp.$4;
            if (!d) {
                d = "*";
            }
            var l = new Array;
            var g = 0;
            for (var x = 0; x < f.length; x++) {
                var m;
                if (d == "*") {
                    m = getAllChildren(f[x]);
                } else {
                    m = f[x].getElementsByTagName(d);
                }
                for (var t = 0; t < m.length; t++) {
                    l[g++] = m[t];
                }
            }
            f = new Array;
            var p = 0;
            var e;
            switch (a) {
                case "=":
                    e = function(h) {
                        return (h.getAttribute(v) == n);
                    };
                    break;
                case "~":
                    e = function(h) {
                        return (h.getAttribute(v).match(new RegExp("\\b" + n + "\\b")));
                    };
                    break;
                case "|":
                    e = function(h) {
                        return (h.getAttribute(v).match(new RegExp("^" + n + "-?")));
                    };
                    break;
                case "^":
                    e = function(h) {
                        return (h.getAttribute(v).indexOf(n) == 0);
                    };
                    break;
                case "$":
                    e = function(h) {
                        return (h.getAttribute(v).lastIndexOf(n) == h.getAttribute(v).length - n.length);
                    };
                    break;
                case "*":
                    e = function(h) {
                        return (h.getAttribute(v).indexOf(n) > -1);
                    };
                    break;
                default:
                    e = function(h) {
                        return h.getAttribute(v);
                    };
            }
            f = new Array;
            var p = 0;
            for (var s = 0; s < l.length; s++) {
                if (e(l[s])) {
                    f[p++] = l[s];
                }
            }
            continue;
        }
        if (!f[0]) {
            return;
        }
        d = token;
        var l = new Array;
        var g = 0;
        for (var x = 0; x < f.length; x++) {
            var m = f[x].getElementsByTagName(d);
            for (var t = 0; t < m.length; t++) {
                l[g++] = m[t];
            }
        }
        f = l;
    }
    return f;
};
var Prototype = {
    Version: "1.5.0_rc1",
    ScriptFragment: "(?:<script.*?>)((\n|\r|.)*?)(?:<\/script>)",
    emptyFunction: function() {},
    K: function(a) {
        return a;
    }
};
var Class = {
    create: function() {
        return function() {
            this.initialize.apply(this, arguments);
        };
    }
};
var Abstract = new Object();
Object.extend = function(a, c) {
    for (var b in c) {
        a[b] = c[b];
    }
    return a;
};
Object.extend(Object, {
    inspect: function(a) {
        try {
            if (a == undefined) {
                return "undefined";
            }
            if (a == null) {
                return "null";
            }
            return a.inspect ? a.inspect() : a.toString();
        } catch (b) {
            if (b instanceof RangeError) {
                return "...";
            }
            throw b;
        }
    },
    keys: function(a) {
        var b = [];
        for (var c in a) {
            b.push(c);
        }
        return b;
    },
    values: function(b) {
        var a = [];
        for (var c in b) {
            a.push(b[c]);
        }
        return a;
    },
    clone: function(a) {
        return Object.extend({}, a);
    }
});
Function.prototype.bind = function() {
    var a = this,
        c = $A(arguments),
        b = c.shift();
    return function() {
        return a.apply(b, c.concat($A(arguments)));
    };
};
Function.prototype.bindAsEventListener = function(c) {
    var a = this,
        b = $A(arguments),
        c = b.shift();
    return function(d) {
        return a.apply(c, [(d || window.event)].concat(b).concat($A(arguments)));
    };
};
Object.extend(Number.prototype, {
    toColorPart: function() {
        var a = this.toString(16);
        if (this < 16) {
            return "0" + a;
        }
        return a;
    },
    succ: function() {
        return this + 1;
    },
    times: function(a) {
        $R(0, this, true).each(a);
        return this;
    }
});
var Try = {
    these: function() {
        var c;
        for (var b = 0; b < arguments.length; b++) {
            var a = arguments[b];
            try {
                c = a();
                break;
            } catch (d) {}
        }
        return c;
    }
};
var PeriodicalExecuter = Class.create();
PeriodicalExecuter.prototype = {
    initialize: function(b, a) {
        this.callback = b;
        this.frequency = a;
        this.currentlyExecuting = false;
        this.registerCallback();
    },
    registerCallback: function() {
        this.timer = setInterval(this.onTimerEvent.bind(this), this.frequency * 1000);
    },
    stop: function() {
        if (!this.timer) {
            return;
        }
        clearInterval(this.timer);
        this.timer = null;
    },
    onTimerEvent: function() {
        if (!this.currentlyExecuting) {
            try {
                this.currentlyExecuting = true;
                this.callback(this);
            } finally {
                this.currentlyExecuting = false;
            }
        }
    }
};
Object.extend(String.prototype, {
    gsub: function(e, c) {
        var a = "",
            d = this,
            b;
        c = arguments.callee.prepareReplacement(c);
        while (d.length > 0) {
            if (b = d.match(e)) {
                a += d.slice(0, b.index);
                a += (c(b) || "").toString();
                d = d.slice(b.index + b[0].length);
            } else {
                a += d, d = "";
            }
        }
        return a;
    },
    sub: function(c, a, b) {
        a = this.gsub.prepareReplacement(a);
        b = b === undefined ? 1 : b;
        return this.gsub(c, function(d) {
            if (--b < 0) {
                return d[0];
            }
            return a(d);
        });
    },
    scan: function(b, a) {
        this.gsub(b, a);
        return this;
    },
    truncate: function(b, a) {
        b = b || 30;
        a = a === undefined ? "..." : a;
        return this.length > b ? this.slice(0, b - a.length) + a : this;
    },
    strip: function() {
        return this.replace(/^\s+/, "").replace(/\s+$/, "");
    },
    stripTags: function() {
        return this.replace(/<\/?[^>]+>/gi, "");
    },
    stripScripts: function() {
        return this.replace(new RegExp(Prototype.ScriptFragment, "img"), "");
    },
    extractScripts: function() {
        var b = new RegExp(Prototype.ScriptFragment, "img");
        var a = new RegExp(Prototype.ScriptFragment, "im");
        return (this.match(b) || []).map(function(c) {
            return (c.match(a) || ["", ""])[1];
        });
    },
    evalScripts: function() {
        return this.extractScripts().map(function(script) {
            return eval(script);
        });
    },
    escapeHTML: function() {
        var b = document.createElement("div");
        var a = document.createTextNode(this);
        b.appendChild(a);
        return b.innerHTML;
    },
    unescapeHTML: function() {
        var a = document.createElement("div");
        a.innerHTML = this.stripTags();
        return a.childNodes[0] ? a.childNodes[0].nodeValue : "";
    },
    toQueryParams: function() {
        var a = this.match(/^\??(.*)$/)[1].split("&");
        return a.inject({}, function(e, b) {
            var d = b.split("=");
            var c = d[1] ? decodeURIComponent(d[1]) : undefined;
            e[decodeURIComponent(d[0])] = c;
            return e;
        });
    },
    toArray: function() {
        return this.split("");
    },
    camelize: function() {
        var d = this.split("-");
        if (d.length == 1) {
            return d[0];
        }
        var b = this.indexOf("-") == 0 ? d[0].charAt(0).toUpperCase() + d[0].substring(1) : d[0];
        for (var c = 1, a = d.length; c < a; c++) {
            var e = d[c];
            b += e.charAt(0).toUpperCase() + e.substring(1);
        }
        return b;
    },
    inspect: function(b) {
        var a = this.replace(/\\/g, "\\\\");
        if (b) {
            return '"' + a.replace(/"/g, '\\"') + '"';
        } else {
            return "'" + a.replace(/'/g, "\\'") + "'";
        }
    }
});
String.prototype.gsub.prepareReplacement = function(b) {
    if (typeof b == "function") {
        return b;
    }
    var a = new Template(b);
    return function(c) {
        return a.evaluate(c);
    };
};
String.prototype.parseQuery = String.prototype.toQueryParams;
var Template = Class.create();
Template.Pattern = /(^|.|\r|\n)(#\{(.*?)\})/;
Template.prototype = {
    initialize: function(a, b) {
        this.template = a.toString();
        this.pattern = b || Template.Pattern;
    },
    evaluate: function(a) {
        return this.template.gsub(this.pattern, function(b) {
            var c = b[1];
            if (c == "\\") {
                return b[2];
            }
            return c + (a[b[3]] || "").toString();
        });
    }
};
var $break = new Object();
var $continue = new Object();
var Enumerable = {
    each: function(b) {
        var a = 0;
        try {
            this._each(function(d) {
                try {
                    b(d, a++);
                } catch (f) {
                    if (f != $continue) {
                        throw f;
                    }
                }
            });
        } catch (c) {
            if (c != $break) {
                throw c;
            }
        }
    },
    all: function(b) {
        var a = true;
        this.each(function(d, c) {
            a = a && !!(b || Prototype.K)(d, c);
            if (!a) {
                throw $break;
            }
        });
        return a;
    },
    any: function(b) {
        var a = false;
        this.each(function(d, c) {
            if (a = !!(b || Prototype.K)(d, c)) {
                throw $break;
            }
        });
        return a;
    },
    collect: function(b) {
        var a = [];
        this.each(function(d, c) {
            a.push(b(d, c));
        });
        return a;
    },
    detect: function(b) {
        var a;
        this.each(function(d, c) {
            if (b(d, c)) {
                a = d;
                throw $break;
            }
        });
        return a;
    },
    findAll: function(b) {
        var a = [];
        this.each(function(d, c) {
            if (b(d, c)) {
                a.push(d);
            }
        });
        return a;
    },
    grep: function(c, b) {
        var a = [];
        this.each(function(f, e) {
            var d = f.toString();
            if (d.match(c)) {
                a.push((b || Prototype.K)(f, e));
            }
        });
        return a;
    },
    include: function(a) {
        var b = false;
        this.each(function(c) {
            if (c == a) {
                b = true;
                throw $break;
            }
        });
        return b;
    },
    inject: function(a, b) {
        this.each(function(d, c) {
            a = b(a, d, c);
        });
        return a;
    },
    invoke: function(b) {
        var a = $A(arguments).slice(1);
        return this.collect(function(c) {
            return c[b].apply(c, a);
        });
    },
    max: function(b) {
        var a;
        this.each(function(d, c) {
            d = (b || Prototype.K)(d, c);
            if (a == undefined || d >= a) {
                a = d;
            }
        });
        return a;
    },
    min: function(b) {
        var a;
        this.each(function(d, c) {
            d = (b || Prototype.K)(d, c);
            if (a == undefined || d < a) {
                a = d;
            }
        });
        return a;
    },
    partition: function(c) {
        var b = [],
            a = [];
        this.each(function(e, d) {
            ((c || Prototype.K)(e, d) ? b : a).push(e);
        });
        return [b, a];
    },
    pluck: function(b) {
        var a = [];
        this.each(function(d, c) {
            a.push(d[b]);
        });
        return a;
    },
    reject: function(b) {
        var a = [];
        this.each(function(d, c) {
            if (!b(d, c)) {
                a.push(d);
            }
        });
        return a;
    },
    sortBy: function(a) {
        return this.collect(function(c, b) {
            return {
                value: c,
                criteria: a(c, b)
            };
        }).sort(function(f, e) {
            var d = f.criteria,
                c = e.criteria;
            return d < c ? -1 : d > c ? 1 : 0;
        }).pluck("value");
    },
    toArray: function() {
        return this.collect(Prototype.K);
    },
    zip: function() {
        var b = Prototype.K,
            a = $A(arguments);
        if (typeof a.last() == "function") {
            b = a.pop();
        }
        var c = [this].concat(a).map($A);
        return this.map(function(e, d) {
            return b(c.pluck(d));
        });
    },
    inspect: function() {
        return "#<Enumerable:" + this.toArray().inspect() + ">";
    }
};
Object.extend(Enumerable, {
    map: Enumerable.collect,
    find: Enumerable.detect,
    select: Enumerable.findAll,
    member: Enumerable.include,
    entries: Enumerable.toArray
});
var $A = Array.from = function(c) {
    if (!c) {
        return [];
    }
    if (c.toArray) {
        return c.toArray();
    } else {
        var b = [];
        for (var a = 0; a < c.length; a++) {
            b.push(c[a]);
        }
        return b;
    }
};
Object.extend(Array.prototype, Enumerable);
if (!Array.prototype._reverse) {
    Array.prototype._reverse = Array.prototype.reverse;
}
Object.extend(Array.prototype, {
    _each: function(b) {
        for (var a = 0; a < this.length; a++) {
            b(this[a]);
        }
    },
    clear: function() {
        this.length = 0;
        return this;
    },
    first: function() {
        return this[0];
    },
    last: function() {
        return this[this.length - 1];
    },
    compact: function() {
        return this.select(function(a) {
            return a != undefined || a != null;
        });
    },
    flatten: function() {
        return this.inject([], function(b, a) {
            return b.concat(a && a.constructor == Array ? a.flatten() : [a]);
        });
    },
    without: function() {
        var a = $A(arguments);
        return this.select(function(b) {
            return !a.include(b);
        });
    },
    indexOf: function(a) {
        for (var b = 0; b < this.length; b++) {
            if (this[b] == a) {
                return b;
            }
        }
        return -1;
    },
    reverse: function(a) {
        return (a !== false ? this : this.toArray())._reverse();
    },
    reduce: function() {
        return this.length > 1 ? this : this[0];
    },
    uniq: function() {
        return this.inject([], function(b, a) {
            return b.include(a) ? b : b.concat([a]);
        });
    },
    inspect: function() {
        return "[" + this.map(Object.inspect).join(", ") + "]";
    }
});
var Hash = {
    _each: function(b) {
        for (var a in this) {
            var c = this[a];
            if (typeof c == "function") {
                continue;
            }
            var d = [a, c];
            d.key = a;
            d.value = c;
            b(d);
        }
    },
    keys: function() {
        return this.pluck("key");
    },
    values: function() {
        return this.pluck("value");
    },
    merge: function(a) {
        return $H(a).inject($H(this), function(b, c) {
            b[c.key] = c.value;
            return b;
        });
    },
    toQueryString: function() {
        return this.map(function(a) {
            return a.map(encodeURIComponent).join("=");
        }).join("&");
    },
    inspect: function() {
        return "#<Hash:{" + this.map(function(a) {
            return a.map(Object.inspect).join(": ");
        }).join(", ") + "}>";
    }
};

function $H(a) {
    var b = Object.extend({}, a || {});
    Object.extend(b, Enumerable);
    Object.extend(b, Hash);
    return b;
}
ObjectRange = Class.create();
Object.extend(ObjectRange.prototype, Enumerable);
Object.extend(ObjectRange.prototype, {
    initialize: function(c, a, b) {
        this.start = c;
        this.end = a;
        this.exclusive = b;
    },
    _each: function(a) {
        var b = this.start;
        while (this.include(b)) {
            a(b);
            b = b.succ();
        }
    },
    include: function(a) {
        if (a < this.start) {
            return false;
        }
        if (this.exclusive) {
            return a < this.end;
        }
        return a <= this.end;
    }
});
var $R = function(c, a, b) {
    return new ObjectRange(c, a, b);
};
var Ajax = {
    getTransport: function() {
        return Try.these(function() {
            return new XMLHttpRequest();
        }, function() {
            return new ActiveXObject("Msxml2.XMLHTTP");
        }, function() {
            return new ActiveXObject("Microsoft.XMLHTTP");
        }) || false;
    },
    activeRequestCount: 0
};
Ajax.Responders = {
    responders: [],
    _each: function(a) {
        this.responders._each(a);
    },
    register: function(a) {
        if (!this.include(a)) {
            this.responders.push(a);
        }
    },
    unregister: function(a) {
        this.responders = this.responders.without(a);
    },
    dispatch: function(d, b, c, a) {
        this.each(function(f) {
            if (f[d] && typeof f[d] == "function") {
                try {
                    f[d].apply(f, [b, c, a]);
                } catch (g) {}
            }
        });
    }
};
Object.extend(Ajax.Responders, Enumerable);
Ajax.Responders.register({
    onCreate: function() {
        Ajax.activeRequestCount++;
    },
    onComplete: function() {
        Ajax.activeRequestCount--;
    }
});
Ajax.Base = function() {};
Ajax.Base.prototype = {
    setOptions: function(a) {
        this.options = {
            method: "post",
            asynchronous: true,
            contentType: "application/x-www-form-urlencoded",
            parameters: ""
        };
        Object.extend(this.options, a || {});
    },
    responseIsSuccess: function() {
        try {
            return this.transport.status == undefined || this.transport.status == 0 || (this.transport.status >= 200 && this.transport.status < 300);
        } catch (a) {
            return false;
        }
    },
    responseIsFailure: function() {
        return !this.responseIsSuccess();
    }
};
Ajax.Request = Class.create();
Ajax.Request.Events = ["Uninitialized", "Loading", "Loaded", "Interactive", "Complete"];
Ajax.Request.prototype = Object.extend(new Ajax.Base(), {
    _complete: false,
    initialize: function(b, a) {
        this.transport = Ajax.getTransport();
        this.setOptions(a);
        this.request(b);
    },
    request: function(b) {
        var c = this.options.parameters || "";
        if (c.length > 0) {
            c += "&_=";
        }
        if (this.options.method != "get" && this.options.method != "post") {
            c += (c.length > 0 ? "&" : "") + "_method=" + this.options.method;
            this.options.method = "post";
        }
        try {
            this.url = b;
            if ((this.options.method == "get" || this.options.method == "post") && c.length > 0) {
                this.url += (this.url.match(/\?/) ? "&" : "?") + c;
            }
            Ajax.Responders.dispatch("onCreate", this, this.transport);
            this.transport.open(this.options.method, this.url, this.options.asynchronous);
            if (this.options.asynchronous) {
                setTimeout(function() {
                    this.respondToReadyState(1);
                }.bind(this), 10);
            }
            this.transport.onreadystatechange = this.onStateChange.bind(this);
            this.setRequestHeaders();
            var a = this.options.postBody ? this.options.postBody : c;
            this.transport.send(this.options.method == "post" ? a : null);
            if (!this.options.asynchronous && this.transport.overrideMimeType) {
                this.onStateChange();
            }
        } catch (d) {
            this.dispatchException(d);
        }
    },
    setRequestHeaders: function() {
        var b = ["X-Requested-With", "XMLHttpRequest", "X-Prototype-Version", Prototype.Version, "Accept", "text/javascript, text/html, application/xml, text/xml, */*"];
        if (this.options.method == "post") {
            b.push("Content-type", this.options.contentType);
            if (this.transport.overrideMimeType) {
                b.push("Connection", "close");
            }
        }
        if (this.options.requestHeaders) {
            b.push.apply(b, this.options.requestHeaders);
        }
        for (var a = 0; a < b.length; a += 2) {
            this.transport.setRequestHeader(b[a], b[a + 1]);
        }
    },
    onStateChange: function() {
        var a = this.transport.readyState;
        if (a > 1 && !((a == 4) && this._complete)) {
            this.respondToReadyState(this.transport.readyState);
        }
    },
    header: function(a) {
        try {
            return this.transport.getResponseHeader(a);
        } catch (b) {}
    },
    evalJSON: function() {
        try {
            return eval("(" + this.header("X-JSON") + ")");
        } catch (e) {}
    },
    evalResponse: function() {
        try {
            return eval(this.transport.responseText);
        } catch (e) {
            this.dispatchException(e);
        }
    },
    respondToReadyState: function(a) {
        var c = Ajax.Request.Events[a];
        var f = this.transport,
            b = this.evalJSON();
        if (c == "Complete") {
            try {
                this._complete = true;
                (this.options["on" + (this.responseIsSuccess() ? "Success" : "Failure")] || Prototype.emptyFunction)(f, b, this.options, this.header("X-DEBUG"));
            } catch (d) {
                this.dispatchException(d);
            }
            if ((this.header("Content-type") || "").match(/^text\/javascript/i)) {
                this.evalResponse();
            }
        }
        try {
            (this.options["on" + c] || Prototype.emptyFunction)(f, b);
            Ajax.Responders.dispatch("on" + c, this, f, b);
        } catch (d) {
            this.dispatchException(d);
        }
        if (c == "Complete") {
            this.transport.onreadystatechange = Prototype.emptyFunction;
        }
    },
    dispatchException: function(a) {
        (this.options.onException || Prototype.emptyFunction)(this, a);
        Ajax.Responders.dispatch("onException", this, a);
    }
});
Ajax.Updater = Class.create();
Object.extend(Object.extend(Ajax.Updater.prototype, Ajax.Request.prototype), {
    initialize: function(a, c, b) {
        this.containers = {
            success: a.success ? $(a.success) : $(a),
            failure: a.failure ? $(a.failure) : (a.success ? null : $(a))
        };
        this.transport = Ajax.getTransport();
        this.setOptions(b);
        var d = this.options.onComplete || Prototype.emptyFunction;
        this.options.onComplete = (function(f, e) {
            this.updateContent();
            d(f, e);
        }).bind(this);
        this.request(c);
    },
    updateContent: function() {
        var b = this.responseIsSuccess() ? this.containers.success : this.containers.failure;
        var a = this.transport.responseText;
        if (!this.options.evalScripts) {
            a = a.stripScripts();
        }
        if (b) {
            if (this.options.insertion) {
                new this.options.insertion(b, a);
            } else {
                Element.update(b, a);
            }
        }
        if (this.responseIsSuccess()) {
            if (this.onComplete) {
                setTimeout(this.onComplete.bind(this), 10);
            }
        }
    }
});
Ajax.PeriodicalUpdater = Class.create();
Ajax.PeriodicalUpdater.prototype = Object.extend(new Ajax.Base(), {
    initialize: function(a, c, b) {
        this.setOptions(b);
        this.onComplete = this.options.onComplete;
        this.frequency = (this.options.frequency || 2);
        this.decay = (this.options.decay || 1);
        this.updater = {};
        this.container = a;
        this.url = c;
        this.start();
    },
    start: function() {
        this.options.onComplete = this.updateComplete.bind(this);
        this.onTimerEvent();
    },
    stop: function() {
        this.updater.options.onComplete = undefined;
        clearTimeout(this.timer);
        (this.onComplete || Prototype.emptyFunction).apply(this, arguments);
    },
    updateComplete: function(a) {
        if (this.options.decay) {
            this.decay = (a.responseText == this.lastText ? this.decay * this.options.decay : 1);
            this.lastText = a.responseText;
        }
        this.timer = setTimeout(this.onTimerEvent.bind(this), this.decay * this.frequency * 1000);
    },
    onTimerEvent: function() {
        this.updater = new Ajax.Updater(this.container, this.url, this.options);
    }
});

function $() {
    var c = [],
        b;
    for (var a = 0; a < arguments.length; a++) {
        b = arguments[a];
        if (typeof b == "string") {
            b = document.getElementById(b);
        }
        c.push(Element.extend(b));
    }
    return c.reduce();
}
document.getElementsByClassName = function(c, a) {
    var b = ($(a) || document.body).getElementsByTagName("*");
    return $A(b).inject([], function(d, e) {
        if (e.className.match(new RegExp("(^|\\s)" + c + "(\\s|$)"))) {
            d.push(Element.extend(e));
        }
        return d;
    });
};
if (!window.Element) {
    var Element = new Object();
}
Element.extend = function(c) {
    if (!c) {
        return;
    }
    if (_nativeExtensions || c.nodeType == 3) {
        return c;
    }
    if (!c._extended && c.tagName && c != window) {
        var b = Object.clone(Element.Methods),
            a = Element.extend.cache;
        if (c.tagName == "FORM") {
            Object.extend(b, Form.Methods);
        }
        if (["INPUT", "TEXTAREA", "SELECT"].include(c.tagName)) {
            Object.extend(b, Form.Element.Methods);
        }
        for (var e in b) {
            var d = b[e];
            if (typeof d == "function") {
                c[e] = a.findOrStore(d);
            }
        }
    }
    c._extended = true;
    return c;
};
Element.extend.cache = {
    findOrStore: function(a) {
        return this[a] = this[a] || function() {
            return a.apply(null, [this].concat($A(arguments)));
        };
    }
};
Element.Methods = {
    visible: function(a) {
        return $(a).style.display != "none";
    },
    toggle: function(a) {
        a = $(a);
        Element[Element.visible(a) ? "hide" : "show"](a);
        return a;
    },
    hide: function(a) {
        $(a).style.display = "none";
        return a;
    },
    show: function(a) {
        $(a).style.display = "";
        return a;
    },
    remove: function(a) {
        a = $(a);
        a.parentNode.removeChild(a);
        return a;
    },
    update: function(b, a) {
        $(b).innerHTML = a.stripScripts();
        setTimeout(function() {
            a.evalScripts();
        }, 10);
        return b;
    },
    replace: function(c, b) {
        c = $(c);
        if (c.outerHTML) {
            c.outerHTML = b.stripScripts();
        } else {
            var a = c.ownerDocument.createRange();
            a.selectNodeContents(c);
            c.parentNode.replaceChild(a.createContextualFragment(b.stripScripts()), c);
        }
        setTimeout(function() {
            b.evalScripts();
        }, 10);
        return c;
    },
    inspect: function(b) {
        b = $(b);
        var a = "<" + b.tagName.toLowerCase();
        $H({
            id: "id",
            className: "class"
        }).each(function(f) {
            var e = f.first(),
                c = f.last();
            var d = (b[e] || "").toString();
            if (d) {
                a += " " + c + "=" + d.inspect(true);
            }
        });
        return a + ">";
    },
    recursivelyCollect: function(a, c) {
        a = $(a);
        var b = [];
        while (a = a[c]) {
            if (a.nodeType == 1) {
                b.push(Element.extend(a));
            }
        }
        return b;
    },
    ancestors: function(a) {
        return $(a).recursivelyCollect("parentNode");
    },
    descendants: function(a) {
        a = $(a);
        return $A(a.getElementsByTagName("*"));
    },
    previousSiblings: function(a) {
        return $(a).recursivelyCollect("previousSibling");
    },
    nextSiblings: function(a) {
        return $(a).recursivelyCollect("nextSibling");
    },
    siblings: function(a) {
        a = $(a);
        return a.previousSiblings().reverse().concat(a.nextSiblings());
    },
    match: function(b, a) {
        b = $(b);
        if (typeof a == "string") {
            a = new Selector(a);
        }
        return a.match(b);
    },
    up: function(b, c, a) {
        return Selector.findElement($(b).ancestors(), c, a);
    },
    down: function(b, c, a) {
        return Selector.findElement($(b).descendants(), c, a);
    },
    previous: function(b, c, a) {
        return Selector.findElement($(b).previousSiblings(), c, a);
    },
    next: function(b, c, a) {
        return Selector.findElement($(b).nextSiblings(), c, a);
    },
    getElementsBySelector: function() {
        var a = $A(arguments),
            b = $(a.shift());
        return Selector.findChildElements(b, a);
    },
    getElementsByClassName: function(a, b) {
        a = $(a);
        return document.getElementsByClassName(b, a);
    },
    getHeight: function(a) {
        a = $(a);
        return a.offsetHeight;
    },
    classNames: function(a) {
        return new Element.ClassNames(a);
    },
    hasClassName: function(a, b) {
        if (!(a = $(a))) {
            return;
        }
        return Element.classNames(a).include(b);
    },
    addClassName: function(a, b) {
        if (!(a = $(a))) {
            return;
        }
        Element.classNames(a).add(b);
        return a;
    },
    removeClassName: function(a, b) {
        if (!(a = $(a))) {
            return;
        }
        Element.classNames(a).remove(b);
        return a;
    },
    observe: function() {
        Event.observe.apply(Event, arguments);
        return $A(arguments).first();
    },
    stopObserving: function() {
        Event.stopObserving.apply(Event, arguments);
        return $A(arguments).first();
    },
    cleanWhitespace: function(b) {
        b = $(b);
        var c = b.firstChild;
        while (c) {
            var a = c.nextSibling;
            if (c.nodeType == 3 && !/\S/.test(c.nodeValue)) {
                b.removeChild(c);
            }
            c = a;
        }
        return b;
    },
    empty: function(a) {
        return $(a).innerHTML.match(/^\s*$/);
    },
    childOf: function(b, a) {
        b = $(b), a = $(a);
        while (b = b.parentNode) {
            if (b == a) {
                return true;
            }
        }
        return false;
    },
    scrollTo: function(b) {
        b = $(b);
        var a = b.x ? b.x : b.offsetLeft,
            c = b.y ? b.y : b.offsetTop;
        window.scrollTo(a, c);
        return b;
    },
    getStyle: function(b, c) {
        b = $(b);
        var d = b.style[c.camelize()];
        if (!d) {
            if (document.defaultView && document.defaultView.getComputedStyle) {
                var a = document.defaultView.getComputedStyle(b, null);
                d = a ? a.getPropertyValue(c) : null;
            } else {
                if (b.currentStyle) {
                    d = b.currentStyle[c.camelize()];
                }
            }
        }
        if (window.opera && ["left", "top", "right", "bottom"].include(c)) {
            if (Element.getStyle(b, "position") == "static") {
                d = "auto";
            }
        }
        return d == "auto" ? null : d;
    },
    setStyle: function(b, c) {
        b = $(b);
        for (var a in c) {
            b.style[a.camelize()] = c[a];
        }
        return b;
    },
    getDimensions: function(b) {
        b = $(b);
        if (Element.getStyle(b, "display") != "none") {
            return {
                width: b.offsetWidth,
                height: b.offsetHeight
            };
        }
        var a = b.style;
        var e = a.visibility;
        var c = a.position;
        a.visibility = "hidden";
        a.position = "absolute";
        a.display = "";
        var f = b.clientWidth;
        var d = b.clientHeight;
        a.display = "none";
        a.position = c;
        a.visibility = e;
        return {
            width: f,
            height: d
        };
    },
    makePositioned: function(a) {
        a = $(a);
        var b = Element.getStyle(a, "position");
        if (b == "static" || !b) {
            a._madePositioned = true;
            a.style.position = "relative";
            if (window.opera) {
                a.style.top = 0;
                a.style.left = 0;
            }
        }
        return a;
    },
    undoPositioned: function(a) {
        a = $(a);
        if (a._madePositioned) {
            a._madePositioned = undefined;
            a.style.position = a.style.top = a.style.left = a.style.bottom = a.style.right = "";
        }
        return a;
    },
    makeClipping: function(a) {
        a = $(a);
        if (a._overflow) {
            return;
        }
        a._overflow = a.style.overflow || "auto";
        if ((Element.getStyle(a, "overflow") || "visible") != "hidden") {
            a.style.overflow = "hidden";
        }
        return a;
    },
    undoClipping: function(a) {
        a = $(a);
        if (!a._overflow) {
            return;
        }
        a.style.overflow = a._overflow == "auto" ? "" : a._overflow;
        a._overflow = null;
        return a;
    }
};
if (document.all) {
    Element.Methods.update = function(c, b) {
        c = $(c);
        var a = c.tagName.toUpperCase();
        if (["THEAD", "TBODY", "TR", "TD"].indexOf(a) > -1) {
            var d = document.createElement("div");
            switch (a) {
                case "THEAD":
                case "TBODY":
                    d.innerHTML = "<table><tbody>" + b.stripScripts() + "</tbody></table>";
                    depth = 2;
                    break;
                case "TR":
                    d.innerHTML = "<table><tbody><tr>" + b.stripScripts() + "</tr></tbody></table>";
                    depth = 3;
                    break;
                case "TD":
                    d.innerHTML = "<table><tbody><tr><td>" + b.stripScripts() + "</td></tr></tbody></table>";
                    depth = 4;
            }
            $A(c.childNodes).each(function(e) {
                c.removeChild(e);
            });
            depth.times(function() {
                d = d.firstChild;
            });
            $A(d.childNodes).each(function(e) {
                c.appendChild(e);
            });
        } else {
            c.innerHTML = b.stripScripts();
        }
        setTimeout(function() {
            b.evalScripts();
        }, 10);
        return c;
    };
}
Object.extend(Element, Element.Methods);
var _nativeExtensions = false;
if (!window.HTMLElement && /Konqueror|Safari|KHTML/.test(navigator.userAgent)) {
    ["", "Form", "Input", "TextArea", "Select"].each(function(b) {
        var a = window["HTML" + b + "Element"] = {};
        a.prototype = document.createElement(b ? b.toLowerCase() : "div").__proto__;
    });
}
Element.addMethods = function(a) {
    Object.extend(Element.Methods, a || {});

    function b(e, c) {
        var d = Element.extend.cache;
        for (var g in e) {
            var f = e[g];
            c[g] = d.findOrStore(f);
        }
    }
    if (typeof HTMLElement != "undefined") {
        b(Element.Methods, HTMLElement.prototype);
        b(Form.Methods, HTMLFormElement.prototype);
        [HTMLInputElement, HTMLTextAreaElement, HTMLSelectElement].each(function(c) {
            b(Form.Element.Methods, c.prototype);
        });
        _nativeExtensions = true;
    }
};
var Toggle = new Object();
Toggle.display = Element.toggle;
Abstract.Insertion = function(a) {
    this.adjacency = a;
};
Abstract.Insertion.prototype = {
    initialize: function(b, c) {
        this.element = $(b);
        this.content = c.stripScripts();
        if (this.adjacency && this.element.insertAdjacentHTML) {
            try {
                this.element.insertAdjacentHTML(this.adjacency, this.content);
            } catch (d) {
                var a = this.element.tagName.toLowerCase();
                if (a == "tbody" || a == "tr") {
                    this.insertContent(this.contentFromAnonymousTable());
                } else {
                    throw d;
                }
            }
        } else {
            this.range = this.element.ownerDocument.createRange();
            if (this.initializeRange) {
                this.initializeRange();
            }
            this.insertContent([this.range.createContextualFragment(this.content)]);
        }
        setTimeout(function() {
            c.evalScripts();
        }, 10);
    },
    contentFromAnonymousTable: function() {
        var a = document.createElement("div");
        a.innerHTML = "<table><tbody>" + this.content + "</tbody></table>";
        return $A(a.childNodes[0].childNodes[0].childNodes);
    }
};
var Insertion = new Object();
Insertion.Before = Class.create();
Insertion.Before.prototype = Object.extend(new Abstract.Insertion("beforeBegin"), {
    initializeRange: function() {
        this.range.setStartBefore(this.element);
    },
    insertContent: function(a) {
        a.each((function(b) {
            this.element.parentNode.insertBefore(b, this.element);
        }).bind(this));
    }
});
Insertion.Top = Class.create();
Insertion.Top.prototype = Object.extend(new Abstract.Insertion("afterBegin"), {
    initializeRange: function() {
        this.range.selectNodeContents(this.element);
        this.range.collapse(true);
    },
    insertContent: function(a) {
        a.reverse(false).each((function(b) {
            this.element.insertBefore(b, this.element.firstChild);
        }).bind(this));
    }
});
Insertion.Bottom = Class.create();
Insertion.Bottom.prototype = Object.extend(new Abstract.Insertion("beforeEnd"), {
    initializeRange: function() {
        this.range.selectNodeContents(this.element);
        this.range.collapse(this.element);
    },
    insertContent: function(a) {
        a.each((function(b) {
            this.element.appendChild(b);
        }).bind(this));
    }
});
Insertion.After = Class.create();
Insertion.After.prototype = Object.extend(new Abstract.Insertion("afterEnd"), {
    initializeRange: function() {
        this.range.setStartAfter(this.element);
    },
    insertContent: function(a) {
        a.each((function(b) {
            this.element.parentNode.insertBefore(b, this.element.nextSibling);
        }).bind(this));
    }
});
Element.ClassNames = Class.create();
Element.ClassNames.prototype = {
    initialize: function(a) {
        this.element = $(a);
    },
    _each: function(a) {
        this.element.className.split(/\s+/).select(function(b) {
            return b.length > 0;
        })._each(a);
    },
    set: function(a) {
        this.element.className = a;
    },
    add: function(a) {
        if (this.include(a)) {
            return;
        }
        this.set(this.toArray().concat(a).join(" "));
    },
    remove: function(a) {
        if (!this.include(a)) {
            return;
        }
        this.set(this.select(function(b) {
            return b != a;
        }).join(" "));
    },
    toString: function() {
        return this.toArray().join(" ");
    }
};
Object.extend(Element.ClassNames.prototype, Enumerable);
var Selector = Class.create();
Selector.prototype = {
    initialize: function(a) {
        this.params = {
            classNames: []
        };
        this.expression = a.toString().strip();
        this.parseExpression();
        this.compileMatcher();
    },
    parseExpression: function() {
        function g(h) {
            throw "Parse error in selector: " + h;
        }
        if (this.expression == "") {
            g("empty expression");
        }
        var f = this.params,
            e = this.expression,
            b, a, d, c;
        while (b = e.match(/^(.*)\[([a-z0-9_:-]+?)(?:([~\|!]?=)(?:"([^"]*)"|([^\]\s]*)))?\]$/i)) {
            f.attributes = f.attributes || [];
            f.attributes.push({
                name: b[2],
                operator: b[3],
                value: b[4] || b[5] || ""
            });
            e = b[1];
        }
        if (e == "*") {
            return this.params.wildcard = true;
        }
        while (b = e.match(/^([^a-z0-9_-])?([a-z0-9_-]+)(.*)/i)) {
            a = b[1], d = b[2], c = b[3];
            switch (a) {
                case "#":
                    f.id = d;
                    break;
                case ".":
                    f.classNames.push(d);
                    break;
                case "":
                case undefined:
                    f.tagName = d.toUpperCase();
                    break;
                default:
                    g(e.inspect());
            }
            e = c;
        }
        if (e.length > 0) {
            g(e.inspect());
        }
    },
    buildMatchExpression: function() {
        var d = this.params,
            c = [],
            b;
        if (d.wildcard) {
            c.push("true");
        }
        if (b = d.id) {
            c.push("element.id == " + b.inspect());
        }
        if (b = d.tagName) {
            c.push("element.tagName.toUpperCase() == " + b.inspect());
        }
        if ((b = d.classNames).length > 0) {
            for (var a = 0; a < b.length; a++) {
                c.push("Element.hasClassName(element, " + b[a].inspect() + ")");
            }
        }
        if (b = d.attributes) {
            b.each(function(f) {
                var g = "element.getAttribute(" + f.name.inspect() + ")";
                var e = function(h) {
                    return g + " && " + g + ".split(" + h.inspect() + ")";
                };
                switch (f.operator) {
                    case "=":
                        c.push(g + " == " + f.value.inspect());
                        break;
                    case "~=":
                        c.push(e(" ") + ".include(" + f.value.inspect() + ")");
                        break;
                    case "|=":
                        c.push(e("-") + ".first().toUpperCase() == " + f.value.toUpperCase().inspect());
                        break;
                    case "!=":
                        c.push(g + " != " + f.value.inspect());
                        break;
                    case "":
                    case undefined:
                        c.push(g + " != null");
                        break;
                    default:
                        throw "Unknown operator " + f.operator + " in selector";
                }
            });
        }
        return c.join(" && ");
    },
    compileMatcher: function() {
        this.match = new Function("element", "if (!element.tagName) return false;       return " + this.buildMatchExpression());
    },
    findElements: function(d) {
        var c;
        if (c = $(this.params.id)) {
            if (this.match(c)) {
                if (!d || Element.childOf(c, d)) {
                    return [c];
                }
            }
        }
        d = (d || document).getElementsByTagName(this.params.tagName || "*");
        var b = [];
        for (var a = 0; a < d.length; a++) {
            if (this.match(c = d[a])) {
                b.push(Element.extend(c));
            }
        }
        return b;
    },
    toString: function() {
        return this.expression;
    }
};
Object.extend(Selector, {
    matchElements: function(b, c) {
        var a = new Selector(c);
        return b.select(a.match.bind(a));
    },
    findElement: function(b, c, a) {
        if (typeof c == "number") {
            a = c, c = false;
        }
        return Selector.matchElements(b, c || "*")[a || 0];
    },
    findChildElements: function(a, b) {
        return b.map(function(c) {
            return c.strip().split(/\s+/).inject([null], function(e, f) {
                var d = new Selector(f);
                return e.inject([], function(h, g) {
                    return h.concat(d.findElements(g || a));
                });
            });
        }).flatten();
    }
});

function $$() {
    return Selector.findChildElements(document, $A(arguments));
}
var Form = {
    reset: function(a) {
        $(a).reset();
        return a;
    }
};
Form.Methods = {
    serialize: function(d) {
        var e = Form.getElements($(d));
        var c = new Array();
        for (var b = 0; b < e.length; b++) {
            var a = Form.Element.serialize(e[b]);
            if (a) {
                c.push(a);
            }
        }
        return c.join("&");
    },
    getElements: function(c) {
        c = $(c);
        var d = new Array();
        for (var b in Form.Element.Serializers) {
            var e = c.getElementsByTagName(b);
            for (var a = 0; a < e.length; a++) {
                d.push(e[a]);
            }
        }
        return d;
    },
    getInputs: function(f, c, d) {
        f = $(f);
        var a = f.getElementsByTagName("input");
        if (!c && !d) {
            return a;
        }
        var g = new Array();
        for (var e = 0; e < a.length; e++) {
            var b = a[e];
            if ((c && b.type != c) || (d && b.name != d)) {
                continue;
            }
            g.push(b);
        }
        return g;
    },
    disable: function(c) {
        c = $(c);
        var d = Form.getElements(c);
        for (var b = 0; b < d.length; b++) {
            var a = d[b];
            a.blur();
            a.disabled = "true";
        }
        return c;
    },
    enable: function(c) {
        c = $(c);
        var d = Form.getElements(c);
        for (var b = 0; b < d.length; b++) {
            var a = d[b];
            a.disabled = "";
        }
        return c;
    },
    findFirstElement: function(a) {
        return Form.getElements(a).find(function(b) {
            return b.type != "hidden" && !b.disabled && ["input", "select", "textarea"].include(b.tagName.toLowerCase());
        });
    },
    focusFirstElement: function(a) {
        a = $(a);
        Field.activate(Form.findFirstElement(a));
        return a;
    }
};
Object.extend(Form, Form.Methods);
Form.Element = {
    focus: function(a) {
        $(a).focus();
        return a;
    },
    select: function(a) {
        $(a).select();
        return a;
    }
};
Form.Element.Methods = {
    serialize: function(b) {
        b = $(b);
        var d = b.tagName.toLowerCase();
        var c = Form.Element.Serializers[d](b);
        if (c) {
            var a = encodeURIComponent(c[0]);
            if (a.length == 0) {
                return;
            }
            if (c[1].constructor != Array) {
                c[1] = [c[1]];
            }
            return c[1].map(function(e) {
                return a + "=" + encodeURIComponent(e);
            }).join("&");
        }
    },
    getValue: function(a) {
        a = $(a);
        var c = a.tagName.toLowerCase();
        var b = Form.Element.Serializers[c](a);
        if (b) {
            return b[1];
        }
    },
    clear: function(a) {
        $(a).value = "";
        return a;
    },
    present: function(a) {
        return $(a).value != "";
    },
    activate: function(a) {
        a = $(a);
        a.focus();
        if (a.select) {
            a.select();
        }
        return a;
    },
    disable: function(a) {
        a = $(a);
        a.disabled = "";
        return a;
    },
    enable: function(a) {
        a = $(a);
        a.blur();
        a.disabled = "true";
        return a;
    }
};
Object.extend(Form.Element, Form.Element.Methods);
var Field = Form.Element;
Form.Element.Serializers = {
    input: function(a) {
        switch (a.type.toLowerCase()) {
            case "checkbox":
            case "radio":
                return Form.Element.Serializers.inputSelector(a);
            default:
                return Form.Element.Serializers.textarea(a);
        }
        return false;
    },
    inputSelector: function(a) {
        if (a.checked) {
            return [a.name, a.value];
        }
    },
    textarea: function(a) {
        return [a.name, a.value];
    },
    select: function(a) {
        return Form.Element.Serializers[a.type == "select-one" ? "selectOne" : "selectMany"](a);
    },
    selectOne: function(c) {
        var d = "",
            b, a = c.selectedIndex;
        if (a >= 0) {
            b = c.options[a];
            d = b.value || b.text;
        }
        return [c.name, d];
    },
    selectMany: function(c) {
        var d = [];
        for (var b = 0; b < c.length; b++) {
            var a = c.options[b];
            if (a.selected) {
                d.push(a.value || a.text);
            }
        }
        return [c.name, d];
    }
};
var $F = Form.Element.getValue;
Abstract.TimedObserver = function() {};
Abstract.TimedObserver.prototype = {
    initialize: function(a, b, c) {
        this.frequency = b;
        this.element = $(a);
        this.callback = c;
        this.lastValue = this.getValue();
        this.registerCallback();
    },
    registerCallback: function() {
        setInterval(this.onTimerEvent.bind(this), this.frequency * 1000);
    },
    onTimerEvent: function() {
        var a = this.getValue();
        if (this.lastValue != a) {
            this.callback(this.element, a);
            this.lastValue = a;
        }
    }
};
Form.Element.Observer = Class.create();
Form.Element.Observer.prototype = Object.extend(new Abstract.TimedObserver(), {
    getValue: function() {
        return Form.Element.getValue(this.element);
    }
});
Form.Observer = Class.create();
Form.Observer.prototype = Object.extend(new Abstract.TimedObserver(), {
    getValue: function() {
        return Form.serialize(this.element);
    }
});
Abstract.EventObserver = function() {};
Abstract.EventObserver.prototype = {
    initialize: function(a, b) {
        this.element = $(a);
        this.callback = b;
        this.lastValue = this.getValue();
        if (this.element.tagName.toLowerCase() == "form") {
            this.registerFormCallbacks();
        } else {
            this.registerCallback(this.element);
        }
    },
    onElementEvent: function() {
        var a = this.getValue();
        if (this.lastValue != a) {
            this.callback(this.element, a);
            this.lastValue = a;
        }
    },
    registerFormCallbacks: function() {
        var b = Form.getElements(this.element);
        for (var a = 0; a < b.length; a++) {
            this.registerCallback(b[a]);
        }
    },
    registerCallback: function(a) {
        if (a.type) {
            switch (a.type.toLowerCase()) {
                case "checkbox":
                case "radio":
                    Event.observe(a, "click", this.onElementEvent.bind(this));
                    break;
                default:
                    Event.observe(a, "change", this.onElementEvent.bind(this));
                    break;
            }
        }
    }
};
Form.Element.EventObserver = Class.create();
Form.Element.EventObserver.prototype = Object.extend(new Abstract.EventObserver(), {
    getValue: function() {
        return Form.Element.getValue(this.element);
    }
});
Form.EventObserver = Class.create();
Form.EventObserver.prototype = Object.extend(new Abstract.EventObserver(), {
    getValue: function() {
        return Form.serialize(this.element);
    }
});
if (!window.Event) {
    var Event = new Object();
}
Object.extend(Event, {
    KEY_BACKSPACE: 8,
    KEY_TAB: 9,
    KEY_RETURN: 13,
    KEY_ESC: 27,
    KEY_LEFT: 37,
    KEY_UP: 38,
    KEY_RIGHT: 39,
    KEY_DOWN: 40,
    KEY_DELETE: 46,
    KEY_HOME: 36,
    KEY_END: 35,
    KEY_PAGEUP: 33,
    KEY_PAGEDOWN: 34,
    element: function(a) {
        return a.target || a.srcElement;
    },
    isLeftClick: function(a) {
        return (((a.which) && (a.which == 1)) || ((a.button) && (a.button == 1)));
    },
    pointerX: function(a) {
        return a.pageX || (a.clientX + (document.documentElement.scrollLeft || document.body.scrollLeft));
    },
    pointerY: function(a) {
        return a.pageY || (a.clientY + (document.documentElement.scrollTop || document.body.scrollTop));
    },
    stop: function(a) {
        if (a.preventDefault) {
            a.preventDefault();
            a.stopPropagation();
        } else {
            a.returnValue = false;
            a.cancelBubble = true;
        }
    },
    findElement: function(c, b) {
        var a = Event.element(c);
        while (a.parentNode && (!a.tagName || (a.tagName.toUpperCase() != b.toUpperCase()))) {
            a = a.parentNode;
        }
        return a;
    },
    observers: false,
    _observeAndCache: function(d, c, b, a) {
        if (!this.observers) {
            this.observers = [];
        }
        if (d.addEventListener) {
            this.observers.push([d, c, b, a]);
            d.addEventListener(c, b, a);
        } else {
            if (d.attachEvent) {
                this.observers.push([d, c, b, a]);
                d.attachEvent("on" + c, b);
            }
        }
    },
    unloadCache: function() {
        if (!Event.observers) {
            return;
        }
        for (var a = 0; a < Event.observers.length; a++) {
            Event.stopObserving.apply(this, Event.observers[a]);
            Event.observers[a][0] = null;
        }
        Event.observers = false;
    },
    observe: function(d, c, b, a) {
        d = $(d);
        a = a || false;
        if (c == "keypress" && (navigator.appVersion.match(/Konqueror|Safari|KHTML/) || d.attachEvent)) {
            c = "keydown";
        }
        Event._observeAndCache(d, c, b, a);
    },
    stopObserving: function(d, c, b, a) {
        d = $(d);
        a = a || false;
        if (c == "keypress" && (navigator.appVersion.match(/Konqueror|Safari|KHTML/) || d.detachEvent)) {
            c = "keydown";
        }
        if (d.removeEventListener) {
            d.removeEventListener(c, b, a);
        } else {
            if (d.detachEvent) {
                try {
                    d.detachEvent("on" + c, b);
                } catch (f) {}
            }
        }
    }
});
if (navigator.appVersion.match(/\bMSIE\b/)) {
    Event.observe(window, "unload", Event.unloadCache, false);
}
var Position = {
    includeScrollOffsets: false,
    prepare: function() {
        this.deltaX = window.pageXOffset || document.documentElement.scrollLeft || document.body.scrollLeft || 0;
        this.deltaY = window.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop || 0;
    },
    realOffset: function(b) {
        var a = 0,
            c = 0;
        do {
            a += b.scrollTop || 0;
            c += b.scrollLeft || 0;
            b = b.parentNode;
        } while (b);
        return [c, a];
    },
    cumulativeOffset: function(b) {
        var a = 0,
            c = 0;
        do {
            a += b.offsetTop || 0;
            c += b.offsetLeft || 0;
            b = b.offsetParent;
        } while (b);
        return [c, a];
    },
    positionedOffset: function(b) {
        var a = 0,
            c = 0;
        do {
            a += b.offsetTop || 0;
            c += b.offsetLeft || 0;
            b = b.offsetParent;
            if (b) {
                p = Element.getStyle(b, "position");
                if (p == "relative" || p == "absolute") {
                    break;
                }
            }
        } while (b);
        return [c, a];
    },
    offsetParent: function(a) {
        if (a.offsetParent) {
            return a.offsetParent;
        }
        if (a == document.body) {
            return a;
        }
        while ((a = a.parentNode) && a != document.body) {
            if (Element.getStyle(a, "position") != "static") {
                return a;
            }
        }
        return document.body;
    },
    within: function(b, a, c) {
        if (this.includeScrollOffsets) {
            return this.withinIncludingScrolloffsets(b, a, c);
        }
        this.xcomp = a;
        this.ycomp = c;
        this.offset = this.cumulativeOffset(b);
        return (c >= this.offset[1] && c < this.offset[1] + b.offsetHeight && a >= this.offset[0] && a < this.offset[0] + b.offsetWidth);
    },
    withinIncludingScrolloffsets: function(b, a, d) {
        var c = this.realOffset(b);
        this.xcomp = a + c[0] - this.deltaX;
        this.ycomp = d + c[1] - this.deltaY;
        this.offset = this.cumulativeOffset(b);
        return (this.ycomp >= this.offset[1] && this.ycomp < this.offset[1] + b.offsetHeight && this.xcomp >= this.offset[0] && this.xcomp < this.offset[0] + b.offsetWidth);
    },
    overlap: function(b, a) {
        if (!b) {
            return 0;
        }
        if (b == "vertical") {
            return ((this.offset[1] + a.offsetHeight) - this.ycomp) / a.offsetHeight;
        }
        if (b == "horizontal") {
            return ((this.offset[0] + a.offsetWidth) - this.xcomp) / a.offsetWidth;
        }
    },
    page: function(d) {
        var a = 0,
            c = 0;
        var b = d;
        do {
            a += b.offsetTop || 0;
            c += b.offsetLeft || 0;
            if (b.offsetParent == document.body) {
                if (Element.getStyle(b, "position") == "absolute") {
                    break;
                }
            }
        } while (b = b.offsetParent);
        b = d;
        do {
            if (!window.opera || b.tagName == "BODY") {
                a -= b.scrollTop || 0;
                c -= b.scrollLeft || 0;
            }
        } while (b = b.parentNode);
        return [c, a];
    },
    clone: function(c, e) {
        var a = Object.extend({
            setLeft: true,
            setTop: true,
            setWidth: true,
            setHeight: true,
            offsetTop: 0,
            offsetLeft: 0
        }, arguments[2] || {});
        c = $(c);
        var d = Position.page(c);
        e = $(e);
        var f = [0, 0];
        var b = null;
        if (Element.getStyle(e, "position") == "absolute") {
            b = Position.offsetParent(e);
            f = Position.page(b);
        }
        if (b == document.body) {
            f[0] -= document.body.offsetLeft;
            f[1] -= document.body.offsetTop;
        }
        if (a.setLeft) {
            e.style.left = (d[0] - f[0] + a.offsetLeft) + "px";
        }
        if (a.setTop) {
            e.style.top = (d[1] - f[1] + a.offsetTop) + "px";
        }
        if (a.setWidth) {
            e.style.width = c.offsetWidth + "px";
        }
        if (a.setHeight) {
            e.style.height = c.offsetHeight + "px";
        }
    },
    absolutize: function(b) {
        b = $(b);
        if (b.style.position == "absolute") {
            return;
        }
        Position.prepare();
        var d = Position.positionedOffset(b);
        var f = d[1];
        var e = d[0];
        var c = b.clientWidth;
        var a = b.clientHeight;
        b._originalLeft = e - parseFloat(b.style.left || 0);
        b._originalTop = f - parseFloat(b.style.top || 0);
        b._originalWidth = b.style.width;
        b._originalHeight = b.style.height;
        b.style.position = "absolute";
        b.style.top = f + "px";
        b.style.left = e + "px";
        b.style.width = c + "px";
        b.style.height = a + "px";
    },
    relativize: function(a) {
        a = $(a);
        if (a.style.position == "relative") {
            return;
        }
        Position.prepare();
        a.style.position = "relative";
        var c = parseFloat(a.style.top || 0) - (a._originalTop || 0);
        var b = parseFloat(a.style.left || 0) - (a._originalLeft || 0);
        a.style.top = c + "px";
        a.style.left = b + "px";
        a.style.height = a._originalHeight;
        a.style.width = a._originalWidth;
    }
};
if (/Konqueror|Safari|KHTML/.test(navigator.userAgent)) {
    Position.cumulativeOffset = function(b) {
        var a = 0,
            c = 0;
        do {
            a += b.offsetTop || 0;
            c += b.offsetLeft || 0;
            if (b.offsetParent == document.body) {
                if (Element.getStyle(b, "position") == "absolute") {
                    break;
                }
            }
            b = b.offsetParent;
        } while (b);
        return [c, a];
    };
}
Element.addMethods();
(function() {
    var combineUrlPieces = function(scheme, hostAndPort) {
        return scheme + "://" + hostAndPort;
    };
    var framelessUtil = {
        init: function(url, onSubmitCallback, initCallback, errorCallback) {
            var that = this;
            var questionIx;
            var path;
            var queryString;
            var pieces;
            var isInstanceUrl = false;
            var parms;
            var urlToUse = url;
            var nextQueryParmDelimitter = "&";
            var setLastUrlLoaded = function(url, type) {
                var ix = url.indexOf("?");
                that.lastUrlLoaded = (ix > 0) ? url.substr(0, ix) : url;
                if (type === "flowtype") {
                    that.lastUrlLoaded += "/current";
                }
            };
            if (url) {
                questionIx = url.indexOf("?");
                path = (questionIx >= 0) ? url.substr(0, questionIx) : url;
                queryString = (questionIx >= 0) ? url.substr(questionIx + 1) : null;
                pieces = path.split("/");
                if (pieces && pieces.length >= 12) {
                    this.scheme = pieces[0].substr(0, pieces[0].length - 1);
                    this.hostAndPort = pieces[2];
                    this.tenantId = pieces[6];
                    this.userId = pieces[8];
                    this.appId = pieces[10];
                    this.type = pieces[11];
                    if (this.type === "form") {
                        this.type = "formtype";
                        isInstanceUrl = true;
                    } else {
                        if (this.type === "flow") {
                            this.type = "flowtype";
                            isInstanceUrl = true;
                        }
                    }
                    this.onSubmitCallback = onSubmitCallback;
                    if (isInstanceUrl) {
                        if (queryString) {
                            parms = queryString.split("&");
                            for (var i = 0; i < parms.length; i++) {
                                if (parms[i].substr(0, "typeId=".length) === "typeId=") {
                                    this.typeId = parms[i].substr("typeId=".length);
                                    break;
                                }
                            }
                        }
                    } else {
                        this.typeId = pieces[12];
                        if (queryString) {
                            parms = queryString.split("&");
                            for (var i = 0; i < parms.length; i++) {
                                if (parms[i].substr(0, "_styleId=".length) === "_styleId=") {
                                    this.styleId = parms[i].substr("_styleId=".length);
                                    break;
                                }
                            }
                        }
                        if (url.indexOf("?") === -1) {
                            nextQueryParmDelimitter = "?";
                        }
                        _frevvo.utilities.ajaxRequest.send(url + nextQueryParmDelimitter + "quiet=true", {
                            method: "get",
                            onComplete: function(t, options) {
                                var x = null;
                                if (t.getResponseHeader("frevvo.app.error.message")) {
                                    if (errorCallback) {
                                        errorCallback(t.getResponseHeader("frevvo.app.error.message"));
                                    }
                                } else {
                                    eval(t.responseText);
                                    urlToUse = x.formUrl;
                                    if (initCallback) {
                                        setLastUrlLoaded(urlToUse, that.type);
                                        initCallback(urlToUse);
                                    }
                                }
                            }
                        });
                        return;
                    }
                }
            }
            if (initCallback) {
                setLastUrlLoaded(urlToUse, that.type);
                initCallback(urlToUse);
            }
        },
        attachFormHtmlToDoc: function(formHtml, elId) {
            var that = this;
            var node = document.getElementById(elId);
            if (node) {
                node.innerHTML = formHtml;
                _frevvo.utilities.ajaxRequest.send(_frevvo.forms.formUrl() + "/locale-strings", {
                    method: "get",
                    onSuccess: function(t) {
                        eval(t.responseText);
                        var el = document.getElementById("root-control");
                        _frevvo.groupController.setupChildren(el);
                        el = document.getElementById("form-container");
                        if (Element.hasClassName(el, "s-preview")) {
                            window._frevvo.uberController.previewMode = true;
                        }
                        _frevvo.utilities.startHere();
                        that.loadCss();
                        var script = _frevvo.protoView.$childByName(node, "script");
                        if (script) {
                            eval(script.innerHTML);
                        }
                    }
                });
            } else {
                throw "unable to find document element";
            }
        },
        loadCss: function() {
            var addStyleSheetToDoc = function(href) {
                var ss = document.styleSheets;
                for (var i = 0, max = ss.length; i < max; i++) {
                    if (ss[i].href == href) {
                        return;
                    }
                }
                if (document.createStyleSheet) {
                    document.createStyleSheet(href);
                } else {
                    var l = document.createElement("link");
                    l.rel = "stylesheet";
                    l.href = href;
                    document.getElementsByTagName("head")[0].appendChild(l);
                }
            };
            var addScriptToDoc = function(src) {
                var s = document.createElement("script");
                s.src = src;
                s.type = "text/javascript";
                document.getElementsByTagName("head")[0].appendChild(s);
            };
            var urlPrefix = combineUrlPieces(this.scheme, this.hostAndPort);
            var url = urlPrefix + "/frevvo/web/tn/" + this.tenantId + "/user/" + this.userId + "/app/" + this.appId + "/" + this.type + "/" + this.typeId + "/cssUrls";
            if (this.styleId) {
                url += "?_styleId=" + this.styleId;
            }
            new Ajax.Request(url, {
                method: "get",
                onComplete: function(t, options) {
                    var urls = null;
                    eval("urls = " + t.responseText);
                    addStyleSheetToDoc(urlPrefix + urls.styleUrl + "/colorscheme.css");
                    addStyleSheetToDoc(urlPrefix + urls.themeUrl + "/common.css");
                    addStyleSheetToDoc(urlPrefix + urls.themeUrl + "/form.css");
                    addStyleSheetToDoc(urlPrefix + urls.themeUrl + "/form-layout.css");
                    if (urls.customUrl) {
                        addScriptToDoc(urlPrefix + urls.customUrl);
                    }
                    var cssFile = (_frevvo.uberController.mobile) ? "form.mobile.pack.css" : "form.pack.css";
                    addStyleSheetToDoc(urlPrefix + urls.cssUrl + "/" + cssFile);
                    setTimeout("if (document.getElementById('_frevvo-frameless-container')) {document.getElementById('_frevvo-frameless-container').style.visibility = 'visible'}", 200);
                }
            });
        }
    };
    var _frevvo = {
        api: {
            loadForm: function(containerElementId, url, successHandler, errorHandler, onSubmitHandler) {
                var delimiter = (url.indexOf("?") === -1) ? "?" : "&";
                framelessUtil.init(url, onSubmitHandler, function(instanceUrl) {
                    new Ajax.Request(instanceUrl + delimiter + "_frameless=true", {
                        method: "get",
                        onComplete: function(t, options) {
                            try {
                                framelessUtil.attachFormHtmlToDoc(t.responseText, containerElementId);
                                if (successHandler) {
                                    successHandler();
                                }
                            } catch (e) {
                                if (errorHandler) {
                                    errorHandler(e);
                                }
                            }
                        },
                        onFailure: function(e) {
                            if (errorHandler) {
                                errorHandler(e);
                            }
                        }
                    });
                }, errorHandler);
            },
            attachFlowsNextFormHtml: function(htmlSnippet, containerElementId) {
                framelessUtil.attachFormHtmlToDoc(htmlSnippet, containerElementId);
            },
            isFramelessLoaded: function() {
                if (document.getElementById("_frevvo-frameless-wrapper")) {
                    return true;
                }
                return false;
            },
            getLastLoadedUrl: function() {
                return framelessUtil.lastUrlLoaded;
            }
        },
        execFramelessOnSubmitCallback: function() {
            if (framelessUtil.onSubmitCallback) {
                framelessUtil.onSubmitCallback();
            }
        },
        utilities: {
            getQuery: function(url) {
                if (url) {
                    var queries = url.split("?");
                    queries.shift();
                    queries = queries.join("?").split("#");
                    if (queries.length > 0) {
                        return queries.shift();
                    }
                }
                return null;
            },
            startHere: function(resize) {
                try {
                    this.windowUtil._referrer_url = gup("_referrer_url");
                    this.windowUtil._iframe_id = gup("_iframe_id");
                    this.windowUtil._resize = resize || gup("_resize", "true");
                    this.windowUtil._edit = gup("edit", "false");
                    if (this.IEVersion) {
                        try {
                            var rel = document.getElementById("form-container");
                            var clsN = "ie" + this.IEVersion;
                            Element.addClassName(rel, clsN);
                        } catch (e) {}
                    }
                    this.ddMenu.setupIE();
                    if (this.util.isDefined(window.Behaviour)) {
                        Behaviour.apply();
                    }
                    if (this.util.isDefined(window._frevvo.taskView)) {
                        window._frevvo.taskView.bindEventHandlers();
                    }
                    this.cssUtil.setBrowserSelector();
                } finally {
                    this.windowUtil.resetSize();
                    try {
                        parent.scrollTo(0, 0);
                    } catch (e) {}
                }
            },
            insertBefore: function(newElement, targetElement, parent) {
                if (!parent) {
                    parent = targetElement.parentNode;
                }
                parent.insertBefore(newElement, targetElement);
            },
            insertAfter: function(newElement, targetElement) {
                var parent = targetElement.parentNode;
                if (parent.lastChild == targetElement) {
                    parent.appendChild(newElement);
                } else {
                    parent.insertBefore(newElement, targetElement.nextSibling);
                }
            },
            insertFirst: function(newElement, targetElement) {
                if (targetElement) {
                    if (targetElement.hasChildNodes()) {
                        targetElement.insertBefore(newElement, targetElement.firstChild);
                    } else {
                        targetElement.appendChild(newElement);
                    }
                }
            },
            removeAllChildren: function(element) {
                for (var i = (element.childNodes.length - 1); i > -1; i--) {
                    element.removeChild(element.childNodes[i]);
                }
            },
            debugPrint: function(message) {
                var debugPrint = $("debugPrint");
                if (debugPrint) {
                    debugPrint.innerHTML = message + "<br/>" + $("debugPrint").innerHTML;
                }
            },
            clearDebugPrint: function() {
                var debugPrint = $("debugPrint");
                if (debugPrint) {
                    debugPrint.innerHTML = "";
                }
                return false;
            },
            setCursor: function(el, cursor) {
                Element.setStyle(el, $H({
                    cursor: cursor
                }));
            },
            getRelatedTarget: function(e) {
                if (!e) {
                    var e = window.event;
                }
                if (e.relatedTarget) {
                    return e.relatedTarget;
                } else {
                    if (e.type.toLowerCase() == "mouseover".toLowerCase()) {
                        return e.fromElement;
                    } else {
                        if (e.type.toLowerCase() == "mouseout".toLowerCase()) {
                            return e.toElement;
                        }
                    }
                }
            },
            isParent: function(element, parent) {
                if (element == null) {
                    return false;
                }
                while (element != null) {
                    if (element.parentNode == parent) {
                        return true;
                    }
                    element = element.parentNode;
                }
                return false;
            },
            ignoreMouseEvent: function(evt, el) {
                var targ = _frevvo.utilities.getRelatedTarget(evt);
                try {
                    if (targ == el) {
                        return true;
                    }
                    if (_frevvo.utilities.isParent(targ, el)) {
                        return true;
                    }
                } catch (e) {
                    return true;
                }
                return false;
            },
            cloneElement: function(el) {
                var clone = el.cloneNode(true);
                var w1 = document.createElement("div");
                w1.appendChild(clone);
                var w2 = document.createElement("div");
                w2.innerHTML = w1.innerHTML;
                return w2.firstChild;
            },
            createCookie: function(name, value, days) {
                path = _frevvo.context || "/";
                if (days) {
                    var date = new Date();
                    date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
                    var expires = "; expires=" + date.toGMTString();
                } else {
                    var expires = "";
                }
                document.cookie = name + "=" + value + expires + "; path=" + path;
            },
            readCookie: function(name) {
                var nameEQ = name + "=";
                var ca = document.cookie.split(";");
                for (var i = 0; i < ca.length; i++) {
                    var c = ca[i];
                    while (c.charAt(0) == " ") {
                        c = c.substring(1, c.length);
                    }
                    if (c.indexOf(nameEQ) == 0) {
                        return c.substring(nameEQ.length, c.length);
                    }
                }
                return null;
            },
            toggleVisible: function(els) {
                $A(els).each(function(el) {
                    Element.toggle(el);
                });
            },
            toggleClassName: function(els, cls) {
                $A(els).each(function(el) {
                    if (Element.hasClassName(el, cls)) {
                        Element.removeClassName(el, cls);
                    } else {
                        Element.addClassName(el, cls);
                    }
                });
            },
            indexOf: function(arr, obj) {
                for (var i = 0; i < arr.length; i++) {
                    if (arr[i] == obj) {
                        return i;
                    }
                }
                return -1;
            },
            entityMap: {
                "&": "&amp;",
                "<": "&lt;",
                ">": "&gt;",
                '"': "&quot;",
                "'": "&#39;",
                "/": "&#x2F;"
            },
            escapeHtml: function(string) {
                return String(string).replace(/[&<>"'\/]/g, function(s) {
                    return _frevvo.utilities.entityMap[s];
                });
            },
            agentUtil: {
                isIOS: function() {
                    if ((navigator.userAgent.match(/iPhone/i)) || (navigator.userAgent.match(/iPod/i)) || (navigator.userAgent.match(/iPad/i))) {
                        return true;
                    }
                    return false;
                }
            },
            windowUtil: {
                _referrer_url: null,
                _iframe_id: null,
                _pubHeight: -1,
                _pubWidth: -1,
                _resize: "true",
                _edit: null,
                _scrollWidth: null,
                _refreshTime: -1,
                showAndReset: function(el) {
                    Element.show(el);
                },
                hideAndReset: function(el) {
                    Element.hide(el);
                },
                calculateFrameSize: function() {
                    var center = document.getElementById("center-cell");
                    if (center) {
                        var w = center.parentNode.scrollWidth;
                        var h = center.scrollHeight;
                        if (window.FlowEditController || (_frevvo.uberController && _frevvo.uberController.editMode)) {
                            var toolbox = document.getElementById("f-toolbox");
                            h = Math.max(center.scrollHeight, toolbox ? toolbox.scrollHeight : 0);
                            if (w < 1200) {
                                w = 1200;
                            }
                        }
                        return [h, w];
                    } else {
                        var container = document.getElementById("center-content");
                        if (container) {
                            return [container.scrollHeight, container.scrollWidth];
                        }
                    }
                    return null;
                },
                resetSize: function() {
                    if (_frevvo.utilities.windowUtil._resize == "false") {
                        return;
                    }
                    var size = _frevvo.utilities.windowUtil.calculateFrameSize();
                    if (!size || (size[0] == 0 && size[1] == 0)) {
                        return;
                    }
                    var h = _frevvo.utilities.windowUtil._resize == "true" || _frevvo.utilities.windowUtil._resize == "vert" ? size[0] : -1;
                    var w = _frevvo.utilities.windowUtil._resize == "true" || _frevvo.utilities.windowUtil._resize == "horz" ? size[1] : -1;
                    if (_frevvo.utilities.windowUtil._resize == "vert") {
                        h += _frevvo.utilities.windowUtil.getScrollBarWidth() + 16;
                        if (_frevvo.utilities.windowUtil._edit == "true") {
                            h += 400;
                        }
                    } else {
                        if (_frevvo.utilities.windowUtil._resize == "horz") {
                            w += _frevvo.utilities.windowUtil.getScrollBarWidth() + 20;
                        } else {
                            if (_frevvo.utilities.windowUtil._resize == "true") {
                                w += _frevvo.utilities.windowUtil.getScrollBarWidth() + 8;
                                h += _frevvo.utilities.windowUtil.getScrollBarWidth() + 8;
                                if (_frevvo.utilities.windowUtil._edit == "true") {
                                    h += 0;
                                }
                            }
                        }
                    }
                    var xdomain = true;
                    try {
                        var f = window.frameElement;
                        if (f) {
                            var ch = f.style.height || "0px";
                            var cw = f.style.width || "0px";
                            xdomain = false;
                            if (ch.substr(0, ch.length - 2) != ("" + h) || cw.substr(0, cw.length - 2) != ("" + w)) {
                                if (h > 0) {
                                    f.style.height = h + "px";
                                }
                                if (w > 0) {
                                    f.style.width = w + "px";
                                }
                                if (_frevvo.utilities.windowUtil._resize == "true") {
                                    window.scrollTo(0, 0);
                                }
                            }
                        }
                    } catch (e) {
                        xdomain = true;
                    }
                    if (xdomain) {
                        if (_frevvo.utilities.windowUtil._referrer_url && _frevvo.utilities.windowUtil._referrer_url.length > 0 && _frevvo.utilities.windowUtil._iframe_id && _frevvo.utilities.windowUtil._iframe_id.length > 0) {
                            if (h != _frevvo.utilities.windowUtil._pubHeight || w != _frevvo.utilities.windowUtil._pubWidth) {
                                if (!h) {
                                    h = _frevvo.utilities.windowUtil._pubHeight;
                                }
                                if (!w) {
                                    w = _frevvo.utilities.windowUtil._pubWidth;
                                }
                                parent.location.replace(_frevvo.utilities.windowUtil._referrer_url + "#" + w + ":" + h + ":" + _frevvo.utilities.windowUtil._iframe_id);
                                _frevvo.utilities.windowUtil._pubHeight = h;
                                _frevvo.utilities.windowUtil._pubWidth = w;
                                if (_frevvo.utilities.windowUtil._resize == "true") {
                                    window.scrollTo(0, 0);
                                }
                            }
                        }
                    }
                    setTimeout(_frevvo.utilities.windowUtil.resetSize, 100);
                },
                setupAutoRefresh: function(refreshTime) {
                    _frevvo.utilities.windowUtil._refreshTime = refreshTime;
                    setTimeout(_frevvo.utilities.windowUtil.autoRefresh, _frevvo.utilities.windowUtil._refreshTime);
                },
                autoRefresh: function() {
                    if (_frevvo.utilities.windowUtil._refreshTime < 0) {
                        return;
                    }
                    window.location.reload(true);
                    setTimeout(_frevvo.utilities.windowUtil.autoRefresh, _frevvo.utilities.windowUtil._refreshTime);
                },
                getScrollBarWidth: function() {
                    if (_frevvo.utilities.windowUtil._scrollWidth) {
                        return _frevvo.utilities.windowUtil._scrollWidth;
                    }
                    var inner = document.createElement("p");
                    inner.style.width = "100%";
                    inner.style.height = "200px";
                    var outer = document.createElement("div");
                    outer.style.position = "absolute";
                    outer.style.top = "0px";
                    outer.style.left = "0px";
                    outer.style.visibility = "hidden";
                    outer.style.width = "200px";
                    outer.style.height = "150px";
                    outer.style.overflow = "hidden";
                    outer.appendChild(inner);
                    document.body.appendChild(outer);
                    var w1 = inner.offsetWidth;
                    outer.style.overflow = "scroll";
                    var w2 = inner.offsetWidth;
                    if (w1 == w2) {
                        w2 = outer.clientWidth;
                    }
                    document.body.removeChild(outer);
                    _frevvo.utilities.windowUtil._scrollWidth = (w1 - w2);
                    return _frevvo.utilities.windowUtil._scrollWidth;
                },
                visible: function(el) {
                    return el.style.display != "none";
                },
                toggle: function(s) {
                    if (_frevvo.utilities.util.isArray(s)) {
                        for (var i = 0; i < s.length; i++) {
                            _frevvo.utilities.windowUtil.doToggle(s[i]);
                        }
                    } else {
                        _frevvo.utilities.windowUtil.doToggle(s);
                    }
                },
                doToggle: function(s) {
                    var el = document.getElementById(s);
                    if (el) {
                        if (_frevvo.utilities.windowUtil.visible(el)) {
                            el.style.display = "none";
                        } else {
                            el.style.display = "";
                        }
                    }
                },
                toggleAndSet: function(s, ael) {
                    if (_frevvo.utilities.util.isArray(s)) {
                        for (var i = 0; i < s.length; i++) {
                            _frevvo.utilities.windowUtil.doToggleAndSet(s[i], ael);
                        }
                    } else {
                        _frevvo.utilities.windowUtil.doToggleAndSet(s, ael);
                    }
                },
                doToggleAndSet: function(s, ael) {
                    var el = document.getElementById(s);
                    if (el) {
                        if (_frevvo.utilities.windowUtil.visible(el)) {
                            el.style.display = "none";
                            Element.removeClassName(ael, "s-expanded");
                            Element.addClassName(ael, "s-collapsed");
                        } else {
                            el.style.display = "";
                            Element.removeClassName(ael, "s-collapsed");
                            Element.addClassName(ael, "s-expanded");
                        }
                    }
                },
                hide: function(s) {
                    var el = document.getElementById(s);
                    if (el) {
                        el.style.display = "none";
                    }
                },
                disableEnter: function(e) {
                    var key;
                    if (window.event) {
                        key = window.event.keyCode;
                    } else {
                        key = e.which;
                    }
                    if (key != Event.KEY_RETURN) {
                        return true;
                    }
                    var el = Event.element(e);
                    if ((el.nodeName.toLowerCase() == "textarea") || (el.tagName.toLowerCase() == "div" && el.className.toLowerCase() == "note-editable")) {
                        return true;
                    } else {
                        return false;
                    }
                },
                IEBody: function(w) {
                    w = w || window;
                    if (w.document.compatMode && document.compatMode != "BackCompat") {
                        return w.document.documentElement;
                    } else {
                        return w.document.body;
                    }
                },
                truePosition: function() {
                    return (window.innerHeight ? window.pageYOffset : _frevvo.utilities.windowUtil.IEBody().scrollTop);
                }
            },
            ddMenu: {
                setupIE: function() {
                    if (document.all && document.getElementById) {
                        var sn = document.getElementById("sn");
                        if (!sn) {
                            return;
                        }
                        for (var i = 0; i < sn.childNodes.length; i++) {
                            var node = sn.childNodes[i];
                            if (node.nodeName == "LI") {
                                node.onmouseover = function() {
                                    this.className += " over";
                                };
                                node.onmouseout = function() {
                                    this.className = this.className.replace("over", "");
                                };
                            }
                        }
                    }
                }
            },
            shareForm: {
                showCode: function(kind) {
                    Element.hide("share-code-link");
                    Element.hide("share-code-page");
                    Element.hide("share-code-iframe");
                    Element.hide("share-code-embed");
                    Element.hide("share-code-embed-link");
                    Element.hide("share-code-form");
                    Element.hide("share-code-googlegadget");
                    Element.hide("share-instructions-link");
                    Element.hide("share-instructions-page");
                    Element.hide("share-instructions-iframe");
                    Element.hide("share-instructions-embed");
                    Element.hide("share-instructions-embed-link");
                    Element.hide("share-instructions-form");
                    Element.hide("share-instructions-googlegadget");
                    Element.removeClassName("share-links-link", "s-selected");
                    Element.removeClassName("share-links-page", "s-selected");
                    Element.removeClassName("share-links-iframe", "s-selected");
                    Element.removeClassName("share-links-embed", "s-selected");
                    Element.removeClassName("share-links-embed-link", "s-selected");
                    Element.removeClassName("share-links-form", "s-selected");
                    Element.removeClassName("share-links-googlegadget", "s-selected");
                    Element.show("share-code-" + kind);
                    Element.show("share-instructions-" + kind);
                    Element.addClassName("share-links-" + kind, "s-selected");
                    return false;
                }
            },
            shareFlow: {
                showCode: function(kind) {
                    Element.hide("share-code-link");
                    Element.hide("share-code-page");
                    Element.hide("share-code-embed");
                    Element.hide("share-code-embed-link");
                    Element.hide("share-code-flow");
                    Element.hide("share-code-googlegadget");
                    Element.hide("share-instructions-link");
                    Element.hide("share-instructions-page");
                    Element.hide("share-instructions-embed");
                    Element.hide("share-instructions-embed-link");
                    Element.hide("share-instructions-flow");
                    Element.hide("share-instructions-googlegadget");
                    Element.removeClassName("share-links-link", "s-selected");
                    Element.removeClassName("share-links-page", "s-selected");
                    Element.removeClassName("share-links-embed", "s-selected");
                    Element.removeClassName("share-links-embed-link", "s-selected");
                    Element.removeClassName("share-links-flow", "s-selected");
                    Element.removeClassName("share-links-googlegadget", "s-selected");
                    Element.show("share-code-" + kind);
                    Element.show("share-instructions-" + kind);
                    Element.addClassName("share-links-" + kind, "s-selected");
                    return false;
                }
            },
            shareTasks: {
                showCode: function(kind) {
                    Element.hide("share-code-link");
                    Element.hide("share-code-embed");
                    Element.hide("share-code-googlegadget");
                    Element.hide("share-instructions-link");
                    Element.hide("share-instructions-embed");
                    Element.hide("share-instructions-googlegadget");
                    Element.removeClassName("share-links-link", "s-selected");
                    Element.removeClassName("share-links-embed", "s-selected");
                    Element.removeClassName("share-links-googlegadget", "s-selected");
                    Element.show("share-code-" + kind);
                    Element.show("share-instructions-" + kind);
                    Element.addClassName("share-links-" + kind, "s-selected");
                    return false;
                }
            },
            cssUtil: {
                setBrowserSelector: function() {
                    var ua = navigator.userAgent.toLowerCase(),
                        is = function(t) {
                            return ua.indexOf(t) != -1;
                        },
                        h = document.getElementsByTagName("html")[0],
                        b = (!(/opera|webtv/i.test(ua)) && /msie (\d)/.test(ua)) ? ("ie ie" + RegExp.$1) : is("gecko/") ? "gecko" : is("opera/9") ? "opera opera9" : /opera (\d)/.test(ua) ? "opera opera" + RegExp.$1 : is("konqueror") ? "konqueror" : is("applewebkit/") ? "webkit safari" : is("mozilla/") ? "gecko" : "",
                        os = (is("x11") || is("linux")) ? " linux" : is("mac") ? " mac" : is("win") ? " win" : "";
                    if (_frevvo.utilities.IEVersion) {
                        b = "ie ie" + _frevvo.utilities.IEVersion;
                    }
                    var c = b + os + " js";
                    h.className += h.className ? " " + c : c;
                }
            },
            util: {
                idCounter: 0,
                makeId: function() {
                    return "e-generated-id" + this.idCounter++;
                },
                isFunction: function(a) {
                    return typeof a == "function";
                },
                isNull: function(a) {
                    return typeof a == "object" && !a;
                },
                isNumber: function(a) {
                    return typeof a == "number" && isFinite(a);
                },
                isObject: function(a) {
                    return (a && typeof a == "object") || this.isFunction(a);
                },
                isString: function(a) {
                    return typeof a == "string";
                },
                isUndefined: function(a) {
                    return typeof a == "undefined";
                },
                isDefined: function(a) {
                    return typeof a != "undefined";
                },
                isArray: function(a) {
                    return this.isObject(a) && a.constructor == Array;
                },
                plusRegex: new RegExp("\\+", "g"),
                escapePlus: function(s) {
                    s = encodeURIComponent(s);
                    return s.replace(this.plusRegex, "%2B");
                },
                zero: function(v) {
                    v = parseInt(v);
                    return (!isNaN(v) ? v : 0);
                },
                getCharFromKeyEvent: function(keyEvent) {
                    if (keyEvent.keyCode === 16) {
                        return "";
                    }
                    if (keyEvent.keyCode === 48) {
                        return (keyEvent.shiftKey) ? ")" : "0";
                    }
                    if (keyEvent.keyCode === 49) {
                        return (keyEvent.shiftKey) ? "!" : "1";
                    }
                    if (keyEvent.keyCode === 50) {
                        return (keyEvent.shiftKey) ? "@" : "2";
                    }
                    if (keyEvent.keyCode === 51) {
                        return (keyEvent.shiftKey) ? "#" : "3";
                    }
                    if (keyEvent.keyCode === 52) {
                        return (keyEvent.shiftKey) ? "$" : "4";
                    }
                    if (keyEvent.keyCode === 53) {
                        return (keyEvent.shiftKey) ? "%" : "5";
                    }
                    if (keyEvent.keyCode === 54) {
                        return (keyEvent.shiftKey) ? "^" : "6";
                    }
                    if (keyEvent.keyCode === 55) {
                        return (keyEvent.shiftKey) ? "&" : "7";
                    }
                    if (keyEvent.keyCode === 56) {
                        return (keyEvent.shiftKey) ? "*" : "8";
                    }
                    if (keyEvent.keyCode === 57) {
                        return (keyEvent.shiftKey) ? "(" : "9";
                    }
                    if (keyEvent.keyCode === 59) {
                        return (keyEvent.shiftKey) ? ":" : ";";
                    }
                    if (keyEvent.keyCode === 61) {
                        return (keyEvent.shiftKey) ? "+" : "=";
                    }
                    if (keyEvent.keyCode === 173) {
                        return (keyEvent.shiftKey) ? "_" : "-";
                    }
                    if (keyEvent.keyCode === 188) {
                        return (keyEvent.shiftKey) ? "<" : ",";
                    }
                    if (keyEvent.keyCode === 189) {
                        return (keyEvent.shiftKey) ? "_" : "-";
                    }
                    if (keyEvent.keyCode === 190) {
                        return (keyEvent.shiftKey) ? ">" : ".";
                    }
                    if (keyEvent.keyCode === 191) {
                        return (keyEvent.shiftKey) ? "?" : "/";
                    }
                    if (keyEvent.keyCode === 192) {
                        return (keyEvent.shiftKey) ? "~" : "`";
                    }
                    if (keyEvent.keyCode === 219) {
                        return (keyEvent.shiftKey) ? "{" : "[";
                    }
                    if (keyEvent.keyCode === 221) {
                        return (keyEvent.shiftKey) ? "}" : "]";
                    }
                    if (keyEvent.keyCode === 220) {
                        return (keyEvent.shiftKey) ? "|" : "\\";
                    }
                    if (keyEvent.keyCode === 222) {
                        return (keyEvent.shiftKey) ? '"' : "'";
                    }
                    return String.fromCharCode(keyEvent.keyCode);
                }
            },
            ajaxRequest: {
                send: function(requestUrl, opts) {
                    opts.contentType = "application/x-www-form-urlencoded;charset=utf-8";
                    opts.originalOnSuccess = opts.onSuccess;
                    opts.originalOnFailure = opts.onFailure;
                    opts.retries = 0;
                    opts.url = requestUrl;
                    opts.onSuccess = function(t, json, options, debug) {
                        try {
                            if (t.responseText && (t.responseText.indexOf("page-login") > 0)) {
                                document.location.href = document.location.href;
                            } else {
                                if (t.responseText && (t.responseText.indexOf("page-error") > 0) && !(options.ignorePageError)) {
                                    _frevvo.lightBoxView.showInfoDialog(_frevvo.protoView.makeNode("<span>" + _frevvo.localeStrings.sessionexpired + "</span>"), _frevvo.localeStrings.error);
                                } else {
                                    if (options.originalOnSuccess) {
                                        if (_frevvo.utilities.debugPrint && debug) {
                                            _frevvo.utilities.debugPrint(debug);
                                        }
                                        options.originalOnSuccess(t, json, options);
                                    }
                                }
                            }
                        } finally {
                            _frevvo.utilities.mutex.decrement();
                        }
                    };
                    opts.onFailure = function(t, json, options, debug) {
                        if (t.getResponseHeader("frevvo.app.error") == null && t.status >= 500 && options.retries < 3) {
                            options.retries++;
                            var timeout = 300 * options.retries;
                            setTimeout(function() {
                                new Ajax.Request(options.url, options);
                            }, timeout);
                        } else {
                            try {
                                if (t.responseText && (t.responseText.indexOf("page-login") > 0)) {
                                    document.location.href = document.location.href;
                                } else {
                                    if (options.originalOnFailure) {
                                        if (debug) {
                                            _frevvo.utilities.debugPrint(debug);
                                        }
                                        options.originalOnFailure(t, json, options, t.getResponseHeader("frevvo.app.error.message"));
                                    }
                                }
                            } finally {
                                _frevvo.utilities.mutex.decrement();
                            }
                        }
                    };
                    _frevvo.utilities.mutex.increment();
                    new Ajax.Request(requestUrl, opts);
                },
                showError: function(message, title, refresh) {
                    if (refresh) {
                        _frevvo.lightBoxView.showWaitDialog(_frevvo.protoView.makeNode(title), title);
                        w = window;
                        setTimeout(function() {
                            _frevvo.lightBoxView.hideWaitDialog();
                            w.location.reload(true);
                        }, 5000);
                    } else {
                        _frevvo.lightBoxView.showInfoDialog(_frevvo.protoView.makeNode(message), title);
                    }
                },
                requestFailedDueToOffline: function(t) {
                    if (t.status === 0) {
                        return true;
                    }
                    return false;
                }
            },
            mutex: {
                pendingRequestCounter: 0,
                increment: function() {
                    _frevvo.utilities.mutex.pendingRequestCounter++;
                },
                decrement: function() {
                    _frevvo.utilities.mutex.pendingRequestCounter--;
                },
                checkCounter: function() {
                    if (_frevvo.utilities.mutex.pendingRequestCounter == 0) {
                        return true;
                    } else {
                        return false;
                    }
                }
            },
            formInputs: {
                getInputs: function(el) {
                    var ipts = new Array();
                    ipts = ipts.concat($A(el.getElementsByTagName("input")), $A(el.getElementsByTagName("select")), $A(el.getElementsByTagName("textarea")));
                    return ipts;
                },
                is: function(el) {
                    var tagName = el.tagName;
                    if (tagName == "input" || tagName == "select" || tagName == "textarea") {
                        return true;
                    }
                    return false;
                }
            },
            newForm: {
                templates: null,
                setTemplate: function(el, tid, head, desc) {
                    if (tid) {
                        _frevvo.wizardController.data.templateId = tid;
                    } else {
                        _frevvo.wizardController.data.templateId = "";
                    }
                    if (desc) {
                        document.getElementById("template-info-head").innerHTML = head;
                        document.getElementById("template-info-desc").innerHTML = desc;
                    }
                },
                setApplication: function(options, aid) {
                    _frevvo.wizardController.data.application = options[aid].value;
                },
                setTheme: function(options, tid) {
                    _frevvo.wizardController.data.theme = options[tid].value;
                    var thumbnail = options[tid].getAttribute("thumbnail");
                    if (thumbnail) {
                        document.getElementById("theme-thumbnail-img").src = thumbnail.replace(" ", "");
                    }
                },
                setFormName: function(value) {
                    _frevvo.wizardController.data.formname = value;
                },
                setWidth: function(options, tid) {
                    _frevvo.wizardController.data.width = options[tid].value;
                },
                setControlLayout: function(options, tid) {
                    _frevvo.wizardController.data.controlLayout = options[tid].value;
                },
                setFontName: function(options, tid) {
                    _frevvo.wizardController.data.fontname = options[tid].value;
                },
                setFontSize: function(options, tid) {
                    _frevvo.wizardController.data.fontsize = options[tid].value;
                },
                setFontColor: function(value) {
                    _frevvo.wizardController.data.fontcolour = value;
                },
                setEmail: function(value) {
                    _frevvo.wizardController.data.email = value;
                },
                setThankYou: function(value) {
                    _frevvo.wizardController.data.thankyou = value;
                },
                setUri: function(value) {
                    _frevvo.wizardController.data.uri = value;
                }
            },
            savedForms: {
                setSubmissionId: function(options, tid) {
                    _frevvo.wizardController.data.submissionId = options[tid].value;
                }
            },
            auth: {
                setUserName: function(value) {
                    _frevvo.wizardController.data.userName = value;
                },
                setPassword: function(value) {
                    _frevvo.wizardController.data.password = value;
                },
                setTenantId: function(value) {
                    _frevvo.wizardController.data.tenantId = value;
                }
            },
            absolutePositioner: {
                display: function(refEl, displayEl, containerEl, radius) {
                    displayEl.style.position = "absolute";
                    displayEl.style.visibility = "visible";
                    displayEl.style.display = "block";
                    displayEl.style.zIndex = 10000;
                    var pos = _frevvo.utilities.absolutePositioner.calculatePosition(refEl, displayEl, containerEl, radius);
                    if (pos.y < 0) {
                        pos.y = 0;
                    }
                    if (pos.x < 0) {
                        pos.x = 0;
                    }
                    displayEl.style.left = pos.x + "px";
                    displayEl.style.top = pos.y + "px";
                },
                displayTopCenter: function(displayEl, containerEl, radius) {
                    radius = radius || 10;
                    displayEl.style.position = "absolute";
                    displayEl.style.visibility = "visible";
                    displayEl.style.display = "block";
                    displayEl.style.zIndex = 10000;
                    var boxwidth = Element.getDimensions(displayEl).width;
                    var boxheight = Element.getDimensions(displayEl).height;
                    var realOffset = Position.realOffset(containerEl);
                    var containerOffset = Position.cumulativeOffset(containerEl);
                    var y = containerOffset[1] + realOffset[1] + radius;
                    var x = (containerOffset[0] + realOffset[0] + containerEl.offsetWidth) / 2 - (boxwidth / 2);
                    if (y < 0) {
                        y = 0;
                    }
                    if (x < 0) {
                        x = 0;
                    }
                    displayEl.style.left = x + "px";
                    displayEl.style.top = y + "px";
                },
                displayCenterFixed: function(windowEl, displayEl) {
                    var height = windowEl.document.documentElement.clientHeight || windowEl.document.body.clientHeight;
                    var width = windowEl.innerWidth || (windowEl.document.documentElement.clientWidth || windowEl.document.body.clientWidth);
                    var boxwidth = Element.getDimensions(displayEl).width;
                    var boxheight = Element.getDimensions(displayEl).height;
                    var xPos = (width / 2) - (boxwidth / 2);
                    if (xPos < 0) {
                        xPos = 0;
                    }
                    var yPos = (height / 2) - (boxheight / 2);
                    if (yPos < 0) {
                        yPos = 0;
                    }
                    displayEl.style.position = "fixed";
                    displayEl.style.left = xPos + "px";
                    displayEl.style.top = yPos + "px";
                },
                undisplay: function(displayEl) {
                    if (displayEl) {
                        displayEl.style.visibility = "";
                        displayEl.style.display = "";
                        displayEl.style.zIndex = "";
                        displayEl.style.left = "";
                        displayEl.style.top = "";
                        displayEl.style.position = "";
                    }
                },
                calculatePosition: function(refEl, displayEl, containerEl, radius) {
                    if (!radius) {
                        radius = 0;
                    }
                    var refOffset = Position.cumulativeOffset(refEl);
                    var refDimensions = Element.getDimensions(refEl);
                    var cx = refOffset[0] + refDimensions.width / 2;
                    var cy = refOffset[1] + refDimensions.height / 2;
                    var boxwidth = Element.getDimensions(displayEl).width;
                    var boxheight = Element.getDimensions(displayEl).height;
                    var containerOffset = Position.cumulativeOffset(containerEl);
                    var position = new Object();
                    position.valid = true;
                    if (Position.within(containerEl, cx + radius + boxwidth, cy + boxheight)) {
                        position.x = cx + radius;
                        position.y = cy;
                    } else {
                        if (Position.within(containerEl, cx + radius + boxwidth, cy - radius - boxheight)) {
                            position.x = cx + radius;
                            position.y = cy - boxheight;
                        } else {
                            if (Position.within(containerEl, cx - radius - boxwidth, cy - radius - boxheight)) {
                                position.x = cx - radius - boxwidth;
                                position.y = cy - boxheight;
                            } else {
                                if (Position.within(containerEl, cx - radius - boxwidth, cy + radius + boxheight)) {
                                    position.x = cx - radius - boxwidth;
                                    position.y = cy;
                                } else {
                                    if (Position.within(containerEl, cx + radius, cy - boxheight / 2) && Position.within(containerEl, cx + radius + boxwidth, cy + boxheight / 2)) {
                                        position.x = cx + radius;
                                        position.y = cy - boxheight / 2;
                                    } else {
                                        if (Position.within(containerEl, cx - boxwidth / 2, cy + radius) && Position.within(containerEl, cx + boxwidth / 2, cy + radius + boxheight)) {
                                            position.x = cx - boxwidth / 2;
                                            position.y = cy + radius;
                                        } else {
                                            if (Position.within(containerEl, cx - radius - boxwidth, cy - boxheight / 2) && Position.within(containerEl, cx - radius, cy + boxheight / 2)) {
                                                position.x = cx - radius - boxwidth;
                                                position.y = cy - boxheight / 2;
                                            } else {
                                                if (Position.within(containerEl, cx - boxwidth / 2, cy - radius - boxheight) && Position.within(containerEl, cx + boxwidth / 2, cy - radius)) {
                                                    position.x = cx - boxwidth / 2;
                                                    position.y = cy - radius - boxheight;
                                                } else {
                                                    if (Position.within(containerEl, containerOffset[0], cy - radius - boxheight) && Position.within(containerEl, containerOffset[0] + boxwidth, cy - radius)) {
                                                        position.x = containerOffset[0];
                                                        position.y = cy - radius - boxheight;
                                                    } else {
                                                        if (Position.within(containerEl, containerOffset[0], cy + radius) && Position.within(containerEl, containerOffset[0] + boxwidth, cy + radius + boxheight)) {
                                                            position.x = containerOffset[0];
                                                            position.y = cy + radius;
                                                        } else {
                                                            var containerOffset = Position.cumulativeOffset(containerEl);
                                                            var centerY = (containerOffset[1] + containerEl.offsetHeight) / 2;
                                                            var centerX = (containerOffset[0] + containerEl.offsetWidth) / 2;
                                                            position.x = centerX - boxwidth / 2;
                                                            position.y = centerY - boxheight / 2;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    return position;
                }
            }
        }
    };
    window._frevvo = _frevvo;
})();
_frevvo.utilities.IEVersion = (function() {
    var c;
    var d = -1;
    var a = navigator.userAgent;
    var b = new RegExp("MSIE ([0-9]{1,}[.0-9]{0,})");
    if (b.exec(a) != null) {
        d = parseFloat(RegExp.$1);
    }
    if (d < 0) {
        b = new RegExp("Trident/.*rv:([0-9]{1,}[.0-9]{0,})");
        if (b.exec(a) != null) {
            d = parseFloat(RegExp.$1);
        }
    }
    return d > 4 ? d : c;
}());
_frevvo.context = document.getElementById("frevvo.context");
if (_frevvo.context) {
    _frevvo.context = _frevvo.context.content;
}

function initMobile() {
    MUtil.updateOrientation();
    if (window.addEventListener) {
        window.addEventListener("orientationchange", MUtil.updateOrientation, false);
    } else {
        if (window.attachEvent) {
            window.attachEvent("onorientationchange", MUtil.updateOrientation);
        }
    }
    /mobi/i.test(navigator.userAgent) && !location.hash && setTimeout(function() {
        if (!pageYOffset) {
            window.scrollTo(0, 1);
        }
    }, 1000);
}

function gup(b, e) {
    b = b.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
    var a = "[\\?&]" + b + "=([^&#]*)";
    var d = new RegExp(a);
    var c = d.exec(window.location.href);
    if (c == null) {
        return e ? e : "";
    } else {
        return decodeURIComponent(c[1].replace("+", " "));
    }
}
var FormTz = {
    timezone: null,
    getName: function() {
        if (this.timezone != null) {
            return this.timezone;
        }
        try {
            if (jstz) {
                var b = jstz.determine_timezone();
                this.timezone = encodeURIComponent(b.name());
                return this.timezone;
            }
        } catch (a) {}
        return null;
    },
    addTz: function(a) {
        if (a != null) {
            a += a.indexOf("?") != -1 ? "&" : "?";
            a += "_formTz=" + FormTz.getName();
        }
        return a;
    }
};
var QuickActions = {
    approve: function() {
        var a = document.getElementById("quick-form");
        a.action += "&action=approve";
        a.action = MUtil.addParam(a.action, "locale");
        a.action = MUtil.addParam(a.action, "_themeId");
        a.action = MUtil.addParam(a.action, "_styleId");
        a.submit();
    },
    reject: function() {
        var a = document.location.href.replace("/quick/view", "/quick/action");
        a += (a.indexOf("?") >= 0) ? "&" : "?";
        a += "reject=true&_task=true";
        window.location.href = a;
    },
    details: function(a) {
        window.location.href = a + "&_task=true&_formTz=" + FormTz.getName();
    }
};
var MUtil = {
    updateOrientation: function() {
        switch (window.orientation) {
            case 0:
                MUtil.removeClass(document.body, "landscape");
                MUtil.removeClass(document.body, "portrait");
                MUtil.addClass(document.body, "portrait");
                break;
            case 90:
            case -90:
                MUtil.removeClass(document.body, "portrait");
                MUtil.removeClass(document.body, "landscape");
                MUtil.addClass(document.body, "landscape");
                break;
            default:
                MUtil.removeClass(document.body, "landscape");
                MUtil.removeClass(document.body, "portrait");
                MUtil.addClass(document.body, "portrait");
                break;
        }
    },
    addParam: function(c, a) {
        var b = gup(a);
        if (b) {
            if (c.indexOf("?") > 0) {
                c += "&" + a + "=" + b;
            } else {
                c += "?" + a + "=" + b;
            }
        }
        return c;
    },
    addClass: function(b, a) {
        b.className += " " + a;
    },
    removeClass: function(b, a) {
        b.className = b.className.replace(a, "");
    },
    device: function() {
        var b = document.URL.indexOf("_device");
        var a = /_device=([^(\&|#)]*)/.exec(document.URL.substring(b));
        return (a && a[0]) || null;
    },
    removeUrlParams: function(a) {
        if (a) {
            a = a.replace(/\?(.)*/g, "");
            a = a.replace(/\#(.)*/g, "");
        }
        return a;
    },
    removeUrlParam: function(a, f) {
        var d = a.split("?");
        if (d.length >= 2) {
            var e = encodeURIComponent(f) + "=";
            var c = d[1].split(/[&;]/g);
            for (var b = c.length; b-- > 0;) {
                if (c[b].lastIndexOf(e, 0) !== -1) {
                    c.splice(b, 1);
                }
            }
            a = d[0] + "?" + c.join("&");
        }
        return a;
    },
    observe: function(d, c, b, a) {
        a = a || false;
        if (d.addEventListener) {
            d.addEventListener(c, b, a);
        } else {
            if (d.attachEvent) {
                d.attachEvent("on" + c, b);
            }
        }
    },
    stopObserving: function(d, c, b, a) {
        a = a || false;
        if (d.removeEventListener) {
            d.removeEventListener(c, b, a);
        } else {
            if (d.detachEvent) {
                try {
                    d.detachEvent("on" + c, b);
                } catch (f) {}
            }
        }
    },
    showForm: function(d, c, b) {
        if (c) {
            var e = function(f) {
                if (f.data == "DISPOSAL COMPLETE") {
                    MUtil.doShowForm(d);
                    MUtil.stopObserving(window, "message", e);
                }
            };
            MUtil.observe(window, "message", e);
            var a = "*";
            if (b) {
                a = b;
            }
            document.getElementById(c).contentWindow.postMessage("DISPOSE", a);
        } else {
            MUtil.doShowForm(d);
        }
    },
    doShowForm: function(a) {
        var b = MUtil.removeUrlParam(document.URL, "_device");
        b = MUtil.removeUrlParam(b, "height");
        b = MUtil.removeUrlParam(b, "width");
        b = MUtil.removeUrlParam(b, "_extId");
        if ("tablet" == a) {
            window.location.href = b + "&_device=tablet&width=768&height=1024&_extId=" + Math.random();
        } else {
            if ("phone" == a) {
                window.location.href = b + "&_device=phone&width=320&height=590&_extId=" + Math.random();
            } else {
                if ("desktop" == a) {
                    window.location.href = b + "&_device=desktop&_extId=" + Math.random();
                }
            }
        }
    },
    setFormColumns: function(d, c) {
        var g = document.getElementById(d);
        if (g) {
            var a = g.contentDocument.body;
            MUtil.removeClass(a, "Four");
            MUtil.removeClass(a, "Six");
            MUtil.removeClass(a, "Eight");
            MUtil.removeClass(a, "Twelve");
            MUtil.addClass(a, c);
            var e = window.frames[0].document.getElementById("form-container");
            if ("Four" == c) {
                e.style.width = "320px";
            } else {
                if ("Six" == c) {
                    e.style.width = "480px";
                } else {
                    if ("Eight" == c) {
                        e.style.width = "768px";
                    } else {
                        if ("Twelve" == c) {
                            e.style.width = e.getAttribute("ow");
                        }
                    }
                }
            }
        }
    }
};
Function.prototype.bindAsObserver = function() {
    var a = this,
        c = $A(arguments),
        b = c.shift();
    return function(d) {
        c.unshift(d || window.event);
        try {
            return a.apply(b, c);
        } finally {
            c.shift();
        }
    };
};
var FrevvoConstants = {
    BAD_LABEL: 1,
    BAD_HELP: 2,
    BAD_NAME: 4,
    BAD_EXTID: 8,
    BAD_ROWS: 16,
    BAD_MAXLENGTH: 32,
    BAD_WIDTH: 64,
    DUP_NAME: 128,
    BAD_MINFILES: 256,
    BAD_MAXFILES: 512,
    BAD_URI: 1024,
    BAD_OPTION_LABEL: 2048,
    BAD_POS_INTERVAL: 4096,
    REFRESH_EDITOR: 2049,
    SAVE_TO_USER_QUICK_VIEW_CONFLICT: 1,
    SAVE_TO_ROLE_QUICK_VIEW_CONFLICT: 2,
    TRIAL_USER: 1,
    AUTH_REQUIRED: 2,
    AUTH_SUCCESS: 4,
    SAVE_SUCCESS: 4,
    BAD_PASSWORD: 8,
    SAVE_OU_SUCCESS: 8,
    AUTH_ANONYMOUS: 16,
    TOO_MANY_USERS: 32,
    FILE_TOO_BIG: 64,
    ACCOUNT_DISABLED: 128,
    CAPTCHA_SUCCESS: 1,
    CAPTCHA_FAILURE: 2,
    SESSION_EXPIRED: 256,
    REMOVE_TASK: 1,
    APPROVE_ERROR: 2,
    EMPTY_STRING: "",
    SELECTED_CLASS: "s-selected",
    NOT_SELECTED_CLASS: "s-not-selected",
    INVALID_UNSELECTED_CLASS: "s-unselected-invalid",
    CONTROL_ID: "id"
};
_frevvo.Errors = Class.create();
_frevvo.Errors.prototype = {
    _status: null,
    _statusText: null,
    initialize: function(a, b) {
        this._status = a;
        this._statusText = decodeURIComponent(b);
    },
    getMessage: function() {
        return "<span><h4>Error</h4>Message: " + this._statusText + "</span>";
    }
};
String.prototype.parseColor = function() {
    var a = "#";
    if (this.slice(0, 4) == "rgb(") {
        var c = this.slice(4, this.length - 1).split(",");
        var b = 0;
        do {
            a += parseInt(c[b]).toColorPart();
        } while (++b < 3);
    } else {
        if (this.slice(0, 1) == "#") {
            if (this.length == 4) {
                for (var b = 1; b < 4; b++) {
                    a += (this.charAt(b) + this.charAt(b)).toLowerCase();
                }
            }
            if (this.length == 7) {
                a = this.toLowerCase();
            }
        }
    }
    return (a.length == 7 ? a : (arguments[0] || this));
};
Element.collectTextNodes = function(a) {
    return $A($(a).childNodes).collect(function(b) {
        return (b.nodeType == 3 ? b.nodeValue : (b.hasChildNodes() ? Element.collectTextNodes(b) : ""));
    }).flatten().join("");
};
Element.collectTextNodesIgnoreClass = function(a, b) {
    return $A($(a).childNodes).collect(function(c) {
        return (c.nodeType == 3 ? c.nodeValue : ((c.hasChildNodes() && !Element.hasClassName(c, b)) ? Element.collectTextNodesIgnoreClass(c, b) : ""));
    }).flatten().join("");
};
Element.setContentZoom = function(a, b) {
    a = $(a);
    Element.setStyle(a, {
        fontSize: (b / 100) + "em"
    });
    if (navigator.appVersion.indexOf("AppleWebKit") > 0) {
        window.scrollBy(0, 0);
    }
};
Element.getOpacity = function(b) {
    var a;
    if (a = Element.getStyle(b, "opacity")) {
        return parseFloat(a);
    }
    if (a = (Element.getStyle(b, "filter") || "").match(/alpha\(opacity=(.*)\)/)) {
        if (a[1]) {
            return parseFloat(a[1]) / 100;
        }
    }
    return 1;
};
Element.setOpacity = function(a, b) {
    a = $(a);
    if (b == 1) {
        Element.setStyle(a, {
            opacity: (/Gecko/.test(navigator.userAgent) && !/Konqueror|Safari|KHTML/.test(navigator.userAgent)) ? 0.999999 : 1
        });
        if (/MSIE/.test(navigator.userAgent) && !window.opera) {
            Element.setStyle(a, {
                filter: Element.getStyle(a, "filter").replace(/alpha\([^\)]*\)/gi, "")
            });
        }
    } else {
        if (b < 0.00001) {
            b = 0;
        }
        Element.setStyle(a, {
            opacity: b
        });
        if (/MSIE/.test(navigator.userAgent) && !window.opera) {
            Element.setStyle(a, {
                filter: Element.getStyle(a, "filter").replace(/alpha\([^\)]*\)/gi, "") + "alpha(opacity=" + b * 100 + ")"
            });
        }
    }
};
Element.getInlineOpacity = function(a) {
    return $(a).style.opacity || "";
};
Element.childrenWithClassName = function(c, d, e) {
    var b = new RegExp("(^|\\s)" + d + "(\\s|$)");
    var a = $A($(c).getElementsByTagName("*"))[e ? "detect" : "select"](function(f) {
        return (f.className && f.className.match(b));
    });
    if (!a) {
        a = [];
    }
    return a;
};
Element.forceRerendering = function(a) {
    try {
        a = $(a);
        var c = document.createTextNode(" ");
        a.appendChild(c);
        a.removeChild(c);
    } catch (b) {}
};
Array.prototype.call = function() {
    var a = arguments;
    this.each(function(b) {
        b.apply(this, a);
    });
};
var Effect = {
    _elementDoesNotExistError: {
        name: "ElementDoesNotExistError",
        message: "The specified DOM element does not exist, but is required for this effect to operate"
    },
    tagifyText: function(a) {
        if (typeof Builder == "undefined") {
            throw ("Effect.tagifyText requires including script.aculo.us' builder.js library");
        }
        var b = "position:relative";
        if (/MSIE/.test(navigator.userAgent) && !window.opera) {
            b += ";zoom:1";
        }
        a = $(a);
        $A(a.childNodes).each(function(c) {
            if (c.nodeType == 3) {
                c.nodeValue.toArray().each(function(d) {
                    a.insertBefore(Builder.node("span", {
                        style: b
                    }, d == " " ? String.fromCharCode(160) : d), c);
                });
                Element.remove(c);
            }
        });
    },
    multiple: function(b, c) {
        var e;
        if (((typeof b == "object") || (typeof b == "function")) && (b.length)) {
            e = b;
        } else {
            e = $(b).childNodes;
        }
        var a = Object.extend({
            speed: 0.1,
            delay: 0
        }, arguments[2] || {});
        var d = a.delay;
        $A(e).each(function(g, f) {
            new c(g, Object.extend(a, {
                delay: f * a.speed + d
            }));
        });
    },
    PAIRS: {
        slide: ["SlideDown", "SlideUp"],
        blind: ["BlindDown", "BlindUp"],
        appear: ["Appear", "Fade"]
    },
    toggle: function(b, c) {
        b = $(b);
        c = (c || "appear").toLowerCase();
        var a = Object.extend({
            queue: {
                position: "end",
                scope: (b.id || "global"),
                limit: 1
            }
        }, arguments[2] || {});
        Effect[b.visible() ? Effect.PAIRS[c][1] : Effect.PAIRS[c][0]](b, a);
    }
};
var Effect2 = Effect;
Effect.Transitions = {};
Effect.Transitions.linear = Prototype.K;
Effect.Transitions.sinoidal = function(a) {
    return (-Math.cos(a * Math.PI) / 2) + 0.5;
};
Effect.Transitions.reverse = function(a) {
    return 1 - a;
};
Effect.Transitions.flicker = function(a) {
    return ((-Math.cos(a * Math.PI) / 4) + 0.75) + Math.random() / 4;
};
Effect.Transitions.wobble = function(a) {
    return (-Math.cos(a * Math.PI * (9 * a)) / 2) + 0.5;
};
Effect.Transitions.pulse = function(a) {
    return (Math.floor(a * 10) % 2 == 0 ? (a * 10 - Math.floor(a * 10)) : 1 - (a * 10 - Math.floor(a * 10)));
};
Effect.Transitions.none = function(a) {
    return 0;
};
Effect.Transitions.full = function(a) {
    return 1;
};
Effect.ScopedQueue = Class.create();
Object.extend(Object.extend(Effect.ScopedQueue.prototype, Enumerable), {
    initialize: function() {
        this.effects = [];
        this.interval = null;
    },
    _each: function(a) {
        this.effects._each(a);
    },
    add: function(b) {
        var c = new Date().getTime();
        var a = (typeof b.options.queue == "string") ? b.options.queue : b.options.queue.position;
        switch (a) {
            case "front":
                this.effects.findAll(function(d) {
                    return d.state == "idle";
                }).each(function(d) {
                    d.startOn += b.finishOn;
                    d.finishOn += b.finishOn;
                });
                break;
            case "end":
                c = this.effects.pluck("finishOn").max() || c;
                break;
        }
        b.startOn += c;
        b.finishOn += c;
        if (!b.options.queue.limit || (this.effects.length < b.options.queue.limit)) {
            this.effects.push(b);
        }
        if (!this.interval) {
            this.interval = setInterval(this.loop.bind(this), 40);
        }
    },
    remove: function(a) {
        this.effects = this.effects.reject(function(b) {
            return b == a;
        });
        if (this.effects.length == 0) {
            clearInterval(this.interval);
            this.interval = null;
        }
    },
    loop: function() {
        var a = new Date().getTime();
        this.effects.invoke("loop", a);
    }
});
Effect.Queues = {
    instances: $H(),
    get: function(a) {
        if (typeof a != "string") {
            return a;
        }
        if (!this.instances[a]) {
            this.instances[a] = new Effect.ScopedQueue();
        }
        return this.instances[a];
    }
};
Effect.Queue = Effect.Queues.get("global");
Effect.DefaultOptions = {
    transition: Effect.Transitions.sinoidal,
    duration: 1,
    fps: 25,
    sync: false,
    from: 0,
    to: 1,
    delay: 0,
    queue: "parallel"
};
Effect.Base = function() {};
Effect.Base.prototype = {
    position: null,
    start: function(a) {
        this.options = Object.extend(Object.extend({}, Effect.DefaultOptions), a || {});
        this.currentFrame = 0;
        this.state = "idle";
        this.startOn = this.options.delay * 1000;
        this.finishOn = this.startOn + (this.options.duration * 1000);
        this.event("beforeStart");
        if (!this.options.sync) {
            Effect.Queues.get(typeof this.options.queue == "string" ? "global" : this.options.queue.scope).add(this);
        }
    },
    loop: function(c) {
        if (c >= this.startOn) {
            if (c >= this.finishOn) {
                this.render(1);
                this.cancel();
                this.event("beforeFinish");
                if (this.finish) {
                    this.finish();
                }
                this.event("afterFinish");
                return;
            }
            var b = (c - this.startOn) / (this.finishOn - this.startOn);
            var a = Math.round(b * this.options.fps * this.options.duration);
            if (a > this.currentFrame) {
                this.render(b);
                this.currentFrame = a;
            }
        }
    },
    render: function(a) {
        if (this.state == "idle") {
            this.state = "running";
            this.event("beforeSetup");
            if (this.setup) {
                this.setup();
            }
            this.event("afterSetup");
        }
        if (this.state == "running") {
            if (this.options.transition) {
                a = this.options.transition(a);
            }
            a *= (this.options.to - this.options.from);
            a += this.options.from;
            this.position = a;
            this.event("beforeUpdate");
            if (this.update) {
                this.update(a);
            }
            this.event("afterUpdate");
        }
    },
    cancel: function() {
        if (!this.options.sync) {
            Effect.Queues.get(typeof this.options.queue == "string" ? "global" : this.options.queue.scope).remove(this);
        }
        this.state = "finished";
    },
    event: function(a) {
        if (this.options[a + "Internal"]) {
            this.options[a + "Internal"](this);
        }
        if (this.options[a]) {
            this.options[a](this);
        }
    },
    inspect: function() {
        return "#<Effect:" + $H(this).inspect() + ",options:" + $H(this.options).inspect() + ">";
    }
};
Effect.Parallel = Class.create();
Object.extend(Object.extend(Effect.Parallel.prototype, Effect.Base.prototype), {
    initialize: function(a) {
        this.effects = a || [];
        this.start(arguments[1]);
    },
    update: function(a) {
        this.effects.invoke("render", a);
    },
    finish: function(a) {
        this.effects.each(function(b) {
            b.render(1);
            b.cancel();
            b.event("beforeFinish");
            if (b.finish) {
                b.finish(a);
            }
            b.event("afterFinish");
        });
    }
});
Effect.Opacity = Class.create();
Object.extend(Object.extend(Effect.Opacity.prototype, Effect.Base.prototype), {
    initialize: function(b) {
        this.element = $(b);
        if (!this.element) {
            throw (Effect._elementDoesNotExistError);
        }
        if (/MSIE/.test(navigator.userAgent) && !window.opera && (!this.element.currentStyle.hasLayout)) {
            this.element.setStyle({
                zoom: 1
            });
        }
        var a = Object.extend({
            from: this.element.getOpacity() || 0,
            to: 1
        }, arguments[1] || {});
        this.start(a);
    },
    update: function(a) {
        this.element.setOpacity(a);
    }
});
Effect.Move = Class.create();
Object.extend(Object.extend(Effect.Move.prototype, Effect.Base.prototype), {
    initialize: function(b) {
        this.element = $(b);
        if (!this.element) {
            throw (Effect._elementDoesNotExistError);
        }
        var a = Object.extend({
            x: 0,
            y: 0,
            mode: "relative"
        }, arguments[1] || {});
        this.start(a);
    },
    setup: function() {
        this.element.makePositioned();
        this.originalLeft = parseFloat(this.element.getStyle("left") || "0");
        this.originalTop = parseFloat(this.element.getStyle("top") || "0");
        if (this.options.mode == "absolute") {
            this.options.x = this.options.x - this.originalLeft;
            this.options.y = this.options.y - this.originalTop;
        }
    },
    update: function(a) {
        this.element.setStyle({
            left: Math.round(this.options.x * a + this.originalLeft) + "px",
            top: Math.round(this.options.y * a + this.originalTop) + "px"
        });
    }
});
Effect.MoveBy = function(b, a, c) {
    return new Effect.Move(b, Object.extend({
        x: c,
        y: a
    }, arguments[3] || {}));
};
Effect.Scale = Class.create();
Object.extend(Object.extend(Effect.Scale.prototype, Effect.Base.prototype), {
    initialize: function(b, c) {
        this.element = $(b);
        if (!this.element) {
            throw (Effect._elementDoesNotExistError);
        }
        var a = Object.extend({
            scaleX: true,
            scaleY: true,
            scaleContent: true,
            scaleFromCenter: false,
            scaleMode: "box",
            scaleFrom: 100,
            scaleTo: c
        }, arguments[2] || {});
        this.start(a);
    },
    setup: function() {
        this.restoreAfterFinish = this.options.restoreAfterFinish || false;
        this.elementPositioning = this.element.getStyle("position");
        this.originalStyle = {};
        ["top", "left", "width", "height", "fontSize"].each(function(b) {
            this.originalStyle[b] = this.element.style[b];
        }.bind(this));
        this.originalTop = this.element.offsetTop;
        this.originalLeft = this.element.offsetLeft;
        var a = this.element.getStyle("font-size") || "100%";
        ["em", "px", "%", "pt"].each(function(b) {
            if (a.indexOf(b) > 0) {
                this.fontSize = parseFloat(a);
                this.fontSizeType = b;
            }
        }.bind(this));
        this.factor = (this.options.scaleTo - this.options.scaleFrom) / 100;
        this.dims = null;
        if (this.options.scaleMode == "box") {
            this.dims = [this.element.offsetHeight, this.element.offsetWidth];
        }
        if (/^content/.test(this.options.scaleMode)) {
            this.dims = [this.element.scrollHeight, this.element.scrollWidth];
        }
        if (!this.dims) {
            this.dims = [this.options.scaleMode.originalHeight, this.options.scaleMode.originalWidth];
        }
    },
    update: function(a) {
        var b = (this.options.scaleFrom / 100) + (this.factor * a);
        if (this.options.scaleContent && this.fontSize) {
            this.element.setStyle({
                fontSize: this.fontSize * b + this.fontSizeType
            });
        }
        this.setDimensions(this.dims[0] * b, this.dims[1] * b);
    },
    finish: function(a) {
        if (this.restoreAfterFinish) {
            this.element.setStyle(this.originalStyle);
        }
    },
    setDimensions: function(a, e) {
        var f = {};
        if (this.options.scaleX) {
            f.width = Math.round(e) + "px";
        }
        if (this.options.scaleY) {
            f.height = Math.round(a) + "px";
        }
        if (this.options.scaleFromCenter) {
            var c = (a - this.dims[0]) / 2;
            var b = (e - this.dims[1]) / 2;
            if (this.elementPositioning == "absolute") {
                if (this.options.scaleY) {
                    f.top = this.originalTop - c + "px";
                }
                if (this.options.scaleX) {
                    f.left = this.originalLeft - b + "px";
                }
            } else {
                if (this.options.scaleY) {
                    f.top = -c + "px";
                }
                if (this.options.scaleX) {
                    f.left = -b + "px";
                }
            }
        }
        this.element.setStyle(f);
    }
});
Effect.Highlight = Class.create();
Object.extend(Object.extend(Effect.Highlight.prototype, Effect.Base.prototype), {
    initialize: function(b) {
        this.element = $(b);
        if (!this.element) {
            throw (Effect._elementDoesNotExistError);
        }
        var a = Object.extend({
            startcolor: "#ffff99"
        }, arguments[1] || {});
        this.start(a);
    },
    setup: function() {
        if (this.element.getStyle("display") == "none") {
            this.cancel();
            return;
        }
        this.oldStyle = {
            backgroundImage: this.element.getStyle("background-image")
        };
        this.element.setStyle({
            backgroundImage: "none"
        });
        if (!this.options.endcolor) {
            this.options.endcolor = this.element.getStyle("background-color").parseColor("#ffffff");
        }
        if (!this.options.restorecolor) {
            this.options.restorecolor = this.element.getStyle("background-color");
        }
        this._base = $R(0, 2).map(function(a) {
            return parseInt(this.options.startcolor.slice(a * 2 + 1, a * 2 + 3), 16);
        }.bind(this));
        this._delta = $R(0, 2).map(function(a) {
            return parseInt(this.options.endcolor.slice(a * 2 + 1, a * 2 + 3), 16) - this._base[a];
        }.bind(this));
    },
    update: function(a) {
        this.element.setStyle({
            backgroundColor: $R(0, 2).inject("#", function(b, c, d) {
                return b + (Math.round(this._base[d] + (this._delta[d] * a)).toColorPart());
            }.bind(this))
        });
    },
    finish: function() {
        this.element.setStyle(Object.extend(this.oldStyle, {
            backgroundColor: this.options.restorecolor
        }));
    }
});
Effect.ScrollTo = Class.create();
Object.extend(Object.extend(Effect.ScrollTo.prototype, Effect.Base.prototype), {
    initialize: function(a) {
        this.element = $(a);
        this.start(arguments[1] || {});
    },
    setup: function() {
        Position.prepare();
        var b = Position.cumulativeOffset(this.element);
        if (this.options.offset) {
            b[1] += this.options.offset;
        }
        var a = window.innerHeight ? window.height - window.innerHeight : document.documentElement.scrollHeight - (document.documentElement.clientHeight ? document.documentElement.clientHeight : document.body.clientHeight);
        this.scrollStart = Position.deltaY;
        this.delta = (b[1] > a ? a : b[1]) - this.scrollStart;
    },
    update: function(a) {
        Position.prepare();
        window.scrollTo(Position.deltaX, this.scrollStart + (a * this.delta));
    }
});
Effect.Fade = function(c) {
    c = $(c);
    var a = c.getInlineOpacity();
    var b = Object.extend({
        from: c.getOpacity() || 1,
        to: 0,
        afterFinishInternal: function(d) {
            if (d.options.to != 0) {
                return;
            }
            d.element.hide();
            d.element.setStyle({
                opacity: a
            });
        }
    }, arguments[1] || {});
    return new Effect.Opacity(c, b);
};
Effect.Appear = function(b) {
    b = $(b);
    var a = Object.extend({
        from: (b.getStyle("display") == "none" ? 0 : b.getOpacity() || 0),
        to: 1,
        afterFinishInternal: function(c) {
            c.element.forceRerendering();
        },
        beforeSetup: function(c) {
            c.element.setOpacity(c.options.from);
            c.element.show();
        }
    }, arguments[1] || {});
    return new Effect.Opacity(b, a);
};
Effect.Puff = function(b) {
    b = $(b);
    var a = {
        opacity: b.getInlineOpacity(),
        position: b.getStyle("position"),
        top: b.style.top,
        left: b.style.left,
        width: b.style.width,
        height: b.style.height
    };
    return new Effect.Parallel([new Effect.Scale(b, 200, {
        sync: true,
        scaleFromCenter: true,
        scaleContent: true,
        restoreAfterFinish: true
    }), new Effect.Opacity(b, {
        sync: true,
        to: 0
    })], Object.extend({
        duration: 1,
        beforeSetupInternal: function(c) {
            Position.absolutize(c.effects[0].element);
        },
        afterFinishInternal: function(c) {
            c.effects[0].element.hide();
            c.effects[0].element.setStyle(a);
        }
    }, arguments[1] || {}));
};
Effect.BlindUp = function(a) {
    a = $(a);
    a.makeClipping();
    return new Effect.Scale(a, 0, Object.extend({
        scaleContent: false,
        scaleX: false,
        restoreAfterFinish: true,
        afterFinishInternal: function(b) {
            b.element.hide();
            b.element.undoClipping();
        }
    }, arguments[1] || {}));
};
Effect.BlindDown = function(b) {
    b = $(b);
    var a = b.getDimensions();
    return new Effect.Scale(b, 100, Object.extend({
        scaleContent: false,
        scaleX: false,
        scaleFrom: 0,
        scaleMode: {
            originalHeight: a.height,
            originalWidth: a.width
        },
        restoreAfterFinish: true,
        afterSetup: function(c) {
            c.element.makeClipping();
            c.element.setStyle({
                height: "0px"
            });
            c.element.show();
        },
        afterFinishInternal: function(c) {
            c.element.undoClipping();
        }
    }, arguments[1] || {}));
};
Effect.SwitchOff = function(b) {
    b = $(b);
    var a = b.getInlineOpacity();
    return new Effect.Appear(b, Object.extend({
        duration: 0.4,
        from: 0,
        transition: Effect.Transitions.flicker,
        afterFinishInternal: function(c) {
            new Effect.Scale(c.element, 1, {
                duration: 0.3,
                scaleFromCenter: true,
                scaleX: false,
                scaleContent: false,
                restoreAfterFinish: true,
                beforeSetup: function(d) {
                    d.element.makePositioned();
                    d.element.makeClipping();
                },
                afterFinishInternal: function(d) {
                    d.element.hide();
                    d.element.undoClipping();
                    d.element.undoPositioned();
                    d.element.setStyle({
                        opacity: a
                    });
                }
            });
        }
    }, arguments[1] || {}));
};
Effect.DropOut = function(b) {
    b = $(b);
    var a = {
        top: b.getStyle("top"),
        left: b.getStyle("left"),
        opacity: b.getInlineOpacity()
    };
    return new Effect.Parallel([new Effect.Move(b, {
        x: 0,
        y: 100,
        sync: true
    }), new Effect.Opacity(b, {
        sync: true,
        to: 0
    })], Object.extend({
        duration: 0.5,
        beforeSetup: function(c) {
            c.effects[0].element.makePositioned();
        },
        afterFinishInternal: function(c) {
            c.effects[0].element.hide();
            c.effects[0].element.undoPositioned();
            c.effects[0].element.setStyle(a);
        }
    }, arguments[1] || {}));
};
Effect.Shake = function(b) {
    b = $(b);
    var a = {
        top: b.getStyle("top"),
        left: b.getStyle("left")
    };
    return new Effect.Move(b, {
        x: 20,
        y: 0,
        duration: 0.05,
        afterFinishInternal: function(c) {
            new Effect.Move(c.element, {
                x: -40,
                y: 0,
                duration: 0.1,
                afterFinishInternal: function(d) {
                    new Effect.Move(d.element, {
                        x: 40,
                        y: 0,
                        duration: 0.1,
                        afterFinishInternal: function(e) {
                            new Effect.Move(e.element, {
                                x: -40,
                                y: 0,
                                duration: 0.1,
                                afterFinishInternal: function(f) {
                                    new Effect.Move(f.element, {
                                        x: 40,
                                        y: 0,
                                        duration: 0.1,
                                        afterFinishInternal: function(g) {
                                            new Effect.Move(g.element, {
                                                x: -20,
                                                y: 0,
                                                duration: 0.05,
                                                afterFinishInternal: function(h) {
                                                    h.element.undoPositioned();
                                                    h.element.setStyle(a);
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            });
        }
    });
};
Effect.SlideDown = function(c) {
    c = $(c);
    c.cleanWhitespace();
    var a = $(c.firstChild).getStyle("bottom");
    var b = c.getDimensions();
    return new Effect.Scale(c, 100, Object.extend({
        scaleContent: false,
        scaleX: false,
        scaleFrom: window.opera ? 0 : 1,
        scaleMode: {
            originalHeight: b.height,
            originalWidth: b.width
        },
        restoreAfterFinish: true,
        afterSetup: function(d) {
            d.element.makePositioned();
            d.element.firstChild.makePositioned();
            if (window.opera) {
                d.element.setStyle({
                    top: ""
                });
            }
            d.element.makeClipping();
            d.element.setStyle({
                height: "0px"
            });
            d.element.show();
        },
        afterUpdateInternal: function(d) {
            d.element.firstChild.setStyle({
                bottom: (d.dims[0] - d.element.clientHeight) + "px"
            });
        },
        afterFinishInternal: function(d) {
            d.element.undoClipping();
            if (/MSIE/.test(navigator.userAgent) && !window.opera) {
                d.element.undoPositioned();
                d.element.firstChild.undoPositioned();
            } else {
                d.element.firstChild.undoPositioned();
                d.element.undoPositioned();
            }
            d.element.firstChild.setStyle({
                bottom: a
            });
        }
    }, arguments[1] || {}));
};
Effect.SlideUp = function(b) {
    b = $(b);
    b.cleanWhitespace();
    var a = $(b.firstChild).getStyle("bottom");
    return new Effect.Scale(b, window.opera ? 0 : 1, Object.extend({
        scaleContent: false,
        scaleX: false,
        scaleMode: "box",
        scaleFrom: 100,
        restoreAfterFinish: true,
        beforeStartInternal: function(c) {
            c.element.makePositioned();
            c.element.firstChild.makePositioned();
            if (window.opera) {
                c.element.setStyle({
                    top: ""
                });
            }
            c.element.makeClipping();
            c.element.show();
        },
        afterUpdateInternal: function(c) {
            c.element.firstChild.setStyle({
                bottom: (c.dims[0] - c.element.clientHeight) + "px"
            });
        },
        afterFinishInternal: function(c) {
            c.element.hide();
            c.element.undoClipping();
            c.element.firstChild.undoPositioned();
            c.element.undoPositioned();
            c.element.setStyle({
                bottom: a
            });
        }
    }, arguments[1] || {}));
};
Effect.Squish = function(a) {
    return new Effect.Scale(a, window.opera ? 1 : 0, {
        restoreAfterFinish: true,
        beforeSetup: function(b) {
            b.element.makeClipping(b.element);
        },
        afterFinishInternal: function(b) {
            b.element.hide(b.element);
            b.element.undoClipping(b.element);
        }
    });
};
Effect.Grow = function(c) {
    c = $(c);
    var b = Object.extend({
        direction: "center",
        moveTransition: Effect.Transitions.sinoidal,
        scaleTransition: Effect.Transitions.sinoidal,
        opacityTransition: Effect.Transitions.full
    }, arguments[1] || {});
    var a = {
        top: c.style.top,
        left: c.style.left,
        height: c.style.height,
        width: c.style.width,
        opacity: c.getInlineOpacity()
    };
    var g = c.getDimensions();
    var h, f;
    var e, d;
    switch (b.direction) {
        case "top-left":
            h = f = e = d = 0;
            break;
        case "top-right":
            h = g.width;
            f = d = 0;
            e = -g.width;
            break;
        case "bottom-left":
            h = e = 0;
            f = g.height;
            d = -g.height;
            break;
        case "bottom-right":
            h = g.width;
            f = g.height;
            e = -g.width;
            d = -g.height;
            break;
        case "center":
            h = g.width / 2;
            f = g.height / 2;
            e = -g.width / 2;
            d = -g.height / 2;
            break;
    }
    return new Effect.Move(c, {
        x: h,
        y: f,
        duration: 0.01,
        beforeSetup: function(i) {
            i.element.hide();
            i.element.makeClipping();
            i.element.makePositioned();
        },
        afterFinishInternal: function(i) {
            new Effect.Parallel([new Effect.Opacity(i.element, {
                sync: true,
                to: 1,
                from: 0,
                transition: b.opacityTransition
            }), new Effect.Move(i.element, {
                x: e,
                y: d,
                sync: true,
                transition: b.moveTransition
            }), new Effect.Scale(i.element, 100, {
                scaleMode: {
                    originalHeight: g.height,
                    originalWidth: g.width
                },
                sync: true,
                scaleFrom: window.opera ? 1 : 0,
                transition: b.scaleTransition,
                restoreAfterFinish: true
            })], Object.extend({
                beforeSetup: function(j) {
                    j.effects[0].element.setStyle({
                        height: "0px"
                    });
                    j.effects[0].element.show();
                },
                afterFinishInternal: function(j) {
                    j.effects[0].element.undoClipping();
                    j.effects[0].element.undoPositioned();
                    j.effects[0].element.setStyle(a);
                }
            }, b));
        }
    });
};
Effect.Shrink = function(c) {
    c = $(c);
    var b = Object.extend({
        direction: "center",
        moveTransition: Effect.Transitions.sinoidal,
        scaleTransition: Effect.Transitions.sinoidal,
        opacityTransition: Effect.Transitions.none
    }, arguments[1] || {});
    var a = {
        top: c.style.top,
        left: c.style.left,
        height: c.style.height,
        width: c.style.width,
        opacity: c.getInlineOpacity()
    };
    var f = c.getDimensions();
    var e, d;
    switch (b.direction) {
        case "top-left":
            e = d = 0;
            break;
        case "top-right":
            e = f.width;
            d = 0;
            break;
        case "bottom-left":
            e = 0;
            d = f.height;
            break;
        case "bottom-right":
            e = f.width;
            d = f.height;
            break;
        case "center":
            e = f.width / 2;
            d = f.height / 2;
            break;
    }
    return new Effect.Parallel([new Effect.Opacity(c, {
        sync: true,
        to: 0,
        from: 1,
        transition: b.opacityTransition
    }), new Effect.Scale(c, window.opera ? 1 : 0, {
        sync: true,
        transition: b.scaleTransition,
        restoreAfterFinish: true
    }), new Effect.Move(c, {
        x: e,
        y: d,
        sync: true,
        transition: b.moveTransition
    })], Object.extend({
        beforeStartInternal: function(g) {
            g.effects[0].element.makePositioned();
            g.effects[0].element.makeClipping();
        },
        afterFinishInternal: function(g) {
            g.effects[0].element.hide();
            g.effects[0].element.undoClipping();
            g.effects[0].element.undoPositioned();
            g.effects[0].element.setStyle(a);
        }
    }, b));
};
Effect.Pulsate = function(c) {
    c = $(c);
    var b = arguments[1] || {};
    var a = c.getInlineOpacity();
    var e = b.transition || Effect.Transitions.sinoidal;
    var d = function(f) {
        return e(1 - Effect.Transitions.pulse(f));
    };
    d.bind(e);
    return new Effect.Opacity(c, Object.extend(Object.extend({
        duration: 3,
        from: 0,
        afterFinishInternal: function(f) {
            f.element.setStyle({
                opacity: a
            });
        }
    }, b), {
        transition: d
    }));
};
Effect.Fold = function(b) {
    b = $(b);
    var a = {
        top: b.style.top,
        left: b.style.left,
        width: b.style.width,
        height: b.style.height
    };
    Element.makeClipping(b);
    return new Effect.Scale(b, 5, Object.extend({
        scaleContent: false,
        scaleX: false,
        afterFinishInternal: function(c) {
            new Effect.Scale(b, 1, {
                scaleContent: false,
                scaleY: false,
                afterFinishInternal: function(d) {
                    d.element.hide();
                    d.element.undoClipping();
                    d.element.setStyle(a);
                }
            });
        }
    }, arguments[1] || {}));
};
["setOpacity", "getOpacity", "getInlineOpacity", "forceRerendering", "setContentZoom", "collectTextNodes", "collectTextNodesIgnoreClass", "childrenWithClassName"].each(function(a) {
    Element.Methods[a] = Element[a];
});
Element.Methods.visualEffect = function(b, c, a) {
    s = c.gsub(/_/, "-").camelize();
    effect_class = s.charAt(0).toUpperCase() + s.substring(1);
    new Effect[effect_class](b, a);
    return $(b);
};
Element.addMethods();
var JSONUtil = {
    m: {
        "\b": "\\b",
        "\t": "\\t",
        "\n": "\\n",
        "\f": "\\f",
        "\r": "\\r",
        '"': '\\"',
        "\\": "\\\\"
    },
    s: {
        array: function(d) {
            var g = ["["],
                c, k, j, e = d.length,
                h;
            for (j = 0; j < e; j += 1) {
                h = d[j];
                k = JSONUtil.s[typeof h];
                if (k) {
                    h = k(h);
                    if (typeof h == "string") {
                        if (c) {
                            g[g.length] = ",";
                        }
                        g[g.length] = h;
                        c = true;
                    }
                }
            }
            g[g.length] = "]";
            return g.join("");
        },
        "boolean": function(a) {
            return String(a);
        },
        "null": function(a) {
            return "null";
        },
        number: function(a) {
            return isFinite(a) ? String(a) : "null";
        },
        object: function(d) {
            if (d) {
                if (d instanceof Array) {
                    return JSONUtil.s.array(d);
                }
                var e = ["{"],
                    c, j, h, g;
                for (h in d) {
                    g = d[h];
                    j = JSONUtil.s[typeof g];
                    if (j) {
                        g = j(g);
                        if (typeof g == "string") {
                            if (c) {
                                e[e.length] = ",";
                            }
                            e.push(JSONUtil.s.string(h), ":", g);
                            c = true;
                        }
                    }
                }
                e[e.length] = "}";
                return e.join("");
            }
            return "null";
        },
        string: function(a) {
            if (/["\\\x00-\x1f]/.test(a)) {
                a = a.replace(/([\x00-\x1f\\"])/g, function(e, d) {
                    var f = JSONUtil.m[d];
                    if (f) {
                        return f;
                    }
                    f = d.charCodeAt();
                    return "\\u00" + Math.floor(f / 16).toString(16) + (f % 16).toString(16);
                });
            }
            return '"' + a + '"';
        }
    },
    objectToJSONString: function(a) {
        return JSONUtil.s.object(a);
    }
};
var detect = navigator.userAgent.toLowerCase();
var OS, browser, version, total, thestring;

function getBrowserInfo() {
    if (checkIt("konqueror")) {
        browser = "Konqueror";
        OS = "Linux";
    } else {
        if (checkIt("safari")) {
            browser = "Safari";
        } else {
            if (checkIt("omniweb")) {
                browser = "OmniWeb";
            } else {
                if (checkIt("opera")) {
                    browser = "Opera";
                } else {
                    if (checkIt("webtv")) {
                        browser = "WebTV";
                    } else {
                        if (checkIt("icab")) {
                            browser = "iCab";
                        } else {
                            if (checkIt("msie")) {
                                browser = "Internet Explorer";
                            } else {
                                if (!checkIt("compatible")) {
                                    browser = "Netscape Navigator";
                                    version = detect.charAt(8);
                                } else {
                                    browser = "An unknown browser";
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    if (!version) {
        version = detect.charAt(place + thestring.length);
    }
    if (!OS) {
        if (checkIt("linux")) {
            OS = "Linux";
        } else {
            if (checkIt("x11")) {
                OS = "Unix";
            } else {
                if (checkIt("mac")) {
                    OS = "Mac";
                } else {
                    if (checkIt("win")) {
                        OS = "Windows";
                    } else {
                        OS = "an unknown operating system";
                    }
                }
            }
        }
    }
}

function checkIt(a) {
    place = detect.indexOf(a) + 1;
    thestring = a;
    return place;
}
Event.observe(window, "load", getBrowserInfo, false);
Event.observe(window, "unload", Event.unloadCache, false);
var lightbox = Class.create();
lightbox.prototype = {
    yPos: 0,
    xPos: 0,
    el: null,
    initialize: function() {},
    setElement: function(a) {
        this.el = a;
    },
    getElement: function() {
        return this.el;
    },
    activate: function() {
        if (browser == "Internet Explorer") {
            this.getScroll();
            this.hideSelects("hidden");
        }
        this.displayLightbox();
    },
    prepareIE: function(a, b) {
        bod = document.getElementsByTagName("body")[0];
        bod.style.height = a;
        bod.style.overflow = b;
        htm = document.getElementsByTagName("html")[0];
        htm.style.height = a;
        htm.style.overflow = b;
    },
    hideSelects: function(a) {
        selects = document.getElementsByTagName("select");
        for (i = 0; i < selects.length; i++) {
            selects[i].style.visibility = a;
        }
    },
    getScroll: function() {
        if (self.pageYOffset) {
            this.yPos = self.pageYOffset;
        } else {
            if (document.documentElement && document.documentElement.scrollTop) {
                this.yPos = document.documentElement.scrollTop;
            } else {
                if (document.body) {
                    this.yPos = document.body.scrollTop;
                }
            }
        }
    },
    setScroll: function(a, b) {
        window.scrollTo(a, b);
    },
    displayLightbox: function() {
        $("overlay").style.display = "block";
        $("lightbox").style.display = "block";
        _frevvo.utilities.insertFirst(this.el, $("lbContent"));
        this.setModal();
    },
    hideLightbox: function() {
        $("lightbox").style.display = "none";
        $("overlay").style.display = "none";
        this.removeModal();
    },
    setModal: function() {
        document.addEventListener("focus", this.focusObserver, true);
    },
    removeModal: function() {
        document.removeEventListener("focus", this.focusObserver, true);
    },
    focusObserver: function(a) {
        var b = $("lightbox");
        if (!b.contains(a.target)) {
            a.stopPropagation();
            b.focus();
        }
    },
    deactivate: function() {
        if (this.el && this.el.parentNode && this.el.parentNode == $("lbContent")) {
            this.el.parentNode.removeChild(this.el);
        }
        if (browser == "Internet Explorer") {
            this.setScroll(0, this.yPos);
            this.prepareIE("auto", "auto");
            this.hideSelects("visible");
        }
        this.hideLightbox();
    }
};
var jstz = function() {
    var a = function(b) {
            b = -b.getTimezoneOffset();
            return b !== null ? b : 0;
        },
        g = function() {
            return a(new Date(2010, 0, 1, 0, 0, 0, 0));
        },
        f = function() {
            return a(new Date(2010, 5, 1, 0, 0, 0, 0));
        },
        h = function() {
            var d = g(),
                c = f(),
                e = g() - f();
            if (e < 0) {
                return d + ",1";
            } else {
                if (e > 0) {
                    return c + ",1,s";
                }
            }
            return d + ",0";
        };
    return {
        determine_timezone: function() {
            var b = h();
            return new jstz.TimeZone(jstz.olson.timezones[b]);
        },
        date_is_dst: function(b) {
            var d = b.getMonth() > 5 ? f() : g(),
                b = a(b);
            return d - b !== 0;
        }
    };
}();
jstz.TimeZone = function() {
    var a = null,
        g = null,
        f = null,
        h = function(b) {
            f = b[0];
            a = b[1];
            g = b[2];
            if (typeof jstz.olson.ambiguity_list[a] !== "undefined") {
                for (var b = jstz.olson.ambiguity_list[a], i = b.length, e = 0, d = b[0]; e < i; e += 1) {
                    if (d = b[e], jstz.date_is_dst(jstz.olson.dst_start_dates[d])) {
                        a = d;
                        break;
                    }
                }
            }
        };
    h.prototype = {
        constructor: jstz.TimeZone,
        name: function() {
            return a;
        },
        dst: function() {
            return g;
        },
        offset: function() {
            return f;
        }
    };
    return h;
}();
jstz.olson = {};
jstz.olson.timezones = function() {
    return {
        "-720,0": ["-12:00", "Etc/GMT+12", !1],
        "-660,0": ["-11:00", "Pacific/Pago_Pago", !1],
        "-600,1": ["-11:00", "America/Adak", !0],
        "-660,1,s": ["-11:00", "Pacific/Apia", !0],
        "-600,0": ["-10:00", "Pacific/Honolulu", !1],
        "-570,0": ["-09:30", "Pacific/Marquesas", !1],
        "-540,0": ["-09:00", "Pacific/Gambier", !1],
        "-540,1": ["-09:00", "America/Anchorage", !0],
        "-480,1": ["-08:00", "America/Los_Angeles", !0],
        "-480,0": ["-08:00", "Pacific/Pitcairn", !1],
        "-420,0": ["-07:00", "America/Phoenix", !1],
        "-420,1": ["-07:00", "America/Denver", !0],
        "-360,0": ["-06:00", "America/Guatemala", !1],
        "-360,1": ["-06:00", "America/Chicago", !0],
        "-360,1,s": ["-06:00", "Pacific/Easter", !0],
        "-300,0": ["-05:00", "America/Bogota", !1],
        "-300,1": ["-05:00", "America/New_York", !0],
        "-270,0": ["-04:30", "America/Caracas", !1],
        "-240,1": ["-04:00", "America/Halifax", !0],
        "-240,0": ["-04:00", "America/Santo_Domingo", !1],
        "-240,1,s": ["-04:00", "America/Asuncion", !0],
        "-210,1": ["-03:30", "America/St_Johns", !0],
        "-180,1": ["-03:00", "America/Godthab", !0],
        "-180,0": ["-03:00", "America/Argentina/Buenos_Aires", !1],
        "-180,1,s": ["-03:00", "America/Montevideo", !0],
        "-120,0": ["-02:00", "America/Noronha", !1],
        "-120,1": ["-02:00", "Etc/GMT+2", !0],
        "-60,1": ["-01:00", "Atlantic/Azores", !0],
        "-60,0": ["-01:00", "Atlantic/Cape_Verde", !1],
        "0,0": ["00:00", "Etc/UTC", !1],
        "0,1": ["00:00", "Europe/London", !0],
        "60,1": ["+01:00", "Europe/Berlin", !0],
        "60,0": ["+01:00", "Africa/Lagos", !1],
        "60,1,s": ["+01:00", "Africa/Windhoek", !0],
        "120,1": ["+02:00", "Asia/Beirut", !0],
        "120,0": ["+02:00", "Africa/Johannesburg", !1],
        "180,1": ["+03:00", "Europe/Moscow", !0],
        "180,0": ["+03:00", "Asia/Baghdad", !1],
        "210,1": ["+03:30", "Asia/Tehran", !0],
        "240,0": ["+04:00", "Asia/Dubai", !1],
        "240,1": ["+04:00", "Asia/Yerevan", !0],
        "270,0": ["+04:30", "Asia/Kabul", !1],
        "300,1": ["+05:00", "Asia/Yekaterinburg", !0],
        "300,0": ["+05:00", "Asia/Karachi", !1],
        "330,0": ["+05:30", "Asia/Kolkata", !1],
        "345,0": ["+05:45", "Asia/Kathmandu", !1],
        "360,0": ["+06:00", "Asia/Dhaka", !1],
        "360,1": ["+06:00", "Asia/Omsk", !0],
        "390,0": ["+06:30", "Asia/Rangoon", !1],
        "420,1": ["+07:00", "Asia/Krasnoyarsk", !0],
        "420,0": ["+07:00", "Asia/Jakarta", !1],
        "480,0": ["+08:00", "Asia/Shanghai", !1],
        "480,1": ["+08:00", "Asia/Irkutsk", !0],
        "525,0": ["+08:45", "Australia/Eucla", !0],
        "525,1,s": ["+08:45", "Australia/Eucla", !0],
        "540,1": ["+09:00", "Asia/Yakutsk", !0],
        "540,0": ["+09:00", "Asia/Tokyo", !1],
        "570,0": ["+09:30", "Australia/Darwin", !1],
        "570,1,s": ["+09:30", "Australia/Adelaide", !0],
        "600,0": ["+10:00", "Australia/Brisbane", !1],
        "600,1": ["+10:00", "Asia/Vladivostok", !0],
        "600,1,s": ["+10:00", "Australia/Sydney", !0],
        "630,1,s": ["+10:30", "Australia/Lord_Howe", !0],
        "660,1": ["+11:00", "Asia/Kamchatka", !0],
        "660,0": ["+11:00", "Pacific/Noumea", !1],
        "690,0": ["+11:30", "Pacific/Norfolk", !1],
        "720,1,s": ["+12:00", "Pacific/Auckland", !0],
        "720,0": ["+12:00", "Pacific/Tarawa", !1],
        "765,1,s": ["+12:45", "Pacific/Chatham", !0],
        "780,0": ["+13:00", "Pacific/Tongatapu", !1],
        "840,0": ["+14:00", "Pacific/Kiritimati", !1]
    };
}();
jstz.olson.dst_start_dates = function() {
    return {
        "America/Denver": new Date(2011, 2, 13, 3, 0, 0, 0),
        "America/Mazatlan": new Date(2011, 3, 3, 3, 0, 0, 0),
        "America/Chicago": new Date(2011, 2, 13, 3, 0, 0, 0),
        "America/Mexico_City": new Date(2011, 3, 3, 3, 0, 0, 0),
        "Atlantic/Stanley": new Date(2011, 8, 4, 7, 0, 0, 0),
        "America/Asuncion": new Date(2011, 9, 2, 3, 0, 0, 0),
        "America/Santiago": new Date(2011, 9, 9, 3, 0, 0, 0),
        "America/Campo_Grande": new Date(2011, 9, 16, 5, 0, 0, 0),
        "America/Montevideo": new Date(2011, 9, 2, 3, 0, 0, 0),
        "America/Sao_Paulo": new Date(2011, 9, 16, 5, 0, 0, 0),
        "America/Los_Angeles": new Date(2011, 2, 13, 8, 0, 0, 0),
        "America/Santa_Isabel": new Date(2011, 3, 5, 8, 0, 0, 0),
        "America/Havana": new Date(2011, 2, 13, 2, 0, 0, 0),
        "America/New_York": new Date(2011, 2, 13, 7, 0, 0, 0),
        "Asia/Gaza": new Date(2011, 2, 26, 23, 0, 0, 0),
        "Asia/Beirut": new Date(2011, 2, 27, 1, 0, 0, 0),
        "Europe/Minsk": new Date(2011, 2, 27, 2, 0, 0, 0),
        "Europe/Helsinki": new Date(2011, 2, 27, 4, 0, 0, 0),
        "Europe/Istanbul": new Date(2011, 2, 28, 5, 0, 0, 0),
        "Asia/Damascus": new Date(2011, 3, 1, 2, 0, 0, 0),
        "Asia/Jerusalem": new Date(2011, 3, 1, 6, 0, 0, 0),
        "Africa/Cairo": new Date(2010, 3, 30, 4, 0, 0, 0),
        "Asia/Yerevan": new Date(2011, 2, 27, 4, 0, 0, 0),
        "Asia/Baku": new Date(2011, 2, 27, 8, 0, 0, 0),
        "Pacific/Auckland": new Date(2011, 8, 26, 7, 0, 0, 0),
        "Pacific/Fiji": new Date(2010, 11, 29, 23, 0, 0, 0),
        "America/Halifax": new Date(2011, 2, 13, 6, 0, 0, 0),
        "America/Goose_Bay": new Date(2011, 2, 13, 2, 1, 0, 0),
        "America/Miquelon": new Date(2011, 2, 13, 5, 0, 0, 0),
        "America/Godthab": new Date(2011, 2, 27, 1, 0, 0, 0)
    };
}();
jstz.olson.ambiguity_list = {
    "America/Denver": ["America/Denver", "America/Mazatlan"],
    "America/Chicago": ["America/Chicago", "America/Mexico_City"],
    "America/Asuncion": ["Atlantic/Stanley", "America/Asuncion", "America/Santiago", "America/Campo_Grande"],
    "America/Montevideo": ["America/Montevideo", "America/Sao_Paulo"],
    "Asia/Beirut": "Asia/Gaza,Asia/Beirut,Europe/Minsk,Europe/Helsinki,Europe/Istanbul,Asia/Damascus,Asia/Jerusalem,Africa/Cairo".split(","),
    "Asia/Yerevan": ["Asia/Yerevan", "Asia/Baku"],
    "Pacific/Auckland": ["Pacific/Auckland", "Pacific/Fiji"],
    "America/Los_Angeles": ["America/Los_Angeles", "America/Santa_Isabel"],
    "America/New_York": ["America/Havana", "America/New_York"],
    "America/Halifax": ["America/Goose_Bay", "America/Halifax"],
    "America/Godthab": ["America/Miquelon", "America/Godthab"]
};
_frevvo.datePicker = {};
Object.extend(_frevvo.datePicker, {
    datePickerDivID: "datepicker",
    datePicker: null,
    dayArrayShort: null,
    dayArrayMed: new Array("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"),
    dayArrayLong: new Array("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"),
    monthArrayShort: new Array("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"),
    monthArrayMed: new Array("Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sept", "Oct", "Nov", "Dec"),
    monthArrayLong: null,
    datePickerDayClass: "_frevvo-datepicker-day",
    dateSeparator: "-",
    dateFormat: "ymd",
    closeHook: null,
    targetElement: null,
    displayElement: null,
    dpfocus: false,
    getDatePicker: function(a) {
        this.init();
        this.targetElement = _frevvo.dateView.$element(a);
        var c = a.getAttribute("dateValue");
        if (this.datePicker == null) {
            this.create(this.targetElement);
        }
        if (this.targetElement != null) {
            var b = this.getFieldDate(c);
            this.refresh(b.getFullYear(), b.getMonth(), b.getDate());
        }
        return this.datePicker;
    },
    create: function(c) {
        this.targetElement = c;
        if (this.datePicker == null) {
            var a = document.createElement("div");
            a.setAttribute("id", this.datePickerDivID);
            Element.addClassName(a, "dpDiv");
            a.setAttribute("style", "visibility: hidden;");
            a.setAttribute("role", "alertdialog");
            document.body.appendChild(a);
            this.datePicker = a;
            Event.observe(this.datePicker, "mouseover", function(d) {
                dpfocus = true;
            });
            Event.observe(this.datePicker, "mouseout", function(d) {
                dpfocus = false;
            });
        }
        if (this.targetElement != null && this.targetElement.value) {
            var b = this.getFieldDate(this.targetElement.value);
            this.refresh(b.getFullYear(), b.getMonth(), b.getDate());
        }
    },
    refresh: function(t, G, C) {
        var z = "";
        this.reInit();
        var g = new Date();
        if ((G >= 0) && (t > 0)) {
            g = new Date(t, G, 1);
        } else {
            C = g.getDate();
            g.setDate(1);
        }
        var n = "\r\n";
        var o = "<table cols=7 class='dpTable'>" + n;
        var d = "</table>" + n;
        var E = "<tr class='dpTR'>";
        var h = "<tr class='dpTitleTR'>";
        var q = "<tr class='dpDayTR'>";
        var s = "<tr class='dpTodayButtonTR'>";
        var j = "</tr>" + n;
        var b = "<td class='dpTD' onMouseOut='this.className=\"dpTD\";' onMouseOver=' this.className=\"dpTDHover\";' ";
        var c = "<td colspan=5 class='dpTitleTD'>";
        var D = "<td class='dpButtonTD'>";
        var I = "<td colspan=7 class='dpTodayButtonTD'>";
        var y = "<td class='dpDayTD'>";
        var H = "<td class='dpDayHighlightTD' onMouseOut='this.className=\"dpDayHighlightTD\";' onMouseOver='this.className=\"dpTDHover\";' ";
        var v = "</td>" + n;
        var f = "<div class='dpTitleText'>";
        var A = "<div class='dpDayHighlight'>";
        var u = "</div>";
        var r = o;
        r += h;
        r += D + this.getButtonCode(g, -1, "&lt;") + v;
        r += c + f + this.monthArrayLong[g.getMonth()] + " " + g.getFullYear() + u + v;
        r += D + this.getButtonCode(g, 1, "&gt;") + v;
        r += j;
        r += q;
        for (B = 0; B < this.dayArrayShort.length; B++) {
            r += y + this.dayArrayShort[B] + v;
        }
        r += j;
        r += E;
        for (var B = 0; B < g.getDay(); B++) {
            r += b + "&nbsp;" + v;
        }
        do {
            dayNum = g.getDate();
            TD_onclick = "name=" + this.getDateString(g) + ">";
            var e = "<input id='_frevvo-" + this.getDateString(g) + "' class='" + this.datePickerDayClass + "' type='button' aria-label='" + this.getAnnouncedDateString(g) + "' value='" + dayNum + "'></input>";
            if (dayNum == C) {
                r += H + TD_onclick + A + e + u + v;
                z = this.getDateString(g);
            } else {
                r += b + TD_onclick + e + v;
            }
            if (g.getDay() == 6) {
                r += j + E;
            }
            g.setDate(g.getDate() + 1);
        } while (g.getDate() > 1);
        if (g.getDay() > 0) {
            for (B = 6; B > g.getDay(); B--) {
                r += b + "&nbsp;" + v;
            }
        }
        r += j;
        var F = new Date();
        var l = "Today is " + this.dayArrayMed[F.getDay()] + ", " + this.monthArrayMed[F.getMonth()] + " " + F.getDate();
        r += s + I;
        r += "<div class='dpTodayButton' onClick='_frevvo.datePicker.refresh();'>" + this.todayStr + "</div> ";
        r += "<div id='dpCloseButton' class='dpTodayButton' onClick='_frevvo.datePicker.closeDatePicker();'>" + this.closeStr + "</div>";
        r += v + j;
        r += d;
        this.datePicker.innerHTML = r;
        var k = document.getElementsByClassName("dpTD", this.datePicker);
        k.each(function(i) {
            Event.observe(i, "click", function(x) {
                _frevvo.datePicker.updateDateField(i.getAttribute("name"));
            });
        });
        var p = document.getElementsByClassName("dpDayHighlightTD", this.datePicker);
        if (p && p.length > 0) {
            Event.observe(p[0], "click", function(i) {
                _frevvo.datePicker.updateDateField(p[0].getAttribute("name"));
            });
        }
        var a = document.getElementsByClassName(this.datePickerDayClass);
        var m = this;
        a.each(function(i) {
            Event.observe(i, "keydown", function(x) {
                m.handleKeyDown(x);
            });
        });
        var w = document.getElementById("_frevvo-" + z);
        if (w) {
            w.focus();
        }
    },
    display: function(d, c, f) {
        if (this.datePicker == null) {
            this.create(d);
        }
        this.targetElement = d;
        if (!c) {
            this.displayElement = this.targetElement;
        } else {
            this.displayElement = c;
        }
        var a = this.displayElement.offsetLeft;
        var e = this.displayElement.offsetTop + this.displayElement.offsetHeight;
        var b = this.displayElement;
        while (b.offsetParent) {
            b = b.offsetParent;
            a += b.offsetLeft;
            e += b.offsetTop;
        }
        a += 24;
        e -= 15;
        this.datePicker.style.position = "absolute";
        this.datePicker.style.left = a + "px";
        this.datePicker.style.top = e + "px";
        this.datePicker.style.visibility = "visible";
        this.datePicker.style.display = "block";
        if (!f) {
            this.datePicker.style.zIndex = 10000;
        } else {
            this.datePicker.style.zIndex = f;
        }
    },
    getButtonCode: function(a, d, c) {
        var e = (a.getMonth() + d) % 12;
        var b = a.getFullYear() + parseInt((a.getMonth() + d) / 12);
        if (e < 0) {
            e += 12;
            b += -1;
        }
        return "<div class='dpButton' onClick='_frevvo.datePicker.refresh(\"" + b + '" , ' + e + ");'>" + c + "</div>";
    },
    getDateString: function(a) {
        var c = "00" + a.getDate();
        var b = "00" + (a.getMonth() + 1);
        c = c.substring(c.length - 2);
        b = b.substring(b.length - 2);
        switch (this.dateFormat) {
            case "dmy":
                return c + this.dateSeparator + b + this.dateSeparator + a.getFullYear();
            case "ymd":
                return a.getFullYear() + this.dateSeparator + b + this.dateSeparator + c;
            case "mdy":
            default:
                return b + this.dateSeparator + c + this.dateSeparator + a.getFullYear();
        }
    },
    getAnnouncedDateString: function(a) {
        var c = "00" + a.getDate();
        c = c.substring(c.length - 2);
        var b = this.monthArrayLong[a.getMonth()];
        switch (this.dateFormat) {
            case "dmy":
                return c + " " + b + " " + a.getFullYear();
            case "ymd":
                return a.getFullYear() + " " + b + " " + c;
            case "mdy":
            default:
                return b + " " + c + " " + a.getFullYear();
        }
    },
    getFieldDate: function(f) {
        var b;
        var c;
        var h, a, i;
        try {
            c = this.splitDateString(f);
            if (c) {
                switch (this.dateFormat) {
                    case "dmy":
                        h = parseInt(c[0], 10);
                        a = parseInt(c[1], 10) - 1;
                        i = parseInt(c[2], 10);
                        break;
                    case "ymd":
                        h = parseInt(c[2], 10);
                        a = parseInt(c[1], 10) - 1;
                        i = parseInt(c[0], 10);
                        break;
                    case "mdy":
                    default:
                        h = parseInt(c[1], 10);
                        a = parseInt(c[0], 10) - 1;
                        i = parseInt(c[2], 10);
                        break;
                }
                b = new Date(i, a, h);
            } else {
                if (f) {
                    b = new Date(f);
                } else {
                    b = new Date();
                }
            }
        } catch (g) {
            b = new Date();
        }
        return b;
    },
    splitDateString: function(b) {
        var a;
        if (b.indexOf("/") >= 0) {
            a = b.split("/");
        } else {
            if (b.indexOf(".") >= 0) {
                a = b.split(".");
            } else {
                if (b.indexOf("-") >= 0) {
                    a = b.split("-");
                } else {
                    if (b.indexOf("\\") >= 0) {
                        a = b.split("\\");
                    } else {
                        a = false;
                    }
                }
            }
        }
        return a;
    },
    updateDateField: function(a) {
        if (a) {
            this.targetElement.value = a;
        }
        this.closeDatePicker();
    },
    closeDatePicker: function() {
        try {
            this.datePicker.style.visibility = "hidden";
            this.datePicker.style.display = "none";
            if (this.closeHook && (typeof(this.closeHook) == "function")) {
                this.closeHook(this.targetElement);
            }
            this.closeHook = null;
            this.targetElement = null;
            this.displayElement = null;
            dpfocus = false;
        } catch (a) {}
    },
    isVisibleForElement: function(a) {
        return (this.datePicker && Element.visible(this.datePicker) && this.targetElement == a);
    },
    isVisible: function(a) {
        return (this.datePicker && Element.visible(this.datePicker));
    },
    init: function() {
        this.dayArrayShort = new Array();
        this.dayArrayShort.push(_frevvo.localeStrings.calWkd1);
        this.dayArrayShort.push(_frevvo.localeStrings.calWkd2);
        this.dayArrayShort.push(_frevvo.localeStrings.calWkd3);
        this.dayArrayShort.push(_frevvo.localeStrings.calWkd4);
        this.dayArrayShort.push(_frevvo.localeStrings.calWkd5);
        this.dayArrayShort.push(_frevvo.localeStrings.calWkd6);
        this.dayArrayShort.push(_frevvo.localeStrings.calWkd7);
        this.monthArrayLong = new Array();
        this.monthArrayLong.push(_frevvo.localeStrings.calMon1);
        this.monthArrayLong.push(_frevvo.localeStrings.calMon2);
        this.monthArrayLong.push(_frevvo.localeStrings.calMon3);
        this.monthArrayLong.push(_frevvo.localeStrings.calMon4);
        this.monthArrayLong.push(_frevvo.localeStrings.calMon5);
        this.monthArrayLong.push(_frevvo.localeStrings.calMon6);
        this.monthArrayLong.push(_frevvo.localeStrings.calMon7);
        this.monthArrayLong.push(_frevvo.localeStrings.calMon8);
        this.monthArrayLong.push(_frevvo.localeStrings.calMon9);
        this.monthArrayLong.push(_frevvo.localeStrings.calMon10);
        this.monthArrayLong.push(_frevvo.localeStrings.calMon11);
        this.monthArrayLong.push(_frevvo.localeStrings.calMon12);
        var a = null;
        this.todayStr = _frevvo.localeStrings.calToday;
        var b = null;
        this.closeStr = _frevvo.localeStrings.calClose;
    },
    reInit: function() {
        if (this.todayStr == null || this.todayStr == undefined) {
            this.todayStr = "today";
        }
        if (this.closeStr == null || this.closeStr == undefined) {
            this.closeStr = "close";
        }
        if (this.monthArrayLong == null) {
            this.monthArrayLong = new Array("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December");
        }
        if (this.dayArrayShort == null) {
            this.dayArrayShort = new Array("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa");
        }
    },
    handleKeyDown: function(b) {
        var a = document.getElementsByClassName("dpDayHighlightTD", this.datePicker);
        var f;
        var e = this;
        var c = function(g, d) {
            if (g) {
                g.setDate(g.getDate() + d);
                e.refresh(g.getFullYear(), g.getMonth(), g.getDate());
            }
        };
        if (b.keyCode === 13) {
            if (a && a.length > 0) {
                f = this.getFieldDate(a[0].getAttribute("name"));
                _frevvo.datePicker.updateDateField(a[0].getAttribute("name"));
            }
        } else {
            if (b.keyCode === 39 || b.keyCode === 40) {
                if (a && a.length > 0) {
                    f = this.getFieldDate(a[0].getAttribute("name"));
                    c(f, 1);
                }
            } else {
                if (b.keyCode === 37 || b.keyCode === 38) {
                    if (a && a.length > 0) {
                        f = this.getFieldDate(a[0].getAttribute("name"));
                        c(f, -1);
                    }
                } else {
                    if (b.keyCode === 27) {
                        this.closeDatePicker();
                    }
                }
            }
        }
    },
    setFocusOnCurrentValue: function(b) {
        var a = b.childrenWithClassName("dpDayHighlightTD");
        if (a && a.length > 0) {
            a[0].firstChild.firstChild.focus();
        }
    }
});
_frevvo.protoView = {
    $childById: function(a, c) {
        if (!a) {
            return null;
        }
        c = c.toLowerCase();
        a = a.firstChild;
        while (a) {
            try {
                if (a.getAttribute("id").toLowerCase() == c) {
                    break;
                }
            } catch (b) {}
            a = a.nextSibling;
        }
        return a;
    },
    $childByName: function(b, a) {
        if (!b) {
            return null;
        }
        a = a.toLowerCase();
        b = b.firstChild;
        while (b) {
            if (b.nodeName.toLowerCase() == a) {
                break;
            }
            b = b.nextSibling;
        }
        return b;
    },
    $childrenByName: function(c, a) {
        if (!c) {
            return null;
        }
        a = a.toLowerCase();
        var b = [];
        c = c.firstChild;
        while (c) {
            if (c.nodeName.toLowerCase() == a) {
                b.push(c);
            }
            c = c.nextSibling;
        }
        return b;
    },
    $childByClass: function(b, a) {
        if (!b) {
            return null;
        }
        b = b.firstChild;
        while (b) {
            if (b.nodeType == 1) {
                if (b.className && b.className.indexOf(a) >= 0) {
                    break;
                }
            }
            b = b.nextSibling;
        }
        return b;
    },
    $childrenByClass: function(c, b) {
        if (!c) {
            return null;
        }
        var a = [];
        c = c.firstChild;
        while (c) {
            if (c.className && c.className.indexOf(b) >= 0) {
                a.push(c);
            }
            c = c.nextSibling;
        }
        return a;
    },
    $parentByClass: function(b, a) {
        while (b) {
            if (b.className && b.className.indexOf(a) >= 0) {
                return b;
            }
            if (b.parentNode == document) {
                return null;
            }
            b = b.parentNode || null;
        }
        return b;
    },
    $parentById: function(a, b) {
        while (a) {
            if (a.getAttribute("id") && a.getAttribute("id").toLowerCase() == b) {
                return a;
            }
            if (a.parentNode == document) {
                return null;
            }
            a = a.parentNode || null;
        }
        return a;
    },
    makeNode: function(c) {
        var b = $("a-invisible-span");
        b.innerHTML = c;
        var a = b.firstChild;
        b.removeChild(a);
        return a;
    },
    makeNodes: function(b) {
        var a = $("a-invisible-span");
        a.innerHTML = b;
        return $A(a.childNodes).findAll(function(c) {
            return (c.nodeType == 1);
        });
    },
    hide: function(a) {
        if (a) {
            Element.hide(a);
        }
    },
    show: function(a) {
        if (a) {
            Element.show(a);
        }
    }
};
_frevvo.lightBoxView = {};
Object.extend(_frevvo.lightBoxView, _frevvo.protoView);
Object.extend(_frevvo.lightBoxView, {
    lightbox: null,
    clsNames: null,
    returnFocusEl: null,
    refreshOnDismiss: false,
    $lightbox: function() {
        return $("lightbox");
    },
    $overlay: function() {
        return $("overlay");
    },
    $content: function() {
        return $("lbContent");
    },
    $title: function() {
        return $("lbTitle");
    },
    $headerLink: function() {
        return $("lbHeader-link");
    },
    $yesLink: function() {
        return $("lbContent-link-yes");
    },
    $noLink: function() {
        return $("lbContent-link-no");
    },
    create: function() {
        this.lightbox = new lightbox();
        FEvent.observe(this.$headerLink(), "click", this.closeObserver.bindAsObserver(this));
        FEvent.observe(this.$headerLink(), "keydown", this.closeObserver.bindAsObserver(this));
        FEvent.observe(this.$lightbox(), "keydown", this.closeHotKeyObserver.bindAsObserver(this));
        FEvent.observe(this.$noLink(), "click", this.dismiss.bindAsObserver(this));
        FEvent.observe(this.$yesLink(), "click", this.handleAndDismiss.bindAsObserver(this));
    },
    getView: function(a) {
        return _frevvo.lightBoxView;
    },
    showPage: function(b, g, c, e, a, d) {
        if (this.isVisible()) {
            return false;
        }
        this.returnFocusEl = d;
        if (e && e.showPleaseWait) {
            if (_frevvo.localeStrings) {
                this.showWaitDialog(_frevvo.protoView.makeNode(_frevvo.localeStrings.pleaseWait2), _frevvo.localeStrings.pleasewait);
            } else {
                this.showWaitDialog(_frevvo.protoView.makeNode("Please wait ..."), "Please wait");
            }
            e.clsNames = c;
        } else {
            if (c instanceof Array) {
                this.setClassNames(this.$lightbox(), c, "s-info-lightbox");
            } else {
                this.setClassName(this.$lightbox(), c, "s-info-lightbox");
            }
        }
        var f;
        if (e) {
            f = this.handleReturn.bindAsObserver(this, g, e);
        } else {
            f = this.handleReturn.bindAsObserver(this, g);
        }
        var h = "get";
        if (a) {
            h = a;
        }
        new Ajax.Request(b, {
            method: h,
            onComplete: f
        });
        return false;
    },
    handleReturn: function(t, title, options) {
        if (options && options.showPleaseWait) {
            this.dismiss();
            var clsNames = null;
            if (options.clsNames) {
                clsNames = options.clsNames;
            }
            if (clsNames instanceof Array) {
                this.setClassNames(this.$lightbox(), clsNames, "s-info-lightbox");
            } else {
                this.setClassName(this.$lightbox(), clsNames, "s-info-lightbox");
            }
        }
        var error = t.getResponseHeader("frevvo.app.error.message");
        var node = $("a-invisible-span");
        if (error) {
            node.innerHTML = error;
        } else {
            node.innerHTML = t.responseText;
            if (!options || !options.error) {
                var control = node.firstChild;
                if (_frevvo.wizardView && _frevvo.wizardView.is(control)) {
                    _frevvo.wizardController.main.setup(control, options, this.$lightbox());
                    options && (options.wizardEl = control);
                    if (Element.hasClassName(control, "f-custom-palette-wizard") && checkIt("msie")) {
                        _frevvo.lightBoxView.refreshOnDismiss = true;
                    }
                    if (Element.hasClassName(control, "tabbed-wizard")) {
                        TabbedWizardEditController.setup(control);
                    }
                } else {
                    if (_frevvo.utilities.util.isDefined(window._frevvo.switchControlView) && _frevvo.switchControlView.is(control)) {
                        _frevvo.uberController.setup(control);
                        _frevvo.switchControlController.setupChildren(control);
                    } else {
                        if (Element.hasClassName(control, "f-schemas")) {
                            _frevvo.lightBoxView.refreshOnDismiss = true;
                            SchemasController.setup(_frevvo.protoView.$childByClass(control, "c-schemas"));
                        } else {
                            if (Element.hasClassName(control, "tabbed-wizard")) {
                                TabbedWizardEditController.setup(control);
                            } else {}
                        }
                    }
                }
            }
        }
        if (options && options.absolutePosition && options.refEl) {
            _frevvo.utilities.absolutePositioner.display(options.refEl, this.$lightbox(), $("form-container"), 10);
        } else {
            if (options && options.centerPosition) {
                _frevvo.utilities.absolutePositioner.displayTopCenter(this.$lightbox(), $("form-container"));
            } else {
                if (options && options.centerFixed) {
                    _frevvo.utilities.absolutePositioner.displayCenterFixed(window, this.$lightbox());
                }
            }
        }
        this.showElement(node.firstChild, title);
        this.$lightbox().focus();
        var script = _frevvo.protoView.$childByName(node, "script");
        if (script) {
            eval(script.innerHTML);
        }
        if (options && options.callback) {
            try {
                options.callback(options);
            } catch (e) {}
        }
    },
    showWaitDialog: function(a, b) {
        if (this.isVisible()) {
            return false;
        }
        this.setClassName(this.$lightbox(), "s-wait-lightbox");
        if (a == null) {
            a = document.createElement("div");
        }
        this.showElement(a, b);
    },
    hideWaitDialog: function() {
        this.dismiss(null);
    },
    hideSaveDialog: function() {
        this.dismiss(null);
    },
    showLimitedHeightInfoDialog: function(a, b) {
        if (this.isVisible()) {
            return false;
        }
        this.setClassName(this.$lightbox(), "s-medium-lightbox");
        this.showInfoDialog(a, b);
    },
    showInfoDialog: function(b, d, c, a) {
        b = $(b);
        if (this.isVisible()) {
            return false;
        }
        this.setClassName(this.$lightbox(), c, "s-info-lightbox");
        if (a && a.absolutePosition && a.refEl) {
            _frevvo.utilities.absolutePositioner.display(a.refEl, this.$lightbox(), $("form-container"), 10);
        }
        this.showElement(b, d);
        if (b.setAttribute) {
            b.setAttribute("tabindex", "-1");
        }
        _frevvo.baseDialogController.setup(b);
        return false;
    },
    showSaveDialog: function(b, d, c, a) {
        b = $(b);
        if (this.isVisible()) {
            return false;
        }
        this.setClassName(this.$lightbox(), c, "s-save-lightbox");
        if (a && a.absolutePosition && a.refEl) {
            _frevvo.utilities.absolutePositioner.display(a.refEl, this.$lightbox(), $("form-container"), 10);
        }
        this.showElement(b, d);
        return false;
    },
    yesHandler: null,
    showYesNoDialog: function(b, c, a) {
        if (this.isVisible()) {
            return false;
        }
        this.setClassName(this.$lightbox(), "s-yesno-lightbox");
        if (a) {
            this.yesHandler = a;
        }
        this.showElement(b, c);
    },
    showElement: function(a, b) {
        if (this.lightbox == null) {
            this.create();
        }
        if (b) {
            this.$title().innerHTML = b;
        }
        this.lightbox.setElement(a);
        this.lightbox.activate();
    },
    setClassName: function(b, c, a) {
        if (!c) {
            c = a;
        }
        this.setClassNames(b, [c], a);
    },
    setClassNames: function(d, c, b) {
        if (c) {
            for (var a = 0; a < c.length; a++) {
                Element.addClassName(this.$lightbox(), c[a]);
            }
            this.clsNames = c;
        } else {
            Element.addClassName(this.$lightbox(), b);
            this.clsNames = [b];
        }
    },
    isVisible: function() {
        if (this.clsNames != null) {
            return true;
        }
        return false;
    },
    closeObserver: function(a) {
        if (_frevvo.uberController.event.isActivationEvent(a)) {
            this.dismiss(a);
        }
    },
    closeHotKeyObserver: function(a) {
        if (a.type == "keydown" && a.keyCode == "27") {
            this.dismiss(a);
        }
    },
    dismiss: function(a) {
        if (a) {
            Event.stop(a);
        }
        var c = this.$lightbox();
        if (this.clsNames) {
            for (var b = 0; b < this.clsNames.length; b++) {
                Element.removeClassName(c, this.clsNames[b]);
            }
        }
        this.clsNames = null;
        _frevvo.utilities.absolutePositioner.undisplay(c);
        this.hide();
        this.returnFocusEl && this.returnFocusEl.focus();
        if (_frevvo.lightBoxView.refreshOnDismiss) {
            if (_frevvo.localeStrings) {
                this.showWaitDialog(_frevvo.protoView.makeNode(_frevvo.localeStrings.pleaseWait2), _frevvo.localeStrings.pleasewait);
            } else {
                this.showWaitDialog(_frevvo.protoView.makeNode("Please wait ..."), "Please wait");
            }
            window.location.reload(true);
        }
    },
    handleAndDismiss: function(a) {
        if (this.yesHandler) {
            this.yesHandler(a);
            this.yesHandler = null;
        }
        this.dismiss(a);
    },
    hide: function() {
        if (this.lightbox) {
            this.lightbox.deactivate();
        }
    }
});
_frevvo.workAreaView = {};
Object.extend(_frevvo.workAreaView, _frevvo.protoView);
Object.extend(_frevvo.workAreaView, {
    $waIFrame: function() {
        return $("wa-iframe");
    },
    $waNoIFrame: function() {
        return $("wa-no-iframe");
    },
    $iFrame: function() {
        return _frevvo.protoView.$childByName(_frevvo.workAreaView.$waIFrame(), "iframe");
    },
    loadIFrame: function(a) {
        _frevvo.workAreaView.showIFrame();
        _frevvo.workAreaView.$iFrame().src = a;
    },
    loadFramelessContainer: function(b) {
        var c = function() {
            if (_frevvo && _frevvo.workAreaView) {
                _frevvo.workAreaView.showFrameless();
                setTimeout("_frevvo.workAreaView.sizeHeight();", 500);
            }
            if (TaskPoller && TaskPoller.doRefresh) {
                TaskPoller.doRefresh();
            }
        };
        var a = function(e) {
            $("_frevvo-taskworkarea-frameless").innerHTML = "<div class='error-box'>" + e + "</div>";
            _frevvo.workAreaView.showFrameless();
        };
        var d = function() {
            try {
                if (TaskPoller) {
                    TaskPoller.doRefresh();
                }
            } catch (f) {}
        };
        _frevvo.api.loadForm("_frevvo-taskworkarea-frameless", b, c, a, d);
    },
    showPage: function(b, c, a) {
        var d;
        if (c) {
            d = this.handleReturn.bindAsObserver(this, c);
        } else {
            d = this.handleReturn.bindAsObserver(this);
        }
        var e = "get";
        if (a) {
            e = a;
        }
        new Ajax.Request(b, {
            method: e,
            onComplete: d
        });
        return false;
    },
    showNode: function(a) {
        _frevvo.utilities.removeAllChildren(this.$waNoIFrame());
        _frevvo.utilities.insertFirst(a, this.$waNoIFrame());
        this.showNoIFrame();
    },
    handleReturn: function(t, options) {
        var error = t.getResponseHeader("frevvo.app.error.message");
        var node = $("a-invisible-span");
        if (error) {
            node.innerHTML = error;
        } else {
            node.innerHTML = t.responseText;
        }
        this.showNoIFrame();
        _frevvo.utilities.removeAllChildren(this.$waNoIFrame());
        _frevvo.utilities.insertFirst(node.firstChild, this.$waNoIFrame());
        var script = _frevvo.protoView.$childByName(node, "script");
        if (script) {
            eval(script.innerHTML);
        }
    },
    hideBoth: function() {
        _frevvo.utilities.removeAllChildren(_frevvo.workAreaView.$waNoIFrame());
        Element.hide(_frevvo.workAreaView.$waNoIFrame());
        Element.hide(_frevvo.workAreaView.$waIFrame());
        Element.hide($("_frevvo-taskworkarea-frameless"));
    },
    showIFrame: function() {
        var a = _frevvo.workAreaView.$waNoIFrame();
        if (a) {
            Element.hide(a);
        }
        a = $("_frevvo-taskworkarea-frameless");
        if (a) {
            Element.hide(a);
        }
        a = _frevvo.workAreaView.$waIFrame();
        if (a) {
            Element.show(a);
        }
    },
    showNoIFrame: function() {
        Element.hide($("_frevvo-taskworkarea-frameless"));
        Element.hide(_frevvo.workAreaView.$waIFrame());
        Element.show(_frevvo.workAreaView.$waNoIFrame());
    },
    showFrameless: function() {
        Element.hide(_frevvo.workAreaView.$waIFrame());
        Element.hide(_frevvo.workAreaView.$waNoIFrame());
        Element.show($("_frevvo-taskworkarea-frameless"));
    },
    showPleaseWait: function() {
        _frevvo.lightBoxView.showWaitDialog(_frevvo.protoView.makeNode("<span><strong>Processing.</strong><br/><small><em>Please wait...</em></small></span>"), "working...");
    },
    sizeHeight: function() {
        var a;
        if ($("_frevvo-taskworkarea-frameless")) {
            a = $("_frevvo-taskworkarea-frameless").scrollHeight + 25;
            $("task-workarea").style.height = a + "px";
        }
    }
});
_frevvo.wizardView = {};
Object.extend(_frevvo.wizardView, _frevvo.protoView);
Object.extend(_frevvo.wizardView, {
    $panels: function(a) {},
    getLiteral: function(a) {
        return "f-wizard";
    },
    is: function(a) {
        return (a && a.className && a.className.indexOf(this.getLiteral(a)) >= 0);
    }
});
CustomView = {
    $frevvoControl: function(a) {
        if (!a) {
            return null;
        }
        a = a.parentNode;
        if (!a) {
            return null;
        }
        while (true) {
            if (Element.hasClassName(a, "f-control")) {
                return a;
            }
            if (Element.hasClassName(a, "f-form")) {
                return null;
            }
            a = a.parentNode;
        }
        getLoadButton;
    },
    getState: function(b) {
        b = this.$frevvoControl(b);
        if (!b) {
            return null;
        }
        var a = _frevvo.baseView.getView(b);
        if (a) {
            return a.getState(b);
        }
        return null;
    },
    getExtId: function(b) {
        b = this.$frevvoControl(b);
        if (!b) {
            return null;
        }
        var a = _frevvo.baseView.getView(b);
        if (a) {
            return a.getExtId(b);
        }
        return null;
    },
    getIndex: function(b) {
        b = this.$frevvoControl(b);
        if (!b) {
            return -1;
        }
        var a = _frevvo.baseView.getView(b);
        if (!a) {
            return -1;
        }
        var d = _frevvo.baseView.getRepeatId(b);
        if (!d) {
            return -1;
        }
        var c = _frevvo.baseView.getControl(d);
        if (!c) {
            return -1;
        }
        return _frevvo.repeatView.getIndex(c, b);
    },
    hasClass: function(b, a) {
        b = this.$frevvoControl(b);
        if (!b) {
            return false;
        }
        return Element.hasClassName(b, a);
    }
};
_frevvo.baseView = {};
Object.extend(_frevvo.baseView, _frevvo.protoView);
Object.extend(_frevvo.baseView, {
    getView: function(a) {
        if (!a) {
            return null;
        }
        if (_frevvo.dateView.is(a)) {
            return _frevvo.dateView;
        }
        if (_frevvo.inputView.is(a)) {
            return _frevvo.inputView;
        } else {
            if (_frevvo.textAreaView.is(a)) {
                return _frevvo.textAreaView;
            } else {
                if (_frevvo.signatureView.is(a)) {
                    return _frevvo.signatureView;
                } else {
                    if (_frevvo.richTextAreaView.is(a)) {
                        return _frevvo.richTextAreaView;
                    } else {
                        if (_frevvo.linkView.is(a)) {
                            return _frevvo.linkView;
                        } else {
                            if (_frevvo.outputView.is(a)) {
                                return _frevvo.outputView;
                            } else {
                                if (_frevvo.videoView.is(a)) {
                                    return _frevvo.videoView;
                                } else {
                                    if (_frevvo.imageView.is(a)) {
                                        return _frevvo.imageView;
                                    } else {
                                        if (_frevvo.dropdownView.is(a)) {
                                            return _frevvo.dropdownView;
                                        } else {
                                            if (_frevvo.radioView.is(a)) {
                                                return _frevvo.radioView;
                                            } else {
                                                if (_frevvo.checkboxView.is(a)) {
                                                    return _frevvo.checkboxView;
                                                } else {
                                                    if (_frevvo.groupView.is(a)) {
                                                        return _frevvo.groupView;
                                                    } else {
                                                        if (_frevvo.panelView.is(a)) {
                                                            return _frevvo.panelView;
                                                        } else {
                                                            if (_frevvo.sectionView.is(a)) {
                                                                return _frevvo.sectionView;
                                                            } else {
                                                                if (_frevvo.tableHeadView.is(a)) {
                                                                    return _frevvo.tableHeadView;
                                                                } else {
                                                                    if (_frevvo.tableHeadColumnView.is(a)) {
                                                                        return _frevvo.tableHeadColumnView;
                                                                    } else {
                                                                        if (_frevvo.tableRowView.is(a)) {
                                                                            return _frevvo.tableRowView;
                                                                        } else {
                                                                            if (_frevvo.tableView.is(a)) {
                                                                                return _frevvo.tableView;
                                                                            } else {
                                                                                if (_frevvo.repeatView.is(a)) {
                                                                                    return _frevvo.repeatView;
                                                                                } else {
                                                                                    if (_frevvo.switchControlView.is(a)) {
                                                                                        return _frevvo.switchControlView;
                                                                                    } else {
                                                                                        if (_frevvo.switchCaseView.is(a)) {
                                                                                            return _frevvo.switchCaseView;
                                                                                        } else {
                                                                                            if (_frevvo.submitView.is(a)) {
                                                                                                return _frevvo.submitView;
                                                                                            } else {
                                                                                                if (_frevvo.triggerView.is(a)) {
                                                                                                    return _frevvo.triggerView;
                                                                                                } else {
                                                                                                    if (_frevvo.uploadView.is(a)) {
                                                                                                        return _frevvo.uploadView;
                                                                                                    } else {
                                                                                                        if (_frevvo.helpContentView.$is(a)) {
                                                                                                            return _frevvo.helpContentView;
                                                                                                        } else {
                                                                                                            if (_frevvo.formView.is(a)) {
                                                                                                                return _frevvo.formView;
                                                                                                            } else {
                                                                                                                if (_frevvo.linkedFormViewerView.is(a)) {
                                                                                                                    return _frevvo.linkedFormViewerView;
                                                                                                                } else {
                                                                                                                    if (_frevvo.phoneGroupView.is(a)) {
                                                                                                                        return _frevvo.phoneGroupView;
                                                                                                                    } else {
                                                                                                                        if (_frevvo.pageBreakView.is(a)) {
                                                                                                                            return _frevvo.pageBreakView;
                                                                                                                        } else {
                                                                                                                            if (_frevvo.comboBoxView.is(a)) {
                                                                                                                                return _frevvo.comboBoxView;
                                                                                                                            } else {
                                                                                                                                return null;
                                                                                                                            }
                                                                                                                        }
                                                                                                                    }
                                                                                                                }
                                                                                                            }
                                                                                                        }
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    },
    isAuthorized: function(a) {
        return !Element.hasClassName(a, "s-not-authorized");
    },
    isGroupView: function(a) {
        return (_frevvo.groupView.is(a) || _frevvo.panelView.is(a) || _frevvo.sectionView.is(a) || _frevvo.repeatView.is(a) || _frevvo.switchControlView.is(a) || _frevvo.switchCaseView.is(a));
    },
    $elements: function(a) {
        if (this.$element) {
            return [this.$element(a)];
        } else {
            return null;
        }
    },
    $facade: function(a) {
        var b = this.$elements(a);
        if (b && b.length > 0) {
            if (Element.hasClassName(b[0], "facade")) {
                return [b[0]];
            }
            var c = b[0].parentNode;
            if (Element.hasClassName(c, "facade")) {
                return [c];
            }
        }
        return null;
    },
    $editContainer: function(a) {
        return a.editor;
    },
    $required: function(a) {
        return a;
    },
    $feedback: function(a) {
        return this.$childByClass(this.$edit(a), "e-feedback");
    },
    $draggable: function(a) {
        return a;
    },
    $droppable: function(a) {
        return a;
    },
    $focusElement: function(a) {
        return null;
    },
    getExtId: function(a) {
        return a.getAttribute("extId");
    },
    setExtId: function(b, a) {
        b.setAttribute("extId", a);
    },
    removeExtId: function(a) {
        a.removeAttribute("extId");
    },
    getParentControl: function(b) {
        var a = b.parentNode;
        if (!a) {
            return null;
        }
        a = a.parentNode;
        if (!a) {
            return null;
        }
        if (Element.hasClassName(a, "f-form")) {
            return null;
        } else {
            return a;
        }
    },
    getRequired: function(a) {
        return Element.hasClassName(this.$required(a), "s-required");
    },
    setRequired: function(b, a) {
        if (a) {
            Element.addClassName(this.$required(b), "s-required");
            this.$element && this.$element(b) && this.$element(b).setAttribute("aria-required", "true");
        } else {
            Element.removeClassName(this.$required(b), "s-required");
            this.$element && this.$element(b) && this.$element(b).setAttribute("aria-required", "false");
        }
    },
    setReadOnly: function(b, a) {},
    getCSSClass: function(a) {
        return a.getAttribute("cssClass");
    },
    setCSSClass: function(b, a) {
        if (b.getAttribute("cssClass")) {
            Element.removeClassName(b, b.getAttribute("cssClass"));
        }
        if (a) {
            Element.addClassName(b, a);
            b.setAttribute("cssClass", a);
        } else {
            b.setAttribute("cssClass", null);
        }
    },
    getRepeatIndex: function(a) {},
    getControl: function(a) {
        return document.getElementById(a);
    },
    getControlId: function(a) {
        return a.getAttribute(FrevvoConstants.CONTROL_ID);
    },
    setControlId: function(b, a) {
        b.setAttribute(FrevvoConstants.CONTROL_ID, a);
    },
    removeControlId: function(a) {
        a.removeAttribute(FrevvoConstants.CONTROL_ID);
    },
    setLabel: function(b, a) {
        try {
            this.$label(b).innerHTML = a;
        } catch (c) {}
    },
    getLabel: function(a) {
        try {
            return this.$label(a).innerHTML;
        } catch (b) {
            return null;
        }
    },
    setHint: function(b, a) {
        try {
            b.setAttribute("title", a);
        } catch (c) {}
    },
    getHint: function(a) {
        try {
            return a.getAttribute("title");
        } catch (b) {
            return null;
        }
    },
    setPlaceholder: function(b, a) {
        try {
            var f = this.$element(b);
            if (f) {
                f.setAttribute("placeholder", a);
            }
        } catch (d) {}
    },
    getAside: function(a) {
        return _frevvo.protoView.$childByName(a, "aside");
    },
    setUseDefaultDecorator: function(d, b, a) {
        try {
            var c = this.getAside(d);
            if (b.length > 0 && b === "true") {
                Element.addClassName(d, "s-prepend");
                if (a) {
                    Element.addClassName(d, "s-append");
                } else {
                    Element.removeClassName(d, "s-append");
                }
                if (Element.hasClassName(d, "Time")) {
                    Element.removeClassName(c, "icon-calendar-empty");
                    Element.addClassName(c, "icon-time");
                } else {
                    if (Element.hasClassName(d, "Date") || Element.hasClassName(d, "DateTime")) {
                        Element.removeClassName(c, "icon-time");
                        Element.addClassName(c, "icon-calendar-empty");
                    }
                }
            } else {
                Element.removeClassName(d, "s-prepend");
                Element.removeClassName(d, "s-append");
            }
        } catch (f) {}
    },
    setCustomDecorator: function(c, a) {
        try {
            var b = this.getAside(c);
            Element.classNames(b).each(function(e) {
                if (e.indexOf("icon-") > -1) {
                    Element.removeClassName(b, e);
                    return;
                }
            });
            if (a.length > 0) {
                Element.addClassName(c, "s-prepend");
                Element.addClassName(b, "fontawesome");
                Element.addClassName(b, a);
            } else {
                Element.removeClassName(c, "s-prepend");
                Element.removeClassName(b, "fontawesome");
            }
        } catch (d) {}
    },
    isLinked: function(a) {
        return (Element.hasClassName(a, "s-linked"));
    },
    setName: function(b, a) {
        b.setAttribute("cname", a);
    },
    getName: function(a) {
        return a.getAttribute("cname");
    },
    setStyle: function(a, c) {
        for (var b = 0; b < a.length; b++) {
            Element.setStyle(a[b], c);
        }
    },
    setColumns: function(b, a) {
        Element.removeClassName(b, "One");
        Element.removeClassName(b, "Two");
        Element.removeClassName(b, "Three");
        Element.removeClassName(b, "Four");
        Element.removeClassName(b, "Five");
        Element.removeClassName(b, "Six");
        Element.removeClassName(b, "Seven");
        Element.removeClassName(b, "Eight");
        Element.removeClassName(b, "Nine");
        Element.removeClassName(b, "Ten");
        Element.removeClassName(b, "Eleven");
        Element.removeClassName(b, "Twelve");
        Element.addClassName(b, a);
    },
    setWidth: function(b, a) {
        Element.setStyle(b, $H({
            width: a
        }));
    },
    setHeight: function(b, a) {
        Element.setStyle(b, $H({
            height: a
        }));
    },
    setControlWidth: function(b, a) {
        Element.setStyle(b, $H({
            width: a
        }));
    },
    setItemWidth: function(b, a) {
        return;
    },
    setBackgroundColor: function(b, a) {
        Element.setStyle(b, $H({
            "background-color": a
        }));
    },
    setBorderWidth: function(b, a) {
        this.$facade && this.$facade(b) && _frevvo.baseView.setStyle(this.$facade(b), $H({
            "border-width": a
        }));
    },
    setBorderColor: function(b, a) {
        this.$facade && this.$facade(b) && _frevvo.baseView.setStyle(this.$facade(b), $H({
            "border-color": a
        }));
    },
    setBorderStyle: function(b, a) {
        this.$facade && this.$facade(b) && _frevvo.baseView.setStyle(this.$facade(b), $H({
            "border-style": a
        }));
    },
    setLabelSize: function(b, a) {
        this.$label(b) && Element.setStyle(this.$label(b).parentNode, $H({
            "font-size": a
        }));
    },
    setLabelColor: function(b, a) {
        this.$label(b) && Element.setStyle(this.$label(b).parentNode, $H({
            color: a
        }));
    },
    setLabelWidth: function(b, a) {
        this.$label(b) && Element.setStyle(this.$label(b).parentNode, $H({
            width: a
        }));
    },
    setNewLine: function(b, a) {
        if (a == "true") {
            Element.addClassName(b, "s-new-line");
        } else {
            Element.removeClassName(b, "s-new-line");
        }
    },
    setLabelBold: function(b, a) {
        if (!this.$label(b)) {
            return;
        }
        if (a == "true") {
            Element.setStyle(this.$label(b).parentNode, $H({
                "font-weight": "bold"
            }));
        } else {
            Element.setStyle(this.$label(b).parentNode, $H({
                "font-weight": ""
            }));
        }
    },
    setLabelItalic: function(b, a) {
        if (!this.$label(b)) {
            return;
        }
        if (a == "true") {
            Element.setStyle(this.$label(b).parentNode, $H({
                "font-style": "italic"
            }));
        } else {
            Element.setStyle(this.$label(b).parentNode, $H({
                "font-style": ""
            }));
        }
    },
    isValid: function(a) {
        return !Element.hasClassName(a, "s-invalid");
    },
    isTyped: function(a) {
        return Element.hasClassName(a, "s-typed");
    },
    setTyped: function(a) {
        Element.removeClassName(a, "s-untyped");
        Element.addClassName(a, "s-typed");
    },
    isEnabled: function(b) {
        try {
            var a = this.$element(b).disabled;
            if (a) {
                return !a;
            }
            return true;
        } catch (c) {
            return true;
        }
    },
    setEnabled: function(b, a) {
        if (a == "true") {
            Element.removeClassName(b, "s-disabled");
            if (this.$element(b)) {
                this.$element(b).disabled = false;
                this.$element(b).removeAttribute("aria-disabled");
            }
        } else {
            Element.addClassName(b, "s-disabled");
            if (this.$element(b)) {
                this.$element(b).disabled = true;
                this.$element(b).setAttribute("aria-disabled", "true");
            }
        }
    },
    isVisible: function(a) {
        return a && !Element.hasClassName(a, "s-invisible");
    },
    setVisible: function(b, a) {
        if (a == "true") {
            Element.removeClassName(b, "s-invisible");
        } else {
            Element.addClassName(b, "s-invisible");
        }
    },
    setHideLabel: function(b, a) {
        if (a == "true") {
            Element.addClassName(b, "s-hidelabel");
        } else {
            Element.removeClassName(b, "s-hidelabel");
        }
    },
    setStatus: function(e, a, c) {
        if (this.$status) {
            var d = this.$status(e);
            if (d) {
                var f = this.$childByName(d, "span");
                if (a) {
                    var g = false;
                    for (var b = (f.childNodes.length - 1); b >= 0; b--) {
                        if (f.childNodes[b].nodeValue == a) {
                            g = true;
                            break;
                        }
                    }
                    if (!g) {
                        f.setAttribute("role", "alert");
                        d.style.clip = "auto";
                        for (var b = (f.childNodes.length - 1); b >= 0; b--) {
                            f.removeChild(f.childNodes[b]);
                        }
                        var h = document.createTextNode(a);
                        f.style.display = "none";
                        f.style.display = "inline";
                        f.appendChild(h);
                        d.parentNode.style.height = d.offsetHeight + "px";
                    }
                    this.$element && this.$element(e) && this.$element(e).setAttribute("aria-invalid", "true");
                } else {
                    if (!c) {
                        f.removeAttribute("role");
                        for (var b = (f.childNodes.length - 1); b >= 0; b--) {
                            f.removeChild(f.childNodes[b]);
                        }
                        d.style.clip = "rect(0px,0px,0px,0px)";
                        d.parentNode.style.height = "auto";
                        this.$element && this.$element(e) && this.$element(e).removeAttribute("aria-invalid");
                    }
                }
            }
        }
    },
    getForm: function() {
        return this.$childByName($("form-container"), "form");
    },
    setBusy: function(a, b) {
        if (b) {
            Element.addClassName(a, "s-busy");
        } else {
            Element.removeClassName(a, "s-busy");
        }
    },
    setState: function(d, b, f) {
        var l = this;
        if (this.isEditing(d)) {
            if (b.xhtml) {
                if (b.displayType && (b.displayType == "Dropdown" || b.displayType == "Radio")) {
                    Element.removeClassName(d, l.getLiteral(d));
                    Element.addClassName(d, "f-select1");
                } else {
                    if (b.displayType && b.displayType == "Checkbox") {
                        Element.removeClassName(d, l.getLiteral(d));
                        Element.addClassName(d, "f-select");
                    } else {
                        if (b.literal && b.literal != l.getLiteral(d)) {
                            Element.removeClassName(d, l.getLiteral(d));
                            Element.addClassName(d, b.literal);
                        }
                    }
                }
                var a = l.$replaceableElements(d);
                if (a != null && a.length > 0) {
                    var m = a[0].parentNode;
                    for (var h = 1; h < a.length; h++) {
                        Element.remove(a[h]);
                    }
                    var n = a[0].nextSibling;
                    Element.remove(a[0]);
                    var c = _frevvo.protoView.makeNodes(b.xhtml);
                    for (var h = 0; h < c.length; h++) {
                        _frevvo.utilities.insertBefore(c[h], n, m);
                    }
                    l.updateDisplayType(d, b.displayType);
                    l = this.getView(d);
                    if (l.isChangeable()) {
                        l.observeValueChange(d, _frevvo.uberController.valueChangeObserver.bindAsObserver(_frevvo.uberController, d));
                        _frevvo.uberController.keyup.setup(d, l);
                        l.observeFocus(d);
                        l.observeBlur(d);
                    }
                    if (_frevvo.repeatView.is(d)) {
                        RepeatController.tearDown(d);
                        _frevvo.groupController.tearDownChildren(d);
                        RepeatController.setup(d);
                        _frevvo.groupController.setupChildren(d);
                    } else {
                        if (_frevvo.tableView.is(d)) {
                            _frevvo.tableController.tearDown(d);
                            _frevvo.groupController.tearDownChildren(d);
                            d.isSetup = false;
                            _frevvo.tableController.setup(d);
                            _frevvo.tableHeadController.setup(_frevvo.tableView.$tablehead(d));
                            _frevvo.groupController.expandedSetupChildren(d);
                        }
                    }
                }
            }
            if (b.displayType) {
                if (!Element.hasClassName(d, b.displayType)) {
                    l.updateDisplayType(d, b.displayType);
                    if (b.displayType == "Date" || b.displayType == "Time" || b.displayType == "DateTime") {
                        _frevvo.dateController.setup(d);
                        l.observeValueChange(d, _frevvo.uberController.valueChangeObserver.bindAsObserver(_frevvo.uberController, d));
                        this.removeEditContainer(d);
                    }
                    if (b.displayType == "LinkedFormViewer" || b.displayType == "EmbeddedFormViewer") {
                        l = this.getView(d);
                    }
                }
            }
            b.columns && l.setColumns && l.setColumns(d, b.columns);
        }
        b.attachments && l.setAttachments(d, b.attachments);
        if (b.itemLabels) {
            l.setOptions(d, b.itemLabels, b.itemValues, b.name);
        }
        if (b.optionsUrl) {
            l.setOptionsUrl(d, b.optionsUrl);
        }
        if (b.busy != "true") {
            this.setBusy(d, false);
        } else {
            this.setBusy(d, true);
        }
        b.maxReached && l.setMaxReached && b.maxReached == "false" && l.setMaxReached(d, b.maxReached);
        b.enabled && l.setEnabled(d, b.enabled);
        b.maxReached && l.setMaxReached && b.maxReached == "true" && l.setMaxReached(d, b.maxReached);
        b.readOnly && l.setReadOnly && l.setReadOnly(d, b.readOnly);
        b.visible && l.setVisible(d, b.visible);
        b.hidelabel && l.setHideLabel(d, b.hidelabel);
        b.selected && l.setSelected(d, b.selected);
        b.signable && l.setSignable && l.setSignable(d, b.signable, b.mustSign);
        b.wetSignable && l.setWetSignable && l.setWetSignable(d, b.wetSignable);
        if (!this.isEditing(d)) {
            b.signed && l.setSigned && l.setSigned(d, b.signed, b.signUserFullName, b.signDate, b.signatureLocked, b.signatureImageUrl);
            b.authorized && l.setAuthorized && l.setAuthorized(d, b.authorized);
            b.badData && l.setBadData && l.setBadData(d, b.badData);
        }
        l.setStatus(d, b.status, f);
        if (b.label || b.label == "") {
            l.setLabel(d, b.label);
        }
        if (b.hint || b.hint == "") {
            l.setHint(d, b.hint);
        }
        if (b.placeholder || b.placeholder == "") {
            l.setPlaceholder(d, b.placeholder);
        }
        if (b.useDefaultDecorator) {
            l.setUseDefaultDecorator(d, b.useDefaultDecorator, false);
        }
        if (b.customDecorator) {
            l.setCustomDecorator(d, b.customDecorator);
        }
        if (b.help || b.help == "") {
            if (l.$helpContent) {
                var j = l.$helpContent(d);
                if (j) {
                    _frevvo.helpContentView.$setContent(j, b.help);
                    _frevvo.helpDisplayView.$setContent(b.help);
                }
            }
        }
        if (l.$help) {
            var j = l.$helpContent(d);
            if (j) {
                var k = _frevvo.helpContentView.$getContent(j);
                var e = l.$help(d);
                if (k) {
                    Element.addClassName(e, "s-help");
                } else {
                    Element.removeClassName(e, "s-help");
                }
            }
        }
        if (this.isEditing(d)) {
            b.rows && l.setRows(d, b.rows);
            b.maxLength && l.setMaxLength(d, b.maxLength);
            b.name ? l.setName(d, b.name) : l.setName(d, null);
            b.id && l.setControlId(d, b.id);
            b.extId ? l.setExtId(d, b.extId) : l.setExtId(d, null);
        }
        if (l.setGeneratedFormUri && b.generatedFormUri) {
            l.setGeneratedFormUri(d, b.generatedFormUri);
        }
        b.valid && l.setValid && l.setValid(d, b.valid);
        b.expanded && l.setExpanded && l.setExpanded(d, b.expanded);
        if (b.values) {
            if (b.values.length > 0) {
                var g = [];
                if (_frevvo.utilities.util.isArray(b.values)) {
                    g = b.values;
                } else {
                    g[0] = b.values;
                }
                if (g) {
                    l.setValue(d, g);
                    if (_frevvo.baseView.isValid(d)) {
                        l.onValueChange && l.onValueChange(d);
                    } else {
                        l.onValueChangeFailed && l.onValueChangeFailed(d);
                    }
                } else {
                    l.setValue(d, null);
                }
            } else {
                l.setValue(d, null);
            }
        } else {
            if (_frevvo.baseView.isValid(d)) {
                l.onValueChange && l.onValueChange(d);
            } else {
                l.onValueChangeFailed && l.onValueChangeFailed(d);
            }
        }
        if (b.dateValues) {
            if (b.dateValues[0]) {
                d.setAttribute("dateValue", b.dateValues[0]);
            } else {
                d.removeAttribute("dateValue");
            }
        }
        if (b.minOccurs) {
            if (b.minOccurs != 0) {
                l.setRequired(d, true);
            } else {
                l.setRequired(d, false);
            }
        }
        if (b.required) {
            if (b.required === "true") {
                l.setRequired(d, true);
            } else {
                l.setRequired(d, false);
            }
        }
        if ((b.minOccurs || b.maxOccurs) && l.isRepeatItem(d)) {
            _frevvo.repeatView.updateMinMaxOccurs(d, b);
        } else {
            if ((b.minOccurs || b.maxOccurs) && _frevvo.tableView.is(d)) {
                _frevvo.tableView.updateMinMaxOccurs(d, b);
            }
        }
        if (_frevvo.tableRowView.is(d)) {
            if (b.deletable) {
                if (b.deletable === "false") {
                    _frevvo.tableRowView.setDeletable(d, false);
                } else {
                    _frevvo.tableRowView.setDeletable(d, true);
                }
            }
        }
        if (_frevvo.inputView.is(d)) {
            if (b.displayType == "Password") {
                l.setPassword(d);
            } else {
                l.unsetPassword(d);
            }
        }
        if (this.isEditing(d)) {
            b.width ? l.setWidth(d, b.width) : l.setWidth(d, "");
            b.height ? l.setHeight(d, b.height) : l.setHeight(d, "");
            b.newLine ? l.setNewLine(d, b.newLine) : l.setNewLine(d, "");
            l.setCSSClass(d, b.cssClass);
            b.controlWidth ? l.setControlWidth(d, b.controlWidth) : l.setControlWidth(d, "");
            b.itemWidth ? l.setItemWidth(d, b.itemWidth) : l.setItemWidth(d, "");
            b.backgroundColor ? l.setBackgroundColor(d, b.backgroundColor) : l.setBackgroundColor(d, "");
            b.borderWidth ? l.setBorderWidth(d, b.borderWidth) : l.setBorderWidth(d, "");
            b.borderColor ? l.setBorderColor(d, b.borderColor) : l.setBorderColor(d, "");
            b.borderStyle ? l.setBorderStyle(d, b.borderStyle) : l.setBorderStyle(d, "");
            b.labelSize ? l.setLabelSize(d, b.labelSize) : l.setLabelSize(d, "");
            b.labelColor ? l.setLabelColor(d, b.labelColor) : l.setLabelColor(d, "");
            b.labelWidth ? l.setLabelWidth(d, b.labelWidth) : l.setLabelWidth(d, "");
            b.labelBold && l.setLabelBold(d, b.labelBold);
            b.labelItalic && l.setLabelItalic(d, b.labelItalic);
            b.center && l.setCenter && l.setCenter(d, b.center);
            b.horizontal && l.setHorizontal && l.setHorizontal(d, b.horizontal);
            b.controlColor && l.setControlColor && l.setControlColor(d, b.controlColor);
            b.expandable && l.setExpandable && l.setExpandable(d, b.expandable);
            b.showPicker && l.setShowPicker && l.setShowPicker(d, b.showPicker);
            b.hideItemLabel && l.setHideItemLabel && l.setHideItemLabel(d, b.hideItemLabel);
        }
        if (_frevvo.selectView.is(d) || _frevvo.dropdownView.is(d)) {
            if (this.isEditing(d)) {
                b.comment != null && l.setCommentEnabled(d, b.comment);
                b.commentRows != null && l.setCommentRows(d, b.commentRows);
            }
            b.commentValue && l.setCommentValue(d, b.commentValue);
            if (l.isCommentEnabled(d) && l.isLastSelected(d)) {
                l.showComment(d);
            } else {
                l.hideComment(d);
            }
        }
        l.validateState(d);
        if (this.isEditing(d) && b.xhtml) {
            _frevvo.utilities.debugPrint("Removing edit container");
            l.removeEditContainer(d);
        }
    },
    validateState: function(a) {},
    updateDisplayType: function(b, a) {
        Element.removeClassName(b, "Dropdown");
        Element.removeClassName(b, "Radio");
        Element.removeClassName(b, "Checkbox");
        Element.removeClassName(b, "Text");
        Element.removeClassName(b, "Date");
        Element.removeClassName(b, "DateTime");
        Element.removeClassName(b, "Time");
        Element.removeClassName(b, "Email");
        Element.removeClassName(b, "Money");
        Element.removeClassName(b, "Phone");
        Element.removeClassName(b, "Number");
        Element.removeClassName(b, "Quantity");
        Element.removeClassName(b, "LinkedFormViewer");
        Element.removeClassName(b, "EmbeddedFormViewer");
        Element.addClassName(b, a);
    },
    isEditing: function() {
        return _frevvo.uberController.editMode;
    },
    isPreview: function() {
        return _frevvo.uberController.previewMode;
    },
    isPrint: function() {
        return _frevvo.uberController.printMode;
    },
    isFormControl: function(a) {
        if (_frevvo.utilities.isParent(a, $("e-prototypes"))) {
            return false;
        }
        return _frevvo.utilities.isParent(a, this.getForm());
    },
    isChangeable: function() {
        return false;
    },
    isRequired: function(a) {
        if (_frevvo.groupView.is(a) || (_frevvo.sectionView.is(a)) || (_frevvo.repeatView.is(a))) {
            return false;
        }
        return (Element.hasClassName(this.$required(a), "s-required"));
    },
    isRepeatItem: function(a) {
        try {
            return (Element.hasClassName(a, "f-table-row") || Element.hasClassName(a.parentNode.parentNode, "f-repeat"));
        } catch (b) {
            return false;
        }
    },
    setRepeatId: function(a, b) {
        if (a) {
            a.setAttribute("rcId", b);
        }
    },
    getRepeatId: function(a) {
        if (a) {
            return a.getAttribute("rcId");
        } else {
            return null;
        }
    },
    removeRepeatId: function(a) {
        if (a) {
            a.removeAttribute("rcId");
        }
    },
    isCustom: function(a) {
        return a.getAttribute("custom") == "true";
    },
    isReadOnly: function(a) {
        var b = a.getAttribute("readonly");
        if (b) {
            return true;
        }
        return false;
    },
    getReadOnlyId: function(a) {
        if (a) {
            return a.getAttribute("roId");
        } else {
            return null;
        }
    },
    isComposite: function(a) {
        return false;
    },
    allowDrop: function(a, b) {
        return true;
    },
    removeEditor: function(a) {
        a.editor = null;
    },
    getRepeatControlForRepeatItem: function(a) {
        if (this.isRepeatItem(a)) {
            if (Element.hasClassName(a, "f-table-row")) {
                return _frevvo.protoView.$parentByClass(a.parentNode, "f-table");
            } else {
                return a.parentNode.parentNode;
            }
        } else {
            return null;
        }
    },
    observeAddItem: function(a, b) {
        var c = this.$add(a);
        if (c) {
            Element.addClassName(c, "s-add");
            if (Element.hasClassName(c.parentNode.parentNode, "f-table-row")) {
                c.parentNode.removeAttribute("aria-hidden");
            }
            if (!FEvent.hasObservers(c, "click")) {
                FEvent.observe(c, "click", b);
            }
            if (!FEvent.hasObservers(c, "keydown")) {
                FEvent.observe(c, "keydown", b);
            }
        }
    },
    unObserveAddItem: function(a) {
        var b = this.$add(a);
        if (b) {
            FEvent.stopObserving(b, "click");
            if (Element.hasClassName(b.parentNode.parentNode, "f-table-row")) {
                b.parentNode.setAttribute("aria-hidden", "true");
            }
            FEvent.stopObserving(b, "keydown");
            Element.removeClassName(b, "s-add");
        }
    },
    observeRemoveItem: function(b, c) {
        var a = this.$remove(b);
        if (a) {
            Element.addClassName(a, "s-remove");
            if (Element.hasClassName(a.parentNode.parentNode, "f-table-row")) {
                a.parentNode.removeAttribute("aria-hidden");
            }
            if (!FEvent.hasObservers(a, "click")) {
                FEvent.observe(a, "click", c);
            }
            if (!FEvent.hasObservers(a, "keydown")) {
                FEvent.observe(a, "keydown", c);
            }
        }
    },
    unObserveRemoveItem: function(b) {
        var a = this.$remove(b);
        if (a) {
            Element.removeClassName(a, "s-remove");
            if (Element.hasClassName(a.parentNode.parentNode, "f-table-row")) {
                a.parentNode.setAttribute("aria-hidden", "true");
            }
            FEvent.stopObserving(a, "click");
            FEvent.stopObserving(a, "keydown");
        }
    },
    showInfo: function(b, d) {
        if (_frevvo.sectionView.is(b) || _frevvo.panelView.is(b) || _frevvo.repeatView.is(b)) {
            var a = _frevvo.baseView.getView(b);
            var c = a.getControls(b);
            if (!c || c.length == 0) {
                Element.show(a.$childByClass(a.$content(b), "f-info"));
            }
        } else {
            if (_frevvo.groupView.is(b)) {
                var a = _frevvo.baseView.getView(b);
                var c = a.getControls(b);
                if (!c) {
                    Element.show(a.$childByClass(a.$content(b), "f-info"));
                } else {
                    if (c.length == 2) {
                        if (_frevvo.submitView.is(c[0]) && _frevvo.submitView.is(c[1])) {
                            Element.show(a.$childByClass(a.$content(b), "f-info"));
                        }
                    }
                }
            } else {
                if (_frevvo.switchCaseView.isContent(d)) {
                    var c = _frevvo.protoView.$childrenByName(d, "DIV");
                    if (!c || c.length == 0) {
                        Element.show(_frevvo.protoView.$childByClass(d, "f-info"));
                    }
                }
            }
        }
    },
    hideInfo: function(a) {
        var b = this.$info(a);
        if (b) {
            Element.hide(b);
        }
    },
    hideActive: function() {
        if (window.sActive) {
            Element.removeClassName(window.sActive, "s-active");
        }
    },
    onRemove: function(b, d) {
        if (d) {
            var a = b.parentNode.parentNode;
            var c = b.parentNode;
            if (c && Element.hasClassName(c, "data-column")) {
                Element.remove(c);
            } else {
                Element.remove(b);
                this.showInfo(a, c);
            }
        }
    },
    onRepeatItemDrop: function(g, e, c) {
        if ("above" === c || "left" === c || (!_frevvo.repeatView.is(e))) {
            var a = _frevvo.baseView.getView(g);
            a.removeRepeatId(g);
            a.removeEditContainer(g);
            Element.removeClassName(a.$add(g), "s-add");
            Element.removeClassName(a.$remove(g), "s-remove");
        }
        var f = _frevvo.baseView.getRepeatControlForRepeatItem(g);
        var b = _frevvo.repeatView.$repeatItems(f);
        if (b && b.length > 0) {
            for (var d = 0; d < b.length; d++) {
                Element.remove(b[d]);
            }
        }
        this.showInfo(f);
    },
    onDropRemove: function(e, b, d) {
        if (_frevvo.baseView.isRepeatItem(e)) {
            this.onRepeatItemDrop(e, b, d);
        } else {
            var a = e.parentNode.parentNode;
            var c = e.parentNode;
            Element.remove(e);
            this.showInfo(a, c);
        }
    },
    onDrop: function(c, b, a) {
        this.onDropRemove(c, b, a);
        if ("above" === a || "left" === a) {
            _frevvo.utilities.insertBefore(c, b);
        } else {
            if ("below" === a || "right" === a) {
                _frevvo.utilities.insertAfter(c, b);
            } else {
                if (Element.hasClassName(b, "f-form-tb")) {
                    _frevvo.groupView.insertFirst(c, _frevvo.formView.$rootControl(this.getForm()));
                } else {
                    _frevvo.utilities.insertAfter(c, b);
                }
            }
        }
    },
    observeHover: function(a) {
        FEvent.observe(a, "mouseover", this.onMouseOver.bindAsObserver(this, a));
        FEvent.observe(a, "mouseout", this.onMouseOut.bindAsObserver(this, a));
    },
    onMouseOver: function(a, b) {
        Event.stop(a);
        if (_frevvo.utilities.ignoreMouseEvent(a, b)) {
            return;
        }
        Element.addClassName(b, "s-hover");
    },
    onMouseOut: function(a, b) {
        Event.stop(a);
        if (_frevvo.utilities.ignoreMouseEvent(a, b)) {
            return;
        }
        Element.removeClassName(b, "s-hover");
    },
    highlight: function(a) {
        function d(g, e) {
            if (e != "transparent") {
                return e;
            }
            for (var f = 0; f < g.childNodes.length; f++) {
                if (!g.childNodes[f].style) {
                    continue;
                }
                var h = Element.getStyle(g.childNodes[f], "background-color").parseColor();
                if (h != "transparent") {
                    return h;
                }
            }
            for (var f = 0; f < g.childNodes.length; f++) {
                e = d(g.childNodes[f], e);
            }
            return e;
        }
        var b = Element.getStyle(a, "background-color").parseColor();
        b = d(a, b);
        Element.addClassName(a, "s-updated");
        var c = Element.getStyle(a, "background-color").parseColor();
        new Effect.Highlight(a, {
            startcolor: c,
            endcolor: b,
            duration: 1,
            delay: 0.5,
            afterFinish: function(e) {
                Element.removeClassName(a, "s-updated");
                a.removeAttribute("style");
            }
        });
    },
    removeEditContainer: function(a) {
        a.editor = null;
        UberEditController.edit.stopEditing(a);
        UberEditController.edit.doIt(null, a);
    },
    removeHover: function(a) {
        Element.removeClassName(a, "s-hover");
    },
    isInTableControl: function(a) {
        if (a.parentNode && a.parentNode.parentNode && Element.hasClassName(a.parentNode.parentNode, "f-table-row")) {
            return true;
        } else {
            return false;
        }
    },
    $repeatingIndexLabel: function(a) {
        return this.$childByClass(a, "f-ri-label");
    },
    setRepeatItemNumber: function(b, c) {
        var a;
        a = this.$repeatingIndexLabel(b);
        a && (a.innerHTML = a.innerHTML.replace(/\d+/, c));
    },
});
_frevvo.baseSimpleView = {};
Object.extend(_frevvo.baseSimpleView, _frevvo.baseView);
Object.extend(_frevvo.baseSimpleView, {
    $label: function(a) {
        return this.$childByName(this.$childByClass(a, "f-label"), "label");
    },
    $status: function(a) {
        return this.$childByClass(this.$childByClass(a, "f-status-positioner"), "f-status");
    },
    $help: function(a) {
        return this.$childByClass(this.$childByClass(a, "f-label"), "f-help");
    },
    $helpContent: function(a) {
        try {
            return this.$childByClass(this.$childByClass(a, "f-label"), "f-helpcontent");
        } catch (b) {}
    },
    $edit: function(a) {
        return this.$childByClass(a, "e-edit");
    },
    $editRemove: function(a) {
        return this.$childByClass(this.$edit(a), "e-remove");
    },
    $shiftLeft: function(a) {
        return this.$childByClass(this.$edit(a), "e-shift-left");
    },
    $shiftRight: function(a) {
        return this.$childByClass(this.$edit(a), "e-shift-right");
    },
    $palette: function(a) {
        return this.$childByClass(this.$edit(a), "e-palette");
    },
    $add: function(a) {
        return this.$childByClass(a, "f-add");
    },
    $remove: function(a) {
        return this.$childByClass(a, "f-remove");
    },
    $controlForDroppable: function(a) {
        return a;
    },
    $elements: function(b) {
        if (this.$element && this.$element(b)) {
            var a = [];
            a[0] = this.$element(b);
            return a;
        }
        return null;
    },
    $replaceableElements: function(a) {
        return this.$elements(a);
    },
    isChangeable: function() {
        return true;
    },
    isAtRoot: function(a) {
        a = a.parentNode.parentNode;
        if (a) {
            return "root-control" == _frevvo.baseView.getControlId(a);
        }
        return false;
    },
    setAppend: function(c, a, b) {
        _frevvo.baseView.setAppend(c, a, "false");
    },
    setHorizontal: function(b, a) {
        if (a == "true") {
            Element.addClassName(b, "s-horizontal");
        } else {
            Element.removeClassName(b, "s-horizontal");
        }
    },
    setControlColor: function(b, a) {
        if (_frevvo.triggerView.is(b) || _frevvo.outputView.is(b)) {
            Element.classNames(b).each(function(c) {
                if (c.indexOf("s-color-") > -1) {
                    Element.removeClassName(b, c);
                    return;
                }
            });
            Element.addClassName(b, "s-color-" + a);
        }
    },
    setValid: function(b, a) {
        if (a == "true") {
            Element.removeClassName(b, "s-invalid");
            Element.addClassName(b, "s-valid");
        } else {
            Element.removeClassName(b, "s-valid");
            Element.addClassName(b, "s-invalid");
        }
    },
    setValue: function(d, a) {
        var c = this.$elements(d);
        if (!c) {
            return;
        }
        if (a && a[0]) {
            for (var b = 0; b < c.length; b++) {
                if (a[b]) {
                    c[b].value = a[b];
                } else {
                    c[b].value = "";
                }
            }
        } else {
            for (var b = 0; b < c.length; b++) {
                c[b].value = "";
            }
        }
    },
    getValue: function(a) {
        return this.$element(a).value;
    },
    getTypeRequired: function(a) {
        return this.$required(a).getAttribute("e-required");
    },
    setTypeRequired: function(a) {
        this.$required(a).setAttribute("e-required", "");
    },
    getState: function(b) {
        var d = {
            name: this.getName(b),
            cssClass: this.getCSSClass(b),
            extId: this.getExtId(b),
            label: this.getLabel(b),
            visible: this.isVisible(b),
            enabled: this.isEnabled(b),
            required: this.getRequired(b)
        };
        var c = this.getValue(b);
        if (c) {
            d.value = c;
        }
        if (this.getCommentValue) {
            var a = this.getCommentValue(b);
            if (a) {
                d.commentValue = a;
            }
        }
        if (this.getHint(b)) {
            d.hint = this.getHint(b);
        }
        d.help = _frevvo.helpContentView.$getContent(this.$helpContent(b));
        return d;
    },
    observeKeyDown: function(a, b) {
        FEvent.observe(this.$element(a), "keydown", b);
    },
    observeKeyUp: function(a, b) {
        FEvent.observe(this.$element(a), "keyup", b);
    },
    observeValueChange: function(a, b) {
        FEvent.observe(this.$element(a), "change", b);
        if (this.observeCommentValueChange) {
            this.observeCommentValueChange(a, b);
        }
    },
    onValueChange: function(a) {
        Element.removeClassName(a, "s-error");
    },
    onValueChangeFailed: function(a) {
        if (!_frevvo.baseView.isValid(a) && this.getValue(a) && (this.getValue(a).length > 0)) {
            Element.addClassName(a, "s-error");
        } else {
            Element.removeClassName(a, "s-error");
        }
    },
    observeFocus: function(c) {
        var a = this.$elements(c);
        if (!a) {
            return;
        }
        for (var b = 0; b < a.length; b++) {
            FEvent.observe(a[b], "focus", function(d, f) {
                var e = Element.hasClassName(d.currentTarget, "f-select-comment") ? "s-focus2" : "s-focus";
                if (Element.hasClassName(f, e)) {
                    return;
                }
                Element.addClassName(f, e);
                if (_frevvo.datePicker.isVisible() && _frevvo.datePicker.targetElement && _frevvo.datePicker.targetElement.parentNode != f) {
                    _frevvo.datePicker.closeDatePicker();
                }
            }.bindAsObserver(this, c));
        }
    },
    observeBlur: function(c) {
        var d = this;
        var b = this.$elements(c);
        if (!b) {
            return;
        }
        for (var a = 0; a < b.length; a++) {
            FEvent.observe(b[a], "blur", function(e, f) {
                Element.removeClassName(f, "s-focus");
                Element.removeClassName(f, "s-focus2");
                if (!Element.hasClassName(f, "_frevvo-s-removing") && !_frevvo.uberController.editMode) {
                    d.lastTimeoutId = setTimeout(function() {
                        if (window.FlowController && window.FlowController.doOpInProgress) {
                            return;
                        }
                        if (d.lastTimeoutId) {
                            _frevvo.model.updateControlValue(f, _frevvo.uberController.valueChangeSuccess, function() {});
                        }
                    }, 200);
                }
            }.bindAsObserver(this, c));
        }
    },
    observeClick: function(a) {},
    lastFunction: function() {}
});
_frevvo.inputView = {};
Object.extend(_frevvo.inputView, _frevvo.baseSimpleView);
Object.extend(_frevvo.inputView, {
    $element: function(a) {
        return this.$childByClass(this.$childByClass(a, "facade"), "input");
    },
    $replaceableElements: function(b) {
        var a = [];
        a.push(this.$childByClass(b, "facade"));
        return a;
    },
    isPassword: function(a) {
        return this.$element(a).type == "password";
    },
    setPassword: function(a) {},
    unsetPassword: function(a) {},
    setMaxLength: function(b, a) {
        var c = this.$element(b);
        if (a > 0) {
            c && (c.maxLength = a);
        } else {
            c && (c.removeAttribute("maxLength"));
        }
    },
    getLiteral: function(a) {
        return "f-input";
    },
    is: function(a) {
        return (a.className && a.className.indexOf(this.getLiteral(a)) >= 0) && this.$element(a);
    }
});
_frevvo.textAreaView = {};
Object.extend(_frevvo.textAreaView, _frevvo.baseSimpleView);
Object.extend(_frevvo.textAreaView, {
    $element: function(a) {
        return this.$childByClass(this.$childByClass(a, "facade"), "textarea");
    },
    $replaceableElements: function(b) {
        var a = [];
        a.push(this.$childByClass(b, "facade"));
        return a;
    },
    getLiteral: function(a) {
        return "f-textarea";
    },
    is: function(a) {
        return (a.className && a.className.indexOf(this.getLiteral(a)) >= 0) && this.$element(a);
    },
    setRows: function(a, b) {
        var c = this.$element(a);
        c && (c.rows = b);
    },
    setCols: function(a, b) {
        var c = this.$element(a);
        c && (c.cols = b);
    },
    isEnabled: function(b) {
        try {
            var a = this.$element(b).getAttribute("readonly");
            if (a) {
                return !a;
            }
            return true;
        } catch (c) {
            return true;
        }
    },
    setEnabled: function(b, a) {
        if (a == "true") {
            this.$element(b) && (this.$element(b).removeAttribute("readonly"));
            Element.removeClassName(b, "s-disabled");
            this.$element(b).removeAttribute("aria-disabled");
        } else {
            this.$element(b) && (this.$element(b).setAttribute("readonly", "true"));
            Element.addClassName(b, "s-disabled");
            this.$element(b).setAttribute("aria-disabled", "true");
        }
    }
});
_frevvo.richTextAreaView = {};
Object.extend(_frevvo.richTextAreaView, _frevvo.baseSimpleView);
Object.extend(_frevvo.richTextAreaView, {
    $element: function(c) {
        var b = this.$childrenByName(c, "div");
        for (var a = 0; a < b.length; a++) {
            if (!Element.hasClassName(b[a], "f-helpcontent")) {
                return this.$childByName(b[a], "input");
            }
        }
        return null;
    },
    getEditorInstance: function(b) {
        var a = this.getControlId(this.$element(b));
        return FCKeditorAPI.GetInstance(a);
    },
    getValue: function(a) {
        var b = this.getEditorInstance(a);
        if (b) {
            return b.GetHTML();
        }
    },
    observeValueChange: function(a, b) {
        var c = this.getEditorInstance(a);
        if (c) {
            c.Events.AttachEvent("OnBlur", b);
        }
    },
    getLiteral: function(a) {
        return "f-richtextarea";
    },
    is: function(a) {
        return (a.className && a.className.indexOf(this.getLiteral(a)) >= 0) && this.$element(a);
    }
});
_frevvo.outputView = {};
Object.extend(_frevvo.outputView, _frevvo.baseSimpleView);
Object.extend(_frevvo.outputView, {
    $message: function(a) {
        return this.$childByName(this.$childByClass(a, "facade"), "span");
    },
    $facade: function(a) {
        return [this.$childByClass(a, "facade")];
    },
    $element: function(a) {
        return null;
    },
    getAside: function(a) {
        return _frevvo.protoView.$childByName(_frevvo.protoView.$childByClass(a, "facade"), "aside");
    },
    setBackgroundColor: function(c, a) {
        var b = this.$facade(c);
        Element.setStyle(this.$facade(c)[0], $H({
            "background-color": a
        }));
    },
    setBorderWidth: function(b, a) {
        Element.setStyle(b, $H({
            "border-width": a
        }));
    },
    setBorderColor: function(b, a) {
        Element.setStyle(b, $H({
            "border-color": a
        }));
    },
    setBorderStyle: function(b, a) {
        Element.setStyle(b, $H({
            "border-style": a
        }));
    },
    setLabelSize: function(b, a) {
        this.$message(b) && Element.setStyle(this.$message(b), $H({
            "font-size": a
        }));
    },
    setLabelColor: function(b, a) {
        this.$message(b) && Element.setStyle(this.$message(b), $H({
            color: a
        }));
    },
    setLabelWidth: function(b, a) {
        this.$message(b) && Element.setStyle(this.$message(b), $H({
            width: a
        }));
    },
    setLabelBold: function(b, a) {
        if (!this.$message(b)) {
            return;
        }
        if (a == "true") {
            Element.setStyle(this.$message(b), $H({
                "font-weight": "bold"
            }));
        } else {
            Element.setStyle(this.$message(b), $H({
                "font-weight": ""
            }));
        }
    },
    setLabelItalic: function(b, a) {
        if (!this.$message(b)) {
            return;
        }
        if (a == "true") {
            Element.setStyle(this.$message(b), $H({
                "font-style": "italic"
            }));
        } else {
            Element.setStyle(this.$message(b), $H({
                "font-style": ""
            }));
        }
    },
    setCenter: function(b, a) {
        if (!this.$message(b)) {
            return;
        }
        if (a == "true") {
            Element.setStyle(this.$message(b), $H({
                "text-align": "center"
            }));
        } else {
            Element.setStyle(this.$message(b), $H({
                "text-align": ""
            }));
        }
    },
    setValue: function(b, a) {
        try {
            this.$message(b).innerHTML = a[0];
        } catch (c) {}
    },
    getValue: function(a) {
        try {
            return this.$message(a).innerHTML;
        } catch (b) {
            return null;
        }
    },
    isChangeable: function() {
        return false;
    },
    getLiteral: function(a) {
        return "f-output";
    },
    is: function(a) {
        return (a.className && a.className.indexOf(this.getLiteral(a)) >= 0) && a.className.indexOf("Message") >= 0;
    }
});
_frevvo.linkView = {};
Object.extend(_frevvo.linkView, _frevvo.baseSimpleView);
Object.extend(_frevvo.linkView, {
    $link: function(a) {
        return this.$childByName(this.$childByClass(this.$childByClass(a, "facade"), "f-link"), "a");
    },
    $label: function(a) {
        return this.$link(a);
    },
    $element: function(a) {
        return null;
    },
    $focusElement: function(a) {
        return this.$link(a);
    },
    setLabelColor: function(b, a) {
        this.$label(b) && Element.setStyle(this.$label(b), $H({
            color: a
        }));
    },
    setValue: function(b, a) {
        if (a) {
            this.$link(b).setAttribute("href", a[0]);
        }
    },
    getValue: function(a) {
        return this.$link(a).getAttribute("href");
    },
    isChangeable: function() {
        return false;
    },
    getLiteral: function(a) {
        return "f-output";
    },
    is: function(a) {
        return (a.className && a.className.indexOf(this.getLiteral(a)) >= 0) && ((a.className.indexOf("Link") >= 0) && (a.className.indexOf("LinkedFormViewer") < 0));
    }
});
_frevvo.linkedFormViewerView = {};
Object.extend(_frevvo.linkedFormViewerView, _frevvo.baseSimpleView);
Object.extend(_frevvo.linkedFormViewerView, {
    $element: function(a) {
        return this.$childByClass(this.$childByName(this.$childByClass(a, "facade"), "span"), "f-a-form-viewer");
    },
    $label: function(a) {
        return this.$element(a);
    },
    $focusElement: function(a) {
        return this.$childByClass(a, "facade");
    },
    setValue: function(b, a) {
        return;
        var d = this.$element(b);
        d && (d.innerHTML = a);
        if (a == null || a.length == 0) {
            Element.addClassName(this.$element(b), "hide");
            var c = this.$childByClass(b, "f-no-form-viewer");
            Element.removeClassName(c, "hide");
            Element.addClassName(c, "show");
        }
    },
    getValue: function(a) {
        return null;
    },
    setHeight: function(b, a) {},
    setGeneratedFormUri: function(b, a) {
        var c = this.$childByClass(b, "f-no-form-viewer");
        if (a != null && a.length > 0) {
            Element.removeClassName(this.$element(b), "hide");
            Element.removeClassName(c, "show");
            Element.addClassName(c, "hide");
        } else {
            Element.addClassName(this.$element(b), "hide");
            Element.removeClassName(c, "hide");
            Element.addClassName(c, "show");
        }
    },
    isChangeable: function() {
        return false;
    },
    getLiteral: function(a) {
        return "f-output";
    },
    is: function(a) {
        return (a.className && a.className.indexOf(this.getLiteral(a)) >= 0) && a.className.indexOf("LinkedFormViewer") >= 0;
    },
    setUseDefaultDecorator: function(c, b, a) {}
});
_frevvo.videoView = {};
Object.extend(_frevvo.videoView, _frevvo.baseSimpleView);
Object.extend(_frevvo.videoView, {
    $element: function(a) {
        return this.$childByName(this.$childByName(a, "span"), "embed");
    },
    setValue: function(b, a) {},
    getValue: function(a) {
        return null;
    },
    isChangeable: function() {
        return false;
    },
    getLiteral: function(a) {
        return "f-output";
    },
    is: function(a) {
        return (a.className && a.className.indexOf(this.getLiteral(a)) >= 0) && a.className.indexOf("Video") >= 0;
    }
});
_frevvo.imageView = {};
Object.extend(_frevvo.imageView, _frevvo.baseSimpleView);
Object.extend(_frevvo.imageView, {
    $element: function(a) {
        return this.$childByName(this.$childByName(a, "span"), "img");
    },
    setValue: function(b, a) {},
    getValue: function(a) {
        return null;
    },
    isChangeable: function() {
        return false;
    },
    getLiteral: function(a) {
        return "f-output";
    },
    is: function(a) {
        return (a.className && a.className.indexOf(this.getLiteral(a)) >= 0) && a.className.indexOf("Image") >= 0;
    }
});
_frevvo.triggerView = {};
Object.extend(_frevvo.triggerView, _frevvo.baseSimpleView);
Object.extend(_frevvo.triggerView, {
    $element: function(a) {
        return this.$childByName(this.$childByClass(a, "facade"), "input");
    },
    getState: function(a) {
        var b = {
            cssClass: this.getCSSClass(a),
            extId: this.getExtId(a),
            label: this.getLabel(a),
            clicked: true
        };
        return b;
    },
    setLabel: function(b, a) {
        this.$element(b).value = a;
    },
    getLabel: function(a) {
        return this.$element(a).value;
    },
    setLabelSize: function(b, a) {
        Element.setStyle(this.$element(b), $H({
            "font-size": a
        }));
    },
    setLabelColor: function(b, a) {
        Element.setStyle(this.$element(b), $H({
            color: a
        }));
    },
    setLabelBold: function(b, a) {
        if (a == "true") {
            Element.setStyle(this.$element(b), $H({
                "font-weight": "bold"
            }));
        } else {
            Element.setStyle(this.$element(b), $H({
                "font-weight": ""
            }));
        }
    },
    setLabelItalic: function(b, a) {
        if (a == "true") {
            Element.setStyle(this.$element(b), $H({
                "font-style": "italic"
            }));
        } else {
            Element.setStyle(this.$element(b), $H({
                "font-style": ""
            }));
        }
    },
    isChangeable: function() {
        return false;
    },
    observeActivation: function(a, b) {
        FEvent.observe(this.$element(a), "click", b);
        FEvent.observe(this.$element(a), "keydown", b);
    },
    setValue: function(b, a) {},
    getValue: function(a) {
        return null;
    },
    getLiteral: function(a) {
        return "f-trigger";
    },
    is: function(a) {
        return (a.className && a.className.indexOf(this.getLiteral(a)) >= 0);
    }
});
_frevvo.signatureView = {};
Object.extend(_frevvo.signatureView, _frevvo.baseSimpleView);
Object.extend(_frevvo.signatureView, {
    $element: function(a) {
        return this.$childByClass(a, "e-signature");
    },
    $signatureX: function(a) {
        return this.$childByClass(this.$signatureHolder(a), "e-signature-x");
    },
    $signatureHolder: function(a) {
        return this.$childByClass(this.$childByClass(a, "e-signature"), "e-signature-holder");
    },
    $signatureImg: function(a) {
        return this.$childByName(this.$childByClass(this.$signatureHolder(a), "e-signature-line"), "img");
    },
    $signatureWarning: function(a) {
        return this.$childByClass(this.$childByClass(a, "e-signature"), "e-signature-warning");
    },
    getLiteral: function(a) {
        return "f-signature";
    },
    is: function(a) {
        return (a.className && a.className.indexOf(this.getLiteral(a)) >= 0) && this.$element(a);
    },
    setControlWidth: function(b, a) {
        this.$elements && this.$elements(b) && _frevvo.baseView.setStyle(this.$elements(b), $H({
            width: a
        }));
        this.$signatureImg(b).width = a;
    },
    getValue: function(a) {
        return null;
    },
    setValue: function(b, a) {
        if (!a) {
            return;
        }
        var c = null;
        if (a.length > 0) {
            c = _frevvo.forms.formUrl() + "/document/" + a;
        }
        this.setSignatureImg(b, c);
    },
    setSignatureImg: function(b, c) {
        var a = this.$signatureImg(b);
        if (c != null && c.length > 0) {
            a.src = c;
            Element.removeClassName(a, "show");
            Element.removeClassName(a, "hide");
            if (this.isEditing(b)) {
                Element.addClassName(a, "hide");
            } else {
                Element.addClassName(a, "show");
            }
        } else {
            a.removeAttribute("src");
            Element.removeClassName(a, "show");
            Element.removeClassName(a, "hide");
            Element.addClassName(a, "hide");
        }
    },
    getState: function(b) {
        var c = {};
        var a = this.$signatureImg(b);
        if (a.src.indexOf("data:image/png;base64,") >= 0) {
            c.signatureData = a.src.substr(22);
        } else {
            if (a.src == null || a.src.length == 0) {
                c.signatureData = "";
            }
        }
        return c;
    }
});
_frevvo.uploadView = {};
Object.extend(_frevvo.uploadView, _frevvo.baseSimpleView);
Object.extend(_frevvo.uploadView, {
    $element: function(a) {
        return this.$childByClass(a, "a-put-file");
    },
    $fileList: function(a) {
        return this.$childByClass(a, "f-upload-file-list");
    },
    getState: function(c) {
        var a = [];
        var h = this.$fileList(c);
        if (h) {
            var g = _frevvo.protoView.$childrenByName(h, "DIV");
            for (var f = 0; f < g.length; f++) {
                var d = _frevvo.protoView.$childrenByName(g[f], "DIV");
                for (var e = 0; e < d.length; e++) {
                    var k = _frevvo.protoView.$childByName(d[e], "a");
                    var b = k.getAttribute("id");
                    b && (a.push(b));
                }
            }
        }
        return {
            cssClass: this.getCSSClass(c),
            extId: this.getExtId(c),
            label: this.getLabel(c),
            attachments: a
        };
    },
    observeClick: function(a, b) {
        if (b) {
            FEvent.observe(this.$element(a), "click", b);
            FEvent.observe(this.$element(a), "keydown", b);
        }
    },
    setAttachments: function(d, a) {
        if (a.length > 0) {
            var b = this.$fileList(d);
            for (var c = 0; c < a.length; c++) {
                _frevvo.uploadController.click.addFileEntry(b, a[c].name, a[c].fullname, a[c].id, a[c].type, this.$element(d).getAttribute("tabindex"));
            }
        }
    },
    setEnabled: function(b, a) {
        if (a == "true" && !_frevvo.uberController.editMode) {
            this.$element(b) && Element.removeClassName(this.$element(b), "s-disabled");
            this.$fileList(b) && Element.removeClassName(this.$fileList(b), "s-disabled");
            this.$element(b).removeAttribute("aria-disabled");
        } else {
            this.$element(b) && Element.addClassName(this.$element(b), "s-disabled");
            this.$fileList(b) && Element.addClassName(this.$fileList(b), "s-disabled");
            this.$element(b).setAttribute("aria-disabled", "true");
        }
    },
    setMaxReached: function(b, a) {
        if (a == "true" && !_frevvo.uberController.editMode) {
            this.$element(b) && Element.addClassName(this.$element(b), "s-disabled");
        } else {
            this.$element(b) && Element.removeClassName(this.$element(b), "s-disabled");
        }
    },
    setValue: function(e, a) {
        if (!a) {
            return;
        }
        for (var d = 0; d < a.length; d++) {
            var f = a[d];
            var c = f.split("://");
            if (c.length <= 1) {
                continue;
            }
            var b = f.split("#")[0];
            b = b.split("?")[0];
            var g = b.split("/");
            _frevvo.uploadController.click.addFileEntry(fileList, b, null, f, null, this.$element(e).getAttribute("tabindex"));
        }
    },
    getValue: function(a) {
        return null;
    },
    getLiteral: function(a) {
        return "f-upload";
    },
    is: function(a) {
        return (a.className && a.className.indexOf(this.getLiteral(a)) >= 0);
    }
});
_frevvo.submitView = {};
Object.extend(_frevvo.submitView, _frevvo.baseSimpleView);
Object.extend(_frevvo.submitView, {
    submitted: false,
    actionStr: null,
    $element: function(a) {
        return this.$childByName(this.$childByClass(a, "facade"), "input");
    },
    setLabel: function(b, a) {
        this.$element(b).value = a;
    },
    getLabel: function(a) {
        return this.$element(a).value;
    },
    setBackgroundColor: function(b, a) {
        Element.setStyle(this.$element(b), $H({
            "background-color": a
        }));
    },
    setLabelSize: function(b, a) {
        Element.setStyle(this.$element(b), $H({
            "font-size": a
        }));
    },
    setLabelColor: function(b, a) {
        Element.setStyle(this.$element(b), $H({
            color: a
        }));
    },
    setLabelWidth: function(b, a) {},
    setLabelBold: function(b, a) {
        if (a == "true") {
            Element.setStyle(this.$element(b), $H({
                "font-weight": "bold"
            }));
        } else {
            Element.setStyle(this.$element(b), $H({
                "font-weight": ""
            }));
        }
    },
    setLabelItalic: function(b, a) {
        if (a == "true") {
            Element.setStyle(this.$element(b), $H({
                "font-style": "italic"
            }));
        } else {
            Element.setStyle(this.$element(b), $H({
                "font-style": ""
            }));
        }
    },
    setUseDefaultDecorator: function(c, b, a) {},
    setButtonType: function(b, a) {
        Element.classNames(b).each(function(c) {
            if (c.indexOf("s-color-") > -1) {
                Element.removeClassName(b, c);
                return;
            }
        });
        Element.addClassName(b, a);
    },
    onDrop: function(c, b, a) {
        if ("above" === a || "left" === a) {
            _frevvo.utilities.insertBefore(c, b);
        }
    },
    isChangeable: function() {
        return false;
    },
    isSubmitValid: function(c) {
        var b = [];
        var e = _frevvo.formView.$rootControl(_frevvo.formView.getForm());
        var a = _frevvo.groupView.$content(e);
        if (a) {
            b = this.$childrenByClass(a, "f-submit");
        } else {
            var f = _frevvo.switchControlView.$switchContents(e);
            b = this.$childrenByClass(f[f.length - 1], "f-submit");
        }
        for (var d = 0; d < b.length; d++) {
            if (this.getName(b[d]) == c) {
                return Element.hasClassName(b[d], "s-valid");
            }
        }
        return false;
    },
    observeClick: function(a) {
        FEvent.observe(this.$element(a), "click", this.onClick.bindAsObserver(this, a));
        FEvent.observe(this.$element(a), "keydown", this.onClick.bindAsObserver(this, a));
    },
    onClick: function(a, b) {
        if (_frevvo.uberController.event.isActivationEvent(a)) {
            if (a) {
                Event.stop(a);
            }
            if (this.submitted) {
                alert("Submitting form. Please wait ...");
            } else {
                var d = this.getForm();
                var c = this;
                if (Element.hasClassName(b, "s-submit")) {
                    setTimeout(function() {
                        c.doSubmit("Submit", _frevvo.submitView.getName(b), b);
                    }, 300);
                } else {
                    if (Element.hasClassName(b, "s-save")) {
                        this.doSubmit("Save", _frevvo.submitView.getName(b), b);
                    } else {
                        this.doSubmit("Cancel", _frevvo.submitView.getName(b), b);
                    }
                }
            }
        }
    },
    doSubmit: function(a, c, d, b) {
        if (!_frevvo.submitView.isSubmitValid(c ? c : "Submit")) {
            return false;
        }
        if (!_frevvo.submitView.isEnabled(d)) {
            return false;
        }
        if (a) {
            this.actionStr = a;
        }
        if (!_frevvo.utilities.mutex.checkCounter()) {
            window.setTimeout("_frevvo.submitView.doSubmit()", 2000);
        } else {
            var e = _frevvo.formView.getForm();
            if (_frevvo.formView.getChallenge(e) && (!Element.hasClassName(d, "s-cancel"))) {
                _frevvo.lightBoxView.showPage(_frevvo.forms.formUrl() + "/captcha", _frevvo.localeStrings.stopSpam, "s-captcha-lightbox", {
                    el: d,
                    absolutePosition: _frevvo.uberController.mobile ? false : true,
                    centerPosition: false,
                    refEl: _frevvo.uberController.mobile ? $("sw-nav-next") : _frevvo.submitView.$element(d),
                    cname: c,
                    doIt: _frevvo.submitController.captcha.challenge
                }, null, this.$element(d));
                return false;
            } else {
                if (Element.hasClassName(d, "s-cancel")) {
                    if (!confirm(_frevvo.localeStrings.areYouSure)) {
                        return false;
                    }
                }
                return this.submit(c, b, d);
            }
        }
    },
    submit: function(f, j, c) {
        _frevvo.submitView.submitted = true;
        var a = _frevvo.submitView.getForm();
        var e = _frevvo.protoView.$childByName(a, "input");
        var h = $("_referrer_url");
        h.value = _frevvo.utilities.windowUtil._referrer_url;
        var g = _frevvo.utilities.getQuery(window.location.href);
        var d = "?formAction=" + this.actionStr + (g ? "&" + g : "");
        if (f) {
            d += "&cname=" + f;
        }
        if (j) {
            var b = "";
            for (p in j) {
                if ("extend" == p || "toJSONString" == p) {
                    continue;
                }
                b += p + ":" + j[p];
            }
            d += "&_data={" + b + "}";
        }
        if ($("_frevvo-frameless-wrapper")) {
            _frevvo.utilities.ajaxRequest.send(_frevvo.forms.formUrl() + d + "&_frameless=true", {
                method: "post",
                onSuccess: function(k) {
                    _frevvo.submitView.submitted = false;
                    $("_frevvo-frameless-container").innerHTML = k.responseText;
                    _frevvo.execFramelessOnSubmitCallback();
                },
                onFailure: function() {
                    _frevvo.submitView.submitted = false;
                }
            });
        } else {
            a.action += d;
            a.submit();
        }
        if ("Submit" === f) {
            Element.addClassName($("page-form"), "s-submitted");
        } else {
            Element.addClassName($("page-form"), "s-cancelled");
        }
        if (c) {
            _frevvo.submitView.setEnabled(c, false);
            var i = _frevvo.protoView.$childByName(_frevvo.protoView.$childByClass(c, "facade"), "INPUT");
            if (i) {
                i.value = _frevvo.localeStrings.pleasewait;
            }
        }
        return true;
    },
    setValid: function(b, a) {
        _frevvo.baseSimpleView.setValid(b, a);
        if (a == "true") {
            if (this.isEnabled(b)) {
                this.$element(b).removeAttribute("aria-disabled");
            }
        } else {
            this.$element(b).setAttribute("aria-disabled", "true");
        }
    },
    isEnabled: function(a) {
        return !Element.hasClassName(a, "s-disabled");
    },
    setEnabled: function(b, a) {
        if (!_frevvo.submitView.isEditing(b)) {
            if (a == "true") {
                Element.removeClassName(b, "s-disabled");
                if (this.isValid(b)) {
                    this.$element(b).removeAttribute("aria-disabled");
                }
            } else {
                Element.addClassName(b, "s-disabled");
                this.$element(b).setAttribute("aria-disabled", "true");
            }
        }
    },
    getValue: function(a) {
        return null;
    },
    setValue: function(b, a) {},
    setExtId: function(b, a) {},
    getLiteral: function(a) {
        return "f-submit";
    },
    is: function(a) {
        return (a.className && a.className.indexOf(this.getLiteral(a)) >= 0);
    }
});
var SubmitView = _frevvo.submitView;
_frevvo.dropdownView = {};
Object.extend(_frevvo.dropdownView, _frevvo.baseSimpleView);
Object.extend(_frevvo.dropdownView, {
    $ul: function(a) {
        return this.$childByName(this.$childByClass(a, "facade"), "ul");
    },
    $element: function(a) {
        if (_frevvo.uberController.mobile) {
            return this.$childByName(this.$childByClass(a, "facade"), "select");
        } else {
            return this.$childByName(this.$childByClass(a, "facade"), "input");
        }
    },
    $elements: function(b) {
        if (this.$element && this.$element(b)) {
            var a = [];
            a[0] = this.$element(b);
            var c = this.$childByClass(b, "f-select-comment");
            if (c) {
                a[1] = c;
            }
            return a;
        }
        return null;
    },
    $caretHolder: function(a) {
        return this.$childByName(this.$childByClass(a, "facade"), "span");
    },
    $replaceableElements: function(b) {
        var a = [];
        a.push(this.$childByClass(b, "facade"));
        return a;
    },
    $comment: function(a) {
        return this.$childByClass(a, "f-select-comment");
    },
    setLabelColor: function(b, a) {
        this.$label(b) && Element.setStyle(this.$label(b).parentNode, $H({
            color: a
        }));
    },
    getLiteral: function(a) {
        return "f-select1";
    },
    is: function(a) {
        return (a.className && a.className.indexOf(this.getLiteral(a)) >= 0 && a.className.indexOf("Dropdown") >= 0) && this.$element(a);
    },
    setEnabled: function(b, a) {
        if (a == "true") {
            this.$element(b) && (this.$element(b).disabled = false);
            Element.removeClassName(b, "s-disabled");
        } else {
            this.$element(b) && (this.$element(b).disabled = true);
            Element.addClassName(b, "s-disabled");
        }
        var c = this.$comment(b);
        if (c) {
            c.disabled = a != "true";
        }
    },
    getValue: function(a) {
        if (_frevvo.uberController.mobile) {
            return this.$element(a).value;
        } else {
            return this.$element(a).getAttribute("ovalue");
        }
    },
    setValue: function(c, j, f) {
        if (!j) {
            var e = this.$element(c);
            e.value = "";
            if (!_frevvo.uberController.mobile) {
                e.removeAttribute("ovalue");
                var k = _frevvo.dropdownView.getOptions(c);
                for (var g = 0; g < k.length; g++) {
                    var h = _frevvo.protoView.$childByName(k[g], "A");
                    Element.removeClassName(h, "icon-ok");
                    Element.removeClassName(h, "selected");
                }
            }
            return;
        }
        if (_frevvo.uberController.mobile) {
            var b = this.$element(c);
            b.value = j[0];
        } else {
            var e = this.$element(c);
            var k = _frevvo.dropdownView.getOptions(c);
            if (j[0] && !f) {
                for (var g = 0; g < k.length; g++) {
                    var h = _frevvo.protoView.$childByName(k[g], "A");
                    if (j[0] === h.getAttribute("ovalue")) {
                        f = h.innerHTML;
                        break;
                    }
                }
            }
            if (j[0]) {
                var d = _frevvo.protoView.makeNode(f);
                e.value = d.nodeValue;
                e.setAttribute("ovalue", j[0]);
            } else {
                e.value = "";
                e.removeAttribute("ovalue");
            }
            for (var g = 0; g < k.length; g++) {
                var h = _frevvo.protoView.$childByName(k[g], "A");
                Element.removeClassName(h, "icon-ok");
                Element.removeClassName(h, "selected");
                if (j[0] === h.getAttribute("ovalue")) {
                    Element.addClassName(h, "icon-ok");
                    Element.addClassName(h, "selected");
                }
            }
        }
    },
    getOptions: function(a) {
        if (_frevvo.uberController.mobile) {
            return this.$element(a).options;
        } else {
            return _frevvo.protoView.$childrenByName(this.$ul(a), "LI");
        }
    },
    setOptions: function(h, j, e) {
        if (_frevvo.uberController.mobile) {
            var c = this.$element(h);
            for (var g = c.options.length; g > 0; g--) {
                c.options[g - 1] = null;
            }
            if (_frevvo.utilities.util.isArray(j)) {
                for (var g = 0; g < j.length; g++) {
                    c.options[g] = new Option(j[g], e[g]);
                }
            } else {
                c.options[0] = new Option(j, e);
            }
        } else {
            var f = this.$ul(h);
            _frevvo.utilities.removeAllChildren(f);
            if (_frevvo.utilities.util.isArray(j)) {
                for (var g = j.length - 1; g >= 0; g--) {
                    var b = document.createElement("LI");
                    _frevvo.utilities.insertFirst(b, f);
                    var d = document.createElement("A");
                    _frevvo.utilities.insertFirst(d, b);
                    d.innerHTML = j[g];
                    d.setAttribute("ovalue", e[g]);
                    FEvent.observe(d, "click", _frevvo.dropdownController.click.doIt.bindAsObserver(_frevvo.dropdownController, h, d));
                }
            }
        }
    },
    isCommentEnabled: function(a) {
        return a.className && a.className.indexOf("s-comment") >= 0;
    },
    setCommentEnabled: function(b, a) {
        if (a) {
            Element.addClassName(b, "s-comment");
        } else {
            Element.removeClassName(b, "s-comment");
        }
    },
    isLastSelected: function(e) {
        var d = this.getOptions(e);
        if (_frevvo.uberController.mobile) {
            return d && d.length > 0 && d[d.length - 1].selected;
        } else {
            if (d && d.length > 0) {
                var c = _frevvo.protoView.$childByName(d[d.length - 1], "A");
                var b = c.getAttribute("ovalue");
                var f = this.$element(e).getAttribute("ovalue");
                return b === f;
            }
        }
    },
    showComment: function(a) {
        var b = this.$comment(a);
        b && Element.show(b);
    },
    hideComment: function(a) {
        var b = this.$comment(a);
        b && Element.hide(b);
    },
    setCommentRows: function(a, b) {
        var d = this.$comment(a);
        d && (d.rows = b);
    },
    setCommentValue: function(a, b) {
        var c = this.$comment(a);
        if (c && this.isCommentEnabled(a)) {
            c.value = b || "";
        }
    },
    getCommentValue: function(a) {
        var b = this.$comment(a);
        if (b && this.isCommentEnabled(a) && this.isLastSelected(a)) {
            return b.value;
        }
        return null;
    },
    observeCommentValueChange: function(a, b) {
        FEvent.observe(this.$comment(a), "change", b);
    },
    onkeyup: function(n, c) {
        var k = this;
        var d = function(i) {
            return i.firstChild.getAttribute("ovalue") === k.getValue(c);
        };
        if (n.keyCode === 40) {
            if (n.altKey) {
                _frevvo.dropdownController.click.toggle(n, c);
            } else {
                var a = this.getOptions(c);
                var j = false;
                for (var h = 0; h < a.length; h++) {
                    if (d(a[h])) {
                        j = true;
                        if (h + 1 < a.length) {
                            var m = [];
                            m[0] = a[h + 1].firstChild.getAttribute("ovalue");
                            this.setValue(c, m);
                            _frevvo.uberController.valueChangeObserver(null, c);
                        }
                        break;
                    }
                }
                if (!j) {
                    var m = [];
                    m[0] = a[1].firstChild.getAttribute("ovalue");
                    this.setValue(c, m);
                    _frevvo.uberController.valueChangeObserver(null, c);
                }
            }
        } else {
            if (n.keyCode === 38) {
                if (n.altKey) {
                    _frevvo.dropdownController.click.toggle(n, c);
                } else {
                    var a = this.getOptions(c);
                    for (var h = a.length - 1; h >= 0; h--) {
                        if (d(a[h]) && h > 0) {
                            var m = [];
                            m[0] = a[h - 1].firstChild.getAttribute("ovalue");
                            this.setValue(c, m);
                            _frevvo.uberController.valueChangeObserver(null, c);
                            break;
                        }
                    }
                }
            } else {
                if (Element.hasClassName(c, "s-active") && (n.keyCode === 13 || n.keyCode === 27)) {
                    _frevvo.dropdownController.click.toggle(n, c);
                } else {
                    if ($(c.id + "-typeahead-timerid").value.length > 0) {
                        var g = parseInt($(c.id + "-typeahead-timerid").value, 10);
                        clearTimeout(g);
                    }
                    var q = this.getOptions(c);
                    var e = -1;
                    for (var h = 0; h < q.length; h++) {
                        if (d(q[h])) {
                            e = h;
                            break;
                        }
                    }
                    var b = [];
                    var l = [];
                    if (e != -1) {
                        for (var h = e; h < q.length; h++) {
                            l.push(h);
                            if (h != e) {
                                b.push(h);
                            }
                        }
                        for (var h = 0; h < e; h++) {
                            l.push(h);
                            b.push(h);
                        }
                    } else {
                        for (var h = 0; h < q.length; h++) {
                            l.push(h);
                            b.push(h);
                        }
                    }
                    var k = this;
                    var o = function(w, v) {
                        for (var u = 0; u < v.length; u++) {
                            var r = v[u];
                            var t = q[r].firstChild.innerHTML;
                            if (t && t.toUpperCase().indexOf(w) === 0) {
                                var s = [];
                                s[0] = q[r].firstChild.getAttribute("ovalue");
                                k.setValue(c, s);
                                _frevvo.uberController.valueChangeObserver(null, c);
                                q[r].parentNode.scrollTop = q[r].offsetTop;
                                return true;
                            }
                        }
                        return false;
                    };
                    var f = _frevvo.utilities.util.getCharFromKeyEvent(n);
                    if (f === "<") {
                        f = "&LT;";
                    }
                    if (f === ">") {
                        f = "&GT;";
                    }
                    if (f === "&") {
                        f = "&AMP;";
                    }
                    if (f === $(c.id + "-typeahead").value) {
                        o(f, b);
                    } else {
                        $(c.id + "-typeahead").value += f;
                        if ($(c.id + "-typeahead").value.length > 1) {
                            o($(c.id + "-typeahead").value, l);
                        } else {
                            o(f, b);
                        }
                    }
                    $(c.id + "-typeahead-timerid").value = setTimeout(function() {
                        if ($(c.id + "-typeahead")) {
                            $(c.id + "-typeahead").value = "";
                            $(c.id + "-typeahead-timerid").value = "";
                        }
                    }, 1000);
                }
            }
        }
    }
});
_frevvo.selectView = {};
Object.extend(_frevvo.selectView, _frevvo.baseSimpleView);
Object.extend(_frevvo.selectView, {
    $element: function(a) {
        return this.$childByName(a, "fieldset");
    },
    $elements: function(b) {
        if (this.$element && this.$element(b)) {
            var a = [];
            a[0] = this.$element(b);
            var c = this.$childByClass(b, "f-select-comment");
            if (c) {
                a[1] = c;
            }
            return a;
        }
        return null;
    },
    $options: function(d) {
        var a = this.$childrenByClass(this.$element(d), "f-select-item");
        var b = [];
        for (var c = 0; c < a.length; c++) {
            b.push(this.$childByName(a[c], "input"));
        }
        return b;
    },
    $checkCtrls: function(d) {
        var a = [];
        var b;
        var c;
        if (Element.hasClassName(d, "Radio")) {
            b = this.$childrenByClass(this.$element(d), "f-select-item");
            for (c = 0; c < b.length; c++) {
                a.push(b[0].firstChild);
            }
        } else {
            b = this.$labels(d);
            for (c = 0; c < b.length; c++) {
                a.push(this.$childByName(b[c], "span"));
            }
        }
        return a;
    },
    $labels: function(c) {
        var a = this.$childrenByClass(this.$element(c), "f-select-item");
        var d = [];
        for (var b = 0; b < a.length; b++) {
            d.push(this.$childByName(a[b], "label"));
        }
        return d;
    },
    $comment: function(a) {
        return this.$childByClass(a, "f-select-comment");
    },
    is: function(a) {
        return (a.className && a.className.indexOf("f-select") >= 0) && this.$element(a);
    },
    setHideItemLabel: function(b, a) {
        var c = this.$facade(b);
        if (c && c[0]) {
            if (a == "true") {
                Element.addClassName(c[0], "s-hide-item-label");
            } else {
                Element.removeClassName(c[0], "s-hide-item-label");
            }
        }
    },
    isEnabled: function(b) {
        try {
            if (Element.hasClassName(b, "s-disabled")) {
                return false;
            }
            var a = this.$options(b).disabled;
            if (a) {
                return !a;
            }
            return true;
        } catch (c) {
            return true;
        }
    },
    setItemWidth: function(d, b) {
        if (!d) {
            return;
        }
        var a = _frevvo.protoView.$childrenByName(_frevvo.protoView.$childByName(d, "fieldset"), "div");
        if (!a) {
            return;
        }
        for (var c = 0; c < a.length; c++) {
            Element.setStyle(a[c], $H({
                width: b
            }));
        }
    },
    setLabelWidth: function(d, b) {
        if (!d) {
            return;
        }
        var a = _frevvo.protoView.$childrenByName(_frevvo.protoView.$childByName(d, "fieldset"), "div");
        if (!a) {
            return;
        }
        for (var c = 0; c < a.length; c++) {
            if (b) {
                Element.setStyle(_frevvo.protoView.$childByName(a[c], "label"), $H({
                    width: b
                }));
            } else {
                Element.setStyle(_frevvo.protoView.$childByName(a[c], "label"), $H({
                    width: ""
                }));
            }
        }
    },
    setEnabled: function(c, a) {
        var f = this.$options(c);
        for (var b = 0; b < f.length; b++) {
            if (a == "true") {
                f[b].disabled = false;
            } else {
                f[b].disabled = true;
            }
        }
        var e = this.$comment(c);
        if (e) {
            e.disabled = !(a == "true");
        }
        if (a == "true") {
            Element.removeClassName(c, "s-disabled");
        } else {
            Element.addClassName(c, "s-disabled");
        }
        var d = this.$checkCtrls(c);
        for (var b = 0; b < d.length; b++) {
            if (a == "true") {
                d[b].removeAttribute("aria-disabled");
            } else {
                d[b].setAttribute("aria-disabled", "true");
            }
        }
        var e = this.$comment(c);
        if (a == "true") {
            e && e.removeAttribute("aria-disabled");
        } else {
            e && e.setAttribute("aria-disabled", "true");
        }
    },
    setValue: function(d, a) {
        var f = this.$options(d);
        for (var c = 0; c < f.length; c++) {
            f[c].checked = false;
        }
        if (a) {
            for (var b = 0; b < a.length; b++) {
                if (a[b]) {
                    var e = a[b].value || a[b];
                    for (var c = 0; c < f.length; c++) {
                        if (e == f[c].value) {
                            f[c].checked = true;
                            break;
                        }
                    }
                }
            }
        }
        this.checkSelectedItems(d);
        if (this.setTabIndex) {
            this.setTabIndex(d);
        }
    },
    checkSelectedItems: function(b) {
        var c = this.$options(b);
        for (var a = 0; a < c.length; a++) {
            if (c[a].checked) {
                Element.addClassName(c[a].parentNode, "s-checked");
                if (Element.hasClassName(b, "Checkbox")) {
                    c[a].nextSibling.firstChild.setAttribute("aria-checked", "true");
                }
            } else {
                Element.removeClassName(c[a].parentNode, "s-checked");
                if (Element.hasClassName(b, "Checkbox")) {
                    c[a].nextSibling.firstChild.setAttribute("aria-checked", "false");
                }
            }
        }
    },
    getValue: function(c) {
        var d = this.$options(c);
        var a = [];
        for (var b = 0; b < d.length; b++) {
            if (d[b].checked) {
                a.push(d[b].value);
            }
        }
        return a;
    },
    addChoices: function(e, h, b, c, g) {
        var a = this.$element(e);
        if (!this.haveChoicesChanged(a, h, b)) {
            return false;
        }
        _frevvo.utilities.removeAllChildren(a);
        if (h == null) {
            return;
        }
        var f = this.getControlId(e);
        if (_frevvo.utilities.util.isArray(h)) {
            for (var d = 0; d < h.length; d++) {
                this.addOneChoice(a, f, h[d], b[d], c, g);
            }
        } else {
            this.addOneChoice(a, f, h, b, c, g);
        }
        return true;
    },
    addOneChoice: function(l, m, c, e, i, b) {
        var n = document.createElement("div");
        var a = document.createElement("label");
        var g;
        var d;
        var k;
        Element.addClassName(n, "f-select-item");
        if (!i) {
            i = m;
        }
        var h = null;
        try {
            h = document.createElement('<input type="' + b + '" name="' + i + '" />');
        } catch (f) {
            h = document.createElement("input");
            h.setAttribute("type", b);
            h.setAttribute("name", i);
        }
        Element.addClassName(h, "input");
        h.id = _frevvo.utilities.util.makeId();
        h.value = e;
        a.htmlFor = h.id;
        if ("radio" === b) {
            a.innerHTML = c;
        } else {
            if ("checkbox" == b) {
                g = document.createElement("span");
                Element.addClassName(g, "radio-checkbox");
                a.appendChild(g);
                k = document.createElement("i");
                Element.addClassName(k, "fontawesome");
                Element.addClassName(k, "icon-ok");
                g.appendChild(k);
                d = document.createElement("span");
                Element.addClassName(d, "radio-checkbox-option");
                d.innerHTML = c;
                a.appendChild(d);
            }
        }
        n.appendChild(h);
        n.appendChild(a);
        var j = l.getAttribute("itemWidth");
        if (j) {
            Element.setStyle(n, $H({
                width: j
            }));
        }
        l.appendChild(n);
    },
    haveChoicesChanged: function(c, f, a) {
        var d = _frevvo.protoView.$childrenByName(c, "div");
        if (!d) {
            return false;
        }
        if (d.length != f.length) {
            return true;
        }
        for (var b = 0; b < d.length; b++) {
            var e = _frevvo.protoView.$childByName(d[b], "label");
            if (e.innerHTML != f[b]) {
                return true;
            }
        }
        return false;
    },
    observeValueChange: function(c, d) {
        var e = this.$labels(c);
        var a;
        var b;
        if (_frevvo.radioView.is(c)) {
            a = this.$options(c);
            for (b = 0; b < a.length; b++) {
                FEvent.observe(a[b], "change", d);
            }
        } else {
            for (b = 0; b < e.length; b++) {
                FEvent.stopObserving(e[b], "click");
                FEvent.observe(e[b], "click", d);
            }
        }
        this.observeCommentValueChange(c, d);
    },
    onValueChange: function(a) {
        Element.removeClassName(a, "s-error");
        if (this.setTabIndex) {
            this.setTabIndex(a);
        }
    },
    onValueChangeFailed: function(a) {
        if (!_frevvo.baseView.isValid(a) && this.getValue(a) && (this.getValue(a).length > 0)) {
            Element.addClassName(a, "s-error");
        } else {
            Element.removeClassName(a, "s-error");
        }
    },
    isCommentEnabled: function(a) {
        return a.className && a.className.indexOf("s-comment") >= 0;
    },
    setCommentEnabled: function(b, a) {
        if (a) {
            Element.addClassName(b, "s-comment");
        } else {
            Element.removeClassName(b, "s-comment");
        }
    },
    isLastSelected: function(b) {
        var a = this.$options(b);
        return a && a.length > 0 && a[a.length - 1].checked;
    },
    showComment: function(a) {
        var b = this.$comment(a);
        b && Element.show(b);
    },
    hideComment: function(a) {
        var b = this.$comment(a);
        b && Element.hide(b);
    },
    setCommentRows: function(a, b) {
        var d = this.$comment(a);
        d && (d.rows = b);
    },
    setCommentValue: function(a, b) {
        var c = this.$comment(a);
        if (c && this.isCommentEnabled(a)) {
            c.value = b || "";
        }
    },
    getCommentValue: function(a) {
        var b = this.$comment(a);
        if (b && this.isCommentEnabled(a) && this.isLastSelected(a)) {
            return b.value;
        }
        return null;
    },
    observeCommentValueChange: function(a, b) {
        FEvent.observe(this.$comment(a), "change", b);
    }
});
_frevvo.radioView = {};
Object.extend(_frevvo.radioView, _frevvo.selectView);
Object.extend(_frevvo.radioView, {
    getLiteral: function(a) {
        return "f-select1";
    },
    is: function(a) {
        return (a.className && a.className.indexOf(this.getLiteral(a)) >= 0 && a.className.indexOf("Radio") >= 0);
    },
    setOptions: function(c, f, a, b) {
        var d = null,
            e = null;
        if (_frevvo.utilities.util.isArray(f)) {
            if (f.length == 1) {
                return;
            }
            d = f.shift();
            e = a.shift();
        }
        if (this.addChoices(c, f, a, b, "radio")) {
            this.observeValueChange(c, _frevvo.uberController.valueChangeObserver.bindAsObserver(_frevvo.uberController, c));
        }
        if (d != null) {
            f.unshift(d);
        }
        if (e != null) {
            a.unshift(e);
        }
    },
    getValue: function(a) {
        var b;
        if (a.newSelectedOption) {
            b = a.newSelectedOption.value;
            delete a.newSelectedOption;
        } else {
            b = _frevvo.selectView.getValue(a);
        }
        return b;
    },
    onkeydown: function(a, e) {
        var f;
        var d = -1;
        var c;
        var b = [];
        if (a.keyCode >= 37 && a.keyCode <= 40) {
            Event.stop(a);
            f = this.$options(e);
            for (c = 0; c < f.length; c++) {
                if (f[c].value === this.getValue(e)[0]) {
                    d = c;
                    break;
                }
            }
            if (a.keyCode === 40 || a.keyCode === 39) {
                if (d === -1 || d === f.length - 1) {
                    d = 0;
                } else {
                    d += 1;
                }
            } else {
                if (a.keyCode === 38 || a.keyCode === 37) {
                    if (d === -1 || d === 0) {
                        d = f.length - 1;
                    } else {
                        d -= 1;
                    }
                }
            }
            if (d >= 0) {
                b[0] = f[d].value;
                this.setValue(e, b);
                f[d].focus();
                _frevvo.model.updateControlValue(e, _frevvo.uberController.valueChangeSuccess, _frevvo.uberController.valueChangeFailed);
            }
        }
    },
    observeBlur: function(c) {
        var b = this.$elements(c);
        var a;
        if (b) {
            for (a = 0; a < b.length; a++) {
                FEvent.observe(b[a], "blur", function(d, e) {
                    Element.removeClassName(e, "s-focus");
                    Element.removeClassName(e, "s-focus2");
                }.bindAsObserver(this, c));
            }
        }
    },
    setTabIndex: function(c) {
        var d = this.$options(c);
        var f = 0;
        var a = (this.isInTableControl(c)) ? "-1" : "0";
        for (var b = 0; b < d.length; b++) {
            if (d[b].hasAttribute("tabindex")) {
                a = d[b].getAttribute("tabindex");
            }
            d[b].removeAttribute("tabindex");
            if (d[b].checked) {
                f = b;
            }
        }
        if (this.isEnabled(c) && d.length > 0) {
            d[f].tabIndex = a;
            if (d.length - f === 1) {
                var e = document.getElementsByName(c.id + "_comment")[0];
                if (e) {
                    e.tabIndex = a;
                }
            }
        }
    },
    isRadioElement: function(b) {
        var a;
        if (b) {
            if (b.type && b.type === "radio") {
                return true;
            }
            a = b.getAttribute("for");
            if (a && $(a) && $(a).type === "radio") {
                return true;
            }
        }
        return false;
    }
});
_frevvo.checkboxView = {};
Object.extend(_frevvo.checkboxView, _frevvo.selectView);
Object.extend(_frevvo.checkboxView, {
    getLiteral: function(a) {
        return "f-select";
    },
    is: function(a) {
        return (a.className && a.className.indexOf(this.getLiteral(a)) >= 0 && a.className.indexOf("Checkbox") >= 0);
    },
    setOptions: function(c, f, a, b) {
        var d = null,
            e = null;
        if (_frevvo.utilities.util.isArray(f)) {
            if (f.length == 1) {
                return;
            }
            d = f.shift();
            e = a.shift();
        }
        if (Element.hasClassName(c, "BooleanCheckbox")) {
            if (this.addChoices(c, f[0], a[0], b, "checkbox")) {
                this.observeValueChange(c, _frevvo.uberController.valueChangeObserver.bindAsObserver(_frevvo.uberController, c));
            }
        } else {
            if (this.addChoices(c, f, a, b, "checkbox")) {
                this.observeValueChange(c, _frevvo.uberController.valueChangeObserver.bindAsObserver(_frevvo.uberController, c));
            }
        }
        if (d != null) {
            f.unshift(d);
        }
        if (e != null) {
            a.unshift(e);
        }
    },
    onkeyup: function(a, f) {
        if (a.keyCode === 32) {
            var g = this.$options(f);
            for (var e = 0; e < g.length; e++) {
                if (document.activeElement === g[e].nextSibling.firstChild) {
                    var c = this.getValue(f);
                    var b = [];
                    var h = c.indexOf(g[e].value);
                    if (h >= 0) {
                        for (var d = 0; d < c.length; d++) {
                            if (d != h) {
                                b.push(c[d]);
                            }
                        }
                    } else {
                        b = c;
                        b.push(g[e].value);
                    }
                    this.setValue(f, b);
                    _frevvo.model.updateControlValue(f, _frevvo.uberController.valueChangeSuccess, _frevvo.uberController.valueChangeFailed);
                    break;
                }
            }
        }
    },
    observeBlur: function(d) {
        var c = this.$elements(d);
        var b, a;
        if (c) {
            for (b = 0; b < c.length; b++) {
                FEvent.observe(c[b], "blur", function(e, f) {
                    Element.removeClassName(f, "s-focus");
                    Element.removeClassName(f, "s-focus2");
                    _frevvo.model.updateControlValue(f, _frevvo.uberController.valueChangeSuccess, _frevvo.uberController.valueChangeFailed);
                }.bindAsObserver(this, d));
                if (c[b].nodeName === "FIELDSET") {
                    a = Element.childrenWithClassName(c[b], "radio-checkbox");
                    if (a.length > 0) {
                        FEvent.observe(a[a.length - 1], "blur", function(e, f) {
                            _frevvo.model.updateControlValue(f, _frevvo.uberController.valueChangeSuccess, _frevvo.uberController.valueChangeFailed);
                        }.bindAsObserver(this, d));
                    }
                }
            }
        }
    },
    setTabIndex: function(e) {
        var f = this.$options(e);
        var c = this.isEnabled(e);
        var a;
        var b = (this.isInTableControl(e)) ? "-1" : "0";
        for (var d = 0; d < f.length; d++) {
            a = f[d].nextElementSibling.firstChild;
            if (c) {
                if (!a.hasAttribute("tabindex")) {
                    a.tabIndex = b;
                }
                if (f.length - d === 1) {
                    var g = document.getElementsByName(e.id + "_comment")[0];
                    if (g) {
                        if (!g.hasAttribute("tabindex")) {
                            g.tabIndex = b;
                        }
                    }
                }
            } else {
                a.removeAttribute("tabindex");
            }
        }
    }
});
_frevvo.groupView = {};
Object.extend(_frevvo.groupView, _frevvo.baseView);
Object.extend(_frevvo.groupView, {
    $header: function(a) {
        return this.$childByClass(a, "h-group");
    },
    $label: function(a) {
        return this.$childByName(this.$childByClass(this.$header(a), "f-label"), "label");
    },
    $status: function(a) {
        return this.$childByClass(this.$childByClass(this.$header(a), "f-status-positioner"), "f-status");
    },
    $helpContent: function(a) {
        return this.$childByClass(this.$childByClass(this.$header(a), "f-label"), "f-helpcontent");
    },
    $help: function(a) {
        return this.$childByClass(this.$childByClass(this.$header(a), "f-label"), "f-help");
    },
    $expand: function(a) {
        return this.$childByClass(this.$header(a), "f-expand");
    },
    $content: function(a) {
        return this.$childByClass(a, "c-group");
    },
    $signature: function(a) {
        return this.$childByClass(a, "c-signature");
    },
    $info: function(a) {
        return this.$childByClass(this.$content(a), "f-info");
    },
    $edit: function(a) {
        return this.$childByClass(this.$header(a), "e-edit");
    },
    $editRemove: function(a) {
        return this.$childByClass(this.$edit(a), "e-remove");
    },
    $palette: function(a) {
        return this.$childByClass(this.$edit(a), "e-palette");
    },
    $add: function(a) {
        return this.$childByClass(this.$header(a), "f-add");
    },
    $remove: function(a) {
        return this.$childByClass(this.$header(a), "f-remove");
    },
    $feedback: function(a) {
        return this.$childByClass(this.$edit(a), "e-feedback");
    },
    $draggable: function(a) {
        return a;
    },
    $droppable: function(a) {
        return this.$header(a);
    },
    $bottomDroppable: function(a) {
        return this.$childByClass(a, "b-droppable");
    },
    setRepeatId: function(a, b) {
        if (a) {
            this.$header(a).setAttribute("rcId", b);
        }
    },
    getRepeatId: function(a) {
        return this.$header(a).getAttribute("rcId");
    },
    removeRepeatId: function(c) {
        if (c) {
            this.$header(c).removeAttribute("rcId");
        }
        var a = this.getControls(c);
        if (a && a.length > 0) {
            for (var b = 0; b < a.length; b++) {
                _frevvo.baseView.getView(a[b]).removeRepeatId(a[b]);
            }
        }
    },
    isCustom: function(a) {
        return this.$header(a).getAttribute("custom") == "true";
    },
    setReadOnly: function(c, a) {
        if (a == "true") {
            var b = this.getForm();
            Element.addClassName(_frevvo.formView.getSaveButton(b), "s-disabled");
        } else {
            var b = this.getForm();
            Element.removeClassName(_frevvo.formView.getSaveButton(b), "s-disabled");
        }
    },
    getReadOnlyId: function(a) {
        if (a) {
            var b = this.$header(a);
            if (b) {
                return b.getAttribute("roId");
            } else {
                return null;
            }
        } else {
            return null;
        }
    },
    getLiteral: function(a) {
        return "f-group";
    },
    is: function(a) {
        return (a.className && a.className.indexOf(this.getLiteral(a)) >= 0);
    },
    isHeader: function(a) {
        return Element.hasClassName(a, "h-group") || Element.hasClassName(a, "h-panel") || Element.hasClassName(a, "h-section") || Element.hasClassName(a, "h-repeat");
    },
    isBottomDroppable: function(a) {
        return Element.hasClassName(a, "b-droppable");
    },
    isHeaderAtRoot: function(a) {
        a = a.parentNode.parentNode.parentNode;
        if (a) {
            return "root-control" == _frevvo.baseView.getControlId(a);
        }
        return false;
    },
    isComposite: function(a) {
        return true;
    },
    setHint: function(b, a) {
        try {
            this.$header(b).setAttribute("title", a);
        } catch (c) {}
    },
    getHint: function(a) {
        try {
            return this.$header(a).getAttribute("title");
        } catch (b) {
            return null;
        }
    },
    getWidth: function(a) {
        return null;
    },
    getMargin: function(a) {
        return null;
    },
    setEnabled: function(b, a) {},
    getTypeRequired: function(a) {
        return null;
    },
    setTypeRequired: function(a) {},
    setValid: function(b, a) {
        if (a == "true") {
            Element.removeClassName(b, "s-invalid");
            if (!this.isExpanded(b)) {
                Element.removeClassName(b, "s-collapsed-invalid");
            }
            Element.addClassName(b, "s-valid");
        } else {
            Element.removeClassName(b, "s-valid");
            Element.addClassName(b, "s-invalid");
            if (!this.isExpanded(b)) {
                Element.addClassName(b, "s-collapsed-invalid");
            }
        }
        if (this.getControlId(b) === "root-control") {
            _frevvo.switchControlController.manageNavBarSubmit(a);
        }
    },
    setValue: function(b, a) {},
    onValueChange: function(a) {},
    onValueChangeFailed: function(a) {},
    getState: function(a) {
        var b = {
            name: this.getName(a),
            cssClass: this.getCSSClass(a),
            extId: this.getExtId(a),
            visible: this.isVisible(a),
            enabled: this.isEnabled(a),
            label: this.getLabel(a),
            hint: this.getHint(a),
            help: _frevvo.helpContentView.$getContent(this.$helpContent(a)),
            required: this.getRequired(a),
            expanded: this.isExpanded(a)
        };
        this.getWidth(a) && (b.width = this.getWidth(a));
        this.getMargin(a) && (b.margin = this.getMargin(a));
        return b;
    },
    getControls: function(b) {
        var c = this.$content(b);
        if (c) {
            if (c.hasChildNodes()) {
                var a = this.$childrenByName(c, "DIV");
                if (a.length == 0) {
                    return null;
                } else {
                    return a;
                }
            } else {
                return null;
            }
        }
    },
    getControlIndex: function(b, c) {
        var a = this.getControls(b);
        return a.indexOf(c);
    },
    insertFirst: function(a, b) {
        var c = this.$content(b);
        if (c) {
            if (c.hasChildNodes()) {
                c.insertBefore(a, c.firstChild);
            } else {
                c.appendChild(a);
            }
            this.hideInfo(b);
        }
    },
    insertLast: function(a, b) {
        var c = this.$content(b);
        if (c) {
            c.appendChild(a);
        }
    },
    validateMinMax: function(d) {
        var a = this.$repeatItems(d);
        if (a && a.length > 0 && a.length < this.getMinOccurs(d)) {
            var c = this.getMinOccurs(d) - a.length;
            for (var b = 0; b < c; b++) {
                RepeatController.addRepeatItem(a[0], "addToMinLimit");
            }
        }
        if (a && a.length > 0 && a.length > this.getMaxOccurs(d)) {
            for (var b = (a.length - 1); b >= this.getMaxOccurs(d); b--) {
                if (a[b].parentNode) {
                    RepeatController.removeRepeatItem(a[b]);
                }
            }
        }
    },
    isExpanded: function(a) {
        return Element.hasClassName(a, "s-expanded");
    },
    setExpanded: function(b, a) {
        if (a == "true") {
            if (this.isExpanded(b)) {
                return;
            }
            this.expand(b);
            Element.show(this.$content(b));
            if (!b.isSetupChildren) {
                _frevvo.groupController.setupChildren(b);
                b.isSetupChildren = true;
            }
        } else {
            if (!this.isExpanded(b)) {
                return;
            }
            Element.hide(this.$content(b));
            this.collapse(b);
        }
    },
    collapse: function(a) {
        Element.removeClassName(a, "s-expanded");
        Element.addClassName(a, "s-collapsed");
        if (!this.isValid(a)) {
            Element.addClassName(a, "s-collapsed-invalid");
        }
        a.setAttribute("aria-expanded", "false");
        if (this.$expandButton) {
            var b = this.$expandButton(a);
            if (b) {
                b.setAttribute("aria-expanded", "false");
            }
        }
        if (this.$header) {
            var c = this.$header(a);
            if (c) {
                c.setAttribute("aria-expanded", "false");
            }
        }
    },
    expand: function(a) {
        Element.removeClassName(a, "s-collapsed");
        Element.removeClassName(a, "s-collapsed-invalid");
        Element.addClassName(a, "s-expanded");
        a.setAttribute("aria-expanded", "true");
        if (this.$expandButton) {
            var b = this.$expandButton(a);
            if (b) {
                b.setAttribute("aria-expanded", "true");
            }
        }
        if (this.$header) {
            var c = this.$header(a);
            if (c) {
                c.setAttribute("aria-expanded", "true");
            }
        }
    },
    onDrop: function(e, c, b) {
        if (_frevvo.baseView.isRepeatItem(e)) {
            this.onRepeatItemDrop(e, c, b);
        } else {
            var a = e.parentNode.parentNode;
            var d = e.parentNode;
            Element.remove(e);
            this.showInfo(a, d);
        }
        if ("above" === b || "left" === b) {
            _frevvo.utilities.insertBefore(e, c);
        } else {
            if ("below" === b || "right" === b) {
                _frevvo.utilities.insertAfter(e, c);
            } else {
                this.insertFirst(e, c);
                if (this.getRepeatId(c)) {
                    _frevvo.baseView.getView(e).setRepeatId(e, this.getRepeatId(c));
                }
            }
        }
    },
    observeExpandCollapse: function(a, b) {
        var c = this.$expand(a);
        if (c) {
            FEvent.observe(c, "click", b);
        }
    },
    onExpandCollapse: function(a) {
        if (this.$header) {
            var c = this.$header(a);
            if (Element.hasClassName(c, "s-not-expandable")) {
                return;
            }
        }
        if (this.isExpanded(a)) {
            Element.hide(this.$content(a));
            var b = this.$signature(a);
            if (b) {
                Element.hide(b);
            }
            this.collapse(a);
            _frevvo.model.updateControlState(a, null, null);
        } else {
            this.expand(a);
            _frevvo.model.updateControlState(a, null, null);
            Element.show(this.$content(a));
            if (this.isSignable && this.isSignable(a)) {
                var b = this.$signature(a);
                if (b) {
                    Element.show(b);
                }
            }
        }
    },
    observeHover: function(a) {
        var c = this.getControlId(a);
        var b = this.$header(a);
        FEvent.observe(b, "mouseover", this.onMouseOver.bindAsObserver(this, a));
        FEvent.observe(b, "mouseout", this.onMouseOut.bindAsObserver(this, a));
    },
    onMouseOver: function(a, b) {
        Event.stop(a);
        if (_frevvo.utilities.ignoreMouseEvent(a, this.$header(b))) {
            return;
        }
        Element.addClassName(b, "s-group-hover");
        Element.addClassName(this.$header(b), "s-hover");
    },
    onMouseOut: function(a, b) {
        Event.stop(a);
        if (_frevvo.utilities.ignoreMouseEvent(a, this.$header(b))) {
            return;
        }
        Element.removeClassName(b, "s-group-hover");
        Element.removeClassName(this.$header(b), "s-hover");
    },
    observeKbNavigation: function(a) {}
});
_frevvo.sectionView = {};
Object.extend(_frevvo.sectionView, _frevvo.groupView);
Object.extend(_frevvo.sectionView, {
    $header: function(a) {
        return this.$childByClass(a, "h-section");
    },
    $focusElement: function(a) {
        return this.$header(a);
    },
    $content: function(a) {
        return this.$childByClass(a, "c-section");
    },
    $expandButton: function(a) {
        return this.$childByClass(this.$header(a), "f-expand");
    },
    $repeatingIndexLabel: function(a) {
        return this.$childByClass(this.$header(a), "f-ri-label");
    },
    getLiteral: function(a) {
        return "f-section";
    },
    observeExpandCollapse: function(a, b) {
        var c = _frevvo.uberController.editMode ? this.$expand(a) : this.$header(a);
        if (c) {
            FEvent.observe(c, "click", b);
            FEvent.observe(c, "keydown", b);
        }
    },
    setEnabled: function(b, a) {
        if (a == "true") {
            Element.removeClassName(b, "s-disabled");
            Element.removeClassName(this.$signature(b), "s-disabled");
        } else {
            Element.addClassName(this.$signature(b), "s-disabled");
        }
    },
    isSignable: function(a) {
        return Element.hasClassName(a, "s-signable");
    },
    isWetSignable: function(a) {
        return Element.hasClassName(a, "s-wet-signable");
    },
    getSignName: function(a) {
        return _frevvo.protoView.$childByClass(this.$signature(a), "c-signature-name");
    },
    getSignDate: function(a) {
        return _frevvo.protoView.$childByClass(this.$signature(a), "c-signature-date");
    },
    getSignButton: function(a) {
        return _frevvo.protoView.$childByName(_frevvo.protoView.$childByClass(this.$signature(a), "c-signature-facade"), "INPUT");
    },
    getSignBadData: function(a) {
        return _frevvo.protoView.$childByClass(this.$signature(a), "c-signature-bad-data");
    },
    isSigned: function(a) {
        return a && Element.hasClassName(a, "s-signed");
    },
    setSigned: function(b, k, f, i, g, c) {
        if (k == "true") {
            Element.addClassName(b, "s-signed");
            var j = this.getSignButton(b);
            j && (j.value = j.getAttribute("editText"));
            if (c) {
                var h = this.getSignName(b);
                if (h) {
                    var a = _frevvo.protoView.$childByClass(h, "Name");
                    var e = _frevvo.protoView.$childByName(a, "IMG");
                    if (e == null) {
                        e = document.createElement("IMG");
                        e.setAttribute("alt", _frevvo.localeStrings.signatureImage);
                        a.appendChild(e);
                    }
                    e.src = c;
                }
            } else {
                if (f) {
                    var h = this.getSignName(b);
                    if (h) {
                        var a = _frevvo.protoView.$childByClass(h, "Name");
                        var l = f;
                        if (f.length > 25) {
                            l = f.substring(0, 22) + "...";
                        }
                        a && (a.innerHTML = l);
                        a && (a.title = f);
                    }
                }
            }
            if (i) {
                var h = this.getSignDate(b);
                if (h) {
                    var a = _frevvo.protoView.$childByName(h, "SPAN");
                    var d = i;
                    if (i.length > 25) {
                        d = i.substring(0, 22) + "...";
                    }
                    a && (a.innerHTML = d);
                    a && (a.title = i);
                }
            }
            if (g == "true") {
                var j = this.getSignButton(b);
                if (j) {
                    Element.hide(j.parentNode);
                }
            }
        } else {
            Element.removeClassName(b, "s-signed");
            var j = this.getSignButton(b);
            j && (j.value = j.getAttribute("signText"));
            var h = this.getSignName(b);
            if (h) {
                var a = _frevvo.protoView.$childByClass(h, "Name");
                a.innerHTML = "";
                a.title = "";
                var e = _frevvo.protoView.$childByName(a, "IMG");
                if (e != null) {
                    a.removeChild(e);
                }
            }
        }
    },
    setBadData: function(b, a) {
        var c = this.getSignBadData(b);
        if (c) {
            if (a == "true") {
                Element.addClassName(b, "s-bad-data");
                Element.show(c);
            } else {
                Element.removeClassName(b, "s-bad-data");
                Element.hide(c);
            }
        }
    },
    setSignable: function(c, a, b) {
        if (a == "true") {
            Element.removeClassName(c, "s-not-signable");
            Element.addClassName(c, "s-signable");
            var d = this.$signature(c);
            if (d) {
                Element.show(d);
            }
        } else {
            Element.removeClassName(c, "s-signable");
            Element.addClassName(c, "s-not-signable");
            var d = this.$signature(c);
            if (d) {
                Element.hide(d);
            }
        }
        if (b == "true") {
            Element.addClassName(c, "s-must-sign");
        } else {
            Element.removeClassName(c, "s-must-sign");
        }
    },
    setWetSignable: function(b, a) {
        if (a == "true") {
            Element.addClassName(b, "s-wet-signable");
        } else {
            Element.removeClassName(b, "s-wet-signable");
        }
    },
    setExpandable: function(c, a) {
        var b = this.$header(c);
        if (a == "true") {
            Element.removeClassName(b, "s-not-expandable");
        } else {
            Element.addClassName(b, "s-not-expandable");
        }
    },
    setAuthorized: function(b, a) {
        if (a == "false") {
            Element.addClassName(b, "s-not-authorized");
        } else {
            Element.removeClassName(b, "s-not-authorized");
        }
    },
    is: function(a) {
        return (a.className && a.className.indexOf(this.getLiteral(a)) >= 0);
    }
});
_frevvo.panelView = {};
Object.extend(_frevvo.panelView, _frevvo.groupView);
Object.extend(_frevvo.panelView, {
    $header: function(a) {
        return this.$childByClass(a, "h-panel");
    },
    $content: function(a) {
        return this.$childByClass(a, "c-panel");
    },
    getLiteral: function(a) {
        return "f-panel";
    },
    is: function(a) {
        return (a.className && a.className.indexOf(this.getLiteral(a)) >= 0);
    },
    isHeader: function(a) {
        return Element.hasClassName(a, "h-panel");
    },
    getWidth: function(b) {
        var a = Element.getStyle(b, "width");
        return (a ? a : null);
    },
    getMargin: function(a) {
        var b = Element.getStyle(a, "margin");
        return (b ? b : null);
    }
});
_frevvo.repeatView = {};
Object.extend(_frevvo.repeatView, _frevvo.groupView);
Object.extend(_frevvo.repeatView, {
    $header: function(a) {
        return this.$childByClass(a, "h-repeat");
    },
    $content: function(a) {
        return this.$childByClass(a, "c-repeat");
    },
    $replaceableElements: function(b) {
        var a = [];
        a.push(this.$childByClass(b, "h-repeat"));
        a.push(this.$childByClass(b, "c-repeat"));
        return a;
    },
    getMinOccurs: function(a) {
        return a.getAttribute("minOccurs");
    },
    setMinOccurs: function(b, a) {
        b.setAttribute("minOccurs", a);
    },
    getMaxOccurs: function(a) {
        return a.getAttribute("maxOccurs");
    },
    setMaxOccurs: function(b, a) {
        b.setAttribute("maxOccurs", a);
    },
    updateMinMaxOccurs: function(a, c) {
        var b = _frevvo.baseView.getRepeatControlForRepeatItem(a);
        if (b != null) {
            c.maxOccurs && _frevvo.repeatView.setMaxOccurs(b, c.maxOccurs);
            c.minOccurs && _frevvo.repeatView.setMinOccurs(b, c.minOccurs);
            RepeatController.setup(b);
            _frevvo.repeatView.validateMinMax(b);
        }
    },
    $repeatItems: function(d) {
        var b = this.$content(d);
        if (b) {
            var a = [];
            for (var c = 0; c < b.childNodes.length; c++) {
                if (b.childNodes[c].nodeName == "DIV") {
                    a.push(b.childNodes[c]);
                }
            }
            return a;
        }
    },
    getIndex: function(d, a) {
        var b = this.$repeatItems(d);
        if (!b || b.length == 0) {
            return -1;
        }
        for (var c = 0; c < b.length; c++) {
            if (a == b[c]) {
                return c;
            }
        }
        return -1;
    },
    getLiteral: function(a) {
        return "f-repeat";
    },
    is: function(a) {
        return (a.className && a.className.indexOf(this.getLiteral(a)) >= 0 && Element.hasClassName(a, "Repeat"));
    },
    isHeader: function(a) {
        return Element.hasClassName(a, "h-repeat");
    },
    isEnabled: function(a) {
        return !Element.hasClassName(a, "s-disabled");
    },
    setEnabled: function(b, a) {
        if (a == "true") {
            RepeatController.setupAddRepeatItemObservers(b);
            RepeatController.setupRemoveRepeatItemObservers(b);
            Element.removeClassName(b, "s-disabled");
        } else {
            RepeatController.removeAddRepeatItemObservers(b);
            RepeatController.removeRemoveRepeatItemObservers(b);
            Element.addClassName(b, "s-disabled");
        }
    },
    getState: function(a) {
        var b = {
            cssClass: this.getCSSClass(a),
            extId: this.getExtId(a),
            label: this.getLabel(a),
            hint: this.getHint(a),
            help: _frevvo.helpContentView.$getContent(this.$helpContent(a)),
            visible: this.isVisible(a),
            enabled: this.isEnabled(a),
            expanded: this.isExpanded(a),
            minOccurs: this.getMinOccurs(a),
            maxOccurs: this.getMaxOccurs(a)
        };
        return b;
    },
    allowDrop: function(a, b) {
        if (_frevvo.baseView.dropBefore) {
            return true;
        }
        if (_frevvo.baseView.dropBelow) {
            return true;
        }
        if (_frevvo.panelView.is(a) || _frevvo.imageView.is(a) || _frevvo.triggerView.is(a) || _frevvo.repeatView.is(a) || _frevvo.outputView.is(a) || _frevvo.linkView.is(a) || _frevvo.videoView.is(a) || _frevvo.switchControlView.is(a) || _frevvo.uploadView.is(a) || _frevvo.tableView.is(a) || _frevvo.linkedFormViewerView.is(a) || _frevvo.signatureView.is(a)) {
            return false;
        }
        var c = this.$repeatItems(b);
        if (_frevvo.baseView.isTyped(a)) {
            return false;
        }
        if (c.length == 0) {
            return true;
        }
        return false;
    },
    onDrop: function(f, d, c) {
        if (_frevvo.baseView.isRepeatItem(f)) {
            this.onRepeatItemDrop(f, d, c);
        } else {
            var b = f.parentNode.parentNode;
            var e = f.parentNode;
            Element.remove(f);
            this.showInfo(b, e);
        }
        if ("above" === c || "left" === c) {
            _frevvo.utilities.insertBefore(f, d);
        } else {
            if ("below" === c || "right" === c) {
                _frevvo.utilities.insertAfter(f, d);
            } else {
                var a = _frevvo.baseView.getView(f);
                this.insertFirst(f, d);
                this.putRcId(f, this.getControlId(d));
                a.removeEditContainer(f);
                RepeatController.setup(d);
            }
        }
    },
    putRcId: function(c, d) {
        _frevvo.baseView.getView(c).setRepeatId(c, d);
        var a = null;
        if (_frevvo.sectionView.is(c)) {
            a = _frevvo.sectionView.getControls(c);
        } else {
            if (_frevvo.repeatView.is(c)) {
                a = _frevvo.repeatView.getControls(c);
            } else {
                if (_frevvo.panelView.is(c)) {
                    a = _frevvo.panelView.getControls(c);
                } else {
                    if (_frevvo.switchControlView.is(c)) {
                        a = _frevvo.switchControlView.$switchCases(c);
                    } else {
                        if (_frevvo.switchCaseView.is(c)) {
                            a = _frevvo.switchCaseView.getControls(c);
                        }
                    }
                }
            }
        }
        if (a && a.length > 0) {
            for (var b = 0; b < a.length; b++) {
                this.putRcId(a[b], d);
            }
        }
    }
});
_frevvo.tableView = {};
Object.extend(_frevvo.tableView, _frevvo.groupView);
Object.extend(_frevvo.tableView, {
    $header: function(a) {
        return this.$childByClass(a, "h-table");
    },
    $content: function(a) {
        return this.$childByName(this.$childByName(this.$childByClass(a, "c-table"), "table"), "tbody");
    },
    $focusElement: function(a) {
        return this.$childByName(this.$childByClass(a, "c-table"), "table");
    },
    $status: function(a) {
        return _frevvo.protoView.$childByClass(_frevvo.protoView.$childByClass(_frevvo.protoView.$childByClass(a, "c-table"), "f-status-positioner"), "f-status");
    },
    $replaceableElements: function(b) {
        var a = [];
        a.push(this.$childByClass(b, "h-table"));
        a.push(this.$childByClass(b, "c-table"));
        return a;
    },
    $tablehead: function(a) {
        return this.$childByName(this.$childByName(this.$childByName(this.$childByClass(a, "c-table"), "table"), "thead"), "TR");
    },
    $repeatItems: function(d) {
        var b = this.$content(d);
        if (b) {
            var a = [];
            for (var c = 0; c < b.childNodes.length; c++) {
                if (b.childNodes[c].nodeName == "TR") {
                    a.push(b.childNodes[c]);
                }
            }
            return a;
        }
    },
    $firstRepeatItem: function(c) {
        var a = this.$content(c);
        if (a) {
            for (var b = 0; b < a.childNodes.length; b++) {
                if (a.childNodes[b].nodeName == "TR") {
                    return a.childNodes[b];
                }
            }
        }
        return null;
    },
    setControlWidth: function(d, e) {
        var b = e.split(" ");
        if (!b) {
            return;
        }
        var a = _frevvo.protoView.$childrenByClass(this.$tablehead(d), "data-column");
        for (var c = 0; c < a.length; c++) {
            if (b.length > c) {
                a[c].setAttribute("width", b[c]);
            } else {
                a[c].removeAttribute("width");
            }
        }
    },
    isEnabled: function(a) {
        return !Element.hasClassName(a, "s-disabled");
    },
    isHeader: function(a) {
        return Element.hasClassName(a, "h-table");
    },
    getMinOccurs: function(a) {
        return a.getAttribute("minOccurs");
    },
    setMinOccurs: function(b, a) {
        b.setAttribute("minOccurs", a);
    },
    getMaxOccurs: function(a) {
        return a.getAttribute("maxOccurs");
    },
    setMaxOccurs: function(b, a) {
        b.setAttribute("maxOccurs", a);
        this.setMaxEqualsMin(b);
    },
    setMaxEqualsMin: function(c) {
        var a = c.getAttribute("maxOccurs");
        var b = c.getAttribute("minOccurs");
        if (b === a) {
            Element.addClassName(c, "s-hide-addremove");
        } else {
            Element.removeClassName(c, "s-hide-addremove");
        }
    },
    updateMinMaxOccurs: function(b, c) {
        var a = _frevvo.baseView.getView(b);
        c.maxOccurs && _frevvo.tableView.setMaxOccurs(b, c.maxOccurs);
        c.minOccurs && _frevvo.tableView.setMinOccurs(b, c.minOccurs);
        if (a.isEnabled(b)) {
            RepeatController.setupAddRepeatItemObservers(b);
            RepeatController.setupRemoveRepeatItemObservers(b);
        } else {
            RepeatController.removeAddRepeatItemObservers(b);
            RepeatController.removeRemoveRepeatItemObservers(b);
        }
        _frevvo.tableView.validateMinMax(b);
    },
    getTable: function(b) {
        var a = _frevvo.tableRowView.getTableRow(b);
        if (!a) {
            return null;
        }
        return _frevvo.protoView.$parentByClass(a.parentNode, "f-table");
    },
    getControls: function(a) {
        return this.$repeatItems(a);
    },
    setEnabled: function(b, a) {
        if (a == "true") {
            RepeatController.setupAddRepeatItemObservers(b);
            RepeatController.setupRemoveRepeatItemObservers(b);
            Element.removeClassName(b, "s-disabled");
        } else {
            RepeatController.removeAddRepeatItemObservers(b);
            RepeatController.removeRemoveRepeatItemObservers(b);
            Element.addClassName(b, "s-disabled");
        }
    },
    getLiteral: function(a) {
        return "f-table";
    },
    is: function(a) {
        return (a && a.className && a.className.indexOf(this.getLiteral(a)) >= 0 && Element.hasClassName(a, "Table"));
    },
    observeKbNavigation: function(a) {
        FEvent.observe(a, "keydown", _frevvo.groupController.navigationObserver.bindAsObserver(_frevvo.groupController, a, null, this.$focusElement(a)));
    }
});
_frevvo.tableHeadView = {};
Object.extend(_frevvo.tableHeadView, _frevvo.groupView);
Object.extend(_frevvo.tableHeadView, {
    getColumnHeaders: function(a) {
        return this.$childrenByClass(a, "data-column");
    },
    getLastNonDataColumnHeader: function(b) {
        var a = this.$childrenByClass(b, "data-not-column");
        if (a) {
            return a[a.length - 1];
        }
    },
    getColumn: function(b, a) {
        if (!b) {
            return null;
        }
        return _frevvo.protoView.$childrenByName(b, "TH")[a];
    },
    setExpanded: function(b, a) {},
    insertFirst: function(a, b) {
        _frevvo.tableHeadView.makeTHAndInsert(a, _frevvo.tableHeadView.getLastNonDataColumnHeader(b));
    },
    makeTHAndInsert: function(d, b) {
        var c = document.createElement("TH");
        for (var a = (d.childNodes.length - 1); a > -1; a--) {
            _frevvo.utilities.insertFirst(d.removeChild(d.childNodes[a]), c);
        }
        Element.classNames(d).each(function(e) {
            Element.addClassName(c, e);
        });
        _frevvo.baseView.setControlId(c, _frevvo.baseView.getControlId(d));
        _frevvo.tableHeadColumnView.setHint(c, _frevvo.baseView.getHint(d));
        Element.hide(c);
        _frevvo.utilities.insertAfter(c, b);
        Element.show(c);
        _frevvo.tableHeadColumnController.setup(c);
        return c;
    },
    getLiteral: function(a) {
        return "f-table-head";
    },
    is: function(a) {
        return (a && a.className && a.className.indexOf(this.getLiteral(a)) >= 0);
    }
});
_frevvo.tableHeadColumnView = {};
Object.extend(_frevvo.tableHeadColumnView, _frevvo.baseSimpleView);
Object.extend(_frevvo.tableHeadColumnView, {
    $label: function(a) {
        return this.$childByName(this.$childByClass(a, "f-label"), "label");
    },
    $helpContent: function(a) {
        return this.$childByClass(this.$childByClass(a, "f-label"), "f-helpcontent");
    },
    $help: function(a) {
        return this.$childByClass(this.$childByClass(a, "f-label"), "f-help");
    },
    getColumnToLeft: function(b) {
        if (b.parentNode) {
            var c = this.$childrenByName(b.parentNode, "TH");
            var a = c.indexOf(b);
            if (a > 0) {
                return c[a - 1];
            }
        }
        return null;
    },
    getColumnToRight: function(b) {
        if (b.parentNode) {
            var c = this.$childrenByName(b.parentNode, "TH");
            var a = c.indexOf(b);
            if (a > 0 && c.length > a + 1) {
                return c[a + 1];
            }
        }
        return null;
    },
    getControlId: function(a) {
        return a.getAttribute(FrevvoConstants.CONTROL_ID);
    },
    getTypeRequired: function(a) {
        return null;
    },
    isRepeatItem: function(a) {
        return false;
    },
    onDrop: function(b, c, e, h) {
        if (_frevvo.tableHeadColumnView.is(b)) {
            var f = _frevvo.protoView.$parentByClass(c, "f-table-head");
            var g = _frevvo.tableHeadView.getColumnHeaders(f).indexOf(b);
            this.onDropRemove(b, c, e);
            if ("right" === e) {
                _frevvo.utilities.insertAfter(b, c);
            } else {
                _frevvo.utilities.insertBefore(b, c);
            }
            var k = _frevvo.tableView.$repeatItems(_frevvo.protoView.$parentByClass(c.parentNode, "Table"));
            for (var d = 0; d < k.length; d++) {
                var a = _frevvo.tableRowView.getDataColumn(k[d], g);
                var j;
                if ("right" === e) {
                    j = _frevvo.tableRowView.getDataColumn(k[d], g + 1);
                    Element.remove(a);
                    _frevvo.utilities.insertAfter(a, j);
                } else {
                    j = _frevvo.tableRowView.getDataColumn(k[d], g - 1);
                    Element.remove(a);
                    _frevvo.utilities.insertBefore(a, j);
                }
            }
        } else {
            this.onDropRemove(b, c, e);
            var l = h.controls;
            PaletteController.drop.handleTableDrop(c, l);
        }
    },
    setState: function(d, e) {
        if (!d) {
            return;
        }
        var b = _frevvo.tableHeadColumnView;
        if (e.label || e.label == "") {
            b.setLabel(d, e.label);
        }
        e.hidelabel && b.setHideLabel(d, e.hidelabel);
        e.visible && b.setVisible(d, e.visible);
        if (e.hint || e.hint == "") {
            b.setHint(d, e.hint);
        }
        if (e.help || e.help == "") {
            if (b.$helpContent) {
                var a = b.$helpContent(d);
                if (a) {
                    _frevvo.helpContentView.$setContent(a, e.help);
                    _frevvo.helpDisplayView.$setContent(e.help);
                }
            }
            if (b.$help) {
                var c = b.$help(d);
                if (e.help.length > 0) {
                    Element.addClassName(c, "s-help");
                } else {
                    Element.removeClassName(c, "s-help");
                }
            }
        }
        if (this.isEditing(d)) {
            e.width ? b.setWidth(d, e.width) : b.setWidth(d, "");
            e.height ? b.setHeight(d, e.height) : b.setHeight(d, "");
            b.setCSSClass(d, e.cssClass);
            e.backgroundColor ? b.setBackgroundColor(d, e.backgroundColor) : b.setBackgroundColor(d, "");
            e.labelSize ? b.setLabelSize(d, e.labelSize) : b.setLabelSize(d, "");
            e.labelColor ? b.setLabelColor(d, e.labelColor) : b.setLabelColor(d, "");
            e.labelWidth ? b.setLabelWidth(d, e.labelWidth) : b.setLabelWidth(d, "");
            e.labelBold && b.setLabelBold(d, e.labelBold);
            e.labelItalic && b.setLabelItalic(d, e.labelItalic);
            e.center && b.setCenter && b.setCenter(d, e.center);
            e.horizontal && b.setHorizontal && b.setHorizontal(d, e.horizontal);
        }
    },
    setHint: function(b, a) {
        if (b) {
            b.setAttribute("title", a);
        }
    },
    setLabel: function(b, a) {
        _frevvo.tableHeadColumnView.$label(b).innerHTML = a;
    },
    setVisible: function(b, a) {
        if (a == "true") {
            Element.removeClassName(b, "s-invisible");
        } else {
            Element.addClassName(b, "s-invisible");
        }
    },
    setRequired: function(b, a) {
        if (!b) {
            return;
        }
        if (a) {
            Element.addClassName(b, "s-required");
        } else {
            Element.removeClassName(b, "s-required");
        }
    },
    setHideLabel: function(b, a) {
        if (!b) {
            return;
        }
        if (a == "true") {
            Element.addClassName(b, "s-hidelabel");
        } else {
            Element.removeClassName(b, "s-hidelabel");
        }
    },
    getLiteral: function(a) {
        return "f-table-column-head";
    },
    is: function(a) {
        return (a && a.className && a.className.indexOf(this.getLiteral(a)) >= 0);
    }
});
_frevvo.tableRowView = {};
Object.extend(_frevvo.tableRowView, _frevvo.groupView);
Object.extend(_frevvo.tableRowView, {
    $add: function(a) {
        return this.$childByClass(this.$childByClass(a, "add-column"), "f-add");
    },
    $remove: function(a) {
        return this.$childByClass(this.$childByClass(a, "remove-column"), "f-remove");
    },
    $rowLabel: function(a) {
        return this.$childByName(this.$childByClass(a, "row-label-column"), "label");
    },
    isLinked: function(a) {
        return (Element.hasClassName(_frevvo.protoView.$parentByClass(a.parentNode, "f-table"), "s-linked"));
    },
    isExpanded: function(a) {
        return true;
    },
    setExpanded: function(b, a) {},
    getControls: function(d) {
        var a = [];
        var c = this.$childrenByName(d, "TD");
        for (var b = 0; b < c.length; b++) {
            var e = this.$childByName(c[b], "DIV");
            if (e) {
                a.push(e);
            }
        }
        return a;
    },
    getControl: function(c, a) {
        var b = this.$childrenByName(c, "TD");
        if (a >= b.length) {
            return null;
        }
        return this.$childByName(b[a], "DIV");
    },
    getTableRow: function(a) {
        return _frevvo.protoView.$parentByClass(a, _frevvo.tableRowView.getLiteral(null));
    },
    getDataColumn: function(c, b) {
        var a = _frevvo.protoView.$childrenByClass(c, "data-column");
        if (a) {
            return a[b];
        }
        return null;
    },
    getIndex: function(b, c) {
        if (!b || !c) {
            return -1;
        }
        var a = c.parentNode;
        return Array.indexOf(_frevvo.protoView.$childrenByName(b, "TD"), a);
    },
    getLastNonDataColumnCell: function(b) {
        var a = this.$childrenByClass(b, "data-not-column");
        if (a) {
            return a[a.length - 1];
        }
    },
    insertFirst: function(a, b) {
        _frevvo.tableRowView.makeTRAndInsert(a, _frevvo.tableRowView.getLastNonDataColumnCell(b));
    },
    makeTRAndInsert: function(b, c) {
        var a = document.createElement("TD");
        Element.addClassName(a, "data-column");
        Element.hide(a);
        _frevvo.utilities.insertFirst(b, a);
        _frevvo.utilities.insertAfter(a, c);
        Element.show(a);
        UberEditController.setupControl(b);
    },
    isIn: function(a) {
        return (a.parentNode && a.parentNode.parentNode && a.parentNode.parentNode.className.indexOf(this.getLiteral(a)) >= 0);
    },
    getLiteral: function(a) {
        return "f-table-row";
    },
    setDeletable: function(a, b) {
        var c = this.$childrenByClass(a, "remove-column");
        if (c && c.length === 1) {
            if (!b) {
                Element.addClassName(c[0], "s-disabled");
            } else {
                Element.removeClassName(c[0], "s-disabled");
            }
        }
    },
    is: function(a) {
        return (a && a.className && a.className.indexOf(this.getLiteral(a)) >= 0);
    },
    setRepeatItemNumber: function(b, c) {
        var a;
        a = this.$rowLabel(b);
        a.innerHTML = a.innerHTML.replace(/\d+/, c);
        a = this.$add(b).getAttribute("aria-label");
        this.$add(b).setAttribute("aria-label", a.replace(/\d+/, c));
        a = this.$remove(b).getAttribute("aria-label");
        this.$remove(b).setAttribute("aria-label", a.replace(/\d+/, c));
    }
});
_frevvo.dateView = {};
Object.extend(_frevvo.dateView, _frevvo.inputView);
Object.extend(_frevvo.dateView, {
    $elements: function(b) {
        var a = [];
        if (this.isDateShowing(b)) {
            a.push(this.$dateElement(b));
        }
        if (this.isTimeShowing(b)) {
            a.push(this.$timeElement(b));
        }
        return a;
    },
    $element: function(a) {
        if (this.isDateShowing(a)) {
            return this.$dateElement(a);
        } else {
            if (this.isTimeShowing(a)) {
                return this.$timeElement(a);
            }
        }
    },
    $dateElement: function(a) {
        return this.$childByName(this.$childByClass(a, "facade"), "input");
    },
    $timeElement: function(a) {
        return this.$childByName(this.$childrenByClass(a, "facade")[1], "input");
    },
    $calendarIcon: function(a) {
        return this.$childByClass(this.$childByClass(a, "facade"), "f-calendar");
    },
    $closeDatePicker: function(a) {
        _frevvo.datePicker.closeDatePicker();
    },
    isDateShowing: function(a) {
        return Element.hasClassName(a, "Date") || Element.hasClassName(a, "DateTime");
    },
    isTimeShowing: function(a) {
        return Element.hasClassName(a, "Time") || Element.hasClassName(a, "DateTime");
    },
    setPlaceholder: function(b, a) {
        try {
            this.$dateElement(b).setAttribute("placeholder", a);
            this.$timeElement(b).setAttribute("placeholder", a);
        } catch (c) {}
    },
    setUseDefaultDecorator: function(c, b, a) {
        if (Element.hasClassName(c, "DateTime")) {
            _frevvo.baseView.setUseDefaultDecorator(c, b, true);
        } else {
            _frevvo.baseView.setUseDefaultDecorator(c, b, false);
        }
    },
    setEnabled: function(b, a) {
        if (a == "true") {
            this.$dateElement(b) && (this.$dateElement(b).disabled = false);
            this.$timeElement(b) && (this.$timeElement(b).disabled = false);
            Element.removeClassName(b, "s-disabled");
            this.$dateElement(b) && (this.$dateElement(b).removeAttribute("aria-disabled"));
            this.$timeElement(b) && (this.$timeElement(b).removeAttribute("aria-disabled"));
            if (this.$calendarIcon(b)) {
                this.$calendarIcon(b).removeAttribute("aria-disabled");
                this.$calendarIcon(b).setAttribute("tabIndex", ((this.isInTableControl(b)) ? "-1" : "0"));
            }
        } else {
            this.$dateElement(b) && (this.$dateElement(b).disabled = true);
            this.$timeElement(b) && (this.$timeElement(b).disabled = true);
            Element.addClassName(b, "s-disabled");
            this.$dateElement(b) && (this.$dateElement(b).setAttribute("aria-disabled", "true"));
            this.$timeElement(b) && (this.$timeElement(b).setAttribute("aria-disabled", "true"));
            if (this.$calendarIcon(b)) {
                this.$calendarIcon(b).setAttribute("aria-disabled", "true");
                this.$calendarIcon(b).removeAttribute("tabIndex");
            }
        }
    },
    getValue: function(c) {
        var b = this.$elements(c);
        if (b.length == 1) {
            return b[0].value;
        } else {
            var a = [];
            a[0] = b[0].value;
            a[1] = b[1].value;
            return a;
        }
    },
    setValue: function(b, a) {
        _frevvo.baseSimpleView.setValue.call(this, b, a);
    },
    onValueChangeFailed: function(c) {
        var b = this.$elements(c);
        if (b.length == 1) {
            var d = this.getValue(c);
            if (d && d.length > 0) {
                Element.addClassName(c, "s-error");
            } else {
                Element.removeClassName(c, "s-error");
            }
        } else {
            if (!_frevvo.baseView.isValid(c)) {
                var a = this.getValue(c);
                if (a[0].length > 0 || a[1].length > 0) {
                    Element.addClassName(c, "s-error");
                }
            } else {
                Element.removeClassName(c, "s-error");
            }
        }
    },
    observeValueChange: function(a, b) {
        if (_frevvo.dateView.isDateShowing(a)) {
            if (_frevvo.utilities.agentUtil.isIOS()) {
                FEvent.observe(this.$dateElement(a), "blur", b);
            } else {
                FEvent.observe(this.$dateElement(a), "change", b);
            }
        }
        if (_frevvo.dateView.isTimeShowing(a)) {
            if (_frevvo.utilities.agentUtil.isIOS()) {
                FEvent.observe(this.$timeElement(a), "blur", b);
            } else {
                FEvent.observe(this.$timeElement(a), "change", b);
            }
        }
    },
    setShowPicker: function(b, a) {
        if (a == "true") {
            Element.removeClassName(b, "s-no-picker");
        } else {
            Element.addClassName(b, "s-no-picker");
        }
    },
    onDatePickerClose: function(a) {
        _frevvo.uberController.valueChangeObserver(null, _frevvo.protoView.$parentByClass(a, "f-control"));
        Form.Element.focus(a);
    },
    onDateClick: function(a, b) {
        this.showDatePicker(b);
    },
    showDatePicker: function(b) {
        if (!this.isEnabled(b)) {
            return;
        }
        try {
            _frevvo.datePicker.closeHook = this.onDatePickerClose;
            var a = _frevvo.datePicker.getDatePicker(b);
            _frevvo.utilities.absolutePositioner.display(_frevvo.dateView.$calendarIcon(b), a, $("form-container"), 10);
            _frevvo.datePicker.setFocusOnCurrentValue(a);
        } catch (c) {
            alert(c);
        }
    },
    observeClick: function(a) {
        a.datePickerClickHandlerFunction = this.onDateClick.bindAsObserver(this, a);
        FEvent.observe(this.$calendarIcon(a), "click", a.datePickerClickHandlerFunction);
    },
    getLiteral: function(a) {
        return "f-input";
    },
    is: function(a) {
        return (a.className && a.className.indexOf(this.getLiteral(a)) >= 0 && (a.className.indexOf("Date") >= 0 || a.className.indexOf("Time") >= 0));
    },
    datePickerKeyup: function(a, b) {
        if (a.keyCode === 32) {
            this.showDatePicker(b);
        }
    },
    observeKeyUp: function(a, b) {
        FEvent.observe(this.$element(a), "keyup", b);
        FEvent.observe(this.$calendarIcon(a), "keyup", this.datePickerKeyup.bindAsObserver(this, a));
    }
});
_frevvo.switchControlView = {};
Object.extend(_frevvo.switchControlView, _frevvo.baseView);
Object.extend(_frevvo.switchControlView, {
    $switchHeaderContainer: function(a) {
        return this.$childByClass(a, "h-switch");
    },
    $switchCaseContainer: function(a) {
        return this.$childByClass(a, "c-switch");
    },
    $editHeader: function(a) {
        return this.$childByClass(this.$switchHeaderContainer(a), "h-switch-edit");
    },
    $label: function(a) {
        return this.$childByName(this.$childByClass(this.$editHeader(a), "f-label"), "label");
    },
    $edit: function(a) {
        return this.$childByClass(a, "e-edit");
    },
    $editRemove: function(a) {
        return this.$childByClass(this.$edit(this.$switchHeaderContainer(a)), "e-remove");
    },
    $palette: function(a) {
        return this.$childByClass(this.$edit(this.$editHeader(a)), "e-palette");
    },
    $feedback: function(a) {
        return this.$childByClass(this.$edit(this.$switchHeaderContainer(a)), "e-feedback");
    },
    $draggable: function(a) {
        return a;
    },
    $droppable: function(a) {
        return this.$editHeader(a);
    },
    $bottomDroppable: function(a) {
        return this.$childByClass(a, "b-droppable");
    },
    isHeader: function(a) {
        return Element.hasClassName(a, "h-switch-edit");
    },
    isHeaderAtRoot: function(a) {
        if (this.isHeader(a)) {
            a = a.parentNode.parentNode.parentNode.parentNode;
            if (a) {
                return "root-control" == _frevvo.baseView.getControlId(a);
            }
        }
        return false;
    },
    observeHover: function(c, b, d) {
        var a = this.$editHeader(c);
        if (a) {
            FEvent.observe(a, "mouseover", this.onMouseOver.bindAsObserver(this, a));
            FEvent.observe(a, "mouseout", this.onMouseOut.bindAsObserver(this, a));
        }
    },
    $switchCases: function(c) {
        var a = [];
        var d = this.$switchHeaderContainer(c);
        for (var b = 0; b < d.childNodes.length; b++) {
            if ((d.childNodes[b].nodeName == "DIV") && (Element.hasClassName(d.childNodes[b], "h-case"))) {
                a.push(d.childNodes[b]);
            }
        }
        return a;
    },
    $switchContents: function(c) {
        var a = [];
        var d = this.$switchCaseContainer(c);
        for (var b = 0; b < d.childNodes.length; b++) {
            if ((d.childNodes[b].nodeName == "DIV") && (Element.hasClassName(d.childNodes[b], "c-case"))) {
                a.push(d.childNodes[b]);
            }
        }
        return a;
    },
    $selectedCase: function(c) {
        var a = _frevvo.switchControlView.$switchCases(c);
        for (var b = 0; b < a.length; b++) {
            if (Element.hasClassName(a[b], FrevvoConstants.SELECTED_CLASS)) {
                return a[b];
            }
        }
        return null;
    },
    setRequired: function(b, a) {},
    setEnabled: function(b, a) {},
    setControlWidth: function(d, a) {
        var b = this.$switchCases(d);
        if (b.length > 0) {
            for (var c = 0; c < b.length; c++) {
                Element.setStyle(b[c], $H({
                    width: a
                }));
            }
        }
    },
    setLabelSize: function(d, b) {
        var a = this.$switchCases(d);
        if (a.length > 0) {
            for (var c = 0; c < a.length; c++) {
                Element.setStyle(_frevvo.switchCaseView.$label(a[c]), $H({
                    "font-size": b
                }));
            }
        }
    },
    setLabelColor: function(d, b) {
        var a = this.$switchCases(d);
        if (a.length > 0) {
            for (var c = 0; c < a.length; c++) {
                Element.setStyle(_frevvo.switchCaseView.$label(a[c]), $H({
                    color: b
                }));
            }
        }
    },
    setLabelBold: function(e, b) {
        var a = this.$switchCases(e);
        var d = "bold";
        if (b != "true") {
            d = "";
        }
        if (a.length > 0) {
            for (var c = 0; c < a.length; c++) {
                Element.setStyle(_frevvo.switchCaseView.$label(a[c]), $H({
                    "font-weight": d
                }));
            }
        }
    },
    setLabelItalic: function(e, c) {
        var b = this.$switchCases(e);
        var a = "italic";
        if (c != "true") {
            a = "";
        }
        if (b.length > 0) {
            for (var d = 0; d < b.length; d++) {
                Element.setStyle(_frevvo.switchCaseView.$label(b[d]), $H({
                    "font-style": a
                }));
            }
        }
    },
    setValid: function(b, a) {
        _frevvo.baseSimpleView.setValid(b, a);
    },
    setNavIndicator: function(d, b) {
        if (!_frevvo.uberController.mobile) {
            return;
        }
        var a = d.getAttribute("id");
        var f = _frevvo.protoView.$childrenByClass($("sw-nav-indicators"), "sw-nav-indicator");
        if (f) {
            for (var c = 0; c < f.length; c++) {
                var e = f[c];
                if (a === e.getAttribute("sid")) {
                    if (b == "true") {
                        Element.removeClassName(e, "s-invalid");
                        Element.addClassName(e, "s-valid");
                    } else {
                        Element.removeClassName(e, "s-valid");
                        Element.addClassName(e, "s-invalid");
                    }
                }
            }
        }
    },
    setValue: function(b, a) {},
    setRepeatId: function(a, b) {
        if (a) {
            this.$editHeader(a).setAttribute("rcId", b);
        }
    },
    getRepeatId: function(a) {
        return this.$editHeader(a).getAttribute("rcId");
    },
    removeRepeatId: function(c) {
        if (c) {
            this.$editHeader(c).removeAttribute("rcId");
        }
        var a = this.$switchCases(c);
        if (a && a.length > 0) {
            for (var b = 0; b < a.length; b++) {
                _frevvo.baseView.getView(a[b]).removeRepeatId(a[b]);
            }
        }
    },
    removeHover: function(a) {
        Element.removeClassName(a, "s-hover");
    },
    getLiteral: function(a) {
        return "f-switch";
    },
    is: function(a) {
        return (a.className && a.className.indexOf(this.getLiteral(a)) >= 0);
    },
    hideMenu: function() {
        var b = $("sw-nav-buttons");
        var c = _frevvo.protoView.$childById(_frevvo.protoView.$childById(b, "sw-action-group"), "sw-action-menu");
        var a = _frevvo.protoView.$childById(_frevvo.protoView.$childById(b, "sw-action-group"), "sw-action-menu-btn");
        Element.addClassName(c, "s-invisible");
        if (a) {
            a.value = "\uf0d7";
        }
    },
    showHideMenu: function(b) {
        var c = _frevvo.protoView.$childById(_frevvo.protoView.$childById(_frevvo.protoView.$childById(b, "sw-nav-buttons"), "sw-action-group"), "sw-action-menu");
        var a = _frevvo.protoView.$childById(_frevvo.protoView.$childById(_frevvo.protoView.$childById(b, "sw-nav-buttons"), "sw-action-group"), "sw-action-menu-btn");
        console.log(a.value);
        if (Element.hasClassName(c, "s-invisible")) {
            Element.removeClassName(c, "s-invisible");
            a.value = "\uf0d8";
        } else {
            Element.addClassName(c, "s-invisible");
            a.value = "\uf0d7";
        }
    },
});
_frevvo.switchCaseView = {};
Object.extend(_frevvo.switchCaseView, _frevvo.groupView);
Object.extend(_frevvo.switchCaseView, {
    $header: function(a) {
        return a;
    },
    $focusElement: function(a) {
        return this.$header(a);
    },
    $content: function(g) {
        var b = this.$switchControl(g);
        if (b) {
            var a = _frevvo.baseView.getView(b);
            if (a) {
                var d = a.$switchCases(b);
                var f = this.getControlId(g);
                var c = -1;
                for (var e = 0; e < d.length; e++) {
                    if (f == _frevvo.baseView.getView(d[e]).getControlId(d[e])) {
                        c = e;
                        break;
                    }
                }
                var h = a.$switchContents(b);
                if (h.length > c) {
                    return h[c];
                }
            }
        }
        return null;
    },
    $switchControl: function(a) {
        return a.parentNode.parentNode;
    },
    $selectedCase: function(e) {
        var b = this.$switchControl(e);
        var a = _frevvo.baseView.getView(b);
        var c = a.$switchCases(b);
        for (var d = 0; d < c.length; d++) {
            if (Element.hasClassName(c[d], FrevvoConstants.SELECTED_CLASS)) {
                return c[d];
            }
        }
        return null;
    },
    getLiteral: function(a) {
        return "h-case";
    },
    is: function(a) {
        return (a.className && a.className.indexOf(this.getLiteral(a)) >= 0);
    },
    isAtRoot: function(a) {
        if (this.is(a)) {
            a = a.parentNode.parentNode.parentNode.parentNode;
            if (a) {
                return "root-control" == _frevvo.baseView.getControlId(a);
            }
        }
        return false;
    },
    isContent: function(a) {
        return (a.className && a.className.indexOf("c-case") >= 0);
    },
    getFirstCase: function(d) {
        var b = this.$switchControl(d);
        var a = _frevvo.baseView.getView(b);
        var c = a.$switchCases(b);
        if (c.length > 0) {
            return c[0];
        } else {
            return null;
        }
    },
    getControlId: function(a) {
        return this.$header(a).getAttribute(FrevvoConstants.CONTROL_ID);
    },
    setControlId: function(b, a) {
        this.$header(b).setAttribute(FrevvoConstants.CONTROL_ID, a);
    },
    getState: function(a) {
        var b = _frevvo.groupView.getState(a);
        b.selected = this.isSelected(a);
        return b;
    },
    isSelected: function(a) {
        return Element.hasClassName(a, FrevvoConstants.SELECTED_CLASS);
    },
    select: function(c) {
        var d = _frevvo.switchCaseView.$switchControl(c);
        if (d) {
            var a = _frevvo.switchControlView.$switchCases(d);
            for (var b = 0; b < a.length; b++) {
                Element.removeClassName(a[b], FrevvoConstants.SELECTED_CLASS);
                Element.addClassName(a[b], FrevvoConstants.NOT_SELECTED_CLASS);
                _frevvo.switchCaseView.hideContent(a[b]);
                a[b].setAttribute("aria-expanded", "false");
            }
        }
        Element.removeClassName(c, FrevvoConstants.NOT_SELECTED_CLASS);
        Element.addClassName(c, FrevvoConstants.SELECTED_CLASS);
        c.setAttribute("aria-expanded", "true");
        Element.removeClassName(c, FrevvoConstants.INVALID_UNSELECTED_CLASS);
        this.showContent(c);
    },
    unSelect: function(a) {
        Element.removeClassName(a, FrevvoConstants.SELECTED_CLASS);
        Element.addClassName(a, FrevvoConstants.NOT_SELECTED_CLASS);
        if (!this.isValid(a)) {
            Element.addClassName(a, FrevvoConstants.INVALID_UNSELECTED_CLASS);
        }
        this.hideContent(a);
    },
    showContent: function(a) {
        Element.show(this.$content(a));
    },
    hideContent: function(a) {
        Element.hide(this.$content(a));
    },
    removeCase: function(b) {
        var a = _frevvo.baseView.getView(b);
        var c = a.$content(b);
        Element.remove(b);
        Element.remove(c);
    },
    onRemove: function(c, e) {
        if (e) {
            var d = this.$switchControl(c);
            this.removeCase(c);
            var a = _frevvo.baseView.getView(d);
            var b = a.$switchCases(d);
            if (b.length == 0) {
                Element.remove(d);
            }
        }
    },
    onDrop: function(c, e, g) {
        if (_frevvo.switchCaseView.is(c)) {
            var h = _frevvo.baseView.getView(e);
            var d = h.$content(c);
            var f = h.$content(e);
            this.removeCase(c);
            _frevvo.utilities.insertAfter(c, e);
            _frevvo.utilities.insertAfter(d, f);
            if (Element.hasClassName(c, "s-first-tab")) {
                Element.removeClassName(c, "s-first-tab");
                Element.addClassName(_frevvo.switchCaseView.getFirstCase(e), "s-first-tab");
            }
            return;
        }
        if (_frevvo.baseView.isRepeatItem(c)) {
            this.onRepeatItemDrop(c, e, g);
        } else {
            var b = c.parentNode.parentNode;
            var a = c.parentNode;
            Element.remove(c);
            this.showInfo(b, a);
        }
        if ("above" === g) {
            _frevvo.utilities.insertBefore(c, this.$switchControl(e));
        } else {
            var i = _frevvo.protoView.$childByClass(this.$content(e), "f-page-break");
            if (i) {
                _frevvo.utilities.insertAfter(c, i);
            } else {
                this.insertFirst(c, e);
            }
            if (!this.isSelected(e)) {
                this.hideContent(e);
            }
        }
    },
    setValid: function(b, a) {
        if (a == "true") {
            Element.removeClassName(b, "s-invalid");
            Element.addClassName(b, "s-valid");
            Element.removeClassName(b, "s-unselected-invalid");
            if (_frevvo.uberController.mobile) {
                _frevvo.switchControlView.setNavIndicator(b, a);
            }
        } else {
            Element.removeClassName(b, "s-valid");
            Element.addClassName(b, "s-invalid");
            if (!this.isSelected(b)) {
                Element.addClassName(b, "s-unselected-invalid");
            }
            if (_frevvo.uberController.mobile) {
                _frevvo.switchControlView.setNavIndicator(b, a);
            }
        }
    },
    setVisible: function(b, a) {
        if (a == "true") {
            Element.removeClassName(b, "s-invisible");
            var c = this.$content(b);
            if (c != null) {
                Element.removeClassName(c, "s-invisible");
            }
        } else {
            Element.addClassName(b, "s-invisible");
            var c = this.$content(b);
            if (c != null) {
                Element.addClassName(c, "s-invisible");
            }
        }
    },
    setSelected: function(b, a) {
        if (a == "true") {
            this.select(b);
            if (!b.isSetupChildren) {
                _frevvo.switchCaseController.setupChildren(b);
                b.isSetupChildren = true;
            }
        } else {
            this.unSelect(b);
        }
    },
    setExpanded: function(b, a) {},
    setControlWidth: function(b, a) {
        Element.setStyle(b, $H({
            width: a
        }));
    },
    setBackgroundColor: function(b, a) {
        if (this.isSelected(b)) {
            Element.setStyle(b, $H({
                "background-color": a
            }));
        } else {
            Element.setStyle(b, $H({
                "background-color": ""
            }));
        }
    },
    onUnFocus: function(d, e, b, a) {
        var c = a;
        if (!c) {
            c = this.$selectedCase(d);
        }
        if (c == d) {
            return;
        }
    },
    onFocus: function(c, d, a) {
        var b = this.$selectedCase(c);
        if (b == c) {
            return;
        }
        this.select(c);
        if (_frevvo.baseView.isLinked(c)) {
            _frevvo.model.updateControlState(c, d, a, false);
        }
    },
    observeSelection: function(a, b) {
        FEvent.observe(a, "click", b);
        FEvent.observe(a, "keydown", b);
    },
    createEmptyContent: function(d) {
        if (d != null) {
            var f = this.$content(d);
            var e = f.cloneNode(false);
            var b = _frevvo.protoView.$childByClass(f, "f-info");
            if (b) {
                var a = b.cloneNode(true);
                e.appendChild(a);
                Element.show(a);
            }
            return e;
        } else {
            return null;
        }
    }
});
_frevvo.pageBreakView = {};
Object.extend(_frevvo.pageBreakView, _frevvo.baseView);
Object.extend(_frevvo.pageBreakView, {
    $element: function(a) {
        return null;
    },
    $elements: function(a) {
        return null;
    },
    $label: function(a) {
        return null;
    },
    $required: function(a) {
        return null;
    },
    $edit: function(a) {
        return this.$childByClass(a, "e-edit");
    },
    $editRemove: function(a) {
        return this.$childByClass(this.$edit(a), "e-remove");
    },
    isAtRoot: function(a) {
        if (this.is(a)) {
            a = a.parentNode.parentNode;
            if (a) {
                return "root-control" == _frevvo.baseView.getControlId(a);
            }
        }
        return false;
    },
    getLiteral: function(a) {
        return "f-page-break";
    },
    is: function(a) {
        return (a.className && a.className.indexOf(this.getLiteral(a)) >= 0 && a.className.indexOf("PageBreak") >= 0);
    }
});
_frevvo.phoneGroupView = {};
Object.extend(_frevvo.phoneGroupView, _frevvo.groupView);
Object.extend(_frevvo.phoneGroupView, {
    $header: function(a) {
        return this.$childByClass(a, "h-phone-group");
    },
    $content: function(a) {
        return this.$childByClass(a, "c-phone-group");
    },
    observeExpandCollapse: function(a, b) {
        var c = this.$header(a);
        if (c) {
            FEvent.observe(c, "click", b);
        }
    },
    getLiteral: function(a) {
        return "f-phone-group";
    },
    is: function(a) {
        return (a.className && a.className.indexOf(this.getLiteral(a)) >= 0);
    }
});
_frevvo.formView = {};
Object.extend(_frevvo.formView, _frevvo.baseView);
Object.extend(_frevvo.formView, {
    CLASS: "f-form",
    $header: function(a) {
        return this.$childByClass(a, "h-form");
    },
    $name: function(a) {
        return this.$childByClass(this.$header(a), "f-name");
    },
    $description: function(a) {
        return this.$childByClass(this.$header(a), "f-description");
    },
    $theme: function(a) {
        return this.$childByClass(this.$header(a), "f-theme");
    },
    $formWidth: function(a) {
        return this.$childByClass(this.$header(a), "f-form-width");
    },
    $formCustomWidth: function(a) {
        return this.$childByClass(this.$header(a), "f-form-custom-width");
    },
    $controlLayout: function(a) {
        return this.$childByClass(this.$header(a), "f-form-controlLayout");
    },
    $edit: function(a) {
        return this.$childByClass(this.$header(a), "e-edit");
    },
    $content: function(a) {
        return this.$childByClass(a, "c-form");
    },
    $rootControl: function(a) {
        var b = this.$content(a);
        if (b) {
            return this.$childById(b, "root-control");
        }
        return null;
    },
    $tbStatus: function() {
        return this.$childByClass(this.$childByClass(this.$childByClass(this.$childByClass($("form-container"), "f-form-header"), "f-form-tb"), "f-tb-status-holder"), "f-tb-status");
    },
    getId: function(a) {
        return a.id;
    },
    setId: function(b, a) {
        b.id = a;
    },
    setName: function(b, a) {
        this.$name(b).innerHTML = a;
    },
    getName: function(a) {
        return this.$name(a).innerHTML;
    },
    setDescription: function(b, a) {
        this.$description(b).innerHTML = a;
    },
    getDescription: function(a) {
        return this.$description(a).innerHTML;
    },
    setTheme: function(a, b) {
        this.$theme(a).innerHTML = b;
    },
    getTheme: function(a) {
        return this.$theme(a).innerHTML;
    },
    setThemeURL: function(c, d) {
        if (!d) {
            return;
        }
        var a = document.getElementById("f-theme-link");
        if (a) {
            a.href = d + "/form.css";
        }
        var e = document.getElementById("f-flow-theme-link");
        if (e) {
            e.href = d + "/flow.css";
        }
        var b = document.getElementById("f-edittheme-link");
        if (b) {
            b.href = d + "/form-edit.css";
        }
    },
    getFormWidth: function(a) {
        return $(a).parentNode.className.replace(/(.*)l-width-([^ $]*)(.*)/, "$2");
    },
    setFormWidth: function(b, a) {
        Element.removeClassName(b.parentNode, "l-width-" + this.getFormWidth(b));
        Element.addClassName(b.parentNode, "l-width-" + a);
    },
    getFormCustomWidth: function(a) {
        return a.parentNode.style.width;
    },
    setFormCustomWidth: function(b, a) {
        $(b).parentNode.style.width = a;
        BaseEditView.setCustomWidth($(b).editor, a);
    },
    getControlLayout: function(a) {
        return this.$controlLayout(a).innerHTML;
    },
    setControlLayout: function(b, a) {
        this.$controlLayout(b).innerHTML = a;
    },
    getChallenge: function(a) {
        return a.getAttribute("challenge");
    },
    setChallenge: function(b, a) {
        b.setAttribute("challenge", a);
    },
    setDynaWidth: function(b, a) {
        if (a) {
            if ($("threecolumn")) {
                $("threecolumn").style.width = a;
            }
            if ($("form-container-table")) {
                $("form-container-table").style.width = a;
            }
            if ($("form-container")) {
                $("form-container").style.width = a;
            }
        }
    },
    setPositionPeriod: function(b, e, a, d, c) {
        if (navigator.geolocation) {
            _frevvo.GeolocationController.positionPeriod = e;
            if (a) {
                _frevvo.GeolocationController.positionInterval = a;
            }
            if (d) {
                _frevvo.GeolocationController.positionTimeout = d;
            }
            if (c) {
                _frevvo.GeolocationController.positionEnhanced = c;
            }
            _frevvo.GeolocationController.setup();
        }
    },
    getSaveButton: function() {
        return _frevvo.protoView.$childByClass($("f-form-tb"), "f-tb-save");
    },
    getLogoutButton: function() {
        return _frevvo.protoView.$childByClass($("f-form-tb"), "f-tb-logout");
    },
    getLiteral: function(a) {
        return this.CLASS;
    },
    is: function(a) {
        return (a.className && a.className.indexOf(this.CLASS) >= 0);
    },
    isEditMode: function(a) {
        return Element.hasClassName(a.parentNode, "s-edit");
    },
    isTyped: function(a) {
        return false;
    },
    insertControl: function(a) {
        var b = _frevvo.baseView.getForm();
        _frevvo.groupView.insertFirst(a, this.$rootControl(b));
    },
    insertLast: function(a) {
        var b = _frevvo.baseView.getForm();
        _frevvo.groupView.insertLast(a, this.$rootControl(b));
    },
    isValid: function(b) {
        var a = this.$rootControl(b);
        return _frevvo.baseView.isValid(a);
    },
    observeHover: function(a) {
        var b = this.$header(a);
        FEvent.observe(b, "mouseover", _frevvo.formView.onMouseOver.bindAsObserver(_frevvo.formView, a));
        FEvent.observe(b, "mouseout", _frevvo.formView.onMouseOut.bindAsObserver(_frevvo.formView, a));
    },
    onMouseOver: function(a, b) {
        Event.stop(a);
        if (_frevvo.utilities.ignoreMouseEvent(a, this.$header(b))) {
            return;
        }
        Element.addClassName(b, "s-group-hover");
        Element.addClassName(this.$header(b), "s-hover");
    },
    onMouseOut: function(a, b) {
        Event.stop(a);
        if (_frevvo.utilities.ignoreMouseEvent(a, this.$header(b))) {
            return;
        }
        Element.removeClassName(b, "s-group-hover");
        Element.removeClassName(this.$header(b), "s-hover");
    },
    isReadOnly: function() {
        var a = _frevvo.baseView.getForm();
        if (a) {
            return Element.hasClassName(a, "s-readonly");
        } else {
            return false;
        }
    },
    setReadOnly: function(a, b) {
        if (b) {
            Element.addClassName(a, "s-readonly");
        } else {
            Element.removeClassName(a, "s-readonly");
        }
    },
    setStateU: function(a, b) {
        if (b.readOnly) {
            _frevvo.formView.setReadOnly(a, b.readOnly);
        }
        if (b.themeBaseUrl) {
            _frevvo.formView.setThemeURL(a, b.themeBaseUrl);
        }
        if (b.challenge) {
            _frevvo.formView.setChallenge(a, b.challenge);
        }
        if (b.dynawidth) {
            _frevvo.formView.setDynaWidth(a, b.dynawidth);
        }
        if (b.positionPeriod) {
            _frevvo.formView.setPositionPeriod(a, b.positionPeriod, b.positionInterval, b.positionTimeout, b.positionEnhanced);
        }
    },
    setState: function(c, d) {
        if (d.name) {
            this.setName(c, d.name);
            var h = $("f-form-title");
            if (h) {
                h.innerHTML = d.name;
            }
        }
        if (d.description) {
            this.setDescription(c, d.description);
        }
        var g = this.getTheme(c);
        if (d.theme !== undefined && g != d.theme) {
            window.location.reload(true);
        }
        if (d.colorSchemeRefresh) {
            window.location.reload(true);
        }
        try {
            var a = $("customWidth");
            if (a) {
                a.disabled = (d.width != "custom");
                if (d.width) {
                    this.setFormWidth(c, d.width);
                }
                if (d.customWidth) {
                    this.setFormCustomWidth(c, d.customWidth);
                }
                if (d.editStatus && ((d.editStatus & FrevvoConstants.BAD_WIDTH) > 0)) {
                    Element.addClassName(a.parentNode, "s-error");
                } else {
                    Element.removeClassName(a.parentNode, "s-error");
                }
            }
            var i = $("positionInterval");
            if (i) {
                i.disabled = (d.positionPeriod != "CUSTOM");
                if (d.positionInterval) {
                    BaseEditView.setPositionInterval($(c).editor, d.positionInterval);
                }
                if (d.editStatus && ((d.editStatus & FrevvoConstants.BAD_POS_INTERVAL) > 0)) {
                    Element.addClassName(i.parentNode, "s-error");
                } else {
                    Element.removeClassName(i.parentNode, "s-error");
                }
            }
            var b = this.getControlLayout(c);
            if (b != d.controlLayout) {
                this.setControlLayout(c, d.controlLayout);
            }
            if (d.fontname) {
                Element.setStyle(c, $H({
                    "font-family": d.fontname
                }));
            } else {
                Element.setStyle(c, $H({
                    "font-family": ""
                }));
            }
            if (d.fontsize) {
                Element.setStyle(c, $H({
                    "font-size": d.fontsize
                }));
            } else {
                Element.setStyle(c, $H({
                    "font-size": ""
                }));
            }
            if (d.fontcolour) {
                Element.setStyle(c, $H({
                    color: d.fontcolour
                }));
            } else {
                Element.setStyle(c, $H({
                    color: ""
                }));
            }
            if (d.showPrint) {
                Element.addClassName(c.parentNode, "s-printable");
            } else {
                Element.removeClassName(c.parentNode, "s-printable");
            }
            if (d.showLogo) {
                Element.addClassName(c.parentNode, "s-logo");
            } else {
                Element.removeClassName(c.parentNode, "s-logo");
            }
            if (d.showSaveSwitch) {
                Element.addClassName(c.parentNode, "s-save-switch");
            } else {
                Element.removeClassName(c.parentNode, "s-save-switch");
            }
            if (d["save-docaction"] == "yes") {
                $("savePDF").disabled = "";
            } else {
                $("savePDF").disabled = "true";
            }
            if (d.enhancedAccessibility == "yes") {
                Element.addClassName(c.parentNode, "s-enhanced-accessibility");
            } else {
                Element.removeClassName(c.parentNode, "s-enhanced-accessibility");
            }
        } catch (f) {}
    },
    applyTheme: function(e, a) {
        var d = document.getElementsByTagName("link");
        for (var c = 0; c < d.length; c++) {
            var b = d[c].href;
            if (b.indexOf(e) > 0) {
                d[c].href = b.replace(e, a);
            }
        }
    },
    traverse: function(a) {
        var b = _frevvo.baseView.getForm();
        this.walkContent(this.$content(b), a);
    },
    walkContent: function(h, g) {
        var b = this.$childrenByName(h, "DIV");
        for (var f = 0; f < b.length; f++) {
            var a = _frevvo.baseView.getView(b[f]);
            if (a) {
                g(b[f]);
                if ((_frevvo.groupView.is(b[f])) || (_frevvo.sectionView.is(b[f])) || (_frevvo.repeatView.is(b[f])) || (_frevvo.panelView.is(b[f])) || (_frevvo.switchCaseView.is(b[f]))) {
                    var e = a.$content(b[f]);
                    if (e != null) {
                        this.walkContent(e, g);
                    }
                } else {
                    if (_frevvo.switchControlView.is(b[f])) {
                        var d = a.$switchCases(b[f]);
                        if (d) {
                            for (var c = 0; c < d.length; c++) {
                                g(d[c]);
                                var e = _frevvo.switchCaseView.$content(d[c]);
                                if (e != null) {
                                    this.walkContent(e, g);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
});
_frevvo.treeView = {};
Object.extend(_frevvo.treeView, _frevvo.protoView);
Object.extend(_frevvo.treeView, {
    CLOSED_CLASS: "closed",
    $plusminus: function(a) {
        return this.$childByName(this.$childByName(a, "span"), "span");
    },
    $folders: function(a) {
        return this.$childByName(this.$childByName(this.$childByName(a, "span"), "span"), "span");
    },
    $content: function(a) {
        return this.$childByName(this.$childByName(this.$childByName(this.$childByName(a, "span"), "span"), "span"), "span");
    },
    $root: function(a) {
        var b = a;
        while (b && !Element.hasClassName(b, "e-tree")) {
            b = b.parentNode;
        }
        return b;
    },
    $rootId: function(a) {
        return this.getId(this.$root(a));
    },
    getView: function() {
        return _frevvo.treeView;
    },
    getId: function(a) {
        return a.getAttribute("cId");
    },
    observePlusMinus: function(b) {
        var a = this.$plusminus(b);
        if (a) {
            FEvent.observe(a, "click", this.onPlusMinus.bindAsObserver(this, b));
        }
    },
    onPlusMinus: function(a, b) {
        Event.stop(a);
        if (Element.hasClassName(b, this.CLOSED_CLASS)) {
            Element.removeClassName(b, this.CLOSED_CLASS);
        } else {
            Element.addClassName(b, this.CLOSED_CLASS);
        }
    },
    observeFolders: function(a) {},
    traverse: function(e, d) {
        var a = this.$childrenByName(e, "li");
        for (var c = 0; c < a.length; c++) {
            d(a[c]);
            var b = this.$childByName(a[c], "ul");
            if (b != null) {
                this.traverse(b, d);
            }
        }
    }
});
_frevvo.applicationView = {};
Object.extend(_frevvo.applicationView, _frevvo.protoView);
Object.extend(_frevvo.applicationView, {
    getView: function() {
        return _frevvo.applicationView;
    },
    getAnId: function(b, a) {
        var c = b;
        while (c && !c.getAttribute(a)) {
            c = c.parentNode;
        }
        return c.getAttribute(a);
    },
    getUserId: function(a) {
        return this.getAnId(a, "uId");
    },
    getApplicationId: function(a) {
        return this.getAnId(a, "aId");
    },
    getThemeId: function(a) {
        return this.getAnId(a, "tId");
    },
    getSchemaId: function(a) {
        return this.getAnId(a, "sId");
    },
    getFormId: function(a) {
        return this.getAnId(a, "fId");
    },
    getFlowId: function(a) {
        return this.getAnId(a, "fId");
    },
    getDocumentId: function(a) {
        return this.getAnId(a, "dId");
    },
    observeClick: function(a, b) {
        FEvent.observe(a, "click", b);
    },
    onNewApplication: function(d, c) {
        var b = this.makeNode(c);
        document.getElementById("a-list").appendChild(b);
        var a = this.$childByClass(b, "a-icons");
        _frevvo.applicationController.removeApplication.setup(this.$childByClass(a, "a-del-app"));
    },
    onRemoveApplication: function(b) {
        var a = b.parentNode.parentNode.parentNode.parentNode;
        a.parentNode.removeChild(a);
    },
    onRemoveTheme: function(b) {
        var a = b.parentNode.parentNode.parentNode;
        a.parentNode.removeChild(a);
    },
    onToggleDeploy: function(a) {
        if (Element.hasClassName(a, "a-deploy-form") || Element.hasClassName(a, "a-deploy-flow")) {
            Element.hide(a);
            Element.show(a.nextSibling);
        } else {
            Element.hide(a);
            Element.show(a.previousSibling);
        }
    },
    onRemoveSchema: function(b) {
        var a = b.parentNode.parentNode;
        a.parentNode.removeChild(a);
    },
    onRemoveDocuments: function(d, c) {
        var b = d.parentNode.parentNode;
        var a = this.$childrenByName(b, "li");
        a.each(function(e) {
            e.parentNode.removeChild(e);
        });
        elToRemove.parentNode.removeChild(elToRemove);
    },
    onRemoveDocument: function(c, b) {
        var a = c.parentNode.parentNode;
        a.parentNode.removeChild(a);
    }
});
_frevvo.helpContentView = {};
Object.extend(_frevvo.helpContentView, _frevvo.protoView);
Object.extend(_frevvo.helpContentView, {
    $getContent: function(b) {
        try {
            return this.$childByClass(b, "c-helpcontent").innerHTML;
        } catch (a) {}
    },
    $header: function(b) {
        try {
            return this.$childByClass(b, "h-helpcontent");
        } catch (a) {}
    },
    $contentClose: function(b) {
        try {
            return this.$childByClass(this.$header(b), "f-helpcontent-close");
        } catch (a) {}
    },
    $setContent: function(c, a) {
        try {
            this.$childByClass(c, "c-helpcontent").innerHTML = a;
        } catch (b) {}
    },
    $is: function(a) {
        return a.className && a.className.indexOf("f-helpcontent") >= 0;
    }
});
_frevvo.helpDisplayView = {};
Object.extend(_frevvo.helpDisplayView, _frevvo.protoView);
Object.extend(_frevvo.helpDisplayView, {
    $helpDisplay: function() {
        if (this.helpDisplayElement == null) {
            this.helpDisplayElement = $("f-help-display");
        }
        var a = this.helpDisplayElement = $("f-help-display");
        return this.helpDisplayElement;
    },
    $setContent: function(a) {
        var b = this.$childByClass(this.$helpDisplay(), "f-help-display-content");
        b.innerHTML = a;
    },
    $getContentEl: function() {
        return this.$childByClass(this.$helpDisplay(), "f-help-display-content");
    },
    $getContent: function() {
        this.$childByClass(this.$helpDisplay(), "f-help-display-content").value;
    },
    $getDisplayClose: function() {
        return this.$childByClass(this.$helpDisplay(), "f-help-display-close");
    },
    $displayPatternHelp: function(b, a) {
        this.doDisplay(null, b, _frevvo.localeStrings.patternHelp1 + '<a target="_new" href="' + a + '">' + _frevvo.localeStrings.patternHelp2 + "</a>", $("c-properties"));
    },
    $displayRootElementHelp: function(b, a) {
        this.doDisplay(null, b, _frevvo.localeStrings.rootElementHelp, $("c-properties"));
    },
    $display: function(c, a, b) {
        this.doDisplay(c, a, b, $("form-container"));
    },
    doDisplay: function(c, e, g, a) {
        var d = this.$helpDisplay();
        if (Element.visible(d)) {
            Element.hide(d);
        }
        if (g.trim().length > 0) {
            this.$setContent(g);
            var b = this.$getDisplayClose();
            var i = document.activeElement;
            var h = _frevvo.baseView.getView(c);
            h && h.$element && (i = h.$element(c));
            if (FEvent.hasObservers(b, "click")) {
                FEvent.stopObserving(b, "click");
            }
            FEvent.observe(b, "click", function(j, k) {
                Element.hide(k.parentNode);
                i && i.focus();
            }.bindAsObserver(this, b));
            if (FEvent.hasObservers(d, "keydown")) {
                FEvent.stopObserving(d, "keydown");
            }
            FEvent.observe(d, "keydown", function(k, j, l) {
                if (k.type == "keydown" && (k.keyCode == 9 || k.keyCode == 27)) {
                    Event.stop(k);
                    Element.hide(l);
                    j && j.focus();
                }
            }.bindAsObserver(this, i, d));
            var f = _frevvo.utilities.absolutePositioner.display(e, d, a, 10);
            this.$getContentEl().focus();
            return f;
        }
    }
});
_frevvo.comboBoxView = {};
Object.extend(_frevvo.comboBoxView, _frevvo.baseSimpleView);
Object.extend(_frevvo.comboBoxView, {
    maxResults: 5,
    $element: function(a) {
        return this.$childByClass(this.$childByClass(a, "facade"), "textarea");
    },
    $ul: function(a) {
        return this.$childByName(this.$childByClass(a, "facade"), "ul");
    },
    getLiteral: function(a) {
        return "f-combobox";
    },
    is: function(a) {
        return (a.className && a.className.indexOf(this.getLiteral(a)) >= 0) && this.$element(a);
    },
    setOptionsUrl: function(a, c) {
        var b = this.$element(a).id + "-combobox-options-url";
        $(b).innerHTML = c;
    },
    getSearchPieces: function(a) {
        return a.value.split(",");
    },
    getCurrentItemIndex: function(c) {
        var e = -1;
        var d = this.getSearchPieces(c);
        var f = 0;
        for (var b = 0; b < d.length; b++) {
            var a = f + d[b].length;
            if (c.selectionStart >= f && c.selectionStart <= a) {
                e = b;
                break;
            }
            f += d[b].length + 1;
        }
        return e;
    },
    isSingleValueOnly: function(a) {
        return ("true" === $(a.id + "-combobox-single-value-only").innerHTML);
    },
    extractSearchString: function(a) {
        if (this.isSingleValueOnly(a)) {
            return a.value;
        } else {
            var b = this.getSearchPieces(a);
            return b[this.getCurrentItemIndex(a)].trim();
        }
    },
    onkeyup: function(a, c) {
        var d = this;
        var b = $(d.$element(c).id + "-combobox-options-url").innerHTML;
        _frevvo.comboBoxController.findMatches(b, this.extractSearchString(this.$element(c)), function(j) {
            if (j.length > 0) {
                var f = d.$element(c).id + "-cbx-result-container-";
                var h = function(i) {
                    return $(f + i);
                };
                var e = function(i) {
                    var l = h(i);
                    return d.$childByName(l, "A");
                };
                for (var g = 0; g < _frevvo.comboBoxView.maxResults; g++) {
                    var k = e(g);
                    k.innerHTML = "";
                    Element.removeClassName(k, "s-hover");
                    Element.removeClassName(h(g), "s-hasvalue");
                }
                for (var g = 0; g < j.length && g < _frevvo.comboBoxView.maxResults; g++) {
                    e(g).innerHTML = j[g];
                    Element.addClassName(h(g), "s-hasvalue");
                }
                Element.addClassName(c, "s-active");
            } else {
                Element.removeClassName(c, "s-active");
            }
        });
    },
    setSelectedResult: function(c, a) {
        var d = this.$element(c);
        if (this.isSingleValueOnly(d)) {
            d.value = a;
        } else {
            var b = this.getSearchPieces(d);
            b[this.getCurrentItemIndex(d)] = a;
            d.value = b.toString();
            if (d.value.length > 0 && d.value.charAt(d.value.length - 1) != ",") {
                d.value = d.value + ",";
            }
        }
        Element.removeClassName(c, "s-active");
        _frevvo.uberController.valueChangeObserver(null, c);
    },
    getOptions: function(a) {
        return _frevvo.protoView.$childrenByName(this.$ul(a), "LI");
    }
});
_frevvo.wizardController = {
    panels: null,
    current: -1,
    backBtn: null,
    nextBtn: null,
    cancelBtn: null,
    finishBtn: null,
    options: {},
    data: {},
    main: {
        setup: function(d, f, b) {
            if (f) {
                _frevvo.wizardController.options = f;
            }
            var g = d.getAttribute("tId");
            if (g) {
                _frevvo.wizardController.data.templateId = g;
            }
            var a = _frevvo.protoView.$childrenByClass(d, "c-wizard");
            _frevvo.wizardController.panels = a;
            _frevvo.wizardController.current = 0;
            for (var c = 1; c < a.length; c++) {
                Element.hide(a[c]);
            }
            var e = _frevvo.protoView.$childByClass(d, "buttons");
            if (e) {
                _frevvo.wizardController.backBtn = _frevvo.protoView.$childByClass(e, "back_btn");
                if (_frevvo.wizardController.backBtn) {
                    Element.hide(_frevvo.wizardController.backBtn);
                    _frevvo.wizardController.back.setup(_frevvo.wizardController.backBtn);
                }
                _frevvo.wizardController.nextBtn = _frevvo.protoView.$childByClass(e, "next_btn");
                if (_frevvo.wizardController.nextBtn) {
                    _frevvo.wizardController.next.setup(_frevvo.wizardController.nextBtn);
                }
                _frevvo.wizardController.cancelBtn = e.querySelector(".cancel_btn");
                if (_frevvo.wizardController.cancelBtn) {
                    _frevvo.wizardController.cancel.setup(_frevvo.wizardController.cancelBtn);
                }
                _frevvo.wizardController.finishBtn = _frevvo.protoView.$childByClass(e, "finish_btn");
                if (!_frevvo.wizardController.finishBtn) {
                    _frevvo.wizardController.finishBtn = _frevvo.protoView.$childByClass(e, "s-finish-button");
                }
                if (_frevvo.wizardController.finishBtn) {
                    _frevvo.wizardController.finish.setup(_frevvo.wizardController.finishBtn);
                }
                if (a.length == 1 && _frevvo.wizardController.nextBtn) {
                    Element.hide(_frevvo.wizardController.nextBtn);
                }
            }
            _frevvo.wizardController.kbNavigator.setup(b);
        }
    },
    kbNavigator: {
        setup: function(a) {
            if (!FEvent.hasObservers(a, "keydown")) {
                FEvent.observe(a, "keydown", _frevvo.wizardController.kbNavigator.observer.bindAsObserver(_frevvo.wizardController.kbNavigator, a));
            }
        },
        initFocus: function(a) {
            var b = document.querySelector("#" + a.id + " *[tabindex='-1']");
            b && b.focus();
        },
        isActivationEvent: function(a) {
            if (a) {
                return (a.type == "click" || (a.type == "keydown" && (a.keyCode == "13" || a.keyCode == "32")));
            }
            return false;
        },
        observer: function(h, b) {
            if (h.type == "keydown") {
                var d = null;
                if (h.keyCode == 9 && !h.shiftKey) {
                    d = "Forward";
                    Event.stop(h);
                } else {
                    if (h.keyCode == 9 && h.shiftKey) {
                        d = "Back";
                        Event.stop(h);
                    }
                }
                if (d) {
                    var c = "#" + b.id;
                    var a;
                    a = document.querySelectorAll(c + " *[tabindex='-1']," + c + " *[tabindex='0']");
                    var g = [];
                    for (var e = 0; e < a.length; e++) {
                        g.push(a[e]);
                    }
                    if (g.length > 0) {
                        var k = -1;
                        for (var f = 0; f < g.length; f++) {
                            if (g[f] == document.activeElement) {
                                k = f;
                                break;
                            }
                        }
                        if (d == "Forward") {
                            k++;
                            if (k >= g.length) {
                                k = 0;
                            }
                            g[k].focus();
                            setTimeout(_frevvo.wizardController.kbNavigator.checkFocus.bind(_frevvo.wizardController, true, k, g, g.length), 50);
                        } else {
                            if (d == "Back") {
                                k--;
                                if (k < 0) {
                                    k = g.length - 1;
                                }
                                g[k].focus();
                                setTimeout(_frevvo.wizardController.kbNavigator.checkFocus.bind(_frevvo.wizardController, false, k, g, g.length), 50);
                            }
                        }
                    }
                }
            }
        },
        checkFocus: function(c, a, d, b) {
            b--;
            if (b <= 0) {
                return;
            }
            if (d[a] == document.activeElement) {
                return;
            }
            if (c) {
                a++;
                if (a >= d.length) {
                    a = 0;
                }
                d[a].focus();
            } else {
                a--;
                if (a < 0) {
                    a = d.length - 1;
                }
                d[a].focus();
            }
            setTimeout(_frevvo.wizardController.kbNavigator.checkFocus.bind(_frevvo.wizardController, c, a, d), 50);
        },
    },
    next: {
        setup: function(b) {
            var a = this.doIt.bindAsObserver(this, b);
            FEvent.observe(b, "click", a);
            FEvent.observe(b, "keydown", a);
        },
        doIt: function(a, b) {
            if (_frevvo.wizardController.kbNavigator.isActivationEvent(a)) {
                if (Element.hasClassName(b, "s-disabled")) {
                    return;
                }
                Element.hide(_frevvo.wizardController.panels[_frevvo.wizardController.current]);
                _frevvo.wizardController.current += 1;
                Element.show(_frevvo.wizardController.panels[_frevvo.wizardController.current]);
                if (!Element.visible(_frevvo.wizardController.backBtn)) {
                    Element.show(_frevvo.wizardController.backBtn);
                }
                if (_frevvo.wizardController.current == _frevvo.wizardController.panels.length - 1) {
                    Element.hide(_frevvo.wizardController.nextBtn);
                    if (_frevvo.wizardController.finishBtn) {
                        Element.show(_frevvo.wizardController.finishBtn);
                    }
                }
            }
        }
    },
    back: {
        setup: function(b) {
            var a = this.doIt.bindAsObserver(this, b);
            FEvent.observe(b, "click", a);
            FEvent.observe(b, "keydown", a);
        },
        doIt: function(a, b) {
            if (_frevvo.wizardController.kbNavigator.isActivationEvent(a)) {
                Element.hide(_frevvo.wizardController.panels[_frevvo.wizardController.current]);
                _frevvo.wizardController.current -= 1;
                Element.show(_frevvo.wizardController.panels[_frevvo.wizardController.current]);
                if (_frevvo.wizardController.current == 0) {
                    Element.hide(_frevvo.wizardController.backBtn);
                }
                Element.show(_frevvo.wizardController.nextBtn);
            }
        }
    },
    cancel: {
        setup: function(b) {
            var a = this.doIt.bindAsObserver(this, b);
            FEvent.observe(b, "click", a);
            FEvent.observe(b, "keydown", a);
        },
        doIt: function(a, b) {
            if (_frevvo.wizardController.kbNavigator.isActivationEvent(a)) {
                if (a) {
                    Event.stop(a);
                }
                _frevvo.lightBoxView.dismiss(a);
            }
            return false;
        }
    },
    finish: {
        setup: function(b) {
            var a = this.doIt.bindAsObserver(this, b);
            FEvent.observe(b, "click", a);
            FEvent.observe(b, "keydown", a);
        },
        doIt: function(a, c) {
            if (_frevvo.wizardController.kbNavigator.isActivationEvent(a)) {
                var d = c.getAttribute("uri");
                if (d) {
                    window.location = d + "&_formTz=" + FormTz.getName() + "&state=" + encodeURIComponent(JSONUtil.objectToJSONString(_frevvo.wizardController.data));
                } else {
                    var g = c.getAttribute("action");
                    if (g == "addPaletteItem") {
                        _frevvo.lightBoxView.dismiss(null);
                        var f = c.getAttribute("controlId");
                        var e = JSONUtil.objectToJSONString(_frevvo.wizardController.data);
                        _frevvo.wizardController.data = {};
                        _frevvo.forms.addPaletteItem(f, UberEditController.palette.success, UberEditController.palette.failure, encodeURIComponent(e), {
                            el: c
                        });
                    } else {
                        if (g == "saveform") {
                            _frevvo.lightBoxView.dismiss(null);
                            var e = JSONUtil.objectToJSONString(_frevvo.wizardController.data);
                            _frevvo.wizardController.data = {};
                            _frevvo.forms.saveForm(_frevvo.formController.saveEmail.success, _frevvo.formController.saveEmail.failure, _frevvo.wizardController.options, encodeURIComponent(e));
                        } else {
                            if (g == "emailDocumentTask" || g == "emailActivityDocumentAction" || g == "activityDocActionsFinish" || g == "activityDocPost") {
                                var e = JSONUtil.objectToJSONString(_frevvo.wizardController.data);
                                _frevvo.wizardController.data = {};
                                CommonWizardFunctions.finish(encodeURIComponent(e), true);
                            } else {
                                if (g == "goToPage" || g == "displayAMessage" || g == "formPost" || g == "manualDocUris" || g == "keyAndSavedFields" || g == "docPost" || g == "emailDocumentDefault" || g == "emailDocumentAux" || g == "goToPaypal") {
                                    var e = JSONUtil.objectToJSONString(_frevvo.wizardController.data);
                                    _frevvo.wizardController.data = {};
                                    CommonWizardFunctions.finish(encodeURIComponent(e));
                                } else {
                                    if (g == "formatUsingGoogleDocument") {
                                        var e = JSONUtil.objectToJSONString(_frevvo.wizardController.data);
                                        _frevvo.wizardController.data = {};
                                        GoogleDocumentsWizard.finish.doIt(encodeURIComponent(e));
                                    } else {
                                        if (g == "saveToGoogleSpreadsheet") {
                                            var e = JSONUtil.objectToJSONString(_frevvo.wizardController.data);
                                            _frevvo.wizardController.data = {};
                                            SaveToGoogleSpreadsheetsWizard.finish.doIt(encodeURIComponent(e));
                                        } else {
                                            if (g == "saveToGoogleDocuments") {
                                                var e = JSONUtil.objectToJSONString(_frevvo.wizardController.data);
                                                _frevvo.wizardController.data = {};
                                                SaveToGoogleDocumentsWizard.finish.doIt(encodeURIComponent(e));
                                            } else {
                                                if (g == "saveToPaperVision") {
                                                    var e = JSONUtil.objectToJSONString(_frevvo.wizardController.data);
                                                    _frevvo.wizardController.data = {};
                                                    SaveToPaperVisionWizard.finish.doIt(encodeURIComponent(e));
                                                } else {
                                                    if (g == "taskQuick") {
                                                        var e = JSONUtil.objectToJSONString(_frevvo.wizardController.data);
                                                        var b = {
                                                            id: _frevvo.wizardController.data.activityId,
                                                            enableQuick: _frevvo.wizardController.data.enableQuick,
                                                            doIt: ActivityEditView.setQuickEnabled
                                                        };
                                                        _frevvo.wizardController.data = {};
                                                        CommonWizardFunctions.finish(encodeURIComponent(e), true, b);
                                                    } else {
                                                        if (g == "modify-space-item") {
                                                            var e = JSONUtil.objectToJSONString(_frevvo.wizardController.data);
                                                            _frevvo.wizardController.data = {};
                                                            _frevvo.wizardController.options.doIt(_frevvo.wizardController.options, e);
                                                        } else {
                                                            if (g == "getforms" || g == "newform" || g == "sign") {
                                                                _frevvo.lightBoxView.dismiss(null);
                                                                var e = JSONUtil.objectToJSONString(_frevvo.wizardController.data);
                                                                _frevvo.wizardController.data = {};
                                                                _frevvo.wizardController.options.doIt(_frevvo.wizardController.options, encodeURIComponent(e));
                                                            } else {
                                                                if (g == "getAForm") {
                                                                    _frevvo.lightBoxView.dismiss(null);
                                                                    var e = _frevvo.wizardController.data;
                                                                    _frevvo.wizardController.data = {};
                                                                    _frevvo.wizardController.options.doIt(_frevvo.wizardController.options, e);
                                                                } else {
                                                                    if (g == "upload" || g == "captcha") {
                                                                        _frevvo.wizardController.options.doIt(_frevvo.wizardController.options);
                                                                    } else {
                                                                        if (g == "quickApprove") {
                                                                            var e = JSONUtil.objectToJSONString(_frevvo.wizardController.data);
                                                                            _frevvo.wizardController.data = {};
                                                                            _frevvo.wizardController.options.doIt(_frevvo.wizardController.options, e);
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
};
_frevvo.baseDialogController = {
    setup: function(a) {
        this.initFocus(a);
        FEvent.observe(a, "keydown", _frevvo.baseDialogController.keydownObserver.bindAsObserver(_frevvo.baseDialogController, a));
    },
    initFocus: function(a) {
        a && a.focus();
    },
    keydownObserver: function(a, b) {
        if (a.type == "keydown") {
            if (a.keyCode == 27) {
                _frevvo.lightBoxView.dismiss(a);
            }
        }
    }
};
_frevvo.templateController = {
    removeTemplate: {
        doIt: function(a, b) {
            if (a) {
                Event.stop(a);
            }
            _frevvo.lightBoxView.showYesNoDialog(_frevvo.protoView.makeNode("This will permanently remove the template from the database. Are you sure?"), "Remove template", this.onConfirm.bindAsObserver(this, b));
            return false;
        },
        onConfirm: function(a, b) {
            window.location = b.href;
        }
    }
};
_frevvo.uberController = {
    editMode: false,
    previewMode: false,
    printMode: false,
    mobile: false,
    flow: false,
    anonymous: false,
    mapDiv: null,
    makeControlXhtml: function(a) {
        newEl = _frevvo.protoView.makeNode(a.controlXhtml);
        if (newEl.nodeName.toLowerCase() == "table") {
            newEl = _frevvo.protoView.$childByName(_frevvo.protoView.$childByName(newEl, "tbody"), "tr");
        }
        return newEl;
    },
    setup: function(b) {
        b = $(b);
        var a = _frevvo.baseView.getView(b);
        if (a != null) {
            if (a === _frevvo.outputView && Element.hasClassName(b, "f-map-div")) {
                _frevvo.uberController.mapDiv = b;
            }
            if (a.isLinked(b)) {
                if (a.isChangeable()) {
                    a.observeValueChange(b, this.valueChangeObserver.bindAsObserver(this, b));
                    this.keyup.setup(b, a);
                    this.keydown.setup(b, a);
                    a.observeFocus(b);
                    a.observeBlur(b);
                    if (a.observeClick) {
                        a.observeClick(b);
                    }
                }
                if (_frevvo.utilities.util.isDefined(window.CustomEventHandlers) && CustomEventHandlers.setup && a.$element) {
                    CustomEventHandlers.setup(a.$element(b));
                }
                this.setupHelp(b, a);
                if (_frevvo.dropdownView.is(b)) {
                    _frevvo.dropdownController.click.setup(b);
                }
                if (_frevvo.dateView.is(b)) {
                    _frevvo.dateController.setup(b);
                }
                if (_frevvo.signatureView.is(b)) {
                    _frevvo.signatureController.click.setup(b);
                } else {
                    if (_frevvo.linkedFormViewerView.is(b)) {
                        _frevvo.formViewerController.click.setup(b);
                    } else {
                        if (_frevvo.linkView.is(b)) {
                            _frevvo.linkController.setup(b);
                        }
                    }
                }
                if (_frevvo.comboBoxView.is(b)) {
                    _frevvo.comboBoxController.click.setup(b);
                }
                if (this.editMode) {
                    UberEditController.setup(b, a);
                }
                b.isSetup = true;
            }
        }
    },
    tearDown: function(b) {
        b = $(b);
        var a = _frevvo.baseView.getView(b);
        if (this.editMode) {
            UberEditController.tearDown(b, a);
        }
        b.isSetup = false;
    },
    setupHelp: function(d, c) {
        if (c.$help && c.$helpContent) {
            var b = c.$help(d);
            var a = c.$helpContent(d);
            if (b) {
                FEvent.observe(b, "click", function(e, f) {
                    _frevvo.helpDisplayView.$display(f, b, _frevvo.helpContentView.$getContent(a));
                }.bindAsObserver(this, d));
                FEvent.observe(d, "keydown", function(e, f) {
                    if (e.type == "keydown" && e.keyCode == 113) {
                        Event.stop(e);
                        _frevvo.helpDisplayView.$display(f, f, _frevvo.helpContentView.$getContent(a));
                    }
                }.bindAsObserver(this, d));
            }
        }
    },
    keydown: {
        setup: function(b, a) {
            if (a != null) {
                a.observeKeyDown(b, this.doIt.bindAsObserver(this, b, a));
            }
        },
        doIt: function(b, c, a) {
            if (a.onkeydown) {
                a.onkeydown(b, c);
            }
        }
    },
    keyup: {
        TAB: 9,
        DELETE: 46,
        TURNOFF: 94,
        SCROLLLOCK: 145,
        ARROWLEFT: 37,
        ARROWUP: 38,
        ARROWRIGHT: 39,
        ARROWDOWN: 40,
        ENTER: 13,
        ESCAPE: 27,
        SPACE: 32,
        setup: function(b, a) {
            if (a != null) {
                a.observeKeyUp(b, this.doIt.bindAsObserver(this, b, a));
            }
        },
        doIt: function(b, c, a) {
            Event.stop(b);
            if (a.onkeyup) {
                a.onkeyup(b, c);
            }
            if (b.keyCode >= this.TAB && b.keyCode <= this.DELETE) {
                return;
            }
            if (b.keyCode >= this.TURNOFF && b.keyCode <= this.SCROLLLOCK) {
                return;
            }
            if (c.timeoutId) {
                window.clearTimeout(c.timeoutId);
                c.timeoutId = null;
            }
            c.timeoutId = setTimeout(function() {
                _frevvo.uberController.keyup.doTimeout(c, a);
            }, 2500);
        },
        doTimeout: function(c, a) {
            var b = a.$element(c);
            if (b) {
                _frevvo.uberController.valueChangeObserver(null, c);
            }
        }
    },
    event: {
        isActivationEvent: function(a) {
            if (a) {
                return (a.type == "click" || (a.type == "keydown" && (a.keyCode == "13" || a.keyCode == "32")));
            }
            return false;
        }
    },
    controlState: {
        doIt: function(e, d, b, c, a) {
            _frevvo.forms.getControlXHTML(e, this.success, this.failure, {
                id: e,
                parentId: d,
                index: b,
                enabled: a
            }, c);
        },
        success: function(t, json, options) {
            if (t.responseText) {
                var jstate = eval(t.responseText);
                if (!jstate || !jstate.controlXhtml) {
                    return;
                }
                var newEl = _frevvo.uberController.makeControlXhtml(jstate);
                var parentEl = document.getElementById(options.parentId);
                var view = _frevvo.baseView.getView(parentEl);
                if (options.index > 0) {
                    var controls;
                    if (_frevvo.repeatView.is(parentEl)) {
                        controls = _frevvo.repeatView.getControls(parentEl);
                    } else {
                        if (_frevvo.groupView.is(parentEl)) {
                            controls = _frevvo.groupView.getControls(parentEl);
                        }
                    }
                    if (controls) {
                        _frevvo.utilities.insertAfter(newEl, controls[options.index - 1]);
                    } else {
                        view.insertLast(newEl, parentEl);
                    }
                } else {
                    view.insertLast(newEl, parentEl);
                }
                if (view == _frevvo.repeatView || view == _frevvo.tableView) {
                    RepeatController.setupAddedItems(newEl, RepeatController);
                    if (!options.enabled || options.enabled == "true") {
                        RepeatController.setupAddRepeatItemObservers.call(RepeatController, parentEl);
                        RepeatController.setupRemoveRepeatItemObservers.call(RepeatController, parentEl);
                    }
                } else {
                    _frevvo.uberController.setup(newEl);
                }
                _frevvo.jsonProcessor.updateStates(jstate);
            }
        },
        failure: function(c, b, a) {
            _frevvo.lightBoxView.hideWaitDialog();
            var d = new _frevvo.Errors(c.status, c.statusText);
            _frevvo.utilities.ajaxRequest.showError(d.getMessage(), "<span><strong>Unable to initialize form.</strong></span>", false);
        }
    },
    valueChangeObserver: function(b, d) {
        var a;
        if (_frevvo.comboBoxView.is(d) && Element.hasClassName(d, "s-active")) {
            return;
        }
        if (b && _frevvo.radioView.is(d)) {
            if (Event.element(b) == _frevvo.radioView.$element(d)) {
                d.newSelectedOption = Event.element(b);
            }
        }
        if (b && _frevvo.checkboxView.is(d)) {
            Event.stop(b);
            var e = _frevvo.protoView.$parentByClass(Event.element(b), "f-select-item");
            var c = _frevvo.protoView.$childByName(e, "INPUT");
            if (c) {
                c.checked = !c.checked;
            }
            _frevvo.baseView.getView(d).checkSelectedItems(d);
        }
        if (d.timeoutId) {
            window.clearTimeout(d.timeoutId);
            d.timeoutId = null;
        }
        a = _frevvo.baseView.getView(d);
        if (a) {
            a.setBusy(d, true);
            _frevvo.model.updateControlValue(d, this.valueChangeSuccess, this.valueChangeFailed);
        }
    },
    valueChangeSuccess: function(t, json, options) {
        var el = options.el;
        var view = _frevvo.baseView.getView(el);
        if (t.responseText) {
            var jstate = eval(t.responseText);
        }
        if (_frevvo.baseView.isValid(el)) {
            view.onValueChange(el);
        } else {
            view.onValueChangeFailed(el);
        }
        jstate && _frevvo.jsonProcessor.updateStates(jstate);
        jstate && _frevvo.jsonProcessor.removeControls(jstate);
        view.setBusy(el, false);
        _frevvo.uberController.debug.printTrace(jstate);
        if (_frevvo.workAreaView) {
            _frevvo.workAreaView.sizeHeight();
        }
    },
    valueChangeFailed: function(d, c, b) {
        var e = b.el;
        var a = _frevvo.baseView.getView(e);
        a.onValueChangeFailed(e);
        a.setBusy(e, false);
        var f = new _frevvo.Errors(d.status, d.statusText);
        _frevvo.utilities.ajaxRequest.showError(f.getMessage(), "<span><strong>" + _frevvo.localeStrings.valueupdatefailed + ".</strong><br/> <small><em>" + _frevvo.localeStrings.refreshingpage + "...</em></small></span>", true);
    },
    debugTemplate: new Template('<tr class="log-#{level}"><td class="log-index"            >#{index}</td> <td class="log-level"            >#{level}</td> <td class="log-event"            >#{event}</td> <td class="log-rule">#{rule}</td> <td class="log-details">#{details}</td></tr>'),
    debugTemplate2: new Template('<tr class="log-#{level}"><td class="log-index" rowspan="2">#{index}</td> <td class="log-level" rowspan="2">#{level}</td> <td class="log-event" rowspan="2">#{event}</td> <td class="log-rule">#{rule}</td> <td class="log-details">#{details}</td></tr> <tr class="log-#{level}"><td class="log-content" colspan="2"><pre>#{content}</pre></td></tr>'),
    debug: {
        printTrace: function(jstate) {
            if (!jstate.ruleTrace) {
                return;
            }
            var debugPrint = parent.document.getElementById("debugPrint");
            if (debugPrint) {
                var template = _frevvo.uberController.debugTemplate;
                var template2 = _frevvo.uberController.debugTemplate2;
                var json = eval(jstate.ruleTrace);
                if (json.length > 0) {
                    var html = "<table class='log'><thead><th class='log-index'>#</th><th class='log-level'>Level</th><th class='log-event'>Event</th><th class='log-rule'>Source</th><th class='log-details'>Details</th></thead><tbody>";
                    for (var i = 0; i < json.length; i++) {
                        if (json[i].content === "") {
                            html = html.concat(template.evaluate(json[i]));
                        } else {
                            html = html.concat(template2.evaluate(json[i]));
                        }
                    }
                    html = html.concat("</tbody></table><br/>");
                    debugPrint.innerHTML = html.concat(debugPrint.innerHTML);
                }
            }
        }
    },
    setStateOffline: function() {
        Element.addClassName(document.body, "s-offline");
    },
    setStateOnline: function() {
        Element.removeClassName(document.body, "s-offline");
    },
    bindEventListeners: function() {
        window.addEventListener("offline", function() {
            _frevvo.uberController.setStateOffline();
        });
        window.addEventListener("online", function() {
            _frevvo.uberController.setStateOnline();
        });
    },
    rules: {
        "#page-form": function(a) {
            _frevvo.uberController.bindEventListeners();
        }
    }
};
var UberController = _frevvo.uberController;
_frevvo.GeolocationController = {
    positionPeriod: null,
    positionInterval: -1,
    positionTimeout: 5,
    positionEnhanced: false,
    position: null,
    geocoder: null,
    setup: function() {
        try {
            if (navigator.geolocation) {
                if (this.positionEnhanced) {
                    this.geocoder = new google.maps.Geocoder();
                }
                if (this.positionPeriod === "LOAD" || this.positionPeriod === "CUSTOM") {
                    this.getPosition();
                }
            }
        } catch (a) {}
    },
    getPosition: function() {
        var a = _frevvo.GeolocationController.positionTimeout * 1000;
        navigator.geolocation.getCurrentPosition(_frevvo.GeolocationController.success, _frevvo.GeolocationController.failure, {
            enableHighAccuracy: true,
            timeout: a,
            maximumAge: 0
        });
        if (this.positionInterval > 0) {
            setInterval(this.getPosition, this.positionInterval * 1000);
        }
    },
    success: function(a) {
        _frevvo.GeolocationController.position = a;
        var b = {
            latitude: _frevvo.GeolocationController.position.coords.latitude,
            longitude: _frevvo.GeolocationController.position.coords.longitude,
            accuracy: _frevvo.GeolocationController.position.coords.accuracy
        };
        if (_frevvo.GeolocationController.positionEnhanced) {
            _frevvo.GeolocationController.geocoder.geocode({
                latLng: new google.maps.LatLng(_frevvo.GeolocationController.position.coords.latitude, _frevvo.GeolocationController.position.coords.longitude)
            }, function(f, d) {
                if (d == google.maps.GeocoderStatus.OK) {
                    if (f.length > 0) {
                        var c = f[0];
                        if (c.types.length > 0 && c.formatted_address) {
                            b[c.types[0]] = c.formatted_address;
                        }
                        var h = c.address_components;
                        for (var e = 0; e < h.length; e++) {
                            var g = h[e];
                            if (g.types.length > 0) {
                                b[g.types[0]] = g.long_name;
                            }
                        }
                    }
                }
                _frevvo.forms.updateLocation(_frevvo.GeolocationController.updateSuccess, _frevvo.GeolocationController.updateFailure, {}, encodeURIComponent(JSONUtil.objectToJSONString(b)));
            });
        } else {
            _frevvo.forms.updateLocation(_frevvo.GeolocationController.updateSuccess, _frevvo.GeolocationController.updateFailure, {}, encodeURIComponent(JSONUtil.objectToJSONString(b)));
        }
    },
    failure: function(a) {
        var b = JSONUtil.objectToJSONString({
            errorCode: a.code,
            errorMsg: a.message
        });
        _frevvo.forms.updateLocation(_frevvo.GeolocationController.updateSuccess, _frevvo.GeolocationController.updateFailure, {}, encodeURIComponent(b));
    },
    updateSuccess: function(t, json, options) {
        if (t.responseText) {
            var jstate = eval(t.responseText);
        }
        jstate && _frevvo.jsonProcessor.updateStates(jstate);
        jstate && _frevvo.jsonProcessor.removeControls(jstate);
        _frevvo.uberController.debug.printTrace(jstate);
        if (_frevvo.uberController.mapDiv && _frevvo.GeolocationController.position) {
            var coords = new google.maps.LatLng(_frevvo.GeolocationController.position.coords.latitude, _frevvo.GeolocationController.position.coords.longitude);
            var options = {
                zoom: 13,
                center: coords,
                mapTypeControl: false,
                navigationControlOptions: {
                    style: google.maps.NavigationControlStyle.SMALL
                },
                mapTypeId: google.maps.MapTypeId.ROADMAP
            };
            var map = new google.maps.Map(_frevvo.uberController.mapDiv, options);
            var marker = new google.maps.Marker({
                position: coords,
                map: map,
                title: "You are here!"
            });
        }
    },
    updateFailure: function(c, b, a) {}
};
_frevvo.dropdownController = {
    click: {
        setup: function(e) {
            if (e.isSetup) {
                return;
            }
            FEvent.observe(_frevvo.dropdownView.$element(e), "click", this.toggle.bindAsObserver(this, e));
            FEvent.observe(_frevvo.dropdownView.$caretHolder(e), "click", this.toggle.bindAsObserver(this, e));
            var c = _frevvo.dropdownView.getOptions(e);
            if (c) {
                for (var d = 0; d < c.length; d++) {
                    var b = _frevvo.protoView.$childByName(c[d], "A");
                    FEvent.flushAll(b, "click");
                    FEvent.observe(b, "click", this.doIt.bindAsObserver(this, e, b));
                }
            }
        },
        toggle: function(a, b) {
            if (a) {
                Event.stop(a);
            }
            if (Element.hasClassName(b, "s-disabled")) {
                return;
            }
            if (Element.hasClassName(b, "s-active")) {
                Element.removeClassName(b, "s-active");
                if (_frevvo.uberController.editMode) {
                    Element.setStyle(b, $H({
                        position: "relative"
                    }));
                }
                window.sActive = null;
            } else {
                _frevvo.baseView.hideActive();
                Element.addClassName(b, "s-active");
                if (_frevvo.uberController.editMode) {
                    Element.setStyle(b, $H({
                        position: ""
                    }));
                    Element.setStyle(b, $H({
                        opacity: ""
                    }));
                }
                window.sActive = b;
            }
        },
        doIt: function(c, e, b) {
            if (c) {
                Event.stop(c);
            }
            var d = [];
            d[0] = b.getAttribute("ovalue");
            _frevvo.dropdownController.click.toggle(null, e);
            _frevvo.dropdownView.setValue(e, d, b.innerHTML);
            _frevvo.uberController.valueChangeObserver(null, e);
        }
    }
};
_frevvo.dateController = {
    setup: function(a) {
        a = $(a);
        if (Element.hasClassName(a, "Date")) {
            _frevvo.dateController.hide(_frevvo.dateView.$timeElement(a));
            _frevvo.dateController.show(_frevvo.dateView.$dateElement(a));
        } else {
            if (Element.hasClassName(a, "Time")) {
                _frevvo.dateController.show(_frevvo.dateView.$timeElement(a));
                _frevvo.dateController.hide(_frevvo.dateView.$dateElement(a));
            } else {
                if (Element.hasClassName(a, "DateTime")) {
                    _frevvo.dateController.show(_frevvo.dateView.$timeElement(a));
                    _frevvo.dateController.show(_frevvo.dateView.$dateElement(a));
                }
            }
        }
    },
    show: function(a) {
        if (a && a.parentNode) {
            Element.show(a.parentNode);
        }
    },
    hide: function(a) {
        if (a && a.parentNode) {
            Element.hide(a.parentNode);
        }
    }
};
_frevvo.submitController = {
    setup: function(a) {
        _frevvo.submitView.observeClick(a);
        _frevvo.uberController.setup(a);
    },
    captcha: {
        challenge: function(b) {
            var c = Recaptcha.get_challenge();
            var a = Recaptcha.get_response();
            var d = document.forms.captcha_form;
            d.challenge.value = Recaptcha.get_challenge();
            d.response.value = Recaptcha.get_response();
            d.cname.value = b.cname;
            d.submit();
        },
        verify: function(a, c, b) {
            if (a & FrevvoConstants.CAPTCHA_SUCCESS) {
                if (_frevvo.uberController.mobile) {
                    _frevvo.switchControlController.disableNextNavBtn();
                }
                _frevvo.lightBoxView.dismiss(null);
                return _frevvo.submitView.submit(b);
            } else {
                var d = $("captcha-error");
                d.innerHTML = c;
                Element.show(d);
                Recaptcha.reload();
            }
        }
    }
};
_frevvo.triggerController = {
    click: {
        setup: function(a) {
            _frevvo.triggerView.observeActivation(a, this.doIt.bindAsObserver(this, a));
            _frevvo.uberController.setup(a);
        },
        doIt: function(a, b) {
            if (a.type == "click" || (a.type == "keydown" && a.keyCode == "13")) {
                if (!_frevvo.uberController.editMode) {
                    Event.stop(a);
                }
                _frevvo.baseView.hideActive();
                _frevvo.baseView.getView(b).setBusy(b, true);
                _frevvo.model.updateControlState(b, this.success, this.failure);
            }
        },
        success: function(t, json, options) {
            var el = options.el;
            if (t.responseText) {
                var jstate = eval(t.responseText);
            }
            jstate && _frevvo.jsonProcessor.updateStates(jstate);
            jstate && _frevvo.jsonProcessor.removeControls(jstate);
            _frevvo.baseView.getView(el).setBusy(el, false);
            _frevvo.uberController.debug.printTrace(jstate);
        },
        failure: function(c, b, a) {
            var d = a.el;
            var e = new _frevvo.Errors(c.status, c.statusText);
            _frevvo.utilities.ajaxRequest.showError(e.getMessage(), "<span><strong>Trigger click failed.</strong><br/><small><em>Refreshing page...</em></small></span>", true);
            _frevvo.baseView.getView(d).setBusy(d, false);
        }
    }
};
_frevvo.signatureController = {
    canvas: null,
    context: null,
    clickX: null,
    clickY: null,
    clickDrag: null,
    paint: null,
    offsetT: null,
    offsetL: null,
    isClear: null,
    preventMouseEvents: false,
    click: {
        setup: function(a) {
            var c = _frevvo.baseView.getView(a);
            if (!_frevvo.baseView.isEditing()) {
                if (!!window.HTMLCanvasElement) {
                    var b = this.doIt.bindAsObserver(this, a);
                    FEvent.observe(c.$element(a), "click", b);
                    FEvent.observe(c.$element(a), "keydown", b);
                } else {
                    Element.addClassName(c.$signatureHolder(a), "hide");
                    var d = c.$signatureWarning(a);
                    Element.removeClassName(d, "hide");
                    Element.addClassName(d, "show");
                }
            }
        },
        doIt: function(a, b) {
            if (_frevvo.uberController.event.isActivationEvent(a)) {
                if (a) {
                    Event.stop(a);
                }
                if (!Element.hasClassName(b, "s-disabled")) {
                    _frevvo.lightBoxView.showPage(_frevvo.forms.controlUrl(_frevvo.baseView.getView(b).getControlId(b)) + "/signature", _frevvo.localeStrings.pleaseSign, "s-signature-lightbox", {
                        doIt: _frevvo.signatureController.done,
                        callback: _frevvo.signatureController.dialogReady,
                        absolutePosition: _frevvo.uberController.mobile ? false : true,
                        refEl: _frevvo.signatureView.$element(b),
                        signatureControl: b
                    }, null, _frevvo.signatureView.$element(b));
                }
            }
        }
    },
    done: function(b) {
        var a = _frevvo.baseView.getView(b.signatureControl);
        if (!_frevvo.signatureController.isClear) {
            var c = _frevvo.signatureController.canvas.toDataURL("image/png");
            a.setSignatureImg(b.signatureControl, c);
        } else {
            a.setSignatureImg(b.signatureControl, null);
        }
        _frevvo.model.updateControlState(b.signatureControl, _frevvo.uberController.valueChangeSuccess, _frevvo.uberController.valueChangeFailed);
        _frevvo.lightBoxView.dismiss();
    },
    dialogReady: function(b) {
        _frevvo.signatureController.initSignature("signaturecanvas");
        var a = _frevvo.signatureController.cancel.bindAsObserver(this, b);
        FEvent.observe($("cancel_btn").parentNode.parentNode, "click", a);
        FEvent.observe($("cancel_btn").parentNode.parentNode, "keydown", a);
        a = _frevvo.signatureController.onClearCanvas.bindAsObserver(_frevvo.signatureController);
        FEvent.observe($("clear_signature").parentNode.parentNode, "click", a);
        FEvent.observe($("clear_signature").parentNode.parentNode, "keydown", a);
        FEvent.observe($("signature-text-input"), "keypress", function(c) {
            setTimeout(function() {
                this.clearCanvas();
                _frevvo.signatureController.isClear = false;
                _frevvo.signatureController.context.font = "70px Arial";
                _frevvo.signatureController.context.fillText($("signature-text-input").value, 20, 85);
            }.bind(this), 20);
        }.bindAsObserver(_frevvo.signatureController));
        $("signature-text-input").focus();
    },
    onClearCanvas: function(a) {
        if (_frevvo.uberController.event.isActivationEvent(a)) {
            this.clearCanvas();
            $("signature-text-input").value = "";
        }
    },
    clearCanvas: function() {
        _frevvo.signatureController.context.clearRect(0, 0, _frevvo.signatureController.canvas.width, _frevvo.signatureController.canvas.height);
        _frevvo.signatureController.clickX.clear();
        _frevvo.signatureController.clickY.clear();
        _frevvo.signatureController.clickDrag.clear();
        _frevvo.signatureController.isClear = true;
    },
    cancel: function(a, b) {
        if (_frevvo.uberController.event.isActivationEvent(a)) {
            _frevvo.lightBoxView.dismiss(a);
        }
    },
    initSignature: function(b) {
        this.canvas = document.getElementById(b);
        this.context = this.canvas.getContext("2d");
        this.clickX = new Array();
        this.clickY = new Array();
        this.clickDrag = new Array();
        this.paint = false;
        this.isClear = true;
        this.offsetL = null;
        this.offsetT = null;
        _frevvo.signatureController.context.scale(1, 1);
        var a = document.getElementById("existing-signature");
        if (a) {
            a.onload = function() {
                _frevvo.signatureController.context.drawImage(a, 0, 0);
                _frevvo.signatureController.isClear = false;
            };
            _frevvo.signatureController.context.drawImage(a, 0, 0);
            _frevvo.signatureController.isClear = false;
        }
        this.canvas.addEventListener("touchmove", function(c) {
            c.preventDefault();
        }, false);
        _frevvo.signatureController.attachHandlers();
    },
    getMousePos: function(a) {
        var b = this.canvas.getBoundingClientRect();
        return {
            x: a.clientX - b.left,
            y: a.clientY - b.top
        };
    },
    attachHandlers: function() {
        if (document.addEventListener) {
            var a = function(g) {
                if (!this.preventMouseEvents) {
                    var f = _frevvo.signatureController.getMousePos(g);
                    _frevvo.signatureController.paint = true;
                    _frevvo.signatureController.addClick(f.x, f.y);
                    _frevvo.signatureController.redraw();
                }
            };
            var c = function(g) {
                if (_frevvo.signatureController.paint && !this.preventMouseEvents) {
                    var f = _frevvo.signatureController.getMousePos(g);
                    _frevvo.signatureController.addClick(f.x, f.y, true);
                    _frevvo.signatureController.redraw();
                }
            };
            var d = function(f) {
                if (!this.preventMouseEvents) {
                    _frevvo.signatureController.paint = false;
                }
                this.preventMouseEvents = false;
            };
            var b = function(f) {
                if (!this.preventMouseEvents) {
                    _frevvo.signatureController.paint = false;
                }
            };
            if (window.navigator.msPointerEnabled) {
                this.canvas.addEventListener("MSPointerDown", a, false);
                this.canvas.addEventListener("MSPointerMove", c, false);
                this.canvas.addEventListener("MSPointerUp", d, false);
                this.canvas.addEventListener("MSPointerOut", b, false);
            } else {
                this.canvas.addEventListener("mousedown", a, false);
                this.canvas.addEventListener("mousemove", c, false);
                this.canvas.addEventListener("mouseup", d, false);
                this.canvas.addEventListener("mouseleave", b, false);
            }
            this.canvas.addEventListener("touchstart", function(g) {
                if (g.targetTouches.length > 0) {
                    this.preventMouseEvents = true;
                    var h = g.targetTouches[0];
                    var f = _frevvo.signatureController.getMousePos(h);
                    _frevvo.signatureController.paint = true;
                    _frevvo.signatureController.addClick(f.x, f.y);
                    _frevvo.signatureController.redraw();
                }
                g.preventDefault();
            }, false);
            this.canvas.addEventListener("touchmove", function(g) {
                if (g.targetTouches.length > 0) {
                    var h = g.targetTouches[0];
                    if (_frevvo.signatureController.paint) {
                        var f = _frevvo.signatureController.getMousePos(h);
                        _frevvo.signatureController.addClick(f.x, f.y, true);
                        _frevvo.signatureController.redraw();
                    }
                }
                g.preventDefault();
            }, false);
            this.canvas.addEventListener("touchend", function(f) {
                this.preventMouseEvents = false;
                _frevvo.signatureController.paint = false;
                f.preventDefault();
            }, false);
            this.canvas.addEventListener("touchcancel", function(f) {
                this.preventMouseEvents = false;
                _frevvo.signatureController.paint = false;
            }, false);
        }
    },
    redraw: function() {
        _frevvo.signatureController.context.strokeStyle = "#000000";
        _frevvo.signatureController.context.lineJoin = "round";
        _frevvo.signatureController.context.lineWidth = 3;
        for (var a = 0; a < _frevvo.signatureController.clickX.length; a++) {
            _frevvo.signatureController.context.beginPath();
            if (_frevvo.signatureController.clickDrag[a] && a) {
                _frevvo.signatureController.context.moveTo(_frevvo.signatureController.clickX[a - 1], _frevvo.signatureController.clickY[a - 1]);
            } else {
                _frevvo.signatureController.context.moveTo(_frevvo.signatureController.clickX[a] - 1, _frevvo.signatureController.clickY[a]);
            }
            _frevvo.signatureController.context.lineTo(_frevvo.signatureController.clickX[a], _frevvo.signatureController.clickY[a]);
            _frevvo.signatureController.context.closePath();
            _frevvo.signatureController.context.stroke();
        }
    },
    addClick: function(a, c, b) {
        _frevvo.signatureController.isClear = false;
        _frevvo.signatureController.clickX.push(a);
        _frevvo.signatureController.clickY.push(c);
        _frevvo.signatureController.clickDrag.push(b);
    }
};
_frevvo.uploadController = {
    click: {
        setup: function(a) {
            if (!_frevvo.uberController.editMode || Element.hasClassName(_frevvo.uploadView.$element(a), "s-disabled")) {
                _frevvo.uploadView.observeClick(a, this.doIt.bindAsObserver(this, a));
            }
            _frevvo.uberController.setup(a);
        },
        doIt: function(a, b) {
            if (_frevvo.uberController.event.isActivationEvent(a)) {
                if (a) {
                    Event.stop(a);
                }
                if (Element.hasClassName(_frevvo.uploadView.$element(b), "s-disabled")) {
                    return;
                } else {
                    _frevvo.lightBoxView.showPage(_frevvo.forms.controlUrl(_frevvo.baseView.getControlId(b)) + "/upload", _frevvo.localeStrings.uploadfile, "s-upload-lightbox", {
                        el: b,
                        absolutePosition: _frevvo.uberController.mobile ? false : true,
                        refEl: _frevvo.uploadView.$element(b),
                        callback: _frevvo.uploadController.click.dialogReady,
                        doIt: _frevvo.uploadController.click.upload
                    }, null, _frevvo.uploadView.$element(b));
                }
            }
        },
        dialogReady: function(a) {},
        upload: function(a) {
            Element.hide(_frevvo.wizardController.finishBtn);
            Element.hide($("upload-file-error"));
            Element.show($("upload-file-feedback"));
            var c = document.forms.frevvo_file_form.getAttribute("actionref");
            var d = _frevvo.forms.formUrl();
            var b = d.lastIndexOf("/");
            document.forms.frevvo_file_form.action = d.substring(0, b + 1) + c;
            document.forms.frevvo_file_form.submit();
        },
        waitForUpload: function(controlId, status, message, fileListJson, json, totalAttachments) {
            if (status & FrevvoConstants.SAVE_SUCCESS) {
                _frevvo.lightBoxView.dismiss(null);
                var el = _frevvo.baseView.getControl(controlId);
                if (fileListJson) {
                    var fileList = eval(fileListJson);
                    if (fileList) {
                        var tabindex = _frevvo.uploadView.$element(el).getAttribute("tabindex");
                        for (var i = 0; i < fileList.length; i++) {
                            _frevvo.uploadController.click.addFileEntry(_frevvo.uploadView.$fileList(el), fileList[i].fileName, fileList[i].fullName, fileList[i].fileId, fileList[i].type, tabindex);
                        }
                    }
                }
                if (json) {
                    var jstate = eval(json);
                    if (jstate) {
                        _frevvo.jsonProcessor.updateStates(jstate);
                    }
                }
            } else {
                Element.hide($("upload-file-feedback"));
                var erEl = $("upload-file-error");
                erEl.innerHTML = message.length <= 60 ? message : message.substr(0, 56).concat("...");
                erEl.title = message;
                Element.show(erEl);
                Element.show(_frevvo.wizardController.finishBtn);
            }
        },
        addFileEntry: function(k, i, c, a, j, f) {
            var h = document.createElement("DIV");
            var d = document.createElement("DIV");
            h.appendChild(d);
            var e = document.createElement("A");
            e.target = "_new";
            e.href = _frevvo.forms.formUrl() + "/document/" + a;
            e.setAttribute("id", a);
            if (c) {
                e.setAttribute("title", c);
            }
            e.setAttribute("tabindex", f);
            e.setAttribute("aria-label", _frevvo.localeStrings.uploaded_file + " " + c);
            e.setAttribute("role", "link");
            e.innerHTML = i + (j ? '<span class="f-type"> (' + j + ")<span>" : "");
            Element.addClassName(e, "f-file-upload-file-name");
            d.appendChild(e);
            var b = document.createElement("DIV");
            h.appendChild(b);
            var l = document.createElement("A");
            l.href = "";
            l.setAttribute("tabindex", f);
            l.setAttribute("aria-label", _frevvo.localeStrings.remove_upload_file + " " + c);
            l.setAttribute("role", "button");
            Element.addClassName(l, "f-upload-file-list-delete fontawesome icon-minus-sign");
            b.appendChild(l);
            var g = function(m) {
                if (_frevvo.uberController.event.isActivationEvent(m)) {
                    if (m) {
                        Event.stop(m);
                    }
                    var p = this.parentNode.parentNode.parentNode.parentNode;
                    var q = this.parentNode.parentNode;
                    var n = this.parentNode.parentNode.parentNode;
                    var o = _frevvo.protoView.$childByName(_frevvo.protoView.$childByName(q, "div"), "a");
                    o.id = "";
                    this.disabled = true;
                    _frevvo.model.updateControlState(p, function(u, s, r) {
                        n.removeChild(q);
                        _frevvo.uberController.valueChangeSuccess(u, s, r);
                        _frevvo.uploadView.$element(p).focus();
                    }, _frevvo.uberController.valueChangeFailed);
                    return false;
                }
                return true;
            };
            l.onclick = g;
            l.onkeydown = g;
            e.onkeydown = function(m) {
                if (_frevvo.uberController.event.isActivationEvent(m)) {
                    if (m) {
                        Event.stop(m);
                    }
                    e.click();
                }
            };
            k.appendChild(h);
        }
    }
};
_frevvo.groupController = {
    setupChildren: function(d) {
        var a = _frevvo.baseView.getView(d);
        if (!d.isSetupChildren) {
            a.observeKbNavigation(d);
        }
        var c = _frevvo.baseView.getView(d).getControls(d);
        if (c && c.length > 0) {
            for (var b = 0; b < c.length; b++) {
                if (_frevvo.sectionView.is(c[b])) {
                    if (!c[b].isSetup) {
                        _frevvo.sectionController.setup(c[b]);
                        this.expandedSetupChildren(c[b]);
                    }
                } else {
                    if (_frevvo.phoneGroupView.is(c[b])) {
                        if (!c[b].isSetup) {
                            _frevvo.phoneGroupController.setup(c[b]);
                            this.expandedSetupChildren(c[b]);
                        }
                    } else {
                        if (_frevvo.pageBreakView.is(c[b])) {
                            if (!c[b].isSetup) {
                                _frevvo.pageBreakController.setup(c[b]);
                                this.expandedSetupChildren(c[b]);
                            }
                        } else {
                            if (_frevvo.repeatView.is(c[b])) {
                                if (!c[b].isSetup) {
                                    RepeatController.setup(c[b]);
                                    this.expandedSetupChildren(c[b]);
                                }
                            } else {
                                if (_frevvo.tableView.is(c[b])) {
                                    if (!c[b].isSetup) {
                                        _frevvo.tableController.setup(c[b]);
                                        _frevvo.tableHeadController.setup(_frevvo.tableView.$tablehead(c[b]));
                                        this.expandedSetupChildren(c[b]);
                                    }
                                } else {
                                    if (_frevvo.tableRowView.is(c[b])) {
                                        if (!c[b].isSetup) {
                                            _frevvo.tableRowController.setup(c[b]);
                                            this.expandedSetupChildren(c[b]);
                                        }
                                    } else {
                                        if (_frevvo.panelView.is(c[b])) {
                                            if (!c[b].isSetup) {
                                                _frevvo.uberController.setup(c[b]);
                                                this.expandedSetupChildren(c[b]);
                                            }
                                        } else {
                                            if (_frevvo.switchControlView.is(c[b])) {
                                                if (!c[b].isSetup) {
                                                    _frevvo.uberController.setup(c[b]);
                                                    _frevvo.switchControlController.setupChildren(c[b]);
                                                }
                                            } else {
                                                if (_frevvo.submitView.is(c[b])) {
                                                    if (!c[b].isSetup) {
                                                        _frevvo.submitController.setup(c[b]);
                                                    }
                                                } else {
                                                    if (_frevvo.triggerView.is(c[b])) {
                                                        if (!c[b].isSetup) {
                                                            _frevvo.triggerController.click.setup(c[b]);
                                                        }
                                                    } else {
                                                        if (_frevvo.uploadView.is(c[b])) {
                                                            if (!c[b].isSetup) {
                                                                _frevvo.uploadController.click.setup(c[b]);
                                                            }
                                                        } else {
                                                            if (!c[b].isSetup) {
                                                                _frevvo.uberController.setup(c[b]);
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    },
    expandedSetupChildren: function(a) {
        if (_frevvo.groupView.isExpanded(a)) {
            this.setupChildren(a);
            a.isSetupChildren = true;
        }
    },
    tearDownChildren: function(c) {
        var b = _frevvo.baseView.getView(c).getControls(c);
        if (b && b.length > 0) {
            for (var a = 0; a < b.length; a++) {
                _frevvo.uberController.tearDown(b[a]);
                if (_frevvo.sectionView.is(b[a]) || _frevvo.phoneGroupView.is(b[a]) || _frevvo.pageBreakView.is(b[a]) || _frevvo.repeatView.is(b[a]) || _frevvo.tableView.is(b[a]) || _frevvo.tableRowView.is(b[a]) || _frevvo.panelView.is(b[a])) {
                    this.tearDownChildren(b[a]);
                }
            }
        }
    },
    navigationObserver: function(g, b, k, c) {
        if (g.type == "keydown") {
            var p = null;
            var d = null;
            var t = null;
            if ((g.key == "Right" || g.keyIdentifier == "Right") && g.shiftKey && !b.navActive) {
                b.navActive = true;
                p = "Enter";
                d = "-1";
                t = "0";
                Event.stop(g);
            } else {
                if ((g.key == "Left" || g.keyIdentifier == "Left") && g.shiftKey && b.navActive) {
                    b.navActive = false;
                    c && c.focus();
                    p = "Exit";
                    d = "0";
                    t = "-1";
                    Event.stop(g);
                } else {
                    if (g.keyCode == 9 && b.navActive) {
                        p = "Navigation";
                        d = "-1";
                        t = "0";
                    }
                }
            }
            if (p) {
                if (!k) {
                    k = [];
                    k[0] = b.id;
                }
                var r = "";
                for (var s = 0; s < k.length; s++) {
                    if (r.length > 0) {
                        r += ", ";
                    }
                    r += "#" + k[s] + " *[tabindex='" + d + "']";
                }
                var o = document.querySelectorAll(r);
                var u = "#" + b.id;
                var a = [];
                for (var q = 0; q < o.length; q++) {
                    a.push(o[q]);
                }
                var l = [];
                var h = 0;
                l[h++] = document.querySelectorAll(u + " .f-section .c-section *[tabindex='" + d + "']");
                l[h++] = document.querySelectorAll(u + " .f-section .h-section .f-expand[tabindex='" + d + "']");
                l[h++] = document.querySelectorAll(u + " .f-switch .c-switch *[tabindex='" + d + "']");
                if (c) {
                    var m = [];
                    m[0] = c;
                    l[h++] = m;
                }
                for (var s = 0; s < l.length; s++) {
                    for (var f = 0; f < l[s].length; f++) {
                        var e = -1;
                        for (var q = 0; q < a.length; q++) {
                            if (a[q] == l[s][f]) {
                                e = q;
                                break;
                            }
                        }
                        if (e != -1) {
                            a.splice(e, 1);
                        }
                    }
                }
                for (var s = 0; s < a.length; s++) {
                    a[s].setAttribute("tabIndex", t);
                }
                if (p == "Enter") {
                    if (c && c == document.activeElement) {
                        a.length >= 1 && a[0].focus();
                        setTimeout(_frevvo.groupController.checkFocus.bind(_frevvo.groupController, true, 0, a, a.length, b, c), 50);
                    }
                }
            }
        }
    },
    checkFocus: function(e, a, f, c, d, b) {
        c--;
        if (c <= 0) {
            return;
        }
        if (f[a] == document.activeElement) {
            return;
        }
        if (e) {
            a++;
            if (a >= f.length) {
                d.navActive = false;
                b && b.focus();
                return;
            }
            f[a].focus();
        } else {
            a--;
            if (a < 0) {
                d.navActive = false;
                b && b.focus();
            }
            f[a].focus();
        }
        setTimeout(_frevvo.groupController.checkFocus.bind(_frevvo.groupController, e, a, f, c, d, b), 50);
    }
};
_frevvo.sectionController = {};
Object.extend(_frevvo.sectionController, _frevvo.groupController);
Object.extend(_frevvo.sectionController, {
    setup: function(a) {
        a = $(a);
        _frevvo.uberController.setup(a);
        _frevvo.sectionView.observeExpandCollapse(a, this.expandCollapseObserver.bindAsObserver(this, a));
        if (_frevvo.sectionView.isSignable(a) && _frevvo.sectionView.isAuthorized(a)) {
            _frevvo.formController.sign.setup(_frevvo.sectionView.getSignButton(a));
        }
        a.isSetup = true;
    },
    expandCollapseObserver: function(a, b) {
        if (a.type == "click" || (a.type == "keydown" && (a.keyCode == "13" || a.keyCode == "32"))) {
            if ((!_frevvo.sectionView.isExpanded(b)) && (!b.isSetupChildren)) {
                _frevvo.groupController.setupChildren(b);
                b.isSetupChildren = true;
            }
            _frevvo.sectionView.onExpandCollapse(b);
        }
    }
});
_frevvo.phoneGroupController = {};
Object.extend(_frevvo.phoneGroupController, _frevvo.groupController);
Object.extend(_frevvo.phoneGroupController, {
    setup: function(a) {
        a = $(a);
        _frevvo.uberController.setup(a);
        _frevvo.phoneGroupView.observeExpandCollapse(a, this.expandCollapseObserver.bindAsObserver(this, a));
        a.isSetup = true;
    },
    expandCollapseObserver: function(a, b) {
        if ((!_frevvo.phoneGroupView.isExpanded(b)) && (!b.isSetupChildren)) {
            _frevvo.groupController.setupChildren(b);
            b.isSetupChildren = true;
        }
        _frevvo.phoneGroupView.onExpandCollapse(b);
    }
});
_frevvo.pageBreakController = {
    setup: function(a) {
        a = $(a);
        _frevvo.uberController.setup(a);
        a.isSetup = true;
    }
};
_frevvo.switchControlController = {
    setupChildren: function(b) {
        var d = _frevvo.baseView.getView(b).$switchCases(b);
        if (d && d.length > 0) {
            for (var a = 0; a < d.length; a++) {
                _frevvo.switchCaseController.setup(d[a]);
                if (_frevvo.switchCaseView.isSelected(d[a])) {
                    _frevvo.switchCaseController.setupChildren(d[a]);
                    d[a].isSetupChildren = true;
                }
            }
        }
        if (_frevvo.uberController.mobile) {
            var e = _frevvo.switchControlView.$switchCaseContainer(b);
            if (e && Element.hasClassName(e, "s-mobile-content")) {
                this.setupNavBtns(b);
            }
        }
        b.isSetupChildren = true;
    },
    tearDownChildren: function(b) {
        var c = _frevvo.baseView.getView(b).$switchCases(b);
        if (c && c.length > 0) {
            for (var a = 0; a < c.length; a++) {
                _frevvo.uberController.tearDown(c[a]);
                _frevvo.groupController.tearDownChildren(c[a]);
            }
        }
    },
    setupNavBtns: function(b) {
        var c = $("sw-nav-buttons");
        var d = _frevvo.protoView.$childById(_frevvo.protoView.$childById(c, "sw-action-group"), "sw-action-menu");
        var a = _frevvo.protoView.$childById(_frevvo.protoView.$childById(c, "sw-action-group"), "sw-nav-next");
        if (a) {
            FEvent.observe(a, "click", this.backNextObserver.bindAsObserver(this, b, true, false));
        }
        var a = _frevvo.protoView.$childById(_frevvo.protoView.$childById(c, "sw-action-group"), "sw-action-menu-btn");
        if (a) {
            FEvent.observe(a, "click", this.menuObserver.toggleIt.bindAsObserver(this, b));
        }
        a = _frevvo.protoView.$childById(c, "sw-nav-back");
        if (a) {
            FEvent.observe(a, "click", this.backNextObserver.bindAsObserver(this, b, false, false));
        }
        a = _frevvo.protoView.$childByName(_frevvo.protoView.$childById(d, "sw-nav-cancel"), "a");
        if (!a) {
            a = _frevvo.protoView.$childById(_frevvo.protoView.$childById(c, "sw-action-group"), "sw-nav-cancel");
        }
        if (a) {
            FEvent.observe(a, "click", this.cancelObserver.doIt.bindAsObserver(this, a));
        }
        a = _frevvo.protoView.$childByName(_frevvo.protoView.$childById(d, "sw-nav-reject"), "a");
        if (a) {
            FEvent.observe(a, "click", this.rejectObserver.doIt.bindAsObserver(this, a));
        }
        a = _frevvo.protoView.$childByName(_frevvo.protoView.$childById(d, "sw-nav-finish"), "a");
        if (a) {
            FEvent.observe(a, "click", this.fastForwardObserver.doIt.bindAsObserver(this, a));
        }
        a = _frevvo.protoView.$childById(c, "sw-nav-save");
        if (a) {
            FEvent.observe(a, "click", this.saveObserver.doIt.bindAsObserver(this, a));
        }
    },
    menuObserver: {
        toggleIt: function(a, b) {
            _frevvo.switchControlView.showHideMenu(b);
        }
    },
    saveObserver: {
        doIt: function(a, b) {
            _frevvo.switchControlView.hideMenu();
            if (_frevvo.uberController.flow) {
                FlowController.save.doIt.bind(FlowController.save)(a, b);
            } else {
                _frevvo.formController.save.doIt.bind(_frevvo.formController.save)(a, b);
            }
        }
    },
    cancelObserver: {
        doIt: function(a, c) {
            _frevvo.switchControlView.hideMenu();
            var b = _frevvo.phoneController.cancelUrl;
            if (b) {
                if (window.Flows) {
                    Flows.dispose(_frevvo.switchControlController.cancelObserver.success, _frevvo.switchControlController.cancelObserver.failure, {
                        el: c,
                        purl: b
                    });
                } else {
                    if (window._frevvo.forms) {
                        _frevvo.forms.dispose(_frevvo.switchControlController.cancelObserver.success, _frevvo.switchControlController.cancelObserver.failure, {
                            el: c,
                            purl: b
                        });
                    }
                }
            }
        },
        success: function(c, b, a) {
            window.location.href = a.purl;
        },
        failure: function(c, b, a) {
            window.location.href = a.purl;
        }
    },
    rejectObserver: {
        doIt: function(a, b) {
            _frevvo.switchControlView.hideMenu();
            FlowController.reject.handleClick(FlowView.getRejectButton(), true);
        }
    },
    fastForwardObserver: {
        doIt: function(a, b) {
            _frevvo.switchControlView.hideMenu();
            var c = _frevvo.protoView.$childByName($("sw-nav-finish"), "a");
            if (Element.hasClassName(c, "s-disabled")) {
                return;
            }
            _frevvo.switchControlController.disableNextNavBtn();
            FlowController.fastForward.handleClick(FlowView.getFastForwardButton());
        }
    },
    backNextObserver: function(i, a, h, c) {
        if (i) {
            Event.stop(i);
        }
        _frevvo.phoneController.hideMobileStatus();
        var b = null;
        if (a) {
            b = _frevvo.switchControlController.getTargetCase(a, h);
        }
        if (b) {
            window.scrollTo(0, 1);
            _frevvo.switchCaseController.activate(b);
            if (_frevvo.phoneController.hiddenPages.indexOf(b.getAttribute("id")) > -1) {
                var f = _frevvo.switchControlView.$switchCases(a);
                if ((h && f.indexOf(b) == f.length - 1) || !h && f.indexOf(b) == 0) {
                    var e = _frevvo.switchCaseView.$content(b);
                    Element.hide(e);
                    _frevvo.phoneController.showMobileStatus(_frevvo.localeStrings.pageBlank);
                } else {
                    _frevvo.switchControlController.backNextObserver(i, a, h, c);
                    return;
                }
            }
        } else {
            if (!c && h) {
                if (_frevvo.formView.isValid(_frevvo.formView.getForm())) {
                    _frevvo.switchControlController.disableNextNavBtn();
                    if (_frevvo.uberController.flow) {
                        FlowController.next.handleClick(FlowView.getFlowButton());
                    } else {
                        var d = _frevvo.submitView.doSubmit("Submit", _frevvo.phoneController.submitName);
                        if (!d) {
                            _frevvo.switchControlController.enableNextNavBtn();
                        }
                    }
                }
            } else {
                if (!c) {
                    var g = $("sw-nav-back");
                    if (Element.hasClassName(g, "s-disabled")) {
                        return;
                    }
                    Element.addClassName(g, "s-disabled");
                    if (Element.hasClassName(document.body, "tablet")) {
                        g.value = _frevvo.localeStrings.pleasewait;
                    }
                    if (_frevvo.uberController.flow) {
                        FlowController.previous.handleClick(FlowView.getPreviousButton());
                    }
                }
            }
        }
    },
    disableNextNavBtn: function() {
        var a = $("sw-nav-next");
        if (a) {
            Element.addClassName(a, "s-disabled");
            a.value = _frevvo.localeStrings.pleasewait;
        }
    },
    enableNextNavBtn: function() {
        var a = $("sw-nav-next");
        if (a) {
            Element.removeClassName(a, "s-disabled");
            if (Element.hasClassName(document.body, "tablet")) {
                a.value = _frevvo.phoneController.submitLabel;
            }
        }
    },
    getTargetCase: function(c, d) {
        var a = _frevvo.switchControlView.$switchCases(c);
        for (var b = 0; b < a.length; b++) {
            if (Element.hasClassName(a[b], FrevvoConstants.SELECTED_CLASS)) {
                if (d && b < a.length - 1) {
                    _frevvo.phoneController.curPage++;
                    _frevvo.switchControlController.manageNavBar();
                    return a[b + 1];
                } else {
                    if (!d && b > 0) {
                        _frevvo.phoneController.curPage--;
                        _frevvo.switchControlController.manageNavBar();
                        return a[b - 1];
                    }
                }
            }
        }
        return null;
    },
    manageNavBarSubmit: function(a) {
        if (_frevvo.uberController.mobile && _frevvo.phoneController.curPage === _frevvo.phoneController.pageCount) {
            if (a === "true") {
                Element.removeClassName($("sw-nav-next"), "s-disabled");
                if (_frevvo.uberController.flow) {
                    Element.removeClassName(_frevvo.protoView.$childByName($("sw-nav-finish"), "a"), "s-disabled");
                }
            } else {
                Element.addClassName($("sw-nav-next"), "s-disabled");
                if (_frevvo.uberController.flow) {
                    Element.addClassName(_frevvo.protoView.$childByName($("sw-nav-finish"), "a"), "s-disabled");
                }
            }
        }
    },
    manageNavBar: function() {
        var i;
        var url;
        var x = [];
        var previous = false;
        if (_frevvo.phoneController.curPage === _frevvo.phoneController.pageCount) {
            $("sw-nav-next").value = _frevvo.phoneController.submitLabel;
            if (!_frevvo.formView.isValid(_frevvo.formView.getForm())) {
                Element.addClassName($("sw-nav-next"), "s-disabled");
            }
            Element.addClassName($("sw-nav-next"), "s-submit");
        } else {
            $("sw-nav-next").value = _frevvo.phoneController.nextLabel;
            Element.removeClassName($("sw-nav-next"), "s-disabled");
            Element.removeClassName($("sw-nav-next"), "s-submit");
        }
        if (_frevvo.phoneController.curPage === 1) {
            if (_frevvo.uberController.flow && FlowView.getPreviousButton()) {
                $("sw-nav-back").value = _frevvo.phoneController.previousLabel;
                url = _frevvo.forms.formUrl();
                url = url.substr(0, url.length - "/current".length);
                url += "/operations";
                _frevvo.utilities.ajaxRequest.send(url, {
                    method: "get",
                    onSuccess: function(t) {
                        eval("x=" + t.responseText);
                        if (x && x.length > 0) {
                            for (i = 0; i < x.length; i++) {
                                if (x[i] === "previous") {
                                    previous = true;
                                    break;
                                }
                            }
                        }
                        if (!previous) {
                            Element.addClassName($("sw-nav-back"), "s-disabled");
                        }
                    }
                });
            } else {
                $("sw-nav-back").value = _frevvo.phoneController.backLabel;
                Element.addClassName($("sw-nav-back"), "s-disabled");
            }
        } else {
            $("sw-nav-back").value = _frevvo.phoneController.backLabel;
            Element.removeClassName($("sw-nav-back"), "s-disabled");
        }
        var indicators = _frevvo.protoView.$childrenByClass($("sw-nav-indicators"), "sw-nav-indicator");
        if (indicators) {
            for (var i = 0; i < indicators.length; i++) {
                if (i == (_frevvo.phoneController.curPage - 1)) {
                    Element.addClassName(indicators[i], "s-selected");
                } else {
                    Element.removeClassName(indicators[i], "s-selected");
                }
            }
        }
        if ($("sw-nav-reject")) {
            $("sw-nav-reject").value = _frevvo.phoneController.rejectLabel;
        }
    }
};
_frevvo.switchCaseController = {};
Object.extend(_frevvo.switchCaseController, _frevvo.groupController);
Object.extend(_frevvo.switchCaseController, {
    view: _frevvo.switchCaseView,
    setup: function(a) {
        a = $(a);
        try {
            if (SwitchCasePaletteView.is(a)) {
                return;
            }
        } catch (b) {}
        _frevvo.uberController.setup(a);
        if ((_frevvo.switchCaseView != null) && (_frevvo.switchCaseView.isEditing(a))) {
            SwitchCaseEditController.setup(a);
        }
        _frevvo.switchCaseView.observeSelection(a, this.selectionObserver.bindAsObserver(this, a));
    },
    selectionObserver: function(a, b) {
        if (_frevvo.uberController.event.isActivationEvent(a)) {
            if (a) {
                Event.stop(a);
            }
            _frevvo.switchCaseController.activate(b);
        } else {
            if (a.type == "keydown" && (a.key == "Right" || a.keyIdentifier == "Right") && a.shiftKey) {
                _frevvo.switchCaseController.activate(b);
            }
        }
    },
    activate: function(b) {
        var a = _frevvo.switchCaseView.$selectedCase(b);
        a && Element.setStyle(a, "visibility:hidden");
        if (!b.isSetupChildren) {
            _frevvo.switchCaseController.setupChildren(b);
            b.isSetupChildren = true;
        }
        _frevvo.switchCaseView.onFocus(b, this.success, this.failure);
        _frevvo.switchCaseView.onUnFocus(b, this.success, this.failure, a);
    },
    success: function(t, json, options) {
        var el = options.el;
        if (t.responseText) {
            var jstate = eval(t.responseText);
        }
        jstate && _frevvo.jsonProcessor.updateStates(jstate);
        _frevvo.uberController.debug.printTrace(jstate);
    },
    failure: function(c, b, a) {
        var d = new _frevvo.Errors(c.status, c.statusText);
        _frevvo.utilities.ajaxRequest.showError(d.getMessage(), "<span><strong>Tab switch failed.</strong><br/><small><em>Refreshing page...</em></small></span>", true);
    }
});
var RepeatController = {};
Object.extend(RepeatController, _frevvo.groupController);
Object.extend(RepeatController, {
    setup: function(b) {
        b = $(b);
        if (!b.isSetup) {
            _frevvo.uberController.setup(b);
            b.isSetup = null;
        }
        var a = _frevvo.baseView.getView(b);
        if (a.isEnabled(b)) {
            this.setupAddRepeatItemObservers(b);
            this.setupRemoveRepeatItemObservers(b);
        } else {
            this.removeAddRepeatItemObservers(b);
            this.removeRemoveRepeatItemObservers(b);
        }(!(b.isSetup)) && a.observeExpandCollapse(b, this.expandCollapseObserver.bindAsObserver(this, b));
        b.isSetup = true;
    },
    tearDown: function(a) {
        _frevvo.uberController.tearDown(a);
    },
    expandCollapseObserver: function(a, b) {
        if ((!_frevvo.repeatView.isExpanded(b)) && (!b.isSetupChildren)) {
            _frevvo.groupController.setupChildren(b);
            b.isSetupChildren = true;
        }
        _frevvo.repeatView.onExpandCollapse(b);
    },
    removeAddRepeatItemObservers: function(c) {
        var a = _frevvo.baseView.getView(c);
        var d = a.$repeatItems(c);
        for (var b = 0; b < d.length; b++) {
            var e = _frevvo.baseView.getView(d[b]);
            e.unObserveAddItem(d[b]);
        }
    },
    setupAddRepeatItemObservers: function(c) {
        var a = _frevvo.baseView.getView(c);
        var e = a.$repeatItems(c);
        if ((a.getMaxOccurs(c) < 0) || (e.length < a.getMaxOccurs(c))) {
            for (var b = 0; b < e.length; b++) {
                var d = _frevvo.baseView.getView(e[b]);
                if (_frevvo.baseView.isTyped(e[b]) && _frevvo.baseView.isEditing()) {
                    d.observeAddItem(e[b], function() {
                        _frevvo.lightBoxView.showInfoDialog(a.makeNode("<span>" + _frevvo.localeStrings.addSchemeGenCtrlErr + ' <a target="_blank" href="http://docs.frevvo.com/d/display/frevvo/Data+Sources#DataSources-ModifyingControlsGeneratedfromSchemaElements">' + _frevvo.localeStrings.clickHereForMoreInfo + "</a></span>"), _frevvo.localeStrings.quickHelp);
                    });
                } else {
                    d.observeAddItem(e[b], this.addRepeatItemObserver.bindAsObserver(this, e[b]));
                }
            }
        } else {
            for (var b = 0; b < e.length; b++) {
                var f = _frevvo.baseView.getView(e[b]);
                f.unObserveAddItem(e[b]);
            }
        }
    },
    addRepeatItemObserver: function(a, b) {
        if (_frevvo.uberController.event.isActivationEvent(a)) {
            if (a != null) {
                Event.stop(a);
            }
            this.addRepeatItem(b);
        }
    },
    addRepeatItem: function(c, b) {
        _frevvo.baseView.hideActive();
        var a = _frevvo.baseView.getView(c);
        if (a != null) {
            _frevvo.utilities.setCursor(c, "wait");
            _frevvo.utilities.setCursor(a.$add(c), "wait");
            if (a.isLinked(c)) {
                _frevvo.model.addRepeatItem(c, this, this.addRepeatItemSuccess, this.addRepeatItemFailed, b);
            } else {
                this.addRepeatItemSuccess(null, {}, {
                    el: c,
                    repeatController: this
                });
            }
        }
    },
    addRepeatItemSuccess: function(t, json, options) {
        var el = options.el;
        var newEl = null;
        var jstate = eval(t.responseText);
        if (jstate.controlAdded) {
            if (jstate && jstate.controlXhtml) {
                newEl = _frevvo.uberController.makeControlXhtml(jstate);
            } else {
                newEl = _frevvo.utilities.cloneElement(el);
                _frevvo.baseView.setControlId(newEl, _frevvo.utilities.util.makeId());
            }
            _frevvo.utilities.insertAfter(newEl, el);
            var view = _frevvo.baseView.getView(newEl);
            var repeatController = options.repeatController;
            var repeatControl = view.getRepeatControlForRepeatItem(el);
            repeatController.setupAddedItems(newEl, repeatController);
            repeatController.setupAddRepeatItemObservers.call(repeatController, repeatControl);
            repeatController.setupRemoveRepeatItemObservers.call(repeatController, repeatControl);
            if (view.isEditing(el)) {
                repeatController.updateMinOccursInEditContainers(_frevvo.baseView.getRepeatControlForRepeatItem(el));
            }
            _frevvo.utilities.setCursor(el, "auto");
            _frevvo.utilities.setCursor(newEl, "auto");
            _frevvo.utilities.setCursor(view.$add(el), "pointer");
            _frevvo.utilities.setCursor(view.$add(newEl), "pointer");
            if (repeatControl.onAddItem) {
                repeatControl.onAddItem(newEl);
            }
            var add = _frevvo.baseView.getView(el).$add(el);
            if (add && !Element.hasClassName(add, "s-add")) {
                var remove = _frevvo.baseView.getView(el).$remove(el);
                remove && Element.hasClassName(remove, "s-remove") && remove.focus();
            }
        }
        if (jstate && jstate.accumulatedStates) {
            _frevvo.jsonProcessor.updateStates(jstate.accumulatedStates);
        }
        _frevvo.uberController.debug.printTrace(jstate);
    },
    updateMinOccursInEditContainers: function(b) {
        if (_frevvo.tableView.is(b)) {
            var c = _frevvo.tableView.getMinOccurs(b);
            var d = _frevvo.baseView.$editContainer(b);
            BaseEditView.setMinOccurs(d, c);
        } else {
            var e = _frevvo.repeatView.$repeatItems(b);
            var c = _frevvo.repeatView.getMinOccurs(b);
            for (var a = 0; a < e.length; a++) {
                var d = _frevvo.baseView.$editContainer(e[a]);
                BaseEditView.setMinOccurs(d, c);
            }
        }
    },
    setupAddedItems: function(b, d) {
        if (_frevvo.sectionView.is(b)) {
            _frevvo.sectionController.setup(b);
            _frevvo.baseView.getView(b).observeKbNavigation(b);
            d.setupAddedChildren(_frevvo.sectionView.getControls(b), d);
        } else {
            if (_frevvo.repeatView.is(b)) {
                RepeatController.setup(b);
                d.setupAddedChildren(_frevvo.repeatView.getControls(b), d);
            } else {
                if (_frevvo.tableRowView.is(b)) {
                    _frevvo.tableRowController.setup(b);
                    d.setupAddedChildren(_frevvo.tableRowView.getControls(b), d);
                } else {
                    if (_frevvo.panelView.is(b)) {
                        d.setupAddedChildren(_frevvo.panelView.getControls(b), d);
                    } else {
                        if (_frevvo.switchControlView.is(b)) {
                            var c = _frevvo.switchControlView.$switchCases(b);
                            d.setupAddedChildren(c, d);
                        } else {
                            if (_frevvo.switchCaseView.is(b)) {
                                _frevvo.switchCaseController.setup(b);
                                d.setupAddedChildren(_frevvo.switchCaseView.getControls(b), d);
                            } else {
                                if (_frevvo.uploadView.is(b)) {
                                    _frevvo.uploadController.click.setup(b);
                                } else {
                                    if (_frevvo.signatureView.is(b)) {
                                        _frevvo.signatureController.click.setup(b);
                                    } else {
                                        _frevvo.uberController.setup(b);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        var a = _frevvo.baseView.getRepeatControlForRepeatItem(b);
        if (a) {
            d.reIndexItems(a);
        }
    },
    setupAddedChildren: function(a, c) {
        if (a && a.length > 0) {
            for (var b = 0; b < a.length; b++) {
                c.setupAddedItems(a[b], c);
            }
        }
    },
    reIndexItems: function(b) {
        var a = _frevvo.baseView.getView(b);
        var e = a.$repeatItems(b);
        for (var d = 0; d < e.length; d++) {
            var c = e[d];
            var f = _frevvo.baseView.getView(c);
            f.setRepeatItemNumber && f.setRepeatItemNumber(c, d + 1);
        }
    },
    addRepeatItemFailed: function(d, c, b) {
        var e = b.el;
        var a = _frevvo.baseView.getView(e);
        var f = new _frevvo.Errors(d.status, d.statusText);
        _frevvo.utilities.ajaxRequest.showError(f.getMessage(), "<span><strong>Add repeat item failed.</strong><br/><small><em>Refreshing page...</em></small></span>", true);
        _frevvo.utilities.setCursor(e, "auto");
        _frevvo.utilities.setCursor(b.newEl, "auto");
        _frevvo.utilities.setCursor(a.$add(e), "pointer");
        _frevvo.utilities.setCursor(a.$add(b.newEl), "pointer");
    },
    removeRemoveRepeatItemObservers: function(c) {
        var a = _frevvo.baseView.getView(c);
        var d = a.$repeatItems(c);
        for (var b = 0; b < d.length; b++) {
            var e = _frevvo.baseView.getView(d[b]);
            e.unObserveRemoveItem(d[b]);
        }
    },
    setupRemoveRepeatItemObservers: function(c) {
        var a = _frevvo.baseView.getView(c);
        var e = a.$repeatItems(c);
        if ((e.length > a.getMinOccurs(c)) && (e.length > 1)) {
            for (var b = 0; b < e.length; b++) {
                var d = _frevvo.baseView.getView(e[b]);
                d.observeRemoveItem(e[b], this.removeRepeatItemObserver.bindAsObserver(this, e[b]));
            }
        } else {
            for (var b = 0; b < e.length; b++) {
                var f = _frevvo.baseView.getView(e[b]);
                f.unObserveRemoveItem(e[b]);
            }
        }
    },
    removeRepeatItemObserver: function(a, b) {
        if (_frevvo.uberController.event.isActivationEvent(a)) {
            if (a != null) {
                Event.stop(a);
            }
            this.removeRepeatItem(b);
        }
    },
    removeRepeatItem: function(b) {
        _frevvo.baseView.hideActive();
        var a = _frevvo.baseView.getView(b);
        if (a != null) {
            if (a.isLinked(b)) {
                _frevvo.model.removeRepeatItem(b, this, this.removeRepeatItemSuccess, this.removeRepeatItemFailed);
            } else {
                this.removeRepeatItemSuccess(null, {}, {
                    el: b,
                    repeatController: this
                });
            }
        }
    },
    removeRepeatItemSuccess: function(t, json, options) {
        var el = options.el;
        var view = _frevvo.baseView.getView(el);
        var repeatController = options.repeatController;
        var repeatControl = view.getRepeatControlForRepeatItem(el);
        var repeatItemToBeFocused = null;
        for (var i = 0; i < el.parentNode.childNodes.length; i++) {
            var repeatItem = el.parentNode.childNodes[i];
            if (repeatItem.id && repeatItem.id != el.id && (Element.hasClassName(repeatItem, "f-table-row") || Element.hasClassName(repeatItem, "f-control"))) {
                repeatItemToBeFocused = el.parentNode.childNodes[i];
                break;
            }
        }
        Element.remove(el);
        if (t && t.responseText) {
            var jstate = eval(t.responseText);
        }
        jstate && jstate.accumulatedStates && _frevvo.jsonProcessor.updateStates(jstate.accumulatedStates);
        _frevvo.uberController.debug.printTrace(jstate);
        repeatController.setupAddRepeatItemObservers.call(repeatController, repeatControl);
        repeatController.setupRemoveRepeatItemObservers.call(repeatController, repeatControl);
        if (repeatControl.onRemoveItem) {
            repeatControl.onRemoveItem(el);
        }
        if (repeatControl) {
            repeatController.reIndexItems(repeatControl);
        }
        if (repeatItemToBeFocused) {
            var add = _frevvo.baseView.getView(repeatItemToBeFocused).$add(repeatItemToBeFocused);
            if (add) {
                add.focus();
            }
        }
    },
    removeRepeatItemFailed: function(d, c, b) {
        var e = b.el;
        var a = _frevvo.baseView.getView(e);
        var f = new _frevvo.Errors(d.status, d.statusText);
        _frevvo.utilities.ajaxRequest.showError(f.getMessage(), "<span><strong>" + _frevvo.localeStrings.removeRepeatItem + ".</strong><br/><small><em>" + _frevvo.localeStrings.refreshingpage + "...</em></small></span>", true);
    }
});
_frevvo.tableController = {};
Object.extend(_frevvo.tableController, _frevvo.groupController);
Object.extend(_frevvo.tableController, {
    setup: function(d) {
        d = $(d);
        if (!d.isSetup) {
            _frevvo.uberController.setup(d);
            d.isSetup = null;
        }
        var b = _frevvo.baseView.getView(d);
        if (b.isEnabled(d)) {
            RepeatController.setupAddRepeatItemObservers(d);
            RepeatController.setupRemoveRepeatItemObservers(d);
        } else {
            RepeatController.removeAddRepeatItemObservers(d);
            RepeatController.removeRemoveRepeatItemObservers(d);
        }
        d.isSetup = true;
        var c = d.getAttribute("minOccurs");
        var a = d.getAttribute("maxOccurs");
        if (c === a) {
            Element.addClassName(d, "s-hide-addremove");
        } else {
            Element.removeClassName(d, "s-hide-addremove");
        }
        _frevvo.groupController.setupChildren(d);
        d.isSetupChildren = true;
    },
    tearDown: function(a) {
        _frevvo.uberController.tearDown(a);
    }
});
_frevvo.tableHeadController = {};
Object.extend(_frevvo.tableHeadController, {
    setup: function(c) {
        c = $(c);
        var b = _frevvo.tableHeadView.getColumnHeaders(c);
        if (b && b.length > 0) {
            for (var a = 0; a < b.length; a++) {
                if (_frevvo.tableHeadColumnView.is(b[a])) {
                    _frevvo.tableHeadColumnController.setup(b[a]);
                }
            }
        }
        if (_frevvo.uberController.editMode) {
            UberEditController.setup(c, _frevvo.tableHeadView);
        }
        c.isSetup = true;
    }
});
_frevvo.tableHeadColumnController = {
    setup: function(a) {
        if (a) {
            _frevvo.uberController.setupHelp(a, _frevvo.tableHeadColumnView);
            if (_frevvo.uberController.editMode) {
                UberEditController.setup(a, _frevvo.tableHeadColumnView);
            }
        }
    }
};
_frevvo.tableRowController = {};
Object.extend(_frevvo.tableRowController, _frevvo.groupController);
Object.extend(_frevvo.tableRowController, {
    setup: function(a) {
        a = $(a);
        _frevvo.uberController.setup(a);
        a.isSetup = true;
    }
});
_frevvo.formController = {
    rules: {
        "#form-container": function(a) {
            if (Element.hasClassName(a, "s-edit")) {
                _frevvo.uberController.editMode = true;
            }
            if (Element.hasClassName(a, "s-preview")) {
                _frevvo.uberController.previewMode = true;
            }
            if (Element.hasClassName(a, "s-print")) {
                _frevvo.uberController.printMode = true;
            }
            _frevvo.formController.setup(_frevvo.protoView.$childByName(a, "form"));
        },
        "#e-form-header": function(a) {
            _frevvo.formController.setup(a);
        }
    },
    timeoutId: null,
    view: _frevvo.formView,
    newBtn: null,
    saveBtn: null,
    switchBtn: null,
    logoutBtn: null,
    setup: function(a) {
        if (!this.view.isEditing() && !this.view.isPreview() && !this.view.isPrint()) {
            this.init.doIt(a);
        }
        if (!this.view.isEditing()) {
            this.newBtn = _frevvo.protoView.$childByClass($("f-form-tb"), "f-tb-new");
            this.newForm.setup(this.newBtn);
        }
        if (!this.view.isEditing()) {
            this.saveBtn = _frevvo.formView.getSaveButton();
            this.save.setup(this.saveBtn);
        }
        if (!this.view.isEditing()) {
            this.logoutBtn = _frevvo.formView.getLogoutButton();
            this.logout.setup(this.logoutBtn);
        }
        FEvent.observe(document.body, "click", function(b) {
            _frevvo.baseView.hideActive();
        });
        FEvent.observe(window, "message", function(b) {
            if (b.data == "DISPOSE") {
                var c = window.parent;
                if (b.source) {
                    c = b.source;
                }
                _frevvo.forms.dispose(function() {
                    c.postMessage("DISPOSAL COMPLETE", "*");
                }, function() {
                    c.postMessage("DISPOSAL COMPLETE", "*");
                }, {});
            }
        });
        FEvent.observe(a, "keypress", function(b) {
            if (b.keyCode == 13 && !_frevvo.formView.isValid(a)) {
                var c = Event.element(b);
                if ((!c || c.tagName.toLowerCase() != "textarea") && !(c.tagName.toLowerCase() == "div" && c.className.toLowerCase() == "note-editable")) {
                    Event.stop(b);
                }
            }
        });
    },
    showTimedStatus: function(a) {
        _frevvo.formController.showTimedStatusWithTimeout(a, 5000);
    },
    showTimedStatusWithTimeout: function(a, d) {
        Element.addClassName(a, "s-status");
        a.setAttribute("role", "alert");
        a.parentNode.style.clip = "auto";
        var b = [];
        for (var c = (a.childNodes.length - 1); c >= 0; c--) {
            b.push(a.childNodes[c]);
            a.removeChild(a.childNodes[c]);
        }
        for (var c = 0; c < b.length; c++) {
            a.appendChild(b[c]);
        }
        a.style.display = "none";
        a.style.display = "inline";
        if (_frevvo.formController.timeoutId) {
            clearTimeout(_frevvo.formController.timeoutId);
            _frevvo.formController.timeoutId = null;
        }
        _frevvo.formController.timeoutId = setTimeout(function() {
            a.innerHTML = "";
            a.removeAttribute("role");
            a.parentNode.style.clip = "rect(0px,0px,0px,0px)";
            Element.removeClassName(a, "s-status");
        }, d);
    },
    logout: {
        setup: function(a) {
            FEvent.observe(a, "click", this.doIt.bindAsObserver(this, a));
        },
        doIt: function(a, b) {
            Event.stop(a);
            _frevvo.forms.logout(_frevvo.formController.logout.success, _frevvo.formController.logout.failure, {
                el: b
            });
        },
        success: function(t, json, options) {
            var status = _frevvo.formView.$tbStatus();
            if (status) {
                var jstate = eval(t.responseText);
                if (jstate.status & FrevvoConstants.AUTH_SUCCESS) {
                    if (_frevvo.formController.logoutBtn) {
                        Element.removeClassName($("form-container"), "s-authenticated");
                    }
                    status.innerHTML = "Logout successful";
                    _frevvo.formController.showTimedStatus(status);
                }
            }
        },
        failure: function(c, b, a) {}
    },
    auth: {
        doIt: function(a, b) {
            _frevvo.forms.auth(_frevvo.formController.auth.success, _frevvo.formController.auth.failure, a, b);
        },
        success: function(t, json, options) {
            var status = _frevvo.formView.$tbStatus();
            if (!status) {}
            var jstate = eval(t.responseText);
            if (jstate.status & FrevvoConstants.AUTH_SUCCESS) {
                if (options.goOn) {
                    options.goOn(options.el);
                }
                return;
            } else {
                if (jstate.status & FrevvoConstants.AUTH_ANONYMOUS) {
                    if (options.goOn) {
                        options.goOn(options.el);
                    }
                    return;
                } else {
                    if (jstate.status & FrevvoConstants.AUTH_REQUIRED) {
                        var requestUrl = _frevvo.forms.userUrl() + "/auth?action=" + options.action;
                        if (_frevvo.localeStrings.locale) {
                            requestUrl += "&locale=" + _frevvo.localeStrings.locale;
                        }
                        _frevvo.lightBoxView.showPage(requestUrl, _frevvo.localeStrings.authenticationrequired, "s-saveauth-lightbox", Object.extend(options, {
                            doIt: _frevvo.formController.auth.doIt
                        }), "post");
                    } else {
                        if (jstate.status & FrevvoConstants.TRIAL_USER) {
                            if (status) {
                                status.innerHTML = _frevvo.localeStrings.notsupported + " ...";
                            }
                        } else {
                            if (jstate.status & FrevvoConstants.SESSION_EXPIRED) {
                                _frevvo.lightBoxView.showInfoDialog(_frevvo.protoView.makeNode("<span>" + _frevvo.localeStrings.sessionexpired + "</span>"), _frevvo.localeStrings.error);
                            } else {
                                if (jstate.status & FrevvoConstants.BAD_PASSWORD) {
                                    _frevvo.formController.auth.showLightBox(_frevvo.forms.userUrl() + "/auth?action=" + options.action + "&message=" + _frevvo.localeStrings.invaliduserpwd + (jstate.email ? "&email=" + jstate.email : ""), options);
                                } else {
                                    if (jstate.status & FrevvoConstants.ACCOUNT_DISABLED) {
                                        _frevvo.formController.auth.showLightBox(_frevvo.forms.userUrl() + "/auth?action=" + options.action + "&message=" + _frevvo.localeStrings.accountdisabled + (jstate.email ? "&email=" + jstate.email : ""), options);
                                    } else {
                                        if (jstate.status & FrevvoConstants.TOO_MANY_USERS) {
                                            _frevvo.formController.auth.showLightBox(_frevvo.forms.userUrl() + "/auth?action=" + options.action + "&message=" + _frevvo.localeStrings.toomanyusers + (jstate.email ? "&email=" + jstate.email : ""), options);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (status) {
                _frevvo.formController.showTimedStatus(status);
            }
        },
        failure: function(c, b, a) {},
        showLightBox: function(b, a) {
            if (_frevvo.localeStrings.locale) {
                b += "&locale=" + _frevvo.localeStrings.locale;
            }
            _frevvo.lightBoxView.showPage(b, _frevvo.localeStrings.authenticationrequired, "s-saveauth-lightbox", Object.extend(a, {
                doIt: _frevvo.formController.auth.doIt
            }), "post");
        }
    },
    newForm: {
        setup: function(a) {
            FEvent.observe(a, "click", this.doIt.bindAsObserver(this, a));
        },
        doIt: function(a, b) {
            Event.stop(a);
            _frevvo.formController.auth.doIt({
                el: b,
                goOn: this.goOn,
                action: "newForm"
            }, null);
        },
        goOn: function(a) {
            _frevvo.forms.newForm(_frevvo.formController.load.success, _frevvo.formController.load.failure, {}, null);
        },
        success: function(t, json, options) {
            var jstate = eval(t.responseText);
            if (jstate.submissionId) {
                var url = document.URL;
                url = url.replace(/form\/[A-Za-z0-9_-]+/, "form/" + jstate.submissionId);
                window.location.href = url;
            } else {
                var status = _frevvo.formView.$tbStatus();
                status.innerHTML = _frevvo.localeStrings.loadfailed;
                _frevvo.formController.showTimedStatus(status);
            }
        },
        failure: function(c, b, a) {}
    },
    save: {
        timeoutId: null,
        setup: function(a) {
            FEvent.observe(a, "click", this.doIt.bindAsObserver(this, a));
        },
        doIt: function(b, d) {
            if (_frevvo.uberController.event.isActivationEvent(b)) {
                Event.stop(b);
                if (_frevvo.formView.isReadOnly()) {
                    return;
                }
                var a = _frevvo.formView.$tbStatus();
                if (!a) {
                    a = $("f-mobile-status");
                }
                if (a) {
                    a.innerHTML = _frevvo.localeStrings.saving + "...";
                }
                var c = JSONUtil.objectToJSONString({
                    id: _frevvo.forms.formId()
                });
                _frevvo.formController.auth.doIt({
                    el: d,
                    goOn: this.goOn,
                    action: "getforms"
                }, encodeURIComponent(c));
            }
        },
        goOn: function(a) {
            _frevvo.forms.saveForm(_frevvo.formController.save.success, _frevvo.formController.save.failure, {
                el: a
            }, null);
        },
        success: function(t, json, options) {
            var status = _frevvo.formView.$tbStatus();
            if (!status) {
                status = $("f-mobile-status");
            }
            if (status) {
                var jstate = eval(t.responseText);
                if (jstate.status & FrevvoConstants.SAVE_SUCCESS) {
                    status.innerHTML = _frevvo.localeStrings.savesuccessful;
                    if (_frevvo.utilities.util.isDefined(window.CustomEventHandlers) && CustomEventHandlers.onSaveSuccess) {
                        CustomEventHandlers.onSaveSuccess(jstate.submissionId);
                    }
                } else {
                    status.innerHTML = _frevvo.localeStrings.savefailed + " " + jstate.status;
                    if (_frevvo.utilities.util.isDefined(window.CustomEventHandlers) && CustomEventHandlers.onSaveFailure) {
                        CustomEventHandlers.onSaveFailure();
                    }
                }
                _frevvo.formController.showTimedStatus(status);
                if (parent.TaskPoller) {
                    parent.TaskPoller.doRefresh();
                }
            }
        },
        failure: function(d, c, b) {
            var a = _frevvo.formView.$tbStatus();
            if (!a) {
                a = $("f-mobile-status");
            }
            if (a) {
                a.innerHTML = _frevvo.localeStrings.saveerror;
            }
            _frevvo.formController.showTimedStatus(a);
        }
    },
    sign: {
        timeoutId: null,
        setup: function(a) {
            if (!_frevvo.baseView.isEditing()) {
                FEvent.observe(a, "click", this.doIt.bindAsObserver(this, a));
                FEvent.observe(a, "keydown", this.doIt.bindAsObserver(this, a));
            }
        },
        doIt: function(b, c) {
            if (_frevvo.uberController.event.isActivationEvent(b)) {
                Event.stop(b);
                var a = c.parentNode.parentNode.parentNode;
                if (Element.hasClassName(a, "s-disabled")) {
                    return;
                }
                _frevvo.formController.auth.doIt({
                    el: c,
                    goOn: this.goOn,
                    action: "sign"
                }, null);
            }
        },
        goOn: function(d) {
            var b = d.parentNode.parentNode.parentNode;
            var a = _frevvo.baseView.getView(b);
            var e = !!window.HTMLCanvasElement;
            if (e && !a.isSigned(b) && a.isWetSignable(b)) {
                var c = ["s-signature-lightbox"];
                if ($("form-container") && Element.hasClassName($("form-container"), "l-width-thin")) {
                    c.push("l-width-thin");
                }
                _frevvo.lightBoxView.showPage(_frevvo.forms.controlUrl(_frevvo.baseView.getControlId(b)) + "/signature", _frevvo.localeStrings.pleaseSign, c, {
                    doIt: _frevvo.formController.sign.wetSignDone,
                    callback: _frevvo.signatureController.dialogReady,
                    absolutePosition: _frevvo.uberController.mobile ? false : true,
                    refEl: _frevvo.sectionView.getSignName(b),
                    sectionControl: b
                }, null, _frevvo.sectionView.$focusElement(b));
            } else {
                _frevvo.forms.signControl(_frevvo.baseView.getControlId(b), true, _frevvo.formController.sign.success, _frevvo.formController.sign.failure, {}, null);
            }
        },
        wetSignDone: function(a) {
            if (!_frevvo.signatureController.isClear) {
                var b = _frevvo.signatureController.canvas.toDataURL("image/png");
                _frevvo.forms.signControl(_frevvo.baseView.getControlId(a.sectionControl), true, _frevvo.formController.sign.success, _frevvo.formController.sign.failure, {
                    wetSignature: b.substr(22)
                }, null);
            } else {}
            _frevvo.lightBoxView.dismiss();
        },
        success: function(t, json, options) {
            if (t.responseText) {
                var jstate = eval(t.responseText);
                if (jstate) {
                    if (jstate.errMsg) {
                        _frevvo.lightBoxView.showInfoDialog(_frevvo.protoView.makeNode("<span class='lbErrorMsg'>" + jstate.errMsg + "</span>"), _frevvo.localeStrings.error);
                    } else {
                        _frevvo.jsonProcessor.updateStates(jstate);
                        _frevvo.uberController.debug.printTrace(jstate);
                    }
                }
            }
        },
        failure: function(c, b, a) {}
    },
    init: {
        showing: false,
        show: function() {
            _frevvo.lightBoxView.showWaitDialog(_frevvo.protoView.makeNode("<span><strong>" + _frevvo.localeStrings.initializingform + ".</strong><br/><small><em>" + _frevvo.localeStrings.pleasewait + "...</em></small></span>"), _frevvo.localeStrings.working + " ...");
            _frevvo.formController.init.showing = true;
        },
        doIt: function(a) {
            if (_frevvo.baseView.getForm()) {
                _frevvo.formController.timeoutId = setTimeout("_frevvo.formController.init.show()", 1000);
                _frevvo.forms.initForm(this.success, this.failure, {
                    target: a
                });
            }
        },
        success: function(t, json, options) {
            if (t.responseText) {
                var jstate = eval(t.responseText);
                if (jstate.error) {
                    if (jstate.errorTarget) {
                        if (jstate.errorTarget == "top") {
                            top.location = unescape(jstate.errorUrl);
                        } else {
                            if (jstate.errorTarget == "parent") {
                                parent.location = unescape(jstate.errorUrl);
                            } else {
                                window.location.href = unescape(jstate.errorUrl);
                            }
                        }
                    } else {
                        window.location.href = unescape(jstate.errorUrl);
                    }
                }
                if (jstate.anonymous != null) {
                    _frevvo.uberController.anonymous = true;
                }
                if (_frevvo.uberController.mobile) {
                    jstate && _frevvo.phoneController.updateStates(jstate);
                }
                jstate && _frevvo.jsonProcessor.removeControls(jstate);
                jstate && _frevvo.jsonProcessor.updateStates(jstate);
                if (jstate) {
                    if (jstate.print) {
                        var el = $("f-form-tb-print-span");
                        if (el) {
                            el.innerHTML = jstate.print;
                        }
                    }
                    if (jstate.save) {
                        var el = $("f-form-tb-save-span");
                        if (el) {
                            el.innerHTML = jstate.save;
                        }
                    }
                    if (jstate.load) {
                        var el = $("f-form-tb-load-span");
                        if (el) {
                            el.innerHTML = jstate.load;
                        }
                    }
                    if (jstate.user) {
                        var el = $("f-form-tb-logout-span");
                        if (el) {
                            el.innerHTML = jstate.user;
                            Element.show(el.parentNode);
                        }
                    }
                    if (jstate.warning) {
                        var el = $("f-form-tb");
                        if (el) {
                            var sEl = _frevvo.formView.$tbStatus();
                            sEl.innerHTML = jstate.warning;
                            Element.addClassName(sEl, "s-status");
                        }
                    }
                    _frevvo.uberController.debug.printTrace(jstate);
                }
            }
            if (_frevvo.formController.timeoutId) {
                clearTimeout(_frevvo.formController.timeoutId);
                _frevvo.formController.timeoutId = null;
            }
            if (_frevvo.formController.init.showing) {
                _frevvo.lightBoxView.hideWaitDialog();
            }
            var overlay = $("form-init-overlay");
            if (overlay) {
                overlay.parentNode.removeChild(overlay);
            }
        },
        failure: function(c, b, a, e) {
            _frevvo.lightBoxView.hideWaitDialog();
            var d = new _frevvo.Errors(c.status, (e != null && e.length > 0) ? e : c.statusText);
            _frevvo.utilities.ajaxRequest.showError(d.getMessage(), "<span><strong>SEVERE: Unable to initialize form.</strong></span>", false);
        }
    },
    onValueChange: function(editorEl, formEl) {
        var formState = EditorView.getState(editorEl);
        _frevvo.forms.updateForm(JSONUtil.objectToJSONString(formState), function(t, formState, options) {
            if (t) {
                var fstate = eval(t.responseText);
                _frevvo.formView.setState(options.formEl, fstate);
                EditorView.setState(options.editorEl, fstate);
            }
        }, function(t, json, options) {
            EditorView.setState(options.editorEl, "Could not update form properties");
        }, {
            formEl: formEl,
            editorEl: editorEl
        });
    },
    print: function(a) {
        var b = _frevvo.forms.formUrl() + "?print=true";
        if (a) {
            b += "&format=" + a;
        }
        alert(b);
    }
};
_frevvo.treeController = {
    setup: function(b) {
        try {
            var a = _frevvo.treeView.getView();
            a.observePlusMinus(b);
            a.observeFolders(b);
        } catch (c) {}
    }
};
_frevvo.applicationController = {
    toggleLeftMenu: {
        setup: function(a) {
            FEvent.observe(a, "click", this.doIt.bindAsObserver(this, a));
            FEvent.observe(a, "keydown", this.doIt.bindAsObserver(this, a));
        },
        doIt: function(b, c) {
            if (_frevvo.uberController.event.isActivationEvent(b)) {
                if (b) {
                    Event.stop(b);
                }
                var a = document.body;
                if (Element.hasClassName(a, "s-left-collapsed")) {
                    Element.removeClassName(a, "s-left-collapsed");
                } else {
                    Element.addClassName(a, "s-left-collapsed");
                }
            }
        }
    },
    showLoadError: {
        setup: function(b) {
            var a = _frevvo.applicationView.getView();
            a.observeClick(b, this.doIt.bindAsObserver(this, b));
        },
        doIt: function(a, c) {
            if (a) {
                Event.stop(a);
            }
            var d = c.getAttribute("errMsg");
            var b = c.getAttribute("docsUrl");
            if (b) {
                d = d.replace("docsUrl", b);
            }
            _frevvo.lightBoxView.showInfoDialog(_frevvo.protoView.makeNode("<span style='color:red;'>" + d + "</span>"), _frevvo.localeStrings.error);
        }
    },
    showSaveError: {
        check: function(a) {
            var c = a.getAttribute("save-error");
            if (c && c == "form-mapping") {
                var b = _frevvo.localeStrings.saveErrorDetected.replace("{0}", a.innerHTML);
                _frevvo.lightBoxView.showYesNoDialog(_frevvo.protoView.makeNode(b), "Saved with Error(s) Detected", this.onConfirm.bindAsObserver(this, a));
            }
        },
        onConfirm: function(a, b) {
            b.click();
        }
    },
    removeUser: {
        setup: function(b) {
            var a = _frevvo.applicationView.getView();
            a.observeClick(b, this.doIt.bindAsObserver(this, b));
        },
        doIt: function(a, b) {
            if (a) {
                Event.stop(a);
            }
            _frevvo.lightBoxView.showYesNoDialog(_frevvo.protoView.makeNode("Are you sure? This will immediately remove the user and all forms."), "Remove user?", this.onConfirm.bindAsObserver(this, b));
        },
        onConfirm: function(a, b) {
            window.location = b.href;
        }
    },
    removeRole: {
        setup: function(b) {
            var a = _frevvo.applicationView.getView();
            a.observeClick(b, this.doIt.bindAsObserver(this, b));
        },
        doIt: function(a, b) {
            if (a) {
                Event.stop(a);
            }
            _frevvo.lightBoxView.showYesNoDialog(_frevvo.protoView.makeNode("Are you sure? This will immediately remove the role."), "Remove role?", this.onConfirm.bindAsObserver(this, b));
        },
        onConfirm: function(a, b) {
            window.location = b.href;
        }
    },
    removeTenant: {
        setup: function(b) {
            var a = _frevvo.applicationView.getView();
            a.observeClick(b, this.doIt.bindAsObserver(this, b));
        },
        doIt: function(a, b) {
            if (a) {
                Event.stop(a);
            }
            _frevvo.lightBoxView.showYesNoDialog(_frevvo.protoView.makeNode("Are you sure? This will immediately remove all forms, users etc."), "Remove tenant?", this.onConfirm.bindAsObserver(this, b));
        },
        onConfirm: function(a, b) {
            window.location = b.href;
        }
    },
    newApplication: {
        setup: function(b) {
            var a = _frevvo.applicationView.getView();
            a.observeClick(b, this.doIt.bindAsObserver(this, b));
        },
        doIt: function(a, b) {
            Event.stop(a);
            _frevvo.application.createApplication(this.success, this.failure, {
                target: b
            });
        },
        success: function(c, b, a) {
            _frevvo.applicationView.getView().onNewApplication(a.target, c.responseText);
        },
        failure: function(c, b, a) {
            var d = new _frevvo.Errors(c.status, c.statusText);
            _frevvo.utilities.ajaxRequest.showError(d.getMessage(), "<span><strong>Add application failed.</strong><br/><small><em>Refreshing page...</em></small></span>", true);
        }
    },
    removeApplication: {
        setup: function(b) {
            var a = _frevvo.applicationView.getView();
            a.observeClick(b, this.doIt.bindAsObserver(this, b));
        },
        doIt: function(b, c, a) {
            if (b) {
                Event.stop(b);
            }
            if (!a) {
                a = "http://docs.frevvo.com/docs/index.php/V4_Forms_and_Flows#Deleting.2FReplacing_Forms.2C_Flows_and_Applications";
            }
            _frevvo.lightBoxView.showYesNoDialog(_frevvo.protoView.makeNode("<span>This will remove the application and all associated submissions. <strong>Are you sure</strong>?<br/><p><small style='text-align:center; display:block;'>If you are trying to replace this form with another version then <strong>don't</strong> delete but upload the new version and check <strong>replace</strong>. Click <a target='_blank' href='" + a + "'>here</a> for more information.</small></p></span>"), "Remove application", this.onConfirm.bindAsObserver(this, c));
            return false;
        },
        onConfirm: function(a, b) {
            window.location = b.href;
        }
    },
    newSpace: {
        setup: function(b) {
            var a = _frevvo.applicationView.getView();
            a.observeClick(b, this.doIt.bindAsObserver(this, b));
        },
        doIt: function(a, b) {
            Event.stop(a);
            _frevvo.application.createSpace(this.success, this.failure, {
                target: b
            });
        },
        success: function(c, b, a) {
            window.location.reload();
        },
        failure: function(c, b, a) {
            var d = new _frevvo.Errors(c.status, c.statusText);
            _frevvo.utilities.ajaxRequest.showError(d.getMessage(), "<span><strong>Add space failed.</strong><br/><small><em>Refreshing page...</em></small></span>", true);
        }
    },
    removeSpace: {
        setup: function(b) {
            var a = _frevvo.applicationView.getView();
            a.observeClick(b, this.doIt.bindAsObserver(this, b));
        },
        doIt: function(a, b) {
            if (a) {
                Event.stop(a);
            }
            _frevvo.lightBoxView.showYesNoDialog(_frevvo.protoView.makeNode("<span>This will remove the space. <strong>Are you sure</strong>?</span>"), "Remove space", this.onConfirm.bindAsObserver(this, b));
            return false;
        },
        onConfirm: function(a, b) {
            window.location = b.href;
        }
    },
    removeColorScheme: {
        setup: function(b) {
            var a = _frevvo.applicationView.getView();
            a.observeClick(b, this.doIt.bindAsObserver(this, b));
        },
        doIt: function(a, b) {
            if (a) {
                Event.stop(a);
            }
            _frevvo.lightBoxView.showYesNoDialog(_frevvo.protoView.makeNode("This will permanently remove the color scheme. Are you sure?"), "Remove color scheme", this.onConfirm.bindAsObserver(this, b));
            return false;
        },
        onConfirm: function(a, b) {
            window.location = b.href;
        }
    },
    removeTheme: {
        setup: function(b) {
            var a = _frevvo.applicationView.getView();
            a.observeClick(b, this.doIt.bindAsObserver(this, b));
        },
        doIt: function(a, b) {
            if (a) {
                Event.stop(a);
            }
            _frevvo.lightBoxView.showYesNoDialog(_frevvo.protoView.makeNode("This will permanently remove the theme. Are you sure?"), "Remove theme", this.onConfirm.bindAsObserver(this, b));
            return false;
        },
        onConfirm: function(a, b) {
            window.location = b.href;
        }
    },
    toggleDeployFormOrFlow: {
        setup: function(a) {
            _frevvo.applicationView.observeClick(a, this.doIt.bindAsObserver(this, a));
        },
        doIt: function(a, b) {
            Event.stop(a);
            if (Element.hasClassName(b, "a-deploy-form") || Element.hasClassName(b, "a-deploy-flow")) {
                _frevvo.lightBoxView.showYesNoDialog(_frevvo.protoView.makeNode("Deployment (to production) is subject to your license. Please confirm."), "Deploy form", this.onConfirm.bindAsObserver(this, b));
            } else {
                _frevvo.lightBoxView.showYesNoDialog(_frevvo.protoView.makeNode("The form will no longer be deployed [to production]. Please confirm."), "Undeploy form", this.onConfirm.bindAsObserver(this, b));
            }
        },
        onConfirm: function(a, c) {
            var b = c.getAttribute("ulink");
            if (b) {
                _frevvo.application.toggleDeployFormOrFlow(_frevvo.applicationController.toggleDeployFormOrFlow.success, _frevvo.applicationController.toggleDeployFormOrFlow.failure, {
                    ulink: b,
                    target: c
                });
            } else {
                _frevvo.application.toggleDeployFormOrFlow(_frevvo.applicationController.toggleDeployFormOrFlow.success, _frevvo.applicationController.toggleDeployFormOrFlow.failure, {
                    aId: _frevvo.applicationView.getView().getApplicationId(c),
                    fId: _frevvo.applicationView.getView().getFormId(c),
                    target: c
                });
            }
        },
        success: function(c, b, a) {
            window.location.reload();
        },
        failure: function(c, b, a) {
            var d = new _frevvo.Errors(c.status, c.statusText);
            _frevvo.utilities.ajaxRequest.showError(d.getMessage(), "<span><strong>Deployment failed.</strong><br/><small><em>Refreshing page...</em></small></span>", true);
        }
    },
    editForm: {
        doIt: function(a, d, c, b) {
            if (a) {
                Event.stop(a);
            }
            if (b) {
                _frevvo.lightBoxView.showYesNoDialog(_frevvo.protoView.makeNode("This form/flow has been partially upgraded from a previous version. Before editing, the upgrade must be completed. This change is irreversible and we suggest you download and save a copy before proceeding. Please confirm."), "Upgrade form/flow", this.onConfirm.bindAsObserver(this, d, c));
            } else {
                window.location = c;
            }
        },
        onConfirm: function(a, c, b) {
            window.location = b;
        }
    },
    removeForm: {
        setup: function(b) {
            var a = _frevvo.applicationView.getView();
            a.observeClick(b, this.doIt.bindAsObserver(this, b));
        },
        doIt: function(b, c) {
            if (b) {
                Event.stop(b);
            }
            var a = c.getAttribute("helpUrl");
            if (!a) {
                a = "http://docs.frevvo.com/docs/index.php/V4_Forms_and_Flows#Deleting.2FReplacing_Forms.2C_Flows_and_Applications";
            }
            _frevvo.lightBoxView.showYesNoDialog(_frevvo.protoView.makeNode("<span>This will remove the form and all associated submissions. <strong>Are you sure</strong>?<br/><p><small style='text-align:center; display:block;'>If you are trying to replace this form with another version then <strong>don't</strong> delete but upload the new version and check <strong>replace</strong>. Click <a target='_blank' href='" + a + "'>here</a> for more information.</small></p></span>"), "Remove form", this.onConfirm.bindAsObserver(this, c));
        },
        onConfirm: function(a, b) {
            window.location = b.href;
        }
    },
    removeFlow: {
        setup: function(b) {
            var a = _frevvo.applicationView.getView();
            a.observeClick(b, this.doIt.bindAsObserver(this, b));
        },
        doIt: function(b, c) {
            if (b) {
                Event.stop(b);
            }
            var a = c.getAttribute("helpUrl");
            if (!a) {
                a = "http://docs.frevvo.com/docs/index.php/V4_Forms_and_Flows#Deleting.2FReplacing_Forms.2C_Flows_and_Applications";
            }
            _frevvo.lightBoxView.showYesNoDialog(_frevvo.protoView.makeNode("<span>This will remove the flow and all associated submissions. <strong>Are you sure</strong>?<br/><p><small style='text-align:center; display:block;'>If you are trying to replace this flow with another version then <strong>don't</strong> delete but upload the new version and check <strong>replace</strong>. Click <a target='_blank' href='" + a + "'>here</a> for more information.</small></p></span>"), "Remove flow", this.onConfirm.bindAsObserver(this, c));
        },
        onConfirm: function(a, b) {
            window.location = b.href;
        }
    },
    removeLocale: {
        setup: function(b) {
            var a = _frevvo.applicationView.getView();
            a.observeClick(b, this.doIt.bindAsObserver(this, b));
        },
        doIt: function(a, b) {
            if (a) {
                Event.stop(a);
            }
            _frevvo.lightBoxView.showYesNoDialog(_frevvo.protoView.makeNode("This will remove the selected language. Are you sure?"), "Remove language", this.onConfirm.bindAsObserver(this, b));
            return false;
        },
        onConfirm: function(a, b) {
            window.location = b.href;
        }
    },
    removeScript: {
        setup: function(b) {
            var a = _frevvo.applicationView.getView();
            a.observeClick(b, this.doIt.bindAsObserver(this, b));
        },
        doIt: function(a, b) {
            if (a) {
                Event.stop(a);
            }
            _frevvo.lightBoxView.showYesNoDialog(_frevvo.protoView.makeNode("This will remove the script. Are you sure?"), "Remove script", this.onConfirm.bindAsObserver(this, b));
            return false;
        },
        onConfirm: function(a, b) {
            window.location = b.href;
        }
    },
    viewSchema: {
        setup: function(b) {
            var a = _frevvo.applicationView.getView();
            a.observeClick(b, this.doIt.bindAsObserver(this, b));
        },
        doIt: function(a, b) {
            Event.stop(a);
            _frevvo.application.viewSchema({
                aId: _frevvo.applicationView.getView().getApplicationId(b),
                sId: _frevvo.applicationView.getView().getSchemaId(b)
            });
        }
    },
    removeSchema: {
        setup: function(b) {
            var a = _frevvo.applicationView.getView();
            a.observeClick(b, this.doIt.bindAsObserver(this, b));
        },
        doIt: function(a, b) {
            Event.stop(a);
            _frevvo.lightBoxView.showYesNoDialog(_frevvo.protoView.makeNode("This will permanently remove the schema. This change is irreversible. Are you sure?"), "Remove schema", this.onConfirm.bindAsObserver(this, b));
        },
        onConfirm: function(a, b) {
            window.location = b.href;
        },
        success: function(c, b, a) {
            _frevvo.applicationView.getView().onRemoveSchema(a.target);
        },
        failure: function(c, b, a) {
            var d = new _frevvo.Errors(c.status, c.statusText);
            _frevvo.utilities.ajaxRequest.showError(d.getMessage(), "<span><strong>Remove schema failed.</strong><br/><small><em>Refreshing page...</em></small></span>", true);
        }
    },
    viewDocument: {
        setup: function(b) {
            var a = _frevvo.applicationView.getView();
            a.observeClick(b, this.doIt.bindAsObserver(this, b));
        },
        doIt: function(a, b) {
            Event.stop(a);
            _frevvo.application.viewDocument(this.success, this.failure, {
                aId: _frevvo.applicationView.getView().getApplicationId(b),
                fId: _frevvo.applicationView.getView().getFormId(b),
                dId: _frevvo.applicationView.getView().getDocumentId(b),
                visit: this.visit
            });
        },
        success: function(c, b, a) {
            var d = _frevvo.applicationView.makeNode(c.responseText);
            _frevvo.lightBoxView.showLimitedHeightInfoDialog(d, "View Document");
            _frevvo.treeView.getView().traverse(d, a.visit);
        },
        visit: function(a) {
            _frevvo.treeController.setup(a);
        },
        failure: function(c, b, a) {
            var d = new _frevvo.Errors(c.status, c.statusText);
            _frevvo.utilities.ajaxRequest.showError(d.getMessage(), "<span><strong>View document failed.</strong><br/><small><em>Refreshing page...</em></small></span>", true);
        }
    },
    removeDocument: {
        setup: function(b) {
            var a = _frevvo.applicationView.getView();
            a.observeClick(b, this.doIt.bindAsObserver(this, b));
        },
        doIt: function(a, b) {
            Event.stop(a);
            _frevvo.lightBoxView.showYesNoDialog(_frevvo.protoView.makeNode("Your document will be permanently deleted. This change is irreversible. Are you sure?"), "Remove document", this.onConfirm.bindAsObserver(this, b));
        },
        onConfirm: function(a, b) {
            window.location = b.href;
        }
    },
    removeDocuments: {
        setup: function(b) {
            var a = _frevvo.applicationView.getView();
            a.observeClick(b, this.doIt.bindAsObserver(this, b));
        },
        doIt: function(a, b) {
            Event.stop(a);
            _frevvo.lightBoxView.showYesNoDialog(_frevvo.protoView.makeNode("All documents will be permanently deleted. This change is irreversible. Are you sure?"), "Remove documents", this.onConfirm.bindAsObserver(this, b));
        },
        onConfirm: function(a, b) {
            window.location = b.href;
        }
    },
    viewMetadata: {
        setup: function(b) {
            var a = _frevvo.applicationView.getView();
            a.observeClick(b, this.doIt.bindAsObserver(this, b));
        },
        doIt: function(a, c) {
            Event.stop(a);
            var b = _frevvo.applicationView.$childByClass(c.parentNode.parentNode, "a-metadata");
            if (b) {
                if (Element.visible(b)) {
                    Element.hide(b);
                } else {
                    Element.show(b);
                }
            }
        }
    }
};
_frevvo.linkController = {
    setup: function(b) {
        var a = _frevvo.baseView.getView(b);
        if (!_frevvo.baseView.isEditing()) {
            a.$focusElement(b).onkeydown = function(c, d) {
                if (_frevvo.uberController.event.isActivationEvent(c)) {
                    if (c) {
                        Event.stop(c);
                    }
                    d.click();
                }
            }.bindAsObserver(null, a.$link(b));
        }
    }
};
_frevvo.formViewerController = {
    click: {
        setup: function(b) {
            var a = _frevvo.baseView.getView(b);
            if (!_frevvo.baseView.isEditing()) {
                FEvent.observe(a.$element(b), "click", this.doIt.bindAsObserver(this, a.$element(b)));
                a.$focusElement(b).onkeydown = function(c, d) {
                    if (_frevvo.uberController.event.isActivationEvent(c)) {
                        if (c) {
                            Event.stop(c);
                        }
                        d.click();
                    }
                }.bindAsObserver(null, a.$element(b));
            }
        },
        doIt: function(b, d) {
            if (b) {
                Event.stop(b);
            }
            var c = d.getAttribute("gen-form-uri");
            if (c != null && c.length > 0) {
                var a = window.open("", "new");
                a.location.href = _frevvo.formViewerController.buildGenFormUrl(c);
                a.focus();
            }
        }
    },
    buildGenFormUrl: function(a) {
        if (_frevvo.utilities.util.isDefined(window._frevvo.forms)) {
            return _frevvo.forms.formUrl() + a;
        } else {
            if (_frevvo.utilities.util.isDefined(window.Flows)) {
                return Flows.flowUrl() + a;
            }
        }
    }
};
_frevvo.comboBoxController = {
    findMatches: function(remoteSourceUrl, matchTerm, callBack) {
        if (matchTerm) {
            _frevvo.utilities.ajaxRequest.send(remoteSourceUrl + matchTerm, {
                method: "get",
                onSuccess: function(t) {
                    var response = "";
                    eval("response = " + t.responseText);
                    var list = null;
                    for (x in response) {
                        list = response[x];
                        break;
                    }
                    callBack(list);
                }
            });
        } else {
            callBack(new Array());
        }
    },
    click: {
        setup: function(e) {
            if (e.isSetup) {
                return;
            }
            var c = _frevvo.comboBoxView.getOptions(e);
            if (c) {
                for (var d = 0; d < c.length; d++) {
                    var b = _frevvo.protoView.$childByName(c[d], "A");
                    FEvent.flushAll(b, "click");
                    FEvent.observe(b, "click", this.doIt.bindAsObserver(this, e, b));
                }
            }
        },
        doIt: function(c, d, b) {
            if (c) {
                Event.stop(c);
            }
            _frevvo.comboBoxView.setSelectedResult(d, b.innerHTML);
        }
    }
};
_frevvo.phoneController = {
    curPage: 1,
    pageCount: null,
    submitLabel: "Submit",
    submitName: "Submit",
    previousLabel: "Previous",
    backLabel: "Back",
    nextLabel: "Next",
    rejectLabel: "Reject",
    portraitColumns: null,
    landscapeColumns: null,
    cancelUrl: null,
    hiddenPages: [],
    updateStates: function(c) {
        _frevvo.phoneController.pageCount = parseInt(c.pageCount);
        if (c.submitLabel != null) {
            _frevvo.phoneController.submitLabel = c.submitLabel;
        }
        if (c.rejectLabel != null) {
            _frevvo.phoneController.rejectLabel = c.rejectLabel;
        }
        if (c.submitName) {
            _frevvo.phoneController.submitName = c.submitName;
        }
        if (c.backLabel != null) {
            _frevvo.phoneController.backLabel = c.backLabel;
        }
        if (c.nextLabel != null) {
            _frevvo.phoneController.nextLabel = c.nextLabel;
        }
        if (c.previousLabel != null) {
            _frevvo.phoneController.previousLabel = c.previousLabel;
        }
        if (c.cancelUrl != null) {
            _frevvo.phoneController.cancelUrl = decodeURIComponent(c.cancelUrl);
        }
        if (c.portraitColumns != null) {
            _frevvo.phoneController.portraitColumns = c.portraitColumns;
        }
        if (c.landscapeColumns != null) {
            _frevvo.phoneController.landscapeColumns = c.landscapeColumns;
        }
        if (c.pages != null) {
            var a = c.pages;
            for (var e = 0; e < a.length; e++) {
                if (!a[e].visible) {
                    _frevvo.phoneController.hiddenPages.push(a[e].id);
                }
            }
        }
        _frevvo.switchControlController.manageNavBar();
        var b = _frevvo.protoView.$childByName(_frevvo.protoView.$childByName($("root-control"), "DIV"), "DIV");
        var d = _frevvo.switchControlView.$switchCases(b);
        if (d.length > 0) {
            if (_frevvo.phoneController.hiddenPages.indexOf(d[0].getAttribute("id")) > -1) {
                var f = _frevvo.switchCaseView.$content(d[0]);
                Element.hide(f);
                _frevvo.phoneController.showMobileStatus(_frevvo.localeStrings.pageBlank);
            }
        }
    },
    setupSummary: function() {
        _frevvo.uberController.flow = true;
        _frevvo.phoneController.pageCount = 1;
        _frevvo.phoneController.submitLabel = _frevvo.protoView.$childByClass(FlowView.getFlowButton(), "facade").innerHTML;
        _frevvo.phoneController.previousLabel = _frevvo.protoView.$childByClass(FlowView.getPreviousButton(), "facade").innerHTML;
        _frevvo.switchControlController.manageNavBar();
        _frevvo.switchControlController.setupNavBtns(null);
        var a = $("sw-nav-cancel");
        if (a) {
            _frevvo.phoneController.cancelUrl = a.getAttribute("purl");
        }
        _frevvo.phoneController.swipe.setup($("sw-nav-buttons"));
    },
    hideMobileStatus: function() {
        var a = $("f-mobile-status");
        if (a) {
            Element.removeClassName(a, "s-status");
        }
    },
    showMobileStatus: function(b) {
        var a = $("f-mobile-status");
        if (a) {
            a.innerHTML = b;
            Element.addClassName(a, "s-status");
        }
    },
    swipe: {
        start: null,
        setup: function(a) {
            var b = $("root-control");
            this.switchcontrol = _frevvo.protoView.$childByClass(_frevvo.groupView.$content(b), "f-switch");
            this.width = ("getBoundingClientRect" in a) ? a.getBoundingClientRect().width : a.offsetWidth;
            this.index = 0;
            if (!this.width) {
                return null;
            }
            var c = $("sw-nav-buttons");
            if (c) {
                FEvent.observe(c, "touchstart", this.touchStartObserver.bindAsObserver(this, a));
                FEvent.observe(c, "touchmove", this.touchMoveObserver.bindAsObserver(this, a));
                FEvent.observe(c, "touchend", this.touchEndObserver.bindAsObserver(this, a));
            }
        },
        touchStartObserver: function(a, b) {
            this.start = {
                pageX: a.touches[0].pageX,
                pageY: a.touches[0].pageY,
                time: Number(new Date())
            };
            this.isScrolling = undefined;
            this.deltaX = 0;
        },
        touchMoveObserver: function(a, b) {
            if (a.touches.length > 1 || a.scale && a.scale !== 1) {
                return;
            }
            this.deltaX = a.touches[0].pageX - this.start.pageX;
            if (typeof this.isScrolling == "undefined") {
                this.isScrolling = !!(this.isScrolling || Math.abs(this.deltaX) < Math.abs(a.touches[0].pageY - this.start.pageY));
            }
            if (!this.isScrolling) {
                a.preventDefault();
                Event.stop(a);
            }
        },
        touchEndObserver: function(a, c) {
            var b = Number(new Date()) - this.start.time < 250 && Math.abs(this.deltaX) > 20 || Math.abs(this.deltaX) > this.width / 2;
            if (b && !this.isScrolling) {
                _frevvo.switchControlController.backNextObserver(null, this.switchcontrol, this.deltaX < 0, true);
            }
        }
    }
};
FEvent = {
    observe: function(d, c, b, a) {
        if (d) {
            if (!d.fhandlers) {
                d.fhandlers = {};
            }
            if (!d.fhandlers[c]) {
                d.fhandlers[c] = new Array();
            }
            if (d.fhandlers[c].indexOf(b) == -1) {
                d.fhandlers[c].push(b);
                Event.observe(d, c, b, a);
            }
        }
    },
    flushAll: function(b, a) {
        if (b) {
            if (a) {
                this.stopObserving(b, a);
            } else {
                for (var c in b.fhandlers) {
                    this.stopObserving(b, c);
                }
            }
            b.fhandlers = null;
        }
    },
    stopObserving: function(d, c, b, a) {
        if (d.fhandlers) {
            var e = d.fhandlers[c];
            if (e && (e instanceof Array)) {
                e.each(function(g, f) {
                    if ((b && g == b) || !b) {
                        Event.stopObserving(d, c, g, a);
                        e[f] = null;
                    }
                });
                d.fhandlers[c] = e.compact();
                if (d.fhandlers[c] == 0) {
                    d.fhandlers[c] = null;
                }
            }
        }
    },
    hasObservers: function(b, a) {
        return (b && b.fhandlers && b.fhandlers[a] && b.fhandlers[a].length > 0);
    },
    getObservers: function(b, a) {
        if (b && b.fhandlers) {
            return b.fhandlers[a];
        }
    }
};
_frevvo.forms = {
    toString: function() {
        return "Forms Client";
    },
    removeUrlParameters: function(b) {
        var a = b.indexOf("?");
        if (a > -1) {
            b = b.substring(0, a);
        }
        a = b.indexOf("#");
        if (a > -1) {
            b = b.substring(0, a);
        }
        return b;
    },
    addReferrerUrlAndIframeId: function(a) {
        if (_frevvo.utilities.windowUtil._referrer_url && _frevvo.utilities.windowUtil._referrer_url.length > 0) {
            a += "&_referrer_url=" + _frevvo.utilities.windowUtil._referrer_url;
        }
        if (_frevvo.utilities.windowUtil._iframe_id && _frevvo.utilities.windowUtil._iframe_id.length > 0) {
            a += "&_iframe_id=" + _frevvo.utilities.windowUtil._iframe_id;
        }
        return a;
    },
    userUrl: function() {
        var c = _frevvo.forms.formUrl();
        var b = c.indexOf("user");
        var a = c.substring(0, b + 5);
        var d = c.substring(b + 5).indexOf("/");
        a += c.substring(b + 5).substring(0, d);
        return a;
    },
    formId: function() {
        return ($("_frevvoFormInstanceId") && $("_frevvoFormInstanceId").value);
    },
    formUrl: function() {
        var a = document.getElementById("_frevvo-frameless-wrapper");
        if (a) {
            return _frevvo.api.getLastLoadedUrl();
        }
        return this.removeUrlParameters(document.URL);
    },
    controlUrl: function(a) {
        url = this.formUrl() + "/control";
        if (a && a.length > 0) {
            url = url + "/" + a;
        }
        return url;
    },
    controlTypeUrl: function(a) {
        return this.controlUrl(a) + "/type";
    },
    appId: function() {
        var b = document.URL.indexOf("/frevvo/web");
        var a = /\/frevvo\/web\/tn\/([^\/]*)\/user\/([^\/]*)\/app\/([^\/]*)\/form\/([^\/]*)/.exec(document.URL.substring(b));
        return (a && a[3].split("?")[0]) || null;
    },
    userId: function() {
        var b = document.URL.indexOf("/frevvo/web");
        var a = /\/frevvo\/web\/tn\/([^\/]*)\/user\/([^\/]*)\/app\/([^\/]*)\/form\/([^\/]*)/.exec(document.URL.substring(b));
        return (a && a[2].split("?")[0]) || null;
    },
    tenantId: function() {
        var b = document.URL.indexOf("/frevvo/web");
        var a = /\/frevvo\/web\/tn\/([^\/]*)\/user\/([^\/]*)\/app\/([^\/]*)\/form\/([^\/]*)/.exec(document.URL.substring(b));
        return (a && a[1].split("?")[0]) || null;
    },
    onException: function(b, a) {
        _frevvo.utilities.debugPrint && _frevvo.utilities.debugPrint("Exception while processing AJAX callback: " + a);
        throw (a);
    },
    auth: function(e, c, a, d) {
        var b = this.userUrl() + "/auth?_method=get&random=" + Math.random();
        if (d != null) {
            b += "&state=" + d;
        }
        Object.extend(a, {
            method: "get",
            onSuccess: e,
            onFailure: c,
            onException: this.onException
        });
        _frevvo.utilities.ajaxRequest.send(b, a);
    },
    logout: function(d, c, a) {
        var b = this.userUrl() + "/auth?_method=get&action=logout&random=" + Math.random();
        Object.extend(a, {
            method: "get",
            onSuccess: d,
            onFailure: c,
            onException: this.onException
        });
        _frevvo.utilities.ajaxRequest.send(b, a);
    },
    dispose: function(d, c, a) {
        var b = this.formUrl() + "?_method=POST&formAction=Cancel&random=" + Math.random();
        Object.extend(a, {
            method: "get",
            ignorePageError: true,
            onSuccess: d,
            onFailure: c,
            onException: this.onException
        });
        _frevvo.utilities.ajaxRequest.send(b, a);
    },
    initForm: function(d, c, a) {
        var e;
        if ($("f-form-tb")) {
            e = _frevvo.protoView.$childByClass($("f-form-tb"), "f-tb-print");
            if (e) {
                e.href = this.formUrl() + e.getAttribute("purl");
            }
        }
        var b = this.formUrl() + "/state?random=" + Math.random();
        if (window.FormTz && FormTz.getName()) {
            b += "&_formTz=" + FormTz.getName();
        }
        Object.extend(a, {
            method: "get",
            asynchronous: true,
            onSuccess: d,
            onFailure: c,
            onException: this.onException
        });
        _frevvo.utilities.ajaxRequest.send(b, a);
    },
    updateLocation: function(e, c, a, d) {
        var b = this.formUrl() + "/position?_method=put&random=" + Math.random();
        if (d != null) {
            b += "&state=" + d;
        }
        Object.extend(a, {
            method: "put",
            asynchronous: true,
            onSuccess: e,
            onFailure: c,
            onException: this.onException
        });
        _frevvo.utilities.ajaxRequest.send(b, a);
    },
    saveForm: function(e, c, a, d) {
        var b = this.formUrl() + "/save";
        if (d != null) {
            b += "?state=" + d;
        }
        Object.extend(a, {
            method: "post",
            onSuccess: e,
            onFailure: c,
            onException: this.onException
        });
        _frevvo.utilities.ajaxRequest.send(b, a);
    },
    addRule: function(d, c, a) {
        var b = this.formUrl() + "/rule?edit=true";
        Object.extend(a, {
            method: "post",
            onSuccess: d,
            onFailure: c,
            onException: this.onException
        });
        _frevvo.utilities.ajaxRequest.send(b, a);
    },
    removeRule: function(d, e, c, a) {
        var b = this.formUrl() + "/rule/" + d + "?edit=true";
        Object.extend(a, {
            method: "delete",
            onSuccess: e,
            onFailure: c,
            onException: this.onException
        });
        _frevvo.utilities.ajaxRequest.send(b, a);
    },
    editRule: function(d, e, c, a) {
        var b = this.formUrl() + "/rule/" + d + "?edit=true&random=" + Math.random();
        Object.extend(a, {
            method: "get",
            onSuccess: e,
            onFailure: c,
            onException: this.onException
        });
        _frevvo.utilities.ajaxRequest.send(b, a);
    },
    updateRule: function(d, f, e, c, a) {
        var b = this.formUrl() + "/rule/" + d + "?_method=put&edit=true";
        Object.extend(a, {
            method: "post",
            postBody: "state=" + _frevvo.utilities.util.escapePlus(f),
            onSuccess: e,
            onFailure: c,
            onException: this.onException
        });
        _frevvo.utilities.ajaxRequest.send(b, a);
    },
    validateRules: function(d, c, a) {
        var b = this.formUrl() + "/rules?action=validate&edit=true";
        Object.extend(a, {
            method: "get",
            onSuccess: d,
            onFailure: c,
            onException: this.onException
        });
        _frevvo.utilities.ajaxRequest.send(b, a);
    },
    loadCompletionData: function(d, c, a) {
        var b = this.formUrl() + "/rules?action=loadCompletionData&edit=true";
        Object.extend(a, {
            method: "get",
            onSuccess: d,
            onFailure: c,
            onException: this.onException
        });
        _frevvo.utilities.ajaxRequest.send(b, a);
    },
    loadControlChildren: function(e, f, d, b) {
        var a = false;
        if (b.mappingMode) {
            a = true;
        }
        var c = this.formUrl();
        if (b.isInFlow) {
            c += "/form/" + b.formId;
        }
        c += "/control/" + e + "/outlineTree?random=" + Math.random() + "&mappingMode=" + a;
        Object.extend(b, {
            method: "get",
            onSuccess: f,
            onFailure: d,
            onException: this.onException
        });
        _frevvo.utilities.ajaxRequest.send(c, b);
    },
    reloadFormOutlineTree: function(e, d, b) {
        var a = false;
        if (b.mappingMode) {
            a = true;
        }
        var c = this.formUrl() + "/outlineTree?random=" + Math.random() + "&mappingMode=" + a;
        Object.extend(b, {
            method: "get",
            onSuccess: e,
            onFailure: d,
            onException: this.onException
        });
        _frevvo.utilities.ajaxRequest.send(c, b);
    },
    notifyExpansionStateChange: function(f, a, b, g, e, c) {
        var d = this.formUrl() + "/control/" + f + "/outlineTree?nodeType=" + a + "&expanded=" + b + "&random=" + Math.random();
        Object.extend(c, {
            method: "put",
            onSuccess: g,
            onFailure: e,
            onException: this.onException
        });
        _frevvo.utilities.ajaxRequest.send(d, c);
    },
    loadElementChildren: function(f, a, e, d, b) {
        var c = this.formUrl() + "/type/doctype/" + f + "/element/" + a + "?edit=true&random=" + Math.random();
        Object.extend(b, {
            method: "get",
            onSuccess: e,
            onFailure: d,
            onException: this.onException
        });
        _frevvo.utilities.ajaxRequest.send(c, b);
    },
    addDocumentType: function(f, c, a, g, e, b) {
        var d = this.formUrl() + "/schema/" + f + "/" + c + "/element/" + a + "?edit=true";
        Object.extend(b, {
            method: "post",
            onSuccess: g,
            onFailure: e,
            onException: this.onException
        });
        _frevvo.utilities.ajaxRequest.send(d, b);
    },
    removeDocumentType: function(e, d, c, a) {
        var b = this.formUrl() + "/type/doctype/" + e + "?edit=true";
        Object.extend(a, {
            method: "delete",
            onSuccess: d,
            onFailure: c,
            onException: this.onException
        });
        _frevvo.utilities.ajaxRequest.send(b, a);
    },
    updateForm: function(d, e, c, a) {
        var b = this.formUrl() + "/type?state=" + _frevvo.utilities.util.escapePlus(d);
        Object.extend(a, {
            method: "put",
            onSuccess: e,
            onFailure: c,
            onException: this.onException
        });
        _frevvo.utilities.ajaxRequest.send(b, a);
    },
    _data: function() {
        var f = "(";
        var e = _frevvo.formView.$rootControl(_frevvo.formView.getForm());
        var c = _frevvo.groupView.$content(e);
        var b = function(n, k) {
            if (!k) {
                k = {};
            }
            if (Element.hasClassName(n, "f-control")) {
                var j = _frevvo.baseView.getView(n);
                if (j.getValue && j.getValue(n) != null) {
                    var h = n.getAttribute("extid");
                    var m = (h in k) ? k[h] : new Array();
                    m.push("'" + j.getValue(n) + "'");
                    k[h] = m;
                }
            }
            for (var l = 0; l < n.children.length; l++) {
                b(n.children[l], k);
            }
            return k;
        };
        var a = b(e);
        for (ctrl in a) {
            if (f != "(") {
                f += ",";
            }
            if (a[ctrl].length === 1) {
                f += ctrl + ":" + a[ctrl][0];
            } else {
                var g = "!(";
                for (var d = 0; d < a[ctrl].length; d++) {
                    if (d > 0) {
                        g += ",";
                    }
                    g += a[ctrl][d];
                }
                g += "!)";
                f += ctrl + ":" + g;
            }
        }
        return f.concat(")");
    },
    createControl: function(e, f, b, g, d, a) {
        var c = this.controlUrl(f) + "/palette?edit=true&typeId=" + e + "&where=" + b;
        Object.extend(a, {
            method: "post",
            onSuccess: g,
            onFailure: d,
            onException: this.onException
        });
        _frevvo.utilities.ajaxRequest.send(c, a);
    },
    createControlFromNodeType: function(b, a, f, e, c) {
        var d = this.controlUrl() + "?edit=true&documentTypeId=" + b + "&nodeTypeId=" + a + "&random=" + Math.random();
        Object.extend(c, {
            method: "post",
            onSuccess: f,
            onFailure: e,
            onException: this.onException
        });
        _frevvo.utilities.ajaxRequest.send(d, c);
    },
    bindNodeTypeToControl: function(b, a, f, g, e, c) {
        var d = this.controlTypeUrl(f) + "?edit=true&action=bind&documentTypeId=" + b + "&nodeTypeId=" + a;
        Object.extend(c, {
            method: "post",
            onSuccess: g,
            onFailure: e,
            onException: this.onException
        });
        _frevvo.utilities.ajaxRequest.send(d, c);
    },
    moveControlType: function(e, g, b, f, d, a) {
        var c = this.controlTypeUrl(g) + "?edit=true&sourceId=" + e + "&action=move&where=" + b;
        Object.extend(a, {
            method: "post",
            onSuccess: f,
            onFailure: d,
            onException: this.onException
        });
        _frevvo.utilities.ajaxRequest.send(c, a);
    },
    addSwitchCase: function(e, d, f, c, a) {
        var b = this.controlTypeUrl(e) + "?edit=true&action=addCase" + (d ? "&before=true" : "");
        Object.extend(a, {
            method: "post",
            onSuccess: f,
            onFailure: c,
            onException: this.onException
        });
        _frevvo.utilities.ajaxRequest.send(b, a);
    },
    removeSwitchCase: function(d, e, c, a) {
        var b = this.controlTypeUrl(d) + "?edit=true&action=removeCase";
        Object.extend(a, {
            method: "delete",
            onSuccess: e,
            onFailure: c,
            onException: this.onException
        });
        _frevvo.utilities.ajaxRequest.send(b, a);
    },
    selectEditControlType: function(d, e, c, a) {
        var b = this.controlUrl(d) + "/select?edit=true&random=" + Math.random();
        Object.extend(a, {
            method: "get",
            onSuccess: e,
            onFailure: c,
            onException: this.onException
        });
        _frevvo.utilities.ajaxRequest.send(b, a);
    },
    editControlType: function(d, e, c, a) {
        var b = this.controlUrl(d) + "/editor?edit=true&random=" + Math.random();
        Object.extend(a, {
            method: "get",
            onSuccess: e,
            onFailure: c,
            onException: this.onException
        });
        _frevvo.utilities.ajaxRequest.send(b, a);
    },
    editFormType: function(d, c, a) {
        var b = this.formUrl() + "/editor?edit=true&random=" + Math.random();
        Object.extend(a, {
            method: "get",
            onSuccess: d,
            onFailure: c,
            onException: this.onException
        });
        _frevvo.utilities.ajaxRequest.send(b, a);
    },
    editLocaleStrings: function(d, c, a) {
        var b = this.formUrl() + "/locale?edit=true&random=" + Math.random();
        Object.extend(a, {
            method: "get",
            asynchronous: false,
            onSuccess: d,
            onFailure: c,
            onException: this.onException
        });
        _frevvo.utilities.ajaxRequest.send(b, a);
    },
    updateControlType: function(d, f, e, c, a) {
        var b = this.controlTypeUrl(d) + "?edit=true&_method=put";
        Object.extend(a, {
            method: "post",
            postBody: "state=" + _frevvo.utilities.util.escapePlus(f),
            onSuccess: e,
            onFailure: c,
            onException: this.onException
        });
        _frevvo.utilities.ajaxRequest.send(b, a);
    },
    updateLabel: function(d, f, e, c, a) {
        var b = this.controlTypeUrl(d) + "?edit=true&state=" + _frevvo.utilities.util.escapePlus(f);
        Object.extend(a, {
            method: "put",
            onSuccess: e,
            onFailure: c,
            onException: this.onException
        });
        _frevvo.utilities.ajaxRequest.send(b, a);
    },
    deleteControlType: function(d, e, c, a) {
        var b = this.controlTypeUrl(d) + "?edit=true";
        Object.extend(a, {
            method: "delete",
            onSuccess: e,
            onFailure: c,
            onException: this.onException
        });
        _frevvo.utilities.ajaxRequest.send(b, a);
    },
    addPaletteItem: function(e, f, c, d, a) {
        var b = this.controlUrl(e) + "/userPalette";
        if (d != null) {
            b += "?state=" + d;
        }
        Object.extend(a, {
            method: "post",
            onSuccess: f,
            onFailure: c,
            onException: this.onException
        });
        _frevvo.utilities.ajaxRequest.send(b, a);
    },
    deletePaletteItem: function(e, d, c, a) {
        var b = this.formUrl() + "/userPalette/" + e;
        Object.extend(a, {
            method: "delete",
            onSuccess: d,
            onFailure: c,
            onException: this.onException
        });
        _frevvo.utilities.ajaxRequest.send(b, a);
    },
    getControlXHTML: function(e, f, c, a, d) {
        var b = this.controlUrl(e) + "?output.type=html";
        Object.extend(a, {
            method: "get",
            asynchronous: d,
            onSuccess: f,
            onFailure: c,
            onException: this.onException
        });
        _frevvo.utilities.ajaxRequest.send(b, a);
    },
    getControl: function(d, e, c, a) {
        var b = this.controlUrl(d);
        Object.extend(a, {
            method: "put",
            onSuccess: e,
            onFailure: c,
            onException: this.onException
        });
        _frevvo.utilities.ajaxRequest.send(b, a);
    },
    updateControlValue: function(f, h, g, d, a, e) {
        var c = true;
        if (!e) {
            c = e;
        }
        var b = this.controlUrl(f) + "?_method=put&_valueUpdate=true";
        Object.extend(a, {
            method: "post",
            asynchronous: c,
            postBody: "state=" + _frevvo.utilities.util.escapePlus(h),
            onSuccess: g,
            onFailure: d,
            onException: this.onException
        });
        _frevvo.utilities.ajaxRequest.send(b, a);
    },
    updateControl: function(f, h, g, d, a, e) {
        var c = true;
        if (!e) {
            c = e;
        }
        var b = this.controlUrl(f) + "?_method=put";
        Object.extend(a, {
            method: "post",
            asynchronous: c,
            postBody: "state=" + _frevvo.utilities.util.escapePlus(h),
            onSuccess: g,
            onFailure: d,
            onException: this.onException
        });
        _frevvo.utilities.ajaxRequest.send(b, a);
    },
    deleteControl: function(d, e, c, a) {
        var b = this.controlUrl(d) + "?edit=true";
        Object.extend(a, {
            method: "delete",
            onSuccess: e,
            onFailure: c,
            onException: this.onException
        });
        _frevvo.utilities.ajaxRequest.send(b, a);
    },
    moveControl: function(e, d, f, c, a) {
        var b = this.controlUrl(e) + "?edit=true&action=move" + (d ? "&before=true" : "");
        Object.extend(a, {
            method: "post",
            onSuccess: f,
            onFailure: c,
            onException: this.onException
        });
        _frevvo.utilities.ajaxRequest.send(b, a);
    },
    signControl: function(f, b, g, e, c) {
        var d = this.controlUrl(f) + "/sign?_method=put";
        var a = "";
        if (c.wetSignature != null && c.wetSignature.length > 0) {
            a = "signatureData=" + _frevvo.utilities.util.escapePlus(c.wetSignature);
        }
        Object.extend(c, {
            method: "post",
            postBody: a,
            onSuccess: g,
            onFailure: e,
            onException: this.onException
        });
        _frevvo.utilities.ajaxRequest.send(d, c);
    },
    addRepeatItem: function(f, e, g, d, b, a) {
        var c = this.controlUrl(f) + "?action=addItem" + (a ? ("&limit=" + a) : "") + (e ? "&before=true" : "");
        Object.extend(b, {
            method: "post",
            asynchronous: false,
            onSuccess: g,
            onFailure: d,
            onException: this.onException
        });
        _frevvo.utilities.ajaxRequest.send(c, b);
    },
    removeRepeatItem: function(d, e, c, a) {
        var b = this.controlUrl(d) + "?action=removeItem";
        Object.extend(a, {
            method: "post",
            asynchronous: false,
            onSuccess: e,
            onFailure: c,
            onException: this.onException
        });
        _frevvo.utilities.ajaxRequest.send(b, a);
    }
};
_frevvo.model = {
    ajaxUrl: "/frevvo/event/",
    ajaxMethod: "post",
    toString: function() {
        return "Model";
    },
    updateLabel: function(d, f, a, c, h, b) {
        var a = _frevvo.baseView.getView(d.target);
        var e = a.getControlId(d.target);
        var g = BaseEditView.getState(d);
        g.label = f.value;
        _frevvo.forms.updateControlType(e, JSONUtil.objectToJSONString(g), h, b, {
            el: d.target,
            editor: d,
            view: a,
            editBtn: f,
            label: c
        });
    },
    updateProperties: function(c, f, b) {
        var a = _frevvo.baseView.getView(c.target);
        var d = a.getControlId(c.target);
        var e = JSONUtil.objectToJSONString(BaseEditView.getState(c));
        _frevvo.forms.updateControlType(d, e, f, b, {
            el: c.target,
            editor: c
        });
    },
    updateControlValue: function(c, g, b, e) {
        var a = _frevvo.baseView.getView(c);
        var d = a.getControlId(c);
        var f = JSONUtil.objectToJSONString(a.getState(c));
        _frevvo.forms.updateControlValue(d, f, g, b, {
            el: c
        }, e);
    },
    updateControlState: function(c, g, b, e) {
        var a = _frevvo.baseView.getView(c);
        var d = a.getControlId(c);
        var f = JSONUtil.objectToJSONString(a.getState(c));
        _frevvo.forms.updateControl(d, f, g, b, {
            el: c
        }, e);
    },
    addRepeatItem: function(d, f, g, c, b) {
        var a = _frevvo.baseView.getView(d);
        var e = a.getControlId(d);
        _frevvo.forms.addRepeatItem(e, false, g, c, {
            el: d,
            repeatController: f
        }, b);
    },
    removeRepeatItem: function(c, e, f, b) {
        var a = _frevvo.baseView.getView(c);
        var d = a.getControlId(c);
        _frevvo.forms.removeRepeatItem(d, f, b, {
            el: c,
            repeatController: e
        });
    },
    addSwitchCase: function(e, d, b) {
        var a = _frevvo.baseView.getView(e);
        var c = a.getControlId(e);
        _frevvo.forms.addSwitchCase(c, false, d, b, {
            header: e
        });
    },
    removeSwitchCase: function(e, d, b) {
        var a = _frevvo.baseView.getView(e);
        var c = a.getControlId(e);
        _frevvo.forms.removeSwitchCase(c, d, b, {
            header: e
        });
    },
    moveControl: function(a, f, d, i, b) {
        var e = _frevvo.baseView.getView(f);
        var g = _frevvo.baseView.getView(a);
        var c = null;
        if (e == _frevvo.formView) {
            c = _frevvo.baseView.getControlId(_frevvo.formView.$rootControl(_frevvo.baseView.getForm()));
        } else {
            c = e.getControlId(f);
        }
        var h = g.getControlId(a);
        _frevvo.forms.moveControlType(h, c, d, i, b, {
            where: d,
            sourceElement: a,
            destinationElement: f
        });
    },
    insertPaletteItem: function(c, f, b, e, a) {
        var g = BasePaletteView.getView(c);
        var d = null;
        if (!f) {
            d = _frevvo.baseView.getControlId(_frevvo.formView.$rootControl(_frevvo.baseView.getForm()));
        } else {
            if (Element.hasClassName(f, "f-form-tb")) {
                d = _frevvo.baseView.getControlId(_frevvo.formView.$rootControl(_frevvo.baseView.getForm()));
            } else {
                if (Element.hasClassName(f, "f-drop-footer-info") || Element.hasClassName(f, "f-drop-flow-footer-info")) {
                    d = _frevvo.baseView.getControlId(_frevvo.formView.$rootControl(_frevvo.baseView.getForm()));
                } else {
                    if (Element.hasClassName(f, "f-info")) {
                        d = _frevvo.baseView.getControlId(f.parentNode.parentNode);
                        f = f.parentNode.parentNode;
                    } else {
                        d = _frevvo.baseView.getView(f).getControlId(f);
                    }
                }
            }
        }
        _frevvo.forms.createControl(g.$itemType(c), d, b, e, a, {
            where: b,
            paletteItem: c,
            destinationElement: f
        });
    },
    addNodeType: function(b, a, d, c) {
        _frevvo.forms.createControlFromNodeType(b, a, d, c, {
            id: a
        });
    },
    bindNodeTypeToControl: function(b, a, e, d, f, c) {
        _frevvo.forms.bindNodeTypeToControl(b, a, d, f, c, {
            control: e
        });
    },
    addRule: function(c, b, a) {
        _frevvo.forms.addRule(c, b, a);
    },
    removeRule: function(c, d, b, a) {
        _frevvo.forms.removeRule(c, d, b, a);
    },
    updateRule: function(c, d, e, b, a) {
        var f = JSONUtil.objectToJSONString(RulesEditView.getState(c));
        _frevvo.forms.updateRule(d, f, e, b, a);
    },
    loadControlChildren: function(c, d, b, a) {
        _frevvo.forms.loadControlChildren(c, d, b, a);
    },
    reloadFormOutlineTree: function(c, b, a) {
        _frevvo.forms.reloadFormOutlineTree(c, b, a);
    },
    notifyExpansionStateChange: function(e, a, b, f, d, c) {
        _frevvo.forms.notifyExpansionStateChange(e, a, b, f, d, c);
    },
    loadElementChildren: function(e, a, d, c, b) {
        _frevvo.forms.loadElementChildren(e, a, d, c, b);
    },
    addDocumentType: function(d, c, a, e, b) {
        _frevvo.forms.addDocumentType(d, c, a, e, b, {});
    },
    removeDocumentType: function(d, c, a, b) {
        _frevvo.forms.removeDocumentType(d, c, a, {
            el: b
        });
    },
    removeControl: function(c, e, b) {
        var a = _frevvo.baseView.getView(c);
        var d = a.getControlId(c);
        _frevvo.forms.deleteControlType(d, e, b, {
            el: c
        });
    }
};
_frevvo.jsonProcessor = {
    toString: function() {
        return "JSON Processor";
    },
    states: [],
    tableColHdrs: [],
    removeControls: function(c) {
        try {
            if (c.removedControls) {
                if (_frevvo.utilities.util.isString(c.removedControls)) {
                    this.states[0] = c.removedControls;
                } else {
                    this.states = $A(c.removedControls);
                }
                var g = [];
                for (var b = 0; b < this.states.length; b++) {
                    var f = document.getElementById(this.states[b]);
                    if (f) {
                        var e = _frevvo.baseView.getRepeatControlForRepeatItem(f);
                        if (e) {
                            var h = _frevvo.baseView.getControlId(e);
                            var d = g.detect(function(j) {
                                var i = (_frevvo.baseView.getControlId(j) == h);
                                return i;
                            });
                            if (!d) {
                                g.push(e);
                            }
                        }
                        Element.remove(f);
                    }
                }
                for (var b = 0; b < g.length; b++) {
                    var a = _frevvo.baseView.getView(g[b]);
                    if (a.isEnabled(g[b])) {
                        RepeatController.setupAddRepeatItemObservers(g[b]);
                        RepeatController.setupRemoveRepeatItemObservers(g[b]);
                    }
                }
            }
        } finally {
            this.states && this.states.clear();
        }
    },
    updateStates: function(c) {
        try {
            var d = _frevvo.baseView.getForm();
            if (d) {
                _frevvo.formView.setStateU(d, c);
            }
            this.tableColHdrs = [];
            var e = c.controls;
            if (e) {
                this.states = $A(e);
                if (this.states.length < 1) {
                    this.states[0] = e;
                }
                var b = false;
                for (var a = 0; a < this.states.length; a++) {
                    var f = document.getElementById(this.states[a].id);
                    if (this.states[a].status) {
                        b = true;
                    }
                    this.updateState(f, this.states[a], false, b);
                    b = false;
                }
            }
            _frevvo.jsonProcessor.updateFlowState(c, c.readOnly);
        } finally {
            this.states && this.states.clear();
        }
    },
    updateEditStates: function(f, a, e) {
        try {
            if (f.controls) {
                this.states = $A(f.controls);
                if (this.states.length < 1) {
                    this.states[0] = f.controls;
                }
                this.tableColHdrs = [];
                for (var d = 0; d < this.states.length; d++) {
                    if (this.states[d].id == a) {
                        continue;
                    }
                    var c = document.getElementById(this.states[d].id);
                    if (c) {
                        if (e && ((e & FrevvoConstants.REFRESH_EDITOR) > 0)) {
                            _frevvo.baseView.removeEditor(c);
                        } else {
                            var b = _frevvo.baseView.$editContainer(c);
                            if (b) {
                                BaseEditView.setState(b, this.states[d]);
                            }
                        }
                    }
                }
            }
        } finally {
            this.states && this.states.clear();
        }
    },
    updateState: function(d, e, f, b) {
        if (!e) {
            return;
        }
        var c = this.states;
        if (!d) {
            if (!e.rcid) {
                return;
            }
            _frevvo.uberController.controlState.doIt(e.id, e.rcid, e.rIndex, false, e.enabled);
            this.states = c;
            return;
        }
        var a = _frevvo.baseView.getView(d);
        if (a) {
            a.setState(d, e, b);
            if (f) {
                a.setTyped(d);
                if (e.minOccurs > 0) {
                    a.setTypeRequired(d);
                }
            }
        }
        this.states = c;
    },
    print: function(b, a) {
        _frevvo.utilities.debugPrint("------------ Begin " + a + " ------------");
        for (property in b) {
            _frevvo.utilities.debugPrint(" " + property + "='" + b[property] + "'");
        }
        _frevvo.utilities.debugPrint("------------ End " + a + " ------------");
    },
    updateFlowState: function(b, g) {
        var d = b["flow-state"];
        if (d) {
            var f = $("flow-footer-buttons");
            if (f) {
                g = g || false;
                var e = d.readOnly || false;
                var c = _frevvo.protoView.$childByClass(f, "save-button");
                if (c) {
                    _frevvo.jsonProcessor.updateButtonState(c, !g);
                }
                var a = _frevvo.protoView.$childByClass(f, "reject-button");
                if (a) {
                    if (g) {
                        _frevvo.jsonProcessor.updateButtonState(a, !g && d.reject);
                    } else {
                        if (d.reject) {
                            Element.removeClassName(a, "s-hidden");
                        } else {
                            Element.addClassName(a, "s-hidden");
                        }
                    }
                }
                if (d.previous != null) {
                    _frevvo.jsonProcessor.updateButtonState(_frevvo.protoView.$childByClass(f, "previous-button"), !e && d.previous);
                }
                if (d.next != null) {
                    _frevvo.jsonProcessor.updateButtonState(_frevvo.protoView.$childByClass(f, "next-button"), !e && d.next);
                    _frevvo.jsonProcessor.updateButtonState(_frevvo.protoView.$childByClass(f, "flow-button"), !e && d.next);
                    _frevvo.jsonProcessor.updateButtonState(_frevvo.protoView.$childById(f, "flow-fast-forward-button"), !e && d.next);
                }
                if (d.finish != null) {
                    _frevvo.jsonProcessor.updateButtonState(_frevvo.protoView.$childByClass(f, "finish-button"), !e && d.finish);
                    _frevvo.jsonProcessor.updateButtonState(_frevvo.protoView.$childByClass(f, "flow-button"), !e && d.finish);
                }
            }
        }
    },
    updateButtonState: function(a, b) {
        if (a) {
            if (b == true) {
                Element.removeClassName(a, "s-disabled");
            } else {
                Element.addClassName(a, "s-disabled");
            }
        }
    }
};
_frevvo.myRules = {};
try {
    if (_frevvo.utilities.util.isDefined(_frevvo.formController)) {
        Object.extend(_frevvo.myRules, _frevvo.formController.rules);
    }
} catch (e) {}
try {
    if (_frevvo.utilities.util.isDefined(FlowController)) {
        Object.extend(_frevvo.myRules, FlowController.rules);
    }
} catch (e) {}
try {
    if (_frevvo.utilities.util.isDefined(AppRules)) {
        Object.extend(_frevvo.myRules, AppRules.rules);
    }
} catch (e) {}
try {
    if (_frevvo.utilities.util.isDefined(_frevvo.uberController)) {
        Object.extend(_frevvo.myRules, _frevvo.uberController.rules);
    }
} catch (e) {}
Object.extend(_frevvo.myRules, {
    "#root-control": function(a) {
        if (_frevvo.uberController.editMode) {
            UberEditController.setupDraggables();
            UberEditController.setup(a);
        }
        if (Element.hasClassName(a, "s-phone")) {
            _frevvo.uberController.mobile = true;
            _frevvo.groupController.setupChildren(a);
            _frevvo.phoneController.swipe.setup($("sw-nav-buttons"));
        } else {
            _frevvo.groupController.setupChildren(a);
        }
    },
    "#f-toolbox": function(a) {
        if (_frevvo.uberController.editMode) {
            ToolboxController.setup(a);
        }
    },
    "#palette": function(b) {
        var a = _frevvo.protoView.$childrenByName(b, "div");
        EditController.left.selected = a[0];
        EditController.left.setup(a[0], a[1], true);
    },
    "#user-palette": function(b) {
        var a = _frevvo.protoView.$childrenByName(b, "div");
        EditController.left.setup(a[0], a[1], Element.hasClassName(a[0], "s-visible"));
    },
    "#editProperties": function(b) {
        var a = _frevvo.protoView.$childrenByName(b, "div");
        EditController.left.setup(a[0], a[1], Element.hasClassName(a[0], "s-visible"));
    },
    "#themes": function(b) {
        var a = _frevvo.protoView.$childrenByName(b, "div");
        EditController.left.setup(a[0], a[1], Element.hasClassName(a[0], "s-visible"));
    },
    "#documentTypes": function(b) {
        var a = _frevvo.protoView.$childrenByName(b, "div");
        EditController.left.setup(a[0], a[1], Element.hasClassName(a[0], "s-visible"));
    },
    "#formOutline": function(b) {
        var a = _frevvo.protoView.$childrenByName(b, "div");
        EditController.left.setup(a[0], a[1], Element.hasClassName(a[0], "s-visible"));
    },
    "#mappingFormOutline": function(b) {
        var a = _frevvo.protoView.$childrenByName(b, "div");
        EditController.left.setup(a[0], a[1], Element.hasClassName(a[0], "s-visible"));
    },
    "#schemas": function(b) {
        var a = _frevvo.protoView.$childrenByName(b, "div");
        EditController.left.setup(a[0], a[1], Element.hasClassName(a[0], "s-visible"));
    },
    "#c-palette": function(a) {
        PaletteController.setup(a);
    },
    "#c-user-palette": function(a) {
        PaletteController.setup(a);
    },
    "#c-themes": function(a) {
        ThemesController.theme.setup(a);
    },
    "#c-doctypes": function(a) {
        DocTypeController.setup(a);
    },
    "#c-schemas": function(a) {
        SchemasController.setup(a);
    },
    "#c-formoutline": function(a) {
        FormOutlineController.setup(a);
    },
    "#c-mappingformoutline": function(a) {
        MappingFormOutlineController.setup(a, false);
        GeneratedFormMappingController.formoutline.setup(a);
    },
    "#e-pilcrow-form": function(a) {
        EditController.pilcrow.setup(a);
    },
    "#e-edit-form": function(a) {
        EditController.edit.setup(a);
    },
    "#e-rules-form": function(a) {
        RulesController.rulesTab.setup(a);
    },
    "#e-edit-pdf-forms": function(a) {
        GenFormsController.genFormsTab.setup(a);
    },
    "#f-form-tb-1": function(a) {
        FormEditController.edit.setup(a);
    },
    "#f-rules": function(a) {
        RulesController.formRules.setup(a);
    },
    "#commit-top": function(a) {
        EditController.commitCancelChanges.setup(a);
    },
    "#cancel-top": function(a) {
        EditController.commitCancelChanges.setup(a);
    },
    "#commit-bot": function(a) {
        EditController.commitCancelChanges.setup(a);
    },
    "#cancel-bot": function(a) {
        EditController.commitCancelChanges.setup(a);
    },
    "#page-form-edit": function(a) {
        EditController.trial.setup(a);
    },
    ".flow-summary": function(a) {
        if (Element.hasClassName(a, "phone")) {
            _frevvo.uberController.mobile = true;
        }
        if (_frevvo.uberController.mobile) {
            _frevvo.phoneController.setupSummary();
        }
    }
});
Behaviour.register(_frevvo.myRules);