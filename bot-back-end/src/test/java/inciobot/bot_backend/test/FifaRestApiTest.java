package inciobot.bot_backend.test;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import inciobot.bot_backend.bot.FifaRestApiProvider;
import inciobot.bot_backend.model.fifa.FifaMatch;
import inciobot.bot_ci.FifaStatus;
import inciobot.bot_ci.MatchCountPerWeekPerUser;
import inciobot.bot_ci.SimpleFifaMatch;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppConfigTest.class })
public class FifaRestApiTest {

	@Autowired
	private FifaRestApiProvider provider;

	@Test
	public void test() {
		FifaStatus status = provider.getStatus();
		System.out.println(status.getStatus());
	}

	@Test
	public void test2() {
		List<MatchCountPerWeekPerUser> result = provider.getMatchDistrGoalsWithoutFilter();
		System.out.println(result.size());
	}

	@Test
	public void test3() {
		List<FifaMatch> result = provider.getRandomMatchWithComment();
		System.out.println(result.size());
	}

	@Test
	public void test4() {
		List<SimpleFifaMatch> result = provider.getRandomMatchWithCommentSimpleFormat();
		System.out.println(result.size());
	}

}
