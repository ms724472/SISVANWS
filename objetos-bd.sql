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
                    masa DECIMAL(3,1) NOT NULL,
                    diagnostico_peso varchar(30),
                    z_peso DECIMAL(7, 5),
                    estatura DECIMAL(4,1) NOT NULL,
                    diagnostico_talla varchar(30),
                    z_talla DECIMAL(7, 5),
                    imc DECIMAL(4,2) AS (masa/POWER((estatura/100), 2)) NOT NULL,
                    diagnostico_imc varchar(30),
                    z_imc DECIMAL(7, 5),
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

CREATE TABLE percentiles_oms_talla(
	id_percentil varchar(25) PRIMARY KEY NOT NULL,
    normalizador float NOT NULL,
    mediana float NOT NULL,
    coeficiente_variacion float NOT NULL
); 

CREATE TABLE percentiles_oms_peso(
	id_percentil varchar(25) PRIMARY KEY NOT NULL,
    normalizador float NOT NULL,
    mediana float NOT NULL,
    coeficiente_variacion float NOT NULL
); 					

DROP function IF EXISTS `actualizar_puntajes`;

DELIMITER $$
CREATE FUNCTION `actualizar_puntajes` (
	alumno INT,
	fecha_medicion DATE
)
RETURNS INT
DETERMINISTIC
BEGIN
	DECLARE sexo_alumno VARCHAR(10);
    DECLARE meses INT;
    DECLARE imc_alumno DECIMAL(4,2);
    DECLARE peso_alumno DECIMAL(3,1);
	DECLARE talla_alumno DECIMAL(4,1);
	
    SELECT sexo, timestampdiff(MONTH, alumnos.fecha_nac, fecha_medicion) 
    INTO sexo_alumno, meses
    FROM alumnos
    WHERE id_alumno = alumno;
    
    SELECT imc, masa, estatura
    INTO imc_alumno, peso_alumno, talla_alumno
    FROM datos
    WHERE id_alumno = alumno AND fecha = fecha_medicion;
    
    UPDATE datos SET z_imc = calcular_z_imc(sexo_alumno, meses, imc_alumno),
					 diagnostico_imc = diagnosticar_imc(z_imc),
                     z_peso = calcular_z_peso(sexo_alumno, meses, peso_alumno),
                     diagnostico_peso = diagnosticar_peso(z_peso),
                     z_talla = calcular_z_talla(sexo_alumno, meses, talla_alumno),
                     diagnostico_talla = diagnosticar_talla(z_talla)
	WHERE id_alumno = alumno AND fecha = fecha_medicion;
    
RETURN 1;
END$$
DELIMITER ;    

DROP function IF EXISTS `calcular_z_imc`;

DELIMITER $$
CREATE FUNCTION `calcular_z_imc` (
	sexo varchar(25),
	meses int,
    imc float
)
RETURNS DECIMAL(7,5)
DETERMINISTIC
BEGIN
	DECLARE valor_normalizador float;
    DECLARE valor_mediana float;
    DECLARE valor_coeficiente float;
    DECLARE resultado_lms DECIMAL(7,5);

	SELECT normalizador, mediana, coeficiente_variacion
    INTO valor_normalizador, valor_mediana, valor_coeficiente
	FROM percentiles_oms_imc
    WHERE id_percentil = concat(sexo, meses);
    
    SET resultado_lms = (power((imc/valor_mediana), valor_normalizador) - 1) / (valor_normalizador * valor_coeficiente);

RETURN resultado_lms;
END$$
DELIMITER ;    

DROP function IF EXISTS `diagnosticar_imc`;

DELIMITER $$
CREATE FUNCTION `diagnosticar_imc` (	
    puntaje_z float
)
RETURNS VARCHAR(25)
DETERMINISTIC
BEGIN	
    DECLARE diagnostico varchar(25);
	
	IF puntaje_z < -2.0 THEN
		SET diagnostico = 'Bajo peso';
	ELSEIF puntaje_z <= 1.0 THEN
		SET diagnostico = "Sin exceso de peso";
	ELSEIF puntaje_z > 1.0 AND resultado_lms <= 2.0 THEN
		SET diagnostico = "Sobrepeso";
	ELSEIF resultado_lms > 2.0 THEN
		SET puntaje_z = "Obesidad";
	END IF;

RETURN diagnostico;
END$$
DELIMITER ;    

DROP function IF EXISTS `calcular_z_talla`;

DELIMITER $$
CREATE FUNCTION `calcular_z_talla` (
	sexo varchar(25),
	meses int,
    talla float
)
RETURNS DECIMAL(7,5)
DETERMINISTIC
BEGIN
	DECLARE valor_normalizador float;
    DECLARE valor_mediana float;
    DECLARE valor_coeficiente float;
    DECLARE resultado_lms float;

	SELECT normalizador, mediana, coeficiente_variacion
    INTO valor_normalizador, valor_mediana, valor_coeficiente
	FROM percentiles_oms_talla
    WHERE id_percentil = concat(sexo, meses);
    
    SET resultado_lms = (power((talla/valor_mediana), valor_normalizador) - 1) / (valor_normalizador * valor_coeficiente);

RETURN resultado_lms;
END$$
DELIMITER ;  

DROP function IF EXISTS `diagnosticar_talla`;

DELIMITER $$
CREATE FUNCTION `diagnosticar_talla` (
	puntaje_z float
)
RETURNS VARCHAR(25)
DETERMINISTIC
BEGIN
    DECLARE diagnostico varchar(25);

	IF puntaje_z < -2.0 THEN
		SET diagnostico = 'Con talla baja';
	ELSE 
		SET diagnostico = 'Sin talla baja';
	END IF;

RETURN diagnostico;
END$$
DELIMITER ;  

DROP function IF EXISTS `calcular_z_peso`;

DELIMITER $$
CREATE FUNCTION `calcular_z_peso` (
	sexo varchar(25),
	meses int,
    peso float
)
RETURNS DECIMAL(7,5)
DETERMINISTIC
BEGIN
	DECLARE valor_normalizador float;
    DECLARE valor_mediana float;
    DECLARE valor_coeficiente float;
    DECLARE resultado_lms float;

	IF meses > 120 THEN
		RETURN NULL;
	END IF;
    
	SELECT normalizador, mediana, coeficiente_variacion
    INTO valor_normalizador, valor_mediana, valor_coeficiente
	FROM percentiles_oms_peso
    WHERE id_percentil = concat(sexo, meses);
    
    SET resultado_lms = (power((peso/valor_mediana), valor_normalizador) - 1) / (valor_normalizador * valor_coeficiente);

RETURN resultado_lms;
END$$
DELIMITER ;

DROP function IF EXISTS `diagnosticar_peso`;

DELIMITER $$
CREATE FUNCTION `diagnosticar_peso` (
	puntaje_z float
)
RETURNS VARCHAR(25)
DETERMINISTIC
BEGIN
    DECLARE diagnostico varchar(25);
	
	IF puntaje_z IS NULL THEN
		SET diagnostico = NULL;
	ELSEIF puntaje_z < -2.0 THEN
		SET diagnostico = 'Con peso bajo';
	ELSE 
		SET diagnostico = 'Sin peso bajo';
	END IF;

RETURN diagnostico;
END$$
DELIMITER ;    								