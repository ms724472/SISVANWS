/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iteso.sisvan.ws;

import static iteso.sisvan.ws.SISVANUtils.DB_JNDI;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.Produces;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.*;
import java.util.Arrays;
import java.util.List;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response.ResponseBuilder;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;

/**
 * REST Web Service
 *
 * @author edgartiburcio
 */
@Path("wls/1.0")
public class SISVANWS {
    /**
     * Agrega un nuevo alumno a la base de datos
     *
     * @param cuerpoPeticion contiene todos los datos necesarios para crear un
     * nuevo alumno
     * @return json mostrando si fue exitosa o no la creacion
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/alumnos/agregarAlumno")
    public Response agregarAlumno(String cuerpoPeticion) {
        String query = "INSERT INTO alumnos(id_alumno, " + "\n"
                + "nombre," + "\n"
                + "apellido_p," + "\n"
                + "apellido_m," + "\n"
                + "sexo," + "\n"
                + "fecha_nac," + "\n"
                + "id_grupo)" + "\n"
                + "VALUES(?,?,?,?,?,?,?)";

        String[] nombresColumnas = {"id_alumno", "nombre", "apellido_p", "apellido_m", "sexo", "fecha_nac", "id_grupo"};

        return Response.ok(SISVANUtils.insertarNuevoDatoEnBD(cuerpoPeticion, nombresColumnas, query).toString()).header("Access-Control-Allow-Origin", "*").build();
    }
    
    /**
     * Agrega un nueva escuela a la base de datos
     *
     * @param cuerpoPeticion contiene todos los datos necesarios para crear un
     * nueva escuela
     * @return json mostrando si fue exitosa o no la creacion
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/escuelas/agregarEscuela")
    public Response agregarEscuela(String cuerpoPeticion) {
        String query = "INSERT INTO escuelas(clave_sep, "+ "\n"
                + "nombre," + "\n"
                + "direccion," + "\n"
                + "colonia," + "\n"
                + "codigo_postal," + "\n"
                + "telefono," + "\n"
                + "municipio," + "\n"
                + "estado)" + "\n"
                + "VALUES(?,?,?,?,?,?,?,?)";

        String[] nombresColumnas = {"clave_sep", "nombre", "direccion", "colonia", "codigo_postal", "telefono", "municipio", "estado"};

        return Response.ok(SISVANUtils.insertarNuevoDatoEnBD(cuerpoPeticion, nombresColumnas, query).toString()).header("Access-Control-Allow-Origin", "*").build();
    }
    
    /**
     * Actualizar la informacion de un escuela en base de datos
     *
     * @param cuerpoPeticion contiene todos los datos necesarios para
     * actualizar una escuela.
     * @return json mostrando si fue exitosa o no la actualizacion
     */
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/escuelas/actualizarEscuela")
    public Response actualizarEscuela(String cuerpoPeticion) {
        String query = "UPDATE escuelas SET " + "\n"
                + "nombre = ?, " + "\n"
                + "clave_sep = ?, " + "\n"
                + "direccion = ?, " + "\n"
                + "colonia = ?, " + "\n"
                + "codigo_postal = ?," + "\n"
                + "telefono = ?, " + "\n"
                + "municipio = ?, " + "\n"
                + "estado = ? " + "\n"
                + "WHERE id_escuela = ?";

        String[] nombresColumnas = {"nombre", "clave_sep", "direccion", "colonia", "codigo_postal", "telefono", "municipio", "estado", "id_escuela"};

        return Response.ok(SISVANUtils.insertarNuevoDatoEnBD(cuerpoPeticion, nombresColumnas, query).toString()).header("Access-Control-Allow-Origin", "*").build();
    }
    
    /**
     * Agrega un nuevo grupo a la base de datos
     *
     * @param cuerpoPeticion contiene todos los datos necesarios para crear un
     * nuevo grupo
     * @return json mostrando si fue exitosa o no la creacion
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/escuelas/agregarGrupo")
    public Response agregarGrupo(String cuerpoPeticion) {
        String query = "INSERT INTO grupos(letra, "+ "\n"
                + "anio_ingreso," + "\n"
                + "id_escuela)" + "\n"
                + "VALUES(?,?,?)";

        String[] nombresColumnas = {"letra", "anio_ingreso", "id_escuela"};

        return Response.ok(SISVANUtils.insertarNuevoDatoEnBD(cuerpoPeticion, nombresColumnas, query).toString()).header("Access-Control-Allow-Origin", "*").build();
    }
    
    /**
     * Actualizar la informacion de un grupo en base de datos
     *
     * @param cuerpoPeticion contiene todos los datos necesarios para
     * actualizar un grupo.
     * @return json mostrando si fue exitosa o no la actualizacion
     */
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/escuelas/actualizarGrupo")
    public Response actualizarGrupo(String cuerpoPeticion) {
        String query = "UPDATE grupos SET " + "\n"
                + "letra = ?, " + "\n"
                + "anio_ingreso = ? " + "\n"
                + "WHERE id_grupo = ?";

        String[] nombresColumnas = {"letra", "anio_ingreso", "id_grupo"};

        return Response.ok(SISVANUtils.insertarNuevoDatoEnBD(cuerpoPeticion, nombresColumnas, query).toString()).header("Access-Control-Allow-Origin", "*").build();
    }
    
    /**
     * Actualizar la informacion de un alumno en base de datos
     *
     * @param cuerpoPeticion contiene todos los datos necesarios para
     * actualizar un alumno.
     * @return json mostrando si fue exitosa o no la actualizacion
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/alumnos/actualizarAlumno")
    public Response actualizarAlumno(String cuerpoPeticion) {
        String query = "UPDATE alumnos SET " + "\n"
                + "nombre = ?, " + "\n"
                + "apellido_p = ?, " + "\n"
                + "apellido_m = ?, " + "\n"
                + "sexo = ?, " + "\n"
                + "fecha_nac = ?," + "\n"
                + "id_grupo = ? " + "\n"
                + "WHERE id_alumno = ?";

        String[] nombresColumnas = {"nombre", "apellido_p", "apellido_m", "sexo", "fecha_nac", "id_grupo", "id_alumno"};

        return Response.ok(SISVANUtils.insertarNuevoDatoEnBD(cuerpoPeticion, nombresColumnas, query).toString()).header("Access-Control-Allow-Origin", "*").build();
    }

    /**
     * Agrega una nueva medicion a un alumno existente
     *
     * @param cuerpoPeticion contiene los nuevos datos de medicion
     * @return json mostrando si fue exitosa o no la creacion
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/alumnos/agregarMedicion")
    public Response agregarMedicion(String cuerpoPeticion) {
        String query = "INSERT INTO datos(id_alumno, " + "\n"
                + "id_grupo," + "\n"
                + "fecha," + "\n"
                + "masa," + "\n"
                + "estatura," + "\n"
                + "perimetro_cuello," + "\n"
                + "cintura, triceps," + "\n"
                + "subescapula," + "\n"
                + "pliegue_cuello)" + "\n"
                + "VALUES(var_sustitucion)";
        String fecha;
        String id_alumno;
        String id_grupo;

        String[] nombresColumnas = {"id_alumno", "id_grupo", "fecha", "masa", "estatura", "perimetro_cuello", "cintura", "triceps", "subescapula", "pliegue_cuello"};

        try (JsonReader bodyReader = Json.createReader(new StringReader(cuerpoPeticion))) {
            JsonObject datosEntrada = bodyReader.readObject();
            List<String> columnas = Arrays.asList(nombresColumnas);
            
            fecha = datosEntrada.getString("fecha");
            id_alumno = datosEntrada.getString("id_alumno"); 
            id_grupo = datosEntrada.getString("id_grupo"); 
            
            if (!datosEntrada.containsKey("perimetro_cuello")) {
                query = query.replace("perimetro_cuello,", "");
                columnas.remove("perimetro_cuello");
            }

            if (!datosEntrada.containsKey("cintura")) {
                query = query.replace("cintura,", "");
                columnas.remove("cintura");
            }

            if (!datosEntrada.containsKey("triceps")) {
                query = query.replace("triceps,", "");
                columnas.remove("triceps");
            }
            
            if (!datosEntrada.containsKey("subescapula")) {
                query = query.replace("subescapula,", "");
                columnas.remove("subescapula");
            }

            if (!datosEntrada.containsKey("pliegue_cuello")) {
                query = query.replace("pliegue_cuello", "");
                columnas.remove("pliegue_cuello");
            }

            String varSustitucion = "?";
            nombresColumnas = columnas.toArray(new String[columnas.size()]);

            for (int indice = 1; indice < datosEntrada.keySet().size(); indice++) {
                varSustitucion += ",?";
            }

            query = query.replace("var_sustitucion", varSustitucion);
        }
        
        JsonObject respuestaInserccion = SISVANUtils.insertarNuevoDatoEnBD(cuerpoPeticion, nombresColumnas, query);
        
        if(respuestaInserccion.containsKey("status") && respuestaInserccion.getString("status").equals("exito")) {
            String actualizacionPuntajes = "SELECT actualizar_puntajes(?, ?, ?)";
            DataSource datasource;
            JsonObjectBuilder jsonObjectBuilder
                = Json.createObjectBuilder();
            
            try {
                datasource = (DataSource) new InitialContext().lookup(DB_JNDI);
            } catch (NamingException ex) {
                jsonObjectBuilder.add("error", "Error al intentar obtener la conexión con la base de datos.");
                jsonObjectBuilder.add("mensaje", ex.getMessage());
                respuestaInserccion = jsonObjectBuilder.build();
                return Response.ok(respuestaInserccion.toString()).header("Access-Control-Allow-Origin", "*").build();
            }

            //Inicializando la conexión a la base de datos
            try (Connection dbConnection = datasource.getConnection();
                    PreparedStatement statement = dbConnection.prepareStatement(actualizacionPuntajes)) {
                statement.setString(1, id_alumno);
                statement.setString(2, id_grupo);
                statement.setString(3, fecha);
                
                try(ResultSet resultado = statement.executeQuery()) {
                    if(!resultado.next()) {
                        respuestaInserccion.remove("status");
                        respuestaInserccion.put("status", JsonValue.FALSE);
                    }
                }
            } catch (SQLException exception) {
                jsonObjectBuilder.add("error", "Error al actualizar los puntajes z.");
                jsonObjectBuilder.add("mensaje", exception.getMessage());
                respuestaInserccion = jsonObjectBuilder.build();
                return Response.ok(respuestaInserccion.toString()).header("Access-Control-Allow-Origin", "*").build();
            }
        }

        return Response.ok(respuestaInserccion.toString()).header("Access-Control-Allow-Origin", "*").build();
    }
    
    /**
     * Actualizar una medicion de un alumno existente
     *
     * @param cuerpoPeticion contiene los datos de medicion
     * @return json mostrando si fue exitosa o no la actualizacion
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/alumnos/actualizarMedicion")
    public Response actualizarMedicion(String cuerpoPeticion) {
        String query = "UPDATE datos SET" + "\n"
                + "id_grupo = ?," + "\n"
                + "masa = ?," + "\n"
                + "estatura = ?," + "\n"
                + "perimetro_cuello =?," + "\n"
                + "cintura = ?, triceps =?," + "\n"
                + "subescapula = ?," + "\n"
                + "pliegue_cuello = ?" + "\n"
                + "WHERE id_alumno = ? AND fecha = ?";
        String fecha;
        String id_alumno;
        String id_grupo;

        String[] nombresColumnas = {"id_grupo", "masa", "estatura", "perimetro_cuello", "cintura", "triceps", "subescapula", "pliegue_cuello", "id_alumno", "fecha"};

        try (JsonReader bodyReader = Json.createReader(new StringReader(cuerpoPeticion))) {
            JsonObject datosEntrada = bodyReader.readObject();
            
            fecha = datosEntrada.getString("fecha");
            id_alumno = datosEntrada.getString("id_alumno");      
            id_grupo = datosEntrada.getString("id_grupo");      
        }
        
        JsonObject respuestaInserccion = SISVANUtils.insertarNuevoDatoEnBD(cuerpoPeticion, nombresColumnas, query);
        
        if(respuestaInserccion.containsKey("status") && respuestaInserccion.getString("status").equals("exito")) {
            String actualizacionPuntajes = "SELECT actualizar_puntajes(?, ?, ?)";
            DataSource datasource;
            JsonObjectBuilder jsonObjectBuilder
                = Json.createObjectBuilder();
            
            try {
                datasource = (DataSource) new InitialContext().lookup(DB_JNDI);
            } catch (NamingException ex) {
                jsonObjectBuilder.add("error", "Error al intentar obtener la conexión con la base de datos.");
                jsonObjectBuilder.add("mensaje", ex.getMessage());
                respuestaInserccion = jsonObjectBuilder.build();
                return Response.ok(respuestaInserccion.toString()).header("Access-Control-Allow-Origin", "*").build();
            }

            //Inicializando la conexión a la base de datos
            try (Connection dbConnection = datasource.getConnection();
                    PreparedStatement statement = dbConnection.prepareStatement(actualizacionPuntajes)) {
                statement.setString(1, id_alumno);
                statement.setString(2, id_grupo);
                statement.setString(3, fecha);
                
                try(ResultSet resultado = statement.executeQuery()) {
                    if(!resultado.next()) {
                        respuestaInserccion.remove("status");
                        respuestaInserccion.put("status", JsonValue.FALSE);
                    }
                }
            } catch (SQLException exception) {
                jsonObjectBuilder.add("error", "Error al actualizar los puntajes z.");
                jsonObjectBuilder.add("mensaje", exception.getMessage());
                respuestaInserccion = jsonObjectBuilder.build();
                return Response.ok(respuestaInserccion.toString()).header("Access-Control-Allow-Origin", "*").build();
            }
        }

        return Response.ok(respuestaInserccion.toString()).header("Access-Control-Allow-Origin", "*").build();
    }
    
    @POST
    @Path("sincronizarDatosMasivos")
    public Response sincronizarDatosMasivos(String datosMasivos) {
        JsonObject datosJSON = Json.createReader(new StringReader(datosMasivos)).readObject(); 
        JsonArray alumnosInsertar = datosJSON.getJsonArray("alumnosInsertar");
        JsonArray alumnosActualizar = datosJSON.getJsonArray("alumnosActualizar");
        JsonArray datosInsertar = datosJSON.getJsonArray("datosInsertar");
        JsonArray datosActualizar = datosJSON.getJsonArray("datosActualizar");
        DataSource datasource;
        
        final String queryInsertarAlumno = "INSERT INTO alumnos(id_alumno, " + "\n"
                + "nombre," + "\n"
                + "apellido_p," + "\n"
                + "apellido_m," + "\n"
                + "sexo," + "\n"
                + "fecha_nac," + "\n"
                + "id_grupo)" + "\n"
                + "VALUES(?,?,?,?,?,?,?)";
        
        final String queryActualizarAlumno = "UPDATE alumnos SET\n"
                + "nombre = ?,\n"
                + "apellido_p = ?,\n"
                + "apellido_m = ?,\n"
                + "sexo = ?,\n"
                + "fecha_nac = ?,\n"
                + "id_grupo = ?\n"
                + "WHERE id_alumno = ?";
        
        final String queryInsertarMedicion = "INSERT INTO datos(id_alumno,\n"
                + "id_grupo,\n"
                + "fecha,\n"
                + "masa,\n"                
                + "diagnostico_peso,\n"
                + "z_peso,\n"
                + "estatura,\n"
                + "diagnostico_talla,\n"
                + "z_talla,\n"
                + "diagnostico_imc,\n"
                + "z_imc,\n"
                + "perimetro_cuello,\n"
                + "cintura, triceps,\n"
                + "subescapula,\n"
                + "pliegue_cuello)\n"
                + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        
        final String queryActualizarMedicion = "UPDATE datos SET\n"
                + "id_grupo = ?,\n"
                + "masa = ?,\n"                
                + "diagnostico_peso = ?,\n"
                + "z_peso = ?,\n"
                + "estatura = ?,\n"
                + "diagnostico_talla = ?,\n"
                + "z_talla = ?,\n"
                + "diagnostico_imc = ?,\n"
                + "z_imc = ?,\n"
                + "perimetro_cuello = ?,\n"
                + "cintura = ?, triceps = ?,\n"
                + "subescapula = ?,\n"
                + "pliegue_cuello = ?\n"
                + "WHERE id_alumno = ? AND fecha = ?";
        
        try {
            datasource = (DataSource) new InitialContext().lookup(DB_JNDI);
        } catch (NamingException excepcion) {
            System.out.println("Falla en base de datos:" + excepcion.getMessage());
            return Response.serverError().header("Access-Control-Allow-Origin", "*").build();
        }
        
        try (Connection dbConnection = datasource.getConnection()) {
            dbConnection.setAutoCommit(false);
            
            try {
                // Agregar alumnos que hayan sido creados en la aplicacion móvil.
                for (int indiceAlumno = 0; indiceAlumno < alumnosInsertar.size(); indiceAlumno++) {
                    JsonObject alumno = alumnosInsertar.getJsonObject(indiceAlumno);
                     try (PreparedStatement statement = dbConnection.prepareStatement(queryInsertarAlumno)) {
                         int numColumna = 1;
                         int resultado;
                         for(String columna : alumno.keySet()) {
                             statement.setString(numColumna++, alumno.getString(columna));
                         }
                         
                         resultado = statement.executeUpdate();
                         if(resultado != 1){
                             System.out.println("Falla en sincronizacion insertar alumno: " + alumno.toString());
                             throw new SQLException("Base de datos corrupta.");
                         } 
                     }                                    
                }

                // Actualizar alumnos que hayan sido modificados en la aplicacion móvil.
                for (int indiceAlumno = 0; indiceAlumno < alumnosActualizar.size(); indiceAlumno++) {
                    JsonObject alumno = alumnosActualizar.getJsonObject(indiceAlumno);
                     try (PreparedStatement statement = dbConnection.prepareStatement(queryActualizarAlumno)) {
                         int numColumna = 1;
                         int resultado;
                         
                         for(String columna : alumno.keySet()) {
                             statement.setString(numColumna++, alumno.getString(columna));
                         }
                         
                         resultado = statement.executeUpdate();
                         if(resultado != 1){
                             System.out.println("Falla en sincronizacion actualizar alumno: " + alumno.toString());
                             throw new SQLException("Base de datos corrupta.");
                         } 
                     }    
                }

                //Insertar datos que hayan sido modificados en la aplicacion movil.
                for (int indiceDato = 0; indiceDato < datosInsertar.size(); indiceDato++) {
                    JsonObject medicion = datosInsertar.getJsonObject(indiceDato);
                     try (PreparedStatement statement = dbConnection.prepareStatement(queryInsertarMedicion)) {
                         int numColumna = 1;
                         int resultado;
                         for(String columna : medicion.keySet()) {
                             statement.setString(numColumna++, medicion.getString(columna));
                         }
                         
                         resultado = statement.executeUpdate();
                         if(resultado != 1){
                             System.out.println("Falla en sincronizacion insertar medicion: " + medicion.toString());
                             throw new SQLException("Base de datos corrupta.");
                         } 
                     }    
                }

                // Actualizacion de datos que hayan sido modificados en la aplicacion movil.
                for (int indiceDato = 0; indiceDato < datosActualizar.size(); indiceDato++) {
                    JsonObject medicion = datosActualizar.getJsonObject(indiceDato);
                     try (PreparedStatement statement = dbConnection.prepareStatement(queryActualizarMedicion)) {
                         int numColumna = 1;
                         int resultado;
                         for(String columna : medicion.keySet()) {
                             statement.setString(numColumna++, medicion.getString(columna));
                         }
                         
                         resultado = statement.executeUpdate();
                         if(resultado != 1){
                             System.out.println("Falla en sincronizacion actualizar medicion: " + medicion.toString());
                             throw new SQLException("Base de datos corrupta.");
                         }  
                     }   
                }
                
                dbConnection.commit();
            } catch (SQLException excepcion) {
                dbConnection.rollback();
                throw excepcion;
            }
        } catch(SQLException excepcion) {
            String mensajeExcepcion = excepcion.getMessage();
            if(mensajeExcepcion.equals("Base de datos corrupta.")) {
                return Response.notAcceptable(null).header("Access-Control-Allow-Origin", "*").build();
            } else {                
                System.out.println("Falla en base de datos:" + mensajeExcepcion);
                return Response.serverError().header("Access-Control-Allow-Origin", "*").build();
            }
        } 
        return Response.ok().header("Access-Control-Allow-Origin", "*").build();
    }
    
    public static void main(String... args) {
    }

    /**
     * 
     * @param cuerpoPeticion
     * @return
     * @throws IOException 
     */
    @POST
    @Produces("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    @Path("/alumnos/generarExcel")
    public Response generarExcel(String cuerpoPeticion) throws IOException {
        JsonObject peticionJSON = Json.createReader(new StringReader(cuerpoPeticion)).readObject(); 
        JsonObjectBuilder constructorJSON = Json.createObjectBuilder();
        String id_alumno = peticionJSON.getString("id_alumno");
        int ancho = peticionJSON.getInt("ancho");
        int alto = peticionJSON.getInt("alto");
        String[] graficos = {SISVANUtils.prepararSVG(peticionJSON.getString("grafico_imc"), ancho, alto),
            SISVANUtils.prepararSVG(peticionJSON.getString("grafico_talla"), ancho, alto),
            SISVANUtils.prepararSVG(peticionJSON.getString("grafico_peso"), ancho, alto)};
        
        byte[][] resultados = new byte[3][];
        int contadorGrafico = 0;
        for (String grafico : graficos) {
            InputStream svgStream = new ByteArrayInputStream(grafico.getBytes(StandardCharsets.UTF_8));
            TranscoderInput imagenSVG = new TranscoderInput(svgStream);
            try (ByteArrayOutputStream streamPNG = new ByteArrayOutputStream()) {
                TranscoderOutput imagenPNG = new TranscoderOutput(streamPNG);
                PNGTranscoder convertidor = new PNGTranscoder();
                convertidor.transcode(imagenSVG, imagenPNG);
                streamPNG.flush();
                resultados[contadorGrafico++] = streamPNG.toByteArray();
            } catch (Exception exception) {
                constructorJSON.add("error", "Error al procesar los graficos.");
                constructorJSON.add("mensaje", exception.getMessage());
                return Response.ok(constructorJSON.build().toString()).header("Access-Control-Allow-Origin", "*").build();
            }
        }
                
        JsonObject datosAlumno = Json.createReader(new StringReader(obtenerDatos(id_alumno)
                .getEntity().toString())).readObject();
        JsonObject medicionesAlumno = Json.createReader(new StringReader(obtenerMediciones(id_alumno)
                .getEntity().toString())).readObject();
        constructorJSON.add("datos", datosAlumno.get("datos"));
        constructorJSON.add("mediciones", medicionesAlumno.get("mediciones"));
        ResponseBuilder response = Response.ok(SISVANUtils.generarExcelConJSON(constructorJSON.build(), resultados));
        response.header("Content-Disposition", "attachment; Reporte.xlsx");
        response.header("Access-Control-Allow-Origin", "*");
        return response.build();
    }
    
    /**
     * 
     * @param desde
     * @param hasta
     * @param id_escuela
     * @return
     */
    @GET
    @Produces("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    @Path("/escolares/generarExcelGrupal")
    public Response generarExcelGrupal(@QueryParam("desde") String desde,
                                       @QueryParam("hasta") String hasta,
                                       @QueryParam("id_escuela") String id_escuela) {
        
        try {
            ResponseBuilder response = Response.ok(SISVANUtils.generarExcelGrupal(desde, hasta, id_escuela));
            response.header("Content-Disposition", "attachment; Reporte escolar.xlsx");
            response.header("Access-Control-Allow-Origin", "*");
            return response.build();
        } catch(NamingException | SQLException | IOException excepcion) {
            return Response.serverError().build();
        }        
    }

    /**
     * Mostrar todos los alumnos con el nombre o apellido especificado
     *
     * @param nombre la palabra a buscar en el nombre o apellido
     * @return json con toda la informacion de la base de datos
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/alumnos/buscarPorNombre/{nombre}")
    public Response buscarPorNombre(@PathParam("nombre") String nombre) {
        String query = "SELECT id_alumno, concat(alumnos.nombre, ' ', apellido_p, ' ', apellido_m) as nombre_completo, escuelas.nombre as nombre_escuela,\n"
                + "CONCAT(IF (calcular_grado(grupos.anio_ingreso, CURDATE()) > 6, "  + "\n"
                + "'EGRESADO', " + "\n"
                + "calcular_grado(grupos.anio_ingreso, CURDATE())), ' ', grupos.letra) as grupo " + "\n"
                + "FROM alumnos \n"
                + "INNER JOIN grupos ON alumnos.id_grupo = grupos.id_grupo \n"
                + "INNER JOIN escuelas ON grupos.id_escuela = escuelas.id_escuela\n"
                + "WHERE alumnos.nombre like '%" + nombre + "%' OR apellido_p like '%" + nombre + "%' OR apellido_m like '%" + nombre + "%';";

        return Response.ok(SISVANUtils.generarJSONMultiTipoDatos(query, nombre, "alumnos", false).toString()).header("Access-Control-Allow-Origin", "*").build();
    }

    /**
     * Regresar el nombre del usuario con el correo registrado
     *
     * @param cuerpoPeticion correo y contrasenia electronico del usuario
     * @return a json con la validacion del usuario
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/autenticacion/iniciarSesion")
    public Response iniciarSesion(String cuerpoPeticion) {
        JsonObjectBuilder jsonObjectBuilder
                = Json.createObjectBuilder();
        JsonObject response;
        DataSource datasource;
        String query = "SELECT nombre FROM usuarios WHERE correo = ? AND contrasenia = ?";
        String correo, contrasenia;

        if (!cuerpoPeticion.equals("")) {
            try (JsonReader bodyReader = Json.createReader(new StringReader(cuerpoPeticion))) {
                JsonObject datosEntrada = bodyReader.readObject();

                if (!datosEntrada.containsKey("usuario")
                        || !datosEntrada.containsKey("contrasenia")) {
                    throw new Exception("Datos incompletos.");
                }

                correo = datosEntrada.getString("usuario");
                contrasenia = datosEntrada.getString("contrasenia");

            } catch (Exception ex) {
                jsonObjectBuilder.add("error", "Favor de proporcionar todos los datos.");
                jsonObjectBuilder.add("mensaje", ex.getMessage());
                response = jsonObjectBuilder.build();
                return Response.ok(response.toString()).header("Access-Control-Allow-Origin", "*").build();
            }
            //Procesando la conversion de la contraseña
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-512");
                byte[] messageDigest = digest.digest(contrasenia.getBytes());
                BigInteger bInteger = new BigInteger(1, messageDigest);
                String textoDisp = bInteger.toString(16);

                while (textoDisp.length() < 32) {
                    textoDisp = "0" + textoDisp;
                }

                contrasenia = textoDisp;
            } catch (NoSuchAlgorithmException ex) {
                jsonObjectBuilder.add("error", "Error al intentar convertir la contraseña.");
                jsonObjectBuilder.add("mensaje", ex.getMessage());
                response = jsonObjectBuilder.build();
                return Response.ok(response.toString()).header("Access-Control-Allow-Origin", "*").build();
            }

            //Encontrar la clase para poder realizar la conexión con RDS
            try {
                datasource = (DataSource) new InitialContext().lookup(SISVANUtils.DB_JNDI);
            } catch (NamingException ex) {
                jsonObjectBuilder.add("error", "Error al intentar obtener la conexión con la base de datos.");
                jsonObjectBuilder.add("mensaje", ex.getMessage());
                response = jsonObjectBuilder.build();
                return Response.ok(response.toString()).header("Access-Control-Allow-Origin", "*").build();
            }

            //Obteniendo la información de la base de datos
            try (Connection dbConnection = datasource.getConnection();
                    PreparedStatement statement = dbConnection.prepareStatement(query)) {
                statement.setString(1, correo);
                statement.setString(2, contrasenia);

                try (ResultSet result = statement.executeQuery()) {

                    if (result.next()) {
                        jsonObjectBuilder.add("nombre", result.getString(1));
                    } else {
                        jsonObjectBuilder.add("error", "Error de autenticación.");
                    }

                    response = jsonObjectBuilder.build();
                } catch (SQLException ex) {
                    throw ex;
                }
            } catch (SQLException ex) {
                jsonObjectBuilder.add("error", "Error al obtener la información de la base de datos.");
                jsonObjectBuilder.add("mensaje", ex.getMessage());
                response = jsonObjectBuilder.build();
                return Response.ok(response.toString()).build();
            }
        } else {
            jsonObjectBuilder.add("error", "Favor de proporcionar todos los datos.");
            response = jsonObjectBuilder.build();
        }

        return Response.ok(response.toString()).header("Access-Control-Allow-Origin", "*").build();
    }

    /**
     * Obtener todos los datos con el id de alumno dado
     *
     * @param idAlumno identificador unico del alumno
     * @return json con toda la informacion de la base de datos
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/alumnos/obtenerDatos/{idAlumno}")
    public Response obtenerDatos(@PathParam("idAlumno") String idAlumno) {
        String query = "SELECT alumnos.nombre," + "\n"
                + "apellido_p," + "\n"
                + "apellido_m," + "\n"
                + "UPPER(sexo) as sexo," + "\n"
                + "fecha_nac," + "\n"
                + "escuelas.nombre as escuela," + "\n"
                + "escuelas.id_escuela," + "\n"
                + "grupos.id_grupo," + "\n"
                + "IF (calcular_grado(grupos.anio_ingreso, CURDATE()) > 6, "  + "\n"
                + "'EGRESADO', " + "\n"
                + "calcular_grado(grupos.anio_ingreso, CURDATE())) as grado," + "\n"
                + "letra" + "\n"
                + "FROM alumnos, grupos, escuelas" + "\n"
                + "WHERE id_alumno = ?" + "\n"
                + "AND grupos.id_grupo = alumnos.id_grupo" + "\n"
                + "AND grupos.id_escuela = escuelas.id_escuela";

        return Response.ok(SISVANUtils.generarJSONMultiTipoDatos(query, idAlumno, "datos", true).toString()).header("Access-Control-Allow-Origin", "*").build();
    }

    /**
     * Obtener toda la lista de escuelas
     *
     * @return json con toda la informacion de la base de datos
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/obtenerEscuelas")
    public Response obtenerEscuelas() {
        String query = "SELECT id_escuela as value, nombre as label FROM escuelas";

        return Response.ok(SISVANUtils.generarJSONMultiTipoDatos(query, "", "escuelas", false).toString()).header("Access-Control-Allow-Origin", "*").build();
    }
    
    /**
     * Obtener toda la lista de escuelas
     *
     * @return json con toda la informacion de la base de datos
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/obtenerDatosEscuelas")
    public Response obtenerDatosEscuelas() {
        String query = "SELECT * FROM escuelas";

        return Response.ok(SISVANUtils.generarJSONMultiTipoDatos(query, "", "escuelas", false).toString()).header("Access-Control-Allow-Origin", "*").build();
    }
    
    /**
     * Obtener toda la lista de grupos con sus datos
     * 
     * @param idGrupo identificador unico del grupo
     * @return json con toda la informacion de la base de datos
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/obtenerDatosGrupos/{idGrupo}")
    public Response obtenerDatosGrupos(@PathParam("idGrupo") String idGrupo) {
        String query = "SELECT id_grupo, \n" +
                       "       letra, \n" +
                       "       anio_ingreso, \n" +
                       "       anio_graduacion, \n" +
                       "       IF (calcular_grado(grupos.anio_ingreso, CURDATE()) > 6, \n" +
                       "        'EGRESADO', \n" +
                       "	calcular_grado(grupos.anio_ingreso, CURDATE())) as grado\n" +
                       "FROM grupos \n" +
                       "WHERE id_escuela = ? GROUP BY concat(anio_ingreso, letra)";

        return Response.ok(SISVANUtils.generarJSONMultiTipoDatos(query, idGrupo, "grupos", true).toString()).header("Access-Control-Allow-Origin", "*").build();
    }

    /**
     * Obtener toda la lista de grupos por escuela
     *
     * @param idEscuela identificador unico de la escuela
     * @return json con toda la informacion de la base de datos
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/escuelas/obtenerGrupos/{idEscuela}")
    public Response obtenerGrupos(@PathParam("idEscuela") String idEscuela) {
        String query = "SELECT id_grupo as value, concat(calcular_grado(anio_ingreso, CURDATE()),' ', letra) as label FROM grupos WHERE id_escuela = ? AND calcular_grado(anio_ingreso, CURDATE()) <= 6";

        return Response.ok(SISVANUtils.generarJSONMultiTipoDatos(query, idEscuela, "grupos", true).toString()).header("Access-Control-Allow-Origin", "*").build();
    }
    
    /**
     * Obtener las fechas para inicializar rangos
     *
     * @return json con las fechas para inicializar rangos.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/obtenerRangos")
    public Response obtenerRangos() {
        String query = "SELECT DATE_FORMAT(MAX(fecha), \"%Y-%m-%d\") as desde, obtener_rangos(MIN(fecha), false) as hasta \n" +
                       "FROM datos \n" +
                       "WHERE fecha between obtener_rangos((SELECT MAX(fecha) from datos), true) AND obtener_rangos((SELECT MAX(fecha) from datos), false)";
        return Response.ok(SISVANUtils.generarJSONMultiTipoDatos(query, "", "rangos", false).toString()).header("Access-Control-Allow-Origin", "*").build();
    }
    
    /**
     * Obtener toda la lista de grupos
     *
     * @return json con todos los grupos del sistema.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/grupos/obtenerTodosLosGrupos/{fecha}")
    public Response obtenerTodosLosGrupos(@PathParam("fecha") String fecha) { 
        JsonObjectBuilder jsonObjectBuilder
                = Json.createObjectBuilder();
        JsonObject response;
        DataSource datasource;
        String query = "SELECT id_escuela, id_grupo as value, concat(concat(calcular_grado(anio_ingreso, ?), ' '), letra) as label " + "\n"
                       + "FROM grupos " + "\n"
                       + "WHERE calcular_grado(anio_ingreso, ?) >= 1 AND calcular_grado(anio_ingreso, ?) <= 6 ORDER BY id_escuela";
        
        //Encontrar la clase para poder realizar la conexión con RDS
        try {
            datasource = (DataSource) new InitialContext().lookup(SISVANUtils.DB_JNDI);
        } catch (NamingException ex) {
            jsonObjectBuilder.add("error", "Error al intentar obtener la conexión con la base de datos.");
            jsonObjectBuilder.add("mensaje", ex.getMessage());
            response = jsonObjectBuilder.build();
            return Response.ok(response.toString()).header("Access-Control-Allow-Origin", "*").build();
        }
        
        if(fecha.equals("hoy")) {
            query = query.replaceAll("\\?", "CURDATE()");
        }
        
        //Obteniendo la información de la base de datos
        try (Connection dbConnection = datasource.getConnection();
                PreparedStatement statement = dbConnection.prepareStatement(query)) {
            
            if(!fecha.equals("hoy")) {
                statement.setString(1, fecha);
                statement.setString(2, fecha);
                statement.setString(3, fecha);
            }
            
            try (ResultSet resultados = statement.executeQuery()) {
                String idEscuela = null;
                JsonArrayBuilder gruposPorEscuela
                        = Json.createArrayBuilder();
                while (resultados.next()) {
                    if (idEscuela == null || !idEscuela.equals(String.valueOf(resultados.getInt(1)))) {
                        if (idEscuela != null) {
                            jsonObjectBuilder.add(idEscuela, gruposPorEscuela);
                            gruposPorEscuela = Json.createArrayBuilder();
                        }
                        idEscuela = String.valueOf(resultados.getInt(1));
                    }

                    gruposPorEscuela.add(
                            Json.createObjectBuilder()
                                    .add("value", resultados.getInt(2))
                                    .add("label", resultados.getString(3)));
                }
                if (idEscuela != null) {
                    jsonObjectBuilder.add(idEscuela, gruposPorEscuela);
                }
                response = jsonObjectBuilder.build();
            }
        } catch (SQLException exception) {
            jsonObjectBuilder.add("error", "Error al intentar obtener informacion de la base de datos.");
            response = jsonObjectBuilder.build();
        }

        return Response.ok(response.toString()).header("Access-Control-Allow-Origin", "*").build();               
    }    
    
    /**
     * Obtener el historico de la imc del alumno
     *
     * @param tipo este parametro es para especificar
     * el tipo de historico.
     * @param idAlumno identificador unico del alumno.
     * @return json con toda la informacion de la base de datos
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/alumnos/obtenerHistorico/{tipo}/{idAlumno}")
    public Response obtenerHistorico(@PathParam("tipo") String tipo, @PathParam("idAlumno") String idAlumno) {
        String tabla;
        String columna;
        switch(tipo) {
            case "talla":
                tabla = "percentiles_oms_talla";
                columna = "estatura as talla";
                break;
            case "imc":
                tabla = "percentiles_oms_imc";
                columna = "imc";
                break;
            default:
                tabla = "percentiles_oms_peso";
                columna = "masa as peso";
                break;
        }
        
        String query = "SELECT CONVERT(meses, UNSIGNED INTEGER) meses_num, \n" 
                    + "       sd3, \n" 
                    + "       sd2, \n" 
                    + "       sd1, \n" 
                    + "       sd0, \n" 
                    + "       sd1_neg, \n" 
                    + "       sd2_neg, \n" 
                    + "       sd3_neg, \n" 
                    + "       " + (columna.contains("as") ? columna.split("as ")[1] : columna) + " \n" 
                    + "FROM   (SELECT REPLACE(id_percentil, sexo, '') AS meses, \n" 
                    + "               sd3, \n" 
                    + "               sd2, \n" 
                    + "               sd1, \n" 
                    + "               sd0, \n" 
                    + "               sd1_neg, \n" 
                    + "               sd2_neg, \n" 
                    + "               sd3_neg, \n" 
                    + "               " + columna + " \n" 
                    + "        FROM   datos \n" 
                    + "               INNER JOIN alumnos \n" 
                    + "                       ON alumnos.id_alumno = datos.id_alumno \n" 
                    + "               INNER JOIN " + tabla + " \n" 
                    + "                       ON id_percentil = Concat(alumnos.sexo, \n" 
                    + "                                         Timestampdiff(month, \n" 
                    + "                                         alumnos.fecha_nac, \n" 
                    + "                                         datos.fecha)) \n" 
                    + "        WHERE  datos.id_alumno = ? \n" 
                    + "        UNION \n" 
                    + "        SELECT REPLACE(id_percentil, sexo, '') AS meses, \n" 
                    + "               sd3, \n" 
                    + "               sd2, \n" 
                    + "               sd1, \n" 
                    + "               sd0, \n" 
                    + "               sd1_neg, \n" 
                    + "               sd2_neg, \n" 
                    + "               sd3_neg, \n" 
                    + "               NULL AS " + (columna.contains("as") ? columna.split("as ")[1] : columna) + " \n" 
                    + "        FROM   " + tabla + " \n" 
                    + "               INNER JOIN alumnos \n" 
                    + "                       ON id_percentil LIKE Concat(sexo, '%') \n" 
                    + "        WHERE  id_alumno = ?) AS estadisticas \n" 
                    + "GROUP  BY meses \n" 
                    + "ORDER  BY meses_num ASC";

        return Response.ok(SISVANUtils.generarJSONGraficoLinea(query, idAlumno, "mes", false).toString()).header("Access-Control-Allow-Origin", "*").build();
    }

    /**
     * Obtener todas las mediciones del alumno dado
     *
     * @param idAlumno identificador unico del alumno
     * @return json con toda la informacion de la base de datos
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/alumnos/obtenerMediciones/{idAlumno}")
    public Response obtenerMediciones(@PathParam("idAlumno") String idAlumno) {
        String query = "SELECT timestampdiff(MONTH, alumnos.fecha_nac, datos.fecha) as meses, " + "\n"
                + "CONCAT(CONCAT(calcular_grado(grupos.anio_ingreso, datos.fecha), ' '), grupos.letra) as grupo, datos.* " + "\n"
                + "FROM datos JOIN alumnos ON datos.id_alumno = alumnos.id_alumno" + "\n"
                + "JOIN grupos ON datos.id_grupo = grupos.id_grupo" + "\n"
                + "WHERE alumnos.id_alumno = ?";

        return Response.ok(SISVANUtils.generarJSONMultiTipoDatos(query, idAlumno, "mediciones", true).toString()).header("Access-Control-Allow-Origin", "*").build();
    }

    /**
     * Obtener los porcentajes de alumnos de cada escuela
     *
     * @param id_escuela identificador unico de la escuela
     * @param desde la fecha inicial de la cual se diagnosticara
     * @param hasta la fecha final de la cual se diagnosticara
     * @param diagnostico el tipo de diagnostico que se reaizara
     * @return json con toda la informacion de la base de datos
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/escolares/obtenerPorcentajesEscuela")
    public Response obtenerPorcentajesEscuela(@QueryParam("id_escuela") String id_escuela,
            @QueryParam("desde") String desde,
            @QueryParam("hasta") String hasta,
            @QueryParam("diagnostico") String diagnostico) {
        String columnaDiagnostico = diagnostico.equals("talla") ? "diagnostico_talla" : diagnostico.equals("peso") ? "diagnostico_peso" : "diagnostico_imc";
        
        String query = "SELECT concat('Escuela ', nombre_escuela) as escuela, diagnostico, COUNT(*) as value\n"
                    + "FROM (SELECT escuelas.nombre as nombre_escuela, alumnos.id_grupo, alumnos.id_alumno, ROUND(masa) as masa, timestampdiff(MONTH, alumnos.fecha_nac, d.fecha) as meses, sexo, d." + columnaDiagnostico + " as diagnostico \n"
                    + "FROM datos d \n"
                    + "INNER JOIN alumnos ON alumnos.id_alumno = d.id_alumno\n"
                    + "INNER JOIN grupos ON grupos.id_grupo = alumnos.id_grupo\n"
                    + "INNER JOIN escuelas ON escuelas.id_escuela = grupos.id_escuela\n"
                    + "WHERE d.fecha between ?  and ?\n"
                    + "AND d.id_grupo IN (SELECT id_grupo FROM grupos WHERE id_escuela = ?)) subdatos\n"
                    + "GROUP BY diagnostico";

        return Response.ok(SISVANUtils.generarJSONGraficoPastel(query, id_escuela, desde, hasta, "escuela").toString()).header("Access-Control-Allow-Origin", "*").build();
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("escolares/obtenerHistoricoEscuela/{medicion}/{idEscuela}")
    public Response obtenerPorcentajesEscuela(@PathParam("medicion") String medicion, @PathParam("idEscuela") String idEscuela) {
        String prefijoQuery;
        switch(medicion) {
            case "talla":
                prefijoQuery = "SELECT fecha, SUM(if(diagnostico_talla = \"CON TALLA BAJA\", 1, 0)) AS con_talla_baja,  SUM(if(diagnostico_talla = \"SIN TALLA BAJA\", 1, 0)) AS sin_talla_baja\n";
                break;
            case "peso":
                prefijoQuery = "SELECT fecha, SUM(if(diagnostico_peso = \"CON PESO BAJO\", 1, 0)) AS con_peso_bajo,  SUM(if(diagnostico_peso = \"SIN PESO BAJO\", 1, 0)) AS sin_peso_bajo\n";
                break;
            default:
                prefijoQuery = "SELECT fecha, SUM(if(diagnostico_imc = \"BAJO PESO\", 1, 0)) AS bajo_peso,   SUM(if(diagnostico_imc = \"SIN EXCESO DE PESO\", 1, 0)) AS sin_exceso_de_peso, SUM(if(diagnostico_imc = \"OBESIDAD\", 1, 0)) AS obesidad, SUM(if(diagnostico_imc = \"SOBREPESO\", 1, 0)) AS sobrepeso\n";
                break;
        }
        
        String query = prefijoQuery
                + "FROM datos INNER JOIN grupos ON datos.id_grupo = grupos.id_grupo\n"
                + "WHERE id_escuela = ?\n"
                + "GROUP BY fecha ORDER BY fecha";
        
        return Response.ok(SISVANUtils.generarJSONGraficoLinea(query, idEscuela, "fecha", false).toString()).header("Access-Control-Allow-Origin", "*").build();
    }

    /**
     * Obtener los porcentajes de alumnos de cada grupo
     *
     * @param id_grupo identificador unico del grupo
     * @param desde la fecha inicial de la cual se diagnosticara
     * @param hasta la fecha final de la cual se diagnosticara
     * @param diagnostico el tipo de diagnostico que se reaizara
     * @return json con toda la informacion de la base de datos
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/escolares/obtenerPorcentajesGrupo")
    public Response obtenerPorcentajesGrupo(@QueryParam("id_grupo") String id_grupo,
            @QueryParam("desde") String desde,
            @QueryParam("hasta") String hasta,
            @QueryParam("diagnostico") String diagnostico) {
        String columnaDiagnostico = diagnostico.equals("talla") ? "diagnostico_talla" : diagnostico.equals("peso") ? "diagnostico_peso" : "diagnostico_imc";
        
        String query = "SELECT concat('Grupo ', id_grupo) as grupo, diagnostico, COUNT(*) as value \n"
                + "FROM (SELECT alumnos.id_grupo, alumnos.id_alumno, ROUND(masa) as masa, timestampdiff(MONTH, alumnos.fecha_nac, d.fecha) as meses, sexo, d." + columnaDiagnostico + " as diagnostico FROM datos d INNER JOIN alumnos\n"
                + "ON alumnos.id_alumno = d.id_alumno\n"
                + "WHERE d.fecha between ? and ? AND d.id_grupo = ?) subdatos\n"
                + "GROUP BY diagnostico;";

        return Response.ok(SISVANUtils.generarJSONGraficoPastel(query, id_grupo, desde, hasta, "grupo").toString()).header("Access-Control-Allow-Origin", "*").build();
    }

    /**
     * Obtener los puntajes Z de la masa proporcionados por la OMS
     *
     * @param sexo el sexo requerido para obtener los puntajes
     * @param tipo el tipo de indice z para obtener puntajes
     * @return json para generar la grafica de linea
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/estadisticas/obtenerPuntajesZ/{tipo}/{sexo}")
    public Response obtenerPuntajesZ(@PathParam("tipo") String tipo, @PathParam("sexo") String sexo) {
        String tipoIndice;
        
        switch(tipo) {
            case "talla":
                tipoIndice = "percentiles_oms_talla";
                break;
            case "imc":
                tipoIndice = "percentiles_oms_imc";
                break;
            default:
                tipoIndice = "percentiles_oms_peso";
                break;
        }
        
        String query = "SELECT cast(replace(id_percentil, ?, '') as unsigned) as mes, \n"
                + "sd3, \n"
                + "sd2, \n"
                + "sd1, \n"
                + "sd0, \n"
                + "sd1_neg, \n"
                + "sd2_neg, \n"
                + "sd3_neg FROM \n"
                + tipoIndice + " WHERE id_percentil LIKE('%" + sexo + "%') \n"
                + "ORDER BY mes";

        return Response.ok(SISVANUtils.generarJSONGraficoLinea(query, sexo, "mes", false).toString()).header("Access-Control-Allow-Origin", "*").build();
    }
    
    /**
     * Obtener todos los alumnos de una escuela dada
     *
     * @param idEscuela identificador unico de la escuela
     * @return json con toda la lista de alumnos por escuela
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/escuelas/obtenerAlumnos/{idEscuela}")
    public Response obtenerAlumnos(@PathParam("idEscuela") String idEscuela) {
        String query = "SELECT id_alumno FROM alumnos \n" +
                       "INNER JOIN grupos ON alumnos.id_grupo = grupos.id_grupo\n" +
                       "WHERE id_escuela = ? AND (calcular_grado(grupos.anio_ingreso, CURDATE()) <= 7);";
        JsonObjectBuilder constructorRespuesta = Json.createObjectBuilder();
        JsonArrayBuilder constructorLista = Json.createArrayBuilder();

        JsonObject jsonAlumnos = SISVANUtils.generarJSONMultiTipoDatos(query, idEscuela, "alumnos", true);
        
        for(JsonValue alumno : jsonAlumnos.getJsonArray("alumnos")) {
            JsonObject alumnoActual = (JsonObject) alumno;
            JsonObjectBuilder alumnoRespuesta = Json.createObjectBuilder();
            String idAlumno = String.valueOf(alumnoActual.getInt("id_alumno"));
            JsonObject datosAlumno = Json.createReader(new StringReader(obtenerDatos(idAlumno)
                .getEntity().toString())).readObject();
            
            JsonObject medicionesAlumno = Json.createReader(new StringReader(obtenerMediciones(idAlumno)
                .getEntity().toString())).readObject();
            
            alumnoRespuesta.add("id_alumno", idAlumno);
            alumnoRespuesta.add("datos", datosAlumno.getJsonArray("datos").getJsonObject(0));
            
            if(medicionesAlumno.containsKey("error")) {
                JsonArrayBuilder medicionesVacia = Json.createArrayBuilder();
                alumnoRespuesta.add("mediciones", medicionesVacia);
            } else {
                alumnoRespuesta.add("mediciones", medicionesAlumno.getJsonArray("mediciones"));
            }
            
            
            constructorLista.add(alumnoRespuesta);            
        } 
        
        constructorRespuesta.add("alumnos", constructorLista);
                
        return Response.ok(constructorRespuesta.build().toString()).header("Access-Control-Allow-Origin", "*").build();
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/obtenerAnfitrion")
    public Response obtenerAnfitrion(@Context HttpHeaders httpheaders) {
        String hostRequest = httpheaders.getHeaderString("User-Agent") == null ? "unknown"
                : httpheaders.getHeaderString("User-Agent");
        ResponseBuilder responseBuilder = Response.ok("Host: " + hostRequest);
        responseBuilder.header("Access-Control-Allow-Origin", "*");
        return responseBuilder.build();
    }

    @POST
    @Produces("image/png")
    @Path("/generarImagen")
    public Response generarImagen(String contenido) {
        JsonObject peticionJSON = Json.createReader(new StringReader(contenido)).readObject(); 
        int ancho = peticionJSON.getInt("ancho");
        int alto = peticionJSON.getInt("alto");
        String svg = SISVANUtils.prepararSVG(peticionJSON.getString("svg"), ancho, alto);
        InputStream svgStream = new ByteArrayInputStream(svg.getBytes());
        TranscoderInput imagenSVG = new TranscoderInput(svgStream);
        try (ByteArrayOutputStream streamPNG = new ByteArrayOutputStream()) {
            TranscoderOutput imagenPNG = new TranscoderOutput(streamPNG);
            PNGTranscoder convertidor = new PNGTranscoder();
            convertidor.transcode(imagenSVG, imagenPNG);
            streamPNG.flush();            
            return Response.ok(streamPNG.toByteArray()).header("Access-Control-Allow-Origin", "*").build();
        } catch (Exception ex) {
            return Response.ok("No es posible generar la imagen: " + ex.getMessage()).build();
        }                
    }       
}
