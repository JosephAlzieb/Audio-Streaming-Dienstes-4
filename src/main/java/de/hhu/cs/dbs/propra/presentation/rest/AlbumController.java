package de.hhu.cs.dbs.propra.presentation.rest;

import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.sql.DataSource;
import javax.swing.text.DateFormatter;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)
public class AlbumController {
    @Inject
    private DataSource dataSource;

    @Context
    private SecurityContext securityContext;

    @Context
    private UriInfo uriInfo;

    @Path("alben")
    @GET //curl -X GET "http://localhost:8080/alben" -H  "accept: application/json;charset=UTF-8"
    public List<Map<String, Object>> getAlben(@DefaultValue("0") @QueryParam("trackanzahl") int trackanzahl,
                                              @QueryParam("bezeichnung") String bezeichnung) throws SQLException {
//        String query = "select A.*,count(TitelID) as trackanzahl \n" +
//                       "from Titel_gehoert_zu_Album T join Album A on A.AlbumID = T.AlbumID \n" +
//                       "group by A.AlbumID \n" +
//                       "having trackanzahl >= '" + trackanzahl + "' ";

        String query = "Select * from Album";
        if(bezeichnung != null){
            query = query + " WHERE Bezeichnung like '" + bezeichnung + "' ";
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
            entity.put("albumid", resultSet.getObject(1));
            entity.put("bezeichnung", resultSet.getObject(2));
            entity.put("erscheinungsjahr", resultSet.getObject(3));
            entities.add(entity);
            System.out.println(entity);
        }
        resultSet.close();
        connection.close();
        return entities;
    }


    @Path("alben")
    @RolesAllowed("Kuenstler")
    @POST// curl -X POST "http://localhost:8080/alben" -H  "accept: */*" -H "Content-Type: multipart/form-data" -F "bezeichnung=album5" -F "erscheinungsjahr=2000" -u "joe2@web.de:00AB"
    public Response addAlbum(@FormDataParam("bezeichnung") String bezeichnung,
                             @FormDataParam("erscheinungsjahr") String erscheinungsjahr) throws SQLException {
        if (bezeichnung == null || erscheinungsjahr == null ){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        try {
            //Erscheinungsjahr sollte nur so yyyy sein
            //zwischen 0 und 99999
            int date = Integer.parseInt(erscheinungsjahr);
            if(date < 0 || date > 9999){
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
        }catch (DateTimeParseException e){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        Connection connection = dataSource.getConnection();
        int newGeneratedID = 0;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("insert into Album(Bezeichnung,Erscheinungsjahr)values (?,?);", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, bezeichnung);
            preparedStatement.setString(2, erscheinungsjahr);
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                newGeneratedID = resultSet.getInt(1);
            }
            preparedStatement = connection.prepareStatement("insert into Kuenstler_veroeffentlicht_Album(AlbumID,Email) VALUES (?,?);", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, newGeneratedID);
            preparedStatement.setObject(2, securityContext.getUserPrincipal().getName());
            preparedStatement.executeUpdate();
            resultSet.close();
            connection.close();
        } catch (SQLException exception) {
            connection.close();
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        return Response.created(uriInfo.getAbsolutePathBuilder().path("" + newGeneratedID + "").build()).build();
    }
}
