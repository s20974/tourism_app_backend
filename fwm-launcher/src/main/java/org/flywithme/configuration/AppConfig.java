package org.flywithme.configuration;

import org.flywithme.adapters.ChatJpaAdapter;
import org.flywithme.adapters.PostsJpaAdapter;
import org.flywithme.adapters.TripJpaAdapter;
import org.flywithme.adapters.UserJpaAdapter;
import org.flywithme.ports.spi.ChatPersistencePort;
import org.flywithme.ports.spi.PostsPersistencePort;
import org.flywithme.ports.spi.TripPersistencePort;
import org.flywithme.ports.spi.UserPersistencePort;
import org.flywithme.service.ChatServiceImpl;
import org.flywithme.service.PostsServiceImpl;
import org.flywithme.service.TripServiceImpl;
import org.flywithme.service.UserServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public UserPersistencePort userPersistence(){
        return new UserJpaAdapter();
    }

    @Bean
    public UserServiceImpl userService(){
        return new UserServiceImpl(userPersistence());
    }

    @Bean
    public PostsPersistencePort postsPersistencePort(){
        return new PostsJpaAdapter();
    }

    @Bean
    public PostsServiceImpl postsService(){
        return new PostsServiceImpl(postsPersistencePort());
    }

    @Bean
    public ChatPersistencePort chatPersistencePort(){
        return new ChatJpaAdapter();
    }

    @Bean
    public ChatServiceImpl chatService(){
        return new ChatServiceImpl(chatPersistencePort());
    }

    @Bean
    public TripPersistencePort tripPersistencePort(){
        return new TripJpaAdapter();
    }

    @Bean
    public TripServiceImpl tripService(){
        return new TripServiceImpl(tripPersistencePort());
    }
}
