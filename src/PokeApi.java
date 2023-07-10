import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import org.json.JSONObject;
import javax.imageio.ImageIO;

public class PokeApi extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField searchField;
    private JLabel nameLabel;
    private JLabel typeLabel;
    private JLabel imageLabel;
    @SuppressWarnings("unused")
	private JLabel nameLabelFooter;

    public PokeApi() {
        setTitle("Pokemon API");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(400, 250));
        setLayout(new FlowLayout());

        JLabel instructionLabel = new JLabel("Digite o nome do Pokémon:");
        add(instructionLabel);

        searchField = new JTextField(20);
        add(searchField);

        JButton searchButton = new JButton("Pesquisar");
        add(searchButton);

        nameLabel = new JLabel();
        add(nameLabel);

        typeLabel = new JLabel();
        add(typeLabel);

        imageLabel = new JLabel();
        add(imageLabel);


        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String pokemonName = searchField.getText().toLowerCase();
                if (pokemonName.isEmpty()) {
                    nameLabel.setText("Digite o nome de um Pokémon válido!");
                    typeLabel.setText("");
                    imageLabel.setIcon(null);
                    return;
                }
                try {
                    URL url = new URL("https://pokeapi.co/api/v2/pokemon/" + pokemonName);
                    JSONObject json = new JSONObject(HttpRequestHelper.sendGet(url));
                    String name = json.getString("name");
                    String type = json.getJSONArray("types").getJSONObject(0).getJSONObject("type").getString("name");
                    String imageUrl = json.getJSONObject("sprites").getString("front_default");

                    nameLabel.setText("Nome: " + name);
                    typeLabel.setText("Tipo: " + capitalizeFirstLetter(type));
                    imageLabel.setIcon(new ImageIcon(ImageIO.read(new URL(imageUrl))));
                } catch (Exception ex) {
                    nameLabel.setText("Erro: Pokémon não encontrado");
                    typeLabel.setText("");
                    imageLabel.setIcon(null);
                }
            }
        });

        pack();
        setLocationRelativeTo(null);
    }

    private String capitalizeFirstLetter(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                PokeApi app = new PokeApi();
                app.setVisible(true);
            }
        });
    }
}

class HttpRequestHelper {
    public static String sendGet(URL url) throws IOException {
        StringBuilder response = new StringBuilder();
        java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        int responseCode = connection.getResponseCode();

        if (responseCode == java.net.HttpURLConnection.HTTP_OK) {
            java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(connection.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
        }

        return response.toString();
    }
}
