package tt.chat.vc.config;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootConfiguration
public class ModelMapperConfig {
    @Bean
    ModelMapper modelMapper(){
        return new ModelMapper();
    }

}
