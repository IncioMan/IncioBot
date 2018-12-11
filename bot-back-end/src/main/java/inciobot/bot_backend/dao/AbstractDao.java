package inciobot.bot_backend.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class AbstractDao {
	@Autowired
	protected SessionFactory sessionFactory;

	protected SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	protected Session getSession() {
		return sessionFactory.getCurrentSession();
	}
}
