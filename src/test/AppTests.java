
import com.google.gson.GsonBuilder;
import com.app.config.ConfigMapper;
import com.app.config.Configuration;
import com.app.config.ConfigurationException;
import com.app.thread.ex.AsyncResult;
import com.app.thread.ex.ThreadAsyncExecutor;
import com.app.thread.pool.Task;
import com.app.thread.pool.TransferTask;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.AssertJUnit.fail;

public class AppTests {
	private static Logger LOGGER = LoggerFactory.getLogger(AppTests.class);
	@Rule
	public final MockWebServer server = new MockWebServer();

	@Test
	public void configInitializationSuccess() throws Exception {
		ConfigMapper mapper = new ConfigMapper(new GsonBuilder().setPrettyPrinting().create());
		Configuration config = mapper.getConfig("config.json");
		assertThat(config.getEndpoint()).isEqualTo("https://aws-pps-news-postedit-dev-aspera-proxy.ppt.warnermedia.com/");
		assertThat(config.getThreadCount()).isEqualTo(5);
		assertThat(config.getWaitTime()).isEqualTo(300);
	}

	@Test
	public void configInitializationIncorrectValues() throws Exception {
		ConfigMapper mapper = new ConfigMapper(new GsonBuilder().setPrettyPrinting().create());
		try {
			mapper.getConfig("transfer.json");
			fail("The test was expected to fail with an exception thrown but was successful instead.");
		} catch (Exception e) {
		  assertThat(e)
			  .isInstanceOf(ConfigurationException.class)
			  .hasMessage(
				  "There was an error with the config file.\nThe endpoint property is required and returned null. Please add this property to config.json.");
		}
	}

	@Test
	public void appThreadTest() throws Exception {
		server.enqueue(new MockResponse().setBody("{\"id\": \"12345678\", \"title\": \"test1\"}"));
		server.enqueue(new MockResponse().setBody("{\"status\": \"complete\"}"));
		server.enqueue(new MockResponse().setBody("{\"id\": \"87654321\", \"title\": \"test2\"}"));
		server.enqueue(new MockResponse().setBody("{\"status\": \"complete\"}"));

		ThreadAsyncExecutor executor = new ThreadAsyncExecutor();
		List<Task> transferTasks = new ArrayList<>();
		for (int i=0; i < 2; i++) {
			transferTasks.add(new TransferTask(executor));
		}
		for(Task task : transferTasks) {
			AsyncResult<Object> start = task.start(server.url("/").url().toString(), 1);
			Object end = task.end(start);
			assertThat(end.toString()).isEqualTo("");
		}
	}
}
