
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

import java.io.IOException;

public class TicTacToeSquare {
	public Button button = new Button();
	private final int SQUARE_LENGTH = 70;

	public Button getButton() {
		return button;
	}

	public void setButton(Button button) {
		this.button = button;
	}

	TicTacToeSquare(TicTacticsGame game, TicTacToeBoard board, Position bigPosition, Position smallPosition) {
		button.setMinSize(SQUARE_LENGTH, SQUARE_LENGTH);
		button.setOnAction(e -> {
			if (button.getText().isEmpty()) {
				if (game.isMyTurn()) {
					button.setText(game.getCurrentPlayer().toString());
					button.setStyle(game.getCurrentPlayer().getStyle());
					board.evaluateState();
					game.endTurn();
					game.sendTurn(bigPosition, smallPosition);
					game.board().disable();
					game.board().enable(smallPosition);
				} else {
					Alert alert = new Alert(Alert.AlertType.INFORMATION);
					alert.setTitle("Illegal action");
					alert.setHeaderText("Illegal:");
					alert.setContentText("It is turn of your opponent");
					alert.showAndWait();
				}
			}
		});
	}

	public TicTacToeSquare(Button button) {
		this.button = button;
	}

	public TicTacToeSquare() {
	}

	public Button button() {
		return button;
	}

	public boolean equivalentTo(TicTacToeSquare target) {
		return !button.getText().isEmpty() && button.getText().equals(target.button().getText());
	}

	public void reset() {
		button.setText("");
		button.setStyle("");
		button.setDisable(false);
	}
}