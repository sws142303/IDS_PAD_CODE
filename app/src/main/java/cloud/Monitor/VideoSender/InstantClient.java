package cloud.Monitor.VideoSender;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
 
/**
 * 
* @ClassName: InstantClient 
* @Description: TODO(闁哄鏅滈悷鈺呭闯閻戣姤鍋ㄩ柕濞垮�椤忛亶鏌涘▎宥呭姤闁伙缚绮欓獮鎾圭疀濮橆剛澧柡澶嗘櫆閻熴倝鏌屽鍛暗闁煎鍊栭悾鍗灻归敐鍡欑煂闁轰緤鎷�
* @author zzw 
* @date 2013-9-2 婵炴垶鎸搁鍛暦閿燂拷14:36 
*
 */
public class InstantClient extends VideoSender  implements  IVideoSenderCallback {

	static private InstantClient mInstantClient = null;

	@SuppressWarnings("unused")
	private Context		 mContext = null;
	static long 		     nCastCount = 0;
	
	private   ArrayList<HashMap<String, String> >  mDevliceList = new  ArrayList< HashMap<String, String> >();
	
	private   ArrayList<IVideoSenderCallback>  mCallBackList = new  ArrayList<IVideoSenderCallback>() ; 
	private   MainHandler mMainHandler = new MainHandler() ;
		
	static public InstantClient instance(Context ctx) {
		if (null == mInstantClient) {
			mInstantClient = new InstantClient(ctx);
		}
		return mInstantClient;
	}
	
	static public InstantClient instance() {
		return mInstantClient;
	}
	
	public InstantClient(Context ctx){
		mContext = ctx;
	}
	public void  create(){
		mInstantClient.initialize();
	}
	
	public   ArrayList<HashMap<String, String> >  getDeviceList() {
		return mDevliceList;
	}
	
	public void  terminate(){
		finality();
		mContext = null;
	}
	
	private int sendMessage(Message msg) {
		mMainHandler.sendMessage(msg);
		return 0;
	}
	private class MainHandler extends Handler {
		public MainHandler() {
			 
		}
		@Override
		public void handleMessage(Message msg) {	
			onHandleMessage(msg);
		}
	}
	
	public int ConnectSever(String serverIp) {
	    return this.ConnectSever(this,serverIp);
	}
	
	public void DisConnect() {
	    this.DisConnectSever();
	}
	
	protected  void onHandleMessage(Message msg) {
		if (msg.what == 1) {
			doSessionConnectStatus(msg.arg1);
		}
		if (msg.what == 2) {
			doDispatchCmd(msg.arg1, msg.arg2, (String)msg.obj);
		}
		if(msg.what == 3){
			doGetTextInfo(msg.arg1, (String)msg.obj);
		}
		if(msg.what == 4){
			doDecoderFrame( msg.arg1, msg.arg2);
		}
	}
	
	public void doSessionConnectStatus(int status) {
		 for (int i=0; i< mCallBackList.size(); i++) {
			 mCallBackList.get(i).OnSessionConnectStatus(status);
		} 
	}
	
	public void doDecoderFrame(int w, int h){
	}
	
	private void doDispatchCmd(int sCmd, int nSourceId, String SourceContent) {
		 
		 for (int i=0; i<mCallBackList.size(); i++) {
			 mCallBackList.get(i).OnDispatchCmd(sCmd,  nSourceId,  SourceContent);
		 } 
	}

	private void doGetTextInfo(int sourceId, String msg){
  
	}
	
	public  HashMap<String, String> findUserInfo(int userId) {  
		for(int i=0; i<mDevliceList.size(); i++) {
			HashMap<String, String>  o = mDevliceList.get(i);
			int  id = Integer.valueOf( o.get("userId") );
			if ( id == userId) 
				return o;
		}
		return null;
	}
	
	public  boolean addCallback(IVideoSenderCallback call) {

		for (int i=0; i<mCallBackList.size(); i++) {
			if (mCallBackList.get(i) == call)
				return false;
		}
		mCallBackList.add(call);
		return true;
	}
	
	public  boolean removeCallback(IVideoSenderCallback call) {
		for (int i=0; i<mCallBackList.size(); i++) {
			if (mCallBackList.get(i) == call){
				mCallBackList.remove(i);
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public void OnSessionConnectStatus(int status) {
		
		Log.d("mmLib", "[InstantClient]  OnSessionConnectStatus " + status);
		Message msg = new Message();
		msg.what = 1;
		msg.arg1 = status;
		sendMessage(msg);
	}
	
	@Override
	public void OnDispatchCmd(int sCmd, int nSourceType, String SourceID) {
		
		Log.d("mmLib", "[InstantClient]  OnDispatchCmd " + sCmd);
		
		Message msg = new Message();
		msg.what = 2;
		msg.arg1 = sCmd;
		msg.arg2 = nSourceType;
		msg.obj = SourceID;
	 
		 sendMessage(msg);
	}
	
	public void onDecoderFrame(int w, int h){
		
		Message msg = new Message();
		msg.what = 4;
		msg.arg1 = w;
		msg.arg2 = h;
		Log.d("mmLib", "[InstantClient]  OnGetTextInfo");
		sendMessage(msg);
	}
	
	/**
	 *  
	 */
	public final static String kBRGetMessage = "BRGetMessage";
	public final static String kUserID	      = "UserID";
	public final static String kMsg	          = "Msg";
	public final static String kType	      = "Type";
	public final static String kWay	          = "Way";
	public final static String kTime	      = "Time";
	public final static String kPath          = "Path";

	/**
	 * 
	* @Title: setCastCount 
	* @Description: TODO(闁哄鏅滈悷鈺呭闯閻戣姤鍋ㄩ柕濞垮�椤忛亶鏌涘▎宥呭姤闁伙缚绮欓獮鎾圭疀濮橆剛澧柡澶嗘櫆閻熴倝鏌屽鍫濇閻熸瑥瀚妴濠囨煟閵娿儱顏х紓宥嗘閹粙鏁撻敓锟�	* @param @param nCount    闁荤姳鐒﹂崕鎶芥偩妤ｅ啫妫橀柛銉檮椤愶拷
	* @return void    闁哄鏅滈弻銊ッ洪弽顐ゅ暗閻犲洩灏欓敓锟�
	* @throws
	 */
	public void setCastCount(long nCount)
	{
		nCastCount = nCount;
	}
	
	public long getCastCount()
	{
		return nCastCount;
	}

	@Override
	public void OnUpLoadFile(String strFileName, int nStatus, double nStatusReport) {
		// TODO Auto-generated method stub
		for (int i=0; i<mCallBackList.size(); i++) {
			 mCallBackList.get(i).OnUpLoadFile(strFileName, nStatus, nStatusReport);
		 } 
	}

	@Override
	public void OnDownLoadFile(String strFileName, int nStatus,	double nStatusReport) {
		// TODO Auto-generated method stub
		for (int i=0; i<mCallBackList.size(); i++) {
			 mCallBackList.get(i).OnDownLoadFile(strFileName, nStatus, nStatusReport);
		 } 
	}
}
