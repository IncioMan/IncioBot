package inciobot.bot_backend.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import inciobot.bot_backend.enums.TypePlayerStats;
import inciobot.bot_backend.model.fifa.Player;
import inciobot.bot_backend.model.fifa.PlayerStats;

@Repository
@Transactional
public class FifaPlayerStatsDao extends AbstractDao {

	public void save(PlayerStats playerStats) {
		if (playerStats == null)
			return;

		if (playerStats.getTypeStats().equals(TypePlayerStats.GENERAL)) {
			List<PlayerStats> stats = getStatsPerPlayer(playerStats.getPlayer().getId(), TypePlayerStats.GENERAL);

			if (!stats.isEmpty()) {
				playerStats.setId(stats.get(0).getId());
			}
			getSession().clear();
		}

		getSession().saveOrUpdate(playerStats);
	}

	@SuppressWarnings("unchecked")
	private List<PlayerStats> getStatsPerPlayer(Long playerId, TypePlayerStats type) {
		Criteria criteria = getSession().createCriteria(PlayerStats.class)
				.add(Restrictions.eqOrIsNull("player.id", playerId)).add(Restrictions.eq("typeStats", type));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<PlayerStats> getAllPlayerStats() {
		Criteria criteria = getSession().createCriteria(PlayerStats.class);

		return criteria.list();
	}

	public void addDraw(Player player, Integer goalsScored, Integer goalsAgainst) {
		PlayerStats playerStats = null;
		List<PlayerStats> stats = getStatsPerPlayer(player.getId(), TypePlayerStats.GENERAL);
		if (stats.isEmpty()) {
			playerStats = new PlayerStats();
			playerStats.setTypeStats(TypePlayerStats.GENERAL);
			playerStats.setPlayer(player);
		} else {
			playerStats = stats.get(0);
		}

		playerStats.increaseMatchSize();
		playerStats.increaseDraw();
		playerStats.increaseGoalAgainst(goalsAgainst);
		playerStats.increaseGoalScored(goalsScored);
		playerStats.onLoad();

		getSession().saveOrUpdate(playerStats);
	}

	public void addLost(Player player, Integer goalsScored, Integer goalsAgainst) {
		PlayerStats playerStats = null;
		List<PlayerStats> stats = getStatsPerPlayer(player.getId(), TypePlayerStats.GENERAL);
		if (stats.isEmpty()) {
			playerStats = new PlayerStats();
			playerStats.setTypeStats(TypePlayerStats.GENERAL);
			playerStats.setPlayer(player);
		} else {
			playerStats = stats.get(0);
		}

		playerStats.increaseMatchSize();
		playerStats.increaseLost();
		playerStats.increaseGoalAgainst(goalsAgainst);
		playerStats.increaseGoalScored(goalsScored);
		playerStats.onLoad();

		getSession().saveOrUpdate(playerStats);

	}

	public void addWon(Player player, Integer goalsScored, Integer goalsAgainst) {
		PlayerStats playerStats = null;
		List<PlayerStats> stats = getStatsPerPlayer(player.getId(), TypePlayerStats.GENERAL);
		if (stats.isEmpty()) {
			playerStats = new PlayerStats();
			playerStats.setTypeStats(TypePlayerStats.GENERAL);
			playerStats.setPlayer(player);
		} else {
			playerStats = stats.get(0);
		}

		playerStats.increaseMatchSize();
		playerStats.increaseWon();
		playerStats.increaseGoalAgainst(goalsAgainst);
		playerStats.increaseGoalScored(goalsScored);
		playerStats.onLoad();

		getSession().saveOrUpdate(playerStats);

	}

	public void removeDraw(Player player, Integer goalsScored, Integer goalsAgainst) {
		PlayerStats playerStats = null;
		List<PlayerStats> stats = getStatsPerPlayer(player.getId(), TypePlayerStats.GENERAL);
		if (stats.isEmpty()) {
			playerStats = new PlayerStats();
			playerStats.setTypeStats(TypePlayerStats.GENERAL);
			playerStats.setPlayer(player);
		} else {
			playerStats = stats.get(0);
		}

		playerStats.decreaseMatchSize();
		playerStats.decreaseDraw();
		playerStats.decreaseGoalAgainst(goalsAgainst);
		playerStats.decreaseGoalScored(goalsScored);
		playerStats.onLoad();

		getSession().saveOrUpdate(playerStats);
	}

	public void removeLost(Player player, Integer goalsScored, Integer goalsAgainst) {
		PlayerStats playerStats = null;
		List<PlayerStats> stats = getStatsPerPlayer(player.getId(), TypePlayerStats.GENERAL);
		if (stats.isEmpty()) {
			playerStats = new PlayerStats();
			playerStats.setTypeStats(TypePlayerStats.GENERAL);
			playerStats.setPlayer(player);
		} else {
			playerStats = stats.get(0);
		}

		playerStats.decreaseMatchSize();
		playerStats.decreaseLost();
		playerStats.decreaseGoalAgainst(goalsAgainst);
		playerStats.decreaseGoalScored(goalsScored);
		playerStats.onLoad();

		getSession().saveOrUpdate(playerStats);

	}

	public void removeWon(Player player, Integer goalsScored, Integer goalsAgainst) {
		PlayerStats playerStats = null;
		List<PlayerStats> stats = getStatsPerPlayer(player.getId(), TypePlayerStats.GENERAL);
		if (stats.isEmpty()) {
			playerStats = new PlayerStats();
			playerStats.setTypeStats(TypePlayerStats.GENERAL);
			playerStats.setPlayer(player);
		} else {
			playerStats = stats.get(0);
		}

		playerStats.decreaseMatchSize();
		playerStats.decreaseWon();
		playerStats.decreaseGoalAgainst(goalsAgainst);
		playerStats.decreaseGoalScored(goalsScored);
		playerStats.onLoad();

		getSession().saveOrUpdate(playerStats);

	}

	public PlayerStats getPlayerStats(Long playerId, TypePlayerStats type) {
		List<PlayerStats> playerStats = getStatsPerPlayer(playerId, type);
		if (!playerStats.isEmpty())
			return playerStats.get(0);
		return new PlayerStats();
	}

	public void deletePlayerStats(PlayerStats s) {
		if (s != null) {
			getSession().delete(s);
		}
	}

	@SuppressWarnings("unchecked")
	public List<PlayerStats> getPlayerStats(Long playerId) {
		Criteria criteria = getSession().createCriteria(PlayerStats.class)
				.add(Restrictions.eqOrIsNull("player.id", playerId));
		return criteria.list();
	}
}
