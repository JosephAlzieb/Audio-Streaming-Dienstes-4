package de.hhu.cs.dbs.propra.presentation.rest;

import javax.inject.Inject;
import javax.sql.DataSource;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)
public class GenreController {
    @Inject
    private DataSource dataSource;

    @Context
    private SecurityContext securityContext;

    @Context
    private UriInfo uriInfo;

    @Path("genres")
    @GET //curl -X GET "http://localhost:8080/genres" -H  "accept: application/json;charset=UTF-8"
    public List<Map<String, Object>> getGenres(@QueryParam("bezeichnung") String bezeichnung) throws SQLException {
        String query = "SELECT * FROM Genre";
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
            entity.put("genreid", resultSet.getRow());
            entity.put("bezeichnung", resultSet.getString(1));
            entities.add(entity);
        }
        resultSet.close();
        connection.close();
        return entities;
    }
}
