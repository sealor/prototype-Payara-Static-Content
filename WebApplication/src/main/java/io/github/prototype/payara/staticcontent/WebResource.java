package io.github.prototype.payara.staticcontent;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/")
public class WebResource {

    @GET
    public String hello() {
        return "I'm dynamic content!";
    }
}
