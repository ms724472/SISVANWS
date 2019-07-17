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
import javax.ws.rs.core.Context;
import javax.ws.rs.Produces;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.*;
import java.text.SimpleDateFormat;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.ws.rs.POST;

/**
 * REST Web Service
 *
 * @author edgartiburcio
 */
@Path("wls/1.0")
public class SISVANWS {

    @Context
    private javax.naming.Context dbContext = null;
    private static final String DB_JNDI = "jdbc/SISVANDS";

    /**
     * Creates a new instance of SISVANWS
     */
    public SISVANWS() {
    }

    /**
     * Regresar el nombre del usuario con el correo registrado
     *
     * @param requestBody contiene el correo electronico y la contraseña del
     * usuario
     * @return a json object with the user
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/auth/getSession")
    public Response getSession(String requestBody) {
        JsonObjectBuilder jsonObjectBuilder
                = Json.createObjectBuilder();
        JsonObject response;
        Connection dbConnection;
        String correo = null, contrasenia = null;

        if (!requestBody.equals("")) {
            try {
                JsonReader bodyReader = Json.createReader(new StringReader(requestBody));
                JsonObject datosEntrada = bodyReader.readObject();
                bodyReader.close();

                if (!datosEntrada.containsKey("usuario") || !datosEntrada.containsKey("contrasenia")) {
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
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException ex) {
                jsonObjectBuilder.add("error", "Error interno del servidor.");
                jsonObjectBuilder.add("mensaje", ex.getMessage());
                response = jsonObjectBuilder.build();
                return Response.ok(response.toString()).build();
            }

            //Realizando la conexión a la base de datos
            try {
                dbContext = new InitialContext();
                DataSource datasource = (DataSource) dbContext.lookup(DB_JNDI);
                dbConnection = datasource.getConnection();
            } catch (NamingException | SQLException ex) {
                jsonObjectBuilder.add("error", "Error al intentar obtener la conexión con la base de datos.");
                jsonObjectBuilder.add("mensaje", ex.getMessage());
                response = jsonObjectBuilder.build();
                return Response.ok(response.toString()).build();
            }

            //Obteniendo la información de la base de datos
            try {
                String query = "SELECT nombre FROM usuarios WHERE correo = ? AND contrasenia = ?";
                PreparedStatement statement = dbConnection.prepareStatement(query);
                statement.setString(1, correo);
                statement.setString(2, contrasenia);
                ResultSet result = statement.executeQuery();

                if (result.next()) {
                    jsonObjectBuilder.add("nombre", result.getString(1));
                } else {
                    jsonObjectBuilder.add("error", "Error de autenticación.");
                }

                response = jsonObjectBuilder.build();

                statement.close();
                statement.close();
                dbConnection.close();
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

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/alumnos/obtenerMediciones")
    public Response obtenerMediciones(String requestBody) {
        Connection dbConnection;
        PreparedStatement statement;
        ResultSet sqlResult;
        JsonObjectBuilder jsonObjectBuilder
                = Json.createObjectBuilder();
        JsonObject response;
        boolean hayInformacion = false;

        try {
            dbContext = new InitialContext();
            DataSource datasource = (DataSource) dbContext.lookup(DB_JNDI);
            dbConnection = datasource.getConnection();

            statement = dbConnection.prepareStatement("SELECT * FROM datos WHERE id_alumno = '1042113'");
            sqlResult = statement.executeQuery();

            JsonArrayBuilder constructorArregloJSON
                    = Json.createArrayBuilder();

            while (sqlResult.next()) {
                if (!hayInformacion) {
                    hayInformacion = true;
                }
                int numColumnas = sqlResult.getMetaData().getColumnCount();
                JsonObjectBuilder alumno
                        = Json.createObjectBuilder();

                for (int indice = 1; indice <= numColumnas; indice++) {
                    String nombreColumna = sqlResult.getMetaData().getColumnLabel(indice).toLowerCase();
                    if (sqlResult.getObject(indice) != null) {
                        Object valor = sqlResult.getObject(indice);
                        if (valor instanceof String) {
                            alumno.add(nombreColumna, String.valueOf(valor));
                        } else if (valor instanceof Date) {
                            alumno.add(nombreColumna, sqlResult.getDate(indice).toString());
                        } else if (valor instanceof Integer) {
                            alumno.add(nombreColumna, sqlResult.getInt(indice));
                        } else {
                            double numero = sqlResult.getDouble(indice) * 100;
                            numero = Math.round(numero);
                            alumno.add(nombreColumna, (numero / 100));
                        }
                    } else {
                        alumno.add(nombreColumna, "");
                    }
                }
                constructorArregloJSON.add(alumno);
            }

            if (hayInformacion) {
                jsonObjectBuilder.add("mediciones", constructorArregloJSON);
            } else {
                jsonObjectBuilder.add("error", "No hay datos.");
            }

            response = jsonObjectBuilder.build();
        } catch (NamingException ex) {
            jsonObjectBuilder.add("error", "Error al intentar obtener el nommre de la conexion de la base de datos.");
            jsonObjectBuilder.add("mensaje", ex.getMessage());
            response = jsonObjectBuilder.build();
            return Response.ok(response.toString()).build();
        } catch (SQLException ex) {
            jsonObjectBuilder.add("error", "Error al intentar ejecutar la consulta en la base de datos.");
            jsonObjectBuilder.add("mensaje", ex.getMessage());
            response = jsonObjectBuilder.build();
            return Response.ok(response.toString()).build();
        }
        return Response.ok(response.toString()).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/alumnos/obtenerDatos")
    public Response obtenerDatos(String requestBody) {
        Connection dbConnection;
        PreparedStatement statement;
        ResultSet sqlResult;
        JsonObjectBuilder jsonObjectBuilder
                = Json.createObjectBuilder();
        JsonObject response;
        boolean hayInformacion = false;
        String idAlumno;
        if (!requestBody.equals("")) {
            try {
                JsonReader bodyReader = Json.createReader(new StringReader(requestBody));
                JsonObject datosEntrada = bodyReader.readObject();
                bodyReader.close();

                if (!datosEntrada.containsKey("id_alumno")) {
                    throw new Exception("Datos incompletos.");
                }

                idAlumno = datosEntrada.getString("id_alumno");

            } catch (Exception ex) {
                jsonObjectBuilder.add("error", "Favor de proporcionar todos los datos.");
                jsonObjectBuilder.add("mensaje", ex.getMessage());
                response = jsonObjectBuilder.build();
                return Response.ok(response.toString()).build();
            }
            
            //Inicializando la conexión a la base de datos
            try {
                dbContext = new InitialContext();
                DataSource datasource = (DataSource) dbContext.lookup(DB_JNDI);
                dbConnection = datasource.getConnection();

                String consultaDatos = "SELECT alumnos.nombre," + "\n" +
                                              "apellido_p," + "\n" +
                                              "apellido_m," + "\n" +
                                              "sexo," + "\n" +
                                              "fecha_nac," + "\n" +
                                              "escuelas.nombre as escuela," + "\n" +
                                              "grado," + "\n" +
                                              "letra" + "\n" +
                                       "FROM alumnos, grupos, escuelas" + "\n" +
                                       "WHERE id_alumno = ?" + "\n" +
                                       "AND grupos.id_grupo = alumnos.id_grupo" + "\n" +
                                       "AND grupos.id_escuela = escuelas.id_escuela";
                
                statement = dbConnection.prepareStatement(consultaDatos);
                statement.setString(1, idAlumno);
                sqlResult = statement.executeQuery();

                JsonArrayBuilder constructorArregloJSON
                        = Json.createArrayBuilder();

                while (sqlResult.next()) {
                    if (!hayInformacion) {
                        hayInformacion = true;
                    }
                    int numColumnas = sqlResult.getMetaData().getColumnCount();
                    JsonObjectBuilder alumno
                            = Json.createObjectBuilder();

                    for (int indice = 1; indice <= numColumnas; indice++) {
                        String nombreColumna = sqlResult.getMetaData().getColumnLabel(indice).toLowerCase();
                        if (sqlResult.getObject(indice) != null) {
                            Object valor = sqlResult.getObject(indice);
                            if (valor instanceof String) {
                                alumno.add(nombreColumna, String.valueOf(valor));
                            } else if (valor instanceof Date) {
                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                alumno.add(nombreColumna, dateFormat.format(sqlResult.getDate(indice)));
                            } else if (valor instanceof Integer) {
                                alumno.add(nombreColumna, sqlResult.getInt(indice));
                            } else {
                                double numero = sqlResult.getDouble(indice) * 100;
                                numero = Math.round(numero);
                                alumno.add(nombreColumna, (numero / 100));
                            }
                        } else {
                            alumno.add(nombreColumna, "");
                        }
                    }
                    constructorArregloJSON.add(alumno);
                }

                if (hayInformacion) {
                    jsonObjectBuilder.add("datos", constructorArregloJSON);
                } else {
                    jsonObjectBuilder.add("error", "No hay datos.");
                }

                response = jsonObjectBuilder.build();
            } catch (NamingException ex) {
                jsonObjectBuilder.add("error", "Error al intentar obtener el nommre de la conexion de la base de datos.");
                jsonObjectBuilder.add("mensaje", ex.getMessage());
                response = jsonObjectBuilder.build();
                return Response.ok(response.toString()).build();
            } catch (SQLException ex) {
                jsonObjectBuilder.add("error", "Error al intentar ejecutar la consulta en la base de datos.");
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
}
