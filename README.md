# Parcelas: pruebas unitarias en Spring Boot

Práctica de Desarrollo Web Integral, Unidad 3, UTCH.

Aplicación Spring Boot 3.5 (Java 17) con una capa de servicio que administra parcelas de huertos urbanos, y pruebas unitarias con JUnit 5, Mockito y AssertJ que validan la recuperación y el registro de datos sin necesidad de una base de datos.

## Estructura
src/main/java/com/utch/parcelas/ model/ Parcel (entidad JPA) repository/ ParcelRepository (Spring Data JPA) service/ ParcelService: findAll, findById, create controller/ ParcelController, ParcelRequest, GlobalExceptionHandler exception/ ParcelNotFoundException, DuplicateParcelException src/test/java/com/utch/parcelas/ service/ ParcelServiceTest

## Requisitos

JDK 17 o superior y Maven 3.9 o superior.

## Ejecutar las pruebas
mvn test

El reporte debe terminar con `BUILD SUCCESS`. Los reportes de Surefire quedan en `target/surefire-reports`.

## Qué se prueba

| Método | Escenario | Verificación |
| --- | --- | --- |
| `findAll` | Hay parcelas | Devuelve la lista del repositorio, en orden, y llama una vez a `findAll` |
| `findAll` | No hay parcelas | Devuelve una lista vacía |
| `findById` | Existe | Devuelve la parcela |
| `findById` | No existe | Lanza `ParcelNotFoundException` |
| `create` | Datos válidos | Limpia el nombre, llama a `save` y conserva los atributos |
| `create` | Nombre repetido | Lanza `DuplicateParcelException` y no guarda |
| `create` | Nombre nulo o vacío, o parcela nula | Lanza `IllegalArgumentException` y no usa el repositorio |

El repositorio se sustituye con `@Mock` y el servicio se crea con `@InjectMocks`, por lo que las pruebas no arrancan Spring ni usan base de datos. Las aserciones usan AssertJ.

## Ejecutar la aplicación
mvn spring-boot:run

Se usa una base H2 en memoria, así que no requiere instalar nada más.

| Método | Ruta | Descripción |
| --- | --- | --- |
| GET | `/api/v1/parcels` | Lista las parcelas |
| GET | `/api/v1/parcels/{id}` | Detalle de una parcela |
| POST | `/api/v1/parcels` | Registra una parcela (201 con encabezado `Location`) |
curl -i -X POST http://localhost:8080/api/v1/parcels
-H "Content-Type: application/json"
-d '{"name":"Huerto Norte","soilType":"arcilloso","ownerOrLead":"Ana López"}'
