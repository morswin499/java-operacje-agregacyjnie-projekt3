# Projekt 3: Operacje agregacyjne i strumienie danych
**Przedmiot:** Programowanie współbieżne w języku JAVA

## O projekcie
Celem projektu jest poznanie sposobu zrównoleglonego rozwiązywania problemów programistycznych z wykorzystaniem strumieni danych (`Stream API`). Aplikacja pozwala na porównanie wydajności klasycznego, sekwencyjnego przetwarzania danych z podejściem zrównoleglonym, wykorzystującym wszystkie dostępne zasoby procesora.

Projekt demonstruje:
1. **Przetwarzanie potokowe:** Zastosowanie operacji `filter`, `map` oraz `sorted` w ramach jednego łańcucha wywołań.
2. **Automatyczne zrównoleglenie:** Przekształcenie strumienia za pomocą metody `parallelStream()` i wykorzystanie wspólnej puli wątków.
3. **Analizę wydajnościową:** Porównanie czasu wykonania operacji na dużym zbiorze danych (10 mln rekordów) przy użyciu profilera.

## Instrukcja uruchomienia i testowania
Główną klasą aplikacji jest `StreamAnalyzer.java`. Program generuje syntetyczny zbiór transakcji, a następnie wykonuje na nich operacje agregacyjne.

Aby przetestować aplikację:
1. Wewnątrz metody `main` w pliku `StreamAnalyzer.java` znajdź zmienną `MODE`:
   - `int MODE = 0;` – Uruchamia standardowy strumień sekwencyjny (`stream()`).
   - `int MODE = 1;` – Uruchamia strumień równoległy (`parallelStream()`).
2. Parametr `DATA_SIZE` określa liczbę generowanych obiektów (domyślnie 10 000 000 dla wyraźnych wyników pomiarowych).
3. Po zakończeniu operacji program wyświetli czas wykonania w milisekundach oraz statystyki dotyczące wykorzystanej puli wątków.

## Analiza techniczna

### Porównanie strumieni standardowych i równoległych
W trybie sekwencyjnym (`MODE = 0`) operacje są wykonywane krok po kroku w głównym wątku aplikacji. Przy dużych zbiorach danych i złożonych operacjach (jak sortowanie), czas wykonania jest znacznie dłuższy. W trybie równoległym (`MODE = 1`) Java dzieli zbiór danych na części, które są procesowane jednocześnie na wielu rdzeniach procesora, co pozwala na znaczące skrócenie czasu operacji przy odpowiednio dużym $N$.

### Charakterystyka wątków w parallelStream
Współbieżne operacje na strumieniach wykorzystują systemową pulę **ForkJoinPool.commonPool**. 
- **Liczba wątków:** Jest ona determinowana przez liczbę dostępnych procesorów logicznych (zazwyczaj $N-1$).
- **Typ wątków:** Są to wątki typu daemon, co oznacza, że działają w tle i nie blokują zakończenia procesu głównego.
- **Efektywność:** Dzięki mechanizmowi work-stealing, wątki, które szybciej ukończyły swoje pod-zadania, pomagają w przetwarzaniu pozostałych części danych.

### Model NQ i wydajność
Wydajność została przeanalizowana w oparciu o model NQ, gdzie N to liczba elementów, a Q to koszt obliczeniowy pojedynczej operacji. Zaobserwowano, że:
- Dla operacji bezstanowych (np. `filter`, `map`) wzrost wydajności jest niemal liniowy względem liczby rdzeni.
- Operacje stanowe (np. `sorted`) generują dodatkowy narzut w trybie równoległym ze względu na konieczność synchronizacji i scalania wyników, jednak przy 10 mln rekordów zysk z równoległego sortowania pozostaje zauważalny.

### Profilowanie wydajności (VisualVM)

Przebiegi zużycia procesora dla obu trybów:

| Tryb sekwencyjny (stream) | Tryb równoległy (parallel) |
| ------------------------- | -------------------------- |
| ![CPU Sekwencyjny](./assets/cpu_seq.png) | ![CPU Równoległy](./assets/cpu_par.png) |

W trybie równoległym widoczne jest pełne obciążenie wszystkich wątków w puli `ForkJoinPool`, co przekłada się na gwałtowny wzrost utylizacji procesora (do 80-100%) przy jednoczesnym drastycznym skróceniu czasu trwania operacji w porównaniu do trybu jednowątkowego.

## Lista zadań (TODO)

### Faza 1: Architektura i bazowa logika
- [ ] Implementacja klasy modelu `Transaction` z odpowiednimi polami.
- [ ] Stworzenie generatora syntetycznych danych o dużej skali.
- [ ] Implementacja bazowego potoku operacji (filter -> map -> sorted).

### Faza 2: Implementacja strumieni
- [ ] Przetwarzanie danych za pomocą metody `stream()`.
- [ ] Przetwarzanie danych za pomocą metody `parallelStream()`.
- [ ] Implementacja logiki zbierania statystyk o użytej puli wątków.

### Faza 3: Profilowanie i Analiza
- [ ] Pomiary czasu dla różnych wielkości zbiorów danych.
- [ ] Rejestracja przebiegów CPU w narzędziu VisualVM.
- [ ] Porównanie wyników i analiza narzutu operacji `sorted()`.

### Faza 4: Dokumentacja
- [ ] Wyciągnięcie wniosków na temat opłacalności zrównoleglania.
- [ ] Przygotowanie finalnego sprawozdania.

## Podsumowanie
Analiza wykazała, że strumienie równoległe są potężnym narzędziem optymalizacyjnym w języku Java, o ile zbiór danych jest wystarczająco duży, by zniwelować narzut związany z zarządzaniem wątkami. Kluczowym czynnikiem sukcesu jest unikanie współdzielenia stanów modyfikowalnych oraz świadomość kosztów operacji stanowych w środowisku rozproszonym.
