import com.app.Decrypter;
import com.google.gson.GsonBuilder;
import com.utils.CredentialsType;
import com.api.Service;
import com.api.gateway.TransferClientException;
import com.api.gateway.TransferStatusClientException;
import com.api.gateway.TransferResponse;
import com.api.gateway.TransferStatusResponse;
import com.app.config.ConfigMapper;
import com.app.config.Configuration;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.Callable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNull;
import static org.testng.AssertJUnit.fail;

public class ServiceTests {
  @Rule public final MockWebServer server = new MockWebServer();

  @BeforeClass
  public static void db() throws Exception {
      Decrypter.decryptFromDB(CredentialsType.POST_EDITOR);
  }

  @Test
  public void transferServiceIsSuccessful() throws Exception {
      Service service = new Service(server.url("/").url().toString());
      server.enqueue(new MockResponse().setBody("{\"id\": \"12345678\", \"title\": \"test\"}"));
      TransferResponse result = service.createNewTransfer();
      assertThat(result.getId()).isEqualTo("12345678");
      assertThat(result.getTitle()).isEqualTo("test");
  }

  @Test
  public void transferServiceIdIsNull() throws Exception {
        Service service = new Service(server.url("/").url().toString());
        server.enqueue(new MockResponse().setBody("{\"id\": null, \"title\": \"test\"}"));
        try {
          service.createNewTransfer();
          fail("The test was expected to fail with an exception thrown but the api call was successful instead.");
        } catch (TransferClientException e) {
          assertThat(e)
              .isInstanceOf(TransferClientException.class)
              .hasMessage(
                  "An error occurred while calling the transfer api.\nThe transfer id returned a null value. Check client configuration.");
        }
  }

    @Test
    public void transferService400Error() throws Exception {
        Service service = new Service(server.url("/").url().toString());
        server.enqueue(new MockResponse().setResponseCode(404));
        try {
            service.createNewTransfer();
            fail("The test was expected to fail with an exception thrown but the api call was successful instead.");
        } catch (TransferClientException e) {
      assertThat(e)
          .isInstanceOf(TransferClientException.class)
          .hasMessage(
              "An error occurred while calling the transfer api.\nService response was 404");
        }
    }

    @Test
    public void transferStatusServiceIsSuccessful() throws Exception {
        Service service = new Service(server.url("/").url().toString());
        server.enqueue(new MockResponse().setBody("{\"status\": \"complete\"}"));
        TransferStatusResponse result = service.verifyTransferStatus("123456789", 1);
        assertThat(result.getStatus()).isEqualTo("complete");
    }

    @Test
    public void transferStatusServiceWaitTimeout() throws Exception {
        Service service = new Service(server.url("/").url().toString());
        server.enqueue(new MockResponse().setResponseCode(200).setBody("{\"status\": \"processing\"}"));
        TransferStatusResponse result = service.verifyTransferStatus("123456789", 1);
        assertNull(result);
    }

    @Test
    public void transferStatusServiceIdIsNull() throws Exception {
        Service service = new Service(server.url("/").url().toString());
        server.enqueue(new MockResponse().setBody("{\"status\": \"complete\"}"));
        try {
            service.verifyTransferStatus(null, 1);
            fail("The test was expected to fail with an exception thrown but the api call was successful instead.");
        } catch (TransferClientException e) {
            assertThat(e)
                    .isInstanceOf(TransferClientException.class)
                    .hasMessage(
                            "An error occurred while calling the transfer api.\nThe transfer id returned a null value. Check client configuration.");
        }
    }

    @Test
    public void transferStatusService400Error() throws Exception {
        Service service = new Service(server.url("/").url().toString());
        server.enqueue(new MockResponse().setResponseCode(404));
        try {
            service.verifyTransferStatus("1233435", 1);
            fail("The test was expected to fail with an exception thrown but the api call was successful instead.");
        } catch (TransferStatusClientException e) {
            assertThat(e)
                    .isInstanceOf(TransferStatusClientException.class)
                    .hasMessage(
                            "An error occurred while calling the transfer status api.\nService response was 404");
        }
    }

    @Test
    public void fullTransferTest() throws Exception {
        Service service = new Service(server.url("/").url().toString());
        server.enqueue(new MockResponse().setBody("{\"id\": \"12345678\", \"title\": \"test\"}"));
        server.enqueue(new MockResponse().setBody("{\"status\": \"complete\"}"));
        Callable<String> result = service.transfer("Done", 1);
        assertThat(result.call()).isEqualTo("Done");
    }

    @Test
    public void transferTest() throws Exception {
        ConfigMapper mapper = new ConfigMapper(new GsonBuilder().setPrettyPrinting().create());
        Configuration config = mapper.getConfig("config.json");
        Service service = new Service(config.getEndpoint());
        Callable<String> result = service.transfer("Done", 1);
        assertThat(result.call()).isEqualTo("Done");
    }
}
