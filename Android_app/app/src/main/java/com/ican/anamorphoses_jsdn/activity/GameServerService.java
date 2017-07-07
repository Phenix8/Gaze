package com.ican.anamorphoses_jsdn.activity;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import com.ican.anamorphoses_jsdn.network.Server;
import com.ican.anamorphoses_jsdn.network.RoomNotifier;
import com.ican.anamorphoses_jsdn.network.ServerBase;
import com.ican.anamorphoses_jsdn.network.Common;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class GameServerService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_START_SERVER = "com.ican.anamorphoses_jsdn.action.STARTSERVER";
    private static final String ACTION_STOP_SERVER = "com.ican.anamorphoses_jsdn.action.STOPSERVER";

    // TODO: Rename parameters
    private static final String TCP_PORT_PARAM = "com.ican.anamorphoses_jsdn.extra.TCP_PORT_PARAM";
    private static final String MAX_PLAYER_PARAM = "com.ican.anamorphoses_jsdn.extra.MAX_PLAYER_PARAM";
    private static final String ROOM_NOTIFIER_PARAM = "com.ican.anamorphoses_jsdn.extra.ROOM_NOTIFIER_PARAM";
    private static final String SERVER_STATE_CALLBACK_PARAM = "com.ican.anamorphoses_jsdn.extra.SERVER_STATE_CALLBACK_PARAM";

    private static ServerBase server = null;

    public GameServerService() {
        super("GameServerService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void StartServer(Context context, int tcpPort, int maxPlayer,
                                   RoomNotifier notifier, ServerBase.ServerStateCallback callback) {
        Intent intent = new Intent(context, GameServerService.class);
        intent.setAction(ACTION_START_SERVER);
        intent.putExtra(TCP_PORT_PARAM, tcpPort);
        intent.putExtra(MAX_PLAYER_PARAM, maxPlayer);
        intent.putExtra(ROOM_NOTIFIER_PARAM, notifier);
        intent.putExtra(SERVER_STATE_CALLBACK_PARAM, callback);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void stopServer(Context context) {
        Intent intent = new Intent(context, GameServerService.class);
        intent.setAction(ACTION_STOP_SERVER);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_START_SERVER.equals(action)) {
                final int tcpPort = intent.getIntExtra(TCP_PORT_PARAM, Common.TCP_PORT);
                final int maxPlayer = intent.getIntExtra(MAX_PLAYER_PARAM, Common.DEFAULT_MAX_PLAYER);
                RoomNotifier notifier = (RoomNotifier) intent.getSerializableExtra(ROOM_NOTIFIER_PARAM);
                ServerBase.ServerStateCallback callback = (ServerBase.ServerStateCallback) intent.getSerializableExtra(SERVER_STATE_CALLBACK_PARAM);
                if (notifier == null) {
                    notifier = new RoomNotifier(Common.BROADCAST_MESSAGE, Common.DEFAULT_GAME_NAME, Common.UDP_PORT);
                }
                handleStartServer(tcpPort, maxPlayer, notifier, callback);
            } else if (ACTION_STOP_SERVER.equals(action)) {
                handleStopServer();
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleStartServer(int tcpPort, int maxPlayer, RoomNotifier notifier, ServerBase.ServerStateCallback callback) {
        if (server != null) {
            return;
        }

        server = new Server(notifier, tcpPort, maxPlayer);
        server.startListening(callback);
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleStopServer() {
        if (server == null) {
            return;
        }
        server.stopListening();
    }
}
