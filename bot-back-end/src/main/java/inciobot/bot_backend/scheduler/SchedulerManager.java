package inciobot.bot_backend.scheduler;

import java.util.ArrayList;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SchedulerManager {
	private List<Runnable> tasksToExecute;
	private List<Runnable> keepAliveTasks;
	private List<Runnable> oftenTasks;
	private List<Runnable> pillTasks;
	private List<Runnable> weeklyTasks;

	public SchedulerManager() {
		tasksToExecute = new ArrayList<>();
		keepAliveTasks = new ArrayList<>();
		oftenTasks = new ArrayList<>();
		pillTasks = new ArrayList<>();
		weeklyTasks = new ArrayList<>();
	}

	public void addKeepAliveTask(Runnable runnable) {
		keepAliveTasks.add(runnable);
	}

	public void removeKeepAliveTask(Runnable runnable) {
		keepAliveTasks.remove(runnable);
	}

	public void addTaskToExecute(Runnable runnable) {
		tasksToExecute.add(runnable);
	}

	public void removeTaskToExecute(Runnable runnable) {
		tasksToExecute.remove(runnable);
	}

	public void addPillTasks(Runnable runnable) {
		if (!pillTasks.contains(runnable))
			pillTasks.add(runnable);
	}

	public void removePillTasks(Runnable runnable) {
		pillTasks.remove(runnable);
	}

	public void addTaskToOftenExecute(Runnable runnable) {
		if (!oftenTasks.contains(runnable))
			oftenTasks.add(runnable);
	}

	public void removeTaskToOftenExecute(Runnable runnable) {
		oftenTasks.remove(runnable);
	}

	public void addTaskToWeekly(Runnable runnable) {
		weeklyTasks.add(runnable);
	}

	@Scheduled(cron = "0 12 0/1 1/1 * ?")
	private void runTasks() {
		for (Runnable runnable : tasksToExecute) {
			runnable.run();
		}
	}

	// To prevent idling in Heroku
	@Scheduled(cron = "0 0 0/2 1/1 * ?")
	private void keepAlive() {
		for (Runnable runnable : keepAliveTasks) {
			runnable.run();
		}
	}

	// Maps direction
	@Scheduled(cron = "0 0/15  18-19 ? * Mon-Fri")
	private void oftenTasks() {
		for (Runnable runnable : oftenTasks) {
			runnable.run();
		}
	}

	// Pill reminder
	@Scheduled(cron = "0 0/20  19 ? * *")
	private void pillTask() {
		for (Runnable runnable : pillTasks) {
			runnable.run();
		}
	}

	// Week job
	@Scheduled(cron = "0 0 12 ? * Sun")
	private void weekJob() {
		for (Runnable runnable : weeklyTasks) {
			runnable.run();
		}
	}
}
