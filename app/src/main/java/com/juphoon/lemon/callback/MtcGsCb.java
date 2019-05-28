/**
 * @file MtcGsCb.java
 * @brief MTC Geolocation Share Informatio callbacks Interface Functions
 */
 package com.juphoon.lemon.callback;

/**
 * @brief Class of MTC Geolocation Share Informatio callbacks
 */
public class MtcGsCb {

    /**
     * @brief MTC Geolocation Share Informatio callbacks
     *
     * In order to receive MTC Geolocation Share Informatio callbacks, user should implement this 
     * interface, then use @ref MtcGsCb.setCallback to register callbacks.
     */    
    public interface Callback {

        /**
         * @brief Set the gs push information receive invitation callback.
         * The callback will be notified if user receiving push information invitation.
         *
         * @param [in] gInfoId The geolocation information id. @ref MtcGsGinfo::Mtc_GsGInfoGetPartp.
         */
        void mtcGsCbPushGInfoRecvIvt(int gInfoId);

        /**
         * @brief Set the gs push information share ok callback.
         * The callback is one of @ref MtcGsGinfo::Mtc_GsGInfoPushText results.
         *
         * @param [in] gInfoId The geolocation information id. @ref MtcGsGinfo::Mtc_GsGInfoGetPartp.
         */
        void mtcGsCbPushGInfoShareOk(int gInfoId);

        /**
         * @brief Set the gs push information share failed callback.
         * The callback is one of @ref MtcGsGinfo::Mtc_GsGInfoPushText results.
         *
         * @param [in] gInfoId The geolocation information id. @ref MtcGsGinfo::Mtc_GsGInfoGetPartp.
         * @param [in] stateCode Status code.
         */
        void mtcGsCbPushGInfoShareFailed(int gInfoId, int stateCode);

        /**
         * @brief Set the gs push information receive done callback.
         * The callback will be notified if user received a geolocation information.
         *
         * @param [in] gInfoId The geolocation information id. @ref MtcGsGinfo::Mtc_GsGInfoGetPartp.
         */
        void mtcGsCbPushGInfoRecvDone(int gInfoId);

        /**
         * @brief Set the gs pull information receive invitation callback.
         * The callback will be notified if user receiving pull information invitation.
         *
         * @param [in] gInfoId The geolocation information id. @ref MtcGsGinfo::Mtc_GsGInfoGetPartp.
         */
        void mtcGsCbPullGInfoRecvIvt(int gInfoId);

        /**
         * @brief Set the gs pull information share ok callback.
         * The callback is one of @ref MtcGsGinfo::Mtc_GsGInfoPullViaFt results.
         *
         * @param [in] gInfoId The geolocation information id. @ref MtcGsGinfo::Mtc_GsGInfoGetPartp.
         */
        void mtcGsCbPullGInfoShareOk(int gInfoId);

        /**
         * @brief Set the gs pull information share failed callback.
         * The callback is one of @ref MtcGsGinfo::Mtc_GsGInfoPullViaFt results.
         *
         * @param [in] gInfoId The geolocation information id. @ref MtcGsGinfo::Mtc_GsGInfoGetPartp.
         * @param [in] stateCode Status code.
         */
        void mtcGsCbPullGInfoShareFailed(int gInfoId, int stateCode);

        /**
         * @brief Set the gs pull information share blocked callback.
         * The callback is one of @ref MtcGsGinfo::Mtc_GsGInfoPullViaFt results.
         *
         * @param [in] gInfoId The geolocation information id. @ref MtcGsGinfo::Mtc_GsGInfoGetPartp.
         */
        void mtcGsCbPullGInfoShareBlocked(int gInfoId);
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

    private static final int CALLBACK_PUSH_GINFORecvIvt = 0;
    private static final int CALLBACK_PUSH_GINFOShareOk = 1;
    private static final int CALLBACK_PUSH_GINFOShareFailed = 2;
    private static final int CALLBACK_PUSH_GINFORecvDone = 3;
    private static final int CALLBACK_PULL_GINFORecvIvt = 4;
    private static final int CALLBACK_PULL_GINFOShareOk = 5;
    private static final int CALLBACK_PULL_GINFOShareFailed = 6;
    private static final int CALLBACK_PULL_GINFOShareBlocked = 7;

    private static void mtcGsCbCallback(int function, int gInfoId, int stateCode) {
        switch (function) {
            case CALLBACK_PUSH_GINFORecvIvt:
                sCallback.mtcGsCbPushGInfoRecvIvt(gInfoId);
                break;
            case CALLBACK_PUSH_GINFOShareOk:
                sCallback.mtcGsCbPushGInfoShareOk(gInfoId);
                break;
            case CALLBACK_PUSH_GINFOShareFailed:
                sCallback.mtcGsCbPushGInfoShareFailed(gInfoId, stateCode);
                break;
            case CALLBACK_PUSH_GINFORecvDone:
                sCallback.mtcGsCbPushGInfoRecvDone(gInfoId);
                break;
            case CALLBACK_PULL_GINFORecvIvt:
                sCallback.mtcGsCbPullGInfoRecvIvt(gInfoId);
                break;
            case CALLBACK_PULL_GINFOShareOk:
                sCallback.mtcGsCbPullGInfoShareOk(gInfoId);
                break;
            case CALLBACK_PULL_GINFOShareFailed:
                sCallback.mtcGsCbPullGInfoShareFailed(gInfoId, stateCode);
                break;
            case CALLBACK_PULL_GINFOShareBlocked:
                sCallback.mtcGsCbPullGInfoShareBlocked(gInfoId);
                break;
        }
    }
}
