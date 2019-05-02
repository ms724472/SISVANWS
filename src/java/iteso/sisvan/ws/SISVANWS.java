/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iteso.sisvan.ws;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.*;
import java.util.Arrays;
import javax.json.JsonObjectBuilder;

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
     * Retrieves the current user
     *
     * @param correo
     * @return a json object with the user
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/auth/getUserName/{correo}")
    public Response getUserName(@PathParam("correo") String correo) {
        JsonObjectBuilder jsonObjectBuilder
                = Json.createObjectBuilder();
        JsonObject response = null;
        Connection dbConnection = null;

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
            String query = "SELECT nombre FROM usuarios WHERE correo = ?";
            PreparedStatement statement = dbConnection.prepareStatement(query);
            statement.setString(1, correo);
            ResultSet result = statement.executeQuery();

            if (result.next()) {
                jsonObjectBuilder.add("nombre", result.getString(1));
            } else {
                jsonObjectBuilder.add("error", "No existe ningun usuario con el correo proporcionado.");
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
