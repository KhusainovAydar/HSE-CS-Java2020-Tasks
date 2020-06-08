package ru.hse.cs.java2020.task03.startrek;

import java.io.IOException;
import java.rmi.UnexpectedException;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.httpclient.ProtocolException;

public interface Startrek {

    Issue createTask(String oauthToken, String orgId, String uid, String queueId, String name, String description,
                     Boolean selfTask) throws JsonProcessingException, ProtocolException, UnexpectedException;

    Issue watchTask(String oauthToken, String orgId, String keyOfTask) throws IOException, UnknownError;

    List<Issue> searchTask(String oauthToken, String orgId, Integer perPage, Integer page)
            throws ProtocolException, UnexpectedException, JsonProcessingException;

    Integer countIssues(String oauthToken, String orgId)
            throws ProtocolException, UnexpectedException, JsonProcessingException;

    List<Queue> searchQueue(String oauthToken, String orgId)
            throws UnexpectedException, ProtocolException, JsonProcessingException;

    Person personInfo(String oauthToken, String orgId) throws
            UnexpectedException, ProtocolException, JsonProcessingException;

    List<Comment> getComments(String oauthToken, String orgId, String issueKey)
            throws UnexpectedException, ProtocolException, JsonProcessingException;

    Comment postComment(String oauthToken, String orgId, String issueKey, String text)
            throws UnexpectedException, ProtocolException, JsonProcessingException;
}
