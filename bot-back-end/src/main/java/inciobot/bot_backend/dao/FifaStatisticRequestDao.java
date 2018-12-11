package inciobot.bot_backend.dao;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import inciobot.bot_backend.enums.TypeComparison;
import inciobot.bot_backend.enums.TypeStatistic;
import inciobot.bot_backend.model.fifa.FifaStatisticRequest;
import inciobot.bot_backend.model.fifa.Player;

@Repository
@Transactional
public class FifaStatisticRequestDao extends FifaAbstractDao<FifaStatisticRequest> {
	@Autowired
	private FifaPlayerDao fifaPlayerDao;

	public void setFirstOpponentStatisticReq(Integer requesterId, String firstOpponentUsername) {
		FifaStatisticRequest request = getNewOrExisting(requesterId);

		Player opponent = fifaPlayerDao.getPlayerByUsername(firstOpponentUsername);
		if (opponent == null || request == null)
			return;

		request.setOpponent1(opponent);
		getSession().saveOrUpdate(request);
	}

	public void setStatRequestComparisonType(Integer requesterId, TypeComparison comparison) {
		FifaStatisticRequest request = getNewOrExisting(requesterId);

		if (request == null)
			return;

		request.setComparison(comparison);
		getSession().saveOrUpdate(request);
	}

	public void setSecondOpponentStatisticReq(Integer requesterId, String opponentUsername) {
		FifaStatisticRequest request = getNewOrExisting(requesterId);

		Player opponent = fifaPlayerDao.getPlayerByUsername(opponentUsername);
		if (opponent == null || request == null)
			return;

		request.setOpponent2(opponent);
		getSession().saveOrUpdate(request);
	}

	public FifaStatisticRequest setStatRequestCompleted(Integer requesterId) {
		FifaStatisticRequest request = getNewOrExisting(requesterId);

		if (request == null)
			return null;

		request.setCompleted(true);
		getSession().saveOrUpdate(request);

		return request;
	}

	public void setStatRequestType(Integer requesterId, TypeStatistic type) {
		FifaStatisticRequest request = getNewOrExisting(requesterId);

		if (request == null)
			return;

		request.setTypeStatistic(type);
		getSession().saveOrUpdate(request);
	}

}
