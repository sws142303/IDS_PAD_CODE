/**
 * @file MtcCapCb.java
 * @brief MTC capability callbacks Interface Functions
 */
 package com.juphoon.lemon.callback;

/**
 * @brief Class of MTC capability callbacks
 */
public class MtcCapCb {

    /**
     * @brief MTC capability callbacks
     *
     * In order to receive MTC capability callbacks, user should implement this 
     * interface, then use @ref MtcCapCb.setCallback to register callbacks.
     */    
    public interface Callback {

        /** 
         * @brief MTC capability query ok callback.
         * 
         * @param [in] uri Uri address.
         * @param [in] cookie capability cookie.
         * @param [in] type capability type.
         * 
         */
        void mtcCapCbCapQOk(String uri, Object cookie, int type);

        /** 
         * @brief MTC capability query failed callback.
         * 
         * @param [in] uri Uri address.
         * @param [in] cookie capability cookie.
         * @param [in] errorCode error Code.
         */
        void mtcCapCbQFailed(String uri, Object cookie, int errorCode);

        /** 
         * @brief MTC capability query update callback.
         * 
         * @param [in] uri Uri address.
         * @param [in] type capability typ
         */
        void mtcCapCbQUpdate(String uri, int type);

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
        if (c != null) {
            if (sCallback == null)
                initCallback();
        } else {
            destroyCallback();
        }
        sCallback = c;
    }

    private static final int CALLBACK_CAPCB_SETQ_OK = 0;
    private static final int CALLBACK_CAPCB_SETQ_FAILED = 1;
    private static final int CALLBACK_CAPCB_SETQ_UPDATE = 2;

    /**
     * @brief Distribute call callbacks
     *
     * Distribute call callbacks
     */
    private static void mtcCapCbCallback(int function, String uri, Object cookie, int argInt) {
        switch (function) {
            case CALLBACK_CAPCB_SETQ_OK:
                sCallback.mtcCapCbCapQOk(uri, cookie, argInt);
                break;
            case CALLBACK_CAPCB_SETQ_FAILED:
                sCallback.mtcCapCbQFailed(uri, cookie, argInt);
                break;
            case CALLBACK_CAPCB_SETQ_UPDATE:
                sCallback.mtcCapCbQUpdate(uri, argInt);
                break;
        }
    }
}
