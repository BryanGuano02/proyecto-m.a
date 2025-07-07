<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Registro - UFood</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <script src="https://cdn.tailwindcss.com"></script>
    <link rel="stylesheet" href="styles.css">
    <script>
        function toggleTipoComidaField() {
            const tipoUsuario = document.querySelector('input[name="tipoUsuario"]:checked').value;
            const tipoComidaDiv = document.getElementById('tipoComidaDiv');

            if (tipoUsuario === "COMENSAL") {
                tipoComidaDiv.style.display = 'block';
            } else {
                tipoComidaDiv.style.display = 'none';
            }
        }

        function enviarFormulario() {
            const tipoUsuario = document.querySelector('input[name="tipoUsuario"]:checked').value;
            const form = document.getElementById('formRegistro');

            if (tipoUsuario === "COMENSAL") {
                form.action = "${pageContext.request.contextPath}/registro-comensal";
            } else {
                form.action = "${pageContext.request.contextPath}/registro-restaurante";
            }
            form.submit();
            return false;
        }

        // Inicializar al cargar la página
        document.addEventListener('DOMContentLoaded', function() {
            toggleTipoComidaField();

            // Agregar listeners a los radio buttons
            document.querySelectorAll('input[name="tipoUsuario"]').forEach(radio => {
                radio.addEventListener('change', toggleTipoComidaField);
            });
        });
    </script>
</head>
<body class="bg-gray-50">
<div class="container mx-auto mt-12 px-4 max-w-md">
    <div class="text-center ">
        <h1 class="text-3xl md:text-4xl font-bold text-blue-600 mb-4">Registro de Usuario</h1>
    </div>
    <% if (request.getAttribute("error") != null) { %>
    <div class="mb-6 p-4 bg-red-100 border border-red-400 text-red-700 rounded">
        ${error}
    </div>
    <% } %>

    <form id="formRegistro" method="post" onsubmit="return enviarFormulario()"
          class="bg-white p-6 rounded-lg shadow-md">
        <div class="mb-6">
            <label class="block text-gray-700 mb-3 font-medium">Tipo de usuario:</label>
            <div class="flex items-center space-x-6">
                <label class="inline-flex items-center">
                    <input type="radio" id="restaurante" name="tipoUsuario" value="RESTAURANTE"
                           class="form-radio h-4 w-4 text-blue-600 transition duration-150">
                    <span class="ml-2 text-gray-700">Restaurante</span>
                </label>
                <label class="inline-flex items-center">
                    <input type="radio" id="comensal" name="tipoUsuario" value="COMENSAL" checked
                           class="form-radio h-4 w-4 text-blue-600 transition duration-150">
                    <span class="ml-2 text-gray-700">Comensal</span>
                </label>
            </div>
        </div>

        <div class="mb-4">
            <label for="nombreUsuario" class="block text-gray-700 mb-2">Nombre de usuario:</label>
            <input type="text" id="nombreUsuario" name="nombreUsuario" required
                   class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500">
        </div>

        <div class="mb-4">
            <label for="email" class="block text-gray-700 mb-2">Email:</label>
            <input type="email" id="email" name="email" required
                   class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500">
        </div>

        <div class="mb-4">
            <label for="contrasena" class="block text-gray-700 mb-2">Contraseña:</label>
            <input type="password" id="contrasena" name="contrasena" required
                   class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500">
        </div>

        <div id="tipoComidaDiv" class="mb-4">
            <label for="tipoComidaFavorita" class="block text-gray-700 mb-2">Tipo de comida favorita:</label>
            <select id="tipoComidaFavorita" name="tipoComidaFavorita"
                    class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500">
                <option value="">Seleccione una opción</option>
                <option value="Comida Rápida">Comida Rápida</option>
                <option value="Comida Costeña">Comida Costeña</option>
                <option value="Comida Casera">Comida Casera</option>
                <option value="Comida Casera">Platos a la Carta</option>
<%--                <option value="Comida Vegetariana">Comida Vegetariana</option>--%>
<%--                <option value="Comida Italiana">Comida Italiana</option>--%>
<%--                <option value="Comida Mexicana">Comida Mexicana</option>--%>
<%--                <option value="Comida China">Comida China</option>--%>
<%--                <option value="Comida Japonesa">Comida Japonesa</option>--%>
            </select>
        </div>

        <button type="submit"
                class="w-full bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700 transition duration-200 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2">
            Registrarse
        </button>
    </form>

    <p class="mt-4 text-center text-gray-600">
        ¿Ya tienes una cuenta?
        <a href="${pageContext.request.contextPath}/login.jsp" class="text-blue-600 hover:text-blue-800">Inicia sesión aquí</a>
    </p>
</div>
</body>
</html>