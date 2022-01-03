package de.hhu.cs.dbs.propra.presentation.rest;

import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.inject.Inject;
import javax.sql.DataSource;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)
public class NutzerController {
    @Inject
    private DataSource dataSource;

    //netstat -ano | findstr :8080
    //taskkill -pid ProzessId /f
    @Context
    private SecurityContext securityContext;

    @Context
    private UriInfo uriInfo;

    @Path("nutzer")
    @GET //curl -X GET "http://localhost:8080/nutzer" -H  "accept: application/json;charset=UTF-8"
    public List<Map<String, Object>> getNutzer(@QueryParam("email") String email) throws SQLException {
        String query = "SELECT * FROM Nutzer ";
        if (email != null) {
            query = query + " WHERE Email like '"+email+"' ";
        }
        query = query + ";";
        Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.closeOnCompletion();
        ResultSet resultSet = preparedStatement.executeQuery();
        List<Map<String, Object>> entities = new ArrayList<>();
        Map<String, Object> entity;
        while (resultSet.next()) {
            entity = new HashMap<>();
            entity.put("nutzerid", resultSet.getRow());
            entity.put("email", resultSet.getString(1));
            entity.put("passwort", resultSet.getString(3));
            entity.put("benutzername", resultSet.getString(2));
            entities.add(entity);
        }
        resultSet.close();
        connection.close();
        return entities;
    }

    @Path("nutzer")
    @POST //curl -X POST "http://localhost:8080/nutzer" -H  "accept: */*" -H  "Content-Type: multipart/form-data" -F "email=joe7@web.de" -F "benutzername=joe7" -F "passwort=00AB"
    public Response addNutzer(@FormDataParam("email") String email,
                                @FormDataParam("benutzername") String benutzername,
                                @FormDataParam("passwort") String passwort)throws SQLException{
        if (email == null || benutzername == null || passwort == null ) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        Connection connection = dataSource.getConnection();
        int newGeneratedID = 0;
        int adresseId = 1;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("insert into Nutzer(Email,Benutzername,Passwort, AdresseID)values (?,?,?,?);", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, benutzername);
            preparedStatement.setString(3, passwort);
            preparedStatement.setInt(4, adresseId);
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                newGeneratedID = resultSet.getInt(1);
            }
            resultSet.close();
            connection.close();
        }catch (SQLException exception){
            connection.close();
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        return Response.created(uriInfo.getAbsolutePathBuilder().path("" + newGeneratedID + "").build()).build();
    }
}
