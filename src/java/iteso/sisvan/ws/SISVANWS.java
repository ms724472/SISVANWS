/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iteso.sisvan.ws;

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
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;

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
                + "VALUES(?,?,?,?,?,?,8)";

        String[] nombresColumnas = {"id_alumno", "nombre", "apellido_p", "apellido_m", "sexo", "fecha_nac"};

        return Response.ok(SISVANUtils.insertarNuevoDatoEnBD(cuerpoPeticion, nombresColumnas, query).toString()).build();
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
                + "fecha," + "\n"
                + "masa," + "\n"
                + "estatura)" + "\n"
                + "VALUES(?,?,?,?)";

        String[] nombresColumnas = {"id_alumno", "fecha", "masa", "estatura"};

        return Response.ok(SISVANUtils.insertarNuevoDatoEnBD(cuerpoPeticion, nombresColumnas, query).toString()).build();
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
                return Response.ok(response.toString()).build();
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
                return Response.ok(response.toString()).build();
            }

            //Encontrar la clase para poder realizar la conexión con RDS
            try {
                datasource = (DataSource) new InitialContext().lookup(SISVANUtils.DB_JNDI);
            } catch (NamingException ex) {
                jsonObjectBuilder.add("error", "Error al intentar obtener la conexión con la base de datos.");
                jsonObjectBuilder.add("mensaje", ex.getMessage());
                response = jsonObjectBuilder.build();
                return Response.ok(response.toString()).build();
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

        return Response.ok(response.toString()).build();
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
                + "sexo," + "\n"
                + "fecha_nac," + "\n"
                + "escuelas.nombre as escuela," + "\n"
                + "grado," + "\n"
                + "letra" + "\n"
                + "FROM alumnos, grupos, escuelas" + "\n"
                + "WHERE id_alumno = ?" + "\n"
                + "AND grupos.id_grupo = alumnos.id_grupo" + "\n"
                + "AND grupos.id_escuela = escuelas.id_escuela";

        return Response.ok(SISVANUtils.generarJSONMultiTipoDatos(query, idAlumno, "datos").toString()).build();
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
                + "estatura*100 as estatura, "
                + "nivel_0 as ideal "
                + "FROM datos" + "\n"
                + "INNER JOIN alumnos ON alumnos.id_alumno = datos.id_alumno" + "\n"
                + "INNER JOIN oms_puntajes_z_estatura ON id_percentil = concat(alumnos.sexo,timestampdiff(MONTH, alumnos.fecha_nac, datos.fecha))" + "\n"
                + "WHERE datos.id_alumno = ?";

        return Response.ok(SISVANUtils.generarJSONGraficoLinea(query, idAlumno, "fecha", true).toString()).build();
    }

    /**
     * Obtener el historico de la estatura del alumno
     *
     * @param idAlumno identificador unico del alumno
     * @return json con toda la informacion de la base de datos
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/alumnos/obtenerHistoricoMasa/{idAlumno}")
    public Response obtenerHistoricoMasa(@PathParam("idAlumno") String idAlumno) {
        String query = "SELECT date_format(fecha, '%d/%m/%Y') as fecha, "
                + "masa, "
                + "nivel_0 as ideal "
                + "FROM datos" + "\n"
                + "INNER JOIN alumnos ON alumnos.id_alumno = datos.id_alumno" + "\n"
                + "INNER JOIN oms_puntajes_z_masa ON id_percentil = concat(alumnos.sexo,timestampdiff(MONTH, alumnos.fecha_nac, datos.fecha))" + "\n"
                + "WHERE datos.id_alumno = ?";

        return Response.ok(SISVANUtils.generarJSONGraficoLinea(query, idAlumno, "fecha", true).toString()).build();
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
        String query = "SELECT fecha,masa,estatura,imc" + "\n"
                + "FROM datos" + "\n"
                + "WHERE id_alumno = ?";

        return Response.ok(SISVANUtils.generarJSONMultiTipoDatos(query, idAlumno, "mediciones").toString()).build();
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/escolares/obtenerPorcentajesEscuela/{id_escuela}")
    public Response obtenerPorcentajesEscuela(@PathParam("id_escuela") String id_escuela) {
        String query = "SELECT concat('Escuela ', id_grupo) as grupo, diagnosticar_alumnos(concat(subdatos.sexo, meses), masa) as diagnostico, COUNT(*) as value \n"
                + "FROM (SELECT alumnos.id_grupo, alumnos.id_alumno, ROUND(masa) as masa, timestampdiff(MONTH, alumnos.fecha_nac, d.fecha) as meses, sexo FROM datos d INNER JOIN alumnos\n"
                + "ON alumnos.id_alumno = d.id_alumno\n"
                + "WHERE d.fecha = (\n"
                + "SELECT MAX(d2.fecha)\n"
                + "FROM datos d2\n"
                + "WHERE d.id_alumno = d2.id_alumno \n"
                + ") AND id_grupo IN (SELECT id_grupo FROM grupos WHERE id_escuela = ?)) subdatos\n"
                + "GROUP BY diagnostico;";

        return Response.ok(SISVANUtils.generarJSONGraficoPastel(query, id_escuela, "escuela").toString()).build();
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/escolares/obtenerPorcentajesGrupo/{id_grupo}")
    public Response obtenerPorcentajesGrupo(@PathParam("id_grupo") String id_grupo) {
        String query = "SELECT concat('Grupo ', id_grupo) as grupo, diagnosticar_alumnos(concat(subdatos.sexo, meses), masa) as diagnostico, COUNT(*) as value \n"
                + "FROM (SELECT alumnos.id_grupo, alumnos.id_alumno, ROUND(masa) as masa, timestampdiff(MONTH, alumnos.fecha_nac, d.fecha) as meses, sexo FROM datos d INNER JOIN alumnos\n"
                + "ON alumnos.id_alumno = d.id_alumno\n"
                + "WHERE d.fecha = (\n"
                + "SELECT MAX(d2.fecha)\n"
                + "FROM datos d2\n"
                + "WHERE d.id_alumno = d2.id_alumno \n"
                + ") AND id_grupo = ?) subdatos\n"
                + "GROUP BY diagnostico;";

        return Response.ok(SISVANUtils.generarJSONGraficoPastel(query, id_grupo, "grupo").toString()).build();
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

        return Response.ok(SISVANUtils.generarJSONGraficoLinea(query, sexo, "mes", false).toString()).build();
    }
}
