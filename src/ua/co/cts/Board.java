package ua.co.cts;

import org.anddev.andengine.entity.primitive.Rectangle;

import android.util.Log;

public class Board extends Rectangle {
	private final float mCellSize;
	private final int mWidth;
	private final int mHeight;
	private final ShipPositioner[] mShips;
	
	Board(float top, float left, float cell_size, int board_width, int board_height, ShipPositioner[] ships) {
		super(top, left, cell_size * board_width, cell_size * board_height);
		setColor(0.2f, 0.0f, 0.0f);
		mCellSize = cell_size;
		mWidth = board_width;
		mHeight = board_height;
		mShips = ships;
	}
	
	public float getCellSize() {
		return mCellSize;
	}

	public int getBoardWidth() {
		return mWidth;
	}

	public int getBoardHeight() {
		return mHeight;
	}
	
	private boolean tryOffset(ShipPositioner ship, int dx, int dy) {
		if (ship.canOffset(dx, dy)) {
			Log.i(getClass().getName(), "Checking offset " + dx + ", " + dy);
			for (int i = 0; i < mShips.length; ++i) {
				if (ship != mShips[i]) {
					if (ship.doesIntersect(mShips[i], dx, dy)) {
						return false;
					}
				}
			}
			Log.i(getClass().getName(), "Got it!");
			return true;
		}
		return false;
	}
	
	private boolean doReposition(ShipPositioner ship) {
		for (int mod = 1; mod < Math.max(mWidth, mHeight); ++mod) {
			for (int i = -mod; i < mod; ++i) {
				if (tryOffset(ship, i, -mod)) {
					ship.offsetGridPosition(i, -mod);
					return true;
				}
				if (tryOffset(ship, -i, mod)) {
					ship.offsetGridPosition(-i, mod);
					return true;
				}
				if (tryOffset(ship, mod, i)) {
					ship.offsetGridPosition(mod, i);
					return true;
				}
				if (tryOffset(ship, -mod, -i)) {
					ship.offsetGridPosition(-mod, -i);
					return true;
				}
			}
		}
		return false;
	}
	
	public void onPositionUpdated(ShipPositioner ship) {
		for (int i = 0; i < mShips.length; ++i) {
			if (ship != mShips[i]) {
				if (ship.doesIntersect(mShips[i])) {
					Log.i(getClass().getName(), "Intersection with " + i);
					if (!doReposition(mShips[i])) {
						mShips[i].rotateGridPosition();
						if (!doReposition(mShips[i])) {
							Log.e(getClass().getName(), "Whoa! We're in a trouble...");
						}
					}
				}
			}
		}
	}
	
	public ShipPositioner[] getShips() {
		return mShips;
	}
}
