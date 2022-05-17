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
 * Servidor cria instância da implementação do objeto remoto, exporta-o e 
 * vincula essa instância a um nome no RMI Registry
 * O objeto remoto pode ser implementado no próprio servidor ou em alguma outra classe 
 * 
 * */

public class Server implements Operacoes_banco {

	public Server() {}
	static ArrayList<Conta> base_de_dados; 
	// implementação do método oi()
	// teste


	public static void main(String args[]) {

		try {
			ler_base_de_dados_do_txt();
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
			if(temporaria.getDados_login().getSenha().equals(senha) &&
					temporaria.getDados_login().getUsuario().equals(login))
			{
				return temporaria;
			}
		}
		return null;
	}

	@Override
	public Conta criar_conta(Conta c) throws RemoteException {

		base_de_dados.add(c);
		salvar_base_de_dados_no_txt();
		return c;
	}

	@Override
	public Conta editar_conta(Conta c) throws RemoteException {

		Conta temporaria = buscar_conta(c.getNumeroContaCliente());

		if(temporaria == null)
		{
			System.out.println("Impossivel editar conta não existente");
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
			System.out.println("Impossível remover conta inexistente");
		}else
		{
			base_de_dados.remove(removida);
			return true;
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

			retorno +="Número de conta: " + temporaria.getNumeroContaCliente()+"\n";
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
			System.out.println("Erro, conta não encontrada");
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
		
		Conta conta_atual = buscar_conta(c.getNumeroContaCliente());
		
		
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
		
		salvar_base_de_dados_no_txt();
		return true;
	}
	
	
	private Conta buscar_conta(long id_conta_buscada)
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
		Conta conta_alvo = buscar_conta(id_conta_alvo);
		if(conta_alvo == null)
		{
			return false;
		}else
		{
			origem.setSaldo(origem.getSaldo()-valor);
			conta_alvo.setSaldo(conta_alvo.getSaldo()+valor);
			salvar_base_de_dados_no_txt();
		}
		return true;
	}
	
	private static void ler_base_de_dados_do_txt()
	{
		String caminho = "base_de_dados.txt";
		File arquivo = new File(caminho);
		
		try {
			FileInputStream fileinput = new FileInputStream(arquivo);
			ObjectInput objectinput = new ObjectInputStream(fileinput);
			
			base_de_dados = (ArrayList<Conta>) objectinput.readObject();
			Conta.setcontroladorNumeroConta(base_de_dados.size());
			System.out.println("Base de dados iniciando com o tamanho "+ base_de_dados.size());
			objectinput.close();
			fileinput.close();
			
		} catch (FileNotFoundException e) {
			//e.printStackTrace();
			// base de dados não encontrada, inicializando do zero
			System.out.println("Base de dados iniciando vazia");
			base_de_dados = new ArrayList<Conta>();
			Conta.setcontroladorNumeroConta(base_de_dados.size());
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
					System.out.println("Arquivo não criado");
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
				retorno +="Número de conta: " + temporaria.getNumeroContaCliente()+"\n";
				retorno +="Nome do cliente: " + temporaria.getNomeCliente()+"\n";
				//retorno +="Saldo do cliente: " + temporaria.getSaldo()+"\n\n\n";
			}
			
		}
		return retorno;
	}
	
	
}
