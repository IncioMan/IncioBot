package inciobot.bot_backend.model.fifa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import inciobot.bot_backend.constants.ITableNames;

@Entity
@Table(name = ITableNames.PLAYER_POSITION)
public class PlayerPosition {
	private Long id;
	private Player player;
	private Integer position;
	private FifaChart chart;

	@Id
	@GeneratedValue
	@Column(name = "player_pos_id")
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

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	@ManyToOne
	public FifaChart getChart() {
		return chart;
	}

	public void setChart(FifaChart chart) {
		this.chart = chart;
	}

}
