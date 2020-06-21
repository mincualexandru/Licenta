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
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

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

		http.requiresChannel().anyRequest().requiresSecure();

		http.csrf().disable().authorizeRequests().antMatchers("/css/**").permitAll().antMatchers("/vendor/**")
				.permitAll().antMatchers("/js/**").permitAll().antMatchers("/").permitAll().antMatchers("/login")
				.permitAll().antMatchers("/signup").permitAll()

				// ADMIN PAGES //

				.antMatchers("/accounts").access("hasRole('ROLE_ADMIN')").antMatchers("/admin")
				.access("hasRole('ROLE_ADMIN')").antMatchers("/helper_activity").access("hasRole('ROLE_ADMIN')")
				.antMatchers("/user_activity").access("hasRole('ROLE_ADMIN')").antMatchers("/view_account_details")
				.access("hasRole('ROLE_ADMIN')").antMatchers("/view_content_of_all_accounts")
				.access("hasRole('ROLE_ADMIN')")

				// USER PAGES //

				.antMatchers("/action_activate_account").access("hasRole('ROLE_USER')").antMatchers("/choose_device")
				.access("hasRole('ROLE_USER')").antMatchers("/choose_helper").access("hasRole('ROLE_USER')")
				.antMatchers("/choose_plan").access("hasRole('ROLE_USER')").antMatchers("/differences_for_calories")
				.access("hasRole('ROLE_USER')").antMatchers("/exercises_done").access("hasRole('ROLE_USER')")
				.antMatchers("/foods_eaten").access("hasRole('ROLE_USER')").antMatchers("/home")
				.access("hasRole('ROLE_USER')").antMatchers("/offers_feedback_food").access("hasRole('ROLE_USER')")
				.antMatchers("/offers_feedback").access("hasRole('ROLE_USER')").antMatchers("/perfom_this_exercise")
				.access("hasRole('ROLE_USER')").antMatchers("/set_height_and_weight").access("hasRole('ROLE_USER')")
				.antMatchers("/shopping_cart").access("hasRole('ROLE_USER')").antMatchers("/view_charts_for_device")
				.access("hasRole('ROLE_USER')").antMatchers("/view_feedbacks_from_helper")
				.access("hasRole('ROLE_USER')").antMatchers("/view_measurements_for_device")
				.access("hasRole('ROLE_USER')").antMatchers("/view_user_devices").access("hasRole('ROLE_USER')")
				.antMatchers("/view_user_plans").access("hasRole('ROLE_USER')").antMatchers("/offers_feedback_food")
				.access("hasRole('ROLE_USER')").antMatchers("/wallet").access("hasRole('ROLE_USER')")

				// NUTRITIONIST PAGES //

				.antMatchers("/create_diet_plan").access("hasRole('ROLE_NUTRITIONIST')")
				.antMatchers("/create_food_for_diet_plan").access("hasRole('ROLE_NUTRITIONIST')")
				.antMatchers("/diet_plans").access("hasRole('ROLE_NUTRITIONIST')").antMatchers("/edit_diet_plan")
				.access("hasRole('ROLE_NUTRITIONIST')").antMatchers("/nutritionist")
				.access("hasRole('ROLE_NUTRITIONIST')").antMatchers("/purchased_diet_plans")
				.access("hasRole('ROLE_NUTRITIONIST')").antMatchers("/view_progress_nutritionist")
				.access("hasRole('ROLE_NUTRITIONIST')")

				// TRAINER PAGES //

				.antMatchers("/create_exercise_for_training_plan").access("hasRole('ROLE_TRAINER')")
				.antMatchers("/create_training_plan").access("hasRole('ROLE_TRAINER')")
				.antMatchers("/edit_training_plan").access("hasRole('ROLE_TRAINER')").antMatchers("/view_progress")
				.access("hasRole('ROLE_TRAINER')").antMatchers("/purchased_training_plans")
				.access("hasRole('ROLE_TRAINER')").antMatchers("/trainer").access("hasRole('ROLE_TRAINER')")
				.antMatchers("/training_plans").access("hasRole('ROLE_TRAINER')")

				// COMMON PAGES //

				.antMatchers("/curriculum-vitae").access("hasRole('ROLE_NUTRITIONIST') or hasRole('ROLE_TRAINER')")
				.antMatchers("/helper_offers_feedback")
				.access("hasRole('ROLE_NUTRITIONIST') or hasRole('ROLE_TRAINER')").antMatchers("/plan_content")
				.access("hasRole('ROLE_NUTRITIONIST') or hasRole('ROLE_TRAINER') or hasRole('ROLE_USER')")
				.antMatchers("/success")
				.access("hasRole('ROLE_NUTRITIONIST') or hasRole('ROLE_TRAINER') or hasRole('ROLE_USER')")
				.antMatchers("/transaction_history")
				.access("hasRole('ROLE_NUTRITIONIST') or hasRole('ROLE_TRAINER') or hasRole('ROLE_USER')")
				.antMatchers("/view_exercise").access("hasRole('ROLE_USER') or hasRole('ROLE_TRAINER')")
				.antMatchers("/view_feedbacks").access("hasRole('ROLE_NUTRITIONIST') or hasRole('ROLE_TRAINER')")
				.antMatchers("/view_food").access("hasRole('ROLE_USER') or hasRole('ROLE_NUTRITIONIST')")
				.antMatchers("/view_learners").access("hasRole('ROLE_NUTRITIONIST') or hasRole('ROLE_TRAINER')")
				.antMatchers("/view_profile")
				.access("hasRole('ROLE_NUTRITIONIST') or hasRole('ROLE_TRAINER') or hasRole('ROLE_USER')");

		http.authorizeRequests().and().formLogin().loginPage("/login").successHandler(customSuccessHandler)
				.failureUrl("/login?error=true").usernameParameter("name").passwordParameter("password").and().logout()
				.invalidateHttpSession(true).clearAuthentication(true)
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessUrl("/?logout").permitAll();
	}

	@Bean
	public HttpFirewall defaultHttpFirewall() {
		return new DefaultHttpFirewall();
	}
}
