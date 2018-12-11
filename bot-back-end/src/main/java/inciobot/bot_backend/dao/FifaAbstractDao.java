package inciobot.bot_backend.dao;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import inciobot.bot_backend.model.fifa.AbstractToComplete;
import inciobot.bot_backend.model.fifa.Player;
import net.jodah.typetools.TypeResolver;

@Repository
@Transactional
public class FifaAbstractDao<T extends AbstractToComplete> extends AbstractDao {

	@Autowired
	private FifaPlayerDao fifaPlayerDao;

	Class<T> type;

	public FifaAbstractDao(Class<T> type) {
		this.type = type;
	}

	@SuppressWarnings("unchecked")
	public FifaAbstractDao() {
		Class<?>[] typeArgs = TypeResolver.resolveRawArguments(FifaAbstractDao.class, getClass());
		type = (Class<T>) typeArgs[0];
	}

	@SuppressWarnings("unchecked")
	public List<T> getCreated(String userId, boolean completed) {
		Criteria criteria = getSession().createCriteria(type);

		criteria.createAlias("creator.user", "u");
		criteria.add(Restrictions.eq("u.id", Integer.parseInt(userId)));
		criteria.add(Restrictions.eq("completed", completed));

		return criteria.list();
	}

	public void deleteUncompleted(String userId) {
		if (userId == null)
			return;

		List<T> created = getCreated(userId, false);

		if (!created.isEmpty())
			created.stream().forEach(m -> getSession().delete(m));
	}

	public T getNewOrExisting(Integer creatorId) {
		T item = null;

		List<T> items = getCreated(creatorId.toString(), false);
		if (items.size() > 0) {
			item = items.get(0);
		} else {
			try {
				item = type.newInstance();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return item;
	}

	public void createNew(Integer userIdentifier) {
		T item = null;

		if (userIdentifier == null)
			return;

		Player player = fifaPlayerDao.getPlayer(userIdentifier.toString());
		if (player == null)
			return;

		List<T> items = getCreated(userIdentifier.toString(), false);
		if (items.size() > 0) {
			item = items.get(0);
		} else {
			try {
				item = type.newInstance();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
		}

		item.setCreator(player);
		item.setDateCreation(new Date());
		getSession().saveOrUpdate(item);
	}
}
