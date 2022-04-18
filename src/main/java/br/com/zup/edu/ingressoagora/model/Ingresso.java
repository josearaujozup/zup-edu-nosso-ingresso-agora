package br.com.zup.edu.ingressoagora.model;

import static br.com.zup.edu.ingressoagora.model.EstadoIngresso.NAOCONSUMIDO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Entity
public class Ingresso {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	private EstadoIngresso estado = NAOCONSUMIDO;

	@Column(nullable = false)
	private LocalDateTime compradoEm = LocalDateTime.now();

	@ManyToOne(optional = false)
	private Evento evento;

	public Ingresso(EstadoIngresso estado) {
		this.estado = estado;
	}

	/**
	 * @deprecated construtor para uso exclusivo do Hibernate
	 */
	@Deprecated
	public Ingresso() {
	}

	public Long getId() {
		return id;
	}

	public void setEstado(EstadoIngresso estado) {
		this.estado = estado;
	}

//	public boolean isNaoConsumido() {
//		boolean retorno = false;
//
//		if (this.estado == EstadoIngresso.NAOCONSUMIDO) {
//			retorno = true;
//		}
//
//		System.out.println("teste: " + retorno);
//		return retorno;
//	}
//
//	public boolean isMinimoUmDiaAntes() {
//		
//		LocalDate hoje = LocalDate.now();
//		Period periodo = Period.between(this.evento.getData(), hoje);
//		
//		if(periodo.getDays() >= 1) {
//			return true;
//		}
//		
//		return false;
//	}

	public void cancela() {

		if (!isNaoConsumido()) {
			throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,"Ingresso já consumido ou cancelado");
		}

		if (!isDentroDoPrazoDeCancelamento()) {
			throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,"Ingresso fora do prazo de cancelamento");
		}

		this.estado = EstadoIngresso.CANCELADO;
	}

	private boolean isNaoConsumido() {
		return this.estado == EstadoIngresso.NAOCONSUMIDO;
	}

	private boolean isDentroDoPrazoDeCancelamento() {
		LocalDate hoje = LocalDate.now();
		LocalDate dataDoEvento = this.evento.getData();
		int prazoEmDias = Period.between(dataDoEvento, hoje).getDays();
		return prazoEmDias >= 1;
	}

}
