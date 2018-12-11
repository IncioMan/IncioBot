package inciobot.bot_backend.bot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import inciobot.bot_backend.enums.TypePlayerStats;
import inciobot.bot_backend.model.User;
import inciobot.bot_backend.model.fifa.FifaMatch;
import inciobot.bot_backend.model.fifa.Player;
import inciobot.bot_backend.model.fifa.PlayerStats;
import inciobot.bot_backend.services.IService;
import inciobot.bot_backend.services.IStatsService;
import inciobot.bot_ci.ChallengeReportDto;
import inciobot.bot_ci.FifaStatus;
import inciobot.bot_ci.Filter;
import inciobot.bot_ci.MatchCount;
import inciobot.bot_ci.MatchCountPerWeekPerUser;
import inciobot.bot_ci.MatchResult;
import inciobot.bot_ci.SimpleFifaMatch;
import inciobot.bot_ci.TimeAggregation;

@Controller
@RequestMapping(value = "/fifa-api/")
public class FifaRestApiProvider {

	@Autowired
	private IService service;
	@Autowired
	private IStatsService statsService;

	@GetMapping(value = "/status/", headers = "Accept=application/json")
	@ResponseBody
	public FifaStatus getStatus() {
		Date today = new Date();
		Calendar todayStart = Calendar.getInstance();
		todayStart.setTime(today);
		todayStart.set(Calendar.HOUR_OF_DAY, 0);
		todayStart.set(Calendar.MINUTE, 0);
		List<FifaMatch> matches = service.getMatches(todayStart.getTime(), today);

		Integer goalsScored = 0;
		for (FifaMatch match : matches) {
			goalsScored += match.getTeam1().getGoals() + match.getTeam2().getGoals();
		}

		List<FifaMatch> generalMatches = service.getAllMatches(true);

		Integer generalGoalsScored = 0;
		for (FifaMatch match : generalMatches) {
			generalGoalsScored += match.getTeam1().getGoals() + match.getTeam2().getGoals();
		}

		FifaStatus status = new FifaStatus("alive");
		status.setTodayGoalsScored(goalsScored);
		status.setTodayMatchesSize(matches.size());
		status.setGeneralGoalsScored(generalGoalsScored);
		status.setGeneralMatchesSize(generalMatches.size());

		return status;
	}

	@GetMapping(value = "/statistics/matchdistribution/", headers = "Accept=application/json")
	@ResponseBody
	public List<MatchCount> getMatchDistr() {
		return statsService.getMatchCount();
	}

	@GetMapping(value = "/statistics/matchdistribution/week-user/", headers = "Accept=application/json")
	@ResponseBody
	public List<MatchCountPerWeekPerUser> getMatchDistrPerWeekPerUser() {
		return statsService.getMatchCountPerWeekPerUser();
	}

	@PostMapping(value = "/statistics/matchdistribution/week-distr/", headers = "Accept=application/json")
	@ResponseBody
	public List<MatchCountPerWeekPerUser> getMatchDistr(@RequestBody Filter filter) {
		return statsService.getMatchWeekDistribution(filter);
	}

	@GetMapping(value = "/statistics/matchdistribution/week-distr/filter", headers = "Accept=application/json")
	@ResponseBody
	public Filter getFilter() {
		Filter filter = new Filter();
		filter.setFromDate(new Date());
		filter.setFromDate(new Date());
		filter.addPlayerUsername("AlexIncerti");
		filter.addMatchResult(MatchResult.WON);

		return filter;
	}

	@GetMapping(value = "/statistics/matchdistribution/week-distr/result", headers = "Accept=application/json")
	@ResponseBody
	public List<MatchCountPerWeekPerUser> getMatchDistrResultWithoutFilter() {
		Calendar from = Calendar.getInstance();
		from.add(Calendar.YEAR, -1);

		Filter filter = new Filter();
		filter.setFromDate(from.getTime());
		filter.setToDate(new Date());
		filter.setPlayersUsername(null);
		filter.addMatchResult(MatchResult.WON);

		return statsService.getMatchWeekDistribution(filter);
	}

	@GetMapping(value = "/statistics/matchdistribution/week-distr/goals", headers = "Accept=application/json")
	@ResponseBody
	public List<MatchCountPerWeekPerUser> getMatchDistrGoalsWithoutFilter() {
		Calendar from = Calendar.getInstance();
		from.add(Calendar.YEAR, -1);

		Filter filter = new Filter();
		filter.setFromDate(from.getTime());
		filter.setToDate(new Date());
		filter.setPlayersUsername(null);

		return statsService.getMatchWeekGoalsDistribution(filter);
	}

	@CrossOrigin
	@GetMapping(value = "/statistics/opponents/", headers = "Accept=application/json")
	@ResponseBody
	public List<ChallengeReportDto> getOpponentChallengeStatistics(@RequestParam String opponent1,
			@RequestParam String opponent2, @RequestParam TimeAggregation timeAggregation) {
		return statsService.getChallengeReport(opponent1, opponent2, timeAggregation);
	}

	@CrossOrigin
	@GetMapping(value = "/statistics/opponents/generalstats/", headers = "Accept=application/json")
	@ResponseBody
	public ChallengeReportDto getOpponentGeneralChallengeStatistics(@RequestParam String opponent1,
			@RequestParam String opponent2) {
		return statsService.getOpponentGeneralChallengeStatistics(opponent1, opponent2);
	}

	@CrossOrigin
	@GetMapping(value = "/statistics/player/", headers = "Accept=application/json")
	@ResponseBody
	public List<ChallengeReportDto> getPlayerChallengeStatistics(@RequestParam String playerUsername,
			@RequestParam TimeAggregation timeAggregation) {
		return statsService.getPlayerReport(playerUsername, timeAggregation);
	}

	@CrossOrigin
	@GetMapping(value = "/users/active/usernames", headers = "Accept=application/json")
	@ResponseBody
	public List<String> getActiveUsersUsername() {
		return service.getActivePlayers().stream().map(Player::getUser).map(User::getUsername)
				.collect(Collectors.toList());
	}

	@CrossOrigin
	@GetMapping(value = "/users/", headers = "Accept=application/json")
	@ResponseBody
	public List<Player> getAllUsers(@RequestParam(required = false) boolean active) {
		if (active) {
			return service.getActivePlayers();
		}
		return service.getAllPlayers();
	}

	@CrossOrigin
	@GetMapping(value = "/matches/random/withcomments", headers = "Accept=application/json")
	@ResponseBody
	public List<FifaMatch> getRandomMatchWithComment() {
		return service.getRandomMatchWithComment();
	}

	@CrossOrigin
	@GetMapping(value = "/matches/random/withcomments/simpleformat", headers = "Accept=application/json")
	@ResponseBody
	public List<SimpleFifaMatch> getRandomMatchWithCommentSimpleFormat() {
		List<FifaMatch> v = service.getRandomMatchWithComment();
		List<SimpleFifaMatch> retval = new ArrayList<>();

		v.stream().forEach(f -> {
			SimpleFifaMatch s = new SimpleFifaMatch();
			s.setName(f.getOpponentsRappresentation());
			s.setResult(f.getResult());
			s.setComment(f.getComment());
			s.setDate(new SimpleDateFormat("dd-MM-YYYY").format(f.getDateCreation()));

			retval.add(s);
		});

		return retval;
	}

	@CrossOrigin
	@GetMapping(value = "/matches/size")
	@ResponseBody
	public String getMatchSize(@RequestParam String username) {
		return service.getAllMatches(username).size() + "";
	}

	@CrossOrigin
	@GetMapping(value = "/matches/")
	@ResponseBody
	public List<FifaMatch> getMatches(@RequestParam(required = false) Long id, //
			@RequestParam(required = false) String username, //
			@RequestParam(required = false) String email, //
			@RequestParam(required = false) Integer limit) {
		List<FifaMatch> matches = new ArrayList<>();

		if (username != null) {
			matches = service.getAllMatches(username);
		} else if (email != null) {
			Player player = service.getPlayerByEmail(email);
			if (player != null) {
				matches = service.getAllMatches(player.getUser().getUsername());
			}
		} else if (id != null) {
			matches.add(service.getMatchById(id));
		} else {
			matches = service.getAllMatches();
		}

		if (limit != null && matches.size() > limit) {
			matches = matches.subList(0, limit);
		}

		return matches;
	}

	@CrossOrigin
	@GetMapping(value = "/general-stats/")
	@ResponseBody
	public PlayerStats getGeneralReport(@RequestParam(required = false) String username, //
			@RequestParam(required = false) String email) {
		Player player = null;

		if (username != null) {
			player = service.getPlayerByUsername(username);
		} else if (email != null) {
			player = service.getPlayerByEmail(email);
		}

		if (player != null) {
			return service.getPlayerStats(player.getId(), TypePlayerStats.GENERAL);
		}

		return null;
	}
}
