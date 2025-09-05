
package com.suaempresa.processos.service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import com.suaempresa.processos.enums.StatusProcesso;
import com.suaempresa.processos.model.EtapaDisponivel;
import com.suaempresa.processos.model.EtapaProcesso;
import com.suaempresa.processos.model.ItemEtapa;
import com.suaempresa.processos.model.Processo;
import com.suaempresa.processos.model.TipoProcessoEtapasSugeridas;
import com.suaempresa.processos.repository.EtapaProcessoRepository;
import com.suaempresa.processos.repository.ItemEtapaRepository;
import com.suaempresa.processos.repository.ProcessoRepository;
import com.suaempresa.processos.repository.TipoProcessoRepository;

@ApplicationScoped
public class ProcessoService {

    private static final Logger logger = Logger.getLogger(ProcessoService.class.getName());

    @Inject
    private ProcessoRepository processoRepository;
    @Inject
    private EtapaProcessoRepository etapaProcessoRepository;
    @Inject
    private ItemEtapaRepository itemEtapaRepository;
    @SuppressWarnings("unused")
	@Inject
    private TipoProcessoRepository tipoProcessoRepository;
    @Inject
    private TipoProcessoService tipoProcessoService;


    // Atualiza as etapas de um processo
    @Transactional
    public void atualizarEtapas(Integer processoId, List<EtapaProcesso> etapas) {
        Processo processo = findById(processoId);
        if (processo != null) {
            processo.setEtapas(new ArrayList<>(etapas));
            save(processo);
        }
    }

    public List<Processo> findAll() {
        return processoRepository.findAll();
    }

    public Processo findById(Integer id) {
        return processoRepository.findById(id);
    }

    @Transactional
    public void save(Processo processo) {
        logger.info("ProcessoService.save() iniciado - ID: " + processo.getId() + ", Titulo: " + processo.getTitulo());
        
        try {
            if (processo.getId() == null) {
                logger.info("Salvando novo processo");
                processo.setDataCriacao(LocalDateTime.now());
                processo.setStatus(StatusProcesso.ABERTO);
            } else {
                logger.info("Atualizando processo existente - ID: " + processo.getId());
            }
            
            processoRepository.save(processo);
            logger.info("ProcessoService.save() concluído com sucesso - ID final: " + processo.getId());
        } catch (Exception e) {
            logger.severe("Erro ao salvar processo: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Transactional
    public void delete(Processo processo) {
        processoRepository.delete(processo);
    }

    @Transactional
    public void iniciarProcesso(Processo processo) {
        processo.setStatus(StatusProcesso.EM_ANDAMENTO);
        processoRepository.save(processo);

        // Criar etapas baseadas nas sugestões do tipo de processo
        if (processo.getTipoProcesso() != null) {
            List<TipoProcessoEtapasSugeridas> etapasSugeridas = tipoProcessoService.findEtapasSugeridasByTipoProcesso(processo.getTipoProcesso().getId());

            for (TipoProcessoEtapasSugeridas sugerida : etapasSugeridas) {
                EtapaProcesso etapa = new EtapaProcesso();
                etapa.setProcesso(processo);
                etapa.setEtapaDisponivel(sugerida.getEtapaDisponivel());
                etapa.setOrdem(sugerida.getOrdem());
                etapa.setStatus(StatusProcesso.ABERTO);
                etapaProcessoRepository.save(etapa);
            }
        }
    }

    @Transactional
    public void concluirEtapa(EtapaProcesso etapa) {
        // Verificar se todos os itens internos estão concluídos
        List<ItemEtapa> itens = itemEtapaRepository.findByEtapaProcessoId(etapa.getId());
        boolean todosItensConcluidos = itens.stream().allMatch(ItemEtapa::getConcluido);

        if (todosItensConcluidos) {
            etapa.setStatus(StatusProcesso.CONCLUIDO);
            etapa.setDataFinalizacao(LocalDateTime.now());
            etapaProcessoRepository.save(etapa);

            // Verificar se todas as etapas do processo estão concluídas
            List<EtapaProcesso> etapasDoProcesso = etapaProcessoRepository.findByProcessoId(etapa.getProcesso().getId());
            boolean todasEtapasConcluidas = etapasDoProcesso.stream().allMatch(e -> e.getStatus() == StatusProcesso.CONCLUIDO);

            if (todasEtapasConcluidas) {
                Processo processo = etapa.getProcesso();
                processo.setStatus(StatusProcesso.CONCLUIDO);
                processo.setDataFinalizacao(LocalDateTime.now());
                processoRepository.save(processo);
            }
        } else {
            throw new IllegalStateException("Não é possível concluir a etapa, pois existem itens internos pendentes.");
        }
    }

        // Atualiza as etapas de um processo a partir de uma lista de EtapaDisponivel
    @Transactional
    public void atualizarEtapasPorDisponiveis(Integer processoId, List<EtapaDisponivel> etapasDisponiveis) {
    	
        Processo processo = findById(processoId);
        if (processo != null) {
            processo.getEtapas().clear();
            int ordem = 1;
            for (EtapaDisponivel etapaDisp : etapasDisponiveis) {
                EtapaProcesso ep = new EtapaProcesso();
                ep.setEtapaDisponivel(etapaDisp);
                ep.setProcesso(processo);
                ep.setOrdem(ordem++);
                ep.setStatus(StatusProcesso.ABERTO);
                processo.getEtapas().add(ep);
            }
            save(processo);
        }
    }
  
    @Transactional
    public void concluirItemEtapa(ItemEtapa item) {
        item.setConcluido(true);
        item.setDataFinalizacao(LocalDateTime.now());
        itemEtapaRepository.save(item);
    }

    public List<EtapaProcesso> getEtapasDoProcesso(Integer processoId) {
        return etapaProcessoRepository.findByProcessoIdWithItens(processoId);
    }

    public List<ItemEtapa> getItensDaEtapa(Integer etapaProcessoId) {
        return itemEtapaRepository.findByEtapaProcessoId(etapaProcessoId);
    }


}