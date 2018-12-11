package inciobot.bot_backend.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import inciobot.bot_backend.enums.FifaConversationFirstStage;
import inciobot.bot_backend.enums.FifaConversationSecondStage;
import inciobot.bot_backend.model.fifa.FifaConversationState;
import inciobot.bot_backend.model.fifa.Player;

@Repository
@Transactional
public class FifaConversationDao extends AbstractDao {

	@Autowired
	private FifaPlayerDao fifaPlayerDao;

	@Autowired
	private FifaMatchDao fifaMatchDao;

	@Autowired
	private FifaInvitationDao fifaInvitationDao;

	@Autowired
	private FifaStatisticRequestDao fifaStatisticRequestDao;

	public void setFirstStage(Integer userIdentifier, FifaConversationFirstStage state) {
		if (userIdentifier == null || state == null) {
			return;
		}
		FifaConversationState stateConv = new FifaConversationState();

		List<FifaConversationState> list = getFifaConverations(userIdentifier);

		if (!list.isEmpty()) {
			stateConv = list.get(0);

			if (!state.equals(FifaConversationFirstStage.ADDMATCH)
					&& stateConv.getFirstStage().equals(FifaConversationFirstStage.ADDMATCH)) {
				fifaMatchDao.deleteUncompleted(userIdentifier.toString());
			}

			if (!state.equals(FifaConversationFirstStage.INVITE_TO_PLAY)
					&& stateConv.getFirstStage().equals(FifaConversationFirstStage.INVITE_TO_PLAY)) {
				fifaInvitationDao.deleteUncompleted(userIdentifier.toString());
			}

			if (!state.equals(FifaConversationFirstStage.STATISTICS)
					&& stateConv.getFirstStage().equals(FifaConversationFirstStage.STATISTICS)) {
				fifaStatisticRequestDao.deleteUncompleted(userIdentifier.toString());
			}
		} else {
			Player player = fifaPlayerDao.getPlayer(userIdentifier.toString());

			if (player == null)
				return;

			stateConv.setUser(player.getUser());
		}

		stateConv.setFirstStage(state);
		// stateConv.setSecondStage(null);

		getSession().saveOrUpdate(stateConv);
	}

	public void setSecondStage(Integer userIdentifier, FifaConversationSecondStage state) {
		if (userIdentifier == null || state == null) {
			return;
		}
		FifaConversationState stateConv = new FifaConversationState();

		List<FifaConversationState> list = getFifaConverations(userIdentifier);

		if (!list.isEmpty()) {
			stateConv = list.get(0);
		} else {
			Player player = fifaPlayerDao.getPlayer(userIdentifier.toString());

			if (player == null)
				return;

			stateConv.setUser(player.getUser());
		}

		stateConv.setSecondStage(state);

		getSession().saveOrUpdate(stateConv);
	}

	@SuppressWarnings("unchecked")
	private List<FifaConversationState> getFifaConverations(Integer userIdentifier) {
		Criteria criteria = getSession().createCriteria(FifaConversationState.class);
		criteria.add(Restrictions.eq("user.id", userIdentifier));

		return criteria.list();
	}

	public FifaConversationState getConversationState(Integer userIdentifier) {
		List<FifaConversationState> fifaConverations = getFifaConverations(userIdentifier);
		if (!fifaConverations.isEmpty()) {
			return fifaConverations.get(0);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<FifaConversationState> getAllConversations() {
		Criteria criteria = getSession().createCriteria(FifaConversationState.class);
		criteria.createAlias("user", "u");

		return criteria.list();
	}

	public boolean isUserInState(String username, FifaConversationFirstStage state) {
		FifaConversationState conversationState = getConversationState(username);

		if (conversationState == null)
			return false;

		if (conversationState.getFirstStage().equals(state))
			return true;

		return false;
	}

	private FifaConversationState getConversationState(String username) {
		Player player = fifaPlayerDao.getPlayerByUsername(username);
		if (player == null)
			return null;

		return getConversationState(player.getUser().getId());
	}
}
