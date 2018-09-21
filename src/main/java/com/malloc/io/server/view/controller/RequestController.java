package com.malloc.io.server.view.controller;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.malloc.io.server.view.model.RandomStringBuilder;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("links")
public class RequestController {
    private static final RandomStringBuilder randomStringBuilder = new RandomStringBuilder(5);
    private static final String BASE_URL = "http://localhost:8081/";
    private static final Table LINKS_TABLE;
    static {
        final AmazonDynamoDB client =  AmazonDynamoDBClientBuilder.standard().build();
        final DynamoDB dynamoDB = new DynamoDB(client);
        LINKS_TABLE = dynamoDB.getTable("Links");
    }
    @GET
    // it means which type the server returns
    @Produces(MediaType.TEXT_PLAIN)
    // it means the path of the class + this path
    @Path("{id}")
    public Response getUrlById(final @PathParam("id") String id)
    {
        if(id == null || id.isEmpty())
        {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        final Item item = LINKS_TABLE.getItem("id", id);
        final String url = item.getString("url");
        if(url == null && url.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(url).build();
    }

    @PUT
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response newURL(final String url)
    {

        if(url == null || url.isEmpty())
        {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if(!url.startsWith("http"))
        {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        int attempt = 0;
        while (attempt < 5)
        {
            final String id = randomStringBuilder.nextString();
            final Item urlRecord = new Item().withPrimaryKey("id", id).withString("url", url);
            try {
                LINKS_TABLE.putItem(new PutItemSpec()
                        .withConditionExpression("attribute_not_exists(id)")
                        .withItem(urlRecord));
                return Response.ok(id).build();
            }
            catch (ConditionalCheckFailedException e) {

            }
            attempt++;
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
}
