package inciobot.bot_backend.model.fifa;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import inciobot.bot_backend.constants.ITableNames;
import inciobot.bot_backend.enums.TypePlayerStats;

@Entity
@EntityListeners(PlayerStats.class)
@Table(name = ITableNames.PLAYER_STATS)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class PlayerStats extends ChallengeReport {
	private Long id;
	private Player player;
	private Date fromDate;
	private Date toDate;
	private TypePlayerStats typeStats;

	@Id
	@GeneratedValue
	@Column(name = "player_stats_id")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne
	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	@Enumerated(EnumType.STRING)
	public TypePlayerStats getTypeStats() {
		return typeStats;
	}

	public void setTypeStats(TypePlayerStats typeStats) {
		this.typeStats = typeStats;
	}

}
