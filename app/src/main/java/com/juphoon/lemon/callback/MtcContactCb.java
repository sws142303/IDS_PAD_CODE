/**
 * @file MtcContactCb.java
 * @brief MTC Contact callbacks Interface Functions
 */
 package com.juphoon.lemon.callback;

import android.database.Observable;

/**
 * @brief Class of MTC Contact callbacks
 */
public class MtcContactCb {

    /**
     * @brief MTC Contact callbacks
     *
     * In order to receive MTC videoshare callbacks, user should implement this 
     * interface, then use @ref MtcVShareCb.setCallback to register callbacks.
     */    
    public static class ContactsEvent {
        /** 
         * @brief MTC contacts load ok callback.
         */
        public void mtcContactCbLoadOk() {
        }
        
        /** 
         * @brief MTC contacts load failed callback.
         * 
         * @param iStatCode The failed state code.
         */
        public void mtcContactCbLoadFailed(int iStatCode) {
        }

        /** 
         * @brief MTC contacts loading callback.
         * 
         * @param iCurSize The current size.
         * @param iTotalSize The total size.
         */
        public void mtcContactCbLoading(int iCurSize, int iTotalSize) {
        }

        /** 
         * @brief MTC contacts synchronize ok callback.
         */
        public void mtcContactCbSyncOk() {
        }

        /** 
         * @brief MTC contacts synchronize failed callback.
         * 
         * @param iStatCode The failed state code.
         */
        public void mtcContactCbSyncFailed(int iStatCode) {
        }

        /** 
         * @brief MTC contacts synchronizing callback.
         * 
         * @param iCurSize The current size.
         * @param iTotalSize The total size.
         */
        public void mtcContactCbSyncing(int iCurSize, int iTotalSize) {
        }

        /** 
         * @brief MTC contacts synchronize stop ok callback.
         */
        public void mtcContactCbSyncStopOk() {
        }

        /** 
         * @brief MTC contacts synchronize stop failed callback.
         * 
         * @param iStatCode The failed state code.
         */
        public void mtcContactCbSyncStopFailed(int iStatCode) {
        }

        /** 
         * @brief MTC contacts apply ok callback.
         * 
         * @param dwId The ID of contact.
         * @param dwType The type of apply operation.
         */
        public void mtcContactCbContactApplyOk(int dwId, int dwType) {
        }

        /** 
         * @brief MTC contacts apply failed callback.
         * 
         * @param dwId The ID of contact.
         * @param iStatCode The failed state code.
         */
        public void mtcContactCbContactApplyFailed(int dwId, int iStatCode) {
        }
    }

    private static class ContactObservable extends Observable<ContactsEvent> {
    
        public int size() {
            return mObservers.size();
        }

        public void mtcContactCbLoadOk() {
            synchronized (mObservers) {
                for (ContactsEvent observer : mObservers) {
                    observer.mtcContactCbLoadOk();
                }
            }
        };

        public void mtcContactCbLoadFailed(int iStatCode) {
            synchronized (mObservers) {
                for (ContactsEvent observer : mObservers) {
                    observer.mtcContactCbLoadFailed(iStatCode);
                }
            }
        };

        public void mtcContactCbLoading(int iCurSize, int iTotalSize) {
            synchronized (mObservers) {
                for (ContactsEvent observer : mObservers) {
                    observer.mtcContactCbLoading(iCurSize, iTotalSize);
                }
            }
        };

        public void mtcContactCbSyncOk() {
            synchronized (mObservers) {
                for (ContactsEvent observer : mObservers) {
                    observer.mtcContactCbSyncOk();
                }
            }
        };

        public void mtcContactCbSyncFailed(int iStatCode) {
            synchronized (mObservers) {
                for (ContactsEvent observer : mObservers) {
                    observer.mtcContactCbSyncFailed(iStatCode);
                }
            }
        };

        public void mtcContactCbSyncing(int iCurSize, int iTotalSize) {
            synchronized (mObservers) {
                for (ContactsEvent observer : mObservers) {
                    observer.mtcContactCbSyncing(iCurSize, iTotalSize);
                }
            }
        };

        public void mtcContactCbSyncStopOk() {
            synchronized (mObservers) {
                for (ContactsEvent observer : mObservers) {
                    observer.mtcContactCbSyncStopOk();
                }
            }
        };

        public void mtcContactCbSyncStopFailed(int iStatCode) {
            synchronized (mObservers) {
                for (ContactsEvent observer : mObservers) {
                    observer.mtcContactCbSyncStopFailed(iStatCode);
                }
            }
        };

        public void mtcContactCbContactApplyOk(int dwId, int dwType) {
            synchronized (mObservers) {
                for (ContactsEvent observer : mObservers) {
                    observer.mtcContactCbContactApplyOk(dwId, dwType);
                }
            }
        };

        public void mtcContactCbContactApplyFailed(int dwId, int iStatCode) {
            synchronized (mObservers) {
                for (ContactsEvent observer : mObservers) {
                    observer.mtcContactCbContactApplyFailed(dwId, iStatCode);
                }
            }
        };

    }

    private static ContactObservable sContactObservable;

    /**
     * @brief MTC client provisioning callback register callbacks.
     *
     * Set the active client provisioning callback instance which to receive 
     * client provisioning callbacks.
     * Use unregisterCallback to deregister provisioning callbacks.
     *
     * @param observer The client provisioning callback instance.
     */
    public static void registerCallback(final ContactsEvent observer) {
        if (sContactObservable == null) {
            sContactObservable = new ContactObservable();
            initCallback(sContactObservable);
        }
        sContactObservable.registerObserver(observer);
    }

    /**
     * @brief MTC client provisioning callback unregister callbacks.
     *
     * @param observer The client provisioning callback instance.
     */
    public static void unregisterCallback(final ContactsEvent observer) {
        if (sContactObservable == null)
            return;
        sContactObservable.unregisterObserver(observer);
        if (sContactObservable.size() == 0) {
            sContactObservable = null;
            destroyCallback();
        }
    }
    
    /**
     * @brief MTC client provisioning callback init callbacks.
     *
     * This interface will call the native method to register client 
     * provisioning callback to MTC.
     */
    private static native void initCallback(ContactObservable contactObservable);

    /**
     * @brief MTC client provisioning callback destory callbacks.
     *
     * This interface will call the native method to deregister client 
     * provisioning callback to MTC.
     */
    private static native void destroyCallback();

    /**
     * used for some specific queryid
     * 
     * @author rex
     * 
     */
    public static final int EN_MTC_CONTACTS_QRYCB_ADDED = 0;
    public static final int EN_MTC_CONTACTS_QRYCB_REMOVED = 1;
    public static final int EN_MTC_CONTACTS_QRYCB_UPDATED = 2;
    public static final int EN_MTC_CONTACTS_QRYCB_MOVED = 3;

    public interface QueryCallback {
        void mtcContactCbQryOk(int dwQryId);

        void mtcContactCbQryFailed(int dwQryId, int iStatCode);

        void mtcContactCbDataChanged(int dwQryId);

        void mtcContactCbDone(int dwQryId, int iSectIndex, int dwObjId, int iObjPos, int iObjNewPos, int dwType);
    }

    public static native void registerQuery(int dwQryId, QueryCallback queryCallback);

    public static native void unregisterQuery(int dwQryId);

}
