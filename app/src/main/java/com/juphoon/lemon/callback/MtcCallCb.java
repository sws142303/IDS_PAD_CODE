/**
 * @file MtcCallCb.java
 * @brief MTC call callbacks Interface Functions
 */
 package com.juphoon.lemon.callback;

/**
 * @brief Class of MTC call callbacks
 */
public class MtcCallCb {
    /**
     * @brief MTC call callbacks
     *
     * In order to receive MTC call callbacks, user should implement this 
     * interface, then use @ref MtcCallCb.setCallback to register callbacks.
     */
    public interface Callback {
        /**
         * @brief callback of indicate received a new call.
         *
         * GUI use like @ref MtcCall::Mtc_SessGetPeerUri, 
         * to get detail information and present those to user. And wait for 
         * user action, accept or decline.
         * "dwSessId" is the ID of session. GUI should use it to get session
         * status.
         * <p>
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
         * @param [in] dwSessId The ID of session. GUI should use it to get session
         *                 status. 
         */
        void mtcCallCbIncoming(int dwSessId);

        /**
         * @brief callback of indicate send a new call.
         *
         * GUI use like @ref MtcCall::Mtc_SessGetPeerUri, 
         * to get detail information and present those to user. And show a 
         * outgoing call window.
         * "dwSessId" is the ID of session. GUI should use it to get session
         * status.
         * <p>
         * - Use @ref MtcCall::Mtc_SessHasVideo to check if outgoing call has video request.
         *   - Has video, show video call window
         *   - No Video, show voice call window
         * - Get peer information by @ref MtcCall::Mtc_SessGetPeerUri.
         *
         * @param dwSessId The ID of session. 
         */
        void mtcCallCbOutgoing(int dwSessId);

        /**
         * @brief callback of indicate the callee is ringing.
         *
         * GUI should update the status of outgoing call window
         * "dwSessId" is the ID of session. GUI should use it to locate call 
         * window.
         * <p>
         * - Use @ref MtcCall::Mtc_SessGetEarlyMediaStatus and 
         *   @ref MtcCall::Mtc_SessHasVideo to check if there is a video early media. 
         *   If so, use @ref MtcCall::Mtc_SessHasOfferAnswer to check if offer answer
         *   exchanged is completed. If so, GUI should invoke 
         *   @ref MtcCall::Mtc_SessVideoStart to start receive video early media.
         *
         * "dwAlertType" has below values:
         * - MTC_CALL_ALERT_RING Alerted by 180 ringing.
         * - MTC_CALL_ALERT_QUEUE Alerted by 182 queued.
         * - MTC_CALL_ALERT_PROGRESS Alerted by 183 session progress.
         *
         * @param [in] dwSessId The ID of session.
         * @param [in] dwAlertType The alter type.
         */
        void mtcCallCbAlerted(int dwSessId, int dwAlertType);

        
        /**
         * @brief callback of indicate the callee receive prack.
         *
         * GUI may use reliable provision response to check if peer is still 
         * alive. If do so, GUI should show a incoming call view in this 
         * callback instead incoming callback. GUI should update the status of 
         * outgoing call window.
         * "dwSessId" is the ID of session. GUI should use it to locate call 
         * window.
         *
         * @param dwSessId The ID of session.
         */
        void mtcCallCbPracked(int dwSessId);

        /**
         * @brief callback of indicate the call has established.
         *
         * The call can be a received call or a send out call. GUI should switch 
         * to talking window.
         * "dwSessId" is the ID of session. GUI should use it to locate call 
         * window.
         * - Use @ref MtcCall::Mtc_SessHasVideo to check if there is a video media. 
         *   If so, GUI should invoke @ref MtcCall::Mtc_SessVideoStart to start send and
         *   receive video media.
         *
         * @param [in] dwSessId The ID of session.
         */
        void mtcCallCbTalking(int dwSessId);

        /**
         * @brief callback of indicate call is terminated.
         *
         * "dwStatCode" has below values.
         * - MTC_CALL_TERM_BYE Terminated by remote BYE
         * - MTC_CALL_TERM_CANCEL Terminated by remote CANCEL
         * - MTC_CALL_TERM_BUSY Terminated by 486 Busy Here
         * - MTC_CALL_TERM_DECLINE Terminated by 603 Decline
         * - MTC_CALL_ERR_FORBIDDEN Terminated by 403 Forbidden
         * - MTC_CALL_ERR_NOT_FOUND Terminated by 404 Not Found
         * - MTC_CALL_ERR_NOT_ACPTED Terminated by 406 Not Acceptable or 488 Not Acceptable Here
         * - MTC_CALL_ERR_REQ_TERMED Terminated by 487 Request Terminated
         * - MTC_CALL_ERR_INTERNAL_ERR Terminated by 500 Server Internal Error
         * - MTC_CALL_ERR_SRV_UNAVAIL Terminated by 503 Service Unavailable
         * - MTC_CALL_ERR_NOT_EXIST Terminated by 604 Does Not Exist Anywhere
         * The call can be a received call or a send out call. GUI should show 
         * the reason why the call was terminated for a few seconds, and switch to
         * main working window.
         * "dwSessId" is the ID of session. GUI should use it to locate call 
         * window.
         *
         * @param [in] dwSessId The ID of session.
         * @param [in] dwStatCode The terminate state code.
         */
        void mtcCallCbTermed(int dwSessId, int dwStatCode);

        /**
         * @brief callback of indicate call modifying accepted.
         *
         * To change the attribute of media stream is called call modify, 
         * includes hold and add or remove media stream. This callback indicates 
         * the modification is accept by peer. GUI should check which service 
         * has been invoke by user. If user hold or un-hold the call, show hold 
         * or un-hold successfully. If user add video upon a voice call, then 
         * switch to video call window.
         * "dwSessId" is the ID of session. GUI should use it to locate call 
         * window.
         *
         * @param [in] dwSessId The ID of session.
         */
        void mtcCallCbMdfyAcpt(int dwSessId);

        /**
         * @brief callback of indicate call modifyed.
         *
         * This callback indicates modification was invoke by peer, and this 
         * modification is completed. GUI use like 
         * @ref MtcCall::Mtc_SessHasVideo, check media status.
         * And then show corresponding changes.
         * "dwSessId" is the ID of session. GUI should use it to locate call 
         * window.
         *
         * @param [in] dwSessId The ID of session.
         */
        void mtcCallCbMdfyed(int dwSessId);

        /**
         * @brief callback of indicate call modify request.
         *
         * This callback indicates modification was invoke by peer, and this 
         * modification is waiting for user's confirm. GUI use 
         * like @ref MtcCall::Mtc_SessHasAudio and
         * @ref MtcCall::Mtc_SessHasVideo, to check media status and make a decision 
         * whether accept one or all of active these streams.
         * "dwSessId" is the ID of session. GUI should use it to locate call
         * window.
         *
         * @param [in] dwSessId The ID of session.
         */
        void mtcCallCbMdfyReq(int dwSessId);

        /**
         * @brief callback of indicate call hold successfully.
         *
         * This callback indicates hold operation has been completed 
         * successfully.
         * GUI should update session status according to session ID.
         * "dwSessId" is the ID of session. GUI should use it to locate call
         * window.
         *
         * @param [in] dwSessId The ID of session.
         */
        void mtcCallCbHoldOk(int dwSessId);

        /**
         * @brief callback of indicate call hold failed.
         *
         * This callback indicates hold operation has been failed.
         * GUI should update session status according to session ID.
         * "dwSessId" is the ID of session. GUI should use it to locate call
         * window.
         *
         * @param [in] dwSessId The ID of session.
         */
        void mtcCallCbHoldFailed(int dwSessId);

        /**
         * @brief callback of indicate call un-hold successfully.
         *
         * This callback indicates un-hold operation has been completed 
         * successfully.
         * GUI should update session status according to session ID.
         * "dwSessId" is the ID of session. GUI should use it to locate call
         * window.
         *
         * @param [in] dwSessId The ID of session.
         */
        void mtcCallCbUnHoldOk(int dwSessId);

        /**
         * @brief callback of indicate call un-hold failed.
         *
         * This callback indicates un-hold operation has been failed.
         * GUI should update session status according to session ID.
         * "dwSessId" is the ID of session. GUI should use it to locate call
         * window.
         *
         * @param [in] dwSessId The ID of session.
         */
        void mtcCallCbUnHoldFailed(int dwSessId);

        /**
         * @brief callback of indicate call was held by peer.
         *
         * This callback indicates peer hold the call.
         * GUI should update session status according to session ID.
         * "dwSessId" is the ID of session. GUI should use it to locate call
         * window.
         *
         * @param dwSessId The ID of session.
         */
        void mtcCallCbHeld(int dwSessId);

        /**
         * @brief callback of indicate call was un-held by peer.
         *
         * This callback indicates un-held hold the call.
         * GUI should update session status according to session ID.
         * "dwSessId" is the ID of session. GUI should use it to locate call
         * window.
         *
         * @param dwSessId The ID of session.
         */
        void mtcCallCbUnHeld(int dwSessId);

        /**
         * @brief callback of indicate call refered.
         *
         * This callback indicates the session is transfered by peer to a new
         * destination. The new session will be indicated by callback of 
         * @ref mtcCallCbOutgoing.
         * "dwSessId" is the ID of session. GUI should use it to locate call
         * window.
         *
         * @param [in] dwSessId The ID of session.
         */
        void mtcCallCbRefered(int dwSessId);

        /**
         * @brief callback of indicate call transferred accepted.
         *
         * This callback indicates receiving a transfer request. GUI should show
         * a dialog to inform user, current call was transfered.
         * "dwSessId" is the ID of session. GUI should use it to locate call
         * window.
         *
         * @param [in] dwSessId The ID of session.
         */
        void mtcCallCbTrsfAcpt(int dwSessId);

        /**
         * @brief callback of indicate call transferred terminated.
         *
         * For extension.
         *
         * @param [in] dwSessId The ID of session.
         */
        void mtcCallCbTrsfTerm(int dwSessId);

        /**
         * @brief callback of indicate call transferred failed.
         *
         * This callback indicates transfer failed. GUI should show a dialog to 
         * inform user, current call was transfered.
         * "dwSessId" is the ID of session. GUI should use it to locate call
         * window.
         *
         * @param [in] dwSessId The ID of session.
         */
        void mtcCallCbTrsfFailed(int dwSessId);

        /**
         * @brief callback of indicate call redirected.
         *
         * This callback indicates the outgoing call was forwarded to another
         * number. GUI should update call status.
         * "dwSessId" is the ID of session. GUI should use it to locate call 
         * window.
         *
         * @param dwSessId The ID of session.
         */
        void mtcCallCbRedirect(int dwSessId);
        
        /**
         * @brief callback of indicate receive info.
         *
         * This callback indicates the call has replaced by another call, which
         * should happen when previous call was transfered to another peer. GUI
         * should update the peer information.
         * "dwSessId" is the ID of session, GUI should use it to locate call 
         * window.
         *
         * @param [in] dwSessId The ID of session.
         * @param [in] info The info string.
         */
        void mtcCallCbInfo(int dwSessId, String info);
    
        /**
         * @brief Set callback of indicate call replaced.
         *
         * This callback indicates the call has replaced by another call, which
         * should happen when previous call was transfered to another peer. GUI
         * should update the peer information.
         * "dwSessId"  is the ID of session replaced, 
         * GUI should use it to locate call window.
         * "dwNewSessId" is the new session ID. 
         *
         * @param [in] dwSessId session id.
         */
        void mtcCallCbReplaced(int dwSessId);
        
        /**
         * @brief Set callback of indicate call replace ok.
         *
         * This callback indicates the call has replace ok by another call, which
         * should happen when previous call was transfered to another peer. GUI
         * should update the peer information.
         * "dwSessId" is the ID of session replace ok, 
         * GUI should use it to locate call window.
         * "dwNewSessId" is the new session ID. 
         *
         * @param [in] dwSessId Callback function.
         */
        void mtcCallCbReplaceOk(int dwSessId);
        
        /**
         * @brief Set callback of indicate call replace failed.
         *
         * This callback indicates the call has replace failed by another call, which
         * should happen when previous call was transfered to another peer. GUI
         * should update the peer information.
         * "dwSessId" is the ID of session replace failed, 
         * GUI should use it to locate call window.
         * "dwNewSessId" is the new session ID. 
         *
         * @param [in] dwSessId Callback function.
         */
        void mtcCallCbReplaceFailed(int dwSessId);

		/**
		 * @brief callback of indicate rtp connectivity.
		 *
		 * This callback indicates the RTP connectivity status is changed.
		 *
		 * @param [in] dwSessId is the ID of session, 
		 * GUI should use it to locate call window.
		 *
		 * @param [in] bConned When ZTRUE indicate media
		 * engine detected RTP packet received, ZFALSE indicate media engine hasn't
		 * detected RTP packet received for about 20 seconds.
		 */
        void mtcCallCbSetRtpConnectivity(int dwSessId, boolean bConnected);
    
        /**
         * @brief Set callback of indicate camera has discinnected.
         *
         * This callback indicates the camera has disconnected with this call.
         * "dwSessId" is the ID of session. GUI should use it to locate call
         * window.
         *
         * @param [in] dwSessId The ID of session.
         */        
         void mtcCallCbCamDisconned(int dwSessId);

        /**
         * @brief callback of indicate call's video size changed.
         *
         * This callback indicates the call's video size changed.
         * "dwSessId" is the ID of session. GUI should use it to locate call 
         * window.
         * "dwWidth" indicate the width of frame.
         * "dwHeight" indicate the height of frame.
         * "dwOrientation" indicate the orientation.
         * @ref MtcMediaConstants::EN_MTC_ORIENTATION_PORTRAIT
         *
         * @param [in] dwSessId The ID of session.
         * @param [in] dwWidth The width of frame.
         * @param [in] dwHeight The height of frame.
         * @param [in] dwOrientation The orientation.
         */
        void mtcCallCbVideoSize(int dwSessId, int dwWidth, int dwHeight, int dwOrientation);
        
        /**
         * @brief callback of indicate call's network status.
         *
         * This callback indicates the call's network status.
         * GUI should update session status according to session ID.
         * "dwSessId" is the ID of session. GUI should use it to locate call 
         * window.
         * "bVideo" indicate if the network status of video stream is changed.
         * "bSend" indicate if the network status of sending direction is changed.
         * "iType" indicate the network status type 
         * @ref MtcMediaConstants::EN_MTC_NET_STATUS_BAD.
         * "iReason" indicate the reason type network status 
         * @ref MtcMediaConstants::EN_MTC_NET_STATUS_REASON_NORMAL.
         *
         * @param [in] dwSessId The ID of session.
         * @param [in] bVideo The video network status flag.
         * @param [in] bSend The sending direction network status flag.
         * @param [in] iType The network status type.
         * @param [in] iReason The reason type network status.
         */
        void mtcCallCbNetStaChanged(int dwSessId, boolean bVideo,
                                    boolean bSend, int iType, int iReason);

        /**
         * @brief callback of indicate call's video incoming status.
         *
         * This callback indicates the call's video incoming status.
         * "dwSessId" is the ID of session. GUI should use it to locate call 
         * window.
         * "dwParm1" indicate the framerate.
         * "dwParm2" indicate the bitrate.
         *
         * @param [in] dwSessId The ID of session.
         * @param [in] dwParm1 The framerate.
         * @param [in] dwParm2 The bitrate.
         */
        void mtcCallCbVideoIncomingSta(int dwSessId, int dwParm1, int dwParm2);

        /**
         * @brief callback of indicate call's video outgoing status.
         *
         * This callback indicates the call's video outgoing status.
         * "dwSessId" is the ID of session. GUI should use it to locate call 
         * window.
         * "dwParm1" indicate the framerate.
         * "dwParm2" indicate the bitrate.
         *
         * @param [in] dwSessId The ID of session.
         * @param [in] dwParm1 The framerate.
         * @param [in] dwParm2 The bitrate.
         */
        void mtcCallCbVideoOutgoingSta(int dwSessId, int dwParm1, int dwParm2);

        /**
         * @brief callback of indicate call's video protection status.
         *
         * This callback indicates the call's video protection status.
         * "dwSessId" is the ID of session. GUI should use it to locate call 
         * window.
         * "dwParm1" indicate the FEC bitrate in kbps.
         * "dwParm2" indicate the NACK bitrate in kbps.
         *
         * @param [in] dwSessId The ID of session.
         * @param [in] dwParm1 The FEC bitrate in kbps.
         * @param [in] dwParm2 The NACK bitrate in kbps.
         */
        void mtcCallCbVideoProtectSta(int dwSessId, int dwParm1, int dwParm2);

        /**
         * @brief callback of indicate capture framerate.
         *
         * This callback indicates the capture statistics.
         * "dwSessId" is the ID of session. GUI should use it to locate call 
         * window.
         * "dwParm" indicate the framerate.
         *
         * @param [in] dwSessId The ID of session.
         * @param [in] dwParm The framerate.
         */
        void mtcCallCbCaptureFramerate(int dwSessId, int dwParm);

        /**
         * @brief callback of indicate capture framerate.
         *
         * This callback indicates the capture size.
         * "dwSessId" is the ID of session. GUI should use it to locate call 
         * window.
         * "dwWidth" indicate capture width.
         * "dwHeight" indicate capture height.
         *
         * @param [in] dwSessId The ID of session.
         * @param [in] dwWidth The capture width.
         * @param [in] dwHeight The capture height.
         */
        void MtcCallCbCaptureSize(int dwSessId, int dwWidth, int dwHeight);

        /**
         * @brief callback of indicate call error.
         *
         * This callback indicates error occured during call. GUI should show
         * the detail error information to user.
         * "dwSessId" is the ID of the session which occurs error.
         * "dwStatCode" is the error code, it can be
         * - If dwStatCode < 0x1000, contains SIP response status code, use
         *      Sip_ReasonFromCode to get description string
         * - Otherwise, use @ref MtcCall::Mtc_SessGetStatDesc to get description string
         *
         * @param [in] dwSessId The ID of session.
         * @param [in] dwStatCode The error code.
         */
        void mtcCallCbSetError(int dwSessId, int dwStatCode);
    }

    /**
     * @brief MTC call callback init callbacks.
     *
     * This interface will call the native method to register call callback to 
     * MTC.
     */
    private static native void initCallback();

    /**
     * @brief MTC call callback destory callbacks.
     *
     * This interface will call the native method to deregister call callback to 
     * MTC.
     */
    private static native void destroyCallback();

    static Callback sCallback;

    /**
     * @brief MTC call callback set callbacks.
     *
     * Set the active call callback instance which to receive call callbacks.
     * Use null to deregister all call callbacks.
     *
     * @param [in] c The call callback instance.
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

    private static final int CALLBACK_INCOMING = 0;
    private static final int CALLBACK_OUTGOING = 1;
    private static final int CALLBACK_ALERTED = 2;
    private static final int CALLBACK_PRACKED = 3;
    private static final int CALLBACK_TALKING = 4;
    private static final int CALLBACK_TERMED = 5;
    private static final int CALLBACK_MDFY_ACPT = 6;
    private static final int CALLBACK_MDFYED = 7;
    private static final int CALLBACK_MDFY_REQ = 8;
    private static final int CALLBACK_HOLD_OK = 9;
    private static final int CALLBACK_HOLD_FAILED = 10;
    private static final int CALLBACK_UNHOLD_OK = 11;
    private static final int CALLBACK_UNHOLD_FAILED = 12;
    private static final int CALLBACK_HELD = 13;
    private static final int CALLBACK_UNHELD = 14;
    private static final int CALLBACK_REFERED = 15;
    private static final int CALLBACK_TRSF_ACPT = 16;
    private static final int CALLBACK_TRSF_TERM = 17;
    private static final int CALLBACK_TRSF_FAILED = 18;
    private static final int CALLBACK_REDIRECT = 19;
    private static final int CALLBACK_INFO = 20;
    private static final int CALLBACK_REPLACED = 21;
    private static final int CALLBACK_REPLACEOK = 22;
    private static final int CALLBACK_REPLACEFAILED = 23;
    private static final int CALLBACK_RTP_CONNECTIVITY = 24;
    private static final int CALLBACK_CAM_DISCONNED = 25;
    private static final int CALLBACK_VIDEO_SIZE = 26;
    private static final int CALLBACK_NET_STA_CHANGED = 27;
    private static final int CALLBACK_VIDEO_INCOMING_STA = 28;
    private static final int CALLBACK_VIDEO_OUTGOING_STA = 29;
    private static final int CALLBACK_VIDEO_PROTECT_STA = 30;
    private static final int CALLBACK_CAPTURE_FRAMERATE = 31;
    private static final int CALLBACK_CAPTURE_SIZE = 32;
    private static final int CALLBACK_SET_ERROR = 33;

    /**
     * @brief Distribute call callbacks
     *
     * Distribute call callbacks
     */
    private static void mtcCallCbCallback(int function, int arg1, int arg2, int arg3, int arg4, String arg5, boolean arg6, boolean arg7) {
        switch (function) {
            case CALLBACK_INCOMING:
                sCallback.mtcCallCbIncoming(arg1);
                break;
            case CALLBACK_OUTGOING:
                sCallback.mtcCallCbOutgoing(arg1);
                break;
            case CALLBACK_ALERTED:
                sCallback.mtcCallCbAlerted(arg1, arg2);
                break;
            case CALLBACK_PRACKED:
                sCallback.mtcCallCbPracked(arg1);
                break;
            case CALLBACK_TALKING:
                sCallback.mtcCallCbTalking(arg1);
                break;
            case CALLBACK_TERMED:
                sCallback.mtcCallCbTermed(arg1, arg2);
                break;
            case CALLBACK_MDFY_ACPT:
                sCallback.mtcCallCbMdfyAcpt(arg1);
                break;
            case CALLBACK_MDFYED:
                sCallback.mtcCallCbMdfyed(arg1);
                break;
            case CALLBACK_MDFY_REQ:
                sCallback.mtcCallCbMdfyReq(arg1);
                break;
            case CALLBACK_HOLD_OK:
                sCallback.mtcCallCbHoldOk(arg1);
                break;
            case CALLBACK_HOLD_FAILED:
                sCallback.mtcCallCbHoldFailed(arg1);
                break;
            case CALLBACK_UNHOLD_OK:
                sCallback.mtcCallCbUnHoldOk(arg1);
                break;
            case CALLBACK_UNHOLD_FAILED:
                sCallback.mtcCallCbUnHoldFailed(arg1);
                break;
            case CALLBACK_HELD:
                sCallback.mtcCallCbHeld(arg1);
                break;
            case CALLBACK_UNHELD:
                sCallback.mtcCallCbUnHeld(arg1);
                break;
            case CALLBACK_REFERED:
                sCallback.mtcCallCbRefered(arg1);
                break;
            case CALLBACK_TRSF_ACPT:
                sCallback.mtcCallCbTrsfAcpt(arg1);
                break;
            case CALLBACK_TRSF_TERM:
                sCallback.mtcCallCbTrsfTerm(arg1);
                break;
            case CALLBACK_TRSF_FAILED:
                sCallback.mtcCallCbTrsfFailed(arg1);
                break;
            case CALLBACK_REDIRECT:
                sCallback.mtcCallCbRedirect(arg1);
                break;
            case CALLBACK_INFO:
                sCallback.mtcCallCbInfo(arg1,arg5);
                break;
            case CALLBACK_REPLACED:
                sCallback.mtcCallCbReplaced(arg1);
                break;
            case CALLBACK_REPLACEOK:
                sCallback.mtcCallCbReplaceOk(arg1);
                break;
            case CALLBACK_REPLACEFAILED:
                sCallback.mtcCallCbReplaceFailed(arg1);
                break;
            case CALLBACK_RTP_CONNECTIVITY:
                sCallback.mtcCallCbSetRtpConnectivity(arg1, arg6);
                break;
            case CALLBACK_CAM_DISCONNED:
                sCallback.mtcCallCbCamDisconned(arg1);
                break;
            case CALLBACK_VIDEO_SIZE:
                sCallback.mtcCallCbVideoSize(arg1, arg2, arg3, arg4);
                break;
            case CALLBACK_NET_STA_CHANGED:
                sCallback.mtcCallCbNetStaChanged(arg1, arg6, arg7, arg2, arg3);
                break;
            case CALLBACK_VIDEO_INCOMING_STA:
                sCallback.mtcCallCbVideoIncomingSta(arg1, arg2, arg3);
                break;
            case CALLBACK_VIDEO_OUTGOING_STA:
                sCallback.mtcCallCbVideoOutgoingSta(arg1, arg2, arg3);
                break;
            case CALLBACK_VIDEO_PROTECT_STA:
                sCallback.mtcCallCbVideoProtectSta(arg1, arg2, arg3);
                break;
            case CALLBACK_CAPTURE_FRAMERATE:
                sCallback.mtcCallCbCaptureFramerate(arg1, arg2);
                break;
            case CALLBACK_CAPTURE_SIZE:
                sCallback.MtcCallCbCaptureSize(arg1, arg2, arg3);
                break;
            case CALLBACK_SET_ERROR:
                sCallback.mtcCallCbSetError(arg1, arg2);
                break;
        }
    }
}
