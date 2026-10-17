import { Component, OnInit } from '@angular/core';
import { CommonModule, registerLocaleData } from '@angular/common';
import localePt from '@angular/common/locales/pt';
registerLocaleData(localePt);
import { FormsModule } from '@angular/forms';
import { CreditoService } from './services/credito.service';
import { ClienteCoreInfo, SolicitacaoCreditoResponse, TransacaoExtrato } from './models/credito.model';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {
  activeTab: 'home' | 'credito' | 'extrato' | 'historico' | 'core' = 'home';
  hideBalance: boolean = false;
  loading: boolean = false;
  enviando: boolean = false;
  erroHistorico: string | null = null;
  erroCore: string | null = null;
  feedbackMessage: string | null = null;
  feedbackType: 'success' | 'warning' | 'error' = 'success';

  // Customer State
  cpfCliente: string = '12345678900';
  nomeCliente: string = 'Ana Silva';
  rendaMensal: number = 7500;

  // Core Bank Account Info
  contaInfo: ClienteCoreInfo = {
    statusConta: 'CONSULTANDO',
    saldo: 0,
    agencia: '',
    numeroConta: ''
  };

  // Form Credit Request State
  selectedTipoCredito: string = 'EMPRÉSTIMO PESSOAL';
  valorSolicitado: number = 15000;
  quantidadeParcelas: number = 24;

  // Credit Types List
  tiposCredito = [
    { id: 'EMPRÉSTIMO PESSOAL', nome: 'Empréstimo Pessoal', taxa: '1.49% a.m.', icone: 'fa-user-tag' },
    { id: 'CRÉDITO IMOBILIÁRIO', nome: 'Crédito Imobiliário', taxa: '0.85% a.m.', icone: 'fa-house-chimney' },
    { id: 'CRÉDITO AUTOMOTIVO', nome: 'Financiamento Auto', taxa: '1.19% a.m.', icone: 'fa-car' },
    { id: 'CONSIGNADO', nome: 'Consignado FIAP', taxa: '0.99% a.m.', icone: 'fa-file-signature' }
  ];

  // Solicitacoes List
  solicitacoes: SolicitacaoCreditoResponse[] = [];

  // Extrato de Movimentacoes
  filtroExtrato: 'TODAS' | 'ENTRADAS' | 'SAIDAS' = 'TODAS';
  transacoes: TransacaoExtrato[] = [
    {
      id: 'TX-901',
      descricao: 'Pix Recebido - Maria Santos',
      tipo: 'ENTRADA',
      categoria: 'PIX',
      valor: 2500.00,
      data: new Date(Date.now() - 3600000 * 2).toISOString(),
      icone: 'fa-qrcode text-emerald'
    },
    {
      id: 'TX-902',
      descricao: 'Pagamento Fatura FIAP Card',
      tipo: 'SAIDA',
      categoria: 'PAGAMENTO',
      valor: 450.00,
      data: new Date(Date.now() - 3600000 * 18).toISOString(),
      icone: 'fa-credit-card text-danger'
    },
    {
      id: 'TX-903',
      descricao: 'Crédito Pessoal Aprovado FIAP Bank',
      tipo: 'ENTRADA',
      categoria: 'CREDITO',
      valor: 15000.00,
      data: new Date(Date.now() - 3600000 * 48).toISOString(),
      icone: 'fa-hand-holding-dollar text-primary'
    },
    {
      id: 'TX-904',
      descricao: 'Transferência Enviada - João Pereira',
      tipo: 'SAIDA',
      categoria: 'TRANSFERENCIA',
      valor: 120.50,
      data: new Date(Date.now() - 3600000 * 72).toISOString(),
      icone: 'fa-arrow-up-right-from-square text-amber'
    }
  ];

  constructor(private creditoService: CreditoService) {}

  ngOnInit(): void {
    this.carregarDadosCore();
    this.carregarSolicitacoes();
  }

  carregarDadosCore(): void {
    this.erroCore = null;
    this.contaInfo = { statusConta: 'CONSULTANDO', saldo: 0, agencia: '', numeroConta: '' };
    const cpfConsultado = this.cpfCliente;
    this.creditoService.consultarCoreBancario(cpfConsultado).subscribe({
      next: (info) => {
        if (this.cpfCliente !== cpfConsultado) return;
        if (info.statusConta === 'INDISPONIVEL') this.erroCore = 'Consulta da conta indisponível. Tente novamente.';
        if (info.statusConta === 'SEM_CONTA') this.erroCore = 'Nenhuma conta encontrada para este CPF.';
        if (info && info.saldo !== undefined) {
          this.contaInfo = info;
        }
      },
      error: (err) => {
        if (this.cpfCliente !== cpfConsultado) return;
        this.contaInfo = { statusConta: 'INDISPONIVEL', saldo: 0, agencia: '', numeroConta: '' };
        this.erroCore = 'Não foi possível consultar a conta. Tente novamente.';
      }
    });
  }

  carregarSolicitacoes(): void {
    this.erroHistorico = null;
    this.loading = true;
    this.creditoService.consultarStatus(this.cpfCliente).subscribe({
      next: (data) => {
        this.solicitacoes = data || [];
        this.loading = false;
      },
      error: (err) => {
        this.erroHistorico = 'Não foi possível consultar suas solicitações. Tente novamente.';
        this.solicitacoes = [];
        this.loading = false;
      }
    });
  }

  get transacoesFiltradas(): TransacaoExtrato[] {
    if (this.filtroExtrato === 'ENTRADAS') {
      return this.transacoes.filter(t => t.tipo === 'ENTRADA');
    }
    if (this.filtroExtrato === 'SAIDAS') {
      return this.transacoes.filter(t => t.tipo === 'SAIDA');
    }
    return this.transacoes;
  }

  get totalEntradas(): number {
    return this.transacoes
      .filter(t => t.tipo === 'ENTRADA')
      .reduce((acc, t) => acc + t.valor, 0);
  }

  get totalSaidas(): number {
    return this.transacoes
      .filter(t => t.tipo === 'SAIDA')
      .reduce((acc, t) => acc + t.valor, 0);
  }

  get parcelaEstimada(): number {
    if (!this.quantidadeParcelas || this.quantidadeParcelas <= 0) return 0;
    const taxaMes = this.getTaxaDecimal();
    const pv = this.valorSolicitado;
    const n = this.quantidadeParcelas;
    if (taxaMes === 0) return pv / n;
    return (pv * taxaMes * Math.pow(1 + taxaMes, n)) / (Math.pow(1 + taxaMes, n) - 1);
  }

  get totalPagar(): number {
    return this.parcelaEstimada * this.quantidadeParcelas;
  }

  getTaxaDecimal(): number {
    switch (this.selectedTipoCredito) {
      case 'CRÉDITO IMOBILIÁRIO': return 0.0085;
      case 'CRÉDITO AUTOMOTIVO': return 0.0119;
      case 'CONSIGNADO': return 0.0099;
      default: return 0.0149;
    }
  }

  solicitarCredito(): void {
    if (this.enviando) return;
    if (!/^(\d{11}|\d{3}\.\d{3}\.\d{3}-\d{2})$/.test(this.cpfCliente)
        || !this.nomeCliente.trim() || !Number.isFinite(this.rendaMensal) || this.rendaMensal <= 0
        || !Number.isInteger(this.quantidadeParcelas) || this.quantidadeParcelas <= 0) {
      this.feedbackType = 'error';
      this.feedbackMessage = 'Informe CPF com 11 dígitos, nome, renda positiva e parcelas válidas.';
      return;
    }
    if (!Number.isFinite(this.valorSolicitado) || this.valorSolicitado <= 0) {
      this.feedbackType = 'error';
      this.feedbackMessage = 'Por favor, informe um valor desejado válido para o crédito.';
      return;
    }

    this.enviando = true;
    this.feedbackMessage = null;

    const payload = {
      cpfCliente: this.cpfCliente,
      nomeCliente: this.nomeCliente,
      tipoCredito: this.selectedTipoCredito,
      valorSolicitado: this.valorSolicitado,
      quantidadeParcelas: this.quantidadeParcelas,
      rendaMensal: this.rendaMensal
    };

    this.creditoService.solicitarCredito(payload).subscribe({
      next: (res) => {
        this.enviando = false;
        if (res.status === 'APROVADO') {
          this.feedbackType = 'success';
          this.feedbackMessage = `Crédito de R$ ${this.valorSolicitado.toLocaleString('pt-BR')} APROVADO com sucesso!`;
          
          this.carregarDadosCore();
        } else if (res.status === 'EM_ANALISE') {
          this.feedbackType = 'warning';
          this.feedbackMessage = `Sua solicitação de R$ ${this.valorSolicitado.toLocaleString('pt-BR')} foi encaminhada para a Mesa de Operações.`;
        } else {
          this.feedbackType = 'error';
          this.feedbackMessage = `Solicitação não aprovada: ${res.parecerOperador}`;
        }
        this.carregarSolicitacoes();
        this.activeTab = 'historico';
      },
      error: (err) => {
        this.enviando = false;
        this.feedbackType = 'error';
        this.feedbackMessage = err.status === 400 ? 'Revise os dados da solicitação e tente novamente.' : 'Não foi possível enviar sua solicitação. Tente novamente em instantes.';
        console.error('Erro na solicitação de crédito:', err);
      }
    });
  }

  selectTipo(tipo: string): void {
    this.selectedTipoCredito = tipo;
  }

  toggleBalanceVisibility(): void {
    this.hideBalance = !this.hideBalance;
  }

  setTab(tab: 'home' | 'credito' | 'extrato' | 'historico' | 'core'): void {
    this.activeTab = tab;
    if (tab === 'core' || tab === 'home') this.carregarDadosCore();
    if (tab === 'historico') {
      this.carregarSolicitacoes();
    }
  }
}
