package inciobot.bot_backend.model.fifa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import inciobot.bot_backend.constants.ITableNames;
import inciobot.bot_backend.enums.TypeComparison;
import inciobot.bot_backend.enums.TypeStatistic;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = ITableNames.FIFA_STATISTIC_REQUEST)
public class FifaStatisticRequest extends AbstractToComplete {
	private Long id;
	private Player opponent1;
	private Player opponent2;
	private TypeComparison comparison;
	private TypeStatistic typeStatistic;

	public FifaStatisticRequest() {
		super();
	}

	@Id
	@GeneratedValue
	@Column(name = "fifa_stat_req_id")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne
	public Player getOpponent1() {
		return opponent1;
	}

	public void setOpponent1(Player opponent1) {
		this.opponent1 = opponent1;
	}

	@ManyToOne
	public Player getOpponent2() {
		return opponent2;
	}

	public void setOpponent2(Player opponent2) {
		this.opponent2 = opponent2;
	}

	@Enumerated(EnumType.STRING)
	public TypeComparison getComparison() {
		return comparison;
	}

	public void setComparison(TypeComparison comparison) {
		this.comparison = comparison;
	}

	@Enumerated(EnumType.STRING)
	public TypeStatistic getTypeStatistic() {
		return typeStatistic;
	}

	public void setTypeStatistic(TypeStatistic typeStatistic) {
		this.typeStatistic = typeStatistic;
	}
}
