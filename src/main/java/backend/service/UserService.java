package backend.service;

import backend.dbModel.User;
import backend.exception.InvalidCredentialsException;
import backend.repository.UserRepository;

import backend.exception.EmailAlreadyExistsException;
import backend.exception.ResourceNotFoundException;

import backend.utils.PasswordHandler;
import backend.utils.JwtHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordHandler passwordHandler;
    private final JwtHandler jwtHandler;

    public UserService(UserRepository userRepository, PasswordHandler passwordHandler, JwtHandler jwtHandler) {

        this.userRepository = userRepository;
        this.passwordHandler = passwordHandler;
        this.jwtHandler = jwtHandler;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with id: " + id)
                );
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with email: %s".formatted(email))
                );
    }

    public User createUser(String email, String password) {

        logger.info("Attempting user signup: email={}", email);

        String hashedPassword = passwordHandler.hashPassword(password);
        User user = new User(email, hashedPassword);

        try {
            User savedUser = userRepository.save(user);
            logger.info("User signup successful: userId={}, email={}", savedUser.getId(), savedUser.getEmail());
            return savedUser;
        } catch (DataIntegrityViolationException e) {
            logger.warn("User signup failed - email already exists: email={}", email);
            throw new EmailAlreadyExistsException("Email already exists", e);
        }
    }
    
    public String userLogin(String email, String password){
        logger.info("Attempting user login: email={}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("Login failed - user not found: email={}", email);
                    return new InvalidCredentialsException("Invalid Email or Password");
                });


        if(!passwordHandler.verifyPassword(password, user.getPassword())){
            logger.warn("Login failed - invalid password: email={}", email);
            throw new InvalidCredentialsException("Invalid Email or Password");
        }

        String token = jwtHandler.generateJwtToken(user.getEmail());
        logger.info("User login successful: userId={}, email={}", user.getId(), user.getEmail());

        return token;
    }

}
