
import javafx.scene.layout.GridPane;

import java.io.IOException;

public class TicTacToeBoard extends GridPane {
	private final int NUMBER_OF_SQUARES = 9;
	public int boardCounter;
	public TicTacToeSquare[] board = new TicTacToeSquare[NUMBER_OF_SQUARES];
	private TicTacticsGame game;
	private boolean captured = false;
	private Winner winner = Winner.NONE;
	private boolean gameOver = false;

	TicTacToeBoard(TicTacticsGame game, Position position) {
		this.game = game;

		for (int i = 0; i < board.length; i++) {
			board[i] = new TicTacToeSquare(this.game, this, position, Position.values()[i]);
			add(board[i].button(), i / 3, i % 3);
		}
		setStyle("-fx-border-color: cadetblue; -fx-border-width: 2; -fx-border-radius: 5");
	}

	public TicTacToeBoard() {
	}

	public int getBoardCounter() {
		return boardCounter;
	}

	public void setBoardCounter(int boardCounter) {
		this.boardCounter = boardCounter;
	}

	public TicTacToeSquare[] getBoard() {
		return board;
	}

	public void setBoard(TicTacToeSquare[] board) {
		this.board = board;
	}

	public TicTacticsGame getGame() {
		return game;
	}

	public void setGame(TicTacticsGame game) {
		this.game = game;
	}

	public void setCaptured(boolean captured) {
		this.captured = captured;
	}

	public Winner getWinner() {
		return winner;
	}

	public void setWinner(Winner winner) {
		this.winner = winner;
	}

	public boolean isGameOver() {
		return gameOver;
	}

	public void setGameOver(boolean gameOver) {
		this.gameOver = gameOver;
	}

	public void evaluateState() {
		for (int horizontal = 0, vertical = 0; horizontal < NUMBER_OF_SQUARES; horizontal += 3) {
			if (checkSet(vertical, vertical + 3, vertical++ + 6) 
			||  checkSet(horizontal, horizontal + 1, horizontal + 2)) {
				return;
			}
		}

		// Diagonal
		if(checkSet(0, 4, 8) || checkSet(2, 4, 6)) {
			return;
		}

		if (++boardCounter == NUMBER_OF_SQUARES) {
			winner = Winner.TIE;
			boardCaptured();
			styleBoard();
			return;
		}
	}

	private boolean checkSet(int square1, int square2, int square3) {
		if (boardCounter >= 2) {
			if (board[square1].equivalentTo(board[square2]) 
			&& board[square2].equivalentTo(board[square3])) {
				if (!captured) {
					winner = board[square1].button().getText().equals("X") ? Winner.X : Winner.O;
					boardCaptured();
				}
				styleBoard();
				return true;
			}
		}
		return false;
	}

	public void resetBoard() {
		captured = false;
		toggleGameStatuses();
		winner = Winner.NONE;
		boardCounter = 0;
	}

	public boolean equivalentTo(TicTacToeBoard target) {
		return winner != Winner.NONE && (winner == target.winner() || target.winner() == Winner.TIE);
	}

	public Winner winner() {
		return winner;
	}

	private void styleBoard() {
		for (TicTacToeSquare square : board) {
			square.button().setStyle(winner.getStyle());
		}
	}

	public void disable() {
		for (int i = 0; i < board.length; i++) {
			board[i].button().setDisable(true);
		}
	}

	public void enable() {
		if (game.isGameGoing.getValue()) {
			for (int i = 0; i < board.length; i++) {
				board[i].button().setDisable(false);
			}
		}
	}

	public boolean isCaptured() {
		return captured;
	}

	public void toggleGameStatus() {
		game.isGameGoing.set(game.isGameGoing.get());
	}

	public void toggleGameStatuses() {
		gameOver = !gameOver;
	}

	private void boardCaptured() {
		captured = true;
		game.evaluateBoard();
	}

	public boolean isFilled() {
		for (TicTacToeSquare square : board) {
			if (square.button().getText().isEmpty()) {
				return false;
			}
		}
		return true;
	}

	public void reset() {
		captured = false;
		toggleGameStatus();
		winner = Winner.NONE;
		boardCounter = 0;
		for (int i = 0; i < board.length; i++) {
			board[i].reset();
		}
	}
}