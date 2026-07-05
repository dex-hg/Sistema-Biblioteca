
-- 1. Crear la base de datos

CREATE DATABASE biblioteca;

-- 2. Creación de Tablas de Soporte y Seguridad

CREATE TABLE roles (
    id_rol SERIAL PRIMARY KEY,
    nombre VARCHAR(30) UNIQUE NOT NULL
);

CREATE TABLE usuarios (
    id_usuario SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    nombre_completo VARCHAR(100) NOT NULL,
    id_rol INT NOT NULL,
    CONSTRAINT fk_usuario_rol FOREIGN KEY(id_rol) REFERENCES roles(id_rol) ON DELETE RESTRICT
);

-- Tabla estudiantes con relación 1:1 con usuarios
CREATE TABLE estudiantes (
    id_estudiante INT PRIMARY KEY,
    codigo VARCHAR(20) UNIQUE NOT NULL,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    carrera VARCHAR(100),
    telefono VARCHAR(20),
    correo VARCHAR(100),
    CONSTRAINT fk_estudiante_usuario FOREIGN KEY(id_estudiante) REFERENCES usuarios(id_usuario) ON DELETE CASCADE
);

-- 3. Creación de Tablas del Catálogo

CREATE TABLE categorias (
    id_categoria SERIAL PRIMARY KEY,
    nombre VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE libros (
    id_libro SERIAL PRIMARY KEY,
    titulo VARCHAR(150) NOT NULL,
    autor VARCHAR(100) NOT NULL,
    editorial VARCHAR(100),
    anio_publicacion INT,
    stock INT NOT NULL CHECK (stock >= 0),
    id_categoria INT,
    CONSTRAINT fk_libro_categoria FOREIGN KEY(id_categoria) REFERENCES categorias(id_categoria) ON DELETE SET NULL
);

-- 4. Creación de Tablas Operativas (Préstamos y Multas)

CREATE TABLE prestamos (
    id_prestamo SERIAL PRIMARY KEY,
    id_estudiante INT NOT NULL,
    id_usuario INT NOT NULL, -- El bibliotecario/administrador que realiza la transacción
    fecha_prestamo DATE NOT NULL DEFAULT CURRENT_DATE,
    fecha_devolucion DATE,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO' CHECK (estado IN ('ACTIVO', 'DEVUELTO', 'ATRASADO')),
    CONSTRAINT fk_prestamo_estudiante FOREIGN KEY(id_estudiante) REFERENCES estudiantes(id_estudiante) ON DELETE CASCADE,
    CONSTRAINT fk_prestamo_usuario FOREIGN KEY(id_usuario) REFERENCES usuarios(id_usuario) ON DELETE RESTRICT
);

CREATE TABLE detalle_prestamo (
    id_detalle SERIAL PRIMARY KEY,
    id_prestamo INT NOT NULL,
    id_libro INT NOT NULL,
    cantidad INT NOT NULL DEFAULT 1 CHECK (cantidad > 0),
    CONSTRAINT fk_detalle_prestamo FOREIGN KEY(id_prestamo) REFERENCES prestamos(id_prestamo) ON DELETE CASCADE,
    CONSTRAINT fk_detalle_libro FOREIGN KEY(id_libro) REFERENCES libros(id_libro) ON DELETE RESTRICT
);

CREATE TABLE multas (
    id_multa SERIAL PRIMARY KEY,
    id_prestamo INT NOT NULL,
    monto DECIMAL(10,2) NOT NULL CHECK (monto >= 0),
    motivo VARCHAR(200),
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE' CHECK (estado IN ('PENDIENTE', 'PAGADA')),
    fecha_creacion DATE NOT NULL DEFAULT CURRENT_DATE,
    fecha_pago DATE,
    CONSTRAINT fk_multa_prestamo FOREIGN KEY(id_prestamo) REFERENCES prestamos(id_prestamo) ON DELETE CASCADE,
    CONSTRAINT chk_estado_pago CHECK (
        (estado = 'PAGADA' AND fecha_pago IS NOT NULL) OR 
        (estado = 'PENDIENTE' AND fecha_pago IS NULL)
	)
);

-- ============================================================================
-- ÍNDICES PARA OPTIMIZACIÓN DE BÚSQUEDAS
-- ============================================================================
CREATE INDEX idx_libros_titulo ON libros(LOWER(titulo));
CREATE INDEX idx_libros_autor ON libros(LOWER(autor));
CREATE INDEX idx_estudiantes_codigo ON estudiantes(codigo);
CREATE INDEX idx_usuarios_username ON usuarios(username);
CREATE INDEX idx_prestamos_estudiante ON prestamos(id_estudiante);
CREATE INDEX idx_prestamos_estado ON prestamos(estado);

-- ============================================================================
-- INSERCIÓN DE DATOS BÁSICOS E INICIALES
-- ============================================================================

-- Inserción de roles
INSERT INTO roles(nombre) VALUES
('ADMINISTRADOR'),
('BIBLIOTECARIO'),
('ESTUDIANTE');

-- Inserción de personal administrativo
INSERT INTO usuarios(username, password, nombre_completo, id_rol) VALUES
('admin', 'root', 'Administrador General', 1),
('biblio1', 'password123', 'Librera María Gómez', 2),
('biblio2', 'password123', 'Librero Carlos Ruiz', 2);

-- Inserción de estudiantes y sus usuarios correspondientes
INSERT INTO usuarios(username, password, nombre_completo, id_rol) VALUES
('estudiante1', 'estudiante123', 'Juan Pérez Soto', 3);
INSERT INTO estudiantes(id_estudiante, codigo, nombres, apellidos, carrera, telefono, correo) VALUES
(currval('usuarios_id_usuario_seq'), 'EST2026101', 'Juan', 'Pérez Soto', 'Ingeniería de Sistemas', '987654321', 'juan.perez@universidad.edu');

INSERT INTO usuarios(username, password, nombre_completo, id_rol) VALUES
('estudiante2', 'estudiante123', 'Ana Lucía Torres', 3);
INSERT INTO estudiantes(id_estudiante, codigo, nombres, apellidos, carrera, telefono, correo) VALUES
(currval('usuarios_id_usuario_seq'), 'EST2026102', 'Ana Lucía', 'Torres', 'Derecho', '912345678', 'ana.torres@universidad.edu');

INSERT INTO usuarios(username, password, nombre_completo, id_rol) VALUES
('estudiante3', 'estudiante123', 'Luis Mendoza', 3);
INSERT INTO estudiantes(id_estudiante, codigo, nombres, apellidos, carrera, telefono, correo) VALUES
(currval('usuarios_id_usuario_seq'), 'EST2026103', 'Luis', 'Mendoza', 'Arquitectura', '933445566', 'luis.mendoza@universidad.edu');

INSERT INTO usuarios(username, password, nombre_completo, id_rol) VALUES
('estudiante4', 'estudiante123', 'Sofía Castro', 3);
INSERT INTO estudiantes(id_estudiante, codigo, nombres, apellidos, carrera, telefono, correo) VALUES
(currval('usuarios_id_usuario_seq'), 'EST2026104', 'Sofía', 'Castro', 'Medicina', '998877665', 'sofia.castro@universidad.edu');

-- Inserción de categorías
INSERT INTO categorias(nombre) VALUES
('Ciencias de la Computación'),
('Literatura'),
('Matemáticas'),
('Historia'),
('Física'),
('Biología'),
('Derecho');

-- Inserción de libros
INSERT INTO libros(titulo, autor, editorial, anio_publicacion, stock, id_categoria) VALUES
('Introducción a los Algoritmos', 'Thomas H. Cormen', 'MIT Press', 2009, 5, 1),
('El amor en los tiempos del cólera', 'Gabriel García Márquez', 'Editorial Sudamericana', 1985, 3, 2),
('Cálculo de una Variable', 'James Stewart', 'Cengage Learning', 2015, 4, 3),
('Sistemas Operativos Modernos', 'Andrew S. Tanenbaum', 'Pearson', 2014, 6, 1),
('Historia del Tiempo', 'Stephen Hawking', 'Bantam Books', 1988, 2, 5),
('Biología Celular y Molecular', 'Harvey Lodish', 'Panamericana', 2016, 3, 6),
('Cien años de soledad', 'Gabriel García Márquez', 'Editorial Sudamericana', 1967, 4, 2),
('Álgebra Lineal', 'Stanley I. Grossman', 'McGraw-Hill', 2012, 7, 3),
('Breve historia de la humanidad', 'Yuval Noah Harari', 'Debate', 2014, 5, 4),
('Introducción al Derecho', 'Aníbal Torres Vásquez', 'Idemsa', 2019, 4, 7);

-- Inserción de préstamos de prueba
-- Préstamo 1: Devuelto
INSERT INTO prestamos(id_estudiante, id_usuario, fecha_prestamo, fecha_devolucion, estado) VALUES
(4, 2, '2026-06-10', '2026-06-15', 'DEVUELTO');
INSERT INTO detalle_prestamo(id_prestamo, id_libro, cantidad) VALUES
(currval('prestamos_id_prestamo_seq'), 1, 1),
(currval('prestamos_id_prestamo_seq'), 4, 1);

-- Préstamo 2: Activo (Aún dentro del plazo de préstamo)
INSERT INTO prestamos(id_estudiante, id_usuario, fecha_prestamo, fecha_devolucion, estado) VALUES
(5, 3, '2026-07-01', NULL, 'ACTIVO');
INSERT INTO detalle_prestamo(id_prestamo, id_libro, cantidad) VALUES
(currval('prestamos_id_prestamo_seq'), 10, 1);

-- Préstamo 3: Atrasado
INSERT INTO prestamos(id_estudiante, id_usuario, fecha_prestamo, fecha_devolucion, estado) VALUES
(6, 2, '2026-05-20', NULL, 'ATRASADO');
INSERT INTO detalle_prestamo(id_prestamo, id_libro, cantidad) VALUES
(currval('prestamos_id_prestamo_seq'), 3, 1),
(currval('prestamos_id_prestamo_seq'), 8, 1);

-- Inserción de multas correspondientes a préstamos atrasados
INSERT INTO multas(id_prestamo, monto, motivo, estado, fecha_creacion) VALUES
(3, 15.50, 'Retraso en la devolución', 'PENDIENTE', '2026-06-25');
