package inciobot.bot_backend.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import inciobot.bot_backend.model.fifa.FifaMatch;
import inciobot.bot_backend.model.fifa.Player;
import inciobot.bot_backend.utils.fifa.MonthAndYear;
import inciobot.bot_backend.utils.fifa.WeekAndYear;
import inciobot.bot_ci.Filter;
import inciobot.bot_ci.MatchCount;
import inciobot.bot_ci.MatchCountPerWeekPerUser;

@Repository
@Transactional
public class FifaMatchDao extends FifaAbstractDao<FifaMatch> {
	@Autowired
	private FifaPlayerDao fifaPlayerDao;

	@Autowired
	private FifaPlayerStatsDao fifaPlayerStatsDao;

	public void saveFifaMatch(FifaMatch match) {
		if (match == null)
			return;
		getSession().save(match);
	}

	public void updateFifaMatch(FifaMatch match) {
		if (match == null)
			return;
		getSession().update(match);
	}

	@SuppressWarnings("unchecked")
	private List<FifaMatch> getFifaMatchesCreated(String userId, boolean completed, boolean withTeamPlayers) {
		Criteria criteria = getSession().createCriteria(FifaMatch.class);
		if (withTeamPlayers) {
			criteria.setFetchMode("team1.players", FetchMode.JOIN);
			criteria.setFetchMode("team2.players", FetchMode.JOIN);
		}
		criteria.createAlias("creator.user", "u");
		criteria.add(Restrictions.eq("u.id", Integer.parseInt(userId)));
		criteria.add(Restrictions.eq("completed", completed));
		criteria.addOrder(Order.desc("dateCreation"));

		return criteria.list();
	}

	public void createNewFifaMatch(Integer creatorId) {
		if (creatorId == null)
			return;

		Player player = fifaPlayerDao.getPlayer(creatorId.toString());
		if (player == null)
			return;

		FifaMatch match = new FifaMatch();

		List<FifaMatch> matches = getFifaMatchesCreated(creatorId.toString(), false, true);
		if (matches.size() > 0) {
			match = matches.get(0);
		}

		match.setCreator(player);
		match.setDateCreation(new Date());
		match.getTeam1().getPlayers().add(player);
		getSession().saveOrUpdate(match);
	}

	public void setOpponentIntoMatch(Integer creatorId, String opponenUsername) {
		if (creatorId == null)
			return;

		Player player = fifaPlayerDao.getPlayerByUsername(opponenUsername);
		if (player == null)
			return;

		FifaMatch match = new FifaMatch();

		List<FifaMatch> matches = getFifaMatchesCreated(creatorId.toString(), false, true);
		if (matches.size() > 0) {
			match = matches.get(0);
		}

		// not use getPlayers.add(player) to prevent other players to get added
		Set<Player> opponents = new HashSet<Player>();
		opponents.add(player);

		match.getTeam2().setPlayers(opponents);
		getSession().saveOrUpdate(match);
	}

	@SuppressWarnings("unchecked")
	public List<FifaMatch> getAllMatches(boolean completed) {
		Criteria criteria = getSession().createCriteria(FifaMatch.class);
		criteria.setFetchMode("team1.players", FetchMode.JOIN);
		criteria.setFetchMode("team2.players", FetchMode.JOIN);
		criteria.add(Restrictions.eq("completed", completed));
		criteria.addOrder(Order.desc("dateCreation"));

		List<FifaMatch> list = criteria.list();
		return list;
	}

	public void setMatchResult(Integer creatorId, int team1Goals, int team2Goals) {
		if (creatorId == null)
			return;

		FifaMatch match = new FifaMatch();

		List<FifaMatch> matches = getFifaMatchesCreated(creatorId.toString(), false, false);
		if (matches.size() > 0) {
			match = matches.get(0);
		}

		match.getTeam1().setGoals(team1Goals);
		match.getTeam2().setGoals(team2Goals);
		getSession().saveOrUpdate(match);
	}

	public FifaMatch setMatchComplete(Integer creatorId) {
		if (creatorId == null)
			return null;

		FifaMatch match = new FifaMatch();

		List<FifaMatch> matches = getFifaMatchesCreated(creatorId.toString(), false, true);
		if (matches.size() > 0) {
			match = matches.get(0);
		}

		match.setCompleted(true);
		updateStats(match, false);
		getSession().saveOrUpdate(match);
		return match;
	}

	private void updateStats(FifaMatch match, boolean removeMatch) {
		boolean won1 = false, draw = false;

		if (match.getTeam1().getGoals() == match.getTeam2().getGoals()) {
			draw = true;
		} else if (match.getTeam1().getGoals() > match.getTeam2().getGoals()) {
			won1 = true;
		}

		if (!removeMatch) {
			if (draw) {
				for (Player player : match.getTeam1().getPlayers()) {
					fifaPlayerStatsDao.addDraw(player, match.getTeam1().getGoals(), match.getTeam2().getGoals());
				}
				for (Player player : match.getTeam2().getPlayers()) {
					fifaPlayerStatsDao.addDraw(player, match.getTeam2().getGoals(), match.getTeam1().getGoals());
				}
			} else {
				for (Player player : match.getTeam1().getPlayers()) {
					if (won1)
						fifaPlayerStatsDao.addWon(player, match.getTeam1().getGoals(), match.getTeam2().getGoals());
					else
						fifaPlayerStatsDao.addLost(player, match.getTeam1().getGoals(), match.getTeam2().getGoals());
				}
				for (Player player : match.getTeam2().getPlayers()) {
					if (!won1)
						fifaPlayerStatsDao.addWon(player, match.getTeam2().getGoals(), match.getTeam1().getGoals());
					else
						fifaPlayerStatsDao.addLost(player, match.getTeam2().getGoals(), match.getTeam1().getGoals());
				}
			}
		}
		if (removeMatch) {
			if (draw) {
				for (Player player : match.getTeam1().getPlayers()) {
					fifaPlayerStatsDao.removeDraw(player, match.getTeam1().getGoals(), match.getTeam2().getGoals());
				}
				for (Player player : match.getTeam2().getPlayers()) {
					fifaPlayerStatsDao.removeDraw(player, match.getTeam2().getGoals(), match.getTeam1().getGoals());
				}
			} else {
				for (Player player : match.getTeam1().getPlayers()) {
					if (won1)
						fifaPlayerStatsDao.removeWon(player, match.getTeam1().getGoals(), match.getTeam2().getGoals());
					else
						fifaPlayerStatsDao.removeLost(player, match.getTeam1().getGoals(), match.getTeam2().getGoals());
				}
				for (Player player : match.getTeam2().getPlayers()) {
					if (!won1)
						fifaPlayerStatsDao.removeWon(player, match.getTeam2().getGoals(), match.getTeam1().getGoals());
					else
						fifaPlayerStatsDao.removeLost(player, match.getTeam2().getGoals(), match.getTeam1().getGoals());
				}
			}
		}
	}

	public List<FifaMatch> getMatchesByCreator(Integer creatorId, boolean completed) {
		List<FifaMatch> allMatches = getAllMatches(true);
		allMatches = allMatches.stream().filter(m -> m.getCreator().getUser().getId().equals(creatorId))
				.collect(Collectors.toList());
		return allMatches;
	}

	public FifaMatch deleteMatchById(Long matchId) {
		FifaMatch match = getMatch(matchId);

		if (match != null) {
			getSession().delete(match);
			getSession().delete(match.getTeam1());
			getSession().delete(match.getTeam2());
			updateStats(match, true);
		}

		return match;
	}

	public void addMatchComment(Integer creatorId, String comment) {
		if (creatorId == null)
			return;

		FifaMatch match = new FifaMatch();

		List<FifaMatch> matches = getFifaMatchesCreated(creatorId.toString(), false, true);
		if (matches.size() > 0) {
			match = matches.get(0);
		}

		match.setComment(comment);
		getSession().saveOrUpdate(match);
	}

	@SuppressWarnings("unchecked")
	private List<FifaMatch> getAllMatches(String opponentUsername, boolean completed) {
		Criteria criteria = getSession().createCriteria(FifaMatch.class);
		criteria.setFetchMode("team1.players", FetchMode.JOIN);
		criteria.setFetchMode("team2.players", FetchMode.JOIN);
		criteria.createAlias("team1.players", "p1");
		criteria.createAlias("team2.players", "p2");
		criteria.createAlias("team1.players.user", "u1");
		criteria.createAlias("team2.players.user", "u2");
		criteria.add(Restrictions.eq("completed", completed));
		criteria.addOrder(Order.desc("dateCreation"));

		Criterion conditionTeam1 = Restrictions.eq("u1.username", opponentUsername);
		Criterion conditionTeam2 = Restrictions.eq("u2.username", opponentUsername);

		criteria.add(Restrictions.or(conditionTeam1, conditionTeam2));
		List<FifaMatch> list = criteria.list();
		return list;
	}

	@SuppressWarnings("unchecked")
	public List<FifaMatch> getAllMatches(Integer userIdentifier, boolean completed) {
		Criteria criteria = getSession().createCriteria(FifaMatch.class);
		criteria.setFetchMode("team1.players", FetchMode.JOIN);
		criteria.setFetchMode("team2.players", FetchMode.JOIN);
		criteria.createAlias("team1.players", "p1");
		criteria.createAlias("team2.players", "p2");
		criteria.add(Restrictions.eq("completed", completed));
		criteria.addOrder(Order.desc("dateCreation"));

		Criterion conditionTeam1 = Restrictions.eq("p1.user.id", userIdentifier);
		Criterion conditionTeam2 = Restrictions.eq("p2.user.id", userIdentifier);

		criteria.add(Restrictions.or(conditionTeam1, conditionTeam2));
		List<FifaMatch> list = criteria.list();
		return list;
	}

	@SuppressWarnings("unchecked")
	public List<FifaMatch> getAllMatches(Integer userIdentifier, String opponentUsername, boolean completed) {
		List<FifaMatch> matchesPlayer1 = getAllMatches(userIdentifier, completed);
		List<FifaMatch> matchesPlayer2 = getAllMatches(opponentUsername, completed);
		List<FifaMatch> retval = new ArrayList<>();

		List<Long> player1ids = matchesPlayer1.stream().map(m -> m.getId()).collect(Collectors.toList());
		List<Long> player2ids = matchesPlayer2.stream().map(m -> m.getId()).collect(Collectors.toList());

		if (!player1ids.isEmpty() && !player2ids.isEmpty()) {
			Criteria criteria = getSession().createCriteria(FifaMatch.class);
			criteria.setFetchMode("team1.players", FetchMode.JOIN);
			criteria.setFetchMode("team2.players", FetchMode.JOIN);
			criteria.createAlias("team1.players", "p1");
			criteria.createAlias("team2.players", "p2");
			criteria.add(Restrictions.eq("completed", completed));
			criteria.addOrder(Order.desc("dateCreation"));

			Criterion conditionTeam1 = Restrictions.in("id", player1ids.toArray());
			Criterion conditionTeam2 = Restrictions.in("id", player2ids.toArray());

			criteria.add(Restrictions.and(conditionTeam1, conditionTeam2));
			criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

			retval = criteria.list();
		}
		return retval;
	}

	@SuppressWarnings("unchecked")
	public List<FifaMatch> getMatches(Date from, Date today) {
		Criteria criteria = getSession().createCriteria(FifaMatch.class);
		criteria.setFetchMode("team1.players", FetchMode.JOIN);
		criteria.setFetchMode("team2.players", FetchMode.JOIN);
		criteria.createAlias("team1.players", "p1");
		criteria.createAlias("team2.players", "p2");
		criteria.add(Restrictions.eq("completed", true));

		Criterion beforeDate = Restrictions.lt("dateCreation", today);
		Criterion afterDate = Restrictions.gt("dateCreation", from);

		criteria.addOrder(Order.desc("dateCreation"));

		criteria.add(Restrictions.and(beforeDate, afterDate));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		return criteria.list();
	}

	public List<FifaMatch> getMatches(Date fromTime, Date toTime, Integer userIdentifier) {
		Player player = fifaPlayerDao.getPlayer(userIdentifier.toString());
		if (player == null) {
			return new ArrayList<>();
		}
		List<FifaMatch> retVal = getMatches(fromTime, toTime);
		retVal = retVal.stream()
				.filter(m -> m.getTeam1().getPlayers().contains(player) || m.getTeam2().getPlayers().contains(player))
				.collect(Collectors.toList());

		return retVal;
	}

	@SuppressWarnings("unchecked")
	public List<MatchCount> getMatchCount() {
		SQLQuery query = getSession().createSQLQuery(
				"SELECT EXTRACT(WEEK FROM datecreation) AS \"week\", EXTRACT(YEAR FROM datecreation) AS \"year\",  COUNT(*) AS \"count\" FROM fifa_match GROUP BY \"week\", \"year\" ORDER BY \"year\", \"week\"");

		query.setResultTransformer(Transformers.aliasToBean(MatchCount.class));
		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<MatchCountPerWeekPerUser> getFirstMatchCountPerWeekPerUser() {
		SQLQuery query = getSession().createSQLQuery(
				"select EXTRACT(WEEK FROM datecreation) as week, EXTRACT(YEAR FROM datecreation) as year, count(*), u2.username from fifa_match m join team_performance_player t on m.team2_team_performance_id = t.team_performance_team_performance_id join player p on t.players_fifa_player_id = p.fifa_player_id join telegram_user u on p.user_user_id = u.user_id  join team_performance_player t2 on m.team1_team_performance_id = t2.team_performance_team_performance_id join player p2 on t2.players_fifa_player_id = p2.fifa_player_id join telegram_user u2 on p2.user_user_id = u2.user_id where m.completed = true group by week, year ,u2.username ORDER BY year, week");

		query.setResultTransformer(Transformers.aliasToBean(MatchCountPerWeekPerUser.class));
		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<MatchCountPerWeekPerUser> getSecondMatchCountPerWeekPerUser() {
		SQLQuery query = getSession().createSQLQuery(
				"select EXTRACT(WEEK FROM datecreation) as week, EXTRACT(YEAR FROM datecreation) as year, count(*), u.username from fifa_match m join team_performance_player t on m.team2_team_performance_id = t.team_performance_team_performance_id join player p on t.players_fifa_player_id = p.fifa_player_id join telegram_user u on p.user_user_id = u.user_id  join team_performance_player t2 on m.team1_team_performance_id = t2.team_performance_team_performance_id join player p2 on t2.players_fifa_player_id = p2.fifa_player_id join telegram_user u2 on p2.user_user_id = u2.user_id where m.completed = true group by week, year ,u.username ORDER BY year, week");

		query.setResultTransformer(Transformers.aliasToBean(MatchCountPerWeekPerUser.class));
		return query.list();
	}

	public List<MatchCountPerWeekPerUser> getMatchCountPerWeekPerUser() {
		List<MatchCountPerWeekPerUser> firstAggr = getFirstMatchCountPerWeekPerUser();
		List<MatchCountPerWeekPerUser> secondAggr = getSecondMatchCountPerWeekPerUser();
		List<MatchCountPerWeekPerUser> toAdd = new ArrayList<>();

		for (MatchCountPerWeekPerUser c2 : secondAggr) {
			boolean found = false;
			for (MatchCountPerWeekPerUser c : firstAggr) {
				if (c.getWeek().equals(c2.getWeek()) && c.getYear().equals(c2.getYear())
						&& c.getUsername().equals(c2.getUsername())) {
					c.addCount(new Long(c2.getCount() + ""));
					found = true;
					continue;
				}
			}
			if (!found) {
				toAdd.add(c2);
			}
		}

		firstAggr.addAll(toAdd);
		return firstAggr;
	}

	public List<MatchCountPerWeekPerUser> getMatchWeekDistribution(Filter filter) {
		if (filter.getToDate() == null) {
			filter.setToDate(new Date());
		}

		List<MatchCountPerWeekPerUser> retval = new ArrayList<>();

		if (filter.getPlayersUsername() == null) {
			filter.setPlayersUsername(fifaPlayerDao.getAllUsername());
		}
		for (String username : filter.getPlayersUsername()) {
			Player player = fifaPlayerDao.getPlayerByUsername(username);
			if (player == null)
				continue;

			List<FifaMatch> matches = getMatches(filter.getFromDate(), filter.getToDate(), player.getUser().getId());
			Map<WeekAndYear, List<FifaMatch>> matchesPerWeek = matches.stream()
					.collect(Collectors.groupingBy(FifaMatch::getWeekDateCreation));

			List<MatchCountPerWeekPerUser> mUsers = getMatchCountPerWeekPerUser(matchesPerWeek, filter, player);
			retval.addAll(mUsers);
		}

		return retval;
	}

	private List<MatchCountPerWeekPerUser> getMatchCountPerWeekPerUser(Map<WeekAndYear, List<FifaMatch>> matchesPerWeek,
			Filter filter, Player player) {
		List<MatchCountPerWeekPerUser> retval = new ArrayList<>();
		for (WeekAndYear weekAndYear : matchesPerWeek.keySet()) {
			MatchCountPerWeekPerUser matchCountPerWeekPerUser = new MatchCountPerWeekPerUser();
			matchCountPerWeekPerUser.setWeek(weekAndYear.getWeek());
			matchCountPerWeekPerUser.setYear(weekAndYear.getYear());
			matchCountPerWeekPerUser.setUsername(player.getUser().getUsername());

			Integer size = matchesPerWeek.get(weekAndYear).size();
			Double results = 0.0;
			for (FifaMatch fifaMatch : matchesPerWeek.get(weekAndYear)) {
				if (filter.getResults().contains(fifaMatch.resultFor(player))) {
					results++;
				}
			}
			matchCountPerWeekPerUser.setCountAvg(results / size);
			retval.add(matchCountPerWeekPerUser);
		}

		return retval;
	}

	public List<FifaMatch> getAllMatches(String username) {
		Player player = fifaPlayerDao.getPlayerByUsername(username);
		if (player == null) {
			return new ArrayList<>();
		} else {
			return getAllMatches(player.getUser().getId(), true);
		}
	}

	public List<MatchCountPerWeekPerUser> getMatchWeekGoalsDistribution(Filter filter) {
		if (filter.getToDate() == null) {
			filter.setToDate(new Date());
		}

		List<MatchCountPerWeekPerUser> retval = new ArrayList<>();

		if (filter.getPlayersUsername() == null) {
			filter.setPlayersUsername(fifaPlayerDao.getAllUsername());
		}
		for (String username : filter.getPlayersUsername()) {
			Player player = fifaPlayerDao.getPlayerByUsername(username);
			if (player == null)
				continue;

			List<FifaMatch> matches = getMatches(filter.getFromDate(), filter.getToDate(), player.getUser().getId());
			Map<WeekAndYear, List<FifaMatch>> matchesPerWeek = matches.stream()
					.collect(Collectors.groupingBy(FifaMatch::getWeekDateCreation));

			List<MatchCountPerWeekPerUser> mUsers = getMatchGoalsCountPerWeekPerUser(matchesPerWeek, filter, player);
			retval.addAll(mUsers);
		}

		return retval;
	}

	private List<MatchCountPerWeekPerUser> getMatchGoalsCountPerWeekPerUser(
			Map<WeekAndYear, List<FifaMatch>> matchesPerWeek, Filter filter, Player player) {
		List<MatchCountPerWeekPerUser> retval = new ArrayList<>();
		for (WeekAndYear weekAndYear : matchesPerWeek.keySet()) {
			MatchCountPerWeekPerUser matchCountPerWeekPerUser = new MatchCountPerWeekPerUser();
			matchCountPerWeekPerUser.setWeek(weekAndYear.getWeek());
			matchCountPerWeekPerUser.setYear(weekAndYear.getYear());
			matchCountPerWeekPerUser.setUsername(player.getUser().getUsername());

			Integer size = matchesPerWeek.get(weekAndYear).size();
			Double results = 0.0;
			for (FifaMatch fifaMatch : matchesPerWeek.get(weekAndYear)) {
				results += fifaMatch.getGoals(player);
			}
			matchCountPerWeekPerUser.setCountAvg(results / size);
			retval.add(matchCountPerWeekPerUser);
		}

		return retval;
	}

	@SuppressWarnings("unchecked")
	public List<FifaMatch> getRandomMatchWithComment() {
		Criteria criteria = getSession().createCriteria(FifaMatch.class);
		criteria.setFetchMode("team1.players", FetchMode.JOIN);
		criteria.setFetchMode("team2.players", FetchMode.JOIN);
		criteria.createAlias("team1.players", "p1");
		criteria.createAlias("team2.players", "p2");
		criteria.add(Restrictions.eq("completed", true));
		criteria.addOrder(Order.desc("dateCreation"));

		Criterion cond1 = Restrictions.neOrIsNotNull("comment", "");
		Criterion cond2 = Restrictions.neOrIsNotNull("comment", " ");

		criteria.add(Restrictions.and(cond1, cond2));

		List<FifaMatch> list = criteria.list();
		int number = list.size() / 3;

		if (list.size() > 5) {
			int value = new Random().nextInt(list.size() - number);
			list = list.subList(value, value + number);
		}

		return list;
	}

	public Map<WeekAndYear, List<FifaMatch>> getMatchesPerWeek(Integer opponent1, Integer opponent2) {
		List<FifaMatch> matches = getAllMatches(opponent1,
				fifaPlayerDao.getPlayer(opponent2.toString()).getUser().getUsername(), true);
		return matches.stream().collect(Collectors.groupingBy(FifaMatch::getWeekDateCreation));
	}

	public Map<MonthAndYear, List<FifaMatch>> getMatchesPerMonth(Integer opponent1, Integer opponent2) {
		List<FifaMatch> matches = getAllMatches(opponent1,
				fifaPlayerDao.getPlayer(opponent2.toString()).getUser().getUsername(), true);
		return matches.stream().collect(Collectors.groupingBy(FifaMatch::getMonthDateCreation));
	}

	public Long addMatch(FifaMatch match) {
		return (Long) getSession().save(match);
	}

	public Map<MonthAndYear, List<FifaMatch>> getMatchesPerMonth(Integer playerId) {
		List<FifaMatch> matches = getAllMatches(playerId, true);
		return matches.stream().collect(Collectors.groupingBy(FifaMatch::getMonthDateCreation));

	}

	public Map<WeekAndYear, List<FifaMatch>> getMatchesPerWeek(Integer playerId) {
		List<FifaMatch> matches = getAllMatches(playerId, true);
		return matches.stream().collect(Collectors.groupingBy(FifaMatch::getWeekDateCreation));
	}

	@SuppressWarnings("unchecked")
	public FifaMatch getMatch(Long id) {
		Criteria criteria = getSession().createCriteria(FifaMatch.class);
		criteria.add(Restrictions.eq("id", id));
		List<FifaMatch> matches = criteria.list();

		if (matches.isEmpty()) {
			return null;
		} else {
			return matches.get(0);
		}
	}
}
