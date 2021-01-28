package br.com.calc.visao;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class Calculadora extends JFrame{

	public Calculadora() {
		
		setSize(232, 322);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);//abre no centro da tela com valor null
		setVisible(true);
		
	}
	
	public static void main(String[] args) {
		new Calculadora();
	}
}
