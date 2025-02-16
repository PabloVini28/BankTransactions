package com.picpaysimplificado.picpaysimplificado.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.picpaysimplificado.picpaysimplificado.domain.transaction.Transaction;
import com.picpaysimplificado.picpaysimplificado.domain.user.User;
import com.picpaysimplificado.picpaysimplificado.dtos.TransactionDto;
import com.picpaysimplificado.picpaysimplificado.repositories.TransactionRepository;

@Service
public class TransactionService {
    
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private NotificationService notificationService;

    public Transaction createTransaction(TransactionDto transaction)throws Exception{
        User sender = this.userService.findUserById(transaction.senderId());
        User receiver = this.userService.findUserById(transaction.receiverId());

        userService.validateTransaction(sender, transaction.value());

        boolean isAuthorized = this.authorizeTransaction(sender, transaction.value());
        if(!isAuthorized){
            throw new Exception("Transação não autorizada");
        }
        Transaction newTransaction = new Transaction();
        newTransaction.setAmount(transaction.value());
        newTransaction.setSender(sender);
        newTransaction.setReceiver(receiver);
        newTransaction.setTimeStamp(LocalDateTime.now());

        sender.setBalance(sender.getBalance().subtract(transaction.value()));
        receiver.setBalance(receiver.getBalance().add(transaction.value()));

        this.transactionRepository.save(newTransaction);
        this.userService.saveUser(sender);
        this.userService.saveUser(receiver);

        //this.notificationService.sendNotification(sender,"Transação realizada com sucesso");
        //this.notificationService.sendNotification(receiver,"Transação recebida com sucesso");

        return newTransaction;
    }

    public boolean authorizeTransaction(User sender, BigDecimal value) {
        try {
            @SuppressWarnings("rawtypes")
            ResponseEntity<Map> authorizationResponse = restTemplate.getForEntity("https://util.devi.tools/api/v2/authorize", Map.class);
    
            if (authorizationResponse.getStatusCode() == HttpStatus.OK && authorizationResponse.getBody() != null) {
                @SuppressWarnings("unchecked")
                Map<String, Object> body = authorizationResponse.getBody();
                @SuppressWarnings("unchecked")
                Map<String, Object> data = (Map<String, Object>) body.get("data"); // Pegando o objeto dentro de "data"
                
                if (data != null && data.containsKey("authorization")) {
                    return (Boolean) data.get("authorization"); // Pegando o valor booleano
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao chamar API de autorização: " + e.getMessage());
        }
        return false;
    }
    

}
