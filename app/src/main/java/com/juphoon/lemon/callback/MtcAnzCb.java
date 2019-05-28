/**
 * @file MtcAnzCb.java
 * @brief MTC anz data stream callbacks Interface Functions
 */
package com.juphoon.lemon.callback;

import java.util.ArrayList;
import android.os.Handler;
import android.os.Message;

import com.juphoon.lemon.MtcAnz;
import com.juphoon.lemon.callback.MtcCallCb.Callback;

/**
 * @brief Class of MTC anz data callbacks
 */
public class MtcAnzCb {
	/**
	 * @brief MTC anz data stream callbacks
	 *
	 * In order to receive MTC anz data stream callbacks, user should
	 * implement this interface, then use @ref MtcAnzCb.registerCallback
	 * to register callbacks.
	 */

	public interface Callback {
		/**
		 * @brief Open a udp port to receive data stream.
		 * 
		 * @param [out] dwTptId The id of data stream..
		 * @param [out] pcUdpData Incoming udp data content.
		 * @param [out] iDataLen Udp data content length.
		 */
		void mtcAnzDataIncoming(int dwTptId, byte[] pcUdpData, int iDataLen);
	}

	/**
	 * @brief MTC call callback set callbacks.
	 * 
	 * Set the active call callback instance which to receive call
	 * callbacks. Use null to deregister all call callbacks.
	 * 
	 * @param [in] c The call callback instance.
	 */
	public static void setCallback(Callback c) {
		if (c != null) {
			if (sCallback == null)
				initAnzCallback();
		} else {
			destroyAnzCallback();
		}
		sCallback = c;
	}

	/**
	 * @brief MTC anz data callback init callbacks.
	 * 
	 * This interface will call the native method to register client
	 * callback to MTC.
	 */
	private static  void initAnzCallback(){

	}

	/**
	 * @brief MTC anz data callback destory callbacks.
	 * 
	 * This interface will call the native method to deregister client
	 * callback to MTC.
	 */
	private  static  void destroyAnzCallback(){

	}
	
	public static final int CALLBACK_DATAINCOMING = 0;
	static Callback sCallback;
	
	public static void mtcAnzCbCallback(int function, int arg1, byte[] arg2,
			int arg3) {
		switch (function) {
		case CALLBACK_DATAINCOMING:
			sCallback.mtcAnzDataIncoming(arg1, arg2, arg3);
			break;
		}
	}
}
