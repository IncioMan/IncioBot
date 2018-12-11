package inciobot.bot_backend.utils.fifa;

import java.util.List;

import org.springframework.stereotype.Component;

import inciobot.bot_backend.model.fifa.ChallengeReport;
import inciobot.bot_backend.model.fifa.FifaMatch;

@Component
public class FifaStatisticsGenerator {

	public ChallengeReport getReportFromMatches(Integer firstOpponentId, List<FifaMatch> matches) {
		ChallengeReport report = new ChallengeReport();

		if (matches == null)
			return report;
		report.setMatchesSize(matches.size());

		for (FifaMatch m : matches) {
			boolean team1;
			team1 = m.getTeam1().getPlayers().stream().anyMatch(p -> p.getUser().getId().equals(firstOpponentId));
			if (m.getTeam1().getGoals() == m.getTeam2().getGoals()) {
				report.increaseDraw();
			} else if (m.getTeam1().getGoals() > m.getTeam2().getGoals()) {
				if (team1)
					report.increaseWon();
				else
					report.increaseLost();
			} else if (m.getTeam1().getGoals() < m.getTeam2().getGoals()) {
				if (!team1)
					report.increaseWon();
				else
					report.increaseLost();
			}
			// if (m.getTeam1().getGoals() == 0) {
			// if (team1) {
			// report.increaseMatchNoScore();
			// } else {
			// report.increaseMatchNoGoalAgainst();
			// }
			// }
			// if (m.getTeam2().getGoals() == 0) {
			// if (team1) {
			// report.increaseMatchNoGoalAgainst();
			// } else {
			// report.increaseMatchNoScore();
			// }
			// }
			if (team1) {
				report.increaseGoalAgainst(m.getTeam2().getGoals());
				report.increaseGoalScored(m.getTeam1().getGoals());
			} else {
				report.increaseGoalAgainst(m.getTeam1().getGoals());
				report.increaseGoalScored(m.getTeam2().getGoals());
			}
		}
		report.onLoad();
		return report;
	}

	public ChallengeReport getReportFromMatches(Integer userIdentifier, String opponentUsername,
			List<FifaMatch> matches) {
		ChallengeReport report = new ChallengeReport();

		if (matches == null)
			return report;
		report.setMatchesSize(matches.size());

		for (FifaMatch m : matches) {
			boolean team1;
			team1 = m.getTeam1().getPlayers().stream().anyMatch(p -> p.getUser().getId().equals(userIdentifier));
			if (m.getTeam1().getGoals() == m.getTeam2().getGoals()) {
				report.increaseDraw();
			} else if (m.getTeam1().getGoals() > m.getTeam2().getGoals()) {
				if (team1)
					report.increaseWon();
				else
					report.increaseLost();
			} else if (m.getTeam1().getGoals() < m.getTeam2().getGoals()) {
				if (!team1)
					report.increaseWon();
				else
					report.increaseLost();
			}
			// if (m.getTeam1().getGoals() == 0) {
			// if (team1) {
			// report.increaseMatchNoScore();
			// } else {
			// report.increaseMatchNoGoalAgainst();
			// }
			// }
			// if (m.getTeam2().getGoals() == 0) {
			// if (team1) {
			// report.increaseMatchNoGoalAgainst();
			// } else {
			// report.increaseMatchNoScore();
			// }
			// }
			if (team1) {
				report.increaseGoalAgainst(m.getTeam2().getGoals());
				report.increaseGoalScored(m.getTeam1().getGoals());
			} else {
				report.increaseGoalAgainst(m.getTeam1().getGoals());
				report.increaseGoalScored(m.getTeam2().getGoals());
			}
		}
		report.onLoad();
		return report;

	}

	public void getReportFromMatches(Integer userIdentifier, List<FifaMatch> matches, ChallengeReport report) {
		if (matches == null)
			return;

		report.setMatchesSize(matches.size());

		for (FifaMatch m : matches) {
			boolean team1;
			team1 = m.getTeam1().getPlayers().stream().anyMatch(p -> p.getUser().getId().equals(userIdentifier));
			if (m.getTeam1().getGoals() == m.getTeam2().getGoals()) {
				report.increaseDraw();
			} else if (m.getTeam1().getGoals() > m.getTeam2().getGoals()) {
				if (team1)
					report.increaseWon();
				else
					report.increaseLost();
			} else if (m.getTeam1().getGoals() < m.getTeam2().getGoals()) {
				if (!team1)
					report.increaseWon();
				else
					report.increaseLost();
			}
			// if (m.getTeam1().getGoals() == 0) {
			// if (team1) {
			// report.increaseMatchNoScore();
			// } else {
			// report.increaseMatchNoGoalAgainst();
			// }
			// }
			// if (m.getTeam2().getGoals() == 0) {
			// if (team1) {
			// report.increaseMatchNoGoalAgainst();
			// } else {
			// report.increaseMatchNoScore();
			// }
			// }
			if (team1) {
				report.increaseGoalAgainst(m.getTeam2().getGoals());
				report.increaseGoalScored(m.getTeam1().getGoals());
			} else {
				report.increaseGoalAgainst(m.getTeam1().getGoals());
				report.increaseGoalScored(m.getTeam2().getGoals());
			}
		}
		report.onLoad();
	}
}
