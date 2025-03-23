package ru.kata.spring.boot_security.demo.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.errors.UsernameNotUniqueException;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    @Override
    public void save(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UsernameNotUniqueException("Username(email) already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    @Override
    public void deleteById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found");
        }
        userRepository.deleteById(id);
    }

    @Override
    public void update(User user) {
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!user.getEmail().equals(existingUser.getEmail())) {
            if (userRepository.existsByEmail(user.getEmail())) {
                throw new UsernameNotUniqueException("Username(email) already exists");
            }
            existingUser.setEmail(user.getEmail());
        }

        if (!user.getPassword().isEmpty() && !user.getPassword().equals(existingUser.getPassword())) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        existingUser.setFirstname(user.getFirstname());
        existingUser.setLastname(user.getLastname());
        existingUser.setAge(user.getAge());
        existingUser.setRoles(user.getRoles());

        userRepository.save(existingUser);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}