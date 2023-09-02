package com.ap.homebanking.services;

import com.ap.homebanking.dtos.ClientDTO;
import com.ap.homebanking.models.Client;

import java.util.List;

public interface ClientService {
    List<ClientDTO> getClientsDTO();

    void save(Client client);

    Client findById(long id);

    ClientDTO getClientDTO(long id);

    ClientDTO getCurrent(String email);
    Client findByEmail(String email);
}
