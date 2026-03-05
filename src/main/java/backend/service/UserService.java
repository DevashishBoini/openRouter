package backend.service;

import backend.dbModel.User;
import backend.dto.SignupRequest;
import backend.dto.LoginRequest;
import backend.exception.InvalidCredentialsException;
import backend.repository.UserRepository;

import backend.exception.EmailAlreadyExistsException;
import backend.exception.ResourceNotFoundException;

import backend.utils.PasswordHandler;
import backend.utils.JwtHandler;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

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

        String hashedPassword = passwordHandler.hashPassword(password);
        User user = new User(email, hashedPassword);

        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new EmailAlreadyExistsException("Email already exists", e);
        }
    }
    
    public String userLogin(String email, String password){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new InvalidCredentialsException("Invalid Email or Password")
                );


        if(!passwordHandler.verifyPassword(password, user.getPassword())){
            throw new InvalidCredentialsException("Invalid Email or Password");
        }

        return jwtHandler.generateJwtToken(user.getEmail());

    }

}
