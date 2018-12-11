package inciobot.bot_backend.model.fifa;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractToComplete {
	protected Player creator;
	protected boolean completed;
	protected Date dateCreation;

	public AbstractToComplete() {
		completed = false;
	}

	@ManyToOne
	public Player getCreator() {
		return creator;
	}

	public void setCreator(Player creator) {
		this.creator = creator;
	}

	@Column(name = "completed")
	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public Date getDateCreation() {
		return dateCreation;
	}

	public void setDateCreation(Date dateCreation) {
		this.dateCreation = dateCreation;
	}

}
