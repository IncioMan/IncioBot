package inciobot.bot_backend.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import inciobot.bot_backend.constants.ICommands;
import inciobot.bot_backend.model.Message;
import inciobot.bot_backend.model.Update;
import inciobot.bot_backend.model.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppConfigTest.class })
public class SendAllMessageTest {

	// @Autowired
	// private FifaDebugManager fifaDebugManager;

	@Test
	public void test() {
		Update update = new Update();
		Message message = new Message();
		message.setText(ICommands.DEBUG[0]);
		User user = new User();
		user.setUsername("92383009");
		user.setId(92383009);
		message.setFrom(user);
		update.setMessage(message);

		// fifaDebugManager.handleRequest(update);
	}
}