/**
 * @license
 * Copyright (C) 2013-2015 KO GmbH <copyright@kogmbh.com>
 *
 * @licstart
 * The JavaScript code in this page is free software: you can redistribute it
 * and/or modify it under the terms of the GNU Affero General Public License
 * (GNU AGPL) as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.  The code is distributed
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU AGPL for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this code.  If not, see <http://www.gnu.org/licenses/>.
 *
 * As additional permission under GNU AGPL version 3 section 7, you
 * may distribute non-source (e.g., minimized or compacted) forms of
 * that code without the copy of the GNU GPL normally required by
 * section 4, provided you include this license notice and a URL
 * through which recipients can access the Corresponding Source.
 *
 * As a special exception to the AGPL, any HTML file which merely makes function
 * calls to this code, and for that purpose includes it by reference shall be
 * deemed a separate work for copyright law purposes. In addition, the copyright
 * holders of this code give you permission to combine this code with free
 * software libraries that are released under the GNU LGPL. You may copy and
 * distribute such a system following the terms of the GNU AGPL for this code
 * and the LGPL for the libraries. If you modify this code, you may extend this
 * exception to your version of the code, but you are not obligated to do so.
 * If you do not wish to do so, delete this exception statement from your
 * version.
 *
 * This license applies to this entire compilation.
 * @licend
 * @source: http://viewerjs.org/
 * @source: http://github.com/kogmbh/ViewerJS
 */

/*global document, PDFJS, console, TextLayerBuilder*/


function PDFViewerPlugin() {
    "use strict";

    function loadScript(path, callback) {
        var script = document.createElement('script');
        script.async = false;
        script.src = path;
        script.type = 'text/javascript';
        script.onload = callback || script.onload;
        document.getElementsByTagName('head')[0].appendChild(script);
    }

    function init(callback) {
        var pluginCSS;

        loadScript('./compatibility.js', function () {
            loadScript('./pdf.js');
            loadScript('./ui_utils.js');
            loadScript('./text_layer_builder.js');
            loadScript('./pdfjsversion.js', callback);
        });

        pluginCSS = document.createElement('link');
        pluginCSS.setAttribute("rel", "stylesheet");
        pluginCSS.setAttribute("type", "text/css");
        pluginCSS.setAttribute("href", "./PDFViewerPlugin.css");
        document.head.appendChild(pluginCSS);
    }

    var self = this,
        pages = [],
        domPages = [],
        pageText = [],
        renderingStates = [],
        RENDERING = {
            BLANK: 0,
            RUNNING: 1,
            FINISHED: 2,
            RUNNINGOUTDATED: 3
        },
        container = null,
        pdfDocument = null,
        pageViewScroll = null,
        isGuessedSlideshow = true, // assume true as default, any non-matching page will unset this
        isPresentationMode = false,
        scale = 1,
        currentPage = 1,
        maxPageWidth = 0,
        maxPageHeight = 0,
        createdPageCount = 0;

    function scrollIntoView(elem) {
        elem.parentNode.scrollTop = elem.offsetTop;
    }

    function isScrolledIntoView(elem) {
        if (elem.style.display === "none") {
            return false;
        }

        var docViewTop = container.scrollTop,
            docViewBottom = docViewTop + container.clientHeight,
            elemTop = elem.offsetTop,
            elemBottom = elemTop + elem.clientHeight;

        // Is in view if either the top or the bottom of the page is between the
        // document viewport bounds,
        // or if the top is above the viewport and the bottom is below it.
        return (elemTop >= docViewTop && elemTop < docViewBottom)
                || (elemBottom >= docViewTop && elemBottom < docViewBottom)
                || (elemTop < docViewTop && elemBottom >= docViewBottom);
    }

    function getDomPage(page) {
        return domPages[page.pageIndex];
    }
    function getPageText(page) {
        return pageText[page.pageIndex];
    }
    function getRenderingStatus(page) {
        return renderingStates[page.pageIndex];
    }
    function setRenderingStatus(page, renderStatus) {
        renderingStates[page.pageIndex] = renderStatus;
    }

    function updatePageDimensions(page, width, height) {
        var domPage = getDomPage(page),
            canvas = domPage.getElementsByTagName('canvas')[0],
            textLayer = domPage.getElementsByTagName('div')[0],
            cssScale = 'scale(' + scale + ', ' + scale + ')';

        domPage.style.width = width + "px";
        domPage.style.height = height + "px";

        canvas.width = width;
        canvas.height = height;

        textLayer.style.width = width + "px";
        textLayer.style.height = height + "px";

        CustomStyle.setProp('transform', textLayer, cssScale);
        CustomStyle.setProp('transformOrigin', textLayer, '0% 0%');

        if (getRenderingStatus(page) === RENDERING.RUNNING) {
            // TODO: should be able to cancel that rendering
            setRenderingStatus(page, RENDERING.RUNNINGOUTDATED);
        } else {
            // Once the page dimension is updated, the rendering state is blank.
            setRenderingStatus(page, RENDERING.BLANK);
        }
    }

    function ensurePageRendered(page) {
        var domPage, textLayer, canvas;

        if (getRenderingStatus(page) === RENDERING.BLANK) {
            setRenderingStatus(page, RENDERING.RUNNING);

            domPage = getDomPage(page);
            textLayer = getPageText(page);
            canvas = domPage.getElementsByTagName('canvas')[0];

            page.render({
                canvasContext: canvas.getContext('2d'),
                textLayer: textLayer,
                viewport: page.getViewport(scale)
            }).promise.then(function () {
                if (getRenderingStatus(page) === RENDERING.RUNNINGOUTDATED) {
                    // restart
                    setRenderingStatus(page, RENDERING.BLANK);
                    ensurePageRendered(page);
                } else {
                    setRenderingStatus(page, RENDERING.FINISHED);
                }
            });
        }
    }

    function completeLoading() {
        var allPagesVisible = !self.isSlideshow();
        domPages.forEach(function (domPage) {
            if (allPagesVisible) {
                domPage.style.display = "block";
            }
            container.appendChild(domPage);
        });

        self.showPage(1);
        self.onLoad();
    }

    function createPage(page) {
        var pageNumber,
            textLayerDiv,
            textLayer,
            canvas,
            domPage,
            viewport;

        pageNumber = page.pageIndex + 1;

        viewport = page.getViewport(scale);

        domPage = document.createElement('div');
        domPage.id = 'pageContainer' + pageNumber;
        domPage.className = 'page';
        domPage.style.display = "none";

        canvas = document.createElement('canvas');
        canvas.id = 'canvas' + pageNumber;

        textLayerDiv = document.createElement('div');
        textLayerDiv.className = 'textLayer';
        textLayerDiv.id = 'textLayer' + pageNumber;

        domPage.appendChild(canvas);
        domPage.appendChild(textLayerDiv);

        pages[page.pageIndex] = page;
        domPages[page.pageIndex] = domPage;
        renderingStates[page.pageIndex] = RENDERING.BLANK;

        updatePageDimensions(page, viewport.width, viewport.height);
        if (maxPageWidth < viewport.width) {
            maxPageWidth = viewport.width;
        }
        if (maxPageHeight < viewport.height) {
            maxPageHeight = viewport.height;
        }
        // A very simple but generally true guess - if any page has the height greater than the width, treat it no longer as a slideshow
        if (viewport.width < viewport.height) {
            isGuessedSlideshow = false;
        }

        textLayer = new TextLayerBuilder({
            textLayerDiv: textLayerDiv,
            viewport: viewport,
            pageIndex: pageNumber - 1
        });
        page.getTextContent().then(function (textContent) {
            textLayer.setTextContent(textContent);
        });
        pageText[page.pageIndex] = textLayer;

        createdPageCount += 1;
        if (createdPageCount === (pdfDocument.numPages)) {
            completeLoading();
        }
    }

    this.initialize = function (viewContainer, location) {
        var self = this,
            i,
            pluginCSS;


        init(function () {
            PDFJS.workerSrc = "./pdf.worker.js";
            PDFJS.getDocument(location).then(function loadPDF(doc) {
                pdfDocument = doc;
                container = viewContainer;

                for (i = 0; i < pdfDocument.numPages; i += 1) {
                    pdfDocument.getPage(i + 1).then(createPage);
                }
            });
        });
    };

    this.isSlideshow = function () {
        return isGuessedSlideshow;
    };

    this.onLoad = function () {};

    this.getPages = function () {
        return domPages;
    };

    this.fitToWidth = function (width) {
        var zoomLevel;

        if (maxPageWidth === width) {
            return;
        }
        zoomLevel = width / maxPageWidth;
        self.setZoomLevel(zoomLevel);
    };

    this.fitToHeight = function (height) {
        var zoomLevel;

        if (maxPageHeight === height) {
            return;
        }
        zoomLevel = height / maxPageHeight;
        self.setZoomLevel(zoomLevel);
    };

    this.fitToPage = function (width, height) {
        var zoomLevel = width / maxPageWidth;
        if (height / maxPageHeight < zoomLevel) {
            zoomLevel = height / maxPageHeight;
        }
        self.setZoomLevel(zoomLevel);
    };

    this.fitSmart = function (width, height) {
        var zoomLevel = width / maxPageWidth;
        if (height && (height / maxPageHeight) < zoomLevel) {
            zoomLevel = height / maxPageHeight;
        }
        zoomLevel = Math.min(1.0, zoomLevel);
        self.setZoomLevel(zoomLevel);
    };

    this.setZoomLevel = function (zoomLevel) {
        var i, viewport;

        if (scale !== zoomLevel) {
            scale = zoomLevel;

            for (i = 0; i < pages.length; i += 1) {
                viewport = pages[i].getViewport(scale);
                updatePageDimensions(pages[i], viewport.width, viewport.height);
            }
        }
    };

    this.getZoomLevel = function () {
        return scale;
    };

    this.onScroll = function () {
        var i;

        for (i = 0; i < domPages.length; i += 1) {
            if (isScrolledIntoView(domPages[i])) {
                ensurePageRendered(pages[i]);
            }
        }
    };

    this.getPageInView = function () {
        var i;

        if (self.isSlideshow()) {
            return currentPage;
        } else {
            for (i = 0; i < domPages.length; i += 1) {
                if (isScrolledIntoView(domPages[i])) {
                    return i + 1;
                }
            }
        }
    };

    this.showPage = function (n) {
        if (self.isSlideshow()) {
            domPages[currentPage - 1].style.display = "none";
            currentPage = n;
            ensurePageRendered(pages[n - 1]);
            domPages[n - 1].style.display = "block";
        } else {
            scrollIntoView(domPages[n - 1]);
        }
    };

    this.getPluginName = function () {
        return "PDF.js"
    };

    this.getPluginVersion = function () {
        var version = (String(typeof pdfjs_version) !== "undefined"
            ? pdfjs_version
            : "From Source"
        );
        return version;
    };

    this.getPluginURL = function () {
        return "https://github.com/mozilla/pdf.js/";
    };
}
