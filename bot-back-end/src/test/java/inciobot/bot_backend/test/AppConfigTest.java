package inciobot.bot_backend.test;

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
import inciobot.bot_backend.config.HibernateConfig;
import inciobot.bot_backend.config.PropertySourcePlaceHolderConfig;

@Configuration
@Import({ PropertySourcePlaceHolderConfig.class, HibernateConfig.class })
@ComponentScan(basePackages = { "inciobot.bot_backend.bot", "inciobot.bot_backend.bot.manager",
		"inciobot.bot_backend.dao", "inciobot.bot_backend.scheduler", "inciobot.bot_backend.services",
		"inciobot.bot_backend.utils" })
@EnableScheduling
@EnableAspectJAutoProxy
public class AppConfigTest {
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
