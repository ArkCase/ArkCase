var virtualViewer = new VirtualViewer();
var myFlexSnap = virtualViewer;
function VirtualViewer() {
    URI.escapeQuerySpace = false;
    var A1 = this;
    var AP = false;
    var A7 = null;
    var d = false;
    var A;
    var AG;
    var BY;
    var BU;
    var CK = false;
    var AV = null;
    var A2 = null;
    var Cy = false;
    var B4 = 0;
    var Cx = 0;
    var AJ = false;
    var CW;
    var BF;
    var BK = false;
    var AI;
    var q = [];
    var Av;
    var Aq;
    var B3 = 0;
    var AE = "resources/loader.gif";
    var Ae = window.devicePixelRatio || 1;
    var AC = {};
    var B1 = 0;
    var B5 = 0;
    this.currentStamp = null;
    this.currentImageRubberStamp = null;
    var Au = null;
    this.documentList = [];
    this.openDocuments = {};
    this.recentlyViewedList = [];
    this.activeTab = 0;
    this.tabs = [];
    var R = "";
    var CF = 0;
    var H = false;
    var t = false;
    var p = false;
    var CV = false;
    var CU = false;
    var Bh = false;
    var CB = 0;
    var BI = 0;
    var Ai = 0;
    var I = vvDefines.defaultGammaValue;
    var Cr = 0;
    var B9 = 0;
    var i = 0;
    var Ao = 0;
    var Bl = 0;
    var Ch = null;
    var BB = 0;
    var J = null;
    var Cl = null;
    var AL = null;
    var y = "";
    var B7 = null;
    var Cp = null;
    var By = null;
    var F = null;
    var CD = null;
    var AK = null;
    var o = null;
    var c = null;
    var B8 = null;
    var CL = null;
    var BC = null;
    var AW = null;
    var CI = null;
    var Ca = null;
    var A8 = null;
    var BQ = null;
    var AX = null;
    var m = null;
    var v = false;
    var P = null;
    var Bc = null;
    var Bb = null;
    var BL = 0;
    var BX = false;
    var CE = false;
    var V = null;
    var Aw = null;
    var s = null;
    D();
    var Bj = {
        bindings: {
            vvSelectTextMenuCopy: function (Cz) {
                virtualViewer.copySelectedText()
            }
        }, offsetHeight: 10, onContextMenu: function (C2) {
            var C0 = virtualViewer.getSelectedTextRects();
            if (!C2) {
                C2 = window.event
            }
            if (Modernizr.touch) {
                C2 = Bv(C2)
            }
            var C3 = Af(vvInnerDiv);
            var Cz = AH(C2.clientX - C3[0], C2.clientY - C3[1]);
            var C1 = myPainter.isAnnotationAtPoint(Cz);
            if (C1) {
                return false
            }
            if (C0 && C0.length > 0) {
                return true
            } else {
                return false
            }
        }, onShowMenu: function (Cz, C0) {
            return C0
        }
    };
    var W = {
        bindings: {
            vvSelectPagesAll: function (Cz) {
                A1.selectAll()
            }, vvSelectPagesNone: function (Cz) {
                A1.selectNone()
            }, vvDeletePages: function (Cz) {
                $("#vvPageThumbs").trigger("vvDeletePages")
            }, vvCutPages: function (Cz) {
                $("#vvPageThumbs").trigger("vvCutPages")
            }, vvCopyPages: function (Cz) {
                $("#vvPageThumbs").trigger("vvCopyPages")
            }, vvInsertPagesBefore: function (Cz) {
                $("#vvPageThumbs").trigger("vvPastePages", Cz.rowIndex)
            }, vvInsertPagesAfter: function (Cz) {
                $("#vvPageThumbs").trigger("vvPastePages", Cz.rowIndex + 1)
            }, vvCopyPagesToNewDocument: function (Cz) {
                $("#vvNewDocumentDialog").dialog("option", "cutDocument", false);
                if (BC) {
                    A1.createNewDocument(BC())
                } else {
                    $("#vvNewDocumentDialog").dialog("open")
                }
            }, vvCutPagesToNewDocument: function (Cz) {
                $("#vvNewDocumentDialog").dialog("option", "cutDocument", true);
                if (BC) {
                    A1.createNewDocument(BC())
                } else {
                    $("#vvNewDocumentDialog").dialog("open")
                }
            }
        }, onContextMenu: function (Cz) {
            if (Cz.target.id === "vvPageThumbs") {
                if (A1.getPageCount() > 0) {
                    return false
                }
            }
            if (!A1.checkPageManipulationsEnabled(true)) {
                return false
            }
            return true
        }, onShowMenu: function (C0, C1) {
            var Cz = virtualViewer.getDocumentModel();
            var C2 = $(C0.target).closest("tr").index();
            if ((C0.ctrlKey === true) || (C0.metaKey === true)) {
            } else {
                if (C0.shiftKey === true) {
                } else {
                    if (Cz.isPageSelected(C2) !== true) {
                        Cz.clearPageSelection();
                        Cz.addPageToSelection(C2)
                    }
                }
            }
            if (!Bo || !Bo.model || (Bo.model.length === 0)) {
                $("#vvInsertPagesBefore", C1).remove();
                $("#vvInsertPagesAfter", C1).remove()
            }
            if (Cz.getSelectedPageNumbers().length === 0) {
                $("#vvCutPages", C1).remove();
                $("#vvCopyPages", C1).remove();
                $("#vvCopyNewDocument", C1).remove();
                $("#vvCutNewDocument", C1).remove()
            }
            if (vvConfig.pageManipulationsNewDocumentMenu !== true) {
                $("#vvCopyPagesToNewDocument", C1).remove();
                $("#vvCutPagesToNewDocument", C1).remove()
            }
            if (virtualViewer.getPageCount() === 0) {
                $("#vvCutPages", C1).remove();
                $("#vvCopyPages", C1).remove();
                $("#vvDeletePages", C1).remove();
                $("#vvSelectPagesAll", C1).remove();
                $("#vvSelectPagesNone", C1).remove();
                $("#vvInsertPagesAfter", C1).remove()
            }
            return C1
        }
    };
    VirtualViewer.prototype.getServerConfig = function () {
        var Cz = new URI(vvConfig.servletPath);
        Cz.addQuery("action", "getServerConfig");
        var C0 = Cz.query();
        Cz.query("");
        $.ajax({
            url: Cz.toString(), type: "POST", data: C0, dataType: "json", success: function (C1) {
                A5(C1)
            }, error: function (C1) {
                console.log("getServerConfig: error")
            }
        })
    };
    VirtualViewer.prototype.getScrollbarWidth = function () {
        var C2 = document.createElement("div");
        C2.style.visibility = "hidden";
        C2.style.width = "100px";
        document.body.appendChild(C2);
        var C0 = C2.offsetWidth;
        C2.style.overflow = "scroll";
        var Cz = document.createElement("div");
        Cz.style.width = "100%";
        C2.appendChild(Cz);
        var C1 = Cz.offsetWidth;
        C2.parentNode.removeChild(C2);
        return C0 - C1
    };
    VirtualViewer.prototype.init = function (Cz) {
        $("[data-localize]").localize("vv", vvDefines.localizeOptions);
        if (Modernizr.svg) {
            $(".vvSVGable").removeClass("vvSVGable").addClass("vvSVG")
        }
        Bq();
        A1.windowWidth = $(window).width();
        A1.windowHeight = $(window).height();
        Bl = this.getScrollbarWidth();
        this.getServerConfig();
        this.validateZoomLevels();
        this.initToolbar();
        var C1 = document.getElementById("vvOuterDiv");
        if (!Modernizr.touch) {
            $("[title]").each(function (C2) {
                var C3 = $(this).attr("title");
                C3 = $("<div />").html(C3).text();
                $(this).attr("title", C3)
            });
            $("#vvToolbar").children().tooltip({show: false, hide: false});
            $("#vvAnnToolbar").children().tooltip({show: false, hide: false});
            $("input").tooltip({show: false, hide: false})
        } else {
            $("head").append('<link type="text/css" rel="stylesheet" href="css/touch.css">');
            vvConfig.imageScrollBars = true;
            $(document).on("orientationchange", function () {
                var C2 = A1.getStateForDocumentId(A1.getDocumentId());
                if (C2) {
                    A1.loadVisibleThumbs(null, C2, false, false)
                }
            });
            $(document).on("touchmove", function (C2) {
                if (!jQuery.contains($("#vvThumbs")[0], C2.target) && !jQuery.contains($("#vvImageInfoDialog")[0], C2.target) && !jQuery.contains($("#vvOuterDiv")[0], C2.target)) {
                    C2.preventDefault()
                }
            });
            Hammer("#vvDummyScroller").on("doubletap", function (C2) {
                A1.annPopUpHandler(C2)
            })
        }
        if (vvConfig.oneLayerPerAnnotation === true) {
            $("#vvLayerManagerDiv").remove();
            $("#vvLayerManagerButton").remove()
        }
        AZ();
        Aj();
        if (A1.checkPageManipulationsEnabled()) {
            vvDefines.cacheBuster = true
        }
        if (vvConfig.imageScrollBars === true) {
            C1.style.overflow = "scroll"
        } else {
            C1.style.overflow = "hidden"
        }
        if ((BrowserDetect.browser === "Explorer") && (BrowserDetect.version < 9)) {
            vvDefines.cacheBuster = true
        }
        Y();
        CS();
        AS();
        Az();
        Cm();
        CM();
        l();
        $("#vvDummyScroller").contextMenu("vvSelectTextContextMenu", Bj);
        $("#vvLayerManagerActiveLayerField").children("input").each(function () {
            var C2 = $(this).val();
            $(this).val($("<div />").html(C2).text())
        });
        $("#vvJumpToPageInput").bind("click", function (C2) {
            $("#vvJumpToPageInput").select()
        });
        $("#vvJumpToPageInput").bind("focusout keyup", function (C3) {
            var C2 = parseInt($("#vvJumpToPageInput").val(), 10);
            if (C3.type === "focusout") {
                if (!A1.showingPageErrorDialog) {
                    A1.currentMatch = 0;
                    A1.setPage(C2 - 1)
                }
            } else {
                if (C3.which === 10 || C3.which === 13) {
                    A1.currentMatch = 0;
                    A1.setPage(C2 - 1)
                }
            }
        });
        AT(vvDefines.dragModes.pan);
        if ((BrowserDetect.browser === "Explorer") && (BrowserDetect.version < 9)) {
            $(C1).on("dragstart", function () {
                return false
            })
        }
        $("body").click(function (C3) {
            if (($(C3.target).closest("#vvPageThumbsInternal").length === 0) && (!$("#vvNewDocumentDialog").dialog("isOpen"))) {
                var C2 = A1.getDocumentModel();
                if (C2) {
                    C2.clearPageSelection()
                }
            }
        });
        $("#vvPageThumbs").scroll(Q);
        $("#vvDocThumbs").scroll(Q);
        $("#vvSearchResults").scroll(Q);
        $(window).resize(function (C2) {
            if ((A1.windowWidth !== $(window).width()) && (A1.windowHeight !== $(window).height())) {
                A1.windowWidth = $(window).width();
                A1.windowHeight = $(window).height()
            }
        });
        $(window).resize(A1.processWindowResize);
        var C0 = parseInt(b("pageNumber"), 10);
        if (isNaN(C0) !== true) {
            CF = C0
        } else {
            CF = 0
        }
        Bh = true;
        if (vvConfig.multipleDocMode !== vvDefines.multipleDocModes.viewedDocuments && (vvConfig.showDocThumbnails === true || vvConfig.showPageThumbnails === true)) {
            this.requestDocumentList();
            AO(null, true);
            A1.loadVisibleThumbs(null, null, true)
        }
        if (Cz !== false) {
            this.openInTab(A1.getDocumentId())
        }
        if (vvConfig.unsavedChangesNotification) {
            $(window).bind("beforeunload", function () {
                var C3 = 0;
                while (C3 < A1.tabs.length) {
                    var C4 = A1.getStateForDocumentId(A1.tabs[C3]);
                    var C2 = C4.getDocumentModel();
                    if (C2.getModifiedSinceInit()) {
                        return getLocalizedValue("browserClose.message", "You have unsaved changes in this session.")
                    } else {
                        C3++
                    }
                }
            })
        }
        $("#vvOuterDiv").scroll(function () {
            var C3 = A1.getCurrentState();
            if (C3) {
                var C2 = C3.getImageObject();
                if (Ce(C2)) {
                    A1.paintCanvases(C3)
                }
            }
        })
    };
    var Bq = function () {
        if (!Array.prototype.indexOf) {
            Array.prototype.indexOf = function (C1, C2) {
                for (var C0 = (C2 || 0), Cz = this.length; C0 < Cz; C0++) {
                    if (this[C0] === C1) {
                        return C0
                    }
                }
                return -1
            }
        }
    };
    VirtualViewer.prototype.validateZoomLevels = function () {
        var C0 = true;
        if (!vvConfig.zoomLevels) {
            C0 = false
        } else {
            for (var Cz = 0; Cz < vvConfig.zoomLevels.length; Cz++) {
                var C1 = vvConfig.zoomLevels[Cz];
                if (Cz > 0 && C1 <= vvConfig.zoomLevels[Cz - 1]) {
                    C0 = false
                }
                if (C1 <= 0) {
                    C0 = false
                }
            }
        }
        if (!C0) {
            vvConfig.zoomLevels = vvDefines.defaultZoomLevels
        }
    };
    VirtualViewer.prototype.initViaURL = function () {
        var Cz = b("documentId");
        if (Cz) {
            A1.setDocumentId(decodeURIComponent(Cz))
        }
        var C0 = b("pageNumber");
        this.setClientInstanceId(this.parseClientInstanceId());
        var C1 = b("openerHistoryBack");
        if (C1 === "true") {
            window.opener.history.back(1)
        }
        this.init(true);
        if (C0) {
            this.setPage(parseInt(C0, 10))
        }
        Bz(document.getElementById("vvToolbar"));
        Bz(document.getElementById("vvAnnToolbar"));
        Bz(document.getElementById("vvDummyScroller"));
        Bz(document.getElementById("vvTextAnnotationContainer"));
        document.getElementById("vvJumpToPageInput").setAttribute("unselectable", "off")
    };
    VirtualViewer.prototype.initSpecifiedDocuments = function (C0) {
        this.documentList = C0;
        var C1 = b("openerHistoryBack");
        if (C1 === "true") {
            window.opener.history.back(1)
        }
        this.init(false);
        for (var Cz = 0; Cz < this.documentList.length; Cz += 1) {
            this.openInTab(this.documentList[Cz])
        }
    };
    var Y = function () {
        $("#vvWaitIndicator").dialog({
            modal: true,
            closeOnEscape: false,
            draggable: false,
            resizable: false,
            dialogClass: "vvWaitIndicatorDialogClass",
            width: 400,
            height: 100,
            autoOpen: false,
            open: function (C5, C6) {
                $(this).parent().children().children(".ui-dialog-titlebar-close").hide()
            }
        });
        $("#vvSaveDocumentDialog").dialog({
            modal: true,
            closeOnEscape: false,
            draggable: false,
            resizable: false,
            dialogClass: "vvSaveDocumentDialogClass",
            autoOpen: false,
            open: function (C5, C6) {
                $(this).parent().children().children(".ui-dialog-titlebar-close").hide()
            }
        });
        $("#vvPrintIndicator").dialog({
            modal: true,
            closeOnEscape: false,
            draggable: false,
            resizable: false,
            dialogClass: "vvPrintIndicatorDialogClass",
            width: 450,
            height: 100,
            autoOpen: false,
            open: function (C5, C6) {
                $(this).parent().children().children(".ui-dialog-titlebar-close").hide()
            }
        });
        $("#vvLocalPrintDialog").dialog({
            modal: true,
            draggable: false,
            resizable: false,
            dialogClass: "vvPrintDialogClass",
            autoOpen: false,
            buttons: [{
                text: getLocalizedValue("printDialog.printButton", "Print"), click: function () {
                    if ($("#vvPrintAnnotationsSelected").is(":checked")) {
                        vvConfig.printBurnAnnotations = true
                    } else {
                        vvConfig.printBurnAnnotations = false
                    }
                    $("#vvPrintFrame").remove();
                    $("#vvPrintFramePanel").remove();
                    var C6 = document.createElement("div");
                    C6.name = "vvPrintFramePanel";
                    C6.id = "vvPrintFramePanel";
                    var DC = document.createElement("iframe");
                    DC.name = "vvPrintFrame";
                    DC.id = "vvPrintFrame";
                    if (vvConfig.enableEnhancedLocalPrintingBeta) {
                        var DA = A1.getCurrentState();
                        var DB = $("#vvPrintFirstPage").val();
                        var C9 = $("#vvPrintLastPage").val();
                        var C8 = $("#vvPrintAnnotationsTypeText").is(":checked");
                        var C7 = $("#vvPrintAnnotationsTypeNonText").is(":checked");
                        var C5 = M(DA, "pages", DB + "-" + C9, C8, C7);
                        if ((BrowserDetect.browser === "Chrome") || (BrowserDetect.browser === "Safari")) {
                            DC.src = C5.toString();
                            T()
                        } else {
                            if (BrowserDetect.browser === "Firefox") {
                                window.open(C5.toString())
                            }
                        }
                    } else {
                        DC.src = "resources/print.html"
                    }
                    $("#virtualViewerMain").append(C6);
                    $("#virtualViewerMain").append(DC);
                    Bt($("#vvPrintFirstPage").val() + "-" + $("#vvPrintLastPage").val());
                    $(this).dialog("close")
                }
            }, {
                text: getLocalizedValue("printDialog.cancelButton", "Cancel"), click: function () {
                    $(this).dialog("close")
                }
            }],
            title: getLocalizedValue("printDialog.title", "Print")
        });
        $("#vvNewPrintDialog").dialog({
            modal: true,
            draggable: false,
            resizable: false,
            width: 520,
            height: 550,
            dialogClass: "vvNewPrintDialogClass",
            autoOpen: false,
            open: function (C5, C6) {
                if ($("#vvNewPrintAnnotationsSelected").is(":checked")) {
                    $("#vvNewPrintAnnotationsTypeText").prop("disabled", false);
                    $("#vvNewPrintAnnotationsTypeNonText").prop("disabled", false);
                    $("#vvNewPrintAnnotationsTypeText").prop("checked", true);
                    $("#vvNewPrintAnnotationsTypeNonText").prop("checked", true)
                } else {
                    $("#vvNewPrintAnnotationsTypeText").prop("disabled", true);
                    $("#vvNewPrintAnnotationsTypeNonText").prop("disabled", true);
                    $("#vvNewPrintAnnotationsTypeText").prop("checked", false);
                    $("#vvNewPrintAnnotationsTypeNonText").prop("checked", false)
                }
                if ($("input[name=vvNewPrintMethod]:checked").val() === "vvNewPrintPrinterExport") {
                    $("#vvNewPrintGraySelected").prop("checked", false);
                    $("#vvNewPrintGraySelected").prop("disabled", true);
                    $("#vvNewPrintColorSelected").prop("checked", true)
                } else {
                    $("#vvNewPrintColorSelected").prop("disabled", false);
                    $("#vvNewPrintGraySelected").prop("disabled", false)
                }
            },
            buttons: [{
                text: getLocalizedValue("printDialog.printButton", "Print"), click: function () {
                    Ax(true);
                    var C5 = virtualViewer.serverSidePrint();
                    if (C5) {
                        $(this).dialog("close")
                    }
                }
            }, {
                text: getLocalizedValue("printDialog.cancelButton", "Cancel"), click: function () {
                    $(this).dialog("close")
                }
            }],
            title: getLocalizedValue("printDialog.title", "Print")
        });
        $("#vvPrintAnnotationsSelected").change(function () {
            if ($(this).is(":checked")) {
                $("#vvPrintAnnotationsTypeCheckboxes").removeClass("vvPrintAnnotationsDisabled");
                $("#vvPrintAnnotationsTypeText").prop("disabled", false);
                $("#vvPrintAnnotationsTypeNonText").prop("disabled", false)
            } else {
                $("#vvPrintAnnotationsTypeCheckboxes").addClass("vvPrintAnnotationsDisabled");
                $("#vvPrintAnnotationsTypeText").prop("disabled", true);
                $("#vvPrintAnnotationsTypeNonText").prop("disabled", true)
            }
        });
        $("#vvNewPrintAnnotationsSelected").change(function () {
            if ($(this).is(":checked")) {
                $("#vvNewPrintAnnotationsTypeCheckboxes").removeClass("vvNewPrintAnnotationsDisabled");
                $("#vvNewPrintAnnotationsTypeText").prop("disabled", false);
                $("#vvNewPrintAnnotationsTypeNonText").prop("disabled", false);
                $("#vvNewPrintAnnotationsTypeText").prop("checked", true);
                $("#vvNewPrintAnnotationsTypeNonText").prop("checked", true)
            } else {
                $("#vvNewPrintAnnotationsTypeCheckboxes").addClass("vvNewPrintAnnotationsDisabled");
                $("#vvNewPrintAnnotationsTypeText").prop("disabled", true);
                $("#vvNewPrintAnnotationsTypeNonText").prop("disabled", true);
                $("#vvNewPrintAnnotationsTypeText").prop("checked", false);
                $("#vvNewPrintAnnotationsTypeNonText").prop("checked", false)
            }
        });
        $("#vvNewPrintPrinterExport").change(function () {
            if ($("input[name=vvNewPrintMethod]:checked").val() === "vvNewPrintPrinterExport") {
                $("#vvNewPrintGraySelected").prop("checked", false);
                $("#vvNewPrintGraySelected").prop("disabled", true);
                $("#vvNewPrintColorSelected").prop("checked", true)
            } else {
                $("#vvNewPrintColorSelected").prop("disabled", false);
                $("#vvNewPrintGraySelected").prop("disabled", false)
            }
        });
        $("#vvNewPrintPrinterServer").change(function () {
            if ($("input[name=vvNewPrintMethod]:checked").val() === "vvNewPrintPrinterExport") {
                $("#vvNewPrintGraySelected").prop("checked", false);
                $("#vvNewPrintGraySelected").prop("disabled", true);
                $("#vvNewPrintColorSelected").prop("checked", true)
            } else {
                $("#vvNewPrintColorSelected").prop("disabled", false);
                $("#vvNewPrintGraySelected").prop("disabled", false)
            }
        });
        if (vvConfig.printShowTypeToggles) {
            $("#vvPrintOptionsAnnotationsTypeCheckboxes").show();
            $("#vvNewPrintOptionsAnnotationsTypeCheckboxes").show()
        } else {
            $("#vvPrintOptionsAnnotationsTypeCheckboxes").hide();
            $("#vvNewPrintOptionsAnnotationsTypeCheckboxes").hide()
        }
        $("#vvExportDialog").dialog({
            modal: true,
            draggable: false,
            resizable: false,
            dialogClass: "vvExportDialogClass",
            width: 400,
            height: 465,
            autoOpen: false,
            open: function (C5, C6) {
                $("#vvExportAnnotationsSelected").prop("checked", vvConfig.exportBurnAnnotations);
                $("#vvExportAnnotationsTypeText").prop("checked", vvConfig.exportBurnAnnotations);
                $("#vvExportAnnotationsTypeNonText").prop("checked", vvConfig.exportBurnAnnotations);
                if ($("#vvExportAnnotationsSelected").is(":checked")) {
                    $("#vvExportOriginalSelected").prop("disabled", true);
                    $("#vvExportOriginalText").addClass("vvExportDisabled");
                    $("#vvExportAnnotationsTypeText").prop("disabled", false);
                    $("#vvExportAnnotationsTypeNonText").prop("disabled", false)
                } else {
                    if (A1.getDocumentId().indexOf("VirtualDocument") > -1) {
                        $("#vvExportOriginalSelected").prop("disabled", true);
                        $("#vvExportOriginalText").addClass("vvExportDisabled")
                    } else {
                        $("#vvExportOriginalSelected").prop("disabled", false);
                        $("#vvExportOriginalText").removeClass("vvExportDisabled")
                    }
                    $("#vvExportAnnotationsTypeText").prop("disabled", true);
                    $("#vvExportAnnotationsTypeNonText").prop("disabled", true)
                }
            },
            buttons: [{
                text: getLocalizedValue("exportDialog.exportButton", "Export"), click: function () {
                    if ($("#vvExportAnnotationsSelected").is(":checked")) {
                        vvConfig.exportBurnAnnotations = true
                    } else {
                        vvConfig.exportBurnAnnotations = false
                    }
                    var C6 = "Original";
                    if ($("#vvExportPDFSelected:checked").val()) {
                        C6 = "PDF"
                    } else {
                        if ($("#vvExportTIFFSelected:checked").val()) {
                            C6 = "TIFF"
                        }
                    }
                    var C5 = Ci(C6);
                    if (C5) {
                        AR(C6);
                        $(this).dialog("close")
                    }
                }
            }, {
                text: getLocalizedValue("exportDialog.cancelButton", "Cancel"), click: function () {
                    $(this).dialog("close")
                }
            }],
            title: getLocalizedValue("exportDialog.title", "Export Document")
        });
        $("#vvExportTIFFSelected").change(function () {
            if ($(this).is(":checked")) {
                $("#vvExportRangePages").prop("disabled", false);
                $("#vvExportRangeComplex").prop("disabled", false);
                $("#vvExportRangeCurrent").prop("disabled", false)
            }
        });
        $("#vvExportPDFSelected").change(function () {
            if ($(this).is(":checked")) {
                $("#vvExportRangePages").prop("disabled", false);
                $("#vvExportRangeComplex").prop("disabled", false);
                $("#vvExportRangeCurrent").prop("disabled", false)
            }
        });
        $("#vvExportOriginalSelected").change(function () {
            if ($(this).is(":checked")) {
                $("#vvExportRangeAll").prop("checked", true);
                $("#vvExportRangePages").prop("disabled", true);
                $("#vvExportRangeComplex").prop("disabled", true);
                $("#vvExportRangeCurrent").prop("disabled", true)
            }
        });
        $("#vvExportAnnotationsSelected").change(function () {
            if ($(this).is(":checked")) {
                $("#vvExportOriginalSelected").prop("disabled", true);
                $("#vvExportAnnotationsTypeText").prop("disabled", false);
                $("#vvExportAnnotationsTypeNonText").prop("disabled", false);
                $("#vvExportAnnotationsTypeText").prop("checked", true);
                $("#vvExportAnnotationsTypeNonText").prop("checked", true);
                $("#vvExportOriginalText").addClass("vvExportDisabled");
                if ($("#vvExportOriginalSelected:checked").val()) {
                    $("#vvExportOriginalSelected").prop("checked", false);
                    $("#vvExportPDFSelected").prop("checked", true)
                }
            } else {
                $("#vvExportOriginalSelected").prop("disabled", false);
                $("#vvExportOriginalText").removeClass("vvExportDisabled");
                $("#vvExportAnnotationsTypeText").prop("disabled", true);
                $("#vvExportAnnotationsTypeNonText").prop("disabled", true);
                $("#vvExportAnnotationsTypeText").prop("checked", false);
                $("#vvExportAnnotationsTypeNonText").prop("checked", false)
            }
        });
        $("#vvPictureControlsDialog").dialog({
            dialogClass: "vvPictureControlsDialogClass",
            autoOpen: false,
            resizable: false,
            modal: true,
            height: 270,
            width: 335,
            title: getLocalizedValue("pictureControls.title", "Picture Controls"),
            buttons: [{
                text: getLocalizedValue("pictureControls.okButton", "OK"), click: function () {
                    $(this).dialog("close")
                }
            }, {
                text: getLocalizedValue("pictureControls.cancelButton", "CANCEL"), click: function () {
                    var C5 = A1.getStateForDocumentId(A1.getDocumentId());
                    if (C5) {
                        C5.setContrastForPage(CF, BI);
                        C5.setBrightnessForPage(CF, Ai);
                        C5.setGammaForPage(CF, I);
                        $("#vvContrastSlider").slider("value", BI);
                        $("#vvBrightnessSlider").slider("value", Ai);
                        $("#vvGammaSlider").slider("value", I);
                        $("#vvContrastDisplay").html(BI);
                        $("#vvBrightnessDisplay").html(Ai);
                        $("#vvGammaDisplay").html(I);
                        U()
                    }
                    $(this).dialog("close")
                }
            }]
        });
        $("#vvAboutDialog").dialog({
            modal: true,
            draggable: false,
            resizable: false,
            autoOpen: false,
            width: 320,
            dialogClass: "vvAboutDialogClass",
            title: getLocalizedValue("aboutDialog.productName", vvDefines.productName) + " " + getLocalizedValue("aboutDialog.versionPrefix", "v") + vvDefines.productVersion,
            buttons: [{
                text: getLocalizedValue("aboutDialog.okButton", "OK"), click: function () {
                    $(this).dialog("close")
                }
            }, {
                text: getLocalizedValue("aboutDialog.helpButton", "Help"), click: function () {
                    window.open(vvConfig.helpURL, vvConfig.helpWindowName, vvConfig.helpWindowParams);
                    $(this).dialog("close")
                }
            }]
        });
        $("#vvAboutDialogText").html(getLocalizedValue("aboutDialog.text", vvDefines.aboutDialogTextContents));
        $("#vvImageInfoDialog").dialog({
            modal: false,
            draggable: true,
            resizable: true,
            autoOpen: false,
            width: 450,
            height: 530,
            close: function () {
                document.body.scrollTop = document.documentElement.scrollTop = 0
            },
            dialogClass: "vvImageInfoClass",
            title: getLocalizedValue("imageInfo.dialogTitle", "Document & Page Properties"),
            buttons: [{
                text: getLocalizedValue("aboutDialog.okButton", "OK"), click: function () {
                    $(this).dialog("close")
                }
            }]
        });
        var C2 = getLocalizedValue("clipboardTitleStart", "Copy to clipboard");
        var C4 = "Ctrl-C";
        var C3 = navigator.platform.toUpperCase().indexOf("MAC") >= 0;
        if (C3) {
            C4 = "Command-C"
        }
        var C1 = "Enter";
        if (C3) {
            C1 = "Return"
        }
        var C0 = C2 + ": " + C4 + ", " + C1;
        var Cz = $("#vvClipboard").dialog({modal: true, width: 975, height: 500, autoOpen: false, title: C0});
        $("#vvClipboardTextarea").keypress(function (C5) {
            if (C5.keyCode === $.ui.keyCode.ENTER) {
                Cz.dialog("close")
            }
        });
        $("#vvEmailIndicator").dialog({
            modal: true,
            closeOnEscape: false,
            draggable: false,
            resizable: false,
            dialogClass: "vvEmailIndicatorDialogClass",
            width: 450,
            height: 100,
            autoOpen: false,
            open: function (C5, C6) {
                $(this).parent().children().children(".ui-dialog-titlebar-close").hide()
            }
        });
        $("#vvEmailDialog").dialog({
            modal: true,
            draggable: false,
            resizable: false,
            autoOpen: false,
            width: 670,
            dialogClass: "vvEmailDialogClass",
            title: getLocalizedValue("emailDialog.dialogTitle", "Email Document"),
            open: function (C5, C6) {
                if ($("#vvEmailAnnotationsSelected").is(":checked")) {
                    $("#vvEmailOriginalSelected").prop("disabled", true);
                    $("#vvEmailOriginalText").addClass("vvEmailDisabled");
                    $("#vvEmailAnnotationsTypeText").prop("disabled", false);
                    $("#vvEmailAnnotationsTypeNonText").prop("disabled", false)
                }
            },
            buttons: [{
                text: getLocalizedValue("emailDialog.sendButton", "Send"), click: function (DA) {
                    var DB = $(this);
                    var C9 = $("#vvEmailFrom").val();
                    var C7 = $("#vvEmailTo").val();
                    var C6 = $("#vvEmailCC").val();
                    var DC = $("#vvEmailBCC").val();
                    var C8 = $("#vvEmailSubject").val();
                    var C5 = $("#vvEmailBody").val();
                    C9.trim();
                    C7.trim();
                    if ((C9 === "") || (C9 === undefined)) {
                        $("#vvEmptyEmailFieldsText").html('The "From" Field needs to be filled out.');
                        $("#vvEmptyEmailFields").dialog("open")
                    }
                    if ((C7 === "") || (C7 === undefined)) {
                        $("#vvEmptyEmailFieldsText").html('The "To" Field needs to be filled out.');
                        $("#vvEmptyEmailFields").dialog("open")
                    } else {
                        Ak(true);
                        window.setTimeout(function () {
                            var DD = Aa(C9, C7, C6, DC, C8, C5);
                            if (DD) {
                                DB.dialog("close");
                                Bp()
                            }
                        }, 2)
                    }
                }
            }, {
                text: getLocalizedValue("emailDialog.cancelButton", "Cancel"), click: function () {
                    $(this).dialog("close")
                }
            }]
        });
        $("#vvEmptyEmailFields").dialog({
            modal: true,
            draggable: false,
            resizable: false,
            autoOpen: false,
            width: 375,
            title: getLocalizedValue("emptyEmailFields.dialogTitle", "Missing Information"),
            buttons: [{
                text: getLocalizedValue("emptyEmailFields.okButton", "Ok"), click: function () {
                    $(this).dialog("close")
                }
            }]
        });
        $("#vvEmailAnnotationsSelected").change(function () {
            if ($(this).is(":checked")) {
                $("#vvEmailOriginalSelected").prop("disabled", true);
                $("#vvEmailAnnotationsTypeText").prop("disabled", false);
                $("#vvEmailAnnotationsTypeNonText").prop("disabled", false);
                $("#vvEmailAnnotationsTypeText").prop("checked", true);
                $("#vvEmailAnnotationsTypeNonText").prop("checked", true);
                $("#vvEmailOriginalText").addClass("vvEmailDisabled");
                if ($("#vvEmailOriginalSelected:checked").val()) {
                    $("#vvEmailOriginalSelected").prop("checked", false);
                    $("#vvEmailPDFSelected").prop("checked", true)
                }
            } else {
                $("#vvEmailOriginalSelected").prop("disabled", false);
                $("#vvEmailOriginalText").removeClass("vvEmailDisabled");
                $("#vvEmailAnnotationsTypeText").prop("disabled", true);
                $("#vvEmailAnnotationsTypeNonText").prop("disabled", true);
                $("#vvEmailAnnotationsTypeText").prop("checked", false);
                $("#vvEmailAnnotationsTypeNonText").prop("checked", false)
            }
        });
        $("#vvKeyboardHints").dialog({
            modal: true,
            draggable: false,
            resizable: false,
            autoOpen: false,
            width: 810,
            height: 520,
            dialogClass: "vvKeyboardHintsClass",
            title: getLocalizedValue("hotkeyHints.title", "Keyboard Shortcuts"),
            buttons: [{
                text: getLocalizedValue("aboutDialog.okButton", "OK"), click: function () {
                    $(this).dialog("close")
                }
            }]
        });
        $("#vvKeyboardHintsText").html(As());
        $("#vvNewDocumentDialog").dialog({
            modal: true,
            draggable: false,
            resizable: false,
            autoOpen: false,
            dialogClass: "vvNewDocumentDialogClass",
            width: 400,
            open: function (C5, C6) {
                $("#vvNewDocumentDialogNameInput").val("");
                $("#vvNewDocumentDialogError").hide()
            },
            buttons: [{
                text: getLocalizedValue("newDocumentDialog.okButton", "OK"), click: function () {
                    A1.createNewDocument()
                }
            }, {
                text: getLocalizedValue("newDocumentDialog.cancelButton", "Cancel"), click: function () {
                    $(this).dialog("close")
                }
            }],
            title: getLocalizedValue("newDocumentDialog.title", "Create New Document")
        });
        $("#vvNewDocumentDialogNameInput").keyup(function (C5) {
            if (S(C5)) {
                A1.createNewDocument()
            }
        })
    };
    var M = function (C3, C0, C2, Cz, C4) {
        var C1 = new URI(vvConfig.servletPath);
        C1.addQuery("action", "newExport");
        C1.addQuery("format", "pdf");
        C1.addQuery("textAnnotations", Cz);
        C1.addQuery("nonTextAnnotations", C4);
        C1.addQuery("pageRangeType", C0);
        C1.addQuery("pageRangeValue", C2);
        C1.addQuery("annotations", JSON.stringify(C3.getAnnotationLayers(true)));
        C1.addQuery("modelJSON", JSON.stringify(C3.getDocumentModel().model));
        C1.addQuery("clientInstanceId", virtualViewer.getClientInstanceId());
        C1.addQuery("asAttachment", false);
        C1.addQuery("pageCount", virtualViewer.getPageCount());
        if (A1.getOverlayPath() !== null) {
            C1.addQuery("overlayPath", A1.getOverlayPath())
        }
        return C1
    };
    var T = function () {
        var Cz = document.getElementById("vvPrintFrame");
        if (!Cz || typeof Cz.contentWindow.print === "undefined") {
            pdfPrintTimeoutId = setTimeout(function () {
                T()
            }, 1000)
        } else {
            Cz.contentWindow.print()
        }
    };
    var K = function () {
        var C0 = virtualViewer.serverSidePrinters;
        var Cz = $("<select id='vvNewPrintSelectPrinterInput'/>");
        $.each(C0, function (C2, C1) {
            Cz.append($("<option/>").attr("value", C1).text(C1))
        });
        $("#vvNewPrintServerPrinterSelection").html("");
        $("#vvNewPrintServerPrinterSelection").append(Cz)
    };
    var CS = function () {
        $("#vvThumbsTabs").css("visibility", "visible");
        $("#vvSearch").css("visibility", "visible");
        $(".vvSearchButton").css("font-size", 0);
        $("#vvThumbs").tabs();
        if ((vvConfig.showThumbnailPanel === false) || ((vvConfig.showPageThumbnails === false) && (vvConfig.showDocThumbnails === false) && (vvConfig.showSearch === false))) {
            virtualViewer.toggleThumbnailPanel(false)
        }
        if (vvConfig.showPageThumbnails === false) {
            $("#vvPageThumbsTab").hide();
            $("#vvThumbs").tabs("option", "active", 1)
        }
        if (vvConfig.showDocThumbnails === false) {
            $("#vvDocThumbsTab").hide()
        }
        if (vvConfig.showSearch === false) {
            $("#vvSearchTab").hide()
        }
        $("#vvImagePanel").tabs({
            activate: function (C0, C1) {
                var Cz = $("#vvImagePanel").tabs("option", "active");
                if (!A1.tabs[Cz]) {
                    return
                }
                if (A1.activeTab !== Cz) {
                    A1.switchToTab(A1.getStateForDocumentId(A1.tabs[Cz]))
                }
            }
        });
        $("#vvThumbs").bind("tabsactivate", function (Cz, C1) {
            var C0 = A1.getStateForDocumentId(A1.getDocumentId());
            if (C1.newTab.index() === 0) {
                A1.loadVisibleThumbs(null, C0, false, false);
                Ay()
            } else {
                if (C1.newTab.index() === 1) {
                    A1.loadVisibleThumbs(null, C0, true, false);
                    BA()
                } else {
                    A1.loadVisibleThumbs(null, C0, false, true);
                    Bg()
                }
            }
        })
    };
    var u = function (C0) {
        if (!Modernizr.touch) {
            if (!C0.getIsSearchable()) {
                $("#vvThumbs").tabs("disable", 2);
                var Cz = getLocalizedValue("errors.searchNotSearchable", "Document is not searchable.");
                $("#vvSearchTab").attr("title", Cz).tooltip({show: false, hide: false});
                $("#vvSearchTab").attr("title", Cz).tooltip("enable")
            } else {
                $("#vvThumbs").tabs("enable", 2);
                $("#vvSearchTab").attr("title", undefined).tooltip({show: false, hide: false});
                $("#vvSearchTab").attr("title", undefined).tooltip("disable")
            }
        }
    };
    var AS = function () {
        var C0 = $("#vvAnnColorGrid");
        if (C0.length === 0) {
            C0 = virtualViewer.createColorGrid("vvAnnColorGrid", "000000")
        }
        $(".vvAnnPopUpButton").each(function () {
            var C2 = $(this).val();
            $(this).val($("<div />").html(C2).text())
        });
        $("#vvAnnBorderColorLabel").hide();
        $("#vvAnnFillColorLabel").hide();
        $("#vvAnnTextColorLabel").hide();
        $("#vvAnnBorderSizeLabel").hide();
        $("#vvAnnColorMoreLink").before(C0);
        $(".vvAnnColorGridSecondaryRow").hide();
        $("#vvAnnColorCustom").hide();
        $("#vvAnnColor").addClass("vvAnnColorLess");
        $("#vvAnnColorMoreLink").addClass("vvAnnColorLess");
        for (var Cz = 0; Cz < vvDefines.fontNames.length; Cz += 1) {
            $("#vvAnnTextFaceSelect").append("<option>" + vvDefines.fontNames[Cz] + "</option>")
        }
        for (var C1 = 0; C1 < vvDefines.fontSizes.length; C1 += 1) {
            $("#vvAnnTextSizeSelect").append("<option>" + vvDefines.fontSizes[C1] + "</option>")
        }
        $("#vvAnnColorMoreLink").click(function () {
            $("#vvAnnColor").removeClass("vvAnnColorLess");
            $(".vvAnnColorGridSecondaryRow").show();
            $("#vvAnnColorMoreLink").hide();
            $("#vvAnnColorCustom").show()
        });
        $(".vvAnnThicknessCell").click(function () {
            var C4 = $(this).children(".vvAnnThicknessElement").css("width");
            C4 = parseInt(C4.replace(/px/i, ""), 10);
            $("#vvAnnLineSizeValue").html(C4);
            $("#vvAnnBorderSizeValue").html(C4);
            var C6 = A1.getCurrentState();
            var C5 = virtualViewer.getCurrentAnnObject();
            var C3 = myPainter.getLayerWithAnn(C5);
            var C2 = C6.getUndoObject(C6.getPageNumber());
            C2.referenceAnn = C5;
            C2.unchangedAnn = C5.clone();
            C2.layer = C3;
            C5.setLineWidth(C4);
            C2.modifiedAnn = C5.clone();
            C2.type = "modify";
            if (!(C2.modifiedAnn).isSameAs(C2.unchangedAnn)) {
                C6.pushUndoObject(C6.getPageNumber(), C2)
            }
            C6.resetUndoObject();
            myPainter.markLayerWithAnnDirty(C5);
            virtualViewer.paintCanvases()
        });
        $("#vvAnnTextFaceValue").change(function (C6) {
            var C5 = A1.getCurrentState();
            var C4 = virtualViewer.getCurrentAnnObject();
            var C3 = myPainter.getLayerWithAnn(C4);
            var C2 = C5.getUndoObject(C5.getPageNumber());
            C2.referenceAnn = C4;
            C2.unchangedAnn = C4.clone();
            C2.layer = C3;
            C4.setFontName($(C6.target).val());
            C2.modifiedAnn = C4.clone();
            C2.type = "modify";
            if (!(C2.modifiedAnn).isSameAs(C2.unchangedAnn)) {
                C5.pushUndoObject(C5.getPageNumber(), C2)
            }
            C5.resetUndoObject();
            myPainter.markLayerWithAnnDirty(C4);
            virtualViewer.paintCanvases()
        });
        $("#vvAnnTextSizeValue").change(function (C6) {
            var C5 = A1.getCurrentState();
            var C4 = virtualViewer.getCurrentAnnObject();
            var C3 = myPainter.getLayerWithAnn(C4);
            var C2 = C5.getUndoObject(C5.getPageNumber());
            C2.referenceAnn = C4;
            C2.unchangedAnn = C4.clone();
            C2.layer = C3;
            C4.setFontSize($(C6.target).val());
            C2.modifiedAnn = C4.clone();
            C2.type = "modify";
            if (!(C2.modifiedAnn).isSameAs(C2.unchangedAnn)) {
                C5.pushUndoObject(C5.getPageNumber(), C2)
            }
            C5.resetUndoObject();
            myPainter.markLayerWithAnnDirty(C4);
            virtualViewer.paintCanvases()
        });
        $("#vvAnnTextStyle").change(function (C6) {
            var C5 = virtualViewer.getCurrentAnnObject();
            var C3 = myPainter.getLayerWithAnn(C5);
            var C4 = A1.getCurrentState();
            var C2 = C4.getUndoObject(C4.getPageNumber());
            C2.referenceAnn = C5;
            C2.unchangedAnn = C5.clone();
            C2.layer = C3;
            if ($("#vvAnnTextStyleBold").is(":checked")) {
                C5.fontBold = true
            } else {
                C5.fontBold = false
            }
            if ($("#vvAnnTextStyleItalic").is(":checked")) {
                C5.fontItalic = true
            } else {
                C5.fontItalic = false
            }
            if ($("#vvAnnTextStyleUnderline").is(":checked")) {
                C5.fontUnderline = true
            } else {
                C5.fontUnderline = false
            }
            C2.modifiedAnn = C5.clone();
            C2.type = "modify";
            if (!(C2.modifiedAnn).isSameAs(C2.unchangedAnn)) {
                C4.pushUndoObject(C4.getPageNumber(), C2)
            }
            C4.resetUndoObject();
            myPainter.markLayerWithAnnDirty(C5);
            virtualViewer.paintCanvases()
        });
        $("#vvAnnTextEditButton").click(function (C2) {
            var C3 = A1.getCurrentAnnObject();
            C3.editing = true;
            virtualViewer.paintCanvases()
        });
        $("#vvAnnDeleteButton").click(function (C3) {
            var C4 = A1.getCurrentAnnObject();
            var C2 = myPainter.getLayerWithAnn(C4);
            var C5 = getLocalizedValue("deleteAnnDialog.title", "Delete Annotation?");
            $("#vvDeleteAnnotationDialog").dialog({
                resizable: false,
                modal: true,
                title: C5,
                dialogClass: "vvDeleteAnnotationDialogClass",
                buttons: [{
                    text: getLocalizedValue("deleteAnnDialog.deleteButton", "Delete"), click: function () {
                        $("#vvAnnPopUp").hide();
                        var C8 = A1.getCurrentState();
                        var C7 = C8.getUndoObject(C8.getPageNumber());
                        C7.layer = C2;
                        if (C4) {
                            C7.referenceAnn = C4;
                            C7.unchangedAnn = C4.clone();
                            C4.setDelete(true);
                            myPainter.markLayerWithAnnDirty(virtualViewer.getCurrentAnnObject());
                            var C6 = getTextAnnDOMId(C4);
                            $("#" + C6).remove()
                        }
                        C7.modifiedAnn = C4.clone();
                        C7.type = "modify";
                        if (!(C7.modifiedAnn).isSameAs(C7.unchangedAnn)) {
                            C8.pushUndoObject(C8.getPageNumber(), C7)
                        }
                        C8.resetUndoObject();
                        virtualViewer.paintCanvases();
                        $(this).dialog("close")
                    }
                }, {
                    text: getLocalizedValue("deleteAnnDialog.cancelButton", "Cancel"), click: function () {
                        $(this).dialog("close")
                    }
                }]
            })
        });
        $("#vvAnnColorCustomInput").bind("keyup", function (C4) {
            var C3 = /^([a-fA-F0-9]{6})$/;
            var C2 = $("#vvAnnColorCustomInput").val();
            if (C3.test(C2)) {
                $("#vvAnnColorCustomInput").css("background-color", "white");
                O(null, null, null, C2);
                myPainter.markLayerWithAnnDirty(virtualViewer.getCurrentAnnObject());
                virtualViewer.paintCanvases()
            } else {
                $("#vvAnnColorCustomInput").css("background-color", "#FFBBBB")
            }
        })
    };
    var Az = function () {
        var Cz = A1.getStateForDocumentId(A1.getDocumentId());
        $("#vvSearchProgress").hide();
        $("#vvSearchProgress").progressbar();
        $(".vvSearchButton").button({disabled: true});
        $("#vvSearchStart").click(function (C1) {
            var C0 = $("#vvSearchTermField").val();
            A1.searchText(C0)
        });
        $("#vvSearchTermField").clearableTextField();
        $("#vvSearchTermField").on("textFieldCleared", function () {
            A1.clearSearchResults(Cz)
        });
        $("#vvSearchTermField").change(function (C0) {
            CA()
        });
        $("#vvSearchTermField").bind("keyup", function (C1) {
            var C0 = $("#vvSearchTermField").val();
            CA();
            if (C1.which === 10 || C1.which === 13) {
                A1.searchText(C0)
            }
        });
        $("#vvSearchPrevious").click(function () {
            A1.previousSearchResult()
        });
        $("#vvSearchNext").click(function () {
            A1.nextSearchResult()
        });
        $("#vvSearchProgressCancel").click(function () {
            A1.cancelCurrentSearch()
        });
        $("#vvSearchNextPreviousButtons > input").mousedown(function () {
            $(this).addClass("mouseDown")
        });
        $("#vvSearchNextPreviousButtons > input").mouseup(function () {
            $(this).removeClass("mouseDown")
        })
    };
    var Cj = function () {
        var Cz = A1.getStateForDocumentId(A1.getDocumentId());
        if (Cz.getSVGableForPage(CF)) {
            Cz.setSVGableForPage(CF, false)
        }
    };
    var Cm = function () {
        $("#vvBrightnessSlider").slider({range: "min", max: 125, min: -125, value: 0});
        $("#vvBrightnessSlider").on("slide", function (C1, Cz) {
            if (C1.originalEvent) {
                Cj();
                var C0 = Cz.value;
                if (By) {
                    clearTimeout(By)
                }
                By = setTimeout(function (C2) {
                    A1.setBrightness(C0)
                }, vvConfig.pictureControlsTimeout)
            }
        });
        $("#vvContrastSlider").slider({range: "min", max: 125, min: -125, value: 0});
        $("#vvContrastSlider").on("slide", function (C1, C0) {
            if (C1.originalEvent) {
                Cj();
                var Cz = C0.value;
                if (By) {
                    clearTimeout(By)
                }
                By = setTimeout(function (C2) {
                    A1.setContrast(Cz)
                }, vvConfig.pictureControlsTimeout)
            }
        });
        $("#vvGammaSlider").slider({range: "min", min: 0, max: 400, value: vvDefines.defaultGammaValue});
        $("#vvGammaSlider").on("slide", function (C1, C0) {
            if (C1.originalEvent) {
                Cj();
                var Cz = C0.value;
                if (By) {
                    clearTimeout(By)
                }
                By = setTimeout(function (C2) {
                    A1.setGamma(Cz)
                }, vvConfig.pictureControlsTimeout)
            }
        });
        $("#vvPictureControlsButton").click(function () {
            $("#vvPictureControlsDialog").dialog("open");
            $("div.ui-widget-overlay").addClass("vvPictureControlsOverlay");
            var C1 = A1.getStateForDocumentId(A1.getDocumentId());
            var C0 = C1.getContrastForPage(CF);
            var C2 = C1.getBrightnessForPage(CF);
            var Cz = C1.getGammaForPage(CF);
            $("#vvContrastSlider").slider("value", C0);
            $("#vvBrightnessSlider").slider("value", C2);
            $("#vvGammaSlider").slider("value", Cz);
            $("#vvContrastDisplay").html(C0);
            $("#vvBrightnessDisplay").html(C2);
            $("#vvGammaDisplay").html(Cz);
            BI = C0;
            Ai = C2;
            I = Cz
        })
    };
    var CM = function () {
        $("#vvPageThumbs").contextMenu("vvThumbContextMenu", W)
    };
    var l = function () {
        if (vvConfig.hideRedactionUI) {
            $("#vvLayerManagerRedactLayerButton").hide()
        } else {
            $("#vvLayerManagerRedactLayerButton").show()
        }
    };
    var CA = function () {
        if ($("#vvSearchTermField").val() === "") {
            $("#vvSearchStart").button({disabled: true})
        } else {
            $("#vvSearchStart").button({disabled: false})
        }
    };
    var AU = function () {
        var C1 = A1.getStateForDocumentId(A1.getDocumentId());
        if (!C1) {
            return
        }
        var C2 = C1.getPageNumber();
        var C3 = C1.getFirstSearchResultPage();
        var C0 = C1.getLastSearchResultPage();
        if ((C3 === undefined) || (C3 === null)) {
            $("#vvSearchPrevious").button({disabled: true})
        } else {
            if (C3 > C2) {
                $("#vvSearchPrevious").button({disabled: true});
                if (!Modernizr.touch) {
                    $("#vvSearchPrevious").tooltip("close")
                }
            } else {
                if (C3 === C2) {
                    if (A1.currentMatch === 0) {
                        $("#vvSearchPrevious").button({disabled: true});
                        if (!Modernizr.touch) {
                            $("#vvSearchPrevious").tooltip("close")
                        }
                    } else {
                        $("#vvSearchPrevious").button({disabled: false})
                    }
                } else {
                    $("#vvSearchPrevious").button({disabled: false})
                }
            }
        }
        var Cz = C1.getSearchResultsForPage(C0);
        if ((C0 === undefined) || (C0 === null)) {
            $("#vvSearchNext").button({disabled: true})
        } else {
            if (C0 < C2) {
                $("#vvSearchNext").button({disabled: true});
                if (!Modernizr.touch) {
                    $("#vvSearchNext").tooltip("close")
                }
            } else {
                if (C0 === C2) {
                    if (A1.currentMatch < (Cz.matches.length - 1)) {
                        $("#vvSearchNext").button({disabled: false})
                    } else {
                        $("#vvSearchNext").button({disabled: true});
                        if (!Modernizr.touch) {
                            $("#vvSearchNext").tooltip("close")
                        }
                    }
                } else {
                    $("#vvSearchNext").button({disabled: false})
                }
            }
        }
    };
    VirtualViewer.prototype.createNewDocument = function (Cz) {
        var C0 = $("#vvNewDocumentDialog").dialog("option", "cutDocument");
        if (Cz) {
            if (C0) {
                A1.cutSelectionToNewDocument(Cz)
            } else {
                A1.copySelectionToNewDocument(Cz)
            }
        } else {
            Cz = $("#vvNewDocumentDialogNameInput").val();
            if (Cz === "") {
                $("#vvNewDocumentDialogError").show()
            } else {
                if (C0) {
                    A1.cutSelectionToNewDocument(Cz)
                } else {
                    A1.copySelectionToNewDocument(Cz)
                }
                $("#vvNewDocumentDialog").dialog("close")
            }
        }
    };
    VirtualViewer.prototype.copySelectionToNewDocument = function (Cz) {
        A1.copySelection();
        A1.openInTab(Cz, true)
    };
    VirtualViewer.prototype.cutSelectionToNewDocument = function (Cz) {
        A1.cutSelection();
        A1.openInTab(Cz, true)
    };
    var Aj = function () {
        for (var Cz = 0; Cz < vvConfig.textRubberStamps.length; Cz += 1) {
            $("#vvTextAnnContextMenuList").append('<li id="vvTextAnnContextMenuListItem' + Cz + '">' + vvConfig.textRubberStamps[Cz].textString + "</li>");
            $("#vvTextAnnContextMenuListItem" + Cz).click(function () {
                var C0 = $(this).closest("li").index();
                A1.currentStamp = C0 - 1;
                if (vvConfig.stickyAnnButtons) {
                    if ((CE === false) || (Aw !== "Rubber Stamp")) {
                        $("#vvAnnTextButton").addClass("stickyPress");
                        CE = true;
                        Aw = "Rubber Stamp";
                        virtualViewer.addAnnotation("Rubber Stamp")
                    } else {
                        $("#vvAnnTextButton").removeClass("stickyPress");
                        CE = false;
                        AT(vvDefines.dragModes.pan);
                        $("#vvOuterDiv").css("cursor", "default")
                    }
                } else {
                    virtualViewer.addAnnotation("Rubber Stamp")
                }
                $("#jqContextMenu").hide();
                $("#jqContextMenuShadow").hide()
            })
        }
        if ((vvConfig.enableTextRubberStamp === true) && (vvConfig.textRubberStamps.length > 0)) {
            $("#vvAnnTextButton").contextMenu("vvTextAnnContextMenu", {
                bindings: {
                    vvTextAnnContextMenuAdd: function (C0) {
                        A1.currentStamp = null;
                        if (vvConfig.stickyAnnButtons) {
                            if ((CE === false) || (Aw !== "Rubber Stamp")) {
                                $("#vvAnnTextButton").addClass("stickyPress");
                                CE = true;
                                Aw = "Rubber Stamp";
                                virtualViewer.addAnnotation("Rubber Stamp")
                            } else {
                                $("#vvAnnTextButton").removeClass("stickyPress");
                                CE = false;
                                AT(vvDefines.dragModes.pan);
                                $("#vvOuterDiv").css("cursor", "default")
                            }
                        } else {
                            virtualViewer.addAnnotation("Rubber Stamp")
                        }
                    }
                }, bindTarget: "click", onContextMenu: function (C0) {
                    return true
                }, onShowMenu: function (C0, C1) {
                    return C1
                }
            })
        } else {
            $("#vvAnnTextButton").click(function () {
                if (vvConfig.stickyAnnButtons) {
                    if ((CE === false) || (Aw !== "Rubber Stamp")) {
                        $("#vvAnnTextButton").addClass("stickyPress");
                        CE = true;
                        Aw = "Rubber Stamp";
                        virtualViewer.addAnnotation("Rubber Stamp")
                    } else {
                        $("#vvAnnTextButton").removeClass("stickyPress");
                        CE = false;
                        AT(vvDefines.dragModes.pan);
                        $("#vvOuterDiv").css("cursor", "default")
                    }
                } else {
                    virtualViewer.addAnnotation("Rubber Stamp")
                }
            })
        }
    };
    VirtualViewer.prototype.openInTab = function (C0, C6) {
        if (!C0) {
            return
        }
        var C8 = A1.getDocumentId();
        var C3 = CF;
        var C4 = A1.getStateForDocumentId(C8);
        var Cz = A1.getStateForDocumentId(C0);
        Co();
        this.addToRecentlyViewed(C0);
        if (vvConfig.multipleDocMode === vvDefines.multipleDocModes.viewedDocuments) {
            this.requestDocumentList();
            AO(Cz, true)
        }
        if (C4) {
            C4.setPageNumber(C3)
        }
        if (this.tabs.length >= vvDefines.maxNumberOfTabs) {
            alert(getLocalizedValue("errors.tabTooManyOpen", "Too many open tabs."));
            return
        }
        var C1 = null;
        var DA = false;
        var C7 = false;
        var C5 = false;
        for (var C9 = 0; C9 < this.tabs.length; C9 += 1) {
            if (this.tabs[C9] === C0) {
                DA = true
            }
        }
        if (Cz) {
            C1 = Cz.getTabNumber();
            C7 = Cz.getClosedTab()
        } else {
            C5 = true;
            Cz = new vvDocViewState(C0, null, null, 0, this.tabs.length, vvConfig.defaultZoomMode);
            A1.setStateForDocumentId(C0, Cz)
        }
        if (C5 || (DA === false) || (C7 === true)) {
            C1 = this.tabs.length;
            this.tabs[C1] = C0;
            var C2 = $("<li><a href='resources/blank.html'>" + C0 + "</a><span class='ui-icon ui-icon-close vvTabCloseButton'>Remove Tab</span></li>").appendTo("#vvImagePanel .ui-tabs-nav");
            $("#vvImagePanel").tabs("refresh");
            $(C2).find(".vvTabCloseButton").click(function () {
                var DE = this.parentElement.parentElement;
                var DB = $(DE).children().index(this.parentElement);
                if (vvConfig.unsavedChangesNotification) {
                    var DD = A1.getStateForDocumentId(A1.tabs[DB]);
                    var DC = DD.getDocumentModel();
                    if (DC.getModifiedSinceInit()) {
                        A1.closeTabDialog(DB)
                    } else {
                        A1.closeTab(DB)
                    }
                } else {
                    A1.closeTab(DB)
                }
            });
            h();
            if (Cz) {
                Cz.setTabNumber(C1);
                Cz.setClosedTab(false)
            }
        } else {
            C1 = Cz.getTabNumber()
        }
        this.switchToTab(Cz, C6)
    };
    VirtualViewer.prototype.updateTabName = function (Cz, C0, C3) {
        if (C3) {
            var C1 = C3.getDocumentModel();
            var C2 = $("#vvImageTabs").find($("a"))[Cz];
            if (C1.getModifiedSinceInit()) {
                C0 = "*" + C0
            }
            $("#vvImageTabs").find(C2).html(C0)
        }
    };
    VirtualViewer.prototype.addToRecentlyViewed = function (Cz) {
        this.removeFromRecentlyViewed(Cz);
        if (this.recentlyViewedList.length === vvDefines.mostRecentlyViewedListLength) {
            this.recentlyViewedList.pop()
        }
        this.recentlyViewedList.unshift(Cz);
        BM()
    };
    VirtualViewer.prototype.removeFromRecentlyViewed = function (C1) {
        for (var Cz = 0; Cz < this.recentlyViewedList.length; Cz += 1) {
            var C0 = this.recentlyViewedList[Cz];
            if (C0 === C1) {
                this.recentlyViewedList.splice(Cz, 1);
                break
            }
        }
        BM()
    };
    var BM = function () {
        var C0 = new Date();
        C0.setDate(C0.getDate() + 365);
        var C1 = "";
        for (var Cz = 0; Cz < A1.recentlyViewedList.length; Cz += 1) {
            C1 += encodeURIComponent(A1.recentlyViewedList[Cz]);
            if (Cz !== (A1.recentlyViewedList.length - 1)) {
                C1 += ","
            }
        }
        document.cookie = "recentlyViewedList=" + encodeURIComponent(C1) + ";expires=" + C0.toGMTString()
    };
    var Co = function () {
        var C2 = "recentlyViewedList";
        if (document.cookie.length > 0) {
            var C0 = document.cookie.indexOf(C2 + "=");
            var C1 = -1;
            if (C0 !== -1) {
                C0 += C2.length + 1;
                C1 = document.cookie.indexOf(";", C0);
                if (C1 === -1) {
                    C1 = document.cookie.length
                }
                A1.recentlyViewedList = decodeURIComponent(document.cookie.substring(C0, C1)).split(",");
                for (var Cz = 0; Cz < A1.recentlyViewedList.length; Cz += 1) {
                    A1.recentlyViewedList[Cz] = decodeURIComponent(A1.recentlyViewedList[Cz]);
                    if (!A1.recentlyViewedList[Cz]) {
                        A1.recentlyViewedList.splice(Cz, 1)
                    }
                }
                return true
            } else {
                return false
            }
        } else {
            return false
        }
    };
    VirtualViewer.prototype.switchToTab = function (C3, C2) {
        this.setCurrentAnnObject(null);
        $("#vvAnnPopUp").hide();
        var C1 = A1.getPageNumber();
        var C4 = A1.getDocumentId();
        var C0 = A1.getStateForDocumentId(C4);
        n(false, false);
        A1.cancelCurrentSearch();
        if (($("#vvSearch").attr("display", "block")) && (!C3.getIsSearchable())) {
            $("#vvThumbs").tabs("option", "active", 0)
        }
        u(C3);
        if (C3 && C3.getDocumentModel()) {
            CO(C1)
        }
        if (C0) {
            C0.setPageNumber(C1)
        }
        CF = C3.getPageNumber();
        if (vvConfig.fitLastBetweenDocuments === false) {
            if (vvConfig.defaultZoomMode === vvDefines.zoomModes.fitLast) {
                C3.setZoomModeForPage(CF, vvDefines.zoomModes.fitWindow)
            } else {
                C3.setZoomModeForPage(CF, vvConfig.defaultZoomMode)
            }
        }
        var Cz = this.tabs[C3.getTabNumber()];
        A1.setDocumentId(Cz);
        this.requestDocumentModel(C3, false, C2, C0);
        CO(null);
        this.resetGuides();
        A1.clearCanvases();
        $("#vvTextAnnotationContainer").empty();
        BV();
        CC(true);
        A4();
        CP();
        L();
        BN();
        this.selectTab(C3)
    };
    VirtualViewer.prototype.selectTab = function (C0) {
        var Cz = C0.getTabNumber();
        if ((Cz < 0) || (Cz > this.tabs.length)) {
            alert(getLocalizedValue("errors.tabIndexOutOfBounds", "Tab index out of bounds."));
            return
        }
        $("#vvImagePanel").tabs("option", "active", Cz);
        this.activeTab = Cz
    };
    VirtualViewer.prototype.clearSpecifiedState = function (Cz) {
        A1.openDocuments[A1.tabs[Cz]] = null
    };
    VirtualViewer.prototype.closeTabDialog = function (Cz, C0) {
        $("#vvConformChangesDialogText").html("Would you like to save: " + A1.tabs[Cz]);
        $("#vvConfirmChangesDialog").dialog({
            resizable: false,
            modal: true,
            title: "Confirm Changes",
            dialogClass: "vvConfirmChangesDialogClass",
            autoOpen: false,
            buttons: [{
                text: getLocalizedValue("confirmChangesDialog.yesButton", "Yes"), click: function () {
                    A1.saveSpecificDocument(A1.tabs[Cz], true);
                    A1.closeTab(Cz, C0);
                    $(this).dialog("close")
                }
            }, {
                text: getLocalizedValue("confirmChangesDialog.noButton", "No"), click: function () {
                    A1.clearSpecifiedState(Cz);
                    A1.closeTab(Cz, C0);
                    $(this).dialog("close")
                }
            }, {
                text: getLocalizedValue("confirmChangesDialog.cancelButton", "Cancel"), click: function () {
                    $(this).dialog("close");
                    return
                }
            },]
        });
        $("#vvConfirmChangesDialog").dialog("open")
    };
    VirtualViewer.prototype.closeTab = function (C0, C4) {
        if ((C0 < 0) || (C0 >= this.tabs.length)) {
            alert(getLocalizedValue("errors.tabIndexOutOfBounds", "Tab index out of bounds."));
            return
        }
        if ((this.tabs.length === 1) && (!C4)) {
            alert(getLocalizedValue("errors.tabCloseLastTab", "Closing last tab is not allowed."));
            return
        }
        var C2 = $("#vvImagePanel").find(".ui-tabs-nav li:eq(" + C0 + ")").remove();
        var C1 = C2.attr("aria-controls");
        $("#" + C1).remove();
        $("#vvImagePanel").tabs("refresh");
        h();
        var C3 = A1.getStateForDocumentId(this.tabs[C0]);
        if (C3) {
            C3.setTabNumber(null);
            C3.setClosedTab(true)
        }
        this.tabs.splice(C0, 1);
        for (var Cz = C0; Cz < this.tabs.length; Cz += 1) {
            C3 = A1.getStateForDocumentId(this.tabs[Cz]);
            C3.setTabNumber(Cz)
        }
        if (this.activeTab >= C0) {
            this.activeTab -= 1;
            if (this.activeTab < 0) {
                this.activeTab = 0
            }
            this.switchToTab(A1.getStateForDocumentId(A1.tabs[A1.activeTab]))
        }
    };
    VirtualViewer.prototype.resetGuides = function () {
        this.horizontalGuide.visible = false;
        this.horizontalGuide.locked = false;
        this.verticalGuide.visible = false;
        this.verticalGuide.locked = false
    };
    var h = function () {
        var Cz = $("#vvImagePanel .ui-tabs-nav li").length;
        if (Cz > 1) {
            $(".vvTabCloseButton").show()
        } else {
            $(".vvTabCloseButton").hide()
        }
    };
    VirtualViewer.prototype.parseClientInstanceId = function () {
        var Cz = "clientInstanceId";
        var C0 = document.cookie.indexOf(Cz + "=");
        var C1 = null;
        if (C0 !== -1) {
            C0 = C0 + Cz.length + 1;
            cookieend = document.cookie.indexOf(";", C0);
            if (cookieend === -1) {
                c_end = document.cookie.length
            }
            C1 = decodeURIComponent(document.cookie.substring(C0, cookieend))
        }
        if (!C1) {
            C1 = b("clientInstanceId")
        }
        return C1
    };
    VirtualViewer.prototype.getClientInstanceId = function () {
        return this.clientInstanceId
    };
    VirtualViewer.prototype.setClientInstanceId = function (Cz) {
        this.clientInstanceId = Cz
    };
    VirtualViewer.prototype.requestDocumentModel = function (C4, C3, C0, Cz) {
        var C1 = new URI(vvConfig.servletPath);
        C1.addQuery("action", "getDocumentModel");
        C1.addQuery("documentId", C4.getDocumentId());
        C1.addQuery("clientInstanceId", A1.getClientInstanceId());
        if (vvDefines.cacheBuster === true) {
            C1.addQuery("cacheBuster", Math.random())
        }
        if (C0) {
            C1 = new URI("resources/blank.json")
        }
        var C2 = C1.query();
        C1.query("");
        if (C4.getDocumentModel() && !C3) {
            A1.processDocumentModel(C4, C0, Cz);
            return
        }
        CL = null;
        $.ajax({
            url: C1.toString(), type: "POST", data: C2, async: false, dataType: "json", success: function (C6) {
                if (AB(C6) === true) {
                    virtualViewer.closeTab(virtualViewer.getActiveTab(), true);
                    return
                }
                var C5 = new DocumentModel(C6);
                C4.setDocumentModel(C5);
                A1.processDocumentModel(C4, C0, Cz)
            }, error: function (C5) {
                console.log("error")
            }
        })
    };
    VirtualViewer.prototype.requestDocumentList = function () {
        var C1 = A1.getStateForDocumentId(A1.getDocumentId());
        var Cz = new URI(vvConfig.servletPath);
        Cz.addQuery("action", "getDocumentList");
        Cz.addQuery("clientInstanceId", virtualViewer.getClientInstanceId());
        if (vvDefines.cacheBuster === true) {
            Cz.addQuery("cacheBuster", Math.random())
        }
        var C0 = Cz.query();
        Cz.query("");
        if (vvConfig.multipleDocMode === vvDefines.multipleDocModes.availableDocuments) {
            $.ajax({
                url: Cz.toString(), type: "POST", data: C0, dataType: "json", success: function (C3) {
                    if (AB(C3) === true) {
                        return
                    }
                    if (C3.documentList) {
                        A1.documentList = [];
                        for (var C2 = 0; C2 < C3.documentList.length; C2 += 1) {
                            A1.documentList[C2] = decodeURIComponent(C3.documentList[C2])
                        }
                    } else {
                        A1.documentList = []
                    }
                    AO(null, true);
                    BA();
                    A1.loadVisibleThumbs(null, C1, true)
                }, error: function (C2) {
                    console.log("error")
                }
            })
        } else {
            if (vvConfig.multipleDocMode === vvDefines.multipleDocModes.specifiedDocuments) {
                AO(null, true);
                A1.loadVisibleThumbs(null, C1, true)
            } else {
                if (vvConfig.multipleDocMode === vvDefines.multipleDocModes.viewedDocuments) {
                    Co();
                    this.documentList = this.recentlyViewedList.slice()
                }
            }
        }
    };
    var b = function (Cz) {
        var C0 = "";
        C0 = BT(window.location.href, Cz);
        return C0
    };
    var BT = function (Cz, C1) {
        var C2 = "";
        var C0 = Cz.match(new RegExp(C1, "i"));
        var C3 = Cz.indexOf(C0 + "=");
        if (C3 === -1) {
            return null
        }
        C2 = Cz.substring(C3 + C1.length + 1);
        C3 = C2.indexOf("&");
        if (C3 > -1) {
            C2 = C2.slice(0, C3)
        }
        return C2
    };
    VirtualViewer.prototype.reloadDocumentModel = function (Cz) {
        if (!Cz) {
            Cz = A1.getCurrentState()
        }
        n();
        A1.requestDocumentModel(Cz, true)
    };
    VirtualViewer.prototype.processDocumentModel = function (C2, C0, Cz) {
        var C5 = null;
        if (Cz) {
            C5 = Cz.getDocumentModel()
        }
        var C4 = C2.getDocumentModel();
        var C3 = C2.documentId;
        C4.lastSelectedPageNumber = CF;
        CB = C4.getDocumentLength();
        C2.setPageCount(CB);
        A4();
        AO(C2, false);
        Ay();
        C2.initPageStatesArray(CB, vvConfig.defaultZoomMode);
        if (C0) {
            if (C5) {
                var C1 = deepObjCopy(C5.model.layerManager);
                C4.model.layerManager = C1
            }
            C4.model.displayName = null;
            C4.model.documentId = C3;
            C2.setDisplayName(null);
            A1.pasteSelection(0, C0)
        }
        CF = C2.getPageNumber();
        if (isNaN(CF)) {
            CF = 0
        }
        CO(null);
        if (!C0) {
            j(C2);
            A1.loadVisibleThumbs(null, C2, false);
            A1.loadVisibleThumbs(null, C2, true)
        }
        if (C0) {
            A1.saveDocument(true)
        }
    };
    var A5 = function (C3) {
        virtualViewer.serverSidePrinters = C3.printers;
        K();
        var C2 = C3.customImageRubberStamps;
        q = [];
        if (C2 && C2.length > 0) {
            for (var C0 = 0; C0 < C2.length; C0 += 1) {
                var Cz = C2[C0];
                if (Cz) {
                    q.push(Cz);
                    var C1 = $('<li id="vvImageRubberStampContextMenuListItem-' + C0 + '">' + Cz.stampTitle + "</li>");
                    $("#vvImageRubberStampContextMenuList").append(C1);
                    C1.click(function () {
                        var C4 = $(this).closest("li").index();
                        virtualViewer.currentImageRubberStamp = C4;
                        if (vvConfig.stickyAnnButtons) {
                            $("#vvAnnImageRubberStampButton").addClass("stickyPress");
                            CE = true;
                            Aw = "Bitmap"
                        }
                        virtualViewer.addAnnotation(vvDefines.annotationTypes.SANN_BITMAP);
                        $("#jqContextMenu").hide();
                        $("#jqContextMenuShadow").hide()
                    })
                }
            }
            $("#vvAnnImageRubberStampButton").contextMenu("vvImageRubberStampContextMenu", {
                bindings: {},
                bindTarget: "click",
                onContextMenu: function (C4) {
                    if ((BrowserDetect.browser === "Explorer") && (BrowserDetect.version < 9)) {
                        alert("Image Rubber Stamps are unsupported on IE8 and earlier.");
                        return false
                    }
                    return true
                },
                onShowMenu: function (C4, C5) {
                    return C5
                }
            })
        }
    };
    var r = function (DG, C3) {
        if (!C3) {
            return
        }
        var DD = null;
        var DB = null;
        var C2 = null;
        var C1 = null;
        var DA = null;
        var Cz = null;
        var C7 = null;
        var C5 = null;
        var DE = null;
        var C6 = null;
        var C0 = C3.getDocumentId();
        if (DG) {
            if (AB(DG) === true) {
                return
            }
            DD = DG.imageWidth;
            DB = DG.imageHeight;
            C2 = DG.imageDPI;
            C1 = DG.imageBitDepth;
            DA = DG.compressionType;
            Cz = DG.imageFormat;
            DE = DG.customerMetadataKeys;
            C6 = DG.customerMetadataValues;
            C7 = DG.tiffTagDataValues;
            C5 = DG.tiffTagDataLabels;
            isSearchable = DG.isSearchable;
            C3.setDocumentByteSize(DG.documentByteSize);
            C3.setDisplayName(decodeURIComponent(DG.displayName));
            C3.setIsSearchable(isSearchable);
            u(C3);
            if (C3.getDisplayName()) {
                A1.updateTabName(C3.getTabNumber(), C3.getDisplayName(), C3)
            } else {
                A1.updateTabName(C3.getTabNumber(), C0, C3)
            }
            virtualViewer.setOriginalWidth(DD);
            virtualViewer.setOriginalHeight(DB);
            C3.setDPIForPage(CF, C2);
            C3.setBitDepthForPage(CF, C1);
            C3.setCompressionTypeForPage(CF, DA);
            C3.setSVGableForPage(CF, DG.isSVGable);
            C3.setFormatForPage(CF, Cz);
            var DF = {};
            var C9 = {};
            for (var DC = 0; DC < C5.length; DC++) {
                DF[C5[DC]] = C7[DC]
            }
            var C4 = {};
            if (DE) {
                for (var C8 = 0; C8 < DE.length; C8++) {
                    C4[DE[C8]] = C6[C8]
                }
                C3.setDocumentModelPropertyHash(C4)
            }
            C3.setTiffTagHashForPage(CF, DF);
            C3.setOriginalWidthForPage(CF, Cr);
            C3.setOriginalHeightForPage(CF, B9)
        } else {
            DD = C3.getOriginalWidthForPage(CF);
            DB = C3.getOriginalHeightForPage(CF);
            virtualViewer.setOriginalWidth(DD);
            virtualViewer.setOriginalHeight(DB);
            if (C3.getDisplayName()) {
                A1.updateTabName(C3.getTabNumber(), C3.getDisplayName(), C3)
            } else {
                A1.updateTabName(C3.getTabNumber(), C0, C3)
            }
        }
        L();
        if (C3.getDocumentId() === A1.getDocumentId()) {
            Cd(C3)
        }
        if (vvConfig.showThumbnailPanel === true && vvConfig.showDocThumbnails === false && vvConfig.showPageThumbnails === false && vvConfig.showSearch === true && virtualViewer.isDocumentSearchable()) {
            $("#vvThumbs").tabs("option", "active", 2)
        }
    };
    VirtualViewer.prototype.checkServerException = function (Cz) {
        AB(Cz)
    };
    var AB = function (C0) {
        var Cz = C0.exceptionMessage;
        if (Cz && !A1.currentExceptionDialog) {
            Cz = decodeURIComponent(Cz);
            A1.currentExceptionDialog = $("<div>" + Cz + "</div>").dialog({
                modal: true, buttons: {
                    Ok: function () {
                        $(this).dialog("close")
                    }
                }, close: function (C1, C2) {
                    A1.currentExceptionDialog = null
                }
            });
            CC(false);
            return true
        } else {
            return false
        }
    };
    VirtualViewer.prototype.requestAnnotations = function (Cz) {
        var C5 = Cz.getDocumentModel();
        myPainter.emptyLayerDialog();
        if (!C5) {
            return
        }
        var DB = null;
        if (Cz) {
            DB = Cz.getLayersForPage(CF)
        }
        if (DB) {
            if (Cz.getCurrentLayer()) {
                Cz.setCurrentLayer(DB[Cz.getCurrentLayer().layerName])
            } else {
                myPainter.getCurrentLayer()
            }
            myPainter.fillLayerDialog()
        } else {
            var C3 = C5.model.layerManager.layers.length;
            var C9 = Cz.getLayerNames();
            for (var C4 = 0; C4 < C3; C4++) {
                var C7 = C5.model.layerManager.layers[C4];
                var C6 = unescape(C7.annotationId);
                var C2 = C5.model.pageData[CF].annotationHash;
                if (!C2) {
                    C5.model.pageData[CF].annotationHash = {};
                    C2 = C5.model.pageData[CF].annotationHash
                }
                var C1 = C2[C6];
                var DC = false;
                if (C1) {
                    DC = C1.annExists
                }
                if (DC === true) {
                    C9[C4] = C6;
                    var C0 = new URI(vvConfig.servletPath);
                    C0.addQuery("action", "getAnnotationModel");
                    C0.addQuery("documentId", decodeURIComponent(C1.documentId));
                    C0.addQuery("clientInstanceId", virtualViewer.getClientInstanceId());
                    C0.addQuery("annotationLayer", decodeURIComponent(C1.layerAnnotationId));
                    C0.addQuery("pageCount", Cz.getPageCount());
                    C0.addQuery("pageIndex", C1.pageIndex);
                    if (vvDefines.cacheBuster === true) {
                        C0.addQuery("cacheBuster", Math.random())
                    }
                    var C8 = C0.query();
                    C0.query("");
                    $.ajax({
                        url: C0.toString(), type: "POST", data: C8, success: function (DD) {
                            myPainter.parseLayer(DD, Cz);
                            if ($("#vvImageCanvas")) {
                                virtualViewer.clearCanvases(Cz);
                                virtualViewer.paintCanvases(Cz);
                                myPainter.fillLayerDialog()
                            }
                        }, error: function (DD) {
                            console.log("error")
                        }
                    })
                } else {
                    var DA = myPainter.createAnnotationLayer(C6);
                    DA.isRedaction = C7.isRedaction;
                    DA.permissionLevel = C7.permissionLevel;
                    if (C9.indexOf(C6) < 0) {
                        C9[C4] = C6
                    }
                    Cz.addLayerToPage(Cz.getPageNumber(), DA)
                }
                if (C3 === 0) {
                    myPainter.createDefaultLayer(Cz);
                    Cz.addLayerToPage(Cz.getPageNumber(), myPainter.getCurrentLayer())
                }
            }
        }
    };
    var Bv = function (Cz) {
        if (Modernizr.touch) {
            if (Cz && Cz.originalEvent) {
                Cz = Cz.originalEvent
            }
            var C1 = Cz.changedTouches;
            var C0 = false;
            if (C1 && C1.length > 0) {
                Cz = C1[0];
                C0 = true
            } else {
                if (Cz.gesture && Cz.gesture.touches && Cz.gesture.touches.length > 0) {
                    C1 = Cz.gesture.touches;
                    if (C1 && C1.length > 0) {
                        Cz = C1[0];
                        C0 = true
                    }
                }
            }
            if (C0) {
                Cz.button = 0;
                Cz.preventDefault = function () {
                };
                Cz.stopPropagation = function () {
                }
            }
        }
        return Cz
    };
    var BE = function (Cz) {
        if (vvConfig.imageScrollBars === false) {
            if (virtualViewer.horizontalGuide.visible || virtualViewer.verticalGuide.visible) {
                virtualViewer.enterGuideMode();
                AM(Cz)
            }
        }
    };
    var Z = function (C4) {
        var C5 = virtualViewer.getCurrentState();
        if (!C5) {
            return
        }
        var C0 = document.getElementById("vvImageCanvas");
        var C3 = document.getElementById("vvDummyScroller");
        var C2 = 0;
        $("#vvAnnPopUp").hide();
        if ((BrowserDetect.browser === "Explorer") && (BrowserDetect.version < 9)) {
            C2 = 1
        }
        if (!C4) {
            C4 = window.event
        }
        if (Modernizr.touch) {
            C4 = Bv(C4)
        }
        B2();
        var C6 = Af(C0);
        var Cz = AH(C4.clientX - C6[0], C4.clientY - C6[1]);
        var C1 = myPainter.isAnnotationAtPoint(Cz);
        if (vvConfig.imageScrollBars === false) {
            if (C4.button === C2) {
                A1.dragStartLeft = C4.clientX;
                A1.dragStartTop = C4.clientY;
                if (C1) {
                    cursorNormal()
                } else {
                    if (A1.currentDragMode !== vvDefines.dragModes.guides) {
                        cursorGrabClosed()
                    }
                }
                B1 = stripPx(C0.style.top);
                B5 = stripPx(C0.style.left);
                A1.dragging = true;
                return false
            }
        } else {
            if (C1) {
                C4.preventDefault()
            } else {
                if (virtualViewer.pageHasText()) {
                    k(C4);
                    return
                }
            }
            B1 = stripPx(C0.style.top);
            B5 = stripPx(C0.style.left)
        }
    };
    var k = function (Cz) {
        AT(vvDefines.dragModes.selectText);
        A9(Cz)
    };
    VirtualViewer.prototype.getPagePropertyByFieldId = function (Cz) {
        return z(Cz, A1.getCurrentState())
    };
    VirtualViewer.prototype.getPagePropertyByCaption = function (C0) {
        for (var Cz = 0; Cz < vvConfig.imageInfoFields.length; Cz++) {
            loopFieldCaption = vvConfig.imageInfoFields[Cz].fieldCaption;
            if (loopFieldCaption === C0) {
                fieldId = vvConfig.imageInfoFields[Cz].fieldId;
                return z(fieldId, A1.getCurrentState())
            }
        }
    };
    VirtualViewer.prototype.getPageProperties = function () {
        retVal = [];
        var Cz;
        var C4;
        var C2;
        var C3;
        for (var C1 = 0; C1 < vvConfig.imageInfoFields.length; C1++) {
            Cz = vvConfig.imageInfoFields[C1].fieldId;
            var C0 = vvConfig.imageInfoFields[C1].fieldCaption;
            localizedFieldCaption = getLocalizedValue("imageInfo." + Cz, C0);
            C2 = z(Cz, A1.getCurrentState());
            if (!C2) {
                C2 = "N/A"
            }
            C3 = N(C2);
            retVal[C1] = {};
            retVal[C1].fieldId = Cz;
            retVal[C1].fieldValue = C3;
            retVal[C1].fieldCaption = localizedFieldCaption
        }
        return retVal
    };
    VirtualViewer.prototype.pageHasText = function () {
        var C0 = A1.getCurrentState();
        var Cz = C0.getPageText(this.getPageNumber());
        if (Cz) {
            if (Cz.wordRects.length > 0) {
                return true
            } else {
                return false
            }
        } else {
            return false
        }
    };
    var AY = function (C1) {
        C1.preventDefault();
        if (!C1) {
            C1 = window.event
        }
        if (Modernizr.touch) {
            C1 = Bv(C1)
        }
        var C0 = virtualViewer.getCurrentState();
        if (!C0) {
            return
        }
        A1.dragging = false;
        A1.annDragging = false;
        A1.annCorner = null;
        A1.annLineEnd = null;
        var C7 = document.getElementById("vvImageCanvas");
        var C5 = Af(C7);
        var C6 = AH(C1.clientX - C5[0], C1.clientY - C5[1]);
        var Cz = myPainter.getAnnotationAtPoint(C6);
        if (Cz) {
            C1.preventDefault()
        }
        if (A1.getCurrentAnnObject() !== Cz) {
            $("#vvAnnPopUp").hide()
        }
        A1.setCurrentAnnObject(Cz);
        var C3 = C0.getUndoObject(C0.getPageNumber());
        var C2 = myPainter.getLayerWithAnn(Cz);
        if ((Cz) && (C3.unchangedAnn !== undefined)) {
            C3.modifiedAnn = Cz.clone();
            C3.type = "modify";
            if (!(C3.modifiedAnn).isSameAs(C3.unchangedAnn)) {
                C0.pushUndoObject(C0.getPageNumber(), C3)
            }
            C0.resetUndoObject()
        }
        if (Cz) {
            var C4 = Cc(Cz, C6);
            cursorNormal();
            A1.paintCanvases()
        } else {
            cursorGrabOpen();
            $("#vvAnnPopUp").hide()
        }
    };
    VirtualViewer.prototype.buttonDown = function (Cz) {
        B2();
        value = false;
        if (Cz.indexOf("AddLayer") !== -1) {
            document.getElementById("vvLayerManagerAddLayerButton").style.borderStyle = "inset"
        } else {
            if (Cz.indexOf("DeleteLayer") !== -1) {
                document.getElementById("vvLayerManagerDeleteLayerButton").style.borderStyle = "inset"
            } else {
                if (Cz.indexOf("RenameLayer") !== -1) {
                    document.getElementById("vvLayerManagerRenameLayerButton").style.borderStyle = "inset"
                } else {
                    if (Cz.indexOf("RedactLayer") !== -1) {
                        document.getElementById("vvLayerManagerRedactLayerButton").style.borderStyle = "inset"
                    }
                }
            }
        }
        myPainter.markLayerWithAnnDirty(virtualViewer.getCurrentAnnObject());
        virtualViewer.paintCanvases()
    };
    VirtualViewer.prototype.getIsAnnButtonSticky = function () {
        return CE
    };
    VirtualViewer.prototype.undoAnnotation = function () {
        var C5 = A1.getCurrentState();
        var C2 = C5.getUndoObjectArray(C5.getPageNumber());
        var C3 = C5.getUndoIndex(C5.getPageNumber());
        if (C2[C3]) {
            var C0 = C2[C3];
            var C4 = C0.referenceAnn;
            var Cz = C0.layer;
            if (C0.type === "modify") {
                if ((C0.unchangedAnn) && (C4)) {
                    C4.copyFrom(C0.unchangedAnn);
                    myPainter.markLayerDirty(Cz);
                    virtualViewer.clearCanvases();
                    virtualViewer.paintCanvases()
                }
            } else {
                if (Cz.anns.length > 0) {
                    myPainter.markLayerDirty(Cz);
                    var C1 = Cz.anns.pop();
                    C5.pushPoppedObject(C5.getPageNumber(), C1);
                    virtualViewer.setCurrentAnnObject(null);
                    virtualViewer.clearCanvases();
                    virtualViewer.paintCanvases()
                }
            }
            if (C3 > 0) {
                C3--;
                C5.setUndoIndex(C5.getPageNumber(), C3)
            }
        }
    };
    VirtualViewer.prototype.redoAnnotation = function () {
        var C3 = A1.getCurrentState();
        var C5 = C3.getUndoObjectArray(C3.getPageNumber());
        var C0 = C3.getUndoIndex(C3.getPageNumber());
        if (C5[C0]) {
            var C1 = C5[C0];
            var C2 = C1.referenceAnn;
            var Cz = C1.layer;
            if (C1.type === "modify") {
                if ((C1.modifiedAnn) && (C2)) {
                    C2.copyFrom(C1.modifiedAnn);
                    myPainter.markLayerDirty(Cz);
                    virtualViewer.clearCanvases();
                    virtualViewer.paintCanvases()
                }
            } else {
                if (C3.getPoppedObjectArray(C3.getPageNumber()).length > 0) {
                    var C4 = C3.popPoppedObject(C3.getPageNumber());
                    Cz.anns.push(C4);
                    myPainter.markLayerDirty(Cz);
                    virtualViewer.clearCanvases();
                    virtualViewer.paintCanvases()
                }
            }
            if (C0 < C5.length - 1) {
                C0++;
                C3.setUndoIndex(C3.getPageNumber(), C0)
            }
        }
    };
    VirtualViewer.prototype.buttonUp = function (C4) {
        B2();
        var C2 = virtualViewer.getStateForDocumentId(A1.getDocumentId());
        if (C4.indexOf("AddLayer") !== -1) {
            var C0 = prompt(getLocalizedValue("addLayerDialog.layerName", "Layer Name") + ":", "");
            document.getElementById("vvLayerManagerAddLayerButton").style.borderStyle = "outset";
            if (!C0) {
                if (!Modernizr.touch) {
                    $(".layerManagerButton").tooltip("close")
                }
                return
            }
            while (BR(C0) !== true) {
                C0 = prompt(getLocalizedValue("errors.invalidLayerName", "Invalid layer name. Please choose a new layer name:"), "");
                if (!C0) {
                    break
                }
            }
            if (C0) {
                var Cz = C2.getLayerFromPage(C2.getPageNumber(), C0);
                if (!Cz) {
                    myPainter.createLayer(C0)
                } else {
                    alert(getLocalizedValue("errors.layerNameAlreadyExists", "A layer with that name already exists."))
                }
            }
        } else {
            if (C4.indexOf("DeleteLayer") !== -1) {
                document.getElementById("vvLayerManagerDeleteLayerButton").style.borderStyle = "outset";
                if (myPainter.checkLayerPermission(myPainter.getCurrentLayer(), vvDefines.permissionLevels.PERM_DELETE)) {
                    if (confirm(getLocalizedValue("deleteLayerDialog.value", "Are you sure you wish to delete layer") + ": " + myPainter.getCurrentLayer().layerName)) {
                        myPainter.deleteCurrentLayer();
                        myPainter.fillLayerDialog()
                    }
                } else {
                    alert(getLocalizedValue("errors.deleteLayerPermission", "You do not have permission to delete this layer."))
                }
            } else {
                if (C4.indexOf("RenameLayer") !== -1) {
                    document.getElementById("vvLayerManagerRenameLayerButton").style.borderStyle = "outset";
                    if (myPainter.checkLayerPermission(myPainter.getCurrentLayer(), vvDefines.permissionLevels.PERM_DELETE)) {
                        var C3 = prompt(getLocalizedValue("addLayerDialog.layerName", "Layer Name") + ":", myPainter.getCurrentLayer().layerName);
                        if (!C3) {
                            if (!Modernizr.touch) {
                                $(".layerManagerButton").tooltip("close")
                            }
                            return
                        }
                        while (BR(C3) !== true) {
                            C3 = prompt(getLocalizedValue("errors.invalidLayerName", "Invalid layer name. Please choose a new layer name:"), myPainter.getCurrentLayer().layerName);
                            if (!C3) {
                                break
                            }
                        }
                        if (C3) {
                            var C1 = C2.getLayerFromPage(C2.getPageNumber(), C3);
                            if (!C1) {
                                myPainter.renameCurrentLayer(C3);
                                myPainter.fillLayerDialog();
                                A1.saveDocument(true)
                            } else {
                                alert(getLocalizedValue("errors.layerNameAlreadyExists", "A layer with that name already exists."))
                            }
                        }
                    } else {
                        alert(getLocalizedValue("errors.renameLayerPermission", "You do not have permission to rename this layer."))
                    }
                } else {
                    if (C4.indexOf("RedactLayer") !== -1) {
                        if (myPainter.getCurrentLayer().isRedaction === true) {
                            document.getElementById("vvLayerManagerRedactLayerButton").style.borderStyle = "outset";
                            myPainter.getCurrentLayer().isRedaction = false;
                            myPainter.markCurrentLayerDirty()
                        } else {
                            document.getElementById("vvLayerManagerRedactLayerButton").style.borderStyle = "inset";
                            myPainter.getCurrentLayer().isRedaction = true;
                            myPainter.markCurrentLayerDirty()
                        }
                    }
                }
            }
        }
        if (!Modernizr.touch) {
            $(".layerManagerButton").tooltip("close")
        }
    };
    var Ac = function () {
        if (!CL) {
            CL = 0;
            var C1 = A1.getStateForDocumentId(A1.getDocumentId());
            var C3 = C1.getLayerNames();
            for (var Cz = 0; Cz < C3.length; Cz += 1) {
                var C0 = C3[Cz];
                var C2 = C0.split("-");
                if (C2) {
                    if (C2[0] === vvDefines.autoLayerPrefix) {
                        CL = Math.max(parseInt(C2[1], 10), CL)
                    }
                }
            }
            CL += 1
        }
        myPainter.createLayer(vvDefines.autoLayerPrefix + "-" + CL);
        CL += 1
    };
    var BR = function (Cz) {
        var C0 = true;
        if (Cz === "") {
            C0 = false
        } else {
            if (Cz.length > 50) {
                C0 = false
            }
        }
        return C0
    };
    var CP = function () {
        var Cz = new URI(vvConfig.servletPath);
        Cz.addQuery("action", "eventNotification");
        Cz.addQuery("KEY_EVENT", "VALUE_EVENT_PAGE_REQUESTED");
        Cz.addQuery("KEY_DOCUMENT_ID", A1.getDocumentId());
        Cz.addQuery("clientInstanceId", virtualViewer.getClientInstanceId());
        Cz.addQuery("KEY_EVENT_PAGE_REQUESTED_NUMBER", CF);
        if (vvDefines.cacheBuster === true) {
            Cz.addQuery("cacheBuster", Math.random())
        }
        var C0 = Cz.query();
        Cz.query("");
        $.ajax({
            url: Cz.toString(), type: "POST", data: C0, dataType: "json", success: function (C1) {
                if (AB(C1) === true) {
                    return
                }
            }, error: function (C1) {
                if (AB(C1) === true) {
                    return
                }
            }
        })
    };
    var B6 = function () {
        var C0 = new URI(vvConfig.servletPath);
        C0.addQuery("action", "eventNotification");
        C0.addQuery("KEY_EVENT", "VALUE_EVENT_SAVE_ANNOTATION");
        C0.addQuery("KEY_DOCUMENT_ID", A1.getDocumentId());
        C0.addQuery("clientInstanceId", virtualViewer.getClientInstanceId());
        C0.addQuery("KEY_EVENT_PAGE_REQUESTED_NUMBER", CF);
        if (vvDefines.cacheBuster === true) {
            C0.addQuery("cacheBuster", Math.random())
        }
        var C2 = A1.getCurrentState().getLayerNames();
        for (var Cz = 0; Cz < C2.length; Cz += 1) {
            C0.addQuery("KEY_EVENT_SAVE_ANNOTATION_LAYER_NAME" + Cz, C2[Cz])
        }
        var C1 = C0.query();
        C0.query("");
        $.ajax({
            url: C0.toString(), type: "POST", data: C1, dataType: "json", success: function (C3) {
                if (AB(C3) === true) {
                    return
                }
            }, error: function (C3) {
                if (AB(C3) === true) {
                    return
                }
            }
        })
    };
    var Bt = function (Cz) {
        var C0 = new URI(vvConfig.servletPath);
        C0.addQuery("action", "eventNotification");
        C0.addQuery("KEY_EVENT", "VALUE_EVENT_PRINT");
        C0.addQuery("KEY_DOCUMENT_ID", A1.getDocumentId());
        C0.addQuery("clientInstanceId", virtualViewer.getClientInstanceId());
        C0.addQuery("KEY_EVENT_PRINT_PAGE_NUMBERS", Cz);
        if (vvDefines.cacheBuster === true) {
            C0.addQuery("cacheBuster", Math.random())
        }
        var C1 = C0.query();
        C0.query("");
        $.ajax({
            url: C0.toString(), type: "POST", data: C1, dataType: "json", success: function (C2) {
                if (AB(C2) === true) {
                    return
                }
            }, error: function (C2) {
                if (AB(C2) === true) {
                    return
                }
            }
        })
    };
    var AR = function (C1) {
        var Cz = new URI(vvConfig.servletPath);
        Cz.addQuery("action", "eventNotification");
        Cz.addQuery("KEY_EVENT", "VALUE_EVENT_EXPORT");
        Cz.addQuery("KEY_DOCUMENT_ID", A1.getDocumentId());
        Cz.addQuery("clientInstanceId", virtualViewer.getClientInstanceId());
        Cz.addQuery("KEY_EVENT_EXPORT_FORMAT_NAME", C1);
        if (vvDefines.cacheBuster === true) {
            Cz.addQuery("cacheBuster", Math.random())
        }
        var C0 = Cz.query();
        Cz.query("");
        $.ajax({
            url: Cz.toString(), type: "POST", data: C0, dataType: "json", success: function (C2) {
                if (AB(C2) === true) {
                    return
                }
            }, error: function (C2) {
                if (AB(C2) === true) {
                    return
                }
            }
        })
    };
    var Bp = function () {
        var Cz = new URI(vvConfig.servletPath);
        Cz.addQuery("action", "eventNotification");
        Cz.addQuery("KEY_EVENT", "VALUE_EVENT_EMAIL");
        Cz.addQuery("KEY_DOCUMENT_ID", A1.getDocumentId());
        Cz.addQuery("clientInstanceId", virtualViewer.getClientInstanceId());
        if (vvDefines.cacheBuster === true) {
            Cz.addQuery("cacheBuster", Math.random())
        }
        var C0 = Cz.query();
        Cz.query("");
        $.ajax({
            url: Cz.toString(), type: "POST", data: C0, dataType: "json", success: function (C1) {
                if (AB(C1) === true) {
                    return
                }
            }, error: function (C1) {
                if (AB(C1) === true) {
                    return
                }
            }
        })
    };
    var e = function (C0) {
        if (!C0) {
            C0 = window.event
        }
        if (!virtualViewer.getCurrentAnnObject()) {
            $("#vvAnnPopUp").hide()
        }
        if (Modernizr.touch) {
            C0 = Bv(C0)
        }
        var Cz = virtualViewer.getCurrentState();
        if (!Cz) {
            return
        }
        if (vvConfig.imageScrollBars === false) {
            var C3 = document.getElementById("vvDummyScroller");
            var C9 = document.getElementById("vvOuterDiv");
            var DA = document.getElementById("vvImageCanvas");
            var C4 = $("#vvDummyScroller").height();
            var C5 = $("#vvDummyScroller").width();
            var C6 = Af(DA);
            var C8 = AH(C0.clientX - C6[0], C0.clientY - C6[1]);
            var DD = myPainter.getAnnotationAtPoint(C8);
            if (DD) {
                var C2 = Cz.getUndoObject(Cz.getPageNumber());
                var C1 = myPainter.getLayerWithAnn(DD);
                C2.referenceAnn = DD;
                C2.unchangedAnn = DD.clone();
                C2.layer = C1
            }
            var DC = myPainter.isAnnotationAtPoint(C8);
            if (DC) {
                cursorNormal()
            } else {
                if (A1.dragging) {
                    cursorGrabClosed()
                } else {
                    cursorGrabOpen()
                }
            }
            if (A1.dragging) {
                var DB = B1 + (C0.clientY - A1.dragStartTop);
                var C7 = B5 + (C0.clientX - A1.dragStartLeft);
                A1.dragStartTop = C0.clientY;
                A1.dragStartLeft = C0.clientX;
                if (C5 < C9.clientWidth) {
                    C7 = (C9.clientWidth / 2) - (C5 / 2)
                }
                if (C4 < C9.clientHeight) {
                    DB = (C9.clientHeight / 2) - (C4 / 2)
                }
                if (vvConfig.invertedPanScrollY) {
                    C9.scrollTop = C9.scrollTop - DB
                } else {
                    C9.scrollTop = C9.scrollTop + DB
                }
                if (vvConfig.invertedPanScrollX) {
                    C9.scrollLeft = C9.scrollLeft - C7
                } else {
                    C9.scrollLeft = C9.scrollLeft + C7
                }
            }
        }
    };
    var g = function (C3) {
        C3.preventDefault();
        var C2 = A1.getCurrentState();
        if (C3.which === 3) {
            return
        }
        var DB = document.getElementById("vvImageCanvas");
        if (!C3) {
            C3 = window.event
        }
        if (Modernizr.touch) {
            C3 = Bv(C3)
        }
        B2();
        var C0 = virtualViewer.getCurrentAnnObject();
        if (!C0) {
            A1.setCurrentAnnObject(null);
            AT(vvDefines.dragModes.pan);
            A1.clearCanvases();
            virtualViewer.paintCanvases();
            return false
        }
        var C7 = C0.getBoundingBox(C0.collapsed);
        var C8 = Af(DB);
        var C9 = AH(C3.clientX - C8[0], C3.clientY - C8[1]);
        var C1 = C9.getX();
        var Cz = C9.getY();
        var DA = C0.getWidthRatio();
        var C6 = C0.getHeightRatio();
        if (C7.contains(C9, DA, C6) === false) {
            A1.setCurrentAnnObject(null);
            A1.clearCanvases();
            virtualViewer.paintCanvases(A1.getStateForCurrentDocument());
            AT(vvDefines.dragModes.pan);
            return false
        }
        var C4 = myPainter.getLayerWithAnn(C0);
        var C5 = C2.getUndoObject(C2.getPageNumber());
        C5.referenceAnn = C0;
        C5.unchangedAnn = C0.clone();
        C5.layer = C4;
        Bc = C1 - C0.box.one.getX() * C0.getWidthRatio();
        Bb = Cz - C0.box.one.getY() * C0.getHeightRatio();
        A1.annDragging = true;
        Cc(C0, C9);
        if ((C0.getType() === vvDefines.annotationTypes.SANN_ARROW) || (C0.getType() === vvDefines.annotationTypes.SANN_LINE)) {
            A1.annLineEnd = C0.findLineEndpoint(C9, C0.getWidthRatio(), C0.getHeightRatio())
        } else {
            A1.annCorner = C7.findCorner(C9, DA, C6)
        }
    };
    var Ar = function (C1) {
        C1.preventDefault();
        if (($("#vvAnnPopUp").filter(":hidden").length === 0) && !A1.annDragging) {
            return
        }
        if (!virtualViewer.getCurrentAnnObject()) {
            return
        }
        var C3 = A1.getCurrentState();
        if (!C1) {
            C1 = window.event
        }
        if (Modernizr.touch) {
            C1 = Bv(C1)
        }
        A1.annDragging = false;
        A1.annCorner = null;
        A1.annLineEnd = null;
        if (virtualViewer.getCurrentAnnObject()) {
            virtualViewer.getCurrentAnnObject().fixBoundingBox()
        }
        var C2 = virtualViewer.getCurrentAnnObject();
        var C0 = myPainter.getLayerWithAnn(C2);
        var Cz = C3.getUndoObject(C3.getPageNumber());
        Cz.modifiedAnn = C2.clone();
        Cz.layer = C0;
        Cz.type = "modify";
        if (!(Cz.modifiedAnn).isSameAs(Cz.unchangedAnn)) {
            if (Cz.unchangedAnn) {
                C3.pushUndoObject(C3.getPageNumber(), Cz)
            }
        }
        C3.resetUndoObject();
        myPainter.markLayerWithAnnDirty(virtualViewer.getCurrentAnnObject());
        A1.setCurrentAnnObject(null);
        virtualViewer.clearCanvases();
        virtualViewer.paintCanvases();
        AT(vvDefines.dragModes.pan);
        if (myPainter.checkLayerPermission(myPainter.getCurrentLayer(), vvDefines.permissionLevels.PERM_EDIT) === false) {
            alert(getLocalizedValue("errors.editAnnOnLayerPermission", "You do not have permission to edit annotations on this layer."));
            virtualViewer.undoAnnotation()
        }
    };
    var Cf = function (C1) {
        C1.preventDefault();
        var DB = document.getElementById("vvImageCanvas");
        if (!C1) {
            C1 = window.event
        }
        if (Modernizr.touch) {
            C1 = Bv(C1)
        }
        if (A1.annDragging) {
            var C7 = Af(DB);
            var Cz = virtualViewer.getCurrentAnnObject();
            var DA = AH(C1.clientX - C7[0], C1.clientY - C7[1]);
            var C9 = DA.getX();
            var C8 = DA.getY();
            var C6 = new Point(C9 - Bc, C8 - Bb);
            var C5 = new Point(C9, C8);
            var C0 = virtualViewer.getCurrentState();
            var C3 = $("#vvDummyScroller").width();
            var C2 = $("#vvDummyScroller").height();
            var C4 = C0.getRotationForPage();
            if ((C4 === 90) || (C4 === 270)) {
                C3 = $("#vvDummyScroller").height();
                C2 = $("#vvDummyScroller").width()
            }
            if (C6.getX() < 0) {
                C6 = new Point(0, C6.getY())
            } else {
                if (C6.getX() + Cz.box.getWidth() * Cz.getWidthRatio() > C3) {
                    C6 = new Point(C3 - Cz.box.getWidth() * Cz.getWidthRatio(), C6.getY())
                }
            }
            if (C6.getY() < 0) {
                C6 = new Point(C6.getX(), 0)
            } else {
                if (C6.getY() + Cz.box.getHeight() * Cz.getHeightRatio() > C2) {
                    C6 = new Point(C6.getX(), C2 - Cz.box.getHeight() * Cz.getHeightRatio())
                }
            }
            if ((A1.annCorner !== null) || (A1.annLineEnd !== null)) {
                if ((virtualViewer.getCurrentAnnObject().getType() === vvDefines.annotationTypes.SANN_ARROW) || (virtualViewer.getCurrentAnnObject().getType() === vvDefines.annotationTypes.SANN_LINE)) {
                    virtualViewer.getCurrentAnnObject().stretchLine(A1.annLineEnd, C5.getX(), C5.getY())
                } else {
                    virtualViewer.getCurrentAnnObject().stretch(A1.annCorner, C5.getX(), C5.getY())
                }
            } else {
                virtualViewer.getCurrentAnnObject().move(C6)
            }
            $("#vvAnnPopUp").hide();
            myPainter.markLayerWithAnnDirty(virtualViewer.getCurrentAnnObject());
            A1.clearCanvases();
            virtualViewer.paintCanvases()
        }
    };
    var Cn = function (DD) {
        var C1 = Af(document.getElementById("vvImageCanvas"));
        var DC = AH(DD.clientX - C1[0], DD.clientY - C1[1]);
        var C5 = DC.getX();
        var C4 = DC.getY();
        var C9 = virtualViewer.getCurrentState().getLayerNames();
        var C3 = virtualViewer.getCurrentState().getLayersForPage(A1.getPageNumber());
        if (C9 && C3) {
            for (var C7 = 0; C7 < C9.length; C7 += 1) {
                var DG = C3[C9[C7]];
                var C2 = DG.anns;
                for (var C6 = 0; C6 < C2.length; C6 += 1) {
                    ann = C2[C6];
                    if (ann.getType() === vvDefines.annotationTypes.SANN_POSTIT) {
                        var DE = getTextAnnDOMId(ann);
                        var C8 = ann.getBoundingBox();
                        var C0 = C8.getX1() * ann.getWidthRatio();
                        var DB = C8.getY1() * ann.getWidthRatio();
                        var Cz = C8.getX2() * ann.getWidthRatio();
                        var DA = C8.getY2() * ann.getWidthRatio();
                        if (C0 < C5 && Cz > C5 && DB < C4 && DA > C4) {
                            if (!AP) {
                                var DF = $("#" + DE).attr("title");
                                $("#vvDummyScroller").attr("title", DF);
                                A7 = DE;
                                AP = true;
                                return
                            }
                        } else {
                            if (AP && DE === A7) {
                                $("#vvDummyScroller").removeAttr("title");
                                AP = false
                            }
                        }
                    }
                }
            }
        }
    };
    var AH = function (Cz, C3) {
        var C1 = virtualViewer.getCurrentState();
        var C2 = new Point(Cz, C3);
        if (C1) {
            var C0 = C1.getRotationForPage();
            C2.unrotate(C0, i, Ao);
            return C2
        }
    };
    var Ad = function (C1) {
        C1.preventDefault();
        var C0 = document.getElementById("vvImageCanvas");
        var C3 = A1.getCurrentState();
        B2();
        A1.currentPointArray = [];
        if (!C1) {
            C1 = window.event
        }
        if (Modernizr.touch) {
            C1 = Bv(C1)
        }
        pos = Af(C0);
        var C4 = AH(C1.clientX - pos[0], C1.clientY - pos[1]);
        var Cz = C3.getUndoObject(C3.getPageNumber());
        Cz.referenceAnn = undefined;
        Cz.unchangedAnn = undefined;
        Cz.layer = undefined;
        A1.dragStartLeft = C4.getX();
        A1.dragStartTop = C4.getY();
        if ((c === vvDefines.annotationTypes.SANN_BITMAP) && (vvConfig.enableSingleClickImageRubberStamp)) {
            var C2 = Ah(true);
            C2.setPreview(false);
            A1.currentPreviewAnnObject = null;
            if (vvConfig.oneLayerPerAnnotation === true) {
                Ac()
            }
            Cz.modifiedAnn = undefined;
            Cz.type = "add";
            C3.pushUndoObject(C3.getPageNumber(), Cz);
            myPainter.addAnnotation(C2);
            virtualViewer.clearCanvases();
            virtualViewer.paintCanvases();
            A1.currentPointArray = null;
            if (vvConfig.stickyAnnButtons && CE) {
                $("#vvOuterDiv").css("cursor", "crosshair");
                AT(vvDefines.dragModes.annotate)
            } else {
                $("#vvOuterDiv").css("cursor", "default");
                AT(vvDefines.dragModes.pan)
            }
            return false
        } else {
            A1.currentPointArray[0] = new Point(A1.dragStartLeft, A1.dragStartTop);
            BB = 1;
            B1 = stripPx(C0.style.top);
            B5 = stripPx(C0.style.left);
            A1.dragging = true
        }
        return false
    };
    var BO = function (C1) {
        C1.preventDefault();
        var C0 = document.getElementById("vvImageCanvas");
        var C3 = A1.getCurrentState();
        if (!C1) {
            C1 = window.event
        }
        if (Modernizr.touch) {
            C1 = Bv(C1)
        }
        A1.dragging = false;
        pos = Af(C0);
        var C4 = AH(C1.clientX - pos[0], C1.clientY - pos[1]);
        A1.dragStopLeft = C4.getX();
        A1.dragStopTop = C4.getY();
        Ap();
        var C2 = Ah(true);
        C2.setPreview(false);
        A1.currentPreviewAnnObject = null;
        if (C2.box.getHeight() < 2 || C2.box.getWidth() < 2) {
            return
        }
        if (vvConfig.oneLayerPerAnnotation === true) {
            Ac()
        }
        if (A1.currentPointArray === null) {
            A1.currentPointArray = []
        }
        if (((C2.getType() === vvDefines.annotationTypes.SANN_ARROW) || (C2.getType() === vvDefines.annotationTypes.SANN_LINE)) && (A1.currentPointArray.length < 2)) {
            AT(vvDefines.dragModes.pan);
            A1.currentPointArray = null;
            if (C2.getType() === vvDefines.annotationTypes.SANN_LINE) {
                $("#vvAnnLineButton").removeClass("stickyPress");
                $("#vvAnnFreehandButton").removeClass("stickyPress")
            }
            if (C2.getType() === vvDefines.annotationTypes.SANN_ARROW) {
                $("#vvAnnArrowButton").removeClass("stickyPress")
            }
            $("#vvOuterDiv").css("cursor", "default");
            return false
        }
        if ((c === vvDefines.annotationTypes.SANN_BITMAP) && (vvConfig.enableSingleClickImageRubberStamp)) {
        } else {
            myPainter.addAnnotation(C2);
            virtualViewer.clearCanvases();
            virtualViewer.paintCanvases()
        }
        if (vvConfig.immediatelyEditTextAnnotations) {
            if (!C2.isStamp && (C2.getType() === vvDefines.annotationTypes.SANN_POSTIT || C2.getType() === vvDefines.annotationTypes.SANN_EDIT)) {
                A1.setCurrentAnnObject(C2);
                $("#vvAnnTextEditButton").click()
            } else {
                AT(vvDefines.dragModes.pan)
            }
        } else {
            AT(vvDefines.dragModes.pan)
        }
        if ((c === vvDefines.annotationTypes.SANN_BITMAP) && (vvConfig.enableSingleClickImageRubberStamp)) {
        } else {
            if (((c === vvDefines.annotationTypes.SANN_EDIT) || (c === vvDefines.annotationTypes.SANN_POSTIT)) && (vvConfig.immediatelyEditTextAnnotations)) {
                C3.setNewTextAnn(C3.getPageNumber(), true)
            } else {
                var Cz = C3.getUndoObject(C3.getPageNumber());
                Cz.modifiedAnn = undefined;
                Cz.layer = myPainter.getCurrentLayer();
                Cz.type = "add";
                C3.pushUndoObject(C3.getPageNumber(), Cz)
            }
        }
        A1.currentPointArray = null;
        BB = 0;
        if (vvConfig.stickyAnnButtons && CE) {
            if (V > -1) {
                A1.currentStamp = V
            }
            $("#vvOuterDiv").css("cursor", "crosshair");
            AT(vvDefines.dragModes.annotate)
        } else {
            $("#vvOuterDiv").css("cursor", "default")
        }
        return false
    };
    var AQ = function (C2) {
        C2.preventDefault();
        var C1 = $("#vvImageCanvas")[0];
        var C0 = C1.getContext("2d");
        if (!C2) {
            C2 = window.event
        }
        if (Modernizr.touch) {
            C2 = Bv(C2)
        }
        if (A1.dragging === true) {
            pos = Af(vvImageCanvas);
            var Cz = AH(C2.clientX - pos[0], C2.clientY - pos[1]);
            A1.dragStopLeft = Cz.getX();
            A1.dragStopTop = Cz.getY();
            Ap();
            if (c === vvDefines.annotationTypes.SANN_FREEHAND) {
                A1.currentPointArray[A1.currentPointArray.length] = Cz
            } else {
                A1.currentPointArray[BB] = Cz
            }
            if (A1.currentPointArray.length > 1) {
                A1.currentPreviewAnnObject = Ah()
            } else {
                A1.currentPreviewAnnObject = null
            }
            A1.paintCanvases()
        }
        return false
    };
    var Bk = function (C3) {
        var C2 = $("#vvImageCanvas")[0];
        var C1 = C2.getContext("2d");
        if (!C3) {
            C3 = window.event
        }
        var C0 = document.getElementById("vvImageCanvas");
        if (A1.dragging === true) {
            pos = Af(C0);
            var Cz = AH(C3.clientX - pos[0], C3.clientY - pos[1]);
            A1.dragStopLeft = Cz.getX();
            A1.dragStopTop = Cz.getY();
            Ap();
            dragDistanceTop = Math.abs(A1.dragStopTop - A1.dragStartTop);
            dragDistanceLeft = Math.abs(A1.dragStopLeft - A1.dragStartTop);
            C1.clearRect(0, 0, C2.width, C2.height);
            if (c === vvDefines.annotationTypes.SANN_FREEHAND) {
                A1.currentPointArray[A1.currentPointArray.length] = Cz
            } else {
                A1.currentPointArray[BB] = Cz
            }
            if (A1.currentPointArray.length > 1) {
                A1.currentPreviewAnnObject = Ah()
            } else {
                A1.currentPreviewAnnObject = null
            }
            A1.paintCanvases()
        }
        return false
    };
    var AA = function (DD) {
        var DI = document.getElementById("vvInnerDiv");
        var DG = document.getElementById("vvOuterDiv");
        var C0 = $("#vvImageCanvas")[0];
        var DB = C0.getContext("2d");
        if (!DD) {
            DD = window.event
        }
        A1.dragging = false;
        var C2 = document.getElementById("vvImageCanvas");
        var C3 = Af(C2);
        var DC = AH(DD.clientX - C3[0], DD.clientY - C3[1]);
        A1.dragStopLeft = DC.getX();
        A1.dragStopTop = DC.getY();
        Ap();
        var DE = Ah(true);
        DE.setPreview(false);
        A1.currentPreviewAnnObject = null;
        AT(vvDefines.dragModes.pan);
        A1.currentPointArray = null;
        var C1 = A1.getCurrentState();
        C1.setZoomModeForPage(C1.getPageNumber(), vvDefines.zoomModes.fitCustom);
        if ((i < 1) || (Ao < 1)) {
            return
        }
        var Cz = Math.abs(A1.dragStopTop - A1.dragStartTop);
        var DH = Math.abs(A1.dragStopLeft - A1.dragStartLeft);
        var C7;
        if ((DH === 0) || (Cz === 0)) {
            return
        } else {
            DB.clearRect(0, 0, C0.width, C0.height)
        }
        if (Cz >= DH) {
            C7 = DG.clientHeight / Cz;
            imgScaleRatio = Ao / Cz
        } else {
            C7 = DG.clientWidth / DH;
            imgScaleRatio = Ao / Cz
        }
        var C4 = C1.getZoomForPage(C1.getPageNumber);
        var C8 = Math.round(C4 * C7);
        C1.setZoomForPage(C1.getPageNumber(), C8);
        if (C8 > vvConfig.maxZoomPercent) {
            C8 = vvConfig.maxZoomPercent;
            C7 = C8 / C4;
            C1.setZoomForPage(C1.getPageNumber(), C8)
        }
        Ao = Math.round(Ao * C7);
        i = Math.round(i * C7);
        var C6;
        var DF;
        if (A1.dragStopLeft > A1.dragStartLeft && A1.dragStopTop < A1.dragStartTop) {
            A1.zoomOffsetTopVal = A1.dragStopTop * C7;
            A1.zoomOffsetLeftVal = A1.dragStartLeft * C7;
            C6 = (A1.dragStopLeft - A1.dragStartLeft) * C7;
            DF = (A1.dragStartTop - A1.dragStopTop) * C7
        } else {
            if (A1.dragStopLeft < A1.dragStartLeft && A1.dragStopTop > A1.dragStartTop) {
                A1.zoomOffsetTopVal = A1.dragStartTop * C7;
                A1.zoomOffsetLeftVal = A1.dragStopLeft * C7;
                C6 = (A1.dragStartLeft - A1.dragStopLeft) * C7;
                DF = (A1.dragStopTop - A1.dragStartTop) * C7
            } else {
                if (A1.dragStopLeft > A1.dragStartLeft && A1.dragStopTop > A1.dragStartTop) {
                    A1.zoomOffsetTopVal = A1.dragStartTop * C7;
                    A1.zoomOffsetLeftVal = A1.dragStartLeft * C7;
                    C6 = (A1.dragStopLeft - A1.dragStartLeft) * C7;
                    DF = (A1.dragStopTop - A1.dragStartTop) * C7
                } else {
                    A1.zoomOffsetTopVal = A1.dragStopTop * C7;
                    A1.zoomOffsetLeftVal = A1.dragStopLeft * C7;
                    C6 = (A1.dragStartLeft - A1.dragStopLeft) * C7;
                    DF = (A1.dragStartTop - A1.dragStopTop) * C7
                }
            }
        }
        if ((i - A1.zoomOffsetLeftVal) < DG.clientWidth) {
            delta = DG.clientWidth - (i - A1.zoomOffsetLeftVal);
            A1.zoomOffsetLeftVal = A1.zoomOffsetLeftVal - delta;
            if (A1.zoomOffsetLeftVal < 0) {
                A1.zoomOffsetLeftVal = A1.zoomOffsetLeftVal / 2
            }
        }
        if ((Ao - A1.zoomOffsetTopVal) < DG.clientHeight) {
            delta = DG.clientHeight - (Ao - A1.zoomOffsetTopVal);
            A1.zoomOffsetTopVal = A1.zoomOffsetTopVal - delta;
            if (A1.zoomOffsetTopVal < 0) {
                A1.zoomOffsetTopVal = A1.zoomOffsetTopVal / 2
            }
        }
        A1.zoomToOffset = true;
        DG.leftScroll = A1.zoomOffsetLeftVal;
        DG.topScroll = A1.zoomOffsetTopVal;
        BN();
        var DA = A1.zoomOffsetLeftVal;
        var C9 = A1.zoomOffsetTopVal;
        var C5 = C1.getRotationForPage(C1.getPageNumber());
        if (C5 === 90) {
            C9 = A1.zoomOffsetLeftVal;
            DA = (Ao - A1.zoomOffsetTopVal) - (DF)
        } else {
            if (C5 === 180) {
                DA = (i - DA) - (C6);
                C9 = (Ao - C9) - (DF)
            } else {
                if (C5 === 270) {
                    C9 = (i - A1.zoomOffsetLeftVal) - (C6);
                    DA = A1.zoomOffsetTopVal
                }
            }
        }
        $("#vvOuterDiv").scrollLeft(DA);
        $("#vvOuterDiv").scrollTop(C9);
        return false
    };
    var a = function (C2) {
        if (!C2) {
            C2 = window.event
        }
        if (A1.currentPointArray === null) {
            A1.currentPointArray = []
        }
        B2();
        if (Bf(C2.clientX, C2.clientY, "#vvOuterDiv") === true) {
            var C0 = document.getElementById("vvImageCanvas");
            pos = Af(C0);
            var Cz = AH(C2.clientX - pos[0], C2.clientY - pos[1]);
            A1.dragStartLeft = Cz.getX();
            A1.dragStartTop = Cz.getY();
            A1.currentPointArray[BB] = Cz;
            var C1 = virtualViewer.getNubSize();
            if (BB > 0) {
                if ((A1.dragStartLeft > (A1.currentPointArray[0].getX() - (C1 / 2))) && (A1.dragStartLeft < (A1.currentPointArray[0].getX() + (C1 / 2))) && (A1.dragStartTop > (A1.currentPointArray[0].getY() - (C1 / 2))) && (A1.dragStartTop < (A1.currentPointArray[0].getY() + (C1 / 2)))) {
                    BO(C2);
                    return false
                }
            }
            BB += 1;
            B1 = stripPx(C0.style.top);
            B5 = stripPx(C0.style.left);
            A1.dragging = true
        }
        return false
    };
    var BD = function (Cz) {
        if (!Cz) {
            Cz = window.event
        }
        if (A1.dragging === true) {
            if (Bf(Cz.clientX, Cz.clientY, "#vvOuterDiv") === true) {
                AQ(Cz)
            }
        }
        return false
    };
    var Ah = function (C3) {
        var C1 = A1.getStateForDocumentId(A1.getDocumentId());
        var DC = vvConfig.annotationDefaults.lineColor;
        var C2 = null;
        if ((c === vvDefines.annotationTypes.SANN_BITMAP) && (vvConfig.enableSingleClickImageRubberStamp)) {
            var DB = q[A1.currentImageRubberStamp];
            zoom = C1.getZoomForPage(C1.getPageNumber());
            width = DB.stampWidth * (zoom / 100);
            height = DB.stampHeight * (zoom / 100)
        } else {
            width = A1.dragStopLeft - A1.dragStartLeft;
            height = A1.dragStopTop - A1.dragStartTop
        }
        var C7 = new BoundingBox(A1.dragStartLeft, A1.dragStartTop, width, height);
        var DA = c;
        if ((C3 !== true) && ((c === vvDefines.annotationTypes.SANN_EDIT) || (c === vvDefines.annotationTypes.SANN_BUBBLE) || (c === vvDefines.annotationTypes.SANN_CLOUD_EDIT) || (c === vvDefines.annotationTypes.SANN_BITMAP) || (c === vvDefines.annotationTypes.SANN_POSTIT))) {
            DA = vvDefines.annotationTypes.SANN_RECTANGLE
        }
        if ((c === vvDefines.annotationTypes.SANN_FILLED_RECT) || (c === vvDefines.annotationTypes.SANN_HIGHLIGHT_RECT) || (c === vvDefines.annotationTypes.SANN_FILLED_ELLIPSE) || (c === vvDefines.annotationTypes.SANN_FILLED_POLYGON) || (c === vvDefines.annotationTypes.SANN_CIRCLE)) {
            DC = null;
            C2 = vvConfig.annotationDefaults.fillColor
        }
        var C9 = vvConfig.annotationDefaults.lineWidth;
        if (!C3 && ((c === vvDefines.annotationTypes.SANN_POSTIT) || (c === vvDefines.annotationTypes.SANN_EDIT))) {
            C9 = 1
        }
        var C6 = myPainter.getCurrentLayer();
        var C4 = C1.getDPIForPage(C1.getPageNumber());
        if (C6 === null) {
            C6 = myPainter.createAnnotationLayer("temp")
        }
        if (C6.isIDM) {
            C4 = 200
        }
        var C0 = new PreviewAnnotation(DA, C7, DC, C2, C9, vvConfig.annotationDefaults.highlightOpacity, A1.currentPointArray, C6.doubleByte, C4, C6.isIDM);
        if (C3 === true) {
            var C8 = C0.getFontSize();
            var C5 = C0.getLineWidth();
            C0.setPageWidth(C6.pageWidth);
            C0.setPageHeight(C6.pageHeight);
            C0.setFontSize(C8);
            C0.setLineWidth(C5)
        }
        if (C6.isFilenet) {
            C0.isFilenet = true
        }
        if (C6.isDaeja) {
            C0.isDaeja = true
        }
        if (C6.isIDM) {
            C0.isIDM = true;
            if (C0.getType() === vvDefines.annotationTypes.SANN_POSTIT) {
                C0.setFontSize(200, true)
            } else {
                if (C0.getType() === vvDefines.annotationTypes.SANN_EDIT) {
                }
            }
        }
        if (C0.getType() === vvDefines.annotationTypes.SANN_BITMAP) {
            C0.bitmapData = q[A1.currentImageRubberStamp].stampData
        }
        if ((A1.currentStamp !== undefined) && (A1.currentStamp !== null) && (A1.currentStamp > -1)) {
            var Cz = vvConfig.textRubberStamps[A1.currentStamp];
            if (C3) {
                V = A1.currentStamp;
                A1.currentStamp = null;
                C0.isStamp = true
            }
            if (Cz.textString) {
                C0.setTextString(Cz.textString)
            }
            if (Cz.fontFace) {
                C0.setFontName(Cz.fontFace)
            }
            if (Cz.fontSize) {
                C0.setFontSize(Cz.fontSize)
            }
            if (Cz.fontBold) {
                C0.fontBold = Cz.fontBold
            }
            if (Cz.fontItalic) {
                C0.fontItalic = Cz.fontItalic
            }
            if (Cz.fontStrike) {
                C0.fontStrike = Cz.fontStrike
            }
            if (Cz.fontUnderline) {
                C0.fontUnderline = Cz.fontUnderline
            }
            if (Cz.fontColor) {
                C0.fontColor = Cz.fontColor
            }
        }
        return C0
    };
    var Bf = function (C0, C3, Cz) {
        var C1 = $(Cz);
        var C2 = Af(C1[0]);
        if ((C0 >= C2[0]) && (C3 >= C2[1])) {
            if ((C0 < (C2[0] + C1.width())) && (C3 < (C2[1] + C1.height()))) {
                return true
            }
        } else {
            return false
        }
    };
    var Ap = function () {
        var Cz = $("#vvDummyScroller");
        if (A1.dragStopLeft > Cz.width()) {
            A1.dragStopLeft = Cz.width()
        } else {
            if (A1.dragStopLeft < 0) {
                A1.dragStopLeft = 0
            }
        }
        if (A1.dragStopTop > Cz.height()) {
            A1.dragStopTop = Cz.height()
        } else {
            if (A1.dragStopTop < 0) {
                A1.dragStopTop = 0
            }
        }
    };
    VirtualViewer.prototype.getDragMode = function () {
        return A1.currentDragMode
    };
    VirtualViewer.prototype.setDragMode = function (Cz) {
        AT(Cz)
    };
    var AT = function (C3) {
        var C1 = document.getElementById("vvOuterDiv");
        var Cz = document.getElementById("vvInnerDiv");
        var C2 = document.getElementById("vvDummyScroller");
        if (C3 === A1.currentDragMode) {
            return
        }
        A1.currentDragMode = C3;
        if (C3 !== vvDefines.dragModes.pan) {
            A1.dragging = false
        }
        $(C1).off("mousedown");
        $(C1).off("mouseup");
        $(C1).off("mousemove");
        $(C1).off("mouseout");
        $(C1).off("click");
        $(C1).off("touchstart");
        $(C1).off("touchmove");
        $(C1).off("touchend");
        $(C1).off("dblclick");
        $(C2).off("mousemove");
        $(Cz).off("mouseup");
        if (C3 === vvDefines.dragModes.pan) {
            if (Modernizr.touch) {
                $(C1).on("touchstart", Z);
                $(C1).on("touchmove", e);
                $(C1).on("touchend", AY)
            }
            $(C1).on("mousedown", Z);
            $(C1).on("mousemove", e);
            $(C1).on("mouseup", AY);
            $(C1).on("mouseout", AY);
            $(C1).on("dblclick", BE);
            $(C2).on("mousemove", Cn)
        } else {
            if (C3 === vvDefines.dragModes.annotate) {
                var C0 = c.indexOf("Polygon");
                if (C0 === -1) {
                    if (Modernizr.touch) {
                        $(C1).on("touchstart", Ad);
                        $(C1).on("touchmove", AQ);
                        $(C1).on("touchend", BO)
                    }
                    $(C1).on("mousedown", Ad);
                    $(C1).on("mousemove", AQ);
                    $(C1).on("mouseup", BO)
                } else {
                    $(C1).on("click", a);
                    $(C1).on("mousemove", BD)
                }
            } else {
                if (C3 === vvDefines.dragModes.moveAnnotation) {
                    if (Modernizr.touch) {
                        $(C1).on("touchstart", g);
                        $(C1).on("touchmove", Cf);
                        $(C1).on("touchend", Ar)
                    }
                    $(C1).on("mousedown", g);
                    $(C1).on("mousemove", Cf);
                    $(C1).on("mouseup", Ar)
                } else {
                    if (C3 === vvDefines.dragModes.zoom) {
                        $(C1).on("mousedown", Ad);
                        $(C1).on("mousemove", Bk);
                        $(C1).on("mouseup", AA)
                    } else {
                        if (C3 === vvDefines.dragModes.textAnnotationEdit) {
                            $(C1).on("click", function (C4) {
                                $(".vvTextAnnEditTextArea").focus()
                            })
                        } else {
                            if (C3 === vvDefines.dragModes.selectText) {
                                cursorSelectText();
                                $(C1).on("mousedown", A9);
                                $(C1).on("mousemove", BS);
                                $(C1).on("mouseup", Cq)
                            } else {
                                if (C3 === vvDefines.dragModes.guides) {
                                    cursorGuides();
                                    $(C1).on("mousedown", BZ);
                                    $(C1).on("mousemove", Bn);
                                    $(C1).on("dblclick", AM);
                                    $(C1).on("mouseup", BG);
                                    $(C2).on("mousemove", Cn)
                                }
                            }
                        }
                    }
                }
            }
        }
    };
    var Af = function (C0) {
        var C2 = 0;
        var Cz = 0;
        if (C0.offsetParent) {
            do {
                C2 += C0.offsetLeft;
                Cz += C0.offsetTop
            } while ((C0 = C0.offsetParent))
        }
        var C1 = document.getElementById("vvOuterDiv");
        Cz = Cz - C1.scrollTop;
        C2 = C2 - C1.scrollLeft;
        return [C2, Cz]
    };
    var Bw = function (C2, C1, Cz) {
        var C4 = document.getElementById("vvOuterDiv");
        var C0 = new URI(vvConfig.servletPath);
        C0.addQuery("action", C1);
        var C3 = CH(CF, C2);
        if (Cz === true) {
            C0.addQuery("documentId", encodeURIComponent(C3));
            C0.addQuery("clientInstanceId", encodeURIComponent(virtualViewer.getClientInstanceId()))
        } else {
            C0.addQuery("documentId", C3);
            C0.addQuery("clientInstanceId", virtualViewer.getClientInstanceId())
        }
        C0.addQuery("pageCount", virtualViewer.getPageCount());
        C0.addQuery("zoomPercent", Math.round(C2.getZoomForPage(CF)));
        C0.addQuery("pageNumber", CY(CF));
        C0.addQuery("flipHorizontal", H);
        C0.addQuery("flipVertical", t);
        C0.addQuery("invertImage", p);
        C0.addQuery("brightness", C2.documentModel.model.pageData[CF].brightness);
        C0.addQuery("contrast", C2.documentModel.model.pageData[CF].contrast);
        C0.addQuery("gamma", C2.documentModel.model.pageData[CF].gamma);
        C0.addQuery("despeckle", C2.getDespeckleForPage(CF));
        C0.addQuery("antiAliasOff", C2.getAntiAliasOffForPage(CF));
        C0.addQuery("requestedHeight", Math.round(Ao));
        C0.addQuery("requestedWidth", Math.round(i));
        C0.addQuery("clientWidth", C4.clientWidth);
        C0.addQuery("clientHeight", C4.clientHeight);
        C0.addQuery("pageCount", CB);
        if (A1.getOverlayPath() !== null) {
            C0.addQuery("overlayPath", A1.getOverlayPath())
        }
        if (vvDefines.cacheBuster === true) {
            C0.addQuery("cacheBuster", Math.random())
        }
        return C0.toString()
    };
    var j = function (C1) {
        if (!C1 || !C1.getDocumentId()) {
            return null
        }
        var C2 = CH(CF, C1);
        if ((C1.getOriginalWidthForPage(CF) === 0) || !C1.getDisplayName()) {
            var Cz = new URI(vvConfig.servletPath);
            Cz.addQuery("action", "getImageInfo");
            Cz.addQuery("documentId", C2);
            Cz.addQuery("clientInstanceId", virtualViewer.getClientInstanceId());
            Cz.addQuery("pageNumber", CY(CF));
            Cz.addQuery("pageCount", virtualViewer.getPageCount());
            Cz.addQuery("imageInfoFields", JSON.stringify(vvConfig.imageInfoFields));
            if (vvDefines.cacheBuster === true) {
                Cz.addQuery("cacheBuster", Math.random())
            }
            var C0 = Cz.query();
            Cz.query("");
            $.ajax({
                url: Cz.toString(), type: "POST", data: C0, dataType: "json", success: function (C3) {
                    r(C3, C1)
                }, error: function (C3) {
                    console.log("error")
                }
            })
        } else {
            r(null, C1)
        }
    };
    var Al = function (Cz) {
        if (Cz) {
            $(Cz).off("load");
            $(Cz).off("error");
            Cz.src = vvDefines.bogusImageURL
        }
    };
    var Ce = function (Cz) {
        if (Cz && Cz.src && (Cz.src.indexOf(vvDefines.bogusImageURL) === -1)) {
            return true
        } else {
            return false
        }
    };
    VirtualViewer.prototype.toggleSVGSupport = function (C1) {
        var C0 = A1.getCurrentState();
        vvConfig.enableSVGSupport = !vvConfig.enableSVGSupport;
        var Cz = C0.getImageObject();
        Al(Cz);
        Cd(C0)
    };
    var Cd = function (C1, C7) {
        var C0 = "getImage";
        var C4 = false;
        if (vvConfig.enableSVGSupport && C1.getSVGableForPage() && Modernizr.svg) {
            C4 = true;
            C0 = "getSVG";
            var Cz = C1.getImageObject();
            if (Ce(Cz)) {
                L();
                virtualViewer.paintCanvases(C1);
                CC(false);
                return
            }
        }
        var C3 = Bw(C1, C0, false);
        var C6 = null;
        if ((BrowserDetect.browser === "Explorer") && (BrowserDetect.version < 10)) {
            C6 = Bw(C1, C0, true)
        }
        var C2 = C1.getImageObject();
        if (!C2) {
            C2 = new Image();
            C1.setImageObject(C2)
        }
        C2.crossOrigin = "Anonymous";
        $(C2).on("load", function () {
            if (virtualViewer.getImageLoadCompletedHandler()) {
                var C8 = virtualViewer.getImageLoadCompletedHandler();
                C8(true)
            }
            C1.setViewedForPage(C1.getPageNumber(), true);
            Cv();
            if ((BrowserDetect.browser === "Explorer") && ((BrowserDetect.version === 9) || (BrowserDetect.version === 10))) {
                J = setTimeout(function (C9) {
                    A1.paintCanvases(C1)
                }, vvDefines.ie9DrawDelay)
            }
            virtualViewer.adjustGuideRotation();
            virtualViewer.getAllText();
            virtualViewer.deselectAllText();
            virtualViewer.updateImageInfoDialog();
            CC(false)
        });
        $(C2).on("error", function (C9) {
            CC(false);
            if (C4) {
                console.log("Error requesting SVG from server, falling back to bitmap display.")
            } else {
                console.log("Error requesting image from server, retrying.")
            }
            if (!C7) {
                C1.setSVGableForPage(null, false);
                Cd(C1, true)
            } else {
                var C8 = getLocalizedValue("errors.imageFailedToLoad", "Image failed to load.");
                A1.currentExceptionDialog = $("<div>" + C8 + "</div>").dialog({
                    modal: true, buttons: {
                        Ok: function () {
                            $(this).dialog("close")
                        }
                    }, close: function (DA, DB) {
                        A1.currentExceptionDialog = null
                    }
                })
            }
        });
        if ((BrowserDetect.browser === "Safari") && C4) {
            $("#vvSafariSVGWorkaround").empty();
            var C5 = document.createElement("embed");
            C5.src = C3;
            C5.type = "image/svg+xml";
            C5.style.width = 0;
            C5.style.height = 0;
            C5.onload = function () {
                C2.doubleSrc = C6;
                C2.src = C3
            };
            $("#vvSafariSVGWorkaround").append(C5)
        } else {
            C2.doubleSrc = C6;
            C2.src = C3
        }
    };
    var Br = function () {
        var C3 = A1.getCurrentState();
        if (C3) {
            var C0 = C3.getRotationForPage(C3.getPageNumber());
            var C4 = i;
            var C1 = Ao;
            var Cz = $("#vvOuterDiv");
            var C5 = 0;
            var C2 = 0;
            if (C0 === 90 || C0 === 270) {
                C4 = Ao;
                C1 = i
            }
            if (Cz.width() - Bl > C4) {
                C5 = (Cz.width() - C4) / 2;
                $("#vvImageCanvas")[0].width = C4;
                $("#vvImageCanvas")[0].height = C1
            } else {
                $("#vvImageCanvas")[0].width = $("#vvOuterDiv").width();
                $("#vvImageCanvas")[0].height = $("#vvOuterDiv").height()
            }
            if (Cz.height() - Bl > C1) {
                C2 = (Cz.height() - C1) / 2
            }
            $("#vvDummyScroller").css("top", C2);
            $("#vvDummyScroller").css("left", C5);
            $("#vvImageCanvas").css("top", C2);
            $("#vvImageCanvas").css("left", C5);
            $("#vvTextAnnotationContainer").css("top", C2);
            $("#vvTextAnnotationContainer").css("left", C5);
            $("#vvTextAnnotationContainer").width($("#vvOuterDiv").width());
            $("#vvTextAnnotationContainer").height($("#vvOuterDiv").height())
        }
    };
    VirtualViewer.prototype.clearCanvases = function () {
        var C0 = $("#vvImageCanvas")[0];
        var Cz = C0.getContext("2d");
        C0.width = C0.width;
        C0.height = C0.height;
        $("#vvTextAnnotationContainer").children().remove();
        $(".vvAnnSelected").removeClass("vvAnnSelected")
    };
    VirtualViewer.prototype.paintCanvases = function (C0) {
        if (!C0) {
            C0 = A1.getCurrentState()
        }
        var C4 = C0.getRotationForPage(C0.getPageNumber());
        Br();
        var Cz = Ao;
        var C3 = i;
        if ((C4 === 90) || (C4 === 270)) {
            Cz = i;
            C3 = Ao
        }
        $("#vvDummyScroller").css("width", C3 - (Bl / 2));
        $("#vvDummyScroller").css("height", Cz - (Bl / 2));
        $("#vvDummyScroller").css("min-width", C3 - (Bl / 2));
        $("#vvDummyScroller").css("min-height", Cz - (Bl / 2));
        $("#vvInnerDiv").css("left", 42);
        $("#vvInnerDiv").css("top", 73);
        var C1 = $("#vvImageCanvas")[0];
        var C8 = C1.getContext("2d");
        var C9 = $("#vvOuterDiv").width();
        var C7 = $("#vvOuterDiv").height();
        if (vvConfig.imageScrollBars) {
            if (C3 + Bl >= C9) {
                C1.width = C9;
                $("#vvTextAnnotationContainer").width($("#vvTextAnnotationContainer").width() - Bl);
                if (C3 + Bl === C9) {
                } else {
                    C1.width -= Bl
                }
            }
            if (Cz + Bl >= C7) {
                C1.height = C7;
                $("#vvTextAnnotationContainer").height($("#vvTextAnnotationContainer").height() - Bl);
                if (Cz + Bl === C7) {
                } else {
                    C1.height -= Bl
                }
            }
        }
        virtualViewer.requestAnnotations(C0);
        var C2 = C0.getImageObject();
        var C6 = $("#vvOuterDiv").scrollLeft();
        var C5 = $("#vvOuterDiv").scrollTop();
        if (C4 === 0) {
            C6 *= -1;
            C5 *= -1
        } else {
            if (C4 === 90) {
                C6 = Ao - C6;
                C5 *= -1
            } else {
                if (C4 === 180) {
                    C6 = i - C6;
                    C5 = Ao - C5
                } else {
                    if (C4 === 270) {
                        C6 *= -1;
                        C5 = i - C5
                    }
                }
            }
        }
        C8.setTransform(1, 0, 0, 1, C6, C5);
        C8.rotate(degreesToRadians(C4));
        Ag(C2, C0, C1, C8);
        Bx(C1, C8);
        Bd(C1, C8);
        CZ(C1, C8);
        myPainter.paintAnnotations(C1, C8);
        if (A1.currentPreviewAnnObject) {
            myPainter.paintAnnotation(C1, A1.currentPreviewAnnObject)
        }
        AF(A1.getCurrentAnnObject())
    };
    var Ag = function (C0, C4, C1, Cz) {
        Cz.save();
        try {
            if (C4.getSVGableForPage()) {
                if (C4.getHorizontalFlipForPage()) {
                    Cz.translate(i, 0);
                    Cz.scale(-1, 1)
                }
                if (C4.getVerticalFlipForPage()) {
                    Cz.translate(0, Ao);
                    Cz.scale(1, -1)
                }
            }
            Cz.drawImage(C0, 0, 0, i, Ao);
            if (C4.getSVGableForPage() && C4.getInvertImageForPage()) {
                var C6 = Cz.getImageData(0, 0, i, Ao);
                var C3 = C6.data;
                for (var C2 = 0; C2 < C3.length; C2 += 4) {
                    C3[C2] = 255 - C3[C2];
                    C3[C2 + 1] = 255 - C3[C2 + 1];
                    C3[C2 + 2] = 255 - C3[C2 + 2]
                }
                Cz.putImageData(C6, 0, 0)
            }
        } catch (C5) {
            console.log("exception: " + C5)
        }
        Cz.restore()
    };
    var U = function () {
        var Cz = A1.getStateForDocumentId(A1.getDocumentId());
        j(Cz)
    };
    var Be = function (C0, C1, C5, C6) {
        var C4 = virtualViewer.getPageCount();
        if (C0 === "pages") {
            if (isNaN(C5) || isNaN(C6)) {
                return "Invalid characters in page range"
            }
            if (C5 > C4 || C6 > C4) {
                return "Page Range out of bounds"
            }
            if (C5 < 1 || C6 < 1) {
                return "Page range out of bounds"
            }
            if (C5 > C6) {
                return "Invalid page range (first page > last page)"
            }
        }
        if (C0 === "complex") {
            if (C1 === "") {
                return "Blank complex page range"
            }
            var C9 = C1.split(",");
            for (var C8 = 0; C8 < C9.length; C8++) {
                var C2 = C9[C8];
                var C7 = C2.split("-");
                for (var Cz = 0; Cz < C7.length; Cz++) {
                    var C3 = C7[Cz];
                    if (isNaN(C3)) {
                        return "Invalid character in complex page range: " + C3
                    }
                    if (C3 > C4 || C3 < 1) {
                        return "Complex page range out of bounds"
                    }
                }
            }
        }
        return "OK"
    };
    var Aa = function (C9, C6, C7, DE, C3, DD) {
        var C0 = A1.getStateForDocumentId(A1.getDocumentId());
        var Cz = C0.getDocumentModel();
        var DH = "Original";
        if ($("#vvEmailPDFSelected:checked").val()) {
            DH = "PDF"
        } else {
            if ($("#vvEmailTIFFSelected:checked").val()) {
                DH = "TIFF"
            }
        }
        var DC = $("#vvEmailAnnotationsSelected").is(":checked");
        var DB = $("#vvEmailAnnotationsTypeText").is(":checked");
        var DF = $("#vvEmailAnnotationsTypeNonText").is(":checked");
        var C8 = "pages";
        var C4 = 0;
        var DG = Cz.getDocumentLength();
        var C5 = C4 + "-" + DG;
        var DA = true;
        var C2 = new URI(vvConfig.servletPath);
        C2.addQuery("action", "emailDocument");
        C2.addQuery("format", DH);
        C2.addQuery("pageRangeType", C8);
        C2.addQuery("pageRangeValue", C5);
        C2.addQuery("pageCount", Cz.getDocumentLength());
        C2.addQuery("textAnnotations", DB);
        C2.addQuery("nonTextAnnotations", DF);
        C2.addQuery("fromAddress", C9);
        C2.addQuery("toAddresses", C6);
        if (C7) {
            C2.addQuery("ccAddresses", C7)
        }
        if (DE) {
            C2.addQuery("bccAddresses", DE)
        }
        C2.addQuery("subject", C3);
        C2.addQuery("emailBody", DD);
        C2.addQuery("documentId", A1.getDocumentId());
        C2.addQuery("clientInstanceId", A1.getClientInstanceId());
        C2.addQuery("cacheBuster", Math.random());
        C2.addQuery("modelJSON", JSON.stringify(Cz.model));
        C2.addQuery("annotations", JSON.stringify(C0.getAnnotationLayers(true)));
        if (A1.getOverlayPath() !== null) {
            C2.addQuery("overlayPath", A1.getOverlayPath())
        }
        var C1 = C2.query();
        C2.query("");
        if (DA) {
            async = false
        }
        $.ajax({
            url: C2.toString(), type: "POST", dataType: "json", data: C1, async: async, success: function (DI) {
                Ak(false)
            }, error: function (DI) {
                AB(DI);
                Ak(false)
            }
        });
        return "OK"
    };
    var Ci = function (C1) {
        var Cz = virtualViewer.getStateForDocumentId(virtualViewer.getDocumentId());
        var C8 = "Original";
        if ($("#vvExportPDFSelected:checked").val()) {
            C8 = "PDF"
        } else {
            if ($("#vvExportTIFFSelected:checked").val()) {
                C8 = "TIFF"
            }
        }
        var C5 = parseInt($("#vvExportFirstPage").val(), 10);
        var C7 = parseInt($("#vvExportLastPage").val(), 10);
        var DA = $("#vvExportComplex").val();
        var C0 = $("input[name=exportRangeType]:checked").val();
        var C2 = "undefined";
        if (C0 === "complex") {
            C2 = DA
        }
        if (C0 === "pages") {
            C2 = C5 + "-" + C7
        }
        if (C0 === "current") {
            C2 = Cz.getPageNumber()
        }
        var C4 = $("#vvExportAnnotationsSelected").is(":checked");
        var C9 = $("#vvExportAnnotationsTypeText").is(":checked");
        var DC = $("#vvExportAnnotationsTypeNonText").is(":checked");
        var DB = Be(C0, C2, C5, C7);
        if (DB !== "OK") {
            alert(DB);
            return false
        }
        var C6 = virtualViewer.getDocumentModel();
        var C3 = $("form#vvExportForm");
        if (C3.attr("action") !== vvConfig.servletPath) {
            C3.attr("action", vvConfig.servletPath)
        }
        var DD = $("#vvExportFormHiddenFields");
        DD.html("");
        DD.append("<input type='hidden' name='action' value='newExport'>");
        DD.append("<input type='hidden' name='format' value='" + C8 + "'>");
        if (AW) {
            DD.append("<input type='hidden' name='exportFileName' value ='" + encodeURIComponent(AW()) + "'>")
        }
        DD.append("<input type='hidden' name='textAnnotations' value='" + C9 + "'>");
        DD.append("<input type='hidden' name='nonTextAnnotations' value='" + DC + "'>");
        DD.append("<input type='hidden' name='pageRangeType' value='" + C0 + "'>");
        DD.append("<input type='hidden' name='pageRangeValue' value='" + C2 + "'>");
        DD.append("<input type='hidden' name='annotations' value='" + CT(JSON.stringify(Cz.getAnnotationLayers(true))) + "'>");
        DD.append("<input type='hidden' name='modelJSON' value='" + CT(JSON.stringify(C6.model)) + "'>");
        DD.append("<input type='hidden' name='clientInstanceId' value='" + CT(A1.getClientInstanceId()) + "'>");
        DD.append("<input type='hidden' name='pageCount' value='" + virtualViewer.getPageCount() + "'>");
        C3.submit();
        return true
    };
    var L = function () {
        var DC = document.getElementById("vvOuterDiv");
        var C0 = A1.getCurrentState();
        var C9 = C0.getZoomModeForPage(C0.getPageNumber());
        if (C9 === null || C9 === undefined) {
            C9 = vvConfig.defaultZoomMode
        }
        var C5 = C0.getZoomForPage(C0.getPageNumber());
        if (!C5) {
            C5 = 100
        }
        var C4 = virtualViewer.getOriginalHeight();
        var C1 = virtualViewer.getOriginalWidth();
        if (!C1 || !C4) {
            return
        }
        var DA = DC.clientWidth;
        var C6 = DC.clientHeight;
        var C3 = C0.getRotationForPage(C0.getPageNumber());
        if ((C3 === 90) || (C3 === 270)) {
            DA = DC.clientHeight;
            C6 = DC.clientWidth
        }
        var C2 = C1;
        var Cz = C4;
        var C8 = DA / C1;
        var C7 = C6 / C4;
        var DB = C5 / 100;
        if (C9 === vvDefines.zoomModes.fitHeight || C9 === vvDefines.zoomModes.fitWidth || C9 === vvDefines.zoomModes.fitWindow || C9 === vvDefines.zoomModes.fitLast) {
            if (C1 && C4) {
                if (C9 === vvDefines.zoomModes.fitWindow || C9 === vvDefines.zoomModes.fitLast) {
                    if (C8 >= C7) {
                        if ((C3 === 90) || (C3 === 270)) {
                            C0.setZoomModeForPage(C0.getPageNumber(), vvDefines.zoomModes.fitWidth);
                            C9 = vvDefines.zoomModes.fitWidth
                        } else {
                            C0.setZoomModeForPage(C0.getPageNumber(), vvDefines.zoomModes.fitHeight);
                            C9 = vvDefines.zoomModes.fitHeight
                        }
                    } else {
                        if ((C3 === 90) || (C3 === 270)) {
                            C0.setZoomModeForPage(C0.getPageNumber(), vvDefines.zoomModes.fitHeight);
                            C9 = vvDefines.zoomModes.fitHeight
                        } else {
                            C0.setZoomModeForPage(C0.getPageNumber(), vvDefines.zoomModes.fitWidth);
                            C9 = vvDefines.zoomModes.fitWidth
                        }
                    }
                }
            }
            if (C9 === vvDefines.zoomModes.fitWidth) {
                if ((C3 === 90) || (C3 === 270)) {
                    C2 = C1 * C7;
                    Cz = C4 * C7;
                    DB = ((Cz / C4))
                } else {
                    C2 = C1 * C8;
                    Cz = C4 * C8;
                    DB = ((C2 / C1))
                }
            }
            if (C9 === vvDefines.zoomModes.fitHeight) {
                if ((C3 === 90) || (C3 === 270)) {
                    C2 = C1 * C8;
                    Cz = C4 * C8;
                    DB = ((C2 / C1))
                } else {
                    C2 = C1 * C7;
                    Cz = C4 * C7;
                    DB = ((Cz / C4))
                }
            }
            C0.setZoomForPage(C0.getPageNumber(), DB * 100)
        } else {
            if (DB !== 1) {
                C2 = C2 * DB;
                Cz = Cz * DB
            }
        }
        i = Math.round(C2);
        Ao = Math.round(Cz)
    };
    var AZ = function () {
        virtualViewer.verticalGuide = new Cz(true);
        virtualViewer.horizontalGuide = new Cz(false);
        function Cz(C1) {
            var C0 = this;
            this.locked = false;
            this.visible = false;
            this.value = 0;
            this.vertical = C1
        }

        if ((BrowserDetect.browser === "Explorer") && ((BrowserDetect.version > 8) && (BrowserDetect.version < 11))) {
            $("#vvImageCanvas").bind("contextmenu", A1.annPopUpHandler)
        } else {
            $("#vvDummyScroller").bind("contextmenu", A1.annPopUpHandler)
        }
    };
    VirtualViewer.prototype.annPopUpHandler = function (C5) {
        if (Modernizr.touch) {
            C5 = Bv(C5)
        }
        C5.preventDefault();
        C5.stopPropagation();
        var C9 = C5.clientX;
        var C7 = C5.clientY;
        var C8 = Af(document.getElementById("vvImageCanvas"));
        var DA = AH(C5.clientX - C8[0], C5.clientY - C8[1]);
        var Cz = myPainter.getAnnotationAtPoint(DA);
        if (myPainter.checkLayerPermission(myPainter.getCurrentLayer(), vvDefines.permissionLevels.PERM_EDIT) === false) {
            alert(getLocalizedValue("errors.editAnnOnLayerPermission", "You do not have permission to edit annotations on this layer."));
            return
        }
        var C3 = null;
        if (!Cz) {
            $("#vvAnnPopUp").hide()
        } else {
            A1.setCurrentAnnObject(Cz);
            if (Cz.collapsed) {
                return
            }
            if (Cz.getType() === vvDefines.annotationTypes.SANN_BITMAP) {
                $("#vvAnnText").hide();
                $("#vvAnnTextEditButton").hide();
                $("#vvAnnColor").hide();
                if (!A1.vvAnnThicknessObject) {
                    A1.vvAnnThicknessObject = $("#vvAnnThickness").detach()
                }
            } else {
                $("#vvAnnText").show();
                $("#vvAnnTextEditButton").show();
                $("#vvAnnColor").show()
            }
            $("#vvAnnLineColorLabel").hide();
            $("#vvAnnBorderColorLabel").hide();
            $("#vvAnnFillColorLabel").hide();
            $("#vvAnnTextColorLabel").hide();
            var C0 = Cz.getLineWidth();
            if (A1.vvAnnThicknessObject) {
                A1.vvAnnThicknessObject.find("#vvAnnLineSizeValue").html(C0)
            } else {
                $("#vvAnnLineSizeValue").html(C0)
            }
            if (Cz.getFontColor()) {
                $("#vvAnnTextColorLabel").show();
                if (!A1.vvAnnThicknessObject) {
                    A1.vvAnnThicknessObject = $("#vvAnnThickness").detach()
                }
                $("#vvAnnText").show();
                $("#vvAnnTextEditButton").show();
                C3 = Cz.getFontColor()
            } else {
                if (Cz.getFillColor()) {
                    $("#vvAnnFillColorLabel").show();
                    if (!A1.vvAnnThicknessObject) {
                        A1.vvAnnThicknessObject = $("#vvAnnThickness").detach()
                    }
                    $("#vvAnnText").hide();
                    $("#vvAnnTextEditButton").hide();
                    C3 = Cz.getFillColorRGB()
                } else {
                    if (Cz.getLineColor()) {
                        $("#vvAnnLineColorLabel").show();
                        if (A1.vvAnnThicknessObject && (Cz.getType() !== vvDefines.annotationTypes.SANN_BITMAP)) {
                            A1.vvAnnThicknessObject.insertAfter("#vvAnnColor");
                            A1.vvAnnThicknessObject = null
                        }
                        $("#vvAnnText").hide();
                        $("#vvAnnTextEditButton").hide();
                        C3 = Cz.getLineColorRGB()
                    }
                }
            }
            if (Cz.getFontName()) {
                $("#vvAnnTextFaceSelect").val(toTitleCase(Cz.getFontName()))
            }
            if (Cz.getFontSize()) {
                var C4 = findClosestFontSize(Cz.getFontSize());
                $("#vvAnnTextSizeSelect").val(C4)
            }
            if (Cz.getFontBold() === true) {
                $("#vvAnnTextStyleBold").prop("checked", true)
            } else {
                $("#vvAnnTextStyleBold").prop("checked", false)
            }
            if (Cz.getFontItalic() === true) {
                $("#vvAnnTextStyleItalic").prop("checked", true)
            } else {
                $("#vvAnnTextStyleItalic").prop("checked", false)
            }
            if (Cz.getFontUnderline() === true) {
                $("#vvAnnTextStyleUnderline").prop("checked", true)
            } else {
                $("#vvAnnTextStyleUnderline").prop("checked", false)
            }
            for (var C6 = 0; C6 < vvDefines.annColors.length; C6 += 1) {
                var C2 = vvDefines.annColors[C6];
                if (C3) {
                    if (C2.toLowerCase() === C3.toLowerCase()) {
                        cName = "vvAnnColorGrid";
                        O(cName, $("#" + cName + C6)[0], C6);
                        break
                    }
                }
            }
            $("#vvAnnColorCustomInput").attr("value", C3);
            var DB = $("#vvOuterDiv").width();
            var C1 = $("#vvOuterDiv").height();
            if ((C9 + $("#vvAnnPopUp").width()) > DB) {
                C9 = DB - $("#vvAnnPopUp").width()
            }
            if ((C7 + $("#vvAnnPopUp").height()) > C1) {
                C7 = C1 - $("#vvAnnPopUp").height()
            }
            $("#vvAnnPopUp").css("top", C7);
            $("#vvAnnPopUp").css("left", C9);
            $("#vvAnnPopUp").show()
        }
        virtualViewer.paintCanvases();
        return false
    };
    var Cw = function (C0) {
        var C3 = this;
        var C1 = C3.src;
        var C6 = parseInt(BT(C1, "virtualPageNumber"), 10);
        var C4 = BT(C1, "documentListThumb");
        var C2 = BT(C1, "searchThumb");
        var C5 = "#vvPageThumbs ";
        if (C4 === "true") {
            C5 = "#vvDocThumbs "
        } else {
            if (C2 === "true") {
                C5 = "#vvSearch "
            }
        }
        var DA = $(C5 + "td");
        var C7 = DA[C6];
        var C9 = $(C7).children("div");
        var Cz = $(C9).children("img");
        if (!Cz) {
            return (false)
        } else {
            Cz.addClass("displayedThumb");
            Cz.css("visibility", "visible")
        }
        var C8 = 0;
        if (C3.height > 0) {
            C8 = C3.height
        } else {
            if (Cz.height > 0) {
                C8 = Cz.css("height")
            }
        }
        if (C8 > 0) {
            Cz.css("position", "relative")
        }
        BV()
    };
    var G = function (C3) {
        if (!C3) {
            C3 = window.event
        }
        var C0 = null;
        if ((BrowserDetect.browser === "Explorer") && (BrowserDetect.version <= 9)) {
            C0 = $(C3.srcElement);
            C3.srcElement.src = "resources/pageNotFound.png"
        } else {
            C0 = $(C3.target);
            C3.target.src = "resources/pageNotFound.png"
        }
        C0.css("visibility", "");
        C0.addClass("displayedThumb");
        var Cz = C0.css("width");
        var C1 = C0.css("height");
        var C2 = vvDefines.thumbnailSize;
        if (Cz > C1) {
            C0.css("height", (C1 / (Cz / C2)) + "px");
            C0.css("width", C2 + "px")
        } else {
            C0.css("width", (Cz / (C1 / C2)) + "px");
            C0.css("height", C2 + "px")
        }
    };
    $.event.special.vvDeletePages = {
        _default: function (Cz) {
            A1.cutSelection(true)
        }
    };
    $.event.special.vvCutPages = {
        _default: function (Cz) {
            A1.cutSelection(false)
        }
    };
    $.event.special.vvCopyPages = {
        _default: function (Cz) {
            A1.copySelection()
        }
    };
    $.event.special.vvPastePages = {
        _default: function (C0, Cz) {
            A1.pasteSelection(Cz)
        }
    };
    var Ab = function (C5, C4, C0, C3, Cz) {
        if (C4) {
            C4.detach();
            var C2 = document.createElement("tbody");
            C2.id = C4[0].id + "Body";
            for (var C1 = 0; C1 < C0; C1 += 1) {
                var C7 = document.createElement("tr");
                var C6 = document.createElement("td");
                $(C7).addClass("thumbRowEmpty");
                $(C6).addClass("thumbCell");
                $(C7).hide();
                $(C6).data("pageNumber", C1);
                if (C3 !== true) {
                    if (A1.checkPageManipulationsEnabled() && Cz !== true) {
                        $(C7).contextMenu("vvThumbContextMenu", W)
                    }
                }
                C7.appendChild(C6);
                C2.appendChild(C7)
            }
            C4.append(C2);
            C4.appendTo(C5)
        }
    };
    var AO = function (Cz, C4, C0, C8, C3) {
        var C6, C5;
        if (C4) {
            n(C4, C0)
        }
        if (vvConfig.showThumbnailPanel === true) {
            if (C4) {
                C6 = $("#vvDocThumbs");
                C5 = $("#vvDocThumbsInternal");
                thumbCount = A1.documentList.length
            } else {
                if (C0) {
                    C6 = $("#vvSearchResults");
                    C5 = $("#vvSearchInternal");
                    thumbCount = Cz.getPageCount()
                } else {
                    C6 = $("#vvPageThumbs");
                    C5 = $("#vvPageThumbsInternal");
                    thumbCount = Cz.getPageCount()
                }
            }
            if (!C8) {
                C8 = 0
            }
            if (!C3) {
                C3 = thumbCount
            }
            if (C5.find("tr").length === 0) {
                Ab(C6, C5, thumbCount, C4, C0)
            }
            if (C5) {
                var C2 = $("#" + C5[0].id + "Body");
                var DB = C2.find("tr");
                var C9 = C2.find("td");
                C5.detach();
                for (var C1 = C8; C1 < C3; C1 += 1) {
                    var DA = DB[C1];
                    if (C0) {
                        if (!Cz.getSearchResultsForPage(C1)) {
                            continue
                        }
                    }
                    $(DA).removeClass("thumbRowEmpty");
                    $(DA).show();
                    thumb = Bs(C1, C4);
                    $(thumb).bind("click", function (DD) {
                        var DE = $(this).closest("td").data("pageNumber");
                        if (C4 === true) {
                            A1.openInTab(A1.documentList[DE])
                        } else {
                            var DC = A1.getDocumentModel();
                            if ((DD.ctrlKey === true) || (DD.metaKey === true)) {
                                if (A1.checkPageManipulationsEnabled()) {
                                    DC.togglePageSelection(DE)
                                }
                            } else {
                                if (DD.shiftKey === true) {
                                    if (A1.checkPageManipulationsEnabled()) {
                                        DC.selectRange(DE)
                                    }
                                } else {
                                    if (A1.checkPageManipulationsEnabled()) {
                                        DC.clearPageSelection();
                                        DC.addPageToSelection(DE)
                                    }
                                    A1.currentMatch = 0;
                                    A1.setPage(DE)
                                }
                            }
                        }
                    });
                    var C7 = C9[C1];
                    C7.appendChild(thumb)
                }
                $(C5).click(function (DD) {
                    var DC = A1.getDocumentModel();
                    var DE = $(DD.target).closest("div");
                    if ((DE.length > 0) && (DE.hasClass("thumbBox") !== true) && (DE.hasClass("selectedThumbBox") !== true) && (DE.hasClass("activeThumbBox") !== true)) {
                        if (DC) {
                            DC.clearPageSelection()
                        }
                    }
                });
                BV();
                A4();
                C6.append(C5)
            }
        }
    };
    var Bs = function (C2, C1) {
        var C0 = document.createElement("div");
        var Cz = document.createElement("img");
        C0.className = "thumbBox";
        if (C1 === true) {
            C0.title = A1.documentList[C2]
        } else {
            C0.title = "Page " + (C2 + 1)
        }
        Cz.src = AE;
        Cz.className = "displayedThumb";
        if ((BrowserDetect.browser !== "Explorer") || ((BrowserDetect.browser === "Explorer") && (BrowserDetect.version > 8))) {
            if (!Modernizr.touch) {
                $(C0).tooltip({show: false, hide: false})
            }
        }
        C0.appendChild(Cz);
        return C0
    };
    var Q = function (C0) {
        var C1 = A1.getStateForDocumentId(A1.getDocumentId());
        if (!C0) {
            C0 = window.event
        }
        if (J) {
            clearTimeout(J)
        }
        var Cz = $("#vvThumbs").tabs("option", "active");
        if (Cz === 0) {
            J = setTimeout(function (C2) {
                A1.loadVisibleThumbs(C2, C1, false, false)
            }, 200)
        } else {
            if (Cz === 1) {
                J = setTimeout(function (C2) {
                    A1.loadVisibleThumbs(C2, C1, true, false)
                }, 200)
            } else {
                J = setTimeout(function (C2) {
                    A1.loadVisibleThumbs(C2, C1, false, true)
                }, 200)
            }
        }
    };
    VirtualViewer.prototype.processWindowResize = function (Cz) {
        if (!Cz) {
            Cz = window.event
        }
        Cv(true)
    };
    var Cv = function (C0) {
        var Cz = A1.getStateForDocumentId(A1.getDocumentId());
        $("#vvImageCanvas")[0].width = $("#vvOuterDiv").width();
        $("#vvImageCanvas")[0].height = $("#vvOuterDiv").height();
        if ((BrowserDetect.browser === "Explorer") && (BrowserDetect.version < 9)) {
            $("#vvImageCanvas").children().width($("#vvOuterDiv").width());
            $("#vvImageCanvas").children().height($("#vvOuterDiv").width())
        }
        if (BrowserDetect.OS.indexOf("iPhone/iPod") > -1) {
            return
        }
        A1.loadVisibleThumbs(null, Cz, false);
        if (C0) {
            L();
            BN()
        }
    };
    VirtualViewer.prototype.getDocumentId = function () {
        return Au
    };
    VirtualViewer.prototype.getCurrentState = function () {
        return A1.getStateForDocumentId(A1.getDocumentId())
    };
    VirtualViewer.prototype.setDocumentId = function (Cz) {
        Au = Cz
    };
    VirtualViewer.prototype.getDisplayName = function () {
        var Cz = A1.getStateForCurrentDocument();
        if (Cz) {
            return Cz.getDisplayName()
        } else {
            return null
        }
    };
    VirtualViewer.prototype.getOpenDocuments = function () {
        return this.openDocuments
    };
    VirtualViewer.prototype.getStateForDocumentId = function (Cz) {
        return this.openDocuments[Cz]
    };
    VirtualViewer.prototype.getStateForCurrentDocument = function () {
        return this.openDocuments[A1.getDocumentId()]
    };
    VirtualViewer.prototype.setStateForDocumentId = function (C0, Cz) {
        if (Cz) {
            this.openDocuments[C0] = Cz
        } else {
            delete this.openDocuments[C0]
        }
    };
    VirtualViewer.prototype.loadVisibleThumbs = function (DF, C3, DB, C5) {
        if (!DF) {
            DF = window.event
        }
        if (BrowserDetect.OS.indexOf("iPhone/iPod") > -1) {
            if (window.orientation === 0) {
                return
            }
        }
        var C9 = document.getElementById("vvPageThumbs");
        var DE = "#vvPageThumbs ";
        if (DB === true) {
            C9 = document.getElementById("vvDocThumbs");
            DE = "#vvDocThumbs "
        } else {
            if (C5 === true) {
                C9 = document.getElementById("vvSearchResults");
                DE = "#vvSearchResults "
            }
        }
        if (C9) {
            var C0 = C9.scrollTop;
            var DA = C0 + $(DE)[0].clientHeight;
            var DD = 0;
            var C6 = $(DE + "tr");
            var C1 = $(DE + "td");
            var C2 = 0;
            var DG = C6.not(".thumbRowEmpty");
            if (DG.length > 0) {
                C2 = $(DG[0]).height()
            } else {
                return
            }
            DD = parseInt(C0 / C2, 10) - 2;
            if (DD < 0) {
                DD = 0
            }
            var DL = C1.length;
            if (DB === true) {
                DL = A1.documentList.length
            }
            for (var DH = DD; DH < DL; DH += 1) {
                var Cz = C1[DH];
                var DC = $(Cz).children("div");
                var DJ = $(DC).children("img");
                var DI = Cz.offsetTop;
                var C8 = DI + Cz.clientHeight;
                if ((DI < DA) && (C8 > C0)) {
                    if (DJ.attr("src").indexOf(AE) > -1) {
                        var DK = document.createElement("img");
                        var C4 = new URI(vvConfig.servletPath);
                        C4.addQuery("action", "getThumbnail");
                        if (C5 === true) {
                            var C7 = DC.closest("td").data("pageNumber");
                            C4.addQuery("documentId", CH(C7, C3));
                            C4.addQuery("pageNumber", CY(C7));
                            C4.addQuery("searchThumb", true)
                        } else {
                            if (DB === false) {
                                C4.addQuery("documentId", CH(DH, C3));
                                C4.addQuery("pageNumber", CY(DH))
                            } else {
                                C4.addQuery("documentId", A1.documentList[DH]);
                                C4.addQuery("pageNumber", 0);
                                C4.addQuery("documentListThumb", true)
                            }
                        }
                        C4.addQuery("thumbnailSize", vvDefines.thumbnailSize);
                        C4.addQuery("clientInstanceId", A1.getClientInstanceId());
                        C4.addQuery("virtualPageNumber", DH);
                        if (DB) {
                            C4.addQuery("pageCount", 1)
                        } else {
                            if (C3) {
                                C4.addQuery("pageCount", C3.getPageCount())
                            }
                        }
                        if (A1.getOverlayPath() !== null) {
                            C4.addQuery("overlayPath", A1.getOverlayPath())
                        }
                        if ((vvDefines.cacheBuster === true) || (BrowserDetect.browser === "Explorer")) {
                            C4.addQuery("cacheBuster", Math.random())
                        }
                        DK.src = C4.toString();
                        $(DK).on("load", Cw);
                        $(DK).on("error", G);
                        DK.style.visibility = "hidden";
                        $(DJ).replaceWith(DK)
                    }
                } else {
                    if (DI > DA) {
                        break
                    }
                }
            }
        }
    };
    var CH = function (C1, C0) {
        var C2 = C0.getDocumentId();
        var Cz = C0.getDocumentModel();
        if (!C0.getDocumentId()) {
            return null
        }
        if (Cz) {
            C2 = decodeURIComponent(Cz.model.pageData[C1].documentId)
        }
        return C2
    };
    var CY = function (C1) {
        var Cz = C1;
        var C0 = virtualViewer.getDocumentModel();
        if (!A1.getDocumentId()) {
            return null
        }
        if (C0) {
            Cz = C0.model.pageData[C1].pageIndex
        }
        return Cz
    };
    var Ay = function () {
        var C0 = document.getElementById("vvPageThumbs");
        var C2 = C0.getElementsByTagName("td");
        if (C2.length > 0) {
            if (isNaN(CF)) {
                CF = 0
            }
            var Cz = C2[CF];
            var C5 = C0.scrollTop;
            var C4 = C5 + C0.clientHeight;
            var C1 = Cz.offsetTop;
            var C3 = C1 + Cz.clientHeight;
            if ((C3 > C4) || (C1 < C5)) {
                C0.scrollTop = C1
            }
        }
    };
    var BA = function () {
        var C6 = document.getElementById("vvDocThumbs");
        var C0 = 0;
        for (var Cz = 0; Cz < A1.documentList.length; Cz += 1) {
            if (A1.documentList[Cz] === A1.getDocumentId()) {
                C0 = Cz;
                break
            }
        }
        var C2 = document.getElementById("thumbBox" + C0);
        if (!C2) {
            return
        }
        var C5 = C6.scrollTop;
        var C4 = C5 + C6.clientHeight;
        var C1 = C2.offsetTop;
        var C3 = C1 + C2.clientHeight;
        if ((C3 > C4) || (C1 < C5)) {
            C6.scrollTop = C1
        }
    };
    var Bg = function () {
        var C1 = document.getElementById("vvSearchResults");
        var C2 = C1.getElementsByTagName("td");
        if (C2.length > 0) {
            var Cz = C2[CF];
            var C5 = C1.scrollTop;
            var C4 = C5 + C1.clientHeight;
            var C0 = Cz.offsetTop;
            var C3 = C0 + Cz.clientHeight;
            if ((C3 > C4) || (C0 < C5)) {
                C1.scrollTop = C0
            }
        }
    };
    var BV = function () {
        $(".activeThumbBox").removeClass("activeThumbBox");
        $(".activeThumb").removeClass("activeThumb");
        var C1 = $("#vvPageThumbs td div:eq(" + CF + ")");
        C1.addClass("activeThumbBox");
        C1.find("img").addClass("activeThumb");
        var C0 = 0;
        for (var Cz = 0; Cz < A1.documentList.length; Cz += 1) {
            if (A1.documentList[Cz] === A1.getDocumentId()) {
                C0 = Cz;
                break
            }
        }
        var C2 = $("#vvDocThumbs td div:eq(" + C0 + ")");
        C2.addClass("activeThumbBox");
        C2.find("img").addClass("activeThumb");
        var C3 = $("#vvSearch td").eq(CF).find("div");
        C3.addClass("activeThumbBox");
        C3.find("img").addClass("activeThumb")
    };
    VirtualViewer.prototype.exportDocument = function () {
        $("#vvExportDialog").dialog("open")
    };
    VirtualViewer.prototype.saveDocument = function (C2) {
        var C4 = A1.getStateForDocumentId(A1.getDocumentId());
        var Cz = C4.getDocumentModel();
        if (A1.getDocumentId().indexOf("VirtualDocument") > -1) {
            A1.disabledToolbarOptionDialog("save", "Saving a Virtual Document is not allowed.");
            $("#vvDisableToolbarDialog").dialog("open");
            return
        }
        if (Cz.getModifiedSinceInit() === true) {
            var C0 = true;
            $("#vvSaveDocumentDialog").dialog("open");
            var C1 = new URI(vvConfig.servletPath);
            C1.addQuery("action", "saveDocumentModel");
            C1.addQuery("documentId", A1.getDocumentId());
            C1.addQuery("clientInstanceId", A1.getClientInstanceId());
            C1.addQuery("cacheBuster", Math.random());
            C1.addQuery("modelJSON", JSON.stringify(Cz.model));
            C1.addQuery("annotations", JSON.stringify(C4.getAnnotationLayers(true)));
            if (A1.getOverlayPath() !== null) {
                C1.addQuery("overlayPath", A1.getOverlayPath())
            }
            var C3 = C1.query();
            C1.query("");
            if (C2) {
                C0 = false
            }
            $.ajax({
                url: C1.toString(), type: "POST", dataType: "json", data: C3, async: C0, success: function (C9) {
                    var C5 = null;
                    var DB = null;
                    B6();
                    if (AB(C9) === true) {
                        $("#vvSaveDocumentDialog").dialog("close");
                        return
                    }
                    if (CI) {
                        CI(A1.getDocumentId(), A1.getClientInstanceId(), C9.documentIdToReload)
                    }
                    if (virtualViewer.getSaveAnnotationsCompletedHandler()) {
                        var C8 = virtualViewer.getSaveAnnotationsCompletedHandler();
                        C8(virtualViewer.getDocumentId(), virtualViewer.getClientInstanceId(), C9.documentIdToReload)
                    }
                    if (vvConfig.reloadDocumentOnSave === true) {
                        var DA = encodeURIComponent(C9.documentIdToReload);
                        var C7 = A1.getDocumentId();
                        if (!DA || (DA === "undefined")) {
                            DA = C7
                        }
                        A1.setDocumentId(DA);
                        C5 = A1.getStateForDocumentId(C7);
                        C5.documentId = DA;
                        A1.setStateForDocumentId(C7, undefined);
                        A1.setStateForDocumentId(DA, C5);
                        A1.removeFromRecentlyViewed(C7);
                        A1.addToRecentlyViewed(DA);
                        for (var DC = 0; DC < A1.tabs.length; DC += 1) {
                            if (A1.tabs[DC] === C7) {
                                A1.tabs[DC] = DA;
                                break
                            }
                        }
                        DB = C5.getRotationForPage(C5.getPageNumber());
                        A1.getDocumentModel().cleanAfterSave(DA);
                        A1.requestDocumentList();
                        AO(C5, true);
                        A1.loadVisibleThumbs(null, C5, true)
                    } else {
                        C5 = A1.getStateForDocumentId(A1.getDocumentId());
                        DB = C5.getRotationForPage(C5.getPageNumber());
                        A1.getDocumentModel().cleanAfterSave(A1.getDocumentId())
                    }
                    if ((DB === 90) || (DB === 270)) {
                        var DD = C5.getOriginalWidthForPage(CF);
                        var C6 = C5.getOriginalHeightForPage(CF);
                        C5.setOriginalWidthForPage(CF, C6);
                        C5.setOriginalHeightForPage(CF, DD);
                        Cr = C6;
                        B9 = DD
                    }
                    C5.resetAfterSave();
                    U();
                    $("#vvSaveDocumentDialog").dialog("close");
                    A1.updateTabName(A1.tabs.indexOf(A1.getDocumentId()), A1.getDocumentId(), C5)
                }, error: function (C5) {
                    console.log("error")
                }
            })
        } else {
            C4 = virtualViewer.getStateForDocumentId(A1.getDocumentId());
            C4.saveAnnotationLayers();
            A1.updateTabName(A1.tabs.indexOf(A1.getDocumentId()), A1.getDocumentId(), C4)
        }
    };
    VirtualViewer.prototype.saveAllDocuments = function (C4) {
        for (var C1 = 0; C1 < A1.tabs.length; C1++) {
            var C6 = A1.getStateForDocumentId(A1.tabs[C1]);
            var C0 = C6.getDocumentModel();
            var Cz = null;
            if (C6.getDocumentId().indexOf("VirtualDocument") > -1) {
                A1.disabledToolbarOptionDialog("save", "Saving a Virtual Document is not allowed.");
                $("#vvDisableToolbarDialog").dialog("open");
                continue
            }
            if (C0.getModifiedSinceInit() === true) {
                var C2 = true;
                $("#vvSaveDocumentDialog").dialog("open");
                var C3 = new URI(vvConfig.servletPath);
                C3.addQuery("action", "saveDocumentModel");
                C3.addQuery("documentId", C6.getDocumentId());
                C3.addQuery("clientInstanceId", A1.getClientInstanceId());
                C3.addQuery("cacheBuster", Math.random());
                C3.addQuery("modelJSON", JSON.stringify(C0.model));
                C3.addQuery("annotations", JSON.stringify(C6.getAnnotationLayers(true)));
                if (A1.getOverlayPath() !== null) {
                    C3.addQuery("overlayPath", A1.getOverlayPath())
                }
                var C5 = C3.query();
                C3.query("");
                if (C4) {
                    C2 = false
                }
                $.ajax({
                    url: C3.toString(),
                    type: "POST",
                    dataType: "json",
                    data: C5,
                    async: C2,
                    success: function (DD) {
                        var DC = null;
                        B6();
                        if (AB(DD) === true) {
                            $("#vvSaveDocumentDialog").dialog("close");
                            return
                        }
                        if (CI) {
                            CI(A1.tabs[C1], A1.getClientInstanceId(), DD.documentIdToReload)
                        }
                        if (virtualViewer.getSaveAnnotationsCompletedHandler()) {
                            var DE = virtualViewer.getSaveAnnotationsCompletedHandler();
                            DE(virtualViewer.getDocumentId(), virtualViewer.getClientInstanceId(), DD.documentIdToReload)
                        }
                        if (vvConfig.reloadDocumentOnSave === true) {
                            var DB = encodeURIComponent(DD.documentIdToReload);
                            var C7 = A1.tabs[C1];
                            if (!DB || (DB === "undefined")) {
                                DB = C7
                            }
                            A1.setDocumentId(DB);
                            DC = A1.getStateForDocumentId(C7);
                            DC.documentId = DB;
                            A1.setStateForDocumentId(C7, undefined);
                            A1.setStateForDocumentId(DB, DC);
                            A1.removeFromRecentlyViewed(C7);
                            A1.addToRecentlyViewed(DB);
                            for (var DA = 0; DA < A1.tabs.length; DA += 1) {
                                if (A1.tabs[DA] === C7) {
                                    A1.tabs[DA] = DB;
                                    break
                                }
                            }
                            Cz = DC.getRotationForPage(DC.getPageNumber());
                            C0.cleanAfterSave(DB);
                            A1.requestDocumentList();
                            AO(DC, true);
                            A1.loadVisibleThumbs(null, DC, true)
                        } else {
                            DC = A1.getStateForDocumentId(A1.tabs[C1]);
                            Cz = DC.getRotationForPage(DC.getPageNumber());
                            C0.cleanAfterSave(C0.model.documentId)
                        }
                        if ((Cz === 90) || (Cz === 270)) {
                            var C8 = DC.getOriginalWidthForPage(CF);
                            var C9 = DC.getOriginalHeightForPage(CF);
                            DC.setOriginalWidthForPage(CF, C9);
                            DC.setOriginalHeightForPage(CF, C8);
                            Cr = C9;
                            B9 = C8
                        }
                        DC.setRotationForPage(DC.getPageNumber(), 0);
                        DC.resetAfterSave();
                        U();
                        $("#vvSaveDocumentDialog").dialog("close");
                        A1.updateTabName(C1, A1.tabs[C1], DC)
                    },
                    error: function (C7) {
                        console.log("error")
                    }
                })
            } else {
                C6 = virtualViewer.getStateForDocumentId(A1.tabs[C1]);
                C6.saveAnnotationLayers();
                A1.updateTabName(C1, A1.tabs[C1], C6)
            }
        }
    };
    VirtualViewer.prototype.saveSpecificDocument = function (C2, C4) {
        var C6 = A1.getStateForDocumentId(C2);
        var C0 = C6.getDocumentModel();
        var Cz = null;
        if (C2.indexOf("VirtualDocument") > -1) {
            A1.disabledToolbarOptionDialog("save", "Saving a Virtual Document is not allowed.");
            $("#vvDisableToolbarDialog").dialog("open");
            return
        }
        if (C0.getModifiedSinceInit() === true) {
            var C1 = true;
            $("#vvSaveDocumentDialog").dialog("open");
            var C3 = new URI(vvConfig.servletPath);
            C3.addQuery("action", "saveDocumentModel");
            C3.addQuery("documentId", C6.getDocumentId());
            C3.addQuery("clientInstanceId", A1.getClientInstanceId());
            C3.addQuery("cacheBuster", Math.random());
            C3.addQuery("modelJSON", JSON.stringify(C0.model));
            C3.addQuery("annotations", JSON.stringify(C6.getAnnotationLayers(true)));
            if (A1.getOverlayPath() !== null) {
                C3.addQuery("overlayPath", A1.getOverlayPath())
            }
            var C5 = C3.query();
            C3.query("");
            if (C4) {
                C1 = false
            }
            $.ajax({
                url: C3.toString(), type: "POST", dataType: "json", data: C5, async: C1, success: function (DD) {
                    var DC = null;
                    B6();
                    if (AB(DD) === true) {
                        $("#vvSaveDocumentDialog").dialog("close");
                        return
                    }
                    if (CI) {
                        CI(C2, A1.getClientInstanceId(), DD.documentIdToReload)
                    }
                    if (virtualViewer.getSaveAnnotationsCompletedHandler()) {
                        var DE = virtualViewer.getSaveAnnotationsCompletedHandler();
                        DE(virtualViewer.getDocumentId(), virtualViewer.getClientInstanceId(), DD.documentIdToReload)
                    }
                    if (vvConfig.reloadDocumentOnSave === true) {
                        var DB = encodeURIComponent(DD.documentIdToReload);
                        var C7 = C2;
                        if (!DB || (DB === "undefined")) {
                            DB = C7
                        }
                        A1.setDocumentId(A1.getDocumentId());
                        DC = A1.getStateForDocumentId(C7);
                        DC.documentId = DB;
                        A1.setStateForDocumentId(C7, undefined);
                        A1.setStateForDocumentId(DB, DC);
                        A1.removeFromRecentlyViewed(C7);
                        A1.addToRecentlyViewed(DB);
                        for (var DA = 0; DA < A1.tabs.length; DA += 1) {
                            if (A1.tabs[DA] === C7) {
                                A1.tabs[DA] = DB;
                                break
                            }
                        }
                        Cz = DC.getRotationForPage(DC.getPageNumber());
                        A1.getDocumentModel().cleanAfterSave(DB);
                        A1.requestDocumentList();
                        AO(DC, true);
                        A1.loadVisibleThumbs(null, DC, true)
                    } else {
                        DC = A1.getStateForDocumentId(C2);
                        Cz = DC.getRotationForPage(DC.getPageNumber());
                        C0.cleanAfterSave(C2)
                    }
                    if ((Cz === 90) || (Cz === 270)) {
                        var C8 = DC.getOriginalWidthForPage(CF);
                        var C9 = DC.getOriginalHeightForPage(CF);
                        DC.setOriginalWidthForPage(CF, C9);
                        DC.setOriginalHeightForPage(CF, C8);
                        Cr = C9;
                        B9 = C8
                    }
                    DC.setRotationForPage(DC.getPageNumber(), 0);
                    DC.resetAfterSave();
                    U();
                    $("#vvSaveDocumentDialog").dialog("close");
                    A1.updateTabName(A1.tabs.indexOf(C2), C2, DC)
                }, error: function (C7) {
                    console.log("error")
                }
            })
        } else {
            C6 = virtualViewer.getStateForDocumentId(C2);
            C6.saveAnnotationLayers();
            A1.updateTabName(A1.tabs.indexOf(C2), C2, C6)
        }
    };
    VirtualViewer.prototype.rotateClock = function () {
        A1.rotateImageBy(90)
    };
    VirtualViewer.prototype.rotateCounter = function () {
        A1.rotateImageBy(-90)
    };
    VirtualViewer.prototype.rotateImageBy = function (C2) {
        if ((Math.abs(C2) !== 0) && (Math.abs(C2) !== 90) && (Math.abs(C2) !== 180) && (Math.abs(C2) !== 270)) {
            return
        }
        var C1 = virtualViewer.getStateForDocumentId(A1.getDocumentId());
        var Cz = C1.getRotationForPage(C1.getPageNumber());
        var C0 = Cz;
        Cz += C2;
        if (Cz < 0) {
            Cz += 360
        }
        if (Cz >= 360) {
            Cz -= 360
        }
        C1.setRotationForPage(CF, Cz);
        this.adjustGuideRotation();
        L();
        BN()
    };
    VirtualViewer.prototype.rotateAllPagesBy = function (C3) {
        if ((Math.abs(C3) !== 0) && (Math.abs(C3) !== 90) && (Math.abs(C3) !== 180) && (Math.abs(C3) !== 270)) {
            return
        }
        var C2 = virtualViewer.getStateForDocumentId(A1.getDocumentId());
        var C0 = C2.getRotationForPage(C2.getPageNumber());
        var C1 = C0;
        C0 += C3;
        if (C0 < 0) {
            C0 += 360
        }
        if (C0 >= 360) {
            C0 -= 360
        }
        for (var Cz = 0; Cz < C2.getPageCount(); Cz += 1) {
            C2.setRotationForPage(Cz, C0)
        }
        this.adjustGuideRotation();
        L();
        BN()
    };
    VirtualViewer.prototype.rotateImageTo = function (C2) {
        if ((Math.abs(C2) !== 0) && (Math.abs(C2) !== 90) && (Math.abs(C2) !== 180) && (Math.abs(C2) !== 270)) {
            return
        }
        if (C2 === -90) {
            C2 = 270
        } else {
            if (C2 === -180) {
                C2 = 180
            } else {
                if (C2 === -270) {
                    C2 = 90
                }
            }
        }
        var C1 = virtualViewer.getStateForDocumentId(A1.getDocumentId());
        var Cz = C1.getRotationForPage(C1.getPageNumber());
        var C0 = Cz;
        Cz = C2;
        C1.setRotationForPage(C1.getPageNumber(), 0);
        C1.setRotationForPage(CF, Cz);
        this.adjustGuideRotation();
        L();
        BN()
    };
    VirtualViewer.prototype.adjustGuideRotation = function () {
        var Cz = A1.getCurrentState();
        var C3 = Cz.getRotationForPage(Cz.getPageNumber());
        var C4 = C3;
        if (C4 === 0) {
            C4 = 360
        }
        var C8 = C4 - A1.guideAngle;
        if (C8 === 360) {
            C8 = 0
        }
        if (C8 === -90) {
            C8 = 270
        }
        if (C8 === -180) {
            C8 = 180
        }
        if (C8 === -270) {
            C8 = 90
        }
        A1.guideAngle = C3;
        if (C8 === 0) {
            return
        }
        var C7 = this.horizontalGuide.value;
        var C5 = this.horizontalGuide.locked;
        var C6 = this.horizontalGuide.visible;
        var C0 = this.verticalGuide.value;
        var C2 = this.verticalGuide.locked;
        var C1 = this.verticalGuide.visible;
        if (C8 === 90) {
            this.horizontalGuide.value = C0;
            this.horizontalGuide.locked = C2;
            this.horizontalGuide.visible = C1;
            this.verticalGuide.locked = C5;
            this.verticalGuide.visible = C6;
            if (C3 === 90 || C3 === 270) {
                this.verticalGuide.value = B9 - C7
            } else {
                this.verticalGuide.value = Cr - C7
            }
        } else {
            if (C8 === 270) {
                this.horizontalGuide.locked = C2;
                this.horizontalGuide.visible = C1;
                this.verticalGuide.value = C7;
                this.verticalGuide.locked = C5;
                this.verticalGuide.visible = C6;
                if (C3 === 90 || C3 === 270) {
                    this.horizontalGuide.value = Cr - C0
                } else {
                    this.horizontalGuide.value = B9 - C0
                }
            } else {
                if (C3 === 90 || C3 === 270) {
                    this.horizontalGuide.value = Cr - C7;
                    this.verticalGuide.value = B9 - C0
                } else {
                    this.horizontalGuide.value = B9 - C7;
                    this.verticalGuide.value = Cr - C0
                }
            }
        }
        A1.paintCanvases(Cz)
    };
    function CG() {
        var Cz = null;
        if (vvConfig.zoomLevels) {
            Cz = vvConfig.zoomLevels
        } else {
            Cz = vvDefines.defaultZoomLevels
        }
        return Cz
    }

    function AN(C1) {
        var C0 = CG();
        for (var Cz = 0; Cz < C0.length; Cz++) {
            candidateZoom = C0[Cz];
            if (candidateZoom > C1) {
                return candidateZoom
            }
        }
        return C0[C0.length - 1]
    }

    function BW(C1) {
        var C0 = CG();
        for (var Cz = C0.length - 1; Cz > -1; Cz--) {
            candidateZoom = C0[Cz];
            if (candidateZoom < C1) {
                return candidateZoom
            }
        }
        return C0[0]
    }

    VirtualViewer.prototype.zoomIn = function () {
        var Cz = A1.getCurrentState();
        var C0 = Cz.getZoomForPage(Cz.getPageNumber());
        C0 = AN(C0);
        if (C0 > vvConfig.maxZoomPercent) {
            C0 = vvConfig.maxZoomPercent
        }
        Cz.setZoomModeForPage(Cz.getPageNumber(), vvDefines.zoomModes.fitCustom);
        Cz.setZoomForPage(Cz.getPageNumber(), C0);
        L();
        BN()
    };
    VirtualViewer.prototype.zoomOut = function () {
        var Cz = A1.getCurrentState();
        var C0 = Cz.getZoomForPage(Cz.getPageNumber());
        C0 = BW(C0);
        Cz.setZoomModeForPage(Cz.getPageNumber(), vvDefines.zoomModes.fitCustom);
        Cz.setZoomForPage(Cz.getPageNumber(), C0);
        L();
        if ((i < 1) || (Ao < 1)) {
            C0 = AN(C0);
            Cz.setZoomForPage(Cz.getPageNumber(), C0);
            return
        }
        BN()
    };
    VirtualViewer.prototype.setZoomPercent = function (C0) {
        var Cz = A1.getCurrentState();
        var C1 = C0;
        if (C1 > vvConfig.maxZoomPercent) {
            C1 = vvConfig.maxZoomPercent
        }
        Cz.setZoomModeForPage(Cz.getPageNumber(), vvDefines.zoomModes.fitCustom);
        Cz.setZoomForPage(Cz.getPageNumber(), C1);
        L();
        if ((i < 1) || (Ao < 1)) {
            C1 = AN(C1);
            Cz.setZoomForPage(Cz.getPageNumber(), C1);
            return
        }
        BN()
    };
    var BN = function () {
        var C0 = A1.getStateForDocumentId(A1.getDocumentId());
        var Cz = C0.getImageObject();
        if (!((BrowserDetect.browser === "Explorer") && (BrowserDetect.version < 9))) {
            $("#vvAnnPopUp").hide();
            A1.setCurrentAnnObject(null)
        }
        if (Ce(Cz)) {
            A1.paintCanvases(C0);
            if (B7) {
                clearTimeout(B7)
            }
            B7 = setTimeout(function (C1) {
                U()
            }, vvConfig.zoomTimeout)
        } else {
            U()
        }
    };
    VirtualViewer.prototype.flipX = function () {
        H = !H;
        A1.getCurrentState().setHorizontalFlipForPage(null, H);
        U()
    };
    VirtualViewer.prototype.flipY = function () {
        t = !t;
        A1.getCurrentState().setVerticalFlipForPage(null, t);
        U()
    };
    VirtualViewer.prototype.textSelectionAllowed = function () {
        var Cz = A1.getCurrentState();
        var C0 = (H === false && t === false && Cz.getRotationForPage(Cz.getPageNumber()) === 0);
        return C0
    };
    VirtualViewer.prototype.invertImage = function () {
        var Cz = virtualViewer.getStateForDocumentId(A1.getDocumentId());
        p = !p;
        Cz.setInvertImageForPage(CF, p);
        U()
    };
    VirtualViewer.prototype.despeckleImage = function () {
        var Cz = virtualViewer.getStateForDocumentId(A1.getDocumentId());
        Cz.setDespeckleForPage(CF);
        U()
    };
    VirtualViewer.prototype.setAntiAliasOff = function (C0) {
        var Cz = virtualViewer.getStateForDocumentId(A1.getDocumentId());
        Cz.setAntiAliasOffForPage(CF, C0);
        U()
    };
    VirtualViewer.prototype.setBrightness = function (C0) {
        var Cz = A1.getStateForDocumentId(A1.getDocumentId());
        Cz.setBrightnessForPage(CF, C0);
        $("#vvBrightnessDisplay").html(C0);
        U()
    };
    VirtualViewer.prototype.setContrast = function (C0) {
        var Cz = A1.getStateForDocumentId(A1.getDocumentId());
        Cz.setContrastForPage(CF, C0);
        $("#vvContrastDisplay").html(C0);
        U()
    };
    VirtualViewer.prototype.setGamma = function (C0) {
        var Cz = A1.getStateForDocumentId(A1.getDocumentId());
        Cz.setGammaForPage(CF, C0);
        $("#vvGammaDisplay").html(C0);
        U()
    };
    VirtualViewer.prototype.getBrightness = function () {
        var Cz = A1.getStateForDocumentId(A1.getDocumentId());
        if (Cz) {
            return Cz.getBrightnessForPage()
        }
    };
    VirtualViewer.prototype.getContrast = function () {
        var Cz = A1.getStateForDocumentId(A1.getDocumentId());
        if (Cz) {
            return Cz.getContrastForPage()
        }
    };
    VirtualViewer.prototype.getGamma = function () {
        var Cz = A1.getStateForDocumentId(A1.getDocumentId());
        if (Cz) {
            return Cz.getGammaForPage()
        }
    };
    VirtualViewer.prototype.fitWidth = function () {
        var Cz = A1.getCurrentState();
        Cz.setZoomModeForPage(Cz.getPageNumber(), vvDefines.zoomModes.fitWidth);
        L();
        U()
    };
    VirtualViewer.prototype.fitHeight = function () {
        var Cz = A1.getCurrentState();
        Cz.setZoomModeForPage(Cz.getPageNumber(), vvDefines.zoomModes.fitHeight);
        L();
        U()
    };
    VirtualViewer.prototype.fitWindow = function () {
        var Cz = A1.getCurrentState();
        Cz.setZoomModeForPage(Cz.getPageNumber(), vvDefines.zoomModes.fitWindow);
        L();
        U()
    };
    VirtualViewer.prototype.scrollDownByNPercent = function (Cz) {
        var C2 = document.getElementById("vvOuterDiv");
        var C0 = C2.scrollTop;
        var C1 = C2.scrollHeight * Cz / 100;
        C2.scrollTop = C0 + C1
    };
    VirtualViewer.prototype.scrollUpByNPercent = function (Cz) {
        var C2 = document.getElementById("vvOuterDiv");
        var C0 = C2.scrollTop;
        var C1 = C2.scrollHeight * Cz / 100;
        C2.scrollTop = C0 - C1
    };
    VirtualViewer.prototype.getPageCount = function () {
        return CB
    };
    VirtualViewer.prototype.firstPage = function () {
        A1.currentMatch = 0;
        A1.setPage(0)
    };
    VirtualViewer.prototype.nextPage = function () {
        A1.currentMatch = 0;
        A1.setPage(CF + 1)
    };
    VirtualViewer.prototype.previousPage = function () {
        A1.currentMatch = 0;
        A1.setPage(CF - 1)
    };
    VirtualViewer.prototype.lastPage = function () {
        A1.currentMatch = 0;
        A1.setPage(CB - 1)
    };
    VirtualViewer.prototype.panUp = function () {
        var Cz = document.getElementById("vvOuterDiv");
        Cz.scrollTop -= vvConfig.panIncrement
    };
    VirtualViewer.prototype.panDown = function () {
        var Cz = document.getElementById("vvOuterDiv");
        Cz.scrollTop += vvConfig.panIncrement
    };
    VirtualViewer.prototype.panLeft = function () {
        var Cz = document.getElementById("vvOuterDiv");
        Cz.scrollLeft -= vvConfig.panIncrement
    };
    VirtualViewer.prototype.panRight = function () {
        var Cz = document.getElementById("vvOuterDiv");
        Cz.scrollLeft += vvConfig.panIncrement
    };
    VirtualViewer.prototype.thumbPageDown = function () {
        vvPageThumbs.scrollTop += $("#vvPageThumbs").height()
    };
    VirtualViewer.prototype.thumbPageUp = function () {
        vvPageThumbs.scrollTop -= $("#vvPageThumbs").height()
    };
    VirtualViewer.prototype.setPage = function (C2) {
        var C0 = CF;
        A1.showingPageErrorDialog = true;
        $("#vvAnnPopUp").hide();
        A1.setCurrentAnnObject(null);
        if ((C2 < 0) || ((CB > 0) && (C2 >= CB))) {
            alert("No such page.");
            A4();
            A1.showingPageErrorDialog = false;
            return
        } else {
            A1.setCurrentAnnObject(null);
            $(".snowbound-text-annotation").remove();
            CF = C2
        }
        if (C0 >= CB) {
            CO()
        } else {
            CO(C0)
        }
        var C1 = A1.getCurrentState();
        C1.setPageNumber(C2);
        var Cz = C1.getImageObject();
        if (Cz) {
            Al(Cz)
        }
        BV();
        Ay();
        Bg();
        CC(true);
        A4();
        CP();
        AU();
        BN();
        A1.showingPageErrorDialog = false
    };
    var CC = function (Cz) {
        if (A1.waitDialogTimeoutId) {
            clearTimeout(A1.waitDialogTimeoutId)
        }
        if (Cz) {
            A1.waitDialogTimeoutId = setTimeout(function () {
                if (!A1.currentExceptionDialog) {
                    $("#vvWaitIndicator").dialog("open")
                }
            }, vvConfig.waitDialogTimeout)
        } else {
            $("#vvWaitIndicator").dialog("close")
        }
    };
    var Ax = function (Cz) {
        if (A1.waitDialogTimeoutId) {
            clearTimeout(A1.waitDialogTimeoutId)
        }
        if (Cz) {
            A1.waitDialogTimeoutId = setTimeout(function () {
                if (!A1.currentExceptionDialog) {
                    $("#vvPrintIndicator").dialog("open")
                }
            }, vvConfig.waitDialogTimeout)
        } else {
            $("#vvPrintIndicator").dialog("close")
        }
    };
    var Ak = function (Cz) {
        if (Cz) {
            $("#vvEmailIndicator").dialog("open")
        } else {
            $("#vvEmailIndicator").dialog("close")
        }
        console.log("Indicator On!")
    };
    VirtualViewer.prototype.printDocument = function () {
        if (vvConfig.defaultPrintingMethod === "local") {
            A1.printDocumentLocal()
        } else {
            A1.printDocumentServer()
        }
    };
    VirtualViewer.prototype.printDocumentServer = function () {
        $("#vvNewPrintFirstPage").val(1);
        $("#vvNewPrintLastPage").val(CB);
        $("#vvNewPrintComplex").val("1 - " + CB);
        $("#vvNewPrintOptionsAnnotationsCheckbox").removeClass("vvNewPrintAnnotationsDisabled");
        $("#vvNewPrintAnnotationsSelected").prop("checked", vvConfig.printBurnAnnotations);
        $("#vvNewPrintDialog").dialog("open")
    };
    VirtualViewer.prototype.printDocumentLocal = function () {
        $("#vvPrintFirstPage").val(1);
        $("#vvPrintLastPage").val(CB);
        if (A1.getDocumentId().indexOf("VirtualDocument") === 0) {
            $("#vvPrintAnnotationsSelected").prop("checked", false);
            $("#vvPrintAnnotationsSelected").prop("disabled", true);
            $("#vvPrintOptionsAnnotationsCheckbox").addClass("vvPrintAnnotationsDisabled")
        } else {
            $("#vvPrintAnnotationsSelected").prop("disabled", false);
            $("#vvPrintOptionsAnnotationsCheckbox").removeClass("vvPrintAnnotationsDisabled");
            $("#vvPrintAnnotationsSelected").prop("checked", vvConfig.printBurnAnnotations);
            if (vvConfig.printBurnAnnotations) {
                $("#vvPrintAnnotationsTypeCheckboxes").removeClass("vvPrintAnnotationsDisabled");
                $("#vvPrintAnnotationsTypeText").prop("disabled", false);
                $("#vvPrintAnnotationsTypeNonText").prop("disabled", false)
            } else {
                $("#vvPrintAnnotationsTypeCheckboxes").addClass("vvPrintAnnotationsDisabled");
                $("#vvPrintAnnotationsTypeText").prop("disabled", true);
                $("#vvPrintAnnotationsTypeNonText").prop("disabled", true)
            }
        }
        $("#vvLocalPrintDialog").dialog("open")
    };
    VirtualViewer.prototype.printDocumentCore = function (Cz) {
        var C5 = parseInt($("#vvPrintFirstPage").val(), 10) - 1;
        var DH = parseInt($("#vvPrintLastPage").val(), 10) - 1;
        var DG = true;
        var C4 = A1.getCurrentState();
        if (vvConfig.enableEnhancedLocalPrintingBeta) {
            if (BrowserDetect.browser === "Firefox") {
                var DF = $("#vvPrintAnnotationsTypeText").is(":checked");
                var DC = $("#vvPrintAnnotationsTypeNonText").is(":checked");
                var DJ = M(C4, "pages", C5 + "-" + DH, DF, DC);
                var C7 = "";
                C7 += "<embed type='application/pdf' id='printEmbed' width='100%' height='100%' src='";
                C7 += DJ.toString();
                C7 += "'>";
                Cz.jQuery("#printBody").html(C7);
                return
            }
        }
        if ($("#vvPrintColorSelected:checked").val()) {
            DG = false
        }
        var DD = "";
        if ((C5 < 0) || (DH > (CB - 1))) {
            alert("Page range out of bounds");
            return
        }
        for (var DA = C5; DA < DH + 1; DA += 1) {
            var C6 = new URI(vvConfig.servletPath);
            C6.addQuery("action", "getImage");
            C6.addQuery("documentId", CH(DA, C4));
            C6.addQuery("clientInstanceId", virtualViewer.getClientInstanceId());
            C6.addQuery("pageNumber", CY(DA));
            C6.addQuery("printSession", true);
            C6.addQuery("grayScale", DG);
            C6.addQuery("rotateAngle", C4.getRotationForPage(DA));
            C6.addQuery("invertImage", C4.getInvertImageForPage(DA));
            if (A1.getOverlayPath() !== null) {
                C6.addQuery("overlayPath", A1.getOverlayPath())
            }
            if (vvDefines.cacheBuster === true) {
                C6.addQuery("cacheBuster", Math.random())
            }
            if (vvConfig.printBurnAnnotations === true) {
                var C2 = virtualViewer.getDocumentModel();
                if (C2) {
                    var C8 = C2.model.layerManager.layers.length;
                    for (var DE = 0; DE < C8; DE++) {
                        var DI = unescape(C2.model.layerManager.layers[DE].annotationId);
                        var C1 = C2.model.pageData[DA].annotationHash;
                        var C0 = false;
                        if (C1) {
                            var C3 = C1[DI];
                            if (C3) {
                                C0 = C3.annExists
                            }
                        }
                        if (C0) {
                            C6.addQuery("burnLayer", DI)
                        }
                    }
                }
                if (vvConfig.printShowTypeToggles) {
                    var DB = vvDefines.annotationTypes;
                    if ($("#vvPrintAnnotationsTypeText").is(":checked")) {
                        C6.addQuery("burnType", DB.SANN_POSTIT);
                        C6.addQuery("burnType", DB.SANN_EDIT);
                        C6.addQuery("burnType", DB.SANN_CLOUD_EDIT)
                    }
                    if ($("#vvPrintAnnotationsTypeNonText").is(":checked")) {
                        C6.addQuery("burnType", DB.SANN_FILLED_RECT);
                        C6.addQuery("burnType", DB.SANN_HIGHLIGHT_RECT);
                        C6.addQuery("burnType", DB.SANN_RECTANGLE);
                        C6.addQuery("burnType", DB.SANN_LINE);
                        C6.addQuery("burnType", DB.SANN_ELLIPSE);
                        C6.addQuery("burnType", DB.SANN_FILLED_ELLIPSE);
                        C6.addQuery("burnType", DB.SANN_FREEHAND);
                        C6.addQuery("burnType", DB.SANN_BITMAP);
                        C6.addQuery("burnType", DB.SANN_POLYGON);
                        C6.addQuery("burnType", DB.SANN_FILLED_POLYGON);
                        C6.addQuery("burnType", DB.SANN_ARROW);
                        C6.addQuery("burnType", DB.SANN_TRANSPARENT_BITMAP);
                        C6.addQuery("burnType", DB.SANN_BUBBLE);
                        C6.addQuery("burnType", DB.SANN_CUSTOM_STAMP);
                        C6.addQuery("burnType", DB.SANN_CIRCLE)
                    }
                }
            }
            var C9 = "";
            C9 += '<p class="breakhere"><img src="';
            C9 += C6.toString();
            C9 += '" ';
            C9 += " /></p>";
            DD += C9
        }
        Cz.jQuery("#printBody").html(DD)
    };
    var AF = function (C0) {
        var C2 = $("#vvImageCanvas")[0];
        var DA = C2.getContext("2d");
        DA.save();
        if (!C0 || C0.getDelete()) {
            return
        }
        var C3 = C0.getBoundingBox();
        var C6 = C0.getPointArray();
        var C9 = null;
        var C7 = null;
        if (C6) {
            C9 = C6[0];
            C7 = C6[1]
        }
        if (((C0.getType() === vvDefines.annotationTypes.SANN_ARROW) || (C0.getType() === vvDefines.annotationTypes.SANN_LINE)) && (C6.length === 2)) {
            Cu(C0, C9.getX() * C0.getWidthRatio(), C9.getY() * C0.getHeightRatio());
            Cu(C0, C7.getX() * C0.getWidthRatio(), C7.getY() * C0.getHeightRatio())
        } else {
            var C5 = C3.getX1() * C0.getWidthRatio();
            var C4 = C3.getY1() * C0.getHeightRatio();
            var C1 = C3.getWidth() * C0.getWidthRatio();
            var C8 = C3.getHeight() * C0.getHeightRatio();
            if ((C0.getType() === vvDefines.annotationTypes.SANN_EDIT) || (C0.getType() === vvDefines.annotationTypes.SANN_BUBBLE) || (C0.getType() === vvDefines.annotationTypes.SANN_CLOUD_EDIT) || (C0.getType() === vvDefines.annotationTypes.SANN_POSTIT)) {
                var Cz = getTextAnnDOMId(C0);
                $("#" + Cz).addClass("vvAnnSelected")
            } else {
                DA.lineWidth = 1;
                DA.strokeStyle = "rgb(0,0,0)";
                DA.strokeRect(C5, C4, C1, C8)
            }
            if (!C0.collapsed) {
                Cu(C0, C5, C4);
                Cu(C0, C5 + C1, C4);
                Cu(C0, C5, C4 + C8);
                Cu(C0, C5 + C1, C4 + C8)
            }
        }
        DA.restore();
        AT(vvDefines.dragModes.moveAnnotation)
    };
    var Cu = function (C4, Cz, C5) {
        var C1 = $("#vvImageCanvas")[0];
        var C0 = C1.getContext("2d");
        var C3 = virtualViewer.getNubSize();
        var C2 = C3 / 2;
        C0.fillStyle = vvConfig.polygonNubFillColor;
        C0.fillRect(Cz - C2, C5 - C2, C3, C3)
    };
    VirtualViewer.prototype.createColorGrid = function (C0, C5) {
        var C1 = document.createElement("div");
        C1.id = C0 + "Chooser";
        var Cz = 0;
        var C7 = 0;
        var C6 = null;
        for (var C4 = 0; C4 < vvDefines.annColors.length; C4 += 1) {
            if (((C7) % vvDefines.annColorRowSize) === 0) {
                C7 = 0;
                Cz += 1;
                C6 = document.createElement("div");
                C6.id = C0 + "ChooserRow" + Cz;
                C1.appendChild(C6);
                if (Cz > 1) {
                    $(C6).addClass(C0 + "SecondaryRow")
                }
            }
            var C3 = document.createElement("div");
            C3.id = C0 + C4;
            $(C3).addClass(C0 + "ChooserCellUnselected");
            C3.style.backgroundColor = "#" + vvDefines.annColors[C4];
            C3.style.width = A1.getColorBlobSize() + "px";
            C3.style.height = A1.getColorBlobSize() + "px";
            C3.style.left = ((A1.getColorBlobSize() + 5) * C7++) + "px";
            C6.appendChild(C3);
            var C2 = function () {
                var C8 = C4;
                C3.onclick = function () {
                    AD(C0, C8)
                }
            };
            C2()
        }
        return C1
    };
    var O = function (C1, C5, C0, C4) {
        var C2 = $("." + C1 + "ChooserCellSelected");
        C2.addClass(C1 + "ChooserCellUnselected");
        C2.removeClass(C1 + "ChooserCellSelected");
        C2.css("width", A1.getColorBlobSize() + "px");
        C2.css("height", A1.getColorBlobSize() + "px");
        var C3 = A1.getCurrentAnnObject();
        var Cz = vvDefines.annColors[C0];
        if (C1 && C5) {
            $(C5).addClass(C1 + "ChooserCellSelected");
            C5.style.width = (A1.getColorBlobSize() - 2) + "px";
            C5.style.height = (A1.getColorBlobSize() - 2) + "px"
        } else {
            Cz = C4
        }
        if (C3) {
            if (C3.getFontColor()) {
                C3.setFontColor(Cz)
            } else {
                if (C3.getFillColor()) {
                    C3.setFillColor(Cz)
                } else {
                    if (C3.getLineColor()) {
                        C3.setLineColor(Cz)
                    }
                }
            }
        }
    };
    var AD = function (C1, C4) {
        var C3 = A1.getCurrentAnnObject();
        var C2 = A1.getCurrentState();
        var C0 = myPainter.getLayerWithAnn(C3);
        var Cz = C2.getUndoObject(C2.getPageNumber());
        Cz.referenceAnn = C3;
        Cz.unchangedAnn = C3.clone();
        Cz.layer = C0;
        if (C3.getFontColor()) {
            currentColor = C3.getFontColor();
            C3.setFontColor(vvDefines.annColors[C4])
        } else {
            if (C3.getFillColor()) {
                currentColor = C3.getFillColor();
                C3.setFillColor(vvDefines.annColors[C4])
            } else {
                if (C3.getLineColor()) {
                    currentColor = C3.getLineColor();
                    C3.setLineColor(vvDefines.annColors[C4])
                }
            }
        }
        tempOption = document.getElementById(C1 + C4);
        O(C1, tempOption, C4);
        B2();
        Cz.modifiedAnn = C3.clone();
        Cz.type = "modify";
        if (!(Cz.modifiedAnn).isSameAs(Cz.unchangedAnn)) {
            C2.pushUndoObject(C2.getPageNumber(), Cz)
        }
        C2.resetUndoObject();
        myPainter.markLayerWithAnnDirty(C3);
        virtualViewer.paintCanvases()
    };
    var B2 = function () {
        var C1 = document.getElementById("fillColorChooser");
        var Cz = document.getElementById("lineColorChooser");
        var C0 = document.getElementById("fontColorChooser");
        if (C1) {
            C1.style.visibility = "hidden"
        }
        if (Cz) {
            Cz.style.visibility = "hidden"
        }
        if (C0) {
            C0.style.visibility = "hidden"
        }
    };
    VirtualViewer.prototype.addAnnotation = function (Cz) {
        $("#vvOuterDiv").css("cursor", "crosshair");
        c = Cz;
        A1.currentPointArray = [];
        BB = 0;
        $("#vvAnnPopUp").hide();
        cursorNormal();
        var C0 = A1.getCurrentState();
        if (!C0.getLayersForPage()) {
            myPainter.createDefaultLayer(C0)
        }
        if (vvConfig.stickyAnnButtons) {
            if ((Cz === "Rubber Stamp") || (Cz === "Bitmap")) {
                AT(vvDefines.dragModes.annotate)
            } else {
                if (CE === false) {
                    CE = true;
                    Aw = Cz;
                    AT(vvDefines.dragModes.annotate)
                } else {
                    if (Aw === Cz) {
                        if (CE === true) {
                            CE = false;
                            $("#vvOuterDiv").css("cursor", "default")
                        }
                        AT(vvDefines.dragModes.pan)
                    } else {
                        Aw = Cz;
                        AT(vvDefines.dragModes.pan);
                        AT(vvDefines.dragModes.annotate)
                    }
                }
            }
        } else {
            AT(vvDefines.dragModes.annotate)
        }
    };
    VirtualViewer.prototype.toggleLayerManager = function () {
        var Cz = $("#vvLayerManagerDiv");
        if (!Cz.data("vv-position-set")) {
            Cz.position({of: $("#vvLayerManagerButton"), my: "left center", at: "right center"});
            Cz.css("left", 41);
            Cz.data("vv-position-set", "true")
        }
        Cz.toggle()
    };
    VirtualViewer.prototype.sendDocument = function (splitParam) {
        var Cz = new URI(vvConfig.servletPath);
        Cz.addQuery("action", "sendDocument");

        // The document name, and the id and type of the parent node are needed by the backend Java split functionality
        var parentWindowUri = new URI(document.referrer);
        var parentNodeArgs = parentWindowUri.query();

        // The url arguments are passed along with the document id/user/ticket to the backend
        var argsForSnowBackend = A1.getDocumentId() + ((parentNodeArgs) ? ("&" + parentNodeArgs) : "");
        argsForSnowBackend += "&splitDocument=" + ((splitParam) ? splitParam : "");
        Cz.addQuery("documentId", argsForSnowBackend);

        Cz.addQuery("clientInstanceId", virtualViewer.getClientInstanceId());
        Cz.addQuery("pageCount", virtualViewer.getPageCount());
        Cz.addQuery("withAnnotations", vvConfig.sendDocumentWithAnnotations);
        Cz.addQuery("stampStatus", "");
        if (A1.getOverlayPath() !== null) {
            Cz.addQuery("overlayPath", A1.getOverlayPath())
        }
        if (vvDefines.cacheBuster === true) {
            Cz.addQuery("cacheBuster", Math.random())
        }
        var C0 = Cz.query();
        Cz.query("");
        $.ajax({
            url: Cz.toString(), type: "POST", data: C0, dataType: "json", success: function (C1) {
                if (AB(C1) === true) {
                    return
                }
            }, error: function (C1) {
                if (AB(C1) === true) {
                    return
                }
            }
        })
    };
    var A4 = function () {
        $("#vvJumpToPageInput").val(CF + 1);
        if (CB !== 0) {
            $("#vvJumpToPageTotal").html(CB)
        }
    };
    var x = function (C4, C1, C3, Cz) {
        var C2 = document.getElementById(C4);
        var C0 = parseFloat(C2.style.opacity);
        if (Cz > 0) {
            if (C0 < C1) {
                C2.style.opacity = C0 + Cz;
                C2.style.filter = "alpha(opacity=" + ((C0 + Cz) * 100) + ");";
                setTimeout(function () {
                    x(C4, C1, C3, Cz)
                }, C3)
            }
        } else {
            if (C0 > C1) {
                C2.style.opacity = C0 + Cz;
                C2.style.filter = "alpha(opacity=" + ((C0 + Cz) * 100) + ");";
                setTimeout(function () {
                    x(C4, C1, C3, Cz)
                }, C3)
            } else {
                C2.style.visibility = "hidden"
            }
        }
    };
    var S = function (Cz) {
        var C2 = 0;
        var C1;
        var C0;
        if (window.event) {
            C2 = Cz.keyCode
        } else {
            if (Cz.which) {
                C2 = Cz.which
            }
        }
        C1 = String.fromCharCode(C2);
        C0 = /\r/;
        return C0.test(C1)
    };
    var An = function (Cz) {
        if ((Cz.length < 6) || (Cz.length > 7)) {
            return null
        }
        if (Cz.charAt(0) === "#") {
            Cz = Cz.substr(1, Cz.length)
        }
        if (Cz.match(/^[0-9a-fA-F]+$/)) {
            return Cz
        } else {
            return null
        }
    };
    VirtualViewer.prototype.getPageNumber = function () {
        return CF
    };
    VirtualViewer.prototype.getOriginalWidth = function () {
        return Cr
    };
    VirtualViewer.prototype.setOriginalWidth = function (Cz) {
        Cr = Cz
    };
    VirtualViewer.prototype.getOriginalHeight = function () {
        return B9
    };
    VirtualViewer.prototype.setOriginalHeight = function (Cz) {
        B9 = Cz
    };
    VirtualViewer.prototype.getDocumentModel = function () {
        var Cz = A1.getStateForDocumentId(A1.getDocumentId());
        if (Cz) {
            return Cz.getDocumentModel()
        } else {
            return null
        }
    };
    VirtualViewer.prototype.setDocumentModel = function (C0) {
        var Cz = A1.getStateForDocumentId(A1.getDocumentId());
        Cz.setDocumentModel(C0)
    };
    VirtualViewer.prototype.getZoomPercent = function () {
        return $("#vvDummyScroller").height() / virtualViewer.getOriginalHeight()
    };
    VirtualViewer.prototype.getCurrentAnnObject = function () {
        return A1.currentAnnObject
    };
    VirtualViewer.prototype.setCurrentAnnObject = function (Cz) {
        A1.currentAnnObject = Cz
    };
    VirtualViewer.prototype.collapseAllStickyNotes = function (C5, C1) {
        var C4 = A1.getCurrentState().getLayerNames();
        var C3 = A1.getCurrentState().getLayersForPage(A1.getPageNumber());
        if (C4 && C3) {
            for (var C0 = 0; C0 < C4.length; C0 += 1) {
                var C2 = C3[C4[C0]];
                var Cz = C2.anns;
                for (var C6 = 0; C6 < Cz.length; C6 += 1) {
                    ann = Cz[C6];
                    if (ann.getType() === vvDefines.annotationTypes.SANN_POSTIT) {
                        ann.collapsed = C5;
                        ann.editing = false
                    }
                }
            }
        }
        AT(vvDefines.dragModes.pan);
        if (C1) {
            virtualViewer.clearCanvases();
            virtualViewer.paintCanvases()
        }
    };
    var CO = function (C1) {
        if (vvConfig.collapseStickiesByDefault) {
            A1.collapseAllStickyNotes(true)
        }
        if (vvConfig.retainViewOptionsBetweenPages === false) {
            var C3 = A1.getCurrentState();
            C3.setZoomModeForPage(C1, vvConfig.defaultZoomMode);
            L();
            C3.setRotationForPage(C1, 0);
            H = false;
            t = false;
            p = false;
            i = 0;
            Ao = 0;
            A1.zoomOffsetTopVal = 0;
            A1.zoomOffsetLeftVal = 0;
            A1.zoomToOffset = false
        } else {
            var C2 = A1.getStateForDocumentId(A1.getDocumentId());
            if (C2) {
                if ((C1 !== undefined) && (C1 !== null)) {
                    w(C1)
                }
                H = C2.getHorizontalFlipForPage(CF);
                H = C2.getVerticalFlipForPage(CF);
                p = C2.getInvertImageForPage(CF);
                var Cz = C2.getScrollTopForPage(CF);
                var C0 = C2.getScrollLeftForPage(CF);
                Cr = C2.getOriginalWidthForPage(CF);
                B9 = C2.getOriginalHeightForPage(CF);
                if (Cz) {
                    A1.zoomOffsetTopVal = Cz;
                    A1.zoomToOffset = true
                } else {
                    A1.zoomToOffset = false
                }
                if (C0) {
                    A1.zoomOffsetLeftVal = C0;
                    A1.zoomToOffset = true
                } else {
                    A1.zoomToOffset = false
                }
                L()
            }
        }
    };
    var w = function (Cz) {
        var C0 = A1.getStateForDocumentId(A1.getDocumentId());
        if (C0) {
            if (vvConfig.defaultZoomMode !== vvDefines.zoomModes.fitLast) {
            } else {
                C0.setZoomModeForPage(Cz, vvDefines.zoomModes.fitLast)
            }
            C0.setHorizontalFlipForPage(Cz, H);
            C0.setVerticalFlipForPage(Cz, t);
            C0.setInvertImageForPage(Cz, p);
            if (vvDefines.autoSaveAnnotations === true) {
                A1.saveDocument()
            }
            C0.setOriginalWidthForPage(Cz, Cr);
            C0.setOriginalHeightForPage(Cz, B9);
            var C1 = document.getElementById("vvOuterDiv");
            C0 = A1.openDocuments[A1.getDocumentId()];
            if (A1.zoomToOffset === true) {
                C0.setScrollLeftForPage(Cz, A1.zoomOffsetLeftVal);
                C0.setScrollTopForPage(Cz, A1.zoomOffsetTopVal)
            } else {
                C0.setScrollLeftForPage(Cz, C1.scrollLeft);
                C0.setScrollTopForPage(Cz, C1.scrollTop)
            }
        }
    };
    VirtualViewer.prototype.initToolbar = function () {
        $("#vvToolbar").children().on("selectstart", function () {
            return false
        });
        $("#vvAnnToolbar").children().on("selectstart", function () {
            return false
        });
        $(".vvToolbarButton").mousedown(function () {
            $(this).addClass("mouseDown")
        });
        $(".vvToolbarButton").mouseup(function () {
            $(this).removeClass("mouseDown")
        });
        $(".vvToolbarButton").mouseleave(function () {
            $(this).removeClass("mouseDown")
        });
        $(".vvToolbarButton").each(function () {
            $(this).removeClass("mouseDown")
        });
        if (vvConfig.stickyAnnButtons) {
            $("#vvAnnStickyNoteButton").mouseup(function () {
                if (!$(this).hasClass("stickyPress")) {
                    $(".vvToolbarButton").each(function () {
                        $(this).removeClass("stickyPress")
                    });
                    $(this).addClass("stickyPress")
                } else {
                    $(this).removeClass("stickyPress")
                }
            });
            $("#vvAnnTextButton").mouseup(function () {
                if (!$(this).hasClass("stickyPress")) {
                    $(".vvToolbarButton").each(function () {
                        $(this).removeClass("stickyPress")
                    })
                } else {
                    if (vvConfig.enableTextRubberStamp === true) {
                        $(this).removeClass("stickyPress");
                        CE = false;
                        AT(vvDefines.dragModes.pan);
                        $("#vvOuterDiv").css("cursor", "default")
                    }
                }
            });
            $("#vvAnnImageRubberStampButton").mouseup(function () {
                if (!$(this).hasClass("stickyPress")) {
                    $(".vvToolbarButton").each(function () {
                        $(this).removeClass("stickyPress")
                    })
                } else {
                    $(this).removeClass("stickyPress");
                    CE = false;
                    AT(vvDefines.dragModes.pan);
                    $("#vvOuterDiv").css("cursor", "default")
                }
            });
            $("#vvAnnLineButton").mouseup(function () {
                if (!$(this).hasClass("stickyPress")) {
                    $(".vvToolbarButton").each(function () {
                        $(this).removeClass("stickyPress")
                    });
                    $(this).addClass("stickyPress")
                } else {
                    $(this).removeClass("stickyPress")
                }
            });
            $("#vvAnnArrowButton").mouseup(function () {
                if (!$(this).hasClass("stickyPress")) {
                    $(".vvToolbarButton").each(function () {
                        $(this).removeClass("stickyPress")
                    });
                    $(this).addClass("stickyPress")
                } else {
                    $(this).removeClass("stickyPress")
                }
            });
            $("#vvAnnFreehandButton").mouseup(function () {
                if (!$(this).hasClass("stickyPress")) {
                    $(".vvToolbarButton").each(function () {
                        $(this).removeClass("stickyPress")
                    });
                    $(this).addClass("stickyPress")
                } else {
                    $(this).removeClass("stickyPress")
                }
            });
            $("#vvAnnHighlightRectButton").mouseup(function () {
                if (!$(this).hasClass("stickyPress")) {
                    $(".vvToolbarButton").each(function () {
                        $(this).removeClass("stickyPress")
                    });
                    $(this).addClass("stickyPress")
                } else {
                    $(this).removeClass("stickyPress")
                }
            });
            $("#vvAnnFilledRectButton").mouseup(function () {
                if (!$(this).hasClass("stickyPress")) {
                    $(".vvToolbarButton").each(function () {
                        $(this).removeClass("stickyPress")
                    });
                    $(this).addClass("stickyPress")
                } else {
                    $(this).removeClass("stickyPress")
                }
            });
            $("#vvAnnFilledEllipseButton").mouseup(function () {
                if (!$(this).hasClass("stickyPress")) {
                    $(".vvToolbarButton").each(function () {
                        $(this).removeClass("stickyPress")
                    });
                    $(this).addClass("stickyPress")
                } else {
                    $(this).removeClass("stickyPress")
                }
            });
            $("#vvAnnFilledPolygonButton").mouseup(function () {
                if (!$(this).hasClass("stickyPress")) {
                    $(".vvToolbarButton").each(function () {
                        $(this).removeClass("stickyPress")
                    });
                    $(this).addClass("stickyPress")
                } else {
                    $(this).removeClass("stickyPress")
                }
            });
            $("#vvAnnRectButton").mouseup(function () {
                if (!$(this).hasClass("stickyPress")) {
                    $(".vvToolbarButton").each(function () {
                        $(this).removeClass("stickyPress")
                    });
                    $(this).addClass("stickyPress")
                } else {
                    $(this).removeClass("stickyPress")
                }
            });
            $("#vvAnnEllipseButton").mouseup(function () {
                if (!$(this).hasClass("stickyPress")) {
                    $(".vvToolbarButton").each(function () {
                        $(this).removeClass("stickyPress")
                    });
                    $(this).addClass("stickyPress")
                } else {
                    $(this).removeClass("stickyPress")
                }
            });
            $("#vvAnnPolygonButton").mouseup(function () {
                if (!$(this).hasClass("stickyPress")) {
                    $(".vvToolbarButton").each(function () {
                        $(this).removeClass("stickyPress")
                    });
                    $(this).addClass("stickyPress")
                } else {
                    $(this).removeClass("stickyPress")
                }
            })
        }
    };
    VirtualViewer.prototype.zoomRubberband = function () {
        c = "Rectangle";
        A1.currentPointArray = [];
        BB = 0;
        AT(vvDefines.dragModes.zoom)
    };
    VirtualViewer.prototype.getActiveTab = function () {
        return this.activeTab
    };
    var n = function (C0, Cz) {
        var C1 = null;
        if (C0) {
            $("#vvDocThumbsInternal").remove();
            C1 = document.createElement("table");
            C1.id = "vvDocThumbsInternal";
            $("#vvDocThumbs").append(C1)
        } else {
            if (Cz) {
                $("#vvSearchInternal").remove();
                C1 = document.createElement("table");
                C1.id = "vvSearchInternal";
                $("#vvSearchResults").append(C1)
            } else {
                $("#vvPageThumbsInternal").remove();
                C1 = document.createElement("table");
                C1.id = "vvPageThumbsInternal";
                $("#vvPageThumbs").append(C1)
            }
        }
    };
    var Bo = {};
    VirtualViewer.prototype.copySelection = function (C2) {
        if (A1.checkPageManipulationsEnabled(C2)) {
            var Cz = virtualViewer.getDocumentModel();
            var C1 = virtualViewer.getStateForDocumentId(A1.getDocumentId());
            var C0 = Cz.getSelectedPageNumbers();
            if (C0.length === 0) {
                return false
            }
            Bo.state = C1.copyPages(C0, 0);
            Bo.layerNames = C1.getLayerNames();
            Bo.model = Cz.copySelection();
            return true
        } else {
            return false
        }
    };
    VirtualViewer.prototype.cutSelection = function (C8, C1) {
        if (A1.checkPageManipulationsEnabled(C1)) {
            var C2 = virtualViewer.getDocumentModel();
            var Cz = virtualViewer.getStateForDocumentId(A1.getDocumentId());
            var C3 = C2.getSelectedPageNumbers();
            if (C3.length === 0) {
                return false
            }
            var C7 = 0;
            for (var C4 = 0; C4 < C2.selection.length; C4 += 1) {
                if (C2.selection[C4] === true) {
                    $("#vvPageThumbsInternal")[0].deleteRow(C4 - C7);
                    C7++
                }
            }
            if (C8 !== true) {
                Bo.model = C2.cutSelection();
                Bo.layerNames = Cz.getLayerNames();
                Bo.state = Cz.cutPages(C3, 0)
            } else {
                C2.cutSelection();
                Cz.cutPages(C3, 0)
            }
            CB = C2.getDocumentLength();
            var C5 = 0;
            $("#vvPageThumbsInternal").find("td").each(function () {
                $(this).data("pageNumber", C5);
                $(this).find(".thumbBox")[0].title = "Page " + (C5 + 1);
                if (!Modernizr.touch) {
                    $(this).find(".thumbBox").tooltip({show: false, hide: false})
                }
                C5 += 1
            });
            Cr = 0;
            B9 = 0;
            if (CB === 0) {
                var C0 = document.getElementById("vvImageCanvas");
                var C6 = C0.getContext("2d");
                C6.clearRect(0, 0, C0.width, C0.height)
            } else {
                if (C3[0] === CB) {
                    virtualViewer.setPage(CB - 1)
                } else {
                    virtualViewer.setPage(C3[0])
                }
            }
            if (CB > 0) {
                virtualViewer.requestAnnotations(Cz);
                virtualViewer.loadVisibleThumbs(null, Cz, false)
            }
            return true
        } else {
            return false
        }
    };
    VirtualViewer.prototype.pasteSelection = function (C3, C0, C2) {
        if (A1.checkPageManipulationsEnabled(C2)) {
            var Cz = virtualViewer.getDocumentModel();
            var C1 = virtualViewer.getStateForDocumentId(A1.getDocumentId());
            if (!C3) {
                C3 = 0
            }
            Cz.paste(Bo.model, C3);
            C1.pastePages(Bo.state, C3);
            C1.setLayerNames(Bo.layerNames);
            CB = Cz.getDocumentLength();
            C1.setPageCount(CB);
            virtualViewer.requestAnnotations(C1);
            n(false, false);
            AO(C1, false, false);
            if (!C0) {
                virtualViewer.loadVisibleThumbs(null, C1, false);
                virtualViewer.setPage(C3)
            }
        }
    };
    VirtualViewer.prototype.selectPage = function (Cz, C1) {
        if (A1.checkPageManipulationsEnabled(C1)) {
            var C0 = A1.getDocumentModel();
            C0.addPageToSelection(Cz)
        }
    };
    VirtualViewer.prototype.deselectPage = function (Cz, C1) {
        if (A1.checkPageManipulationsEnabled(C1)) {
            var C0 = A1.getDocumentModel();
            C0.removePageFromSelection(Cz)
        }
    };
    VirtualViewer.prototype.selectRange = function (Cz, C1) {
        if (A1.checkPageManipulationsEnabled(C1)) {
            var C0 = A1.getDocumentModel();
            C0.selectRange(Cz)
        }
    };
    VirtualViewer.prototype.selectAll = function (C0) {
        if (A1.checkPageManipulationsEnabled(C0)) {
            var Cz = A1.getDocumentModel();
            Cz.selectAll()
        }
    };
    VirtualViewer.prototype.selectNone = function (C0) {
        if (A1.checkPageManipulationsEnabled(C0)) {
            var Cz = A1.getDocumentModel();
            Cz.clearPageSelection()
        }
    };
    VirtualViewer.prototype.isPageSelected = function (Cz, C1) {
        if (A1.checkPageManipulationsEnabled(C1)) {
            var C0 = A1.getDocumentModel();
            return C0.isPageSelected(Cz)
        }
    };
    VirtualViewer.prototype.getSelectedPageNumbers = function (C0) {
        if (A1.checkPageManipulationsEnabled(C0)) {
            var Cz = A1.getDocumentModel();
            return Cz.getSelectedPageNumbers()
        }
    };
    VirtualViewer.prototype.showAboutDialog = function () {
        $("#vvAboutDialog").dialog("open")
    };
    VirtualViewer.prototype.showEmailDialog = function () {
        $("#vvEmailDocumentName").html(A1.getDisplayName());
        $("#vvEmailFrom").val(vvConfig.emailDefaults.prepopulateFrom);
        $("#vvEmailTo").val(vvConfig.emailDefaults.prepopulateTo);
        $("#vvEmailCC").val(vvConfig.emailDefaults.prepopulateCC);
        $("#vvEmailBCC").val(vvConfig.emailDefaults.prepopulateBCC);
        $("#vvEmailSubject").val(vvConfig.emailDefaults.prepopulateSubject);
        $("#vvEmailBody").val(vvConfig.emailDefaults.prepopulateBody);
        $("#vvEmailDialog").dialog("open")
    };
    VirtualViewer.prototype.setProperty = function (C1, Cz) {
        var C0 = A1.getCurrentState();
        C0.setDocumentModelProperty(C1, Cz)
    };
    VirtualViewer.prototype.getProperty = function (C0) {
        var Cz = A1.getCurrentState();
        return Cz.getDocumentModelProperty(C0)
    };
    function B0(Cz) {
        var C1 = ["documentId", "documentDisplayName", "documentByteSize", "pageCount", "documentFormat"];
        var C0 = $.inArray(Cz, C1) > -1;
        return C0
    }

    function z(C5, Cz) {
        var C0;
        var C4;
        if (C5 === "pageCount") {
            return Cz.getPageCount()
        }
        if (C5 === "documentDisplayName") {
            return Cz.getDisplayName()
        }
        if (C5 === "documentByteSize") {
            var C1 = Cz.getDocumentByteSize();
            if (C1) {
                C1 = C1.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",")
            }
            return C1
        }
        if (C5 === "documentId") {
            return Cz.getDocumentId()
        }
        if (C5 === "dpi") {
            return Cz.getDPIForPage(virtualViewer.getPageNumber())
        }
        if (C5 === "compressionType") {
            var C7 = Cz.getCompressionTypeForPage(virtualViewer.getPageNumber());
            if (!C7) {
                C7 = "N/A"
            }
            return C7
        }
        if (C5 === "bitDepth") {
            return Cz.getBitDepthForPage(virtualViewer.getPageNumber())
        }
        if (C5 === "pageNumber") {
            return virtualViewer.getPageNumber() + 1
        }
        if (C5 === "documentFormat") {
            return Cz.getFormatForPage(virtualViewer.getPageNumber())
        }
        if (C5.indexOf("tiffTag") === 0) {
            var C6 = Cz.getTiffTag(virtualViewer.getPageNumber(), C5);
            if (!C6) {
                return "N/A"
            }
            return Am(C5, C6)
        }
        if (C5 === "imageSizePixels") {
            C0 = Cz.getOriginalWidthForPage(virtualViewer.getPageNumber());
            C4 = Cz.getOriginalHeightForPage(virtualViewer.getPageNumber());
            return "" + C0 + " x " + C4
        }
        if (C5 === "imageSizeInches") {
            var C3 = Cz.getDPIForPage(virtualViewer.getPageNumber());
            C0 = Cz.getOriginalWidthForPage(virtualViewer.getPageNumber());
            C4 = Cz.getOriginalHeightForPage(virtualViewer.getPageNumber());
            var C2 = C0 / C3;
            var C8 = C4 / C3;
            return "" + C2.toFixed(2) + " x " + C8.toFixed(2) + " inches"
        }
        return undefined
    }

    function Am(C1, C2) {
        var Cz = vvDefines.tiffTagValueHash[C1];
        if (Cz) {
            var C0 = Cz[C2];
            if (C0) {
                return C0
            }
        }
        return C2
    }

    VirtualViewer.prototype.showImageInfo = function () {
        this.updateImageInfoDialog();
        $("#vvImageInfoDialog").dialog("open")
    };
    VirtualViewer.prototype.hideImageInfo = function () {
        $("#vvImageInfoDialog").dialog("close")
    };
    VirtualViewer.prototype.toggleImageInfo = function () {
        var Cz = $("#vvImageInfoDialog").dialog("isOpen");
        if (Cz) {
            this.hideImageInfo()
        } else {
            this.showImageInfo()
        }
    };
    function N(C1) {
        var C0 = vvConfig.maxInfoFieldLength;
        var Cz = "" + C1;
        if (Cz.length < C0) {
            return Cz
        } else {
            return jQuery.trim(Cz).substring(0, C0) + "..."
        }
    }

    VirtualViewer.prototype.updateImageInfoDialog = function () {
        if (!vvConfig.imageInfoFields) {
            return $("#vvImageInfoText").html("No Image Info Available")
        }
        var Cz = virtualViewer.getStateForDocumentId(A1.getDocumentId());
        var C3 = "<div class='vvImageInfoContents'>";
        C3 += "<div class='vvImageInfoHeader'>" + getLocalizedValue("imageInfo.documentInfoHeader", "Document Info") + "</div>";
        C3 += '<table class="vvImageInfoTable" width=100%>';
        var C4;
        var C7;
        var C6;
        var C0;
        var C5;
        var C8 = null;
        for (var C2 = 0; C2 < vvConfig.imageInfoFields.length; C2++) {
            C4 = vvConfig.imageInfoFields[C2].fieldId;
            C6 = vvConfig.imageInfoFields[C2].fieldCaption;
            C7 = getLocalizedValue("imageInfo." + C4, C6);
            if (C2 % 2 === 0) {
                C8 = "vvImageInfoOddRow"
            } else {
                C8 = "vvImageInfoEvenRow"
            }
            if (B0(C4)) {
                C3 += "<tr class='" + C8 + "'><td width=50%>";
                C0 = z(C4, Cz);
                if (!C0) {
                    C0 = "N/A"
                }
                C5 = N(C0);
                C3 += C7 + '</td><td title="' + C0 + '">' + C5;
                C3 += "</td></tr>"
            }
        }
        C3 += "<tr><td class='vvImageInfoPageHeader'><div class='vvImageInfoPageHeader'>" + getLocalizedValue("imageInfo.pageInfoHeader", "Page Info") + "</div></td></tr>";
        for (var C1 = 0; C1 < vvConfig.imageInfoFields.length; C1++) {
            C4 = vvConfig.imageInfoFields[C1].fieldId;
            C6 = vvConfig.imageInfoFields[C1].fieldCaption;
            C7 = getLocalizedValue("imageInfo." + C4, C6);
            if (C1 % 2 === 0) {
                C8 = "vvImageInfoOddRow"
            } else {
                C8 = "vvImageInfoEvenRow"
            }
            if (!B0(C4)) {
                C3 += "<tr class='" + C8 + "'><td width=50%>";
                C0 = z(C4, Cz);
                if (!C0) {
                    C0 = "N/A"
                }
                C5 = N(C0);
                C3 += C7 + '</td><td title="' + C0 + '">' + C5;
                C3 += "</td></tr>"
            }
        }
        C3 += "</table>";
        C3 += "</div>";
        $("#vvImageInfoText").html(C3)
    };
    VirtualViewer.prototype.toggleKeyboardHints = function () {
        var Cz = $("#vvKeyboardHints").dialog("isOpen");
        if (Cz) {
            this.hideKeyboardHints()
        } else {
            this.showKeyboardHints()
        }
    };
    VirtualViewer.prototype.hideKeyboardHints = function () {
        $("#vvKeyboardHints").dialog("close")
    };
    VirtualViewer.prototype.showKeyboardHints = function () {
        $("#vvKeyboardHints").dialog("open")
    };
    VirtualViewer.prototype.pageContainsAnnotations = function (Cz) {
        var C0 = A1.getDocumentModel();
        return C0.pageContainsAnnotations(Cz)
    };
    VirtualViewer.prototype.toggleThumbnailPanel = function (Cz) {
        var C0 = virtualViewer.getStateForDocumentId(A1.getDocumentId());
        if (Cz === undefined) {
            if ($("#vvImagePanel").hasClass("noThumbs")) {
                Cz = true
            } else {
                Cz = false
            }
        }
        if (Cz === true) {
            $("#vvImagePanel").removeClass("noThumbs");
            $("#vvThumbs").show();
            vvConfig.showThumbnailPanel = true;
            if (C0) {
                AO(C0, false);
                virtualViewer.loadVisibleThumbs(null, C0, false)
            }
            AO(C0, true);
            virtualViewer.loadVisibleThumbs(null, C0, true)
        } else {
            $("#vvImagePanel").addClass("noThumbs");
            $("#vvThumbs").hide();
            vvConfig.showThumbnailPanel = false;
            n(false);
            n(true)
        }
        if (C0) {
            Cv(true)
        }
    };
    VirtualViewer.prototype.setDocumentIdGenerator = function (Cz) {
        BC = Cz
    };
    VirtualViewer.prototype.setExportDocumentNameGenerator = function (Cz) {
        AW = Cz
    };
    VirtualViewer.prototype.setSaveDocumentCompletedHandler = function (Cz) {
        CI = Cz
    };
    VirtualViewer.prototype.getSaveDocumentCompletedHandler = function () {
        return CI
    };
    VirtualViewer.prototype.setSaveAnnotationsCompletedHandler = function (Cz) {
        A8 = Cz
    };
    VirtualViewer.prototype.getSaveAnnotationsCompletedHandler = function () {
        return A8
    };
    VirtualViewer.prototype.setImageLoadCompletedHandler = function (Cz) {
        Ca = Cz
    };
    VirtualViewer.prototype.getImageLoadCompletedHandler = function () {
        return Ca
    };
    VirtualViewer.prototype.clearSearchResults = function (Cz) {
        A1.searchCancel = false;
        A1.currentMatch = 0;
        n(false, true);
        if (!Cz) {
            Cz = virtualViewer.getStateForDocumentId(A1.getDocumentId())
        }
        if (Cz) {
            Cz.clearSearchResults()
        }
        AU();
        virtualViewer.paintCanvases()
    };
    VirtualViewer.prototype.getAllText = function () {
        var Cz = virtualViewer.getStateForDocumentId(A1.getDocumentId());
        if (Cz.getPageText(virtualViewer.getPageNumber()) === null) {
            A3(virtualViewer.getPageNumber())
        }
    };
    VirtualViewer.prototype.serverSidePrint = function () {
        return this.serverSidePrintCore(A1.getDocumentId())
    };
    VirtualViewer.prototype.showSearchField = function () {
        $("#vvThumbs").tabs("option", "active", 2)
    };
    VirtualViewer.prototype.searchText = function (C1, C4, C3) {
        var C2 = virtualViewer.getStateForDocumentId(A1.getDocumentId());
        if (!C1 || (C1 === "")) {
            return
        }
        A1.clearSearchResults(C2);
        $("#vvSearchTerm").hide();
        $("#vvSearchProgressText").text(C1);
        $("#vvSearchProgress").progressbar("value", 0);
        $("#vvSearchProgress").show();
        $("#vvSearchProgressCancel").css("visibility", "visible");
        var Cz = $("#vvSearchProgress").width();
        $("#vvSearchProgressText").width(Cz - 2);
        $("#vvSearchProgressCancel").position({my: "right", at: "right", of: "#vvSearchProgressText"});
        if ((!C4) || (C4 < 0)) {
            C4 = 0
        }
        if ((!C3) || (C3 > C2.getPageCount())) {
            C3 = C2.getPageCount()
        }
        var C0 = vvDefines.searchBatchSize;
        if (C0 > (C3 - C4)) {
            C0 = C3 - C4
        }
        Ck(C1, C4, C4 + C0, C3 + 1, vvConfig.searchCaseSensitive, false)
    };
    var Ck = function (C1, C2, C7, C4, C6, C3) {
        var Cz = A1.getStateForDocumentId(A1.getDocumentId());
        var C0 = new URI(vvConfig.servletPath);
        C0.escapeQuerySpace(false);
        C0.addQuery("action", "searchDocument");
        C0.addQuery("documentId", Cz.getDocumentId());
        C0.addQuery("clientInstanceId", A1.getClientInstanceId());
        C0.addQuery("pageCount", virtualViewer.getPageCount());
        if (A1.getOverlayPath() !== null) {
            C0.addQuery("overlayPath", A1.getOverlayPath())
        }
        if (vvDefines.cacheBuster === true) {
            C0.addQuery("cacheBuster", Math.random())
        }
        C0.addQuery("searchText", C1);
        if (C7 > Cz.getPageCount() - 1) {
            C7 = Cz.getPageCount() - 1
        }
        C0.addQuery("firstPage", C2);
        C0.addQuery("lastPage", C7);
        if (C6) {
            C0.addQuery("caseSensitive", "true")
        }
        if (C3) {
            C0.addQuery("exactMatch", "true")
        }
        if (Cp) {
            clearTimeout(Cp)
        }
        var C5 = C0.query();
        C0.query("");
        $.ajax({
            url: C0.toString(), dataType: "json", data: C5, success: function (C8) {
                Ba(C8, C1, C2, C7, C4, C6, C3)
            }, error: function (C8) {
                console.log("error")
            }
        })
    };
    var Ba = function (C6, C2, C5, DC, DA, DB, C7) {
        var Cz = A1.getCurrentState();
        if (A1.searchCancel) {
            return
        }
        Cz.addSearchResults(C6);
        AU();
        var C0 = C6[0].pageNumber;
        var C3 = C6[C6.length - 1].pageNumber;
        var C9 = Cz.getPageNumber();
        $("#vvSearchProgress").progressbar("value", (C3 / DA) * 100);
        AO(Cz, false, true, C0, C3 + 1);
        A1.loadVisibleThumbs(null, Cz, false, true);
        if ((C9 >= C0) && (C9 <= C3)) {
            A1.paintCanvases()
        }
        var C8 = vvDefines.searchBatchSize;
        var C4 = DC + 1;
        var C1 = DC + C8;
        if (C1 > DA) {
            C1 = DA - 2
        }
        if (!A1.searchCancel && (C1 >= C4)) {
            Cp = setTimeout(function (DD) {
                Ck(C2, C4, C1, DA, DB, C7)
            }, vvDefines.searchTimeout)
        } else {
            $("#vvSearchProgress").hide();
            $("#vvSearchProgressText").text("");
            $("#vvSearchProgress").progressbar("value", 0);
            $("#vvSearchTerm").show()
        }
    };
    VirtualViewer.prototype.cancelCurrentSearch = function () {
        A1.searchCancel = true;
        clearTimeout(Cp);
        $("#vvSearchProgress").hide();
        $("#vvSearchProgressText").text("");
        $("#vvSearchProgress").progressbar("value", 0);
        $("#vvSearchTerm").show()
    };
    VirtualViewer.prototype.isDocumentSearchable = function () {
        var Cz = A1.getStateForDocumentId(A1.getDocumentId());
        return Cz.getIsSearchable()
    };
    VirtualViewer.prototype.nextSearchResult = function () {
        var C5 = A1.getStateForDocumentId(A1.getDocumentId());
        var C1 = C5.getPageNumber();
        var C3 = C5.getPageCount();
        var C4 = C5.getSearchResultsForPage(C1);
        A1.currentMatch++;
        if ((!C4) || (A1.currentMatch >= C4.matches.length)) {
            A1.currentMatch = 0;
            var C0 = C5.getPageNumber() + 1;
            if (C0 < C3) {
                for (var Cz = C0; Cz < C3; Cz += 1) {
                    var C2 = C5.getSearchResultsForPage(Cz);
                    if (C2) {
                        A1.setPage(Cz);
                        break
                    }
                }
            }
        } else {
            A1.paintCanvases()
        }
        AU()
    };
    VirtualViewer.prototype.previousSearchResult = function () {
        var C2 = A1.getStateForDocumentId(A1.getDocumentId());
        A1.currentMatch--;
        if (A1.currentMatch < 0) {
            A1.currentMatch = 0;
            var C0 = C2.getPageNumber() - 1;
            if (C0 >= 0) {
                for (var Cz = C0; Cz >= 0; Cz -= 1) {
                    var C1 = C2.getSearchResultsForPage(Cz);
                    if (C1) {
                        A1.currentMatch = C1.matches.length - 1;
                        A1.setPage(Cz);
                        break
                    }
                }
            }
        } else {
            A1.paintCanvases()
        }
        AU()
    };
    VirtualViewer.prototype.enterGuideMode = function () {
        cursorGuides();
        AT(vvDefines.dragModes.guides)
    };
    VirtualViewer.prototype.enterPanMode = function () {
        AT(vvDefines.dragModes.pan)
    };
    VirtualViewer.prototype.enterSelectTextMode = function () {
        if (!this.pageHasText()) {
            alert(getLocalizedValue("selectText.noText", "No selectable text on this page"));
            return
        }
        cursorSelectText();
        AT(vvDefines.dragModes.selectText)
    };
    VirtualViewer.prototype.toggleSelectTextMode = function () {
        if (this.currentDragMode !== vvDefines.dragModes.selectText) {
            if (this.pageHasText()) {
                AT(vvDefines.dragModes.selectText)
            } else {
                alert(getLocalizedValue("selectText.noText", "No selectable text on this page"))
            }
        } else {
            AT(vvDefines.dragModes.pan)
        }
    };
    VirtualViewer.prototype.toggleHGuide = function () {
        this.toggleGuide(this.horizontalGuide, this.verticalGuide)
    };
    VirtualViewer.prototype.toggleVGuide = function (Cz, C0) {
        this.toggleGuide(this.verticalGuide, this.horizontalGuide)
    };
    VirtualViewer.prototype.toggleGuide = function (Cz, C1) {
        var C0 = this.currentDragMode;
        if (C0 !== vvDefines.dragModes.guides) {
            Cz.locked = false;
            Cz.visible = true
        } else {
            if (!C1.visible) {
                Cz.visible = !Cz.visible
            } else {
                C1.visible = false;
                Cz.visible = true
            }
        }
        C1.locked = false;
        Cz.locked = false;
        if (!C1.visible && !Cz.visible) {
            cursorNormal();
            AT(vvDefines.dragModes.pan)
        } else {
            AT(vvDefines.dragModes.guides)
        }
        A1.paintCanvases()
    };
    VirtualViewer.prototype.toggleCrosshairGuide = function () {
        var Cz = this.currentDragMode;
        AT(vvDefines.dragModes.guides);
        if (Cz === vvDefines.dragModes.guides) {
            var C0 = !this.verticalGuide.visible || !this.horizontalGuide.visible;
            this.verticalGuide.visible = this.horizontalGuide.visible = C0
        } else {
            this.verticalGuide.visible = this.horizontalGuide.visible = true
        }
        this.verticalGuide.locked = this.horizontalGuide.locked = false;
        if (!this.verticalGuide.visible && !this.horizontalGuide.visible) {
            cursorNormal();
            AT(vvDefines.dragModes.pan)
        }
        A1.paintCanvases()
    };
    VirtualViewer.prototype.toggleBothGuidesVisibility = function () {
        Av.visible = !Av.visible;
        Aq.visible = !Aq.visible;
        A1.paintCanvases()
    };
    VirtualViewer.prototype.toggleVGuideVisibility = function () {
        Av.visible = !Av.visible;
        A1.paintCanvases()
    };
    VirtualViewer.prototype.toggleHGuideVisibility = function () {
        Aq.visible = !Aq.visible;
        A1.paintCanvases()
    };
    VirtualViewer.prototype.getWidthRatio = function () {
        var C0 = A1.getCurrentState();
        var C1 = Cr;
        var Cz = C0.getRotationForPage(C0.getPageNumber());
        if (Cz === 90 || Cz === 270) {
            C1 = B9
        }
        return $("#vvDummyScroller").width() / C1
    };
    VirtualViewer.prototype.getHeightRatio = function () {
        var C1 = A1.getCurrentState();
        var C0 = B9;
        var Cz = C1.getRotationForPage(C1.getPageNumber());
        if (Cz === 90 || Cz === 270) {
            C0 = Cr
        }
        return $("#vvDummyScroller").height() / C0
    };
    var E = function (C3, C0) {
        var Cz = C3.split(",");
        for (var C2 = 0; C2 < Cz.length; C2++) {
            var C4 = Cz[C2].split("-");
            for (var C1 = 0; C1 < C4.length; C1++) {
                var C6 = C4[C1];
                var C5 = parseInt(C6, 10);
                if (isNaN(C5)) {
                    return false
                }
                if (C5 < 1) {
                    return false
                }
                if (C5 > C0) {
                    return false
                }
            }
        }
        return true
    };
    VirtualViewer.prototype.printViaExportCore = function (C5, C2, C4, C0, C1, C6) {
        var C3 = $("#vvNewPrintForm");
        C3.attr("action", vvConfig.servletPath);
        var Cz = $("#vvNewPrintFormHiddenFields");
        Cz.html("");
        Cz.append("<input type='hidden' name='action' value='newExport'>");
        Cz.append("<input type='hidden' name='format' value='pdf'>");
        Cz.append("<input type='hidden' name='textAnnotations' value='" + C1 + "'>");
        Cz.append("<input type='hidden' name='nonTextAnnotations' value='" + C6 + "'>");
        Cz.append("<input type='hidden' name='pageRangeType' value='" + C2 + "'>");
        Cz.append("<input type='hidden' name='pageRangeValue' value='" + C4 + "'>");
        Cz.append("<input type='hidden' name='annotations' value='" + CT(JSON.stringify(C5.getAnnotationLayers(true))) + "'>");
        Cz.append("<input type='hidden' name='modelJSON' value='" + CT(JSON.stringify(C0.model)) + "'>");
        Cz.append("<input type='hidden' name='clientInstanceId' value='" + CT(A1.getClientInstanceId()) + "'>");
        Cz.append("<input type='hidden' name='pageCount' value='" + virtualViewer.getPageCount() + "'>");
        C3.submit();
        return "Printed Via Export"
    };
    function CT(Cz) {
        return $("<div/>").text(Cz).html()
    }

    VirtualViewer.prototype.serverSidePrintCore = function (C1) {
        var Cz = A1.getStateForDocumentId(A1.getDocumentId());
        var DE = $("#vvNewPrintSelectPrinterInput option:selected").text();
        var C4 = parseInt($("#vvNewPrintFirstPage").val(), 10);
        var C6 = parseInt($("#vvNewPrintLastPage").val(), 10);
        var C9 = $("#vvNewPrintComplex").val();
        var C8 = $("input[name=rangetype]:checked").val();
        var DF = "undefined";
        if (C8 === "complex") {
            DF = C9
        }
        if (C8 === "pages") {
            DF = C4 + "-" + C6
        }
        if (C8 === "current") {
            DF = Cz.getPageNumber()
        }
        if (C8 === "complex" || C8 === "pages") {
            if (!E(DF, virtualViewer.getPageCount())) {
                alert("Page Range Error");
                return
            }
        }
        var C3 = $("input[name=vvNewPrintMethod]:checked").val();
        var C2 = $("#vvNewPrintAnnotationsSelected").is(":checked");
        var C7 = $("#vvNewPrintAnnotationsTypeText").is(":checked");
        var DC = $("#vvNewPrintGraySelected").is(":checked");
        var DB = $("#vvNewPrintAnnotationsTypeNonText").is(":checked");
        var DA = Be(C8, DF, C4, C6);
        if (DA !== "OK") {
            alert(DA);
            return false
        }
        var C5 = this.getDocumentModel();
        if (C3 === "vvNewPrintPrinterExport") {
            Ax(false);
            return this.printViaExportCore(Cz, C8, DF, C5, C7, DB)
        }
        var C0 = new URI(vvConfig.servletPath);
        C0.addQuery("textAnnotations", C7);
        C0.addQuery("nonTextAnnotations", DB);
        C0.addQuery("action", "serverSidePrint");
        C0.addQuery("grayScale", DC);
        C0.addQuery("printerName", encodeURIComponent(DE));
        C0.addQuery("pageRangeType", C8);
        C0.addQuery("pageRangeValue", encodeURIComponent(DF));
        C0.addQuery("annotations", JSON.stringify(Cz.getAnnotationLayers(true)));
        C0.addQuery("modelJSON", JSON.stringify(C5.model));
        C0.addQuery("clientInstanceId", A1.getClientInstanceId());
        C0.addQuery("pageCount", virtualViewer.getPageCount());
        if (A1.getOverlayPath() !== null) {
            C0.addQuery("overlayPath", A1.getOverlayPath())
        }
        if (vvDefines.cacheBuster === true) {
            C0.addQuery("cacheBuster", Math.random())
        }
        if (Cp) {
            clearTimeout(Cp)
        }
        var DD = C0.query();
        C0.query("");
        $.ajax({
            url: C0.toString(), dataType: "json", type: "POST", data: DD, success: function (DG) {
                if (AB(DG) === true) {
                    return
                }
                BH(DG, C1);
                Ax(false)
            }, error: function (DG) {
                console.log("error")
            }
        });
        return true
    };
    var A3 = function (Cz) {
        var C2 = A1.getStateForDocumentId(A1.getDocumentId());
        var C0 = new URI(vvConfig.servletPath);
        C0.addQuery("action", "getAllTextForPage");
        C0.addQuery("pageIndex", Cz);
        C0.addQuery("documentId", C2.getDocumentId());
        C0.addQuery("clientInstanceId", A1.getClientInstanceId());
        C0.addQuery("pageCount", virtualViewer.getPageCount());
        if (vvDefines.cacheBuster === true) {
            C0.addQuery("cacheBuster", Math.random())
        }
        if (A1.getOverlayPath() !== null) {
            C0.addQuery("overlayPath", A1.getOverlayPath())
        }
        if (Cp) {
            clearTimeout(Cp)
        }
        var C1 = C0.query();
        C0.query("");
        $.ajax({
            url: C0.toString(), type: "POST", dataType: "json", data: C1, success: function (C3) {
                X(C3, Cz)
            }, error: function (C3) {
                console.log("error")
            }
        })
    };
    var BH = function (C0, Cz) {
    };
    var X = function (C0, Cz) {
        var C1 = C0.wordRects;
        if (C1) {
            C1.sort(function (C4, C3) {
                var C2 = C4.bottom - C4.top;
                if (C4.bottom - (C2 * 0.2) <= C3.top) {
                    return C4.top - C3.top
                }
                if (C4.top < C3.top && C4.bottom < C3.bottom) {
                    return C4.top - C3.top
                }
                if (C3.bottom - (C2 * 0.2) <= C4.top) {
                    return C4.top - C3.top
                }
                if (C3.top < C4.top && C3.bottom < C4.bottom) {
                    return C4.top - C3.top
                } else {
                    return C4.left - C3.left
                }
                return 0
            });
            A1.getCurrentState().addPageText(C0, Cz)
        }
    };
    VirtualViewer.prototype.toggleColumnSelectionMode = function () {
        this.columnSelectionMode = !this.columnSelectionMode
    };
    var Bx = function (C0, DA) {
        var Cz = A1.getCurrentState();
        DA.lineWidth = 2;
        var C6 = Cz.getSearchResultsForPage(virtualViewer.getPageNumber());
        var C8 = Cz.getZoomForPage(virtualViewer.getPageNumber) / 100;
        var C9 = Cz.getRotationForPage(Cz.getPageNumber());
        if (C6) {
            for (var C4 = 0; C4 < C6.matches.length; C4 += 1) {
                var C2 = C6.matches[C4];
                var C7;
                if (C4 === A1.currentMatch) {
                    C7 = vvDefines.searchSelectedColor
                } else {
                    C7 = vvDefines.searchDefaultColor
                }
                for (var C5 = 0; C5 < C2.rectangles.length; C5 += 1) {
                    var C3 = C2.rectangles[C5];
                    var C1 = new A0(C3.left, C3.top, C3.right, C3.bottom);
                    A1.highlightImageRect(C1, C0, DA, C7)
                }
            }
        }
    };
    VirtualViewer.prototype.getNubSize = function () {
        if (Modernizr.touch) {
            return vvConfig.polygonNubSizeTouch
        } else {
            return vvConfig.polygonNubSize
        }
    };
    VirtualViewer.prototype.getColorBlobSize = function () {
        if (Modernizr.touch) {
            return vvDefines.annColorBlobSizeTouch
        } else {
            return vvDefines.annColorBlobSize
        }
    };
    function A0(Cz, C1, C2, C0) {
        this.left = Cz;
        this.top = C1;
        this.right = C2;
        this.bottom = C0;
        this.intersects = function (C4) {
            var C3 = !(C4.left > this.right || C4.right < this.left || C4.top > this.bottom || C4.bottom < this.top);
            return C3
        }
    }

    VirtualViewer.prototype.selectAllText = function () {
        var Cz = A1.getCurrentState();
        this.selectTextStartPoint = new Point(0, 0);
        this.selectTextEndPoint = new Point(Cz.getOriginalWidthForPage(CF), Cz.getOriginalHeightForPage(CF));
        this.paintCanvases()
    };
    VirtualViewer.prototype.deselectAllText = function () {
        this.selectTextStartPoint = new Point(0, 0);
        this.selectTextEndPoint = new Point(0, 0);
        this.paintCanvases()
    };
    VirtualViewer.prototype.copyAllText = function () {
        var Cz = A1.getCurrentState();
        this.selectTextStartPoint = new Point(0, 0);
        this.selectTextEndPoint = new Point(Cz.getOriginalWidthForPage(CF), Cz.getOriginalHeightForPage(CF));
        if (A1.currentDragMode === vvDefines.dragModes.selectText) {
            this.paintCanvases()
        }
        this.copySelectedText()
    };
    VirtualViewer.prototype.getSelectedTextRects = function () {
        var C4 = A1.getCurrentState();
        if (!this.selectTextStartPoint || !this.selectTextEndPoint || this.selectTextStartPoint === null || this.selectTextEndPoint === null) {
            return
        }
        var C3 = C4.getPageText(virtualViewer.getPageNumber());
        if (C3) {
            var C6 = C3.wordRects;
            var C5 = [];
            var C2 = new Point(this.selectTextStartPoint.x, this.selectTextStartPoint.y);
            var C1 = new Point(this.selectTextEndPoint.x, this.selectTextEndPoint.y);
            if (C1.y < C2.y) {
                var C0 = C1;
                C1 = C2;
                C2 = C0
            }
            for (var Cz = 0; Cz < C6.length; Cz++) {
                if (At(C2, C1, C6[Cz], this.columnSelectionMode)) {
                    C5.push(C6[Cz])
                }
            }
            return C5
        }
    };
    function At(C0, Cz, C2, C3) {
        if (!C3) {
            if (C0.y < C2.top && Cz.y > C2.bottom) {
                return true
            }
            if (C0.y < C2.bottom && Cz.y > C2.bottom && C0.x < C2.right) {
                return true
            }
            if (Cz.y > C2.top && Cz.y < C2.bottom && C0.y < C2.top && C2.left < Cz.x) {
                return true
            }
        }
        var C1 = new A0(Math.min(C0.x, Cz.x), Math.min(C0.y, Cz.y), Math.max(C0.x, Cz.x), Math.max(C0.y, Cz.y));
        return C1.intersects(C2)
    }

    VirtualViewer.prototype.copySelectedText = function () {
        if (!vvConfig.copySelectedText) {
            return
        }
        var C1 = this.getSelectedTextRects();
        if (C1) {
            var Cz = "";
            var C0 = null;
            var C4 = 1000000;
            for (var C3 = 0; C3 < C1.length; C3++) {
                var C6 = C1[C3];
                if (C6.left < C4) {
                    C4 = C6.left
                }
            }
            for (C3 = 0; C3 < C1.length; C3++) {
                var C2 = C1[C3];
                var C5 = (C2.word).indexOf(";");
                if (C5 !== -1) {
                    C2.word = (C2.word).slice(0, C5 + 1)
                }
                if (C3 === 0) {
                    Cz = A6(Cz, C2, C4)
                }
                if (C0 !== null) {
                    Cz = BP(Cz, C0, C2, C4);
                    Cz = Bu(Cz, C0, C2)
                }
                if (C2.word.indexOf("%%%%EOT") !== 0) {
                    Cz += C2.word
                }
                C0 = C2
            }
            Ct(Cz)
        }
    };
    function Ct(Cz) {
        if ($.trim(Cz) === "") {
            return
        }
        $("#vvClipboardTextarea").html(Cz);
        $("#vvClipboard").dialog("open");
        $("#vvClipboardTextarea").select()
    }

    function BP(C0, C3, C8, Cz) {
        var C2 = 0.7;
        var C6 = C2;
        var DA = 2;
        var C4 = C8.top - C3.top;
        var C9 = C3.bottom - C3.top;
        var C7 = Math.floor(C4 / (C9 * C6));
        var C5 = 0;
        if (C8.left < C3.left) {
            C5 = 1
        }
        C7 = Math.max(C7, C5);
        C7 = Math.min(C7, DA);
        for (var C1 = 0; C1 < C7; C1++) {
            C0 += "\n"
        }
        if (C7 > 0) {
            C0 = A6(C0, C8, Cz)
        }
        return C0
    }

    function A6(C0, C5, Cz) {
        var C7 = 0.355;
        var C6 = C5.bottom - C5.top;
        var C2 = (C6 * C7);
        var C1 = C5.left;
        var C4 = C1 - Cz;
        var C8 = Math.round(C4 / C2);
        for (var C3 = 0; C3 < C8; C3++) {
            C0 += " "
        }
        return C0
    }

    function Bu(DH, C7, C9) {
        var C5 = 0.1272;
        var DD = 0.355;
        var DA = C9.top - C7.top;
        var C4 = C9.bottom - C7.bottom;
        var DF = C7.bottom - C7.top;
        var DC = C9.bottom - C9.top;
        var DB = Math.min(DF, DC);
        var C6 = (DB * C5);
        var C2 = (DB * DD);
        var C8 = Math.floor(DA / DB);
        var C1 = Math.floor(C4 / DB);
        if (C8 > 0 && C1 > 0) {
            return DH
        }
        var C3 = C7.right;
        var DG = C9.left;
        var C0 = DG - C3;
        if (C0 > C6) {
            amountOfWhiteSpace = 1;
            var Cz = Math.round((C0 - (C6 * 2.6)) / C2);
            amountOfWhiteSpace += Cz
        } else {
            amountOfWhiteSpace = 0
        }
        for (var DE = 0; DE < amountOfWhiteSpace; DE++) {
            DH += " "
        }
        return DH
    }

    var CZ = function (C2, DB) {
        if (A1.currentDragMode !== vvDefines.dragModes.selectText) {
            return
        }
        if (!A1.textSelectionAllowed()) {
            return
        }
        DB.lineWidth = 2;
        var C9 = A1.getSelectedTextRects();
        if (C9) {
            var C1 = null;
            var C0 = null;
            var C8 = [];
            for (var C5 = 0; C5 < C9.length; C5++) {
                var C3 = C9[C5];
                var C4 = new A0(C3.left, C3.top, C3.right, C3.bottom);
                if (C0 === null) {
                    C0 = C4
                }
                if (C1 !== null) {
                    if (C4.top === C1.top) {
                        C0.right = C4.right;
                        C1 = C4;
                        continue
                    }
                    C8.push(C0);
                    C0 = C4
                }
                C1 = C4
            }
            if (C0 !== null) {
                C8.push(C0)
            }
            if (C8.length === 0) {
                return
            }
            var Cz = vvDefines.selectedTextColor;
            A1.highlightImageRect(C8[0], C2, DB, Cz);
            var C7 = new A0(0, 0, 0, 0);
            if (C8.length > 2) {
                C7.top = 1000000;
                C7.bottom = 0;
                C7.left = 1000000;
                C7.right = 0;
                for (C5 = 1; C5 < C8.length - 1; C5++) {
                    var C6 = C8[C5];
                    C7.top = Math.min(C6.top, C7.top);
                    C7.bottom = Math.max(C6.bottom, C7.bottom);
                    C7.left = Math.min(C6.left, C7.left);
                    C7.right = Math.max(C6.right, C7.right);
                    A1.highlightImageRect(C6, C2, DB, Cz)
                }
            }
            if (C8.length > 1) {
                var DA = C8[C8.length - 1];
                A1.highlightImageRect(DA, C2, DB, Cz)
            }
        }
    };
    VirtualViewer.prototype.highlightImageRect = function (C4, C6, C3, C0) {
        var C1 = A1.getCurrentState();
        var DA = C1.getZoomForPage(C1.getPageNumber()) / 100;
        var DB = C1.getRotationForPage(C1.getPageNumber());
        var C7 = C4.top * DA;
        var Cz = C4.bottom * DA;
        var C2 = C4.left * DA;
        var C9 = C4.right * DA;
        var C5 = new BoundingBox(C2, C7, C9 - C2, Cz - C7);
        var C8 = true;
        if ((BrowserDetect.browser === "Explorer") && (BrowserDetect.version < 9)) {
            C8 = false
        }
        C3.strokeStyle = C0;
        C3.fillStyle = C0;
        C3.beginPath();
        C3.moveTo(C5.getX1(), C5.getY1());
        C3.lineTo(C5.getX2(), C5.getY1());
        C3.lineTo(C5.getX2(), C5.getY2());
        C3.lineTo(C5.getX1(), C5.getY2());
        C3.closePath();
        C3.fill()
    };
    function C(C0, C1) {
        for (var Cz = 0; Cz < C0.data.length; Cz += 4) {
            C0.data[Cz] = C0.data[Cz] - 255 + C1.r;
            C0.data[Cz + 1] = C0.data[Cz + 1] - 255 + C1.g;
            C0.data[Cz + 2] = C0.data[Cz + 2] - 255 + C1.b;
            C0.data[Cz + 3] = 255
        }
    }

    function Bz(Cz) {
        if (Cz.nodeType === 1) {
            Cz.setAttribute("unselectable", "on")
        }
        var C0 = Cz.firstChild;
        while (C0) {
            Bz(C0);
            C0 = C0.nextSibling
        }
    }

    function A9(C0) {
        var DA = document.getElementById("vvImageCanvas");
        if (!C0) {
            C0 = window.event
        }
        if (Modernizr.touch) {
            C0 = Bv(C0)
        }
        B2();
        var C5 = Af(DA);
        var C8 = AH(C0.clientX - C5[0], C0.clientY - C5[1]);
        var DB = myPainter.isAnnotationAtPoint(C8);
        if (DB) {
            cursorNormal();
            AT(vvDefines.dragModes.pan);
            return
        }
        var C1 = false;
        var C3 = false;
        switch (C0.which) {
            case 1:
                C1 = true;
                break;
            case 2:
                break;
            case 3:
                C3 = true;
                break;
            default:
                break
        }
        if (C1) {
            AJ = true;
            var C2 = document.getElementById("vvImageCanvas");
            var Cz = C2.getBoundingClientRect();
            var C6 = C0.clientX - Cz.left;
            var C9 = C6 / virtualViewer.getWidthRatio();
            var C4 = C0.clientY - Cz.top;
            var C7 = C4 / virtualViewer.getHeightRatio();
            virtualViewer.selectTextStartPoint = new Point(C9, C7);
            virtualViewer.selectTextEndPoint = new Point(C9, C7);
            virtualViewer.paintCanvases()
        }
    }

    function Cb(C3) {
        if (!C3) {
            C3 = window.event
        }
        var C4 = document.getElementById("vvImageCanvas");
        var C2 = C4.getBoundingClientRect();
        var Cz = C3.clientX - C2.left;
        var C1 = Cz / virtualViewer.getWidthRatio();
        var C5 = C3.clientY - C2.top;
        var C0 = C5 / virtualViewer.getHeightRatio();
        virtualViewer.selectTextEndPoint = new Point(C1, C0);
        virtualViewer.paintCanvases()
    }

    function Cq(Cz) {
        B(Cz)
    }

    function B(Cz) {
        virtualViewer.paintCanvases();
        AJ = false
    }

    function BS(Cz) {
        if (AJ) {
            Cb(Cz)
        }
    }

    function BZ(C2) {
        if (!C2) {
            C2 = window.event
        }
        A1.dragging = false;
        cursorGuides();
        if (A1.currentDragMode !== vvDefines.dragModes.guides) {
            return
        }
        if ((virtualViewer.horizontalGuide.locked || !virtualViewer.horizontalGuide.visible) && (virtualViewer.verticalGuide.locked || !virtualViewer.verticalGuide.visisible)) {
            var C0 = document.getElementById("vvInnerDiv");
            if (Modernizr.touch) {
                C2 = Bv(C2)
            }
            B2();
            var C3 = Af(C0);
            var Cz = AH(C2.clientX - C3[0], C2.clientY - C3[1]);
            var C1 = myPainter.isAnnotationAtPoint(Cz);
            if (C1) {
                cursorNormal();
                AT(vvDefines.dragModes.pan);
                return
            }
            if (vvConfig.imageScrollBars === false) {
                Z(C2);
                return
            }
        }
        CR(C2);
        virtualViewer.horizontalGuide.locked = virtualViewer.horizontalGuide.visible;
        virtualViewer.verticalGuide.locked = virtualViewer.verticalGuide.visible;
        A1.paintCanvases()
    }

    function BG(Cz) {
        A1.dragging = false
    }

    function AM(Cz) {
        cursorGuides();
        if (A1.currentDragMode !== vvDefines.dragModes.guides) {
            return
        }
        virtualViewer.horizontalGuide.locked = false;
        virtualViewer.verticalGuide.locked = false;
        CR(Cz);
        A1.paintCanvases()
    }

    function Bn(Cz) {
        if ((virtualViewer.horizontalGuide.locked || !virtualViewer.horizontalGuide.visible) && (virtualViewer.verticalGuide.locked || !virtualViewer.verticalGuide.visisible)) {
            if (A1.dragging) {
                AT(vvDefines.dragModes.pan);
                cursorGrabClosed();
                e(Cz);
                return
            }
        }
        cursorGuides();
        CR(Cz)
    }

    function CR(C1) {
        if (!C1) {
            C1 = window.event
        }
        if (A1.currentDragMode !== vvDefines.dragModes.guides) {
            return
        }
        var C2 = document.getElementById("vvImageCanvas");
        var C0 = C2.getBoundingClientRect();
        if (!virtualViewer.verticalGuide.locked) {
            var Cz = C1.clientX - C0.left;
            virtualViewer.verticalGuide.value = Cz / virtualViewer.getWidthRatio()
        }
        if (!virtualViewer.horizontalGuide.locked) {
            var C3 = C1.clientY - C0.top;
            virtualViewer.horizontalGuide.value = C3 / virtualViewer.getHeightRatio()
        }
        A1.paintCanvases()
    }

    var Bd = function (C3, C0) {
        var C1, Cz, C4, C2;
        if (virtualViewer.verticalGuide && virtualViewer.verticalGuide.visible) {
            C1 = virtualViewer.verticalGuide.value * virtualViewer.getWidthRatio();
            C4 = 0;
            Cz = C1;
            C2 = C3.height;
            if (virtualViewer.verticalGuide.locked) {
                C0.strokeStyle = vvConfig.lockedGuideColor
            } else {
                C0.strokeStyle = vvConfig.activeGuideColor
            }
        }
        C0.beginPath();
        C0.moveTo(C1, C4);
        C0.lineTo(Cz, C2);
        C0.lineWidth = vvConfig.guideLineWidth;
        C0.stroke();
        if (virtualViewer.horizontalGuide && virtualViewer.horizontalGuide.visible) {
            C1 = 0;
            C4 = virtualViewer.horizontalGuide.value * virtualViewer.getHeightRatio();
            Cz = C3.width;
            C2 = C4;
            if (virtualViewer.horizontalGuide.locked) {
                C0.strokeStyle = vvConfig.lockedGuideColor
            } else {
                C0.strokeStyle = vvConfig.activeGuideColor
            }
        }
        C0.beginPath();
        C0.moveTo(C1, C4);
        C0.lineTo(Cz, C2);
        C0.lineWidth = vvConfig.guideLineWidth;
        C0.stroke()
    };

    function D() {
        var C1 = vvConfig.hotkeys;
        if (!C1) {
            return
        }
        var C0 = function (C3) {
            for (var C4 = 0; C4 < C3.length; C4++) {
                (function (C5) {
                    Bi(C3[C5].key, function (C6) {
                        C6.preventDefault();
                        C3[C5].method();
                        return false
                    })
                })(C4)
            }
        };
        C0(C1);
        var C2 = vvConfig.customHotKeys;
        if (!C2) {
            return
        }
        var Cz = function (C4) {
            for (var C3 = 0; C3 < C4.length; C3++) {
                (function (C5) {
                    Bi(C4[C5].key, function (C6) {
                        C6.preventDefault();
                        C4[C5].method();
                        return false
                    })
                })(C3)
            }
        };
        Cz(C2)
    }

    function Bi(C5, C1) {
        if (!C5) {
            return
        }
        var C0 = C5.split(",");
        for (var Cz = 0; Cz < C0.length; Cz++) {
            var C3 = C0[Cz];
            if (AC[C3] === C3) {
                alert(C3 + " has been detected as a duplicate keyboard shortcut")
            }
            AC[C3] = C3;
            var C2 = BJ(C3);
            if (Bm(C3) && !CX(C3)) {
                var C4 = CQ(C3);
                Cs(C2, C4, C1)
            } else {
                jQuery(document).bind(C2, C3, C1)
            }
        }
    }

    function Cs(C0, C1, Cz) {
        $(document).bind(C0, function (C2) {
            if (C2.shiftKey && C2.ctrlKey && f(C1, C2.keyCode)) {
                Cz(C2)
            }
        })
    }

    function CX(C0) {
        var Cz = Cg(C0);
        if (vvDefines.specialKeys[Cz]) {
            return true
        }
        return false
    }

    function Bm(Cz) {
        return Cz.indexOf("ctrl+shift") === 0
    }

    function f(C0, C1) {
        var Cz = {187: 61, 189: 173};
        return C0 === C1 || C0 === Cz[C1]
    }

    function CQ(Cz) {
        var C1 = Cg(Cz);
        if (C1 === "=") {
            return 61
        }
        if (C1 === "plusKeypad") {
            return 107
        }
        if (C1 === "minusKeypad") {
            return 109
        }
        if (C1 === "-") {
            return 173
        }
        var C0 = C1.charCodeAt(0) - 32;
        return C0
    }

    function Cg(C0) {
        var Cz = C0.split("+");
        var C1 = Cz[Cz.length - 1];
        return C1
    }

    function BJ(Cz) {
        return "keydown"
    }

    function As() {
        if (!vvConfig.hotkeys) {
            return ""
        }
        var C2 = [];
        for (var C3 = 0; C3 < Math.ceil((vvConfig.hotkeys.length) / 2); C3++) {
            C2.push([vvConfig.hotkeys[C3].key, getLocalizedValue(vvConfig.hotkeys[C3].localizedValue, vvConfig.hotkeys[C3].defaultValue)])
        }
        var C5 = [];
        for (var C1 = Math.ceil((vvConfig.hotkeys.length) / 2); C1 < (vvConfig.hotkeys.length); C1++) {
            C5.push([vvConfig.hotkeys[C1].key, getLocalizedValue(vvConfig.hotkeys[C1].localizedValue, vvConfig.hotkeys[C1].defaultValue)])
        }
        var C8 = "";
        C8 += '<div class="vvKeyboardHintsContents">';
        C8 += '<table width=100% class="vvKeyboardHintsMasterTable"><tr><td valign=top>';
        C8 += '<table width=100% class="vvKeyboardHintsSubTable">';
        var C0 = null;
        var C4 = null;
        var C7 = null;
        for (var C6 = 0; C6 < C2.length; C6++) {
            if (C6 % 2 === 0) {
                C7 = "vvKeyboardHintsOddRow"
            } else {
                C7 = "vvKeyboardHintsEvenRow"
            }
            C0 = C2[C6][0];
            C4 = C2[C6][1];
            if (C0 && C4) {
                C8 += "<tr class='" + C7 + "'><td>" + CJ(C0) + "</td><td>" + C4 + "</td></tr>"
            }
        }
        C8 += "</table></td>";
        C8 += '<td class="vvKeyboardHintsGap"></td>';
        C8 += '<td valign=top><table width=100% class="vvKeyboardHintsSubTable">';
        for (var Cz = 0; Cz < C5.length; Cz++) {
            if (Cz % 2 === 0) {
                C7 = "vvKeyboardHintsOddRow"
            } else {
                C7 = "vvKeyboardHintsEvenRow"
            }
            C0 = C5[Cz][0];
            C4 = C5[Cz][1];
            if (C0 && C4) {
                C8 += "<tr class='" + C7 + "'><td>" + CJ(C0) + "</td><td>" + C4 + "</td></tr>"
            }
        }
        C8 += "</table></td></tr>";
        C8 += "</table>";
        C8 += "</div>";
        return C8
    }

    function CN(C0) {
        var Cz = C0;
        var C1 = vvDefines.specialKeys[C0];
        if (C1) {
            Cz = C1
        }
        Cz = "<span class='keyboardkey'>" + Cz + "</span>";
        return Cz
    }

    function CJ(C5) {
        if (!C5) {
            return "N/A"
        }
        C5 = C5.replace("++", "+plus");
        var C1 = C5.split(",");
        var C3 = C1[0];
        var C4 = "";
        var C0 = C3.split("+");
        for (var Cz = 0; Cz < C0.length; Cz++) {
            var C2 = C0[Cz];
            C4 += CN(C2);
            if (Cz !== C0.length - 1) {
                C4 += " "
            }
        }
        C4 = C4.replace("plus", "+");
        return C4
    }

    VirtualViewer.prototype.countPagesForDocument = function (C0, Cz) {
        var C3 = -1;
        var C1 = new URI(vvConfig.servletPath);
        C1.addQuery("action", "countPages");
        C1.addQuery("documentId", C0);
        C1.addQuery("clientInstanceId", Cz);
        var C2 = C1.query();
        C1.query("");
        $.ajax({
            url: C1.toString(), async: false, type: "POST", data: C2, dataType: "json", success: function (C4) {
                C3 = C4.pageCount
            }
        });
        return C3
    };
    VirtualViewer.prototype.addPageToSelection = function (Cz) {
        var C1 = A1.getCurrentState();
        var C0 = C1.getDocumentModel();
        if (C0) {
            C0.addPageToSelection(Cz)
        }
    };
    VirtualViewer.prototype.removePageFromSelection = function (Cz) {
        var C1 = A1.getCurrentState();
        var C0 = C1.getDocumentModel();
        if (C0) {
            C0.removePageFromSelection(Cz)
        }
    };
    VirtualViewer.prototype.disabledToolbarOptionDialog = function (C1, Cz) {
        var C3;
        var C2;
        var C0 = C1;
        switch (C0) {
            case"save":
                if (!Cz) {
                    C2 = "Saving this Document is not allowed."
                } else {
                    C2 = Cz
                }
                C3 = "Cannot Save Document";
                break;
            case"export":
                if (!Cz) {
                    C2 = "Exporting this Document is not allowed."
                } else {
                    C2 = Cz
                }
                C3 = "Cannot Export Document";
                break;
            case"send":
                if (!Cz) {
                    C2 = "Sending this Document is not allowed."
                } else {
                    C2 = Cz
                }
                C3 = "Cannot Send Document";
                break;
            case"print":
                if (!Cz) {
                    C2 = "Printing this Document is not allowed."
                } else {
                    C2 = Cz
                }
                C3 = "Cannot Print Document";
                break
        }
        $("#vvDisableToolbarDialogText").html(C2);
        $("#vvDisableToolbarDialog").dialog({
            resizable: false,
            modal: true,
            title: C3,
            dialogClass: "vvDisableToolbarDialogClass",
            autoOpen: false,
            minWidth: "315",
            buttons: [{
                text: getLocalizedValue("disableToolbarDialog.okButton", "Ok"), click: function () {
                    $(this).dialog("close")
                }
            }]
        })
    };
    VirtualViewer.prototype.setOverlayPath = function (Cz) {
        A1.overlayPath = Cz
    };
    VirtualViewer.prototype.getOverlayPath = function () {
        return A1.overlayPath
    };
    VirtualViewer.prototype.checkPageManipulationsEnabled = function (C0) {
        var C1 = null;
        var Cz = true;
        if (!vvConfig.pageManipulations) {
            C1 = getLocalizedValue("pageManipulations.disabled", "Page Manipulations have been disabled by the administrator in the configuration.");
            Cz = false
        } else {
            if (A1.getDocumentModel() && A1.getDocumentModel().hasRedactions()) {
                C1 = getLocalizedValue("pageManipulations.redactions", "Page Manipulations are currently not supported in documents containing redactions.");
                Cz = false
            }
        }
        if (C0 && !Cz) {
            alert(C1)
        }
        return Cz
    };
    var Cc = function (C0, DA) {
        var C4 = false;
        if (C0.getType() === vvDefines.annotationTypes.SANN_POSTIT) {
            var C2 = DA.getX();
            var Cz = DA.getY();
            var C5 = $(".snowbound-text-annotation");
            var C9 = stripPx(C5.css("padding-right"));
            var C3 = $(".vvAnnMinimize").width();
            var C6 = (C3 + C9);
            var C7 = C0.getBoundingBox(C0.collapsed);
            var C1 = C7.getX2() * C0.getWidthRatio();
            var C8 = C7.getY1() * C0.getHeightRatio();
            if (C2 >= (C1 - C6) && C2 <= C1 && Cz >= C8 && Cz <= (C8 + C6)) {
                C0.collapsed = !C0.collapsed;
                virtualViewer.paintCanvases();
                C4 = true
            }
        }
        return C4
    };

    // reload date/time png image on mouse over stamp button
    VirtualViewer.prototype.reloadDateTimePngImage = function () {
        var uri = new URI('/VirtualViewerJavaHTML5/resources/stamps/DateTime.png');
        uri.addQuery("base64", "true");
        var data = uri.query();
        uri.query("");

        $.ajax({
            url: uri.toString(),
            type: "GET",
            data: data,
            success: function (result) {
                q[8].stampData = result;
            }
        })
    };
};