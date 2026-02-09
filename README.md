# Simple Java HTTP Server

Servidor HTTP básico implementado en Java usando `ServerSocket`.

## Funcionalidades
- Manejo de múltiples conexiones con hilos
- Soporte para solicitudes GET
- Servir recursos estáticos desde la carpeta `public`
- Manejo de error 404 personalizado

## Rutas disponibles
- http://localhost:8080/index.html
- http://localhost:8080/jiraiya.gif
- http://localhost:8080/mario.jpg
- Cualquier otra ruta, llevará al error 404 Not Found

## Estructura del proyecto
public/
├── index.html
├── jiraiya.gif
├── mario.jpg
└── 404.html
src/
├── Main.java
└── HttpProcess.java
