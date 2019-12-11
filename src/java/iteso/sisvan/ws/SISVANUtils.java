/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iteso.sisvan.ws;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
import javax.json.JsonValue;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IgnoredErrorType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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
                    columnasConValores.put(nombreColumna, datosEntrada.getString(nombreColumna));
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

                int indiceColumna = 1;
                for (String nombreColumna : nombresColumnas) {
                    statement.setString(indiceColumna++, columnasConValores.get(nombreColumna));
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

    public static byte[] generarExcelConJSON(JsonObject jsonEntrada) {
        String idAlumno = String.valueOf(jsonEntrada.getJsonArray("mediciones").getJsonObject(0).getInt("id_alumno"));
        String tituloReporte = "Reporte historico de mediciones del Alumno";
        String subTituloReporte = "Mediciones realizadas en campo de datos antropometricos";
        String nombreCompleto = jsonEntrada.getJsonArray("datos").getJsonObject(0).getString("nombre") + " "
                + jsonEntrada.getJsonArray("datos").getJsonObject(0).getString("apellido_p") + " "
                + jsonEntrada.getJsonArray("datos").getJsonObject(0).getString("apellido_m");

        XSSFWorkbook libroTrabajo = new XSSFWorkbook();
        XSSFSheet hojaCalculo = libroTrabajo.createSheet(idAlumno);
        int contadorFilas = 10;

        Row titulo = hojaCalculo.createRow(0);
        Row subTitulo = hojaCalculo.createRow(1);
        Row nombre = hojaCalculo.createRow(2);
        Row sexo = hojaCalculo.createRow(3);
        Row fechaNac = hojaCalculo.createRow(4);
        Row escuela = hojaCalculo.createRow(5);
        Row grado = hojaCalculo.createRow(6);
        Row grupo = hojaCalculo.createRow(7);
        Row encabezados = hojaCalculo.createRow(9);

        hojaCalculo.setDisplayGridlines(false);

        CellStyle estiloTitulo = libroTrabajo.createCellStyle();
        CellStyle estiloSubTitulo = libroTrabajo.createCellStyle();

        CellStyle estiloEncabezados = libroTrabajo.createCellStyle();
        estiloEncabezados.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        estiloEncabezados.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle estiloFilasPares = libroTrabajo.createCellStyle();

        CellStyle estiloFilasNones = libroTrabajo.createCellStyle();
        estiloFilasNones.setFillBackgroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        estiloFilasNones.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle estiloNombresDatos = libroTrabajo.createCellStyle();

        XSSFFont letraTitulo = libroTrabajo.createFont();
        letraTitulo.setFontName("Calibri");
        letraTitulo.setFontHeightInPoints((short) 16);
        letraTitulo.setBold(true);
        estiloTitulo.setFont(letraTitulo);

        XSSFFont letraSubTitulo = libroTrabajo.createFont();
        letraSubTitulo.setFontName("Calibri");
        letraSubTitulo.setFontHeightInPoints((short) 12);
        estiloSubTitulo.setFont(letraSubTitulo);

        Cell celdaTitulo = titulo.createCell(1);
        CellRangeAddress rangoTitulo = new CellRangeAddress(titulo.getRowNum(), titulo.getRowNum(), celdaTitulo.getColumnIndex(), celdaTitulo.getColumnIndex() + 5);
        hojaCalculo.addMergedRegion(rangoTitulo);
        celdaTitulo.setCellStyle(estiloTitulo);
        celdaTitulo.setCellValue(tituloReporte);

        Cell celdaSubTitulo = subTitulo.createCell(1);
        CellRangeAddress rangoSubTitulo = new CellRangeAddress(subTitulo.getRowNum(), subTitulo.getRowNum(),
                celdaSubTitulo.getColumnIndex(), celdaSubTitulo.getColumnIndex() + 5);
        hojaCalculo.addMergedRegion(rangoSubTitulo);
        celdaSubTitulo.setCellStyle(estiloSubTitulo);
        celdaSubTitulo.setCellValue(subTituloReporte);

        XSSFFont letraEncabezados = libroTrabajo.createFont();
        letraEncabezados.setFontName("Calibri");
        letraEncabezados.setFontHeightInPoints((byte) 10);
        letraEncabezados.setBold(true);
        letraEncabezados.setColor(IndexedColors.WHITE.getIndex());
        estiloEncabezados.setFont(letraEncabezados);

        XSSFFont letraFilas = libroTrabajo.createFont();
        letraFilas.setFontName("Calibri");
        letraFilas.setFontHeightInPoints((byte) 10);
        estiloFilasPares.setFont(letraFilas);
        estiloFilasNones.setFont(letraFilas);

        XSSFFont letraDatosPrincipales = libroTrabajo.createFont();
        letraDatosPrincipales.setFontName("Calibri");
        letraDatosPrincipales.setFontHeightInPoints((byte) 10);
        letraDatosPrincipales.setBold(true);
        estiloNombresDatos.setFont(letraDatosPrincipales);

        Cell celdaNombre = nombre.createCell(0);
        celdaNombre.setCellStyle(estiloNombresDatos);
        celdaNombre.setCellValue("Nombre completo:");

        Cell celdaDatoNombre = nombre.createCell(1);
        celdaDatoNombre.setCellStyle(estiloFilasPares);
        celdaDatoNombre.setCellValue(nombreCompleto);

        Cell celdaSexo = sexo.createCell(0);
        celdaSexo.setCellStyle(estiloNombresDatos);
        celdaSexo.setCellValue("Sexo:");

        Cell celdaDatoSexo = sexo.createCell(1);
        celdaDatoSexo.setCellStyle(estiloFilasPares);
        celdaDatoSexo.setCellValue(jsonEntrada.getJsonArray("datos").getJsonObject(0).getString("sexo"));

        Cell celdaFechaNac = fechaNac.createCell(0);
        celdaFechaNac.setCellStyle(estiloNombresDatos);
        celdaFechaNac.setCellValue("Fecha de nacimiento:");

        Cell celdaDatoFechaNac = fechaNac.createCell(1);
        celdaDatoFechaNac.setCellStyle(estiloFilasPares);
        celdaDatoFechaNac.setCellValue(jsonEntrada.getJsonArray("datos").getJsonObject(0).getString("fecha_nac"));

        Cell celdaEscuela = escuela.createCell(0);
        celdaEscuela.setCellStyle(estiloNombresDatos);
        celdaEscuela.setCellValue("Escuela:");

        Cell celdaDatoEscuela = escuela.createCell(1);
        celdaDatoEscuela.setCellStyle(estiloFilasPares);
        celdaDatoEscuela.setCellValue(jsonEntrada.getJsonArray("datos").getJsonObject(0).getString("escuela"));

        Cell celdaGrado = grado.createCell(0);
        celdaGrado.setCellStyle(estiloNombresDatos);
        celdaGrado.setCellValue("Grado:");

        Cell celdaDatoGrado = grado.createCell(1);
        celdaDatoGrado.setCellStyle(estiloFilasPares);
        celdaDatoGrado.setCellValue(jsonEntrada.getJsonArray("datos").getJsonObject(0).getString("grado"));

        Cell celdaGrupo = grupo.createCell(0);
        celdaGrupo.setCellStyle(estiloNombresDatos);
        celdaGrupo.setCellValue("Grupo:");

        Cell celdaDatoGrupo = grupo.createCell(1);
        celdaDatoGrupo.setCellStyle(estiloFilasPares);
        celdaDatoGrupo.setCellValue(jsonEntrada.getJsonArray("datos").getJsonObject(0).getString("letra"));

        for (int indice = 0; indice < jsonEntrada.getJsonArray("mediciones").size(); indice++) {
            JsonObject fila = jsonEntrada.getJsonArray("mediciones").getJsonObject(indice);
            Row filaExcel = hojaCalculo.createRow(contadorFilas++);

            int contadorCelda = 0;

            for (Object actual : fila.keySet()) {
                String columna = actual.toString();
                if (columna.equals("id_alumno")) {
                    continue;
                }
                if (indice == jsonEntrada.getJsonArray("mediciones").size() - 1) {
                    Cell celdaEncabezado = encabezados.createCell(contadorCelda);
                    celdaEncabezado.setCellValue(columna);
                    celdaEncabezado.setCellStyle(estiloEncabezados);
                    hojaCalculo.autoSizeColumn(contadorCelda);
                }

                Cell celda = filaExcel.createCell(contadorCelda++);
                celda.setCellStyle(estiloFilasPares);
                celda.setCellValue(fila.getString(columna));
            }

        }
        
        hojaCalculo.addIgnoredErrors(new CellRangeAddress(0, 15, 0, 10), IgnoredErrorType.NUMBER_STORED_AS_TEXT);

        try {
            ByteArrayOutputStream salida = new ByteArrayOutputStream();

            libroTrabajo.write(salida);
            libroTrabajo.close();

            return salida.toByteArray();
        } catch (IOException ex) {
            return ex.getMessage().getBytes();
        }
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

    public static JsonObject generarJSONGraficoPastel(String query, String idAlumno, String nombreSerieX) {
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

                    alumno.add("id", contador++);
                    alumno.add("serie", result.getString(2));
                    alumno.add(nombreSerieX, result.getString(1));
                    alumno.add("valor", result.getInt(3));
                    constructorArregloJSON.add(alumno);

                }

                if (hayInformacion) {
                    jsonObjectBuilder.add("datos", constructorArregloJSON);
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

    public static JsonObject generarJSONMultiTipoDatos(String query, String parameter, String raiz, boolean tieneParametro) {
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

            if (tieneParametro) {
                statement.setString(1, parameter);
            }

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
                                alumno.add(nombreColumna, String.format("%.2f", (numero / 100)));
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
