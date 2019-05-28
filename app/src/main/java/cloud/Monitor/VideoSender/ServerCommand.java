package cloud.Monitor.VideoSender;

public class ServerCommand {
	public static final   int		LOGINSERVERED 		= 1;
	public static final   int		REMOTEUSERLOGIN 	= 2;
	public static final   int		REMOTEUSERQUIT 		= 3;
	public static final   int		UPDATEUSERLIST 		= 4;
	public static final   int		OPENUSERVIDEO 		= 5;
	public static final   int		CLOSEUSERVIDEO 		= 6;
	public static final   int		TEXTMESSAGES 		= 7;
	public static final	  int       UPDATEIMAGEFILELIST = 10; // Add by zzw
	public static final	  int       UPDATEDOCFILELIST  	= 11; // Add by zzw
	public static final   int		UPDATEREPORTFORMS	= 12; // Add by zzw
	public static final   int       UPDATEREPORTDATA	= 13; // Add by zzw
	public static final	  int		UPLOADIMAGEFILE		= 14; // Add by zzw
	public static final   int		DOWNLOADFILE		= 15; // Add by zzw
	
	//////////////////////////////////////////////////////////////////////////
	public static final	  int       kDownStatusStart	= 0; // Add by zzw Download Start
	public static final	  int       kDownStatusIng		= 1; // Add by zzw Downloaing, Will Return Process Format: [dPersent],[dSpeed]
	public static final	  int       kDownStatusEnd		= 2; // Add by zzw Download End
	public static final	  int       kDownStatusError	= 3; // Add by zzw Download Failed 
	//////////////////////////////////////////////////////////////////////////
	public static final	  int       kUnkownPage		= 0;
	public static final	  int       kLoginPage 		= 1;
	public static final	  int       kMainPage		= 2;
	public static final	  int       kSpeekPage		= 3;
	public static final	  int       kBroadcastPage	= 4;
	public static final	  int       kMeetingPage	= 5;
	public static final	  int       kStudyPage		= 6;
	public static final	  int       kReportPage		= 7;
	public static final	  int       kGetPicturePage	= 8;
	//////////////////////////////////////////////////////////////////////////
	public static final	  int   	kOne2One    = 1;	// 涓�涓�
	public static final	  int		kOne2More   = 2;	// 涓�澶�
	public static final	  int		kOne2Center = 3;	// 涓�涓績
	public static final	  int		kBroadcast  = 4;	// 骞挎挱
	////////////////////////////////////////////////////////////////////////////
	public static final	  int   	kTextMessage  = 1;
	public static final	  int   	kImageMessage = 2;
	public static final	  int   	kAudioMessage = 3;
	//////////////////////////////////////////////////////////////////////////
	public static final	  int   	kBroadcastMsg = 1;
	public static final	  int   	kExchangeMsg  = 2;
	public static final	  int   	kToCenterMsg  = 3;	
	public static final   int		kToMoreMsg	  = 4;
}
