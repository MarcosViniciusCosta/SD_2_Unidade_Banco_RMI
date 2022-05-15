package cliente;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;



import java.util.Date;

import java.text.SimpleDateFormat;

import server.Conta;
import server.Dados_login;
import server.Operacoes_banco;

public class Cliente {

    private Cliente() {}

    public static void main(String[] args) {
    	
    	Scanner teclado = new Scanner(System.in);
    	
    	System.out.println("Informe o nome/endereço do RMIRegistry:");
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
            System.out.print("Digite o seu endereço: ");
            String endereco = teclado.nextLine();
            System.out.print("Digite o seu número telefônico: ");
            String numero_telefonico = teclado.nextLine();
            System.out.print("Digite a sua data de nascimento: ");
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date data_nascimento = sdf.parse(teclado.next()); 
            Conta c = new Conta(cpf,nome, endereco, data_nascimento,numero_telefonico, dados_login);
            //System.out.println("Conta criada com sucesso");
            stub.criar_conta(c);
            */
            System.out.println(stub.listar_contas());
            
            
            
            
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
        teclado.close();
    }
}
