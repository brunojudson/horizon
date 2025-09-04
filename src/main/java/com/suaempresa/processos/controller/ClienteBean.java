package com.suaempresa.processos.controller;

import com.suaempresa.processos.enums.TipoPessoa;
import com.suaempresa.processos.model.Cliente;
import com.suaempresa.processos.service.ClienteService;
import com.suaempresa.processos.util.DocumentoUtil;
import org.primefaces.util.LangUtils;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;

@Named
@ViewScoped
public class ClienteBean implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Inject
    private ClienteService clienteService;
    
    private Cliente cliente;
    private List<Cliente> clientes;
    private List<Cliente> clientesFilter;
    
    @PostConstruct
    public void init() {
        clientes = clienteService.findAll();
        cliente = new Cliente();
    }
    
    public void novoCliente() {
        this.cliente = new Cliente();
    }
    
    public void selecionarCliente(Cliente cli) {
        this.cliente = cli;
    }
    public void salvarCliente() {
        try {
            TipoPessoa tipoPessoaEnum = null;
            try {
                tipoPessoaEnum = TipoPessoa.valueOf(cliente.getTipoPessoa());
            } catch (Exception ex) {
                // valor inválido
            }
            if (!DocumentoUtil.isDocumentoValido(cliente.getDocumento(), tipoPessoaEnum)) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "CPF/CNPJ inválido."));
                return;
            }
            clienteService.save(cliente);
            clientes = clienteService.findAll();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Cliente salvo com sucesso!"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Erro ao salvar cliente: " + e.getMessage()));
        }
    }

    public void deletarCliente(Cliente cli) {
        try {
            clienteService.delete(cli);
            clientes = clienteService.findAll();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Cliente removido com sucesso!"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Erro ao remover cliente: " + e.getMessage()));
        }
    }

    public boolean globalFilterFunction(Object value, Object filter, Locale locale) {
        String filterText = (filter == null) ? null : filter.toString().trim().toLowerCase();
        if (LangUtils.isBlank(filterText)) {
            return true;
        }
        
        Cliente cli = (Cliente) value;
        
        return (cli.getId() != null && cli.getId().toString().toLowerCase().contains(filterText))
                || (cli.getTipoPessoa() != null && cli.getTipoPessoa().toLowerCase().contains(filterText))
                || (cli.getDocumento() != null && cli.getDocumento().toLowerCase().contains(filterText))
                || (cli.getRazaoSocial() != null && cli.getRazaoSocial().toLowerCase().contains(filterText))
                || (cli.getNomeFantasia() != null && cli.getNomeFantasia().toLowerCase().contains(filterText))
                || (cli.getTelefone() != null && cli.getTelefone().toLowerCase().contains(filterText))
                || (cli.getEmail() != null && cli.getEmail().toLowerCase().contains(filterText))
                || (cli.getCidade() != null && cli.getCidade().toLowerCase().contains(filterText))
                || (cli.getUf() != null && cli.getUf().toLowerCase().contains(filterText));
    }
    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public List<Cliente> getClientes() {
        return clientes;
    }

    public List<Cliente> getClientesFilter() {
        return clientesFilter;
    }

    public void setClientesFilter(List<Cliente> clientesFilter) {
        this.clientesFilter = clientesFilter;
    }
    
    
}
