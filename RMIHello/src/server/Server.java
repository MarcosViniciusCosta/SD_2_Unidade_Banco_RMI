package server;

import java.rmi.registry.Registry;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import server.Conta;

/* 
 * Servidor cria inst�ncia da implementa��o do objeto remoto, exporta-o e 
 * vincula essa inst�ncia a um nome no RMI Registry
 * O objeto remoto pode ser implementado no pr�prio servidor ou em alguma outra classe 
 * 
 * */

public class Server implements Operacoes_banco {

	public Server() {}
	static ArrayList<Conta> base_de_dados; 
	private static long controladorNumeroConta;
	// implementa��o do m�todo oi()
	// teste


	public static void main(String args[]) {

		try {
			ler_base_de_dados_do_txt();
			//criar objeto servidor
			Server refObjetoRemoto = new Server();

			// cria um objeto stub do servidor

			/* O m�todo est�tico UnicastRemoteObject.exportObject exporta o objeto remoto 
			 * fornecido para receber invoca��es de m�todo remoto em uma porta 
			 * TCP an�nima e retorna o stub para o objeto remoto para passar aos clientes. 
			 * 
			 * Como resultado da chamada de exportObject, o RMI pode come�ar a escutar em um 
			 * novo server socket ou pode usar um server socket compartilhado para aceitar 
			 * chamadas remotas de entrada para o objeto remoto. 
			 * 
			 * RemoteException em caso de falha*/

			Operacoes_banco stub= (Operacoes_banco) UnicastRemoteObject.exportObject(refObjetoRemoto, 0);


			// Acionar RMIRegistry na porta padr�o(1099)  
			/* 
			 * Sem essa linha � preciso lan�ar o RMiRegistry via linha de comando
			 */
			LocateRegistry.createRegistry( Registry.REGISTRY_PORT ); 

			// associa o stub do servidor (objeto remoto) no rmiregistry

			/* O m�todo est�tico LocateRegistry.getRegistry, que n�o recebe 
			 * nenhum argumento, retorna um stub que implementa a interface 
			 * remota java.rmi.registry.Registry e envia invoca��es para o 
			 * registro (rmiregistry) no host do servidor na porta de registro padr�o de 1099. 
			 */ 
			//System.out.println(InetAddress.getLocalHost().getHostAddress());
			//Registry registry = LocateRegistry.getRegistry(InetAddress.getLocalHost().getHostAddress());

			Registry registro = LocateRegistry.getRegistry();

			/* O m�todo bind � ent�o chamado no stub do registro para vincular 
			 * o stub do objeto remoto ao nome "Hello" no registro.*/

			registro.bind("Banco", stub);

			System.err.println("Servidor pronto:");

		} catch (Exception e) {
			System.err.println("Server exception: " + e.toString());
			e.printStackTrace();
		}
	}



	@Override
	public Conta fazer_login(String login, String senha) throws RemoteException {

		for(int cont=0;cont<base_de_dados.size();cont++)
		{
			Conta temporaria = base_de_dados.get(cont);
			if(temporaria.getDados_login().getSenha().equals(senha) &&
					temporaria.getDados_login().getUsuario().equals(login))
			{
				return temporaria;
			}
		}
		return null;
	}

	@Override
	public Conta criar_conta(Conta c) throws RemoteException 
	{
		controladorNumeroConta++;
		c.setNumeroContaCliente(controladorNumeroConta);
		base_de_dados.add(c);
		salvar_base_de_dados_no_txt();
		return c;
	}

	@Override
	public Conta editar_conta(Conta c) throws RemoteException {

		Conta temporaria = buscar_conta(c.getNumeroContaCliente());

		if(temporaria == null)
		{
			System.out.println("Impossivel editar conta n�o existente");
		}else
		{
			base_de_dados.remove(temporaria);
			base_de_dados.add(c);
		}

		salvar_base_de_dados_no_txt();
		return c;
	}

	@Override
	public boolean remover_conta(long id) throws RemoteException {

		Conta removida = buscar_conta(id);
		if(removida == null)
		{
			//System.out.println("Imposs�vel remover conta inexistente");
		}else
		{
			// s� podemos remover contas que solicitaram remo��o
			if(removida.isSolicitada_remocao() == false) 
			{
				return false;
			}else
			{
				base_de_dados.remove(removida);
				return true;
			}
			
			
		}

		salvar_base_de_dados_no_txt();
		return false;
	}

	@Override
	public String listar_contas() throws RemoteException 
	{
		String retorno = "";
		for(int cont= 0;cont<base_de_dados.size();cont++)
		{
			Conta temporaria = base_de_dados.get(cont);

			retorno +="N�mero de conta: " + temporaria.getNumeroContaCliente()+"\n";
			retorno +="Nome do cliente: " + temporaria.getNomeCliente()+"\n";
			retorno +="Saldo do cliente: " + temporaria.getSaldo()+"\n\n\n";
		}
		return retorno;
	}

	@Override
	public boolean sacar_valor(Conta c,double valor) throws RemoteException 
	{
		Conta conta_atual = buscar_conta(c.getNumeroContaCliente());


		if(conta_atual == null)
		{
			System.out.println("Erro, conta n�o encontrada");
		}else
		{
			//saldo insuficiente
			if(valor > conta_atual.getSaldo())
			{
				System.out.println("Imposs�vel sacar um valor maior que o saldo atual");
			}else
			{
				//saldo suficiente
				conta_atual.setSaldo(conta_atual.getSaldo()-valor);
				//System.out.println("Novo saldo = "+ conta_atual.getSaldo());
				return true;
			}
		}



		salvar_base_de_dados_no_txt();
		return false;
	}

	@Override
	public boolean depositar_valor(Conta c,double valor) throws RemoteException {

		Conta conta_atual = buscar_conta(c.getNumeroContaCliente());

		if(conta_atual == null)
		{
			System.out.println("Erro, conta n�o encontrada");
			return false;
		}else
		{
			// adicionando no saldo
			conta_atual.setSaldo(conta_atual.getSaldo()+valor);
			//System.out.println("Novo saldo = "+ conta_atual.getSaldo());
		}

		salvar_base_de_dados_no_txt();
		return true;
	}


	@Override
	public double consultar_saldo(Conta c) throws RemoteException 
	{

		Conta conta_atual = buscar_conta(c.getNumeroContaCliente());


		if(conta_atual == null)
		{
			System.out.println("Erro, conta n�o encontrada");
			return -1;
		}else
		{
			// adicionando no saldo
			System.out.println("Saldo atual = "+ conta_atual.getSaldo());
			return conta_atual.getSaldo();
		}


	}


	@Override
	public boolean aderir_renda_fixa(Conta c) throws RemoteException 
	{

		Conta conta_atual = buscar_conta(c.getNumeroContaCliente());


		if(conta_atual == null)
		{
			System.out.println("Erro, conta n�o encontrada");
			return false;
		}else
		{
			//aderindo a renda fixa
			conta_atual.setTipoInvestimento(1);
			System.out.println("Conta "+ conta_atual.getNumeroContaCliente() +" Aderiu a renda fixa.");
		}

		salvar_base_de_dados_no_txt();
		return true;
	}

	@Override
	public Conta buscar_conta(long id_conta_buscada)
	{
		for(int cont=0;cont<base_de_dados.size();cont++)
		{
			Conta temporaria = base_de_dados.get(cont);
			if(temporaria.getNumeroContaCliente() == id_conta_buscada) return temporaria;
		}

		return null;
	}

	@Override
	public boolean transferir_saldo (Conta origem,double valor,long id_conta_alvo)
	{
		try {
			Conta conta_alvo = buscar_conta(id_conta_alvo);
			if(conta_alvo == null)
			{
				return false;
			}else
			{
				origem.setSaldo(origem.getSaldo()-valor);
				// atualizando a base de dados no arraylist
				editar_conta(origem);

				conta_alvo.setSaldo(conta_alvo.getSaldo()+valor);
				salvar_base_de_dados_no_txt();
			}
			return true;
		} catch (RemoteException e) {
			e.printStackTrace();
			return false;
		}
		
	}

	private static void ler_base_de_dados_do_txt()
	{
		String caminho = "base_de_dados.txt";
		File arquivo = new File(caminho);

		try {
			FileInputStream fileinput = new FileInputStream(arquivo);
			ObjectInput objectinput = new ObjectInputStream(fileinput);

			base_de_dados = (ArrayList<Conta>) objectinput.readObject();
			controladorNumeroConta = descobrir_maior_id();
			System.out.println("Base de dados iniciando com o tamanho "+ controladorNumeroConta);
			objectinput.close();
			fileinput.close();

		} catch (FileNotFoundException e) {
			//e.printStackTrace();
			// base de dados n�o encontrada, inicializando do zero
			System.out.println("Base de dados iniciando vazia");
			base_de_dados = new ArrayList<Conta>();
			controladorNumeroConta = descobrir_maior_id();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}


	}


	private static boolean salvar_base_de_dados_no_txt()
	{

		String caminho = "base_de_dados.txt";
		File arquivo = new File(caminho);

		if(arquivo.exists() == false) 
		{
			try {
				arquivo.createNewFile();
			} catch (IOException e) {
				System.out.println("Arquivo n�o criado");
				e.printStackTrace();
				return false;
			}
		}


		try {
			FileOutputStream fileoutputstream = new FileOutputStream(arquivo);

			ObjectOutputStream objectoutputstream = new ObjectOutputStream(fileoutputstream);

			objectoutputstream.writeObject(base_de_dados);

			objectoutputstream.flush();
			fileoutputstream.flush();

			objectoutputstream.close();
			fileoutputstream.close();


		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}


		return true;
	} 

	@Override
	public String listar_contas_a_serem_removidas() throws RemoteException 
	{
		String retorno = "";
		for(int cont= 0;cont<base_de_dados.size();cont++)
		{
			Conta temporaria = base_de_dados.get(cont);
			if(temporaria.isSolicitada_remocao() == true)
			{
				retorno +="N�mero de conta: " + temporaria.getNumeroContaCliente()+"\n";
				retorno +="Nome do cliente: " + temporaria.getNomeCliente()+"\n";
				//retorno +="Saldo do cliente: " + temporaria.getSaldo()+"\n\n\n";
			}

		}
		return retorno;
	}

	private static long descobrir_maior_id()
	{
		long maior_id = 0;

		for(int cont=0;cont<base_de_dados.size();cont++)
		{
			if(base_de_dados.get(cont).getNumeroContaCliente() > maior_id)
			{
				maior_id = base_de_dados.get(cont).getNumeroContaCliente();
			}
		}
		return maior_id;
	}

	public Conta buscar_conta(String parametro, int escolha_atributo)
	{
		System.out.println("Chegou aqui");
		Conta retorno = null;
		System.out.println();
		switch(escolha_atributo)
		{
		//busca por nome
		case 2:
			System.out.println("Parametro = "  + parametro);
			System.out.println("Busca por nome\n");
			for(int cont=0;cont<base_de_dados.size();cont++)
			{
				Conta temporaria = base_de_dados.get(cont);
				if(temporaria.getNomeCliente().equals(parametro)) return temporaria;
			}

			return null;
		//busca por cpf
		case 3:
			for(int cont=0;cont<base_de_dados.size();cont++)
			{
				Conta temporaria = base_de_dados.get(cont);
				if(temporaria.getCpfCliente().equals(parametro)) return temporaria;
			}

			return null;
		//busca por endere�o
		case 4:
			for(int cont=0;cont<base_de_dados.size();cont++)
			{
				Conta temporaria = base_de_dados.get(cont);
				if(temporaria.getEnderecoCliente().equals(parametro)) return temporaria;
			}

			return null;
		//busca por login
		case 5:
			for(int cont=0;cont<base_de_dados.size();cont++)
			{
				Conta temporaria = base_de_dados.get(cont);
				if(temporaria.getDados_login().getUsuario().equals(parametro)) return temporaria;
			}

			return null;
		
			
		}
		
		
		return retorno;
	}
	
	
}
