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

public class PlaylistController {

    @Inject
    private DataSource dataSource;

    @Context
    private SecurityContext securityContext;

    @Context
    private UriInfo uriInfo;

    @Path("playlists")
    @GET //curl -X GET "http://localhost:8080/playlists" -H  "accept: application/json;charset=UTF-8"
    public List<Map<String, Object>> getPlaylists(@QueryParam("ist_privat") Boolean ist_privat,
                                                  @QueryParam("bezeichnung") String bezeichnung) throws SQLException {
        String query = "SELECT * FROM Playlist WHERE ID > 0 ";

        if (ist_privat != null) {
            query = query + " AND oeffentlich = 0 ";
        }
        if (bezeichnung != null) {
            query = query + " AND Playlist.Bezeichnung like '" + bezeichnung + "' ";
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
            entity.put("playlistid", resultSet.getObject(1));
            entity.put("bezeichnung", resultSet.getObject(2));
            entity.put("coverbild", resultSet.getObject(3));
            entity.put("ist_privat", resultSet.getObject(4));
            entities.add(entity);
        }
        resultSet.close();
        connection.close();
        return entities;
    }

    @Path("playlists")
    @RolesAllowed("Premiumnutzer")
    @POST // curl -X POST "http://localhost:8080/playlists" -H  "accept: */*" -H "Content-Type: multipart/form-data" -F "bezeichnung=P6" -F "ist_privat=false" -F "coverbild=c.png" -u "joe2@web.de:00AB"
    public Response addPlaylist(@FormDataParam("bezeichnung") String bezeichnung,
                                @FormDataParam("ist_privat") Boolean ist_privat,
                                @FormDataParam("coverbild") String  coverbild) throws SQLException {
        if (bezeichnung == null || ist_privat == null || coverbild == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        Connection connection = dataSource.getConnection();
        int newGeneratedID = 0;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("insert into Playlist(Bezeichnung,Coverbild,oeffentlich,Email)values (?,?,?,?);", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, bezeichnung);
            preparedStatement.setString(2, coverbild);
            preparedStatement.setObject(3, ist_privat ? 0:1);
            preparedStatement.setString(4, securityContext.getUserPrincipal().getName());
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
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

    @Path("playlist/{playlistid}/titel")
    @RolesAllowed({"Premiumnutzer"})
    @POST  //curl -X POST "http://localhost:8080/playlist/3/titel" -H "accept: */*" -H "Content-Type: multipart/form-data" -F "titelid=4" -u "joe3@web.de:00AB"
    public Response addKuenstlerZuBand(@PathParam("playlistid") Integer playlistid,
                                       @FormDataParam("titelid") Integer titelid) throws SQLException{
        if (playlistid == null || titelid == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * from Playlist where rowid = ?;");
        preparedStatement.closeOnCompletion();
        preparedStatement.setObject(1, playlistid);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (!resultSet.next()){
            resultSet.close();
            connection.close();
            throw new NotFoundException("Kein Playlist fuer das gegebene PlaylistId");
        }
        resultSet.close();

        preparedStatement = connection.prepareStatement("SELECT * from Playlist where rowid = ?  and Email like ?;");
        preparedStatement.closeOnCompletion();
        preparedStatement.setObject(1, playlistid);
        preparedStatement.setObject(2, securityContext.getUserPrincipal().getName());
        resultSet = preparedStatement.executeQuery();
        if (!resultSet.next()){
            resultSet.close();
            connection.close();
            throw new ForbiddenException("Das ist nicht Ihr Playlist");
        }
        resultSet.close();

        preparedStatement = connection.prepareStatement("SELECT * from Titel where rowid = ?;");
        preparedStatement.closeOnCompletion();
        preparedStatement.setObject(1, titelid);
        resultSet = preparedStatement.executeQuery();
        if (!resultSet.next()){
            resultSet.close();
            connection.close();
            throw new NotFoundException("Kein Titel fuer das gegebene TitelId");
        }
        resultSet.close();
        int newGeneratedID = 1;
        try {
            preparedStatement = connection.prepareStatement("insert into Titel_gehoert_zu_Playlist(TitelID, PlaylistID) VALUES (?, ?);", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setObject(1, titelid);
            preparedStatement.setObject(2, playlistid);
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
