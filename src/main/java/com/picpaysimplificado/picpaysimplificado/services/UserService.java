package com.picpaysimplificado.picpaysimplificado.services;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.picpaysimplificado.picpaysimplificado.domain.user.User;
import com.picpaysimplificado.picpaysimplificado.domain.user.UserType;
import com.picpaysimplificado.picpaysimplificado.dtos.UserDto;
import com.picpaysimplificado.picpaysimplificado.repositories.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public void validateTransaction(User sender, BigDecimal amount) throws Exception{
        if(sender.getUserType() == UserType.MERCHANT){
            throw new Exception("Usuário do tipo lojista não está autorizado");
        }

        if(sender.getBalance().compareTo(amount) < 0){
            throw new Exception("Saldo insuficiente");
        }
    }

    public User findUserById(Long id) throws Exception{
        return this.userRepository.findUserById(id).orElseThrow(() -> new Exception("Usuário não encontrado"));
    }

    public void saveUser(User newUser){
        this.userRepository.save(newUser);
    }

    public User createUser(UserDto data) {
        User newUser = new User(data); 
        this.saveUser(newUser);
        return newUser;
    }

    public List<User> getAllUsers() {
        return this.userRepository.findAll();
    }
}
