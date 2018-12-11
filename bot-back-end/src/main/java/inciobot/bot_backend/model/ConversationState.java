package inciobot.bot_backend.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import inciobot.bot_backend.constants.ITableNames;

@Entity
@Table(name = ITableNames.CONVERSATION_STATE)
@Inheritance(strategy = InheritanceType.JOINED)
public class ConversationState {
	private Long id;
	private User user;

	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
