package inciobot.bot_backend.utils;

import inciobot.bot_backend.model.fifa.ChallengeReport;
import inciobot.bot_ci.ChallengeReportDto;

public class ModelToDtoConverter {

	public static ChallengeReportDto convert(ChallengeReport model) {
		ChallengeReportDto dto = new ChallengeReportDto();
		dto.setDraw(model.getDraw());
		dto.setMatchesSize(model.getMatchesSize());
		dto.setWon(model.getWon());
		dto.setLost(model.getLost());
		dto.setGoalsAgainst(model.getGoalsAgainst());
		dto.setGoalsScored(model.getGoalsScored());
		dto.setWeek(model.getWeek());
		dto.setMonth(model.getMonth());
		dto.setYear(model.getYear());

		dto.setMatchNoGoalAgainst(model.getMatchNoGoalAgainst());
		dto.setMatchNoScore(model.getMatchNoScore());

		dto.onLoad();
		return dto;
	}

}
