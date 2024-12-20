import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

public class FlightBookingService {

    private static final String DATA_FILE = "flights.txt";

    public static void main(String[] args) {
        try {
            List<Flight> flights = loadFlights();
            System.out.println("Добро пожаловать в сервис подбора авиабилетов!");

            Scanner scanner = new Scanner(System.in);
            System.out.print("Введите пункт назначения: ");
            String destination = scanner.nextLine();

            System.out.print("Введите минимальную цену: ");
            String minPriceInput = scanner.nextLine();

            System.out.print("Введите максимальную цену: ");
            String maxPriceInput = scanner.nextLine();

            int minPrice = parsePrice(minPriceInput);
            int maxPrice = parsePrice(maxPriceInput);

            List<Flight> matchedFlights = findFlights(flights, destination, minPrice, maxPrice);

            if (matchedFlights.isEmpty()) {
                System.out.println("Нет доступных рейсов по заданным критериям.");
            } else {
                System.out.println("Найденные рейсы:");
                for (Flight flight : matchedFlights) {
                    System.out.println(flight);
                }
            }

            System.out.print("Сохранить результаты в файл? (да/нет): ");
            String saveToFile = scanner.nextLine();
            if (saveToFile.equalsIgnoreCase("да")) {
                saveResults(matchedFlights);
                System.out.println("Результаты сохранены в файл results.txt.");
            }
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private static List<Flight> loadFlights() throws IOException {
        List<Flight> flights = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(DATA_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    flights.add(parseFlight(line));
                } catch (IllegalArgumentException e) {
                    System.out.println("Пропуск некорректной строки: " + line);
                }
            }
        }

        return flights;
    }

    private static Flight parseFlight(String line) {
        String regex = "^(\\w+),\\s*(\\w+),\\s*(\\d+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(line);

        if (matcher.matches()) {
            String from = matcher.group(1);
            String to = matcher.group(2);
            int price = Integer.parseInt(matcher.group(3));
            return new Flight(from, to, price);
        } else {
            throw new IllegalArgumentException("Некорректный формат строки: " + line);
        }
    }

    private static int parsePrice(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Цена должна быть числом: " + input);
        }
    }

    private static List<Flight> findFlights(List<Flight> flights, String destination, int minPrice, int maxPrice) {
        List<Flight> matchedFlights = new ArrayList<>();

        for (Flight flight : flights) {
            if (flight.getTo().equalsIgnoreCase(destination) &&
                    flight.getPrice() >= minPrice &&
                    flight.getPrice() <= maxPrice) {
                matchedFlights.add(flight);
            }
        }

        return matchedFlights;
    }

    private static void saveResults(List<Flight> flights) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get("results.txt"))) {
            for (Flight flight : flights) {
                writer.write(flight.toString());
                writer.newLine();
            }
        }
    }

    static class Flight {
        private final String from;
        private final String to;
        private final int price;

        public Flight(String from, String to, int price) {
            this.from = from;
            this.to = to;
            this.price = price;
        }

        public String getFrom() {
            return from;
        }

        public String getTo() {
            return to;
        }

        public int getPrice() {
            return price;
        }

        @Override
        public String toString() {
            return String.format("Рейс из %s в %s за %d рублей", from, to, price);
        }
    }
}
