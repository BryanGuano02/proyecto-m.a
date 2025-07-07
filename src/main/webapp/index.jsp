<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>

<head>
    <title>U-Food | Inicio</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600&display=swap"
          rel="stylesheet">
    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
          rel="stylesheet">
    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="styles.css">
</head>

<body>
<div class="container mt-5">
    <% request.setAttribute("titulo", "Lista de Restaurantes "); // Ejemplo: para resaltar menú
        request.setAttribute("botonAtras", false); // Ejemplo: para resaltar menú %>
    <%@ include file="layout/header.jsp" %> <!-- Barra de búsqueda y acciones -->
    <div class="card shadow mb-4">
        <div class="card-body">
            <div class="row">
                <div class="col-12 d-flex flex-wrap justify-content-center gap-2">
                    <c:if
                            test="${not empty sessionScope.usuario && sessionScope.usuario.tipoUsuario == 'COMENSAL'}">
                        <a href="${pageContext.request.contextPath}/planificar"
                                class="btn btn-success">
                        <i class="fas fa-calendar-plus me-2"></i>Crear Planificación
                    </a>
                        <a href="${pageContext.request.contextPath}/misPlanificaciones"
                           class="btn btn-primary">
                            <i class="fas fa-calendar-alt me-2"></i>Mis Planificaciones
                        </a>
                    </c:if>
                </div>
            </div>
        </div>
    </div>

    <div class="row" id="restaurantes-container">
        <c:choose>
            <c:when test="${empty restaurantes}">
                <div class="col-12">
                    <div class="card no-restaurants">
                        <i class="fas fa-utensils fa-4x mb-3"></i>
                        <h3>No hay restaurantes disponibles</h3>
                        <p class="text-muted">No se encontraron restaurantes que
                            coincidan con tu búsqueda.</p>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <c:forEach items="${restaurantes}" var="restaurante">
                    <div class="col-md-6 col-lg-4 mb-4">
                        <div class="card restaurant-card h-100">
                            <div class="restaurant-img-placeholder">
                                <i class="fas fa-utensils fa-3x"></i>
                            </div>
                            <div class="card-body">
                                <h5 class="card-title">
                                    <a href="${pageContext.request.contextPath}/detalleRestaurante?id=${restaurante.id}"
                                       class="text-decoration-none text-dark">
                                        <c:out value="${not empty restaurante.nombre ? restaurante.nombre : 'Sin nombre'}"/>
                                    </a>
                                </h5>
                                <p class="restaurant-type mb-2">
                                    <i class="fas fa-utensils me-1"></i>
                                    <c:out value="${not empty restaurante.tipoComida ? restaurante.tipoComida : 'No especificado'}"/>
                                </p>

                                <div class="mb-2">
                                    <c:choose>
                                        <c:when test="${restaurante.puntajePromedio > 0}">
                                            <c:forEach begin="1" end="5" var="i">
                                                <i class="fas fa-star ${i <= restaurante.puntajePromedio ? 'text-warning' : 'text-secondary'}"></i>
                                            </c:forEach>
                                            <span class="ms-1">(
                                                <fmt:formatNumber value="${restaurante.puntajePromedio}"
                                                                  pattern="#.##"/>)
                                            </span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="text-muted">Sin calificaciones</span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>

                                <p class="card-text">
                                    <c:out value="${not empty restaurante.descripcion ? restaurante.descripcion : 'Sin descripción'}"/>
                                </p>

                                <div class="restaurant-actions">
                                    <c:if test="${not empty sessionScope.usuario && sessionScope.usuario.tipoUsuario == 'COMENSAL'}">

                                        <a href="${pageContext.request.contextPath}/detalleRestaurante?id=${restaurante.id}"
                                           class="btn btn-sm btn-primary me-1">
                                            <i class="fas fa-info-circle"></i> Detalles
                                        </a>
                                        <a href="${pageContext.request.contextPath}/calificar?idRestaurante=${restaurante.id}"
                                           class="btn btn-sm btn-outline-success">
                                            <i class="fas fa-star"></i> Calificar
                                        </a>
                                    </c:if>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<script
        src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>    document.addEventListener('DOMContentLoaded', function () {
    const seccionRecomendados = document.getElementById('seccionRecomendados');
    const tieneRecomendados = ${not empty restaurantesRecomendados ? 'true' : 'false'};

    if (seccionRecomendados && tieneRecomendados) {
        // Forzar repintado del componente
        seccionRecomendados.style.display = 'none';
        setTimeout(() => {
            seccionRecomendados.style.display = 'block';
        }, 50);
    }
    // Manejar el formulario de búsqueda
    const searchForm = document.getElementById('searchForm');
    if (searchForm) {
        searchForm.addEventListener('submit', function (e) {
            e.preventDefault();
            const searchTerm = document.getElementById('searchInput').value.trim();
            buscarRestaurantes(searchTerm);
        });
    }

    // Verificar si ya hay restaurantes cargados
    const hasRestaurants = document.querySelectorAll('.restaurant-card').length > 0;
    const hasSearchParam = new URL(window.location.href).searchParams.get('busqueda');

    if (!hasRestaurants && !hasSearchParam) {
        cargarRestaurantes();
    }
});

function cargarRestaurantes() {
    const container = document.getElementById('restaurantes-container');
    if (!container) return;

    container.innerHTML = `
            <div class="col-12 loading-spinner">
                <div class="spinner-border text-primary" role="status">
                    <span class="visually-hidden">Cargando...</span>
                </div>
            </div>`;

    fetch('${pageContext.request.contextPath}/inicio')
        .then(response => {
            if (!response.ok) throw new Error('Error en la respuesta del servidor');
            return response.text();
        })
        .then(html => {
            // Parsear el HTML para extraer solo los restaurantes
            const tempDiv = document.createElement('div');
            tempDiv.innerHTML = html;
            const restaurantesHTML = tempDiv.querySelector('#restaurantes-container').innerHTML;
            container.innerHTML = restaurantesHTML;
            inicializarModales();
        })
        .catch(error => {
            console.error('Error al cargar restaurantes:', error);
            container.innerHTML = `
                    <div class="col-12 error-message">
                        <i class="fas fa-exclamation-triangle fa-2x mb-2"></i>
                        <h4>Error al cargar los restaurantes</h4>
                        <p>Por favor intenta recargar la página</p>
                        <button onclick="location.reload()" class="btn btn-primary mt-2">
                            <i class="fas fa-sync-alt me-2"></i>Recargar
                        </button>
                    </div>`;
        });
}

function buscarRestaurantes(searchTerm) {
    const container = document.getElementById('restaurantes-container');
    if (!container) return;

    container.innerHTML = `
            <div class="col-12 loading-spinner">
                <div class="spinner-border text-primary" role="status">
                    <span class="visually-hidden">Buscando...</span>
                </div>
            </div>`;

    const url = '${pageContext.request.contextPath}/inicio?busqueda=' + encodeURIComponent(searchTerm);

    fetch(url, {
        headers: {
            'X-Requested-With': 'XMLHttpRequest'
        }
    })
        .then(response => {
            if (!response.ok) throw new Error('Error en la búsqueda');
            return response.text();
        })
        .then(html => {
            // Parsear el HTML para extraer solo los restaurantes
            const tempDiv = document.createElement('div');
            tempDiv.innerHTML = html;
            const restaurantesHTML = tempDiv.querySelector('#restaurantes-container').innerHTML;
            container.innerHTML = restaurantesHTML;
            inicializarModales();

            // Actualizar URL sin recargar la página
            window.history.pushState({}, '', url);
        })
        .catch(error => {
            console.error('Error en la búsqueda:', error);
            container.innerHTML = `
                <div class="col-12 error-message">
                    <i class="fas fa-exclamation-triangle fa-2x mb-2"></i>
                    <h4>Error en la búsqueda</h4>
                    <p>${error.message}</p>
                    <button onclick="buscarRestaurantes('${searchTerm}')" class="btn btn-primary mt-2">
                        <i class="fas fa-sync-alt me-2"></i>Reintentar
                    </button>
                </div>`;
        });
}

function inicializarModales() {
    if (typeof bootstrap !== 'undefined' && bootstrap.Modal) {
        document.querySelectorAll('.modal').forEach(modalEl => {
            new bootstrap.Modal(modalEl);
        });
    }
    document.querySelectorAll('.btn-like').forEach(function (btn) {
        btn.addEventListener('click', function () {
            if (btn.classList.contains('clicked')) return;

            const idRestaurante = btn.getAttribute('data-id');
            console.log('Like enviado para restaurante con ID:', idRestaurante);

            fetch('${pageContext.request.contextPath}/SvMenuDelDia', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: 'id=' + encodeURIComponent(idRestaurante)
            });

            btn.classList.add('clicked');
        });
    });

}

document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('.btn-like').forEach(function (btn) {
        btn.addEventListener('click', function () {
            if (btn.classList.contains('clicked')) return;

            const idRestaurante = btn.getAttribute('data-id');

            fetch('${pageContext.request.contextPath}/SvMenuDelDia', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: 'id=' + encodeURIComponent(idRestaurante)
            });

            btn.classList.add('clicked'); // esto se debe ejecutar
            var modal = new bootstrap.Modal(document.getElementById('modalVotoConfirmado'));
            modal.show();
        });
    });
});
</script>
<script>
    document.addEventListener('DOMContentLoaded', function () {
        // Cuando se cierra el modal de voto confirmado, cierra cualquier modal de menú abierto
        var modalVotoConfirmado = document.getElementById('modalVotoConfirmado');
        if (modalVotoConfirmado) {
            modalVotoConfirmado.addEventListener('hidden.bs.modal', function () {
                // Cierra cualquier modal de menú abierto
                document.querySelectorAll('.modal.show').forEach(function (modal) {
                    if (modal.id.startsWith('menuModal')) {
                        var bsModal = bootstrap.Modal.getInstance(modal);
                        if (bsModal) {
                            bsModal.hide();
                        }
                    }
                });
            });
        }
    });
</script>
<!-- Modal de voto confirmado -->
<div class="modal fade" id="modalVotoConfirmado" tabindex="-1"
     aria-labelledby="modalVotoConfirmadoLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header bg-success text-white">
                <h5 class="modal-title" id="modalVotoConfirmadoLabel">
                    <i class="fas fa-check-circle me-2"></i>Voto confirmado
                </h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"
                        aria-label="Cerrar"></button>
            </div>
            <div class="modal-body text-center">
                ¡Tu voto ha sido registrado correctamente!
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-success"
                        data-bs-dismiss="modal">Aceptar
                </button>
            </div>
        </div>
    </div>
</div>

</body>

</html>
