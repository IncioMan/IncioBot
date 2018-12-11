package inciobot.bot_backend.bot.manager;

import org.springframework.stereotype.Component;

import inciobot.bot_backend.annotations.AccessPolicy;
import inciobot.bot_backend.annotations.AccessPolicy.ChatType;
import inciobot.bot_backend.bot.AbstractBotController;
import inciobot.bot_backend.constants.ICommands;
import inciobot.bot_backend.constants.IMessage;
import inciobot.bot_backend.enums.FifaConversationFirstStage;
import inciobot.bot_backend.model.Update;
import inciobot.bot_backend.model.fifa.Player;

@Component
public class RegistrationFifaManager extends AbstractFifaManager {

	public RegistrationFifaManager() {
	}

	public AbstractBotController getBot() {
		return bot;
	}

	public void setBot(AbstractBotController bot) {
		this.bot = bot;
	}

	@AccessPolicy(ChatType.PRIVATE)
	public void registrationRequest(Update update) {
		currentUpdate = update;

		service.setFirstStageConversation(bot.getUserIdentifier(currentUpdate), FifaConversationFirstStage.REGISTER);

		switch (bot.getTextLowerCaseMessage(currentUpdate)) {
		case ICommands.REGISTER:
			registerNewUser();
			break;

		case ICommands.DELETE_REGISTRATION:
			deleteRegistration();
			break;

		case ICommands.UPDATE_USERNAME:
			updateUsername();
			break;

		default:
			break;
		}

	}

	private void updateUsername() {
		if (!service.isUserPresent(currentUpdate.getMessage().getFrom().getId())) {
			bot.sendMessage(getChatId(currentUpdate), IMessage.ALREADY_UNREGISTERED);
			return;
		}

		Player player = service.getPlayer(bot.getUserIdentifier(currentUpdate).toString());

		if (player != null) {
			player.getUser().setUsername(bot.getUsername(currentUpdate));
			service.updatePlayer(player);
			bot.sendMessage(getChatId(currentUpdate),
					IMessage.USERNAME_UPDATED + ": " + bot.getUsername(currentUpdate));
		}
	}

	private void registerNewUser() {
		if (service.isUserPresent(currentUpdate.getMessage().getFrom().getId())) {
			if (service.isUserActive(currentUpdate.getMessage().getFrom().getId()))
				bot.sendMessage(getChatId(currentUpdate), IMessage.ALREADY_REGISTERED);
			else {
				try {
					service.activatePlayer(currentUpdate.getMessage().getFrom().getId());
				} catch (Exception e) {
					bot.sendMessage(getChatId(currentUpdate), IMessage.REGISTRATION_ERROR);
					return;
				}
				bot.sendMessage(getChatId(currentUpdate), IMessage.WELCOME_BACK_FIFA);
			}
			return;
		}

		if (currentUpdate.getMessage().getFrom().getUsername() == null) {
			bot.sendMessage(getChatId(currentUpdate), IMessage.USERNAME_NEEDED);
			return;
		}

		try {
			Player player = new Player();
			player.setUser(currentUpdate.getMessage().getFrom());
			player.setActive(true);
			player.setChatId(Long.parseLong(getChatId(currentUpdate)));

			service.savePlayer(player);
		} catch (Exception e) {
			bot.sendMessage(getChatId(currentUpdate), IMessage.REGISTRATION_ERROR);
			e.printStackTrace();
		}
		bot.sendMessage(getChatId(currentUpdate), IMessage.REGISTRATION_SUCCESS + "\n" + IMessage.YOUR_USERNAME + ": "
				+ currentUpdate.getMessage().getFrom().getUsername());

	}

	private void deleteRegistration() {
		if (!service.isUserPresent(currentUpdate.getMessage().getFrom().getId())) {
			bot.sendMessage(getChatId(currentUpdate), IMessage.ALREADY_UNREGISTERED);
			return;
		}

		try {
			service.disablePlayer(currentUpdate.getMessage().getFrom().getId());
		} catch (Exception exception) {
			bot.sendMessage(getChatId(currentUpdate), IMessage.UNREGISTRATION_ERROR);
		}
		bot.sendMessage(getChatId(currentUpdate), IMessage.UNREGISTRATION_SUCCESS);
	}

	private String getChatId(Update currentUpdate) {
		return currentUpdate.getMessage().getChat().getId().toString();
	}
}
