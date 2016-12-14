package io.github.prototype.payara.staticcontent;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.formatter.Formatters;
import org.jboss.shrinkwrap.api.importer.ExplodedImporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Arquillian.class)
public class DownloadStaticContentTest {

    @Deployment
    public static WebArchive createWebArchive() throws FileNotFoundException {
        JavaArchive staticContent1Archive = ShrinkWrap.create(GenericArchive.class).as(ExplodedImporter.class) //
                .importDirectory("../StaticContent1/target/classes") //
                .as(JavaArchive.class);

        JavaArchive staticContent2Archive = ShrinkWrap.create(GenericArchive.class).as(ExplodedImporter.class) //
                .importDirectory("../StaticContent2/target/classes") //
                .as(JavaArchive.class);

        WebArchive webArchive = ShrinkWrap.create(WebArchive.class) //
                .addPackages(true, WebApplication.class.getPackage()) //
                .addAsLibraries(staticContent1Archive) //
                .addAsLibraries(staticContent2Archive);

        printArchiveList(staticContent1Archive);
        printArchiveList(staticContent2Archive);
        printArchiveList(webArchive);

        return webArchive;
    }

    private static void printArchiveList(Archive archive) {
        archive.writeTo(System.out, Formatters.VERBOSE);
        System.out.println();
    }

    @ArquillianResource
    private URL baseURI;

    @Test
    public void testDownload() {
        assertEquals("I'm in static-content1 project!", request("static-content1.txt"));
        assertEquals("I'm in static-content2 project!", request("static-content2.txt"));
        assertTrue(request("static-mixed-content.txt").contains("project"));

        assertEquals("I'm dynamic content!", request("api/"));
    }

    private String request(String relativeUrl) {
        try {
            Client client = ClientBuilder.newClient();
            URI uri = this.baseURI.toURI().resolve(relativeUrl);
            return client.target(uri).request().get(String.class);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
