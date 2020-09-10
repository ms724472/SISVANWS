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
					
CREATE TABLE percentiles_oms_imc(
	id_percentil varchar(25) PRIMARY KEY NOT NULL,
    normalizador float NOT NULL,
    mediana float NOT NULL,
    coeficiente_variacion float NOT NULL
); 					
					
DELIMITER $$
DROP function IF EXISTS `diagnosticar_imc`;
CREATE FUNCTION `diagnosticar_imc` (
	sexo varchar(25),
	meses int,
    imc float
)
RETURNS VARCHAR(25)
DETERMINISTIC
BEGIN
	DECLARE valor_normalizador float;
    DECLARE valor_mediana float;
    DECLARE valor_coeficiente float;
    DECLARE resultado_lms float;
    DECLARE diagnostico varchar(25);

	SELECT normalizador, mediana, coeficiente_variacion
    INTO valor_normalizador, valor_mediana, valor_coeficiente
	FROM percentiles_oms_imc
    WHERE id_percentil = concat(sexo, meses);
    
    SET resultado_lms = (power((imc/valor_mediana), valor_normalizador) - 1) / (valor_normalizador * valor_coeficiente);

	IF resultado_lms < -2.0 THEN
		SET diagnostico = 'Bajo peso';
	ELSEIF resultado_lms <= 1.0 THEN
		SET diagnostico = "Sin exceso de peso";
	ELSEIF resultado_lms > 1.0 AND resultado_lms <= 2.0 THEN
		SET diagnostico = "Sobrepeso";
	ELSEIF resultado_lms > 2.0 THEN
		SET diagnostico = "Obesidad";
	END IF;

RETURN diagnostico;
END$$
DELIMITER ;   					