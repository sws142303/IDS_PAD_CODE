package com.juphoon.lemon.ui;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.content.Intent;
import android.util.SparseArray;

import com.juphoon.lemon.MtcCall;
import com.juphoon.lemon.callback.MtcCallCb;

public class MtcCallDelegate {

    public interface Callback {
        
        public void mtcCallDelegateCall(Object contact, String number, boolean isVideo);
        public void mtcCallDelegateIncoming(int dwSessId);
        //public void mtcCallDelegateIncoming(int dwSessId,String number,boolean isVideo);
        public void mtcCallDelegateOutgoing(int dwSessId);
        //public void mtcCallDelegateOutgoing(int dwSessId,String name);
        public void mtcCallDelegateAlerted(int dwSessId, int dwAlertType);
        //public void mtcCallDelegateAlerted(int dwSessId, int dwAlertType,String name);
        public void mtcCallDelegateTalking(int dwSessId);
        //public void mtcCallDelegateTalking(int dwSessId,String name);
        public void mtcCallDelegateTermed(int dwSessId, int dwStatCode);
        //public void mtcCallDelegateTermed(int dwSessId, int dwStatCode,String number);
        
        public void mtcCallDelegateStartPreview();
        //public void mtcCallDelegateStartPreview(String userName);
        public void mtcCallDelegateStartVideo(int dwSessId);
        //public void mtcCallDelegateStartVideo(int dwSessId,String name);
        public void mtcCallDelegateStopVideo(int dwSessId);
        //public void mtcCallDelegateStopVideo(int dwSessId,String name);
        public void mtcCallDelegateCaptureSize(int dwSessId, int dwWidth, int dwHeight);
        public void mtcCallDelegateVideoSize(int dwSessId, int dwWidth, int dwHeight, int iOrientation);
        
        public void mtcCallDelegateNetStaChanged(int dwSessId, boolean bVideo, boolean bSend, int iType, int iReason);
        public void mtcCallDelegateMdfyed(int dwSessId);
        public void mtcCallCbHoldOk(int dwSessId);
        public void mtcCallCbHoldFailed(int dwSessId); 
        public void mtcCallCbUnHoldOk(int dwSessId);
        public void mtcCallCbUnHoldFailed(int dwSessId);
        public void mtccallcbheld(int dwSessId);
        public void mtcCallCbUnHeld(int dwSessId);
        //SDK 申请话权
        public void mtcPttReqState(int state, String reason);
    }
    
    public static void init(Context context) {
        sContext = context;
        MtcCallCb.setCallback(new MtcCallCb.Callback() {

            @Override
            public void mtcCallCbIncoming(int dwSessId) {
                Intent intent = new Intent(sContext, sCallActivityClass);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | 
                                Intent.FLAG_ACTIVITY_CLEAR_TOP | 
                                Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra(VIDEO, MtcCall.Mtc_SessPeerOfferVideo(dwSessId));
                intent.putExtra(SESS_ID, dwSessId);
                sContext.startActivity(intent);
            }

            @Override
            public void mtcCallCbOutgoing(int dwSessId) {
                Callback callback = getCallback();
                if (callback != null) {
                    callback.mtcCallDelegateOutgoing(dwSessId);
                }
            }

            @Override
            public void mtcCallCbAlerted(int dwSessId, int dwAlertType) {
                Callback callback = getCallback();
                if (callback != null) {
                    callback.mtcCallDelegateAlerted(dwSessId, dwAlertType);
                }
            }

            @Override
            public void mtcCallCbPracked(int dwSessId) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void mtcCallCbTalking(int dwSessId) {
                Callback callback = getCallback();
                if (callback != null) {
                    callback.mtcCallDelegateTalking(dwSessId);
                }
            }

            
            
            @Override
            public void mtcCallCbTermed(int dwSessId, int dwStatCode) {
                Callback callback = getCallback();
                if (callback != null) {
                    callback.mtcCallDelegateTermed(dwSessId, dwStatCode);
                } else {
                    Intent intent = new Intent(sContext, sCallActivityClass);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | 
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP | 
                                    Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra(TERMED, true);
                    intent.putExtra(SESS_ID, dwSessId);
                    intent.putExtra(STAT_CODE, dwStatCode);
                    sContext.startActivity(intent);
                }
            }

            @Override
            public void mtcCallCbMdfyAcpt(int dwSessId) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void mtcCallCbMdfyed(int dwSessId) {
                // TODO Auto-generated method stub
                Callback callback = getCallback();
                if (callback != null) {
                    callback.mtcCallDelegateMdfyed(dwSessId);
                }
                
            }

            @Override
            public void mtcCallCbMdfyReq(int dwSessId) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void mtcCallCbHoldOk(int dwSessId) {
            	Callback callback=getCallback();
            	if(callback!=null){
            		callback.mtcCallCbHoldOk(dwSessId);
            	}
                
            }

            @Override
            public void mtcCallCbHoldFailed(int dwSessId) {
                // TODO Auto-generated method stub
            	Callback callback=getCallback();
            	if(callback!=null){
            		callback.mtcCallCbHoldFailed(dwSessId);
            	}
            }

            @Override
            public void mtcCallCbUnHoldOk(int dwSessId) {
                // TODO Auto-generated method stub
            	Callback callback=getCallback();
            	if(callback!=null){
            		callback.mtcCallCbUnHoldOk(dwSessId);
            	}
            }

            @Override
            public void mtcCallCbUnHoldFailed(int dwSessId) {
                // TODO Auto-generated method stub
            	Callback callback=getCallback();
            	if(callback!=null){
            		callback.mtcCallCbUnHoldFailed(dwSessId);
            	}
            }

            @Override
            public void mtcCallCbHeld(int dwSessId) {
            	Callback callback=getCallback();
            	if(callback!=null){
            		callback.mtccallcbheld(dwSessId);
            	}
            }

            @Override
            public void mtcCallCbUnHeld(int dwSessId) {
            	Callback callback=getCallback();
            	if(callback!=null){
            		callback.mtcCallCbUnHeld(dwSessId);
            	}
            }

            @Override
            public void mtcCallCbRefered(int dwSessId) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void mtcCallCbTrsfAcpt(int dwSessId) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void mtcCallCbTrsfTerm(int dwSessId) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void mtcCallCbTrsfFailed(int dwSessId) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void mtcCallCbRedirect(int dwSessId) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void mtcCallCbInfo(int dwSessId, String info) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void mtcCallCbCamDisconned(int dwSessId) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void mtcCallCbVideoSize(int dwSessId, int dwWidth,
                    int dwHeight, int iOrientation) {
                Callback callback = getCallback();
                if (callback != null) {
                    callback.mtcCallDelegateVideoSize(dwSessId, dwWidth, dwHeight, iOrientation);
                }
            }

            @Override
            public void mtcCallCbNetStaChanged(int dwSessId, boolean bVideo,
                    boolean bSend, int iType, int iReason) {
                Callback callback = getCallback();
                if (callback != null) {
                    callback.mtcCallDelegateNetStaChanged(dwSessId, bVideo, bSend, iType, iReason);
                }
            }

            @Override
            public void mtcCallCbVideoIncomingSta(int dwSessId, int dwParm1,
                    int dwParm2) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void mtcCallCbVideoOutgoingSta(int dwSessId, int dwParm1,
                    int dwParm2) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void mtcCallCbVideoProtectSta(int dwSessId, int dwParm1,
                    int dwParm2) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void mtcCallCbCaptureFramerate(int dwSessId, int dwParm) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void MtcCallCbCaptureSize(int dwSessId, int dwWidth,
                    int dwHeight) {
                Callback callback = getCallback();
                if (callback != null) {
                    callback.mtcCallDelegateCaptureSize(dwSessId, dwWidth, dwHeight);
                }
            }

            @Override
            public void mtcCallCbSetError(int dwSessId, int dwStatCode) {
                // TODO Auto-generated method stub
                
            }

			@Override
			public void mtcCallCbReplaced(int dwSessId) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mtcCallCbReplaceOk(int dwSessId) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mtcCallCbReplaceFailed(int dwSessId) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mtcCallCbSetRtpConnectivity(int dwSessId,
					boolean bConnected) {
				// TODO Auto-generated method stub
				
			}

        });
    }
    
    public static void setCallback(Callback callback) {
        sCallback = (callback == null) ? null : new WeakReference<Callback>(callback);
    }
    
    public static Callback getCallback() {
        return (sCallback == null) ? null : sCallback.get();
    }
    
    public static void setCallActivityClass(Class<?> cls) {
        sCallActivityClass = cls;
    }
    
    public static void call(Object contact, String number, boolean isVideo) {
        Callback callback = (sCallback == null) ? null : sCallback.get();
        if (callback != null) {
            callback.mtcCallDelegateCall(contact, number, isVideo);
        } else {
            Intent intent = new Intent(sContext, sCallActivityClass);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(VIDEO, isVideo);
            intent.putExtra(NUMBER, number);
            if (contact != null) {
                int key = putContact(contact);
                intent.putExtra(CONTACT, key);
            }
            sContext.startActivity(intent);
        }
    }
    
    public static int putContact(Object contact) {
        if (mContactArray == null) {
            mContactArray = new SparseArray<Object>();
            mContactKey = 1;
        } else {
            ++mContactKey;
        }
        mContactArray.append(mContactKey, contact);
        return mContactKey;
    }

    public static Object retrieveContact(int key) {
        Object contact = mContactArray.get(key);
        mContactArray.delete(key);
        return contact;
    }
    
    private static SparseArray<Object> mContactArray;
    private static int mContactKey;
    
    private static Context sContext;
    private static WeakReference<Callback> sCallback;
    private static Class<?> sCallActivityClass;
    
    public static final String SESS_ID = "sess_id";
    public static final String VIDEO = "video";
    public static final String NUMBER = "number";
    public static final String CONTACT = "contact";
    public static final String TERMED = "termed";
    public static final String STAT_CODE = "stat_code";
}
