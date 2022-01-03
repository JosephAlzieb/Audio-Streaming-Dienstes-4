package de.hhu.cs.dbs.propra.presentation.rest;

import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.annotation.security.RolesAllowed;
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

public class TitelController {
    @Inject
    private DataSource dataSource;

    @Context
    private SecurityContext securityContext;

    @Context
    private UriInfo uriInfo;

    @Path("titel")
    @GET //curl -X GET "http://localhost:8080/titel" -H  "accept: application/json;charset=UTF-8"
    public List<Map<String, Object>> getTitels(@DefaultValue("0") @QueryParam("dauer") Integer dauer,
                                               @QueryParam("bezeichnung") String bezeichnung) throws SQLException {
        String query = "SELECT * FROM Titel T , Speicherort S WHERE T.SpeicherortID = S.ID ";

        if(bezeichnung != null){
            query = query + " AND Benennung like '" + bezeichnung + "' ";
        }
        if(dauer != null){
            query = query + " AND Dauer >= '" + dauer + "' ";
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
            entity.put("titelid", resultSet.getObject(1));
            entity.put("bezeichnung", resultSet.getObject(2));
            entity.put("dauer", resultSet.getObject(3));
            entity.put("speicherort_lq", resultSet.getObject(7));
            entity.put("speicherort_hq", resultSet.getObject(8));
            entities.add(entity);
        }
        resultSet.close();
        connection.close();
        return entities;
    }

    @Path("titel/{titelid}/kommentare")
    @GET //curl -X GET "http://localhost:8080/titel/1/kommentare" -H  "accept: application/json;charset=UTF-8"
    public List<Map<String, Object>> getTitelKommentare(@PathParam("titelid") Integer titelid) throws SQLException {
        String query = "SELECT * FROM Titel WHERE ROWID = ? ;";
        Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.closeOnCompletion();
        preparedStatement.setObject(1, titelid);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (!resultSet.next()){
            System.out.println(1);
            resultSet.close();
            connection.close();
            throw new NotFoundException("kein Titel gefunden für das gegebene titelid");
        }
        resultSet.close();
        String query2 = "SELECT * FROM Nutzer_kommentiert_Titel WHERE TitelID = '" + titelid + "' ;";
        preparedStatement = connection.prepareStatement(query2);
        resultSet = preparedStatement.executeQuery();
        List<Map<String, Object>> entities = new ArrayList<>();
        Map<String, Object> entity;
        while (resultSet.next()) {
            entity = new HashMap<>();
            entity.put("kommentarid", resultSet.getRow());
            entity.put("titelid", titelid);
            entity.put("text", resultSet.getObject(3));
            entities.add(entity);
        }
        System.out.println(entities);
        resultSet.close();
        connection.close();
        return entities;
    }

    @Path("titel")
    @RolesAllowed("Kuenstler")
    @POST//curl -X POST "http://localhost:8080/titel" -H  "accept: */*" -H  "Content-Type: multipart/form-data" -F "bezeichnung=titel6" -F "dauer=200" -F "speicherort_lq=lq6" -F "speicherort_hq=hq6" -u "joe2@web.de:00AB"
    public Response addTitel(@FormDataParam("bezeichnung") String bezeichnung,
                             @FormDataParam("dauer") Integer dauer,
                             @FormDataParam("speicherort_lq") String speicherort_lq,
                             @FormDataParam("speicherort_hq") String speicherort_hq) throws SQLException {
        if (bezeichnung == null || dauer == null || speicherort_hq == null || speicherort_lq == null ){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        Connection connection = dataSource.getConnection();
        int speicherOrtID = 0;
        int newGeneratedID = 0;
        /*da ich kein GenreBeueichnung als Parameter bekomme, habeich "genreI" bei allen Bands hinzuge,
        da ich das in der Datenbank-schema als not null & unique annotiert habe
         */
        String genre = "genreI";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("insert into Speicherort (LQ,HQ) values (?,?); " , Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, speicherort_lq);
            preparedStatement.setString(2, speicherort_hq);
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                speicherOrtID = resultSet.getInt(1);
            }
            preparedStatement = connection.prepareStatement("insert into Titel (Benennung , Dauer ,GenreBezeichnung, SpeicherortID) VALUES (?,?,?,?); " , Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, bezeichnung);
            preparedStatement.setInt(2, dauer);
            preparedStatement.setString(3, genre);
            preparedStatement.setInt(4, speicherOrtID);
            preparedStatement.executeUpdate();
            resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                newGeneratedID = resultSet.getInt(1);
            }
            preparedStatement = connection.prepareStatement(" insert into Kuenstler_veroeffentlicht_Titel(Email, TitelID) VALUES (?,?); ", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, securityContext.getUserPrincipal().getName());
            preparedStatement.setInt(2, newGeneratedID);
            preparedStatement.executeUpdate();
            resultSet.close();
            connection.close();
        } catch (SQLException exception) {
            connection.close();
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        return Response.created(uriInfo.getAbsolutePathBuilder().path("" + newGeneratedID + "").build()).build();
    }
    //

    @Path("titel/{titelid}/kommentare")
    @RolesAllowed({"Nutzer"})
    @POST  //curl -X POST "http://localhost:8080/titel/2/kommentare" -H  "accept: */*" -H  "Content-Type: multipart/form-data" -F "text=kommmentar" -u "joe8@web.de:00AB"
    public Response addKuenstlerZuBand(@PathParam("titelid") Integer titelid,
                                       @FormDataParam("text") String text) throws SQLException{
        if (text == null || titelid == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * from Titel where rowid = ?;");
        preparedStatement.closeOnCompletion();
        preparedStatement.setObject(1, titelid);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (!resultSet.next()){
            resultSet.close();
            connection.close();
            throw new NotFoundException("Kein Titel fuer das gegebene TitelId");
        }
        resultSet.close();
        int newGeneratedID = 1;
        try {
            preparedStatement = connection.prepareStatement("insert into Nutzer_kommentiert_Titel(Email, TitelID, Text) VALUES (?, ?, ?);", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setObject(1, securityContext.getUserPrincipal().getName());
            preparedStatement.setObject(2, titelid);
            preparedStatement.setObject(3, text);
            preparedStatement.executeUpdate();
            resultSet = preparedStatement.getGeneratedKeys();
            while (resultSet.next()) {
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
