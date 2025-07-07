<%@ page import="entidades.Restaurante" %>
<%@ page import="entidades.Calificacion" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html lang="es">

<head>
    <title>U-Food | Detalles de Restaurante</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="styles.css">
    <style>
        .restaurant-header {
            padding: 1.5rem;
            border-radius: 0.5rem;
            background-color: #f8f9fa;
            margin-bottom: 1.5rem;
        }

        .restaurant-name {
            font-size: 1.8rem;
            font-weight: 600;
            margin-bottom: 0.5rem;
        }

        .restaurant-info {
            margin-bottom: 1.5rem;
        }

        .info-card {
            background: #fff;
            padding: 1rem;
            border-radius: 0.5rem;
            box-shadow: 0 0.125rem 0.25rem rgba(0, 0, 0, 0.075);
            margin-bottom: 1rem;
        }

        .rating-stars {
            color: #ffc107;
            font-size: 1.2rem;
        }

        .info-label {
            font-weight: 500;
            margin-bottom: 0.25rem;
            color: #6c757d;
        }

        .calificacion-card {
            background: #fff;
            border-radius: 0.5rem;
            padding: 1rem 1.25rem 0.75rem 1.25rem;
            margin-bottom: 1rem;
            border-left: 4px solid #0d6efd;
            box-shadow: 0 0.08rem 0.18rem rgba(0, 0, 0, 0.06);
            transition: box-shadow 0.2s;
            position: relative;
        }

        .calificacion-card:hover {
            box-shadow: 0 0.25rem 0.5rem rgba(13, 110, 253, 0.10);
        }

        .calificacion-fecha {
            font-size: 0.85rem;
            color: #6c757d;
            margin-left: 1rem;
            white-space: nowrap;
        }

        .calificacion-usuario {
            margin-top: 0.5rem;
            margin-bottom: 0.5rem;
            color: #333;
            font-size: 1.05rem;
        }

        .calificacion-comentario {
            margin-top: 0.5rem;
            margin-bottom: 0.5rem;
            line-height: 1.5;
            font-size: 1.01rem;
            color: #444;
        }

        .calificacion-actions {
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }

        .rating-stars {
            color: #ffc107;
            font-size: 1.1rem;
            margin-right: 0.5rem;
        }

        .calificacion-card .btn-link {
            padding: 0.2rem 0.5rem;
            font-size: 1rem;
            text-decoration: none;
        }

        .calificacion-card .badge {
            font-size: 0.85em;
            margin-left: 0.2rem;
        }

        .d-flex.justify-content-between.align-items-center.mb-3 {
            margin-bottom: 0.5rem !important;
        }

        .btn-calificar {
            padding: 0.5rem 1rem;
            font-size: 0.9rem;
            display: inline-flex;
            align-items: center;
        }

        .actions-container {
            display: flex;
            gap: 1rem;
        }
        /* Estilos generales para la sección de calificaciones */
        .calificaciones-container {
            background-color: #fff;
            border-radius: 0.75rem;
            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
            padding: 1.5rem;
            margin-bottom: 2rem;
        }

        .calificaciones-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 1.5rem;
            padding-bottom: 1rem;
            border-bottom: 1px solid #f0f0f0;
        }

        .calificaciones-title {
            font-size: 1.25rem;
            font-weight: 600;
            color: #2d3748;
            display: flex;
            align-items: center;
        }

        .calificaciones-title i {
            margin-right: 0.75rem;
            color: #4f46e5;
        }

        /* Estilos para el mensaje cuando no hay calificaciones */
        .no-calificaciones {
            text-align: center;
            padding: 2rem;
            background-color: #f8fafc;
            border-radius: 0.75rem;
        }

        .no-calificaciones i {
            font-size: 3rem;
            color: #cbd5e0;
            margin-bottom: 1rem;
        }

        .no-calificaciones p {
            color: #64748b;
            margin-bottom: 1.5rem;
        }
        /* Estilos para cada tarjeta de calificación */
        .calificacion-card {
            background: #fff;
            border-radius: 0.75rem;
            padding: 1.5rem;
            margin-bottom: 1.25rem;
            border-left: 4px solid #4f46e5;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
            transition: all 0.3s ease;
            position: relative;
            overflow: hidden;
        }

        .calificacion-card:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 16px rgba(0, 0, 0, 0.08);
        }

        .calificacion-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 0.75rem;
        }

        .rating-stars {
            color: #ffc107;
            font-size: 1.1rem;
            letter-spacing: 1px;
        }

        .calificacion-fecha {
            font-size: 0.85rem;
            color: #718096;
            display: flex;
            align-items: center;
        }

        .calificacion-fecha i {
            margin-right: 0.5rem;
        }
        /* Estilos para la información del usuario y comentario */
        .calificacion-usuario {
            display: flex;
            align-items: center;
            margin-bottom: 0.5rem;
        }

        .user-avatar {
            width: 36px;
            height: 36px;
            border-radius: 50%;
            background-color: #4f46e5;
            color: white;
            display: flex;
            align-items: center;
            justify-content: center;
            margin-right: 0.75rem;
            font-weight: 600;
            font-size: 0.9rem;
        }

        .user-name {
            font-weight: 600;
            color: #2d3748;
        }

        .calificacion-comentario {
            color: #4a5568;
            line-height: 1.6;
            margin: 1rem 0;
            padding: 0.75rem;
            background-color: #f8fafc;
            border-radius: 0.5rem;
            border-left: 3px solid #e2e8f0;
        }
        /* Estilos para las acciones de votación */
        .calificacion-actions {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-top: 1rem;
            padding-top: 0.75rem;
            border-top: 1px solid #f0f0f0;
        }

        .votos-container {
            display: flex;
            align-items: center;
        }

        .voto-btn {
            background: none;
            border: none;
            cursor: pointer;
            display: flex;
            align-items: center;
            padding: 0.25rem 0.75rem;
            border-radius: 20px;
            transition: all 0.2s ease;
            color: #64748b;
        }

        .voto-btn:hover {
            background-color: #f8fafc;
        }

        .voto-btn i {
            font-size: 1rem;
            margin-right: 0.25rem;
        }

        .voto-btn.active {
            color: #4f46e5;
            font-weight: 500;
        }

        .votos-count {
            font-size: 0.85rem;
            color: #64748b;
            margin-left: 0.5rem;
        }
        /* Estilos para el botón de ordenar */
        .sort-btn {
            background-color: #f8fafc;
            border: 1px solid #e2e8f0;
            color: #4f46e5;
            padding: 0.5rem 1rem;
            border-radius: 0.5rem;
            font-size: 0.9rem;
            display: flex;
            align-items: center;
            transition: all 0.2s ease;
        }

        .sort-btn:hover {
            background-color: #edf2f7;
            border-color: #cbd5e0;
        }

        .sort-btn i {
            margin-right: 0.5rem;
        }

        /* Ajustes responsive */
        @media (max-width: 768px) {
            .calificacion-header {
                flex-direction: column;
                align-items: flex-start;
            }

            .calificacion-fecha {
                margin-top: 0.5rem;
            }

            .calificacion-actions {
                flex-direction: column;
                align-items: flex-start;
                gap: 0.5rem;
            }
        }
    </style>
</head>

<body>
<div class="container mt-5">
    <%
        request.setAttribute("titulo", "Detalles de Restaurante");
        request.setAttribute("botonAtras", true);
    %>
    <%@ include file="layout/header.jsp" %>

    <!-- Información del restaurante -->
    <div class="card shadow mb-4">
        <div class="card-body">
            <c:choose>
                <c:when test="${not empty restaurante}">
                    <div class="restaurant-header">
                        <div class="restaurant-name">${restaurante.nombre}</div>
                        <div class="d-flex align-items-center mb-2">
                            <div class="badge bg-primary me-2">${restaurante.tipoComida}</div>
                            <div class="rating-stars me-2">
                                <c:choose>
                                    <c:when test="${restaurante.puntajePromedio > 0}">
                                        <!-- <fmt:formatNumber value="${restaurante.puntajePromedio}" maxFractionDigits="1"
                                                          var="puntaje"/> -->
<fmt:formatNumber value="${restaurante.puntajePromedio}" maxFractionDigits="1" var="puntajeString"/>
                                        <!-- <fmt:formatNumber value="${restaurante.puntajePromedio}" maxFractionDigits="1" var="puntajeString"/> -->
                                        <fmt:parseNumber value="${puntajeString}" var="puntaje"/>
                                        <c:forEach begin="1" end="5" var="i">
                                            <c:choose>
                                                <c:when test="${i <= puntaje}">
                                                    <i class="fas fa-star"></i>
                                                </c:when>
                                                <c:when test="${i <= puntaje + 0.5 && i > puntaje}">
                                                    <i class="fas fa-star-half-alt"></i>
                                                </c:when>
                                                <c:otherwise>
                                                    <i class="far fa-star"></i>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="text-muted">Sin calificaciones</span>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <div class="text-muted">
                                <fmt:formatNumber value="${restaurante.puntajePromedio}" maxFractionDigits="1"/>
                                (${calificaciones.size()} calificaciones)
                            </div>
                        </div>
                        <p>${restaurante.descripcion}</p>
                    </div>

                    <div class="row">
                        <div class="col-md-6">
                            <div class="info-card">
                                <div class="info-label"><i class="fas fa-clock me-2"></i>Horarios:</div>
                                <p class="mb-0">Abierto de ${restaurante.horaApertura} a ${restaurante.horaCierre}</p>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="info-card">
                                <div class="info-label"><i class="fas fa-dollar-sign me-2"></i>Rango de precios:</div>
                                <p class="mb-0">
                                    <c:forEach begin="1" end="${restaurante.precio}" var="i">
                                        $
                                    </c:forEach>
                                </p>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="info-card">
                                <div class="info-label"><i class="fas fa-map-marker-alt me-2"></i>Distancia:</div>
                                <p class="mb-0">${restaurante.distanciaUniversidad} km de la universidad</p>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="info-card">
                                <div class="info-label"><i class="fas fa-stopwatch me-2"></i>Tiempo de espera promedio:
                                </div>
                                <p class="mb-0">${restaurante.tiempoEspera} minutos</p>
                            </div>
                        </div>
                    </div>

                    <c:if test="${not empty sessionScope.usuario && sessionScope.usuario.tipoUsuario == 'COMENSAL'}">
                        <div class="actions-container d-flex gap-2">
                            <a href="${pageContext.request.contextPath}/calificar?idRestaurante=${restaurante.id}"
                               class="btn btn-outline-primary d-flex align-items-center">
                                <i class="fas fa-star me-2"></i>Calificar
                            </a>

                        </div>
                    </c:if>
                </c:when>
                <c:otherwise>
                    <div class="alert alert-warning">
                        <i class="fas fa-exclamation-triangle me-2"></i>No se encontró información del restaurante
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <!-- Sección de calificaciones -->
    <div class="calificaciones-container">
        <div class="calificaciones-header">
            <h5 class="calificaciones-title"><i class="fas fa-comments"></i>Reseñas de clientes</h5>
            <form method="get" action="${pageContext.request.contextPath}/detalleRestaurante" class="m-0 p-0">
                <input type="hidden" name="id" value="${restaurante.id}" />
                <input type="hidden" name="orden" value="relevancia" />
                <button type="submit" class="sort-btn">
                    <i class="fas fa-sort-amount-down-alt"></i> Ordenar por relevancia
                </button>
            </form>
        </div>

        <c:choose>
            <c:when test="${not empty calificaciones}">
                <c:forEach items="${calificaciones}" var="calificacion">
                    <div class="calificacion-card">
                        <div class="calificacion-header">
                            <div class="rating-stars">
                                <c:forEach begin="1" end="5" var="i">
                                    <c:choose>
                                        <c:when test="${i <= calificacion.puntaje}">
                                            <i class="fas fa-star"></i>
                                        </c:when>
                                        <c:otherwise>
                                            <i class="far fa-star"></i>
                                        </c:otherwise>
                                    </c:choose>
                                </c:forEach>
                            </div>
                            <div class="calificacion-fecha">
                                <i class="far fa-calendar-alt"></i>
                                    ${calificacion.fechaFormateada}
                            </div>
                        </div>

                        <div class="calificacion-usuario">
                            <div class="user-avatar">
                                    ${fn:substring(calificacion.comensal.nombreUsuario, 0, 1)}
                            </div>
                            <span class="user-name">${calificacion.comensal.nombreUsuario}</span>
                        </div>

                        <div class="calificacion-comentario">
                                ${calificacion.comentario}
                        </div>

                        <div class="calificacion-actions">
                            <div class="votos-container">
                                <c:if test="${not empty sessionScope.usuario && sessionScope.usuario.tipoUsuario == 'COMENSAL'}">
                                    <form action="${pageContext.request.contextPath}/votarCalificacion" method="POST" class="m-0 p-0">
                                        <input type="hidden" name="idCalificacion" value="${calificacion.id}"/>
                                        <input type="hidden" name="idComensal" value="${sessionScope.usuario.id}"/>
                                        <c:set var="yaVoto" value="false"/>
                                        <c:forEach var="voto" items="${calificacion.votos}">
                                            <c:if test="${voto.comensal.id == sessionScope.usuario.id}">
                                                <c:set var="yaVoto" value="true"/>
                                            </c:if>
                                        </c:forEach>
                                        <button type="submit" class="voto-btn ${yaVoto ? 'active' : ''}">
                                            <i class="fas ${yaVoto ? 'fa-thumbs-down' : 'fa-thumbs-up'}"></i>
                                            <span>${yaVoto ? 'Quitar voto útil' : 'Agregar voto útil'}</span>
                                        </button>
                                        <span class="votos-count">${fn:length(calificacion.votos)} votos</span>
                                    </form>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <div class="no-calificaciones">
                    <i class="fas fa-comment-slash"></i>
                    <p>Este restaurante no tiene reseñas todavía.</p>
                    <c:if test="${not empty sessionScope.usuario && sessionScope.usuario.tipoUsuario == 'COMENSAL'}">
                        <a href="${pageContext.request.contextPath}/calificar?idRestaurante=${restaurante.id}"
                           class="btn btn-primary">
                            <i class="fas fa-star me-2"></i>Sé el primero en opinar
                        </a>
                    </c:if>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
