/**
 * Report.Rule
 *
 * manages rules govern interaction between elements in page
 *
 * @author jwu
 */
Report.Rule = {
    initialize : function() {
    }

	,validateCaseNumber : function(caseNumber) {

		if ( undefined === caseNumber || caseNumber === "" || caseNumber === "Case Number" ) {
			return false;
		}
		else {
			return true;
		}
	}
};

