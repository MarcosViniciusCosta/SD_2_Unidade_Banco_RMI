package server;
import java.io.Serializable;
import java.util.Date;


public class Conta implements Serializable{
	
	private long numeroContaCliente;
	private String cpfCliente;
	private String nomeCliente;
	private String enderecoCliente;
	private Date dataNascimentoCliente;
	private String numeroTelefonicoCliente;
	private Dados_login dados_login;
	private double saldo;
	private int tipoInvestimento;	//	0 - poupan�a, 1 - renda fixa
	private boolean solicitada_remocao;
	
	
	
	
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
		
		setCpfCliente(cpfCliente);
		setNomeCliente(nomeCliente);
		setEnderecoCliente(enderecoCliente);
		setDataNascimentoCliente(dataNascimentoCliente);
		setNumeroTelefonicoCliente(numeroTelefonicoCliente);
		//come�a como conta poupan�a com saldo zero
		setSaldo(0);
		setDados_login(dados_login);
		setTipoInvestimento(0);
		setSolicitada_remocao(false);;
	}
	
	
	public boolean isSolicitada_remocao() {
		return solicitada_remocao;
	}


	public void setSolicitada_remocao(boolean solicitada_remocao) {
		this.solicitada_remocao = solicitada_remocao;
	}


	public Conta() {
		
	}


	public Dados_login getDados_login() {
		return dados_login;
	}


	public void setDados_login(Dados_login dados_login) {
		this.dados_login = dados_login;
	}


	


	

	public void setNumeroContaCliente(long numeroContaCliente) {
		this.numeroContaCliente = numeroContaCliente;
	}

	public long getNumeroContaCliente() {
		return numeroContaCliente;
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
			System.out.println("Imposs�vel cadastrar esse nome, por favor digite um nome v�lido!");
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
			System.out.println("Imposs�vel cadastrar esse endere�o, por favor digite um endere�o v�lido!");
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
			System.out.println("Imposs�vel cadastrar esse n�mero telef�nico, por favor digite um n�mero telef�nico v�lido!");
		}else
		{
			this.numeroTelefonicoCliente = numeroTelefonicoCliente;
		}
	}
	
	public String toString() 
	{
		String retorno = "";
		
		retorno += ("N�mero da conta do usu�rio: " + getNumeroContaCliente()+"\n");
		retorno += ("Nome do usu�rio: " + getNomeCliente()+"\n");
		retorno += ("CPF do usu�rio: " + getCpfCliente()+"\n");
		retorno += ("Endere�o do usu�rio: " + getEnderecoCliente()+"\n");
		retorno += ("Data de nascimento do usu�rio: " + getDataNascimentoCliente()+"\n");
		retorno += ("Numero do telefone do usuario: " + getNumeroTelefonicoCliente()+"\n");
		retorno += ("Login do usuario: " + getDados_login().getUsuario()+"\n");
		retorno += ("Senha do usuario: " + getDados_login().getSenha()+"\n");
		if( getDados_login().getTipo() == 0)
		{
			retorno += ("Usu�rio do tipo cliente\n");
		}else
		{
			retorno += ("Usu�rio do tipo funcion�rio\n");
		}
		retorno += ("Saldo do usuario: " + getSaldo()+"\n");
		if( getTipoInvestimento() == 0)
		{
			retorno += ("Investimento do usu�rio � do tipo poupan�a\n");
		}else
		{
			retorno += ("Investimento do usu�rio � do tipo renda fixa\n");
		}
		if( isSolicitada_remocao())
		{
			retorno += ("Solicita��o de remo��o ativa\n");
		}else
		{
			retorno += ("Solicita��o de remo��o inativa\n");
		}
		
		
		return retorno;
	}
	
}
