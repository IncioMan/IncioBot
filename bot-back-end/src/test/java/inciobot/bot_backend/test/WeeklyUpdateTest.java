package inciobot.bot_backend.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppConfigTest.class })
public class WeeklyUpdateTest {

	// @Autowired
	// private FifaStatisticsManager fifaStatisticsManager;

	@Test
	public void test() {
		// fifaStatisticsManager.sendWeeklyReports();
	}
}
