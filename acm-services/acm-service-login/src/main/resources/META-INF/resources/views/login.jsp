<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<!DOCTYPE html>
<html lang="en" class="app">
<head>
    <meta charset="utf-8" />
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <%@include file="/WEB-INF/tagf/global.tagf" %>
    <title><spring:message code="login.page.title" text="ACM | ArkCase" /></title>
</head>
<body>
    <section id="content" class="m-t-lg wrapper-md animated fadeInUp">
        <div class="container aside-xl ">
            <a class="navbar-brand block" href="portal.html"><img src="<c:url value='/resources/vendors/${acm_theme}/images/logo.png'/>" /></a>
            <section class="m-b-lg">
                <header class="wrapper text-center">
                    <strong><spring:message code="login.instruction" text="Enter your username and password." /></strong>
                </header>
                <p/>
                <c:if test="${not empty param.login_error}">
                    <div class="error">
                        <spring:message code="login.fail.message" text="Your login attempt was not successful, try again." /><br />
                        <spring:message code="login.fail.reason" text="Reason" />: ${sessionScope.SPRING_SECURITY_LAST_EXCEPTION.message}
                    </div>
                </c:if>

                <form action="<%= request.getContextPath()%>/j_spring_security_check" method="post">
                    <div class="list-group">
                        <div class="list-group-item">
                            <c:if test="${not empty param.login_error}">
                                <c:set var="username" value="${sessionScope.SPRING_SECURITY_LAST_USERNAME}"/>
                            </c:if>
                            <input type="text" name="j_username" value="${username}" placeholder="Username" class="form-control no-border"/>
                        </div>
                        <div class="list-group-item">
                            <input type="password" name="j_password" placeholder="Password" class="form-control no-border"/>
                        </div>
                    </div>
                    <button type="submit" class="btn btn-lg btn-primary btn-block"><spring:message code="login.button.label" text="Log In" /></button>
                    <div class="text-center m-t m-b"><a href="#"><small><spring:message code="login.forgot.password" text="Forgot password?" /></small></a></div>

                </form>
            </section>
        </div>
    </section>


    <!-- footer -->
    <footer id="footer">
        <div class="text-center padder">
            <p>
                <small><spring:message code="login.footer" text="ArkCase <br>&copy; 2014, 2015" /></small>
            </p>
        </div>
    </footer>

    <script src="<c:url value='/resources/vendors/${vd_slimscroll}/${js_slimscroll}'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/login/login.js'/>"></script>
    <script type="text/javascript">
        jQuery(document).ready(function() {
            Application.run();
        });
    </script>
</body>
</html>
