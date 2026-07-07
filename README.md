# Sistema de Gestión de Biblioteca

Sistema de escritorio desarrollado en Java para administrar el catálogo, 
préstamos, devoluciones, multas y reportes de una biblioteca académica. El 
proyecto conserva una arquitectura por capas con MVC, servicios, DAO y 
persistencia en PostgreSQL, usando Swing con FlatLaf para la interfaz gráfica.

## Objetivo

El objetivo principal del sistema es demostrar la aplicación práctica de 
estructuras de datos y algoritmos propios dentro de un caso real de gestión 
bibliotecaria. Por ello, además de resolver operaciones funcionales como 
registrar préstamos, controlar devoluciones y consultar reportes, el proyecto 
integra estructuras personalizadas como `ListaEnlazada`, `Pila` y `Cola`, 
junto con algoritmos propios de búsqueda y ordenamiento.

Este enfoque permite reforzar los contenidos de Algoritmos y Estructuras de 
Datos mediante lógica aplicada en pantallas, servicios y reportes, no solo en 
pruebas aisladas.

## Funcionalidades principales

- Inicio de sesión por rol de usuario.
- Panel principal con indicadores del sistema.
- Gestión del catálogo de libros.
- Registro de préstamos y devoluciones.
- Carrito de préstamo con soporte para deshacer y rehacer libros agregados.
- Consulta rápida de libros y estudiantes desde la ventana de préstamos.
- Gestión de multas pendientes y pagadas.
- Historial rápido de préstamos desde la ventana de multas.
- Reportes administrativos de préstamos, libros más prestados y multas.
- Filtros y ordenamientos en memoria usando estructuras y algoritmos propios.

## Roles del sistema

- Administrador: acceso a catálogo, préstamos, multas y reportes.
- Bibliotecario: acceso operativo a catálogo, préstamos y multas.
- Estudiante: consulta de sus préstamos y multas.

## Uso de estructuras de datos y algoritmos

El proyecto incluye implementaciones propias en el paquete 
`biblioteca.estructuras`:

- `ListaEnlazada<T>`: estructura principal para transportar colecciones entre 
DAO, servicios, controladores y vistas.
- `Pila<T>`: usada para flujos de deshacer/rehacer en el registro de préstamos.
- `Cola<T>`: usada para flujos FIFO, como la atención de devoluciones.
- `AlgoritmosOrdenamiento`: QuickSort propio para ordenar arreglos y 
`ListaEnlazada`.
- `AlgoritmosBusqueda`: búsqueda lineal y búsqueda binaria para arreglos y 
`ListaEnlazada`.

Estos componentes se usan en operaciones reales del sistema, por ejemplo:

- Ordenamiento de préstamos, multas y reportes.
- Búsqueda y filtrado de libros, estudiantes y registros históricos.
- Manejo temporal de libros agregados al préstamo.
- Ranking de libros más prestados.

## Arquitectura

El código está organizado en paquetes según responsabilidad:

```text
src/main/java/biblioteca
|-- conexion      # Configuración y manejo de conexión JDBC
|-- controlador   # Controladores entre vistas y servicios
|-- dao           # Interfaces e implementaciones DAO
|-- estructuras   # Estructuras y algoritmos propios
|-- modelo        # Entidades del dominio
|-- servicios     # Reglas de negocio
|-- vista         # Interfaz gráfica Swing
```

La aplicación sigue una separación por capas:

```text
Vista Swing -> Controlador -> Servicio -> DAO -> PostgreSQL
```

## Tecnologías utilizadas

- Java 23
- Maven
- Swing
- FlatLaf
- PostgreSQL
- JDBC
- JUnit 5

## Requisitos

- JDK 23 o compatible con la configuración del proyecto.
- Maven instalado.
- PostgreSQL en ejecución.
- Base de datos local

La conexión se configura en:

```text
src/main/java/biblioteca/conexion/ConexionConfig.java
```

Valores actuales:

```java
URL = "jdbc:postgresql://localhost:5432/biblioteca"
USER = "postgres"
PASSWORD = "root"
```

Si tu instalación local usa otro usuario, contraseña, puerto o nombre de base 
de datos, modifica esos valores antes de ejecutar la aplicación.

## Notas sobre base de datos

Este repositorio contiene el código Java de la aplicación. Para que el sistema 
funcione completamente, la base de datos PostgreSQL debe tener las tablas 
esperadas por los DAO, incluyendo usuarios, roles, libros, estudiantes, 
préstamos, detalles de préstamo y multas.

El script SQL de creación y carga inicial se encuentra en:

```text
.scriptsSQL/Esquema de inicialización.sql
```

Ese script crea la base de datos `biblioteca`, sus tablas, restricciones y 
datos iniciales de prueba. Contiene `CREATE DATABASE biblioteca;` que debe 
ejecutarse primero. Luego, conectarse a esa base de datos y ejecutar el resto 
del script.

## Ejecución

Desde NetBeans:

1. Abrir el proyecto Maven.
2. Verificar que PostgreSQL esté activo.
3. Ejecutar la clase principal:

```text
src/main/java/biblioteca/Main.java
```

Desde terminal:

```bash
mvn clean compile
mvn exec:java
```

## Pruebas

El proyecto tiene pruebas automatizadas con JUnit 5 y pruebas manuales tipo 
`main` dentro de `src/test/java`.

Para ejecutar las pruebas automatizadas:

```bash
mvn test
```

Las pruebas cubren principalmente:

- Operaciones de `ListaEnlazada`, `Pila` y `Cola`.
- QuickSort y búsquedas propias.
- Validaciones de servicios.
- Casos de multas y actualización de estado de pago.

## Estado del proyecto

Proyecto académico en evolución, enfocado en reforzar el uso real de 
estructuras de datos y algoritmos propios dentro de un sistema de biblioteca 
funcional.
