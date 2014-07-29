<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout>
<jsp:attribute name="endOfHead">
    <title>${pageDescriptor.title}</title>
</jsp:attribute>


<jsp:attribute name="endOfBody">
    <script type="text/javascript" src="<c:url value='/resources/js/report/report.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/report/reportObject.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/report/reportEvent.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/report/reportPage.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/report/reportRule.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/report/reportService.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/report/reportCallback.js'/>"></script>

    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_slimscroll}/jquery.slimscroll.min.js"></script>

</jsp:attribute>

<jsp:body>
    <section id="content">
        <section class="vbox">
            <section class="scrollable padder">
                <section class="row m-b-md">
                    <div class="col-sm-12">
                        <h3 class="m-b-xs text-black">Report</h3>
                    </div>
                </section>



                test
                <input type="button" value="Test" id="test" />




            </section>

        </section>
    </section>
</jsp:body>
</t:layout>


