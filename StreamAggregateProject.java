import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class StreamAggregateProject {

    // Klasa reprezentująca syntetyczne dane
    static class Transaction {
        int id;
        double amount;
        String currency;
        boolean isFraudulent;

        public Transaction(int id, double amount, String currency, boolean isFraudulent) {
            this.id = id;
            this.amount = amount;
            this.currency = currency;
            this.isFraudulent = isFraudulent;
        }
    }

    public static void main(String[] args) {
        // Wygenerowanie syntetyczne bardzo dużego zbioru danych
        int DATA_SIZE = 5_000_000; // 5 milionów obiektów
        System.out.println("Generowanie " + DATA_SIZE + " transakcji...");

        List<Transaction> transactions = new ArrayList<>(DATA_SIZE);
        Random random = new Random();
        String[] currencies = {"PLN", "EUR", "USD", "GBP"};

        for (int i = 0; i < DATA_SIZE; i++) {
            // 5% szans na fraud
            boolean isFraud = random.nextInt(100) < 5;
            transactions.add(new Transaction(i, random.nextDouble() * 10000,
                    currencies[random.nextInt(currencies.length)], isFraud));
        }
        System.out.println("Dane wygenerowane. Rozpoczynam przetwarzanie...\n");

        // ==========================================
        // PRZEŁĄCZNIK ARCHITEKTURY
        // 0 = Tryb Sekwencyjny (stream)
        // 1 = Tryb Równoległy (parallelStream)
        // ==========================================
        int MODE = 1;

        // Zbiór do wyłapywania nazw wątków
        Set<String> threadNames = ConcurrentHashMap.newKeySet();

        long startTime = System.currentTimeMillis();
        long processedCount = 0;

        if (MODE == 0) {
            System.out.println("=== TRYB SEKWENCYJNY (stream) ===");
            // Przekształcenie za pomocą stream()
            processedCount = transactions.stream()
                    // Operacje pośrednie (Intermediate operations)
                    // 1. Odrzucenie oszustw
                    .filter(t -> !t.isFraudulent)
                    // 2. Filtracja walut
                    .filter(t -> t.currency.equals("USD") || t.currency.equals("EUR"))
                    // 3. Ciężka operacja mapująca
                    .map(t -> {
                        threadNames.add(Thread.currentThread().getName());
                        return simulateHeavyTaxCalculation(t.amount);
                    }).count(); // Operacja terminalna
        } else {
            System.out.println("=== TRYB RÓWNOLEGŁY (parallelStream) ===");
            // WYMÓG 4: Przekształcenie za pomocą parallelStream()
            processedCount = transactions.parallelStream()
                    // Ponowne wykonanie tych samych operacji pośrednich
                    .filter(t -> !t.isFraudulent)
                    .filter(t -> t.currency.equals("USD") || t.currency.equals("EUR")).map(t -> {
                        threadNames.add(Thread.currentThread().getName());
                        return simulateHeavyTaxCalculation(t.amount);
                    }).count(); // Operacja terminalna
        }

        long endTime = System.currentTimeMillis();

        // Podsumowanie wyników
        System.out.println("Przetworzono prawidłowych transakcji: " + processedCount);
        System.out.println("Czas wykonania: " + (endTime - startTime) + " ms");
        System.out.println("\nUżyte wątki (" + threadNames.size() + "):");
        threadNames.forEach(name -> System.out.println(" - " + name));
    }

    // Symulacja skomplikowanego algorytmu analitycznego (np. obliczenia podatkowe)
    private static double simulateHeavyTaxCalculation(double amount) {
        double result = amount;
        for (int i = 0; i < 500; i++) {
            result = Math.hypot(result, Math.cos(result));
        }
        return result;
    }
}
