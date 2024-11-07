# 🎬 Movie API - Spring Boot 3 + MySQL + JWT Auth
Una API de películas creada con Java y Spring Boot 3 para demostrar habilidades en el desarrollo de aplicaciones web y 
gestión de seguridad con JWT. Esta API permite realizar operaciones CRUD en una base de datos de películas y cuenta con 
autenticación y autorización mediante Spring Security 6.

## 📋 Índice  

1. [Características](#-características)
2. [Tecnologías Utilizadas](#-tecnologías-utilizadas)
3. [Instalación y Configuración](#-instalación-y-configuración)
4. [Autenticación y Seguridad](#-autenticación-y-seguridad)
5. [Documentación de Endpoints](#-documentación-de-endpoints)
  
## 📜 [Características](#-características)
* CRUD completo para gestionar películas.  
* Paginación y ordenamiento en la consulta de películas.  
* Autenticación JWT con sesión y refresh token, con expiración configurable (15 minutos).  
* Protección de endpoints mediante roles de usuario y administrador.  
* Arquitectura RESTful que facilita la integración con otras aplicaciones o servicios.  

## 🚀 [Tecnologías Utilizadas](#-tecnologías-utilizadas)
* **Java** 17  
* **Spring Boot 3**  
* **Spring Security 6** para autenticación y autorización.  
* **Spring Data JPA** para la interacción con la base de datos MySQL.  
* **MySQL** como base de datos relacional.  
* **JWT** (JSON Web Token) para autenticación y autorización de usuarios.  
* **Maven** para la gestión de dependencias.  

## 📥 [Instalación y Configuración](#-instalación-y-configuración)
**Prerrequisitos**
* Java 17 o superior  
* Maven 3.8+  
* MySQL instalado y en ejecución

**Pasos para su ejecución**
1. Clona el repositorio en tu máquina local.
```bash
git clone https://github.com/dariobralo/movie-api.git
cd movie-api
```  
2. Configura tu base de datos MySQL y actualiza las propiedades de conexión en
   application.properties:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/nombredb
spring.datasource.username=tuusuario
spring.datasource.password=tucontraseña
```
  
3. Configura el servicio de envio de emails para la recuperación de cuenta.
```properties
spring.mail.username=${MAIL_USERNAME}
spring.mail.password=${MAIL_PASSWORD}
```
4. Instala las dependencias y ejecuta la aplicación.
```bash
mvn clean install
mvn spring-boot:run

```
  
## 🔑 [Autenticación y Seguridad](#-autenticación-y-seguridad)
Este proyecto utiliza JWT para gestionar sesiones de usuario:

* Inicio de sesión: Los usuarios reciben un token JWT y un refresh token.
* Expiración del token: El JWT expira en 15 minutos y se puede renovar con el refresh token
  el cual se renueva tambien al mismo tiempo.
  Por su lado el Refresh token tiene una validez de 60 minutos. Expirado este se debera volver
  a iniciar sesión
* Roles de usuario:
  * Usuario: Acceso a endpoints de lectura de datos.
  * Administrador: Acceso completo, incluyendo creación y eliminación de registros.

## 📖 [Documentación de Endpoints](#-documentación-de-endpoints)
### Documentación de API - Colección de Postman
Para facilitar las pruebas y la interacción con esta API, puedes utilizar la colección de Postman incluida en este repositorio.

Pasos para importar la colección
1. Debes tener Postman instalado.
2. Descarga el archivo de la colección con este enlace.

     [Descargar colección de Postman](https://github.com/dariobralo/movie-api/raw/main/postman/Movie-api.postman_collection.json)
   [Descargar colección de Postman](https://raw.githubusercontent.com/dariobralo/movie-api/main/postman/Movie-api.postman_collection.json)


4. En Postman, ve a File > Import y selecciona el archivo descargado.
  
  
## Contacto del desarrollador

dariobralo@gmail.com






