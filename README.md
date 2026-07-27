## Descripción

Plataforma mobile de subastas desarrollada con React Native y Spring Boot.

El sistema permite que usuarios registrados participen en remates en tiempo real, publiquen artículos para futuras subastas y gestionen las operaciones posteriores a una adjudicación.

El proyecto modela el ciclo completo de una subasta:

- registro y validación de usuarios;
- configuración de medios de pago;
- solicitud e inspección de artículos;
- contratación y revisión de pólizas;
- creación de catálogos y lotes;
- pujas concurrentes en tiempo real;
- cierre automático de lotes;
- adjudicación del bien;
- confirmación del pago;
- envío o retiro;
- seguimiento de la entrega.

La aplicación fue construida alrededor de reglas de negocio reales, con validaciones de dominio, control de concurrencia, operaciones transaccionales y persistencia de los eventos principales.


#Capturas



## Arquitectura

El proyecto está dividido en dos aplicaciones:

### Backend

- Java 17
- Spring Boot
- Spring Security
- JWT
- Spring Data JPA
- Hibernate
- PostgreSQL
- WebSocket
- Maven

El backend organiza la lógica en:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
Database
