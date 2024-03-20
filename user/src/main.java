import java.util.Scanner;
import Producteur;
public class Main {

    public static void main(String[] args) {
        // Exemple d'utilisation
        Producteur producer = new KafkaCommandProducer("kafka:9092", "user_commands");
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Entrez une commande (tapez 'exit' pour quitter) : ");
            String command = scanner.nextLine();

            if ("exit".equalsIgnoreCase(command)) {
                break;
            }

            producer.sendCommand(command);
            System.out.println("Commande envoyée: " + command);
        }

        scanner.close();
        producer.close();
        System.out.println("Producteur fermé et interface de ligne de commande fermée.");
    }
}
