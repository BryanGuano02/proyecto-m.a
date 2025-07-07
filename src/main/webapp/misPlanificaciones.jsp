<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>

<head>
    <title>Mis Planificaciones</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600&display=swap"
          rel="stylesheet">
    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="styles.css">
</head>

<body>
<div class="container mt-5">

    <% request.setAttribute("titulo", "Mis Planificaciones");
        request.setAttribute("botonAtras", true); %>
    <%@ include file="layout/header.jsp" %>

    <!-- Tabla de planificaciones en las que participo -->
    <div class="card shadow mb-4 mt-4">
        <div class="card-body">
            <h4 class="mb-3"><i class="fas fa-calendar-alt me-2"></i>Planificaciones en las que participo</h4>
            <c:choose>
                <c:when test="${empty planificaciones}">
                    <div class="alert alert-warning">No participa en ninguna planificación.</div>
                </c:when>
                <c:otherwise>
                    <div class="table-responsive">
                        <table class="table table-bordered align-middle">
                            <thead class="table-light">
                            <tr>
                                <th>ID</th>
                                <th>Nombre</th>
                                <th>Hora</th>
                                <th>Organizador</th>
                                <th>Estado</th>
                                <th>Votación</th>
                                <th>Acciones</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="plan" items="${planificaciones}">
                                <tr>
                                    <td>${plan.id}</td>
                                    <td>
<%--                                        <a href="${pageContext.request.contextPath}/detallePlanificacion?id=${plan.id}" class="text-decoration-none">--%>
<%--                                            ${plan.nombre}--%>
<%--                                        </a>--%>
                                        <span>${plan.nombre}</span>
                                    </td>
                                    <td>${plan.hora}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${plan.comensalPlanificador.id eq sessionScope.usuario.id}">
                                                <span class="badge bg-primary">Usted</span>
                                            </c:when>
                                            <c:otherwise>
                                                ${plan.comensalPlanificador.nombreUsuario}
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <span class="badge ${plan.estado == 'Activa' ? 'bg-success' : (plan.estado == 'Terminada' ? 'bg-info' : 'bg-secondary')}">
                                            ${plan.estado}
                                        </span>
                                    </td>
                                    <td>
                                        <span class="badge ${plan.estadoVotacion == 'En progreso' ? 'bg-warning' : (plan.estadoVotacion == 'Terminada' ? 'bg-info' : 'bg-secondary')}">
                                            ${plan.estadoVotacion}
                                        </span>
                                    </td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/detallePlanificacion?id=${plan.id}"
                                           class="btn btn-sm btn-primary me-1">
                                            <i class="fas fa-eye"></i> Ver Detalles
                                        </a>
<%--                                        <c:if test="${plan.estadoVotacion == 'En progreso'}">--%>
<%--                                            <a href="${pageContext.request.contextPath}/detallePlanificacion?id=${plan.id}#votacion"--%>
<%--                                               class="btn btn-sm btn-warning">--%>
<%--                                                <i class="fas fa-vote-yea"></i> Votar--%>
<%--                                            </a>--%>
<%--                                        </c:if>--%>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>

</html>
