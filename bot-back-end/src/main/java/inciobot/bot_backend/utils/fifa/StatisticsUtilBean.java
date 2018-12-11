package inciobot.bot_backend.utils.fifa;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import inciobot.bot_backend.enums.TypePlayerStats;
import inciobot.bot_backend.model.fifa.FifaMatch;
import inciobot.bot_backend.model.fifa.Player;
import inciobot.bot_backend.model.fifa.PlayerStats;
import inciobot.bot_backend.services.IService;

//@Profile("initialize_stats")
@Component
public class StatisticsUtilBean {

	@Autowired
	private IService service;

	@Autowired
	private FifaStatisticsGenerator statsGenerator;

	public void postConstruct() {
		deleteAllGeneralStats();
		initializeStats();
	}

	public void initializeStats() {
		for (Player player : service.getAllPlayers()) {
			List<FifaMatch> matches = service.getAllMatches(player.getUser().getId(), true);
			PlayerStats playerStats = new PlayerStats();
			playerStats.setPlayer(player);
			playerStats.setTypeStats(TypePlayerStats.GENERAL);

			statsGenerator.getReportFromMatches(player.getUser().getId(), matches, playerStats);
			service.savePlayerStats(playerStats);
		}
	}

	public void deleteAllGeneralStats() {
		List<PlayerStats> allPlayerStats = service.getAllPlayerStats();
		allPlayerStats.stream().forEach(s -> {
			if (s.getTypeStats().equals(TypePlayerStats.GENERAL)) {
				service.deletePlayerStats(s);
			}
		});
	}
}
