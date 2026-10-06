# Contributing 

Dziękujemy za zainteresowanie projektem! Ten dokument opisuje zasady i proces
współtworzenia, aby praca nad projektem była przyjemna i przewidywalna dla
wszystkich zaangażowanych.

---

## Spis treści

1.  [Kodeks postępowania](#kodeks-postępowania)
2.  [Wymagania wstępne](#wymagania-wstępne)
3.  [Zgłaszanie błędów](#zgłaszanie-błędów)
4.  [Proponowanie funkcji](#proponowanie-funkcji)
5.  [Proces Pull Request](#proces-pull-request)
6.  [Standardy kodu](#standardy-kodu)
7.  [Konwencje commitów](#konwencje-commitów)
8.  [Testy](#testy)
9.  [Dokumentacja](#dokumentacja)
10. [Licencja](#licencja)

---

## Kodeks postępowania

Projekt kieruje się zasadami **Contributor Covenant** w wersji 2.1.
Uczestnicząc w projekcie, zobowiązujesz się do przestrzegania następujących
zasad:

-   Bądź uprzejmy i wyrozumiały wobec innych.
-   Szanuj różnice w doświadczeniu, poglądach i pochodzeniu.
-   Udzielaj konstruktywnej informacji zwrotnej.
-   Skupiaj się na tym, co najlepsze dla społeczności.
-   Nie tolerujemy obraźliwych komentarzy, ataków personalnych ani
    dyskryminacji.

Zgłoszenia naruszeń należy kierować na adres e-mail maintainera podany
w profilu GitHub projektu. Wszystkie skargi zostaną rozpatrzone
sprawiedliwie i poufnie.

---

## Wymagania wstępne

Przed rozpoczęciem pracy upewnij się, że masz zainstalowane:

-   **Java 25 (LTS)** – wymagana wersja środowiska uruchomieniowego.
-   **Gradle Wrapper** – projekt korzysta z `./gradlew`, nie musisz
    instalować Gradle globalnie.
-   **Git** – do zarządzania wersjami.
-   **Edytor** – IntelliJ IDEA, VS Code, Neovim lub dowolny inny z
    obsługą Javy 25.
-   **Docker** – opcjonalnie, do testów integracyjnych.

### Szybki start

    git clone https://github.com/TWOJ_USER/plugin-template.git
    cd plugin-template
    ./gradlew build

Po udanym buildzie artefakt znajdziesz w `build/libs/`.

---

## Zgłaszanie błędów

Bug report powinien zawierać:

1.  **Tytuł** – krótki, opisowy (np. „NPE przy pustej konfiguracji").
2.  **Opis** – co się stało, a czego się spodziewałeś.
3.  **Kroki reprodukcji** – numerowana lista.
4.  **Środowisko**:
    -   Wersja Javy (`java -version`)
    -   Wersja Gradle (`./gradlew --version`)
    -   System operacyjny
5.  **Logi** – fragment logów lub stack trace.
6.  **Zrzuty ekranu** – jeśli dotyczą UI.

Szablon zgłoszenia znajduje się w `.github/ISSUE_TEMPLATE/bug_report.md`.

**Przed zgłoszeniem**:

-   Upewnij się, że problem nie został już zgłoszony.
-   Sprawdź, czy używasz najnowszej wersji z gałęzi `main`.

---

## Proponowanie funkcji

Feature request powinien zawierać:

1.  **Problem** – jaki problem rozwiązuje funkcja.
2.  **Propozycja** – jak miałaby działać.
3.  **Alternatywy** – jakie inne rozwiązania rozważałeś.
4.  **Dodatkowy kontekst** – przykłady użycia, linki do podobnych
    implementacji w innych projektach.

Szablon znajduje się w `.github/ISSUE_TEMPLATE/feature_request.md`.

**Przed zgłoszeniem**: rozważ, czy funkcja pasuje do ogólnego zakresu
projektu. Jeśli nie masz pewności, otwórz dyskusję w GitHub Discussions.

---

## Proces Pull Request

### 1. Fork i klonowanie

    # Forkuj repozytorium przez GitHub UI
    git clone https://github.com/TWOJ_FORK/plugin-template.git
    cd plugin-template
    git remote add upstream https://github.com/ORIGINAL/plugin-template.git

### 2. Utworzenie gałęzi

    git checkout -b feature/nazwa-funkcji

Konwencja nazewnictwa gałęzi:

| Typ            | Format                       | Przykład                    |
| -------------- | ---------------------------- | --------------------------- |
| Nowa funkcja   | `feature/krotki-opis`        | `feature/command-handler`   |
| Poprawka błędu | `fix/krotki-opis`            | `fix/null-config-crash`     |
| Refaktoryzacja | `refactor/krotki-opis`       | `refactor/service-layer`    |
| Dokumentacja   | `docs/krotki-opis`           | `docs/installation-guide`   |
| Testy          | `test/krotki-opis`           | `test/edge-cases`           |

### 3. Wprowadzenie zmian

-   Trzymaj się [standardów kodu](#standardy-kodu).
-   Pisz testy dla nowych funkcji.
-   Aktualizuj dokumentację, jeśli zmieniasz API.

### 4. Uruchomienie testów

    ./gradlew check

To polecenie uruchamia:

-   kompilację,
-   testy jednostkowe,
-   testy integracyjne (jeśli skonfigurowane),
-   Checkstyle,
-   SpotBugs.

### 5. Commit

    git add .
    git commit -m "feat: dodaj handler komend"

Konwencje commitów opisane są w sekcji [Konwencje commitów](#konwencje-commitów).

### 6. Push i Pull Request

    git push origin feature/nazwa-funkcji

Następnie otwórz Pull Request na GitHubie. W opisie PR zawrzyj:

-   **Co** – jakie zmiany wprowadzasz.
-   **Dlaczego** – jaki problem rozwiązują.
-   **Jak** – krótki opis implementacji.
-   **Testy** – jak przetestować zmiany.
-   **Issues** – odwołania do powiązanych zgłoszeń (np. `Closes #42`).

### 7. Code review

-   Reaguj na uwagi recenzentów.
-   Wprowadzaj poprawki w osobnych commitach (nie force-pushuj, dopóki
    recenzja trwa).
-   Po zatwierdzeniu maintainer zmerguje PR.

### 8. Po merge

    git checkout main
    git pull upstream main
    git branch -d feature/nazwa-funkcji

---

## Standardy kodu

### Java

-   **Wersja**: Java 25 z pełnym wykorzystaniem nowoczesnych funkcji
    (`var`, `record`, `sealed`, `switch` expressions, text blocks).
-   **Formatowanie**: 4 spacje, UTF-8, LF.
-   **Długość linii**: maksymalnie 120 znaków.
-   **Nazewnictwo**:
    -   Klasy: `PascalCase`
    -   Metody i zmienne: `camelCase`
    -   Stałe: `UPPER_SNAKE_CASE`
    -   Pakiety: `lowercase`
-   **Importy**:
    -   Unikaj importów gwiazdkowych (`import java.util.*`).
    -   Usuwaj nieużywane importy (Checkstyle to wymusi).
-   **Wyjątki**:
    -   Nie łap `Exception` bez konkretnego powodu.
    -   Nie używaj pustych bloków `catch`.
    -   Zawsze przekazuj kontekst w komunikatach błędów.
-   **Komentarze**:
    -   Komentuj **dlaczego**, nie **co**.
    -   Javadoc dla publicznych API.

### Konfiguracja IDE

Projekt zawiera `.editorconfig`, który automatycznie ustawia formatowanie
w większości edytorów. Upewnij się, że Twoje IDE go respektuje.

### Analiza statyczna

-   **Checkstyle** – sprawdza styl kodu.
-   **SpotBugs** – wykrywa potencjalne błędy.

Oba narzędzia uruchamiają się przy `./gradlew check`. Build nie przejdzie,
jeśli znajdą poważne problemy.

---

## Konwencje commitów

Projekt używa **Conventional Commits** w wersji 1.0.0.

### Format

    <typ>(<zakres>): <krótki opis>

    <opcjonalny dłuższy opis>

    <opcjonalne stopki>

### Typy

| Typ        | Opis                                              |
| ---------- | ------------------------------------------------- |
| `feat`     | Nowa funkcja                                      |
| `fix`      | Poprawka błędu                                    |
| `docs`     | Zmiany w dokumentacji                             |
| `style`    | Formatowanie, brak zmian w kodzie                 |
| `refactor` | Refaktoryzacja bez zmiany zachowania              |
| `perf`     | Poprawa wydajności                                |
| `test`     | Dodanie lub poprawa testów                        |
| `build`    | Zmiany w systemie build (Gradle, zależności)      |
| `ci`       | Zmiany w CI/CD                                    |
| `chore`    | Pozostałe zadania (nie dotyczy `src` ani `test`)  |
| `revert`   | Cofnięcie wcześniejszego commita                  |

### Przykłady

    feat(commands): dodaj obsługę komend z argumentami
    fix(config): napraw NPE przy pustym pliku
    docs(readme): zaktualizuj instrukcję instalacji
    refactor(service): wydziel logikę do osobnej klasy
    test(parser): dodaj testy dla przypadków brzegowych

### Breaking changes

Jeśli commit wprowadza niekompatybilną zmianę, dodaj `!` po typie oraz
stopkę `BREAKING CHANGE`:

    feat(api)!: zmień sygnaturę metody execute

    BREAKING CHANGE: metoda execute przyjmuje teraz dwa argumenty zamiast
    jednego. Zaktualizuj wywołania.

---

## Testy

### Testy jednostkowe

-   Umieszczaj w `src/test/java`.
-   Używaj **JUnit 5** (Jupiter).
-   Nazwy klas: `<Klasa>Test`.
-   Nazwy metod: opisowe, np. `shouldReturnNullWhenInputIsEmpty`.
-   Pokrycie: dąż do **80%** dla nowego kodu.
-   Mockowanie: używaj **Mockito** lub wbudowanych mechanizmów.

### Testy integracyjne

-   Umieszczaj w `src/integrationTest/java` (jeśli skonfigurowane).
-   Uruchamiaj przez `./gradlew integrationTest`.
-   Wymagają Dockera (TestContainers).

### Uruchamianie testów

    # Wszystkie testy
    ./gradlew test

    # Konkretna klasa
    ./gradlew test --tests "com.example.MyClassTest"

    # Konkretna metoda
    ./gradlew test --tests "com.example.MyClassTest.shouldDoSomething"

    # Z raportem HTML
    ./gradlew test
    # Otwórz build/reports/tests/test/index.html

---

## Dokumentacja

-   **README.md** – aktualizuj przy zmianach w API publicznym.
-   **Javadoc** – pisz dla wszystkich publicznych klas i metod.
-   **Komentarze** – tylko dla nietrywialnej logiki.
-   **CHANGELOG.md** – aktualizuj przy nowych wersjach.

### Format Javadoc

    /**
     * Krótki opis metody.
     *
     * <p>Dłuższy opis, jeśli potrzebny.</p>
     *
     * @param name nazwa użytkownika, nie może być null
     * @return sformatowany komunikat powitalny
     * @throws IllegalArgumentException jeśli name jest pusty
     */
    public String greet(String name) {
        // ...
    }

---

## Licencja

Współtworząc projekt, zgadzasz się na udostępnienie swojego kodu na
warunkach licencji **MIT** (patrz plik `LICENSE`).

Oznacza to, że:

-   Twój kod może być używany komercyjnie.
-   Twój kod może być modyfikowany i redystrybuowany.
-   Musisz zachować informację o prawach autorskich.

Jeśli nie zgadzasz się z tymi warunkami, nie wysyłaj Pull Requesta.

---

## Pytania?

-   **GitHub Issues** – problemy i propozycje.
-   **GitHub Discussions** – pytania, dyskusje, pomoc.
-   **E-mail** – sprawy poufne (adres w profilu maintainera).

Dziękujemy za wkład w rozwój projektu!
