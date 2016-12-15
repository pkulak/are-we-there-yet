package com.pkulak.webhooks.duration;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.io.Resources;
import com.pkulak.webhooks.IntegrationTest;
import org.testng.annotations.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.nio.charset.Charset;
import java.time.*;
import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class DurationResourceTest extends IntegrationTest {
    @Test
    public void fullDateTimesPast() throws Exception {
        testInput(
                "date-time",
                Instant.now().minus(Duration.ofDays(3)).toString(),
                "That was 3 days 1 minute ago.");
    }

    @Test
    public void fullDateTimesFuture() throws Exception {
        testInput(
                "date-time",
                Instant.now().plus(Duration.ofDays(3)).toString(),
                "That will be 2 days 23 hours from now.");
    }

    @Test
    public void dateOnly() throws Exception {
        Response resp = getResponse("date", LocalDate.now().plusDays(3).toString());
        String speech = resp.readEntity(ObjectNode.class).get("speech").asText();

        assertTrue(speech.startsWith("That will be 2 days"));
    }

    @Test
    public void timeOnly() throws Exception {
        testInput(
                "time",
                LocalTime.now().plusHours(2).toString(),
                "That will be 1 hour 59 minutes from now.");
    }

    private Response getResponse(String parameterName, String parameter) throws Exception {
        String payload = Resources.toString(
                Resources.getResource(getClass(),"payload.json"),
                Charset.defaultCharset());

        // set the parameter
        for (String s : Arrays.asList("date", "date-time", "time")) {
            if (parameterName.equals(s)) {
                payload = payload.replace("{{" + s + "}}", parameter);
            } else {
                payload = payload.replace("{{" + s + "}}", "");
            }
        }

        return target().path("/duration").request()
                .post(Entity.entity(payload, MediaType.APPLICATION_JSON));
    }

    private void testInput(String parameterName, String parameter, String expectedResponse) throws Exception {
        Response resp = getResponse(parameterName, parameter);

        // and make sure the speech lines up
        assertEquals(resp.readEntity(ObjectNode.class).get("speech").asText(), expectedResponse);
    }
}
