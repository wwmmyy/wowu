//package com.wuwo.im.chat;
//
//import com.wuwo.im.config.WowuApp;
//
//import org.jivesoftware.smack.Connection;
//import org.jivesoftware.smack.ConnectionConfiguration;
//import org.jivesoftware.smack.XMPPConnection;
//import org.jivesoftware.smack.XMPPException;
//
//public class XmppTool {
//
//    private static XMPPConnection con = null;
//
//    private static Connection openConnection() {
//        try {
//            ConnectionConfiguration config = new ConnectionConfiguration(WowuApp.XMPPserverIP,
//                    WowuApp.XMPPserverPort);
//            //	              ConnectionConfiguration config = new ConnectionConfiguration("192.168.1.239",8888);//239服务器
//
//            //	              允许自动连接
//            config.setReconnectionAllowed(true);
//            config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
//            //	              config.setSendPresence(true);
//            config.setSendPresence(false); // 为获取离线消息进行的设置
//            /** 是否启用安全验证 */
//            config.setSASLAuthenticationEnabled(false);
//            config.setRosterLoadedAtLogin(false);
//            //	              config.setCompressionEnabled(true);
//            //	              config.setSASLAuthenticationEnabled(true);
//            con = new XMPPConnection(config);
//
//        } catch (Exception xe) {
//            xe.printStackTrace();
//            return null;
//        }
//
//        try {
//            con.connect();
//        } catch (XMPPException e) {
//            // TODO 自动生成的 catch 块
//            e.printStackTrace();
//            return null;
//        }
//        return con;
//    }
//
//    public static XMPPConnection getConnection() {
//        if (con == null) {
//            openConnection();
//        }
//        return con;
//    }
//
//    public static void closeConnection() {
//        con.disconnect();
//        con = null;
//    }
//}
