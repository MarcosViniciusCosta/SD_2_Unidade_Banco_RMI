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

			/*
			//criando conta para testar stubs
			
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
			c.getDados_login().elevar_nivel_para_funcionario();
			stub.editar_conta(c);
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
							menu_funcionario(teclado,logada,stub);
							sair = true;
							break;
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
			System.out.println("4 - Realizar transferência");
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
				System.out.printf("Saldo da conta atual = %.2f\n",logada.getSaldo());
				break;
			case 2:
				System.out.print("Digite o valor a ser sacado:");
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
						System.out.printf("Novo saldo: %.2f\n", logada.getSaldo());
					}


				} catch (RemoteException e) {
					e.printStackTrace();
				}
				break;

			case 3:

				System.out.print("Digite o valor a ser depositado:");
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
						System.out.printf("Novo saldo = %.2f\n",logada.getSaldo());
					}


				} catch (RemoteException e) {
					e.printStackTrace();
				}
				break;

			case 4:
				System.out.printf("Saldo atual: %.2f" + logada.getSaldo());
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
							System.out.printf("Novo saldo: %.2f\n" + logada.getSaldo());
						}


					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
				break;

			case 5:
				// conta poupan�a
				if(logada.getTipoInvestimento() == 0)	
				{
					System.out.println("\nSimula��o de investimento para conta poupan�a");
					System.out.printf("Valor atual: %.2f\n",logada.getSaldo());
					System.out.printf("Valor daqui a tr�s meses: %.2f\n" ,(logada.getSaldo()*1.015));
					System.out.printf("Valor daqui a seis meses: %.2f\n" , (logada.getSaldo()*1.030));
					System.out.printf("Valor daqui a doze meses: %.2f\n\n" , (logada.getSaldo()*1.060));
				}else
				{
					// conta de renda fixa
					System.out.println("\nSimula��o de investimento para conta de renda fixa");
					System.out.printf("Valor atual: %.2f\n", logada.getSaldo());
					System.out.printf("Valor daqui a tr�s meses: %.2f\n", (logada.getSaldo()*1.045));
					System.out.printf("Valor daqui a seis meses: %.2f\n",(logada.getSaldo()*1.090));
					System.out.printf("Valor daqui a doze meses: %.2f\n\n" , (logada.getSaldo()*1.180));
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
				System.out.println("Solicitação de remoção de conta realizada com sucesso!");
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
					System.out.print("\nDigite o novo endere�o atual ao lado ou repita o atual: ");
					logada.setEnderecoCliente(teclado.nextLine());
					System.out.print("\nDigite a nova data de nascimento ao lado ou repita o atual: ");
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					logada.setDataNascimentoCliente(sdf.parse(teclado.next()));
					teclado.nextLine();
					System.out.print("\nDigite o novo n�mero telef�nico ao lado ou repita o atual: ");
					logada.setNumeroTelefonicoCliente(teclado.nextLine());
					stub.editar_conta(logada);
				} catch (ParseException e) {
					e.printStackTrace();
				}

				break;
			case 10:
				System.out.println("Nível da conta alterado para funcionário");
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


	public static int menu_funcionario(Scanner teclado, Conta logada, Operacoes_banco stub)
	{
		int opcao = 0;
		while(opcao != 12)
		{
			System.out.println("\n1 - Criar conta");
			System.out.println("2 - Alterar conta");
			System.out.println("3 - Buscar conta");
			System.out.println("4 - listar contas");
			System.out.println("5 - Remover contas");
			System.out.println("6 - Consultar saldo");
			System.out.println("7 - Realizar saque");
			System.out.println("8 - Realizar depósito");
			System.out.println("9 - Realizar transferência");
			System.out.println("10 - Consultar resultados dos investimentos");
			System.out.println("11 - Mudar o regime dos investimentos");
			System.out.println("12 - Sair");
			System.out.print("Digite a op��o ao lado: ");
			opcao = teclado.nextInt();
			//capturando o \n
			teclado.nextLine();
			try {
				switch(opcao)
				{
				case 1:

					Dados_login dados_login = new Dados_login();
					System.out.print("Digite o login desejado pelo cliente: ");
					dados_login.setUsuario(teclado.nextLine());
					System.out.print("Digite a sua senha desejada pelo cliente: ");
					dados_login.setSenha(teclado.nextLine());
					System.out.print("Digite o nome do cliente: ");
					String nome = teclado.nextLine();
					System.out.print("Digite o cpf do cliente: ");
					String cpf = teclado.nextLine();
					System.out.print("Digite o endereço do cliente: ");
					String endereco = teclado.nextLine();
					System.out.print("Digite o número telefônico do cliente: ");
					String numero_telefonico = teclado.nextLine();
					System.out.print("Digite a data de nascimento do cliente: ");
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					Date data_nascimento;
					data_nascimento = sdf.parse(teclado.next());
					Conta c = new Conta(cpf,nome, endereco, data_nascimento,numero_telefonico, dados_login);
					stub.criar_conta(c);
					System.out.println("Conta criada com sucesso");

					break;
				case 2:

					break;
				case 3:

					break;
				case 4:
					System.out.println(stub.listar_contas());
					break;

				case 5:
					System.out.println("Listando contas que pediram remoção:\n");
					System.out.println(stub.listar_contas_a_serem_removidas());
					
					System.out.println("\n0 - Não remover nenhuma conta agora");
					System.out.println("1 - Remover alguma conta agora");
					System.out.print("Digite a opção ao lado: ");
					int op = teclado.nextInt();
					// capturando \n
					teclado.nextLine();
					
					switch(op)
					{
						case 0:
							System.out.println("Não será removida conta alguma.");
							break;
						case 1:
							System.out.print("Digite o número da conta a ser excluida: ");
							long numero_conta_a_ser_excluida = teclado.nextLong();
							// capturando \n
							teclado.nextLine();
							
							boolean deu_certo = stub.remover_conta(numero_conta_a_ser_excluida);
							
							if(deu_certo == true)
							{
								System.out.println("Conta de número "+numero_conta_a_ser_excluida+" foi excluida com sucesso!");
							}else
							{
								System.out.println("Conta de número "+numero_conta_a_ser_excluida+" não foi encontrada!");
							}
							
							
							break;
					}
					
					
					
					break;
				case 6:
					System.out.printf("Saldo da conta atual = %.2f\n",logada.getSaldo());
					break;
					
				case 7:

					System.out.print("Digite o valor a ser sacado:");
					double valor_a_ser_sacado = teclado.nextDouble();
					//capturando o \n
					teclado.nextLine();

					boolean deu_certo = stub.sacar_valor(logada, valor_a_ser_sacado);

					if(deu_certo == false)
					{
						System.out.println("Saldo insuficiente");
					}else
					{
						logada = stub.fazer_login(logada.getDados_login().getUsuario(),
								logada.getDados_login().getSenha());
						System.out.printf("Novo saldo: %.2f\n", logada.getSaldo());
					}

					break;

				case 8:

					System.out.print("Digite o valor a ser depositado:");
					double valor_a_ser_depositado = teclado.nextDouble();
					//capturando o \n
					teclado.nextLine();

					boolean deu_certo1 = stub.depositar_valor(logada, valor_a_ser_depositado);

					if(deu_certo1 == false)
					{
						System.out.println("Conta não encontrada");
					}else
					{
						// atualizar a conta que est� em se��o atual
						logada = stub.fazer_login(logada.getDados_login().getUsuario(),
								logada.getDados_login().getSenha());
						System.out.printf("Novo saldo = %.2f\n",logada.getSaldo());
					}

					break;
					
				case 9:

					System.out.printf("Saldo atual: %.2f" + logada.getSaldo());
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

						boolean deu_certo2 = stub.transferir_saldo(logada, valor_a_ser_transferido, conta_alvo);

						if(deu_certo2)
						{
							logada = stub.fazer_login(logada.getDados_login().getUsuario(),
									logada.getDados_login().getSenha());

							System.out.println("Operação de transferência concluída com sucesso");
							System.out.printf("Novo saldo: %.2f\n" + logada.getSaldo());
						}
					}

					break;
					
				case 10:

					// conta poupan�a
					if(logada.getTipoInvestimento() == 0)	
					{
						System.out.println("\nSimula��o de investimento para conta poupan�a");
						System.out.printf("Valor atual: %.2f\n",logada.getSaldo());
						System.out.printf("Valor daqui a tr�s meses: %.2f\n" ,(logada.getSaldo()*1.015));
						System.out.printf("Valor daqui a seis meses: %.2f\n" , (logada.getSaldo()*1.030));
						System.out.printf("Valor daqui a doze meses: %.2f\n\n" , (logada.getSaldo()*1.060));
					}else
					{
						// conta de renda fixa
						System.out.println("\nSimula��o de investimento para conta de renda fixa");
						System.out.printf("Valor atual: %.2f\n", logada.getSaldo());
						System.out.printf("Valor daqui a tr�s meses: %.2f\n", (logada.getSaldo()*1.045));
						System.out.printf("Valor daqui a seis meses: %.2f\n",(logada.getSaldo()*1.090));
						System.out.printf("Valor daqui a doze meses: %.2f\n\n" , (logada.getSaldo()*1.180));
					}
					break;
				case 11:
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
					
				case 12:
					System.out.println("Saindo...");
					return 2;
					
				}

			} catch (ParseException e) {
				e.printStackTrace();
			} catch(RemoteException e)
			{
				e.printStackTrace();
			}	
		}
		return 2;
	}
}