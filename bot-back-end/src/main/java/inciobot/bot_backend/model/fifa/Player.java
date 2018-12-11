package inciobot.bot_backend.model.fifa;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import inciobot.bot_backend.constants.ITableNames;
import inciobot.bot_backend.model.User;

@Entity
@Table(name = ITableNames.PLAYER)
public class Player {
	private Long id;
	private boolean active;
	private User user;
	private Long chatId;
	private String email;

	@OneToOne(cascade = CascadeType.ALL)
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Id
	@GeneratedValue
	@Column(name = "fifa_player_id")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getChatId() {
		return chatId;
	}

	public void setChatId(Long chatId) {
		this.chatId = chatId;
	}

	@Transient
	public String getRappresentation() {
		StringBuilder retVal = new StringBuilder();
		retVal.append(user.getUsername() + " - ");
		retVal.append(getChatId() + " - ");
		retVal.append(isActive());

		return retVal.toString();
	}

	@Column(name = "email")
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
