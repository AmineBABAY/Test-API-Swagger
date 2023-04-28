package com.arender.actions;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestClientBuilder;
import org.opensearch.client.base.RestClientTransport;
import org.opensearch.client.base.Transport;
import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._global.IndexRequest;
import org.opensearch.client.opensearch._global.SearchResponse;
import org.opensearch.client.opensearch.indices.CreateRequest;
import org.opensearch.client.opensearch.indices.IndexSettings;
import org.opensearch.client.opensearch.indices.PutSettingsRequest;
import org.opensearch.client.opensearch.indices.put_settings.IndexSettingsBody;

public class SendDataToOpenSearch
{
    public static void SendData(List<Result> resultList)
    {
        RestClient restClient = null;
        try
        {
            System.setProperty("javax.net.ssl.trustStore",
                    "C:/Users/ilyes.fatfouti_arond/Downloads/openSearch/opensearch-2.6.0/config/trustStore");
            System.setProperty("javax.net.ssl.trustStorePassword", "opensearch");

            // Only for demo purposes. Don't specify your credentials in code.
            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("admin", "admin"));

            // Initialize the client with SSL and TLS enabled
            restClient = RestClient.builder(new HttpHost("localhost", 9200, "https"))
                    .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback()
                    {
                        @Override
                        public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder)
                        {
                            return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                        }
                    }).build();
            Transport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
            OpenSearchClient client = new OpenSearchClient(transport);

            // Create the index
            String index = "sample-index";
            CreateRequest createIndexRequest = new CreateRequest.Builder().index(index).build();
            client.indices().create(createIndexRequest);

            // Add some settings to the index
            IndexSettings indexSettings = new IndexSettings.Builder().autoExpandReplicas("0-all").build();
            IndexSettingsBody settingsBody = new IndexSettingsBody.Builder().settings(indexSettings).build();
            PutSettingsRequest putSettingsRequest = new PutSettingsRequest.Builder().index(index).value(settingsBody)
                    .build();
            client.indices().putSettings(putSettingsRequest);

            // Index some data
            indexData(resultList, client);

            // Search for the document
            SearchResponse<IndexData> searchResponse = client.search(s -> s.index(index), IndexData.class);
            for (int i = 0; i < searchResponse.hits().hits().size(); i++)
            {
                System.out.println(searchResponse.hits().hits().get(i).source());
            }

        }
        catch (IOException e)
        {
            System.out.println(e.toString());
        }
        finally
        {
            try
            {
                if (restClient != null)
                {
                    restClient.close();
                }
            }
            catch (IOException e)
            {
                System.out.println(e.toString());
            }
        }
    }

    private static void indexData(List<Result> resultList, OpenSearchClient client) throws IOException
    {
        for (Result res : resultList)
        {
            IndexRequest<Result> indexRequest = new IndexRequest.Builder<Result>().index(res.getName())
                    .id("ID" + res.getName()).value(res).build();
            client.index(indexRequest);
        }

    }

}