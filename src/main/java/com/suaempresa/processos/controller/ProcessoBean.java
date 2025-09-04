package com.suaempresa.processos.controller;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.model.menu.MenuModel;

import com.suaempresa.processos.enums.StatusProcesso;
import com.suaempresa.processos.model.Cliente;
import com.suaempresa.processos.model.EtapaDisponivel;
import com.suaempresa.processos.model.EtapaProcesso;
import com.suaempresa.processos.model.ItemEtapa;
 import com.suaempresa.processos.model.Licenciamento;
import com.suaempresa.processos.model.Processo;
import com.suaempresa.processos.model.TipoProcesso;
import com.suaempresa.processos.model.Usuario;
import com.suaempresa.processos.repository.ClienteRepository;
import com.suaempresa.processos.repository.EtapaProcessoRepository;
import com.suaempresa.processos.repository.ItemEtapaRepository;
import com.suaempresa.processos.repository.UsuarioRepository;
import com.suaempresa.processos.service.EtapaDisponivelService;
import com.suaempresa.processos.service.ItemDisponivelService;
import com.suaempresa.processos.service.LicenciamentoService;
import com.suaempresa.processos.service.ProcessoService;
import com.suaempresa.processos.service.TipoProcessoService;

@Named
@ViewScoped
public class ProcessoBean implements Serializable {
   
    private static final long serialVersionUID = 1L;
    
    // Para capturar o parâmetro da etapa ao abrir a página de adição de item
    private Integer etapaId;
   
    private final ProcessoService processoService;
    private final TipoProcessoService tipoProcessoService;
    private final EtapaDisponivelService etapaDisponivelService;
    private final UsuarioRepository usuarioRepository;
    private final EtapaProcessoRepository etapaProcessoRepository;
    private final ClienteRepository clienteRepository;
    private final ItemEtapaRepository itemEtapaRepository;
    private final ItemDisponivelService itemDisponivelService;
    private final LicenciamentoService licenciamentoService;

    @Inject
    public ProcessoBean(
        ProcessoService processoService,
        TipoProcessoService tipoProcessoService,
        EtapaDisponivelService etapaDisponivelService,
        UsuarioRepository usuarioRepository,
        EtapaProcessoRepository etapaProcessoRepository,
        ClienteRepository clienteRepository,
        ItemEtapaRepository itemEtapaRepository,
        ItemDisponivelService itemDisponivelService,
        LicenciamentoService licenciamentoService
    ) {
        this.processoService = processoService;
        this.tipoProcessoService = tipoProcessoService;
        this.etapaDisponivelService = etapaDisponivelService;
        this.usuarioRepository = usuarioRepository;
        this.etapaProcessoRepository = etapaProcessoRepository;
        this.clienteRepository = clienteRepository;
        this.itemEtapaRepository = itemEtapaRepository;
        this.itemDisponivelService = itemDisponivelService;
        this.licenciamentoService = licenciamentoService;
    }

    
    
    // Campo auxiliar para o ID do cliente selecionado
    private Integer clienteId;
    private Integer licenciamentoIdSelecionado; // Para o select de licenciamento
    private List<Processo> processos;
    private List<Processo> selectedProcessos;
    private Processo processo;
    private Integer processoId; // Para capturar o parâmetro da URL
    private Integer licenciamentoId; // Para capturar o parâmetro da URL
    private List<TipoProcesso> tiposProcesso;
    private List<Usuario> responsaveis;
    private List<EtapaDisponivel> etapasDisponiveis;
    private List<EtapaProcesso> etapasDoProcesso;
    private List<ItemEtapa> itensEtapaProcesso;
    private List<Licenciamento> licenciamentos;
    private String filtroLicenciamento; // Para filtro na tabela
    
    private List<Cliente> clientes;
    private EtapaProcesso selectedEtapa; // Para o componente Steps
    private transient MenuModel stepsModel; // Para PrimeFaces Steps

     private List<Processo> filteredProcessos;

    // Para adição de itens em etapas
    private ItemEtapa novoItemEtapa;
    private List<com.suaempresa.processos.model.ItemDisponivel> itemDisponiveisParaEtapa;
    private EtapaProcesso etapaParaAdicionarItem;

    // Para selectOneMenu sem conversor
    private Integer itemDisponivelIdSelecionado;


    @PostConstruct
    public void init() {
        try {
            if(this.processo == null){
                carregarProcessos();
            }

            Integer idSelecionado = null;
            Integer licenciamentoIdParam = null;
            try {
                String processoIdParam = FacesContext.getCurrentInstance().getExternalContext()
                        .getRequestParameterMap().get("processoId");
                String licenciamentoParam = FacesContext.getCurrentInstance().getExternalContext()
                        .getRequestParameterMap().get("licenciamentoId");
                        
                if (processoIdParam != null && !processoIdParam.trim().isEmpty()) {
                    idSelecionado = Integer.parseInt(processoIdParam);
                    this.processoId = idSelecionado;
                }
                
                if (licenciamentoParam != null && !licenciamentoParam.trim().isEmpty()) {
                    licenciamentoIdParam = Integer.parseInt(licenciamentoParam);
                }
            } catch (Exception e) {
                // Silenciar erro de parâmetro
            }
            
            // Armazenar licenciamentoId para uso posterior
            this.licenciamentoId = licenciamentoIdParam;

            if (idSelecionado != null) {
                Processo processoCarregado = processoService.findById(idSelecionado);
                if (processoCarregado != null) {
                    this.processo = processoCarregado;
                    try {
                        this.etapasDoProcesso = processoService.getEtapasDoProcesso(idSelecionado);
                    } catch (Exception e) {
                        this.etapasDoProcesso = new ArrayList<>();
                    }
                } else {
                    this.processo = new Processo();
                    this.etapasDoProcesso = new ArrayList<>();
                }
            } else {
                this.processo = new Processo();
                this.etapasDoProcesso = new ArrayList<>();
            }
            
            // Se foi passado um licenciamentoId, pré-selecionar o licenciamento
            if (licenciamentoIdParam != null) {
                carregarLicenciamentos(); // Garante que a lista está carregada
                Licenciamento licenciamentoPre = licenciamentoService.findById(licenciamentoIdParam);
                if (licenciamentoPre != null && this.processo != null) {
                    this.processo.setLicenciamentoAssociado(licenciamentoPre);
                }
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro",
                            "Erro ao inicializar a página: " + e.getMessage()));
        }
        
        // Carregar listas necessárias
        carregarLicenciamentos();
    }


        // Filtro global para DataTable
    public boolean globalFilterFunction(Object value, Object filter, java.util.Locale locale) {
        if (filter == null || filter.toString().isEmpty()) {
            return true;
        }
        String filterText = filter.toString().trim().toLowerCase(locale);
        Processo p = (Processo) value;
        return (p.getId() != null && p.getId().toString().contains(filterText))
                || (p.getTitulo() != null && p.getTitulo().toLowerCase(locale).contains(filterText))
                || (p.getStatus() != null && p.getStatus().name().toLowerCase(locale).contains(filterText))
                || (p.getCliente() != null && p.getCliente().getRazaoSocial() != null && p.getCliente().getRazaoSocial().toLowerCase(locale).contains(filterText));
    }
    
    public StatusProcesso[] getStatusProcessos() {
        return StatusProcesso.values();
    }
    
    private void carregarClientes() {
        try {
            clientes = clienteRepository.findAll();
            if (clientes == null) {
                clientes = new ArrayList<>();
            }
        } catch (Exception e) {
            clientes = new ArrayList<>();
        }
    }

    // Chamar este método na página adicionarItemEtapa.xhtml para garantir
    // carregamento correto
    public void carregarEtapaParaAdicionarItem() {
        if (etapaParaAdicionarItem == null) {
            Integer id = null;
            try {
                String etapaIdParam = FacesContext.getCurrentInstance().getExternalContext()
                        .getRequestParameterMap().get("etapaId");
                if (etapaIdParam != null && !etapaIdParam.trim().isEmpty()) {
                    id = Integer.parseInt(etapaIdParam);
                    this.etapaId = id;
                }
            } catch (Exception e) {
                // Silenciar erro de parâmetro
            }
            if (id != null) {
                this.etapaParaAdicionarItem = etapaProcessoRepository.findById(id);
                this.novoItemEtapa = new ItemEtapa();
                this.novoItemEtapa.setEtapaProcesso(etapaParaAdicionarItem);
                this.novoItemEtapa.setConcluido(false);
                this.itemDisponiveisParaEtapa = itemDisponivelService.findAll();
            }
        }
    }

    public String getDuracaoProcessoFormatada() {
        if (processo == null || processo.getDataCriacao() == null) {
            return "-";
        }
        java.time.LocalDateTime inicio = processo.getDataCriacao();
        java.time.LocalDateTime fim = processo.getDataFinalizacao();
        if (fim == null) {
            fim = java.time.LocalDateTime.now();
        }
        java.time.Duration duracao = java.time.Duration.between(inicio, fim);
        long dias = duracao.toDays();
        long horas = duracao.toHours() % 24;
        long minutos = duracao.toMinutes() % 60;
        StringBuilder sb = new StringBuilder();
        if (dias > 0)
            sb.append(dias).append(dias == 1 ? " dia" : " dias");
        if (horas > 0) {
            if (sb.length() > 0)
                sb.append(", ");
            sb.append(horas).append(horas == 1 ? " hora" : " horas");
        }
        if (minutos > 0 && dias == 0) { // só mostra minutos se for menos de 1 dia
            if (sb.length() > 0)
                sb.append(", ");
            sb.append(minutos).append(minutos == 1 ? " minuto" : " minutos");
        }
        if (sb.length() == 0)
            return "menos de 1 minuto";
        return sb.toString();
    }

    // Iniciar etapa manualmente
    public void iniciarEtapa(EtapaProcesso etapa) {
        if (etapa != null && etapa.getStatus() == StatusProcesso.ABERTO) {
            etapa.setStatus(StatusProcesso.EM_ANDAMENTO);
            etapa.setDataCadastro(LocalDateTime.now());
            etapaProcessoRepository.save(etapa);
            // Nova regra: se o processo estiver ABERTO, mudar para EM_ANDAMENTO
            if (processo != null && processo.getStatus() == StatusProcesso.ABERTO) {
                processo.setStatus(StatusProcesso.EM_ANDAMENTO);
                processoService.save(processo);
            }
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Etapa iniciada", "A etapa '" + etapa.getEtapaDisponivel().getTitulo() + "' foi iniciada."));
        }
    }
    public void atualizarStatusItemEtapa(ItemEtapa itemEtapa) {
        try {
            if (itemEtapa.getConcluido() != null && itemEtapa.getConcluido()) {
                itemEtapa.setDataFinalizacao(LocalDateTime.now());
            } else {
                itemEtapa.setDataFinalizacao(null);
            }
            itemEtapaRepository.save(itemEtapa);
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Item atualizado",
                    "O status do item foi atualizado com sucesso."));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao atualizar item",
                    "Não foi possível atualizar o status do item: " + e.getMessage()));
        }
    }

   

    // Deletar processo selecionado
    public void deletarProcesso(Processo p) {
        try {
            if (p != null && p.getId() != null) {
                processoService.delete(p);
                carregarProcessos();
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Processo removido com sucesso!"));
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso",
                                "Selecione um processo válido para remover."));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro",
                            "Erro ao remover processo: " + e.getMessage()));
        }
    }

    // Método para inicializar um novo processo ao abrir o modal de novo processo
    public void novoProcesso() {
    carregarTiposProcesso();
    carregarResponsaveis();
    carregarClientes();
    carregarLicenciamentos();
    this.processo = new Processo();
    this.etapasDoProcesso = new ArrayList<>();
    this.clienteId = null;
    this.licenciamentoIdSelecionado = null;
    
    // Verificar se há um licenciamentoId como parâmetro da URL
    if (this.licenciamentoId != null && this.licenciamentoId > 0) {
        this.licenciamentoIdSelecionado = this.licenciamentoId;
    }
    }

    // Navegação para detalhes do processo
    public String visualizarProcesso(Processo p) {
        if (p != null && p.getId() != null) {
            return "/processos/detalhe?faces-redirect=true&amp;processoId=" + p.getId();
        }
        return null;
    }

    public void carregarProcessos() {
        try {
            processos = processoService.findAll();
            if (processos == null) {
                processos = new ArrayList<>();
            }
        } catch (Exception e) {
            processos = new ArrayList<>();
        }
    }

    private void carregarTiposProcesso() {
        try {
            tiposProcesso = tipoProcessoService.findAll();
            if (tiposProcesso == null) {
                tiposProcesso = new ArrayList<>();
            }
        } catch (Exception e) {
            tiposProcesso = new ArrayList<>();
        }
    }

    private void carregarResponsaveis() {
        try {
            responsaveis = usuarioRepository.findAll();
            if (responsaveis == null) {
                responsaveis = new ArrayList<>();
            }
        } catch (Exception e) {
            responsaveis = new ArrayList<>();
        }
    }

    @SuppressWarnings("unused")
	private void carregarEtapasDisponiveis() {
        try {
            etapasDisponiveis = etapaDisponivelService.findAll();
            if (etapasDisponiveis == null) {
                etapasDisponiveis = new ArrayList<>();
            }
        } catch (Exception e) {
            etapasDisponiveis = new ArrayList<>();
        }
    }

    private void carregarLicenciamentos() {
        try {
            licenciamentos = licenciamentoService.findAll();
            if (licenciamentos == null) {
                licenciamentos = new ArrayList<>();
            }
        } catch (Exception e) {
            licenciamentos = new ArrayList<>();
        }
    }

     // Para dialog de fluxo/etapas resumidas
    private List<EtapaProcesso> etapasResumo;

    public List<EtapaProcesso> getEtapasResumo() {
        return etapasResumo;
    }

    public void carregarEtapasResumo(Processo proc) {
        if (proc != null && proc.getId() != null) {
            try {
                this.etapasResumo = processoService.getEtapasDoProcesso(proc.getId());
            } catch (Exception e) {
                this.etapasResumo = new ArrayList<>();
            }
        } else {
            this.etapasResumo = new ArrayList<>();
        }
    }

    // Tentar carregar processo se houver ID na URL durante a inicialização

    public void salvarProcesso() {
        try {
            // Carregar licenciamentos se não foram carregados
            if (licenciamentos == null || licenciamentos.isEmpty()) {
                carregarLicenciamentos();
            }
            
            // Buscar o cliente pelo ID selecionado
            if (clienteId != null) {
                Cliente clienteSelecionado = clienteRepository.findById(clienteId);
                processo.setCliente(clienteSelecionado);
            } else {
                processo.setCliente(null);
            }
            
            // Buscar o licenciamento pelo ID selecionado
            if (licenciamentoIdSelecionado != null) {
                Licenciamento licenciamentoSelecionado = licenciamentoService.findById(licenciamentoIdSelecionado);
                processo.setLicenciamentoAssociado(licenciamentoSelecionado);
            } else {
                processo.setLicenciamentoAssociado(null);
            }
            if (processo == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Processo não pode ser nulo"));
                return;
            }
            if (processo.getTitulo() == null || processo.getTitulo().trim().isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Título é obrigatório"));
                return;
            }
            if (processo.getTipoProcesso() == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Tipo de Processo é obrigatório"));
                return;
            }
            
            // Atualiza a lista de etapas do processo com a ordem atual antes de salvar
            if (etapasDoProcesso != null) {
                for (int i = 0; i < etapasDoProcesso.size(); i++) {
                    EtapaProcesso etapa = etapasDoProcesso.get(i);
                    etapa.setOrdem(i + 1); // Garante ordem sequencial
                }
                processo.setEtapas(new ArrayList<>(etapasDoProcesso));
            }
            processoService.save(processo);
            carregarProcessos();
            processo = new Processo();
            clienteId = null;
            licenciamentoIdSelecionado = null;
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Processo salvo com sucesso!"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro",
                            "Erro ao salvar processo: " + e.getMessage()));
        }
    }

    // Método de teste para verificar se o bean está funcionando
    public void testarBean() {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Teste", "Bean está funcionando!"));
    }

    public void carregarProcesso(Processo p) {
        try {
            // Carregar listas para os selects
            carregarTiposProcesso();
            carregarResponsaveis();
            carregarClientes();
            carregarLicenciamentos();
            Processo processoCarregado = processoService.findById(p.getId());
            if (processoCarregado != null) {
                this.processo = processoCarregado;
                // Preencher o campo auxiliar clienteId para exibir o cliente no select
                if (processoCarregado.getCliente() != null) {
                    this.clienteId = processoCarregado.getCliente().getId();
                } else {
                    this.clienteId = null;
                }
                // Preencher o campo auxiliar licenciamentoIdSelecionado para exibir o licenciamento no select
                if (processoCarregado.getLicenciamentoAssociado() != null) {
                    this.licenciamentoIdSelecionado = processoCarregado.getLicenciamentoAssociado().getId();
                } else {
                    this.licenciamentoIdSelecionado = null;
                }
                this.etapasDoProcesso = processoService.getEtapasDoProcesso(p.getId());
            } else {
                this.processo = new Processo();
                this.etapasDoProcesso = new ArrayList<>();
            }
        } catch (Exception e) {
            this.processo = new Processo();
            this.etapasDoProcesso = new ArrayList<>();
        }
    }

    public void carregarProcessoPeloId() {
        try {
            if (processo == null) {
                processo = new Processo();
            }
            Integer idParaCarregar = this.processoId;
            if (idParaCarregar == null && processo.getId() != null) {
                idParaCarregar = processo.getId();
            }
            if (idParaCarregar == null) {
                String idParam = FacesContext.getCurrentInstance().getExternalContext()
                        .getRequestParameterMap().get("processoId");
                if (idParam != null && !idParam.trim().isEmpty()) {
                    try {
                        idParaCarregar = Integer.parseInt(idParam);
                        this.processoId = idParaCarregar;
                    } catch (NumberFormatException e) {
                        // Silenciar erro de conversão
                    }
                }
            }
            if (idParaCarregar != null) {
                Processo processoCompleto = processoService.findById(idParaCarregar);
                if (processoCompleto != null) {
                    this.processo = processoCompleto;
                    this.processoId = idParaCarregar;
                    this.etapasDoProcesso = processoService.getEtapasDoProcesso(idParaCarregar);
                    criarStepsModel();
                    if (!this.etapasDoProcesso.isEmpty()) {
                        this.selectedEtapa = this.etapasDoProcesso.get(0);
                    }
                } else {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Processo não encontrado"));
                }
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "ID do processo não informado"));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro",
                            "Erro ao carregar processo: " + e.getMessage()));
        }
    }

    // Método auxiliar para carregar processo por ID específico
    public void carregarProcessoPorId(Integer id) {
        this.processoId = id;
        carregarProcessoPeloId();
    }

    // Método para preparar a navegação para a página de detalhes

    // Método de teste/debug
    public void testarCarregamento() {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Debug",
                        "Teste executado. processoId=" + this.processoId +
                                ", processo=" + (processo != null ? "carregado" : "null")));
    }

    // Métodos para funcionalidade da timeline
    private void criarStepsModel() {
        stepsModel = new org.primefaces.model.menu.DefaultMenuModel();

        if (etapasDoProcesso != null && !etapasDoProcesso.isEmpty()) {
            for (int i = 0; i < etapasDoProcesso.size(); i++) {
                EtapaProcesso etapa = etapasDoProcesso.get(i);
                org.primefaces.model.menu.DefaultMenuItem item = org.primefaces.model.menu.DefaultMenuItem.builder()
                        .value(etapa.getEtapaDisponivel().getTitulo())
                        .command("#{processoBean.selecionarEtapa(" + i + ")}")
                        .update("stepContent")
                        .build();
                stepsModel.getElements().add(item);
            }
        }
    }

    public void selecionarEtapa(int index) {
        if (etapasDoProcesso != null && index >= 0 && index < etapasDoProcesso.size()) {
            this.selectedEtapa = etapasDoProcesso.get(index);
        }
    }

    public void onStepSelect(@SuppressWarnings("rawtypes") org.primefaces.event.SelectEvent event) {
        // Evento quando um step é selecionado
        if (event.getObject() instanceof Integer) {
            int index = (Integer) event.getObject();
            selecionarEtapa(index);
        }
    }

    public int getPorcentagemConclusao() {
        if (etapasDoProcesso == null || etapasDoProcesso.isEmpty()) {
            return 0;
        }

        long concluidas = etapasDoProcesso.stream()
                .filter(e -> "CONCLUIDO".equals(e.getStatus().toString()))
                .count();

        return (int) ((concluidas * 100) / etapasDoProcesso.size());
    }

    // Métodos de ação para etapas
    public void concluirEtapa(EtapaProcesso etapa) {
        try {
            if (etapa == null || etapa.getStatus() == StatusProcesso.CONCLUIDO) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso", "Etapa já está concluída."));
                return;
            }
            // Verifica se a etapa anterior está concluída (exceto para a primeira etapa)
            int idx = etapasDoProcesso.indexOf(etapa);
            if (idx > 0) {
                EtapaProcesso anterior = etapasDoProcesso.get(idx - 1);
                if (anterior.getStatus() != StatusProcesso.CONCLUIDO) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso",
                                    "Só é permitido finalizar esta etapa após a anterior estar concluída."));
                    return;
                }
            }
            // Marcar todos os itens da etapa como concluídos
            if (etapa.getItens() != null) {
                for (ItemEtapa item : etapa.getItens()) {
                    item.setConcluido(true);
                    itemEtapaRepository.save(item);
                }
            }
            etapa.setStatus(StatusProcesso.CONCLUIDO);
            etapa.setDataFinalizacao(LocalDateTime.now());
            // Persistir etapa concluída
            etapaProcessoRepository.save(etapa);

            // Atualizar próxima etapa (se houver)
            if (idx >= 0 && idx < etapasDoProcesso.size() - 1) {
                EtapaProcesso proxima = etapasDoProcesso.get(idx + 1);
                if (proxima.getStatus() == StatusProcesso.PENDENTE) {
                    proxima.setStatus(StatusProcesso.EM_ANDAMENTO);
                    etapaProcessoRepository.save(proxima);
                }
            }
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso",
                            "Etapa '" + etapa.getEtapaDisponivel().getTitulo()
                                    + "' marcada como concluída. Todos os itens foram marcados como concluídos."));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro",
                            "Erro ao concluir etapa: " + e.getMessage()));
        }
    }

    public void reabrirEtapa(EtapaProcesso etapa) {
        try {
            if (etapa == null || etapa.getStatus() != StatusProcesso.CONCLUIDO) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso",
                                "Apenas etapas concluídas podem ser reabertas."));
                return;
            }
            // Marcar todos os itens da etapa como não concluídos
            if (etapa.getItens() != null) {
                for (ItemEtapa item : etapa.getItens()) {
                    item.setConcluido(false);
                    itemEtapaRepository.save(item);
                }
            }
            etapa.setStatus(StatusProcesso.EM_ANDAMENTO);
            etapa.setDataFinalizacao(null);
            etapaProcessoRepository.save(etapa);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso",
                            "Etapa reaberta para edição. Todos os itens foram marcados como não concluídos."));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro",
                            "Erro ao reabrir etapa: " + e.getMessage()));
        }
    }

    public void finalizarProcesso() {
        try {
            if (processo != null) {
                // Nova regra: só permite finalizar se todas as etapas estiverem concluídas
                boolean todasConcluidas = true;
                if (etapasDoProcesso != null && !etapasDoProcesso.isEmpty()) {
                    for (EtapaProcesso etapa : etapasDoProcesso) {
                        if (etapa.getStatus() != StatusProcesso.CONCLUIDO) {
                            todasConcluidas = false;
                            break;
                        }
                    }
                }
                if (!todasConcluidas) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção",
                                    "Só é possível finalizar o processo se todas as etapas estiverem concluídas."));
                    return;
                }
                processo.setStatus(StatusProcesso.CONCLUIDO);
                processo.setDataFinalizacao(java.time.LocalDateTime.now());
                processoService.save(processo);

                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso",
                                "Processo finalizado com sucesso"));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro",
                            "Erro ao finalizar processo: " + e.getMessage()));
        }
    }

    public void reabrirProcesso() {
        try {
            if (processo != null) {
                processo.setStatus(StatusProcesso.EM_ANDAMENTO);
                processo.setDataFinalizacao(null);
                processoService.save(processo);

                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso",
                                "Processo reaberto com sucesso"));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro",
                            "Erro ao reabrir processo: " + e.getMessage()));
        }
    }

    public String configurarEtapas() {
        if (processo != null && processo.getId() != null) {
            return "/processos/configurarEtapas?faces-redirect=true&amp;processoId=" + processo.getId();
        }
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso",
                        "Processo não selecionado para configurar etapas."));
        return null;
    }

    public void imprimirRelatorio() {
        // Gerar relatório PDF do processo
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Em desenvolvimento",
                        "Funcionalidade de impressão de relatórios será implementada"));
    }

    public void prepararNovoItem(EtapaProcesso etapa) {
        this.novoItemEtapa = new ItemEtapa();
        this.novoItemEtapa.setEtapaProcesso(etapa);
        this.novoItemEtapa.setConcluido(false);
        this.etapaParaAdicionarItem = etapa;
        this.itemDisponiveisParaEtapa = itemDisponivelService.findAll();
    }

    public void salvarNovoItemEtapa() {
        try {
            if (novoItemEtapa != null && etapaParaAdicionarItem != null && itemDisponivelIdSelecionado != null) {
                com.suaempresa.processos.model.ItemDisponivel item = itemDisponivelService
                        .findById(itemDisponivelIdSelecionado);
                novoItemEtapa.setItemDisponivel(item);
                novoItemEtapa.setEtapaProcesso(etapaParaAdicionarItem);
                novoItemEtapa.setDataCriacao(java.time.LocalDateTime.now());
                itemEtapaRepository.save(novoItemEtapa);
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Item adicionado à etapa!"));
                // Reinicializar para novo cadastro
                this.novoItemEtapa = new ItemEtapa();
                this.novoItemEtapa.setEtapaProcesso(etapaParaAdicionarItem);
                this.novoItemEtapa.setConcluido(false);
                this.itemDisponivelIdSelecionado = null;
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Erro ao adicionar item: " + e.getMessage()));
        }
    }
     // Move a etapa para cima na ordem
    public void moverEtapaParaCima(EtapaProcesso etapa) {
        if (etapasDoProcesso == null || etapa == null) return;
        int idx = etapasDoProcesso.indexOf(etapa);
        if (idx > 0) {
            EtapaProcesso anterior = etapasDoProcesso.get(idx - 1);
            Integer ordemAtual = etapa.getOrdem();
            Integer ordemAnterior = anterior.getOrdem();
            // Troca apenas o campo ordem
            etapa.setOrdem(ordemAnterior);
            anterior.setOrdem(ordemAtual);
            // Atualiza apenas os campos alterados no banco
            etapaProcessoRepository.updateOrdem(etapa.getId(), ordemAnterior);
            etapaProcessoRepository.updateOrdem(anterior.getId(), ordemAtual);
            // Atualiza lista em memória
            etapasDoProcesso.set(idx, anterior);
            etapasDoProcesso.set(idx - 1, etapa);
        }
    }

    // Move a etapa para baixo na ordem
    public void moverEtapaParaBaixo(EtapaProcesso etapa) {
        if (etapasDoProcesso == null || etapa == null) return;
        int idx = etapasDoProcesso.indexOf(etapa);
        if (idx >= 0 && idx < etapasDoProcesso.size() - 1) {
            EtapaProcesso proxima = etapasDoProcesso.get(idx + 1);
            Integer ordemAtual = etapa.getOrdem();
            Integer ordemProxima = proxima.getOrdem();
            etapa.setOrdem(ordemProxima);
            proxima.setOrdem(ordemAtual);
            etapaProcessoRepository.updateOrdem(etapa.getId(), ordemProxima);
            etapaProcessoRepository.updateOrdem(proxima.getId(), ordemAtual);
            etapasDoProcesso.set(idx, proxima);
            etapasDoProcesso.set(idx + 1, etapa);
        }
    }
    
    // Getters e Setters

     public List<Processo> getFilteredProcessos() {
        return filteredProcessos;
    }

    public void setFilteredProcessos(List<Processo> filteredProcessos) {
        this.filteredProcessos = filteredProcessos;
    }
    public List<Processo> getProcessos() {
        return processos;
    }

    public void setProcessos(List<Processo> processos) {
        this.processos = processos;
    }

    public List<Processo> getSelectedProcessos() {
        return selectedProcessos;
    }

    public void setSelectedProcessos(List<Processo> selectedProcessos) {
        this.selectedProcessos = selectedProcessos;
    }

    public Processo getProcesso() {
        return processo;
    }

    public void setProcesso(Processo processo) {
        this.processo = processo;
    }

    public Integer getProcessoId() {
        return processoId;
    }

    public void setProcessoId(Integer processoId) {
        this.processoId = processoId;
    }

    public Integer getLicenciamentoId() {
        return licenciamentoId;
    }

    public void setLicenciamentoId(Integer licenciamentoId) {
        this.licenciamentoId = licenciamentoId;
    }

    public List<TipoProcesso> getTiposProcesso() {
        return tiposProcesso;
    }

    public void setTiposProcesso(List<TipoProcesso> tiposProcesso) {
        this.tiposProcesso = tiposProcesso;
    }

    public List<Usuario> getResponsaveis() {
    // Não carregue do banco aqui! Apenas retorna a lista já carregada
    return responsaveis;
    }

    public void setResponsaveis(List<Usuario> responsaveis) {
        this.responsaveis = responsaveis;
    }

    public List<EtapaDisponivel> getEtapasDisponiveis() {
        return etapasDisponiveis;
    }

    public void setEtapasDisponiveis(List<EtapaDisponivel> etapasDisponiveis) {
        this.etapasDisponiveis = etapasDisponiveis;
    }

    public List<EtapaProcesso> getEtapasDoProcesso() {
        return etapasDoProcesso;
    }

    public void setEtapasDoProcesso(List<EtapaProcesso> etapasDoProcesso) {
        this.etapasDoProcesso = etapasDoProcesso;
    }

    public List<ItemEtapa> getItensEtapaProcesso() {
        return itensEtapaProcesso;
    }

    public void setItensEtapaProcesso(List<ItemEtapa> itensEtapaProcesso) {
        this.itensEtapaProcesso = itensEtapaProcesso;
    }

    public EtapaProcesso getSelectedEtapa() {
        return selectedEtapa;
    }

    public void setSelectedEtapa(EtapaProcesso selectedEtapa) {
        this.selectedEtapa = selectedEtapa;
    }

    public org.primefaces.model.menu.MenuModel getStepsModel() {
        return stepsModel;
    }

    public void setStepsModel(org.primefaces.model.menu.MenuModel stepsModel) {
        this.stepsModel = stepsModel;
    }

     public Integer getEtapaId() {
        return etapaId;
    }

    public void setEtapaId(Integer etapaId) {
        this.etapaId = etapaId;
    }
     public Integer getItemDisponivelIdSelecionado() {
        return itemDisponivelIdSelecionado;
    }

    public void setItemDisponivelIdSelecionado(Integer itemDisponivelIdSelecionado) {
        this.itemDisponivelIdSelecionado = itemDisponivelIdSelecionado;
    }
     public Integer getClienteId() {
        return clienteId;
    }

    public void setClienteId(Integer clienteId) {
        this.clienteId = clienteId;
    }

    public Integer getLicenciamentoIdSelecionado() {
        return licenciamentoIdSelecionado;
    }

    public void setLicenciamentoIdSelecionado(Integer licenciamentoIdSelecionado) {
        this.licenciamentoIdSelecionado = licenciamentoIdSelecionado;
    }

    public ItemEtapa getNovoItemEtapa() {
        return novoItemEtapa;
    }

    public void setNovoItemEtapa(ItemEtapa novoItemEtapa) {
        this.novoItemEtapa = novoItemEtapa;
    }

    public List<com.suaempresa.processos.model.ItemDisponivel> getItemDisponiveisParaEtapa() {
        return itemDisponiveisParaEtapa;
    }

    public void setItemDisponiveisParaEtapa(
            List<com.suaempresa.processos.model.ItemDisponivel> itemDisponiveisParaEtapa) {
        this.itemDisponiveisParaEtapa = itemDisponiveisParaEtapa;
    }

    public EtapaProcesso getEtapaParaAdicionarItem() {
        return etapaParaAdicionarItem;
    }

    public void setEtapaParaAdicionarItem(EtapaProcesso etapaParaAdicionarItem) {
        this.etapaParaAdicionarItem = etapaParaAdicionarItem;
    }
    public List<Cliente> getClientes() {
    // Não carregue do banco aqui! Apenas retorna a lista já carregada
    return clientes;
    }
    public void setClientes(List<Cliente> clientes) {
        this.clientes = clientes;
    }
    
    public List<Licenciamento> getLicenciamentos() {
        return licenciamentos;
    }
    
    public void setLicenciamentos(List<Licenciamento> licenciamentos) {
        this.licenciamentos = licenciamentos;
    }
    
    public String getFiltroLicenciamento() {
        return filtroLicenciamento;
    }
    
    public void setFiltroLicenciamento(String filtroLicenciamento) {
        this.filtroLicenciamento = filtroLicenciamento;
    }
    
}