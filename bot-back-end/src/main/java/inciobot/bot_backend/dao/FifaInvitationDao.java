package inciobot.bot_backend.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import inciobot.bot_backend.constants.ICommands;
import inciobot.bot_backend.model.fifa.InvitationToPlay;
import inciobot.bot_backend.model.fifa.Player;

@Repository
@Transactional
public class FifaInvitationDao extends FifaAbstractDao<InvitationToPlay> {

	@Autowired
	private FifaPlayerDao fifaPlayerDao;

	public void createNewInviteToPlay(Integer creatorId) {
		if (creatorId == null)
			return;

		Player player = fifaPlayerDao.getPlayer(creatorId.toString());
		if (player == null)
			return;

		InvitationToPlay invitation = getNewOrExisting(creatorId);

		invitation.setCreator(player);
		invitation.setSender(player);
		invitation.setDateCreation(new Date());
		getSession().saveOrUpdate(invitation);
	}

	public void setReceiverIntoInvitation(Integer creatorId, String recevierId) {
		if (creatorId == null)
			return;

		Player player = fifaPlayerDao.getPlayerByUsername(recevierId);
		if (player == null)
			return;

		InvitationToPlay invitation = getNewOrExisting(creatorId);

		invitation.setReceiver(player);
		getSession().saveOrUpdate(invitation);
	}

	public void setInvitationDate(Integer creatorId, Date date) {
		if (creatorId == null)
			return;

		InvitationToPlay invitationToPlay = getNewOrExisting(creatorId);
		invitationToPlay.setDateMatch(date);
		getSession().saveOrUpdate(invitationToPlay);
	}

	public void addInvitationComment(Integer creatorId, String comment) {
		if (creatorId == null)
			return;

		InvitationToPlay invitationToPlay = getNewOrExisting(creatorId);
		invitationToPlay.setComment(comment);
		getSession().saveOrUpdate(invitationToPlay);
	}

	public InvitationToPlay setInvitationCompleted(Integer creatorId) {
		if (creatorId == null)
			return null;

		InvitationToPlay invitationToPlay = getNewOrExisting(creatorId);
		invitationToPlay.setCompleted(true);
		getSession().saveOrUpdate(invitationToPlay);
		return invitationToPlay;
	}

	public void setInvitationHour(Integer creatorId, String hour) {
		if (creatorId == null)
			return;

		InvitationToPlay invitationToPlay = getNewOrExisting(creatorId);
		try {
			invitationToPlay.setDateMatch(new SimpleDateFormat("dd-MM-yyyy HH:mm")
					.parse(new SimpleDateFormat("dd-MM-yyyy").format(invitationToPlay.getDateMatch()) + " " + hour));
		} catch (ParseException e) {
			e.printStackTrace();
			return;
		}
		getSession().saveOrUpdate(invitationToPlay);
		return;
	}

	public InvitationToPlay setResponseToInvitation(Integer receiverId, String reply) {
		InvitationToPlay invitation = getInvitationFromReceiver(receiverId, false);

		if (invitation == null)
			return null;

		if (reply.toLowerCase().equals(ICommands.YES.toLowerCase())
				|| reply.toLowerCase().equals(ICommands.NO.toLowerCase())) {
			invitation.setConfirmed(true);
			invitation.setReply(reply);
		}

		getSession().save(invitation);
		return invitation;
	}

	@SuppressWarnings("unchecked")
	private InvitationToPlay getInvitationFromReceiver(Integer receiverId, boolean confirmed) {
		Criteria criteria = getSession().createCriteria(InvitationToPlay.class);

		criteria.createAlias("receiver.user", "u");
		criteria.add(Restrictions.eq("u.id", receiverId));
		criteria.add(Restrictions.eq("confirmed", confirmed));

		List<InvitationToPlay> invitations = criteria.list();
		if (invitations.size() > 0)
			return invitations.get(0);
		else {
			return null;
		}
	}
}
