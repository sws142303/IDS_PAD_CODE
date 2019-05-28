/**
 * @file MtcConfCb.java
 * @brief MTC conference callbacks Interface Functions
 */
 package com.juphoon.lemon.callback;

/**
 * @brief Class of MTC conference callbacks
 */
public class MtcConfCb {

    /**
     * @brief MTC conference callbacks
     *
     * In order to receive MTC videoshare callbacks, user should implement this 
     * interface, then use @ref MtcVShareCb.setCallback to register callbacks.
     */    
    public interface Callback {

        /**
         * @brief Set callback of client receive an incoming conference.
         *
         * This callback indicates client receive an incoming conference. GUI should 
         * show the conference window.
         * "dwConfId" is the ID of conference. GUI should assign it to the
         *   corresponding window, use MtcConf::Mtc_ConfGetPeerUri to get detail
         *   information of conference.
         *
         * @param [in] dwConfId conference Id.
         */
        void mtcConfCbIncoming(int dwConfId);

        /**
         * @brief Set callback of client setup an outgoing conference.
         *
         * This callback indicates client has setup an outgoing conference. GUI should not
         * close the conference window, just update the client status in conference.
         * "dwConfId" is the ID of conference. GUI use it to locate the conference
         *   window.
         *
         * @param [in] dwConfId conference Id.
         */
        void mtcConfCbOutgoing(int dwConfId);

        /**
         * @brief Set callback of receive response during conference establish.
         *
         * This client try to establish a conference. And when receive 1xx response
         * this callback will be invoked. GUI should update status of conference
         * window.
         * "dwConfId" is the ID of conference. GUI use it to locate the conference
         *   window.
         *
         * @param [in] dwConfId conference Id.
         */
        void mtcConfCbAlerted(int dwConfId);

        /**
         * @brief Set callback of conference has established.
         *
         * This callback indicates conference has established. GUI should update
         * status of conference window.
         * "dwConfId" is the ID of conference. GUI use it to locate the conference
         *   window.
         *
         * @param [in] dwConfId conference Id.
         */
        void mtcConfCbConned(int dwConfId);

        /**
         * @brief Set callback of disconnected from conference.
         *
         * This callback indicates client has disconnected from conference. GUI 
         * should close conference window.
         * "dwConfId" is the ID of conference. GUI use it to locate the conference
         *   window.
         *
         * @param [in] dwConfId conference Id.
         */
        void mtcConfCbDisced(int dwConfId);

        /**
         * @brief Set callback of the invited user has accepted invite.
         *
         * This client add another user to the conference. This callback was invoked
         * when that user accept the invite. Usually, callback which was set by 
         * MtcConf::Mtc_ConfCbSetPtptUpdt will be invoked sequentially. 
         * GUI should then update the participants of the conference. 
         * "dwConfId" is the ID of conference. GUI use it to locate the conference
         *   window.
         *
         * @param [in] dwConfId conference Id.
         */
        void mtcConfCbIvtAcpt(int dwConfId);

        /**
         * @brief Set callback of kick user is accepted.
         *
         * This client want to remove a user from the conference. This callback 
         * was invoked when that user has been removed. Usually, 
         * callback which was set by @ref mtcConfCbPtptUpdt will be invoked sequentially. 
         * GUI should then update the participants of the conference. 
         * "dwConfId" is the ID of conference. GUI use it to locate the conference
         *   window.
         *
         * @param [in] dwConfId conference Id.
         */
        void mtcConfCbKickAcpt(int dwConfId);

        /**
         * @brief Set callback of indicate conference participant's status updated.
         *
         * GUI should update conference participant's status.
         * "dwConfId" is the ID of conference. GUI should use it to locate
         *        call window.
         * "pcUri" is the URI of the participant which status has been updated.
         * "dwState" see below values:
         * -MTC_CONF_PARTP_STATE_PENDING Unconfirmed state
         * -MTC_CONF_PARTP_STATE_DIALINGIN Creating or join a conference
         * -MTC_CONF_PARTP_STATE_DIALINGOUT Being invited to a conference
         * -MTC_CONF_PARTP_STATE_ALERTING Being invited and is alerting
         * -MTC_CONF_PARTP_STATE_CONNED Attend to a conference successfully
         * -MTC_CONF_PARTP_STATE_ONHOLD In hold state
         * -MTC_CONF_PARTP_STATE_DISCING Leaving a conference
         * -MTC_CONF_PARTP_STATE_DISCED Already leave a conference
         *
         * @param [in] dwConfId conference Id.
         * @param [in] uri participant uri.
         * @param [in] dwStatCode Status code.
         */
        void mtcConfCbPtptUpdt(int dwConfId, int dwStatCode, String uri);

        /**
         * @brief Set callback of indicate conference modifying accepted.
         *
         * To change the attribute of media stream is called conference modify,
         * includes hold/unhold and add/remove media stream. This callback indicates
         * the modification is accept by peer. GUI should check which service has
         * been invoke by user. If user hold or unhold the call, show hold or
         * unhold successfully. If user add video upon a voice call, then switch
         * to video call window.
         * "dwConfId" is the ID of conference. GUI should use it to locate
         *        conference window.
         *
         * @param [in] dwConfId conference Id.
         */
        void mtcConfCbMdfyAcpt(int dwConfId);

        /**
         * @brief Set callback of indicate conference modified.
         *
         * This callback indicates modification was invoke by peer, and this 
         * modification is completed. 
         * "dwConfId" is the ID of conference. GUI should use it to locate
         *    conference window.
         *
         * @param [in] dwConfId conference Id.
         */
        void mtcConfCbMdfyed(int dwConfId);

        /**
         * @brief Set callback of indicate conference hold OK.
         *
         * This callback indicates hold operation has been completed successfully.
         * GUI should update conference status according to conference ID.
         * "dwConfId" is the ID of conference. GUI should use it to locate
         *    call window.
         *
         * @param [in] dwConfId conference Id.
         */
        void mtcConfCbHoldOk(int dwConfId);

        /**
         * @brief Set callback of indicate conference hold failed.
         *
         * This callback indicates hold operation has been failed.
         * GUI should update conference status according to conference ID.
         * "dwConfId" is the ID of conference. GUI should use it to locate
         *    call window.
         *
         * @param [in] dwConfId conference Id.
         */
        void mtcConfCbHoldFailed(int dwConfId);

        /**
         * @brief Set callback of indicate conference un-hold OK.
         *
         * This callback indicates un-hold operation has been completed successfully.
         * GUI should update conference status according to conference ID.
         * "dwConfId" is the ID of conference. GUI should use it to locate
         *    call window.
         *
         * @param [in] dwConfId conference Id.
         */
        void mtcConfCbUnHoldOk(int dwConfId);

        /**
         * @brief Set callback of indicate conference un-hold failed.
         *
         * This callback indicates un-hold operation has been failed.
         * GUI should update conference status according to conference ID.
         * "dwConfId" is the ID of conference. GUI should use it to locate
         *    call window.
         *
         * @param [in] dwConfId conference Id.
         */
        void mtcConfCbUnHoldFailed(int dwConfId);

        /**
         * @brief Set callback of indicate conference was held by peer.
         *
         * This callback indicates peer hold the conference.
         * GUI should update conference status according to conference ID.
         * "dwConfId" is the ID of conference. GUI should use it to locate
         *    call window.
         *
         * @param [in] dwConfId conference Id.
         */
        void mtcConfCbHeld(int dwConfId);

        /**
         * @brief Set callback of indicate conference was un-held by peer.
         *
         * This callback indicates un-held hold the conference.
         * GUI should update conference status according to conference ID.
         * "dwConfId" is the ID of conference. GUI should use it to locate
         *    call window.
         *
         * @param [in] dwConfId conference Id.
         */
        void mtcConfCbUnHeld(int dwConfId);

        /**
         * @brief Set callback of error occurred during conference.
         *
         * This callback indicates error occurred during call. GUI should show
         * the detail error information(english) to user.
         *
         * @param [in] dwConfId conference Id.
         * @param [in] dwStatCode Error code.         
         */
        void mtcConfCbError(int dwConfId, int dwStatCode);
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

    private static final int CALLBACK_CONFCB_INCOMING = 0;
    private static final int CALLBACK_CONFCB_OUTGOING = 1;
    private static final int CALLBACK_CONFCB_ALERTED = 2;
    private static final int CALLBACK_CONFCB_CONNED = 3;
    private static final int CALLBACK_CONFCB_DISCED = 4;
    private static final int CALLBACK_CONFCB_IVTACPT = 5;
    private static final int CALLBACK_CONFCB_KICKACPT = 6;
    private static final int CALLBACK_CONFCB_PTPTUPDT = 7;
    private static final int CALLBACK_CONFCB_MDFYACPT = 8;
    private static final int CALLBACK_CONFCB_MDFYED = 9;
    private static final int CALLBACK_CONFCB_HOLDOK = 10;
    private static final int CALLBACK_CONFCB_HOLDFAILED = 11;
    private static final int CALLBACK_CONFCB_UNHOLDOK = 12;
    private static final int CALLBACK_CONFCB_UNHOLDFAILED = 13;
    private static final int CALLBACK_CONFCB_HELD = 14;
    private static final int CALLBACK_CONFCB_UNHELD = 15;
    private static final int CALLBACK_CONFCB_ERROR = 16;

    /**
     * @brief Distribute call callbacks
     *
     * Distribute call callbacks
     */
    private static void mtcConfCbCallback(int function, int dwConfId, int dwStatCode, String uri) {
        switch (function) {
            case CALLBACK_CONFCB_INCOMING:
                sCallback.mtcConfCbIncoming(dwConfId);
                break;
            case CALLBACK_CONFCB_OUTGOING:
                sCallback.mtcConfCbOutgoing(dwConfId);
                break;
            case CALLBACK_CONFCB_ALERTED:
                sCallback.mtcConfCbAlerted(dwConfId);
                break;
            case CALLBACK_CONFCB_CONNED:
                sCallback.mtcConfCbConned(dwConfId);
                break;
            case CALLBACK_CONFCB_DISCED:
                sCallback.mtcConfCbDisced(dwConfId);
                break;
            case CALLBACK_CONFCB_IVTACPT:
                sCallback.mtcConfCbIvtAcpt(dwConfId);
                break;
            case CALLBACK_CONFCB_KICKACPT:
                sCallback.mtcConfCbKickAcpt(dwConfId);
                break;
            case CALLBACK_CONFCB_PTPTUPDT:
                sCallback.mtcConfCbPtptUpdt(dwConfId, dwStatCode, uri);
                break;
            case CALLBACK_CONFCB_MDFYACPT:
                sCallback.mtcConfCbMdfyAcpt(dwConfId);
                break;
            case CALLBACK_CONFCB_MDFYED:
                sCallback.mtcConfCbMdfyed(dwConfId);
                break;
            case CALLBACK_CONFCB_HOLDOK:
                sCallback.mtcConfCbHoldOk(dwConfId);
                break;
            case CALLBACK_CONFCB_HOLDFAILED:
                sCallback.mtcConfCbHoldFailed(dwConfId);
                break;
            case CALLBACK_CONFCB_UNHOLDOK:
                sCallback.mtcConfCbUnHoldOk(dwConfId);
                break;
            case CALLBACK_CONFCB_UNHOLDFAILED:
                sCallback.mtcConfCbUnHoldFailed(dwConfId);
                break;
            case CALLBACK_CONFCB_HELD:
                sCallback.mtcConfCbHeld(dwConfId);

                break;
            case CALLBACK_CONFCB_UNHELD:
                sCallback.mtcConfCbUnHeld(dwConfId);
                break;
            case CALLBACK_CONFCB_ERROR:
                sCallback.mtcConfCbError(dwConfId, dwStatCode);
                break;
        }
    }
}
