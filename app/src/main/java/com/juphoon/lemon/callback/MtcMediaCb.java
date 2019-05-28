/**
 * @file MtcMediaCb.java
 * @brief MTC Media callbacks Interface Functions
 */
package com.juphoon.lemon.callback;

/**
 * @brief Class of MTC Media callbacks
 */
public class MtcMediaCb {

    public interface Callback {

        /**
         * @brief Set callback of indicate audio device was used by another application.
         *
         * GUI should terminate all call session for there is no resource.
         *
         */
        void mtcAudioDevChanged();

        /**
         * @brief Set callback of indicate audio device list changed.
         *
         * GUI use "MTC Media Interfaces" to refresh the audio devcie
         * list.
         *
         */
        void mtcVideoDevChanged();

        /**
         * @brief Set callback of indicate video device list changed.
         *
         * GUI use "MTC Media Interfaces" to refresh the video devcie
         * list.
         *
         */
        void mtcMediaCbAudioDevInterrupted();
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

    private static final int CALLBACK_MEDIACB_AUDIODEVCHANGE = 0;
    private static final int CALLBACK_MEDIACB_VIDEODEVCHANGE = 1;
    private static final int CALLBACK_MEDIACB_AUDIODEV_INTERRUPTED = 2;
    
    /**
     * @brief Distribute wmi callbacks
     *
     * Distribute Media callbacks
     */
    private static void mtcMediaCbCallback(int function, int sessionId) {
        switch (function) {
            case CALLBACK_MEDIACB_AUDIODEVCHANGE:
                sCallback.mtcAudioDevChanged();
                break;
            case CALLBACK_MEDIACB_VIDEODEVCHANGE:
                sCallback.mtcVideoDevChanged();
                break;
            case CALLBACK_MEDIACB_AUDIODEV_INTERRUPTED:
            	sCallback.mtcMediaCbAudioDevInterrupted();
            	break;
        }
    }
}
