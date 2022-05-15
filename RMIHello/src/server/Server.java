package server;

import java.rmi.registry.Registry;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import server.Conta;

/* 
 * Servidor cria instância da implementação do objeto remoto, exporta-o e 
 * vincula essa instância a um nome no RMI Registry
 * O objeto remoto pode ser implementado no próprio servidor ou em alguma outra classe 
 * 
 * */

public class Server implements Operacoes_banco {

	public Server() {}
	static ArrayList<Conta> base_de_dados; 
	// implementação do método oi()


	public static void main(String args[]) {

		try {
			base_de_dados = new ArrayList<Conta>();
			//criar objeto servidor
			Server refObjetoRemoto = new Server();

			// cria um objeto stub do servidor

			/* O método estático UnicastRemoteObject.exportObject exporta o objeto remoto 
			 * fornecido para receber invocações de método remoto em uma porta 
			 * TCP anônima e retorna o stub para o objeto remoto para passar aos clientes. 
			 * 
			 * Como resultado da chamada de exportObject, o RMI pode começar a escutar em um 
			 * novo server socket ou pode usar um server socket compartilhado para aceitar 
			 * chamadas remotas de entrada para o objeto remoto. 
			 * 
			 * RemoteException em caso de falha*/

			Operacoes_banco stub= (Operacoes_banco) UnicastRemoteObject.exportObject(refObjetoRemoto, 0);


			// Acionar RMIRegistry na porta padrão(1099)  
			/* 
			 * Sem essa linha é preciso lançar o RMiRegistry via linha de comando
			 */
			LocateRegistry.createRegistry( Registry.REGISTRY_PORT ); 

			// associa o stub do servidor (objeto remoto) no rmiregistry

			/* O método estático LocateRegistry.getRegistry, que não recebe 
			 * nenhum argumento, retorna um stub que implementa a interface 
			 * remota java.rmi.registry.Registry e envia invocações para o 
			 * registro (rmiregistry) no host do servidor na porta de registro padrão de 1099. 
			 */ 
			//System.out.println(InetAddress.getLocalHost().getHostAddress());
			//Registry registry = LocateRegistry.getRegistry(InetAddress.getLocalHost().getHostAddress());

			Registry registro = LocateRegistry.getRegistry();

			/* O método bind é então chamado no stub do registro para vincular 
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
			if(temporaria.getSenha().equals(senha) &&
					temporaria.getLogin().equals(login))
			{
				return temporaria;
			}
		}
		return null;
	}

	@Override
	public Conta criar_conta(Conta c) throws RemoteException {

		base_de_dados.add(c);

		return c;
	}

	@Override
	public Conta editar_conta(Conta c) throws RemoteException {

		Conta temporaria = buscar_conta(c);

		if(temporaria == null)
		{
			System.out.println("Impossivel editar conta não existente");
		}else
		{
			base_de_dados.remove(temporaria);
			base_de_dados.add(c);
		}
		
		return c;
	}

	@Override
	public boolean remover_conta(Conta c) throws RemoteException {

		Conta removida = buscar_conta(c);
		if(removida == null)
		{
			System.out.println("Impossível remover conta inexistente");
		}else
		{
			base_de_dados.remove(removida);
			return true;
		}

		return false;
	}

	@Override
	public String listar_contas() throws RemoteException 
	{
		String retorno = "";
		for(int cont= 0;cont<base_de_dados.size();cont++)
		{
			Conta temporaria = base_de_dados.get(cont);

			retorno +="Número de conta: " + temporaria.getNumeroContaCliente()+"\n";
			retorno +="Nome do cliente: " + temporaria.getNomeCliente()+"\n";
			retorno +="Saldo do cliente: " + temporaria.getSaldo()+"\n\n\n";
		}
		return retorno;
	}

	@Override
	public boolean sacar_valor(Conta c,double valor) throws RemoteException 
	{

		Conta conta_atual = buscar_conta(c);
		

		if(conta_atual == null)
		{
			System.out.println("Erro, conta não encontrada");
		}else
		{
			//saldo insuficiente
			if(valor > conta_atual.getSaldo())
			{
				System.out.println("Impossível sacar um valor maior que o saldo atual");
			}else
			{
				//saldo suficiente
				conta_atual.setSaldo(conta_atual.getSaldo()-valor);
				System.out.println("Novo saldo = "+ conta_atual.getSaldo());
			}
		}




		return false;
	}

	@Override
	public boolean depositar_valor(Conta c,double valor) throws RemoteException {

		Conta conta_atual = buscar_conta(c);

		if(conta_atual == null)
		{
			System.out.println("Erro, conta não encontrada");
			return false;
		}else
		{
			// adicionando no saldo
			conta_atual.setSaldo(conta_atual.getSaldo()+valor);
			System.out.println("Novo saldo = "+ conta_atual.getSaldo());
		}

		return true;
	}

	
	@Override
	public double consultar_saldo(Conta c) throws RemoteException 
	{
		
		Conta conta_atual = buscar_conta(c);
		
		
		if(conta_atual == null)
		{
			System.out.println("Erro, conta não encontrada");
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
		
		Conta conta_atual = buscar_conta(c);
		
		
		if(conta_atual == null)
		{
			System.out.println("Erro, conta não encontrada");
			return false;
		}else
		{
			//aderindo a renda fixa
			conta_atual.setTipoInvestimento(1);
			System.out.println("Conta "+ conta_atual.getNumeroContaCliente() +" Aderiu a renda fixa.");
		}
		
		
		return true;
	}
	
	private Conta buscar_conta(Conta c)
	{
		for(int cont=0;cont<base_de_dados.size();cont++)
		{
			Conta temporaria = base_de_dados.get(cont);
			if(temporaria.getNumeroContaCliente() == c.getNumeroContaCliente()) return temporaria;
		}
		
		return null;
	}
	
	
}
