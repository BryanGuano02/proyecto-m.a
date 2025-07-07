<%@ page import="entidades.Restaurante" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html lang="es">

<head>
    <title>U-Food | Calificar Restaurante</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600&display=swap"
          rel="stylesheet">
    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="styles.css">
</head>

<body>
<div class="container mt-5">
    <% request.setAttribute("titulo", "Calificar Restaurante" ); request.setAttribute("botonAtras",
            true); %>
    <%@ include file="layout/header.jsp" %>

    <div class="card shadow">
        <div class="card-body">
            <div class="restaurant-info">
                <% Restaurante restaurante=(Restaurante) request.getAttribute("restaurante"); if
                (restaurante !=null) { %>
                <div class="restaurant-name">
                    <%= restaurante.getNombre() %>
                </div>
                <div class="restaurant-type mb-2">
                    <i class="fas fa-utensils me-1"></i>
                    <%= restaurante.getTipoComida() %>
                </div>
                <p class="mb-0">
                    <%= restaurante.getDescripcion() %>
                </p>
                <% } else { %>
                <div class="alert alert-warning">No se encontró información del
                    restaurante</div>
                <% } %>
            </div>

            <form action="${pageContext.request.contextPath}/calificar" method="POST">
                <input type="hidden" name="action" value="crear">
                <input type="hidden" name="idRestaurante"
                       value="<%= restaurante != null ? restaurante.getId() : "" %>">
                <input type="hidden" name="idComensal" value="${sessionScope.usuario.id}">
                <!-- Puntaje -->
                <div class="mb-3">
                    <label class="form-label">Puntaje:</label>
                    <div class="d-flex justify-content-between mb-3">
                        <div class="rating-option">
                            <input type="radio" id="puntaje1" name="puntaje" value="1" required>
                            <label for="puntaje1">★</label>
                        </div>
                        <div class="rating-option">
                            <input type="radio" id="puntaje2" name="puntaje" value="2">
                            <label for="puntaje2">★★</label>
                        </div>
                        <div class="rating-option">
                            <input type="radio" id="puntaje3" name="puntaje" value="3">
                            <label for="puntaje3">★★★</label>
                        </div>
                        <div class="rating-option">
                            <input type="radio" id="puntaje4" name="puntaje" value="4">
                            <label for="puntaje4">★★★★</label>
                        </div>
                        <div class="rating-option">
                            <input type="radio" id="puntaje5" name="puntaje" value="5">
                            <label for="puntaje5">★★★★★</label>
                        </div>
                    </div>
                </div>

                <div class="mb-4">
                    <label for="comentario" class="form-label">Comentario:</label>
                    <textarea class="form-control" id="comentario" name="comentario"
                              placeholder="Describe tu experiencia..." rows="4" required></textarea>
                </div>

                <button type="submit" class="btn btn-primary w-100 py-2">
                    <i class="fas fa-star me-2"></i> Enviar Calificación
                </button>
            </form>
        </div>
    </div>
</div>
<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>

</html>
