import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;



public class GUIsearchEngine extends JFrame implements ActionListener {
    private JTextField searchField;
    private JTextArea resultsArea;
    private JButton searchButton;

    public GUIsearchEngine(){
        setTitle("Motor Cautare");
        setSize(600,400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout());

        searchField = new JTextField(30);
        searchButton = new JButton("Search");

        searchButton.addActionListener(this);

        topPanel.add(new JLabel("Type..."));
        topPanel.add(searchField);
        topPanel.add(searchButton);

        resultsArea = new JTextArea();
        resultsArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultsArea);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }
    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == searchButton){
            String searchTerm = searchField.getText();
            resultsArea.setText("Searching results for: " + searchTerm + "..\n");
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
            StringBuilder output = new StringBuilder();

            while((line = reader.readLine()) != null){
                output.append(line).append("\n");
            }
            int exitCode = p.waitFor();

            if (exitCode == 0){
                resultsArea.setText(output.toString());
            } else {
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                StringBuilder errorOutput = new StringBuilder();
                while ((line = errorReader.readLine()) != null) {
                    errorOutput.append(line).append("\n");
                }
                resultsArea.setText("Error: Python Script");
            }
        } catch (Exception ex){
            resultsArea.setText("Eroare la rularea scriptului");
            ex.printStackTrace();
        }
    }
    public static void main(String[] args){
        SwingUtilities.invokeLater(()-> new GUIsearchEngine());
    }
}
