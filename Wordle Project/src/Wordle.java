import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


public class Wordle implements ActionListener, KeyListener {
    private JButton[] buttons;
    private JLabel[][] labels;
    private int row;
    private int col;
    private String word;
    private String[] dictionary;
    private String[] guesses;
    private JFrame frame;

    public Wordle() {
        initializeUI();
        loadDictionary();
        chooseRandomWord();
    }

    private void initializeUI() {
        frame = new JFrame("dictionary");
        frame.setLayout(new GridLayout(9, 1));

        buttons = new JButton[28];
        String alphabet = "QWERTYUIOPASDFGHJKLZXCVBNM";
        for (int i = 0; i < 26; i++) {
            buttons[i] = new JButton(String.valueOf(alphabet.charAt(i)));
            buttons[i].addActionListener(this);
            buttons[i].setBackground(Color.WHITE);
            buttons[i].setOpaque(true);
        }
        buttons[26] = new JButton("Enter");
        buttons[26].addActionListener(this);
        buttons[26].setBackground(Color.WHITE);
        buttons[26].setOpaque(true);
        buttons[27] = new JButton("Backspace");
        buttons[27].addActionListener(this);
        buttons[27].setBackground(Color.WHITE);
        buttons[27].setOpaque(true);

        JPanel[] panels = new JPanel[3];
        for (int i = 0; i < 3; i++) {
            panels[i] = new JPanel();
        }
        panels[0].setLayout(new GridLayout(1, 10));
        panels[1].setLayout(new GridLayout(1, 8));
        panels[2].setLayout(new GridLayout(1, 10));

        for (int i = 0; i < 19; i++) {
            if (i < 10)
                panels[0].add(buttons[i]);
            else
                panels[1].add(buttons[i]);
        }

        panels[2].add(buttons[26]);
        for (int i = 0; i < 7; i++) {
            panels[2].add(buttons[i + 19]);
        }
        panels[2].add(buttons[27]);

        labels = new JLabel[6][5];
        for (int r = 0; r < 6; r++) {
            for (int c = 0; c < 5; c++) {
                labels[r][c] = new JLabel("");
                labels[r][c].setOpaque(true);
                labels[r][c].setBackground(Color.WHITE);
                labels[r][c].setForeground(Color.black);
                labels[r][c].setHorizontalAlignment(SwingConstants.CENTER);
                labels[r][c].setBorder(BorderFactory.createLineBorder(Color.BLACK));
            }
        }

        JPanel[] lPanels = new JPanel[6];
        for (int i = 0; i < 6; i++) {
            lPanels[i] = new JPanel();
            lPanels[i].setLayout(new GridLayout(1, 5));
        }

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 5; j++)
                lPanels[i].add(labels[i][j]);
        }

        for (int i = 0; i < 6; i++)
            frame.add(lPanels[i]);

        for (int i = 0; i < 3; i++)
            frame.add(panels[i]);

        frame.setSize(800, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.addKeyListener(this);
        frame.setFocusable(true);
        frame.requestFocusInWindow();
    }

    private void loadDictionary() {
        dictionary = new String[2315];
        guesses = new String[12972];

        try {
            Scanner scan = new Scanner(new File("dictionary"));
            System.out.println();
            for (int i = 0; i < 2315; i++) {
                if (scan.hasNextLine()) {
                    dictionary[i] = scan.nextLine();
                }
            }
            scan = new Scanner(new File("guesses"));
            for (int i = 0; i < 12972; i++) {
                if (scan.hasNextLine()) {
                    guesses[i] = scan.nextLine();
                }
            }
            scan.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void chooseRandomWord() {
        int random = (int) (Math.random() * 2315);
        word = dictionary[random];
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton source = (JButton) e.getSource();

        if (source.equals(buttons[26])) {
            checkGuess();
        } else if (source.equals(buttons[27])) {
            backspace();
        } else {
            letterButton(source);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            checkGuess();
        } else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
            backspace();
        } else {
            char keyChar = Character.toUpperCase(e.getKeyChar());
            if (keyChar >= 'A' && keyChar <= 'Z') {
                letterButton(new JButton(String.valueOf(keyChar)));
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        //not implemented
    }

    @Override
    public void keyReleased(KeyEvent e) {
        //not implemented
    }

    private void checkGuess() {
        String guess = "";
        for (int c = 0; c < 5; c++) {
            guess += labels[row][c].getText();
        }
        guess = guess.toLowerCase();

        boolean found = false;
        int index = 0;
        while (index < guesses.length && !found ) {
            found = guess.equals(guesses[index]);
            index++;
        }

        if (found) {
            for (int c = 0; c < 5; c++) {
                if (word.charAt(c) == guess.charAt(c)) {
                    labels[row][c].setBackground(Color.GREEN);
                } else if (word.indexOf(guess.charAt(c)) != -1) {
                    labels[row][c].setBackground(Color.YELLOW);
                } else {
                    labels[row][c].setBackground(Color.LIGHT_GRAY);
                }
                for (int i = 0; i < buttons.length-2; i++) {
                    if (buttons[i].getText().equals(labels[row][c].getText()) && buttons[i].getBackground() != Color.GREEN) {
                        buttons[i].setBackground(labels[row][c].getBackground());
                    }
                }
            }
            row++;
            col = 0;

        } else {
            JOptionPane.showMessageDialog(frame, "Your guess is not a valid word.");
        }

        if (word.equals(guess)) {
            JOptionPane.showMessageDialog(frame, "You won!");
        } else if (row == 6) {
            JOptionPane.showMessageDialog(frame, "You lost. The word was " + word.toUpperCase() + ".");
        }
    }

    private void backspace() {
        if (col > 0) {
            col--;
            labels[row][col].setText("");
        }
    }

    private void letterButton(JButton button) {
        if (col < 5) {
            labels[row][col].setText(button.getText());
            labels[row][col].setFont(new Font("Arial", Font.BOLD, 40));
            col++;
        }
    }

    public static void main(String[] args) {
        new Wordle();
    }

}