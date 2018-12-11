package inciobot.bot_backend.model;

public class Update {
	private String updateid;
	private Message message;
	private InlineQuery inlinequery;

	public Update() {
		super();
	}

	public String getUpdateid() {
		return updateid;
	}

	public void setUpdateid(String updateid) {
		this.updateid = updateid;
	}

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

	public InlineQuery getInlinequery() {
		return inlinequery;
	}

	public void setInlinequery(InlineQuery inlinequery) {
		this.inlinequery = inlinequery;
	}

}
