import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class TicTactics2 extends Application {
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) {
		stage.setTitle("Tic Tactics");
		Scene scene = new Scene(new TicTacticsGame(stage));
		scene.getStylesheets().add("tictactics.css");
		stage.setScene(scene);
		stage.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));
		stage.show();
	}
}