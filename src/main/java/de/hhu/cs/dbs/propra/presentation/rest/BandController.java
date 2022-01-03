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
public class BandController {


    @Inject
    private DataSource dataSource;

    @Context
    private SecurityContext securityContext;

    @Context
    private UriInfo uriInfo;

    @Path("bands")
    @GET //curl -X GET "http://localhost:8080/bands" -H "accept: application/json;charset=UTF-8"
    public List<Map<String, Object>> getBands(@QueryParam("name") String name,
                                                  @QueryParam("geschichte") String geschichte) throws SQLException {
        String query = "SELECT * FROM Band B, Geschichte G  WHERE B.BandID = G.BandID ";

        if(name != null){
            query = query + " AND Name like '" + name + "' ";
        }
        if(geschichte != null){
            query = query + " AND G.Text like '" + geschichte + "' ";
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
            entity.put("bandid", resultSet.getObject(1));
            entity.put("name", resultSet.getObject(2));
            entity.put("geschichte", resultSet.getObject(4));
            entities.add(entity);
        }
        resultSet.close();
        connection.close();
        return entities;
    }

    @Path("bands")
    @RolesAllowed("Kuenstler")
    @POST// curl -X POST "http://localhost:8080/bands" -H  "accept: */*" -H  "Content-Type: multipart/form-data" -F "name=band6" -F "geschichte=geschichte" -u "joe2@web.de:00AB"
    public Response addBnad(@FormDataParam("name") String name,
                             @FormDataParam("geschichte") String geschichte) throws SQLException {
        if ( name == null ){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        Connection connection = dataSource.getConnection();
        int newGeneratedID = 0;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("insert into Band (Name)values (?);", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, name);
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                newGeneratedID = resultSet.getInt(1);
            }
            if (geschichte != null){
                preparedStatement = connection.prepareStatement("insert into Geschichte(Text, BandID) VALUES (?,?);", Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1, geschichte);
                preparedStatement.setInt(2, newGeneratedID);
                preparedStatement.executeUpdate();
                resultSet.close();
                connection.close();
            }
        } catch (SQLException exception) {
            connection.close();
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        return Response.created(uriInfo.getAbsolutePathBuilder().path("" + newGeneratedID + "").build()).build();
    }

    @Path("bands/{bandid}/kuenstler")
    @RolesAllowed({"Kuenstler"})
    @POST  //curl -X POST "http://localhost:8080/bands/2/kuenstler" -H  "accept: */*" -H  "Content-Type: multipart/form-data" -F "kuenstlerid=5" -u "joe9@web.de:00AB"
    public Response addKuenstlerZuBand(@PathParam("bandid") Integer bandid,
                                        @FormDataParam("kuenstlerid") Integer kuenstlerid) throws SQLException{
        if (bandid == null || kuenstlerid == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * from Band where rowid = ?;");
        preparedStatement.closeOnCompletion();
        preparedStatement.setObject(1, bandid);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (!resultSet.next()){
            resultSet.close();
            connection.close();
            throw new NotFoundException("Kein Band fuer das gegebene BandId");
        }
        resultSet.close();
        preparedStatement = connection.prepareStatement("SELECT * from Kuenstler where rowid = ?;");
        preparedStatement.closeOnCompletion();
        preparedStatement.setObject(1, kuenstlerid);
        resultSet = preparedStatement.executeQuery();
        if (!resultSet.next()){
            resultSet.close();
            connection.close();
            throw new NotFoundException("Kein Kuenstler fuer das gegebene KuenstlerId");
        }
        String kuenstlerEmail = resultSet.getString(1);
        resultSet.close();
        int newGeneratedID = 1;
        try {
            preparedStatement = connection.prepareStatement("insert into Band_besteht_aus_Kuenstler(Email, BandID) VALUES (?, ?);", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setObject(1, kuenstlerEmail);
            preparedStatement.setObject(2, bandid);
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

    @Path("bands/{bandid}")
    @RolesAllowed({"Kuenstler"})
    @DELETE //curl -X DELETE "http://localhost:8080/bands/2" -H  "accept: */*" -u "joe8@web.de:00AB"
    public Response deleteBand(@PathParam("bandid") int bandid) throws SQLException{
        Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * from Band where rowid = ?;");
        preparedStatement.closeOnCompletion();
        preparedStatement.setObject(1, bandid);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (!resultSet.next()){
            resultSet.close();
            connection.close();
            throw new NotFoundException("Kein Band fuer das gegebene BandId");
        }
        int band = resultSet.getInt(1);
        resultSet.close();
        preparedStatement = connection.prepareStatement("SELECT * from Band_besteht_aus_Kuenstler where Email like ? AND BandID = ? ;");
        preparedStatement.setObject(1, securityContext.getUserPrincipal().getName());
        preparedStatement.setObject(2, band);
        resultSet = preparedStatement.executeQuery();
        if (!resultSet.next()){
            resultSet.close();
            connection.close();
            throw new ForbiddenException("Sie sind kein Mitglied dieser Band");
        }
        resultSet.close();
        String query = "delete from Band where rowid = '" + bandid + "' ;";
        preparedStatement = connection.prepareStatement(query);
        preparedStatement.executeUpdate();
        connection.close();
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}

