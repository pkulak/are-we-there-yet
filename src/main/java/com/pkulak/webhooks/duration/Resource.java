package com.pkulak.webhooks.duration;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;
import com.pkulak.webhooks.WebhookResponse;
import org.ocpsoft.prettytime.PrettyTime;
import org.ocpsoft.prettytime.units.JustNow;
import org.ocpsoft.prettytime.units.Millisecond;
import org.ocpsoft.prettytime.units.Second;
import org.ocpsoft.prettytime.units.Week;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Path("duration")
public class Resource {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public WebhookResponse getIt(ObjectNode payload) {
        ObjectNode parameters = (ObjectNode) payload.get("result").get("parameters");
        Instant query;

        if (contains(parameters, "date-time")) {
            query = Instant.parse(parameters.get("date-time").asText());
        } else if (contains(parameters, "date")) {
            LocalDate date = LocalDate.parse(parameters.get("date").asText());
            query = date.atStartOfDay().toInstant(ZoneOffset.ofHours(-8));
        } else if (contains(parameters, "time")) {
            LocalTime time = LocalTime.parse(parameters.get("time").asText());
            query = LocalDateTime.of(LocalDate.now(), time).toInstant(ZoneOffset.ofHours(-8));

            // let's not go backwards for this one
            if (query.isBefore(Instant.now())) {
                query = LocalDateTime
                        .of(LocalDate.now().plus(1, ChronoUnit.DAYS), time)
                        .toInstant(ZoneOffset.ofHours(-8));
            }
        } else {
            throw new BadRequestException("unknown parameter");
        }

        PrettyTime pt = new PrettyTime(new Date());

        pt.removeUnit(JustNow.class);
        pt.removeUnit(Millisecond.class);
        pt.removeUnit(Second.class);
        pt.removeUnit(Week.class);

        List<org.ocpsoft.prettytime.Duration> durations = pt.calculatePreciseDuration(Date.from(query));

        if (durations.size() > 2) {
            durations = durations.subList(0, 2);
        }

        if (query.isAfter(Instant.now())) {
            return new WebhookResponse("That will be " + pt.format(durations) + ".");
        }

        return new WebhookResponse("That was " + pt.format(durations) + ".");
    }

    // we get empty strings from the API
    private boolean contains(ObjectNode node, String key) {
        return node.has("date-time") && !Strings.isNullOrEmpty(node.get(key).asText());
    }
}
