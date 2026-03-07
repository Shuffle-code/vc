//package tt.chat.vc.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
//import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
//
//@Configuration
//public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {
//    @Override
//    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
//        messages
//                // Разрешаем подписку на топики стримов только аутентифицированным
//                .simpSubscribeDestMatchers("/topic/streams/*").authenticated()
//                // Разрешаем отправку сообщений в чат только аутентифицированным
//                .simpDestMatchers("/app/streams/*/message").authenticated()
//                // Все остальные сообщения требуют аутентификации
//                .anyMessage().authenticated();
//    }
//
//    @Override
//    protected boolean sameOriginDisabled() {
//        return true; // Для разработки, в production настроить CORS правильно
//    }
//}