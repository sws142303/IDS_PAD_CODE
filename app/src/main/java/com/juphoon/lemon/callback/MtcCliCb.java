/**
 * @file MtcCliCb.java
 * @brief MTC client callbacks Interface Functions
 */
 package com.juphoon.lemon.callback;

import java.util.ArrayList;

import android.os.Handler;
import android.os.Message;

import com.juphoon.lemon.MtcCli;

/**
 * @brief Class of MTC client callbacks
 */
public class MtcCliCb {
    /**
     * @brief MTC client callbacks
     *
     * In order to receive MTC client callbacks, user should implement this 
     * interface, then use @ref MtcCliCb.registerCallback to register callbacks.
     */
    public interface Callback {

        /**
         * @brief the login successfully indication callback.
         * The callback will be notified if server login successfully
         * If @ref MtcCliDb::Mtc_CliDbGetUserReg is ZFALSE, this callback will not be called.
         * 
         * The callback is one of @ref MtcCli::Mtc_CliLogin results
         */
        public void mtcCliCbServLoginOk();

        /**
         * @brief the login successfully indication callback.
         * The callback will be notified if local login successfully
         * If @ref MtcCliDb::Mtc_CliDbGetUserReg is ZTRUE, this callback will not be called.
         * 
         * The callback is one of @ref MtcCli::Mtc_CliLogin results
         */
        public void mtcCliCbLclLoginOk();

        /**
         * @brief the login failed indication callback.
         * The callback will be notified if login failed
         * The callback is one of @ref MtcCli::Mtc_CliLogin results
         *
         * @param [in] dwStatCode Login failed code.
         */
        public void mtcCliCbLoginFailed(int dwStatCode);
        
        /**
         * @brief the refresh successfully indication callback.
         * The callback will be notified if refresh successfully
         * The callback is one of @ref MtcCli::Mtc_CliRefresh results
         *
         * @param [in] bActive Indicate if it is a user action results.
         * @param [in] bChanged Indicate if the registration information changed.
         */
        public void mtcCliCbRefreshOk(boolean bActive, boolean bChanged);
        
        /**
         * @brief the refresh indication callback.
         * The callback will be notified if refresh failed
         * The callback is one of @ref MtcCli::Mtc_CliRefresh results
         *
         * @param [in] bActive Indicate if it is a user action results.
         * @param [in] dwStatCode status code.
         */
        public void mtcCliCbRefreshFailed(boolean bActive, int dwStatCode);

        /**
         * @brief the local logout indication callback.
         * The callback will be notified if client is not in register with server.
         * If @ref MtcCliDb::Mtc_CliDbGetUserReg is ZTRUE, this callback will not be called.
         * 
         * The callback is one of @ref MtcCli::Mtc_CliLogout results
         */
        public void mtcCliCbLclLogout();

        /**
         * @brief the server logout indication callback.
         * The callback will be notified if client has un-register from server.
         * If @ref MtcCliDb::Mtc_CliDbGetUserReg is ZFALSE, this callback will not be called.
         * 
         * The callback is one of @ref MtcCli::Mtc_CliLogout results
         *
         * @param [in] bActive Indicate if it is a user action results.
         * @param [in] iStatCode The event type which trigger logout.
         * @param [in] dwExpires The expire time value if server notify logout later.
         */
        public void mtcCliCbServLogout(boolean bActive, int iStatCode, int dwExpires);

        /**
         * @brief Subscribe network changed indication callback.
         * The callback will be notified if network has changed
         *
         * @param [in] netType Access network type.
        */
        public void mtcCliCbSubNetChanged(int netType);
        
     /**
         * @brief the send user message ok callback.
         *
         * @param [in] iCookie User cookie value.
        */
        public void mtcCliCbSendUserMsgOk(int iCookie);
        
	     /**
	         * @brief the send user message failed callback.
	         *
	         * @param [in] iCookie User cookie value.
	        */
	     public void mtcCliCbSendUserMsgFailed(int iCookie);
	 	
		 /**
		 * @brief Type define of MTC GUI callback for notify send user message result.
		 *
		 * @param [out] sPeerUri Uri of peer user.
		 * @param [out] sBodyType Body type string, ZNULL for "plain".
		 * @param [out] sMsgBody Sip info, options or page message body string.
		 */
		public void mtcCliCbReceiveUserMsgReq(String sPeerUri,
                                              String sBodyType, String sMsgBody);
    }
    
    /**
     * @brief MTC client callback set callbacks.
     *
     * Set the active client callback instance which to receive client callbacks.
     * Use unregisterCallback to deregister all client callbacks.
     *
     * @param [in] c The client callback instance.
     */
    public static void registerCallback(final Callback c) {
        if (sCallbacks == null) {
            sCallbacks = new ArrayList<Callback>();
            initCallback();
        }
        sHandler.post(new Runnable() {
            @Override
            public void run() {
                sCallbacks.add(c);
            } 
        });
    }
    
    public static void unregisterCallback(final Callback c) {
        if (sCallbacks == null) {
            return;
        }
        sHandler.post(new Runnable() {
            @Override
            public void run() {
                sCallbacks.remove(c);
                if (sCallbacks.size() == 0) {
                    sCallbacks = null;
                    destroyCallback();
                }
            } 
        }); 
    }
    
    private static ArrayList<Callback> sCallbacks;
    
    /**
     * @brief MTC client callback init callbacks.
     *
     * This interface will call the native method to register client callback to 
     * MTC.
     */
    private static  void initCallback(){

    }

    /**
     * @brief MTC client callback destory callbacks.
     *
     * This interface will call the native method to deregister client callback to 
     * MTC.
     */
    private static  void destroyCallback(){

    }

    static final Handler sHandler = new Handler() {
        public void handleMessage(Message msg) {
            MtcCli.Mtc_CliDrive(msg.what);
        }
    };

    private static void mtcCliCbEvnt(int eventId) {
        Message msg = sHandler.obtainMessage(eventId);
        sHandler.sendMessage(msg);
    }

    public static final int CALLBACK_EVENT = 0;
    public static final int CALLBACK_SERV_LOGIN_OK = 1;
    public static final int CALLBACK_LCL_LOGIN_OK = 2;
    public static final int CALLBACK_LOGIN_FAILED = 3;
    public static final int CALLBACK_REFRESH_OK = 4;
    public static final int CALLBACK_REFRESH_FAILED = 5;
    public static final int CALLBACK_LCL_LOGOUT = 6;
    public static final int CALLBACK_SERV_LOGOUT = 7;
    public static final int CALLBACK_SUBNETCHANGED = 8;
    public static final int CALLBACK_SENDUMSG_OK = 9;
    public static final int CALLBACK_SENDUMSG_FAILED = 10;
	public static final int CALLBACK_RECEIVEUMSG_REQ = 11;

    public static void mtcCliCbCallback(int function, int arg1, 
				int arg2, boolean arg3, boolean arg4, String arg5, String arg6, String arg7) {
        switch (function) {
            case CALLBACK_EVENT:
                mtcCliCbEvnt(arg1);
                break;
            case CALLBACK_SERV_LOGIN_OK:
                for (Callback c : sCallbacks) {
                    c.mtcCliCbServLoginOk();
                }
                break;
            case CALLBACK_LCL_LOGIN_OK:
                for (Callback c : sCallbacks) {
                    c.mtcCliCbLclLoginOk();
                }
                break;
            case CALLBACK_LOGIN_FAILED:
                for (Callback c : sCallbacks) {
                    c.mtcCliCbLoginFailed(arg1);
                }
                break;
            case CALLBACK_REFRESH_OK:
                for (Callback c : sCallbacks) {
                    c.mtcCliCbRefreshOk(arg3, arg4);
                }
                break;
            case CALLBACK_REFRESH_FAILED:
                for (Callback c : sCallbacks) {
                    c.mtcCliCbRefreshFailed(arg3, arg1);
                }
                break;
            case CALLBACK_LCL_LOGOUT:
                for (Callback c : sCallbacks) {
                    c.mtcCliCbLclLogout();
                }
                break;
            case CALLBACK_SERV_LOGOUT:
                for (Callback c : sCallbacks) {
                    c.mtcCliCbServLogout(arg3, arg1, arg2);
                }
                break;
			case CALLBACK_SENDUMSG_OK:
				for (Callback c : sCallbacks) {
					c.mtcCliCbSendUserMsgOk(arg1);
				}
				break;
			case CALLBACK_SENDUMSG_FAILED:
				for (Callback c : sCallbacks) {
					c.mtcCliCbSendUserMsgFailed(arg1);
				}
				break;
			case CALLBACK_RECEIVEUMSG_REQ:
				for (Callback c : sCallbacks) {
					c.mtcCliCbReceiveUserMsgReq(arg5, arg6, arg7);
				}
				break;
        }
    }
}
