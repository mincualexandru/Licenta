package com.web.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private DataSource dataSource;

	@Autowired
	CustomSuccessHandler customSuccessHandler;

	private final String USERS_QUERY = "select name, password, 1 as active from accounts where name=?";
	private final String ROLES_QUERY = "select u.name, r.name from accounts u inner join accounts_roles ur on (u.account_id = ur.account_id) inner join roles r on (ur.role_id=r.role_id) where u.name=?";

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.jdbcAuthentication().usersByUsernameQuery(USERS_QUERY).authoritiesByUsernameQuery(ROLES_QUERY)
				.dataSource(dataSource).passwordEncoder(bCryptPasswordEncoder);
	}

	@Override
	protected void configure(final HttpSecurity http) throws Exception {
		http.csrf().disable().authorizeRequests().antMatchers("/css/**").permitAll().antMatchers("/js/**").permitAll()
				.antMatchers("/").permitAll().antMatchers("/login").permitAll().antMatchers("/signup").permitAll()

				.antMatchers("/users").access("hasRole('ROLE_ADMIN')").antMatchers("/admin")
				.access("hasRole('ROLE_ADMIN')").antMatchers("/validations-accounts").access("hasRole('ROLE_ADMIN')")
				.antMatchers("/validate-account").access("hasRole('ROLE_ADMIN')").antMatchers("/view_account_details")
				.access("hasRole('ROLE_ADMIN')")

				.antMatchers("/home").access("hasRole('ROLE_USER')").antMatchers("/buy_band")
				.access("hasRole('ROLE_USER')").antMatchers("/buy_scale").access("hasRole('ROLE_USER')")
				.antMatchers("/current_balance").access("hasRole('ROLE_USER')").antMatchers("/shopping_cart")
				.access("hasRole('ROLE_USER')")

				.antMatchers("/nutritionist").access("hasRole('ROLE_NUTRITIONIST')")

				.antMatchers("/trainer").access("hasRole('ROLE_TRAINER')")

				.antMatchers("/curriculum-vitae").access("hasRole('ROLE_NUTRITIONIST') or hasRole('ROLE_TRAINER')")
				.antMatchers("/view_profile").access("hasRole('ROLE_NUTRITIONIST') or hasRole('ROLE_TRAINER')");

		http.authorizeRequests().and().formLogin().loginPage("/login").successHandler(customSuccessHandler)
				.failureUrl("/login?error=true").usernameParameter("name").passwordParameter("password").and().logout()
				.logoutUrl("/logout").logoutSuccessUrl("/");
	}

	@Bean
	public HttpFirewall defaultHttpFirewall() {
		return new DefaultHttpFirewall();
	}
}
