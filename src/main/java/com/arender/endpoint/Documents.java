package com.arender.endpoint;

import java.io.File;
import com.arender.utlis.Initialization;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class Documents extends Initialization {

	public static Response uploadDocument(String file) {
		String filepath = System.getProperty("user.dir") + prop.getProperty(file);
		File f = new File(filepath);
		RestAssured.baseURI = url;
		return RestAssured.given().contentType(ContentType.BINARY).body(f).post("/documents");
	}

	public static Response getDocument(String id) {
		RestAssured.baseURI = url;
		return RestAssured.given().contentType("application/json").when().get("/documents/" + id);
	}

	public static Response getDocumentContent(String idDocument, String format) {
		RestAssured.baseURI = url;
		return RestAssured.given().queryParam("format", format).when().get("/documents/" + idDocument + "/file");

	}

	public static Response getDocumentLayout(String idDocument) {
		RestAssured.baseURI = url;
		return RestAssured.given().when().get("/documents/" + idDocument + "/layout");

	}

	public static Response getPrintableDocument(String id) {
		RestAssured.baseURI = url;
		return RestAssured.given().contentType("application/json").when().get("/documents/" + id + "/printable");
	}

	public static Response getBookmarks(String id) {
		RestAssured.baseURI = url;
		return RestAssured.given().contentType("application/json").when().get("/documents/" + id + "/bookmarks");
	}

	public static Response getNameDestination(String id) {
		RestAssured.baseURI = url;
		return RestAssured.given().contentType("application/json").when().get("/documents/" + id + "/destinations");
	}

	public static Response searchPageForTextPositions(String id, String text, int page) {
		RestAssured.baseURI = url;
		return RestAssured.given().contentType("application/json")

				.queryParam("searchText", text).when().get("/documents/" + id + "/pages/" + page + "/text");
	}

	public static Response getTextPosition(String id, int page) {
		RestAssured.baseURI = url;
		return RestAssured.given().contentType("application/json")

				.when().get("/documents/" + id + "/pages/" + page + "/text/position");
	}

	public static Response getPagesSearchResult(String id, String text) {
		RestAssured.baseURI = url;
		return RestAssured.given().contentType("application/json")

				.queryParam("searchText", text).when().get("/documents/" + id + "/pages/");
	}

	public static Response getSignatures(String id) {
		RestAssured.baseURI = url;
		return RestAssured.given().contentType("application/json").when().get("/documents/" + id + "/signatures");
	}

	public static Response getPageImage(String id, int numPage, String pageImageDescription) {
		RestAssured.baseURI = url;
		return RestAssured.given().contentType("application/json")

				.queryParam("pageImageDescription", pageImageDescription).when()
				.get("/documents/" + id + "/pages/" + numPage + "/image");
	}

	public static Response getPageImage(String id, int numPage) {
		RestAssured.baseURI = url;
		return RestAssured.given().contentType("application/json")

				.when().get("/documents/" + id + "/pages/" + numPage + "/image");
	}

	public static Response evictDocument(String id) {
		
		RestAssured.baseURI = url;
		
		return RestAssured.given().when().delete("/documents/" + id);
	}

}
