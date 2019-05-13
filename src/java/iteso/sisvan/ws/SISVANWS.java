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
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.*;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.ws.rs.POST;

/**
 * REST Web Service
 *
 * @author edgartiburcio
 */
@Path("wls/1.0")
public class SISVANWS {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of SISVANWS
     */
    public SISVANWS() {
    }

    /**
     * Regresar el nombre del usuario con el correo registrado
     *
     * @param requestBody contiene el correo electronico y la contraseña del usuario
     * @return a json object with the user
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/auth/getSession")
    public Response getSession(String requestBody) {
        JsonObjectBuilder jsonObjectBuilder
                = Json.createObjectBuilder();
        JsonObject response = null;
        Connection dbConnection = null;
        String correo = null, contrasenia = null;
        
        if(!requestBody.equals("")){
            try{
                JsonReader bodyReader = Json.createReader(new StringReader(requestBody));
                JsonObject datosEntrada = bodyReader.readObject();
                bodyReader.close();
                
                if(!datosEntrada.containsKey("usuario") || !datosEntrada.containsKey("contrasenia"))
                    throw new Exception("Datos incompletos.");
                
                correo = datosEntrada.getString("usuario");
                contrasenia = datosEntrada.getString("contrasenia");
                
            }catch(Exception ex){
                jsonObjectBuilder.add("error", "Favor de proporcionar todos los datos.");
                jsonObjectBuilder.add("mensaje", ex.getMessage());
                response = jsonObjectBuilder.build();
                return Response.ok(response.toString()).build();
            }
            
        }
        
        //Procesando la conversion de la contraseña
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            byte[] messageDigest = digest.digest(contrasenia.getBytes());
            BigInteger bInteger = new BigInteger(1, messageDigest); 
            String textoDisp = bInteger.toString(16);
            
            while (textoDisp.length() < 32) { 
                textoDisp = "0" + textoDisp; 
            }
            
            contrasenia = textoDisp;
        }catch(NoSuchAlgorithmException ex){
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
            dbConnection = DriverManager.getConnection(
                    "jdbc:mysql://sisvan-db.cmdnu6gxjvn2.us-east-2.rds.amazonaws.com:3306/SISVAN", "admin", "Tablero_92");
        } catch (SQLException ex) {
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

        return Response.ok(response.toString()).build();
    }
}
