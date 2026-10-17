import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { CreditoService } from './credito.service';
import { AppConfigService } from './app-config.service';

describe('Contratos HTTP legados', () => {
  let service: CreditoService; let http: HttpTestingController;
  beforeEach(() => {
    TestBed.configureTestingModule({providers:[provideHttpClient(),provideHttpClientTesting(),
      {provide:AppConfigService,useValue:{plataformaCreditoUrl:'http://localhost:8080/api/credito'}}]});
    service=TestBed.inject(CreditoService);http=TestBed.inject(HttpTestingController);
  });
  afterEach(()=>http.verify());
  it('consulta conta e historico na Plataforma com CPF normalizado', () => {
    service.consultarCoreBancario('123.456.789-00').subscribe();
    service.consultarStatus('123.456.789-00').subscribe();
    for(const path of ['consultarClienteCore','consultarStatusCredito']) {
      const req=http.expectOne('http://localhost:8080/api/credito/'+path);
      expect(req.request.method).toBe('POST');expect(req.request.body).toEqual({cpfCliente:'12345678900'});req.flush([]);
    }
  });
});
