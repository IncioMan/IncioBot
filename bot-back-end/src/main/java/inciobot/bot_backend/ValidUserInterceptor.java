package inciobot.bot_backend;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import inciobot.bot_backend.annotations.BotManager;
import inciobot.bot_backend.annotations.ValidUser;
import inciobot.bot_backend.exceptions.NoValidUserException;
import inciobot.bot_backend.model.Update;
import inciobot.bot_backend.services.IService;

@Service
public class ValidUserInterceptor implements MethodInterceptor {

	@Autowired
	private IService service;

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Object target = invocation.getThis();

		if (AopUtils.isJdkDynamicProxy(target)) {
			target = ((Advised) target).getTargetSource().getTarget();
		}

		if (invocation.getThis().getClass().isAnnotationPresent(BotManager.class)
				|| target.getClass().isAnnotationPresent(BotManager.class)) {

			Method method = invocation.getMethod();
			// Annotation[][] parameterAnnotations =
			// method.getParameterAnnotations();
			Set<Annotation> methodAnnotations = new HashSet<>(Arrays.asList(method.getDeclaredAnnotations()));

			if (AopUtils.isJdkDynamicProxy(invocation.getThis())) {
				// Method concreteMethod = getConcreteMethod(target,
				// invocation);
				// enhanceParametersAnnotations(parameterAnnotations,
				// concreteMethod);
				// Annotation[] concreteMethodAnnotations =
				// concreteMethod.getDeclaredAnnotations();
				// methodAnnotations.addAll(Arrays.asList(concreteMethodAnnotations));
			}

			ValidUser validUser = null;
			for (Annotation annotation : methodAnnotations) {
				if (annotation instanceof ValidUser) {
					validUser = (ValidUser) annotation;
					break;
				}
			}

			if (validUser != null) {
				Object[] args = invocation.getArguments();

				boolean found = false;
				for (int i = 0; i < args.length; i++) {
					Object arg = args[i];
					if (arg == null) {
						continue;
					}

					if (arg instanceof Update) {
						checkValidUser(validUser, (Update) arg);
						found = true;
						break;
					}
				}
				if (!found) {
					System.err.println(
							"WARNING: @ValidUser on method without Update as parameter won't have effect. Target: ["
									+ target.toString() + "] + Method: [" + method.getName() + "]");
				}
			}
		}

		return invocation.proceed();
	}

	private void checkValidUser(ValidUser validUser, Update update) throws NoValidUserException {
		if (!service.isUserActive(update.getMessage().getFrom().getId())) {
			throw new NoValidUserException();
		}
	}
}
