package com.suaempresa.processos.service;

import com.suaempresa.processos.model.Cliente;
import com.suaempresa.processos.repository.ClienteRepository;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;

@Stateless
public class ClienteService {
    @Inject
    private ClienteRepository clienteRepository;

    public Cliente findById(Integer id) {
        return clienteRepository.findById(id);
    }

    public List<Cliente> findAll() {
        return clienteRepository.findAll();
    }

    public int quantidadeClientesAtivos() {
        return clienteRepository.countByAtivoTrue();
    }
    public void save(Cliente cliente) {
        clienteRepository.save(cliente);
    }

    public void delete(Cliente cliente) {
        clienteRepository.delete(cliente);
    }
}
