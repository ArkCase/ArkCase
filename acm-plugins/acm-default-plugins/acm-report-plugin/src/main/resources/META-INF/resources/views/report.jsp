<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<t:layout>
<jsp:attribute name="endOfHead">
    <title><spring:message code="report.page.title" text="Report | ACM | Armedia Case Management" /></title>
    <div id="detailData" itemscope="true" style="display: none">
        <span itemprop="resourceNamespace">report</span>
    </div>
</jsp:attribute>


<jsp:attribute name="endOfBody">
    <script type="text/javascript" src="<c:url value='/resources/js/report/report.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/report/reportObject.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/report/reportEvent.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/report/reportPage.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/report/reportRule.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/report/reportService.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/report/reportCallback.js'/>"></script>

    <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_slimscroll}/${js_slimscroll}'/>"></script>

</jsp:attribute>

<jsp:body>
    <section class="hbox stretch">
        <aside class="aside-md bg-light dker b-r" id="subNav">
            <section class="scrollable">
                <div class="wrapper b-b header" data-i18n="report:navigation.title">Report</div>
                <div class="wrapper">
                    <label for="selectReport"  class="label" data-i18n="report:navigation.label.report">Report</label>
                    <select name="priority" class="form-control" id ="selectReport">
                        <option data-i18n="report:navigation.label.choose-report">Choose Report</option>
                        <c:forEach items="${reportUrlsMap}" var="entry">
                            <option title="${entry.key}" value="${entry.value}">${entry.key}</option>
                        </c:forEach>
                    </select>

                    <section id="caseNumberSection">
                        <div class="line line-dashed b-b line-lg pull-in"></div>
                        <label for="caseNumber"  class="label" id="caseNumberlbl" data-i18n="report:navigation.label.case-number">Case Number</label>
                        <input id="caseNumber" type="text" class="form-control" data-i18n="[placeholder]report:navigation.label.report" placeholder="Case Number"  >
                    </section>

                    <section id="caseStatusSection">
                        <div class="line line-dashed b-b line-lg pull-in"></div>
                        <label for="selectCaseStatus"  class="label" id="caseStatuslbl">State</label>
                        <select name="cStatus" class="form-control" id ="selectCaseStatus">
                            <option value="Choose State" data-i18n="report:navigation.label.choose-state">Choose State</option>
                            <%--<option value="ACTIVE">Active</option>
                            <option value="APPROVED">Approved</option>--%>
                            <option value="DRAFT">DRAFT</option>
                            <option value="IN APPROVAL">IN APPROVAL</option>
                            <option value="CLOSED">CLOSED</option>
                            <option value="INACTIVE">INACTIVE</option>
                            <option value="ACTIVE">ACTIVE</option>
                            <%--<option value="IN APPROVAL">In Approval</option>   &ndash;%&gt;--%>
    <%--                                                 <c:forEach items="${caseStatusMap}" var="entry">
                                                        <option title="${entry.key}" value="${entry.value}">${entry.key}</option>
                                                    </c:forEach>
     --%>
                        </select>
                    </section>

                    <section id="datepickerSection">
                        <div class="line line-dashed b-b line-lg pull-in"></div>
                        <label for="startDate"  class="label" data-i18n="report:navigation.label.date-range">Date Range</label>
                        <div class="clearfix"></div>
                        <label class="label col-sm-3" data-i18n="report:navigation.label.date-from">From</label>
                        <div class="col-sm-9">
                            <input class="datepicker-input form-control" type="text" id = "startDate" value= "" data-date-format="dd-mm-yyyy" >
                        </div>

                        <label class="label col-sm-3" data-i18n="report:navigation.label.date-to">To</label>
                        <div class="col-sm-9">

                            <input class="datepicker-input form-control" type="text" id = "endDate" value= "" data-date-format="dd-mm-yyyy" >
                        </div>
                    </section>

                    <div class="clearfix"></div>
                    <section id="reportSubmitSection">
                        <div class="line line-dashed b-b line-lg pull-in"></div>
                        <button class="btn btn-sm btn-default left-block" type="button" id="generateReport" data-i18n="report:navigation.label.generate-report">Generate Report</button>
                    </section>
                </div>
            </section>
        </aside>
        <aside>
            <label for="Report message" id="ReportMeassge" class="class="m-b-xs text-black pull-left" data-i18n="report:navigation.label.report-message">To run a report, select a report name, enter criteria, and click Generate Report</label>
            <iframe src="" name="report_iframe" style="width:100%; height:100%;" frameborder="0"></iframe>
        </aside>
    </section>
</jsp:body>
</t:layout>


