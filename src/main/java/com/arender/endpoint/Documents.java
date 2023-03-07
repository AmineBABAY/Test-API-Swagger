package com.arender.endpoint;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.arender.utlis.Initialization;

import io.restassured.RestAssured;
import io.restassured.config.ConnectionConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class Documents extends Initialization
{

    static
    {
        RestAssured.baseURI = url;
        ConnectionConfig connectionConfig = new ConnectionConfig(
                new ConnectionConfig.CloseIdleConnectionConfig(0, TimeUnit.NANOSECONDS));
        // RestAssured.config()
        // .httpClient(HttpClientConfig.httpClientConfig()
        // .setParam("http.connection.timeout", 10000) // set connection timeout
        // to 10 seconds
        // .setParam("http.socket.timeout", 60000) // set socket timeout to 60
        // seconds
        // .setParam("http.connection-manager.timeout", 100000));
        RestAssured.config().connectionConfig(connectionConfig);

    }

    public static Response uploadDocument(File file)
    {
        return RestAssured.given().contentType(ContentType.BINARY).body(file).post("/documents");
    }

    public static Response uploadDocument(File file, String name)
    {
        return RestAssured.given().queryParam("documentTitle", name + ": " + UUID.randomUUID().toString())
                .contentType(ContentType.BINARY).body(file).post("/documents");
    }

    public static Response uploadDocument(String file)
    {
        String filepath = System.getProperty("user.dir") + prop.getProperty(file);
        File f = new File(filepath);
        return RestAssured.given().contentType(ContentType.BINARY).body(f).post("/documents");
    }

    public static Response checkDocument(String id)
    {
        return RestAssured.given().contentType("application/json").when().get("/documents/" + id + "/check");
    }

    public static Response getDocument(String id)
    {
        return RestAssured.given().contentType("application/json").when().get("/documents/" + id);
    }

    public static Response getDocumentContent(String idDocument, String format)
    {
        return RestAssured.given().queryParam("format", format).when().get("/documents/" + idDocument + "/file");

    }

    public static Response getDocumentLayout(String idDocument)
    {
        return RestAssured.given().when().get("/documents/" + idDocument + "/layout");

    }

    public static Response getPrintableDocument(String id)
    {
        RestAssured.baseURI = url;
        return RestAssured.given().contentType("application/json").when().get("/documents/" + id + "/printable");
    }

    public static Response getBookmarks(String id)
    {
        return RestAssured.given().contentType("application/json").when().get("/documents/" + id + "/bookmarks");
    }

    public static Response getNameDestination(String id)
    {
        return RestAssured.given().contentType("application/json").when().get("/documents/" + id + "/destinations");
    }

    public static Response searchPageForTextPositions(String id, String text, int page)
    {
        return RestAssured.given().contentType("application/json")

                .queryParam("searchText", text).when().get("/documents/" + id + "/pages/" + page + "/text");
    }

    public static Response getTextPosition(String id, int page)
    {
        return RestAssured.given().contentType("application/json")

                .when().get("/documents/" + id + "/pages/" + page + "/text/position");
    }

    public static Response getPagesSearchResult(String id, String text)
    {
        return RestAssured.given().contentType("application/json")

                .queryParam("searchText", text).when().get("/documents/" + id + "/pages/");
    }

    public static Response getSignatures(String id)
    {
        return RestAssured.given().contentType("application/json").when().get("/documents/" + id + "/signatures");
    }

    public static Response getPageImage(String id, int numPage, String pageImageDescription)
    {
        return RestAssured.given().contentType("application/json")

                .queryParam("pageImageDescription", pageImageDescription).when()
                .get("/documents/" + id + "/pages/" + numPage + "/image");
    }

    public static Response getPageImage(String id, int numPage)
    {
        return RestAssured.given().contentType("application/json")

                .when().get("/documents/" + id + "/pages/" + numPage + "/image");
    }

    public static Response evictDocument(String id)
    {

        return RestAssured.given().when().delete("/documents/" + id);
    }

    public static Response getDocumentAnnotations(String id)
    {

        return RestAssured.given().when().get("/documents/" + id + "/file/annotations");
    }

}
