package inciobot.bot_backend.services;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import inciobot.bot_backend.enums.FifaConversationFirstStage;
import inciobot.bot_backend.enums.FifaConversationSecondStage;
import inciobot.bot_backend.enums.TypeComparison;
import inciobot.bot_backend.enums.TypePlayerStats;
import inciobot.bot_backend.enums.TypeStatistic;
import inciobot.bot_backend.model.Refueling;
import inciobot.bot_backend.model.fifa.FifaConversationState;
import inciobot.bot_backend.model.fifa.FifaMatch;
import inciobot.bot_backend.model.fifa.FifaStatisticRequest;
import inciobot.bot_backend.model.fifa.InvitationToPlay;
import inciobot.bot_backend.model.fifa.Player;
import inciobot.bot_backend.model.fifa.PlayerStats;

@Component
public interface IService {

	FifaConversationState getConversationState(Integer userIdentifier);

	void setFirstStageConversation(Integer userIdentifier, FifaConversationFirstStage state);

	List<FifaConversationState> getAllConversations();

	List<Player> getPlayers();

	List<Player> getOtherActivePlayers(Integer userIdentifier);

	void setSecondStageConversation(Integer userIdentifier, FifaConversationSecondStage state);

	boolean isUserActive(Integer userIdentifier);

	List<Player> getActivePlayers();

	Player getPlayer(String id);

	void updatePlayer(Player player);

	boolean isUserPresent(Integer id);

	void activatePlayer(Integer id) throws Exception;

	Long savePlayer(Player player) throws Exception;

	void disablePlayer(Integer id) throws Exception;

	void createNewFifaMatch(Integer userIdentifier);

	void setOpponentIntoMatch(Integer userIdentifier, String opponentId);

	List<FifaMatch> getAllMatches();

	List<FifaMatch> getAllMatches(boolean completed);

	void setMatchResult(Integer userIdentifier, int team1Goals, int team2Goals);

	FifaMatch setMatchCompleted(Integer userIdentifier);

	List<FifaMatch> getMatchesByCreator(Integer creatorId, boolean completed);

	FifaMatch deleteMatch(Long matchId);

	void addMatchComment(Integer userIdentifier, String comment);

	List<FifaMatch> getAllMatches(Integer userIdentifier, boolean completed);

	List<FifaMatch> getAllMatches(Integer userIdentifier, String opponent, boolean completed);

	File getMatchPicture(FifaMatch match, boolean dark) throws Exception;

	void createNewInviteToPlay(Integer userIdentifier);

	void setReceiverIntoInvitation(Integer userIdentifier, String recevierId);

	void setInvitationDate(Integer userIdentifier, Date date);

	void addInvitationComment(Integer senderId, String comment);

	InvitationToPlay setInvitationCompleted(Integer senderId);

	void setInvitationHour(Integer senderId, String hour);

	InvitationToPlay setResponseToInvitation(Integer userIdentifier, String reply);

	boolean isUserInState(String receiverUsername, FifaConversationFirstStage state);

	void createNewFifaStatisticRequest(Integer userIdentifier);

	void setFirstOpponentStatisticReq(Integer userIdentifier, String username);

	void setStatRequestComparisonType(Integer userIdentifier, TypeComparison comparison);

	void setSecondOpponentStatisticReq(Integer userIdentifier, String opponentUsername);

	void setStatRequestType(Integer userIdentifier, TypeStatistic allMatches);

	FifaStatisticRequest setStatRequestCompleted(Integer userIdentifier);

	List<Player> getAllPlayers();

	void savePlayerStats(PlayerStats playerStats);

	List<PlayerStats> getAllPlayerStats();

	PlayerStats getPlayerStats(Long playerId, TypePlayerStats type);

	List<FifaMatch> getMatches(Date from, Date today);

	List<FifaMatch> getMatches(Date fromTime, Date toTime, Integer userIdentifier);

	void deletePlayer(Integer id);

	void deletePlayerStats(PlayerStats s);

	List<FifaMatch> getAllMatches(String username);

	void addRefueling(Refueling refueling);

	List<FifaMatch> getRandomMatchWithComment();

	Long addMatch(FifaMatch match);

	void deletePlayer(Long id);

	List<PlayerStats> getPlayerStats(Long id);

	Player getPlayerByEmail(String email);

	FifaMatch getMatchById(Long id);

	Player getPlayerByUsername(String username);
}
