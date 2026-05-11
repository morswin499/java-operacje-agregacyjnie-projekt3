# Projekt 3: Operacje agregacyjne i strumienie danych
**Przedmiot:** Programowanie współbieżne w języku JAVA

## O projekcie
Celem projektu jest analiza wydajnościowa zrównoleglonego przetwarzania danych z wykorzystaniem `parallelStream()`. Aplikacja symuluje system analityczny dla transakcji finansowych, który musi przefiltrować miliony rekordów i wykonać złożone operacje matematyczne (symulacja obciążenia procesora).

Projekt demonstruje:
1. **Przetwarzanie potokowe:** Wykorzystanie trzech operacji pośrednich: podwójnej filtracji (fraud/waluta) oraz złożonego mapowania.
2. **Monitoring wątków:** Dynamiczne wyłapywanie nazw wątków biorących udział w obliczeniach przy użyciu `ConcurrentHashMap`.
3. **Analizę obciążenia:** Symulację ciężkich obliczeń podatkowych przy użyciu funkcji trygonometrycznych, co pozwala zauważyć realny zysk z wielowątkowości.

## Instrukcja uruchomienia i testowania
Główną klasą aplikacji jest `StreamAggregateProject.java`. 

Aby przetestować aplikację:
1. Wewnątrz metody `main` znajdź zmienną `MODE`:
   - `int MODE = 0;` – Tryb sekwencyjny: całość wykonuje jeden wątek (main).
   - `int MODE = 1;` – Tryb równoległy: zadania są rozdzielane na pulę ForkJoinPool.
2. Parametr `DATA_SIZE` jest ustawiony na 5 000 000 rekordów, co stanowi odpowiednio dużą próbkę testową.
3. Po uruchomieniu program wypisze:
   - Czas wykonania operacji.
   - Liczbę przefiltrowanych transakcji.
   - **Listę wszystkich wątków**, które uczestniczyły w przetwarzaniu strumienia.

## Analiza techniczna

### Operacje pośrednie (Intermediate Operations)
W kodzie zaimplementowano trzy kluczowe kroki przetwarzania:
1. **`.filter(t -> !t.isFraudulent)`**: Odsianie transakcji oznaczonych jako oszustwa.
2. **`.filter(t -> t.currency.equals("USD") || t.currency.equals("EUR"))`**: Selekcja transakcji w konkretnych walutach.
3. **`.map(...)`**: Wywołanie metody `simulateHeavyTaxCalculation`, która dzięki pętli z funkcjami `Math.hypot` i `Math.cos` sztucznie zwiększa koszt $Q$ (obliczeniowy) każdego elementu.

### Charakterystyka wątków
Dzięki zastosowaniu zbioru `threadNames`, program udowadnia wykorzystanie puli **ForkJoinPool.commonPool**. 
- W trybie sekwencyjnym na liście pojawi się tylko jeden wątek: `main`.
- W trybie równoległym program wylistuje wątki typu `ForkJoinPool.commonPool-worker-X`. 
- Liczba tych wątków zazwyczaj odpowiada liczbie rdzeni logicznych procesora pomniejszonej o jeden (gdyż wątek `main` również włącza się do pracy).

### Wydajność i Profilowanie (VisualVM)
Dzięki metodzie `simulateHeavyTaxCalculation`, obciążenie procesora jest bardzo wyraźne. 

| Tryb pracy | Obserwowane zachowanie |
| ---------- | ---------------------- |
| **Sekwencyjny** | Wykorzystanie CPU na poziomie jednego rdzenia. Długi czas oczekiwania. |
| **Równoległy** | Gwałtowny skok użycia wszystkich rdzeni (blisko 100%). Czas wykonania krótszy o ok. 60-80% (zależnie od liczby rdzeni). |

## Lista zadań (TODO)

### Faza 1: Architektura i dane
- [x] Definicja klasy `Transaction` z polami `amount`, `currency` i `isFraudulent`.
- [x] Implementacja wydajnego generatora danych opartego o klasę `Random`.
- [x] Stworzenie metody symulującej ciężkie obliczenia (`simulateHeavyTaxCalculation`).

### Faza 2: Implementacja Stream API
- [x] Przekształcenie kolekcji w strumień sekwencyjny (`stream()`).
- [x] Przekształcenie kolekcji w strumień równoległy (`parallelStream()`).
- [x] Zastosowanie mechanizmu monitorowania nazw wątków wewnątrz potoku.

### Faza 3: Analiza i Raportowanie
- [x] Porównanie czasów wykonania (milisekundy).
- [x] Weryfikacja liczby aktywnych wątków dla trybu równoległego.
- [x] Przygotowanie wniosków dotyczących zysku wydajnościowego.

### Faza 4: Dokumentacja
- [ ] Wyciągnięcie wniosków na temat opłacalności zrównoleglania.
- [ ] Przygotowanie finalnego sprawozdania.

## Podsumowanie
Implementacja dowiodła, że przy operacjach wymagających dużych nakładów obliczeniowych na każdym elemencie (wysokie $Q$), `parallelStream()` oferuje niemal liniowe przyspieszenie. Wykorzystanie `ConcurrentHashMap` do monitorowania wątków potwierdziło, że Java efektywnie zarządza pulą wątków roboczych, minimalizując czas bezczynności procesora.

## Autorzy
- Mateusz Moskwin
- Beniamin Raczyński
- Monika Szczerba
- Kacper Marciniak
- Maciej Wojnowski
