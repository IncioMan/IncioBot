package inciobot.bot_backend.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import inciobot.bot_backend.dao.FifaMatchDao;
import inciobot.bot_backend.dao.FifaPlayerDao;
import inciobot.bot_backend.dao.RefuelingDao;
import inciobot.bot_backend.model.fifa.ChallengeReport;
import inciobot.bot_backend.model.fifa.FifaMatch;
import inciobot.bot_backend.model.fifa.Player;
import inciobot.bot_backend.services.IStatsService;
import inciobot.bot_backend.utils.ModelToDtoConverter;
import inciobot.bot_backend.utils.fifa.FifaStatisticsGenerator;
import inciobot.bot_backend.utils.fifa.MonthAndYear;
import inciobot.bot_backend.utils.fifa.WeekAndYear;
import inciobot.bot_ci.ChallengeReportDto;
import inciobot.bot_ci.Filter;
import inciobot.bot_ci.MatchCount;
import inciobot.bot_ci.MatchCountPerWeekPerUser;
import inciobot.bot_ci.MonthCount;
import inciobot.bot_ci.TimeAggregation;

@Component
public class MainStatsService implements IStatsService {
	@Autowired
	private FifaMatchDao fifaMatchDao;
	@Autowired
	private FifaPlayerDao fifaPlayerDao;
	@Autowired
	private RefuelingDao refuelingDao;
	@Autowired
	private FifaStatisticsGenerator fifaStatisticsGenerator;

	@Override
	public List<MatchCount> getMatchCount() {
		return fifaMatchDao.getMatchCount();
	}

	@Override
	public List<MatchCountPerWeekPerUser> getMatchCountPerWeekPerUser() {
		return fifaMatchDao.getMatchCountPerWeekPerUser();
	}

	@Override
	public List<MatchCountPerWeekPerUser> getMatchWeekDistribution(Filter filter) {
		return fifaMatchDao.getMatchWeekDistribution(filter);
	}

	@Override
	public List<MatchCountPerWeekPerUser> getMatchWeekGoalsDistribution(Filter filter) {
		return fifaMatchDao.getMatchWeekGoalsDistribution(filter);
	}

	@Override
	public List<MonthCount> getPricePerMonth() {
		return refuelingDao.getPricePerMonth();
	}

	@Override
	public List<MonthCount> getDistancePerMonth() {
		return refuelingDao.getDistancePerMonth();
	}

	@Override
	public List<MonthCount> getAvgDistQuantityPerMonth() {
		return refuelingDao.getAvgDistQuantityPerMonth();
	}

	@Override
	public List<ChallengeReportDto> getChallengeReport(String opponent1, String opponent2,
			TimeAggregation timeAggregation) {
		List<ChallengeReport> retVal = new ArrayList<>();
		Player player1 = fifaPlayerDao.getPlayerByUsername(opponent1);
		Player player2 = fifaPlayerDao.getPlayerByUsername(opponent2);

		if (player1 == null || player2 == null) {
			return null;
		}

		switch (timeAggregation) {
		case WEEK:
			Map<WeekAndYear, List<FifaMatch>> matchesPerWeek = fifaMatchDao.getMatchesPerWeek(player1.getUser().getId(),
					player2.getUser().getId());

			matchesPerWeek.keySet().stream().forEach(k -> {
				ChallengeReport report = fifaStatisticsGenerator.getReportFromMatches(player1.getUser().getId(),
						opponent2, matchesPerWeek.get(k));
				report.setWeek(k.getWeek());
				report.setYear(k.getYear());

				retVal.add(report);
			});
			break;
		case MONTH:
			Map<MonthAndYear, List<FifaMatch>> matchesPerMonth = fifaMatchDao
					.getMatchesPerMonth(player1.getUser().getId(), player2.getUser().getId());

			matchesPerMonth.keySet().stream().forEach(k -> {
				ChallengeReport report = fifaStatisticsGenerator.getReportFromMatches(player1.getUser().getId(),
						opponent2, matchesPerMonth.get(k));
				report.setMonth(k.getMonth());
				report.setYear(k.getYear());

				retVal.add(report);
			});
		default:
			break;
		}

		List<ChallengeReportDto> retValDtos = new ArrayList<>();
		retVal.stream().forEach(r -> {
			retValDtos.add(ModelToDtoConverter.convert(r));
		});
		return retValDtos;
	}

	@Override
	public List<ChallengeReportDto> getPlayerReport(String playerUsername, TimeAggregation timeAggregation) {
		List<ChallengeReport> retVal = new ArrayList<>();
		Player player1 = fifaPlayerDao.getPlayerByUsername(playerUsername);

		if (player1 == null) {
			return null;
		}

		switch (timeAggregation) {
		case WEEK:
			Map<WeekAndYear, List<FifaMatch>> matchesPerWeek = fifaMatchDao
					.getMatchesPerWeek(player1.getUser().getId());

			matchesPerWeek.keySet().stream().forEach(k -> {
				ChallengeReport report = fifaStatisticsGenerator.getReportFromMatches(player1.getUser().getId(),
						matchesPerWeek.get(k));
				report.setWeek(k.getWeek());
				report.setYear(k.getYear());

				retVal.add(report);
			});
			break;
		case MONTH:
			Map<MonthAndYear, List<FifaMatch>> matchesPerMonth = fifaMatchDao
					.getMatchesPerMonth(player1.getUser().getId());

			matchesPerMonth.keySet().stream().forEach(k -> {
				ChallengeReport report = fifaStatisticsGenerator.getReportFromMatches(player1.getUser().getId(),
						matchesPerMonth.get(k));
				report.setMonth(k.getMonth());
				report.setYear(k.getYear());

				retVal.add(report);
			});
		default:
			break;
		}

		List<ChallengeReportDto> retValDtos = new ArrayList<>();
		retVal.stream().forEach(r -> {
			retValDtos.add(ModelToDtoConverter.convert(r));
		});
		return retValDtos;
	}

	@Override
	public ChallengeReportDto getOpponentGeneralChallengeStatistics(String opponent1, String opponent2) {
		Player player1 = fifaPlayerDao.getPlayerByUsername(opponent1);
		Player player2 = fifaPlayerDao.getPlayerByUsername(opponent2);

		if (player1 == null || player2 == null) {
			return null;
		}

		ChallengeReport retVal = fifaStatisticsGenerator.getReportFromMatches(player1.getUser().getId(),
				fifaMatchDao.getAllMatches(player1.getUser().getId(), opponent2, true));
		return ModelToDtoConverter.convert(retVal);
	}
}
