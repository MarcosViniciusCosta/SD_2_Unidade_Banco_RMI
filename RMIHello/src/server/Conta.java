package server;
import java.io.Serializable;
import java.util.Date;


public class Conta implements Serializable{
	private static long controladorNumeroConta = 0;
	private long numeroContaCliente;
	private String cpfCliente;
	private String nomeCliente;
	private String enderecoCliente;
	private Date dataNascimentoCliente;
	private String numeroTelefonicoCliente;
	private Dados_login dados_login;
	private double saldo;
	private String senha;
	private String login;
	private int tipoInvestimento;	//	0 - poupança, 1 - renda fixa
	
	
	
	
	public int getTipoInvestimento() {
		return tipoInvestimento;
	}


	public void setTipoInvestimento(int tipoInvestimento) {
		this.tipoInvestimento = tipoInvestimento;
	}


	public double getSaldo() {
		return saldo;
	}


	public void setSaldo(double saldo) {
		this.saldo = saldo;
	}


	public Conta(String cpfCliente, String nomeCliente, String enderecoCliente,
			Date dataNascimentoCliente, String numeroTelefonicoCliente, Dados_login dados_login)
	{
		// incrementando a quantidade de contas existentes
		controladorNumeroConta++;
		setNumeroContaCliente(controladorNumeroConta);
		setCpfCliente(cpfCliente);
		setNomeCliente(nomeCliente);
		setEnderecoCliente(enderecoCliente);
		setDataNascimentoCliente(dataNascimentoCliente);
		setNumeroTelefonicoCliente(numeroTelefonicoCliente);
		//começa como conta poupança com saldo zero
		setSaldo(0);
		setDados_login(dados_login);
		setTipoInvestimento(0);
	}
	
	
	public Conta() {
		
	}


	public Dados_login getDados_login() {
		return dados_login;
	}


	public void setDados_login(Dados_login dados_login) {
		this.dados_login = dados_login;
	}


	public String getSenha() {
		return senha;
	}


	public void setSenha(String senha) {
		this.senha = senha;
	}


	public String getLogin() {
		return login;
	}


	public void setLogin(String login) {
		this.login = login;
	}


	

	public void setNumeroContaCliente(long numeroContaCliente) {
		this.numeroContaCliente = numeroContaCliente;
	}

	public long getNumeroContaCliente() {
		return numeroContaCliente;
	}
	
	public void setNumeroContaCliente() {
		controladorNumeroConta++;
		this.numeroContaCliente = controladorNumeroConta;
	}
	
	public String getCpfCliente() {
		return cpfCliente;
	}
	
	public void setCpfCliente(String cpfCliente) {
		this.cpfCliente = cpfCliente;
	}
	
	public String getNomeCliente() {
		return nomeCliente;
	}
	
	public void setNomeCliente(String nomeCliente) {
		if(nomeCliente == null || nomeCliente.length() == 0)
		{
			System.out.println("Impossível cadastrar esse nome, por favor digite um nome válido!");
		}else
		{
			this.nomeCliente = nomeCliente;
		}
		
	}
	public String getEnderecoCliente() {
		return enderecoCliente;
	}
	public void setEnderecoCliente(String enderecoCliente) {
		if(enderecoCliente == null || enderecoCliente.length() == 0)
		{
			System.out.println("Impossível cadastrar esse endereço, por favor digite um endereço válido!");
		}else
		{
			this.enderecoCliente = enderecoCliente;
		}
		
	}
	
	public Date getDataNascimentoCliente() {
		return dataNascimentoCliente;
	}
	
	public void setDataNascimentoCliente(Date dataNascimentoCliente) {
		this.dataNascimentoCliente = dataNascimentoCliente;
	}
	
	public String getNumeroTelefonicoCliente() {
		return numeroTelefonicoCliente;
	}
	
	public void setNumeroTelefonicoCliente(String numeroTelefonicoCliente) {
		if(enderecoCliente == null || enderecoCliente.length() == 9)
		{
			System.out.println("Impossível cadastrar esse número telefônico, por favor digite um número telefônico válido!");
		}else
		{
			this.numeroTelefonicoCliente = numeroTelefonicoCliente;
		}
	}
	
}
