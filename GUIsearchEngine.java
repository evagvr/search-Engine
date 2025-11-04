import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;




public class GUIsearchEngine extends JFrame implements ActionListener {
    private JTextField searchField;
    private JPanel resultsArea;
    private JButton searchButton;

    public GUIsearchEngine(){
        setTitle("Search Engine");
        setSize(600,400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(226, 201,158));
        topPanel.setLayout(new FlowLayout());

        searchField = new JTextField(30);
        searchButton = new JButton("Search");

        searchButton.setBackground(new Color(133, 39, 54));
        searchButton.setForeground(new Color(226, 201,158));
        searchButton.setFont(new Font("Arial", Font.BOLD, 15));
        searchButton.setBorder(null);

        searchButton.addActionListener(this);

        topPanel.add(new JLabel("Type..."));
        topPanel.add(searchField);
        topPanel.add(searchButton);

        resultsArea = new JPanel();
        resultsArea.setLayout(new BoxLayout(resultsArea, BoxLayout.Y_AXIS)); 
        resultsArea.setBackground(Color.WHITE);

        add(topPanel, BorderLayout.NORTH);
        add(resultsArea, BorderLayout.CENTER);

        setVisible(true);
    }
    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == searchButton){
            String searchTerm = searchField.getText();
            resultsArea.removeAll(); // Curăță rezultatele anterioare
            resultsArea.add(new JLabel("Searching results for: " + searchTerm + "...\n"));
            resultsArea.revalidate();
            resultsArea.repaint();
            callPythonScript(searchTerm);
        }
    }
    private void callPythonScript(String searchItem){
        try{
            String pythonPath = "python";
            String scriptPath = "searchengine.py";

            ProcessBuilder pb = new ProcessBuilder(pythonPath, scriptPath, searchItem);
            Process p = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            resultsArea.removeAll();

            while((line = reader.readLine()) != null){
                String[] parts = line.split("\\|", 2); 
                
                if (parts.length == 2) {
                    String title = parts[0].trim();
                    String url = parts[1].trim();

                    hyperlinkLabel link = new hyperlinkLabel(title, url);
                    resultsArea.add(link);
                    resultsArea.add(Box.createVerticalStrut(5));
                } else {
                    resultsArea.add(new JLabel(line));
                }
            }
            int exitCode = p.waitFor();

            if (exitCode == 0){
                resultsArea.revalidate(); 
                resultsArea.repaint();
            } else {
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                StringBuilder errorOutput = new StringBuilder();
                while ((line = errorReader.readLine()) != null) {
                    errorOutput.append(line).append("\n");
                }
                resultsArea.removeAll();
                resultsArea.add(new JLabel("<html><font color='red'>Eroare Script Python:</font></html>"));
                resultsArea.add(new JLabel(errorOutput.toString()));
                resultsArea.revalidate(); 
                resultsArea.repaint();
            }
        } catch (Exception ex){
            resultsArea.removeAll();
            resultsArea.add(new JLabel("<html><font color='red'>Eroare Java la rularea scriptului: </font></html>" + ex.getMessage()));
            ex.printStackTrace();
        }
    }
    public static void main(String[] args){
        SwingUtilities.invokeLater(()-> new GUIsearchEngine());
    }
}
