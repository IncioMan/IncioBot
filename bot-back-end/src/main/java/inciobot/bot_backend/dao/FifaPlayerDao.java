package inciobot.bot_backend.dao;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import inciobot.bot_backend.model.User;
import inciobot.bot_backend.model.fifa.Player;

@Repository
@Transactional
public class FifaPlayerDao extends AbstractDao {

	public FifaPlayerDao() {
	}

	public boolean isUserPresentOrActive(Integer id) {
		return isUserPresent(id) || isUserActive(id);
	}

	@SuppressWarnings("unchecked")
	public boolean isUserPresent(Integer id) {
		if (id == null)
			return false;
		Criteria criteria = getSession().createCriteria(Player.class);
		criteria.createAlias("user", "u").add(Restrictions.eq("u.id", id));

		List<Player> list = criteria.list();
		return !list.isEmpty();
	}

	public Long savePlayer(Player player) throws Exception {
		if (player == null)
			throw new Exception();
		return (Long) getSession().save(player);
	}

	public void deletePlayer(Player player) throws Exception {
		if (player == null)
			throw new Exception();
		getSession().delete(player);
	}

	@SuppressWarnings("unchecked")
	public void disablePlayer(Integer id) throws Exception {
		Criteria criteria = getSession().createCriteria(Player.class);
		criteria.createAlias("user", "u").add(Restrictions.eq("u.id", id));

		List<Player> players = criteria.list();
		if (players == null || players.size() == 0)
			throw new Exception();

		players.stream().forEach(p -> p.setActive(false));
	}

	@SuppressWarnings("unchecked")
	public void activatePlayer(Integer id) throws Exception {
		Criteria criteria = getSession().createCriteria(Player.class);
		criteria.createAlias("user", "u").add(Restrictions.eq("u.id", id));

		List<Player> players = criteria.list();
		if (players == null || players.size() == 0)
			throw new Exception();

		players.stream().forEach(p -> p.setActive(true));
	}

	@SuppressWarnings("unchecked")
	public List<Player> getPlayers() {
		Criteria criteria = getSession().createCriteria(Player.class);
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<Player> getActivePlayers() {
		Criteria criteria = getSession().createCriteria(Player.class).add(Restrictions.eq("active", true));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<Player> getAllPlayers() {
		Criteria criteria = getSession().createCriteria(Player.class);
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public boolean isUserActive(Integer id) {
		if (id == null)
			return false;
		Criteria criteria = getSession().createCriteria(Player.class);
		criteria.createAlias("user", "u").add(Restrictions.eq("u.id", id)).add(Restrictions.eq("active", true));

		List<Player> players = criteria.list();
		return !players.isEmpty();
	}

	public List<Player> getOtherActivePlayers(Integer id) {
		return getActivePlayers().stream().filter(p -> !p.getUser().getId().equals(id)).collect(Collectors.toList());
	}

	@SuppressWarnings("unchecked")
	public Player getPlayer(String id) {
		if (id == null)
			return null;
		Criteria criteria = getSession().createCriteria(Player.class);
		criteria.createAlias("user", "u").add(Restrictions.eq("u.id", Integer.parseInt(id)));

		List<Player> players = criteria.list();
		if (!players.isEmpty()) {
			return players.get(0);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public Player getPlayerByEmail(String email) {
		if (!StringUtils.isNotEmpty(email))
			return null;

		Criteria criteria = getSession().createCriteria(Player.class);
		criteria.add(Restrictions.eq("email", email));

		List<Player> players = criteria.list();
		if (!players.isEmpty()) {
			return players.get(0);
		}
		return null;
	}

	public void updatePlayer(Player player) {
		if (player == null)
			return;
		getSession().update(player);
	}

	@SuppressWarnings("unchecked")
	public Player getPlayerByUsername(String opponenUsername) {
		if (opponenUsername == null)
			return null;
		Criteria criteria = getSession().createCriteria(Player.class);
		criteria.createAlias("user", "u").add(Restrictions.eq("u.username", opponenUsername));

		List<Player> players = criteria.list();
		if (!players.isEmpty()) {
			return players.get(0);
		}
		return null;
	}

	public void deletePlayer(Integer id) {
		if (id == null)
			return;
		Criteria criteria = getSession().createCriteria(Player.class);
		criteria.createAlias("user", "u").add(Restrictions.eq("u.id", id));

		@SuppressWarnings("unchecked")
		List<Player> players = criteria.list();
		players.stream().forEach(p -> {
			getSession().delete(p);
		});
	}

	public List<String> getAllUsername() {
		List<User> users = getAllPlayers().stream().map(Player::getUser).collect(Collectors.toList());
		List<String> retval = users.stream().map(User::getUsername).collect(Collectors.toList());
		return retval;
	}

	@SuppressWarnings("unchecked")
	public void deletePlayer(Long id) {
		Criteria criteria = getSession().createCriteria(Player.class);
		criteria.add(Restrictions.eq("id", id));

		List<Player> players = criteria.list();
		if (!players.isEmpty()) {
			getSession().delete(players.get(0));
			getSession().delete(players.get(0).getUser());
		}
	}
}
