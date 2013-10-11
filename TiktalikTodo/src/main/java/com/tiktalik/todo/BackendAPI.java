    package com.tiktalik.todo;

import java.util.ArrayList;

import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;

/**
 * Created by alek on 10/10/13.
 */
public interface BackendAPI {
    @POST("/api/item")
    Response addItem(@Body Item item);

    @GET("/api/item")
    ArrayList<Item> getItems();
}
