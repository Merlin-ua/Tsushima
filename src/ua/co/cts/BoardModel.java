package ua.co.cts;

public class BoardModel {
	public static final int BOARD_SIZE = 10;
	public enum Cell {
		UNKNOWN,
		SHIP,
		DEAD_SHIP,
		EMPTY,
		DEAD_EMPTY
	}
	
	private Cell mOurBoard[][] = new Cell[BOARD_SIZE][BOARD_SIZE];
	private Cell mTheirBoard[][] = new Cell[BOARD_SIZE][BOARD_SIZE];
	private boolean mOurTurn = false;
	
	public BoardModel(Board board, boolean turn) {
		mOurTurn = turn;
		for (int i = 0; i < BOARD_SIZE; ++i) {
			for (int j = 0; j < BOARD_SIZE; ++j) {
				mOurBoard[i][j] = Cell.EMPTY;
				mTheirBoard[i][j] = Cell.UNKNOWN;
			}
		}
		ShipPositioner[] ships = board.getShips();
		for (int i = 0; i < ships.length; ++i) {
			ShipStub[] stubs = ships[i].getStubs();
			for (int j = 0; j < stubs.length; ++j) {
				mOurBoard[stubs[j].getGridX()][stubs[j].getGridY()] = Cell.SHIP;
			}
		}
	}
}
