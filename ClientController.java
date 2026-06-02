package com.company.invoice.controller;

import com.company.invoice.model.Client;
import com.company.invoice.service.ClientService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    // ✅ CREATE
    @PostMapping
    public Client create(@RequestBody Client client) {
        return clientService.createClient(client);
    }

    // ✅ GET ALL
    @GetMapping
    public List<Client> getAll() {
        return clientService.getAllClients();
    }

    // ✅ GET BY ID
    @GetMapping("/{id}")
    public Client getById(@PathVariable Long id) {
        return clientService.getClient(id);
    }

    // ✅ UPDATE
    @PutMapping("/{id}")
    public Client update(@PathVariable Long id,
                         @RequestBody Client updated) {
        return clientService.updateClient(id, updated);
    }

    // ✅ DELETE
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        clientService.deleteClient(id);
    }
}