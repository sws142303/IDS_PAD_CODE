package com.juphoon.lemon.callback;

import android.database.Observable;

public class MtcLogCb {
    
    public interface QueryCallback {
        void Mtc_LogCbQryOk(int dwQryId);

        void Mtc_LogCbQryFailed(int dwQryId, int iStatCode);
        
        void Mtc_LogCbContinueOk(int dwQryId);
        
        void Mtc_LogCbContinueFailed(int dwQryId, int iStatCode);

        void Mtc_LogCbErased(int dwQryId);
        
        void Mtc_LogCbDone(int dwQryId, int dwObjId, int iObjPos, int iObjNewPos, int dwType);
    }

    public static native void registerQuery(int dwQryId, QueryCallback callback);

    public static native void unregisterQuery(int dwQryId);

    public interface LogsCallback {
        public void Mtc_LogCbLoadOk();

        public void Mtc_LogCbLoadFailed(int iStatCode);

        public void Mtc_LogCbLoading(int iCurSize, int iTotalSize);
        
        public void Mtc_LogCbAllErased();
    }

    public static void registerCallback(LogsCallback callback) {
        if (sLogObservable == null) {
            sLogObservable = new LogObservable();
            initCallback(sLogObservable);
        }
        sLogObservable.registerObserver(callback);
    }

    public static void unregisterCallback(LogsCallback callback) {
        sLogObservable.unregisterObserver(callback);
        if (sLogObservable.size() == 0) {
            sLogObservable = null;
            destroyCallback();
        }
    }

    private static native void initCallback(LogObservable logObservable);

    private static native void destroyCallback();

    private static LogObservable sLogObservable;
    
    @SuppressWarnings("unused")
    private static class LogObservable extends Observable<LogsCallback> {

        public int size() {
            return mObservers.size();
        }

        public void Mtc_LogCbLoadOk() {
            synchronized (mObservers) {
                for (LogsCallback callback : mObservers) {
                    callback.Mtc_LogCbLoadOk();
                }
            }
        };

        public void Mtc_LogCbLoadFailed(int iStatCode) {
            synchronized (mObservers) {
                for (LogsCallback callback : mObservers) {
                    callback.Mtc_LogCbLoadFailed(iStatCode);
                }
            }
        };

        public void Mtc_LogCbLoading(int iCurSize, int iTotalSize) {
            synchronized (mObservers) {
                for (LogsCallback callback : mObservers) {
                    callback.Mtc_LogCbLoading(iCurSize, iTotalSize);
                }
            }
        };
        
        public void Mtc_LogCbAllErased() {
            synchronized (mObservers) {
                for (LogsCallback callback : mObservers) {
                    callback.Mtc_LogCbAllErased();
                }
            }
        };
    }
}
