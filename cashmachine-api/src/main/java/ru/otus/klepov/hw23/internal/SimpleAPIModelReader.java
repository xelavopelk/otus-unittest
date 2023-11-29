package ru.otus.klepov.hw23.internal;

import org.eclipse.microprofile.openapi.OASFactory;
import org.eclipse.microprofile.openapi.OASModelReader;
import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.eclipse.microprofile.openapi.models.PathItem;
import org.eclipse.microprofile.openapi.models.Paths;

/**
 * Defines two paths using the OpenAPI model reader mechanism, one that should
 * be suppressed by the filter class and one that should appear in the published
 * OpenAPI document.
 */
public class SimpleAPIModelReader implements OASModelReader {

    /**
     * Path for the example endpoint added by this model reader that should be visible.
     */
    public static final String MODEL_READER_PATH = "/test/newpath";

    /**
     * Path for an endpoint that the filter should hide.
     */
    public static final String DOOMED_PATH = "/test/doomed";

    /**
     * ID for an endpoint that the filter should hide.
     */
    public static final String DOOMED_OPERATION_ID = "doomedPath";

    /**
     * Summary text for the endpoint.
     */
    public static final String SUMMARY = "A sample test endpoint from ModelReader";

    @Override
    public OpenAPI buildModel() {
        /*
         * Add two path items, one of which we expect to be removed by
         * the filter and a very simple one that will appear in the
         * published OpenAPI document.
         */
        /*PathItem newPathItem = OASFactory.createPathItem()
                .GET(OASFactory.createOperation()
                    .operationId("newPath")
                    .summary(SUMMARY));
        PathItem doomedPathItem = OASFactory.createPathItem()
                .GET(OASFactory.createOperation()
                    .operationId(DOOMED_OPERATION_ID)
                    .summary("This should become invisible"));*/
        OpenAPI openAPI = OASFactory.createOpenAPI();
        /*Paths paths = OASFactory.createPaths()
                .addPathItem(MODEL_READER_PATH, newPathItem)
                .addPathItem(DOOMED_PATH, doomedPathItem);
        openAPI.paths(paths);*/

        return openAPI;
    }
}
