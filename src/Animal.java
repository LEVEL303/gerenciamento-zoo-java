import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Animal {
    private String name;
    private String species;
    private int age;
    private int codEnclosure;

    public Animal(String name, String species, int age, int codEnclosure) {
        this.name = name;
        this.species = species;
        this.age = age;
        this.codEnclosure = codEnclosure;
    }

    public static ResultSet searchAnimal(String name, Statement statement) throws SQLException {
        String sql = "SELECT * FROM animals WHERE name = '" + name + "'";
        return statement.executeQuery(sql);
    }

    public static Animal createAnimal(Scanner scanner, Statement statement) throws SQLException {
        String name, species;
        int age, codEnclosure;
        ResultSet results;
        
        System.out.print("# Digite o nome do animal: ");
        name = scanner.nextLine();

        System.out.print("# Digite a espécie do animal: ");
        species = scanner.nextLine();

        while (true) {
            age = Main.readInt(scanner, "# Digite a idade do animal: ");

            if (age >= 0) {
                break;
            } else {
                System.out.println("\n* A idade precisa ser positiva! *\n");
            }
        }

        while (true) {
            codEnclosure = Main.readInt(scanner, "# Digite o código do recinto do animal (Ou -1 para animal sem recino): ");

            results = Enclosure.searchEnclosure(codEnclosure, statement);
            if (results.next()) {
                if (results.getInt("number_animals") < results.getInt("capacity")) {
                    break;
                } else  {
                    System.out.println("\n* O recinto informado já atingiu a capacidade máxima! *\n");
                }
            } else if (codEnclosure == -1) {
                break;
            } else {
                System.out.println("\n* Recinto não encontrado! *\n");
            }
        }
        results.close();
        
        return new Animal(name, species, age, codEnclosure);
    }

    public boolean addAnimal(Statement statement) throws SQLException {
        ResultSet results = searchAnimal(this.name, statement);
        String sql1;

        if (!results.next()) {
            results.close();

            if (this.codEnclosure == -1) {
                sql1 = "INSERT INTO animals (name, species, age) VALUES ('" + this.name + "','" + this.species + "','" + this.age + "')";
            } else {
                results = Enclosure.searchEnclosure(this.codEnclosure, statement);
                results.next();

                sql1 = "INSERT INTO animals (name, species, age, id_enclosure) VALUES ('" + this.name + "','" + this.species + "','" + this.age + "','" + results.getInt("id") + "')";

                Enclosure.increaseNumberAnimalsById(results.getInt("id"), statement);

                results.close();
            }

            statement.executeUpdate(sql1);
            return true;
        } else {
            results.close();
            return false;
        }
    }

    public static boolean removeAnimal(String name, Statement statement) throws SQLException {
        ResultSet results = searchAnimal(name, statement);

        if (results.next()) {
            String sql1 = "DELETE FROM animals WHERE id = '" + results.getInt("id") + "'";

            if (results.getInt("id_enclosure") != 0) {
                Enclosure.decrementNumberAnimalsById(results.getInt("id_enclosure"), statement);
            }

            statement.executeUpdate(sql1);
            results.close();
            return true;
        } else {
            results.close();
            return false;
        }
    }

    public boolean updateAnimal(int id, int old_cod, Statement statement) throws SQLException {
        String sql;

        if (this.codEnclosure == -1) {
            sql = "UPDATE animals SET name ='" + name + "', species ='" + species + "', age ='" + age + "', id_enclosure = null WHERE id ='"+ id + "'"; 

            if (this.codEnclosure != old_cod) {
                Enclosure.decrementNumberAnimals(old_cod, statement);
            }
        } else {
            ResultSet results = Enclosure.searchEnclosure(this.codEnclosure, statement);
            results.next();
            sql = "UPDATE animals SET name ='" + name + "', species ='" + species + "', age ='" + age + "', id_enclosure ='" + results.getInt("id") + "' WHERE id ='"+ id + "'"; 
            results.close();

            if (this.codEnclosure != old_cod) {
                Enclosure.increaseNumberAnimals(this.codEnclosure, statement);
            }
            if (old_cod != -1) {
                Enclosure.decrementNumberAnimals(old_cod, statement);
            }
        }

        return statement.executeUpdate(sql) > 0;
    }
}
