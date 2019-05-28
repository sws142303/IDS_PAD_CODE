/**
 * @file MtcMwiCb.java
 * @brief MTC mwi callbacks Interface Functions
 */
package com.juphoon.lemon.callback;

/**
 * @brief Class of mwi callbacks 
 */
public class MtcMwiCb {

    public interface Callback {

        /**
         * @brief Set the incoming message wait indication callback.
         * The callback will be notified if user subscribe MWI service.
         *
         */
        void mtcMwiCbIncoming();
    }
    
    /**
     * @brief MTC client provisioning callback init callbacks.
     *
     * This interface will call the native method to register client 
     * provisioning callback to MTC.
     */
    private static native void initCallback();

    /**
     * @brief MTC client provisioning callback destory callbacks.
     *
     * This interface will call the native method to deregister client 
     * provisioning callback to MTC.
     */
    private static native void destroyCallback();

    private static Callback sCallback;

     /**
     * @brief MTC client provisioning callback register callbacks.
     *
     * Set the active client provisioning callback instance which to receive 
     * client provisioning callbacks.
     * Use null to deregister provisioning callbacks.
     *
     * @param c The client provisioning callback instance.
     */
    public static void setCallback(Callback c) {
        sCallback = c;
        if (c != null) {
            initCallback();

        } else {
            destroyCallback();
        }
    }

    private static final int CALLBACK_MWICB_SETINCOMING = 0;
    
    /**
     * @brief Distribute wmi callbacks
     *
     * Distribute wimi callbacks
     */
    private static void mtcWmiCbCallback(int function) {
        switch (function) {
            case CALLBACK_MWICB_SETINCOMING:
                sCallback.mtcMwiCbIncoming();
                break;
        }
    }
}
