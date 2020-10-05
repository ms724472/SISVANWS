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
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import static javax.json.JsonValue.ValueType.NUMBER;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IgnoredErrorType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFColor;
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
        svg = svg.replace("-apple-system,BlinkMacSystemFont,'Segoe UI','Helvetica Neue',Arial,sans-serif", "Arial");
        svg = svg.replaceFirst("rgba\\([0, ]+\\)", "white");
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
        String test = "<svg width=\"100%\" height=\"100%\" style=\"position:absolute;left:0px;top:0px;padding:inherit;\"><defs><clipPath id=\"chart1000267490887$cp0\"><rect x=\"0\" y=\"0\" width=\"343\" height=\"244\"></rect></clipPath></defs><g cursor=\"default\" font-family=\"-apple-system,BlinkMacSystemFont,'Segoe UI','Helvetica Neue',Arial,sans-serif\" font-size=\"12px\" font-weight=\"400\"><g cursor=\"default\"><g><rect width=\"387.109\" height=\"350\" fill=\"rgba(0,0,0,0)\"></rect><g transform=\"matrix(1,0,0,1,158,8)\"><g><rect width=\"95\" height=\"22\" fill=\"rgba(0,0,0,0)\"></rect><g><g><text dominant-baseline=\"text-before-edge\" fill=\"rgba(0, 0, 0, 0.8)\" x=\"20\" y=\"3\">imc</text><g><line x1=\"3\" y1=\"11\" x2=\"13\" y2=\"11\" stroke=\"rgb(35, 123, 177)\" stroke-width=\"2\" shape-rendering=\"crispEdges\"></line><path d=\"M5,8H11V14H5Z\" fill=\"rgb(35, 123, 177)\" shape-rendering=\"crispEdges\"></path></g><rect x=\"1\" y=\"1\" width=\"40\" height=\"20\" fill=\"rgba(0,0,0,0)\"></rect><text dominant-baseline=\"text-before-edge\" fill=\"rgba(0, 0, 0, 0.8)\" x=\"66\" y=\"3\">ideal</text><g><line x1=\"49\" y1=\"11\" x2=\"59\" y2=\"11\" stroke=\"#006600\" stroke-width=\"2\" shape-rendering=\"crispEdges\"></line><path d=\"M51,8H57V14H51Z\" fill=\"#006600\" shape-rendering=\"crispEdges\"></path></g><rect x=\"47\" y=\"1\" width=\"47\" height=\"20\" fill=\"rgba(0,0,0,0)\"></rect></g></g></g></g><g transform=\"matrix(1,0,0,1,10,47)\"><text dominant-baseline=\"middle\" x=\"12.9375\" y=\"244\" text-anchor=\"end\" fill=\"rgba(0, 0, 0, 0.8)\">0</text><text dominant-baseline=\"middle\" x=\"12.9375\" y=\"203.33333333333334\" text-anchor=\"end\" fill=\"rgba(0, 0, 0, 0.8)\">3</text><text dominant-baseline=\"middle\" x=\"12.9375\" y=\"162.66666666666669\" text-anchor=\"end\" fill=\"rgba(0, 0, 0, 0.8)\">6</text><text dominant-baseline=\"middle\" x=\"12.9375\" y=\"122\" text-anchor=\"end\" fill=\"rgba(0, 0, 0, 0.8)\">9</text><text dominant-baseline=\"middle\" x=\"12.9375\" y=\"81.33333333333334\" text-anchor=\"end\" fill=\"rgba(0, 0, 0, 0.8)\">12</text><text dominant-baseline=\"middle\" x=\"12.9375\" y=\"40.66666666666666\" text-anchor=\"end\" fill=\"rgba(0, 0, 0, 0.8)\">15</text><text dominant-baseline=\"middle\" text-anchor=\"end\" fill=\"rgba(0, 0, 0, 0.8)\" x=\"12.9375\">18</text></g><g transform=\"matrix(1,0,0,1,33.9375,301)\"><g><text dominant-baseline=\"text-before-edge\" fill=\"rgba(0, 0, 0, 0.55)\" font-size=\"14px\" text-anchor=\"middle\" x=\"171.53125\" y=\"22\">Historico IMC</text></g><text dominant-baseline=\"text-before-edge\" x=\"171.53125\" text-anchor=\"middle\" fill=\"rgba(0, 0, 0, 0.8)\">21/04/2015</text></g><g transform=\"matrix(1,0,0,1,34,47)\"><rect width=\"343\" height=\"244\" fill=\"rgba(0,0,0,0)\"></rect><g></g><line y1=\"244\" x2=\"343\" y2=\"244\" shape-rendering=\"crispEdges\" stroke=\"rgba(78,82,86,0.4)\" pointer-events=\"none\"></line><path d=\"M0,203.3H343M0,162.7H343M0,122H343M0,81.3H343M0,40.7H343M0,0H343\" shape-rendering=\"crispEdges\" stroke=\"rgba(196,206,215,0.4)\" pointer-events=\"none\"></path><g clip-path=\"url(#chart1000267490887$cp0)\"><g><polyline points=\"171.5 54.2\" fill=\"none\" stroke=\"rgb(35, 123, 177)\" stroke-width=\"3\"></polyline></g><g><polyline points=\"171.5 40.7\" fill=\"none\" stroke=\"#006600\" stroke-width=\"3\"></polyline></g></g><path d=\"M0,244H343\" shape-rendering=\"crispEdges\" stroke=\"#9E9E9E\" pointer-events=\"none\"></path><g fill=\"rgba(0,0,0,0)\"><path d=\"M167,49H177V59H167Z\" fill=\"rgb(35, 123, 177)\" stroke=\"#FFFFFF\" stroke-width=\"1.25\"></path></g><g fill=\"rgba(0,0,0,0)\"><path d=\"M167,36H177V46H167Z\" fill=\"#006600\" stroke=\"#FFFFFF\" stroke-width=\"1.25\"></path></g></g></g></g></g></svg>";
        System.out.println(test.replaceAll("g fill=\"rgba\\(0,0,0,0\\)\"", "g fill=\"white\""));
    }

    public static byte[] generarExcelConJSON(JsonObject jsonEntrada, byte[][] graficos) {
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
                if(fila.get(columna).getValueType() == NUMBER) {
                    celda.setCellValue(fila.getJsonNumber(columna).toString());
                } else {
                    celda.setCellValue(fila.getString(columna));
                }                
            }

        }
        
        //Se inserta el grafico de IMC
        int idGraficoIMC = libroTrabajo.addPicture(graficos[0], Workbook.PICTURE_TYPE_PNG);
        
        XSSFDrawing fondoIMC = hojaCalculo.createDrawingPatriarch();
        XSSFClientAnchor posicionadorIMC = new XSSFClientAnchor();
        
        posicionadorIMC.setCol1(0);
        posicionadorIMC.setRow1(contadorFilas+1);
        posicionadorIMC.setDx2(1210000);
        posicionadorIMC.setDy2(2090000);
        
        XSSFPicture  graficoIMC = fondoIMC.createPicture(posicionadorIMC, idGraficoIMC);
        graficoIMC.resize(1.5);
        
        //Se inserta el grafico de Talla
        int idGraficoTalla = libroTrabajo.addPicture(graficos[1], Workbook.PICTURE_TYPE_PNG);
        
        XSSFDrawing fondoTalla = hojaCalculo.createDrawingPatriarch();
        XSSFClientAnchor posicionadorTalla = new XSSFClientAnchor();
        
        posicionadorTalla.setCol1(5);
        posicionadorTalla.setRow1(contadorFilas+1);
        posicionadorTalla.setDx2(1450000);
        posicionadorTalla.setDy2(2090000);
        
        XSSFPicture  graficoTalla = fondoTalla.createPicture(posicionadorTalla, idGraficoTalla);
        graficoTalla.resize(1.5);
        
        //Se inserta el grafico de Talla
        int idGraficoPeso = libroTrabajo.addPicture(graficos[2], Workbook.PICTURE_TYPE_PNG);
        
        XSSFDrawing fondoPeso = hojaCalculo.createDrawingPatriarch();
        XSSFClientAnchor posicionadorPeso = new XSSFClientAnchor();
        
        posicionadorPeso.setCol1(11);
        posicionadorPeso.setRow1(contadorFilas+1);
        posicionadorPeso.setDx2(2000000);
        posicionadorPeso.setDy2(2090000);
        
        XSSFPicture  graficoPeso = fondoPeso.createPicture(posicionadorPeso, idGraficoPeso);
        graficoPeso.resize(1.5);
        
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
    
    public static byte[] generarExcelGrupal(String desde, String hasta, String id_escuela) throws NamingException, SQLException, IOException {
        XSSFWorkbook libroTrabajo = new XSSFWorkbook();
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
        
        CellStyle estiloFilasEspeciales  = libroTrabajo.createCellStyle();
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
                    for(int indiceColumna = 1; indiceColumna < contadorColumnas; indiceColumna++) {
                        Cell celda = fila.createCell(indiceColumna-1);
                        
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
                
                for(int indiceColumna = 1; indiceColumna < contadorColumnas; indiceColumna++) {
                    Cell celdaEncabezado = nombresColumnas.createCell(indiceColumna-1);
                    celdaEncabezado.setCellValue(metaDatos.getColumnLabel(indiceColumna));
                    celdaEncabezado.setCellStyle(estiloColumnas);                    
                    hojaCalculo.autoSizeColumn(indiceColumna-1);
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
