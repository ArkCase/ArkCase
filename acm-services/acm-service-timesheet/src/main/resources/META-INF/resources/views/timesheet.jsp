<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<t:layout>
	<jsp:attribute name="endOfHead">
	    <title><spring:message code="timesheet.page.title" text="Time Tracking | ACM | ArkCase" /></title>
	</jsp:attribute>

    <jsp:attribute name="endOfBody">
        <script type="text/javascript" src="<c:url value='/resources/js/timesheet/timesheet.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/resources/js/timesheet/timesheetModel.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/resources/js/timesheet/timesheetService.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/resources/js/timesheet/timesheetView.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/resources/js/timesheet/timesheetController.js'/>"></script>
    </jsp:attribute>

    <jsp:body>
        <section id="content">
            <section class="vbox">
                <section class="scrollable padder">
                    <section class="row m-b-md">
                        <div class="col-sm-12">
                            <h3 class="m-b-xs text-black"><spring:message code="timesheet.page.descShort" text="Track Time" /></h3>
                        </div>
                    </section>

                    <div class="row">
                        <div class="col-sm-12">
                        </div>
                    </div>

                </section>
            </section>
        </section>
    </jsp:body>
</t:layout>