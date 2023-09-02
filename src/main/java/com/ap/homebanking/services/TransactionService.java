package com.ap.homebanking.services;

import com.ap.homebanking.models.Transaction;

public interface TransactionService {
    void save(Transaction transaction);
}
