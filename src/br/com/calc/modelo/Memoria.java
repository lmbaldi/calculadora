package br.com.calc.modelo;

import java.util.ArrayList;
import java.util.List;

public class Memoria {
	
	
	private enum TipoComando{
		ZERAR, NUMERO,SINAL, DIV, MULT, SUB, SOMA, IGUAL, VIRGULA;
	}
	
	private static final Memoria instancia = new Memoria();
	
	private final List<MemoriaObservador> observadores = new ArrayList<>();
	
	private TipoComando ultimaOperacao = null;
	private boolean substituir = false;
	private String textoAtual = "";
	private String textoBuffer = "";

	private Memoria(){}
	
	
	public static Memoria getInstancia() {
		return instancia;
	}
	
	public void adicionarObservador(MemoriaObservador observador) {
		observadores.add(observador);
	}

	public String getTextoAtual() {
		return textoAtual.isEmpty() ? "0" : textoAtual;
	}
	

	public void processarComando(String texto) {
		
		TipoComando tipoComando = detectarTipoComando(texto);
		tipoComandoExecutado(tipoComando, texto);
		
		observadores.forEach(o -> o.valorAlterado(getTextoAtual()));
	}


	private void tipoComandoExecutado(TipoComando tipoComando, String texto) {
		if(tipoComando == null) {
			return;
		} else if (tipoComando == TipoComando.ZERAR) {
			textoAtual = "";
			textoBuffer = "";
			substituir = false;
			ultimaOperacao = null;
		} else if (tipoComando == TipoComando.SINAL && textoAtual.contains("-")) {
			textoAtual = textoAtual.substring(1);
		}else if (tipoComando == TipoComando.SINAL && !textoAtual.contains("-")) {
			textoAtual = "-" + textoAtual;
		} else if (tipoComando == TipoComando.NUMERO || tipoComando == TipoComando.VIRGULA ) {
			textoAtual = substituir ? texto : textoAtual + texto;
			substituir = false;
		}else {
			substituir = true;
			textoAtual = obterResultadoOperacao();
			textoBuffer = textoAtual;
			ultimaOperacao = tipoComando;
		}
		
	}


	private String obterResultadoOperacao() {
		if(ultimaOperacao == null || ultimaOperacao == TipoComando.IGUAL) {
			return textoAtual;
		}
		//conveter string para double
		double numeroBuffer = Double.parseDouble(textoBuffer.replace(",", "."));
		double numeroAtual = Double.parseDouble(textoAtual.replace(",", "."));
		
		return calculoDaOperacaoExecutada(numeroBuffer, numeroAtual);
		
	}


	private  String calculoDaOperacaoExecutada(double numeroBuffer, double numeroAtual) {
		double resultado = 0;
		
		//verifica operacado executada
		if(ultimaOperacao == TipoComando.SOMA) {
			resultado = numeroBuffer + numeroAtual;
		} else if (ultimaOperacao == TipoComando.SUB) {
			resultado = numeroBuffer - numeroAtual;
		} else if (ultimaOperacao == TipoComando.MULT) {
			resultado = numeroBuffer * numeroAtual;
		} else if (ultimaOperacao == TipoComando.DIV) {
			resultado = numeroBuffer / numeroAtual;
		}
		
		String resultadoString = Double.toString(resultado).replace(".", ",");
		boolean inteiro = resultadoString.endsWith(",0");
		
		return inteiro ? resultadoString.replace(",0", "") : resultadoString;
		
	}


	private TipoComando detectarTipoComando(String texto) {
		if(textoAtual.isEmpty() && texto == "0") {
			return null;
		}
		
		try {
			Integer.parseInt(texto);
			return TipoComando.NUMERO;
		} catch (NumberFormatException e) {
			return comandoExecutado(texto);
		}
		
	}


	private TipoComando comandoExecutado(String texto) {
		if("AC".contentEquals(texto)) {
			return TipoComando.ZERAR;
		} else if ("/".contentEquals(texto)) {
			return TipoComando.DIV;
		}else if ("*".contentEquals(texto)) {
			return TipoComando.MULT;
		} else if ("+".contentEquals(texto)) {
			return TipoComando.SOMA;
		} else if ("-".contentEquals(texto)) {
			return TipoComando.SUB;
		} else if ("=".contentEquals(texto)) {
			return TipoComando.IGUAL;
		} else if ("+/-".contentEquals(texto)) {
			return TipoComando.SINAL;
		}else if (",".contentEquals(texto) && !textoAtual.contains(",")) {
			return TipoComando.VIRGULA;
		}
		return null;					
	}
	
	
}
