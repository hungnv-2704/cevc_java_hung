package com.travel.tourbooking.security;

import com.travel.tourbooking.enums.Role;
import com.travel.tourbooking.model.User;
import com.travel.tourbooking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public CustomOAuth2UserService(@Lazy PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        
        String provider = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo oAuth2UserInfo = new OAuth2UserInfo(oauth2User.getAttributes());
        
        processOAuth2User(provider, oAuth2UserInfo);
        
        return oauth2User;
    }

    private void processOAuth2User(String provider, OAuth2UserInfo oAuth2UserInfo) {
        String email = oAuth2UserInfo.getEmail();
        
        Optional<User> userOptional = userRepository.findByEmail(email);
        
        if (userOptional.isEmpty()) {
            User newUser = User.builder()
                    .username(email.split("@")[0] + "_" + UUID.randomUUID().toString().substring(0, 8))
                    .name(oAuth2UserInfo.getName())
                    .email(email)
                    .password(passwordEncoder.encode(UUID.randomUUID().toString())) // Random password for OAuth users
                    .provider(provider)
                    .providerId(oAuth2UserInfo.getId())
                    .role(Role.USER)
                    .build();
            
            userRepository.save(newUser);
        } else {
            User existingUser = userOptional.get();
            if (existingUser.getProvider() == null) {
                existingUser.setProvider(provider);
                existingUser.setProviderId(oAuth2UserInfo.getId());
                userRepository.save(existingUser);
            }
        }
    }
}
