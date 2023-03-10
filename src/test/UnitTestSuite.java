import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
public class UnitTestSuite {
	@Suite.SuiteClasses({
			AppTests.class,
			ServiceTests.class
	})
	public class UnitTests {

	}
}
