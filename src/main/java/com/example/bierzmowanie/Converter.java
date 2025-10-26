package com.example.bierzmowanie;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import javax.swing.Timer;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.io.File;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.*;
import java.awt.image.*;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageSz;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STPageOrientation;

import java.io.*;
import java.io.*;
import java.nio.file.*;
import javax.swing.*;
import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;

public class Converter
{
    private JEditorPane instructionPane;
    private static final String LICENSE_FILE = "mvnlicense.dat"; // 🔹 Plik licencyjny
    private static final String LICENSE_MARKER = "activation.marker";
    private JComboBox<Integer> spaceSelector;
    int boardWidth = 700;
    int boardHeight = 700;

    JFrame frame=new JFrame("Bierzmowanie version 1.0");
    JLabel fileLabel;
    private File selectedFile;
    private XWPFDocument generatedDocument;
    private Image backgroundImage;
    String instructionText =
            "<html><body style='font-family:sans-serif; font-size:12px; width:700px'>" +
                    "<h2 style='text-align:center;'>Instrukcja obsługi</h2>" +
                    "<ol>" +
                    "<li><b>Konstrukcja pliku wejściowego:</b><br>" +
                    "• Do programu należy wgrać uzupełniony plik w formacie .xls, .xlsx lub .csv. Program współpracuje z plikami z pakietów: Microsoft Office oraz Open Office.<br>" +
                    "• Plik musi być zgodny ze wzorem.<br>" +
                    "• Uwaga na kolumnę <b>'Imię z bierzmowania'</b> – dane będą zapisane na czerwono, ale wielkość liter zależy od danych wejściowych tzn. imię zapisane w Excelu np. 'Justyna' zostanie zapisane w dokumencie WORD jako 'Justyna', a jeśli imię zostanie wprowadzone w takiej formie: 'JUSTYNA' to w dokumencie WORD również pojawi się jako 'JUSTYNA'.<br>" +
                    "• Kolumny <b>'Nazwa parafii'</b>, <b>'Adres parafii'</b> i <b>'Kod pocztowy i miejscowość parafii'</b> są obowiązkowe – brak danych uniemożliwi wygenerowanie dokumentu.<br>" +
                    "• Dane należy wprowadzać od wiersza nr 2 (tuż pod nagłówkiem).<br>" +
                    "• Kolumny: <b>'Miejsce i data bierzmowania'</b>, <b>'Szafarz bierzmowania'</b>, <b>'Nazwa parafii'</b>, <b>'Adres parafii'</b>, <b>'Kod pocztowy i miejscowość parafii'</b>, <b>'Rok bierzmowania'</b> należy uzupełnić tylko raz – w pierwszym wierszu tuż pod nazwą kolumny. Najlepiej wprowadzić dane do tych kolumn jako ostatnie, po posortowaniu w Excelu danych z pozostałych kolumn. Można również wprowadzić do Excela wszystkie dane i posortować, a następnie dane ze wspomnianych w tym punkcie kolumn przekopiować z powrotem do odpowiednich komórek tuż pod nazwami odpowiednich kolumn (czyli do 2 wiersza arkusza).<br>" +
                    "• Kolumna <b>'Rok bierzmowania'</b> powinna zawierać np. 2025 – bez ukośników. Program sam doda znak '/' i numer L.p.<br>" +
                    "• <b>Przypomnienie:</b> Program <b>nie sortuje</b> alfabetycznie – należy to zrobić wcześniej w Excelu.<br>" +
                    "</li>" +
                    "<li><b>Wczytaj plik:</b> Kliknij przycisk <i>'Wczytaj plik EXCEL CSV/XLSX/XLS'</i>, wybierz plik i zatwierdź przyciskiem 'Open'.</li>" +
                    "<li><b>Wybierz odstęp:</b> Wybierz liczbę spacji pomiędzy nazwą parafii a napisem L.p. Zakres: 0–120. Wartość zależy od długości nazwy parafii.</li>" +
                    "<li><b>Generuj dokument:</b> Po załadowaniu pliku kliknij <i>'Wygeneruj Word z danymi z pliku'</i>, aby utworzyć dokument WORD.</li>" +
                    "<li><b>Zapisz plik:</b> Kliknij <i>'Pobierz plik WORD'</i>, wybierz folder i zapisz dokument. Na jednej stronie znajdują się dwa świadectwa.</li>" +
                    "<li><b>Sprawdzenie pliku WORD:</b><br>" +
                    "• Po wygenerowaniu i pobraniu pliku, należy go uważnie sprawdzić.<br>" +
                    "• Zweryfikuj układ każdej strony – czy np. tekst nie \"przeskakuje\" na kolejną stronę.<br>" +
                    "• Jeśli tak się dzieje, możesz usunąć jedną z pustych linii np. między sekcjami <i>'Data i miejsce chrztu'</i> i <i>'Świadek bierzmowania'</i>.<br>" +
                    "</li>" +
                    "</ol>" +
                    "<p style='text-align:center;'>Obsługiwane formaty pliku wejściowego: <b>CSV, XLSX, XLS</b></p>" +
                    "</body></html>";


    public static void main(String[] args) {
        // **🔹 Sprawdzenie licencji przed uruchomieniem GUI**
        String currentSerial = getMotherboardSerial();
        String savedSerial = readLicenseFile();
//        if (savedSerial == null) {
//            writeLicenseFile(currentSerial);
//            //   JOptionPane.showMessageDialog(null, "Licencja zapisana. Aplikacja aktywowana na tym komputerze.", "Licencja", JOptionPane.INFORMATION_MESSAGE);
//        } else if (!savedSerial.equals(currentSerial)) {
//            //  JOptionPane.showMessageDialog(null, "BŁĄD: Licencja nie pasuje do tego komputera!\nAplikacja zostanie zamknięta.", "Błąd licencji", JOptionPane.ERROR_MESSAGE);
//            System.exit(1);
//        }
        if (savedSerial == null) {
            if (Files.exists(Paths.get(LICENSE_MARKER))) {
                // 🔒 Plik licencyjny był już wcześniej, a teraz go brakuje – blokujemy uruchomienie
                JOptionPane.showMessageDialog(null, "Błąd: Plik licencyjny został usunięty!\nAplikacja nie może zostać uruchomiona.", "Błąd licencji", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            } else {
                // 🟢 Pierwsze uruchomienie – zapisujemy licencję i marker
                writeLicenseFile(currentSerial);
                try {
                    Files.createFile(Paths.get(LICENSE_MARKER));
                    System.out.println("🔐 Marker aktywacji zapisany.");
                } catch (IOException e) {
                    System.err.println("❌ Błąd podczas tworzenia markera aktywacji: " + e.getMessage());
                }
               // System.out.println("🔐 Marker aktywacji zapisany.");
            }
        } else if (!savedSerial.equals(currentSerial)) {
            JOptionPane.showMessageDialog(null, "BŁĄD: Licencja nie pasuje do tego komputera!\nAplikacja zostanie zamknięta.", "Błąd licencji", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }


        // **🔹 Uruchomienie aplikacji po weryfikacji licencji**
        SwingUtilities.invokeLater(Converter::new);
    }

    //    JPanel mainPanel=new JPanel();
    Converter() {

        try {
           // backgroundImage = ImageIO.read(new File("src/main/resources/holy-spirit-8847203_1280.jpg")); // Podaj ścieżkę do obrazu
           // backgroundImage = ImageIO.read(new File("resources/holy-spirit-8847203_1280.jpg"));
            backgroundImage = ImageIO.read(getClass().getResource("/holy-spirit-8847203_1280.jpg"));

        } catch (IOException e) {
            System.err.println("Błąd wczytywania obrazu: " + e.getMessage());
        }
        JPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(null);

        frame.setVisible(true);
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setAlwaysOnTop(true);
        frame.setContentPane(backgroundPanel);
        frame.setLayout(null);

        JButton loadCsvButton=new JButton("Wczytaj plik  EXCEL csv/xlsx/xls");
     //   loadCsvButton.setFocusable(false);
        loadCsvButton.setFocusPainted(false);
        JButton convertButton=new JButton("Wygeneruj Word z danymi z pliku");
        convertButton.setFocusPainted(false);
        JButton downloadButton=new JButton("Pobierz plik WORD");
        downloadButton.setFocusPainted(false);
        JButton helpButton = new JButton("Instrukcja");
        helpButton.setFocusPainted(false);
        loadCsvButton.setBackground(new Color(0, 0, 139));
        loadCsvButton.setForeground(Color.white);
        convertButton.setBackground(new Color(0, 0, 139));
        convertButton.setForeground(Color.white);
        downloadButton.setBackground(new Color(0, 0, 139));
        downloadButton.setForeground(Color.white);
        helpButton.setBackground(new Color(0, 0, 139));
        helpButton.setForeground(Color.white);
//        mainPanel.setLayout(new BorderLayout());
//        mainPanel.add(loadCsvButton,BorderLayout.WEST);
//        frame.add(mainPanel,BorderLayout.CENTER);
        int buttonWidth = 150;
        int buttonHeight = 40;
        int x = 225;  // 50px od lewej krawędzi
        int y = (int) (boardHeight * 0.30);
        int y2=(int) (boardHeight*0.6);
        int y3=(int) (boardHeight*0.75);
        int y4=(int) (boardHeight*0.85);
        loadCsvButton.setBounds(x, y, 250, buttonHeight);
        JLabel fileTextLabel = new JLabel("Wczytany plik:");
        fileTextLabel.setBounds(x, y + 50, 150, 30);
        fileTextLabel.setFont(new Font("Arial", Font.BOLD, 20));
        fileTextLabel.setForeground(Color.BLACK);
        fileTextLabel.setBackground(Color.white);
        fileTextLabel.setOpaque(true);
        convertButton.setBounds(x,y2,250,buttonHeight);
        fileLabel = new JLabel("");
        fileLabel.setBounds(x + 100, y + 50, 500, 30);
//        fileLabel.setForeground(Color.BLACK);
//        fileLabel.setBackground(Color.white);
//        fileLabel.setOpaque(true);
        downloadButton.setBounds(x,y3,250,buttonHeight);
        helpButton.setBounds(x+50, y4, 150, 40);
        loadCsvButton.setBorder(new LineBorder(new Color(100, 120, 0), 5));
        convertButton.setBorder(new LineBorder(new Color(100, 120, 0), 5));
        downloadButton.setBorder(new LineBorder(new Color(100, 120, 0), 5));
        helpButton.setBorder(new LineBorder(new Color(100, 120, 0), 5));
        // Dodanie przycisku bezpośrednio do ramki
//        frame.add(loadCsvButton);
//        frame.add(fileTextLabel);
//        frame.add(fileLabel);
//        frame.add(convertButton);
//        frame.add(downloadButton);
//        frame.add(helpButton);
        // 🔹 Pozycjonowanie na środku poziomo
        int spacingLabelWidth = 400;
        int spaceSelectorWidth = 60;
        int totalWidth = spacingLabelWidth + spaceSelectorWidth + 10; // odstęp 10px między nimi
        int centerX = (frame.getWidth() - totalWidth) / 2;
        int verticalY = y + 150;

// 🔹 Label z opisem
        JLabel spacingLabel = new JLabel("Liczba spacji (odstęp między nazwą parafii a liczbą porządkową):");
        spacingLabel.setBounds(centerX, verticalY, spacingLabelWidth, 30);
        spacingLabel.setForeground(Color.BLACK);
        spacingLabel.setBackground(Color.white);
        spacingLabel.setOpaque(true);
        backgroundPanel.add(spacingLabel);

// 🔹 Lista rozwijana z wartościami 0–120
        spaceSelector = new JComboBox<>();
        for (int i = 0; i <= 120; i++) {
            spaceSelector.addItem(i);
        }
        spaceSelector.setBounds(centerX + spacingLabelWidth + 10, verticalY, spaceSelectorWidth, 30);
        spaceSelector.setToolTipText("Wybierz liczbę spacji między nazwą parafii a L.p.");
        spaceSelector.setBackground(new Color(0, 0, 139));      // Granatowe tło jak w przyciskach
        spaceSelector.setForeground(Color.WHITE);              // Biały tekst
        spaceSelector.setBorder(new LineBorder(new Color(100, 120, 0), 5)); // Taka sama ramka jak przycisk
        spaceSelector.setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton button = new JButton(new Icon() {
                    @Override
                    public void paintIcon(Component c, Graphics g, int x, int y) {
                        g.setColor(Color.WHITE);
                        int[] xPoints = {x, x + getIconWidth() / 2, x + getIconWidth()};
                        int[] yPoints = {y, y + getIconHeight(), y};
                        g.fillPolygon(xPoints, yPoints, 3);
                    }

                    @Override
                    public int getIconWidth() {
                        return 10;
                    }

                    @Override
                    public int getIconHeight() {
                        return 6;
                    }
                });

             //   button.setBorder(new LineBorder(new Color(100, 120, 0), 5));
                button.setBackground(new Color(0, 0, 139));
                button.setOpaque(true);
                return button;
            }
        });
        instructionPane = new JEditorPane("text/html", instructionText);
        instructionPane.setEditable(false);
        backgroundPanel.add(spaceSelector);
        backgroundPanel.add(loadCsvButton);
        backgroundPanel.add(fileTextLabel);
        backgroundPanel.add(fileLabel);
        backgroundPanel.add(convertButton);
        backgroundPanel.add(downloadButton);
        backgroundPanel.add(helpButton);
        try {
          //  ImageIcon icon = new ImageIcon("src/main/resources/holy-spirit-8847203_1280.jpg");
            ImageIcon icon = new ImageIcon("resources/holy-spirit-8847203_1280.jpg");
            Image image = icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            frame.setIconImage(image);
        } catch (Exception e) {
            System.err.println("Błąd wczytywania ikony: " + e.getMessage());
        }
        fileTextLabel.setSize(fileTextLabel.getPreferredSize());
        fileTextLabel.setLocation((frame.getWidth() - fileTextLabel.getWidth()) / 2, fileTextLabel.getY());
        UIManager.put("Button.focus", new Color(0, 0, 0, 0));
        loadCsvButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadCsvButton.setEnabled(false); // Tymczasowo blokujemy

                SwingWorker<File, Void> worker = new SwingWorker<File, Void>() {
                    @Override
                    protected File doInBackground() {
                        JFileChooser chooser = new JFileChooser();
                        chooser.setMultiSelectionEnabled(false);
                        FileNameExtensionFilter filter = new FileNameExtensionFilter("Pliki CSV i Excel", "csv", "xlsx", "xls");
                        chooser.setFileFilter(filter);

                        int status = chooser.showOpenDialog(frame);
                        if (status == JFileChooser.APPROVE_OPTION) {
                            return chooser.getSelectedFile();
                        }
                        return null;
                    }

                    @Override
                    protected void done() {
                        try {
                            File file = get();
                            if (file != null) {
                                selectedFile = file;

                                if (isValidFile(selectedFile)) {
                                    System.out.println("Wybrano plik: " + selectedFile.getAbsolutePath());
                                    fileTextLabel.setText("Wczytano plik: " + selectedFile.getName());
                                } else {
                                    JOptionPane.showMessageDialog(frame, "Błąd: Wybrany plik nie istnieje!", "Błąd", JOptionPane.ERROR_MESSAGE);
                                    fileTextLabel.setText("Wczytany plik:");
                                    selectedFile = null;
                                }
                            } else {
                                JOptionPane.showMessageDialog(frame, "Nie wczytano pliku!", "Błąd", JOptionPane.WARNING_MESSAGE);
                                fileTextLabel.setText("Nie wczytano pliku!");
                                selectedFile = null;
                            }

                            // Ustawiamy pozycję i rozmiar etykiety
                            fileTextLabel.setSize(fileTextLabel.getPreferredSize());
                            fileTextLabel.setLocation((frame.getWidth() - fileTextLabel.getWidth()) / 2, fileTextLabel.getY());

                        } catch (Exception ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(frame, "Wystąpił błąd podczas ładowania pliku!", "Błąd", JOptionPane.ERROR_MESSAGE);
                        } finally {
                            loadCsvButton.setEnabled(true); // Odblokuj przycisk niezależnie od wyniku
                        }
                    }
                };

                worker.execute();
            }
        });

        convertButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Utwórz okno dialogowe z paskiem postępu
                JDialog progressDialog = new JDialog(frame, "Przetwarzanie...", true);
                progressDialog.setSize(300, 100);
                progressDialog.setLocationRelativeTo(frame);
                progressDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
                progressDialog.setLayout(new BorderLayout());

                JProgressBar progressBar = new JProgressBar();
                progressBar.setIndeterminate(true); // Pasek w trybie "nieokreślonym" (animowany)
                progressDialog.add(progressBar, BorderLayout.CENTER);

                JLabel progressLabel = new JLabel("Generowanie dokumentu...", JLabel.CENTER);
                progressDialog.add(progressLabel, BorderLayout.NORTH);

                // Uruchom proces w osobnym wątku
                SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        convertButton.setEnabled(false); // Wyłącz przycisk konwersji

                        if (selectedFile != null) {
                            List<List<String>> columnData = convertToColumnLists(selectedFile);
                            List<Integer> size = new ArrayList<>();
                            for (int i = 0; i < columnData.size(); i++) {
                                size.add(columnData.get(i).size());
                            }
                            Integer h = Collections.max(size);
                            Integer w = (h % 4 == 0) ? (h / 4) : (h / 4 + 1);

                            // **Generowanie dokumentu**
                           // generatedDocument = readWordFile("src/main/resources/Kartka.docx", w, columnData, h);
                          //  generatedDocument = readWordFile("resources/Kartka.docx", w, columnData, h);
                         //   generatedDocument = readWordFile("/Kartka.docx", w, columnData, h);
                            InputStream docxStream = getClass().getResourceAsStream("/Kartka.docx");
                            generatedDocument = readWordFile(docxStream, w, columnData, h);
                        }
                        System.out.println(selectedFile);
                        return null;
                    }

                    @Override
                    protected void done() {
                        progressDialog.dispose(); // Zamknięcie okna postępu
                        convertButton.setEnabled(true); // Włącz ponownie przycisk

                        if (generatedDocument == null) {
                            JOptionPane.showMessageDialog(frame, "Błąd generowania dokumentu!", "Błąd", JOptionPane.ERROR_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(frame, "Operacja generowania dokumentu powiodła się!\nNaciśnij przycisk 'Pobierz plik WORD' aby zapisać dokument na komputerze.", "Sukces", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                };

                // Uruchom worker w tle
                worker.execute();

                // **Wyświetl okno postępu**
                progressDialog.setVisible(true);
            }
        });
        downloadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (generatedDocument == null) {
                    JOptionPane.showMessageDialog(frame, "Najpierw wygeneruj dokument!", "Błąd", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                JFileChooser chooser = new JFileChooser();
                chooser.setDialogTitle("Wybierz folder do zapisania pliku");
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                int status = chooser.showSaveDialog(frame);
                if (status == JFileChooser.APPROVE_OPTION) {
                    File selectedFolder = chooser.getSelectedFile();
                    LocalDateTime now = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
                    String timestamp = now.format(formatter);
                    String savePath = selectedFolder.getAbsolutePath() + "/Bierzmowanie_" + timestamp + ".docx";

                    downloadButton.setEnabled(false); // ⛔ tymczasowo zablokuj

                    SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                        @Override
                        protected Void doInBackground() {
                            try (FileOutputStream fos = new FileOutputStream(savePath)) {
                                generatedDocument.write(fos);
                            } catch (IOException ex) {
                                SwingUtilities.invokeLater(() -> {
                                    JOptionPane.showMessageDialog(frame, "Błąd zapisu pliku!", "Błąd", JOptionPane.ERROR_MESSAGE);
                                    System.err.println("Błąd zapisu pliku: " + ex.getMessage());
                                });
                            }
                            return null;
                        }

                        @Override
                        protected void done() {
                            downloadButton.setEnabled(true); // ✅ ponownie włącz
                            JOptionPane.showMessageDialog(frame, "Plik zapisano w: " + savePath, "Sukces", JOptionPane.INFORMATION_MESSAGE);
                        }
                    };

                    worker.execute();
                }
            }
        });

        helpButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
//                SwingUtilities.invokeLater(() -> helpButton.setEnabled(false));
//                SwingUtilities.invokeLater(() -> helpButton.setEnabled(true));
//                showInstructionDialog();
                helpButton.setEnabled(false); // 👈 natychmiast wyłącza
                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() {
                        return null; // nic nie robimy w tle
                    }

                    @Override
                    protected void done() {
                        showInstructionDialog();
                        helpButton.setEnabled(true); // ponownie aktywuj po zamknięciu
                    }
                }.execute();
            }
        });
    }
    class BackgroundPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }
    private boolean isValidFile(File file) {
        if (!file.exists() || !file.isFile()) {
            return false;
        }

        // Pobieramy rozszerzenie pliku
        String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".csv") || fileName.endsWith(".xlsx") || fileName.endsWith(".xls");
    }

    public static List<List<String>> convertToColumnLists(File file) {
        String fileName = file.getName().toLowerCase();

        if (fileName.endsWith(".csv")) {
            return convertCSVToColumnLists(file);
        } else if (fileName.endsWith(".xlsx")) {
            return convertXLSXToColumnLists(file);
        }
        else if(fileName.endsWith(".xls")){
            return convertXLSToColumnLists(file);
        }else {
            System.err.println("Nieobsługiwany format pliku: " + fileName);
            return new ArrayList<>();
        }
    }

    // **🔹 Obsługa CSV (wykrywa separator i kodowanie)**
    private static List<List<String>> convertCSVToColumnLists(File file) {
        List<List<String>> columnLists = new ArrayList<>();
        char separator = detectSeparator(file);

        // Możliwe kodowania do testowania, dodajemy `Charset.defaultCharset()`
        Charset[] charsetsToTry = {
                Charset.defaultCharset(), // Systemowe kodowanie użytkownika
                StandardCharsets.UTF_8,
                Charset.forName("Windows-1250"),
                Charset.forName("ISO-8859-2")

        };

        for (Charset charset : charsetsToTry) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset))) {
                CSVReader reader = new CSVReaderBuilder(br)
                        .withCSVParser(new CSVParserBuilder().withSeparator(separator).build())
                        .build();

                List<String[]> rows = reader.readAll();
                if (rows.isEmpty()) {
                    System.out.println("Plik CSV jest pusty!");
                    return columnLists;
                }

                System.out.println("📌 Wykryte kodowanie: " + charset.displayName());
            //    System.out.println("📌 Nagłówki CSV: " + Arrays.toString(rows.get(0)));

                int numColumns = rows.get(0).length;
                for (int i = 0; i < numColumns; i++) {
                    columnLists.add(new ArrayList<>());
                }

                for (String[] row : rows) {
                    for (int i = 0; i < row.length; i++) {
                        if (i < columnLists.size()) {
                            columnLists.get(i).add(row[i]);
                        }
                    }
                }
                return columnLists; // Jeśli nie było błędów, zwracamy listę
            } catch (IOException | CsvException e) {
                System.err.println("Błąd odczytu pliku CSV w kodowaniu " + charset.displayName() + ": " + e.getMessage());
            }
        }

        System.err.println("❌ Nie udało się odczytać pliku CSV w żadnym z kodowań.");
        return columnLists;
    }


    // **🔹 Obsługa plików XLSX (Excel)**
    private static List<List<String>> convertXLSXToColumnLists(File file) {
        List<List<String>> columnLists = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0); // Pobieramy pierwszą zakładkę Excela
            int numColumns = sheet.getRow(0).getPhysicalNumberOfCells();

            for (int i = 0; i < numColumns; i++) {
                columnLists.add(new ArrayList<>());
            }

            for (Row row : sheet) {
                for (int i = 0; i < numColumns; i++) {
                    Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    columnLists.get(i).add(cell.toString().trim()); // Konwertujemy komórkę do String
                }
            }

            System.out.println("📌 Odczytano plik Excel, liczba kolumn: " + columnLists.size());
        } catch (IOException e) {
            System.err.println("Błąd odczytu pliku XLSX: " + e.getMessage());
        }

        return columnLists;
    }
    private static List<List<String>> convertXLSToColumnLists(File file) {
        List<List<String>> columnLists = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new HSSFWorkbook(fis)) {  // Używamy HSSFWorkbook dla plików XLS

            Sheet sheet = workbook.getSheetAt(0); // Pobieramy pierwszą zakładkę Excela
            int numColumns = sheet.getRow(0).getPhysicalNumberOfCells();

            // Tworzymy listy dla każdej kolumny
            for (int i = 0; i < numColumns; i++) {
                columnLists.add(new ArrayList<>());
            }

            // Odczytujemy wiersze i zapisujemy wartości do list kolumnowych
            for (Row row : sheet) {
                for (int i = 0; i < numColumns; i++) {
                    Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    columnLists.get(i).add(cell.toString().trim()); // Konwersja do String i usunięcie spacji
                }
            }

            System.out.println("📌 Odczytano plik Excel 97-2003, liczba kolumn: " + columnLists.size());

        } catch (IOException e) {
            System.err.println("Błąd odczytu pliku XLS: " + e.getMessage());
        }

        return columnLists;
    }

    // **🔹 Automatyczne wykrywanie separatora CSV (`;` lub `,`)**
    private static char detectSeparator(File file) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String firstLine = br.readLine();
            if (firstLine != null) {
                int commaCount = firstLine.split(",").length - 1;
                int semicolonCount = firstLine.split(";").length - 1;

                if (semicolonCount > commaCount) {
                    System.out.println("📌 Wykryty separator: średnik `;`");
                    return ';';
                } else {
                    System.out.println("📌 Wykryty separator: przecinek `,`");
                    return ',';
                }
            }
        } catch (IOException e) {
            System.err.println("Błąd podczas wykrywania separatora: " + e.getMessage());
        }
        return ','; // Domyślnie przecinek
    }

    public XWPFDocument readWordFile(InputStream docxStream, Integer document_pages, List<List<String>> columnData, Integer columnMaxSize) {
        try (XWPFDocument document = new XWPFDocument(docxStream)) {

            // **Tworzymy nowy dokument**
            XWPFDocument newDocument = new XWPFDocument();
            int spacingCount = (int) spaceSelector.getSelectedItem(); // 🔹 liczba spacji z GUI
         //   String spacing = " ".repeat(spacingCount); //  generujemy odstęp
           // String spacing = new String(new char[spacingCount]).replace('\0', ' ');
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < spacingCount; i++) {
                sb.append(" ");
            }
            String spacing = sb.toString();
//            CTSectPr sectPr = newDocument.getDocument().getBody().isSetSectPr() ?
//                    newDocument.getDocument().getBody().getSectPr() :
//                    newDocument.getDocument().getBody().addNewSectPr();
//
//// Ustawienie strony na poziomą
//            CTPageSz pageSize = sectPr.isSetPgSz() ? sectPr.getPgSz() : sectPr.addNewPgSz();
//            pageSize.setW(BigInteger.valueOf(16840)); // Szerokość strony w TWIP
//            pageSize.setH(BigInteger.valueOf(11907)); // Wysokość strony w TWIP
//            pageSize.setOrient(STPageOrientation.LANDSCAPE);
//
//// Ustawienie marginesów
//            CTPageMar pageMar = sectPr.isSetPgMar() ? sectPr.getPgMar() : sectPr.addNewPgMar();
//            pageMar.setTop(BigInteger.valueOf(567));  // 1 cm
//            pageMar.setBottom(BigInteger.valueOf(567));  // 1 cm
//            pageMar.setLeft(BigInteger.valueOf(850));  // 1.5 cm
//            pageMar.setRight(BigInteger.valueOf(850));  // 1.5 cm


            boolean foundSzafarz = false;
            int occurrenceCount = 0;
            int rowIndex = 1; // Pomijamy nagłówek listy (pierwszy wiersz)

            // **Znajdujemy właściwe kolumny**
            List<String> nameList = null, fatherList = null, motherList = null, bierzmowanieNameList = null,
                    birthAndPlaceList = null, chrzestDateandPlaceList = null, witnessList = null,placeList=null,mainmanList=null,parafiaList=null,adresParafiiList=null,kodIMiejsceList=null,surnameList=null, yearList=null;
            System.out.println(columnData.get(0).get(0));
            for (List<String> column : columnData) {
                if (column.isEmpty()) continue; // Pomijamy puste listy
                String header = column.get(0).trim();

                if ("Imię".equalsIgnoreCase(header)) nameList = column;
                if ("Nazwisko".equalsIgnoreCase(header)) surnameList = column;
                if ("Rok bierzmowania".equalsIgnoreCase(header)) yearList = column;
                if ("Imię ojca".equalsIgnoreCase(header)) fatherList = column;
                if ("Imię matki".equalsIgnoreCase(header)) motherList = column;
                if ("Imię z bierzmowania".equalsIgnoreCase(header)) bierzmowanieNameList = column;
                if ("Data i miejsce urodzenia".equalsIgnoreCase(header)) birthAndPlaceList = column;
                if ("Data i miejsce chrztu".equalsIgnoreCase(header)) chrzestDateandPlaceList = column;
                if ("Świadek bierzmowania".equalsIgnoreCase(header)) witnessList = column;
                if ("Miejsce i data bierzmowania".equalsIgnoreCase(header)) placeList = column;
                if ("Szafarz bierzmowania".equalsIgnoreCase(header)) mainmanList = column;
                if ("Nazwa parafii".equalsIgnoreCase(header)) parafiaList = column;
                if ("Adres parafii".equalsIgnoreCase(header)) adresParafiiList = column;
                if("Kod pocztowy i miejscowość parafii".equalsIgnoreCase(header)) kodIMiejsceList = column;

            }

            if (nameList == null || fatherList == null || motherList == null || bierzmowanieNameList == null ||
                    birthAndPlaceList == null || chrzestDateandPlaceList == null || witnessList == null || placeList == null || mainmanList == null || surnameList==null || yearList==null) {
                System.err.println("Błąd: Nie znaleziono wymaganych kolumn.");
                return null;
            }
            System.out.println(parafiaList.size());
            for (int i = 0; i < document_pages; i++) {
                for (XWPFParagraph paragraph : document.getParagraphs()) {
                    String originalText = paragraph.getText().trim(); // Pobieramy oryginalny tekst paragrafu

                    System.out.println("Paragraph: " + originalText);
                    if (originalText.startsWith("Parafia rzymskokatolicka św. Piotra i Pawła")) {
                        if (parafiaList != null && parafiaList.size() > 1) {
                            originalText = parafiaList.get(1); // Zamiana na nową nazwę parafii
                        } else {
                            originalText = ""; // Jeśli brak danych, wpisujemy domyślną wartość

                        }
                        originalText += spacing + "L.p.";
                    }

                    // **Obsługa "Szafarz bierzmowania" i "Parafia..."**
                    if (originalText.equalsIgnoreCase("Szafarz bierzmowania:")) foundSzafarz = true;

                    if (foundSzafarz  && originalText.isEmpty()) continue;//

                    if (foundSzafarz && originalText.startsWith(parafiaList.get(1))) {
                        occurrenceCount++;

                        if (occurrenceCount % 2 != 0) {
                            newDocument.createParagraph().createRun().setText(""); // Dodajemy pustą linię przed "Parafią"
                        }

                        XWPFParagraph newParagraph = newDocument.createParagraph();
                        newParagraph.setAlignment(ParagraphAlignment.LEFT);
                        XWPFRun newRun = newParagraph.createRun();
                        newRun.setText(originalText);
                        newRun.setFontFamily("Times New Roman");
                        newRun.setFontSize(12);

                        foundSzafarz = false;
                        continue;
                    }
                    if(originalText.startsWith("ul. Ks.Hm. M.Luzara 1,")) {
                        originalText = "";
                        if (adresParafiiList != null && adresParafiiList.size() > 1) {
                            originalText = adresParafiiList.get(1);
                        }else{
                            originalText="";
                        }
                    }
                    if(originalText.startsWith("32-540, Trzebinia.")){
                        originalText="";
                        if (kodIMiejsceList != null && kodIMiejsceList.size() > 1) {
                            originalText = kodIMiejsceList.get(1);
                        }else{
                            originalText="";
                        }
                    }
//                    if(originalText.startsWith("Parafia rzymskokatolicka św. Piotra i Pawła Apostołów")){
//                        originalText="";
//                        originalText=parafiaList.get(1);
//                    }

                    // **Tworzymy nowy paragraf**
                    XWPFParagraph newParagraph = newDocument.createParagraph();
                    newParagraph.setAlignment(paragraph.getAlignment());
                    XWPFRun newRun = newParagraph.createRun();
                    newRun.setFontFamily("Times New Roman");
                    newRun.setFontSize(12);

                    boolean isNameField = originalText.startsWith("Imię i nazwisko:");
                    boolean isParentsField = originalText.startsWith("Imiona rodziców:");
                    boolean isBierzmowanieNameField = originalText.startsWith("Imię z bierzmowania:");
                    boolean isBirthAndPlaceField = originalText.startsWith("Data i miejsce urodzenia:");
                    boolean isChrzestField = originalText.startsWith("Data i miejsce chrztu (adres):");
                    boolean isWitnessField = originalText.startsWith("Świadek bierzmowania:");
                    boolean isplaceField = originalText.startsWith("Miejsce i data bierzmowania:");
                    boolean isManField = originalText.startsWith("Szafarz bierzmowania:");
                    boolean isSakramentField = originalText.startsWith("SAKRAMENT BIERZMOWANIA");
                  //  boolean isParafiaField = originalText.startsWith("Parafia rzymskokatolicka św. Piotra i Pawła Apostołów");
                    if (isBierzmowanieNameField || isSakramentField) {
                        newRun.setBold(true); // 🔹 Pogrubiamy tekst
                    }

                    // **Ustawiamy zmodyfikowany tekst**
                    newRun.setText(originalText + " ");

                    // **Uzupełniamy dane**
                    if (isNameField && rowIndex < columnMaxSize) {
                        String name = (rowIndex < nameList.size()) ? nameList.get(rowIndex) : "_";
                        String surname = (rowIndex < surnameList.size()) ? surnameList.get(rowIndex) : "_";
                        newRun.setText(name+" "+surname);
                    }
                    if (isParentsField && rowIndex < columnMaxSize) {
                        String father = (rowIndex < fatherList.size()) ? fatherList.get(rowIndex) : "_";
                        String mother = (rowIndex < motherList.size()) ? motherList.get(rowIndex) : "_";
                        newRun.setText(father + ", " + mother);
                    }
                    if (isBierzmowanieNameField && rowIndex < columnMaxSize) {
                        String name = (rowIndex < bierzmowanieNameList.size()) ? bierzmowanieNameList.get(rowIndex) : "_";
                        newRun.setBold(true);
                        XWPFRun coloredRun = newParagraph.createRun();
                        coloredRun.setBold(true); // Jeśli chcesz, aby tekst pozostał pogrubiony
                        coloredRun.setColor("FF0000"); // Ustawiamy kolor na czerwony (dla jasnego czerwonego)
                        coloredRun.setFontFamily("Times New Roman");
                        coloredRun.setFontSize(12);
                        coloredRun.setText(name);
                    }
                    if (isBirthAndPlaceField && rowIndex < columnMaxSize) {
                        String name = (rowIndex < birthAndPlaceList.size()) ? birthAndPlaceList.get(rowIndex) : "_";
                        newRun.setText(name);
                    }
                    if (isChrzestField && rowIndex < columnMaxSize) {
                        String name = (rowIndex < chrzestDateandPlaceList.size()) ? chrzestDateandPlaceList.get(rowIndex) : "_";
                        newRun.setText(name);
                    }
                    if (isWitnessField && rowIndex < columnMaxSize) {
                        String name = (rowIndex < witnessList.size()) ? witnessList.get(rowIndex) : "_";
                        newRun.setText(name);
                    }
                    if (isplaceField && rowIndex < columnMaxSize) {
                        String name = placeList.get(1);
                        newRun.setText(name);
                    }



                    if (originalText.startsWith("Szafarz bierzmowania:")) {
                        if (isManField && rowIndex < columnMaxSize) {
                            String name = mainmanList.get(1);
                            newRun.setText(name);
                        }
                        rowIndex++;
                    }


                }

                // **Dodajemy podział strony po każdej kopii**
//                if (i < document_pages - 1) {
//                    newDocument.createParagraph().setPageBreak(true);
//                }
            }

            // **🔄 NOWA PĘTLA – Przeszukiwanie i numeracja L.p.**
            int lp = 1;
            int index=1;
            for (XWPFParagraph paragraph : newDocument.getParagraphs()) {
                String originalText = paragraph.getText().trim();
                if (originalText.contains("L.p.") && index < columnMaxSize) {
                    String yearRaw = yearList.get(1).trim();
                    String yearFormatted;

                    try {
                        // Próbujemy sparsować do double i obciąć .0
                        double yearAsDouble = Double.parseDouble(yearRaw);
                        yearFormatted = String.valueOf((int) yearAsDouble);
                    } catch (NumberFormatException e) {
                        // Jeśli nie da się sparsować, traktujemy jako zwykły String
                        yearFormatted = yearRaw;
                    }

                    String modifiedText = originalText.replaceAll("L\\.p\\.*", "L.p. " + lp + "/" + yearFormatted);

                    // Czyszczenie runów i dodanie nowego tekstu
                    for (int i = paragraph.getRuns().size() - 1; i >= 0; i--) {
                        paragraph.removeRun(i);
                    }

                    XWPFRun newRun = paragraph.createRun();
                    newRun.setText(modifiedText);
                    newRun.setFontFamily("Times New Roman");
                    newRun.setFontSize(12);

                    lp++;
                    index++;
                }
            }

            return newDocument;

        } catch (IOException e) {
            System.err.println("Błąd odczytu pliku Word: " + e.getMessage());
            return null;
        }
    }
    private void showInstructionDialog() {
      //  JEditorPane editorPane = new JEditorPane("text/html", instructionText);
      //  editorPane.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(instructionPane);
        scrollPane.setPreferredSize(new Dimension(780, 520)); // szersze i wyższe okno

        JOptionPane.showMessageDialog(frame, scrollPane, "Instrukcja", JOptionPane.INFORMATION_MESSAGE);
    }


    // **🔹 Obsługa licencji**
    private static String getMotherboardSerial() {
        try {
            ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "wmic baseboard get serialnumber");
            builder.redirectErrorStream(true); // Przekierowanie błędów na standardowe wyjście
            Process process = builder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            List<String> outputLines = new ArrayList<>();
            String line;

            while ((line = reader.readLine()) != null) {
                outputLines.add(line.trim()); // Usuwamy białe znaki
            }

            System.out.println("📌 Pełny wynik polecenia WMIC: " + outputLines); // Debugowanie

            process.waitFor(); // Czekamy na zakończenie procesu

            // Filtrujemy tylko niepuste linie i ignorujemy pierwszą (nagłówek)
            List<String> serialNumbers = outputLines.stream()
                    .skip(1)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());

            if (!serialNumbers.isEmpty()) {
                String serial = serialNumbers.get(0); // Pobieramy pierwszy poprawny numer seryjny
                System.out.println("📌 Pobrany numer seryjny płyty głównej: " + serial);
                return serial;
            } else {
                System.err.println("❌ Błąd: Brak numeru seryjnego w wyjściu!");
                return "UNKNOWN";
            }

        } catch (Exception e) {
            System.err.println("❌ Błąd pobierania numeru seryjnego: " + e.getMessage());
            return "UNKNOWN";
        }
    }






    private static String readLicenseFile() {
        try {
            Path path = Paths.get(LICENSE_FILE);

            if (!Files.exists(path)) {
                System.err.println("❌ Plik licencyjny nie istnieje.");
                return null;
            }

            String encryptedContent = new String(Files.readAllBytes(path), StandardCharsets.UTF_8).trim();
            if (encryptedContent.isEmpty()) return null;

            String decryptedSerial = decryptAES(encryptedContent); // 🔹 Odszyfrowujemy
            System.out.println("📌 Odczytana i odszyfrowana zawartość pliku licencyjnego: " + decryptedSerial);

            return decryptedSerial;
        } catch (Exception e) {
            System.err.println("❌ Błąd odczytu pliku licencyjnego: " + e.getMessage());
            return null;
        }
    }


    private static void writeLicenseFile(String serial) {
        try {
            Path path = Paths.get(LICENSE_FILE);

            if (!Files.exists(path)) {
                Files.createFile(path);
            }

            if (serial.equals("UNKNOWN") || serial.isEmpty()) {
                System.err.println("❌ Nie można zapisać pustego numeru seryjnego!");
                return;
            }

            String encryptedSerial = encryptAES(serial); // 🔹 Szyfrujemy numer seryjny
            Files.write(path, encryptedSerial.getBytes(StandardCharsets.UTF_8), StandardOpenOption.TRUNCATE_EXISTING);
            System.out.println("✅ Plik licencyjny zapisany poprawnie!");

            // Sprawdzenie zawartości po zapisie
            System.out.println("📌 Po zapisie w pliku znajduje się (zaszyfrowane): " + encryptedSerial);

        } catch (Exception e) {
            System.err.println("❌ Błąd zapisu pliku licencyjnego: " + e.getMessage());
        }
    }

    private static final String AES_KEY = "1234567890123456"; // 16 znaków (128-bit)

    private static String encryptAES(String data) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec keySpec = new SecretKeySpec(AES_KEY.getBytes(), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encryptedData = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    private static String decryptAES(String encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec keySpec = new SecretKeySpec(AES_KEY.getBytes(), "AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] decryptedData = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(decryptedData);
    }

}
