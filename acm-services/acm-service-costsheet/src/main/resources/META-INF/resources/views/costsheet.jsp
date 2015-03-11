<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<t:layout>
	<jsp:attribute name="endOfHead">
	    <title><spring:message code="costsheetNew.page.title" text="Time Tracking | ACM | ArkCase" /></title>
        <div id="detailData" itemscope="true" style="display: none">
            <span itemprop="newCostsheetFormUrl">${newCostsheetFormUrl}</span>
        </div>
	</jsp:attribute>

    <jsp:attribute name="endOfBody">
        <script type="text/javascript" src="<c:url value='/resources/js/costsheet/costsheet.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/resources/js/costsheet/costsheetModel.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/resources/js/costsheet/costsheetService.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/resources/js/costsheet/costsheetView.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/resources/js/costsheet/costsheetController.js'/>"></script>
    </jsp:attribute>

    <jsp:body>
        <section id="content">
            <section class="vbox">
                <section class="scrollable padder">
                    <section class="row m-b-md">
                        <div class="col-sm-12">
                            <h3 class="m-b-xs text-black"><spring:message code="costsheet.page.descShort" text="Cost Tracking" /></h3>
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