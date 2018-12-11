package inciobot.bot_backend.model.fifa;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import inciobot.bot_backend.constants.ITableNames;

@Entity
@Table(name = ITableNames.INVITATION_TO_PLAY)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class InvitationToPlay extends AbstractToComplete {
	private Long id;
	private Player sender;
	private Player receiver;
	private Date dateMatch;
	private boolean confirmed;
	private String comment;
	private String reply;

	public InvitationToPlay() {
		super();
		confirmed = false;
		comment = "";
	}

	@Id
	@GeneratedValue
	@Column(name = "invitation_to_play_id")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@OneToOne
	public Player getReceiver() {
		return receiver;
	}

	public void setReceiver(Player receiver) {
		this.receiver = receiver;
	}

	public Date getDateMatch() {
		return dateMatch;
	}

	public void setDateMatch(Date dateMatch) {
		this.dateMatch = dateMatch;
	}

	public boolean isConfirmed() {
		return confirmed;
	}

	public void setConfirmed(boolean confirmed) {
		this.confirmed = confirmed;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Transient
	public String getRappresentation() {
		StringBuilder retVal = new StringBuilder();
		retVal.append("Da: " + getCreator().getUser().getUsername());
		retVal.append("\nGiorno e ora: " + new SimpleDateFormat("dd-MM-YYYY HH:mm").format(dateMatch));
		if (!comment.equals("")) {
			retVal.append("\nCommento: " + comment);
		}
		return retVal.toString();
	}

	public String getReply() {
		return reply;
	}

	public void setReply(String reply) {
		this.reply = reply;
	}

	public void setSender(Player sender) {
		this.sender = sender;
	}

	@ManyToOne
	public Player getSender() {
		return sender;
	}

}
