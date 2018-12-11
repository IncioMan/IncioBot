package inciobot.bot_backend.model;

public class Message {
	private Integer message_id;
	private User from;
	private Integer date;
	private Chat chat;
	private User forward_from;
	private Chat forward_from_chat;
	private Integer forward_date;
	private Message reply_to_message;
	private Integer edit_date;
	private String text;

	// private MessageEntity[] entities;
	// private Audio audio;
	// private Document document;
	// private PhotoSize[] photo;
	// private Sticker sticker;
	// private Video video;
	// private Voice voice;
	// private String caption;
	// private Contact contact;
	// private Location location;
	// private Venue venue;
	// private User new_chat_member;
	// private User left_chat_member;
	// private String new_chat_title;
	// private PhotoSize[] new_chat_photo;
	// private Boolean delete_chat_photo;
	// private Boolean group_chat_created;
	// private Boolean supergroup_chat_created;
	// private Boolean channel_chat_created;
	// private Long migrate_to_chat_id;
	// private Long migrate_from_chat_id;
	// private Message pinned_message;
	public Message() {
		super();
	}

	public Integer getMessage_id() {
		return message_id;
	}

	public void setMessage_id(Integer message_id) {
		this.message_id = message_id;
	}

	public User getFrom() {
		return from;
	}

	public void setFrom(User from) {
		this.from = from;
	}

	public Integer getDate() {
		return date;
	}

	public void setDate(Integer date) {
		this.date = date;
	}

	public Chat getChat() {
		return chat;
	}

	public void setChat(Chat chat) {
		this.chat = chat;
	}

	public User getForward_from() {
		return forward_from;
	}

	public void setForward_from(User forward_from) {
		this.forward_from = forward_from;
	}

	public Chat getForward_from_chat() {
		return forward_from_chat;
	}

	public void setForward_from_chat(Chat forward_from_chat) {
		this.forward_from_chat = forward_from_chat;
	}

	public Integer getForward_date() {
		return forward_date;
	}

	public void setForward_date(Integer forward_date) {
		this.forward_date = forward_date;
	}

	public Message getReply_to_message() {
		return reply_to_message;
	}

	public void setReply_to_message(Message reply_to_message) {
		this.reply_to_message = reply_to_message;
	}

	public Integer getEdit_date() {
		return edit_date;
	}

	public void setEdit_date(Integer edit_date) {
		this.edit_date = edit_date;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
