package ru.hse.cs.java2020.task03.startrek;

import java.io.IOException;
import java.rmi.UnexpectedException;
import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.ProtocolException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;
import ru.hse.cs.java2020.task03.startrek.exceptions.AuthenticationException;
import ru.hse.cs.java2020.task03.startrek.exceptions.ConflictException;
import ru.hse.cs.java2020.task03.startrek.exceptions.NotFoundException;
import ru.hse.cs.java2020.task03.startrek.exceptions.PermissionException;
import ru.hse.cs.java2020.task03.util.Response;


public class StartrekClient implements Startrek {

    private String stHost;
    private String apiVersion;
    private static final Logger LOGGER = Logger.getLogger(StartrekClient.class.getName());
    private static final DefaultHttpMethodRetryHandler RETRY_HANDLER = new DefaultHttpMethodRetryHandler(3, false);

    private HttpClient httpClient = new HttpClient();

    public StartrekClient() {
        stHost = "api.tracker.yandex.net";
        apiVersion = "v2";
    }

    public StartrekClient(String stHost, String apiVersion) {
        this.stHost = stHost;
        this.apiVersion = apiVersion;
    }

    private String getApiUrl(String query) {
        return String.format("https://%s/%s/%s", stHost, apiVersion, query);
    }

    private Response executeMethod(HttpMethod method) {
        try {
            int statusCode = httpClient.executeMethod(method);
            return new Response(method.getResponseBodyAsString(), statusCode);
        } catch (HttpException e) {
            LOGGER.log(Level.WARNING, "Fatal protocol violation: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Fatal transport error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            method.releaseConnection();
        }
        throw new UnknownError("HttpException or IOException");
    }

    private void checkStatusCode(Integer statusCode) throws ProtocolException, UnexpectedException {
        switch (statusCode) {
            case HttpStatus.SC_OK:
            case HttpStatus.SC_CREATED:
                break;
            case HttpStatus.SC_UNAUTHORIZED:
                throw new AuthenticationException("Invalid authorization");
            case HttpStatus.SC_FORBIDDEN:
                throw new PermissionException("Permission denied");
            case HttpStatus.SC_NOT_FOUND:
                throw new NotFoundException("Issue not found 404");
            case HttpStatus.SC_CONFLICT:
                throw new ConflictException("Conflict exception");
            default:
                throw new UnexpectedException("Unexpected value: " + statusCode);
        }
    }

    private void addHeadersToMethod(HttpMethod method, List<Header> headers) {
        for (Header header : headers) {
            method.addRequestHeader(header);
        }
    }

    private List<Header> getListOfHeaders(String oauthToken, String orgId) {
        List<Header> result = new ArrayList<>();
        result.add(new Header("Authorization", "OAuth " + oauthToken));
        result.add(new Header("X-Org-Id", orgId));
        return result;
    }

    private String getMethod(String apiUrl, List<Header> headers)
            throws UnexpectedException, ProtocolException {
        GetMethod method = new GetMethod(apiUrl);
        addHeadersToMethod(method, headers);
        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, RETRY_HANDLER);

        Response response = executeMethod(method);
        checkStatusCode(response.getStatusCode());

        return response.getContent();
    }

    private String postMethod(String apiUrl, List<Header> headers, JSONObject requstParams)
            throws UnexpectedException, ProtocolException {
        PostMethod method = new PostMethod(apiUrl);
        addHeadersToMethod(method, headers);

        method.setRequestEntity(new StringRequestEntity(requstParams.toString()));
        Response response = executeMethod(method);
        checkStatusCode(response.getStatusCode());

        return response.getContent();
    }

    @Override
    public Issue createTask(String oauthToken, String orgId, String uid, String queueId,
                            String name, String description, Boolean selfTask)
            throws JsonProcessingException, ProtocolException, UnexpectedException {
        List<Header> headers = getListOfHeaders(oauthToken, orgId);
        String apiUrl = getApiUrl("issues/");
        JSONObject requestParams = new JSONObject();
        requestParams.put("summary", name);
        requestParams.put("queue", new JSONObject().put("id", queueId));
        requestParams.put("description", description);
        if (selfTask) {
            requestParams.put("assignee", new JSONObject().put("id", uid));
        }

        String response = postMethod(apiUrl, headers, requestParams);
        return new ObjectMapper().readValue(response, Issue.class);
    }

    @Override
    public Issue watchTask(String oauthToken, String orgId, String keyOfTask) throws IOException, UnknownError {
        List<Header> headers = getListOfHeaders(oauthToken, orgId);
        String apiUrl = getApiUrl("issues/" + keyOfTask);

        String response = getMethod(apiUrl, headers);
        return new ObjectMapper().readValue(response, Issue.class);
    }

    @Override
    public List<Issue> searchTask(String oauthToken, String orgId, Integer perPage, Integer page)
            throws ProtocolException, UnexpectedException, JsonProcessingException {
        List<Header> headers = getListOfHeaders(oauthToken, orgId);
        String apiUrl = getApiUrl(String.format("issues/_search?perPage=%d&page=%d", perPage, page));

        JSONObject requestParams = new JSONObject();
        requestParams.put("filter", new JSONObject().put("assignee", "me()"));
        requestParams.put("order", "-created");

        String response = postMethod(apiUrl, headers, requestParams);
        return new ObjectMapper().readValue(response, new TypeReference<List<Issue>>() {
        });
    }

    @Override
    public Integer countIssues(String oauthToken, String orgId)
            throws ProtocolException, UnexpectedException, JsonProcessingException {
        List<Header> headers = getListOfHeaders(oauthToken, orgId);
        String apiUrl = getApiUrl("issues/_count?");

        JSONObject requstParams = new JSONObject();
        requstParams.put("filter", new JSONObject().put("assignee", "me()"));

        String response = postMethod(apiUrl, headers, requstParams);
        return new ObjectMapper().readValue(response, Integer.class);
    }

    @Override
    public List<Queue> searchQueue(String oauthToken, String orgId)
            throws UnexpectedException, ProtocolException, JsonProcessingException {
        List<Header> headers = getListOfHeaders(oauthToken, orgId);
        String apiUrl = getApiUrl("queues/");

        String response = getMethod(apiUrl, headers);
        return new ObjectMapper().readValue(response, new TypeReference<List<Queue>>() {
        });
    }

    @Override
    public Person personInfo(String oauthToken, String orgId) throws
            UnexpectedException, ProtocolException, JsonProcessingException {
        List<Header> headers = getListOfHeaders(oauthToken, orgId);
        String apiUrl = getApiUrl("myself/");

        String response = getMethod(apiUrl, headers);
        return new ObjectMapper().readValue(response, Person.class);
    }

}
