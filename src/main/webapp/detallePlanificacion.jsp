<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Detalle de Planificación</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="styles.css">
</head>
<body>
    <div class="container mt-5">
        <% request.setAttribute("titulo", "Detalle de Planificación"); request.setAttribute("botonAtras", true); %>
        <%@ include file="layout/header.jsp" %>

        <div class="card shadow mb-4">
            <div class="card-body">
                <h3 class="mb-4"><i class="fas fa-calendar-check me-2"></i>Detalle de Planificación</h3>

                <c:if test="${not empty mensaje}">
                    <div class="alert alert-info">${mensaje}</div>
                </c:if>

                <div class="alert alert-light">
                    <div class="row">
                        <div class="col-md-6">
                            <p><strong>ID:</strong> ${planificacion.id}</p>
                            <p><strong>Nombre:</strong> ${planificacion.nombre}</p>
                            <p><strong>Hora:</strong> ${planificacion.hora}</p>
                            <p><strong>Estado:</strong> ${planificacion.estado}</p>
                            <p><strong>Planificador:</strong> ${planificacion.comensalPlanificador.nombreUsuario}</p>
                            <p><strong>Estado de Votación:</strong> 
                                <span class="${planificacion.estadoVotacion == 'No iniciada' ? 'text-secondary' : 
                                              (planificacion.estadoVotacion == 'En progreso' ? 'text-primary' : 'text-success')}">
                                    ${planificacion.estadoVotacion}
                                </span>
                            </p>
                        </div>
                        <div class="col-md-6">
                            <c:if test="${planificacion.estadoVotacion == 'Terminada' && not empty planificacion.restauranteGanador}">
                                <div class="mb-3">
                                    <h5>Restaurante Ganador</h5>
                                    <div class="alert alert-success">
                                        <p><strong>Nombre:</strong> ${planificacion.restauranteGanador.nombre}</p>
                                        <p><strong>Tipo de comida:</strong> ${planificacion.restauranteGanador.tipoComida}</p>
                                        <p><strong>Puntaje promedio:</strong> ${planificacion.restauranteGanador.puntajePromedio} ★</p>
                                    </div>
                                </div>
                            </c:if>
                            
                            <c:if test="${planificacion.estado == 'Activa' && sessionScope.usuario.id == planificacion.comensalPlanificador.id}">
                                <div class="d-grid gap-2">
                                    <c:choose>
                                        <c:when test="${planificacion.estadoVotacion == 'No iniciada'}">
                                            <form action="${pageContext.request.contextPath}/iniciarVotacion" method="POST">
                                                <input type="hidden" name="planificacionId" value="${planificacion.id}">
                                                <button type="submit" class="btn btn-primary">
                                                    <i class="fas fa-play me-1"></i> Iniciar Votación
                                                </button>
                                            </form>
                                        </c:when>
                                        <c:when test="${planificacion.estadoVotacion == 'En progreso'}">
                                            <form action="${pageContext.request.contextPath}/terminarVotacion" method="POST">
                                                <input type="hidden" name="planificacionId" value="${planificacion.id}">
                                                <button type="submit" class="btn btn-warning">
                                                    <i class="fas fa-stop me-1"></i> Terminar Votación
                                                </button>
                                            </form>
                                        </c:when>
                                    </c:choose>
                                </div>
                            </c:if>
                        </div>
                    </div>
                </div>

                <!-- Pestañas para comensales y restaurantes -->
                <ul class="nav nav-tabs mt-4" id="myTab" role="tablist">
                    <li class="nav-item" role="presentation">
                        <button class="nav-link active" id="comensales-tab" data-bs-toggle="tab" data-bs-target="#comensales"
                                type="button" role="tab" aria-controls="comensales" aria-selected="true">
                            <i class="fas fa-users me-1"></i>Comensales
                        </button>
                    </li>
                    <li class="nav-item" role="presentation">
                        <button class="nav-link" id="restaurantes-tab" data-bs-toggle="tab" data-bs-target="#restaurantes"
                                type="button" role="tab" aria-controls="restaurantes" aria-selected="false">
                            <i class="fas fa-utensils me-1"></i>Restaurantes
                        </button>
                    </li>
                    <c:if test="${planificacion.estadoVotacion == 'En progreso' || planificacion.estadoVotacion == 'Terminada'}">
                        <li class="nav-item" role="presentation">
                            <button class="nav-link" id="votacion-tab" data-bs-toggle="tab" data-bs-target="#votacion"
                                    type="button" role="tab" aria-controls="votacion" aria-selected="false">
                                <i class="fas fa-vote-yea me-1"></i>Votación
                            </button>
                        </li>
                    </c:if>
                </ul>

                <!-- Contenido de las pestañas -->
                <div class="tab-content" id="myTabContent">
                    <!-- Pestaña de Comensales -->
                    <div class="tab-pane fade show active p-3" id="comensales" role="tabpanel" aria-labelledby="comensales-tab">
                        <h4 class="mt-3 mb-3">Comensales actuales</h4>
                        <c:choose>
                            <c:when test="${empty planificacion.comensales}">
                                <div class="alert alert-warning">No hay comensales añadidos a esta planificación.</div>
                            </c:when>
                            <c:otherwise>
                                <div class="table-responsive">
                                    <table class="table table-bordered align-middle">
                                        <thead class="table-light">
                                            <tr>
                                                <th>ID</th>
                                                <th>Nombre de Usuario</th>
                                                <th>Email</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="comensal" items="${planificacion.comensales}">
                                                <tr>
                                                    <td>${comensal.id}</td>
                                                    <td>${comensal.nombreUsuario}</td>
                                                    <td>${comensal.email}</td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </c:otherwise>
                        </c:choose>

                        <!-- Formulario para añadir un nuevo comensal -->
                        <c:if test="${planificacion.estado == 'Activa'}">
                            <h4 class="mt-4 mb-3">Añadir nuevo comensal</h4>
                            <form action="${pageContext.request.contextPath}/agregarComensal" method="POST">
                                <input type="hidden" name="planificacionId" value="${planificacion.id}">

                                <div class="mb-3">
                                    <label for="comensalId" class="form-label">ID del Comensal</label>
                                    <input type="number" class="form-control" id="comensalId" name="comensalId" required>
                                    <div class="form-text">Ingrese el ID del comensal que desea añadir a la planificación.</div>
                                </div>

                                <button type="submit" class="btn btn-primary">
                                    <i class="fas fa-plus me-2"></i>Añadir Comensal
                                </button>
                            </form>
                        </c:if>
                    </div>

                    <!-- Pestaña de Restaurantes -->
                    <!-- Hola soy goku  -->

                    <div class="tab-pane fade p-3" id="restaurantes" role="tabpanel" aria-labelledby="restaurantes-tab">                        <!-- Mostrar todos los restaurantes de la planificación -->
                        <h4 class="mt-3 mb-3">Restaurantes considerados</h4>
                        
                        <c:choose>
                            <c:when test="${empty planificacion.restaurantes}">
                                <div class="alert alert-warning">No hay restaurantes añadidos a esta planificación.</div>
                            </c:when>
                            <c:otherwise>
                                <div class="table-responsive">
                                    <table class="table table-bordered align-middle">
                                        <thead class="table-light">
                                            <tr>
                                                <th>Nombre</th>
                                                <th>Tipo de comida</th>
                                                <th>Puntaje</th>
                                                <th>Horario</th>
                                                <c:if test="${planificacion.estadoVotacion == 'En progreso'}">
                                                    <th>Acción</th>
                                                </c:if>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="restaurante" items="${planificacion.restaurantes}">
                                                <tr>
                                                    <td>${restaurante.nombre}</td>
                                                    <td>${restaurante.tipoComida}</td>
                                                    <td>${restaurante.puntajePromedio} ★</td>
                                                    <td>
                                                        <c:if test="${not empty restaurante.horaApertura && not empty restaurante.horaCierre}">
                                                            ${restaurante.horaApertura} - ${restaurante.horaCierre}
                                                        </c:if>
                                                        <c:if test="${empty restaurante.horaApertura || empty restaurante.horaCierre}">
                                                            No especificado
                                                        </c:if>
                                                    </td>
                                                    <c:if test="${planificacion.estadoVotacion == 'En progreso'}">
                                                        <td>
                                                            <form action="${pageContext.request.contextPath}/votar" method="POST">
                                                                <input type="hidden" name="planificacionId" value="${planificacion.id}">
                                                                <input type="hidden" name="restauranteId" value="${restaurante.id}">
                                                                <button type="submit" class="btn btn-sm btn-primary">
                                                                    <i class="fas fa-vote-yea me-1"></i>Votar
                                                                </button>
                                                            </form>
                                                        </td>
                                                    </c:if>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </c:otherwise>
                        </c:choose>

                        <!-- Formulario para añadir un nuevo restaurante -->
                        <c:if test="${planificacion.estado == 'Activa'}">
                            <h4 class="mt-4 mb-3">Añadir nuevo restaurante</h4>
                            <form action="${pageContext.request.contextPath}/agregarRestaurante" method="POST">
                                <input type="hidden" name="planificacionId" value="${planificacion.id}">

                                <div class="mb-3">
                                    <label for="restauranteId" class="form-label">Restaurante</label>
                                    <select class="form-select" id="restauranteId" name="restauranteId" required>
                                        <option value="">Seleccione un restaurante</option>
                                        <c:forEach var="restaurante" items="${restaurantes}">
                                            <option value="${restaurante.id}">${restaurante.nombre} - ${restaurante.tipoComida} (${restaurante.puntajePromedio} ★)</option>
                                        </c:forEach>
                                    </select>
                                </div>

                                <button type="submit" class="btn btn-primary">
                                    <i class="fas fa-plus me-2"></i>Añadir Restaurante
                                </button>
                            </form>
                        </c:if>
                    </div>
                    
                    <!-- Pestaña de Votación -->
                    <c:if test="${planificacion.estadoVotacion == 'En progreso' || planificacion.estadoVotacion == 'Terminada'}">
                        <div class="tab-pane fade p-3" id="votacion" role="tabpanel" aria-labelledby="votacion-tab">
                            <h4 class="mt-3 mb-3">Estado de Votación</h4>
                            
                            <c:choose>
                                <c:when test="${planificacion.estadoVotacion == 'En progreso'}">
                                    <div class="alert alert-info">
                                        <p>La votación está actualmente en progreso. Cada comensal puede votar por su restaurante preferido.</p>
                                        <c:if test="${not empty restauranteVotado}">
                                            <p class="mb-0"><strong>Tu voto actual:</strong> ${restauranteVotado.nombre}</p>
                                        </c:if>
                                        <c:if test="${empty restauranteVotado}">
                                            <p class="mb-0"><strong>Aún no has votado.</strong> Ve a la pestaña de restaurantes para emitir tu voto.</p>
                                        </c:if>
                                    </div>
                                </c:when>
                                <c:when test="${planificacion.estadoVotacion == 'Terminada'}">
                                    <div class="alert alert-success">
                                        <h5 class="mb-2">Votación finalizada</h5>
                                        <c:if test="${not empty planificacion.restauranteGanador}">
                                            <p><strong>Restaurante ganador:</strong> ${planificacion.restauranteGanador.nombre}</p>
                                        </c:if>
                                    </div>
                                </c:when>
                            </c:choose>
                            
                            <h5 class="mt-4 mb-3">Resultados de la votación</h5>
                            
                            <c:choose>
                                <c:when test="${empty resultados}">
                                    <div class="alert alert-warning">No hay votos registrados aún.</div>
                                </c:when>
                                <c:otherwise>
                                    <div class="table-responsive">
                                        <table class="table table-bordered align-middle">
                                            <thead class="table-light">
                                                <tr>
                                                    <th>Restaurante</th>
                                                    <th>Tipo de comida</th>
                                                    <th>Votos</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <c:forEach var="resultado" items="${resultados}">
                                                    <tr>
                                                        <td>${resultado.key.nombre}</td>
                                                        <td>${resultado.key.tipoComida}</td>
                                                        <td>
                                                            <span class="badge bg-primary">${resultado.value}</span>
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                            </tbody>
                                        </table>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </c:if>
                </div>
            </div>
        </div>
    </div>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
