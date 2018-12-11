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

import com.pengrad.telegrambot.model.Chat.Type;

import inciobot.bot_backend.annotations.AccessPolicy;
import inciobot.bot_backend.annotations.BotManager;
import inciobot.bot_backend.annotations.AccessPolicy.ChatType;
import inciobot.bot_backend.exceptions.GroupChatPolicyViolatedException;
import inciobot.bot_backend.exceptions.PrivateChatPolicyViolatedException;
import inciobot.bot_backend.model.Update;

public class AccessPolicyInterceptor implements MethodInterceptor {

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

			AccessPolicy policy = null;
			for (Annotation annotation : methodAnnotations) {
				if (annotation instanceof AccessPolicy) {
					policy = (AccessPolicy) annotation;
					break;
				}
			}

			if (policy != null) {
				Object[] args = invocation.getArguments();

				boolean found = false;
				for (int i = 0; i < args.length; i++) {
					Object arg = args[i];
					if (arg == null) {
						continue;
					}

					if (arg instanceof Update) {
						checkAccessPolicy(policy, (Update) arg);
						found = true;
						break;
					}
				}
				if (!found) {
					System.err.println(
							"WARNING: @AccessPolicy on method without Update as parameter won't have effect. Target: ["
									+ target.toString() + "] + Method: [" + method.getName() + "]");
				}
			}
		}

		return invocation.proceed();
	}

	private void checkAccessPolicy(AccessPolicy policy, Update update) throws Exception {
		if (policy.value().equals(ChatType.ALL))
			return;

		if (policy.value().equals(ChatType.GROUP)) {
			if (!update.getMessage().getChat().getType().equals(Type.group)) {
				throw new GroupChatPolicyViolatedException();
			}
		}
		if (policy.value().equals(ChatType.PRIVATE)) {
			if (!update.getMessage().getChat().getType().equals(Type.Private)) {
				throw new PrivateChatPolicyViolatedException();
			}
		}
	}
}
