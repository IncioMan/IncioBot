package inciobot.bot_backend.test;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import inciobot.bot_backend.services.IStatsService;
import inciobot.bot_ci.Filter;
import inciobot.bot_ci.MatchCount;
import inciobot.bot_ci.MatchCountPerWeekPerUser;
import inciobot.bot_ci.MatchResult;
import inciobot.bot_ci.MonthCount;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppConfigTest.class })
public class StatsTest {

	@Autowired
	private IStatsService service;

	@Test
	public void matchCount() {
		List<MatchCount> matchCount = service.getMatchCount();
		matchCount.size();
	}

	@Test
	public void matchCountPerWeekPerUser() {
		List<MatchCountPerWeekPerUser> matchCount = service.getMatchCountPerWeekPerUser();
		matchCount.size();
	}

	@Test
	public void matchCountFilter() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.YEAR, -1);
		Filter filter = new Filter();
		filter.setFromDate(calendar.getTime());
		filter.setToDate(new Date());
		filter.addPlayerUsername("AlexIncerti");
		filter.addMatchResult(MatchResult.WON);

		service.getMatchWeekDistribution(filter);
	}

	@Test
	public void getPricePerMonth() {
		List<MonthCount> pricePerMonth = service.getPricePerMonth();
		System.out.println(pricePerMonth.size());
	}

	@Test
	public void getDistancePerMonth() {
		List<MonthCount> distancePerMonth = service.getDistancePerMonth();
		System.out.println(distancePerMonth.size());
	}

	@Test
	public void getavgDistQuantityPerMonth() {
		List<MonthCount> avgDistQuantityPerMonth = service.getAvgDistQuantityPerMonth();
		System.out.println(avgDistQuantityPerMonth.size());
	}
}
