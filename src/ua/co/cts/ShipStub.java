package ua.co.cts;

import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.input.touch.TouchEvent;

import android.util.Log;
import android.view.MotionEvent;

public class ShipStub extends Rectangle {
	private final static long CLICK_TIME = 300;

	private final Board mBoard;
	private final ShipPositioner mParent;
	private int mX = 0;
	private int mY = 0;
	
	private long mTouchStart;
	
	public ShipStub(Board board, ShipPositioner ship) {
		super(board.getX(),
			  board.getY(),
			  board.getCellSize(),
			  board.getCellSize());
		setColor(1f, 1f, 1f);
		mBoard = board;
		mParent = ship;
	}
	
	protected void setGridPosition(int x, int y) {
		if (x != mX || y != mY) {
			setPosition(mBoard.getX() + x * mBoard.getCellSize(),
					    mBoard.getY() + y * mBoard.getCellSize());
			mX = x;
			mY = y;
		}
	}
	
	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
		switch (pSceneTouchEvent.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mTouchStart = pSceneTouchEvent.getMotionEvent().getEventTime();
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			if (pSceneTouchEvent.getMotionEvent().getEventTime() - mTouchStart < CLICK_TIME) {
				Log.i(getClass().getName(), "Rotate on " + mX + ", " + mY);
				mParent.rotateGridPosition(this);
			}
			mBoard.onPositionUpdated(mParent);
			break;
		default:
			if (pSceneTouchEvent.getMotionEvent().getEventTime() - mTouchStart >= CLICK_TIME) {
				int cell_x = (int) ((pSceneTouchEvent.getX() - mBoard.getX()) / mBoard.getCellSize());
				int cell_y = (int) ((pSceneTouchEvent.getY() - mBoard.getY()) / mBoard.getCellSize());
				mParent.changeGridPosition(this, cell_x, cell_y);
			}
		}
		return true;
	}
	
	public int getGridX() {
		return mX;
	}
	
	public int getGridY() {
		return mY;
	}
}
