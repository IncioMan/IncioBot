package inciobot.bot_backend.model.fifa;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import inciobot.bot_backend.constants.ITableNames;
import inciobot.bot_backend.enums.ChartType;

@Entity
@Table(name = ITableNames.FIFA_CHART)
public class FifaChart {
	private Long id;
	private Date dateCreation;
	private List<PlayerPosition> playerPositions;
	private boolean upToDate;
	private ChartType type;

	@Id
	@GeneratedValue
	@Column(name = "fifa_chart_id")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getDateCreation() {
		return dateCreation;
	}

	public void setDateCreation(Date dateCreation) {
		this.dateCreation = dateCreation;
	}

	@OneToMany(mappedBy = "chart")
	public List<PlayerPosition> getPlayerPositions() {
		return playerPositions;
	}

	public void setPlayerPositions(List<PlayerPosition> playerPositions) {
		this.playerPositions = playerPositions;
	}

	public boolean isUpToDate() {
		return upToDate;
	}

	public void setUpToDate(boolean upToDate) {
		this.upToDate = upToDate;
	}

	@Enumerated(EnumType.STRING)
	public ChartType getType() {
		return type;
	}

	public void setType(ChartType type) {
		this.type = type;
	}

}
