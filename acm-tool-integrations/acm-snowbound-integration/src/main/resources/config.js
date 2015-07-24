vvConfig = {
    // Path to the AJAX servlet
    servletPath: "/VirtualViewerJavaHTML5/AjaxServlet",

    // Defines the intervals at which to zoom
    zoomLevels : [ 2, 3, 4, 6, 8, 10, 15, 20, 30, 40, 50, 75, 100, 150, 200, 300, 400, 600,
        800, 1000, 1500, 2000, 3000],

    // Percentage to stop allowing users to zoom the image
    maxZoomPercent: 1000,

    // Wait X milliseconds before requesting the zoomed image.  This exists so
    // that if the user hits zoom several times quickly we spare the server load
    // by only requesting the final zoom level
    zoomTimeout: 500,

    // Wait X milliseconds before requesting the adjusted images.  This
    // exists to throttle the number of image requests sent to the server.
    // If the user quickly slides the sliders back and forth this will wait
    // X seconds before requesting the updated image.
    pictureControlsTimeout: 200,

    // Wait X milliseconds before displaying the "Please wait while your
    // image is loaded." dialog
    waitDialogTimeout: 1000,

    // Set the size and color of the "handle" used to resize annotations as
    // well as indicate the "end zone" of the polygon tools.
    polygonNubSize: 10,
    polygonNubSizeTouch: 40,
    polygonNubFillColor: "rgba(0,0,255,.40)",

    // Turn on scroll bars for the image display.  This disables the pan tool.
    imageScrollBars: true,

    //inverting pan scroll.
    invertedPanScrollX: true,
    invertedPanScrollY: true,

    // Whether or not to include annotations when sendDocument is called
    sendDocumentWithAnnotations: true,

    // maintain the same zoom, rotation, fit, flip, and other settings when
    // switching between pages
    retainViewOptionsBetweenPages: true,

    // The default zoom mode 
    defaultZoomMode: vvDefines.zoomModes.fitWindow,

    // Should the text inside of text annotations rotate along with the
    // document?
    rotateTextAnnotations: true,

    // Which printing mechanism to use, "local" or "server"
    defaultPrintingMethod: "server",

    // Beta version of enhanced local print (may be used at your own risk)
    enableEnhancedLocalPrintingBeta: false,

    // Should we burn the annotations into the image when printing
    printBurnAnnotations: true,

    // Include the options to print only Text or Non-Text annotations
    printShowTypeToggles: false,

    // Should we burn the annotations into the image when exporting
    exportBurnAnnotations: true,

    // Create a new annotation layer for each annotation
    oneLayerPerAnnotation: false,

    // The default width in image pixels of the collapsed sticky note
    collapseStickiesSize: 50,

    // Collapse stickies by default when loading a page
    collapseStickiesByDefault: false,

    // Reload the document model after a save.  Used for systems like
    // FileNet which generate a documentId on the server
    //reloadDocumentOnSave: true,
    reloadDocumentOnSave: false,

    // If true, newly added text annotations will immediately enter 'edit'
    // mode with the contents highlighted.
    immediatelyEditTextAnnotations: true,

    // If true, the redaction button in the layer manager will be hidden. 
    hideRedactionUI: false,

    // The following three help parameters are passed to window.open when
    // creating the help window.
    // window.open(helpURL,helpWindowName,helpWindowParams);

    // This can be (and often should be) a relative URL Path 
    helpURL: "http://virtualviewer.com/VirtualViewerJavaAJAXHelp/virtualviewer.htm",
    helpWindowName: "helpWindow",
    helpWindowParams: "scrollbars=1,width=800,height=600",

    // If the default zoom mode is 'fitLast', should the viewer respect that
    // when switching between documents
    fitLastBetweenDocuments: false,

    // Toggle the thumbnail panel
    showThumbnailPanel: true,

    // Enable/disable the pages thumbnail tab
    showPageThumbnails: true,

    // Enable/disable the documents thumbnail tab
    showDocThumbnails: true,

    // Enable/disable the search tab
    showSearch: true,

    // Multiple Document mode
    // Accepted values for multipleDocMode are: availableDocuments, viewedDocuments, specifiedDocuments
    multipleDocMode: vvDefines.multipleDocModes.availableDocuments,

    // Enable/Disable Page Manipulation Functionality
    pageManipulations: true,

    // Enable/Disable the "New Document" page manipulation menu 
    pageManipulationsNewDocumentMenu: true,

    // Enable/Disable Base64 encoding of annotations
    base64EncodeAnnotations: true,

    // Enable/Disable the Text Rubber Stamp Functionality
    enableTextRubberStamp: false,

    // Configure the text rubber stamps
    textRubberStamps: [
//        { textString: "Approved", 
//          fontFace: "Times New Roman",
//          fontSize: 30,
//          fontBold: true, 
//          fontItalic: true,
//          fontUnderline: false,
//          fontColor: "00FF00" }, 
//        { textString: "Denied", 
//          fontColor: "FF0000" }
    ],

    // Enable/Disable single-click Image Rubber Stamp functionality
    enableSingleClickImageRubberStamp: true,

    // Whether or not text searches should be case sensitive
    searchCaseSensitive: false,

    // defines how many pixels to pan for each arrow press
    panIncrement: 30,

    // Whether or not to even attempt to request documents as SVG images 
    enableSVGSupport: true,

    // Allows the copying of text
    copySelectedText: true,

    // Allows notification of unsaved changes (Dialog Box) to appear when closing a tab or browser
    unsavedChangesNotification : true,

    // Allow the use of "sticky" Annotation Buttons. The Annotation will stay "on" until it is clicked again.
    stickyAnnButtons: false,

    // Configure the keyboard shortcuts 
    hotkeys: [
        {
            key:'ctrl+shift+=,ctrl+shift+i,ctrl+shift+plusKeypad', // ctrl+shift+= will not work in IE 9 or IE 10
            method: function() {
                virtualViewer.zoomIn();
            },
            localizedValue: 'hotkeyHints.zoomIn',
            defaultValue: 'Zoom In'
        },
        {
            key:'ctrl+shift+-,ctrl+shift+o,ctrl+shift+minusKeypad', // ctrl+shift+- will not work in IE 9 or IE 10
            method: function() {
                virtualViewer.zoomOut();
            },
            localizedValue: 'hotkeyHints.zoomOut',
            defaultValue: 'Zoom Out'
        },
        {
            key:'ctrl+shift+e',
            method: function() {
                virtualViewer.exportDocument();
            },
            localizedValue: 'hotkeyHints.exportDocument',
            defaultValue: 'Export Document'
        },
        {
            key:'ctrl+shift+p',
            method: function() {
                virtualViewer.printDocument();
            },
            localizedValue: 'hotkeyHints.printDocument',
            defaultValue: 'Print Document'
        },
        {
            key:'end',
            method: function() {
                virtualViewer.lastPage();
            },
            localizedValue: 'hotkeyHints.lastPage',
            defaultValue: 'Last Page'
        },
        {
            key:'home',
            method: function() {
                virtualViewer.firstPage();
            },
            localizedValue: 'hotkeyHints.firstPage',
            defaultValue: 'First Page'
        },
        {
            key:'ctrl+shift+pageup',
            method: function() {
                virtualViewer.previousPage();
            },
            localizedValue: 'hotkeyHints.previousPage',
            defaultValue: 'Previous Page'
        },
        {
            key:'ctrl+shift+pagedown',
            method: function() {
                virtualViewer.nextPage();
            },
            localizedValue: 'hotkeyHints.nextPage',
            defaultValue: 'Next Page'
        },
        {
            key:'ctrl+shift+l',
            method: function() {
                virtualViewer.rotateCounter();
            },
            localizedValue: 'hotkeyHints.rotateCounter',
            defaultValue: 'Rotate Left'
        },
        {
            key:'ctrl+shift+r',
            method: function() {
                virtualViewer.rotateClock();
            },
            localizedValue: 'hotkeyHints.rotateClock',
            defaultValue: 'Rotate Right'
        },
        {
            key:'ctrl+shift+t',
            method: function() {
                virtualViewer.toggleThumbnailPanel();
            },
            localizedValue: 'hotkeyHints.toggleThumbnailPanel',
            defaultValue: 'Toggle Thumbnail Panel'
        },
        {
            key:'ctrl+shift+c',
            method: function() {
                virtualViewer.copySelectedText();
            },
            localizedValue: 'hotkeyHints.copyText',
            defaultValue: 'Copy Selected Text'
        },
        {
            key:'ctrl+shift+d',
            method: function() {
                virtualViewer.toggleColumnSelectionMode();
            },
            localizedValue: 'hotkeyHints.toggleTextSelectionMode',
            defaultValue: 'Toggle Column Text Selection'
        },
        {
            key:'ctrl+shift+u',
            method: function() {
                virtualViewer.toggleImageInfo();
            },
            localizedValue: 'hotkeyHints.toggleImageInfo',
            defaultValue: 'Toggle Image Info Dialog'
        },
        {
            key:'ctrl+shift+g',
            method: function() {
                virtualViewer.collapseAllStickyNotes(true,true);
            },
            localizedValue: 'hotkeyHints.collapseStickyNotes',
            defaultValue: 'Collapse Sticky Notes'
        },
        {
            key:'ctrl+shift+n',
            method: function() {
                virtualViewer.collapseAllStickyNotes(false,true);
            },
            localizedValue: 'hotkeyHints.expandStickyNotes',
            defaultValue: 'Expand Sticky Notes'
        },
        {
            key:'ctrl+/',
            method: function() {
                virtualViewer.toggleKeyboardHints();
            },
            localizedValue: 'hotkeyHints.showKeyboardHints',
            defaultValue: 'Show Keyboard Hints'
        }

    ],

    customHotKeys: [

        {
            key: 'ctrl+shift+z',
            method: function() { virtualViewer.undoAnnotation(); }
        },
        {
            key:'ctrl+shift+y',
            method: function() {
                virtualViewer.redoAnnotation(); }
        }

    ],

    emailDefaults: {
        prepopulateFrom: "prepopulatedEmail@domain.com",
        prepopulateTo : "",
        prepopulateCC : "",
        prepopulateBCC : "",
        prepopulateSubject: "VirtualViewer Document attached",
        prepopulateBody: "Please see the attached document sent from VirtualViewer."
    },

    // Default appearance options for annotations
    annotationDefaults: {
        lineColor: "FE0000",
        lineWidth: 3,

        fillColor: "FE0000",

        stickyFillColor: "FCEFA1",
        stickyMargin: 10, // also need to adjust .vvStickyNote in webviewer.css

        highlightFillColor: "FCEFA1",
        highlightOpacity: 0.4,

        textString: "Text",

        fontFace: "Arial",
        fontSize: 14,
        fontBold: false,
        fontItalic: false,
        fontStrike: false,    // for future use
        fontUnderline: false, // for future use
        fontColor: "000000"
    },

    maxInfoFieldLength : 128,

    guideLineWidth : 2,
    activeGuideColor : "#000099",
    lockedGuideColor : "#990000",

    // Define the fields that will be displayed in the Image Info Dialog     
    imageInfoFields : [
        // Define the *Document-Specific* properties here in the order they are to be displayed
        { fieldId: "documentId",
            fieldCaption: "Document ID"},
        { fieldId: "documentDisplayName",
            fieldCaption: "Document Name" },
        { fieldId: "documentByteSize",
            fieldCaption: "File Size (Bytes)" },
        { fieldId: "pageCount",
            fieldCaption: "Page Count" },
        { fieldId: "documentFormat",
            fieldCaption: "File Format" },

        // Define the *Page-Specific* properties here in the order they are to be displayed

        { fieldId: "compressionType", // This is only for Tiff files and will be 'TIFF_G4_FAX', 'TIFF_JPEG', 'TIFF_LZW', etc
            fieldCaption: "Compression Type" },
        { fieldId: "imageSizePixels",
            fieldCaption: "Size in Pixels" },
        { fieldId: "imageSizeInches",
            fieldCaption: "Image Size" },
        { fieldId: "dpi",
            fieldCaption: "DPI" },
        { fieldId: "bitDepth",
            fieldCaption: "Bit Depth" },
        { fieldId: "pageNumber",
            fieldCaption: "Page Number" },
        { fieldId: "tiffTag315",
            fieldCaption: "Copyright" }
    ]
};
