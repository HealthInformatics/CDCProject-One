package edu.gatech.cdcproject.backend.endpoint;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.api.users.User;
import com.googlecode.objectify.cmd.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.inject.Named;

import edu.gatech.cdcproject.backend.model.FoodImage;
import edu.gatech.cdcproject.backend.model.Response;

import static edu.gatech.cdcproject.backend.util.OfyService.*;
/**
 * Created by mkatri on 12/3/15.
 */


@Api(
        name = "myApi",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "backend.cdcproject.gatech.edu",
                ownerName = "backend.cdcproject.gatech.edu",
                packagePath=""
        )
)
public class ImageEndpoint {

    private static final Logger logger = Logger.getLogger(ImageEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 5;

    static {
        logger.setLevel(Level.INFO);
    }

    /**
    * @return the url of image to be stored
    * @throws OAuthRequestException
    **/
    @ApiMethod(
            name = "image.newImageUrl",
            path = "image/newImageUrl",
            httpMethod = ApiMethod.HttpMethod.GET)
    public Response newImageUrl() throws OAuthRequestException {

        BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
        String url = blobstoreService.createUploadUrl("/uploadImage");
        logger.info("Create new Image URL: " + url);
        Response response = new Response();
        response.setStringResponse(url);
        return response;
    }

    @ApiMethod(
            name = "image.insert",
            path = "image/insert",
            httpMethod = ApiMethod.HttpMethod.POST)
    public void insert(FoodImage image)
            throws BadRequestException {
        if (image == null) {
            logger.warning("Null food image");
            throw new BadRequestException("Null food image");
        }

        ofy().save().entity(image).now();

    }

    @ApiMethod(
            name = "image.list",
            path = "image/list",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<FoodImage> list(@Nullable @Named("cursor") String cursor,
                                                      @Nullable @Named("limit") Integer limit) {
        /**
         * When creating projection queries, the ID property should not be projected.
         * It is still included in the resulting entity, but including it in the
         * projections causes the query to turn up no results. The ID property is
         * not stored the same way as the other property fields in the entity.
         *
         * Also, projection requires index, so to prevent consumption of much resource
         * It is better not to use projection.
         */
        Query<FoodImage> query = ofy().load().type(FoodImage.class)
                .order("-created")
                .limit(limit == null ? DEFAULT_LIST_LIMIT : limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<FoodImage> queryIterator = query.iterator();
        List<FoodImage> images = new ArrayList<>();
        while (queryIterator.hasNext()) {
            images.add(queryIterator.next());
        }
        return CollectionResponse.<FoodImage>builder().setItems
                (images).setNextPageToken(queryIterator.getCursor()
                .toWebSafeString()).build();
    }
}
