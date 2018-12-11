package inciobot.bot_backend.services.impl;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import inciobot.bot_backend.dao.FifaConversationDao;
import inciobot.bot_backend.dao.FifaInvitationDao;
import inciobot.bot_backend.dao.FifaMatchDao;
import inciobot.bot_backend.dao.FifaPlayerDao;
import inciobot.bot_backend.dao.FifaPlayerStatsDao;
import inciobot.bot_backend.dao.FifaStatisticRequestDao;
import inciobot.bot_backend.dao.RefuelingDao;
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
import inciobot.bot_backend.services.IService;
import inciobot.bot_backend.utils.fifa.FifaPictureCreator;

@Component
public class MainServiceImpl implements IService {

	@Autowired
	private FifaConversationDao fifaConversationDao;

	@Autowired
	private FifaPlayerDao fifaPlayerDao;

	@Autowired
	private FifaMatchDao fifaMatchDao;

	@Autowired
	private FifaPictureCreator fifaPictureCreator;

	@Autowired
	private FifaInvitationDao fifaInvitationDao;

	@Autowired
	private FifaStatisticRequestDao fifaStatisticRequestDao;

	@Autowired
	private FifaPlayerStatsDao fifaPlayerStatsDao;

	@Autowired
	private RefuelingDao refuelingDao;

	@Override
	public FifaConversationState getConversationState(Integer userIdentifier) {
		return fifaConversationDao.getConversationState(userIdentifier);
	}

	@Override
	public void setFirstStageConversation(Integer userIdentifier, FifaConversationFirstStage state) {
		fifaConversationDao.setFirstStage(userIdentifier, state);
	}

	@Override
	public List<FifaConversationState> getAllConversations() {
		return fifaConversationDao.getAllConversations();
	}

	@Override
	public List<Player> getPlayers() {
		return fifaPlayerDao.getPlayers();
	}

	@Override
	public List<Player> getOtherActivePlayers(Integer userIdentifier) {
		return fifaPlayerDao.getOtherActivePlayers(userIdentifier);
	}

	@Override
	public void setSecondStageConversation(Integer userIdentifier, FifaConversationSecondStage state) {
		fifaConversationDao.setSecondStage(userIdentifier, state);
	}

	@Override
	public boolean isUserActive(Integer userIdentifier) {
		return fifaPlayerDao.isUserActive(userIdentifier);
	}

	@Override
	public List<Player> getActivePlayers() {
		return fifaPlayerDao.getActivePlayers();
	}

	@Override
	public List<Player> getAllPlayers() {
		return fifaPlayerDao.getAllPlayers();
	}

	@Override
	public Player getPlayer(String id) {
		return fifaPlayerDao.getPlayer(id);
	}

	@Override
	public void updatePlayer(Player player) {
		fifaPlayerDao.updatePlayer(player);
	}

	@Override
	public boolean isUserPresent(Integer id) {
		return fifaPlayerDao.isUserPresent(id);
	}

	@Override
	public void activatePlayer(Integer id) throws Exception {
		fifaPlayerDao.activatePlayer(id);
	}

	@Override
	public Long savePlayer(Player player) throws Exception {
		return fifaPlayerDao.savePlayer(player);
	}

	@Override
	public void disablePlayer(Integer id) throws Exception {
		fifaPlayerDao.disablePlayer(id);
	}

	@Override
	public void createNewFifaMatch(Integer creatorId) {
		fifaMatchDao.createNewFifaMatch(creatorId);
	}

	@Override
	public void setOpponentIntoMatch(Integer creatorId, String opponentId) {
		fifaMatchDao.setOpponentIntoMatch(creatorId, opponentId);
	}

	@Override
	public List<FifaMatch> getAllMatches(boolean completed) {
		return fifaMatchDao.getAllMatches(completed);
	}

	@Override
	public List<FifaMatch> getAllMatches() {
		return fifaMatchDao.getAllMatches(true);
	}

	@Override
	public void setMatchResult(Integer userIdentifier, int team1Goals, int team2Goals) {
		fifaMatchDao.setMatchResult(userIdentifier, team1Goals, team2Goals);
	}

	@Override
	public FifaMatch setMatchCompleted(Integer userIdentifier) {
		return fifaMatchDao.setMatchComplete(userIdentifier);
	}

	@Override
	public List<FifaMatch> getMatchesByCreator(Integer creatorId, boolean completed) {
		return fifaMatchDao.getMatchesByCreator(creatorId, completed);
	}

	@Override
	public FifaMatch deleteMatch(Long matchId) {
		return fifaMatchDao.deleteMatchById(matchId);
	}

	@Override
	public void addMatchComment(Integer userIdentifier, String comment) {
		fifaMatchDao.addMatchComment(userIdentifier, comment);
	}

	public List<FifaMatch> getAllMatches(Integer userIdentifier, boolean completed) {
		return fifaMatchDao.getAllMatches(userIdentifier, true);
	}

	@Override
	public List<FifaMatch> getAllMatches(Integer userIdentifier, String opponentUsername, boolean completed) {
		return fifaMatchDao.getAllMatches(userIdentifier, opponentUsername, true);
	}

	@Override
	public File getMatchPicture(FifaMatch match, boolean dark) throws Exception {
		if (dark)
			return fifaPictureCreator.createPictureFromMatchDark(match);
		else {
			return fifaPictureCreator.createPictureFromMatch(match);
		}
	}

	@Override
	public void createNewInviteToPlay(Integer userIdentifier) {
		fifaInvitationDao.createNew(userIdentifier);
	}

	@Override
	public void setReceiverIntoInvitation(Integer userIdentifier, String recevierId) {
		fifaInvitationDao.setReceiverIntoInvitation(userIdentifier, recevierId);
	}

	@Override
	public void setInvitationDate(Integer userIdentifier, Date date) {
		fifaInvitationDao.setInvitationDate(userIdentifier, date);
	}

	@Override
	public void addInvitationComment(Integer senderId, String comment) {
		fifaInvitationDao.addInvitationComment(senderId, comment);

	}

	@Override
	public InvitationToPlay setInvitationCompleted(Integer senderId) {
		return fifaInvitationDao.setInvitationCompleted(senderId);
	}

	@Override
	public void setInvitationHour(Integer senderId, String hour) {
		fifaInvitationDao.setInvitationHour(senderId, hour);
	}

	@Override
	public InvitationToPlay setResponseToInvitation(Integer userIdentifier, String reply) {
		return fifaInvitationDao.setResponseToInvitation(userIdentifier, reply);
	}

	@Override
	public boolean isUserInState(String username, FifaConversationFirstStage state) {
		return fifaConversationDao.isUserInState(username, state);
	}

	@Override
	public void createNewFifaStatisticRequest(Integer userIdentifier) {
		fifaStatisticRequestDao.createNew(userIdentifier);
	}

	@Override
	public void setFirstOpponentStatisticReq(Integer requesterId, String firstOpponentUsername) {
		fifaStatisticRequestDao.setFirstOpponentStatisticReq(requesterId, firstOpponentUsername);
	}

	@Override
	public void setStatRequestComparisonType(Integer requesterId, TypeComparison comparison) {
		fifaStatisticRequestDao.setStatRequestComparisonType(requesterId, comparison);
	}

	@Override
	public void setSecondOpponentStatisticReq(Integer requesterId, String opponentUsername) {
		fifaStatisticRequestDao.setSecondOpponentStatisticReq(requesterId, opponentUsername);
	}

	@Override
	public void setStatRequestType(Integer requesterIdentifier, TypeStatistic type) {
		fifaStatisticRequestDao.setStatRequestType(requesterIdentifier, type);
	}

	@Override
	public FifaStatisticRequest setStatRequestCompleted(Integer requesterId) {
		return fifaStatisticRequestDao.setStatRequestCompleted(requesterId);
	}

	@Override
	public void savePlayerStats(PlayerStats playerStats) {
		fifaPlayerStatsDao.save(playerStats);
	}

	@Override
	public List<PlayerStats> getAllPlayerStats() {
		return fifaPlayerStatsDao.getAllPlayerStats();
	}

	@Override
	public PlayerStats getPlayerStats(Long playerId, TypePlayerStats type) {
		return fifaPlayerStatsDao.getPlayerStats(playerId, type);
	}

	@Override
	public List<FifaMatch> getMatches(Date from, Date today) {
		return fifaMatchDao.getMatches(from, today);
	}

	@Override
	public List<FifaMatch> getMatches(Date fromTime, Date toTime, Integer userIdentifier) {
		return fifaMatchDao.getMatches(fromTime, toTime, userIdentifier);
	}

	@Override
	public void deletePlayer(Integer id) {
		fifaPlayerDao.deletePlayer(id);

	}

	@Override
	public void deletePlayerStats(PlayerStats s) {
		fifaPlayerStatsDao.deletePlayerStats(s);

	}

	@Override
	public List<FifaMatch> getAllMatches(String username) {
		return fifaMatchDao.getAllMatches(username);
	}

	@Override
	public void addRefueling(Refueling refueling) {
		refuelingDao.addRefueling(refueling);
	}

	@Override
	public List<FifaMatch> getRandomMatchWithComment() {
		return fifaMatchDao.getRandomMatchWithComment();
	}

	@Override
	public Long addMatch(FifaMatch match) {
		return fifaMatchDao.addMatch(match);
	}

	@Override
	public void deletePlayer(Long id) {
		fifaPlayerDao.deletePlayer(id);
	}

	@Override
	public List<PlayerStats> getPlayerStats(Long playerId) {
		return fifaPlayerStatsDao.getPlayerStats(playerId);
	}

	@Override
	public Player getPlayerByEmail(String email) {
		return fifaPlayerDao.getPlayerByEmail(email);
	}

	@Override
	public FifaMatch getMatchById(Long id) {
		return fifaMatchDao.getMatch(id);
	}

	@Override
	public Player getPlayerByUsername(String username) {
		return fifaPlayerDao.getPlayerByUsername(username);
	}
}
