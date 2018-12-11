package inciobot.bot_backend.constants;

public interface IMessage {
	public static String MOM_MESSAGE = "";
	public static String MESSAGE_TO_ME = "Woken Up!";
	public static String LOVE_ANSWER = "Io di più, amore mio";
	public static String WHAT_IS_THIS = "e che mminchia è sta roba?";
	public static String HI_ANSWER = "Salve";
	public static String APOLOGIZE_OWNER = "Scusa padrone";
	public static String LOVE_MESSAGE = "" + IEmoji.RED_HEART;
	public static String NOW_STOP = "mò basta!";
	public static String DURATION_GE_MA = "La durata di percorrenza tratto GrandeEmilia - Marzaglia";
	public static String DURATION_WRK_HOME_VIA_EMILIA = "Tempo di percorrenza \"lavoro-casa\" tramite Via Emilia";
	public static String DURATION_WRK_HOME_FREEWAY = "Tempo di percorrenza \"lavoro-casa\" tramite tangenziale";
	public static String MINUTES = "minuti";
	public static String STOP_DIRECTIONS = "Ricevuto, per oggi niente più info sulle direzioni";
	public static String START_DIRECTIONS = "Bene, attivato la modalità info sulle direzioni!";
	public static String GOOGLE_MAPS_BOT_WELCOME = "Ciao! Benvenuto nel mio personale Bot GoogleMaps "
			+ IEmoji.BIG_SMILE + "\n"
			+ "Questo bot è una utility per avere informazioni sul traffico nei percorsi da me frequentati quotidianamente "
			+ IEmoji.BLUE_CAR;
	public static String TALKING_ABOUT_ME = "Stavate parlando di me?";
	public static String LAVABO = "http://www.deghishop.it/default.asp?l=1&cmd=getProd&cmdID=14874&rr=GoogleMerchant&utm_source=GoogleMerchant&utm_medium=cpc&gclid=CMzyhqOL388CFWUq0wodrekEuQ";
	public static String POST_SUCKS = "ilpost.it SUCKS";
	public static String ILGIOMALE_RULES = "ilfattoquotidaino.it RULES!";
	public static String PILL_MESSAGE = "Ciao amore mio meraviglioso, ricordati la pillola!";
	public static String STOP_REMINDER_PILL = "Ricevuto, per oggi non ti ricorderò più la pillola";
	public static String START_REMINDER_PILL = "Ok, ricomincierò a ricordarti di prendere la pillola";
	public static String FIFA_TOURNAMENT = "Vi siete già registrati nel bot aziendale del torneo di Fifa?\nFatelo subito! @EueiFifaBot "
			+ IEmoji.THUMB_UP_WHITE;
	public static String FIFA_WELCOME = "Benvenuto nel bot di Fifa di EUEI. " + IEmoji.FOOTBALL + "\n"
			+ "Registrandoti potrai inserire i risultati delle partite tenendo traccia delle tue performance e statistiche relative agli scontri con i colleghi."
			+ "\n\nInizia con: \n\n/register - registrati al bot ed inizia a salvare i risultati\n/addmatch - aggiungi una partita contro un avversario\n/invitetoplay - invita un avversario a giocare una partita\n\n...e molto altro!"
			+ "\n\n" + "Che la sfida abbia inizio! " + IEmoji.JOYSTICK;
	public static String ACTION_ONLY_PRIVATE_CHAT = "Per questa operazione è necessario utilizzare una chat privata. Scrivimi a @EueiFifaBot";
	public static String ALREADY_REGISTERED = "Risulti già registrato!";
	public static String REGISTRATION_ERROR = "Errore durante la registrazione, ci scusiamo per il disagio. Riprova!";
	public static String YOUR_USERNAME = "Il tuo username";
	public static String REGISTRATION_SUCCESS = "Registrazione avvenuta con successo";
	public static String SELECT_MATCH_OPPONENT = "Seleziona il tuo avversario";
	public static String ALREADY_UNREGISTERED = "Non risulti registrato.";
	public static String UNREGISTRATION_ERROR = "Problemi durante la disattivazione del tuo account. Riprova";
	public static String UNREGISTRATION_SUCCESS = "Account disattivato. Il tuo storico verrà tuttavia mantenuto nei nostri database";
	public static String WELCOME_BACK_FIFA = "Account riattivato. Bentornato! Tornata voglia di giocare? "
			+ IEmoji.JOYSTICK;
	public static String NEED_REGISTRATION = "Necessaria la registrazione per questa funzionalità";
	public static String USERNAME_NEEDED = "Per effettuare la registrazione è necessario aver impostato uno username nel proprio profilo di Telegram.\nFarlo è semplice!\nApp di Telegram -> Menu scorrimento -> Impostazioni ->  Username";
	public static String INSERT_RESULT = "Inserire ora il risultato nel formato:\nGoal-tuoi - Goal-avversario";
	public static String OPPONENT_NON_VALID = "Avversario inserito non valido " + IEmoji.RED_CROSS;
	public static String RESULT_NON_VALID = "Il risultato inserito non è valido " + IEmoji.RED_CROSS;
	public static String USERNAME_UPDATED = "Username aggiornato";
	public static String NEW_MATCH_ADDED_FROM_USER = "Una nuova partita contro di te è stata registrata dall'utente";
	public static String MATCH_ADDED_SUCCESSFULLY = "Partita aggiunta con successo! " + IEmoji.GREEN_CHECK;
	public static String NO_MATCH_TO_DELETE = "Non è presente nessuna partita da eliminare";
	public static String SELECT_ID_FROM_FOLLOWING = "Specificare l'id della partita che si vuole eliminare, fra le seguenti:";
	public static String ID_NON_VALID = "Id inserito non valido";
	public static String INSERT_MATCH_ID_TO_DELETE = "Inserire l'id della partita da eliminare";
	public static String MATCH_DELETED_SUCCESSFULLY = "Partita eliminata con successo! " + IEmoji.GREEN_CHECK;
	public static String ADD_COMMENT_OR_CLICK_BUTTON_MATCH = "Se desideri puoi aggiungere un commento testuale alla partita."
			+ IEmoji.KNOCK_WHITE + "\nAltrimenti clicca il bottone \"Nessun commento\"";
	public static String ADD_COMMENT_OR_CLICK_BUTTON_INVITATION = "Se desideri puoi aggiungere un commento testuale all'invito."
			+ IEmoji.KNOCK_WHITE + "\nAltrimenti clicca il bottone \"Nessun commento\"";
	public static String STATISTICS_TYPE_NO_VALID = "Tipo di statistica non valido";
	public static String SELECT_STATISTICS_TYPE = "Seleziona il tipo di statistica: " + IEmoji.BAR_CHART;
	public static String SELECT_STATISTICS_OPPONENT = "Seleziona l'utente riguardo il quale vuoi ottenere le statistiche";
	public static String USER_SELECTED_NO_VALID = "L'utente selezionato non è valido. " + IEmoji.RED_CROSS;
	public static String NO_MATCHES_WITH_OPPONENT = "Non vi sono partite registrate con l'utente";
	public static String WON_MATCHES = "Vinte";
	public static String DRAW_MATCHES = "Pareggiate";
	public static String LOST_MATCHES = "Perse";
	public static String MATCHES = "Partite";
	public static String REGISTER_TO_ACCESS_FUNCTIONALITY = "Registrati per accedere a questa funzionalità";
	public static String USER = "L'utente";
	public static String DELETED_MATCH = "ha eliminato la seguente partita";
	public static String REGISTRATION_DATE = "Registrata in data";
	public static String NO_COMMENT = "Nessun commento";
	public static String SHARE_PICTURE = "Puoi usare l'immagine per condividere il risultato con i tuo colleghi!";
	public static String NOT_YET_IMPLEMENTED = "Not yet implemented";
	public static String INSERT_DATE = "Inserire il giorno nel quale si vuole giocare " + IEmoji.JOYSTICK
			+ "\nNel formato: ";
	public static String SELECT_INVITATION_RECEIVER = "Seleziona il destinatario dell'invito " + IEmoji.LETTER[0];
	public static String DATA_NOT_VALID = "La data inserita non è valida! " + IEmoji.RED_CROSS;
	public static String INSERT_HOUR = "Inserire l'orario " + IEmoji.CLOCK[1] + "\nNel formato ";
	public static String INVITATION_SENT = "Invito inviato con successo!";
	public static String NEW_INVITATION_RECEIVED = "Hai ricevuto una nuova richiesta di giocare " + IEmoji.JOYSTICK;
	public static String ACCEPT_OR_NOT = "Accetti oppure no?";
	public static String HOUR_NOT_VALID = "Orario inserito non valido! " + IEmoji.RED_CROSS;
	public static String TO_INVITATION = "all'invito";
	public static String REPLIED_INVITATION = "ha risposto";
	public static String INVALID_INVITATION_ANSWER = "Risposta invalida. Accetti o no l'invito?\nRispondi con "
			+ ICommands.YES + " oppure " + ICommands.NO;
	public static String RECEIVER_NOT_AVAILABLE = "non è disponibile perchè ha già un invito in sospeso";
	public static String INVALID_ACCEPTED = "Invito accettato!";
	public static String INVALID_REFUSED = "Invito rifiutato!";
	public static String WHOSE_STATS = "Di chi vuoi le statistiche?";
	public static String AGAINST_WHO = "Contro chi?";
	public static String SELECT_PLAYER = "Seleziona l'utente";
	public static String TYPE_STATS = "Seleziona il tipo di statistica";
	public static String MATCH_NO_GOAL_AGAINST = "Partite senza goal subiti";
	public static String MATCH_NO_SCORE = "Parite senza aver segnato";
	public static String GOAL_AGAINST = "Goal subiti";
	public static String GOAL_SCORED = "Goal fatti";
	public static String NO_MATCHES_OF_USER = "Nessun match dell'utente";
	public static String STATISTICS_OF_USERS = "Statistiche dell'utente";
	public static String WEEKLY_REPORT = "Report settimanale";
	public static Object NO_MATCH_THIS_WEEK = "Non hai disputato partite questa settimana.\nMantieniti in allenamento! "
			+ IEmoji.JOYSTICK;
	public static String HI_DEVELOPER = "Ciao sviluppatore!";
}