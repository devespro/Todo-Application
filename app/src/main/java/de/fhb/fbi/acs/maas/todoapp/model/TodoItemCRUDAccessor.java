package de.fhb.fbi.acs.maas.todoapp.model;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;


@Path("/todoitems")
@Consumes({ "application/json" })
@Produces({ "application/json" })
public interface TodoItemCRUDAccessor {

    @GET
    List<TodoItem> readAllItems();

    @POST
    TodoItem createItem(TodoItem item);

    @DELETE
    @Path("/{itemId}")
    boolean deleteItem(@PathParam("itemId") long itemId);

    @PUT
    TodoItem updateItem(TodoItem item);
}