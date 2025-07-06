# U-Food App

U-Food es una aplicación web, desarrollado en Java, está diseñado para facilitar la interacción entre comensales y restaurantes ubicados cerca de una universidad. Su objetivo principal es permitir que los usuarios  encuentren opciones gastronómicas mediante las calificaciones de restaurantes.

## Features

**Comensales:**

- Calificar restaurantes con estrellas y un comentario.
- Comparar dos restaurantes entre sí con base en atributos como precio, distancia a la universidad, entre otros.
- Filtrar restaurantes usando atributos representativos (tipo de comida, horarios, precios, etc.) y guardar dichos filtros como "preferencias" para futuras consultas.
- Recibir notificaciones sobre la publicación del menú del día actual del restaurante suscrito.

**Restaurantes:**
- Publicar el menú del día para que esté visible a los comensales.
- Proponer el menú del día siguiente a través de votaciones de los comensales.

## Run Locally

Para correr el proyecto localmente a través de Docker:

1. Descargar la imagen del proyecto desde Docker Hub:

```docker
docker pull bryang02/u-food:lts
docker run -p 8080:8080 bryang02/u-food:lts
```

2. Acceder a la aplicación con el siguiente link: http://localhost:8080/login

## Screenshots

![App Screenshot](https://via.placeholder.com/468x300?text=App+Screenshot+Here)

## Tech Stack

**Desarrollo:**
- Java Web
- MySQL 8.0.33
- Tailwind

**Dependencias**
- Jakarta EE 9
- Hibernate 6.2.5
- Mockito 5.17.0
- Junit4
- ByteBuddy 1.14.6

**DevOps:**
- Git
- GitHub
- Jenkins


## Authors
