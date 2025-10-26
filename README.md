# Bierzmowanie-konwerter-generacja-kartek
Program do konwertowania danych z arkusza kalkulacyjnego na dane w formacie Microsoft Word. Dane osób w pliku są zapisywane zgodnie ze schematem kartek do bierzmowania.

# 🕊️ Bierzmowanie – Generator świadectw

Aplikacja **Bierzmowanie** to narzędzie desktopowe napisane w **Java (Swing)**, służące do automatycznego generowania dokumentów Word (`.docx`) zawierających dane uczestników bierzmowania.  
Program został zaprojektowany z myślą o prostocie obsługi, kompatybilności z różnymi formatami arkuszy oraz bezpieczeństwie licencyjnym.

---

## ✨ Kluczowe funkcje

- 📂 **Obsługa plików wejściowych:**  
  Program wczytuje dane z plików:
  - `.xlsx` (Excel 2007 i nowszy)  
  - `.xls` (Excel 97–2003)  
  - `.csv` (plik tekstowy, automatyczne wykrywanie separatora `,` lub `;` oraz kodowania)

- 🧾 **Generowanie dokumentów Word:**  
  Na podstawie wczytanych danych program tworzy dokument `.docx`, w którym:
  - każdy rekord (osoba) generowany jest według gotowego wzorca (szablonu `.docx`),
  - dane są automatycznie uzupełniane w odpowiednich miejscach,
  - tekst "Imię z bierzmowania" wyróżniany jest kolorem **czerwonym**,
  - na jednej stronie mieszczą się **dwa świadectwa**.

- ⚙️ **Parametryzacja:**  
  Użytkownik może określić:
  - odstęp (liczbę spacji) między nazwą parafii a numerem L.p.,
  - lokalizację zapisu wygenerowanego pliku,
  - nazwę wynikowego pliku (automatycznie zawiera znacznik czasu).

- 🧠 **Inteligentna obsługa plików CSV:**  
  Program sam wykrywa separator i kodowanie (UTF-8, Windows-1250, ISO-8859-2), dzięki czemu nie wymaga ręcznej konfiguracji.

- 💬 **Wbudowana instrukcja użytkownika:**  
  Dostępna bezpośrednio z interfejsu poprzez przycisk **„Instrukcja”**, w czytelnej formie HTML.

---

## 🔒 System licencyjny

Aplikacja posiada wbudowany **mechanizm zabezpieczenia licencyjnego**, który weryfikuje zgodność licencji przy każdym uruchomieniu.

Dzięki temu program działa wyłącznie na komputerze, na którym został aktywowany.

---

## Technologie

- **Java 8+**
- **Swing (GUI)**
- **Apache POI** – do obsługi plików Excel (`.xls`, `.xlsx`) i Word (`.docx`)
- **OpenCSV** – do przetwarzania plików `.csv`
- **AES Encryption** – do szyfrowania danych licencyjnych

---

##  Jak używać

1. Uruchom aplikację `Bierzmowanie`.
2. Wybierz plik wejściowy (`CSV`, `XLS`, `XLSX`) zawierający dane uczestników.
3. Określ liczbę spacji między nazwą parafii a oznaczeniem L.p.
4. Kliknij **„Wygeneruj Word z danymi z pliku”**.
5. Zapisz wygenerowany dokument Word w wybranym folderze.
6. Zweryfikuj układ stron w pliku wynikowym.

---

## Licencja
Ten projekt jest chroniony licencją **CC BY-NC-ND 4.0 (Creative Commons Attribution-NonCommercial-NoDerivatives 4.0)**.  
Projekt chroniony lokalnym systemem licencji i przeznaczony do użytku wewnętrznego (w parafii lub instytucji kościelnej).  
Nie wymaga połączenia z Internetem i działa **offline**.
- Zabrania się używania przedstawionego kodu lub jego fragmentów w celach komercyjnych.  
- Zabrania się dokonywania modyfikacji w programie.  
- Dozwolone jest przeglądanie kodu i jego wykorzystanie w celach edukacyjnych.
Wszystkie prawa zastrzeżone.
---

## Autor

**Mateusz Goc**  
Projekt stworzony w celach użytkowych i edukacyjnych.  
Inspiracją była potrzeba automatyzacji procesu przygotowywania świadectw bierzmowania w parafiach.

---


