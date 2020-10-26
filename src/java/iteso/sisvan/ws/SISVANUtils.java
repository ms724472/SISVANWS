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
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import static javax.json.JsonValue.ValueType.NUMBER;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IgnoredErrorType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.Units;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFPicture;
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
        int filasInsertadas;
        JsonObjectBuilder jsonObjectBuilder
                = Json.createObjectBuilder();

        if (!cuerpoPeticion.equals("")) {
            try (JsonReader bodyReader = Json.createReader(new StringReader(cuerpoPeticion))) {
                JsonObject datosEntrada = bodyReader.readObject();

                for (String nombreColumna : nombresColumnas) {
                    if (!datosEntrada.containsKey(nombreColumna)) {
                        throw new Exception("Datos incompletos.");
                    }
                    
                    columnasConValores.put(nombreColumna, datosEntrada.getString(nombreColumna, ""));                    
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
                    String valor = columnasConValores.get(nombreColumna);
                    if(valor.equals("")) {
                        statement.setNull(indiceColumna++, java.sql.Types.DECIMAL);
                    } else {
                        statement.setString(indiceColumna++, valor);
                    }                    
                }

                filasInsertadas = statement.executeUpdate();
                jsonObjectBuilder.add("status", filasInsertadas > 0 ? "exito" : "fallo");
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
    
    public static String prepararSVG(String svg, int ancho, int alto) {
        svg = svg.replace("<svg width=\"100%\" height=\"100%\" style=\"position: absolute; left: 0px; top: 0px; padding: inherit;\">", "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"" + ancho + "px\" height=\"" + alto + "px\">");
        svg = svg.replace("<svg width=\"100%\" height=\"100%\" style=\"position:absolute;left:0px;top:0px;padding:inherit;\">", "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"" + ancho + "px\" height=\"" + alto + "px\">");
        svg = svg.replace("<svg width=\"100%\" height=\"100%\">", "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"" + ancho + "px\" height=\"" + alto + "px\">");
        svg = svg.replace("-apple-system,BlinkMacSystemFont,'Segoe UI','Helvetica Neue',Arial,sans-serif", "Arial");
        svg = svg.replaceFirst("rgba\\([0, ]+\\)", "white");
        svg = svg.replaceAll("<circle", "<circle r=\"0\"");
        svg = svg.replaceAll("g fill=\"rgba\\(0,0,0,0\\)\"", "g fill=\"white\"");
        while (svg.indexOf("rgba") > 0) {
            String aux = svg.substring(svg.indexOf("rgba"));
            aux = aux.substring(0, aux.indexOf(")") + 1);
            String[] componentes = aux.replace("rgba(", "").replace(")", "").replace(" ", "").split(",");
            String newFormat = "rgb(" + componentes[0] + ", " + componentes[1] + ", " + componentes[2] + ")\" fill-opacity=\"" + componentes[3];
            svg = svg.replace(aux, newFormat);
        }
        return svg;
    }
    
    public static void main(String... args) {
        int contador = 0;
        int test = contador++;
        System.out.println(contador + " " + test);
    }

    public static byte[] generarExcelConJSON(JsonObject jsonEntrada, byte[][] graficos) {
        String idAlumno = String.valueOf(jsonEntrada.getJsonArray("mediciones").getJsonObject(0).getInt("id_alumno"));
        String tituloReporte = "Reporte historico de mediciones del Alumno";
        String subTituloReporte = "Mediciones realizadas en campo de datos antropometricos";
        String nombreCompleto = jsonEntrada.getJsonArray("datos").getJsonObject(0).getString("nombre") + " "
                + jsonEntrada.getJsonArray("datos").getJsonObject(0).getString("apellido_p") + " "
                + jsonEntrada.getJsonArray("datos").getJsonObject(0).getString("apellido_m");
        Map<String, String> nombresColumnas = new HashMap<>();
        nombresColumnas.put("masa", "Peso");
        nombresColumnas.put("diagnostico_peso", "DxPeso");
        nombresColumnas.put("z_peso", "zPeso");
        nombresColumnas.put("estatura", "Talla");
        nombresColumnas.put("diagnostico_talla", "DxTalla");
        nombresColumnas.put("z_talla", "zTalla");
        nombresColumnas.put("imc", "IMC");
        nombresColumnas.put("diagnostico_imc", "DxIMC");
        nombresColumnas.put("z_imc", "zIMC");
        nombresColumnas.put("subescapula", "Subescapular");
        
        try (XSSFWorkbook libroTrabajo = new XSSFWorkbook()) {
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
            estiloEncabezados.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
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

                int contadorCelda = 3;

                for (Object actual : fila.keySet()) {
                    String columna = actual.toString();
                    if (columna.equals("id_alumno") || columna.equals("id_grupo")) {
                        continue;
                    }
                    
                    int indiceColumna = columna.equals("fecha") ? 0 
                            : columna.equals("meses") ? 1 
                            : columna.equals("grupo") ? 2 
                            : contadorCelda++;
                    
                    if (indice == jsonEntrada.getJsonArray("mediciones").size() - 1) {
                        Cell celdaEncabezado = encabezados.createCell(indiceColumna);
                        celdaEncabezado.setCellValue(nombresColumnas.containsKey(columna) ? 
                                nombresColumnas.get(columna) : normalizarColumna(columna));
                        celdaEncabezado.setCellStyle(estiloEncabezados);
                        hojaCalculo.autoSizeColumn(indiceColumna);
                    }

                    Cell celda = filaExcel.createCell(indiceColumna);
                    celda.setCellStyle(estiloFilasPares);
                    if (fila.get(columna).getValueType() == NUMBER) {
                        celda.setCellValue((columna.equals("meses") ? 
                                String.valueOf(fila.getJsonNumber(columna).intValue()) : 
                                fila.getJsonNumber(columna).toString()));
                    } else {
                        celda.setCellValue(fila.getString(columna));
                    }
                }
            }

            //Se inserta el grafico de IMC
            int idGraficoIMC = libroTrabajo.addPicture(graficos[0], Workbook.PICTURE_TYPE_PNG);

            XSSFDrawing fondoIMC = hojaCalculo.createDrawingPatriarch();
            XSSFClientAnchor posicionadorIMC = new SujetadorGraficoExcel(hojaCalculo, 0, contadorFilas + 1, 387 * Units.EMU_PER_PIXEL, 350 * Units.EMU_PER_PIXEL);

            XSSFPicture graficoIMC = fondoIMC.createPicture(posicionadorIMC, idGraficoIMC);
            graficoIMC.resize(1);

            //Se inserta el grafico de Talla
            int idGraficoTalla = libroTrabajo.addPicture(graficos[1], Workbook.PICTURE_TYPE_PNG);

            XSSFDrawing fondoTalla = hojaCalculo.createDrawingPatriarch();
            ClientAnchor posicionadorTalla = new SujetadorGraficoExcel(hojaCalculo, posicionadorIMC.getCol2() + 1, contadorFilas + 1, 387 * Units.EMU_PER_PIXEL, 350 * Units.EMU_PER_PIXEL);

            XSSFPicture graficoTalla = fondoTalla.createPicture(posicionadorTalla, idGraficoTalla);
            graficoTalla.resize(1);

            //Se inserta el grafico de Talla
            int idGraficoPeso = libroTrabajo.addPicture(graficos[2], Workbook.PICTURE_TYPE_PNG);

            XSSFDrawing fondoPeso = hojaCalculo.createDrawingPatriarch();
            ClientAnchor posicionadorPeso = new SujetadorGraficoExcel(hojaCalculo, posicionadorTalla.getCol2() + 1, contadorFilas + 1, 387 * Units.EMU_PER_PIXEL, 350 * Units.EMU_PER_PIXEL);

            XSSFPicture graficoPeso = fondoPeso.createPicture(posicionadorPeso, idGraficoPeso);
            graficoPeso.resize(1);

            hojaCalculo.addIgnoredErrors(new CellRangeAddress(0, 15, 0, 10), IgnoredErrorType.NUMBER_STORED_AS_TEXT);

            ByteArrayOutputStream salida = new ByteArrayOutputStream();

            libroTrabajo.write(salida);
            libroTrabajo.close();

            return salida.toByteArray();
        } catch (IOException ex) {
            return ex.getMessage().getBytes();
        }
    }
    
    public static String normalizarColumna(String columna) {
        String nombreColumna = "";
        for (String componente : columna.split("_")) {
            nombreColumna += " " + componente.substring(0, 1).toUpperCase() + componente.substring(1);
        }
        return nombreColumna.replaceFirst(" ", "");
    }
    
    public static byte[] generarExcelGrupal(String desde, String hasta, String id_escuela) throws NamingException, SQLException, IOException {
        try(XSSFWorkbook libroTrabajo = new XSSFWorkbook()){
            XSSFSheet hojaCalculo = libroTrabajo.createSheet("Antropometria");
            DataSource datasource;
            String query = "SELECT DATE_FORMAT(datos.fecha, '%d/%m/%Y') 'Fecha Medición', escuelas.nombre 'Escuela', \n" +
                            "calcular_grado(grupos.anio_ingreso, datos.fecha) 'Grado', grupos.letra 'Grupo', alumnos.id_alumno 'Identificador Alumno', \n" +
                            "upper(CONCAT(alumnos.apellido_p, ' ', alumnos.apellido_m, ' ', alumnos.nombre)) 'Nombre Alumno', \n" +
                            "upper(alumnos.sexo) 'Sexo',\n" +
                            "DATE_FORMAT(alumnos.fecha_nac, '%d/%m/%Y') 'Fecha de Nacimiento',\n" +
                            "datos.masa 'Peso',\n" +
                            "datos.estatura 'Talla',\n" +
                            "datos.imc 'IMC',\n" +
                            "datos.perimetro_cuello 'Perimetro Cuello',\n" +
                            "datos.cintura 'Cintura',\n" +
                            "datos.triceps 'Triceps',\n" +
                            "datos.subescapula 'Subescapular',\n" +
                            "datos.pliegue_cuello 'Pliegue Cuello'\n" +
                            "FROM datos INNER JOIN alumnos ON datos.id_alumno = alumnos.id_alumno\n" +
                            "INNER JOIN grupos on alumnos.id_grupo = grupos.id_grupo \n" +
                            "INNER JOIN escuelas ON grupos.id_escuela = escuelas.id_escuela\n" +
                            "WHERE grupos.id_escuela = ? AND datos.fecha between ? and ?";

            //Encontrar la clase para poder realizar la conexión con RDS
            datasource = (DataSource) new InitialContext().lookup(DB_JNDI);

            Row nombresColumnas = hojaCalculo.createRow(0);
            int contadorFilas = 1;

            XSSFFont letraEncabezados = libroTrabajo.createFont();
            letraEncabezados.setFontName("Calibri");
            letraEncabezados.setFontHeightInPoints((byte) 10);
            letraEncabezados.setBold(true);

            CellStyle estiloColumnas = libroTrabajo.createCellStyle();
            estiloColumnas.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
            estiloColumnas.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            estiloColumnas.setFont(letraEncabezados);
            estiloColumnas.setAlignment(HorizontalAlignment.CENTER);

            CellStyle estiloFilas = libroTrabajo.createCellStyle();
            XSSFFont letraFilas = libroTrabajo.createFont();
            letraFilas.setFontName("Calibri");
            letraFilas.setFontHeightInPoints((byte) 10);
            estiloFilas.setFont(letraFilas);
            estiloFilas.setAlignment(HorizontalAlignment.CENTER);

            CellStyle estiloFilasEspeciales = libroTrabajo.createCellStyle();
            estiloFilasEspeciales.setFont(letraFilas);

            try (Connection dbConnection = datasource.getConnection();
                    PreparedStatement statement = dbConnection.prepareStatement(query)) {

                statement.setString(1, id_escuela);
                statement.setString(2, desde);
                statement.setString(3, hasta);

                try (ResultSet resultado = statement.executeQuery()) {
                    ResultSetMetaData metaDatos = resultado.getMetaData();
                    int contadorColumnas = metaDatos.getColumnCount();

                    while (resultado.next()) {
                        Row fila = hojaCalculo.createRow(contadorFilas++);
                        for (int indiceColumna = 1; indiceColumna < contadorColumnas; indiceColumna++) {
                            Cell celda = fila.createCell(indiceColumna - 1);

                            if (metaDatos.getColumnLabel(indiceColumna).equals("Nombre Alumno")
                                    || metaDatos.getColumnLabel(indiceColumna).equals("Sexo")) {
                                celda.setCellStyle(estiloFilasEspeciales);
                            } else {
                                celda.setCellStyle(estiloFilas);
                            }

                            Object valor = resultado.getObject(indiceColumna);
                            if (valor instanceof String) {
                                celda.setCellValue(resultado.getString(indiceColumna));
                            } else if (valor instanceof Integer) {
                                celda.setCellValue(resultado.getInt(indiceColumna));
                            } else {
                                celda.setCellValue(resultado.getDouble(indiceColumna));
                            }
                        }
                    }

                    for (int indiceColumna = 1; indiceColumna < contadorColumnas; indiceColumna++) {
                        Cell celdaEncabezado = nombresColumnas.createCell(indiceColumna - 1);
                        celdaEncabezado.setCellValue(metaDatos.getColumnLabel(indiceColumna));
                        celdaEncabezado.setCellStyle(estiloColumnas);
                        hojaCalculo.autoSizeColumn(indiceColumna - 1);
                    }

                } catch (SQLException excepcion) {
                    throw excepcion;
                }
            }

            ByteArrayOutputStream salida = new ByteArrayOutputStream();

            libroTrabajo.write(salida);
            libroTrabajo.close();

            return salida.toByteArray();
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
            AtomicInteger paramCounter = new AtomicInteger(0);
                        
            query.chars().filter(ch -> ch == '?').forEach((int count) -> {
                try {
                    statement.setString(paramCounter.incrementAndGet(), idAlumno);
                } catch (SQLException exception) {
                    System.out.println(exception.getMessage());
                }
            });

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

    public static JsonObject generarJSONGraficoPastel(String query, String idAlumno, String desde, String hasta, String nombreSerieX) {
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
            statement.setString(1, desde);
            statement.setString(2, hasta);
            statement.setString(3, idAlumno);

            try (ResultSet result = statement.executeQuery()) {
                JsonArrayBuilder constructorArregloJSON
                        = Json.createArrayBuilder();
                int contador = 0;

                while (result.next()) {
                    if (!hayInformacion) {
                        hayInformacion = true;
                    }
                    
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
                                double numero = result.getDouble(indice);
                                alumno.add(nombreColumna, numero);
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
