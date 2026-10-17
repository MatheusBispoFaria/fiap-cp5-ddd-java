import { TestBed } from '@angular/core/testing';
import { of, throwError, Subject } from 'rxjs';
import { AppComponent } from './app.component';
import { CreditoService } from './services/credito.service';

describe('Fluxos do Internet Banking', () => {
  let api: jasmine.SpyObj<CreditoService>;
  beforeEach(async () => {
    api = jasmine.createSpyObj('CreditoService', ['consultarCoreBancario', 'consultarStatus', 'solicitarCredito']);
    api.consultarCoreBancario.and.returnValue(of({statusConta:'INATIVA',saldo:12700,agencia:'0001',numeroConta:'123'}));
    api.consultarStatus.and.returnValue(of([]));
    await TestBed.configureTestingModule({imports:[AppComponent],providers:[{provide:CreditoService,useValue:api}]}).compileComponents();
  });
  it('renderiza valores brasileiros e conta retornada pela API', () => {
    const fixture=TestBed.createComponent(AppComponent);fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('12.700,00');
    expect(fixture.componentInstance.contaInfo.statusConta).toBe('INATIVA');
  });
  it('distingue falha de consulta de historico vazio e nao mostra saldo inventado', () => {
    api.consultarCoreBancario.and.returnValue(throwError(()=>new Error('offline')));
    api.consultarStatus.and.returnValue(throwError(()=>new Error('offline')));
    const fixture=TestBed.createComponent(AppComponent);fixture.detectChanges();
    fixture.componentInstance.setTab('historico');fixture.detectChanges();
    expect(fixture.componentInstance.contaInfo.statusConta).toBe('INDISPONIVEL');
    expect(fixture.nativeElement.textContent).toContain('Não foi possível consultar');
    expect(fixture.nativeElement.textContent).not.toContain('Nenhuma solicitação encontrada');
  });
  it('rejeita dados invalidos sem enviar uma proposta', () => {
    const app=TestBed.createComponent(AppComponent).componentInstance;
    app.cpfCliente='123';app.solicitarCredito();expect(api.solicitarCredito).not.toHaveBeenCalled();
    app.cpfCliente='12345678900';app.rendaMensal=0;app.solicitarCredito();expect(api.solicitarCredito).not.toHaveBeenCalled();
  });
  it('bloqueia envio duplicado enquanto a proposta esta pendente', () => {
    api.solicitarCredito.and.returnValue(new Subject());
    const app=TestBed.createComponent(AppComponent).componentInstance;
    app.solicitarCredito();app.solicitarCredito();expect(api.solicitarCredito).toHaveBeenCalledTimes(1);
  });
  it('aprovacao nao soma dinheiro ficticio ao saldo', () => {
    api.solicitarCredito.and.returnValue(of({id:1,status:'APROVADO',valorSolicitado:15000,tipoCredito:'PESSOAL'} as any));
    const fixture=TestBed.createComponent(AppComponent);fixture.detectChanges();
    fixture.componentInstance.solicitarCredito();
    expect(fixture.componentInstance.contaInfo.saldo).toBe(12700);
    expect(fixture.componentInstance.activeTab).toBe('historico');
  });
});
