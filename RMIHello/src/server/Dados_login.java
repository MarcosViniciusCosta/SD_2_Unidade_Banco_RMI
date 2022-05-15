package server;

import java.io.Serializable;

public class Dados_login implements Serializable{

	private String usuario;
	private String senha;
	private int tipo;	// 0 usuario, 1 funcionario
	
	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public int getTipo() {
		return tipo;
	}

	public void setTipo(int tipo) {
		this.tipo = tipo;
	}

	public Dados_login(String usuario,String senha) 
	{
		setUsuario(usuario);
		setSenha(senha);
		setTipo(0);			// por padrão conta é criada com nível cliente
	}

	public Dados_login() 
	{
		setTipo(0);
	}
	
	
	
	
	public void elevar_nivel_para_funcionario()
	{
		setTipo(1);			// por padrão conta é criada com nível cliente
	}
}
