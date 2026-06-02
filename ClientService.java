package com.company.invoice.service;
import com.company.invoice.model.Client;
import com.company.invoice.model.Invoice;
import com.company.invoice.model.InvoiceStatus;
import com.company.invoice.repository.ClientRepository;
import com.company.invoice.repository.InvoiceRepository;
import com.company.invoice.repository.InvoiceItemRepository;
import com.company.invoice.repository.PaymentRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceItemRepository itemRepository;
    private final PaymentRepository paymentRepository;

    public ClientService(ClientRepository clientRepository,
                         InvoiceRepository invoiceRepository,
                         InvoiceItemRepository itemRepository,
                         PaymentRepository paymentRepository) {
        this.clientRepository = clientRepository;
        this.invoiceRepository = invoiceRepository;
        this.itemRepository = itemRepository;
        this.paymentRepository = paymentRepository;
    }

    // ✅ CREATE
    public Client createClient(Client client) {
        return clientRepository.save(client);
    }

    // ✅ GET ALL
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    // ✅ GET BY ID
    public Client getClient(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found"));
    }

    // ✅ UPDATE
    public Client updateClient(Long id, Client updated) {

        Client client = getClient(id);

        if (updated.getName() != null) 
            client.setName(updated.getName());

        if (updated.getEmail() != null)
            client.setEmail(updated.getEmail());

        if (updated.getPhone() != null)
            client.setPhone(updated.getPhone());

        if (updated.getAddress() != null)
            client.setAddress(updated.getAddress());

        return clientRepository.save(client);
    }

    @Transactional
    public void deleteClient(Long clientId) {

        List<Invoice> invoices = invoiceRepository.findByClientId(clientId);

        for (Invoice invoice : invoices) {
            if (invoice.getStatus() == InvoiceStatus.PAID) {
                throw new RuntimeException(
                    "Cannot delete client with PAID invoices"
                );
            }
        }

        if (!invoices.isEmpty()) {
            throw new RuntimeException(
                "Delete invoices first before deleting client"
            );
        }

        clientRepository.deleteById(clientId);
    }
}