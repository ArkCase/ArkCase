<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<t:layout>
<jsp:attribute name="endOfHead">
    <title><spring:message code="subscription.page.title" text="Subscription | ACM | ArkCase" /></title>

    <div id="microData" itemscope="true" style="display: none">
        <span itemprop="resourceNamespace">subscription,search</span>
        <span itemprop="search.name">${searchName}</span>
        <span itemprop="search.filters">${searchFilters}</span>
    </div>
</script>
</jsp:attribute>


<jsp:attribute name="endOfBody">
    <script type="text/javascript" src="<c:url value='/resources/js/subscription/subscription.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/subscription/subscriptionModel.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/subscription/subscriptionService.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/subscription/subscriptionView.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/subscription/subscriptionController.js'/>"></script>

    <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_slimscroll}/jquery.slimscroll.min.js'/>"></script>

    <!-- JTable -->
    <link rel="stylesheet" href="<c:url value='/resources/vendors/${vd_acm}/themes/basic/${vd_jtable}/blue/jtable.css'/>" type="text/css"/>
    <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_jtable}/${js_jtable}'/>"></script>
</jsp:attribute>

<jsp:body>
    <header class="header bg-gradient b-b clearfix">
        <div class="row m-t-sm">
            <div class="col-md-12 m-b-sm">
                <div class="input-group">
                    <input type="text" class="input-md form-control" id="searchQuery" data-i18n="[placeholder]search:label.input-search" placeholder="Type in your search query to find complaints, cases, tasks, and documents.">
                  <span class="input-group-btn">
                  <button class="btn btn-md btn-default" type="button" data-i18n="search:button.go">Go!</button>
                  </span> </div>
            </div>
        </div>
    </header>

    <section class="hbox stretchSearch">
        <aside class="aside-md bg-light dker b-r" id="subNav">
            <section class="vbox">
                <section class="scrollable">

                    <div class="wrapper facets" id="divFacets">
                    </div>

                </section>
            </section>
        </aside>

        <aside>
            <section class="vbox">
                <section class="scrollable wrapper w-f">
                    <section class="panel panel-default">
                        <div id="divResults" style="width:100%"></div>
                    </section>
                </section>
            </section>
        </aside>
    </section>
</jsp:body>
</t:layout>


