/**
 * @file MtcBuddyCb.java
 * @brief MTC buddies callbacks Interface Functions
 */
 package com.juphoon.lemon.callback;

/**
 * @brief Class of MTC buddies callbacks
 */
public class MtcBuddyCb {
    /**
     * @brief MTC buddies callbacks
     *
     * In order to receive MTC buddies callbacks, user should implement this 
     * interface, then use @ref MtcBuddyCb.setCallback to register callbacks.
     */    
    public interface Callback {

        /**
         * @brief Set the OMA buddy set load all buddies successfully callback.
         * The callback is one of @ref MtcBuddy::Mtc_BuddysLoadAll results
         *
         */
        void mtcBuddyCbAllLoadOk();

        /**
         * @brief Set the OMA buddy set load failed callback.
         * The callback is one of @ref MtcBuddy::Mtc_BuddysLoadAll results
         *
         */
        void mtcBuddyCbAllLoadFailed(int dwStatCode);

        /**
         * @brief Set the OMA buddy set upload all buddies successfully callback.
         * The callback is one of @ref MtcBuddy::Mtc_BuddysUploadAll results
         *
         */
        void mtcBuddyCbAllUploadOk();

        /**
         * @brief Set the OMA buddy set upload failed callback.
         * The callback is one of @ref MtcBuddy::Mtc_BuddysUploadAll results
         *
         * @param [in] dwStatCode status code.
         */
        void mtcBuddyCbAllUploadFailed(int dwStatCode);

        /**
         * @brief Set the OMA buddy set load a group buddies successfully callback.
         * The callback is one of @ref MtcBuddy::Mtc_BuddysUpload results
         *
         * @param [in] dwGrpType group type.
         */
        void mtcBuddyCbRcsAllLoadOk(int dwGrpType);

        /**
         * @brief Set the OMA buddy set load a group buddies failed callback.
         * The callback is one of @ref MtcBuddy::Mtc_BuddysUpload results
         *
         * @param [in] dwGrpType group type.
         * @param [in] dwStatCode status code.
         */
        void mtcBuddyCbRcsAllLoadFailed(int dwGrpType, int dwStatCode);

        /**
         * @brief Set the OMA buddy set upload a group buddies successfully callback.
         * The callback is one of @ref MtcBuddy::Mtc_BuddysUpload results
         *
         * @param [in] dwGrpType group type.
         */
        void mtcBuddyCbRcsAllUploadOk(int dwGrpType);

        /**
         * @brief Set the OMA buddy set upload a group buddies failed callback.
         * The callback is one of @ref MtcBuddy::Mtc_BuddysUpload results
         *
         * @param [in] dwGrpType group type.
         * @param [in] dwStatCode status code.

         */
        void mtcBuddyCbRcsAllUploadFailed(int dwGrpType, int dwStatCode);

        /**
         * @brief Set the group added successfully callback.
         * The callback is one of @ref MtcBuddy::Mtc_BuddysAddGrp results
         *
         * @param [in] dwGrpType group type.
         * @param [in] dwId group Id.
         * @param [in] pcIdStr group Id str.
         *
         */
        void mtcBuddyCbGrpAddOk(int dwGrpType, int dwId, String pcIdStr);

        /**
         * @brief Set the group added failed callback.
         * The callback is one of @ref MtcBuddy::Mtc_BuddysAddGrp results
         *
         * @param [in] dwGrpType group type.
         * @param [in] pcIdStr group Id str.
         * @param [in] dwStatCode status code.
         *
         */
        void mtcBuddyCbGrpAddFailed(int dwGrpType, String pcIdStr, int dwStatCode);

        /**
         * @brief Set the group removed successfully callback.
         * The callback is one of @ref MtcBuddy::Mtc_BuddysRmvGrp results
         *
         * @param [in] dwGrpType group type.
         * @param [in] pcIdStr group Id str.
         */
        void mtcBuddyCbGrpRmvOk(int dwGrpType, String pcIdStr);

        /**
         * @brief Set the group removed failed callback.
         * The callback is one of @ref MtcBuddy::Mtc_BuddysRmvGrp results
         *
         * @param [in] dwGrpType group type.
         * @param [in] dwStatCode status code.
         * @param [in] dwId group Id.
         * @param [in] pcIdStr group Id str.
         *
         */
        void mtcBuddyCbGrpRmvFailed(int dwGrpType, int dwId, String pcIdStr, int dwStatCode);

        /**
         * @brief Set the group modified successfully callback.
         * The callback is one of @ref MtcBuddy::Mtc_BuddyGrpSetDispName results
         *
         * @param [in] dwGrpType group type.
         * @param [in] dwId group Id.
         * @param [in] pcIdStr group Id str.
         *
         */
        void mtcBuddyCbGrpMdfyOk(int dwGrpType, int dwId, String pcIdStr);

        /**
         * @brief Set the group modified failed callback.
         * The callback is one of @ref MtcBuddy::Mtc_BuddyGrpSetDispName results
         *
         * @param [in] dwGrpType group type.
         * @param [in] dwStatCode status code.
         * @param [in] dwId group Id.
         * @param [in] pcIdStr group Id str.
         */
        void mtcBuddyCbGrpMdfyFailed(int dwGrpType, int dwId, String pcIdStr, int dwStatCode);

        /**
         * @brief Set the buddy group state change callback.
         * The callback will be notified if user subscribe buddy's presence status in one group.
         *
         * @param [in] dwId group Id.
         * @param [in] pcIdStr group Id str.
         */
        void mtcBuddyCbGrpStaChged(int dwId, String pcIdStr);

        /**
         * @brief Set the buddy added successfully callback.
         * The callback is one of @ref MtcBuddy::Mtc_BuddysAddBuddy results
         *
         * @param [in] dwGrpType group type.
         * @param [in] dwId buddy Id.
         * @param [in] pcIdStr buddy Id str.
         */
        void mtcBuddyCbBuddyAddOk(int dwGrpType, int dwId, String pcIdStr);

        /**
         * @brief Set the buddy added failed callback.
         * The callback is one of @ref MtcBuddy::Mtc_BuddysAddBuddy results
         *
         * @param [in] dwGrpType group type.
         * @param [in] dwStatCode status code.
         * @param [in] pcIdStr buddy Id str.
         */
        void mtcBuddyCbBuddyAddFailed(int dwGrpType, String pcIdStr, int dwStatCode);

        /**
         * @brief Set the buddy removed successfully callback.
         * The callback is one of @ref MtcBuddy::Mtc_BuddysRmvBuddy results
         *
         * @param [in] dwGrpType group type.
         * @param [in] pcIdStr buddy Id str.
         */
        void mtcBuddyCbBuddyRmvOk(int dwGrpType, String pcIdStr);

        /**
         * @brief Set the buddy removed failed callback.
         * The callback is one of @ref MtcBuddy::Mtc_BuddysRmvBuddy results
         *
         * @param [in] dwGrpType group type.
         * @param [in] dwStatCode status code.
         * @param [in] dwId buddy Id.
         * @param [in] pcIdStr buddy Id str.
         *
         */
        void mtcBuddyCbBuddyRmvFailed(int dwGrpType, int dwId, String pcIdStr, int dwStatCode);

        /**
         * @brief Set the buddy modified successfully callback.
         * The callback is one of @ref MtcBuddy::Mtc_BuddySetDispName results
         *
         * @param [in] dwGrpType group type.
         * @param [in] dwId buddy Id.
         * @param [in] pcIdStr buddy Id str.
         *
         */
        void mtcBuddyCbBuddyMdfyOk(int dwGrpType, int dwId, String pcIdStr);

        /**
         * @brief Set the buddy modified failed callback.
         * The callback is one of @ref MtcBuddy::Mtc_BuddySetDispName results
         *
         * @param [in] dwGrpType group type.
         * @param [in] dwId buddy Id.
         * @param [in] pcIdStr buddy Id str.
         * @param [in] dwStatCode status code.
         *
         */
        void mtcBuddyCbBuddyMdfyFailed(int dwGrpType, int dwId, String pcIdStr, int dwStatCode);

        /**
         * @brief Set the buddy icon load successfully callback.
         * The callback is one of @ref MtcBuddy::Mtc_BuddyLoadPresIcon results
         *
         * @param [in] dwGrpType group type.
         * @param [in] dwBuddyId buddy id.
         * @param [in] pcIdStr buddy Id str.
         */
        void mtcBuddyCbBuddyIconLoadOk(int dwGrpType, int dwBuddyId, String pcIdStr);

        /**
         * @brief Set the buddy icon load failed callback.
         * The callback is one of @ref MtcBuddy::Mtc_BuddyLoadPresIcon results
         *
         * @param [in] dwGrpType group type.
         * @param [in] dwBuddyId buddy id.
         * @param [in] pcIdStr buddy Id str.
         * @param [in] dwStatCode status code.
         */
        void mtcBuddyCbBuddyIconLoadFailed(int dwGrpType, int dwBuddyId, String pcIdStr, int dwStatCode);

        /**
         * @brief Set the buddy state change callback.
         * The callback will be notified if user subscribe buddy's presence status
         *
         * @param [in] pcIdStr buddy Id str.
         * @param [in] dwId buddy Id.
         *
         */
        void mtcBuddyCbBuddyStatChged(int dwId, String pcIdStr);

        /**
         * @brief Set the OMA blocked buddy set load all buddies successfully callback.
         * The callback is one of @ref MtcBuddyBlk::Mtc_BlkBuddysLoad results
         *
         *
         */
        void mtcBuddyCbBlkAllLoadOk();

        /**
         * @brief Set the OMA blocked buddy set load failed callback.
         * The callback is one of @ref MtcBuddyBlk::Mtc_BlkBuddysLoad results
         *
         * @param [in] dwStatCode status code.
         *
         */
        void mtcBuddyCbBlkAllLoadFailed(int dwStatCode);

        /**
         * @brief Set the OMA blocked buddy set upload all buddies successfully callback.
         * The callback is one of @ref MtcBuddyBlk::Mtc_BlkBuddysUpload results
         *
         *
         */
        void mtcBuddyCbBlkAllUploadOk();

        /**
         * @brief Set the OMA blocked buddy set upload failed callback.
         * The callback is one of @ref MtcBuddyBlk::Mtc_BlkBuddysUpload results
         *
         * @param [in] dwStatCode status code.
         *
         */
        void mtcBuddyCbBlkAllUploadFailed(int dwStatCode);

        /**
         * @brief Set the buddy added successfully callback.
         * The callback is one of @ref MtcBuddyBlk::Mtc_BlkBuddysAddBuddy results
         *
         * @param [in] pcIdStr buddy Id str.
         * @param [in] dwId buddy Id.
         *
         */
        void mtcBuddyCbBlkBuddyAddOk(int dwId, String pcIdStr);

        /**
         * @brief Set the buddy added failed callback.
         * The callback is one of @ref MtcBuddyBlk::Mtc_BlkBuddysAddBuddy results
         *
         * @param [in] pcIdStr buddy Id str.
         * @param [in] dwStatCode status code.
         *
         */
        void mtcBuddyCbBlkBuddyAddFailed(String pcIdStr, int dwStatCode);

        /**
         * @brief Set the buddy removed successfully callback.
         * The callback is one of @ref MtcBuddyBlk::Mtc_BlkBuddysRmvBuddy results
         *
         * @param [in] pcIdStr buddy Id str.
         *
         */
        void mtcBuddyCbBlkBuddyRmvOk(String pcIdStr);

        /**
         * @brief Set the buddy removed failed callback.
         * The callback is one of @ref MtcBuddyBlk::Mtc_BlkBuddysRmvBuddy results
         *
         * @param [in] pcIdStr buddy Id str.
         * @param [in] dwId buddy Id.
         * @param [in] dwStatCode status code.
         *
         */
        void mtcBuddyCbBlkBuddyRmvFailed(int dwId, String pcIdStr, int dwStatCode);

        /**
         * @brief Set the buddy modified successfully callback.
         * The callback is one of @ref MtcBuddyBlk::Mtc_BlkBuddySetDispName results
         *
         * @param [in] pcIdStr buddy Id str.
         * @param [in] dwId buddy Id.
         *
         */
        void mtcBuddyCbBlkBuddyMdfyOk(int dwId, String pcIdStr);

        /**
         * @brief Set the buddy modified failed callback.
         * The callback is one of @ref MtcBuddyBlk::Mtc_BlkBuddySetDispName results
         *
         * @param [in] dwId buddy Id.
         * @param [in] pcIdStr buddy Id str.
         * @param [in] dwStatCode status code.
         *
         */
        void mtcBuddyCbBlkBuddyMdfyFailed(int dwId, String pcIdStr, int dwStatCode);

        /**
         * @brief Set the OMA revoked buddy set load all buddies successfully callback.
         * The callback is one of @ref MtcBuddyRvk::Mtc_RvkBuddysLoad results
         *
         */
        void mtcBuddyCbRvkAllLoadOk();

        /**
         * @brief Set the OMA revoked buddy set load failed callback.
         * The callback is one of @ref MtcBuddyRvk::Mtc_RvkBuddysLoad results
         *
         * @param [in] dwStatCode status code.
         *
         */
        void mtcBuddyCbRvkAllLoadFailed(int dwStatCode);

        /**
         * @brief Set the OMA revoked buddy set upload all buddies successfully callback.
         * The callback is one of @ref MtcBuddyRvk::Mtc_RvkBuddysUpload results
         *
         */
        void mtcBuddyCbRvkAllUploadOk();

        /**
         * @brief Set the OMA revoked buddy set upload failed callback.
         * The callback is one of @ref MtcBuddyRvk::Mtc_RvkBuddysUpload results
         *
         * @param [in] dwStatCode status code.
         *
         */
        void mtcBuddyCbRvkAllUploadFailed(int dwStatCode);

        /**
         * @brief Set the buddy added successfully callback.
         * The callback is one of @ref MtcBuddyRvk::Mtc_RvkBuddysAddBuddy results
         *
         * @param [in] dwId buddy Id.
         * @param [in] pcIdStr buddy Id str.
         *
         */
        void mtcBuddyCbRvkBuddyAddOk(int dwId, String pcIdStr);

        /**
         * @brief Set the buddy added failed callback.
         * The callback is one of @ref MtcBuddyRvk::Mtc_RvkBuddysAddBuddy results
         *
         * @param [in] pcIdStr buddy Id str.
         * @param [in] dwStatCode status code.
         *
         */
        void mtcBuddyCbRvkBuddyAddFailed(String pcIdStr, int dwStatCode);

        /**
         * @brief Set the buddy removed successfully callback.
         * The callback is one of @ref MtcBuddyRvk::Mtc_RvkBuddysRmvBuddy results
         *
         * @param [in] pcIdStr buddy Id str.
         *
         */
        void mtcBuddyCbRvkBuddyRmvOk(String pcIdStr);

        /**
         * @brief Set the buddy removed failed callback.
         * The callback is one of @ref MtcBuddyRvk::Mtc_RvkBuddysRmvBuddy results
         *
         * @param [in] dwId buddy Id.
         * @param [in] pcIdStr buddy Id str.
         * @param [in] dwStatCode status code.
         *
         */
        void mtcBuddyCbRvkBuddyRmvFailed(int dwId, String pcIdStr, int dwStatCode);

        /**
         * @brief Set the buddy modified successfully callback.
         * The callback is one of @ref MtcBuddyRvk::Mtc_RvkBuddySetDispName results
         *
         * @param [in] dwId buddy Id.
         * @param [in] pcIdStr buddy Id str.
         *
         */
        void mtcBuddyCbRvkBuddyMdfyOk(int dwId, String pcIdStr);

        /**
         * @brief Set the buddy modified failed callback.
         * The callback is one of @ref MtcBuddyRvk::Mtc_RvkBuddySetDispName results
         *
         * @param [in] dwId buddy Id.
         * @param [in] pcIdStr buddy Id str.
         * @param [in] dwStatCode status code.
         *
         */
        void mtcBuddyCbRvkBuddyMdfyFailed(int dwId, String pcIdStr, int dwStatCode);

        /**
         * @brief Set the OMA pnb buddy set load all buddies successfully callback.
         * The callback is one of @ref MtcBuddyPnb::Mtc_PnbBuddysLoad results
         *
         * @param [in] dwGrpType group type.
         *
         */
        void mtcBuddyCbPnbAllLoadOk(int dwGrpType);

        /**
         * @brief Set the OMA pnb buddy set load failed callback.
         * The callback is one of @ref MtcBuddyPnb::Mtc_PnbBuddysLoad results
         *
         * @param [in] dwGrpType group type.
         * @param [in] dwStatCode status code.
         *
         */
        void mtcBuddyCbPnbAllLoadFailed(int dwGrpType, int dwStatCode);

        /**
         * @brief Set the OMA pnb buddy set upload all buddies successfully callback.
         * The callback is one of @ref MtcBuddyPnb::Mtc_PnbBuddysUpload results
         *
         * @param [in] dwGrpType group type.
         *
         */
        void mtcBuddyCbPnbAllUploadOk(int dwGrpType);

        /**
         * @brief Set the OMA pnb buddy set upload failed callback.
         * The callback is one of @ref MtcBuddyPnb::Mtc_PnbBuddysUpload results
         *
         * @param [in] dwGrpType group type.
         * @param [in] dwStatCode status code.
         *
         */
        void mtcBuddyCbPnbAllUploadFailed(int dwGrpType, int dwStatCode);

        /**
         * @brief Set the buddy added successfully callback.
         * The callback is one of @ref MtcBuddyPnb::Mtc_PnbBuddysAddBuddy results
         *
         * @param [in] dwGrpType group type.
         * @param [in] dwId buddy Id.
         * @param [in] pcIdStr buddy Id str.
         *
         */
        void mtcBuddyCbPnbBuddyAddOk(int dwGrpType, int dwId, String pcIdStr);

        /**
         * @brief Set the buddy added failed callback.
         * The callback is one of @ref MtcBuddyPnb::Mtc_PnbBuddysAddBuddy results
         *
         * @param [in] dwGrpType group type.
         * @param [in] pcIdStr buddy Id str.
         * @param [in] dwStatCode status code.
         *
         */
        void mtcBuddyCbPnbBuddyAddFailed(int dwGrpType, String pcIdStr, int dwStatCode);

        /**
         * @brief Set the buddy removed successfully callback.
         * The callback is one of @ref MtcBuddyPnb::Mtc_PnbBuddysRmvBuddy results
         *
         * @param [in] dwGrpType group type.
         * @param [in] pcIdStr buddy Id str.
         *
         */
        void mtcBuddyCbPnbBuddyRmvOk(int dwGrpType, String pcIdStr);

        /**
         * @brief Set the buddy removed failed callback.
         * The callback is one of @ref MtcBuddyPnb::Mtc_PnbBuddysRmvBuddy results
         *
         * @param [in] dwGrpType group type.
         * @param [in] dwId buddy Id.
         * @param [in] pcIdStr buddy Id str.
         * @param [in] dwStatCode status code.
         *
         */
        void mtcBuddyCbPnbBuddyRmvFailed(int dwGrpType, int dwId, String pcIdStr, int dwStatCode);

        /**
         * @brief Set the buddy modified successfully callback.
         * The callback is one of @ref MtcBuddyPnb::Mtc_PnbBuddySetDispName results
         *
         * @param [in] dwGrpType group type.
         * @param [in] dwId buddy Id.
         * @param [in] pcIdStr buddy Id str.
         *
         */
        void mtcBuddyCbPnbBuddyMdfyOk(int dwGrpType, int dwId, String pcIdStr);

        /**
         * @brief Set the buddy modified failed callback.
         * The callback is one of @ref MtcBuddyPnb::Mtc_PnbBuddySetDispName results
         *
         * @param [in] dwGrpType group type.
         * @param [in] dwId buddy Id.
         * @param [in] pcIdStr buddy Id str.
         * @param [in] dwStatCode status code.
         *
         */
        void mtcBuddyCbPnbBuddyMdfyFailed(int dwGrpType, int dwId, String pcIdStr, int dwStatCode);


        void mtcBuddyCbSearchRetOk(int dwSearchId);


        void mtcBuddyCbSearchRetFailed(int dwSearchId);
        

        /**
         * @brief Set the subscribe document changes successfully callback.
         * The callback is one of @ref MtcBuddy::Mtc_BuddySubsDocChgs results
         *
         * @param [in] dwSubsId subscribe id.
         */
        void mtcBuddyCbSubsDocChgsOk(int dwSubsId);

      /**
       * @brief Set the subscribe document changes failed callback.
       * The callback is one of @ref MtcBuddy::Mtc_BuddySubsDocChgs results
       *
       * @param [in] dwSubsId subscribe id.
       * @param [in] dwStatCode status code.
       */
      void mtcBuddyCbSubsDocChgsFailed(int dwSubsId, int dwStatCode);

     /**
      * @brief Set the un-subscribe document changes successfully callback.
      * The callback is one of @ref MtcBuddy::Mtc_BuddyUnSubsDocChgs results
      *
      * @param [in] dwSubsId subscribe id.
      */
      void mtcBuddyCbUnSubsDocChgsOk(int dwSubsId);

     /**
      * @brief Set the un-subscribe document changes failed callback.
      * The callback is one of @ref MtcBuddy::Mtc_BuddyUnSubsDocChgs results
      *
      * @param [in] dwSubsId subscribe id.
      * @param [in] dwStatCode status code.
      */
      void mtcBuddyCbUnSubsDocChgsFailed(int dwSubsId, int dwStatCode);
      

     /**
      * @brief Set the OMA buddy set document synchronize indication callback.
      *
      * @param [in] iSyncType synchronize type.
      */
      void mtcBuddyCbDocSyncInd(int iSyncType);
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

    private static final int CALLBACK_BUDDY_ALL_LOAD_OK = 0;
    private static final int CALLBACK_BUDDY_ALL_LOAD_FAILED = 1;
    private static final int CALLBACK_BUDDY_ALL_UPLOAD_OK = 2;
    private static final int CALLBACK_BUDDY_ALL_UPLOAD_FAILED = 3;

    private static final int CALLBACK_BUDDY_RCS_ALL_LOAD_OK = 4;
    private static final int CALLBACK_BUDDY_RCS_ALL_LOAD_FAILED = 5;
    private static final int CALLBACK_BUDDY_RCS_ALL_UPLOAD_OK = 6;
    private static final int CALLBACK_BUDDY_RCS_ALL_UPLOAD_FAILED = 7;

    private static final int CALLBACK_BUDDY_GRP_ADD_OK = 8;
    private static final int CALLBACK_BUDDY_GRP_ADD_FAILED = 9;
    private static final int CALLBACK_BUDDY_GRP_RMV_OK = 10;
    private static final int CALLBACK_BUDDY_GRP_RMV_FAILED = 11;
    private static final int CALLBACK_BUDDY_GRP_MDFY_OK = 12;
    private static final int CALLBACK_BUDDY_GRP_MDFY_FAILED = 13;
    private static final int CALLBACK_BUDDY_GRP_STA_CHGED = 14;

    private static final int CALLBACK_BUDDY_BUDDY_ADD_OK = 15;
    private static final int CALLBACK_BUDDY_BUDDY_ADD_FAILED = 16;
    private static final int CALLBACK_BUDDY_BUDDY_RMV_OK = 17;
    private static final int CALLBACK_BUDDY_BUDDY_RMV_FAILED = 18;
    private static final int CALLBACK_BUDDY_BUDDY_MDFY_OK = 19;
    private static final int CALLBACK_BUDDY_BUDDY_MDFY_FAILED = 20;
    private static final int CALLBACK_BUDDY_BUDDY_ICON_LOAD_OK = 21;
    private static final int CALLBACK_BUDDY_BUDDY_ICON_LOAD_FAILED = 22;
    private static final int CALLBACK_BUDDY_BUDDY_STAT_CHGED = 23;

    private static final int CALLBACK_BUDDY_BLK_ALL_LOAD_OK = 24;
    private static final int CALLBACK_BUDDY_BLK_ALL_LOAD_FAILED = 25;
    private static final int CALLBACK_BUDDY_BLK_ALL_UPLOAD_OK = 26;
    private static final int CALLBACK_BUDDY_BLK_ALL_UPLOAD_FAILED = 27;
    private static final int CALLBACK_BUDDY_BLK_BUDDY_ADD_OK = 28;
    private static final int CALLBACK_BUDDY_BLK_BUDDY_ADD_FAILED = 29;
    private static final int CALLBACK_BUDDY_BLK_BUDDY_RMV_OK = 30;
    private static final int CALLBACK_BUDDY_BLK_BUDDY_RMV_FAILED = 31;
    private static final int CALLBACK_BUDDY_BLK_BUDDY_MDFY_OK = 32;
    private static final int CALLBACK_BUDDY_BLK_BUDDY_MDFY_FAILED = 33;

    private static final int CALLBACK_BUDDY_RVK_ALL_LOAD_OK = 34;
    private static final int CALLBACK_BUDDY_RVK_ALL_LOAD_FAILED = 35;
    private static final int CALLBACK_BUDDY_RVK_ALL_UPLOAD_OK = 36;
    private static final int CALLBACK_BUDDY_RVK_ALL_UPLOAD_FAILED = 37;
    private static final int CALLBACK_BUDDY_RVK_BUDDY_ADD_OK = 38;
    private static final int CALLBACK_BUDDY_RVK_BUDDY_ADD_FAILED = 39;
    private static final int CALLBACK_BUDDY_RVK_BUDDY_RMV_OK = 40;
    private static final int CALLBACK_BUDDY_RVK_BUDDY_RMV_FAILED = 41;
    private static final int CALLBACK_BUDDY_RVK_BUDDY_MDFY_OK = 42;
    private static final int CALLBACK_BUDDY_RVK_BUDDY_MDFY_FAILED = 43;

    private static final int CALLBACK_BUDDY_PNB_ALL_LOADOK = 44;
    private static final int CALLBACK_BUDDY_PNB_ALL_LOAD_FAILED = 45;
    private static final int CALLBACK_BUDDY_PNB_ALL_UPLOAD_OK = 46;
    private static final int CALLBACK_BUDDY_PNB_ALL_UPLOAD_FAILED = 47;
    private static final int CALLBACK_BUDDY_PNB_BUDDY_ADD_OK = 48;
    private static final int CALLBACK_BUDDY_PNB_BUDDY_ADD_FAILED = 49;
    private static final int CALLBACK_BUDDY_PNB_BUDDY_RMV_OK = 50;
    private static final int CALLBACK_BUDDY_PNB_BUDDY_RMV_FAILED = 51;
    private static final int CALLBACK_BUDDY_PNB_BUDDY_MDFY_OK = 52;
    private static final int CALLBACK_BUDDY_PNB_BUDDY_MDFY_FAILED = 53;
    private static final int CALLBACK_BUDDY_SEARCH_RET_OK = 54;
    private static final int CALLBACK_BUDDY_SEARCH_RET_FAILED = 55;

    private static final int CALLBACK_BUDDY_SUBS_DOC_CHGS_OK = 56;
    private static final int CALLBACK_BUDDY_SUBS_DOC_CHGS_FAILED = 57;
    private static final int CALLBACK_BUDDY_UNSUBS_DOC_CHGS_OK = 58;
    private static final int CALLBACK_BUDDY_UNSUBS_DOC_CHGS_FAILED = 59;
    private static final int CALLBACK_BUDDY_DOC_SYNC_IND = 60;

    /**
     * @brief Distribute call callbacks
     *
     * Distribute call callbacks
     */
    private static void mtcBuddyCbCallback(int function, int type, int dwId, String str, int dwStatCode) {
        switch (function) {
            case CALLBACK_BUDDY_ALL_LOAD_OK:
                sCallback.mtcBuddyCbAllLoadOk();
                break;
            case CALLBACK_BUDDY_ALL_LOAD_FAILED:
                sCallback.mtcBuddyCbAllLoadFailed(dwStatCode);
                break;
            case CALLBACK_BUDDY_ALL_UPLOAD_OK:
                sCallback.mtcBuddyCbAllUploadOk();
                break;
            case CALLBACK_BUDDY_ALL_UPLOAD_FAILED:
                sCallback.mtcBuddyCbAllUploadFailed(dwStatCode);
                break;
            case CALLBACK_BUDDY_RCS_ALL_LOAD_OK:
                sCallback.mtcBuddyCbRcsAllLoadOk(type);
                break;
            case CALLBACK_BUDDY_RCS_ALL_LOAD_FAILED:
                sCallback.mtcBuddyCbRcsAllLoadFailed(type, dwStatCode);
                break;
            case CALLBACK_BUDDY_RCS_ALL_UPLOAD_OK:
                sCallback.mtcBuddyCbRcsAllUploadOk(type);
                break;
            case CALLBACK_BUDDY_RCS_ALL_UPLOAD_FAILED:
                sCallback.mtcBuddyCbRcsAllUploadFailed(type, dwStatCode);
                break;
            case CALLBACK_BUDDY_GRP_ADD_OK:
                sCallback.mtcBuddyCbGrpAddOk(type, dwId, str);
                break;
            case CALLBACK_BUDDY_GRP_ADD_FAILED:
                sCallback.mtcBuddyCbGrpAddFailed(type, str, dwStatCode);
                break;
            case CALLBACK_BUDDY_GRP_RMV_OK:
                sCallback.mtcBuddyCbGrpRmvOk(type, str);
                break;
            case CALLBACK_BUDDY_GRP_RMV_FAILED:
                sCallback.mtcBuddyCbGrpRmvFailed(type, dwId, str, dwStatCode);
                break;
            case CALLBACK_BUDDY_GRP_MDFY_OK:
                sCallback.mtcBuddyCbGrpMdfyOk(type, dwId, str);
                break;
            case CALLBACK_BUDDY_GRP_MDFY_FAILED:
                sCallback.mtcBuddyCbGrpMdfyFailed(type, dwId, str, dwStatCode);
                break;
            case CALLBACK_BUDDY_GRP_STA_CHGED:
                sCallback.mtcBuddyCbGrpStaChged(dwId, str);
                break;
            case CALLBACK_BUDDY_BUDDY_ADD_OK:
                sCallback.mtcBuddyCbBuddyAddOk(type, dwId, str);
                break;
            case CALLBACK_BUDDY_BUDDY_ADD_FAILED:
                sCallback.mtcBuddyCbBuddyAddFailed(type, str, dwStatCode);
                break;
            case CALLBACK_BUDDY_BUDDY_RMV_OK:
                sCallback.mtcBuddyCbBuddyRmvOk(type, str);
                break;
            case CALLBACK_BUDDY_BUDDY_RMV_FAILED:
                sCallback.mtcBuddyCbBuddyRmvFailed(type, dwId, str, dwStatCode);
                break;
            case CALLBACK_BUDDY_BUDDY_MDFY_OK:
                sCallback.mtcBuddyCbBuddyMdfyOk(type, dwId, str);
                break;
            case CALLBACK_BUDDY_BUDDY_MDFY_FAILED:
                sCallback.mtcBuddyCbBuddyMdfyFailed(type, dwId, str, dwStatCode);
                break;
            case CALLBACK_BUDDY_BUDDY_ICON_LOAD_OK:
                sCallback.mtcBuddyCbBuddyIconLoadOk(type, dwId, str);
                break;
            case CALLBACK_BUDDY_BUDDY_ICON_LOAD_FAILED:
                sCallback.mtcBuddyCbBuddyIconLoadFailed(type, dwId, str, dwStatCode);
                break;
            case CALLBACK_BUDDY_BUDDY_STAT_CHGED:
                sCallback.mtcBuddyCbBuddyStatChged(dwId, str);
                break;
            case CALLBACK_BUDDY_BLK_ALL_LOAD_OK:
                sCallback.mtcBuddyCbBlkAllLoadOk();
                break;
            case CALLBACK_BUDDY_BLK_ALL_LOAD_FAILED:
                sCallback.mtcBuddyCbBlkAllLoadFailed(dwStatCode);
                break;
            case CALLBACK_BUDDY_BLK_ALL_UPLOAD_OK:
                sCallback.mtcBuddyCbBlkAllUploadOk();
                break;
            case CALLBACK_BUDDY_BLK_ALL_UPLOAD_FAILED:
                sCallback.mtcBuddyCbBlkAllUploadFailed(dwStatCode);
                break;
            case CALLBACK_BUDDY_BLK_BUDDY_ADD_OK:
                sCallback.mtcBuddyCbBlkBuddyAddOk(dwId, str);
                break;
            case CALLBACK_BUDDY_BLK_BUDDY_ADD_FAILED:
                sCallback.mtcBuddyCbBlkBuddyAddFailed(str, dwStatCode);
                break;
            case CALLBACK_BUDDY_BLK_BUDDY_RMV_OK:
                sCallback.mtcBuddyCbBlkBuddyRmvOk(str);
                break;
            case CALLBACK_BUDDY_BLK_BUDDY_RMV_FAILED:
                sCallback.mtcBuddyCbBlkBuddyRmvFailed(dwId, str, dwStatCode);
                break;
            case CALLBACK_BUDDY_BLK_BUDDY_MDFY_OK:
                sCallback.mtcBuddyCbBlkBuddyMdfyOk(dwId, str);
                break;
            case CALLBACK_BUDDY_BLK_BUDDY_MDFY_FAILED:
                sCallback.mtcBuddyCbBlkBuddyMdfyFailed(dwId, str, dwStatCode);
                break;
            case CALLBACK_BUDDY_RVK_ALL_LOAD_OK:
                sCallback.mtcBuddyCbRvkAllLoadOk();
                break;
            case CALLBACK_BUDDY_RVK_ALL_LOAD_FAILED:
                sCallback.mtcBuddyCbRvkAllLoadFailed(dwStatCode);
                break;
            case CALLBACK_BUDDY_RVK_ALL_UPLOAD_OK:
                sCallback.mtcBuddyCbRvkAllUploadOk();
                break;
            case CALLBACK_BUDDY_RVK_ALL_UPLOAD_FAILED:
                sCallback.mtcBuddyCbRvkAllLoadFailed(dwStatCode);
                break;
            case CALLBACK_BUDDY_RVK_BUDDY_ADD_OK:
                sCallback.mtcBuddyCbRvkBuddyAddOk(dwId, str);
                break;
            case CALLBACK_BUDDY_RVK_BUDDY_ADD_FAILED:
                sCallback.mtcBuddyCbRvkBuddyAddFailed(str, dwStatCode);
                break;
            case CALLBACK_BUDDY_RVK_BUDDY_RMV_OK:
                sCallback.mtcBuddyCbRvkBuddyRmvOk(str);
                break;
            case CALLBACK_BUDDY_RVK_BUDDY_RMV_FAILED:
                sCallback.mtcBuddyCbRvkBuddyRmvFailed(dwId, str, dwStatCode);
                break;
            case CALLBACK_BUDDY_RVK_BUDDY_MDFY_OK:
                sCallback.mtcBuddyCbRvkBuddyMdfyOk(dwId, str);
                break;
            case CALLBACK_BUDDY_RVK_BUDDY_MDFY_FAILED:
                sCallback.mtcBuddyCbRvkBuddyMdfyFailed(dwId, str, dwStatCode);
                break;
            case CALLBACK_BUDDY_PNB_ALL_LOADOK:
                sCallback.mtcBuddyCbPnbAllLoadOk(type);
                break;
            case CALLBACK_BUDDY_PNB_ALL_LOAD_FAILED:
                sCallback.mtcBuddyCbPnbAllLoadFailed(type, dwStatCode);
                break;
            case CALLBACK_BUDDY_PNB_ALL_UPLOAD_OK:
                sCallback.mtcBuddyCbPnbAllUploadOk(type);
                break;
            case CALLBACK_BUDDY_PNB_ALL_UPLOAD_FAILED:
                sCallback.mtcBuddyCbPnbAllUploadFailed(type, dwStatCode);
                break;
            case CALLBACK_BUDDY_PNB_BUDDY_ADD_OK:
                sCallback.mtcBuddyCbPnbBuddyAddOk(type, dwId, str);
                break;
            case CALLBACK_BUDDY_PNB_BUDDY_ADD_FAILED:
                sCallback.mtcBuddyCbPnbBuddyAddFailed(type, str, dwStatCode);
                break;
            case CALLBACK_BUDDY_PNB_BUDDY_RMV_OK:
                sCallback.mtcBuddyCbPnbBuddyRmvOk(type, str);
                break;
            case CALLBACK_BUDDY_PNB_BUDDY_RMV_FAILED:
                sCallback.mtcBuddyCbPnbBuddyRmvFailed(type, dwId, str, dwStatCode);
                break;
            case CALLBACK_BUDDY_PNB_BUDDY_MDFY_OK:
                sCallback.mtcBuddyCbPnbBuddyMdfyOk(type, dwId, str);
                break;
            case CALLBACK_BUDDY_PNB_BUDDY_MDFY_FAILED:
                sCallback.mtcBuddyCbPnbBuddyMdfyFailed(type, dwId, str, dwStatCode);
                break;
            case CALLBACK_BUDDY_SEARCH_RET_OK:
                sCallback.mtcBuddyCbSearchRetOk(dwId);
                break;
            case CALLBACK_BUDDY_SEARCH_RET_FAILED:
                sCallback.mtcBuddyCbSearchRetFailed(dwId);
                break;
            case CALLBACK_BUDDY_SUBS_DOC_CHGS_OK:
              sCallback.mtcBuddyCbSubsDocChgsOk(dwId);
              break;
            case CALLBACK_BUDDY_SUBS_DOC_CHGS_FAILED:
              sCallback.mtcBuddyCbSubsDocChgsFailed(dwId, dwStatCode);
              break;
            case CALLBACK_BUDDY_UNSUBS_DOC_CHGS_OK:
              sCallback.mtcBuddyCbUnSubsDocChgsOk(dwId);
              break;
            case CALLBACK_BUDDY_UNSUBS_DOC_CHGS_FAILED:
              sCallback.mtcBuddyCbUnSubsDocChgsFailed(dwId, dwStatCode);
              break;
            case CALLBACK_BUDDY_DOC_SYNC_IND:
              sCallback.mtcBuddyCbDocSyncInd(type);
              break;
        }
    }
}
