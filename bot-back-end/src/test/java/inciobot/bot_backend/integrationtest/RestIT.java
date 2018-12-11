package inciobot.bot_backend.integrationtest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import inciobot.bot_backend.model.User;
import inciobot.bot_backend.model.fifa.FifaMatch;
import inciobot.bot_backend.model.fifa.Player;
import inciobot.bot_backend.model.fifa.TeamPerformance;
import inciobot.bot_backend.services.IService;
import inciobot.bot_backend.test.AppConfigTest;
import inciobot.bot_ci.ChallengeReportDto;
import inciobot.bot_ci.FifaStatus;
import inciobot.bot_ci.TimeAggregation;
import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppConfigTest.class })
public class RestIT extends TestCase {
	private static final Integer USER_1_ID = 190;
	private static final Integer USER_2_ID = 191;
	private static final String BACKEND_ADDRESS = "http://localhost:8081/test";
	private static final String player2username = "Player_2";
	private static final TimeAggregation time = TimeAggregation.MONTH;
	private static final String player1username = "Player_1";
	private static final int GOALS_1 = 2;
	private static final int GOALS_2 = 1;
	private static final String player3username = "Player_3";
	private static final Integer USER_3_ID = 192;
	private List<Player> players = new ArrayList<>();
	private List<FifaMatch> matches = new ArrayList<>();

	@Autowired
	private IService service;

	@Before
	public void setUp() {
		Player player1 = createPlayer(player1username, USER_1_ID);
		Player player2 = createPlayer(player2username, USER_2_ID);
		Player player3 = createPlayer(player3username, USER_3_ID);

		try {
			player1.setId(service.savePlayer(player1));
			player2.setId(service.savePlayer(player2));
			player3.setId(service.savePlayer(player3));
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		players.add(player1);
		players.add(player2);
		players.add(player3);

		/*
		 * Match 1
		 */
		TeamPerformance teamPerformance1 = createTeamPerformance(player1, GOALS_1);
		TeamPerformance teamPerformance2 = createTeamPerformance(player2, GOALS_2);

		Date date1;
		try {
			date1 = new SimpleDateFormat("dd-MM-yyyy").parse("21-02-2016");
		} catch (ParseException e) {
			e.printStackTrace();
			return;
		}

		FifaMatch match1 = createFifaMatch(teamPerformance1, teamPerformance2, date1);
		match1.setId(service.addMatch(match1));
		matches.add(match1);

		/*
		 * Match 2
		 */

		TeamPerformance teamPerformance3 = createTeamPerformance(player1, GOALS_2);
		TeamPerformance teamPerformance4 = createTeamPerformance(player2, GOALS_1);

		Date date2;
		try {
			date2 = new SimpleDateFormat("dd-MM-yyyy").parse("21-03-2016");
		} catch (ParseException e) {
			e.printStackTrace();
			return;
		}

		FifaMatch match2 = createFifaMatch(teamPerformance3, teamPerformance4, date2);
		match2.setId(service.addMatch(match2));
		matches.add(match2);

		/*
		 * Match 3
		 */

		TeamPerformance teamPerformance5 = createTeamPerformance(player1, GOALS_1);
		TeamPerformance teamPerformance6 = createTeamPerformance(player3, GOALS_1);

		Date date3;
		try {
			date3 = new SimpleDateFormat("dd-MM-yyyy").parse("20-03-2016");
		} catch (ParseException e) {
			e.printStackTrace();
			return;
		}

		FifaMatch match3 = createFifaMatch(teamPerformance5, teamPerformance6, date3);
		match3.setId(service.addMatch(match3));
		matches.add(match3);
	}

	private FifaMatch createFifaMatch(TeamPerformance teamPerformance1, TeamPerformance teamPerformance2,
			Date dateCreation) {
		FifaMatch match = new FifaMatch();
		match.setTeam1(teamPerformance1);
		match.setTeam2(teamPerformance2);
		match.setDateCreation(dateCreation);
		match.setCompleted(true);

		return match;
	}

	private TeamPerformance createTeamPerformance(Player player, int goals) {
		TeamPerformance teamPerformance = new TeamPerformance();

		HashSet<Player> players = new HashSet<>();
		players.add(player);

		teamPerformance.setPlayers(players);
		teamPerformance.setGoals(goals);

		return teamPerformance;
	}

	private Player createPlayer(String username, Integer userId) {
		Player player = new Player();
		player.setUser(createUser(username, userId));
		player.setActive(true);
		return player;
	}

	private User createUser(String username, Integer userId) {
		User user = new User();
		user.setId(userId);
		user.setUsername(username);
		return user;
	}

	@Test
	public void testAlive() {
		RestTemplate restTemplate = new RestTemplate();
		FifaStatus status = restTemplate.getForObject(BACKEND_ADDRESS + "/fifa-api/status/", FifaStatus.class);
		Assert.assertTrue(status.getTodayGoalsScored() >= 0);
		Assert.assertTrue(status.getTodayMatchesSize() >= 0);
	}

	@Test
	public void testOpponentChallenge() {
		ResponseEntity<List<ChallengeReportDto>> rateResponse = new RestTemplate().exchange(
				BACKEND_ADDRESS + "/fifa-api/statistics/opponents/?opponent1=" + player1username + "&opponent2="
						+ player2username + "&timeAggregation=" + time,
				HttpMethod.GET, null, new ParameterizedTypeReference<List<ChallengeReportDto>>() {
				});
		List<ChallengeReportDto> counts = rateResponse.getBody();

		Assert.assertNotNull(counts);

		counts.sort((o1, o2) -> {
			if (o1.getYear().equals(o2.getYear())) {
				return o1.getMonth().compareTo(o2.getMonth());
			} else {
				return o1.getYear().compareTo(o2.getYear());
			}
		});

		Assert.assertTrue(counts.size() == 2);

		Assert.assertTrue(counts.get(0).getMonth().equals(1.0d));
		Assert.assertTrue(counts.get(0).getWon().equals(1));
		Assert.assertTrue(counts.get(0).getWonPercentage().equals(100d));

		Assert.assertTrue(counts.get(1).getMonth().equals(2.0d));
		Assert.assertTrue(counts.get(1).getWon().equals(0));
		Assert.assertTrue(counts.get(1).getWonPercentage().equals(0d));
	}

	@Test
	public void testOpponentGeneralStats() {
		ChallengeReportDto rateResponse = new RestTemplate()
				.getForObject(BACKEND_ADDRESS + "/fifa-api/statistics/opponents/generalstats/?opponent1="
						+ player1username + "&opponent2=" + player2username, ChallengeReportDto.class);

		Assert.assertNotNull(rateResponse);

		Assert.assertTrue(rateResponse.getMatchesSize().equals(2));
		Assert.assertTrue(rateResponse.getLostPercentage().equals(50.0d));
		Assert.assertTrue(rateResponse.getWonPercentage().equals(50.0d));
		Assert.assertTrue(rateResponse.getGoalsScored() == ((GOALS_1 + GOALS_2) * 1.0d));
	}

	@Test
	public void testPlayerChallenge() {
		ResponseEntity<List<ChallengeReportDto>> rateResponse = new RestTemplate()
				.exchange(
						BACKEND_ADDRESS + "/fifa-api/statistics/player/?playerUsername=" + player1username
								+ "&timeAggregation=" + time,
						HttpMethod.GET, null, new ParameterizedTypeReference<List<ChallengeReportDto>>() {
						});
		List<ChallengeReportDto> counts = rateResponse.getBody();

		Assert.assertNotNull(counts);

		counts.sort((o1, o2) -> {
			if (o1.getYear().equals(o2.getYear())) {
				return o1.getMonth().compareTo(o2.getMonth());
			} else {
				return o1.getYear().compareTo(o2.getYear());
			}
		});

		Assert.assertTrue(counts.size() == 2);

		Assert.assertTrue(counts.get(0).getMonth().equals(1.0d));
		Assert.assertTrue(counts.get(0).getWon().equals(1));
		Assert.assertTrue(counts.get(0).getWonPercentage().equals(100d));

		Assert.assertTrue(counts.get(1).getMonth().equals(2.0d));
		Assert.assertTrue(counts.get(1).getWon().equals(0));
		Assert.assertTrue(counts.get(1).getWonPercentage().equals(0d));
		Assert.assertTrue(counts.get(1).getDraw().equals(1));
		Assert.assertTrue(counts.get(1).getDrawPercentage().equals(50d));
		Assert.assertTrue(counts.get(1).getLost().equals(1));
		Assert.assertTrue(counts.get(1).getLostPercentage().equals(50d));
		Assert.assertTrue(counts.get(1).getGoalsScoredAvg().equals(1.5d));
		Assert.assertTrue(counts.get(1).getGoalsAgainstAvg().equals((GOALS_1 + GOALS_1) / 2d));
	}

	@After
	public void closeUp() {
		matches.stream().forEach(m -> {
			service.deleteMatch(m.getId());
		});
		players.stream().forEach(p -> {
			service.getPlayerStats(p.getId()).stream().forEach(s -> {
				service.deletePlayerStats(s);
			});
			service.deletePlayer(p.getId());
		});
	}
}
