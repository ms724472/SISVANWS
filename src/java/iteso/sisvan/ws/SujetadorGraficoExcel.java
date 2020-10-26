/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iteso.sisvan.ws;

import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveSize2D;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTMarker;

/**
 *
 * @author etiburci
 */
public class SujetadorGraficoExcel extends XSSFClientAnchor {
    public SujetadorGraficoExcel(XSSFSheet hojaEstilos, int indiceColumna, int indiceFila, int ancho, int alto) {
        super(hojaEstilos, crearMarcador(indiceColumna, indiceFila), obtenerDimensiones(ancho, alto));
    }

    private static CTMarker crearMarcador(int indiceColumna, int indiceFila) {
        CTMarker marcador = CTMarker.Factory.newInstance();
        marcador.setCol(indiceColumna);
        marcador.setColOff(0);
        marcador.setRow(indiceFila);
        marcador.setRowOff(0);
        return marcador;
    }

    private static CTPositiveSize2D obtenerDimensiones(int ancho, int alto) {
        CTPositiveSize2D dimensiones = CTPositiveSize2D.Factory.newInstance();
        dimensiones.setCx(ancho);
        dimensiones.setCy(alto);
        return dimensiones;
    }
}
