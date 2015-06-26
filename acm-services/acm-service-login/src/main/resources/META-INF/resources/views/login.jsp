<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<!DOCTYPE html>
<html lang="en" class="app">
<head>
    <meta charset="utf-8" />
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <%--<%@include file="/WEB-INF/tagf/global.tagf" %>--%>
    <%@include file="/resources/include/global.jspf" %>
    <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_acm}/js/app/app.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_acm}/js/app/appModel.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_acm}/js/app/appView.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_acm}/js/app/appController.js'/>"></script>

    <%--<title><spring:message code="login.page.title" text="ACM | ArkCase" /></title>--%>
    <title data-i18n="login:page-title">ACM | ArkCase</title>
    <div id="acmData" itemscope="true" style="display: none">
        <span itemprop="contextPath"><%=request.getContextPath()%></span>
        <span itemprop="resourceNamespace">login</span>
    </div>
</head>
<body>
<section id="content" class="m-t-lg wrapper-md animated fadeInUp">
    <div class="container aside-xl ">
        <a class="navbar-brand block" href="portal.html"><img src="<c:url value='/branding/loginlogo.png'/>" /></a>
        <section class="m-b-lg">
            <header class="wrapper text-center">
                <strong data-i18n="login:instruction">Enter your username and password.</strong>
            </header>
            <p/>
            <c:if test="${not empty param.login_error}">
                <div class="error">
                    <span data-i18n="login:fail-message">"Your login attempt was not successful, try again.</span><br />
                    <span data-i18n="login:fail-reason">Reason</span>: ${sessionScope.SPRING_SECURITY_LAST_EXCEPTION.message}
                </div>
            </c:if>

            <form action="<%= request.getContextPath()%>/j_spring_security_check" method="post">
                <div class="list-group">
                    <div class="list-group-item">
                        <c:if test="${not empty param.login_error}">
                            <c:set var="username" value="${sessionScope.SPRING_SECURITY_LAST_USERNAME}"/>
                        </c:if>
                        <input type="text" name="j_username" value="${username}" data-i18n="[placeholder]login:placeholder-username" placeholder="Username" class="form-control no-border"/>
                    </div>
                    <div class="list-group-item">
                        <input type="password" name="j_password" data-i18n="[placeholder]login:placeholder-password" placeholder="Password" class="form-control no-border"/>
                    </div>
                </div>
                <button type="submit" class="btn btn-lg btn-primary btn-block" data-i18n="login:login-button">Log In</button>
                <div class="text-center m-t m-b"><a href="#"><small data-i18n="login:forgot-password">Forgot password?</small></a></div>

            </form>
        </section>
    </div>
</section>


<!-- footer -->
<footer id="footer">
    <div class="text-center padder">
        <p>
            <small><span data-i18n="login:footer">ArkCase</span> <br>&copy; <span data-i18n="login:copy-right">2014, 2015</span></small>
            <%--<small>ArkCase <br>&copy; 2014, 2015</small>--%>
        </p>
    </div>
</footer>

<script src="<c:url value='/resources/vendors/${vd_slimscroll}/${js_slimscroll}'/>"></script>
<script type="text/javascript" src="<c:url value='/resources/js/login/login.js'/>"></script>
<%--<script type="text/javascript" src="<c:url value='/resources/js/login/loginView.js'/>"></script>--%>
<script type="text/javascript">
    jQuery(document).ready(function() {
        var context = App.getPageContext();
        context.loginPage = true;
        ThisApp.run(context);
    });
</script>
</body>
</html>
