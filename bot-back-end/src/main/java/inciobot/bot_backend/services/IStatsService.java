package inciobot.bot_backend.services;

import java.util.List;

import inciobot.bot_ci.ChallengeReportDto;
import inciobot.bot_ci.Filter;
import inciobot.bot_ci.MatchCount;
import inciobot.bot_ci.MatchCountPerWeekPerUser;
import inciobot.bot_ci.MonthCount;
import inciobot.bot_ci.TimeAggregation;

public interface IStatsService {
	List<MatchCount> getMatchCount();

	List<MatchCountPerWeekPerUser> getMatchCountPerWeekPerUser();

	List<MatchCountPerWeekPerUser> getMatchWeekDistribution(Filter filter);

	List<MatchCountPerWeekPerUser> getMatchWeekGoalsDistribution(Filter filter);

	List<MonthCount> getPricePerMonth();

	List<MonthCount> getDistancePerMonth();

	List<MonthCount> getAvgDistQuantityPerMonth();

	List<ChallengeReportDto> getChallengeReport(String opponent1, String opponent2, TimeAggregation timeAggregation);

	List<ChallengeReportDto> getPlayerReport(String playerUsername, TimeAggregation timeAggregation);

	ChallengeReportDto getOpponentGeneralChallengeStatistics(String opponent1, String opponent2);

}
