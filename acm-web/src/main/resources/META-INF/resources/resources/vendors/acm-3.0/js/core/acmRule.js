Acm.Rule = {
    initialize: function() {
    }

    ,isAlpha: function (value) {
        return /^([-a-z])+$/i.test(value);
    }
    ,isAlphaWithAnySpace: function (value) {
        return /^([-a-z\s])+$/i.test(value);
    }
    ,isAlphaNumeric: function (value) {
        return /^([-a-z0-9])+$/i.test(value);
    }
    ,isAlphaNumericSpace: function (value) {
        return /^([-a-z0-9\s])+$/i.test(value);
    }
    ,isNumeric: function (value) {
        return /^([0-9])+$/i.test(value);
    }
/*
    validateCase: function (caseNumber, errors) {
        if(undefined !== caseNumber && caseNumber.length > 0) {
            if (!((caseNumber.length == 8 || caseNumber.length == 10) && Acm.Validation.isAlphaNumeric(caseNumber))) {
                errors.push("Case Number is invalid. Must be either 8 or 10 characters and alpha-numeric only.");
            }
        }
    },

    validateEqipId: function (eqipNumber, errors) {
        if(eqipNumber !== undefined && eqipNumber.length > 0) {
            if (!Acm.Validation.isAlphaNumeric(eqipNumber)) {
                errors.push("e-QIP ID is invalid.  Must be alpha-numeric only");
            }
        }
    },

    validateLastName: function (lastName, errors) {
        if(undefined !== lastName && lastName.length > 0) {
            //if (!Acm.Validation.isAlphaWithAnySpace(lastName)) {
            if (!Acm.Validation.isAlphaNumericSpace(lastName)) {
                errors.push("Last Name must be alpha-numeric");
            }
        }
    },
    validateNamePart: function (part, fieldName, errors) {
        if(undefined !== part && part.length > 0) {
            //if (!Acm.Validation.isAlphaWithAnySpace(part)) {
            if (!Acm.Validation.isAlphaNumericSpace(part)) {
                errors.push(fieldName + " must be alpha-numeric");
            }
        }
    },

    validateFullName : function(fullName, errors) {
        //if(fullName.length > 0 && !this.isAlphaWithAnySpace(fullName)) {
        if(fullName.length > 0 && !this.isAlphaNumericSpace(fullName)) {
            errors.push("Full Name must be alpha-numeric only (with spaces)");
        }
    },
    validateDob : function(dob, errors) {
        if(dob.length > 0 ) {
            var validDateFormat = this._getCorrectDateFormat(dob);
            if(null == validDateFormat || null == Date.parse(validDateFormat)) {
                errors.push("DOB is not a valid date");
            }
        }
    },
    validateSsn: function(ssn, errors) {
        if(undefined !== ssn && ssn.length > 0) {
            if(ssn.length != 9 || !this.isNumeric(ssn)) {
                errors.push("SSN must be a 9 digit number");
            }
        }
    },
    showValidationErrorAlert : function(msg, errors, title, pos) {
        if(errors.length > 0) {
            var len = errors.length;
            for(var i = 0; i < len; i++) {
                var newMsg = "* " + errors[i];
                errors[i] = newMsg;
            }
        }

//jwu pos info is not shown with Acm.Popup
//        if(undefined !== pos) {
//            Acm.Alert.showErrorDialogPositioned(msg, errors.join('<br>'), title, pos);
//        } else {
//            Acm.Alert.showErrorDialog(msg, errors.join('<br>'), title, pos);
//        }
          Acm.Popup.error2(msg, errors.join('<br>'), Acm.Popup.NO_CALLBACK, title);

    },
    _getCorrectDateFormat : function(val) {
        var dateParts = val.split("/");
        var newDateStr = null;
        if(dateParts.length == 3) {
            if(dateParts[2].length == 4 && dateParts[0].length == 2 && dateParts[1].length == 2) {
                newDateStr = dateParts[2] + "/" + dateParts[0] + "/" + dateParts[1];
            }
        }
        return newDateStr;
    }
*/
}