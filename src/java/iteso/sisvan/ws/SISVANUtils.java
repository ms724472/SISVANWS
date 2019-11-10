/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iteso.sisvan.ws;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 *
 * @author edgartiburcio
 */
public class SISVANUtils {

    public static final String DB_JNDI = "jdbc/SISVANDS";

    public static JsonObject insertarNuevoDatoEnBD(String cuerpoPeticion, String[] nombresColumnas, String query) {
        HashMap<String, String> columnasConValores = new HashMap<>();
        JsonObject response;
        DataSource datasource;
        boolean sqlResult;
        JsonObjectBuilder jsonObjectBuilder
                = Json.createObjectBuilder();

        if (!cuerpoPeticion.equals("")) {
            try (JsonReader bodyReader = Json.createReader(new StringReader(cuerpoPeticion))) {
                JsonObject datosEntrada = bodyReader.readObject();

                for (String nombreColumna : nombresColumnas) {
                    if (!datosEntrada.containsKey(nombreColumna)) {
                        throw new Exception("Datos incompletos.");
                    }
                    columnasConValores.put(nombreColumna, datosEntrada.getString("id_alumno"));
                }
            } catch (Exception ex) {
                jsonObjectBuilder.add("error", "Favor de proporcionar todos los datos.");
                jsonObjectBuilder.add("mensaje", ex.getMessage());
                response = jsonObjectBuilder.build();
                return response;
            }

            //Encontrar la clase para poder realizar la conexión con RDS
            try {
                datasource = (DataSource) new InitialContext().lookup(DB_JNDI);
            } catch (NamingException ex) {
                jsonObjectBuilder.add("error", "Error al intentar obtener la conexión con la base de datos.");
                jsonObjectBuilder.add("mensaje", ex.getMessage());
                response = jsonObjectBuilder.build();
                return response;
            }

            //Inicializando la conexión a la base de datos
            try (Connection dbConnection = datasource.getConnection();
                    PreparedStatement statement = dbConnection.prepareStatement(query)) {

                int columnCount = 1;
                for (String nombreColumna : nombresColumnas) {
                    statement.setString(columnCount++, columnasConValores.get(nombreColumna));
                }

                sqlResult = statement.execute();
                jsonObjectBuilder.add("status", sqlResult ? "exito" : "fallo");
                response = jsonObjectBuilder.build();
            } catch (SQLException ex) {
                jsonObjectBuilder.add("error", "Error al intentar ejecutar la consulta en la base de datos.");
                jsonObjectBuilder.add("mensaje", ex.getMessage());
                response = jsonObjectBuilder.build();
                return response;
            }
        } else {
            jsonObjectBuilder.add("error", "Favor de proporcionar todos los datos.");
            response = jsonObjectBuilder.build();
        }

        return response;
    }

    public static JsonObject generarJSONGraficoLinea(String query, String idAlumno, String nombreSerieX, boolean redondear) {
        DataSource datasource;
        JsonObjectBuilder jsonObjectBuilder
                = Json.createObjectBuilder();
        JsonObject response;
        boolean hayInformacion = false;

        //Encontrar la clase para poder realizar la conexión con RDS
        try {
            datasource = (DataSource) new InitialContext().lookup(DB_JNDI);
        } catch (NamingException ex) {
            jsonObjectBuilder.add("error", "Error al intentar obtener la conexión con la base de datos.");
            jsonObjectBuilder.add("mensaje", ex.getMessage());
            response = jsonObjectBuilder.build();
            return response;
        }

        try (Connection dbConnection = datasource.getConnection();
                PreparedStatement statement = dbConnection.prepareStatement(query)) {
            statement.setString(1, idAlumno);

            try (ResultSet result = statement.executeQuery()) {
                JsonArrayBuilder constructorArregloJSON
                        = Json.createArrayBuilder();
                int contador = 0;

                while (result.next()) {
                    if (!hayInformacion) {
                        hayInformacion = true;
                    }
                    int numColumnas = result.getMetaData().getColumnCount();
                    JsonObjectBuilder alumno
                            = Json.createObjectBuilder();

                    for (int indice = 2; indice <= numColumnas; indice++) {
                        String nombreColumna = result.getMetaData().getColumnLabel(indice).toLowerCase();
                        if (result.getObject(indice) != null) {
                            double numero = result.getDouble(indice);
                            alumno.add("id", contador++);
                            alumno.add("serie", nombreColumna);
                            alumno.add(nombreSerieX, result.getString(1));
                            alumno.add("valor", redondear
                                    ? Math.round(numero)
                                    : numero);
                            constructorArregloJSON.add(alumno);
                        } else {
                            alumno.add(nombreColumna, "");
                        }
                    }
                }

                if (hayInformacion) {
                    jsonObjectBuilder.add("mediciones", constructorArregloJSON);
                } else {
                    jsonObjectBuilder.add("error", "No hay datos.");
                }

                response = jsonObjectBuilder.build();
            } catch (SQLException ex) {
                throw ex;
            }
        } catch (SQLException ex) {
            jsonObjectBuilder.add("error", "Error al intentar ejecutar la consulta en la base de datos.");
            jsonObjectBuilder.add("mensaje", ex.getMessage());
            response = jsonObjectBuilder.build();
            return response;
        }
        return response;
    }

    public static JsonObject generarJSONMultiTipoDatos(String query, String parameter, String raiz) {
        DataSource datasource;
        JsonObjectBuilder jsonObjectBuilder
                = Json.createObjectBuilder();
        JsonObject response;
        boolean hayInformacion = false;

        //Encontrar la clase para poder realizar la conexión con RDS
        try {
            datasource = (DataSource) new InitialContext().lookup(DB_JNDI);
        } catch (NamingException ex) {
            jsonObjectBuilder.add("error", "Error al intentar obtener la conexión con la base de datos.");
            jsonObjectBuilder.add("mensaje", ex.getMessage());
            response = jsonObjectBuilder.build();
            return response;
        }

        try (Connection dbConnection = datasource.getConnection();
                PreparedStatement statement = dbConnection.prepareStatement(query)) {

            statement.setString(1, parameter);

            try (ResultSet result = statement.executeQuery()) {
                JsonArrayBuilder constructorArregloJSON
                        = Json.createArrayBuilder();
                while (result.next()) {
                    if (!hayInformacion) {
                        hayInformacion = true;
                    }
                    int numColumnas = result.getMetaData().getColumnCount();
                    JsonObjectBuilder alumno
                            = Json.createObjectBuilder();

                    for (int indice = 1; indice <= numColumnas; indice++) {
                        String nombreColumna = result.getMetaData().getColumnLabel(indice).toLowerCase();
                        if (result.getObject(indice) != null) {
                            Object valor = result.getObject(indice);
                            if (valor instanceof String) {
                                alumno.add(nombreColumna, String.valueOf(valor));
                            } else if (valor instanceof Date) {
                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                alumno.add(nombreColumna, dateFormat.format(result.getDate(indice)));
                            } else if (valor instanceof Integer) {
                                alumno.add(nombreColumna, result.getInt(indice));
                            } else {
                                double numero = result.getDouble(indice) * 100;
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
                    jsonObjectBuilder.add(raiz, constructorArregloJSON);
                } else {
                    jsonObjectBuilder.add("error", "No hay datos.");
                }

                response = jsonObjectBuilder.build();
            } catch (SQLException ex) {
                throw ex;
            }
        } catch (SQLException ex) {
            jsonObjectBuilder.add("error", "Error al intentar ejecutar la consulta en la base de datos.");
            jsonObjectBuilder.add("mensaje", ex.getMessage());
            response = jsonObjectBuilder.build();
            return response;
        }

        return response;
    }
}
