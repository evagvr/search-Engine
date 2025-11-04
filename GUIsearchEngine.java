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
    private JLabel loadingLabel;

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

        loadingLabel = new JLabel("<html><b><font color='gray'>Processing the search... Please wait.</font></b></html>");
        loadingLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        loadingLabel.setVisible(false);

        JScrollPane scrollPane = new JScrollPane(resultsArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == searchButton){
            String searchTerm = searchField.getText();
            
            searchButton.setEnabled(false);
            SearchTask task = new SearchTask(searchTerm);
            task.execute();
        }
    }

    private class SearchTask extends SwingWorker<Void, String> {
        private String searchTerm;

        public SearchTask(String searchTerm) {
            this.searchTerm = searchTerm;
            
            resultsArea.removeAll();
            resultsArea.add(loadingLabel);
            loadingLabel.setVisible(true);
            resultsArea.revalidate();
            resultsArea.repaint();
        }

        @Override
        protected Void doInBackground() throws Exception {
            String pythonPath = "python";
            String scriptPath = "searchengine.py";

            ProcessBuilder pb = new ProcessBuilder(pythonPath, scriptPath, searchTerm);
            Process p = pb.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    publish(line); 
                }
            }

            int exitCode = p.waitFor();

            if (exitCode != 0) {
                try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(p.getErrorStream()))) {
                    String line;
                    while ((line = errorReader.readLine()) != null) {
                        publish("ERROR_STREAM|" + line); // Marcăm mesajul ca eroare
                    }
                }
            }
            return null;
        }

        @Override
        protected void process(java.util.List<String> chunks) {
            if (loadingLabel.isVisible()) {
                resultsArea.removeAll();
                loadingLabel.setVisible(false);
            }
            
            for (String line : chunks) {
                if (line.startsWith("ERROR_STREAM|")) {
                    resultsArea.add(new JLabel("<html><font color='red'>Eroare Script Python:</font></html>"));
                    resultsArea.add(new JLabel(line.substring(13)));
                    continue;
                }
                
                String[] parts = line.split("\\|", 3); 
                
                if (parts.length == 3) {
                    String title = parts[0].trim();
                    String url = parts[1].trim();
                    String snippet = parts[2].trim();
                    JPanel resultItemPanel = new JPanel();

                    resultItemPanel.setLayout(new BoxLayout(resultItemPanel, BoxLayout.Y_AXIS));
                    resultItemPanel.setBackground(resultsArea.getBackground());
                    resultItemPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

                    hyperlinkLabel link = new hyperlinkLabel(title, url);
                    link.setAlignmentX(Component.LEFT_ALIGNMENT);

                    JLabel snippetLabel = new JLabel(snippet);
                    snippetLabel.setForeground(Color.BLACK);
                    snippetLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                    
                    resultItemPanel.add(link);
                    resultItemPanel.add(snippetLabel);

                    resultsArea.add(resultItemPanel);
                    resultsArea.add(Box.createVerticalStrut(10));
                } else if (line.trim().length() > 0) {
                    JLabel message = new JLabel(line);
                    message.setAlignmentX(Component.LEFT_ALIGNMENT);
                    resultsArea.add(message);
                }
            }
            resultsArea.revalidate();
            resultsArea.repaint();
        }

        @Override
        protected void done() {
            loadingLabel.setVisible(false);
            resultsArea.revalidate();
            resultsArea.repaint();
            searchButton.setEnabled(true); 
        }
    }
    public static void main(String[] args){
        SwingUtilities.invokeLater(()-> new GUIsearchEngine());
    }
}
