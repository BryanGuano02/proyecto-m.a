# Usa la imagen de Tomcat con JDK 21
FROM tomcat:10.1.40-jdk21-temurin-jammy

# Copia el archivo WAR a la carpeta de despliegue de Tomcat
COPY ./target/ROOT.war /usr/local/tomcat/webapps/ROOT.war

# Comando para iniciar Tomcat
CMD ["catalina.sh", "run"]
