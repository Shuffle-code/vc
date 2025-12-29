package tt.chat.vc.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tt.chat.vc.entity.OnlineUsersInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private OnlineUsersInterceptor onlineUsersInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(onlineUsersInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/admin/**", "/api/**", "/static/**");
    }
}
