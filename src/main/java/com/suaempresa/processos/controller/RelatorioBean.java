package com.suaempresa.processos.controller;

import com.suaempresa.processos.dto.RelatorioProcessoDTO;
import com.suaempresa.processos.enums.StatusProcesso;
import com.suaempresa.processos.model.Cliente;
import com.suaempresa.processos.model.Usuario;
import com.suaempresa.processos.service.ClienteService;
import com.suaempresa.processos.service.RelatorioService;
import com.suaempresa.processos.service.UsuarioService;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.axes.cartesian.CartesianScales;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearAxes;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearTicks;
import org.primefaces.model.charts.bar.BarChartDataSet;
import org.primefaces.model.charts.bar.BarChartModel;
import org.primefaces.model.charts.bar.BarChartOptions;
import org.primefaces.model.charts.line.LineChartDataSet;
import org.primefaces.model.charts.line.LineChartModel;
import org.primefaces.model.charts.line.LineChartOptions;
import org.primefaces.model.charts.pie.PieChartDataSet;
import org.primefaces.model.charts.pie.PieChartModel;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Named
@ViewScoped
public class RelatorioBean implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Inject
    private RelatorioService relatorioService;
    @Inject
    private ClienteService clienteService;
    @Inject
    private UsuarioService usuarioService;

    // Filtros
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private List<StatusProcesso> statusSelecionados;
    private List<Cliente> clientesSelecionados;
    private List<Usuario> responsaveisSelecionados;

    // Dados para os filtros
    private List<StatusProcesso> todosStatus;
    private List<Cliente> todosClientes;
    private List<Usuario> todosResponsaveis;

    // Dados dos relatórios
    private List<RelatorioProcessoDTO> processos;
    private long processosFinalizados;
    private double tempoMedioConclusao;
    private BigDecimal totalTaxasGerenciadas;
    private long processosAtrasados;

    // Modelos dos gráficos
    private PieChartModel pieModel;
    private BarChartModel barModel;
    private LineChartModel lineModel;

    @PostConstruct
    public void init() {
        todosStatus = Arrays.asList(StatusProcesso.values());
        todosClientes = clienteService.findAll();
        todosResponsaveis = usuarioService.findAll();
        //gerarRelatorio(); // Gerar com dados iniciais (sem filtros)
    }

    public void gerarRelatorio() {
        if (dataInicio == null && dataFim == null
            && (statusSelecionados == null || statusSelecionados.isEmpty())
            && (clientesSelecionados == null || clientesSelecionados.isEmpty())
            && (responsaveisSelecionados == null || responsaveisSelecionados.isEmpty())) {
            // Nenhum filtro preenchido, não executa o relatório
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Preencha pelo menos um campo para executar o relatório: "));
        }else{
            processos = relatorioService.gerarRelatorioProcessos(dataInicio, dataFim, statusSelecionados, clientesSelecionados, responsaveisSelecionados);
            processosFinalizados = relatorioService.countProcessosFinalizados(dataInicio, dataFim, statusSelecionados, clientesSelecionados, responsaveisSelecionados);
            tempoMedioConclusao = relatorioService.getTempoMedioConclusao(dataInicio, dataFim, statusSelecionados, clientesSelecionados, responsaveisSelecionados);
            totalTaxasGerenciadas = relatorioService.getTotalTaxasGerenciadas(dataInicio, dataFim, statusSelecionados, clientesSelecionados, responsaveisSelecionados);
            processosAtrasados = relatorioService.countProcessosAtrasados(dataInicio, dataFim, statusSelecionados, clientesSelecionados, responsaveisSelecionados);
    
            pieModel = new PieChartModel();
            barModel = new BarChartModel();
            lineModel = new LineChartModel();
            createPieModel();
            createBarModel();
            createLineModel();
        }

    }

    private void createPieModel() {
        //pieModel = new PieChartModel();
        ChartData data = new ChartData();

        PieChartDataSet dataSet = new PieChartDataSet();
        List<Number> values = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        List<String> bgColors = new ArrayList<>();

        Map<String, Long> distribuicao = relatorioService.getDistribuicaoProcessosPorTipo(dataInicio, dataFim, statusSelecionados, clientesSelecionados, responsaveisSelecionados);
        int i = 0;
        for (Map.Entry<String, Long> entry : distribuicao.entrySet()) {
            labels.add(entry.getKey());
            values.add(entry.getValue());
            bgColors.add(getColor(i++));
        }

        dataSet.setData(values);
        dataSet.setBackgroundColor(bgColors);
        data.addChartDataSet(dataSet);
        data.setLabels(labels);

        pieModel.setData(data);
    }

    private void createBarModel() {
       
        ChartData data = new ChartData();

        Map<String, Map<StatusProcesso, Long>> analise = relatorioService.getAnaliseCargaTrabalhoPorResponsavel(dataInicio, dataFim, statusSelecionados, clientesSelecionados, responsaveisSelecionados);

        List<String> labels = new ArrayList<>(analise.keySet());

        // Ensure labels are not empty before proceeding
        if (labels.isEmpty()) {
            // Handle empty data scenario, e.g., display a message or return an empty chart
            barModel.setData(new ChartData()); // Set empty data to avoid NPE
            return;
        }

        for (StatusProcesso status : todosStatus) {
            BarChartDataSet barDataSet = new BarChartDataSet();
            barDataSet.setLabel(status.name());
            barDataSet.setBackgroundColor(getStatusColor(status));
            List<Number> values = new ArrayList<>();
            for (String responsavel : labels) {
                values.add(analise.getOrDefault(responsavel, java.util.Collections.emptyMap()).getOrDefault(status, 0L));
            }
            barDataSet.setData(values);
            data.addChartDataSet(barDataSet);
        }

        data.setLabels(labels);
        barModel.setData(data);

        // Options
    BarChartOptions options = new BarChartOptions();
    CartesianScales cScales = new CartesianScales();
    CartesianLinearAxes xAxes = new CartesianLinearAxes();
    xAxes.setStacked(true);
    CartesianLinearAxes yAxes = new CartesianLinearAxes();
    yAxes.setStacked(true);
    CartesianLinearTicks yTicks = new CartesianLinearTicks();
    yTicks.setStepSize(1);
    yTicks.setPrecision(0); // força inteiro
    yAxes.setTicks(yTicks);
    cScales.addXAxesData(xAxes);
    cScales.addYAxesData(yAxes);
    options.setScales(cScales);
    barModel.setOptions(options);
    }

    private void createLineModel() {
        ChartData data = new ChartData();

        LineChartDataSet dataSet = new LineChartDataSet();
        List<Object> values = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        Map<String, Double> tempoMedio = relatorioService.getTempoMedioPorEtapa(dataInicio, dataFim, statusSelecionados, clientesSelecionados, responsaveisSelecionados);

        if (tempoMedio != null) {
            for (Map.Entry<String, Double> entry : tempoMedio.entrySet()) {
                labels.add(entry.getKey());
                values.add(entry.getValue());
            }
        }

        dataSet.setData(values);
        dataSet.setLabel("Tempo Médio (dias)");
        dataSet.setFill(false);
        dataSet.setBorderColor("rgb(75, 192, 192)");
        dataSet.setTension(0.1);
        data.addChartDataSet(dataSet);
        data.setLabels(labels);

        // Options
        LineChartOptions options = new LineChartOptions();
        CartesianScales cScales = new CartesianScales();
        CartesianLinearAxes linearAxes = new CartesianLinearAxes();
        CartesianLinearTicks ticks = new CartesianLinearTicks();
        ticks.setStepSize(1); // força passo de 1
        ticks.setPrecision(0); // força inteiro
        linearAxes.setTicks(ticks);
        cScales.addYAxesData(linearAxes);
        options.setScales(cScales);
        lineModel.setOptions(options);
        lineModel.setData(data);
    }

    private String getColor(int index) {
        String[] colors = {"rgb(255, 99, 132)", "rgb(54, 162, 235)", "rgb(255, 205, 86)", "rgb(75, 192, 192)", "rgb(153, 102, 255)", "rgb(255, 159, 64)"};
        return colors[index % colors.length];
    }

    private String getStatusColor(StatusProcesso status) {
        switch (status) {
            case ABERTO: return "rgb(201, 203, 207)";
            case EM_ANDAMENTO: return "rgb(54, 162, 235)";
            case CONCLUIDO: return "rgb(75, 192, 192)";
            case CANCELADO: return "rgb(255, 99, 132)";
            default: return "rgb(0, 0, 0)";
        }
    }

    // Getters e Setters para filtros e dados
    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public List<StatusProcesso> getStatusSelecionados() {
        return statusSelecionados;
    }

    public void setStatusSelecionados(List<StatusProcesso> statusSelecionados) {
        this.statusSelecionados = statusSelecionados;
    }

    public List<Cliente> getClientesSelecionados() {
        return clientesSelecionados;
    }

    public void setClientesSelecionados(List<Cliente> clientesSelecionados) {
        this.clientesSelecionados = clientesSelecionados;
    }

    public List<Usuario> getResponsaveisSelecionados() {
        return responsaveisSelecionados;
    }

    public void setResponsaveisSelecionados(List<Usuario> responsaveisSelecionados) {
        this.responsaveisSelecionados = responsaveisSelecionados;
    }

    public List<StatusProcesso> getTodosStatus() {
        return todosStatus;
    }

    public List<Cliente> getTodosClientes() {
        return todosClientes;
    }

    public List<Usuario> getTodosResponsaveis() {
        return todosResponsaveis;
    }

    public List<RelatorioProcessoDTO> getProcessos() {
        return processos;
    }

    public long getProcessosFinalizados() {
        return processosFinalizados;
    }

    public double getTempoMedioConclusao() {
        return tempoMedioConclusao;
    }

    public BigDecimal getTotalTaxasGerenciadas() {
        return totalTaxasGerenciadas;
    }

    public long getProcessosAtrasados() {
        return processosAtrasados;
    }

    public PieChartModel getPieModel() {
        return pieModel;
    }

    public BarChartModel getBarModel() {
        return barModel;
    }

    public LineChartModel getLineModel() {
        return lineModel;
    }
}

