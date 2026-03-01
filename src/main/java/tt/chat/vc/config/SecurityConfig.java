package tt.chat.vc.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import java.io.IOException;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    @Autowired
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    @Autowired
    private final CustomLogoutSuccessHandler customLogoutSuccessHandler;
    //        private final JwtConfigurer jwtConfigurer; /auth/registration
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/registration","/auth/register","/observer/image/**","/video",
                                "/swagger-ui.html/**", "/auth/confirmation", "/observer/images/*", "/login","auth/login",  "/auth/invalid-confirmation", "/video/rules").permitAll()
//                        .requestMatchers().permitAll()
                        .anyRequest().authenticated()

        );
        http.formLogin(form -> form
                                .loginPage("/auth/login")
                                .loginProcessingUrl("/auth/login")
                                .defaultSuccessUrl("/video", true)
                                .successHandler(customAuthenticationSuccessHandler)
                                .permitAll()
                )
                .logout(logout -> logout
                                .deleteCookies("JSESSIONID")
                                .logoutSuccessHandler(customLogoutSuccessHandler)
                                .clearAuthentication(true)
                                .permitAll()
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/auth/login"))
                        .accessDeniedPage("/error/access-denied")
                )
                .httpBasic(Customizer.withDefaults())
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/public/**"));
        return http.build();
    }

//    @Bean
//    public AccessDeniedHandler accessDeniedHandler() {
//        return new AccessDeniedHandler() {
//            @Override
//            public void handle(HttpServletRequest request, HttpServletResponse response,
//                               AccessDeniedException accessDeniedException) throws IOException {
//                response.sendRedirect("/error/access-denied");
//
//            }
//        };
//    }

}
//        http.authorizeHttpRequests(HttpSecurity.RequestMatcherConfigurer(filterChain()))
////                .requestMatchers(HttpMethod.PUT,"/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html/**").hasAuthority("observer.create")
////                .requestMatchers(HttpMethod.GET,"/v3/api-docs/*", "/swagger-ui/*", "/swagger-ui.html/*").hasAuthority("observer.create")
//                .requestMatchers(HttpMethod.POST, "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui/index.html").hasRole("ADMIN")
////                .requestMatchers(HttpMethod.PATCH,"/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html/**").hasAuthority("observer.create")
//                .requestMatchers(HttpMethod.GET, "/observer/images/*").permitAll()
//                .requestMatchers(HttpMethod.DELETE, "/observer/images/*").hasRole("ADMIN")
//                .requestMatchers(HttpMethod.GET, "/observer/image/*").permitAll()
//                .requestMatchers(HttpMethod.POST, "/observer/add").hasRole("ADMIN")
//                .requestMatchers(HttpMethod.GET, "/observer/*").permitAll()
//                .requestMatchers(HttpMethod.GET, "/observer/status_delete/*").hasAuthority("observer.delete")
////                .requestMatchers(HttpMethod.GET, "/upcomingTournaments/enroll/{{playerId}}/{{tournamentId}}").hasRole("USER")
////                .requestMatchers(HttpMethod.PUT, "/upcomingTournaments/disenroll/{playerId}/{tournamentId}").permitAll()
//                .requestMatchers(HttpMethod.GET, "/auth/registration").anonymous()
//                .requestMatchers(HttpMethod.POST, "/auth/register").anonymous()
//                .requestMatchers(HttpMethod.POST, "/auth/invalid-confirmation").anonymous()
//                .requestMatchers(HttpMethod.POST, "/auth/changeUsername").anonymous()
//                .requestMatchers(HttpMethod.POST, "/auth/confirmation").anonymous()
////                .requestMatchers(HttpMethod.POST, "http://localhost:8093/swagger-ui/index.html").hasAuthority("observer.create")
////                .requestMatchers(HttpMethod.POST, "http://localhost:8093/swagger-ui.html").hasAuthority("observer.create")
//                .requestMatchers(HttpMethod.GET, "/*").authenticated()
//                .requestMatchers(HttpMethod.POST, "/*").authenticated()
////                .requestMatchers(HttpMethod.GET, "/*").anonymous()
////                .requestMatchers(HttpMethod.POST, "/*").anonymous()
////                .antMatchers("/v3/api-docs/**").permitAll()
//                .and().csrf().disable();
//        http.exceptionHandling().accessDeniedPage("/access-denied");
//        http.formLogin()
//                .loginPage("/auth/login")
//                .loginProcessingUrl("/auth/login")
//                .successHandler(customAuthenticationSuccessHandler)
//                .permitAll();
//        http.logout(Customizer.withDefaults())
//                .logoutSuccessUrl("/observer/all")
//                .permitAll();
//        http.httpBasic(Customizer.withDefaults());
//        return http.build();
//    }
//


//}
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http.authorizeHttpRequests()
////                .antMatchers(HttpMethod.POST,"/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
//////                .antMatchers(HttpMethod.POST, "/api/v1/auth/login/").permitAll()
////                .antMatchers(HttpMethod.GET, "/observer/images/*").permitAll()
////                .antMatchers(HttpMethod.POST, "/observer/add").hasAuthority("observer.create")
//////                .antMatchers(HttpMethod.POST, "/api/v1/observer/all").hasAuthority("observer.read")
////                .antMatchers(HttpMethod.GET, "/observer/all").hasAuthority("observer.read")
//////                .requestMatchers(HttpMethod.GET, "/upcomingTournaments/enroll/{{playerId}}/{{tournamentId}}").hasRole("USER")
//////                .requestMatchers(HttpMethod.PUT, "/upcomingTournaments/disenroll/{playerId}/{tournamentId}").permitAll()
////                .antMatchers(HttpMethod.GET, "/auth/registration").permitAll()
////                .antMatchers(HttpMethod.POST, "/auth/register").permitAll()
////                .antMatchers(HttpMethod.POST, "/auth/invalid-confirmation").permitAll()
////                .antMatchers(HttpMethod.POST, "/auth/changeUsername").permitAll()
////                .antMatchers(HttpMethod.POST, "/auth/confirmation").permitAll()
////                .requestMatchers(HttpMethod.POST, "http://localhost:8093/swagger-ui/index.html").anonymous()
////                .antMatchers(HttpMethod.GET, "/*").authenticated()
////                .antMatchers(HttpMethod.POST, "/*").authenticated()
////                .and().csrf().disable();
//        http.exceptionHandling().accessDeniedPage("/access-denied");
//        http.apply(jwtConfigurer);
//        http.formLogin()
//                .loginPage("/auth/login")
//                .loginProcessingUrl("/auth/login")
//                .successHandler(customAuthenticationSuccessHandler)
//                .permitAll();
//        http.logout()
//                .logoutSuccessUrl("/observer/all")
//                .permitAll();
//        http.httpBasic();
//        return http.build();
//    }

