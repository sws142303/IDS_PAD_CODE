/**
 * @file MtcVShareCb.java
 * @brief MTC videoshare callbacks Interface Functions
 */
package com.juphoon.lemon.callback;

/**
 * @brief Class of MTC videoshare callbacks
 */
public class MtcVShareCb {

    /**
     * @brief MTC videoshare callbacks
     *
     * In order to receive MTC videoshare callbacks, user should implement this 
     * interface, then use @ref MtcVShareCb.setCallback to register callbacks.
     */    
    public interface Callback {

        /**
        * @brief Set callback of indicate received a new call.
        *
        * GUI use "MTC Session Interfaces", like 
        * @ref MtcCall::Mtc_SessGetPeerUri, to get detail information and present 
        * those to user. And wait for user action, accept or decline.
        * dwSessId is the ID of session. GUI should use it to get session
        *        status. 
        * - Use @ref MtcCall::Mtc_SessPeerOfferVideo to check if incoming call has 
        *   video request.
        *   - Has video, show video call alert window with 3 options
        *     - accept
        *     - accept with video
        *     - decline
        *   - No Video, show voice call alert window with 2 options
        *     - accept
        *     - decline
        * - Assign dwSessId to alert window. It will be used when invoke
        *   @ref MtcCall::Mtc_SessAnswer or @ref MtcCall::Mtc_SessTerm.
        * - Get peer information by @ref MtcCall::Mtc_SessGetPeerUri.
        *
        * @param [in] sessionId the ID of session. GUI should use it to get 
        * session status.
        */
        void mtcVShareCbIncoming(int sessionId);

        /**
        * @brief Set callback of indicate send a new call.
        *
        * GUI use "MTC Session Interfaces", like 
        * @ref MtcCall::Mtc_SessGetPeerUri, to get detail information and present 
        * those to user. And show a outgoing call window.
        * "dwSessId" is the ID of session. GUI should use it to get session
        *        status. 
        * - Use @ref MtcCall::Mtc_SessHasVideo to check if outgoing call has video 
        *   request.
        *   - Has video, show video call window
        *   - No Video, show voice call window
        * - Get peer information by @ref MtcCall::Mtc_SessGetPeerUri.
        *
        * @param [in] sessionId the ID of session. GUI should use it to get 
        * session status
        */
        void mtcVShareCbOutgoing(int sessionId);

        /**
        * @brief Set callback of indicate the callee is ringing.
        *
        * GUI should update the status of outgoing call window
        * "dwSessId" is the ID of session. GUI should use it to locate
        * call window.
        * - Use @ref MtcCall::Mtc_SessGetEarlyMediaStatus and @ref 
        *   MtcCall::Mtc_SessHasVideo to check 
        *   if there is a video early media. If so, use @ref 
        *   MtcCall::Mtc_SessHasOfferAnswer
        *   to check if offer answer exchanged is completed. If so, GUI should
        *   invoke @ref MtcCall::Mtc_SessVideoStart to start receive video early 
        *   media.
        *
        * @param [in] sessionId the ID of session. GUI should use it to get 
        * session status
        * @param [in] argInt  Alert type, you have has below values to use
        * - MTC_VSHARE_ALERT_RING Alerted by 180 ringing.
        * - MTC_VSHARE_ALERT_QUEUED Alerted by 182 queued.
        * - MTC_VSHARE_ALERT_PROGRESS Alerted by 183 session progress.
        */
        void mtcVShareCbAlerted(int sessionId, int argInt);


        /**
        * @brief Set callback of indicate the call has established.
        *
        * The call can be a received call or a send out call. GUI should switch 
        * to talking window.
        * "dwSessId" is the ID of session. GUI should use it to locate
        * call window.
        * - Use @ref MtcCall::Mtc_SessHasVideo to check if there is a video media. 
        *   If so, GUI should invoke @ref MtcCall::Mtc_SessVideoStart to start send 
        *   and receive video media.
        *
        * @param [in] sessionId the ID of session. GUI should use it to get 
        * session status
        */
        void mtcVShareCbTalking(int sessionId);

        /**
        * @brief Set callback of indicate call is terminated.
        *
        * "dwStatCode" has below values.
        * - MTC_VSHARE_TERM_BYE Terminated by remote BYE
        * - MTC_VSHARE_TERM_CANCEL Terminated by remote CANCEL
        * - MTC_VSHARE_TERM_BUSY Terminated by 486 Busy Here
        * - MTC_VSHARE_TERM_DECLINE Terminated by 603 Decline
        * - MTC_VSHARE_ERR_FORBIDDEN Terminated by 403 Forbidden
        * - MTC_VSHARE_ERR_NOT_FOUND Terminated by 404 Not Found
        * - MTC_VSHARE_ERR_NOT_ACPTED Terminated by 406 Not Acceptable or 488 
        *   Not Acceptable Here
        * - MTC_VSHARE_ERR_REQ_TERMED Terminated by 487 Request Terminated
        * - MTC_VSHARE_ERR_INTERNAL_ERR Terminated by 500 Server Internal Error
        * - MTC_VSHARE_ERR_SRV_UNAVAIL Terminated by 503 Service Unavailable
        * - MTC_VSHARE_ERR_NOT_EXIST Terminated by 604 Does Not Exist Anywhere
        * The call can be a received call or a send out call. GUI should show 
        * the reason why the call was terminated for a few seconds, and switch 
        * to main working window.
        * "dwSessId" the ID of session. GUI should use it to locate
        * call window.
        *
        * @param [in] sessionId the ID of session. GUI should use it to get 
        * session status.
        * @param [in] argInt the dwStatCode.
        */
        void mtcShareCbTermed(int sessionId, int argInt);

        /**
        * @brief Set callback of indicate video share's video size changed.
        *
        * @param [in] dwSessId the ID of session. GUI should use it to get 
        * session status.
        * @param [in] dwWidth indicate the width of frame.
        * @param [in] dwHeight indicate the height of frame.
        * @param [in] dwOrientation" indicate the orientation 
        * @ref MtcMediaConstants::EN_MTC_ORIENTATION_PORTRAIT.
        */
        void mtcShareCbVideoSize(int dwSessId, int dwWidth, int dwHeight, int dwOrientation);

        /**
        * @brief Set callback of indicate capture size.
        *
        * This callback indicates the capture size.
        * "dwSessId" is the ID of session.
        * GUI should use it to locate call window.
        * "dwWidth" indicate capture width.
        * "dwHeight" indicate capture height.
        *
        * @param [in] dwSessId the ID of session. GUI should use it to get 
        * session status.
        * @param [in] dwWidth indicate capture width.
        * @param [in] dwHeight  indicate capture height.
        */
        void mtcShareCbCaptureSize(int dwSessId, int dwWidth, int dwHeight);

        /**
        * @brief Set callback of indicate call error.
        *
        * This callback indicates error occured during call. GUI should show
        * the detail error information to user.
        * "dwSessId" is the ID of the session which occurs error.
        * "dwStatCode" is the error code(see MSF_STAT_ERR_NO)
        * - If dwStatCode < 0x1000, contains SIP response status code, use
        *      Sip_ReasonFromCode to get description string
        *
        * @param [in] sessionId the ID of session. GUI should use it to get 
        * session status.
        * @param [in] argInt dwStatCode.
        */
        void mtcVShareCbError(int sessionId, int argInt);
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

    private static final int CALLBACK_VSHARECB_INCOMING = 0;
    private static final int CALLBACK_VSHARECB_OUTGOING = 1;
    private static final int CALLBACK_VSHARECB_ALERTED = 2;
    private static final int CALLBACK_VSHARECB_TALKING = 3;
    private static final int CALLBACK_VSHARECB_TERMED = 4;
    private static final int CALLBACK_VSHARECB_VIDEO_SIZE = 5;
    private static final int CALLBACK_VSHARECB_CAPTURE_SIZE = 6;
    private static final int CALLBACK_VSHARECB_ERROR = 7;

    /**
     * @brief Distribute call callbacks
     *
     * Distribute call callbacks
     */
    private static void mtcVShareCbCallback(int function, int sessionId, int arg1, int arg2, int arg3) {
        switch (function) {
            case CALLBACK_VSHARECB_INCOMING:
                sCallback.mtcVShareCbIncoming(sessionId);
                break;
            case CALLBACK_VSHARECB_OUTGOING:
                sCallback.mtcVShareCbOutgoing(sessionId);
                break;
            case CALLBACK_VSHARECB_ALERTED:
                sCallback.mtcVShareCbAlerted(sessionId, arg1);
                break;
            case CALLBACK_VSHARECB_TALKING:
                sCallback.mtcVShareCbTalking(sessionId);
                break;
            case CALLBACK_VSHARECB_TERMED:
                sCallback.mtcShareCbTermed(sessionId, arg1);
                break;
            case CALLBACK_VSHARECB_VIDEO_SIZE:
                sCallback.mtcShareCbVideoSize(sessionId, arg1, arg2, arg3);
                break;
            case CALLBACK_VSHARECB_CAPTURE_SIZE:
                sCallback.mtcShareCbCaptureSize(sessionId, arg1, arg2);
                break;
            case CALLBACK_VSHARECB_ERROR:
                sCallback.mtcVShareCbError(sessionId, arg1);
                break;
        }
    }
}
