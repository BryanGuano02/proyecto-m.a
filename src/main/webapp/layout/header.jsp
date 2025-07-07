<%-- Created by IntelliJ IDEA. User: alejo Date: 7/5/2025 Time: 18:47 To change this template use File | Settings | File
    Templates. --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>

<head>
    <title>Title</title>
</head>

<body>
<% String titulo = (String) request.getAttribute("titulo");
    Boolean botonAtras = (Boolean)
            request.getAttribute("botonAtrás"); %>
<div class="d-flex justify-content-between align-items-center mb-4">
    <div class="header-title">
        <h2 class="text-primary">
            <%=titulo%>
        </h2>
        <c:if test="${botonAtras}">
            <a href="javascript:history.back()"
               class="btn btn-sm btn-outline-secondary me-3 d-flex align-items-center">
                <i class="fas fa-arrow-left"></i> Atrás
            </a>
        </c:if>
    </div>
    <div class="d-flex align-items-center">
        <c:choose>
            <c:when test="${not empty sessionScope.usuario}">
                <img src="https://ui-avatars.com/api/?name=${sessionScope.usuario.nombreUsuario}&background=ff6b6b&color=fff"
                     alt="Usuario" class="rounded-circle me-2" width="40">
                <span class="fw-bold">${sessionScope.usuario.nombreUsuario}</span>
                <a href="${pageContext.request.contextPath}/logout"
                   class="btn btn-sm btn-outline-danger ms-3">
                    <i class="fas fa-sign-out-alt"></i> Salir
                </a>
            </c:when>
            <c:otherwise>
                <a href="${pageContext.request.contextPath}/login.jsp"
                   class="btn btn-sm btn-primary">
                    <i class="fas fa-sign-in-alt"></i> Iniciar sesión
                </a>
            </c:otherwise>
        </c:choose>
    </div>
</div>

</body>

</html>
