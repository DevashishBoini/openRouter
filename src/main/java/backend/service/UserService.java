package backend.service;

import backend.dbModel.User;
import backend.dto.CreateUserRequest;
import backend.repository.UserRepository;

import backend.exception.EmailAlreadyExistsException;
import backend.exception.ResourceNotFoundException;

import backend.utils.EncryptionHandler;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final EncryptionHandler encryptionHandler;

    public UserService(UserRepository userRepository, EncryptionHandler encryptionHandler) {

        this.userRepository = userRepository;
        this.encryptionHandler = encryptionHandler;
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

    public User createUser(CreateUserRequest request) {

        String hashedPassword = encryptionHandler.hashPassword(request.password());
        User user = new User(request.email(), hashedPassword);

        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new EmailAlreadyExistsException("Email already exists", e);
        }
    }

}
