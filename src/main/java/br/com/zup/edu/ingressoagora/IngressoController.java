package br.com.zup.edu.ingressoagora;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import br.com.zup.edu.ingressoagora.model.Ingresso;
import br.com.zup.edu.ingressoagora.repository.IngressoRepository;

@RestController
@RequestMapping("/ingressos")
public class IngressoController {
	
	private final IngressoRepository repository;

	public IngressoController(IngressoRepository repository) {
		this.repository = repository;
	}
	
	@PatchMapping("/{id}")
	public ResponseEntity<?> atualizar(@PathVariable("id") Long idIngresso, @RequestBody @Valid IngressoRequest request){
		
		Ingresso ingresso = repository.findById(idIngresso).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Ingresso não encontrado"));
		
//		if(!ingresso.isNaoConsumido() || !ingresso.isMinimoUmDiaAntes()) {
//			throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,"impossivel atualizar ingresso");
//		}
//		
//		ingresso.setEstado(request.getEstado());
		
		ingresso.cancela();
		
		repository.save(ingresso);
		
		return ResponseEntity.noContent().build();
	}
	
	
}
