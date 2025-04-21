import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Enclosure {
    private int cod;
    private int capacity;
    private String visitingHours;
    private int numberAnimals;

    public Enclosure(int cod, int capacity, String visitingHours) {
        this.cod = cod;
        this.capacity = capacity;
        this.visitingHours = visitingHours;
        this.numberAnimals = 0;
    }

    public static ResultSet searchEnclosure(int cod, Statement statement) throws SQLException {
        String sql = "SELECT * FROM enclosure WHERE cod = '" + cod + "'";
        return statement.executeQuery(sql);
    }

    public static Enclosure createEnclosure(Scanner scanner) {
        int cod, capacity;
        String visitingHours;

        cod = Main.readInt(scanner, "# Digite o código do recinto: ");

        while (true) {
            capacity = Main.readInt(scanner, "# Digite a capacidade do recinto: ");

            if (capacity > 0) {
                break;
            } else {
                System.out.println("\n* O mínimo da capacidade é 1 *\n");
            }
        }

        System.out.print("# Digite o horário de visitas (Ex: 10:00 às 12:00): ");
        visitingHours = scanner.nextLine();

        return new Enclosure(cod, capacity, visitingHours);
    }

    public boolean addEnclosure(Statement statement) throws SQLException {
        ResultSet results = searchEnclosure(cod, statement);
        
        if (!results.next()) {
            String sql = "INSERT INTO enclosure (cod, capacity, visiting_hours, number_animals) VALUES ('" + this.cod + "','" + this.capacity + "','" + this.visitingHours + "','" + this.numberAnimals  +"')";
            statement.executeUpdate(sql);
            results.close();
            return true;
        } else {
            results.close();
            return false;
        }
    }

    public static boolean removeEnclosure(int cod, Statement statement) throws SQLException {
        ResultSet results = searchEnclosure(cod, statement);

        if(results.next()) {
            String sql1 = "DELETE FROM enclosure WHERE id = '" + results.getInt("id") + "'";
            String sql2 = "UPDATE animals SET id_enclosure = null WHERE id_enclosure = " + results.getInt("id"); 
            statement.executeUpdate(sql1);
            statement.executeUpdate(sql2);
            results.close();
            return true;
        } else {
            results.close();
            return false;
        }
    }

    public boolean updateEnclosure(int id, Statement statement) throws SQLException {
        String sql = "UPDATE enclosure SET cod ='" + this.cod + "', capacity ='" + this.capacity + "', visiting_hours ='" + this.visitingHours + "' WHERE id ='" + id + "'";
        return statement.executeUpdate(sql) > 0;
    }

    public static void listEnclosure(Statement statement) throws SQLException {
        int i = 0;
        String sql1 = "SELECT * FROM enclosure";
        String sql2;
        ResultSet results1 = statement.executeQuery(sql1);
        ResultSet results2;
        Statement statement2 = Database.getConnection().createStatement();

        while (results1.next()) {
            sql2 = "SELECT * FROM animals WHERE id_enclosure ='" + results1.getInt("id") + "'";
            results2 = statement2.executeQuery(sql2);
            System.out.printf("------- Recinto %d -------\n", ++i);
            System.out.println(
                "Código: " + results1.getInt("cod") + "\n" +
                "Capacidade: " + results1.getInt("capacity") + "\n" +
                "Horário: " + results1.getString("visiting_hours") + "\n" +
                "Quantidade de animais: " + results1.getInt("number_animals")
            );

            if (results2.next()) {
                System.out.println("\nAnimais presentes no recinto:\n");

                do {
                    System.out.println(
                        "Nome: " + results2.getString("name") + "\n" +
                        "Espécie: " + results2.getString("species") + "\n" +
                        "Idade: " + results2.getInt("age") +
                        "\n"
                    );
                } while (results2.next());
            }
            System.out.println("-------------------------\n");
            
            results2.close();
        }
        results1.close();
        statement2.close();
    }

    public static ResultSet searchEnclosureByid (int id, Statement statement) throws SQLException {
        String sql = "SELECT * FROM enclosure WHERE id = " + id;
        return statement.executeQuery(sql);
    }

    public static void increaseNumberAnimalsById(int id, Statement statement) throws SQLException {
        String sql = "UPDATE enclosure SET number_animals = number_animals + 1 WHERE id =" + id;
        statement.executeUpdate(sql);
    }

    public static void increaseNumberAnimals(int cod, Statement statement) throws SQLException {
        String sql = "UPDATE enclosure SET number_animals = number_animals + 1 WHERE cod =" + cod;
        statement.executeUpdate(sql);
    }

    public static void decrementNumberAnimalsById(int id, Statement statement) throws SQLException {
        String sql = "UPDATE enclosure SET number_animals = number_animals - 1 WHERE id =" + id;
        statement.executeUpdate(sql);
    }

    public static void decrementNumberAnimals(int cod, Statement statement) throws SQLException {
        String sql = "UPDATE enclosure SET number_animals = number_animals - 1 WHERE cod =" + cod;
        statement.executeUpdate(sql);
    }
}
