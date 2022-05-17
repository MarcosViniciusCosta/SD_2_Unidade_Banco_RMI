package cliente;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;



import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import server.Conta;
import server.Dados_login;
import server.Operacoes_banco;

public class Cliente {

	private Cliente() {}

	public static void main(String[] args) {

		Scanner teclado = new Scanner(System.in);

		System.out.println("Informe o nome/endere�o do RMIRegistry:");
		String host = teclado.nextLine();


		try {    	
			Registry registro = LocateRegistry.getRegistry(host);

			Operacoes_banco stub = (Operacoes_banco) registro.lookup("Banco");

			
            //criando conta para testar stubs
			/*
            Dados_login dados_login = new Dados_login();
            System.out.print("Digite o seu login desejado: ");
            dados_login.setUsuario(teclado.nextLine());
            System.out.print("Digite a sua senha desejada: ");
            dados_login.setSenha(teclado.nextLine());
            System.out.print("Digite o seu nome: ");
            String nome = teclado.nextLine();
            System.out.print("Digite o seu cpf: ");
            String cpf = teclado.nextLine();
            System.out.print("Digite o seu endere�o: ");
            String endereco = teclado.nextLine();
            System.out.print("Digite o seu n�mero telef�nico: ");
            String numero_telefonico = teclado.nextLine();
            System.out.print("Digite a sua data de nascimento: ");
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date data_nascimento = sdf.parse(teclado.next()); 
            Conta c = new Conta(cpf,nome, endereco, data_nascimento,numero_telefonico, dados_login);
            //System.out.println("Conta criada com sucesso");
            stub.criar_conta(c);
			*/

			//System.out.println(stub.listar_contas());

			Conta logada = null;
			boolean sair = false;

			while(sair == false)
			{

				System.out.println("\n1 - Fazer login");
				System.out.println("2 - Sair");

				System.out.print("Digite a op��o ao lado: ");
				int opcao = teclado.nextInt();
				// capturando \n
				teclado.nextLine();
				switch(opcao)
				{
				case 1:
					System.out.println("---------------Login---------------");
					System.out.print("Digite seu login ao lado: ");
					String login = teclado.nextLine();
					System.out.print("Digite sua senha ao lado: ");
					String senha = teclado.nextLine();

					//System.out.println("Login lido = " + login );
					//System.out.println("Senha lida = " + senha );
					logada = stub.fazer_login(login, senha);

					if(logada == null)
					{
						System.out.println("Dados de login inv�lido");
					}else
					{
						if(logada.getDados_login().getTipo() == 0)
						{
							System.out.println("Bem-vindo, Usu�rio " + logada.getNomeCliente());
							menu_usuario(teclado,logada,stub);
							sair = true;
							break;
						}else
						{
							System.out.println("Bem-vindo, funcion�rio " + logada.getNomeCliente());
						}
					}
					break;
				case 2:
					System.out.println("Saindo...");
					sair = true;
					break;
				}



			}

		} catch (Exception e) {
			System.err.println("Client exception: " + e.toString());
			e.printStackTrace();
		}
		teclado.close();

	}


	public static int menu_usuario(Scanner teclado, Conta logada,Operacoes_banco stub ) throws RemoteException
	{
		
		int opcao = 0;
		while(opcao != 11)
		{
			System.out.println("\n1 - Consultar saldo");
			System.out.println("2 - Realizar saque");
			System.out.println("3 - Realizar dep�sito");
			System.out.println("4 - Realizar transfer�ncia");
			System.out.println("5 - Consultar resultados dos investimentos");
			System.out.println("6 - Mudar o regime dos investimentos");
			System.out.println("7 - Solicitar remo��o da conta");
			System.out.println("8 - Listar contas");
			System.out.println("9 - Alterar dados da conta");
			System.out.println("10 - Alterar n�vel de conta para funcion�rio");
			System.out.println("11 - Sair");
			System.out.print("Digite a op��o ao lado: ");
			opcao = teclado.nextInt();
			//capturando o \n
			teclado.nextLine();
			
			
			switch(opcao)
			{
			case 1:
				System.out.println("Saldo da conta atual = " + logada.getSaldo());
				break;
			case 2:
				System.out.println("Digite o valor a ser sacado:");
				double valor_a_ser_sacado = teclado.nextDouble();
				//capturando o \n
				teclado.nextLine();
				try {
					boolean deu_certo = stub.sacar_valor(logada, valor_a_ser_sacado);

					if(deu_certo == false)
					{
						System.out.println("Saldo insuficiente");
					}else
					{
						logada = stub.fazer_login(logada.getDados_login().getUsuario(),
								logada.getDados_login().getSenha());
						System.out.println("Novo saldo: "+ logada.getSaldo());
					}


				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;

			case 3:

				System.out.println("Digite o valor a ser depositado:");
				double valor_a_ser_depositado = teclado.nextDouble();
				//capturando o \n
				teclado.nextLine();
				try {
					boolean deu_certo = stub.depositar_valor(logada, valor_a_ser_depositado);

					if(deu_certo == false)
					{
						System.out.println("Conta n�o encontrada");
					}else
					{
						// atualizar a conta que est� em se��o atual
						logada = stub.fazer_login(logada.getDados_login().getUsuario(),
								logada.getDados_login().getSenha());
						System.out.println("Novo saldo = "+ logada.getSaldo());
					}


				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;

			case 4:
				System.out.println("Saldo atual: " + logada.getSaldo());
				System.out.print("Digite o valor a ser transferido: ");
				double valor_a_ser_transferido = teclado.nextDouble();
				//capturando o \n
				teclado.nextLine();
				if(valor_a_ser_transferido > logada.getSaldo())
				{
					System.out.println("Imposs�vel transferir valor maior do que o saldo atual");
				}else
				{
					System.out.print("Digite o c�digo da conta que receber� a transfer�ncia: ");
					long conta_alvo = teclado.nextLong();
					//capturando o \n
					teclado.nextLine();
					try {
						boolean deu_certo = stub.transferir_saldo(logada, valor_a_ser_transferido, conta_alvo);
						
						if(deu_certo)
						{
							logada = stub.fazer_login(logada.getDados_login().getUsuario(),
									logada.getDados_login().getSenha());
							
							
							
							System.out.println("Opera��o de transfer�ncia conclu�da com sucesso");
							System.out.println("Novo saldo: " + logada.getSaldo());
						}


					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				break;
		
			case 5:
				// conta poupan�a
				if(logada.getTipoInvestimento() == 0)	
				{
					System.out.println("\nSimula��o de investimento para conta poupan�a");
					System.out.println("Valor atual: " + logada.getSaldo());
					System.out.println("Valor daqui a tr�s meses: " + (logada.getSaldo()*1.015));
					System.out.println("Valor daqui a seis meses: " + (logada.getSaldo()*1.030));
					System.out.println("Valor daqui a doze meses: " + (logada.getSaldo()*1.060)+"\n");
				}else
				{
					// conta de renda fixa
					System.out.println("\nSimula��o de investimento para conta de renda fixa");
					System.out.println("Valor atual: " + logada.getSaldo());
					System.out.println("Valor daqui a tr�s meses: " + (logada.getSaldo()*1.045));
					System.out.println("Valor daqui a seis meses: " + (logada.getSaldo()*1.090));
					System.out.println("Valor daqui a doze meses: " + (logada.getSaldo()*1.180)+"\n");
				}


				break;

			case 6:
				if(logada.getTipoInvestimento() == 1)	
				{
					logada.setTipoInvestimento(0);
					System.out.println("Conta opera agora sobre a poupan�a");
				}else
				{
					if(logada.getTipoInvestimento() == 0)	
					{
						logada.setTipoInvestimento(1);
						System.out.println("Conta opera agora sobre a renda fixa");
					}
					
				}
				stub.editar_conta(logada);
				break;
				
			case 7:
				logada.setSolicitada_remocao(true);
				stub.editar_conta(logada);
				break;
				
			case 8:
				System.out.println(stub.listar_contas());
				break;	
				
				//editar dados da conta
			case 9:
				try {
				System.out.println("Dados modific�veis exibidos abaixo");
				System.out.println("Endere�o atual: "+ logada.getEnderecoCliente());
				System.out.println("Data de nascimento: "+ logada.getDataNascimentoCliente());
				System.out.println("Numero telef�nico: " + logada.getNumeroTelefonicoCliente());
				System.out.println("\n\nDigite o novo endere�o atual ao lado: ");
				logada.setEnderecoCliente(teclado.nextLine());
				System.out.println("\n\nDigite a nova data de nascimento ao lado: ");
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	            logada.setDataNascimentoCliente(sdf.parse(teclado.next()));
	            System.out.println("\n\nDigite o novo n�mero telef�nico ao lado: ");
				logada.setNumeroTelefonicoCliente(teclado.nextLine());
	            stub.editar_conta(logada);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				
				break;
			case 10:
				System.out.println("N�vel da conta alterado para funcion�rio");
				logada.getDados_login().setTipo(1);
				stub.editar_conta(logada);
				return 2;
				
			case 11:
				System.out.println("Saindo...");
				return 2;
			}
		}
		

		return 2;
	}


	public int menu_funcionario()
	{
		return 2;
	}
	
	

}