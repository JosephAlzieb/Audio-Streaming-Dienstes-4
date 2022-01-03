package de.hhu.cs.dbs.propra.presentation.rest;

import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.sql.DataSource;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Path("/")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)
public class KommentarController {


    @Inject
    private DataSource dataSource;

    @Context
    private SecurityContext securityContext;

    @Context
    private UriInfo uriInfo;
    @Path("kommentare/{kommentarid}")
    @RolesAllowed({"Nutzer"})
    @PATCH //curl -X PATCH "http://localhost:8080/kommentare/2" -H  "accept: */*" -H "Content-Type: multipart/form-data" -F "text=neuesKommentar" -u "joe@web.de:00AB"
    public Response updateKommentar(@PathParam("kommentarid") Integer kommentarid,
                                   @FormDataParam("text") String text) throws SQLException {
        if (kommentarid == null || text == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * from Nutzer_kommentiert_Titel where rowid = ?;");
        preparedStatement.closeOnCompletion();
        preparedStatement.setObject(1, kommentarid);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (!resultSet.next()){
            resultSet.close();
            connection.close();
            throw new NotFoundException("kein Kommentar fuer das gegebene KommentarId");
        }
        resultSet.close();
        preparedStatement = connection.prepareStatement("SELECT * from Nutzer_kommentiert_Titel where ROWID = ? and Email like ?;");
        preparedStatement.setObject(1, kommentarid);
        preparedStatement.setObject(2, securityContext.getUserPrincipal().getName());
        resultSet = preparedStatement.executeQuery();
        if (!resultSet.next()){
            resultSet.close();
            connection.close();
            throw new ForbiddenException("Das ist nicht Ihr Kommentar, und somit Sie koennen das nicht aendern");
        }
        try {
            resultSet.close();
            preparedStatement = connection.prepareStatement("update Nutzer_kommentiert_Titel set Text = ? where rowid = ? and Email = ?;");
            preparedStatement.setObject(1, text);
            preparedStatement.setObject(2, kommentarid);
            preparedStatement.setObject(3, securityContext.getUserPrincipal().getName());
            preparedStatement.executeUpdate();
            connection.close();
        }catch (SQLException exception){
            connection.close();
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
