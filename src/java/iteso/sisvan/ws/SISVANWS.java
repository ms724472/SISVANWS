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
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

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

        String[] nombresColumnas = {"id_alumno", "id_grupo", "fecha", "masa", "estatura", "perimetro_cuello", "cintura", "triceps", "subescapula", "pliegue_cuello"};

        try (JsonReader bodyReader = Json.createReader(new StringReader(cuerpoPeticion))) {
            JsonObject datosEntrada = bodyReader.readObject();
            List<String> columnas = Arrays.asList(nombresColumnas);
            
            fecha = datosEntrada.getString("fecha");
            id_alumno = datosEntrada.getString("id_alumno"); 
            
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
            String actualizacionPuntajes = "SELECT actualizar_puntajes(?, ?)";
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
                statement.setString(2, fecha);
                
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
                + "fecha = ?," + "\n"
                + "masa = ?," + "\n"
                + "estatura = ?," + "\n"
                + "perimetro_cuello =?," + "\n"
                + "cintura = ?, triceps =?," + "\n"
                + "subescapula = ?," + "\n"
                + "pliegue_cuello = ?" + "\n"
                + "WHERE id_alumno = ?";
        String fecha;
        String id_alumno;

        String[] nombresColumnas = {"id_grupo", "fecha", "masa", "estatura", "perimetro_cuello", "cintura", "triceps", "subescapula", "pliegue_cuello", "id_alumno"};

        try (JsonReader bodyReader = Json.createReader(new StringReader(cuerpoPeticion))) {
            JsonObject datosEntrada = bodyReader.readObject();
            
            fecha = datosEntrada.getString("fecha");
            id_alumno = datosEntrada.getString("id_alumno");            
        }
        
        JsonObject respuestaInserccion = SISVANUtils.insertarNuevoDatoEnBD(cuerpoPeticion, nombresColumnas, query);
        
        if(respuestaInserccion.containsKey("status") && respuestaInserccion.getString("status").equals("exito")) {
            String actualizacionPuntajes = "SELECT actualizar_puntajes(?, ?)";
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
                statement.setString(2, fecha);
                
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
            InputStream svgStream = new ByteArrayInputStream(grafico.getBytes());
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
     * Mostrar todos los alumnos con el nombre o apellido especificado
     *
     * @param nombre la palabra a buscar en el nombre o apellido
     * @return json con toda la informacion de la base de datos
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/alumnos/buscarPorNombre/{nombre}")
    public Response buscarPorNombre(@PathParam("nombre") String nombre) {
        String query = "SELECT id_alumno, concat(alumnos.nombre, ' ', apellido_p, ' ', apellido_m) as nombre_completo, escuelas.nombre as nombre_escuela\n"
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
                + "CONCAT(UPPER(SUBSTRING(sexo, 1, 1)), SUBSTRING(sexo, 2)) as sexo," + "\n"
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
        String query = "SELECT obtener_rangos(MIN(fecha), true) as desde, obtener_rangos(MIN(fecha), false) as hasta \n" +
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
                       + "WHERE calcular_grado(anio_ingreso, ?) >= 1 AND calcular_grado(anio_ingreso, ?) <= 6";
        
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

        return Response.ok(response.toString()).build();               
    }    
    
    public static void main(String... args) {
        String test = "SELECT ? FROM ?";
        System.out.println(test.replaceAll("\\?", ""));
    }

    /**
     * Obtener el historico de la estatura del alumno
     *
     * @param idAlumno identificador unico del alumno
     * @return json con toda la informacion de la base de datos
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/alumnos/obtenerHistoricoEstatura/{idAlumno}")
    public Response obtenerHistoricoEstatura(@PathParam("idAlumno") String idAlumno) {
        String query = "SELECT date_format(fecha, '%d/%m/%Y') as fecha, "
                + "estatura as talla, "
                + "mediana as ideal "
                + "FROM datos" + "\n"
                + "INNER JOIN alumnos ON alumnos.id_alumno = datos.id_alumno" + "\n"
                + "INNER JOIN percentiles_oms_talla ON id_percentil = concat(alumnos.sexo,timestampdiff(MONTH, alumnos.fecha_nac, datos.fecha))" + "\n"
                + "WHERE datos.id_alumno = ?";

        return Response.ok(SISVANUtils.generarJSONGraficoLinea(query, idAlumno, "fecha", true).toString()).header("Access-Control-Allow-Origin", "*").build();
    }

    /**
     * Obtener el historico de la peso del alumno
     *
     * @param idAlumno identificador unico del alumno
     * @return json con toda la informacion de la base de datos
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/alumnos/obtenerHistoricoMasa/{idAlumno}")
    public Response obtenerHistoricoMasa(@PathParam("idAlumno") String idAlumno) {
        String query = "SELECT date_format(fecha, '%d/%m/%Y') as fecha, "
                + "masa as peso, "
                + "mediana as ideal "
                + "FROM datos" + "\n"
                + "INNER JOIN alumnos ON alumnos.id_alumno = datos.id_alumno" + "\n"
                + "INNER JOIN percentiles_oms_peso ON id_percentil = concat(alumnos.sexo,timestampdiff(MONTH, alumnos.fecha_nac, datos.fecha))" + "\n"
                + "WHERE datos.id_alumno = ?";

        return Response.ok(SISVANUtils.generarJSONGraficoLinea(query, idAlumno, "fecha", true).toString()).header("Access-Control-Allow-Origin", "*").build();
    }
    
    /**
     * Obtener el historico de la imc del alumno
     *
     * @param idAlumno identificador unico del alumno
     * @return json con toda la informacion de la base de datos
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/alumnos/obtenerHistoricoIMC/{idAlumno}")
    public Response obtenerHistoricoIMC(@PathParam("idAlumno") String idAlumno) {
        String query = "SELECT date_format(fecha, '%d/%m/%Y') as fecha, "
                + "imc, "
                + "mediana as ideal "
                + "FROM datos" + "\n"
                + "INNER JOIN alumnos ON alumnos.id_alumno = datos.id_alumno" + "\n"
                + "INNER JOIN percentiles_oms_imc ON id_percentil = concat(alumnos.sexo,timestampdiff(MONTH, alumnos.fecha_nac, datos.fecha))" + "\n"
                + "WHERE datos.id_alumno = ?";

        return Response.ok(SISVANUtils.generarJSONGraficoLinea(query, idAlumno, "fecha", true).toString()).header("Access-Control-Allow-Origin", "*").build();
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
        String query = "SELECT datos.*, timestampdiff(MONTH, alumnos.fecha_nac, datos.fecha) as meses, " + "\n"
                + "CONCAT(CONCAT(calcular_grado(grupos.anio_ingreso, datos.fecha), ' '), grupos.letra) as grupo " + "\n"
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
        
        String query = "SELECT concat('Escuela ', id_grupo) as grupo, diagnostico, COUNT(*) as value \n"
                + "FROM (SELECT alumnos.id_grupo, alumnos.id_alumno, ROUND(masa) as masa, timestampdiff(MONTH, alumnos.fecha_nac, d.fecha) as meses, sexo, d." + columnaDiagnostico + " as diagnostico FROM datos d INNER JOIN alumnos\n"
                + "ON alumnos.id_alumno = d.id_alumno\n"
                + "WHERE d.fecha between ? and ? \n"
                + "AND d.id_grupo IN (SELECT id_grupo FROM grupos WHERE id_escuela = ?)) subdatos\n"
                + "GROUP BY diagnostico;";

        return Response.ok(SISVANUtils.generarJSONGraficoPastel(query, id_escuela, desde, hasta, "escuela").toString()).header("Access-Control-Allow-Origin", "*").build();
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
     * @return json para generar la grafica de linea
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/estadisticas/obtenerPuntajesZMasa/{sexo}")
    public Response obtenerPuntajesZMasa(@PathParam("sexo") String sexo) {
        String query = "SELECT cast(replace(id_percentil, ?, '') as unsigned) as mes, \n"
                + "nivel_n3, \n"
                + "nivel_n2, \n"
                + "nivel_n1, \n"
                + "nivel_0, \n"
                + "nivel_p1, \n"
                + "nivel_p2, \n"
                + "nivel_p3 FROM \n"
                + "oms_puntajes_z_masa WHERE id_percentil LIKE('%" + sexo + "%') \n"
                + "ORDER BY mes";

        return Response.ok(SISVANUtils.generarJSONGraficoLinea(query, sexo, "mes", false).toString()).header("Access-Control-Allow-Origin", "*").build();
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
    @Produces("application/pdf")
    @Path("/generarPDF")
    public Response generarPDF(String contenido) {
        JsonObject peticionJSON = Json.createReader(new StringReader(contenido)).readObject();        
        String tipo = peticionJSON.getString("tipo");
        int ancho = peticionJSON.getInt("ancho");
        int alto = peticionJSON.getInt("alto");
        String svg = SISVANUtils.prepararSVG(peticionJSON.getString("svg"), ancho, alto);
        ByteArrayOutputStream streamPDF = new ByteArrayOutputStream();        
        InputStream svgStream = new ByteArrayInputStream(svg.getBytes());
        TranscoderInput imagenSVG = new TranscoderInput(svgStream);
        try (ByteArrayOutputStream streamPNG = new ByteArrayOutputStream()) {
            TranscoderOutput imagenPNG = new TranscoderOutput(streamPNG);
            PNGTranscoder convertidor = new PNGTranscoder();
            convertidor.transcode(imagenSVG, imagenPNG);
            streamPNG.flush();
            streamPNG.close();
            try (PDDocument documento = new PDDocument()) {
                PDPage pagina = new PDPage();
                documento.addPage(pagina);
                try (PDPageContentStream streamContenido = new PDPageContentStream(documento, pagina)) {
                    PDImageXObject grafica = PDImageXObject.createFromByteArray(documento, streamPNG.toByteArray(), "grafica");
                    streamContenido.setFont(PDType1Font.HELVETICA_BOLD, 18);
                    streamContenido.beginText();
                    streamContenido.newLineAtOffset(160, 670);
                    streamContenido.showText("Reporte de evaluación por " + tipo);
                    streamContenido.endText();
                    streamContenido.drawImage(grafica, 50, 250);
                    streamContenido.close();
                    documento.save(streamPDF);
                } catch (IOException ex) {
                    return Response.ok("No es posible generar el PDF: " + ex.getMessage()).build();
                }
            } catch (IOException ex) {
                return Response.ok("No es posible generar el PDF: " + ex.getMessage()).build();
            }
        } catch (Exception ex) {
            return Response.ok("No es posible generar la imagen: " + ex.getMessage()).build();
        }
        
        return Response.ok(streamPDF.toByteArray()).header("Access-Control-Allow-Origin", "*").build();
    }       
}
