import java.util.InputMismatchException;
import java.util.Scanner;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int option, cod, old_cod;
        String name;
        Enclosure enclosure;
        Animal animal;
        ResultSet results;
        ResultSet results2;
        Statement statement;
        Statement statement2;
        try {
            statement = Database.getConnection().createStatement();
            statement2 = Database.getConnection().createStatement();
        } catch (SQLException exception) {
            scanner.close();
            throw new RuntimeException("failed to initialize statement", exception);
        }
        
        try {
            do {
                displayMenu();
                option = readInt(scanner, "# Opção: ");

                switch (option) {
                    case 1:
                        try {
                            System.out.println("\n======= Adicionar recinto =======");
                            enclosure = Enclosure.createEnclosure(scanner);
                            System.out.println("=================================\n");

                            if (enclosure.addEnclosure(statement)) {
                                System.out.println("* Recinto adicionado com sucesso! *");
                            } else {
                                System.out.println("* Recinto já existente *");
                            }
                        } catch (SQLException exception) {
                            exception.printStackTrace();
                        }

                        break;
                    case 2:
                        try {
                            System.out.println("\n======= Remover recinto =======");
                            cod = readInt(scanner, "# Digite o código do recinto: ");
                            System.out.println("==============================\n");

                            if (Enclosure.removeEnclosure(cod, statement)) {
                                System.out.println("* Recinto removido com sucesso! *");
                            } else {
                                System.out.println("* Recinto não encontrado! *");
                            }

                        } catch (SQLException exception) {
                            exception.printStackTrace();
                        }
                        break;
                    case 3:
                        try {
                            System.out.println("\n======= Adicionar animal =======");
                            animal = Animal.createAnimal(scanner, statement);
                            System.out.println("================================\n");

                            if (animal.addAnimal(statement)) {
                                System.out.println("* Animal adicionado com sucesso! *");
                            } else {
                                System.out.println("* Animal já existente! *");
                            }

                        } catch(SQLException exception) {
                            exception.printStackTrace();
                        }

                        break;
                    case 4:
                        try {
                            System.out.println("\n======= Remover animal =======");
                            System.out.print("# Digite o nome do animal: ");
                            name = scanner.nextLine();
                            System.out.println("==============================\n");

                            if (Animal.removeAnimal(name, statement)) {
                                System.out.println("* Animal removido com sucesso! *");
                            } else {
                                System.out.println("* Animal não encontrado! *");
                            }

                        } catch (SQLException exception) {
                            exception.printStackTrace();
                        }

                        break;
                    case 5:
                        try {
                            cod = readInt(scanner ,"# Digite o codigo do recinto que terá os dados editados: ");

                            results = Enclosure.searchEnclosure(cod, statement);

                            if (results.next()) {
                                System.out.println("\n======= Editar dados de recinto =======");

                                System.out.println("\n----------- Dados atuais -----------");
                                System.out.println(
                                    "Código: " + results.getInt("cod") + '\n' +
                                    "Capacidade: " + results.getInt("capacity") + "\n" +
                                    "Horário de visitas: " + results.getString("visiting_hours") 
                                );
                                System.out.println("--------------------------------------\n");

                                System.out.println("------------ Novos dados -------------");
                                enclosure = Enclosure.createEnclosure(scanner);
                                System.out.println("--------------------------------------\n");
                                
                                System.out.println("=======================================\n");
                                if (enclosure.updateEnclosure(results.getInt("id"), statement)) {
                                    System.out.println("* Os dados do recinto foram atualizados com sucesso! *");
                                }
                            } else {
                                System.out.println("\n* recinto não encontrado! *");
                            }

                            results.close();
                        } catch (SQLException exception) {
                            exception.printStackTrace();
                        }

                        break;
                    case 6:
                        try {
                            System.out.print("# Digite o nome do animal que terá os dados editados: ");
                            name = scanner.nextLine();

                            results = Animal.searchAnimal(name, statement);

                            if (results.next()) {
                                results2 = Enclosure.searchEnclosureByid(results.getInt("id_enclosure"), statement2);
                                old_cod = -1;

                                System.out.println("\n======= Editar dados de animal =======");

                                System.out.println("\n----------- Dados atuais -----------");
                                System.out.println(
                                    "Nome: " + results.getString("name") + '\n' +
                                    "Espécie: " + results.getString("species") + "\n" +
                                    "Idade: " + results.getInt("age") + '\n' +
                                    "Recinto: " + (results2.next() ? old_cod = results2.getInt("cod") : "Animal sem recinto")
                                );
                                System.out.println("--------------------------------------\n");

                                System.out.println("------------ Novos dados -------------");
                                animal = Animal.createAnimal(scanner, statement2);
                                System.out.println("--------------------------------------\n");
                                
                                System.out.println("======================================\n");
                                if (animal.updateAnimal(results.getInt("id"), old_cod, statement)) {
                                    System.out.println("* Os dados do animal foram atualizados com sucesso! *");
                                }

                                results2.close();
                            } else {
                                System.out.println("\n* Animal não encontrado! *");
                            }

                            results.close();

                        } catch (SQLException exception) {
                            exception.printStackTrace();
                        }

                        break;
                    case 7:
                        System.out.println("\n======= Buscar animal =======");
                        System.out.print("# Digite o nome do animal: ");
                        name = scanner.nextLine();
                        System.out.println("=============================\n");

                        try {
                            results = Animal.searchAnimal(name, statement);

                            if (results.next()){
                                results2 = Enclosure.searchEnclosureByid(results.getInt("id_enclosure"), statement2);

                                System.out.println("* Animal encontrado! *\n");
                                System.out.println("----------------------");
                                System.out.println(
                                    "Nome: " + results.getString("name") + "\n" +
                                    "Espécie: " + results.getString("species") + "\n" +
                                    "Idade: " + results.getInt("age") + '\n' +
                                    "Recinto: " + (results2.next() ? results2.getInt("cod") : "Animal sem recinto")
                                );
                                System.out.println("----------------------");

                                results2.close();
                            } else {
                                System.out.println("* Animal não encontrado! *");
                            }   

                            results.close();

                        } catch (SQLException exception) {
                            exception.printStackTrace();
                        }

                        break;
                    case 8:
                        try {
                            System.out.println("\n======= Lista de recintos =======\n");
                            Enclosure.listEnclosure(statement);
                            System.out.println("=================================");
                        } catch (SQLException exception) {
                            exception.printStackTrace();
                        }

                        break;
                    case 9:
                        System.out.println("Encerrando...");
                        break;
                    default:
                        System.out.println("\n* Opção inválida! *");
                        break;
                }

            } while (option != 9);
        } finally {
            try {
                if (statement != null) statement.close();
                if (statement2 != null) statement2.close();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
            scanner.close();
        }
    }

    public static void displayMenu() {
        System.out.println(
            "\n======= MENU =======\n" +
            "1. Adicionar recinto\n" +
            "2. Remover recinto\n" +
            "3. Adicionar animal\n" +
            "4. Remover animal\n" +
            "5. Editar dados de recinto\n" +
            "6. Editar dados de animal\n" +
            "7. Buscar animal\n" +
            "8. Listar recintos\n" +
            "9. Sair\n" +
            "===================="
        );
    }

    public static int readInt(Scanner scanner, String msg) {
        int number;
        while (true) {
            try {
                System.out.print(msg);
                number = scanner.nextInt();
                scanner.nextLine();

                return number;
            } catch (InputMismatchException exception) {
                System.out.println("\n* Entrada inválida! Digite um número inteiro. *\n");
                scanner.nextLine();
            }
        }
    }
}