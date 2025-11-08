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

import java.io.IOException;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    @Autowired
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    //        private final JwtConfigurer jwtConfigurer;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                        // Разрешаем доступ к статическим ресурсам и публичным endpoint'ам
                        .requestMatchers("/auth/registration/", "/observer/image/**", "/auth/register", "/swagger-ui.html/**", "/auth/confirmation").permitAll()
//                        .requestMatchers("/observer/images/*", "/login", "/auth/register", "/auth/invalid-confirmation").permitAll()

                        // Защищенные маршруты
//                        .requestMatchers("/admin/**").hasRole("ADMIN")
//                        .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
//                        .requestMatchers("/auth/logout/", "/user").authenticated()
                        .anyRequest().permitAll()
                // Все остальные запросы требуют аутентификации
//                        .anyRequest().authenticated()
        );
        http.formLogin(form -> form
                                .loginPage("/auth/login")
//                        .loginProcessingUrl("/api/auth/login").loginProcessingUrl("/auth/login")
//                        .successHandler(customAuthenticationSuccessHandler)
                                .defaultSuccessUrl("/video", true)
                                .successHandler(customAuthenticationSuccessHandler)
//                        .failureUrl("/login?error=true")
                                .permitAll()
                )
                .logout(logout -> logout
//                        .logoutUrl("/api/auth/logout")
                                .logoutSuccessUrl("/video")
                                .deleteCookies("JSESSIONID")
                                .permitAll()
                )
                .httpBasic(Customizer.withDefaults())
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/public/**")
                );


        return http.build();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new AccessDeniedHandler() {
            @Override
            public void handle(HttpServletRequest request, HttpServletResponse response,
                               AccessDeniedException accessDeniedException) throws IOException {
                // Ваша кастомная логика
                response.sendRedirect("/error/access-denied");

            }
        };
    }

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

