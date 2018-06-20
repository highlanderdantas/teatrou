package br.com.teatrou.service;

import java.time.LocalDate;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import br.com.teatrou.exception.EventoInexistenteException;
import br.com.teatrou.exception.UsuarioInexistenteOuDeslogadoException;
import br.com.teatrou.model.Evento;
import br.com.teatrou.model.Usuario;
import br.com.teatrou.repository.EventoRepository;
import br.com.teatrou.repository.UsuarioRepository;
import br.com.teatrou.storage.S3;
import br.com.teatrou.token.AuthenticationHelper;

@Service
public class EventoService {

	@Autowired
	private EventoRepository eventoRepository;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private S3 s3;

	@Autowired
	private AuthenticationHelper authenticationHelper;

	public Page<Evento> listar(Pageable pageable) {
		return eventoRepository.findAll(pageable);
	}

	public Evento salvar(Evento evento) {

		Usuario usuario = authenticationHelper.getUsuario();
		if (usuario == null) {
			throw new UsuarioInexistenteOuDeslogadoException();
		}
		if (StringUtils.hasText(evento.getAnexo())) {
			s3.salvar(evento.getAnexo());
		}
		evento.setUsuario(usuario);

		return eventoRepository.save(evento);
	}

	private void validUsuario(Evento evento) {
		Usuario usuario = null;
		Long codigo = authenticationHelper.getUsuario().getCodigo();
		if (codigo != null) {
			usuario = usuarioRepository.findOne(codigo);
		}
		if (usuario == null) {
			throw new UsuarioInexistenteOuDeslogadoException();
		}
	}

	public Evento atualizar(Long codigo, Evento evento) {
		Evento eventoSalvo = BuscaPeloCodigo(codigo);
		if (!evento.getUsuario().equals(eventoSalvo.getUsuario())) {
			validUsuario(evento);
		}
		if (StringUtils.isEmpty(evento.getAnexo()) &&
			StringUtils.hasText(eventoSalvo.getAnexo())) {
			s3.remover(eventoSalvo.getAnexo());
		} 
		else if (StringUtils.hasText(evento.getAnexo()) &&
				    !evento.getAnexo().equals(eventoSalvo.getAnexo())) {
			
			s3.substituir(eventoSalvo.getAnexo(), evento.getAnexo());
		}

		BeanUtils.copyProperties(evento, eventoSalvo, "codigo");
		return salvar(eventoSalvo);
	}

	private Evento BuscaPeloCodigo(Long codigo) {
		Evento eventoSalvo = eventoRepository.findOne(codigo);
		if (eventoSalvo == null) {
			throw new EmptyResultDataAccessException(1);
		}
		return eventoSalvo;
	}

	public Evento atualizarDataEvento(Long codigo, LocalDate dataEvento) {
		Evento eventoSalvo = BuscaPeloCodigo(codigo);
		eventoSalvo.setDataEvento(dataEvento);
		return salvar(eventoSalvo);
	}

	public Evento ativarEvento(Long codigo) {
		Evento eventoSalvo = BuscaPeloCodigo(codigo);
		eventoSalvo.setAtivo(true);
		return salvar(eventoSalvo);
	}

	public Evento atualizarDescricao(Long codigo, String descricao) {
		Evento eventoSalvo = BuscaPeloCodigo(codigo);
		eventoSalvo.setDescricao(descricao);
		return salvar(eventoSalvo);
	}

	public void retomaIngresso(String codigo) {
		Evento evento = BuscaPeloCodigo(Long.parseLong(codigo));
		if(evento == null) 
			throw new EventoInexistenteException();
		evento.setQuantidadeIngresso(evento.getQuantidadeIngresso() + 1);
		eventoRepository.save(evento);
	}

}
