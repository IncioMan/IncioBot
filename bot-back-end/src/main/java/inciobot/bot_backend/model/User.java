package inciobot.bot_backend.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import inciobot.bot_backend.constants.ITableNames;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = ITableNames.USER)
public class User {
	private Integer id;
	private String first_name;
	private String last_name;
	private String username;

	public User() {
		super();
	}

	@Id
	@Column(name = "user_id")
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFirst_name() {
		return first_name;
	}

	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}

	public String getLast_name() {
		return last_name;
	}

	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
