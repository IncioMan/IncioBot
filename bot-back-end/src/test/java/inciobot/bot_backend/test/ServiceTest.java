package inciobot.bot_backend.test;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import inciobot.bot_backend.model.User;
import inciobot.bot_backend.model.fifa.Player;
import inciobot.bot_backend.services.IService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppConfigTest.class })
public class ServiceTest {
	@Autowired
	private IService service;

	private User user;

	@Before
	public void setUp() throws Exception {
		Player player = new Player();
		player.setActive(true);
		player.setChatId(Long.parseLong("9696969"));
		user = new User();
		user.setId(9696969);
		user.setFirst_name("Alex");
		player.setUser(user);

		service.savePlayer(player);
	}

	@Test
	public void userValid() throws Exception {
		assertTrue(service.isUserActive(user.getId()));
	}

	@After
	public void tearDown() throws Exception {
		service.deletePlayer(user.getId());
	}
}
