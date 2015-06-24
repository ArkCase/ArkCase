
import * as helper from './../helpers.js';
import * as dom from './../dom.js';
import {getEditor, registerEditor} from './../editors.js';
import {BaseEditor} from './_baseEditor.js';
import {eventManager as eventManagerObject} from './../eventManager.js';

var
  MobileTextEditor = BaseEditor.prototype.extend(),
  domDimensionsCache = {};

export {MobileTextEditor};

Handsontable.editors = Handsontable.editors || {};

/**
 * @private
 * @editor
 * @class MobileTextEditor
 */
Handsontable.editors.MobileTextEditor = MobileTextEditor;

var createControls = function () {
  this.controls = {};

  this.controls.leftButton = document.createElement('DIV');
  this.controls.leftButton.className = 'leftButton';
  this.controls.rightButton = document.createElement('DIV');
  this.controls.rightButton.className = 'rightButton';
  this.controls.upButton = document.createElement('DIV');
  this.controls.upButton.className = 'upButton';
  this.controls.downButton = document.createElement('DIV');
  this.controls.downButton.className = 'downButton';

  for (var button in this.controls) {
    if (this.controls.hasOwnProperty(button)) {
      this.positionControls.appendChild(this.controls[button]);
    }
  }
};

MobileTextEditor.prototype.valueChanged = function() {
  return this.initValue != this.getValue();
};

MobileTextEditor.prototype.init = function() {
  var that = this;
  this.eventManager = eventManagerObject(this.instance);

  this.createElements();
  this.bindEvents();

  this.instance.addHook('afterDestroy', function() {
    that.destroy();
  });

};

MobileTextEditor.prototype.getValue = function() {
  return this.TEXTAREA.value;
};

MobileTextEditor.prototype.setValue = function(newValue) {
  this.initValue = newValue;

  this.TEXTAREA.value = newValue;
};

MobileTextEditor.prototype.createElements = function() {
  this.editorContainer = document.createElement('DIV');
  this.editorContainer.className = "htMobileEditorContainer";

  this.cellPointer = document.createElement('DIV');
  this.cellPointer.className = "cellPointer";

  this.moveHandle = document.createElement('DIV');
  this.moveHandle.className = "moveHandle";

  this.inputPane = document.createElement('DIV');
  this.inputPane.className = "inputs";

  this.positionControls = document.createElement('DIV');
  this.positionControls.className = "positionControls";

  this.TEXTAREA = document.createElement('TEXTAREA');
  dom.addClass(this.TEXTAREA, 'handsontableInput');

  this.inputPane.appendChild(this.TEXTAREA);

  this.editorContainer.appendChild(this.cellPointer);
  this.editorContainer.appendChild(this.moveHandle);
  this.editorContainer.appendChild(this.inputPane);
  this.editorContainer.appendChild(this.positionControls);

  createControls.call(this);

  document.body.appendChild(this.editorContainer);
};

MobileTextEditor.prototype.onBeforeKeyDown = function(event) {
  var instance = this;
  var that = instance.getActiveEditor();

  dom.enableImmediatePropagation(event);

  if (event.target !== that.TEXTAREA || event.isImmediatePropagationStopped()) {
    return;
  }

  var keyCodes = helper.keyCode;

  switch (event.keyCode) {
    case keyCodes.ENTER:
      that.close();
      event.preventDefault(); //don't add newline to field
      break;
    case keyCodes.BACKSPACE:
      event.stopImmediatePropagation(); //backspace, delete, home, end should only work locally when cell is edited (not in table context)
      break;
  }
};

MobileTextEditor.prototype.open = function() {
  this.instance.addHook('beforeKeyDown', this.onBeforeKeyDown);

  dom.addClass(this.editorContainer, 'active');
  dom.removeClass(this.cellPointer, 'hidden');

  this.updateEditorPosition();
};

MobileTextEditor.prototype.focus = function() {
  this.TEXTAREA.focus();
  dom.setCaretPosition(this.TEXTAREA, this.TEXTAREA.value.length);
};

MobileTextEditor.prototype.close = function() {
  this.TEXTAREA.blur();
  this.instance.removeHook('beforeKeyDown', this.onBeforeKeyDown);

  dom.removeClass(this.editorContainer, 'active');
};

MobileTextEditor.prototype.scrollToView = function() {
  var coords = this.instance.getSelectedRange().highlight;
  this.instance.view.scrollViewport(coords);
};

MobileTextEditor.prototype.hideCellPointer = function() {
  if (!dom.hasClass(this.cellPointer, 'hidden')) {
    dom.addClass(this.cellPointer, 'hidden');
  }
};

MobileTextEditor.prototype.updateEditorPosition = function(x, y) {
  if (x && y) {
    x = parseInt(x, 10);
    y = parseInt(y, 10);

    this.editorContainer.style.top = y + "px";
    this.editorContainer.style.left = x + "px";

  } else {
    var selection = this.instance.getSelected(),
      selectedCell = this.instance.getCell(selection[0], selection[1]);

    //cache sizes
    if (!domDimensionsCache.cellPointer) {
      domDimensionsCache.cellPointer = {
        height: dom.outerHeight(this.cellPointer),
        width: dom.outerWidth(this.cellPointer)
      };
    }
    if (!domDimensionsCache.editorContainer) {
      domDimensionsCache.editorContainer = {
        width: dom.outerWidth(this.editorContainer)
      };
    }

    if (selectedCell !== undefined) {
      var scrollLeft = this.instance.view.wt.wtOverlays.leftOverlay
          .trimmingContainer == window ? 0 : dom.getScrollLeft(this.instance.view.wt.wtOverlays.leftOverlay.holder);
      var scrollTop = this.instance.view.wt.wtOverlays.topOverlay
          .trimmingContainer == window ? 0 : dom.getScrollTop(this.instance.view.wt.wtOverlays.topOverlay.holder);

      var selectedCellOffset = dom.offset(selectedCell),
        selectedCellWidth = dom.outerWidth(selectedCell),
        currentScrollPosition = {
          x: scrollLeft,
          y: scrollTop
        };

      this.editorContainer.style.top = parseInt(selectedCellOffset.top + dom.outerHeight(selectedCell) -
          currentScrollPosition.y + domDimensionsCache.cellPointer.height, 10) + "px";
      this.editorContainer.style.left = parseInt((window.innerWidth / 2) - (domDimensionsCache.editorContainer.width / 2), 10) + "px";

      if (selectedCellOffset.left + selectedCellWidth / 2 > parseInt(this.editorContainer.style.left, 10) +
          domDimensionsCache.editorContainer.width) {
        this.editorContainer.style.left = window.innerWidth - domDimensionsCache.editorContainer.width + "px";

      } else if (selectedCellOffset.left + selectedCellWidth / 2 < parseInt(this.editorContainer.style.left, 10) + 20) {
        this.editorContainer.style.left = 0 + "px";
      }

      this.cellPointer.style.left = parseInt(selectedCellOffset.left - (domDimensionsCache.cellPointer.width / 2) -
        dom.offset(this.editorContainer).left + (selectedCellWidth / 2) - currentScrollPosition.x, 10) + "px";
    }
  }
};


// For the optional dont-affect-editor-by-zooming feature:

//MobileTextEditor.prototype.updateEditorDimensions = function () {
//  if(!this.beginningWindowWidth) {
//    this.beginningWindowWidth = window.innerWidth;
//    this.beginningEditorWidth = Handsontable.Dom.outerWidth(this.editorContainer);
//    this.scaleRatio = this.beginningEditorWidth / this.beginningWindowWidth;
//
//    this.editorContainer.style.width = this.beginningEditorWidth + "px";
//    return;
//  }
//
//  var currentScaleRatio = this.beginningEditorWidth / window.innerWidth;
//  //if(currentScaleRatio > this.scaleRatio + 0.2 || currentScaleRatio < this.scaleRatio - 0.2) {
//  if(currentScaleRatio != this.scaleRatio) {
//    this.editorContainer.style["zoom"] = (1 - ((currentScaleRatio * this.scaleRatio) - this.scaleRatio)) * 100 + "%";
//  }
//
//};

MobileTextEditor.prototype.updateEditorData = function() {
  var selected = this.instance.getSelected(),
    selectedValue = this.instance.getDataAtCell(selected[0], selected[1]);

  this.row = selected[0];
  this.col = selected[1];
  this.setValue(selectedValue);
  this.updateEditorPosition();
};

MobileTextEditor.prototype.prepareAndSave = function() {
  var val;

  if (!this.valueChanged()) {
    return true;
  }

  if (this.instance.getSettings().trimWhitespace) {
    val = [
      [String.prototype.trim.call(this.getValue())]
    ];
  } else {
    val = [
      [this.getValue()]
    ];
  }


  this.saveValue(val);
};

MobileTextEditor.prototype.bindEvents = function() {
  var that = this;

  this.eventManager.addEventListener(this.controls.leftButton, "touchend", function(event) {
    that.prepareAndSave();
    that.instance.selection.transformStart(0, - 1, null, true);
    that.updateEditorData();
    event.preventDefault();
  });
  this.eventManager.addEventListener(this.controls.rightButton, "touchend", function(event) {
    that.prepareAndSave();
    that.instance.selection.transformStart(0, 1, null, true);
    that.updateEditorData();
    event.preventDefault();
  });
  this.eventManager.addEventListener(this.controls.upButton, "touchend", function(event) {
    that.prepareAndSave();
    that.instance.selection.transformStart(-1, 0, null, true);
    that.updateEditorData();
    event.preventDefault();
  });
  this.eventManager.addEventListener(this.controls.downButton, "touchend", function(event) {
    that.prepareAndSave();
    that.instance.selection.transformStart(1, 0, null, true);
    that.updateEditorData();
    event.preventDefault();
  });

  this.eventManager.addEventListener(this.moveHandle, "touchstart", function(event) {
    if (event.touches.length == 1) {
      var touch = event.touches[0],
        onTouchPosition = {
          x: that.editorContainer.offsetLeft,
          y: that.editorContainer.offsetTop
        }, onTouchOffset = {
          x: touch.pageX - onTouchPosition.x,
          y: touch.pageY - onTouchPosition.y
        };

      that.eventManager.addEventListener(this, "touchmove", function(event) {
        var touch = event.touches[0];
        that.updateEditorPosition(touch.pageX - onTouchOffset.x, touch.pageY - onTouchOffset.y);
        that.hideCellPointer();
        event.preventDefault();
      });

    }
  });

  this.eventManager.addEventListener(document.body, "touchend", function(event) {
    if (!dom.isChildOf(event.target, that.editorContainer) && !dom.isChildOf(event.target, that.instance.rootElement)) {
      that.close();
    }
  });

  this.eventManager.addEventListener(this.instance.view.wt.wtOverlays.leftOverlay.holder, "scroll", function(event) {
    if (that.instance.view.wt.wtOverlays.leftOverlay.trimmingContainer != window) {
      that.hideCellPointer();
    }
  });

  this.eventManager.addEventListener(this.instance.view.wt.wtOverlays.topOverlay.holder, "scroll", function(event) {
    if (that.instance.view.wt.wtOverlays.topOverlay.trimmingContainer != window) {
      that.hideCellPointer();
    }
  });

};

MobileTextEditor.prototype.destroy = function() {
  this.eventManager.clear();

  this.editorContainer.parentNode.removeChild(this.editorContainer);
};

registerEditor('mobile', MobileTextEditor);
