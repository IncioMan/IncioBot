package inciobot.bot_backend.aspects;

import org.apache.commons.lang3.time.StopWatch;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import inciobot.bot_backend.model.Update;

@Component
@Aspect
public class LoggingAspect {
	@Pointcut("target(inciobot.bot_backend.services.IService)")
	public void serviceMethods() {
	}

	@Pointcut("execution(* inciobot.bot_backend.bot.manager.*.*(..))")
	public void managersMethods() {
	}

	@Around("serviceMethods()")
	public Object logWsCall(ProceedingJoinPoint jp) throws Throwable {
		final String method = jp.getSignature().getName();
		final StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		Object retVal = null;
		try {
			retVal = jp.proceed();
		} catch (final Throwable t) {
			stopWatch.stop();
			System.out.println(
					String.format("Error in execution of method %s. Total time: %s", method, stopWatch.getTime()));
			throw t;
		}

		stopWatch.stop();

		System.out
				.println(String.format("IService: completed %s in %s", method, stopWatch.getTime() / 1000.0 + " sec"));

		return retVal;
	}

	@Around("managersMethods()  && args(update)")
	public Object logMngCall(ProceedingJoinPoint jp, Update update) throws Throwable {
		final String method = jp.getSignature().getName();
		String className = jp.getTarget().getClass().getName();
		String user = "";
		className = className.replace("inciobot.bot_backend.bot.manager.", "");

		if (update != null && update.getMessage() != null && update.getMessage().getFrom() != null)
			user = update.getMessage().getFrom().getUsername() + "(" + update.getMessage().getFrom().getId() + ")";

		final StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		Object retVal = null;
		try {
			retVal = jp.proceed();
		} catch (final Throwable t) {
			stopWatch.stop();
			System.out.println(
					String.format("Error in execution of method %s. Total time: %s", method, stopWatch.getTime()));
			throw t;
		}

		stopWatch.stop();

		System.out.println(String.format("%s: %s completed %s in %s", className, user, method,
				stopWatch.getTime() / 1000.0 + " sec"));

		return retVal;
	}
}
