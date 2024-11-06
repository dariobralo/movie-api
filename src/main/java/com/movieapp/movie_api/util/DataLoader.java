package com.movieapp.movie_api.util;

import com.movieapp.movie_api.auth.entity.User;
import com.movieapp.movie_api.auth.entity.UserRoleEnum;
import com.movieapp.movie_api.auth.repository.UserRepository;
import com.movieapp.movie_api.persistence.repository.MovieRepository;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DataLoader implements CommandLineRunner {

    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final Faker faker = new Faker();

    public DataLoader(MovieRepository movieRepository, UserRepository userRepository, PasswordEncoder encoder){
        this.movieRepository = movieRepository;
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) throws Exception {
//        for (int i = 0; i < 10; i++){
//            Set<String> castList = new HashSet<>();
//            for (int j = 0; j < 2; j++){
//                castList.add(faker.name().fullName());
//            }
//
//            MovieEntity movieEntity = new MovieEntity(
//                    null,
//                    faker.book().title(),
//                    faker.name().fullName(),
//                    castList,
//                    faker.number().numberBetween(1900, 2024),
//                    faker.file().fileName()
//            );
//            movieRepository.save(movieEntity);
//        }

        if (this.userRepository.count() == 0){
            this.userRepository.saveAll(List.of(
                    User.builder().nickName("Dario").email("dario@g")
                            .password(encoder.encode("12345678"))
                            .role(UserRoleEnum.ADMIN)
                            .build()
            ));
        }

    }

}
