package de.hhu.cs.dbs.propra.presentation.rest;


import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.inject.Inject;
import javax.sql.DataSource;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)
public class PremiumNutzerController {

    @Inject
    private DataSource dataSource;

    @Context
    private SecurityContext securityContext;

    @Context
    private UriInfo uriInfo;

    @Path("premiumnutzer")
    @GET //curl -X GET "http://localhost:8080/premiumnutzer" -H  "accept: application/json;charset=UTF-8"
    public List<Map<String, Object>> getPremiumNutzer(@QueryParam("abgelaufen") Boolean abgelaufen) throws SQLException {
        String query = "SELECT * FROM PremiumNutzer P JOIN Nutzer N on P.Email = N.Email";
        if(abgelaufen != null){
            if (abgelaufen) {
                query = query + " WHERE MitgliedschaftsDtaum <= '" + LocalDate.now() + "' ;";
            } else {
                query = query + " WHERE MitgliedschaftsDtaum >= '" + LocalDate.now() + "' ";
            }
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
            entity.put("premiumnutzerid", resultSet.getRow());
            entity.put("nutzerid", resultSet.getRow());
            entity.put("email", resultSet.getObject(1));
            entity.put("ablaufdatum", resultSet.getObject(2));
            entity.put("benutzername", resultSet.getObject(4));
            entity.put("passwort", resultSet.getObject(5));
            entities.add(entity);
        }
        resultSet.close();
        connection.close();
        return entities;
    }

    @Path("premiumnutzer")
    @POST//curl -X POST "http://localhost:8080/premiumnutzer" -H  "accept: */*" -H  "Content-Type: multipart/form-data" -F "email=joe8@web.de" -F "benutzername=joe8" -F "passwort=00AB" -F "ablaufdatum=2022-11-14"
    public Response addPremiumNutzer(@FormDataParam("email") String email,
                                     @FormDataParam("benutzername") String benutzername,
                                     @FormDataParam("passwort") String passwort,
                                     @FormDataParam("ablaufdatum") String ablaufdatum) throws SQLException {
        if (email == null || benutzername == null || passwort == null || ablaufdatum == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        try {
            LocalDate localDate = LocalDate.parse(ablaufdatum);
        } catch (DateTimeParseException e){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        Connection connection = dataSource.getConnection();
        int newGeneratedID = 0;
        int adresseId = 1;

        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * from Nutzer where Email = ?;");
        preparedStatement.closeOnCompletion();
        preparedStatement.setObject(1,email );
        ResultSet resultSet = preparedStatement.executeQuery();

        // erstmal schauen, ob der Nutzer schon existiert ,
        // wenn ja dann brauchen wir das nicht nocht mal hinzufügn, denn das geht nicht.
        if (!resultSet.next()){
            try{
                preparedStatement = connection.prepareStatement("insert into Nutzer(Email,Benutzername,Passwort, AdresseID)values (?,?,?,?);", Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1, email);
                preparedStatement.setString(2, benutzername);
                preparedStatement.setString(3, passwort);
                preparedStatement.setInt(4, adresseId);
                preparedStatement.executeUpdate();
            }catch (SQLException exception) {
                connection.close();
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
        }
        resultSet.close();

        try {
            preparedStatement = connection.prepareStatement("insert into PremiumNutzer(Email,MitgliedschaftsDtaum) VALUES (?,?)", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, email);
            preparedStatement.setObject(2, ablaufdatum);
            preparedStatement.executeUpdate();
            resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                newGeneratedID = resultSet.getInt(1);
            }
            resultSet.close();
            connection.close();
        } catch (SQLException exception) {
            connection.close();
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        return Response.created(uriInfo.getAbsolutePathBuilder().path("" + newGeneratedID + "").build()).build();
    }
}
