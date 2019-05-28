/**
 * @file MtcGrpCb.java
 * @brief MTC Group callbacks Interface Functions
 */
 package com.juphoon.lemon.callback;

/**
 * @brief Class of MTC Group callbacks
 */
public class MtcGrpCb {

    /**
     * @brief MTC group callbacks
     *
     * In order to receive MTC group callbacks, user should implement this 
     * interface, then use @ref MtcGrpCb.setCallback to register callbacks.
     */    
    public interface Callback {

        /**
         * @brief Set the OMA pre-arranged group set load all groups successfully callback.
         * The callback is one of @ref MtcGrp::Mtc_GrpsLoadAllGrp results
         *
         */
        void mtcGrpCbLoadAllOk();

        /**
         * @brief Set the OMA pre-arranged group set load failed callback.
         * The callback is one of @ref MtcGrp::Mtc_GrpsLoadAllGrp results
         *
         * @param [in] dwStatCode Error code .
         */
        void mtcGrpCbLoadAllFailed(int dwStatCode);

        /**
         * @brief Set the OMA pre-arranged group load successfully callback.
         * The callback is one of @ref MtcGrp::Mtc_GrpsLoadGrpU results
         *
         * @param [in] dwId Group Id.
         * @param [in] pcIdStr Group display name.
         */
        void mtcGrpCbLoadOk(int dwId, String pcIdStr);

        /**
         * @brief Set the OMA pre-arranged group load failed callback.
         * The callback is one of @ref MtcGrp::Mtc_GrpsLoadGrpU results
         *
         * @param [in] pcIdStr Group display name.
         * @param [in] dwStatCode Error code.
         */
        void mtcGrpCbLoadFailed(String pcIdStr, int dwStatCode);

        /**
         * @brief Set the pre-arranged group added successfully callback.
         * The callback is one of @ref MtcGrp::Mtc_GrpsAddGrp results
         *
         * @param [in] dwId Group display name.
         * @param [in] pcIdStr  Group uri.
         */
        void mtcGrpCbGrpAddOk(int dwId, String pcIdStr);

        /**
         * @brief Set the pre-arranged group added failed callback.
         * The callback is one of @ref MtcGrp::Mtc_GrpsAddGrp results
         *
         * @param [in] pcIdStr Group display name.
         * @param [in] dwStatCode Error code.
         */
        void mtcGrpCbGrpAddFailed(String pcIdStr, int dwStatCode);

        /**
         * @brief Set the pre-arranged group removed successfully callback.
         * The callback is one of @ref MtcGrp::Mtc_GrpsRmvGrp results
         *
         * @param [in] pcIdStr Group display name.
         */
        void mtcGrpCbGrpRmvOk(String pcIdStr);

        /**
         * @brief Set the pre-arranged group removed failed callback.
         * The callback is one of @ref MtcGrp::Mtc_GrpsRmvGrp results
         *
         * @param [in] dwId Group Id.
         * @param [in] pcIdStr Group display name.
         * @param [in] dwStatCode Error code.
         */
        void mtcGrpCbGrpRmvFailed(int dwId, String pcIdStr, int dwStatCode);

        /**
         * @brief Set the pre-arranged group entry added successfully callback.
         * The callback is one of @ref MtcGrp::Mtc_GrpAddEntry results
         *
         * @param [in] dwEntryId Group index.
         * @param [in] dwGrpId Group Id.
         * @param [in] pcIdStr Group display name.
         */
        void mtcGrpCbEntryAddOk(int dwEntryId, int dwGrpId, String pcIdStr);

        /**
         * @brief Set the pre-arranged group entry added failed callback.
         * The callback is one of @ref MtcGrp::Mtc_GrpAddEntry results
         *
         * @param [in] pcIdStr Group display name.
         * @param [in] dwGrpId Group Id.
         * @param [in] dwStatCode Error code.
         */
        void mtcGrpCbEntryAddFailed(String pcIdStr, int dwGrpId, int dwStatCode);

        /**
         * @brief Set the pre-arranged group entry removed successfully callback.
         * The callback is one of @ref MtcGrp::Mtc_GrpRmvEntry results
         *
         * @param [in] pcIdStr Group display name.
         * @param [in] dwGrpId Group Id.        
         */
        void mtcGrpCbEntryRmvOk(String pcIdStr, int dwGrpId);

        /**
         * @brief Set the pre-arranged group entry removed failed callback.
         * The callback is one of @ref MtcGrp::Mtc_GrpRmvEntry results
         *
         * @param [in] dwEntryId Group index.
         * @param [in] dwGrpId Group Id.
         * @param [in] pcIdStr Group display name.
         * @param [in] dwStatCode Error code.         
         */
        void mtcGrpCbEntryRmvFailed(int dwEntryId, int dwGrpId, String pcIdStr, int dwStatCode);
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

    private static final int CALLBACK_GRP_LOAD_ALL_OK = 0;
    private static final int CALLBACK_GRP_LOAD_ALL_FAILED = 1;
    private static final int CALLBACK_GRP_LOAD_OK = 2;
    private static final int CALLBACK_GRP_LOAD_FAILED = 3;
    private static final int CALLBACK_GRP_GRP_ADD_OK = 4;
    private static final int CALLBACK_GRP_GRP_ADD_FAILED = 5;
    private static final int CALLBACK_GRP_GRP_RMV_OK = 6;
    private static final int CALLBACK_GRP_GRP_RMV_FAILED = 7;
    private static final int CALLBACK_GRP_ENTRY_ADD_OK = 8;
    private static final int CALLBACK_GRP_ENTRY_ADD_FAILED = 9;
    private static final int CALLBACK_GRP_ENTRY_RMV_OK = 10;
    private static final int CALLBACK_GRP_ENTRY_RMV_FAILED = 11;

    /**
     * @brief Distribute call callbacks
     *
     * Distribute call callbacks
     */
    private static void mtcGrpCbCallback(int function, int dwEntryId, int dwGrpId, String pcIdStr, int dwStatCode) {
        switch (function) {
            case CALLBACK_GRP_LOAD_ALL_OK:
                sCallback.mtcGrpCbLoadAllOk();
                break;
            case CALLBACK_GRP_LOAD_ALL_FAILED:
                sCallback.mtcGrpCbLoadAllFailed(dwStatCode);
                break;
            case CALLBACK_GRP_LOAD_OK:
                sCallback.mtcGrpCbLoadOk(dwEntryId, pcIdStr);
                break;
            case CALLBACK_GRP_LOAD_FAILED:
                sCallback.mtcGrpCbLoadFailed(pcIdStr, dwStatCode);
                break;
            case CALLBACK_GRP_GRP_ADD_OK:
                sCallback.mtcGrpCbGrpAddOk(dwEntryId, pcIdStr);
                break;
            case CALLBACK_GRP_GRP_ADD_FAILED:
                sCallback.mtcGrpCbGrpAddFailed(pcIdStr, dwStatCode);
                break;
            case CALLBACK_GRP_GRP_RMV_OK:
                sCallback.mtcGrpCbGrpRmvOk(pcIdStr);
                break;
            case CALLBACK_GRP_GRP_RMV_FAILED:
                sCallback.mtcGrpCbGrpRmvFailed(dwEntryId, pcIdStr, dwStatCode);
                break;
            case CALLBACK_GRP_ENTRY_ADD_OK:
                sCallback.mtcGrpCbEntryAddOk(dwEntryId, dwGrpId, pcIdStr);
                break;
            case CALLBACK_GRP_ENTRY_ADD_FAILED:
                sCallback.mtcGrpCbEntryAddFailed(pcIdStr, dwGrpId, dwStatCode);
                break;
            case CALLBACK_GRP_ENTRY_RMV_OK:
                sCallback.mtcGrpCbEntryRmvOk(pcIdStr, dwGrpId);
                break;
            case CALLBACK_GRP_ENTRY_RMV_FAILED:
                sCallback.mtcGrpCbEntryRmvFailed(dwEntryId, dwGrpId, pcIdStr, dwStatCode);
                break;
        }
    }
}
