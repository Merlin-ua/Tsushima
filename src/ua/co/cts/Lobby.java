package ua.co.cts;

import it.gotoandplay.smartfoxclient.ISFSEventListener;
import it.gotoandplay.smartfoxclient.SFSEvent;
import it.gotoandplay.smartfoxclient.SmartFoxClient;
import it.gotoandplay.smartfoxclient.data.Room;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;

public class Lobby extends ListActivity implements ISFSEventListener {
	private static final String ROOM_CREATE_TAG = "<New>";
	/*
	private TextView chat;
	private List<String> chat_text = new LinkedList<String>();
	
	private void Log.i(this.getClass().getName(), String text) {
		chat_text.add(text);
		if (chat_text.size() > 10) {
			chat_text.remove(0);
		}
		StringBuilder builder = new StringBuilder();
		for (String line : chat_text) {
			builder.append(line);
			builder.append('\n');
		}
		final String new_text = builder.toString();
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				chat.setText(new_text);
			}
		});
	}
	*/
	List<String> rooms = new ArrayList<String>();
	List<Integer> roomIds = new ArrayList<Integer>();
	
	ArrayAdapter<String> adapter;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        rooms.add(ROOM_CREATE_TAG);
        roomIds.add(-1);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, rooms);
        adapter.setNotifyOnChange(false);
        this.getListView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.i(this.getClass().getName(), "Clicked " + rooms.get(position));
				if (position == 0) {
					assert rooms.get(position).equals(ROOM_CREATE_TAG);
					Map<String, Object> properties = new HashMap<String, Object>();
					// properties.put("password", "123");
					Time current = new Time();
					current.setToNow();
					ServerConnection.mClient.createRoom("created@" + current.format("%H%M%S"), 2, properties);
				} else {
					ServerConnection.mClient.joinRoom(roomIds.get(position));
				}
			}
		});
        setListAdapter(adapter);
        
        final SmartFoxClient sfs = ServerConnection.mClient;
        sfs.addEventListener(SFSEvent.onConnection, this);
        sfs.addEventListener(SFSEvent.onConnectionLost, this);
        sfs.addEventListener(SFSEvent.onLogin, this);
        sfs.addEventListener(SFSEvent.onRoomListUpdate, this);
        sfs.addEventListener(SFSEvent.onJoinRoom, this);
        sfs.addEventListener(SFSEvent.onRoomAdded, this);
        sfs.addEventListener(SFSEvent.onRoomDeleted, this);
        sfs.addEventListener(SFSEvent.onPublicMessage, this);

        new Thread() {
        	@Override
            public void run() {
        		// TODO: get it?
            	sfs.connect("10.0.2.2", 9339);
            }
        }.start();
        
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
        final SmartFoxClient sfs = ServerConnection.mClient;
        sfs.removeEventListener(SFSEvent.onConnection, this);
        sfs.removeEventListener(SFSEvent.onConnectionLost, this);
        sfs.removeEventListener(SFSEvent.onLogin, this);
        sfs.removeEventListener(SFSEvent.onRoomListUpdate, this);
        sfs.removeEventListener(SFSEvent.onJoinRoom, this);
        sfs.removeEventListener(SFSEvent.onRoomAdded, this);
        sfs.removeEventListener(SFSEvent.onRoomDeleted, this);
        sfs.removeEventListener(SFSEvent.onPublicMessage, this);
    }
    
    private void updateRoomList() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				adapter.notifyDataSetChanged();
				adapter.setNotifyOnChange(false);
			}
		});
    }

	@Override
	public void handleEvent(SFSEvent event) {
		if (event.getName().equals(SFSEvent.onConnection)) {
			if (event.getParams().getBool("success")) {
				Log.i(this.getClass().getName(), "Connection successful");
				// TODO: get it?
				ServerConnection.mClient.login("battleShip", "merlin", "");
			} else {
				Log.i(this.getClass().getName(), "Connection NOT successful");
				Log.i(this.getClass().getName(), event.toString());
			}
		} else if (event.getName().equals(SFSEvent.onConnectionLost)) {
			Log.e(this.getClass().getName(), "Connection lost");
		} else if (event.getName().equals(SFSEvent.onLogin)) {
			if (event.getParams().getBool("success")) {
				Log.i(this.getClass().getName(), "Login successful");
			} else {
				Log.i(this.getClass().getName(), "Login NOT successful");
				Log.i(this.getClass().getName(), event.toString());
			}
		} else if (event.getName().equals(SFSEvent.onRoomListUpdate)) {
			ServerConnection.mClient.autoJoin();
			@SuppressWarnings("unchecked")
			Map<Integer, Room> obj = (Map<Integer, Room>) event.getParams().get("roomList");
			Log.i(this.getClass().getName(), "List update");
			roomIds.clear();
			rooms.clear();
			rooms.add(ROOM_CREATE_TAG);
			roomIds.add(-1);
			for (Integer rId : obj.keySet()) {
				roomIds.add(rId);
				rooms.add(obj.get(rId).getName());
			}
			updateRoomList();
		} else if (event.getName().equals(SFSEvent.onRoomAdded)) {
			Room room = (Room)event.getParams().get("room");
			roomIds.add(room.getId());
			rooms.add(room.getName());
			Log.i(this.getClass().getName(), "Adding room " + room.getName());
			updateRoomList();
		} else if (event.getName().equals(SFSEvent.onRoomDeleted)) {
			Room room = (Room)event.getParams().get("room");
			int loc = roomIds.indexOf(room.getId());
			if (loc != -1) {
				roomIds.remove(loc);
				rooms.remove(loc);
				Log.i(this.getClass().getName(), "Removing room " + room.getName());
				updateRoomList();
			} else {
				Log.i(this.getClass().getName(), "Removal unsuccessful for " + room.getName());
			}
		} else if (event.getName().equals(SFSEvent.onJoinRoom)) {
			Room room = (Room)event.getParams().get("room");
			Log.i(this.getClass().getName(), "Room [" + room.getName() + "] joined.");
			if (!room.getName().equals("Lobby")) {
				startActivity(new Intent(this, ua.co.cts.Room.class));
			}
		} else {
			Log.i(this.getClass().getName(), "Unknown event:");
			Log.i(this.getClass().getName(), event.toString());
		}
	}
}