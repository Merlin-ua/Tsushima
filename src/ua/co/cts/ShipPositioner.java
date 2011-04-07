package ua.co.cts;


public class ShipPositioner {
	private final Board mBoard;
	private ShipStub mStubs[];
	private int mDx[];
	private int mDy[];
	
	public ShipPositioner(Board board) {
		mBoard = board;
	}
	
	public void setShips(ShipStub[] stubs) {
		mStubs = stubs;
		mDx = new int[mStubs.length];
		mDy = new int[mStubs.length];
		for (int i = 0; i < mStubs.length; ++i) {
			mStubs[i].setGridPosition(3, 3 + i);
			mDx[i] = mStubs[i].getGridX();
			mDy[i] = mStubs[i].getGridY();
		}
	}
	
	public ShipStub[] getStubs() {
		return mStubs;
	}
	
	private int getStubIndex(ShipStub stub) {
		int index = -1;
		for (int i = 0; i < mStubs.length; ++i) {
			if (mStubs[i] == stub) {
				index = i;
				break;
			}
		}
		return index;
	}
	
	private void adjustPosition() {
		int adjust_x = 0;
		int adjust_y = 0;
		for (int i = 0; i < mStubs.length; ++i) {
			if (mDx[i] < 0 && mDx[i] + adjust_x < 0) {
				adjust_x = -mDx[i];
			}
			if (mDx[i] >= mBoard.getBoardWidth() && mDx[i] + adjust_x >= mBoard.getBoardWidth()) {
				adjust_x = mBoard.getBoardWidth() - mDx[i] - 1;
			}
			if (mDy[i] < 0 && mDy[i] + adjust_y < 0) {
				adjust_y = -mDy[i];
			}
			if (mDy[i] >= mBoard.getBoardHeight() && mDy[i] + adjust_y >= mBoard.getBoardHeight()) {
				adjust_y = mBoard.getBoardHeight() - mDy[i] - 1;
			}
		}
		for (int i = 0; i < mStubs.length; ++i) {
			mStubs[i].setGridPosition(mDx[i] + adjust_x, mDy[i] + adjust_y);
		}
	}
	
	public void changeGridPosition(ShipStub stub, int x, int y) {
		int index = getStubIndex(stub);
		if (index >= 0) {
			for (int i = 0; i < mStubs.length; ++i) {
				mDx[i] = x + mStubs[i].getGridX() - mStubs[index].getGridX();
				mDy[i] = y + mStubs[i].getGridY() - mStubs[index].getGridY();
			}
			adjustPosition();
		}
	}
	
	public void offsetGridPosition(int dx, int dy) {
		changeGridPosition(mStubs[0], mStubs[0].getGridX() + dx, mStubs[0].getGridY() + dy);
	}
	
	public void rotateGridPosition(ShipStub stub) {
		int index = getStubIndex(stub);
		if (index >= 0) {
			for (int i = 0; i < mStubs.length; ++i) {
				mDx[i] = mStubs[index].getGridX() + mStubs[i].getGridY() - mStubs[index].getGridY();
				mDy[i] = mStubs[index].getGridY() + mStubs[i].getGridX() - mStubs[index].getGridX();
			}
			adjustPosition();
		}
	}
	
	public void rotateGridPosition() {
		rotateGridPosition(mStubs[0]);
	}
	
	public boolean doesIntersect(ShipPositioner other, int dx, int dy) {
		for (int i = 0; i < mStubs.length; ++i) {
			for (int j = 0; j < other.mStubs.length; ++j) {
				if (Math.abs(mStubs[i].getGridX() + dx - other.mStubs[j].getGridX()) <= 1 &&
					Math.abs(mStubs[i].getGridY() + dy - other.mStubs[j].getGridY()) <= 1) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean doesIntersect(ShipPositioner other) {
		return doesIntersect(other, 0, 0);
	}
	
	public boolean canOffset(int dx, int dy) {
		for (int i = 0; i < mStubs.length; ++i) {
			if (mStubs[i].getGridX() + dx < 0 || mStubs[i].getGridX() + dx >= mBoard.getBoardWidth()) {
				return false;
			}
			if (mStubs[i].getGridY() + dy < 0 || mStubs[i].getGridY() + dy >= mBoard.getBoardHeight()) {
				return false;
			}
		}
		return true;
	}
}