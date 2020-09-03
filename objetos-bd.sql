CREATE TABLE usuarios(correo varchar(255) PRIMARY KEY NOT NULL,
						nombre varchar(255) NOT NULL,
                        contrasenia varchar(255) NOT NULL,
                        roles BLOB);

CREATE TABLE escuelas(id_escuela INT PRIMARY KEY NOT NULL,
						nombre varchar(255) NOT NULL,
                        direccion varchar(255) NOT NULL,
                        municipio varchar(100)  NOT NULL,
                        estado varchar(50) NOT NULL);
                        
CREATE TABLE grupos(id_grupo INT PRIMARY KEY NOT NULL,
					grado INT NOT NULL,
                    letra varchar(1),
                    anio_ingreso INT NOT NULL,
                    anio_graduacion INT,
                    id_escuela INT NOT NULL,
                    FOREIGN KEY (id_escuela) REFERENCES escuelas(id_escuela)
);                   

CREATE TABLE alumnos(id_alumno INT PRIMARY KEY NOT NULL,
					nombre varchar(30) NOT NULL,
                    apellido_p varchar(30) NOT NULL,
                    apellido_m varchar(30) NOT NULL,
                    sexo ENUM('masculino', 'femenino') NOT NULL,
                    fecha_nac DATE NOT NULL,
                    id_grupo INT NOT NULL,
                    FOREIGN KEY (id_grupo) REFERENCES grupos(id_grupo));
                    
CREATE TABLE datos(id_alumno INT NOT NULL,
					fecha DATE NOT NULL,
                    masa FLOAT,
                    estatura FLOAT,
                    perimetro_cuello FLOAT,
                    cintura FLOAT,
                    triceps FLOAT,
                    subescapula FLOAT,
                    pliegue_cuello FLOAT,
                    FOREIGN KEY (id_alumno) REFERENCES alumnos(id_alumno));                    