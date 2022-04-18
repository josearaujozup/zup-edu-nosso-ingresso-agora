package br.com.zup.edu.ingressoagora;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

import br.com.zup.edu.ingressoagora.model.EstadoIngresso;

public class IngressoRequest {
	
	@NotNull
    @Enumerated(EnumType.STRING)
	private EstadoIngresso estado;
	
	public IngressoRequest(EstadoIngresso estado) {
		this.estado = estado;
	}
	
	public IngressoRequest() {
		
	}

	public EstadoIngresso getEstado() {
		return estado;
	}
	
	
	
}
