package com.ap.homebanking.services.implement;

import com.ap.homebanking.models.ClientLoan;
import com.ap.homebanking.repositories.ClientLoanRepository;
import com.ap.homebanking.services.ClientLoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientLoanServiceImplement implements ClientLoanService {
    @Autowired
    private ClientLoanRepository clientLoanRepository;
    @Override
    public void save(ClientLoan clientLoan) {
        clientLoanRepository.save(clientLoan);
    }
}
