package ua.co.cts;

import it.gotoandplay.smartfoxclient.ISFSEventListener;
import it.gotoandplay.smartfoxclient.SFSEvent;
import it.gotoandplay.smartfoxclient.SmartFoxClient;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.util.DisplayMetrics;
import android.util.Log;

public class Room extends BaseGameActivity implements ISFSEventListener {
    private static int CAMERA_WIDTH = 720;
    private static int CAMERA_HEIGHT = 480;

	private Camera mCamera;
	
	@Override
	public Engine onLoadEngine() {
        final SmartFoxClient sfs = ServerConnection.mClient;
        sfs.addEventListener(SFSEvent.onConnectionLost, this);
        sfs.addEventListener(SFSEvent.onJoinRoom, this);
        sfs.addEventListener(SFSEvent.onPublicMessage, this);
        sfs.addEventListener(SFSEvent.onUserEnterRoom, this);
        sfs.addEventListener(SFSEvent.onUserLeaveRoom, this);

		final DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        CAMERA_WIDTH = displayMetrics.widthPixels;
        CAMERA_HEIGHT = displayMetrics.heightPixels;
		this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		return new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera).setNeedsSound(true));
	}

	@Override
	public void onLoadResources() {
	}

	@Override
	public Scene onLoadScene() {
		mEngine.registerUpdateHandler(new FPSLogger());
		
		final Scene scene = new Scene(2);
		scene.setBackground(new ColorBackground(0, 0.1f, 0));
		
		int sizes[] = {1, 1, 1, 1, 2, 2, 2, 3, 3, 4};
		ShipPositioner[] ships = new ShipPositioner[sizes.length];
		
		float cell_size = (CAMERA_HEIGHT - 20) / 10f;
		Board board = new Board(10f, 10f, cell_size, 10, 10, ships);
		scene.getLayer(0).addEntity(board);
		
		for (int i = 0; i < sizes.length; ++i) {
			ships[i] = new ShipPositioner(board);
			
			ShipStub[] stubs = new ShipStub[sizes[i]];
			for (int j = 0; j < stubs.length; ++j) {
				stubs[j] = new ShipStub(board, ships[i]);
				scene.getLayer(1).registerTouchArea(stubs[j]);
				scene.getLayer(1).addEntity(stubs[j]);
			}
			ships[i].setShips(stubs);
		}
		
		for (int i = 0; i < ships.length; ++i) {
			board.onPositionUpdated(ships[i]);
		}

		scene.setTouchAreaBindingEnabled(true);
		
		return scene;
	}

	@Override
	public void onLoadComplete() {
	}

	@Override
	public void handleEvent(SFSEvent event) {
		if (event.getName().equals(SFSEvent.onConnectionLost)) {
			Log.i(this.getClass().getName(), "Connection lost");
			finish();
		} else {
			Log.i(this.getClass().getName(), "Unknown event:");
			Log.i(this.getClass().getName(), event.toString());
		}
	}
}
