package inciobot.bot_backend.config;

import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

import inciobot.bot_backend.AccessPolicyInterceptor;
import inciobot.bot_backend.ValidUserInterceptor;
import inciobot.bot_backend.annotations.BotManager;
import inciobot.bot_backend.aspects.LoggingAspect;

@Configuration
@Import(PropertySourcePlaceHolderConfig.class)
@ComponentScan(basePackages = "inciobot.bot_backend")
@EnableScheduling
@EnableAspectJAutoProxy
public class AppConfig {
	@Autowired
	private ApplicationContext appContext;

	@Bean
	public BeanNameAutoProxyCreator beanNameAutoProxyCreator() {
		BeanNameAutoProxyCreator beanNameAutoProxyCreator = new BeanNameAutoProxyCreator();
		String[] botManagerBeans = appContext.getBeanNamesForAnnotation(BotManager.class);
		beanNameAutoProxyCreator.setBeanNames(botManagerBeans);
		beanNameAutoProxyCreator.setProxyTargetClass(true);
		beanNameAutoProxyCreator.setInterceptorNames("access-policy-interceptor", "valid-user-interceptor");
		return beanNameAutoProxyCreator;
	}

	@Bean(name = "access-policy-interceptor")
	public AccessPolicyInterceptor getAccessPolicyInterceptor() {
		return new AccessPolicyInterceptor();
	}

	@Bean(name = "valid-user-interceptor")
	public ValidUserInterceptor getValidUserInterceptor() {
		return new ValidUserInterceptor();
	}

	@Bean
	public LoggingAspect loggingAspect() {
		return new LoggingAspect();
	}
}
