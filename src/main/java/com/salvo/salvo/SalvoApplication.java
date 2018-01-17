package com.salvo.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class);

	}

	@Bean
	public CommandLineRunner initData(PlayerRepository repPlayer, GameRepository repGame, GamePlayerRepository repGamePlayer, ShipRepository repShip, SalvoRepository repSalvo, ScoreRepository repScore) {
		return (String... args) -> {

			Player p1 = new Player("j.bauer", "24");
			repPlayer.save(p1);
			Player p2 = new Player("c.obrian", "42");
			repPlayer.save(p2);
			Player p3 = new Player("kim_bauer","kb");
			repPlayer.save(p3);
			Player p4 = new Player("t.almeida","mole");
			repPlayer.save(p4);

			Date date = new Date();

			Game g1 = new Game(date);
			repGame.save(g1);
			Game g2 = new Game(Date.from(date.toInstant().plusSeconds(3600)));
			repGame.save(g2);
			Game g3 = new Game(Date.from(date.toInstant().plusSeconds(7200)));
			repGame.save(g3);
			Game g4 = new Game(Date.from(date.toInstant().plusSeconds(10800)));
			repGame.save(g4);
			Game g5 = new Game(Date.from(date.toInstant().plusSeconds(14400)));
			repGame.save(g5);
			Game g6 = new Game(Date.from(date.toInstant().plusSeconds(18000)));
			repGame.save(g6);

			LocalDateTime joinDate = null;
			GamePlayer gp1 = new GamePlayer(joinDate.now(), p1, g1);
			repGamePlayer.save(gp1);
			GamePlayer gp2 = new GamePlayer(joinDate.now(), p2, g1);
			repGamePlayer.save(gp2);
			GamePlayer gp3 = new GamePlayer(joinDate.now().plusMinutes(60), p1, g2);
			repGamePlayer.save(gp3);
			GamePlayer gp4 = new GamePlayer(joinDate.now().plusMinutes(62), p2, g2);
			repGamePlayer.save(gp4);
			GamePlayer gp5 = new GamePlayer(joinDate.now().plusMinutes(30), p2, g3);
			repGamePlayer.save(gp5);
			GamePlayer gp6 = new GamePlayer(joinDate.now().plusMinutes(32), p3, g3);
			repGamePlayer.save(gp6);
			GamePlayer gp7 = new GamePlayer(joinDate.now().plusMinutes(30), p1, g4);
			repGamePlayer.save(gp7);
			GamePlayer gp8 = new GamePlayer(joinDate.now().plusMinutes(32), p2, g4);
			repGamePlayer.save(gp8);
			GamePlayer gp9 = new GamePlayer(joinDate.now().plusMinutes(30), p3, g5);
			repGamePlayer.save(gp9);
			GamePlayer gp10 = new GamePlayer(joinDate.now().plusMinutes(32), p1, g5);
			repGamePlayer.save(gp10);
			GamePlayer gp11 = new GamePlayer(joinDate.now().plusMinutes(30), p4, g6);
			repGamePlayer.save(gp11);

			List<String> locs1 = Arrays.asList("H2", "H3", "H4");
			Ship ship1 = new Ship("Destroyer", gp1, locs1);
			repShip.save(ship1);
			List<String> locs2 = Arrays.asList("E1", "F1", "G1");
			Ship ship2 = new Ship("Submarine", gp1, locs2);
			repShip.save(ship2);
			List<String> locs3 = Arrays.asList("B4", "B5");
			Ship ship3 = new Ship("Patrol Boat", gp1, locs3);
			repShip.save(ship3);
			List<String> locs4 = Arrays.asList("B5", "C5", "D5");
			Ship ship4 = new Ship("Destroyer", gp2, locs4);
			repShip.save(ship4);
			List<String> locs5 = Arrays.asList("F1", "F2");
			Ship ship5 = new Ship("Patrol Boat", gp2, locs5);
			repShip.save(ship5);

			List<String> salvoLoc1 = Arrays.asList("B5");
			Salvo salvo1 = new Salvo(gp1, salvoLoc1, 1);
			repSalvo.save(salvo1);

			List<String> salvoLoc2 = Arrays.asList("C5");
			Salvo salvo2 = new Salvo(gp1, salvoLoc2, 2);
			repSalvo.save(salvo2);

			List<String> salvoLoc3 = Arrays.asList("F1");
			Salvo salvo3 = new Salvo(gp1, salvoLoc3, 3);
			repSalvo.save(salvo3);

			List<String> salvoLoc4 = Arrays.asList("B4");
			Salvo salvo4 = new Salvo(gp2, salvoLoc4, 1);
			repSalvo.save(salvo4);

			List<String> salvoLoc5 = Arrays.asList("B5");
			Salvo salvo5 = new Salvo(gp2, salvoLoc5, 2);
			repSalvo.save(salvo5);

			List<String> salvoLoc6 = Arrays.asList("B6");
			Salvo salvo6 = new Salvo(gp2, salvoLoc6, 3);
			repSalvo.save(salvo6);

			Score score1 = new Score(date, 1, g1, p1);
			repScore.save(score1);
			Score score2 = new Score(date, 0, g1, p2);
			repScore.save(score2);
			Score score3 = new Score(Date.from(date.toInstant().plusSeconds(7200)), 0.5, g2, p1);
			repScore.save(score3);
			Score score4 = new Score(Date.from(date.toInstant().plusSeconds(7200)), 0.5, g2, p2);
			repScore.save(score4);

		};
	}

}

@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

	@Autowired
	PlayerRepository repPlayer;

	@Override
	public void init(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService());
	}

	@Bean
	UserDetailsService userDetailsService() {
		return new UserDetailsService() {

			@Override
			public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
				List<Player> players = repPlayer.findByUserName(userName);
				if (!players.isEmpty()) {
					Player player = players.get(0);
					return new User(player.getUserName(), player.getPassword(),
							AuthorityUtils.createAuthorityList("USER"));
				} else {
					throw new UsernameNotFoundException("Unknown user: " + userName);
				}
			}
		};
	}
}

@Configuration
@EnableWebSecurity
class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()

				.antMatchers("/web/public/**", "/api/games").permitAll()
				.antMatchers("/api/login").permitAll()
				.antMatchers("/api/players").permitAll()
				.anyRequest().fullyAuthenticated();

		http.formLogin()
				.usernameParameter("username")
				.passwordParameter("password")
				.loginPage("/api/login");

		http.logout().logoutUrl("/api/logout");


		// turn off checking for CSRF tokens
		http.csrf().disable();

		// if user is not authenticated, just send an authentication failure response
		http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		// if login is successful, just clear the flags asking for authentication
		http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

		// if login fails, just send an authentication failure response
		http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		// if logout is successful, just send a success response
		http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
	}
	private void clearAuthenticationAttributes(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
		}
	}

}
