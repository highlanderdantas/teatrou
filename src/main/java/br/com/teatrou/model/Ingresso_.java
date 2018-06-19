package br.com.teatrou.model;

import br.com.teatrou.model.enums.FaixaEtariaEnum;
import br.com.teatrou.model.enums.SituacaoEnum;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Ingresso.class)
public abstract class Ingresso_ {

	public static volatile SingularAttribute<Ingresso, Compra> compra;
	public static volatile SingularAttribute<Ingresso, Long> codigo;
	public static volatile SingularAttribute<Ingresso, Evento> evento;
	public static volatile SingularAttribute<Ingresso, SituacaoEnum> situacao;
	public static volatile SingularAttribute<Ingresso, Boolean> ativo;
	public static volatile SingularAttribute<Ingresso, FaixaEtariaEnum> faixaEtaria;

}

