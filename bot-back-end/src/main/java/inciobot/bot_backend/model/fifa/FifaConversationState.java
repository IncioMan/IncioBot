package inciobot.bot_backend.model.fifa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import inciobot.bot_backend.constants.ITableNames;
import inciobot.bot_backend.enums.FifaConversationFirstStage;
import inciobot.bot_backend.enums.FifaConversationSecondStage;
import inciobot.bot_backend.model.ConversationState;

@Entity
@Table(name = ITableNames.CONVERSATION_STATE_FIFA)
@PrimaryKeyJoinColumn(name = "fifa_conversation_id")
public class FifaConversationState extends ConversationState {
	private FifaConversationFirstStage firstStage;
	private FifaConversationSecondStage secondStage;

	@Enumerated(EnumType.STRING)
	@Column(name = "first_stage")
	public FifaConversationFirstStage getFirstStage() {
		return firstStage;
	}

	public void setFirstStage(FifaConversationFirstStage firstStage) {
		this.firstStage = firstStage;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "second_stage")
	public FifaConversationSecondStage getSecondStage() {
		return secondStage;
	}

	public void setSecondStage(FifaConversationSecondStage secondStage) {
		this.secondStage = secondStage;
	}

	@Transient
	public String getRappresentation() {
		StringBuilder retVal = new StringBuilder();
		if (getUser() != null)
			retVal.append(getUser().getUsername() + " - ");
		retVal.append(firstStage + " - ");
		retVal.append(secondStage);

		return retVal.toString();
	}
}
