package inciobot.bot_backend.model.fifa;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import inciobot.bot_backend.constants.ITableNames;

@Entity
@Table(name = ITableNames.TEAM_PERFORMANCE)
public class TeamPerformance {

	private Set<Player> players;
	private Integer goals;
	private Long id;

	public TeamPerformance() {
		players = new HashSet<>();
	}

	@Id
	@GeneratedValue
	@Column(name = "team_performance_id")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToMany(fetch = FetchType.EAGER)
	public Set<Player> getPlayers() {
		return players;
	}

	public void setPlayers(Set<Player> players) {
		this.players = players;
	}

	public Integer getGoals() {
		return goals;
	}

	public void setGoals(Integer goals) {
		this.goals = goals;
	}
}
