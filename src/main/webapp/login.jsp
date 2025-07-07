<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Login - UFood</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <script src="https://cdn.tailwindcss.com"></script>
    <link rel="stylesheet" href="styles.css">
</head>
<body class="bg-gray-50">
<div class="container mx-auto mt-12 px-4 max-w-md">
    <!-- Encabezado centrado -->
    <div class="text-center">
        <h1 class="text-3xl md:text-4xl font-bold text-blue-600 mb-4">Iniciar Sesión</h1>

        <% if (request.getParameter("registroExitoso") != null) { %>
        <p class="text-green-600 mb-4">Registro exitoso! Por favor inicie sesión.</p>
        <% } %>
        <% if (request.getAttribute("error") != null) { %>
        <p class="text-red-500 mb-4">${error}</p>
        <% } %>
    </div>

    <!-- Formulario -->
    <form action="${pageContext.request.contextPath}/login" method="post"
          class="bg-white p-6 rounded-lg shadow-md mb-6">
        <div class="mb-4">
            <label for="nombreUsuario" class="block text-gray-700 mb-2">Nombre de usuario:</label>
            <input type="text" id="nombreUsuario" name="nombreUsuario" required
                   class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500">
        </div>
        <div class="mb-6">
            <label for="contrasena" class="block text-gray-700 mb-2">Contraseña:</label>
            <input type="password" id="contrasena" name="contrasena" required
                   class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500">
        </div>
        <button type="submit"
                class="w-full bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700 transition duration-200 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2">
            Iniciar Sesión
        </button>
    </form>

    <!-- Enlace de registro -->
    <p class="text-center text-gray-600">
        ¿No tienes una cuenta?
        <a href="${pageContext.request.contextPath}/registro.jsp" class="text-blue-600 hover:text-blue-800">Regístrate
            aquí</a>
    </p>
</div>
</body>
</html>