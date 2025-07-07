<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>

<head>
    <title>Crear Planificación</title>
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

    <% request.setAttribute("titulo", "Crear Nueva Planificación");
        request.setAttribute("botonAtras",
                true); %>
    <%@ include file="layout/header.jsp" %>

    <div class="card shadow mb-4">
        <div class="card-body">
            <h3 class="mb-4"><i class="fas fa-calendar-plus me-2"></i>Crear Nueva Planificación</h3>
            <form action="${pageContext.request.contextPath}/planificar" method="POST">
                <input type="hidden" name="idComensalPlanificador" id="idComensalPlanificador" value=${sessionScope.usuario.id}>

                <c:if test="${not empty mensaje}">
                    <div class="alert alert-info">${mensaje}</div>
                </c:if>
                <div class="mb-3">
                    <label for="nombre" class="form-label">Nombre de la planificación</label>
                    <input type="text" class="form-control" id="nombre" name="nombre" required>
                </div>
                <div class="mb-3">
                    <label for="hora" class="form-label">Hora</label>
                    <input type="time" class="form-control" id="hora" name="hora" required>
                </div>
                <button type="submit" class="btn btn-primary w-100 py-2">
                    <i class="fas fa-plus me-2"></i>Crear
                </button>
            </form>
        </div>
    </div>

    <!-- Tabla de planificaciones existentes -->
    <div class="card shadow mb-4 mt-4">
        <div class="card-body">
            <h4 class="mb-3"><i class="fas fa-list me-2"></i>Planificaciones Creadas</h4>
            <c:choose>
                <c:when test="${empty planificaciones}">
                    <div class="alert alert-warning">No hay planificaciones registradas.</div>
                </c:when>
                <c:otherwise>
                    <div class="table-responsive">
                        <table class="table table-bordered align-middle">
                            <thead class="table-light">
                            <tr>
                                <th>ID</th>
                                <th>Nombre</th>
                                <th>Hora</th>
                                <th>Estado</th>
                                <th>Acciones</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="plan" items="${planificaciones}">                                        <tr>
                                    <td>${plan.id}</td>
                                    <td>
<%--                                        <a href="${pageContext.request.contextPath}/detallePlanificacion?id=${plan.id}" class="text-decoration-none">--%>
<%--                                            ${plan.nombre}--%>
<%--                                        </a>--%>
                                        <span>${plan.nombre}</span>
                                    </td>
                                    <td>${plan.hora}</td>
                                    <td>
                                        <span class="badge ${plan.estado == 'Activa' ? 'bg-success' : (plan.estado == 'Terminada' ? 'bg-info' : 'bg-secondary')}">
                                            ${plan.estado}
                                        </span>
                                    </td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/detallePlanificacion?id=${plan.id}"
                                           class="btn btn-sm btn-primary me-1">
                                            <i class="fas fa-eye"></i> Ver Detalles
                                        </a>
                                        <c:if test="${plan.estado == 'Activa'}">
                                            <a href="${pageContext.request.contextPath}/terminarVotacion?id=${plan.id}"
                                               class="btn btn-sm btn-warning">
                                                <i class="fas fa-check"></i> Terminar Votación
                                            </a>
                                        </c:if>
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
