package inciobot.bot_backend.constants;

public interface ICommands {
	public static String TAKE_ME_HOME = "Take me home";
	public static String LAST_STEP = "Last step";
	public static String STOP_REMINDER_PILL = "Stop reminder, for today";
	public static String START_REMINDER_PILL = "Start reminder";
	public static String START = "/start";
	public static String REGISTER = "/register";
	public static String ADD_MATCH = "/addmatch";
	public static String DELETE_REGISTRATION = "/unregister";
	public static String GET_ALL_PLAYERS = "/getAllPlayers";
	public static String GET_ALL_CONVERSATION_STATE = "/getAllConversations";
	public static String UPDATE_USERNAME = "/updateusername";
	public static String GET_ALL_MATCHES = "/getAllMatches";
	public static String DELETE_MATCH = "/deletematch";
	public static String NO_COMMENT = "Nessun commento";
	public static String GET_STATISTICS = "/getstatistics";
	public static String INVITE_TO_PLAY = "/invitetoplay";
	public static String GET_MATCH_PIC = "/getmatchpicture";
	public static String GENERAL = "Generale";
	public static String SPECIFIC_USER = "Contro uno specifico utente";
	public static String YES = "Si";
	public static String NO = "No";
	public static String MIE = "Mie";
	public static String OTHER_USER = "Altro utente";
	public static String ALL_MATCHES = "Lista di tutti i match";
	public static String GRID_STATS = "Griglia con statistiche";
	public static String SEND_WEEKLY_REPORT = "/sendWeelkyReport";
	public static String[] DEBUG = { "/debug", "/getAllPlayers", "/getAllConversations", "/getAllMatches",
			"/sendWeelkyReport", "/regenerateStats", "/sendMessageToPlayers", };
}
