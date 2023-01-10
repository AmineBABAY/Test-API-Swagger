package com.arender.tests;

import static org.testng.Assert.assertTrue;


import org.testng.annotations.Test;
import com.arender.actions.AssertActions;
import com.arender.endpoint.Documents;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;


public class LayoutTest extends AssertActions{
	
	String docxDocumentId="";
	String zipDocumentId="";
	String txtDocumentId="";
	String pdfDocumentId="";
	String tiffDocumentId="";
	String pngDocumentId="";
	String jpegDocumentId="";
	
	@Test(priority=1)
	public void  uploadDocument()
	{	
        Response response = Documents.uploadDocument("docx");
        JsonPath jsonPath = JsonPath.from(response.asString());
        verifyStatusCode(response, 200);
        docxDocumentId = jsonPath.get("id");
        
        Response responseZip = Documents.uploadDocument("zip");
        JsonPath jsonPathZip = JsonPath.from(responseZip.asString());
        verifyStatusCode(responseZip, 200);       
        zipDocumentId = jsonPathZip.get("id");

        Response responsePDF = Documents.uploadDocument("pdf");        
        JsonPath jsonPathPDF = JsonPath.from(responsePDF.asString());
        verifyStatusCode(responsePDF, 200);
        pdfDocumentId = jsonPathPDF.get("id");
        
        Response responseTXT = Documents.uploadDocument("txt");
        JsonPath jsonPathTXT = JsonPath.from(responseTXT.asString());
        verifyStatusCode(responseTXT, 200);      
        txtDocumentId = jsonPathTXT.get("id");
        
        Response responseTIFF = Documents.uploadDocument("tiff");
        JsonPath jsonPathTIFF = JsonPath.from(responseTIFF.asString());
        verifyStatusCode(responseTIFF, 200);      
        tiffDocumentId = jsonPathTIFF.get("id");
        
        Response responsePNG = Documents.uploadDocument("png");
        JsonPath jsonPathPNG = JsonPath.from(responseTXT.asString());
        verifyStatusCode(responsePNG, 200);      
        pngDocumentId = jsonPathPNG.get("id");
        
        Response responseJPEG = Documents.uploadDocument("jpeg");
        JsonPath jsonPathJPEG = JsonPath.from(responseTXT.asString());
        verifyStatusCode(responseJPEG, 200);      
        jpegDocumentId = jsonPathJPEG.get("id");
	}
	@Test(priority=2) 
	public void getDocumentLayout() {
		Response response=Documents.getDocumentLayout(docxDocumentId);
		
		verifyStatusCode(response, 200);
		JsonPath jsonPath = JsonPath.from (response.asString ());
		assertTrue(jsonPath.get("type").toString().contains("DocumentPageLayout"));
		

	}
	@Test(priority=3) 
	public void getDocumentLayoutForZip () {
        Response response=Documents.getDocumentLayout(zipDocumentId);
        
        verifyStatusCode(response, 200);
        JsonPath jsonPath = JsonPath.from (response.asString ());
        assertTrue(jsonPath.get("children.children.documentId.id").toString().contains("/1/1"));
        assertTrue(jsonPath.get("type").toString().contains("DocumentContainer"));
	}
	
	   @Test(priority=4) 
	    public void checkDPILayoutForPDF () {
	        Response response=Documents.getDocumentLayout(pdfDocumentId);	        
	        verifyStatusCode(response, 200);
	        JsonPath jsonPath = JsonPath.from (response.asString ());
	        assertTrue(jsonPath.get("pageDimensionsList.dpi").toString()!=null);
	    }
       @Test(priority=4) 
       public void checkDPILayoutForTIFF () {
           Response response=Documents.getDocumentLayout(tiffDocumentId);           
           verifyStatusCode(response, 200);
           JsonPath jsonPath = JsonPath.from (response.asString ());
           assertTrue(jsonPath.get("pageDimensionsList.dpi").toString().equals("[300]"));
       }
       @Test(priority=4) 
       public void checkDPILayoutForDOCX () {
           Response response=Documents.getDocumentLayout(docxDocumentId);           
           verifyStatusCode(response, 200);
           JsonPath jsonPath = JsonPath.from (response.asString ());
           assertTrue(jsonPath.get("pageDimensionsList.dpi").toString()!=null);
       }
       @Test(priority=4) 
       public void checkDPILayoutForPNG () {
           Response response=Documents.getDocumentLayout(pngDocumentId);           
           verifyStatusCode(response, 200);
           JsonPath jsonPath = JsonPath.from (response.asString ());
           assertTrue(jsonPath.get("pageDimensionsList.dpi").toString()!=null);
       }
       @Test(priority=4) 
       public void checkDPILayoutForJPEG () {
           Response response=Documents.getDocumentLayout(jpegDocumentId);           
           verifyStatusCode(response, 200);
           JsonPath jsonPath = JsonPath.from (response.asString ());
           assertTrue(jsonPath.get("pageDimensionsList.dpi").toString()!=null);
       }
       @Test(priority=4) 
       public void checkDPILayoutForTXT () {
           Response response=Documents.getDocumentLayout(txtDocumentId);           
           verifyStatusCode(response, 200);
           JsonPath jsonPath = JsonPath.from (response.asString ());
           assertTrue(jsonPath.get("pageDimensionsList.dpi").toString()!=null);
       }

}
