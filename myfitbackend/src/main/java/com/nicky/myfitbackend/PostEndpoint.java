package com.nicky.myfitbackend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.inject.Named;

import static com.nicky.myfitbackend.OfyService.ofy;

/**
 * WARNING: This generated code is intended as a sample or starting point for using a
 * Google Cloud Endpoints RESTful API with an Objectify entity. It provides no data access
 * restrictions and no data validation.
 * <p/>
 * DO NOT deploy this code unchanged as part of a real application to real users.
 */
@Api(
        name = "postEndpoint",
        version = "v1",
        resource = "post",
        namespace = @ApiNamespace(
                ownerDomain = "myfitbackend.nicky.com",
                ownerName = "myfitbackend.nicky.com",
                packagePath = ""
        )
)
public class PostEndpoint {

    private static final Logger logger = Logger.getLogger(PostEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    /**
     * Returns the {@link Post} with the corresponding ID.
     *
     * @param id the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code Post} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "post/{id}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public Post get(@Named("id") Long id) throws NotFoundException {
        logger.info("Getting Post with ID: " + id);
        Post post = ofy().load().type(Post.class).id(id).now();
        if (post == null) {
            throw new NotFoundException("Could not find Post with ID: " + id);
        }
        return post;
    }

    /**
     * Inserts a new {@code Post}.
     */
    @ApiMethod(
            name = "insert",
            path = "post",
            httpMethod = ApiMethod.HttpMethod.POST)
    public Post insert(Post post) {
        ofy().save().entity(post).now();
        logger.info("Created Post with ID: " + post.getId());

        return ofy().load().entity(post).now();
    }

    /**
     * Updates an existing {@code Post}.
     *
     * @param id   the ID of the entity to be updated
     * @param post the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code Post}
     */
    @ApiMethod(
            name = "update",
            path = "post/{id}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public Post update(@Named("id") Long id, Post post) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(id);
        ofy().save().entity(post).now();
        logger.info("Updated Post: " + post);
        return ofy().load().entity(post).now();
    }


    /*@ApiMethod(name="getAllPosts")
    public CollectionResponse<Post> getAllPosts() {
        List<Post> posts = ofy().load().type(Post.class).list();
        return CollectionResponse.<Post>builder().setItems(posts).build();
    }*/

    /**
     * Deletes the specified {@code Post}.
     *
     * @param id the ID of the entity to delete
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code Post}
     */
    @ApiMethod(
            name = "remove",
            path = "post/{id}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("id") Long id) throws NotFoundException {
        checkExists(id);
        ofy().delete().type(Post.class).id(id).now();
        logger.info("Deleted Post with ID: " + id);
    }

    /**
     * List all entities.
     *
     * @param cursor used for pagination to determine which page to return
     * @param limit  the maximum number of entries to return
     * @return a response that encapsulates the result list and the next page token/cursor */

    @ApiMethod(
            name = "list",
            path = "post",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<Post> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<Post> query = ofy().load().type(Post.class).order("-timeInMillis").limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<Post> queryIterator = query.iterator();
        List<Post> postList = new ArrayList<Post>(limit);
        while (queryIterator.hasNext()) {
            postList.add(queryIterator.next());
        }
        return CollectionResponse.<Post>builder().setItems(postList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private void checkExists(Long id) throws NotFoundException {
        try {
            ofy().load().type(Post.class).id(id).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find Post with ID: " + id);
        }
    }
}