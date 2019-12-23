
import javafx.application.Platform;
import javafx.beans.binding.Binding;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.beans.binding.Bindings;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Formatter;
import java.util.Optional;

public class TicTacticsGame extends BorderPane {
	private StringProperty xPlayer = new SimpleStringProperty("X player");
	private StringProperty oPlayer = new SimpleStringProperty("O player");
	private StringProperty currentlyPlaying = new SimpleStringProperty("X player");
	private IntegerProperty xScore = new SimpleIntegerProperty(0);
	private IntegerProperty oScore = new SimpleIntegerProperty(0);
	private IntegerProperty tieScore = new SimpleIntegerProperty(0);
	private Player currentPlayer = Player.X;
	private Player mySymbol = Player.X;
	private TicTacticsBoard board;
	private StringProperty myName = new SimpleStringProperty();
	private StringProperty opponentName = new SimpleStringProperty();
	private StringProperty serverName = new SimpleStringProperty("localhost");
	private static final int SERVER_PORT = 12367;
	private Socket socket;
	public BooleanProperty isGameGoing = new SimpleBooleanProperty(false);

	TicTacticsGame(Stage stage) {
		try {
			myName.setValue(NameGenerator.generateName());
		} catch (IOException e) {
			myName.setValue("John Doe");
		}
		board = new TicTacticsBoard(this);
		HBox layout = new HBox();
		MenuBar mainMenu = generateMenuBar(stage);
		HBox.setHgrow(mainMenu, Priority.ALWAYS);
		Text playingText = new Text();
		playingText.textProperty().bind(Bindings.when(isGameGoing)
				.then(Bindings.concat(myName).concat(" vs ").concat(opponentName))
				.otherwise(myName));
		layout.getChildren().addAll(mainMenu, playingText);
		setTop(layout);
		setCenter(board);
	}

	private MenuBar generateMenuBar(Stage stage) {
		MenuItem newGameItem = new MenuItem("_New Game");
		newGameItem.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.SHORTCUT_DOWN));
		newGameItem.setOnAction(e -> newGame());

		MenuItem changeNameItem = new MenuItem("Change name");
		changeNameItem.setAccelerator(new KeyCodeCombination(KeyCode.C, KeyCombination.SHORTCUT_DOWN));
		changeNameItem.setOnAction(e -> changeName());

		MenuItem exitItem = new MenuItem("E_xit");
		exitItem.setOnAction(e -> Platform.exit());

		Menu gameMenu = new Menu("_Game");
		gameMenu.getItems().addAll(newGameItem, changeNameItem, exitItem);

		MenuItem changeServerItem = new MenuItem("Change server");
		changeServerItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN));
		changeServerItem.setOnAction(e -> changeServer());

		Menu serverMenu = new Menu("_Server");
		serverMenu.getItems().add(changeServerItem);

		MenuItem howToItem = new MenuItem("How to _play");
		howToItem.setAccelerator(new KeyCodeCombination(KeyCode.H, KeyCombination.SHORTCUT_DOWN));
		howToItem.setOnAction(e -> showGameRules());

		Menu helpMenu = new Menu("_Help");
		helpMenu.getItems().add(howToItem);

		activateMnemonics(
			gameMenu,
			newGameItem,
			changeNameItem,
			exitItem,
			serverMenu,
			changeServerItem,
			helpMenu,
			howToItem
		);

		MenuBar menuBar = new MenuBar();
		menuBar.getMenus().addAll(gameMenu, serverMenu, helpMenu);
		return menuBar;
	}

	public void endPrompt(String message) {
		if (message.equals("It's a tie!")) {
			tieScore.setValue(tieScore.getValue() + 1);
		}
		
		Stage stage = new Stage();
		Label label = new Label(message);
		label.setStyle("-fx-font-weight: bold;");

		final int BUTTON_WIDTH = 80;

		Button reset = new Button("New Round");
		reset.setMinWidth(BUTTON_WIDTH);
		reset.setOnAction(e -> {
			stage.close();
			newRound();
		});
		reset.setDefaultButton(true);

		Button quit = new Button("Quit");
		quit.setMinWidth(BUTTON_WIDTH);
		quit.setOnAction(e -> Platform.exit());

		HBox gameLayout = new HBox(5);
		gameLayout.getChildren().addAll(reset, quit);
		gameLayout.setAlignment(Pos.CENTER);

		VBox layout = new VBox(5);
		layout.getChildren().addAll(label, gameLayout);
		layout.setAlignment(Pos.CENTER);

		stage.setScene(new Scene(layout, 175 + new Text(message).getLayoutBounds().getWidth(), 75));
		stage.sizeToScene();
		stage.setTitle("Game Over");
		board.disable();
		stage.show();
	}

	public boolean isMyTurn() {
		return currentPlayer == mySymbol;
	}

	public Player getCurrentPlayer() {
		return currentPlayer;
	}

	private void activateMnemonics(MenuItem... items) {
		for (MenuItem item : items) {
			item.setMnemonicParsing(true);
		}
	}

	private void newGame() {
		board.boardCounter = 0;
		currentPlayer = Player.X;
		currentlyPlaying.setValue(xPlayer.getValue());
		board.reset();

		connect();
	}

	private void newRound() {
		board.boardCounter = 0;
		board.reset();
	}

	public String checkWinner(String winner) {
		if (winner.equals("X")) {
			xScore.setValue(xScore.getValue() + 1);
			return xPlayer.getValue();
		} else {
			oScore.setValue(oScore.getValue() + 1);
			return oPlayer.getValue();
		}
	}

	public void changeName() {
		TextInputDialog dialog = new TextInputDialog(myName.getValue());
		dialog.setTitle("Change name");
		dialog.setHeaderText("Enter new name:");
		dialog.setContentText("Name:");

		Optional<String> result = dialog.showAndWait();

		result.ifPresent(name -> {
			myName.setValue(result.get());
		});
	}

	public void changeServer() {
		TextInputDialog dialog = new TextInputDialog(serverName.getValue());
		dialog.setTitle("Change server");
		dialog.setHeaderText("Enter new hostname:");
		dialog.setContentText("Hostname:");

		Optional<String> result = dialog.showAndWait();

		result.ifPresent(name -> {
			serverName.setValue(result.get());
		});
	}

	private int findIndex (Position[] posses, Position pos) {
		for (int i = 0; i < posses.length; i++) {
			if (posses[i].equals(pos)) {
				return i;
			}
		}
		return -1;
	}

	public void connect() {
		StringProperty statusText = new SimpleStringProperty();
		try {
			statusText.setValue("Connect to server " + this.serverName.getValue());
			Socket socket;
			socket = new Socket(serverName.getValue(), SERVER_PORT);
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

			String input;

			statusText.setValue("Searching for opponent...");
			input = reader.readLine();

			writer.write(myName.getValue()+"\n");
			writer.flush();
			input = reader.readLine();
			opponentName.setValue(input);

			input = reader.readLine();
			if (input.equals(MSG.FIRST_TURN)) {
				mySymbol = Player.X;
			} else {
				mySymbol = Player.O;
			}

			this.socket = socket;
			isGameGoing.set(true);

			Thread thread = new Thread(() -> {
				try {
					BufferedReader reader1 = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					while(true) {
						String input1 = reader1.readLine();
						String[] numbers = input1.split(" ");
						Position bigPos = Position.valueOf(numbers[0]), smallPos = Position.valueOf(numbers[1]);
						int bigInt = findIndex(Position.values(), bigPos), smallInt = findIndex(Position.values(), smallPos);

						Button button = board.board[bigInt].board[smallInt].button;
						Platform.runLater(() -> {
							button.setText(getCurrentPlayer().toString());
							button.setStyle(getCurrentPlayer().getStyle());
							board.board[bigInt].evaluateState();
							endTurn();
							board().disable();
							board().enable(smallPos);
						});
					}
				} catch (IOException e) {
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setTitle("Error");
					alert.setHeaderText("Exception on turn reading:");
					alert.setContentText(e.getMessage());
					alert.showAndWait();
				}
			});
			thread.start();
		}
		catch (Exception e) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Exception on connection:");
			alert.setContentText(e.getMessage());
			alert.showAndWait();
		}
	}

	public void showGameRules() {
		Stage stage = new Stage();
		TextArea area = new TextArea();
		area.setEditable(false);
		area.setText("1. Each turn, you mark one of the small squares."
			+ "\n2. When you get three in a row on a small board, you’ve won that board."
			+ "\n3. To win the game, you need to win three small boards in a row."
			+ "\n4. Tied boards may count for either X or O."
			+ "\n\nYou don't get to pick the board you play on:"
			+ "\n*Whichever square your opponent picks corresponds to the board you must play in"
			+ "\n*If your opponent sends you to an already won board, you may play anywhere");
		stage.setScene(new Scene(area));
		stage.setTitle("Rules");
		stage.show();
	}

	private void closeConnection() {
		isGameGoing.setValue(false);
		try {
			socket.close();
		} catch (IOException e) {}
	}

	public void sendTurn(Position bigPos, Position smallPos) {
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
			writer.write(bigPos.toString() + " " + smallPos.toString() + "\n");
			writer.flush();
		} catch (IOException e) {
			closeConnection();
		}
	}

	public void endTurn() {
		if (currentPlayer == Player.X) {
			currentPlayer = Player.O;
			currentlyPlaying.setValue(oPlayer.getValue());
		} else {
			currentPlayer = Player.X;
			currentlyPlaying.setValue(xPlayer.getValue());
		}
	}

	public void evaluateBoard() {
		board.evaluateState();
	}

	public TicTacticsBoard board() {
		return board;
	}
}

class SearchWindow extends Thread {
	private StringProperty statusText;

	SearchWindow(StringProperty statusText) {
		this.statusText = statusText;
	}

	@Override
	public void run() {
		Stage dialog = new Stage();
		dialog.initModality(Modality.APPLICATION_MODAL);

		ProgressBar progressBar = new ProgressBar();

		StringProperty statusText = new SimpleStringProperty();
		Text text = new Text();
		text.textProperty().bind(Bindings.concat(statusText));

		BorderPane root = new BorderPane();
		root.setPadding(new Insets(10));
		root.setCenter(progressBar);

		Scene scene = new Scene(root, 400, 300);

		dialog.setScene(scene);

		dialog.showAndWait();
	}
}
