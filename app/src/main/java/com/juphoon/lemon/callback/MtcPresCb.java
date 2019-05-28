/**
 * @file MtcPresCb.java
 * @brief MTC Presence callbacks Interface Functions
 */
 package com.juphoon.lemon.callback;

   /**
   * @brief Class of MTC Presence callbacks
   */
public class MtcPresCb {

     /**
     * @brief MTC Presence callbacks
     *
     * In order to receive MTC Presence callbacks, user should implement this 
     * interface, then use @ref MtcPresCb.setCallback to register callbacks.
     */    
    public interface Callback {

         /**
         * @brief Set the publish presence status successfully callback.
         * The callback is one of @ref MtcPres::Mtc_PresPubStatus results
         *
         */
        void mtcPresCbPubStatusOk();

         /**
         * @brief Set the publish presence status failed callback.
         * The callback is one of @ref MtcPres::Mtc_PresPubStatus results
         *
         * @param [in] dwStatCode Pubstatus error code.
         */
        void mtcPresCbPubStatusFailed(int dwStatCode);

         /**
         * @brief Set the un-publish presence status successfully callback.
         * The callback is one of @ref MtcPres::Mtc_PresUnPubStatus results
         *
         */
        void mtcPresCbUnPubStatusOk();

        /**
        * @brief Set the un-publish presence status failed callback.
        * The callback is one of @ref MtcPres::Mtc_PresUnPubStatus results
        *
        * @param [in] dwStatCode Error code.
        */
        void mtcPresCbUnPubStatusFailed(int dwStatCode);

        /**
         * @brief Set the publish poc-settings successfully callback.
         * The callback is one of @ref MtcPres::Mtc_PresPubPocSets results
         *
         */
        void mtcPresCbPubPocSetsOk();

        /**
         * @brief Set the publish poc-settings failed callback.
         * The callback is one of @ref MtcPres::Mtc_PresPubPocSets results
         *
         * @param [in] dwStatCode  Error code.
         */
        void mtcPresCbPubPocSetsFailed(int dwStatCode);

        /**
         * @brief Set the un-publish poc-settings successfully callback.
         * The callback is one of @ref MtcPres::Mtc_PresUnPubPocSets results
         *
         */
        void mtcPresCbUnPubPocSetsOk();

        /**
         * @brief Set the un-publish poc-settings failed callback.
         * The callback is one of @ref MtcPres::Mtc_PresUnPubPocSets results
         *
         * @param [in] dwStatCode Error code.
         */
        void mtcPresCbUnPubPocSetsFailed(int dwStatCode);

        /**
         * @brief Set the subscribe one buddy presence status successfully callback.
         * The callback is one of @ref MtcPres::Mtc_PresSubsBuddy results
         *
         */
        void mtcPresCbSubsBuddyOk();

        /**
         * @brief Set the subscribe one buddy presence status failed callback.
         * The callback is one of @ref MtcPres::Mtc_PresSubsBuddy results
         *
         * @param [in]  dwStatCode Error code.
         */
        void mtcPresCbSubsBuddyFailed(int dwStatCode);

        /**
         * @brief Set the un-subscribe one buddy presence status successfully callback.
         * The callback is one of @ref MtcPres::Mtc_PresUnSubsBuddy results
         *
         */
        void mtcPresCbUnSubsBuddyOk();

        /**
         * @brief Set the un-subscribe one buddy presence status failed callback.
         * The callback is one of @ref MtcPres::Mtc_PresUnSubsBuddy results
         *
         * @param [in] dwStatCode Error code.
         */
        void mtcPresCbUnSubsBuddyFailed(int dwStatCode);
        /**
         * @brief Set the subscribe presence status successfully callback.
         * The callback is one of @ref MtcPres::Mtc_PresSubsBuddyLst results
         *
         */
        void mtcPresCbSubsBuddyLstOk();

        /**
         * @brief Set the subscribe presence status failed callback.
         * The callback is one of @ref MtcPres::Mtc_PresSubsBuddyLst results
         *
         * @param [in]  dwStatCode Error code.
         */
        void mtcPresCbSubsBuddyLstFailed(int dwStatCode);

        /**
         * @brief Set the un-subscribe presence status successfully callback.
         * The callback is one of @ref MtcPres::Mtc_PresUnSubsBuddyLst results
         *
         */
        void mtcPresCbUnSubsBuddyLstOk();

        /**
         * @brief Set the un-subscribe presence status failed callback.
         * The callback is one of @ref MtcPres::Mtc_PresUnSubsBuddyLst results
         *
         * @param [in]  dwStatCode Error code.
         */
        void mtcPresCbUnSubsBuddyLstFailed(int dwStatCode);

        /**
         * @brief Set the subscribe one contact info presence status successfully callback.
         * The callback is one of @ref MtcPresCinfo::Mtc_PresCInfoSubs results
         *
         * @param [in] dwCInfoId ContactInfo Id.
         */
        void mtcPresCbSubsCInfoOk(int dwCInfoId);

        /**
         * @brief Set the subscribe one contact info presence status failed callback.
         * The callback is one of @ref MtcPresCinfo::Mtc_PresCInfoSubs results
         *
         * @param [in] dwStatCode status code.
         * @param [in] dwCInfoId ContactInfo Id.
         */
        void mtcPresCbSubsCInfoFailed(int dwCInfoId, int dwStatCode);

        /**
         * @brief Set the un-subscribe one contact info presence status successfully callback.
         * The callback is one of @ref MtcPresCinfo::Mtc_PresCInfoUnSubs results
         *
         * @param [in] dwCInfoId ContactInfo Id.
         */
        void mtcPresCbUnSubsCInfoOk(int dwCInfoId);

        /**
         * @brief Set the un-subscribe one contact info presence status failed callback.
         * The callback is one of @ref MtcPresCinfo::Mtc_PresCInfoUnSubs results
         *
         * @param [in] dwStatCode dwStatCode Error code.
         * @param [in] dwCInfoId ContactInfo Id.
         */
        void mtcPresCbUnSubsCInfoFailed(int dwCInfoId, int dwStatCode);

        /**
         * @brief Set the contact info has updated callback.
         * The callback will be notified if user subscribe contact info
         *
         * @param [in] dwCInfoId ContactInfo Id.
         */
        void mtcPresCbCInfoUpdated(int dwCInfoId);

        /**
         * @brief Set the subscribe watch info successfully callback.
         * The callback is one of @ref MtcPresWinfo::Mtc_PresWinfoSubs results
         *
         */
        void mtcPresCbSubsWinfoOk();

        /**
         * @brief Set the subscribe watch info failed callback.
         * The callback is one of @ref MtcPresWinfo::Mtc_PresWinfoSubs results
         *
         * @param [in]  dwStatCode Error code.
         */
        void mtcPresCbSubsWinfoFailed(int dwStatCode);

        /**
         * @brief Set the un-subscribe watch info successfully callback.
         * The callback is one of @ref MtcPresWinfo::Mtc_PresWinfoUnSubs results
         *
         */
        void mtcPresCbUnSubsWinfoOk();

        /**
         * @brief Set the un-subscribe watch info failed callback.
         * The callback is one of @ref MtcPresWinfo::Mtc_PresWinfoUnSubs results
         *
         * @param [in]  dwStatCode Error code.
         */
        void mtcPresCbUnSubsWinfoFailed(int dwStatCode);

        /**
         * @brief Set the watch info has updated callback.
         * The callback will be notified if user subscribe watch info
         *
         */
        void mtcPresCbWinfoUpdated();

        /**
         * @brief Set the load presence rule successfully callback.
         * The callback is one of @ref MtcPresRule::Mtc_PresRulesLoad results
         *
         */
        void mtcPresCbRuleLoadOk();

        /**
         * @brief Set the load presence rule failed callback.
         * The callback is one of @ref MtcPresRule::Mtc_PresRulesLoad results
         *
         * @param [in]  dwStatCode Error code.
         */
        void mtcPresCbRuleLoadFailed(int dwStatCode);

        /**
         * @brief Set the load presence service successfully callback.
         * The callback is one of @ref MtcPresSrv::Mtc_PresSrvsLoad results
         *
         */
        void mtcPresCbSrvLoadOk();

        /**
         * @brief Set the load presence service failed callback.
         * The callback is one of @ref MtcPresSrv::Mtc_PresSrvsLoad results
         *
         * @param [in] dwStatCode Error code.
         */
        void mtcPresCbSrvLoadFailed(int dwStatCode);

        /**
         * @brief Set the upload presence service successfully callback.
         * The callback is one of @ref MtcPresSrv::Mtc_PresSrvsUpload results
         *
         */
        void mtcPresCbSrvUploadOk();

        /**
         * @brief Set the upload presence service failed callback.
         * The callback is one of @ref MtcPresSrv::Mtc_PresSrvsUpload results
         *
         * @param [in] dwStatCode Error code.
         */
        void mtcPresCbSrvUploadFailed(int dwStatCode);

        /**
         * @brief Set the add a buddy in presence service successfully callback.
         * The callback is one of @ref MtcPresSrv::Mtc_PresSrvAddBuddy results
         *
         * @param [in] dwCInfoId ContactInfo Id.
         */
        void mtcPresCbSrvAddBuddyOk(int dwCInfoId);

        /**
         * @brief Set the add a buddy in presence service failed callback.
         * The callback is one of @ref MtcPresSrv::Mtc_PresSrvAddBuddy results
         *
         * @param [in] dwStatCode Error code.
         * @param [in] dwCInfoId ContactInfo Id.
         */
        void mtcPresCbSrvAddBuddyFailed(int dwCInfoId, int dwStatCode);

        /**
         * @brief Set the remove a buddy in presence service successfully callback.
         * The callback is one of @ref MtcPresSrv::Mtc_PresSrvRmvBuddy results
         *
         * @param [in] dwCInfoId ContactInfo Id.
         */
        void mtcPresCbSrvRmvBuddyOk(int dwCInfoId);

        /**
         * @brief Set the remove a buddy in presence service failed callback.
         * The callback is one of @ref MtcPresSrv::Mtc_PresSrvRmvBuddy results
         *
         * @param [in] dwStatCode Error code.
         * @param [in] dwCInfoId ContactInfo Id.
         */
        void mtcPresCbSrvRmvBuddyFailed(int dwCInfoId, int dwStatCode);

        /**
         * @brief Set the load presence permanent successfully callback.
         * The callback is one of @ref MtcPresPerm::Mtc_PresPermLoad results
         *
         */
        void mtcPresCbPermsLoadOk();

        /**
         * @brief Set the load presence permanent failed callback.
         * The callback is one of @ref MtcPresPerm::Mtc_PresPermLoad results
         *
         * @param [in] dwStatCode Error code.
         */
        void mtcPresCbPermsLoadFailed(int dwStatCode);

        /**
         * @brief Set the upload presence permanent successfully callback.
         * The callback is one of @ref MtcPresPerm::Mtc_PresPermUpload results
         *
         */
        void mtcPresCbPermsUploadOk();

        /**
         * @brief Set the upload presence permanent failed callback.
         * The callback is one of @ref MtcPresPerm::Mtc_PresPermUpload results
         *
         * @param [in] dwStatCode Error code.
         */
        void mtcPresCbPermsUploadFailed(int dwStatCode);

        /**
         * @brief Set the subscribe presence permanent document changes successfully callback.
         * The callback is one of @ref MtcPresPerm::Mtc_PresPermSubsDocChgs results
         *
         * @param [in] dwSubsId Subscribe Id.
         */
        void mtcPresCbPermsSubsDocChgsOk(int dwSubsId);

        /**
         * @brief Set the subscribe presence permanent document changes failed callback.
         * The callback is one of @ref MtcPresPerm::Mtc_PresPermSubsDocChgs results
         *
         * @param [in] dwSubsId Subscribe Id.
         * @param [in] dwStatCode Error code.
         */
        void mtcPresCbPermsSubsDocChgsFailed(int dwSubsId, int dwStatCode);
        
        /**
         * @brief Set the un-subscribe presence permanent document changes 
         * successfully callback.
         * The callback is one of @ref MtcPresPerm::Mtc_PresPermUnSubsDocChgs results
         *
         * @param [in] dwSubsId subscribe Id.
         */
        void mtcPresCbPermsUnSubsDocChgsOk(int dwSubsId);
        /**
         * @brief Set the un-subscribe presence permanent document changes failed callback.
         * The callback is one of @ref MtcPresPerm::Mtc_PresPermUnSubsDocChgs results
         *
         * @param [in] dwSubsId Subscribe Id.
         * @param [in] dwStatCode Error code.
         */
        void mtcPresCbPermsUnSubsDocChgsFailed(int dwSubsId, int dwStatCode);

        /**
         * @brief Set the presence permanent document synchronize indication callback.
         *
         * @param [in] iSyncType synchronize Type.
         */
        void mtcPresCbPermsDocSyncInd(int iSyncType);

        /**
         * @brief Set the load presence status icon successfully callback.
         * The callback is one of @ref MtcPresPerm::Mtc_PresPermLoadIcon results
         *
         */
        void mtcPresCbStatIconLoadOk();

        /**
         * @brief Set the load presence status icon failed callback.
         * The callback is one of @ref MtcPresPerm::Mtc_PresPermLoadIcon results
         *
         * @param [in] dwStatCode status code.
         */
        void mtcPresCbStatIconLoadFailed(int dwStatCode);

        /**
         * @brief Set the upload presence status icon successfully callback.
         * The callback is one of @ref MtcPresPerm::Mtc_PresPermUploadIcon results
         *
         */
        void mtcPresCbStatIconUploadOk();

        /**
         * @brief Set the upload presence status icon failed callback.
         * The callback is one of @ref MtcPresPerm::Mtc_PresPermUploadIcon results
         *
         * @param [in] dwStatCode Error code.
         */
        void mtcPresCbStatIconUploadFailed(int dwStatCode);
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

    private static final int CALLBACK_PRES_PUB_STATUS_OK = 0;
    private static final int CALLBACK_PRES_PUB_STATUS_FAILED = 1;
    private static final int CALLBACK_PRES_UNPUB_STATUS_OK = 2;
    private static final int CALLBACK_PRES_UNPUB_STATUS_FAILED = 3;
    private static final int CALLBACK_PRES_PUB_POC_SETS_OK = 4;
    private static final int CALLBACK_PRES_PUB_POC_SETS_FAILED = 5;
    private static final int CALLBACK_PRES_UNPUB_POC_SETS_OK = 6;
    private static final int CALLBACK_PRES_UNPUB_POC_SETS_FAILED = 7;
    private static final int CALLBACK_PRES_SUBS_BUDDY_OK = 8;
    private static final int CALLBACK_PRES_SUBS_BUDDY_FAILED = 9;
    private static final int CALLBACK_PRES_UNSUBS_BUDDY_OK = 10;
    private static final int CALLBACK_PRES_UNSUBS_BUDDY_FAILED = 11;
    private static final int CALLBACK_PRES_SUBS_BUDDY_LST_OK = 12;
    private static final int CALLBACK_PRES_SUBS_BUDDY_LST_FAILED = 13;
    private static final int CALLBACK_PRES_UNSUBS_BUDDY_LST_OK = 14;
    private static final int CALLBACK_PRES_UNSUBS_BUDDY_LST_FAILED = 15;

    private static final int CALLBACK_PRES_SUBS_CINFO_OK = 16;
    private static final int CALLBACK_PRES_SUBS_CINFO_FAILED = 17;
    private static final int CALLBACK_PRES_UNSUBS_CINFO_OK = 18;
    private static final int CALLBACK_PRES_UNSUBS_CINFO_FAILED = 19;
    private static final int CALLBACK_PRES_CINFO_UPDATED = 20;

    private static final int CALLBACK_PRES_SUBS_WINFO_OK = 21;
    private static final int CALLBACK_PRES_SUBS_WINFO_FAILED = 22;
    private static final int CALLBACK_PRES_UNSUBS_WINFO_OK = 23;
    private static final int CALLBACK_PRES_UNSUBS_WINFO_FAILED = 24;
    private static final int CALLBACK_PRES_WINFO_UPDATED = 25;
    private static final int CALLBACK_PRES_RULE_LOAD_OK = 26;
    private static final int CALLBACK_PRES_RULE_LOAD_FAILED = 27;
    private static final int CALLBACK_PRES_SRV_LOAD_OK = 28;
    private static final int CALLBACK_PRES_SRV_LOAD_FAILED = 29;
    private static final int CALLBACK_PRES_SRV_UPLOAD_OK = 30;
    private static final int CALLBACK_PRES_SRV_UPLOAD_FAILED = 31;
    private static final int CALLBACK_PRES_SRV_ADD_BUDDY_OK = 32;
    private static final int CALLBACK_PRES_SRV_ADD_BUDDY_FAILED = 33;
    private static final int CALLBACK_PRES_SRV_RMV_BUDDY_OK = 34;
    private static final int CALLBACK_PRES_SRV_RMV_BUDDY_FAILED = 35;
    private static final int CALLBACK_PRES_PERMS_LOAD_OK = 36;
    private static final int CALLBACK_PRES_PERMS_LOAD_FAILED = 37;
    private static final int CALLBACK_PRES_PERMS_UPLOAD_OK = 38;
    private static final int CALLBACK_PRES_PERMS_UPLOAD_FAILED = 39;
    
    private static final int CALLBACK_PRES_PERMS_SUBS_DOC_CHGS_OK = 40;
    private static final int CALLBACK_PRES_PERMS_SUBS_DOC_CHGS_FAILED = 41;
    private static final int CALLBACK_PRES_PERMS_UNSUBS_DOC_CHGS_OK = 42;
    private static final int CALLBACK_PRES_PERMS_UNSUBS_DOC_CHGS_FAILED = 43;
    private static final int CALLBACK_PRES_PERMS_DOC_SYNC_IND = 44;
    
    private static final int CALLBACK_PRES_STAT_ICON_LOAD_OK = 45;
    private static final int CALLBACK_PRES_STAT_ICON_LOAD_FAILED = 46;
    private static final int CALLBACK_PRES_STAT_ICON_UPLOAD_OK = 47;
    private static final int CALLBACK_PRES_STAT_ICON_UPLOAD_FAILED = 48;

    private static void mtcPresCbCallback(int function, int dwId, int dwStatCode) {
        switch (function) {
            case CALLBACK_PRES_PUB_STATUS_OK:
                sCallback.mtcPresCbPubStatusOk();
                break;
            case CALLBACK_PRES_PUB_STATUS_FAILED:
                sCallback.mtcPresCbPubStatusFailed(dwStatCode);
                break;
            case CALLBACK_PRES_UNPUB_STATUS_OK:
                sCallback.mtcPresCbUnPubStatusOk();
                break;
            case CALLBACK_PRES_UNPUB_STATUS_FAILED:
                sCallback.mtcPresCbUnPubStatusFailed(dwStatCode);
                break;
            case CALLBACK_PRES_PUB_POC_SETS_OK:
                sCallback.mtcPresCbPubPocSetsOk();
                break;
            case CALLBACK_PRES_PUB_POC_SETS_FAILED:
                sCallback.mtcPresCbPubPocSetsFailed(dwStatCode);
                break;
            case CALLBACK_PRES_UNPUB_POC_SETS_OK:
                sCallback.mtcPresCbUnPubPocSetsOk();
                break;
            case CALLBACK_PRES_UNPUB_POC_SETS_FAILED:
                sCallback.mtcPresCbUnPubPocSetsFailed(dwStatCode);
                break;
            case CALLBACK_PRES_SUBS_BUDDY_OK:
                sCallback.mtcPresCbSubsBuddyOk();
                break;
            case CALLBACK_PRES_SUBS_BUDDY_FAILED:
                sCallback.mtcPresCbSubsBuddyFailed(dwStatCode);
                break;
            case CALLBACK_PRES_UNSUBS_BUDDY_OK:
                sCallback.mtcPresCbUnSubsBuddyOk();
                break;
            case CALLBACK_PRES_UNSUBS_BUDDY_FAILED:
                sCallback.mtcPresCbUnSubsBuddyFailed(dwStatCode);
                break;
            case CALLBACK_PRES_SUBS_BUDDY_LST_OK:
                sCallback.mtcPresCbSubsBuddyLstOk();
                break;
            case CALLBACK_PRES_SUBS_BUDDY_LST_FAILED:
                sCallback.mtcPresCbSubsBuddyLstFailed(dwStatCode);
                break;
            case CALLBACK_PRES_UNSUBS_BUDDY_LST_OK:
                sCallback.mtcPresCbUnSubsBuddyLstOk();
                break;
            case CALLBACK_PRES_UNSUBS_BUDDY_LST_FAILED:
                sCallback.mtcPresCbUnSubsBuddyLstFailed(dwStatCode);
                break;
            case CALLBACK_PRES_SUBS_CINFO_OK:
                sCallback.mtcPresCbSubsCInfoOk(dwId);
                break;
            case CALLBACK_PRES_SUBS_CINFO_FAILED:
                sCallback.mtcPresCbSubsCInfoFailed(dwId, dwStatCode);
                break;
            case CALLBACK_PRES_UNSUBS_CINFO_OK:
                sCallback.mtcPresCbUnSubsCInfoOk(dwId);
                break;
            case CALLBACK_PRES_UNSUBS_CINFO_FAILED:
                sCallback.mtcPresCbUnSubsCInfoFailed(dwId, dwStatCode);
                break;
            case CALLBACK_PRES_CINFO_UPDATED:
                sCallback.mtcPresCbCInfoUpdated(dwId);
                break;
            case CALLBACK_PRES_SUBS_WINFO_OK:
                sCallback.mtcPresCbSubsWinfoOk();
                break;
            case CALLBACK_PRES_SUBS_WINFO_FAILED:
                sCallback.mtcPresCbSubsWinfoFailed(dwStatCode);
                break;
            case CALLBACK_PRES_UNSUBS_WINFO_OK:
                sCallback.mtcPresCbUnSubsWinfoOk();
                break;
            case CALLBACK_PRES_UNSUBS_WINFO_FAILED:
                sCallback.mtcPresCbUnSubsWinfoFailed(dwStatCode);
                break;
            case CALLBACK_PRES_WINFO_UPDATED:
                sCallback.mtcPresCbWinfoUpdated();
                break;
            case CALLBACK_PRES_RULE_LOAD_OK:
                sCallback.mtcPresCbRuleLoadOk();
                break;
            case CALLBACK_PRES_RULE_LOAD_FAILED:
                sCallback.mtcPresCbRuleLoadFailed(dwStatCode);
                break;
            case CALLBACK_PRES_SRV_LOAD_OK:
                sCallback.mtcPresCbSrvLoadOk();
                break;
            case CALLBACK_PRES_SRV_LOAD_FAILED:
                sCallback.mtcPresCbSrvLoadFailed(dwStatCode);
                break;
            case CALLBACK_PRES_SRV_UPLOAD_OK:
                sCallback.mtcPresCbSrvUploadOk();
                break;
            case CALLBACK_PRES_SRV_UPLOAD_FAILED:
                sCallback.mtcPresCbSrvUploadFailed(dwStatCode);
                break;
            case CALLBACK_PRES_SRV_ADD_BUDDY_OK:
                sCallback.mtcPresCbSrvAddBuddyOk(dwId);
                break;
            case CALLBACK_PRES_SRV_ADD_BUDDY_FAILED:
                sCallback.mtcPresCbSrvAddBuddyFailed(dwId, dwStatCode);
                break;
            case CALLBACK_PRES_SRV_RMV_BUDDY_OK:
                sCallback.mtcPresCbSrvRmvBuddyOk(dwId);
                break;
            case CALLBACK_PRES_SRV_RMV_BUDDY_FAILED:
                sCallback.mtcPresCbSrvRmvBuddyFailed(dwId, dwStatCode);
                break;
            case CALLBACK_PRES_PERMS_LOAD_OK:
                sCallback.mtcPresCbPermsLoadOk();
                break;
            case CALLBACK_PRES_PERMS_LOAD_FAILED:
                sCallback.mtcPresCbPermsLoadFailed(dwStatCode);
                break;
            case CALLBACK_PRES_PERMS_UPLOAD_OK:
                sCallback.mtcPresCbPermsUploadOk();
                break;
            case CALLBACK_PRES_PERMS_UPLOAD_FAILED:
                sCallback.mtcPresCbPermsUploadFailed(dwStatCode);
                break;
            case CALLBACK_PRES_PERMS_SUBS_DOC_CHGS_OK:
              sCallback.mtcPresCbPermsSubsDocChgsOk(dwId);
              break;
            case CALLBACK_PRES_PERMS_SUBS_DOC_CHGS_FAILED:
              sCallback.mtcPresCbPermsSubsDocChgsFailed(dwId, dwStatCode);
              break;
            case CALLBACK_PRES_PERMS_UNSUBS_DOC_CHGS_OK:
              sCallback.mtcPresCbPermsUnSubsDocChgsOk(dwId);
              break;
            case CALLBACK_PRES_PERMS_UNSUBS_DOC_CHGS_FAILED:
              sCallback.mtcPresCbPermsUnSubsDocChgsFailed(dwId, dwStatCode);
              break;
            case CALLBACK_PRES_PERMS_DOC_SYNC_IND:
              sCallback.mtcPresCbPermsDocSyncInd(dwId);
              break;
            case CALLBACK_PRES_STAT_ICON_LOAD_OK:
                sCallback.mtcPresCbStatIconLoadOk();
                break;
            case CALLBACK_PRES_STAT_ICON_LOAD_FAILED:
                sCallback.mtcPresCbStatIconLoadFailed(dwStatCode);
                break;
            case CALLBACK_PRES_STAT_ICON_UPLOAD_OK:
                sCallback.mtcPresCbStatIconUploadOk();
                break;
            case CALLBACK_PRES_STAT_ICON_UPLOAD_FAILED:
                sCallback.mtcPresCbStatIconUploadFailed(dwStatCode);
                break;
        }
    }
}
